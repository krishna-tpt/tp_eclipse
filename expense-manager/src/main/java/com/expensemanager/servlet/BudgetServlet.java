package com.expensemanager.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.dao.BudgetDAO;
import com.expensemanager.dao.CategoryDAO;
import com.expensemanager.model.Budget;
import com.expensemanager.model.BudgetCategory;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/budget")
public class BudgetServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(BudgetServlet.class);
    private final Gson gson = new Gson();

    // ── GET /budget → budget page ──────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int bookId = (Integer) req.getSession().getAttribute("activeBookId");
        BudgetDAO dao = new BudgetDAO();
        CategoryDAO catDAO = new CategoryDAO();

        // Which month to show? default = current
        LocalDate now = LocalDate.now();
        int year  = parseIntParam(req, "year",  now.getYear());
        int month = parseIntParam(req, "month", now.getMonthValue());

        // Tab: budget | trend
        String tab = req.getParameter("tab");
        if (tab == null) tab = "budget";

        try {
            // ── Budget tab ─────────────────────────────────────────
            Budget current = dao.findByMonth(bookId, year, month);
            // Null-safe defaults — JSP fmt:formatNumber crashes on null
            if (current != null) {
                if (current.getTotalSpent()    == null) current.setTotalSpent(java.math.BigDecimal.ZERO);
                if (current.getRemainingAmount()== null) current.setRemainingAmount(current.getOverallLimit());
            }
            req.setAttribute("budget",     current);
            req.setAttribute("allBudgets", dao.listByBook(bookId));
            req.setAttribute("expenseCategories", catDAO.findByType("EXPENSE"));

            // ── Trend tab ──────────────────────────────────────────
            int trendMonths = parseIntParam(req, "trendMonths", 12);
            List<Map<String, Object>> monthly  = dao.monthlyTrend(bookId, trendMonths);
            List<Map<String, Object>> catTrend = dao.categoryTrend(bookId, trendMonths);
            List<Map<String, Object>> yoy      = dao.yearOverYear(bookId);

            // Pass as JSON strings for Chart.js
            req.setAttribute("monthlyTrendJson",  gson.toJson(monthly));
            req.setAttribute("catTrendJson",       gson.toJson(catTrend));
            req.setAttribute("yoyJson",            gson.toJson(yoy));
            req.setAttribute("trendMonths",        trendMonths);

        } catch (Exception e) {
            log.error("BudgetServlet GET error: {}", e.getMessage(), e);
            req.setAttribute("dbError", e.getMessage());
        }

        req.setAttribute("pageTitle",  "Budget & Trends");
        req.setAttribute("activePage", "budget");
        req.setAttribute("selYear",    year);
        req.setAttribute("selMonth",   month);
        req.setAttribute("tab",        tab);
        req.getRequestDispatcher("/WEB-INF/views/budget.jsp").forward(req, resp);
    }

    // ── POST /budget → save budget ─────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        int bookId = (Integer) req.getSession().getAttribute("activeBookId");
        BudgetDAO dao = new BudgetDAO();

        try {
            int year  = Integer.parseInt(req.getParameter("year"));
            int month = Integer.parseInt(req.getParameter("month"));
            BigDecimal overallLimit = new BigDecimal(req.getParameter("overallLimit"));

            Budget b = new Budget();
            b.setBookId(bookId);
            b.setYear(year);
            b.setMonth(month);
            b.setOverallLimit(overallLimit);
            int budgetId = dao.upsert(b);

            // Save category budgets
            String[] catIds   = req.getParameterValues("catId");
            String[] catLimits= req.getParameterValues("catLimit");
            String[] alertPcts= req.getParameterValues("alertPct");

            if (catIds != null) {
                for (int i = 0; i < catIds.length; i++) {
                    String limitStr = (catLimits != null && i < catLimits.length)
                                      ? catLimits[i] : "0";
                    if (limitStr == null || limitStr.isBlank()) continue;

                    BudgetCategory bc = new BudgetCategory();
                    bc.setBudgetId(budgetId);
                    bc.setCategoryId(Integer.parseInt(catIds[i]));
                    bc.setCatLimit(new BigDecimal(limitStr));
                    int ap = 80;
                    try { if (alertPcts != null && i < alertPcts.length)
                              ap = Integer.parseInt(alertPcts[i]); } catch (Exception ignored) {}
                    bc.setAlertPct(ap);
                    dao.upsertCategory(bc);
                }
            }

            resp.sendRedirect(req.getContextPath()
                    + "/budget?year=" + year + "&month=" + month + "&success=saved");

        } catch (Exception e) {
            log.error("BudgetServlet POST error: {}", e.getMessage(), e);
            resp.sendRedirect(req.getContextPath() + "/budget?error=" +
                    java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    private int parseIntParam(HttpServletRequest req, String name, int def) {
        try {
            String v = req.getParameter(name);
            return (v != null && !v.isBlank()) ? Integer.parseInt(v) : def;
        } catch (Exception e) { return def; }
    }
}