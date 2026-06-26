package com.expensemanager.servlet;

import java.io.IOException;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.dao.TransactionDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(ReportServlet.class);
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int bookId = (Integer) req.getSession().getAttribute("activeBookId");

		// ── Month selector (default = current month) ──────────────
		LocalDate now = LocalDate.now();
		int selYear, selMonth;
		try {
			selYear = Integer.parseInt(req.getParameter("year"));
			selMonth = Integer.parseInt(req.getParameter("month"));
		} catch (Exception e) {
			selYear = now.getYear();
			selMonth = now.getMonthValue();
		}

		try {
			TransactionDAO dao = new TransactionDAO();

			// ── Overall totals (all time) ──────────────────────────
			var income = dao.sumByType("INCOME", bookId);
			var expense = dao.sumByType("EXPENSE", bookId);
			req.setAttribute("totalIncome", income);
			req.setAttribute("totalExpense", expense);
			req.setAttribute("balance", income.subtract(expense));

			// ── Monthly trend (last 12 months) ─────────────────────
			req.setAttribute("monthlyJson", mapper.writeValueAsString(dao.monthlyTrend(12, bookId)));

			// ── All-time category breakdown ────────────────────────
			req.setAttribute("expCatJson", mapper.writeValueAsString(dao.expenseByCategory(bookId)));
			req.setAttribute("incCatJson", mapper.writeValueAsString(dao.incomeByCategory(bookId)));

			// ── Selected month stats ───────────────────────────────
			req.setAttribute("monthSummary", dao.monthSummary(selYear, selMonth, bookId));

			req.setAttribute("dailyJson", mapper.writeValueAsString(dao.dailyTotals(selYear, selMonth, bookId)));

			req.setAttribute("expCatMonthJson",
					mapper.writeValueAsString(dao.categoryBreakdownByMonth("EXPENSE", selYear, selMonth, bookId)));

			req.setAttribute("incCatMonthJson",
					mapper.writeValueAsString(dao.categoryBreakdownByMonth("INCOME", selYear, selMonth, bookId)));

			req.setAttribute("expSubCatJson",
					mapper.writeValueAsString(dao.subCategoryBreakdownByMonth("EXPENSE", selYear, selMonth, bookId)));

			req.setAttribute("incSubCatJson",
					mapper.writeValueAsString(dao.subCategoryBreakdownByMonth("INCOME", selYear, selMonth, bookId)));

			req.setAttribute("dowJson", mapper.writeValueAsString(dao.dayOfWeekPattern(selYear, selMonth, bookId)));

			req.setAttribute("weeklyJson", mapper.writeValueAsString(dao.weeklyTotals(selYear, selMonth, bookId)));

		} catch (Exception e) {
			log.error("ReportServlet error: {}", e.getMessage(), e);
			req.setAttribute("dbError", e.getMessage());
		}

		req.setAttribute("selYear", selYear);
		req.setAttribute("selMonth", selMonth);
		req.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(req, resp);
	}
}
