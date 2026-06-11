package com.expensemanager.dao;

import com.expensemanager.model.CashBook;
import com.expensemanager.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CashBookDAO {

	private final DBConnection db = DBConnection.getInstance();

	public List<CashBook> findAll() throws SQLException {
		String sql = "SELECT id, name, description, created_at, is_active FROM cash_books ORDER BY created_at DESC";
		Connection conn = db.getConnection();
		try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			List<CashBook> list = new ArrayList<>();
			while (rs.next())
				list.add(mapRow(rs));
			return list;
		} finally {
			db.releaseConnection(conn);
		}
	}

	public CashBook findById(int id) throws SQLException {
		String sql = "SELECT id, name, description, created_at, is_active FROM cash_books WHERE id=?";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? mapRow(rs) : null;
		} finally {
			db.releaseConnection(conn);
		}
	}

	public int insert(String name, String description) throws SQLException {
		String sql = "INSERT INTO cash_books (name, description) VALUES (?, ?) RETURNING id";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, name.trim());
			ps.setString(2, description != null ? description.trim() : "");
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);
		} finally {
			db.releaseConnection(conn);
		}
	}

	public void update(int id, String name, String description) throws SQLException {
		String sql = "UPDATE cash_books SET name=?, description=? WHERE id=?";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, name.trim());
			ps.setString(2, description != null ? description.trim() : "");
			ps.setInt(3, id);
			ps.executeUpdate();
		} finally {
			db.releaseConnection(conn);
		}
	}

	public void delete(int id) throws SQLException {
		// Only delete if no transactions exist
		String sql = "DELETE FROM cash_books WHERE id=? AND NOT EXISTS (SELECT 1 FROM transactions WHERE book_id=?)";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.setInt(2, id);
			ps.executeUpdate();
		} finally {
			db.releaseConnection(conn);
		}
	}

	/** Summary stats per book */
	public Map<String, BigDecimal> getSummary(int bookId) throws SQLException {
		String sql = """
				SELECT
				  COALESCE(SUM(CASE WHEN type='INCOME'  THEN amount ELSE 0 END), 0) AS income,
				  COALESCE(SUM(CASE WHEN type='EXPENSE' THEN amount ELSE 0 END), 0) AS expense
				FROM transactions WHERE book_id = ?
				""";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, bookId);
			ResultSet rs = ps.executeQuery();
			Map<String, BigDecimal> m = new LinkedHashMap<>();
			if (rs.next()) {
				m.put("income", rs.getBigDecimal("income"));
				m.put("expense", rs.getBigDecimal("expense"));
			}
			return m;
		} finally {
			db.releaseConnection(conn);
		}
	}

	private CashBook mapRow(ResultSet rs) throws SQLException {
		return new CashBook(rs.getInt("id"), rs.getString("name"), rs.getString("description"),
				rs.getTimestamp("created_at").toLocalDateTime(), rs.getBoolean("is_active"));
	}
}