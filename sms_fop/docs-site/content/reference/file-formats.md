---
title: File formats
description: The two Michelin stock file shapes we consume — fixed-width .dat vs delimited .cfo.
---

Michelin ships stock files in two shapes. Same business content; different physical encoding.

## Delimited CFO (production)

**Extensions seen:** `.cfo`, `.LSF.cfo`, `.dat` (some legacy files use `.dat` despite being delimited)  
**Encoding:** UTF-8  
**Line terminator:** LF or CRLF (both accepted)

### Structure

Semicolon-delimited, double-quoted values. Each record is one line. Field 1 of every record is the record tag.

```
"HEADER_FILE";"BATCH";"MNA";"...";"...";"20260715043200";"batch_filename.cfo";...;"20"

"HEADER_BLOCK";"...";"3"
"MTL_STOCKLEVEL";"408_OU";"P57";"FINISHED GOODS";...;52 fields total
"MTL_STOCKLEVEL";"408_OU";"P57";"FINISHED GOODS";...;52 fields total
"MTL_STOCKLEVEL";...
"FOOTER_BLOCK";"...";"284"
                              ↑ data_row_count = number of MTL_STOCKLEVEL rows in this block

"FOOTER_FILE";"...";"286"
                        ↑ total_row_count = header + data + footer records
```

### Record tags

| Tag | Fields | Purpose |
|---|---|---|
| `HEADER_FILE` | 20 | File-level header. Captures `source_system` (field 3), `generated_at` (field 6, timestamp), `batch_filename` (field 7). |
| `HEADER_BLOCK` | 3 | Block-level header. Zero or more per file — Michelin currently emits one. |
| `MTL_STOCKLEVEL` | 52 | Data record. One per stock position. |
| `FOOTER_BLOCK` | 3 | Block-level footer. Field 3 asserts the block's data row count. |
| `FOOTER_FILE` | 2 | File-level footer. Field 2 asserts the total row count. |

### Filename convention

```
MICH_INV_STOCKLEVEL_<VARIANT>_<TENANT>.<...>.cfo
```

Examples:

- `MICH_INV_STOCKLEVEL_SMS_413.1010.A003IIST.20260715.LSF.cfo`
- `MICH_INV_STOCKLEVEL_DMC_408.20260603064315.cfo.PFDMU005.MC93.260603_064409`
- `MICH_INV_STOCKLEVEL_BATCH_408_NNI_20260603123442_FG_IFOPEUR.dat` (legacy .dat, delimited-format)

Filename regex the catalog uses: `^MICH_INV_STOCKLEVEL_(SMS|DMC|BATCH)_\d{3}[._].+$`

### Parser knobs (from catalog YAML)

```yaml
parser:
  delimiter: ";"
  quote:     "\""
  encoding:  UTF-8
  trim:      true              # strip leading/trailing whitespace per field
  empty_is_null: true          # empty quoted string → null
  decimal_separator: "."       # for NUMERIC coercion
```

### AME_QUANTITY history

The FD spec `FD_MICH_INV_IC227_STOCKLEVEL_CFO` originally listed 55 CFO fields including `AME_QUANTITY`. Michelin removed it per **JIRA FOP_AGL-63630** — total dropped to 54. The delimited variant emits 51 fields, so with the record tag = 52 total on the wire.

### Trailing three fields

Three columns in the FD spec (fields 53–55: `receiving_location_type`, `stock_status`, `business_line`) are absent in the delimited variant. Catalog marks them with `{ literal: NP }` — see [Interface catalog](/reference/interface-catalog#the-np-marker).

## Fixed-width .dat (legacy — no longer produced)

**Extension:** `.dat`  
**Encoding:** UTF-8  
**Line terminator:** LF or CRLF  
**Line length:** 1143 characters (fixed)

Historical format. Michelin's Oracle EBS extractor produced this until they switched to the delimited CFO shape. We keep parser support in `PositionalRecordParser` (fixed-width mode) but no live catalog uses it as of 2026-07.

If Michelin ever ships a fixed-width variant again, the catalog would specify:

```yaml
parser:
  delimiter: null              # or omit
  positions:                   # column N starts at char X for L chars
    field_1:  { start: 1,   length: 20 }
    field_2:  { start: 21,  length: 30 }
    # ...
```

The rest of the pipeline (envelope capture, field mapping, envelope validation) is identical.

## Example — one MTL_STOCKLEVEL data record

```
"MTL_STOCKLEVEL";"408_OU";"P57";"FINISHED GOODS";"CW";"";"";"";"";"459473_101";"";"";"";"";"CAT_A";"FG_A";"PUR_A";"MFG_A";"FAM_1";"OM_A";"408_ES_P57_..._CO";"Item 459473_101 description";"";"ONHAND";"";"LOT_123";"";"";"";"";"";"350.0000";"EA";"1750.0000";"5.0000";"03-06-2026";"ACTIVE";"ONHAND";"";"Frozen";"FR";"MRK_1";"CLI_A";"";"";"";"2026-06-03 16:06:29 +02";"DUNS_1";"MICH_1";"MRK_1";"PL_A";"COMMERCIAL"
```

Parse it: 52 semicolon-separated fields, all double-quoted. Field 10 (`item_segment1`) = `459473_101`. Field 32 (`primary_quantity`) = `350.0000` → BigDecimal. Field 36 (`snapshot_date`) = `03-06-2026` → LocalDate via `dd-MM-yyyy`. Field 47 (`snapshot_date_with_timezone`) = `2026-06-03 16:06:29 +02` → OffsetDateTime.

## Empty vs NULL vs NP

Three distinct states for a field value:

- **Empty in file** (`""`) → NULL in `staging.stocklevel_inbox` (parser rule `empty_is_null: true`)
- **NULL in DB** → column exists but wasn't populated (data-quality issue; investigate)
- **`"NP"` in DB** → column exists in schema but delimited variant doesn't carry it (expected)

Downstream reports and Grafana dashboards distinguish these three states.

## What we don't accept

- **Non-UTF-8 files** — parser rejects
- **Missing envelope** — `HEADER_FILE` and `FOOTER_FILE` are required
- **Field count mismatch** — a data record with fewer or more than 52 fields is rejected (whole file goes to reject)
- **Envelope count mismatch** — `FOOTER_BLOCK.data_row_count` or `FOOTER_FILE.total_row_count` not matching the actual count → rejected (guards against mid-transfer truncation)
- **Two records with identical natural key** — see [Interface catalog — dedupe keys](/reference/interface-catalog#dedupe-keys)

Each rejection produces a `file.failed` event in `audit.event_log` with the specific reason, and a row in `notification_outbox` for webhook drain.
