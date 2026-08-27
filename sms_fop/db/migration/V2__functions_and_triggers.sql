-- V2__functions_and_triggers.sql
-- Triggers and stored functions for the inventory ledger.
--
-- Owns the write path: stock_balance updated by trigger; mv_atp_dirty marker set
-- by trigger; audit_log populated by trigger. Public functions:
--   load_opening_balance(batch_id)
--   post_transaction(payload jsonb)            -> returns (inv_transaction_id, accepted, reason)
--   post_transaction_bulk(payloads jsonb[])    -> returns table of same shape
--   upsert_order(payload jsonb)                -> returns (sfdc_order_id, lines_changed)
--   refresh_mv_atp()                           -> refreshes mv_atp for dirty tenants
--   calculate_inventory(tenant_id, product_id, warehouse_id)  -> on-the-fly calc
--   fetch_inventory(tenant_id, product_id, warehouse_id)      -> MV-backed read
--   fetch_pending_orders(tenant_id)
--
-- recalculate_inventory is deliberately NOT included; deferred feature.

-- ============================================================================
-- Audit trigger function (generic; attached to every domain table)
-- Reads NEW/OLD via row_to_json; captures tenant_id when present in NEW or OLD.
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
BEGIN
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

    INSERT INTO audit.audit_log (
        tenant_id, table_schema, table_name, operation, before_data, after_data
    ) VALUES (
        v_tenant_id, TG_TABLE_SCHEMA, TG_TABLE_NAME, left(TG_OP, 1), v_before, v_after
    );

    RETURN COALESCE(NEW, OLD);
END$$;

-- Attach audit triggers to all domain tables
DO $$
DECLARE
    rec record;
BEGIN
    FOR rec IN
        SELECT * FROM (VALUES
            ('public', 'tenant'),
            ('public', 'product'),
            ('public', 'warehouse'),
            ('public', 'uom'),
            ('public', 'lot'),
            ('public', 'opening_balance'),
            ('public', 'inv_transaction'),
            ('public', 'stock_balance'),
            ('public', 'sfdc_order'),
            ('public', 'sfdc_order_line')
        ) AS v(schema_name, table_name)
    LOOP
        EXECUTE format($f$
            CREATE TRIGGER trg_audit_%I
            AFTER INSERT OR UPDATE OR DELETE ON %I.%I
            FOR EACH ROW EXECUTE FUNCTION audit.f_audit_capture();
        $f$, rec.table_name, rec.schema_name, rec.table_name);
    END LOOP;
END$$;

-- ============================================================================
-- stock_balance update trigger on inv_transaction
-- ============================================================================
CREATE OR REPLACE FUNCTION f_stock_balance_apply()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO stock_balance (
        tenant_id, product_id, warehouse_id, lot_id, on_hand_qty, uom_id, last_updated_at
    ) VALUES (
        NEW.tenant_id, NEW.product_id, NEW.warehouse_id,
        COALESCE(NEW.lot_id, 0), NEW.signed_qty, NEW.uom_id, now()
    )
    ON CONFLICT (tenant_id, product_id, warehouse_id, lot_id) DO UPDATE
        SET on_hand_qty     = stock_balance.on_hand_qty + EXCLUDED.on_hand_qty,
            last_updated_at = now();
    RETURN NEW;
END$$;

CREATE TRIGGER trg_inv_txn_stock_balance
AFTER INSERT ON inv_transaction
FOR EACH ROW EXECUTE FUNCTION f_stock_balance_apply();

-- ============================================================================
-- mv_atp_dirty marker triggers
-- ============================================================================
CREATE OR REPLACE FUNCTION f_mv_atp_mark_dirty()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
    v_tenant_id uuid;
BEGIN
    v_tenant_id := COALESCE(NEW.tenant_id, OLD.tenant_id);
    INSERT INTO mv_atp_dirty (tenant_id) VALUES (v_tenant_id)
    ON CONFLICT (tenant_id) DO UPDATE SET marked_at = now();
    RETURN COALESCE(NEW, OLD);
END$$;

CREATE TRIGGER trg_inv_txn_mv_dirty
AFTER INSERT OR UPDATE OR DELETE ON inv_transaction
FOR EACH ROW EXECUTE FUNCTION f_mv_atp_mark_dirty();

CREATE TRIGGER trg_sfdc_order_line_mv_dirty
AFTER INSERT OR UPDATE OR DELETE ON sfdc_order_line
FOR EACH ROW EXECUTE FUNCTION f_mv_atp_mark_dirty();

CREATE TRIGGER trg_opening_balance_mv_dirty
AFTER INSERT OR UPDATE OR DELETE ON opening_balance
FOR EACH ROW EXECUTE FUNCTION f_mv_atp_mark_dirty();

-- ============================================================================
-- reconcile_orders trigger
-- When an inv_transaction arrives that matches an open sfdc_order_line
-- (same tenant + product + warehouse, qty >= line.qty, fop_synced_at IS NULL),
-- stamp fop_synced_at on the oldest matching line.
-- ============================================================================
CREATE OR REPLACE FUNCTION f_reconcile_orders()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
    v_matched_id text;
    v_matched_no int;
BEGIN
    -- Only true customer shipments reconcile orders. An adjustment,
    -- purchase_return, transfer_out or scrap is outbound but must NOT
    -- mark a customer order as fulfilled.
    IF NEW.signed_qty >= 0 THEN
        RETURN NEW;
    END IF;
    IF NEW.txn_type NOT IN ('issue', 'shipment') THEN
        RETURN NEW;
    END IF;

    SELECT sfdc_order_id, line_no
      INTO v_matched_id, v_matched_no
      FROM sfdc_order_line
     WHERE tenant_id    = NEW.tenant_id
       AND product_id   = NEW.product_id
       AND warehouse_id = NEW.warehouse_id
       AND line_state IN ('open', 'synced')
       AND fop_synced_at IS NULL
       AND qty <= ABS(NEW.signed_qty)
     ORDER BY created_at
     LIMIT 1;

    IF v_matched_id IS NOT NULL THEN
        UPDATE sfdc_order_line
           SET fop_synced_at = now(),
               line_state    = 'synced',
               updated_at    = now()
         WHERE sfdc_order_id = v_matched_id
           AND line_no       = v_matched_no;
    END IF;

    RETURN NEW;
END$$;

CREATE TRIGGER trg_inv_txn_reconcile_orders
AFTER INSERT ON inv_transaction
FOR EACH ROW EXECUTE FUNCTION f_reconcile_orders();

-- ============================================================================
-- load_opening_balance(batch_id bigint)
-- Reads staging.ob_load for the batch, validates each row against masters,
-- inserts good rows to opening_balance, rejects bad rows to staging.ob_reject.
-- Updates batch metadata. Idempotent on batch_id (skips if already loaded).
-- ============================================================================
CREATE OR REPLACE FUNCTION load_opening_balance(p_batch_id bigint)
RETURNS TABLE (accepted_count int, rejected_count int)
LANGUAGE plpgsql
AS $$
DECLARE
    v_status text;
    v_accepted int := 0;
    v_rejected int := 0;
BEGIN
    SELECT status INTO v_status FROM staging.ob_load_batch WHERE batch_id = p_batch_id FOR UPDATE;
    IF v_status IS NULL THEN
        RAISE EXCEPTION 'batch % not found', p_batch_id;
    END IF;
    IF v_status = 'loaded' THEN
        SELECT accepted_count, rejected_count INTO v_accepted, v_rejected
          FROM staging.ob_load_batch WHERE batch_id = p_batch_id;
        RETURN QUERY SELECT v_accepted, v_rejected;
        RETURN;
    END IF;

    -- Resolve codes to ids; collect rejection reasons
    WITH resolved AS (
        SELECT
            l.ob_load_id,
            l.line_no,
            t.tenant_id,
            p.product_id,
            w.warehouse_id,
            lt.lot_id,
            u.uom_id,
            l.tenant_code, l.product_code, l.warehouse_code, l.lot_code, l.uom_code,
            l.qty AS qty_text,
            l.as_of_date AS as_of_text,
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
        LEFT JOIN tenant   t  ON t.tenant_code   = l.tenant_code
        LEFT JOIN product  p  ON p.product_code  = l.product_code  AND p.tenant_id = t.tenant_id
        LEFT JOIN warehouse w ON w.warehouse_code = l.warehouse_code AND w.tenant_id = t.tenant_id
        LEFT JOIN uom      u  ON u.uom_code      = l.uom_code      AND u.tenant_id = t.tenant_id
        LEFT JOIN lot      lt ON lt.lot_code     = l.lot_code      AND lt.tenant_id = t.tenant_id AND lt.product_id = p.product_id
        WHERE l.batch_id = p_batch_id
    ),
    inserted AS (
        INSERT INTO opening_balance (
            tenant_id, product_id, warehouse_id, lot_id, qty, uom_id, as_of_date, batch_id
        )
        SELECT
            tenant_id, product_id, warehouse_id, lot_id,
            qty_text::numeric, uom_id, as_of_text::date, p_batch_id
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

    -- Truncate staging rows for this batch (NFR: purge staging post-load)
    DELETE FROM staging.ob_load WHERE batch_id = p_batch_id;

    IF v_rejected > 0 THEN
        INSERT INTO notification_outbox (severity, source, message, payload)
        VALUES ('warn', 'load_opening_balance',
                format('batch %s loaded with %s rejects', p_batch_id, v_rejected),
                jsonb_build_object('batch_id', p_batch_id, 'rejected_count', v_rejected));
    END IF;

    RETURN QUERY SELECT v_accepted, v_rejected;
END$$;

-- ============================================================================
-- post_transaction(payload jsonb)
-- Resolves codes, validates, inserts to inv_transaction. Idempotent on
-- (tenant_id, external_txn_id). Returns the inserted txn id or NULL if duplicate.
-- ============================================================================
CREATE OR REPLACE FUNCTION post_transaction(p_payload jsonb)
RETURNS TABLE (inv_transaction_id bigint, accepted boolean, reason text)
LANGUAGE plpgsql
AS $$
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
BEGIN
    v_external      := p_payload->>'external_txn_id';
    v_qty           := (p_payload->>'signed_qty')::numeric;
    v_txn_type      := p_payload->>'txn_type';
    v_posted_at     := COALESCE((p_payload->>'posted_at')::timestamptz, now());
    v_source        := COALESCE(p_payload->>'source_system', 'unknown');
    v_transfer_pair := NULLIF(p_payload->>'transfer_pair_id', '')::uuid;

    SELECT tenant_id INTO v_tenant_id FROM tenant WHERE tenant_code = p_payload->>'tenant_code';
    IF v_tenant_id IS NULL THEN
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_tenant'::text;
        RETURN;
    END IF;

    SELECT product_id   INTO v_product_id   FROM product   WHERE tenant_id = v_tenant_id AND product_code   = p_payload->>'product_code';
    SELECT warehouse_id INTO v_warehouse_id FROM warehouse WHERE tenant_id = v_tenant_id AND warehouse_code = p_payload->>'warehouse_code';
    SELECT uom_id       INTO v_uom_id       FROM uom       WHERE tenant_id = v_tenant_id AND uom_code       = p_payload->>'uom_code';
    IF p_payload ? 'lot_code' AND (p_payload->>'lot_code') IS NOT NULL THEN
        SELECT lot_id INTO v_lot_id FROM lot WHERE tenant_id = v_tenant_id AND product_id = v_product_id AND lot_code = p_payload->>'lot_code';
        IF v_lot_id IS NULL THEN
            RETURN QUERY SELECT NULL::bigint, false, 'unknown_lot'::text;
            RETURN;
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

    BEGIN
        INSERT INTO inv_transaction AS it (
            tenant_id, external_txn_id, product_id, warehouse_id, lot_id,
            signed_qty, uom_id, txn_type, transfer_pair_id, source_system, posted_at, payload
        ) VALUES (
            v_tenant_id, v_external, v_product_id, v_warehouse_id, v_lot_id,
            v_qty, v_uom_id, v_txn_type, v_transfer_pair, v_source, v_posted_at, p_payload
        )
        RETURNING it.inv_transaction_id INTO v_id;
        RETURN QUERY SELECT v_id, true, 'ok'::text;
    EXCEPTION
        WHEN unique_violation THEN
            RETURN QUERY SELECT NULL::bigint, false, 'duplicate'::text;
    END;
END$$;

-- ============================================================================
-- post_transaction_bulk(payloads jsonb[])
-- Calls post_transaction in a loop; one audit row per row. Transfers should be
-- supplied as paired rows sharing transfer_pair_id; this function does NOT
-- enforce pair atomicity beyond standard transaction semantics.
-- ============================================================================
CREATE OR REPLACE FUNCTION post_transaction_bulk(p_payloads jsonb[])
RETURNS TABLE (idx int, inv_transaction_id bigint, accepted boolean, reason text)
LANGUAGE plpgsql
AS $$
DECLARE
    i int;
    r record;
BEGIN
    FOR i IN 1 .. array_length(p_payloads, 1) LOOP
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
-- upsert_order(payload jsonb)
-- Diffs against existing order + lines. Idempotent. Returns line change counts.
-- Lines present in DB but not in payload are marked cancelled.
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

        INSERT INTO sfdc_order_line (
            sfdc_order_id, line_no, tenant_id, product_id, warehouse_id, qty, uom_id, line_state, payload
        ) VALUES (
            v_order_id, (v_line->>'line_no')::int, v_tenant_id,
            v_product_id, v_warehouse_id,
            (v_line->>'qty')::numeric, v_uom_id,
            COALESCE(v_line->>'line_state', 'open'),
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

-- ============================================================================
-- refresh_mv_atp() — REFRESH MV CONCURRENTLY; clear dirty markers.
-- pg_cron schedules this via V3 migration.
-- ============================================================================
CREATE OR REPLACE FUNCTION refresh_mv_atp()
RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM mv_atp_dirty) THEN
        RETURN;
    END IF;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_atp;
    DELETE FROM mv_atp_dirty;
END$$;

-- ============================================================================
-- calculate_inventory: on-the-fly recompute for one (tenant, product, warehouse)
-- Used by drift detection (NFR) and any caller that wants real-time numbers
-- without waiting for MV refresh.
-- ============================================================================
CREATE OR REPLACE FUNCTION calculate_inventory(
    p_tenant_id    uuid,
    p_product_id   bigint,
    p_warehouse_id bigint
)
RETURNS TABLE (
    on_hand_qty   numeric,
    reserved_qty  numeric,
    allocated_qty numeric,
    pending_qty   numeric,
    atp_qty       numeric,
    computed_at   timestamptz
)
LANGUAGE sql
AS $$
    WITH
    o AS (
        SELECT COALESCE(SUM(qty), 0) AS on_hand
          FROM opening_balance
         WHERE tenant_id = p_tenant_id AND product_id = p_product_id AND warehouse_id = p_warehouse_id
    ),
    t AS (
        SELECT COALESCE(SUM(signed_qty), 0) AS txn_qty
          FROM inv_transaction
         WHERE tenant_id = p_tenant_id AND product_id = p_product_id AND warehouse_id = p_warehouse_id
    ),
    r AS (
        SELECT COALESCE(SUM(qty), 0) AS qty FROM sfdc_order_line
         WHERE tenant_id = p_tenant_id AND product_id = p_product_id AND warehouse_id = p_warehouse_id
           AND line_state = 'open'
    ),
    a AS (
        SELECT COALESCE(SUM(qty), 0) AS qty FROM sfdc_order_line
         WHERE tenant_id = p_tenant_id AND product_id = p_product_id AND warehouse_id = p_warehouse_id
           AND line_state = 'synced'
    ),
    p AS (
        SELECT COALESCE(SUM(qty), 0) AS qty FROM sfdc_order_line
         WHERE tenant_id = p_tenant_id AND product_id = p_product_id AND warehouse_id = p_warehouse_id
           AND line_state IN ('open', 'synced') AND fop_synced_at IS NULL
    )
    SELECT
        o.on_hand + t.txn_qty,
        r.qty,
        a.qty,
        p.qty,
        o.on_hand + t.txn_qty - p.qty,
        now()
    FROM o, t, r, a, p;
$$;

-- ============================================================================
-- fetch_inventory — MV-backed read. NULL p_product/p_warehouse returns all.
-- ============================================================================
CREATE OR REPLACE FUNCTION fetch_inventory(
    p_tenant_id    uuid,
    p_product_id   bigint DEFAULT NULL,
    p_warehouse_id bigint DEFAULT NULL
)
RETURNS TABLE (
    tenant_id     uuid,
    product_id    bigint,
    warehouse_id  bigint,
    on_hand_qty   numeric,
    reserved_qty  numeric,
    allocated_qty numeric,
    pending_qty   numeric,
    atp_qty       numeric,
    computed_at   timestamptz
)
LANGUAGE sql
AS $$
    SELECT *
      FROM mv_atp
     WHERE tenant_id = p_tenant_id
       AND (p_product_id   IS NULL OR product_id   = p_product_id)
       AND (p_warehouse_id IS NULL OR warehouse_id = p_warehouse_id);
$$;

-- ============================================================================
-- fetch_pending_orders — SFDC order lines not yet matched by FOP.
-- ============================================================================
CREATE OR REPLACE FUNCTION fetch_pending_orders(p_tenant_id uuid)
RETURNS TABLE (
    sfdc_order_id text,
    line_no       int,
    product_id    bigint,
    warehouse_id  bigint,
    qty           numeric,
    line_state    text,
    created_at    timestamptz
)
LANGUAGE sql
AS $$
    SELECT sfdc_order_id, line_no, product_id, warehouse_id, qty, line_state, created_at
      FROM sfdc_order_line
     WHERE tenant_id = p_tenant_id
       AND line_state IN ('open', 'synced')
       AND fop_synced_at IS NULL
     ORDER BY created_at;
$$;
