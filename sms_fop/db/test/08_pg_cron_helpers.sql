-- 08_pg_cron_helpers.sql — TC-190 through TC-195
-- pg_cron jobs registered, f_purge_staging, f_drift_detect, pipeline_config helpers.

BEGIN;

SELECT plan(6);

-- TC-190: all four pg_cron jobs registered
SELECT bag_eq(
    $$ SELECT jobname FROM cron.job
        WHERE jobname IN ('inventoryledger_partman_maintenance',
                          'inventoryledger_refresh_mv_atp',
                          'inventoryledger_purge_staging',
                          'inventoryledger_drift_detect') $$,
    $$ VALUES ('inventoryledger_partman_maintenance'),
              ('inventoryledger_refresh_mv_atp'),
              ('inventoryledger_purge_staging'),
              ('inventoryledger_drift_detect') $$,
    'TC-190: all four pg_cron jobs registered by jobname'
);

-- TC-195: pipeline_config helpers
SELECT is(
    pipeline_config_int('drift_sample_pct'),
    5,
    'TC-195a: pipeline_config_int returns default 5 for drift_sample_pct'
);

SELECT is(
    pipeline_config_int('not_a_real_key'),
    NULL,
    'TC-195b: pipeline_config_int returns NULL for missing key'
);

-- TC-191: f_purge_staging deletes only rows older than threshold
INSERT INTO tenant (tenant_id, tenant_code, name) VALUES
    ('00000000-0000-0000-0000-000000000c02', 'PG-T', 'PgCron');

INSERT INTO staging.txn_inbox (tenant_id, external_txn_id, payload, status, received_at)
VALUES
    ('00000000-0000-0000-0000-000000000c02', 'OLD-PROC', '{}'::jsonb, 'processed', now() - interval '30 days'),
    ('00000000-0000-0000-0000-000000000c02', 'NEW-PROC', '{}'::jsonb, 'processed', now() - interval '1 day'),
    ('00000000-0000-0000-0000-000000000c02', 'OLD-PEND', '{}'::jsonb, 'pending',   now() - interval '30 days');

SELECT f_purge_staging();

SELECT bag_eq(
    $$ SELECT external_txn_id FROM staging.txn_inbox
        WHERE tenant_id = '00000000-0000-0000-0000-000000000c02' $$,
    $$ VALUES ('NEW-PROC'), ('OLD-PEND') $$,
    'TC-191: only old processed rows deleted; new processed and old pending kept'
);

-- TC-192: changing pipeline_config takes effect on next call
UPDATE pipeline_config SET value = '1' WHERE key = 'txn_inbox_purge_days';

INSERT INTO staging.txn_inbox (tenant_id, external_txn_id, payload, status, received_at)
VALUES ('00000000-0000-0000-0000-000000000c02', 'TWO-DAYS-OLD', '{}'::jsonb, 'processed', now() - interval '2 days');

SELECT f_purge_staging();

SELECT ok(
    NOT EXISTS (SELECT 1 FROM staging.txn_inbox
                 WHERE tenant_id = '00000000-0000-0000-0000-000000000c02'
                   AND external_txn_id = 'TWO-DAYS-OLD'),
    'TC-192: changing pipeline_config.txn_inbox_purge_days=1 causes 2-day-old row to be purged'
);

-- TC-193: f_drift_detect reports zero mismatches when mv matches calc
-- Setup minimal data, refresh MV (non-concurrent since we are in a tx)
INSERT INTO product   (tenant_id, product_code,   name) VALUES ('00000000-0000-0000-0000-000000000c02', 'P', 'P');
INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES ('00000000-0000-0000-0000-000000000c02', 'W', 'W');
INSERT INTO uom       (tenant_id, uom_code,       name) VALUES ('00000000-0000-0000-0000-000000000c02', 'EA', 'EA');

WITH ids AS (
    SELECT (SELECT product_id   FROM product   WHERE tenant_id = '00000000-0000-0000-0000-000000000c02' AND product_code   = 'P') AS pid,
           (SELECT warehouse_id FROM warehouse WHERE tenant_id = '00000000-0000-0000-0000-000000000c02' AND warehouse_code = 'W') AS wid,
           (SELECT uom_id       FROM uom       WHERE tenant_id = '00000000-0000-0000-0000-000000000c02' AND uom_code       = 'EA') AS uid
)
INSERT INTO opening_balance (tenant_id, product_id, warehouse_id, qty, uom_id, as_of_date, batch_id)
SELECT '00000000-0000-0000-0000-000000000c02', pid, wid, 50, uid, '2026-05-18', 0 FROM ids;

REFRESH MATERIALIZED VIEW mv_atp;

DELETE FROM notification_outbox WHERE source = 'f_drift_detect';
-- Force 100% sample so we don't randomly miss
UPDATE pipeline_config SET value = '100' WHERE key = 'drift_sample_pct';
SELECT f_drift_detect();

SELECT ok(
    NOT EXISTS (
        SELECT 1 FROM notification_outbox
         WHERE source = 'f_drift_detect'
           AND severity = pipeline_config_text('drift_alert_severity')
           AND message LIKE 'drift detected%'
    ),
    'TC-193: f_drift_detect reports no mismatches when mv_atp matches calculate_inventory'
);

SELECT * FROM finish();
ROLLBACK;
