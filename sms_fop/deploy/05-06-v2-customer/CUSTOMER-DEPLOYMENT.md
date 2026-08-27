# Customer Deployment — Tag `05-06-v2-customer`

Snapshot taken **2026-06-05**. Replaces tag `05-06-v1-customer` with the
simplified function/trigger model (3 drain functions + 3 triggers + cascade
from shipment txn → order line close → reservation release).

```bash
cd deploy/05-06-v2-customer/

# 1. Schemas + tables + functions + triggers
psql -h <host> -U postgres -d <dbname> -f customer_install.sql

# 2. Tenants
psql -h <host> -U postgres -d <dbname> -f customer_seed.sql

# 3. (Optional) 65-row Michelin BATCH sample for smoke test
psql -h <host> -U postgres -d <dbname> -f customer_sample_data.sql
```

---

## What's in this folder

| File | Purpose |
|---|---|
| `customer_install.sql` | Schemas, tables, indexes, **3 trigger functions, 4 drain/read/maintenance functions** |
| `customer_seed.sql` | `IFOPEUR` + `MNA` tenants |
| `customer_sample_data.sql` | 65 Michelin BATCH_408 rows + `load_stocklevel` promotion |
| `CUSTOMER-DEPLOYMENT.md` | This runbook |

---

## The new model

### Drain functions (3)

| Function | Reads | Writes | Concurrency |
|---|---|---|---|
| `load_stocklevel(file_name)` | `staging.stocklevel_inbox` | `processed.opening_balance` | Idempotent per file (batch tracker) |
| `load_transactions(limit DEFAULT 500)` | `staging.txn_inbox` (other teams write here) | `processed.inv_transaction` | **`FOR UPDATE SKIP LOCKED`** — many workers safe |
| `load_orders(limit DEFAULT 500)` | `staging.order_inbox` (other teams write here) | `processed.sfdc_order` + `sfdc_order_line` | **`FOR UPDATE SKIP LOCKED`** — many workers safe |

### Trigger functions (3) — all in `processed` schema

| Trigger function | Fires on | What it does |
|---|---|---|
| `f_stock_balance_opening_apply` | `processed.opening_balance INSERT` | **Rebases** `stock_balance.on_hand_qty` for newer (or first) `as_of_date`. Back-dated rows are flagged via `notification_outbox` and skipped (run `recalculate_stock_balance` to apply). |
| `f_stock_balance_txn_apply` | `processed.inv_transaction INSERT` | (1) `on_hand_qty += signed_qty`. (2) If `sfdc_order_id` is set, **UPDATEs matching order lines to `closed`**, which cascades into trigger #3. |
| `f_stock_balance_reservation_apply` | `processed.sfdc_order_line INSERT / UPDATE / DELETE` | Maintains `stock_balance.reserved_qty`. INSERT (open/synced) → +qty. State leaving active → −qty. Qty changes → delta. |

### Triggers (3)

```
trg_opening_balance_stock_apply       AFTER INSERT  ON processed.opening_balance
trg_inv_transaction_stock_apply       AFTER INSERT  ON processed.inv_transaction
trg_sfdc_order_line_reservation       AFTER INSERT OR UPDATE OR DELETE
                                                    ON processed.sfdc_order_line
```

### Read + maintenance + helper (3)

| Function | Purpose |
|---|---|
| `fetch_inventory(tenant_code, product_code, warehouse_code)` | Read API for SFDC. Returns `(on_hand_qty, reserved_qty, atp, uom_code, last_updated_at)`. Pure subtraction, no aggregation, MVCC consistent. |
| `recalculate_stock_balance(tenant_code)` | Wipes the tenant's `stock_balance`, rebuilds from `opening_balance` (latest by `as_of_date`) + transactions posted after that date + reservations from active order lines. For repairs, restores, back-dated corrections. |
| `notify_outbox(tenant_id, severity, source, message, payload, dedup_key)` | Outbox row for the Java MicroService to webhook out. Dedupes on key. |

### Procedures

**0.** Postgres `CREATE PROCEDURE` is not needed — every write path is a single function call; the Java MicroService manages the outer transaction.

---

## The cascade — most important behavior

```
FOP team INSERTs into staging.txn_inbox (with sfdc_order_id="SO-0001")
                            │
                            ▼
              ops/cron call load_transactions()
                            │
                            ▼
            INSERT INTO processed.inv_transaction
                            │
            ▼ (trigger 2 fires)
   f_stock_balance_txn_apply:
       1. on_hand_qty += signed_qty               ◄── stock drops
       2. UPDATE sfdc_order_line SET line_state='closed'
                            │
            ▼ (trigger 3 fires from that UPDATE)
   f_stock_balance_reservation_apply:
       reserved_qty -= line.qty                   ◄── reservation released
```

One transaction insert → two stock_balance columns adjusted → order line closed
→ all within one DB transaction. No cron, no janitor, no eventual consistency.

---

## Read query

The salesperson's "what's available to sell right now" query:

```sql
SELECT * FROM fetch_inventory('IFOPEUR', '459473_101', '408_ES_P57_P57_WH_CO');
```

Returns:

```
 tenant_code | product_code | warehouse_code     | on_hand_qty | reserved_qty | atp | uom_code | last_updated_at
-------------+--------------+--------------------+-------------+--------------+-----+----------+-----------------
 IFOPEUR     | 459473_101   | 408_ES_P57_P57_WH_…|           2 |            5 |  -3 | EA       | 2026-06-05 ...
```

SFDC calls this at quote entry **and** again at order commit (their workflow).

---

## What is NOT in this install

- **Role grants** — ops territory, per your privilege policy
- **`pg_partman` partition policy** — apply if you want monthly partitions on `inv_transaction` / `audit_log`
- **`pg_cron` schedules** — wire the drain functions to a schedule when other teams turn on (suggested: `SELECT load_transactions()` every 30s, `SELECT load_orders()` every 30s)

---

## Verification

After install:

```sql
SELECT count(*) AS processed_tables FROM pg_tables WHERE schemaname = 'processed';  -- 11
SELECT count(*) AS staging_tables   FROM pg_tables WHERE schemaname = 'staging';    -- 4
SELECT proname FROM pg_proc p JOIN pg_namespace n ON n.oid=p.pronamespace
 WHERE n.nspname IN ('public','processed') AND p.prokind='f'
 ORDER BY proname;  -- 7 + 3 = 10 functions (3 trigger fns + 7 callable)
SELECT trigger_name FROM information_schema.triggers
 WHERE trigger_schema='processed' ORDER BY trigger_name;  -- 3 triggers
```

After cascade smoke test (per the integration verified for v2):

```sql
-- 1. Insert an order via staging.order_inbox
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','SO-0001', jsonb_build_object(
    'customer_id','CUST-A','order_state','open',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','459473_101', 'warehouse_code','408_ES_P57_P57_WH_CO',
        'qty',5, 'uom_code','EA', 'line_state','open'))));
SELECT * FROM load_orders();
SELECT reserved_qty FROM fetch_inventory('IFOPEUR','459473_101','408_ES_P57_P57_WH_CO');
-- expect reserved_qty = 5

-- 2. Insert the matching shipment txn — reserved should release
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','TX-0001', jsonb_build_object(
    'product_code','459473_101','warehouse_code','408_ES_P57_P57_WH_CO',
    'signed_qty',-5,'uom_code','EA','txn_type','shipment',
    'sfdc_order_id','SO-0001'));
SELECT * FROM load_transactions();
SELECT line_state FROM processed.sfdc_order_line WHERE sfdc_order_id='SO-0001';
-- expect 'closed'
SELECT reserved_qty FROM fetch_inventory('IFOPEUR','459473_101','408_ES_P57_P57_WH_CO');
-- expect reserved_qty = 0
```

---

## Tag provenance

| | |
|---|---|
| Tag | `05-06-v2-customer` |
| Snapshot date | 2026-06-05 |
| Replaces | `05-06-v1-customer` |
| Schema delta from v1 | + `stock_balance.reserved_qty` column<br>+ `inv_transaction.sfdc_order_id` column<br>+ 2 trigger functions (`f_stock_balance_txn_apply`, `f_stock_balance_reservation_apply`)<br>+ 3 callable functions (`load_transactions`, `load_orders`, `fetch_inventory`, `recalculate_stock_balance`)<br>+ updated `f_stock_balance_opening_apply` to respect `as_of_date` |
| Function/trigger count | **7 functions + 3 triggers + 0 procedures** |
| Verified against | `postgres:16-alpine` — install, seed, sample-data, order + shipment cascade, fetch_inventory, recalculate |
