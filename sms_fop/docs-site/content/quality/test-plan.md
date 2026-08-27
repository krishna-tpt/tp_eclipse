---
title: Test plan
description: 100+ test cases mapped to F-IDs and NFRs, across SQL, Java unit, integration, security.
---

Every test case has an ID (`TC-NNN`), maps to a functional requirement (`F-NN`) or NFR from the [spec](/overview/spec), and is executed before code merges.

## Layers and ID ranges

| Range | Layer | Tooling | Location |
|---|---|---|---|
| TC-100–TC-199 | SQL — pgTAP | pgTAP 1.x | `db/test/*.sql` |
| TC-200–TC-299 | Java — unit | JUnit 5 | `filemanager-core/src/test/java/…/unit/` |
| TC-300–TC-399 | Java — integration | JUnit 5 + Testcontainers | `filemanager-core/src/test/java/…/integration/` |
| TC-400–TC-499 | Performance | pgbench + JUnit timer | `db/test/perf/` |
| TC-500–TC-599 | Security / negative | pgTAP + JUnit | mixed |
| TC-600–TC-699 | Synthetic data tools | JUnit 5 | `filemanager-data-tools/src/test/java/` |

A test case is **done** when: code exists, test exists, latest CI run passes, TC-ID maps to a requirement in `SPEC.md`.

## Fixtures

All test data lives in fixture files; **no inline literals** for domain data.

```
filemanager-core/src/test/resources/fixtures/
├── catalog/                        (valid + malformed catalog YAMLs)
├── file_parser/                    (positional record parser fixtures)
├── stocklevel/                     (sample Michelin CFO files)
├── config/                         (valid + missing-key + invalid-cron YAMLs)
└── master_data/seed.sql
```

Synthetic-data-tool outputs go into `filemanager-data-tools/target/generated/` and may be used by integration tests via the `tools.yaml` seed parameter for reproducibility.

## Traceability matrix (requirement → tests)

| Requirement | Covered by |
|---|---|
| F01 file pickup + hash idempotency | TC-220, TC-230, TC-300, TC-301 |
| F02 field validation | TC-205..TC-216, TC-275..TC-277, TC-290..TC-299 |
| F03 bad shape routing | TC-232, TC-302 |
| F04 promote to processed | TC-130..TC-134, TC-231 |
| F05 archive on success | TC-136, TC-300 |
| F06 outbox webhook drain | TC-250..TC-263, TC-300 |
| F07 post_transaction / promote_txn | TC-104, TC-124..TC-127, TC-140..TC-149, TC-303 |
| F09 transfer pair | TC-145 |
| F10 upsert_order / promote_order | TC-150..TC-154 |
| F11 reconcile / cascade | TC-127, TC-160..TC-164, TC-330, TC-331 |
| F12 stock calculation | TC-170..TC-172 |
| F13 fetch_inventory_json | TC-175, TC-176, TC-400 |
| F17 exception → outbox | TC-138, TC-302 |
| Idempotency NFR | TC-104, TC-135, TC-141, TC-147, TC-301, TC-582 |
| Audit NFR | TC-115, TC-120..TC-123 |
| Tenant isolation NFR | TC-105, TC-110..TC-114, TC-500, TC-511 |
| Failure notification NFR | TC-138, TC-302, TC-544 |
| Performance NFR | TC-400, TC-401, TC-402 |
| No-hardcoding rule | TC-192, TC-195, TC-200..TC-204, TC-234 |
| Catalog contract | TC-280..TC-285 (loader), TC-290..TC-299 (parser + detector) |

## SQL — schema and RLS (pgTAP, TC-100 range)

Sample entries:

| TC-ID | Description | Maps to |
|---|---|---|
| TC-100 | All expected schemas (`public`, `staging`, `audit`, `archive`) exist | Schema |
| TC-101 | All core tables exist with expected column types | Schema |
| TC-104 | UNIQUE `(tenant, external_txn_id)` blocks duplicate | F07, Idempotency |
| TC-105 | RLS enabled on all 11 domain tables | Tenant isolation |
| TC-110 | Tenant A cannot see tenant B's products | Tenant isolation |
| TC-112 | RLS WITH CHECK blocks cross-tenant INSERT | Tenant isolation |
| TC-115 | All 10 domain tables have audit triggers attached | Audit |
| TC-116 | All FK columns are indexed | Performance |

## SQL — audit + triggers (TC-120 range)

- TC-120..TC-123: audit rows on INSERT/UPDATE/DELETE, tenant_id captured
- TC-124..TC-127: `inv_transaction` INSERT updates `stock_balance.on_hand_qty` in the same transaction
- TC-128: `sfdc_order_line` UPDATE cascades to `stock_balance.reserved_qty`

## SQL — `load_stocklevel` (TC-130 range)

Loads validation, rejection routing, batch metadata, idempotency, staging purge.

## SQL — `post_transaction` / `promote_txn` (TC-140 range)

Valid receipts, duplicates, unknown_tenant / product / warehouse / uom / lot, invalid_qty, transfer pairs, bulk semantics, concurrent identical posts.

## SQL — `upsert_order` / `promote_order` (TC-150 range)

Insert, update qty, cancel-by-omission, state transitions, idempotency.

## SQL — reconcile / cascade (TC-160 range)

FIFO matching, matched lines skipped, no-eligible-line is no-op.

## SQL — calculate + fetch + MV (TC-170 range)

On-hand math (opening + receipts − issues), ATP subtracts open+synced reservations, closed lines excluded, `fetch_inventory` returns rows.

## Java unit (TC-200 range)

- `Config` / `ConfigLoader` (TC-200..TC-204)
- `FieldMapper` (TC-205..TC-216)
- `InterfaceDefinition` (TC-275..TC-277)
- `Database` / `FlywayMigrator` (TC-210..TC-212)
- `LocalFolderFileSource` / `SftpFileSource` (TC-220..TC-222)
- `OpeningBalanceLoader` (TC-230..TC-234) — legacy CSV-header pipeline, retired but tests retained during migration
- `CsvHeaderValidator` (TC-240..TC-243) — legacy
- `WebhookNotifier` (TC-250..TC-253)
- `OutboxDrainer` (TC-260..TC-263)
- `App` exit codes (TC-270..TC-273)
- `CatalogLoader` (TC-280..TC-285)
- `PositionalRecordParser` (TC-290..TC-295)
- `VariantDetector` (TC-296..TC-299)

## Java integration (TC-300 range)

Testcontainers Postgres + full pipeline against fixture files.

- TC-300: end-to-end valid CSV → loaded → archived → outbox delivered (legacy path)
- TC-301: duplicate file → archived without re-load
- TC-302: bad shape → rejected, notification dispatched
- TC-303: `post_transaction` triggers `stock_balance` update
- TC-310: end-to-end catalog-driven ingest — `MICH_INV_STOCKLEVEL_BATCH_408` sample (5 rows) → `CatalogIngestPipeline` → `staging.stocklevel_inbox` (typed BigDecimal, DATE, all 4 stock_status values, captured envelope, literal source_marker, footer counts validated)

## Performance (TC-400 range)

- TC-400: `fetch_inventory_json` p95 < 300 ms at 5k rows over 1000 calls
- TC-401: `load_stocklevel` for 5000-row file wall time < 60 s
- TC-402: sustained ≥ 100 tps on `post_transaction` for 1 min

## Security / negative (TC-500 range)

- TC-500: cross-tenant read returns zero
- TC-501..TC-503: `inventoryledger_app` / `writer` / `reader` role privilege minimums
- TC-504: secrets never logged (grep debug logs for password value)

## Adversarial (TC-A / TC-B / TC-C / TC-D / TC-E / TC-H / TC-I)

Introduced during the v6-baseline production-readiness review. See [Test results](/quality/test-results) for the six blockers + ten majors surfaced, and the V8 + V9 fixes.

## Synthetic data tools (TC-600 range)

- TC-600: `GenerateOpeningBalance` emits CSV matching prod header
- TC-601: same seed → identical output
- TC-602: `LoadTransactions` calls `post_transaction` for each generated payload
- TC-603: `LoadOrders` creates orders with realistic line counts
- TC-604: tools never reference production secrets

## Execution

- **Local:** `mvn verify` at parent. Spins up Testcontainers Postgres, applies migrations, runs JUnit + pgTAP suites.
- **CI:** same `mvn verify`. Test reports archived. `TEST-RESULTS.md` regenerated from JaCoCo + Surefire reports.
- **Coverage gate:** JaCoCo. Build fails if line coverage < 80% on production sources or < 100% on critical paths (`CatalogIngestPipeline`, `OutboxDrainer`, `ConfigLoader`, `Database`, plus SQL `load_stocklevel`, `f_promote_txn`, `f_stock_balance_reservation_apply`).

## Out of plan

- `recalculate_inventory` rules-engine tests — deferred to that feature's own plan
- DR / restore-drill — runbook concern, not unit test
- Multi-replica leader election — out of scope (single replica at this scale)
- UI tests — no UI
