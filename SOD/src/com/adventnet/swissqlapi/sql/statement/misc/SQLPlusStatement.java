package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class SQLPlusStatement implements SwisSQLStatement {
   private String setString;
   private String autoCommitString;
   private String autoCommitState;
   private String equals;
   private String defineString;
   private String notSupportedComment = null;

   public void setSetString(String setString) {
      this.setString = setString;
   }

   public void setAutoCommitString(String autoCommitString) {
      this.autoCommitString = autoCommitString;
   }

   public void setAutoCommitState(String autoCommitState) {
      this.autoCommitState = autoCommitState;
   }

   public void setEquals(String equals) {
      this.equals = equals;
   }

   public void setDefineString(String defineString) {
      this.defineString = defineString;
   }

   public String getSetString() {
      return this.setString;
   }

   public String getAutoCommitString() {
      return this.autoCommitString;
   }

   public String setAutoCommitState() {
      return this.autoCommitState;
   }

   public String getEquals() {
      return this.equals;
   }

   public String getDefineString() {
      return this.defineString;
   }

   public CommentClass getCommentClass() {
      return null;
   }

   public UserObjectContext getObjectContext() {
      return null;
   }

   public String removeIndent(String str) {
      return str;
   }

   public void setCommentClass(CommentClass commentClass) {
   }

   public void setObjectContext(UserObjectContext userObjectContext) {
   }

   public String toANSIString() throws ConvertException {
      return this.toANSISQLPlus().toString();
   }

   public String toTeradataString() throws ConvertException {
      return this.toTeradataSQLPlus().toString();
   }

   public String toDB2String() throws ConvertException {
      return this.toDB2SQLPlus().toString();
   }

   public String toInformixString() throws ConvertException {
      return this.toInformixSQLPlus().toString();
   }

   public String toMSSQLServerString() throws ConvertException {
      return this.toMSSQLServerSQLPlus().toString();
   }

   public String toMySQLString() throws ConvertException {
      return this.toMySQLSQLPlus().toString();
   }

   public String toOracleString() throws ConvertException {
      return this.toOracleSQLPlus().toString();
   }

   public String toPostgreSQLString() throws ConvertException {
      return this.toPostgreSQLPlus().toString();
   }

   public String toBigQueryString() throws ConvertException {
      return this.toBigQueryPlus().toString();
   }

   public String toSybaseString() throws ConvertException {
      return this.toSybaseSQLPlus().toString();
   }

   public String toTimesTenString() throws ConvertException {
      return this.toTimesTenSQLPlus().toString();
   }

   public String toNetezzaString() throws ConvertException {
      return this.toNetezzaSQLPlus().toString();
   }

   public String toSnowflakeString() throws ConvertException {
      return this.toSnowflakeSQLPlus().toString();
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

   public SQLPlusStatement toANSISQLPlus() throws ConvertException {
      SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
      if (this.setString != null) {
         sqlPlusStatement.setSetString(this.setString);
      }

      if (this.autoCommitString != null) {
         sqlPlusStatement.setAutoCommitString(this.autoCommitString);
      }

      if (this.defineString != null) {
         sqlPlusStatement.setDefineString(this.defineString);
         sqlPlusStatement.setNotSupportedComment("The following construct is not supported in ANSI SQL");
      }

      if (this.equals != null) {
         sqlPlusStatement.setEquals((String)null);
      }

      if (this.autoCommitState != null) {
         if (this.autoCommitState.equals("1")) {
            sqlPlusStatement.setAutoCommitState("ON");
         } else if (this.autoCommitState.equals("0")) {
            sqlPlusStatement.setAutoCommitState("OFF");
         } else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
            sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
         }
      }

      return sqlPlusStatement;
   }

   public SQLPlusStatement toTeradataSQLPlus() throws ConvertException {
      SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
      if (this.setString != null) {
         sqlPlusStatement.setSetString(this.setString);
      }

      if (this.autoCommitString != null) {
         sqlPlusStatement.setAutoCommitString(this.autoCommitString);
      }

      if (this.defineString != null) {
         sqlPlusStatement.setDefineString(this.defineString);
         sqlPlusStatement.setNotSupportedComment("The following construct is not supported in Teradata SQL");
      }

      if (this.equals != null) {
         sqlPlusStatement.setEquals((String)null);
      }

      if (this.autoCommitState != null) {
         if (this.autoCommitState.equals("1")) {
            sqlPlusStatement.setAutoCommitState("ON");
         } else if (this.autoCommitState.equals("0")) {
            sqlPlusStatement.setAutoCommitState("OFF");
         } else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
            sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
         }
      }

      return sqlPlusStatement;
   }

   public SQLPlusStatement toDB2SQLPlus() throws ConvertException {
      SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
      if (this.setString != null) {
         sqlPlusStatement.setSetString("DB2SET");
      }

      if (this.autoCommitString != null) {
         sqlPlusStatement.setAutoCommitString("DB2OPTIONS");
      }

      if (this.defineString != null) {
         sqlPlusStatement.setDefineString(this.defineString);
         sqlPlusStatement.setNotSupportedComment("The following construct is not supported in DB2");
      }

      if (this.equals != null) {
         sqlPlusStatement.setEquals("=");
      }

      if (this.autoCommitState != null) {
         if (this.autoCommitState.equals("1")) {
            sqlPlusStatement.setAutoCommitState("-C");
         } else if (this.autoCommitState.equals("0")) {
            sqlPlusStatement.setAutoCommitState("+C");
         } else if ((this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) && this.defineString != null) {
            sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
         }
      }

      return sqlPlusStatement;
   }

   public SQLPlusStatement toInformixSQLPlus() throws ConvertException {
      SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
      if (this.setString != null) {
         sqlPlusStatement.setSetString(this.setString);
      }

      if (this.autoCommitString != null) {
         sqlPlusStatement.setAutoCommitString(this.autoCommitString);
      }

      if (this.defineString != null) {
         sqlPlusStatement.setDefineString(this.defineString);
         sqlPlusStatement.setNotSupportedComment("The following construct is not supported in Informix");
      }

      if (this.equals != null) {
         sqlPlusStatement.setEquals((String)null);
      }

      if (this.autoCommitState != null) {
         if (this.autoCommitState.equals("1")) {
            sqlPlusStatement.setAutoCommitState("ON");
         } else if (this.autoCommitState.equals("0")) {
            sqlPlusStatement.setAutoCommitState("OFF");
         } else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
            sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
         }
      }

      return sqlPlusStatement;
   }

   public SQLPlusStatement toMSSQLServerSQLPlus() throws ConvertException {
      SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
      if (this.setString != null) {
         sqlPlusStatement.setSetString(this.setString);
      }

      if (this.autoCommitString != null) {
         sqlPlusStatement.setAutoCommitString("IMPLICIT_TRANSACTIONS");
      }

      if (this.defineString != null) {
         sqlPlusStatement.setDefineString(this.defineString);
         sqlPlusStatement.setNotSupportedComment("The following construct is not supported in SQL Server");
      }

      if (this.equals != null) {
         sqlPlusStatement.setEquals((String)null);
      }

      if (this.autoCommitState != null) {
         if (this.autoCommitState.equals("1")) {
            sqlPlusStatement.setAutoCommitState("OFF");
         } else if (this.autoCommitState.equals("0")) {
            sqlPlusStatement.setAutoCommitState("ON");
         } else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
            sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
         }
      }

      return sqlPlusStatement;
   }

   public SQLPlusStatement toMySQLSQLPlus() throws ConvertException {
      SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
      if (this.setString != null) {
         sqlPlusStatement.setSetString(this.setString);
      }

      if (this.autoCommitString != null) {
         sqlPlusStatement.setAutoCommitString(this.autoCommitString);
      }

      if (this.defineString != null) {
         sqlPlusStatement.setDefineString(this.defineString);
         sqlPlusStatement.setNotSupportedComment("The following construct is not supported in MySQL");
      }

      if (this.equals != null) {
         sqlPlusStatement.setEquals(this.equals);
      }

      if (this.autoCommitState != null) {
         if (this.autoCommitState.equals("1")) {
            sqlPlusStatement.setAutoCommitState("1");
         } else if (this.autoCommitState.equals("0")) {
            sqlPlusStatement.setAutoCommitState("0");
         } else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
            sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
         }
      }

      return sqlPlusStatement;
   }

   public SQLPlusStatement toOracleSQLPlus() throws ConvertException {
      SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
      if (this.setString != null) {
         sqlPlusStatement.setSetString(this.setString);
      }

      if (this.autoCommitString != null) {
         if (this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
            sqlPlusStatement.setAutoCommitString("CONSTRAINTS");
         } else {
            sqlPlusStatement.setAutoCommitString(this.autoCommitString);
         }
      }

      if (this.defineString != null) {
         sqlPlusStatement.setDefineString(this.defineString);
      }

      if (this.equals != null) {
         sqlPlusStatement.setEquals((String)null);
      }

      if (this.autoCommitState != null) {
         if (this.autoCommitState.equals("1")) {
            if (this.autoCommitString != null && this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
               sqlPlusStatement.setAutoCommitState("IMMEDIATE");
            } else {
               sqlPlusStatement.setAutoCommitState("ON");
            }
         } else if (this.autoCommitState.equals("0")) {
            if (this.autoCommitString != null && this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
               sqlPlusStatement.setAutoCommitState("DEFERRED");
            } else {
               sqlPlusStatement.setAutoCommitState("OFF");
            }
         } else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
            sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
         }
      }

      return sqlPlusStatement;
   }

   public SQLPlusStatement toBigQueryPlus() throws ConvertException {
      SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
      if (this.setString != null) {
         sqlPlusStatement.setSetString(this.setString);
      }

      if (this.autoCommitString != null) {
         if (this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
            sqlPlusStatement.setAutoCommitString("CONSTRAINTS ALL");
         } else {
            sqlPlusStatement.setAutoCommitString(this.autoCommitString);
         }
      }

      if (this.defineString != null) {
         sqlPlusStatement.setDefineString(this.defineString);
         sqlPlusStatement.setNotSupportedComment("The following construct is not supported in BigQuery");
      }

      if (this.equals != null && !this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
         sqlPlusStatement.setEquals("TO");
      }

      if (this.autoCommitState != null) {
         if (this.autoCommitState.equals("1")) {
            if (this.autoCommitString != null && this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
               sqlPlusStatement.setAutoCommitState("IMMEDIATE");
            } else {
               sqlPlusStatement.setAutoCommitState("ON");
            }
         } else if (this.autoCommitState.equals("0")) {
            if (this.autoCommitString != null && this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
               sqlPlusStatement.setAutoCommitState("DEFERRED");
            } else {
               sqlPlusStatement.setAutoCommitState("OFF");
            }
         } else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
            sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
         }
      }

      return sqlPlusStatement;
   }

   public SQLPlusStatement toPostgreSQLPlus() throws ConvertException {
      SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
      if (this.setString != null) {
         sqlPlusStatement.setSetString(this.setString);
      }

      if (this.autoCommitString != null) {
         if (this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
            sqlPlusStatement.setAutoCommitString("CONSTRAINTS ALL");
         } else {
            sqlPlusStatement.setAutoCommitString(this.autoCommitString);
         }
      }

      if (this.defineString != null) {
         sqlPlusStatement.setDefineString(this.defineString);
         sqlPlusStatement.setNotSupportedComment("The following construct is not supported in PostgreSQL");
      }

      if (this.equals != null && !this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
         sqlPlusStatement.setEquals("TO");
      }

      if (this.autoCommitState != null) {
         if (this.autoCommitState.equals("1")) {
            if (this.autoCommitString != null && this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
               sqlPlusStatement.setAutoCommitState("IMMEDIATE");
            } else {
               sqlPlusStatement.setAutoCommitState("ON");
            }
         } else if (this.autoCommitState.equals("0")) {
            if (this.autoCommitString != null && this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
               sqlPlusStatement.setAutoCommitState("DEFERRED");
            } else {
               sqlPlusStatement.setAutoCommitState("OFF");
            }
         } else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
            sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
         }
      }

      return sqlPlusStatement;
   }

   public SQLPlusStatement toSybaseSQLPlus() throws ConvertException {
      SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
      if (this.setString != null) {
         sqlPlusStatement.setSetString(this.setString);
      }

      if (this.autoCommitString != null) {
         sqlPlusStatement.setAutoCommitString("CHAINED");
      }

      if (this.defineString != null) {
         sqlPlusStatement.setDefineString(this.defineString);
         sqlPlusStatement.setNotSupportedComment("The following construct is not supported in Sybase");
      }

      if (this.equals != null) {
         sqlPlusStatement.setEquals((String)null);
      }

      if (this.autoCommitState != null) {
         if (this.autoCommitState.equals("1")) {
            sqlPlusStatement.setAutoCommitState("OFF");
         } else if (this.autoCommitState.equals("0")) {
            sqlPlusStatement.setAutoCommitState("ON");
         } else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
            sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
         }
      }

      return sqlPlusStatement;
   }

   public SQLPlusStatement toTimesTenSQLPlus() throws ConvertException {
      SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
      if (this.setString != null) {
         sqlPlusStatement.setSetString((String)null);
      }

      if (this.autoCommitString != null) {
         sqlPlusStatement.setAutoCommitString("AUTOCOMMIT");
      }

      if (this.defineString != null) {
         sqlPlusStatement.setDefineString(this.defineString);
         sqlPlusStatement.setNotSupportedComment("The following construct is not supported in TimesTen");
      }

      if (this.equals != null) {
         sqlPlusStatement.setEquals((String)null);
      }

      if (this.autoCommitState != null) {
         if (this.autoCommitState.equalsIgnoreCase("ON")) {
            sqlPlusStatement.setAutoCommitState("0");
         } else if (this.autoCommitState.equalsIgnoreCase("OFF")) {
            sqlPlusStatement.setAutoCommitState("1");
         } else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
            sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
         }
      }

      return sqlPlusStatement;
   }

   public SQLPlusStatement toNetezzaSQLPlus() throws ConvertException {
      SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
      if (this.setString != null) {
         sqlPlusStatement.setSetString(this.setString);
      }

      if (this.autoCommitString != null) {
         sqlPlusStatement.setAutoCommitString(this.autoCommitString);
      }

      if (this.defineString != null) {
         sqlPlusStatement.setDefineString(this.defineString);
         sqlPlusStatement.setNotSupportedComment("The following construct is not supported in Netezza SQL");
      }

      if (this.equals != null) {
         sqlPlusStatement.setEquals((String)null);
      }

      if (this.autoCommitState != null) {
         if (this.autoCommitState.equals("1")) {
            sqlPlusStatement.setAutoCommitState("ON");
         } else if (this.autoCommitState.equals("0")) {
            sqlPlusStatement.setAutoCommitState("OFF");
         } else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
            sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
         }
      }

      return sqlPlusStatement;
   }

   public SQLPlusStatement toSnowflakeSQLPlus() throws ConvertException {
      SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
      if (this.setString != null) {
         sqlPlusStatement.setSetString(this.setString);
      }

      if (this.autoCommitString != null) {
         if (this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
            sqlPlusStatement.setAutoCommitString("CONSTRAINTS ALL");
         } else {
            sqlPlusStatement.setAutoCommitString(this.autoCommitString);
         }
      }

      if (this.defineString != null) {
         sqlPlusStatement.setDefineString(this.defineString);
         sqlPlusStatement.setNotSupportedComment("The following construct is not supported in Snowflake");
      }

      if (this.equals != null && !this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
         sqlPlusStatement.setEquals("TO");
      }

      if (this.autoCommitState != null) {
         if (this.autoCommitState.equals("1")) {
            if (this.autoCommitString != null && this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
               sqlPlusStatement.setAutoCommitState("IMMEDIATE");
            } else {
               sqlPlusStatement.setAutoCommitState("ON");
            }
         } else if (this.autoCommitState.equals("0")) {
            if (this.autoCommitString != null && this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
               sqlPlusStatement.setAutoCommitState("DEFERRED");
            } else {
               sqlPlusStatement.setAutoCommitState("OFF");
            }
         } else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
            sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
         }
      }

      return sqlPlusStatement;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      String indentString = "\n";
      if (this.setString != null) {
         sb.append(indentString + this.setString.toUpperCase());
      }

      if (this.autoCommitString != null) {
         sb.append(" " + this.autoCommitString.toUpperCase());
      }

      if (this.defineString != null) {
         sb.append(" " + this.defineString.toUpperCase());
      }

      if (this.equals != null) {
         sb.append(" " + this.equals);
      }

      if (this.autoCommitState != null) {
         sb.append(" " + this.autoCommitState.toUpperCase());
      }

      if (this.notSupportedComment != null) {
         sb.insert(0, "/*" + this.notSupportedComment + "\n");
         sb.append("\n*/");
      }

      return sb.toString();
   }

   public void setNotSupportedComment(String notSupportedComment) {
      this.notSupportedComment = notSupportedComment;
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
