---
title: Failure scenarios
description: Recovery walkthroughs for the most common failures.
---

Every scenario follows the same shape: **symptoms → diagnosis → recovery → verify**. Copy-paste-safe commands.

## Health probe reports DOWN

### Symptoms

- `curl http://localhost:8080/actuator/health` returns HTTP 503 or `{"status":"DOWN",...}`
- K8s restarts the pod repeatedly (crashloop)
- No new `daemon.heartbeat` events

### Diagnosis

```bash
# 1. What does the health response say?
kubectl exec -n <ns> deploy/psql-inventory-integration-service -- \
  curl -s http://localhost:8080/actuator/health | jq

# 2. Recent pod logs
kubectl logs -n <ns> deploy/psql-inventory-integration-service --tail=200

# 3. Recent restarts
kubectl get pods -n <ns> -l app=psql-inventory-integration-service
```

`checks.db.status = DOWN` → DB unreachable (see below).  
`checks.scheduler.status = DOWN` → scheduler thread died — bug or JVM crash. Check logs for a stack trace.

### Recovery

- **`db-ping-failed`** → verify DB is up (`psql "$DB_URL" -c 'SELECT 1'` from a bastion). If DB is fine, check the pod's `DB_URL`/`DB_PASSWORD` env. Rotate password per [Config updates](/support/config-updates#rotate-the-db-password) if creds are stale.
- **`scheduler-dead`** → this shouldn't happen — the loop swallows per-tick exceptions and continues. If it does, restart the pod. Then look at the log stack trace and escalate.

### Verify

```bash
curl http://localhost:8080/actuator/health   # 200 UP
```

```sql
SELECT max(at) FROM audit.event_log WHERE event_type = 'daemon.heartbeat';
-- should be within the last few minutes
```

## DB unreachable

### Symptoms

- Health probe `checks.db.status = DOWN` with reason `db-ping-failed`
- Pod logs: `PSQLException: connect timed out` or `Connection refused`
- No new promotions, no new heartbeats

### Diagnosis

```bash
# From the pod itself, try connecting
kubectl exec -n <ns> deploy/psql-inventory-integration-service -- \
  sh -c 'nc -zv <db-host> 5432 && echo "TCP ok"'

# Full JDBC test
kubectl exec -n <ns> deploy/psql-inventory-integration-service -- \
  psql "$DB_URL" -c 'SELECT 1'
```

Fails at TCP level → network / firewall / private endpoint. Fails at psql level → DB up but auth or SSL wrong.

### Recovery

- **Network** — escalate to Michelin infra / DBA
- **Auth** — [Rotate the DB password](/support/config-updates#rotate-the-db-password)
- **SSL** — `DB_URL` must include `?sslmode=require` for Azure. Fix in ConfigMap, restart pod.

Once DB is reachable, the daemon self-recovers on the next tick. No manual replay needed — file backlog processes normally; FOP/SFDC promotions were queuing at their sources.

### Verify

```sql
SELECT count(*) FROM staging.txn_inbox WHERE status = 'pending';
-- should decrease over the next few minutes as promotions catch up
```

## Files.com unreachable

### Symptoms

- Pod logs: `SocketTimeoutException` or `HTTP 401 Unauthorized` or `HTTP 403`
- `audit.event_log` gets no new `file.picked` events despite files existing in pickup
- Daemon otherwise appears healthy (DB fine, scheduler firing, heartbeat OK)

### Diagnosis

```bash
kubectl exec -n <ns> deploy/psql-inventory-integration-service -- \
  curl -sv "https://filehub.michelin.net/api/rest/v1/folders/EU/PRD/BR/C10/Inbound" \
       -H "X-FilesAPI-Key: $FILES_COM_API_KEY" 2>&1 | head -30
```

- **HTTP 401** — API key invalid or expired
- **HTTP 403** — key valid but lacks folder-level permission (`FullPermissionRequired`)
- **Timeout** — network or files.com is down
- **HTTP 200** — files.com is fine; problem is elsewhere in the daemon

### Recovery

- **401** — [Rotate the files.com API key](/support/config-updates#rotate-the-files-com-api-key)
- **403** — files.com dashboard → API key → grant folder-level permission on pickup / archive / reject
- **Timeout** — escalate to Michelin infra

### Verify

Next tick fires and the log shows `file-picked file=...`. If files were queuing during the outage, they all process in one batch.

## The auto-promote triggers stopped firing

### Symptoms

- `staging.txn_inbox` or `staging.order_inbox` rows piling up in `status='pending'` and never moving
- No `promotion.success` or `promotion.failed` events in `audit.v_all_events` for the affected tables
- DB looks otherwise healthy

### Diagnosis

```sql
-- Verify the triggers exist and are enabled
SELECT tgname, tgenabled, tgrelid::regclass AS table_name
  FROM pg_trigger
 WHERE tgname LIKE 'trg_%_inbox_promote';
```

Expected: two rows, both with `tgenabled = 'O'` (Origin — enabled). If `tgenabled = 'D'`, they've been disabled.

If they don't exist at all, someone dropped them or the customer install wasn't fully applied.

### Recovery

**If disabled:**

```sql
ALTER TABLE staging.txn_inbox   ENABLE TRIGGER trg_txn_inbox_promote;
ALTER TABLE staging.order_inbox ENABLE TRIGGER trg_order_inbox_promote;
```

**If missing:** re-apply the trigger definitions from `deploy/11-06-v6-customer/alter_01_inbox_promote_triggers.sql`.

**Then drain the backlog in order — see [Recovery sequence](#recovery-sequence-after-trigger-outage) below.**

### Recovery sequence after trigger outage

Order matters. Baseline first, then transactions, then orders — same reasoning as the [Manual operations → Emergency section](/support/manual-operations#emergency-full-stop) explains.

```sql
-- 1. Drain any pending stock files (usually none — files use daemon path, not triggers)
SELECT file_name FROM staging.stocklevel_batch WHERE status = 'pending';
-- for each: SELECT staging.load_stocklevel('<file_name>');

-- 2. Drain pending transactions — up to 500 per call, re-run until (0,0)
SELECT staging.load_transactions();

-- 3. Drain pending orders
SELECT staging.load_orders();

-- 4. Sanity check
SELECT processed.recalculate_stock_balance('MNA');
```

### Verify

```sql
-- All backlog cleared
SELECT status, count(*) FROM staging.txn_inbox   GROUP BY status;
SELECT status, count(*) FROM staging.order_inbox GROUP BY status;

-- New promotions flowing again
SELECT count(*) FROM audit.v_all_events
 WHERE event_type LIKE 'promotion.%'
   AND at > now() - interval '10 minutes';
```

## A single file is stuck in `reject/`

### Symptoms

- File appears in `reject/` folder on files.com
- `audit.event_log` shows a `file.failed` for it
- No `stocklevel_batch` row (or one in `status='failed'`)

### Diagnosis

```sql
SELECT at, error_msg, payload
  FROM audit.event_log
 WHERE event_type = 'file.failed'
   AND correlation_id = '<filename>'
 ORDER BY at DESC LIMIT 1;
```

Common `error_msg` values:

- `no catalog matched` → filename regex didn't match. Check the catalog's `filename_pattern`.
- `header_confirms failed` → header field 3 wasn't `MNA`. Wrong tenant, or format changed.
- `expected N fields, got M` → data row field count mismatch. Michelin exporter change.
- `envelope validation failed: total_row_count X != Y` → footer count doesn't match rows. Truncated file.
- `FieldMappingException: value 'X' not in allowed_values [...]` → new value from Michelin, catalog whitelist needs updating.
- `duplicate row on line X and Y` → within-file natural-key duplicate, exporter bug.

### Recovery

Depends on the cause. See [Troubleshooting → Ingest fails](/operations/troubleshooting#ingest-fails) for exact remedies per error.

For a legitimate one-off fix (e.g., Michelin re-sent a valid file with the same name), delete the failure trace and drop the fixed file back to pickup:

```sql
-- Cleanup so re-processing isn't blocked
DELETE FROM staging.stocklevel_batch WHERE file_name = '<filename>';
DELETE FROM staging.stocklevel_inbox WHERE file_name = '<filename>';
```

Then move the corrected file back to pickup via files.com UI.

### Verify

Next tick processes it — new `file.parsed` event with `status='loaded'`.

## Webhook endpoint rejecting with 4xx

### Symptoms

- `notification_outbox.failed_permanent` count growing
- Same source repeatedly failing (grep by `source` + `message`)

### Diagnosis

```sql
SELECT source, message, retry_count, status, created_at
  FROM processed.notification_outbox
 WHERE status = 'failed_permanent'
   AND created_at > now() - interval '2 hours'
 ORDER BY created_at DESC LIMIT 20;
```

Check pod logs for the HTTP response:

```bash
kubectl logs -n <ns> deploy/psql-inventory-integration-service --tail=500 \
  | grep -A2 'webhook-post-failed'
```

- **400** — payload shape mismatch. Endpoint expects something the notifier isn't sending.
- **401 / 403** — auth or ACL problem with the endpoint.
- **404** — URL is wrong.

### Recovery

- Fix the endpoint (their side) or fix the URL (our side — see [Change the webhook URL](/support/config-updates#change-the-webhook-url))
- After fix, reset failed rows for retry:

```sql
UPDATE processed.notification_outbox
   SET status = 'pending', retry_count = 0
 WHERE status = 'failed_permanent'
   AND created_at > now() - interval '24 hours';
```

### Verify

Next tick's drain retries them. Watch:

```sql
SELECT status, count(*) FROM processed.notification_outbox
 WHERE created_at > now() - interval '2 hours' GROUP BY status;
```

## Stock numbers look wrong

### Symptoms

- `fetch_inventory_json` returns numbers that don't match what business expects
- SFDC reports discrepancies

### Diagnosis

Sanity check math for one product:

```sql
-- What's currently in stock_balance
SELECT on_hand_qty, reserved_qty FROM processed.stock_balance
 WHERE tenant_code = 'MNA' AND product_code = 'PROD-A' AND warehouse_code = 'WH-01';

-- What the latest opening_balance says (source of truth for baseline)
SELECT as_of_date, qty FROM processed.opening_balance
 WHERE tenant_code = 'MNA' AND product_code = 'PROD-A' AND warehouse_code = 'WH-01'
 ORDER BY as_of_date DESC LIMIT 3;

-- Transactions since that date
SELECT SUM(signed_qty), count(*) FROM processed.inv_transaction
 WHERE tenant_code = 'MNA' AND product_code = 'PROD-A' AND warehouse_code = 'WH-01'
   AND posted_at >= (
     SELECT max(as_of_date) FROM processed.opening_balance
      WHERE tenant_code='MNA' AND product_code='PROD-A' AND warehouse_code='WH-01'
   );

-- Active reservations
SELECT SUM(qty - shipped_qty) FROM processed.sfdc_order_line
 WHERE tenant_code = 'MNA' AND product_code = 'PROD-A' AND warehouse_code = 'WH-01'
   AND line_state IN ('open', 'synced');
```

Expected: `on_hand_qty = opening_balance.qty + SUM(inv_transaction.signed_qty)` (subject to subinv/lot grouping — the above rolls up).

Mismatch → `stock_balance` drifted. Look at the outbox for `back-dated opening_balance` warnings that suggest a manual `recalculate_stock_balance` was needed at some point.

### Recovery

```sql
SELECT processed.recalculate_stock_balance('MNA');
-- Returns rows_rebuilt count. ~1-2 s for 5k rows.
```

### Verify

Re-run the sanity queries above. Should now agree.

## The daemon is running but no ticks are firing

### Symptoms

- Pod is UP, health probe OK
- No new `file.picked` or `file.parsed` events
- Heartbeat events keep appearing (so scheduler thread is alive)

### Diagnosis

Two possibilities:

1. **Nothing to do.** Pickup folder is empty and no files have changed. Verify:
   ```bash
   # Get the last few file-picked entries — if empty, pickup is empty
   kubectl logs -n <ns> deploy/psql-inventory-integration-service --tail=500 | grep 'file-picked' | tail -5
   ```
2. **Schedule config wrong.** Check what schedules are actually loaded:
   ```bash
   kubectl logs -n <ns> deploy/psql-inventory-integration-service | grep 'scheduler-loop-started'
   ```
   You should see something like `scheduler-loop-started schedules=[daily(0 6 * * *), hourly(5 * * * *)]`. If the expected schedule is missing, the env var wasn't applied.

### Recovery

- **Empty pickup** — no action; genuine idle state.
- **Wrong schedule** — update the ConfigMap and restart:
  ```bash
  kubectl edit configmap inventoryledger-config -n <ns>  # set SCHEDULE_DAILY / SCHEDULE_HOURLY
  kubectl rollout restart deploy/psql-inventory-integration-service -n <ns>
  ```

## Time zone drift — cron fires at the wrong wall-clock time

### Symptoms

- `SCHEDULE_DAILY=0 6 * * *` is supposed to fire at 06:00 Michelin time but actually fires at 07:00 or 08:00

### Diagnosis

```bash
kubectl exec -n <ns> deploy/psql-inventory-integration-service -- date
kubectl exec -n <ns> deploy/psql-inventory-integration-service -- env | grep TZ
```

If TZ shows UTC (or is unset), the JVM interprets `0 6 * * *` as 06:00 UTC — 08:00 in Amsterdam (summer) or 07:00 (winter).

### Recovery

Set `TZ` env in the ConfigMap:

```yaml
env:
  TZ: Europe/Amsterdam
```

Restart the pod.

### Verify

```bash
kubectl exec -n <ns> deploy/psql-inventory-integration-service -- date
# should now show local time
kubectl logs -n <ns> deploy/psql-inventory-integration-service | grep 'next-fire'
# should show the next fire timestamp in local time
```

## Two operators promoting concurrently

### Symptoms

- Rare in practice — but you're running `load_transactions()` and someone else is too, or the trigger fires while you run it manually.

### Diagnosis

Not a problem. Both `load_transactions` / `load_orders` use `FOR UPDATE SKIP LOCKED` — parallel-safe by design. Each call picks up whatever's currently `pending` and locks those rows for its own processing. No row processes twice.

### Recovery

None needed. Both calls complete. The union of their (processed, rejected) counts equals what was pending at the start.

## Master data reference broken

### Symptoms

- Bulk of new transactions rejecting with `unknown_reference` in outbox
- FOP team asks "why are you rejecting all our transactions?"

### Diagnosis

```sql
-- What's rejected in the last hour
SELECT reject_reason, count(*) FROM staging.txn_inbox
 WHERE status = 'rejected' AND processed_at > now() - interval '1 hour'
 GROUP BY reject_reason
 ORDER BY 2 DESC;
```

- `unknown_reference` — payload references a product/warehouse/uom/lot that doesn't exist in the master table for that tenant.

### Recovery

Either add the master (via a manual INSERT) or ask FOP to fix its extract. Once master exists, reset the rejected rows for retry:

```sql
UPDATE staging.txn_inbox
   SET status = 'pending', reject_reason = NULL, processed_at = NULL
 WHERE status = 'rejected'
   AND reject_reason = 'unknown_reference'
   AND processed_at > now() - interval '24 hours';

SELECT staging.load_transactions();
```

### Verify

```sql
-- Should now succeed
SELECT status, count(*) FROM staging.txn_inbox
 WHERE received_at > now() - interval '2 hours'
 GROUP BY status;
```
