package com.expensemanager.dao;

import com.expensemanager.model.Receipt;
import com.expensemanager.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiptDAO {

	private final DBConnection db = DBConnection.getInstance();
	private final AuditLogDAO auditDAO = new AuditLogDAO();
	
	public void insert(Receipt r) throws SQLException {
		String sql = """
				INSERT INTO transaction_receipts
				  (transaction_id, file_name, file_type, file_data, file_size)
				VALUES (?, ?, ?, ?, ?)
				""";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, r.getTransactionId());
			ps.setString(2, r.getFileName());
			ps.setString(3, r.getFileType());
			ps.setBytes(4, r.getFileData());
			ps.setInt(5, r.getFileSize());
			ps.executeUpdate();
			auditDAO.logReceiptUpload(r.getTransactionId(), "user");
		} finally {
			db.releaseConnection(conn);
		}
	}

	public List<Receipt> findByTransactionId(int txnId) throws SQLException {
		String sql = """
				SELECT id, transaction_id, file_name, file_type, file_data, file_size, uploaded_at
				FROM transaction_receipts
				WHERE transaction_id = ?
				ORDER BY uploaded_at DESC
				""";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, txnId);
			ResultSet rs = ps.executeQuery();
			List<Receipt> list = new ArrayList<>();
			while (rs.next())
				list.add(mapRow(rs));
			return list;
		} finally {
			db.releaseConnection(conn);
		}
	}

	/** Metadata only (no file_data) for listing */
	public List<Receipt> findMetaByTransactionId(int txnId) throws SQLException {
		String sql = """
				SELECT id, transaction_id, file_name, file_type, file_size, uploaded_at
				FROM transaction_receipts
				WHERE transaction_id = ?
				ORDER BY uploaded_at DESC
				""";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, txnId);
			ResultSet rs = ps.executeQuery();
			List<Receipt> list = new ArrayList<>();
			while (rs.next()) {
				Receipt r = new Receipt();
				r.setId(rs.getInt("id"));
				r.setTransactionId(rs.getInt("transaction_id"));
				r.setFileName(rs.getString("file_name"));
				r.setFileType(rs.getString("file_type"));
				r.setFileSize(rs.getInt("file_size"));
				Timestamp ts = rs.getTimestamp("uploaded_at");
				if (ts != null)
					r.setUploadedAt(ts.toLocalDateTime());
				list.add(r);
			}
			return list;
		} finally {
			db.releaseConnection(conn);
		}
	}

	public Receipt findById(int id) throws SQLException {
		String sql = """
				SELECT id, transaction_id, file_name, file_type, file_data, file_size, uploaded_at
				FROM transaction_receipts WHERE id = ?
				""";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? mapRow(rs) : null;
		} finally {
			db.releaseConnection(conn);
		}
	}

	public void delete(int id) throws SQLException {
		String sql = "DELETE FROM transaction_receipts WHERE id = ?";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		} finally {
			db.releaseConnection(conn);
		}
	}

	private Receipt mapRow(ResultSet rs) throws SQLException {
		Receipt r = new Receipt();
		r.setId(rs.getInt("id"));
		r.setTransactionId(rs.getInt("transaction_id"));
		r.setFileName(rs.getString("file_name"));
		r.setFileType(rs.getString("file_type"));
		r.setFileData(rs.getBytes("file_data"));
		r.setFileSize(rs.getInt("file_size"));
		Timestamp ts = rs.getTimestamp("uploaded_at");
		if (ts != null)
			r.setUploadedAt(ts.toLocalDateTime());
		return r;
	}
}