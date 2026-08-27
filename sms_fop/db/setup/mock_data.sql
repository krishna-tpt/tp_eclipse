-- mock_data.sql
--
-- Comprehensive mock data for ALL tables (staging + public).
-- Scenario: TyreCo Distributors — tyres & wheels, two warehouses.
--
-- State of the world:
--   - Opening balance batch: fully loaded (8 rows → opening_balance)
--   - txn_inbox: 5 processed, 3 still pending
--   - order_inbox: 2 processed, 1 still pending
--   - stock_balance: reflects OB + 5 processed transactions
--
-- Prerequisites: run inventoryledger_schema.sql first.
--     psql -d inventoryledger -f inventoryledger_schema.sql
--     psql -d inventoryledger -f mock_data.sql

BEGIN;

-- ============================================================================
-- 1. Master data (auto-populated by drain functions in production)
--    Using OVERRIDING SYSTEM VALUE to pin IDs for reproducible test data.
-- ============================================================================

-- Tenant
INSERT INTO tenant (tenant_id, tenant_code, name)
VALUES ('a0000000-0000-0000-0000-000000000001', 'TYRECO', 'TyreCo Distributors Pvt Ltd');

-- Products (IDs 1-5)
INSERT INTO product (product_id, tenant_id, product_code, name) OVERRIDING SYSTEM VALUE VALUES
    (1, 'a0000000-0000-0000-0000-000000000001', 'TYR-RAD-195',  'Radial Tyre 195/65 R15'),
    (2, 'a0000000-0000-0000-0000-000000000001', 'TYR-RAD-205',  'Radial Tyre 205/55 R16'),
    (3, 'a0000000-0000-0000-0000-000000000001', 'TYR-TRUCK-10', 'Truck Tyre 10.00 R20'),
    (4, 'a0000000-0000-0000-0000-000000000001', 'WHL-ALLOY-15', 'Alloy Wheel 15 inch'),
    (5, 'a0000000-0000-0000-0000-000000000001', 'WHL-ALLOY-16', 'Alloy Wheel 16 inch');
SELECT setval(pg_get_serial_sequence('product', 'product_id'), 5);

-- Warehouses (IDs 1-2)
INSERT INTO warehouse (warehouse_id, tenant_id, warehouse_code, name) OVERRIDING SYSTEM VALUE VALUES
    (1, 'a0000000-0000-0000-0000-000000000001', 'WH-CHN', 'Chennai Central Warehouse'),
    (2, 'a0000000-0000-0000-0000-000000000001', 'WH-BLR', 'Bangalore Hub');
SELECT setval(pg_get_serial_sequence('warehouse', 'warehouse_id'), 2);

-- Units of measure (IDs 1-2)
INSERT INTO uom (uom_id, tenant_id, uom_code, name) OVERRIDING SYSTEM VALUE VALUES
    (1, 'a0000000-0000-0000-0000-000000000001', 'EA',  'Each'),
    (2, 'a0000000-0000-0000-0000-000000000001', 'SET', 'Set of 4');
SELECT setval(pg_get_serial_sequence('uom', 'uom_id'), 2);

-- Lots (IDs 1-3)
INSERT INTO lot (lot_id, tenant_id, product_id, lot_code) OVERRIDING SYSTEM VALUE VALUES
    (1, 'a0000000-0000-0000-0000-000000000001', 1, 'LOT-2026-Q1'),
    (2, 'a0000000-0000-0000-0000-000000000001', 1, 'LOT-2026-Q2'),
    (3, 'a0000000-0000-0000-0000-000000000001', 3, 'LOT-2026-MAR');
SELECT setval(pg_get_serial_sequence('lot', 'lot_id'), 3);


-- ============================================================================
-- 2. Staging: Opening Balance — ob_load_batch + ob_load (Pipeline 1)
--    Batch fully loaded. 8 CSV rows → 8 opening_balance rows.
-- ============================================================================

INSERT INTO staging.ob_load_batch
    (batch_id, tenant_code, file_name, file_hash, status, row_count, accepted_count, rejected_count, completed_at)
OVERRIDING SYSTEM VALUE VALUES
    (1, 'TYRECO',
     'tyreco_opening_stock_20260401.csv',
     'sha256:ab3f7c0e9d1245a6b8c3d4e5f60718293a4b5c6d7e8f901234567890abcdef12',
     'loaded', 8, 8, 0, '2026-04-02 10:00:00+05:30');
SELECT setval(pg_get_serial_sequence('staging.ob_load_batch', 'batch_id'), 1);

INSERT INTO staging.ob_load
    (batch_id, tenant_code, product_code, warehouse_code, lot_code, uom_code, qty, as_of_date, source_ref, line_no)
VALUES
    (1, 'TYRECO', 'TYR-RAD-195',  'WH-CHN', 'LOT-2026-Q1',  'EA',  '500',  '2026-04-01', 'OB-CHN-001', 1),
    (1, 'TYRECO', 'TYR-RAD-205',  'WH-CHN', NULL,            'EA',  '320',  '2026-04-01', 'OB-CHN-002', 2),
    (1, 'TYRECO', 'TYR-TRUCK-10', 'WH-CHN', 'LOT-2026-MAR', 'EA',  '150',  '2026-04-01', 'OB-CHN-003', 3),
    (1, 'TYRECO', 'WHL-ALLOY-15', 'WH-CHN', NULL,            'EA',  '200',  '2026-04-01', 'OB-CHN-004', 4),
    (1, 'TYRECO', 'WHL-ALLOY-16', 'WH-CHN', NULL,            'EA',  '180',  '2026-04-01', 'OB-CHN-005', 5),
    (1, 'TYRECO', 'TYR-RAD-195',  'WH-BLR', 'LOT-2026-Q2',  'EA',  '250',  '2026-04-01', 'OB-BLR-001', 6),
    (1, 'TYRECO', 'TYR-RAD-205',  'WH-BLR', NULL,            'EA',  '100',  '2026-04-01', 'OB-BLR-002', 7),
    (1, 'TYRECO', 'WHL-ALLOY-16', 'WH-BLR', NULL,            'EA',  '160',  '2026-04-01', 'OB-BLR-003', 8);


-- ============================================================================
-- 3. Live: Opening Balance (result of processing the batch above)
-- ============================================================================

INSERT INTO opening_balance
    (tenant_id, tenant_code, product_id, product_code, warehouse_id, warehouse_code,
     lot_id, lot_code, qty, uom_id, uom_code, as_of_date, batch_id)
VALUES
    ('a0000000-0000-0000-0000-000000000001', 'TYRECO', 1, 'TYR-RAD-195',  1, 'WH-CHN', 1,    'LOT-2026-Q1',  500, 1, 'EA', '2026-04-01', 1),
    ('a0000000-0000-0000-0000-000000000001', 'TYRECO', 2, 'TYR-RAD-205',  1, 'WH-CHN', NULL,  NULL,           320, 1, 'EA', '2026-04-01', 1),
    ('a0000000-0000-0000-0000-000000000001', 'TYRECO', 3, 'TYR-TRUCK-10', 1, 'WH-CHN', 3,    'LOT-2026-MAR', 150, 1, 'EA', '2026-04-01', 1),
    ('a0000000-0000-0000-0000-000000000001', 'TYRECO', 4, 'WHL-ALLOY-15', 1, 'WH-CHN', NULL,  NULL,           200, 1, 'EA', '2026-04-01', 1),
    ('a0000000-0000-0000-0000-000000000001', 'TYRECO', 5, 'WHL-ALLOY-16', 1, 'WH-CHN', NULL,  NULL,           180, 1, 'EA', '2026-04-01', 1),
    ('a0000000-0000-0000-0000-000000000001', 'TYRECO', 1, 'TYR-RAD-195',  2, 'WH-BLR', 2,    'LOT-2026-Q2',  250, 1, 'EA', '2026-04-01', 1),
    ('a0000000-0000-0000-0000-000000000001', 'TYRECO', 2, 'TYR-RAD-205',  2, 'WH-BLR', NULL,  NULL,           100, 1, 'EA', '2026-04-01', 1),
    ('a0000000-0000-0000-0000-000000000001', 'TYRECO', 5, 'WHL-ALLOY-16', 2, 'WH-BLR', NULL,  NULL,           160, 1, 'EA', '2026-04-01', 1);


-- ============================================================================
-- 4. Staging: Transaction Inbox (Pipeline 2)
--    First 5: processed → rows in inv_transaction.
--    Last 3: still pending → waiting for drain.
-- ============================================================================

INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload, status, processed_at) VALUES

-- === PROCESSED (5 transactions) ===

('TYRECO', 'TXN-20260510-001',
 '{"tenant_code": "TYRECO", "external_txn_id": "TXN-20260510-001",
   "txn_type": "receipt", "product_code": "TYR-RAD-195", "warehouse_code": "WH-CHN",
   "lot_code": "LOT-2026-Q2", "signed_qty": 200, "uom_code": "EA", "source_system": "ERP",
   "posted_at": "2026-05-10T09:15:00+05:30", "reference": "PO-4521", "supplier": "MRF Ltd"}'::jsonb,
 'processed', '2026-05-10 09:20:00+05:30'),

('TYRECO', 'TXN-20260510-002',
 '{"tenant_code": "TYRECO", "external_txn_id": "TXN-20260510-002",
   "txn_type": "receipt", "product_code": "WHL-ALLOY-16", "warehouse_code": "WH-BLR",
   "signed_qty": 80, "uom_code": "EA", "source_system": "ERP",
   "posted_at": "2026-05-10T10:30:00+05:30", "reference": "PO-4522", "supplier": "Steel Strips Wheels"}'::jsonb,
 'processed', '2026-05-10 10:35:00+05:30'),

('TYRECO', 'TXN-20260511-001',
 '{"tenant_code": "TYRECO", "external_txn_id": "TXN-20260511-001",
   "txn_type": "issue", "product_code": "TYR-TRUCK-10", "warehouse_code": "WH-CHN",
   "lot_code": "LOT-2026-MAR", "signed_qty": -50, "uom_code": "EA", "source_system": "ERP",
   "posted_at": "2026-05-11T14:00:00+05:30", "reference": "WO-1100", "reason": "Fleet fitting job"}'::jsonb,
 'processed', '2026-05-11 14:05:00+05:30'),

('TYRECO', 'TXN-20260512-001',
 '{"tenant_code": "TYRECO", "external_txn_id": "TXN-20260512-001",
   "txn_type": "transfer_out", "product_code": "TYR-RAD-205", "warehouse_code": "WH-CHN",
   "signed_qty": -100, "uom_code": "EA", "source_system": "WMS",
   "posted_at": "2026-05-12T08:00:00+05:30", "transfer_pair_id": "b1111111-1111-1111-1111-111111111111",
   "reference": "STO-0050", "destination_warehouse": "WH-BLR"}'::jsonb,
 'processed', '2026-05-12 08:05:00+05:30'),

('TYRECO', 'TXN-20260512-002',
 '{"tenant_code": "TYRECO", "external_txn_id": "TXN-20260512-002",
   "txn_type": "transfer_in", "product_code": "TYR-RAD-205", "warehouse_code": "WH-BLR",
   "signed_qty": 100, "uom_code": "EA", "source_system": "WMS",
   "posted_at": "2026-05-12T08:00:00+05:30", "transfer_pair_id": "b1111111-1111-1111-1111-111111111111",
   "reference": "STO-0050", "source_warehouse": "WH-CHN"}'::jsonb,
 'processed', '2026-05-12 08:05:00+05:30'),

-- === PENDING (3 transactions — not yet drained, ready for process_txn_inbox) ===

('TYRECO', 'TXN-20260513-001',
 '{"tenant_code": "TYRECO", "external_txn_id": "TXN-20260513-001",
   "txn_type": "shipment", "product_code": "WHL-ALLOY-15", "warehouse_code": "WH-CHN",
   "signed_qty": -30, "uom_code": "EA", "source_system": "ERP",
   "posted_at": "2026-05-13T11:45:00+05:30", "reference": "DO-7890", "customer": "AutoZone India"}'::jsonb,
 'pending', NULL),

('TYRECO', 'TXN-20260514-001',
 '{"tenant_code": "TYRECO", "external_txn_id": "TXN-20260514-001",
   "txn_type": "scrap", "product_code": "TYR-RAD-195", "warehouse_code": "WH-BLR",
   "lot_code": "LOT-2026-Q2", "signed_qty": -5, "uom_code": "EA", "source_system": "QC",
   "posted_at": "2026-05-14T16:20:00+05:30", "reference": "SCR-0022", "reason": "Sidewall damage during storage"}'::jsonb,
 'pending', NULL),

('TYRECO', 'TXN-20260515-001',
 '{"tenant_code": "TYRECO", "external_txn_id": "TXN-20260515-001",
   "txn_type": "sales_return", "product_code": "WHL-ALLOY-16", "warehouse_code": "WH-CHN",
   "signed_qty": 10, "uom_code": "EA", "source_system": "ERP",
   "posted_at": "2026-05-15T09:00:00+05:30", "reference": "RMA-0315", "customer": "Wheel Mart",
   "return_reason": "Wrong size shipped"}'::jsonb,
 'pending', NULL);


-- ============================================================================
-- 5. Live: Inventory Transactions (result of processing first 5 txn_inbox rows)
--    sign convention: receipt/transfer_in > 0, issue/transfer_out < 0
-- ============================================================================

INSERT INTO inv_transaction
    (inv_transaction_id, tenant_id, tenant_code, external_txn_id,
     product_id, product_code, warehouse_id, warehouse_code,
     lot_id, lot_code, signed_qty, uom_id, uom_code,
     txn_type, transfer_pair_id, source_system, posted_at, payload)
OVERRIDING SYSTEM VALUE VALUES

(1, 'a0000000-0000-0000-0000-000000000001', 'TYRECO', 'TXN-20260510-001',
    1, 'TYR-RAD-195', 1, 'WH-CHN',
    2, 'LOT-2026-Q2', 200, 1, 'EA',
    'receipt', NULL, 'ERP', '2026-05-10 09:15:00+05:30',
    '{"reference": "PO-4521", "supplier": "MRF Ltd"}'::jsonb),

(2, 'a0000000-0000-0000-0000-000000000001', 'TYRECO', 'TXN-20260510-002',
    5, 'WHL-ALLOY-16', 2, 'WH-BLR',
    NULL, NULL, 80, 1, 'EA',
    'receipt', NULL, 'ERP', '2026-05-10 10:30:00+05:30',
    '{"reference": "PO-4522", "supplier": "Steel Strips Wheels"}'::jsonb),

(3, 'a0000000-0000-0000-0000-000000000001', 'TYRECO', 'TXN-20260511-001',
    3, 'TYR-TRUCK-10', 1, 'WH-CHN',
    3, 'LOT-2026-MAR', -50, 1, 'EA',
    'issue', NULL, 'ERP', '2026-05-11 14:00:00+05:30',
    '{"reference": "WO-1100", "reason": "Fleet fitting job"}'::jsonb),

(4, 'a0000000-0000-0000-0000-000000000001', 'TYRECO', 'TXN-20260512-001',
    2, 'TYR-RAD-205', 1, 'WH-CHN',
    NULL, NULL, -100, 1, 'EA',
    'transfer_out', 'b1111111-1111-1111-1111-111111111111', 'WMS', '2026-05-12 08:00:00+05:30',
    '{"reference": "STO-0050", "destination_warehouse": "WH-BLR"}'::jsonb),

(5, 'a0000000-0000-0000-0000-000000000001', 'TYRECO', 'TXN-20260512-002',
    2, 'TYR-RAD-205', 2, 'WH-BLR',
    NULL, NULL, 100, 1, 'EA',
    'transfer_in', 'b1111111-1111-1111-1111-111111111111', 'WMS', '2026-05-12 08:00:00+05:30',
    '{"reference": "STO-0050", "source_warehouse": "WH-CHN"}'::jsonb);

SELECT setval(pg_get_serial_sequence('inv_transaction_default', 'inv_transaction_id'), 5);


-- ============================================================================
-- 6. Stock Balance — auto-populated by triggers from opening_balance + inv_transaction.
--    No direct inserts needed. Expected final balances:
--
--    Product          | Warehouse | Lot          |  OB  | Txns | Balance
--    -----------------+-----------+--------------+------+------+--------
--    TYR-RAD-195      | WH-CHN    | LOT-2026-Q1  |  500 |    0 |    500
--    TYR-RAD-195      | WH-CHN    | LOT-2026-Q2  |    0 | +200 |    200
--    TYR-RAD-195      | WH-BLR    | LOT-2026-Q2  |  250 |    0 |    250
--    TYR-RAD-205      | WH-CHN    | (none)       |  320 | -100 |    220
--    TYR-RAD-205      | WH-BLR    | (none)       |  100 | +100 |    200
--    TYR-TRUCK-10     | WH-CHN    | LOT-2026-MAR |  150 |  -50 |    100
--    WHL-ALLOY-15     | WH-CHN    | (none)       |  200 |    0 |    200
--    WHL-ALLOY-16     | WH-CHN    | (none)       |  180 |    0 |    180
--    WHL-ALLOY-16     | WH-BLR    | (none)       |  160 |  +80 |    240
-- ============================================================================


-- ============================================================================
-- 7. Staging: Order Inbox (Pipeline 3)
--    First 2 orders: processed → rows in sfdc_order + sfdc_order_line.
--    Last order: still pending.
-- ============================================================================

INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload, status, processed_at) VALUES

('TYRECO', 'SFDC-ORD-50001',
 '{"tenant_code": "TYRECO", "sfdc_order_id": "SFDC-ORD-50001",
   "customer_id": "CUST-AZ-001", "customer_name": "AutoZone India", "order_date": "2026-05-16",
   "lines": [
     {"line_no": 1, "product_code": "TYR-RAD-195",  "warehouse_code": "WH-CHN", "qty": 100, "uom_code": "EA"},
     {"line_no": 2, "product_code": "WHL-ALLOY-15", "warehouse_code": "WH-CHN", "qty": 50,  "uom_code": "EA"},
     {"line_no": 3, "product_code": "TYR-RAD-205",  "warehouse_code": "WH-BLR", "qty": 60,  "uom_code": "EA"}
   ]}'::jsonb,
 'processed', '2026-05-16 12:00:00+05:30'),

('TYRECO', 'SFDC-ORD-50002',
 '{"tenant_code": "TYRECO", "sfdc_order_id": "SFDC-ORD-50002",
   "customer_id": "CUST-FL-010", "customer_name": "BlueDart Fleet Services", "order_date": "2026-05-17",
   "lines": [
     {"line_no": 1, "product_code": "TYR-TRUCK-10", "warehouse_code": "WH-CHN", "qty": 40, "uom_code": "EA"}
   ]}'::jsonb,
 'processed', '2026-05-17 09:00:00+05:30'),

-- Still pending — ready for process_order_inbox
('TYRECO', 'SFDC-ORD-50003',
 '{"tenant_code": "TYRECO", "sfdc_order_id": "SFDC-ORD-50003",
   "customer_id": "CUST-WM-005", "customer_name": "Wheel Mart", "order_date": "2026-05-18",
   "lines": [
     {"line_no": 1, "product_code": "WHL-ALLOY-16", "warehouse_code": "WH-CHN", "qty": 20,  "uom_code": "EA"},
     {"line_no": 2, "product_code": "WHL-ALLOY-16", "warehouse_code": "WH-BLR", "qty": 30,  "uom_code": "EA"},
     {"line_no": 3, "product_code": "WHL-ALLOY-15", "warehouse_code": "WH-CHN", "qty": 25,  "uom_code": "EA"}
   ]}'::jsonb,
 'pending', NULL);


-- ============================================================================
-- 8. Live: SFDC Orders + Lines (result of processing first 2 order_inbox rows)
-- ============================================================================

INSERT INTO sfdc_order
    (sfdc_order_id, tenant_id, tenant_code, customer_id, order_state, payload)
VALUES
    ('SFDC-ORD-50001', 'a0000000-0000-0000-0000-000000000001', 'TYRECO', 'CUST-AZ-001', 'open',
     '{"customer_name": "AutoZone India", "order_date": "2026-05-16"}'::jsonb),
    ('SFDC-ORD-50002', 'a0000000-0000-0000-0000-000000000001', 'TYRECO', 'CUST-FL-010', 'open',
     '{"customer_name": "BlueDart Fleet Services", "order_date": "2026-05-17"}'::jsonb);

INSERT INTO sfdc_order_line
    (sfdc_order_id, line_no, tenant_id, tenant_code,
     product_id, product_code, warehouse_id, warehouse_code,
     qty, uom_id, uom_code, line_state, payload)
VALUES
    ('SFDC-ORD-50001', 1, 'a0000000-0000-0000-0000-000000000001', 'TYRECO',
     1, 'TYR-RAD-195', 1, 'WH-CHN', 100, 1, 'EA', 'open',
     '{"customer": "AutoZone India"}'::jsonb),
    ('SFDC-ORD-50001', 2, 'a0000000-0000-0000-0000-000000000001', 'TYRECO',
     4, 'WHL-ALLOY-15', 1, 'WH-CHN', 50, 1, 'EA', 'open',
     '{"customer": "AutoZone India"}'::jsonb),
    ('SFDC-ORD-50001', 3, 'a0000000-0000-0000-0000-000000000001', 'TYRECO',
     2, 'TYR-RAD-205', 2, 'WH-BLR', 60, 1, 'EA', 'open',
     '{"customer": "AutoZone India"}'::jsonb),
    ('SFDC-ORD-50002', 1, 'a0000000-0000-0000-0000-000000000001', 'TYRECO',
     3, 'TYR-TRUCK-10', 1, 'WH-CHN', 40, 1, 'EA', 'open',
     '{"customer": "BlueDart Fleet Services"}'::jsonb);

COMMIT;
