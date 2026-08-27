-- ============================================================================
-- alter_04_audit_event_log.sql
-- ============================================================================
-- Adds the observability event log + derived views that present every
-- observable system event in a single consistent shape for Grafana.
--
-- Design:
--   - audit.event_log (TABLE) — holds events with no equivalent elsewhere:
--       file.picked, file.parsed, file.failed, atp.queried, daemon.heartbeat
--   - audit.v_* (VIEWS)       — expose existing tables in the same shape so
--                               we don't duplicate rows that already exist
--   - audit.v_all_events      — UNION of the above; the single Grafana endpoint
--   - processed.fetch_inventory_json_observed — caller-identifying wrapper
--                               around fetch_inventory_json that logs each call
--
-- Idempotent: every CREATE uses IF NOT EXISTS / OR REPLACE. Safe to re-run.
-- ============================================================================

BEGIN;

CREATE SCHEMA IF NOT EXISTS audit;

-- ============================================================================
-- 1. Table — the only thing we write to
-- ============================================================================
CREATE TABLE IF NOT EXISTS audit.event_log (
    event_id        BIGSERIAL    PRIMARY KEY,
    at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    event_type      TEXT         NOT NULL,
    severity        TEXT         NOT NULL DEFAULT 'info'
                                 CHECK (severity IN ('debug','info','warn','error')),
    source          TEXT         NOT NULL,
    tenant_id       TEXT,
    correlation_id  TEXT,
    status          TEXT,
    ref_id          TEXT,
    error_code      TEXT,
    error_msg       TEXT,
    latency_ms      INTEGER,
    rows_affected   INTEGER,
    bytes           BIGINT,
    payload         JSONB        NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX IF NOT EXISTS ix_event_log_at
    ON audit.event_log (at DESC);
CREATE INDEX IF NOT EXISTS ix_event_log_type_at
    ON audit.event_log (event_type, at DESC);
CREATE INDEX IF NOT EXISTS ix_event_log_tenant_at
    ON audit.event_log (tenant_id, at DESC)
 WHERE tenant_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS ix_event_log_corr
    ON audit.event_log (correlation_id)
 WHERE correlation_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS ix_event_log_alerts
    ON audit.event_log (severity, at DESC)
 WHERE severity IN ('warn','error');

COMMENT ON TABLE audit.event_log IS
    'Append-only observability event stream. Written by the Java service '
    'and by the fetch_inventory_json_observed wrapper. Derived events from '
    'existing tables live in audit.v_* views and unify in audit.v_all_events.';

-- ============================================================================
-- 2. Derived views — same column shape as event_log, no rows written
-- ============================================================================

-- 2a. File batches → file.parsed (terminal state). Failures pre-staging are
--     never written here because they never reach stocklevel_batch; the
--     Java daemon writes file.failed into audit.event_log instead.
CREATE OR REPLACE VIEW audit.v_file_batch_events AS
SELECT
    'fb-' || batch_id::TEXT                                       AS event_id,
    started_at                                                    AS at,
    'file.parsed'                                                 AS event_type,
    CASE WHEN status = 'failed' THEN 'error' ELSE 'info' END      AS severity,
    'files'                                                       AS source,
    NULL::TEXT                                                    AS tenant_id,
    file_name                                                     AS correlation_id,
    status,
    file_name                                                     AS ref_id,
    NULL::TEXT                                                    AS error_code,
    NULL::TEXT                                                    AS error_msg,
    (EXTRACT(EPOCH FROM (completed_at - started_at)) * 1000)::INT AS latency_ms,
    (rows_accepted + rows_rejected)                               AS rows_affected,
    NULL::BIGINT                                                  AS bytes,
    jsonb_build_object(
        'rows_accepted', rows_accepted,
        'rows_rejected', rows_rejected
    )                                                             AS payload
  FROM staging.stocklevel_batch
 WHERE completed_at IS NOT NULL;

-- 2b. Order inbox — emits TWO logical events per inbox row:
--     a) 'order.received' at received_at — always
--     b) 'promotion.success' / 'promotion.failed' / 'order.superseded' at
--        processed_at — only when the auto-promote trigger has run
--     This is intentional: receipt and promotion are distinct observables
--     on the same row, separated by trigger latency. Grafana wants to plot
--     both rates independently.
CREATE OR REPLACE VIEW audit.v_order_events AS
    SELECT
        'ord-r-' || inbox_id::TEXT                                AS event_id,
        received_at                                               AS at,
        'order.received'                                          AS event_type,
        'info'                                                    AS severity,
        'sfdc'                                                    AS source,
        tenant_code                                               AS tenant_id,
        sfdc_order_id                                             AS correlation_id,
        'received'                                                AS status,
        sfdc_order_id                                             AS ref_id,
        NULL::TEXT                                                AS error_code,
        NULL::TEXT                                                AS error_msg,
        NULL::INT                                                 AS latency_ms,
        NULL::INT                                                 AS rows_affected,
        octet_length(payload::TEXT)::BIGINT                       AS bytes,
        jsonb_build_object('inbox_id', inbox_id)                  AS payload
      FROM staging.order_inbox
UNION ALL
    SELECT
        'ord-p-' || inbox_id::TEXT,
        processed_at,
        CASE WHEN status = 'processed'  THEN 'promotion.success'
             WHEN status = 'rejected'   THEN 'promotion.failed'
             WHEN status = 'superseded' THEN 'order.superseded'
             ELSE 'order.unknown' END,
        CASE WHEN status = 'rejected' THEN 'warn' ELSE 'info' END,
        'sfdc',
        tenant_code,
        sfdc_order_id,
        status,
        sfdc_order_id,
        NULL::TEXT,
        reject_reason,
        (EXTRACT(EPOCH FROM (processed_at - received_at)) * 1000)::INT,
        NULL::INT,
        NULL::BIGINT,
        jsonb_build_object('inbox_id', inbox_id)
      FROM staging.order_inbox
     WHERE processed_at IS NOT NULL;

-- 2c. Transaction inbox — same two-event pattern as v_order_events above.
CREATE OR REPLACE VIEW audit.v_txn_events AS
    SELECT
        'tx-r-' || inbox_id::TEXT                                 AS event_id,
        received_at                                               AS at,
        'txn.received'                                            AS event_type,
        'info'                                                    AS severity,
        'fop'                                                     AS source,
        tenant_code                                               AS tenant_id,
        external_txn_id                                           AS correlation_id,
        'received'                                                AS status,
        external_txn_id                                           AS ref_id,
        NULL::TEXT                                                AS error_code,
        NULL::TEXT                                                AS error_msg,
        NULL::INT                                                 AS latency_ms,
        NULL::INT                                                 AS rows_affected,
        octet_length(payload::TEXT)::BIGINT                       AS bytes,
        jsonb_build_object('inbox_id', inbox_id)                  AS payload
      FROM staging.txn_inbox
UNION ALL
    SELECT
        'tx-p-' || inbox_id::TEXT,
        processed_at,
        CASE WHEN status = 'processed'  THEN 'promotion.success'
             WHEN status = 'rejected'   THEN 'promotion.failed'
             WHEN status = 'superseded' THEN 'txn.superseded'
             ELSE 'txn.unknown' END,
        CASE WHEN status = 'rejected' THEN 'warn' ELSE 'info' END,
        'fop',
        tenant_code,
        external_txn_id,
        status,
        external_txn_id,
        NULL::TEXT,
        reject_reason,
        (EXTRACT(EPOCH FROM (processed_at - received_at)) * 1000)::INT,
        NULL::INT,
        NULL::BIGINT,
        jsonb_build_object('inbox_id', inbox_id)
      FROM staging.txn_inbox
     WHERE processed_at IS NOT NULL;

-- 2d. Stock changes — chronological feed of every on_hand / reserved move.
--     UNIONs three sources:
--       - inv_transaction       (shipments, returns, receipts, adjustments)
--       - opening_balance       (file-driven overwrites)
--       - sfdc_order_line       (reservations / cancellations / state moves)
CREATE OR REPLACE VIEW audit.v_stock_change_events AS
    SELECT
        'itx-' || inv_transaction_id::TEXT                        AS event_id,
        posted_at                                                 AS at,
        'stock.changed'                                           AS event_type,
        'info'                                                    AS severity,
        'fop'                                                     AS source,
        tenant_id,
        external_txn_id                                           AS correlation_id,
        'applied'                                                 AS status,
        external_txn_id                                           AS ref_id,
        NULL::TEXT                                                AS error_code,
        NULL::TEXT                                                AS error_msg,
        NULL::INT                                                 AS latency_ms,
        NULL::INT                                                 AS rows_affected,
        NULL::BIGINT                                              AS bytes,
        jsonb_build_object(
            'product_code',   product_code,
            'warehouse_code', warehouse_code,
            'subinventory',   subinventory,
            'stock_status',   stock_status,
            'signed_qty',     signed_qty,
            'txn_type',       txn_type,
            'cause',          'transaction'
        )                                                         AS payload
      FROM processed.inv_transaction
UNION ALL
    SELECT
        'ob-' || opening_balance_id::TEXT,
        as_of_date::TIMESTAMPTZ,
        'stock.changed',
        'info',
        'files',
        tenant_id,
        source_file,
        'applied',
        source_file,
        NULL::TEXT, NULL::TEXT, NULL::INT, NULL::INT, NULL::BIGINT,
        jsonb_build_object(
            'product_code',   product_code,
            'warehouse_code', warehouse_code,
            'subinventory',   subinventory,
            'stock_status',   stock_status,
            'qty',            qty,
            'cause',          'opening_balance'
        )
      FROM processed.opening_balance
UNION ALL
    -- sfdc_order_line has a composite PK (sfdc_order_id, line_no) — synthesize
    -- an event_id from those two columns rather than a non-existent surrogate.
    SELECT
        'ol-' || sfdc_order_id || ':' || line_no::TEXT,
        created_at,
        'stock.changed',
        'info',
        'sfdc',
        tenant_id,
        COALESCE(sfdc_line_id, sfdc_order_id || ':' || line_no::TEXT),
        line_state,
        COALESCE(sfdc_line_id, sfdc_order_id || ':' || line_no::TEXT),
        NULL::TEXT, NULL::TEXT, NULL::INT, NULL::INT, NULL::BIGINT,
        jsonb_build_object(
            'sfdc_order_id',  sfdc_order_id,
            'line_no',        line_no,
            'product_code',   product_code,
            'warehouse_code', warehouse_code,
            'qty',            qty,
            'state',          line_state,
            'cause',          'order_line'
        )
      FROM processed.sfdc_order_line;

-- 2e. Notifications — already an outbox; expose in event shape.
CREATE OR REPLACE VIEW audit.v_notify_events AS
SELECT
    'nx-' || outbox_id::TEXT                                      AS event_id,
    created_at                                                    AS at,
    'notify.emitted'                                              AS event_type,
    severity,
    source,
    tenant_id,
    dedup_key                                                     AS correlation_id,
    status,
    dedup_key                                                     AS ref_id,
    NULL::TEXT                                                    AS error_code,
    message                                                       AS error_msg,
    NULL::INT                                                     AS latency_ms,
    repeat_count                                                  AS rows_affected,
    NULL::BIGINT                                                  AS bytes,
    payload
  FROM processed.notification_outbox;

-- ============================================================================
-- 3. Master union — single endpoint for Grafana
-- ============================================================================
CREATE OR REPLACE VIEW audit.v_all_events AS
    SELECT event_id::TEXT, at, event_type, severity, source, tenant_id, correlation_id,
           status, ref_id, error_code, error_msg, latency_ms, rows_affected, bytes, payload
      FROM audit.event_log
UNION ALL SELECT * FROM audit.v_file_batch_events
UNION ALL SELECT * FROM audit.v_order_events
UNION ALL SELECT * FROM audit.v_txn_events
UNION ALL SELECT * FROM audit.v_stock_change_events
UNION ALL SELECT * FROM audit.v_notify_events;

COMMENT ON VIEW audit.v_all_events IS
    'Single observability endpoint. Grafana points its Postgres datasource '
    'here and gets every event in one shape. Backed by audit.event_log plus '
    'derived views over inbox / outbox / transaction tables.';

-- ============================================================================
-- 4. ATP-call logger — wraps fetch_inventory_json with caller-identifying logs
-- ============================================================================
-- New callers (e.g., the SFDC integration tier) should use this variant; the
-- original processed.fetch_inventory_json stays available for legacy callers.
-- An INSERT failure in the log path NEVER propagates to the caller — the read
-- result is more important than the audit row.
-- ============================================================================
CREATE OR REPLACE FUNCTION processed.fetch_inventory_json_observed(
    p_tenant_code     TEXT,
    p_warehouse_code  TEXT DEFAULT NULL,
    p_subinventory    TEXT DEFAULT NULL,
    p_product_code    TEXT DEFAULT NULL,
    p_caller          TEXT DEFAULT NULL
)
RETURNS SETOF JSON
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, audit, public, pg_catalog
AS $f$
DECLARE
    v_started TIMESTAMPTZ := clock_timestamp();
    v_rows    INTEGER     := 0;
    v_row     JSON;
BEGIN
    FOR v_row IN
        SELECT * FROM processed.fetch_inventory_json(
            p_tenant_code, p_warehouse_code, p_subinventory, p_product_code)
    LOOP
        v_rows := v_rows + 1;
        RETURN NEXT v_row;
    END LOOP;

    BEGIN
        INSERT INTO audit.event_log
            (event_type, severity, source, tenant_id, ref_id,
             latency_ms, rows_affected, payload)
        VALUES
            ('atp.queried', 'info', 'atp', p_tenant_code,
             COALESCE(p_caller, 'unknown'),
             (EXTRACT(EPOCH FROM clock_timestamp() - v_started) * 1000)::INT,
             v_rows,
             jsonb_build_object(
                 'warehouse_code', p_warehouse_code,
                 'subinventory',   p_subinventory,
                 'product_code',   p_product_code,
                 'caller',         COALESCE(p_caller, 'unknown')
             ));
    EXCEPTION WHEN OTHERS THEN
        -- Audit write failure must not affect the read response.
        RAISE WARNING 'audit log write failed for atp.queried: %', SQLERRM;
    END;

    RETURN;
END;
$f$;

COMMENT ON FUNCTION processed.fetch_inventory_json_observed IS
    'ATP read wrapper that logs every call to audit.event_log. Caller passes '
    'p_caller (their service id) so the log identifies who is asking. '
    'Audit failures are swallowed — never block the read path.';

COMMIT;
