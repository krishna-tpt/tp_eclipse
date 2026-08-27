-- ============================================================================
-- test_smoke_native_payloads.sql
-- ============================================================================
-- End-to-end smoke against the real Michelin sample payloads:
--   1. Oracle EBS transaction (TRANSACTION_ID 40178333)
--   2. SFDC Work Order (sfRecordId__c 0WO9Z000009f6lpWAA)
--
-- Run on the customer DB to prove the v6 auto-promote triggers fire.
-- Works in both pgAdmin Query Tool and psql.
-- Idempotent — re-run safely. Cleanup at the bottom (commented out).
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
-- 1. MASTERS — products / warehouses / uom for both payloads
-- ----------------------------------------------------------------------------
INSERT INTO processed.product (tenant_id, product_code, name)
SELECT t.tenant_id, code, code
  FROM processed.tenant t
  CROSS JOIN (VALUES ('213777_101'), ('9.248.11629'), ('CALLCHG1')) v(code)
 WHERE t.tenant_code = 'IFOPEUR'
ON CONFLICT (tenant_id, product_code) DO NOTHING;

INSERT INTO processed.warehouse (tenant_id, warehouse_code, name)
SELECT t.tenant_id, code, code
  FROM processed.tenant t
  CROSS JOIN (VALUES ('408_ES_COS_COS_WH_CO'), ('40')) v(code)
 WHERE t.tenant_code = 'IFOPEUR'
ON CONFLICT (tenant_id, warehouse_code) DO NOTHING;

INSERT INTO processed.uom (tenant_id, uom_code, name)
SELECT t.tenant_id, code, code
  FROM processed.tenant t
  CROSS JOIN (VALUES ('EA'), ('KG')) v(code)
 WHERE t.tenant_code = 'IFOPEUR'
ON CONFLICT (tenant_id, uom_code) DO NOTHING;

-- ----------------------------------------------------------------------------
-- 2. OPENING BALANCE — gives us stock to reserve / ship against
-- ----------------------------------------------------------------------------
-- For Oracle's ONHAND (qty 50 of 213777_101)
INSERT INTO processed.opening_balance
    (tenant_id, tenant_code, product_id, product_code,
     warehouse_id, warehouse_code, subinventory, stock_status,
     qty, uom_id, uom_code, as_of_date, batch_id, source_file)
SELECT t.tenant_id, 'IFOPEUR', p.product_id, p.product_code,
       w.warehouse_id, w.warehouse_code, 'ONHAND', 'LIBERATED',
       50, u.uom_id, u.uom_code, CURRENT_DATE, 1, 'smoke_v6'
  FROM processed.tenant t
  JOIN processed.product   p ON p.tenant_id = t.tenant_id AND p.product_code   = '213777_101'
  JOIN processed.warehouse w ON w.tenant_id = t.tenant_id AND w.warehouse_code = '408_ES_COS_COS_WH_CO'
  JOIN processed.uom       u ON u.tenant_id = t.tenant_id AND u.uom_code       = 'EA'
 WHERE t.tenant_code = 'IFOPEUR';

-- For SFDC's "1319Z000001l1mYQAQ" subinv (qty 20 of 9.248.11629 and CALLCHG1)
INSERT INTO processed.opening_balance
    (tenant_id, tenant_code, product_id, product_code,
     warehouse_id, warehouse_code, subinventory, stock_status,
     qty, uom_id, uom_code, as_of_date, batch_id, source_file)
SELECT t.tenant_id, 'IFOPEUR', p.product_id, p.product_code,
       w.warehouse_id, w.warehouse_code, '1319Z000001l1mYQAQ', 'LIBERATED',
       20, u.uom_id, u.uom_code, CURRENT_DATE, 1, 'smoke_v6'
  FROM processed.tenant t
  JOIN processed.product   p ON p.tenant_id = t.tenant_id AND p.product_code IN ('9.248.11629','CALLCHG1')
  JOIN processed.warehouse w ON w.tenant_id = t.tenant_id AND w.warehouse_code = '40'
  JOIN processed.uom       u ON u.tenant_id = t.tenant_id AND u.uom_code       = 'EA'
 WHERE t.tenant_code = 'IFOPEUR';

COMMIT;

SELECT '== STEP 1: After opening balance: stock_balance should have 3 rows ==' AS step;
SELECT product_code, warehouse_code, subinventory, on_hand_qty, reserved_qty
  FROM processed.stock_balance ORDER BY 1, 3;

-- ============================================================================
-- TEST 1 — INSERT real SFDC Work Order payload → trigger auto-promotes
-- ============================================================================
SELECT '== STEP 2: Insert SFDC Work Order 0WO9Z000009f6lpWAA ==' AS step;

INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR', '0WO9Z000009f6lpWAA', $J$ {
    "ReplayId": "21890619",
    "EventUuid": "8e558881-3c43-4f0f-af7f-31f9144b2cf0",
    "status__c": "Order Placed",
    "_EventType": "ftqCNeHG8yLKC6yZD250og",
    "account__c": "C01003_COOMERA_shipto",
    "_ObjectType": "Work_Order_Source_Event__e",
    "poNumber__c": "PO123422",
    "totalTax__c": 48.15,
    "sfRecordId__c": "0WO9Z000009f6lpWAA",
    "totalPrice__c": 481.5,
    "soldToAccount__c": "C01003",
    "currencyISOCode__c": "AUD",
    "operationType__c": "Create",
    "erpExternalId__c": null,
    "salesOrganisation__c": "6900",
    "totalIncludingTax__c": 529.65,
    "workOrderCreatedBy__c": "Nindia Sirly",
    "workOrderLineItems__c": "[{\"attributes\":{\"type\":\"WorkOrderLineItem\",\"url\":\"/services/data/v67.0/sobjects/WorkOrderLineItem/1WL9Z000001KSHJWA4\"},\"WorkOrderId\":\"0WO9Z000009f6lpWAA\",\"Id\":\"1WL9Z000001KSHJWA4\",\"Quantity\":1.00,\"ERP_Base_Price__c\":450.00,\"Delivery_Date__c\":\"2026-06-09\",\"Product_Code__c\":\"9.248.11629\",\"erpExternalId__c\":\"6787577\",\"Location_External_Id__c\":\"40\",\"Requested_Date__c\":\"2026-06-09\",\"Status\":\"New\",\"Storage_Location__c\":\"1319Z000001l1mYQAQ\"},{\"attributes\":{\"type\":\"WorkOrderLineItem\",\"url\":\"/services/data/v67.0/sobjects/WorkOrderLineItem/1WL9Z000001KSHKWA4\"},\"WorkOrderId\":\"0WO9Z000009f6lpWAA\",\"Id\":\"1WL9Z000001KSHKWA4\",\"Quantity\":1.00,\"ERP_Base_Price__c\":99.00,\"Delivery_Date__c\":\"2026-06-09\",\"Product_Code__c\":\"CALLCHG1\",\"erpExternalId__c\":\"6787578\",\"Location_External_Id__c\":\"40\",\"Requested_Date__c\":\"2026-06-09\",\"Status\":\"New\",\"Storage_Location__c\":\"1319Z000001l1mYQAQ\"}]"
}$J$::jsonb);

SELECT '-- staging row should be processed (not pending) --' AS step;
SELECT sfdc_order_id, status, processed_at, reject_reason
  FROM staging.order_inbox WHERE sfdc_order_id = '0WO9Z000009f6lpWAA';

SELECT '-- processed.sfdc_order should be populated --' AS step;
SELECT sfdc_order_id, customer_id, order_state, erp_external_id, received_at, created_at
  FROM processed.sfdc_order WHERE sfdc_order_id = '0WO9Z000009f6lpWAA';

SELECT '-- 2 lines should exist; erp_external_id populated; shipped_qty=0 --' AS step;
SELECT line_no, sfdc_line_id, erp_external_id, product_code, warehouse_code,
       subinventory, qty, shipped_qty, line_state
  FROM processed.sfdc_order_line
 WHERE sfdc_order_id = '0WO9Z000009f6lpWAA'
 ORDER BY line_no;

SELECT '-- reservation should land on subinv 1319Z000001l1mYQAQ --' AS step;
SELECT product_code, subinventory, on_hand_qty, reserved_qty,
       on_hand_qty - reserved_qty AS atp
  FROM processed.stock_balance
 WHERE subinventory = '1319Z000001l1mYQAQ' ORDER BY 1;

-- ============================================================================
-- TEST 2 — INSERT real Oracle EBS transaction payload → trigger auto-promotes
-- Note: SFDC line 1 above set erpExternalId__c="6787577" which matches the
-- Oracle SOURCE_LINE_ID. So this shipment cascades to the SFDC line and
-- closes it (qty was 1, this ships 5 → fully shipped, line closes).
-- ============================================================================
SELECT '== STEP 3: Insert Oracle txn 40178333 (signed_qty=-5, SOURCE_LINE_ID=6787577) ==' AS step;

INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR', '40178333', $J$ {
    "TRANSACTION_ID": 40178333,
    "OPERATING_UNIT": "408",
    "ORGANIZATION_NAME": "408_ES_COS_COS_WH_CO",
    "ORGANIZATION_CODE": "COS",
    "ORGANIZATION_TYPE": "FINISHED GOODS",
    "FG_ORGANIZATION_TYPE": "WHU",
    "TRX_FROM_DATE_TIME": "2026-06-10T08:06:56Z",
    "TRX_TO_DATE_TIME": "2026-06-10T08:06:56Z",
    "INSTANCE_NAME": "CFOPMAP3",
    "TRANSACTION_TYPE": "Sales order issue",
    "TRX_DATE_TIME": "2026-06-08T06:00:00Z",
    "TRX_CREATION_DATE_TIME": "2026-06-10T07:59:34Z",
    "CREATED_BY": "MICHEAI",
    "TOTAL_WEIGHT": -45.66000000,
    "PRIMARY_QTY": -5.00000000,
    "PRIMARY_UOM_CODE": "EA",
    "TRANSACTION_QTY": -5.00000000,
    "UNIT_COST": 213.09684000,
    "SOURCE_SUBINVENTORY": "ONHAND",
    "BASE_TRANSACTION_VALUE": -1065.48420000,
    "CURRENCY_CODE": "EUR",
    "FUNCTIONAL_TRX_VALUE": -1065.48420000,
    "TRANSACTION_REF": "5451391",
    "OWNING_PARTY": "COS-408_ES_COS_COS_WH_CO",
    "SHIPMENT_NUMBER": "WMS234330701",
    "ITEM_CODE": "213777_101",
    "INVENTORY_ITEM_ID": 1752294,
    "ITEM_TYPE": "CAD",
    "ITEM_DESCRIPTION": "205/55ZR16 91W TL PILSP4 MI",
    "ITEM_COST": 213.09684000,
    "PRIMARY_WEIGHT": 9.13200000,
    "PRIMARY_WEIGHT_CODE": "KG",
    "LAST_UPDATE_DATE": "2026-06-10T08:01:48Z",
    "SOURCE_CODE": "ORDER ENTRY",
    "SOURCE_LINE_ID": 6787577,
    "TRANSACTION_SOURCE_ID": 5565740,
    "TRX_SOURCE_LINE_ID": 6787577,
    "TRANSACTION_TYPE_DESCRIPTION": "Ship Confirm external Sales Order"
}$J$::jsonb);

SELECT '-- staging row should be processed --' AS step;
SELECT external_txn_id, status, processed_at, reject_reason
  FROM staging.txn_inbox WHERE external_txn_id = '40178333';

SELECT '-- processed.inv_transaction should have erp_line_id=6787577, erp_header_id=5565740 --' AS step;
SELECT external_txn_id, product_code, warehouse_code, subinventory,
       signed_qty, txn_type, posted_at, erp_line_id, erp_header_id
  FROM processed.inv_transaction WHERE external_txn_id = '40178333';

SELECT '-- stock_balance on ONHAND: on_hand should drop by 5 (50 -> 45) --' AS step;
SELECT product_code, subinventory, on_hand_qty, reserved_qty
  FROM processed.stock_balance
 WHERE product_code = '213777_101' ORDER BY 3;

SELECT '-- cascade: SFDC line 1 (erp_external_id=6787577) closed, line 2 still open --' AS step;
SELECT line_no, product_code, erp_external_id, qty, shipped_qty, line_state
  FROM processed.sfdc_order_line
 WHERE sfdc_order_id = '0WO9Z000009f6lpWAA' ORDER BY line_no;

SELECT '-- final stock_balance for SFDC products (line 1 reservation should now be 0) --' AS step;
SELECT product_code, subinventory, on_hand_qty, reserved_qty,
       on_hand_qty - reserved_qty AS atp
  FROM processed.stock_balance
 WHERE subinventory = '1319Z000001l1mYQAQ' ORDER BY 1;


-- ============================================================================
-- EXPECTED FINAL STATE
-- ============================================================================
-- staging.order_inbox:  1 row, status=processed
-- staging.txn_inbox:    1 row, status=processed
-- processed.sfdc_order: 1 row (0WO9Z000009f6lpWAA)
-- processed.sfdc_order_line: 2 rows
--   line 1: 9.248.11629, qty=1, shipped_qty=5 (over-ship — sample payloads
--           aren't from same business event), line_state=closed  ← cascade fired
--   line 2: CALLCHG1,    qty=1, shipped_qty=0, line_state=open    ← not touched
-- processed.inv_transaction: 1 row, signed_qty=-5
-- processed.stock_balance:
--   213777_101 / ONHAND:           on=45 res=0   atp=45  (50 - 5 shipped)
--   9.248.11629 / 1319Z000001...:  on=20 res=0   atp=20  (was reserved=1, cascade released)
--   CALLCHG1    / 1319Z000001...:  on=20 res=1   atp=19  (still reserved)
-- ============================================================================

-- ============================================================================
-- CLEANUP (uncomment to undo this smoke test)
-- ============================================================================
-- BEGIN;
-- DELETE FROM processed.inv_transaction WHERE external_txn_id = '40178333';
-- DELETE FROM processed.sfdc_order      WHERE sfdc_order_id   = '0WO9Z000009f6lpWAA';
-- DELETE FROM staging.txn_inbox         WHERE external_txn_id = '40178333';
-- DELETE FROM staging.order_inbox       WHERE sfdc_order_id   = '0WO9Z000009f6lpWAA';
-- DELETE FROM processed.opening_balance WHERE source_file = 'smoke_v6';
-- DELETE FROM processed.stock_balance   WHERE product_code IN ('213777_101','9.248.11629','CALLCHG1');
-- DELETE FROM processed.product         WHERE product_code IN ('213777_101','9.248.11629','CALLCHG1');
-- DELETE FROM processed.warehouse       WHERE warehouse_code IN ('408_ES_COS_COS_WH_CO','40');
-- DELETE FROM processed.uom             WHERE uom_code IN ('EA','KG');
-- COMMIT;
