-- V3__schedules_partitions_grants.sql
-- pg_partman partition setup, pg_cron schedules, helper jobs, BI views, role grants.
--
-- No hard-coded business values: all retention, sampling, and purge windows
-- live in pipeline_config (this migration seeds defaults; ops edits the table
-- to change behavior — no code change required).
--
-- Cron expressions ARE stored in pg_cron's own cron.job table after this
-- migration runs; ops may alter them live via cron.alter_job() without
-- redeploying the JAR. The values in cron.schedule() calls below are initial
-- defaults only.
--
-- Required extensions (created by privileged bootstrap, not here): pg_partman, pg_cron.

-- ============================================================================
-- pipeline_config — business parameters as data, not code
-- ============================================================================
CREATE TABLE pipeline_config (
    key         text PRIMARY KEY,
    value       text NOT NULL,
    description text NOT NULL,
    updated_at  timestamptz NOT NULL DEFAULT now()
);

INSERT INTO pipeline_config (key, value, description) VALUES
    ('archive_retention_days',
     '90',
     'audit_log partitions older than this are detached and moved to archive schema by pg_partman maintenance.'),
    ('inv_txn_retention',
     '2 years',
     'inv_transaction partitions older than this are detached and moved to archive schema. Stored as a Postgres interval string (e.g. "2 years", "18 months").'),
    ('archive_keep_years',
     '7',
     'How long detached partitions are retained in archive schema before being dropped. Drop step is manual / separate review.'),
    ('txn_inbox_purge_days',
     '7',
     'staging.txn_inbox rows in status=processed older than this are deleted by f_purge_staging.'),
    ('order_inbox_purge_days',
     '7',
     'staging.order_inbox rows in status=processed older than this are deleted by f_purge_staging.'),
    ('reject_purge_days',
     '30',
     'staging.ob_reject rows older than this are deleted by f_purge_staging.'),
    ('drift_sample_pct',
     '5',
     'Percentage of (tenant, product, warehouse) combinations sampled nightly by f_drift_detect. 1-100.'),
    ('drift_alert_severity',
     'warn',
     'Severity tag applied to notification_outbox rows when drift is detected.')
ON CONFLICT (key) DO NOTHING;

CREATE OR REPLACE FUNCTION pipeline_config_int(p_key text)
RETURNS int
LANGUAGE sql STABLE
AS $$
    SELECT value::int FROM pipeline_config WHERE key = p_key;
$$;

CREATE OR REPLACE FUNCTION pipeline_config_text(p_key text)
RETURNS text
LANGUAGE sql STABLE
AS $$
    SELECT value FROM pipeline_config WHERE key = p_key;
$$;

-- ============================================================================
-- pg_partman setup
-- Configures monthly partitioning + automatic archival on inv_transaction
-- and audit.audit_log. retention is read from pipeline_config.
-- ============================================================================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM partman.part_config WHERE parent_table = 'public.inv_transaction'
    ) THEN
        -- pg_partman 5.x creates its own default partition; the V1 stub
        -- (inv_transaction_default) is detached + dropped first to avoid the
        -- "already a partition" collision.
        IF EXISTS (
            SELECT 1 FROM pg_class c
              JOIN pg_inherits i ON i.inhrelid = c.oid
             WHERE c.relname = 'inv_transaction_default'
        ) THEN
            ALTER TABLE public.inv_transaction DETACH PARTITION public.inv_transaction_default;
            DROP TABLE public.inv_transaction_default;
        END IF;
        -- Annual partitions: expected volume ~3M rows/yr is well below the
        -- "partition by month" threshold (~50-100M rows). Yearly partitions
        -- keep the catalog small and reduce planner overhead. p_premake=1
        -- creates next year's partition; cron maintenance rolls forward.
        PERFORM partman.create_parent(
            p_parent_table => 'public.inv_transaction',
            p_control      => 'posted_at',
            p_type         => 'range',
            p_interval     => '1 year',
            p_premake      => 1
        );
    END IF;

    -- Retention for inv_transaction is read from pipeline_config.inv_txn_retention.
    -- With yearly partitions, partman archives a partition only when its upper
    -- bound is older than the retention window — so a "2 years" retention
    -- keeps the current and previous full year online (effective window
    -- ~2-3 years depending on when you're looking).
    UPDATE partman.part_config
       SET retention             = pipeline_config_text('inv_txn_retention'),
           retention_keep_table  = true,
           retention_schema      = 'archive',
           infinite_time_partitions = true
     WHERE parent_table = 'public.inv_transaction';

    IF NOT EXISTS (
        SELECT 1 FROM partman.part_config WHERE parent_table = 'audit.audit_log'
    ) THEN
        IF EXISTS (
            SELECT 1 FROM pg_class c
              JOIN pg_inherits i ON i.inhrelid = c.oid
             WHERE c.relname = 'audit_log_default'
        ) THEN
            ALTER TABLE audit.audit_log DETACH PARTITION audit.audit_log_default;
            DROP TABLE audit.audit_log_default;
        END IF;
        PERFORM partman.create_parent(
            p_parent_table => 'audit.audit_log',
            p_control      => 'changed_at',
            p_type         => 'range',
            p_interval     => '1 month',
            p_premake      => 4
        );
    END IF;

    UPDATE partman.part_config
       SET retention             = (pipeline_config_int('archive_retention_days') || ' days')::interval::text,
           retention_keep_table  = true,
           retention_schema      = 'archive',
           infinite_time_partitions = true
     WHERE parent_table = 'audit.audit_log';
END$$;

-- ============================================================================
-- f_purge_staging — deletes processed inbox rows and old rejects.
-- All thresholds from pipeline_config.
-- Writes a summary row to notification_outbox at severity=info.
-- ============================================================================
CREATE OR REPLACE FUNCTION f_purge_staging()
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
    v_txn_deleted    bigint;
    v_order_deleted  bigint;
    v_reject_deleted bigint;
    v_txn_days     int := pipeline_config_int('txn_inbox_purge_days');
    v_order_days   int := pipeline_config_int('order_inbox_purge_days');
    v_reject_days  int := pipeline_config_int('reject_purge_days');
BEGIN
    DELETE FROM staging.txn_inbox
     WHERE status = 'processed'
       AND received_at < now() - (v_txn_days || ' days')::interval;
    GET DIAGNOSTICS v_txn_deleted = ROW_COUNT;

    DELETE FROM staging.order_inbox
     WHERE status = 'processed'
       AND received_at < now() - (v_order_days || ' days')::interval;
    GET DIAGNOSTICS v_order_deleted = ROW_COUNT;

    DELETE FROM staging.ob_reject
     WHERE rejected_at < now() - (v_reject_days || ' days')::interval;
    GET DIAGNOSTICS v_reject_deleted = ROW_COUNT;

    INSERT INTO notification_outbox (severity, source, message, payload)
    VALUES ('info', 'f_purge_staging',
            format('purged txn_inbox=%s order_inbox=%s ob_reject=%s',
                   v_txn_deleted, v_order_deleted, v_reject_deleted),
            jsonb_build_object(
                'txn_inbox_deleted',   v_txn_deleted,
                'order_inbox_deleted', v_order_deleted,
                'ob_reject_deleted',   v_reject_deleted
            ));
END$$;

-- ============================================================================
-- f_drift_detect — samples N% of (tenant, product, warehouse) combos in mv_atp
-- and compares to a fresh calculate_inventory; any mismatch -> outbox.
-- Sample size from pipeline_config; severity from pipeline_config.
-- ============================================================================
CREATE OR REPLACE FUNCTION f_drift_detect()
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
    v_sample_pct   numeric := pipeline_config_int('drift_sample_pct');
    v_severity     text    := pipeline_config_text('drift_alert_severity');
    v_mismatches   int     := 0;
    rec record;
    calc record;
BEGIN
    FOR rec IN
        SELECT tenant_id, product_id, warehouse_id, on_hand_qty, reserved_qty,
               allocated_qty, pending_qty, atp_qty
          FROM mv_atp
         WHERE random() * 100 < v_sample_pct
    LOOP
        SELECT * INTO calc FROM calculate_inventory(rec.tenant_id, rec.product_id, rec.warehouse_id);
        IF calc.on_hand_qty   <> rec.on_hand_qty
        OR calc.reserved_qty  <> rec.reserved_qty
        OR calc.allocated_qty <> rec.allocated_qty
        OR calc.pending_qty   <> rec.pending_qty
        OR calc.atp_qty       <> rec.atp_qty
        THEN
            v_mismatches := v_mismatches + 1;
            INSERT INTO notification_outbox (tenant_id, severity, source, message, payload)
            VALUES (
                rec.tenant_id, v_severity, 'f_drift_detect',
                format('drift detected for tenant=%s product=%s warehouse=%s',
                       rec.tenant_id, rec.product_id, rec.warehouse_id),
                jsonb_build_object(
                    'mv',   row_to_json(rec),
                    'calc', row_to_json(calc)
                )
            );
        END IF;
    END LOOP;

    INSERT INTO notification_outbox (severity, source, message, payload)
    VALUES (
        'info', 'f_drift_detect',
        format('drift sample run: %s mismatches found', v_mismatches),
        jsonb_build_object('mismatches', v_mismatches, 'sample_pct', v_sample_pct)
    );
END$$;

-- ============================================================================
-- BI views (deck names these as "vw_atp_by_product" and "vw_atp_by_warehouse")
-- Same refresh cadence as mv_atp (they read from it).
-- ============================================================================
CREATE OR REPLACE VIEW vw_atp_by_product AS
SELECT
    tenant_id,
    product_id,
    SUM(on_hand_qty)   AS on_hand_qty,
    SUM(reserved_qty)  AS reserved_qty,
    SUM(allocated_qty) AS allocated_qty,
    SUM(pending_qty)   AS pending_qty,
    SUM(atp_qty)       AS atp_qty,
    max(computed_at)   AS computed_at
  FROM mv_atp
 GROUP BY tenant_id, product_id;

CREATE OR REPLACE VIEW vw_atp_by_warehouse AS
SELECT
    tenant_id,
    warehouse_id,
    SUM(on_hand_qty)   AS on_hand_qty,
    SUM(reserved_qty)  AS reserved_qty,
    SUM(allocated_qty) AS allocated_qty,
    SUM(pending_qty)   AS pending_qty,
    SUM(atp_qty)       AS atp_qty,
    max(computed_at)   AS computed_at
  FROM mv_atp
 GROUP BY tenant_id, warehouse_id;

-- ============================================================================
-- pg_cron schedules
-- Initial defaults. Ops may live-edit via cron.alter_job() without redeploy.
-- jobname uniquely identifies each schedule for idempotent re-runs.
-- ============================================================================
DO $$
DECLARE
    j_partman   text := 'inventoryledger_partman_maintenance';
    j_refresh   text := 'inventoryledger_refresh_mv_atp';
    j_purge     text := 'inventoryledger_purge_staging';
    j_drift     text := 'inventoryledger_drift_detect';
BEGIN
    PERFORM cron.unschedule(j_partman) FROM cron.job WHERE jobname = j_partman;
    PERFORM cron.unschedule(j_refresh) FROM cron.job WHERE jobname = j_refresh;
    PERFORM cron.unschedule(j_purge)   FROM cron.job WHERE jobname = j_purge;
    PERFORM cron.unschedule(j_drift)   FROM cron.job WHERE jobname = j_drift;

    PERFORM cron.schedule(j_partman, '*/30 * * * *',
        $cmd$ SELECT partman.run_maintenance(p_analyze := false); $cmd$);

    PERFORM cron.schedule(j_refresh, '*/5 * * * *',
        $cmd$ SELECT refresh_mv_atp(); $cmd$);

    PERFORM cron.schedule(j_purge, '15 3 * * *',
        $cmd$ SELECT f_purge_staging(); $cmd$);

    PERFORM cron.schedule(j_drift, '0 4 * * *',
        $cmd$ SELECT f_drift_detect(); $cmd$);
END$$;

-- ============================================================================
-- Roles + GRANTs
-- Roles created defensively if missing (NOLOGIN since secrets live elsewhere).
-- On Azure Flexible, this DO block requires the migration role to have
-- CREATEROLE; if not, pre-create the roles externally and the GRANT statements
-- below will still apply.
--
--   inventoryledger_app    — used by the Java JAR (file loader + outbox drain)
--   inventoryledger_writer — used by FOP / SFDC services (other teams)
--   inventoryledger_reader — used by the read-API exposer (PostgREST etc.)
-- ============================================================================
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'inventoryledger_app') THEN
        CREATE ROLE inventoryledger_app NOLOGIN;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'inventoryledger_writer') THEN
        CREATE ROLE inventoryledger_writer NOLOGIN;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'inventoryledger_reader') THEN
        CREATE ROLE inventoryledger_reader NOLOGIN;
    END IF;
EXCEPTION WHEN insufficient_privilege THEN
    RAISE NOTICE 'CREATEROLE not granted; roles must be pre-created by ops. Proceeding with GRANTs only.';
END$$;

-- Schema USAGE
GRANT USAGE ON SCHEMA public  TO inventoryledger_app, inventoryledger_writer, inventoryledger_reader;
GRANT USAGE ON SCHEMA staging TO inventoryledger_app, inventoryledger_writer;
GRANT USAGE ON SCHEMA audit   TO inventoryledger_app;

-- inventoryledger_app: file loader + outbox drainer
GRANT INSERT, SELECT, UPDATE ON staging.ob_load_batch TO inventoryledger_app;
GRANT INSERT, SELECT          ON staging.ob_load       TO inventoryledger_app;
GRANT SELECT                  ON staging.ob_reject     TO inventoryledger_app;
GRANT SELECT, UPDATE          ON notification_outbox   TO inventoryledger_app;
GRANT SELECT                  ON tenant, product, warehouse, uom, lot TO inventoryledger_app;
GRANT EXECUTE ON FUNCTION load_opening_balance(bigint) TO inventoryledger_app;
GRANT USAGE   ON ALL SEQUENCES IN SCHEMA staging       TO inventoryledger_app;

-- inventoryledger_writer: other teams' write path
GRANT EXECUTE ON FUNCTION
    post_transaction(jsonb),
    post_transaction_bulk(jsonb[]),
    upsert_order(jsonb)
TO inventoryledger_writer;
GRANT INSERT, SELECT, UPDATE ON staging.txn_inbox    TO inventoryledger_writer;
GRANT INSERT, SELECT, UPDATE ON staging.order_inbox  TO inventoryledger_writer;
GRANT SELECT ON tenant, product, warehouse, uom, lot TO inventoryledger_writer;

-- inventoryledger_reader: ATP read path
GRANT EXECUTE ON FUNCTION
    fetch_inventory(uuid, bigint, bigint),
    fetch_pending_orders(uuid),
    calculate_inventory(uuid, bigint, bigint)
TO inventoryledger_reader;
GRANT SELECT ON mv_atp, vw_atp_by_product, vw_atp_by_warehouse TO inventoryledger_reader;

-- All three roles need access to pipeline_config for read-only introspection
GRANT SELECT ON pipeline_config TO inventoryledger_app, inventoryledger_writer, inventoryledger_reader;
GRANT EXECUTE ON FUNCTION pipeline_config_int(text), pipeline_config_text(text)
    TO inventoryledger_app, inventoryledger_writer, inventoryledger_reader;
