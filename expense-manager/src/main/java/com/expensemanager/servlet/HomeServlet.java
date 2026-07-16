package com.expensemanager.servlet;

import com.expensemanager.dao.CategoryDAO;
import com.expensemanager.dao.ColumnDefinitionDAO;
import com.expensemanager.dao.SubCategoryDAO;
import com.expensemanager.dao.TransactionDAO;
import com.expensemanager.model.DayGroup;
import com.expensemanager.model.Transaction;
import com.expensemanager.model.TransactionFilter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(HomeServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int bookId = (Integer) req.getSession().getAttribute("activeBookId");

		// Default so the JSP never sees a null dayGroups, even if an
		// exception happens before we reach the grouping step below.
		req.setAttribute("dayGroups", Collections.emptyList());

		try {
			TransactionDAO dao = new TransactionDAO();
			CategoryDAO catDAO = new CategoryDAO();
			ColumnDefinitionDAO colDAO = new ColumnDefinitionDAO();
			SubCategoryDAO scDAO = new SubCategoryDAO();

			// ── Global summary cards (always full book totals) ──────
			BigDecimal income = dao.sumByType("INCOME", bookId);
			BigDecimal expense = dao.sumByType("EXPENSE", bookId);
			req.setAttribute("totalIncome", income);
			req.setAttribute("totalExpense", expense);
			req.setAttribute("balance", income.subtract(expense));

			// ── Filter + paginated transactions ─────────────────────
			TransactionFilter filter = parseFilter(req, bookId);

			List<Transaction> txns = dao.findByFilter(filter);
			int total = dao.countByFilter(filter);
			int totalPages = (int) Math.ceil((double) total / filter.getPageSize());

			req.setAttribute("transactions", txns);
			req.setAttribute("dayGroups", DayGroup.groupByDay(txns));
			req.setAttribute("filter", filter);
			req.setAttribute("page", filter.getPage());
			req.setAttribute("totalPages", totalPages);
			req.setAttribute("total", total);

			// ── Filtered income / expense / net for export bar ──────
			if (filter.isFiltered()) {
				TransactionFilter incF = cloneFilterWithType(filter, "INCOME");
				TransactionFilter expF = cloneFilterWithType(filter, "EXPENSE");
				BigDecimal fIncome = dao.sumByFilter(incF);
				BigDecimal fExpense = dao.sumByFilter(expF);
				req.setAttribute("filteredIncome", fIncome);
				req.setAttribute("filteredExpense", fExpense);
				req.setAttribute("filteredNet", fIncome.subtract(fExpense));
			} else {
				req.setAttribute("filteredIncome", income);
				req.setAttribute("filteredExpense", expense);
				req.setAttribute("filteredNet", income.subtract(expense));
			}

			// ── Dropdowns ───────────────────────────────────────────
			req.setAttribute("incomeCategories", catDAO.findByType("INCOME", bookId));
			req.setAttribute("expenseCategories", catDAO.findByType("EXPENSE", bookId));
			req.setAttribute("incomeColumns", colDAO.findByType("INCOME"));
			req.setAttribute("expenseColumns", colDAO.findByType("EXPENSE"));
			req.setAttribute("subCategories", scDAO.findAll());

		} catch (Exception e) {
			log.error("HomeServlet error: {}", e.getMessage(), e);
			req.setAttribute("dbError", e.getMessage());
		}

		req.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("/transactions").forward(req, resp);
	}

	// ── Clone filter but force a specific type (INCOME or EXPENSE) ──
	private TransactionFilter cloneFilterWithType(TransactionFilter src, String type) {
		TransactionFilter f = new TransactionFilter();
		f.setBookId(src.getBookId());
		f.setType(type);
		f.setDateFrom(src.getDateFrom());
		f.setDateTo(src.getDateTo());
		f.setCategoryIds(src.getCategoryIds());
		f.setSubCategoryIds(src.getSubCategoryIds());
		f.setNoteSearch(src.getNoteSearch());
		f.setAmountOp1(src.getAmountOp1());
		f.setAmount1(src.getAmount1());
		f.setAmountOp2(src.getAmountOp2());
		f.setAmount2(src.getAmount2());
		return f;
	}

	// ── Filter parsing ──────────────────────────────────────────────
	private TransactionFilter parseFilter(HttpServletRequest req, int bookId) {
		TransactionFilter f = new TransactionFilter();
		f.setBookId(bookId);

		String type = req.getParameter("filter");
		if (type != null && !type.isBlank())
			f.setType(type);

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

		String[] catIds = req.getParameterValues("categoryId");
		if (catIds != null) {
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

		String[] subIds = req.getParameterValues("subCategoryId");
		if (subIds != null) {
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

		String search = req.getParameter("search");
		if (search != null && !search.isBlank())
			f.setNoteSearch(search);

		try {
			String p = req.getParameter("page");
			if (p != null)
				f.setPage(Integer.parseInt(p));
		} catch (Exception ignored) {
		}

		String sortBy = req.getParameter("sortBy");
		if (sortBy != null && !sortBy.isBlank())
			f.setSortBy(sortBy);

		String sortDir = req.getParameter("sortDir");
		f.setSortDir("asc".equalsIgnoreCase(sortDir) ? "asc" : "desc");

		return f;
	}
}