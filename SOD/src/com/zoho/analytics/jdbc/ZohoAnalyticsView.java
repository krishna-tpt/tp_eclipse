package com.zoho.analytics.jdbc;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class ZohoAnalyticsView implements ZohoAnalyticsData {
   private Map<String, String> viewProperties = new HashMap();
   private Map<String, ZohoAnalyticsColumn> columnsMap = new HashMap();
   private ZohoAnalyticsWorkspace workspace;

   private void addToColumns(ZohoAnalyticsColumn column) {
      this.columnsMap.put(column.getColumnName(), column);
   }

   public String get(String property) {
      return (String)this.viewProperties.get(property);
   }

   int propertiesSize() {
      return this.viewProperties.size();
   }

   String getViewName() {
      return (String)this.viewProperties.get("TABLE_NAME");
   }

   void populateViewMetaData(ZohoAnalyticsWorkspace workspace, JSONObject viewItem) {
      this.workspace = workspace;
      this.viewProperties.put("TABLE_ID", viewItem.getString("viewId"));
      this.viewProperties.put("TABLE_NAME", viewItem.getString("viewName"));
      this.viewProperties.put("TABLE_CAT", this.workspace.getWorkspaceName());
      this.viewProperties.put("REMARKS", viewItem.getString("viewDesc"));
      this.viewProperties.put("TABLE_TYPE", viewItem.getString("viewType").equalsIgnoreCase("table") ? "TABLE" : "VIEW");
      this.viewProperties.put("TYPE_NAME", viewItem.getString("viewType"));
      this.viewProperties.put("ROW_COUNT", String.valueOf(viewItem.getInt("rowCount")));
      JSONArray columns = viewItem.getJSONArray("columns");

      for(int i = 0; i < columns.length(); ++i) {
         JSONObject columnItem = columns.getJSONObject(i);
         ZohoAnalyticsColumn column = new ZohoAnalyticsColumn();
         column.populateColumnMetaData(this, columnItem);
         this.addToColumns(column);
      }

   }

   ZohoAnalyticsColumn getAnalyticsColumn(String columnName) throws SQLException {
      if (!this.columnsMap.containsKey(columnName)) {
         throw new SQLException(MessageFormat.format("Column {0} is not present in the view", columnName), "", 7408);
      } else {
         return (ZohoAnalyticsColumn)this.columnsMap.get(columnName);
      }
   }

   ZohoAnalyticsColumn[] getAnalyticsColumns(String columnNamePattern) throws SQLException {
      columnNamePattern = ZohoAnalyticsJDBCUtil.convertPatternForJava(columnNamePattern == null ? "%" : columnNamePattern);
      ArrayList<ZohoAnalyticsColumn> columns = new ArrayList();
      Iterator var3 = this.columnsMap.keySet().iterator();

      while(var3.hasNext()) {
         String columnName = (String)var3.next();
         if (columnName.toLowerCase().matches(columnNamePattern.toLowerCase())) {
            columns.add(this.columnsMap.get(columnName));
         }
      }

      return (ZohoAnalyticsColumn[])columns.toArray(new ZohoAnalyticsColumn[0]);
   }

   ArrayList<ZohoAnalyticsColumn> getExportedKeys(String pkTableName) {
      ArrayList<ZohoAnalyticsColumn> columns = new ArrayList();
      Iterator var4 = this.columnsMap.values().iterator();

      while(var4.hasNext()) {
         ZohoAnalyticsColumn column = (ZohoAnalyticsColumn)var4.next();
         String curPkName = column.get("PKTABLE_NAME");
         if (curPkName != null && curPkName.equalsIgnoreCase(pkTableName)) {
            columns.add(column);
         }
      }

      return columns;
   }

   ArrayList<String> getColumnNames() {
      return (ArrayList)this.columnsMap.keySet();
   }
}
