---
title: Evolution log
description: v1 → v6 rationale trail across customer schema snapshots.
---

Cross-tag chronology of customer-side schema and behavior decisions. Each tag's own `CUSTOMER-DEPLOYMENT.md` has the runbook; this file is the **rationale** trail across tags.

```
v1  2026-06-05   single trigger; warehouse-level stock_balance
v2  2026-06-05   3-trigger cascade model + reservation flow
v3  2026-06-08   subinventory + stock_status as first-class dimensions
v4  2026-06-11   per-subinv reservations + append-only inboxes
v4+ 2026-06-11   UUID → TEXT across all identifier columns
v5  2026-06-11   auto-promote triggers on inbox INSERTs; UPSERT sfdc_order
v6  2026-06-11   line-precise cascade via erp_external_id; partial shipments
```

## v1 — `deploy/05-06-v1-customer/`

**Goal:** customer-facing fresh install. First DB-only package.

- **Three-schema layout** (`processed` / `staging` / `audit`) — crisp boundary between landing-zone tables and live ledger
- **Single trigger** on `opening_balance INSERT` → `stock_balance`; advanced cascade deferred to v2
- **`pgcrypto` not required** — Azure doesn't allow-list it; `gen_random_uuid()` is built-in PG13+
- **Per-tag folder** layout under `deploy/`, not branches — side-by-side comparison via `diff -r`; customer can ship as zip

## v2 — `deploy/05-06-v2-customer/`

**Goal:** model the full cascade — orders reserve stock, shipments close the order line, reservation releases automatically.

- **Three trigger functions** — `f_stock_balance_opening_apply`, `f_stock_balance_txn_apply`, `f_stock_balance_reservation_apply`. One per data event
- **Reservation cascade** — shipment txn UPDATEs `sfdc_order_line.line_state='closed'`, which fires trigger 3 to release. Idempotent through one DB transaction. No cron, no janitor.
- **`FOR UPDATE SKIP LOCKED`** in drain functions — parallel-safe
- **`SECURITY DEFINER` + `SET search_path`** on every function — protects against search-path attacks
- **`notify_outbox` is a function, not a table** — lets the function dedupe by `dedup_key`

## v3 — `deploy/08-06-v3-customer/`

**Goal:** track sales by subinventory + stock_status, not just by warehouse.

- Added `subinventory` + `stock_status` to `opening_balance`, `stock_balance` (in PK), `inv_transaction`
- **POOL pattern** — reservations stored on `subinventory='POOL'` sentinel row (workaround; removed in v4)
- `fetch_inventory` becomes parameter-flexible: tenant required, product/warehouse/subinventory optional, stock_status defaults `LIBERATED`
- `recalculate_stock_balance` rebuilds on the new key shape
- `'ALL'` value for `p_stock_status` disables the filter — audit views need to see BLOCKED stock too

## v4 — `deploy/11-06-v4-customer/`

**Goal:** subinventory as a first-class reservation dimension; append-only inboxes; no UPSERT in the drain path.

### Architectural shifts

- **POOL pattern REMOVED** — Michelin confirmed SFDC commits at subinv level (`Storage_Location__c` + `Location_External_Id__c`). Per-row reservations give better operational visibility
- `sfdc_order_line` gains `subinventory` + `stock_status` + `sfdc_line_id`
- `inv_transaction` gains `sfdc_line_id`
- `sfdc_order` gains `received_at`
- `opening_balance` gains `source_file` — replay key

### Append-only inbox contract

- Drop `UNIQUE (tenant_code, external_txn_id)` on `staging.txn_inbox` — FOP can re-INSERT the same business key on every state change
- Drop `UNIQUE (tenant_code, sfdc_order_id)` on `staging.order_inbox` — same
- Add `'superseded'` to inbox `status` CHECK
- Drain picks latest by `received_at DESC` (DISTINCT ON)
- **No `ON CONFLICT DO UPDATE`** in `load_orders`, `load_transactions`, `load_stocklevel` — DELETE-then-INSERT / `WHERE NOT EXISTS` patterns

## v4+ — UUID → TEXT

**Goal:** simplify the identifier story across all tables.

- Every `tenant_id` column becomes TEXT (12 columns across `processed` + `audit`)
- `processed.tenant.tenant_id` default: `gen_random_uuid()::text` — auto-generated values still look UUID-shaped, but the column type is TEXT
- `inv_transaction.transfer_pair_id` also UUID → TEXT
- `notify_outbox` signature: first param UUID → TEXT (drop + recreate — CREATE OR REPLACE can't change argument types)
- 4 drain functions: DECLARE variables UUID → TEXT
- Trigger functions unchanged — they bind via `NEW.tenant_id` row types

**Engineering rule:** no UUID columns in `processed`, `staging`, or `audit`. Captured in [Coding guidelines](/quality/coding-guidelines).

## v5 — `deploy/11-06-v5-customer/`

**Goal:** event-driven promotion — every INSERT into staging inboxes auto-promotes to processed via per-row triggers.

- `trg_txn_inbox_promote` (AFTER INSERT FOR EACH ROW on `staging.txn_inbox`) → `staging.f_promote_txn()`
- `trg_order_inbox_promote` (AFTER INSERT FOR EACH ROW on `staging.order_inbox`) → `staging.f_promote_order()`
- Triggers fire **only on INSERT** (not UPDATE) — the trigger's internal `UPDATE` of the staging row does not recurse
- **`BEGIN/EXCEPTION` inside each trigger function** — original INSERT NEVER fails because of downstream issues. Staging row ends in terminal state (`processed` / `rejected`) with non-NULL `processed_at`
- `processed.sfdc_order` — **UPSERT** on `sfdc_order_id` instead of v4's DELETE+INSERT. Preserves `created_at`; `updated_at = now()` refreshes; `received_at` tracks latest inbox arrival
- `processed.sfdc_order_line` — **still DELETE+INSERT** (not UPSERT). `line_no` may re-number across re-posts (SFDC drop/add/reorder)
- `load_transactions` and `load_orders` RETAINED as drain fallback — catch-up after temporary trigger outage; operator reprocessing

### The "trigger absorbs its own errors" contract

| Scenario | Before v5 (drain model) | After v5 (trigger model) |
|---|---|---|
| FOP INSERTs a txn with unknown product | Row sits `pending` until next cron tick → moves to `rejected` | Trigger fires → catches lookup miss → staging row `rejected` with `reject_reason` populated, ALL in the same statement |
| FOP INSERT triggers an FK violation | Same delay | Same instant feedback (caught in trigger's EXCEPTION block) |

## v6 — `deploy/11-06-v6-customer/`

**Goal:** correct multi-line orders and partial shipments. Replaces v5's header-level cascade with **line-precise matching via the FOP/Oracle order-line ID**.

- Add `sfdc_order_line.erp_external_id` (FOP/Oracle SO line ID) + `inv_transaction.erp_line_id`
- Add `sfdc_order.erp_external_id` (header ID) + `inv_transaction.erp_header_id`
- Add `sfdc_order_line.shipped_qty NUMERIC NOT NULL DEFAULT 0` — line auto-closes when `shipped_qty >= qty`
- `f_stock_balance_txn_apply` rewritten — match precedence: `erp_line_id > sfdc_line_id > sfdc_order_id` (header fallback)
- `f_stock_balance_reservation_apply` rewritten — `target = (qty - shipped_qty)` when active, 0 when not; delta = `NEW_target − OLD_target`
- `load_transactions` / `load_orders` collapsed to ~30-line delegators that `PERFORM staging.promote_one_*` — single source of truth for parse logic
- Match precedence handles three payload shapes: native Oracle, native SFDC, normalized snake_case — backward-compatible

### Resolves Q2 (open since v4)

| Tag | Q2 status |
|---|---|
| v4 | Open — placeholder `sfdc_line_id` columns added |
| v5 | Open — cascade matches at `sfdc_order_id` header level (broken for multi-line) |
| **v6** | **Answered** — `inv_transaction.erp_line_id ↔ sfdc_order_line.erp_external_id` (Michelin confirmed FOP order-line ID flows both ways) |

### Partial-shipment math (proven against `postgres:16-alpine`)

Order: 2 lines (P123 qty=5, P456 qty=10). Four shipment txns: 3 of A, 4 of B, 2 of A (closes), 6 of B (closes).

| | P123 on/res/atp | P456 on/res/atp | Lines |
|---|---|---|---|
| Initial | 20/0/20 | 20/0/20 | — |
| After order | 20/5/15 | 20/10/10 | both open, shipped=0 |
| Ship 3 of A | 17/2/15 | unchanged | A: shipped=3 open |
| Ship 4 of B | unchanged | 16/6/10 | B: shipped=4 open |
| Ship 2 more of A | 15/0/15 | unchanged | A: shipped=5 **closed** |
| Ship 6 more of B | unchanged | 10/0/10 | B: shipped=10 **closed** |

ATP holds stable across the cycle. Reserved decrements per shipment. Lines close at exactly the right moment.

## Cross-cutting decisions (across all tags)

| Decision | First adopted | Why |
|---|---|---|
| Three schemas: `processed` / `staging` / `audit` | v1 | Trust boundary between landing-zone and live ledger |
| Drop `pg_cron` scheduling (operator's call) | v1 | Customer scheduling tool varies; we ship functions, ops schedules them |
| Notification outbox + webhook drain (Java side) | v1 | Reliable async notifications; survives temporary endpoint failures |
| No JSON-side polymorphism on `inv_transaction.payload` | v3 | Payload is opaque audit data; structure validation happens at staging-insert time |
| Default `search_path` for the database | v1 | Avoid having to type `processed.` everywhere in operator scripts |
| Two-project boundary (Java MicroService + DB) | v3 | Java owns SFTP/files.com + staging INSERT; DB owns everything from staging to `fetch_inventory` |
| TEXT everywhere for identifiers | v4+ | Cross-system identifiers flow in as strings |

## Post-v6: alter_01 through alter_04

Additive to v6, applied on top of `customer_install.sql`:

| Alter | What it adds |
|---|---|
| `alter_01_inbox_promote_triggers.sql` | (v6-align of promotion triggers) |
| `alter_02_fetch_inventory_json.sql` | `fetch_inventory_json` function |
| `alter_03_fetch_inventory_json_format.sql` | JSON shape refinement (`available` field, filter defaults) |
| `alter_04_audit_event_log.sql` | `audit.event_log` table + 5 views + `v_all_events` union + `fetch_inventory_json_observed` wrapper. See [Observability](/architecture/observability). |

## Open items

| # | Question | Status |
|---|---|---|
| Q1 | Canonical `sfdc_order_id`? | Answered 2026-06-11 — `sfRecordId__c` |
| Q2 | Oracle txn ↔ SFDC line link? | **Answered in v6** — `erp_line_id` ↔ `sfdc_order_line.erp_external_id` |
| Q3 | Does SFDC commit per subinventory? | Answered 2026-06-11 — yes (`Storage_Location__c` + `Location_External_Id__c`) |
| — | Drop legacy `staging.ob_load` tables | Deferred indefinitely — sits unused, doesn't hurt anything |

*Last updated 2026-06-11. Per-tag detail: each tag's `CUSTOMER-DEPLOYMENT.md`.*
