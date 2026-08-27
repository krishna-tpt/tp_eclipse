-- ============================================================================
-- test_v6_full_suite.sql
-- ============================================================================
-- Full v6 test pack: basic calc + edge + state transitions + negative + query.
--
-- Each test row produces: test_id, category, scenario, original, expected,
-- got, pass_fail. Final SELECT returns the whole pack ordered by category.
--
-- Tenants assumed pre-seeded by customer_install: IFOPEUR, MNA.
-- All other masters are created by this script under SIM_FULL_* names so it
-- can be re-run cleanly.
-- ============================================================================

-- ── 0. Defensive cleanup (idempotent) ───────────────────────────────────────
BEGIN;
DELETE FROM staging.txn_inbox       WHERE external_txn_id LIKE 'FS-TXN-%';
DELETE FROM staging.order_inbox     WHERE sfdc_order_id   LIKE 'FS-ORD-%';
DELETE FROM processed.inv_transaction WHERE external_txn_id LIKE 'FS-TXN-%';
DELETE FROM processed.sfdc_order_line WHERE sfdc_order_id LIKE 'FS-ORD-%';
DELETE FROM processed.sfdc_order      WHERE sfdc_order_id LIKE 'FS-ORD-%';
DELETE FROM processed.opening_balance WHERE source_file = 'fs_full_suite';
DELETE FROM processed.stock_balance WHERE product_code LIKE 'FS-%';
DELETE FROM processed.product       WHERE product_code LIKE 'FS-%';
DELETE FROM processed.warehouse     WHERE warehouse_code = 'WH-FS';
COMMIT;

-- ── 1. Masters ──────────────────────────────────────────────────────────────
INSERT INTO processed.product (tenant_id, product_code, name)
SELECT t.tenant_id, code, code
  FROM processed.tenant t
  CROSS JOIN (VALUES
      ('FS-BC-A'),('FS-BC-B'),('FS-BC-C'),
      ('FS-EC-NO-OB'),('FS-EC-EXACT'),('FS-EC-OVERSELL'),
      ('FS-EC-OVERSHIP'),('FS-EC-CANCELLED'),('FS-EC-EMPTY'),
      ('FS-ST-A')
   ) v(code)
 WHERE t.tenant_code = 'IFOPEUR';

INSERT INTO processed.warehouse (tenant_id, warehouse_code, name)
SELECT t.tenant_id, 'WH-FS', 'WH-FS'
  FROM processed.tenant t WHERE t.tenant_code = 'IFOPEUR';

INSERT INTO processed.uom (tenant_id, uom_code, name)
SELECT t.tenant_id, 'EA', 'EA'
  FROM processed.tenant t WHERE t.tenant_code = 'IFOPEUR'
ON CONFLICT (tenant_id, uom_code) DO NOTHING;

-- ── 2. Opening balance ──────────────────────────────────────────────────────
INSERT INTO processed.opening_balance
    (tenant_id, tenant_code, product_id, product_code,
     warehouse_id, warehouse_code, subinventory, stock_status,
     qty, uom_id, uom_code, as_of_date, batch_id, source_file)
SELECT t.tenant_id, 'IFOPEUR', p.product_id, p.product_code,
       w.warehouse_id, 'WH-FS', s.subinv, 'LIBERATED',
       s.qty, u.uom_id, 'EA', CURRENT_DATE, 1, 'fs_full_suite'
  FROM processed.tenant t
  JOIN processed.warehouse w ON w.tenant_id = t.tenant_id AND w.warehouse_code = 'WH-FS'
  JOIN processed.uom u       ON u.tenant_id = t.tenant_id AND u.uom_code = 'EA'
  JOIN processed.product p   ON p.tenant_id = t.tenant_id
  JOIN (VALUES
        ('FS-BC-A','ONHAND', 100),
        ('FS-BC-A','STAGING', 20),
        ('FS-BC-B','ONHAND',  50),
        ('FS-BC-C','ONHAND', 200),
        ('FS-EC-EXACT','ONHAND',    50),
        ('FS-EC-OVERSELL','ONHAND', 30),
        ('FS-EC-OVERSHIP','ONHAND', 20),
        ('FS-EC-CANCELLED','ONHAND',40),
        ('FS-EC-EMPTY','',          70),
        ('FS-ST-A','ONHAND',       100),
        ('FS-ST-A','STAGING',       50)
       ) s(prod, subinv, qty) ON s.prod = p.product_code
 WHERE t.tenant_code = 'IFOPEUR';

-- ── 3. Orders (auto-promoted by inbox trigger) ──────────────────────────────
-- Basic / reservation block
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-001', jsonb_build_object(
    'customer_id','C1','order_state','open','erp_external_id','FS-HDR-001',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-BC-A','warehouse_code','WH-FS','subinventory','ONHAND',
        'erp_external_id','FS-LINE-001-1','qty',10,'uom_code','EA','line_state','open'))));

INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-002', jsonb_build_object(
    'customer_id','C1','order_state','open','erp_external_id','FS-HDR-002',
    'lines', jsonb_build_array(
        jsonb_build_object('product_code','FS-BC-A','warehouse_code','WH-FS','subinventory','ONHAND',
                           'erp_external_id','FS-LINE-002-1','qty',5,'uom_code','EA','line_state','open'),
        jsonb_build_object('product_code','FS-BC-A','warehouse_code','WH-FS','subinventory','ONHAND',
                           'erp_external_id','FS-LINE-002-2','qty',3,'uom_code','EA','line_state','open'))));

INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-003', jsonb_build_object(
    'customer_id','C1','order_state','open','erp_external_id','FS-HDR-003',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-BC-B','warehouse_code','WH-FS','subinventory','ONHAND',
        'erp_external_id','FS-LINE-003-1','qty',15,'uom_code','EA','line_state','open'))));

INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-004', jsonb_build_object(
    'customer_id','C1','order_state','open','erp_external_id','FS-HDR-004',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-BC-C','warehouse_code','WH-FS','subinventory','ONHAND',
        'erp_external_id','FS-LINE-004-1','qty',50,'uom_code','EA','line_state','open'))));

INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-005', jsonb_build_object(
    'customer_id','C1','order_state','open','erp_external_id','FS-HDR-005',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-BC-A','warehouse_code','WH-FS','subinventory','STAGING',
        'erp_external_id','FS-LINE-005-1','qty',8,'uom_code','EA','line_state','open'))));

-- Edge-case orders
-- EC1: product with NO opening balance
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-EC1', jsonb_build_object(
    'customer_id','C2','order_state','open','erp_external_id','FS-HDR-EC1',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-EC-NO-OB','warehouse_code','WH-FS','subinventory','ONHAND',
        'erp_external_id','FS-LINE-EC1','qty',20,'uom_code','EA','line_state','open'))));

-- EC2: ATP=0 boundary (order exactly equals on_hand, no ship)
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-EC2', jsonb_build_object(
    'customer_id','C2','order_state','open','erp_external_id','FS-HDR-EC2',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-EC-EXACT','warehouse_code','WH-FS','subinventory','ONHAND',
        'erp_external_id','FS-LINE-EC2','qty',50,'uom_code','EA','line_state','open'))));

-- EC3: oversell (order > on_hand)
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-EC3', jsonb_build_object(
    'customer_id','C2','order_state','open','erp_external_id','FS-HDR-EC3',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-EC-OVERSELL','warehouse_code','WH-FS','subinventory','ONHAND',
        'erp_external_id','FS-LINE-EC3','qty',50,'uom_code','EA','line_state','open'))));

-- EC4: overship — order 20, then ship 25 (more than on_hand of 20)
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-EC4', jsonb_build_object(
    'customer_id','C2','order_state','open','erp_external_id','FS-HDR-EC4',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-EC-OVERSHIP','warehouse_code','WH-FS','subinventory','ONHAND',
        'erp_external_id','FS-LINE-EC4','qty',20,'uom_code','EA','line_state','open'))));

-- EC5: line_state='cancelled' on insert (no reservation should occur)
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-EC5', jsonb_build_object(
    'customer_id','C2','order_state','open','erp_external_id','FS-HDR-EC5',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-EC-CANCELLED','warehouse_code','WH-FS','subinventory','ONHAND',
        'erp_external_id','FS-LINE-EC5','qty',10,'uom_code','EA','line_state','cancelled'))));

-- EC6: empty subinventory string (default)
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-EC6', jsonb_build_object(
    'customer_id','C2','order_state','open','erp_external_id','FS-HDR-EC6',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-EC-EMPTY','warehouse_code','WH-FS','subinventory','',
        'erp_external_id','FS-LINE-EC6','qty',15,'uom_code','EA','line_state','open'))));

-- State-transition orders (ST-A)
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-ST1', jsonb_build_object(
    'customer_id','C3','order_state','open','erp_external_id','FS-HDR-ST1',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-ST-A','warehouse_code','WH-FS','subinventory','ONHAND',
        'erp_external_id','FS-LINE-ST1','qty',10,'uom_code','EA','line_state','open'))));

INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-ST4', jsonb_build_object(
    'customer_id','C3','order_state','open','erp_external_id','FS-HDR-ST4',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-ST-A','warehouse_code','WH-FS','subinventory','ONHAND',
        'erp_external_id','FS-LINE-ST4','qty',12,'uom_code','EA','line_state','open'))));

INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-ST5', jsonb_build_object(
    'customer_id','C3','order_state','open','erp_external_id','FS-HDR-ST5',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-ST-A','warehouse_code','WH-FS','subinventory','ONHAND',
        'erp_external_id','FS-LINE-ST5','qty',20,'uom_code','EA','line_state','open'))));

-- Negative-case orders (these are EXPECTED to be rejected by promote_one_order)
-- NC1: unknown tenant
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('NOT_A_TENANT','FS-ORD-NC1', jsonb_build_object(
    'customer_id','X','order_state','open','erp_external_id','FS-HDR-NC1',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-BC-A','warehouse_code','WH-FS','subinventory','ONHAND',
        'erp_external_id','FS-LINE-NC1','qty',1,'uom_code','EA','line_state','open'))));

-- NC2: unknown product
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-NC2', jsonb_build_object(
    'customer_id','X','order_state','open','erp_external_id','FS-HDR-NC2',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-DOES-NOT-EXIST','warehouse_code','WH-FS','subinventory','ONHAND',
        'erp_external_id','FS-LINE-NC2','qty',1,'uom_code','EA','line_state','open'))));

-- NC3: unknown warehouse
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-NC3', jsonb_build_object(
    'customer_id','X','order_state','open','erp_external_id','FS-HDR-NC3',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-BC-A','warehouse_code','WH-DOES-NOT-EXIST','subinventory','ONHAND',
        'erp_external_id','FS-LINE-NC3','qty',1,'uom_code','EA','line_state','open'))));

-- NC4: unknown UOM
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','FS-ORD-NC4', jsonb_build_object(
    'customer_id','X','order_state','open','erp_external_id','FS-HDR-NC4',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','FS-BC-A','warehouse_code','WH-FS','subinventory','ONHAND',
        'erp_external_id','FS-LINE-NC4','qty',1,'uom_code','XYZ','line_state','open'))));

-- ── 4. Shipment transactions ────────────────────────────────────────────────
-- SC: partial against ORD-002 L1
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','FS-TXN-001', jsonb_build_object(
    'product_code','FS-BC-A','warehouse_code','WH-FS','subinventory','ONHAND',
    'signed_qty',-2,'uom_code','EA','txn_type','shipment',
    'erp_line_id','FS-LINE-002-1','erp_header_id','FS-HDR-002'));

-- SC: full against ORD-002 L2
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','FS-TXN-002', jsonb_build_object(
    'product_code','FS-BC-A','warehouse_code','WH-FS','subinventory','ONHAND',
    'signed_qty',-3,'uom_code','EA','txn_type','shipment',
    'erp_line_id','FS-LINE-002-2','erp_header_id','FS-HDR-002'));

-- SC: full against ORD-003
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','FS-TXN-003', jsonb_build_object(
    'product_code','FS-BC-B','warehouse_code','WH-FS','subinventory','ONHAND',
    'signed_qty',-15,'uom_code','EA','txn_type','shipment',
    'erp_line_id','FS-LINE-003-1','erp_header_id','FS-HDR-003'));

-- SC: partial against ORD-005 (STAGING)
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','FS-TXN-004', jsonb_build_object(
    'product_code','FS-BC-A','warehouse_code','WH-FS','subinventory','STAGING',
    'signed_qty',-5,'uom_code','EA','txn_type','shipment',
    'erp_line_id','FS-LINE-005-1','erp_header_id','FS-HDR-005'));

-- EC4: overship (25 against a line of 20)
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','FS-TXN-EC4', jsonb_build_object(
    'product_code','FS-EC-OVERSHIP','warehouse_code','WH-FS','subinventory','ONHAND',
    'signed_qty',-25,'uom_code','EA','txn_type','shipment',
    'erp_line_id','FS-LINE-EC4','erp_header_id','FS-HDR-EC4'));

-- Idempotency: re-insert TXN-001 with the same external_txn_id
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','FS-TXN-001', jsonb_build_object(
    'product_code','FS-BC-A','warehouse_code','WH-FS','subinventory','ONHAND',
    'signed_qty',-2,'uom_code','EA','txn_type','shipment',
    'erp_line_id','FS-LINE-002-1','erp_header_id','FS-HDR-002'));

-- ── 5. State-transition mutations ───────────────────────────────────────────
-- ST1: bump qty up 10 → 15 on FS-LINE-ST1
UPDATE processed.sfdc_order_line
   SET qty = 15
 WHERE erp_external_id = 'FS-LINE-ST1';

-- ST2: drop qty down 15 → 7
UPDATE processed.sfdc_order_line
   SET qty = 7
 WHERE erp_external_id = 'FS-LINE-ST1';

-- ST3: cancel the line (state → cancelled): full release
UPDATE processed.sfdc_order_line
   SET line_state = 'cancelled'
 WHERE erp_external_id = 'FS-LINE-ST1';

-- ST4: move FS-LINE-ST4 from ONHAND → STAGING (release+reserve)
UPDATE processed.sfdc_order_line
   SET subinventory = 'STAGING'
 WHERE erp_external_id = 'FS-LINE-ST4';

-- ST5: DELETE FS-LINE-ST5 (release everything)
DELETE FROM processed.sfdc_order_line
 WHERE erp_external_id = 'FS-LINE-ST5';

-- ============================================================================
-- 6. Results harness — one row per test
-- ============================================================================
CREATE TEMP TABLE fs_results (
    test_id    TEXT,
    category   TEXT,
    scenario   TEXT,
    original   TEXT,
    expected   TEXT,
    got        TEXT,
    pass_fail  TEXT
);

-- ── A. Basic calculation ───────────────────────────────────────────────────
INSERT INTO fs_results
SELECT 'BC1',
       'Basic calc',
       'Opening balance lands in stock_balance (BC-A/ONHAND on=100)',
       'on=100',
       'on_hand=95 after BC-O2 ships 5 total (2+3)',
       'on_hand='||on_hand_qty,
       CASE WHEN on_hand_qty=95 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance
 WHERE product_code='FS-BC-A' AND subinventory='ONHAND';

INSERT INTO fs_results
SELECT 'BC2',
       'Basic calc',
       'Reservation math BC-A/ONHAND (ORD-001=10, ORD-002 L1+L2 partial/full)',
       'open lines reserve qty - shipped_qty',
       'reserved_qty=13',
       'reserved_qty='||reserved_qty,
       CASE WHEN reserved_qty=13 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance
 WHERE product_code='FS-BC-A' AND subinventory='ONHAND';

INSERT INTO fs_results
SELECT 'BC3',
       'Basic calc',
       'ATP = on_hand - reserved (BC-A/ONHAND)',
       'derived',
       'atp=82',
       'atp='||(on_hand_qty - reserved_qty),
       CASE WHEN (on_hand_qty - reserved_qty)=82 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance
 WHERE product_code='FS-BC-A' AND subinventory='ONHAND';

INSERT INTO fs_results
SELECT 'BC4',
       'Basic calc',
       'Different subinventory tracked independently (BC-A/STAGING)',
       'STAGING ob=20, ORD-005 qty=8 partial-ship 5',
       'on=15 reserved=3 atp=12',
       'on='||on_hand_qty||' reserved='||reserved_qty||' atp='||(on_hand_qty-reserved_qty),
       CASE WHEN on_hand_qty=15 AND reserved_qty=3 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance
 WHERE product_code='FS-BC-A' AND subinventory='STAGING';

INSERT INTO fs_results
SELECT 'BC5',
       'Basic calc',
       'Different product tracked independently (BC-B/ONHAND, fully shipped)',
       'ob=50, ORD-003 qty=15 full ship',
       'on=35 reserved=0 atp=35',
       'on='||on_hand_qty||' reserved='||reserved_qty||' atp='||(on_hand_qty-reserved_qty),
       CASE WHEN on_hand_qty=35 AND reserved_qty=0 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance
 WHERE product_code='FS-BC-B' AND subinventory='ONHAND';

INSERT INTO fs_results
SELECT 'BC6',
       'Basic calc',
       'Different product, open order (BC-C/ONHAND, no ship)',
       'ob=200, ORD-004 qty=50 open',
       'on=200 reserved=50 atp=150',
       'on='||on_hand_qty||' reserved='||reserved_qty||' atp='||(on_hand_qty-reserved_qty),
       CASE WHEN on_hand_qty=200 AND reserved_qty=50 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance
 WHERE product_code='FS-BC-C' AND subinventory='ONHAND';

-- ── Shipment / line cascade ─────────────────────────────────────────────────
INSERT INTO fs_results
SELECT 'SC1',
       'Shipment cascade',
       'Partial shipment: line stays open with shipped_qty bumped',
       'ORD-002 L1 qty=5, ship -2',
       'shipped=2, line_state=open',
       'shipped='||shipped_qty||', line_state='||line_state,
       CASE WHEN shipped_qty=2 AND line_state='open' THEN 'PASS' ELSE 'FAIL' END
  FROM processed.sfdc_order_line WHERE erp_external_id='FS-LINE-002-1';

INSERT INTO fs_results
SELECT 'SC2',
       'Shipment cascade',
       'Full shipment closes the line',
       'ORD-002 L2 qty=3, ship -3',
       'shipped=3, line_state=closed',
       'shipped='||shipped_qty||', line_state='||line_state,
       CASE WHEN shipped_qty=3 AND line_state='closed' THEN 'PASS' ELSE 'FAIL' END
  FROM processed.sfdc_order_line WHERE erp_external_id='FS-LINE-002-2';

INSERT INTO fs_results
SELECT 'SC3',
       'Shipment cascade',
       'Single-line full shipment (BC-B)',
       'ORD-003 qty=15, ship -15',
       'shipped=15, line_state=closed',
       'shipped='||shipped_qty||', line_state='||line_state,
       CASE WHEN shipped_qty=15 AND line_state='closed' THEN 'PASS' ELSE 'FAIL' END
  FROM processed.sfdc_order_line WHERE erp_external_id='FS-LINE-003-1';

INSERT INTO fs_results
SELECT 'SC4',
       'Shipment cascade',
       'Line-precise: ORD-001 / ORD-004 untouched by other shipments',
       'no txn references their erp_external_id',
       'sum(shipped_qty)=0',
       'sum='||COALESCE(SUM(shipped_qty),0),
       CASE WHEN COALESCE(SUM(shipped_qty),0)=0 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.sfdc_order_line
 WHERE sfdc_order_id IN ('FS-ORD-001','FS-ORD-004');

INSERT INTO fs_results
SELECT 'SC5',
       'Shipment cascade',
       'Idempotency: re-inserted FS-TXN-001 promoted but inv_transaction stays unique',
       '2 inbox rows, 1 inv_transaction row',
       '1',
       COUNT(*)::text,
       CASE WHEN COUNT(*)=1 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.inv_transaction WHERE external_txn_id='FS-TXN-001';

-- ── Edge cases ─────────────────────────────────────────────────────────────
INSERT INTO fs_results
SELECT 'EC1a',
       'Edge case',
       'Order for product NOT in opening balance: stock_balance row auto-created',
       'no opening_balance for FS-EC-NO-OB',
       '1 stock_balance row exists',
       COUNT(*)::text,
       CASE WHEN COUNT(*)=1 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance WHERE product_code='FS-EC-NO-OB';

INSERT INTO fs_results
SELECT 'EC1b',
       'Edge case',
       'Auto-created row: on_hand=0, reserved=qty, atp=-qty (negative)',
       'order qty=20',
       'on=0 reserved=20 atp=-20',
       'on='||on_hand_qty||' reserved='||reserved_qty||' atp='||(on_hand_qty-reserved_qty),
       CASE WHEN on_hand_qty=0 AND reserved_qty=20 AND (on_hand_qty-reserved_qty)=-20
            THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance WHERE product_code='FS-EC-NO-OB';

INSERT INTO fs_results
SELECT 'EC2',
       'Edge case',
       'ATP=0 boundary (order qty exactly equals on_hand)',
       'ob=50, order qty=50, no ship',
       'on=50 reserved=50 atp=0',
       'on='||on_hand_qty||' reserved='||reserved_qty||' atp='||(on_hand_qty-reserved_qty),
       CASE WHEN on_hand_qty=50 AND reserved_qty=50 AND (on_hand_qty-reserved_qty)=0
            THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance WHERE product_code='FS-EC-EXACT';

INSERT INTO fs_results
SELECT 'EC3',
       'Edge case',
       'Oversell: order > on_hand → negative ATP, order still accepted',
       'ob=30, order qty=50',
       'on=30 reserved=50 atp=-20',
       'on='||on_hand_qty||' reserved='||reserved_qty||' atp='||(on_hand_qty-reserved_qty),
       CASE WHEN on_hand_qty=30 AND reserved_qty=50 AND (on_hand_qty-reserved_qty)=-20
            THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance WHERE product_code='FS-EC-OVERSELL';

INSERT INTO fs_results
SELECT 'EC4',
       'Edge case',
       'Overship: ship -25 against ob=20 → on_hand goes negative, line closes',
       'ob=20, order=20, ship -25',
       'on=-5 reserved=0 line_state=closed',
       'on='||sb.on_hand_qty||' reserved='||sb.reserved_qty||' line_state='||ol.line_state,
       CASE WHEN sb.on_hand_qty=-5 AND sb.reserved_qty=0 AND ol.line_state='closed'
            THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance sb
  JOIN processed.sfdc_order_line ol ON ol.erp_external_id='FS-LINE-EC4'
 WHERE sb.product_code='FS-EC-OVERSHIP' AND sb.subinventory='ONHAND';

INSERT INTO fs_results
SELECT 'EC5',
       'Edge case',
       'Line inserted as cancelled: zero reservation (target=0 for non-active states)',
       'ob=40, order qty=10, state=cancelled at insert',
       'on=40 reserved=0',
       'on='||on_hand_qty||' reserved='||reserved_qty,
       CASE WHEN on_hand_qty=40 AND reserved_qty=0 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance WHERE product_code='FS-EC-CANCELLED';

INSERT INTO fs_results
SELECT 'EC6',
       'Edge case',
       'Empty-string subinventory accepted; reservation lands on the empty row',
       'ob=70, order qty=15, subinv=""',
       'on=70 reserved=15',
       'on='||on_hand_qty||' reserved='||reserved_qty||' subinv='''||subinventory||'''',
       CASE WHEN on_hand_qty=70 AND reserved_qty=15 AND subinventory=''
            THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance WHERE product_code='FS-EC-EMPTY';

-- ── State transitions ─────────────────────────────────────────────────────
INSERT INTO fs_results
SELECT 'ST1+ST2+ST3',
       'State transition',
       'qty up→down→cancel chain: final reserved on ST-A/ONHAND should NOT include FS-LINE-ST1',
       'FS-LINE-ST1: 10→15→7 then cancelled. FS-LINE-ST4 moved out. FS-LINE-ST5 deleted.',
       'reserved on ST-A/ONHAND = 0',
       'reserved='||COALESCE(reserved_qty,-999)::text,
       CASE WHEN reserved_qty=0 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance
 WHERE product_code='FS-ST-A' AND subinventory='ONHAND';

INSERT INTO fs_results
SELECT 'ST4',
       'State transition',
       'Subinv move ONHAND→STAGING: reservation moves with the line',
       'FS-LINE-ST4 qty=12, moved ONHAND→STAGING',
       'STAGING reserved=12',
       'STAGING reserved='||COALESCE(reserved_qty,-999)::text,
       CASE WHEN reserved_qty=12 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.stock_balance
 WHERE product_code='FS-ST-A' AND subinventory='STAGING';

INSERT INTO fs_results
SELECT 'ST5',
       'State transition',
       'DELETE order line releases its reservation',
       'FS-LINE-ST5 qty=20 deleted',
       'no row references FS-LINE-ST5',
       'sfdc_order_line rows='||COUNT(*)::text,
       CASE WHEN COUNT(*)=0 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.sfdc_order_line WHERE erp_external_id='FS-LINE-ST5';

-- ── Negative cases ─────────────────────────────────────────────────────────
INSERT INTO fs_results
SELECT 'NC1',
       'Negative',
       'Unknown tenant → inbox rejected with reason=unknown_tenant',
       'tenant_code=NOT_A_TENANT',
       'status=rejected, reason=unknown_tenant',
       'status='||status||', reason='||COALESCE(reject_reason,'<null>'),
       CASE WHEN status='rejected' AND reject_reason='unknown_tenant'
            THEN 'PASS' ELSE 'FAIL' END
  FROM staging.order_inbox WHERE sfdc_order_id='FS-ORD-NC1';

INSERT INTO fs_results
SELECT 'NC2',
       'Negative',
       'Unknown product → inbox rejected with reason containing unknown_product',
       'product_code=FS-DOES-NOT-EXIST',
       'status=rejected, reason ~ unknown_product:FS-DOES-NOT-EXIST',
       'status='||status||', reason='||COALESCE(reject_reason,'<null>'),
       CASE WHEN status='rejected' AND reject_reason LIKE '%unknown_product:FS-DOES-NOT-EXIST%'
            THEN 'PASS' ELSE 'FAIL' END
  FROM staging.order_inbox WHERE sfdc_order_id='FS-ORD-NC2';

INSERT INTO fs_results
SELECT 'NC3',
       'Negative',
       'Unknown warehouse → inbox rejected with reason containing unknown_warehouse',
       'warehouse_code=WH-DOES-NOT-EXIST',
       'status=rejected, reason ~ unknown_warehouse:WH-DOES-NOT-EXIST',
       'status='||status||', reason='||COALESCE(reject_reason,'<null>'),
       CASE WHEN status='rejected' AND reject_reason LIKE '%unknown_warehouse:WH-DOES-NOT-EXIST%'
            THEN 'PASS' ELSE 'FAIL' END
  FROM staging.order_inbox WHERE sfdc_order_id='FS-ORD-NC3';

INSERT INTO fs_results
SELECT 'NC4',
       'Negative',
       'Unknown UOM → inbox rejected with reason containing unknown_uom',
       'uom_code=XYZ',
       'status=rejected, reason ~ unknown_uom:XYZ',
       'status='||status||', reason='||COALESCE(reject_reason,'<null>'),
       CASE WHEN status='rejected' AND reject_reason LIKE '%unknown_uom:XYZ%'
            THEN 'PASS' ELSE 'FAIL' END
  FROM staging.order_inbox WHERE sfdc_order_id='FS-ORD-NC4';

INSERT INTO fs_results
SELECT 'NC5',
       'Negative',
       'Negative-case inboxes did NOT create sfdc_order rows',
       '4 rejected inboxes',
       '0 sfdc_order rows',
       'count='||COUNT(*)::text,
       CASE WHEN COUNT(*)=0 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.sfdc_order
 WHERE sfdc_order_id IN ('FS-ORD-NC1','FS-ORD-NC2','FS-ORD-NC3','FS-ORD-NC4');

-- ── Inbox terminal-state sanity ─────────────────────────────────────────────
INSERT INTO fs_results
SELECT 'IB1',
       'Inbox state',
       'All non-NC order inboxes reach status=processed',
       '14 non-NC orders',
       'all processed',
       'processed='||COUNT(*) FILTER (WHERE status='processed')||
                       ' of '||COUNT(*)::text,
       CASE WHEN COUNT(*) FILTER (WHERE status='processed') = COUNT(*)
            THEN 'PASS' ELSE 'FAIL' END
  FROM staging.order_inbox
 WHERE sfdc_order_id LIKE 'FS-ORD-%' AND sfdc_order_id NOT LIKE 'FS-ORD-NC%';

INSERT INTO fs_results
SELECT 'IB2',
       'Inbox state',
       'All txn inboxes reach status=processed (incl. duplicate retry)',
       '6 txn inboxes',
       'all processed',
       'processed='||COUNT(*) FILTER (WHERE status='processed')||
                       ' of '||COUNT(*)::text,
       CASE WHEN COUNT(*) FILTER (WHERE status='processed') = COUNT(*)
            THEN 'PASS' ELSE 'FAIL' END
  FROM staging.txn_inbox WHERE external_txn_id LIKE 'FS-TXN-%';

-- ── Query / function (fetch_inventory_json) ────────────────────────────────
INSERT INTO fs_results
SELECT 'QC1',
       'Query/function',
       'fetch_inventory_json with all NULL filters returns >=11 rows for tenant',
       'all stock_balance rows for IFOPEUR',
       '>=11 rows',
       'rows='||COUNT(*)::text,
       CASE WHEN COUNT(*) >= 11 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.fetch_inventory_json('IFOPEUR', NULL, NULL, NULL);

INSERT INTO fs_results
SELECT 'QC2',
       'Query/function',
       'fetch_inventory_json filtered by product returns expected count',
       'product_code=FS-BC-A → 2 rows (ONHAND + STAGING)',
       '2 rows',
       'rows='||COUNT(*)::text,
       CASE WHEN COUNT(*)=2 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.fetch_inventory_json('IFOPEUR', NULL, NULL, 'FS-BC-A');

INSERT INTO fs_results
SELECT 'QC3',
       'Query/function',
       'fetch_inventory_json with non-existent product returns 0 rows',
       'product_code=NOPE',
       '0 rows',
       'rows='||COUNT(*)::text,
       CASE WHEN COUNT(*)=0 THEN 'PASS' ELSE 'FAIL' END
  FROM processed.fetch_inventory_json('IFOPEUR', NULL, NULL, 'NOPE');

INSERT INTO fs_results
SELECT 'QC4',
       'Query/function',
       'fetch_inventory_json returns INTEGER quantities (no .0000 decimals)',
       'BC-A/ONHAND row',
       'on_hand_qty=95, reserved_qty=13, atp_qty=82 (as int)',
       'on_hand='||(j->>'on_hand_qty')||' reserved='||(j->>'reserved_qty')||' atp='||(j->>'atp_qty'),
       CASE WHEN (j->>'on_hand_qty')='95' AND (j->>'reserved_qty')='13' AND (j->>'atp_qty')='82'
            THEN 'PASS' ELSE 'FAIL' END
  FROM (
    SELECT row::jsonb AS j
      FROM processed.fetch_inventory_json('IFOPEUR','WH-FS','ONHAND','FS-BC-A') AS f(row)
  ) wrapper;

-- ============================================================================
-- 7. Final result set
-- ============================================================================
SELECT test_id, category, scenario, original, expected, got, pass_fail
  FROM fs_results
 ORDER BY CASE category
            WHEN 'Basic calc'        THEN 1
            WHEN 'Shipment cascade'  THEN 2
            WHEN 'Edge case'         THEN 3
            WHEN 'State transition'  THEN 4
            WHEN 'Negative'          THEN 5
            WHEN 'Inbox state'       THEN 6
            WHEN 'Query/function'    THEN 7
            ELSE 99 END,
          test_id;
