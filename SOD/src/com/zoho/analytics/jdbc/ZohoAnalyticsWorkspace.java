package com.zoho.analytics.jdbc;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class ZohoAnalyticsWorkspace implements ZohoAnalyticsData {
   private String workspaceName;
   private String workspaceId;
   private boolean isMetaDataPopulated = false;
   private Map<String, ZohoAnalyticsView> tablesMap = new HashMap();
   private Map<String, String> workspaceProperties = new HashMap();

   ZohoAnalyticsWorkspace(String workspaceName, String workspaceId) {
      this.workspaceName = workspaceName;
      this.workspaceId = workspaceId;
      this.workspaceProperties.put("TABLE_CAT", workspaceName);
   }

   private void addToTables(ZohoAnalyticsView table) {
      this.tablesMap.put(table.getViewName(), table);
   }

   String getWorkspaceName() {
      return this.workspaceName;
   }

   String getWorkspaceId() {
      return this.workspaceId;
   }

   boolean isMetaDataPopulated() {
      return this.isMetaDataPopulated;
   }

   void updateMetaDataStatus(boolean status) {
      this.isMetaDataPopulated = status;
   }

   void populateWorkspaceMetaData(JSONObject response) throws SQLException {
      JSONArray viewArray = response.getJSONArray("views");

      for(int i = 0; i < viewArray.length(); ++i) {
         JSONObject viewItem = viewArray.getJSONObject(i);
         ZohoAnalyticsView view = new ZohoAnalyticsView();
         view.populateViewMetaData(this, viewItem);
         this.addToTables(view);
      }

      this.updateMetaDataStatus(true);
   }

   public String get(String key) {
      return (String)this.workspaceProperties.getOrDefault(key, (String)null);
   }

   ZohoAnalyticsView getAnalyticsView(String tableName) throws SQLException {
      if (!this.tablesMap.containsKey(tableName)) {
         throw new SQLException(MessageFormat.format("View {0} is not present in the workspace", tableName), "", 7104);
      } else {
         return (ZohoAnalyticsView)this.tablesMap.get(tableName);
      }
   }

   ZohoAnalyticsView[] getAnalyticsViews(String tableNamePattern, String[] types) throws SQLException {
      tableNamePattern = ZohoAnalyticsJDBCUtil.convertPatternForJava(tableNamePattern == null ? "%" : tableNamePattern);
      if (types == null || types.length == 0) {
         types = new String[]{"TABLE", "VIEW"};
      }

      ArrayList<ZohoAnalyticsView> views = new ArrayList();
      Iterator var5 = this.tablesMap.keySet().iterator();

      while(true) {
         ZohoAnalyticsView view;
         do {
            String viewName;
            do {
               if (!var5.hasNext()) {
                  return (ZohoAnalyticsView[])views.toArray(new ZohoAnalyticsView[0]);
               }

               viewName = (String)var5.next();
            } while(!viewName.toLowerCase().matches(tableNamePattern.toLowerCase()));

            view = (ZohoAnalyticsView)this.tablesMap.get(viewName);
         } while(types != null && !this.isTableTypeFound(types, view.get("TABLE_TYPE")));

         views.add(view);
      }
   }

   private boolean isTableTypeFound(String[] types, String currentViewType) throws SQLException {
      try {
         String[] var3 = types;
         int var4 = types.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String type = var3[var5];
            if (type.equalsIgnoreCase(currentViewType)) {
               return true;
            }
         }

         return false;
      } catch (Exception var7) {
         throw new SQLException(var7.getMessage());
      }
   }

   long getViewIdFromName(String viewName) throws SQLException {
      ZohoAnalyticsView view = this.getAnalyticsView(viewName);
      return Long.valueOf(view.get("TABLE_ID"));
   }

   long getColumnIdFromName(String viewName, String columnName) throws SQLException {
      ZohoAnalyticsView view = this.getAnalyticsView(viewName);
      ZohoAnalyticsColumn column = view.getAnalyticsColumn(columnName);
      return Long.valueOf(column.get("COLUMN_ID"));
   }

   ArrayList<String> getColumnNamesForView(String viewName) throws SQLException {
      ZohoAnalyticsView view = this.getAnalyticsView(viewName);
      return view.getColumnNames();
   }

   void findAndRemoveViewIfExist(long viewId) {
      ZohoAnalyticsView existingView = (ZohoAnalyticsView)this.tablesMap.values().stream().filter((viewObj) -> {
         return viewObj.get("TABLE_ID").equals(viewId);
      }).findFirst().orElse((ZohoAnalyticsView)null);
      if (existingView != null) {
         this.tablesMap.remove(existingView.getViewName());
      }

   }

   void updateViewDetails(long viewId, JSONObject viewInfo) {
      try {
         this.findAndRemoveViewIfExist(viewId);
         ZohoAnalyticsView view = new ZohoAnalyticsView();
         view.populateViewMetaData(this, viewInfo);
         this.addToTables(view);
      } catch (Exception var5) {
         this.updateMetaDataStatus(false);
      }

   }
}
