-- ============================================================================
-- alter_02_fetch_inventory_json.sql
-- ============================================================================
-- Adds processed.fetch_inventory_json — a JSON-returning inventory query for
-- consumers (Java / REST / dashboard) that need one row of JSONB per
-- (product, warehouse, subinventory, stock_status) hit.
--
-- Parameters:
--   p_tenant_code     TEXT   — REQUIRED, the client / tenant ('IFOPEUR' etc.)
--   p_warehouse_code  TEXT   — NULL or 'all' for every warehouse
--   p_subinventory    TEXT   — NULL or 'all' for every locator
--   p_product_code    TEXT   — NULL or 'all' for every product
--
-- Returns: SETOF JSONB. Each row is one inventory record:
--   { tenant_code, product_code, warehouse_code, subinventory,
--     stock_status, on_hand_qty, reserved_qty, atp_qty,
--     uom_code, last_updated_at }
--
-- Idempotent (CREATE OR REPLACE). Re-running is a no-op.
-- ============================================================================

BEGIN;

CREATE OR REPLACE FUNCTION processed.fetch_inventory_json(
    p_tenant_code     TEXT,
    p_warehouse_code  TEXT DEFAULT NULL,
    p_subinventory    TEXT DEFAULT NULL,
    p_product_code    TEXT DEFAULT NULL
)
RETURNS SETOF JSONB
LANGUAGE sql
STABLE
AS $f$
    SELECT jsonb_build_object(
        'tenant_code',     sb.tenant_code,
        'product_code',    sb.product_code,
        'warehouse_code',  sb.warehouse_code,
        'subinventory',    sb.subinventory,
        'stock_status',    sb.stock_status,
        'on_hand_qty',     sb.on_hand_qty,
        'reserved_qty',    sb.reserved_qty,
        'atp_qty',         sb.on_hand_qty - sb.reserved_qty,
        'uom_code',        sb.uom_code,
        'last_updated_at', sb.last_updated_at
    )
      FROM processed.stock_balance sb
     WHERE sb.tenant_code = p_tenant_code
       AND (p_warehouse_code IS NULL OR p_warehouse_code = 'all' OR sb.warehouse_code = p_warehouse_code)
       AND (p_subinventory   IS NULL OR p_subinventory   = 'all' OR sb.subinventory   = p_subinventory)
       AND (p_product_code   IS NULL OR p_product_code   = 'all' OR sb.product_code   = p_product_code)
     ORDER BY sb.product_code, sb.warehouse_code, sb.subinventory, sb.stock_status;
$f$;

COMMIT;
