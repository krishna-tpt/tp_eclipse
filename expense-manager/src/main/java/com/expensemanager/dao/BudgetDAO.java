package com.expensemanager.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.model.Budget;
import com.expensemanager.model.BudgetCategory;
import com.expensemanager.util.DBConnection;

public class BudgetDAO {

    private static final Logger log = LoggerFactory.getLogger(BudgetDAO.class);
    private final DBConnection db = DBConnection.getInstance();

    // ── Upsert month budget ────────────────────────────────────────
    public int upsert(Budget b) throws SQLException {
        String sql = """
            INSERT INTO budgets (book_id, year, month, overall_limit, updated_at)
            VALUES (?, ?, ?, ?, NOW())
            ON CONFLICT (book_id, year, month)
            DO UPDATE SET overall_limit = EXCLUDED.overall_limit, updated_at = NOW()
            RETURNING id
            """;
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, b.getBookId());
            ps.setInt(2, b.getYear());
            ps.setInt(3, b.getMonth());
            ps.setBigDecimal(4, b.getOverallLimit());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;
        } finally {
            db.releaseConnection(conn);
        }
    }

    // ── Upsert category budget ─────────────────────────────────────
    public void upsertCategory(BudgetCategory bc) throws SQLException {
        String sql = """
            INSERT INTO budget_categories (budget_id, category_id, cat_limit, alert_pct)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (budget_id, category_id)
            DO UPDATE SET cat_limit = EXCLUDED.cat_limit, alert_pct = EXCLUDED.alert_pct
            """;
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bc.getBudgetId());
            ps.setInt(2, bc.getCategoryId());
            ps.setBigDecimal(3, bc.getCatLimit());
            ps.setInt(4, bc.getAlertPct());
            ps.executeUpdate();
        } finally {
            db.releaseConnection(conn);
        }
    }

    // ── Delete category budget row ─────────────────────────────────
    public void deleteCategory(int budgetId, int categoryId) throws SQLException {
        String sql = "DELETE FROM budget_categories WHERE budget_id=? AND category_id=?";
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, budgetId);
            ps.setInt(2, categoryId);
            ps.executeUpdate();
        } finally {
            db.releaseConnection(conn);
        }
    }

    // ── Find budget for a specific month (with category rows + spent) ──
    public Budget findByMonth(int bookId, int year, int month) throws SQLException {
        String sql = """
            SELECT id, book_id, year, month, overall_limit, created_at, updated_at
            FROM budgets
            WHERE book_id=? AND year=? AND month=?
            """;
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, year);
            ps.setInt(3, month);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            Budget b = mapBudget(rs);
            b.setCategories(loadCategories(conn, b.getId(), year, month));
            b.setTotalSpent(loadMonthExpense(conn, bookId, year, month, 0));
            BigDecimal remaining = b.getOverallLimit().subtract(
                b.getTotalSpent() == null ? BigDecimal.ZERO : b.getTotalSpent());
            b.setRemainingAmount(remaining);
            return b;
        } finally {
            db.releaseConnection(conn);
        }
    }

    // ── List all budgets for a book (summary, no category detail) ──
    public List<Budget> listByBook(int bookId) throws SQLException {
        String sql = """
            SELECT b.id, b.book_id, b.year, b.month, b.overall_limit,
                   b.created_at, b.updated_at,
                   COALESCE(SUM(t.amount),0) AS total_spent
            FROM budgets b
            LEFT JOIN transactions t
                   ON t.book_id = b.book_id
                  AND t.type = 'EXPENSE'::txn_type
                  AND EXTRACT(YEAR  FROM t.txn_datetime) = b.year
                  AND EXTRACT(MONTH FROM t.txn_datetime) = b.month
            WHERE b.book_id = ?
            GROUP BY b.id
            ORDER BY b.year DESC, b.month DESC
            """;
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            List<Budget> list = new ArrayList<>();
            while (rs.next()) {
                Budget b = mapBudget(rs);
                b.setTotalSpent(rs.getBigDecimal("total_spent"));
                BigDecimal rem = b.getOverallLimit().subtract(b.getTotalSpent());
                b.setRemainingAmount(rem);
                list.add(b);
            }
            return list;
        } finally {
            db.releaseConnection(conn);
        }
    }

    // ── Dashboard: current month budget with alerts ─────────────────
    public Budget currentMonthBudget(int bookId) throws SQLException {
        java.time.LocalDate now = java.time.LocalDate.now();
        return findByMonth(bookId, now.getYear(), now.getMonthValue());
    }

    // ── Trend: monthly income+expense for last N months ────────────
    public List<java.util.Map<String, Object>> monthlyTrend(int bookId, int months) throws SQLException {
        String sql = """
            SELECT
                EXTRACT(YEAR  FROM txn_datetime)::INT AS yr,
                EXTRACT(MONTH FROM txn_datetime)::INT AS mo,
                SUM(CASE WHEN type='INCOME'::txn_type  THEN amount ELSE 0 END) AS income,
                SUM(CASE WHEN type='EXPENSE'::txn_type THEN amount ELSE 0 END) AS expense
            FROM transactions
            WHERE book_id = ?
              AND txn_datetime >= NOW() - (?::INT || ' months')::INTERVAL
            GROUP BY yr, mo
            ORDER BY yr ASC, mo ASC
            """;
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, months);
            ResultSet rs = ps.executeQuery();
            List<java.util.Map<String, Object>> rows = new ArrayList<>();
            while (rs.next()) {
                java.util.Map<String, Object> row = new java.util.LinkedHashMap<>();
                int yr = rs.getInt("yr");
                int mo = rs.getInt("mo");
                row.put("yr",      yr);
                row.put("mo",      mo);
                row.put("label",   java.time.Month.of(mo).getDisplayName(
                                       java.time.format.TextStyle.SHORT,
                                       java.util.Locale.ENGLISH) + " " + yr);
                row.put("income",  rs.getBigDecimal("income"));
                row.put("expense", rs.getBigDecimal("expense"));
                row.put("net",     rs.getBigDecimal("income")
                                     .subtract(rs.getBigDecimal("expense")));
                rows.add(row);
            }
            return rows;
        } finally {
            db.releaseConnection(conn);
        }
    }

    // ── Trend: category-wise monthly breakdown ──────────────────────
    public List<java.util.Map<String, Object>> categoryTrend(int bookId, int months) throws SQLException {
        String sql = """
            SELECT
                EXTRACT(YEAR  FROM t.txn_datetime)::INT AS yr,
                EXTRACT(MONTH FROM t.txn_datetime)::INT AS mo,
                c.name  AS category,
                SUM(t.amount) AS total
            FROM transactions t
            JOIN categories c ON c.id = t.category_id
            WHERE t.book_id = ?
              AND t.type = 'EXPENSE'::txn_type
              AND t.txn_datetime >= NOW() - (?::INT || ' months')::INTERVAL
            GROUP BY yr, mo, c.name
            ORDER BY yr ASC, mo ASC, total DESC
            """;
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, months);
            ResultSet rs = ps.executeQuery();
            List<java.util.Map<String, Object>> rows = new ArrayList<>();
            while (rs.next()) {
                java.util.Map<String, Object> row = new java.util.LinkedHashMap<>();
                int mo = rs.getInt("mo");
                int yr = rs.getInt("yr");
                row.put("yr",       yr);
                row.put("mo",       mo);
                row.put("label",    java.time.Month.of(mo).getDisplayName(
                                        java.time.format.TextStyle.SHORT,
                                        java.util.Locale.ENGLISH) + " " + yr);
                row.put("category", rs.getString("category"));
                row.put("total",    rs.getBigDecimal("total"));
                rows.add(row);
            }
            return rows;
        } finally {
            db.releaseConnection(conn);
        }
    }

    // ── Year-over-year: same months across years ────────────────────
    public List<java.util.Map<String, Object>> yearOverYear(int bookId) throws SQLException {
        String sql = """
            SELECT
                EXTRACT(YEAR  FROM txn_datetime)::INT AS yr,
                EXTRACT(MONTH FROM txn_datetime)::INT AS mo,
                SUM(CASE WHEN type='INCOME'::txn_type  THEN amount ELSE 0 END) AS income,
                SUM(CASE WHEN type='EXPENSE'::txn_type THEN amount ELSE 0 END) AS expense
            FROM transactions
            WHERE book_id = ?
            GROUP BY yr, mo
            ORDER BY mo ASC, yr ASC
            """;
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            List<java.util.Map<String, Object>> rows = new ArrayList<>();
            while (rs.next()) {
                java.util.Map<String, Object> row = new java.util.LinkedHashMap<>();
                row.put("yr",      rs.getInt("yr"));
                row.put("mo",      rs.getInt("mo"));
                row.put("income",  rs.getBigDecimal("income"));
                row.put("expense", rs.getBigDecimal("expense"));
                rows.add(row);
            }
            return rows;
        } finally {
            db.releaseConnection(conn);
        }
    }

    // ── Helpers ────────────────────────────────────────────────────
    private Budget mapBudget(ResultSet rs) throws SQLException {
        Budget b = new Budget();
        b.setId(rs.getInt("id"));
        b.setBookId(rs.getInt("book_id"));
        b.setYear(rs.getInt("year"));
        b.setMonth(rs.getInt("month"));
        b.setOverallLimit(rs.getBigDecimal("overall_limit"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) b.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) b.setUpdatedAt(ua.toLocalDateTime());
        return b;
    }

    private List<BudgetCategory> loadCategories(Connection conn, int budgetId,
                                                 int year, int month) throws SQLException {
        String sql = """
            SELECT bc.id, bc.budget_id, bc.category_id, bc.cat_limit, bc.alert_pct,
                   c.name AS cat_name,
                   COALESCE(SUM(t.amount),0) AS spent
            FROM budget_categories bc
            JOIN categories c ON c.id = bc.category_id
            LEFT JOIN transactions t
                   ON t.category_id = bc.category_id
                  AND t.type = 'EXPENSE'::txn_type
                  AND EXTRACT(YEAR  FROM t.txn_datetime) = ?
                  AND EXTRACT(MONTH FROM t.txn_datetime) = ?
            WHERE bc.budget_id = ?
            GROUP BY bc.id, bc.budget_id, bc.category_id, bc.cat_limit,
                     bc.alert_pct, c.name
            ORDER BY c.name
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ps.setInt(3, budgetId);
            ResultSet rs = ps.executeQuery();
            List<BudgetCategory> list = new ArrayList<>();
            while (rs.next()) {
                BudgetCategory bc = new BudgetCategory();
                bc.setId(rs.getInt("id"));
                bc.setBudgetId(rs.getInt("budget_id"));
                bc.setCategoryId(rs.getInt("category_id"));
                bc.setCategoryName(rs.getString("cat_name"));
                bc.setCatLimit(rs.getBigDecimal("cat_limit"));
                bc.setAlertPct(rs.getInt("alert_pct"));
                BigDecimal spent = rs.getBigDecimal("spent");
                bc.setSpent(spent);
                bc.setRemaining(bc.getCatLimit().subtract(spent));
                list.add(bc);
            }
            return list;
        }
    }

    private BigDecimal loadMonthExpense(Connection conn, int bookId,
                                         int year, int month, int catId) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(amount),0)
            FROM transactions
            WHERE book_id=? AND type='EXPENSE'::txn_type
              AND EXTRACT(YEAR  FROM txn_datetime)=?
              AND EXTRACT(MONTH FROM txn_datetime)=?
            """ + (catId > 0 ? " AND category_id=?" : "");
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, year);
            ps.setInt(3, month);
            if (catId > 0) ps.setInt(4, catId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }
}