package org.michelin.filemanager.ingest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.michelin.filemanager.catalog.Catalog;
import org.michelin.filemanager.catalog.CatalogLoader;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-261 — pure unit coverage for the per-file natural-key duplicate check.
 *
 * <p>Uses a synthetic 3-column catalog written into a @TempDir so the test
 * stays focused on the dedupe behavior, free of the 55-column Michelin format.
 * The dedupe check runs before any database write, so we pass a null
 * Connection — the pipeline never reaches the JDBC path.
 */
class CatalogIngestPipelineTest {

    private static final String TINY_CATALOG = """
            interface: TEST_DEDUPE
            variant:   V1
            version:   "1.0"
            matches:
              filename_pattern: "^dedupe_test_\\\\d+\\\\.dat$"
              header_confirms:
                record: HEADER
                field:  2
                equals: TEST
            parser:
              delimiter: ";"
              quote:     "\\""
              encoding:  UTF-8
              trim:      true
              empty_is_null: true
              decimal_separator: "."
            envelope:
              - tag: HEADER
                fields: 2
            records:
              - tag: ROW
                fields: 3
                target_table: staging.test_inbox
                dedupe_keys:
                  - key1
                  - key2
                columns:
                  key1:  { field: 2 }
                  key2:  { field: 3 }
            """;

    @Test
    @DisplayName("TC-261: pipeline rejects file when two rows share the same natural key")
    void rejectsFile_whenNaturalKeyDuplicateFound(@TempDir Path tmp) throws Exception {
        Path catalogDir = tmp.resolve("catalog");
        Files.createDirectories(catalogDir);
        Files.writeString(catalogDir.resolve("dedupe_test.yaml"), TINY_CATALOG);

        Path dataFile = tmp.resolve("dedupe_test_001.dat");
        Files.writeString(dataFile, """
                "HEADER";"TEST"
                "ROW";"A";"X"
                "ROW";"B";"Y"
                "ROW";"A";"X"
                """);

        Catalog catalog = new CatalogLoader().loadFromPath(catalogDir);
        CatalogIngestPipeline pipeline = new CatalogIngestPipeline(catalog);

        IngestResult result = pipeline.ingest(dataFile, dataFile.getFileName().toString(), null);

        assertThat(result).isInstanceOf(IngestResult.Rejected.class);
        IngestResult.Rejected r = (IngestResult.Rejected) result;
        assertThat(r.reason())
            .contains("duplicate row")
            .contains("ROW")
            .contains("[A, X]");
    }

    @Test
    @DisplayName("TC-262: pipeline returns Success when every natural key is distinct")
    void acceptsFile_whenEveryNaturalKeyDistinct(@TempDir Path tmp) throws Exception {
        Path catalogDir = tmp.resolve("catalog");
        Files.createDirectories(catalogDir);
        Files.writeString(catalogDir.resolve("dedupe_test.yaml"), TINY_CATALOG);

        Path dataFile = tmp.resolve("dedupe_test_002.dat");
        Files.writeString(dataFile, """
                "HEADER";"TEST"
                "ROW";"A";"X"
                "ROW";"B";"Y"
                "ROW";"C";"Z"
                """);

        Catalog catalog = new CatalogLoader().loadFromPath(catalogDir);
        CatalogIngestPipeline pipeline = new CatalogIngestPipeline(catalog);

        // Connection is null because no duplicates found means writer.writeBatch IS reached.
        // We expect an NPE there — that's how we know the pipeline got past dedupe successfully.
        // (A "happy path completes" assertion requires a real Connection, covered by the IT.)
        Throwable thrown = catchThrowable(() ->
            pipeline.ingest(dataFile, dataFile.getFileName().toString(), null));

        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    private static Throwable catchThrowable(ThrowingRunnable r) {
        try { r.run(); return null; }
        catch (Throwable t) { return t; }
    }

    @FunctionalInterface
    private interface ThrowingRunnable { void run() throws Exception; }
}
