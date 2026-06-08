package com.expensemanager.servlet;

import com.expensemanager.dao.CashBookDAO;
import com.expensemanager.dao.TransactionDAO;
import com.expensemanager.model.CashBook;
import com.expensemanager.model.Transaction;
import com.expensemanager.service.ExportService;
import com.expensemanager.service.GmailService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * GET /export?type=pdf → download PDF GET /export?type=excel → download Excel
 * POST /export (action=email) → send PDF via Gmail
 */
@WebServlet("/export")
public class ExportServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int bookId = (Integer) req.getSession().getAttribute("activeBookId");
		String type = req.getParameter("type"); // pdf | excel

		try {
			CashBookDAO cashBookDAO = new CashBookDAO();
			TransactionDAO txnDAO = new TransactionDAO();
			ExportService exporter = new ExportService();

			CashBook book = cashBookDAO.findById(bookId);
			List<Transaction> txns = txnDAO.findAll(null, 1, Integer.MAX_VALUE, bookId);
			BigDecimal income = txnDAO.sumByType("INCOME", bookId);
			BigDecimal expense = txnDAO.sumByType("EXPENSE", bookId);
			String safeName = book.getName().replaceAll("[^a-zA-Z0-9_-]", "_");

			if ("excel".equalsIgnoreCase(type)) {
				byte[] bytes = exporter.generateExcel(book, txns, income, expense);
				resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				resp.setHeader("Content-Disposition", "attachment; filename=\"" + safeName + ".xlsx\"");
				resp.setContentLength(bytes.length);
				resp.getOutputStream().write(bytes);

			} else {
				// Default PDF
				byte[] bytes = exporter.generatePDF(book, txns, income, expense);
				resp.setContentType("application/pdf");
				resp.setHeader("Content-Disposition", "attachment; filename=\"" + safeName + ".pdf\"");
				resp.setContentLength(bytes.length);
				resp.getOutputStream().write(bytes);
			}

		} catch (Exception e) {
			resp.sendRedirect(
					req.getContextPath() + "/home?exportError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		int bookId = (Integer) req.getSession().getAttribute("activeBookId");
		String to = req.getParameter("email");
		String fmt = req.getParameter("format"); // pdf | excel

		if (to == null || to.isBlank()) {
			resp.sendRedirect(req.getContextPath() + "/home?exportError=email_missing");
			return;
		}

		String gmailFrom = getServletContext().getInitParameter("GMAIL_FROM");
		String gmailPass = getServletContext().getInitParameter("GMAIL_APP_PASS");

		try {
			CashBookDAO cashBookDAO = new CashBookDAO();
			TransactionDAO txnDAO = new TransactionDAO();
			ExportService exporter = new ExportService();
			GmailService gmail = new GmailService(gmailFrom, gmailPass);

			CashBook book = cashBookDAO.findById(bookId);
			List<Transaction> txns = txnDAO.findAll(null, 1, Integer.MAX_VALUE, bookId);
			BigDecimal income = txnDAO.sumByType("INCOME", bookId);
			BigDecimal expense = txnDAO.sumByType("EXPENSE", bookId);
			String safeName = book.getName().replaceAll("[^a-zA-Z0-9_-]", "_");

			byte[] attachment;
			String attachName;
			String mimeType;

			if ("excel".equalsIgnoreCase(fmt)) {
				attachment = exporter.generateExcel(book, txns, income, expense);
				attachName = safeName + ".xlsx";
				mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			} else {
				attachment = exporter.generatePDF(book, txns, income, expense);
				attachName = safeName + ".pdf";
				mimeType = "application/pdf";
			}

			String subject = "ExpenseOS — " + book.getName() + " Report";
			String body = buildEmailBody(book, txns.size(), income, expense);

			gmail.sendMail(to, subject, body, attachment, attachName, mimeType);
			resp.sendRedirect(req.getContextPath() + "/home?emailSent=1");

		} catch (Exception e) {
			resp.sendRedirect(
					req.getContextPath() + "/home?exportError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
		}
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
				+ "<p style='color:#64748b;font-size:.85rem'>" + count
				+ " transactions &nbsp;|&nbsp; Generated by ExpenseOS</p>" + "</body></html>";
	}
}
