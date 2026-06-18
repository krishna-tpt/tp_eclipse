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
		
		String url = "jdbc:postgresql://ep-plain-meadow-apr9n2ix-pooler.c-7.us-east-1.aws.neon.tech/neondb?sslmode=require";
		String user = "neondb_owner";
		String pass = "npg_30bZIuKdaFvM";

		if (url.isBlank())
			throw new SQLException(
					"Neon DB URL not configured. " + "Set system property 'neon.jdbc.url' or env var 'NEON_DB_URL'.");

		return DriverManager.getConnection(url, user, pass);
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
						"SELECT id, name, currency, created_at FROM cash_books",
						"INSERT INTO cash_books (id, name, currency, created_at) VALUES (?,?,?,?) "
								+ "ON CONFLICT (id) DO UPDATE SET name=EXCLUDED.name, currency=EXCLUDED.currency",
						4));

				result.add(syncTable(local, remote, "categories", "SELECT id, name, type, created_at FROM categories",
						"INSERT INTO categories (id, name, type, created_at) VALUES (?,?,?,?::txn_type) "
								+ "ON CONFLICT (id) DO UPDATE SET name=EXCLUDED.name",
						4));

				result.add(syncTable(local, remote, "sub_categories",
						"SELECT sub_categories_id, category_id, name FROM sub_categories",
						"INSERT INTO sub_categories (sub_categories_id, category_id, name) VALUES (?,?,?) "
								+ "ON CONFLICT (sub_categories_id) DO UPDATE SET name=EXCLUDED.name",
						3));

				result.add(syncTable(local, remote, "column_definitions",
						"SELECT id, type, col_key, col_name FROM column_definitions",
						"INSERT INTO column_definitions (id, type, col_key, col_name) VALUES (?,?,?,?) "
								+ "ON CONFLICT (id) DO UPDATE SET col_name=EXCLUDED.col_name",
						4));

				// Transactions
				result.add(syncTable(local, remote, "transactions",
						"SELECT id, book_id, type, amount, category_id, subcategory_id, "
								+ "       note, date_time, created_at FROM transactions",
						"INSERT INTO transactions (id, book_id, type, amount, category_id, "
								+ "  subcategory_id, note, date_time, created_at) "
								+ "VALUES (?,?,?::txn_type,?,?,?,?,?,?) "
								+ "ON CONFLICT (id) DO UPDATE SET amount=EXCLUDED.amount, "
								+ "  note=EXCLUDED.note, category_id=EXCLUDED.category_id",
						9));

				result.add(syncTable(local, remote, "transaction_custom_values",
						"SELECT id, transaction_id, col_key, col_value FROM transaction_custom_values",
						"INSERT INTO transaction_custom_values (id, transaction_id, col_key, col_value) "
								+ "VALUES (?,?,?,?) ON CONFLICT (id) DO UPDATE SET col_value=EXCLUDED.col_value",
						4));

				result.add(syncTable(local, remote, "transaction_audit_log",
						"SELECT id, transaction_id, field_name, old_value, new_value, "
								+ "       changed_by, changed_at FROM transaction_audit_log",
						"INSERT INTO transaction_audit_log (id, transaction_id, field_name, "
								+ "  old_value, new_value, changed_by, changed_at) "
								+ "VALUES (?,?,?,?,?,?,?) ON CONFLICT (id) DO NOTHING",
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