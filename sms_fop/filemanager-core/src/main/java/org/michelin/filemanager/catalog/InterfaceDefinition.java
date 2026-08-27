package org.michelin.filemanager.catalog;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Immutable spec for one (interface, variant) pair, hydrated from a catalog YAML.
 * Compact constructors enforce structural invariants so a malformed definition
 * cannot exist at runtime.
 */
public record InterfaceDefinition(
        String interfaceName,
        String variant,
        String version,
        Matches matches,
        ParserRules parser,
        List<EnvelopeSpec> envelope,
        List<DataRecordSpec> records) {

    public InterfaceDefinition {
        if (interfaceName == null || interfaceName.isBlank())
            throw new CatalogValidationException("interface is required");
        if (variant == null || variant.isBlank())
            throw new CatalogValidationException("variant is required");
        Objects.requireNonNull(matches, "matches");
        Objects.requireNonNull(parser,  "parser");
        envelope = List.copyOf(Objects.requireNonNull(envelope, "envelope"));
        records  = List.copyOf(Objects.requireNonNull(records,  "records"));
        if (envelope.isEmpty())
            throw new CatalogValidationException("envelope must declare at least one record type");
        if (records.isEmpty())
            throw new CatalogValidationException("records must declare at least one data record type");
        // no duplicate envelope tags
        long uniqueEnv = envelope.stream().map(EnvelopeSpec::tag).distinct().count();
        if (uniqueEnv != envelope.size())
            throw new CatalogValidationException("envelope contains duplicate tags");
        // no duplicate data record tags
        long uniqueRec = records.stream().map(DataRecordSpec::tag).distinct().count();
        if (uniqueRec != records.size())
            throw new CatalogValidationException("records contains duplicate tags");
        // envelope tags must not collide with data record tags
        var envTags = envelope.stream().map(EnvelopeSpec::tag).toList();
        for (DataRecordSpec r : records) {
            if (envTags.contains(r.tag()))
                throw new CatalogValidationException(
                    "record tag '" + r.tag() + "' also used as envelope tag — must be distinct");
        }
        // EnvelopeRule references must resolve
        for (DataRecordSpec r : records) {
            for (Map.Entry<String, ColumnRule> e : r.columns().entrySet()) {
                if (e.getValue() instanceof EnvelopeRule er) {
                    EnvelopeSpec target = envelope.stream()
                        .filter(env -> env.tag().equals(er.envelopeTag()))
                        .findFirst()
                        .orElseThrow(() -> new CatalogValidationException(
                            "column '" + e.getKey() + "' references unknown envelope tag '" + er.envelopeTag() + "'"));
                    if (!target.capture().containsKey(er.capturedAs()))
                        throw new CatalogValidationException(
                            "column '" + e.getKey() + "' references unknown capture '" + er.capturedAs() +
                            "' on envelope '" + er.envelopeTag() + "'");
                }
            }
        }
    }

    public Key key() { return new Key(interfaceName, variant); }

    /** Routing — locate a data record spec by its first-field tag. */
    public Optional<DataRecordSpec> findRecord(String tag) {
        for (DataRecordSpec r : records) {
            if (r.tag().equals(tag)) return Optional.of(r);
        }
        return Optional.empty();
    }

    /** Routing — locate an envelope spec by its first-field tag. */
    public Optional<EnvelopeSpec> findEnvelope(String tag) {
        for (EnvelopeSpec e : envelope) {
            if (e.tag().equals(tag)) return Optional.of(e);
        }
        return Optional.empty();
    }

    /** Composite key uniquely identifying a variant. */
    public record Key(String interfaceName, String variant) {
        public Key {
            Objects.requireNonNull(interfaceName, "interfaceName");
            Objects.requireNonNull(variant,       "variant");
        }
    }

    // ------------------------------------------------------------------
    // Matches — how a file gets routed to this definition
    // ------------------------------------------------------------------

    public record Matches(String filenamePattern, HeaderConfirms headerConfirms) {
        public Matches {
            if (filenamePattern == null || filenamePattern.isBlank())
                throw new CatalogValidationException("matches.filename_pattern is required");
            try { Pattern.compile(filenamePattern); }
            catch (PatternSyntaxException e) {
                throw new CatalogValidationException(
                    "matches.filename_pattern is not a valid regex: " + filenamePattern, e);
            }
        }
    }

    public record HeaderConfirms(String record, int field, String equals) {
        public HeaderConfirms {
            if (record == null || record.isBlank())
                throw new CatalogValidationException("matches.header_confirms.record is required");
            if (field < 1)
                throw new CatalogValidationException("matches.header_confirms.field must be >= 1");
            if (equals == null)
                throw new CatalogValidationException("matches.header_confirms.equals is required");
        }
    }

    // ------------------------------------------------------------------
    // Parser rules
    // ------------------------------------------------------------------

    public record ParserRules(
            String delimiter,
            String quote,
            String encoding,
            boolean trim,
            boolean emptyIsNull,
            String decimalSeparator) {

        public ParserRules {
            if (delimiter == null || delimiter.length() != 1)
                throw new CatalogValidationException("parser.delimiter must be exactly 1 character");
            if (quote == null || quote.length() != 1)
                throw new CatalogValidationException("parser.quote must be exactly 1 character");
            if (encoding == null || encoding.isBlank())
                throw new CatalogValidationException("parser.encoding is required");
            if (decimalSeparator == null || decimalSeparator.length() != 1)
                throw new CatalogValidationException("parser.decimal_separator must be exactly 1 character");
        }
    }

    // ------------------------------------------------------------------
    // Envelope record types
    // ------------------------------------------------------------------

    public record EnvelopeSpec(
            String tag,
            int fields,
            Map<String, CaptureSpec> capture,
            Map<String, ValidationSpec> validates) {

        public EnvelopeSpec {
            if (tag == null || tag.isBlank())
                throw new CatalogValidationException("envelope[].tag is required");
            if (fields < 1)
                throw new CatalogValidationException("envelope[].fields must be >= 1");
            capture   = (capture   == null) ? Map.of() : Map.copyOf(capture);
            validates = (validates == null) ? Map.of() : Map.copyOf(validates);
            for (Map.Entry<String, CaptureSpec> e : capture.entrySet()) {
                if (e.getValue().field() < 1 || e.getValue().field() > fields)
                    throw new CatalogValidationException(
                        "envelope '" + tag + "' capture '" + e.getKey() + "' field out of range");
            }
            for (Map.Entry<String, ValidationSpec> e : validates.entrySet()) {
                if (e.getValue().field() < 1 || e.getValue().field() > fields)
                    throw new CatalogValidationException(
                        "envelope '" + tag + "' validates '" + e.getKey() + "' field out of range");
            }
        }
    }

    public record CaptureSpec(int field, ColumnType type, String format) {
        public CaptureSpec {
            if (field < 1)
                throw new CatalogValidationException("capture.field must be >= 1");
            if (type == null) type = ColumnType.STRING;
            if (type.needsFormat() && (format == null || format.isBlank()))
                throw new CatalogValidationException("capture type " + type + " requires a format");
        }
    }

    public record ValidationSpec(int field) {
        public ValidationSpec {
            if (field < 1)
                throw new CatalogValidationException("validates.field must be >= 1");
        }
    }

    // ------------------------------------------------------------------
    // Data record type
    // ------------------------------------------------------------------

    public record DataRecordSpec(
            String tag,
            int fields,
            String targetTable,
            LinkedHashMap<String, ColumnRule> columns,
            List<String> dedupeKeys) {

        public DataRecordSpec {
            if (tag == null || tag.isBlank())
                throw new CatalogValidationException("records[].tag is required");
            if (fields < 1)
                throw new CatalogValidationException("records[].fields must be >= 1 for record '" + tag + "'");
            if (targetTable == null || targetTable.isBlank())
                throw new CatalogValidationException("records[].target_table is required for record '" + tag + "'");
            if (columns == null || columns.isEmpty())
                throw new CatalogValidationException("records[].columns is required for record '" + tag + "'");
            columns = new LinkedHashMap<>(columns); // defensive copy preserves order
            // No duplicate field numbers among FieldRule columns
            var seenFields = new java.util.HashSet<Integer>();
            for (Map.Entry<String, ColumnRule> e : columns.entrySet()) {
                ColumnRule rule = e.getValue();
                if (rule instanceof FieldRule fr) {
                    if (fr.field() < 1 || fr.field() > fields)
                        throw new CatalogValidationException(
                            "record '" + tag + "' column '" + e.getKey() + "' field " + fr.field() +
                            " out of range 1.." + fields);
                    if (!seenFields.add(fr.field()))
                        throw new CatalogValidationException(
                            "record '" + tag + "' has multiple columns mapped to field " + fr.field());
                }
            }
            // dedupe_keys is optional. When present, every named key must resolve to a
            // declared column so the pipeline can extract values at runtime.
            dedupeKeys = (dedupeKeys == null) ? List.of() : List.copyOf(dedupeKeys);
            for (String key : dedupeKeys) {
                if (key == null || key.isBlank())
                    throw new CatalogValidationException(
                        "record '" + tag + "' dedupe_keys contains a blank entry");
                if (!columns.containsKey(key))
                    throw new CatalogValidationException(
                        "record '" + tag + "' dedupe_keys references unknown column '" + key + "'");
            }
        }

        /** Convenience — true if this record opted into per-file row deduplication. */
        public boolean hasDedupeKeys() { return !dedupeKeys.isEmpty(); }
    }

    // ------------------------------------------------------------------
    // Column rules (sealed — three concrete sources)
    // ------------------------------------------------------------------

    public sealed interface ColumnRule permits FieldRule, EnvelopeRule, LiteralRule {
        Modifiers modifiers();
    }

    public record FieldRule(int field, Modifiers modifiers) implements ColumnRule {
        public FieldRule {
            if (field < 1) throw new CatalogValidationException("field must be >= 1");
            Objects.requireNonNull(modifiers, "modifiers");
        }
    }

    public record EnvelopeRule(String envelopeTag, String capturedAs, Modifiers modifiers) implements ColumnRule {
        public EnvelopeRule {
            if (envelopeTag == null || envelopeTag.isBlank())
                throw new CatalogValidationException("envelope_rule.envelope_tag is required");
            if (capturedAs == null || capturedAs.isBlank())
                throw new CatalogValidationException("envelope_rule.captured_as is required");
            Objects.requireNonNull(modifiers, "modifiers");
        }
    }

    public record LiteralRule(String value, Modifiers modifiers) implements ColumnRule {
        public LiteralRule {
            Objects.requireNonNull(value,     "literal value");
            Objects.requireNonNull(modifiers, "modifiers");
        }
    }

    // ------------------------------------------------------------------
    // Modifiers — type, validators, default, applied to any ColumnRule
    // ------------------------------------------------------------------

    public record Modifiers(
            ColumnType type,
            String format,
            boolean required,
            Integer maxLength,
            String regex,
            List<String> allowedValues,
            String defaultValue) {

        public static final Modifiers DEFAULTS =
            new Modifiers(ColumnType.STRING, null, false, null, null, List.of(), null);

        public Modifiers {
            if (type == null) type = ColumnType.STRING;
            if (type.needsFormat() && (format == null || format.isBlank()))
                throw new CatalogValidationException("column type " + type + " requires a format");
            if (maxLength != null && maxLength < 1)
                throw new CatalogValidationException("max_length must be >= 1");
            if (regex != null) {
                try { Pattern.compile(regex); }
                catch (PatternSyntaxException e) {
                    throw new CatalogValidationException("regex is not valid: " + regex, e);
                }
            }
            allowedValues = (allowedValues == null) ? List.of() : List.copyOf(allowedValues);
        }
    }
}
