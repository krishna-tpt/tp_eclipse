package org.michelin.filemanager.mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * One row ready for insertion into a staging table. {@link #values} preserves
 * the column order declared in the catalog YAML so callers can build INSERT or
 * COPY statements deterministically.
 *
 * <p>Values are Java objects of the type declared by the column's
 * {@code type:} modifier — String, Long, BigDecimal, LocalDate, LocalDateTime,
 * OffsetDateTime, or Boolean. A value of {@code null} means the source was
 * blank and the column was not required.
 */
public record MappedRow(String targetTable, Map<String, Object> values, long lineNumber) {

    public MappedRow {
        Objects.requireNonNull(targetTable, "targetTable");
        Objects.requireNonNull(values,      "values");
        values = new LinkedHashMap<>(values);
    }
}
