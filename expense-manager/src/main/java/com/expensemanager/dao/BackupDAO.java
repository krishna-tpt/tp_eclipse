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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.model.BackupMetadata;
import com.expensemanager.model.BackupMetadata.BackupMode;
import com.expensemanager.model.BackupMetadata.BackupStatus;
import com.expensemanager.model.BackupMetadata.BackupType;
import com.expensemanager.servlet.BackupServlet;
import com.expensemanager.util.DBConnection;

public class BackupDAO {
	private static final Logger log = LoggerFactory.getLogger(BackupDAO.class);
	
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
		} catch (Exception e) {
			System.out.println("createTableIfNotExists Method : " + e.getMessage());
		}
	}

	public int insert(BackupMetadata m) throws SQLException {
		String sql = "INSERT INTO backup_history (file_name,file_path,file_size_bytes,backup_type,status,description,income_count,expense_count,created_at, backupmode) VALUES (?,?,?,?,?,?,?,?,?,?) RETURNING id";
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
			ps.setString(10, m.getMode().name());
			ResultSet rs = ps.executeQuery();
			log.error("Backupmetadata inserting...");
			return rs.next() ? rs.getInt(1) : -1;
		} catch (Exception e) {
			log.error("insert Method : {} ", e.getMessage());
			return 0;
		}
	}

	public void updateCompletion(int id, BackupStatus status, long size, int inc, int exp, String err)
			throws SQLException {
		String sql = "UPDATE backup_history SET status=?,file_size_bytes=?,income_count=?,expense_count=?,error_message=?,completed_at=NOW() WHERE id=?";
		try (Connection con = DBConnection.getInstance().getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, status.name());
			ps.setLong(2, size);
			ps.setInt(3, inc);
			ps.setInt(4, exp);
			ps.setString(5, err);
			ps.setInt(6, id);
			ps.executeUpdate();
		} catch (Exception e) {
			log.error("updateCompletion Method : {} ", e.getMessage());
		}
	}

	public void updateStatus(int id, BackupStatus status) throws SQLException {
		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement ps = con
						.prepareStatement("UPDATE backup_history SET status=?,completed_at=NOW() WHERE id=?")) {
			ps.setString(1, status.name());
			ps.setInt(2, id);
			ps.executeUpdate();
		} catch (Exception e) {
			log.error("updateStatus Method : {} ", e.getMessage());
		}
	}
	
	public void updateExternalID(int id, BackupStatus status, String externalID) throws SQLException {
		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement ps = con
						.prepareStatement("UPDATE backup_history SET status=?,external_ID=?, completed_at=NOW() WHERE id=?")) {
			ps.setString(1, status.name());
			ps.setString(2, externalID);
			ps.setInt(3, id);
			ps.executeUpdate();
		} catch (Exception e) {
			log.error("updateExternalID Method : {} ", e.getMessage());
		}
	}

	public List<BackupMetadata> getAll() throws SQLException {
		List<BackupMetadata> list = new ArrayList<>();
		try (Connection con = DBConnection.getInstance().getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM backup_history ORDER BY created_at DESC")) {
			while (rs.next())
				list.add(mapRow(rs));
		} catch (Exception e) {
			log.error("getAll Method : {} ", e.getMessage());
		}
		return list;
	}

	public BackupMetadata getById(int id) throws SQLException {
		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT * FROM backup_history WHERE id=?")) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? mapRow(rs) : null;
		} catch (Exception e) {
			log.error("getById Method : {} ", e.getMessage());
			return null;
		}
	}

	public void delete(int id) throws SQLException {
		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM backup_history WHERE id=?")) {
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (Exception e) {
			log.error("delete Method : {} ", e.getMessage());
		}
	}

	public int countRows(String type) throws SQLException {
		String sql = "SELECT COUNT(*) FROM transactions WHERE type = ?::txn_type";
	    try (Connection con = DBConnection.getInstance().getConnection();
	         PreparedStatement pt = con.prepareStatement(sql)) {
	        pt.setString(1, type);
	        ResultSet rs = pt.executeQuery();
	        return rs.next() ? rs.getInt(1) : 0;
	    } catch (Exception e) {
	        log.error("countRows Method : {} ", e.getMessage());
	        return 0;
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
		m.setExternal_ID(rs.getString("external_ID"));
		try {
			m.setMode(BackupMode.valueOf(rs.getString("backupmode")));
		} catch (Exception e) {
			m.setMode(BackupMode.OFFLINE);
		}
//		m.setMode(rs.getString("backupmode"));
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
