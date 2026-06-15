package com.expensemanager.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.expensemanager.model.Budget;
import com.expensemanager.model.Budget.Period;
import com.expensemanager.util.DBConnection;

public class BudgetDAO {

    // ── DDL ───────────────────────────────────────────────────────────────────
    public void createTablesIfNotExist() throws SQLException {
        String sql =
            "CREATE TABLE IF NOT EXISTS budgets (" +
            "  id           SERIAL PRIMARY KEY," +
            "  category     VARCHAR(100)  NOT NULL," +
            "  amount       DECIMAL(15,2) NOT NULL," +
            "  period       VARCHAR(10)   NOT NULL DEFAULT 'MONTHLY'," +
            "  alert_at_pct INT           NOT NULL DEFAULT 80," +
            "  year         INT           NOT NULL," +
            "  month        INT," +
            "  is_active    BOOLEAN       NOT NULL DEFAULT TRUE," +
            "  created_at   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP," +
            "  UNIQUE(category, period, year, month)" +
            ");" +
            "CREATE TABLE IF NOT EXISTS budget_alerts (" +
            "  id         SERIAL PRIMARY KEY," +
            "  budget_id  INT NOT NULL REFERENCES budgets(id) ON DELETE CASCADE," +
            "  spent_pct  DECIMAL(5,2) NOT NULL," +
            "  spent_amt  DECIMAL(15,2) NOT NULL," +
            "  alerted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ");" +
            "CREATE INDEX IF NOT EXISTS idx_budget_period ON budgets(year, month);" +
            "CREATE INDEX IF NOT EXISTS idx_budget_active ON budgets(is_active);";
        try (Connection con = DBConnection.getInstance().getConnection();
             Statement  st  = con.createStatement()) {
            st.execute(sql);
        }
    }

    // ── SAVE ──────────────────────────────────────────────────────────────────
    public int save(Budget b) throws SQLException {
        String sql =
            "INSERT INTO budgets (category,amount,period,alert_at_pct,year,month,is_active) " +
            "VALUES (?,?,?,?,?,?,?) " +
            "ON CONFLICT (category,period,year,month) DO UPDATE " +
            "SET amount=EXCLUDED.amount, alert_at_pct=EXCLUDED.alert_at_pct, is_active=true " +
            "RETURNING id";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString (1, b.getCategory());
            ps.setBigDecimal(2, b.getAmount());
            ps.setString (3, b.getPeriod().name());
            ps.setInt    (4, b.getAlertAtPct());
            ps.setInt    (5, b.getYear());
            if (b.getMonth() != null) ps.setInt(6, b.getMonth());
            else                      ps.setNull(6, Types.INTEGER);
            ps.setBoolean(7, b.isActive());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public boolean update(Budget b) throws SQLException {
        String sql =
            "UPDATE budgets SET category=?,amount=?,period=?,alert_at_pct=?,year=?,month=?,is_active=? WHERE id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString (1, b.getCategory());
            ps.setBigDecimal(2, b.getAmount());
            ps.setString (3, b.getPeriod().name());
            ps.setInt    (4, b.getAlertAtPct());
            ps.setInt    (5, b.getYear());
            if (b.getMonth() != null) ps.setInt(6, b.getMonth()); else ps.setNull(6, Types.INTEGER);
            ps.setBoolean(7, b.isActive());
            ps.setInt    (8, b.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM budgets WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }

    // ── GET with spent amount joined ───────────────────────────────────────────
    public List<Budget> getBudgetsWithSpent(int year, int month) throws SQLException {
        String sql =
            "SELECT b.*, " +
            "  COALESCE(SUM(e.amount), 0) AS spent " +
            "FROM budgets b " +
            "LEFT JOIN expense e " +
            "  ON e.category = b.category " +
            "  AND EXTRACT(YEAR  FROM e.transaction_date) = b.year " +
            "  AND EXTRACT(MONTH FROM e.transaction_date) = b.month " +
            "WHERE b.is_active = true AND b.year = ? AND b.month = ? " +
            "GROUP BY b.id " +
            "ORDER BY (COALESCE(SUM(e.amount),0) / b.amount) DESC";
        List<Budget> list = new ArrayList<>();
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs, true));
        }
        return list;
    }

    public Budget getById(int id) throws SQLException {
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM budgets WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs, false) : null;
        }
    }

    // ── Budgets that have crossed alert threshold (for SSE/scheduler) ──────────
    public List<Budget> getAlertBudgets(int year, int month) throws SQLException {
        String sql =
            "SELECT b.*, COALESCE(SUM(e.amount),0) AS spent " +
            "FROM budgets b " +
            "LEFT JOIN expense e " +
            "  ON e.category = b.category " +
            "  AND EXTRACT(YEAR  FROM e.transaction_date) = b.year " +
            "  AND EXTRACT(MONTH FROM e.transaction_date) = b.month " +
            "WHERE b.is_active = true AND b.year = ? AND b.month = ? " +
            "GROUP BY b.id " +
            "HAVING COALESCE(SUM(e.amount),0) >= b.amount * b.alert_at_pct / 100";
        List<Budget> list = new ArrayList<>();
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, year); ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs, true));
        }
        return list;
    }

    public void logAlert(int budgetId, double spentPct, BigDecimal spentAmt) throws SQLException {
        String sql = "INSERT INTO budget_alerts (budget_id,spent_pct,spent_amt) VALUES (?,?,?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, budgetId);
            ps.setDouble(2, spentPct);
            ps.setBigDecimal(3, spentAmt);
            ps.executeUpdate();
        }
    }

    public boolean alertedTodayFor(int budgetId) throws SQLException {
        String sql = "SELECT 1 FROM budget_alerts WHERE budget_id=? AND DATE(alerted_at)=CURRENT_DATE LIMIT 1";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, budgetId);
            return ps.executeQuery().next();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TREND ANALYSIS QUERIES
    // ══════════════════════════════════════════════════════════════════════════

    /** Monthly income vs expense for last N months */
    public List<Map<String,Object>> getMonthlyTrend(int months) throws SQLException {
        String sql =
            "WITH months AS (" +
            "  SELECT generate_series(1, ?) AS n" +
            "), period AS (" +
            "  SELECT " +
            "    DATE_TRUNC('month', NOW() - ((n-1) || ' months')::INTERVAL) AS month_start," +
            "    EXTRACT(YEAR  FROM NOW() - ((n-1) || ' months')::INTERVAL)::INT AS yr," +
            "    EXTRACT(MONTH FROM NOW() - ((n-1) || ' months')::INTERVAL)::INT AS mo" +
            "  FROM months" +
            ")" +
            "SELECT " +
            "  p.yr, p.mo," +
            "  TO_CHAR(p.month_start,'Mon YYYY') AS label," +
            "  COALESCE(i.total,0) AS income," +
            "  COALESCE(e.total,0) AS expense," +
            "  COALESCE(i.total,0) - COALESCE(e.total,0) AS savings" +
            " FROM period p" +
            " LEFT JOIN (SELECT EXTRACT(YEAR FROM transaction_date)::INT yr, EXTRACT(MONTH FROM transaction_date)::INT mo, SUM(amount) total FROM income  GROUP BY yr,mo) i ON i.yr=p.yr AND i.mo=p.mo" +
            " LEFT JOIN (SELECT EXTRACT(YEAR FROM transaction_date)::INT yr, EXTRACT(MONTH FROM transaction_date)::INT mo, SUM(amount) total FROM expense GROUP BY yr,mo) e ON e.yr=p.yr AND e.mo=p.mo" +
            " ORDER BY p.yr ASC, p.mo ASC";
        List<Map<String,Object>> list = new ArrayList<>();
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, months);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("yr",      rs.getInt("yr"));
                row.put("mo",      rs.getInt("mo"));
                row.put("label",   rs.getString("label"));
                row.put("income",  rs.getBigDecimal("income"));
                row.put("expense", rs.getBigDecimal("expense"));
                row.put("savings", rs.getBigDecimal("savings"));
                list.add(row);
            }
        }
        return list;
    }

    /** Category-wise spending for last N months (heatmap data) */
    public List<Map<String,Object>> getCategoryTrend(int months) throws SQLException {
        String sql =
            "SELECT " +
            "  category," +
            "  EXTRACT(YEAR  FROM transaction_date)::INT AS yr," +
            "  EXTRACT(MONTH FROM transaction_date)::INT AS mo," +
            "  TO_CHAR(DATE_TRUNC('month',transaction_date),'Mon YY') AS label," +
            "  SUM(amount) AS total, COUNT(*) AS cnt" +
            " FROM expense" +
            " WHERE transaction_date >= DATE_TRUNC('month', NOW() - (? || ' months')::INTERVAL)" +
            " GROUP BY category, yr, mo, label" +
            " ORDER BY category, yr, mo";
        List<Map<String,Object>> list = new ArrayList<>();
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, months - 1);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("category", rs.getString("category"));
                row.put("yr",       rs.getInt("yr"));
                row.put("mo",       rs.getInt("mo"));
                row.put("label",    rs.getString("label"));
                row.put("total",    rs.getBigDecimal("total"));
                row.put("cnt",      rs.getInt("cnt"));
                list.add(row);
            }
        }
        return list;
    }

    /** Year-over-year comparison: this year vs last year per month */
    public List<Map<String,Object>> getYearOverYear(int year) throws SQLException {
        String sql =
            "SELECT mo," +
            "  TO_CHAR(TO_DATE(mo::TEXT,'MM'),'Mon') AS label," +
            "  SUM(CASE WHEN yr=?   THEN total ELSE 0 END) AS this_year," +
            "  SUM(CASE WHEN yr=?-1 THEN total ELSE 0 END) AS last_year" +
            " FROM (" +
            "  SELECT EXTRACT(YEAR FROM transaction_date)::INT yr," +
            "         EXTRACT(MONTH FROM transaction_date)::INT mo," +
            "         SUM(amount) total" +
            "  FROM expense" +
            "  WHERE EXTRACT(YEAR FROM transaction_date) IN (?,?-1)" +
            "  GROUP BY yr,mo" +
            " ) t" +
            " GROUP BY mo, label ORDER BY mo";
        List<Map<String,Object>> list = new ArrayList<>();
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1,year); ps.setInt(2,year); ps.setInt(3,year); ps.setInt(4,year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("mo",        rs.getInt("mo"));
                row.put("label",     rs.getString("label"));
                row.put("thisYear",  rs.getBigDecimal("this_year"));
                row.put("lastYear",  rs.getBigDecimal("last_year"));
                list.add(row);
            }
        }
        return list;
    }

    /** Top spending categories with % change vs previous month */
    public List<Map<String,Object>> getCategoryGrowth(int year, int month) throws SQLException {
        int prevMonth = month == 1 ? 12 : month - 1;
        int prevYear  = month == 1 ? year - 1 : year;
        String sql =
            "SELECT " +
            "  COALESCE(c.category, p.category) AS category," +
            "  COALESCE(c.total, 0) AS curr_total," +
            "  COALESCE(p.total, 0) AS prev_total," +
            "  CASE WHEN COALESCE(p.total,0) = 0 THEN NULL" +
            "       ELSE ROUND((COALESCE(c.total,0) - COALESCE(p.total,0)) / p.total * 100, 1) END AS pct_change" +
            " FROM" +
            "  (SELECT category, SUM(amount) total FROM expense" +
            "   WHERE EXTRACT(YEAR FROM transaction_date)=? AND EXTRACT(MONTH FROM transaction_date)=?" +
            "   GROUP BY category) c" +
            " FULL OUTER JOIN" +
            "  (SELECT category, SUM(amount) total FROM expense" +
            "   WHERE EXTRACT(YEAR FROM transaction_date)=? AND EXTRACT(MONTH FROM transaction_date)=?" +
            "   GROUP BY category) p ON c.category=p.category" +
            " ORDER BY curr_total DESC LIMIT 8";
        List<Map<String,Object>> list = new ArrayList<>();
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1,year); ps.setInt(2,month); ps.setInt(3,prevYear); ps.setInt(4,prevMonth);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("category",   rs.getString("category"));
                row.put("currTotal",  rs.getBigDecimal("curr_total"));
                row.put("prevTotal",  rs.getBigDecimal("prev_total"));
                row.put("pctChange",  rs.getObject("pct_change"));
                list.add(row);
            }
        }
        return list;
    }

    /** Daily spending for current month (sparkline) */
    public List<Map<String,Object>> getDailySpending(int year, int month) throws SQLException {
        String sql =
            "SELECT EXTRACT(DAY FROM transaction_date)::INT AS day, SUM(amount) AS total" +
            " FROM expense" +
            " WHERE EXTRACT(YEAR FROM transaction_date)=? AND EXTRACT(MONTH FROM transaction_date)=?" +
            " GROUP BY day ORDER BY day";
        List<Map<String,Object>> list = new ArrayList<>();
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1,year); ps.setInt(2,month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("day",   rs.getInt("day"));
                row.put("total", rs.getBigDecimal("total"));
                list.add(row);
            }
        }
        return list;
    }

    // ── Private mapper ────────────────────────────────────────────────────────
    private Budget mapRow(ResultSet rs, boolean hasSpent) throws SQLException {
        Budget b = new Budget();
        b.setId        (rs.getInt      ("id"));
        b.setCategory  (rs.getString   ("category"));
        b.setAmount    (rs.getBigDecimal("amount"));
        b.setAlertAtPct(rs.getInt      ("alert_at_pct"));
        b.setYear      (rs.getInt      ("year"));
        b.setActive    (rs.getBoolean  ("is_active"));
        int mo = rs.getInt("month"); b.setMonth(rs.wasNull() ? null : mo);
        try { b.setPeriod(Period.valueOf(rs.getString("period"))); }
        catch (Exception e) { b.setPeriod(Period.MONTHLY); }
        if (hasSpent) b.setSpent(rs.getBigDecimal("spent"));
        return b;
    }
}
