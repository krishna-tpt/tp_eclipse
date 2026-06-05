package com.expensemanager.servlet;

import com.expensemanager.model.AppConfig;
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

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CategoryServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        AppContext appCtx = AppContext.getInstance();

        if (!appCtx.isLoaded()) {
            resp.sendError(503, "Workbook not loaded.");
            return;
        }

        String action   = req.getParameter("action");    // "category" or "column"
        String name     = req.getParameter("name");
        String type     = req.getParameter("type");      // I | E | BOTH

        if (name == null || name.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/transactions?error=empty_name");
            return;
        }

        ExcelService excelService = (ExcelService) getServletContext().getAttribute("excelService");
        ZohoWorkDriveService workDrive = (ZohoWorkDriveService) getServletContext().getAttribute("workDriveService");
        String filename = (String) getServletContext().getAttribute("excelFilename");

        try {
            byte[] updated;
            if ("column".equalsIgnoreCase(action)) {
                // Add custom column to Data sheet
                updated = excelService.addCustomColumn(appCtx.getWorkbookBytes(), name);
                log.info("Added custom column: '{}'", name);
            } else {
                // Add category to Config sheet
                updated = excelService.addCategory(appCtx.getWorkbookBytes(), name, type != null ? type : "BOTH");
                log.info("Added category '{}' (type={})", name, type);
            }

            appCtx.setWorkbookBytes(updated);

            // Refresh config
            AppConfig newConfig = excelService.readConfig(updated);
            appCtx.setConfig(newConfig);

            // Async upload
            workDrive.uploadFileAsync(updated, filename, appCtx.getWorkdriveFileId())
                    .thenRun(() -> log.info("WorkDrive upload done after category/column add"))
                    .exceptionally(ex -> { log.error("WorkDrive upload error", ex); return null; });

            resp.sendRedirect(req.getContextPath() + "/transactions?success=config");
        } catch (Exception e) {
            log.error("Error adding category/column", e);
            resp.sendRedirect(req.getContextPath() + "/transactions?error=config_failed");
        }
    }
}
