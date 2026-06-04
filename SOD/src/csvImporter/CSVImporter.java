package csvImporter;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * CSV Importer: Reads a CSV file and imports it into PostgreSQL table
 * Z_ad_changelog Tracks: file size, line count, and elapsed import time.
 *
 * Dependencies (add to classpath): - PostgreSQL JDBC Driver:
 * https://jdbc.postgresql.org/download/ e.g., postgresql-42.x.x.jar
 *
 * Compile: javac -cp .:postgresql-42.x.x.jar CsvImporter.java
 *
 * Run: java -cp .:postgresql-42.x.x.jar CsvImporter /path/to/your/file.csv
 */
public class CSVImporter {

	// ─── DB CONFIG — update these values ─────────────────────────────────────
	private static final String DB_URL = "jdbc:postgresql://localhost:5432/iDempiereV13";
	private static final String DB_USER = "adempiere";
	private static final String DB_PASSWORD = "idempiere";
	private static final String FILE = "/home/dev021/Downloads/Changlog (2)/Changlog/changelog_dump_02042025.csv";
	// ─────────────────────────────────────────────────────────────────────────

	private static final String TABLE_NAME = "adempiere.z_ad_changelog";
	private static final int BATCH_SIZE = 500; // rows per JDBC batch

	private static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME + " ("
			+ "AD_CHANGELOG_ID, AD_CLIENT_ID, AD_COLUMN_ID, AD_ORG_ID, AD_ROLE_ID, "
			+ "AD_SESSION_ID, AD_TABLE_ID, CHANGELOGTYPE, CREATEDON, CREATEDBY, "
			+ "DESCRIPTION, ISACTIVE, ISCUSTOMIZATION, NEWVALUE, OLDVALUE, "
			+ "RECORD2_ID, RECORD_ID, REDO, TRXNAME, UNDO, UPDATEDON, UPDATEDBY"
			+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	public static void main(String[] args) throws Exception {

		Connection connn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		if (connn != null) {
			System.out.println("DB Connected...");
		}

		// ── 1. Resolve file path ──────────────────────────────────────────────
		String filePath = FILE;
		File csvFile = new File(filePath);

		if (!csvFile.exists() || !csvFile.isFile()) {
			System.err.println("ERROR: File not found → " + filePath);
			System.exit(1);
		}

		// ── 2. File metadata ──────────────────────────────────────────────────
		long fileSizeBytes = csvFile.length();
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.println("  CSV → PostgreSQL Importer");
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.printf("  File      : %s%n", csvFile.getAbsolutePath());
		System.out.printf("  File Size : %s%n", humanReadableSize(fileSizeBytes));

		// Count lines (including header) before import
		long totalLines = countLines(filePath);
		long dataLines = totalLines - 1; // exclude header
		System.out.printf("  Total Lines (incl. header) : %,d%n", totalLines);
		System.out.printf("  Data Rows  (excl. header)  : %,d%n", dataLines);
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.printf("  Import started at  : %s%n",
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));

		// ── 3. Import ─────────────────────────────────────────────────────────
		long startNano = System.nanoTime();

		long imported = importCsv(filePath);

		long endNano = System.nanoTime();
		long startMs = startNano / 1_000_000; // for display only

		// ── 4. Summary ────────────────────────────────────────────────────────
		double elapsedSec = (endNano - startNano) / 1_000_000_000.0;
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.printf("  Import ended at    : %s%n",
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
		System.out.printf("  Rows imported      : %,d%n", imported);
		System.out.printf("  Time taken         : %.3f seconds%n", elapsedSec);
		if (elapsedSec > 0) {
			System.out.printf("  Throughput         : %.1f rows/sec%n", imported / elapsedSec);
		}
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
	}

	// ── Core import logic ─────────────────────────────────────────────────────
	private static long importCsv(String filePath) throws Exception {
		long rowsInserted = 0;

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				BufferedReader br = new BufferedReader(new FileReader(filePath));
				PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

//			conn.setAutoCommit(false); // manual commit for batching

			String headerLine = br.readLine(); // skip header
			if (headerLine == null) {
				System.out.println("  WARNING: File is empty (no header found).");
				return 0;
			}

			String line;
			int batchCount = 0;

			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty())
					continue;

				String[] cols = parseCsvLine(line);

				if (cols.length < 22) {
					System.err.printf("  SKIP (too few columns): %s%n", line);
					continue;
				}

				// Map columns positionally (matches CSV header order)
				ps.setObject(1, parseDecimal(cols[0])); // AD_CHANGELOG_ID
				ps.setObject(2, parseDecimal(cols[1])); // AD_CLIENT_ID
				ps.setObject(3, parseDecimal(cols[2])); // AD_COLUMN_ID
				ps.setObject(4, parseDecimal(cols[3])); // AD_ORG_ID
				ps.setObject(5, parseDecimal(cols[4])); // AD_ROLE_ID
				ps.setObject(6, parseDecimal(cols[5])); // AD_SESSION_ID
				ps.setObject(7, parseDecimal(cols[6])); // AD_TABLE_ID
				ps.setString(8, nullIfEmpty(cols[7])); // CHANGELOGTYPE
				ps.setString(9, nullIfEmpty(cols[8])); // CREATEDON
				ps.setObject(10, parseDecimal(cols[9])); // CREATEDBY
				ps.setString(11, nullIfEmpty(cols[10])); // DESCRIPTION
				ps.setString(12, nullIfEmpty(cols[11])); // ISACTIVE
				ps.setString(13, nullIfEmpty(cols[12])); // ISCUSTOMIZATION
				ps.setString(14, nullIfEmpty(cols[13])); // NEWVALUE
				ps.setString(15, nullIfEmpty(cols[14])); // OLDVALUE
				ps.setObject(16, parseDecimal(cols[15])); // RECORD2_ID
				ps.setObject(17, parseDecimal(cols[16])); // RECORD_ID
				ps.setString(18, nullIfEmpty(cols[17])); // REDO
				ps.setString(19, nullIfEmpty(cols[18])); // TRXNAME
				ps.setString(20, nullIfEmpty(cols[19])); // UNDO
				ps.setString(21, nullIfEmpty(cols[20])); // UPDATEDON
				ps.setObject(22, parseDecimal(cols[21])); // UPDATEDBY

				ps.addBatch();
				batchCount++;
				rowsInserted++;

				// Execute and commit every BATCH_SIZE rows
				if (batchCount >= BATCH_SIZE) {
					ps.executeBatch();
//					conn.commit();
					System.out.printf("  ... %,d rows committed%n", rowsInserted);
					batchCount = 0;
				}
			}

			// Flush remaining rows
			if (batchCount > 0) {
				ps.executeBatch();
//				conn.commit();
			}
		}

		return rowsInserted;
	}

	// ── Helpers ───────────────────────────────────────────────────────────────

	/** Count all lines in a file (including header). */
	private static long countLines(String filePath) throws IOException {
		long count = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			while (br.readLine() != null)
				count++;
		}
		return count;
	}

	/**
	 * Minimal CSV line parser: handles comma-delimited fields. Does NOT handle
	 * quoted fields containing commas. Upgrade to OpenCSV if your data has quoted
	 * commas.
	 */
	private static String[] parseCsvLine(String line) {
		return line.split(",", -1);
	}

	/** Return null for empty/blank strings; otherwise the trimmed value. */
	private static String nullIfEmpty(String s) {
		if (s == null)
			return null;
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}

	/**
	 * Parse a string to Double; returns null for empty/blank. Using Double so
	 * NUMERIC(precision-unlimited) types map cleanly.
	 */
	private static Double parseDecimal(String s) {
		String t = nullIfEmpty(s);
		if (t == null)
			return null;
		try {
			return Double.parseDouble(t);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** Format bytes to human-readable string (KB / MB / GB). */
	private static String humanReadableSize(long bytes) {
		DecimalFormat df = new DecimalFormat("#,##0.##");
		if (bytes < 1024)
			return bytes + " B";
		if (bytes < 1024 * 1024)
			return df.format(bytes / 1024.0) + " KB";
		if (bytes < 1024 * 1024 * 1024)
			return df.format(bytes / (1024.0 * 1024)) + " MB";
		return df.format(bytes / (1024.0 * 1024 * 1024)) + " GB";
	}
}