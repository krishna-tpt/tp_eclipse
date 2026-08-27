# Database — Evolution Log

Cross-tag chronology of customer-side schema and behavior decisions.
Each tag's own `CUSTOMER-DEPLOYMENT.md` has the runbook; this file is the
**rationale** trail across tags.

```
v1  2026-06-05   single trigger; warehouse-level stock_balance
v2  2026-06-05   3-trigger cascade model + reservation flow
v3  2026-06-08   subinventory + stock_status as first-class dimensions
v4  2026-06-11   per-subinv reservations + append-only inboxes
v4+ 2026-06-11   UUID → TEXT across all identifier columns
v5  2026-06-11   auto-promote triggers on inbox INSERTs; UPSERT sfdc_order
v6  2026-06-11   line-precise cascade via erp_external_id; partial shipments
```

---

## v1 — `deploy/05-06-v1-customer/`  (2026-06-05)

**Goal:** customer-facing fresh install. First DB-only package.

| Decision | Rationale |
|---|---|
| Three-schema layout: `processed` (live), `staging` (inboxes), `audit` (log) | Crisp boundary between landing-zone tables (writeable by anyone) and live ledger (writeable only via drain functions) |
| Single trigger on `opening_balance INSERT` → `stock_balance` | Smallest moving part to start with; advanced cascade pushed to v2 |
| `pgcrypto` extension **NOT** required | Azure Flexible Server doesn't allow-list it; `gen_random_uuid()` is built-in PG13+ |
| Per-tag folder layout under `deploy/`, not branches | Side-by-side comparison via `diff -r`; customer can ship as zip; tag = folder |

---

## v2 — `deploy/05-06-v2-customer/`  (2026-06-05)

**Goal:** model the full cascade — orders reserve stock, shipments close the
order line, reservation releases automatically.

| Decision | Rationale |
|---|---|
| Three trigger functions: `f_stock_balance_opening_apply`, `f_stock_balance_txn_apply`, `f_stock_balance_reservation_apply` | One trigger per data event keeps each function focused and testable |
| Reservation via cascade: shipment txn UPDATEs `sfdc_order_line.line_state='closed'`, which fires trigger 3 to release | Idempotent through a single DB transaction. No cron, no janitor. |
| `FOR UPDATE SKIP LOCKED` in drain functions | Parallel-safe — multiple workers can drain concurrently without lock contention |
| Drop the legacy `staging.ob_load` tables — **deferred** | V1/V2/V3/V8/V11/V13 migrations reference them; not worth the refactor; sit unused at runtime |
| SECURITY DEFINER + explicit `SET search_path` on every function | Protects against search-path attacks; matches Postgres security guideline |
| `notify_outbox` is a function, not a table directly INSERTed | Lets the function dedupe by `dedup_key` (repeat_count bumps existing rows) |

---

## v3 — `deploy/08-06-v3-customer/`  (2026-06-08)

**Goal:** track sales by subinventory + stock_status, not just by warehouse.

| Decision | Rationale |
|---|---|
| Added `subinventory` + `stock_status` to `opening_balance`, `stock_balance` (in PK), `inv_transaction` | Customer needed visibility into which storage location (ONHAND, US01, etc.) held what stock |
| **POOL pattern** — reservations stored on `subinventory='POOL'` sentinel row, warehouse-level | SFDC's first design didn't commit at subinv granularity; pooled reservation kept ATP math right at the warehouse level |
| `fetch_inventory` becomes parameter-flexible: tenant required, product/warehouse/subinventory optional, stock_status defaults LIBERATED | One read API across 5 query shapes; SFDC's quote/order screens can call it however they need |
| `recalculate_stock_balance` rebuilds on the new key shape | Manual recovery path for back-dated opening_balance or trigger failures |
| `'ALL'` value for `p_stock_status` disables the filter | Audit views need to see BLOCKED stock too — special-case the wildcard |

---

## v4 — `deploy/11-06-v4-customer/`  (2026-06-11)

**Goal:** subinventory as a first-class reservation dimension; append-only inboxes; no UPSERT in the drain path.

### Architectural shifts

| Decision | Rationale |
|---|---|
| **POOL pattern REMOVED** | Michelin team confirmed SFDC commits at subinv level (`Storage_Location__c` + `Location_External_Id__c`). The pooled-reservation workaround isn't needed; per-row reservations give better operational visibility |
| `sfdc_order_line` gains `subinventory` + `stock_status` + `sfdc_line_id` | Carries the reservation dimension end-to-end; `sfdc_line_id` is a Q2 placeholder for the future Oracle ↔ SFDC line link |
| `inv_transaction` gains `sfdc_line_id` | Same placeholder, on the shipment side |
| `sfdc_order` gains `received_at` | Audit trail of when the SFDC payload arrived, distinct from when we wrote to processed |
| `opening_balance` gains `source_file` | Replay key — DELETE-by-source_file enables clean file replay |

### Append-only inbox contract

| Decision | Rationale |
|---|---|
| Drop `UNIQUE (tenant_code, external_txn_id)` on `staging.txn_inbox` | FOP can re-INSERT the same business key on every state change — no constraint to fight |
| Drop `UNIQUE (tenant_code, sfdc_order_id)` on `staging.order_inbox` | Same — SFDC pushes every change as a new inbox row |
| Add `'superseded'` to inbox `status` CHECK | Drain marks older pending rows of the same key as superseded — keeps history without ambiguity about which was processed |
| Drain picks latest by `received_at DESC` (DISTINCT ON) | Latest-wins semantics; FOP doesn't need to delete or update earlier rows |
| **No `ON CONFLICT DO UPDATE`** in `load_orders`, `load_transactions`, `load_stocklevel` | "Avoid UPSERTs" directive. `load_orders` does `DELETE` then `INSERT` (cascade trigger handles reservation release); `load_transactions` uses `WHERE NOT EXISTS`; `load_stocklevel` does `DELETE WHERE batch_id = existing` then INSERT |
| Trigger functions **keep** `ON CONFLICT` for `stock_balance` maintenance | Narrow reading of "no UPSERTs" — those are internal materialization of the running aggregate, not the inbox→processed contract |

### Reservation behavior change (v3 → v4)

| Concern | v3 (POOL) | v4 (per-row) |
|---|---|---|
| Where reservation lands | `subinventory='POOL'` sentinel row | The actual `(subinv, stock_status)` row |
| `fetch_inventory(...,'ONHAND')` reserved column | 0 (visible only via POOL) | Real per-subinv value |
| ATP across warehouse | Correct (single subtraction) | Correct (per-row, sums identically) |
| `UPDATE` that changes subinv | Trigger updated POOL only | Trigger releases OLD row + reserves NEW row |
| stock_balance row count for one warehouse | Reals + 1 POOL | Just reals |

### Cascade matching — Q2 still open

| Decision | Rationale |
|---|---|
| Cascade still matches on `sfdc_order_id` (not `sfdc_line_id`) | Awaiting Michelin's confirmation on how Oracle's `SOURCE_LINE_ID` translates to SFDC's `Id`. The `sfdc_line_id` columns are nullable placeholders so future swap is one WHERE-clause change |

### Upgrade path

| Decision | Rationale |
|---|---|
| `upgrade_from_v3.sql` is self-contained (883 lines) | Operator runs one file; no `\i` dependencies; idempotent (safe to re-run) |
| **Reservation rescue step** after `recalculate_stock_balance` | v3 order lines have `subinventory=''` after the ALTER. `recalculate`'s LEFT JOIN from `opening_balance` drops them; the rescue writes them back as `''`-subinv rows so warehouse ATP stays correct |

---

## v5 — `deploy/11-06-v5-customer/`  (2026-06-11)

**Goal:** event-driven promotion — every INSERT into the staging inboxes auto-promotes to processed via per-row triggers. No scheduler needed for transactions/orders.

| Decision | Rationale |
|---|---|
| `trg_txn_inbox_promote` (AFTER INSERT FOR EACH ROW on `staging.txn_inbox`) calls `staging.f_promote_txn()` | FOP "fires and forgets" — no `pg_cron` schedule, no JAR call for transactions. Per-row trigger fits the typical low-burst arrival pattern |
| `trg_order_inbox_promote` (AFTER INSERT FOR EACH ROW on `staging.order_inbox`) calls `staging.f_promote_order()` | Same for orders. SFDC posts each work-order change as a new inbox row; trigger promotes it immediately |
| Triggers fire **only on INSERT** (not UPDATE) | Trigger's own internal `UPDATE` of the staging row (to set `status` and `processed_at`) does not recurse |
| `BEGIN/EXCEPTION` block inside each trigger function | Original INSERT NEVER fails because of downstream issues (unknown master, validation failure). Staging row ends in terminal state (`processed` or `rejected`) with non-NULL `processed_at` |
| `processed.sfdc_order` — UPSERT (`ON CONFLICT (sfdc_order_id) DO UPDATE`) instead of v4's DELETE+INSERT | Preserves `created_at` across re-posts; `updated_at = now()` refreshes; `received_at` tracks latest inbox arrival. Three timestamps, three meanings |
| `processed.sfdc_order_line` — still DELETE+INSERT (not UPSERT) | `line_no` may re-number across re-posts (SFDC drop/add/reorder); per-line UPSERT is more complex than DELETE+INSERT, and the cascade trigger handles reservation release/reserve correctly either way |
| `load_transactions` and `load_orders` **RETAINED** as drain fallback | Catch-up after temporary trigger outage; operator reprocessing batch. Vestigial in normal operation; `load_orders` body now also uses UPSERT for consistency with the trigger |
| Same payload contract as v4 — lowercase snake_case keys | FOP / SFDC normalize Oracle EBS / Salesforce native shapes before INSERT. Mapping not done in our SQL |
| `upgrade_from_v4.sql` is purely additive | No table changes, no constraint changes, no data migration. Functions + triggers only |

### Cross-tag behavior contract — "trigger absorbs its own errors"

| Scenario | Before v5 (drain model) | After v5 (trigger model) |
|---|---|---|
| FOP INSERTs a txn with unknown product | INSERT succeeds → staging row sits with `status='pending'` until next `pg_cron` tick of `load_transactions` → moves to `rejected` | INSERT succeeds → trigger fires → catches lookup miss → staging row in `rejected` status with `reject_reason` populated, ALL within the same statement |
| FOP INSERTs a txn that triggers an FK violation | Same delay | Same instant feedback (caught in trigger's EXCEPTION block) |
| FOP INSERT itself fails | n/a — failure was in FOP's transaction | Still impossible — the trigger absorbs failures so the INSERT succeeds |

## v6 — `deploy/11-06-v6-customer/`  (2026-06-11)

**Goal:** correct multi-line orders and partial shipments. Replaces v5's header-level cascade with line-precise matching via the FOP/Oracle order-line ID.

| Decision | Rationale |
|---|---|
| Add `sfdc_order_line.erp_external_id` (FOP/Oracle SO line ID) + `inv_transaction.erp_line_id` | Michelin confirmed: SFDC sends the FOP line ID as `erpExternalId__c`; Oracle txns carry it as `SOURCE_LINE_ID`. Matching on this string gives line-precise cascade |
| Add `sfdc_order.erp_external_id` (header ID) + `inv_transaction.erp_header_id` | Symmetric header-level cross-check (Oracle `TRANSACTION_SOURCE_ID`). Supports full-order operations even when line ID not yet synced |
| Add `sfdc_order_line.shipped_qty NUMERIC NOT NULL DEFAULT 0` | Tracks cumulative shipped quantity per line. Line auto-closes when `shipped_qty >= qty` |
| `f_stock_balance_txn_apply` rewritten — match precedence: `erp_line_id` > `sfdc_line_id` > `sfdc_order_id` (header fallback) | Line ID is most precise; header fallback retained for normalized payloads with single-line orders |
| `f_stock_balance_reservation_apply` rewritten — `target = (qty - shipped_qty)` when active, 0 when not; delta = NEW_target − OLD_target | One unified formula handles INSERT, partial UPDATE, close, cancel, subinv-move. Reservations decrement incrementally per partial — `fetch_inventory` reflects "what's still committable" accurately mid-shipment |
| `load_transactions` / `load_orders` collapsed to ~30-line delegators that PERFORM `staging.promote_one_*` | Single source of truth for parse logic — same code runs from trigger and from drain fallback |
| Match precedence handles three payload shapes: native Oracle, native SFDC, normalized snake_case | Backward-compatible — no payload shape change required to apply v6 |

### Resolves the open Q2 from prior tags

| Tag | Q2 status |
|---|---|
| v4 | Open — placeholder `sfdc_line_id` columns added |
| v5 | Open — cascade matches at sfdc_order_id header level (broken for multi-line) |
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

## v4+ — UUID → TEXT  (2026-06-11)

**Goal:** simplify the identifier story across all tables.

| Decision | Rationale |
|---|---|
| **Every `tenant_id` column becomes TEXT** (12 columns across `processed` + `audit`) | Cross-system identifiers (SFDC `sfRecordId__c`, Oracle integers, files.com paths) flow in as strings — homogeneous schema |
| `processed.tenant.tenant_id` default: `gen_random_uuid()::text` | Auto-generated values still look UUID-shaped, but the column type is TEXT |
| `inv_transaction.transfer_pair_id` also UUID → TEXT | Same rule applied consistently |
| `notify_outbox` signature: first param `p_tenant_id UUID` → `TEXT` | Drop + recreate (CREATE OR REPLACE can't change argument types) |
| 4 drain functions: `DECLARE v_tid UUID` → `TEXT` | Variable types must match column types or `SELECT INTO` fails |
| Trigger functions: **unchanged** | They bind via `NEW.tenant_id` row types — automatically adapt to the new column type |
| Applied to customer DB via `alter_uuid_to_text.sql`; `customer_install.sql` updated for future fresh installs | Customer doesn't need to re-run anything; future installs are TEXT-native |

**Engineering rule going forward:** no UUID columns in `processed`, `staging`, or `audit` schemas. Captured in user-memory as a binding feedback rule.

---

## Cross-cutting decisions (apply across all tags)

| Decision | First adopted | Why |
|---|---|---|
| Three schemas: `processed` / `staging` / `audit` | v1 | Trust boundary between landing-zone and live ledger |
| Drop the `pg_cron` scheduling — operator's call | v1 | Customer scheduling tool varies; we ship functions, ops schedules them |
| Notification outbox + webhook drain (Java side) | v1 | Reliable async notifications; survives temporary endpoint failures |
| No JSON-side polymorphism on `inv_transaction.payload` | v3 | Payload is opaque audit data; structure validation happens at staging-insert time, not retrieval |
| Default `search_path` for the database | v1 | Avoid having to type `processed.` everywhere in operator scripts |
| Two-project boundary: Java MicroService + DB | v3 | Java owns SFTP/files.com ingestion + staging INSERT; DB owns everything from staging to fetch_inventory |

---

## Open questions (referenced across tags)

| # | Question | Affects | Status |
|---|---|---|---|
| Q1 | Canonical `sfdc_order_id`? | v4 | Answered 2026-06-11 — `sfRecordId__c` (e.g. `0WO9Z000009f6lpWAA`) |
| Q2 | How does Oracle txn link back to SFDC line? | v4 cascade | **Open** — `sfdc_line_id` columns added as placeholders on `sfdc_order_line` + `inv_transaction` |
| Q3 | Does SFDC commit per subinventory? | v4 design | Answered 2026-06-11 — yes (`Storage_Location__c` + `Location_External_Id__c`) |
| — | Drop legacy `staging.ob_load` tables? | All | Deferred indefinitely — sits unused, doesn't hurt anything |

---

*Last updated 2026-06-11. Source of truth for the per-tag detail: each tag's `CUSTOMER-DEPLOYMENT.md`.*
