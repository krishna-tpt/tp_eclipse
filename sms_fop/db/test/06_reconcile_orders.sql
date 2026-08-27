-- 06_reconcile_orders.sql — TC-160 through TC-164

BEGIN;

SELECT plan(5);

-- Setup
INSERT INTO tenant (tenant_id, tenant_code, name) VALUES
    ('00000000-0000-0000-0000-000000000a02', 'RC-T', 'Reconcile');
INSERT INTO product (tenant_id, product_code, name) VALUES
    ('00000000-0000-0000-0000-000000000a02', 'P1', 'P1');
INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES
    ('00000000-0000-0000-0000-000000000a02', 'W1', 'W1');
INSERT INTO uom (tenant_id, uom_code, name) VALUES
    ('00000000-0000-0000-0000-000000000a02', 'EA', 'Each');

-- Order with 2 open lines, line A older than line B
INSERT INTO sfdc_order (sfdc_order_id, tenant_id, customer_id, payload)
VALUES ('RC-ORD', '00000000-0000-0000-0000-000000000a02', 'CUST', '{}'::jsonb);

INSERT INTO sfdc_order_line (sfdc_order_id, line_no, tenant_id, product_id, warehouse_id, qty, uom_id, line_state, payload, created_at)
SELECT 'RC-ORD', 1, '00000000-0000-0000-0000-000000000a02',
       p.product_id, w.warehouse_id, 5, u.uom_id, 'open', '{}'::jsonb,
       now() - interval '1 hour'
  FROM product p, warehouse w, uom u
 WHERE p.tenant_id = '00000000-0000-0000-0000-000000000a02'
   AND p.product_code = 'P1' AND w.warehouse_code = 'W1' AND u.uom_code = 'EA';

INSERT INTO sfdc_order_line (sfdc_order_id, line_no, tenant_id, product_id, warehouse_id, qty, uom_id, line_state, payload)
SELECT 'RC-ORD', 2, '00000000-0000-0000-0000-000000000a02',
       p.product_id, w.warehouse_id, 5, u.uom_id, 'open', '{}'::jsonb
  FROM product p, warehouse w, uom u
 WHERE p.tenant_id = '00000000-0000-0000-0000-000000000a02'
   AND p.product_code = 'P1' AND w.warehouse_code = 'W1' AND u.uom_code = 'EA';

-- TC-160: outbound txn stamps oldest line
INSERT INTO inv_transaction (
    tenant_id, external_txn_id, product_id, warehouse_id, uom_id,
    signed_qty, txn_type, source_system, posted_at, payload
)
SELECT '00000000-0000-0000-0000-000000000a02', 'RC-TX-1',
       p.product_id, w.warehouse_id, u.uom_id, -5, 'issue', 'test', now(), '{}'::jsonb
  FROM product p, warehouse w, uom u
 WHERE p.tenant_id = '00000000-0000-0000-0000-000000000a02'
   AND p.product_code = 'P1' AND w.warehouse_code = 'W1' AND u.uom_code = 'EA';

SELECT isnt(
    (SELECT fop_synced_at FROM sfdc_order_line WHERE sfdc_order_id = 'RC-ORD' AND line_no = 1),
    NULL,
    'TC-160: outbound transaction stamps fop_synced_at on oldest open line'
);

-- TC-162: line_state of line 1 became synced
SELECT is(
    (SELECT line_state FROM sfdc_order_line WHERE sfdc_order_id = 'RC-ORD' AND line_no = 1),
    'synced'::text,
    'TC-162: stamped line transitions to synced'
);

-- TC-161: inbound txn does NOT stamp anything
INSERT INTO inv_transaction (
    tenant_id, external_txn_id, product_id, warehouse_id, uom_id,
    signed_qty, txn_type, source_system, posted_at, payload
)
SELECT '00000000-0000-0000-0000-000000000a02', 'RC-TX-2-IN',
       p.product_id, w.warehouse_id, u.uom_id, 10, 'receipt', 'test', now(), '{}'::jsonb
  FROM product p, warehouse w, uom u
 WHERE p.tenant_id = '00000000-0000-0000-0000-000000000a02'
   AND p.product_code = 'P1' AND w.warehouse_code = 'W1' AND u.uom_code = 'EA';

SELECT is(
    (SELECT fop_synced_at FROM sfdc_order_line WHERE sfdc_order_id = 'RC-ORD' AND line_no = 2),
    NULL,
    'TC-161: inbound transaction does not stamp any line'
);

-- TC-163: second outbound stamps the next open line (line 2)
INSERT INTO inv_transaction (
    tenant_id, external_txn_id, product_id, warehouse_id, uom_id,
    signed_qty, txn_type, source_system, posted_at, payload
)
SELECT '00000000-0000-0000-0000-000000000a02', 'RC-TX-3-OUT',
       p.product_id, w.warehouse_id, u.uom_id, -5, 'issue', 'test', now(), '{}'::jsonb
  FROM product p, warehouse w, uom u
 WHERE p.tenant_id = '00000000-0000-0000-0000-000000000a02'
   AND p.product_code = 'P1' AND w.warehouse_code = 'W1' AND u.uom_code = 'EA';

SELECT isnt(
    (SELECT fop_synced_at FROM sfdc_order_line WHERE sfdc_order_id = 'RC-ORD' AND line_no = 2),
    NULL,
    'TC-163: next outbound stamps the second line'
);

-- TC-164: no matching line → trigger no-ops
INSERT INTO tenant (tenant_id, tenant_code, name) VALUES
    ('00000000-0000-0000-0000-000000000a03', 'EM-T', 'Empty');
INSERT INTO product (tenant_id, product_code, name) VALUES
    ('00000000-0000-0000-0000-000000000a03', 'X', 'X');
INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES
    ('00000000-0000-0000-0000-000000000a03', 'X', 'X');
INSERT INTO uom (tenant_id, uom_code, name) VALUES
    ('00000000-0000-0000-0000-000000000a03', 'EA', 'Each');

SELECT lives_ok(
    $$ INSERT INTO inv_transaction (
           tenant_id, external_txn_id, product_id, warehouse_id, uom_id,
           signed_qty, txn_type, source_system, posted_at, payload
       )
       SELECT '00000000-0000-0000-0000-000000000a03', 'TC164',
              p.product_id, w.warehouse_id, u.uom_id, -1, 'issue', 'test', now(), '{}'::jsonb
         FROM product p, warehouse w, uom u
        WHERE p.tenant_id = '00000000-0000-0000-0000-000000000a03'
          AND p.product_code = 'X' AND w.warehouse_code = 'X' AND u.uom_code = 'EA' $$,
    'TC-164: outbound without matching line does not raise'
);

SELECT * FROM finish();
ROLLBACK;
