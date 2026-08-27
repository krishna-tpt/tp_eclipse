package org.michelin.filemanager.health;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Aggregated health status reported by /actuator/health. UP iff every
 * registered component check returns UP. Field order in JSON output follows
 * insertion order in the map, so callers control the human-readable layout.
 *
 * The endpoint runs the registered checks per request — there is no caching.
 * For 1–3 cheap checks (alive flag, SELECT 1) that's the right trade-off:
 * always-fresh, no stale-cache surprises.
 */
public record HealthStatus(boolean up, Map<String, ComponentResult> components) {

    public HealthStatus {
        Objects.requireNonNull(components, "components");
        // LinkedHashMap copy + unmodifiable wrapper: preserves insertion order
        // (Map.copyOf returns an immutable but order-undefined map, which
        // would shuffle the JSON output keys).
        components = Collections.unmodifiableMap(new LinkedHashMap<>(components));
    }

    public record ComponentResult(boolean up, String reason) {

        // Renamed from up()/down() so they don't clash with the record's
        // auto-generated accessor named up().
        public static ComponentResult passing()             { return new ComponentResult(true,  null); }
        public static ComponentResult failing(String reason){ return new ComponentResult(false, reason); }

        public static ComponentResult fromSupplier(Supplier<Boolean> aliveCheck) {
            try {
                return aliveCheck.get() ? passing() : failing("check returned false");
            } catch (RuntimeException e) {
                return failing(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
            }
        }
    }

    /**
     * Compose a status from named checks in the supplied order. Aggregates to
     * {@code up=true} only if every component is up.
     */
    public static HealthStatus from(Map<String, ComponentResult> components) {
        boolean allUp = components.values().stream().allMatch(ComponentResult::up);
        return new HealthStatus(allUp, components);
    }

    /** JSON serialization. Hand-rolled — keeps the health endpoint dep-free. */
    public String toJson() {
        StringBuilder sb = new StringBuilder(160);
        sb.append("{\"status\":\"").append(up ? "UP" : "DOWN").append("\",\"components\":{");
        boolean first = true;
        for (Map.Entry<String, ComponentResult> e : components.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append('"').append(escape(e.getKey())).append("\":{")
              .append("\"status\":\"").append(e.getValue().up() ? "UP" : "DOWN").append('"');
            if (e.getValue().reason() != null) {
                sb.append(",\"reason\":\"").append(escape(e.getValue().reason())).append('"');
            }
            sb.append('}');
        }
        sb.append("}}");
        return sb.toString();
    }

    private static String escape(String s) {
        StringBuilder out = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"'  -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default   -> {
                    if (c < 0x20) out.append(String.format("\\u%04x", (int) c));
                    else          out.append(c);
                }
            }
        }
        return out.toString();
    }

    /** Empty-state helper for tests / construction order. */
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final Map<String, ComponentResult> components = new LinkedHashMap<>();

        public Builder check(String name, ComponentResult result) {
            components.put(name, result);
            return this;
        }

        public HealthStatus build() { return HealthStatus.from(components); }
    }
}
