package com.test.sod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CsvToInsertGenerator {

	// ---- Modify these paths as needed ----
	private static final String INPUT_CSV_FILE = "/home/dev021/Downloads/changlog/changelog_backup.csv";

	private static final String OUTPUT_SQL_FILE = "/home/dev021/Downloads/changlog/output files/changelog_insert.sql";
	// ----------------------------------------

	// Actual DB column names to use in the generated INSERT statement (in CSV
	// column order)
	private static final String[] COLUMNS = { "AD_CHANGELOG_ID", "AD_CLIENT_ID", "AD_COLUMN_ID", "AD_ORG_ID",
			"AD_ROLE_ID", "AD_SESSION_ID", "AD_TABLE_ID", "CHANGELOGTYPE", "CREATED", "CREATEDBY", "DESCRIPTION",
			"ISACTIVE", "ISCUSTOMIZATION", "NEWVALUE", "OLDVALUE", "RECORD2_ID", "RECORD_ID", "REDO", "TRXNAME", "UNDO",
			"UPDATED", "UPDATEDBY" };

	// Columns that are plain numbers (no quotes, empty -> null)
	private static final java.util.Set<String> NUMERIC_COLUMNS = new java.util.HashSet<>(
			java.util.Arrays.asList("AD_CHANGELOG_ID", "AD_CLIENT_ID", "AD_COLUMN_ID", "AD_ORG_ID", "AD_ROLE_ID",
					"AD_SESSION_ID", "AD_TABLE_ID", "CREATEDBY", "RECORD2_ID", "RECORD_ID", "UPDATEDBY"));

	// Columns that are dates (need to_date(...) conversion)
	private static final java.util.Set<String> DATE_COLUMNS = new java.util.HashSet<>(
			java.util.Arrays.asList("CREATED", "UPDATED"));

	// First line of the CSV is a header row (AD_CHANGELOG_ID,AD_CLIENT_ID,...) -
	// skip it, don't parse as data
	private static final boolean SKIP_HEADER_ROW = true;

	// CSV source date format e.g. "09-03-2026 04:59"
	private static final SimpleDateFormat CSV_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);

	// Target Oracle to_date format e.g. to_date('13-05-2026 : 09-51-23
	// AM','dd-MM-yyyy : HH12-MI-SS AM')
	private static final SimpleDateFormat ORACLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy : hh-mm-ss a",
			Locale.ENGLISH);

	private static final String ORACLE_TO_DATE_MASK = "dd-MM-yyyy : HH12-MI-SS AM";

	private static final String TABLE_NAME = "AD_CHANGELOG";

	public static void main(String[] args) {

		File inputFile = new File(INPUT_CSV_FILE);
		File outputFile = new File(OUTPUT_SQL_FILE);

		File outputFolder = outputFile.getParentFile();
		if (outputFolder != null && !outputFolder.exists()) {
			outputFolder.mkdirs();
		}

		int totalRows = 0;
		int successRows = 0;
		int errorRows = 0;

		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

			String line;
			boolean isFirstLine = true;
			while ((line = reader.readLine()) != null) {

				if (line.trim().isEmpty()) {
					continue;
				}

				if (isFirstLine) {
					isFirstLine = false;
					if (SKIP_HEADER_ROW) {
						continue;
					}
				}

				totalRows++;

				// -1 keeps trailing empty fields (important for trailing empty columns)
				String[] fields = line.split(",", -1);

				if (fields.length != COLUMNS.length) {
					System.err.println("Row " + totalRows + " skipped - expected " + COLUMNS.length
							+ " columns but found " + fields.length + " -> " + line);
					errorRows++;
					continue;
				}

				try {
					String insertStatement = buildInsertStatement(fields);
					writer.write(insertStatement);
					writer.newLine();
					successRows++;
				} catch (Exception e) {
					System.err.println("Row " + totalRows + " failed: " + e.getMessage() + " -> " + line);
					errorRows++;
				}
			}

			System.out.println("----------------------------------------");
			System.out.println("SUMMARY");
			System.out.println("Total rows read     : " + totalRows);
			System.out.println("Insert statements    : " + successRows);
			System.out.println("Failed/skipped rows : " + errorRows);
			System.out.println("Output SQL file     : " + outputFile.getPath());
			System.out.println("----------------------------------------");

		} catch (IOException e) {
			System.err.println("Error reading/writing file: " + e.getMessage());
		}
	}

	private static String buildInsertStatement(String[] fields) throws ParseException {

		StringBuilder columnsPart = new StringBuilder();
		StringBuilder valuesPart = new StringBuilder();

		for (int i = 0; i < COLUMNS.length; i++) {
			String column = COLUMNS[i];
			String rawValue = fields[i].trim();

			if (i > 0) {
				columnsPart.append(",");
				valuesPart.append(",");
			}
			columnsPart.append(column);

			valuesPart.append(formatValue(column, rawValue));
		}

		return "Insert into " + TABLE_NAME + " (" + columnsPart + ") values (" + valuesPart + ");";
	}

	private static String formatValue(String column, String rawValue) throws ParseException {

		if (rawValue.isEmpty()) {
			return "null";
		}

		if (NUMERIC_COLUMNS.contains(column)) {
			return rawValue;
		}

		if (DATE_COLUMNS.contains(column)) {
			Date parsedDate = CSV_DATE_FORMAT.parse(rawValue);
			String oracleFormattedDate = ORACLE_DATE_FORMAT.format(parsedDate).toUpperCase(Locale.ENGLISH);
			return "to_date('" + oracleFormattedDate + "','" + ORACLE_TO_DATE_MASK + "')";
		}

		// Default: treat as string -> quote it (escape single quotes for SQL safety)
		String escaped = rawValue.replace("'", "''");
		return "'" + escaped + "'";
	}
}