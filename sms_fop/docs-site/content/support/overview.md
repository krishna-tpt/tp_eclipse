---
title: Support overview
description: Support & maintenance handbook — what's here and when to reach for it.
---

For the support team, on-call rotation, and the customer's operators. Everything you need to keep the service running, diagnose problems, recover from failures, and audit what happened.

## Read this section when

| Situation | Start here |
|---|---|
| Something is broken right now | [Failure scenarios](/support/failure-scenarios) — grouped by symptom |
| A support ticket asks "how do I …?" | [FAQ](/support/faq) — quick answers |
| You need to do X by hand | [Manual operations](/support/manual-operations) — every operator-runnable action |
| Rotating a credential or updating a URL | [Configuration updates](/support/config-updates) |
| Tracking down why something happened | [Log inspection](/support/log-inspection) + [Audit review](/support/audit-review) |
| Verifying compliance / who did what | [Audit review](/support/audit-review) |

## Skills you should have

- **kubectl** basics — `kubectl logs`, `kubectl exec`, `kubectl port-forward`, `kubectl get pods`
- **psql** basics — connecting, running queries, reading `SELECT` output
- **SQL** — reading, understanding joins, running the queries in this section (copy-paste-friendly)
- **Reading structured JSON logs** — the daemon logs JSON when `LOG_FORMAT=json` (prod default)

Nothing here requires Java or code changes. It's all operational.

## Escalation paths

| What you're seeing | Who to page |
|---|---|
| Health probe stays `DOWN` after > 5 min | On-call engineer (Java) |
| Rejection rate on FOP / SFDC promotions spikes | FOP / SFDC integration team |
| Stock numbers look wrong but everything is green | On-call engineer (DB) |
| files.com is unreachable | Michelin infra team |
| DB is unreachable | Michelin DBA team |
| Anything security-related (leaked key, unauthorized access) | SecOps immediately |

## Contact points

- **App team (this service)** — Arul, TenthPlanet
- **DBA** — customer's DBA team; owns applying `customer_install.sql` + `alter_*.sql`
- **DevOps** — customer's DevOps; owns container image + K8s manifests + secret sourcing
- **SFDC read tier** — customer's SFDC team; owns setting `app.tenant_id` before calling `fetch_inventory_json`

## Companion reading

- [Deployment](/operations/deployment) — how the JAR was originally deployed
- [Configuration](/operations/configuration) — every knob the JAR reads
- [Troubleshooting](/operations/troubleshooting) — deeper problem-driven remedies
- [Observability](/architecture/observability) — the event stream that makes triage possible
