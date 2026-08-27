---
title: Customer releases
description: v1 through v6 — what each tag added and why.
---

Six tags under `deploy/`. Each is a self-contained schema snapshot. Post-v6, additive features ship as `alter_<name>.sql` files inside `deploy/11-06-v6-customer/` — no new tag folders unless a schema break demands one.

See [Evolution log](/database/evolution) for the full rationale trail. See [Customer installs](/operations/customer-installs) for how to apply each tag.

## Timeline

```
2026-06-05  v1  single-trigger baseline
            v2  three-trigger cascade model
2026-06-08  v3  subinventory + stock_status dimensions (POOL pattern)
2026-06-11  v4  per-subinv reservations, append-only inboxes, UUID→TEXT
            v5  auto-promote triggers on inbox INSERTs
            v6  line-precise cascade via erp_external_id, partial shipments  ← ACTIVE
2026-06-29  alter_01–04 land on top of v6 (observability event log)
```

## v1 — `05-06-v1-customer/`

First customer-facing fresh install. Three-schema layout, single trigger.

**Highlights:**

- `processed` / `staging` / `audit` schemas
- Single trigger on `opening_balance INSERT` → `stock_balance`
- Per-tag folder layout (not branches)
- `pgcrypto` avoided (Azure Flexible allowlist)

## v2 — `05-06-v2-customer/`

Full cascade model — orders reserve stock, shipments close lines.

**Highlights:**

- Three trigger functions (`f_stock_balance_opening_apply`, `f_stock_balance_txn_apply`, `f_stock_balance_reservation_apply`)
- Cascade releases reservation on line close, idempotently within one transaction
- `FOR UPDATE SKIP LOCKED` in drain functions
- `SECURITY DEFINER` + explicit `SET search_path` on every function
- `notify_outbox` as a function (not direct table INSERT) — enables dedup

## v3 — `08-06-v3-customer/`

Track by subinventory + stock_status, not just warehouse.

**Highlights:**

- `subinventory` + `stock_status` added to `opening_balance`, `stock_balance` (in PK), `inv_transaction`
- **POOL pattern** — reservations stored on a `subinventory='POOL'` sentinel row (workaround for SFDC's initial reservation model)
- `fetch_inventory` becomes parameter-flexible: tenant required, others optional
- `recalculate_stock_balance` rebuilds on the new key shape

## v4 — `11-06-v4-customer/`

Subinventory as a first-class reservation dimension; append-only inboxes.

**Highlights:**

- **POOL pattern REMOVED** — SFDC now commits at subinv level; per-row reservations
- `sfdc_order_line` gains `subinventory` + `stock_status` + `sfdc_line_id`
- `inv_transaction` gains `sfdc_line_id`
- `opening_balance` gains `source_file` for replay
- **Append-only inboxes** — dropped UNIQUE on `(tenant_code, external_txn_id)` / `(tenant_code, sfdc_order_id)`. FOP / SFDC append; drain picks latest
- `'superseded'` added to inbox `status` CHECK
- **No `ON CONFLICT DO UPDATE`** in drain functions — DELETE+INSERT / WHERE NOT EXISTS patterns

Applied via `upgrade_from_v3.sql` (883 lines, self-contained, idempotent, includes a reservation rescue step).

### v4+ addendum — UUID → TEXT

Same day as v4. Every `tenant_id` and cross-system identifier column becomes TEXT. `gen_random_uuid()::text` for auto-gen. Trigger functions unchanged (they bind via `NEW.tenant_id` row types).

Rule: no UUID columns in `processed`, `staging`, or `audit`.

## v5 — `11-06-v5-customer/`

Event-driven promotion — every INSERT into staging inboxes auto-promotes.

**Highlights:**

- `trg_txn_inbox_promote` (AFTER INSERT FOR EACH ROW) → `staging.f_promote_txn()`
- `trg_order_inbox_promote` (AFTER INSERT FOR EACH ROW) → `staging.f_promote_order()`
- **`BEGIN/EXCEPTION` inside each trigger** — original INSERT never fails due to downstream issues; staging row terminates in `processed` or `rejected` with `processed_at`
- `processed.sfdc_order` — **UPSERT** (was v4 DELETE+INSERT). Preserves `created_at`; refreshes `updated_at`; tracks `received_at`
- `processed.sfdc_order_line` — **still DELETE+INSERT** (line_no re-numbers across re-posts)
- `load_transactions` / `load_orders` retained as drain fallback

The "trigger absorbs its own errors" contract: FOP fires and forgets — no scheduler needed for transactions/orders on the FOP side.

Applied via `upgrade_from_v4.sql` (purely additive — no table or constraint changes).

## v6 — `11-06-v6-customer/` (ACTIVE)

Correct multi-line orders and partial shipments. Line-precise cascade.

**Highlights:**

- `sfdc_order_line.erp_external_id` — FOP/Oracle SO line ID (`SOURCE_LINE_ID`)
- `inv_transaction.erp_line_id` — matches `sfdc_order_line.erp_external_id`
- `sfdc_order.erp_external_id` — SO header ID
- `inv_transaction.erp_header_id` — matches SO header
- `sfdc_order_line.shipped_qty NUMERIC NOT NULL DEFAULT 0` — line auto-closes when `shipped_qty >= qty`
- `f_stock_balance_txn_apply` rewritten — match precedence `erp_line_id > sfdc_line_id > sfdc_order_id`
- `f_stock_balance_reservation_apply` rewritten — unified formula `target = (qty - shipped_qty)` when active, 0 when not; delta = `NEW_target − OLD_target`
- `load_transactions` / `load_orders` collapsed to ~30-line delegators around `staging.promote_one_*`

### Resolves Q2 (open since v4)

`inv_transaction.erp_line_id ↔ sfdc_order_line.erp_external_id`. Michelin confirmed FOP order-line ID flows both ways.

### Proven partial-shipment math

Two-line order, four shipment txns, correct final state per line. See [Evolution log — v6](/database/evolution#v6).

## Post-v6 alter files

Applied on top of `customer_install.sql`. All idempotent.

| Alter | What it adds |
|---|---|
| `alter_01_inbox_promote_triggers.sql` | (v6 promotion triggers — also in customer_install.sql; alter is a v5→v6 diff) |
| `alter_02_fetch_inventory_json.sql` | Introduces `fetch_inventory_json` function |
| `alter_03_fetch_inventory_json_format.sql` | JSON shape refinement — adds `available` field, adjusts filter defaults |
| `alter_04_audit_event_log.sql` | `audit.event_log` + 5 derived views + `v_all_events` union + `fetch_inventory_json_observed` observing wrapper. See [Observability](/architecture/observability) |

## What's frozen and what isn't

**Frozen** in the tag folder (do not modify after release):

- `customer_install.sql`
- `customer_seed.sql`
- `upgrade_from_v<prev>.sql`
- `test_v6_*.sql`
- `CUSTOMER-DEPLOYMENT.md`

**Additive** on top:

- New `alter_<N>_<name>.sql` files (numerically ordered, apply after customer_install.sql)
- New test files if new alters land

**When a new tag becomes necessary:** a schema break that isn't safely expressible as an additive alter — column rename, table rename, PK change, constraint tightening. Rare.

## Handoff artifacts

For every release cut:

- The tag folder itself (in-repo)
- A signed-off `CUSTOMER-DEPLOYMENT.md` inside the folder (customer's DBA reads this)
- Any test reports (`TEST-REPORT-v6.md` for the latest)
- An observability dashboard render if relevant (`OBSERVABILITY-DASHBOARD.html` in v6)

Handoff bundles for the JAR (like `inventoryledger-devops-2026-07-16/`) are separate — they ship the compiled JAR plus supporting docs for the JAR deploy, not the schema deploy.
