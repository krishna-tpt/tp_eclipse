---
title: Catalog-driven decoding
description: The YAML interface catalog — one contract, zero parser code changes.
---

Every file format the JAR understands is declared in a YAML file under `filemanager-core/src/main/resources/interfaces/`. The catalog is loaded from classpath at startup, validated, and immutable for the run.

**Adding or changing a column is a YAML edit + rebuild.** Parser code is never touched.

## Why a catalog

The alternative is per-format Java code: one class per file type, per-column parsing hand-written, envelope validation woven into the code. That approach breaks the moment Michelin adds a column, splits a field, changes the delimiter for one variant, or ships a second file family (batch vs SMS vs DMC). The catalog isolates all of that in data.

## Contract shape

```yaml
interface: MICH_INV_STOCKLEVEL   # logical interface name
variant:   "413"                 # variant discriminator
version:   "1.0"

matches:                          # how VariantDetector picks this catalog
  filename_pattern: "^MICH_INV_STOCKLEVEL_(SMS|DMC|BATCH)_\\d{3}[._].+$"
  header_confirms:
    record: HEADER_FILE
    field:  3
    equals: MNA

parser:                           # PositionalRecordParser knobs
  delimiter: ";"
  quote:     "\""
  encoding:  UTF-8
  trim:      true
  empty_is_null: true
  decimal_separator: "."

envelope:                         # non-data record shapes
  - tag: HEADER_FILE
    fields: 20
    capture:
      source_system:  { field: 3 }
      generated_at:   { field: 6, type: timestamp, format: yyyyMMddHHmmss }
      batch_filename: { field: 7 }
  - tag: FOOTER_FILE
    fields: 2
    validates:
      total_row_count: { field: 2 }

records:                          # data records → staging tables
  - tag: MTL_STOCKLEVEL
    fields: 52
    target_table: staging.stocklevel_inbox
    dedupe_keys:
      - organization_code
      - item_segment1
      - warehouse
      - subinventory
      - locator
      - lot
      - material_location
    columns:
      tenant_code:    { from_envelope: HEADER_FILE, captured_as: source_system }
      source_marker:  { literal: MICHELIN }
      item_segment1:  { field: 10, required: true }
      primary_quantity: { field: 32, type: numeric, required: true }
      snapshot_date:  { field: 36, type: date, format: dd-MM-yyyy }
      material_location: { field: 38, allowed_values: [ONHAND, RECEIVING] }
      # …
      receiving_location_type: { literal: NP }
      stock_status:            { literal: NP }
      business_line:           { literal: NP }
```

## Column source rules

Every column entry is one of three shapes:

| Shape | What it does |
|---|---|
| `{ field: N }` | Take positional field N from the current data record (1-indexed). |
| `{ from_envelope: TAG, captured_as: KEY }` | Pull from the EnvelopeContext captured earlier. |
| `{ literal: VALUE }` | Inject a constant string. Used for `source_marker: MICHELIN` and for columns absent in the delimited variant (see `NP` markers). |

Additional per-column modifiers (all optional):

- `type: numeric | date | timestamp | timestamptz` — enable coercion (default: TEXT pass-through)
- `format: <SimpleDateFormat>` — required when `type` is a date/timestamp variant
- `required: true` — non-null after coercion
- `max_length: N` — string length cap
- `allowed_values: [...]` — whitelist check
- `default: "value"` — used when coerced value is null

Any violation raises `FieldMappingException` naming the column and the specific rule.

## Envelope validates

Envelope records may carry a `validates:` block. `EnvelopeValidator` compares the captured value to the count of DATA records seen (see [Ingest pipeline](/architecture/ingest-pipeline#5e-envelope-validation)).

- `FOOTER_BLOCK.field 3` = count of `MTL_STOCKLEVEL` records (per block)
- `FOOTER_FILE.field 2` = total-row-count across the whole file

Mismatch → `EnvelopeValidationException`, file rejected.

## Variant detection precedence

```
1. VariantDetector iterates all loaded catalogs
2. For each catalog:
   a. Try filename_pattern regex against the file's basename
   b. If no match → DetectionResult.None
   c. If match and no header_confirms declared → DetectionResult.Match(interface, variant)
   d. If match and header_confirms present:
        - Read first non-blank line as the header record
        - Check header_confirms.field == header_confirms.equals
        - Match → DetectionResult.Match; mismatch → DetectionResult.None
3. First matching catalog wins
4. No matches → file goes to reject with reason="no catalog matched"
```

The header_confirms check is what lets the same filename pattern serve multiple interfaces (or reject look-alike files from other systems).

## Catalog validation (at startup)

`CatalogLoader` fails fast on any of these:

- Two catalogs declaring the same `(interface, variant)` pair
- A record with two columns mapping to the same field number
- A record missing `target_table`
- A column with an unknown `type`
- A `format` missing on a typed date/timestamp column
- A `from_envelope` referencing an envelope tag not declared in `envelope:`

Errors name the offending catalog file and the offending column, so a mis-typed YAML fails at container startup, not mid-run.

## Testing the catalog

Two levels of coverage in `filemanager-core/src/test/java/…/catalog/`:

- **Unit** — `CatalogLoaderTest`, `VariantDetectorTest`, `PositionalRecordParserTest`, `FieldMapperTest` cover the mechanical rules on synthetic fixtures.
- **Integration** — `CatalogIngestPipelineTest` runs the full pipeline against a Testcontainers Postgres with the real Michelin catalog + a real fixture file (TC-310 in the [test plan](/quality/test-plan)).

## Adding a new interface

1. Create `filemanager-core/src/main/resources/interfaces/<name>.yaml` following the shape above.
2. Add a matching `staging.*` table via `alter_*.sql` in the current customer deploy tag.
3. Write a fixture file for `CatalogIngestPipelineTest`.
4. Rebuild the JAR; the catalog is picked up automatically from classpath at startup.

No changes to `PositionalRecordParser`, `FieldMapper`, `EnvelopeValidator`, or `CatalogIngestPipeline`.
