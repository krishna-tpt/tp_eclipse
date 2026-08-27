# Customer Deployment — Tag `05-06-v1-customer`

Snapshot taken **2026-06-05**. Self-contained: every file referenced below
lives in this same folder.

```bash
cd deploy/05-06-v1-customer/

# 1. Apply schemas + tables + functions + triggers
psql -h <host> -U postgres -d <dbname> -f customer_install.sql

# 2. Seed tenants
psql -h <host> -U postgres -d <dbname> -f customer_seed.sql

# 3. (Optional) Load the 65-row Michelin BATCH sample for a smoke test
psql -h <host> -U postgres -d <dbname> -f customer_sample_data.sql
```

After step 2 the Java MicroService can start ingesting files. Step 3 is purely
optional — useful for demoing the read query before SFTP is wired.

---

## What's in this folder

| File | Purpose |
|---|---|
| `customer_install.sql` | Schemas, tables, indexes, triggers, functions — full DDL |
| `customer_seed.sql` | `IFOPEUR` + `MNA` tenants |
| `customer_sample_data.sql` | 65 rows of Michelin BATCH_408 stock + the promotion call |
| `CUSTOMER-DEPLOYMENT.md` | This runbook |

---

## What gets created

### Schemas
| Schema | Purpose | Tables |
|---|---|---|
| `processed` | Live ledger | tenant, product, warehouse, uom, lot, opening_balance, stock_balance, inv_transaction, sfdc_order, sfdc_order_line, notification_outbox |
| `staging` | Landing zones | stocklevel_inbox, stocklevel_batch (ours), txn_inbox, order_inbox (other teams') |
| `audit` | Change log | audit_log (partitioned by changed_at) |
| `public` | Untouched | (PostgreSQL extensions only) |

### Functions
- `load_stocklevel(p_file_name TEXT) → (batch_id, rows_accepted, rows_rejected, status)` — promotion from staging to live; idempotent
- `notify_outbox(tenant_id, severity, source, message, payload, dedup_key) → outbox_id` — writes one notification row, dedupes by key

### Triggers
- `trg_opening_balance_stock_apply` on `processed.opening_balance` — auto-updates `processed.stock_balance.on_hand_qty`

### Seed data
- `processed.tenant`: `IFOPEUR` (Michelin BATCH source), `MNA` (Michelin DMC source)

---

## What is NOT in this install (applied by ops separately)

- **Role grants** — least-privilege roles (`inventoryledger_app`, `inventoryledger_writer`) should be created and granted `EXECUTE` / `SELECT` / `INSERT` per ops policy
- `pg_partman` partition policy — if monthly partitions on `inv_transaction` / `audit_log` are wanted
- `pg_cron` schedules — for MV refresh, archive, drift detection
- Stored functions for transactions / orders (`post_transaction`, `upsert_order`, `process_txn_inbox`, etc.) — those arrive when the other-team integrations turn on; staging tables are already in place to receive their writes

---

## Verification checklist

After running the scripts:

```sql
-- 3 schemas (plus public) exist
SELECT nspname FROM pg_namespace
 WHERE nspname IN ('processed','staging','audit','public')
 ORDER BY nspname;

-- 11 processed tables
SELECT count(*) FROM pg_tables WHERE schemaname = 'processed';

-- 4 staging tables
SELECT count(*) FROM pg_tables WHERE schemaname = 'staging';

-- 2 tenants
SELECT tenant_code, name FROM processed.tenant ORDER BY tenant_code;

-- Promotion function exists and returns a clean result on empty staging
SELECT * FROM load_stocklevel('smoke-test.cfo');
--   batch_id | rows_accepted | rows_rejected | status
--   ---------+---------------+---------------+--------
--          1 |             0 |             0 | loaded
```

If you ran `customer_sample_data.sql` (step 3), also:

```sql
-- 65 rows in each
SELECT 'staging.stocklevel_inbox' AS t, COUNT(*) FROM staging.stocklevel_inbox
UNION ALL SELECT 'processed.opening_balance', COUNT(*) FROM processed.opening_balance
UNION ALL SELECT 'processed.stock_balance',    COUNT(*) FROM processed.stock_balance;
```

---

## Read query: "what's on hand right now"

```sql
SELECT product_code, warehouse_code, on_hand_qty, uom_code
  FROM processed.stock_balance
 WHERE tenant_code = 'IFOPEUR';
```

Today (day 1) `stock_balance.on_hand_qty = opening_balance.qty`.
Once transactions begin flowing, the trigger keeps `on_hand_qty` adjusted
without changing this query.

---

## Re-running

All three scripts are idempotent at the object level:
- `customer_install.sql` uses `CREATE TABLE` / `CREATE FUNCTION OR REPLACE`; re-runs are no-ops once installed
- `customer_seed.sql` uses `ON CONFLICT DO NOTHING`
- `customer_sample_data.sql` deletes the prior copy of that file's data before re-inserting, so it can be re-run as a clean reset

You **cannot** mix this install with the historical Flyway migrations
(`db/migration/V*.sql`). Those create live tables in `public` and won't
coexist with the `processed` schema layout. Pick one path per environment.

---

## Why a separate `processed` schema?

Old layout had live tables in `public`. That works but mixes Postgres-supplied
objects, our live ledger, and any future extension installs. Moving live data
to a named schema:
- Makes searches via `\dt processed.*` instant
- Lets us grant per-schema (`GRANT USAGE ON SCHEMA processed TO read_role`)
- Keeps `public` clean for vendor extensions
- Makes the data-flow direction explicit: `staging.* → load_stocklevel() → processed.*`

---

## Tag provenance

| | |
|---|---|
| Tag | `05-06-v1-customer` |
| Snapshot date | 2026-06-05 |
| Schema version | `processed` layout (V14 + V15 baked into install) |
| Sample data | Michelin BATCH_408 × 65 rows |
| Verified against | `postgres:16-alpine` (stock image, no extensions beyond `plpgsql`) |
| Replaces | None — this is the first customer tag |
