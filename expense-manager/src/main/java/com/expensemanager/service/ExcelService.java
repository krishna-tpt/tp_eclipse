package com.expensemanager.service;

import com.expensemanager.model.AppConfig;
import com.expensemanager.model.ExpenseBook;
import com.expensemanager.model.Transaction;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Reads and writes the Expense_Manager.xlsx workbook.
 *
 * Sheet layout:
 *   Sheet 0 → Expense_Book  (monthly ledger index)
 *   Sheet 1 → Config        (categories, payment modes)
 *   Sheet 2 → Data          (all transactions)
 */
public class ExcelService {

    private static final Logger log = LoggerFactory.getLogger(ExcelService.class);

    // Sheet names
    public static final String SHEET_BOOK   = "Expense_Book";
    public static final String SHEET_CONFIG = "Config";
    public static final String SHEET_DATA   = "Data";

    // Data sheet standard columns
    private static final String COL_TYPE     = "Type";
    private static final String COL_CREATED  = "Created";
    private static final String COL_AMOUNT   = "Amount";
    private static final String COL_CATEGORY = "Category";
    private static final String COL_PAYMENT  = "Payment";
    private static final String COL_NOTE     = "Note";

    // ──────────────────────────────────────────────────────────
    // READ
    // ──────────────────────────────────────────────────────────

    public AppConfig readConfig(byte[] workbookBytes) throws IOException {
        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(workbookBytes))) {
            Sheet sheet = wb.getSheet(SHEET_CONFIG);
            AppConfig config = new AppConfig();
            if (sheet == null) return config;

            // Header row
            Row header = sheet.getRow(0);
            if (header == null) return config;

            Map<String, Integer> colIndex = buildColumnIndex(header);

            List<String> iCat = new ArrayList<>();
            List<String> eCat = new ArrayList<>();
            List<String> ePay = new ArrayList<>();
            List<String> iPay = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                addIfNotBlank(iCat, getCellString(row, colIndex.get("I_Category")));
                addIfNotBlank(eCat, getCellString(row, colIndex.get("E_Cataegory")));
                addIfNotBlank(ePay, getCellString(row, colIndex.get("E_Payment")));
                addIfNotBlank(iPay, getCellString(row, colIndex.get("I_Payment")));
            }

            config.setIncomeCategories(iCat);
            config.setExpenseCategories(eCat);
            config.setIncomePaymentModes(iPay);
            config.setExpensePaymentModes(ePay);
            return config;
        }
    }

    public List<ExpenseBook> readExpenseBook(byte[] workbookBytes) throws IOException {
        List<ExpenseBook> books = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(workbookBytes))) {
            Sheet sheet = wb.getSheet(SHEET_BOOK);
            if (sheet == null) return books;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell snoCell  = row.getCell(0);
                Cell nameCell = row.getCell(1);
                Cell dateCell = row.getCell(2);
                if (snoCell == null || nameCell == null) continue;
                int sno = (int) snoCell.getNumericCellValue();
                String name = nameCell.getStringCellValue();
                LocalDate created = null;
                if (dateCell != null && dateCell.getCellType() == CellType.NUMERIC) {
                    Date d = DateUtil.getJavaDate(dateCell.getNumericCellValue());
                    created = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                }
                books.add(new ExpenseBook(sno, name, created));
            }
        }
        return books;
    }

    public List<Transaction> readTransactions(byte[] workbookBytes) throws IOException {
        List<Transaction> txns = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(workbookBytes))) {
            Sheet sheet = wb.getSheet(SHEET_DATA);
            if (sheet == null) return txns;

            Row header = sheet.getRow(0);
            if (header == null) return txns;
            Map<String, Integer> colIndex = buildColumnIndex(header);

            // Standard column indices
            Integer typeIdx     = colIndex.get(COL_TYPE);
            Integer createdIdx  = colIndex.get(COL_CREATED);
            Integer amountIdx   = colIndex.get(COL_AMOUNT);
            Integer categoryIdx = colIndex.get(COL_CATEGORY);
            Integer paymentIdx  = colIndex.get(COL_PAYMENT);
            Integer noteIdx     = colIndex.get(COL_NOTE);

            // Extra columns (beyond standard 6)
            Set<String> standardCols = Set.of(COL_TYPE, COL_CREATED, COL_AMOUNT, COL_CATEGORY, COL_PAYMENT, COL_NOTE);
            Map<String, Integer> extraColIndex = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> e : colIndex.entrySet()) {
                if (!standardCols.contains(e.getKey())) {
                    extraColIndex.put(e.getKey(), e.getValue());
                }
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String typeStr = getCellString(row, typeIdx);
                if (typeStr == null || typeStr.isBlank()) continue;

                Transaction t = new Transaction();
                t.setType("Income".equalsIgnoreCase(typeStr) ? Transaction.Type.INCOME : Transaction.Type.EXPENSE);

                if (createdIdx != null) {
                    Cell cell = row.getCell(createdIdx);
                    if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                        Date d = DateUtil.getJavaDate(cell.getNumericCellValue());
                        t.setDateTime(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    }
                }
                if (amountIdx != null) {
                    Cell c = row.getCell(amountIdx);
                    if (c != null && c.getCellType() == CellType.NUMERIC) t.setAmount(c.getNumericCellValue());
                }
                t.setCategory(getCellString(row, categoryIdx));
                t.setPayment(getCellString(row, paymentIdx));
                t.setNote(getCellString(row, noteIdx));

                for (Map.Entry<String, Integer> extra : extraColIndex.entrySet()) {
                    t.addExtraField(extra.getKey(), getCellString(row, extra.getValue()));
                }
                txns.add(t);
            }
        }
        return txns;
    }

    // ──────────────────────────────────────────────────────────
    // WRITE
    // ──────────────────────────────────────────────────────────

    /**
     * Appends a new transaction row to the Data sheet.
     */
    public byte[] addTransaction(byte[] workbookBytes, Transaction txn) throws IOException {
        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(workbookBytes))) {
            Sheet sheet = wb.getSheet(SHEET_DATA);
            if (sheet == null) sheet = wb.createSheet(SHEET_DATA);

            Row header = sheet.getRow(0);
            Map<String, Integer> colIndex = buildColumnIndex(header);

            int nextRow = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(nextRow);

            setCell(row, colIndex.getOrDefault(COL_TYPE, 0),
                    txn.getType() == Transaction.Type.INCOME ? "Income" : "Expense");

            if (txn.getDateTime() != null) {
                Cell c = row.createCell(colIndex.getOrDefault(COL_CREATED, 1));
                c.setCellValue(txn.getDateTime().atZone(ZoneId.systemDefault()).toInstant()
                        .toEpochMilli() / 86400000.0 + 25569);
                CellStyle dateStyle = wb.createCellStyle();
                CreationHelper ch = wb.getCreationHelper();
                dateStyle.setDataFormat(ch.createDataFormat().getFormat("dd-mm-yyyy hh:mm"));
                c.setCellStyle(dateStyle);
            }

            setNumericCell(row, colIndex.getOrDefault(COL_AMOUNT, 2), txn.getAmount());
            setCell(row, colIndex.getOrDefault(COL_CATEGORY, 3), txn.getCategory());
            setCell(row, colIndex.getOrDefault(COL_PAYMENT, 4), txn.getPayment());
            setCell(row, colIndex.getOrDefault(COL_NOTE, 5), txn.getNote());

            // Write extra fields
            for (Map.Entry<String, String> extra : txn.getExtraFields().entrySet()) {
                Integer idx = colIndex.get(extra.getKey());
                if (idx != null) setCell(row, idx, extra.getValue());
            }

            return toBytes(wb);
        }
    }

    /**
     * Adds a new category to the Config sheet.
     * type: "I" = income, "E" = expense, "BOTH" = both
     */
    public byte[] addCategory(byte[] workbookBytes, String category, String type) throws IOException {
        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(workbookBytes))) {
            Sheet sheet = wb.getSheet(SHEET_CONFIG);
            if (sheet == null) sheet = wb.createSheet(SHEET_CONFIG);

            int nextRow = sheet.getLastRowNum() + 1;
            Row header = sheet.getRow(0);
            Map<String, Integer> colIndex = buildColumnIndex(header);

            Row row = sheet.createRow(nextRow);
            if ("I".equalsIgnoreCase(type) || "BOTH".equalsIgnoreCase(type)) {
                setCell(row, colIndex.getOrDefault("I_Category", 0), category);
            }
            if ("E".equalsIgnoreCase(type) || "BOTH".equalsIgnoreCase(type)) {
                setCell(row, colIndex.getOrDefault("E_Cataegory", 1), category);
            }
            return toBytes(wb);
        }
    }

    /**
     * Adds a new custom column header to the Data sheet.
     */
    public byte[] addCustomColumn(byte[] workbookBytes, String columnName) throws IOException {
        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(workbookBytes))) {
            Sheet sheet = wb.getSheet(SHEET_DATA);
            if (sheet == null) sheet = wb.createSheet(SHEET_DATA);

            Row header = sheet.getRow(0);
            if (header == null) header = sheet.createRow(0);

            // Find next empty header cell
            int lastCol = header.getLastCellNum();
            if (lastCol < 0) lastCol = 0;
            header.createCell(lastCol).setCellValue(columnName);

            return toBytes(wb);
        }
    }

    // ──────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────

    private Map<String, Integer> buildColumnIndex(Row header) {
        Map<String, Integer> map = new LinkedHashMap<>();
        if (header == null) return map;
        for (Cell cell : header) {
            if (cell != null && cell.getCellType() == CellType.STRING) {
                map.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
            }
        }
        return map;
    }

    private String getCellString(Row row, Integer colIdx) {
        if (colIdx == null || row == null) return null;
        Cell cell = row.getCell(colIdx);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default      -> null;
        };
    }

    private void setCell(Row row, int idx, String value) {
        if (value != null) row.createCell(idx).setCellValue(value);
    }

    private void setNumericCell(Row row, int idx, double value) {
        row.createCell(idx).setCellValue(value);
    }

    private void addIfNotBlank(List<String> list, String value) {
        if (value != null && !value.isBlank()) list.add(value);
    }

    private byte[] toBytes(Workbook wb) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        wb.write(bos);
        return bos.toByteArray();
    }
}
