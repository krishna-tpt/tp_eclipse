---
title: ADR-0008 - Ops team owns containerization and scheduling
description: Architecture Decision Record 0008 - Ops team owns containerization and scheduling
---


**Status:** Accepted · **Date:** 2026-05-18

## Context

TenthPlanet has a separate ops team that operates Kubernetes, container registries, secret management (Azure Key Vault), and the scheduling infrastructure. The application team's previous projects (e.g. `smslite-filemanager`) follow the same separation: app team delivers a runnable JAR; ops team wraps it in a container and a scheduler.

## Decision

Our team's deliverables stop at:
1. `db/migration/V*.sql` — Flyway migrations
2. `filemanager-core/target/filemanager-core.jar` — runnable shaded JAR
3. `filemanager-data-tools` — dev-only synthetic data tools (not shipped)
4. `SPEC.md`, `ARCHITECTURE.md`, `TEST-PLAN.md`, `docs/adr/`
5. Documented env vars and exit codes (in `README.md`)

We do **not** produce:
- Dockerfile / container image
- K8s manifests (`CronJob`, `Deployment`, `ConfigMap`, `Secret`, `ServiceMonitor`)
- Helm chart, Kustomize overlays
- Scheduler configuration (cron schedule, retry/backoff policy)
- Secret-sourcing pipeline (Key Vault → K8s Secret → env)

The ops team builds the image, wires secrets, picks the scheduler (K8s CronJob, Azure Container Apps Job, on-prem cron — any equivalent), and operates the runtime.

## Consequences

**Positive**
- Clean ownership boundary — we ship code, they ship runtime.
- No duplicate work — ops's existing containerization stack handles us the same as everything else.
- We can change Java internals without coordinating a container release.
- Smaller repository — no infrastructure code to maintain.

**Negative**
- A required env-var change requires coordination with ops (they update their secret store; we update `README.md`).
- We cannot directly fix runtime issues like memory limits, restart policy, or network config — must route through ops.
- A clear handover document (env vars, exit codes, expected resources) is essential. Lives in `README.md` and `ARCHITECTURE.md` §6.

## Alternatives considered

- **App team owns containerization** — rejected to match TenthPlanet's existing ownership model.
- **Hybrid (we ship a Dockerfile, they wrap it)** — rejected because partial ownership creates ambiguity over who fixes runtime issues.
