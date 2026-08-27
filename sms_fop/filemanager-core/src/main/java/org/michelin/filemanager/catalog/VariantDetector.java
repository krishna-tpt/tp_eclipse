package org.michelin.filemanager.catalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.michelin.filemanager.parser.ParsedRecord;
import org.michelin.filemanager.parser.PositionalRecordParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Two-stage variant detection:
 * <ol>
 *   <li><b>Filename match</b> — every catalog variant whose
 *       {@code matches.filename_pattern} matches the incoming filename becomes
 *       a candidate. Fast path.</li>
 *   <li><b>Header confirmation</b> — for candidates that declare
 *       {@code matches.header_confirms}, read the first non-blank line of the
 *       file using THAT candidate's parser rules, check the configured
 *       envelope tag and field value. Candidates without {@code header_confirms}
 *       are accepted on filename match alone.</li>
 * </ol>
 *
 * <p>Outcomes are explicit ({@link DetectionResult.Match}, {@link DetectionResult.None},
 * {@link DetectionResult.Ambiguous}). The detector never throws on a bad file —
 * it returns a {@code None} with a human-readable reason so the caller can
 * write to the notification outbox and move on.
 */
public final class VariantDetector {

    private static final Logger log = LoggerFactory.getLogger(VariantDetector.class);

    private final Catalog catalog;

    public VariantDetector(Catalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    /**
     * Identify which catalog variant applies to {@code file}.
     *
     * @param filename external filename used for pattern matching (typically the
     *        SFTP filename — may differ from the local temp path)
     * @param file actual readable path whose first envelope row will be checked
     *        when any candidate declares {@code header_confirms}
     */
    public DetectionResult detect(String filename, Path file) {
        Objects.requireNonNull(filename, "filename");
        Objects.requireNonNull(file,     "file");

        // Stage 1 — filename pattern
        List<InterfaceDefinition> filenameMatches = new ArrayList<>();
        for (InterfaceDefinition def : catalog.all()) {
            if (Pattern.compile(def.matches().filenamePattern()).matcher(filename).matches()) {
                filenameMatches.add(def);
            }
        }
        if (filenameMatches.isEmpty()) {
            return new DetectionResult.None(
                "no variant matches filename: " + filename);
        }

        // Stage 2 — header confirmation
        List<InterfaceDefinition> confirmed = new ArrayList<>();
        List<String> rejectionReasons = new ArrayList<>();
        for (InterfaceDefinition def : filenameMatches) {
            if (def.matches().headerConfirms() == null) {
                confirmed.add(def);
                continue;
            }
            try {
                if (headerMatches(def, file)) {
                    confirmed.add(def);
                } else {
                    rejectionReasons.add(def.interfaceName() + "/" + def.variant() +
                        " header_confirms failed");
                }
            } catch (IOException e) {
                log.warn("variant detector: cannot read first record of {} for {}/{}",
                    file, def.interfaceName(), def.variant(), e);
                rejectionReasons.add(def.interfaceName() + "/" + def.variant() +
                    " unreadable: " + e.getMessage());
            }
        }

        if (confirmed.isEmpty()) {
            return new DetectionResult.None(
                "filename matched " + filenameMatches.size() +
                " variant(s) but none confirmed: " + String.join("; ", rejectionReasons));
        }
        if (confirmed.size() > 1) {
            List<String> labels = confirmed.stream()
                .map(d -> d.interfaceName() + "/" + d.variant()).toList();
            return new DetectionResult.Ambiguous(confirmed,
                "multiple variants confirmed for " + filename + ": " + String.join(", ", labels));
        }
        return new DetectionResult.Match(confirmed.get(0));
    }

    // ------------------------------------------------------------------

    private boolean headerMatches(InterfaceDefinition def, Path file) throws IOException {
        InterfaceDefinition.HeaderConfirms hc = def.matches().headerConfirms();
        PositionalRecordParser parser = new PositionalRecordParser(def.parser());
        Optional<ParsedRecord> first = parser.firstRecord(file);
        if (first.isEmpty()) return false;
        ParsedRecord record = first.get();
        if (!record.tag().equals(hc.record())) return false;
        String actual = record.field(hc.field());
        return hc.equals().equals(actual);
    }
}
