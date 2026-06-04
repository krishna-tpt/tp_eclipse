package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class last_day extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Boolean isDenodo = from_sqs != null && from_sqs.isDenodo();
      String lastDaySyntax = isDenodo ? "LASTDAYOFMONTH" : "LAST_DAY";
      this.functionName.setColumnName(lastDaySyntax);
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateColumn = isDenodo ? StringFunctions.handleLiteralStringDateForDenodo(arguments.get(0).toString()) : StringFunctions.handleLiteralStringDateForOracle(arguments.get(0).toString());
      arguments.set(0, dateColumn);
      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LAST_DAY");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (arguments.size() == 1) {
         this.lastDayWithOneArgForTsqls(arguments.get(0), true);
      }

   }

   private void lastDayWithOneArgForTsqls(Object obj, boolean isSQLServer) {
      this.functionName.setColumnName("DATEADD");
      SelectColumn sc = new SelectColumn();
      Vector scArguments = new Vector();
      SelectColumn scForSecondArg = new SelectColumn();
      Vector secondArgColExp = new Vector();
      SelectColumn scForThirdArg = new SelectColumn();
      Vector thirdArgForColExp = new Vector();
      TableColumn tc = new TableColumn();
      if (isSQLServer) {
         tc.setColumnName("D");
      } else {
         tc.setColumnName("DD");
      }

      scArguments.add(tc);
      sc.setColumnExpression(scArguments);
      Vector newFunctionArguments = new Vector();
      FunctionCalls subFC = new FunctionCalls();
      Vector subFCArgsForDayFn = new Vector();
      SelectColumn scForDayFn = new SelectColumn();
      TableColumn newFunctionName = new TableColumn();
      newFunctionName.setColumnName("-DAY");
      subFC.setFunctionName(newFunctionName);
      Vector scArgumentsForDayFn = new Vector();
      FunctionCalls subFCForDateAddFn = new FunctionCalls();
      TableColumn newFunctionNameForDateAddFn = new TableColumn();
      newFunctionNameForDateAddFn.setColumnName("DATEADD");
      subFCForDateAddFn.setFunctionName(newFunctionNameForDateAddFn);
      Vector subFCArgForDateAddFn = new Vector();
      SelectColumn scForDateAddFn = new SelectColumn();
      Vector scArgumentsForDateAddFn = new Vector();
      TableColumn tcForDateAddFn = new TableColumn();
      if (isSQLServer) {
         tcForDateAddFn.setColumnName("M");
      } else {
         tcForDateAddFn.setColumnName("MM");
      }

      scArgumentsForDateAddFn.add(tcForDateAddFn);
      scForDateAddFn.setColumnExpression(scArgumentsForDateAddFn);
      subFCArgForDateAddFn.add(scForDateAddFn);
      subFCArgForDateAddFn.add("1");
      subFCArgForDateAddFn.add(obj);
      subFCForDateAddFn.setFunctionArguments(subFCArgForDateAddFn);
      scArgumentsForDayFn.add(subFCForDateAddFn);
      scForDayFn.setColumnExpression(scArgumentsForDayFn);
      subFCArgsForDayFn.add(scForDayFn);
      subFC.setFunctionArguments(subFCArgsForDayFn);
      newFunctionArguments.add(sc);
      secondArgColExp.add(subFC);
      scForSecondArg.setColumnExpression(secondArgColExp);
      newFunctionArguments.add(scForSecondArg);
      thirdArgForColExp.add(subFCForDateAddFn);
      scForThirdArg.setColumnExpression(thirdArgForColExp);
      newFunctionArguments.add(scForThirdArg);
      this.setFunctionArguments(newFunctionArguments);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LAST_DAY");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (arguments.size() == 1) {
         this.lastDayWithOneArgForTsqls(arguments.get(0), false);
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String arg = null;
      String colName = null;
      if (this.functionArguments.size() == 1) {
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            arg = ((SelectColumn)this.functionArguments.elementAt(0)).toDB2Select(to_sqs, from_sqs).toString();
         } else if (this.functionArguments.elementAt(0) instanceof FunctionCalls) {
            arg = ((FunctionCalls)this.functionArguments.elementAt(0)).toDB2Select(to_sqs, from_sqs).toString();
         } else {
            arg = this.functionArguments.elementAt(0).toString();
         }

         colName = arg + " + 1 MONTH - DAY(" + arg + " + 1 MONTH) DAY";
         this.functionName.setColumnName(colName);
         this.setOpenBracesForFunctionNameRequired(false);
         Vector arguments = new Vector();
         this.setFunctionArguments(arguments);
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      boolean canUseUDFFunction = from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.date.time");

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0) {
               this.handleStringLiteralForDateTime(from_sqs, i_count, true);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (from_sqs != null && from_sqs.isAmazonRedShift()) {
         this.functionName.setColumnName("LAST_DAY");
         this.setFunctionArguments(arguments);
      } else {
         String qry = from_sqs != null && from_sqs.isVerticaDb() ? "date(date_trunc('MONTH', date(" + arguments.get(0) + ")) + INTERVAL '1 MONTH' year to month - INTERVAL '1 DAY') " : "date(date_trunc('MONTH', date(" + arguments.get(0) + ")) + INTERVAL '1 MONTH - 1 day') ";
         if (canUseUDFFunction) {
            qry = "LAST_DAY(" + arguments.get(0).toString() + ")";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LAST_DAY");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LAST_DAY");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LAST_DAY");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LAST_DAY");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nThe function LAST_DAY is not supported in TimesTen 5.1.21\n");
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CAST");
      Vector arguments = new Vector();
      if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
         SelectColumn selCol = ((SelectColumn)this.functionArguments.elementAt(0)).toNetezzaSelect(to_sqs, from_sqs);
         String target = "date_trunc('month'," + selCol.toString() + ") + interval '1 month' - interval '1 day' as date";
         arguments.add(target);
      } else {
         String target = "date_trunc('month'," + this.functionArguments.elementAt(0).toString() + ") + interval '1 month' - interval '1 day' as date";
         arguments.add(target);
      }

      this.setFunctionArguments(arguments);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LAST_DAY");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LAST_DAY");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0) {
               this.handleStringLiteralForDateTime(from_sqs, i_count, true);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
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

      String qry = "CAST(DATE_SUB(DATE_TRUNC(DATE_ADD(DATE(CAST(" + arguments.get(0) + " AS DATETIME)), INTERVAL '1' MONTH), MONTH) , INTERVAL '1' DAY) AS DATETIME)";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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

      String qry = " DATE(DATE_TRUNC('MONTH'," + StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString()) + ") + INTERVAL '1' MONTH -INTERVAL '1' DAY) ";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LAST_DAY");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      arguments.setElementAt(StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString()), 0);
      this.setFunctionArguments(arguments);
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

      String qry = "date(" + StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString()) + ",'start of month','+1 month','-1 day')";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
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

      String qry = "DATESERIAL(YEAR(" + arguments.get(0).toString() + "),MONTH(" + arguments.get(0).toString() + ")+1,0)";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LAST_DAY");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }
}
