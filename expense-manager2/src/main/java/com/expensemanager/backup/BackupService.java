package com.expensemanager.backup;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
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
import java.util.zip.ZipOutputStream;

import com.expensemanager.dao.BackupDAO;
import com.expensemanager.model.BackupMetadata;
import com.expensemanager.model.BackupMetadata.BackupStatus;
import com.expensemanager.model.BackupMetadata.BackupType;
import com.expensemanager.util.DBConnection;

/**
 * Core backup/restore engine.
 * ZIP contents: backup_info.txt, income.csv, expense.csv,
 *               custom_columns.csv, custom_column_values.csv,
 *               income_categories.csv, expense_categories.csv
 */
public class BackupService {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String DEFAULT_DIR = System.getProperty("user.home") + "/expense_backups";
    private final BackupDAO dao = new BackupDAO();

    // ── CREATE ────────────────────────────────────────────────────────────────
    public BackupMetadata createBackup(String description, BackupType type) throws Exception {
        Path dir = Paths.get(System.getProperty("expense.backup.dir", DEFAULT_DIR));
        Files.createDirectories(dir);
        String ts       = LocalDateTime.now().format(FMT);
        String fileName = "backup_" + ts + ".zip";
        Path   filePath = dir.resolve(fileName);

        BackupMetadata meta = new BackupMetadata();
        meta.setFileName(fileName); meta.setFilePath(filePath.toString());
        meta.setBackupType(type); meta.setStatus(BackupStatus.PENDING);
        meta.setDescription(description != null ? description : "");
        meta.setCreatedAt(LocalDateTime.now());
        int id = dao.insert(meta); meta.setId(id);

        try (Connection con = DBConnection.getInstance().getConnection()) {
            int inc = dao.countRows("income"), exp = dao.countRows("expense");
            try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(filePath.toFile())))) {
                writeInfo(zos, ts, inc, exp, description);
                writeCSV(zos, con, "income.csv",             "SELECT id,transaction_date,transaction_time,amount,category,note,created_at FROM income ORDER BY id");
                writeCSV(zos, con, "expense.csv",            "SELECT id,transaction_date,transaction_time,amount,category,note,created_at FROM expense ORDER BY id");
                writeCSV(zos, con, "custom_columns.csv",     "SELECT * FROM custom_columns ORDER BY id");
                writeCSV(zos, con, "custom_column_values.csv","SELECT * FROM custom_column_values ORDER BY id");
                writeCSV(zos, con, "income_categories.csv",  "SELECT * FROM income_categories ORDER BY name");
                writeCSV(zos, con, "expense_categories.csv", "SELECT * FROM expense_categories ORDER BY name");
            }
            long size = Files.size(filePath);
            dao.updateCompletion(id, BackupStatus.SUCCESS, size, inc, exp, null);
            meta.setStatus(BackupStatus.SUCCESS); meta.setFileSizeBytes(size);
            meta.setIncomeCount(inc); meta.setExpenseCount(exp);
        } catch (Exception ex) {
            dao.updateCompletion(id, BackupStatus.FAILED, 0, 0, 0, ex.getMessage());
            meta.setStatus(BackupStatus.FAILED); throw ex;
        }
        return meta;
    }

    // ── RESTORE ───────────────────────────────────────────────────────────────
    public void restoreBackup(int backupId) throws Exception {
        BackupMetadata meta = dao.getById(backupId);
        if (meta == null)         throw new Exception("Backup not found: " + backupId);
        if (!meta.isRestorable()) throw new Exception("Backup not restorable: " + meta.getStatus());
        Path zipPath = Paths.get(meta.getFilePath());
        if (!Files.exists(zipPath)) throw new Exception("Backup file missing: " + zipPath);

        createBackup("Auto-backup before restore of #" + backupId, BackupType.AUTO_BEFORE_RESTORE);
        dao.updateStatus(backupId, BackupStatus.RESTORING);

        try (Connection con = DBConnection.getInstance().getConnection()) {
            con.setAutoCommit(false);
            try (ZipFile zip = new ZipFile(zipPath.toFile())) {
                truncate(con);
                restoreCSV(con, zip, "income_categories.csv",
                    "INSERT INTO income_categories (name) VALUES (?) ON CONFLICT DO NOTHING",
                    r -> new Object[]{ r[1] });
                restoreCSV(con, zip, "expense_categories.csv",
                    "INSERT INTO expense_categories (name) VALUES (?) ON CONFLICT DO NOTHING",
                    r -> new Object[]{ r[1] });
                restoreCSV(con, zip, "income.csv",
                    "INSERT INTO income (id,transaction_date,transaction_time,amount,category,note,created_at) VALUES (?,?,?,?,?,?,?)",
                    r -> new Object[]{ iOf(r[0]),r[1],r[2],bdOf(r[3]),r[4],r[5],tsOf(r[6]) });
                restoreCSV(con, zip, "expense.csv",
                    "INSERT INTO expense (id,transaction_date,transaction_time,amount,category,note,created_at) VALUES (?,?,?,?,?,?,?)",
                    r -> new Object[]{ iOf(r[0]),r[1],r[2],bdOf(r[3]),r[4],r[5],tsOf(r[6]) });
                restoreCSV(con, zip, "custom_columns.csv",
                    "INSERT INTO custom_columns (id,table_name,column_name,column_label,column_type) VALUES (?,?,?,?,?)",
                    r -> new Object[]{ iOf(r[0]),r[1],r[2],r[3],r[4] });
                restoreCSV(con, zip, "custom_column_values.csv",
                    "INSERT INTO custom_column_values (id,table_name,record_id,column_name,column_value) VALUES (?,?,?,?,?)",
                    r -> new Object[]{ iOf(r[0]),r[1],iOf(r[2]),r[3],r[4] });
                resetSeq(con);
            }
            con.commit();
            dao.updateStatus(backupId, BackupStatus.RESTORED);
        } catch (Exception ex) {
            dao.updateStatus(backupId, BackupStatus.SUCCESS); throw ex;
        }
    }

    public byte[] getBackupBytes(int id) throws Exception {
        BackupMetadata m = dao.getById(id); if(m==null) throw new Exception("Not found");
        Path p = Paths.get(m.getFilePath()); if(!Files.exists(p)) throw new Exception("File missing");
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
        m.setFileName(fileName); m.setFilePath(dest.toString()); m.setFileSizeBytes(zipBytes.length);
        m.setBackupType(BackupType.MANUAL); m.setStatus(BackupStatus.SUCCESS);
        m.setDescription("Uploaded: " + (desc!=null?desc:origName)); m.setCreatedAt(LocalDateTime.now());
        int id = dao.insert(m); dao.updateCompletion(id,BackupStatus.SUCCESS,zipBytes.length,0,0,null);
        m.setId(id); return m;
    }

    public void deleteBackup(int id) throws Exception {
        BackupMetadata m = dao.getById(id);
        if(m!=null){ Files.deleteIfExists(Paths.get(m.getFilePath())); dao.delete(id); }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void writeInfo(ZipOutputStream zos, String ts, int inc, int exp, String desc) throws IOException {
        zos.putNextEntry(new ZipEntry("backup_info.txt"));
        zos.write(("ExpenseIQ Backup\nTimestamp: "+ts+"\nIncome: "+inc+"\nExpense: "+exp+"\nDesc: "+(desc!=null?desc:"")+"\n").getBytes("UTF-8"));
        zos.closeEntry();
    }
    private void writeCSV(ZipOutputStream zos, Connection con, String entry, String sql) throws Exception {
        zos.putNextEntry(new ZipEntry(entry));
        try(PreparedStatement ps=con.prepareStatement(sql); ResultSet rs=ps.executeQuery()){
            ResultSetMetaData rm=rs.getMetaData(); int n=rm.getColumnCount();
            StringBuilder sb=new StringBuilder();
            for(int i=1;i<=n;i++){ if(i>1)sb.append(','); sb.append(esc(rm.getColumnName(i))); } sb.append('\n');
            zos.write(sb.toString().getBytes("UTF-8"));
            while(rs.next()){
                sb.setLength(0);
                for(int i=1;i<=n;i++){ if(i>1)sb.append(','); Object v=rs.getObject(i); sb.append(v==null?"":esc(v.toString())); }
                sb.append('\n'); zos.write(sb.toString().getBytes("UTF-8"));
            }
        }
        zos.closeEntry();
    }
    private void truncate(Connection con) throws SQLException {
        try(Statement st=con.createStatement()){
            for(String t:new String[]{"custom_column_values","custom_columns","income","expense","income_categories","expense_categories"})
                st.execute("TRUNCATE TABLE "+t+" RESTART IDENTITY CASCADE");
        }
    }
    private void resetSeq(Connection con) throws SQLException {
        try(Statement st=con.createStatement()){
            for(String t:new String[]{"income","expense","custom_columns","custom_column_values","income_categories","expense_categories"})
                st.execute("SELECT setval(pg_get_serial_sequence('"+t+"','id'),COALESCE((SELECT MAX(id)+1 FROM "+t+"),1),false)");
        }
    }
    @FunctionalInterface interface RM { Object[] map(String[] r); }
    private void restoreCSV(Connection con, ZipFile zip, String entry, String sql, RM mapper) throws Exception {
        ZipEntry ze=zip.getEntry(entry); if(ze==null) return;
        try(BufferedReader br=new BufferedReader(new InputStreamReader(zip.getInputStream(ze),"UTF-8"));
            PreparedStatement ps=con.prepareStatement(sql)){
            String line; boolean first=true;
            while((line=br.readLine())!=null){
                if(first){first=false;continue;} if(line.trim().isEmpty()) continue;
                Object[] vals=mapper.map(parseCSV(line));
                for(int i=0;i<vals.length;i++) ps.setObject(i+1,vals[i]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    private void validateZip(Path p) throws Exception {
        try(ZipFile z=new ZipFile(p.toFile())){
            if(z.getEntry("backup_info.txt")==null||z.getEntry("income.csv")==null) throw new Exception("Invalid backup ZIP.");
        }
    }
    private String esc(String s){ if(s==null)return ""; if(s.contains(",")||s.contains("\"")||s.contains("\n")) return "\""+s.replace("\"","\"\"")+'"'; return s; }
    private String[] parseCSV(String line){
        java.util.List<String> t=new java.util.ArrayList<>(); StringBuilder cur=new StringBuilder(); boolean q=false;
        for(int i=0;i<line.length();i++){ char c=line.charAt(i);
            if(c=='"'){ if(q&&i+1<line.length()&&line.charAt(i+1)=='"'){cur.append('"');i++;}else q=!q; }
            else if(c==','&&!q){t.add(cur.toString());cur.setLength(0);}else cur.append(c); }
        t.add(cur.toString()); return t.toArray(new String[0]);
    }
    private Integer iOf(String s){ try{return(s==null||s.isEmpty())?null:Integer.parseInt(s.trim());}catch(Exception e){return null;} }
    private BigDecimal bdOf(String s){ try{return(s==null||s.isEmpty())?BigDecimal.ZERO:new BigDecimal(s.trim());}catch(Exception e){return BigDecimal.ZERO;} }
    private Timestamp tsOf(String s){ try{return(s==null||s.isEmpty())?null:Timestamp.valueOf(s.trim().replace("T"," "));}catch(Exception e){return null;} }
}