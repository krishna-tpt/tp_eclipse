-- V7__outbox_dedup_and_audit_trim.sql
-- Adversarial findings (TC-E03, TC-E04) — fixes:
--   1. notification_outbox: dedup within a sliding window per
--      (tenant_id, source, dedup_key). A flood of identical oversells now
--      bumps a counter on one row instead of producing N rows.
--   2. audit_log.after_data: when row contains a 'payload' jsonb that is
--      itself larger than pipeline_config.audit_payload_max_bytes, replace
--      with a SHA-256 digest + size marker. Stops attacker payload bloat
--      from amplifying audit storage 2x.

-- ============================================================================
-- 1. pipeline_config keys
-- ============================================================================
INSERT INTO pipeline_config (key, value, description) VALUES
    ('outbox_dedup_window_minutes',
     '60',
     'Within this window, repeated notifications with identical (tenant_id, source, dedup_key) update an existing row''s repeat_count instead of inserting a new row.'),
    ('audit_payload_max_bytes',
     '4096',
     'In audit_log.after_data, any nested "payload" field larger than this is replaced with {hash, size_bytes, truncated:true}. The original lives on in the source table only.')
ON CONFLICT (key) DO NOTHING;

-- ============================================================================
-- 2. notification_outbox.repeat_count column
-- ============================================================================
ALTER TABLE notification_outbox
    ADD COLUMN IF NOT EXISTS repeat_count int NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS dedup_key text;

CREATE INDEX IF NOT EXISTS idx_notification_outbox_dedup
    ON notification_outbox (tenant_id, source, dedup_key, created_at)
    WHERE status IN ('pending', 'failed');

-- ============================================================================
-- 3. Helper — dedup-aware insert
-- ============================================================================
CREATE OR REPLACE FUNCTION notify_outbox(
    p_tenant_id uuid,
    p_severity  text,
    p_source    text,
    p_message   text,
    p_payload   jsonb,
    p_dedup_key text DEFAULT NULL
) RETURNS bigint
LANGUAGE plpgsql
AS $$
DECLARE
    v_window int := pipeline_config_int('outbox_dedup_window_minutes');
    v_id bigint;
BEGIN
    IF p_dedup_key IS NULL THEN
        INSERT INTO notification_outbox (
            tenant_id, severity, source, message, payload, dedup_key, repeat_count
        ) VALUES (p_tenant_id, p_severity, p_source, p_message, p_payload, NULL, 1)
        RETURNING notification_outbox_id INTO v_id;
        RETURN v_id;
    END IF;

    -- Find an existing pending/failed row within the dedup window
    SELECT notification_outbox_id INTO v_id
      FROM notification_outbox
     WHERE COALESCE(tenant_id::text,'') = COALESCE(p_tenant_id::text,'')
       AND source = p_source
       AND dedup_key = p_dedup_key
       AND status IN ('pending','failed')
       AND created_at > now() - (v_window || ' minutes')::interval
     ORDER BY created_at DESC
     LIMIT 1
     FOR UPDATE;

    IF v_id IS NOT NULL THEN
        UPDATE notification_outbox
           SET repeat_count = repeat_count + 1,
               message      = p_message,   -- keep latest message
               payload      = p_payload,   -- keep latest payload (size cap below)
               updated_at   = now()
         WHERE notification_outbox_id = v_id;
        RETURN v_id;
    END IF;

    INSERT INTO notification_outbox (
        tenant_id, severity, source, message, payload, dedup_key, repeat_count
    ) VALUES (p_tenant_id, p_severity, p_source, p_message, p_payload, p_dedup_key, 1)
    RETURNING notification_outbox_id INTO v_id;
    RETURN v_id;
END$$;

-- ============================================================================
-- 4. post_transaction — use dedup-aware notify for oversell + lookup events
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
    v_size_cap      int := pipeline_config_int('payload_max_bytes');
BEGIN
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

    SELECT t.tenant_id INTO v_tenant_id FROM tenant t WHERE t.tenant_code = p_payload->>'tenant_code';
    IF v_tenant_id IS NULL THEN
        PERFORM notify_outbox(NULL, 'info', 'post_transaction.lookup',
            'unknown_tenant: ' || COALESCE(p_payload->>'tenant_code','<null>'),
            jsonb_build_object('hint','tenant_code not found','tenant_code', p_payload->>'tenant_code'),
            'unknown_tenant:' || COALESCE(p_payload->>'tenant_code','<null>'));
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_reference'::text; RETURN;
    END IF;

    SELECT pr.product_id   INTO v_product_id   FROM product   pr WHERE pr.tenant_id = v_tenant_id AND pr.product_code   = p_payload->>'product_code';
    SELECT wh.warehouse_id INTO v_warehouse_id FROM warehouse wh WHERE wh.tenant_id = v_tenant_id AND wh.warehouse_code = p_payload->>'warehouse_code';
    SELECT um.uom_id       INTO v_uom_id       FROM uom       um WHERE um.tenant_id = v_tenant_id AND um.uom_code       = p_payload->>'uom_code';
    IF p_payload ? 'lot_code' AND (p_payload->>'lot_code') IS NOT NULL THEN
        SELECT lt.lot_id INTO v_lot_id FROM lot lt
         WHERE lt.tenant_id = v_tenant_id AND lt.product_id = v_product_id AND lt.lot_code = p_payload->>'lot_code';
        IF v_lot_id IS NULL THEN
            PERFORM notify_outbox(v_tenant_id, 'info', 'post_transaction.lookup',
                'unknown_lot: ' || (p_payload->>'lot_code'),
                jsonb_build_object('hint','lot_code not found','lot_code', p_payload->>'lot_code'),
                'unknown_lot:' || (p_payload->>'lot_code'));
            RETURN QUERY SELECT NULL::bigint, false, 'unknown_reference'::text; RETURN;
        END IF;
    END IF;

    IF v_product_id IS NULL THEN
        PERFORM notify_outbox(v_tenant_id, 'info', 'post_transaction.lookup',
            'unknown_product: ' || (p_payload->>'product_code'),
            jsonb_build_object('hint','product_code not found','product_code', p_payload->>'product_code'),
            'unknown_product:' || (p_payload->>'product_code'));
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_reference'::text; RETURN;
    END IF;
    IF v_warehouse_id IS NULL THEN
        PERFORM notify_outbox(v_tenant_id, 'info', 'post_transaction.lookup',
            'unknown_warehouse: ' || (p_payload->>'warehouse_code'),
            jsonb_build_object('hint','warehouse_code not found','warehouse_code', p_payload->>'warehouse_code'),
            'unknown_warehouse:' || (p_payload->>'warehouse_code'));
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_reference'::text; RETURN;
    END IF;
    IF v_uom_id IS NULL THEN
        PERFORM notify_outbox(v_tenant_id, 'info', 'post_transaction.lookup',
            'unknown_uom: ' || (p_payload->>'uom_code'),
            jsonb_build_object('hint','uom_code not found','uom_code', p_payload->>'uom_code'),
            'unknown_uom:' || (p_payload->>'uom_code'));
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_reference'::text; RETURN;
    END IF;
    IF v_qty IS NULL OR v_qty = 0 THEN
        RETURN QUERY SELECT NULL::bigint, false, 'invalid_qty'::text; RETURN;
    END IF;

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

    -- Oversell — dedup by (tenant, product, warehouse) so a flood of identical
    -- oversells collapses into one outbox row with incremented repeat_count.
    IF v_qty < 0 AND v_txn_type IN ('issue','shipment') THEN
        SELECT on_hand_qty INTO v_on_hand
          FROM calculate_inventory(v_tenant_id, v_product_id, v_warehouse_id);
        IF v_on_hand < 0 THEN
            PERFORM notify_outbox(
                v_tenant_id, 'info', 'post_transaction',
                format('oversell: on_hand=%s after txn %s', v_on_hand, v_external),
                jsonb_build_object(
                    'external_txn_id', v_external,
                    'product_id', v_product_id,
                    'warehouse_id', v_warehouse_id,
                    'on_hand_after', v_on_hand,
                    'signed_qty', v_qty),
                format('oversell:%s:%s:%s', v_tenant_id, v_product_id, v_warehouse_id));
        END IF;
    END IF;

    RETURN QUERY SELECT v_id, true, 'ok'::text;
END$f$;

-- ============================================================================
-- 5. f_audit_capture — trim large 'payload' field in row JSON
-- ============================================================================
CREATE OR REPLACE FUNCTION audit.f_audit_capture()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = audit, public, pg_catalog
AS $$
DECLARE
    v_tenant_id uuid;
    v_before    jsonb;
    v_after     jsonb;
    v_cap       int;
    v_payload   text;
    v_marker    jsonb;
BEGIN
    BEGIN
        v_cap := pipeline_config_int('audit_payload_max_bytes');
    EXCEPTION WHEN OTHERS THEN v_cap := NULL; END;

    IF TG_OP = 'INSERT' THEN
        v_after  := to_jsonb(NEW);
        v_tenant_id := NULLIF(v_after->>'tenant_id', '')::uuid;
    ELSIF TG_OP = 'UPDATE' THEN
        v_before := to_jsonb(OLD);
        v_after  := to_jsonb(NEW);
        v_tenant_id := COALESCE(NULLIF(v_after->>'tenant_id',''), NULLIF(v_before->>'tenant_id',''))::uuid;
    ELSIF TG_OP = 'DELETE' THEN
        v_before := to_jsonb(OLD);
        v_tenant_id := NULLIF(v_before->>'tenant_id','')::uuid;
    END IF;

    -- Trim huge 'payload' subfield if present
    IF v_cap IS NOT NULL THEN
        IF v_after IS NOT NULL AND v_after ? 'payload' THEN
            v_payload := v_after->>'payload';
            IF v_payload IS NOT NULL AND length(v_payload) > v_cap THEN
                v_marker := jsonb_build_object(
                    'truncated', true,
                    'size_bytes', length(v_payload),
                    'sha256', encode(digest(v_payload, 'sha256'), 'hex'));
                v_after := v_after - 'payload' || jsonb_build_object('payload', v_marker);
            END IF;
        END IF;
        IF v_before IS NOT NULL AND v_before ? 'payload' THEN
            v_payload := v_before->>'payload';
            IF v_payload IS NOT NULL AND length(v_payload) > v_cap THEN
                v_marker := jsonb_build_object(
                    'truncated', true,
                    'size_bytes', length(v_payload),
                    'sha256', encode(digest(v_payload, 'sha256'), 'hex'));
                v_before := v_before - 'payload' || jsonb_build_object('payload', v_marker);
            END IF;
        END IF;
    END IF;

    INSERT INTO audit.audit_log (
        tenant_id, table_schema, table_name, operation, before_data, after_data
    ) VALUES (
        v_tenant_id, TG_TABLE_SCHEMA, TG_TABLE_NAME, left(TG_OP, 1), v_before, v_after
    );

    RETURN COALESCE(NEW, OLD);
END$$;
