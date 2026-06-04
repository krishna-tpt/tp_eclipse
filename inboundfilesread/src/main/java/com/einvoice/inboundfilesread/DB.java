package com.einvoice.inboundfilesread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

public class DB {

	Connection conn = null;
	Properties prop = new Properties();
	String inboundPath = null;
	String inboundPath_Archieve = null;
	String inboundDestination = null;
	ArrayList<File> listOfFiles = new ArrayList<>();
	PreparedStatement ps = null;
	ResultSet rs = null;
	int totalFilesUpdatedKSEF;

	{
		try (FileInputStream fs = new FileInputStream("DB_config.Properties")) {
			prop.load(fs);
		} catch (IOException e) {
			System.out.println("File loading error");
		}
		String url = prop.getProperty("db.url");
		String username = prop.getProperty("db.username");
		String password = prop.getProperty("db.password");
		inboundPath = prop.getProperty("file.inbound");
		inboundPath_Archieve = prop.getProperty("file.iboundArcheive");
		inboundDestination = prop.getProperty("file.destination");
		try {
			conn = DriverManager.getConnection(url, username, password);
			conn.setAutoCommit(true);
			System.out.println("Database connected...");
		} catch (SQLException e) {
			System.out.println("DB Connection Error : "+e.getMessage());
		}
	}

	public void verifyFilesProcessed() {
		File dir = new File(inboundPath);
		System.out.println("InBoundPath: "+inboundPath);
		System.out.println("inboundPath_Archieve: "+inboundPath_Archieve);
		File[] jsonFiles = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toUpperCase().endsWith(".JSON");
			}
		});

		if (jsonFiles == null || jsonFiles.length == 0) {
			System.out.println("No JSON files found.");
			return;
		}

		System.out.println("Found " + jsonFiles.length + " JSON file(s):\n");

		for (File file : jsonFiles) {
			String sql = "Select Count(*) From fw_incoming_files Where actual_file_name=?";
			try {
				ps = conn.prepareStatement(sql);
				ps.setString(1, file.getName()); // destination
				rs = ps.executeQuery();
				while (rs.next()) {
					int count = rs.getInt(1);
					if (count == 0) {
						listOfFiles.add(file);
					}
				}

				System.out.println("Reading into FW_INCOMING_FILES");

			} catch (SQLException e) {
				System.out.println("Error: Reading FW_INCOMING_FILES Table : "+e.getMessage());
			} finally {
				closeStatemt(ps);
			}
		}

		System.out.println("File need to be processed : " + listOfFiles.size());

	}

	public void readJsonFiles() {
		for (File file : listOfFiles) {
			long size = file.length() / 1024;
			String sql = "Insert into FW_INCOMING_FILES (DESTINATION,DIRECTORY_PATH,ACTUAL_FILE_NAME,FILE_SIZE,FILE_TIMESTAMP,CREATED) values (?,?,?,?,?,Sysdate)";
			try {
				ps = conn.prepareStatement(sql);
				ps.setString(1, inboundDestination); // destination
				ps.setString(2, inboundPath); // path
				ps.setString(3, file.getName()); // filename
				ps.setLong(4, size); // size
				ps.setString(5, getFileStamp(file)); // File Time Stamp
				ps.executeUpdate();
				System.out.println("Inserted into FW_INCOMING_FILES");

			} catch (SQLException e) {
				System.out.println("Error: Insert FW_INCOMING_FILES Table : "+e.getMessage());
			} finally {
				closeStatemt(ps);
			}
		}

	}

	public String getFileStamp(File file) {
		long lastModified = file.lastModified();
		Date date = new Date(lastModified);

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy hh.mm.ss.SSS000000 a z");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

		return sdf.format(date).toUpperCase();
	}

	public void processingFiles() {

		for (int i = 0; i < listOfFiles.size(); i++) {
			StringBuilder sb = new StringBuilder();
			File file = listOfFiles.get(i);
			System.out.println("File Name : " + file.getName());

			BufferedReader br = null;

			try {
				br = new BufferedReader(new FileReader(file));
				String line = null;

				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

			} catch (IOException e) {
				System.err.println("Error reading file: " + e.getMessage());
				// return null;
			} finally {
				
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						System.out.println("Error:Buffered Reading closing.."+e.getMessage());
					}
				}
			}
			
			if (sb.length() == 0 || sb.toString() == null) {
	            System.out.println("Error: Empty file, skipping: " + file.getName());
				continue;
			}
			
			processInvoices(sb.toString(), file.getName());

		}
		// return sb.toString();
	}

	public void processInvoices(String jsonContent, String fileName) {

		try {
			JSONObject root = new JSONObject(jsonContent);
			JSONObject invoiceStatus = root.getJSONObject("invoiceStatus");
			
			JSONArray documents;
			Object documentRow=invoiceStatus.get("document");
			
			//Handle both JSONArray and single JSONObject
			if(documentRow instanceof JSONArray) {
				documents=(JSONArray)documentRow;
			}else if (documentRow instanceof JSONObject) {
				documents=new JSONArray();
				documents.put((JSONObject)documentRow); // wrap single object in array
			}else {
	            System.out.println("Error: Unexpected 'document' type in file: " + fileName);
	            return;
			}
			

			for (int i = 0; i < documents.length(); i++) {
				JSONObject doc = documents.getJSONObject(i);

				String Documentno = doc.optString("documentNumber");
				String documentDate = doc.optString("documentDate");
				String customer = doc.optString("customer");
				String ksefNumber = doc.optString("validationStamp");
				String ksefDateStr = doc.optString("validationDate");

				java.sql.Date docDate = parseDate(documentDate, "yyyy-MM-dd");
				java.sql.Date ksefDate = parseDateTime(ksefDateStr, "yyyy-MM-dd'T'HH:mm:ss");
				
				System.out.println("File Name : "+ fileName);
				System.out.println("Documentno : "+Documentno);
				System.out.println("documentDate : "+documentDate);
				System.out.println("customer : "+customer);
				System.out.println("ksefNumber : "+ksefNumber);
				System.out.println("ksefDateStr : "+ksefDateStr);

				int matchCount = getInvoiceMatchCount(Documentno, docDate, customer);
				System.out.println("matchCount : "+matchCount);
				
				if (matchCount == 1) {
					
					totalFilesUpdatedKSEF=totalFilesUpdatedKSEF+matchCount;
					
					long C_invoice_ID = getInvoiceID(Documentno, docDate, customer);

					updateCInvoice(C_invoice_ID, ksefNumber, ksefDate);

					updateEInvoiceLog(C_invoice_ID, ksefNumber, ksefDate, fileName);

					moveArcheiveFolder(fileName);
				}

			}
		} catch (Exception e) {
			System.out.println("Error: processInvoices method : "+e.getMessage());
		}
	}

	public int getInvoiceMatchCount(String Documentno, java.sql.Date docDate, String customer) {
		String sql = "SELECT Count(i.C_Invoice_ID)  FROM C_Invoice i INNER JOIN C_BPartner bp"
				+ "    ON i.C_BPartner_ID = bp.C_BPartner_ID"
				+ "    WHERE i.AD_Org_ID = 1000002 AND i.Documentno = ? AND i.Dateinvoiced = ? "
				+ "    AND bp.TaxID = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, Documentno);
			ps.setDate(2, docDate);
			ps.setString(3, customer);
			rs = ps.executeQuery();

			while (rs.next()) {
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			System.out.println("Select getInvoiceMatchCount Method : "+e.getMessage());
		} finally {
			closeStatemt(ps);
			closeResultSet(rs);
		}
		return 0;
	}

	public int getInvoiceID(String Documentno, java.sql.Date docDate, String customer) {
		String sql = "SELECT i.C_Invoice_id  FROM C_Invoice i INNER JOIN C_BPartner bp"
				+ "    ON i.C_BPartner_ID = bp.C_BPartner_ID"
				+ "    WHERE i.AD_Org_ID = 1000002 AND i.Documentno = ? AND i.Dateinvoiced = ? "
				+ "    AND bp.TaxID = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, Documentno);
			ps.setDate(2, docDate);
			ps.setString(3, customer);
			rs = ps.executeQuery();

			while (rs.next()) {
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			System.out.println("Select getInvoiceID Method : "+e.getMessage());
		} finally {
			closeStatemt(ps);
			closeResultSet(rs);
		}
		return 0;
	}

	public void updateCInvoice(long invoiceid, String ksefNumber, java.sql.Date ksefDate) {
		String sql = "UPDATE C_Invoice SET Z_EINV_ValidationStamp = ?, Z_EINV_ValidationDate  = ? "
				+ " WHERE AD_Org_ID = 1000002 AND C_Invoice_ID = ? AND Z_EINV_ValidationStamp is null and Z_EINV_ValidationDate is null";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, ksefNumber);
			ps.setDate(2, ksefDate);
			ps.setLong(3, invoiceid);
			ps.executeUpdate();

		} catch (SQLException e) {
			System.err.println("Error updating C_Invoice: " + e.getMessage());
		} finally {
			closeStatemt(ps);
		}
	}

	public void updateEInvoiceLog(long invoiceid, String ksefNumber, java.sql.Date ksefDate, String fileName) {
		String sql = "UPDATE zpo_einvoice_log SET validationdate = ?, validationstamp  = ?, "
				+ "inbound_filename=?, Updated= sysdate " + " WHERE AD_Org_ID = 1000002 AND C_Invoice_ID = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setDate(1, ksefDate);
			ps.setString(2, ksefNumber);
			ps.setString(3, fileName);
			ps.setLong(4, invoiceid);
			ps.executeUpdate();

		} catch (SQLException e) {
			System.err.println("Error updating zpo_einvoice_log: " + e.getMessage());
		} finally {
			closeStatemt(ps);
		}
	}

	public void moveArcheiveFolder(String fileName) {
		
//		Path source=Paths.get(inboundPath,fileName);
//		Path destination =Paths.get(inboundPath_Archieve);
		
		
		File sourceFile = new File(inboundPath, fileName);
		File archeiveDir = new File(inboundPath_Archieve);
		File destFile = new File(archeiveDir, fileName);

		if (destFile.exists()) {
			System.err.println("Error moving file: file already exists in archive.");
			return;
		}

		boolean moved = sourceFile.renameTo(destFile);
		if (moved) {
			System.out.println("File moved to archive: " + destFile.getAbsolutePath());
		} else
			System.err.println("Error moving file to archive: " + fileName); 
	}

	public java.sql.Date parseDate(String dateStr, String pattern) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return new java.sql.Date(sdf.parse(dateStr).getTime());
	}

	private java.sql.Date parseDateTime(String dateStr, String pattern) throws ParseException {
		return parseDate(dateStr, pattern);
	}

	public void closeStatemt(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				System.out.println("Error: While Close Statement : "+e.getMessage());
			}
		}
	}

	public void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				System.out.println("Error: While Close ResultSet : "+e.getMessage());
			}
		}
	}
}
