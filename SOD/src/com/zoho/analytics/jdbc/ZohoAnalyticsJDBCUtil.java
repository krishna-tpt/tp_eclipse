package com.zoho.analytics.jdbc;

import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.alter.AddClause;
import com.adventnet.swissqlapi.sql.statement.alter.AlterTable;
import com.adventnet.swissqlapi.sql.statement.alter.DropClause;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.CreateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.create.ForeignConstraintClause;
import com.adventnet.swissqlapi.sql.statement.drop.DropStatement;
import com.adventnet.swissqlapi.sql.statement.insert.InsertClause;
import com.adventnet.swissqlapi.sql.statement.insert.InsertQueryStatement;
import com.adventnet.swissqlapi.sql.statement.insert.ValuesClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.update.SetClause;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.update.UpdateQueryStatement;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

class ZohoAnalyticsJDBCUtil {
   static ZohoAnalyticsResultSet getZohoResultSet(ZohoAnalyticsData[] data, String[] colNames, String[] tableNames, String catalog) {
      ArrayList rowData = new ArrayList();
      int noCols = colNames.length;
      int[] dataTypes = new int[noCols];
      int[] subTypeIdArr = new int[noCols];
      String[] subTypeArr = new String[noCols];

      int i;
      for(i = 0; i < noCols; ++i) {
         String curColName = colNames[i];
         dataTypes[i] = ZohoAnalyticsMetaField.getType(curColName);
         subTypeIdArr[i] = ZohoAnalyticsMetaField.getSubTypeId(curColName);
         subTypeArr[i] = ZohoAnalyticsMetaField.getSubTypeName(curColName);
      }

      for(i = 0; i < data.length; ++i) {
         String[] curRow = new String[noCols];

         for(int k = 0; k < noCols; ++k) {
            curRow[k] = data[i].get(colNames[k]);
         }

         rowData.add(curRow);
      }

      return new ZohoAnalyticsResultSet((Statement)null, rowData, colNames, dataTypes, tableNames, new ZohoAnalyticsResultSetMetaData(colNames, dataTypes, tableNames, subTypeArr, subTypeIdArr, catalog));
   }

   static String convertPatternForJava(String likePattern) {
      StringBuilder sb = new StringBuilder(likePattern.length() + likePattern.length() / 2);
      char prevChar = 0;
      char SLASH = '\\';
      char[] likePatternChars = likePattern.toCharArray();
      char[] var5 = likePatternChars;
      int var6 = likePatternChars.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         char curChar = var5[var7];
         if (curChar != '%' && curChar != '_') {
            if (curChar != '*' && curChar != '.') {
               if (prevChar == SLASH && curChar != SLASH) {
                  sb.append(SLASH).append(SLASH).append(curChar);
               } else if (curChar != SLASH) {
                  sb.append(curChar);
               }
            } else {
               if (prevChar == SLASH) {
                  sb.append(SLASH).append(SLASH);
               }

               sb.append(SLASH).append(SLASH).append(curChar);
            }
         } else if (prevChar == SLASH) {
            sb.append(curChar);
         } else {
            sb.append(curChar == '%' ? ".*" : '.');
         }

         prevChar = curChar;
      }

      return sb.toString();
   }

   static JSONObject parseInsertQuery(ZohoAnalyticsWorkspace workspace, String sql) throws Exception {
      SwisSQLStatement parsedQuery = ZohoAnalyticsQueryParserUtil.parseSQLQuery(sql);
      InsertQueryStatement inst = (InsertQueryStatement)parsedQuery;
      String tableName = GeneralUtil.trimIfAliasNameIsEnclosed(inst.getInsertClause().getTableExpression().toString().trim());
      InsertClause insertClause = inst.getInsertClause();
      ArrayList<String> columnList = new ArrayList();
      ValuesClause valuesClause = inst.getValuesClause();
      JSONObject columnData = new JSONObject();
      JSONObject columnValues = new JSONObject();
      int columnInd;
      if (insertClause.getColumnList() == null) {
         columnList = workspace.getColumnNamesForView(tableName);
      } else {
         for(columnInd = 1; columnInd < insertClause.getColumnList().size(); columnInd += 2) {
            columnList.add(GeneralUtil.trimIfAliasNameIsEnclosed((String)insertClause.getColumnList().get(columnInd)));
         }
      }

      columnInd = 0;

      for(int i = 0; i < valuesClause.getValuesList().size(); ++i) {
         Object valToBeChecked = valuesClause.getValuesList().get(i);
         if (valToBeChecked instanceof SelectColumn) {
            if (columnInd >= columnList.size()) {
               throw new IllegalArgumentException("The provided values exceed the number of available columns.");
            }

            columnData.put((String)columnList.get(columnInd), GeneralUtil.trimIfAliasNameIsEnclosed(valToBeChecked.toString()));
            ++columnInd;
         }
      }

      if (columnInd < columnList.size()) {
         throw new IllegalArgumentException("The number of columns provided exceeds the number of values available.");
      } else {
         columnValues.put("viewId", workspace.getViewIdFromName(tableName));
         columnValues.put("columnData", columnData);
         return columnValues;
      }
   }

   static JSONObject parseUpdateStatementCols(UpdateQueryStatement upd) throws Exception {
      SetClause setClause = upd.getSetClause();
      int setClauseSize = setClause.getExpression().size();
      ArrayList<String> columnNameList = new ArrayList();
      ArrayList<String> columnValueList = new ArrayList();

      for(int i = 0; i < setClauseSize; ++i) {
         if (setClause.getExpression().get(i) instanceof TableColumn) {
            TableColumn colName = (TableColumn)setClause.getExpression().get(i);
            columnNameList.add(GeneralUtil.trimIfAliasNameIsEnclosed(colName.toString().trim()));
         } else if (setClause.getExpression().get(i) instanceof SelectColumn) {
            SelectColumn columnValue = (SelectColumn)setClause.getExpression().get(i);
            columnValueList.add(GeneralUtil.trimIfAliasNameIsEnclosed(columnValue.toString()));
         }
      }

      JSONObject columnValues = new JSONObject();
      JSONObject columnData = new JSONObject();
      int columnNameListSize = columnNameList.size();

      for(int i = 0; i < columnNameListSize; ++i) {
         columnData.put(((String)columnNameList.get(i)).trim(), ((String)columnValueList.get(i)).trim());
      }

      columnValues.put("tableName", GeneralUtil.trimIfAliasNameIsEnclosed(upd.getTableExpression().toString().trim()));
      columnValues.put("columnData", columnData);
      return columnValues;
   }

   static JSONObject parseCreateQuery(String sql) throws SQLException, Exception {
      ArrayList<String> columnNames = new ArrayList();
      Vector<CreateColumn> foreignKeyClauseObjects = new Vector();
      LinkedHashMap<String, JSONObject> columnNameAndJSONObjectMap = new LinkedHashMap();
      SwisSQLStatement swisSt = ZohoAnalyticsQueryParserUtil.parseSQLQuery(sql);
      CreateQueryStatement cre = (CreateQueryStatement)swisSt;
      Vector<CreateColumn> columns = new Vector(cre.getColumnNames());
      Iterator var7 = columns.iterator();

      CreateColumn fkClauseItem;
      JSONObject tableDetails;
      String referenceTableName;
      while(var7.hasNext()) {
         fkClauseItem = (CreateColumn)var7.next();
         if (fkClauseItem.getColumnName() != null) {
            tableDetails = ZohoAnalyticsQueryParserUtil.constructColumnItem(fkClauseItem, columnNames);
            Vector constraintVector = fkClauseItem.getConstraintClause();
            if (constraintVector != null) {
               ConstraintClause clauseObject = (ConstraintClause)constraintVector.get(0);
               ForeignConstraintClause fkClauseObject = (ForeignConstraintClause)clauseObject.getConstraintType();
               String referenceTableName = fkClauseObject.getTableName().getTableName();
               referenceTableName = (String)fkClauseObject.getReferenceTableColumnNames().get(0);
               JSONObject lookupColumn = new JSONObject();
               lookupColumn.put("TABLENAME", GeneralUtil.trimIfAliasNameIsEnclosed(referenceTableName).trim());
               lookupColumn.put("COLUMNNAME", GeneralUtil.trimIfAliasNameIsEnclosed(referenceTableName).trim());
               tableDetails.put("LOOKUPCOLUMN", lookupColumn);
            }

            columnNameAndJSONObjectMap.put(tableDetails.getString("COLUMNNAME"), tableDetails);
         } else {
            foreignKeyClauseObjects.add(fkClauseItem);
         }
      }

      var7 = foreignKeyClauseObjects.iterator();

      while(var7.hasNext()) {
         fkClauseItem = (CreateColumn)var7.next();
         Vector constraintVector = fkClauseItem.getConstraintClause();
         ConstraintClause clauseObject = (ConstraintClause)constraintVector.get(0);
         ForeignConstraintClause fkClauseObject = (ForeignConstraintClause)clauseObject.getConstraintType();
         String constraintColumnName = (String)fkClauseObject.getConstraintColumnNames().get(0);
         if (!columnNames.contains(constraintColumnName)) {
            throw new SQLException("Key column '" + constraintColumnName + "' doesn't exist in table");
         }

         JSONObject columnItem = (JSONObject)columnNameAndJSONObjectMap.get(constraintColumnName);
         referenceTableName = fkClauseObject.getTableName().getTableName();
         String referenceTableColumnName = (String)fkClauseObject.getReferenceTableColumnNames().get(0);
         JSONObject lookupColumn = new JSONObject();
         lookupColumn.put("TABLENAME", GeneralUtil.trimIfAliasNameIsEnclosed(referenceTableName).trim());
         lookupColumn.put("COLUMNNAME", GeneralUtil.trimIfAliasNameIsEnclosed(referenceTableColumnName).trim());
         columnItem.put("LOOKUPCOLUMN", lookupColumn);
         columnNameAndJSONObjectMap.put(constraintColumnName, columnItem);
      }

      JSONArray columnsArray = new JSONArray();
      Iterator var18 = columnNameAndJSONObjectMap.entrySet().iterator();

      while(var18.hasNext()) {
         Entry<String, JSONObject> columnObject = (Entry)var18.next();
         JSONObject value = (JSONObject)columnObject.getValue();
         columnsArray.put(value);
      }

      TableObject tableObj = cre.getTableObject();
      tableDetails = new JSONObject();
      tableDetails.put("TABLENAME", GeneralUtil.trimIfAliasNameIsEnclosed(tableObj.getTableName()).trim());
      tableDetails.put("COLUMNS", columnsArray);
      return tableDetails;
   }

   static JSONObject parseCreateView(String sql) throws Exception {
      SwisSQLStatement parsedQuery = ZohoAnalyticsQueryParserUtil.parseSQLQuery(sql);
      CreateQueryStatement crt = (CreateQueryStatement)parsedQuery;
      JSONObject config = new JSONObject();
      String sqlQuery = null;
      if (crt.getSelectQueryStatement() != null) {
         sqlQuery = crt.getSelectQueryStatement().toString();
      } else if (crt.getWithStatement() != null) {
         sqlQuery = crt.getWithStatement().toString();
      }

      config.put("sqlQuery", sqlQuery);
      config.put("queryTableName", GeneralUtil.trimIfAliasNameIsEnclosed(crt.getTableObject().toString()).trim());
      return config;
   }

   static String parseDropStatement(String sql) throws Exception {
      DropStatement drp = (DropStatement)((DropStatement)ZohoAnalyticsQueryParserUtil.parseSQLQuery(sql));
      return GeneralUtil.trimIfAliasNameIsEnclosed(drp.getTableObjectVector().firstElement().toString()).trim();
   }

   static JSONObject parseAlterAddColumnQuery(ZohoAnalyticsWorkspace workspace, String sql) throws Exception {
      JSONObject alterTableDetails = new JSONObject();
      JSONObject alterStVecObj = ZohoAnalyticsQueryParserUtil.parseAlterStatementVector(workspace, sql);
      AlterTable alterStVector = (AlterTable)alterStVecObj.get("alterStVector");
      AddClause addClauseObject = alterStVector.getAddClause();
      CreateColumn columnDetails = (CreateColumn)addClauseObject.getCreateColumnVector().get(0);
      String userDefinedDataType = columnDetails.getUserDefinedDatatype();
      if (userDefinedDataType == null || userDefinedDataType.trim().isEmpty()) {
         userDefinedDataType = columnDetails.getDatatype().toString().trim();
      }

      if (userDefinedDataType.contains("GEO")) {
         Pattern pattern = Pattern.compile("(GEO)_(\\d+)");
         Matcher matcher = pattern.matcher(userDefinedDataType);
         if (!matcher.find()) {
            throw new SQLException(MessageFormat.format("\"Data type 'GEO' must be formatted as ''GEO_{{GEOROLE}}''. Sample: ''GEO_2''", ""), "", 7292);
         }

         userDefinedDataType = matcher.group(1);
         alterTableDetails.put("geoRole", matcher.group(2));
      }

      alterTableDetails.put("viewId", alterStVecObj.getLong("viewId"));
      alterTableDetails.put("columnName", GeneralUtil.trimIfAliasNameIsEnclosed(columnDetails.getColumnName()).trim());
      alterTableDetails.put("dataType", userDefinedDataType);
      return alterTableDetails;
   }

   static JSONObject parseAlterStatementRenameTableQuery(ZohoAnalyticsWorkspace workspace, String sql) throws Exception {
      JSONObject alterTableDetails = new JSONObject();
      JSONObject alterStVecObj = ZohoAnalyticsQueryParserUtil.parseAlterStatementVector(workspace, sql);
      AlterTable alterStVector = (AlterTable)alterStVecObj.get("alterStVector");
      String newTableName = alterStVector.getTableName();
      alterTableDetails.put("viewId", alterStVecObj.getLong("viewId"));
      alterTableDetails.put("tableName", GeneralUtil.trimIfAliasNameIsEnclosed(newTableName).trim());
      return alterTableDetails;
   }

   static JSONObject parseAlterStatementAddForeignKey(ZohoAnalyticsWorkspace workspace, String sql) throws Exception {
      JSONObject alterStVecObj = ZohoAnalyticsQueryParserUtil.parseAlterStatementVector(workspace, sql);
      AlterTable alterStVector = (AlterTable)alterStVecObj.get("alterStVector");
      AddClause addClause = alterStVector.getAddClause();
      CreateColumn createEle = (CreateColumn)addClause.getCreateColumnVector().firstElement();
      ConstraintClause constraintVec = (ConstraintClause)createEle.getConstraintClause().firstElement();
      ForeignConstraintClause fk = (ForeignConstraintClause)constraintVec.getConstraintType();
      String tableName = alterStVecObj.getString("viewName");
      String columnName = GeneralUtil.trimIfAliasNameIsEnclosed(fk.getConstraintColumnNames().firstElement().toString()).trim();
      String refViewName = GeneralUtil.trimIfAliasNameIsEnclosed(fk.getTableName().toString()).trim();
      String refColumn = GeneralUtil.trimIfAliasNameIsEnclosed(fk.getReferenceTableColumnNames().firstElement().toString()).trim();
      JSONObject config = new JSONObject();
      config.put("viewId", alterStVecObj.getLong("viewId"));
      config.put("columnId", workspace.getColumnIdFromName(tableName, columnName));
      config.put("referenceViewId", workspace.getViewIdFromName(refViewName));
      config.put("referenceColumnId", workspace.getColumnIdFromName(refViewName, refColumn));
      return config;
   }

   static JSONObject parseAlterStatementRenameColumn(ZohoAnalyticsWorkspace workspace, String sql) throws Exception {
      JSONObject alterStVecObj = ZohoAnalyticsQueryParserUtil.parseAlterStatementVector(workspace, sql);
      AlterTable alterStVector = (AlterTable)alterStVecObj.get("alterStVector");
      String viewName = alterStVecObj.getString("viewName");
      String oldColumnName = GeneralUtil.trimIfAliasNameIsEnclosed(alterStVector.getTableName()).trim();
      JSONObject config = new JSONObject();
      config.put("viewId", alterStVecObj.getLong("viewId"));
      config.put("columnId", workspace.getColumnIdFromName(viewName, oldColumnName));
      config.put("columnName", GeneralUtil.trimIfAliasNameIsEnclosed(alterStVector.getColumnName()).trim());
      return config;
   }

   static JSONObject parseAlterStatementDropColumn(ZohoAnalyticsWorkspace workspace, String sql) throws Exception {
      JSONObject alterStVecObj = ZohoAnalyticsQueryParserUtil.parseAlterStatementVector(workspace, sql);
      AlterTable alterStVector = (AlterTable)alterStVecObj.get("alterStVector");
      String viewName = alterStVecObj.getString("viewName");
      DropClause dropClause = alterStVector.getDropClause();
      String columnName = GeneralUtil.trimIfAliasNameIsEnclosed(dropClause.getColumnNamesVector().firstElement().toString()).trim();
      JSONObject config = new JSONObject();
      config.put("viewId", alterStVecObj.getLong("viewId"));
      config.put("columnId", workspace.getColumnIdFromName(viewName, columnName));
      return config;
   }

   protected static String getAccountsURL(String hostName, Properties props) throws SQLException {
      if (props.containsKey("ACCOUNTS_URL")) {
         return (String)props.get("ACCOUNTS_URL");
      } else if (ZohoAnalyticsJDBCConstants.HOST_NAME_VS_ACCOUNTS_URL.containsKey(hostName)) {
         return (String)ZohoAnalyticsJDBCConstants.HOST_NAME_VS_ACCOUNTS_URL.get(hostName);
      } else {
         throw new SQLException("Invalid Accounts URL");
      }
   }

   protected static String getAnalyticsURL(String hostName, Properties props) {
      return props.containsKey("ANALYTICS_URL") ? (String)props.get("ANALYTICS_URL") : String.format("https://%s", hostName);
   }

   static String getServerProps(String property, String dummy) throws SQLException {
      try {
         Object propertyValue = ZohoAnalyticsJDBCConstants.ANALYTICS_SERVER_PROPS.get(property);
         String value = "";
         if (propertyValue != null) {
            value = (String)propertyValue;
         }

         return value;
      } catch (Exception var4) {
         throw new NotImplementedException();
      }
   }

   static int getServerProps(String property, int dummy) throws SQLException {
      try {
         Object propertyValue = ZohoAnalyticsJDBCConstants.ANALYTICS_SERVER_PROPS.get(property);
         int value = -1;
         if (propertyValue != null) {
            value = (Integer)propertyValue;
         }

         return value;
      } catch (Exception var4) {
         throw new NotImplementedException();
      }
   }

   static boolean getServerProps(String property, boolean dummy) throws SQLException {
      try {
         Object propertyValue = ZohoAnalyticsJDBCConstants.ANALYTICS_SERVER_PROPS.get(property);
         boolean value = false;
         if (propertyValue != null) {
            value = (Boolean)propertyValue;
         }

         return value;
      } catch (Exception var4) {
         throw new NotImplementedException();
      }
   }

   static String validateAndTrimSQL(String sql) throws SQLException {
      if (sql != null && !sql.trim().isEmpty()) {
         return sql.trim().replaceAll(";$", "");
      } else {
         throw new SQLException("Empty SQL statement");
      }
   }
}
