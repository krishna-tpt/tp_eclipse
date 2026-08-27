---
title: Performance
description: Targets, observed numbers, and where the bottlenecks live.
---

Scale target: **~5,000 records/day, one tenant**. Every design decision is calibrated for this scale — deliberately not over-engineered for 100× that.

## Targets (from the [spec](/overview/spec#4-non-functional-requirements-acceptance-criteria))

| Metric | Target | Test case |
|---|---|---|
| `fetch_inventory_json` p95 latency | < 300 ms at 5k records | TC-400 |
| `load_stocklevel` wall time for a 5000-row file | < 60 s | TC-401 |
| `post_transaction` / `f_promote_txn` sustained throughput | ≥ 100 tps for 1 min without errors | TC-402 |
| JAR startup time | < 5 s (config + DB connect + Flyway skip + scheduler start) | (informal, checked at startup) |
| JAR RSS at steady state | < 256 MB | (informal, checked in prod) |

## Observed numbers

From the July 15 end-to-end run against Azure Postgres Flex Server 16.13:

| Metric | Observed |
|---|---|
| File: `MICH_INV_STOCKLEVEL_SMS_413.1010.A003IIST.20260715.LSF.cfo` (284 rows) | 1.2 s wall time end-to-end |
| Parse only (Java) | ~40 ms |
| Envelope validate | < 1 ms |
| Staging INSERT (batch of 284 rows) | ~600 ms |
| `load_stocklevel` promote | ~500 ms |
| Archive move on files.com | ~200 ms |
| Outbox drain (0 rows) | ~20 ms |
| `daemon.heartbeat` write | ~15 ms |

Well under the 60 s target for a 5k-row file. Scaling from 284 rows to 5k should stay near-linear: parse is CPU-bound and negligible; staging INSERT is I/O-bound and dominates.

### `fetch_inventory_json` sampling

Sampled from live customer DB after the July 15 load:

```sql
-- 100 iterations, timed
SELECT clock_timestamp();
SELECT * FROM processed.fetch_inventory_json('MNA', NULL, NULL, NULL);  -- 284 rows
SELECT clock_timestamp();
```

Observed: 45–95 ms per call, no cache hits. Well within the 300 ms p95 target.

Filter-narrowed calls (`fetch_inventory_json('MNA', 'WH-01', 'ONHAND', 'PROD-A')`) return in 8–20 ms.

## Where the bottlenecks live

1. **JDBC INSERT for large batches** — the 284-row INSERT is one round trip; a 5k-row INSERT is still one round trip. If we ever exceeded ~50k rows/file, we'd switch to `CopyManager` (already imported for the legacy CSV path).
2. **Files.com list()** — pagination costs one API call per page (default page size 200). Not currently rate-limited by files.com. Very fast (< 500 ms typical).
3. **Files.com download** — sequential per file. If we ever needed to parallelize, `CatalogFileLoader` would need a small work-queue and worker pool. Currently: no.
4. **Trigger cascade** — `f_stock_balance_reservation_apply` fires per `sfdc_order_line` INSERT. Cost is O(1) per line. At 5k orders/day with average 3 lines each = 15k trigger invocations = ~50 ms per invocation × 15k = 12 min *if serialized*, but they're spread across the day.

## Non-goals

- Sub-second file ingestion — files land on a schedule, not on-demand
- 10k tps on transactions — customer's peak is < 1 tps
- 100+ tenants — customer is single-tenant per install
- Real-time ATP freshness (< 5 s) — SFDC's quote screen tolerates 30-60 s stale reads via a local cache

If any of these shift, revisit — the design has room but isn't optimizing for it today.

## What we deliberately didn't optimize

### No connection pooling

One JDBC connection per JAR lifetime. Reused across scheduled ticks. Startup cost of a pool (HikariCP) is not justified when we have one long-lived caller.

### No materialized view for ATP

`fetch_inventory_json` reads live from `stock_balance`. An MV would let us cache the aggregation, but:

- Aggregation is fast (< 100 ms on 284 rows; would scale to ~500 ms at 5k)
- MV refresh cadence is another config to tune
- Reservation cascade means the underlying data changes every txn; MV would need aggressive refresh

At current scale, MV overhead outweighs benefit. Reconsider if `fetch_inventory_json` starts hitting the 300 ms target.

### No partitioning

`processed.inv_transaction` is not partitioned. At 5k txns/day × 365 = 1.8M/year, a single unpartitioned table is fine for years. Partitioning by month (`pg_partman`) is architected for but not shipped — the `partitioning NFR` in the spec is deferred.

### No sharding

Single tenant, single DB. Postgres 16 handles this comfortably.

## Load testing

Not currently automated. To run:

1. Use `filemanager-data-tools:GenerateOpeningBalance` with `count=5000, seed=42` to produce a synthetic 5k-row CSV.
2. Convert to CFO format via a small conversion script (matches Michelin's `;`-delimited shape).
3. Drop into pickup folder.
4. Time the full end-to-end.

Repeat with `count=10000, 20000` if you need to characterize scaling. Diminishing returns past 5x the target scale — the data doesn't tell you what will happen at prod scale in a year.

## Alerting

Suggested (not enforced):

- p95 `latency_ms` on `file.parsed` events > 60 s over 15 min → ticket
- Any `file.parsed` event with `latency_ms > 120000` (2 min) → ticket
- `atp.queried` p95 `latency_ms` > 500 ms over 15 min → notice
- DB connection age > 24 h → cycle the pod (guards against slow leaks in the JDBC driver)

Wire these into your alerting system of choice by querying `audit.v_all_events`.

## Historical benchmarks

Recorded from CI + one-off local runs:

| Date | Data volume | Environment | Result |
|---|---|---|---|
| 2026-07-15 | 284 rows | Azure Flex Server 16.13, JAR from local machine | 1.2 s end-to-end |
| 2026-06-11 | 5000 rows (synthetic) | `postgres:16-alpine` | 8.5 s end-to-end (parse 200 ms + stage 3.5 s + promote 4.2 s + archive 100 ms). Under target. |
| 2026-06-08 | 500 rows | postgres 16 local (`idempiere-pg` container) | 1.9 s end-to-end |
| 2026-05-19 | 6 rows (guerrilla suite) | postgres 16 local | 0.7 s end-to-end (mostly SSL handshake + Flyway) |

Trend: linear in row count above ~500 rows. Below that, startup / handshake dominates.
