-- Test master data. Loaded by integration tests before any transaction-level test.
-- Two tenants so cross-tenant RLS tests have a counterparty.

INSERT INTO tenant (tenant_id, tenant_code, name) VALUES
    ('00000000-0000-0000-0000-000000000001', 'ACME', 'Acme NL'),
    ('00000000-0000-0000-0000-000000000002', 'BETA', 'Beta NL')
ON CONFLICT (tenant_code) DO NOTHING;

INSERT INTO product (tenant_id, product_code, name) VALUES
    ('00000000-0000-0000-0000-000000000001', 'P1001', 'Widget A'),
    ('00000000-0000-0000-0000-000000000001', 'P1002', 'Widget B'),
    ('00000000-0000-0000-0000-000000000001', 'P1003', 'Bulk Material'),
    ('00000000-0000-0000-0000-000000000002', 'P2001', 'Tenant B Widget')
ON CONFLICT (tenant_id, product_code) DO NOTHING;

INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES
    ('00000000-0000-0000-0000-000000000001', 'WH-NL-01', 'Amsterdam DC'),
    ('00000000-0000-0000-0000-000000000001', 'WH-NL-02', 'Rotterdam DC'),
    ('00000000-0000-0000-0000-000000000002', 'WH-B-01',  'Tenant B Warehouse')
ON CONFLICT (tenant_id, warehouse_code) DO NOTHING;

INSERT INTO uom (tenant_id, uom_code, name) VALUES
    ('00000000-0000-0000-0000-000000000001', 'EA', 'Each'),
    ('00000000-0000-0000-0000-000000000001', 'KG', 'Kilogram'),
    ('00000000-0000-0000-0000-000000000001', 'L',  'Litre'),
    ('00000000-0000-0000-0000-000000000002', 'EA', 'Each')
ON CONFLICT (tenant_id, uom_code) DO NOTHING;

-- Lots — keyed by product, only for products that track lot
INSERT INTO lot (tenant_id, product_id, lot_code)
    SELECT '00000000-0000-0000-0000-000000000001', product_id, l.lot_code
    FROM product p, (VALUES ('LOT-A'), ('LOT-B')) AS l(lot_code)
    WHERE p.product_code = 'P1002'
      AND p.tenant_id = '00000000-0000-0000-0000-000000000001'
ON CONFLICT (tenant_id, product_id, lot_code) DO NOTHING;
