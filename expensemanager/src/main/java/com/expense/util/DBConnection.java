package com.expense.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DBConnection {

	private static final Logger LOG = Logger.getLogger(DBConnection.class.getName());

	private static final String HOST = System.getProperty("db.host", "localhost");
	private static final String PORT = System.getProperty("db.port", "5432");
	private static final String DB_NAME = System.getProperty("db.name", "expense_manager");
	private static final String USERNAME = System.getProperty("db.user", "postgres");
	private static final String PASSWORD = System.getProperty("db.password", "postgres");
	// ─────────────────────────────────────────────────────────────────────────

	private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;

	static {
		try {
			Class.forName("org.postgresql.Driver");
			LOG.info("[DBConnection] PostgreSQL driver loaded. URL: " + URL);
		} catch (ClassNotFoundException e) {
			LOG.severe("[DBConnection] PostgreSQL driver NOT found: " + e.getMessage());
			throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USERNAME, PASSWORD);
	}
}