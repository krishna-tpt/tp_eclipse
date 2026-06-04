package com.adventnet.swissqlapi.config.datatypes;

import java.util.Hashtable;
import java.util.Map;

public class DatatypeMapping {
   Hashtable globalType = new Hashtable();
   Hashtable tableMapping = new Hashtable();

   public void addGlobalDatatypeMapping(String sourceDatatype, String mappedDatatype) {
      if (sourceDatatype != null && mappedDatatype != null) {
         this.globalType.put(sourceDatatype.trim().toLowerCase(), mappedDatatype);
      }

   }

   public void addGlobalDatatypeMapping(Map datatypeMappings) {
      this.globalType.putAll(datatypeMappings);
   }

   public void addTableSpecificDatatypeMapping(String tableName, String columnName, String mappedDatatype) {
      if (tableName != null && columnName != null && mappedDatatype != null) {
         Hashtable columnDatatypeMapping = new Hashtable();
         if (this.tableMapping != null && this.tableMapping.containsKey(tableName.toLowerCase())) {
            columnDatatypeMapping = (Hashtable)this.tableMapping.get(tableName.toLowerCase());
            columnDatatypeMapping.put(columnName.toLowerCase(), mappedDatatype);
         } else {
            columnDatatypeMapping.put(columnName.toLowerCase(), mappedDatatype);
            this.tableMapping.put(tableName.toLowerCase(), columnDatatypeMapping);
         }
      }

   }

   public Hashtable getGlobalDatatypeMapping() {
      return this.globalType;
   }

   public Hashtable getTableSpecificDatatypeMapping() {
      return this.tableMapping;
   }
}
