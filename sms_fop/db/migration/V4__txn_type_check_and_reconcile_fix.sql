-- V4__txn_type_check_and_reconcile_fix.sql
--
-- Two corrections surfaced by end-to-end testing:
--   1. inv_transaction.txn_type accepted any string. Add CHECK constraint
--      enumerating the supported values. New values can be added via a follow-up
--      migration when business needs them.
--   2. f_reconcile_orders fired on every signed_qty<0 transaction — meaning
--      a negative adjustment, scrap, transfer_out, or purchase_return would
--      falsely stamp an open customer order. Tighten so only true customer
--      shipments (txn_type='issue' or 'shipment') reconcile orders.
--
-- Supported txn_types (and their typical sign):
--   receipt          +    inbound from supplier
--   issue            -    customer shipment  (reconciles open orders)
--   shipment         -    customer shipment  (alias of issue; reconciles open orders)
--   transfer_out     -    leaves source warehouse (paired with transfer_in)
--   transfer_in      +    arrives at destination warehouse
--   adjustment       +/-  physical count discrepancy
--   sales_return     +    customer returns goods to us
--   purchase_return  -    we return goods to supplier
--   scrap            -    damaged / written-off stock

-- ============================================================================
-- 1. CHECK constraint on inv_transaction.txn_type
-- Idempotent — V1 added this inline in the CREATE TABLE; on databases where V1
-- predated the inline constraint, this DO block backfills it.
-- ============================================================================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
         WHERE conname = 'inv_transaction_txn_type_check'
           AND conrelid = 'inv_transaction'::regclass
    ) THEN
        ALTER TABLE inv_transaction
            ADD CONSTRAINT inv_transaction_txn_type_check
            CHECK (txn_type IN (
                'receipt',
                'issue', 'shipment',
                'transfer_out', 'transfer_in',
                'adjustment',
                'sales_return', 'purchase_return',
                'scrap'
            ));
    END IF;
END$$;

-- ============================================================================
-- 2. Tighten f_reconcile_orders — only customer shipments stamp orders
-- ============================================================================
CREATE OR REPLACE FUNCTION f_reconcile_orders()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
    v_matched_id text;
    v_matched_no int;
BEGIN
    -- Reconcile only true customer shipments. An adjustment, purchase_return,
    -- transfer_out or scrap is outbound but must NOT mark a customer order
    -- as fulfilled.
    IF NEW.signed_qty >= 0 THEN
        RETURN NEW;
    END IF;
    IF NEW.txn_type NOT IN ('issue', 'shipment') THEN
        RETURN NEW;
    END IF;

    SELECT sfdc_order_id, line_no
      INTO v_matched_id, v_matched_no
      FROM sfdc_order_line
     WHERE tenant_id    = NEW.tenant_id
       AND product_id   = NEW.product_id
       AND warehouse_id = NEW.warehouse_id
       AND line_state IN ('open', 'synced')
       AND fop_synced_at IS NULL
       AND qty <= ABS(NEW.signed_qty)
     ORDER BY created_at
     LIMIT 1;

    IF v_matched_id IS NOT NULL THEN
        UPDATE sfdc_order_line
           SET fop_synced_at = now(),
               line_state    = 'synced',
               updated_at    = now()
         WHERE sfdc_order_id = v_matched_id
           AND line_no       = v_matched_no;
    END IF;

    RETURN NEW;
END$$;

-- The trigger itself doesn't need re-creation; only the function body changed.
