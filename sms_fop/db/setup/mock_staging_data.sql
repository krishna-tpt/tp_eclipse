-- mock_staging_data.sql
--
-- Synthetic test data for staging tables: ob_load/ob_load_batch, txn_inbox,
-- order_inbox.  Scenario: a tyre & wheel distributor ("TyreCo") with two
-- warehouses (Chennai, Bangalore) and five products.
--
-- Prerequisites: run inventoryledger_schema.sql first, then this script.
--     psql -d inventoryledger -f inventoryledger_schema.sql
--     psql -d inventoryledger -f mock_staging_data.sql
--
-- Master data is seeded here for testing. In production, drain functions
-- auto-populate masters on first encounter — no pre-setup needed.

BEGIN;

-- ============================================================================
-- 1. Master data seed (for test only — auto-populated in production)
-- ============================================================================

INSERT INTO tenant (tenant_id, tenant_code, name)
VALUES ('a0000000-0000-0000-0000-000000000001', 'TYRECO', 'TyreCo Distributors Pvt Ltd');

INSERT INTO product (tenant_id, product_code, name) VALUES
    ('a0000000-0000-0000-0000-000000000001', 'TYR-RAD-195',  'Radial Tyre 195/65 R15'),
    ('a0000000-0000-0000-0000-000000000001', 'TYR-RAD-205',  'Radial Tyre 205/55 R16'),
    ('a0000000-0000-0000-0000-000000000001', 'TYR-TRUCK-10', 'Truck Tyre 10.00 R20'),
    ('a0000000-0000-0000-0000-000000000001', 'WHL-ALLOY-15', 'Alloy Wheel 15 inch'),
    ('a0000000-0000-0000-0000-000000000001', 'WHL-ALLOY-16', 'Alloy Wheel 16 inch');

INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES
    ('a0000000-0000-0000-0000-000000000001', 'WH-CHN', 'Chennai Central Warehouse'),
    ('a0000000-0000-0000-0000-000000000001', 'WH-BLR', 'Bangalore Hub');

INSERT INTO uom (tenant_id, uom_code, name) VALUES
    ('a0000000-0000-0000-0000-000000000001', 'EA',  'Each'),
    ('a0000000-0000-0000-0000-000000000001', 'SET', 'Set of 4');

INSERT INTO lot (tenant_id, product_id, lot_code) VALUES
    ('a0000000-0000-0000-0000-000000000001',
     (SELECT product_id FROM product WHERE product_code = 'TYR-RAD-195'),
     'LOT-2026-Q1'),
    ('a0000000-0000-0000-0000-000000000001',
     (SELECT product_id FROM product WHERE product_code = 'TYR-RAD-195'),
     'LOT-2026-Q2'),
    ('a0000000-0000-0000-0000-000000000001',
     (SELECT product_id FROM product WHERE product_code = 'TYR-TRUCK-10'),
     'LOT-2026-MAR');


-- ============================================================================
-- 2. Opening Balance — ob_load_batch + ob_load (Pipeline 1)
--    Simulates a CSV file with 8 rows of starting stock as of 2026-04-01.
--    All columns are flat text — exactly what a CSV parser would land.
-- ============================================================================

INSERT INTO staging.ob_load_batch (tenant_code, file_name, file_hash, status, row_count)
VALUES (
    'TYRECO',
    'tyreco_opening_stock_20260401.csv',
    'sha256:ab3f7c0e9d1245a6b8c3d4e5f60718293a4b5c6d7e8f901234567890abcdef12',
    'in_progress',
    8
);

INSERT INTO staging.ob_load
    (batch_id, tenant_code, product_code, warehouse_code, lot_code, uom_code, qty, as_of_date, source_ref, line_no)
VALUES
    -- Chennai warehouse stock
    (1, 'TYRECO', 'TYR-RAD-195',  'WH-CHN', 'LOT-2026-Q1',  'EA', '500',  '2026-04-01', 'OB-CHN-001', 1),
    (1, 'TYRECO', 'TYR-RAD-205',  'WH-CHN', NULL,            'EA', '320',  '2026-04-01', 'OB-CHN-002', 2),
    (1, 'TYRECO', 'TYR-TRUCK-10', 'WH-CHN', 'LOT-2026-MAR', 'EA', '150',  '2026-04-01', 'OB-CHN-003', 3),
    (1, 'TYRECO', 'WHL-ALLOY-15', 'WH-CHN', NULL,            'EA', '200',  '2026-04-01', 'OB-CHN-004', 4),
    (1, 'TYRECO', 'WHL-ALLOY-16', 'WH-CHN', NULL,            'EA', '180',  '2026-04-01', 'OB-CHN-005', 5),
    -- Bangalore warehouse stock
    (1, 'TYRECO', 'TYR-RAD-195',  'WH-BLR', 'LOT-2026-Q2',  'EA', '250',  '2026-04-01', 'OB-BLR-001', 6),
    (1, 'TYRECO', 'TYR-RAD-205',  'WH-BLR', NULL,            'EA', '100',  '2026-04-01', 'OB-BLR-002', 7),
    (1, 'TYRECO', 'WHL-ALLOY-16', 'WH-BLR', NULL,            'SET', '40',  '2026-04-01', 'OB-BLR-003', 8);


-- ============================================================================
-- 3. Transaction Inbox — txn_inbox (Pipeline 2)
--    8 transactions: receipts, issues, a transfer pair, shipment, scrap,
--    and a sales return.
--    tenant_code is flat text — external systems never see UUIDs.
--    Each payload is the JSON the source system would POST.
-- ============================================================================

INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES

-- Receipt: 200 Radial 195 tyres received at Chennai from MRF
('TYRECO', 'TXN-20260510-001',
 '{"txn_type": "receipt",
   "product_code": "TYR-RAD-195",
   "warehouse_code": "WH-CHN",
   "lot_code": "LOT-2026-Q2",
   "qty": 200,
   "uom_code": "EA",
   "source_system": "ERP",
   "posted_at": "2026-05-10T09:15:00+05:30",
   "reference": "PO-4521",
   "supplier": "MRF Ltd"}'::jsonb),

-- Receipt: 80 Alloy Wheel 16" at Bangalore from Steel Strips
('TYRECO', 'TXN-20260510-002',
 '{"txn_type": "receipt",
   "product_code": "WHL-ALLOY-16",
   "warehouse_code": "WH-BLR",
   "qty": 80,
   "uom_code": "EA",
   "source_system": "ERP",
   "posted_at": "2026-05-10T10:30:00+05:30",
   "reference": "PO-4522",
   "supplier": "Steel Strips Wheels"}'::jsonb),

-- Issue: 50 Truck Tyres issued from Chennai for fleet fitting
('TYRECO', 'TXN-20260511-001',
 '{"txn_type": "issue",
   "product_code": "TYR-TRUCK-10",
   "warehouse_code": "WH-CHN",
   "lot_code": "LOT-2026-MAR",
   "qty": 50,
   "uom_code": "EA",
   "source_system": "ERP",
   "posted_at": "2026-05-11T14:00:00+05:30",
   "reference": "WO-1100",
   "reason": "Fleet fitting job"}'::jsonb),

-- Transfer out: 100 Radial 205 from Chennai -> Bangalore (leg 1 of pair)
('TYRECO', 'TXN-20260512-001',
 '{"txn_type": "transfer_out",
   "product_code": "TYR-RAD-205",
   "warehouse_code": "WH-CHN",
   "qty": 100,
   "uom_code": "EA",
   "source_system": "WMS",
   "posted_at": "2026-05-12T08:00:00+05:30",
   "transfer_pair_id": "b1111111-1111-1111-1111-111111111111",
   "reference": "STO-0050",
   "destination_warehouse": "WH-BLR"}'::jsonb),

-- Transfer in: 100 Radial 205 into Bangalore (leg 2 of pair, same transfer_pair_id)
('TYRECO', 'TXN-20260512-002',
 '{"txn_type": "transfer_in",
   "product_code": "TYR-RAD-205",
   "warehouse_code": "WH-BLR",
   "qty": 100,
   "uom_code": "EA",
   "source_system": "WMS",
   "posted_at": "2026-05-12T08:00:00+05:30",
   "transfer_pair_id": "b1111111-1111-1111-1111-111111111111",
   "reference": "STO-0050",
   "source_warehouse": "WH-CHN"}'::jsonb),

-- Shipment: 30 Alloy Wheel 15" shipped from Chennai to AutoZone
('TYRECO', 'TXN-20260513-001',
 '{"txn_type": "shipment",
   "product_code": "WHL-ALLOY-15",
   "warehouse_code": "WH-CHN",
   "qty": 30,
   "uom_code": "EA",
   "source_system": "ERP",
   "posted_at": "2026-05-13T11:45:00+05:30",
   "reference": "DO-7890",
   "customer": "AutoZone India"}'::jsonb),

-- Scrap: 5 damaged Radial 195 tyres scrapped at Bangalore
('TYRECO', 'TXN-20260514-001',
 '{"txn_type": "scrap",
   "product_code": "TYR-RAD-195",
   "warehouse_code": "WH-BLR",
   "lot_code": "LOT-2026-Q2",
   "qty": 5,
   "uom_code": "EA",
   "source_system": "QC",
   "posted_at": "2026-05-14T16:20:00+05:30",
   "reference": "SCR-0022",
   "reason": "Sidewall damage during storage"}'::jsonb),

-- Sales return: 10 Alloy Wheel 16" returned by Wheel Mart to Chennai
('TYRECO', 'TXN-20260515-001',
 '{"txn_type": "sales_return",
   "product_code": "WHL-ALLOY-16",
   "warehouse_code": "WH-CHN",
   "qty": 10,
   "uom_code": "EA",
   "source_system": "ERP",
   "posted_at": "2026-05-15T09:00:00+05:30",
   "reference": "RMA-0315",
   "customer": "Wheel Mart",
   "return_reason": "Wrong size shipped"}'::jsonb);


-- ============================================================================
-- 4. Order Inbox — order_inbox (Pipeline 3)
--    3 Salesforce orders from different customers, totalling 7 order lines.
--    tenant_code is flat text — external systems never see UUIDs.
-- ============================================================================

INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES

-- Order 1: AutoZone wants tyres + wheels (3 lines across 2 warehouses)
('TYRECO', 'SFDC-ORD-50001',
 '{"customer_id": "CUST-AZ-001",
   "customer_name": "AutoZone India",
   "order_date": "2026-05-16",
   "lines": [
     {"line_no": 1, "product_code": "TYR-RAD-195",  "warehouse_code": "WH-CHN", "qty": 100, "uom_code": "EA"},
     {"line_no": 2, "product_code": "WHL-ALLOY-15", "warehouse_code": "WH-CHN", "qty": 50,  "uom_code": "EA"},
     {"line_no": 3, "product_code": "TYR-RAD-205",  "warehouse_code": "WH-BLR", "qty": 60,  "uom_code": "EA"}
   ]}'::jsonb),

-- Order 2: Fleet customer wants truck tyres (1 line, large qty)
('TYRECO', 'SFDC-ORD-50002',
 '{"customer_id": "CUST-FL-010",
   "customer_name": "BlueDart Fleet Services",
   "order_date": "2026-05-17",
   "lines": [
     {"line_no": 1, "product_code": "TYR-TRUCK-10", "warehouse_code": "WH-CHN", "qty": 40, "uom_code": "EA"}
   ]}'::jsonb),

-- Order 3: Retail chain wants alloy wheels from both warehouses (3 lines)
('TYRECO', 'SFDC-ORD-50003',
 '{"customer_id": "CUST-WM-005",
   "customer_name": "Wheel Mart",
   "order_date": "2026-05-18",
   "lines": [
     {"line_no": 1, "product_code": "WHL-ALLOY-16", "warehouse_code": "WH-CHN", "qty": 20,  "uom_code": "EA"},
     {"line_no": 2, "product_code": "WHL-ALLOY-16", "warehouse_code": "WH-BLR", "qty": 30,  "uom_code": "EA"},
     {"line_no": 3, "product_code": "WHL-ALLOY-15", "warehouse_code": "WH-CHN", "qty": 25,  "uom_code": "SET"}
   ]}'::jsonb);

COMMIT;
