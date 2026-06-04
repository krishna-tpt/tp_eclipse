package com.zoho.analytics.jdbc;

import java.util.HashMap;

class ZohoAnalyticsMetaField {
   private static HashMap<String, Integer> fieldNameType = new HashMap();

   private ZohoAnalyticsMetaField() {
   }

   static int getType(String fieldName) {
      Integer value = (Integer)fieldNameType.get(fieldName);
      return value == null ? 12 : value;
   }

   static int getSubTypeId(String fieldName) {
      return 1;
   }

   static String getSubTypeName(String fieldName) {
      return "PLAIN TEXT";
   }

   static {
      fieldNameType.put("ATTR_SIZE", 4);
      fieldNameType.put("AUTO_INCREMENT", 16);
      fieldNameType.put("BASE_TYPE", 4);
      fieldNameType.put("BUFFER_LENGTH", 4);
      fieldNameType.put("CARDINALITY", 4);
      fieldNameType.put("CASE_SENSITIVE", 16);
      fieldNameType.put("CHAR_OCTET_LENGTH", 4);
      fieldNameType.put("COLUMN_SIZE", 4);
      fieldNameType.put("COLUMN_TYPE", 4);
      fieldNameType.put("DATA_TYPE", 4);
      fieldNameType.put("DECIMAL_DIGITS", 4);
      fieldNameType.put("DEFERRABILITY", 4);
      fieldNameType.put("DELETE_RULE", 4);
      fieldNameType.put("FIXED_PREC_SCALE", 16);
      fieldNameType.put("KEY_SEQ", 4);
      fieldNameType.put("LENGTH", 4);
      fieldNameType.put("MAXIMUM_SCALE", 4);
      fieldNameType.put("NON_UNIQUE", 16);
      fieldNameType.put("NULLABLE", 4);
      fieldNameType.put("NUM_PREC_RADIX", 4);
      fieldNameType.put("ORDINAL_POSITION", 4);
      fieldNameType.put("PAGES", 4);
      fieldNameType.put("PRECISION", 4);
      fieldNameType.put("PROCEDURE_TYPE", 4);
      fieldNameType.put("PSEUDO_COLUMN", 4);
      fieldNameType.put("RADIX", 4);
      fieldNameType.put("SCALE", 4);
      fieldNameType.put("SEARCHABLE", 4);
      fieldNameType.put("SOURCE_DATA_TYPE", 4);
      fieldNameType.put("SQL_DATA_TYPE", 4);
      fieldNameType.put("SQL_DATETIME_SUB", 4);
      fieldNameType.put("TYPE", 4);
      fieldNameType.put("UNSIGNED_ATTRIBUTE", 16);
      fieldNameType.put("UPDATE_RULE", 4);
   }
}
