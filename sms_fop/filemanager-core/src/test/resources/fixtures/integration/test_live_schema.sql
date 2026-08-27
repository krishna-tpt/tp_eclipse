-- Minimal live-table schema for CatalogIngestPipelineIT.
-- Subset of V1 + V13 that the load_stocklevel function depends on.
-- This is NOT a substitute for production migrations — it exists only so the
-- integration test can verify staging → live promotion against a stock
-- postgres:16-alpine image without pulling pg_partman / pg_cron / pgTAP.

-- Tenant: provisioned out-of-band (NOT auto-created by the loader)
CREATE TABLE IF NOT EXISTS tenant (
    tenant_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_code  TEXT NOT NULL UNIQUE,
    name         TEXT NOT NULL,
    is_active    BOOLEAN NOT NULL DEFAULT true,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS product (
    product_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id    UUID NOT NULL REFERENCES tenant(tenant_id),
    product_code TEXT NOT NULL,
    name         TEXT NOT NULL,
    is_active    BOOLEAN NOT NULL DEFAULT true,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, product_code)
);

CREATE TABLE IF NOT EXISTS warehouse (
    warehouse_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id      UUID NOT NULL REFERENCES tenant(tenant_id),
    warehouse_code TEXT NOT NULL,
    name           TEXT NOT NULL,
    is_active      BOOLEAN NOT NULL DEFAULT true,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, warehouse_code)
);

CREATE TABLE IF NOT EXISTS uom (
    uom_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id  UUID NOT NULL REFERENCES tenant(tenant_id),
    uom_code   TEXT NOT NULL,
    name       TEXT NOT NULL,
    is_active  BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, uom_code)
);

-- opening_balance — V1 shape + V13 flatten columns (carries natural-key codes
-- alongside surrogate IDs)
CREATE TABLE IF NOT EXISTS opening_balance (
    opening_balance_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id          UUID NOT NULL REFERENCES tenant(tenant_id),
    tenant_code        TEXT NOT NULL,
    product_id         BIGINT NOT NULL REFERENCES product(product_id),
    product_code       TEXT NOT NULL,
    warehouse_id       BIGINT NOT NULL REFERENCES warehouse(warehouse_id),
    warehouse_code     TEXT NOT NULL,
    lot_id             BIGINT,
    lot_code           TEXT,
    qty                NUMERIC(18, 4) NOT NULL,
    uom_id             BIGINT NOT NULL REFERENCES uom(uom_id),
    uom_code           TEXT NOT NULL,
    as_of_date         DATE NOT NULL,
    batch_id           BIGINT NOT NULL,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_opening_balance_key
    ON opening_balance (tenant_id, product_id, warehouse_id, COALESCE(lot_id, 0), as_of_date);

-- Pre-provision the IFOPEUR tenant (matches the header_confirms value in the
-- BATCH_408 catalog YAML).
INSERT INTO tenant (tenant_code, name) VALUES ('IFOPEUR', 'IFOP Europe')
    ON CONFLICT (tenant_code) DO NOTHING;
