package com.expensemanager.service;

import com.expensemanager.model.AppConfig;
import com.expensemanager.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * In-memory cache for the workbook bytes + parsed data.
 * Single source of truth for the current state.
 *
 * WorkDrive is the persistent store; this is the runtime cache.
 * Reload via SyncServlet when needed.
 */
public class AppContext {

    private static final Logger log = LoggerFactory.getLogger(AppContext.class);
    private static AppContext instance;

    // Raw workbook bytes (latest downloaded from WorkDrive)
    private byte[] workbookBytes;
    private String workdriveFileId;

    // Parsed data
    private List<Transaction> transactions;
    private AppConfig config;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private AppContext() {}

    public static synchronized AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    public byte[] getWorkbookBytes() {
        lock.readLock().lock();
        try { return workbookBytes; }
        finally { lock.readLock().unlock(); }
    }

    public void setWorkbookBytes(byte[] bytes) {
        lock.writeLock().lock();
        try { this.workbookBytes = bytes; }
        finally { lock.writeLock().unlock(); }
    }

    public String getWorkdriveFileId() {
        lock.readLock().lock();
        try { return workdriveFileId; }
        finally { lock.readLock().unlock(); }
    }

    public void setWorkdriveFileId(String id) {
        lock.writeLock().lock();
        try { this.workdriveFileId = id; }
        finally { lock.writeLock().unlock(); }
    }

    public List<Transaction> getTransactions() {
        lock.readLock().lock();
        try { return transactions; }
        finally { lock.readLock().unlock(); }
    }

    public void setTransactions(List<Transaction> transactions) {
        lock.writeLock().lock();
        try { this.transactions = transactions; }
        finally { lock.writeLock().unlock(); }
    }

    public AppConfig getConfig() {
        lock.readLock().lock();
        try { return config; }
        finally { lock.readLock().unlock(); }
    }

    public void setConfig(AppConfig config) {
        lock.writeLock().lock();
        try { this.config = config; }
        finally { lock.writeLock().unlock(); }
    }

    public boolean isLoaded() {
        lock.readLock().lock();
        try { return workbookBytes != null && transactions != null; }
        finally { lock.readLock().unlock(); }
    }
}
