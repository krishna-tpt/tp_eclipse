-- V8__races_state_machine_helpers.sql
-- Code-review findings (block + major from Part 2):
--   1. f_reconcile_orders racy under concurrency — add FOR UPDATE SKIP LOCKED
--   2. f_inv_transaction_tenant_check only on INSERT — extend to UPDATE
--   3. stock_balance never reflects opening_balance — add trigger
--   4. f_purge_staging unbounded DELETE — batched loop
--   5. load_opening_balance not using notify_outbox dedup — refactor
--   6. upsert_order raises on unknown_tenant — return status row + state machine
--   7. pipeline_config_int returns NULL silently — add default-overload

-- ============================================================================
-- 1. f_reconcile_orders — FOR UPDATE SKIP LOCKED prevents double-reconcile
-- ============================================================================
CREATE OR REPLACE FUNCTION f_reconcile_orders()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
    v_matched_id text;
    v_matched_no int;
BEGIN
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
     LIMIT 1
     FOR UPDATE SKIP LOCKED;

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

-- ============================================================================
-- 2. inv_transaction tenant-check on UPDATE too
-- ============================================================================
DROP TRIGGER IF EXISTS trg_inv_txn_tenant_check ON inv_transaction;
CREATE TRIGGER trg_inv_txn_tenant_check
    BEFORE INSERT OR UPDATE ON inv_transaction
    FOR EACH ROW EXECUTE FUNCTION f_inv_transaction_tenant_check();

-- ============================================================================
-- 3. opening_balance INSERT/UPDATE/DELETE → stock_balance keeps in sync.
--    Closes the "stock_balance doesn't include opening_balance" gap.
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
            tenant_id, product_id, warehouse_id, lot_id, on_hand_qty, uom_id, last_updated_at
        ) VALUES (
            NEW.tenant_id, NEW.product_id, NEW.warehouse_id,
            COALESCE(NEW.lot_id, 0), v_delta, NEW.uom_id, now()
        )
        ON CONFLICT (tenant_id, product_id, warehouse_id, lot_id) DO UPDATE
            SET on_hand_qty     = stock_balance.on_hand_qty + EXCLUDED.on_hand_qty,
                last_updated_at = now();
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        v_delta := NEW.qty - OLD.qty;
        IF v_delta <> 0 THEN
            UPDATE stock_balance
               SET on_hand_qty = on_hand_qty + v_delta,
                   last_updated_at = now()
             WHERE tenant_id    = NEW.tenant_id
               AND product_id   = NEW.product_id
               AND warehouse_id = NEW.warehouse_id
               AND lot_id       = COALESCE(NEW.lot_id, 0);
        END IF;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE stock_balance
           SET on_hand_qty = on_hand_qty - OLD.qty,
               last_updated_at = now()
         WHERE tenant_id    = OLD.tenant_id
           AND product_id   = OLD.product_id
           AND warehouse_id = OLD.warehouse_id
           AND lot_id       = COALESCE(OLD.lot_id, 0);
        RETURN OLD;
    END IF;
    RETURN NULL;
END$$;

DROP TRIGGER IF EXISTS trg_opening_balance_stock_balance ON opening_balance;
CREATE TRIGGER trg_opening_balance_stock_balance
    AFTER INSERT OR UPDATE OR DELETE ON opening_balance
    FOR EACH ROW EXECUTE FUNCTION f_stock_balance_opening_apply();

-- Reconcile existing rows so stock_balance reflects current opening_balance state.
-- For any row already in stock_balance, we add opening_balance.qty to its on_hand_qty.
-- For tenants whose stock_balance row doesn't yet exist for an opening_balance key,
-- create it.
WITH ob_agg AS (
    SELECT tenant_id, product_id, warehouse_id, COALESCE(lot_id, 0) AS lot_id,
           SUM(qty) AS sum_qty, min(uom_id) AS uom_id
      FROM opening_balance
     GROUP BY tenant_id, product_id, warehouse_id, COALESCE(lot_id, 0)
)
INSERT INTO stock_balance (tenant_id, product_id, warehouse_id, lot_id, on_hand_qty, uom_id)
SELECT tenant_id, product_id, warehouse_id, lot_id, sum_qty, uom_id
  FROM ob_agg
ON CONFLICT (tenant_id, product_id, warehouse_id, lot_id) DO UPDATE
   SET on_hand_qty = stock_balance.on_hand_qty + EXCLUDED.on_hand_qty,
       last_updated_at = now();

-- ============================================================================
-- 4. f_purge_staging — batched DELETE loop to avoid table-level lock on large tables
-- ============================================================================
CREATE OR REPLACE FUNCTION f_purge_staging()
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
    v_txn_days     int := pipeline_config_int('txn_inbox_purge_days');
    v_order_days   int := pipeline_config_int('order_inbox_purge_days');
    v_reject_days  int := pipeline_config_int('reject_purge_days');
    v_batch_size   int := 5000;
    v_total_txn    bigint := 0;
    v_total_order  bigint := 0;
    v_total_reject bigint := 0;
    v_n bigint;
BEGIN
    LOOP
        WITH del AS (
            DELETE FROM staging.txn_inbox
             WHERE ctid = ANY (ARRAY(
                 SELECT ctid FROM staging.txn_inbox
                  WHERE status = 'processed'
                    AND received_at < now() - (v_txn_days || ' days')::interval
                  LIMIT v_batch_size))
            RETURNING 1
        )
        SELECT count(*) INTO v_n FROM del;
        v_total_txn := v_total_txn + v_n;
        EXIT WHEN v_n = 0;
    END LOOP;

    LOOP
        WITH del AS (
            DELETE FROM staging.order_inbox
             WHERE ctid = ANY (ARRAY(
                 SELECT ctid FROM staging.order_inbox
                  WHERE status = 'processed'
                    AND received_at < now() - (v_order_days || ' days')::interval
                  LIMIT v_batch_size))
            RETURNING 1
        )
        SELECT count(*) INTO v_n FROM del;
        v_total_order := v_total_order + v_n;
        EXIT WHEN v_n = 0;
    END LOOP;

    LOOP
        WITH del AS (
            DELETE FROM staging.ob_reject
             WHERE ctid = ANY (ARRAY(
                 SELECT ctid FROM staging.ob_reject
                  WHERE rejected_at < now() - (v_reject_days || ' days')::interval
                  LIMIT v_batch_size))
            RETURNING 1
        )
        SELECT count(*) INTO v_n FROM del;
        v_total_reject := v_total_reject + v_n;
        EXIT WHEN v_n = 0;
    END LOOP;

    PERFORM notify_outbox(
        NULL, 'info', 'f_purge_staging',
        format('purged txn_inbox=%s order_inbox=%s ob_reject=%s',
               v_total_txn, v_total_order, v_total_reject),
        jsonb_build_object(
            'txn_inbox_deleted',   v_total_txn,
            'order_inbox_deleted', v_total_order,
            'ob_reject_deleted',   v_total_reject),
        NULL);
END$$;

-- ============================================================================
-- 5. load_opening_balance — emit via notify_outbox() (dedup-aware)
-- ============================================================================
CREATE OR REPLACE FUNCTION load_opening_balance(p_batch_id bigint)
RETURNS TABLE (accepted_count int, rejected_count int)
LANGUAGE plpgsql
AS $f$
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

    WITH resolved AS (
        SELECT
            l.ob_load_id, l.line_no,
            t.tenant_id, p.product_id, w.warehouse_id, lt.lot_id, u.uom_id,
            l.tenant_code, l.product_code, l.warehouse_code, l.lot_code, l.uom_code,
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

-- ============================================================================
-- 6. upsert_order — return status row + state-machine
--    Old signature kept (returns same TABLE shape); new column `reason`.
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
    v_existing_state text;
    v_requested_state text;
    v_effective_state text;
    v_order_state  text;
    v_existing_order_state text;
BEGIN
    v_order_id := p_payload->>'sfdc_order_id';
    v_lines    := COALESCE(p_payload->'lines', '[]'::jsonb);
    v_order_state := COALESCE(p_payload->>'order_state', 'open');

    SELECT t.tenant_id INTO v_tenant_id FROM tenant t WHERE t.tenant_code = p_payload->>'tenant_code';
    IF v_tenant_id IS NULL THEN
        RETURN QUERY SELECT v_order_id, 0, 0, 0, false, 'unknown_reference'::text;
        RETURN;
    END IF;

    SELECT order_state INTO v_existing_order_state FROM sfdc_order WHERE sfdc_order_id = v_order_id;

    -- order_state machine — allowed transitions: open → synced → closed; any → cancelled
    IF v_existing_order_state IS NOT NULL
       AND v_existing_order_state <> v_order_state
       AND NOT (
           (v_existing_order_state = 'open'     AND v_order_state IN ('synced','cancelled'))   OR
           (v_existing_order_state = 'synced'   AND v_order_state IN ('closed','cancelled'))   OR
           (v_existing_order_state = 'closed'   AND v_order_state = 'cancelled')               OR
           (v_existing_order_state = 'cancelled' AND v_order_state = 'cancelled')
       )
    THEN
        RETURN QUERY SELECT v_order_id, 0, 0, 0, false,
            format('illegal_order_state_transition: %s -> %s',
                   v_existing_order_state, v_order_state)::text;
        RETURN;
    END IF;

    INSERT INTO sfdc_order (sfdc_order_id, tenant_id, customer_id, order_state, payload)
    VALUES (
        v_order_id, v_tenant_id,
        p_payload->>'customer_id',
        v_order_state,
        p_payload)
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

        SELECT line_state, true INTO v_existing_state, v_existed
          FROM sfdc_order_line sol
         WHERE sol.sfdc_order_id = v_order_id AND sol.line_no = (v_line->>'line_no')::int;
        v_existed := COALESCE(v_existed, false);

        v_requested_state := COALESCE(v_line->>'line_state', 'open');

        -- New lines must start in 'open' (V6). Allowed line_state transitions on UPDATE:
        --   open → synced → closed; any → cancelled
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
                   (v_existing_state = 'open'     AND v_effective_state IN ('synced','cancelled'))   OR
                   (v_existing_state = 'synced'   AND v_effective_state IN ('closed','cancelled'))   OR
                   (v_existing_state = 'closed'   AND v_effective_state = 'cancelled')               OR
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
            sfdc_order_id, line_no, tenant_id, product_id, warehouse_id, qty, uom_id, line_state, payload
        ) VALUES (
            v_order_id, (v_line->>'line_no')::int, v_tenant_id,
            v_product_id, v_warehouse_id,
            (v_line->>'qty')::numeric, v_uom_id,
            v_effective_state,
            v_line)
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

    RETURN QUERY SELECT v_order_id, v_inserted, v_updated, v_cancelled, true, 'ok'::text;
END$$;

-- Re-grant after DROP+CREATE
GRANT EXECUTE ON FUNCTION upsert_order(jsonb) TO inventoryledger_writer;

-- ============================================================================
-- 7. pipeline_config_int default-overload — never returns NULL silently
-- ============================================================================
CREATE OR REPLACE FUNCTION pipeline_config_int(p_key text, p_default int)
RETURNS int
LANGUAGE sql STABLE
AS $$
    SELECT COALESCE(
        (SELECT value::int FROM pipeline_config WHERE key = p_key),
        p_default
    );
$$;
GRANT EXECUTE ON FUNCTION pipeline_config_int(text, int)
    TO inventoryledger_app, inventoryledger_writer, inventoryledger_reader;
