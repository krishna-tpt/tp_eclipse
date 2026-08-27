---
title: Interface catalog
description: The mich_inv_stocklevel_batch.yaml file — every column, every rule.
---

**File:** `filemanager-core/src/main/resources/interfaces/mich_inv_stocklevel_batch.yaml`

One YAML file per interface family. Loaded from classpath at JAR startup; validated by `CatalogLoader`; immutable for the process lifetime.

**Contract explainer:** [Catalog-driven decoding](/architecture/catalog-driven).

## Full catalog contents

```yaml
interface: MICH_INV_STOCKLEVEL
variant:   "413"
version:   "1.0"

matches:
  filename_pattern: "^MICH_INV_STOCKLEVEL_(SMS|DMC|BATCH)_\\d{3}[._].+$"
  header_confirms:
    record: HEADER_FILE
    field:  3
    equals: MNA

parser:
  delimiter: ";"
  quote:     "\""
  encoding:  UTF-8
  trim:      true
  empty_is_null: true
  decimal_separator: "."

envelope:
  - tag: HEADER_FILE
    fields: 20
    capture:
      source_system:  { field: 3 }
      generated_at:   { field: 6, type: timestamp, format: yyyyMMddHHmmss }
      batch_filename: { field: 7 }
  - tag: HEADER_BLOCK
    fields: 3
  - tag: FOOTER_BLOCK
    fields: 3
    validates:
      data_row_count: { field: 3 }
  - tag: FOOTER_FILE
    fields: 2
    validates:
      total_row_count: { field: 2 }

records:
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
      # Pipeline-injected metadata
      tenant_code:                 { from_envelope: HEADER_FILE, captured_as: source_system }
      file_batch_id:               { from_envelope: HEADER_FILE, captured_as: batch_filename }
      source_marker:               { literal: MICHELIN }

      # CFO positions 1..54 (file fields 2..55 — field 1 is the record tag)
      company_code:                { field: 2 }
      organization_code:           { field: 3 }
      organization_type:           { field: 4 }
      finished_good_org_type:      { field: 5 }
      oc_companycode:              { field: 6 }
      od_countrycode:              { field: 7 }
      or_countrycode:              { field: 8 }
      site:                        { field: 9 }
      item_segment1:               { field: 10, required: true }
      parent_item:                 { field: 11 }
      item_segment2:               { field: 12 }
      cci:                         { field: 13 }
      cad:                         { field: 14 }
      item_category:               { field: 15 }
      fg_category:                 { field: 16 }
      purchasing_category:         { field: 17 }
      mfg_category:                { field: 18 }
      product_family:              { field: 19 }
      om_category:                 { field: 20 }
      warehouse:                   { field: 21 }
      item_description:            { field: 22 }
      revision:                    { field: 23 }
      subinventory:                { field: 24 }
      locator:                     { field: 25 }
      lot:                         { field: 26 }
      lpn:                         { field: 27 }
      parent_lpn:                  { field: 28 }
      serial:                      { field: 29 }
      containerized_flag:          { field: 30 }
      receiving_location:          { field: 31 }
      primary_quantity:            { field: 32, type: numeric, required: true }
      primary_uom:                 { field: 33 }
      primary_quantity_value:      { field: 34, type: numeric, default: "0" }
      unit_cost:                   { field: 35, type: numeric }
      snapshot_date:               { field: 36, type: date, format: dd-MM-yyyy }
      subinventory_status:         { field: 37 }
      material_location:           { field: 38, allowed_values: [ONHAND, RECEIVING] }
      owning_party:                { field: 39 }
      cost_type:                   { field: 40 }
      customer_country_code:       { field: 41 }
      market_code:                 { field: 42 }
      client_code:                 { field: 43 }
      nip_imp:                     { field: 44 }
      matnom:                      { field: 45 }
      matref:                      { field: 46 }
      snapshot_date_with_timezone: { field: 47, type: timestamptz, format: "yyyy-MM-dd HH:mm:ss xxx" }
      duns_code:                   { field: 48 }
      mich_duns:                   { field: 49 }
      intended_market:             { field: 50 }
      item_product_line:           { field: 51 }
      stock_type:                  { field: 52 }
      # Fields 53–55 of the FD spec not carried in the delimited variant — literal NP
      receiving_location_type:     { literal: NP }
      stock_status:                { literal: NP }
      business_line:               { literal: NP }
```

## Column table (mapped to `staging.stocklevel_inbox`)

| Column | Source | Type | Constraints | Notes |
|---|---|---|---|---|
| `tenant_code` | envelope `HEADER_FILE.source_system` (field 3) | TEXT | | `MNA` for Michelin |
| `file_batch_id` | envelope `HEADER_FILE.batch_filename` (field 7) | TEXT | | filename embedded in header |
| `source_marker` | literal `MICHELIN` | TEXT | | constant |
| `company_code` | field 2 | TEXT | | Operating Unit, e.g. `408_OU` |
| `organization_code` | field 3 | TEXT | | e.g. `P57` |
| `organization_type` | field 4 | TEXT | | `FINISHED GOODS`, `MANUFACTURING`, `PDR` |
| `finished_good_org_type` | field 5 | TEXT | | e.g. `CW` |
| `oc_companycode` … `or_countrycode` | fields 6-8 | TEXT | | not used today |
| `site` | field 9 | TEXT | | plant/site code |
| `item_segment1` | field 10 | TEXT | **required** | product code (e.g. `459473_101`) |
| `parent_item` … `cad` | fields 11-14 | TEXT | | not used today |
| `item_category` | field 15 | TEXT | | PDR Reporting category |
| `fg_category` … `mfg_category` | fields 16-18 | TEXT | | |
| `product_family` | field 19 | TEXT | | |
| `om_category` | field 20 | TEXT | | Order Management / Product Line |
| `warehouse` | field 21 | TEXT | | Organization full name |
| `item_description` | field 22 | TEXT | | |
| `revision` | field 23 | TEXT | | not currently used |
| `subinventory` | field 24 | TEXT | | `ONHAND`, `US01`, … |
| `locator` | field 25 | TEXT | | optional |
| `lot` | field 26 | TEXT | | |
| `lpn` … `serial` | fields 27-29 | TEXT | | not currently used |
| `containerized_flag` | field 30 | TEXT | | not currently used |
| `receiving_location` | field 31 | TEXT | | only when `material_location=RECEIVING` |
| `primary_quantity` | field 32 | NUMERIC | **required** | can be negative |
| `primary_uom` | field 33 | TEXT | | |
| `primary_quantity_value` | field 34 | NUMERIC | default `0` | qty × unit_cost |
| `unit_cost` | field 35 | NUMERIC | | |
| `snapshot_date` | field 36 | DATE | format `dd-MM-yyyy` | |
| `subinventory_status` | field 37 | TEXT | | `ACTIVE`, `No ATP`, … |
| `material_location` | field 38 | TEXT | allowed_values `[ONHAND, RECEIVING]` | |
| `owning_party` | field 39 | TEXT | | consigned stock owner |
| `cost_type` | field 40 | TEXT | | e.g. `Frozen` |
| `customer_country_code` | field 41 | TEXT | | |
| `market_code` … `matref` | fields 42-46 | TEXT | | |
| `snapshot_date_with_timezone` | field 47 | TIMESTAMPTZ | format `yyyy-MM-dd HH:mm:ss xxx` | |
| `duns_code`, `mich_duns` | fields 48-49 | TEXT | | |
| `intended_market` | field 50 | TEXT | | |
| `item_product_line` | field 51 | TEXT | | Costing Product Nature |
| `stock_type` | field 52 | TEXT | | `COMMERCIAL` / `INDUSTRIAL` / Other |
| `receiving_location_type` | literal `NP` | TEXT | | absent in delimited variant |
| `stock_status` | literal `NP` | TEXT | | absent in delimited variant |
| `business_line` | literal `NP` | TEXT | | absent in delimited variant |

## The `NP` marker

Three columns are declared with `{ literal: NP }` — Michelin's delimited CFO variant is silent on them, but the FD spec (`FD_MICH_INV_IC227_STOCKLEVEL_CFO`) lists them. Rather than store NULL (which would ambiguously mean "the file was silent" OR "the file explicitly said empty"), we store the string `"NP"` (Not Present).

Downstream reports can distinguish:

- `NULL` → the column exists in the schema but the file didn't populate it (data-quality issue)
- `"NP"` → the column exists in the schema and the current variant doesn't carry it (expected)

Change back to `{ field: 53 }` / `{ field: 54 }` / `{ field: 55 }` once Michelin extends the delimited exporter.

## Dedupe keys

The pipeline rejects a file if any two data rows agree on ALL of:

```
organization_code, item_segment1, warehouse, subinventory, locator, lot, material_location
```

That combination is a Michelin exporter bug — two rows for the same stock position would double-count. The rejection is intentional; escalate to Michelin rather than loosen the key.

If Michelin confirms they treat serials distinctly (adding `serial` to the natural key), add it to `dedupe_keys:` in the YAML. No code change needed.

## Envelope validation

```yaml
- tag: FOOTER_BLOCK
  fields: 3
  validates:
    data_row_count: { field: 3 }
- tag: FOOTER_FILE
  fields: 2
  validates:
    total_row_count: { field: 2 }
```

`EnvelopeValidator` compares the captured value to the count of data records seen:

- `FOOTER_BLOCK.field 3` = count of `MTL_STOCKLEVEL` records seen in the block
- `FOOTER_FILE.field 2` = total row count across the whole file

Mismatch → `EnvelopeValidationException` → file goes to `reject/`. Guards against mid-transfer truncation.

## Change control

- **Rebuild required** — the catalog ships in the JAR. YAML edits + `mvn package`.
- **Schema alignment** — new columns require an `ALTER TABLE staging.stocklevel_inbox ADD COLUMN ...` in an `alter_*.sql` under the current customer tag.
- **Field renumbering** — never mutate an existing column's `field:` value without cross-checking every fixture and every downstream consumer.
- **Testing** — every YAML change must pass `CatalogLoaderTest`, `VariantDetectorTest`, and `CatalogIngestPipelineTest` (integration).
