-- ============================================================================
-- Michelin Inventory Ledger — Customer Fresh Install
-- Tag: 11-06-v5-customer (2026-06-11)
-- ============================================================================
-- Single-script install for a clean PostgreSQL 16 database.
--
-- Differences from v4 (11-06-v4-customer):
--   * AUTO-PROMOTE TRIGGERS on staging.txn_inbox and staging.order_inbox.
--     Every INSERT into either inbox fires a per-row trigger that parses
--     the payload, looks up master IDs, and writes into processed.*.
--     FOP / SFDC integration teams now "fire and forget" — no scheduler needed.
--   * staging.f_promote_txn   — per-row promotion for inv_transaction
--   * staging.f_promote_order — per-row promotion for sfdc_order / _line
--   * Both trigger functions catch their own exceptions; staging rows ALWAYS
--     end up with terminal status ('processed' or 'rejected') and a non-NULL
--     processed_at timestamp. The original INSERT never fails because of
--     downstream issues — failures are recorded inline.
--   * processed.sfdc_order — UPSERT on (sfdc_order_id) preserves created_at.
--     updated_at = now() per edit; received_at tracks the latest inbox row
--     that drove the update.
--   * load_transactions and load_orders RETAINED as drain fallback for
--     reprocessing rejected rows (and for catch-up after a temporary trigger
--     outage). Normal path is now the trigger.
--   * Payload contract: lowercase snake_case keys (unchanged from v4). FOP /
--     SFDC integration teams are responsible for normalizing Oracle EBS /
--     Salesforce native shapes to this contract before INSERTing into the
--     inbox tables.
--
-- Differences from v3 (08-06-v3-customer):
--   * staging.txn_inbox     UNIQUE(tenant, external_txn_id) DROPPED — append-only
--   * staging.order_inbox   UNIQUE(tenant, sfdc_order_id)   DROPPED — append-only
--                           Same business key may appear many times.
--   * sfdc_order_line       + subinventory, + stock_status, + sfdc_line_id
--   * inv_transaction       + sfdc_line_id
--   * POOL sentinel REMOVED — reservations land on the real
--                           (tenant, product, warehouse, subinventory, stock_status, lot=0)
--                           row, not on a per-warehouse pool row.
--   * f_stock_balance_reservation_apply rewritten to release-OLD + reserve-NEW
--                                       handling per-row, including UPDATE that
--                                       changes subinv or stock_status.
--   * load_stocklevel       DELETE existing opening_balance rows for the file's
--                           batch, INSERT fresh. No ON CONFLICT.
--   * fetch_inventory       Per-row math — no POOL special case.
--   * recalculate_stock_balance Rebuilds per-row reservations from order_line.subinv.
--
-- Identifier columns: TEXT (was UUID through v4). gen_random_uuid()::text for
-- auto-gen of tenant_id. No UUID type anywhere in the schema.
-- ============================================================================

BEGIN;

CREATE SCHEMA IF NOT EXISTS processed;
CREATE SCHEMA IF NOT EXISTS staging;
CREATE SCHEMA IF NOT EXISTS audit;

-- ============================================================================
-- processed — master data (unchanged from v3)
-- ============================================================================
CREATE TABLE processed.tenant (
    tenant_id    TEXT PRIMARY KEY DEFAULT gen_random_uuid()::text,
    tenant_code  TEXT NOT NULL UNIQUE,
    name         TEXT NOT NULL,
    is_active    BOOLEAN NOT NULL DEFAULT true,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE processed.product (
    product_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id    TEXT NOT NULL REFERENCES processed.tenant(tenant_id),
    product_code TEXT NOT NULL,
    name         TEXT NOT NULL,
    is_active    BOOLEAN NOT NULL DEFAULT true,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, product_code)
);

CREATE TABLE processed.warehouse (
    warehouse_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id      TEXT NOT NULL REFERENCES processed.tenant(tenant_id),
    warehouse_code TEXT NOT NULL,
    name           TEXT NOT NULL,
    is_active      BOOLEAN NOT NULL DEFAULT true,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, warehouse_code)
);

CREATE TABLE processed.uom (
    uom_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id  TEXT NOT NULL REFERENCES processed.tenant(tenant_id),
    uom_code   TEXT NOT NULL,
    name       TEXT NOT NULL,
    is_active  BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, uom_code)
);

CREATE TABLE processed.lot (
    lot_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id  TEXT NOT NULL REFERENCES processed.tenant(tenant_id),
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
    tenant_id          TEXT NOT NULL REFERENCES processed.tenant(tenant_id),
    tenant_code        TEXT NOT NULL,
    product_id         BIGINT NOT NULL REFERENCES processed.product(product_id),
    product_code       TEXT NOT NULL,
    warehouse_id       BIGINT NOT NULL REFERENCES processed.warehouse(warehouse_id),
    warehouse_code     TEXT NOT NULL,
    subinventory       TEXT NOT NULL DEFAULT '',
    stock_status       TEXT NOT NULL DEFAULT 'LIBERATED',
    lot_id             BIGINT REFERENCES processed.lot(lot_id),
    lot_code           TEXT,
    qty                NUMERIC(18, 4) NOT NULL,
    uom_id             BIGINT NOT NULL REFERENCES processed.uom(uom_id),
    uom_code           TEXT NOT NULL,
    as_of_date         DATE NOT NULL,
    batch_id           BIGINT NOT NULL,
    source_file        TEXT,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_opening_balance_key
    ON processed.opening_balance
       (tenant_id, product_id, warehouse_id, subinventory, stock_status,
        COALESCE(lot_id, 0), as_of_date);
CREATE INDEX idx_opening_balance_tenant_product
    ON processed.opening_balance (tenant_id, product_id, warehouse_id);
CREATE INDEX idx_opening_balance_source_file
    ON processed.opening_balance (source_file) WHERE source_file IS NOT NULL;

-- Stock balance: PK shape unchanged from v3. The 'POOL' subinventory value is
-- no longer written by any path; left as a benign legal value in case any
-- historical record retains it.
CREATE TABLE processed.stock_balance (
    tenant_id        TEXT NOT NULL REFERENCES processed.tenant(tenant_id),
    tenant_code      TEXT NOT NULL,
    product_id       BIGINT NOT NULL REFERENCES processed.product(product_id),
    product_code     TEXT NOT NULL,
    warehouse_id     BIGINT NOT NULL REFERENCES processed.warehouse(warehouse_id),
    warehouse_code   TEXT NOT NULL,
    subinventory     TEXT NOT NULL DEFAULT '',
    stock_status     TEXT NOT NULL DEFAULT 'LIBERATED',
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
    tenant_id           TEXT NOT NULL REFERENCES processed.tenant(tenant_id),
    tenant_code         TEXT NOT NULL,
    external_txn_id     TEXT NOT NULL,
    product_id          BIGINT NOT NULL REFERENCES processed.product(product_id),
    product_code        TEXT NOT NULL,
    warehouse_id        BIGINT NOT NULL REFERENCES processed.warehouse(warehouse_id),
    warehouse_code      TEXT NOT NULL,
    subinventory        TEXT,
    stock_status        TEXT NOT NULL DEFAULT 'LIBERATED',
    lot_id              BIGINT REFERENCES processed.lot(lot_id),
    lot_code            TEXT,
    signed_qty          NUMERIC(18, 4) NOT NULL,
    uom_id              BIGINT NOT NULL REFERENCES processed.uom(uom_id),
    uom_code            TEXT NOT NULL,
    txn_type            TEXT NOT NULL,
    posted_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    transfer_pair_id    TEXT,
    sfdc_order_id       TEXT,
    sfdc_line_id        TEXT,                                  -- NEW in v4 (nullable, Q2 placeholder)
    payload             JSONB,
    UNIQUE (tenant_id, external_txn_id)
);
CREATE INDEX idx_inv_transaction_tenant_pw_posted
    ON processed.inv_transaction (tenant_id, product_id, warehouse_id, posted_at);
CREATE INDEX idx_inv_transaction_order_link
    ON processed.inv_transaction (sfdc_order_id) WHERE sfdc_order_id IS NOT NULL;
CREATE INDEX idx_inv_transaction_line_link
    ON processed.inv_transaction (sfdc_line_id) WHERE sfdc_line_id IS NOT NULL;

CREATE TABLE processed.sfdc_order (
    sfdc_order_id TEXT PRIMARY KEY,
    tenant_id     TEXT NOT NULL REFERENCES processed.tenant(tenant_id),
    tenant_code   TEXT NOT NULL,
    customer_id   TEXT NOT NULL,
    order_state   TEXT NOT NULL DEFAULT 'open'
                  CHECK (order_state IN ('open','synced','closed','cancelled')),
    payload       JSONB NOT NULL,
    received_at   TIMESTAMPTZ NOT NULL,                       -- NEW in v4 — from the inbox row
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- sfdc_order_line gains subinventory + stock_status + sfdc_line_id.
-- The PK stays (sfdc_order_id, line_no); since load_orders does
-- DELETE-then-INSERT for each order, line_no can be re-numbered cleanly.
CREATE TABLE processed.sfdc_order_line (
    sfdc_order_id  TEXT NOT NULL REFERENCES processed.sfdc_order(sfdc_order_id) ON DELETE CASCADE,
    line_no        INTEGER NOT NULL,
    sfdc_line_id   TEXT,                                       -- NEW in v4 (e.g. "1WL9Z000001KSHJWA4")
    tenant_id      TEXT NOT NULL,
    tenant_code    TEXT NOT NULL,
    product_id     BIGINT NOT NULL REFERENCES processed.product(product_id),
    product_code   TEXT NOT NULL,
    warehouse_id   BIGINT NOT NULL REFERENCES processed.warehouse(warehouse_id),
    warehouse_code TEXT NOT NULL,
    subinventory   TEXT NOT NULL DEFAULT '',                   -- NEW in v4
    stock_status   TEXT NOT NULL DEFAULT 'LIBERATED',          -- NEW in v4
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
CREATE INDEX idx_sfdc_order_line_sfdc_line_id
    ON processed.sfdc_order_line (sfdc_line_id) WHERE sfdc_line_id IS NOT NULL;
CREATE INDEX idx_sfdc_order_line_subinv_active
    ON processed.sfdc_order_line (tenant_id, product_id, warehouse_id, subinventory, stock_status)
    WHERE line_state IN ('open','synced');

-- ============================================================================
-- processed — operational (notification outbox unchanged from v3)
-- ============================================================================
CREATE TABLE processed.notification_outbox (
    outbox_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id     TEXT,
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
-- staging — landing zones (APPEND-ONLY in v4)
--   stocklevel_inbox: unchanged (already append-only — file-based)
--   txn_inbox:        UNIQUE(tenant, external_txn_id) DROPPED in v4
--   order_inbox:      UNIQUE(tenant, sfdc_order_id) DROPPED in v4
-- All three now accept duplicate business keys; drain functions pick latest.
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

-- v4: no UNIQUE on (tenant_code, external_txn_id). FOP appends; drain picks latest.
-- Status values extended with 'superseded' for older inbox rows of the same key.
CREATE TABLE staging.txn_inbox (
    inbox_id        BIGSERIAL PRIMARY KEY,
    tenant_code     TEXT NOT NULL,
    external_txn_id TEXT NOT NULL,
    payload         JSONB NOT NULL,
    status          TEXT NOT NULL DEFAULT 'pending'
                    CHECK (status IN ('pending','processed','rejected','superseded')),
    received_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    processed_at    TIMESTAMPTZ,
    reject_reason   TEXT
);
CREATE INDEX idx_txn_inbox_pending_key
    ON staging.txn_inbox (tenant_code, external_txn_id, received_at DESC)
    WHERE status = 'pending';
CREATE INDEX idx_txn_inbox_status_received
    ON staging.txn_inbox (status, received_at);

-- v4: no UNIQUE on (tenant_code, sfdc_order_id). FOP appends on every SFDC change.
CREATE TABLE staging.order_inbox (
    inbox_id       BIGSERIAL PRIMARY KEY,
    tenant_code    TEXT NOT NULL,
    sfdc_order_id  TEXT NOT NULL,
    payload        JSONB NOT NULL,
    status         TEXT NOT NULL DEFAULT 'pending'
                   CHECK (status IN ('pending','processed','rejected','superseded')),
    received_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    processed_at   TIMESTAMPTZ,
    reject_reason  TEXT
);
CREATE INDEX idx_order_inbox_pending_key
    ON staging.order_inbox (tenant_code, sfdc_order_id, received_at DESC)
    WHERE status = 'pending';
CREATE INDEX idx_order_inbox_status_received
    ON staging.order_inbox (status, received_at);

-- ============================================================================
-- audit
-- ============================================================================
CREATE TABLE audit.audit_log (
    audit_id      BIGSERIAL,
    tenant_id     TEXT,
    table_schema  TEXT NOT NULL,
    table_name    TEXT NOT NULL,
    op            CHAR(1) NOT NULL CHECK (op IN ('I','U','D')),
    pk            JSONB NOT NULL,
    old_row       JSONB,
    new_row       JSONB,
    actor         TEXT,
    at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (audit_id, at)
);

-- ============================================================================
-- TRIGGER FUNCTIONS — stock_balance maintenance
-- ============================================================================
-- 1. opening_balance INSERT → stock_balance.on_hand_qty (per subinventory + stock_status)
CREATE OR REPLACE FUNCTION processed.f_stock_balance_opening_apply()
RETURNS trigger LANGUAGE plpgsql AS $f$
DECLARE
    v_latest_date DATE;
BEGIN
    SELECT MAX(ob.as_of_date) INTO v_latest_date
      FROM processed.opening_balance ob
     WHERE ob.tenant_id    = NEW.tenant_id
       AND ob.product_id   = NEW.product_id
       AND ob.warehouse_id = NEW.warehouse_id
       AND ob.subinventory  = NEW.subinventory
       AND ob.stock_status  = NEW.stock_status
       AND COALESCE(ob.lot_id, 0) = COALESCE(NEW.lot_id, 0)
       AND ob.opening_balance_id <> NEW.opening_balance_id;

    IF v_latest_date IS NULL OR NEW.as_of_date >= v_latest_date THEN
        INSERT INTO processed.stock_balance (
            tenant_id, tenant_code, product_id, product_code,
            warehouse_id, warehouse_code, subinventory, stock_status,
            lot_id, lot_code, on_hand_qty, reserved_qty,
            uom_id, uom_code, last_updated_at
        )
        VALUES (
            NEW.tenant_id, NEW.tenant_code, NEW.product_id, NEW.product_code,
            NEW.warehouse_id, NEW.warehouse_code, NEW.subinventory, NEW.stock_status,
            COALESCE(NEW.lot_id, 0), NEW.lot_code, NEW.qty, 0,
            NEW.uom_id, NEW.uom_code, now()
        )
        ON CONFLICT (tenant_id, product_id, warehouse_id, subinventory, stock_status, lot_id)
        DO UPDATE SET
            on_hand_qty     = EXCLUDED.on_hand_qty,
            last_updated_at = now();
    ELSE
        PERFORM processed.notify_outbox(
            NEW.tenant_id, 'WARN', 'opening_balance_apply',
            format('back-dated opening_balance (as_of=%s, latest=%s) — run recalculate_stock_balance to apply',
                   NEW.as_of_date, v_latest_date),
            jsonb_build_object('product_code', NEW.product_code,
                               'warehouse_code', NEW.warehouse_code,
                               'subinventory', NEW.subinventory),
            'ob_backdated:' || NEW.tenant_code || ':' || NEW.product_code
            || ':' || NEW.warehouse_code || ':' || NEW.as_of_date
            || ':' || NEW.subinventory
        );
    END IF;

    RETURN NEW;
END;
$f$;

CREATE TRIGGER trg_opening_balance_stock_apply
    AFTER INSERT ON processed.opening_balance
    FOR EACH ROW EXECUTE FUNCTION processed.f_stock_balance_opening_apply();

-- 2. inv_transaction INSERT → stock_balance.on_hand_qty + close matching SFDC order line
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

    -- Cascade: close any matching order lines so trigger 3 releases the reservation.
    -- v4: when Q2 (Oracle txn ↔ SFDC line link) resolves, swap the matcher to
    -- sfdc_line_id. For now we still close at the order-header level.
    IF NEW.sfdc_order_id IS NOT NULL THEN
        UPDATE processed.sfdc_order_line
           SET line_state = 'closed', updated_at = now()
         WHERE sfdc_order_id = NEW.sfdc_order_id
           AND line_state IN ('open','synced');
    END IF;

    RETURN NEW;
END;
$f$;

CREATE TRIGGER trg_inv_transaction_stock_apply
    AFTER INSERT ON processed.inv_transaction
    FOR EACH ROW EXECUTE FUNCTION processed.f_stock_balance_txn_apply();

-- 3. sfdc_order_line INSERT/UPDATE/DELETE → reservation on the SPECIFIC row
--    (no more POOL sentinel).
--    Logic: independently release the OLD row (full OLD.qty if was active) and
--    reserve the NEW row (full NEW.qty if is active). This handles every shape
--    correctly, including an UPDATE that changes subinv or stock_status.
CREATE OR REPLACE FUNCTION processed.f_stock_balance_reservation_apply()
RETURNS trigger LANGUAGE plpgsql AS $f$
DECLARE
    v_active_states TEXT[] := ARRAY['open','synced'];
    v_was_active    BOOLEAN := false;
    v_is_active     BOOLEAN := false;
    v_release_qty   NUMERIC := 0;
    v_reserve_qty   NUMERIC := 0;
    v_old_subinv    TEXT;
    v_old_status    TEXT;
BEGIN
    IF TG_OP IN ('DELETE','UPDATE') THEN
        v_was_active := OLD.line_state = ANY(v_active_states);
        IF v_was_active THEN
            v_release_qty := OLD.qty;
            v_old_subinv  := OLD.subinventory;
            v_old_status  := OLD.stock_status;
        END IF;
    END IF;

    IF TG_OP IN ('INSERT','UPDATE') THEN
        v_is_active := NEW.line_state = ANY(v_active_states);
        IF v_is_active THEN
            v_reserve_qty := NEW.qty;
        END IF;
    END IF;

    -- Release on the OLD row (if any).
    IF v_release_qty > 0 THEN
        UPDATE processed.stock_balance
           SET reserved_qty    = reserved_qty - v_release_qty,
               last_updated_at = now()
         WHERE tenant_id    = OLD.tenant_id
           AND product_id   = OLD.product_id
           AND warehouse_id = OLD.warehouse_id
           AND subinventory = v_old_subinv
           AND stock_status = v_old_status
           AND lot_id       = 0;
    END IF;

    -- Reserve on the NEW row (if any). Creates the row if absent.
    IF v_reserve_qty > 0 THEN
        UPDATE processed.stock_balance
           SET reserved_qty    = reserved_qty + v_reserve_qty,
               last_updated_at = now()
         WHERE tenant_id    = NEW.tenant_id
           AND product_id   = NEW.product_id
           AND warehouse_id = NEW.warehouse_id
           AND subinventory = NEW.subinventory
           AND stock_status = NEW.stock_status
           AND lot_id       = 0;

        IF NOT FOUND THEN
            INSERT INTO processed.stock_balance (
                tenant_id, tenant_code, product_id, product_code,
                warehouse_id, warehouse_code, subinventory, stock_status,
                lot_id, lot_code, on_hand_qty, reserved_qty,
                uom_id, uom_code, last_updated_at
            )
            VALUES (
                NEW.tenant_id, NEW.tenant_code, NEW.product_id, NEW.product_code,
                NEW.warehouse_id, NEW.warehouse_code, NEW.subinventory, NEW.stock_status,
                0, NULL, 0, v_reserve_qty,
                NEW.uom_id, NEW.uom_code, now()
            )
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
-- HELPER: notify_outbox  (unchanged from v3)
-- ============================================================================
CREATE OR REPLACE FUNCTION processed.notify_outbox(
    p_tenant_id TEXT, p_severity TEXT, p_source TEXT,
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
-- DRAIN: load_stocklevel — file-based opening balance promotion
-- v4: DELETE existing opening_balance rows for this file's batch, then INSERT
--     fresh. No ON CONFLICT. Re-running the same file replays cleanly.
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
    v_existing   BIGINT;
    v_accepted   INTEGER := 0;
    v_rejected   INTEGER := 0;
    v_status     TEXT;
    v_master_row RECORD;
    v_tenant_id  TEXT;
BEGIN
    IF p_file_name IS NULL OR length(p_file_name) = 0 THEN
        RAISE EXCEPTION 'load_stocklevel: file_name is required';
    END IF;

    -- Replay-friendly: if a batch exists for this file, blow away its opening_balance
    -- rows before re-inserting. The trigger will rebuild stock_balance from the new
    -- rows; recalculate_stock_balance is the manual recovery path if needed.
    SELECT b.batch_id INTO v_existing
      FROM staging.stocklevel_batch b WHERE b.file_name = p_file_name FOR UPDATE;
    IF v_existing IS NOT NULL THEN
        DELETE FROM processed.opening_balance WHERE batch_id = v_existing;
        UPDATE staging.stocklevel_batch
           SET status = 'pending', completed_at = NULL,
               rows_accepted = 0, rows_rejected = 0
         WHERE batch_id = v_existing;
        v_batch_id := v_existing;
    ELSE
        INSERT INTO staging.stocklevel_batch (file_name) VALUES (p_file_name)
        RETURNING staging.stocklevel_batch.batch_id INTO v_batch_id;
    END IF;

    -- Auto-create masters as needed.
    FOR v_master_row IN
        SELECT DISTINCT s.tenant_code,
               s.item_segment1 AS product_code,
               s.warehouse     AS warehouse_code,
               s.primary_uom   AS uom_code
          FROM staging.stocklevel_inbox s
         WHERE s.file_name = p_file_name AND s.promoted_at IS NULL AND s.tenant_code IS NOT NULL
    LOOP
        SELECT t.tenant_id INTO v_tenant_id
          FROM processed.tenant t WHERE t.tenant_code = v_master_row.tenant_code;
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

    -- Insert the promoted rows.
    WITH src AS (
        SELECT s.inbox_id, s.tenant_code,
               s.item_segment1 AS product_code,
               s.warehouse     AS warehouse_code,
               COALESCE(NULLIF(trim(s.subinventory), ''), '') AS subinventory,
               COALESCE(NULLIF(trim(s.stock_status), ''), 'LIBERATED') AS stock_status,
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
            as_of_date, batch_id, source_file
        )
        SELECT tenant_id, tenant_code, product_id, product_code,
               warehouse_id, warehouse_code,
               subinventory, stock_status,
               qty, uom_id, uom_code,
               snapshot_date, v_batch_id, p_file_name
          FROM src WHERE reject_reason IS NULL
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
-- DRAIN: load_transactions — append-only inbox, dedupe by external_txn_id
-- v4: pick latest pending row per (tenant_code, external_txn_id). Older pending
--     rows for the same key get marked 'superseded'. processed.inv_transaction
--     enforces UNIQUE so duplicate txn_id from FOP retry is silently absorbed.
-- ============================================================================
CREATE OR REPLACE FUNCTION load_transactions(p_limit INTEGER DEFAULT 500)
RETURNS TABLE (rows_processed INTEGER, rows_failed INTEGER, rows_superseded INTEGER)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, staging, public, pg_catalog
AS $f$
DECLARE
    v_row     RECORD;
    v_tid     TEXT;
    v_pid     BIGINT;
    v_wid     BIGINT;
    v_uid     BIGINT;
    v_ok      INTEGER := 0;
    v_bad     INTEGER := 0;
    v_sup     INTEGER := 0;
    v_payload JSONB;
BEGIN
    -- Mark older pending rows as superseded (when same key has multiple rows).
    WITH ranked AS (
        SELECT inbox_id,
               row_number() OVER (PARTITION BY tenant_code, external_txn_id
                                  ORDER BY received_at DESC, inbox_id DESC) AS rn
          FROM staging.txn_inbox
         WHERE status = 'pending'
    ),
    superseded AS (
        UPDATE staging.txn_inbox ti
           SET status = 'superseded', processed_at = now()
          FROM ranked r
         WHERE ti.inbox_id = r.inbox_id AND r.rn > 1
         RETURNING ti.inbox_id
    )
    SELECT COUNT(*)::int INTO v_sup FROM superseded;

    -- Drain the latest pending row per key.
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
            SELECT t.tenant_id INTO v_tid
              FROM processed.tenant t WHERE t.tenant_code = v_row.tenant_code;
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

            -- Idempotent INSERT — if (tenant_id, external_txn_id) already exists in
            -- processed.inv_transaction (FOP retried after we processed it), the
            -- WHERE NOT EXISTS guard silently absorbs the duplicate. No UPSERT.
            INSERT INTO processed.inv_transaction (
                tenant_id, tenant_code, external_txn_id,
                product_id, product_code, warehouse_id, warehouse_code,
                subinventory, stock_status,
                signed_qty, uom_id, uom_code, txn_type,
                posted_at, sfdc_order_id, sfdc_line_id, payload
            )
            SELECT v_tid, v_row.tenant_code, v_row.external_txn_id,
                   v_pid, v_payload->>'product_code', v_wid, v_payload->>'warehouse_code',
                   v_payload->>'subinventory',
                   COALESCE(v_payload->>'stock_status', 'LIBERATED'),
                   (v_payload->>'signed_qty')::numeric, v_uid, v_payload->>'uom_code',
                   COALESCE(v_payload->>'txn_type', 'unknown'),
                   COALESCE((v_payload->>'posted_at')::timestamptz, now()),
                   v_payload->>'sfdc_order_id',
                   v_payload->>'sfdc_line_id',
                   v_payload
             WHERE NOT EXISTS (
                 SELECT 1 FROM processed.inv_transaction it
                  WHERE it.tenant_id = v_tid AND it.external_txn_id = v_row.external_txn_id
             );

            UPDATE staging.txn_inbox SET status='processed', processed_at=now() WHERE inbox_id = v_row.inbox_id;
            v_ok := v_ok + 1;
        EXCEPTION WHEN OTHERS THEN
            UPDATE staging.txn_inbox SET status='rejected', processed_at=now(),
                   reject_reason=SQLERRM
             WHERE inbox_id = v_row.inbox_id;
            v_bad := v_bad + 1;
        END;
    END LOOP;

    RETURN QUERY SELECT v_ok, v_bad, v_sup;
END;
$f$;

-- ============================================================================
-- DRAIN: load_orders — append-only inbox, latest-wins, DELETE-then-INSERT
-- v4: pick DISTINCT ON (tenant_code, sfdc_order_id) ORDER BY received_at DESC.
--     Then DELETE existing sfdc_order (cascades to lines + reservation release
--     via trigger 3) and INSERT fresh. Older pending rows for the same key
--     marked 'superseded'. No ON CONFLICT anywhere.
-- ============================================================================
CREATE OR REPLACE FUNCTION load_orders(p_limit INTEGER DEFAULT 500)
RETURNS TABLE (rows_processed INTEGER, rows_failed INTEGER, rows_superseded INTEGER)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, staging, public, pg_catalog
AS $f$
DECLARE
    v_row      RECORD;
    v_tid      TEXT;
    v_line     JSONB;
    v_line_no  INT;
    v_pid      BIGINT;
    v_wid      BIGINT;
    v_uid      BIGINT;
    v_ok       INTEGER := 0;
    v_bad      INTEGER := 0;
    v_sup      INTEGER := 0;
    v_payload  JSONB;
BEGIN
    -- Mark older pending rows for the same key as superseded.
    WITH ranked AS (
        SELECT inbox_id,
               row_number() OVER (PARTITION BY tenant_code, sfdc_order_id
                                  ORDER BY received_at DESC, inbox_id DESC) AS rn
          FROM staging.order_inbox
         WHERE status = 'pending'
    ),
    superseded AS (
        UPDATE staging.order_inbox oi
           SET status = 'superseded', processed_at = now()
          FROM ranked r
         WHERE oi.inbox_id = r.inbox_id AND r.rn > 1
         RETURNING oi.inbox_id
    )
    SELECT COUNT(*)::int INTO v_sup FROM superseded;

    -- Drain the latest pending row per key.
    FOR v_row IN
        SELECT inbox_id, tenant_code, sfdc_order_id, payload, received_at
          FROM staging.order_inbox
         WHERE status = 'pending'
         ORDER BY received_at, inbox_id
         LIMIT p_limit
         FOR UPDATE SKIP LOCKED
    LOOP
        v_payload := v_row.payload;
        BEGIN
            SELECT t.tenant_id INTO v_tid
              FROM processed.tenant t WHERE t.tenant_code = v_row.tenant_code;
            IF v_tid IS NULL THEN
                UPDATE staging.order_inbox SET status='rejected', processed_at=now(),
                       reject_reason='unknown_tenant'
                 WHERE inbox_id = v_row.inbox_id;
                v_bad := v_bad + 1;
                CONTINUE;
            END IF;

            -- v5: UPSERT on sfdc_order preserves created_at; lines re-numbered fresh.
            -- DELETE of order_line fires trigger 3 which releases the prior reservation;
            -- subsequent INSERTs re-reserve on the new lines' subinv rows.
            DELETE FROM processed.sfdc_order_line WHERE sfdc_order_id = v_row.sfdc_order_id;

            INSERT INTO processed.sfdc_order (
                sfdc_order_id, tenant_id, tenant_code, customer_id,
                order_state, payload, received_at
            )
            VALUES (
                v_row.sfdc_order_id, v_tid, v_row.tenant_code,
                COALESCE(v_payload->>'customer_id', 'unknown'),
                COALESCE(v_payload->>'order_state', 'open'),
                v_payload, v_row.received_at
            )
            ON CONFLICT (sfdc_order_id) DO UPDATE
            SET tenant_id   = EXCLUDED.tenant_id,
                tenant_code = EXCLUDED.tenant_code,
                customer_id = EXCLUDED.customer_id,
                order_state = EXCLUDED.order_state,
                payload     = EXCLUDED.payload,
                received_at = EXCLUDED.received_at,
                updated_at  = now();
            -- created_at NOT touched - preserves original insertion time

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

                -- Insert the order line. Trigger 3 fires AFTER INSERT and reserves
                -- on the specific (subinv, stock_status) row.
                INSERT INTO processed.sfdc_order_line (
                    sfdc_order_id, line_no, sfdc_line_id,
                    tenant_id, tenant_code,
                    product_id, product_code, warehouse_id, warehouse_code,
                    subinventory, stock_status,
                    qty, uom_id, uom_code, line_state, payload
                )
                VALUES (
                    v_row.sfdc_order_id, v_line_no, v_line->>'sfdc_line_id',
                    v_tid, v_row.tenant_code,
                    v_pid, v_line->>'product_code', v_wid, v_line->>'warehouse_code',
                    COALESCE(v_line->>'subinventory', ''),
                    COALESCE(v_line->>'stock_status', 'LIBERATED'),
                    (v_line->>'qty')::numeric, v_uid, v_line->>'uom_code',
                    COALESCE(v_line->>'line_state', 'open'),
                    v_line
                );
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

    RETURN QUERY SELECT v_ok, v_bad, v_sup;
END;
$f$;

-- ============================================================================
-- READ: fetch_inventory — simplified, no POOL special case
-- Each (subinventory, stock_status) row reports its own on_hand and reserved.
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
    SELECT sb.tenant_code, sb.product_code, sb.warehouse_code,
           CASE WHEN p_subinventory IS NULL THEN 'ALL' ELSE sb.subinventory END AS subinventory,
           sb.stock_status,
           SUM(sb.on_hand_qty)                          AS on_hand_qty,
           SUM(sb.reserved_qty)                         AS reserved_qty,
           SUM(sb.on_hand_qty) - SUM(sb.reserved_qty)   AS atp,
           MAX(sb.uom_code)                             AS uom_code,
           MAX(sb.last_updated_at)                      AS last_updated_at
      FROM processed.stock_balance sb
     WHERE sb.tenant_code = p_tenant_code
       AND (p_product_code   IS NULL OR sb.product_code   = p_product_code)
       AND (p_warehouse_code IS NULL OR sb.warehouse_code = p_warehouse_code)
       AND (p_subinventory   IS NULL OR sb.subinventory   = p_subinventory)
       AND (p_stock_status = 'ALL' OR sb.stock_status = p_stock_status)
     GROUP BY sb.tenant_code, sb.product_code, sb.warehouse_code,
              CASE WHEN p_subinventory IS NULL THEN 'ALL' ELSE sb.subinventory END,
              sb.stock_status;
$f$;

-- ============================================================================
-- MAINTENANCE: recalculate_stock_balance — rebuild with per-subinv reservations
-- v4: reservations land on the specific (subinv, stock_status) row.
--     No POOL rows are generated.
-- ============================================================================
CREATE OR REPLACE FUNCTION recalculate_stock_balance(p_tenant_code TEXT)
RETURNS TABLE (rows_rebuilt INTEGER)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, public, pg_catalog
AS $f$
DECLARE
    v_tid TEXT;
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
    -- v4: reservations are now per-(subinv, stock_status) — no POOL.
    reservations AS (
        SELECT sol.tenant_id, sol.product_id, sol.warehouse_id,
               sol.subinventory, sol.stock_status,
               SUM(sol.qty) AS sum_reserved
          FROM processed.sfdc_order_line sol
         WHERE sol.tenant_id = v_tid AND sol.line_state IN ('open','synced')
         GROUP BY sol.tenant_id, sol.product_id, sol.warehouse_id,
                  sol.subinventory, sol.stock_status
    ),
    -- Real stock_balance rows: one per (subinv, stock_status, lot)
    -- with both on_hand_qty and reserved_qty populated.
    rebuilt AS (
        INSERT INTO processed.stock_balance (
            tenant_id, tenant_code, product_id, product_code,
            warehouse_id, warehouse_code, subinventory, stock_status,
            lot_id, lot_code, on_hand_qty, reserved_qty,
            uom_id, uom_code, last_updated_at
        )
        SELECT lo.tenant_id, lo.tenant_code, lo.product_id, lo.product_code,
               lo.warehouse_id, lo.warehouse_code, lo.subinventory, lo.stock_status,
               COALESCE(lo.lot_id, 0), lo.lot_code,
               lo.qty + COALESCE(ta.sum_signed, 0),
               COALESCE(rv.sum_reserved, 0),
               lo.uom_id, lo.uom_code, now()
          FROM latest_ob lo
          LEFT JOIN txn_after ta
                 ON ta.tenant_id    = lo.tenant_id
                AND ta.product_id   = lo.product_id
                AND ta.warehouse_id = lo.warehouse_id
                AND ta.subinventory = lo.subinventory
                AND ta.stock_status = lo.stock_status
                AND ta.lot_key      = COALESCE(lo.lot_id, 0)
          LEFT JOIN reservations rv
                 ON rv.tenant_id    = lo.tenant_id
                AND rv.product_id   = lo.product_id
                AND rv.warehouse_id = lo.warehouse_id
                AND rv.subinventory = lo.subinventory
                AND rv.stock_status = lo.stock_status
        RETURNING 1
    )
    SELECT COUNT(*) INTO v_n FROM rebuilt;

    RETURN QUERY SELECT v_n;
END;
$f$;

-- ============================================================================
-- staging.promote_one_txn — does the work for ONE staging.txn_inbox row.
-- Called by the trigger AND callable directly for backfill / retry.
-- ============================================================================
CREATE OR REPLACE FUNCTION staging.promote_one_txn(p_inbox_id BIGINT)
RETURNS VOID
LANGUAGE plpgsql AS $f$
DECLARE
    v_row             staging.txn_inbox%ROWTYPE;
    v_payload         JSONB;
    v_tid             TEXT;
    v_pid             BIGINT;
    v_wid             BIGINT;
    v_uid             BIGINT;
    -- Extracted fields (populated from whichever shape was sent)
    v_product_code    TEXT;
    v_warehouse_code  TEXT;
    v_uom_code        TEXT;
    v_subinventory    TEXT;
    v_stock_status    TEXT;
    v_signed_qty      NUMERIC;
    v_txn_type        TEXT;
    v_posted_at       TIMESTAMPTZ;
    v_sfdc_order_id   TEXT;
    v_sfdc_line_id    TEXT;
BEGIN
    SELECT * INTO v_row FROM staging.txn_inbox WHERE inbox_id = p_inbox_id;
    IF NOT FOUND THEN RETURN; END IF;
    v_payload := v_row.payload;

    -- ── Shape detection ────────────────────────────────────────────────────
    IF v_payload ? 'TRANSACTION_TYPE' OR v_payload ? 'TRANSACTION_ID' THEN
        -- Oracle EBS native
        v_product_code   := v_payload->>'ITEM_CODE';
        v_warehouse_code := v_payload->>'ORGANIZATION_NAME';
        v_uom_code       := v_payload->>'PRIMARY_UOM_CODE';
        v_subinventory   := v_payload->>'SOURCE_SUBINVENTORY';
        v_stock_status   := 'LIBERATED';
        v_signed_qty     := (v_payload->>'PRIMARY_QTY')::numeric;
        v_txn_type       := COALESCE(v_payload->>'TRANSACTION_TYPE', 'unknown');
        v_posted_at      := COALESCE((v_payload->>'TRX_DATE_TIME')::timestamptz, now());
        -- TODO Q2: confirm. TRANSACTION_REF looks like WMS doc ref; SOURCE_LINE_ID
        -- is Oracle SO line ID. Placeholder until Michelin clarifies SFDC mapping.
        v_sfdc_order_id  := v_payload->>'TRANSACTION_REF';
        v_sfdc_line_id   := (v_payload->>'SOURCE_LINE_ID')::text;
    ELSE
        -- Normalized snake_case
        v_product_code   := v_payload->>'product_code';
        v_warehouse_code := v_payload->>'warehouse_code';
        v_uom_code       := v_payload->>'uom_code';
        v_subinventory   := v_payload->>'subinventory';
        v_stock_status   := COALESCE(v_payload->>'stock_status', 'LIBERATED');
        v_signed_qty     := (v_payload->>'signed_qty')::numeric;
        v_txn_type       := COALESCE(v_payload->>'txn_type', 'unknown');
        v_posted_at      := COALESCE((v_payload->>'posted_at')::timestamptz, now());
        v_sfdc_order_id  := v_payload->>'sfdc_order_id';
        v_sfdc_line_id   := v_payload->>'sfdc_line_id';
    END IF;

    -- ── Master lookup ─────────────────────────────────────────────────────
    SELECT t.tenant_id INTO v_tid
      FROM processed.tenant t WHERE t.tenant_code = v_row.tenant_code;
    IF v_tid IS NULL THEN
        UPDATE staging.txn_inbox SET status='rejected', processed_at=now(),
               reject_reason='unknown_tenant'
         WHERE inbox_id = p_inbox_id;
        RETURN;
    END IF;

    SELECT p.product_id   INTO v_pid FROM processed.product   p WHERE p.tenant_id = v_tid AND p.product_code   = v_product_code;
    SELECT w.warehouse_id INTO v_wid FROM processed.warehouse w WHERE w.tenant_id = v_tid AND w.warehouse_code = v_warehouse_code;
    SELECT u.uom_id       INTO v_uid FROM processed.uom       u WHERE u.tenant_id = v_tid AND u.uom_code       = v_uom_code;

    IF v_pid IS NULL OR v_wid IS NULL OR v_uid IS NULL THEN
        UPDATE staging.txn_inbox SET status='rejected', processed_at=now(),
               reject_reason=CASE WHEN v_pid IS NULL THEN 'unknown_product:'   || COALESCE(v_product_code,   '<null>')
                                  WHEN v_wid IS NULL THEN 'unknown_warehouse:' || COALESCE(v_warehouse_code, '<null>')
                                  ELSE                    'unknown_uom:'       || COALESCE(v_uom_code,       '<null>') END
         WHERE inbox_id = p_inbox_id;
        RETURN;
    END IF;

    -- ── Idempotent promote ─────────────────────────────────────────────────
    INSERT INTO processed.inv_transaction (
        tenant_id, tenant_code, external_txn_id,
        product_id, product_code, warehouse_id, warehouse_code,
        subinventory, stock_status,
        signed_qty, uom_id, uom_code, txn_type,
        posted_at, sfdc_order_id, sfdc_line_id, payload
    )
    SELECT v_tid, v_row.tenant_code, v_row.external_txn_id,
           v_pid, v_product_code, v_wid, v_warehouse_code,
           v_subinventory, v_stock_status,
           v_signed_qty, v_uid, v_uom_code, v_txn_type,
           v_posted_at, v_sfdc_order_id, v_sfdc_line_id, v_payload
     WHERE NOT EXISTS (
         SELECT 1 FROM processed.inv_transaction it
          WHERE it.tenant_id = v_tid AND it.external_txn_id = v_row.external_txn_id
     );

    UPDATE staging.txn_inbox SET status='processed', processed_at=now()
     WHERE inbox_id = p_inbox_id;
EXCEPTION WHEN OTHERS THEN
    UPDATE staging.txn_inbox SET status='rejected', processed_at=now(),
           reject_reason=SQLERRM
     WHERE inbox_id = p_inbox_id;
END;
$f$;

-- ============================================================================
-- staging.promote_one_order — does the work for ONE staging.order_inbox row.
-- ============================================================================
CREATE OR REPLACE FUNCTION staging.promote_one_order(p_inbox_id BIGINT)
RETURNS VOID
LANGUAGE plpgsql AS $f$
DECLARE
    v_row             staging.order_inbox%ROWTYPE;
    v_payload         JSONB;
    v_tid             TEXT;
    v_lines           JSONB;
    v_line            JSONB;
    v_line_no         INT := 0;
    v_pid             BIGINT;
    v_wid             BIGINT;
    v_uid             BIGINT;
    v_customer_id     TEXT;
    v_order_state     TEXT;
    v_line_sfdc_id    TEXT;
    v_line_product    TEXT;
    v_line_warehouse  TEXT;
    v_line_uom        TEXT;
    v_line_subinv     TEXT;
    v_line_status     TEXT;
    v_line_qty        NUMERIC;
    v_is_sfdc_native  BOOLEAN;
BEGIN
    SELECT * INTO v_row FROM staging.order_inbox WHERE inbox_id = p_inbox_id;
    IF NOT FOUND THEN RETURN; END IF;
    v_payload := v_row.payload;

    SELECT t.tenant_id INTO v_tid
      FROM processed.tenant t WHERE t.tenant_code = v_row.tenant_code;
    IF v_tid IS NULL THEN
        UPDATE staging.order_inbox SET status='rejected', processed_at=now(),
               reject_reason='unknown_tenant'
         WHERE inbox_id = p_inbox_id;
        RETURN;
    END IF;

    -- ── Shape detection: header ────────────────────────────────────────────
    v_is_sfdc_native := v_payload ? 'sfRecordId__c' OR v_payload ? '_ObjectType';

    IF v_is_sfdc_native THEN
        v_customer_id := COALESCE(v_payload->>'soldToAccount__c', 'unknown');
        v_order_state := CASE COALESCE(v_payload->>'status__c', '')
                            WHEN 'Cancelled' THEN 'cancelled'
                            WHEN 'Closed'    THEN 'closed'
                            WHEN 'Completed' THEN 'closed'
                            ELSE 'open'
                         END;
        -- workOrderLineItems__c is a STRINGIFIED JSON array — parse twice
        v_lines := COALESCE((v_payload->>'workOrderLineItems__c')::jsonb, '[]'::jsonb);
    ELSE
        v_customer_id := COALESCE(v_payload->>'customer_id', 'unknown');
        v_order_state := COALESCE(v_payload->>'order_state', 'open');
        v_lines       := COALESCE(v_payload->'lines', '[]'::jsonb);
    END IF;

    -- ── DELETE old lines + UPSERT header ──────────────────────────────────
    DELETE FROM processed.sfdc_order_line WHERE sfdc_order_id = v_row.sfdc_order_id;

    INSERT INTO processed.sfdc_order (
        sfdc_order_id, tenant_id, tenant_code, customer_id,
        order_state, payload, received_at
    )
    VALUES (
        v_row.sfdc_order_id, v_tid, v_row.tenant_code,
        v_customer_id, v_order_state,
        v_payload, v_row.received_at
    )
    ON CONFLICT (sfdc_order_id) DO UPDATE
    SET tenant_id   = EXCLUDED.tenant_id,
        tenant_code = EXCLUDED.tenant_code,
        customer_id = EXCLUDED.customer_id,
        order_state = EXCLUDED.order_state,
        payload     = EXCLUDED.payload,
        received_at = EXCLUDED.received_at,
        updated_at  = now();

    -- ── Per-line: shape detect + map + INSERT ─────────────────────────────
    FOR v_line IN SELECT * FROM jsonb_array_elements(v_lines)
    LOOP
        v_line_no := v_line_no + 1;

        IF v_line ? 'Product_Code__c' OR (v_line ? 'Id' AND v_line ? 'WorkOrderId') THEN
            -- SFDC native line
            v_line_sfdc_id   := v_line->>'Id';
            v_line_product   := v_line->>'Product_Code__c';
            -- TODO: Location_External_Id__c sample value is "40"; warehouse_codes
            -- look like "408_ES_COS_COS_WH_CO". May need a mapping table.
            v_line_warehouse := v_line->>'Location_External_Id__c';
            -- TODO: Storage_Location__c is an opaque SFDC record ID
            -- ("1319Z000001l1mYQAQ"). Resolve via mapping table when one exists.
            v_line_subinv    := v_line->>'Storage_Location__c';
            v_line_uom       := COALESCE(v_line->>'uom_code', 'EA');
            v_line_status    := CASE COALESCE(v_line->>'Status', '')
                                    WHEN 'Cancelled' THEN 'cancelled'
                                    WHEN 'Closed'    THEN 'closed'
                                    WHEN 'Completed' THEN 'closed'
                                    ELSE 'open'
                                END;
            v_line_qty       := (v_line->>'Quantity')::numeric;
        ELSE
            -- Normalized snake_case line
            v_line_sfdc_id   := v_line->>'sfdc_line_id';
            v_line_product   := v_line->>'product_code';
            v_line_warehouse := v_line->>'warehouse_code';
            v_line_subinv    := COALESCE(v_line->>'subinventory', '');
            v_line_uom       := v_line->>'uom_code';
            v_line_status    := COALESCE(v_line->>'line_state', 'open');
            v_line_qty       := (v_line->>'qty')::numeric;
        END IF;

        SELECT p.product_id   INTO v_pid FROM processed.product   p WHERE p.tenant_id = v_tid AND p.product_code   = v_line_product;
        SELECT w.warehouse_id INTO v_wid FROM processed.warehouse w WHERE w.tenant_id = v_tid AND w.warehouse_code = v_line_warehouse;
        SELECT u.uom_id       INTO v_uid FROM processed.uom       u WHERE u.tenant_id = v_tid AND u.uom_code       = v_line_uom;

        IF v_pid IS NULL OR v_wid IS NULL OR v_uid IS NULL THEN
            RAISE EXCEPTION 'line %: %',
                v_line_no,
                CASE WHEN v_pid IS NULL THEN 'unknown_product:'   || COALESCE(v_line_product,   '<null>')
                     WHEN v_wid IS NULL THEN 'unknown_warehouse:' || COALESCE(v_line_warehouse, '<null>')
                     ELSE                    'unknown_uom:'       || COALESCE(v_line_uom,       '<null>') END;
        END IF;

        INSERT INTO processed.sfdc_order_line (
            sfdc_order_id, line_no, sfdc_line_id,
            tenant_id, tenant_code,
            product_id, product_code, warehouse_id, warehouse_code,
            subinventory, stock_status,
            qty, uom_id, uom_code, line_state, payload
        )
        VALUES (
            v_row.sfdc_order_id, v_line_no, v_line_sfdc_id,
            v_tid, v_row.tenant_code,
            v_pid, v_line_product, v_wid, v_line_warehouse,
            COALESCE(v_line_subinv, ''),
            'LIBERATED',
            v_line_qty, v_uid, v_line_uom, v_line_status,
            v_line
        );
    END LOOP;

    UPDATE staging.order_inbox SET status='processed', processed_at=now()
     WHERE inbox_id = p_inbox_id;
EXCEPTION WHEN OTHERS THEN
    UPDATE staging.order_inbox SET status='rejected', processed_at=now(),
           reject_reason=SQLERRM
     WHERE inbox_id = p_inbox_id;
END;
$f$;

-- ============================================================================
-- THIN TRIGGER WRAPPERS: just call the procedures
-- ============================================================================
CREATE OR REPLACE FUNCTION staging.f_promote_txn()
RETURNS trigger LANGUAGE plpgsql AS $f$
BEGIN
    PERFORM staging.promote_one_txn(NEW.inbox_id);
    RETURN NEW;
END;
$f$;

CREATE OR REPLACE FUNCTION staging.f_promote_order()
RETURNS trigger LANGUAGE plpgsql AS $f$
BEGIN
    PERFORM staging.promote_one_order(NEW.inbox_id);
    RETURN NEW;
END;
$f$;

CREATE TRIGGER trg_txn_inbox_promote
    AFTER INSERT ON staging.txn_inbox
    FOR EACH ROW EXECUTE FUNCTION staging.f_promote_txn();

CREATE TRIGGER trg_order_inbox_promote
    AFTER INSERT ON staging.order_inbox
    FOR EACH ROW EXECUTE FUNCTION staging.f_promote_order();

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
