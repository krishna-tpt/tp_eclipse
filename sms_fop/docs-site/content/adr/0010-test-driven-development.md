---
title: ADR-0010 - Test-driven development
description: Architecture Decision Record 0010 - Test-driven development
---


**Status:** Accepted · **Date:** 2026-05-18

## Context

The user mandated TDD for this project alongside the no-hard-coding rule. Tests must be written **before** the implementation they exercise. Every test case must be catalogued and mapped to a functional or non-functional requirement.

The deck's NFR list (12 entries) is largely operational — partitioning, archival, audit, tenant isolation, drift detection. Without tests, these become aspirational notes in slides. With tests, they become enforceable contracts.

## Decision

- **`TEST-PLAN.md`** lists every test case with a `TC-NNN` id, description, preconditions, steps, expected result, and a mapping to an `F-NN` (functional) or NFR row from `SPEC.md`. It is committed before any test code is written.
- **RED → GREEN → REFACTOR.** For each SQL function and Java class:
  1. Write the failing test referencing the as-yet-uncreated function/class.
  2. Run the build; confirm RED (compile or assertion failure).
  3. Write the minimum production code to make the test pass.
  4. Refactor with tests as safety net.
- **Coverage gates** enforced by JaCoCo on `mvn verify`:
  - ≥ 80% line coverage across `filemanager-core` production sources.
  - 100% line coverage on critical-path classes: `ConfigLoader`, `Database`, `OpeningBalanceLoader`, `OutboxDrainer`.
- **`TEST-RESULTS.md`** is regenerated on every CI run, listing `TC-ID → pass/fail/duration/timestamp`.
- **Test layers and tooling:**
  - SQL: pgTAP suite in `db/test/*.sql` (TC-100 — TC-195)
  - Java unit: JUnit 5 + AssertJ + Mockito (TC-200 — TC-299)
  - Java integration: JUnit 5 + Testcontainers (TC-300 — TC-399, TC-602 — TC-603)
  - Security/negative: pgTAP + JUnit (TC-500 — TC-504, TC-604)

A test case is "done" only when: code exists, test exists, test passes, the latest CI run is recorded in `TEST-RESULTS.md`.

## Consequences

**Positive**
- API surfaces are pinned by tests before they harden in production code. Test signatures double as design contracts.
- The NFRs in the deck become executable assertions, not aspirations.
- New contributors have a guided tour via `TEST-PLAN.md` → individual TC implementations.
- Coverage gate catches dead code and untested branches mechanically.

**Negative**
- Up-front cost is real — RED-phase tests for 30+ classes were written before any production code.
- Integration tests require a Postgres image with `pg_partman`, `pg_cron`, and `pgTAP` pre-installed (`ghcr.io/tenthplanet/inventoryledger-pg:test`). Build and document this image as part of CI setup.
- Some properties are easier to assert in production than in tests (e.g. partition pruning at scale). For those we document the assertion target in `TEST-PLAN.md` and revisit when load testing.

## Alternatives considered

- **Test-after development** — rejected by user mandate. Anecdotally also produces lower-quality tests biased toward implementation, not behavior.
- **No coverage gate** — rejected; without a hard gate, coverage drifts in the wrong direction.
- **Only integration tests, no units** — rejected; integration tests are slow and obscure root cause when they fail.
