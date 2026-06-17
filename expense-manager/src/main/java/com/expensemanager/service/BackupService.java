package com.expensemanager.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.dao.BackupDAO;
import com.expensemanager.dao.ReceiptDAO;
import com.expensemanager.model.BackupMetadata;
import com.expensemanager.model.BackupMetadata.BackupMode;
import com.expensemanager.model.BackupMetadata.BackupStatus;
import com.expensemanager.model.BackupMetadata.BackupType;
import com.expensemanager.model.Receipt;
import com.expensemanager.servlet.BackupServlet;
import com.expensemanager.util.DBConnection;

/**
 * Core backup/restore engine. ZIP contents: backup_info.txt, income.csv,
 * expense.csv, custom_columns.csv, custom_column_values.csv,
 * income_categories.csv, expense_categories.csv
 */
public class BackupService {

	private static final Logger log = LoggerFactory.getLogger(BackupServlet.class);
	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
	private static final String DEFAULT_DIR = System.getProperty("user.home") + "/expense_backups";
	private final BackupDAO dao = new BackupDAO();
	private final FilesApiService fas = new FilesApiService();

	// ── CREATE ────────────────────────────────────────────────────────────────
	public BackupMetadata createBackup(String description, BackupType type, BackupMode backupMode) throws Exception {
		Path dir = Paths.get(System.getProperty("expense.backup.dir", DEFAULT_DIR));
		log.debug("Backup Path : {} " + dir);
		Files.createDirectories(dir);
		String ts = LocalDateTime.now().format(FMT);
		String fileName = "backup_" + ts + ".zip";
		Path filePath = dir.resolve(fileName);

		BackupMetadata meta = new BackupMetadata();
		meta.setFileName(fileName);
		meta.setFilePath(filePath.toString());
		meta.setBackupType(type);
		meta.setStatus(BackupStatus.PENDING);
		meta.setDescription(description != null ? description : "");
		meta.setCreatedAt(LocalDateTime.now());
		meta.setMode(backupMode);
		int id = dao.insert(meta);
		meta.setId(id);

		try (Connection con = DBConnection.getInstance().getConnection()) {
			int inc = dao.countRows("INCOME"), exp = dao.countRows("EXPENSE");
			try (ZipOutputStream zos = new ZipOutputStream(
					new BufferedOutputStream(new FileOutputStream(filePath.toFile())))) {
				writeInfo(zos, ts, inc, exp, description);
				writeCSV(zos, con, "transactions.csv", "SELECT * FROM transactions ORDER BY id");
				writeCSV(zos, con, "cash_books.csv", "SELECT * From cash_books ORDER BY id");
				writeCSV(zos, con, "categories.csv", "SELECT * FROM categories ORDER BY id");
				writeCSV(zos, con, "column_definitions.csv", "SELECT * FROM column_definitions ORDER BY id");
				writeCSV(zos, con, "sub_categories.csv", "SELECT * FROM sub_categories ORDER BY sub_categories_id");
				writeCSV(zos, con, "transaction_audit_log.csv", "SELECT * FROM transaction_audit_log ORDER BY id");
				writeCSV(zos, con, "transaction_custom_values.csv",
						"SELECT * FROM transaction_custom_values ORDER BY id");
//				writeCSV(zos, con, "transaction_receipts.csv", "SELECT * FROM transaction_receipts ORDER BY id desc");
				writeCSV(zos, con, "transaction_receipts.csv", "Select id, transaction_id, file_name, file_type, file_size, uploaded_at From transaction_receipts  order by id desc");
				writeFileBackup(zos, con, filePath, "Receipts", "SELECT * FROM transaction_receipts ORDER BY id");
			}
			long size = Files.size(filePath);
			if (backupMode == backupMode.ONLINE) {
				dao.updateCompletion(id, BackupStatus.PENDING, size, inc, exp, null);
				meta.setStatus(BackupStatus.PENDING);
			} else {
				dao.updateCompletion(id, BackupStatus.SUCCESS, size, inc, exp, null);
				meta.setStatus(BackupStatus.SUCCESS);
			}
			meta.setFileSizeBytes(size);
			meta.setIncomeCount(inc);
			meta.setExpenseCount(exp);
		} catch (Exception ex) {
			log.debug("createBackup Method : {}" + ex.getMessage());
			dao.updateCompletion(id, BackupStatus.FAILED, 0, 0, 0, ex.getMessage());
			meta.setStatus(BackupStatus.FAILED);
			throw ex;
		} finally {
			if (backupMode == backupMode.ONLINE) {
				String external_id = fas.UploadFile(filePath.toFile());
				Files.deleteIfExists(filePath); // Upload success → local zip delete
				log.info("Local backup deleted after upload: {}", fileName);
				log.info("Uploading --> ResourcedID : {}", external_id);
				if (external_id != null) {
					log.info("Upload Success --> ResourcedID : {}", external_id);
					dao.updateExternalID(id, BackupStatus.SUCCESS, external_id);
					meta.setStatus(BackupStatus.SUCCESS);
					meta.setExternal_ID(external_id);

				} else {
					dao.updateStatus(id, BackupStatus.FAILED);
					meta.setStatus(BackupStatus.FAILED);
					log.warn("Upload failed --> ResourcedID : {}", external_id);
				}
			}
		}
		return meta;
	}

	// ── RESTORE ───────────────────────────────────────────────────────────────
	public void restoreBackup(int backupId) throws Exception {
		BackupMetadata meta = dao.getById(backupId);
		if (meta == null) {
			log.debug("Backup not found: {}", backupId);
			throw new Exception("Backup not found: " + backupId);
		}
		if (!meta.isRestorable()) {
			log.debug("Backup not restorable: {}", meta.getStatus());
			throw new Exception("Backup not restorable: " + meta.getStatus());
		}

		if (meta.getMode() == BackupMode.ONLINE) {
			log.debug("Backup file ID: {}", meta.getExternal_ID());
			if (meta.getExternal_ID() != null) {
				byte[] result = fas.downloadFile(meta.getExternal_ID());
				Path savePath = Paths.get(meta.getFilePath());
				Files.write(savePath, result);
				log.debug("Saved: {}", savePath);
			}
		}
		Path zipPath = Paths.get(meta.getFilePath());
		if (!Files.exists(zipPath)) {
			log.debug("Backup file missing: {}", zipPath);
			throw new Exception("Backup file missing: " + zipPath);
		}

		createBackup("Auto-backup before restore of #" + backupId, BackupType.AUTO_BEFORE_RESTORE, meta.getMode());
		dao.updateStatus(backupId, BackupStatus.RESTORING);

		try (Connection con = DBConnection.getInstance().getConnection()) {
			con.setAutoCommit(false);
			try (ZipFile zip = new ZipFile(zipPath.toFile())) {

				truncate(con);
				restoreCSV(con, zip, "cash_books.csv",
						"INSERT INTO cash_books (id, name, description, created_at, is_active) VALUES (?,?,?,?,?)",
						r -> new Object[] { iOf(r[0]), r[1], r[2], tsOf(r[3]), boolOf(r[4]) });

				restoreCSV(con, zip, "categories.csv",
						"INSERT INTO categories (id, name, type, created_at) VALUES (?,?,?::txn_type,?)",
						r -> new Object[] { iOf(r[0]), r[1], r[2], tsOf(r[3]) });

				restoreCSV(con, zip, "column_definitions.csv",
						"INSERT INTO column_definitions (id, col_name, col_key, type, created_at) VALUES (?,?,?,?,?)",
						r -> new Object[] { iOf(r[0]), r[1], r[2], r[3], tsOf(r[4]) });

				restoreCSV(con, zip, "sub_categories.csv",
						"INSERT INTO sub_categories (sub_categories_id, name, created, category_id) VALUES (?,?,?,?)",
						r -> new Object[] { iOf(r[0]), r[1], tsOf(r[2]), iOf(r[3]) });

				restoreCSV(con, zip, "transactions.csv",
						"INSERT INTO transactions (id, type, txn_datetime, amount, category_id, note, created_at, sub_categories_id, book_id) VALUES (?,?::txn_type,?,?,?,?,?,?,?)",
						r -> new Object[] { iOf(r[0]), r[1], tsOf(r[2]), bdOf(r[3]), iOf(r[4]), r[5], tsOf(r[6]),
								iOf(r[7]), iOf(r[8]) });
				restoreCSV(con, zip, "transaction_custom_values.csv",
						"INSERT INTO transaction_custom_values (id, transaction_id, col_def_id, value) VALUES (?,?,?,?)",
						r -> new Object[] { iOf(r[0]), iOf(r[1]), iOf(r[2]), r[3] });

				restoreCSV(con, zip, "transaction_audit_log.csv",
						"INSERT INTO transaction_audit_log (id, transaction_id, action, changed_by, changed_at, field_name, old_value, new_value, note) VALUES (?,?,?,?,?,?,?,?,?)",
						r -> new Object[] { iOf(r[0]), iOf(r[1]), r[2], r[3], tsOf(r[4]), r[5], r[6], r[7], r[8] });

				restoreCSV(con, zip, "transaction_receipts.csv",
						"INSERT INTO transaction_receipts (id, transaction_id, file_name, file_type, file_size, uploaded_at) VALUES (?,?,?,?,?,?)",
						r -> new Object[] { iOf(r[0]), iOf(r[1]), r[2], r[3], iOf(r[4]), tsOf(r[5]) });
				con.commit();

				restoreFiles(con, zipPath, "Receipts");

				resetSeq(con);
			}
			con.commit();
			dao.updateStatus(backupId, BackupStatus.RESTORED);
		} catch (Exception ex) {
			log.debug("restoreBackup failed: {}", ex.getMessage());
			// ✅ rollback add
			try (Connection con2 = DBConnection.getInstance().getConnection()) {
				con2.rollback();
			} catch (Exception ignore) {
			}
			dao.updateStatus(backupId, BackupStatus.SUCCESS);
			throw ex;
		}
	}

	private void restoreFiles(Connection con, Path zipPath, String folderName)
			throws FileNotFoundException, IOException, SQLException {
		ReceiptDAO rDAO = new ReceiptDAO();
		ZipEntry entry;

		try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipPath.toFile())));) {
			while ((entry = zis.getNextEntry()) != null) {
				String entryName = entry.getName();

				if (entry.isDirectory() || !entryName.startsWith(folderName + "/")) {
					continue;
				}

				int slashIdx = entryName.indexOf("/") + 1;
				String name = entryName.substring(slashIdx);

				if (name.isEmpty()) {
					continue;
				}

				int underIdx = name.indexOf("_");
				if (underIdx < 0) {
					log.debug("restoreFiles: skip invalid name: {}", name);
					continue;
				}

				int receiptId;
				try {
					receiptId = Integer.parseInt(name.substring(0, underIdx));
				} catch (NumberFormatException e) {
					log.debug("restoreFiles: invalid receiptId in: {}", name);
					continue;
				}

				String filename = name.substring(underIdx + 1);
				byte[] fileData = zis.readAllBytes();

				log.debug("restoreFiles: receiptId={} filename={} size={}", receiptId, filename, fileData.length);

				Receipt receipt = rDAO.findById(receiptId);
				if (receipt == null) {
					log.debug("restoreFiles: receipt not found in DB: id={}", receiptId);
					continue;
				}

				receipt.setFileData(fileData);
				rDAO.uploadReceipt(receipt);
			}
		} catch (Exception e) {
			log.debug("restoreFiles method : {}", e.getMessage());
		}
	}

	public byte[] getBackupBytes(int id) throws Exception {
		BackupMetadata meta = dao.getById(id);
		if (meta == null)
			throw new Exception("Not found");

		if (meta.getMode() == BackupMode.ONLINE) {
			log.debug("Backup file ID: {}", meta.getExternal_ID());
			if (meta.getExternal_ID() != null) {
				byte[] result = fas.downloadFile(meta.getExternal_ID());
				Path savePath = Paths.get(meta.getFilePath());
				Files.write(savePath, result);
				log.debug("Saved: {}", savePath);
			}
		}
		Path p = Paths.get(meta.getFilePath());
		if (!Files.exists(p))
			throw new Exception("File missing");
		return Files.readAllBytes(p);
	}

	public BackupMetadata registerUploadedBackup(byte[] zipBytes, String origName, String desc) throws Exception {
		Path dir = Paths.get(System.getProperty("expense.backup.dir", DEFAULT_DIR));
		Files.createDirectories(dir);
		String fileName = "uploaded_" + LocalDateTime.now().format(FMT) + "_" + origName;
		Path dest = dir.resolve(fileName);
		Files.write(dest, zipBytes);
		validateZip(dest);
		BackupMetadata m = new BackupMetadata();
		m.setFileName(fileName);
		m.setFilePath(dest.toString());
		m.setFileSizeBytes(zipBytes.length);
		m.setBackupType(BackupType.MANUAL);
		m.setStatus(BackupStatus.SUCCESS);
		m.setDescription("Uploaded: " + (desc != null ? desc : origName));
		m.setCreatedAt(LocalDateTime.now());
		int id = dao.insert(m);
		dao.updateCompletion(id, BackupStatus.SUCCESS, zipBytes.length, 0, 0, null);
		m.setId(id);
		return m;
	}

	public void deleteBackup(int id) throws Exception {
		BackupMetadata meta = dao.getById(id);
		if (meta != null) {
			if (meta.getMode() == BackupMode.ONLINE) {
				log.debug("Online file deletion started...");
				boolean isDeleted = fas.deleteFile(meta.getExternal_ID());
				log.debug("Cloud file deletion completed...");
				if (isDeleted) {
					dao.delete(id);
				} else {
					log.warn("File deletion failed : {}", meta.getExternal_ID());
				}
			} else {
				Files.deleteIfExists(Paths.get(meta.getFilePath()));
				log.debug("Local file deletion completed...");
			}
		}
	}

	// ── Helpers ───────────────────────────────────────────────────────────────
	private void writeInfo(ZipOutputStream zos, String ts, int inc, int exp, String desc) throws IOException {
		zos.putNextEntry(new ZipEntry("backup_info.txt"));
		zos.write(("ExpenseIQ Backup\nTimestamp: " + ts + "\nIncome: " + inc + "\nExpense: " + exp + "\nDesc: "
				+ (desc != null ? desc : "") + "\n").getBytes("UTF-8"));
		zos.closeEntry();
	}

	private void writeFileBackup(ZipOutputStream zos, Connection con, Path path, String entry, String sql)
			throws IOException {
		try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				int receiptId = rs.getInt("id");
				int txnId = rs.getInt("transaction_id");
				String fileName = rs.getString("file_name");
				byte[] fileData = rs.getBytes("file_data");

				if (fileData == null || fileData.length == 0) {
					log.debug("  [SKIP] receipt_id= {}  — empty data", receiptId);
					continue;
				}
				String safeFileName = entry + "/" + receiptId + "_" + fileName;
				zos.putNextEntry(new ZipEntry(safeFileName));
				zos.write(fileData);

				System.out.printf("[SAVED] receipt_id=%-4d  txn=%-4d  file=%s  size=%d bytes%n", receiptId, txnId,
						safeFileName, fileData.length);

			}

		} catch (Exception e) {
			log.debug("writeCSV Method : {}, Error : {}", entry, e.getMessage());
			log.debug("SQL --> {}", sql);
		}
		zos.closeEntry();
	}

	private void writeCSV(ZipOutputStream zos, Connection con, String entry, String sql) throws Exception {
		zos.putNextEntry(new ZipEntry(entry));
		try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			ResultSetMetaData rm = rs.getMetaData();
			int n = rm.getColumnCount();
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i <= n; i++) {
				if (i > 1)
					sb.append(',');
				sb.append(esc(rm.getColumnName(i)));
			}
			sb.append('\n');
			zos.write(sb.toString().getBytes("UTF-8"));
			while (rs.next()) {
				sb.setLength(0);
				for (int i = 1; i <= n; i++) {
					if (i > 1)
						sb.append(',');
					Object v = rs.getObject(i);
					sb.append(v == null ? "" : esc(v.toString()));
				}
				sb.append('\n');
				zos.write(sb.toString().getBytes("UTF-8"));
			}
		} catch (Exception e) {
			log.debug("writeCSV Method : {}, Error : {}", entry, e.getMessage());
			log.debug("SQL --> {}", sql);
		}
		zos.closeEntry();
	}

	private void truncate(Connection con) throws SQLException {
		String[] tables = { "cash_books", "categories", "column_definitions", "sub_categories", "transaction_audit_log",
				"transaction_custom_values", "transaction_receipts", "transactions" };

		try (Statement st = con.createStatement()) {
			for (String table : tables) {
				st.execute("TRUNCATE TABLE " + table + " RESTART IDENTITY CASCADE");
				log.debug("truncate Method. {}", table);
			}
		} catch (Exception e) {
			log.debug("truncate method : {}" + e.getMessage());
		}
	}

	private void resetSeq(Connection con) throws SQLException {
		String[] tables = { "cash_books", "categories", "column_definitions", "sub_categories", "transaction_audit_log",
				"transaction_custom_values", "transaction_receipts", "transactions" };
		try (Statement st = con.createStatement()) {
			for (String table : tables) {
				if (table.startsWith("sub_categories")) {
					st.execute("SELECT setval(pg_get_serial_sequence('" + table
							+ "','sub_categories_id'),COALESCE((SELECT MAX(sub_categories_id)+1 FROM " + table
							+ "),1),false)");
				} else {
					st.execute("SELECT setval(pg_get_serial_sequence('" + table
							+ "','id'),COALESCE((SELECT MAX(id)+1 FROM " + table + "),1),false)");
				}
				log.debug("resetSeq method --> {} ", table);
			}
		} catch (Exception e) {
			log.debug("resetSeq method --> {} ", e.getMessage());
		}
	}

	@FunctionalInterface
	interface RM {
		Object[] map(String[] r);
	}

	private void restoreCSV(Connection con, ZipFile zip, String entry, String sql, RM mapper) throws Exception {
		ZipEntry ze = zip.getEntry(entry);
		if (ze == null)
			return;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(zip.getInputStream(ze), "UTF-8"));
				PreparedStatement ps = con.prepareStatement(sql)) {
			String line;
			boolean first = true;
			while ((line = br.readLine()) != null) {
				if (first) {
					first = false;
					continue;
				}
				if (line.trim().isEmpty())
					continue;
				Object[] vals = mapper.map(parseCSV(line));
				for (int i = 0; i < vals.length; i++) {
					ps.setObject(i + 1, vals[i]);
				}
//				log.debug("SQL--> {}",vals);
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (Exception e) {
			log.debug("restoreCSV method --> {} : {}", entry, e.getMessage());
			try {
				con.rollback();
			} catch (Exception re) {
				log.debug("restoreCSV rollback error: {}", re.getMessage());
			}
			throw e;
		}
	}

	private void validateZip(Path p) throws Exception {
		try (ZipFile z = new ZipFile(p.toFile())) {
			if (z.getEntry("backup_info.txt") == null)
				throw new Exception("Invalid backup ZIP.");
		} catch (Exception e) {
			log.debug("validateZip method --> {} ", e.getMessage());
		}
	}

	private String esc(String s) {
		if (s == null)
			return "";
		if (s.contains(",") || s.contains("\"") || s.contains("\n"))
			return "\"" + s.replace("\"", "\"\"") + '"';
		return s;
	}

	private String[] parseCSV(String line) {
		java.util.List<String> t = new java.util.ArrayList<>();
		StringBuilder cur = new StringBuilder();
		boolean q = false;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == '"') {
				if (q && i + 1 < line.length() && line.charAt(i + 1) == '"') {
					cur.append('"');
					i++;
				} else
					q = !q;
			} else if (c == ',' && !q) {
				t.add(cur.toString());
				cur.setLength(0);
			} else
				cur.append(c);
		}
		t.add(cur.toString());
		return t.toArray(new String[0]);
	}

	private Integer iOf(String s) {
		try {
			return (s == null || s.isEmpty()) ? null : Integer.parseInt(s.trim());
		} catch (Exception e) {
			log.debug("iOf method --> {} ", e.getMessage());
			return null;
		}
	}

	private BigDecimal bdOf(String s) {
		try {
			return (s == null || s.isEmpty()) ? BigDecimal.ZERO : new BigDecimal(s.trim());
		} catch (Exception e) {
			log.debug("bdOf method --> {} ", e.getMessage());
			return BigDecimal.ZERO;
		}
	}

	private Timestamp tsOf(String s) {
		try {
			return (s == null || s.isEmpty()) ? null : Timestamp.valueOf(s.trim().replace("T", " "));
		} catch (Exception e) {
			return null;
		}
	}

	private Boolean boolOf(String s) {
		return (s == null || s.isEmpty()) ? false : Boolean.parseBoolean(s.trim());
	}
}
