package com.expensemanager.util;

import com.expensemanager.model.AppConfig;
import com.expensemanager.model.Transaction;
import com.expensemanager.service.AppContext;
import com.expensemanager.service.ExcelService;
import com.expensemanager.service.ZohoWorkDriveService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Runs on app startup:
 *  1. Reads Zoho credentials from web.xml context-params
 *  2. Downloads Excel from WorkDrive
 *  3. Parses and caches in AppContext
 */
@WebListener
public class AppStartupListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(AppStartupListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        String clientId      = ctx.getInitParameter("ZOHO_CLIENT_ID");
        String clientSecret  = ctx.getInitParameter("ZOHO_CLIENT_SECRET");
        String refreshToken  = ctx.getInitParameter("ZOHO_REFRESH_TOKEN");
        String folderId      = ctx.getInitParameter("WORKDRIVE_FOLDER_ID");
        String filename      = ctx.getInitParameter("EXCEL_FILENAME");

        ZohoWorkDriveService workDrive = new ZohoWorkDriveService(clientId, clientSecret, refreshToken, folderId);
        ExcelService excelService = new ExcelService();

        // Store services in ServletContext for access from servlets
        ctx.setAttribute("workDriveService", workDrive);
        ctx.setAttribute("excelService", excelService);
        ctx.setAttribute("excelFilename", filename);

        log.info("Starting Expense Manager — downloading workbook from WorkDrive...");
        try {
            String fileId = workDrive.findFileId(filename);
            if (fileId == null) {
                log.warn("Excel file '{}' not found in WorkDrive. App will wait for manual sync.", filename);
                return;
            }
            byte[] bytes = workDrive.downloadFile(fileId);
            AppContext.getInstance().setWorkbookBytes(bytes);
            AppContext.getInstance().setWorkdriveFileId(fileId);

            List<Transaction> txns = excelService.readTransactions(bytes);
            AppConfig config = excelService.readConfig(bytes);
            AppContext.getInstance().setTransactions(txns);
            AppContext.getInstance().setConfig(config);

            log.info("Loaded {} transactions, {} income categories, {} expense categories",
                    txns.size(),
                    config.getIncomeCategories().size(),
                    config.getExpenseCategories().size());
        } catch (Exception e) {
            log.error("Failed to load workbook from WorkDrive on startup", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Object service = sce.getServletContext().getAttribute("workDriveService");
        if (service instanceof ZohoWorkDriveService wds) {
            wds.shutdown();
        }
    }
}
