---
title: Ingest pipeline
description: Variant detection → parse → map → validate → staging insert → promote → archive.
---

The Java daemon processes each file through the same seven-stage pipeline. All stages are pure Java; the last two hand off to Postgres.

## The seven stages

```
1. list()                       FileSource returns candidate files
2. filter                       filename pattern from catalog
3. hash + dedup                 SHA-256; skip if already in stocklevel_batch
4. download                     stream to local temp file
5. ingest (CatalogIngestPipeline)
     5a. variant detect         filename + header_confirms
     5b. parse                  PositionalRecordParser (delimited or fixed-width)
     5c. envelope capture       HEADER_FILE + HEADER_BLOCK → EnvelopeContext
     5d. field map + coerce     FieldMapper against catalog columns
     5e. envelope validate      footer counts vs data rows
     5f. dedupe within file     natural-key duplicate detection
6. staging insert + promote     BEGIN; INSERT batch; CALL load_stocklevel; COMMIT
7. archive or reject            move file to archive/YYYY-MM or reject/ ; emit event
```

## Stage 1–4: file discovery and dedup

`CatalogFileLoader` calls `FileSource.list()` (either `FilesComFileSource`, `SftpFileSource`, or `LocalFolderFileSource`). Each candidate is filtered by the catalog's `filename_pattern` regex. Files that match are downloaded and SHA-256 hashed against `staging.stocklevel_batch`; matches are skipped and moved to archive (with a `file.skipped` audit event) — the file was already ingested by a prior run.

## Stage 5: catalog-driven decoding

`CatalogIngestPipeline` orchestrates the six sub-stages. Every rule is declared in the [catalog YAML](/reference/interface-catalog), not in code.

### 5a. Variant detection (`VariantDetector`)

Two checks run in order:

1. **Filename pattern** — regex from `matches.filename_pattern`. Michelin's stock files match `^MICH_INV_STOCKLEVEL_(SMS|DMC|BATCH)_\d{3}[._].+$`.
2. **Header confirms** — read the first non-blank line, parse it against the `HEADER_FILE` envelope shape, and check `matches.header_confirms.field` equals `matches.header_confirms.equals`. Michelin's stock files confirm on field 3 = `MNA`.

If either check fails, the pipeline returns `DetectionResult.None(reason)` and the file is rejected.

### 5b. Positional parse (`PositionalRecordParser`)

Reads the file line by line. For each non-blank line:

- Splits on `parser.delimiter` (`;` for Michelin CFO).
- Strips `parser.quote` characters (`"`).
- Trims whitespace if `parser.trim: true`.
- Converts empty fields to `null` if `parser.empty_is_null: true`.
- Strips UTF-8 BOM from the first field of the first record.
- Blank lines skipped; line numbers preserved on the emitted `ParsedRecord`.

The first field of every record is treated as the record **tag** (e.g., `HEADER_FILE`, `MTL_STOCKLEVEL`, `FOOTER_FILE`).

### 5c. Envelope capture

The catalog declares one or more envelope records (`HEADER_FILE`, `HEADER_BLOCK`, `FOOTER_BLOCK`, `FOOTER_FILE`). For each declared record found in the file, `EnvelopeContext` captures the fields listed under `capture:`. For Michelin:

| Envelope | Captured as | Source |
|---|---|---|
| `HEADER_FILE` | `source_system` | field 3 |
| `HEADER_FILE` | `generated_at` | field 6 (timestamp, `yyyyMMddHHmmss`) |
| `HEADER_FILE` | `batch_filename` | field 7 |

These captured values feed the mapper via `from_envelope: HEADER_FILE, captured_as: source_system`.

### 5d. Field mapping + type coercion (`FieldMapper` + `TypeCoercer`)

For every column in the catalog's `columns:` block:

- `{ field: N }` — take positional field N from the current record.
- `{ from_envelope: TAG, captured_as: KEY }` — pull from EnvelopeContext.
- `{ literal: VALUE }` — inject a constant. Used for `source_marker: MICHELIN` and for the three trailing columns (`receiving_location_type`, `stock_status`, `business_line`) that are absent in Michelin's delimited CFO variant — they get `NP` (Not Present).

Type coercion applies per `type:` and `format:`:

| YAML `type` | Coerced to | Notes |
|---|---|---|
| (default) | `TEXT` | strings pass through |
| `numeric` | `BigDecimal` | supports negative, decimal, thousands separator per `decimal_separator` |
| `date` | `LocalDate` | requires `format:` in `SimpleDateFormat` syntax (Michelin uses `dd-MM-yyyy`) |
| `timestamp` | `LocalDateTime` | e.g., `yyyyMMddHHmmss` on `HEADER_FILE.generated_at` |
| `timestamptz` | `OffsetDateTime` | Michelin uses `yyyy-MM-dd HH:mm:ss xxx` |

Constraint checks per column:

- `required: true` → non-null after coercion
- `max_length: N` → string length ≤ N
- `allowed_values: [...]` → value in whitelist
- `default: "0"` → substitute default if coerced value is null (for `numeric` mainly)

Any violation raises `FieldMappingException` with the column name and the specific reason.

### 5e. Envelope validation (`EnvelopeValidator`)

For each envelope declared with a `validates:` block, compare the captured field against the count of DATA records seen. Michelin's `FOOTER_BLOCK.field 3` must equal the count of `MTL_STOCKLEVEL` records; `FOOTER_FILE.field 2` must equal total-row-count. Mismatch → `EnvelopeValidationException`, the file goes to reject.

### 5f. Within-file dedupe

The catalog's `dedupe_keys:` list defines the natural key for a stock position. Two data rows that agree on every listed column are a Michelin exporter bug; the file is rejected before any staging INSERT runs. Michelin's key: `organization_code`, `item_segment1`, `warehouse`, `subinventory`, `locator`, `lot`, `material_location`.

## Stage 6: staging + promote

`StagingWriter` batches the mapped rows into a single JDBC `INSERT INTO staging.stocklevel_inbox (…) VALUES (…), (…), …` under one transaction. On success, it calls `staging.load_stocklevel(file_name)` which drains the staging batch into `processed.opening_balance` (DELETE-then-INSERT for the file's batch). If the promote raises, the whole transaction rolls back — no partial state ever reaches processed.

## Stage 7: archive or reject

On success, `Archiver` moves the file to `archive/YYYY-MM/<name>_<ts>`. On any pipeline exception, the file moves to `reject/` (or `Error/` on files.com) and:

- A row is added to `notification_outbox` with `severity=ERROR`, `source='file_loader'`, `message=<exception summary>`.
- A `file.failed` event is written to `audit.event_log` with `error_code` and `latency_ms`.

The next scheduler tick still fires — a per-file failure does not kill the daemon or the scheduler.
