---
title: fetch_inventory_json API
description: The read contract SFDC calls from their quote and order screens.
---

The read contract exposed to the SFDC integration tier. Not an HTTP API — a Postgres function callable via any JDBC / Postgres access path (PostgREST, Heroku Connect, direct connection).

Two variants:

- `processed.fetch_inventory_json(...)` — the plain read
- `processed.fetch_inventory_json_observed(...)` — same result set + audit log entry per call (**new callers should use this**)

## Signature

```sql
processed.fetch_inventory_json(
    p_tenant_code    TEXT,
    p_warehouse_code TEXT DEFAULT NULL,
    p_subinventory   TEXT DEFAULT NULL,
    p_product_code   TEXT DEFAULT NULL
) RETURNS SETOF JSON
```

```sql
processed.fetch_inventory_json_observed(
    p_tenant_code    TEXT,
    p_warehouse_code TEXT DEFAULT NULL,
    p_subinventory   TEXT DEFAULT NULL,
    p_product_code   TEXT DEFAULT NULL,
    p_caller         TEXT DEFAULT NULL
) RETURNS SETOF JSON
```

## Parameters

| Param | Required | Behavior |
|---|---|---|
| `p_tenant_code` | yes | Filter to a specific tenant. Must match `processed.tenant.tenant_code` (e.g., `MNA`) |
| `p_warehouse_code` | no | Filter to a specific warehouse. NULL = all warehouses |
| `p_subinventory` | no | Filter to a specific subinventory. NULL = all subinventories. Special value `'ALL'` shows every stock_status (default omits `BLOCKED`) |
| `p_product_code` | no | Filter to a specific product. NULL = all products |
| `p_caller` (observed only) | no | Identifies the caller service in the audit log entry. Defaults to `'unknown'` |

## Return shape

Each row is one JSON object per `(tenant, product, warehouse, subinventory, stock_status)` group:

```json
{
  "tenant_code":    "MNA",
  "product_code":   "459473_101",
  "warehouse_code": "408_ES_P57_..._CO",
  "subinventory":   "ONHAND",
  "stock_status":   "LIBERATED",
  "on_hand":        350.0000,
  "reserved":       50.0000,
  "available":      300.0000,
  "uom_code":       "EA"
}
```

`available = on_hand - reserved` (computed in the function).

## Example calls

### All rows for a tenant

```sql
SELECT * FROM processed.fetch_inventory_json('MNA', NULL, NULL, NULL);
-- returns N rows, one per stock position
```

### One product across all warehouses

```sql
SELECT * FROM processed.fetch_inventory_json('MNA', NULL, NULL, '459473_101');
```

### One (warehouse, subinventory, product) — the ATP check for a specific quote line

```sql
SELECT * FROM processed.fetch_inventory_json(
    'MNA',
    '408_ES_P57_..._CO',
    'ONHAND',
    '459473_101'
);
```

### With audit logging (observed variant)

```sql
SELECT * FROM processed.fetch_inventory_json_observed(
    'MNA',
    '408_ES_P57_..._CO',
    'ONHAND',
    '459473_101',
    'sfdc-quote-screen'
);
```

Same result set. Additionally writes one row to `audit.event_log`:

```
event_type='atp.queried', source='atp', tenant_id='MNA',
ref_id='sfdc-quote-screen', latency_ms=<n>, rows_affected=<n>,
payload={warehouse_code, subinventory, product_code, caller}
```

## Audit-write failure semantics

The audit log write in `fetch_inventory_json_observed` is wrapped in `BEGIN ... EXCEPTION WHEN OTHERS THEN RAISE WARNING`. If the audit write fails:

- The read response is unaffected — the caller still gets its rows
- A WARNING is logged in the Postgres server log
- No row is written to `audit.event_log` for that call
- No exception propagates to the caller

**The read is more important than the audit row.** This is deliberate.

## Access control

Roles that can call each variant:

| Role | `fetch_inventory_json` | `fetch_inventory_json_observed` |
|---|---|---|
| `inventoryledger_reader` (SFDC read tier) | ✓ | ✓ |
| `inventoryledger_writer` (FOP + SFDC ingestion) | — | — |
| `inventoryledger_app` (Java daemon) | — | — |

Explicit `REVOKE ALL FROM PUBLIC; GRANT EXECUTE TO inventoryledger_reader` on each. No public execute.

## Tenant isolation

The DB does **not** bind the connected role to a tenant. The caller must:

1. Authenticate the user at the edge
2. Resolve the user's permitted tenants from the identity store
3. Validate the requested `p_tenant_code` is in the permitted set
4. Set `SET LOCAL app.tenant_id = '<tenant_id>'` inside a transaction (optional — the function filters on `p_tenant_code` explicitly)

Skipping step 3 lets a compromised reader query any tenant's inventory. See [Coding guidelines — tenant isolation is upstream](/quality/coding-guidelines#tenant-isolation-is-upstream).

## Data freshness

Reads live from `processed.stock_balance`. That table is maintained transactionally by the promote triggers:

- Every `staging.txn_inbox INSERT` synchronously updates `stock_balance` (via `f_stock_balance_txn_apply`)
- Every `staging.order_inbox INSERT` synchronously updates `stock_balance.reserved_qty` (via `f_stock_balance_reservation_apply`)
- Every opening_balance INSERT updates `stock_balance.on_hand_qty`

There is no MV, no cache, no eventual consistency. `fetch_inventory_json` reflects the DB's current committed state at call time.

## Performance

- p95 latency: 45–95 ms for unfiltered tenant query (284 rows in prod today)
- Filtered queries (`p_product_code` + `p_warehouse_code` + `p_subinventory`): 8–20 ms

See [Performance](/quality/performance#fetch_inventory_json-sampling).

## What NOT to call this for

- **Not for backfill / batch export.** Use `SELECT * FROM processed.stock_balance` (via a role with SELECT on that table) if you need to export large slices.
- **Not for point-in-time historical lookup.** This is the *current* position. For history, query `processed.inv_transaction` + `processed.opening_balance` filtered by date.
- **Not for order-line reservation lookup.** For "what's currently reserved for order X, line Y?", query `processed.sfdc_order_line` directly.

## Error semantics

- Tenant doesn't exist → empty result set (not an error)
- No matching rows → empty result set
- Product / warehouse / subinventory doesn't exist → empty result set for that filter (not an error)
- Missing `p_tenant_code` → SQL error (required parameter)

The function never raises for missing reference data — it returns an empty set. Callers should not treat empty as an error condition.

## Sample response for the SFDC quote screen

Query: "how much ONHAND stock of product X do we have at warehouse Y right now?"

```sql
SELECT (row->>'available')::NUMERIC AS available
  FROM processed.fetch_inventory_json_observed(
      'MNA', '408_ES_P57_..._CO', 'ONHAND', 'PROD-A', 'sfdc-quote') AS row;
```

Returns a single row with `available` as a NUMERIC. Empty if no matching stock position exists.
