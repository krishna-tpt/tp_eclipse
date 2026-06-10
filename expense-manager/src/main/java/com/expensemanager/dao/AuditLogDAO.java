package com.expensemanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.model.AuditLog;
import com.expensemanager.util.DBConnection;

public class AuditLogDAO {

    private final DBConnection db = DBConnection.getInstance();
    private static final Logger log = LoggerFactory.getLogger(AuditLogDAO.class);

    /** Log a CREATE event */
    public void logCreate(int transactionId, String changedBy) throws SQLException {
        insert(transactionId, "CREATE", changedBy, null, null, null, "Transaction created");
    }

    /** Log one field change */
    public void logUpdate(int transactionId, String changedBy,
                          String fieldName, String oldValue, String newValue) throws SQLException {
        insert(transactionId, "UPDATE", changedBy, fieldName, oldValue, newValue, null);
    }

    /** Log DELETE */
    public void logDelete(int transactionId, String changedBy) throws SQLException {
        insert(transactionId, "DELETE", changedBy, null, null, null, "Transaction deleted");
    }
    
    /** Log receipt upload */
    public void logReceiptUpload(int transactionId, String changedBy, 
                                  String fileName) throws SQLException {
        insert(transactionId, "RECEIPT_ADD", changedBy, 
               "receipt", null, fileName, "Receipt uploaded: " + fileName);
    }

    /** Log receipt delete */
    public void logReceiptDelete(int transactionId, String changedBy, 
                                  String fileName) throws SQLException {
        insert(transactionId, "RECEIPT_DEL", changedBy, 
               "receipt", fileName, null, "Receipt deleted: " + fileName);
    }

    private void insert(int transactionId, String action, String changedBy,
                        String fieldName, String oldValue, String newValue, String note)
            throws SQLException {
        String sql = """
            INSERT INTO transaction_audit_log
              (transaction_id, action, changed_by, field_name, old_value, new_value, note)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            ps.setString(2, action);
            ps.setString(3, changedBy != null ? changedBy : "user");
            ps.setString(4, fieldName);
            ps.setString(5, oldValue);
            ps.setString(6, newValue);
            ps.setString(7, note);
            ps.executeUpdate();
        } finally {
            db.releaseConnection(conn);
        }
    }

    /** All audit entries for a transaction */
    public List<AuditLog> findByTransactionId(int transactionId) throws SQLException {
        String sql = """
            SELECT id, transaction_id, action, changed_by, changed_at,
                   field_name, old_value, new_value, note
            FROM transaction_audit_log
            WHERE transaction_id = ?
            ORDER BY changed_at ASC
            """;
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            ResultSet rs = ps.executeQuery();
            List<AuditLog> list = new ArrayList<>();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } finally {
            db.releaseConnection(conn);
        }
    }

    /** Recent audit entries across all transactions (for admin view) */
    public List<AuditLog> findRecent(int limit) throws SQLException {
        String sql = """
            SELECT id, transaction_id, action, changed_by, changed_at,
                   field_name, old_value, new_value, note
            FROM transaction_audit_log
            ORDER BY changed_at DESC
            LIMIT ?
            """;
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            List<AuditLog> list = new ArrayList<>();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } finally {
            db.releaseConnection(conn);
        }
    }

    private AuditLog mapRow(ResultSet rs) throws SQLException {
        AuditLog a = new AuditLog();
        a.setId(rs.getInt("id"));
        a.setTransactionId(rs.getInt("transaction_id"));
        a.setAction(rs.getString("action"));
        a.setChangedBy(rs.getString("changed_by"));
        Timestamp ts = rs.getTimestamp("changed_at");
        if (ts != null) a.setChangedAt(ts.toLocalDateTime());
        a.setFieldName(rs.getString("field_name"));
        a.setOldValue(rs.getString("old_value"));
        a.setNewValue(rs.getString("new_value"));
        a.setNote(rs.getString("note"));
        return a;
    }
}
