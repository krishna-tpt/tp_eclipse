package org.michelin.filemanager.ingest;

import org.michelin.filemanager.catalog.InterfaceDefinition;
import org.michelin.filemanager.catalog.InterfaceDefinition.EnvelopeSpec;
import org.michelin.filemanager.catalog.InterfaceDefinition.ValidationSpec;
import org.michelin.filemanager.parser.ParsedRecord;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Accumulates row counts as records stream, then validates declared envelope
 * footer counts at end-of-file.
 *
 * <p>Per the catalog YAML:
 * <ul>
 *   <li>{@code FOOTER_BLOCK.data_row_count} is the expected number of data rows
 *       (records belonging to a {@code records:} entry, not envelopes).</li>
 *   <li>{@code FOOTER_FILE.total_row_count} is the expected number of NON-footer-file
 *       rows in the entire file (everything except the FOOTER_FILE line itself).</li>
 * </ul>
 *
 * <p>The Michelin format keeps these exact semantics: {@code FOOTER_FILE}
 * counts only what came before it, not itself.
 *
 * <p>The validator does not throw during streaming. It collects expected counts
 * from each envelope row and tallies actual counts. Call {@link #validate()}
 * once at EOF; if any tally mismatches, it throws — which the pipeline catches
 * as "file rejected" per the locked bad-row policy.
 */
public final class EnvelopeValidator {

    public static final String DATA_COUNT_KEY  = "data_row_count";
    public static final String TOTAL_COUNT_KEY = "total_row_count";
    public static final String FOOTER_FILE_TAG = "FOOTER_FILE";

    private final InterfaceDefinition definition;

    /** Expected counts, captured from envelope rows when they arrive. */
    private final Map<String, Map<String, Long>> expected = new LinkedHashMap<>();

    /** Running tallies for actual stream observation. */
    private long dataRowsSeen = 0;
    private long nonFooterFileRowsSeen = 0;

    public EnvelopeValidator(InterfaceDefinition definition) {
        this.definition = definition;
    }

    /**
     * Observe a parsed record. Routes envelope rows to capture their declared
     * expectations; counts data rows for later comparison.
     *
     * @return true if the record was an envelope (caller may want to capture metadata too),
     *         false if it was a data record (caller should map and write it).
     */
    public RecordKind observe(ParsedRecord record) {
        if (!FOOTER_FILE_TAG.equals(record.tag())) {
            nonFooterFileRowsSeen++;
        }
        var envOpt = definition.findEnvelope(record.tag());
        if (envOpt.isPresent()) {
            EnvelopeSpec env = envOpt.get();
            captureExpected(env, record);
            return RecordKind.ENVELOPE;
        }
        var recOpt = definition.findRecord(record.tag());
        if (recOpt.isPresent()) {
            dataRowsSeen++;
            return RecordKind.DATA;
        }
        return RecordKind.UNKNOWN;
    }

    /**
     * Throws {@link EnvelopeValidationException} if any declared footer count
     * mismatches what was observed. Call exactly once after EOF.
     */
    public void validate() {
        Map<String, Long> footerBlock = expected.getOrDefault("FOOTER_BLOCK", Map.of());
        Long expectedData = footerBlock.get(DATA_COUNT_KEY);
        if (expectedData != null && expectedData != dataRowsSeen) {
            throw new EnvelopeValidationException(
                "FOOTER_BLOCK.data_row_count expected " + expectedData +
                " but observed " + dataRowsSeen + " data rows");
        }

        Map<String, Long> footerFile = expected.getOrDefault(FOOTER_FILE_TAG, Map.of());
        Long expectedTotal = footerFile.get(TOTAL_COUNT_KEY);
        if (expectedTotal != null && expectedTotal != nonFooterFileRowsSeen) {
            throw new EnvelopeValidationException(
                "FOOTER_FILE.total_row_count expected " + expectedTotal +
                " but observed " + nonFooterFileRowsSeen + " non-footer-file rows");
        }
    }

    public long dataRowsSeen() { return dataRowsSeen; }

    private void captureExpected(EnvelopeSpec env, ParsedRecord record) {
        if (env.validates().isEmpty()) return;
        Map<String, Long> capt = new HashMap<>();
        for (Map.Entry<String, ValidationSpec> e : env.validates().entrySet()) {
            String raw = record.field(e.getValue().field());
            if (raw == null) {
                throw new EnvelopeValidationException(
                    "envelope '" + env.tag() + "' validates '" + e.getKey() +
                    "' at field " + e.getValue().field() + " is empty");
            }
            try {
                capt.put(e.getKey(), Long.parseLong(raw.trim()));
            } catch (NumberFormatException nfe) {
                throw new EnvelopeValidationException(
                    "envelope '" + env.tag() + "' validates '" + e.getKey() +
                    "' is not a valid count: " + raw, nfe);
            }
        }
        expected.put(env.tag(), capt);
    }

    public enum RecordKind { ENVELOPE, DATA, UNKNOWN }
}
