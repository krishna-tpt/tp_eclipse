-- 07_calculate_fetch.sql — TC-170 through TC-177
-- Notes:
--   refresh_mv_atp() uses REFRESH MATERIALIZED VIEW CONCURRENTLY, which cannot
--   run inside a transaction. Tests of refresh side-effects use direct
--   non-concurrent REFRESH and assert against logic separately.

BEGIN;

SELECT plan(8);

-- Setup
INSERT INTO tenant (tenant_id, tenant_code, name) VALUES
    ('00000000-0000-0000-0000-000000000b02', 'CF-T', 'CalcFetch');
INSERT INTO product (tenant_id, product_code, name) VALUES
    ('00000000-0000-0000-0000-000000000b02', 'P1', 'P1');
INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES
    ('00000000-0000-0000-0000-000000000b02', 'W1', 'W1');
INSERT INTO uom (tenant_id, uom_code, name) VALUES
    ('00000000-0000-0000-0000-000000000b02', 'EA', 'Each');

WITH ids AS (
    SELECT (SELECT product_id   FROM product   WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND product_code   = 'P1') AS pid,
           (SELECT warehouse_id FROM warehouse WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND warehouse_code = 'W1') AS wid,
           (SELECT uom_id       FROM uom       WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND uom_code       = 'EA') AS uid
)
INSERT INTO opening_balance (tenant_id, product_id, warehouse_id, qty, uom_id, as_of_date, batch_id)
SELECT '00000000-0000-0000-0000-000000000b02', pid, wid, 100, uid, '2026-05-18', 0 FROM ids;

WITH ids AS (
    SELECT (SELECT product_id   FROM product   WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND product_code   = 'P1') AS pid,
           (SELECT warehouse_id FROM warehouse WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND warehouse_code = 'W1') AS wid,
           (SELECT uom_id       FROM uom       WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND uom_code       = 'EA') AS uid
)
INSERT INTO inv_transaction (
    tenant_id, external_txn_id, product_id, warehouse_id, uom_id,
    signed_qty, txn_type, source_system, posted_at, payload
)
SELECT '00000000-0000-0000-0000-000000000b02', 'CF-TX-' || g, pid, wid, uid,
       CASE WHEN g = 1 THEN 20 ELSE -5 END,
       CASE WHEN g = 1 THEN 'receipt' ELSE 'issue' END,
       'test', now(), '{}'::jsonb
  FROM ids, generate_series(1, 2) AS g;

-- Order lines: open 10, synced 5, both fop_synced_at NULL
INSERT INTO sfdc_order (sfdc_order_id, tenant_id, customer_id, payload)
VALUES ('CF-ORD', '00000000-0000-0000-0000-000000000b02', 'CUST', '{}'::jsonb);

WITH ids AS (
    SELECT (SELECT product_id   FROM product   WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND product_code   = 'P1') AS pid,
           (SELECT warehouse_id FROM warehouse WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND warehouse_code = 'W1') AS wid,
           (SELECT uom_id       FROM uom       WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND uom_code       = 'EA') AS uid
)
INSERT INTO sfdc_order_line (sfdc_order_id, line_no, tenant_id, product_id, warehouse_id, qty, uom_id, line_state, payload)
SELECT 'CF-ORD', g, '00000000-0000-0000-0000-000000000b02', pid, wid,
       CASE WHEN g = 1 THEN 10 ELSE 5 END,
       uid,
       CASE WHEN g = 1 THEN 'open' ELSE 'synced' END,
       '{}'::jsonb
  FROM ids, generate_series(1, 2) AS g;

-- TC-170: on_hand = 100 + 20 - 5 = 115
WITH ids AS (
    SELECT (SELECT product_id   FROM product   WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND product_code   = 'P1') AS pid,
           (SELECT warehouse_id FROM warehouse WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND warehouse_code = 'W1') AS wid
)
SELECT is(
    (SELECT on_hand_qty FROM ids, LATERAL calculate_inventory('00000000-0000-0000-0000-000000000b02', ids.pid, ids.wid)),
    115::numeric,
    'TC-170: on_hand = opening + sum(signed_qty)'
);

-- TC-171: atp = on_hand - pending. open + synced (both fop_synced_at NULL) = 15. atp = 115 - 15 = 100
WITH ids AS (
    SELECT (SELECT product_id   FROM product   WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND product_code   = 'P1') AS pid,
           (SELECT warehouse_id FROM warehouse WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND warehouse_code = 'W1') AS wid
)
SELECT is(
    (SELECT atp_qty FROM ids, LATERAL calculate_inventory('00000000-0000-0000-0000-000000000b02', ids.pid, ids.wid)),
    100::numeric,
    'TC-171: atp = on_hand - pending'
);

-- TC-172: closed/cancelled lines do NOT reduce atp
UPDATE sfdc_order_line SET line_state = 'closed' WHERE sfdc_order_id = 'CF-ORD' AND line_no = 1;
UPDATE sfdc_order_line SET line_state = 'cancelled' WHERE sfdc_order_id = 'CF-ORD' AND line_no = 2;

WITH ids AS (
    SELECT (SELECT product_id   FROM product   WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND product_code   = 'P1') AS pid,
           (SELECT warehouse_id FROM warehouse WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND warehouse_code = 'W1') AS wid
)
SELECT is(
    (SELECT atp_qty FROM ids, LATERAL calculate_inventory('00000000-0000-0000-0000-000000000b02', ids.pid, ids.wid)),
    115::numeric,
    'TC-172: closed/cancelled lines do not reduce atp'
);

-- Restore open lines for remaining tests
UPDATE sfdc_order_line SET line_state = 'open' WHERE sfdc_order_id = 'CF-ORD' AND line_no = 1;
UPDATE sfdc_order_line SET line_state = 'synced' WHERE sfdc_order_id = 'CF-ORD' AND line_no = 2;

-- TC-173: refresh_mv_atp is no-op when mv_atp_dirty empty
DELETE FROM mv_atp_dirty;
SELECT lives_ok(
    $$ SELECT refresh_mv_atp() $$,
    'TC-173: refresh_mv_atp() no-ops with empty dirty marker'
);

-- TC-174: dirty marker exists then non-concurrent refresh reflects content
-- (In production refresh_mv_atp uses CONCURRENTLY; in tests we exercise the
-- refresh path directly to verify mv content; refresh_mv_atp wiring is covered
-- by TC-173 + TC-194 + integration tests outside transactions.)
INSERT INTO mv_atp_dirty (tenant_id) VALUES ('00000000-0000-0000-0000-000000000b02')
ON CONFLICT (tenant_id) DO NOTHING;
REFRESH MATERIALIZED VIEW mv_atp;

WITH ids AS (
    SELECT (SELECT product_id   FROM product   WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND product_code   = 'P1') AS pid,
           (SELECT warehouse_id FROM warehouse WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND warehouse_code = 'W1') AS wid
)
SELECT is(
    (SELECT on_hand_qty FROM mv_atp m JOIN ids ON m.product_id = ids.pid AND m.warehouse_id = ids.wid
       WHERE m.tenant_id = '00000000-0000-0000-0000-000000000b02'),
    115::numeric,
    'TC-174: mv_atp content matches calculate_inventory after refresh'
);

-- TC-175: fetch_inventory returns all rows for tenant
SELECT ok(
    (SELECT count(*) > 0 FROM fetch_inventory('00000000-0000-0000-0000-000000000b02')),
    'TC-175: fetch_inventory(tenant) returns rows'
);

-- TC-176: fetch_inventory with product+warehouse narrows to single row
WITH ids AS (
    SELECT (SELECT product_id   FROM product   WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND product_code   = 'P1') AS pid,
           (SELECT warehouse_id FROM warehouse WHERE tenant_id = '00000000-0000-0000-0000-000000000b02' AND warehouse_code = 'W1') AS wid
)
SELECT is(
    (SELECT count(*)::int FROM ids, LATERAL fetch_inventory('00000000-0000-0000-0000-000000000b02', ids.pid, ids.wid)),
    1,
    'TC-176: fetch_inventory with filter returns single row'
);

-- TC-177: fetch_pending_orders returns only open+synced with NULL fop_synced_at
SELECT is(
    (SELECT count(*)::int FROM fetch_pending_orders('00000000-0000-0000-0000-000000000b02')),
    2,
    'TC-177: fetch_pending_orders returns 2 unmatched lines'
);

SELECT * FROM finish();
ROLLBACK;
