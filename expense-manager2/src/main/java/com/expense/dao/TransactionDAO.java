package com.expense.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.expense.model.CustomColumn;
import com.expense.model.Transaction;
import com.expense.util.DBConnection;

public class TransactionDAO {

	// ─── INSERT ───────────────────────────────────────────────────────────────

	public int addTransaction(Transaction t) throws SQLException {
		String table = t.getType(); // "income" or "expense"
		String sql = "INSERT INTO " + table + " (transaction_date, transaction_time, amount, category, note) "
				+ "VALUES (?, ?, ?, ?, ?) RETURNING id";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setDate(1, t.getTransactionDate());
			ps.setTime(2, t.getTransactionTime());
			ps.setBigDecimal(3, t.getAmount());
			ps.setString(4, t.getCategory());
			ps.setString(5, t.getNote());

			ResultSet rs = ps.executeQuery();
			int newId = -1;
			if (rs.next())
				newId = rs.getInt(1);

			// Insert custom column values
			if (newId > 0 && !t.getCustomValues().isEmpty()) {
				saveCustomValues(con, table, newId, t.getCustomValues());
			}
			return newId;
		}
	}

	// ─── UPDATE ───────────────────────────────────────────────────────────────

	public boolean updateTransaction(Transaction t) throws SQLException {
		String table = t.getType();
		String sql = "UPDATE " + table + " SET transaction_date=?, transaction_time=?, amount=?, category=?, note=? "
				+ "WHERE id=?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setDate(1, t.getTransactionDate());
			ps.setTime(2, t.getTransactionTime());
			ps.setBigDecimal(3, t.getAmount());
			ps.setString(4, t.getCategory());
			ps.setString(5, t.getNote());
			ps.setInt(6, t.getId());
			int rows = ps.executeUpdate();

			if (rows > 0) {
				saveCustomValues(con, table, t.getId(), t.getCustomValues());
			}
			return rows > 0;
		}
	}

	// ─── DELETE ───────────────────────────────────────────────────────────────

	public boolean deleteTransaction(String type, int id) throws SQLException {
		String sql = "DELETE FROM " + type + " WHERE id=?";
		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			// Also delete custom values
			deleteCustomValues(con, type, id);
			return ps.executeUpdate() > 0;
		}
	}

	// ─── GET ALL ──────────────────────────────────────────────────────────────

	public List<Transaction> getAll(String type, String fromDate, String toDate, String category, String sortBy,
			String sortDir) throws SQLException {

		StringBuilder sql = new StringBuilder("SELECT * FROM " + type + " WHERE 1=1");

		List<Object> params = new ArrayList<>();

		if (fromDate != null && !fromDate.isEmpty()) {
			sql.append(" AND transaction_date >= ?");
			params.add(Date.valueOf(fromDate));
		}
		if (toDate != null && !toDate.isEmpty()) {
			sql.append(" AND transaction_date <= ?");
			params.add(Date.valueOf(toDate));
		}
		if (category != null && !category.isEmpty()) {
			sql.append(" AND category = ?");
			params.add(category);
		}

		String col = isValidColumn(sortBy) ? sortBy : "transaction_date";
		String dir = "ASC".equalsIgnoreCase(sortDir) ? "ASC" : "DESC";
		sql.append(" ORDER BY ").append(col).append(" ").append(dir);

		List<Transaction> list = new ArrayList<>();

		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql.toString())) {

			for (int i = 0; i < params.size(); i++)
				ps.setObject(i + 1, params.get(i));

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Transaction t = mapRow(rs, type);
				list.add(t);
			}
		}

		// Attach custom values
		attachCustomValues(list, type);
		return list;
	}

	// ─── GET BY ID ────────────────────────────────────────────────────────────

	public Transaction getById(String type, int id) throws SQLException {
		String sql = "SELECT * FROM " + type + " WHERE id=?";
		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Transaction t = mapRow(rs, type);
				// load custom values
				Map<String, String> cv = getCustomValues(con, type, id);
				t.setCustomValues(cv);
				return t;
			}
		}
		return null;
	}

	// ─── SUMMARY ──────────────────────────────────────────────────────────────

	public BigDecimal getTotalAmount(String type, String fromDate, String toDate) throws SQLException {
		StringBuilder sql = new StringBuilder("SELECT COALESCE(SUM(amount),0) FROM " + type + " WHERE 1=1");
		List<Object> params = new ArrayList<>();
		if (fromDate != null && !fromDate.isEmpty()) {
			sql.append(" AND transaction_date >= ?");
			params.add(Date.valueOf(fromDate));
		}
		if (toDate != null && !toDate.isEmpty()) {
			sql.append(" AND transaction_date <= ?");
			params.add(Date.valueOf(toDate));
		}

		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql.toString())) {
			for (int i = 0; i < params.size(); i++)
				ps.setObject(i + 1, params.get(i));
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return rs.getBigDecimal(1);
		}
		return BigDecimal.ZERO;
	}

	public List<Map<String, Object>> getCategorySummary(String type, String fromDate, String toDate)
			throws SQLException {
		StringBuilder sql = new StringBuilder(
				"SELECT category, SUM(amount) as total, COUNT(*) as cnt FROM " + type + " WHERE 1=1");
		List<Object> params = new ArrayList<>();
		if (fromDate != null && !fromDate.isEmpty()) {
			sql.append(" AND transaction_date >= ?");
			params.add(Date.valueOf(fromDate));
		}
		if (toDate != null && !toDate.isEmpty()) {
			sql.append(" AND transaction_date <= ?");
			params.add(Date.valueOf(toDate));
		}
		sql.append(" GROUP BY category ORDER BY total DESC");

		List<Map<String, Object>> result = new ArrayList<>();
		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql.toString())) {
			for (int i = 0; i < params.size(); i++)
				ps.setObject(i + 1, params.get(i));
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Map<String, Object> row = new LinkedHashMap<>();
				row.put("category", rs.getString("category"));
				row.put("total", rs.getBigDecimal("total"));
				row.put("count", rs.getInt("cnt"));
				result.add(row);
			}
		}
		return result;
	}

	public List<Map<String, Object>> getMonthlyTrend(int year) throws SQLException {
		String sql = "SELECT EXTRACT(MONTH FROM transaction_date) as month, "
				+ "       'income' as type, SUM(amount) as total "
				+ "FROM income WHERE EXTRACT(YEAR FROM transaction_date) = ? GROUP BY month " + "UNION ALL "
				+ "SELECT EXTRACT(MONTH FROM transaction_date) as month, "
				+ "       'expense' as type, SUM(amount) as total "
				+ "FROM expense WHERE EXTRACT(YEAR FROM transaction_date) = ? GROUP BY month " + "ORDER BY month";

		List<Map<String, Object>> result = new ArrayList<>();
		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, year);
			ps.setInt(2, year);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Map<String, Object> row = new LinkedHashMap<>();
				row.put("month", rs.getInt("month"));
				row.put("type", rs.getString("type"));
				row.put("total", rs.getBigDecimal("total"));
				result.add(row);
			}
		}
		return result;
	}

	// ─── CATEGORIES ───────────────────────────────────────────────────────────

	public List<String> getCategories(String type) throws SQLException {
		String table = "income".equals(type) ? "income_categories" : "expense_categories";
		List<String> cats = new ArrayList<>();
		try (Connection con = DBConnection.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT name FROM " + table + " ORDER BY name")) {
			while (rs.next())
				cats.add(rs.getString("name"));
		}
		return cats;
	}

	public void addCategory(String type, String name) throws SQLException {
		String table = "income".equals(type) ? "income_categories" : "expense_categories";
		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con
						.prepareStatement("INSERT INTO " + table + " (name) VALUES (?) ON CONFLICT DO NOTHING")) {
			ps.setString(1, name);
			ps.executeUpdate();
		}
	}

	// ─── CUSTOM COLUMNS ───────────────────────────────────────────────────────

	public List<CustomColumn> getCustomColumns(String type) throws SQLException {
		List<CustomColumn> cols = new ArrayList<>();
		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con
						.prepareStatement("SELECT * FROM custom_columns WHERE table_name=? ORDER BY id")) {
			ps.setString(1, type);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				cols.add(new CustomColumn(rs.getInt("id"), rs.getString("table_name"), rs.getString("column_name"),
						rs.getString("column_label"), rs.getString("column_type")));
			}
		}
		return cols;
	}

	public void addCustomColumn(String type, String columnName, String columnLabel, String columnType)
			throws SQLException {
		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(
						"INSERT INTO custom_columns (table_name, column_name, column_label, column_type) "
								+ "VALUES (?, ?, ?, ?)")) {
			ps.setString(1, type);
			ps.setString(2, columnName.toLowerCase().replaceAll("[^a-z0-9_]", "_"));
			ps.setString(3, columnLabel);
			ps.setString(4, columnType);
			ps.executeUpdate();
		}
	}

	public void deleteCustomColumn(int columnId) throws SQLException {
		try (Connection con = DBConnection.getConnection()) {
			// Get column info first
			PreparedStatement info = con
					.prepareStatement("SELECT table_name, column_name FROM custom_columns WHERE id=?");
			info.setInt(1, columnId);
			ResultSet rs = info.executeQuery();
			if (rs.next()) {
				String tbl = rs.getString("table_name");
				String col = rs.getString("column_name");
				// Delete values
				PreparedStatement del = con
						.prepareStatement("DELETE FROM custom_column_values WHERE table_name=? AND column_name=?");
				del.setString(1, tbl);
				del.setString(2, col);
				del.executeUpdate();
			}
			// Delete column definition
			PreparedStatement delCol = con.prepareStatement("DELETE FROM custom_columns WHERE id=?");
			delCol.setInt(1, columnId);
			delCol.executeUpdate();
		}
	}

	// ─── PRIVATE HELPERS ──────────────────────────────────────────────────────

	private Transaction mapRow(ResultSet rs, String type) throws SQLException {
		Transaction t = new Transaction();
		t.setId(rs.getInt("id"));
		t.setTransactionDate(rs.getDate("transaction_date"));
		t.setTransactionTime(rs.getTime("transaction_time"));
		t.setAmount(rs.getBigDecimal("amount"));
		t.setCategory(rs.getString("category"));
		t.setNote(rs.getString("note"));
		t.setType(type);
		return t;
	}

	private void saveCustomValues(Connection con, String type, int recordId, Map<String, String> values)
			throws SQLException {
		String sql = "INSERT INTO custom_column_values (table_name, record_id, column_name, column_value) "
				+ "VALUES (?, ?, ?, ?) "
				+ "ON CONFLICT (table_name, record_id, column_name) DO UPDATE SET column_value=EXCLUDED.column_value";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			for (Map.Entry<String, String> entry : values.entrySet()) {
				ps.setString(1, type);
				ps.setInt(2, recordId);
				ps.setString(3, entry.getKey());
				ps.setString(4, entry.getValue());
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	private Map<String, String> getCustomValues(Connection con, String type, int recordId) throws SQLException {
		Map<String, String> map = new LinkedHashMap<>();
		try (PreparedStatement ps = con.prepareStatement(
				"SELECT column_name, column_value FROM custom_column_values " + "WHERE table_name=? AND record_id=?")) {
			ps.setString(1, type);
			ps.setInt(2, recordId);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				map.put(rs.getString(1), rs.getString(2));
		}
		return map;
	}

	private void attachCustomValues(List<Transaction> list, String type) throws SQLException {
		if (list.isEmpty())
			return;
		StringBuilder ids = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				ids.append(",");
			ids.append(list.get(i).getId());
		}
		String sql = "SELECT record_id, column_name, column_value FROM custom_column_values " + "WHERE table_name='"
				+ type + "' AND record_id IN (" + ids + ")";
		Map<Integer, Map<String, String>> dataMap = new HashMap<>();
		try (Connection con = DBConnection.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				int rid = rs.getInt("record_id");
				dataMap.computeIfAbsent(rid, k -> new LinkedHashMap<>()).put(rs.getString("column_name"),
						rs.getString("column_value"));
			}
		}
		for (Transaction t : list) {
			Map<String, String> cv = dataMap.getOrDefault(t.getId(), new LinkedHashMap<>());
			t.setCustomValues(cv);
		}
	}

	private void deleteCustomValues(Connection con, String type, int recordId) throws SQLException {
		try (PreparedStatement ps = con
				.prepareStatement("DELETE FROM custom_column_values WHERE table_name=? AND record_id=?")) {
			ps.setString(1, type);
			ps.setInt(2, recordId);
			ps.executeUpdate();
		}
	}

	private boolean isValidColumn(String col) {
		if (col == null)
			return false;
		return col.matches("transaction_date|transaction_time|amount|category");
	}
}