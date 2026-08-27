package org.michelin.filemanager.notifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import org.michelin.filemanager.exception.DataAccessException;
import org.michelin.filemanager.exception.NotificationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL gateway for notification_outbox. SELECT ... FOR UPDATE SKIP LOCKED so
 * two drainers don't pick the same rows.
 */
public class OutboxRepository {

    private static final ObjectMapper JSON = new ObjectMapper();

    // Columns + table name match the v6 customer schema
    // (processed.notification_outbox).  Customer PK is `outbox_id`; there is
    // no `updated_at` column — keep the UPDATE statements lean.
    private static final String SELECT_PENDING_SQL = """
            SELECT outbox_id, severity, source, message, payload, retry_count
              FROM processed.notification_outbox
             WHERE status IN ('pending', 'failed')
               AND retry_count < ?
             ORDER BY created_at
             LIMIT ?
             FOR UPDATE SKIP LOCKED
            """;

    private static final String UPDATE_DELIVERED_SQL = """
            UPDATE processed.notification_outbox
               SET status='delivered', delivered_at=?
             WHERE outbox_id=?
            """;

    private static final String UPDATE_NON_DELIVERED_SQL = """
            UPDATE processed.notification_outbox
               SET status=?, retry_count=retry_count+1, last_attempt_at=?
             WHERE outbox_id=?
            """;

    public record Row(long id, int retryCount, Notification notification) {}

    public List<Row> fetchPending(Connection c, int maxRetries, int batchSize) {
        List<Row> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(SELECT_PENDING_SQL)) {
            ps.setInt(1, maxRetries);
            ps.setInt(2, batchSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Row(
                            rs.getLong("outbox_id"),
                            rs.getInt("retry_count"),
                            new Notification(
                                    Severity.from(rs.getString("severity")),
                                    rs.getString("source"),
                                    rs.getString("message"),
                                    parseJsonOrNull(rs.getString("payload")))));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("fetchPending failed", e);
        }
        return out;
    }

    public void markDelivered(Connection c, long id, Timestamp at) {
        try (PreparedStatement ps = c.prepareStatement(UPDATE_DELIVERED_SQL)) {
            ps.setTimestamp(1, at);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("markDelivered failed", e);
        }
    }

    public void markNonDelivered(Connection c, long id, OutboxStatus newStatus, Timestamp at) {
        if (newStatus == OutboxStatus.DELIVERED) {
            throw new NotificationException("markNonDelivered called with DELIVERED status");
        }
        try (PreparedStatement ps = c.prepareStatement(UPDATE_NON_DELIVERED_SQL)) {
            ps.setString(1, newStatus.wireName());
            ps.setTimestamp(2, at);
            ps.setLong(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("markNonDelivered failed", e);
        }
    }

    public static Timestamp now() { return Timestamp.from(Instant.now()); }

    private JsonNode parseJsonOrNull(String json) {
        if (json == null || json.isBlank()) return NullNode.getInstance();
        try {
            return JSON.readTree(json);
        } catch (JsonProcessingException e) {
            return NullNode.getInstance();
        }
    }
}
