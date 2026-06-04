package com.zoho.analytics.jdbc;

import com.zoho.analytics.client.ServerException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;

public class ZohoAnalyticsMetaData {
   private String orgId;
   private String workspaceId;
   private String workspaceName;
   private ZohoAnalyticsAPI api;
   private Map<String, ZohoAnalyticsWorkspace> workspaces = Collections.synchronizedMap(new HashMap());

   ZohoAnalyticsMetaData(String orgId, String workspaceName, ZohoAnalyticsAPI api) throws SQLException {
      this.orgId = orgId;
      this.workspaceName = workspaceName;
      this.api = api;
      this.fetchAndUpdateWorkspaceList(orgId);
      this.workspaceId = this.getAnalyticsWorkspace(workspaceName).getWorkspaceId();
   }

   protected String getOrganisationId() {
      return this.orgId;
   }

   protected String getWorkspaceName() {
      return this.workspaceName;
   }

   protected String getWorkspaceId() {
      return this.workspaceId;
   }

   protected void switchWorkspace(String workspaceName) throws SQLException {
      this.workspaceId = this.getAnalyticsWorkspace(workspaceName).getWorkspaceId();
      this.workspaceName = workspaceName;
   }

   private void fetchAndUpdateWorkspaceList(String orgId) throws SQLException {
      try {
         JSONArray workspacesArr = this.api.fetchWorkspaceList(orgId);

         for(int i = 0; i < workspacesArr.length(); ++i) {
            JSONObject workspaceObj = workspacesArr.getJSONObject(i);
            ZohoAnalyticsWorkspace workspace = new ZohoAnalyticsWorkspace(workspaceObj.getString("workspaceName"), workspaceObj.getString("workspaceId"));
            this.workspaces.put(workspace.getWorkspaceName(), workspace);
         }

      } catch (ServerException var6) {
         throw (SQLException)(new SQLException(var6.getErrorMessage(), (String)null, var6.getErrorCode())).initCause(var6);
      } catch (Exception var7) {
         throw (SQLException)(new SQLException(var7.getMessage())).initCause(var7);
      }
   }

   ZohoAnalyticsWorkspace getCurrentAnalyticsWorkspace() throws SQLException {
      return this.getAnalyticsWorkspace(this.workspaceName);
   }

   ZohoAnalyticsWorkspace getAnalyticsWorkspace(String workspaceName) throws SQLException {
      if (!this.workspaces.containsKey(workspaceName)) {
         throw new SQLException(MessageFormat.format("Workspace {0} is not present in the organization", workspaceName), "", 7103);
      } else {
         ZohoAnalyticsWorkspace workspace = (ZohoAnalyticsWorkspace)this.workspaces.get(workspaceName);
         if (!workspace.isMetaDataPopulated()) {
            workspace.populateWorkspaceMetaData(this.api.fetchWorkspaceMetaData(this.orgId, workspace.getWorkspaceId()));
         }

         return workspace;
      }
   }

   ResultSet getCatalogs() throws SQLException {
      ZohoAnalyticsWorkspace[] workspaceArr = (ZohoAnalyticsWorkspace[])this.workspaces.values().toArray(new ZohoAnalyticsWorkspace[this.workspaces.size()]);
      return ZohoAnalyticsJDBCUtil.getZohoResultSet(workspaceArr, new String[]{"TABLE_CAT"}, new String[0], (String)null);
   }

   ResultSet getBestRowIdentifier(String catalogName, String tableName) throws SQLException {
      ZohoAnalyticsView view = this.getAnalyticsWorkspace(catalogName).getAnalyticsView(tableName);
      ZohoAnalyticsColumn[] columns = view.getAnalyticsColumns("%");
      ArrayList<ZohoAnalyticsColumn> bestRowIdentifiers = new ArrayList();
      ZohoAnalyticsColumn[] var6 = columns;
      int var7 = columns.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         ZohoAnalyticsColumn column = var6[var8];
         if (column.get("TYPE_NAME").equalsIgnoreCase("AUTO_NUMBER")) {
            bestRowIdentifiers.add(column);
         }
      }

      return ZohoAnalyticsJDBCUtil.getZohoResultSet((ZohoAnalyticsData[])bestRowIdentifiers.toArray(new ZohoAnalyticsColumn[0]), ZohoAnalyticsJDBCConstants.BEST_ROW_PROPS, new String[0], catalogName);
   }

   ResultSet getColumns(String catalogName, String tableNamePattern, String columnNamePattern) throws SQLException {
      ZohoAnalyticsView[] views = this.getAnalyticsWorkspace(catalogName).getAnalyticsViews(tableNamePattern, (String[])null);
      ArrayList<ZohoAnalyticsColumn> columnList = new ArrayList();
      ZohoAnalyticsView[] var6 = views;
      int var7 = views.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         ZohoAnalyticsView view = var6[var8];
         ZohoAnalyticsColumn[] columns = view.getAnalyticsColumns(columnNamePattern);
         columnList.addAll((Collection)Arrays.stream(columns).collect(Collectors.toList()));
      }

      return ZohoAnalyticsJDBCUtil.getZohoResultSet((ZohoAnalyticsData[])columnList.toArray(new ZohoAnalyticsColumn[0]), ZohoAnalyticsJDBCConstants.COLUMN_PROPS, new String[0], catalogName);
   }

   ResultSet getCrossReference(String primaryCatalogName, String primaryTableName, String foreignCatalogName, String foreignTableName) throws SQLException {
      ZohoAnalyticsWorkspace primaryCatalog = this.getAnalyticsWorkspace(primaryCatalogName);
      ZohoAnalyticsView primaryTable = primaryCatalog.getAnalyticsView(primaryTableName);
      ZohoAnalyticsWorkspace foreignCatalog = this.getAnalyticsWorkspace(foreignCatalogName);
      ZohoAnalyticsView foreignTable = foreignCatalog.getAnalyticsView(foreignTableName);
      ArrayList<ZohoAnalyticsColumn> exportedColumns = foreignTable.getExportedKeys(primaryTable.getViewName());
      return ZohoAnalyticsJDBCUtil.getZohoResultSet((ZohoAnalyticsData[])exportedColumns.toArray(new ZohoAnalyticsColumn[0]), ZohoAnalyticsJDBCConstants.REFERENCE_KEYS_PROPS, new String[0], primaryCatalogName);
   }

   ResultSet getExportedKeys(String catalogName, String tableName) throws SQLException {
      ZohoAnalyticsWorkspace workspace = this.getAnalyticsWorkspace(catalogName);
      ZohoAnalyticsView pkTable = workspace.getAnalyticsView(tableName);
      ArrayList<ZohoAnalyticsColumn> exportedColumns = new ArrayList();
      ZohoAnalyticsView[] var6 = workspace.getAnalyticsViews("%", (String[])null);
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         ZohoAnalyticsView table = var6[var8];
         if (!table.equals(pkTable)) {
            exportedColumns.addAll(table.getExportedKeys(pkTable.getViewName()));
         }
      }

      return ZohoAnalyticsJDBCUtil.getZohoResultSet((ZohoAnalyticsData[])exportedColumns.toArray(new ZohoAnalyticsColumn[0]), ZohoAnalyticsJDBCConstants.REFERENCE_KEYS_PROPS, new String[0], catalogName);
   }

   ResultSet getImportedKeys(String catalogName, String tableName) throws SQLException {
      ZohoAnalyticsView view = this.getAnalyticsWorkspace(catalogName).getAnalyticsView(tableName);
      ArrayList<ZohoAnalyticsColumn> importedColumns = new ArrayList();
      ZohoAnalyticsColumn[] columns = view.getAnalyticsColumns("%");
      ZohoAnalyticsColumn[] var6 = columns;
      int var7 = columns.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         ZohoAnalyticsColumn column = var6[var8];
         String pkTableName = column.get("PKTABLE_NAME");
         if (pkTableName != null && !pkTableName.isEmpty()) {
            importedColumns.add(column);
         }
      }

      return ZohoAnalyticsJDBCUtil.getZohoResultSet((ZohoAnalyticsData[])importedColumns.toArray(new ZohoAnalyticsColumn[0]), ZohoAnalyticsJDBCConstants.REFERENCE_KEYS_PROPS, new String[0], catalogName);
   }

   ResultSet getPrimaryKeys(String catalogName, String tableName) throws SQLException {
      return ZohoAnalyticsJDBCUtil.getZohoResultSet(new ZohoAnalyticsConstants[0], ZohoAnalyticsJDBCConstants.COLUMN_PROPS, new String[0], catalogName);
   }

   ResultSet getTables(String catalogName, String tableNamePattern, String[] types) throws SQLException {
      ZohoAnalyticsView[] views = this.getAnalyticsWorkspace(catalogName).getAnalyticsViews(tableNamePattern, types);
      return ZohoAnalyticsJDBCUtil.getZohoResultSet(views, ZohoAnalyticsJDBCConstants.VIEW_PROPS, new String[0], catalogName);
   }

   ResultSet getTableTypes() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getZohoResultSet(ZohoAnalyticsJDBCConstants.TABLE_TYPES, new String[]{"TABLE_TYPE"}, (String[])null, (String)null);
   }

   ResultSet getTypeInfo() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getZohoResultSet(ZohoAnalyticsJDBCConstants.ZDB_SUBTYPES, ZohoAnalyticsJDBCConstants.DATA_TYPE_PROPS, (String[])null, (String)null);
   }
}
