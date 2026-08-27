package org.michelin.filemanager.mapper;

import org.michelin.filemanager.catalog.InterfaceDefinition;
import org.michelin.filemanager.parser.ParsedRecord;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mutable holder for values captured from envelope rows during one file's
 * traversal. Stored as raw strings (no type coercion at capture time);
 * downstream {@link FieldMapper} coerces using the column's own type/format
 * when an {@link InterfaceDefinition.EnvelopeRule} resolves a value here.
 *
 * <p>Lookup is keyed by ({@code envelopeTag}, {@code captureName}). Returns
 * {@code null} when nothing has been captured for that pair — the field mapper
 * treats {@code null} as "use default / fail-if-required" exactly like a null
 * data field.
 */
public final class EnvelopeContext {

    private final Map<String, Map<String, String>> store = new HashMap<>();

    /**
     * Record the captures declared by {@code spec} from a parsed envelope row.
     * Subsequent {@code get} calls with this envelope tag can retrieve them.
     */
    public void capture(InterfaceDefinition.EnvelopeSpec spec, ParsedRecord record) {
        Map<String, String> values = store.computeIfAbsent(spec.tag(), k -> new LinkedHashMap<>());
        for (Map.Entry<String, InterfaceDefinition.CaptureSpec> e : spec.capture().entrySet()) {
            String raw = record.field(e.getValue().field());
            values.put(e.getKey(), raw);
        }
    }

    /** Returns the captured raw string for ({@code envelopeTag}, {@code captureName}), or {@code null}. */
    public String get(String envelopeTag, String captureName) {
        Map<String, String> values = store.get(envelopeTag);
        return values == null ? null : values.get(captureName);
    }

    /** True if {@code capture} has been called for {@code envelopeTag}. */
    public boolean hasCaptured(String envelopeTag) {
        return store.containsKey(envelopeTag);
    }
}
