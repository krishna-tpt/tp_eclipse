package org.michelin.filemanager.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-296..TC-299 — VariantDetector behavior.
 *
 * Exercises the two-stage match: filename regex first, then optional
 * header_confirms confirmation against the file's first envelope row.
 */
class VariantDetectorTest {

    private static final Path BATCH_CATALOG     = Paths.get("src/test/resources/fixtures/catalog/valid");
    private static final Path ACME_CATALOG      = Paths.get("src/test/resources/fixtures/catalog/no_header_confirms");

    private static final Path WITH_IFOPEUR      = Paths.get("src/test/resources/fixtures/variant_detector/with_ifopeur.txt");
    private static final Path WITHOUT_IFOPEUR   = Paths.get("src/test/resources/fixtures/variant_detector/without_ifopeur.txt");
    private static final Path ACME_SAMPLE       = Paths.get("src/test/resources/fixtures/variant_detector/acme_sample.txt");

    private static final String BATCH_FILENAME  = "MICH_INV_STOCKLEVEL_BATCH_408_20260603160800.cfo";
    private static final String ACME_FILENAME   = "acme_2026.txt";

    @Test
    @DisplayName("TC-296: no filename match → DetectionResult.None")
    void noFilenameMatch_returnsNone() {
        Catalog catalog = new CatalogLoader().loadFromPath(BATCH_CATALOG);
        VariantDetector detector = new VariantDetector(catalog);

        DetectionResult result = detector.detect("random_file.csv", WITH_IFOPEUR);

        assertThat(result).isInstanceOf(DetectionResult.None.class);
        assertThat(((DetectionResult.None) result).reason()).contains("random_file.csv");
    }

    @Test
    @DisplayName("TC-297: filename matches and no header_confirms declared → Match")
    void filenameMatchesNoHeaderConfirms_returnsMatch() {
        Catalog catalog = new CatalogLoader().loadFromPath(ACME_CATALOG);
        VariantDetector detector = new VariantDetector(catalog);

        DetectionResult result = detector.detect(ACME_FILENAME, ACME_SAMPLE);

        assertThat(result).isInstanceOf(DetectionResult.Match.class);
        InterfaceDefinition def = ((DetectionResult.Match) result).definition();
        assertThat(def.interfaceName()).isEqualTo("ACME_FEED");
        assertThat(def.variant()).isEqualTo("V1");
    }

    @Test
    @DisplayName("TC-298: filename matches and header_confirms passes → Match")
    void filenameMatchesHeaderConfirmsPasses_returnsMatch() {
        Catalog catalog = new CatalogLoader().loadFromPath(BATCH_CATALOG);
        VariantDetector detector = new VariantDetector(catalog);

        DetectionResult result = detector.detect(BATCH_FILENAME, WITH_IFOPEUR);

        assertThat(result).isInstanceOf(DetectionResult.Match.class);
        InterfaceDefinition def = ((DetectionResult.Match) result).definition();
        assertThat(def.interfaceName()).isEqualTo("MICH_INV_STOCKLEVEL");
        assertThat(def.variant()).isEqualTo("BATCH_408");
    }

    @Test
    @DisplayName("TC-299: filename matches but header_confirms field value mismatches → None")
    void filenameMatchesHeaderConfirmsFails_returnsNone() {
        Catalog catalog = new CatalogLoader().loadFromPath(BATCH_CATALOG);
        VariantDetector detector = new VariantDetector(catalog);

        DetectionResult result = detector.detect(BATCH_FILENAME, WITHOUT_IFOPEUR);

        assertThat(result).isInstanceOf(DetectionResult.None.class);
        String reason = ((DetectionResult.None) result).reason();
        assertThat(reason).contains("header_confirms failed");
    }
}
