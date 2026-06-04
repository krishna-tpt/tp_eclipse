package com.adventnet.swissqlapi.sql.functions.reports;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class fiscalQuarterYear extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer[] argu;
      int i_count;
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fquarteryear")) {
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
         qry = " (YEAR(timestamp(" + colName + ") + INTERVAL '1' MONTH * " + i_count + ") * 10) + quarter(timestamp(" + colName + ") + INTERVAL  '1'  MONTH * " + i_count + ")";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         String qry;
         String colNameDate;
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fquarteryearintrval")) {
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
            qry = "CAST((((((MONTH((FROM_UNIXTIME(0) + INTERVAL  '1'  second * (" + colNameDate + " / 1000))) + " + argu[1] + " -1) / 12) + YEAR((FROM_UNIXTIME(0) + INTERVAL  '1'  second * (" + colNameDate + " / 1000)))) * 10) + ((  MOD((MONTH((FROM_UNIXTIME(0) + INTERVAL  '1'  second * (" + colNameDate + " / 1000))) + " + argu[1] + " -1), 12)  ) / 3) + 1) AS INTEGER)";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fquarteryeardt")) {
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
            qry = "CAST((((((MONTH(" + colNameDate + ") + " + argu[1] + " -1) / 12) + YEAR(" + colNameDate + ")) * 10) + ((  MOD((MONTH(" + colNameDate + ") + " + argu[1] + " -1), 12)  ) / 3) + 1) AS INTEGER)";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }
      }

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
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterYearDt")) {
            String dateString = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
            qry = "(TRUNC(((TRUNC((MONTH(" + dateString + ")+" + arguments.get(1).toString() + " - 1) / 12) + YEAR(" + dateString + "))*10)+ MOD((MONTH(" + dateString + ")+" + arguments.get(1).toString() + " - 1),12) / 3) +1)";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (from_sqs != null && from_sqs.isMySqlLive()) {
         qry = "";
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterYearIntrval")) {
            qry = "((((((MONTH((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))+" + arguments.get(1).toString() + " - 1) div 12) + YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)))*10)+ (( MONTH((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))+" + arguments.get(1).toString() + " - 1)%12) div 3) +1)";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterYearDt")) {
            qry = "((((((MONTH(" + arguments.get(0).toString() + ")+" + arguments.get(1).toString() + " - 1) div 12) + YEAR(" + arguments.get(0).toString() + "))*10)+ ((MONTH(" + arguments.get(0).toString() + ")+" + arguments.get(1).toString() + " - 1)%12) div 3) +1)";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (isPostgreLiveDbs) {
         String qry = "((((( cast(EXTRACT(MONTH FROM " + arguments.get(0).toString() + ") as int) + " + arguments.get(1).toString() + " -1) / 12) + cast(extract (year from  " + arguments.get(0).toString() + " ) as int) ) * 10) + ((( cast(EXTRACT(MONTH FROM " + arguments.get(0).toString() + ") as int) + " + arguments.get(1).toString() + " -1) % 12) / 3) + 1)";
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterYearIntrval")) {
            qry = "((((( cast(EXTRACT(MONTH FROM (timestamp '1970-01-01 00:00:00' + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int) + " + arguments.get(1).toString() + " -1) / 12) + cast(extract (year from  (timestamp '1970-01-01 00:00:00' + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int) ) * 10) + ((( cast(EXTRACT(MONTH FROM (timestamp '1970-01-01 00:00:00' + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int) + " + arguments.get(1).toString() + " -1) % 12) / 3) + 1)";
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

      this.setFunctionArguments(arguments);
      if (from_sqs.isMSAzure() && this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterYearDt")) {
         qry = "((((((MONTH(" + arguments.get(0).toString() + ")+ " + arguments.get(1).toString() + "- 1) / 12) + YEAR(" + arguments.get(0).toString() + "))*10)+ ((MONTH(" + arguments.get(0).toString() + ")+ " + arguments.get(1).toString() + " - 1)%12) /3) +1)";
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

      this.setFunctionArguments(arguments);
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterYearDt")) {
         qry = "((((((MONTH(" + arguments.get(0).toString() + ")+ " + arguments.get(1).toString() + "- 1) / 12) + YEAR(" + arguments.get(0).toString() + "))*10)+ ((MONTH(" + arguments.get(0).toString() + ")+ " + arguments.get(1).toString() + " - 1)%12) /3) +1)";
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

      this.setFunctionArguments(arguments);
      String dateString = StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterYearDt")) {
         qry = "((((((MONTH(" + dateString + ")+ " + arguments.get(1).toString() + "- 1) / 12) + YEAR(" + dateString + "))*10)+ ((MONTH(" + dateString + ")+ " + arguments.get(1).toString() + " - 1)%12) /3) +1)";
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

      this.setFunctionArguments(arguments);
      String dateString = arguments.get(0).toString();
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterYearDt")) {
         qry = "CLng(Int(((Int((MONTH(" + dateString + ")+ " + arguments.get(1).toString() + "- 1) / 12) + YEAR(" + dateString + "))*10)+ ((MONTH(" + dateString + ")+ " + arguments.get(1).toString() + " - 1) MOD 12) /3) +1)";
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
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterYearDt")) {
         String dateString = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
         qry = "(TRUNC(((TRUNC((MONTH(" + dateString + ")+" + arguments.get(1).toString() + " - 1) / 12) + YEAR(" + dateString + "))*10)+ MOD((MONTH(" + dateString + ")+" + arguments.get(1).toString() + " - 1),12) / 3) +1)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

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

      this.setFunctionArguments(arguments);
      String dateString = arguments.get(0).toString();
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterYearDt")) {
         qry = "((((((MONTH(" + dateString + ")+ " + arguments.get(1).toString() + "- 1) / 12) + YEAR(" + dateString + "))*10)+ (MOD(MONTH(" + dateString + ")+ " + arguments.get(1).toString() + " - 1),12) /3) +1)";
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
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterYearDt")) {
         qry = "cast(((((strftime('%m'," + dateString + ")+" + arguments.get(1).toString() + " -1)/12) + strftime('%Y'," + dateString + "))*10)+((mod((strftime('%m'," + dateString + ")+" + arguments.get(1).toString() + "-1),12)/3)+1) as integer)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector argu = new Vector();
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            argu.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            argu.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fquarteryear")) {
         String colName = argu.get(0).toString();
         int yearOffset = argu.get(2).toString().equals("1") ? 0 : 1;
         int addMonth = 12 - Integer.parseInt(argu.get(1).toString()) + 1 - 12 * yearOffset;
         if (isdenodo) {
            qry = "(GETYEAR((" + StringFunctions.handleLiteralStringDateForDenodo(colName) + ")+INTERVAL '1' MONTH * " + addMonth + ")*10)+(GETQUARTER((" + StringFunctions.handleLiteralStringDateForDenodo(colName) + ")+INTERVAL '1' MONTH * " + addMonth + "))";
         } else {
            qry = " (TO_CHAR((to_timestamp(" + colName + ") + INTERVAL '1' MONTH * " + addMonth + "),'YYYY') * 10) + TO_CHAR((to_timestamp(" + colName + ") + INTERVAL  '1'  MONTH * " + addMonth + "),'Q')";
         }
      } else {
         String colNameDate;
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fquarteryearintrval")) {
            colNameDate = argu.get(0).toString();
            if (isdenodo) {
               qry = "TRUNC((((((GETMONTH((TO_LOCALDATE('yyyymmdd','19700101')+INTERVAL '1' second * (GETTIMEINMILLIS(" + colNameDate + ") / 1000))) + " + argu.get(1) + " -1) /12) + GETYEAR((TO_LOCALDATE('yyyymmdd','19700101') + INTERVAL '1' second * (GETTIMEINMILLIS(" + colNameDate + ") / 1000))))*10) + (( MOD((GETMONTH((TO_LOCALDATE('yyyymmdd','19700101') + INTERVAL '1' second * (GETTIMEINMILLIS(" + colNameDate + ") / 1000))) + " + argu.get(1) + " -1), 12) ) /3)+1))";
            } else {
               qry = "TRUNC((((((TO_CHAR((TO_DATE('19700101','yyyymmdd') + INTERVAL  '1'  second * (" + colNameDate + " / 1000)),'MM') + " + argu.get(1) + " -1) / 12) + TO_CHAR((TO_DATE('19700101','yyyymmdd') + INTERVAL  '1'  second * (" + colNameDate + " / 1000)),'YYYY')) * 10) + ((  MOD((TO_CHAR((TO_DATE('19700101','yyyymmdd') + INTERVAL  '1'  second * (" + colNameDate + " / 1000)),'MM') + " + argu.get(1) + " -1), 12)  ) / 3) + 1))";
            }
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fquarteryeardt")) {
            colNameDate = argu.get(0).toString();
            if (isdenodo) {
               qry = "(((TRUNC(((GETMONTH(" + StringFunctions.handleLiteralStringDateForDenodo(colNameDate) + ")+" + argu.get(1) + " -1)/12)+GETYEAR(" + colNameDate + "))*10)+TRUNC((MOD((GETMONTH(" + StringFunctions.handleLiteralStringDateForDenodo(colNameDate) + ")+" + argu.get(1) + " -1), 12) /3))+1))";
            } else {
               qry = "(((TRUNC(((TO_CHAR(" + colNameDate + ",'MM') + " + argu.get(1) + " -1) / 12) + TO_CHAR(" + colNameDate + ",'YYYY')) * 10) + TRUNC((  MOD((TO_CHAR(" + colNameDate + ",'MM') + " + argu.get(1) + " -1), 12)  ) / 3))+ 1)";
            }
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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
      String qry = "cast((((div(( cast(EXTRACT(MONTH FROM " + arguments.get(0).toString() + ") as int64) + " + arguments.get(1).toString() + " -1) , 12) + cast(extract (year from  " + arguments.get(0).toString() + " ) as int64) ) * 10) + div(MOD(( cast(EXTRACT(MONTH FROM " + arguments.get(0).toString() + ") as int64) + " + arguments.get(1).toString() + " -1) , 12) , 3) + 1) AS INT64)";
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterYearIntrval")) {
         qry = "((((( cast(EXTRACT(MONTH FROM (timestamp '1970-01-01 00:00:00' + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int64) + " + arguments.get(1).toString() + " -1) / 12) + cast(extract (year from  (timestamp '1970-01-01 00:00:00' + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int64) ) * 10) + ((( cast(EXTRACT(MONTH FROM (timestamp '1970-01-01 00:00:00' + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int64) + " + arguments.get(1).toString() + " -1) % 12) / 3) + 1)";
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
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterYearDt")) {
         qry = "((((((MONTH(" + dateString + ")+ " + arguments.get(1).toString() + "- 1) / 12)::INT + YEAR(" + dateString + "))*10)+ MOD((MONTH(" + dateString + ")+ " + arguments.get(1).toString() + " - 1),12) /3)::INT +1)";
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

      String dateString = arguments.get(0).toString();
      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterYearDt")) {
         qry = "(CAST((((CAST(((MONTH(" + dateString + ")+ " + arguments.get(1).toString() + "- 1) / 12) AS INT) + YEAR(" + dateString + "))*10)+ MOD((MONTH(" + dateString + ")+ " + arguments.get(1).toString() + " - 1),12) /3) AS INT) +1)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }
}
