package org.smslite.sharepointetl;

import org.smslite.sharepointetl.db.PostgresLoader;
import org.smslite.sharepointetl.model.SharePointFile;
import org.smslite.sharepointetl.model.FolderConfig;
import org.smslite.sharepointetl.sharepoint.SharePointClient;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.Set;

public class ETLRunner {

    private static final Logger logger = LoggerFactory.getLogger(ETLRunner.class);

    // Set logging properties before any logger is initialized
    static {
        try {
            String configPath = System.getenv("ETL_CONFIG_PATH");
            String configFilePath;
            if (configPath != null && !configPath.isEmpty()) {
                configFilePath = new File(configPath).getAbsolutePath();
            } else {
                configFilePath = new File("config.properties").getAbsolutePath();
            }
            
            // Get log directory from config or use default
            String logDir = "logs";
            String logFileName = "etl";
            try (FileInputStream tempConfigInput = new FileInputStream(configFilePath)) {
                Properties tempProps = new Properties();
                tempProps.load(tempConfigInput);
                String logDirProp = tempProps.getProperty("etl.log.directory");
                if (logDirProp != null && !logDirProp.isEmpty()) {
                    logDir = logDirProp;
                }
                String logFileProp = tempProps.getProperty("etl.log.file");
                if (logFileProp != null && !logFileProp.isEmpty()) {
                    logFileName = logFileProp;
                }
            } catch (Exception e) {
                // fallback to defaults
            }

            // Create logs directory if it doesn't exist
            File logDirectory = new File(logDir);
            if (!logDirectory.exists()) {
                logDirectory.mkdirs();
            }

            // Set up rolling log file with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String logFilePath = Paths.get(logDir, logFileName + "_" + timestamp + ".log").toString();
            
            System.setProperty("org.slf4j.simpleLogger.logFile", logFilePath);
            System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
            System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss.SSS");
            System.setProperty("org.slf4j.simpleLogger.showThreadName", "true");
            System.setProperty("org.slf4j.simpleLogger.showLogName", "true");
            System.setProperty("org.slf4j.simpleLogger.showShortLogName", "true");
            System.setProperty("org.slf4j.simpleLogger.logFileLevel", "INFO");
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");
            
            logger.info("Logging initialized. Log file: {}", logFilePath);
        } catch (Exception e) {
            // fallback to defaults
            System.err.println("Failed to initialize logging: " + e.getMessage());
        }
    }

    private static String readMultiLineProperty(InputStream input, String propertyName) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            StringBuilder value = new StringBuilder();
            String line;
            boolean foundProperty = false;
            boolean inJson = false;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith(propertyName + "=")) {
                    foundProperty = true;
                    value.append(line.substring(line.indexOf('=') + 1).trim());
                    if (value.toString().startsWith("[")) {
                        inJson = true;
                    }
                } else if (foundProperty && inJson) {
                    if (line.trim().startsWith("]")) {
                        value.append(line.trim());
                        break;
                    } else {
                        value.append(line.trim());
                    }
                }
            }
            return value.toString();
        }
    }

    private static String addTimestampToFilename(String originalFilename) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        // If the filename already has a timestamp, remove it first
        String baseFilename = originalFilename;
        if (originalFilename.matches("\\d{8}_\\d{6}_.*")) {
            baseFilename = originalFilename.substring(15); // Remove existing timestamp
        }
        return String.format("%s_%s", timestamp, baseFilename);
    }

    /**
	 * Change for loading sequence added on 08102025
     * Sort files by priority (for dependency management) then by creation time (FIFO within same priority).
     * This ensures parent data (e.g., Products) is processed before dependent data (e.g., ProductDiscounts).
     * 
     * @param files List of SharePoint files to sort
     * @param mappings JSON array of file mappings with priority configuration
     */
    private static void sortFilesByPriority(List<SharePointFile> files, JSONArray mappings) {
        files.sort((f1, f2) -> {
            int priority1 = getFilePriority(f1.getName().toLowerCase(), mappings);
            int priority2 = getFilePriority(f2.getName().toLowerCase(), mappings);
            
            if (priority1 != priority2) {
                // Lower priority number = higher priority (process first)
                return Integer.compare(priority1, priority2);
            }
            // Same priority → use timestamp (FIFO)
            return f1.getCreatedTime().compareTo(f2.getCreatedTime());
        });
    }

    /**
     * Get the priority value for a file based on its filename prefix.
     * Used to determine processing order for dependency management.
     * 
     * @param fileName Name of the file (lowercase)
     * @param mappings JSON array of file mappings with priority configuration
     * @return Priority value (lower = higher priority), 999 if not found
     */
    private static int getFilePriority(String fileName, JSONArray mappings) {
        try {
            for (int i = 0; i < mappings.length(); i++) {
                JSONObject mapping = mappings.getJSONObject(i);
                String prefix = mapping.getString("prefix").toLowerCase();
                if (fileName.startsWith(prefix)) {
                    // Return priority from config, or default to 999 if not specified
                    int priority = mapping.optInt("priority", 999);
                    logger.debug("File '{}' matched prefix '{}' with priority {}", fileName, prefix, priority);
                    return priority;
                }
            }
        } catch (Exception e) {
            logger.warn("Error getting priority for file: {}", fileName, e);
        }
        logger.debug("File '{}' did not match any prefix, using default priority 999", fileName);
        return 999; // Unknown files go last
    }

    /**
     * Get the priority value for a folder configuration based on its name.
     * Used to determine processing order for received folders.
     * 
     * @param folderName Name of the folder
     * @param mappings JSON array of file mappings with priority configuration
     * @return Priority value (lower = higher priority), 999 if not found
     */
    private static int getFolderPriority(String folderName, JSONArray mappings) {
        try {
            for (int i = 0; i < mappings.length(); i++) {
                JSONObject mapping = mappings.getJSONObject(i);
                if (mapping.getString("folder").equals(folderName)) {
                    int priority = mapping.optInt("priority", 999);
                    logger.debug("Folder '{}' has priority {}", folderName, priority);
                    return priority;
                }
            }
        } catch (Exception e) {
            logger.warn("Error getting priority for folder: {}", folderName, e);
        }
        logger.debug("Folder '{}' did not match any mapping, using default priority 999", folderName);
        return 999; // Unknown folders go last
    }

    /**
     * Sort master folder configurations by priority for dependency-aware processing.
     * This ensures folders with parent data are processed before folders with dependent data.
     * 
     * @param masterFolders JSONArray of master folder configurations
     * @param mappings JSON array of file mappings with priority configuration
     * @return Sorted list of folder configurations
     */
    private static List<JSONObject> sortMasterFoldersByPriority(JSONArray masterFolders, JSONArray mappings) {
        List<JSONObject> sortedFolders = new ArrayList<>();
        for (int i = 0; i < masterFolders.length(); i++) {
            sortedFolders.add(masterFolders.getJSONObject(i));
        }
        
        sortedFolders.sort((a, b) -> {
            int priorityA = getFolderPriority(a.getString("name"), mappings);
            int priorityB = getFolderPriority(b.getString("name"), mappings);
            return Integer.compare(priorityA, priorityB);
        });
        
        logger.info("Sorted {} master folders by priority for dependency-aware processing", sortedFolders.size());
        return sortedFolders;
    }	
	// End of Change for File Loading Sequence - 08102025


    private static void distributeFilesFromCommonFolder(SharePointClient commonClient, List<FolderConfig> folderConfigs, 
            String tenantId, String clientId, String clientSecret, String driveId,
            String dbUrl, String dbUser, String dbPass, String configFilePath) {
        try {
            List<SharePointFile> commonFiles = commonClient.listFilesInFolder();
            if (commonFiles.isEmpty()) {
                logger.debug("No files found in common drop folder");
                return;
            }

            logger.debug("Found {} files in common drop folder", commonFiles.size());

            // Sort files by creation time (FIFO)
            //commonFiles.sort((f1, f2) -> f1.getCreatedTime().compareTo(f2.getCreatedTime()));
            //logger.debug("Sorted files by creation time");
            // End of Comment - 08102025

            // Load file mappings from config
            String mappingsStr;
            try (InputStream multiLineInput = new FileInputStream(configFilePath)) {
                mappingsStr = readMultiLineProperty(multiLineInput, "etl.file.mappings");
            }
            JSONArray mappings = new JSONArray(mappingsStr);
            logger.debug("Loaded {} file mappings from config", mappings.length());

            // Sort files by priority (based on dependency order) then creation time (FIFO within same priority) - - 08102025
            sortFilesByPriority(commonFiles, mappings);
            logger.info("Sorted {} files by priority and creation time for dependency-aware processing", commonFiles.size());
            // End of Change - 08102025

            for (SharePointFile file : commonFiles) {
                String fileName = file.getName().toLowerCase();
                logger.debug("Processing file: {}", fileName);
                
                // Find matching folder config based on file prefix
                FolderConfig targetConfig = null;
                for (int i = 0; i < mappings.length(); i++) {
                    JSONObject mapping = mappings.getJSONObject(i);
                    String prefix = mapping.getString("prefix").toLowerCase();
                    if (fileName.startsWith(prefix)) {
                        String folderName = mapping.getString("folder");
                        targetConfig = folderConfigs.stream()
                            .filter(fc -> fc.getName().equals(folderName))
                            .findFirst()
                            .orElse(null);
                        logger.debug("Found matching folder config: {} for prefix: {}", folderName, prefix);
                        break;
                    }
                }

                if (targetConfig != null) {
                    logger.info("Processing file {} for dataset {}", fileName, targetConfig.getName());
                    
                    // Download file to temp location
                    Path tempDir = Files.createTempDirectory("sharepoint-etl");
                    logger.debug("Created temporary directory: {}", tempDir);
                    
                    Path downloaded = downloadFile(file.getDownloadUrl(), tempDir.resolve(file.getName()));
                    logger.debug("Downloaded file to: {}", downloaded);
                    
                    // Get table columns from PostgreSQL
                    PostgresLoader loader = new PostgresLoader(dbUrl, dbUser, dbPass, targetConfig.getTargetTable());
                    List<String> requiredColumns = loader.getTableColumns();
                    logger.debug("Retrieved {} required columns from table {}", requiredColumns.size(), targetConfig.getTargetTable());
                    
                    // Validate CSV header against table columns
                    boolean isValid = validateCsvHeader(downloaded.toString(), requiredColumns, targetConfig.getIgnoreColumns());
                    logger.debug("CSV header validation result: {}", isValid);
                    
                    // Move file to appropriate folder with timestamp
                    SharePointClient targetClient = new SharePointClient(tenantId, clientId, clientSecret, driveId, targetConfig);
                    String newFileName = addTimestampToFilename(file.getName());
                    
                    if (isValid) {
                        logger.info("File {} passed validation, moving to received folder as {}", fileName, newFileName);
                        targetClient.moveFileToFolder(file.getId(), targetConfig.getReceivedFolderId(), newFileName);
                    } else {
                        logger.warn("File {} failed validation, moving to error folder as {}", fileName, newFileName);
                        targetClient.moveFileToFolder(file.getId(), targetConfig.getErrorFolderId(), newFileName);
                    }
                    
                    // Clean up temp directory
                    Files.deleteIfExists(downloaded);
                    Files.deleteIfExists(tempDir);
                    logger.debug("Cleaned up temporary files");
                } else {
                    logger.warn("No matching dataset folder found for file: {}", fileName);
                }
            }
        } catch (Exception e) {
            logger.error("Error distributing files from common folder: {}", e.getMessage(), e);
        }
    }

    private static boolean validateCsvHeader(String csvFilePath, List<String> requiredColumns, List<String> ignoreColumns) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(csvFilePath), Charset.forName("UTF-8")))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                logger.error("Empty CSV file: {}", csvFilePath);
                return false;
            }

            // Remove BOM if present
            if (headerLine.startsWith("\uFEFF")) {
                logger.debug("Stripping BOM from header line");
                headerLine = headerLine.substring(1);
            }
            logger.debug("Raw CSV header line: '{}'", headerLine);

            // Split header by semicolon and trim
            String[] headers = headerLine.split(";");
            for (int i = 0; i < headers.length; i++) {
                headers[i] = headers[i].trim();
            }
            logger.debug("Found {} columns in CSV header: {}", headers.length, String.join(", ", headers));

            // Normalize ignoreColumns for comparison
            Set<String> ignoreSet = ignoreColumns.stream()
                .map(s -> s.trim().toLowerCase().replaceAll("\\s+", ""))
                .collect(Collectors.toSet());
            List<String> filteredRequired = requiredColumns.stream()
                .filter(col -> !ignoreSet.contains(col.trim().toLowerCase().replaceAll("\\s+", "")))
                .collect(Collectors.toList());

            // Check if all required columns are present (compare as lowercase, no spaces)
            for (String requiredColumn : filteredRequired) {
                boolean found = false;
                String requiredNormalized = requiredColumn.trim().toLowerCase().replaceAll("\\s+", "");
                logger.debug("Looking for required column: '{}' (normalized: '{}')", requiredColumn, requiredNormalized);
                for (String header : headers) {
                    String headerNormalized = header.toLowerCase().replaceAll("\\s+", "");
                    logger.debug("Comparing with CSV header: '{}' (normalized: '{}')", header, headerNormalized);
                    if (headerNormalized.equals(requiredNormalized)) {
                        found = true;
                        logger.debug("Found match for column: '{}'", requiredColumn);
                        break;
                    }
                }
                if (!found) {
                    logger.error("Required column '{}' not found in CSV file: {}", requiredColumn, csvFilePath);
                    logger.error("Available CSV headers: {}", String.join(", ", headers));
                    return false;
                }
            }
            logger.debug("All required columns found in CSV header");
            return true;
        } catch (Exception e) {
            logger.error("Error validating CSV header: {}", e.getMessage(), e);
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            // Load config
            Properties props = new Properties();
            InputStream configInput = null;
            String configPath = System.getenv("ETL_CONFIG_PATH");
            String configFilePath;
            if (configPath != null && !configPath.isEmpty()) {
                File configFile = new File(configPath);
                if (configFile.exists()) {
                    configInput = new FileInputStream(configFile);
                    configFilePath = configFile.getAbsolutePath();
                    logger.info("Loaded config from external file: {}", configFilePath);
                } else {
                    logger.error("ETL_CONFIG_PATH is set but file does not exist: {}", configFile.getAbsolutePath());
                    throw new RuntimeException("Config file specified by ETL_CONFIG_PATH does not exist.");
                }
            } else {
                File configFile = new File("config.properties");
                if (configFile.exists()) {
                    configInput = new FileInputStream(configFile);
                    configFilePath = configFile.getAbsolutePath();
                    logger.info("Loaded config from working directory: {}", configFilePath);
                } else {
                    logger.error("No config.properties found in working directory and ETL_CONFIG_PATH not set.");
                    throw new RuntimeException("No config.properties found in working directory and ETL_CONFIG_PATH not set.");
                }
            }
            props.load(configInput);
            logger.debug("Successfully loaded configuration properties");

            // Read polling interval from config
            long pollingIntervalMs = 60000; // default 1 min
            String pollingIntervalStr = props.getProperty("etl.polling.interval.ms");
            if (pollingIntervalStr != null) {
                try {
                    pollingIntervalMs = Long.parseLong(pollingIntervalStr);
                    logger.debug("Using polling interval: {} ms", pollingIntervalMs);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid polling interval in config: {}. Using default {} ms.", pollingIntervalStr, pollingIntervalMs);
                }
            }

            while (true) {
                logger.info("Starting ETL polling cycle...");
                try {
                    // Parse folder configurations
                    String masterFoldersStr;
                    try (InputStream multiLineInput = new FileInputStream(configFilePath)) {
                        masterFoldersStr = readMultiLineProperty(multiLineInput, "sharepoint.master-folders");
                    }
                    JSONArray masterFolders = new JSONArray(masterFoldersStr);
                    logger.debug("Loaded {} master folder configurations", masterFolders.length());

                    String tenantId = props.getProperty("sharepoint.tenant-id");
                    String clientId = props.getProperty("sharepoint.client-id");
                    String clientSecret = props.getProperty("sharepoint.client-secret");
                    String driveId = props.getProperty("sharepoint.drive-id");
                    String dbUrl = props.getProperty("postgres.url");
                    String dbUser = props.getProperty("postgres.user");
                    String dbPass = props.getProperty("postgres.password");
                    logger.debug("Loaded SharePoint and database credentials");

                    // Get common drop folder ID from config
                    String commonDropFolderId = props.getProperty("sharepoint.common-drop-folder-id");
                    if (commonDropFolderId != null && !commonDropFolderId.isEmpty()) {
                        logger.debug("Processing common drop folder: {}", commonDropFolderId);
                        // Create a temporary FolderConfig for the common drop folder
                        FolderConfig commonConfig = new FolderConfig("common-drop", commonDropFolderId, commonDropFolderId, commonDropFolderId, null, null);
                        SharePointClient commonClient = new SharePointClient(tenantId, clientId, clientSecret, driveId, commonConfig);
                        
                        // Create list of folder configs for distribution
                        List<FolderConfig> folderConfigs = new ArrayList<>();
                        for (int i = 0; i < masterFolders.length(); i++) {
                            JSONObject folderObj = masterFolders.getJSONObject(i);
                            List<String> ignoreColumns = new ArrayList<>();
                            if (folderObj.has("ignore_columns")) {
                                JSONArray ignoreArr = folderObj.getJSONArray("ignore_columns");
                                for (int j = 0; j < ignoreArr.length(); j++) {
                                    ignoreColumns.add(ignoreArr.getString(j));
                                }
                            }
                            folderConfigs.add(new FolderConfig(
                                folderObj.getString("name"),
                                folderObj.getString("received"),
                                folderObj.getString("processed"),
                                folderObj.getString("error"),
                                folderObj.getString("table"),
                                null,
                                ignoreColumns
                            ));
                        }
                        logger.debug("Created {} folder configurations for distribution", folderConfigs.size());
                        
                        // Distribute files from common folder
                        distributeFilesFromCommonFolder(commonClient, folderConfigs, tenantId, clientId, clientSecret, driveId, dbUrl, dbUser, dbPass, configFilePath);
                    }
                    // Load file mappings for priority-based sorting - 08102025
                    String mappingsStr;
                    try (InputStream multiLineInput = new FileInputStream(configFilePath)) {
                        mappingsStr = readMultiLineProperty(multiLineInput, "etl.file.mappings");
                    }
                    JSONArray mappings = new JSONArray(mappingsStr);
                    logger.debug("Loaded {} file mappings for priority-based processing", mappings.length());

                    // Sort master folders by priority for dependency-aware processing
                    List<JSONObject> sortedMasterFolders = sortMasterFoldersByPriority(masterFolders, mappings);
                    //End of Change - 08102025

                    // Process each folder configuration in priority order
                    //for (int i = 0; i < masterFolders.length(); i++) {  - Commented 08102025
                    //    JSONObject folderObj = masterFolders.getJSONObject(i); - Commented 08102025
                    //Below Line Added 08102025
                    for (JSONObject folderObj : sortedMasterFolders) {
                        List<String> ignoreColumns = new ArrayList<>();
                        if (folderObj.has("ignore_columns")) {
                            JSONArray ignoreArr = folderObj.getJSONArray("ignore_columns");
                            for (int j = 0; j < ignoreArr.length(); j++) {
                                ignoreColumns.add(ignoreArr.getString(j));
                            }
                        }
                        FolderConfig folderConfig = new FolderConfig(
                            folderObj.getString("name"),
                            folderObj.getString("received"),
                            folderObj.getString("processed"),
                            folderObj.getString("error"),
                            folderObj.getString("table"),
                            null,
                            ignoreColumns
                        );

                        logger.info("Processing master folder: {}", folderConfig.getName());
                        SharePointClient client = new SharePointClient(tenantId, clientId, clientSecret, driveId, folderConfig);
                        PostgresLoader loader = new PostgresLoader(dbUrl, dbUser, dbPass, folderConfig.getTargetTable());
                        List<SharePointFile> files = client.listFilesInFolder();
                        if (files.isEmpty()) {
                            logger.debug("No files found in folder: {}", folderConfig.getName());
                            continue;
                        }
                        logger.debug("Found {} files in folder: {}", files.size(), folderConfig.getName());
                        
                        Path tempDir = Files.createTempDirectory("sharepoint-etl");
                        logger.debug("Created temporary directory: {}", tempDir);
                        
                        for (SharePointFile file : files) {
                            logger.info("Processing: {}", file.getName());
                            try {
                                // Download file
                                Path downloaded = downloadFile(file.getDownloadUrl(), tempDir.resolve(file.getName()));
                                logger.debug("Downloaded file to: {}", downloaded);
                                
                                // Load into PostgreSQL
                                loader.loadCsvToPostgres(downloaded.toString());
                                logger.info("Loaded to DB: {}", file.getName());
                                
                                // Move file to processed folder with timestamp
                                String newFileName = addTimestampToFilename(file.getName());
                                logger.info("Moving to processed folder as: {}", newFileName);
                                client.moveFile(file.getId(), newFileName);
                                
                                // Call postprocess procedure if configured
                                String postProcessProc = folderObj.optString("postprocess_procedure", null);
                                if (postProcessProc != null && !postProcessProc.isEmpty()) {
                                    logger.debug("Calling postprocess procedure: {}", postProcessProc);
                                    loader.callProcedure(postProcessProc);
                                }
                            } catch (Exception e) {
                                logger.error("Failed to process file: {} => {}", file.getName(), e.getMessage(), e);
                            }
                        }
                        logger.info("Completed processing folder: {}", folderConfig.getName());
                    }
                    logger.info("ETL polling cycle completed. Sleeping for {} ms...", pollingIntervalMs);
                } catch (InterruptedException e) {
                    logger.info("ETL polling interrupted. Exiting.");
                    break;
                } catch (Exception e) {
                    logger.error("ETL polling cycle failed: {}", e.getMessage(), e);
                }
                try {
                    Thread.sleep(pollingIntervalMs);
                } catch (InterruptedException e) {
                    logger.info("ETL polling interrupted during sleep. Exiting.");
                    break;
                }
            }

        } catch (Exception e) {
            logger.error("ETL job failed: {}", e.getMessage(), e);
        }
    }

    private static Path downloadFile(String fileUrl, Path targetPath) throws Exception {
        logger.debug("Downloading file from: {} to: {}", fileUrl, targetPath);
        try (InputStream in = new URL(fileUrl).openStream();
             FileOutputStream out = new FileOutputStream(targetPath.toFile())) {
            byte[] buffer = new byte[8192];
            int len;
            long totalBytes = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                totalBytes += len;
            }
            logger.debug("Downloaded {} bytes to: {}", totalBytes, targetPath);
        }
        return targetPath;
    }
}