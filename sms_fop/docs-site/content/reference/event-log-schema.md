---
title: Event log schema
description: Every event type in audit.event_log — what emits it, when, and what payload to expect.
---

Every operator-visible event lands in one place: `audit.event_log` (Java-written events) or one of five derived views (existing tables reshaped). All accessible via `audit.v_all_events`. See [Views](/database/views) and [Observability](/architecture/observability).

## Column shape (identical across all events + views)

| Column | Type | Notes |
|---|---|---|
| `event_id` | BIGSERIAL / TEXT (view) | Monotonic in table; synthesized (`fb-N`, `tx-r-N`, etc.) in views |
| `at` | TIMESTAMPTZ | When the event occurred |
| `event_type` | TEXT | See catalog below |
| `severity` | TEXT | `debug` / `info` / `warn` / `error` |
| `source` | TEXT | `files` / `fop` / `sfdc` / `atp` / `daemon` |
| `tenant_id` | TEXT | Nullable |
| `correlation_id` | TEXT | Traceable identifier: filename, order id, txn id |
| `status` | TEXT | Terminal state for the event |
| `ref_id` | TEXT | Secondary identifier |
| `error_code`, `error_msg` | TEXT | Set on `severity IN ('warn', 'error')` |
| `latency_ms` | INTEGER | Duration where meaningful |
| `rows_affected` | INTEGER | Row / record count |
| `bytes` | BIGINT | Byte count (e.g., JSON payload size) |
| `payload` | JSONB | Event-specific data |

## Event catalog

### Written by the Java daemon (into `audit.event_log`)

| `event_type` | `source` | `severity` | Emitted when | Payload |
|---|---|---|---|---|
| `file.picked` | `files` | `info` | Daemon lists a file from the pickup folder | `{ file_bytes, sha256 }` |
| `file.parsed` | `files` | `info` | File successfully parsed + promoted | `{ rows_accepted, rows_rejected }` (also on `v_file_batch_events`) |
| `file.failed` | `files` | `error` | File failed pre-staging (bad shape, envelope mismatch, no catalog match) | `{ reason, stage }` |
| `file.skipped` | `files` | `info` | Duplicate file (hash dedup hit) | `{ prior_batch_id }` |
| `daemon.heartbeat` | `daemon` | `debug` | Every N minutes from `HeartbeatEmitter` in scheduled mode | `{ scheduler_alive, db_ping_ms }` |

### Written by `fetch_inventory_json_observed`

| `event_type` | `source` | `severity` | Emitted when | Payload |
|---|---|---|---|---|
| `atp.queried` | `atp` | `info` | Each call to `fetch_inventory_json_observed` | `{ warehouse_code, subinventory, product_code, caller }` |

### Derived from `staging.stocklevel_batch` (view `v_file_batch_events`)

| `event_type` | `source` | `severity` | Emitted when | Payload |
|---|---|---|---|---|
| `file.parsed` | `files` | `info` or `error` | Row's `completed_at` is set. `severity=error` when `status='failed'`. | `{ rows_accepted, rows_rejected }` |

Note: `file.parsed` appears twice — once from the Java daemon (with parse-time metadata) and once from the view (with post-promote metadata). Same `correlation_id` (filename) ties them together.

### Derived from `staging.order_inbox` (view `v_order_events`)

Two events per inbox row:

| `event_type` | `source` | `severity` | Emitted when |
|---|---|---|---|
| `order.received` | `sfdc` | `info` | `received_at` (always) |
| `promotion.success` | `sfdc` | `info` | `processed_at`, `status='processed'` |
| `promotion.failed` | `sfdc` | `warn` | `processed_at`, `status='rejected'`. `error_msg` = `reject_reason` |
| `order.superseded` | `sfdc` | `info` | `processed_at`, `status='superseded'` |

`bytes` = JSON payload length. `correlation_id` = `sfdc_order_id`.

### Derived from `staging.txn_inbox` (view `v_txn_events`)

Same two-event pattern:

| `event_type` | `source` | `severity` | Emitted when |
|---|---|---|---|
| `txn.received` | `fop` | `info` | `received_at` |
| `promotion.success` | `fop` | `info` | `processed_at`, `status='processed'` |
| `promotion.failed` | `fop` | `warn` | `processed_at`, `status='rejected'` |
| `txn.superseded` | `fop` | `info` | `processed_at`, `status='superseded'` |

`correlation_id` = `external_txn_id`.

### Derived from stock-change sources (view `v_stock_change_events`)

One event per stock movement. UNION of three sources:

| `event_type` | `source` | `payload.cause` | From |
|---|---|---|---|
| `stock.changed` | `fop` | `transaction` | `processed.inv_transaction` — per txn |
| `stock.changed` | `files` | `opening_balance` | `processed.opening_balance` — per opening balance row |
| `stock.changed` | `sfdc` | `order_line` | `processed.sfdc_order_line` — per line create/update |

Payload always includes `product_code`, `warehouse_code`, `subinventory`, `stock_status`. Transaction events add `signed_qty` + `txn_type`. Opening-balance events add `qty`. Order-line events add `sfdc_order_id`, `line_no`, `qty`, `state`.

### Derived from `processed.notification_outbox` (view `v_notify_events`)

| `event_type` | `source` | `severity` | Emitted when |
|---|---|---|---|
| `notify.emitted` | passes through (from outbox row) | passes through | `created_at` |

`rows_affected` = `repeat_count` (how many identical notifications collapsed via `dedup_key`). `error_msg` = the outbox's `message` field. `status` = one of `pending`/`delivered`/`failed`/`failed_permanent`.

## Correlation IDs — how to trace

| Trace target | `correlation_id` value |
|---|---|
| A file | filename (e.g., `MICH_INV_STOCKLEVEL_SMS_413.20260715.LSF.cfo`) |
| An SFDC order | `sfdc_order_id` (e.g., `0WO9Z000009f6lpWAA`) |
| A FOP transaction | `external_txn_id` |
| A notification | `dedup_key` (or NULL if not deduped) |

**Trace a file end-to-end:**

```sql
SELECT at, event_type, severity, latency_ms, error_msg, payload
  FROM audit.v_all_events
 WHERE correlation_id = 'MICH_INV_STOCKLEVEL_SMS_413.20260715.LSF.cfo'
 ORDER BY at;
```

Expected timeline: `file.picked` → `file.parsed` (Java) → `file.parsed` (view) → several `stock.changed cause=opening_balance` → possibly `notify.emitted`.

**Trace an order:**

```sql
SELECT at, event_type, severity, error_msg, payload
  FROM audit.v_all_events
 WHERE correlation_id = '0WO9Z000009f6lpWAA'
 ORDER BY at;
```

Expected: `order.received` → `promotion.success` → several `stock.changed cause=order_line`.

## Severity semantics

- `debug` — operational: `daemon.heartbeat`. Filtered out of alert dashboards by default.
- `info` — normal successful operation. `file.picked`, `file.parsed`, `order.received`, `promotion.success`, `stock.changed`, `atp.queried`.
- `warn` — degraded but recoverable. `promotion.failed` (with rejection reason), `file.skipped` (if operator considers duplicates a warn), transient webhook delivery failures.
- `error` — needs attention. `file.failed` (pre-staging failure), notification `failed_permanent`.

Alert dashboards typically filter `severity IN ('warn', 'error')`.

## Storage growth

`audit.event_log` grows only from Java-written events (files + heartbeat + ATP calls). Derived views cost zero storage — they project existing rows in the underlying tables.

Rough sizing at 5k records/day, 20 heartbeats/day, one file/day, 100 ATP calls/hour:

- Java event_log rows/day: ~2500
- Row size: ~500 bytes (payload varies)
- Growth: ~1.25 MB/day → ~450 MB/year

Housekeeping: partition `audit.event_log` monthly via `pg_partman` if / when installed. Not shipped in v6 customer package — the customer's scale doesn't require it yet.

## Sample events (real data)

### `file.parsed` from Java

```json
{
  "at":             "2026-07-15T04:33:12+02",
  "event_type":     "file.parsed",
  "severity":       "info",
  "source":         "files",
  "correlation_id": "MICH_INV_STOCKLEVEL_SMS_413.20260715.LSF.cfo",
  "status":         "loaded",
  "ref_id":         "MICH_INV_STOCKLEVEL_SMS_413.20260715.LSF.cfo",
  "latency_ms":     1240,
  "rows_affected":  284,
  "bytes":          98240,
  "payload":        {"rows_accepted": 284, "rows_rejected": 0}
}
```

### `atp.queried`

```json
{
  "at":             "2026-07-16T15:22:04+02",
  "event_type":     "atp.queried",
  "severity":       "info",
  "source":         "atp",
  "tenant_id":      "MNA",
  "ref_id":         "sfdc-quote-screen",
  "latency_ms":     52,
  "rows_affected":  1,
  "payload":        {"warehouse_code": "408_ES_P57_..._CO",
                     "subinventory":   "ONHAND",
                     "product_code":   "459473_101",
                     "caller":         "sfdc-quote-screen"}
}
```

### `promotion.failed` (from view)

```json
{
  "at":             "2026-07-15T09:14:22+02",
  "event_type":     "promotion.failed",
  "severity":       "warn",
  "source":         "fop",
  "tenant_id":      "MNA",
  "correlation_id": "FOP-TX-98765",
  "status":         "rejected",
  "error_msg":      "unknown_reference",
  "latency_ms":     18,
  "payload":        {"inbox_id": 12345}
}
```
