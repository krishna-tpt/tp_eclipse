package org.michelin.filemanager.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Owns a single JDBC connection for the lifetime of the run. Provides a
 * transaction template so callers don't reinvent autoCommit/commit/rollback.
 * AutoCloseable so try-with-resources at the application level closes it.
 */
public class Database implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(Database.class);

    private final Connection connection;

    public Database(Config.DbConfig cfg) {
        try {
            Properties props = new Properties();
            props.setProperty("user", cfg.user());
            props.setProperty("password", cfg.password());
            props.setProperty("ApplicationName", cfg.applicationName());
            props.setProperty("loginTimeout", String.valueOf(cfg.connectTimeoutS()));
            props.setProperty("tcpKeepAlive", "true");
            // Enforce SSL unless URL explicitly sets a mode.
            if (cfg.url() != null && !cfg.url().toLowerCase().contains("sslmode=")) {
                props.setProperty("sslmode", "require");
            }
            this.connection = DriverManager.getConnection(cfg.url(), props);
            if (cfg.statementTimeoutMs() > 0) {
                try (var stmt = connection.createStatement()) {
                    stmt.execute("SET statement_timeout = " + cfg.statementTimeoutMs());
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to open DB connection", e);
        }
    }

    public Connection connection() {
        return connection;
    }

    /**
     * Runs the lambda inside a transaction: turns off autoCommit, executes,
     * commits on success or rolls back on exception, restores prior autoCommit.
     *
     * Use this from every code path that needs more than a single auto-committed
     * statement — eliminates the manual try/commit/catch/rollback/finally dance.
     */
    public <T> T inTransaction(SqlFunction<T> fn) {
        boolean prior;
        try {
            prior = connection.getAutoCommit();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DataAccessException("setAutoCommit failed", e);
        }
        try {
            T result = fn.apply(connection);
            connection.commit();
            return result;
        } catch (Exception e) {
            try { connection.rollback(); } catch (SQLException rb) {
                log.warn("rollback failed: {}", rb.getMessage());
            }
            if (e instanceof DataAccessException dae) throw dae;
            throw new DataAccessException("transaction failed", e);
        } finally {
            try { connection.setAutoCommit(prior); } catch (SQLException ignore) { /* shutdown */ }
        }
    }

    /** Void overload for transactions that don't return a value. */
    public void inTransaction(SqlRunnable fn) {
        inTransaction(c -> { fn.apply(c); return null; });
    }

    @Override
    public void close() {
        try {
            if (!connection.isClosed()) connection.close();
        } catch (SQLException e) {
            log.warn("connection close failed: {}", e.getMessage());
        }
    }
}
