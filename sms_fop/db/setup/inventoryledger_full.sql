-- inventoryledger_full.sql
--
-- Complete one-shot setup: tables + functions + triggers + views.
-- Produces a fully working Inventory Ledger database.
--
-- Requires PostgreSQL 13+ (gen_random_uuid() built in).
-- No extensions needed (pg_cron/pg_partman are operational, not structural).
--
-- Apply to a fresh database:
--     createdb inventoryledger
--     psql -d inventoryledger -f inventoryledger_full.sql
--
-- Then load mock data:
--     psql -d inventoryledger -f mock_data.sql

BEGIN;

-- ============================================================================
-- Schemas
-- ============================================================================
CREATE SCHEMA IF NOT EXISTS staging;
CREATE SCHEMA IF NOT EXISTS audit;

-- ============================================================================
-- Infrastructure: pipeline_config, notification_outbox, audit_log, mv_atp_dirty
-- ============================================================================

CREATE TABLE pipeline_config (
    key         text PRIMARY KEY,
    value       text NOT NULL,
    description text NOT NULL,
    updated_at  timestamptz NOT NULL DEFAULT now()
);

INSERT INTO pipeline_config (key, value, description) VALUES
    ('archive_retention_days',   '90',     'audit_log partitions older than this are detached.'),
    ('inv_txn_retention',        '2 years','inv_transaction partitions older than this are archived.'),
    ('txn_inbox_purge_days',     '7',      'staging.txn_inbox processed rows older than this are purged.'),
    ('order_inbox_purge_days',   '7',      'staging.order_inbox processed rows older than this are purged.'),
    ('reject_purge_days',        '30',     'staging.ob_reject rows older than this are purged.'),
    ('drift_sample_pct',         '5',      'Percentage of combinations sampled by drift detection.'),
    ('drift_alert_severity',     'warn',   'Severity tag for drift notifications.'),
    ('bulk_max_size',            '1000',   'post_transaction_bulk rejects arrays longer than this.'),
    ('payload_max_bytes',        '10240',  'post_transaction rejects payloads larger than this.'),
    ('txn_inbox_batch_size',     '500',    'process_txn_inbox claims at most this many rows per invocation.'),
    ('order_inbox_batch_size',   '200',    'process_order_inbox claims at most this many rows per invocation.'),
    ('outbox_dedup_window_minutes','60',   'Repeated notifications with same dedup_key within this window bump repeat_count.')
ON CONFLICT (key) DO NOTHING;

CREATE OR REPLACE FUNCTION pipeline_config_int(p_key text)
RETURNS int LANGUAGE sql STABLE AS $$
    SELECT value::int FROM pipeline_config WHERE key = p_key;
$$;

CREATE OR REPLACE FUNCTION pipeline_config_int(p_key text, p_default int)
RETURNS int LANGUAGE sql STABLE AS $$
    SELECT COALESCE((SELECT value::int FROM pipeline_config WHERE key = p_key), p_default);
$$;

CREATE OR REPLACE FUNCTION pipeline_config_text(p_key text)
RETURNS text LANGUAGE sql STABLE AS $$
    SELECT value FROM pipeline_config WHERE key = p_key;
$$;

CREATE TABLE notification_outbox (
    notification_outbox_id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id              uuid,
    severity               text NOT NULL,
    source                 text NOT NULL,
    message                text NOT NULL,
    payload                jsonb,
    status                 text NOT NULL DEFAULT 'pending',
    retry_count            int NOT NULL DEFAULT 0,
    repeat_count           int NOT NULL DEFAULT 1,
    dedup_key              text,
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
CREATE INDEX idx_notification_outbox_dedup
    ON notification_outbox (tenant_id, source, dedup_key, created_at)
    WHERE status IN ('pending', 'failed');

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

CREATE TABLE mv_atp_dirty (
    tenant_id uuid PRIMARY KEY,
    marked_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================================
-- Tenant + master data (auto-populated by drain functions)
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
-- Opening balance
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
-- Inventory transaction — immutable ledger, partitioned by posted_at
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
        'receipt', 'issue', 'shipment',
        'transfer_out', 'transfer_in', 'adjustment',
        'sales_return', 'purchase_return', 'scrap'
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
-- Stock balance — current position
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
-- Salesforce order + lines
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

CREATE INDEX idx_sfdc_order_tenant_state ON sfdc_order (tenant_id, order_state);

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
-- Staging tables — 100% flat, text codes only
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
-- Materialized view: ATP per (tenant, product, warehouse)
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
    k.tenant_id, k.product_id, k.warehouse_id,
    COALESCE((SELECT SUM(qty) FROM opening_balance ob
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
                AND ol.line_state IN ('open', 'synced') AND ol.fop_synced_at IS NULL), 0) AS pending_qty,
    (COALESCE((SELECT SUM(qty) FROM opening_balance ob
               WHERE ob.tenant_id = k.tenant_id AND ob.product_id = k.product_id
                 AND ob.warehouse_id = k.warehouse_id), 0)
      + COALESCE((SELECT SUM(signed_qty) FROM inv_transaction it
                  WHERE it.tenant_id = k.tenant_id AND it.product_id = k.product_id
                    AND it.warehouse_id = k.warehouse_id), 0)
      - COALESCE((SELECT SUM(qty) FROM sfdc_order_line ol
                  WHERE ol.tenant_id = k.tenant_id AND ol.product_id = k.product_id
                    AND ol.warehouse_id = k.warehouse_id
                    AND ol.line_state IN ('open', 'synced') AND ol.fop_synced_at IS NULL), 0)
    ) AS atp_qty,
    now() AS computed_at
FROM keys k;

CREATE UNIQUE INDEX idx_mv_atp_key ON mv_atp (tenant_id, product_id, warehouse_id);

-- ============================================================================
-- Views
-- ============================================================================
CREATE VIEW vw_atp_by_product AS
SELECT tenant_id, product_id,
       SUM(on_hand_qty)  AS on_hand_qty,
       SUM(reserved_qty) AS reserved_qty,
       SUM(allocated_qty) AS allocated_qty,
       SUM(pending_qty)  AS pending_qty,
       SUM(atp_qty)      AS atp_qty
  FROM mv_atp
 GROUP BY tenant_id, product_id;

CREATE VIEW vw_atp_by_warehouse AS
SELECT tenant_id, warehouse_id,
       SUM(on_hand_qty)  AS on_hand_qty,
       SUM(reserved_qty) AS reserved_qty,
       SUM(allocated_qty) AS allocated_qty,
       SUM(pending_qty)  AS pending_qty,
       SUM(atp_qty)      AS atp_qty
  FROM mv_atp
 GROUP BY tenant_id, warehouse_id;


-- ############################################################################
-- FUNCTIONS & TRIGGERS
-- ############################################################################

-- ============================================================================
-- notify_outbox — dedup-aware notification insert
-- ============================================================================
CREATE OR REPLACE FUNCTION notify_outbox(
    p_tenant_id uuid,
    p_severity  text,
    p_source    text,
    p_message   text,
    p_payload   jsonb,
    p_dedup_key text DEFAULT NULL
) RETURNS bigint
LANGUAGE plpgsql AS $$
DECLARE
    v_window int := pipeline_config_int('outbox_dedup_window_minutes');
    v_id bigint;
BEGIN
    IF p_dedup_key IS NULL THEN
        INSERT INTO notification_outbox (tenant_id, severity, source, message, payload, dedup_key, repeat_count)
        VALUES (p_tenant_id, p_severity, p_source, p_message, p_payload, NULL, 1)
        RETURNING notification_outbox_id INTO v_id;
        RETURN v_id;
    END IF;

    SELECT notification_outbox_id INTO v_id
      FROM notification_outbox
     WHERE COALESCE(tenant_id::text,'') = COALESCE(p_tenant_id::text,'')
       AND source = p_source AND dedup_key = p_dedup_key
       AND status IN ('pending','failed')
       AND created_at > now() - (v_window || ' minutes')::interval
     ORDER BY created_at DESC LIMIT 1 FOR UPDATE;

    IF v_id IS NOT NULL THEN
        UPDATE notification_outbox
           SET repeat_count = repeat_count + 1, message = p_message,
               payload = p_payload, updated_at = now()
         WHERE notification_outbox_id = v_id;
        RETURN v_id;
    END IF;

    INSERT INTO notification_outbox (tenant_id, severity, source, message, payload, dedup_key, repeat_count)
    VALUES (p_tenant_id, p_severity, p_source, p_message, p_payload, p_dedup_key, 1)
    RETURNING notification_outbox_id INTO v_id;
    RETURN v_id;
END$$;

-- ============================================================================
-- audit.f_audit_capture — generic audit trigger
-- ============================================================================
CREATE OR REPLACE FUNCTION audit.f_audit_capture()
RETURNS trigger LANGUAGE plpgsql
SECURITY DEFINER SET search_path = audit, public, pg_catalog
AS $$
DECLARE
    v_tenant_id uuid;
    v_before jsonb; v_after jsonb;
BEGIN
    IF TG_OP = 'INSERT' THEN
        v_after := to_jsonb(NEW);
        v_tenant_id := NULLIF(v_after->>'tenant_id', '')::uuid;
    ELSIF TG_OP = 'UPDATE' THEN
        v_before := to_jsonb(OLD); v_after := to_jsonb(NEW);
        v_tenant_id := COALESCE(NULLIF(v_after->>'tenant_id',''), NULLIF(v_before->>'tenant_id',''))::uuid;
    ELSIF TG_OP = 'DELETE' THEN
        v_before := to_jsonb(OLD);
        v_tenant_id := NULLIF(v_before->>'tenant_id','')::uuid;
    END IF;

    INSERT INTO audit.audit_log (tenant_id, table_schema, table_name, operation, before_data, after_data)
    VALUES (v_tenant_id, TG_TABLE_SCHEMA, TG_TABLE_NAME, left(TG_OP, 1), v_before, v_after);

    RETURN COALESCE(NEW, OLD);
END$$;

-- Attach audit triggers
DO $$
DECLARE rec record;
BEGIN
    FOR rec IN
        SELECT * FROM (VALUES
            ('public','tenant'),('public','product'),('public','warehouse'),
            ('public','uom'),('public','lot'),('public','opening_balance'),
            ('public','inv_transaction'),('public','stock_balance'),
            ('public','sfdc_order'),('public','sfdc_order_line')
        ) AS v(schema_name, table_name)
    LOOP
        EXECUTE format($f$
            CREATE TRIGGER trg_audit_%I
            AFTER INSERT OR UPDATE OR DELETE ON %I.%I
            FOR EACH ROW EXECUTE FUNCTION audit.f_audit_capture();
        $f$, rec.table_name, rec.schema_name, rec.table_name);
    END LOOP;
END$$;

-- ============================================================================
-- f_stock_balance_apply — inv_transaction INSERT → stock_balance upsert
-- ============================================================================
CREATE OR REPLACE FUNCTION f_stock_balance_apply()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO stock_balance (
        tenant_id, tenant_code, product_id, product_code,
        warehouse_id, warehouse_code, lot_id, lot_code,
        on_hand_qty, uom_id, uom_code, last_updated_at
    ) VALUES (
        NEW.tenant_id, NEW.tenant_code, NEW.product_id, NEW.product_code,
        NEW.warehouse_id, NEW.warehouse_code,
        COALESCE(NEW.lot_id, 0), NEW.lot_code,
        NEW.signed_qty, NEW.uom_id, NEW.uom_code, now()
    )
    ON CONFLICT (tenant_id, product_id, warehouse_id, lot_id) DO UPDATE
        SET on_hand_qty = stock_balance.on_hand_qty + EXCLUDED.on_hand_qty,
            last_updated_at = now();
    RETURN NEW;
END$$;

CREATE TRIGGER trg_inv_txn_stock_balance
AFTER INSERT ON inv_transaction
FOR EACH ROW EXECUTE FUNCTION f_stock_balance_apply();

-- ============================================================================
-- f_stock_balance_opening_apply — opening_balance changes → stock_balance
-- ============================================================================
CREATE OR REPLACE FUNCTION f_stock_balance_opening_apply()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE v_delta numeric;
BEGIN
    IF TG_OP = 'INSERT' THEN
        v_delta := NEW.qty;
        INSERT INTO stock_balance (
            tenant_id, tenant_code, product_id, product_code,
            warehouse_id, warehouse_code, lot_id, lot_code,
            on_hand_qty, uom_id, uom_code, last_updated_at
        ) VALUES (
            NEW.tenant_id, NEW.tenant_code, NEW.product_id, NEW.product_code,
            NEW.warehouse_id, NEW.warehouse_code,
            COALESCE(NEW.lot_id, 0), NEW.lot_code,
            v_delta, NEW.uom_id, NEW.uom_code, now()
        )
        ON CONFLICT (tenant_id, product_id, warehouse_id, lot_id) DO UPDATE
            SET on_hand_qty = stock_balance.on_hand_qty + EXCLUDED.on_hand_qty,
                last_updated_at = now();
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        v_delta := NEW.qty - OLD.qty;
        IF v_delta <> 0 THEN
            UPDATE stock_balance SET on_hand_qty = on_hand_qty + v_delta, last_updated_at = now()
             WHERE tenant_id = NEW.tenant_id AND product_id = NEW.product_id
               AND warehouse_id = NEW.warehouse_id AND lot_id = COALESCE(NEW.lot_id, 0);
        END IF;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE stock_balance SET on_hand_qty = on_hand_qty - OLD.qty, last_updated_at = now()
         WHERE tenant_id = OLD.tenant_id AND product_id = OLD.product_id
           AND warehouse_id = OLD.warehouse_id AND lot_id = COALESCE(OLD.lot_id, 0);
        RETURN OLD;
    END IF;
    RETURN NULL;
END$$;

CREATE TRIGGER trg_opening_balance_stock_balance
AFTER INSERT OR UPDATE OR DELETE ON opening_balance
FOR EACH ROW EXECUTE FUNCTION f_stock_balance_opening_apply();

-- ============================================================================
-- f_mv_atp_mark_dirty — marks tenant for MV refresh
-- ============================================================================
CREATE OR REPLACE FUNCTION f_mv_atp_mark_dirty()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE v_tenant_id uuid;
BEGIN
    v_tenant_id := COALESCE(NEW.tenant_id, OLD.tenant_id);
    INSERT INTO mv_atp_dirty (tenant_id) VALUES (v_tenant_id)
    ON CONFLICT (tenant_id) DO UPDATE SET marked_at = now();
    RETURN COALESCE(NEW, OLD);
END$$;

CREATE TRIGGER trg_inv_txn_mv_dirty
AFTER INSERT OR UPDATE OR DELETE ON inv_transaction
FOR EACH ROW EXECUTE FUNCTION f_mv_atp_mark_dirty();

CREATE TRIGGER trg_sfdc_order_line_mv_dirty
AFTER INSERT OR UPDATE OR DELETE ON sfdc_order_line
FOR EACH ROW EXECUTE FUNCTION f_mv_atp_mark_dirty();

CREATE TRIGGER trg_opening_balance_mv_dirty
AFTER INSERT OR UPDATE OR DELETE ON opening_balance
FOR EACH ROW EXECUTE FUNCTION f_mv_atp_mark_dirty();

-- ============================================================================
-- f_inv_transaction_tenant_check — cross-tenant integrity
-- ============================================================================
CREATE OR REPLACE FUNCTION f_inv_transaction_tenant_check()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE v_bad text;
BEGIN
    SELECT 'product mismatch (' || tenant_id || ')' FROM product
     WHERE product_id = NEW.product_id AND tenant_id <> NEW.tenant_id INTO v_bad;
    IF v_bad IS NOT NULL THEN
        RAISE EXCEPTION 'inv_transaction tenant_id mismatch: %', v_bad USING ERRCODE = 'integrity_constraint_violation';
    END IF;

    SELECT 'warehouse mismatch (' || tenant_id || ')' FROM warehouse
     WHERE warehouse_id = NEW.warehouse_id AND tenant_id <> NEW.tenant_id INTO v_bad;
    IF v_bad IS NOT NULL THEN
        RAISE EXCEPTION 'inv_transaction tenant_id mismatch: %', v_bad USING ERRCODE = 'integrity_constraint_violation';
    END IF;

    SELECT 'uom mismatch (' || tenant_id || ')' FROM uom
     WHERE uom_id = NEW.uom_id AND tenant_id <> NEW.tenant_id INTO v_bad;
    IF v_bad IS NOT NULL THEN
        RAISE EXCEPTION 'inv_transaction tenant_id mismatch: %', v_bad USING ERRCODE = 'integrity_constraint_violation';
    END IF;

    IF NEW.lot_id IS NOT NULL THEN
        SELECT 'lot mismatch (' || tenant_id || ')' FROM lot
         WHERE lot_id = NEW.lot_id AND tenant_id <> NEW.tenant_id INTO v_bad;
        IF v_bad IS NOT NULL THEN
            RAISE EXCEPTION 'inv_transaction tenant_id mismatch: %', v_bad USING ERRCODE = 'integrity_constraint_violation';
        END IF;
    END IF;

    RETURN NEW;
END$$;

CREATE TRIGGER trg_inv_txn_tenant_check
BEFORE INSERT OR UPDATE ON inv_transaction
FOR EACH ROW EXECUTE FUNCTION f_inv_transaction_tenant_check();

-- ============================================================================
-- f_reconcile_orders — match shipments/issues to open order lines
-- ============================================================================
CREATE OR REPLACE FUNCTION f_reconcile_orders()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE v_matched_id text; v_matched_no int;
BEGIN
    IF NEW.signed_qty >= 0 THEN RETURN NEW; END IF;
    IF NEW.txn_type NOT IN ('issue', 'shipment') THEN RETURN NEW; END IF;

    SELECT sfdc_order_id, line_no INTO v_matched_id, v_matched_no
      FROM sfdc_order_line
     WHERE tenant_id = NEW.tenant_id AND product_id = NEW.product_id
       AND warehouse_id = NEW.warehouse_id
       AND line_state IN ('open', 'synced') AND fop_synced_at IS NULL
       AND qty <= ABS(NEW.signed_qty)
     ORDER BY created_at LIMIT 1 FOR UPDATE SKIP LOCKED;

    IF v_matched_id IS NOT NULL THEN
        UPDATE sfdc_order_line
           SET fop_synced_at = now(), line_state = 'synced', updated_at = now()
         WHERE sfdc_order_id = v_matched_id AND line_no = v_matched_no;
    END IF;
    RETURN NEW;
END$$;

CREATE TRIGGER trg_inv_txn_reconcile_orders
AFTER INSERT ON inv_transaction
FOR EACH ROW EXECUTE FUNCTION f_reconcile_orders();

-- ============================================================================
-- refresh_mv_atp
-- ============================================================================
CREATE OR REPLACE FUNCTION refresh_mv_atp()
RETURNS void LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM mv_atp_dirty) THEN RETURN; END IF;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_atp;
    DELETE FROM mv_atp_dirty;
END$$;

-- ============================================================================
-- calculate_inventory — on-the-fly recompute
-- ============================================================================
CREATE OR REPLACE FUNCTION calculate_inventory(
    p_tenant_id uuid, p_product_id bigint, p_warehouse_id bigint
)
RETURNS TABLE (
    on_hand_qty numeric, reserved_qty numeric, allocated_qty numeric,
    pending_qty numeric, atp_qty numeric, computed_at timestamptz
) LANGUAGE sql AS $$
    WITH
    o AS (SELECT COALESCE(SUM(qty), 0) AS v FROM opening_balance
          WHERE tenant_id = p_tenant_id AND product_id = p_product_id AND warehouse_id = p_warehouse_id),
    t AS (SELECT COALESCE(SUM(signed_qty), 0) AS v FROM inv_transaction
          WHERE tenant_id = p_tenant_id AND product_id = p_product_id AND warehouse_id = p_warehouse_id),
    r AS (SELECT COALESCE(SUM(qty), 0) AS v FROM sfdc_order_line
          WHERE tenant_id = p_tenant_id AND product_id = p_product_id AND warehouse_id = p_warehouse_id AND line_state = 'open'),
    a AS (SELECT COALESCE(SUM(qty), 0) AS v FROM sfdc_order_line
          WHERE tenant_id = p_tenant_id AND product_id = p_product_id AND warehouse_id = p_warehouse_id AND line_state = 'synced'),
    p AS (SELECT COALESCE(SUM(qty), 0) AS v FROM sfdc_order_line
          WHERE tenant_id = p_tenant_id AND product_id = p_product_id AND warehouse_id = p_warehouse_id
            AND line_state IN ('open', 'synced') AND fop_synced_at IS NULL)
    SELECT o.v + t.v, r.v, a.v, p.v, o.v + t.v - p.v, now()
    FROM o, t, r, a, p;
$$;

-- ============================================================================
-- fetch_inventory — MV-backed read
-- ============================================================================
CREATE OR REPLACE FUNCTION fetch_inventory(
    p_tenant_id uuid, p_product_id bigint DEFAULT NULL, p_warehouse_id bigint DEFAULT NULL
)
RETURNS TABLE (
    tenant_id uuid, product_id bigint, warehouse_id bigint,
    on_hand_qty numeric, reserved_qty numeric, allocated_qty numeric,
    pending_qty numeric, atp_qty numeric, computed_at timestamptz
) LANGUAGE sql AS $$
    SELECT * FROM mv_atp
     WHERE tenant_id = p_tenant_id
       AND (p_product_id   IS NULL OR product_id   = p_product_id)
       AND (p_warehouse_id IS NULL OR warehouse_id = p_warehouse_id);
$$;

-- ============================================================================
-- fetch_pending_orders
-- ============================================================================
CREATE OR REPLACE FUNCTION fetch_pending_orders(p_tenant_id uuid)
RETURNS TABLE (
    sfdc_order_id text, line_no int,
    product_id bigint, product_code text,
    warehouse_id bigint, warehouse_code text,
    qty numeric, uom_code text,
    line_state text, created_at timestamptz
) LANGUAGE sql AS $$
    SELECT sfdc_order_id, line_no, product_id, product_code,
           warehouse_id, warehouse_code, qty, uom_code, line_state, created_at
      FROM sfdc_order_line
     WHERE tenant_id = p_tenant_id
       AND line_state IN ('open', 'synced') AND fop_synced_at IS NULL
     ORDER BY created_at;
$$;

-- ============================================================================
-- post_transaction — auto-creates masters, populates code columns
-- ============================================================================
CREATE OR REPLACE FUNCTION post_transaction(p_payload jsonb)
RETURNS TABLE (inv_transaction_id bigint, accepted boolean, reason text)
LANGUAGE plpgsql AS $f$
#variable_conflict use_column
DECLARE
    v_tenant_id uuid;       v_tenant_code    text;
    v_product_id bigint;    v_product_code   text;
    v_warehouse_id bigint;  v_warehouse_code text;
    v_lot_id bigint;        v_lot_code       text;
    v_uom_id bigint;        v_uom_code       text;
    v_external text;        v_qty numeric;
    v_txn_type text;        v_posted_at timestamptz;
    v_source text;          v_transfer_pair uuid;
    v_id bigint;            v_on_hand numeric;
    v_size_cap int := pipeline_config_int('payload_max_bytes');
BEGIN
    IF v_size_cap IS NOT NULL AND pg_column_size(p_payload) > v_size_cap THEN
        RETURN QUERY SELECT NULL::bigint, false, 'payload_too_large'::text; RETURN;
    END IF;

    v_tenant_code    := p_payload->>'tenant_code';
    v_product_code   := p_payload->>'product_code';
    v_warehouse_code := p_payload->>'warehouse_code';
    v_uom_code       := p_payload->>'uom_code';
    v_lot_code       := p_payload->>'lot_code';
    v_external       := p_payload->>'external_txn_id';
    v_qty            := (p_payload->>'signed_qty')::numeric;
    v_txn_type       := p_payload->>'txn_type';
    v_posted_at      := COALESCE((p_payload->>'posted_at')::timestamptz, now());
    v_source         := COALESCE(p_payload->>'source_system', 'unknown');
    v_transfer_pair  := NULLIF(p_payload->>'transfer_pair_id', '')::uuid;

    SELECT t.tenant_id INTO v_tenant_id FROM tenant t WHERE t.tenant_code = v_tenant_code;
    IF v_tenant_id IS NULL THEN
        PERFORM notify_outbox(NULL, 'info', 'post_transaction.lookup',
            'unknown_tenant: ' || COALESCE(v_tenant_code, '<null>'),
            jsonb_build_object('hint', 'tenant_code not found', 'tenant_code', v_tenant_code),
            'unknown_tenant:' || COALESCE(v_tenant_code, '<null>'));
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_reference'::text; RETURN;
    END IF;

    INSERT INTO product (tenant_id, product_code, name) VALUES (v_tenant_id, v_product_code, v_product_code)
    ON CONFLICT (tenant_id, product_code) DO NOTHING;
    SELECT pr.product_id INTO v_product_id FROM product pr WHERE pr.tenant_id = v_tenant_id AND pr.product_code = v_product_code;

    INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES (v_tenant_id, v_warehouse_code, v_warehouse_code)
    ON CONFLICT (tenant_id, warehouse_code) DO NOTHING;
    SELECT wh.warehouse_id INTO v_warehouse_id FROM warehouse wh WHERE wh.tenant_id = v_tenant_id AND wh.warehouse_code = v_warehouse_code;

    INSERT INTO uom (tenant_id, uom_code, name) VALUES (v_tenant_id, v_uom_code, v_uom_code)
    ON CONFLICT (tenant_id, uom_code) DO NOTHING;
    SELECT um.uom_id INTO v_uom_id FROM uom um WHERE um.tenant_id = v_tenant_id AND um.uom_code = v_uom_code;

    IF v_lot_code IS NOT NULL THEN
        INSERT INTO lot (tenant_id, product_id, lot_code) VALUES (v_tenant_id, v_product_id, v_lot_code)
        ON CONFLICT (tenant_id, product_id, lot_code) DO NOTHING;
        SELECT lt.lot_id INTO v_lot_id FROM lot lt WHERE lt.tenant_id = v_tenant_id AND lt.product_id = v_product_id AND lt.lot_code = v_lot_code;
    END IF;

    IF v_product_id IS NULL OR v_warehouse_id IS NULL OR v_uom_id IS NULL THEN
        RETURN QUERY SELECT NULL::bigint, false, 'unknown_reference'::text; RETURN;
    END IF;
    IF v_qty IS NULL OR v_qty = 0 THEN
        RETURN QUERY SELECT NULL::bigint, false, 'invalid_qty'::text; RETURN;
    END IF;

    IF NOT (
        (v_txn_type = 'receipt'             AND v_qty > 0) OR
        (v_txn_type IN ('issue','shipment') AND v_qty < 0) OR
        (v_txn_type = 'transfer_out'        AND v_qty < 0) OR
        (v_txn_type = 'transfer_in'         AND v_qty > 0) OR
        (v_txn_type = 'adjustment'          AND v_qty <> 0) OR
        (v_txn_type = 'sales_return'        AND v_qty > 0) OR
        (v_txn_type = 'purchase_return'     AND v_qty < 0) OR
        (v_txn_type = 'scrap'               AND v_qty < 0)
    ) THEN
        RETURN QUERY SELECT NULL::bigint, false, 'invalid_sign_for_type'::text; RETURN;
    END IF;

    BEGIN
        INSERT INTO inv_transaction AS it (
            tenant_id, tenant_code, external_txn_id,
            product_id, product_code, warehouse_id, warehouse_code,
            lot_id, lot_code, signed_qty, uom_id, uom_code,
            txn_type, transfer_pair_id, source_system, posted_at, payload
        ) VALUES (
            v_tenant_id, v_tenant_code, v_external,
            v_product_id, v_product_code, v_warehouse_id, v_warehouse_code,
            v_lot_id, v_lot_code, v_qty, v_uom_id, v_uom_code,
            v_txn_type, v_transfer_pair, v_source, v_posted_at, p_payload
        ) RETURNING it.inv_transaction_id INTO v_id;
    EXCEPTION
        WHEN unique_violation THEN
            RETURN QUERY SELECT NULL::bigint, false, 'duplicate'::text; RETURN;
        WHEN check_violation THEN
            RETURN QUERY SELECT NULL::bigint, false, 'check_violation'::text; RETURN;
    END;

    IF v_qty < 0 AND v_txn_type IN ('issue','shipment') THEN
        SELECT on_hand_qty INTO v_on_hand FROM calculate_inventory(v_tenant_id, v_product_id, v_warehouse_id);
        IF v_on_hand < 0 THEN
            PERFORM notify_outbox(v_tenant_id, 'info', 'post_transaction',
                format('oversell: on_hand=%s after txn %s', v_on_hand, v_external),
                jsonb_build_object('external_txn_id', v_external, 'product_code', v_product_code,
                    'warehouse_code', v_warehouse_code, 'on_hand_after', v_on_hand, 'signed_qty', v_qty),
                format('oversell:%s:%s:%s', v_tenant_id, v_product_id, v_warehouse_id));
        END IF;
    END IF;

    RETURN QUERY SELECT v_id, true, 'ok'::text;
END$f$;

-- ============================================================================
-- post_transaction_bulk
-- ============================================================================
CREATE OR REPLACE FUNCTION post_transaction_bulk(p_payloads jsonb[])
RETURNS TABLE (idx int, inv_transaction_id bigint, accepted boolean, reason text)
LANGUAGE plpgsql AS $$
DECLARE
    v_cap int := pipeline_config_int('bulk_max_size');
    v_len int := COALESCE(array_length(p_payloads, 1), 0);
    i int; r record;
BEGIN
    IF v_cap IS NOT NULL AND v_len > v_cap THEN
        idx := 0; inv_transaction_id := NULL; accepted := false;
        reason := format('bulk_size_exceeded: %s > %s', v_len, v_cap);
        RETURN NEXT; RETURN;
    END IF;
    FOR i IN 1 .. v_len LOOP
        FOR r IN SELECT * FROM post_transaction(p_payloads[i]) LOOP
            idx := i; inv_transaction_id := r.inv_transaction_id;
            accepted := r.accepted; reason := r.reason;
            RETURN NEXT;
        END LOOP;
    END LOOP;
END$$;

-- ============================================================================
-- load_opening_balance — auto-creates masters, populates code columns
-- ============================================================================
CREATE OR REPLACE FUNCTION load_opening_balance(p_batch_id bigint)
RETURNS TABLE (accepted_count int, rejected_count int)
LANGUAGE plpgsql AS $f$
DECLARE
    v_status text; v_accepted int := 0; v_rejected int := 0;
    v_tenant_id uuid; v_row record;
BEGIN
    SELECT status INTO v_status FROM staging.ob_load_batch WHERE batch_id = p_batch_id FOR UPDATE;
    IF v_status IS NULL THEN RAISE EXCEPTION 'batch % not found', p_batch_id; END IF;
    IF v_status = 'loaded' THEN
        SELECT ob.accepted_count, ob.rejected_count INTO v_accepted, v_rejected
          FROM staging.ob_load_batch ob WHERE ob.batch_id = p_batch_id;
        RETURN QUERY SELECT v_accepted, v_rejected; RETURN;
    END IF;

    FOR v_row IN
        SELECT DISTINCT l.tenant_code, l.product_code, l.warehouse_code, l.uom_code, l.lot_code
          FROM staging.ob_load l WHERE l.batch_id = p_batch_id AND l.tenant_code IS NOT NULL
    LOOP
        SELECT t.tenant_id INTO v_tenant_id FROM tenant t WHERE t.tenant_code = v_row.tenant_code;
        IF v_tenant_id IS NULL THEN CONTINUE; END IF;

        IF v_row.product_code IS NOT NULL THEN
            INSERT INTO product (tenant_id, product_code, name) VALUES (v_tenant_id, v_row.product_code, v_row.product_code)
            ON CONFLICT (tenant_id, product_code) DO NOTHING;
        END IF;
        IF v_row.warehouse_code IS NOT NULL THEN
            INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES (v_tenant_id, v_row.warehouse_code, v_row.warehouse_code)
            ON CONFLICT (tenant_id, warehouse_code) DO NOTHING;
        END IF;
        IF v_row.uom_code IS NOT NULL THEN
            INSERT INTO uom (tenant_id, uom_code, name) VALUES (v_tenant_id, v_row.uom_code, v_row.uom_code)
            ON CONFLICT (tenant_id, uom_code) DO NOTHING;
        END IF;
        IF v_row.lot_code IS NOT NULL AND v_row.product_code IS NOT NULL THEN
            INSERT INTO lot (tenant_id, product_id, lot_code)
            VALUES (v_tenant_id,
                    (SELECT product_id FROM product WHERE tenant_id = v_tenant_id AND product_code = v_row.product_code),
                    v_row.lot_code)
            ON CONFLICT (tenant_id, product_id, lot_code) DO NOTHING;
        END IF;
    END LOOP;

    WITH resolved AS (
        SELECT l.ob_load_id, l.line_no,
            t.tenant_id, t.tenant_code, p.product_id, l.product_code,
            w.warehouse_id, l.warehouse_code, lt.lot_id, l.lot_code,
            u.uom_id, l.uom_code, l.qty AS qty_text, l.as_of_date AS as_of_text,
            CASE
              WHEN t.tenant_id    IS NULL THEN 'unknown_tenant'
              WHEN p.product_id   IS NULL THEN 'unknown_product'
              WHEN w.warehouse_id IS NULL THEN 'unknown_warehouse'
              WHEN u.uom_id       IS NULL THEN 'unknown_uom'
              WHEN l.lot_code IS NOT NULL AND lt.lot_id IS NULL THEN 'unknown_lot'
              WHEN l.qty IS NULL OR l.qty !~ '^-?[0-9]+(\.[0-9]+)?$' THEN 'invalid_qty'
              WHEN l.as_of_date IS NULL THEN 'invalid_date'
              ELSE NULL
            END AS reason_code
        FROM staging.ob_load l
        LEFT JOIN tenant    t  ON t.tenant_code    = l.tenant_code
        LEFT JOIN product   p  ON p.product_code   = l.product_code  AND p.tenant_id = t.tenant_id
        LEFT JOIN warehouse w  ON w.warehouse_code = l.warehouse_code AND w.tenant_id = t.tenant_id
        LEFT JOIN uom       u  ON u.uom_code       = l.uom_code      AND u.tenant_id = t.tenant_id
        LEFT JOIN lot       lt ON lt.lot_code      = l.lot_code       AND lt.tenant_id = t.tenant_id AND lt.product_id = p.product_id
        WHERE l.batch_id = p_batch_id
    ),
    inserted AS (
        INSERT INTO opening_balance (
            tenant_id, tenant_code, product_id, product_code,
            warehouse_id, warehouse_code, lot_id, lot_code,
            qty, uom_id, uom_code, as_of_date, batch_id
        )
        SELECT tenant_id, tenant_code, product_id, product_code,
               warehouse_id, warehouse_code, lot_id, lot_code,
               qty_text::numeric, uom_id, uom_code, as_of_text::date, p_batch_id
        FROM resolved WHERE reason_code IS NULL
        ON CONFLICT DO NOTHING RETURNING 1
    ),
    rejected AS (
        INSERT INTO staging.ob_reject (batch_id, ob_load_id, reason_code, reason_detail, raw_line)
        SELECT p_batch_id, ob_load_id, reason_code,
            format('line=%s tenant=%s product=%s warehouse=%s uom=%s',
                   line_no, tenant_code, product_code, warehouse_code, uom_code),
            jsonb_build_object('tenant_code', tenant_code, 'product_code', product_code,
              'warehouse_code', warehouse_code, 'lot_code', lot_code,
              'uom_code', uom_code, 'qty', qty_text, 'as_of_date', as_of_text)
        FROM resolved WHERE reason_code IS NOT NULL RETURNING 1
    )
    SELECT (SELECT count(*) FROM inserted)::int, (SELECT count(*) FROM rejected)::int
      INTO v_accepted, v_rejected;

    UPDATE staging.ob_load_batch
       SET status = 'loaded', row_count = (SELECT count(*) FROM staging.ob_load WHERE batch_id = p_batch_id),
           accepted_count = v_accepted, rejected_count = v_rejected, completed_at = now()
     WHERE batch_id = p_batch_id;

    DELETE FROM staging.ob_load WHERE batch_id = p_batch_id;

    IF v_rejected > 0 THEN
        PERFORM notify_outbox(NULL, 'warn', 'load_opening_balance',
            format('batch %s loaded with %s rejects', p_batch_id, v_rejected),
            jsonb_build_object('batch_id', p_batch_id, 'rejected_count', v_rejected),
            'load_opening_balance:batch:' || p_batch_id);
    END IF;

    RETURN QUERY SELECT v_accepted, v_rejected;
END$f$;

-- ============================================================================
-- upsert_order — auto-creates masters, populates code columns
-- ============================================================================
CREATE OR REPLACE FUNCTION upsert_order(p_payload jsonb)
RETURNS TABLE (
    sfdc_order_id text, lines_inserted int, lines_updated int,
    lines_cancelled int, accepted boolean, reason text
) LANGUAGE plpgsql AS $$
#variable_conflict use_column
DECLARE
    v_tenant_id uuid;       v_tenant_code text;
    v_order_id text;        v_order_state text;
    v_inserted int := 0;    v_updated int := 0;    v_cancelled int := 0;
    v_line jsonb;           v_lines jsonb;
    v_product_id bigint;    v_product_code text;
    v_warehouse_id bigint;  v_warehouse_code text;
    v_uom_id bigint;        v_uom_code text;
    v_payload_line_nos int[];
    v_existed boolean;      v_existing_state text;
    v_requested_state text; v_effective_state text;
    v_existing_order_state text;
BEGIN
    v_order_id    := p_payload->>'sfdc_order_id';
    v_lines       := COALESCE(p_payload->'lines', '[]'::jsonb);
    v_order_state := COALESCE(p_payload->>'order_state', 'open');
    v_tenant_code := p_payload->>'tenant_code';

    SELECT t.tenant_id INTO v_tenant_id FROM tenant t WHERE t.tenant_code = v_tenant_code;
    IF v_tenant_id IS NULL THEN
        RETURN QUERY SELECT v_order_id, 0, 0, 0, false, 'unknown_reference'::text; RETURN;
    END IF;

    SELECT order_state INTO v_existing_order_state FROM sfdc_order WHERE sfdc_order_id = v_order_id;

    IF v_existing_order_state IS NOT NULL AND v_existing_order_state <> v_order_state
       AND NOT (
           (v_existing_order_state = 'open'      AND v_order_state IN ('synced','cancelled'))  OR
           (v_existing_order_state = 'synced'    AND v_order_state IN ('closed','cancelled'))  OR
           (v_existing_order_state = 'closed'    AND v_order_state = 'cancelled')              OR
           (v_existing_order_state = 'cancelled' AND v_order_state = 'cancelled')
       )
    THEN
        RETURN QUERY SELECT v_order_id, 0, 0, 0, false,
            format('illegal_order_state_transition: %s -> %s', v_existing_order_state, v_order_state)::text;
        RETURN;
    END IF;

    INSERT INTO sfdc_order (sfdc_order_id, tenant_id, tenant_code, customer_id, order_state, payload)
    VALUES (v_order_id, v_tenant_id, v_tenant_code, p_payload->>'customer_id', v_order_state, p_payload)
    ON CONFLICT (sfdc_order_id) DO UPDATE
       SET customer_id = EXCLUDED.customer_id, order_state = EXCLUDED.order_state,
           payload = EXCLUDED.payload, updated_at = now();

    v_payload_line_nos := ARRAY(SELECT (jsonb_array_elements(v_lines)->>'line_no')::int);

    FOR v_line IN SELECT jsonb_array_elements(v_lines) LOOP
        v_product_code   := v_line->>'product_code';
        v_warehouse_code := v_line->>'warehouse_code';
        v_uom_code       := v_line->>'uom_code';

        INSERT INTO product (tenant_id, product_code, name) VALUES (v_tenant_id, v_product_code, v_product_code)
        ON CONFLICT (tenant_id, product_code) DO NOTHING;
        SELECT pr.product_id INTO v_product_id FROM product pr WHERE pr.tenant_id = v_tenant_id AND pr.product_code = v_product_code;

        INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES (v_tenant_id, v_warehouse_code, v_warehouse_code)
        ON CONFLICT (tenant_id, warehouse_code) DO NOTHING;
        SELECT wh.warehouse_id INTO v_warehouse_id FROM warehouse wh WHERE wh.tenant_id = v_tenant_id AND wh.warehouse_code = v_warehouse_code;

        INSERT INTO uom (tenant_id, uom_code, name) VALUES (v_tenant_id, v_uom_code, v_uom_code)
        ON CONFLICT (tenant_id, uom_code) DO NOTHING;
        SELECT um.uom_id INTO v_uom_id FROM uom um WHERE um.tenant_id = v_tenant_id AND um.uom_code = v_uom_code;

        SELECT line_state, true INTO v_existing_state, v_existed
          FROM sfdc_order_line sol WHERE sol.sfdc_order_id = v_order_id AND sol.line_no = (v_line->>'line_no')::int;
        v_existed := COALESCE(v_existed, false);

        v_requested_state := COALESCE(v_line->>'line_state', 'open');

        IF NOT v_existed THEN
            v_effective_state := 'open';
            IF v_requested_state <> 'open' THEN
                PERFORM notify_outbox(v_tenant_id, 'info', 'upsert_order.line_state',
                    format('new line forced to open (caller requested %s)', v_requested_state),
                    jsonb_build_object('sfdc_order_id', v_order_id, 'line_no', v_line->>'line_no',
                        'requested_state', v_requested_state),
                    'upsert_order.line_state:' || v_order_id || ':' || (v_line->>'line_no'));
            END IF;
        ELSE
            v_effective_state := v_requested_state;
            IF v_existing_state <> v_effective_state
               AND NOT (
                   (v_existing_state = 'open'      AND v_effective_state IN ('synced','cancelled'))  OR
                   (v_existing_state = 'synced'    AND v_effective_state IN ('closed','cancelled'))  OR
                   (v_existing_state = 'closed'    AND v_effective_state = 'cancelled')              OR
                   (v_existing_state = 'cancelled' AND v_effective_state = 'cancelled')
               )
            THEN
                RETURN QUERY SELECT v_order_id, 0, 0, 0, false,
                    format('illegal_line_state_transition: line %s %s -> %s',
                           v_line->>'line_no', v_existing_state, v_effective_state)::text;
                RETURN;
            END IF;
        END IF;

        INSERT INTO sfdc_order_line (
            sfdc_order_id, line_no, tenant_id, tenant_code,
            product_id, product_code, warehouse_id, warehouse_code,
            qty, uom_id, uom_code, line_state, payload
        ) VALUES (
            v_order_id, (v_line->>'line_no')::int, v_tenant_id, v_tenant_code,
            v_product_id, v_product_code, v_warehouse_id, v_warehouse_code,
            (v_line->>'qty')::numeric, v_uom_id, v_uom_code, v_effective_state, v_line
        )
        ON CONFLICT (sfdc_order_id, line_no) DO UPDATE
           SET product_id = EXCLUDED.product_id, product_code = EXCLUDED.product_code,
               warehouse_id = EXCLUDED.warehouse_id, warehouse_code = EXCLUDED.warehouse_code,
               qty = EXCLUDED.qty, uom_id = EXCLUDED.uom_id, uom_code = EXCLUDED.uom_code,
               line_state = EXCLUDED.line_state, payload = EXCLUDED.payload, updated_at = now();
        IF v_existed THEN v_updated := v_updated + 1; ELSE v_inserted := v_inserted + 1; END IF;
    END LOOP;

    UPDATE sfdc_order_line SET line_state = 'cancelled', updated_at = now()
     WHERE sfdc_order_line.sfdc_order_id = v_order_id
       AND sfdc_order_line.line_state <> 'cancelled'
       AND NOT (sfdc_order_line.line_no = ANY(v_payload_line_nos));
    GET DIAGNOSTICS v_cancelled = ROW_COUNT;

    RETURN QUERY SELECT v_order_id, v_inserted, v_updated, v_cancelled, true, 'ok'::text;
END$$;

-- ============================================================================
-- process_txn_inbox — staging drain
-- ============================================================================
CREATE OR REPLACE FUNCTION process_txn_inbox(p_batch_size int DEFAULT NULL)
RETURNS TABLE (processed int, rejected int)
LANGUAGE plpgsql SECURITY DEFINER SET search_path = public, staging, pg_temp
AS $$
DECLARE
    v_batch int := COALESCE(p_batch_size, pipeline_config_int('txn_inbox_batch_size'));
    v_row staging.txn_inbox%ROWTYPE;
    v_result record;
    v_processed int := 0; v_rejected int := 0;
BEGIN
    FOR v_row IN
        SELECT * FROM staging.txn_inbox WHERE status = 'pending'
         ORDER BY received_at LIMIT v_batch FOR UPDATE SKIP LOCKED
    LOOP
        BEGIN
            SELECT * INTO v_result FROM post_transaction(v_row.payload);
            IF v_result.accepted THEN
                UPDATE staging.txn_inbox SET status = 'processed', processed_at = now(), error_detail = NULL
                 WHERE txn_inbox_id = v_row.txn_inbox_id;
                v_processed := v_processed + 1;
            ELSE
                UPDATE staging.txn_inbox SET status = 'rejected', processed_at = now(), error_detail = v_result.reason
                 WHERE txn_inbox_id = v_row.txn_inbox_id;
                v_rejected := v_rejected + 1;
            END IF;
        EXCEPTION WHEN OTHERS THEN
            UPDATE staging.txn_inbox SET status = 'rejected', processed_at = now(), error_detail = 'exception: ' || SQLERRM
             WHERE txn_inbox_id = v_row.txn_inbox_id;
            v_rejected := v_rejected + 1;
        END;
    END LOOP;
    RETURN QUERY SELECT v_processed, v_rejected;
END$$;

-- ============================================================================
-- process_order_inbox — staging drain
-- ============================================================================
CREATE OR REPLACE FUNCTION process_order_inbox(p_batch_size int DEFAULT NULL)
RETURNS TABLE (processed int, rejected int)
LANGUAGE plpgsql SECURITY DEFINER SET search_path = public, staging, pg_temp
AS $$
DECLARE
    v_batch int := COALESCE(p_batch_size, pipeline_config_int('order_inbox_batch_size'));
    v_row staging.order_inbox%ROWTYPE;
    v_result record;
    v_processed int := 0; v_rejected int := 0;
BEGIN
    FOR v_row IN
        SELECT * FROM staging.order_inbox WHERE status = 'pending'
         ORDER BY received_at LIMIT v_batch FOR UPDATE SKIP LOCKED
    LOOP
        BEGIN
            SELECT * INTO v_result FROM upsert_order(v_row.payload);
            IF v_result.accepted THEN
                UPDATE staging.order_inbox SET status = 'processed', processed_at = now(), error_detail = NULL
                 WHERE order_inbox_id = v_row.order_inbox_id;
                v_processed := v_processed + 1;
            ELSE
                UPDATE staging.order_inbox SET status = 'rejected', processed_at = now(), error_detail = v_result.reason
                 WHERE order_inbox_id = v_row.order_inbox_id;
                v_rejected := v_rejected + 1;
            END IF;
        EXCEPTION WHEN OTHERS THEN
            UPDATE staging.order_inbox SET status = 'rejected', processed_at = now(), error_detail = 'exception: ' || SQLERRM
             WHERE order_inbox_id = v_row.order_inbox_id;
            v_rejected := v_rejected + 1;
        END;
    END LOOP;
    RETURN QUERY SELECT v_processed, v_rejected;
END$$;

COMMIT;
