# Deploy — Versioned Customer Snapshots

Each subfolder is a **self-contained, frozen** deployment package. Pick one
folder, run its scripts in order, get the schema state of that snapshot.

```
deploy/
├── 05-06-v1-customer/             ← initial tag (single trigger)
├── 05-06-v2-customer/             ← 3-trigger model with shipment→order cascade
├── 08-06-v3-customer/             ← subinventory + stock_status, POOL reservations
├── 11-06-v4-customer/             ← per-subinv reservations + append-only inboxes
└── 11-06-v5-customer/             ← ACTIVE tag — auto-promote triggers on inbox INSERTs
    ├── customer_install.sql       (per-row AFTER INSERT triggers on txn_inbox + order_inbox;
    │                               UPSERT on sfdc_order preserves created_at;
    │                               drain fns retained as fallback)
    ├── customer_seed.sql
    ├── upgrade_from_v4.sql        (purely additive — no schema or data migration)
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

Examples:
- `05-06-v1-customer` — first customer-targeted snapshot on June 5
- `05-06-v2-customer` — same day, second revision
- `11-06-v5-customer` — fifth customer release on June 11

## Workflow

### To use a snapshot

```bash
cd deploy/11-06-v5-customer/
psql -h <host> -U postgres -d <dbname> -f customer_install.sql
psql -h <host> -U postgres -d <dbname> -f customer_seed.sql
```

Or follow the per-snapshot `CUSTOMER-DEPLOYMENT.md` runbook.

### To upgrade from the previous tag

Each tag ships an `upgrade_from_v<prev>.sql` script when the upgrade is non-trivial. v5 has `upgrade_from_v4.sql`; v4 has `upgrade_from_v3.sql`.

### To cut a new tag

1. Copy the most recent snapshot folder to a new one:
   `cp -r deploy/11-06-v5-customer deploy/12-06-v1-customer`
2. Edit the files in the new folder for whatever changed
3. Update `CUSTOMER-DEPLOYMENT.md` inside the new folder with what's different from the prior tag (see its "Tag provenance" section at the bottom)
4. Test against a fresh `postgres:16-alpine` container before sharing
5. Update the "active tag" pointer at the top of this README
6. Add a section to `DB-EVOLUTION.md` summarizing the new tag's decisions

### To roll back

A tag is just a folder, fully self-contained. Apply an older folder's
`customer_install.sql` against a fresh DB and you have that snapshot's
schema exactly. No migration history to walk back.

## Why folders, not branches

- Folders are visible without git tooling (customers can `ls deploy/`)
- A tag can ship as zip/email without anyone resolving refs
- Side-by-side comparison is `diff -r 11-06-v4-customer 11-06-v5-customer`
- The "tag and reuse" workflow: pick the folder you want, deploy it

Git tags / branches can layer on top of this for SCM-level provenance, but
they're optional. The folder is the deliverable.

## What lives in deploy/ vs db/setup/

| | `deploy/<tag>/` | `db/setup/` |
|---|---|---|
| Purpose | **Frozen** snapshot for customer deployment | Historical bootstraps used during dev (mock data, full Flyway-consolidated install) |
| Schema layout | `processed` (live tables) | `public` (live tables) — legacy |
| Files | self-contained per folder | `inventoryledger_full.sql`, `inventoryledger_schema.sql`, `mock_data.sql`, `mock_staging_data.sql` |
| Modify after release? | **No** — make a new tag instead | Yes — these are dev tooling |
| Customer-facing? | Yes | No |

If you're cutting a customer release, work under `deploy/`. If you're seeding a
dev box, `db/setup/mock_data.sql` is still the right tool.
