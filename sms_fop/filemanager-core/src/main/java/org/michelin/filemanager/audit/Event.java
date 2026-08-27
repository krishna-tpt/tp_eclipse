package org.michelin.filemanager.audit;

/**
 * Immutable event row destined for {@code audit.event_log}. Constructed via
 * {@link Builder} so call sites stay readable and only set the fields they
 * actually have. {@code eventType} and {@code source} are mandatory; everything
 * else is optional and serialized as NULL when not set.
 */
public record Event(
        String eventType,
        String severity,
        String source,
        String tenantId,
        String correlationId,
        String status,
        String refId,
        String errorCode,
        String errorMsg,
        Integer latencyMs,
        Integer rowsAffected,
        Long bytes,
        String payloadJson) {

    public Event {
        if (eventType == null || eventType.isBlank())
            throw new IllegalArgumentException("eventType is required");
        if (source == null || source.isBlank())
            throw new IllegalArgumentException("source is required");
        if (severity == null || severity.isBlank()) severity = "info";
    }

    public static Builder builder(String eventType, String source) {
        return new Builder().eventType(eventType).source(source);
    }

    public static final class Builder {
        private String eventType;
        private String severity = "info";
        private String source;
        private String tenantId;
        private String correlationId;
        private String status;
        private String refId;
        private String errorCode;
        private String errorMsg;
        private Integer latencyMs;
        private Integer rowsAffected;
        private Long bytes;
        private String payloadJson;

        public Builder eventType(String v)     { this.eventType = v;     return this; }
        public Builder severity(String v)      { this.severity = v;      return this; }
        public Builder source(String v)        { this.source = v;        return this; }
        public Builder tenantId(String v)      { this.tenantId = v;      return this; }
        public Builder correlationId(String v) { this.correlationId = v; return this; }
        public Builder status(String v)        { this.status = v;        return this; }
        public Builder refId(String v)         { this.refId = v;         return this; }
        public Builder errorCode(String v)     { this.errorCode = v;     return this; }
        public Builder errorMsg(String v)      { this.errorMsg = v;      return this; }
        public Builder latencyMs(Integer v)    { this.latencyMs = v;     return this; }
        public Builder rowsAffected(Integer v) { this.rowsAffected = v;  return this; }
        public Builder bytes(Long v)           { this.bytes = v;         return this; }
        public Builder payloadJson(String v)   { this.payloadJson = v;   return this; }

        public Event build() {
            return new Event(eventType, severity, source, tenantId, correlationId,
                    status, refId, errorCode, errorMsg,
                    latencyMs, rowsAffected, bytes, payloadJson);
        }
    }
}
