package org.michelin.filemanager.mapper;

import org.michelin.filemanager.catalog.ColumnType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Pure stateless string→typed coercion driven by the column's
 * {@link ColumnType} and (for date/time types) a format pattern.
 *
 * <p>Trim is the parser's job; by the time we get here the string is already
 * trimmed and non-blank. Empty/null handling happens in the caller.
 */
final class TypeCoercer {

    private TypeCoercer() {}

    static Object coerce(String value, ColumnType type, String format,
                         String decimalSeparator, String columnName, long lineNumber) {
        try {
            return switch (type) {
                case STRING      -> value;
                case INTEGER     -> Long.parseLong(value);
                case NUMERIC     -> new BigDecimal(normalizeDecimal(value, decimalSeparator));
                case DATE        -> LocalDate.parse(value, DateTimeFormatter.ofPattern(format));
                case TIMESTAMP   -> LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
                case TIMESTAMPTZ -> OffsetDateTime.parse(value, DateTimeFormatter.ofPattern(format));
                case BOOLEAN     -> parseBoolean(value, columnName, lineNumber);
            };
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new FieldMappingException(
                "line " + lineNumber + ": column '" + columnName + "' value '" + value +
                "' is not a valid " + type +
                (format == null ? "" : " for format '" + format + "'"), e);
        }
    }

    private static String normalizeDecimal(String value, String decimalSeparator) {
        if (decimalSeparator == null || ".".equals(decimalSeparator)) return value;
        return value.replace(decimalSeparator.charAt(0), '.');
    }

    private static Boolean parseBoolean(String value, String columnName, long lineNumber) {
        String s = value.toLowerCase(Locale.ROOT);
        if (s.equals("true")  || s.equals("t") || s.equals("yes") || s.equals("y") || s.equals("1")) return Boolean.TRUE;
        if (s.equals("false") || s.equals("f") || s.equals("no")  || s.equals("n") || s.equals("0")) return Boolean.FALSE;
        throw new FieldMappingException(
            "line " + lineNumber + ": column '" + columnName + "' value '" + value + "' is not a valid BOOLEAN");
    }
}
