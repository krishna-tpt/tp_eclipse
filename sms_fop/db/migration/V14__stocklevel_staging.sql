-- V14__stocklevel_staging.sql
-- Catalog-driven stocklevel staging for Pipeline 1 (Michelin MICH_INV_STOCKLEVEL).
--
-- Columns mirror the Extract Phase - Structure spec (CFO fields 1..54)
-- plus pipeline-managed bookkeeping (file_name, line_number, ingested_at)
-- plus envelope captures (tenant_code, file_batch_id) and a literal source tag.
--
-- One inbox table per record type per interface. BATCH_408 (this file) and
-- DMC_408 (later migration) share the same column set since both are
-- MICH_INV_STOCKLEVEL — DMC carries fewer fields and leaves the rest NULL.

CREATE SCHEMA IF NOT EXISTS staging;

CREATE TABLE IF NOT EXISTS staging.stocklevel_inbox (
    inbox_id            BIGSERIAL PRIMARY KEY,

    -- Pipeline bookkeeping
    file_name           TEXT        NOT NULL,
    line_number         BIGINT      NOT NULL,
    ingested_at         TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- Captured from HEADER_FILE envelope + literal
    tenant_code         TEXT,
    file_batch_id       TEXT,
    source_marker       TEXT,

    -- CFO data fields per spec (positions 1..54 → file fields 2..55)
    company_code                  TEXT,
    organization_code             TEXT,
    organization_type             TEXT,
    finished_good_org_type        TEXT,
    oc_companycode                TEXT,
    od_countrycode                TEXT,
    or_countrycode                TEXT,
    site                          TEXT,
    item_segment1                 TEXT      NOT NULL,
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
    primary_quantity              NUMERIC   NOT NULL,
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

CREATE INDEX IF NOT EXISTS ix_stocklevel_inbox_file       ON staging.stocklevel_inbox (file_name);
CREATE INDEX IF NOT EXISTS ix_stocklevel_inbox_item_wh    ON staging.stocklevel_inbox (item_segment1, warehouse);
CREATE INDEX IF NOT EXISTS ix_stocklevel_inbox_ingested   ON staging.stocklevel_inbox (ingested_at);
CREATE INDEX IF NOT EXISTS ix_stocklevel_inbox_status     ON staging.stocklevel_inbox (stock_status);

COMMENT ON TABLE  staging.stocklevel_inbox IS
  'Staging landing zone for MICH_INV_STOCKLEVEL feeds, populated by CatalogIngestPipeline. '
  'Catalog YAML at classpath:interfaces/mich_inv_stocklevel_batch.yaml owns the column-mapping spec.';
COMMENT ON COLUMN staging.stocklevel_inbox.item_segment1 IS
  'CFO ITEM_SEGMENT1 — the inventory item code (e.g. 459473_101).';
COMMENT ON COLUMN staging.stocklevel_inbox.primary_quantity IS
  'CFO PRIMARY_QUANTITY in the item primary UOM. Can be negative (WIP/INTRANSIT adjustments).';
COMMENT ON COLUMN staging.stocklevel_inbox.subinventory IS
  'Subinventory code (e.g. ONHAND, US01) — NOT the stock status; the actual status is in stock_status.';
COMMENT ON COLUMN staging.stocklevel_inbox.material_location IS
  'Either ONHAND or RECEIVING — determines whether receiving_location is populated.';
COMMENT ON COLUMN staging.stocklevel_inbox.stock_status IS
  'Supply Chain Stock Status from the subinventory DFF (e.g. LIBERATED).';
COMMENT ON COLUMN staging.stocklevel_inbox.business_line IS
  'Michelin business line (e.g. OHT = Off-Highway Tires).';
