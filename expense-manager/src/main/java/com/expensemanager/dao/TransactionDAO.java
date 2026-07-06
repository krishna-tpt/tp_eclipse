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

	private static final Map<String, String> SORT_COLUMNS = Map.of("date", "t.txn_datetime", "type", "t.type",
			"category", "c.name", "subcategory", "sc.name", "amount", "t.amount", "note", "t.note");

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
		} catch (Exception e) {
			log.debug("insert method exception : {}", e.getMessage());
		} finally {
			db.releaseConnection(conn);
		}
		return 0;
	}

	// ── UPDATE ────────────────────────────────────────────
	public void update(Transaction oldT, Transaction newT) throws SQLException {
		String sql = """
				UPDATE transactions SET
				  txn_datetime      = ?,
				  amount            = ?,
				  category_id       = ?,
				  sub_categories_id = ?,
				  note              = ?,
				  book_id           = ?,
				  updated_at=NOW()
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
			ps.setInt(6, newT.getBookId());
			ps.setInt(7, oldT.getId());
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

		if (!Objects.equals(oldT.getBookId(), newT.getBookId())) {
			CashBookDAO dao = new CashBookDAO();
			auditDAO.logUpdate(oldT.getId(), "user", "book", dao.findById(oldT.getBookId()).getName(),
					dao.findById(newT.getBookId()).getName());
		}
	}

	// ── DELETE ────────────────────────────────────────────
	// Also writes a tombstone to deleted_records so NeonSyncService can
	// propagate this delete on the next sync (a plain DELETE leaves no
	// trace for an "updated_at >= ?" sync query to find).
	public void delete(int id) throws SQLException {
		auditDAO.logDelete(id, "user");
		Connection conn = db.getConnection();
		boolean prevAutoCommit = true;
		try {
			prevAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);

			try (PreparedStatement ps = conn.prepareStatement("DELETE FROM transactions WHERE id = ?")) {
				ps.setInt(1, id);
				ps.executeUpdate();
			}
			recordTombstone(conn, "transactions", id);

			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(prevAutoCommit);
			db.releaseConnection(conn);
		}
	}

	// ── Tombstone helper — shared by every DAO's delete() so
	// NeonSyncService can find & propagate deletions. ──────
	private void recordTombstone(Connection conn, String tableName, int recordId) throws SQLException {
		String sql = """
				INSERT INTO deleted_records (table_name, record_id, deleted_at)
				VALUES (?, ?, NOW())
				ON CONFLICT (table_name, record_id) DO UPDATE SET deleted_at = EXCLUDED.deleted_at
				""";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, tableName);
			ps.setInt(2, recordId);
			ps.executeUpdate();
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

	// ── ADJACENT (Prev/Next) — for the transaction detail page ────
	// "Prev" = one row NEWER (up) in the same order the list uses
	// (txn_datetime DESC, id DESC as a tiebreaker for equal timestamps).
	// "Next" = one row OLDER (down). Scoped to the same book only —
	// does not currently account for an active list filter.
	public Integer findPrevId(int id, int bookId) throws SQLException {
		return findAdjacentId(id, bookId, true);
	}

	public Integer findNextId(int id, int bookId) throws SQLException {
		return findAdjacentId(id, bookId, false);
	}

	private Integer findAdjacentId(int id, int bookId, boolean newer) throws SQLException {
		Connection conn = db.getConnection();
		try {
			Timestamp curTs;
			try (PreparedStatement ps = conn.prepareStatement("SELECT txn_datetime FROM transactions WHERE id = ?")) {
				ps.setInt(1, id);
				ResultSet rs = ps.executeQuery();
				if (!rs.next())
					return null;
				curTs = rs.getTimestamp("txn_datetime");
			}

			String sql = newer
					? "SELECT id FROM transactions WHERE book_id = ? AND (txn_datetime > ? OR (txn_datetime = ? AND id > ?)) ORDER BY txn_datetime ASC, id ASC LIMIT 1"
					: "SELECT id FROM transactions WHERE book_id = ? AND (txn_datetime < ? OR (txn_datetime = ? AND id < ?)) ORDER BY txn_datetime DESC, id DESC LIMIT 1";

			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, bookId);
				ps.setTimestamp(2, curTs);
				ps.setTimestamp(3, curTs);
				ps.setInt(4, id);
				ResultSet rs = ps.executeQuery();
				return rs.next() ? rs.getInt("id") : null;
			}
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

	// ── NEW: Category breakdown for a specific month ──────
	public List<Map<String, Object>> categoryBreakdownByMonth(String type, int year, int month, Integer bookId)
			throws SQLException {
		StringBuilder sql = new StringBuilder("""
				SELECT c.name AS category,
				       COALESCE(SUM(t.amount), 0) AS total,
				       COUNT(*) AS txn_count
				FROM transactions t
				JOIN categories c ON t.category_id = c.id
				WHERE t.type = ?::txn_type
				  AND EXTRACT(YEAR  FROM t.txn_datetime) = ?
				  AND EXTRACT(MONTH FROM t.txn_datetime) = ?
				""");
		if (bookId != null && bookId > 0)
			sql.append(" AND t.book_id = ").append(bookId);
		sql.append(" GROUP BY c.name ORDER BY total DESC");
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			ps.setString(1, type);
			ps.setInt(2, year);
			ps.setInt(3, month);
			ResultSet rs = ps.executeQuery();
			List<Map<String, Object>> rows = new ArrayList<>();
			while (rs.next()) {
				Map<String, Object> m = new LinkedHashMap<>();
				m.put("category", rs.getString("category"));
				m.put("total", rs.getBigDecimal("total"));
				m.put("txnCount", rs.getInt("txn_count"));
				rows.add(m);
			}
			return rows;
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── NEW: Sub-category breakdown for a specific month ──
	public List<Map<String, Object>> subCategoryBreakdownByMonth(String type, int year, int month, Integer bookId)
			throws SQLException {
		StringBuilder sql = new StringBuilder("""
				SELECT c.name AS category,
				       COALESCE(sc.name, 'Uncategorized') AS subcategory,
				       COALESCE(SUM(t.amount), 0) AS total,
				       COUNT(*) AS txn_count
				FROM transactions t
				JOIN categories c ON t.category_id = c.id
				LEFT JOIN sub_categories sc ON t.sub_categories_id = sc.sub_categories_id
				WHERE t.type = ?::txn_type
				  AND EXTRACT(YEAR  FROM t.txn_datetime) = ?
				  AND EXTRACT(MONTH FROM t.txn_datetime) = ?
				""");
		if (bookId != null && bookId > 0)
			sql.append(" AND t.book_id = ").append(bookId);
		sql.append(" GROUP BY c.name, sc.name ORDER BY total DESC");
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			ps.setString(1, type);
			ps.setInt(2, year);
			ps.setInt(3, month);
			ResultSet rs = ps.executeQuery();
			List<Map<String, Object>> rows = new ArrayList<>();
			while (rs.next()) {
				Map<String, Object> m = new LinkedHashMap<>();
				m.put("category", rs.getString("category"));
				m.put("subcategory", rs.getString("subcategory"));
				m.put("total", rs.getBigDecimal("total"));
				m.put("txnCount", rs.getInt("txn_count"));
				rows.add(m);
			}
			return rows;
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── NEW: Day-of-week spending pattern ─────────────────
	public List<Map<String, Object>> dayOfWeekPattern(int year, int month, Integer bookId) throws SQLException {
		StringBuilder sql = new StringBuilder("""
				SELECT TO_CHAR(txn_datetime, 'Dy') AS dow_label,
				       EXTRACT(DOW FROM txn_datetime)::INT AS dow_num,
				       SUM(CASE WHEN type='INCOME'::txn_type  THEN amount ELSE 0 END) AS income,
				       SUM(CASE WHEN type='EXPENSE'::txn_type THEN amount ELSE 0 END) AS expense,
				       COUNT(*) AS txn_count
				FROM transactions
				WHERE EXTRACT(YEAR  FROM txn_datetime) = ?
				  AND EXTRACT(MONTH FROM txn_datetime) = ?
				""");
		if (bookId != null && bookId > 0)
			sql.append(" AND book_id = ").append(bookId);
		sql.append(" GROUP BY dow_num, dow_label ORDER BY dow_num");
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			ps.setInt(1, year);
			ps.setInt(2, month);
			ResultSet rs = ps.executeQuery();
			List<Map<String, Object>> rows = new ArrayList<>();
			// Prefill all 7 days with zero
			String[] days = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
			Map<Integer, Map<String, Object>> byDow = new LinkedHashMap<>();
			for (int i = 0; i < 7; i++) {
				Map<String, Object> m = new LinkedHashMap<>();
				m.put("dow", i);
				m.put("label", days[i]);
				m.put("income", java.math.BigDecimal.ZERO);
				m.put("expense", java.math.BigDecimal.ZERO);
				m.put("txnCount", 0);
				byDow.put(i, m);
			}
			while (rs.next()) {
				int dow = rs.getInt("dow_num");
				Map<String, Object> m = byDow.getOrDefault(dow, new LinkedHashMap<>());
				m.put("income", rs.getBigDecimal("income"));
				m.put("expense", rs.getBigDecimal("expense"));
				m.put("txnCount", rs.getInt("txn_count"));
				byDow.put(dow, m);
			}
			rows.addAll(byDow.values());
			return rows;
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── NEW: Weekly totals within a month ─────────────────
	public List<Map<String, Object>> weeklyTotals(int year, int month, Integer bookId) throws SQLException {
		StringBuilder sql = new StringBuilder("""
				SELECT EXTRACT(WEEK FROM txn_datetime)::INT AS wk,
				       MIN(DATE(txn_datetime)) AS week_start,
				       SUM(CASE WHEN type='INCOME'::txn_type  THEN amount ELSE 0 END) AS income,
				       SUM(CASE WHEN type='EXPENSE'::txn_type THEN amount ELSE 0 END) AS expense,
				       COUNT(*) AS txn_count
				FROM transactions
				WHERE EXTRACT(YEAR  FROM txn_datetime) = ?
				  AND EXTRACT(MONTH FROM txn_datetime) = ?
				""");
		if (bookId != null && bookId > 0)
			sql.append(" AND book_id = ").append(bookId);
		sql.append(" GROUP BY wk ORDER BY wk");
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			ps.setInt(1, year);
			ps.setInt(2, month);
			ResultSet rs = ps.executeQuery();
			List<Map<String, Object>> rows = new ArrayList<>();
			int weekNum = 1;
			while (rs.next()) {
				Map<String, Object> m = new LinkedHashMap<>();
				m.put("week", "Week " + weekNum++);
				m.put("weekStart", rs.getDate("week_start").toString());
				m.put("income", rs.getBigDecimal("income"));
				m.put("expense", rs.getBigDecimal("expense"));
				m.put("txnCount", rs.getInt("txn_count"));
				rows.add(m);
			}
			return rows;
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── NEW: Month summary (income, expense, net, txn count) ──
	public Map<String, Object> monthSummary(int year, int month, Integer bookId) throws SQLException {
		StringBuilder sql = new StringBuilder("""
				SELECT
				  SUM(CASE WHEN type='INCOME'::txn_type  THEN amount ELSE 0 END) AS income,
				  SUM(CASE WHEN type='EXPENSE'::txn_type THEN amount ELSE 0 END) AS expense,
				  COUNT(*) AS txn_count,
				  COUNT(DISTINCT category_id) AS cat_count
				FROM transactions
				WHERE EXTRACT(YEAR  FROM txn_datetime) = ?
				  AND EXTRACT(MONTH FROM txn_datetime) = ?
				""");
		if (bookId != null && bookId > 0)
			sql.append(" AND book_id = ").append(bookId);
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			ps.setInt(1, year);
			ps.setInt(2, month);
			ResultSet rs = ps.executeQuery();
			Map<String, Object> m = new LinkedHashMap<>();
			if (rs.next()) {
				java.math.BigDecimal inc = rs.getBigDecimal("income");
				java.math.BigDecimal exp = rs.getBigDecimal("expense");
				if (inc == null)
					inc = java.math.BigDecimal.ZERO;
				if (exp == null)
					exp = java.math.BigDecimal.ZERO;
				m.put("income", inc);
				m.put("expense", exp);
				m.put("net", inc.subtract(exp));
				m.put("txnCount", rs.getInt("txn_count"));
				m.put("catCount", rs.getInt("cat_count"));
				m.put("savingsRate",
						inc.compareTo(java.math.BigDecimal.ZERO) > 0
								? inc.subtract(exp).multiply(java.math.BigDecimal.valueOf(100)).divide(inc, 1,
										java.math.RoundingMode.HALF_UP)
								: java.math.BigDecimal.ZERO);
			}
			return m;
		} finally {
			db.releaseConnection(conn);
		}
	}

	// ── SQL BUILDER ───────────────────────────────────────
	/**
	 * Sum of amounts for transactions matching the given filter. Filter must have
	 * type set (INCOME or EXPENSE) for meaningful results.
	 */
	public BigDecimal sumByFilter(TransactionFilter f) throws SQLException {
		// Reuse countOnly=true path to get WHERE clause, then replace SELECT
		BuildResult q = buildSQL(f, true); // gives: SELECT COUNT(*) FROM transactions t WHERE ...
		String sumSql = q.sql().replace("SELECT COUNT(*)", "SELECT COALESCE(SUM(t.amount), 0)");
		Connection conn = db.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sumSql)) {
			setParams(ps, q.params());
			ResultSet rs = ps.executeQuery();
			return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
		} finally {
			db.releaseConnection(conn);
		}
	}

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

			String[] split = f.getNoteSearch().split(";");

//			log.debug("split words count : {}", split.length);
//			log.debug("split words : {}", Arrays.toString(split));

			if (split.length > 0)
				sql.append(" AND (");

			for (int i = 0; i < split.length; i++) {
				String searchWord = split[i].trim();
				String like = "%" + searchWord + "%";
				if (i > 0)
					sql.append(" OR ");

				sql.append("""
						 (t.note ILIKE ?
						      OR EXISTS (
						        SELECT 1 FROM transaction_custom_values tcv
						        WHERE tcv.transaction_id = t.id AND tcv.value ILIKE ?
						      ))
						""");
				params.add(like);
				params.add(like);
			}

			sql.append(" )");

		}
		if (!countOnly) {
			String col = resolveSortColumn(f.getSortBy());
			String dir = "asc".equalsIgnoreCase(f.getSortDir()) ? "ASC" : "DESC";
			sql.append(" ORDER BY ").append(col).append(" ").append(dir).append(", t.id ").append(dir); 
			// tiebreaker for equal values
			if (f.getPageSize() < Integer.MAX_VALUE) {
				sql.append(" LIMIT ? OFFSET ?");
				params.add(f.getPageSize());
				params.add((f.getPage() - 1) * f.getPageSize());
			}
		}

//		log.debug("params : {}", params);
//		log.debug("sql : {}", sql.toString());
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

	private String resolveSortColumn(String key) {
		return SORT_COLUMNS.getOrDefault(key, "t.txn_datetime");
	}

}