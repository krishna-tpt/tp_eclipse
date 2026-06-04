package com.adventnet.swissqlapi.sql.functions.reports;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class fiscalYear extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer[] argu;
      int i_count;
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fyear")) {
         argu = new StringBuffer[3];
         String qry = "";

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            argu[i_count] = new StringBuffer();
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               argu[i_count].append(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
            } else {
               argu[i_count].append(this.functionArguments.elementAt(i_count));
            }
         }

         String colName = argu[0].toString().replaceAll("\"", "");
         int yearOffset = argu[2].toString().equals("1") ? 0 : 1;
         i_count = 12 - Integer.parseInt(argu[1].toString()) + 1 - 12 * yearOffset;
         qry = " YEAR(timestamp(" + colName + ") + INTERVAL '1' MONTH * " + i_count + ")";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         String qry;
         String colNameDate;
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fyearintrval")) {
            argu = new StringBuffer[2];
            qry = "";

            for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               argu[i_count] = new StringBuffer();
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  argu[i_count].append(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
               } else {
                  argu[i_count].append(this.functionArguments.elementAt(i_count));
               }
            }

            colNameDate = argu[0].toString().replaceAll("\"", "");
            qry = "CAST((((MONTH((FROM_UNIXTIME(0) + INTERVAL  '1'  second * (" + colNameDate + " / 1000))) + " + argu[1] + " -1) / 12) + YEAR((FROM_UNIXTIME(0) + INTERVAL  '1'  second * (" + colNameDate + " / 1000)))) AS INTEGER)";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fyeardt")) {
            argu = new StringBuffer[2];
            qry = "";

            for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               argu[i_count] = new StringBuffer();
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  argu[i_count].append(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
               } else {
                  argu[i_count].append(this.functionArguments.elementAt(i_count));
               }
            }

            colNameDate = argu[0].toString().replaceAll("\"", "");
            qry = "CAST((((MONTH(" + colNameDate + ") + " + argu[1] + " -1) / 12) + YEAR(" + colNameDate + ")) AS INTEGER)";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0) instanceof String) {
               this.handleStringLiteralForDate(from_sqs, i_count, true);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (isPostgreLiveDbs) {
         String divType = from_sqs != null && from_sqs.isVerticaDb() ? "//" : "/";
         String qry = "(cast((( cast(EXTRACT(MONTH FROM CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as int) + " + arguments.get(1).toString() + " -1) " + divType + " 12) as int)+ cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as int) )";
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearIntrval")) {
            qry = "(cast((( cast(EXTRACT(MONTH FROM (timestamp '1970-01-01 00:00:00' + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " " + divType + " 1000))) as int) + " + arguments.get(1).toString() + " -1) " + divType + " 12) as int) + cast(extract (year from  (timestamp '1970-01-01 00:00:00' + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " " + divType + " 1000)) ) as int) )";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (from_sqs.isMSAzure() && this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearDt")) {
         qry = "(((MONTH(" + arguments.get(0) + ") + " + arguments.get(1) + " - 1)/12) + YEAR(" + arguments.get(0) + "))";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearDt")) {
         qry = "((TRUNC((MONTH(CAST(" + arguments.get(0) + " AS DATETIME)) + " + arguments.get(1) + " - 1)/12) + YEAR(CAST(" + arguments.get(0) + " AS DATETIME)))::INT)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearDt")) {
         qry = "CAST((((MONTH(" + dateString + ") + " + arguments.get(1).toString() + " - 1)/12) + YEAR(" + dateString + ")) AS INT)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearDt")) {
         qry = "CAST(((MONTH(" + dateString + ") + " + arguments.get(1).toString() + " - 1)/12) + YEAR(" + dateString + ") AS INTEGER)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearDt")) {
         qry = "cast((((strftime('%m'," + dateString + ")+" + arguments.get(1).toString() + " -1)/12) + strftime('%Y'," + dateString + ")) as integer)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector argu = new Vector();
      Boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            argu.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            argu.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fyear")) {
         String colName = argu.get(0).toString();
         int yearOffset = argu.get(2).toString().equals("1") ? 0 : 1;
         int addMonth = 12 - Integer.parseInt(argu.get(1).toString()) + 1 - 12 * yearOffset;
         qry = " TO_CHAR((TO_TIMESTAMP(" + colName + ") + INTERVAL '1' MONTH * " + addMonth + "),'YYYY')";
      } else {
         String colNameDate;
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fyearintrval")) {
            colNameDate = argu.get(0).toString();
            qry = "TRUNC((((TO_CHAR((TO_DATE('19700101','yyyymmdd') + INTERVAL  '1'  second * (" + colNameDate + " / 1000)),'MM') + " + argu.get(1) + " -1) / 12) + TO_CHAR((TO_DATE('19700101','yyyymmdd') + INTERVAL  '1'  second * (" + colNameDate + " / 1000)),'YYYY')))";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fyeardt")) {
            colNameDate = argu.get(0).toString();
            if (isdenodo) {
               colNameDate = StringFunctions.handleLiteralStringDateForDenodo(colNameDate);
               qry = "TRUNC((((GETMONTH(" + colNameDate + ") + " + argu.get(1) + " -1) / 12) + GETYEAR(" + colNameDate + ")))";
            } else {
               colNameDate = StringFunctions.handleLiteralStringDateForOracle(colNameDate);
               qry = "TRUNC((((TO_CHAR(" + colNameDate + ",'MM') + " + argu.get(1) + " -1) / 12) + TO_CHAR(" + colNameDate + ",'YYYY')))";
            }
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      String qry;
      if (from_sqs != null && from_sqs.isHyperSql()) {
         qry = "";
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearDt")) {
            String dateString = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
            qry = " (TRUNC((MONTH(" + dateString + ") + " + arguments.get(1).toString() + " - 1) / 12) + YEAR(" + dateString + "))";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (from_sqs != null && from_sqs.isMySqlLive()) {
         qry = "";
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fyearintrval")) {
            qry = "(((MONTH((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))+" + arguments.get(1).toString() + " - 1) div 12) + YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)))";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearDt")) {
            qry = " (((MONTH(" + arguments.get(0).toString() + ") + " + arguments.get(1).toString() + " - 1) DIV 12) + YEAR(" + arguments.get(0).toString() + "))";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
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

      this.setFunctionArguments(arguments);
      String qry = "(DIV(( cast(EXTRACT(MONTH FROM CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as int64) + " + arguments.get(1).toString() + " -1) , 12) + cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as int64) )";
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearIntrval")) {
         qry = "((( cast(EXTRACT(MONTH FROM (timestamp '1970-01-01 00:00:00' + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int64) + " + arguments.get(1).toString() + " -1) / 12) + cast(extract (year from  (timestamp '1970-01-01 00:00:00' + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int64) )";
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

      this.setFunctionArguments(arguments);
      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearDt")) {
         qry = "CAST((((MONTH(" + arguments.get(0).toString() + ") + " + arguments.get(1).toString() + " - 1)/12) + YEAR(" + arguments.get(0).toString() + ")) AS INT)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearDt")) {
         qry = "CAST((((MONTH(" + dateString + ") + " + arguments.get(1).toString() + " - 1)/12) + YEAR(" + dateString + ")) AS INT)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = arguments.get(0).toString();
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearDt")) {
         qry = "(Int((MONTH(" + dateString + ") + " + arguments.get(1).toString() + " - 1)/12) + YEAR(" + dateString + "))";
      }

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

      this.setFunctionArguments(arguments);
      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearDt")) {
         String dateString = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
         qry = " (TRUNC((MONTH(" + dateString + ") + " + arguments.get(1).toString() + " - 1) / 12) + YEAR(" + dateString + "))";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }
}
