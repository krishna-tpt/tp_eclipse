-- ============================================================================
-- TenthPlanet Inventory Ledger — Customer Seed Data
-- ============================================================================
-- Runs AFTER customer_install.sql. Provisions the tenants the MicroService
-- expects to encounter in incoming files. Without these rows the loader will
-- accept files but every row gets rejected with reason='unknown_tenant'.
--
-- Add one row per real ERP source the MicroService will see.
-- The tenant_code MUST match the value in HEADER_FILE field 3 of the source
-- file (per the catalog YAML's matches.header_confirms.equals).
-- ============================================================================

BEGIN;

-- Michelin IFOP Europe — the source_system code in BATCH_408 files
INSERT INTO processed.tenant (tenant_code, name)
VALUES ('IFOPEUR', 'IFOP Europe (Michelin)')
ON CONFLICT (tenant_code) DO NOTHING;

-- Michelin MNA — the source_system code in DMC_408 files (when that variant
-- ships). Pre-provisioned so the day a DMC file arrives, the loader does not
-- reject every row.
INSERT INTO processed.tenant (tenant_code, name)
VALUES ('MNA', 'Michelin North America')
ON CONFLICT (tenant_code) DO NOTHING;

COMMIT;

-- ============================================================================
-- VERIFY
-- ============================================================================
-- SELECT tenant_code, name FROM processed.tenant ORDER BY tenant_code;
--    tenant_code |             name
--   -------------+------------------------------
--    IFOPEUR     | IFOP Europe (Michelin)
--    MNA         | Michelin North America
