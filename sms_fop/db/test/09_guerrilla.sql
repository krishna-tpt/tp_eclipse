-- Guerrilla / hostile-input tests. Throw whatever might break things.

\set ON_ERROR_STOP off

TRUNCATE tc_result;

-- Clean prior GUER tenant data for a fresh run
DELETE FROM inv_transaction_default WHERE tenant_id IN ('00000000-0000-0000-0000-000000005001','00000000-0000-0000-0000-000000005002');
DELETE FROM sfdc_order_line WHERE tenant_id IN ('00000000-0000-0000-0000-000000005001','00000000-0000-0000-0000-000000005002');
DELETE FROM sfdc_order WHERE tenant_id IN ('00000000-0000-0000-0000-000000005001','00000000-0000-0000-0000-000000005002');
DELETE FROM opening_balance WHERE tenant_id IN ('00000000-0000-0000-0000-000000005001','00000000-0000-0000-0000-000000005002');
DELETE FROM stock_balance WHERE tenant_id IN ('00000000-0000-0000-0000-000000005001','00000000-0000-0000-0000-000000005002');

-- Fresh tenant for these tests
INSERT INTO tenant (tenant_id, tenant_code, name) VALUES
    ('00000000-0000-0000-0000-000000005001', 'GUER', 'Guerrilla') ON CONFLICT DO NOTHING;
INSERT INTO product   (tenant_id, product_code, name) VALUES
    ('00000000-0000-0000-0000-000000005001', 'GUER-P', 'Guerrilla product') ON CONFLICT DO NOTHING;
INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES
    ('00000000-0000-0000-0000-000000005001', 'GUER-W', 'Guerrilla warehouse') ON CONFLICT DO NOTHING;
INSERT INTO uom (tenant_id, uom_code, name) VALUES
    ('00000000-0000-0000-0000-000000005001', 'EA', 'Each') ON CONFLICT DO NOTHING;

-- ============================================================================
-- GROUP G: Sign vs txn_type semantics — these should be rejected but probably aren't
-- ============================================================================
SELECT tc('TC-500', 'receipt with negative qty SHOULD be rejected',
    (SELECT NOT accepted FROM post_transaction(
        '{"tenant_code":"GUER","external_txn_id":"G2-NEG-RCPT","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":-10,"txn_type":"receipt","source_system":"g","posted_at":"2026-05-19T10:00:00Z"}'::jsonb)));

SELECT tc('TC-501', 'issue with positive qty SHOULD be rejected',
    (SELECT NOT accepted FROM post_transaction(
        '{"tenant_code":"GUER","external_txn_id":"G2-POS-ISSUE","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":10,"txn_type":"issue","source_system":"g","posted_at":"2026-05-19T10:00:00Z"}'::jsonb)));

SELECT tc('TC-502', 'transfer_out with positive qty SHOULD be rejected',
    (SELECT NOT accepted FROM post_transaction(
        '{"tenant_code":"GUER","external_txn_id":"G2-POS-XOUT","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":5,"txn_type":"transfer_out","transfer_pair_id":"00000000-0000-0000-0000-000000099999","source_system":"g","posted_at":"2026-05-19T10:00:00Z"}'::jsonb)));

SELECT tc('TC-503', 'transfer_in with negative qty SHOULD be rejected',
    (SELECT NOT accepted FROM post_transaction(
        '{"tenant_code":"GUER","external_txn_id":"G2-NEG-XIN","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":-5,"txn_type":"transfer_in","transfer_pair_id":"00000000-0000-0000-0000-000000099998","source_system":"g","posted_at":"2026-05-19T10:00:00Z"}'::jsonb)));

SELECT tc('TC-504', 'sales_return with negative qty SHOULD be rejected',
    (SELECT NOT accepted FROM post_transaction(
        '{"tenant_code":"GUER","external_txn_id":"G2-NEG-SRTN","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":-3,"txn_type":"sales_return","source_system":"g","posted_at":"2026-05-19T10:00:00Z"}'::jsonb)));

SELECT tc('TC-505', 'purchase_return with positive qty SHOULD be rejected',
    (SELECT NOT accepted FROM post_transaction(
        '{"tenant_code":"GUER","external_txn_id":"G2-POS-PRTN","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":3,"txn_type":"purchase_return","source_system":"g","posted_at":"2026-05-19T10:00:00Z"}'::jsonb)));

SELECT tc('TC-506', 'scrap with positive qty SHOULD be rejected',
    (SELECT NOT accepted FROM post_transaction(
        '{"tenant_code":"GUER","external_txn_id":"G2-POS-SCRP","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":4,"txn_type":"scrap","source_system":"g","posted_at":"2026-05-19T10:00:00Z"}'::jsonb)));

-- ============================================================================
-- GROUP H: Cross-tenant integrity (product belongs to tenant A, claim tenant B)
-- ============================================================================
-- Set up two tenants with same product_code but different tenant_ids
INSERT INTO tenant (tenant_id, tenant_code, name) VALUES
    ('00000000-0000-0000-0000-000000005002', 'XOTH', 'Cross-tenant other')
ON CONFLICT DO NOTHING;
INSERT INTO product (tenant_id, product_code, name) VALUES
    ('00000000-0000-0000-0000-000000005002', 'GUER-P', 'Other tenant product') ON CONFLICT DO NOTHING;
INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES
    ('00000000-0000-0000-0000-000000005002', 'GUER-W', 'Other tenant warehouse') ON CONFLICT DO NOTHING;
INSERT INTO uom (tenant_id, uom_code, name) VALUES
    ('00000000-0000-0000-0000-000000005002', 'EA', 'Each') ON CONFLICT DO NOTHING;

-- Cross-tenant: same codes resolve within each tenant. Each lookup uses
-- tenant_id filter so they resolve to their respective tenant's product_id.
-- The risk would be: what if Java passes wrong tenant_code and gets that
-- tenant's product instead? post_transaction handles by looking up product
-- under the resolved tenant_id, so this is intrinsically tenant-safe.
SELECT tc('TC-510', 'post_transaction looks up product UNDER tenant — tenant_code = GUER resolves to GUER tenant data',
    (SELECT accepted FROM post_transaction(
        '{"tenant_code":"GUER","external_txn_id":"G2-OK","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":1,"txn_type":"receipt","source_system":"g","posted_at":"2026-05-19T10:00:00Z"}'::jsonb)));

-- TC-511: Direct INSERT with cross-tenant product_id (bypassing post_transaction)
DO $$
DECLARE
    other_tenant_product_id bigint;
    v_raised boolean := false;
BEGIN
    SELECT product_id INTO other_tenant_product_id FROM product
     WHERE tenant_id='00000000-0000-0000-0000-000000005002' AND product_code='GUER-P';
    BEGIN
        -- Insert claiming GUER tenant but using OTH tenant's product_id
        INSERT INTO inv_transaction (tenant_id, external_txn_id, product_id, warehouse_id, uom_id,
            signed_qty, txn_type, source_system, posted_at, payload)
        SELECT '00000000-0000-0000-0000-000000005001', 'G2-CROSS',
               other_tenant_product_id,
               (SELECT warehouse_id FROM warehouse WHERE tenant_id='00000000-0000-0000-0000-000000005001' AND warehouse_code='GUER-W'),
               (SELECT uom_id FROM uom WHERE tenant_id='00000000-0000-0000-0000-000000005001' AND uom_code='EA'),
               1, 'receipt', 'g', now(), '{}'::jsonb;
    EXCEPTION WHEN OTHERS THEN
        v_raised := true;
    END;
    -- The insert will succeed because inv_transaction has no FK to product (partitioned table)
    -- and no tenant_id consistency check. Document this.
    PERFORM tc('TC-511', 'CHECK / FK does NOT prevent inv_transaction.product_id from another tenant — design gap',
        v_raised);
END$$;

-- ============================================================================
-- GROUP I: SFDC state machine
-- ============================================================================
DO $$
DECLARE v_raised boolean := false;
BEGIN
    BEGIN
        PERFORM upsert_order(
            '{"tenant_code":"GUER","sfdc_order_id":"G2-BAD-STATE","customer_id":"C","order_state":"bogus","lines":[]}'::jsonb);
    EXCEPTION WHEN check_violation THEN v_raised := true; END;
    PERFORM tc('TC-520', 'order_state=bogus raises check_violation', v_raised);
END$$;

SELECT upsert_order('{"tenant_code":"GUER","sfdc_order_id":"G2-EMPTY","customer_id":"C","order_state":"open","lines":[]}'::jsonb);
SELECT tc('TC-521', 'upsert_order with empty lines array succeeds (header inserted, 0 lines)',
    (SELECT count(*) = 0 FROM sfdc_order_line WHERE sfdc_order_id='G2-EMPTY'));

DO $$
DECLARE v_raised boolean := false;
BEGIN
    BEGIN
        PERFORM upsert_order(
            '{"tenant_code":"GUER","sfdc_order_id":"G2-DUP","customer_id":"C","order_state":"open","lines":[{"line_no":1,"product_code":"GUER-P","warehouse_code":"GUER-W","qty":1,"uom_code":"EA","line_state":"open"},{"line_no":1,"product_code":"GUER-P","warehouse_code":"GUER-W","qty":2,"uom_code":"EA","line_state":"open"}]}'::jsonb);
    EXCEPTION WHEN OTHERS THEN v_raised := true; END;
    -- Two lines with same line_no in payload: second INSERT into sfdc_order_line
    -- triggers ON CONFLICT DO UPDATE (PK is sfdc_order_id, line_no), so the
    -- second overwrites the first. No exception, but data loss.
    PERFORM tc('TC-522', 'duplicate line_no in payload silently overwrites (later wins, no error) — gap',
        NOT v_raised AND (SELECT qty = 2 FROM sfdc_order_line WHERE sfdc_order_id='G2-DUP' AND line_no=1));
END$$;

DO $$
DECLARE v_raised boolean := false;
BEGIN
    BEGIN
        PERFORM upsert_order(
            '{"tenant_code":"GUER","sfdc_order_id":"G2-NEG","customer_id":"C","order_state":"open","lines":[{"line_no":1,"product_code":"GUER-P","warehouse_code":"GUER-W","qty":-5,"uom_code":"EA","line_state":"open"}]}'::jsonb);
    EXCEPTION WHEN OTHERS THEN v_raised := true; END;
    -- No CHECK on sfdc_order_line.qty > 0; negative qty silently accepted
    PERFORM tc('TC-523', 'negative order qty rejected (CHECK qty > 0) — currently accepted, design gap',
        v_raised);
END$$;

DO $$
DECLARE v_raised boolean := false;
BEGIN
    BEGIN
        PERFORM upsert_order(
            '{"tenant_code":"GUER","sfdc_order_id":"G2-BADLS","customer_id":"C","order_state":"open","lines":[{"line_no":1,"product_code":"GUER-P","warehouse_code":"GUER-W","qty":1,"uom_code":"EA","line_state":"bogus_state"}]}'::jsonb);
    EXCEPTION WHEN check_violation THEN v_raised := true; END;
    -- V6: new line with non-open state is silently coerced to open + logged.
    PERFORM tc('TC-524', 'new line with bogus line_state is coerced to open (V6 behavior)',
        (SELECT line_state='open' FROM sfdc_order_line WHERE sfdc_order_id='G2-BADLS'));
END$$;

-- ============================================================================
-- GROUP J: Malformed payloads
-- ============================================================================
SELECT tc('TC-530', 'empty payload returns unknown_reference (V6)',
    (SELECT reason = 'unknown_reference' FROM post_transaction('{}'::jsonb)));

SELECT tc('TC-531', 'missing external_txn_id — what happens?',
    (SELECT reason IS NOT NULL FROM post_transaction(
        '{"tenant_code":"GUER","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":1,"txn_type":"receipt","source_system":"g","posted_at":"2026-05-19T10:00:00Z"}'::jsonb)));

SELECT tc('TC-532', 'extra unknown fields are tolerated',
    (SELECT accepted FROM post_transaction(
        '{"tenant_code":"GUER","external_txn_id":"G2-EXTRA","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":1,"txn_type":"receipt","source_system":"g","posted_at":"2026-05-19T10:00:00Z","extra_field":"ignored","another":42}'::jsonb)));

DO $$
DECLARE v_raised boolean := false;
BEGIN
    BEGIN
        PERFORM post_transaction(
            '{"tenant_code":"GUER","external_txn_id":"G2-BADQTY","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":"not_a_number","txn_type":"receipt","source_system":"g","posted_at":"2026-05-19T10:00:00Z"}'::jsonb);
    EXCEPTION WHEN OTHERS THEN v_raised := true; END;
    PERFORM tc('TC-533', 'signed_qty as non-numeric string raises (cast fails)', v_raised);
END$$;

-- ============================================================================
-- GROUP K: Numeric extremes
-- ============================================================================
SELECT tc('TC-540', 'tiny qty 0.0001 accepted (4-decimal precision)',
    (SELECT accepted FROM post_transaction(
        '{"tenant_code":"GUER","external_txn_id":"G2-TINY","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":0.0001,"txn_type":"receipt","source_system":"g","posted_at":"2026-05-19T10:00:00Z"}'::jsonb)));

SELECT tc('TC-541', 'large qty 999999999.0000 accepted',
    (SELECT accepted FROM post_transaction(
        '{"tenant_code":"GUER","external_txn_id":"G2-HUGE","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":999999999.0000,"txn_type":"receipt","source_system":"g","posted_at":"2026-05-19T10:00:00Z"}'::jsonb)));

DO $$
DECLARE v_raised boolean := false;
BEGIN
    BEGIN
        -- numeric(18,4) max integer part is 14 digits; 9999999999999999 (16 nines) → overflow
        PERFORM post_transaction(
            '{"tenant_code":"GUER","external_txn_id":"G2-OVERFLOW","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":99999999999999999.0,"txn_type":"receipt","source_system":"g","posted_at":"2026-05-19T10:00:00Z"}'::jsonb);
    EXCEPTION WHEN numeric_value_out_of_range OR OTHERS THEN v_raised := true; END;
    PERFORM tc('TC-542', 'overflow qty raises numeric_value_out_of_range', v_raised);
END$$;

-- TC-543: Oversell behavior under V5 — INTENTIONAL backorder support that emits
-- an info notification. Use a dedicated tenant for clean math.
INSERT INTO tenant (tenant_id, tenant_code, name) VALUES
    ('00000000-0000-0000-0000-000000005003', 'OVER', 'Oversell test') ON CONFLICT DO NOTHING;
INSERT INTO product   (tenant_id, product_code, name) VALUES
    ('00000000-0000-0000-0000-000000005003', 'OVER-P', 'Oversell product') ON CONFLICT DO NOTHING;
INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES
    ('00000000-0000-0000-0000-000000005003', 'OVER-W', 'Oversell warehouse') ON CONFLICT DO NOTHING;
INSERT INTO uom (tenant_id, uom_code, name) VALUES
    ('00000000-0000-0000-0000-000000005003', 'EA', 'Each') ON CONFLICT DO NOTHING;

-- Clean any prior oversell run rows
DELETE FROM inv_transaction_default WHERE tenant_id='00000000-0000-0000-0000-000000005003';
DELETE FROM opening_balance         WHERE tenant_id='00000000-0000-0000-0000-000000005003';
DELETE FROM stock_balance           WHERE tenant_id='00000000-0000-0000-0000-000000005003';

INSERT INTO opening_balance (tenant_id, product_id, warehouse_id, qty, uom_id, as_of_date, batch_id)
SELECT '00000000-0000-0000-0000-000000005003',
       (SELECT product_id FROM product WHERE tenant_id='00000000-0000-0000-0000-000000005003' AND product_code='OVER-P'),
       (SELECT warehouse_id FROM warehouse WHERE tenant_id='00000000-0000-0000-0000-000000005003' AND warehouse_code='OVER-W'),
       5,
       (SELECT uom_id FROM uom WHERE tenant_id='00000000-0000-0000-0000-000000005003' AND uom_code='EA'),
       '2026-05-19', 0;

SELECT post_transaction(
    '{"tenant_code":"OVER","external_txn_id":"G2-OVERSELL","product_code":"OVER-P","warehouse_code":"OVER-W","uom_code":"EA","signed_qty":-100,"txn_type":"issue","source_system":"g","posted_at":"2026-05-19T11:00:00Z"}'::jsonb);

SELECT tc('TC-543', 'oversell drops on_hand to -95 (intentional backorder)',
    (SELECT on_hand_qty = -95 FROM calculate_inventory(
        '00000000-0000-0000-0000-000000005003',
        (SELECT product_id FROM product WHERE tenant_id='00000000-0000-0000-0000-000000005003' AND product_code='OVER-P'),
        (SELECT warehouse_id FROM warehouse WHERE tenant_id='00000000-0000-0000-0000-000000005003' AND warehouse_code='OVER-W'))));

SELECT tc('TC-544', 'oversell emits info notification (V5 backorder visibility)',
    EXISTS (SELECT 1 FROM notification_outbox
             WHERE source='post_transaction'
               AND severity='info'
               AND message LIKE 'oversell:%'
               AND tenant_id='00000000-0000-0000-0000-000000005003'));

SELECT tc('TC-545', 'oversell stock_balance is negative (intentional)',
    (SELECT on_hand_qty < 0 FROM stock_balance WHERE tenant_id='00000000-0000-0000-0000-000000005003'));

-- ============================================================================
-- GROUP L: Time extremes
-- ============================================================================
SELECT tc('TC-550', 'posted_at in the future (2099) accepted',
    (SELECT accepted FROM post_transaction(
        '{"tenant_code":"GUER","external_txn_id":"G2-FUTURE","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":1,"txn_type":"receipt","source_system":"g","posted_at":"2099-12-31T23:59:59Z"}'::jsonb)));

SELECT tc('TC-551', 'posted_at in the far past (1900) accepted',
    (SELECT accepted FROM post_transaction(
        '{"tenant_code":"GUER","external_txn_id":"G2-ANCIENT","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":1,"txn_type":"receipt","source_system":"g","posted_at":"1900-01-01T00:00:00Z"}'::jsonb)));

-- ============================================================================
-- GROUP M: Reconciliation specifics
-- ============================================================================

-- Clean order data for this group
DELETE FROM sfdc_order_line WHERE sfdc_order_id LIKE 'G2-RECON-%';
DELETE FROM sfdc_order      WHERE sfdc_order_id LIKE 'G2-RECON-%';

-- TC-560: Order qty 10 > shipment qty 3 → MUST NOT match (qty <= ABS(signed_qty) requires)
INSERT INTO sfdc_order (sfdc_order_id, tenant_id, customer_id, payload) VALUES
    ('G2-RECON-BIG','00000000-0000-0000-0000-000000005001','C','{}'::jsonb)
ON CONFLICT DO NOTHING;
INSERT INTO sfdc_order_line (sfdc_order_id, line_no, tenant_id, product_id, warehouse_id, qty, uom_id, line_state, payload)
SELECT 'G2-RECON-BIG', 1, '00000000-0000-0000-0000-000000005001',
       p.product_id, w.warehouse_id, 10, u.uom_id, 'open', '{}'::jsonb
  FROM product p, warehouse w, uom u
 WHERE p.tenant_id='00000000-0000-0000-0000-000000005001' AND p.product_code='GUER-P'
   AND w.tenant_id='00000000-0000-0000-0000-000000005001' AND w.warehouse_code='GUER-W'
   AND u.tenant_id='00000000-0000-0000-0000-000000005001' AND u.uom_code='EA';

SELECT post_transaction(
    '{"tenant_code":"GUER","external_txn_id":"G2-SHIP-3","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":-3,"txn_type":"issue","source_system":"g","posted_at":"2026-05-19T12:00:00Z"}'::jsonb);

SELECT tc('TC-560', 'shipment qty < order qty does NOT match (qty <= ABS(signed_qty))',
    (SELECT fop_synced_at IS NULL FROM sfdc_order_line WHERE sfdc_order_id='G2-RECON-BIG' AND line_no=1));

-- TC-561: cancelled line shouldn't match
INSERT INTO sfdc_order (sfdc_order_id, tenant_id, customer_id, payload) VALUES
    ('G2-RECON-CANC','00000000-0000-0000-0000-000000005001','C','{}'::jsonb)
ON CONFLICT DO NOTHING;
INSERT INTO sfdc_order_line (sfdc_order_id, line_no, tenant_id, product_id, warehouse_id, qty, uom_id, line_state, payload)
SELECT 'G2-RECON-CANC', 1, '00000000-0000-0000-0000-000000005001',
       p.product_id, w.warehouse_id, 2, u.uom_id, 'cancelled', '{}'::jsonb
  FROM product p, warehouse w, uom u
 WHERE p.tenant_id='00000000-0000-0000-0000-000000005001' AND p.product_code='GUER-P'
   AND w.tenant_id='00000000-0000-0000-0000-000000005001' AND w.warehouse_code='GUER-W'
   AND u.tenant_id='00000000-0000-0000-0000-000000005001' AND u.uom_code='EA';

SELECT post_transaction(
    '{"tenant_code":"GUER","external_txn_id":"G2-SHIP-CANC","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":-2,"txn_type":"issue","source_system":"g","posted_at":"2026-05-19T12:01:00Z"}'::jsonb);

SELECT tc('TC-561', 'cancelled line is NOT reconciled by shipment',
    (SELECT fop_synced_at IS NULL FROM sfdc_order_line WHERE sfdc_order_id='G2-RECON-CANC' AND line_no=1));

-- TC-562: closed line shouldn't match
INSERT INTO sfdc_order (sfdc_order_id, tenant_id, customer_id, payload) VALUES
    ('G2-RECON-CLS','00000000-0000-0000-0000-000000005001','C','{}'::jsonb)
ON CONFLICT DO NOTHING;
INSERT INTO sfdc_order_line (sfdc_order_id, line_no, tenant_id, product_id, warehouse_id, qty, uom_id, line_state, payload, fop_synced_at)
SELECT 'G2-RECON-CLS', 1, '00000000-0000-0000-0000-000000005001',
       p.product_id, w.warehouse_id, 2, u.uom_id, 'closed', '{}'::jsonb, NULL
  FROM product p, warehouse w, uom u
 WHERE p.tenant_id='00000000-0000-0000-0000-000000005001' AND p.product_code='GUER-P'
   AND w.tenant_id='00000000-0000-0000-0000-000000005001' AND w.warehouse_code='GUER-W'
   AND u.tenant_id='00000000-0000-0000-0000-000000005001' AND u.uom_code='EA';

SELECT post_transaction(
    '{"tenant_code":"GUER","external_txn_id":"G2-SHIP-CLS","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":-2,"txn_type":"issue","source_system":"g","posted_at":"2026-05-19T12:02:00Z"}'::jsonb);

SELECT tc('TC-562', 'closed line is NOT reconciled by shipment',
    (SELECT fop_synced_at IS NULL FROM sfdc_order_line WHERE sfdc_order_id='G2-RECON-CLS' AND line_no=1));

-- TC-563: FIFO — two open lines for same prod/wh, oldest matches first
DELETE FROM sfdc_order_line WHERE sfdc_order_id LIKE 'G2-FIFO-%';
DELETE FROM sfdc_order      WHERE sfdc_order_id LIKE 'G2-FIFO-%';
INSERT INTO sfdc_order (sfdc_order_id, tenant_id, customer_id, payload) VALUES
    ('G2-FIFO-1','00000000-0000-0000-0000-000000005001','C','{}'::jsonb),
    ('G2-FIFO-2','00000000-0000-0000-0000-000000005001','C','{}'::jsonb)
ON CONFLICT DO NOTHING;
INSERT INTO sfdc_order_line (sfdc_order_id, line_no, tenant_id, product_id, warehouse_id, qty, uom_id, line_state, payload, created_at)
SELECT 'G2-FIFO-1', 1, '00000000-0000-0000-0000-000000005001',
       p.product_id, w.warehouse_id, 2, u.uom_id, 'open', '{}'::jsonb,
       now() - interval '2 hours'
  FROM product p, warehouse w, uom u
 WHERE p.tenant_id='00000000-0000-0000-0000-000000005001' AND p.product_code='GUER-P'
   AND w.tenant_id='00000000-0000-0000-0000-000000005001' AND w.warehouse_code='GUER-W'
   AND u.tenant_id='00000000-0000-0000-0000-000000005001' AND u.uom_code='EA';
INSERT INTO sfdc_order_line (sfdc_order_id, line_no, tenant_id, product_id, warehouse_id, qty, uom_id, line_state, payload, created_at)
SELECT 'G2-FIFO-2', 1, '00000000-0000-0000-0000-000000005001',
       p.product_id, w.warehouse_id, 2, u.uom_id, 'open', '{}'::jsonb,
       now() - interval '1 hour'
  FROM product p, warehouse w, uom u
 WHERE p.tenant_id='00000000-0000-0000-0000-000000005001' AND p.product_code='GUER-P'
   AND w.tenant_id='00000000-0000-0000-0000-000000005001' AND w.warehouse_code='GUER-W'
   AND u.tenant_id='00000000-0000-0000-0000-000000005001' AND u.uom_code='EA';

SELECT post_transaction(
    '{"tenant_code":"GUER","external_txn_id":"G2-FIFO-SHIP","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":-2,"txn_type":"issue","source_system":"g","posted_at":"2026-05-19T13:00:00Z"}'::jsonb);

SELECT tc('TC-563', 'FIFO: oldest open line (G2-FIFO-1) matched first',
    (SELECT (SELECT fop_synced_at IS NOT NULL FROM sfdc_order_line WHERE sfdc_order_id='G2-FIFO-1')
        AND (SELECT fop_synced_at IS NULL     FROM sfdc_order_line WHERE sfdc_order_id='G2-FIFO-2')));

-- TC-564: shipment with no eligible orders — no error
DO $$
DECLARE v_raised boolean := false;
BEGIN
    BEGIN
        PERFORM post_transaction(
            '{"tenant_code":"GUER","external_txn_id":"G2-NOORDER","product_code":"GUER-P","warehouse_code":"GUER-W","uom_code":"EA","signed_qty":-1,"txn_type":"issue","source_system":"g","posted_at":"2026-05-19T14:00:00Z"}'::jsonb);
    EXCEPTION WHEN OTHERS THEN v_raised := true; END;
    PERFORM tc('TC-564', 'shipment with no eligible orders is a no-op (no exception)', NOT v_raised);
END$$;

-- ============================================================================
-- GROUP N: RLS / role escalation
-- ============================================================================

-- TC-570: writer role can SELECT inv_transaction directly? (should be DENIED — only EXECUTE on functions granted)
DO $$
DECLARE v_allowed boolean := false;
BEGIN
    SET LOCAL ROLE inventoryledger_writer;
    BEGIN
        PERFORM (SELECT 1 FROM inv_transaction LIMIT 1);
        v_allowed := true;
    EXCEPTION WHEN insufficient_privilege THEN
        v_allowed := false;
    END;
    RESET ROLE;
    PERFORM tc('TC-570', 'inventoryledger_writer cannot SELECT inv_transaction directly (least-privilege)',
        NOT v_allowed);
END$$;

-- TC-571: writer role can SELECT sfdc_order_line directly? (should be DENIED)
DO $$
DECLARE v_allowed boolean := false;
BEGIN
    SET LOCAL ROLE inventoryledger_writer;
    BEGIN
        PERFORM (SELECT 1 FROM sfdc_order_line LIMIT 1);
        v_allowed := true;
    EXCEPTION WHEN insufficient_privilege THEN v_allowed := false;
    END;
    RESET ROLE;
    PERFORM tc('TC-571', 'inventoryledger_writer cannot SELECT sfdc_order_line directly',
        NOT v_allowed);
END$$;

-- TC-572: reader can SELECT mv_atp but cannot INSERT into inv_transaction
DO $$
DECLARE v_select_ok boolean := false; v_insert_ok boolean := false;
BEGIN
    SET LOCAL ROLE inventoryledger_reader;
    BEGIN
        PERFORM (SELECT 1 FROM mv_atp LIMIT 1);
        v_select_ok := true;
    EXCEPTION WHEN insufficient_privilege THEN v_select_ok := false; END;
    BEGIN
        INSERT INTO inv_transaction (tenant_id, external_txn_id, product_id, warehouse_id, uom_id, signed_qty, txn_type, source_system, posted_at, payload)
        VALUES ('00000000-0000-0000-0000-000000005001','G2-READ-INJ',1,1,1,1,'receipt','g',now(),'{}'::jsonb);
        v_insert_ok := true;
    EXCEPTION WHEN insufficient_privilege THEN v_insert_ok := false; END;
    RESET ROLE;
    PERFORM tc('TC-572', 'inventoryledger_reader can SELECT mv_atp but NOT INSERT inv_transaction',
        v_select_ok AND NOT v_insert_ok);
END$$;

-- ============================================================================
-- GROUP O: load_opening_balance edges
-- ============================================================================
DO $$
DECLARE v_raised boolean := false;
BEGIN
    BEGIN
        PERFORM load_opening_balance(99999);  -- non-existent batch
    EXCEPTION WHEN OTHERS THEN v_raised := true; END;
    PERFORM tc('TC-580', 'load_opening_balance(non-existent batch) raises descriptive error', v_raised);
END$$;

-- Empty batch (no rows in staging)
INSERT INTO staging.ob_load_batch (file_name, file_hash) VALUES ('empty.csv', 'EMPTY-HASH-' || gen_random_uuid()::text);
SELECT tc('TC-581', 'empty batch loads with accepted=0, rejected=0',
    (SELECT accepted_count = 0 AND rejected_count = 0 FROM load_opening_balance(
        (SELECT max(batch_id) FROM staging.ob_load_batch)
    )));

-- TC-582: rerun on already-loaded batch returns same counts (idempotent)
WITH last_batch AS (SELECT max(batch_id) AS bid FROM staging.ob_load_batch)
SELECT tc('TC-582', 'rerun load_opening_balance on already-loaded batch returns same counts',
    (SELECT accepted_count = 0 AND rejected_count = 0 FROM load_opening_balance((SELECT bid FROM last_batch))));

-- TC-583: row with whitespace-only codes (e.g. tenant_code = '   ')
INSERT INTO staging.ob_load_batch (file_name, file_hash) VALUES ('whitespace.csv', 'WS-HASH-' || gen_random_uuid()::text);
INSERT INTO staging.ob_load (batch_id, tenant_code, product_code, warehouse_code, lot_code, uom_code, qty, as_of_date, source_ref, line_no)
SELECT (SELECT max(batch_id) FROM staging.ob_load_batch),
       '   ', 'GUER-P', 'GUER-W', NULL, 'EA', '5', '2026-05-19', 'g', 1;

SELECT tc('TC-583', 'whitespace-only tenant_code rejected (not silently treated as GUER)',
    (SELECT rejected_count >= 1 FROM load_opening_balance((SELECT max(batch_id) FROM staging.ob_load_batch))));

-- TC-584: mixed-case codes — DB uses tenant_code='GUER' but staging row has 'guer'
INSERT INTO staging.ob_load_batch (file_name, file_hash) VALUES ('mixedcase.csv', 'MC-HASH-' || gen_random_uuid()::text);
INSERT INTO staging.ob_load (batch_id, tenant_code, product_code, warehouse_code, lot_code, uom_code, qty, as_of_date, source_ref, line_no)
SELECT (SELECT max(batch_id) FROM staging.ob_load_batch),
       'guer', 'GUER-P', 'GUER-W', NULL, 'EA', '5', '2026-05-19', 'g', 1;

SELECT tc('TC-584', 'lowercase tenant_code rejected when DB has uppercase (case-sensitive lookup)',
    (SELECT rejected_count >= 1 FROM load_opening_balance((SELECT max(batch_id) FROM staging.ob_load_batch))));

-- ============================================================================
-- Print results
-- ============================================================================
\echo
\echo === GUERRILLA TEST SUMMARY ===
SELECT status, count(*) FROM tc_result GROUP BY status ORDER BY 1;

\echo
\echo === FAILS (each represents a real bug or design gap) ===
SELECT tc_id, description FROM tc_result WHERE status='FAIL' ORDER BY tc_id;

\echo
\echo === ALL RESULTS ===
SELECT tc_id, status, description FROM tc_result ORDER BY tc_id;

\COPY (SELECT tc_id, status, description, detail FROM tc_result ORDER BY tc_id) TO '/tmp/tc_guerrilla.csv' WITH CSV HEADER
