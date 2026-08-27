package org.michelin.filemanager.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.michelin.filemanager.catalog.CatalogLoader;
import org.michelin.filemanager.catalog.InterfaceDefinition;
import org.michelin.filemanager.parser.ParsedRecord;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TC-205..TC-209 + TC-213..TC-216 — FieldMapper behavior, aligned to the
 * Michelin Extract Phase spec column names.
 *
 * Tests cover the three source kinds (field/envelope/literal), the four
 * non-trivial type coercions, and each string-level validator. The locked
 * bad-row policy means any rule violation must throw FieldMappingException so
 * the pipeline rejects the whole file.
 */
class FieldMapperTest {

    private static final Path VALID = Paths.get("src/test/resources/fixtures/catalog/valid");

    private static InterfaceDefinition load() {
        return new CatalogLoader().loadFromPath(VALID)
            .find("MICH_INV_STOCKLEVEL", "BATCH_408").orElseThrow();
    }

    /**
     * Build a {@link ParsedRecord} of {@code totalFields} length. {@code values}
     * supplies positions 2..N (field 1 is the tag); unset positions are {@code null},
     * which the {@code ParsedRecord} constructor stores via its internal NULL marker.
     */
    private static ParsedRecord row(String tag, int totalFields, long lineNumber, String... values) {
        List<String> fields = new ArrayList<>(totalFields);
        fields.add(tag);
        for (int i = 1; i < totalFields; i++) {
            fields.add(i - 1 < values.length ? values[i - 1] : null);
        }
        return new ParsedRecord(tag, fields, lineNumber, "raw");
    }

    // ------------------------------------------------------------------
    // Source kinds
    // ------------------------------------------------------------------

    @Test
    @DisplayName("TC-205: column with field source maps the positional value to the target column")
    void fieldSource_maps() {
        InterfaceDefinition def = load();
        InterfaceDefinition.DataRecordSpec spec = def.findRecord("MTL_STOCKLEVEL").orElseThrow();

        String[] values = baseRowValues();
        values[20] = "Warehouse-Hub-A"; // field 22 (item_description)
        ParsedRecord rec = row("MTL_STOCKLEVEL", 55, 5, values);

        EnvelopeContext ctx = headerCaptured(def);
        MappedRow out = new FieldMapper(def).map(spec, rec, ctx);

        assertThat(out.targetTable()).isEqualTo("staging.stocklevel_inbox");
        assertThat(out.values()).containsEntry("item_segment1", "459473_101");
        assertThat(out.values()).containsEntry("item_description", "Warehouse-Hub-A");
        assertThat(out.lineNumber()).isEqualTo(5);
    }

    @Test
    @DisplayName("TC-206: column with envelope source reads from EnvelopeContext")
    void envelopeSource_readsCapture() {
        InterfaceDefinition def = load();
        InterfaceDefinition.DataRecordSpec spec = def.findRecord("MTL_STOCKLEVEL").orElseThrow();

        EnvelopeContext ctx = new EnvelopeContext();
        InterfaceDefinition.EnvelopeSpec hf = def.findEnvelope("HEADER_FILE").orElseThrow();
        String[] envFields = new String[19];
        envFields[1] = "IFOPEUR";        // field 3 (source_system)
        envFields[4] = "20260603160800"; // field 6 (generated_at)
        envFields[5] = "batch99.cfo";    // field 7 (batch_filename)
        ParsedRecord envRow = row("HEADER_FILE", 20, 1, envFields);
        ctx.capture(hf, envRow);

        ParsedRecord rec = row("MTL_STOCKLEVEL", 55, 5, baseRowValues());

        MappedRow out = new FieldMapper(def).map(spec, rec, ctx);
        assertThat(out.values()).containsEntry("tenant_code", "IFOPEUR");
        assertThat(out.values()).containsEntry("file_batch_id", "batch99.cfo");
    }

    @Test
    @DisplayName("TC-207: column with literal source uses the constant value")
    void literalSource_usesConstant() {
        InterfaceDefinition def = load();
        InterfaceDefinition.DataRecordSpec spec = def.findRecord("MTL_STOCKLEVEL").orElseThrow();

        ParsedRecord rec = row("MTL_STOCKLEVEL", 55, 5, baseRowValues());
        MappedRow out = new FieldMapper(def).map(spec, rec, headerCaptured(def));

        assertThat(out.values()).containsEntry("source_marker", "MICHELIN");
    }

    // ------------------------------------------------------------------
    // Type coercions
    // ------------------------------------------------------------------

    @Test
    @DisplayName("TC-208: NUMERIC coercion handles positive, negative, and decimal values")
    void numericCoercion_handlesAllSigns() {
        InterfaceDefinition def = load();
        InterfaceDefinition.DataRecordSpec spec = def.findRecord("MTL_STOCKLEVEL").orElseThrow();

        for (String v : List.of("100", "-100", "0", "123.45", "-46918")) {
            String[] vals = baseRowValues();
            vals[30] = v; // field 32 = primary_quantity
            ParsedRecord rec = row("MTL_STOCKLEVEL", 55, 5, vals);
            MappedRow out = new FieldMapper(def).map(spec, rec, headerCaptured(def));
            assertThat(out.values().get("primary_quantity")).isEqualTo(new BigDecimal(v));
        }
    }

    @Test
    @DisplayName("TC-209: DATE (dd-MM-yyyy) + TIMESTAMPTZ (yyyy-MM-dd HH:mm:ss xxx) coercion")
    void dateAndTimestampCoercion() {
        InterfaceDefinition def = load();
        InterfaceDefinition.DataRecordSpec spec = def.findRecord("MTL_STOCKLEVEL").orElseThrow();

        String[] vals = baseRowValues();
        vals[34] = "03-06-2026";                  // field 36 (snapshot_date)
        vals[45] = "2026-06-03 16:06:29 +02:00";  // field 47 (snapshot_date_with_timezone)
        ParsedRecord rec = row("MTL_STOCKLEVEL", 55, 5, vals);

        MappedRow out = new FieldMapper(def).map(spec, rec, headerCaptured(def));
        assertThat(out.values().get("snapshot_date")).isEqualTo(LocalDate.of(2026, 6, 3));
        assertThat(out.values().get("snapshot_date_with_timezone"))
            .isEqualTo(OffsetDateTime.of(2026, 6, 3, 16, 6, 29, 0, ZoneOffset.ofHours(2)));
    }

    // ------------------------------------------------------------------
    // Validators
    // ------------------------------------------------------------------

    @Test
    @DisplayName("TC-213: required column with null source raises FieldMappingException")
    void requiredColumnNull_raises() {
        InterfaceDefinition def = load();
        InterfaceDefinition.DataRecordSpec spec = def.findRecord("MTL_STOCKLEVEL").orElseThrow();

        String[] vals = baseRowValues();
        vals[8] = null; // field 10 (item_segment1) is required → null fails
        ParsedRecord rec = row("MTL_STOCKLEVEL", 55, 9, vals);

        assertThatThrownBy(() -> new FieldMapper(def).map(spec, rec, headerCaptured(def)))
            .isInstanceOf(FieldMappingException.class)
            .hasMessageContaining("required column 'item_segment1'");
    }

    @Test
    @DisplayName("TC-214: primary_quantity must be numeric — non-numeric raises FieldMappingException")
    void primaryQuantityNotNumeric_raises() {
        // primary_quantity has no max_length in the catalog, but the type modifier
        // gives us a clean way to exercise the typed-coercion error path.
        InterfaceDefinition def = load();
        InterfaceDefinition.DataRecordSpec spec = def.findRecord("MTL_STOCKLEVEL").orElseThrow();

        String[] vals = baseRowValues();
        vals[30] = "not-a-number"; // field 32
        ParsedRecord rec = row("MTL_STOCKLEVEL", 55, 11, vals);

        assertThatThrownBy(() -> new FieldMapper(def).map(spec, rec, headerCaptured(def)))
            .isInstanceOf(FieldMappingException.class)
            .hasMessageContaining("is not a valid NUMERIC");
    }

    @Test
    @DisplayName("TC-215: material_location outside allowed_values [ONHAND, RECEIVING] raises FieldMappingException")
    void allowedValuesViolation_raises() {
        InterfaceDefinition def = load();
        InterfaceDefinition.DataRecordSpec spec = def.findRecord("MTL_STOCKLEVEL").orElseThrow();

        String[] vals = baseRowValues();
        vals[36] = "MYSTERY"; // field 38 = material_location
        ParsedRecord rec = row("MTL_STOCKLEVEL", 55, 12, vals);

        assertThatThrownBy(() -> new FieldMapper(def).map(spec, rec, headerCaptured(def)))
            .isInstanceOf(FieldMappingException.class)
            .hasMessageContaining("not in allowed_values");
    }

    @Test
    @DisplayName("TC-216: record with wrong field count raises FieldMappingException")
    void wrongFieldCount_raises() {
        InterfaceDefinition def = load();
        InterfaceDefinition.DataRecordSpec spec = def.findRecord("MTL_STOCKLEVEL").orElseThrow();

        ParsedRecord shortRow = row("MTL_STOCKLEVEL", 30, 13, baseRowValues());

        assertThatThrownBy(() -> new FieldMapper(def).map(spec, shortRow, headerCaptured(def)))
            .isInstanceOf(FieldMappingException.class)
            .hasMessageContaining("expected 55 fields, got 30");
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /** Required fields filled with valid sample values; everything else null. */
    private static String[] baseRowValues() {
        String[] v = new String[54];
        v[8]  = "459473_101";   // field 10 (item_segment1) — required
        v[30] = "100";          // field 32 (primary_quantity) — required
        v[31] = "EA";           // field 33 (primary_uom)
        v[36] = "ONHAND";       // field 38 (material_location) — allowed_values
        return v;
    }

    private static EnvelopeContext headerCaptured(InterfaceDefinition def) {
        EnvelopeContext ctx = new EnvelopeContext();
        InterfaceDefinition.EnvelopeSpec hf = def.findEnvelope("HEADER_FILE").orElseThrow();
        String[] envFields = new String[19];
        envFields[1] = "IFOPEUR";
        envFields[4] = "20260603160800";
        envFields[5] = "batch.cfo";
        ParsedRecord envRow = row("HEADER_FILE", 20, 1, envFields);
        ctx.capture(hf, envRow);
        return ctx;
    }
}
