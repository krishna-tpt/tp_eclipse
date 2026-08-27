---
title: Deployment
description: The DevOps runbook — build, configure, deploy, verify, rollback.
---

Operator-facing runbook for the customer's DevOps team. Mirrors `DEPLOY-DEVOPS.md` in the repo, adapted for on-page reading.

## What this service does (one paragraph)

Long-running JVM that polls files.com or SFTP for `MICH_INV_STOCKLEVEL_*.cfo` files, parses them against an interface catalog embedded in the JAR, and INSERTs the rows into `staging.stocklevel_inbox`. Then calls `staging.load_stocklevel(file_name)` which promotes the batch into `processed.opening_balance`. Failures land in `notification_outbox` and are drained via webhook. Every event lands in `audit.event_log`. Health probes at `GET /actuator/health`.

## Prerequisites

| | |
|---|---|
| JDK | Eclipse Temurin 21 (or any JDK 21) |
| Maven | 3.9+ (for building; skip if you're deploying a pre-built JAR) |
| Postgres | 16 — customer schema already installed via `customer_install.sql` + `alter_04_audit_event_log.sql` |
| Network | Outbound HTTPS to `filehub.michelin.net`, JDBC to Azure Postgres endpoint, HTTPS to webhook endpoint |
| Credentials | files.com API key, DB user + password, webhook URL |

## Build

From the repo root:

```bash
mvn -pl filemanager-core -am clean package
# → filemanager-core/target/psql-inventory-integration-service-1.0.0.jar (~13 MB)
```

Or use the pre-built JAR handed off in `inventoryledger-devops-2026-07-16/`.

## Configuration

**Full reference:** [Environment variables](/reference/environment-variables). Only vars marked REQUIRED need explicit values; everything else has a sensible default in `application.yaml`.

### Required env vars

```bash
APP_PROFILE=prod
RUN_MODE=scheduled

DB_URL=jdbc:postgresql://<azure-flex-host>:5432/<dbname>?sslmode=require
DB_USER=inventoryledger_app
DB_PASSWORD=<from-vault>

FILE_SOURCE=filescom
FILES_COM_API_KEY=<from-files.com-dashboard>
FILES_COM_BASE_URL=https://filehub.michelin.net
FILES_COM_PICKUP_PATH=/EU/PRD/BR/C10/Inbound
FILES_COM_ARCHIVE_PATH=/EU/PRD/BR/C10/Archive
FILES_COM_REJECT_PATH=/EU/PRD/BR/C10/Error

WEBHOOK_URL_PRIMARY=https://hooks.example.com/inventoryledger
```

### Frequently-set optional vars

```bash
SCHEDULE_DAILY=0 6 * * *              # producer's nominal delivery time
SCHEDULE_HOURLY=5 * * * *             # catch-up
HEALTH_PORT=8080
FLYWAY_ENABLED=false                  # customer DB owns schema
CONFIG_DIR=/opt/app/config            # centralized-config overlay dir
TZ=Europe/Amsterdam
```

Setting `FLYWAY_ENABLED=false` in the customer environment is important — the customer DBA applies `customer_install.sql` themselves; the JAR should not try to run its own Flyway migrations against a schema it doesn't own.

## Kubernetes deployment (customer-owned)

The customer's DevOps team owns the K8s manifests. Reference shape:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata: { name: psql-inventory-integration-service }
spec:
  replicas: 1                          # single replica — leader-election out of scope
  strategy: { type: Recreate }         # ensure only one at a time (no rolling)
  template:
    spec:
      terminationGracePeriodSeconds: 120
      containers:
      - name: app
        image: registry.example.com/psql-inventory-integration-service:1.0.0
        envFrom:
        - configMapRef: { name: inventoryledger-config }
        - secretRef:    { name: inventoryledger-secrets }
        ports:
        - { name: health, containerPort: 8080 }
        livenessProbe:
          httpGet: { path: /actuator/health, port: health }
          initialDelaySeconds: 30
          periodSeconds: 20
        readinessProbe:
          httpGet: { path: /actuator/health, port: health }
          initialDelaySeconds: 10
          periodSeconds: 10
```

`Secret` holds `DB_PASSWORD`, `FILES_COM_API_KEY`, `WEBHOOK_URL_PRIMARY`. `ConfigMap` holds everything else. Never bake secrets into the image.

## Verify

### Pod-level

```bash
kubectl logs -f deploy/psql-inventory-integration-service
# expect: "startup complete" → "scheduled-run-complete latency=XXX ms" on each tick
```

Look for:

- `scheduler-loop-started`
- `health-endpoint listening on 0.0.0.0:8080`
- `scheduled-run-complete latency=<n> ms files_processed=<n>` on cron ticks
- `daemon.heartbeat` in `audit.event_log`

### DB-side

```sql
-- Last 5 files processed
SELECT started_at, completed_at, file_name, status, rows_accepted, rows_rejected
  FROM staging.stocklevel_batch
 ORDER BY started_at DESC LIMIT 5;

-- Any alerts in the last hour
SELECT at, source, event_type, severity, error_msg, correlation_id
  FROM audit.v_all_events
 WHERE at > now() - interval '1 hour'
   AND severity IN ('warn', 'error')
 ORDER BY at DESC;

-- Outbox drain state
SELECT status, count(*) FROM processed.notification_outbox GROUP BY 1;
```

### Health check

```bash
kubectl port-forward deploy/psql-inventory-integration-service 8080:8080
curl -s http://localhost:8080/actuator/health | jq
# {"status":"UP","checks":{"db":{"status":"UP"},"scheduler":{"status":"UP"}}}
```

## Rollback

Since this is a stateless service (all state lives in Postgres), rollback = redeploy the prior image tag:

```bash
kubectl set image deploy/psql-inventory-integration-service app=registry.example.com/psql-inventory-integration-service:<prior-tag>
```

**Do not rollback the database.** Schema is customer-owned. If a bad alter reached prod, work with the DBA on a corrective alter, not a schema revert.

## Escalation path

1. Check `/actuator/health` → if `DOWN`, look at `checks.*.status` for the reason.
2. Check `audit.v_all_events WHERE severity IN ('warn','error')` for last hour.
3. Check `processed.notification_outbox WHERE status IN ('failed','failed_permanent')` for undelivered notifications.
4. Check `kubectl logs` for stack traces — JSON structured logging makes grep easy.
5. If DB is unreachable, health probe reports `db-ping-failed` and K8s will restart the pod. If the restart loops, the DB is genuinely down or the credentials are wrong.
6. If files.com is unreachable, the tick logs a WARN and the next tick retries. No pod restart.

See [Troubleshooting](/operations/troubleshooting) for specific failure modes.

## Change log for the most recent deploy

Commit `7764e50` — `fix(catalog): align MICH_INV_STOCKLEVEL to actual delimited CFO feed`:

- Filename pattern: `^MICH_INV_STOCKLEVEL_(SMS|DMC|BATCH)_\d{3}[._].+$`
- Header confirms: field 3 = `MNA`
- Record `MTL_STOCKLEVEL` field count: 52 (was 55; AME_QUANTITY removed per JIRA FOP_AGL-63630)
- Three trailing columns set to `literal: NP` (Not Present) because the delimited variant is silent on them: `receiving_location_type`, `stock_status`, `business_line`

The alignment lets the JAR ingest both the DMC_408 opening-balance file (June 3) and the SMS_413 stock file (July 15) via the same catalog.
