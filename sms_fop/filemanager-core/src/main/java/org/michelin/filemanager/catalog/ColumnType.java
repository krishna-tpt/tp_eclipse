package org.michelin.filemanager.catalog;

import java.util.Locale;

public enum ColumnType {
    STRING, INTEGER, NUMERIC, DATE, TIMESTAMP, TIMESTAMPTZ, BOOLEAN;

    public static ColumnType fromYaml(String raw) {
        if (raw == null || raw.isBlank()) return STRING;
        try {
            return ColumnType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new CatalogValidationException(
                "Unknown column type: '" + raw + "'. Expected one of: STRING, INTEGER, NUMERIC, DATE, TIMESTAMP, TIMESTAMPTZ, BOOLEAN");
        }
    }

    public boolean needsFormat() {
        return this == DATE || this == TIMESTAMP || this == TIMESTAMPTZ;
    }
}
