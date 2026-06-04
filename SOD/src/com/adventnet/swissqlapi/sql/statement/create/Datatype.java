package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.exception.ConvertException;

public interface Datatype {
   void toOracleString() throws ConvertException;

   void toMSSQLServerString() throws ConvertException;

   void toSybaseString() throws ConvertException;

   void toDB2String() throws ConvertException;

   void toPostgreSQLString() throws ConvertException;

   void toMySQLString() throws ConvertException;

   void toANSIString() throws ConvertException;

   void toInformixString() throws ConvertException;

   void toTimesTenString() throws ConvertException;

   void toNetezzaString() throws ConvertException;

   void toTeradataString() throws ConvertException;

   void toBigQueryString() throws ConvertException;

   void toSnowflakeString() throws ConvertException;

   void toAthenaString() throws ConvertException;

   void toSapHanaString() throws ConvertException;

   void toSqliteString() throws ConvertException;

   void toDenodoString() throws ConvertException;

   void toExcelString() throws ConvertException;

   void toMsAccessJdbcString() throws ConvertException;

   String getDatatypeName();

   void setDatatypeName(String var1);

   String getOpenBrace();

   void setOpenBrace(String var1);

   String getClosedBrace();

   void setClosedBrace(String var1);

   String getSize();

   void setSize(String var1);

   String getArray();

   void setArray(String var1);
}
