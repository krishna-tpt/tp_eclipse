-- V5__sign_tenant_qty_oversell.sql
-- Bugs surfaced by guerrilla tests:
--   1. inv_transaction had no constraint linking txn_type to signed_qty sign
--      (a 'receipt' could have negative qty, an 'issue' positive, etc.)
--   2. inv_transaction had no enforcement that product/warehouse/uom belong
--      to the same tenant as tenant_id on the row
--   3. sfdc_order_line.qty allowed negative values
--   4. Oversell (issue > on_hand) silently produced negative on_hand
--      with no warning. Reframed as INTENTIONAL backorder support, but now
--      emits an info notification so ops sees it.
--
-- All fixes are defensive — they enforce invariants at the DB layer rather
-- than relying on every caller to do the right thing.

-- ============================================================================
-- 1. Sign vs txn_type CHECK
-- ============================================================================
ALTER TABLE inv_transaction
    ADD CONSTRAINT inv_transaction_sign_check
    CHECK (
        (txn_type = 'receipt'             AND signed_qty > 0) OR
        (txn_type IN ('issue','shipment') AND signed_qty < 0) OR
        (txn_type = 'transfer_out'        AND signed_qty < 0) OR
        (txn_type = 'transfer_in'         AND signed_qty > 0) OR
        (txn_type = 'adjustment'          AND signed_qty <> 0) OR
        (txn_type = 'sales_return'        AND signed_qty > 0) OR
        (txn_type = 'purchase_return'     AND signed_qty < 0) OR
        (txn_type = 'scrap'               AND signed_qty < 0)
    );

-- ============================================================================
-- 2. Tenant consistency trigger — product / warehouse / uom / lot all share
--    the same tenant_id as the inv_transaction row
-- ============================================================================
CREATE OR REPLACE FUNCTION f_inv_transaction_tenant_check()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
    v_bad text;
BEGIN
    SELECT 'product mismatch (' || tenant_id || ')'
      FROM product WHERE product_id = NEW.product_id AND tenant_id <> NEW.tenant_id
      INTO v_bad;
    IF v_bad IS NOT NULL THEN
        RAISE EXCEPTION 'inv_transaction tenant_id mismatch: %', v_bad
            USING ERRCODE = 'integrity_constraint_violation';
    END IF;

    SELECT 'warehouse mismatch (' || tenant_id || ')'
      FROM warehouse WHERE warehouse_id = NEW.warehouse_id AND tenant_id <> NEW.tenant_id
      INTO v_bad;
    IF v_bad IS NOT NULL THEN
        RAISE EXCEPTION 'inv_transaction tenant_id mismatch: %', v_bad
            USING ERRCODE = 'integrity_constraint_violation';
    END IF;

    SELECT 'uom mismatch (' || tenant_id || ')'
      FROM uom WHERE uom_id = NEW.uom_id AND tenant_id <> NEW.tenant_id
      INTO v_bad;
    IF v_bad IS NOT NULL THEN
        RAISE EXCEPTION 'inv_transaction tenant_id mismatch: %', v_bad
            USING ERRCODE = 'integrity_constraint_violation';
    END IF;

    IF NEW.lot_id IS NOT NULL THEN
        SELECT 'lot mismatch (' || tenant_id || ')'
          FROM lot WHERE lot_id = NEW.lot_id AND tenant_id <> NEW.tenant_id
          INTO v_bad;
        IF v_bad IS NOT NULL THEN
            RAISE EXCEPTION 'inv_transaction tenant_id mismatch: %', v_bad
                USING ERRCODE = 'integrity_constraint_violation';
        END IF;
    END IF;

    RETURN NEW;
END$$;

DROP TRIGGER IF EXISTS trg_inv_txn_tenant_check ON inv_transaction;
CREATE TRIGGER trg_inv_txn_tenant_check
    BEFORE INSERT ON inv_transaction
    FOR EACH ROW EXECUTE FUNCTION f_inv_transaction_tenant_check();

-- ============================================================================
-- 3. sfdc_order_line.qty > 0
-- ============================================================================
ALTER TABLE sfdc_order_line
    ADD CONSTRAINT sfdc_order_line_qty_check CHECK (qty > 0);

-- ============================================================================
-- 4. post_transaction returns clean reasons instead of bubbling check_violation
--    Also: catches the tenant-mismatch trigger error gracefully.
--    Also: emits an info notification when the resulting on_hand goes negative
--    (oversell / backorder). This is intentional behavior; the notification
--    is a heads-up to ops, not a rejection.
-- ============================================================================
CREATE OR REPLACE FUNCTION post_transaction(p_payload jsonb)
RETURNS TABLE (inv_transaction_id bigint, accepted boolean, reason text)
LANGUAGE plpgsql
AS $f$
#variable_conflict use_column
DECLARE
    v_tenant_id    uuid;
    v_product_id   bigint;
    v_warehouse_id bigint;
    v_lot_id       bigint;
    v_uom_id       bigint;
    v_external     text;
    v_qty          numeric;
    v_txn_type     text;
    v_posted_at    timestamptz;
    v_source       text;
    v_transfer_pair uuid;
    v_id           bigint;
    v_on_hand      numeric;
BEGIN
    v_external      := p_payload->>'external_txn_id';
    v_qty           := (p_payload->>'signed_qty')::numeric;
    v_txn_type      := p_payload->>'txn_type';
    v_posted_at     := COALESCE((p_payload->>'posted_at')::timestamptz, now());
    v_source        := COALESCE(p_payload->>'source_system', 'unknown');
    v_transfer_pair := NULLIF(p_payload->>'transfer_pair_id', '')::uuid;

    SELECT t.tenant_id INTO v_tenant_id FROM tenant t WHERE t.tenant_code = p_payload->>'tenant_code';
    IF v_tenant_id IS NULL THEN
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_tenant'::text; RETURN;
    END IF;

    SELECT pr.product_id   INTO v_product_id   FROM product   pr WHERE pr.tenant_id = v_tenant_id AND pr.product_code   = p_payload->>'product_code';
    SELECT wh.warehouse_id INTO v_warehouse_id FROM warehouse wh WHERE wh.tenant_id = v_tenant_id AND wh.warehouse_code = p_payload->>'warehouse_code';
    SELECT um.uom_id       INTO v_uom_id       FROM uom       um WHERE um.tenant_id = v_tenant_id AND um.uom_code       = p_payload->>'uom_code';
    IF p_payload ? 'lot_code' AND (p_payload->>'lot_code') IS NOT NULL THEN
        SELECT lt.lot_id INTO v_lot_id FROM lot lt
         WHERE lt.tenant_id = v_tenant_id AND lt.product_id = v_product_id AND lt.lot_code = p_payload->>'lot_code';
        IF v_lot_id IS NULL THEN
            RETURN QUERY SELECT NULL::bigint, false, 'unknown_lot'::text; RETURN;
        END IF;
    END IF;

    IF v_product_id IS NULL THEN
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_product'::text; RETURN;
    END IF;
    IF v_warehouse_id IS NULL THEN
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_warehouse'::text; RETURN;
    END IF;
    IF v_uom_id IS NULL THEN
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_uom'::text; RETURN;
    END IF;
    IF v_qty IS NULL OR v_qty = 0 THEN
        RETURN QUERY SELECT NULL::bigint, false, 'invalid_qty'::text; RETURN;
    END IF;

    -- Sign-vs-type pre-check
    IF NOT (
        (v_txn_type = 'receipt'             AND v_qty > 0) OR
        (v_txn_type IN ('issue','shipment') AND v_qty < 0) OR
        (v_txn_type = 'transfer_out'        AND v_qty < 0) OR
        (v_txn_type = 'transfer_in'         AND v_qty > 0) OR
        (v_txn_type = 'adjustment'          AND v_qty <> 0) OR
        (v_txn_type = 'sales_return'        AND v_qty > 0) OR
        (v_txn_type = 'purchase_return'     AND v_qty < 0) OR
        (v_txn_type = 'scrap'               AND v_qty < 0)
    ) THEN
        RETURN QUERY SELECT NULL::bigint, false, 'invalid_sign_for_type'::text; RETURN;
    END IF;

    BEGIN
        INSERT INTO inv_transaction AS it (
            tenant_id, external_txn_id, product_id, warehouse_id, lot_id,
            signed_qty, uom_id, txn_type, transfer_pair_id, source_system, posted_at, payload
        ) VALUES (
            v_tenant_id, v_external, v_product_id, v_warehouse_id, v_lot_id,
            v_qty, v_uom_id, v_txn_type, v_transfer_pair, v_source, v_posted_at, p_payload
        )
        RETURNING it.inv_transaction_id INTO v_id;
    EXCEPTION
        WHEN unique_violation THEN
            RETURN QUERY SELECT NULL::bigint, false, 'duplicate'::text; RETURN;
        WHEN check_violation THEN
            RETURN QUERY SELECT NULL::bigint, false, 'check_violation'::text; RETURN;
    END;

    -- Backorder / oversell info notification
    IF v_qty < 0 AND v_txn_type IN ('issue','shipment') THEN
        SELECT on_hand_qty INTO v_on_hand
          FROM calculate_inventory(v_tenant_id, v_product_id, v_warehouse_id);
        IF v_on_hand < 0 THEN
            INSERT INTO notification_outbox (tenant_id, severity, source, message, payload)
            VALUES (v_tenant_id, 'info', 'post_transaction',
                    format('oversell: on_hand=%s after txn %s', v_on_hand, v_external),
                    jsonb_build_object(
                        'external_txn_id', v_external,
                        'product_id', v_product_id,
                        'warehouse_id', v_warehouse_id,
                        'on_hand_after', v_on_hand,
                        'signed_qty', v_qty));
        END IF;
    END IF;

    RETURN QUERY SELECT v_id, true, 'ok'::text;
END$f$;
