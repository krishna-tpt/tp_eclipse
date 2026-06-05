package com.expensemanager.servlet;

import com.expensemanager.model.Transaction;
import com.expensemanager.service.AppContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        AppContext ctx = AppContext.getInstance();
        if (!ctx.isLoaded()) {
            req.setAttribute("error", "notLoaded");
            req.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(req, resp);
            return;
        }

        List<Transaction> all = ctx.getTransactions();
        if (all == null) all = List.of();

        // ── Summary ───────────────────────────────────────────────────
        double totalIncome  = all.stream().filter(t -> t.getType() == Transaction.Type.INCOME)
                .mapToDouble(Transaction::getAmount).sum();
        double totalExpense = all.stream().filter(t -> t.getType() == Transaction.Type.EXPENSE)
                .mapToDouble(Transaction::getAmount).sum();

        // ── Expense by Category ────────────────────────────────────────
        Map<String, Double> expByCategory = all.stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() != null ? t.getCategory() : "Unknown",
                        Collectors.summingDouble(Transaction::getAmount)));

        // ── Income by Category ─────────────────────────────────────────
        Map<String, Double> incByCategory = all.stream()
                .filter(t -> t.getType() == Transaction.Type.INCOME)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() != null ? t.getCategory() : "Unknown",
                        Collectors.summingDouble(Transaction::getAmount)));

        // ── Monthly trend (last 6 months, or all) ─────────────────────
        Map<String, double[]> monthly = new LinkedHashMap<>();
        all.forEach(t -> {
            if (t.getDateTime() == null) return;
            String key = t.getDateTime().getYear() + "-"
                    + String.format("%02d", t.getDateTime().getMonthValue());
            monthly.computeIfAbsent(key, k -> new double[2]);
            if (t.getType() == Transaction.Type.INCOME) monthly.get(key)[0] += t.getAmount();
            else                                         monthly.get(key)[1] += t.getAmount();
        });

        // Sort by month key
        List<String>   monthLabels  = new ArrayList<>(monthly.keySet());
        List<Double>   monthIncome  = monthLabels.stream().map(k -> monthly.get(k)[0]).toList();
        List<Double>   monthExpense = monthLabels.stream().map(k -> monthly.get(k)[1]).toList();

        // ── Payment mode breakdown ─────────────────────────────────────
        Map<String, Double> payByMode = all.stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE && t.getPayment() != null && !t.getPayment().isBlank())
                .collect(Collectors.groupingBy(Transaction::getPayment, Collectors.summingDouble(Transaction::getAmount)));

        // JSON for charts
        req.setAttribute("totalIncome",      totalIncome);
        req.setAttribute("totalExpense",     totalExpense);
        req.setAttribute("balance",          totalIncome - totalExpense);

        req.setAttribute("expByCategoryJson", mapper.writeValueAsString(expByCategory));
        req.setAttribute("incByCategoryJson", mapper.writeValueAsString(incByCategory));
        req.setAttribute("monthLabelsJson",   mapper.writeValueAsString(monthLabels));
        req.setAttribute("monthIncomeJson",   mapper.writeValueAsString(monthIncome));
        req.setAttribute("monthExpenseJson",  mapper.writeValueAsString(monthExpense));
        req.setAttribute("payByModeJson",     mapper.writeValueAsString(payByMode));

        req.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(req, resp);
    }
}
