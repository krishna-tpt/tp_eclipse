---
title: ADR-0001 - Plain Java, no framework
description: Architecture Decision Record 0001 - Plain Java, no framework
---


**Status:** Accepted · **Date:** 2026-05-18

## Context

The base release is a single-shot batch JAR that runs once per day. The workload is narrow: read files, COPY to a staging table, call a stored function, drain an outbox table, exit. No HTTP server, no background threads, no inbound traffic.

A framework (Spring Boot, Quarkus, Micronaut) would bring DI, auto-config, lifecycle, logging, OpenAPI, and metrics. Most of those features are not needed here, and the team already maintains `smslite-filemanager` — a plain-Java daemon with no framework.

## Decision

Use plain Java 21 (Eclipse Temurin) with single-purpose libraries. No DI framework, no auto-configuration, no application context. Components are plain classes wired by hand in `App.main`.

Libraries chosen for narrow purposes only: HikariCP was not adopted (single connection per run), Flyway for migrations, Jackson for JSON, SnakeYAML for config, SLF4J + Logback for logging, MINA SSHD for SFTP, `java.net.http.HttpClient` for outbound webhooks.

## Consequences

**Positive**
- Smallest possible dependency surface — ~10 transitive jars in production
- Startup cost is ~250 ms; runtime memory ~150 MB
- Failures are local and obvious — no opaque DI wiring or reflection
- Matches the team's existing operational mental model
- Easier security review (fewer libraries to audit)

**Negative**
- We hand-wire components in `App.main`. If the system grows substantially, this seam will pinch.
- No `@Test` configuration auto-magic — tests must instantiate Config / Database / etc explicitly.
- If a future feature genuinely needs DI (e.g. multi-tenant module isolation), this decision should be revisited.

## Alternatives considered

- **Spring Boot 3** — most familiar to Java teams; rejected for footprint and complexity vs the narrow workload.
- **Quarkus 3** — fast startup, K8s-native; rejected because we don't need REST/health/metrics endpoints and adding them just to use the framework would invert the cost/benefit.
- **Micronaut 4** — compile-time DI; rejected for same reasons as Quarkus with smaller community.
