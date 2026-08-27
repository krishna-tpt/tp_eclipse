---
title: File sources
description: files.com, SFTP, and local folder — pick one with FILE_SOURCE.
---

Three implementations of the `FileSource` interface. The active one is chosen at startup by the `FILE_SOURCE` env var. Files.com is the production default.

| Value | Implementation | Purpose |
|---|---|---|
| `filescom` (default) | `FilesComFileSource` via `FilesComSdkGateway` | Production. Michelin's inbound landing zone. |
| `sftp` | `SftpFileSource` (Apache MINA SSHD) | Fallback route. Same landing zone via SFTP protocol. |
| `local` | `LocalFolderFileSource` | Dev/test. Reads from a local directory. |

## `FileSource` interface

```java
public interface FileSource {
    List<SourceFile> list();
    FileOutcome download(SourceFile file, Path localTarget);
    FileOutcome moveToArchive(SourceFile file);
    FileOutcome moveToReject(SourceFile file);
}
```

`SourceFile` carries the remote path + a stable identifier. `FileOutcome` returns Success | Failure with a reason. Implementations retry only what's obviously transient (2s connect timeout; single retry on 5xx). Anything more is escalated to the caller.

## Files.com (production)

`FilesComFileSource` wraps `FilesComSdkGateway`, which is a thin layer over `com.files:files-sdk` (native REST SDK). This is the production default because Michelin's DevOps chose files.com as the exchange platform.

**Config (env vars):**

- `FILES_COM_API_KEY` — REST bearer token
- `FILES_COM_BASE_URL` — defaults to `https://app.files.com`; Michelin uses `https://filehub.michelin.net`
- `FILES_COM_PICKUP_PATH` — e.g. `/EU/PRD/BR/C10/Inbound`
- `FILES_COM_ARCHIVE_PATH` — e.g. `/EU/PRD/BR/C10/Archive`
- `FILES_COM_REJECT_PATH` — e.g. `/EU/PRD/BR/C10/Error`
- `FILES_COM_PAGE_SIZE` — default 200 (list page size)
- `FILES_COM_CONNECT_TIMEOUT_MS`, `FILES_COM_READ_TIMEOUT_MS` — defaults 10s / 60s

**Operations against files.com** (from `FilesComGateway`):

- `list(folder)` — paginated folder listing
- `download(path)` — streamed download to a local temp file
- `rename(path, newPath)` — `PATCH file's path`
- `move(path, destinationFolder)` — `POST file_actions/move`
- `copy(path, destinationFolder)` — `POST file_actions/copy`
- `delete(path)` — `DELETE`

**Known gotcha:** the API key must have `full_permission` on the pickup/archive/reject folders. A read-only key surfaces as HTTP 403 `FullPermissionRequired` on the first move call.

## SFTP (fallback)

`SftpFileSource` uses `org.apache.sshd:sshd-sftp` (MINA SSHD). Cleaner than JSch, actively maintained, JDK 21 compatible.

**Auth:** SSH keypair OR password. If both are set, key wins.

**Config (env vars):**

- `SFTP_HOST`, `SFTP_PORT` (default 22), `SFTP_USER`
- `SFTP_PASSWORD` — for password auth
- `SFTP_PRIVATE_KEY_PATH` — PEM file readable by the JAR process
- `SFTP_PRIVATE_KEY_PASSPHRASE` — blank if key is unencrypted
- `SFTP_KNOWN_HOSTS_PATH` — production posture: pin the host key; blank = accept any (dev only)
- `SFTP_PICKUP_PATH`, `SFTP_ARCHIVE_PATH`, `SFTP_REJECT_PATH`
- `SFTP_CONNECT_TIMEOUT_MS` — default 10s

**Operations:** `list`, `get`, `rename` (moves), `remove`. All under one SSH session per pipeline run.

**Known gotcha:** Michelin's corporate VDIs block outbound port 22. Runs from the customer's K8s cluster fine (private endpoint), but you can't smoke-test an SFTP config from a locked-down developer machine. The `filescom` route works over 443 and is unblocked.

## Local folder (dev/test)

`LocalFolderFileSource` reads from three configured local directories. Used by every unit / integration test and by developer smoke tests before pushing to the branch.

**Config:**

- `LOCAL_PICKUP_PATH`, `LOCAL_ARCHIVE_PATH`, `LOCAL_REJECT_PATH`

**Behavior:** `list()` returns files in the pickup folder sorted by name (deterministic for tests). `moveToArchive` creates the `YYYY-MM/` subdirectory and moves atomically with `Files.move(REPLACE_EXISTING)`.

## Factory + dispatch

`DefaultFileSourceFactory.create(config)` inspects `Config.fileSource.type` and returns the right implementation. The factory is called once at startup; the resolved `FileSource` is injected into `CatalogFileLoader` for the process lifetime.

```java
switch (type) {
    case FILESCOM -> new FilesComFileSource(new FilesComSdkGateway(config), config);
    case SFTP     -> new SftpFileSource(config);
    case LOCAL    -> new LocalFolderFileSource(config);
}
```

Choosing at startup (not per-tick) means switching FILE_SOURCE requires a pod restart. That's intentional — the source is a deployment concern, not a runtime one.

## Path convention (all three)

- **Pickup** — flat; the daemon lists this and processes anything matching the catalog's `filename_pattern`.
- **Archive** — organized by `YYYY-MM/`; the daemon creates monthly subfolders on demand. Filename gets a `_<timestamp>` suffix so re-drops with the same name don't collide.
- **Reject** — flat; a failed file is moved here as-is (no rename). A companion `notification_outbox` row and `audit.event_log 'file.failed'` event carry the diagnosis.

## Smoke tests

Two dedicated smoke tests in `filemanager-core/src/test/java/…/smoke/`:

- **`FilesComConnectivitySmokeTest`** — lists the configured pickup folder, downloads one file, verifies a checksum. Not run by default; enable with `-Dsmoke.filescom=true`.
- **`SftpConnectivitySmokeTest`** — same for SFTP; enable with `-Dsmoke.sftp=true`.

Both require live credentials. Meant for VDI / prod-parity connectivity checks, not CI.
