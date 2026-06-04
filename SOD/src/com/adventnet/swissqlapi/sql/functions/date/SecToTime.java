package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class SecToTime extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("sec_to_time")) {
         if (from_sqs != null && from_sqs.isAmazonRedShift()) {
            qry = "to_char(INTERVAL '" + arguments.get(0).toString() + " seconds', 'HH24:MI:SS' )";
         } else {
            qry = " to_char( (" + arguments.get(0).toString() + " ||' seconds')::interval, 'HH24:MI:SS' )";
         }
      } else {
         String hours = "CAST(FLOOR(" + arguments.get(0).toString() + ") AS INTEGER)";
         String minutes = "CAST(FLOOR(" + arguments.get(1).toString() + ") AS SMALLINT)";
         String seconds = "CAST(" + arguments.get(2).toString() + " AS SMALLINT)";
         if (from_sqs != null && from_sqs.isVerticaDb()) {
            qry = "((CASE WHEN " + hours + "  > 9 THEN CAST(" + hours + " as VARCHAR) ELSE CONCAT('0', CAST(" + hours + " as VARCHAR)) END) || ':' || (CASE WHEN " + minutes + "  > 9 THEN CAST(" + minutes + " as VARCHAR) ELSE CONCAT('0', CAST(" + minutes + " as VARCHAR)) END) || ':' || (CASE WHEN " + seconds + "  > 9 THEN CAST(" + seconds + " as VARCHAR) ELSE CONCAT('0', CAST(" + seconds + " as VARCHAR)) END))";
         } else if (from_sqs != null && from_sqs.isPgsqlLive()) {
            qry = "CONCAT_WS(':', (CASE WHEN " + hours + "  > 9 THEN CAST(" + hours + " as TEXT) ELSE CONCAT('0', CAST(" + hours + " as TEXT)) END), (CASE WHEN " + minutes + "  > 9 THEN CAST(" + minutes + " as TEXT) ELSE CONCAT('0', CAST(" + minutes + " as TEXT)) END), (CASE WHEN " + seconds + "  > 9 THEN CAST(" + seconds + " as TEXT) ELSE CONCAT('0', CAST(" + seconds + " as TEXT)) END))";
         } else if (from_sqs != null && from_sqs.isAmazonRedShift()) {
            qry = "((CASE WHEN " + hours + "  > 9 THEN CAST(" + hours + " as TEXT) ELSE CONCAT('0', CAST(" + hours + " as TEXT)) END) || ':' || (CASE WHEN " + minutes + "  > 9 THEN CAST(" + minutes + " as TEXT) ELSE CONCAT('0', CAST(" + minutes + " as TEXT)) END) || ':' || (CASE WHEN " + seconds + "  > 9 THEN CAST(" + seconds + " as TEXT) ELSE CONCAT('0', CAST(" + seconds + " as TEXT)) END))";
         } else {
            qry = "ZR_SECTOTIME(" + hours + "," + minutes + "," + seconds + ")";
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (from_sqs != null && from_sqs.isMySqlLive()) {
         String hours;
         String minutes;
         String seconds;
         String qry;
         if (from_sqs.isHyperSql()) {
            Vector arguments = new Vector();

            for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i_count));
               }
            }

            hours = arguments.get(0).toString();
            minutes = arguments.get(1).toString();
            seconds = arguments.get(2).toString();
            qry = "CONCAT_WS(':',(CASE WHEN " + hours + " > 9 THEN TO_CHAR(" + hours + ") ELSE CONCAT('0'," + hours + ") END),(CASE WHEN " + minutes + " > 9 THEN TO_CHAR(" + minutes + ") ELSE CONCAT('0'," + minutes + ") END),(CASE WHEN " + seconds + " > 9 THEN TO_CHAR(" + seconds + ") ELSE CONCAT('0'," + seconds + ") END))";
         } else {
            hours = this.functionArguments.get(0).toString();
            minutes = this.functionArguments.get(1).toString();
            seconds = this.functionArguments.get(2).toString();
            qry = "CONCAT_WS(':',if(" + hours + " > 9, " + hours + ", CONCAT('0'," + hours + ")),if(" + minutes + " > 9, " + minutes + ", CONCAT('0'," + minutes + ")),if(" + seconds + " > 9, " + seconds + ", CONCAT('0'," + seconds + ")))";
         }

         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         Vector arguments = new Vector();
         Vector vector1 = new Vector(1);
         Vector vector2 = new Vector(1);
         Vector vector3 = new Vector(1);

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               vector1.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
               vector2.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
               vector3.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            } else {
               vector1.addElement(this.functionArguments.elementAt(i_count));
               vector2.addElement(this.functionArguments.elementAt(i_count));
               vector3.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.functionName.setColumnName("ZR_SECTOTIME");
         SelectColumn scHours = new SelectColumn();
         Vector vcHours = new Vector(3);
         vcHours.addElement(this.absColumn(vector1));
         vcHours.addElement("div");
         vcHours.addElement("3600");
         scHours.setOpenBrace("(");
         scHours.setCloseBrace(")");
         scHours.setColumnExpression(vcHours);
         arguments.addElement(scHours);
         SelectColumn scMinutes = new SelectColumn();
         Vector vcMinutesOut = new Vector(3);
         SelectColumn scMinutesIn = new SelectColumn();
         Vector vcMinutesIn = new Vector(3);
         vcMinutesIn.addElement(this.absColumn(vector2));
         vcMinutesIn.addElement("%");
         vcMinutesIn.addElement("3600");
         scMinutesIn.setOpenBrace("(");
         scMinutesIn.setCloseBrace(")");
         scMinutesIn.setColumnExpression(vcMinutesIn);
         vcMinutesOut.addElement(scMinutesIn);
         vcMinutesOut.addElement("div");
         vcMinutesOut.addElement("60");
         scMinutes.setOpenBrace("(");
         scMinutes.setCloseBrace(")");
         scMinutes.setColumnExpression(vcMinutesOut);
         arguments.addElement(scMinutes);
         SelectColumn scSeconds = new SelectColumn();
         Vector vcSecondsOut = new Vector(3);
         SelectColumn scSecondsIn = new SelectColumn();
         Vector vcSecondsIn = new Vector(3);
         vcSecondsIn.addElement(this.absColumn(vector3));
         vcSecondsIn.addElement("%");
         vcSecondsIn.addElement("3600");
         scSecondsIn.setOpenBrace("(");
         scSecondsIn.setCloseBrace(")");
         scSecondsIn.setColumnExpression(vcSecondsIn);
         vcSecondsOut.addElement(scSecondsIn);
         vcSecondsOut.addElement("%");
         vcSecondsOut.addElement("60");
         scSeconds.setOpenBrace("(");
         scSeconds.setCloseBrace(")");
         scSeconds.setColumnExpression(vcSecondsOut);
         arguments.addElement(scSeconds);
         this.setFunctionArguments(arguments);
      }
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String funName = this.functionName.getColumnName().trim();
      String hours;
      if (funName.equalsIgnoreCase("ZR_SECTOTIME")) {
         hours = "FLOOR( " + arguments.get(0).toString() + " )";
         String minutes = "FLOOR( " + arguments.get(1).toString() + " )";
         String seconds = "FLOOR( " + arguments.get(2).toString() + " )";
         String qry = "CONCAT_WS(':', (CASE WHEN " + hours + "  > 9 THEN CAST(" + hours + " as TEXT) ELSE CONCAT('0', CAST(" + hours + " as TEXT)) END), (CASE WHEN " + minutes + "  > 9 THEN CAST(" + minutes + " as TEXT) ELSE CONCAT('0', CAST(" + minutes + " as TEXT)) END), (CASE WHEN " + seconds + "  > 9 THEN CAST(" + seconds + " as TEXT) ELSE CONCAT('0', CAST(" + seconds + " as TEXT)) END))";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         hours = "TIME_FROM_PARTS(0, 0, " + arguments.get(0) + ")";
         this.functionName.setColumnName(hours);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String funName = this.functionName.getColumnName().trim();
      String qry = "";
      if (funName.equalsIgnoreCase("ZR_SECTOTIME")) {
         String hours = "CAST(FLOOR( " + arguments.get(0).toString() + " ) AS INT)";
         String minutes = "CAST(FLOOR( " + arguments.get(1).toString() + " ) AS INT)";
         String seconds = "CAST(FLOOR( " + arguments.get(2).toString() + " ) AS INT)";
         qry = "(CASE WHEN " + hours + "  > 9 THEN CAST(" + hours + " as VARCHAR) ELSE CONCAT('0', CAST(" + hours + " as VARCHAR)) END) ||':'|| (CASE WHEN " + minutes + "  > 9 THEN CAST(" + minutes + " as VARCHAR) ELSE CONCAT('0', CAST(" + minutes + " as VARCHAR)) END)||':'|| (CASE WHEN " + seconds + "  > 9 THEN CAST(" + seconds + " as VARCHAR) ELSE CONCAT('0', CAST(" + seconds + " as VARCHAR)) END)";
      } else {
         qry = "DATE_FORMAT(CAST((TIME'00:00:00'+(" + arguments.get(0) + ")*interval '1' second) AS TIMESTAMP),'%H:%i:%s')";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector(3);

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String argument;
      if (this.functionName.getColumnName().equalsIgnoreCase("sec_to_time")) {
         argument = "(time('00:00:00')+ interval '1' second * (" + arguments + "))";
         this.functionName.setColumnName(argument);
      } else {
         argument = "CAST(FLOOR(" + arguments.get(0).toString() + ") AS INTEGER)";
         String minutes = "(" + arguments.get(1).toString() + ")";
         String seconds = arguments.get(2).toString();
         String qry = "CONCAT(if(" + argument + "  > 9, CAST(" + argument + " AS VARCHAR), CONCAT('0',CAST(" + argument + " as VARCHAR))) , ':', if(" + minutes + "  > 9, CAST(" + minutes + " AS VARCHAR), CONCAT('0',CAST(" + minutes + " as VARCHAR))) , ':', if(" + seconds + "  > 9, CAST(" + seconds + " AS VARCHAR), CONCAT('0',CAST(" + seconds + " as VARCHAR))) )";
         this.functionName.setColumnName(qry);
      }

      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public SelectColumn absColumn(Vector vcAbsIn) {
      SelectColumn scAbs = new SelectColumn();
      Vector vcAbsOut = new Vector(1);
      FunctionCalls fnClAbs = new FunctionCalls();
      TableColumn tbClAbs = new TableColumn();
      tbClAbs.setColumnName("ABS");
      fnClAbs.setFunctionName(tbClAbs);
      fnClAbs.setFunctionArguments(vcAbsIn);
      vcAbsOut.addElement(fnClAbs);
      scAbs.setColumnExpression(vcAbsOut);
      return scAbs;
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String funName = this.functionName.getColumnName().trim();
      if (funName.equalsIgnoreCase("ZR_SECTOTIME")) {
         String hours = arguments.get(0).toString();
         String minutes = arguments.get(1).toString();
         String seconds = arguments.get(2).toString();
         String qry = "((CASE WHEN " + hours + "  > 9 THEN CAST(" + hours + " as VARCHAR) ELSE CONCAT('0', CAST(" + hours + " as VARCHAR)) END) + ':' + (CASE WHEN " + minutes + "  > 9 THEN CAST(" + minutes + " as VARCHAR) ELSE CONCAT('0', CAST(" + minutes + " as VARCHAR)) END) + ':' + (CASE WHEN " + seconds + "  > 9 THEN CAST(" + seconds + " as VARCHAR) ELSE CONCAT('0', CAST(" + seconds + " as VARCHAR)) END))";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }

   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String funName = this.functionName.getColumnName().trim();
      if (funName.equalsIgnoreCase("ZR_SECTOTIME")) {
         String hours = "CAST(" + arguments.get(0).toString() + " AS INT64)";
         String minutes = "CAST(" + arguments.get(1).toString() + " AS INT64)";
         String seconds = "CAST(" + arguments.get(2).toString() + " AS INT64)";
         String qry = "TIME(" + hours + ", " + minutes + ", " + seconds + " )";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }

   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String intSyntax = isdenodo ? "INT4" : "INT";
      if (this.functionName.getColumnName().equalsIgnoreCase("sec_to_time")) {
         qry = "TO_CHAR(DATE '1970-01-01' + ( 1 / 86400) *" + arguments.get(0).toString() + ",'hh24:mi:ss')";
      } else {
         qry = "(case when " + arguments.get(0).toString() + " > 9 then trim(cast(round(cast(floor(" + arguments.get(0).toString() + ") as " + intSyntax + ")) as char(100))) else (0 || trim(cast(round(cast(floor(" + arguments.get(0).toString() + ") as " + intSyntax + ")) as char(100)))) end)||':'||(case when " + arguments.get(1).toString() + " > 9 then trim(cast(round(cast(floor(" + arguments.get(1).toString() + ") as " + intSyntax + ")) as char(100))) else (0 || trim(cast(round(cast(floor(" + arguments.get(1).toString() + ") as " + intSyntax + ")) as char(100)))) end)||':'||(case when " + arguments.get(2).toString() + " > 9 then trim(cast(round(cast(floor(" + arguments.get(2).toString() + ") as " + intSyntax + ")) as char(100))) else (0 || trim(cast(round(cast(floor(" + arguments.get(2).toString() + ") as " + intSyntax + ")) as char(2)))) end)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String funName = this.functionName.getColumnName().trim();
      String qry = "";
      if (funName.equalsIgnoreCase("ZR_SECTOTIME")) {
         String hours = "CAST(" + arguments.get(0).toString() + " AS BIGINT)";
         String minutes = "CAST(" + arguments.get(1).toString() + " AS BIGINT)";
         String seconds = "CAST(" + arguments.get(2).toString() + " AS BIGINT)";
         qry = "((CASE WHEN " + hours + "  > 9 THEN CAST(" + hours + " as VARCHAR) ELSE CONCAT('0', CAST(" + hours + " as VARCHAR)) END) || ':' || (CASE WHEN " + minutes + "  > 9 THEN CAST(" + minutes + " as VARCHAR) ELSE CONCAT('0', CAST(" + minutes + " as VARCHAR)) END) || ':' || (CASE WHEN " + seconds + "  > 9 THEN CAST(" + seconds + " as VARCHAR) ELSE CONCAT('0', CAST(" + seconds + " as VARCHAR)) END))";
      } else {
         qry = "TO_TIME(ADD_SECONDS('00:00:00'," + arguments.get(0) + "),'HH24:MI:SS')";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String funName = this.functionName.getColumnName().trim();
      String qry = "";
      if (funName.equalsIgnoreCase("ZR_SECTOTIME")) {
         String hours = "CAST(" + arguments.get(0).toString() + " AS INTEGER)";
         String minutes = "CAST(" + arguments.get(1).toString() + " AS INTEGER)";
         String seconds = "CAST(" + arguments.get(2).toString() + " AS INTEGER)";
         qry = "((CASE WHEN " + hours + "  > 9 THEN " + hours + " ELSE ('0' || " + hours + ") END) || ':' || (CASE WHEN " + minutes + "  > 9 THEN " + minutes + " ELSE ('0' || " + minutes + ") END) || ':' || (CASE WHEN " + seconds + "  > 9 THEN " + seconds + " ELSE ('0' || " + seconds + ") END))";
      } else {
         qry = "time(" + arguments.get(0) + ",'unixepoch')";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String funName = this.functionName.getColumnName().trim();
      if (funName.equalsIgnoreCase("ZR_SECTOTIME")) {
         String hours = arguments.get(0).toString();
         String minutes = arguments.get(1).toString();
         String seconds = arguments.get(2).toString();
         String qry = "((CASE WHEN " + hours + "  > 9 THEN CAST(" + hours + " as VARCHAR) ELSE CONCAT('0', CAST(" + hours + " as VARCHAR)) END) || ':' || (CASE WHEN " + minutes + "  > 9 THEN CAST(" + minutes + " as VARCHAR) ELSE CONCAT('0', CAST(" + minutes + " as VARCHAR)) END) || ':' || (CASE WHEN " + seconds + "  > 9 THEN CAST(" + seconds + " as VARCHAR) ELSE CONCAT('0', CAST(" + seconds + " as VARCHAR)) END))";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }

   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String funName = this.functionName.getColumnName().trim();
      String qry = "";
      if (funName.equalsIgnoreCase("ZR_SECTOTIME")) {
         String hours = arguments.get(0).toString();
         String minutes = arguments.get(1).toString();
         String seconds = arguments.get(2).toString();
         qry = "((CASE WHEN " + hours + "  > 9 THEN " + hours + "::LVARCHAR ELSE ('0' || " + hours + ") END) || ':' || (CASE WHEN " + minutes + "  > 9 THEN " + minutes + "::LVARCHAR ELSE ('0' || " + minutes + ") END) || ':' || (CASE WHEN " + seconds + "  > 9 THEN " + seconds + "::LVARCHAR ELSE ('0' || " + seconds + ") END))";
      } else {
         qry = "TO_CHAR(TO_DATE('1970-01-01 00:00:00') + " + arguments.get(0).toString() + " UNITS SECOND,'%H:%M:%S')";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "FORMAT(TIMESERIAL(" + arguments.get(0) + "," + arguments.get(1) + "," + arguments.get(2) + "),'hh:nn:ss')";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String hours = arguments.get(0).toString();
      String minutes = arguments.get(1).toString();
      String seconds = arguments.get(2).toString();
      String qry = "CONCAT_WS(':',(CASE WHEN " + hours + " > 9 THEN TO_CHAR(" + hours + ") ELSE CONCAT('0'," + hours + ") END),(CASE WHEN " + minutes + " > 9 THEN TO_CHAR(" + minutes + ") ELSE CONCAT('0'," + minutes + ") END),(CASE WHEN " + seconds + " > 9 THEN TO_CHAR(" + seconds + ") ELSE CONCAT('0'," + seconds + ") END))";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }
}
