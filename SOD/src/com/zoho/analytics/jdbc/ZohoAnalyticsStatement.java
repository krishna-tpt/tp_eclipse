package com.zoho.analytics.jdbc;

import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.alter.AlterStatement;
import com.adventnet.swissqlapi.sql.statement.alter.AlterTable;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.CreateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.delete.DeleteQueryStatement;
import com.adventnet.swissqlapi.sql.statement.delete.TruncateStatement;
import com.adventnet.swissqlapi.sql.statement.drop.DropStatement;
import com.adventnet.swissqlapi.sql.statement.insert.InsertQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.update.UpdateQueryStatement;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;
import com.zoho.analytics.client.ServerException;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import org.json.JSONObject;

public class ZohoAnalyticsStatement extends ZohoAnalyticsWrapper implements Statement, ZohoAnalyticsJDBCConstants {
   private ZohoAnalyticsAPI api;
   protected boolean isClosed = false;
   protected ResultSet currResultSet = null;
   protected Connection con;
   protected int maxRows = 0;
   protected int affectedRowCount = -1;
   protected int fetchSize = -1;
   protected int maxFieldSize = -1;
   protected boolean escapeProcessing = true;
   protected int timeout = 3;
   protected String cursorName = "";
   private static final int DEFAULTLIMIT = 1000;
   private static final int MAXBUFFERSIZE = 65535;
   private ArrayList<String[]> rowData = new ArrayList();
   private long organisationId;
   private long workspaceId;
   private String workspaceName;

   ZohoAnalyticsStatement(Connection con) {
      this.con = con;
      this.api = ((ZohoAnalyticsConnection)con).getAnalyticsAPI();
      ZohoAnalyticsMetaData analyticsMetaData = ((ZohoAnalyticsConnection)con).getAnalyticsMetaData();
      this.organisationId = Long.valueOf(analyticsMetaData.getOrganisationId());
      this.workspaceId = Long.valueOf(analyticsMetaData.getWorkspaceId());
      this.workspaceName = analyticsMetaData.getWorkspaceName();
      ((ZohoAnalyticsConnection)con).registerStatement(this);
   }

   ZohoAnalyticsWorkspace getAnalyticsWorkspace() throws SQLException {
      ZohoAnalyticsMetaData analyticsMetaData = ((ZohoAnalyticsConnection)this.con).getAnalyticsMetaData();
      return analyticsMetaData.getAnalyticsWorkspace(this.workspaceName);
   }

   public void addBatch(String sql) throws SQLException {
      throw new NotImplementedException();
   }

   public void cancel() throws SQLException {
      throw new NotImplementedException();
   }

   public void clearBatch() throws SQLException {
   }

   public void clearWarnings() throws SQLException {
   }

   public void close() throws SQLException {
      this.close(true);
   }

   void close(boolean alsoUnregister) throws SQLException {
      if (!this.isClosed) {
         if (alsoUnregister) {
            ((ZohoAnalyticsConnection)this.con).unRegisterStatement(this);
         }

         if (this.currResultSet != null) {
            this.currResultSet.close();
         }

         this.isClosed = true;
      }
   }

   public boolean execute(String sql) throws SQLException {
      try {
         if (ZohoAnalyticsQueryParserUtil.parseSQLQuery(sql) instanceof SelectQueryStatement) {
            this.executeQuery(sql);
            return true;
         } else {
            this.executeUpdate(sql);
            return false;
         }
      } catch (SQLException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new SQLException(var4.getMessage());
      }
   }

   public boolean execute(String sql, String[] columnNames) throws SQLException {
      throw new NotImplementedException();
   }

   public boolean execute(String sql, int[] columnIndices) throws SQLException {
      throw new NotImplementedException();
   }

   public boolean execute(String sql, int columnIndex) throws SQLException {
      throw new NotImplementedException();
   }

   public int[] executeBatch() throws SQLException {
      throw new NotImplementedException();
   }

   public ResultSet executeQuery(String sql) throws SQLException {
      this.checkClosed();
      if (this.currResultSet != null) {
         this.currResultSet.close();
      }

      sql = ZohoAnalyticsJDBCUtil.validateAndTrimSQL(sql);
      ZohoAnalyticsDataParser data = new ZohoAnalyticsDataParser();
      ByteArrayOutputStream bos = null;

      try {
         bos = new ByteArrayOutputStream();
         this.api.fetchAnalyticsData(this.organisationId, this.workspaceId, sql, bos);
         data.parseCSV(this.rowData, bos);
      } catch (ServerException var13) {
         throw (SQLException)(new SQLException(var13.getErrorMessage(), (String)null, var13.getErrorCode())).initCause(var13);
      } catch (Exception var14) {
         throw (SQLException)(new SQLException(var14.getMessage())).initCause(var14);
      } finally {
         if (bos != null) {
            try {
               bos.close();
            } catch (Exception var12) {
               throw (SQLException)(new SQLException(var12.getMessage())).initCause(var12);
            }
         }

      }

      ZohoAnalyticsResultSetMetaData rsmd = new ZohoAnalyticsResultSetMetaData(data.colNameArr, data.sqlTypeArr, data.tblNameArr, data.subTypeArr, data.subTypeIdArr, this.workspaceName);
      ZohoAnalyticsResultSet rs = new ZohoAnalyticsResultSet(this, this.rowData, data.colNameArr, data.sqlTypeArr, data.tblNameArr, rsmd);
      this.currResultSet = rs;
      return rs;
   }

   public int executeUpdate(String sql) throws SQLException {
      this.checkClosed();
      sql = ZohoAnalyticsJDBCUtil.validateAndTrimSQL(sql);
      int queryType = this.determineQueryType(sql);

      try {
         ZohoAnalyticsWorkspace workspace = ((ZohoAnalyticsConnection)this.con).getAnalyticsMetaData().getAnalyticsWorkspace(this.workspaceName);
         JSONObject response = this.processQuery(workspace, sql, queryType);
         int affectedRows = response.getInt("affectedRows");
         this.checkAndUpdateAnalyticsMetadata(workspace, queryType, response.optLong("viewId", -1L));
         return affectedRows;
      } catch (ServerException var6) {
         throw new SQLException(var6.getErrorMessage(), (String)null, var6.getErrorCode(), var6);
      } catch (Exception var7) {
         throw new SQLException(var7.getMessage(), var7);
      }
   }

   private int determineQueryType(String sql) throws SQLException {
      try {
         SwisSQLStatement parsedStatement = ZohoAnalyticsQueryParserUtil.parseSQLQuery(sql);
         if (parsedStatement instanceof InsertQueryStatement) {
            return 1;
         }

         if (parsedStatement instanceof UpdateQueryStatement) {
            return 2;
         }

         if (parsedStatement instanceof DeleteQueryStatement || parsedStatement instanceof TruncateStatement) {
            return 3;
         }

         if (parsedStatement instanceof CreateQueryStatement) {
            CreateQueryStatement createStmt = (CreateQueryStatement)parsedStatement;
            return createStmt.getTableOrView().equalsIgnoreCase("table") ? 4 : 5;
         }

         if (parsedStatement instanceof AlterStatement) {
            return 6;
         }

         if (parsedStatement instanceof DropStatement) {
            return 7;
         }
      } catch (Exception var4) {
         throw new SQLException("Error parsing query type: " + var4.getMessage(), var4);
      }

      throw new SQLException("Unsupported SQL statement type");
   }

   private JSONObject processQuery(ZohoAnalyticsWorkspace workspace, String sql, int queryType) throws Exception {
      SwisSQLStatement parsedStatement = ZohoAnalyticsQueryParserUtil.parseSQLQuery(sql);
      switch(queryType) {
      case 1:
         return this.processInsert(workspace, sql);
      case 2:
         return this.processUpdate(workspace, (UpdateQueryStatement)parsedStatement);
      case 3:
         return this.processDelete(workspace, sql, parsedStatement);
      case 4:
         return this.processCreateTable(sql);
      case 5:
         return this.processCreateView(sql);
      case 6:
         return this.processAlterTable(workspace, sql);
      case 7:
         return this.processDrop(workspace, sql);
      default:
         throw new SQLException("Unsupported query type");
      }
   }

   private JSONObject processInsert(ZohoAnalyticsWorkspace workspace, String sql) throws Exception {
      JSONObject columnValues = ZohoAnalyticsJDBCUtil.parseInsertQuery(workspace, sql);
      long viewId = columnValues.getLong("viewId");
      this.api.addRows(this.organisationId, this.workspaceId, viewId, columnValues.getJSONObject("columnData"));
      return (new JSONObject()).put("viewId", viewId).put("affectedRows", 1);
   }

   private JSONObject processUpdate(ZohoAnalyticsWorkspace workspace, UpdateQueryStatement statement) throws Exception {
      JSONObject columnValues = ZohoAnalyticsJDBCUtil.parseUpdateStatementCols(statement);
      JSONObject columnData = columnValues.getJSONObject("columnData");
      String tableName = columnValues.getString("tableName");
      String criteria = ZohoAnalyticsQueryParserUtil.parseWhereExpressions(statement.getWhereExpression(), tableName);
      long viewId = workspace.getViewIdFromName(tableName);
      JSONObject config = new JSONObject();
      if (criteria == null) {
         config.put("updateAllRows", true);
      }

      return (new JSONObject()).put("viewId", viewId).put("affectedRows", this.api.updateRows(this.organisationId, this.workspaceId, viewId, columnData, criteria, config));
   }

   private JSONObject processDelete(ZohoAnalyticsWorkspace workspace, String sql, SwisSQLStatement parsedStatement) throws Exception {
      String criteria = null;
      long viewId;
      if (parsedStatement instanceof DeleteQueryStatement) {
         DeleteQueryStatement deleteStmt = (DeleteQueryStatement)parsedStatement;
         String tableName = GeneralUtil.trimIfAliasNameIsEnclosed(deleteStmt.getTableExpression().toString().trim());
         WhereExpression whereExp = deleteStmt.getWhereExpression();
         whereExp.removeBrace();
         criteria = ZohoAnalyticsQueryParserUtil.parseWhereExpressions(whereExp, tableName);
         viewId = workspace.getViewIdFromName(tableName);
      } else {
         if (!(parsedStatement instanceof TruncateStatement)) {
            throw new SQLException("Invalid DELETE or TRUNCATE query.");
         }

         TruncateStatement truncateStmt = (TruncateStatement)parsedStatement;
         viewId = workspace.getViewIdFromName(GeneralUtil.trimIfAliasNameIsEnclosed(truncateStmt.getTableObject().toString().trim()));
      }

      JSONObject config = new JSONObject();
      if (criteria == null) {
         config.put("deleteAllRows", true);
      }

      return (new JSONObject()).put("viewId", viewId).put("affectedRows", this.api.deleteRows(this.organisationId, this.workspaceId, viewId, criteria, config));
   }

   private JSONObject processCreateTable(String sql) throws Exception {
      JSONObject tableDetails = ZohoAnalyticsJDBCUtil.parseCreateQuery(sql);
      return (new JSONObject()).put("viewId", this.api.createTable(this.organisationId, this.workspaceId, tableDetails)).put("affectedRows", 0);
   }

   private JSONObject processCreateView(String sql) throws Exception {
      JSONObject config = ZohoAnalyticsJDBCUtil.parseCreateView(sql);
      long viewId = this.api.createQueryTable(this.organisationId, this.workspaceId, config.getString("sqlQuery"), config.getString("queryTableName"));
      return (new JSONObject()).put("viewId", viewId).put("affectedRows", 0);
   }

   private JSONObject processAlterTable(ZohoAnalyticsWorkspace workspace, String sql) throws Exception {
      JSONObject alterStObject = ZohoAnalyticsQueryParserUtil.parseAlterStatementVector(workspace, sql);
      AlterTable alterVector = (AlterTable)alterStObject.get("alterStVector");
      long viewId = -1L;
      if (alterVector.getAddClause() != null) {
         CreateColumn columnDetails = (CreateColumn)alterVector.getAddClause().getCreateColumnVector().firstElement();
         JSONObject configObj;
         if (columnDetails.getColumnName() != null && columnDetails.getConstraintClause() == null) {
            configObj = ZohoAnalyticsJDBCUtil.parseAlterAddColumnQuery(workspace, sql);
            viewId = configObj.getLong("viewId");
            this.api.addColumn(this.organisationId, this.workspaceId, viewId, configObj.getString("columnName"), configObj.getString("dataType"), (new JSONObject()).put("geoRole", configObj.optString("geoRole", (String)null)));
         } else if (columnDetails.getConstraintClause() != null) {
            configObj = ZohoAnalyticsJDBCUtil.parseAlterStatementAddForeignKey(workspace, sql);
            viewId = configObj.getLong("viewId");
            this.api.addLookup(this.organisationId, this.workspaceId, viewId, configObj.getLong("columnId"), configObj.getLong("referenceViewId"), configObj.getLong("referenceColumnId"));
         }
      } else {
         JSONObject configObj;
         if ("RENAME".equalsIgnoreCase(alterVector.getRename())) {
            if (alterVector.getRenameColumn() == null) {
               configObj = ZohoAnalyticsJDBCUtil.parseAlterStatementRenameTableQuery(workspace, sql);
               viewId = configObj.getLong("viewId");
               this.api.renameView(this.organisationId, this.workspaceId, viewId, configObj.getString("tableName"));
            } else if ("COLUMN".equalsIgnoreCase(alterVector.getRenameColumn())) {
               configObj = ZohoAnalyticsJDBCUtil.parseAlterStatementRenameColumn(workspace, sql);
               viewId = configObj.getLong("viewId");
               this.api.renameColumn(this.organisationId, this.workspaceId, viewId, configObj.getLong("columnId"), configObj.getString("columnName"));
            }
         } else if (alterVector.getDropClause() != null) {
            configObj = ZohoAnalyticsJDBCUtil.parseAlterStatementDropColumn(workspace, sql);
            viewId = configObj.getLong("viewId");
            this.api.deleteColumn(this.organisationId, this.workspaceId, viewId, configObj.getLong("columnId"));
         }
      }

      return (new JSONObject()).put("viewId", viewId).put("affectedRows", 1);
   }

   private JSONObject processDrop(ZohoAnalyticsWorkspace workspace, String sql) throws Exception {
      long viewId = workspace.getViewIdFromName(ZohoAnalyticsJDBCUtil.parseDropStatement(sql));
      this.api.deleteView(this.organisationId, this.workspaceId, viewId);
      return (new JSONObject()).put("viewId", viewId).put("affectedRows", 1);
   }

   private void checkAndUpdateAnalyticsMetadata(ZohoAnalyticsWorkspace workspace, int type, long viewId) throws SQLException {
      switch(type) {
      case 4:
      case 5:
      case 6:
         workspace.updateViewDetails(viewId, this.api.fetchViewMetaData(viewId));
         break;
      case 7:
         workspace.findAndRemoveViewIfExist(viewId);
      }

   }

   public int executeUpdate(String sql, String[] columnNames) throws SQLException {
      throw new NotImplementedException();
   }

   public int executeUpdate(String sql, int columnIndex) throws SQLException {
      throw new NotImplementedException();
   }

   public int executeUpdate(String sql, int[] columnIndices) throws SQLException {
      throw new NotImplementedException();
   }

   public Connection getConnection() throws SQLException {
      return this.con;
   }

   public int getFetchDirection() throws SQLException {
      return 1000;
   }

   public int getFetchSize() throws SQLException {
      return this.fetchSize == -1 ? 1000 : this.fetchSize;
   }

   public ResultSet getGeneratedKeys() throws SQLException {
      throw new NotImplementedException();
   }

   public int getMaxFieldSize() throws SQLException {
      return this.maxFieldSize == -1 ? '\uffff' : this.maxFieldSize;
   }

   public int getMaxRows() throws SQLException {
      return this.maxRows;
   }

   public boolean getMoreResults() throws SQLException {
      return this.getMoreResults(1);
   }

   public boolean getMoreResults(int current) throws SQLException {
      return this.currResultSet != null;
   }

   public int getQueryTimeout() throws SQLException {
      return this.timeout;
   }

   public ResultSet getResultSet() throws SQLException {
      return this.currResultSet;
   }

   public int getResultSetConcurrency() throws SQLException {
      return 1007;
   }

   public int getResultSetHoldability() throws SQLException {
      return 1;
   }

   public int getResultSetType() throws SQLException {
      return 1004;
   }

   public int getUpdateCount() throws SQLException {
      return this.affectedRowCount;
   }

   public long getLargeUpdateCount() throws SQLException {
      return (long)this.affectedRowCount;
   }

   public SQLWarning getWarnings() throws SQLException {
      return null;
   }

   public void setCursorName(String cursor) throws SQLException {
      this.cursorName = cursor;
   }

   public void setEscapeProcessing(boolean escape) throws SQLException {
      this.escapeProcessing = escape;
   }

   public void setFetchDirection(int direction) throws SQLException {
      switch(direction) {
      case 1000:
      case 1001:
      case 1002:
         return;
      default:
         throw new SQLException("Illegal argument");
      }
   }

   public void setFetchSize(int size) throws SQLException {
      this.fetchSize = size;
   }

   public void setMaxFieldSize(int maxsize) throws SQLException {
      this.maxFieldSize = maxsize;
   }

   public void setMaxRows(int rows) throws SQLException {
      this.maxRows = rows;
   }

   public void setQueryTimeout(int timeout) throws SQLException {
      if (timeout < 0) {
         new SQLException("Illegal timeout value" + timeout);
      }

      this.timeout = timeout;
   }

   private void checkClosed() throws SQLException {
      if (this.isClosed) {
         throw new SQLException("No operation allowed after statement is closed");
      }
   }

   public boolean isPoolable() throws SQLException {
      throw new NotImplementedException();
   }

   public void setPoolable(boolean poolable) throws SQLException {
      throw new NotImplementedException();
   }

   public boolean isClosed() throws SQLException {
      throw new NotImplementedException();
   }

   public void closeOnCompletion() throws SQLException {
      throw new NotImplementedException();
   }

   public boolean isCloseOnCompletion() throws SQLException {
      throw new NotImplementedException();
   }
}
