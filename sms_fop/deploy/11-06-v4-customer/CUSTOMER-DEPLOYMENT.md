# Customer Deployment — Tag `11-06-v4-customer`

Snapshot taken **2026-06-11**. Two architectural changes from v3:

1. **Reservations now land on the specific `(tenant, product, warehouse, subinventory, stock_status, lot=0)` row** — the warehouse-level **POOL sentinel pattern is removed**. SFDC commits at subinventory granularity (per Michelin team confirmation), and `sfdc_order_line` carries `subinventory` + `stock_status` as first-class columns.
2. **Both staging inboxes are append-only.** The `UNIQUE(tenant_code, external_txn_id)` constraint on `txn_inbox` and `UNIQUE(tenant_code, sfdc_order_id)` on `order_inbox` are dropped. FOP appends a new row on every change; drain functions pick the **latest by `received_at`** and mark older pending rows as `superseded`. No `ON CONFLICT DO UPDATE` in the drain path — `load_orders` does `DELETE` + `INSERT`.

## Two install paths

### Path A — fresh install (no live data on the customer DB)

```bash
cd deploy/11-06-v4-customer/
psql -h <host> -U postgres -d <dbname> -f customer_install.sql
psql -h <host> -U postgres -d <dbname> -f customer_seed.sql
```

### Path B — in-place upgrade from v3 (preserves live data)

```bash
cd deploy/11-06-v4-customer/
pg_dump -h <host> -U postgres -d <dbname> -Fc -f pre_v4.dump   # always back up first
psql   -h <host> -U postgres -d <dbname> -f upgrade_from_v3.sql
```

The upgrade script:
- Drops the v3 `UNIQUE` constraints on `staging.txn_inbox` and `staging.order_inbox` (append-only inboxes)
- Extends inbox `status` CHECK to include `'superseded'`
- Adds `subinventory`, `stock_status`, `sfdc_line_id` to `sfdc_order_line`; `sfdc_line_id` to `inv_transaction`; `received_at` to `sfdc_order`; `source_file` to `opening_balance`
- Replaces every function with its v4 version (CREATE OR REPLACE — safe on second run)
- Drops + recreates the 3 triggers
- Deletes v3 POOL rows from `stock_balance`
- Rebuilds `stock_balance` per tenant via `recalculate_stock_balance`
- **Rescue step**: preserves any v3-era reservation (where `sfdc_order_line.subinventory` was the new `''` default after the ALTER) by writing it as a `stock_balance` row with `subinventory=''` and `reserved_qty = SUM(qty)` of those order lines

#### About the `''` subinv rows after upgrade

If you had pending v3 orders, you'll see one `stock_balance` row per `(tenant, product, warehouse)` with `subinventory=''` and a positive `reserved_qty`. **Warehouse-level totals are correct** — `Σ on_hand − Σ reserved = ATP` matches v3. But the reservation isn't yet tied to a specific subinventory because v3 didn't track that dimension.

Two ways to resolve:
- **Wait** — those orders will close naturally when shipments arrive, and the cascade releases the reservation (the new line will land cleanly).
- **Backfill** — update each `sfdc_order_line` with its real subinv (UPDATE fires trigger 3, which moves the reservation from `''` to the new value).

For most customers there will be no pending orders at upgrade time and this whole concern is moot.

---

## What's in this folder

| File | Purpose |
|---|---|
| `customer_install.sql` | Fresh install — schemas, tables, triggers, **9 functions** (per-subinv reservation + insert-only inboxes) |
| `customer_seed.sql` | `IFOPEUR` + `MNA` tenants |
| `upgrade_from_v3.sql` | In-place migration from v3 — preserves live data; includes reservation rescue for v3-era orders |
| `CUSTOMER-DEPLOYMENT.md` | This runbook |

---

## What's new in v4

### Schema additions

| Where | Column | Type | Default |
|---|---|---|---|
| `processed.sfdc_order_line` | `subinventory` | TEXT NOT NULL | `''` |
| `processed.sfdc_order_line` | `stock_status` | TEXT NOT NULL | `'LIBERATED'` |
| `processed.sfdc_order_line` | `sfdc_line_id` | TEXT | NULL (Q2 placeholder) |
| `processed.sfdc_order` | `received_at` | TIMESTAMPTZ NOT NULL | — (from inbox row) |
| `processed.inv_transaction` | `sfdc_line_id` | TEXT | NULL (Q2 placeholder) |
| `processed.opening_balance` | `source_file` | TEXT | NULL (file replay key) |

### Schema removals / changes

| Where | What | Why |
|---|---|---|
| `staging.txn_inbox` | DROPPED `UNIQUE (tenant_code, external_txn_id)` | Append-only inbox |
| `staging.order_inbox` | DROPPED `UNIQUE (tenant_code, sfdc_order_id)` | Append-only inbox |
| Inbox `status` CHECK constraint | + `'superseded'` value | Drain marks older pending rows |
| `processed.stock_balance` POOL rows | No longer written by any code path | Per-subinv reservations replace it |

### Function changes

| Function | Old | New |
|---|---|---|
| `f_stock_balance_reservation_apply` | Targets `subinventory='POOL'` row | Targets specific `NEW.subinventory` row; UPDATE that changes subinv releases OLD row + reserves NEW row |
| `load_orders(limit)` | `INSERT ... ON CONFLICT DO UPDATE` for header + lines | `DELETE FROM sfdc_order WHERE sfdc_order_id = X` (cascades to lines, fires reservation release) → fresh `INSERT` header + lines. No UPSERTs |
| `load_transactions(limit)` | `INSERT ... ON CONFLICT DO NOTHING` | `INSERT ... WHERE NOT EXISTS` (idempotent against FOP retries). Older pending rows marked `superseded` |
| `load_stocklevel(file_name)` | `INSERT ... ON CONFLICT DO NOTHING` | `DELETE FROM opening_balance WHERE batch_id = existing` → fresh INSERT. Replay-friendly |
| `fetch_inventory(...)` | Excluded `subinventory='POOL'` for on_hand; only POOL for reserved | Each row reports its own on_hand and reserved — no special case |
| `recalculate_stock_balance(tenant)` | Wrote POOL rows for reservations | Writes per-`(subinv, stock_status)` row with both qty columns populated |

### Same count as v3 — every change happened inside existing functions

```
Drain functions:       3   load_stocklevel, load_transactions, load_orders
Trigger functions:     3   stock maintenance: opening / txn / orders
Triggers:              3   one each (sfdc_order_line trigger spans I/U/D)
Read function:         1   fetch_inventory  (5 optional filters)
Maintenance function:  1   recalculate_stock_balance
Helper:                1   notify_outbox
Procedures:            0

Total: 7 callable + 3 trigger functions, 3 triggers.
```

---

## The append-only contract — FOP team's view

FOP can now safely **INSERT into staging.\*_inbox at any time, in any order, with the same business key**. No constraint to wrestle with. The contract:

| FOP responsibility | Our responsibility |
|---|---|
| INSERT every event from upstream (one row per change) | Drain picks latest by `received_at`; older marked `superseded` |
| Set `received_at` correctly (defaults to `now()`) | We never UPDATE the FOP payload — they're immutable events |
| Same `sfdc_order_id` may appear N times | We rebuild order state from the latest row only |

If FOP retries the same `external_txn_id` in `txn_inbox`, no problem — the processed.inv_transaction's `UNIQUE(tenant_id, external_txn_id)` guard makes the second `INSERT` a no-op silently.

---

## Verification (the exact flow validated for v4)

```sql
-- After install + seed, add a tenant + masters
INSERT INTO processed.product   (tenant_id, product_code, name)
  SELECT tenant_id, 'TYRE-001', 'TYRE-001' FROM processed.tenant WHERE tenant_code='IFOPEUR';
INSERT INTO processed.warehouse (tenant_id, warehouse_code, name)
  SELECT tenant_id, 'WH-408', 'WH-408' FROM processed.tenant WHERE tenant_code='IFOPEUR';
INSERT INTO processed.uom       (tenant_id, uom_code, name)
  SELECT tenant_id, 'EA', 'Each' FROM processed.tenant WHERE tenant_code='IFOPEUR';

-- Opening balance: 10 in each of two subinventories
INSERT INTO processed.opening_balance
    (tenant_id, tenant_code, product_id, product_code, warehouse_id, warehouse_code,
     subinventory, stock_status, qty, uom_id, uom_code, as_of_date, batch_id, source_file)
SELECT t.tenant_id, 'IFOPEUR', p.product_id, 'TYRE-001',
       w.warehouse_id, 'WH-408', sub, 'LIBERATED',
       10, u.uom_id, 'EA', CURRENT_DATE, 1, 'seed_v4'
  FROM processed.tenant t
  JOIN processed.product   p ON p.tenant_id = t.tenant_id
  JOIN processed.warehouse w ON w.tenant_id = t.tenant_id
  JOIN processed.uom       u ON u.tenant_id = t.tenant_id
  CROSS JOIN (VALUES ('ONHAND'),('US01')) AS s(sub)
 WHERE t.tenant_code='IFOPEUR';

-- Order arrives via inbox, reserves 5 against ONHAND
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','0WO_TEST_001', jsonb_build_object(
    'customer_id','C01003','order_state','open',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','TYRE-001','warehouse_code','WH-408',
        'subinventory','ONHAND','stock_status','LIBERATED',
        'sfdc_line_id','1WL_TEST_LINE_001',
        'qty',5,'uom_code','EA','line_state','open'))));
SELECT * FROM load_orders();
SELECT * FROM fetch_inventory('IFOPEUR','TYRE-001','WH-408');
-- expect:                 subinv=ALL  on_hand=20  reserved=5  atp=15

SELECT * FROM fetch_inventory('IFOPEUR','TYRE-001','WH-408','ONHAND');
-- expect:                 subinv=ONHAND  on_hand=10  reserved=5  atp=5
--                                              ^ reservation visible on the specific row

-- Shipment from ONHAND closes the line, releases reservation
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','TXN_TEST_001', jsonb_build_object(
    'product_code','TYRE-001','warehouse_code','WH-408',
    'subinventory','ONHAND','stock_status','LIBERATED',
    'signed_qty',-5,'uom_code','EA','txn_type','shipment',
    'sfdc_order_id','0WO_TEST_001','sfdc_line_id','1WL_TEST_LINE_001'));
SELECT * FROM load_transactions();
SELECT * FROM fetch_inventory('IFOPEUR','TYRE-001','WH-408');
-- expect:                 subinv=ALL  on_hand=15  reserved=0  atp=15

SELECT line_state FROM processed.sfdc_order_line WHERE sfdc_order_id='0WO_TEST_001';
-- 'closed'

-- Append-only test: same order id 3x, drain picks latest
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload, received_at) VALUES
('IFOPEUR','0WO_TEST_002', '{"customer_id":"C1","lines":[{"product_code":"TYRE-001","warehouse_code":"WH-408","subinventory":"ONHAND","qty":1,"uom_code":"EA"}]}', '2026-06-11 09:00:00+00'),
('IFOPEUR','0WO_TEST_002', '{"customer_id":"C1","lines":[{"product_code":"TYRE-001","warehouse_code":"WH-408","subinventory":"ONHAND","qty":2,"uom_code":"EA"}]}', '2026-06-11 09:01:00+00'),
('IFOPEUR','0WO_TEST_002', '{"customer_id":"C1","lines":[{"product_code":"TYRE-001","warehouse_code":"WH-408","subinventory":"ONHAND","qty":3,"uom_code":"EA"}]}', '2026-06-11 09:02:00+00');
SELECT * FROM load_orders();
-- expect: rows_processed=1  rows_failed=0  rows_superseded=2

SELECT qty FROM processed.sfdc_order_line WHERE sfdc_order_id='0WO_TEST_002';
-- expect: 3  (the latest payload)
```

All assertions verified against `postgres:16-alpine` on 2026-06-11.

---

## What's NOT in this install

- **Role grants** — ops territory, per your privilege policy
- **`pg_partman` partition policy** — apply if you want monthly partitions on `inv_transaction` / `audit_log`
- **`pg_cron` schedules** — wire the drain functions to a schedule when other teams turn on (suggested: `SELECT load_transactions()` every 30s, `SELECT load_orders()` every 30s)
- **The Oracle txn ↔ SFDC line link (Q2)** — `sfdc_line_id` columns added as placeholders. When Michelin tells us how Oracle's `SOURCE_LINE_ID` translates to SFDC's `Id`, the cascade matcher in `f_stock_balance_txn_apply` switches from `sfdc_order_id` to `sfdc_line_id` — one-line change, no schema work

---

## Tag provenance

| | |
|---|---|
| Tag | `11-06-v4-customer` |
| Snapshot date | 2026-06-11 |
| Replaces | `08-06-v3-customer` |
| Schema delta from v3 | + `sfdc_order_line.{subinventory, stock_status, sfdc_line_id}`; + `inv_transaction.sfdc_line_id`; + `sfdc_order.received_at`; + `opening_balance.source_file`; − UNIQUE constraints on both staging inboxes; − POOL row pattern |
| Design decisions | (a) SFDC commits at subinv → reservation per-row · (b) inboxes append-only · (c) DELETE-then-INSERT in load_orders, no ON CONFLICT · (d) narrow-UPSERT reading: triggers keep ON CONFLICT for stock_balance maintenance |
| Function/trigger count | **7 callable + 3 trigger functions + 3 triggers + 0 procedures** (same count as v3) |
| Verified against | `postgres:16-alpine` — install, seed, opening balance trigger, order reservation, shipment cascade, append-only inbox drain |
