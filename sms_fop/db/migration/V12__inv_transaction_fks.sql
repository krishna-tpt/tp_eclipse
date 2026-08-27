-- V12__inv_transaction_fks.sql
--
-- Adds the foreign-key constraints that V1 deferred. When V1 was written, PG
-- partitioned tables couldn't carry FKs at all; PG 12+ supports them and PG
-- routes the constraint down to each partition automatically.
--
-- Defence in depth: post_transaction still resolves and validates every code,
-- and f_inv_transaction_tenant_check still enforces multi-tenant consistency.
-- These FKs catch the case where a row reaches inv_transaction via any path
-- other than post_transaction (manual INSERT, future bulk loader, etc.).
--
-- Note on locking:
--   PG 16 does NOT support ADD CONSTRAINT ... NOT VALID on partitioned tables
--   (each partition would need its own validation, which the planner can't
--   defer cleanly). The ADD CONSTRAINT below therefore scans every partition
--   inline. Acceptable on first deploy (empty table); on a hot production
--   table this should run during a maintenance window or be done partition-
--   by-partition via the partman-friendly path.
-- Idempotent: wrapped in IF NOT EXISTS guards so re-runs are no-ops.

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
         WHERE conrelid = 'public.inv_transaction'::regclass
           AND conname  = 'inv_transaction_tenant_id_fkey'
    ) THEN
        ALTER TABLE public.inv_transaction
          ADD CONSTRAINT inv_transaction_tenant_id_fkey
              FOREIGN KEY (tenant_id) REFERENCES public.tenant(tenant_id);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
         WHERE conrelid = 'public.inv_transaction'::regclass
           AND conname  = 'inv_transaction_product_id_fkey'
    ) THEN
        ALTER TABLE public.inv_transaction
          ADD CONSTRAINT inv_transaction_product_id_fkey
              FOREIGN KEY (product_id) REFERENCES public.product(product_id);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
         WHERE conrelid = 'public.inv_transaction'::regclass
           AND conname  = 'inv_transaction_warehouse_id_fkey'
    ) THEN
        ALTER TABLE public.inv_transaction
          ADD CONSTRAINT inv_transaction_warehouse_id_fkey
              FOREIGN KEY (warehouse_id) REFERENCES public.warehouse(warehouse_id);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
         WHERE conrelid = 'public.inv_transaction'::regclass
           AND conname  = 'inv_transaction_lot_id_fkey'
    ) THEN
        ALTER TABLE public.inv_transaction
          ADD CONSTRAINT inv_transaction_lot_id_fkey
              FOREIGN KEY (lot_id) REFERENCES public.lot(lot_id);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
         WHERE conrelid = 'public.inv_transaction'::regclass
           AND conname  = 'inv_transaction_uom_id_fkey'
    ) THEN
        ALTER TABLE public.inv_transaction
          ADD CONSTRAINT inv_transaction_uom_id_fkey
              FOREIGN KEY (uom_id) REFERENCES public.uom(uom_id);
    END IF;
END$$;

-- Correct the V11 comment on tenant_id (was: "FK enforcement is deferred on
-- partitioned tables; post_transaction validates.").
COMMENT ON COLUMN public.inv_transaction.tenant_id IS
    'Tenant scope. Enforced by FK (V12) to tenant(tenant_id); cross-tenant integrity (product/warehouse must belong to the same tenant) is enforced by the f_inv_transaction_tenant_check trigger.';
