package com.adventnet.swissqlapi.sql.statement;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;

public interface SwisSQLStatement {
   String toString();

   String toOracleString() throws ConvertException;

   String toMSSQLServerString() throws ConvertException;

   String toSybaseString() throws ConvertException;

   String toDB2String() throws ConvertException;

   String toPostgreSQLString() throws ConvertException;

   String toMySQLString() throws ConvertException;

   String toANSIString() throws ConvertException;

   String toInformixString() throws ConvertException;

   String toTimesTenString() throws ConvertException;

   String toNetezzaString() throws ConvertException;

   String toTeradataString() throws ConvertException;

   void setCommentClass(CommentClass var1);

   CommentClass getCommentClass();

   UserObjectContext getObjectContext();

   void setObjectContext(UserObjectContext var1);

   String removeIndent(String var1);

   String toVectorWiseString() throws ConvertException;

   String toBigQueryString() throws ConvertException;

   String toSnowflakeString() throws ConvertException;

   String toAthenaString() throws ConvertException;

   String toSapHanaString() throws ConvertException;

   String toSqliteString() throws ConvertException;

   String toExcelString() throws ConvertException;

   String toMsAccessJdbcString() throws ConvertException;

   SwisSQLStatement toVectorWise() throws ConvertException;

   SwisSQLStatement toInformix() throws ConvertException;

   SwisSQLStatement toNetezza() throws ConvertException;

   SwisSQLStatement toTimesTen() throws ConvertException;

   SwisSQLStatement toOracle() throws ConvertException;

   SwisSQLStatement toSqlite() throws ConvertException;

   SwisSQLStatement toSapHana() throws ConvertException;

   SwisSQLStatement toAthena() throws ConvertException;

   SwisSQLStatement toSnowflake() throws ConvertException;

   SwisSQLStatement toMySQL() throws ConvertException;

   SwisSQLStatement toPostgreSQL() throws ConvertException;

   SwisSQLStatement toBigQuery() throws ConvertException;

   SwisSQLStatement toDB2() throws ConvertException;

   SwisSQLStatement toSybase() throws ConvertException;

   SwisSQLStatement toMSSQLServer() throws ConvertException;

   SwisSQLStatement toTeradata() throws ConvertException;

   SwisSQLStatement toANSI() throws ConvertException;

   SwisSQLStatement toMsAccessJdbc() throws ConvertException;

   SwisSQLStatement toExcel() throws ConvertException;
}
