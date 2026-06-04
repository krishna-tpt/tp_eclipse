package com.adventnet.swissqlapi.sql.statement.delete;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectNames;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import java.util.Vector;

public class TruncateStatement implements SwisSQLStatement {
   private String truncateClause = null;
   private String tableString = null;
   private TableObject truncatedTableObject = null;
   private ModifiedObjectNames modifiedObjects = null;

   public void TruncateStatement() {
   }

   public void setTruncateClause(String truncateClause) {
      this.truncateClause = truncateClause;
   }

   public void setTableString(String tableString) {
      this.tableString = tableString;
   }

   public void setTableObject(TableObject tableObject) {
      this.truncatedTableObject = tableObject;
   }

   public String getTruncateClause() {
      return this.truncateClause;
   }

   public String getTableString() {
      return this.tableString;
   }

   public TableObject getTableObject() {
      return this.truncatedTableObject;
   }

   public String toOracleString() throws ConvertException {
      return this.toOracleTruncate().toString();
   }

   public String toMSSQLServerString() throws ConvertException {
      return this.toMSSQLServerTruncate().toString();
   }

   public String toSybaseString() throws ConvertException {
      return this.toSybaseTruncate().toString();
   }

   public String toDB2String() throws ConvertException {
      return this.toDB2Truncate().toString();
   }

   public String toPostgreSQLString() throws ConvertException {
      return this.toPostgreSQLTruncate().toString();
   }

   public String toBigQueryString() throws ConvertException {
      return this.toBigQueryTruncate().toString();
   }

   public String toMySQLString() throws ConvertException {
      return this.toMySQLTruncate().toString();
   }

   public String toANSIString() throws ConvertException {
      return this.toANSITruncate().toString();
   }

   public String toTeradataString() throws ConvertException {
      return this.toTeradataTruncate().toString();
   }

   public String toInformixString() throws ConvertException {
      return this.toInformixTruncate().toString();
   }

   public String toTimesTenString() throws ConvertException {
      return this.toTimesTenTruncate().toString();
   }

   public String toNetezzaString() throws ConvertException {
      return this.toNetezzaTruncate().toString();
   }

   public String toSnowflakeString() throws ConvertException {
      return this.toSnowflakeTruncate().toString();
   }

   public String toAthenaString() throws ConvertException {
      return null;
   }

   public String toSapHanaString() throws ConvertException {
      return null;
   }

   public String toSqliteString() throws ConvertException {
      return null;
   }

   public String toExcelString() throws ConvertException {
      return null;
   }

   public String toMsAccessJdbcString() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toOracleTruncate() throws ConvertException {
      TruncateStatement truncStmt = new TruncateStatement();
      truncStmt.setTruncateClause("TRUNCATE");
      truncStmt.setTableString("TABLE");
      this.getTableObject().toOracle();
      this.truncatedTableObject = this.handleTableObject(this.getTableObject());
      truncStmt.setTableObject(this.getTableObject());
      return truncStmt;
   }

   public SwisSQLStatement toMSSQLServerTruncate() throws ConvertException {
      TruncateStatement truncStmt = new TruncateStatement();
      truncStmt.setTruncateClause("TRUNCATE");
      truncStmt.setTableString("TABLE");
      this.getTableObject().toMSSQLServer();
      this.truncatedTableObject = this.handleTableObject(this.getTableObject());
      truncStmt.setTableObject(this.getTableObject());
      return truncStmt;
   }

   public SwisSQLStatement toSybaseTruncate() throws ConvertException {
      TruncateStatement truncStmt = new TruncateStatement();
      truncStmt.setTruncateClause("TRUNCATE");
      truncStmt.setTableString("TABLE");
      this.getTableObject().toSybase();
      this.truncatedTableObject = this.handleTableObject(this.getTableObject());
      truncStmt.setTableObject(this.getTableObject());
      return truncStmt;
   }

   public SwisSQLStatement toDB2Truncate() throws ConvertException {
      DeleteQueryStatement deleteStmt = new DeleteQueryStatement();
      DeleteClause deleteClause = new DeleteClause();
      deleteClause.setDelete("DELETE");
      deleteStmt.setDeleteClause(deleteClause);
      FromClause fromClause = new FromClause();
      fromClause.setFromClause("FROM");
      Vector fromTableList = new Vector();
      FromTable fromTable = new FromTable();
      this.getTableObject().toDB2();
      this.truncatedTableObject = this.handleTableObject(this.getTableObject());
      fromTable.setTableName(this.getTableObject());
      fromTableList.add(fromTable);
      fromClause.setFromItemList(fromTableList);
      deleteStmt.setFromClause(fromClause);
      return deleteStmt;
   }

   public SwisSQLStatement toPostgreSQLTruncate() throws ConvertException {
      TruncateStatement truncStmt = new TruncateStatement();
      truncStmt.setTruncateClause("TRUNCATE");
      truncStmt.setTableString("TABLE");
      this.getTableObject().toPostgreSQL();
      this.truncatedTableObject = this.handleTableObject(this.getTableObject());
      truncStmt.setTableObject(this.getTableObject());
      return truncStmt;
   }

   public SwisSQLStatement toBigQueryTruncate() throws ConvertException {
      TruncateStatement truncStmt = new TruncateStatement();
      truncStmt.setTruncateClause("TRUNCATE");
      truncStmt.setTableString("TABLE");
      this.getTableObject().toPostgreSQL();
      this.truncatedTableObject = this.handleTableObject(this.getTableObject());
      truncStmt.setTableObject(this.getTableObject());
      return truncStmt;
   }

   public SwisSQLStatement toMySQLTruncate() throws ConvertException {
      TruncateStatement truncStmt = new TruncateStatement();
      truncStmt.setTruncateClause("TRUNCATE");
      truncStmt.setTableString("TABLE");
      this.getTableObject().toMySQL();
      this.truncatedTableObject = this.handleTableObject(this.getTableObject());
      truncStmt.setTableObject(this.getTableObject());
      return truncStmt;
   }

   public SwisSQLStatement toSnowflakeTruncate() throws ConvertException {
      TruncateStatement truncStmt = new TruncateStatement();
      truncStmt.setTruncateClause("TRUNCATE");
      truncStmt.setTableString("TABLE");
      this.getTableObject().toSnowflake();
      this.truncatedTableObject = this.handleTableObject(this.getTableObject());
      truncStmt.setTableObject(this.getTableObject());
      return truncStmt;
   }

   public SwisSQLStatement toANSITruncate() throws ConvertException {
      TruncateStatement truncStmt = new TruncateStatement();
      truncStmt.setTruncateClause("TRUNCATE");
      truncStmt.setTableString("TABLE");
      this.getTableObject().toANSISQL();
      this.truncatedTableObject = this.handleTableObject(this.getTableObject());
      truncStmt.setTableObject(this.getTableObject());
      return truncStmt;
   }

   public SwisSQLStatement toTeradataTruncate() throws ConvertException {
      DeleteQueryStatement deleteStmt = new DeleteQueryStatement();
      DeleteClause deleteClause = new DeleteClause();
      deleteClause.setDelete("DELETE");
      deleteStmt.setDeleteClause(deleteClause);
      FromClause fromClause = new FromClause();
      fromClause.setFromClause("FROM");
      Vector fromTableList = new Vector();
      FromTable fromTable = new FromTable();
      this.getTableObject().toTeradata();
      this.truncatedTableObject = this.handleTableObject(this.getTableObject());
      fromTable.setTableName(this.getTableObject());
      fromTableList.add(fromTable);
      fromClause.setFromItemList(fromTableList);
      deleteStmt.setFromClause(fromClause);
      DeleteLimitClause deleteLimitClause = new DeleteLimitClause();
      deleteLimitClause.setLimit("ALL");
      deleteLimitClause.setDimension("");
      deleteStmt.setDeleteLimitClause(deleteLimitClause);
      return deleteStmt;
   }

   public SwisSQLStatement toInformixTruncate() throws ConvertException {
      TruncateStatement truncStmt = new TruncateStatement();
      truncStmt.setTruncateClause("TRUNCATE");
      truncStmt.setTableString("TABLE");
      this.getTableObject().toInformix();
      this.truncatedTableObject = this.handleTableObject(this.getTableObject());
      truncStmt.setTableObject(this.getTableObject());
      return truncStmt;
   }

   public SwisSQLStatement toTimesTenTruncate() throws ConvertException {
      TruncateStatement truncStmt = new TruncateStatement();
      truncStmt.setTruncateClause("TRUNCATE");
      truncStmt.setTableString("TABLE");
      this.getTableObject().toTimesTen();
      this.truncatedTableObject = this.handleTableObject(this.getTableObject());
      truncStmt.setTableObject(this.getTableObject());
      return truncStmt;
   }

   public SwisSQLStatement toNetezzaTruncate() throws ConvertException {
      TruncateStatement truncStmt = new TruncateStatement();
      truncStmt.setTruncateClause("TRUNCATE");
      truncStmt.setTableString("TABLE");
      this.getTableObject().toNetezza();
      this.truncatedTableObject = this.handleTableObject(this.getTableObject());
      truncStmt.setTableObject(this.getTableObject());
      return truncStmt;
   }

   public void setCommentClass(CommentClass commentObject) {
   }

   public CommentClass getCommentClass() {
      return null;
   }

   public UserObjectContext getObjectContext() {
      return null;
   }

   public void setObjectContext(UserObjectContext obj) {
   }

   public String removeIndent(String formattedSqlString) {
      formattedSqlString = formattedSqlString.replace('\n', ' ');
      formattedSqlString = formattedSqlString.replace('\t', ' ');
      return formattedSqlString;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.truncateClause != null) {
         sb.append(this.truncateClause + " ");
      }

      if (this.tableString != null) {
         sb.append(this.tableString + " ");
      }

      if (this.truncatedTableObject != null) {
         sb.append(this.truncatedTableObject.toString());
      }

      return sb.toString();
   }

   public TableObject handleTableObject(TableObject tableObj) throws ConvertException {
      if (tableObj == null) {
         return tableObj;
      } else {
         String ownerName = tableObj.getOwner();
         String userName = tableObj.getUser();
         String tableName = tableObj.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tableObj.setOwner(ownerName);
         tableObj.setUser(userName);
         tableObj.setTableName(tableName);
         return tableObj;
      }
   }

   public String toVectorWiseString() throws ConvertException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public SwisSQLStatement toVectorWise() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toInformix() throws ConvertException {
      return this.toInformixTruncate();
   }

   public SwisSQLStatement toNetezza() throws ConvertException {
      return this.toNetezzaTruncate();
   }

   public SwisSQLStatement toTimesTen() throws ConvertException {
      return this.toTimesTenTruncate();
   }

   public SwisSQLStatement toOracle() throws ConvertException {
      return this.toOracleTruncate();
   }

   public SwisSQLStatement toSqlite() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSapHana() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toAthena() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSnowflake() throws ConvertException {
      return this.toSnowflakeTruncate();
   }

   public SwisSQLStatement toMySQL() throws ConvertException {
      return this.toMySQLTruncate();
   }

   public SwisSQLStatement toPostgreSQL() throws ConvertException {
      return this.toPostgreSQLTruncate();
   }

   public SwisSQLStatement toBigQuery() throws ConvertException {
      return this.toBigQueryTruncate();
   }

   public SwisSQLStatement toDB2() throws ConvertException {
      return this.toDB2Truncate();
   }

   public SwisSQLStatement toSybase() throws ConvertException {
      return this.toSybaseTruncate();
   }

   public SwisSQLStatement toMSSQLServer() throws ConvertException {
      return this.toMSSQLServerTruncate();
   }

   public SwisSQLStatement toTeradata() throws ConvertException {
      return this.toTeradataTruncate();
   }

   public SwisSQLStatement toANSI() throws ConvertException {
      return this.toANSITruncate();
   }

   public SwisSQLStatement toMsAccessJdbc() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toExcel() throws ConvertException {
      return null;
   }
}
