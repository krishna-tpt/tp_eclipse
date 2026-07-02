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

	// Keep old one for backward compatibility (other JSPs might call it)
	public List<CashBook> findAll() throws SQLException {
	    return findAll(null, null);
	}

	/**
	 * @param search  partial book-name search (case-insensitive), nullable
	 * @param sort    one of: "updated" (default), "name_asc", "balance_desc", "balance_asc", "created"
	 */
	public List<CashBook> findAll(String search, String sort) throws SQLException {
		StringBuilder sql = new StringBuilder("""
				SELECT b.id, b.name, b.description, b.created_at, b.updated_at, b.is_active,
				       COALESCE(t.income,0) - COALESCE(t.expense,0) AS net_balance
				FROM cash_books b
				LEFT JOIN (
				    SELECT book_id,
				           SUM(CASE WHEN type='INCOME'  THEN amount ELSE 0 END) AS income,
				           SUM(CASE WHEN type='EXPENSE' THEN amount ELSE 0 END) AS expense
				    FROM transactions
				    GROUP BY book_id
				) t ON t.book_id = b.id
				""");

		List<String> params = new ArrayList<>();
		if (search != null && !search.isBlank()) {
			sql.append("WHERE LOWER(b.name) LIKE LOWER(?) ");
			params.add("%" + search.trim() + "%");
		}

		sql.append(switch (sort == null ? "" : sort) {
		case "name_asc" -> "ORDER BY b.name ASC";
		case "balance_desc" -> "ORDER BY net_balance DESC";
		case "balance_asc" -> "ORDER BY net_balance ASC";
		case "created" -> "ORDER BY b.created_at DESC";
		default -> "ORDER BY COALESCE(b.updated_at, b.created_at) DESC"; // "updated"
		});

		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			for (int i = 0; i < params.size(); i++) {
				ps.setString(i + 1, params.get(i));
			}
			try (ResultSet rs = ps.executeQuery()) {
				List<CashBook> list = new ArrayList<>();
				while (rs.next())
					list.add(mapRow(rs));
				return list;
			}
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
		update(id, name, description, true);
	}

	public void update(int id, String name, String description, boolean active) throws SQLException {
		String sql = "UPDATE cash_books SET name=?, description=?, is_active=?, updated_at=NOW() WHERE id=?";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, name.trim());
			ps.setString(2, description != null ? description.trim() : "");
			ps.setBoolean(3, active);
			ps.setInt(4, id);
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