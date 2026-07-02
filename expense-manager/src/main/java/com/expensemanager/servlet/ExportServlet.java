package com.expensemanager.servlet;

import com.expensemanager.dao.*;
import com.expensemanager.model.*;
import com.expensemanager.service.ExportService;
import com.expensemanager.service.GmailService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GET /export?type=pdf|excel -> full book download GET
 * /export?type=pdf&filtered=1&... -> filtered transactions download GET
 * /export?type=reports-pdf[&year=&month=] -> FULL reports PDF (matches
 * reports.jsp) GET /export?type=calendar-pdf&year=&month= -> calendar PDF POST
 * /export action=email[&reportEmail=1] -> send via Gmail (transactions OR full
 * report)
 */
@WebServlet("/export")
public class ExportServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(ExportServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		log.debug("doGet method");

		int bookId = (Integer) req.getSession().getAttribute("activeBookId");
		String type = req.getParameter("type");
		log.debug("type : {}", type);

		try {
			CashBookDAO cashDAO = new CashBookDAO();
			TransactionDAO txnDAO = new TransactionDAO();
			ExportService exporter = new ExportService();
			CashBook book = cashDAO.findById(bookId);
			String safe = book.getName().replaceAll("[^a-zA-Z0-9_-]", "_");

			// ── FULL Reports PDF (matches reports.jsp page exactly) ──
			if ("reports-pdf".equals(type)) {
				byte[] bytes = buildFullReportsPdf(exporter, txnDAO, book, bookId, req);
				sendFile(resp, bytes, "application/pdf", safe + "_full_report.pdf");
				return;
			}

			// ── Calendar PDF ──────────────────────────────
			if ("calendar-pdf".equals(type)) {
				int year = intParam(req, "year", LocalDate.now().getYear());
				int month = intParam(req, "month", LocalDate.now().getMonthValue());
				List<Map<String, Object>> daily = txnDAO.dailyTotals(year, month, bookId);
				byte[] bytes = exporter.generateCalendarPDF(book, year, month, daily);
				sendFile(resp, bytes, "application/pdf",
						safe + "_calendar_" + year + "_" + String.format("%02d", month) + ".pdf");
				return;
			}

			// ── Filtered or full transactions ─────────────
			boolean filtered = "1".equals(req.getParameter("filtered"));
			List<Transaction> txns;
			BigDecimal income, expense;
			String filterLabel = "";

			if (filtered) {
				TransactionFilter f = parseFilterFromParams(req, bookId);
				f.setPageSize(Integer.MAX_VALUE);
				txns = txnDAO.findByFilter(f);
				income = txns.stream().filter(t -> t.getType() == Transaction.Type.INCOME).map(Transaction::getAmount)
						.reduce(BigDecimal.ZERO, BigDecimal::add);
				expense = txns.stream().filter(t -> t.getType() == Transaction.Type.EXPENSE).map(Transaction::getAmount)
						.reduce(BigDecimal.ZERO, BigDecimal::add);
				filterLabel = buildFilterLabel(req);
			} else {
				txns = txnDAO.findAll(null, 1, Integer.MAX_VALUE, bookId);
				income = txnDAO.sumByType("INCOME", bookId);
				expense = txnDAO.sumByType("EXPENSE", bookId);
			}

			if ("excel".equals(type)) {
				byte[] bytes = exporter.generateExcel(book, txns, income, expense, filterLabel);
				sendFile(resp, bytes, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
						safe + (filtered ? "_filtered" : "") + ".xlsx");
			} else {
				byte[] bytes = exporter.generatePDF(book, txns, income, expense, filterLabel);
				sendFile(resp, bytes, "application/pdf", safe + (filtered ? "_filtered" : "") + ".pdf");
			}

		} catch (Exception e) {
			log.error("Export error: {}", e.getMessage(), e);
			resp.sendRedirect(req.getContextPath() + "/home?exportError=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		log.debug("doPost method");

		req.setCharacterEncoding("UTF-8");
		int bookId = (Integer) req.getSession().getAttribute("activeBookId");
		String to = req.getParameter("email");
		String fmt = req.getParameter("format"); // pdf | excel
		boolean isReportEmail = "1".equals(req.getParameter("reportEmail"));

		if (to == null || to.isBlank()) {
			resp.sendRedirect(req.getContextPath() + "/home?exportError=email_missing");
			return;
		}

		String gmailFrom = getServletContext().getInitParameter("GMAIL_FROM");
		String gmailPass = getServletContext().getInitParameter("GMAIL_APP_PASS");

		try {
			CashBookDAO cashDAO = new CashBookDAO();
			TransactionDAO txnDAO = new TransactionDAO();
			ExportService exporter = new ExportService();
			GmailService gmail = new GmailService(gmailFrom, gmailPass);

			CashBook book = cashDAO.findById(bookId);
			String safe = book.getName().replaceAll("[^a-zA-Z0-9_-]", "_");

			byte[] attachment;
			String attachName;
			String mimeType;
			BigDecimal income, expense;
			int txnCount;

			if (isReportEmail) {
				// ── Full Reports PDF as email attachment ───────
				attachment = buildFullReportsPdf(exporter, txnDAO, book, bookId, req);
				attachName = safe + "_full_report.pdf";
				mimeType = "application/pdf";
				income = txnDAO.sumByType("INCOME", bookId);
				expense = txnDAO.sumByType("EXPENSE", bookId);
				txnCount = txnDAO.findAll(null, 1, Integer.MAX_VALUE, bookId).size();
			} else {
				// ── Standard transaction list export (honors active filter) ───
				boolean filtered = "1".equals(req.getParameter("filteredExport"));
				List<Transaction> txns;
				String filterLabel = null;
				if (filtered) {
					TransactionFilter f = parseFilterFromParams(req, bookId);
					f.setPageSize(Integer.MAX_VALUE);
					txns = txnDAO.findByFilter(f);
					income = txns.stream().filter(t -> t.getType() == Transaction.Type.INCOME)
							.map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
					expense = txns.stream().filter(t -> t.getType() == Transaction.Type.EXPENSE)
							.map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
					filterLabel = buildFilterLabel(req);
				} else {
					txns = txnDAO.findAll(null, 1, Integer.MAX_VALUE, bookId);
					income = txnDAO.sumByType("INCOME", bookId);
					expense = txnDAO.sumByType("EXPENSE", bookId);
				}
				txnCount = txns.size();
				if ("excel".equals(fmt)) {
					attachment = exporter.generateExcel(book, txns, income, expense, filterLabel);
					attachName = safe + (filtered ? "_filtered" : "") + ".xlsx";
					mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
				} else {
					attachment = exporter.generatePDF(book, txns, income, expense, filterLabel);
					attachName = safe + (filtered ? "_filtered" : "") + ".pdf";
					mimeType = "application/pdf";
				}
			}

			String subject = "ExpenseOS — " + book.getName() + (isReportEmail ? " Full Report" : " Report");
			String body = buildEmailBody(book, txnCount, income, expense);
			gmail.sendMail(to, subject, body, attachment, attachName, mimeType);
			resp.sendRedirect(req.getContextPath() + (isReportEmail ? "/reports" : "/home") + "?emailSent=1");

		} catch (Exception e) {
			log.error("Email export error: {}", e.getMessage(), e);
			resp.sendRedirect(req.getContextPath() + "/home?exportError=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
		}
	}

	// ── Build the FULL reports PDF — fetches all the same data as reports.jsp ──
	private byte[] buildFullReportsPdf(ExportService exporter, TransactionDAO txnDAO, CashBook book, int bookId,
			HttpServletRequest req) throws Exception {

		LocalDate now = LocalDate.now();
		int selYear, selMonth;
		try {
			selYear = Integer.parseInt(req.getParameter("year"));
			selMonth = Integer.parseInt(req.getParameter("month"));
		} catch (Exception e) {
			selYear = now.getYear();
			selMonth = now.getMonthValue();
		}

		BigDecimal allTimeIncome = txnDAO.sumByType("INCOME", bookId);
		BigDecimal allTimeExpense = txnDAO.sumByType("EXPENSE", bookId);

		List<Map<String, Object>> monthly = txnDAO.monthlyTrend(12, bookId);
		List<Map<String, Object>> expByCatAll = txnDAO.expenseByCategory(bookId);
		List<Map<String, Object>> incByCatAll = txnDAO.incomeByCategory(bookId);

		Map<String, Object> monthSummary = txnDAO.monthSummary(selYear, selMonth, bookId);
		List<Map<String, Object>> dailyData = txnDAO.dailyTotals(selYear, selMonth, bookId);
		List<Map<String, Object>> weeklyData = txnDAO.weeklyTotals(selYear, selMonth, bookId);
		List<Map<String, Object>> dowData = txnDAO.dayOfWeekPattern(selYear, selMonth, bookId);
		List<Map<String, Object>> expCatMonth = txnDAO.categoryBreakdownByMonth("EXPENSE", selYear, selMonth, bookId);
		List<Map<String, Object>> incCatMonth = txnDAO.categoryBreakdownByMonth("INCOME", selYear, selMonth, bookId);
		List<Map<String, Object>> expSubCatMonth = txnDAO.subCategoryBreakdownByMonth("EXPENSE", selYear, selMonth,
				bookId);
		List<Map<String, Object>> incSubCatMonth = txnDAO.subCategoryBreakdownByMonth("INCOME", selYear, selMonth,
				bookId);

		return exporter.generateReportsPDF(book, allTimeIncome, allTimeExpense, monthly, expByCatAll, incByCatAll,
				selYear, selMonth, monthSummary, dailyData, weeklyData, dowData, expCatMonth, incCatMonth,
				expSubCatMonth, incSubCatMonth);
	}

	// ── Helpers ───────────────────────────────────────────
	private void sendFile(HttpServletResponse resp, byte[] bytes, String mimeType, String filename) throws IOException {
		resp.setContentType(mimeType);
		resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		resp.setContentLength(bytes.length);
		resp.getOutputStream().write(bytes);
	}

	private int intParam(HttpServletRequest req, String name, int def) {
		try {
			return Integer.parseInt(req.getParameter(name));
		} catch (Exception e) {
			return def;
		}
	}

	private TransactionFilter parseFilterFromParams(HttpServletRequest req, int bookId) {
		TransactionFilter f = new TransactionFilter();
		f.setBookId(bookId);
		String type = req.getParameter("filter");
		if (type != null && !type.isBlank())
			f.setType(type);
		try {
			if (req.getParameter("dateFrom") != null && !req.getParameter("dateFrom").isBlank())
				f.setDateFrom(LocalDate.parse(req.getParameter("dateFrom")));
		} catch (Exception ignored) {
		}
		try {
			if (req.getParameter("dateTo") != null && !req.getParameter("dateTo").isBlank())
				f.setDateTo(LocalDate.parse(req.getParameter("dateTo")));
		} catch (Exception ignored) {
		}
		String[] catIds = req.getParameterValues("categoryId");
		if (catIds != null) {
			List<Integer> ids = new java.util.ArrayList<>();
			for (String s : catIds)
				try {
					if (s != null && !s.isBlank())
						ids.add(Integer.parseInt(s));
				} catch (Exception ignored) {
				}
			if (!ids.isEmpty())
				f.setCategoryIds(ids);
		}
		// NOTE: this was previously missing entirely, so the sub-category
		// part of the filter was silently dropped on export.
		String[] subIds = req.getParameterValues("subCategoryId");
		if (subIds != null) {
			List<Integer> ids = new java.util.ArrayList<>();
			for (String s : subIds)
				try {
					if (s != null && !s.isBlank())
						ids.add(Integer.parseInt(s));
				} catch (Exception ignored) {
				}
			if (!ids.isEmpty())
				f.setSubCategoryIds(ids);
		}
		String amt1 = req.getParameter("amount1"), op1 = req.getParameter("amountOp1");
		String amt2 = req.getParameter("amount2"), op2 = req.getParameter("amountOp2");
		try {
			if (amt1 != null && !amt1.isBlank()) {
				f.setAmountOp1(op1);
				f.setAmount1(new java.math.BigDecimal(amt1));
			}
		} catch (Exception ignored) {
		}
		try {
			if (amt2 != null && !amt2.isBlank()) {
				f.setAmountOp2(op2);
				f.setAmount2(new java.math.BigDecimal(amt2));
			}
		} catch (Exception ignored) {
		}
		String search = req.getParameter("search");
		if (search != null && !search.isBlank())
			f.setNoteSearch(search);
		return f;
	}

	private String buildFilterLabel(HttpServletRequest req) {
		List<String> parts = new java.util.ArrayList<>();
		if (req.getParameter("dateFrom") != null && !req.getParameter("dateFrom").isBlank())
			parts.add("From: " + req.getParameter("dateFrom"));
		if (req.getParameter("dateTo") != null && !req.getParameter("dateTo").isBlank())
			parts.add("To: " + req.getParameter("dateTo"));
		if (req.getParameter("filter") != null && !req.getParameter("filter").isBlank())
			parts.add("Type: " + req.getParameter("filter"));
		if (req.getParameter("search") != null && !req.getParameter("search").isBlank())
			parts.add("Search: " + req.getParameter("search"));
		return String.join(", ", parts);
	}

	private String buildEmailBody(CashBook book, int count, BigDecimal income, BigDecimal expense) {
		return "<html><body style='font-family:Inter,sans-serif;color:#1e293b'>"
				+ "<h2 style='color:#2563eb'>ExpenseOS — " + book.getName() + "</h2>"
				+ "<p>Please find the attached financial report.</p>"
				+ "<table style='border-collapse:collapse;margin:1rem 0'>"
				+ "<tr><td style='padding:.4rem 1rem;background:#dcfce7;color:#15803d;font-weight:700'>Total Income</td>"
				+ "<td style='padding:.4rem 1rem'>&#8377;" + income + "</td></tr>"
				+ "<tr><td style='padding:.4rem 1rem;background:#fee2e2;color:#b91c1c;font-weight:700'>Total Expense</td>"
				+ "<td style='padding:.4rem 1rem'>&#8377;" + expense + "</td></tr>"
				+ "<tr><td style='padding:.4rem 1rem;background:#eff6ff;color:#2563eb;font-weight:700'>Balance</td>"
				+ "<td style='padding:.4rem 1rem'>&#8377;" + income.subtract(expense) + "</td></tr>" + "</table>"
				+ "<p style='color:#64748b;font-size:.85rem'>" + count + " transactions | Generated by ExpenseOS</p>"
				+ "</body></html>";
	}
}