-- 05_upsert_order.sql — TC-150 through TC-154

BEGIN;

SELECT plan(5);

-- Setup
INSERT INTO tenant (tenant_id, tenant_code, name) VALUES
    ('00000000-0000-0000-0000-000000000f01', 'UO-T', 'UpsertOrder');
INSERT INTO product   (tenant_id, product_code,   name) VALUES
    ('00000000-0000-0000-0000-000000000f01', 'P1', 'P1'),
    ('00000000-0000-0000-0000-000000000f01', 'P2', 'P2');
INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES
    ('00000000-0000-0000-0000-000000000f01', 'W1', 'W1');
INSERT INTO uom (tenant_id, uom_code, name) VALUES
    ('00000000-0000-0000-0000-000000000f01', 'EA', 'Each');

-- TC-150: insert new order with 2 lines
SELECT upsert_order($$
{
  "tenant_code": "UO-T",
  "sfdc_order_id": "TC150-ORD",
  "customer_id": "CUST",
  "order_state": "open",
  "lines": [
    {"line_no":1,"product_code":"P1","warehouse_code":"W1","qty":3,"uom_code":"EA","line_state":"open"},
    {"line_no":2,"product_code":"P2","warehouse_code":"W1","qty":2,"uom_code":"EA","line_state":"open"}
  ]
}
$$::jsonb);

SELECT is(
    (SELECT count(*)::int FROM sfdc_order_line WHERE sfdc_order_id = 'TC150-ORD' AND line_state = 'open'),
    2,
    'TC-150: 2 lines inserted in open state'
);

-- TC-151: update qty on line 1
SELECT upsert_order($$
{
  "tenant_code": "UO-T",
  "sfdc_order_id": "TC150-ORD",
  "customer_id": "CUST",
  "order_state": "open",
  "lines": [
    {"line_no":1,"product_code":"P1","warehouse_code":"W1","qty":99,"uom_code":"EA","line_state":"open"},
    {"line_no":2,"product_code":"P2","warehouse_code":"W1","qty":2,"uom_code":"EA","line_state":"open"}
  ]
}
$$::jsonb);

SELECT is(
    (SELECT qty FROM sfdc_order_line WHERE sfdc_order_id = 'TC150-ORD' AND line_no = 1),
    99::numeric,
    'TC-151: line 1 qty updated to 99'
);

-- TC-152: omitting a line marks it cancelled
SELECT upsert_order($$
{
  "tenant_code": "UO-T",
  "sfdc_order_id": "TC150-ORD",
  "customer_id": "CUST",
  "order_state": "open",
  "lines": [
    {"line_no":1,"product_code":"P1","warehouse_code":"W1","qty":99,"uom_code":"EA","line_state":"open"}
  ]
}
$$::jsonb);

SELECT is(
    (SELECT line_state FROM sfdc_order_line WHERE sfdc_order_id = 'TC150-ORD' AND line_no = 2),
    'cancelled'::text,
    'TC-152: omitted line 2 marked cancelled'
);

-- TC-153: order_state transition
SELECT upsert_order($$
{
  "tenant_code": "UO-T",
  "sfdc_order_id": "TC150-ORD",
  "customer_id": "CUST",
  "order_state": "synced",
  "lines": [
    {"line_no":1,"product_code":"P1","warehouse_code":"W1","qty":99,"uom_code":"EA","line_state":"open"}
  ]
}
$$::jsonb);

SELECT is(
    (SELECT order_state FROM sfdc_order WHERE sfdc_order_id = 'TC150-ORD'),
    'synced'::text,
    'TC-153: order_state transition to synced'
);

-- TC-154: re-call same payload idempotent (no error, same end state)
SELECT upsert_order($$
{
  "tenant_code": "UO-T",
  "sfdc_order_id": "TC150-ORD",
  "customer_id": "CUST",
  "order_state": "synced",
  "lines": [
    {"line_no":1,"product_code":"P1","warehouse_code":"W1","qty":99,"uom_code":"EA","line_state":"open"}
  ]
}
$$::jsonb);

SELECT is(
    (SELECT qty FROM sfdc_order_line WHERE sfdc_order_id = 'TC150-ORD' AND line_no = 1),
    99::numeric,
    'TC-154: re-call leaves line qty unchanged'
);

SELECT * FROM finish();
ROLLBACK;
