# Observability validation — how to prove `alter_04` works end-to-end

After applying `alter_04_audit_event_log.sql`, the existing test tooling
already exercises every code path that writes audit rows or feeds the
derived views. No new test harness is required — just point the existing
tools at the upgraded DB and watch the events accumulate.

## Prerequisites

- Customer DB backup restored (or any fresh DB with `customer_install.sql`
  applied)
- `alter_01_inbox_promote_triggers.sql` through
  `alter_04_audit_event_log.sql` applied **in order**
- The built JAR at
  `filemanager-core/target/psql-inventory-integration-service-1.0.0.jar`

## Five-step validation

### 1. Apply the alters

```bash
psql -h <host> -U postgres -d inventoryledger \
     -f deploy/11-06-v6-customer/alter_01_inbox_promote_triggers.sql \
     -f deploy/11-06-v6-customer/alter_02_fetch_inventory_json.sql \
     -f deploy/11-06-v6-customer/alter_03_fetch_inventory_json_format.sql \
     -f deploy/11-06-v6-customer/alter_04_audit_event_log.sql
```

Sanity check that the new objects exist:

```sql
\dt audit.*
\dv audit.*
\df processed.fetch_inventory_json_observed
```

### 2. Drive the order / transaction / stock paths via the simulator

```bash
cd deploy/11-06-v6-customer/test-data-generator
./run-sim.sh --scenarios sample-scenarios.csv \
             --db-url "jdbc:postgresql://<host>:5432/inventoryledger" \
             --db-user postgres --db-pass <pw> \
             --batch-tag SMOKE --clean --report sim-report.csv
```

Then check the view-derived event types are populating from existing tables:

```sql
SELECT event_type, count(*)
  FROM audit.v_all_events
 WHERE at > now() - interval '5 minutes'
 GROUP BY event_type
 ORDER BY 1;
```

Expected non-zero counts for: `order.received`, `txn.received`,
`promotion.success`, `promotion.failed` (any rejected scenarios),
`stock.changed`.

### 3. Drive the file path via the Java app

Place a `.dat` file named to match `^MICH_INV_STOCKLEVEL_413\d{14}\.dat$`
in the configured pickup folder (local / files.com / SFTP), then run:

```bash
APP_PROFILE=test RUN_MODE=oneshot \
DB_URL=jdbc:postgresql://<host>:5432/inventoryledger \
DB_USER=postgres DB_PASSWORD=<pw> \
WEBHOOK_URL_PRIMARY=http://localhost:8080/webhook \
FILE_SOURCE=local LOCAL_PICKUP_PATH=./inbound \
java -jar filemanager-core/target/psql-inventory-integration-service-1.0.0.jar
```

Confirm the written event types landed in `audit.event_log`:

```sql
SELECT event_type, severity, source, ref_id, correlation_id, latency_ms,
       rows_affected, bytes
  FROM audit.event_log
 WHERE at > now() - interval '5 minutes'
   AND event_type IN ('file.picked','file.parsed','file.failed')
 ORDER BY at;
```

Expected: one `file.picked` per file (with bytes), one `file.parsed` on
success (with `rows_affected` + `latency_ms`), one `file.failed` per
quarantined file. Every row from the same file carries the same
`correlation_id`.

### 4. Drive the ATP read path

```sql
SELECT * FROM processed.fetch_inventory_json_observed(
    'IFOPEUR', 'WH-LYO', NULL, NULL, 'SFDC-validation');
```

Then:

```sql
SELECT event_type, source, tenant_id, ref_id, latency_ms, rows_affected,
       payload
  FROM audit.event_log
 WHERE event_type = 'atp.queried'
 ORDER BY at DESC LIMIT 5;
```

Expected: one row per call with the caller (`SFDC-validation`), measured
latency, returned-row count, and the filter parameters in `payload`.

### 5. Confirm the heartbeat (scheduled mode only)

Start the JAR in scheduled mode and let it run for ~3 minutes:

```bash
RUN_MODE=scheduled <other env vars> \
java -jar filemanager-core/target/psql-inventory-integration-service-1.0.0.jar
```

In another shell:

```sql
SELECT count(*) AS beats, max(at) AS latest
  FROM audit.event_log
 WHERE event_type = 'daemon.heartbeat'
   AND at > now() - interval '3 minutes';
```

Expected: ≥ 2 beats, `latest` within the last 90 s.

## One query to confirm everything is wired

```sql
SELECT event_type, count(*) AS events,
       max(at) AS most_recent,
       min(at) AS first_seen
  FROM audit.v_all_events
 WHERE at > now() - interval '15 minutes'
 GROUP BY event_type
 ORDER BY most_recent DESC;
```

A healthy post-validation run should show non-zero counts for at least:

| event_type           | written by                              |
|----------------------|-----------------------------------------|
| `file.picked`        | CatalogFileLoader (step 3)              |
| `file.parsed`        | CatalogFileLoader (step 3)              |
| `order.received`     | view over `staging.order_inbox` (step 2)|
| `txn.received`       | view over `staging.txn_inbox`   (step 2)|
| `promotion.success`  | views over both inboxes         (step 2)|
| `stock.changed`      | view UNION over `inv_transaction` + `opening_balance` + `sfdc_order_line` (steps 2+3) |
| `atp.queried`        | `fetch_inventory_json_observed` (step 4)|
| `daemon.heartbeat`   | HeartbeatEmitter                (step 5)|
| `notify.emitted`     | view over `notification_outbox` (steps 2+3 produce these as side-effects) |

If any expected event_type is missing, that's the failure signal — start
by checking the call site listed in the right column.

## Why no separate end-to-end test class

The simulator covers every order / transaction / stock scenario already
(22 cases, see `test-data-generator/sample-scenarios.csv`). The Java
`AppIT` covers the file pipeline. `EventLogWriterIT` covers the writer.
The new audit code is wired into call sites the existing tools already
exercise, so a separate `EndToEndFlowIT` would duplicate effort.

The single source of truth for "did everything flow" is the query above —
run it after a simulator + app + ATP cycle, and missing rows tell you
exactly which code path is broken.
