---
title: ADR-0012 - Files.com as the production FileSource backend
description: Architecture Decision Record 0012 - Files.com as the production FileSource backend
---


**Status:** Accepted · **Date:** 2026-06-09

## Context

The MicroService picks up Michelin opening-balance files from "some" remote
landing zone. The early plan (ADR-implicit) was plain SFTP via Apache MINA
SSHD — task [#40](../../README.md) tracks that work, with `SftpFileSource` a
stub that refuses to construct.

The customer has standardized on [files.com](https://www.files.com/) as their
file-transfer platform. Files.com exposes the same files via three protocols:
SFTP, HTTPS REST, and a native Java SDK (`com.files:files-sdk`). We must pick
one without painting the codebase into a corner if a future customer prefers
plain SFTP.

## Decision

Use the **native files.com Java SDK** as the production backend, accessed
through the existing `FileSource` SPI. Specifically:

- A new `FILESCOM` member on `FileSourceType` joins `LOCAL` and `SFTP`.
- A new `FilesComFileSource` (in `org.michelin.filemanager.file`)
  implements `FileSource` and sits behind the same factory the other two use.
- A narrow `FilesComGateway` interface seams the SDK's static surface so the
  source can be unit-tested without mocking statics.
- Backend selection is driven by `file.source` in `application.yaml` /
  `FILE_SOURCE` env. `filescom` is the new default.

The existing `SftpFileSource` stub stays as the documented future sibling
(task #40 is not abandoned, just deprioritized).

## Why the native SDK over plain SFTP-to-files.com

| Concern | Native SDK | SFTP to files.com |
|---|---|---|
| Credential | One rotating API key in env | SSH key + known_hosts pinning |
| Transport | HTTPS — friendlier to corporate proxies / IDS / TLS-inspecting middleboxes | SSH (port 22 commonly outbound-blocked) |
| Pagination, retry | Built-in (`listAutoPaging`) | Hand-rolled |
| Future event-driven discovery | Files.com webhooks compose with this path | Not available over SFTP protocol |
| Lock-in to files.com | Yes, by construction | No — works against any SFTP server |

The lock-in trade-off is real but contained: one `FilesComFileSource` class
that fully implements `FileSource`. If a future customer mandates plain SFTP,
`SftpFileSource` becomes a sibling implementation behind the same interface
— no business-logic change.

## Configuration shape

```yaml
file:
  source: filescom                       # filescom | sftp | local
  filescom:
    api_key:        ${FILES_COM_API_KEY}   # required; never in YAML
    base_url:       ${FILES_COM_BASE_URL:} # optional subdomain
    pickup_path:    ${FILES_COM_PICKUP_PATH:}
    archive_path:   ${FILES_COM_ARCHIVE_PATH:}
    reject_path:    ${FILES_COM_REJECT_PATH:}
    connect_timeout_ms: ${FILES_COM_CONNECT_TIMEOUT_MS:10000}
    read_timeout_ms:    ${FILES_COM_READ_TIMEOUT_MS:60000}
    page_size:          ${FILES_COM_PAGE_SIZE:200}
```

API key resolution is the same env-var placeholder mechanism every other
secret (DB password, webhook URL) already uses — no special path.

## Consequences

**Positive**
- One secret to rotate, no SSH key plumbing.
- HTTPS gets through proxies that block port 22.
- Door open to event-driven file discovery via webhooks (Pipeline 1 stops
  polling) — that's a future ADR.
- Native idempotent `move` (atomic from the SDK's POV) for archive/reject.
- The shaded production JAR picks up `com.files:files-sdk:1.6.24` — pulls
  Jackson (already present) and OkHttp transitively.

**Negative**
- Vendor coupling at the SDK class-name level (`FilesClient`,
  `Folder.listFor`, `File.move`). Contained to `FilesComSdkGateway.java`.
- `FilesClient.apiKey` is a static field. If we ever run two files.com
  identities in one JVM, the gateway constructor races. Documented in the
  gateway's class comment; safe today (one MicroService, one tenant).
- New transitive deps in the shaded JAR — size increase of ~3 MB.

## Verification

- `FilesComFileSourceTest` (6 cases) covers list/download/move with the SDK
  seamed out via `FakeFilesComGateway`. No live network.
- A live integration test against a real files.com sandbox is intentionally
  out of scope for this ADR — gated behind `FILES_COM_API_KEY` env when added.

## Alternatives considered

- **Plain SFTP via MINA SSHD against files.com** — works, but loses HTTPS
  reach and adds SSH-key operational load. Kept as a future sibling, not the
  default.
- **REST directly via OkHttp / Java HttpClient** — re-implements what the SDK
  already does (auth, pagination, retry). Not worth the maintenance.
- **Heroku Connect / SharePoint / mounted blob storage** — outside the scope
  the customer has committed to.
