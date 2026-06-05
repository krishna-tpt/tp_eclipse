package com.expensemanager.servlet;

import com.expensemanager.model.Transaction;
import com.expensemanager.service.AppContext;
import com.expensemanager.service.ExcelService;
import com.expensemanager.service.ZohoWorkDriveService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/transactions")
public class TransactionServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(TransactionServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String filter = req.getParameter("filter"); // INCOME | EXPENSE | null
        AppContext ctx = AppContext.getInstance();

        List<Transaction> txns = ctx.isLoaded() ? ctx.getTransactions() : List.of();

        if ("INCOME".equalsIgnoreCase(filter)) {
            txns = txns.stream().filter(t -> t.getType() == Transaction.Type.INCOME).toList();
        } else if ("EXPENSE".equalsIgnoreCase(filter)) {
            txns = txns.stream().filter(t -> t.getType() == Transaction.Type.EXPENSE).toList();
        }

        req.setAttribute("transactions", txns);
        req.setAttribute("filter", filter);
        req.setAttribute("config", ctx.getConfig());
        req.getRequestDispatcher("/WEB-INF/views/transactions.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        AppContext appCtx = AppContext.getInstance();

        if (!appCtx.isLoaded()) {
            resp.sendError(503, "Workbook not loaded. Please sync first.");
            return;
        }

        // Build transaction from form params
        String typeStr   = req.getParameter("type");
        String amountStr = req.getParameter("amount");
        String category  = req.getParameter("category");
        String payment   = req.getParameter("payment");
        String note      = req.getParameter("note");
        String dateStr   = req.getParameter("dateTime");

        if (typeStr == null || amountStr == null || category == null) {
            resp.sendRedirect(req.getContextPath() + "/transactions?error=missing_fields");
            return;
        }

        Transaction txn = new Transaction();
        txn.setType("income".equalsIgnoreCase(typeStr) ? Transaction.Type.INCOME : Transaction.Type.EXPENSE);
        txn.setAmount(Double.parseDouble(amountStr));
        txn.setCategory(category);
        txn.setPayment(payment);
        txn.setNote(note);
        txn.setDateTime(dateStr != null && !dateStr.isBlank()
                ? LocalDateTime.parse(dateStr)
                : LocalDateTime.now());

        // Collect custom/extra fields (any param starting with "extra_")
        Map<String, String> extra = new LinkedHashMap<>();
        Enumeration<String> params = req.getParameterNames();
        while (params.hasMoreElements()) {
            String p = params.nextElement();
            if (p.startsWith("extra_")) {
                extra.put(p.substring(6), req.getParameter(p));
            }
        }
        txn.setExtraFields(extra);

        try {
            ExcelService excelService = (ExcelService) getServletContext().getAttribute("excelService");
            byte[] updatedBytes = excelService.addTransaction(appCtx.getWorkbookBytes(), txn);
            appCtx.setWorkbookBytes(updatedBytes);

            // Refresh in-memory list
            List<Transaction> updated = excelService.readTransactions(updatedBytes);
            appCtx.setTransactions(updated);

            // Async upload to WorkDrive
            ZohoWorkDriveService workDrive = (ZohoWorkDriveService) getServletContext().getAttribute("workDriveService");
            String filename = (String) getServletContext().getAttribute("excelFilename");
            workDrive.uploadFileAsync(updatedBytes, filename, appCtx.getWorkdriveFileId())
                    .thenRun(() -> log.info("WorkDrive upload complete after adding transaction"))
                    .exceptionally(ex -> { log.error("WorkDrive upload failed", ex); return null; });

            resp.sendRedirect(req.getContextPath() + "/transactions?success=1");
        } catch (Exception e) {
            log.error("Error adding transaction", e);
            resp.sendRedirect(req.getContextPath() + "/transactions?error=save_failed");
        }
    }
}
