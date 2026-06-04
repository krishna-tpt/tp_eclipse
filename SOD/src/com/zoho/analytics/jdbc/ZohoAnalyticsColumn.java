package com.zoho.analytics.jdbc;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class ZohoAnalyticsColumn implements ZohoAnalyticsData {
   private Map<String, String> columnProperties = new HashMap();
   private ZohoAnalyticsView table;

   String getColumnName() {
      return (String)this.columnProperties.get("COLUMN_NAME");
   }

   public String get(String propertyName) {
      return (String)this.columnProperties.get(propertyName);
   }

   void populateColumnMetaData(ZohoAnalyticsView table, JSONObject columnItem) {
      this.table = table;
      this.columnProperties.put("TABLE_CAT", table.get("TABLE_CAT"));
      this.columnProperties.put("TABLE_NAME", table.getViewName());
      this.columnProperties.put("COLUMN_NAME", columnItem.getString("columnName"));
      this.columnProperties.put("COLUMN_ID", columnItem.getString("columnId"));
      this.columnProperties.put("TYPE_NAME", columnItem.getString("dataType"));
      this.columnProperties.put("DATA_TYPE", String.valueOf(columnItem.getInt("dataTypeId")));
      this.columnProperties.put("SQL_DATA_TYPE", String.valueOf(ZohoAnalyticsJDBCConstants.ANALYTICS_SQL_DATATYPE_MAP.get(columnItem.getInt("dataTypeId"))));
      this.columnProperties.put("ORDINAL_POSITION", String.valueOf(columnItem.getInt("columnIndex")));
      this.columnProperties.put("REMARKS", columnItem.getString("columnDesc"));
      this.columnProperties.put("COLUMN_SIZE", String.valueOf(columnItem.getInt("columnMaxSize")));
      this.columnProperties.put("IS_NULLABLE", String.valueOf(columnItem.getBoolean("isNullable")));
      this.columnProperties.put("NULLABLE", columnItem.getBoolean("isNullable") ? "1" : "0");
      this.columnProperties.put("COLUMN_DEF", columnItem.getString("defaultValue"));
      this.columnProperties.put("DATE_FORMAT", columnItem.optString("dateFormat", ""));
      this.columnProperties.put("DECIMAL_DIGITS", columnItem.optString("decimalPlaces", "-1"));
      this.columnProperties.put("PKTABLE_CAT", table.get("TABLE_CAT"));
      this.columnProperties.put("PKTABLE_NAME", columnItem.getString("pkTableName"));
      this.columnProperties.put("PKCOLUMN_NAME", columnItem.getString("pkColumnName"));
      this.columnProperties.put("FKTABLE_CAT", table.get("TABLE_CAT"));
      this.columnProperties.put("FKTABLE_NAME", table.getViewName());
      this.columnProperties.put("FKCOLUMN_NAME", columnItem.getString("columnName"));
      this.columnProperties.put("TABLE_SCHEM", "");
      this.columnProperties.put("PKTABLE_SCHEM", "");
      this.columnProperties.put("FKTABLE_SCHEM", "");
      this.columnProperties.put("SCOPE", "2");
      this.columnProperties.put("FK_NAME", "");
      this.columnProperties.put("PK_NAME", "");
      this.columnProperties.put("UPDATE_RULE", "3");
      this.columnProperties.put("DELETE_RULE", "3");
      this.columnProperties.put("DEFERRABILITY", "7");
   }
}
