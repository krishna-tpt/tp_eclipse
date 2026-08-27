-- ============================================================================
-- TenthPlanet Inventory Ledger — Customer Fresh Install
-- Tag: 08-06-v3-customer (2026-06-08)
-- ============================================================================
-- Single-script install for a clean PostgreSQL 16 database.
--
-- Differences from v2 (05-06-v2-customer):
--   * processed.opening_balance         + subinventory, + stock_status
--   * processed.stock_balance           + subinventory, + stock_status (both in PK)
--   * processed.inv_transaction         + subinventory, + stock_status
--   * load_stocklevel                   carries subinventory + stock_status from staging
--   * f_stock_balance_opening_apply     keyed on (..., subinventory, stock_status, lot)
--   * f_stock_balance_txn_apply         same — with stock_status defaulting to 'LIBERATED'
--   * f_stock_balance_reservation_apply writes to POOL row (subinventory='POOL',
--                                       stock_status='LIBERATED') — warehouse-level pool
--   * fetch_inventory(tenant, product?, warehouse?, subinventory?, stock_status?)
--                                       optional filters; default stock_status='LIBERATED'
--   * recalculate_stock_balance         rebuilds on new key shape
--
-- The "POOL" subinventory is a sentinel: one row per (tenant, product, warehouse)
-- that holds the warehouse-level reservation total. It has on_hand_qty=0 and
-- the reservation in reserved_qty. Real subinventory rows hold on_hand_qty
-- with reserved_qty=0. fetch_inventory aggregates both shapes correctly.
-- ============================================================================

BEGIN;

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
    subinventory       TEXT NOT NULL DEFAULT '',                 -- NEW in v3
    stock_status       TEXT NOT NULL DEFAULT 'LIBERATED',        -- NEW in v3
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
    ON processed.opening_balance
       (tenant_id, product_id, warehouse_id, subinventory, stock_status,
        COALESCE(lot_id, 0), as_of_date);
CREATE INDEX idx_opening_balance_tenant_product
    ON processed.opening_balance (tenant_id, product_id, warehouse_id);

CREATE TABLE processed.stock_balance (
    tenant_id        UUID NOT NULL REFERENCES processed.tenant(tenant_id),
    tenant_code      TEXT NOT NULL,
    product_id       BIGINT NOT NULL REFERENCES processed.product(product_id),
    product_code     TEXT NOT NULL,
    warehouse_id     BIGINT NOT NULL REFERENCES processed.warehouse(warehouse_id),
    warehouse_code   TEXT NOT NULL,
    subinventory     TEXT NOT NULL DEFAULT '',                 -- NEW in v3 (in PK)
                                                                -- value 'POOL' = warehouse-level
                                                                -- reservation row (on_hand=0)
    stock_status     TEXT NOT NULL DEFAULT 'LIBERATED',        -- NEW in v3 (in PK)
    lot_id           BIGINT NOT NULL DEFAULT 0,
    lot_code         TEXT,
    on_hand_qty      NUMERIC(18, 4) NOT NULL DEFAULT 0,
    reserved_qty     NUMERIC(18, 4) NOT NULL DEFAULT 0,
    uom_id           BIGINT NOT NULL REFERENCES processed.uom(uom_id),
    uom_code         TEXT NOT NULL,
    last_updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (tenant_id, product_id, warehouse_id, subinventory, stock_status, lot_id)
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
    subinventory        TEXT,                                  -- NEW in v3 (nullable; NULL=default)
    stock_status        TEXT NOT NULL DEFAULT 'LIBERATED',     -- NEW in v3
    lot_id              BIGINT REFERENCES processed.lot(lot_id),
    lot_code            TEXT,
    signed_qty          NUMERIC(18, 4) NOT NULL,
    uom_id              BIGINT NOT NULL REFERENCES processed.uom(uom_id),
    uom_code            TEXT NOT NULL,
    txn_type            TEXT NOT NULL,
    posted_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    transfer_pair_id    UUID,
    sfdc_order_id       TEXT,
    payload             JSONB,
    UNIQUE (tenant_id, external_txn_id)
);
CREATE INDEX idx_inv_transaction_tenant_pw_posted
    ON processed.inv_transaction (tenant_id, product_id, warehouse_id, posted_at);
CREATE INDEX idx_inv_transaction_order_link
    ON processed.inv_transaction (sfdc_order_id) WHERE sfdc_order_id IS NOT NULL;

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

-- sfdc_order_line: no subinventory column — orders pool at warehouse level (Option B)
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
-- staging — landing zones
-- ============================================================================
CREATE TABLE staging.stocklevel_inbox (
    inbox_id            BIGSERIAL PRIMARY KEY,
    file_name           TEXT NOT NULL,
    line_number         BIGINT NOT NULL,
    ingested_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    promoted_at         TIMESTAMPTZ,
    tenant_code         TEXT,
    file_batch_id       TEXT,
    source_marker       TEXT,
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

-- ============================================================================
-- TRIGGER FUNCTIONS
-- ============================================================================

-- 1. opening_balance INSERT → stock_balance.on_hand_qty (per subinventory + stock_status)
CREATE OR REPLACE FUNCTION processed.f_stock_balance_opening_apply()
RETURNS trigger LANGUAGE plpgsql AS $f$
DECLARE
    v_lot_key           BIGINT := COALESCE(NEW.lot_id, 0);
    v_existing_max_date DATE;
BEGIN
    SELECT MAX(ob.as_of_date) INTO v_existing_max_date
      FROM processed.opening_balance ob
     WHERE ob.tenant_id     = NEW.tenant_id
       AND ob.product_id    = NEW.product_id
       AND ob.warehouse_id  = NEW.warehouse_id
       AND ob.subinventory  = NEW.subinventory
       AND ob.stock_status  = NEW.stock_status
       AND COALESCE(ob.lot_id, 0) = v_lot_key
       AND ob.opening_balance_id <> NEW.opening_balance_id;

    IF v_existing_max_date IS NULL OR NEW.as_of_date >= v_existing_max_date THEN
        INSERT INTO processed.stock_balance (
            tenant_id, tenant_code, product_id, product_code,
            warehouse_id, warehouse_code, subinventory, stock_status,
            lot_id, lot_code, on_hand_qty, reserved_qty,
            uom_id, uom_code, last_updated_at
        )
        VALUES (
            NEW.tenant_id, NEW.tenant_code, NEW.product_id, NEW.product_code,
            NEW.warehouse_id, NEW.warehouse_code, NEW.subinventory, NEW.stock_status,
            v_lot_key, NEW.lot_code, NEW.qty, 0,
            NEW.uom_id, NEW.uom_code, now()
        )
        ON CONFLICT (tenant_id, product_id, warehouse_id, subinventory, stock_status, lot_id)
        DO UPDATE SET
            on_hand_qty     = EXCLUDED.on_hand_qty,
            uom_id          = EXCLUDED.uom_id,
            uom_code        = EXCLUDED.uom_code,
            last_updated_at = now();
    ELSE
        INSERT INTO processed.notification_outbox
            (tenant_id, severity, source, message, payload, dedup_key)
        VALUES (
            NEW.tenant_id, 'WARN', 'opening_balance_trigger',
            format('back-dated opening_balance (as_of=%s, latest=%s) — run recalculate_stock_balance to apply',
                   NEW.as_of_date, v_existing_max_date),
            jsonb_build_object('opening_balance_id', NEW.opening_balance_id,
                               'tenant_code', NEW.tenant_code,
                               'product_code', NEW.product_code,
                               'warehouse_code', NEW.warehouse_code,
                               'subinventory', NEW.subinventory),
            'back_dated_ob:' || NEW.tenant_id || ':' || NEW.product_id || ':' || NEW.warehouse_id
            || ':' || NEW.subinventory
        )
        ON CONFLICT DO NOTHING;
    END IF;

    RETURN NEW;
END;
$f$;

CREATE TRIGGER trg_opening_balance_stock_apply
    AFTER INSERT ON processed.opening_balance
    FOR EACH ROW EXECUTE FUNCTION processed.f_stock_balance_opening_apply();

-- 2. inv_transaction INSERT → stock_balance.on_hand_qty + close matching SFDC order
CREATE OR REPLACE FUNCTION processed.f_stock_balance_txn_apply()
RETURNS trigger LANGUAGE plpgsql AS $f$
DECLARE
    v_lot_key      BIGINT := COALESCE(NEW.lot_id, 0);
    v_subinv       TEXT   := COALESCE(NEW.subinventory, '');
    v_stock_status TEXT   := COALESCE(NEW.stock_status, 'LIBERATED');
BEGIN
    INSERT INTO processed.stock_balance (
        tenant_id, tenant_code, product_id, product_code,
        warehouse_id, warehouse_code, subinventory, stock_status,
        lot_id, lot_code, on_hand_qty, reserved_qty,
        uom_id, uom_code, last_updated_at
    )
    VALUES (
        NEW.tenant_id, NEW.tenant_code, NEW.product_id, NEW.product_code,
        NEW.warehouse_id, NEW.warehouse_code, v_subinv, v_stock_status,
        v_lot_key, NEW.lot_code, NEW.signed_qty, 0,
        NEW.uom_id, NEW.uom_code, now()
    )
    ON CONFLICT (tenant_id, product_id, warehouse_id, subinventory, stock_status, lot_id)
    DO UPDATE SET
        on_hand_qty     = processed.stock_balance.on_hand_qty + EXCLUDED.on_hand_qty,
        last_updated_at = now();

    -- Cascade: shipment txn fulfils an SFDC order → close its lines, release reservation
    IF NEW.sfdc_order_id IS NOT NULL THEN
        UPDATE processed.sfdc_order_line
           SET line_state    = 'closed',
               fop_synced_at = now(),
               updated_at    = now()
         WHERE sfdc_order_id = NEW.sfdc_order_id
           AND line_state IN ('open','synced');
    END IF;

    RETURN NEW;
END;
$f$;

CREATE TRIGGER trg_inv_transaction_stock_apply
    AFTER INSERT ON processed.inv_transaction
    FOR EACH ROW EXECUTE FUNCTION processed.f_stock_balance_txn_apply();

-- 3. sfdc_order_line INSERT/UPDATE/DELETE → POOL row's reserved_qty
--    (warehouse-level pool, Option B from design discussion)
CREATE OR REPLACE FUNCTION processed.f_stock_balance_reservation_apply()
RETURNS trigger LANGUAGE plpgsql AS $f$
DECLARE
    v_active_states TEXT[] := ARRAY['open','synced'];
    v_was_active    BOOLEAN := false;
    v_is_active     BOOLEAN := false;
    v_delta         NUMERIC := 0;
    v_key_tenant    UUID;
    v_key_product   BIGINT;
    v_key_warehouse BIGINT;
BEGIN
    IF TG_OP = 'DELETE' THEN
        v_key_tenant    := OLD.tenant_id;
        v_key_product   := OLD.product_id;
        v_key_warehouse := OLD.warehouse_id;
        v_was_active    := OLD.line_state = ANY(v_active_states);
        v_delta         := CASE WHEN v_was_active THEN -OLD.qty ELSE 0 END;
    ELSIF TG_OP = 'INSERT' THEN
        v_key_tenant    := NEW.tenant_id;
        v_key_product   := NEW.product_id;
        v_key_warehouse := NEW.warehouse_id;
        v_is_active     := NEW.line_state = ANY(v_active_states);
        v_delta         := CASE WHEN v_is_active THEN NEW.qty ELSE 0 END;
    ELSE
        v_key_tenant    := NEW.tenant_id;
        v_key_product   := NEW.product_id;
        v_key_warehouse := NEW.warehouse_id;
        v_was_active    := OLD.line_state = ANY(v_active_states);
        v_is_active     := NEW.line_state = ANY(v_active_states);
        v_delta := CASE
                       WHEN v_was_active AND v_is_active     THEN NEW.qty - OLD.qty
                       WHEN v_was_active AND NOT v_is_active THEN -OLD.qty
                       WHEN NOT v_was_active AND v_is_active THEN NEW.qty
                       ELSE 0
                   END;
    END IF;

    IF v_delta <> 0 THEN
        -- Warehouse-level POOL row holds the reservation total
        UPDATE processed.stock_balance
           SET reserved_qty    = reserved_qty + v_delta,
               last_updated_at = now()
         WHERE tenant_id    = v_key_tenant
           AND product_id   = v_key_product
           AND warehouse_id = v_key_warehouse
           AND subinventory = 'POOL'
           AND stock_status = 'LIBERATED'
           AND lot_id       = 0;

        IF NOT FOUND AND TG_OP <> 'DELETE' THEN
            INSERT INTO processed.stock_balance (
                tenant_id, tenant_code, product_id, product_code,
                warehouse_id, warehouse_code, subinventory, stock_status,
                lot_id, lot_code, on_hand_qty, reserved_qty,
                uom_id, uom_code, last_updated_at
            )
            SELECT NEW.tenant_id, NEW.tenant_code, NEW.product_id, NEW.product_code,
                   NEW.warehouse_id, NEW.warehouse_code, 'POOL', 'LIBERATED',
                   0, NULL, 0, v_delta,
                   NEW.uom_id, NEW.uom_code, now()
            ON CONFLICT (tenant_id, product_id, warehouse_id, subinventory, stock_status, lot_id)
            DO UPDATE SET
                reserved_qty    = processed.stock_balance.reserved_qty + EXCLUDED.reserved_qty,
                last_updated_at = now();
        END IF;
    END IF;

    RETURN COALESCE(NEW, OLD);
END;
$f$;

CREATE TRIGGER trg_sfdc_order_line_reservation
    AFTER INSERT OR UPDATE OR DELETE ON processed.sfdc_order_line
    FOR EACH ROW EXECUTE FUNCTION processed.f_stock_balance_reservation_apply();

-- ============================================================================
-- HELPER: notify_outbox
-- ============================================================================
CREATE OR REPLACE FUNCTION notify_outbox(
    p_tenant_id UUID, p_severity TEXT, p_source TEXT,
    p_message TEXT, p_payload JSONB, p_dedup_key TEXT
)
RETURNS BIGINT
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, public, pg_catalog
AS $f$
DECLARE v_id BIGINT;
BEGIN
    IF p_dedup_key IS NOT NULL THEN
        UPDATE processed.notification_outbox
           SET repeat_count = repeat_count + 1
         WHERE dedup_key = p_dedup_key AND status = 'pending'
        RETURNING outbox_id INTO v_id;
        IF v_id IS NOT NULL THEN RETURN v_id; END IF;
    END IF;
    INSERT INTO processed.notification_outbox
        (tenant_id, severity, source, message, payload, dedup_key)
    VALUES (p_tenant_id, p_severity, p_source, p_message, p_payload, p_dedup_key)
    RETURNING outbox_id INTO v_id;
    RETURN v_id;
END;
$f$;

-- ============================================================================
-- DRAIN FUNCTION: load_stocklevel — file-based opening balance promotion
-- Now carries subinventory + stock_status through to opening_balance.
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

    FOR v_master_row IN
        SELECT DISTINCT s.tenant_code,
               s.item_segment1 AS product_code,
               s.warehouse     AS warehouse_code,
               s.primary_uom   AS uom_code
          FROM staging.stocklevel_inbox s
         WHERE s.file_name = p_file_name AND s.promoted_at IS NULL AND s.tenant_code IS NOT NULL
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
        SELECT s.inbox_id, s.tenant_code,
               s.item_segment1 AS product_code,
               s.warehouse     AS warehouse_code,
               COALESCE(NULLIF(trim(s.subinventory), ''), '') AS subinventory,         -- NEW in v3
               COALESCE(NULLIF(trim(s.stock_status), ''), 'LIBERATED') AS stock_status, -- NEW in v3
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
          LEFT JOIN processed.product   p ON p.product_code   = s.item_segment1 AND p.tenant_id = t.tenant_id
          LEFT JOIN processed.warehouse w ON w.warehouse_code = s.warehouse     AND w.tenant_id = t.tenant_id
          LEFT JOIN processed.uom       u ON u.uom_code       = s.primary_uom   AND u.tenant_id = t.tenant_id
         WHERE s.file_name = p_file_name AND s.promoted_at IS NULL
    ),
    inserted AS (
        INSERT INTO processed.opening_balance (
            tenant_id, tenant_code, product_id, product_code,
            warehouse_id, warehouse_code,
            subinventory, stock_status,
            qty, uom_id, uom_code,
            as_of_date, batch_id
        )
        SELECT tenant_id, tenant_code, product_id, product_code,
               warehouse_id, warehouse_code,
               subinventory, stock_status,
               qty, uom_id, uom_code,
               snapshot_date, v_batch_id
          FROM src WHERE reject_reason IS NULL
        ON CONFLICT DO NOTHING
        RETURNING opening_balance_id
    ),
    counts AS (
        SELECT (SELECT COUNT(*)::int FROM inserted) AS ok_count,
               (SELECT COUNT(*)::int FROM src WHERE reject_reason IS NOT NULL) AS bad_count
    )
    SELECT ok_count, bad_count INTO v_accepted, v_rejected FROM counts;

    UPDATE staging.stocklevel_inbox SET promoted_at = now()
     WHERE file_name = p_file_name AND promoted_at IS NULL;

    v_status := CASE WHEN v_rejected = 0 THEN 'loaded'
                     WHEN v_accepted = 0 THEN 'failed'
                     ELSE 'partial' END;
    UPDATE staging.stocklevel_batch
       SET status = v_status, completed_at = now(),
           rows_accepted = v_accepted, rows_rejected = v_rejected
     WHERE staging.stocklevel_batch.batch_id = v_batch_id;

    RETURN QUERY SELECT v_batch_id, v_accepted, v_rejected, v_status;
END;
$f$;

-- ============================================================================
-- DRAIN: load_transactions — drains staging.txn_inbox → processed.inv_transaction
-- Now picks up subinventory + stock_status from payload.
-- ============================================================================
CREATE OR REPLACE FUNCTION load_transactions(p_limit INTEGER DEFAULT 500)
RETURNS TABLE (rows_processed INTEGER, rows_failed INTEGER)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, staging, public, pg_catalog
AS $f$
DECLARE
    v_row    RECORD;
    v_tid    UUID;
    v_pid    BIGINT;
    v_wid    BIGINT;
    v_uid    BIGINT;
    v_ok     INTEGER := 0;
    v_bad    INTEGER := 0;
    v_payload JSONB;
BEGIN
    FOR v_row IN
        SELECT inbox_id, tenant_code, external_txn_id, payload
          FROM staging.txn_inbox
         WHERE status = 'pending'
         ORDER BY received_at, inbox_id
         LIMIT p_limit
         FOR UPDATE SKIP LOCKED
    LOOP
        v_payload := v_row.payload;
        BEGIN
            SELECT t.tenant_id INTO v_tid FROM processed.tenant t WHERE t.tenant_code = v_row.tenant_code;
            IF v_tid IS NULL THEN
                UPDATE staging.txn_inbox SET status='rejected', processed_at=now(),
                       reject_reason='unknown_tenant'
                 WHERE inbox_id = v_row.inbox_id;
                v_bad := v_bad + 1;
                CONTINUE;
            END IF;

            SELECT p.product_id   INTO v_pid FROM processed.product   p WHERE p.tenant_id = v_tid AND p.product_code   = v_payload->>'product_code';
            SELECT w.warehouse_id INTO v_wid FROM processed.warehouse w WHERE w.tenant_id = v_tid AND w.warehouse_code = v_payload->>'warehouse_code';
            SELECT u.uom_id       INTO v_uid FROM processed.uom       u WHERE u.tenant_id = v_tid AND u.uom_code       = v_payload->>'uom_code';

            IF v_pid IS NULL OR v_wid IS NULL OR v_uid IS NULL THEN
                UPDATE staging.txn_inbox SET status='rejected', processed_at=now(),
                       reject_reason=CASE WHEN v_pid IS NULL THEN 'unknown_product'
                                          WHEN v_wid IS NULL THEN 'unknown_warehouse'
                                          ELSE 'unknown_uom' END
                 WHERE inbox_id = v_row.inbox_id;
                v_bad := v_bad + 1;
                CONTINUE;
            END IF;

            INSERT INTO processed.inv_transaction (
                tenant_id, tenant_code, external_txn_id,
                product_id, product_code, warehouse_id, warehouse_code,
                subinventory, stock_status,
                signed_qty, uom_id, uom_code, txn_type,
                posted_at, sfdc_order_id, payload
            )
            VALUES (
                v_tid, v_row.tenant_code, v_row.external_txn_id,
                v_pid, v_payload->>'product_code', v_wid, v_payload->>'warehouse_code',
                v_payload->>'subinventory',                                      -- NEW: nullable
                COALESCE(v_payload->>'stock_status', 'LIBERATED'),               -- NEW: default LIBERATED
                (v_payload->>'signed_qty')::numeric, v_uid, v_payload->>'uom_code',
                COALESCE(v_payload->>'txn_type', 'unknown'),
                COALESCE((v_payload->>'posted_at')::timestamptz, now()),
                v_payload->>'sfdc_order_id',
                v_payload
            )
            ON CONFLICT (tenant_id, external_txn_id) DO NOTHING;

            UPDATE staging.txn_inbox SET status='processed', processed_at=now() WHERE inbox_id = v_row.inbox_id;
            v_ok := v_ok + 1;
        EXCEPTION WHEN OTHERS THEN
            UPDATE staging.txn_inbox SET status='rejected', processed_at=now(),
                   reject_reason=SQLERRM
             WHERE inbox_id = v_row.inbox_id;
            v_bad := v_bad + 1;
        END;
    END LOOP;

    RETURN QUERY SELECT v_ok, v_bad;
END;
$f$;

-- ============================================================================
-- DRAIN: load_orders — drains staging.order_inbox → processed.sfdc_order(+_line)
-- ============================================================================
CREATE OR REPLACE FUNCTION load_orders(p_limit INTEGER DEFAULT 500)
RETURNS TABLE (rows_processed INTEGER, rows_failed INTEGER)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, staging, public, pg_catalog
AS $f$
DECLARE
    v_row      RECORD;
    v_tid      UUID;
    v_line     JSONB;
    v_line_no  INT;
    v_pid      BIGINT;
    v_wid      BIGINT;
    v_uid      BIGINT;
    v_ok       INTEGER := 0;
    v_bad      INTEGER := 0;
    v_payload  JSONB;
BEGIN
    FOR v_row IN
        SELECT inbox_id, tenant_code, sfdc_order_id, payload
          FROM staging.order_inbox
         WHERE status = 'pending'
         ORDER BY received_at, inbox_id
         LIMIT p_limit
         FOR UPDATE SKIP LOCKED
    LOOP
        v_payload := v_row.payload;
        BEGIN
            SELECT t.tenant_id INTO v_tid FROM processed.tenant t WHERE t.tenant_code = v_row.tenant_code;
            IF v_tid IS NULL THEN
                UPDATE staging.order_inbox SET status='rejected', processed_at=now(),
                       reject_reason='unknown_tenant'
                 WHERE inbox_id = v_row.inbox_id;
                v_bad := v_bad + 1;
                CONTINUE;
            END IF;

            INSERT INTO processed.sfdc_order (sfdc_order_id, tenant_id, tenant_code, customer_id, order_state, payload)
            VALUES (
                v_row.sfdc_order_id, v_tid, v_row.tenant_code,
                COALESCE(v_payload->>'customer_id', 'unknown'),
                COALESCE(v_payload->>'order_state', 'open'),
                v_payload
            )
            ON CONFLICT (sfdc_order_id) DO UPDATE
            SET order_state = EXCLUDED.order_state,
                payload     = EXCLUDED.payload,
                updated_at  = now();

            v_line_no := 0;
            FOR v_line IN SELECT * FROM jsonb_array_elements(COALESCE(v_payload->'lines', '[]'::jsonb))
            LOOP
                v_line_no := v_line_no + 1;
                SELECT p.product_id   INTO v_pid FROM processed.product   p WHERE p.tenant_id = v_tid AND p.product_code   = v_line->>'product_code';
                SELECT w.warehouse_id INTO v_wid FROM processed.warehouse w WHERE w.tenant_id = v_tid AND w.warehouse_code = v_line->>'warehouse_code';
                SELECT u.uom_id       INTO v_uid FROM processed.uom       u WHERE u.tenant_id = v_tid AND u.uom_code       = v_line->>'uom_code';

                IF v_pid IS NULL OR v_wid IS NULL OR v_uid IS NULL THEN
                    RAISE EXCEPTION 'unknown master in line %', v_line_no;
                END IF;

                INSERT INTO processed.sfdc_order_line (
                    sfdc_order_id, line_no, tenant_id, tenant_code,
                    product_id, product_code, warehouse_id, warehouse_code,
                    qty, uom_id, uom_code, line_state, payload
                )
                VALUES (
                    v_row.sfdc_order_id, v_line_no, v_tid, v_row.tenant_code,
                    v_pid, v_line->>'product_code', v_wid, v_line->>'warehouse_code',
                    (v_line->>'qty')::numeric, v_uid, v_line->>'uom_code',
                    COALESCE(v_line->>'line_state', 'open'), v_line
                )
                ON CONFLICT (sfdc_order_id, line_no) DO UPDATE
                SET qty        = EXCLUDED.qty,
                    line_state = EXCLUDED.line_state,
                    payload    = EXCLUDED.payload,
                    updated_at = now();
            END LOOP;

            UPDATE staging.order_inbox SET status='processed', processed_at=now() WHERE inbox_id = v_row.inbox_id;
            v_ok := v_ok + 1;
        EXCEPTION WHEN OTHERS THEN
            UPDATE staging.order_inbox SET status='rejected', processed_at=now(),
                   reject_reason=SQLERRM
             WHERE inbox_id = v_row.inbox_id;
            v_bad := v_bad + 1;
        END;
    END LOOP;

    RETURN QUERY SELECT v_ok, v_bad;
END;
$f$;

-- ============================================================================
-- READ FUNCTION: fetch_inventory — flexible filtering
-- ============================================================================
-- Filters:
--   p_tenant_code     required
--   p_product_code    NULL = all products
--   p_warehouse_code  NULL = all warehouses
--   p_subinventory    NULL = aggregate across subinventories
--                     specified = just that one
--   p_stock_status    default 'LIBERATED' (sellable stock only)
--                     pass 'ALL' to disable stock_status filter
--
-- Reservations: always sourced from the POOL row (warehouse-level), regardless
-- of the subinventory filter. Implements pooled reservations (Option B).
-- ============================================================================
CREATE OR REPLACE FUNCTION fetch_inventory(
    p_tenant_code    TEXT,
    p_product_code   TEXT DEFAULT NULL,
    p_warehouse_code TEXT DEFAULT NULL,
    p_subinventory   TEXT DEFAULT NULL,
    p_stock_status   TEXT DEFAULT 'LIBERATED'
)
RETURNS TABLE (
    tenant_code      TEXT,
    product_code     TEXT,
    warehouse_code   TEXT,
    subinventory     TEXT,
    stock_status     TEXT,
    on_hand_qty      NUMERIC,
    reserved_qty     NUMERIC,
    atp              NUMERIC,
    uom_code         TEXT,
    last_updated_at  TIMESTAMPTZ
)
LANGUAGE sql
SECURITY DEFINER
SET search_path = processed, public, pg_catalog
AS $f$
    WITH on_hand AS (
        SELECT sb.tenant_code, sb.product_code, sb.warehouse_code,
               CASE WHEN p_subinventory IS NULL THEN 'ALL' ELSE sb.subinventory END AS subinventory_grp,
               sb.stock_status,
               SUM(sb.on_hand_qty) AS sum_on_hand,
               MAX(sb.uom_code)    AS uom_code,
               MAX(sb.last_updated_at) AS last_updated_at
          FROM processed.stock_balance sb
         WHERE sb.tenant_code = p_tenant_code
           AND (p_product_code   IS NULL OR sb.product_code   = p_product_code)
           AND (p_warehouse_code IS NULL OR sb.warehouse_code = p_warehouse_code)
           AND sb.subinventory <> 'POOL'                          -- exclude pool from on_hand
           AND (p_subinventory IS NULL OR sb.subinventory = p_subinventory)
           AND (p_stock_status = 'ALL' OR sb.stock_status = p_stock_status)
         GROUP BY sb.tenant_code, sb.product_code, sb.warehouse_code,
                  CASE WHEN p_subinventory IS NULL THEN 'ALL' ELSE sb.subinventory END,
                  sb.stock_status
    ),
    reserved AS (
        SELECT sb.tenant_code, sb.product_code, sb.warehouse_code,
               SUM(sb.reserved_qty)       AS sum_reserved,
               MAX(sb.last_updated_at)    AS last_updated_at
          FROM processed.stock_balance sb
         WHERE sb.tenant_code = p_tenant_code
           AND (p_product_code   IS NULL OR sb.product_code   = p_product_code)
           AND (p_warehouse_code IS NULL OR sb.warehouse_code = p_warehouse_code)
           AND sb.subinventory = 'POOL'                            -- only the pool row
         GROUP BY sb.tenant_code, sb.product_code, sb.warehouse_code
    )
    SELECT
        o.tenant_code, o.product_code, o.warehouse_code,
        o.subinventory_grp AS subinventory,
        o.stock_status,
        o.sum_on_hand                                   AS on_hand_qty,
        COALESCE(r.sum_reserved, 0)                     AS reserved_qty,
        o.sum_on_hand - COALESCE(r.sum_reserved, 0)     AS atp,
        o.uom_code,
        GREATEST(o.last_updated_at, COALESCE(r.last_updated_at, o.last_updated_at)) AS last_updated_at
      FROM on_hand o
      LEFT JOIN reserved r USING (tenant_code, product_code, warehouse_code);
$f$;

-- ============================================================================
-- MAINTENANCE: recalculate_stock_balance — rebuild on new key shape
-- ============================================================================
CREATE OR REPLACE FUNCTION recalculate_stock_balance(p_tenant_code TEXT)
RETURNS TABLE (rows_rebuilt INTEGER)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, public, pg_catalog
AS $f$
DECLARE
    v_tid UUID;
    v_n   INTEGER := 0;
BEGIN
    SELECT tenant_id INTO v_tid FROM processed.tenant WHERE tenant_code = p_tenant_code;
    IF v_tid IS NULL THEN
        RAISE EXCEPTION 'recalculate_stock_balance: unknown tenant %', p_tenant_code;
    END IF;

    DELETE FROM processed.stock_balance WHERE tenant_id = v_tid;

    WITH latest_ob AS (
        SELECT DISTINCT ON (tenant_id, product_id, warehouse_id, subinventory, stock_status, COALESCE(lot_id, 0))
               tenant_id, tenant_code, product_id, product_code,
               warehouse_id, warehouse_code, subinventory, stock_status,
               lot_id, lot_code, qty, uom_id, uom_code, as_of_date
          FROM processed.opening_balance
         WHERE tenant_id = v_tid
         ORDER BY tenant_id, product_id, warehouse_id, subinventory, stock_status,
                  COALESCE(lot_id, 0), as_of_date DESC
    ),
    txn_after AS (
        SELECT tx.tenant_id, tx.product_id, tx.warehouse_id,
               COALESCE(tx.subinventory, '') AS subinventory,
               COALESCE(tx.stock_status, 'LIBERATED') AS stock_status,
               COALESCE(tx.lot_id, 0) AS lot_key,
               SUM(tx.signed_qty) AS sum_signed
          FROM processed.inv_transaction tx
          JOIN latest_ob lo ON tx.tenant_id    = lo.tenant_id
                           AND tx.product_id   = lo.product_id
                           AND tx.warehouse_id = lo.warehouse_id
                           AND COALESCE(tx.subinventory, '') = lo.subinventory
                           AND COALESCE(tx.stock_status, 'LIBERATED') = lo.stock_status
                           AND COALESCE(tx.lot_id, 0) = COALESCE(lo.lot_id, 0)
                           AND tx.posted_at >= lo.as_of_date
         GROUP BY tx.tenant_id, tx.product_id, tx.warehouse_id,
                  COALESCE(tx.subinventory, ''),
                  COALESCE(tx.stock_status, 'LIBERATED'),
                  COALESCE(tx.lot_id, 0)
    ),
    reservations AS (
        SELECT sol.tenant_id, sol.product_id, sol.warehouse_id,
               SUM(sol.qty) AS sum_reserved
          FROM processed.sfdc_order_line sol
         WHERE sol.tenant_id = v_tid AND sol.line_state IN ('open','synced')
         GROUP BY sol.tenant_id, sol.product_id, sol.warehouse_id
    ),
    -- Real stock_balance rows (one per subinventory + stock_status + lot)
    rebuilt_real AS (
        INSERT INTO processed.stock_balance (
            tenant_id, tenant_code, product_id, product_code,
            warehouse_id, warehouse_code, subinventory, stock_status,
            lot_id, lot_code, on_hand_qty, reserved_qty,
            uom_id, uom_code, last_updated_at
        )
        SELECT lo.tenant_id, lo.tenant_code, lo.product_id, lo.product_code,
               lo.warehouse_id, lo.warehouse_code, lo.subinventory, lo.stock_status,
               COALESCE(lo.lot_id, 0), lo.lot_code,
               lo.qty + COALESCE(ta.sum_signed, 0), 0,
               lo.uom_id, lo.uom_code, now()
          FROM latest_ob lo
          LEFT JOIN txn_after ta ON ta.tenant_id   = lo.tenant_id
                                AND ta.product_id  = lo.product_id
                                AND ta.warehouse_id= lo.warehouse_id
                                AND ta.subinventory= lo.subinventory
                                AND ta.stock_status= lo.stock_status
                                AND ta.lot_key     = COALESCE(lo.lot_id, 0)
        RETURNING 1
    ),
    -- POOL rows: one per (tenant, product, warehouse) holding warehouse-level reservation
    rebuilt_pool AS (
        INSERT INTO processed.stock_balance (
            tenant_id, tenant_code, product_id, product_code,
            warehouse_id, warehouse_code, subinventory, stock_status,
            lot_id, lot_code, on_hand_qty, reserved_qty,
            uom_id, uom_code, last_updated_at
        )
        SELECT DISTINCT
               rv.tenant_id, t.tenant_code, rv.product_id, p.product_code,
               rv.warehouse_id, w.warehouse_code, 'POOL', 'LIBERATED',
               0, NULL, 0, rv.sum_reserved,
               (SELECT uom_id FROM processed.uom WHERE tenant_id = rv.tenant_id LIMIT 1),
               (SELECT uom_code FROM processed.uom WHERE tenant_id = rv.tenant_id LIMIT 1),
               now()
          FROM reservations rv
          JOIN processed.tenant    t ON t.tenant_id = rv.tenant_id
          JOIN processed.product   p ON p.product_id = rv.product_id
          JOIN processed.warehouse w ON w.warehouse_id = rv.warehouse_id
         WHERE rv.sum_reserved > 0
        RETURNING 1
    )
    SELECT (SELECT COUNT(*) FROM rebuilt_real) + (SELECT COUNT(*) FROM rebuilt_pool) INTO v_n;

    RETURN QUERY SELECT v_n;
END;
$f$;

COMMIT;

-- ============================================================================
-- Convenience: default search_path (skipped if no permission)
-- ============================================================================
DO $do$
BEGIN
    EXECUTE format('ALTER DATABASE %I SET search_path = processed, staging, audit, public',
                   current_database());
EXCEPTION
    WHEN insufficient_privilege OR feature_not_supported THEN
        RAISE NOTICE
            'Skipping ALTER DATABASE search_path — current role cannot ALTER this database. '
            'In your sessions, run:  SET search_path = processed, staging, audit, public;';
END
$do$;
