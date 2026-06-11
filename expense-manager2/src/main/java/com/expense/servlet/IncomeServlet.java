package com.expense.servlet;

import com.expense.dao.TransactionDAO;
import com.expense.model.CustomColumn;
import com.expense.model.Transaction;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@WebServlet("/income/*")
public class IncomeServlet extends HttpServlet {

	private final TransactionDAO dao = new TransactionDAO();
	private static final String TYPE = "income";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String path = req.getPathInfo();
		if (path == null)
			path = "/list";

		switch (path) {
		case "/add":
			showForm(req, res, null);
			break;
		case "/edit":
			int editId = Integer.parseInt(req.getParameter("id"));
			try {
				Transaction t = dao.getById(TYPE, editId);
				showForm(req, res, t);
			} catch (Exception e) {
				req.setAttribute("error", e.getMessage());
				res.sendRedirect(req.getContextPath() + "/income/list");
			}
			break;
		case "/delete":
			int delId = Integer.parseInt(req.getParameter("id"));
			try {
				dao.deleteTransaction(TYPE, delId);
				req.getSession().setAttribute("successMsg", "Income deleted successfully!");
			} catch (Exception e) {
				req.getSession().setAttribute("errorMsg", e.getMessage());
			}
			res.sendRedirect(req.getContextPath() + "/income/list");
			break;
		default:
			listTransactions(req, res);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		String action = req.getParameter("action");

		try {
			if ("addColumn".equals(action)) {
				String label = req.getParameter("columnLabel");
				String cType = req.getParameter("columnType");
				String name = label.toLowerCase().replaceAll("\\s+", "_");
				dao.addCustomColumn(TYPE, name, label, cType);
				req.getSession().setAttribute("successMsg", "Custom column added!");
				res.sendRedirect(req.getContextPath() + "/income/list");
				return;
			}
			if ("deleteColumn".equals(action)) {
				int colId = Integer.parseInt(req.getParameter("columnId"));
				dao.deleteCustomColumn(colId);
				req.getSession().setAttribute("successMsg", "Column deleted.");
				res.sendRedirect(req.getContextPath() + "/income/list");
				return;
			}
			if ("addCategory".equals(action)) {
				dao.addCategory(TYPE, req.getParameter("categoryName"));
				req.getSession().setAttribute("successMsg", "Category added!");
				res.sendRedirect(req.getContextPath() + "/income/add");
				return;
			}

			// Save transaction
			Transaction t = buildTransaction(req);
			if (t.getId() == 0) {
				dao.addTransaction(t);
				req.getSession().setAttribute("successMsg", "Income added successfully!");
			} else {
				dao.updateTransaction(t);
				req.getSession().setAttribute("successMsg", "Income updated successfully!");
			}
			res.sendRedirect(req.getContextPath() + "/income/list");

		} catch (Exception e) {
			req.setAttribute("error", "Error: " + e.getMessage());
			try {
				showForm(req, res, null);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void listTransactions(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		try {
			String from = req.getParameter("fromDate");
			String to = req.getParameter("toDate");
			String category = req.getParameter("category");
			String sortBy = req.getParameter("sortBy");
			String sortDir = req.getParameter("sortDir");

			List<Transaction> list = dao.getAll(TYPE, from, to, category, sortBy, sortDir);
			List<String> cats = dao.getCategories(TYPE);
			List<CustomColumn> columns = dao.getCustomColumns(TYPE);
			BigDecimal total = dao.getTotalAmount(TYPE, from, to);

			req.setAttribute("transactions", list);
			req.setAttribute("categories", cats);
			req.setAttribute("customColumns", columns);
			req.setAttribute("totalAmount", total);
			req.setAttribute("type", TYPE);
			req.getRequestDispatcher("/WEB-INF/jsp/transaction-list.jsp").forward(req, res);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private void showForm(HttpServletRequest req, HttpServletResponse res, Transaction t)
			throws ServletException, IOException {
		try {
			List<String> cats = dao.getCategories(TYPE);
			List<CustomColumn> columns = dao.getCustomColumns(TYPE);
			req.setAttribute("transaction", t);
			req.setAttribute("categories", cats);
			req.setAttribute("customColumns", columns);
			req.setAttribute("type", TYPE);
			req.getRequestDispatcher("/WEB-INF/jsp/transaction-form.jsp").forward(req, res);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private Transaction buildTransaction(HttpServletRequest req) throws Exception {
		Transaction t = new Transaction();
		String idStr = req.getParameter("id");
		if (idStr != null && !idStr.isEmpty())
			t.setId(Integer.parseInt(idStr));

		t.setTransactionDate(Date.valueOf(req.getParameter("transactionDate")));
		t.setTransactionTime(Time.valueOf(req.getParameter("transactionTime") + ":00"));
		t.setAmount(new BigDecimal(req.getParameter("amount")));
		t.setCategory(req.getParameter("category"));
		t.setNote(req.getParameter("note"));
		t.setType(TYPE);

		// Custom column values (prefixed with "custom_")
		for (String paramName : req.getParameterMap().keySet()) {
			if (paramName.startsWith("custom_")) {
				String colName = paramName.substring(7);
				t.addCustomValue(colName, req.getParameter(paramName));
			}
		}
		return t;
	}
}