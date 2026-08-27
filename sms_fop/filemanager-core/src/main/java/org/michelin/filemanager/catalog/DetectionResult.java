package org.michelin.filemanager.catalog;

import java.util.List;
import java.util.Objects;

/**
 * Outcome of {@link VariantDetector#detect}. Sealed so callers can exhaustively
 * pattern-match on the three possible outcomes.
 */
public sealed interface DetectionResult {

    /** Exactly one variant identified — proceed with this definition. */
    record Match(InterfaceDefinition definition) implements DetectionResult {
        public Match { Objects.requireNonNull(definition, "definition"); }
    }

    /** No variant identified. Reason describes whether the filename didn't match,
     *  the header_confirms check failed, or the file was empty/unreadable. */
    record None(String reason) implements DetectionResult {
        public None { Objects.requireNonNull(reason, "reason"); }
    }

    /** Multiple variants confirmed — caller must reject the file. This indicates
     *  catalog design error (overlapping filename patterns + insufficient
     *  header_confirms to disambiguate). */
    record Ambiguous(List<InterfaceDefinition> candidates, String reason) implements DetectionResult {
        public Ambiguous {
            Objects.requireNonNull(reason, "reason");
            candidates = List.copyOf(Objects.requireNonNull(candidates, "candidates"));
        }
    }
}
