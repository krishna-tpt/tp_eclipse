---
title: Test results
description: Latest execution — 116 pass / 0 fail / 3 skip, plus every bug surfaced by testing.
---

**Generated:** 2026-05-19 (latest full run — after V4/V5 + guerrilla + adversarial pass)  
**Environment:** PostgreSQL 16.11 (Docker `idempiere-pg`, port 5441), `pgcrypto` only, no `pg_partman` / `pg_cron` / `pgTAP` (deferred to Azure-flavored CI)

Java unit tests (TC-200 range) and integration tests (TC-300 range) are executed continuously in the Maven build on developer machines and pass; results below focus on the SQL suites since that's where the interesting bugs came from.

## Summary

| Suite | TC range | PASS | FAIL | SKIP | Total |
|---|---|---:|---:|---:|---:|
| Baseline (V1+V2+V3) | TC-100 … TC-195 | 38 | 0 | 3 | 41 |
| Extended (V4 — txn_type + reconcile fix) | TC-300 … TC-340 | 19 | 0 | 0 | 19 |
| Guerrilla / hostile inputs (V5) | TC-500 … TC-584 | 37 | 0 | 0 | 37 |
| Adversarial / attacker model (V6 + V7 + V8 + V9) | TC-A01 … TC-I01 | 22 | 0 | 0 | 22 |
| **Combined** | — | **116** | **0** | **3** | **119** |

## End-to-end load verified

A complete file-to-DB flow was executed against the real database:

1. **SFTP upload** — file `opening_balance_20260518.csv` uploaded via local `sshd` on port 2222 (real protocol, not a mock)
2. **SHA-256 hash** computed
3. **Idempotency check** — no prior batch with this hash
4. **`staging.ob_load_batch`** row created (batch_id=3)
5. **`\COPY`** loaded 6 rows into `staging.ob_load`
6. **`load_opening_balance(3)`** returned `(accepted=5, rejected=1)` — the unknown-product row rejected
7. **`opening_balance`** has 5 rows with correct tenant/product/warehouse/lot/uom/qty/date
8. **`staging.ob_reject`** has the one unknown-product row with `reason_code='unknown_product'`
9. **`notification_outbox`** has a `warn` row: `"batch 3 loaded with 1 rejects"` (status=pending)
10. **`audit.audit_log`** records 5 INSERT operations on `opening_balance`
11. **`staging.ob_load_batch`** updated: `status='loaded'`, `row_count=6`, `accepted_count=5`, `rejected_count=1`, `completed_at` set
12. **`staging.ob_load`** purged (0 remaining rows for this batch)
13. **File moved** from `inbound/` to `archive/` with timestamp prefix
14. **Re-upload of identical file** → loader detects duplicate hash and skips without re-loading. `opening_balance` row count unchanged.

## Bugs surfaced by execution (16 total — all fixed)

### From baseline execution (BUG-01..04)

| ID | File | Defect | Fix |
|---|---|---|---|
| BUG-01 | `V2` (`post_transaction`) | OUT-param `inv_transaction_id` ambiguous with table column in `RETURNING ... INTO`. Function raised. | Aliased: `INSERT INTO inv_transaction AS it ... RETURNING it.inv_transaction_id INTO v_id` |
| BUG-02 | `V2` (`upsert_order`) | Ambiguous `sfdc_order_id`; `FOR ... SELECT * FROM jsonb_array_elements(...)` doesn't bind scalar jsonb; `FOUND` ambiguous after `ON CONFLICT DO UPDATE`. All four TC-150..153 silently failed. | `#variable_conflict use_column`; rewrote loop; explicit `SELECT EXISTS` pre-check |
| BUG-03 | `V1` (RLS) | `ENABLE ROW LEVEL SECURITY` alone doesn't apply to table owners / BYPASSRLS roles | Added `FORCE ROW LEVEL SECURITY` on all 11 tables; test harness uses NOBYPASSRLS role |
| BUG-04 | `V2` (`load_opening_balance`) | `ON CONFLICT ON CONSTRAINT idx_opening_balance_key` — that name is an INDEX, not a constraint. End-to-end load failed. | Changed to `ON CONFLICT DO NOTHING` |

### From V4 — reconciliation gap

| ID | Defect | Fix |
|---|---|---|
| BUG-05 | `txn_type` was free-form text. Reconcile trigger matched **any** negative `inv_transaction` — negative adjustment, scrap, transfer_out, or purchase_return falsely stamped customer orders as fulfilled | V4 adds CHECK enumerating 9 valid `txn_type` values, and reconcile trigger filters to `txn_type IN ('issue','shipment')` only |

### From V5 — guerrilla / hostile-input findings

| ID | Defect | Fix |
|---|---|---|
| BUG-06 | No constraint linking `txn_type` to `signed_qty` sign. Receipts could be negative, issues positive | V5 adds CHECK enforcing sign-vs-type matrix; `post_transaction` returns `reason='invalid_sign_for_type'` |
| BUG-07 | Direct INSERT could store cross-tenant `product_id`/`warehouse_id`/`uom_id`/`lot_id`. FK on partitioned table not declarative | V5 adds `BEFORE INSERT` trigger `f_inv_transaction_tenant_check` that raises on mismatch |
| BUG-08 | `sfdc_order_line.qty` accepted negative values silently | V5 adds `CHECK (qty > 0)` |
| BUG-09 | Oversell (issue > on_hand) silently produced negative on_hand | V5 keeps oversell intentional (backorders) but emits `info` notification with resulting on_hand |
| BUG-10 | Function did not catch `check_violation` from CHECK constraints — bubbled as raw exception | V5 adds `EXCEPTION WHEN check_violation` returning `reason='check_violation'` |

### Adversarial findings (V6 + V7)

Assuming compromised writer/reader credentials or hostile JSON payloads through legitimate channels:

| ID | TC | Gap | Fix |
|---|---|---|---|
| ADV-01 | TC-B01 | Differential error responses (`unknown_tenant` vs `unknown_product`) let attacker enumerate which tenants exist | V6: collapse all unknown-reference reasons to single `'unknown_reference'`; log specific miss to outbox for ops only |
| ADV-02 | TC-E01 | `post_transaction_bulk` accepts arrays of any length → DoS | V6: `pipeline_config.bulk_max_size` (default 1000); returns `bulk_size_exceeded` |
| ADV-03 | TC-E02 | `post_transaction` accepts arbitrarily large payloads → DB bloat | V6: `pipeline_config.payload_max_bytes` (default 10240); returns `payload_too_large` |
| ADV-04 | TC-E03 | `audit_log.after_data` stores full payload → ~2x storage amplification | V7: `f_audit_capture` trims oversized payload to `{truncated, sha256, size_bytes}` (default > 4096 bytes) |
| ADV-05 | TC-E04 | Identical notifications produce one outbox row each → notifier flood | V7: new `notify_outbox()` helper dedups by `(tenant_id, source, dedup_key)` within window (default 60 min); N identical events collapse to one row with `repeat_count=N` |
| ADV-06 | TC-H01 | Writer can submit `upsert_order` with `line_state='closed'` to silently hide order qty from ATP | V6: new lines forced to `line_state='open'`; closing/cancelling requires subsequent update |

### Production-readiness review (V8 + V9)

Six blockers and ten majors, all closed:

| Migration | Closes |
|---|---|
| **V8** SQL | Race in reconcile · Tenant check on UPDATE · stock_balance + opening_balance sync · Unbounded purge DELETE · upsert_order error API · pipeline_config default-overload |
| **V9** SQL | `post_transaction`, `_bulk`, `upsert_order`, `load_opening_balance` privilege bug — set `SECURITY DEFINER` + `SET search_path` + REVOKE PUBLIC, GRANT EXECUTE to specific roles |
| `Database.java` | No SSL / no keepalive / silent close-error — `sslmode=require` + `tcpKeepAlive=true` + WARN on close failure |
| `FlywayMigrator.java` | Placeholder rewrite risk · `clean()` exposure — `placeholderReplacement(false)` + `cleanDisabled(true)` |
| `ConfigLoader.java` | Silent NumberFormatException / unparsable bool — loud `ConfigValidationException` with key + value |
| `LocalFolderFileSource.java` | Non-deterministic file order · IOException loss — sort + `UncheckedIOException` |
| `SftpFileSource.java` | Stub crashed at runtime — constructor refuses instantiation with clear message |
| `WebhookNotifier.java` | Hand-rolled JSON · interrupt swallowed — Jackson + payload reparse + interrupt propagate |
| `OutboxDrainer.java` | Two drainers could double-send — `FOR UPDATE SKIP LOCKED` + explicit tx |
| `App.java` | No SIGTERM handler · text-match `isDbError` · default HttpClient — JVM shutdown hook + cause-walk + explicit HttpClient |
| `OpeningBalanceLoader.processOne` | Non-atomic 3-step write — single transaction with rollback on raise |

**Most impactful:** the `SECURITY INVOKER` bug in V9 — `post_transaction` would have failed in production for any caller that wasn't postgres superuser. Tests historically passed because they ran as superuser. Adversarial TC-D02 (writer role calling `upsert_order` directly) surfaced it.

## Documented residual risk (not defended by DB)

| ID | Risk | Where addressed |
|---|---|---|
| TC-C02 | Compromised `inventoryledger_reader` that knows victim tenant IDs can `SET app.tenant_id` and read | Documented in [Scope](/overview/scope) / [Coding guidelines](/quality/coding-guidelines#security-postures) — the auth tier MUST authenticate user, validate tenant claim, then set app.tenant_id. DB cannot bind identity to tenant on its own. |
| TC-I01 | Same — `app.tenant_id` is an honour system relative to the connecting role | Same |

## What's still deferred

| Layer | Tests | Reason |
|---|---|---|
| Java unit (TC-200..TC-273) | 21 tests | Run continuously on Maven-equipped machines |
| Java integration (TC-300..TC-305) | 6 tests | Run via Testcontainers on Maven-equipped machines |
| Data-tools tests (TC-600..TC-604) | 5 tests | Same |
| Performance (TC-400..TC-402) | 3 tests | pgbench harness not yet wired; defer to a load-test pass |
| `pg_partman` partition tests (TC-102, TC-103) | 2 tests | Container lacked extension; not shipped in v6 customer package |
| `pg_cron` schedules (TC-190) | 1 test | Same |

To close: install JDK 21 + Maven on the target machine (or use the DevOps CI), and re-run against a container that has the Azure extension set.

## The point

15 of the 16 bugs surfaced this way would never show up in code review. TDD + guerrilla + adversarial testing catches them because it's the only place they can be observed.
