---
title: Views
description: Audit views that reshape existing tables into a single Grafana feed.
---

Five reshaping views + one master union. All in the `audit` schema. All defined in `alter_04_audit_event_log.sql`. No rows are written to the views — they're pure projections over existing tables.

Every view has the same column shape as `audit.event_log`:

```
event_id · at · event_type · severity · source · tenant_id ·
correlation_id · status · ref_id · error_code · error_msg ·
latency_ms · rows_affected · bytes · payload
```

That uniformity is the whole point — Grafana can point one Postgres query at `audit.v_all_events` and get every event.

## `audit.v_file_batch_events`

Wraps `staging.stocklevel_batch` — one row per file, emitted at terminal state (`completed_at IS NOT NULL`).

| Field | Value |
|---|---|
| `event_id` | `'fb-' || batch_id` |
| `at` | `started_at` |
| `event_type` | `'file.parsed'` |
| `severity` | `'error'` if `status='failed'` else `'info'` |
| `source` | `'files'` |
| `correlation_id` | `file_name` |
| `latency_ms` | `(completed_at - started_at) × 1000` |
| `rows_affected` | `rows_accepted + rows_rejected` |
| `payload` | `{rows_accepted, rows_rejected}` |

Pre-staging failures (bad shape, envelope mismatch) never reach `stocklevel_batch` — the Java daemon writes `file.failed` directly to `audit.event_log`.

## `audit.v_order_events`

Wraps `staging.order_inbox` — emits **two** events per inbox row:

**Receipt event (always):**

- `event_id`: `'ord-r-' || inbox_id`
- `at`: `received_at`
- `event_type`: `'order.received'`
- `bytes`: byte length of the JSON payload

**Promotion event (only when `processed_at IS NOT NULL`):**

- `event_id`: `'ord-p-' || inbox_id`
- `at`: `processed_at`
- `event_type`: `promotion.success` / `promotion.failed` / `order.superseded` based on `status`
- `severity`: `warn` if rejected, else `info`
- `error_msg`: `reject_reason`
- `latency_ms`: `(processed_at - received_at) × 1000`

Receipt and promotion are distinct observables on the same row — Grafana wants to plot their rates independently.

## `audit.v_txn_events`

Same two-event pattern as `v_order_events`, over `staging.txn_inbox`. Sources are `'fop'`, business keys are `external_txn_id`.

## `audit.v_stock_change_events`

UNION of three sources — every on-hand or reserved movement in chronological order.

**From `processed.inv_transaction`:**

- `event_id`: `'itx-' || inv_transaction_id`
- `at`: `posted_at`
- `event_type`: `'stock.changed'`
- `source`: `'fop'`
- `payload`: `{product_code, warehouse_code, subinventory, stock_status, signed_qty, txn_type, cause: 'transaction'}`

**From `processed.opening_balance`:**

- `event_id`: `'ob-' || opening_balance_id`
- `at`: `as_of_date::TIMESTAMPTZ`
- `source`: `'files'`
- `payload`: `{product_code, warehouse_code, subinventory, stock_status, qty, cause: 'opening_balance'}`

**From `processed.sfdc_order_line`:**

- `event_id`: `'ol-' || sfdc_order_id || ':' || line_no`
- `at`: `created_at`
- `source`: `'sfdc'`
- `status`: `line_state`
- `payload`: `{sfdc_order_id, line_no, product_code, warehouse_code, qty, state, cause: 'order_line'}`

Reservations, cancellations, and state moves are visible in one feed.

## `audit.v_notify_events`

Wraps `processed.notification_outbox`. One-to-one mapping.

| Field | Value |
|---|---|
| `event_id` | `'nx-' || outbox_id` |
| `at` | `created_at` |
| `event_type` | `'notify.emitted'` |
| `severity` | passes through (`INFO` / `WARN` / `ERROR`) |
| `source` | passes through (e.g. `'file_loader'`, `'promote_txn'`) |
| `status` | `pending` / `delivered` / `failed` / `failed_permanent` |
| `error_msg` | `message` |
| `rows_affected` | `repeat_count` (how many collapsed via `dedup_key`) |
| `payload` | passes through |

## `audit.v_all_events` — the master union

```sql
CREATE OR REPLACE VIEW audit.v_all_events AS
    SELECT event_id::TEXT, at, event_type, severity, source, tenant_id, correlation_id,
           status, ref_id, error_code, error_msg, latency_ms, rows_affected, bytes, payload
      FROM audit.event_log
UNION ALL SELECT * FROM audit.v_file_batch_events
UNION ALL SELECT * FROM audit.v_order_events
UNION ALL SELECT * FROM audit.v_txn_events
UNION ALL SELECT * FROM audit.v_stock_change_events
UNION ALL SELECT * FROM audit.v_notify_events;
```

Grafana's Postgres data source points at this view. Every dashboard filters by `at`, `event_type`, `severity`, `source`, or `tenant_id` on the union.

## Sample queries

**Last 15 minutes of alerts:**

```sql
SELECT at, source, event_type, error_msg, correlation_id
  FROM audit.v_all_events
 WHERE at > now() - interval '15 minutes'
   AND severity IN ('warn', 'error')
 ORDER BY at DESC;
```

**File processing latency percentiles (last 24h):**

```sql
SELECT date_trunc('hour', at)                        AS bucket,
       percentile_cont(0.5)  WITHIN GROUP (ORDER BY latency_ms) AS p50,
       percentile_cont(0.95) WITHIN GROUP (ORDER BY latency_ms) AS p95,
       percentile_cont(0.99) WITHIN GROUP (ORDER BY latency_ms) AS p99
  FROM audit.v_all_events
 WHERE event_type = 'file.parsed'
   AND at > now() - interval '24 hours'
 GROUP BY 1 ORDER BY 1;
```

**Promotion failure rate (last hour):**

```sql
SELECT source,
       count(*) FILTER (WHERE event_type = 'promotion.failed')::float
       / NULLIF(count(*) FILTER (WHERE event_type LIKE 'promotion.%'), 0) AS fail_rate
  FROM audit.v_all_events
 WHERE at > now() - interval '1 hour'
 GROUP BY 1;
```

**Recent stock movements for a product:**

```sql
SELECT at, source, payload
  FROM audit.v_stock_change_events
 WHERE payload->>'product_code' = 'WIDGET-001'
 ORDER BY at DESC LIMIT 50;
```

**Trace a filename through the pipeline:**

```sql
SELECT at, event_type, severity, latency_ms, error_msg, payload
  FROM audit.v_all_events
 WHERE correlation_id = 'MICH_INV_STOCKLEVEL_SMS_413.1010.A003IIST.20260715.LSF.cfo'
 ORDER BY at;
```
