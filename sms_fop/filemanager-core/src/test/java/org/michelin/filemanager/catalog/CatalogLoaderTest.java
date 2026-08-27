package org.michelin.filemanager.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TC-280..TC-285 — CatalogLoader behavior.
 *
 * The catalog drives all positional file parsing. These tests prove that
 * (a) a well-formed catalog YAML is loaded into typed records, and
 * (b) every malformed shape we care about is rejected at startup with a
 * named, actionable error.
 */
class CatalogLoaderTest {

    private static final Path VALID          = Paths.get("src/test/resources/fixtures/catalog/valid");
    private static final Path DUPLICATE_IV   = Paths.get("src/test/resources/fixtures/catalog/duplicate_iv");
    private static final Path DUPLICATE_FLD  = Paths.get("src/test/resources/fixtures/catalog/duplicate_field");
    private static final Path NO_TARGET      = Paths.get("src/test/resources/fixtures/catalog/no_target");
    private static final Path UNKNOWN_TYPE   = Paths.get("src/test/resources/fixtures/catalog/unknown_type");
    private static final Path BAD_DEDUPE_KEY = Paths.get("src/test/resources/fixtures/catalog/bad_dedupe_key");

    @Test
    @DisplayName("TC-280: loads a valid catalog, records carry expected fields")
    void loadsValidCatalog_recordsCarryExpectedFields() {
        Catalog catalog = new CatalogLoader().loadFromPath(VALID);

        assertThat(catalog.size()).isEqualTo(1);
        InterfaceDefinition def = catalog.find("MICH_INV_STOCKLEVEL", "BATCH_408").orElseThrow();

        assertThat(def.variant()).isEqualTo("BATCH_408");
        assertThat(def.parser().delimiter()).isEqualTo(";");
        assertThat(def.envelope()).extracting(InterfaceDefinition.EnvelopeSpec::tag)
            .containsExactly("HEADER_FILE", "HEADER_BLOCK", "FOOTER_BLOCK", "FOOTER_FILE");
        assertThat(def.records()).hasSize(1);

        InterfaceDefinition.DataRecordSpec rec = def.records().get(0);
        assertThat(rec.tag()).isEqualTo("MTL_STOCKLEVEL");
        assertThat(rec.fields()).isEqualTo(55);
        assertThat(rec.targetTable()).isEqualTo("staging.stocklevel_inbox");
        assertThat(rec.columns()).containsKey("item_segment1");
        assertThat(rec.columns().get("item_segment1"))
            .isInstanceOf(InterfaceDefinition.FieldRule.class);
        assertThat(((InterfaceDefinition.FieldRule) rec.columns().get("item_segment1")).field())
            .isEqualTo(10);
        assertThat(rec.columns().get("tenant_code"))
            .isInstanceOf(InterfaceDefinition.EnvelopeRule.class);
        assertThat(rec.columns().get("source_marker"))
            .isInstanceOf(InterfaceDefinition.LiteralRule.class);
    }

    @Test
    @DisplayName("TC-281: rejects catalog with duplicate (interface, variant)")
    void rejectsDuplicateInterfaceVariant() {
        assertThatThrownBy(() -> new CatalogLoader().loadFromPath(DUPLICATE_IV))
            .isInstanceOf(CatalogValidationException.class)
            .hasMessageContaining("Duplicate")
            .hasMessageContaining("BATCH_408");
    }

    @Test
    @DisplayName("TC-282: rejects record with two columns mapped to the same field number")
    void rejectsDuplicateFieldNumberWithinRecord() {
        assertThatThrownBy(() -> new CatalogLoader().loadFromPath(DUPLICATE_FLD))
            .isInstanceOf(CatalogValidationException.class)
            .hasMessageContaining("multiple columns mapped to field 1");
    }

    @Test
    @DisplayName("TC-283: rejects record missing target_table")
    void rejectsMissingTargetTable() {
        assertThatThrownBy(() -> new CatalogLoader().loadFromPath(NO_TARGET))
            .isInstanceOf(CatalogValidationException.class)
            .hasMessageContaining("target_table");
    }

    @Test
    @DisplayName("TC-284: rejects unknown column type")
    void rejectsUnknownColumnType() {
        assertThatThrownBy(() -> new CatalogLoader().loadFromPath(UNKNOWN_TYPE))
            .isInstanceOf(CatalogValidationException.class)
            .hasMessageContaining("Unknown column type");
    }

    @Test
    @DisplayName("TC-285: catalog.find returns empty for an unknown key")
    void findReturnsEmptyForUnknownKey() {
        Catalog catalog = new CatalogLoader().loadFromPath(VALID);
        assertThat(catalog.find("NOPE", "X")).isEmpty();
    }

    @Test
    @DisplayName("TC-286: valid catalog exposes dedupe_keys in order, marks hasDedupeKeys")
    void validCatalogExposesDedupeKeys() {
        Catalog catalog = new CatalogLoader().loadFromPath(VALID);
        InterfaceDefinition.DataRecordSpec rec = catalog.find("MICH_INV_STOCKLEVEL", "BATCH_408")
            .orElseThrow().records().get(0);

        assertThat(rec.hasDedupeKeys()).isTrue();
        assertThat(rec.dedupeKeys()).containsExactly(
            "organization_code",
            "item_segment1",
            "warehouse",
            "subinventory",
            "locator",
            "lot",
            "material_location");
    }

    @Test
    @DisplayName("TC-287: rejects catalog whose dedupe_keys references an unknown column")
    void rejectsDedupeKeysReferencingUnknownColumn() {
        assertThatThrownBy(() -> new CatalogLoader().loadFromPath(BAD_DEDUPE_KEY))
            .isInstanceOf(CatalogValidationException.class)
            .hasMessageContaining("dedupe_keys")
            .hasMessageContaining("i_do_not_exist");
    }
}
