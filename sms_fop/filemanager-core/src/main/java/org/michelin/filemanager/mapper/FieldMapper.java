package org.michelin.filemanager.mapper;

import org.michelin.filemanager.catalog.InterfaceDefinition;
import org.michelin.filemanager.catalog.InterfaceDefinition.ColumnRule;
import org.michelin.filemanager.catalog.InterfaceDefinition.DataRecordSpec;
import org.michelin.filemanager.catalog.InterfaceDefinition.EnvelopeRule;
import org.michelin.filemanager.catalog.InterfaceDefinition.FieldRule;
import org.michelin.filemanager.catalog.InterfaceDefinition.LiteralRule;
import org.michelin.filemanager.catalog.InterfaceDefinition.Modifiers;
import org.michelin.filemanager.parser.ParsedRecord;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Projects a {@link ParsedRecord} onto a target staging row using the column
 * rules from a {@link DataRecordSpec}. Per column:
 * <ol>
 *   <li>Resolve the raw string source — positional field, envelope capture, or literal.</li>
 *   <li>If raw is null, apply {@code default}; if still null and column is {@code required}, reject the file.</li>
 *   <li>Run string-level validators (max_length, regex, allowed_values) — any failure rejects the file.</li>
 *   <li>Coerce the validated string to the target type via {@link TypeCoercer}.</li>
 * </ol>
 *
 * <p>Per the locked "reject_file" bad-row policy, any rule violation throws
 * {@link FieldMappingException} so the caller can abort the whole file in a
 * single transaction.
 */
public final class FieldMapper {

    private final InterfaceDefinition definition;

    public FieldMapper(InterfaceDefinition definition) {
        this.definition = Objects.requireNonNull(definition, "definition");
    }

    public MappedRow map(DataRecordSpec spec, ParsedRecord record, EnvelopeContext context) {
        Objects.requireNonNull(spec,    "spec");
        Objects.requireNonNull(record,  "record");
        Objects.requireNonNull(context, "context");

        if (record.fieldCount() != spec.fields()) {
            throw new FieldMappingException(
                "line " + record.lineNumber() + ": record '" + spec.tag() +
                "' expected " + spec.fields() + " fields, got " + record.fieldCount());
        }

        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<String, ColumnRule> e : spec.columns().entrySet()) {
            String columnName = e.getKey();
            Object value = applyRule(columnName, e.getValue(), record, context);
            out.put(columnName, value);
        }
        return new MappedRow(spec.targetTable(), out, record.lineNumber());
    }

    // ------------------------------------------------------------------

    private Object applyRule(String columnName, ColumnRule rule,
                             ParsedRecord record, EnvelopeContext context) {
        String raw = resolveRaw(rule, record, context);
        Modifiers mods = rule.modifiers();

        if (raw == null && mods.defaultValue() != null) {
            raw = mods.defaultValue();
        }
        if (raw == null) {
            if (mods.required()) {
                throw new FieldMappingException(
                    "line " + record.lineNumber() + ": required column '" + columnName + "' is null");
            }
            return null;
        }

        validateString(columnName, raw, mods, record.lineNumber());

        return TypeCoercer.coerce(
            raw, mods.type(), mods.format(),
            definition.parser().decimalSeparator(),
            columnName, record.lineNumber());
    }

    private String resolveRaw(ColumnRule rule, ParsedRecord record, EnvelopeContext context) {
        if (rule instanceof FieldRule fr) {
            return record.field(fr.field());
        }
        if (rule instanceof EnvelopeRule er) {
            return context.get(er.envelopeTag(), er.capturedAs());
        }
        if (rule instanceof LiteralRule lr) {
            return lr.value();
        }
        throw new IllegalStateException("unhandled ColumnRule subtype: " + rule.getClass());
    }

    private static void validateString(String columnName, String raw, Modifiers mods, long lineNumber) {
        if (mods.maxLength() != null && raw.length() > mods.maxLength()) {
            throw new FieldMappingException(
                "line " + lineNumber + ": column '" + columnName + "' length " + raw.length() +
                " exceeds max_length " + mods.maxLength());
        }
        if (mods.regex() != null && !Pattern.compile(mods.regex()).matcher(raw).matches()) {
            throw new FieldMappingException(
                "line " + lineNumber + ": column '" + columnName + "' value '" + raw +
                "' does not match regex /" + mods.regex() + "/");
        }
        if (!mods.allowedValues().isEmpty() && !mods.allowedValues().contains(raw)) {
            throw new FieldMappingException(
                "line " + lineNumber + ": column '" + columnName + "' value '" + raw +
                "' not in allowed_values " + mods.allowedValues());
        }
    }
}
