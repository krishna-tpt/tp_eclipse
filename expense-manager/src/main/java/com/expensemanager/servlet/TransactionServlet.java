package com.expensemanager.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.dao.CategoryDAO;
import com.expensemanager.dao.ColumnDefinitionDAO;
import com.expensemanager.dao.ReceiptDAO;
import com.expensemanager.dao.SubCategoryDAO;
import com.expensemanager.dao.TransactionDAO;
import com.expensemanager.model.Receipt;
import com.expensemanager.model.Transaction;
import com.expensemanager.model.TransactionFilter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/transactions")
public class TransactionServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(TransactionServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int bookId = (Integer) req.getSession().getAttribute("activeBookId");
		TransactionFilter filter = parseFilter(req, bookId);

		try {
			TransactionDAO txnDAO = new TransactionDAO();
			CategoryDAO catDAO = new CategoryDAO();
			ColumnDefinitionDAO colDAO = new ColumnDefinitionDAO();
			SubCategoryDAO scDAO = new SubCategoryDAO();

			List<Transaction> txns = txnDAO.findByFilter(filter);
			int total = txnDAO.countByFilter(filter);
			int totalPages = (int) Math.ceil((double) total / filter.getPageSize());

			req.setAttribute("transactions", txns);
			req.setAttribute("incomeCategories", catDAO.findByType("INCOME"));
			req.setAttribute("expenseCategories", catDAO.findByType("EXPENSE"));
			req.setAttribute("incomeColumns", colDAO.findByType("INCOME"));
			req.setAttribute("expenseColumns", colDAO.findByType("EXPENSE"));
			req.setAttribute("subCategories", scDAO.findAll());
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

//		Enumeration<String> paramNames = req.getParameterNames();
//
//		while (paramNames.hasMoreElements()) {
//			String key = paramNames.nextElement();
//			String value = req.getParameter(key);
//
//			log.debug("Key: {} --> Value: {}", key, value);
//		}

		String typeStr = req.getParameter("type");
		String amountStr = req.getParameter("amount");
		String catIdStr = req.getParameter("categoryid");
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

		log.info("Transaction saving...");
		int txnId;
		try {
			txnId = new TransactionDAO().insert(t);

			String contentType = req.getContentType();

			if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {

				Part filePart = req.getPart("receipt");
				log.debug("File size : {}", filePart.getSize());
				if (filePart != null && filePart.getSize() > 0) {
					log.debug("File uploading...");
					Receipt r = new Receipt();
					r.setTransactionId(txnId);
					r.setFileName(getFileName(filePart));
					r.setFileType(filePart.getContentType());
					r.setFileData(filePart.getInputStream().readAllBytes());
					r.setFileSize((int) filePart.getSize());
//					new AuditLogDAO().logReceiptUpload(txnId, "user", r.getFileName());
					new ReceiptDAO().insert(r);
					log.debug("File uploaded...");
				}
			}
		} catch (Exception e) {
			System.out.println("File upload : " + e.getMessage());
		}
		// resp.sendRedirect(req.getContextPath() + "/home?msg=saved");
		resp.sendRedirect(req.getContextPath() + "/home");
//		resp.setContentType("application/json");
//		resp.setCharacterEncoding("UTF-8");
//		resp.getWriter().write("{\"status\":\"saved\"}");
	}

	private String getFileName(Part part) {
		String cd = part.getHeader("content-disposition");
		if (cd != null) {
			for (String s : cd.split(";")) {
				if (s.trim().startsWith("filename")) {
					return s.substring(s.indexOf('=') + 1).trim().replace("\"", "");
				}
			}
		}
		return "receipt_" + System.currentTimeMillis();
	}

	// ── Filter parsing (multi-category aware) ─────────────
	private TransactionFilter parseFilter(HttpServletRequest req, int bookId) {
		TransactionFilter f = new TransactionFilter();
		f.setBookId(bookId);

		// Type tab
		String type = req.getParameter("filter");
		if (type != null && !type.isBlank())
			f.setType(type);

		// Dates
		try {
			String s = req.getParameter("dateFrom");
			if (s != null && !s.isBlank())
				f.setDateFrom(LocalDate.parse(s));
		} catch (Exception ignored) {
		}
		try {
			String s = req.getParameter("dateTo");
			if (s != null && !s.isBlank())
				f.setDateTo(LocalDate.parse(s));
		} catch (Exception ignored) {
		}

		// Multi-category
		String[] catIds = req.getParameterValues("categoryId");
		if (catIds != null && catIds.length > 0) {
			List<Integer> ids = new ArrayList<>();
			for (String s : catIds) {
				try {
					if (!s.isBlank())
						ids.add(Integer.parseInt(s));
				} catch (Exception ignored) {
				}
			}
			if (!ids.isEmpty())
				f.setCategoryIds(ids);
		}

		// Multi-subcategory
		String[] subIds = req.getParameterValues("subCategoryId");
		if (subIds != null && subIds.length > 0) {
			List<Integer> ids = new ArrayList<>();
			for (String s : subIds) {
				try {
					if (!s.isBlank())
						ids.add(Integer.parseInt(s));
				} catch (Exception ignored) {
				}
			}
			if (!ids.isEmpty())
				f.setSubCategoryIds(ids);
		}

		// Amount
		String op1 = req.getParameter("amountOp1"), amt1 = req.getParameter("amount1");
		String op2 = req.getParameter("amountOp2"), amt2 = req.getParameter("amount2");
		try {
			if (amt1 != null && !amt1.isBlank()) {
				f.setAmountOp1(op1);
				f.setAmount1(new BigDecimal(amt1));
			}
		} catch (Exception ignored) {
		}
		try {
			if (amt2 != null && !amt2.isBlank()) {
				f.setAmountOp2(op2);
				f.setAmount2(new BigDecimal(amt2));
			}
		} catch (Exception ignored) {
		}

		// Note search
		String search = req.getParameter("search");
		if (search != null && !search.isBlank())
			f.setNoteSearch(search);

		// Pagination
		try {
			String p = req.getParameter("page");
			if (p != null)
				f.setPage(Integer.parseInt(p));
		} catch (Exception ignored) {
		}

		return f;
	}
}