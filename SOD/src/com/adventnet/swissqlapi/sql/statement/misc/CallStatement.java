package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class CallStatement implements SwisSQLStatement {
   private String callClause;
   private Object callStatement;

   public void setCallClause(String s_call_clause) {
      this.callClause = s_call_clause;
   }

   public void setCallStatement(Object o_call_stmt) {
      this.callStatement = o_call_stmt;
   }

   public String getCallClause() {
      return this.callClause;
   }

   public Object getCallStatement() {
      return this.callStatement;
   }

   public String toOracleString() throws ConvertException {
      return "Query not supported";
   }

   public String toMSSQLServerString() throws ConvertException {
      return "Query not supported";
   }

   public String toSybaseString() throws ConvertException {
      return "Query not supported";
   }

   public String toDB2String() throws ConvertException {
      return "Query not supported";
   }

   public String toPostgreSQLString() throws ConvertException {
      return "Query not supported";
   }

   public String toMySQLString() throws ConvertException {
      return "Query not supported";
   }

   public String toANSIString() throws ConvertException {
      return "Query not supported";
   }

   public String toInformixString() throws ConvertException {
      return "Query not supported";
   }

   public String toTimesTenString() throws ConvertException {
      return "Query not supported";
   }

   public String toNetezzaString() throws ConvertException {
      return "Query not supported";
   }

   public String toTeradataString() throws ConvertException {
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

   public void setCommentClass(CommentClass commentClass) {
   }

   public CommentClass getCommentClass() {
      return null;
   }

   public UserObjectContext getObjectContext() {
      return null;
   }

   public void setObjectContext(UserObjectContext userObjectContext) {
   }

   public String removeIndent(String s) {
      return null;
   }

   public String toVectorWiseString() throws ConvertException {
      return "Query not supported";
   }

   public String toBigQueryString() throws ConvertException {
      return "Query not supported";
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.callClause + " ");
      sb.append(this.callStatement.toString());
      return sb.toString();
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
