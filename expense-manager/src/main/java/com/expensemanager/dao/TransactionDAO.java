package com.expensemanager.dao;

import com.expensemanager.model.Transaction;
import com.expensemanager.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TransactionDAO {

	private final DBConnection db = DBConnection.getInstance();
	private final AuditLogDAO auditDAO = new AuditLogDAO();

	private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

	// ── INSERT (with CREATE audit log) ────────────────────
	public int insert(Transaction t) throws SQLException {
		String sql = """
				INSERT INTO transactions
				  (type, txn_datetime, amount, category_id, sub_categories_id, note, book_id)
				VALUES (?::txn_type, ?, ?, ?, ?, ?, ?)
				RETURNING id
				""";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, t.getType().name());
			ps.setTimestamp(2, Timestamp.valueOf(t.getDateTime()));
			ps.setBigDecimal(3, t.getAmount());
			ps.setInt(4, t.getCategoryId());
			if (t.getSubcategoryid() > 0)
				ps.setInt(5, t.getSubcategoryid());
			else
				ps.setNull(5, Types.INTEGER);
			ps.setString(6, t.getNote());
			if (t.getBookId() > 0)
				ps.setInt(7, t.getBookId());
			else
				ps.setNull(7, Types.INTEGER);

			ResultSet rs = ps.executeQuery();
			rs.next();
			int newId = rs.getInt(1);
			if (!t.getCustomValues().isEmpty())
				insertCustomValues(conn, newId, t.getCustomValues());

			// Log creation
			auditDAO.logCreate(newId, "user");
			return newId;
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── UPDATE (with field-level audit log) ───────────────
	/**
	 * Compare old vs new transaction — log only changed fields.
	 */
	public void update(Transaction oldT, Transaction newT) throws SQLException {
		String sql = """
				UPDATE transactions SET
				  txn_datetime   = ?,
				  amount         = ?,
				  category_id    = ?,
				  sub_categories_id = ?,
				  note           = ?
				WHERE id = ?
				""";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setTimestamp(1, Timestamp.valueOf(newT.getDateTime()));
			ps.setBigDecimal(2, newT.getAmount());
			ps.setInt(3, newT.getCategoryId());
			if (newT.getSubcategoryid() > 0)
				ps.setInt(4, newT.getSubcategoryid());
			else
				ps.setNull(4, Types.INTEGER);
			ps.setString(5, newT.getNote());
			ps.setInt(6, oldT.getId());
			ps.executeUpdate();
		} finally {
			db.releaseConnection(conn);
		}

		// ── Audit: log each changed field ──
		// Amount
		if (oldT.getAmount().compareTo(newT.getAmount()) != 0) {
			auditDAO.logUpdate(oldT.getId(), "user", "amount", "₹" + oldT.getAmount(), "₹" + newT.getAmount());
		}
		// DateTime
		if (!oldT.getDateTime().equals(newT.getDateTime())) {
			auditDAO.logUpdate(oldT.getId(), "user", "datetime", oldT.getDateTime().format(DT_FMT),
					newT.getDateTime().format(DT_FMT));
		}
		// Category
		if (oldT.getCategoryId() != newT.getCategoryId()) {
			auditDAO.logUpdate(oldT.getId(), "user", "category", nvl(oldT.getCategoryName()),
					nvl(newT.getCategoryName()));
		}
		// SubCategory
		if (oldT.getSubcategoryid() != newT.getSubcategoryid()) {
			auditDAO.logUpdate(oldT.getId(), "user", "subcategory", nvl(oldT.getSubCategoryName()),
					nvl(newT.getSubCategoryName()));
		}
		// Note
		if (!Objects.equals(oldT.getNote(), newT.getNote())) {
			auditDAO.logUpdate(oldT.getId(), "user", "note", nvl(oldT.getNote()), nvl(newT.getNote()));
		}
	}

	// ── DELETE ────────────────────────────────────────────
	public void delete(int transactionId) throws SQLException {
		auditDAO.logDelete(transactionId, "user"); // log before delete (FK cascade)
		String sql = "DELETE FROM transactions WHERE id = ?";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, transactionId);
			ps.executeUpdate();
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── FIND BY ID ────────────────────────────────────────
	public Transaction findById(int id) throws SQLException {
		String sql = """
				SELECT t.id, t.type, t.txn_datetime, t.amount, t.note, t.book_id,
				       c.id AS cat_id, c.name AS cat_name,
				       sc.sub_categories_id AS subcat_id, sc.name AS subcat_name
				FROM transactions t
				LEFT JOIN categories c    ON t.category_id = c.id
				LEFT JOIN sub_categories sc ON t.sub_categories_id = sc.sub_categories_id
				WHERE t.id = ?
				""";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Transaction t = mapRow(rs);
				loadCustomValues(conn, List.of(t));
				return t;
			}
			return null;
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── LIST (book-aware, paginated) ─────────────────────
	public List<Transaction> findAll(String typeFilter, int page, int pageSize, Integer bookId) throws SQLException {
		StringBuilder sql = new StringBuilder("""
				SELECT t.id, t.type, t.txn_datetime, t.amount, t.note, t.book_id,
				       c.id AS cat_id, c.name AS cat_name,
				       sc.sub_categories_id AS subcat_id, sc.name AS subcat_name
				FROM transactions t
				LEFT JOIN categories c    ON t.category_id = c.id
				LEFT JOIN sub_categories sc ON t.sub_categories_id = sc.sub_categories_id
				WHERE 1=1
				""");
		if (bookId != null && bookId > 0)
			sql.append(" AND t.book_id = ").append(bookId);
		if (typeFilter != null && !typeFilter.isBlank())
			sql.append(" AND t.type = '").append(typeFilter).append("'::txn_type");
		sql.append(" ORDER BY t.txn_datetime DESC");
		if (pageSize < Integer.MAX_VALUE)
			sql.append(" LIMIT ").append(pageSize).append(" OFFSET ").append((page - 1) * pageSize);

		Connection conn = db.getConnection();
		try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql.toString())) {
			List<Transaction> list = new ArrayList<>();
			while (rs.next())
				list.add(mapRow(rs));
			loadCustomValues(conn, list);
			return list;
		} finally {
			db.releaseConnection(conn);
		}
	}

	public int count(String typeFilter, Integer bookId) throws SQLException {
		StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM transactions WHERE 1=1");
		if (bookId != null && bookId > 0)
			sql.append(" AND book_id = ").append(bookId);
		if (typeFilter != null && !typeFilter.isBlank())
			sql.append(" AND type = '").append(typeFilter).append("'::txn_type");
		Connection conn = db.getConnection();
		try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql.toString())) {
			rs.next();
			return rs.getInt(1);
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── ANALYTICS ─────────────────────────────────────────
	public BigDecimal sumByType(String type, Integer bookId) throws SQLException {
		StringBuilder sql = new StringBuilder(
				"SELECT COALESCE(SUM(amount),0) FROM transactions WHERE type=?::txn_type");
		if (bookId != null && bookId > 0)
			sql.append(" AND book_id = ").append(bookId);
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			ps.setString(1, type);
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getBigDecimal(1);
		} finally {
			db.releaseConnection(conn);
		}
	}

	public List<Map<String, Object>> monthlyTrend(int months, Integer bookId) throws SQLException {
		StringBuilder sql = new StringBuilder("""
				SELECT TO_CHAR(DATE_TRUNC('month', txn_datetime), 'Mon YYYY') AS month,
				       SUM(CASE WHEN type='INCOME'  THEN amount ELSE 0 END) AS income,
				       SUM(CASE WHEN type='EXPENSE' THEN amount ELSE 0 END) AS expense
				FROM transactions WHERE txn_datetime >= NOW() - INTERVAL '1 month' * ?
				""");
		if (bookId != null && bookId > 0)
			sql.append(" AND book_id = ").append(bookId);
		sql.append(" GROUP BY DATE_TRUNC('month',txn_datetime) ORDER BY 1");
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			ps.setInt(1, months);
			ResultSet rs = ps.executeQuery();
			List<Map<String, Object>> rows = new ArrayList<>();
			while (rs.next()) {
				Map<String, Object> m = new LinkedHashMap<>();
				m.put("month", rs.getString("month"));
				m.put("income", rs.getBigDecimal("income"));
				m.put("expense", rs.getBigDecimal("expense"));
				rows.add(m);
			}
			return rows;
		} finally {
			db.releaseConnection(conn);
		}
	}

	public List<Map<String, Object>> expenseByCategory(Integer bookId) throws SQLException {
		StringBuilder sql = new StringBuilder("""
				SELECT c.name, COALESCE(SUM(t.amount),0) AS total
				FROM transactions t JOIN categories c ON t.category_id = c.id
				WHERE t.type='EXPENSE'
				""");
		if (bookId != null && bookId > 0)
			sql.append(" AND t.book_id=").append(bookId);
		sql.append(" GROUP BY c.name ORDER BY total DESC");
		Connection conn = db.getConnection();
		try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql.toString())) {
			List<Map<String, Object>> rows = new ArrayList<>();
			while (rs.next())
				rows.add(Map.of("name", rs.getString("name"), "total", rs.getBigDecimal("total")));
			return rows;
		} finally {
			db.releaseConnection(conn);
		}
	}

	public List<Map<String, Object>> incomeByCategory(Integer bookId) throws SQLException {
		StringBuilder sql = new StringBuilder("""
				SELECT c.name, COALESCE(SUM(t.amount),0) AS total
				FROM transactions t JOIN categories c ON t.category_id = c.id
				WHERE t.type='INCOME'
				""");
		if (bookId != null && bookId > 0)
			sql.append(" AND t.book_id=").append(bookId);
		sql.append(" GROUP BY c.name ORDER BY total DESC");
		Connection conn = db.getConnection();
		try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql.toString())) {
			List<Map<String, Object>> rows = new ArrayList<>();
			while (rs.next())
				rows.add(Map.of("name", rs.getString("name"), "total", rs.getBigDecimal("total")));
			return rows;
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── Helpers ───────────────────────────────────────────
	private void insertCustomValues(Connection conn, int txnId, Map<String, String> values) throws SQLException {
		String sql = """
				INSERT INTO transaction_custom_values (transaction_id, col_def_id, value)
				SELECT ?, id, ? FROM column_definitions WHERE col_key = ?
				ON CONFLICT (transaction_id, col_def_id) DO UPDATE SET value = EXCLUDED.value
				""";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Map.Entry<String, String> e : values.entrySet()) {
				ps.setInt(1, txnId);
				ps.setString(2, e.getValue());
				ps.setString(3, e.getKey());
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	private Transaction mapRow(ResultSet rs) throws SQLException {
		Transaction t = new Transaction();
		t.setId(rs.getInt("id"));
		t.setType(Transaction.Type.valueOf(rs.getString("type")));
		t.setDateTime(rs.getTimestamp("txn_datetime").toLocalDateTime());
		t.setAmount(rs.getBigDecimal("amount"));
		t.setNote(rs.getString("note"));
		t.setCategoryId(rs.getInt("cat_id"));
		t.setCategoryName(rs.getString("cat_name"));
		t.setSubcategoryid(rs.getInt("subcat_id"));
		t.setSubCategoryName(rs.getString("subcat_name"));
		t.setBookId(rs.getInt("book_id"));
		return t;
	}

	private void loadCustomValues(Connection conn, List<Transaction> list) throws SQLException {
		if (list.isEmpty())
			return;
		StringJoiner ids = new StringJoiner(",");
		Map<Integer, Transaction> byId = new LinkedHashMap<>();
		for (Transaction t : list) {
			ids.add(String.valueOf(t.getId()));
			byId.put(t.getId(), t);
		}
		String sql = """
				SELECT tcv.transaction_id, cd.col_key, tcv.value
				FROM transaction_custom_values tcv
				JOIN column_definitions cd ON tcv.col_def_id = cd.id
				WHERE tcv.transaction_id IN (""" + ids + ")";
		try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				Transaction t = byId.get(rs.getInt("transaction_id"));
				if (t != null)
					t.addCustomValue(rs.getString("col_key"), rs.getString("value"));
			}
		}
	}

	private String nvl(String s) {
		return s != null ? s : "";
	}
}