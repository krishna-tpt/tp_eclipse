package org.michelin.filemanager.ingest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Outcome of a single-file ingest run. Sealed so callers can pattern-match.
 *
 * <p>{@code Success} carries per-target-table row counts so the loader can log
 * "wrote N to staging.X, M to staging.Y" without recomputing.
 * <p>{@code Rejected} carries the reason for the rejection (variant
 * detection failed, envelope/footer mismatch, validation failure on a row).
 */
public sealed interface IngestResult {

    String filename();

    record Success(String filename, String interfaceName, String variant,
                   Map<String, Integer> rowCountsByTable) implements IngestResult {
        public Success {
            Objects.requireNonNull(filename, "filename");
            Objects.requireNonNull(interfaceName, "interfaceName");
            Objects.requireNonNull(variant, "variant");
            rowCountsByTable = Map.copyOf(Objects.requireNonNull(rowCountsByTable, "rowCountsByTable"));
        }
        public int totalRowsWritten() {
            return rowCountsByTable.values().stream().mapToInt(Integer::intValue).sum();
        }
    }

    record Rejected(String filename, String reason) implements IngestResult {
        public Rejected {
            Objects.requireNonNull(filename, "filename");
            Objects.requireNonNull(reason,   "reason");
        }
    }

    static Success success(String filename, String interfaceName, String variant,
                           Map<String, Integer> counts) {
        return new Success(filename, interfaceName, variant, new LinkedHashMap<>(counts));
    }

    static Rejected rejected(String filename, String reason) {
        return new Rejected(filename, reason);
    }
}
