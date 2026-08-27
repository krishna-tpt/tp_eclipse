---
title: ADR-0007 - No inbound business REST
description: Architecture Decision Record 0007 - No inbound business REST
---


**Status:** Accepted · **Date:** 2026-05-18

## Context

The original solution deck shows REST POST endpoints for Pipeline 2 (FOP transactions) and Pipeline 3 (Salesforce orders) and a "Public read API" (`fetch_inventory`) consumed by Salesforce Order/Quote screens. The Java service was initially scoped to expose those endpoints.

In review, the user clarified: other teams (FOP service, Salesforce integration) write directly to the database with their own credentials. The Salesforce-facing read tier is owned by a separate team (PostgREST, Heroku Connect, or another microservice in front of `fetch_inventory`).

## Decision

The inventory ledger Java service exposes **no inbound business REST endpoints**. Its responsibilities are:
1. Load the daily opening-balance file.
2. Drain the notification outbox.

That's all. There is no HTTP server in the JAR — not even for health or metrics, since the JAR is a short-lived batch process (see [ADR-0003](0003-batch-jar-no-daemon.md)).

Other teams interact with the database via:
- **Writes** — direct JDBC: call `post_transaction(jsonb)` / `upsert_order(jsonb)` as the `inventoryledger_writer` role, OR INSERT to staging inbox tables.
- **Reads** — direct JDBC or an external REST tier they own: call `fetch_inventory` / `fetch_pending_orders` as the `inventoryledger_reader` role.

We deliver the SQL contract (functions, RLS, audit). They wire up their own ingress.

## Consequences

**Positive**
- The service's surface area is tiny — file handling + outbox drain only.
- No authentication/authorization machinery in our code. Other teams handle authn/authz at their edge.
- No API versioning concern. Function signatures are the contract; SQL evolves via migrations.
- Reduced security review scope — no inbound traffic to harden.

**Negative**
- The "Public read API" promised by the deck doesn't materialize from our service. Another team owns it.
- If that other team's service is slow or unavailable, Salesforce screens degrade even though our DB is healthy.
- Ownership ambiguity around the REST tier requires a clear handoff document (covered by the deck slide 11).

## Alternatives considered

- **Expose REST in our JAR** — rejected after scope clarification; would duplicate work being done elsewhere.
- **PostgREST in our deployment** — rejected; deployment is owned by a separate team (see [ADR-0008](0008-ops-owns-containerization.md)).
