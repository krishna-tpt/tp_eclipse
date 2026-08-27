---
title: Health & monitoring
description: /actuator/health, audit.v_all_events, notification outbox, and the Grafana dashboards.
---

Four things to watch. If any of these is red, the operator has a problem.

## 1. `/actuator/health`

The K8s probe endpoint. Bound to `HEALTH_HOST:HEALTH_PORT` (defaults `0.0.0.0:8080`).

**Response shape:**

```json
{
  "status": "UP",
  "checks": {
    "db":        { "status": "UP" },
    "scheduler": { "status": "UP" }
  }
}
```

**Checks:**

| Check | Passes when | Fails → |
|---|---|---|
| `db` | `SELECT 1` succeeds within 2 s | `DOWN`, reason `db-ping-failed` |
| `scheduler` | SchedulerLoop thread `isAlive() && !isInterrupted()` | `DOWN`, reason `scheduler-dead` |

Any check `DOWN` → HTTP 503. All `UP` → HTTP 200.

**K8s wiring:** liveness AND readiness probes point at the same URL. Liveness restarts the pod if health stays `DOWN`; readiness pulls the pod out of any Service until it comes back `UP`.

Not authenticated — this is an intentional decision for K8s probes.

## 2. `audit.v_all_events`

The observability spine. Every notable event lands here — file events, promotion outcomes, stock changes, ATP queries, notifications, heartbeats. See [Views](/database/views) for the full schema.

**Grafana:**

Point a Postgres data source at the customer DB. Queries always filter `audit.v_all_events`:

```sql
SELECT at, source, event_type, severity, error_msg, correlation_id
  FROM audit.v_all_events
 WHERE at > $__timeFrom() AND at < $__timeTo()
   AND severity IN ('warn', 'error')
 ORDER BY at DESC;
```

**Panels the operator typically wants:**

- **Live alert stream** — table, last 15 min, `severity IN ('warn','error')`
- **File processing latency** — timeseries, `event_type='file.parsed'`, p50/p95/p99 of `latency_ms`
- **Files processed / hour** — stat, `event_type='file.parsed'`, count
- **Files failed / hour** — stat, `event_type='file.failed'`, count (should be 0)
- **Promotion failure rate** — gauge, `event_type='promotion.failed' / promotion.*`
- **ATP query rate** — timeseries, `event_type='atp.queried'`, count grouped by `caller`
- **Daemon heartbeat** — stat, `event_type='daemon.heartbeat'`, `max(at)` should be < 15 minutes old
- **Outbox pending count** — from `processed.notification_outbox WHERE status IN ('pending','failed')`

**Reference dashboards** committed alongside `alter_04`:

- `deploy/11-06-v6-customer/OBSERVABILITY-DASHBOARD.html` — point-in-time render
- `deploy/11-06-v6-customer/OBSERVABILITY-DESIGN.html` — panel design notes

## 3. `processed.notification_outbox`

Failure channel with a webhook drain. See [Observability](/architecture/observability#2-notification-outbox).

**Watch:**

```sql
SELECT status, count(*)
  FROM processed.notification_outbox
 GROUP BY 1;
```

Healthy: `delivered` grows, `pending` stays low (< 10), `failed` clears within a few ticks.

Unhealthy signals:

- `failed_permanent` count > 0 — webhook is rejecting messages (400/401/403). Fix the endpoint.
- `pending` count grows unbounded — outbox drainer isn't running. Check pod logs for `outbox-drain` messages.
- Same `dedup_key` reappearing with high `repeat_count` — a persistent underlying issue is generating repeated notifications.

**Inspect recent failures:**

```sql
SELECT created_at, source, severity, message, retry_count, status
  FROM processed.notification_outbox
 WHERE status IN ('failed', 'failed_permanent')
 ORDER BY created_at DESC LIMIT 20;
```

## 4. Pod logs (kubectl / Loki / Elastic)

Structured JSON logs to stdout. Every scheduled run logs:

- `startup-complete`
- `next-fire at=<ts> in=<ms>`
- `tick-start`
- `file-picked file=<name>`
- `file-parsed file=<name> rows=<n> latency_ms=<n>`
- `file-failed file=<name> reason=<msg>` (on failure)
- `scheduled-run-complete latency=<ms> files_processed=<n>`
- `outbox-drain delivered=<n> failed=<n>`
- `daemon-heartbeat` (every N minutes)

Ops can `kubectl logs -f deploy/psql-inventory-integration-service` for a live tail.

## Suggested alert thresholds

Not enforced — configure in Grafana / PagerDuty / Slack / whatever alerting tool you use.

| Condition | Severity | Suggested response |
|---|---|---|
| `/actuator/health` reports `DOWN` for > 3 min | page | investigate — DB or scheduler thread down |
| `file.failed` count > 0 in last hour | ticket | look at `error_msg` on the failed row |
| `notification_outbox.status='failed_permanent'` new row | ticket | webhook endpoint is broken |
| `notification_outbox.pending` count > 100 | ticket | outbox not draining |
| No `daemon.heartbeat` for > 15 min | page | daemon is dead or DB write is failing |
| No `file.parsed` events between `SCHEDULE_DAILY` fire time and +15 min | ticket | file source might not be delivering, or daemon isn't running |
| p95 latency on `file.parsed` > 60 s | notice | look at file size and DB load |
| `promotion.failed` rate > 10% over 15 min | ticket | staging payloads are hitting validation errors — investigate |
| Reservation drift (see `recalculate_stock_balance`) | ticket | run manual recovery |

## Correlation IDs

Every event has `correlation_id`. Use it to trace a single artifact through the pipeline:

**Trace a file:**

```sql
SELECT at, event_type, severity, latency_ms, error_msg, payload
  FROM audit.v_all_events
 WHERE correlation_id = '<filename>.cfo'
 ORDER BY at;
```

Expected timeline: `file.picked` → `file.parsed` → `stock.changed × N` → `notify.emitted` (if any).

**Trace an SFDC order:**

```sql
SELECT * FROM audit.v_all_events
 WHERE correlation_id = '<sfdc_order_id>'
 ORDER BY at;
```

Expected: `order.received` → `promotion.success` → several `stock.changed cause=order_line`.

**Trace a transaction:**

```sql
SELECT * FROM audit.v_all_events
 WHERE correlation_id = '<external_txn_id>'
 ORDER BY at;
```

Expected: `txn.received` → `promotion.success` → `stock.changed cause=transaction`.

## Failed-run playbook

1. `/actuator/health` → if `DOWN`, look at `checks.*.status`. `db-ping-failed` → DB. `scheduler-dead` → escalate.
2. `SELECT * FROM audit.v_all_events WHERE severity IN ('warn','error') AND at > now() - interval '1 hour' ORDER BY at DESC` → get the last hour of alerts.
3. If a specific file failed: `SELECT * FROM audit.v_all_events WHERE correlation_id = '<filename>'`. Look at `error_msg` on `file.failed`.
4. Check the file in `reject/` folder — download and inspect against the [interface catalog](/reference/interface-catalog).
5. Fix the file (Michelin's problem) or fix the catalog (our problem) and re-drop into `pickup/`. Delete the corresponding `staging.stocklevel_batch` row to defeat the hash-dedup, or re-drop with a different filename.
