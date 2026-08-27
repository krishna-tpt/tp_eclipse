-- ============================================================================
-- TenthPlanet Inventory Ledger — Customer Fresh Install
-- ============================================================================
-- Single-script install for a clean PostgreSQL 16 database. Run once on a new
-- customer DB; the Java MicroService can ingest files immediately after.
--
-- Schemas:
--   processed  — live ledger (masters + opening_balance + stock_balance +
--                inv_transaction + sfdc_order + sfdc_order_line +
--                notification_outbox)
--   staging    — landing zones (stocklevel_inbox + stocklevel_batch are
--                populated by our MicroService; txn_inbox + order_inbox
--                are populated by other teams)
--   audit      — audit_log
--
-- No live tables remain in public. (Legacy CSV-header loader and its tables
-- removed.) Functions use explicit SECURITY DEFINER + search_path so writes
-- through them work for least-privilege app roles.
--
-- Applied separately (NOT in this file): pg_partman, pg_cron, role grants —
-- ops territory.
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
-- Extensions
-- ----------------------------------------------------------------------------
-- None required. gen_random_uuid() — the only UUID generator we use — has
-- been a built-in core function since PostgreSQL 13. No pgcrypto needed.
-- (If a future change pulls in digest(), encrypt(), pgp_*, etc., add the
-- extension here AND coordinate with Azure to allowlist it.)

-- ----------------------------------------------------------------------------
-- Schemas
-- ----------------------------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS processed;
CREATE SCHEMA IF NOT EXISTS staging;
CREATE SCHEMA IF NOT EXISTS audit;

-- ============================================================================
-- processed — master data
-- ============================================================================
CREATE TABLE processed.tenant (
    tenant_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_code  TEXT NOT NULL UNIQUE,
    name         TEXT NOT NULL,
    is_active    BOOLEAN NOT NULL DEFAULT true,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE processed.product (
    product_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id    UUID NOT NULL REFERENCES processed.tenant(tenant_id),
    product_code TEXT NOT NULL,
    name         TEXT NOT NULL,
    is_active    BOOLEAN NOT NULL DEFAULT true,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, product_code)
);

CREATE TABLE processed.warehouse (
    warehouse_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id      UUID NOT NULL REFERENCES processed.tenant(tenant_id),
    warehouse_code TEXT NOT NULL,
    name           TEXT NOT NULL,
    is_active      BOOLEAN NOT NULL DEFAULT true,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, warehouse_code)
);

CREATE TABLE processed.uom (
    uom_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id  UUID NOT NULL REFERENCES processed.tenant(tenant_id),
    uom_code   TEXT NOT NULL,
    name       TEXT NOT NULL,
    is_active  BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, uom_code)
);

CREATE TABLE processed.lot (
    lot_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id  UUID NOT NULL REFERENCES processed.tenant(tenant_id),
    product_id BIGINT NOT NULL REFERENCES processed.product(product_id),
    lot_code   TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, product_id, lot_code)
);

-- ============================================================================
-- processed — live ledger
-- ============================================================================
CREATE TABLE processed.opening_balance (
    opening_balance_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id          UUID NOT NULL REFERENCES processed.tenant(tenant_id),
    tenant_code        TEXT NOT NULL,
    product_id         BIGINT NOT NULL REFERENCES processed.product(product_id),
    product_code       TEXT NOT NULL,
    warehouse_id       BIGINT NOT NULL REFERENCES processed.warehouse(warehouse_id),
    warehouse_code     TEXT NOT NULL,
    lot_id             BIGINT REFERENCES processed.lot(lot_id),
    lot_code           TEXT,
    qty                NUMERIC(18, 4) NOT NULL,
    uom_id             BIGINT NOT NULL REFERENCES processed.uom(uom_id),
    uom_code           TEXT NOT NULL,
    as_of_date         DATE NOT NULL,
    batch_id           BIGINT NOT NULL,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_opening_balance_key
    ON processed.opening_balance (tenant_id, product_id, warehouse_id, COALESCE(lot_id, 0), as_of_date);
CREATE INDEX idx_opening_balance_tenant_product
    ON processed.opening_balance (tenant_id, product_id, warehouse_id);

CREATE TABLE processed.stock_balance (
    tenant_id        UUID NOT NULL REFERENCES processed.tenant(tenant_id),
    tenant_code      TEXT NOT NULL,
    product_id       BIGINT NOT NULL REFERENCES processed.product(product_id),
    product_code     TEXT NOT NULL,
    warehouse_id     BIGINT NOT NULL REFERENCES processed.warehouse(warehouse_id),
    warehouse_code   TEXT NOT NULL,
    lot_id           BIGINT NOT NULL DEFAULT 0,
    lot_code         TEXT,
    on_hand_qty      NUMERIC(18, 4) NOT NULL DEFAULT 0,
    uom_id           BIGINT NOT NULL REFERENCES processed.uom(uom_id),
    uom_code         TEXT NOT NULL,
    last_updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (tenant_id, product_id, warehouse_id, lot_id)
);

CREATE TABLE processed.inv_transaction (
    inv_transaction_id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id           UUID NOT NULL REFERENCES processed.tenant(tenant_id),
    tenant_code         TEXT NOT NULL,
    external_txn_id     TEXT NOT NULL,
    product_id          BIGINT NOT NULL REFERENCES processed.product(product_id),
    product_code        TEXT NOT NULL,
    warehouse_id        BIGINT NOT NULL REFERENCES processed.warehouse(warehouse_id),
    warehouse_code      TEXT NOT NULL,
    lot_id              BIGINT REFERENCES processed.lot(lot_id),
    lot_code            TEXT,
    signed_qty          NUMERIC(18, 4) NOT NULL,
    uom_id              BIGINT NOT NULL REFERENCES processed.uom(uom_id),
    uom_code            TEXT NOT NULL,
    txn_type            TEXT NOT NULL,
    posted_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    transfer_pair_id    UUID,
    payload             JSONB,
    UNIQUE (tenant_id, external_txn_id)
);
CREATE INDEX idx_inv_transaction_tenant_pw_posted
    ON processed.inv_transaction (tenant_id, product_id, warehouse_id, posted_at);

CREATE TABLE processed.sfdc_order (
    sfdc_order_id TEXT PRIMARY KEY,
    tenant_id     UUID NOT NULL REFERENCES processed.tenant(tenant_id),
    tenant_code   TEXT NOT NULL,
    customer_id   TEXT NOT NULL,
    order_state   TEXT NOT NULL DEFAULT 'open'
                  CHECK (order_state IN ('open','synced','closed','cancelled')),
    payload       JSONB NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE processed.sfdc_order_line (
    sfdc_order_id  TEXT NOT NULL REFERENCES processed.sfdc_order(sfdc_order_id) ON DELETE CASCADE,
    line_no        INTEGER NOT NULL,
    tenant_id      UUID NOT NULL,
    tenant_code    TEXT NOT NULL,
    product_id     BIGINT NOT NULL REFERENCES processed.product(product_id),
    product_code   TEXT NOT NULL,
    warehouse_id   BIGINT NOT NULL REFERENCES processed.warehouse(warehouse_id),
    warehouse_code TEXT NOT NULL,
    qty            NUMERIC(18, 4) NOT NULL CHECK (qty > 0),
    uom_id         BIGINT NOT NULL REFERENCES processed.uom(uom_id),
    uom_code       TEXT NOT NULL,
    line_state     TEXT NOT NULL DEFAULT 'open'
                   CHECK (line_state IN ('open','synced','closed','cancelled')),
    fop_synced_at  TIMESTAMPTZ,
    payload        JSONB NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (sfdc_order_id, line_no)
);

-- ============================================================================
-- processed — operational
-- ============================================================================
CREATE TABLE processed.notification_outbox (
    outbox_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id     UUID,
    severity      TEXT NOT NULL CHECK (severity IN ('INFO','WARN','ERROR')),
    source        TEXT NOT NULL,
    message       TEXT NOT NULL,
    payload       JSONB NOT NULL DEFAULT '{}'::jsonb,
    dedup_key     TEXT,
    repeat_count  INTEGER NOT NULL DEFAULT 1,
    status        TEXT NOT NULL DEFAULT 'pending'
                  CHECK (status IN ('pending','delivered','failed','failed_permanent')),
    retry_count   INTEGER NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    delivered_at  TIMESTAMPTZ,
    last_attempt_at TIMESTAMPTZ
);
CREATE INDEX idx_notification_outbox_pending
    ON processed.notification_outbox (status, created_at)
    WHERE status IN ('pending','failed');

-- ============================================================================
-- staging — file landing zones
-- ============================================================================
CREATE TABLE staging.stocklevel_inbox (
    inbox_id            BIGSERIAL PRIMARY KEY,
    file_name           TEXT NOT NULL,
    line_number         BIGINT NOT NULL,
    ingested_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    promoted_at         TIMESTAMPTZ,
    -- Envelope captures + literal
    tenant_code         TEXT,
    file_batch_id       TEXT,
    source_marker       TEXT,
    -- CFO data fields per Michelin "Extract Phase" spec (fields 2..55)
    company_code                  TEXT,
    organization_code             TEXT,
    organization_type             TEXT,
    finished_good_org_type        TEXT,
    oc_companycode                TEXT,
    od_countrycode                TEXT,
    or_countrycode                TEXT,
    site                          TEXT,
    item_segment1                 TEXT NOT NULL,
    parent_item                   TEXT,
    item_segment2                 TEXT,
    cci                           TEXT,
    cad                           TEXT,
    item_category                 TEXT,
    fg_category                   TEXT,
    purchasing_category           TEXT,
    mfg_category                  TEXT,
    product_family                TEXT,
    om_category                   TEXT,
    warehouse                     TEXT,
    item_description              TEXT,
    revision                      TEXT,
    subinventory                  TEXT,
    locator                       TEXT,
    lot                           TEXT,
    lpn                           TEXT,
    parent_lpn                    TEXT,
    serial                        TEXT,
    containerized_flag            TEXT,
    receiving_location            TEXT,
    primary_quantity              NUMERIC NOT NULL,
    primary_uom                   TEXT,
    primary_quantity_value        NUMERIC,
    unit_cost                     NUMERIC,
    snapshot_date                 DATE,
    subinventory_status           TEXT,
    material_location             TEXT,
    owning_party                  TEXT,
    cost_type                     TEXT,
    customer_country_code         TEXT,
    market_code                   TEXT,
    client_code                   TEXT,
    nip_imp                       TEXT,
    matnom                        TEXT,
    matref                        TEXT,
    snapshot_date_with_timezone   TIMESTAMPTZ,
    duns_code                     TEXT,
    mich_duns                     TEXT,
    intended_market               TEXT,
    item_product_line             TEXT,
    stock_type                    TEXT,
    receiving_location_type       TEXT,
    stock_status                  TEXT,
    business_line                 TEXT
);
CREATE INDEX ix_stocklevel_inbox_file    ON staging.stocklevel_inbox (file_name);
CREATE INDEX ix_stocklevel_inbox_item_wh ON staging.stocklevel_inbox (item_segment1, warehouse);
CREATE INDEX ix_stocklevel_inbox_pending ON staging.stocklevel_inbox (file_name) WHERE promoted_at IS NULL;

CREATE TABLE staging.stocklevel_batch (
    batch_id      BIGSERIAL PRIMARY KEY,
    file_name     TEXT NOT NULL UNIQUE,
    started_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at  TIMESTAMPTZ,
    status        TEXT NOT NULL DEFAULT 'pending'
                  CHECK (status IN ('pending','loaded','partial','failed')),
    rows_accepted INTEGER NOT NULL DEFAULT 0,
    rows_rejected INTEGER NOT NULL DEFAULT 0
);

-- Other teams' contract surface: schemas only — they populate, our drain
-- functions (added in a later migration) promote into processed.
CREATE TABLE staging.txn_inbox (
    inbox_id        BIGSERIAL PRIMARY KEY,
    tenant_code     TEXT NOT NULL,
    external_txn_id TEXT NOT NULL,
    payload         JSONB NOT NULL,
    status          TEXT NOT NULL DEFAULT 'pending'
                    CHECK (status IN ('pending','processed','rejected')),
    received_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    processed_at    TIMESTAMPTZ,
    reject_reason   TEXT,
    UNIQUE (tenant_code, external_txn_id)
);
CREATE INDEX idx_txn_inbox_status ON staging.txn_inbox (status, received_at);

CREATE TABLE staging.order_inbox (
    inbox_id       BIGSERIAL PRIMARY KEY,
    tenant_code    TEXT NOT NULL,
    sfdc_order_id  TEXT NOT NULL,
    payload        JSONB NOT NULL,
    status         TEXT NOT NULL DEFAULT 'pending'
                   CHECK (status IN ('pending','processed','rejected')),
    received_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    processed_at   TIMESTAMPTZ,
    reject_reason  TEXT,
    UNIQUE (tenant_code, sfdc_order_id)
);
CREATE INDEX idx_order_inbox_status ON staging.order_inbox (status, received_at);

-- ============================================================================
-- audit
-- ============================================================================
CREATE TABLE audit.audit_log (
    audit_id      BIGSERIAL,
    tenant_id     UUID,
    table_schema  TEXT NOT NULL,
    table_name    TEXT NOT NULL,
    op            CHAR(1) NOT NULL CHECK (op IN ('I','U','D')),
    pk            JSONB,
    before        JSONB,
    after         JSONB,
    changed_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    changed_by    TEXT
) PARTITION BY RANGE (changed_at);

CREATE TABLE audit.audit_log_default PARTITION OF audit.audit_log DEFAULT;
CREATE INDEX idx_audit_log_tenant_changed ON audit.audit_log (tenant_id, changed_at);
CREATE INDEX idx_audit_log_table          ON audit.audit_log (table_schema, table_name, changed_at);

-- ============================================================================
-- Trigger: opening_balance INSERT → backfill stock_balance
-- ============================================================================
CREATE OR REPLACE FUNCTION processed.f_stock_balance_opening_apply()
RETURNS trigger
LANGUAGE plpgsql
AS $f$
DECLARE
    v_lot_key BIGINT := COALESCE(NEW.lot_id, 0);
BEGIN
    INSERT INTO processed.stock_balance (
        tenant_id, tenant_code,
        product_id, product_code,
        warehouse_id, warehouse_code,
        lot_id, lot_code,
        on_hand_qty, uom_id, uom_code, last_updated_at
    )
    VALUES (
        NEW.tenant_id, NEW.tenant_code,
        NEW.product_id, NEW.product_code,
        NEW.warehouse_id, NEW.warehouse_code,
        v_lot_key, NEW.lot_code,
        NEW.qty, NEW.uom_id, NEW.uom_code, now()
    )
    ON CONFLICT (tenant_id, product_id, warehouse_id, lot_id) DO UPDATE
    SET on_hand_qty     = processed.stock_balance.on_hand_qty + EXCLUDED.on_hand_qty,
        uom_id          = EXCLUDED.uom_id,
        uom_code        = EXCLUDED.uom_code,
        last_updated_at = now();
    RETURN NEW;
END;
$f$;

CREATE TRIGGER trg_opening_balance_stock_apply
    AFTER INSERT ON processed.opening_balance
    FOR EACH ROW EXECUTE FUNCTION processed.f_stock_balance_opening_apply();

-- ============================================================================
-- Function: notify_outbox — small helper for emitting outbox rows
-- ============================================================================
CREATE OR REPLACE FUNCTION notify_outbox(
    p_tenant_id  UUID,
    p_severity   TEXT,
    p_source     TEXT,
    p_message    TEXT,
    p_payload    JSONB,
    p_dedup_key  TEXT
)
RETURNS BIGINT
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, public, pg_catalog
AS $f$
DECLARE
    v_id BIGINT;
BEGIN
    -- Dedup: if a recent identical key exists, just bump the repeat count.
    IF p_dedup_key IS NOT NULL THEN
        UPDATE processed.notification_outbox
           SET repeat_count = repeat_count + 1
         WHERE dedup_key = p_dedup_key AND status = 'pending'
        RETURNING outbox_id INTO v_id;
        IF v_id IS NOT NULL THEN RETURN v_id; END IF;
    END IF;

    INSERT INTO processed.notification_outbox
        (tenant_id, severity, source, message, payload, dedup_key)
    VALUES
        (p_tenant_id, p_severity, p_source, p_message, p_payload, p_dedup_key)
    RETURNING outbox_id INTO v_id;
    RETURN v_id;
END;
$f$;

-- ============================================================================
-- Function: load_stocklevel(file_name) — promotion (V15 logic)
-- ============================================================================
CREATE OR REPLACE FUNCTION load_stocklevel(p_file_name TEXT)
RETURNS TABLE (
    batch_id      BIGINT,
    rows_accepted INTEGER,
    rows_rejected INTEGER,
    status        TEXT
)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, staging, public, pg_catalog
AS $f$
DECLARE
    v_batch_id   BIGINT;
    v_status     TEXT;
    v_accepted   INTEGER := 0;
    v_rejected   INTEGER := 0;
    v_master_row RECORD;
    v_tenant_id  UUID;
BEGIN
    IF p_file_name IS NULL OR length(p_file_name) = 0 THEN
        RAISE EXCEPTION 'load_stocklevel: file_name is required';
    END IF;

    SELECT b.batch_id, b.status INTO v_batch_id, v_status
      FROM staging.stocklevel_batch b WHERE b.file_name = p_file_name FOR UPDATE;

    IF v_batch_id IS NOT NULL AND v_status = 'loaded' THEN
        SELECT b.batch_id, b.rows_accepted, b.rows_rejected, b.status
          INTO v_batch_id, v_accepted, v_rejected, v_status
          FROM staging.stocklevel_batch b WHERE b.batch_id = v_batch_id;
        RETURN QUERY SELECT v_batch_id, v_accepted, v_rejected, v_status;
        RETURN;
    END IF;

    IF v_batch_id IS NULL THEN
        INSERT INTO staging.stocklevel_batch (file_name) VALUES (p_file_name)
        RETURNING staging.stocklevel_batch.batch_id INTO v_batch_id;
    END IF;

    -- Auto-create masters; tenant must be provisioned externally
    FOR v_master_row IN
        SELECT DISTINCT
               s.tenant_code,
               s.item_segment1 AS product_code,
               s.warehouse     AS warehouse_code,
               s.primary_uom   AS uom_code
          FROM staging.stocklevel_inbox s
         WHERE s.file_name = p_file_name
           AND s.promoted_at IS NULL
           AND s.tenant_code IS NOT NULL
    LOOP
        SELECT t.tenant_id INTO v_tenant_id FROM processed.tenant t WHERE t.tenant_code = v_master_row.tenant_code;
        IF v_tenant_id IS NULL THEN CONTINUE; END IF;

        IF v_master_row.product_code IS NOT NULL THEN
            INSERT INTO processed.product (tenant_id, product_code, name)
            VALUES (v_tenant_id, v_master_row.product_code, v_master_row.product_code)
            ON CONFLICT (tenant_id, product_code) DO NOTHING;
        END IF;
        IF v_master_row.warehouse_code IS NOT NULL THEN
            INSERT INTO processed.warehouse (tenant_id, warehouse_code, name)
            VALUES (v_tenant_id, v_master_row.warehouse_code, v_master_row.warehouse_code)
            ON CONFLICT (tenant_id, warehouse_code) DO NOTHING;
        END IF;
        IF v_master_row.uom_code IS NOT NULL THEN
            INSERT INTO processed.uom (tenant_id, uom_code, name)
            VALUES (v_tenant_id, v_master_row.uom_code, v_master_row.uom_code)
            ON CONFLICT (tenant_id, uom_code) DO NOTHING;
        END IF;
    END LOOP;

    WITH src AS (
        SELECT
            s.inbox_id, s.tenant_code,
            s.item_segment1 AS product_code,
            s.warehouse     AS warehouse_code,
            s.primary_uom   AS uom_code,
            s.primary_quantity AS qty,
            s.snapshot_date,
            t.tenant_id, p.product_id, w.warehouse_id, u.uom_id,
            CASE
                WHEN t.tenant_id      IS NULL THEN 'unknown_tenant'
                WHEN p.product_id     IS NULL THEN 'unknown_product'
                WHEN w.warehouse_id   IS NULL THEN 'unknown_warehouse'
                WHEN u.uom_id         IS NULL THEN 'unknown_uom'
                WHEN s.primary_quantity IS NULL THEN 'missing_qty'
                WHEN s.snapshot_date  IS NULL THEN 'missing_snapshot_date'
                ELSE NULL
            END AS reject_reason
          FROM staging.stocklevel_inbox s
          LEFT JOIN processed.tenant    t ON t.tenant_code    = s.tenant_code
          LEFT JOIN processed.product   p ON p.product_code   = s.item_segment1 AND p.tenant_id    = t.tenant_id
          LEFT JOIN processed.warehouse w ON w.warehouse_code = s.warehouse     AND w.tenant_id    = t.tenant_id
          LEFT JOIN processed.uom       u ON u.uom_code       = s.primary_uom   AND u.tenant_id    = t.tenant_id
         WHERE s.file_name = p_file_name AND s.promoted_at IS NULL
    ),
    inserted AS (
        INSERT INTO processed.opening_balance (
            tenant_id, tenant_code, product_id, product_code,
            warehouse_id, warehouse_code, qty, uom_id, uom_code,
            as_of_date, batch_id
        )
        SELECT tenant_id, tenant_code, product_id, product_code,
               warehouse_id, warehouse_code, qty, uom_id, uom_code,
               snapshot_date, v_batch_id
          FROM src WHERE reject_reason IS NULL
        ON CONFLICT DO NOTHING
        RETURNING opening_balance_id
    ),
    counts AS (
        SELECT
            (SELECT COUNT(*)::int FROM inserted) AS ok_count,
            (SELECT COUNT(*)::int FROM src WHERE reject_reason IS NOT NULL) AS bad_count
    )
    SELECT ok_count, bad_count INTO v_accepted, v_rejected FROM counts;

    UPDATE staging.stocklevel_inbox SET promoted_at = now()
     WHERE file_name = p_file_name AND promoted_at IS NULL;

    v_status := CASE
                    WHEN v_rejected = 0 THEN 'loaded'
                    WHEN v_accepted = 0 THEN 'failed'
                    ELSE 'partial'
                END;

    UPDATE staging.stocklevel_batch
       SET status = v_status, completed_at = now(),
           rows_accepted = v_accepted, rows_rejected = v_rejected
     WHERE staging.stocklevel_batch.batch_id = v_batch_id;

    RETURN QUERY SELECT v_batch_id, v_accepted, v_rejected, v_status;
END;
$f$;

COMMIT;

-- ============================================================================
-- Convenience: set a default search_path on the database so ad-hoc psql /
-- pgAdmin sessions don't have to qualify every table. ENTIRELY OPTIONAL — the
-- Java MicroService sets its own search_path per connection. If the user
-- running this script isn't the database owner (common on managed services
-- like Azure Flexible Server), this block silently skips itself.
-- ============================================================================
DO $do$
BEGIN
    EXECUTE format(
        'ALTER DATABASE %I SET search_path = processed, staging, audit, public',
        current_database()
    );
EXCEPTION
    WHEN insufficient_privilege OR feature_not_supported THEN
        RAISE NOTICE
            'Skipping ALTER DATABASE search_path — current role cannot ALTER this database. '
            'In your sessions, run:  SET search_path = processed, staging, audit, public;';
END
$do$;

-- ============================================================================
-- VERIFY (run after install)
-- ============================================================================
-- \dn                                     -- four schemas: processed, staging, audit, public
-- \dt processed.*                         -- 10 tables
-- \dt staging.*                           -- 4 tables (stocklevel_inbox, stocklevel_batch, txn_inbox, order_inbox)
-- \dt audit.*                             -- 1 partitioned table
-- \df load_stocklevel                     -- 1 function
-- SELECT * FROM load_stocklevel('test');  -- expect: zero-row batch with status='loaded'
