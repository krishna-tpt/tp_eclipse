-- 02_audit_triggers.sql — TC-120 through TC-128
-- Audit log, stock_balance auto-update, mv_atp_dirty marker triggers.

BEGIN;

SELECT plan(9);

-- Setup tenant + master
INSERT INTO tenant   (tenant_id, tenant_code, name) VALUES ('00000000-0000-0000-0000-000000000c01', 'AUD', 'AuditTest');
INSERT INTO product  (tenant_id, product_code,   name) VALUES ('00000000-0000-0000-0000-000000000c01', 'PA', 'ProdA');
INSERT INTO warehouse(tenant_id, warehouse_code, name) VALUES ('00000000-0000-0000-0000-000000000c01', 'WA', 'WhA');
INSERT INTO uom      (tenant_id, uom_code,       name) VALUES ('00000000-0000-0000-0000-000000000c01', 'EA', 'Each');

-- TC-120: INSERT on product writes audit row
DELETE FROM audit.audit_log WHERE table_name = 'product' AND tenant_id = '00000000-0000-0000-0000-000000000c01';
INSERT INTO product (tenant_id, product_code, name)
VALUES ('00000000-0000-0000-0000-000000000c01', 'TC120', 'tc120');

SELECT is(
    (SELECT operation FROM audit.audit_log
      WHERE table_name = 'product' AND tenant_id = '00000000-0000-0000-0000-000000000c01'
      ORDER BY changed_at DESC LIMIT 1),
    'I'::char(1),
    'TC-120: product INSERT produces audit operation=I'
);

-- TC-121: UPDATE writes before+after
UPDATE product SET name = 'tc121-changed' WHERE product_code = 'TC120' AND tenant_id = '00000000-0000-0000-0000-000000000c01';
SELECT is(
    (SELECT before_data->>'name' FROM audit.audit_log
      WHERE table_name = 'product' AND operation = 'U' AND tenant_id = '00000000-0000-0000-0000-000000000c01'
      ORDER BY changed_at DESC LIMIT 1),
    'tc120',
    'TC-121: UPDATE captures before_data.name'
);

-- TC-122: DELETE writes before, after NULL
DELETE FROM product WHERE product_code = 'TC120' AND tenant_id = '00000000-0000-0000-0000-000000000c01';
SELECT is(
    (SELECT after_data FROM audit.audit_log
      WHERE table_name = 'product' AND operation = 'D' AND tenant_id = '00000000-0000-0000-0000-000000000c01'
      ORDER BY changed_at DESC LIMIT 1),
    NULL,
    'TC-122: DELETE has after_data NULL'
);

-- TC-123: audit captures tenant_id from row
SELECT is(
    (SELECT tenant_id FROM audit.audit_log
      WHERE table_name = 'product' AND operation = 'I'
      ORDER BY changed_at DESC LIMIT 1),
    '00000000-0000-0000-0000-000000000c01'::uuid,
    'TC-123: audit captures tenant_id'
);

-- TC-124: INSERT on inv_transaction updates stock_balance
INSERT INTO inv_transaction (
    tenant_id, external_txn_id, product_id, warehouse_id, uom_id,
    signed_qty, txn_type, source_system, posted_at, payload
)
SELECT '00000000-0000-0000-0000-000000000c01', 'TC124',
       p.product_id, w.warehouse_id, u.uom_id, 10, 'receipt', 'test', now(), '{}'::jsonb
  FROM product p, warehouse w, uom u
 WHERE p.tenant_id = '00000000-0000-0000-0000-000000000c01'
   AND w.tenant_id = '00000000-0000-0000-0000-000000000c01'
   AND u.tenant_id = '00000000-0000-0000-0000-000000000c01'
   AND p.product_code = 'PA' AND w.warehouse_code = 'WA' AND u.uom_code = 'EA';

SELECT is(
    (SELECT on_hand_qty FROM stock_balance
      WHERE tenant_id = '00000000-0000-0000-0000-000000000c01' AND lot_id = 0),
    10::numeric,
    'TC-124: inv_transaction trigger created stock_balance row with on_hand_qty=10'
);

-- TC-125: second INSERT accumulates
INSERT INTO inv_transaction (
    tenant_id, external_txn_id, product_id, warehouse_id, uom_id,
    signed_qty, txn_type, source_system, posted_at, payload
)
SELECT '00000000-0000-0000-0000-000000000c01', 'TC125',
       p.product_id, w.warehouse_id, u.uom_id, 5, 'receipt', 'test', now(), '{}'::jsonb
  FROM product p, warehouse w, uom u
 WHERE p.tenant_id = '00000000-0000-0000-0000-000000000c01'
   AND p.product_code = 'PA' AND w.warehouse_code = 'WA' AND u.uom_code = 'EA';

SELECT is(
    (SELECT on_hand_qty FROM stock_balance
      WHERE tenant_id = '00000000-0000-0000-0000-000000000c01' AND lot_id = 0),
    15::numeric,
    'TC-125: second receipt accumulates on_hand_qty to 15'
);

-- TC-126: outbound reduces
INSERT INTO inv_transaction (
    tenant_id, external_txn_id, product_id, warehouse_id, uom_id,
    signed_qty, txn_type, source_system, posted_at, payload
)
SELECT '00000000-0000-0000-0000-000000000c01', 'TC126',
       p.product_id, w.warehouse_id, u.uom_id, -3, 'issue', 'test', now(), '{}'::jsonb
  FROM product p, warehouse w, uom u
 WHERE p.tenant_id = '00000000-0000-0000-0000-000000000c01'
   AND p.product_code = 'PA' AND w.warehouse_code = 'WA' AND u.uom_code = 'EA';

SELECT is(
    (SELECT on_hand_qty FROM stock_balance
      WHERE tenant_id = '00000000-0000-0000-0000-000000000c01' AND lot_id = 0),
    12::numeric,
    'TC-126: outbound transaction reduces on_hand_qty to 12'
);

-- TC-127: inv_transaction INSERT marks mv_atp_dirty
SELECT ok(
    EXISTS (SELECT 1 FROM mv_atp_dirty WHERE tenant_id = '00000000-0000-0000-0000-000000000c01'),
    'TC-127: inv_transaction INSERT populates mv_atp_dirty for tenant'
);

-- TC-128: sfdc_order_line UPDATE marks dirty
INSERT INTO sfdc_order (sfdc_order_id, tenant_id, customer_id, payload)
VALUES ('TC128', '00000000-0000-0000-0000-000000000c01', 'CUST', '{}'::jsonb);

INSERT INTO sfdc_order_line (sfdc_order_id, line_no, tenant_id, product_id, warehouse_id, qty, uom_id, payload)
SELECT 'TC128', 1, '00000000-0000-0000-0000-000000000c01',
       p.product_id, w.warehouse_id, 5, u.uom_id, '{}'::jsonb
  FROM product p, warehouse w, uom u
 WHERE p.tenant_id = '00000000-0000-0000-0000-000000000c01'
   AND p.product_code = 'PA' AND w.warehouse_code = 'WA' AND u.uom_code = 'EA';

-- Clear and re-mark to detect the UPDATE side
DELETE FROM mv_atp_dirty WHERE tenant_id = '00000000-0000-0000-0000-000000000c01';
UPDATE sfdc_order_line SET qty = 6 WHERE sfdc_order_id = 'TC128';

SELECT ok(
    EXISTS (SELECT 1 FROM mv_atp_dirty WHERE tenant_id = '00000000-0000-0000-0000-000000000c01'),
    'TC-128: sfdc_order_line UPDATE re-populates mv_atp_dirty'
);

SELECT * FROM finish();
ROLLBACK;
