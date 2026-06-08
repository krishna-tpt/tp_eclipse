package com.expensemanager.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.dao.CategoryDAO;
import com.expensemanager.dao.ColumnDefinitionDAO;
import com.expensemanager.dao.SubCategoryDAO;
import com.expensemanager.dao.TransactionDAO;
import com.expensemanager.model.Transaction;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/transactions")
public class TransactionServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(TransactionServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int bookId    = (Integer) req.getSession().getAttribute("activeBookId");
		String filter = req.getParameter("filter"); // INCOME | EXPENSE | null
		String pageStr = req.getParameter("page");
		int page = (pageStr != null) ? Integer.parseInt(pageStr) : 1;
		int pageSize = 15;

		try {
			TransactionDAO txnDao = new TransactionDAO();
			CategoryDAO catDao = new CategoryDAO();
			ColumnDefinitionDAO colDao = new ColumnDefinitionDAO();
            SubCategoryDAO scDao  = new SubCategoryDAO();

            List<Transaction> txns = txnDao.findAll(filter, page, pageSize, bookId);
            int total      = txnDao.count(filter, bookId);
			int totalPages = (int) Math.ceil((double) total / pageSize);

			req.setAttribute("transactions", txns);
			req.setAttribute("incomeCategories", catDao.findByType("INCOME"));
			req.setAttribute("expenseCategories", catDao.findByType("EXPENSE"));
			req.setAttribute("incomeColumns", colDao.findByType("INCOME"));
			req.setAttribute("expenseColumns", colDao.findByType("EXPENSE"));
            req.setAttribute("subCategories",      scDao.findAll());
			req.setAttribute("filter", filter);
			req.setAttribute("page", page);
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
		String subcatIdStr = req.getParameter("subcategoryId");
//		System.out.println("subcategoryid: "+subcatIdStr);
		String note = req.getParameter("note");
		String dateStr = req.getParameter("dateTime");

        if (typeStr == null || amountStr == null || catIdStr == null || amountStr.isBlank() || catIdStr.isBlank()) {
			resp.sendRedirect(req.getContextPath() + "/transactions?error=missing");
			return;
		}

		Transaction t = new Transaction();
		t.setType(Transaction.Type.valueOf(typeStr.toUpperCase()));
		t.setAmount(new BigDecimal(amountStr));
		t.setCategoryId(Integer.parseInt(catIdStr));
        t.setBookId(bookId);
		
		if (subcatIdStr != null && !subcatIdStr.isBlank()) {
		    t.setSubcategoryid(Integer.parseInt(subcatIdStr.trim()));
		//} else {
		//    t.setSubcategoryid(0); // 0 = no subcategory
		}
		
		t.setNote(note);
        t.setDateTime(dateStr != null && !dateStr.isBlank()
                ? LocalDateTime.parse(dateStr) : LocalDateTime.now());

		// Collect custom field values (param name = "custom_<col_key>")
		Map<String, String> customs = new LinkedHashMap<>();
		req.getParameterNames().asIterator().forEachRemaining(p -> {
			if (p.startsWith("custom_")) {
//				String key = p.substring(7);
				String val = req.getParameter(p);
				if (val != null && !val.isBlank())
                    customs.put(p.substring(7), val);
			}
		});
		t.setCustomValues(customs);

		try {
			new TransactionDAO().insert(t);
//			resp.sendRedirect(req.getContextPath() + "/transactions?success=1&filter=" + typeStr);
			resp.sendRedirect(req.getContextPath() +"/home");
		} catch (Exception e) {
			resp.sendRedirect(req.getContextPath() + "/transactions?error=" + e.getMessage());
		}
	}
}
