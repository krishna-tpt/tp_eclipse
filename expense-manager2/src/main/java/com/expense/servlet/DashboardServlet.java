package com.expense.servlet;

import com.expense.dao.TransactionDAO;
import com.expense.model.Transaction;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebServlet(urlPatterns = { "/dashboard", "/dashboard/*" })
public class DashboardServlet extends HttpServlet {

	private final TransactionDAO dao = new TransactionDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			LocalDate now = LocalDate.now();
			String fromDate = now.withDayOfMonth(1).toString();
			String toDate = now.toString();

			BigDecimal totalIncome = dao.getTotalAmount("income", fromDate, toDate);
			BigDecimal totalExpense = dao.getTotalAmount("expense", fromDate, toDate);
			BigDecimal balance = totalIncome.subtract(totalExpense);

			List<Transaction> recentIncome = dao.getAll("income", null, null, null, "transaction_date", "DESC");
			List<Transaction> recentExpense = dao.getAll("expense", null, null, null, "transaction_date", "DESC");
			if (recentIncome.size() > 5)
				recentIncome = recentIncome.subList(0, 5);
			if (recentExpense.size() > 5)
				recentExpense = recentExpense.subList(0, 5);

			req.setAttribute("totalIncome", totalIncome);
			req.setAttribute("totalExpense", totalExpense);
			req.setAttribute("balance", balance);
			req.setAttribute("recentIncome", recentIncome);
			req.setAttribute("recentExpense", recentExpense);
			req.setAttribute("currentMonth", now.getMonth().toString());
			req.setAttribute("activePage", "dashboard");
			req.setAttribute("pageTitle", "Dashboard");

			req.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(req, res);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
