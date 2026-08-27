-- inventoryledger_schema.sql
--
-- One-shot DDL setup for the Inventory Ledger functional schema.
-- Scope: live (public) + staging tables only. NO functions, triggers,
-- materialized views, audit log, outbox, pipeline_config, pg_partman, or
-- pg_cron — those live in the Flyway migrations (db/migration/V*.sql) and
-- are applied by the full deployment path.
--
-- Design principles:
--   - Core fact tables (inv_transaction, stock_balance, opening_balance,
--     sfdc_order_line) carry BOTH surrogate IDs and natural-key codes.
--     This makes them self-describing — no joins needed to read.
--   - Master tables (product, warehouse, uom, lot) are kept for referential
--     integrity and dedup, but are auto-populated by drain functions on
--     first encounter. They are NOT prerequisites.
--   - Staging tables are 100% flat text — no UUIDs, no FKs to live tables.
--     External systems use tenant_code, not tenant_id.
--
-- Apply to a fresh database:
--     createdb inventoryledger
--     psql -d inventoryledger -f inventoryledger_schema.sql
--
-- Requires PostgreSQL 13+ (gen_random_uuid() is built in).
-- Runs in a single transaction so a failure rolls everything back.

BEGIN;

-- ============================================================================
-- Schemas
-- ============================================================================
CREATE SCHEMA IF NOT EXISTS staging;

COMMENT ON SCHEMA public  IS
    'Live business tables. External readers/writers do NOT have direct DML here — they go through staging.* (write) and a read function (added by the full Flyway setup).';
COMMENT ON SCHEMA staging IS
    'Inbox + landing area for external writers (txn_inbox, order_inbox, ob_load*). All columns are text codes — no UUIDs or surrogate IDs.';

-- ============================================================================
-- Tenant + master data (live)
-- Auto-populated by drain functions on first encounter of a new code.
-- ============================================================================
CREATE TABLE tenant (
    tenant_id    uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_code  text NOT NULL UNIQUE,
    name         text NOT NULL,
    is_active    boolean NOT NULL DEFAULT true,
    created_at   timestamptz NOT NULL DEFAULT now(),
    updated_at   timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE product (
    product_id   bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id    uuid NOT NULL REFERENCES tenant(tenant_id),
    product_code text NOT NULL,
    name         text NOT NULL,
    is_active    boolean NOT NULL DEFAULT true,
    created_at   timestamptz NOT NULL DEFAULT now(),
    updated_at   timestamptz NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, product_code)
);

CREATE TABLE warehouse (
    warehouse_id   bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id      uuid NOT NULL REFERENCES tenant(tenant_id),
    warehouse_code text NOT NULL,
    name           text NOT NULL,
    is_active      boolean NOT NULL DEFAULT true,
    created_at     timestamptz NOT NULL DEFAULT now(),
    updated_at     timestamptz NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, warehouse_code)
);

CREATE TABLE uom (
    uom_id     bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id  uuid NOT NULL REFERENCES tenant(tenant_id),
    uom_code   text NOT NULL,
    name       text NOT NULL,
    is_active  boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, uom_code)
);

CREATE TABLE lot (
    lot_id     bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id  uuid NOT NULL REFERENCES tenant(tenant_id),
    product_id bigint NOT NULL REFERENCES product(product_id),
    lot_code   text NOT NULL,
    is_active  boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, product_id, lot_code)
);

-- ============================================================================
-- Opening balance (live) — starting position per tenant/product/warehouse.
-- Carries codes inline so the row is self-describing without joins.
-- ============================================================================
CREATE TABLE opening_balance (
    opening_balance_id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id          uuid NOT NULL REFERENCES tenant(tenant_id),
    tenant_code        text NOT NULL,
    product_id         bigint NOT NULL REFERENCES product(product_id),
    product_code       text NOT NULL,
    warehouse_id       bigint NOT NULL REFERENCES warehouse(warehouse_id),
    warehouse_code     text NOT NULL,
    lot_id             bigint REFERENCES lot(lot_id),
    lot_code           text,
    qty                numeric(18, 4) NOT NULL,
    uom_id             bigint NOT NULL REFERENCES uom(uom_id),
    uom_code           text NOT NULL,
    as_of_date         date NOT NULL,
    batch_id           bigint NOT NULL,
    created_at         timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_opening_balance_key
    ON opening_balance (tenant_id, product_id, warehouse_id, COALESCE(lot_id, 0), as_of_date);
CREATE INDEX idx_opening_balance_tenant_product
    ON opening_balance (tenant_id, product_id, warehouse_id);

-- ============================================================================
-- Inventory transaction (live) — immutable ledger
--   Partitioned annually by posted_at (pg_partman manages additional partitions
--   in the full deployment; for this setup script we ship the parent + a
--   DEFAULT partition so the table is immediately usable).
--   signed_qty: receipts/transfer_in/sales_return > 0; issues/shipments/
--   transfer_out/purchase_return/scrap < 0; adjustment can be either.
--   Transfers post as a paired (out + in) row sharing transfer_pair_id.
--   Carries codes inline so the ledger is self-describing without joins.
-- ============================================================================
CREATE TABLE inv_transaction (
    inv_transaction_id bigint GENERATED ALWAYS AS IDENTITY,
    tenant_id          uuid NOT NULL,
    tenant_code        text NOT NULL,
    external_txn_id    text NOT NULL,
    product_id         bigint NOT NULL,
    product_code       text NOT NULL,
    warehouse_id       bigint NOT NULL,
    warehouse_code     text NOT NULL,
    lot_id             bigint,
    lot_code           text,
    signed_qty         numeric(18, 4) NOT NULL,
    uom_id             bigint NOT NULL,
    uom_code           text NOT NULL,
    txn_type           text NOT NULL,
    transfer_pair_id   uuid,
    source_system      text NOT NULL,
    posted_at          timestamptz NOT NULL,
    payload            jsonb NOT NULL,
    created_at         timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (inv_transaction_id, posted_at),
    UNIQUE (tenant_id, external_txn_id, posted_at),
    CONSTRAINT inv_transaction_txn_type_check CHECK (txn_type IN (
        'receipt',
        'issue', 'shipment',
        'transfer_out', 'transfer_in',
        'adjustment',
        'sales_return', 'purchase_return',
        'scrap'
    )),
    CONSTRAINT inv_transaction_sign_check CHECK (
        (txn_type = 'receipt'             AND signed_qty > 0)  OR
        (txn_type IN ('issue','shipment') AND signed_qty < 0)  OR
        (txn_type = 'transfer_out'        AND signed_qty < 0)  OR
        (txn_type = 'transfer_in'         AND signed_qty > 0)  OR
        (txn_type = 'adjustment'          AND signed_qty <> 0) OR
        (txn_type = 'sales_return'        AND signed_qty > 0)  OR
        (txn_type = 'purchase_return'     AND signed_qty < 0)  OR
        (txn_type = 'scrap'               AND signed_qty < 0)
    ),
    CONSTRAINT inv_transaction_tenant_id_fkey    FOREIGN KEY (tenant_id)    REFERENCES tenant(tenant_id),
    CONSTRAINT inv_transaction_product_id_fkey   FOREIGN KEY (product_id)   REFERENCES product(product_id),
    CONSTRAINT inv_transaction_warehouse_id_fkey FOREIGN KEY (warehouse_id) REFERENCES warehouse(warehouse_id),
    CONSTRAINT inv_transaction_lot_id_fkey       FOREIGN KEY (lot_id)       REFERENCES lot(lot_id),
    CONSTRAINT inv_transaction_uom_id_fkey       FOREIGN KEY (uom_id)       REFERENCES uom(uom_id)
) PARTITION BY RANGE (posted_at);

CREATE TABLE inv_transaction_default PARTITION OF inv_transaction DEFAULT;

CREATE INDEX idx_inv_transaction_tenant_pw_posted
    ON inv_transaction (tenant_id, product_id, warehouse_id, posted_at);
CREATE INDEX idx_inv_transaction_external_id
    ON inv_transaction (tenant_id, external_txn_id);
CREATE INDEX idx_inv_transaction_transfer_pair
    ON inv_transaction (transfer_pair_id) WHERE transfer_pair_id IS NOT NULL;

-- ============================================================================
-- Stock balance (live) — current position per tenant/product/warehouse/lot.
-- Carries codes inline so the balance is readable without joins.
-- ============================================================================
CREATE TABLE stock_balance (
    tenant_id        uuid NOT NULL REFERENCES tenant(tenant_id),
    tenant_code      text NOT NULL,
    product_id       bigint NOT NULL REFERENCES product(product_id),
    product_code     text NOT NULL,
    warehouse_id     bigint NOT NULL REFERENCES warehouse(warehouse_id),
    warehouse_code   text NOT NULL,
    lot_id           bigint NOT NULL DEFAULT 0,
    lot_code         text,
    on_hand_qty      numeric(18, 4) NOT NULL DEFAULT 0,
    uom_id           bigint NOT NULL REFERENCES uom(uom_id),
    uom_code         text NOT NULL,
    last_updated_at  timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (tenant_id, product_id, warehouse_id, lot_id)
);

-- ============================================================================
-- Salesforce order + lines (live).
-- Lines carry codes inline for the same self-describing reason.
-- ============================================================================
CREATE TABLE sfdc_order (
    sfdc_order_id text PRIMARY KEY,
    tenant_id     uuid NOT NULL REFERENCES tenant(tenant_id),
    tenant_code   text NOT NULL,
    customer_id   text NOT NULL,
    order_state   text NOT NULL DEFAULT 'open',
    payload       jsonb NOT NULL,
    created_at    timestamptz NOT NULL DEFAULT now(),
    updated_at    timestamptz NOT NULL DEFAULT now(),
    CHECK (order_state IN ('open', 'synced', 'closed', 'cancelled'))
);

CREATE INDEX idx_sfdc_order_tenant_state
    ON sfdc_order (tenant_id, order_state);

CREATE TABLE sfdc_order_line (
    sfdc_order_id  text NOT NULL REFERENCES sfdc_order(sfdc_order_id) ON DELETE CASCADE,
    line_no        int NOT NULL,
    tenant_id      uuid NOT NULL,
    tenant_code    text NOT NULL,
    product_id     bigint NOT NULL REFERENCES product(product_id),
    product_code   text NOT NULL,
    warehouse_id   bigint NOT NULL REFERENCES warehouse(warehouse_id),
    warehouse_code text NOT NULL,
    qty            numeric(18, 4) NOT NULL,
    uom_id         bigint NOT NULL REFERENCES uom(uom_id),
    uom_code       text NOT NULL,
    line_state     text NOT NULL DEFAULT 'open',
    fop_synced_at  timestamptz,
    payload        jsonb NOT NULL,
    created_at     timestamptz NOT NULL DEFAULT now(),
    updated_at     timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (sfdc_order_id, line_no),
    CHECK (line_state IN ('open', 'synced', 'closed', 'cancelled')),
    CONSTRAINT sfdc_order_line_qty_check CHECK (qty > 0)
);

CREATE INDEX idx_sfdc_order_line_tenant_pw_state
    ON sfdc_order_line (tenant_id, product_id, warehouse_id, line_state);
CREATE INDEX idx_sfdc_order_line_pending
    ON sfdc_order_line (tenant_id, fop_synced_at)
    WHERE fop_synced_at IS NULL AND line_state IN ('open', 'synced');

-- ============================================================================
-- Staging tables — 100% flat, text codes only, no UUIDs or surrogate IDs.
--   ob_load_batch / ob_load / ob_reject — Pipeline 1 (opening-balance CSV)
--   txn_inbox / order_inbox            — Pipelines 2 & 3 (txn + SFDC order)
-- The "ob_" prefix is short for "opening_balance", not "outbound".
-- ============================================================================
CREATE TABLE staging.ob_load_batch (
    batch_id       bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_code    text,
    file_name      text NOT NULL,
    file_hash      text NOT NULL UNIQUE,
    received_at    timestamptz NOT NULL DEFAULT now(),
    status         text NOT NULL DEFAULT 'in_progress',
    row_count      int,
    accepted_count int,
    rejected_count int,
    completed_at   timestamptz,
    CHECK (status IN ('in_progress', 'loaded', 'rejected'))
);

CREATE TABLE staging.ob_load (
    ob_load_id     bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    batch_id       bigint NOT NULL REFERENCES staging.ob_load_batch(batch_id),
    tenant_code    text,
    product_code   text,
    warehouse_code text,
    lot_code       text,
    uom_code       text,
    qty            text,
    as_of_date     text,
    source_ref     text,
    line_no        int
);
CREATE INDEX idx_ob_load_batch ON staging.ob_load (batch_id);

CREATE TABLE staging.ob_reject (
    ob_reject_id  bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    batch_id      bigint NOT NULL REFERENCES staging.ob_load_batch(batch_id),
    ob_load_id    bigint,
    reason_code   text NOT NULL,
    reason_detail text,
    raw_line      jsonb,
    rejected_at   timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX idx_ob_reject_batch ON staging.ob_reject (batch_id);

CREATE TABLE staging.txn_inbox (
    txn_inbox_id    bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_code     text NOT NULL,
    external_txn_id text NOT NULL,
    payload         jsonb NOT NULL,
    received_at     timestamptz NOT NULL DEFAULT now(),
    status          text NOT NULL DEFAULT 'pending',
    processed_at    timestamptz,
    error_detail    text,
    UNIQUE (tenant_code, external_txn_id),
    CHECK (status IN ('pending', 'processed', 'rejected'))
);
CREATE INDEX idx_txn_inbox_status ON staging.txn_inbox (status, received_at);

CREATE TABLE staging.order_inbox (
    order_inbox_id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_code    text NOT NULL,
    sfdc_order_id  text NOT NULL,
    payload        jsonb NOT NULL,
    received_at    timestamptz NOT NULL DEFAULT now(),
    status         text NOT NULL DEFAULT 'pending',
    processed_at   timestamptz,
    error_detail   text,
    CHECK (status IN ('pending', 'processed', 'rejected'))
);
CREATE INDEX idx_order_inbox_status ON staging.order_inbox (status, received_at);

-- ============================================================================
-- Documentation
-- ============================================================================
COMMENT ON TABLE tenant          IS 'Top-level tenant boundary. Every business row carries both tenant_id (surrogate) and tenant_code (natural key).';
COMMENT ON TABLE product         IS 'Per-tenant product master. Auto-populated by drain functions on first encounter. (tenant_id, product_code) is the natural key.';
COMMENT ON TABLE warehouse       IS 'Per-tenant warehouse / location master. Auto-populated by drain functions on first encounter. (tenant_id, warehouse_code) is the natural key.';
COMMENT ON TABLE uom             IS 'Per-tenant unit-of-measure master (EA, KG, SET, ...). Auto-populated by drain functions on first encounter.';
COMMENT ON TABLE lot             IS 'Optional lot / batch identifier for products that track lots. Auto-populated by drain functions on first encounter.';
COMMENT ON TABLE opening_balance IS 'Per-tenant starting stock position as of as_of_date. Loaded by Pipeline 1. Self-describing: carries codes inline alongside surrogate IDs.';
COMMENT ON TABLE inv_transaction IS
    'Immutable inventory movement ledger. Partitioned annually by posted_at. Self-describing: carries product_code, warehouse_code, uom_code, lot_code inline. Receipts and inbound transfers > 0; issues, shipments, outbound transfers < 0. Transfers post as a paired (out + in) row sharing transfer_pair_id. external_txn_id is the caller-supplied idempotency key (UNIQUE per tenant + posted_at).';
COMMENT ON TABLE stock_balance   IS 'Current position per (tenant, product, warehouse, lot). Self-describing: carries codes inline. Maintained by application logic from opening_balance + inv_transaction.';
COMMENT ON TABLE sfdc_order      IS 'Salesforce order header. order_state machine: open -> synced -> closed | cancelled.';
COMMENT ON TABLE sfdc_order_line IS 'Salesforce order line. Self-describing: carries codes inline. fop_synced_at is stamped when a matching customer-shipment inv_transaction posts.';

COMMENT ON TABLE staging.ob_load_batch IS 'One row per opening-balance CSV ingested. file_hash is UNIQUE — re-uploading the same content is a no-op. Uses tenant_code (text), not UUID.';
COMMENT ON TABLE staging.ob_load       IS 'Raw CSV rows landed as text. All columns are text codes — no UUIDs or surrogate IDs. Parsed/validated by the loader and either promoted to opening_balance or copied to ob_reject.';
COMMENT ON TABLE staging.ob_reject     IS 'Per-row reject log for opening-balance loads.';
COMMENT ON TABLE staging.txn_inbox     IS 'External write surface for inventory transactions (Pipeline 2). UNIQUE (tenant_code, external_txn_id) provides inbox-level idempotency. Uses tenant_code (text), not UUID.';
COMMENT ON TABLE staging.order_inbox   IS 'External write surface for Salesforce orders (Pipeline 3). Uses tenant_code (text), not UUID. Resends are expected as the order moves through states.';

COMMIT;
