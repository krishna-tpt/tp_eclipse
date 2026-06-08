package com.expensemanager.servlet;

import com.expensemanager.dao.*;
import com.expensemanager.model.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet("/transactions")
public class TransactionServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int bookId = (Integer) req.getSession().getAttribute("activeBookId");

		// Build filter from request params
		TransactionFilter filter = parseFilter(req, bookId);

		try {
			TransactionDAO txnDao = new TransactionDAO();
			CategoryDAO catDao = new CategoryDAO();
			ColumnDefinitionDAO colDao = new ColumnDefinitionDAO();
			SubCategoryDAO scDao = new SubCategoryDAO();

			List<Transaction> txns = txnDao.findByFilter(filter);
			int total = txnDao.countByFilter(filter);
			int totalPages = (int) Math.ceil((double) total / filter.getPageSize());

			req.setAttribute("transactions", txns);
			req.setAttribute("incomeCategories", catDao.findByType("INCOME"));
			req.setAttribute("expenseCategories", catDao.findByType("EXPENSE"));
			req.setAttribute("incomeColumns", colDao.findByType("INCOME"));
			req.setAttribute("expenseColumns", colDao.findByType("EXPENSE"));
			req.setAttribute("subCategories", scDao.findAll());
			req.setAttribute("filter", filter);
			req.setAttribute("page", filter.getPage());
			req.setAttribute("totalPages", totalPages);
			req.setAttribute("total", total);
		} catch (Exception e) {
			req.setAttribute("dbError", e.getMessage());
		}
		req.getRequestDispatcher("/WEB-INF/views/transactions.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		int bookId = (Integer) req.getSession().getAttribute("activeBookId");
		String typeStr = req.getParameter("type");
		String amountStr = req.getParameter("amount");
		String catIdStr = req.getParameter("categoryId");
		String subcatStr = req.getParameter("subcategoryId");
		String note = req.getParameter("note");
		String dateStr = req.getParameter("dateTime");

		if (typeStr == null || amountStr == null || catIdStr == null || amountStr.isBlank()) {
			resp.sendRedirect(req.getContextPath() + "/transactions?error=missing");
			return;
		}

		Transaction t = new Transaction();
		t.setType(Transaction.Type.valueOf(typeStr.toUpperCase()));
		t.setAmount(new BigDecimal(amountStr));
		t.setCategoryId(Integer.parseInt(catIdStr));
		t.setBookId(bookId);
		if (subcatStr != null && !subcatStr.isBlank())
			t.setSubcategoryid(Integer.parseInt(subcatStr.trim()));
		t.setNote(note);
		t.setDateTime(dateStr != null && !dateStr.isBlank() ? LocalDateTime.parse(dateStr) : LocalDateTime.now());

		Map<String, String> customs = new LinkedHashMap<>();
		req.getParameterNames().asIterator().forEachRemaining(p -> {
			if (p.startsWith("custom_")) {
				String val = req.getParameter(p);
				if (val != null && !val.isBlank())
					customs.put(p.substring(7), val);
			}
		});
		t.setCustomValues(customs);

		try {
			new TransactionDAO().insert(t);
			resp.sendRedirect(req.getContextPath() + "/home");
		} catch (Exception e) {
			resp.sendRedirect(req.getContextPath() + "/transactions?error=" + e.getMessage());
		}
	}

	// ── Filter parsing ────────────────────────────────────
	private TransactionFilter parseFilter(HttpServletRequest req, int bookId) {
		TransactionFilter f = new TransactionFilter();
		f.setBookId(bookId);

		// Type (tab filter)
		String type = req.getParameter("filter");
		if (type != null && !type.isBlank())
			f.setType(type);

		// Dates
		String from = req.getParameter("dateFrom");
		String to = req.getParameter("dateTo");
		if (from != null && !from.isBlank()) {
			try {
				f.setDateFrom(LocalDate.parse(from));
			} catch (Exception ignored) {
			}
		}
		if (to != null && !to.isBlank()) {
			try {
				f.setDateTo(LocalDate.parse(to));
			} catch (Exception ignored) {
			}
		}

		// Category / SubCat
		String catId = req.getParameter("categoryId");
		String subcatId = req.getParameter("subCategoryId");
		if (catId != null && !catId.isBlank()) {
			try {
				f.setCategoryId(Integer.parseInt(catId));
			} catch (Exception ignored) {
			}
		}
		if (subcatId != null && !subcatId.isBlank()) {
			try {
				f.setSubCategoryId(Integer.parseInt(subcatId));
			} catch (Exception ignored) {
			}
		}

		// Amount conditions
		String op1 = req.getParameter("amountOp1");
		String amt1 = req.getParameter("amount1");
		String op2 = req.getParameter("amountOp2");
		String amt2 = req.getParameter("amount2");
		if (amt1 != null && !amt1.isBlank()) {
			try {
				f.setAmountOp1(op1);
				f.setAmount1(new BigDecimal(amt1));
			} catch (Exception ignored) {
			}
		}
		if (amt2 != null && !amt2.isBlank()) {
			try {
				f.setAmountOp2(op2);
				f.setAmount2(new BigDecimal(amt2));
			} catch (Exception ignored) {
			}
		}

		// Note search
		String note = req.getParameter("search");
		if (note != null && !note.isBlank())
			f.setNoteSearch(note);

		// Pagination
		String pageStr = req.getParameter("page");
		if (pageStr != null) {
			try {
				f.setPage(Integer.parseInt(pageStr));
			} catch (Exception ignored) {
			}
		}
		return f;
	}
}