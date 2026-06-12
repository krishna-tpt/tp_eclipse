package com.expensemanager.service;

import com.expensemanager.model.CashBook;
import com.expensemanager.model.Transaction;
import com.expensemanager.servlet.ExportServlet;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ExportService {
	private static final Logger log = LoggerFactory.getLogger(ExportService.class);
	private static final BaseColor COL_HEADER = new BaseColor(37, 99, 235);
	private static final BaseColor COL_GREEN = new BaseColor(220, 252, 231);
	private static final BaseColor COL_RED = new BaseColor(254, 226, 226);
	private static final BaseColor COL_BLUE = new BaseColor(219, 234, 254);
	private static final BaseColor COL_ALT = new BaseColor(248, 250, 252);

	// ── Transactions PDF (full or filtered) ──────────────
	public byte[] generatePDF(CashBook book, List<Transaction> txns, BigDecimal income, BigDecimal expense,
			String filterLabel) throws Exception {
		Document doc = new Document(PageSize.A4.rotate(), 28, 28, 28, 28);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PdfWriter writer = PdfWriter.getInstance(doc, bos);
		writer.setPageEvent(new PageFooter());
		doc.open();

		addTitle(doc, "ExpenseOS — " + book.getName()
				+ (filterLabel != null && !filterLabel.isBlank() ? " (" + filterLabel + ")" : ""));
		addSubtitle(doc, "Generated " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
		addSummaryTable(doc, income, expense);

		// Transactions table
		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100);
		table.setWidths(new float[] { 2.8f, 1.4f, 2f, 2f, 1.8f, 3f });
		table.setSpacingBefore(10);
		addHeaderRow(table, "Date & Time", "Type", "Category", "Sub Cat", "Amount", "Note");
		boolean alt = false;
		for (Transaction t : txns) {
			BaseColor bg = alt ? COL_ALT : BaseColor.WHITE;
			alt = !alt;
			addCell(table, t.getFormattedDateTime(), 9, bg, Element.ALIGN_LEFT);
			boolean isIncome = t.getType() == Transaction.Type.INCOME;
			PdfPCell typeCell = styledCell(t.getType().name(), 8,
					isIncome ? new BaseColor(21, 128, 61) : new BaseColor(185, 28, 28), isIncome ? COL_GREEN : COL_RED);
			table.addCell(typeCell);
			addCell(table, nvl(t.getCategoryName()), 9, bg, Element.ALIGN_LEFT);
			addCell(table, nvl(t.getSubCategoryName()), 9, bg, Element.ALIGN_LEFT);
			String amt = (isIncome ? "+" : "-") + "₹" + t.getAmount();
			addCell(table, amt, 9, isIncome ? new BaseColor(240, 255, 244) : new BaseColor(255, 240, 240),
					Element.ALIGN_RIGHT);
			addCell(table, nvl(t.getNote()), 9, bg, Element.ALIGN_LEFT);
		}
		if (txns.isEmpty()) {
			PdfPCell empty = new PdfPCell(new Phrase("No transactions.", small()));
			empty.setColspan(6);
			empty.setPadding(10);
			empty.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(empty);
		}
		doc.add(table);
		doc.close();
		return bos.toByteArray();
	}

	// ── Reports PDF ───────────────────────────────────────
	public byte[] generateReportsPDF(CashBook book, BigDecimal income, BigDecimal expense,
			List<Map<String, Object>> monthly, List<Map<String, Object>> expByCat, List<Map<String, Object>> incByCat)
			throws Exception {
		Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PdfWriter writer = PdfWriter.getInstance(doc, bos);
		writer.setPageEvent(new PageFooter());
		doc.open();

		addTitle(doc, "ExpenseOS — Reports: " + book.getName());
		addSubtitle(doc, "Generated " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
		addSummaryTable(doc, income, expense);

		// Monthly trend table
		doc.add(sectionTitle("Monthly Trend"));
		PdfPTable monthTable = new PdfPTable(4);
		monthTable.setWidthPercentage(80);
		monthTable.setHorizontalAlignment(Element.ALIGN_LEFT);
		monthTable.setSpacingBefore(6);
		addHeaderRow(monthTable, "Month", "Income", "Expense", "Net");
		boolean alt = false;
		for (Map<String, Object> row : monthly) {
			BaseColor bg = alt ? COL_ALT : BaseColor.WHITE;
			alt = !alt;
			BigDecimal inc = (BigDecimal) row.get("income");
			BigDecimal exp = (BigDecimal) row.get("expense");
			BigDecimal net = inc.subtract(exp);
			addCell(monthTable, row.get("month").toString(), 9, bg, Element.ALIGN_LEFT);
			addCell(monthTable, "₹" + inc, 9, new BaseColor(240, 255, 244), Element.ALIGN_RIGHT);
			addCell(monthTable, "₹" + exp, 9, new BaseColor(255, 240, 240), Element.ALIGN_RIGHT);
			addCell(monthTable, "₹" + net, 9,
					net.signum() >= 0 ? new BaseColor(235, 248, 255) : new BaseColor(255, 235, 235),
					Element.ALIGN_RIGHT);
		}
		doc.add(monthTable);

		// Two-column: expense vs income by category
		doc.add(sectionTitle("Category Breakdown"));
		PdfPTable catGrid = new PdfPTable(2);
		catGrid.setWidthPercentage(100);
		catGrid.setSpacingBefore(6);

		PdfPCell expHeader = new PdfPCell(sectionTitle("Expense by Category"));
		expHeader.setBorder(Rectangle.NO_BORDER);
		catGrid.addCell(expHeader);
		PdfPCell incHeader = new PdfPCell(sectionTitle("Income by Category"));
		incHeader.setBorder(Rectangle.NO_BORDER);
		catGrid.addCell(incHeader);

		// Expense categories nested table
		PdfPTable expCatTable = new PdfPTable(2);
		addHeaderRow(expCatTable, "Category", "Total");
		for (Map<String, Object> row : expByCat)
			buildCatRow(expCatTable, row);
		PdfPCell expCell = new PdfPCell();
		expCell.addElement(expCatTable);
		expCell.setBorder(Rectangle.NO_BORDER);
		catGrid.addCell(expCell);

		PdfPTable incCatTable = new PdfPTable(2);
		addHeaderRow(incCatTable, "Category", "Total");
		for (Map<String, Object> row : incByCat)
			buildCatRow(incCatTable, row);
		PdfPCell incCell = new PdfPCell();
		incCell.addElement(incCatTable);
		incCell.setBorder(Rectangle.NO_BORDER);
		catGrid.addCell(incCell);

		doc.add(catGrid);
		doc.close();
		return bos.toByteArray();
	}

	// ── Calendar PDF ──────────────────────────────────────
	public byte[] generateCalendarPDF(CashBook book, int year, int month, List<Map<String, Object>> dailyData)
			throws Exception {
		String[] MONTHS = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		Document doc = new Document(PageSize.A4.rotate(), 28, 28, 28, 28);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PdfWriter writer = PdfWriter.getInstance(doc, bos);
		writer.setPageEvent(new PageFooter());
		doc.open();

		addTitle(doc, "ExpenseOS — Calendar: " + MONTHS[month] + " " + year);
		addSubtitle(doc,
				book.getName() + " | Generated " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

		// Build daily map
		Map<String, Map<String, Object>> dayMap = new java.util.LinkedHashMap<>();
		for (Map<String, Object> d : dailyData)
			dayMap.put(d.get("day").toString(), d);

		// Calendar grid — 7 columns
		PdfPTable grid = new PdfPTable(7);
		grid.setWidthPercentage(100);
		grid.setWidths(new float[] { 1f, 1f, 1f, 1f, 1f, 1f, 1f }); // equal columns
		grid.setSpacingBefore(12);

		// Day headers
		String[] days = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
		for (String d : days) {
			PdfPCell h = new PdfPCell(new Phrase(d, hdrFont()));
			h.setBackgroundColor(COL_HEADER);
			h.setPadding(6);
			h.setHorizontalAlignment(Element.ALIGN_CENTER);
			grid.addCell(h);
		}

		// ✅ Fix: Sunday=0 based firstDow
		// Java DayOfWeek: MONDAY=1 ... SUNDAY=7
		// We need Sunday=0, Monday=1 ... Saturday=6
		int javaDow = LocalDate.of(year, month, 1).getDayOfWeek().getValue(); // Mon=1, Sun=7
		int firstDow = javaDow % 7; // Sun=0, Mon=1, Tue=2 ... Sat=6 ✅

		int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();

		log.debug("firstDow (Sun=0): {} --> daysInMonth: {}", firstDow, daysInMonth);

		// Empty cells before first day
		for (int i = 0; i < firstDow; i++) {
			PdfPCell empty = new PdfPCell(new Phrase(" "));
			empty.setMinimumHeight(60);
			empty.setBackgroundColor(new BaseColor(248, 248, 248));
			empty.setBorder(Rectangle.BOX);
			empty.setBorderColor(new BaseColor(220, 220, 220));
			grid.addCell(empty);
		}

		// Day cells
		for (int d = 1; d <= daysInMonth; d++) {
			String ds = String.format("%d-%02d-%02d", year, month, d);
			Map<String, Object> data = dayMap.get(ds);
			PdfPCell cell = new PdfPCell();
			cell.setMinimumHeight(60);
			cell.setPadding(4);
			cell.setBorder(Rectangle.BOX);
			cell.setBorderColor(new BaseColor(220, 220, 220));

			// ✅ Date number — top of cell
			cell.addElement(new Phrase(String.valueOf(d), dateFont()));

			if (data != null) {
				BigDecimal inc = toBD(data.get("income"));
				BigDecimal exp = toBD(data.get("expense"));
				if (inc.compareTo(BigDecimal.ZERO) > 0)
					cell.addElement(new Phrase("+\u20b9" + inc,
							FontFactory.getFont(FontFactory.HELVETICA, 7, new BaseColor(21, 128, 61))));
				if (exp.compareTo(BigDecimal.ZERO) > 0)
					cell.addElement(new Phrase("-\u20b9" + exp,
							FontFactory.getFont(FontFactory.HELVETICA, 7, new BaseColor(185, 28, 28))));
				cell.setBackgroundColor(
						inc.compareTo(exp) >= 0 ? new BaseColor(240, 255, 244) : new BaseColor(255, 240, 240));
			}
			grid.addCell(cell);
		}

		// ✅ Fill remaining cells to complete last row
		int totalCells = firstDow + daysInMonth;
		int remainder = totalCells % 7;
		if (remainder != 0) {
			for (int i = 0; i < (7 - remainder); i++) {
				PdfPCell empty = new PdfPCell(new Phrase(" "));
				empty.setMinimumHeight(60);
				empty.setBackgroundColor(new BaseColor(248, 248, 248));
				empty.setBorder(Rectangle.BOX);
				empty.setBorderColor(new BaseColor(220, 220, 220));
				grid.addCell(empty);
			}
		}

		doc.add(grid);
		doc.close();
		return bos.toByteArray();
	}

	// ✅ Helper — safe BigDecimal conversion
	private BigDecimal toBD(Object val) {
		if (val == null)
			return BigDecimal.ZERO;
		if (val instanceof BigDecimal)
			return (BigDecimal) val;
		try {
			return new BigDecimal(val.toString());
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	// ── Excel ─────────────────────────────────────────────
	public byte[] generateExcel(CashBook book, List<Transaction> txns, BigDecimal income, BigDecimal expense,
			String filterLabel) throws Exception {
		try (XSSFWorkbook wb = new XSSFWorkbook()) {
			Sheet sheet = wb.createSheet(book.getName());
			CellStyle hdrStyle = createHdrStyle(wb);
			CellStyle incStyle = createColorStyle(wb, IndexedColors.DARK_GREEN);
			CellStyle expStyle = createColorStyle(wb, IndexedColors.RED);

			// Title
			Row r0 = sheet.createRow(0);
			Cell c0 = r0.createCell(0);
			c0.setCellValue("ExpenseOS — " + book.getName()
					+ (filterLabel != null && !filterLabel.isBlank() ? " (" + filterLabel + ")" : ""));
			CellStyle ts = wb.createCellStyle();
			org.apache.poi.ss.usermodel.Font tf = wb.createFont();
			tf.setBold(true);
			tf.setFontHeightInPoints((short) 13);
			ts.setFont(tf);
			c0.setCellStyle(ts);
			sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

			// Summary
			Row r1 = sheet.createRow(1);
			r1.createCell(0).setCellValue("Income: ₹" + income);
			r1.createCell(2).setCellValue("Expense: ₹" + expense);
			r1.createCell(4).setCellValue("Balance: ₹" + income.subtract(expense));

			// Header
			Row hr = sheet.createRow(3);
			String[] cols = { "Date & Time", "Type", "Category", "Sub Category", "Amount", "Note" };
			for (int i = 0; i < cols.length; i++) {
				Cell c = hr.createCell(i);
				c.setCellValue(cols[i]);
				c.setCellStyle(hdrStyle);
			}

			int rowNum = 4;
			for (Transaction t : txns) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(t.getFormattedDateTime());
				Cell tc = row.createCell(1);
				boolean isInc = t.getType() == Transaction.Type.INCOME;
				tc.setCellValue(t.getType().name());
				tc.setCellStyle(isInc ? incStyle : expStyle);
				row.createCell(2).setCellValue(nvl(t.getCategoryName()));
				row.createCell(3).setCellValue(nvl(t.getSubCategoryName()));
				Cell ac = row.createCell(4);
				ac.setCellValue((isInc ? "+" : "-") + t.getAmount());
				ac.setCellStyle(isInc ? incStyle : expStyle);
				row.createCell(5).setCellValue(nvl(t.getNote()));
			}
			for (int i = 0; i < 6; i++)
				sheet.autoSizeColumn(i);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			wb.write(bos);
			return bos.toByteArray();
		}
	}

	// ── Helpers ───────────────────────────────────────────
	private void addTitle(Document doc, String text) throws DocumentException {
		Paragraph p = new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, BaseColor.BLACK));
		p.setAlignment(Element.ALIGN_CENTER);
		p.setSpacingAfter(3);
		doc.add(p);
	}

	private void addSubtitle(Document doc, String text) throws DocumentException {
		Paragraph p = new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY));
		p.setAlignment(Element.ALIGN_CENTER);
		p.setSpacingAfter(10);
		doc.add(p);
	}

	private Paragraph sectionTitle(String text) {
		Paragraph p = new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK));
		p.setSpacingBefore(10);
		p.setSpacingAfter(4);
		return p;
	}

	private void addSummaryTable(Document doc, BigDecimal income, BigDecimal expense) throws DocumentException {
		PdfPTable t = new PdfPTable(3);
		t.setWidthPercentage(65);
		t.setHorizontalAlignment(Element.ALIGN_CENTER);
		t.setSpacingAfter(12);
		addSummaryCell(t, "Total Income", "₹" + income, COL_GREEN);
		addSummaryCell(t, "Total Expense", "₹" + expense, COL_RED);
		addSummaryCell(t, "Balance", "₹" + income.subtract(expense), COL_BLUE);
		doc.add(t);
	}

	private void addSummaryCell(PdfPTable t, String label, String value, BaseColor bg) {
		PdfPCell cell = new PdfPCell();
		cell.addElement(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.DARK_GRAY)));
		cell.addElement(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
		cell.setBackgroundColor(bg);
		cell.setPadding(8);
		cell.setBorder(Rectangle.NO_BORDER);
		t.addCell(cell);
	}

	private void addHeaderRow(PdfPTable table, String... headers) {
		for (String h : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(h, hdrFont()));
			cell.setBackgroundColor(COL_HEADER);
			cell.setPadding(6);
			table.addCell(cell);
		}
	}

	private void addCell(PdfPTable table, String text, int size, BaseColor bg, int align) {
		PdfPCell cell = new PdfPCell(
				new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, size, BaseColor.BLACK)));
		cell.setBackgroundColor(bg);
		cell.setPadding(5);
		cell.setHorizontalAlignment(align);
		table.addCell(cell);
	}

	private PdfPCell styledCell(String text, int size, BaseColor color, BaseColor bg) {
		PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, size, color)));
		cell.setBackgroundColor(bg);
		cell.setPadding(5);
		return cell;
	}

	private void buildCatRow(PdfPTable table, Map<String, Object> row) {
		boolean alt = table.size() % 2 == 0;
		BaseColor bg = alt ? COL_ALT : BaseColor.WHITE;
		addCell(table, row.get("name").toString(), 9, bg, Element.ALIGN_LEFT);
		addCell(table, "₹" + row.get("total"), 9, bg, Element.ALIGN_RIGHT);
	}

	private com.itextpdf.text.Font hdrFont() {
		return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
	}

	private com.itextpdf.text.Font small() {
		return FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);
	}

	private com.itextpdf.text.Font dateFont() {
		return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.BLACK);
	}

	private CellStyle createHdrStyle(XSSFWorkbook wb) {
		CellStyle s = wb.createCellStyle();
		org.apache.poi.ss.usermodel.Font f = wb.createFont();
		f.setBold(true);
		f.setColor(IndexedColors.WHITE.getIndex());
		s.setFont(f);
		s.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
		s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return s;
	}

	private CellStyle createColorStyle(XSSFWorkbook wb, IndexedColors color) {
		CellStyle s = wb.createCellStyle();
		org.apache.poi.ss.usermodel.Font f = wb.createFont();
		f.setBold(true);
		f.setColor(color.getIndex());
		s.setFont(f);
		return s;
	}

	private String nvl(String s) {
		return s != null ? s : "";
	}

	// Page footer for PDF
	private static class PageFooter extends PdfPageEventHelper {
		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte cb = writer.getDirectContent();
			Phrase footer = new Phrase("ExpenseOS  |  Page " + writer.getPageNumber(),
					FontFactory.getFont(FontFactory.HELVETICA, 7, BaseColor.GRAY));
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
					(document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);
		}
	}
}