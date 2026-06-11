package com.expensemanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.expensemanager.model.BackupMetadata;
import com.expensemanager.model.BackupMetadata.BackupStatus;
import com.expensemanager.model.BackupMetadata.BackupType;
import com.expensemanager.util.DBConnection;

public class BackupDAO {
	public void createTableIfNotExists() throws SQLException {
		String ddl = "CREATE TABLE IF NOT EXISTS backup_history ("
				+ "id SERIAL PRIMARY KEY, file_name VARCHAR(200) NOT NULL, file_path TEXT NOT NULL,"
				+ "file_size_bytes BIGINT DEFAULT 0, backup_type VARCHAR(30) NOT NULL DEFAULT 'MANUAL',"
				+ "status VARCHAR(30) NOT NULL DEFAULT 'PENDING', description TEXT, error_message TEXT,"
				+ "income_count INT DEFAULT 0, expense_count INT DEFAULT 0,"
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, completed_at TIMESTAMP);"
				+ "CREATE INDEX IF NOT EXISTS idx_backup_created ON backup_history(created_at DESC);";
		try (Connection con = DBConnection.getInstance().getConnection(); Statement st = con.createStatement()) {
			st.execute(ddl);
		}
	}

	public int insert(BackupMetadata m) throws SQLException {
		String sql = "INSERT INTO backup_history (file_name,file_path,file_size_bytes,backup_type,status,description,income_count,expense_count,created_at) VALUES (?,?,?,?,?,?,?,?,?) RETURNING id";
		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, m.getFileName());
			ps.setString(2, m.getFilePath());
			ps.setLong(3, m.getFileSizeBytes());
			ps.setString(4, m.getBackupType().name());
			ps.setString(5, m.getStatus().name());
			ps.setString(6, m.getDescription());
			ps.setInt(7, m.getIncomeCount());
			ps.setInt(8, m.getExpenseCount());
			ps.setTimestamp(9, Timestamp.valueOf(m.getCreatedAt() != null ? m.getCreatedAt() : LocalDateTime.now()));
			ResultSet rs = ps.executeQuery();
			return rs.next() ? rs.getInt(1) : -1;
		}
	}

	public void updateCompletion(int id, BackupStatus status, long size, int inc, int exp, String err)
			throws SQLException {
		String sql = "UPDATE backup_history SET status=?,file_size_bytes=?,income_count=?,expense_count=?,error_message=?,completed_at=NOW() WHERE id=?";
		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, status.name());
			ps.setLong(2, size);
			ps.setInt(3, inc);
			ps.setInt(4, exp);
			ps.setString(5, err);
			ps.setInt(6, id);
			ps.executeUpdate();
		}
	}

	public void updateStatus(int id, BackupStatus status) throws SQLException {
		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement ps = con
						.prepareStatement("UPDATE backup_history SET status=?,completed_at=NOW() WHERE id=?")) {
			ps.setString(1, status.name());
			ps.setInt(2, id);
			ps.executeUpdate();
		}
	}

	public List<BackupMetadata> getAll() throws SQLException {
		List<BackupMetadata> list = new ArrayList<>();
		try (Connection con = DBConnection.getInstance().getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM backup_history ORDER BY created_at DESC")) {
			while (rs.next())
				list.add(mapRow(rs));
		}
		return list;
	}

	public BackupMetadata getById(int id) throws SQLException {
		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT * FROM backup_history WHERE id=?")) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? mapRow(rs) : null;
		}
	}

	public void delete(int id) throws SQLException {
		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM backup_history WHERE id=?")) {
			ps.setInt(1, id);
			ps.executeUpdate();
		}
	}

	public int countRows(String table) throws SQLException {
		try (Connection con = DBConnection.getInstance().getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
			return rs.next() ? rs.getInt(1) : 0;
		}
	}

	private BackupMetadata mapRow(ResultSet rs) throws SQLException {
		BackupMetadata m = new BackupMetadata();
		m.setId(rs.getInt("id"));
		m.setFileName(rs.getString("file_name"));
		m.setFilePath(rs.getString("file_path"));
		m.setFileSizeBytes(rs.getLong("file_size_bytes"));
		m.setDescription(rs.getString("description"));
		m.setErrorMessage(rs.getString("error_message"));
		m.setIncomeCount(rs.getInt("income_count"));
		m.setExpenseCount(rs.getInt("expense_count"));
		try {
			m.setBackupType(BackupType.valueOf(rs.getString("backup_type")));
		} catch (Exception e) {
			m.setBackupType(BackupType.MANUAL);
		}
		try {
			m.setStatus(BackupStatus.valueOf(rs.getString("status")));
		} catch (Exception e) {
			m.setStatus(BackupStatus.PENDING);
		}
		Timestamp ca = rs.getTimestamp("created_at");
		if (ca != null)
			m.setCreatedAt(ca.toLocalDateTime());
		Timestamp co = rs.getTimestamp("completed_at");
		if (co != null)
			m.setCompletedAt(co.toLocalDateTime());
		return m;
	}
}
