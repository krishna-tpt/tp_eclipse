package tptTest;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Exports all receipt files from transaction_receipts table
 * to a local folder.
 *
 * Run: javac ReceiptExporter.java && java ReceiptExporter
 */
public class ReceiptExporter {

    // ── DB config —  ──────────────────────────
    private static final String DB_URL  = "jdbc:postgresql://localhost:5432/mydb";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "postgres";

    // ── Output folder ────────────────────────────────────
    private static final String OUTPUT_DIR = "/home/dev021/receipts_export";

    public static void main(String[] args) throws Exception {

        // Output folder create         
    	Path outDir = Paths.get(OUTPUT_DIR);
        if (!Files.exists(outDir)) {
            Files.createDirectories(outDir);
            System.out.println("Created folder: " + outDir.toAbsolutePath());
        }

        String sql = "SELECT id, transaction_id, file_name, file_type, file_data " +
                     "FROM transaction_receipts ORDER BY id";

        int saved = 0, skipped = 0;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int    receiptId = rs.getInt("id");
                int    txnId     = rs.getInt("transaction_id");
                String fileName  = rs.getString("file_name");
                byte[] fileData  = rs.getBytes("file_data");

                if (fileData == null || fileData.length == 0) {
                    System.out.println("  [SKIP] receipt_id=" + receiptId + " — empty data");
                    skipped++;
                    continue;
                }

                // Sub-folder per transaction: receipts_export/txn_123/
                Path txnDir = outDir.resolve("txn_" + txnId);
                if (!Files.exists(txnDir)) {
                    Files.createDirectories(txnDir);
                }

                String safeFileName = receiptId + "_" + fileName;
                Path   filePath     = txnDir.resolve(safeFileName);

                try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                    fos.write(fileData);
                }

                System.out.printf("  [SAVED] receipt_id=%-4d  txn=%-4d  file=%s  size=%d bytes%n",
                        receiptId, txnId, safeFileName, fileData.length);
                saved++;
            }

        } catch (SQLException e) {
            System.err.println("DB Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=============================");
        System.out.println("Done! Saved: " + saved + "  Skipped: " + skipped);
        System.out.println("Output folder: " + outDir.toAbsolutePath());
        System.out.println("=============================");
    }
}
