---
title: ADR-0002 - Raw JDBC + records, no ORM
description: Architecture Decision Record 0002 - Raw JDBC + records, no ORM
---


**Status:** Accepted · **Date:** 2026-05-18

## Context

All ledger writes flow through PostgreSQL stored functions (`load_opening_balance`, `post_transaction`, `upsert_order`, etc.). The Java service has `EXECUTE` privileges on these functions and `INSERT` on staging tables, but no direct DML on live tables.

An ORM (Hibernate, JPA) models tables as entities and assumes the application owns the write path. Mapping stored function calls into an ORM is an exercise in fighting the framework. jOOQ would handle this well but adds a code-gen step and a non-trivial dependency.

## Decision

Use raw JDBC (`java.sql.*`) with Java 21 `record` types as DTOs. Each stored function gets a hand-written Java wrapper that builds JSON via Jackson, invokes the function via `CallableStatement` / `PreparedStatement`, and unpacks results into records.

A single `Connection` per Job run. No connection pool (HikariCP was considered and rejected — a single-shot Job derives no benefit from pooling).

## Consequences

**Positive**
- The Java code mirrors the SQL contract literally — read either to understand the other.
- No mapper layer, no lazy-loading surprises, no proxy generation.
- Records as DTOs are immutable and serialize cleanly to JSON.
- Zero ORM-related dependencies in the production JAR.

**Negative**
- Boilerplate per function (build payload, call, unpack). Acceptable since there are ~10 functions.
- Renaming a function or column requires a coordinated change in SQL + Java.
- No compile-time SQL validation (mitigated by Testcontainers integration tests that catch breakage early).

## Alternatives considered

- **JPA / Hibernate** — rejected; entity-centric model fights the function-only write path.
- **Spring JDBC (`JdbcClient`, `SimpleJdbcCall`)** — rejected to avoid pulling Spring transitively. Behaviorally equivalent to raw JDBC.
- **jOOQ** — rejected for the additional code-gen step at a 5k records/day workload. Worth revisiting if SQL surface grows.
- **JDBI** — rejected for the same reason as jOOQ at this scale.
