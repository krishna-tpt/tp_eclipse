---
title: Status
description: Where we are today (2026-07-20).
---

**Snapshot date:** 2026-07-20  
**Sandbox HEAD:** `c769341`  
**Branch:** `sandbox`  
**Baseline for customer:** `deploy/11-06-v6-customer/` + `alter_04_audit_event_log.sql`

## Delivery state

| Item | Status |
|---|---|
| **JAR** | `psql-inventory-integration-service-1.0.0.jar` — built from `c769341`, 13 MB, catalog embedded, all unit + integration tests pass |
| **Handoff bundle** | `inventoryledger-devops-2026-07-16/` — bundle + JAR + runbook + SFTP smoke kit — delivered to customer DevOps |
| **Customer DB (Azure)** | v6 baseline + alter_04. Confirmed against July 16 backup |
| **Live pipeline (VDI smoke)** | Blocked by corporate network — port 22 (SFTP) and TLS-MITM on files.com. Fixed by running from the K8s cluster (unblocked). |
| **Manual load path** | `manual-stocklevel-load.sql` produces 284 INSERTs + `load_stocklevel` call. Validated against Azure DB directly. |
| **Docs site** | Blume `docs-site/` scaffolded + this content set (~30 pages). Builds clean; dev server running at `http://127.0.0.1:4321/` |

## What's shipping right now

- **Java**: `c769341` catalog-aligned build. Handles both DMC_408 opening-balance and SMS_413 stock files via the same catalog.
- **DB**: v6 baseline + alter_04. Auto-promote triggers on both inboxes. `fetch_inventory_json_observed` for the SFDC read tier. Full audit event stream via `audit.v_all_events`.
- **Config**: env vars + optional `CONFIG_DIR` overlay. Flyway skipped in customer prod (`FLYWAY_ENABLED=false`).
- **Observability**: JAR writes `file.picked` / `file.parsed` / `file.failed` / `daemon.heartbeat` to `audit.event_log`. Five derived views + `v_all_events` union for Grafana.

## Known open items

- **Task #45** — second variant fixture (DMC_408 alongside BATCH_408). Not blocking; the two variants share the catalog.
- **Task #46** — ADR for the catalog contract. Content exists in [Catalog-driven decoding](/architecture/catalog-driven); needs the formal ADR file under `docs/adr/`.
- **Task #47** — scheduler + lifecycle "in-prod for a week" gate. Code shipped; observation period in progress.
- **Task #79** — TC22 physical-count path in the simulator. Not blocking.

## Recent verified end-to-end runs

| Date | Where | Data | Result |
|---|---|---|---|
| 2026-07-15 | Local WSL → Azure Flex Server | 284-row SMS_413 file via SFTP | Parsed, staged, promoted, archived, event logged. 1.2 s. |
| 2026-07-16 | Local WSL → Azure Flex Server | manual `test_v6_full_suite.sql` execution | All assertions pass |
| 2026-07-16 | Local WSL → Azure Flex Server | `fetch_inventory_json('MNA', NULL, NULL, NULL)` after load | 284 rows returned |
| 2026-07-16 | Local WSL → Azure Flex Server | `fetch_inventory_json_observed(...)` | 284 rows + `atp.queried` event in `audit.event_log` |
| 2026-07-16 | VDI (E062754) | Push `c769341` to GitLab | Success (after unblocking with `.zip` gitignore rule) |

## Known blockers

- **VDI SFTP** — corporate network blocks port 22 outbound. Not fixable by us — SecOps ticket needed if any smoke test from a locked-down developer machine is required. Prod is unaffected (K8s cluster has private endpoint).
- **VDI TLS to files.com** — corporate MITM proxy breaks the SDK's cert validation. Fix: `-Djavax.net.ssl.trustStoreType=Windows-ROOT`. Prod is unaffected.

Neither blocker affects the customer's production path — they only impact developer smoke testing from within Michelin's corporate network. Documented in [Troubleshooting](/operations/troubleshooting#files-com-issues).

## Health of the docs

| Doc | State |
|---|---|
| [Spec](/overview/spec) | Reflects v6 + alter_04. v0.5 revision (scheduler / health NFRs added). |
| [Architecture](/architecture/overview) | v0.5 — scheduler + health topology in. |
| [Scope](/overview/scope) | Current. Two-project boundary documented. |
| [Test plan](/quality/test-plan) | Current. 100+ TCs cataloged. |
| [Test results](/quality/test-results) | Last full run 2026-05-19. 116/119 pass. Newer verification runs in [Recent verified end-to-end runs](#recent-verified-end-to-end-runs) above. |
| ADRs 0001–0014 | Complete. Index at [ADR index](/delivery/adr-index). |
| This docs site | New — 2026-07-17 scaffold, 2026-07-20 expansion to full 6-section structure. |

## What ops needs from us next

Nothing right now. The customer has:

- The JAR (verified against their DB backup)
- The DEPLOY-DEVOPS.md runbook
- The manual SQL fallback (if they want to bypass the JAR)
- The env.example + SFTP smoke kit

Deploy is on their side. We're on standby for:

- Feedback from their first production tick
- Any config surprises (`CONFIG_DIR` overlay in particular)
- Alert wiring for `audit.v_all_events`

## Team

- **App** — Arul (TenthPlanet), owner
- **DBA** — customer's DBA team (they apply `customer_install.sql` + alters)
- **DevOps** — customer's DevOps team (they own container image + K8s manifests + secrets)
- **Auth tier** — customer's SFDC team (they own `SET app.tenant_id` before `fetch_inventory_json` calls)

## Contact + escalation

- Ops questions → [Deployment](/operations/deployment) + [Troubleshooting](/operations/troubleshooting)
- Data / schema questions → [Database overview](/database/overview) + [Evolution log](/database/evolution)
- Failures → [Health & monitoring](/operations/health-monitoring)
