package com.expense.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Simple connection pool for PostgreSQL.
 * Initialised once by AppContextListener on startup.
 */
public class DBConnection {

	private static final Logger log = LoggerFactory.getLogger(DBConnection.class);
	private static DBConnection instance;

	private final BlockingQueue<Connection> pool;
	private final String url;
	private final String user;
	private final String password;
	private static final int POOL_SIZE = 10;

	private DBConnection(String url, String user, String password) throws SQLException {
		this.url = url;
		this.user = user;
		this.password = password;
		this.pool = new ArrayBlockingQueue<>(POOL_SIZE);
		for (int i = 0; i < POOL_SIZE; i++) {
			pool.offer(createConnection());
		}
		log.info("DB pool initialised ({} connections) → {}", POOL_SIZE, url);
	}

	public static synchronized void init(String url, String user, String password) throws SQLException {
		if (instance == null) {
			instance = new DBConnection(url, user, password);
		}
	}

	public static DBConnection getInstance() {
		if (instance == null)
			throw new IllegalStateException("DBConnection not initialised");
		return instance;
	}

	private Connection createConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	/** Borrow a connection (blocks up to 5s if pool exhausted) */
	public Connection getConnection() throws SQLException {
		try {
			Connection conn = pool.poll(5, java.util.concurrent.TimeUnit.SECONDS);
			if (conn == null || conn.isClosed()) {
				conn = createConnection();
			}
			return conn;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new SQLException("Interrupted while waiting for DB connection", e);
		}
	}

	/** Return connection to pool */
	public void releaseConnection(Connection conn) {
		if (conn != null) {
			pool.offer(conn);
		}
	}

	public static void shutdown() {
		if (instance != null) {
			instance.pool.forEach(c -> {
				try {
					c.close();
				} catch (Exception ignored) {
				}
			});
			instance = null;
			log.info("DB pool shut down");
		}
	}
}
