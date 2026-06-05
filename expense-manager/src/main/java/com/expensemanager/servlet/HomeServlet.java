package com.expensemanager.servlet;

import com.expensemanager.model.Transaction;
import com.expensemanager.service.AppContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // TEMP TEST — JSP  direct response
        resp.setContentType("text/html");
        resp.getWriter().println("<h1>Hello! Servlet works!</h1>");
    }
}

/**
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        AppContext ctx = AppContext.getInstance();
        boolean loaded = ctx.isLoaded();

        if (loaded) {
            List<Transaction> txns = ctx.getTransactions();
            double totalIncome  = txns.stream().filter(t -> t.getType() == Transaction.Type.INCOME)
                    .mapToDouble(Transaction::getAmount).sum();
            double totalExpense = txns.stream().filter(t -> t.getType() == Transaction.Type.EXPENSE)
                    .mapToDouble(Transaction::getAmount).sum();

            req.setAttribute("totalIncome",  totalIncome);
            req.setAttribute("totalExpense", totalExpense);
            req.setAttribute("balance",      totalIncome - totalExpense);
            req.setAttribute("txnCount",     txns.size());
            req.setAttribute("config",       ctx.getConfig());

            // Recent 5 transactions
            int from = Math.max(0, txns.size() - 5);
            req.setAttribute("recentTxns", txns.subList(from, txns.size()));
        }

        req.setAttribute("loaded", loaded);
        req.setAttribute("currentYear", LocalDate.now().getYear());
        req.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(req, resp);
    }
}
*/