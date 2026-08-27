---
title: ADR-0013 - `SftpFileSource` with SSH public-key auth
description: Architecture Decision Record 0013 - `SftpFileSource` with SSH public-key auth
---


**Status:** Accepted · **Date:** 2026-06-09

## Context

[ADR-0012](0012-files-com-as-file-source.md) selected files.com (HTTPS / native
SDK) as the default `FileSource`. `SftpFileSource` was left as a stub for
"future customer mandates plain SFTP."

Within days of that decision the customer asked whether SSH public/private
key auth would work as an alternative to API tokens. The answer is no on the
SDK path — public/private key auth is an SFTP-protocol concept. Implementing
the SFTP sibling is the only way to honor that auth model.

We made a deliberate choice to **build the SFTP backend now even though we
don't ship it as default** — so the option exists the day a customer
requires SSH-key auth or HTTPS is blocked at their egress.

## Decision

Replace the `SftpFileSource` stub with a real implementation against Apache
MINA SSHD (`org.apache.sshd:sshd-sftp`, already a project dep). Behavior:

- Implements the existing `FileSource` SPI — same `list/download/moveToArchive/moveToReject` contract as `LocalFolderFileSource` and `FilesComFileSource`.
- **Per-call** connection lifecycle: each call opens an `SshClient` → `ClientSession` → `SftpClient`, does its work, closes. No long-lived connection to keep healthy. The polling cadence (minutes) makes connect cost irrelevant.
- Auth: `private_key_path` if set (preferred), else `password`. Both blank → construction fails.
- Host key pinning via `known_hosts_path` (production). Blank → accept any with a loud warning (dev only).
- Key formats: standard PEM (OpenSSH, PKCS#8, RSA, ed25519) via `SecurityUtils.loadKeyPairIdentities`. Optional passphrase.

Activation is config-only — `FILE_SOURCE=sftp`. The factory already routes the
`SFTP` enum case; no business-logic change.

## Why now, even though it's not the default

- The cost of building is contained (one file, one test, one ADR) — far less than the cost of either telling a customer "we don't support that" or scrambling to build it under deadline.
- The SPI is already there. Keeping a sibling backend live in CI catches regressions in the abstraction (the `FileSource` interface) that we'd otherwise only discover the day we activated SFTP.
- Apache MINA SSHD is already a runtime dependency — implementing now adds zero new dependencies, just zero `UnsupportedOperationException`.

## Why per-call connections

A long-lived `SshClient` + `ClientSession` would be faster but introduces:
- Stale-connection detection and reconnect logic
- Concurrency rules around the shared `SftpClient` instance
- Cleanup on JVM shutdown

The file manager polls every few minutes and processes a small batch. Connect
overhead (~150ms) is dwarfed by the polling interval. Per-call wins on
simplicity. Revisit only if poll frequency ever climbs to single-second.

## Why MINA SSHD over JSch

- JSch is unmaintained (last release 2018) and has known weak-cipher defaults.
- MINA SSHD is actively developed, used by Apache Karaf and others.
- It's already a transitive dep via `sshd-sftp`.

## Test posture

Unit tests run against an **in-process `SshServer`** (also MINA), virtualized
to a `@TempDir`. Six cases:

| TC | What |
|---|---|
| TC-250 | password auth + list-with-name-filter |
| TC-251 | download writes bytes |
| TC-252 | moveToArchive renames on the server |
| TC-253 | moveToReject renames on the server |
| TC-254 | private-key auth (generated RSA keypair, PKCS#8 PEM) |
| TC-255 | construction fails when neither password nor key path is set |

No Docker, no live network, deterministic. A live integration test against
the customer's real SFTP endpoint can be added later as a sibling of
`FilesComConnectivitySmokeTest` — env-gated, opt-in.

## Configuration shape

```yaml
file:
  source: sftp                                  # filescom | sftp | local
  sftp:
    host:                   ${SFTP_HOST}
    port:                   ${SFTP_PORT:22}
    username:               ${SFTP_USER}
    password:               ${SFTP_PASSWORD:}              # blank if using key
    private_key_path:       ${SFTP_PRIVATE_KEY_PATH:}      # wins if set
    private_key_passphrase: ${SFTP_PRIVATE_KEY_PASSPHRASE:}
    known_hosts_path:       ${SFTP_KNOWN_HOSTS_PATH:}      # blank = dev only
    pickup_path:            ${SFTP_PICKUP_PATH}
    archive_path:           ${SFTP_ARCHIVE_PATH}
    reject_path:            ${SFTP_REJECT_PATH}
    connect_timeout_ms:     ${SFTP_CONNECT_TIMEOUT_MS:10000}
```

## Consequences

**Positive**
- SSH key auth is now a real option, switchable by config alone.
- HTTPS-blocked networks (rare, but real) have a path.
- The SPI is now exercised by three siblings in CI — any breaking change to
  `FileSource` shows up loudly.
- No new dependencies — `sshd-sftp` was already there for the stub.

**Negative**
- One more code path to maintain. The connection lifecycle is the riskiest
  part — review carefully on any MINA SSHD upgrade.
- `AcceptAllServerKeyVerifier` is reachable in dev mode (blank
  `known_hosts_path`). Logged as a warning, but not enforced. If a production
  deploy ever ships with blank `SFTP_KNOWN_HOSTS_PATH`, that's a MITM risk —
  document loudly and consider hardening to a fail-fast in a future ADR.

## Alternatives considered

- **Stay with the stub** — rejected; we'd be in the same conversation again in
  a future customer cycle, with less context.
- **Implement via JSch** — rejected (see "Why MINA SSHD over JSch").
- **Wrap the OpenSSH binary via `Runtime.exec`** — rejected; loses the
  failure-mode richness of an SSH library, adds a system dependency.
