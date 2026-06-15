package com.expensemanager.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.dao.BudgetDAO;
import com.expensemanager.model.Budget;
import com.expensemanager.model.Budget.Period;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GET /budget/list – budget overview page GET /budget/trends – trend analysis
 * page GET /budget/add – add form GET /budget/edit?id=N – edit form GET
 * /budget/delete?id=N – delete POST /budget/save – create/update GET
 * /budget/api/trend?months=N – JSON monthly trend GET
 * /budget/api/category?months=N – JSON category trend GET
 * /budget/api/yoy?year=N – JSON year-over-year GET /budget/api/daily – JSON
 * daily spending this month GET /budget/api/growth – JSON category growth vs
 * prev month
 */
@WebServlet("/budget/*")
public class BudgetServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(BudgetServlet.class);
	private final BudgetDAO dao = new BudgetDAO();
	private final Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String path = req.getPathInfo();
		if (path == null || path.equals("/"))
			path = "/list";

		// JSON API endpoints
		if (path.startsWith("/api/")) {
			handleApi(path, req, res);
			return;
		}

		switch (path) {
		case "/list":
			showList(req, res);
			break;
		case "/trends":
			showTrends(req, res);
			break;
		case "/add":
			showForm(req, res, null);
			break;
		case "/edit":
			try {
				showForm(req, res, dao.getById(intParam(req, "id")));
			} catch (Exception e) {
				log.debug("doGet method --> edit : {}", e.getMessage());
				error(req, res, e.getMessage());
				res.sendRedirect(req.getContextPath() + "/budget/list");
			}
			break;
		case "/delete":
			try {
				dao.delete(intParam(req, "id"));
				success(req, res, "Budget deleted.");
			} catch (Exception e) {
				log.debug("doGet method --> delete : {}", e.getMessage());
				error(req, res, e.getMessage());
			}
			res.sendRedirect(req.getContextPath() + "/budget/list");
			break;
		default:
			showList(req, res);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		try {
			Budget b = buildFromRequest(req);
			if (b.getId() == 0) {
				dao.save(b);
				success(req, res, "✅ Budget created!");
			} else {
				dao.update(b);
				success(req, res, "✅ Budget updated!");
			}
		} catch (Exception e) {
			log.debug("doPost method : {}", e.getMessage());
			error(req, res, "Save failed: " + e.getMessage());
		}
		res.sendRedirect(req.getContextPath() + "/budget/list");
	}

	// ── Page handlers ─────────────────────────────────────────────────────────

	private void showList(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			LocalDate now = LocalDate.now();
			int year = getIntParam(req, "year", now.getYear());
			int month = getIntParam(req, "month", now.getMonthValue());

			List<Budget> budgets = dao.getBudgetsWithSpent(year, month);

			// Summary stats
			BigDecimal totalBudget = budgets.stream().map(Budget::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal totalSpent = budgets.stream().map(b -> b.getSpent() != null ? b.getSpent() : BigDecimal.ZERO)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			long overCount = budgets.stream().filter(Budget::isOverBudget).count();
			long alertCount = budgets.stream().filter(Budget::isAtAlert).count();

			req.setAttribute("budgets", budgets);
			req.setAttribute("totalBudget", totalBudget);
			req.setAttribute("totalSpent", totalSpent);
			req.setAttribute("overCount", overCount);
			req.setAttribute("alertCount", alertCount);
			req.setAttribute("selectedYear", year);
			req.setAttribute("selectedMonth", month);
			req.setAttribute("activePage", "budget");
			req.setAttribute("pageTitle", "Budget Management");
			req.getRequestDispatcher("/WEB-INF/jsp/budget.jsp").forward(req, res);
		} catch (Exception e) {
			log.debug("showList method : {}", e.getMessage());
			throw new ServletException(e);
		}
	}

	private void showTrends(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			LocalDate now = LocalDate.now();
			int year = getIntParam(req, "year", now.getYear());
			int months = getIntParam(req, "months", 12);

			req.setAttribute("selectedYear", year);
			req.setAttribute("selectedMonths", months);
			req.setAttribute("activePage", "budget");
			req.setAttribute("pageTitle", "Trend Analysis");
			req.getRequestDispatcher("/WEB-INF/jsp/trends.jsp").forward(req, res);
		} catch (Exception e) {
			log.debug("showTrends method : {}", e.getMessage());
			throw new ServletException(e);
		}
	}

	private void showForm(HttpServletRequest req, HttpServletResponse res, Budget b)
			throws ServletException, IOException {
		req.setAttribute("budget", b);
		req.setAttribute("periods", Period.values());
		req.setAttribute("activePage", "budget");
		// Pass current month/year as defaults
		LocalDate now = LocalDate.now();
		req.setAttribute("currentYear", now.getYear());
		req.setAttribute("currentMonth", now.getMonthValue());
		req.getRequestDispatcher("/WEB-INF/jsp/budget-form.jsp").forward(req, res);
	}

	// ── JSON API ──────────────────────────────────────────────────────────────

	private void handleApi(String path, HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		try {
			LocalDate now = LocalDate.now();
			Object result;
			switch (path) {
			case "/api/trend":
				int months = getIntParam(req, "months", 12);
				result = dao.getMonthlyTrend(months);
				break;
			case "/api/category":
				int catMonths = getIntParam(req, "months", 6);
				result = dao.getCategoryTrend(catMonths);
				break;
			case "/api/yoy":
				int year = getIntParam(req, "year", now.getYear());
				result = dao.getYearOverYear(year);
				break;
			case "/api/daily":
				int dy = getIntParam(req, "year", now.getYear());
				int dm = getIntParam(req, "month", now.getMonthValue());
				result = dao.getDailySpending(dy, dm);
				break;
			case "/api/growth":
				int gy = getIntParam(req, "year", now.getYear());
				int gm = getIntParam(req, "month", now.getMonthValue());
				result = dao.getCategoryGrowth(gy, gm);
				break;
			default:
				res.setStatus(404);
				res.getWriter().write("{\"error\":\"Not found\"}");
				return;
			}
			res.getWriter().write(gson.toJson(result));
		} catch (Exception e) {
			log.debug("handleApi method : {}", e.getMessage());
			res.setStatus(500);
			res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
		}
	}

	// ── Helpers ───────────────────────────────────────────────────────────────

	private Budget buildFromRequest(HttpServletRequest req) throws Exception {
		Budget b = new Budget();
		String idStr = req.getParameter("id");
		if (idStr != null && !idStr.isEmpty())
			b.setId(Integer.parseInt(idStr));
		b.setCategory(req.getParameter("category"));
		b.setAmount(new BigDecimal(req.getParameter("amount")));
		b.setPeriod(Period.valueOf(req.getParameter("period")));
		b.setAlertAtPct(Integer.parseInt(req.getParameter("alertAtPct")));
		b.setYear(Integer.parseInt(req.getParameter("year")));
		String mo = req.getParameter("month");
		b.setMonth(mo != null && !mo.isEmpty() ? Integer.parseInt(mo) : null);
		b.setActive(true);
		return b;
	}

	private int intParam(HttpServletRequest req, String name) {
		return Integer.parseInt(req.getParameter(name));
	}

	private int getIntParam(HttpServletRequest req, String name, int def) {
		try {
			String v = req.getParameter(name);
			return v != null ? Integer.parseInt(v) : def;
		} catch (Exception e) {
			log.debug("getIntParam method : {}", e.getMessage());
			return def;
		}
	}

	private void success(HttpServletRequest req, HttpServletResponse res, String msg) {
		req.getSession().setAttribute("successMsg", msg);
	}

	private void error(HttpServletRequest req, HttpServletResponse res, String msg) {
		req.getSession().setAttribute("errorMsg", msg);
	}
}
