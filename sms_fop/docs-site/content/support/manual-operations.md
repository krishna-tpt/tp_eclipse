---
title: Manual operations
description: Every operator-runnable action — how to do it by hand.
---

Every operation you can perform against the running system without a code change. Assume you're `psql`-connected as `postgres` or an equivalent superuser role unless noted.

## Prerequisites

```bash
# Terminal 1 — port-forward to health endpoint
kubectl port-forward -n <ns> deploy/psql-inventory-integration-service 8080:8080

# Terminal 2 — psql to the customer DB
psql "$DB_URL"    # or the connection string your ops team uses
```

For runnable examples below, replace `<tenant>`, `<filename>`, `<inbox_id>`, `<outbox_id>` etc. with real values.

## Files

### Re-process a specific file

You need to re-process a file that was already processed (say the raw data changed, or an operator wants to force it). Steps:

```sql
-- 1. See what the existing batch looks like
SELECT * FROM staging.stocklevel_batch WHERE file_name = '<filename>';

-- 2. Wipe its promoted rows from opening_balance
DELETE FROM processed.opening_balance WHERE source_file = '<filename>';

-- 3. Wipe its staging rows so load_stocklevel doesn't skip
DELETE FROM staging.stocklevel_inbox WHERE file_name = '<filename>';

-- 4. Wipe the batch row
DELETE FROM staging.stocklevel_batch WHERE file_name = '<filename>';
```

Then re-drop the file into the pickup folder (or move it back from `archive/`), or let the daemon pick it up on its next tick. It'll process as if brand new.

### Force pickup right now (not wait for next tick)

The daemon runs `Application.run()` on cron ticks. To force one:

Option A — restart the pod. On startup, it always runs one tick immediately (this is `oneshot` mode behavior baked into the scheduler as well):

```bash
kubectl rollout restart deploy/psql-inventory-integration-service -n <ns>
```

Option B — run in `oneshot` mode via a one-off pod:

```bash
kubectl run -it --rm oneshot-run --restart=Never \
  --image=<same image as prod> \
  --env="RUN_MODE=oneshot" \
  --env-from=configMapRef=inventoryledger-config \
  --env-from=secretRef=inventoryledger-secrets \
  -- java -jar psql-inventory-integration-service-1.0.0.jar
```

Watch stdout until it prints `run-complete`, then it exits.

### Manually rescue a file from the reject folder

A file in `reject/` was rejected pre-staging. If you've fixed whatever was wrong (Michelin re-issued the file, or the catalog got updated), rescue it by moving it back to pickup:

**On files.com:**

- Log into the files.com UI, navigate to `/EU/PRD/BR/C10/Error`, move the file to `/EU/PRD/BR/C10/Inbound`. Next tick picks it up.

**Or via the API (if you have permissions):**

```bash
curl -X POST "https://filehub.michelin.net/api/rest/v1/file_actions/move" \
  -H "X-FilesAPI-Key: $FILES_COM_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"path":"/EU/PRD/BR/C10/Error/<filename>","destination":"/EU/PRD/BR/C10/Inbound"}'
```

### Promote a specific staging batch by hand

The daemon normally calls this. To do it yourself (e.g., daemon is down and you have transactions to run through):

```sql
-- Assumes staging.stocklevel_inbox already has the file's rows
-- and staging.stocklevel_batch has the batch entry in status='pending'
SELECT staging.load_stocklevel('<filename>');
-- Returns (accepted INTEGER, rejected INTEGER)
```

## Transactions & orders

### Drain pending transactions manually

Bypass the trigger (or catch up if the trigger was disabled during an outage):

```sql
SELECT staging.load_transactions();
-- Returns (processed, rejected) as row counts. Default limit 500 per call.
-- Re-run until (0, 0).
```

Same for orders:

```sql
SELECT staging.load_orders();
```

Both are idempotent — re-running only picks up new `pending` rows.

### Promote a single inbox row by ID

If you want to isolate one row for debugging:

```sql
-- Transaction
SELECT staging.promote_one_txn(<inbox_id>);
-- Order
SELECT staging.promote_one_order(<inbox_id>);
```

Both catch their own exceptions and set the row's `status` and `reject_reason` on failure. Look at the row after:

```sql
SELECT status, processed_at, reject_reason FROM staging.txn_inbox WHERE inbox_id = <inbox_id>;
```

### Reset a rejected staging row for retry

A row is stuck in `rejected` (maybe a master data lookup failed the first time; you've since added the master). Reset:

```sql
UPDATE staging.txn_inbox
   SET status = 'pending', reject_reason = NULL, processed_at = NULL
 WHERE inbox_id = <inbox_id>;

-- Triggers will fire on UPDATE? NO — auto-promote fires only on INSERT.
-- So call the promote function manually:
SELECT staging.promote_one_txn(<inbox_id>);
```

### Cancel / undo a promoted transaction

Transactions are immutable once in `processed.inv_transaction`. To reverse the effect, POST a compensating transaction with the opposite `signed_qty` and a distinct `external_txn_id`. The `stock_balance` trigger will apply the offset.

Do NOT delete rows from `processed.inv_transaction` — the audit trail depends on them, and `recalculate_stock_balance` reads them to rebuild.

## Stock balance recovery

### Rebuild `stock_balance` for one tenant

```sql
SELECT processed.recalculate_stock_balance('<tenant_code>');
-- Returns (rows_rebuilt INTEGER). Idempotent. ~1-2 s for 5k rows.
```

Reads latest `opening_balance` per natural key + all `inv_transaction` since that date + active `sfdc_order_line` reservations. Wipes and rewrites `stock_balance`. Safe to run any time — but blocks concurrent writes to `stock_balance` for the duration.

### Verify a specific product's rebuild

```sql
-- Before:
SELECT on_hand_qty, reserved_qty FROM processed.stock_balance
 WHERE tenant_code = 'MNA' AND product_code = 'PROD-A';

-- Rebuild:
SELECT processed.recalculate_stock_balance('MNA');

-- After — should match the baseline math:
--   opening_balance.qty (latest as_of_date row)
--   + SUM(inv_transaction.signed_qty since that date)
--   with reserved_qty = SUM(sfdc_order_line.qty for open+synced lines)
SELECT on_hand_qty, reserved_qty FROM processed.stock_balance
 WHERE tenant_code = 'MNA' AND product_code = 'PROD-A';
```

## Outbox

### Force retry all failed webhook deliveries

```sql
UPDATE processed.notification_outbox
   SET status = 'pending', retry_count = 0
 WHERE status IN ('failed','failed_permanent')
   AND created_at > now() - interval '24 hours';
```

Next daemon tick will re-attempt them. Only reset within a sensible time window (recent failures); older `failed_permanent` are usually structural.

### Manually mark a notification as delivered

You paged the on-call human by other means; the outbox row can be flushed:

```sql
UPDATE processed.notification_outbox
   SET status = 'delivered', delivered_at = now()
 WHERE outbox_id = <outbox_id>;
```

### Force-drain the outbox from a daemon tick

The daemon drains at the end of every tick. To force a drain right now, either restart the daemon (fastest — SIGTERM handler doesn't drain, but the next startup tick will) or wait for the next scheduled tick.

### Emit a test notification

```sql
SELECT processed.notify_outbox(
    NULL,                          -- tenant_id (NULL = system-wide)
    'INFO',
    'manual_test',
    'Test notification from support team',
    'manual_test:' || now()::text, -- dedup_key
    '{"note":"just checking the webhook"}'::jsonb
);
```

Next daemon tick drains it. Confirm delivery:

```sql
SELECT outbox_id, status, delivered_at FROM processed.notification_outbox
 WHERE source = 'manual_test' ORDER BY outbox_id DESC LIMIT 1;
```

## Scheduler

### Change the schedule without a code change

Update env vars via ConfigMap:

```bash
kubectl edit configmap inventoryledger-config -n <ns>
# change SCHEDULE_DAILY or SCHEDULE_HOURLY
```

Then restart the pod:

```bash
kubectl rollout restart deploy/psql-inventory-integration-service -n <ns>
```

The next log line after startup will show `scheduler-loop-started schedules=[...]` with the new values.

### Disable one of the schedules

Set to empty string. At least one must remain non-empty:

```yaml
env:
  SCHEDULE_DAILY: "0 6 * * *"
  SCHEDULE_HOURLY: ""             # disabled
```

### Pause the daemon completely

Scale the deployment to zero:

```bash
kubectl scale -n <ns> deploy/psql-inventory-integration-service --replicas=0
```

Files pile up in the pickup folder harmlessly (nothing consumes them). Transactions from FOP still promote via triggers (those run entirely in the DB). Restart with `--replicas=1` to resume — the next tick catches up on the file backlog.

## Master data

### Add a new product / warehouse / uom / lot

Master data changes go through the DBA, not through this service. Example:

```sql
INSERT INTO processed.product (tenant_id, product_code, name)
VALUES ((SELECT tenant_id FROM processed.tenant WHERE tenant_code='MNA'),
        'PROD-NEW', 'New product name');
```

After adding, any `staging.txn_inbox` rows that were `rejected` with `reject_reason='unknown_product'` for this code can be reset and re-promoted (see [Reset a rejected staging row](#reset-a-rejected-staging-row-for-retry)).

### Deactivate a product

```sql
UPDATE processed.product SET is_active = false
 WHERE tenant_id = (SELECT tenant_id FROM processed.tenant WHERE tenant_code='MNA')
   AND product_code = 'PROD-OLD';
```

Historic transactions and stock rows survive.

## Emergency: full stop

If something is very wrong and you need to stop everything immediately:

```bash
# Stop the daemon
kubectl scale -n <ns> deploy/psql-inventory-integration-service --replicas=0

# Optionally disable the inbox triggers to stop FOP/SFDC promotions
```

```sql
ALTER TABLE staging.txn_inbox   DISABLE TRIGGER trg_txn_inbox_promote;
ALTER TABLE staging.order_inbox DISABLE TRIGGER trg_order_inbox_promote;
```

Then investigate. To resume:

```sql
ALTER TABLE staging.txn_inbox   ENABLE TRIGGER trg_txn_inbox_promote;
ALTER TABLE staging.order_inbox ENABLE TRIGGER trg_order_inbox_promote;
```

```bash
kubectl scale -n <ns> deploy/psql-inventory-integration-service --replicas=1
```

Then drain the backlog:

```sql
SELECT staging.load_transactions();
SELECT staging.load_orders();
```

If a stock file arrived during the pause, the daemon's next tick picks it up automatically. See [Failure scenarios → Trigger outage](/support/failure-scenarios#the-auto-promote-triggers-stopped-firing).
