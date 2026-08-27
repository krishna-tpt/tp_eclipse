-- V13__flatten_schema_functions.sql
--
-- Schema flattening:
--   1. Core fact tables carry natural-key codes alongside surrogate IDs,
--      making them self-describing without joins.
--   2. Staging tables use tenant_code (text) instead of tenant_id (uuid).
--   3. All write functions updated to populate code columns.
--   4. Masters (product, warehouse, uom, lot) are auto-created on first
--      encounter — no pre-setup requirement. Tenant still requires explicit
--      provisioning.
--
-- Applies cleanly to an existing database with V1–V12 already applied.

-- ============================================================================
-- Part 1: DDL — add code columns to core tables
-- ============================================================================

-- inv_transaction (partitioned — columns propagate to all partitions)
ALTER TABLE inv_transaction ADD COLUMN IF NOT EXISTS tenant_code    text;
ALTER TABLE inv_transaction ADD COLUMN IF NOT EXISTS product_code   text;
ALTER TABLE inv_transaction ADD COLUMN IF NOT EXISTS warehouse_code text;
ALTER TABLE inv_transaction ADD COLUMN IF NOT EXISTS lot_code       text;
ALTER TABLE inv_transaction ADD COLUMN IF NOT EXISTS uom_code       text;

-- stock_balance
ALTER TABLE stock_balance ADD COLUMN IF NOT EXISTS tenant_code    text;
ALTER TABLE stock_balance ADD COLUMN IF NOT EXISTS product_code   text;
ALTER TABLE stock_balance ADD COLUMN IF NOT EXISTS warehouse_code text;
ALTER TABLE stock_balance ADD COLUMN IF NOT EXISTS lot_code       text;
ALTER TABLE stock_balance ADD COLUMN IF NOT EXISTS uom_code       text;

-- opening_balance
ALTER TABLE opening_balance ADD COLUMN IF NOT EXISTS tenant_code    text;
ALTER TABLE opening_balance ADD COLUMN IF NOT EXISTS product_code   text;
ALTER TABLE opening_balance ADD COLUMN IF NOT EXISTS warehouse_code text;
ALTER TABLE opening_balance ADD COLUMN IF NOT EXISTS lot_code       text;
ALTER TABLE opening_balance ADD COLUMN IF NOT EXISTS uom_code       text;

-- sfdc_order
ALTER TABLE sfdc_order ADD COLUMN IF NOT EXISTS tenant_code text;

-- sfdc_order_line
ALTER TABLE sfdc_order_line ADD COLUMN IF NOT EXISTS tenant_code    text;
ALTER TABLE sfdc_order_line ADD COLUMN IF NOT EXISTS product_code   text;
ALTER TABLE sfdc_order_line ADD COLUMN IF NOT EXISTS warehouse_code text;
ALTER TABLE sfdc_order_line ADD COLUMN IF NOT EXISTS uom_code       text;

-- ============================================================================
-- Part 2: Backfill code columns from master tables
-- ============================================================================

UPDATE inv_transaction
   SET tenant_code    = t.tenant_code,
       product_code   = p.product_code,
       warehouse_code = w.warehouse_code,
       uom_code       = u.uom_code,
       lot_code       = l.lot_code
  FROM tenant t, product p, warehouse w, uom u, lot l
 WHERE t.tenant_id    = inv_transaction.tenant_id
   AND p.product_id   = inv_transaction.product_id
   AND w.warehouse_id = inv_transaction.warehouse_id
   AND u.uom_id       = inv_transaction.uom_id
   AND l.lot_id       = inv_transaction.lot_id
   AND inv_transaction.tenant_code IS NULL;

-- Second pass: rows with lot_id IS NULL (LEFT JOIN lot not possible in UPDATE FROM)
UPDATE inv_transaction
   SET tenant_code    = t.tenant_code,
       product_code   = p.product_code,
       warehouse_code = w.warehouse_code,
       uom_code       = u.uom_code
  FROM tenant t, product p, warehouse w, uom u
 WHERE t.tenant_id    = inv_transaction.tenant_id
   AND p.product_id   = inv_transaction.product_id
   AND w.warehouse_id = inv_transaction.warehouse_id
   AND u.uom_id       = inv_transaction.uom_id
   AND inv_transaction.lot_id IS NULL
   AND inv_transaction.tenant_code IS NULL;

UPDATE stock_balance
   SET tenant_code    = t.tenant_code,
       product_code   = p.product_code,
       warehouse_code = w.warehouse_code,
       uom_code       = u.uom_code,
       lot_code       = l.lot_code
  FROM tenant t, product p, warehouse w, uom u, lot l
 WHERE t.tenant_id    = stock_balance.tenant_id
   AND p.product_id   = stock_balance.product_id
   AND w.warehouse_id = stock_balance.warehouse_id
   AND u.uom_id       = stock_balance.uom_id
   AND l.lot_id       = stock_balance.lot_id
   AND stock_balance.lot_id <> 0
   AND stock_balance.tenant_code IS NULL;

UPDATE stock_balance
   SET tenant_code    = t.tenant_code,
       product_code   = p.product_code,
       warehouse_code = w.warehouse_code,
       uom_code       = u.uom_code
  FROM tenant t, product p, warehouse w, uom u
 WHERE t.tenant_id    = stock_balance.tenant_id
   AND p.product_id   = stock_balance.product_id
   AND w.warehouse_id = stock_balance.warehouse_id
   AND u.uom_id       = stock_balance.uom_id
   AND stock_balance.lot_id = 0
   AND stock_balance.tenant_code IS NULL;

UPDATE opening_balance
   SET tenant_code    = t.tenant_code,
       product_code   = p.product_code,
       warehouse_code = w.warehouse_code,
       uom_code       = u.uom_code,
       lot_code       = l.lot_code
  FROM tenant t, product p, warehouse w, uom u, lot l
 WHERE t.tenant_id    = opening_balance.tenant_id
   AND p.product_id   = opening_balance.product_id
   AND w.warehouse_id = opening_balance.warehouse_id
   AND u.uom_id       = opening_balance.uom_id
   AND l.lot_id       = opening_balance.lot_id
   AND opening_balance.tenant_code IS NULL;

UPDATE opening_balance
   SET tenant_code    = t.tenant_code,
       product_code   = p.product_code,
       warehouse_code = w.warehouse_code,
       uom_code       = u.uom_code
  FROM tenant t, product p, warehouse w, uom u
 WHERE t.tenant_id    = opening_balance.tenant_id
   AND p.product_id   = opening_balance.product_id
   AND w.warehouse_id = opening_balance.warehouse_id
   AND u.uom_id       = opening_balance.uom_id
   AND opening_balance.lot_id IS NULL
   AND opening_balance.tenant_code IS NULL;

UPDATE sfdc_order so
   SET tenant_code = t.tenant_code
  FROM tenant t
 WHERE t.tenant_id = so.tenant_id
   AND so.tenant_code IS NULL;

UPDATE sfdc_order_line
   SET tenant_code    = t.tenant_code,
       product_code   = p.product_code,
       warehouse_code = w.warehouse_code,
       uom_code       = u.uom_code
  FROM tenant t, product p, warehouse w, uom u
 WHERE t.tenant_id    = sfdc_order_line.tenant_id
   AND p.product_id   = sfdc_order_line.product_id
   AND w.warehouse_id = sfdc_order_line.warehouse_id
   AND u.uom_id       = sfdc_order_line.uom_id
   AND sfdc_order_line.tenant_code IS NULL;

-- ============================================================================
-- Part 3: Set NOT NULL after backfill
-- ============================================================================

DO $$
BEGIN
    -- inv_transaction
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'inv_transaction' AND column_name = 'tenant_code' AND is_nullable = 'YES') THEN
        ALTER TABLE inv_transaction ALTER COLUMN tenant_code    SET NOT NULL;
        ALTER TABLE inv_transaction ALTER COLUMN product_code   SET NOT NULL;
        ALTER TABLE inv_transaction ALTER COLUMN warehouse_code SET NOT NULL;
        ALTER TABLE inv_transaction ALTER COLUMN uom_code       SET NOT NULL;
    END IF;

    -- stock_balance
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'stock_balance' AND column_name = 'tenant_code' AND is_nullable = 'YES') THEN
        ALTER TABLE stock_balance ALTER COLUMN tenant_code    SET NOT NULL;
        ALTER TABLE stock_balance ALTER COLUMN product_code   SET NOT NULL;
        ALTER TABLE stock_balance ALTER COLUMN warehouse_code SET NOT NULL;
        ALTER TABLE stock_balance ALTER COLUMN uom_code       SET NOT NULL;
    END IF;

    -- opening_balance
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'opening_balance' AND column_name = 'tenant_code' AND is_nullable = 'YES') THEN
        ALTER TABLE opening_balance ALTER COLUMN tenant_code    SET NOT NULL;
        ALTER TABLE opening_balance ALTER COLUMN product_code   SET NOT NULL;
        ALTER TABLE opening_balance ALTER COLUMN warehouse_code SET NOT NULL;
        ALTER TABLE opening_balance ALTER COLUMN uom_code       SET NOT NULL;
    END IF;

    -- sfdc_order
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'sfdc_order' AND column_name = 'tenant_code' AND is_nullable = 'YES') THEN
        ALTER TABLE sfdc_order ALTER COLUMN tenant_code SET NOT NULL;
    END IF;

    -- sfdc_order_line
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'sfdc_order_line' AND column_name = 'tenant_code' AND is_nullable = 'YES') THEN
        ALTER TABLE sfdc_order_line ALTER COLUMN tenant_code    SET NOT NULL;
        ALTER TABLE sfdc_order_line ALTER COLUMN product_code   SET NOT NULL;
        ALTER TABLE sfdc_order_line ALTER COLUMN warehouse_code SET NOT NULL;
        ALTER TABLE sfdc_order_line ALTER COLUMN uom_code       SET NOT NULL;
    END IF;
END$$;

-- ============================================================================
-- Part 4: Staging — tenant_id uuid → tenant_code text
-- ============================================================================

-- txn_inbox
ALTER TABLE staging.txn_inbox ADD COLUMN IF NOT EXISTS tenant_code text;
UPDATE staging.txn_inbox ti
   SET tenant_code = t.tenant_code
  FROM tenant t WHERE t.tenant_id = ti.tenant_id
   AND ti.tenant_code IS NULL;
-- For any rows where tenant_id didn't resolve, use the UUID as fallback
UPDATE staging.txn_inbox SET tenant_code = tenant_id::text WHERE tenant_code IS NULL AND tenant_id IS NOT NULL;
ALTER TABLE staging.txn_inbox ALTER COLUMN tenant_code SET NOT NULL;
ALTER TABLE staging.txn_inbox DROP CONSTRAINT IF EXISTS txn_inbox_tenant_id_external_txn_id_key;
ALTER TABLE staging.txn_inbox ADD CONSTRAINT txn_inbox_tenant_code_external_txn_id_key
    UNIQUE (tenant_code, external_txn_id);
ALTER TABLE staging.txn_inbox DROP COLUMN IF EXISTS tenant_id;

-- order_inbox
ALTER TABLE staging.order_inbox ADD COLUMN IF NOT EXISTS tenant_code text;
UPDATE staging.order_inbox oi
   SET tenant_code = t.tenant_code
  FROM tenant t WHERE t.tenant_id = oi.tenant_id
   AND oi.tenant_code IS NULL;
UPDATE staging.order_inbox SET tenant_code = tenant_id::text WHERE tenant_code IS NULL AND tenant_id IS NOT NULL;
ALTER TABLE staging.order_inbox ALTER COLUMN tenant_code SET NOT NULL;
ALTER TABLE staging.order_inbox DROP COLUMN IF EXISTS tenant_id;

-- ob_load_batch
ALTER TABLE staging.ob_load_batch ADD COLUMN IF NOT EXISTS tenant_code text;
UPDATE staging.ob_load_batch olb
   SET tenant_code = t.tenant_code
  FROM tenant t WHERE t.tenant_id = olb.tenant_id
   AND olb.tenant_code IS NULL;
ALTER TABLE staging.ob_load_batch DROP COLUMN IF EXISTS tenant_id;

-- ============================================================================
-- Part 5: Trigger — f_stock_balance_apply (inv_transaction → stock_balance)
--   Now carries code columns from NEW into stock_balance.
-- ============================================================================
CREATE OR REPLACE FUNCTION f_stock_balance_apply()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO stock_balance (
        tenant_id, tenant_code,
        product_id, product_code,
        warehouse_id, warehouse_code,
        lot_id, lot_code,
        on_hand_qty, uom_id, uom_code, last_updated_at
    ) VALUES (
        NEW.tenant_id, NEW.tenant_code,
        NEW.product_id, NEW.product_code,
        NEW.warehouse_id, NEW.warehouse_code,
        COALESCE(NEW.lot_id, 0), NEW.lot_code,
        NEW.signed_qty, NEW.uom_id, NEW.uom_code, now()
    )
    ON CONFLICT (tenant_id, product_id, warehouse_id, lot_id) DO UPDATE
        SET on_hand_qty     = stock_balance.on_hand_qty + EXCLUDED.on_hand_qty,
            last_updated_at = now();
    RETURN NEW;
END$$;

-- ============================================================================
-- Part 6: Trigger — f_stock_balance_opening_apply (opening_balance → stock_balance)
--   Now carries code columns from NEW into stock_balance on INSERT.
-- ============================================================================
CREATE OR REPLACE FUNCTION f_stock_balance_opening_apply()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
    v_delta numeric;
BEGIN
    IF TG_OP = 'INSERT' THEN
        v_delta := NEW.qty;
        INSERT INTO stock_balance (
            tenant_id, tenant_code,
            product_id, product_code,
            warehouse_id, warehouse_code,
            lot_id, lot_code,
            on_hand_qty, uom_id, uom_code, last_updated_at
        ) VALUES (
            NEW.tenant_id, NEW.tenant_code,
            NEW.product_id, NEW.product_code,
            NEW.warehouse_id, NEW.warehouse_code,
            COALESCE(NEW.lot_id, 0), NEW.lot_code,
            v_delta, NEW.uom_id, NEW.uom_code, now()
        )
        ON CONFLICT (tenant_id, product_id, warehouse_id, lot_id) DO UPDATE
            SET on_hand_qty     = stock_balance.on_hand_qty + EXCLUDED.on_hand_qty,
                last_updated_at = now();
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        v_delta := NEW.qty - OLD.qty;
        IF v_delta <> 0 THEN
            UPDATE stock_balance
               SET on_hand_qty     = on_hand_qty + v_delta,
                   last_updated_at = now()
             WHERE tenant_id    = NEW.tenant_id
               AND product_id   = NEW.product_id
               AND warehouse_id = NEW.warehouse_id
               AND lot_id       = COALESCE(NEW.lot_id, 0);
        END IF;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE stock_balance
           SET on_hand_qty     = on_hand_qty - OLD.qty,
               last_updated_at = now()
         WHERE tenant_id    = OLD.tenant_id
           AND product_id   = OLD.product_id
           AND warehouse_id = OLD.warehouse_id
           AND lot_id       = COALESCE(OLD.lot_id, 0);
        RETURN OLD;
    END IF;
    RETURN NULL;
END$$;

-- ============================================================================
-- Part 7: post_transaction
--   - Auto-creates product/warehouse/uom/lot on first encounter
--   - Populates code columns on inv_transaction INSERT
--   - Tenant still requires explicit provisioning (no auto-create)
-- ============================================================================
CREATE OR REPLACE FUNCTION post_transaction(p_payload jsonb)
RETURNS TABLE (inv_transaction_id bigint, accepted boolean, reason text)
LANGUAGE plpgsql
AS $f$
#variable_conflict use_column
DECLARE
    v_tenant_id      uuid;
    v_tenant_code    text;
    v_product_id     bigint;
    v_product_code   text;
    v_warehouse_id   bigint;
    v_warehouse_code text;
    v_lot_id         bigint;
    v_lot_code       text;
    v_uom_id         bigint;
    v_uom_code       text;
    v_external       text;
    v_qty            numeric;
    v_txn_type       text;
    v_posted_at      timestamptz;
    v_source         text;
    v_transfer_pair  uuid;
    v_id             bigint;
    v_on_hand        numeric;
    v_size_cap       int := pipeline_config_int('payload_max_bytes');
BEGIN
    IF v_size_cap IS NOT NULL AND pg_column_size(p_payload) > v_size_cap THEN
        RETURN QUERY SELECT NULL::bigint, false, 'payload_too_large'::text;
        RETURN;
    END IF;

    v_tenant_code    := p_payload->>'tenant_code';
    v_product_code   := p_payload->>'product_code';
    v_warehouse_code := p_payload->>'warehouse_code';
    v_uom_code       := p_payload->>'uom_code';
    v_lot_code       := p_payload->>'lot_code';
    v_external       := p_payload->>'external_txn_id';
    v_qty            := (p_payload->>'signed_qty')::numeric;
    v_txn_type       := p_payload->>'txn_type';
    v_posted_at      := COALESCE((p_payload->>'posted_at')::timestamptz, now());
    v_source         := COALESCE(p_payload->>'source_system', 'unknown');
    v_transfer_pair  := NULLIF(p_payload->>'transfer_pair_id', '')::uuid;

    -- Tenant: must exist (no auto-create)
    SELECT t.tenant_id INTO v_tenant_id FROM tenant t WHERE t.tenant_code = v_tenant_code;
    IF v_tenant_id IS NULL THEN
        PERFORM notify_outbox(NULL, 'info', 'post_transaction.lookup',
            'unknown_tenant: ' || COALESCE(v_tenant_code, '<null>'),
            jsonb_build_object('hint', 'tenant_code not found', 'tenant_code', v_tenant_code),
            'unknown_tenant:' || COALESCE(v_tenant_code, '<null>'));
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_reference'::text; RETURN;
    END IF;

    -- Product: auto-create if not found
    INSERT INTO product (tenant_id, product_code, name)
    VALUES (v_tenant_id, v_product_code, v_product_code)
    ON CONFLICT (tenant_id, product_code) DO NOTHING;
    SELECT pr.product_id INTO v_product_id
      FROM product pr WHERE pr.tenant_id = v_tenant_id AND pr.product_code = v_product_code;

    -- Warehouse: auto-create if not found
    INSERT INTO warehouse (tenant_id, warehouse_code, name)
    VALUES (v_tenant_id, v_warehouse_code, v_warehouse_code)
    ON CONFLICT (tenant_id, warehouse_code) DO NOTHING;
    SELECT wh.warehouse_id INTO v_warehouse_id
      FROM warehouse wh WHERE wh.tenant_id = v_tenant_id AND wh.warehouse_code = v_warehouse_code;

    -- UOM: auto-create if not found
    INSERT INTO uom (tenant_id, uom_code, name)
    VALUES (v_tenant_id, v_uom_code, v_uom_code)
    ON CONFLICT (tenant_id, uom_code) DO NOTHING;
    SELECT um.uom_id INTO v_uom_id
      FROM uom um WHERE um.tenant_id = v_tenant_id AND um.uom_code = v_uom_code;

    -- Lot: auto-create if code provided
    IF v_lot_code IS NOT NULL THEN
        INSERT INTO lot (tenant_id, product_id, lot_code)
        VALUES (v_tenant_id, v_product_id, v_lot_code)
        ON CONFLICT (tenant_id, product_id, lot_code) DO NOTHING;
        SELECT lt.lot_id INTO v_lot_id
          FROM lot lt WHERE lt.tenant_id = v_tenant_id AND lt.product_id = v_product_id AND lt.lot_code = v_lot_code;
    END IF;

    IF v_product_id IS NULL OR v_warehouse_id IS NULL OR v_uom_id IS NULL THEN
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_reference'::text; RETURN;
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
            tenant_id, tenant_code, external_txn_id,
            product_id, product_code, warehouse_id, warehouse_code,
            lot_id, lot_code,
            signed_qty, uom_id, uom_code,
            txn_type, transfer_pair_id, source_system, posted_at, payload
        ) VALUES (
            v_tenant_id, v_tenant_code, v_external,
            v_product_id, v_product_code, v_warehouse_id, v_warehouse_code,
            v_lot_id, v_lot_code,
            v_qty, v_uom_id, v_uom_code,
            v_txn_type, v_transfer_pair, v_source, v_posted_at, p_payload
        )
        RETURNING it.inv_transaction_id INTO v_id;
    EXCEPTION
        WHEN unique_violation THEN
            RETURN QUERY SELECT NULL::bigint, false, 'duplicate'::text; RETURN;
        WHEN check_violation THEN
            RETURN QUERY SELECT NULL::bigint, false, 'check_violation'::text; RETURN;
    END;

    -- Oversell notification (dedup-aware)
    IF v_qty < 0 AND v_txn_type IN ('issue','shipment') THEN
        SELECT on_hand_qty INTO v_on_hand
          FROM calculate_inventory(v_tenant_id, v_product_id, v_warehouse_id);
        IF v_on_hand < 0 THEN
            PERFORM notify_outbox(
                v_tenant_id, 'info', 'post_transaction',
                format('oversell: on_hand=%s after txn %s', v_on_hand, v_external),
                jsonb_build_object(
                    'external_txn_id', v_external,
                    'product_code', v_product_code,
                    'warehouse_code', v_warehouse_code,
                    'on_hand_after', v_on_hand,
                    'signed_qty', v_qty),
                format('oversell:%s:%s:%s', v_tenant_id, v_product_id, v_warehouse_id));
        END IF;
    END IF;

    RETURN QUERY SELECT v_id, true, 'ok'::text;
END$f$;

-- Re-apply SECURITY DEFINER (CREATE OR REPLACE resets it)
ALTER FUNCTION post_transaction(jsonb)
    SECURITY DEFINER
    SET search_path = public, pg_catalog;

-- ============================================================================
-- Part 8: post_transaction_bulk — no logic change, just re-declare so it picks
--   up the updated post_transaction.
-- ============================================================================
CREATE OR REPLACE FUNCTION post_transaction_bulk(p_payloads jsonb[])
RETURNS TABLE (idx int, inv_transaction_id bigint, accepted boolean, reason text)
LANGUAGE plpgsql
AS $$
DECLARE
    v_cap int := pipeline_config_int('bulk_max_size');
    v_len int := COALESCE(array_length(p_payloads, 1), 0);
    i int;
    r record;
BEGIN
    IF v_cap IS NOT NULL AND v_len > v_cap THEN
        idx := 0;
        inv_transaction_id := NULL;
        accepted := false;
        reason := format('bulk_size_exceeded: %s > %s', v_len, v_cap);
        RETURN NEXT;
        RETURN;
    END IF;

    FOR i IN 1 .. v_len LOOP
        FOR r IN SELECT * FROM post_transaction(p_payloads[i]) LOOP
            idx := i;
            inv_transaction_id := r.inv_transaction_id;
            accepted := r.accepted;
            reason := r.reason;
            RETURN NEXT;
        END LOOP;
    END LOOP;
END$$;

ALTER FUNCTION post_transaction_bulk(jsonb[])
    SECURITY DEFINER
    SET search_path = public, pg_catalog;

-- ============================================================================
-- Part 9: load_opening_balance
--   - Auto-creates product/warehouse/uom/lot on first encounter
--   - Populates code columns on opening_balance INSERT
-- ============================================================================
CREATE OR REPLACE FUNCTION load_opening_balance(p_batch_id bigint)
RETURNS TABLE (accepted_count int, rejected_count int)
LANGUAGE plpgsql
AS $f$
DECLARE
    v_status   text;
    v_accepted int := 0;
    v_rejected int := 0;
    v_tenant_id uuid;
    v_row      record;
BEGIN
    SELECT status INTO v_status FROM staging.ob_load_batch WHERE batch_id = p_batch_id FOR UPDATE;
    IF v_status IS NULL THEN
        RAISE EXCEPTION 'batch % not found', p_batch_id;
    END IF;
    IF v_status = 'loaded' THEN
        SELECT ob.accepted_count, ob.rejected_count INTO v_accepted, v_rejected
          FROM staging.ob_load_batch ob WHERE ob.batch_id = p_batch_id;
        RETURN QUERY SELECT v_accepted, v_rejected;
        RETURN;
    END IF;

    -- Auto-create masters for all valid rows in this batch
    FOR v_row IN
        SELECT DISTINCT l.tenant_code, l.product_code, l.warehouse_code, l.uom_code, l.lot_code
          FROM staging.ob_load l
         WHERE l.batch_id = p_batch_id
           AND l.tenant_code IS NOT NULL
    LOOP
        SELECT t.tenant_id INTO v_tenant_id FROM tenant t WHERE t.tenant_code = v_row.tenant_code;
        IF v_tenant_id IS NULL THEN CONTINUE; END IF;

        IF v_row.product_code IS NOT NULL THEN
            INSERT INTO product (tenant_id, product_code, name)
            VALUES (v_tenant_id, v_row.product_code, v_row.product_code)
            ON CONFLICT (tenant_id, product_code) DO NOTHING;
        END IF;
        IF v_row.warehouse_code IS NOT NULL THEN
            INSERT INTO warehouse (tenant_id, warehouse_code, name)
            VALUES (v_tenant_id, v_row.warehouse_code, v_row.warehouse_code)
            ON CONFLICT (tenant_id, warehouse_code) DO NOTHING;
        END IF;
        IF v_row.uom_code IS NOT NULL THEN
            INSERT INTO uom (tenant_id, uom_code, name)
            VALUES (v_tenant_id, v_row.uom_code, v_row.uom_code)
            ON CONFLICT (tenant_id, uom_code) DO NOTHING;
        END IF;
        IF v_row.lot_code IS NOT NULL AND v_row.product_code IS NOT NULL THEN
            INSERT INTO lot (tenant_id, product_id, lot_code)
            VALUES (v_tenant_id,
                    (SELECT product_id FROM product WHERE tenant_id = v_tenant_id AND product_code = v_row.product_code),
                    v_row.lot_code)
            ON CONFLICT (tenant_id, product_id, lot_code) DO NOTHING;
        END IF;
    END LOOP;

    -- Resolve codes to IDs and insert
    WITH resolved AS (
        SELECT
            l.ob_load_id, l.line_no,
            t.tenant_id, t.tenant_code,
            p.product_id, l.product_code,
            w.warehouse_id, l.warehouse_code,
            lt.lot_id, l.lot_code,
            u.uom_id, l.uom_code,
            l.qty AS qty_text, l.as_of_date AS as_of_text,
            CASE
              WHEN t.tenant_id    IS NULL THEN 'unknown_tenant'
              WHEN p.product_id   IS NULL THEN 'unknown_product'
              WHEN w.warehouse_id IS NULL THEN 'unknown_warehouse'
              WHEN u.uom_id       IS NULL THEN 'unknown_uom'
              WHEN l.lot_code IS NOT NULL AND lt.lot_id IS NULL THEN 'unknown_lot'
              WHEN l.qty IS NULL OR l.qty !~ '^-?[0-9]+(\.[0-9]+)?$' THEN 'invalid_qty'
              WHEN l.as_of_date IS NULL THEN 'invalid_date'
              ELSE NULL
            END AS reason_code
        FROM staging.ob_load l
        LEFT JOIN tenant    t  ON t.tenant_code    = l.tenant_code
        LEFT JOIN product   p  ON p.product_code   = l.product_code  AND p.tenant_id = t.tenant_id
        LEFT JOIN warehouse w  ON w.warehouse_code = l.warehouse_code AND w.tenant_id = t.tenant_id
        LEFT JOIN uom       u  ON u.uom_code       = l.uom_code      AND u.tenant_id = t.tenant_id
        LEFT JOIN lot       lt ON lt.lot_code      = l.lot_code       AND lt.tenant_id = t.tenant_id AND lt.product_id = p.product_id
        WHERE l.batch_id = p_batch_id
    ),
    inserted AS (
        INSERT INTO opening_balance (
            tenant_id, tenant_code,
            product_id, product_code,
            warehouse_id, warehouse_code,
            lot_id, lot_code,
            qty, uom_id, uom_code, as_of_date, batch_id
        )
        SELECT
            tenant_id, tenant_code,
            product_id, product_code,
            warehouse_id, warehouse_code,
            lot_id, lot_code,
            qty_text::numeric, uom_id, uom_code, as_of_text::date, p_batch_id
        FROM resolved
        WHERE reason_code IS NULL
        ON CONFLICT DO NOTHING
        RETURNING 1
    ),
    rejected AS (
        INSERT INTO staging.ob_reject (batch_id, ob_load_id, reason_code, reason_detail, raw_line)
        SELECT
            p_batch_id, ob_load_id, reason_code,
            format('line=%s tenant=%s product=%s warehouse=%s uom=%s',
                   line_no, tenant_code, product_code, warehouse_code, uom_code),
            jsonb_build_object(
              'tenant_code', tenant_code, 'product_code', product_code,
              'warehouse_code', warehouse_code, 'lot_code', lot_code,
              'uom_code', uom_code, 'qty', qty_text, 'as_of_date', as_of_text
            )
        FROM resolved WHERE reason_code IS NOT NULL
        RETURNING 1
    )
    SELECT (SELECT count(*) FROM inserted)::int,
           (SELECT count(*) FROM rejected)::int
      INTO v_accepted, v_rejected;

    UPDATE staging.ob_load_batch
       SET status         = 'loaded',
           row_count      = (SELECT count(*) FROM staging.ob_load WHERE batch_id = p_batch_id),
           accepted_count = v_accepted,
           rejected_count = v_rejected,
           completed_at   = now()
     WHERE batch_id = p_batch_id;

    DELETE FROM staging.ob_load WHERE batch_id = p_batch_id;

    IF v_rejected > 0 THEN
        PERFORM notify_outbox(
            NULL, 'warn', 'load_opening_balance',
            format('batch %s loaded with %s rejects', p_batch_id, v_rejected),
            jsonb_build_object('batch_id', p_batch_id, 'rejected_count', v_rejected),
            'load_opening_balance:batch:' || p_batch_id);
    END IF;

    RETURN QUERY SELECT v_accepted, v_rejected;
END$f$;

ALTER FUNCTION load_opening_balance(bigint)
    SECURITY DEFINER
    SET search_path = public, pg_catalog, audit, staging;

-- ============================================================================
-- Part 10: upsert_order
--   - Auto-creates product/warehouse/uom on first encounter
--   - Populates code columns on sfdc_order and sfdc_order_line INSERTs
-- ============================================================================
DROP FUNCTION IF EXISTS upsert_order(jsonb);
CREATE FUNCTION upsert_order(p_payload jsonb)
RETURNS TABLE (
    sfdc_order_id   text,
    lines_inserted  int,
    lines_updated   int,
    lines_cancelled int,
    accepted        boolean,
    reason          text
)
LANGUAGE plpgsql
AS $$
#variable_conflict use_column
DECLARE
    v_tenant_id      uuid;
    v_tenant_code    text;
    v_order_id       text;
    v_inserted       int := 0;
    v_updated        int := 0;
    v_cancelled      int := 0;
    v_line           jsonb;
    v_lines          jsonb;
    v_product_id     bigint;
    v_product_code   text;
    v_warehouse_id   bigint;
    v_warehouse_code text;
    v_uom_id         bigint;
    v_uom_code       text;
    v_payload_line_nos int[];
    v_existed        boolean;
    v_existing_state text;
    v_requested_state text;
    v_effective_state text;
    v_order_state    text;
    v_existing_order_state text;
BEGIN
    v_order_id    := p_payload->>'sfdc_order_id';
    v_lines       := COALESCE(p_payload->'lines', '[]'::jsonb);
    v_order_state := COALESCE(p_payload->>'order_state', 'open');
    v_tenant_code := p_payload->>'tenant_code';

    SELECT t.tenant_id INTO v_tenant_id FROM tenant t WHERE t.tenant_code = v_tenant_code;
    IF v_tenant_id IS NULL THEN
        RETURN QUERY SELECT v_order_id, 0, 0, 0, false, 'unknown_reference'::text;
        RETURN;
    END IF;

    SELECT order_state INTO v_existing_order_state FROM sfdc_order WHERE sfdc_order_id = v_order_id;

    -- Order state machine: open → synced → closed; any → cancelled
    IF v_existing_order_state IS NOT NULL
       AND v_existing_order_state <> v_order_state
       AND NOT (
           (v_existing_order_state = 'open'      AND v_order_state IN ('synced','cancelled'))  OR
           (v_existing_order_state = 'synced'    AND v_order_state IN ('closed','cancelled'))  OR
           (v_existing_order_state = 'closed'    AND v_order_state = 'cancelled')              OR
           (v_existing_order_state = 'cancelled' AND v_order_state = 'cancelled')
       )
    THEN
        RETURN QUERY SELECT v_order_id, 0, 0, 0, false,
            format('illegal_order_state_transition: %s -> %s',
                   v_existing_order_state, v_order_state)::text;
        RETURN;
    END IF;

    INSERT INTO sfdc_order (sfdc_order_id, tenant_id, tenant_code, customer_id, order_state, payload)
    VALUES (v_order_id, v_tenant_id, v_tenant_code, p_payload->>'customer_id', v_order_state, p_payload)
    ON CONFLICT (sfdc_order_id) DO UPDATE
       SET customer_id = EXCLUDED.customer_id,
           order_state = EXCLUDED.order_state,
           payload     = EXCLUDED.payload,
           updated_at  = now();

    v_payload_line_nos := ARRAY(SELECT (jsonb_array_elements(v_lines)->>'line_no')::int);

    FOR v_line IN SELECT jsonb_array_elements(v_lines) LOOP
        v_product_code   := v_line->>'product_code';
        v_warehouse_code := v_line->>'warehouse_code';
        v_uom_code       := v_line->>'uom_code';

        -- Auto-create masters
        INSERT INTO product (tenant_id, product_code, name)
        VALUES (v_tenant_id, v_product_code, v_product_code)
        ON CONFLICT (tenant_id, product_code) DO NOTHING;
        SELECT pr.product_id INTO v_product_id
          FROM product pr WHERE pr.tenant_id = v_tenant_id AND pr.product_code = v_product_code;

        INSERT INTO warehouse (tenant_id, warehouse_code, name)
        VALUES (v_tenant_id, v_warehouse_code, v_warehouse_code)
        ON CONFLICT (tenant_id, warehouse_code) DO NOTHING;
        SELECT wh.warehouse_id INTO v_warehouse_id
          FROM warehouse wh WHERE wh.tenant_id = v_tenant_id AND wh.warehouse_code = v_warehouse_code;

        INSERT INTO uom (tenant_id, uom_code, name)
        VALUES (v_tenant_id, v_uom_code, v_uom_code)
        ON CONFLICT (tenant_id, uom_code) DO NOTHING;
        SELECT um.uom_id INTO v_uom_id
          FROM uom um WHERE um.tenant_id = v_tenant_id AND um.uom_code = v_uom_code;

        SELECT line_state, true INTO v_existing_state, v_existed
          FROM sfdc_order_line sol
         WHERE sol.sfdc_order_id = v_order_id AND sol.line_no = (v_line->>'line_no')::int;
        v_existed := COALESCE(v_existed, false);

        v_requested_state := COALESCE(v_line->>'line_state', 'open');

        IF NOT v_existed THEN
            v_effective_state := 'open';
            IF v_requested_state <> 'open' THEN
                PERFORM notify_outbox(v_tenant_id, 'info', 'upsert_order.line_state',
                    format('new line forced to open (caller requested %s)', v_requested_state),
                    jsonb_build_object(
                        'sfdc_order_id', v_order_id,
                        'line_no', v_line->>'line_no',
                        'requested_state', v_requested_state),
                    'upsert_order.line_state:' || v_order_id || ':' || (v_line->>'line_no'));
            END IF;
        ELSE
            v_effective_state := v_requested_state;
            IF v_existing_state <> v_effective_state
               AND NOT (
                   (v_existing_state = 'open'      AND v_effective_state IN ('synced','cancelled'))  OR
                   (v_existing_state = 'synced'    AND v_effective_state IN ('closed','cancelled'))  OR
                   (v_existing_state = 'closed'    AND v_effective_state = 'cancelled')              OR
                   (v_existing_state = 'cancelled' AND v_effective_state = 'cancelled')
               )
            THEN
                RETURN QUERY SELECT v_order_id, 0, 0, 0, false,
                    format('illegal_line_state_transition: line %s %s -> %s',
                           v_line->>'line_no', v_existing_state, v_effective_state)::text;
                RETURN;
            END IF;
        END IF;

        INSERT INTO sfdc_order_line (
            sfdc_order_id, line_no, tenant_id, tenant_code,
            product_id, product_code, warehouse_id, warehouse_code,
            qty, uom_id, uom_code, line_state, payload
        ) VALUES (
            v_order_id, (v_line->>'line_no')::int, v_tenant_id, v_tenant_code,
            v_product_id, v_product_code, v_warehouse_id, v_warehouse_code,
            (v_line->>'qty')::numeric, v_uom_id, v_uom_code,
            v_effective_state, v_line
        )
        ON CONFLICT (sfdc_order_id, line_no) DO UPDATE
           SET product_id     = EXCLUDED.product_id,
               product_code   = EXCLUDED.product_code,
               warehouse_id   = EXCLUDED.warehouse_id,
               warehouse_code = EXCLUDED.warehouse_code,
               qty            = EXCLUDED.qty,
               uom_id         = EXCLUDED.uom_id,
               uom_code       = EXCLUDED.uom_code,
               line_state     = EXCLUDED.line_state,
               payload        = EXCLUDED.payload,
               updated_at     = now();
        IF v_existed THEN v_updated := v_updated + 1; ELSE v_inserted := v_inserted + 1; END IF;
    END LOOP;

    UPDATE sfdc_order_line
       SET line_state = 'cancelled', updated_at = now()
     WHERE sfdc_order_line.sfdc_order_id = v_order_id
       AND sfdc_order_line.line_state <> 'cancelled'
       AND NOT (sfdc_order_line.line_no = ANY(v_payload_line_nos));
    GET DIAGNOSTICS v_cancelled = ROW_COUNT;

    RETURN QUERY SELECT v_order_id, v_inserted, v_updated, v_cancelled, true, 'ok'::text;
END$$;

ALTER FUNCTION upsert_order(jsonb)
    SECURITY DEFINER
    SET search_path = public, pg_catalog;

-- Re-grant after DROP+CREATE
GRANT EXECUTE ON FUNCTION upsert_order(jsonb) TO inventoryledger_app;

-- ============================================================================
-- Part 11: fetch_pending_orders — return codes alongside IDs
-- ============================================================================
DROP FUNCTION IF EXISTS fetch_pending_orders(uuid);
CREATE FUNCTION fetch_pending_orders(p_tenant_id uuid)
RETURNS TABLE (
    sfdc_order_id  text,
    line_no        int,
    product_id     bigint,
    product_code   text,
    warehouse_id   bigint,
    warehouse_code text,
    qty            numeric,
    uom_code       text,
    line_state     text,
    created_at     timestamptz
)
LANGUAGE sql
AS $$
    SELECT sfdc_order_id, line_no,
           product_id, product_code,
           warehouse_id, warehouse_code,
           qty, uom_code, line_state, created_at
      FROM sfdc_order_line
     WHERE tenant_id = p_tenant_id
       AND line_state IN ('open', 'synced')
       AND fop_synced_at IS NULL
     ORDER BY created_at;
$$;

-- ============================================================================
-- Part 12: Re-grant permissions stripped by DROP/CREATE
-- ============================================================================
REVOKE EXECUTE ON FUNCTION post_transaction(jsonb)        FROM PUBLIC;
REVOKE EXECUTE ON FUNCTION post_transaction_bulk(jsonb[]) FROM PUBLIC;
REVOKE EXECUTE ON FUNCTION upsert_order(jsonb)            FROM PUBLIC;
REVOKE EXECUTE ON FUNCTION load_opening_balance(bigint)   FROM PUBLIC;

GRANT EXECUTE ON FUNCTION load_opening_balance(bigint)   TO inventoryledger_app;
GRANT EXECUTE ON FUNCTION upsert_order(jsonb)            TO inventoryledger_app;
