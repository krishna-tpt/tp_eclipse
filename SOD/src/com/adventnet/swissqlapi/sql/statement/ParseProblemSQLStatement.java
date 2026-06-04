package com.adventnet.swissqlapi.sql.statement;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;

public class ParseProblemSQLStatement implements SwisSQLStatement {
   private String sqlStatement = null;
   private CommentClass commentObject;
   private String plsqlStatement = null;
   private UserObjectContext objectContext = null;

   public void setCommentClass(CommentClass commentObject) {
      commentObject = this.commentObject;
   }

   public CommentClass getCommentClass() {
      return this.commentObject;
   }

   public void setSQLStatement(String sql) {
      this.sqlStatement = sql;
   }

   public String getSQLStatement() {
      return this.sqlStatement;
   }

   public void setPLSQLStr(String plsqlStatement) {
      this.plsqlStatement = plsqlStatement;
   }

   public String getPLSQLStr() {
      return this.plsqlStatement;
   }

   public String toString() {
      return this.sqlStatement;
   }

   public String toOracleString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toMSSQLServerString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toSybaseString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toDB2String() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toPostgreSQLString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toMySQLString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toANSIString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toInformixString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toTimesTenString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toBigQueryString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toSnowflakeString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toAthenaString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toSapHanaString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toSqliteString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toExcelString() throws ConvertException {
      return "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toMsAccessJdbcString() throws ConvertException {
      return "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String removeIndent(String s_ri) {
      return this.toString();
   }

   public UserObjectContext getObjectContext() {
      return this.objectContext;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.objectContext = obj;
   }

   public String toNetezzaString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toTeradataString() throws ConvertException {
      return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
   }

   public String toVectorWiseString() throws ConvertException {
      throw new UnsupportedOperationException("Not supported yet.");
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
