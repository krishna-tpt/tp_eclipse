package com.expensemanager.service;

import com.expensemanager.model.CashBook;
import com.expensemanager.model.Transaction;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

public class ExportService {

	// ── PDF ──────────────────────────────────────────────────────────────
	public byte[] generatePDF(CashBook book, List<Transaction> txns, BigDecimal income, BigDecimal expense)
			throws Exception {

		Document doc = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PdfWriter.getInstance(doc, bos);
		doc.open();

		// Fonts
		com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
		com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
		com.itextpdf.text.Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
		com.itextpdf.text.Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);

		// Title
		Paragraph title = new Paragraph("ExpenseOS — " + book.getName(), titleFont);
		title.setAlignment(Element.ALIGN_CENTER);
		title.setSpacingAfter(4);
		doc.add(title);

		Paragraph sub = new Paragraph("Generated on " + java.time.LocalDate.now(), subFont);
		sub.setAlignment(Element.ALIGN_CENTER);
		sub.setSpacingAfter(12);
		doc.add(sub);

		// Summary row
		PdfPTable summary = new PdfPTable(3);
		summary.setWidthPercentage(60);
		summary.setHorizontalAlignment(Element.ALIGN_CENTER);
		summary.setSpacingAfter(16);

		addSummaryCell(summary, "Total Income", "₹" + income, new BaseColor(220, 252, 231));
		addSummaryCell(summary, "Total Expense", "₹" + expense, new BaseColor(254, 226, 226));
		addSummaryCell(summary, "Balance", "₹" + income.subtract(expense),
				income.compareTo(expense) >= 0 ? new BaseColor(219, 234, 254) : new BaseColor(254, 226, 226));
		doc.add(summary);

		// Transactions table
		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100);
		table.setWidths(new float[] { 2.5f, 1.5f, 2f, 2f, 1.8f, 3f });
		table.setSpacingBefore(8);

		BaseColor hdrBg = new BaseColor(37, 99, 235);
		String[] headers = { "Date & Time", "Type", "Category", "Sub Cat", "Amount", "Note" };
		for (String h : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
			cell.setBackgroundColor(hdrBg);
			cell.setPadding(6);
			cell.setBorderColor(new BaseColor(30, 80, 200));
			table.addCell(cell);
		}

		boolean alt = false;
		for (Transaction t : txns) {
			BaseColor rowBg = alt ? new BaseColor(248, 250, 252) : BaseColor.WHITE;
			alt = !alt;

			addCell(table, t.getFormattedDateTime(), cellFont, rowBg);

			// Type cell colored
			com.itextpdf.text.Font typeFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8,
					t.getType() == Transaction.Type.INCOME ? new BaseColor(21, 128, 61) : new BaseColor(185, 28, 28));
			PdfPCell typeCell = new PdfPCell(new Phrase(t.getType().name(), typeFont));
			typeCell.setBackgroundColor(t.getType() == Transaction.Type.INCOME ? new BaseColor(220, 252, 231)
					: new BaseColor(254, 226, 226));
			typeCell.setPadding(5);
			table.addCell(typeCell);

			addCell(table, nvl(t.getCategoryName()), cellFont, rowBg);
			addCell(table, nvl(t.getSubCategoryName()), cellFont, rowBg);

			// Amount colored
			com.itextpdf.text.Font amtFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9,
					t.getType() == Transaction.Type.INCOME ? new BaseColor(21, 128, 61) : new BaseColor(185, 28, 28));
			PdfPCell amtCell = new PdfPCell(
					new Phrase((t.getType() == Transaction.Type.INCOME ? "+" : "-") + "₹" + t.getAmount(), amtFont));
			amtCell.setBackgroundColor(rowBg);
			amtCell.setPadding(5);
			table.addCell(amtCell);

			addCell(table, nvl(t.getNote()), cellFont, rowBg);
		}

		if (txns.isEmpty()) {
			PdfPCell empty = new PdfPCell(new Phrase("No transactions found.", cellFont));
			empty.setColspan(6);
			empty.setPadding(10);
			empty.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(empty);
		}

		doc.add(table);
		doc.close();
		return bos.toByteArray();
	}

	// ── Excel ────────────────────────────────────────────────────────────
	public byte[] generateExcel(CashBook book, List<Transaction> txns, BigDecimal income, BigDecimal expense)
			throws Exception {

		try (XSSFWorkbook wb = new XSSFWorkbook()) {
			Sheet sheet = wb.createSheet(book.getName());

			// Styles
			CellStyle headerStyle = wb.createCellStyle();
			Font hFont = wb.createFont();
			hFont.setBold(true);
			hFont.setColor(IndexedColors.WHITE.getIndex());
			headerStyle.setFont(hFont);
			headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setBorderBottom(BorderStyle.THIN);

			CellStyle incomeStyle = wb.createCellStyle();
			Font iFont = wb.createFont();
			iFont.setColor(IndexedColors.DARK_GREEN.getIndex());
			iFont.setBold(true);
			incomeStyle.setFont(iFont);

			CellStyle expStyle = wb.createCellStyle();
			Font eFont = wb.createFont();
			eFont.setColor(IndexedColors.RED.getIndex());
			eFont.setBold(true);
			expStyle.setFont(eFont);

			// Title row
			Row titleRow = sheet.createRow(0);
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue("ExpenseOS — " + book.getName());
			Font tf = wb.createFont();
			tf.setBold(true);
			tf.setFontHeightInPoints((short) 14);
			CellStyle ts = wb.createCellStyle();
			ts.setFont(tf);
			titleCell.setCellStyle(ts);
			sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

			// Summary row
			Row sumRow = sheet.createRow(1);
			sumRow.createCell(0).setCellValue("Total Income: ₹" + income);
			sumRow.createCell(2).setCellValue("Total Expense: ₹" + expense);
			sumRow.createCell(4).setCellValue("Balance: ₹" + income.subtract(expense));

			// Header
			Row hRow = sheet.createRow(3);
			String[] cols = { "Date & Time", "Type", "Category", "Sub Category", "Amount", "Note" };
			for (int i = 0; i < cols.length; i++) {
				Cell c = hRow.createCell(i);
				c.setCellValue(cols[i]);
				c.setCellStyle(headerStyle);
			}

			// Data
			int rowNum = 4;
			for (Transaction t : txns) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(t.getFormattedDateTime());
				Cell typeCell = row.createCell(1);
				typeCell.setCellValue(t.getType().name());
				typeCell.setCellStyle(t.getType() == Transaction.Type.INCOME ? incomeStyle : expStyle);
				row.createCell(2).setCellValue(nvl(t.getCategoryName()));
				row.createCell(3).setCellValue(nvl(t.getSubCategoryName()));
				Cell amtCell = row.createCell(4);
				amtCell.setCellValue((t.getType() == Transaction.Type.INCOME ? "+" : "-") + t.getAmount());
				amtCell.setCellStyle(t.getType() == Transaction.Type.INCOME ? incomeStyle : expStyle);
				row.createCell(5).setCellValue(nvl(t.getNote()));
			}

			// Auto-size
			for (int i = 0; i < 6; i++)
				sheet.autoSizeColumn(i);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			wb.write(bos);
			return bos.toByteArray();
		}
	}

	// ── Helpers ───────────────────────────────────────────────────────────
	private void addCell(PdfPTable table, String text, com.itextpdf.text.Font font, BaseColor bg) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setBackgroundColor(bg);
		cell.setPadding(5);
		table.addCell(cell);
	}

	private void addSummaryCell(PdfPTable table, String label, String value, BaseColor bg) {
		com.itextpdf.text.Font lf = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.DARK_GRAY);
		com.itextpdf.text.Font vf = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);
		PdfPCell cell = new PdfPCell();
		cell.addElement(new Phrase(label, lf));
		cell.addElement(new Phrase(value, vf));
		cell.setBackgroundColor(bg);
		cell.setPadding(8);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);
	}

	private String nvl(String s) {
		return s != null ? s : "";
	}
}
