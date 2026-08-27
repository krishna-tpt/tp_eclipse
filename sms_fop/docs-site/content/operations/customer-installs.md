---
title: Customer installs
description: v1..v6 tag packages, install order, and the active baseline.
---

The customer database is installed from a **frozen tag folder** under `deploy/`. Each folder is self-contained: schema DDL, seed data, upgrade script from the prior tag, test suites, and a per-tag `CUSTOMER-DEPLOYMENT.md` runbook.

**Active baseline (2026-07):** `deploy/11-06-v6-customer/` + `alter_04_audit_event_log.sql`. This is the frozen production shape. All new features go in as `alter_<feature>.sql` files inside this folder — no new tag folders until a schema break demands one.

## Available tags

```
deploy/
├── 05-06-v1-customer/    single-trigger baseline
├── 05-06-v2-customer/    three-trigger cascade model
├── 08-06-v3-customer/    subinventory + stock_status dimensions (POOL reservations)
├── 11-06-v4-customer/    per-subinv reservations + append-only inboxes
├── 11-06-v5-customer/    auto-promote triggers on inbox INSERTs
└── 11-06-v6-customer/    line-precise cascade via erp_external_id; partial shipments  ← ACTIVE
```

See [Evolution log](/database/evolution) for the rationale trail across tags.

## Fresh install (v6)

```bash
cd deploy/11-06-v6-customer/

# Core schema — tables, functions, triggers
psql -h <host> -U postgres -d <dbname> -f customer_install.sql

# Seed rows (test tenants + masters, optional in prod)
psql -h <host> -U postgres -d <dbname> -f customer_seed.sql

# Observability layer — audit.event_log + views + fetch_inventory_json_observed wrapper
psql -h <host> -U postgres -d <dbname> -f alter_04_audit_event_log.sql
```

Then create the DB role the JAR will connect as:

```sql
CREATE ROLE inventoryledger_app LOGIN PASSWORD '<from-vault>';
GRANT USAGE ON SCHEMA staging TO inventoryledger_app;
GRANT INSERT ON staging.stocklevel_inbox TO inventoryledger_app;
GRANT SELECT, UPDATE ON staging.stocklevel_batch TO inventoryledger_app;
GRANT EXECUTE ON FUNCTION staging.load_stocklevel(TEXT) TO inventoryledger_app;
GRANT USAGE ON SCHEMA processed TO inventoryledger_app;
GRANT SELECT, INSERT, UPDATE ON processed.notification_outbox TO inventoryledger_app;
GRANT USAGE ON SCHEMA audit TO inventoryledger_app;
GRANT INSERT ON audit.event_log TO inventoryledger_app;
GRANT USAGE ON SEQUENCE audit.event_log_event_id_seq TO inventoryledger_app;
```

(Precise grants may vary — check the `customer_install.sql` GRANT block for the current set.)

## Upgrade path from prior tag

Each tag ships a `upgrade_from_v<prev>.sql` when the upgrade is non-trivial:

| Tag | Upgrade script | Nature |
|---|---|---|
| v2 | `upgrade_from_v1.sql` | schema additions + trigger split |
| v3 | `upgrade_from_v2.sql` | + `subinventory`, `stock_status` columns |
| v4 | `upgrade_from_v3.sql` | 883 lines — DROP + ALTER + reservation rescue step |
| v5 | `upgrade_from_v4.sql` | purely additive — functions + triggers only |
| v6 | `upgrade_from_v5.sql` | + `erp_external_id`, `shipped_qty`; trigger rewrites |

Apply the upgrade script for each tag you're jumping through, in order.

## Rollback

A tag is a folder. To roll back, apply the older tag's `customer_install.sql` against a fresh DB. There is no migration history to walk back through — each tag stands alone.

For a live prod DB, rollback typically means:

1. Restore from Azure Postgres PITR (point-in-time recovery).
2. Re-apply from the desired older tag folder.
3. Backfill any data changes from `audit.event_log` if needed.

## Test packs per tag

Each tag ships one or more test SQL files:

| Tag | Test files |
|---|---|
| v6 | `test_v6_full_suite.sql` — the main coverage pack |
| v6 | `test_v6_comprehensive.sql` — earlier iteration, superseded |
| v6 | `test_smoke_native_payloads.sql` — smoke against native Oracle / SFDC payload shapes |

Run against a `postgres:16-alpine` container with the tag's `customer_install.sql` applied.

See [Test results](/quality/test-results) for the latest execution summary.

## `alter_*.sql` — post-v6 additions

Rather than cut a new tag for every additive feature, we ship `alter_<name>.sql` files inside `deploy/11-06-v6-customer/`. Each is idempotent (CREATE IF NOT EXISTS / OR REPLACE) and safe to re-run. See [Coding guidelines](/quality/coding-guidelines#alter-file-conventions) for the discipline.

Currently shipped:

| Alter | Purpose |
|---|---|
| `alter_01_inbox_promote_triggers.sql` | v6 promotion triggers (also present in `customer_install.sql`; alter is a diff for v5→v6) |
| `alter_02_fetch_inventory_json.sql` | Introduces `fetch_inventory_json` function |
| `alter_03_fetch_inventory_json_format.sql` | JSON shape refinement — adds `available`, adjusts filter defaults |
| `alter_04_audit_event_log.sql` | `audit.event_log` + views + `fetch_inventory_json_observed` wrapper |

Apply in numerical order after `customer_install.sql`.

## Naming convention

```
DD-MM-vN-<audience>/
│  │   │  │
│  │   │  └── audience: customer, dev, staging, …
│  │   └───── revision number within that day (v1, v2, …)
│  └───────── month (numeric)
└──────────── day (numeric)
```

Examples: `05-06-v1-customer` (first customer tag on June 5); `11-06-v6-customer` (sixth customer release on June 11).

## Why folders, not branches

- Folders are visible without git tooling — customers can `ls deploy/`
- A tag can ship as zip/email without anyone resolving refs
- Side-by-side comparison via `diff -r 11-06-v5-customer 11-06-v6-customer`
- "Tag and reuse" workflow: pick the folder you want, deploy it

Git tags / branches can layer on top of this for SCM-level provenance, but they're optional.

## What lives in `deploy/` vs `db/`

| | `deploy/<tag>/` | `db/` |
|---|---|---|
| Purpose | **Frozen** snapshot for customer deployment | Historical bootstraps used during dev |
| Schema layout | `processed` (live tables) | `public` (live tables) — legacy |
| Files | Self-contained per folder | `db/migration/V*.sql` (Flyway), `db/setup/mock_data.sql`, `db/test/*.sql` |
| Modify after release? | **No** — make a new alter file or a new tag | Yes — dev tooling |
| Customer-facing? | Yes | No |

If you're cutting a customer release, work under `deploy/`. If you're seeding a dev box, `db/setup/mock_data.sql` is still the right tool.
