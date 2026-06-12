package com.expensemanager.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.expensemanager.model.Transaction;
import com.expensemanager.model.TransactionFilter;
import com.expensemanager.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TransactionDAO {

	private final DBConnection db = DBConnection.getInstance();
	private final AuditLogDAO auditDAO = new AuditLogDAO();
	private static final Logger log = LoggerFactory.getLogger(TransactionDAO.class);

	private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

	// ── INSERT ────────────────────────────────────────────
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
			auditDAO.logCreate(newId, "user");
			return newId;
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── UPDATE ────────────────────────────────────────────
	public void update(Transaction oldT, Transaction newT) throws SQLException {
		String sql = """
				UPDATE transactions SET
				  txn_datetime      = ?,
				  amount            = ?,
				  category_id       = ?,
				  sub_categories_id = ?,
				  note              = ?
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
		if (oldT.getAmount().compareTo(newT.getAmount()) != 0)
			auditDAO.logUpdate(oldT.getId(), "user", "amount", "₹" + oldT.getAmount(), "₹" + newT.getAmount());
		if (!oldT.getDateTime().equals(newT.getDateTime()))
			auditDAO.logUpdate(oldT.getId(), "user", "datetime", oldT.getDateTime().format(DT_FMT),
					newT.getDateTime().format(DT_FMT));
		if (oldT.getCategoryId() != newT.getCategoryId())
			auditDAO.logUpdate(oldT.getId(), "user", "category", nvl(oldT.getCategoryName()),
					nvl(newT.getCategoryName()));
		if (oldT.getSubcategoryid() != newT.getSubcategoryid())
			auditDAO.logUpdate(oldT.getId(), "user", "subcategory", nvl(oldT.getSubCategoryName()),
					nvl(newT.getSubCategoryName()));
		if (!Objects.equals(oldT.getNote(), newT.getNote()))
			auditDAO.logUpdate(oldT.getId(), "user", "note", nvl(oldT.getNote()), nvl(newT.getNote()));
	}

	// ── DELETE ────────────────────────────────────────────
	public void delete(int id) throws SQLException {
		auditDAO.logDelete(id, "user");
		String sql = "DELETE FROM transactions WHERE id = ?";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── FIND BY ID ────────────────────────────────────────
	public Transaction findById(int id) throws SQLException {
		String sql = baseSelect() + " WHERE t.id = ?";
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

	// ── LEGACY find (backward compat) ─────────────────────
	public List<Transaction> findAll(String typeFilter, int page, int pageSize, Integer bookId) throws SQLException {
		TransactionFilter f = new TransactionFilter();
		f.setType(typeFilter);
		f.setBookId(bookId);
		f.setPage(page);
		f.setPageSize(pageSize);
		return findByFilter(f);
	}

	public int count(String typeFilter, Integer bookId) throws SQLException {
		TransactionFilter f = new TransactionFilter();
		f.setType(typeFilter);
		f.setBookId(bookId);
		return countByFilter(f);
	}

	// ── FILTER-BASED SEARCH ───────────────────────────────
	public List<Transaction> findByFilter(TransactionFilter f) throws SQLException {
		BuildResult q = buildSQL(f, false);
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(q.sql)) {
			setParams(ps, q.params);
			ResultSet rs = ps.executeQuery();
			List<Transaction> list = new ArrayList<>();
			while (rs.next())
				list.add(mapRow(rs));
			loadCustomValues(conn, list);
			return list;
		} finally {
			db.releaseConnection(conn);
		}
	}

	public int countByFilter(TransactionFilter f) throws SQLException {
		BuildResult q = buildSQL(f, true);
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(q.sql)) {
			setParams(ps, q.params);
			ResultSet rs = ps.executeQuery();
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
			sql.append(" AND book_id=").append(bookId);
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
				SELECT TO_CHAR(DATE_TRUNC('month', txn_datetime),'Mon YYYY') AS month,
				       SUM(CASE WHEN type='INCOME'  THEN amount ELSE 0 END) AS income,
				       SUM(CASE WHEN type='EXPENSE' THEN amount ELSE 0 END) AS expense
				FROM transactions
				WHERE txn_datetime >= NOW() - INTERVAL '1 month' * ?
				""");
		if (bookId != null && bookId > 0)
			sql.append(" AND book_id=").append(bookId);
		sql.append(" GROUP BY DATE_TRUNC('month',txn_datetime) ORDER BY DATE_TRUNC('month',txn_datetime)");
//		System.out.println("SQL -->"+sql);
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
		} catch (Exception e) {
			log.info("monthlyTrend(); --> {}", e.getMessage());
//			System.out.println("monthlyTrend -->"+e.getMessage());
			return null;
		} finally {
			db.releaseConnection(conn);
		}
	}

	public List<Map<String, Object>> expenseByCategory(Integer bookId) throws SQLException {
		return categoryBreakdown("EXPENSE", bookId);
	}

	public List<Map<String, Object>> incomeByCategory(Integer bookId) throws SQLException {
		return categoryBreakdown("INCOME", bookId);
	}

	private List<Map<String, Object>> categoryBreakdown(String type, Integer bookId) throws SQLException {
		StringBuilder sql = new StringBuilder("""
				SELECT c.name, COALESCE(SUM(t.amount),0) AS total
				FROM transactions t JOIN categories c ON t.category_id = c.id
				WHERE t.type=?::txn_type
				""");
		if (bookId != null && bookId > 0)
			sql.append(" AND t.book_id=").append(bookId);
		sql.append(" GROUP BY c.name ORDER BY total DESC");
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			ps.setString(1, type);
			ResultSet rs = ps.executeQuery();
			List<Map<String, Object>> rows = new ArrayList<>();
			while (rs.next())
				rows.add(Map.of("name", rs.getString("name"), "total", rs.getBigDecimal("total")));
			return rows;
		} finally {
			db.releaseConnection(conn);
		}
	}

	public List<Map<String, Object>> dailyTotals(int year, int month, Integer bookId) throws SQLException {
		String sql = """
				SELECT DATE(txn_datetime) AS day,
				       SUM(CASE WHEN type='INCOME'  THEN amount ELSE 0 END) AS income,
				       SUM(CASE WHEN type='EXPENSE' THEN amount ELSE 0 END) AS expense
				FROM transactions
				WHERE EXTRACT(YEAR FROM txn_datetime)  = ?
				  AND EXTRACT(MONTH FROM txn_datetime) = ?
				""" + (bookId != null && bookId > 0 ? " AND book_id = " + bookId : "") + """
				GROUP BY DATE(txn_datetime)
				ORDER BY day
				""";
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, year);
			ps.setInt(2, month);
			ResultSet rs = ps.executeQuery();
			List<Map<String, Object>> rows = new ArrayList<>();
			while (rs.next()) {
				Map<String, Object> m = new LinkedHashMap<>();
				m.put("day", rs.getDate("day").toString());
				m.put("income", rs.getBigDecimal("income"));
				m.put("expense", rs.getBigDecimal("expense"));
				rows.add(m);
			}
			return rows;
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── SQL BUILDER ───────────────────────────────────────
	private record BuildResult(String sql, List<Object> params) {
	}

	private BuildResult buildSQL(TransactionFilter f, boolean countOnly) {
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder();

		if (countOnly) {
			sql.append("SELECT COUNT(*) FROM transactions t WHERE 1=1");
		} else {
			sql.append(baseSelect() + " WHERE 1=1");
		}

		if (f.getBookId() != null && f.getBookId() > 0) {
			sql.append(" AND t.book_id = ?");
			params.add(f.getBookId());
		}
		if (f.getType() != null && !f.getType().isBlank()) {
			sql.append(" AND t.type = ?::txn_type");
			params.add(f.getType());
		}
		if (f.getDateFrom() != null) {
			sql.append(" AND t.txn_datetime >= ?");
			params.add(Timestamp.valueOf(f.getDateFrom().atStartOfDay()));
		}
		if (f.getDateTo() != null) {
			sql.append(" AND t.txn_datetime < ?");
			params.add(Timestamp.valueOf(f.getDateTo().plusDays(1).atStartOfDay()));
		}
		// Multi-category IN clause
		if (f.getCategoryIds() != null && !f.getCategoryIds().isEmpty()) {
			sql.append(" AND t.category_id IN (");
			for (int i = 0; i < f.getCategoryIds().size(); i++) {
				sql.append(i > 0 ? ",?" : "?");
				params.add(f.getCategoryIds().get(i));
			}
			sql.append(")");
		}
		// Multi-subcategory IN clause
		if (f.getSubCategoryIds() != null && !f.getSubCategoryIds().isEmpty()) {
			sql.append(" AND t.sub_categories_id IN (");
			for (int i = 0; i < f.getSubCategoryIds().size(); i++) {
				sql.append(i > 0 ? ",?" : "?");
				params.add(f.getSubCategoryIds().get(i));
			}
			sql.append(")");
		}
		// Amount conditions
		if (f.getAmount1() != null && f.getAmountOp1() != null) {
			sql.append(" AND t.amount ").append(TransactionFilter.safeOp(f.getAmountOp1())).append(" ?");
			params.add(f.getAmount1());
		}
		if (f.getAmount2() != null && f.getAmountOp2() != null) {
			sql.append(" AND t.amount ").append(TransactionFilter.safeOp(f.getAmountOp2())).append(" ?");
			params.add(f.getAmount2());
		}
		// Note + custom field ILIKE
		if (f.getNoteSearch() != null && !f.getNoteSearch().isBlank()) {
			String like = "%" + f.getNoteSearch().trim() + "%";
			sql.append("""
					 AND (t.note ILIKE ?
					      OR EXISTS (
					        SELECT 1 FROM transaction_custom_values tcv
					        WHERE tcv.transaction_id = t.id AND tcv.value ILIKE ?
					      ))
					""");
			params.add(like);
			params.add(like);
		}
		if (!countOnly) {
			sql.append(" ORDER BY t.txn_datetime DESC");
			if (f.getPageSize() < Integer.MAX_VALUE) {
				sql.append(" LIMIT ? OFFSET ?");
				params.add(f.getPageSize());
				params.add((f.getPage() - 1) * f.getPageSize());
			}
		}
		return new BuildResult(sql.toString(), params);
	}

	private String baseSelect() {
		return """
				SELECT t.id, t.type, t.txn_datetime, t.amount, t.note, t.book_id,
				       c.id AS cat_id, c.name AS cat_name,
				       sc.sub_categories_id AS subcat_id, sc.name AS subcat_name
				FROM transactions t
				LEFT JOIN categories c ON t.category_id=c.id
				LEFT JOIN sub_categories sc ON t.sub_categories_id=sc.sub_categories_id
				""";
	}

	private void setParams(PreparedStatement ps, List<Object> params) throws SQLException {
		for (int i = 0; i < params.size(); i++) {
			Object p = params.get(i);
			if (p instanceof String s)
				ps.setString(i + 1, s);
			else if (p instanceof Integer n)
				ps.setInt(i + 1, n);
			else if (p instanceof BigDecimal d)
				ps.setBigDecimal(i + 1, d);
			else if (p instanceof Timestamp ts)
				ps.setTimestamp(i + 1, ts);
			else
				ps.setObject(i + 1, p);
		}
	}

	private void insertCustomValues(Connection conn, int txnId, Map<String, String> values) throws SQLException {
		String sql = """
				INSERT INTO transaction_custom_values (transaction_id, col_def_id, value)
				SELECT ?, id, ? FROM column_definitions WHERE col_key=?
				ON CONFLICT (transaction_id, col_def_id) DO UPDATE SET value=EXCLUDED.value
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
				JOIN column_definitions cd ON tcv.col_def_id=cd.id
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