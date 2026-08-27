# Deploy — Versioned Customer Snapshots

Each subfolder is a **self-contained, frozen** deployment package. Pick one
folder, run its scripts in order, get the schema state of that snapshot.

> **v6 is the LOCKED BASELINE (2026-06-11).** No more new tag folders. All
> future DB changes ship as `alter_<feature>.sql` inside
> `deploy/11-06-v6-customer/`. Every alter must be idempotent
> (`IF NOT EXISTS`, `CREATE OR REPLACE`, `DROP TRIGGER IF EXISTS`).
> `customer_install.sql` is the immutable baseline — do NOT modify it.

```
deploy/
├── 05-06-v1-customer/             ← initial tag (single trigger)
├── 05-06-v2-customer/             ← 3-trigger model with shipment→order cascade
├── 08-06-v3-customer/             ← subinventory + stock_status, POOL reservations
├── 11-06-v4-customer/             ← per-subinv reservations + append-only inboxes
├── 11-06-v5-customer/             ← auto-promote triggers on inbox INSERTs
└── 11-06-v6-customer/             ← ACTIVE tag — line-precise cascade + partial shipments
    ├── customer_install.sql       (erp_external_id + erp_line_id + shipped_qty;
    │                               line-precise cascade; incremental reservation release;
    │                               drain fns delegate to promote_one_* procedures)
    ├── customer_seed.sql
    ├── upgrade_from_v5.sql        (purely additive — 5 columns + 4 indexes + 4 functions replaced)
    └── CUSTOMER-DEPLOYMENT.md     (read this for the verified flow)
```

Cross-tag rationale lives in [`DB-EVOLUTION.md`](DB-EVOLUTION.md) — what changed at each tag and why.

## Naming convention

```
DD-MM-vN-<audience>
│  │   │  │
│  │   │  └─ who this tag is for: customer, dev, staging, …
│  │   └──── revision number within that day (v1, v2, …)
│  └─────── month (numeric)
└────────── day (numeric)
```

## Workflow

```bash
# Use a snapshot
cd deploy/11-06-v6-customer/
psql -h <host> -U postgres -d <dbname> -f customer_install.sql
psql -h <host> -U postgres -d <dbname> -f customer_seed.sql

# Upgrade from previous tag (each tag ships an upgrade_from_v<prev>.sql)
psql -h <host> -U postgres -d <dbname> -f upgrade_from_v5.sql
```

A tag is just a folder — fully self-contained. Apply an older folder's `customer_install.sql` against a fresh DB and you have that snapshot's schema exactly. No migration history to walk back.
