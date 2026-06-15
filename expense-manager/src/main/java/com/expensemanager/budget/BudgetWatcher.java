package com.expensemanager.budget;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.expensemanager.dao.BudgetDAO;
import com.expensemanager.model.Budget;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Runs every 5 minutes.
 * Checks if any budget has crossed its alert threshold.
 * Fires SSE "budget_alert" event → browser shows real-time notification.
 */
@WebListener
public class BudgetWatcher implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(BudgetWatcher.class.getName());
    private ScheduledExecutorService exec;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            new BudgetDAO().createTablesIfNotExist();
            LOG.info("[BudgetWatcher] Tables ready.");
        } catch (Exception e) {
            LOG.severe("[BudgetWatcher] Init failed: " + e.getMessage());
        }

        exec = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "BudgetWatcher");
            t.setDaemon(true);
            return t;
        });
        // Check every 5 minutes
        exec.scheduleAtFixedRate(this::checkBudgets, 10, 300, TimeUnit.SECONDS);
        LOG.info("[BudgetWatcher] Started — checking every 5 minutes.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (exec != null) exec.shutdownNow();
    }

    private void checkBudgets() {
        try {
            BudgetDAO dao  = new BudgetDAO();
            LocalDate now  = LocalDate.now();
            int year       = now.getYear();
            int month      = now.getMonthValue();

            List<Budget> alerts = dao.getAlertBudgets(year, month);

            for (Budget b : alerts) {
                if (dao.alertedTodayFor(b.getId())) continue;

                double pct = b.getSpentPct();
                dao.logAlert(b.getId(), pct, b.getSpent());

                // SSE broadcast
                String eventType = b.isOverBudget() ? "budget_over" : "budget_alert";
                String json = String.format(
                    "{\"category\":\"%s\",\"spent\":\"%.2f\",\"limit\":\"%.2f\"," +
                    "\"pct\":\"%.1f\",\"remaining\":\"%.2f\",\"over\":%b}",
                    b.getCategory(),
                    b.getSpent(),
                    b.getAmount(),
                    pct,
                    b.getRemaining(),
                    b.isOverBudget()
                );
                SSEManager.broadcast(eventType, json);

                LOG.info("[BudgetWatcher] Alert: " + b.getCategory() +
                         " at " + String.format("%.1f", pct) + "%");
            }
        } catch (Exception e) {
            LOG.severe("[BudgetWatcher] Error: " + e.getMessage());
        }
    }
}
