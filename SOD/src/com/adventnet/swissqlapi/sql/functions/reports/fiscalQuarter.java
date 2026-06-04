package com.adventnet.swissqlapi.sql.functions.reports;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class fiscalQuarter extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
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
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterDt")) {
            qry = "(TRUNC(MOD(( MONTH(" + StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString()) + ")+ " + arguments.get(1).toString() + " - 1) , 12) / 3) +1)";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (from_sqs != null && from_sqs.isMySqlLive()) {
         qry = "";
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterIntrval")) {
            qry = "(((( MONTH((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))+ " + arguments.get(1).toString() + " - 1)%12) div 3) +1)";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterDt")) {
            qry = "(((( MONTH(" + arguments.get(0).toString() + ")+ " + arguments.get(1).toString() + " - 1) %12) div 3) +1)";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer[] argu;
      int i_count;
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fquarter")) {
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
         qry = " quarter(timestamp(" + colName + ") + INTERVAL  '1'  MONTH * " + i_count + ")";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         String qry;
         String colNameDate;
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fquarterintrval")) {
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
            qry = "CAST((((  MOD((MONTH((FROM_UNIXTIME(0) + INTERVAL  '1'  second * (" + colNameDate + " / 1000))) + " + argu[1] + " -1), 12)  ) / 3) + 1) AS INTEGER)";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fquarterdt")) {
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
            qry = "CAST((((  MOD((MONTH(" + colNameDate + ") + " + argu[1] + " -1), 12)  ) / 3) + 1) AS INTEGER)";
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
         String qry = "(cast(((( cast(EXTRACT(MONTH FROM CAST(" + this.handleStringLiteralForDateTime(arguments.get(0).toString(), from_sqs) + " AS TIMESTAMP)) as int) + " + arguments.get(1).toString() + " -1) % 12) " + divType + " 3) as int) + 1)";
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterIntrval")) {
            qry = "(cast(((( cast(EXTRACT(MONTH FROM (timestamp '1970-01-01 00:00:00' + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " " + divType + " 1000))) as int) + " + arguments.get(1).toString() + " -1) % 12) " + divType + " 3) as int) + 1)";
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
      if (from_sqs.isMSAzure()) {
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterIntrval")) {
            qry = "(((( MONTH((FROM_UNIXTIME(0)+INTERVAL(d DIV 1000) second))+ addMonth - 1)%12) div 3) +1)";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterDt")) {
            qry = "(((( MONTH(" + arguments.get(0).toString() + ")+ " + arguments.get(1).toString() + " - 1) %12) /3) +1)";
         }
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
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fquarter")) {
         String colName = argu.get(0).toString();
         int yearOffset = argu.get(2).toString().equals("1") ? 0 : 1;
         int addMonth = 12 - Integer.parseInt(argu.get(1).toString()) + 1 - 12 * yearOffset;
         if (isdenodo) {
            qry = "GETQUARTER(" + StringFunctions.handleLiteralStringDateForDenodo(colName) + " + INTERVAL '1' MONTH * " + addMonth + ")";
         } else {
            qry = " TO_CHAR((to_timestamp(" + colName + ") + INTERVAL  '1'  MONTH * " + addMonth + "),'Q')";
         }
      } else {
         String colNameDate;
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fquarterintrval")) {
            colNameDate = argu.get(0).toString();
            if (isdenodo) {
               qry = "TRUNC(((( MOD((GETMONTH((TO_LOCALDATE('yyyymmdd','19700101')+INTERVAL '1' second * (GETTIMEINMILLIS(" + StringFunctions.handleLiteralStringDateForDenodo(colNameDate) + ")/1000)))+" + argu.get(1) + " -1), 12) ) /3)+1))";
            } else {
               qry = "TRUNC((((  MOD((TO_CHAR((TO_DATE('19700101','yyyymmdd') + INTERVAL  '1'  second * (" + colNameDate + " / 1000)),'MM') + " + argu.get(1) + " -1), 12)  ) / 3) + 1))";
            }
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fquarterdt")) {
            if (isdenodo) {
               qry = "TRUNC((((  MOD((GETMONTH(" + StringFunctions.handleLiteralStringDateForDenodo(argu.get(0).toString()) + ") + " + argu.get(1) + " -1), 12) ) / 3) + 1))";
            } else {
               colNameDate = StringFunctions.handleLiteralStringDateForOracle(argu.get(0).toString());
               qry = "TRUNC((((  MOD((TO_CHAR(" + colNameDate + ",'MM') + " + argu.get(1) + " -1), 12)  ) / 3) + 1))";
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
      String qry = "CAST(TRUNC(((MOD(( cast(EXTRACT(MONTH FROM CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as int64) + " + arguments.get(1).toString() + " -1) , 12) / 3) + 1)) AS INT64)";
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterIntrval")) {
         qry = "(((( cast(EXTRACT(MONTH FROM (timestamp '1970-01-01 00:00:00' + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int64) + " + arguments.get(1).toString() + " -1) % 12) / 3) + 1)";
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
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterDt")) {
         qry = "TRUNC((((( MONTH(CAST(" + arguments.get(0).toString() + " AS DATETIME))+ " + arguments.get(1).toString() + " - 1) %12) /3) +1))";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterDt")) {
         qry = "(((( MONTH(" + arguments.get(0).toString() + ")+ " + arguments.get(1).toString() + " - 1) %12) /3) +1)";
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
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterDt")) {
         qry = "(((( MONTH(" + StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString()) + ")+ " + arguments.get(1).toString() + " - 1) %12) /3) +1)";
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

      this.setFunctionArguments(arguments);
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterDt")) {
         qry = "CAST(((MOD((MONTH(" + StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString()) + ")+ " + arguments.get(1).toString() + " - 1) ,12) /3) +1) AS INTEGER)";
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

      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterDt")) {
         qry = "cast(((mod((strftime('%m'," + StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString()) + ")+" + arguments.get(1).toString() + "-1),12)/3)+1) as integer)";
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

      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterDt")) {
         qry = " ((MOD((MONTH(" + StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString()) + ")+ " + arguments.get(1).toString() + " - 1) ,12) /3) +1)::INT ";
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

      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterDt")) {
         qry = " (Int(((MONTH(" + arguments.get(0).toString() + ")+ " + arguments.get(1).toString() + " - 1) MOD 12) /3) +1) ";
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
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fQuarterDt")) {
         qry = "(TRUNC(MOD(( MONTH(" + StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString()) + ")+ " + arguments.get(1).toString() + " - 1) , 12) / 3) +1)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }
}
