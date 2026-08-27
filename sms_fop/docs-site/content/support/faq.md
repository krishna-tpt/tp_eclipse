---
title: FAQ
description: Quick answers to the most common support questions.
---

## Daemon / service

### Q: Is the daemon alive?

```bash
kubectl exec -n <ns> deploy/psql-inventory-integration-service -- \
  curl -s http://localhost:8080/actuator/health
```

Expect `{"status":"UP","checks":{"db":{"status":"UP"},"scheduler":{"status":"UP"}}}`. Anything else → see [Failure scenarios](/support/failure-scenarios#health-probe-reports-down).

Also check the audit stream:

```sql
-- Latest heartbeat (should be < ~15 min old)
SELECT max(at) AS last_heartbeat
  FROM audit.event_log
 WHERE event_type = 'daemon.heartbeat';
```

### Q: When did the daemon last run a tick?

```sql
SELECT max(at) AS last_tick, event_type
  FROM audit.event_log
 WHERE event_type IN ('file.parsed','file.skipped','daemon.heartbeat')
 GROUP BY event_type
 ORDER BY last_tick DESC;
```

### Q: Is the scheduler firing on time?

```sql
-- Gaps between consecutive heartbeats > 20 min = problem
SELECT at, at - lag(at) OVER (ORDER BY at) AS gap
  FROM audit.event_log
 WHERE event_type = 'daemon.heartbeat'
   AND at > now() - interval '6 hours'
 ORDER BY at DESC;
```

## Files

### Q: What files are in the pickup folder right now?

Log into files.com (or run against the API), or ask the daemon logs — it logs `file-picked` for every file it sees.

```bash
kubectl logs -n <ns> deploy/psql-inventory-integration-service --tail=200 \
  | grep file-picked
```

### Q: Was file X processed?

```sql
SELECT started_at, completed_at, status, rows_accepted, rows_rejected
  FROM staging.stocklevel_batch
 WHERE file_name = 'MICH_INV_STOCKLEVEL_SMS_413.20260721.LSF.cfo';
```

Also the audit trace:

```sql
SELECT at, event_type, severity, latency_ms, error_msg
  FROM audit.v_all_events
 WHERE correlation_id = 'MICH_INV_STOCKLEVEL_SMS_413.20260721.LSF.cfo'
 ORDER BY at;
```

### Q: Why is a file in the `reject/` folder?

```sql
SELECT at, error_msg, payload
  FROM audit.event_log
 WHERE event_type = 'file.failed'
   AND correlation_id = '<filename>';
```

Also see the corresponding outbox row:

```sql
SELECT severity, source, message, payload
  FROM processed.notification_outbox
 WHERE created_at > now() - interval '24 hours'
   AND payload::text LIKE '%<filename>%';
```

### Q: How do I re-process a file?

See [Manual operations → Re-process a file](/support/manual-operations#re-process-a-specific-file).

### Q: Files aren't being picked up at all — pickup folder is not empty

Common causes:

1. **Daemon down** — check `/actuator/health`.
2. **files.com API key expired** — logs show `HTTP 401 Unauthorized`. Rotate per [Config updates](/support/config-updates#rotate-the-files-com-api-key).
3. **Filename pattern mismatch** — the file doesn't match the catalog's `filename_pattern`. Rename the file to match, or update the catalog and redeploy.
4. **Network unreachable from pod** — the daemon logs will show `SocketTimeoutException`.

## Transactions & orders (FOP / SFDC)

### Q: Are transactions being promoted from `staging.txn_inbox`?

```sql
-- Should show near-zero pending, high processed
SELECT status, count(*)
  FROM staging.txn_inbox
 WHERE received_at > now() - interval '1 hour'
 GROUP BY status;
```

If `pending` is growing:

- Check the triggers exist: `\dt+ staging.txn_inbox` should show `trg_txn_inbox_promote`
- Check trigger function isn't errored: `SELECT * FROM audit.v_all_events WHERE event_type='promotion.failed' ORDER BY at DESC LIMIT 20;`

### Q: An SFDC order didn't create reservations

```sql
-- Trace the order end to end
SELECT at, event_type, severity, error_msg
  FROM audit.v_all_events
 WHERE correlation_id = '<sfdc_order_id>'
 ORDER BY at;
```

Expected: `order.received` → `promotion.success` → several `stock.changed cause=order_line`. If `promotion.failed` appears, `error_msg` shows why.

### Q: A shipment didn't reduce the reservation

The reservation cascade needs `erp_line_id` (or `sfdc_line_id`, or `sfdc_order_id` header fallback) on the shipment txn. Check the txn's payload:

```sql
SELECT external_txn_id, erp_line_id, sfdc_line_id, sfdc_order_id
  FROM processed.inv_transaction
 WHERE external_txn_id = '<txn-id>';
```

If all three are NULL, FOP didn't send the linking IDs. Ask FOP to include `erp_line_id` on outbound-shipment txns. See [Match precedence](/database/er-diagram#match-precedence).

## Stock numbers

### Q: How much stock does product X have right now?

```sql
SELECT product_code, warehouse_code, subinventory, stock_status,
       on_hand_qty, reserved_qty, on_hand_qty - reserved_qty AS available
  FROM processed.stock_balance
 WHERE product_code = 'PROD-A'
 ORDER BY warehouse_code, subinventory;
```

Or via the JSON API SFDC uses:

```sql
SELECT * FROM processed.fetch_inventory_json('MNA', NULL, NULL, 'PROD-A');
```

### Q: Numbers look wrong

Two most-likely causes:

1. **Trigger outage** — `stock_balance` drifted from source truth. Fix: `SELECT processed.recalculate_stock_balance('<tenant_code>');`
2. **A back-dated `opening_balance` arrived** — the trigger refuses to apply it. Look for the WARN in the outbox:
   ```sql
   SELECT * FROM processed.notification_outbox
    WHERE source = 'opening_balance_apply'
      AND message LIKE '%back-dated%'
    ORDER BY created_at DESC LIMIT 5;
   ```
   Same fix: run `recalculate_stock_balance`.

## Notifications

### Q: Are webhook notifications being delivered?

```sql
SELECT status, count(*)
  FROM processed.notification_outbox
 WHERE created_at > now() - interval '24 hours'
 GROUP BY status;
```

Healthy: `delivered` dominates, `pending` is small and transient. Growing `failed_permanent` → webhook endpoint is broken. See [Failure scenarios → Webhook 4xx](/support/failure-scenarios#webhook-endpoint-rejecting-with-4xx).

### Q: The webhook endpoint changed — where do I update it?

`WEBHOOK_URL_PRIMARY` env var. See [Config updates → Change webhook URL](/support/config-updates#change-the-webhook-url).

### Q: Notifications for a particular problem are flooding

The `notify_outbox()` function auto-dedups by `(tenant_id, source, dedup_key)` within `outbox_dedup_window_minutes` (default 60). Look at `repeat_count`:

```sql
SELECT source, message, repeat_count, created_at
  FROM processed.notification_outbox
 WHERE created_at > now() - interval '2 hours'
   AND repeat_count > 5
 ORDER BY repeat_count DESC;
```

A row with high `repeat_count` means "N identical events collapsed into this one row". Fix the underlying issue rather than the notification.

## Config

### Q: What env vars is the pod actually running with?

```bash
kubectl exec -n <ns> deploy/psql-inventory-integration-service -- env \
  | grep -E '^(APP_PROFILE|RUN_MODE|DB_|FILE_SOURCE|FILES_COM_|SFTP_|WEBHOOK_|SCHEDULE_|HEALTH_|FLYWAY_|CONFIG_DIR|LOG_)' \
  | sort
```

Redact secrets before pasting anywhere. Startup logs also print a redacted config summary — look for `startup complete config=...`.

### Q: I updated the ConfigMap but the pod isn't picking it up

ConfigMap changes don't restart the pod automatically. Either:

```bash
kubectl rollout restart deploy/psql-inventory-integration-service -n <ns>
```

Or delete the pod (the Deployment will replace it):

```bash
kubectl delete pod -l app=psql-inventory-integration-service -n <ns>
```

## Audit / compliance

### Q: Who called `fetch_inventory_json` for tenant X in the last hour?

```sql
SELECT at, ref_id AS caller, latency_ms, rows_affected, payload
  FROM audit.event_log
 WHERE event_type = 'atp.queried'
   AND tenant_id = 'MNA'
   AND at > now() - interval '1 hour'
 ORDER BY at DESC;
```

### Q: All promotions for tenant X in the last 24 hours

```sql
SELECT at, source, event_type, correlation_id, error_msg
  FROM audit.v_all_events
 WHERE tenant_id = 'MNA'
   AND event_type LIKE 'promotion.%'
   AND at > now() - interval '24 hours'
 ORDER BY at DESC;
```

### Q: I need a report of stock movements for product X between two dates

```sql
SELECT at, payload->>'signed_qty' AS qty, payload->>'txn_type' AS type,
       payload->>'warehouse_code' AS warehouse, correlation_id
  FROM audit.v_stock_change_events
 WHERE payload->>'product_code' = 'PROD-A'
   AND at BETWEEN '2026-07-01' AND '2026-07-31'
 ORDER BY at;
```

## Data cleanup / retention

### Q: Old superseded staging rows are piling up

`staging.txn_inbox` / `staging.order_inbox` are append-only by design — every SFDC/FOP state change lands as a new row. Superseded rows are safe to purge after N days:

```sql
-- Delete superseded staging rows older than 30 days (customize N)
DELETE FROM staging.txn_inbox
 WHERE status IN ('superseded','processed')
   AND processed_at < now() - interval '30 days';

DELETE FROM staging.order_inbox
 WHERE status IN ('superseded','processed')
   AND processed_at < now() - interval '30 days';
```

Rejected rows are worth keeping longer (they're diagnostic history):

```sql
DELETE FROM staging.txn_inbox
 WHERE status = 'rejected'
   AND processed_at < now() - interval '90 days';
```

Run these as a periodic maintenance job — daily / weekly, up to your operational cadence.

### Q: `audit.event_log` is growing large

At 5k records/day + heartbeats + ATP queries, it's ~1-2 MB/day → ~500 MB/year. If storage becomes a concern:

```sql
-- Delete events older than 90 days
DELETE FROM audit.event_log
 WHERE at < now() - interval '90 days';
```

Better: partition by month via `pg_partman` when installed. Not shipped in v6.

### Q: `notification_outbox` — delivered rows never age out

```sql
-- Delete delivered outbox rows older than 30 days
DELETE FROM processed.notification_outbox
 WHERE status = 'delivered'
   AND delivered_at < now() - interval '30 days';
```

## When to page whom

| Signal | Severity | Who |
|---|---|---|
| `/actuator/health` DOWN > 5 min | P1 | On-call (Java) |
| `file.failed` count > 5 in last hour | P2 | On-call (Java) + Michelin extract team |
| `promotion.failed` rate > 10% over 15 min | P2 | FOP or SFDC integration team |
| No `daemon.heartbeat` for > 20 min | P1 | On-call (Java) |
| `notification_outbox.failed_permanent` new rows | P2 | On-call — webhook endpoint issue |
| `stock_balance` numbers wrong vs source | P2 | On-call (DB) — run `recalculate_stock_balance` first |
| DB unreachable | P1 | DBA team |
| files.com unreachable | P2 | Michelin infra |
| Suspected credential leak | P1 | SecOps |
