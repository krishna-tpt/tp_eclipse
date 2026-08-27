package org.michelin.filemanager.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-275..TC-277 — record / envelope routing methods on InterfaceDefinition.
 * These are simple lookup helpers used by the ingest pipeline to decide
 * "is this row an envelope or a data record?" by its first-field tag.
 */
class InterfaceDefinitionRoutingTest {

    private static final Path VALID = Paths.get("src/test/resources/fixtures/catalog/valid");

    private static InterfaceDefinition load() {
        return new CatalogLoader().loadFromPath(VALID)
            .find("MICH_INV_STOCKLEVEL", "BATCH_408").orElseThrow();
    }

    @Test
    @DisplayName("TC-275: findRecord('MTL_STOCKLEVEL') returns the data record spec")
    void findRecordReturnsDataSpec() {
        InterfaceDefinition def = load();
        Optional<InterfaceDefinition.DataRecordSpec> rec = def.findRecord("MTL_STOCKLEVEL");

        assertThat(rec).isPresent();
        assertThat(rec.get().fields()).isEqualTo(55);
        assertThat(rec.get().targetTable()).isEqualTo("staging.stocklevel_inbox");
    }

    @Test
    @DisplayName("TC-276: findEnvelope('HEADER_FILE') returns the envelope spec")
    void findEnvelopeReturnsEnvelopeSpec() {
        InterfaceDefinition def = load();
        Optional<InterfaceDefinition.EnvelopeSpec> env = def.findEnvelope("HEADER_FILE");

        assertThat(env).isPresent();
        assertThat(env.get().fields()).isEqualTo(20);
        assertThat(env.get().capture()).containsKey("source_system");
    }

    @Test
    @DisplayName("TC-277: unknown tags return Optional.empty for both lookups")
    void unknownTagsReturnEmpty() {
        InterfaceDefinition def = load();
        assertThat(def.findRecord("UNKNOWN_X")).isEmpty();
        assertThat(def.findEnvelope("UNKNOWN_X")).isEmpty();
    }
}
