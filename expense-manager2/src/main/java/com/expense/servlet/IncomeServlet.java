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
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String path = req.getPathInfo();
        if (path == null || path.equals("/") || path.equals("/list")) {
            listTransactions(req, res);
            return;
        }
        switch (path) {
            case "/add":
                showForm(req, res, null);
                break;
            case "/edit":
                try {
                    int id = Integer.parseInt(req.getParameter("id"));
                    Transaction t = dao.getById(TYPE, id);
                    showForm(req, res, t);
                } catch (Exception e) {
                    req.getSession().setAttribute("errorMsg", e.getMessage());
                    res.sendRedirect(req.getContextPath() + "/income/list");
                }
                break;
            case "/delete":
                try {
                    dao.deleteTransaction(TYPE, Integer.parseInt(req.getParameter("id")));
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
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        try {
            if ("addColumn".equals(action)) {
                String label = req.getParameter("columnLabel");
                dao.addCustomColumn(TYPE, label.toLowerCase().replaceAll("\\s+","_"), label, req.getParameter("columnType"));
                req.getSession().setAttribute("successMsg", "Custom column added!");
                res.sendRedirect(req.getContextPath() + "/income/list"); return;
            }
            if ("deleteColumn".equals(action)) {
                dao.deleteCustomColumn(Integer.parseInt(req.getParameter("columnId")));
                req.getSession().setAttribute("successMsg", "Column deleted.");
                res.sendRedirect(req.getContextPath() + "/income/list"); return;
            }
            if ("addCategory".equals(action)) {
                dao.addCategory(TYPE, req.getParameter("categoryName"));
                req.getSession().setAttribute("successMsg", "Category added!");
                res.sendRedirect(req.getContextPath() + "/income/add"); return;
            }
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
            try { showForm(req, res, null); } catch (Exception ex) { throw new ServletException(ex); }
        }
    }

    private void listTransactions(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            List<Transaction>  list    = dao.getAll(TYPE, req.getParameter("fromDate"), req.getParameter("toDate"), req.getParameter("category"), req.getParameter("sortBy"), req.getParameter("sortDir"));
            List<String>       cats    = dao.getCategories(TYPE);
            List<CustomColumn> columns = dao.getCustomColumns(TYPE);
            BigDecimal         total   = dao.getTotalAmount(TYPE, req.getParameter("fromDate"), req.getParameter("toDate"));
            req.setAttribute("transactions",  list);
            req.setAttribute("categories",    cats);
            req.setAttribute("customColumns", columns);
            req.setAttribute("totalAmount",   total);
            req.setAttribute("type",          TYPE);
            req.setAttribute("activePage",    TYPE);
            req.getRequestDispatcher("/WEB-INF/jsp/transaction-list.jsp").forward(req, res);
        } catch (Exception e) { throw new ServletException(e); }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse res, Transaction t)
            throws ServletException, IOException {
        try {
            req.setAttribute("transaction",   t);
            req.setAttribute("categories",    dao.getCategories(TYPE));
            req.setAttribute("customColumns", dao.getCustomColumns(TYPE));
            req.setAttribute("type",          TYPE);
            req.getRequestDispatcher("/WEB-INF/jsp/transaction-form.jsp").forward(req, res);
        } catch (Exception e) { throw new ServletException(e); }
    }

    private Transaction buildTransaction(HttpServletRequest req) throws Exception {
        Transaction t = new Transaction();
        String idStr = req.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) t.setId(Integer.parseInt(idStr));
        t.setTransactionDate(Date.valueOf(req.getParameter("transactionDate")));
        t.setTransactionTime(Time.valueOf(req.getParameter("transactionTime") + ":00"));
        t.setAmount(new java.math.BigDecimal(req.getParameter("amount")));
        t.setCategory(req.getParameter("category"));
        t.setNote(req.getParameter("note"));
        t.setType(TYPE);
        for (String p : req.getParameterMap().keySet())
            if (p.startsWith("custom_")) t.addCustomValue(p.substring(7), req.getParameter(p));
        return t;
    }
}
