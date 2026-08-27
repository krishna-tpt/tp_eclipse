---
title: Troubleshooting
description: Common failures and their remedies.
---

Grouped by symptom. Each entry names the underlying cause and the fix.

## Startup fails immediately

### `ConfigValidationException: missing required field 'db.url'`

**Cause:** required env var not set.

**Fix:** verify env vars are present in the pod:

```bash
kubectl exec deploy/psql-inventory-integration-service -- env | grep DB_
```

Cross-check against [Required env vars](/reference/environment-variables#required).

### `Flyway migrate failed: … cannot apply migration V… — checksum mismatch`

**Cause:** JAR's Flyway migrations are out of sync with the customer's DB (customer schema was applied via `customer_install.sql`, not via the JAR's migrations).

**Fix:** set `FLYWAY_ENABLED=false`. The JAR then skips its own migrations and trusts the DB is correctly installed.

### `PSQLException: FATAL: password authentication failed for user "inventoryledger_app"`

**Cause:** wrong `DB_PASSWORD`, or role doesn't exist, or role has no `LOGIN` privilege.

**Fix:**

```sql
-- as postgres superuser
ALTER ROLE inventoryledger_app WITH LOGIN PASSWORD '<from-vault>';
```

### `PSQLException: connect timed out`

**Cause:** DB unreachable — network, firewall, private endpoint, or wrong host.

**Fix:** `psql "$DB_URL" -c 'SELECT 1'` from inside the pod. If that fails, escalate to networking.

## Ingest fails

### File goes to `reject/` with reason `no catalog matched`

**Cause:** filename doesn't match any catalog's `filename_pattern`.

**Fix:** check the file name against the pattern in `mich_inv_stocklevel_batch.yaml`:

```
^MICH_INV_STOCKLEVEL_(SMS|DMC|BATCH)_\d{3}[._].+$
```

If Michelin sent a new naming shape (e.g., a different tenant code prefix), update the pattern in the catalog YAML and rebuild.

### File goes to `reject/` with reason `header_confirms failed`

**Cause:** `HEADER_FILE.field 3` is not `MNA`. Either the wrong tenant sent the file, or the format changed.

**Fix:** open the file, read the first line, check field 3. If Michelin has legitimately changed the tenant code, update `header_confirms.equals` in the YAML.

### File goes to `reject/` with reason `expected N fields, got M`

**Cause:** field count on some data record doesn't match the catalog's `records[].fields:` count.

**Fix:** count the fields on a sample line. If Michelin added a column (unlikely for Michelin — they'd usually remove one), update the catalog and add the corresponding column to `staging.stocklevel_inbox` via an alter file. If it's an exporter bug on their side, escalate to Michelin.

### File goes to `reject/` with reason `envelope validation failed: total_row_count 5 != 6`

**Cause:** `FOOTER_FILE.field 2` (declared row count) doesn't match the count of data records actually in the file.

**Fix:** this is a Michelin exporter bug — the file is malformed. Escalate. Don't remove the validator; the whole point is catching mid-transfer truncation.

### `FieldMappingException: required column 'item_segment1' is null on line 42`

**Cause:** a data row has a blank field 10 (`item_segment1`), which is marked `required: true`.

**Fix:** open the file at line 42, verify. Escalate to Michelin if the blank field is legitimate data.

### `FieldMappingException: value 'ABC' not in allowed_values [ONHAND, RECEIVING] on line 87`

**Cause:** `material_location` has a value outside the whitelist.

**Fix:** if the new value is legitimate, add it to `allowed_values:` in the catalog. Otherwise, escalate.

### `duplicate row on line X and Y (natural key match)`

**Cause:** the within-file dedup catches two rows agreeing on every column in `dedupe_keys:`. Michelin exporter bug.

**Fix:** escalate. The catch is intentional — accepting the duplicate would double-count the stock position.

## Promotion fails

### `staging.txn_inbox` row status stays `pending` forever

**Cause:** the trigger fired but hit an unhandled path, OR the trigger isn't attached.

**Fix:** check trigger exists:

```sql
SELECT tgname, tgenabled FROM pg_trigger
 WHERE tgname = 'trg_txn_inbox_promote';
```

Should be one row with `tgenabled = 'O'`. If missing, the schema install didn't complete — re-run `customer_install.sql`.

### Trigger fires but row status = `rejected` with `reject_reason='unknown_product'`

**Cause:** payload references a product code not in `processed.product` for that tenant.

**Fix:** either FOP is sending bad data (escalate) or the master data hasn't been seeded (fix `customer_seed.sql` and re-apply).

### Cascade doesn't close the line — `sfdc_order_line.line_state` stays `open`

**Cause:** shipment txn payload doesn't carry `erp_line_id`. The header-only fallback (`sfdc_order_id`) can't disambiguate multi-line orders.

**Fix:** FOP payload must include `erp_line_id` (Oracle `SOURCE_LINE_ID`). This is the v6 contract. Fixed by v6 alter_01 trigger rewrite — no code change on our side; escalate to FOP.

### Reservation drift — `stock_balance.reserved_qty` doesn't match `sum(sfdc_order_line.qty - shipped_qty)` for open lines

**Cause:** trigger outage during a batch of order updates, or manual DDL that bypassed the trigger.

**Fix:**

```sql
SELECT processed.recalculate_stock_balance();
```

Idempotent; safe to run any time — but blocks concurrent writes to `stock_balance` for the duration.

## Files.com issues

### `HTTP 401 Unauthorized` on file list

**Cause:** API key expired, revoked, or wrong.

**Fix:** rotate via files.com dashboard; update `FILES_COM_API_KEY` in the K8s Secret.

### `HTTP 403 FullPermissionRequired` on file move

**Cause:** API key has read/list but not move/delete on the target folder.

**Fix:** files.com dashboard → key permissions → grant `full_permission` on the pickup/archive/reject folders. Or switch to a service-account key that has folder-level access (Michelin's `DEV-BR-C10-EU` account).

### `SSLHandshakeException: PKIX path building failed`

**Cause:** corporate MITM proxy rewrites TLS; JDK doesn't trust the corporate CA.

**Fix:** on Windows JVMs, add `-Djavax.net.ssl.trustStoreType=Windows-ROOT` so the JDK inherits the Windows CA store. In K8s, the pod is on the trusted network — this only affects VDI / developer boxes.

## SFTP issues

### `SocketTimeoutException: connect timed out` after 10 s

**Cause:** corporate firewall blocks outbound port 22.

**Fix:** SFTP from Michelin VDI is blocked. Either run from the K8s cluster (private endpoint), or use `FILE_SOURCE=filescom` (443 is unblocked).

### `SFtpException: Auth fail`

**Cause:** SSH key not present, wrong permissions (0600 required), wrong path, or key isn't associated with the SFTP user account.

**Fix:** verify `SFTP_PRIVATE_KEY_PATH` file exists in the pod at mode 0600. Test with:

```bash
kubectl exec deploy/psql-inventory-integration-service -- \
  ssh -i $SFTP_PRIVATE_KEY_PATH -p $SFTP_PORT $SFTP_USER@$SFTP_HOST 'ls'
```

### `Host key verification failed`

**Cause:** production posture — `SFTP_KNOWN_HOSTS_PATH` is set and the server's host key isn't in the known_hosts file.

**Fix:** capture the host key once (`ssh-keyscan -p 22 <host>`) and add it to the known_hosts file mounted at `SFTP_KNOWN_HOSTS_PATH`. Do NOT unset the var — that's a downgrade to insecure.

## Notifications not delivered

### `notification_outbox.status='failed_permanent'` new rows appearing

**Cause:** webhook endpoint is rejecting with 4xx (400 bad request, 401 unauthorized, 403 forbidden).

**Fix:** inspect the payload — is the shape right? Check webhook endpoint config. Once fixed, ops can manually reset:

```sql
UPDATE processed.notification_outbox
   SET status='pending', retry_count=0
 WHERE status='failed_permanent'
   AND created_at > now() - interval '24 hours';
```

Next tick's drain will re-attempt.

### `notification_outbox.pending` count growing unbounded

**Cause:** outbox drainer isn't running. Either the daemon is dead, or the outbox drain step is failing.

**Fix:** `kubectl logs -f` — look for `outbox-drain` messages. If none, the tick isn't reaching that step (upstream failure). If they show errors, look at the exceptions.

## The daemon looks alive but no files are being processed

### `daemon.heartbeat` events keep appearing, but no `file.parsed`

**Cause:** no files in the pickup folder, OR files there don't match the filename pattern.

**Fix:**

```
# List files in the pickup folder (from the daemon's perspective)
kubectl logs deploy/psql-inventory-integration-service | grep 'file-picked' | tail -5
```

Empty list = no matching files. Log into files.com and verify what's in the pickup folder.

### Files present but daemon skips them

**Cause:** files were already processed — hash-dedup caught them. This is correct behavior.

**Fix:** if you want to re-process, either drop the file with a different name, or delete the corresponding row from `staging.stocklevel_batch`:

```sql
DELETE FROM staging.stocklevel_batch WHERE file_name = '<name>';
DELETE FROM processed.opening_balance WHERE source_file = '<name>';
-- (delete cascade on stocklevel_inbox not needed — hash-dedup uses batch, not inbox)
```

Then re-drop the file into pickup.
