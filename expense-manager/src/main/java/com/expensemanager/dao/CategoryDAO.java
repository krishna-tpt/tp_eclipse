package com.expensemanager.dao;

import com.expensemanager.model.Category;
import com.expensemanager.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

	private final DBConnection db = DBConnection.getInstance();

	/**
	 * Returns common categories (book_id IS NULL) PLUS custom categories belonging
	 * to the given book. If bookId is null, only common categories are returned.
	 */
	public List<Category> findByType(String type, Integer bookId) throws SQLException {
		String sql = "SELECT id, name, type, book_id FROM categories "
				+ "WHERE type=?::txn_type AND (book_id IS NULL OR book_id=?) " + "ORDER BY book_id NULLS FIRST, name";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, type);
			if (bookId != null)
				ps.setInt(2, bookId);
			else
				ps.setNull(2, Types.INTEGER);
			ResultSet rs = ps.executeQuery();
			List<Category> list = new ArrayList<>();
			while (rs.next()) {
				int bId = rs.getInt("book_id");
				Integer bookIdVal = rs.wasNull() ? null : bId;
				list.add(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type"), bookIdVal));
			}
			return list;
		} finally {
			db.releaseConnection(conn);
		}
	}

	/** Backward-compatible overload: common categories only. */
	public List<Category> findByType(String type) throws SQLException {
		return findByType(type, null);
	}

	public void insert(String name, String type, Integer bookId) throws SQLException {
		String sql = "INSERT INTO categories (name, type, book_id) VALUES (?, ?::txn_type, ?) ON CONFLICT DO NOTHING";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, name);
			ps.setString(2, type);
			if (bookId != null)
				ps.setInt(3, bookId);
			else
				ps.setNull(3, Types.INTEGER);
			ps.executeUpdate();
		} finally {
			db.releaseConnection(conn);
		}
	}

	/** Backward-compatible overload: inserts a common category. */
	public void insert(String name, String type) throws SQLException {
		insert(name, type, null);
	}

	public void delete(int id) throws SQLException {
		Connection conn = db.getConnection();
		try {
			conn.setAutoCommit(false);
			try (PreparedStatement ps1 = conn.prepareStatement(
					"UPDATE transactions SET category_id = NULL, updated_at=NOW() WHERE category_id = ?")) {
				ps1.setInt(1, id);
				ps1.executeUpdate();
			}
			try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM categories WHERE id = ?")) {
				ps2.setInt(1, id);
				ps2.executeUpdate();
			}
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
			db.releaseConnection(conn);
		}
	}
}