-- ============================================================================
-- test_v6_comprehensive.sql
-- ============================================================================
-- Test-driven validation of v6 (run in pgAdmin Query Tool or psql).
--
-- ─── TEST CASES ─────────────────────────────────────────────────────────────
--   TC1  Opening balance lands in stock_balance with correct on_hand
--   TC2  Order INSERT reserves qty on the matching subinv
--   TC3  Multiple lines on same subinv sum into one reservation
--   TC4  Same product on different subinvs tracked independently
--   TC5  Different products are independent
--   TC6  Partial shipment: on_hand AND reserved both decrement; line stays
--        open; ATP stable
--   TC7  Full shipment in single txn: line auto-closes; reservation released
--   TC8  Line-precise cascade: only the line matching erp_line_id is touched
--   TC9  All staging rows reach terminal 'processed' status
--   TC10 Idempotency: re-inserting same external_txn_id is a no-op on
--        processed.inv_transaction
--
-- ─── DATA MODEL ─────────────────────────────────────────────────────────────
--   3 products: PROD-A, PROD-B, PROD-C
--   1 warehouse: WH-TEST
--   2 subinventories used: ONHAND, STAGING
--   Opening balance:
--     PROD-A / ONHAND   = 100
--     PROD-A / STAGING  =  20
--     PROD-B / ONHAND   =  50
--     PROD-C / ONHAND   = 200
--
-- ─── ORDERS ─────────────────────────────────────────────────────────────────
--   ORD-001  →  PROD-A ONHAND qty=10                      (open, no ship)
--   ORD-002  →  2 lines on PROD-A ONHAND
--                L1 qty=5 (partial ship 2)
--                L2 qty=3 (full ship 3)
--   ORD-003  →  PROD-B ONHAND qty=15 (full ship 15)
--   ORD-004  →  PROD-C ONHAND qty=50 (open, no ship)
--   ORD-005  →  PROD-A STAGING qty=8 (partial ship 5)
--
-- ─── EXPECTED FINAL STATE ───────────────────────────────────────────────────
--   PROD-A / ONHAND     on=95   res=13  atp=82
--   PROD-A / STAGING    on=15   res= 3  atp=12
--   PROD-B / ONHAND     on=35   res= 0  atp=35
--   PROD-C / ONHAND     on=200  res=50  atp=150
-- ============================================================================

-- ── Defensive cleanup of any prior run of this script ──────────────────────
BEGIN;
DELETE FROM staging.txn_inbox       WHERE external_txn_id LIKE 'TEST-TXN-%';
DELETE FROM staging.order_inbox     WHERE sfdc_order_id   LIKE 'TEST-ORD-%';
DELETE FROM processed.inv_transaction WHERE external_txn_id LIKE 'TEST-TXN-%';
DELETE FROM processed.sfdc_order    WHERE sfdc_order_id   LIKE 'TEST-ORD-%';
DELETE FROM processed.opening_balance WHERE source_file = 'comprehensive_v6_test';
DELETE FROM processed.stock_balance WHERE product_code IN ('PROD-A','PROD-B','PROD-C');
DELETE FROM processed.product       WHERE product_code IN ('PROD-A','PROD-B','PROD-C');
DELETE FROM processed.warehouse     WHERE warehouse_code = 'WH-TEST';
COMMIT;

-- ----------------------------------------------------------------------------
-- 1. MASTERS
-- ----------------------------------------------------------------------------
INSERT INTO processed.product (tenant_id, product_code, name)
SELECT t.tenant_id, code, code
  FROM processed.tenant t
  CROSS JOIN (VALUES ('PROD-A'),('PROD-B'),('PROD-C')) v(code)
 WHERE t.tenant_code = 'IFOPEUR';

INSERT INTO processed.warehouse (tenant_id, warehouse_code, name)
SELECT t.tenant_id, 'WH-TEST', 'WH-TEST'
  FROM processed.tenant t WHERE t.tenant_code = 'IFOPEUR';

INSERT INTO processed.uom (tenant_id, uom_code, name)
SELECT t.tenant_id, 'EA', 'EA'
  FROM processed.tenant t WHERE t.tenant_code = 'IFOPEUR'
ON CONFLICT (tenant_id, uom_code) DO NOTHING;

-- ----------------------------------------------------------------------------
-- 2. OPENING BALANCE — gives us stock to reserve / ship against
-- ----------------------------------------------------------------------------
INSERT INTO processed.opening_balance
    (tenant_id, tenant_code, product_id, product_code,
     warehouse_id, warehouse_code, subinventory, stock_status,
     qty, uom_id, uom_code, as_of_date, batch_id, source_file)
SELECT t.tenant_id, 'IFOPEUR', p.product_id, p.product_code,
       w.warehouse_id, 'WH-TEST', subinv, 'LIBERATED',
       qty, u.uom_id, 'EA', CURRENT_DATE, 1, 'comprehensive_v6_test'
  FROM processed.tenant t
  JOIN processed.warehouse w ON w.tenant_id = t.tenant_id AND w.warehouse_code = 'WH-TEST'
  JOIN processed.uom u       ON u.tenant_id = t.tenant_id AND u.uom_code = 'EA'
  JOIN processed.product p   ON p.tenant_id = t.tenant_id
  JOIN (VALUES
        ('PROD-A','ONHAND',  100),
        ('PROD-A','STAGING',  20),
        ('PROD-B','ONHAND',   50),
        ('PROD-C','ONHAND',  200)
       ) AS s(prod, subinv, qty) ON s.prod = p.product_code
 WHERE t.tenant_code = 'IFOPEUR';

-- ----------------------------------------------------------------------------
-- 3. ORDERS (the triggers will auto-promote and reserve)
-- ----------------------------------------------------------------------------
-- ORD-001: PROD-A ONHAND qty=10 (just placed, no ship)
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','TEST-ORD-001', jsonb_build_object(
    'customer_id','ACME','order_state','open','erp_external_id','ERP-HDR-001',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','PROD-A','warehouse_code','WH-TEST','subinventory','ONHAND',
        'erp_external_id','ERP-LINE-001-1','qty',10,'uom_code','EA','line_state','open'))));

-- ORD-002: 2 lines on PROD-A ONHAND (L1 qty=5 will partial ship; L2 qty=3 will fully ship)
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','TEST-ORD-002', jsonb_build_object(
    'customer_id','ACME','order_state','open','erp_external_id','ERP-HDR-002',
    'lines', jsonb_build_array(
        jsonb_build_object('product_code','PROD-A','warehouse_code','WH-TEST','subinventory','ONHAND',
                           'erp_external_id','ERP-LINE-002-1','qty',5,'uom_code','EA','line_state','open'),
        jsonb_build_object('product_code','PROD-A','warehouse_code','WH-TEST','subinventory','ONHAND',
                           'erp_external_id','ERP-LINE-002-2','qty',3,'uom_code','EA','line_state','open'))));

-- ORD-003: PROD-B ONHAND qty=15 (will fully ship)
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','TEST-ORD-003', jsonb_build_object(
    'customer_id','ACME','order_state','open','erp_external_id','ERP-HDR-003',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','PROD-B','warehouse_code','WH-TEST','subinventory','ONHAND',
        'erp_external_id','ERP-LINE-003-1','qty',15,'uom_code','EA','line_state','open'))));

-- ORD-004: PROD-C ONHAND qty=50 (open, no ship)
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','TEST-ORD-004', jsonb_build_object(
    'customer_id','ACME','order_state','open','erp_external_id','ERP-HDR-004',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','PROD-C','warehouse_code','WH-TEST','subinventory','ONHAND',
        'erp_external_id','ERP-LINE-004-1','qty',50,'uom_code','EA','line_state','open'))));

-- ORD-005: PROD-A STAGING qty=8 (partial ship 5)
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','TEST-ORD-005', jsonb_build_object(
    'customer_id','ACME','order_state','open','erp_external_id','ERP-HDR-005',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','PROD-A','warehouse_code','WH-TEST','subinventory','STAGING',
        'erp_external_id','ERP-LINE-005-1','qty',8,'uom_code','EA','line_state','open'))));

-- ----------------------------------------------------------------------------
-- 4. SHIPMENT TRANSACTIONS (auto-cascade via erp_line_id match)
-- ----------------------------------------------------------------------------
-- TXN-001: -2 PROD-A ONHAND against ORD-002 L1 (partial — line stays open)
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','TEST-TXN-001', jsonb_build_object(
    'product_code','PROD-A','warehouse_code','WH-TEST','subinventory','ONHAND',
    'signed_qty',-2,'uom_code','EA','txn_type','shipment',
    'erp_line_id','ERP-LINE-002-1','erp_header_id','ERP-HDR-002'));

-- TXN-002: -3 PROD-A ONHAND against ORD-002 L2 (full — line closes)
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','TEST-TXN-002', jsonb_build_object(
    'product_code','PROD-A','warehouse_code','WH-TEST','subinventory','ONHAND',
    'signed_qty',-3,'uom_code','EA','txn_type','shipment',
    'erp_line_id','ERP-LINE-002-2','erp_header_id','ERP-HDR-002'));

-- TXN-003: -15 PROD-B ONHAND against ORD-003 (full — line closes)
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','TEST-TXN-003', jsonb_build_object(
    'product_code','PROD-B','warehouse_code','WH-TEST','subinventory','ONHAND',
    'signed_qty',-15,'uom_code','EA','txn_type','shipment',
    'erp_line_id','ERP-LINE-003-1','erp_header_id','ERP-HDR-003'));

-- TXN-004: -5 PROD-A STAGING against ORD-005 (partial — line stays open)
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','TEST-TXN-004', jsonb_build_object(
    'product_code','PROD-A','warehouse_code','WH-TEST','subinventory','STAGING',
    'signed_qty',-5,'uom_code','EA','txn_type','shipment',
    'erp_line_id','ERP-LINE-005-1','erp_header_id','ERP-HDR-005'));

-- TC10 idempotency: re-insert TXN-001 with same external_txn_id, expect no double-effect
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','TEST-TXN-001', jsonb_build_object(
    'product_code','PROD-A','warehouse_code','WH-TEST','subinventory','ONHAND',
    'signed_qty',-2,'uom_code','EA','txn_type','shipment',
    'erp_line_id','ERP-LINE-002-1','erp_header_id','ERP-HDR-002'));

-- ============================================================================
-- VALIDATION — one result set, one row per test case
-- ============================================================================
SELECT 'TC1a' AS test, 'PROD-A/ONHAND  on=95 res=13 atp=82' AS expected,
       CASE WHEN on_hand_qty=95 AND reserved_qty=13 THEN 'PASS'
            ELSE 'FAIL: on='||on_hand_qty||' res='||reserved_qty END AS result
  FROM processed.stock_balance WHERE product_code='PROD-A' AND subinventory='ONHAND'
UNION ALL
SELECT 'TC1b', 'PROD-A/STAGING on=15 res=3  atp=12',
       CASE WHEN on_hand_qty=15 AND reserved_qty=3 THEN 'PASS'
            ELSE 'FAIL: on='||on_hand_qty||' res='||reserved_qty END
  FROM processed.stock_balance WHERE product_code='PROD-A' AND subinventory='STAGING'
UNION ALL
SELECT 'TC1c', 'PROD-B/ONHAND  on=35 res=0  atp=35',
       CASE WHEN on_hand_qty=35 AND reserved_qty=0 THEN 'PASS'
            ELSE 'FAIL: on='||on_hand_qty||' res='||reserved_qty END
  FROM processed.stock_balance WHERE product_code='PROD-B' AND subinventory='ONHAND'
UNION ALL
SELECT 'TC1d', 'PROD-C/ONHAND  on=200 res=50 atp=150',
       CASE WHEN on_hand_qty=200 AND reserved_qty=50 THEN 'PASS'
            ELSE 'FAIL: on='||on_hand_qty||' res='||reserved_qty END
  FROM processed.stock_balance WHERE product_code='PROD-C' AND subinventory='ONHAND'

UNION ALL
SELECT 'TC2', 'ORD-001 line: shipped=0, open',
       CASE WHEN shipped_qty=0 AND line_state='open' THEN 'PASS'
            ELSE 'FAIL: shipped='||shipped_qty||' state='||line_state END
  FROM processed.sfdc_order_line WHERE sfdc_order_id='TEST-ORD-001'

UNION ALL
SELECT 'TC6', 'ORD-002 L1 (partial): shipped=2, open',
       CASE WHEN shipped_qty=2 AND line_state='open' THEN 'PASS'
            ELSE 'FAIL: shipped='||shipped_qty||' state='||line_state END
  FROM processed.sfdc_order_line WHERE erp_external_id='ERP-LINE-002-1'
UNION ALL
SELECT 'TC7a', 'ORD-002 L2 (full): shipped=3, closed',
       CASE WHEN shipped_qty=3 AND line_state='closed' THEN 'PASS'
            ELSE 'FAIL: shipped='||shipped_qty||' state='||line_state END
  FROM processed.sfdc_order_line WHERE erp_external_id='ERP-LINE-002-2'
UNION ALL
SELECT 'TC7b', 'ORD-003 (full): shipped=15, closed',
       CASE WHEN shipped_qty=15 AND line_state='closed' THEN 'PASS'
            ELSE 'FAIL: shipped='||shipped_qty||' state='||line_state END
  FROM processed.sfdc_order_line WHERE erp_external_id='ERP-LINE-003-1'

UNION ALL
SELECT 'TC8', 'ORD-002 L1 shipped, L2 closed — neither side-effects ORD-001/004 (still shipped=0)',
       CASE WHEN (SELECT SUM(shipped_qty) FROM processed.sfdc_order_line
                   WHERE sfdc_order_id IN ('TEST-ORD-001','TEST-ORD-004')) = 0
            THEN 'PASS' ELSE 'FAIL' END

UNION ALL
SELECT 'TC9a', 'staging.order_inbox: 5 rows, all processed',
       CASE WHEN COUNT(*) FILTER (WHERE status='processed') = 5
                 AND COUNT(*) FILTER (WHERE status<>'processed') = 0
            THEN 'PASS'
            ELSE 'FAIL: '|| COUNT(*) FILTER (WHERE status='processed') ||' processed of '||COUNT(*)::text END
  FROM staging.order_inbox WHERE sfdc_order_id LIKE 'TEST-ORD-%'
UNION ALL
SELECT 'TC9b', 'staging.txn_inbox: 5 rows (4 unique + 1 idem retry), all processed',
       CASE WHEN COUNT(*) FILTER (WHERE status='processed') = 5
            THEN 'PASS'
            ELSE 'FAIL: '|| COUNT(*) FILTER (WHERE status='processed') ||' processed of '||COUNT(*)::text END
  FROM staging.txn_inbox WHERE external_txn_id LIKE 'TEST-TXN-%'

UNION ALL
SELECT 'TC10', 'processed.inv_transaction has only 4 rows (idempotency guard worked)',
       CASE WHEN COUNT(*) = 4 THEN 'PASS' ELSE 'FAIL: count='||COUNT(*)::text END
  FROM processed.inv_transaction WHERE external_txn_id LIKE 'TEST-TXN-%'

ORDER BY 1;

-- ============================================================================
-- DETAIL DUMPS (handy for debugging if anything fails above)
-- ============================================================================
SELECT '== stock_balance ==' AS section, NULL::text AS unused;
SELECT product_code, subinventory, on_hand_qty, reserved_qty,
       on_hand_qty - reserved_qty AS atp
  FROM processed.stock_balance
 WHERE product_code IN ('PROD-A','PROD-B','PROD-C')
 ORDER BY 1, 2;

SELECT '== sfdc_order_line ==' AS section, NULL::text AS unused;
SELECT sfdc_order_id, line_no, erp_external_id, product_code, subinventory,
       qty, shipped_qty, line_state
  FROM processed.sfdc_order_line
 WHERE sfdc_order_id LIKE 'TEST-ORD-%'
 ORDER BY 1, 2;

SELECT '== inv_transaction ==' AS section, NULL::text AS unused;
SELECT external_txn_id, product_code, subinventory, signed_qty,
       erp_line_id, erp_header_id
  FROM processed.inv_transaction
 WHERE external_txn_id LIKE 'TEST-TXN-%'
 ORDER BY 1;

-- ============================================================================
-- CLEANUP (uncomment to undo this test run)
-- ============================================================================
-- BEGIN;
-- DELETE FROM staging.txn_inbox         WHERE external_txn_id LIKE 'TEST-TXN-%';
-- DELETE FROM staging.order_inbox       WHERE sfdc_order_id   LIKE 'TEST-ORD-%';
-- DELETE FROM processed.inv_transaction WHERE external_txn_id LIKE 'TEST-TXN-%';
-- DELETE FROM processed.sfdc_order      WHERE sfdc_order_id   LIKE 'TEST-ORD-%';
-- DELETE FROM processed.opening_balance WHERE source_file = 'comprehensive_v6_test';
-- DELETE FROM processed.stock_balance   WHERE product_code IN ('PROD-A','PROD-B','PROD-C');
-- DELETE FROM processed.product         WHERE product_code IN ('PROD-A','PROD-B','PROD-C');
-- DELETE FROM processed.warehouse       WHERE warehouse_code = 'WH-TEST';
-- COMMIT;
