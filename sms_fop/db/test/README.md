# pgTAP test suite

SQL-side test cases (TC-100 through TC-195) covering schema, RLS, audit, triggers, stored functions, and pg_cron helpers.

## Where it runs
- **Local dev** — Postgres with pgTAP extension installed
- **CI** — Testcontainers Postgres image with pgTAP pre-installed
- **NOT in production** — pgTAP is not on Azure Flexible's `azure.extensions` allowlist (deliberate; tests never run against prod)

## Files
| File | TC range | Description |
|---|---|---|
| `01_schema_and_rls.sql` | TC-100 — TC-116 | schemas, partitioning, RLS isolation policies |
| `02_audit_triggers.sql` | TC-120 — TC-128 | audit log triggers, stock_balance, mv_atp_dirty |
| `03_load_opening_balance.sql` | TC-130 — TC-139 | file→staging→live flow |
| `04_post_transaction.sql` | TC-140 — TC-149 | transaction insert path |
| `05_upsert_order.sql` | TC-150 — TC-154 | SFDC order upsert + state machine |
| `06_reconcile_orders.sql` | TC-160 — TC-164 | reconciler trigger |
| `07_calculate_fetch.sql` | TC-170 — TC-177 | calculate_inventory + fetch + MV |
| `08_pg_cron_helpers.sql` | TC-190 — TC-195 | purge, drift, pipeline_config |

Each file is one transaction (BEGIN/ROLLBACK) so it leaves the DB unchanged.

## Prerequisites
1. Database with V1, V2, V3 migrations applied
2. `pgTAP` extension installed: `CREATE EXTENSION IF NOT EXISTS pgtap;`
3. Recommended: `pg_prove` CLI (from `cpan TAP::Parser::SourceHandler::pgTAP`)

## Run
```bash
# All files, summary output
pg_prove -d inventoryledger -U postgres db/test/*.sql

# Single file via psql
psql -d inventoryledger -U postgres -f db/test/01_schema_and_rls.sql

# Via Testcontainers (in Java test code)
# See integration tests in filemanager-core/src/test/java/.../integration/PgTapSuiteIT.java
```

## Conventions
- Every test description starts with the TC-ID (e.g. `'TC-100: schemas exist'`)
- Setup happens inside the BEGIN block before `plan(N)`
- ROLLBACK at end of every file — no test leaks data
- Tests that need real REFRESH MV (cannot run inside a transaction) are split into setup + assertion sections and noted in comments
