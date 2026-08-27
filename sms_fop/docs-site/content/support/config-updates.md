---
title: Configuration updates
description: How to rotate credentials, change URLs, and update runtime knobs safely.
---

Every runtime knob lives in env vars (via K8s Secret or ConfigMap). Config changes require a pod restart — env vars are read once at startup.

## Golden rules

1. **Never commit secrets to git.** Real values live in the K8s Secret; the `.env.example` in the repo is a template with placeholders.
2. **Always verify after restart.** Watch the pod logs for `startup complete config=...` (redacted).
3. **Change one thing at a time.** If you rotate the API key and change the schedule in one edit, you don't know which one broke it if the pod fails.
4. **Have the old value handy** until the new one is verified working — rollback is `kubectl edit` back.

## Rotate the files.com API key

The API key has full permission on the `pickup/archive/reject` folders. Rotate whenever the key is suspected leaked or on your regular rotation cadence.

### Steps

```bash
# 1. Generate the new key
#    files.com UI → API Keys → New Key → grant folder-level "full permission" on
#    /EU/PRD/BR/C10/{Inbound,Archive,Error}

# 2. Update the K8s Secret (encoded)
NEW_KEY='<paste from files.com>'
kubectl create secret generic inventoryledger-secrets \
  --from-literal=FILES_COM_API_KEY="$NEW_KEY" \
  --from-literal=DB_PASSWORD="$(kubectl get secret inventoryledger-secrets -o jsonpath='{.data.DB_PASSWORD}' | base64 -d)" \
  --from-literal=WEBHOOK_URL_PRIMARY="$(kubectl get secret inventoryledger-secrets -o jsonpath='{.data.WEBHOOK_URL_PRIMARY}' | base64 -d)" \
  --dry-run=client -o yaml | kubectl apply -f - -n <ns>

# 3. Restart the pod so it picks up the new value
kubectl rollout restart deploy/psql-inventory-integration-service -n <ns>

# 4. Watch logs for the next tick
kubectl logs -n <ns> deploy/psql-inventory-integration-service -f | grep -E 'file-picked|HTTP 4|SocketTimeout'
```

### Verify

Successful pickup in the next tick → old key can be revoked in the files.com UI.

If the next tick shows `HTTP 401 Unauthorized`, the new key isn't right (mistyped, or missing folder permissions). Rollback to old key by re-applying the previous secret value, and try again.

## Rotate the DB password

Zero-downtime approach: run two roles briefly, or the simpler approach — accept a few seconds of failed ticks.

### Steps (simple)

```sql
-- 1. As postgres superuser, change the password
ALTER ROLE inventoryledger_app WITH PASSWORD '<new-strong-password>';
```

```bash
# 2. Update the Secret
kubectl edit secret inventoryledger-secrets -n <ns>
# Change DB_PASSWORD (base64-encoded)

# 3. Restart the pod
kubectl rollout restart deploy/psql-inventory-integration-service -n <ns>

# 4. Watch health probe come back UP
kubectl exec -n <ns> deploy/psql-inventory-integration-service -- \
  curl -s http://localhost:8080/actuator/health | jq
```

Between step 1 and step 3, the daemon will fail one or two health-check pings and any tick that fires — K8s will restart the pod, which will keep failing until step 3 completes. If you want zero downtime, do steps 1 and 3 close together (script the whole thing) or add a temporary second role and cut over.

### Verify

```sql
-- Old sessions are invalidated but new ones with the new password succeed
SELECT count(*) FROM staging.txn_inbox WHERE status = 'pending';
-- returns instantly ⇒ auth is working
```

## Change the webhook URL

### Steps

```bash
kubectl edit secret inventoryledger-secrets -n <ns>
# Change WEBHOOK_URL_PRIMARY (base64-encoded)

kubectl rollout restart deploy/psql-inventory-integration-service -n <ns>
```

### Verify

Emit a test notification and watch delivery:

```sql
SELECT processed.notify_outbox(
    NULL, 'INFO', 'webhook_test', 'testing new URL',
    'webhook_test:' || now()::text, '{}'::jsonb);
```

```sql
-- Wait ~1 min for a scheduled tick to drain
SELECT status, delivered_at FROM processed.notification_outbox
 WHERE source = 'webhook_test' ORDER BY outbox_id DESC LIMIT 1;
```

`status='delivered'` → new URL is receiving. Anything else → check the pod logs for the HTTP response code.

## Update the cron schedule

### Steps

```bash
kubectl edit configmap inventoryledger-config -n <ns>
# Change SCHEDULE_DAILY or SCHEDULE_HOURLY
```

Setting either to an empty string disables it. At least one must remain non-empty in `scheduled` mode.

```bash
kubectl rollout restart deploy/psql-inventory-integration-service -n <ns>
```

### Verify

```bash
kubectl logs -n <ns> deploy/psql-inventory-integration-service | grep 'scheduler-loop-started'
# → scheduler-loop-started schedules=[daily(0 6 * * *), hourly(15 * * * *)]

kubectl logs -n <ns> deploy/psql-inventory-integration-service | grep 'next-fire'
# → next-fire at=2026-07-21T06:00+02:00 in=42000 ms
```

## Update file-source paths

Rare but comes up if files.com folder structure changes.

### Steps

```bash
kubectl edit configmap inventoryledger-config -n <ns>
# Change FILES_COM_PICKUP_PATH / FILES_COM_ARCHIVE_PATH / FILES_COM_REJECT_PATH
```

```bash
kubectl rollout restart deploy/psql-inventory-integration-service -n <ns>
```

### Verify

Next tick logs `file-picked` from the new pickup path. `file.picked` events in `audit.event_log` will reference the new absolute path.

## Switch file source (filescom → sftp, or vice versa)

Emergency-only — used if files.com is unreachable for an extended period and Michelin can also drop to SFTP.

### Steps

```bash
kubectl edit configmap inventoryledger-config -n <ns>
```

```yaml
env:
  FILE_SOURCE: sftp                        # was: filescom
  SFTP_HOST: filehub.michelin.net
  SFTP_PORT: "22"
  SFTP_USER: DEV-BR-C10-EU
  SFTP_PRIVATE_KEY_PATH: /secrets/sftp_id_rsa
  SFTP_KNOWN_HOSTS_PATH: /secrets/sftp_known_hosts
  SFTP_PICKUP_PATH: /EU/PRD/BR/C10/Inbound
  SFTP_ARCHIVE_PATH: /EU/PRD/BR/C10/Archive
  SFTP_REJECT_PATH: /EU/PRD/BR/C10/Error
```

Mount the SSH key + known_hosts via a Secret + volume mount.

```bash
kubectl rollout restart deploy/psql-inventory-integration-service -n <ns>
```

### Verify

```bash
kubectl logs -n <ns> deploy/psql-inventory-integration-service | grep -E 'file-source|file-picked'
```

## Change RUN_MODE (scheduled ↔ oneshot)

You almost never need to do this in prod. `oneshot` is for dev / CI / one-off backfills.

If you legitimately want a one-off backfill run in prod (say, replay a batch of files), spin up a temporary pod instead of switching the main deployment:

```bash
kubectl run -it --rm oneshot-run --restart=Never \
  --image=<same image> \
  --env="RUN_MODE=oneshot" \
  --env-from=configMapRef=inventoryledger-config \
  --env-from=secretRef=inventoryledger-secrets \
  -- java -jar psql-inventory-integration-service-1.0.0.jar
```

Watch stdout until it prints `run-complete`, then it exits. The main deployment keeps running its own schedule undisturbed.

## Disable Flyway migrations

Customer prod owns the schema via `customer_install.sql` — Flyway must not try to run its own migrations.

```yaml
env:
  FLYWAY_ENABLED: "false"
```

Restart. Logs should show `flyway-skipped FLYWAY_ENABLED=false` at startup instead of migration output.

## Update `pipeline_config` values (SQL-side runtime knobs)

Some behavior is tunable via the `processed.pipeline_config` table without touching env vars — e.g., outbox dedup window, audit payload trim threshold.

### Current values

```sql
SELECT key, value, description FROM processed.pipeline_config ORDER BY key;
```

### Change one

```sql
UPDATE processed.pipeline_config
   SET value = '120', updated_at = now()
 WHERE key = 'outbox_dedup_window_minutes';
```

No restart needed — SQL functions read the value on next call via `processed.pipeline_config_int(...)`.

### Verify

```sql
SELECT processed.pipeline_config_int('outbox_dedup_window_minutes');
-- → 120
```

## Update the interface catalog

The catalog is embedded in the JAR — YAML changes require a rebuild + redeploy. It's not a runtime config.

### Steps

1. Edit `filemanager-core/src/main/resources/interfaces/mich_inv_stocklevel_batch.yaml`
2. Add corresponding `ALTER TABLE staging.stocklevel_inbox ADD COLUMN ...` in a new `alter_*.sql` under `deploy/11-06-v6-customer/`
3. Rebuild JAR: `mvn -pl filemanager-core clean package`
4. Update pod image tag or hand the new JAR to DevOps
5. DBA applies the alter file
6. Rolling restart

See [Interface catalog](/reference/interface-catalog) for the current shape.

## Adding centralized-config overlay files

If Michelin's centralized-config repo mounts YAML files into `/opt/app/config` on the pod:

```yaml
env:
  CONFIG_DIR: /opt/app/config
```

Any `*.yaml` / `*.yml` in that dir gets merged in alphabetical order between the classpath defaults and the env vars. Handy for shared config across many services.

### Verify what got loaded

Startup log line:

```
config-overlay dir=/opt/app/config files=[010-common-service-configs.yaml, 020-inventoryledger.yaml]
```

Missing files log a WARN but don't fail startup.

## Env-var precedence recap

The value the JAR actually uses:

```
env var  >  overlay file (from CONFIG_DIR)  >  application-<profile>.yaml (classpath)  >  application.yaml (classpath)
```

Last-write-wins. Verify at startup:

```bash
kubectl logs -n <ns> deploy/psql-inventory-integration-service | grep 'startup complete config='
# prints the redacted resolved config
```

## Emergency: revert a bad config change

You just applied a change and the pod crashloops. Rollback:

```bash
kubectl rollout undo deploy/psql-inventory-integration-service -n <ns>
```

Rolls back to the previous replicaset — including the previous ConfigMap/Secret refs. Note: this doesn't undo ConfigMap/Secret content changes themselves. To fully undo:

```bash
kubectl edit configmap inventoryledger-config -n <ns>   # revert your change manually
kubectl rollout restart deploy/psql-inventory-integration-service -n <ns>
```

## Secret rotation cadence — suggested

| Secret | Rotate | Reason |
|---|---|---|
| `FILES_COM_API_KEY` | quarterly | Third-party credential, moderate blast radius |
| `DB_PASSWORD` | quarterly | On the customer's DBA cadence typically |
| `WEBHOOK_URL_PRIMARY` | when URL changes | Not a secret per se — an endpoint |
| SFTP key (`SFTP_PRIVATE_KEY_PATH`) | annually | If SFTP is enabled as fallback |

Immediate rotation whenever a secret is suspected leaked. Log everything in the change tracker.
