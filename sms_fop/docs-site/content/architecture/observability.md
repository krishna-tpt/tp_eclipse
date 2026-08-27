---
title: Observability
description: Event log, notification outbox, and the /actuator/health endpoint.
---

Three loosely-coupled channels. Each answers a different operator question.

| Channel | Answers | Cadence |
|---|---|---|
| **`audit.event_log`** + views | "What happened, when, why?" | Every file + every notable state change |
| **`processed.notification_outbox`** → webhook | "Something needs attention." | Emitted on failure/warn; drained per tick |
| **`/actuator/health`** | "Is the process alive right now?" | Continuous, K8s probes |

## 1. Audit event log

Introduced in `alter_04_audit_event_log.sql`. The event log is the observability spine. Grafana points its Postgres data source at `audit.v_all_events` and gets every event in one consistent shape.

### The one table we write to

```sql
CREATE TABLE audit.event_log (
    event_id       BIGSERIAL PRIMARY KEY,
    at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    event_type     TEXT NOT NULL,
    severity       TEXT NOT NULL DEFAULT 'info'
                   CHECK (severity IN ('debug','info','warn','error')),
    source         TEXT NOT NULL,
    tenant_id      TEXT,
    correlation_id TEXT,
    status         TEXT,
    ref_id         TEXT,
    error_code     TEXT,
    error_msg      TEXT,
    latency_ms     INTEGER,
    rows_affected  INTEGER,
    bytes          BIGINT,
    payload        JSONB NOT NULL DEFAULT '{}'
);
```

Indexed by `at DESC`, by `(event_type, at)`, by `(tenant_id, at)`, by `correlation_id`, and by `(severity, at)` for the alert-only partial index (`WHERE severity IN ('warn','error')`). See [Event log schema](/reference/event-log-schema) for the full event catalog.

### Written by whom

Two writers only:

- **The Java daemon** — file events (`file.picked`, `file.parsed`, `file.failed`) via `EventLogWriter` on every file's terminal outcome, plus `daemon.heartbeat` every N minutes from `HeartbeatEmitter`.
- **`fetch_inventory_json_observed`** — the ATP read wrapper. Logs `atp.queried` for every call with `caller`, `latency_ms`, `rows_affected`. Audit-write failure never blocks the read path (`EXCEPTION WHEN OTHERS THEN RAISE WARNING`).

Nothing else writes here.

### Derived views (no writes)

Existing data already lives in inbox, outbox, and transaction tables. Rather than duplicate rows, five views expose those tables in the same shape as `event_log`:

| View | Wraps | Emits |
|---|---|---|
| `audit.v_file_batch_events` | `staging.stocklevel_batch` | `file.parsed` (terminal) |
| `audit.v_order_events` | `staging.order_inbox` | `order.received` + `promotion.success` / `promotion.failed` / `order.superseded` |
| `audit.v_txn_events` | `staging.txn_inbox` | `txn.received` + `promotion.*` |
| `audit.v_stock_change_events` | `inv_transaction` ∪ `opening_balance` ∪ `sfdc_order_line` | `stock.changed` (with `cause`: `transaction` / `opening_balance` / `order_line`) |
| `audit.v_notify_events` | `notification_outbox` | `notify.emitted` |

### The Grafana endpoint

```sql
CREATE OR REPLACE VIEW audit.v_all_events AS
    SELECT * FROM audit.event_log
UNION ALL SELECT * FROM audit.v_file_batch_events
UNION ALL SELECT * FROM audit.v_order_events
UNION ALL SELECT * FROM audit.v_txn_events
UNION ALL SELECT * FROM audit.v_stock_change_events
UNION ALL SELECT * FROM audit.v_notify_events;
```

Grafana queries this view with `WHERE at > now() - interval '15 minutes' AND severity IN ('warn','error')`, groups by `event_type`, and gets a live alert feed.

## 2. Notification outbox

`processed.notification_outbox` is the DB-side failure channel. Any component (Java or SQL) that wants to notify operators writes a row; the Java daemon drains it via webhook at the end of every tick.

### Row shape

```sql
outbox_id      BIGSERIAL PRIMARY KEY
tenant_id      TEXT
severity       INFO | WARN | ERROR
source         TEXT              -- e.g. 'file_loader', 'promote_txn', 'load_stocklevel'
message        TEXT
payload        JSONB
dedup_key      TEXT              -- collapses repeats via notify_outbox()
repeat_count   INTEGER DEFAULT 1
status         pending | delivered | failed | failed_permanent
retry_count    INTEGER
created_at     TIMESTAMPTZ
delivered_at   TIMESTAMPTZ
last_attempt_at TIMESTAMPTZ
```

Partial index `WHERE status IN ('pending', 'failed')` makes drain-time queries constant-time regardless of outbox size.

### Dedup via `notify_outbox(...)`

The SQL side never `INSERT`s directly; it calls `processed.notify_outbox(tenant, severity, source, message, dedup_key, payload)`. The function collapses repeated calls with the same `(tenant_id, source, dedup_key)` within `outbox_dedup_window_minutes` (default 60) into a single row with `repeat_count` incremented. A flood of oversell notifications is one row, not N.

### Java-side drain

`OutboxDrainer.drain()`:

1. `SELECT ... FROM notification_outbox WHERE status IN ('pending','failed') FOR UPDATE SKIP LOCKED` — parallel-safe.
2. For each row, POST payload to every configured webhook URL.
3. On 2xx → `UPDATE status='delivered', delivered_at=now()`.
4. On 4xx → `UPDATE status='failed_permanent'` (permanent — do not retry).
5. On 5xx / timeout → `UPDATE retry_count++, status='failed'`. Next tick's drain picks it up until `retry_count >= WEBHOOK_MAX_RETRIES`, then `failed_permanent`.

### Webhook config

- `WEBHOOK_URL_PRIMARY` — required
- `WEBHOOK_MAX_RETRIES` — default 5
- `WEBHOOK_TIMEOUT_MS` — default 5000
- `WEBHOOK_RETRY_BACKOFF_MS` — default 1000 (linear backoff × retry_count)
- `WEBHOOK_BATCH_SIZE` — default 50 (max rows drained per tick)

## 3. `/actuator/health`

The K8s probe endpoint. See [Scheduler and lifecycle](/architecture/scheduler-lifecycle#healthendpoint-details) for the full contract. Summary: two checks (`db` = `SELECT 1`, `scheduler` = thread alive); any `DOWN` → HTTP 503, all `UP` → HTTP 200.

## What operators see

Together, the three channels give the operator:

- **Right now** — hit `/actuator/health`.
- **Last 15 minutes** — `SELECT * FROM audit.v_all_events WHERE at > now() - interval '15 minutes' AND severity IN ('warn','error')`.
- **What went wrong on this file** — `SELECT * FROM audit.event_log WHERE correlation_id = '<filename>'`.
- **What Slack got told about it** — `SELECT * FROM processed.notification_outbox WHERE source='file_loader' AND status='delivered' ORDER BY delivered_at DESC LIMIT 20`.
- **Ledger movements** — `SELECT * FROM audit.v_stock_change_events WHERE at > now() - interval '1 hour' ORDER BY at DESC`.

The dashboards themselves (`OBSERVABILITY-DASHBOARD.html` in the v6 tag) are point-in-time snapshots; the live source is always `audit.v_all_events`.
