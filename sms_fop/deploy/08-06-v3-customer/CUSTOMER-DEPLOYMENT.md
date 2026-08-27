# Customer Deployment — Tag `08-06-v3-customer`

Snapshot taken **2026-06-08**. Adds **subinventory + stock_status** as
first-class dimensions throughout the ledger, with a flexible
`fetch_inventory()` read API.

```bash
cd deploy/08-06-v3-customer/

psql -h <host> -U postgres -d <dbname> -f customer_install.sql
psql -h <host> -U postgres -d <dbname> -f customer_seed.sql
psql -h <host> -U postgres -d <dbname> -f customer_sample_data.sql   # optional
```

---

## What's in this folder

| File | Purpose |
|---|---|
| `customer_install.sql` | Schemas, tables, triggers, **9 functions** (subinventory-aware) |
| `customer_seed.sql` | `IFOPEUR` + `MNA` tenants |
| `customer_sample_data.sql` | 65 Michelin BATCH_408 rows + promotion |
| `CUSTOMER-DEPLOYMENT.md` | This runbook |

---

## What's new in v3

### Schema additions

| Where | Column | Type | Default |
|---|---|---|---|
| `processed.opening_balance` | `subinventory` | TEXT | `''` |
| `processed.opening_balance` | `stock_status` | TEXT | `'LIBERATED'` |
| `processed.stock_balance` | `subinventory` | TEXT (in **PK**) | `''` |
| `processed.stock_balance` | `stock_status` | TEXT (in **PK**) | `'LIBERATED'` |
| `processed.inv_transaction` | `subinventory` | TEXT (nullable) | NULL |
| `processed.inv_transaction` | `stock_status` | TEXT | `'LIBERATED'` |

`sfdc_order_line` is intentionally **unchanged** — orders pool at warehouse
level (decision B).

### Stock balance — physical row layout

For one (tenant, product, warehouse), the table now holds:

```
Real on-hand rows (one per subinventory + stock_status + lot):
  subinventory='ONHAND'   stock_status='LIBERATED'   on_hand=100   reserved=0
  subinventory='ONHAND'   stock_status='BLOCKED'     on_hand=20    reserved=0
  subinventory='US01'     stock_status='LIBERATED'   on_hand=50    reserved=0
  ...

POOL row (one per tenant+product+warehouse):
  subinventory='POOL'     stock_status='LIBERATED'   on_hand=0     reserved=5
```

The POOL row holds the warehouse-level reservation total. fetch_inventory
combines them correctly:
- Real subinventory rows contribute on_hand
- POOL row contributes reserved
- ATP = on_hand − reserved

### `fetch_inventory()` signature

```sql
fetch_inventory(
    p_tenant_code    TEXT,                       -- required
    p_product_code   TEXT DEFAULT NULL,          -- NULL = all products
    p_warehouse_code TEXT DEFAULT NULL,          -- NULL = all warehouses
    p_subinventory   TEXT DEFAULT NULL,          -- NULL = aggregate
    p_stock_status   TEXT DEFAULT 'LIBERATED'    -- 'ALL' = no filter
)
RETURNS TABLE (
    tenant_code, product_code, warehouse_code,
    subinventory, stock_status,
    on_hand_qty, reserved_qty, atp,
    uom_code, last_updated_at
)
```

### Usage patterns

```sql
-- "What's available right now for TYRE-001 in warehouse 408?"
SELECT * FROM fetch_inventory('IFOPEUR', '459473_101', '408_ES_P57_P57_WH_CO');
-- Returns 1 row with subinventory='ALL' — aggregated across all subinventories

-- "Specifically the ONHAND subinventory only"
SELECT * FROM fetch_inventory('IFOPEUR', '459473_101', '408_ES_P57_P57_WH_CO', 'ONHAND');
-- Returns 1 row with subinventory='ONHAND'

-- "Across all warehouses"
SELECT * FROM fetch_inventory('IFOPEUR', '459473_101');
-- Returns 1 row per warehouse holding this product

-- "Everything sellable in this warehouse"
SELECT * FROM fetch_inventory('IFOPEUR', NULL, '408_ES_P57_P57_WH_CO');
-- Returns 1 row per product

-- "Include blocked stock too (audit)"
SELECT * FROM fetch_inventory('IFOPEUR', '459473_101', NULL, NULL, 'ALL');
-- Disables stock_status filter — includes BLOCKED, etc.
```

---

## Functions and triggers

```
Drain functions:       3   load_stocklevel, load_transactions, load_orders
Trigger functions:     3   stock maintenance: opening / txn / orders
Triggers:              3   one each
Read function:         1   fetch_inventory  (now with 5 optional filters)
Maintenance function:  1   recalculate_stock_balance  (rebuilds incl. POOL rows)
Helper:                1   notify_outbox
Procedures:            0

Total: 7 callable + 3 trigger functions, 3 triggers.
```

Same count as v2 — every change happened inside existing functions, no new
moving parts.

---

## What this enables for SFDC

| SFDC's question | Single fetch_inventory call |
|---|---|
| What can my salesperson sell **right now**? | `fetch_inventory(tenant, product, warehouse)` — defaults to LIBERATED + aggregated subinventories |
| Specifically what's in our IR1 subinventory? | `fetch_inventory(tenant, product, warehouse, 'IR1')` |
| Where is this product across all warehouses? | `fetch_inventory(tenant, product)` — one row per warehouse |
| What blocked stock do we have? | `fetch_inventory(tenant, product, warehouse, NULL, 'BLOCKED')` |
| Show full inventory snapshot (audit/admin) | `fetch_inventory(tenant, NULL, NULL, NULL, 'ALL')` |

---

## Verification (the exact flow validated for v3)

```sql
-- After running install + seed + sample
SELECT count(*) FROM processed.stock_balance;       -- 65 (ONHAND rows from sample)

-- Insert an order
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','SO-V3-1', jsonb_build_object(
    'customer_id','C1','order_state','open',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','459473_101','warehouse_code','408_ES_P57_P57_WH_CO',
        'qty',5,'uom_code','EA','line_state','open'))));
SELECT * FROM load_orders();

-- POOL row created, fetch_inventory shows reserved=5
SELECT * FROM fetch_inventory('IFOPEUR', '459473_101', '408_ES_P57_P57_WH_CO');
-- on_hand=2, reserved=5, atp=-3, subinventory='ALL'

-- Now ship it (linked transaction)
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','TX-V3-1', jsonb_build_object(
    'product_code','459473_101','warehouse_code','408_ES_P57_P57_WH_CO',
    'signed_qty',-5,'uom_code','EA','txn_type','shipment',
    'sfdc_order_id','SO-V3-1','subinventory','ONHAND'));
SELECT * FROM load_transactions();

-- Order auto-closed, reservation released, on_hand reduced
SELECT * FROM fetch_inventory('IFOPEUR', '459473_101', '408_ES_P57_P57_WH_CO');
-- on_hand=-3, reserved=0, atp=-3
SELECT line_state FROM processed.sfdc_order_line WHERE sfdc_order_id='SO-V3-1';
-- 'closed'
```

---

## Tag provenance

| | |
|---|---|
| Tag | `08-06-v3-customer` |
| Snapshot date | 2026-06-08 |
| Replaces | `05-06-v2-customer` |
| Schema delta from v2 | `subinventory` + `stock_status` cols on opening_balance / stock_balance / inv_transaction; stock_balance PK extended to include both; fetch_inventory becomes parameter-flexible with default LIBERATED filter; new POOL row pattern for warehouse-level reservations |
| Design decisions | A=LIBERATED default · B=pooled reservations · C=locator dropped on promotion |
| Function/trigger count | **7 callable + 3 trigger functions + 3 triggers + 0 procedures** |
| Verified against | `postgres:16-alpine` — install, seed, sample-data, order, shipment cascade, all fetch_inventory filter combinations |
