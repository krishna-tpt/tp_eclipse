package com.expense.servlet;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expense.dao.TransactionDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(TransactionServlet.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int bookId = (Integer) req.getSession().getAttribute("activeBookId");
        try {
            TransactionDAO dao = new TransactionDAO();

            var income  = dao.sumByType("INCOME",  bookId);
            var expense = dao.sumByType("EXPENSE", bookId);

            req.setAttribute("totalIncome",  income);
            req.setAttribute("totalExpense", expense);
            req.setAttribute("balance",      income.subtract(expense));

            req.setAttribute("monthlyJson",  mapper.writeValueAsString(dao.monthlyTrend(6, bookId)));
            req.setAttribute("expCatJson",   mapper.writeValueAsString(dao.expenseByCategory(bookId)));
            req.setAttribute("incCatJson",   mapper.writeValueAsString(dao.incomeByCategory(bookId)));
        } catch (Exception e) {
            req.setAttribute("dbError", e.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(req, resp);
    }
}
