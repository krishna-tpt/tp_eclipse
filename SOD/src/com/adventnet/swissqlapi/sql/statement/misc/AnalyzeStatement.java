package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class AnalyzeStatement implements SwisSQLStatement {
   private String analyzeString = null;
   private String tableString = null;
   private String tableName = null;
   private String analyzeOption = null;
   private String sampleNumber = null;
   private String rowsOrPercent = null;

   public CommentClass getCommentClass() {
      return null;
   }

   public UserObjectContext getObjectContext() {
      return null;
   }

   public void setAnalyzeString(String str) {
      this.analyzeString = str;
   }

   public void setTableString(String str) {
      this.tableString = str;
   }

   public void setTableName(String str) {
      this.tableName = str;
   }

   public void setAnalyzeOption(String str) {
      this.analyzeOption = str;
   }

   public void setSampleNumber(String str) {
      this.sampleNumber = str;
   }

   public void setRowsOrPercent(String str) {
      this.rowsOrPercent = str;
   }

   public String removeIndent(String formattedSqlString) {
      return formattedSqlString;
   }

   public void setCommentClass(CommentClass commentObject) {
   }

   public void setObjectContext(UserObjectContext obj) {
   }

   public String toANSIString() throws ConvertException {
      return "Query not supported";
   }

   public String toTeradataString() throws ConvertException {
      return "Query not supported";
   }

   public String toDB2String() throws ConvertException {
      return "Query not supported";
   }

   public String toInformixString() throws ConvertException {
      return "Query not supported";
   }

   public String toMSSQLServerString() throws ConvertException {
      if (this.tableString != null && this.tableString.equalsIgnoreCase("TABLE")) {
         if (this.tableName != null && this.analyzeOption != null && this.analyzeOption.equalsIgnoreCase("ESTIMATE") && this.rowsOrPercent != null && this.sampleNumber != null && this.rowsOrPercent.equalsIgnoreCase("ROWS")) {
            return "UPDATE STATISTICS " + this.tableName + " WITH SAMPLE " + this.sampleNumber + " " + this.rowsOrPercent;
         }

         if (this.tableName != null && this.analyzeOption != null && this.analyzeOption.equalsIgnoreCase("COMPUTE")) {
            return "UPDATE STATISTICS " + this.tableName;
         }
      }

      return "Query not supported";
   }

   public String toMySQLString() throws ConvertException {
      return "Query not supported";
   }

   public String toOracleString() throws ConvertException {
      return "Query not supported";
   }

   public String toPostgreSQLString() throws ConvertException {
      return "Query not supported";
   }

   public String toSybaseString() throws ConvertException {
      return "Query not supported";
   }

   public String toTimesTenString() throws ConvertException {
      return "Query not supported";
   }

   public String toNetezzaString() throws ConvertException {
      return "Query not supported";
   }

   public String toVectorWiseString() throws ConvertException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public String toBigQueryString() throws ConvertException {
      return "Query not supported";
   }

   public String toSnowflakeString() throws ConvertException {
      return "Query not supported";
   }

   public String toAthenaString() throws ConvertException {
      return "Query not supported";
   }

   public String toSapHanaString() throws ConvertException {
      return "Query not supported";
   }

   public String toSqliteString() throws ConvertException {
      return "Query not supported";
   }

   public String toExcelString() throws ConvertException {
      return "Query not supported";
   }

   public String toMsAccessJdbcString() throws ConvertException {
      return "Query not supported";
   }

   public SwisSQLStatement toVectorWise() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toInformix() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toNetezza() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toTimesTen() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toOracle() throws ConvertException {
      return null;
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
      return null;
   }

   public SwisSQLStatement toMySQL() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toPostgreSQL() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toBigQuery() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toDB2() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSybase() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toMSSQLServer() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toTeradata() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toANSI() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toMsAccessJdbc() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toExcel() throws ConvertException {
      return null;
   }
}
