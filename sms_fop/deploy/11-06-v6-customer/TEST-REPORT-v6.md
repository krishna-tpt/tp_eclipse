# Inventory Ledger v6 — Test Report

**Run date:** 2026-06-15
**Build / baseline:** `deploy/11-06-v6-customer/customer_install.sql` (v6 baseline)
**Patches applied on baseline:**
- `alter_01_inbox_promote_triggers.sql` — restores `staging.f_promote_*` + 2 inbox triggers
- `alter_02_fetch_inventory_json.sql` — adds `processed.fetch_inventory_json`
- `alter_03_fetch_inventory_json_format.sql` — integer qtys, preserved key order
**Test driver:** `deploy/11-06-v6-customer/test_v6_full_suite.sql`
**Environment:** customer backup `inventoryledger_v6_11_06.sql` restored into `postgres:18-alpine` (PostgreSQL 18.4), tenant `IFOPEUR`.

---

## Summary

| Category | Tests | PASS | FAIL |
|---|---:|---:|---:|
| Basic calculation | 6 | 6 | 0 |
| Shipment cascade | 5 | 5 | 0 |
| Edge cases | 7 | 7 | 0 |
| State transitions (UPDATE / DELETE order line) | 3 | 3 | 0 |
| Negative cases (rejection paths) | 5 | 5 | 0 |
| Inbox terminal state | 2 | 2 | 0 |
| Query / function (`fetch_inventory_json`) | 4 | 4 | 0 |
| **TOTAL** | **32** | **32** | **0** |

---

## Test fixture (seed)

**Products created in `processed.product` (tenant IFOPEUR):**
`FS-BC-A`, `FS-BC-B`, `FS-BC-C`, `FS-EC-NO-OB`, `FS-EC-EXACT`, `FS-EC-OVERSELL`, `FS-EC-OVERSHIP`, `FS-EC-CANCELLED`, `FS-EC-EMPTY`, `FS-ST-A`.

**Warehouse:** `WH-FS`; **UOM:** `EA`.

**Opening balance (`processed.opening_balance`):**

| product | subinv | qty |
|---|---|---:|
| FS-BC-A | ONHAND | 100 |
| FS-BC-A | STAGING | 20 |
| FS-BC-B | ONHAND | 50 |
| FS-BC-C | ONHAND | 200 |
| FS-EC-EXACT | ONHAND | 50 |
| FS-EC-OVERSELL | ONHAND | 30 |
| FS-EC-OVERSHIP | ONHAND | 20 |
| FS-EC-CANCELLED | ONHAND | 40 |
| FS-EC-EMPTY | _(empty string)_ | 70 |
| FS-ST-A | ONHAND | 100 |
| FS-ST-A | STAGING | 50 |
| **FS-EC-NO-OB** | _(none — intentional)_ | – |

**Orders + shipments** seeded via `staging.order_inbox` / `staging.txn_inbox` (auto-promoted by v6 triggers). Full payload set is in `test_v6_full_suite.sql`.

---

## Detailed results

### A. Basic calculation

| Test | Scenario | Original (seed) | Expected | Got | Result |
|---|---|---|---|---|---|
| BC1 | Opening balance materialises on `stock_balance` | BC-A/ONHAND ob=100, then ORD-002 ships 2+3 | `on_hand=95` | `on_hand=95.0000` | **PASS** |
| BC2 | Reservation math on BC-A/ONHAND | ORD-001(10) + ORD-002 L1(5−2)+L2(3−3=closed) | `reserved=13` | `reserved=13.0000` | **PASS** |
| BC3 | `ATP = on_hand − reserved` | 95−13 | `atp=82` | `atp=82.0000` | **PASS** |
| BC4 | Different subinventory tracked independently | BC-A/STAGING ob=20, ORD-005 qty=8 ship 5 | `on=15 reserved=3 atp=12` | `on=15.0000 reserved=3.0000 atp=12.0000` | **PASS** |
| BC5 | Different product, fully shipped | BC-B/ONHAND ob=50, ORD-003 qty=15 full ship | `on=35 reserved=0 atp=35` | `on=35.0000 reserved=0.0000 atp=35.0000` | **PASS** |
| BC6 | Different product, open order | BC-C/ONHAND ob=200, ORD-004 qty=50 open | `on=200 reserved=50 atp=150` | `on=200.0000 reserved=50.0000 atp=150.0000` | **PASS** |

### B. Shipment cascade

| Test | Scenario | Original (seed) | Expected | Got | Result |
|---|---|---|---|---|---|
| SC1 | Partial shipment — line stays `open`, `shipped_qty` bumped | ORD-002 L1 qty=5, ship −2 | `shipped=2, state=open` | `shipped=2.0000, state=open` | **PASS** |
| SC2 | Full shipment closes the line | ORD-002 L2 qty=3, ship −3 | `shipped=3, state=closed` | `shipped=3.0000, state=closed` | **PASS** |
| SC3 | Single-line full shipment | ORD-003 qty=15, ship −15 | `shipped=15, state=closed` | `shipped=15.0000, state=closed` | **PASS** |
| SC4 | Line-precise cascade — unrelated lines untouched | ORD-001 & ORD-004 (no txn) | `sum(shipped_qty)=0` | `sum=0.0000` | **PASS** |
| SC5 | Idempotency — duplicate `external_txn_id` not double-applied | TXN-001 inserted twice | `inv_transaction count=1` | `count=1` | **PASS** |

### C. Edge cases

| Test | Scenario | Original (seed) | Expected | Got | Result |
|---|---|---|---|---|---|
| EC1a | Order for product NOT in opening balance — `stock_balance` row auto-created | FS-EC-NO-OB had no OB; order qty=20 | `1 row created` | `1 row` | **PASS** |
| EC1b | Auto-created row: on_hand=0, reserved=order qty, ATP negative | order qty=20 | `on=0 reserved=20 atp=−20` | `on=0.0000 reserved=20.0000 atp=−20.0000` | **PASS** |
| EC2 | ATP=0 boundary — order qty exactly equals on_hand | FS-EC-EXACT ob=50, order qty=50 | `on=50 reserved=50 atp=0` | `on=50.0000 reserved=50.0000 atp=0.0000` | **PASS** |
| EC3 | Oversell — order > on_hand, accepted, ATP negative | FS-EC-OVERSELL ob=30, order qty=50 | `on=30 reserved=50 atp=−20` | `on=30.0000 reserved=50.0000 atp=−20.0000` | **PASS** |
| EC4 | Overship — ship more than on_hand | FS-EC-OVERSHIP ob=20, order qty=20, ship −25 | `on=−5 reserved=0 state=closed` | `on=−5.0000 reserved=0.0000 state=closed` | **PASS** |
| EC5 | Line inserted as `cancelled` — no reservation taken | FS-EC-CANCELLED ob=40, order qty=10, state=cancelled | `on=40 reserved=0` | `on=40.0000 reserved=0.0000` | **PASS** |
| EC6 | Empty-string subinventory accepted as a real key | FS-EC-EMPTY ob=70, order qty=15, subinv='' | `on=70 reserved=15 subinv=''` | `on=70.0000 reserved=15.0000 subinv=''` | **PASS** |

> **EC1 highlights the answer to the original question:** if you receive an order for a product that has no opening balance, the system creates the missing `stock_balance` row with `on_hand=0` and `reserved=order_qty`. The `fetch_inventory_json` query then returns it with a **negative ATP**, which is exactly what downstream consumers need to see to flag "we owe stock we don't have."

### D. State transitions (`sfdc_order_line` UPDATE / DELETE)

| Test | Scenario | Original (seed) | Expected | Got | Result |
|---|---|---|---|---|---|
| ST1+ST2+ST3 | qty up → qty down → cancel chain releases the reservation | FS-LINE-ST1 qty 10→15→7, then `line_state='cancelled'` | `reserved=0 on ST-A/ONHAND` (after combined with ST4 & ST5) | `reserved=0.0000` | **PASS** |
| ST4 | UPDATE subinventory ONHAND→STAGING releases on OLD, reserves on NEW | FS-LINE-ST4 qty=12 moved | `STAGING reserved=12` | `STAGING reserved=12.0000` | **PASS** |
| ST5 | DELETE order line releases its reservation | FS-LINE-ST5 qty=20 deleted | `0 rows reference FS-LINE-ST5` | `0 rows` | **PASS** |

### E. Negative cases (rejection paths)

| Test | Scenario | Original (seed) | Expected | Got | Result |
|---|---|---|---|---|---|
| NC1 | Unknown tenant rejects the inbox row | `tenant_code='NOT_A_TENANT'` | `status=rejected, reason=unknown_tenant` | `status=rejected, reason=unknown_tenant` | **PASS** |
| NC2 | Unknown product code in payload rejects the inbox | `product_code='FS-DOES-NOT-EXIST'` | `status=rejected, reason~unknown_product:…` | `status=rejected, reason=line 1: unknown_product:FS-DOES-NOT-EXIST` | **PASS** |
| NC3 | Unknown warehouse code rejects the inbox | `warehouse_code='WH-DOES-NOT-EXIST'` | `status=rejected, reason~unknown_warehouse:…` | `status=rejected, reason=line 1: unknown_warehouse:WH-DOES-NOT-EXIST` | **PASS** |
| NC4 | Unknown UOM rejects the inbox | `uom_code='XYZ'` | `status=rejected, reason~unknown_uom:…` | `status=rejected, reason=line 1: unknown_uom:XYZ` | **PASS** |
| NC5 | Rejection paths do NOT leave half-formed `sfdc_order` rows | 4 rejected inboxes | `0 rows in processed.sfdc_order` | `count=0` | **PASS** |

### F. Inbox terminal state (smoke)

| Test | Scenario | Original (seed) | Expected | Got | Result |
|---|---|---|---|---|---|
| IB1 | All non-NC `order_inbox` rows reach `status=processed` | 14 order inboxes (5 BC + 6 EC + 3 ST) | `14/14 processed` | `14/14 processed` | **PASS** |
| IB2 | All `txn_inbox` rows reach `status=processed` (incl. duplicate retry) | 6 txn inboxes (5 unique + 1 duplicate of TXN-001) | `6/6 processed` | `6/6 processed` | **PASS** |

### G. Query / function — `processed.fetch_inventory_json`

| Test | Scenario | Original (seed) | Expected | Got | Result |
|---|---|---|---|---|---|
| QC1 | All-NULL filters return every `stock_balance` row for the tenant | 12 stock_balance rows (incl. EC1 auto-created) | `>=11 rows` | `12 rows` | **PASS** |
| QC2 | Product filter narrows to the right rows | `product_code='FS-BC-A'` → ONHAND + STAGING | `2 rows` | `2 rows` | **PASS** |
| QC3 | Non-existent product returns nothing | `product_code='NOPE'` | `0 rows` | `0 rows` | **PASS** |
| QC4 | Quantities returned as INTEGER (no `.0000`); key order preserved | BC-A/ONHAND row | `on_hand_qty=95, reserved_qty=13, atp_qty=82` | `on_hand=95 reserved=13 atp=82` | **PASS** |

---

## How to reproduce

```bash
# 1. Start a fresh Postgres
docker rm -f ilv6-test 2>/dev/null
docker run -d --name ilv6-test \
  -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=inventoryledger \
  -p 5455:5432 postgres:18-alpine

# 2. Restore the customer backup
docker cp inventoryledger_v6_11_06.sql ilv6-test:/customer_backup.sql
docker exec ilv6-test pg_restore -U postgres -d inventoryledger \
  --no-owner --no-privileges --clean --if-exists /customer_backup.sql

# 3. Apply the v6 alters
for f in alter_01_inbox_promote_triggers.sql \
         alter_02_fetch_inventory_json.sql \
         alter_03_fetch_inventory_json_format.sql; do
  docker cp deploy/11-06-v6-customer/$f ilv6-test:/$f
  docker exec ilv6-test psql -U postgres -d inventoryledger -f /$f
done

# 4. Run the test suite
docker cp deploy/11-06-v6-customer/test_v6_full_suite.sql ilv6-test:/test_v6_full_suite.sql
docker exec ilv6-test psql -U postgres -d inventoryledger -X -A -F'|' \
  -f /test_v6_full_suite.sql
```

The final query of the script returns a pipe-delimited table with one row per test:
`test_id | category | scenario | original | expected | got | pass_fail`.

---

## Files

| File | Purpose |
|---|---|
| `deploy/11-06-v6-customer/test_v6_full_suite.sql` | Full driver (this report's source) |
| `deploy/11-06-v6-customer/test_v6_comprehensive.sql` | Original v6 smoke (TC1–TC10) — superseded by this suite but kept for history |
| `deploy/11-06-v6-customer/test_smoke_native_payloads.sql` | Native SFDC-shape payload smoke — orthogonal coverage |
| `deploy/11-06-v6-customer/TEST-REPORT-v6.md` | This report |
