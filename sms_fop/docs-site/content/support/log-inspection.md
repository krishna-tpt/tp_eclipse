---
title: Log inspection
description: Where logs live, how to query them, and what to look for.
---

Three log surfaces to know:

1. **Java daemon logs** — stdout, structured JSON, one line per event
2. **Postgres server logs** — DB errors, function output, connection state
3. **`audit.event_log`** — the structured operational event stream (this is often more useful than either of the above)

## 1. Java daemon logs

### Where they are

```bash
kubectl logs -n <ns> deploy/psql-inventory-integration-service --tail=200
```

Follow live:

```bash
kubectl logs -n <ns> deploy/psql-inventory-integration-service -f
```

Multiple pods (if scaled):

```bash
kubectl logs -n <ns> -l app=psql-inventory-integration-service --tail=200
```

Historical (older than the current pod's session) — depends on the customer's log aggregator (Loki, Elastic, Splunk). If none, `kubectl logs --previous <pod>` gets the last-crashed instance's logs but nothing older.

### Format

`LOG_FORMAT=json` (prod default). One JSON object per line:

```json
{"@timestamp":"2026-07-21T06:00:12.045+02:00","level":"INFO","logger":"CatalogFileLoader","message":"file-picked","file":"MICH_INV_STOCKLEVEL_SMS_413.20260721.LSF.cfo","size_bytes":98240}
```

`LOG_FORMAT=text` (dev) is human-readable plain lines. Prod uses json so log aggregators can index and query fields.

### Key events to grep

| Grep for | What it tells you |
|---|---|
| `startup complete` | Daemon booted; config resolved |
| `scheduler-loop-started` | Schedule set; next fire time |
| `next-fire` | When the next tick fires (in local time) |
| `tick-start` | A scheduled tick began |
| `file-picked` | The daemon saw a file matching the pattern |
| `file-parsed` | A file was parsed + promoted successfully |
| `file-failed` | Parse/promote failed; includes the reason |
| `file-skipped` | Duplicate hash — already processed |
| `scheduled-run-complete` | Tick ended, includes latency + file count |
| `outbox-drained` | Webhook drain outcome |
| `daemon-heartbeat` | Periodic heartbeat (every N min) |
| `health-endpoint listening` | Health server bound |
| `shutdown` | SIGTERM received; graceful stop |
| `WARN` / `ERROR` | All non-info events |

Practical greps:

```bash
# Last 5 files processed
kubectl logs deploy/... --tail=1000 | grep file-parsed | tail -5

# All failures in this session
kubectl logs deploy/... --tail=5000 | grep -E 'ERROR|WARN|file-failed' | tail -20

# Every scheduled tick
kubectl logs deploy/... --tail=5000 | grep scheduled-run-complete | tail -10

# Time to process each file
kubectl logs deploy/... --tail=1000 | grep file-parsed | jq -r '"\(.file) \(.latency_ms)ms"'
```

### Log level

```yaml
env:
  LOG_LEVEL: INFO   # DEBUG | INFO | WARN | ERROR
```

`DEBUG` produces significant output — useful for one-off diagnosis, painful for steady state. Restart to apply.

### Correlation IDs

Every event that concerns a specific artifact carries a correlation identifier:

- Files → `file` field = filename
- Transactions → `external_txn_id`
- Orders → `sfdc_order_id`

Trace one file end-to-end:

```bash
kubectl logs deploy/... --tail=10000 | grep 'MICH_INV_STOCKLEVEL_SMS_413.20260721.LSF.cfo'
```

## 2. Postgres server logs

### Where they are

Azure Postgres Flexible Server: exposed via the Azure Portal → Postgres server → **Server Logs**. Also queryable via Azure Log Analytics if the customer has streaming enabled.

Local dev postgres: `docker logs invled-pg` or similar.

### What lives there

- Connection attempts (successful and failed)
- SQL errors (constraint violations, permission denied, etc.)
- `RAISE WARNING` / `RAISE NOTICE` from our functions
- Slow-query logs (if `log_min_duration_statement` is set)
- Autovacuum activity

### Key errors to expect

| Message | Meaning |
|---|---|
| `password authentication failed for user "inventoryledger_app"` | `DB_PASSWORD` mismatch. See [Rotate the DB password](/support/config-updates#rotate-the-db-password) |
| `SSL SYSCALL error: EOF detected` | Network mid-session drop; usually auto-retries |
| `duplicate key value violates unique constraint` | Almost always a bug; check the function's ON CONFLICT clause |
| `permission denied for function ...` | Grant missing; verify with `\df+ <function_name>` in psql |
| `deadlock detected` | Two concurrent writers on the same rows; usually the loser retries |
| `audit log write failed for atp.queried: ...` | `fetch_inventory_json_observed`'s audit write hit an error; the read still returned. See `audit.event_log` grants. |

### Enable slow-query logging (temporary)

```sql
-- Log any statement running > 500ms; requires superuser
ALTER SYSTEM SET log_min_duration_statement = 500;
SELECT pg_reload_conf();
-- undo:
ALTER SYSTEM SET log_min_duration_statement = -1;
SELECT pg_reload_conf();
```

## 3. `audit.event_log` — the operational event stream

**This is usually where you should look first.** It captures everything of operational importance, is queryable via SQL, and traces per-artifact via `correlation_id`.

### Structure

See [Event log schema](/reference/event-log-schema). Key columns: `at`, `event_type`, `severity`, `source`, `tenant_id`, `correlation_id`, `error_msg`, `latency_ms`, `payload`.

Access via the union view `audit.v_all_events` which folds in the Java-written `event_log` table plus derived views over the inbox / outbox / stock-change tables.

### Common queries

**Everything in the last 15 minutes:**

```sql
SELECT at, event_type, severity, source, correlation_id, error_msg
  FROM audit.v_all_events
 WHERE at > now() - interval '15 minutes'
 ORDER BY at DESC;
```

**Only alerts (warnings + errors):**

```sql
SELECT at, event_type, severity, source, correlation_id, error_msg, payload
  FROM audit.v_all_events
 WHERE at > now() - interval '1 hour'
   AND severity IN ('warn', 'error')
 ORDER BY at DESC;
```

**Trace one file's whole lifecycle:**

```sql
SELECT at, event_type, severity, latency_ms, error_msg, payload
  FROM audit.v_all_events
 WHERE correlation_id = 'MICH_INV_STOCKLEVEL_SMS_413.20260721.LSF.cfo'
 ORDER BY at;
```

Expected timeline for a successful file: `file.picked` → `file.parsed` (Java) → `file.parsed` (view) → several `stock.changed cause=opening_balance` → possibly `notify.emitted`.

**Trace one SFDC order:**

```sql
SELECT at, event_type, severity, error_msg
  FROM audit.v_all_events
 WHERE correlation_id = '<sfdc_order_id>'
 ORDER BY at;
```

**Trace one transaction:**

```sql
SELECT at, event_type, severity, error_msg
  FROM audit.v_all_events
 WHERE correlation_id = '<external_txn_id>'
 ORDER BY at;
```

**Who queried ATP for tenant X in the last hour?**

```sql
SELECT at, ref_id AS caller, latency_ms, rows_affected,
       payload->>'product_code' AS product
  FROM audit.event_log
 WHERE event_type = 'atp.queried'
   AND tenant_id = 'MNA'
   AND at > now() - interval '1 hour'
 ORDER BY at DESC;
```

**Ingest latency percentiles today:**

```sql
SELECT date_trunc('hour', at) AS bucket,
       percentile_cont(0.5)  WITHIN GROUP (ORDER BY latency_ms) AS p50,
       percentile_cont(0.95) WITHIN GROUP (ORDER BY latency_ms) AS p95,
       max(latency_ms) AS max
  FROM audit.event_log
 WHERE event_type = 'file.parsed'
   AND at > current_date
 GROUP BY 1 ORDER BY 1;
```

**Rejection rate (last hour, by kind):**

```sql
SELECT source,
       count(*) FILTER (WHERE event_type = 'promotion.failed')::float
       / NULLIF(count(*) FILTER (WHERE event_type LIKE 'promotion.%'), 0) AS fail_rate,
       count(*) FILTER (WHERE event_type LIKE 'promotion.%') AS total
  FROM audit.v_all_events
 WHERE at > now() - interval '1 hour'
 GROUP BY 1;
```

**Recent stock movements for a product:**

```sql
SELECT at, payload->>'signed_qty' AS qty, payload->>'txn_type' AS type,
       payload->>'warehouse_code' AS warehouse, correlation_id
  FROM audit.v_stock_change_events
 WHERE payload->>'product_code' = 'PROD-A'
   AND at > now() - interval '24 hours'
 ORDER BY at DESC;
```

**Heartbeat health — gaps:**

```sql
SELECT at, at - lag(at) OVER (ORDER BY at) AS gap
  FROM audit.event_log
 WHERE event_type = 'daemon.heartbeat'
   AND at > now() - interval '6 hours'
 ORDER BY at DESC LIMIT 30;
```

Any gap > 20 min indicates the daemon was down or the scheduler thread paused.

## Notification outbox — inspecting webhook deliveries

Distinct from the audit log — the outbox is the persistent notification queue.

**Current backlog:**

```sql
SELECT status, count(*)
  FROM processed.notification_outbox
 GROUP BY status;
```

**Recent failures:**

```sql
SELECT created_at, source, severity, message, retry_count, status
  FROM processed.notification_outbox
 WHERE status IN ('failed', 'failed_permanent')
 ORDER BY created_at DESC LIMIT 20;
```

**Frequently repeated notifications (dedup at work):**

```sql
SELECT source, message, repeat_count, created_at
  FROM processed.notification_outbox
 WHERE repeat_count > 5
   AND created_at > now() - interval '24 hours'
 ORDER BY repeat_count DESC LIMIT 20;
```

Fix the source of high-repeat notifications; the notifier is doing its job by collapsing them.

## Practical debugging workflow

1. **Start with health** — `curl /actuator/health`. If DOWN, [Failure scenarios](/support/failure-scenarios).
2. **Then `audit.v_all_events` last 15 min** — filter by `severity IN ('warn','error')`.
3. **Grab the correlation_id** of the offending artifact and trace it end-to-end.
4. **Cross-reference to notification_outbox** — was ops notified?
5. **Only then check daemon logs** — for stack traces or context the event log doesn't carry.
6. **DB logs last** — connection issues, permission errors, deadlocks.

The audit stream is designed to answer 80% of triage questions without leaving `psql`. Daemon and DB logs are for the remaining 20% — usually stack traces or infrastructure noise.
