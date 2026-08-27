-- V6__caps_and_uniform_response.sql
-- Adversarial findings (TC-B01, TC-E01, TC-E02, TC-H01) — fixes:
--   1. post_transaction_bulk: cap array length to pipeline_config.bulk_max_size
--   2. post_transaction: cap payload size to pipeline_config.payload_max_bytes
--   3. post_transaction: collapse unknown_tenant / unknown_product / unknown_warehouse
--      / unknown_uom / unknown_lot into a single 'unknown_reference' response, so
--      a caller can't enumerate tenants by differential responses. The specific
--      reason is still logged to notification_outbox for ops.
--   4. upsert_order: new lines (no prior row) are forced to line_state='open'.
--      Closing / cancelling must go through subsequent upsert_order calls or a
--      dedicated state-transition function; a malicious writer cannot create a
--      line that bypasses the pending bucket and never reduces ATP.

-- ============================================================================
-- 1. pipeline_config keys for new caps
-- ============================================================================
INSERT INTO pipeline_config (key, value, description) VALUES
    ('bulk_max_size',
     '1000',
     'post_transaction_bulk rejects arrays longer than this. Prevents lock-holding DoS via huge bulk calls.'),
    ('payload_max_bytes',
     '10240',
     'post_transaction rejects payloads larger than this many bytes (pg_column_size). Prevents DB bloat amplification.')
ON CONFLICT (key) DO NOTHING;

-- ============================================================================
-- 2. post_transaction — size cap + uniform unknown response
-- ============================================================================
CREATE OR REPLACE FUNCTION post_transaction(p_payload jsonb)
RETURNS TABLE (inv_transaction_id bigint, accepted boolean, reason text)
LANGUAGE plpgsql
AS $f$
#variable_conflict use_column
DECLARE
    v_tenant_id     uuid;
    v_product_id    bigint;
    v_warehouse_id  bigint;
    v_lot_id        bigint;
    v_uom_id        bigint;
    v_external      text;
    v_qty           numeric;
    v_txn_type      text;
    v_posted_at     timestamptz;
    v_source        text;
    v_transfer_pair uuid;
    v_id            bigint;
    v_on_hand       numeric;
    v_specific      text;
    v_size_cap      int := pipeline_config_int('payload_max_bytes');
BEGIN
    -- Size cap (defense against payload bloat)
    IF v_size_cap IS NOT NULL AND pg_column_size(p_payload) > v_size_cap THEN
        RETURN QUERY SELECT NULL::bigint, false, 'payload_too_large'::text;
        RETURN;
    END IF;

    v_external      := p_payload->>'external_txn_id';
    v_qty           := (p_payload->>'signed_qty')::numeric;
    v_txn_type      := p_payload->>'txn_type';
    v_posted_at     := COALESCE((p_payload->>'posted_at')::timestamptz, now());
    v_source        := COALESCE(p_payload->>'source_system', 'unknown');
    v_transfer_pair := NULLIF(p_payload->>'transfer_pair_id', '')::uuid;

    -- Reference resolution — collect a specific reason internally for ops,
    -- but return a uniform 'unknown_reference' to the caller so tenant existence
    -- cannot be probed via differential responses (see TC-B01).
    SELECT t.tenant_id INTO v_tenant_id FROM tenant t WHERE t.tenant_code = p_payload->>'tenant_code';
    IF v_tenant_id IS NULL THEN
        v_specific := 'unknown_tenant: ' || COALESCE(p_payload->>'tenant_code','<null>');
        INSERT INTO notification_outbox (severity, source, message, payload)
        VALUES ('info', 'post_transaction.lookup', v_specific,
                jsonb_build_object('hint', 'tenant_code not found',
                                   'tenant_code', p_payload->>'tenant_code'));
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_reference'::text; RETURN;
    END IF;

    SELECT pr.product_id   INTO v_product_id   FROM product   pr WHERE pr.tenant_id = v_tenant_id AND pr.product_code   = p_payload->>'product_code';
    SELECT wh.warehouse_id INTO v_warehouse_id FROM warehouse wh WHERE wh.tenant_id = v_tenant_id AND wh.warehouse_code = p_payload->>'warehouse_code';
    SELECT um.uom_id       INTO v_uom_id       FROM uom       um WHERE um.tenant_id = v_tenant_id AND um.uom_code       = p_payload->>'uom_code';
    IF p_payload ? 'lot_code' AND (p_payload->>'lot_code') IS NOT NULL THEN
        SELECT lt.lot_id INTO v_lot_id FROM lot lt
         WHERE lt.tenant_id = v_tenant_id AND lt.product_id = v_product_id AND lt.lot_code = p_payload->>'lot_code';
        IF v_lot_id IS NULL THEN
            INSERT INTO notification_outbox (tenant_id, severity, source, message, payload)
            VALUES (v_tenant_id, 'info', 'post_transaction.lookup',
                    'unknown_lot: ' || (p_payload->>'lot_code'),
                    jsonb_build_object('hint','lot_code not found','lot_code', p_payload->>'lot_code'));
            RETURN QUERY SELECT NULL::bigint, false, 'unknown_reference'::text; RETURN;
        END IF;
    END IF;

    IF v_product_id IS NULL THEN
        INSERT INTO notification_outbox (tenant_id, severity, source, message, payload)
        VALUES (v_tenant_id, 'info', 'post_transaction.lookup',
                'unknown_product: ' || (p_payload->>'product_code'),
                jsonb_build_object('hint','product_code not found','product_code', p_payload->>'product_code'));
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_reference'::text; RETURN;
    END IF;
    IF v_warehouse_id IS NULL THEN
        INSERT INTO notification_outbox (tenant_id, severity, source, message, payload)
        VALUES (v_tenant_id, 'info', 'post_transaction.lookup',
                'unknown_warehouse: ' || (p_payload->>'warehouse_code'),
                jsonb_build_object('hint','warehouse_code not found','warehouse_code', p_payload->>'warehouse_code'));
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_reference'::text; RETURN;
    END IF;
    IF v_uom_id IS NULL THEN
        INSERT INTO notification_outbox (tenant_id, severity, source, message, payload)
        VALUES (v_tenant_id, 'info', 'post_transaction.lookup',
                'unknown_uom: ' || (p_payload->>'uom_code'),
                jsonb_build_object('hint','uom_code not found','uom_code', p_payload->>'uom_code'));
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_reference'::text; RETURN;
    END IF;
    IF v_qty IS NULL OR v_qty = 0 THEN
        RETURN QUERY SELECT NULL::bigint, false, 'invalid_qty'::text; RETURN;
    END IF;

    -- Sign-vs-type pre-check (V5)
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

    -- Backorder / oversell info notification (V5)
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

-- ============================================================================
-- 3. post_transaction_bulk — array length cap
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

-- ============================================================================
-- 4. upsert_order — new lines forced to line_state='open'
-- ============================================================================
CREATE OR REPLACE FUNCTION upsert_order(p_payload jsonb)
RETURNS TABLE (sfdc_order_id text, lines_inserted int, lines_updated int, lines_cancelled int)
LANGUAGE plpgsql
AS $$
#variable_conflict use_column
DECLARE
    v_tenant_id    uuid;
    v_order_id     text;
    v_inserted     int := 0;
    v_updated      int := 0;
    v_cancelled    int := 0;
    v_line         jsonb;
    v_lines        jsonb;
    v_product_id   bigint;
    v_warehouse_id bigint;
    v_uom_id       bigint;
    v_payload_line_nos int[];
    v_existed      boolean;
    v_effective_state text;
BEGIN
    v_order_id := p_payload->>'sfdc_order_id';
    v_lines    := COALESCE(p_payload->'lines', '[]'::jsonb);

    SELECT t.tenant_id INTO v_tenant_id FROM tenant t WHERE t.tenant_code = p_payload->>'tenant_code';
    IF v_tenant_id IS NULL THEN
        RAISE EXCEPTION 'unknown tenant: %', p_payload->>'tenant_code';
    END IF;

    INSERT INTO sfdc_order (sfdc_order_id, tenant_id, customer_id, order_state, payload)
    VALUES (
        v_order_id, v_tenant_id,
        p_payload->>'customer_id',
        COALESCE(p_payload->>'order_state', 'open'),
        p_payload
    )
    ON CONFLICT (sfdc_order_id) DO UPDATE
       SET customer_id = EXCLUDED.customer_id,
           order_state = EXCLUDED.order_state,
           payload     = EXCLUDED.payload,
           updated_at  = now();

    v_payload_line_nos := ARRAY(SELECT (jsonb_array_elements(v_lines)->>'line_no')::int);

    FOR v_line IN SELECT jsonb_array_elements(v_lines) LOOP
        SELECT pr.product_id   INTO v_product_id   FROM product   pr WHERE pr.tenant_id = v_tenant_id AND pr.product_code   = v_line->>'product_code';
        SELECT wh.warehouse_id INTO v_warehouse_id FROM warehouse wh WHERE wh.tenant_id = v_tenant_id AND wh.warehouse_code = v_line->>'warehouse_code';
        SELECT um.uom_id       INTO v_uom_id       FROM uom       um WHERE um.tenant_id = v_tenant_id AND um.uom_code       = v_line->>'uom_code';

        SELECT EXISTS (SELECT 1 FROM sfdc_order_line sol
                        WHERE sol.sfdc_order_id = v_order_id AND sol.line_no = (v_line->>'line_no')::int)
          INTO v_existed;

        -- New lines are FORCED to line_state='open'. Closing/cancelling a line
        -- must happen through a subsequent upsert_order call that already finds
        -- the line existing.
        IF v_existed THEN
            v_effective_state := COALESCE(v_line->>'line_state', 'open');
        ELSE
            v_effective_state := 'open';
            IF v_line ? 'line_state' AND (v_line->>'line_state') NOT IN ('open') THEN
                -- Caller tried to create a line in a non-open state; log for ops
                INSERT INTO notification_outbox (tenant_id, severity, source, message, payload)
                VALUES (v_tenant_id, 'info', 'upsert_order.line_state',
                        format('new line forced to open (caller requested %s)', v_line->>'line_state'),
                        jsonb_build_object(
                            'sfdc_order_id', v_order_id,
                            'line_no', v_line->>'line_no',
                            'requested_state', v_line->>'line_state'));
            END IF;
        END IF;

        INSERT INTO sfdc_order_line (
            sfdc_order_id, line_no, tenant_id, product_id, warehouse_id, qty, uom_id, line_state, payload
        ) VALUES (
            v_order_id, (v_line->>'line_no')::int, v_tenant_id,
            v_product_id, v_warehouse_id,
            (v_line->>'qty')::numeric, v_uom_id,
            v_effective_state,
            v_line
        )
        ON CONFLICT (sfdc_order_id, line_no) DO UPDATE
           SET product_id   = EXCLUDED.product_id,
               warehouse_id = EXCLUDED.warehouse_id,
               qty          = EXCLUDED.qty,
               uom_id       = EXCLUDED.uom_id,
               line_state   = EXCLUDED.line_state,
               payload      = EXCLUDED.payload,
               updated_at   = now();
        IF v_existed THEN v_updated := v_updated + 1; ELSE v_inserted := v_inserted + 1; END IF;
    END LOOP;

    UPDATE sfdc_order_line
       SET line_state = 'cancelled', updated_at = now()
     WHERE sfdc_order_line.sfdc_order_id = v_order_id
       AND sfdc_order_line.line_state <> 'cancelled'
       AND NOT (sfdc_order_line.line_no = ANY(v_payload_line_nos));
    GET DIAGNOSTICS v_cancelled = ROW_COUNT;

    RETURN QUERY SELECT v_order_id, v_inserted, v_updated, v_cancelled;
END$$;
