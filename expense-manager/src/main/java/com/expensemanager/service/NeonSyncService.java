package com.expensemanager.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.util.DBConnection;

/**
 * Syncs all transactions + master data to Neon (PostgreSQL cloud) DB. Uses
 * UPSERT (INSERT ... ON CONFLICT DO UPDATE) for idempotent sync.
 *
 * Config: System property neon.jdbc.url / neon.jdbc.user / neon.jdbc.password
 * OR environment variable NEON_DB_URL
 */
public class NeonSyncService {

	private static final Logger log = LoggerFactory.getLogger(NeonSyncService.class);
	private static java.util.Properties neonProps = null;

	// ── Neon connection ────────────────────────────────────────────
	private Connection neonConn() throws SQLException {
//		String url = System.getProperty("neon.jdbc.url",
//				System.getenv("NEON_DB_URL") != null ? System.getenv("NEON_DB_URL") : "");
//		String user = System.getProperty("neon.jdbc.user",
//				System.getenv("NEON_DB_USER") != null ? System.getenv("NEON_DB_USER") : "");
//		String pass = System.getProperty("neon.jdbc.password",
//				System.getenv("NEON_DB_PASSWORD") != null ? System.getenv("NEON_DB_PASSWORD") : "");
//		String url = AppContextListener.getContext().getInitParameter("NEON_DB_URL");
//		String user = AppContextListener.getContext().getInitParameter("NEON_DB_USER");
//		String pass = AppContextListener.getContext().getInitParameter("NEON_DB_PASSWORD");

//		String url = "jdbc:postgresql://ep-plain-meadow-apr9n2ix-pooler.c-7.us-east-1.aws.neon.tech/neondb?sslmode=require";
//		String user = "neondb_owner";
//		String pass = "npg_30bZIuKdaFvM";

		String url = resolve("neon.jdbc.url", "NEON_DB_URL");
		String user = resolve("neon.jdbc.user", "NEON_DB_USER");
		String pass = resolve("neon.jdbc.password", "NEON_DB_PASSWORD");

		if (url == null || url.isBlank())
			throw new SQLException("Neon DB URL not configured. "
					+ "Set 'neon.jdbc.url' in neon_config.properties, system property, or env var 'NEON_DB_URL'.");

		log.info("[NeonSync] Connecting to: {}", url.replaceAll("password=[^&]*", "password=***"));
		return DriverManager.getConnection(url, user, pass);
	}

	private String resolve(String sysProp, String envVar) {
		// 1. System property
		String v = System.getProperty(sysProp);
		if (v != null && !v.isBlank())
			return v.trim();

		// 2. Environment variable
		v = System.getenv(envVar);
		if (v != null && !v.isBlank())
			return v.trim();

		// 3. neon_config.properties from classpath
		if (neonProps == null) {
			neonProps = new java.util.Properties();
			try (java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("neon_config.properties")) {
				if (is != null) {
					neonProps.load(is);
					log.info("[NeonSync] Loaded neon_config.properties from classpath");
				} else {
					log.warn("[NeonSync] neon_config.properties not found on classpath");
				}
			} catch (Exception e) {
				log.error("[NeonSync] Failed to load neon_config.properties: {}", e.getMessage());
			}
		}
		v = neonProps.getProperty(sysProp);
		return (v != null) ? v.trim() : "";
	}

	// ── Main sync entry point ──────────────────────────────────────
	public SyncResult sync() {
		SyncResult result = new SyncResult();
		log.info("[NeonSync] Starting full sync...");

		try (Connection local = DBConnection.getInstance().getConnection(); Connection remote = neonConn()) {

			remote.setAutoCommit(false);

			try {
				// Master tables first (FK order)
				result.add(syncTable(local, remote, "cash_books",
						"SELECT id, name, description, created_at, is_active FROM cash_books",
						"INSERT INTO cash_books (id, name, description, created_at, is_active) VALUES (?, ?, ?, ?, ?) "
								+ "ON CONFLICT (id) DO UPDATE SET name=EXCLUDED.name, description = EXCLUDED.description, "
								+ "created_at = EXCLUDED.created_at,is_active = EXCLUDED.is_active",
						5));

				result.add(syncTable(local, remote, "categories", "SELECT id, name, type, created_at FROM categories",
						"INSERT INTO categories (id, name, type, created_at) VALUES (?,?,?::txn_type, ?) "
								+ "ON CONFLICT (id) DO UPDATE SET name=EXCLUDED.name , type=EXCLUDED.type, "
								+ "created_at=EXCLUDED.created_at",
						4));

				result.add(syncTable(local, remote, "sub_categories",
						"SELECT sub_categories_id, name, created, category_id FROM sub_categories",
						"INSERT INTO sub_categories (sub_categories_id, name, created, category_id) VALUES (?,?,?,?) "
								+ "ON CONFLICT (sub_categories_id) DO UPDATE SET name=EXCLUDED.name, "
								+ "created=EXCLUDED.created, category_id=EXCLUDED.category_id",
						4));

				result.add(syncTable(local, remote, "column_definitions",
						"SELECT id, type, col_key, col_name FROM column_definitions",
						"INSERT INTO column_definitions (id, col_name, col_key, type, created_at) VALUES (?, ?, ?, ?::txn_type, ?) "
								+ "ON CONFLICT (id) DO UPDATE SET col_name=EXCLUDED.col_name, "
								+ "col_key=EXCLUDED.col_key, type=EXCLUDED.type, created_at=EXCLUDED.created_at",
						5));

				// Transactions
				result.add(syncTable(local, remote, "transactions",
						"SELECT id, type, txn_datetime, amount, category_id, note, created_at, sub_categories_id, book_id FROM transactions",
						"INSERT INTO transactions (id, type, txn_datetime, amount, category_id, note, created_at, sub_categories_id, book_id) "
								+ "VALUES (?, ?::txn_type, ?, ?, ?, ?, ?, ?, ?)"
								+ "ON CONFLICT (id) DO UPDATE SET type=EXCLUDED.type, txn_datetime=EXCLUDED.txn_datetime, "
								+ "amount=EXCLUDED.amount, category_id=EXCLUDED.category_id, "
								+ "  note=EXCLUDED.note, created_at=EXCLUDED.created_at, "
								+ "sub_categories_id=EXCLUDED.sub_categories_id, book_id=EXCLUDED.book_id",
						9));

				result.add(syncTable(local, remote, "transaction_custom_values",
						"SELECT id, transaction_id, col_def_id, value FROM transaction_custom_values",
						"INSERT INTO transaction_custom_values (id, transaction_id, col_def_id, value) "
								+ "VALUES (?,?,?,?) ON CONFLICT (id) DO UPDATE SET transaction_id=EXCLUDED.transaction_id, col_def_id=EXCLUDED.col_def_id, value=EXCLUDED.value",
						4));

				result.add(syncTable(local, remote, "transaction_audit_log",
						"SELECT id, transaction_id, action, changed_by, changed_at, field_name, old_value, new_value, note FROM transaction_audit_log ",
						"INSERT INTO transaction_audit_log (id, transaction_id, action, changed_by, changed_at, field_name, old_value, new_value, note)"
								+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)" + " ON CONFLICT (id) DO NOTHING",
						9));

				result.add(syncTable(local, remote, "transaction_receipts",
						"SELECT id, transaction_id, file_name, file_type, file_data, file_size, uploaded_at FROM transaction_receipts ",
						"INSERT INTO transaction_receipts(id, transaction_id, file_name, file_type, file_data, file_size, uploaded_at)"
								+ "	VALUES (?, ?, ?, ?, ?, ?, ?)"
								+ " ON CONFLICT (id) DO UPDATE SET transaction_id=EXCLUDED.transaction_id, "
								+ "file_name=EXCLUDED.file_name, file_type=EXCLUDED.file_type, file_data=EXCLUDED.file_data, "
								+ "file_size=EXCLUDED.file_size, uploaded_at=EXCLUDED.uploaded_at",
						7));

				result.add(syncTable(local, remote, "backup_history",
						"SELECT id, file_name, file_path, file_size_bytes, backup_type, status, description, error_message, "
								+ "income_count, expense_count, created_at, completed_at, backupmode, external_id "
								+ "FROM backup_history ",
						"INSERT INTO backup_history(id, file_name, file_path, file_size_bytes, backup_type, status, description, error_message, income_count, expense_count, created_at, completed_at, backupmode, external_id) "
								+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" + " ON CONFLICT (id) "
								+ "DO UPDATE SET file_name = EXCLUDED.file_name, file_path = EXCLUDED.file_path, "
								+ "file_size_bytes = EXCLUDED.file_size_bytes, backup_type = EXCLUDED.backup_type, "
								+ "status = EXCLUDED.status, description = EXCLUDED.description, error_message = EXCLUDED.error_message, "
								+ "income_count = EXCLUDED.income_count, expense_count = EXCLUDED.expense_count, created_at = EXCLUDED.created_at, "
								+ "completed_at = EXCLUDED.completed_at, backupmode = EXCLUDED.backupmode, external_id = EXCLUDED.external_id",
						14));

				result.add(syncTable(local, remote, "budget_categories",
						"SELECT id, budget_id, category_id, cat_limit, alert_pct FROM budget_categories ",
						"INSERT INTO budget_categories(id, budget_id, category_id, cat_limit, alert_pct) VALUES (?, ?, ?, ?, ?)"
								+ " ON CONFLICT (id) DO UPDATE SET budget_id=EXCLUDED.budget_id, "
								+ "category_id=EXCLUDED.category_id, cat_limit=EXCLUDED.cat_limit, alert_pct=EXCLUDED.alert_pct ",
						5));

				result.add(syncTable(local, remote, "budgets",
						"SELECT id, book_id, year, month, overall_limit, created_at, updated_at FROM budgets ",
						"INSERT INTO budgets(id, book_id, year, month, overall_limit, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)"
								+ " ON CONFLICT (id) DO UPDATE SET book_id=EXCLUDED.book_id, "
								+ "year=EXCLUDED.year, month=EXCLUDED.month, overall_limit=EXCLUDED.overall_limit, "
								+ "created_at=EXCLUDED.created_at, updated_at=EXCLUDED.updated_at",
						7));

				result.add(syncTable(local, remote, "schedulers",
						"SELECT id, name, display_name, enabled, repeat_type, repeat_days, run_hour, run_minute, last_run_at, "
								+ "last_run_status, last_run_msg, next_run_at, created_at, updated_at FROM schedulers ",
						"INSERT INTO schedulers(id, name, display_name, enabled, repeat_type, repeat_days, run_hour, run_minute, "
								+ "last_run_at, last_run_status, last_run_msg, next_run_at, created_at, updated_at) "
								+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
								+ " ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, display_name = EXCLUDED.display_name, "
								+ "enabled = EXCLUDED.enabled, repeat_type = EXCLUDED.repeat_type, repeat_days = EXCLUDED.repeat_days, "
								+ "run_hour = EXCLUDED.run_hour, run_minute = EXCLUDED.run_minute, last_run_at = EXCLUDED.last_run_at, "
								+ "last_run_status = EXCLUDED.last_run_status, last_run_msg = EXCLUDED.last_run_msg, "
								+ "next_run_at = EXCLUDED.next_run_at, created_at = EXCLUDED.created_at, updated_at = EXCLUDED.updated_at",
						14));
				result.add(syncTable(local, remote, "scheduler_log",
						"SELECT id, scheduler_id, started_at, finished_at, status, message, rows_synced FROM scheduler_log ",
						"INSERT INTO scheduler_log(id, scheduler_id, started_at, finished_at, status, message, rows_synced) VALUES (?, ?, ?, ?, ?, ?, ?)"
								+ " ON CONFLICT (id) DO UPDATE SET scheduler_id=EXCLUDED.scheduler_id, "
								+ "started_at=EXCLUDED.started_at, finished_at=EXCLUDED.finished_at, status=EXCLUDED.status, "
								+ "message=EXCLUDED.message, rows_synced=EXCLUDED.rows_synced",
						7));

				remote.commit();
				result.success = true;
				log.info("[NeonSync] Sync complete. Total rows: {}", result.totalRows);

			} catch (Exception ex) {
				remote.rollback();
				result.success = false;
				result.error = ex.getMessage();
				log.error("[NeonSync] Sync failed, rolled back: {}", ex.getMessage(), ex);
			}

		} catch (SQLException ex) {
			result.success = false;
			result.error = ex.getMessage();
			log.error("[NeonSync] Connection failed: {}", ex.getMessage(), ex);
		}

		return result;
	}

	// ── Generic table sync ─────────────────────────────────────────
	private TableResult syncTable(Connection local, Connection remote, String tableName, String selectSql,
			String upsertSql, int colCount) {
		TableResult tr = new TableResult(tableName);
		log.debug("[NeonSync] Syncing table: {}", tableName);
		try (PreparedStatement sel = local.prepareStatement(selectSql);
				PreparedStatement ups = remote.prepareStatement(upsertSql)) {

			ResultSet rs = sel.executeQuery();
			int batch = 0;
			while (rs.next()) {
				for (int i = 1; i <= colCount; i++) {
					ups.setObject(i, rs.getObject(i));
				}
				ups.addBatch();
				batch++;
				if (batch % 500 == 0) {
					ups.executeBatch();
					ups.clearBatch();
				}
			}
			if (batch % 500 != 0)
				ups.executeBatch();
			tr.rows = batch;
			log.debug("[NeonSync] {} → {} rows", tableName, batch);

		} catch (SQLException ex) {
			tr.error = ex.getMessage();
			log.error("[NeonSync] Error syncing {}: {}", tableName, ex.getMessage());
			throw new RuntimeException("Table sync failed: " + tableName, ex);
		}
		return tr;
	}

	// ── Result classes ─────────────────────────────────────────────
	public static class SyncResult {
		public boolean success = false;
		public String error;
		public int totalRows = 0;
		public List<TableResult> tables = new ArrayList<>();

		public void add(TableResult tr) {
			tables.add(tr);
			totalRows += tr.rows;
		}

		public String getSummary() {
			if (!success)
				return "FAILED: " + error;
			StringBuilder sb = new StringBuilder("Synced: ");
			for (TableResult t : tables)
				sb.append(t.table).append("(").append(t.rows).append(") ");
			return sb.toString().trim();
		}
	}

	public static class TableResult {
		public String table;
		public int rows = 0;
		public String error;

		TableResult(String table) {
			this.table = table;
		}
	}
}