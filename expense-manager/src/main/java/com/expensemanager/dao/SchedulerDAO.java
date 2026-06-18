package com.expensemanager.dao;

import com.expensemanager.model.SchedulerConfig;
import com.expensemanager.model.SchedulerLog;
import com.expensemanager.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SchedulerDAO {

	private final DBConnection db = DBConnection.getInstance();

	// ── List all schedulers ────────────────────────────────────────
	public List<SchedulerConfig> findAll() throws SQLException {
		String sql = "SELECT * FROM schedulers ORDER BY id";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();
			List<SchedulerConfig> list = new ArrayList<>();
			while (rs.next())
				list.add(mapConfig(rs));
			return list;
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── Find by name ───────────────────────────────────────────────
	public SchedulerConfig findByName(String name) throws SQLException {
		String sql = "SELECT * FROM schedulers WHERE name=?";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? mapConfig(rs) : null;
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── Update scheduler config ────────────────────────────────────
	public void update(SchedulerConfig s) throws SQLException {
		String sql = """
				UPDATE schedulers SET
				    enabled      = ?,
				    repeat_type  = ?,
				    repeat_days  = ?,
				    run_hour     = ?,
				    run_minute   = ?,
				    next_run_at  = ?,
				    updated_at   = NOW()
				WHERE id = ?
				""";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setBoolean(1, s.isEnabled());
			ps.setString(2, s.getRepeatType());
			ps.setString(3, s.getRepeatDays());
			ps.setInt(4, s.getRunHour());
			ps.setInt(5, s.getRunMinute());
			if (s.getNextRunAt() != null)
				ps.setTimestamp(6, Timestamp.valueOf(s.getNextRunAt()));
			else
				ps.setNull(6, Types.TIMESTAMP);
			ps.setInt(7, s.getId());
			ps.executeUpdate();
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── Mark run started ───────────────────────────────────────────
	public int logStart(int schedulerId) throws SQLException {
		String sql = """
				INSERT INTO scheduler_log (scheduler_id, started_at, status)
				VALUES (?, NOW(), 'RUNNING') RETURNING id
				""";
		// Also update schedulers table
		String upd = "UPDATE schedulers SET last_run_status='RUNNING', updated_at=NOW() WHERE id=?";
		Connection conn = db.getConnection();
		try {
			try (PreparedStatement ps = conn.prepareStatement(upd)) {
				ps.setInt(1, schedulerId);
				ps.executeUpdate();
			}
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, schedulerId);
				ResultSet rs = ps.executeQuery();
				return rs.next() ? rs.getInt(1) : -1;
			}
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── Mark run finished ──────────────────────────────────────────
	public void logFinish(int logId, int schedulerId, String status, String message, int rowsSynced,
			LocalDateTime nextRunAt) throws SQLException {
		String sql = """
				UPDATE scheduler_log
				SET finished_at=NOW(), status=?, message=?, rows_synced=?
				WHERE id=?
				""";
		String upd = """
				UPDATE schedulers SET
				    last_run_at=NOW(), last_run_status=?, last_run_msg=?,
				    next_run_at=?, updated_at=NOW()
				WHERE id=?
				""";
		Connection conn = db.getConnection();
		try {
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, status);
				ps.setString(2, message);
				ps.setInt(3, rowsSynced);
				ps.setInt(4, logId);
				ps.executeUpdate();
			}
			try (PreparedStatement ps = conn.prepareStatement(upd)) {
				ps.setString(1, status);
				ps.setString(2, message);
				if (nextRunAt != null)
					ps.setTimestamp(3, Timestamp.valueOf(nextRunAt));
				else
					ps.setNull(3, Types.TIMESTAMP);
				ps.setInt(4, schedulerId);
				ps.executeUpdate();
			}
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── Recent logs ────────────────────────────────────────────────
	public List<SchedulerLog> recentLogs(int schedulerId, int limit) throws SQLException {
		String sql = """
				SELECT sl.*, s.display_name AS scheduler_name
				FROM scheduler_log sl
				JOIN schedulers s ON s.id = sl.scheduler_id
				WHERE sl.scheduler_id = ?
				ORDER BY sl.started_at DESC LIMIT ?
				""";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, schedulerId);
			ps.setInt(2, limit);
			ResultSet rs = ps.executeQuery();
			List<SchedulerLog> list = new ArrayList<>();
			while (rs.next())
				list.add(mapLog(rs));
			return list;
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── All recent logs (all schedulers) ───────────────────────────
	public List<SchedulerLog> allRecentLogs(int limit) throws SQLException {
		String sql = """
				SELECT sl.*, s.display_name AS scheduler_name
				FROM scheduler_log sl
				JOIN schedulers s ON s.id = sl.scheduler_id
				ORDER BY sl.started_at DESC LIMIT ?
				""";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, limit);
			ResultSet rs = ps.executeQuery();
			List<SchedulerLog> list = new ArrayList<>();
			while (rs.next())
				list.add(mapLog(rs));
			return list;
		} finally {
			db.releaseConnection(conn);
		}
	}

	private SchedulerConfig mapConfig(ResultSet rs) throws SQLException {
		SchedulerConfig s = new SchedulerConfig();
		s.setId(rs.getInt("id"));
		s.setName(rs.getString("name"));
		s.setDisplayName(rs.getString("display_name"));
		s.setEnabled(rs.getBoolean("enabled"));
		s.setRepeatType(rs.getString("repeat_type"));
		s.setRepeatDays(rs.getString("repeat_days"));
		s.setRunHour(rs.getInt("run_hour"));
		s.setRunMinute(rs.getInt("run_minute"));
		s.setLastRunStatus(rs.getString("last_run_status"));
		s.setLastRunMsg(rs.getString("last_run_msg"));
		Timestamp lra = rs.getTimestamp("last_run_at");
		if (lra != null)
			s.setLastRunAt(lra.toLocalDateTime());
		Timestamp nra = rs.getTimestamp("next_run_at");
		if (nra != null)
			s.setNextRunAt(nra.toLocalDateTime());
		Timestamp ca = rs.getTimestamp("created_at");
		if (ca != null)
			s.setCreatedAt(ca.toLocalDateTime());
		return s;
	}

	private SchedulerLog mapLog(ResultSet rs) throws SQLException {
		SchedulerLog l = new SchedulerLog();
		l.setId(rs.getInt("id"));
		l.setSchedulerId(rs.getInt("scheduler_id"));
		l.setSchedulerName(rs.getString("scheduler_name"));
		l.setStatus(rs.getString("status"));
		l.setMessage(rs.getString("message"));
		l.setRowsSynced(rs.getInt("rows_synced"));
		Timestamp sa = rs.getTimestamp("started_at");
		if (sa != null)
			l.setStartedAt(sa.toLocalDateTime());
		Timestamp fa = rs.getTimestamp("finished_at");
		if (fa != null)
			l.setFinishedAt(fa.toLocalDateTime());
		return l;
	}
}