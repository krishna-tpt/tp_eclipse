package com.ExcelToOracleInserter;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet; // Ensure this import (ss.usermodel, NOT sl.usermodel)
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelToOracleInserter {

	private static final Logger logger = LoggerFactory.getLogger(ExcelToOracleInserter.class);

	private static String JDBC_URL;
	private static String USERNAME;
	private static String PASSWORD;
	private static String excelFilePath;
	private static String sheetName;

	static Connection conn = null;
	static Properties prop = new Properties();

	static {
		try (FileInputStream fs = new FileInputStream("DB_config.Properties")) {
			prop.load(fs);
			logger.debug("Loaded DB configuration from DB_config.Properties");
		} catch (IOException e) {
			logger.error("Failed to load DB_config.Properties: {}", e.getMessage(), e);
			System.out.println("File not found to load: Missing DB_config.Properties");
		}
		JDBC_URL = prop.getProperty("db.url");
		USERNAME = prop.getProperty("db.username");
		PASSWORD = prop.getProperty("db.password");
		excelFilePath = prop.getProperty("excelFilePath");
		sheetName = prop.getProperty("sheetName");
		logger.debug("Attempting to connect to database: url={}", JDBC_URL);
		try {
			conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			logger.info("Database connection established successfully");
		} catch (SQLException e) {
			logger.error("Failed to establish database connection: {}", e.getMessage(), e);
			System.out.println("Database connection not established");
			e.printStackTrace();
		}
	}

	public Properties getProperties() {
		return prop;
	}

	// Table structure based on sheet columns (32 columns, VARCHAR2(4000))
	private static final String TABLE_NAME = "Temp_service_newpid_PL";
	private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
			+ "ID Number generated always as identity primary key,"
			+ "group_description VARCHAR2(4000)," + "pid_type VARCHAR2(4000)," + "sms_product_id VARCHAR2(4000),"
			+ "long_description VARCHAR2(4000)," + "polish VARCHAR2(4000)," + "beschrijving VARCHAR2(4000),"
			+ "unit_of_measure VARCHAR2(4000)," + "general_service_price_list VARCHAR2(4000),"
			+ "toyota_service_price_list VARCHAR2(4000)," + "jungheinrich_service_list VARCHAR2(4000),"
			+ "still_service_price_list VARCHAR2(4000)," + "linde_service_price_list VARCHAR2(4000),"
			+ "zeppelin_service_price_list VARCHAR2(4000)," + "wdx_service_price_list VARCHAR2(4000),"
			+ "comments VARCHAR2(4000)," + "pl_actuals_today VARCHAR2(4000)," + "col_17 VARCHAR2(4000),"
			+ "col_18 VARCHAR2(4000)," + "matching_md_pid VARCHAR2(4000),"
			+ "toyota_service_price_list_1 VARCHAR2(4000)," + "jungheinrich_service_list_1 VARCHAR2(4000),"
			+ "still_service_price_list_1 VARCHAR2(4000)," + "linde_service_price_list_1 VARCHAR2(4000),"
			+ "zeppelin_service_price_list_1 VARCHAR2(4000)," + "wdx_service_price_list_1 VARCHAR2(4000),"
			+ "col_26 VARCHAR2(4000)," + "col_27 VARCHAR2(4000)," + "col_28 VARCHAR2(4000)," + "col_29 VARCHAR2(4000),"
			+ "col_30 VARCHAR2(4000)," + "col_31 VARCHAR2(4000)," + "col_32 VARCHAR2(4000)" + ")";

	private static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME
			+ " (group_description, pid_type, sms_product_id, long_description, polish, beschrijving, "
			+ "unit_of_measure, general_service_price_list, toyota_service_price_list, jungheinrich_service_list, "
			+ "still_service_price_list, linde_service_price_list, zeppelin_service_price_list, wdx_service_price_list, "
			+ "comments, pl_actuals_today, col_17, col_18, matching_md_pid, toyota_service_price_list_1, "
			+ "jungheinrich_service_list_1, still_service_price_list_1, linde_service_price_list_1, "
			+ "zeppelin_service_price_list_1, wdx_service_price_list_1, col_26, col_27, col_28, col_29, col_30, col_31, col_32) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	public static void main(String[] args) {
//        String excelFilePath = "WO types and SA types for PL.xlsx"; // Update path if needed
//        String sheetName = "Global Service PID reviewed";

		List<List<String>> dataRows = null;
		try {
			logger.info("Starting Excel read from file: {}", excelFilePath);
			// Step 1: Read Excel data
			dataRows = readExcel(excelFilePath, sheetName);
			logger.info("Read {} rows from Excel sheet: {}", dataRows.size(), sheetName);

			// Step 2: Connect to Oracle and insert
			Connection conn = null;
			try {
				logger.debug("Attempting connection to Oracle DB: {}", JDBC_URL);
				conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
				logger.info("Successfully connected to Oracle DB.");

				// Disable auto-commit to allow explicit commit/rollback
                conn.setAutoCommit(false);
                logger.debug("Auto-commit disabled for explicit transaction control.");
                
				dropAndCreateTable(conn);
				logger.info("Table {} dropped (if existed) and recreated successfully.", TABLE_NAME);

				insertData(conn, dataRows);
				logger.info("Batch insert completed for {} rows.", dataRows.size());

				conn.commit();
				logger.info("Transaction committed. Data inserted successfully into {}.", TABLE_NAME);
			} catch (SQLException e) {
				logger.error("Database error during processing: {}", e.getMessage(), e);
				if (conn != null) {
					try {
						conn.rollback();
						logger.warn("Transaction rolled back due to error.");
					} catch (SQLException rollbackEx) {
						logger.error("Rollback failed: {}", rollbackEx.getMessage(), rollbackEx);
					}
				}
				throw e;
			} finally {
				if (conn != null) {
					// Re-enable auto-commit before closing (optional, but good practice)
                    try {
                        conn.setAutoCommit(true);
                        logger.debug("Auto-commit re-enabled.");
                    } catch (SQLException e) {
                        logger.warn("Failed to re-enable auto-commit: {}", e.getMessage());
                    }
					conn.close();
					logger.debug("DB connection closed.");
				}
			}

		} catch (IOException e) {
			logger.error("Error reading Excel file '{}': {}", excelFilePath, e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Unexpected error: {}", e.getMessage(), e);
		} finally {
			logger.info("Program execution completed.");
		}
	}

	/**
	 * Reads Excel sheet starting from row 9 (0-indexed 8), collects data as List of
	 * Lists.
	 */
	private static List<List<String>> readExcel(String filePath, String sheetName) throws IOException {
        logger.debug("Reading Excel file: {}", filePath);
        List<List<String>> rows = new ArrayList<List<String>>();
        FileInputStream fis = null;
        Workbook workbook = null;
        FormulaEvaluator evaluator = null;
        try {
            fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.error("Sheet '{}' not found in Excel file.", sheetName);
                throw new IOException("Sheet '" + sheetName + "' not found.");
            }
            logger.debug("Found sheet: {}, last row index: {}", sheetName, sheet.getLastRowNum());

            // Start from row 9 (index 8), skip empty rows
            int processedRows = 0;
            for (int i = 8; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    logger.trace("Skipping null row at index: {}", i);
                    continue;
                }

                List<String> rowData = new ArrayList<String>();
                for (int j = 0; j < 32; j++) { // 32 columns
                    Cell cell = row.getCell(j);
                    String cellValue = (cell != null) ? getCellValueAsString(cell, evaluator) : "";
                    rowData.add(cellValue);
                }
                // Skip fully empty rows
                boolean allEmpty = true;
                for (String val : rowData) {
                    if (!val.isEmpty()) {
                        allEmpty = false;
                        break;
                    }
                }
                if (!allEmpty) {
                    rows.add(rowData);
                    processedRows++;
                    if (processedRows % 1000 == 0) {
                        logger.debug("Processed {} non-empty rows so far.", processedRows);
                    }
                }
            }
            logger.debug("Total non-empty rows extracted: {}", rows.size());
        } finally {
            if (evaluator != null) {
                evaluator.clearAllCachedResultValues();
            }
            if (workbook != null) {
                workbook.close();
                logger.trace("Workbook closed.");
            }
            if (fis != null) {
                fis.close();
                logger.trace("FileInputStream closed.");
            }
        }
        return rows;
    }

    /**
     * Converts cell value to string, handling formulas/numbers with evaluation.
     */
    private static String getCellValueAsString(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return "";
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    return (numericValue == Math.floor(numericValue)) ? String.valueOf((long) numericValue) : String.valueOf(numericValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                logger.trace("Evaluating formula cell: {}", cell.getCellFormula());
                try {
                    CellValue cellValue = evaluator.evaluate(cell);
                    switch (cellValue.getCellType()) {
                        case STRING:
                            return cellValue.getStringValue().trim();
                        case NUMERIC:
                            double numValue = cellValue.getNumberValue();
                            return (numValue == Math.floor(numValue)) ? String.valueOf((long) numValue) : String.valueOf(numValue);
                        case BOOLEAN:
                            return String.valueOf(cellValue.getBooleanValue());
                        case BLANK:
                            return "";
                        case ERROR:
                            logger.warn("Formula error in cell: {}", cellValue.getErrorValue());
                            return "#ERROR";
                        default:
                            return "";
                    }
                } catch (Exception e) {
                    logger.warn("Failed to evaluate formula '{}': {}", cell.getCellFormula(), e.getMessage());
                    return cell.getCellFormula(); // Fallback to formula string
                }
            default:
                return "";
        }
    }

    /**
     * Drops table if exists, then creates the permanent temp table.
     */
    private static void dropAndCreateTable(Connection conn) throws SQLException {
        logger.debug("Dropping table if exists: {}", TABLE_NAME);
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("DROP TABLE " + TABLE_NAME + " CASCADE CONSTRAINTS");
            logger.debug("Table {} dropped successfully.", TABLE_NAME);
        } catch (SQLException e) {
            // Ignore if table doesn't exist
            if (!e.getMessage().contains("ORA-00942")) {
                logger.warn("Unexpected error dropping table: {}", e.getMessage());
                throw e;
            }
            logger.debug("Table {} did not exist, skipping drop.", TABLE_NAME);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }

        logger.debug("Creating table: {}", TABLE_NAME);
        stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(CREATE_TABLE_SQL);
            logger.info("Table {} created successfully.", TABLE_NAME);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /**
     * Inserts rows using PreparedStatement (batch for efficiency).
     */
    private static void insertData(Connection conn, List<List<String>> rows) throws SQLException {
        if (rows.isEmpty()) {
            logger.warn("No rows to insert.");
            return;
        }
        
        logger.debug("Preparing batch insert for {} rows.", rows.size());
        PreparedStatement pstmt = null;
        int batchSize = 1000; // Batch every 1000 rows for efficiency
        int totalInserted = 0;
        try {
            pstmt = conn.prepareStatement(INSERT_SQL);
            for (int i = 0; i < rows.size(); i++) {
                List<String> row = rows.get(i);
                for (int j = 0; j < 32; j++) {
                    pstmt.setString(j + 1, row.get(j));
                }
                pstmt.addBatch();

                if ((i + 1) % batchSize == 0 || (i + 1) == rows.size()) {
                    int[] results = pstmt.executeBatch();
                    totalInserted += results.length;
                    logger.debug("Inserted batch of {} rows (total so far: {}).", results.length, totalInserted);
                }
            }
            logger.info("All batches executed successfully. Total inserted: {}.", totalInserted);
        } finally {
            if (pstmt != null) {
                pstmt.close();
                logger.trace("PreparedStatement closed.");
            }
        }
    }
}