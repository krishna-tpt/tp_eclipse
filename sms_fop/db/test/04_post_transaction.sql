-- 04_post_transaction.sql — TC-140 through TC-149

BEGIN;

SELECT plan(10);

-- Setup
INSERT INTO tenant (tenant_id, tenant_code, name) VALUES
    ('00000000-0000-0000-0000-000000000e01', 'PT-T', 'PT Test');
INSERT INTO product   (tenant_id, product_code,   name) VALUES
    ('00000000-0000-0000-0000-000000000e01', 'P1', 'P1');
INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES
    ('00000000-0000-0000-0000-000000000e01', 'W1', 'W1');
INSERT INTO uom (tenant_id, uom_code, name) VALUES
    ('00000000-0000-0000-0000-000000000e01', 'EA', 'Each');
INSERT INTO lot (tenant_id, product_id, lot_code)
SELECT '00000000-0000-0000-0000-000000000e01', product_id, 'LOT-A'
  FROM product WHERE tenant_id = '00000000-0000-0000-0000-000000000e01' AND product_code = 'P1';

-- TC-140: valid receipt
SELECT is(
    (SELECT accepted FROM post_transaction(
        '{"tenant_code":"PT-T","external_txn_id":"TC140","product_code":"P1","warehouse_code":"W1","uom_code":"EA","signed_qty":10,"txn_type":"receipt","source_system":"test","posted_at":"2026-05-18T10:00:00Z"}'::jsonb
    )),
    true,
    'TC-140: valid receipt accepted'
);

-- TC-141: duplicate external_txn_id returns duplicate
SELECT is(
    (SELECT reason FROM post_transaction(
        '{"tenant_code":"PT-T","external_txn_id":"TC140","product_code":"P1","warehouse_code":"W1","uom_code":"EA","signed_qty":10,"txn_type":"receipt","source_system":"test","posted_at":"2026-05-18T10:00:00Z"}'::jsonb
    )),
    'duplicate',
    'TC-141: duplicate external_txn_id returns reason=duplicate'
);

-- TC-142: unknown tenant
SELECT is(
    (SELECT reason FROM post_transaction(
        '{"tenant_code":"NO-SUCH","external_txn_id":"TC142","product_code":"P1","warehouse_code":"W1","uom_code":"EA","signed_qty":1,"txn_type":"receipt","source_system":"test","posted_at":"2026-05-18T10:00:00Z"}'::jsonb
    )),
    'unknown_tenant',
    'TC-142: unknown tenant returns unknown_tenant'
);

-- TC-143a: unknown product
SELECT is(
    (SELECT reason FROM post_transaction(
        '{"tenant_code":"PT-T","external_txn_id":"TC143a","product_code":"NO","warehouse_code":"W1","uom_code":"EA","signed_qty":1,"txn_type":"receipt","source_system":"test","posted_at":"2026-05-18T10:00:00Z"}'::jsonb
    )),
    'unknown_product',
    'TC-143a: unknown product returns unknown_product'
);

-- TC-143b: unknown warehouse
SELECT is(
    (SELECT reason FROM post_transaction(
        '{"tenant_code":"PT-T","external_txn_id":"TC143b","product_code":"P1","warehouse_code":"NO","uom_code":"EA","signed_qty":1,"txn_type":"receipt","source_system":"test","posted_at":"2026-05-18T10:00:00Z"}'::jsonb
    )),
    'unknown_warehouse',
    'TC-143b: unknown warehouse returns unknown_warehouse'
);

-- TC-144: qty=0
SELECT is(
    (SELECT reason FROM post_transaction(
        '{"tenant_code":"PT-T","external_txn_id":"TC144","product_code":"P1","warehouse_code":"W1","uom_code":"EA","signed_qty":0,"txn_type":"receipt","source_system":"test","posted_at":"2026-05-18T10:00:00Z"}'::jsonb
    )),
    'invalid_qty',
    'TC-144: qty=0 returns invalid_qty'
);

-- TC-145: transfer pair via bulk
WITH bulk AS (
    SELECT * FROM post_transaction_bulk(ARRAY[
        '{"tenant_code":"PT-T","external_txn_id":"TC145-OUT","product_code":"P1","warehouse_code":"W1","uom_code":"EA","signed_qty":-5,"txn_type":"transfer_out","transfer_pair_id":"11111111-1111-1111-1111-111111111111","source_system":"test","posted_at":"2026-05-18T11:00:00Z"}'::jsonb,
        '{"tenant_code":"PT-T","external_txn_id":"TC145-IN","product_code":"P1","warehouse_code":"W1","uom_code":"EA","signed_qty":5,"txn_type":"transfer_in","transfer_pair_id":"11111111-1111-1111-1111-111111111111","source_system":"test","posted_at":"2026-05-18T11:00:00Z"}'::jsonb
    ])
)
SELECT is(
    (SELECT count(*)::int FROM bulk WHERE accepted),
    2,
    'TC-145: transfer pair both rows accepted'
);

-- TC-146: bulk returns per-row outcomes (one valid, one duplicate)
-- pgTAP's bag_eq runs the $$ ... $$ args as new queries; a CTE wouldn't be
-- visible there, so materialise to a TEMP TABLE.
CREATE TEMP TABLE bulk_146 ON COMMIT DROP AS
SELECT idx, accepted, reason FROM post_transaction_bulk(ARRAY[
    '{"tenant_code":"PT-T","external_txn_id":"TC146-NEW","product_code":"P1","warehouse_code":"W1","uom_code":"EA","signed_qty":1,"txn_type":"receipt","source_system":"test","posted_at":"2026-05-18T12:00:00Z"}'::jsonb,
    '{"tenant_code":"PT-T","external_txn_id":"TC140","product_code":"P1","warehouse_code":"W1","uom_code":"EA","signed_qty":1,"txn_type":"receipt","source_system":"test","posted_at":"2026-05-18T12:00:00Z"}'::jsonb
]);
SELECT bag_eq(
    $$ SELECT idx, reason FROM bulk_146 ORDER BY idx $$,
    $$ VALUES (1, 'ok'), (2, 'duplicate') $$,
    'TC-146: bulk returns per-row outcomes'
);

-- TC-148: known lot resolves
SELECT is(
    (SELECT accepted FROM post_transaction(
        '{"tenant_code":"PT-T","external_txn_id":"TC148","product_code":"P1","warehouse_code":"W1","lot_code":"LOT-A","uom_code":"EA","signed_qty":1,"txn_type":"receipt","source_system":"test","posted_at":"2026-05-18T10:00:00Z"}'::jsonb
    )),
    true,
    'TC-148: known lot_code resolves and accepted'
);

-- TC-149: unknown lot
SELECT is(
    (SELECT reason FROM post_transaction(
        '{"tenant_code":"PT-T","external_txn_id":"TC149","product_code":"P1","warehouse_code":"W1","lot_code":"NOLOT","uom_code":"EA","signed_qty":1,"txn_type":"receipt","source_system":"test","posted_at":"2026-05-18T10:00:00Z"}'::jsonb
    )),
    'unknown_lot',
    'TC-149: unknown lot_code returns unknown_lot'
);

SELECT * FROM finish();
ROLLBACK;
