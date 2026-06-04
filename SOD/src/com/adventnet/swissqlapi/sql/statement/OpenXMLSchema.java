package com.adventnet.swissqlapi.sql.statement;

import com.adventnet.swissqlapi.SwisSQLAPI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class OpenXMLSchema {
   private ArrayList column_names = null;
   private ArrayList column_type = null;
   private ArrayList column_pattern = null;
   private ArrayList meta_property = null;
   private String tableName = null;
   private boolean noMetaData = false;

   public OpenXMLSchema() {
      this.column_names = new ArrayList();
      this.column_type = new ArrayList();
      this.column_pattern = new ArrayList();
      this.meta_property = new ArrayList();
   }

   public void setTableName(String name) {
      this.tableName = name;
   }

   public String getTableName() {
      return this.tableName;
   }

   public boolean getNoMetaData() {
      if (this.column_names.isEmpty() && this.column_type.isEmpty() && this.tableName != null) {
         this.buildTheColumnNamesFromTable();
      }

      return this.noMetaData;
   }

   public void setColumnNames(ArrayList column_names) {
      this.column_names = column_names;
   }

   public ArrayList getColumnNames() {
      return this.column_names;
   }

   public void setColumnPatterns(ArrayList column_pattern) {
      this.column_pattern = column_pattern;
   }

   public ArrayList getColumnPatterns() {
      return this.column_pattern;
   }

   public void setColumnTypes(ArrayList column_type) {
      this.column_type = column_type;
   }

   public ArrayList getColumnTypes() {
      return this.column_type;
   }

   public void setMetaProperties(ArrayList meta_property) {
      this.meta_property = meta_property;
   }

   public ArrayList getMetaProperties() {
      return this.meta_property;
   }

   public void buildTheColumnNamesFromTable() {
      if (!SwisSQLAPI.getDataTypesFromMetaDataHT().isEmpty() && SwisSQLAPI.getDataTypesFromMetaDataHT().containsKey(this.tableName)) {
         Hashtable colDetails = (Hashtable)SwisSQLAPI.getDataTypesFromMetaDataHT().get(this.tableName);
         Enumeration cols = colDetails.keys();

         while(cols.hasMoreElements()) {
            String columnName = (String)cols.nextElement();
            this.column_names.add(columnName);
            String columnType = (String)colDetails.get(columnName);
            this.column_type.add(columnType);
         }
      } else {
         this.noMetaData = true;
      }

   }
}
