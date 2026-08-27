-- V1__core_schema.sql
-- Inventory Ledger core schema.
-- All names lowercase snake_case. Tenant model: uuid tenant_id on every domain table.
-- RLS enabled with NULL-bypass on app.tenant_id setting; tighten to BYPASSRLS role pattern
-- once role infrastructure is provisioned (deployment concern).
--
-- Defaults to revisit when SPEC.md open questions are answered:
--   Q1: tenant_id type (currently uuid)
--   Q4: master data ownership (currently we own product/warehouse/lot/uom)
--   Q5: ATP scope (currently warehouse-scoped; mv_atp aggregates above lot)
--   Q6: opening balance CSV layout (currently generic codes + qty + as_of_date)
--
-- Required extensions (created by privileged bootstrap migration, not here):
--   pgcrypto, pg_partman. pg_cron is added via Azure server parameter.
--   pgTAP is dev/CI only (not on Azure allowlist).

-- ============================================================================
-- Schemas
-- ============================================================================
CREATE SCHEMA IF NOT EXISTS staging;
CREATE SCHEMA IF NOT EXISTS audit;
CREATE SCHEMA IF NOT EXISTS archive;

-- ============================================================================
-- Tenant + master data
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
-- Opening balance (live)
-- ============================================================================
CREATE TABLE opening_balance (
    opening_balance_id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id          uuid NOT NULL REFERENCES tenant(tenant_id),
    product_id         bigint NOT NULL REFERENCES product(product_id),
    warehouse_id       bigint NOT NULL REFERENCES warehouse(warehouse_id),
    lot_id             bigint REFERENCES lot(lot_id),
    qty                numeric(18, 4) NOT NULL,
    uom_id             bigint NOT NULL REFERENCES uom(uom_id),
    as_of_date         date NOT NULL,
    batch_id           bigint NOT NULL,
    created_at         timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_opening_balance_key
    ON opening_balance (tenant_id, product_id, warehouse_id, COALESCE(lot_id, 0), as_of_date);

CREATE INDEX idx_opening_balance_tenant_product
    ON opening_balance (tenant_id, product_id, warehouse_id);

-- ============================================================================
-- Inventory transaction (monthly partitions; pg_partman manages future)
-- positive signed_qty = inbound, negative = outbound. Transfers post as two
-- paired rows sharing transfer_pair_id.
-- FK enforcement on partitioned tables is deferred (validated by triggers/functions).
-- ============================================================================
CREATE TABLE inv_transaction (
    inv_transaction_id bigint GENERATED ALWAYS AS IDENTITY,
    tenant_id          uuid NOT NULL,
    external_txn_id    text NOT NULL,
    product_id         bigint NOT NULL,
    warehouse_id       bigint NOT NULL,
    lot_id             bigint,
    signed_qty         numeric(18, 4) NOT NULL,
    uom_id             bigint NOT NULL,
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
    ))
) PARTITION BY RANGE (posted_at);

CREATE TABLE inv_transaction_default PARTITION OF inv_transaction DEFAULT;

CREATE INDEX idx_inv_transaction_tenant_pw_posted
    ON inv_transaction (tenant_id, product_id, warehouse_id, posted_at);

CREATE INDEX idx_inv_transaction_external_id
    ON inv_transaction (tenant_id, external_txn_id);

CREATE INDEX idx_inv_transaction_transfer_pair
    ON inv_transaction (transfer_pair_id) WHERE transfer_pair_id IS NOT NULL;

-- ============================================================================
-- Stock balance (current position; per tenant × product × warehouse × lot)
-- ============================================================================
CREATE TABLE stock_balance (
    tenant_id        uuid NOT NULL REFERENCES tenant(tenant_id),
    product_id       bigint NOT NULL REFERENCES product(product_id),
    warehouse_id     bigint NOT NULL REFERENCES warehouse(warehouse_id),
    lot_id           bigint NOT NULL DEFAULT 0,
    on_hand_qty      numeric(18, 4) NOT NULL DEFAULT 0,
    uom_id           bigint NOT NULL REFERENCES uom(uom_id),
    last_updated_at  timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (tenant_id, product_id, warehouse_id, lot_id)
);

-- ============================================================================
-- Salesforce order + lines
-- ============================================================================
CREATE TABLE sfdc_order (
    sfdc_order_id text PRIMARY KEY,
    tenant_id     uuid NOT NULL REFERENCES tenant(tenant_id),
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
    sfdc_order_id text NOT NULL REFERENCES sfdc_order(sfdc_order_id) ON DELETE CASCADE,
    line_no       int NOT NULL,
    tenant_id     uuid NOT NULL,
    product_id    bigint NOT NULL REFERENCES product(product_id),
    warehouse_id  bigint NOT NULL REFERENCES warehouse(warehouse_id),
    qty           numeric(18, 4) NOT NULL,
    uom_id        bigint NOT NULL REFERENCES uom(uom_id),
    line_state    text NOT NULL DEFAULT 'open',
    fop_synced_at timestamptz,
    payload       jsonb NOT NULL,
    created_at    timestamptz NOT NULL DEFAULT now(),
    updated_at    timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (sfdc_order_id, line_no),
    CHECK (line_state IN ('open', 'synced', 'closed', 'cancelled'))
);

CREATE INDEX idx_sfdc_order_line_tenant_pw_state
    ON sfdc_order_line (tenant_id, product_id, warehouse_id, line_state);

CREATE INDEX idx_sfdc_order_line_pending
    ON sfdc_order_line (tenant_id, fop_synced_at)
    WHERE fop_synced_at IS NULL AND line_state IN ('open', 'synced');

-- ============================================================================
-- Audit log (partitioned monthly)
-- ============================================================================
CREATE TABLE audit.audit_log (
    audit_log_id bigint GENERATED ALWAYS AS IDENTITY,
    tenant_id    uuid,
    table_schema text NOT NULL,
    table_name   text NOT NULL,
    operation    char(1) NOT NULL,
    changed_at   timestamptz NOT NULL DEFAULT now(),
    changed_by   text NOT NULL DEFAULT current_user,
    before_data  jsonb,
    after_data   jsonb,
    PRIMARY KEY (audit_log_id, changed_at),
    CHECK (operation IN ('I', 'U', 'D'))
) PARTITION BY RANGE (changed_at);

CREATE TABLE audit.audit_log_default PARTITION OF audit.audit_log DEFAULT;

CREATE INDEX idx_audit_log_tenant_changed ON audit.audit_log (tenant_id, changed_at);
CREATE INDEX idx_audit_log_table          ON audit.audit_log (table_schema, table_name, changed_at);

-- ============================================================================
-- Notification outbox (drained by Java daily run)
-- ============================================================================
CREATE TABLE notification_outbox (
    notification_outbox_id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id              uuid,
    severity               text NOT NULL,
    source                 text NOT NULL,
    message                text NOT NULL,
    payload                jsonb,
    status                 text NOT NULL DEFAULT 'pending',
    retry_count            int NOT NULL DEFAULT 0,
    last_attempt_at        timestamptz,
    delivered_at           timestamptz,
    created_at             timestamptz NOT NULL DEFAULT now(),
    updated_at             timestamptz NOT NULL DEFAULT now(),
    CHECK (severity IN ('info', 'warn', 'error', 'critical')),
    CHECK (status   IN ('pending', 'delivered', 'failed', 'failed_permanent'))
);

CREATE INDEX idx_notification_outbox_pending
    ON notification_outbox (created_at)
    WHERE status IN ('pending', 'failed');

-- ============================================================================
-- Staging tables
-- ob_load_batch: file-level dedup (sha256) + run summary
-- ob_load: raw rows as text columns; parsed in load_opening_balance
-- ob_reject: rows that failed validation
-- txn_inbox / order_inbox: optional buffer if other teams stage instead of
-- calling functions directly. Both UNIQUEd on natural keys for idempotency.
-- ============================================================================
CREATE TABLE staging.ob_load_batch (
    batch_id       bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id      uuid,
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
    tenant_id       uuid NOT NULL,
    external_txn_id text NOT NULL,
    payload         jsonb NOT NULL,
    received_at     timestamptz NOT NULL DEFAULT now(),
    status          text NOT NULL DEFAULT 'pending',
    processed_at    timestamptz,
    error_detail    text,
    UNIQUE (tenant_id, external_txn_id),
    CHECK (status IN ('pending', 'processed', 'rejected'))
);

CREATE INDEX idx_txn_inbox_status ON staging.txn_inbox (status, received_at);

CREATE TABLE staging.order_inbox (
    order_inbox_id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id      uuid NOT NULL,
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
-- MV dirty marker — populated by triggers; cleared by refresh_mv_atp
-- ============================================================================
CREATE TABLE mv_atp_dirty (
    tenant_id uuid PRIMARY KEY,
    marked_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================================
-- Materialized view: ATP per (tenant, product, warehouse)
-- atp = on_hand − pending (open + synced orders not yet fop-confirmed)
-- on_hand = opening_balance.qty + Σ inv_transaction.signed_qty
-- Built from a UNION of activity keys to avoid empty rows from cross-joins.
-- ============================================================================
CREATE MATERIALIZED VIEW mv_atp AS
WITH keys AS (
    SELECT DISTINCT tenant_id, product_id, warehouse_id FROM opening_balance
    UNION
    SELECT DISTINCT tenant_id, product_id, warehouse_id FROM inv_transaction
    UNION
    SELECT DISTINCT tenant_id, product_id, warehouse_id FROM sfdc_order_line
    WHERE line_state IN ('open', 'synced')
)
SELECT
    k.tenant_id,
    k.product_id,
    k.warehouse_id,
    COALESCE((SELECT SUM(qty)        FROM opening_balance ob
              WHERE ob.tenant_id = k.tenant_id AND ob.product_id = k.product_id
                AND ob.warehouse_id = k.warehouse_id), 0)
      + COALESCE((SELECT SUM(signed_qty) FROM inv_transaction it
                  WHERE it.tenant_id = k.tenant_id AND it.product_id = k.product_id
                    AND it.warehouse_id = k.warehouse_id), 0) AS on_hand_qty,
    COALESCE((SELECT SUM(qty) FROM sfdc_order_line ol
              WHERE ol.tenant_id = k.tenant_id AND ol.product_id = k.product_id
                AND ol.warehouse_id = k.warehouse_id AND ol.line_state = 'open'), 0) AS reserved_qty,
    COALESCE((SELECT SUM(qty) FROM sfdc_order_line ol
              WHERE ol.tenant_id = k.tenant_id AND ol.product_id = k.product_id
                AND ol.warehouse_id = k.warehouse_id AND ol.line_state = 'synced'), 0) AS allocated_qty,
    COALESCE((SELECT SUM(qty) FROM sfdc_order_line ol
              WHERE ol.tenant_id = k.tenant_id AND ol.product_id = k.product_id
                AND ol.warehouse_id = k.warehouse_id
                AND ol.line_state IN ('open', 'synced')
                AND ol.fop_synced_at IS NULL), 0) AS pending_qty,
    (
      COALESCE((SELECT SUM(qty)        FROM opening_balance ob
                WHERE ob.tenant_id = k.tenant_id AND ob.product_id = k.product_id
                  AND ob.warehouse_id = k.warehouse_id), 0)
      + COALESCE((SELECT SUM(signed_qty) FROM inv_transaction it
                  WHERE it.tenant_id = k.tenant_id AND it.product_id = k.product_id
                    AND it.warehouse_id = k.warehouse_id), 0)
      - COALESCE((SELECT SUM(qty) FROM sfdc_order_line ol
                  WHERE ol.tenant_id = k.tenant_id AND ol.product_id = k.product_id
                    AND ol.warehouse_id = k.warehouse_id
                    AND ol.line_state IN ('open', 'synced')
                    AND ol.fop_synced_at IS NULL), 0)
    ) AS atp_qty,
    now() AS computed_at
FROM keys k;

CREATE UNIQUE INDEX idx_mv_atp_key ON mv_atp (tenant_id, product_id, warehouse_id);

-- ============================================================================
-- Row level security
-- Relaxed policy: when app.tenant_id is unset (admin/migration), no filter.
-- Tighten to BYPASSRLS-role pattern once role infrastructure exists.
-- ============================================================================
-- ENABLE applies RLS to non-owners. FORCE also applies to owners (e.g. the
-- migration role), so RLS is honoured even when the connecting user owns the
-- table. Without FORCE, tests run as superuser/owner bypass policies entirely.
ALTER TABLE tenant              ENABLE ROW LEVEL SECURITY; ALTER TABLE tenant              FORCE ROW LEVEL SECURITY;
ALTER TABLE product             ENABLE ROW LEVEL SECURITY; ALTER TABLE product             FORCE ROW LEVEL SECURITY;
ALTER TABLE warehouse           ENABLE ROW LEVEL SECURITY; ALTER TABLE warehouse           FORCE ROW LEVEL SECURITY;
ALTER TABLE uom                 ENABLE ROW LEVEL SECURITY; ALTER TABLE uom                 FORCE ROW LEVEL SECURITY;
ALTER TABLE lot                 ENABLE ROW LEVEL SECURITY; ALTER TABLE lot                 FORCE ROW LEVEL SECURITY;
ALTER TABLE opening_balance     ENABLE ROW LEVEL SECURITY; ALTER TABLE opening_balance     FORCE ROW LEVEL SECURITY;
ALTER TABLE inv_transaction     ENABLE ROW LEVEL SECURITY; ALTER TABLE inv_transaction     FORCE ROW LEVEL SECURITY;
ALTER TABLE stock_balance       ENABLE ROW LEVEL SECURITY; ALTER TABLE stock_balance       FORCE ROW LEVEL SECURITY;
ALTER TABLE sfdc_order          ENABLE ROW LEVEL SECURITY; ALTER TABLE sfdc_order          FORCE ROW LEVEL SECURITY;
ALTER TABLE sfdc_order_line     ENABLE ROW LEVEL SECURITY; ALTER TABLE sfdc_order_line     FORCE ROW LEVEL SECURITY;
ALTER TABLE notification_outbox ENABLE ROW LEVEL SECURITY; ALTER TABLE notification_outbox FORCE ROW LEVEL SECURITY;

DO $$
DECLARE
    t  text;
    qn text;
BEGIN
    FOR t, qn IN
        SELECT * FROM (VALUES
            ('public', 'tenant'),
            ('public', 'product'),
            ('public', 'warehouse'),
            ('public', 'uom'),
            ('public', 'lot'),
            ('public', 'opening_balance'),
            ('public', 'inv_transaction'),
            ('public', 'stock_balance'),
            ('public', 'sfdc_order'),
            ('public', 'sfdc_order_line'),
            ('public', 'notification_outbox')
        ) AS v(schema_name, table_name)
    LOOP
        EXECUTE format($f$
            CREATE POLICY tenant_isolation ON %I.%I
            USING (
                current_setting('app.tenant_id', true) IS NULL
                OR tenant_id::text = current_setting('app.tenant_id', true)
            )
            WITH CHECK (
                current_setting('app.tenant_id', true) IS NULL
                OR tenant_id::text = current_setting('app.tenant_id', true)
            );
        $f$, t, qn);
    END LOOP;
END$$;
