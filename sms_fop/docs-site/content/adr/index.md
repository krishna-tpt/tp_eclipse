---
title: ADR index
description: Architecture Decision Records - the durable rationale behind Inventory Ledger's design.
---


Short, durable records of design choices that shape Inventory Ledger. Each file follows the [Michael Nygard ADR format](https://github.com/joelparkerhenderson/architecture-decision-record): **Context → Decision → Consequences → Alternatives**.

## Index

| ID | Title | Status |
|---|---|---|
| [0001](/adr/0001) | Plain Java, no framework | Accepted |
| [0002](/adr/0002) | Raw JDBC + records, no ORM | Accepted |
| [0003](/adr/0003) | Single-shot batch JAR (no long-running daemon) | Superseded by ADR-0014 |
| [0004](/adr/0004) | `pg_cron` over a Java scheduler | Accepted |
| [0005](/adr/0005) | Outbox pattern for failure notifications | Accepted |
| [0006](/adr/0006) | Azure Flexible Postgres prod, local PG dev/test | Accepted |
| [0007](/adr/0007) | No inbound business REST | Accepted |
| [0008](/adr/0008) | Ops team owns containerization and scheduling | Accepted |
| [0009](/adr/0009) | No hard-coded values | Accepted |
| [0010](/adr/0010) | Test-driven development | Accepted |
| [0011](/adr/0011) | Synthetic data tools in a separate Maven module | Accepted |
| [0012](/adr/0012) | Files.com as the production FileSource backend | Accepted |
| [0013](/adr/0013) | `SftpFileSource` with SSH public-key auth | Accepted |
| [0014](/adr/0014) | Long-running service with internal cron scheduler | Accepted |

## When to add a new ADR

Every non-obvious decision that future readers will have to re-derive without context. Examples:
- Choosing one library over another with broadly similar features
- A protocol or contract that constrains downstream code
- A "we deliberately did NOT do X" choice
- A trade-off where the runner-up option is plausible

Trivial choices (e.g., `LinkedHashMap` vs `HashMap`) do not warrant an ADR.

## Lifecycle

- **Proposed** — circulating for review
- **Accepted** — decision is in effect; code reflects it
- **Deprecated** — no longer in effect but kept for history; supersede with a new ADR
- **Superseded by ADR-NNNN** — replaced; explain the swap in the new ADR's Context
