package org.michelin.filemanager.audit;

import org.michelin.filemanager.db.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Writes events to {@code audit.event_log}. Used for the small set of events
 * that have no equivalent in existing tables — file pickup, parse outcomes,
 * heartbeats. All other observable events live as views over inbox / outbox
 * tables (see {@code alter_04_audit_event_log.sql}).
 *
 * <p><b>Failure policy:</b> every write swallows SQL exceptions and logs a
 * WARN. Observability writes must never break the main flow. If the
 * {@code audit.event_log} table doesn't exist (e.g., a test DB without the
 * alter applied), {@code write} silently no-ops at the WARN level.
 */
public final class EventLogWriter {

    private static final Logger log = LoggerFactory.getLogger(EventLogWriter.class);

    private static final String INSERT_SQL =
            "INSERT INTO audit.event_log "
          + "  (event_type, severity, source, tenant_id, correlation_id, "
          + "   status, ref_id, error_code, error_msg, "
          + "   latency_ms, rows_affected, bytes, payload) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb)";

    private final Database db;

    public EventLogWriter(Database db) { this.db = db; }

    /** Writes one event. Returns true on success, false on swallowed failure. */
    public boolean write(Event e) {
        try (PreparedStatement ps = db.connection().prepareStatement(INSERT_SQL)) {
            ps.setString(1, e.eventType());
            ps.setString(2, e.severity());
            ps.setString(3, e.source());
            setStringOrNull(ps, 4, e.tenantId());
            setStringOrNull(ps, 5, e.correlationId());
            setStringOrNull(ps, 6, e.status());
            setStringOrNull(ps, 7, e.refId());
            setStringOrNull(ps, 8, e.errorCode());
            setStringOrNull(ps, 9, e.errorMsg());
            setIntOrNull(ps,    10, e.latencyMs());
            setIntOrNull(ps,    11, e.rowsAffected());
            setLongOrNull(ps,   12, e.bytes());
            ps.setString(13, e.payloadJson() == null ? "{}" : e.payloadJson());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            log.warn("audit event_log write failed for {}: {}", e.eventType(), ex.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------------
    // Convenience helpers — keep call sites short and consistent
    // ------------------------------------------------------------------

    public void filePicked(String fileName, long bytes, String correlationId, String source) {
        write(Event.builder("file.picked", source)
                .correlationId(correlationId)
                .refId(fileName)
                .bytes(bytes)
                .build());
    }

    public void fileParsed(String fileName, int rowsAffected, Integer latencyMs,
                           String correlationId, String source) {
        write(Event.builder("file.parsed", source)
                .correlationId(correlationId)
                .refId(fileName)
                .rowsAffected(rowsAffected)
                .latencyMs(latencyMs)
                .status("ok")
                .build());
    }

    public void fileFailed(String fileName, String errorCode, String errorMsg,
                           String correlationId, String source) {
        write(Event.builder("file.failed", source)
                .severity("error")
                .correlationId(correlationId)
                .refId(fileName)
                .errorCode(errorCode)
                .errorMsg(errorMsg)
                .status("failed")
                .build());
    }

    public void heartbeat() {
        write(Event.builder("daemon.heartbeat", "daemon").build());
    }

    // ------------------------------------------------------------------

    private static void setStringOrNull(PreparedStatement ps, int i, String v) throws SQLException {
        if (v == null) ps.setNull(i, Types.VARCHAR); else ps.setString(i, v);
    }
    private static void setIntOrNull(PreparedStatement ps, int i, Integer v) throws SQLException {
        if (v == null) ps.setNull(i, Types.INTEGER); else ps.setInt(i, v);
    }
    private static void setLongOrNull(PreparedStatement ps, int i, Long v) throws SQLException {
        if (v == null) ps.setNull(i, Types.BIGINT); else ps.setLong(i, v);
    }
}
