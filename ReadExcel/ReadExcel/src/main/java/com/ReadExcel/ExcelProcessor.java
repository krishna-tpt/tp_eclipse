package com.ReadExcel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelProcessor {
    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.err.println("Usage: java ExcelProcessor <path_to_excel_file.xlsx>");
//            System.exit(1);
//        }

        String filePath = "/home/dev021/Documents/Michelin/Tickets/Z_SOD_AUDIT/Files/SOD_Reports_30-10-2025/SOD_AUDIT_REPORT_Polska_10_30_2025.xlsx";
        Map<String, Set<String>> normalizedSourceToDocs = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(filePath);
                Workbook workbook = new XSSFWorkbook(fis)) {

               Sheet sheet = workbook.getSheetAt(0); // Assume first sheet
               for (Row row : sheet) {
                   if (row.getRowNum() == 0) continue; // Skip header row

                   Cell sourceCell = row.getCell(1); // SOURCE column (index 1)
                   Cell docCell = row.getCell(3); // DOCUMENTNO column (index 3)

                   if (sourceCell == null || docCell == null) continue;

                   String source = sourceCell.getStringCellValue().trim();
                   String documentNo = docCell.getStringCellValue().trim();

                   if (source.isEmpty() || documentNo.isEmpty()) continue;

                   // Normalize SOURCE: remove suffixes like " LINES UPDATE", " LINES CREATION", " UPDATE", " CREATION"
                   String normalizedSource = normalizeSource(source);

                   // Add to set for uniqueness
                   normalizedSourceToDocs.computeIfAbsent(normalizedSource, k -> new HashSet<>()).add(documentNo);
               }

               // Output the results
               System.out.println("Normalized SOURCE to Unique DOCUMENTNO Counts:");
               for (Map.Entry<String, Set<String>> entry : normalizedSourceToDocs.entrySet()) {
                   System.out.println(entry.getKey() + " : " + entry.getValue().size());
               }

           } catch (IOException e) {
               System.err.println("Error reading Excel file: " + e.getMessage());
               e.printStackTrace();
           }
       }

       private static String normalizeSource(String source) {
           source = source.trim();

           // Check for " LINES UPDATE"
           if (source.endsWith(" LINES UPDATE")) {
               return source.substring(0, source.length() - " LINES UPDATE".length()).trim();
           }
           // Check for " LINES CREATION"
           if (source.endsWith(" LINES CREATION")) {
               return source.substring(0, source.length() - " LINES CREATION".length()).trim();
           }
           // Check for " UPDATE"
           if (source.endsWith(" UPDATE")) {
               return source.substring(0, source.length() - " UPDATE".length()).trim();
           }
           // Check for " CREATION"
           if (source.endsWith(" CREATION")) {
               return source.substring(0, source.length() - " CREATION".length()).trim();
           }

           // No change if no matching suffix
           return source;
       }
}