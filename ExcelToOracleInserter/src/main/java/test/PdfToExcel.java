package test;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PdfToExcel {

    static final String PDF_PATH   = "/home/dev021/Downloads/files/F851763_ocr.pdf";
    static final String EXCEL_PATH = "/home/dev021/Downloads/files/invoice_data.xlsx";
    static final int START_PAGE = 9;    // 1-based
    static final int END_PAGE   = 16;   // 1-based, inclusive

    static final String TESSDATA = "/usr/share/tesseract-ocr/4.00/tessdata";
    static final int DPI = 300;
    static final double HEADER_RATIO = 0.11;  // top header skip (tune if needed)
    static final double FOOTER_RATIO = 0.01;  // bottom skip

    static final Pattern CARD = Pattern.compile(
            "(?:(S\\d?)\\s+)?(\\d{1,4})\\s*[|\\[\\]]*\\s*([A-Z0-9]{10})\\s*"
          + "Name\\s*[:;]?\\s*(.*?)\\s*"
          + "(Father|Husband|Mother|Other)\\s*Name\\s*[:;]?\\s*(.*?)\\s*"
          + "House\\s*Number\\s*[:;]?\\s*(.*?)\\s*"
          + "Age\\s*[:;]?\\s*(\\d+)\\s*Gender\\s*[:;]?\\s*(\\w+)",
            Pattern.DOTALL);

    static class Voter {
        int pageNo, serialNo;
        String mark, epicId, name, relationType, relationName, houseNo, gender;
        int age;
    }

    public static void main(String[] args) throws Exception {
        List<Voter> voters = new ArrayList<>();
        
        File file =new File(PDF_PATH);
        System.out.println("File Found : "+file.isFile());

        try (PDDocument doc = PDDocument.load(new File(PDF_PATH))) {
            System.out.println("Total pages : " + doc.getNumberOfPages());

            int last = Math.min(END_PAGE, doc.getNumberOfPages());
            for (int p = START_PAGE; p <= last; p++) {
                PDPage page = doc.getPage(p - 1);
                float w = page.getMediaBox().getWidth();
                float h = page.getMediaBox().getHeight();
                float top = (float) (h * HEADER_RATIO);
                float height = (float) (h - top - h * FOOTER_RATIO);
                float colW = w / 3f;

                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);
                for (int c = 0; c < 3; c++) {
                    stripper.addRegion("col" + c, new Rectangle2D.Float(c * colW, top, colW, height));
                }
                stripper.extractRegions(page);

                for (int c = 0; c < 3; c++) {
                    String text = stripper.getTextForRegion("col" + c);
                    if (p == START_PAGE) {
                        java.nio.file.Files.write(
                            java.nio.file.Paths.get("/home/dev021/Downloads/files/debug_p9.txt"),
                            ("=== col" + c + " RAW ===\n" + text + "\n").getBytes(java.nio.charset.StandardCharsets.UTF_8),
                            java.nio.file.StandardOpenOption.CREATE,
                            java.nio.file.StandardOpenOption.APPEND);
                    }
                    if (p == START_PAGE) {
                        System.out.println("--- Page " + p + " col" + c + " RAW (len=" + text.length() + ") ---");
                        System.out.println(text);
                    }
                    text = text.replaceAll("\\b(Photo|Available|DELETED)\\b", " ")
                               .replaceAll("\\s+", " ");
                    if (p == START_PAGE) {
                        System.out.println("--- CLEANED col" + c + " ---");
                        System.out.println(text);
                    }
                    Matcher m = CARD.matcher(text);
                    while (m.find()) {
                        Voter v = new Voter();
                        v.pageNo       = p;
                        v.mark         = m.group(1) == null ? "" : m.group(1);
                        v.serialNo     = Integer.parseInt(m.group(2));
                        v.epicId       = m.group(3);
                        v.name         = m.group(4).trim();
                        v.relationType = m.group(5);
                        v.relationName = m.group(6).trim();
                        v.houseNo      = m.group(7).trim();
                        v.age          = Integer.parseInt(m.group(8));
                        v.gender       = m.group(9);
                        voters.add(v);
                    }
                }
            }
        }

        voters.sort(Comparator.comparingInt((Voter v) -> v.pageNo).thenComparingInt(v -> v.serialNo));
        writeExcel(voters);
        System.out.println("Done. Records: " + voters.size());
    }

    static void writeExcel(List<Voter> list) throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Voters");
            String[] heads = {"Page", "Serial No", "Mark", "EPIC ID", "Name",
                              "Relation", "Relation Name", "House No", "Age", "Gender"};
            Row hr = sh.createRow(0);
            for (int i = 0; i < heads.length; i++) hr.createCell(i).setCellValue(heads[i]);

            int r = 1;
            for (Voter v : list) {
                Row row = sh.createRow(r++);
                row.createCell(0).setCellValue(v.pageNo);
                row.createCell(1).setCellValue(v.serialNo);
                row.createCell(2).setCellValue(v.mark);
                row.createCell(3).setCellValue(v.epicId);
                row.createCell(4).setCellValue(v.name);
                row.createCell(5).setCellValue(v.relationType);
                row.createCell(6).setCellValue(v.relationName);
                row.createCell(7).setCellValue(v.houseNo);
                row.createCell(8).setCellValue(v.age);
                row.createCell(9).setCellValue(v.gender);
            }
            for (int i = 0; i < heads.length; i++) sh.autoSizeColumn(i);

            try (FileOutputStream fos = new FileOutputStream(EXCEL_PATH)) {
                wb.write(fos);
            }
        }
    }
}