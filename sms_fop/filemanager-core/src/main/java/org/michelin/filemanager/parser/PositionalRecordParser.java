package org.michelin.filemanager.parser;

import org.michelin.filemanager.catalog.InterfaceDefinition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Streaming line-by-line parser for positional, semicolon-delimited,
 * quote-wrapped files (the Michelin / IFOP / ERP-export family). Driven by the
 * {@link InterfaceDefinition.ParserRules} from the catalog — no record-type or
 * interface knowledge lives here.
 *
 * <p>Pipeline per non-blank line:
 * <ol>
 *   <li>Strip the UTF-8 BOM if present at the very start of the file</li>
 *   <li>Split on the configured delimiter, preserving trailing empty fields</li>
 *   <li>Per field: strip outer quote pair if present, then trim if configured</li>
 *   <li>If the field is now empty and {@code empty_is_null} is true, mark it null</li>
 *   <li>Emit {@link ParsedRecord} where field 1 is the tag</li>
 * </ol>
 *
 * <p>Embedded delimiters inside quoted fields are NOT supported — the source
 * format does not use them. If a future feed requires CSV-style escaping, a
 * second parser type should be introduced rather than complicating this one.
 */
public final class PositionalRecordParser {

    private static final char BOM = '﻿';

    private final char delimiter;
    private final char quote;
    private final Charset charset;
    private final boolean trim;
    private final boolean emptyIsNull;

    public PositionalRecordParser(InterfaceDefinition.ParserRules rules) {
        Objects.requireNonNull(rules, "rules");
        this.delimiter   = rules.delimiter().charAt(0);
        this.quote       = rules.quote().charAt(0);
        this.charset     = Charset.forName(rules.encoding());
        this.trim        = rules.trim();
        this.emptyIsNull = rules.emptyIsNull();
    }

    // ------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------

    /**
     * Parse a file and dispatch each record to {@code consumer}. Lines are read
     * one at a time; memory use is bounded by the longest line in the file.
     */
    public void parse(Path file, Consumer<ParsedRecord> consumer) throws IOException {
        try (BufferedReader r = Files.newBufferedReader(file, charset)) {
            parse(r, consumer);
        }
    }

    /** Eager helper for tests / small files. Use {@link #parse(Path, Consumer)} in production paths. */
    public List<ParsedRecord> parseAll(Path file) throws IOException {
        List<ParsedRecord> out = new ArrayList<>();
        parse(file, out::add);
        return out;
    }

    /**
     * Reads only the first non-blank line as a {@link ParsedRecord} and returns
     * immediately. Used by {@code VariantDetector} to inspect a file's envelope
     * row without scanning the whole file.
     */
    public Optional<ParsedRecord> firstRecord(Path file) throws IOException {
        try (BufferedReader r = Files.newBufferedReader(file, charset)) {
            long lineNumber = 0;
            boolean firstLine = true;
            String line;
            while ((line = r.readLine()) != null) {
                lineNumber++;
                if (firstLine) {
                    line = stripBom(line);
                    firstLine = false;
                }
                if (line.isBlank()) continue;
                return Optional.of(splitLine(line, lineNumber));
            }
        }
        return Optional.empty();
    }

    /** Reader-based variant. Caller owns the reader's lifecycle. */
    public void parse(Reader reader, Consumer<ParsedRecord> consumer) throws IOException {
        BufferedReader br = (reader instanceof BufferedReader b) ? b : new BufferedReader(reader);
        long lineNumber = 0;
        boolean firstLine = true;
        String line;
        while ((line = br.readLine()) != null) {
            lineNumber++;
            if (firstLine) {
                line = stripBom(line);
                firstLine = false;
            }
            if (line.isBlank()) continue;
            consumer.accept(splitLine(line, lineNumber));
        }
    }

    // ------------------------------------------------------------------
    // Internals
    // ------------------------------------------------------------------

    private ParsedRecord splitLine(String line, long lineNumber) {
        // String.split with limit = -1 preserves trailing empty fields,
        // which matters for envelope rows that legitimately end in ";".
        String[] raw = line.split(java.util.regex.Pattern.quote(String.valueOf(delimiter)), -1);
        List<String> processed = new ArrayList<>(raw.length);
        for (String s : raw) processed.add(processField(s));

        String tag = processed.get(0);
        if (tag == null || tag.isBlank()) {
            throw new PositionalParseException(
                "line " + lineNumber + ": record tag (field 1) is empty");
        }
        return new ParsedRecord(tag, processed, lineNumber, line);
    }

    private String processField(String raw) {
        String s = raw;
        if (s.length() >= 2 && s.charAt(0) == quote && s.charAt(s.length() - 1) == quote) {
            s = s.substring(1, s.length() - 1);
        }
        if (trim) s = s.trim();
        if (emptyIsNull && s.isEmpty()) return null;
        return s;
    }

    private static String stripBom(String line) {
        return (!line.isEmpty() && line.charAt(0) == BOM) ? line.substring(1) : line;
    }
}
