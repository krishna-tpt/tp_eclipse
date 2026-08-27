---
title: ADR-0009 - No hard-coded values
description: Architecture Decision Record 0009 - No hard-coded values
---


**Status:** Accepted · **Date:** 2026-05-18

## Context

Hard-coded values rot. They embed assumptions that become invisible to future readers, force code changes for what should be operational tuning, and silently diverge from documentation. Common offenders: connection timeouts, retry counts, file paths, batch sizes, retention windows, cron expressions, severity thresholds.

The user set this as a non-negotiable rule during the design review.

## Decision

**Every value that could plausibly change between environments, deployments, or business decisions must come from configuration.** This means:

| Where | What |
|---|---|
| `application.yaml` + env vars | Java service config (DB URLs, file paths, retry counts, timeouts, batch sizes, hash algorithm, etc.) |
| `tools.yaml` + env vars | Synthetic data tool config (counts, ratios, seeds, output paths) |
| `pipeline_config` table | DB-side business parameters (retention days, drift sample %, alert severity) — editable by ops via SQL UPDATE without redeploying |
| `cron.job` table | Schedule expressions — editable via `cron.alter_job` without redeploying |

`static final` constants are permitted **only** for true code invariants: regex patterns, SQL skeletons, fixed CSV header literal (`GenerateOpeningBalance.HEADER`), error codes. If removing the constant and reading from config would feel silly, it's a code invariant. Otherwise it isn't.

Validation happens at startup:
- `ConfigLoader` fail-fasts on missing required env vars with a named error (e.g. `"db.url is required"`).
- Wrong types throw `ConfigValidationException` with the offending field.

## Consequences

**Positive**
- Operational tuning becomes a runtime change (SQL UPDATE or env-var rotation), not a code change.
- Reviewers can grep for hard-coded values mechanically. The `NoHardcodedSecretsTest` enforces the rule for secrets specifically.
- Test fixtures use distinctive non-secret values so tests don't accidentally encode prod credentials.
- Documentation (`README.md` env-var table) stays in sync with code because there's only one source of truth.

**Negative**
- More YAML to write and review.
- Required fields with no sensible default add startup friction for new developers — mitigated by profile-specific overlays (`application-dev.yaml`) providing dev-friendly defaults.

## Alternatives considered

- **Hard-code dev defaults, override only in prod** — rejected; the asymmetry is itself a bug magnet (dev passes, prod fails).
- **Allow inline `static final` constants liberally** — rejected; the rule must be unambiguous to enforce.
