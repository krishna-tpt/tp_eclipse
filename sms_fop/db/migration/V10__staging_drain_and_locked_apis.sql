-- V10__staging_drain_and_locked_apis.sql
--
-- Locks the public API to the intended shape:
--   * External writers (inventoryledger_writer role) push to staging.txn_inbox
--     and staging.order_inbox only. They no longer have EXECUTE on
--     post_transaction / upsert_order directly.
--   * Two drain functions (process_txn_inbox, process_order_inbox) pull rows
--     from staging in bounded batches and apply them via the existing
--     SECURITY DEFINER post_transaction / upsert_order. Drain is run by
--     pg_cron; interval and batch size live in pipeline_config.
--   * Read side: external readers (inventoryledger_reader role) lose direct
--     SELECT on mv_atp / vw_atp_* and gain EXECUTE on fetch_inventory. One
--     function = one exposed read surface.
--
-- Rationale:
--   * Async buffering: external systems can push at their own pace, drain
--     runs on a predictable cadence and applies back-pressure naturally.
--   * Single chokepoint for validation: every external write traverses the
--     same post_transaction / upsert_order paths.
--   * Single read API: BI tools and other consumers can be re-pointed at
--     fetch_inventory; the underlying view shape can evolve without
--     breaking consumers.

-- ============================================================================
-- 1. Configurable drain parameters (no hard-coded batch sizes / intervals)
-- ============================================================================
INSERT INTO pipeline_config (key, value, description) VALUES
    ('txn_inbox_batch_size',
     '500',
     'process_txn_inbox claims at most this many rows per invocation. Caps lock-holding time when the inbox is backlogged.'),
    ('order_inbox_batch_size',
     '200',
     'process_order_inbox claims at most this many rows per invocation. Orders are heavier than transactions (multi-line payloads); a smaller cap keeps drain latency predictable.')
ON CONFLICT (key) DO NOTHING;

-- ============================================================================
-- 2. process_txn_inbox — staging.txn_inbox → post_transaction
--
-- Claims pending rows with FOR UPDATE SKIP LOCKED so multiple drains never
-- pick the same row. For each row:
--   * call post_transaction(payload); if accepted → status='processed',
--     else status='rejected' with error_detail = reason.
--   * Any unexpected exception is caught per-row so one bad row never
--     stalls the batch.
-- Returns (processed, rejected) so cron jobs / monitors can log it.
-- ============================================================================
CREATE OR REPLACE FUNCTION process_txn_inbox(p_batch_size int DEFAULT NULL)
RETURNS TABLE (processed int, rejected int)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, staging
AS $$
DECLARE
    v_batch    int := COALESCE(p_batch_size, pipeline_config_int('txn_inbox_batch_size'));
    v_row      staging.txn_inbox%ROWTYPE;
    v_result   record;
    v_processed int := 0;
    v_rejected  int := 0;
BEGIN
    FOR v_row IN
        SELECT * FROM staging.txn_inbox
         WHERE status = 'pending'
         ORDER BY received_at
         LIMIT v_batch
         FOR UPDATE SKIP LOCKED
    LOOP
        BEGIN
            SELECT * INTO v_result FROM post_transaction(v_row.payload);
            IF v_result.accepted THEN
                UPDATE staging.txn_inbox
                   SET status        = 'processed',
                       processed_at  = now(),
                       error_detail  = NULL
                 WHERE txn_inbox_id = v_row.txn_inbox_id;
                v_processed := v_processed + 1;
            ELSE
                UPDATE staging.txn_inbox
                   SET status        = 'rejected',
                       processed_at  = now(),
                       error_detail  = v_result.reason
                 WHERE txn_inbox_id = v_row.txn_inbox_id;
                v_rejected := v_rejected + 1;
            END IF;
        EXCEPTION WHEN OTHERS THEN
            -- Per-row isolation: a bad payload that bubbles up an exception
            -- (cast failure, etc.) is logged and the row marked rejected,
            -- but the rest of the batch continues.
            UPDATE staging.txn_inbox
               SET status       = 'rejected',
                   processed_at = now(),
                   error_detail = 'exception: ' || SQLERRM
             WHERE txn_inbox_id = v_row.txn_inbox_id;
            v_rejected := v_rejected + 1;
        END;
    END LOOP;

    RETURN QUERY SELECT v_processed, v_rejected;
END$$;

-- ============================================================================
-- 3. process_order_inbox — staging.order_inbox → upsert_order
-- ============================================================================
CREATE OR REPLACE FUNCTION process_order_inbox(p_batch_size int DEFAULT NULL)
RETURNS TABLE (processed int, rejected int)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, staging
AS $$
DECLARE
    v_batch    int := COALESCE(p_batch_size, pipeline_config_int('order_inbox_batch_size'));
    v_row      staging.order_inbox%ROWTYPE;
    v_result   record;
    v_processed int := 0;
    v_rejected  int := 0;
BEGIN
    FOR v_row IN
        SELECT * FROM staging.order_inbox
         WHERE status = 'pending'
         ORDER BY received_at
         LIMIT v_batch
         FOR UPDATE SKIP LOCKED
    LOOP
        BEGIN
            SELECT * INTO v_result FROM upsert_order(v_row.payload);
            IF v_result.accepted THEN
                UPDATE staging.order_inbox
                   SET status        = 'processed',
                       processed_at  = now(),
                       error_detail  = NULL
                 WHERE order_inbox_id = v_row.order_inbox_id;
                v_processed := v_processed + 1;
            ELSE
                UPDATE staging.order_inbox
                   SET status        = 'rejected',
                       processed_at  = now(),
                       error_detail  = v_result.reason
                 WHERE order_inbox_id = v_row.order_inbox_id;
                v_rejected := v_rejected + 1;
            END IF;
        EXCEPTION WHEN OTHERS THEN
            UPDATE staging.order_inbox
               SET status       = 'rejected',
                   processed_at = now(),
                   error_detail = 'exception: ' || SQLERRM
             WHERE order_inbox_id = v_row.order_inbox_id;
            v_rejected := v_rejected + 1;
        END;
    END LOOP;

    RETURN QUERY SELECT v_processed, v_rejected;
END$$;

-- ============================================================================
-- 4. pg_cron schedules for the drains
-- ============================================================================
DO $$
DECLARE
    j_txn   text := 'inventoryledger_drain_txn_inbox';
    j_order text := 'inventoryledger_drain_order_inbox';
BEGIN
    PERFORM cron.unschedule(j_txn)   FROM cron.job WHERE jobname = j_txn;
    PERFORM cron.unschedule(j_order) FROM cron.job WHERE jobname = j_order;

    -- Every minute; SKIP LOCKED makes overlapping runs safe.
    PERFORM cron.schedule(j_txn,   '* * * * *',
        $cmd$ SELECT process_txn_inbox(); $cmd$);
    PERFORM cron.schedule(j_order, '* * * * *',
        $cmd$ SELECT process_order_inbox(); $cmd$);
END$$;

-- ============================================================================
-- 5. Tighten the public API surface
--
-- Writers can NO LONGER call post_transaction / upsert_order directly. They
-- become internal helpers reachable only through the inbox drain path. App
-- role (our daemon) keeps EXECUTE so it can invoke process_*_inbox.
-- ============================================================================
REVOKE EXECUTE ON FUNCTION post_transaction(jsonb)        FROM inventoryledger_writer;
REVOKE EXECUTE ON FUNCTION post_transaction_bulk(jsonb[]) FROM inventoryledger_writer;
REVOKE EXECUTE ON FUNCTION upsert_order(jsonb)            FROM inventoryledger_writer;

-- Lock down direct view selects for readers; expose fetch_inventory instead.
REVOKE SELECT ON mv_atp,
                 vw_atp_by_product,
                 vw_atp_by_warehouse
  FROM inventoryledger_reader;

-- Make sure the function lives in a place readers can call.
GRANT EXECUTE ON FUNCTION fetch_inventory(uuid, bigint, bigint)
   TO inventoryledger_reader;

-- App role can call the drains (e.g. manual catch-up or daemon-driven path).
GRANT EXECUTE ON FUNCTION process_txn_inbox(int)   TO inventoryledger_app;
GRANT EXECUTE ON FUNCTION process_order_inbox(int) TO inventoryledger_app;

-- pg_cron runs as superuser, so its scheduled SELECTs don't need grants.
-- post_transaction / upsert_order remain SECURITY DEFINER (per V9), so the
-- drain functions (also SECURITY DEFINER) can call them regardless of
-- caller's role.

-- ============================================================================
-- 6. SECURITY DEFINER hardening for the new drain functions
-- ============================================================================
ALTER FUNCTION process_txn_inbox(int)   SET search_path = public, staging, pg_temp;
ALTER FUNCTION process_order_inbox(int) SET search_path = public, staging, pg_temp;

REVOKE EXECUTE ON FUNCTION process_txn_inbox(int)   FROM PUBLIC;
REVOKE EXECUTE ON FUNCTION process_order_inbox(int) FROM PUBLIC;
