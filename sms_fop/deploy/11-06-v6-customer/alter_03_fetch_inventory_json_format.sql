-- ============================================================================
-- alter_03_fetch_inventory_json_format.sql
-- ============================================================================
-- Refines processed.fetch_inventory_json from alter_02:
--   * Quantities returned as INTEGER (no .0000 trailing zeros)
--   * JSON key order preserved as: tenant_code → warehouse_code → subinventory
--                                   → product_code → stock_status → on_hand_qty
--                                   → reserved_qty → atp_qty → uom_code
--                                   → last_updated_at
--   * Return type switched from SETOF JSONB to SETOF JSON.
--     Reason: JSONB normalises keys to alphabetical; JSON preserves insertion
--     order. Consumers that need JSONB can cast on receipt: rec::jsonb.
--
-- Parameters and filter semantics unchanged from alter_02.
--
-- Idempotent (CREATE OR REPLACE) — re-running is a no-op.
-- ============================================================================

BEGIN;

-- Drop required: alter_02 returned SETOF JSONB, this version returns SETOF JSON.
-- Postgres rejects return-type changes via CREATE OR REPLACE.
DROP FUNCTION IF EXISTS processed.fetch_inventory_json(TEXT, TEXT, TEXT, TEXT);

CREATE OR REPLACE FUNCTION processed.fetch_inventory_json(
    p_tenant_code     TEXT,
    p_warehouse_code  TEXT DEFAULT NULL,
    p_subinventory    TEXT DEFAULT NULL,
    p_product_code    TEXT DEFAULT NULL
)
RETURNS SETOF JSON
LANGUAGE sql
STABLE
AS $f$
    SELECT json_build_object(
        'tenant_code',     sb.tenant_code,
        'warehouse_code',  sb.warehouse_code,
        'subinventory',    sb.subinventory,
        'product_code',    sb.product_code,
        'stock_status',    sb.stock_status,
        'on_hand_qty',     sb.on_hand_qty::INTEGER,
        'reserved_qty',    sb.reserved_qty::INTEGER,
        'atp_qty',         (sb.on_hand_qty - sb.reserved_qty)::INTEGER,
        'uom_code',        sb.uom_code,
        'last_updated_at', sb.last_updated_at
    )
      FROM processed.stock_balance sb
     WHERE sb.tenant_code = p_tenant_code
       AND (p_warehouse_code IS NULL OR p_warehouse_code = 'all' OR sb.warehouse_code = p_warehouse_code)
       AND (p_subinventory   IS NULL OR p_subinventory   = 'all' OR sb.subinventory   = p_subinventory)
       AND (p_product_code   IS NULL OR p_product_code   = 'all' OR sb.product_code   = p_product_code)
     ORDER BY sb.tenant_code, sb.warehouse_code, sb.subinventory, sb.product_code;
$f$;

COMMIT;
