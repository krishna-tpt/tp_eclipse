# DBServices — Inventory Ledger Database (PostgreSQL)

The Postgres half of the Inventory Ledger. Owns the entire schema, every
trigger, every stored function, and every `pg_cron` schedule. The Java
MicroService and other teams' services all reach through this contract.

> **Scope:** This project owns the **complete schema** — including the
> staging tables that *other teams* (FOP for transactions, SFDC for orders)
> write into. We do not own those teams' application code; we own the
> tables they target and the drain functions that promote those rows into
> live state. See the docs site (`docs-site/content/overview/scope.mdx`) for the full boundary.

---

## Layout

```
db/
├── migration/                            ← Flyway sources, applied at app startup
│   ├── V1__core_schema.sql               core tables, types, basic indexes
│   ├── V2__functions_and_triggers.sql    stock_balance triggers, audit capture
│   ├── V3__schedules_partitions_grants.sql  pg_partman, pg_cron, role grants
│   ├── V4..V13                           layered fixes, security definer,
│   │                                      RLS hardening, flatten
│   └── V14__stocklevel_staging.sql       opening-balance staging (61 cols, spec-aligned)
│
├── setup/                                ← one-shot bootstraps (not Flyway)
│   ├── inventoryledger_schema.sql        DDL-only
│   ├── inventoryledger_full.sql          schema + functions + triggers + views
│   ├── mock_data.sql                     test seed for masters + staging
│   └── mock_staging_data.sql
│
└── test/                                 ← pgTAP suites
    ├── 01_schema_and_rls.sql
    ├── 02_audit_triggers.sql
    ├── 03_load_opening_balance.sql
    ├── 04_post_transaction.sql
    ├── 05_upsert_order.sql
    ├── 06_reconcile_orders.sql
    ├── 07_calculate_fetch.sql
    ├── 08_pg_cron_helpers.sql
    ├── 09_guerrilla.sql
    ├── 10_adversarial.sql
    └── README.md
```

---

## What lives where

### Schemas

| Schema | Purpose |
|---|---|
| `staging` | Landing zones (`stocklevel_inbox`, `txn_inbox`, `order_inbox`) — INSERT-only from outside; drained by stored functions |
| `public` | Live ledger (`inv_transaction`, `stock_balance`, `opening_balance`, `sfdc_order`, `sfdc_order_line`, `tenant`, masters) |
| `audit` | Append-only `audit_log` with JSONB before/after, populated by triggers |
| `archive` | Cold rows moved by `pg_cron` after the retention window |

### Functions

| Function | Caller | Purpose |
|---|---|---|
| `load_opening_balance(batch_id)` | MicroService (Java) | Promote `staging.stocklevel_inbox` rows into `opening_balance` + stamp `stock_balance` |
| `process_txn_inbox()` | `pg_cron` | Drain `staging.txn_inbox` (rows written by FOP) into `inv_transaction` |
| `process_order_inbox()` | `pg_cron` | Drain `staging.order_inbox` (rows written by SFDC) into `sfdc_order_*` |
| `post_transaction(jsonb)` | Reserved / not exposed today | Single-transaction posting path |
| `post_transaction_bulk(jsonb[])` | Reserved | Bulk posting |
| `upsert_order(jsonb)` | Reserved | Idempotent order upsert state machine |
| `reconcile_orders()` | `pg_cron` | Stamp `fop_synced_at`, free ATP |
| `calculate_inventory(...)` | Internal | On-demand recompute path |
| `refresh_mv_atp()` | `pg_cron` | Refresh materialized view |
| `fetch_inventory(...)` | Other teams (read) | Returns on-hand / reserved / allocated / pending / ATP |
| `fetch_pending_orders(...)` | Other teams (read) | Returns orders awaiting FOP sync |
| `notify_outbox(...)` | All paths | Append a row to `notification_outbox` for MicroService to drain |

### Triggers

- `inv_transaction` insert → update `stock_balance` in the same transaction, mark `mv_atp_dirty`
- `opening_balance` insert → initialize `stock_balance`
- Audit triggers on every live table → append to `audit.audit_log`

---

## Build / apply

```bash
# Apply at JAR startup (the standard path):
#   - MicroService runs Flyway against db/migration/ at boot

# Or apply manually for a fresh dev environment:
createdb inventoryledger
psql -d inventoryledger -f db/setup/inventoryledger_full.sql
psql -d inventoryledger -f db/setup/mock_data.sql        # optional seed
```

For Azure Flexible Server, `pg_partman` and `pg_cron` must be on the
allowlist (`azure.extensions = pg_partman, pg_cron, pgcrypto`).

---

## Test

```bash
# Local pgTAP run against a fresh container
docker run --rm -d --name il-pgtap -p 55432:5432 \
    -e POSTGRES_PASSWORD=postgres ghcr.io/tenthplanet/inventoryledger-pg:test
psql -h localhost -p 55432 -U postgres -d inventoryledger \
    -f db/setup/inventoryledger_full.sql
pg_prove -h localhost -p 55432 -U postgres -d inventoryledger db/test/*.sql
docker stop il-pgtap
```

A subset also runs from the Java side via `PgTapSuiteIT.java` against
Testcontainers — see `filemanager-core/src/test/`.

---

## Boundaries

### Calls into us (we publish the contract)
- MicroService → `load_opening_balance(batch_id)`
- FOP → `INSERT INTO staging.txn_inbox (...)` — schema is our contract
- SFDC → `INSERT INTO staging.order_inbox (...)` — schema is our contract
- SFDC read path → `SELECT * FROM fetch_inventory(...)`

### Calls out from us (we consume nothing)
- `notify_outbox(...)` writes to `notification_outbox`; the MicroService reads/drains
- No HTTP calls. No outbound dependencies.

If a foreign caller breaks one of our published contracts (sends a row with
the wrong shape), the drain function rejects it loudly via
`notification_outbox` and rolls back its own work. Callers are responsible for
re-trying with corrected data.
