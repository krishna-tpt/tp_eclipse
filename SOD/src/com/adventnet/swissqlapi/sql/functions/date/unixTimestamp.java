package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class unixTimestamp extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            if (selColumn.getColumnExpression() != null && !selColumn.getColumnExpression().isEmpty() && selColumn.getColumnExpression().size() == 1 && selColumn.getColumnExpression().get(0) != null && selColumn.getColumnExpression().get(0) instanceof String) {
               String stringValue = selColumn.getColumnExpression().get(0).toString();
               stringValue = "CAST(" + this.handleStringLiteralForDateTime(stringValue, from_sqs) + " AS TIMESTAMP)";
               selColumn.getColumnExpression().set(0, stringValue);
            }

            arguments.addElement(selColumn.toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String arg = "";
      if (arguments.size() > 0) {
         arg = arguments.get(0).toString();
      } else {
         arg = " now() ";
      }

      String qry = " cast(" + extract.applyDatePartOrExtractWrapper(from_sqs, "EPOCH", arg + "") + " as bigint)";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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

      if (this.functionName.getColumnName().equalsIgnoreCase("unix_timestamp")) {
         String qry = "";
         if (arguments.size() == 1) {
            qry = "DATE_PART(epoch_second, " + arguments.get(0) + " ::TIMESTAMP)";
         } else {
            qry = "DATE_PART(epoch_second,CURRENT_TIMESTAMP())";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }

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

      if (this.functionName.getColumnName().equalsIgnoreCase("unix_timestamp")) {
         String qry = "";
         if (arguments.isEmpty()) {
            qry = "DATEDIFF(SECOND,'1970-01-01',GETUTCDATE())";
         } else {
            qry = "DATEDIFF(SECOND,'1970-01-01', " + arguments.get(0).toString() + ")";
         }

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
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            arguments.addElement(selColumn.toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String arg = "";
      if (arguments.size() > 0) {
         arg = arguments.get(0).toString();
      } else {
         arg = "CURRENT_DATETIME()";
      }

      String qry = "cast(UNIX_SECONDS( CAST(" + arg + " AS TIMESTAMP)) as INT64)";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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

      String arg = "";
      if (arguments.size() > 0) {
         arg = isdenodo ? StringFunctions.handleLiteralStringDateForDenodo(arguments.get(0).toString()) : StringFunctions.handleLiteralStringDateForOracle(arguments.get(0).toString());
      } else {
         arg = isdenodo ? " CURRENT_DATE" : " SYSDATE ";
      }

      String qry = isdenodo ? "round((getTimeInMillis(" + arg + ")-getTimeInMillis(cast('1970-01-01' as date)))/(1000.0))" : "round((cast(" + arg + " as date)- DATE '1970-01-01' ) * 86400)";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            arguments.addElement(selColumn.toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String arg = "";
      if (arguments.size() > 0) {
         arg = arguments.get(0).toString();
      } else {
         arg = "CURRENT TIMESTAMP";
      }

      String qry = "(CAST(DAYS(" + arg + ") - DAYS('1970-01-01') AS INTEGER) * 86400 + MIDNIGHT_SECONDS(" + arg + "))";
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

      String arg = "";
      if (arguments.size() > 0) {
         arg = StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString());
      } else {
         arg = "CURRENT_TIMESTAMP";
      }

      String qry = "(SECONDS_BETWEEN('1970-01-01 0:00:00'," + arg + "))";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            arguments.addElement(selColumn.toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String arg = "";
      if (arguments.size() > 0) {
         arg = StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString());
      } else {
         arg = "CURRENT_TIMESTAMP";
      }

      String qry = "CAST(TO_UNIXTIME(" + arg + ") AS BIGINT)";
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

      String arg = "";
      if (arguments.size() > 0) {
         arg = StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString());
      } else {
         arg = "DATETIME()";
      }

      String qry = "(strftime('%s'," + arg + "))";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            arguments.addElement(selColumn.toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String arg = "";
      if (arguments.size() > 0) {
         arg = StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString());
      } else {
         arg = "CURRENT";
      }

      String qry = "((((EXTEND(" + arg + ", YEAR TO DAY) - DATETIME(1970-01-01) YEAR TO DAY)::INTERVAL DAY(9) TO DAY)::CHAR(15)::DECIMAL(18,5) * 86400 + ((EXTEND(" + arg + ", HOUR TO FRACTION(5)) - DATETIME(00:00:00.00000) HOUR TO FRACTION(5))::INTERVAL SECOND(6) TO FRACTION(5)::CHAR(15))::DECIMAL(18,5))::BIGINT)";
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

      if (this.functionName.getColumnName().equalsIgnoreCase("unix_timestamp")) {
         String qry = "";
         if (arguments.isEmpty()) {
            qry = "DATEDIFF('s','1970-01-01',NOW())";
         } else {
            qry = "DATEDIFF('s','1970-01-01', " + arguments.get(0).toString() + ")";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }

   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            arguments.addElement(selColumn.toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      arguments.set(0, StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString()));
      this.setFunctionArguments(arguments);
   }
}
