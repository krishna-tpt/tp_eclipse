package com.sodreport.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipFolders {
    private static final Logger logger = LoggerFactory.getLogger(ZipFolders.class);
    private final DBUtil dbutil;
    private String zipFileName; // Store the ZIP file name

    public ZipFolders(DBUtil dbutil) {
        this.dbutil = dbutil;
        logger.debug("Initialized ZipFolders with DBUtil instance");
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void convertExcelToZipFile() throws IOException, SQLException {
        logger.info("Starting ZIP file creation process");
        Properties prop = dbutil.getProperties();
        String zipfolder = prop.getProperty("zip");
        if (zipfolder == null) {
            zipfolder = prop.getProperty("Zip");
            if (zipfolder == null) {
                logger.error("ZIP folder property not found in DB.config");
                throw new IllegalArgumentException("ZIP folder property not found in DB.config");
            }
        }

        // Ensure the ZIP directory exists
        Path zipDir = Paths.get(zipfolder);
        try {
            Files.createDirectories(zipDir);
            logger.debug("ZIP directory ensured: {}", zipDir);
        } catch (IOException e) {
            logger.error("Failed to create ZIP directory {}: {}", zipDir, e.getMessage(), e);
            throw e;
        }

        String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        zipFileName = Paths.get(zipfolder, "SOD_Reports_" + currentDate + ".zip").toString();
        logger.info("Creating ZIP file: {}", zipFileName);

        try (FileOutputStream fos = new FileOutputStream(zipFileName);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            for (int ad_org_id : Main.org_ids) {
                String filepath = dbutil.getFileName(ad_org_id);
                File file = new File(filepath);
                if (file.exists()) {
                    logger.debug("Adding file to ZIP: {}", filepath);
                    zipFile(file, Paths.get(filepath).getFileName().toString(), zos);
                } else {
                    logger.warn("Skipping non-existent file: {}", filepath);
                    System.out.println("Skipping " + filepath);
                }
            }
            logger.info("ZIP file created successfully: {}", zipFileName);
        } catch (IOException e) {
            logger.error("Error creating ZIP file {}: {}", zipFileName, e.getMessage(), e);
            throw e;
        }
    }

    private void zipFile(File file, String entryName, ZipOutputStream zos) throws IOException {
        logger.debug("Zipping file: {} as entry: {}", file.getAbsolutePath(), entryName);
        try (FileInputStream fis = new FileInputStream(file)) {
            zos.putNextEntry(new ZipEntry(entryName));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            logger.trace("Added file to ZIP: {}", entryName);
        } catch (IOException e) {
            logger.error("Error zipping file {}: {}", entryName, e.getMessage(), e);
            throw e;
        }
    }
}