# Inventory Test Data Generator

Two Java entry points, one jar:

| Mode | Main class | Launcher | Purpose |
|---|---|---|---|
| **Loader** (original) | `InventoryTestDataLoader` | `run.sh` / `run.bat` | Reads a positions CSV (`tenant, product, warehouse, subinv, quantity`) and generates OB + orders + shipments using `--orders-ratio` / `--ships-ratio`. Good for bulk load. |
| **Simulator** (new) | `InventorySimulator` | `run-sim.sh` / `run-sim.bat` | Reads a *scenarios* CSV — one row per test case — with explicit `opening_qty`, `order_qty`, `ship_qty`, and **expected** on_hand / reserved / atp / line_state. Verifies each row and prints PASS/FAIL. |

Both modes:

1. write one `processed.opening_balance` row per CSV row (loader uses the
   row qty, simulator uses `opening_qty`)
2. write one `staging.order_inbox` row (auto-promoted by v6 trigger)
3. write one `staging.txn_inbox` row (auto-promoted by v6 trigger)

The v6 inbox triggers cascade everything synchronously, so the moment the tool
finishes you have a full chain in `processed.*` with correct reservations and
partial-shipment math.

## When to use which

- **Bulk synthetic data for performance / volume work** → loader.
- **TDD-style scenario coverage with expected values** → simulator.

The simulator names the product `PROD_<tc_id>` (`PROD_TC01`, `PROD_TC02`, …)
so every test case is independently observable in `stock_balance`,
`sfdc_order_line`, and `inv_transaction`.

## Prerequisites (all OS)

- JDK 21+
- Maven 3.6+
- Network access from the running machine to the customer Postgres

## Build (once)

```bash
cd deploy/11-06-v6-customer/test-data-generator
mvn -q clean package
```

This produces:
- `target/inventory-test-data-generator.jar`
- `target/lib/postgresql-*.jar` (dependency)

## Configure (once per machine)

Copy the sample config and edit it with your DB credentials:

```bash
cp sample-config.properties local.properties
# then edit local.properties — set db.url / db.user / db.pass / csv.path
```

`local.properties` keys (all optional; CLI flags override):

```properties
csv.path = sample-params.csv
db.url   = jdbc:postgresql://your-db-host:5432/inventoryledger
db.user  = inventoryledger
db.pass  = your_password_here

orders.ratio = 0.5
ships.ratio  = 0.5
batch.tag    = MORNING_RUN
tenant       = IFOPEUR
clean        = false
```

> **Don't commit `local.properties`** — it has the DB password.
> Only `sample-config.properties` should ever be checked in.

## Run — Loader mode (bulk)

### Linux / macOS

```bash
./run.sh                          # uses local.properties
./run.sh --orders-ratio 0.8       # override one value
./run.sh --batch-tag MORNING --clean
```

### Windows (CMD or PowerShell)

```cmd
run.bat
run.bat --orders-ratio 0.8
run.bat --batch-tag MORNING --clean
```

### Or call java directly (any OS)

```bash
# Linux/macOS classpath uses ':'
java -cp "target/inventory-test-data-generator.jar:target/lib/*" \
  com.michelin.inventorytest.InventoryTestDataLoader \
  --config local.properties

# Windows classpath uses ';'
java -cp "target\inventory-test-data-generator.jar;target\lib\*" ^
  com.michelin.inventorytest.InventoryTestDataLoader ^
  --config local.properties
```

## Run — Simulator mode (scenario-driven)

Scenarios CSV at `sample-scenarios.csv`. Each row defines one test case with
explicit quantities and expected values. The product is auto-named
`PROD_<tc_id>` (`PROD_TC01`, `PROD_TC02`, …) so every TC is independently
observable end-to-end.

### Linux / macOS

```bash
./run-sim.sh                                   # uses local.properties
./run-sim.sh --batch-tag SMOKE_RUN --clean     # wipe prior + reload
./run-sim.sh --scenarios my-scenarios.csv      # alternate scenarios CSV
./run-sim.sh --report sim-report.csv           # write per-TC CSV result
```

### Windows

```cmd
run-sim.bat
run-sim.bat --batch-tag SMOKE_RUN --clean
```

### Or call java directly

```bash
java -cp "target/inventory-test-data-generator.jar:target/lib/*" \
  com.michelin.inventorytest.InventorySimulator \
  --config local.properties --clean --report sim-report.csv
```

Console output (one row per scenario, plus a final totals line):

```
tc_id   scenario                          product           on_hand  reserved       atp  line_st    result
TC01    Opening balance and order         PROD_TC01        100.0000   15.0000   85.0000  open       PASS
TC02    Partial shipment                  PROD_TC02         95.0000   10.0000   85.0000  open       PASS
TC03    Full shipment closes line         PROD_TC03         85.0000    0.0000   85.0000  closed     PASS
TC04    Oversell drives ATP negative      PROD_TC04         30.0000   50.0000  -20.0000  open       PASS
TC05    ATP zero boundary                 PROD_TC05         50.0000   50.0000    0.0000  open       PASS
TC06    Cancelled line at insert          PROD_TC06         40.0000    0.0000   40.0000  cancelled  PASS
TC07    Order without opening balance     PROD_TC07          0.0000   20.0000  -20.0000  open       PASS
TC08    Overship beyond on_hand           PROD_TC08         -5.0000    0.0000   -5.0000  closed     PASS
TC09    Empty subinventory string         PROD_TC09         70.0000   15.0000   55.0000  open       PASS
TC10    Alternate subinventory STAGING    PROD_TC10         75.0000   15.0000   60.0000  open       PASS

Total: 10 | Pass: 10 | Fail: 0
```

The CSV report at `--report` adds the expected vs actual columns side-by-side
so it can be opened in Excel for sharing.

### Scenarios CSV format

```
tc_id,name,tenant_code,warehouse_code,subinventory,
opening_qty,order_qty,ship_qty,line_state,
expected_on_hand,expected_reserved,expected_atp,expected_line_st,notes
```

| Column | Meaning |
|---|---|
| `tc_id` | identifier; becomes product suffix `PROD_<tc_id>` |
| `name` | human-readable scenario label |
| `opening_qty` | `processed.opening_balance.qty` (0 → skip OB row) |
| `order_qty` | order line qty (0 → skip order) |
| `ship_qty` | shipment qty (absolute; 0 → skip txn) |
| `line_state` | line state at INSERT — `open` or `cancelled` |
| `expected_*` | the values the engine compares against |

A scenario with `opening_qty=0` exercises the "order arrived for a product
with no opening balance" path — the reservation trigger creates the
`stock_balance` row with on_hand=0, reserved=order_qty (negative ATP).

## CSV format

Header is required (column order is free). Required columns:

```
tenant_code,product_code,warehouse_code,subinventory,quantity
IFOPEUR,SIM-TYRE-A,WH-SIM,ONHAND,100
IFOPEUR,SIM-TYRE-A,WH-SIM,STAGING,40
...
```

A sample is provided at `sample-params.csv`. Lines starting with `#` and blank
lines are skipped.

Rules:

- Tenant **must already exist** in `processed.tenant`
- Products / warehouses / uom (`EA`) are **auto-created** if missing

## All options

| Flag | Property | Default | Purpose |
|---|---|---|---|
| `--config <file>` | — | — | path to a `.properties` file |
| `--csv <path>` | `csv.path` | — required — | parameters CSV |
| `--db-url <jdbc>` | `db.url` | — required — | JDBC URL |
| `--db-user <user>` | `db.user` | — required — | DB user |
| `--db-pass <pass>` | `db.pass` | — required — | DB password |
| `--orders-ratio <0.0-1.0>` | `orders.ratio` | `0.5` | fraction of qty reserved as a new order |
| `--ships-ratio <0.0-1.0>` | `ships.ratio` | `0.5` | fraction of the order qty that gets shipped |
| `--batch-tag <string>` | `batch.tag` | `B<epoch-ms>` | identifies all SIM rows from this run |
| `--tenant <code>` | `tenant` | — | overrides `tenant_code` column for every row |
| `--clean` | `clean` | `false` | wipe prior batch + opening/stock for these products before loading |

CLI flags ALWAYS override values from the properties file.

## What each CSV row produces (quantity Q)

| Object | Quantity | Identifier |
|---|---|---|
| `processed.opening_balance` | Q | `source_file = <batch_tag>` |
| `staging.order_inbox` (1 line) | Q × `orders-ratio` | `sfdc_order_id = SIM-<tag>-ORD-NNNN` |
| `staging.txn_inbox` (shipment) | order_qty × `ships-ratio` | `external_txn_id = SIM-<tag>-TXN-NNNN` |

The transaction's `erp_line_id` matches the order line's `erp_external_id`,
so the v6 cascade fires correctly. Expected math:

- `on_hand_qty = Q − ship_qty`
- `reserved_qty = order_qty − ship_qty` (the still-to-ship portion)
- `atp_qty = on_hand_qty − reserved_qty = Q − order_qty`

## Verified end-to-end against the customer DB backup

Test rig: `postgres:18-alpine` + restored backup `inventoryledger_v6_11_06.sql`
+ `alter_01_inbox_promote_triggers.sql` applied.

| Input qty Q | --orders-ratio | --ships-ratio | on_hand | reserved | atp |
|---|---|---|---|---|---|
| 100 | 0.5 | 0.5 | 75 | 25 | 50 |
| 100 | 0.8 | 0.5 | 60 | 40 | 20 |
| 40  | 0.5 | 0.5 | 30 | 10 | 20 |
| 50  | 0.5 | 0.5 | 38 | 13 | 25 |
| 200 | 0.5 | 0.5 | 150 | 50 | 100 |
| 30  | 0.5 | 0.5 | 23 | 8 | 15 |

(Note: `ship_qty` rounds DOWN to a whole number, hence 38/13 instead of 37.5/12.5 for Q=50.)

## Re-run semantics

| Scenario | Behavior |
|---|---|
| Same `--batch-tag`, **no** `--clean` | Opening balance skipped if today's row exists; orders + txns still INSERT under the same tag prefix — but those will collide on `external_txn_id` UNIQUE. Use a different tag instead. |
| Same `--batch-tag` **with** `--clean` | Wipes orders/txns for this tag AND opening/stock_balance for the (product, warehouse, subinv) keys in the CSV. Re-loads from scratch. |
| Different `--batch-tag`, no `--clean` | Opening balance skipped for keys with today's row; orders + txns INSERT fresh under the new tag → existing stock_balance picks up additional reservation and ship. |

For pure idempotent "wipe and reload", always use `--clean` with the same tag.

## Cleanup (without re-running)

```sql
DELETE FROM staging.txn_inbox         WHERE external_txn_id LIKE 'SIM-<tag>-%';
DELETE FROM staging.order_inbox       WHERE sfdc_order_id   LIKE 'SIM-<tag>-%';
DELETE FROM processed.inv_transaction WHERE external_txn_id LIKE 'SIM-<tag>-%';
DELETE FROM processed.sfdc_order      WHERE sfdc_order_id   LIKE 'SIM-<tag>-%';
DELETE FROM processed.opening_balance WHERE source_file = '<tag>';
```
