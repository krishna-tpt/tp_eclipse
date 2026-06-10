package com.expensemanager.servlet;

import com.expensemanager.dao.TransactionDAO;
import com.expensemanager.model.Transaction;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(TransactionServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int bookId = (Integer) req.getSession().getAttribute("activeBookId");
//		System.out.println("Book ID: "+bookId);
		try {
			TransactionDAO dao = new TransactionDAO();
			BigDecimal income = dao.sumByType("INCOME", bookId);
			BigDecimal expense = dao.sumByType("EXPENSE", bookId);
			List<Transaction> recent = dao.findAll(null, 1, 5, bookId);

			req.setAttribute("totalIncome", income);
			req.setAttribute("totalExpense", expense);
			req.setAttribute("balance", income.subtract(expense));
			req.setAttribute("recentTxns", recent);
		} catch (Exception e) {
			req.setAttribute("dbError", e.getMessage());
		}
		req.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    // Delegate to TransactionServlet
	    req.getRequestDispatcher("/transactions").forward(req, resp);
	}
}
