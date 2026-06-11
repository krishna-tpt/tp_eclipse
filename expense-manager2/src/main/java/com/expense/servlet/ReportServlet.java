package com.expense.servlet;

import com.expense.dao.TransactionDAO;
import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@WebServlet("/reports/*")
public class ReportServlet extends HttpServlet {

    private final TransactionDAO dao  = new TransactionDAO();
    private final Gson           gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String path = req.getPathInfo();
        if (path == null) path = "/summary";

        if ("/chart-data".equals(path)) {
            serveChartData(req, res);
            return;
        }

        // Default: summary report
        try {
            String fromDate = req.getParameter("fromDate");
            String toDate   = req.getParameter("toDate");
            int    year     = req.getParameter("year") != null
                              ? Integer.parseInt(req.getParameter("year"))
                              : Calendar.getInstance().get(Calendar.YEAR);

            BigDecimal totalIncome  = dao.getTotalAmount("income",  fromDate, toDate);
            BigDecimal totalExpense = dao.getTotalAmount("expense", fromDate, toDate);
            BigDecimal balance      = totalIncome.subtract(totalExpense);

            List<Map<String, Object>> incomeByCategory  = dao.getCategorySummary("income",  fromDate, toDate);
            List<Map<String, Object>> expenseByCategory = dao.getCategorySummary("expense", fromDate, toDate);
            List<Map<String, Object>> monthlyTrend      = dao.getMonthlyTrend(year);

            req.setAttribute("totalIncome",        totalIncome);
            req.setAttribute("totalExpense",       totalExpense);
            req.setAttribute("balance",            balance);
            req.setAttribute("incomeByCategory",   incomeByCategory);
            req.setAttribute("expenseByCategory",  expenseByCategory);
            req.setAttribute("monthlyTrend",       monthlyTrend);
            req.setAttribute("selectedYear",       year);
            req.setAttribute("fromDate",           fromDate);
            req.setAttribute("toDate",             toDate);

            req.getRequestDispatcher("/WEB-INF/jsp/reports.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void serveChartData(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        try {
            String fromDate = req.getParameter("fromDate");
            String toDate   = req.getParameter("toDate");
            int    year     = req.getParameter("year") != null
                              ? Integer.parseInt(req.getParameter("year"))
                              : Calendar.getInstance().get(Calendar.YEAR);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("incomeByCategory",  dao.getCategorySummary("income",  fromDate, toDate));
            data.put("expenseByCategory", dao.getCategorySummary("expense", fromDate, toDate));
            data.put("monthlyTrend",      dao.getMonthlyTrend(year));

            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write(gson.toJson(data));
        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}