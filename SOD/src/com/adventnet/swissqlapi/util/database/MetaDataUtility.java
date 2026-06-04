package com.adventnet.swissqlapi.util.database;

import com.adventnet.swissqlapi.config.metadata.MetaDataProperties;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class MetaDataUtility {
   private MetaDataProperties metaDataProperty = null;
   private String driverName = null;
   private String driverURL = null;
   private String userName = null;
   private String password = null;
   private String catalogname = null;
   private String schemaname = null;
   private String tablenamepattern = null;
   private String columnnamepattern = null;
   private String destinationFile = null;
   private Connection databaseConnection = null;
   private DatabaseMetaData dmd = null;

   public MetaDataUtility() throws IOException {
      try {
         MetaDataProperties property = new MetaDataProperties((String)null);
         this.metaDataProperty = property;
         this.getMetaDataUtilConfiguration();
      } catch (IOException var2) {
         System.out.println("conf/MetaDataConfiguration.conf file Not found.");
      } catch (Exception var3) {
         System.out.println("conf/MetaDataConfiguration.conf file Not found.");
      }

   }

   public MetaDataUtility(MetaDataProperties property) throws IOException {
      this.metaDataProperty = property;
      this.getMetaDataUtilConfiguration();
   }

   public MetaDataUtility(Connection connection, MetaDataProperties property) throws IOException {
      this.metaDataProperty = property;
      this.databaseConnection = connection;
      this.getMetaDataUtilConfiguration();
   }

   public MetaDataUtility(Connection connection, String catalogName, String schemaName, String tableNamePattern, String columnNamePattern) throws IOException {
      this.databaseConnection = connection;
      this.catalogname = catalogName;
      this.schemaname = schemaName;
      this.tablenamepattern = tableNamePattern;
      this.columnnamepattern = columnNamePattern;
      if (!this.catalogname.trim().equals("null") && !this.catalogname.trim().equals("") && !this.catalogname.trim().equals("*")) {
         this.catalogname = this.catalogname.toUpperCase();
      } else {
         this.catalogname = null;
      }

      if (!this.tablenamepattern.trim().equals("null") && !this.tablenamepattern.trim().equals("") && !this.tablenamepattern.trim().equals("*")) {
         this.tablenamepattern = this.tablenamepattern.toUpperCase();
      } else {
         this.tablenamepattern = null;
      }

      if (!this.columnnamepattern.trim().equals("null") && !this.columnnamepattern.trim().equals("") && !this.columnnamepattern.trim().equals("*")) {
         this.columnnamepattern = this.columnnamepattern.toUpperCase();
      } else {
         this.columnnamepattern = null;
      }

   }

   public void getMetaData(Vector outputStrings) throws SQLException, Exception {
      try {
         File dest = null;
         if (this.destinationFile != null) {
            dest = new File(this.destinationFile);
         } else {
            dest = new File("conf/DatabaseMetaDataFile.conf");
            this.destinationFile = "conf/DatabaseMetaDataFile.conf";
            System.out.println("Default MetadataStorage file is taken as conf/DatabaseMetaDataFile.conf and proceeding...");
         }

         FileOutputStream fos = null;

         try {
            fos = new FileOutputStream(dest);
         } catch (FileNotFoundException var11) {
            System.out.println(" File Not Found : " + dest + ". Default MetadataStorage file is taken as conf/DatabaseMetaDataFile.conf");
            fos = new FileOutputStream("conf/DatabaseMetaDataFile.conf");
            this.destinationFile = "conf/DatabaseMetaDataFile.conf";
         }

         OutputStreamWriter osw = new OutputStreamWriter(fos);
         PrintWriter pw = new PrintWriter(osw);
         if (this.databaseConnection == null && this.driverName != null && this.driverURL != null) {
            Class.forName(this.driverName);
            this.databaseConnection = DriverManager.getConnection(this.driverURL, this.userName, this.password);
         }

         if (this.databaseConnection != null) {
            this.dmd = this.databaseConnection.getMetaData();
         }

         ResultSet rs;
         if (this.schemaname != null) {
            for(StringTokenizer st1 = new StringTokenizer(this.schemaname, ","); st1.hasMoreTokens(); rs.close()) {
               String schema = st1.nextToken().trim();
               if (!schema.trim().equals("null") && !schema.trim().equals("*")) {
                  schema = schema.toUpperCase();
               } else {
                  schema = null;
               }

               if (this.catalogname != null && this.catalogname.trim().equals("*")) {
                  this.catalogname = null;
               }

               if (this.tablenamepattern != null && this.tablenamepattern.trim().equals("*")) {
                  this.tablenamepattern = null;
               }

               if (this.columnnamepattern != null && this.columnnamepattern.trim().equals("*")) {
                  this.columnnamepattern = null;
               }

               rs = this.dmd.getColumns(this.catalogname, schema, this.tablenamepattern, this.columnnamepattern);
               boolean isSuccess = this.printResultSet(rs, pw);
               String out;
               if (isSuccess) {
                  if (schema == null) {
                     out = "\nMetadata successfully fetched from the database\n";
                     System.out.println(out);
                     if (outputStrings != null) {
                        outputStrings.addElement(out);
                     }
                  } else {
                     out = "\nMetadata for schema '" + schema + "' successfully fetched from the database\n";
                     System.out.println(out);
                     if (outputStrings != null) {
                        outputStrings.addElement(out);
                     }
                  }
               } else if (schema == null) {
                  out = "\nFetching of Metadata failed\n";
                  System.out.println(out);
                  if (outputStrings != null) {
                     outputStrings.addElement(out);
                  }
               } else {
                  out = "\nFetching of Metadata for schema '" + schema + "' failed\n";
                  System.out.println(out);
                  if (outputStrings != null) {
                     outputStrings.addElement(out);
                  }
               }
            }
         } else if (this.dmd != null) {
            ResultSet rs = this.dmd.getColumns(this.catalogname, this.schemaname, this.tablenamepattern, this.columnnamepattern);
            boolean isSucess = this.printResultSet(rs, pw);
            String out;
            if (isSucess) {
               if (this.schemaname == null) {
                  out = "\nMetadata successfully fetched from the database\n";
                  System.out.println(out);
                  if (outputStrings != null) {
                     outputStrings.addElement(out);
                  }
               } else {
                  out = "\nMetadata for schema '" + this.schemaname + "' successfully fetched from the database\n";
                  System.out.println(out);
                  if (outputStrings != null) {
                     outputStrings.addElement(out);
                  }
               }
            } else if (this.schemaname == null) {
               out = "\nFetching of Metadata failed\n";
               System.out.println(out);
               if (outputStrings != null) {
                  outputStrings.addElement(out);
               }
            } else {
               out = "\nFetching of Metadata for schema '" + this.schemaname + "' failed\n";
               System.out.println(out);
               if (outputStrings != null) {
                  outputStrings.addElement(out);
               }
            }

            rs.close();
         }

         pw.close();
      } catch (SQLException var12) {
         throw var12;
      } catch (Exception var13) {
         throw var13;
      }
   }

   public String getDestinationFile() {
      return this.destinationFile;
   }

   private boolean printResultSet(ResultSet rs) throws SQLException, IOException {
      File dest = null;
      if (this.destinationFile != null) {
         dest = new File(this.destinationFile);
      } else {
         dest = new File("conf/DatabaseMetaDataFile.conf");
         this.destinationFile = "conf/DatabaseMetaDataFile.conf";
      }

      FileOutputStream fos = null;

      try {
         fos = new FileOutputStream(dest);
      } catch (FileNotFoundException var6) {
         System.out.println(" File Not Found : " + dest + ". Default MetadataStorage file is taken as conf/DatabaseMetaDataFile.conf");
         fos = new FileOutputStream("conf/DatabaseMetaDataFile.conf");
         this.destinationFile = "conf/DatabaseMetaDataFile.conf";
      }

      OutputStreamWriter osw = new OutputStreamWriter(fos);
      PrintWriter pw = new PrintWriter(osw);
      return this.printResultSet(rs, pw);
   }

   private boolean printResultSet(ResultSet rs, PrintWriter pw) throws SQLException, IOException {
      ResultSetMetaData rsmd = rs.getMetaData();
      boolean isSucess = false;

      try {
         Hashtable tablePKColList = new Hashtable(10);

         while(true) {
            while(rs.next()) {
               String tableName = rs.getString(3);
               String columnName = rs.getString(4);
               pw.println("" + rsmd.getColumnLabel(3) + "=: " + tableName);
               pw.println("" + rsmd.getColumnLabel(4) + "=: " + columnName);
               String schema = null;
               if (!this.schemaname.trim().equals("null") && !this.schemaname.trim().equals("*")) {
                  schema = this.schemaname.toUpperCase();
               } else {
                  schema = null;
               }

               if (!tablePKColList.containsKey(tableName)) {
                  ArrayList pkColList = new ArrayList();
                  ResultSet rsPKCols = this.dmd.getPrimaryKeys(this.catalogname, schema, tableName);

                  while(rsPKCols.next()) {
                     pkColList.add(rsPKCols.getString(4));
                  }

                  rsPKCols.close();
                  tablePKColList.put(tableName, pkColList);
               }

               String typeLength = "";
               if (rs.getString(7) != null) {
                  typeLength = "(" + rs.getString(7);
               }

               if (rs.getString(9) != null && !rs.getString(9).trim().equalsIgnoreCase("0")) {
                  typeLength = typeLength + "," + rs.getString(9) + ")";
               } else {
                  typeLength = typeLength + ")";
               }

               if (!rs.getString(6).equalsIgnoreCase("DATE") && !rs.getString(6).equalsIgnoreCase("LONG") && !rs.getString(6).equalsIgnoreCase("RAW") && !rs.getString(6).equalsIgnoreCase("LONG RAW") && !rs.getString(6).equalsIgnoreCase("BFILE") && !rs.getString(6).equalsIgnoreCase("BLOB") && !rs.getString(6).equalsIgnoreCase("CLOB") && !rs.getString(6).equalsIgnoreCase("TIMESTAMP")) {
                  pw.println("" + rsmd.getColumnLabel(6) + "=: " + rs.getString(6) + typeLength);
               } else {
                  pw.println("" + rsmd.getColumnLabel(6) + "=: " + rs.getString(6));
               }

               if (tablePKColList.get(tableName) != null && ((ArrayList)tablePKColList.get(tableName)).contains(columnName)) {
                  pw.println("PRIMARY_KEY=: 1");
               } else {
                  pw.println("PRIMARY_KEY=: 0");
               }
            }

            isSucess = true;
            break;
         }
      } catch (Exception var11) {
         System.out.println(" Exception in Print Resultset. Proceeding with default handling...");
      }

      return isSucess;
   }

   private void getMetaDataUtilConfiguration() throws IOException {
      this.destinationFile = this.metaDataProperty.getMetadataStorageFile();
      this.driverName = this.metaDataProperty.getDriverName();
      this.driverURL = this.metaDataProperty.getConnectionURL();
      this.userName = this.metaDataProperty.getUserName();
      this.password = this.metaDataProperty.getPasswd();
      this.catalogname = this.metaDataProperty.getCatalogName();
      this.schemaname = this.metaDataProperty.getSchemaName();
      this.tablenamepattern = this.metaDataProperty.getTableNamePattern();
      this.columnnamepattern = this.metaDataProperty.getColumnNamePattern();
      if (this.tablenamepattern != null) {
         this.tablenamepattern = this.tablenamepattern.toUpperCase();
      }

      if (this.columnnamepattern != null) {
         this.columnnamepattern = this.columnnamepattern.toUpperCase();
      }

   }
}
