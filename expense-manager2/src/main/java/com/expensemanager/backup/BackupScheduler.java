package com.expensemanager.backup;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.expensemanager.model.BackupMetadata.BackupType;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class BackupScheduler implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(BackupScheduler.class.getName());
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try { new com.expensemanager.dao.BackupDAO().createTableIfNotExists(); LOG.info("[BackupScheduler] Table ready."); }
        catch (Exception e) { LOG.severe("[BackupScheduler] Table create failed: "+e.getMessage()); }

        int hour   = getInt(sce,"backup.schedule.hour",0);
        int minute = getInt(sce,"backup.schedule.minute",0);
        long delay = secondsUntil(LocalTime.now(), LocalTime.of(hour,minute));

        scheduler = Executors.newSingleThreadScheduledExecutor(r->{ Thread t=new Thread(r,"BackupScheduler"); t.setDaemon(true); return t; });
        scheduler.scheduleAtFixedRate(this::run, delay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
        LOG.info(String.format("[BackupScheduler] Daily backup at %02d:%02d. First in %d min.", hour, minute, delay/60));
    }

    @Override public void contextDestroyed(ServletContextEvent sce) { if(scheduler!=null) scheduler.shutdownNow(); }

    private void run() {
        try { new BackupService().createBackup("Scheduled daily backup", BackupType.SCHEDULED);
              LOG.info("[BackupScheduler] Scheduled backup done."); }
        catch(Exception e) { LOG.severe("[BackupScheduler] FAILED: "+e.getMessage()); }
    }

    private long secondsUntil(LocalTime now, LocalTime target) {
        long diff = target.toSecondOfDay() - now.toSecondOfDay();
        return diff <= 0 ? diff+86400 : diff;
    }
    private int getInt(ServletContextEvent sce, String name, int def) {
        String v = sce.getServletContext().getInitParameter(name);
        try { return v!=null?Integer.parseInt(v):def; } catch(Exception e){ return def; }
    }
}