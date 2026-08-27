package org.michelin.filemanager.pipeline;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.michelin.filemanager.db.Database;
import org.michelin.filemanager.exception.DataAccessException;
import org.michelin.filemanager.notifier.Severity;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Writes a row into notification_outbox via the SQL notify_outbox() helper
 * (which handles dedup by sliding window). Encapsulates the JDBC + JSON
 * conversion so callers think in domain terms.
 */
public class NotificationEmitter {

    private static final Logger log = LoggerFactory.getLogger(NotificationEmitter.class);
    private static final ObjectMapper JSON = new ObjectMapper();

    // Matches processed.notify_outbox(p_tenant_id TEXT, p_severity TEXT,
    // p_source TEXT, p_message TEXT, p_payload JSONB, p_dedup_key TEXT)
    // in the v6 customer schema. tenant_id is currently always NULL on the
    // Java side — webhook notifications aren't tenant-scoped.
    private static final String NOTIFY_SQL = """
            SELECT processed.notify_outbox(
                NULL::text,
                ?::text,
                ?::text,
                ?::text,
                ?::jsonb,
                ?::text)
            """;

    private final Database db;

    public NotificationEmitter(Database db) {
        this.db = db;
    }

    public void emit(Severity severity, String source, String message, JsonNode payload, String dedupKey) {
        String payloadJson;
        try {
            payloadJson = JSON.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.warn("could not serialise notification payload: {}", e.getMessage());
            payloadJson = "null";
        }
        try (PreparedStatement ps = db.connection().prepareStatement(NOTIFY_SQL)) {
            ps.setString(1, severity.wireName());
            ps.setString(2, source);
            ps.setString(3, message);
            ps.setString(4, payloadJson);
            ps.setString(5, dedupKey);
            ps.executeQuery();
        } catch (SQLException e) {
            // Notification path is best-effort — don't fail the pipeline on it.
            log.error("notification emit failed: {}", e.getMessage(), e);
        }
    }
}
