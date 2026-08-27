# Customer Deployment — Tag `11-06-v5-customer`

Snapshot taken **2026-06-11**. **Architectural shift from v4:**

The drain-on-schedule model is replaced with **per-row AFTER INSERT triggers**
on `staging.txn_inbox` and `staging.order_inbox`. The moment FOP / SFDC
integration teams INSERT into either inbox, a trigger fires that does
master-data lookup, payload parsing, and writes into `processed.*` — all
within the same transaction. No `pg_cron`, no scheduler, no JAR call needed
for transactions or orders.

Stocklevel ingestion stays exactly as v4 — Java MicroService reads files and
calls `load_stocklevel(file_name)` explicitly.

## Two install paths

### Path A — fresh install (no live data yet)

```bash
cd deploy/11-06-v5-customer/
psql -h <host> -U postgres -d <dbname> -f customer_install.sql
psql -h <host> -U postgres -d <dbname> -f customer_seed.sql
```

### Path B — in-place upgrade from v4

```bash
cd deploy/11-06-v5-customer/
pg_dump -h <host> -U postgres -d <dbname> -Fc -f pre_v5.dump
psql   -h <host> -U postgres -d <dbname> -f upgrade_from_v4.sql
```

The upgrade script:
- Replaces `load_orders` with the v5 version (UPSERT on `sfdc_order` instead of DELETE+INSERT — preserves `created_at`)
- Adds `staging.f_promote_txn()` + `trg_txn_inbox_promote` trigger
- Adds `staging.f_promote_order()` + `trg_order_inbox_promote` trigger
- No table changes, no constraint changes, no data migration — purely additive

---

## What's in this folder

| File | Purpose |
|---|---|
| `customer_install.sql` | Fresh install (full v5) |
| `customer_seed.sql` | `IFOPEUR` + `MNA` tenants |
| `upgrade_from_v4.sql` | In-place upgrade from v4 (purely additive) |
| `CUSTOMER-DEPLOYMENT.md` | This runbook |

---

## What's new in v5

### Auto-promote triggers — the headline change

| Trigger | Fires on | What it does |
|---|---|---|
| `trg_txn_inbox_promote` | `staging.txn_inbox INSERT` (per row) | Parses payload, looks up tenant/product/warehouse/uom IDs, INSERTs into `processed.inv_transaction` (idempotent on `external_txn_id`), marks staging row `processed` with `processed_at = now()`. On error: marks staging row `rejected` with `reject_reason`. |
| `trg_order_inbox_promote` | `staging.order_inbox INSERT` (per row) | Parses payload + lines, looks up masters, DELETEs existing `sfdc_order_line` (cascade releases prior reservation), UPSERTs `sfdc_order` (preserves `created_at`), INSERTs new lines (cascade fires reservation on the specific subinv row), marks staging row `processed`. On error: marks staging row `rejected`. |

Both triggers fire **AFTER INSERT only**. No UPDATE or DELETE triggers on inbox tables.

### The UPSERT semantics for `processed.sfdc_order`

```sql
INSERT INTO processed.sfdc_order (...)
VALUES (...)
ON CONFLICT (sfdc_order_id) DO UPDATE
SET tenant_id   = EXCLUDED.tenant_id,
    tenant_code = EXCLUDED.tenant_code,
    customer_id = EXCLUDED.customer_id,
    order_state = EXCLUDED.order_state,
    payload     = EXCLUDED.payload,
    received_at = EXCLUDED.received_at,
    updated_at  = now();
-- created_at NOT in the SET list - preserved across re-posts
```

Three timestamps on `processed.sfdc_order`:

| Column | Meaning |
|---|---|
| `created_at` | When the order was **first ever** posted (preserved by UPSERT) |
| `updated_at` | When the order **last changed** (refreshed on every UPSERT hit) |
| `received_at` | When the **latest inbox row** that drove the UPSERT arrived (from `staging.order_inbox.received_at`) |

### Why DELETE+INSERT for `sfdc_order_line` (not UPSERT)

Line numbers (`line_no`) might re-number across re-posts (SFDC could drop, add, or reorder lines). DELETE-then-INSERT is simpler than per-line UPSERT, and the `f_stock_balance_reservation_apply` trigger handles release (on DELETE) and reserve (on INSERT) correctly. Cascade math works out.

### Error handling — staging row always reaches terminal state

```
FOP INSERT into staging.txn_inbox
              ↓
  AFTER INSERT trigger fires
              ↓
  BEGIN
      [look up tenant/product/warehouse/uom]
      [INSERT into processed.inv_transaction]
      UPDATE staging row → status='processed', processed_at=now()
      RETURN NEW
  EXCEPTION WHEN OTHERS:
      UPDATE staging row → status='rejected', processed_at=now(), reject_reason=SQLERRM
      RETURN NEW
  END
              ↓
  Original INSERT completes successfully — FOP sees no error
```

Implications:
- FOP / SFDC integration teams **never** see a failed INSERT due to downstream issues (unknown master, validation failure, etc.). The INSERT always succeeds.
- Every staging row ends with `status` in `('processed','rejected')` and a non-NULL `processed_at`.
- Operators reprocess `'rejected'` rows by fixing the cause (e.g. add the missing tenant) and re-INSERTing — the trigger fires again on the new INSERT.

### Drain functions retained as fallback

`load_transactions(p_limit)` and `load_orders(p_limit)` are kept. They're vestigial in normal operation (the triggers handle everything), but useful for:
- Catch-up after a temporary trigger outage (manually disabled triggers)
- Operator-driven reprocessing batch (e.g. drain everything in `'pending'` after a fix)

`load_orders` in v5 uses the same UPSERT pattern as the trigger for consistency.

### What stays the same from v4

| | |
|---|---|
| Schema | Unchanged. Same tables, same columns, same PKs, same FKs |
| Stock-balance triggers | Unchanged — `f_stock_balance_opening_apply`, `f_stock_balance_txn_apply`, `f_stock_balance_reservation_apply` keep the same bodies |
| Payload contract | Same lowercase snake_case keys. FOP / SFDC normalize Oracle EBS / Salesforce native shapes before INSERT |
| Identifier type | TEXT (no UUID anywhere) |
| Inbox shape | Append-only, no UNIQUE on business keys. Same `(pending, processed, rejected, superseded)` status enum |

---

## Verification (the exact flow validated for v5)

```sql
-- After install + seed, add masters + opening stock
INSERT INTO processed.product   (tenant_id, product_code, name)
  SELECT tenant_id, 'TYRE-001', 'TYRE-001' FROM processed.tenant WHERE tenant_code='IFOPEUR';
INSERT INTO processed.warehouse (tenant_id, warehouse_code, name)
  SELECT tenant_id, 'WH-408', 'WH-408' FROM processed.tenant WHERE tenant_code='IFOPEUR';
INSERT INTO processed.uom       (tenant_id, uom_code, name)
  SELECT tenant_id, 'EA', 'Each' FROM processed.tenant WHERE tenant_code='IFOPEUR';
INSERT INTO processed.opening_balance
    (tenant_id, tenant_code, product_id, product_code, warehouse_id, warehouse_code,
     subinventory, stock_status, qty, uom_id, uom_code, as_of_date, batch_id, source_file)
SELECT t.tenant_id, 'IFOPEUR', p.product_id, 'TYRE-001',
       w.warehouse_id, 'WH-408', sub, 'LIBERATED',
       10, u.uom_id, 'EA', CURRENT_DATE, 1, 'seed_v5'
  FROM processed.tenant t
  JOIN processed.product   p ON p.tenant_id=t.tenant_id
  JOIN processed.warehouse w ON w.tenant_id=t.tenant_id
  JOIN processed.uom       u ON u.tenant_id=t.tenant_id
  CROSS JOIN (VALUES ('ONHAND'),('US01')) AS s(sub)
 WHERE t.tenant_code='IFOPEUR';

-- A. INSERT order into inbox - trigger auto-promotes
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','SO_V5_001', jsonb_build_object(
    'customer_id','C01003','order_state','open',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','TYRE-001','warehouse_code','WH-408',
        'subinventory','ONHAND','stock_status','LIBERATED',
        'sfdc_line_id','LINE_001',
        'qty',5,'uom_code','EA','line_state','open'))));

SELECT status, processed_at FROM staging.order_inbox WHERE sfdc_order_id='SO_V5_001';
-- expect: processed | (timestamp)

SELECT subinventory, on_hand_qty, reserved_qty FROM processed.stock_balance ORDER BY 1;
-- expect: ONHAND 10/5, US01 10/0

-- B. SAME order_id again with different qty - UPSERT preserves created_at
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','SO_V5_001', jsonb_build_object(
    'customer_id','C01003','order_state','open',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','TYRE-001','warehouse_code','WH-408',
        'subinventory','ONHAND','stock_status','LIBERATED',
        'sfdc_line_id','LINE_001',
        'qty',8,'uom_code','EA','line_state','open'))));

SELECT created_at = updated_at AS created_eq_updated FROM processed.sfdc_order WHERE sfdc_order_id='SO_V5_001';
-- expect: false (created preserved, updated fresh)

SELECT reserved_qty FROM processed.stock_balance WHERE subinventory='ONHAND';
-- expect: 8 (previous 5 released, new 8 reserved)

-- C. INSERT shipment into inbox - trigger auto-promotes + cascade closes order
INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) VALUES
('IFOPEUR','TXN_V5_001', jsonb_build_object(
    'product_code','TYRE-001','warehouse_code','WH-408',
    'subinventory','ONHAND','stock_status','LIBERATED',
    'signed_qty',-5,'uom_code','EA','txn_type','shipment',
    'sfdc_order_id','SO_V5_001','sfdc_line_id','LINE_001'));

SELECT line_state FROM processed.sfdc_order_line WHERE sfdc_order_id='SO_V5_001';
-- expect: closed

SELECT subinventory, on_hand_qty, reserved_qty FROM processed.stock_balance ORDER BY 1;
-- expect: ONHAND 5/0, US01 10/0

-- D. Bad master - trigger marks REJECTED, original INSERT still succeeds
INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) VALUES
('IFOPEUR','SO_BAD', jsonb_build_object(
    'customer_id','C1',
    'lines', jsonb_build_array(jsonb_build_object(
        'product_code','UNKNOWN-PRODUCT','warehouse_code','WH-408',
        'qty',1,'uom_code','EA'))));

SELECT status, reject_reason FROM staging.order_inbox WHERE sfdc_order_id='SO_BAD';
-- expect: rejected | 'unknown master in line 1'
```

All assertions verified against `postgres:16-alpine` on 2026-06-11.

---

## Function/trigger inventory

```
DRAIN (fallback):       3   load_stocklevel, load_transactions, load_orders
PROMOTE TRIGGER FNS:    2   staging.f_promote_txn, staging.f_promote_order  ← NEW
STOCK-BALANCE TRIGGER FNS: 3   stock maintenance: opening / txn / orders
TRIGGERS:               5   3 on processed (stock_balance), 2 on staging (promote)
READ:                   1   fetch_inventory
MAINTENANCE:            1   recalculate_stock_balance
HELPER:                 1   notify_outbox
PROCEDURES:             0

Total: 8 callable + 5 trigger functions, 5 triggers.
```

---

## Tag provenance

| | |
|---|---|
| Tag | `11-06-v5-customer` |
| Snapshot date | 2026-06-11 |
| Replaces | `11-06-v4-customer` |
| Schema delta from v4 | **none** — purely additive functions + triggers |
| Function delta from v4 | + `staging.f_promote_txn`, + `staging.f_promote_order`; `load_orders` swapped DELETE+INSERT for UPSERT on `sfdc_order` |
| Trigger delta from v4 | + `trg_txn_inbox_promote`, + `trg_order_inbox_promote` (both AFTER INSERT, FOR EACH ROW) |
| Design decisions | Per-row event-driven promotion · UPSERT preserves `created_at` · trigger absorbs its own errors via EXCEPTION block · drain functions retained as fallback |
| Function/trigger count | **8 callable + 5 trigger functions + 5 triggers + 0 procedures** |
| Verified against | `postgres:16-alpine` — install, seed, opening trigger, order auto-promote, second-insert UPSERT, shipment auto-promote with cascade, reject-inline path |
