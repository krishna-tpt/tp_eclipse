package com.expensemanager.servlet;

import com.expensemanager.model.AppConfig;
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
import java.util.List;

/**
 * GET /sync  → downloads latest Excel from WorkDrive, reloads cache
 * Returns JSON for AJAX polling from the UI.
 */
@WebServlet("/sync")
public class SyncServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(SyncServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        ZohoWorkDriveService workDrive = (ZohoWorkDriveService) getServletContext().getAttribute("workDriveService");
        ExcelService excelService      = (ExcelService)          getServletContext().getAttribute("excelService");
        String filename                = (String)                 getServletContext().getAttribute("excelFilename");
        AppContext appCtx              = AppContext.getInstance();

        try {
            String fileId = workDrive.findFileId(filename);
            if (fileId == null) {
                resp.getWriter().write("{\"status\":\"error\",\"message\":\"File not found in WorkDrive\"}");
                return;
            }
            byte[] bytes = workDrive.downloadFile(fileId);
            appCtx.setWorkbookBytes(bytes);
            appCtx.setWorkdriveFileId(fileId);

            List<Transaction> txns = excelService.readTransactions(bytes);
            AppConfig config = excelService.readConfig(bytes);
            appCtx.setTransactions(txns);
            appCtx.setConfig(config);

            log.info("Sync complete — {} transactions loaded", txns.size());
            resp.getWriter().write("{\"status\":\"ok\",\"count\":" + txns.size() + "}");
        } catch (Exception e) {
            log.error("Sync failed", e);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
