package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class month extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 1) {
         String datecolumn = StringFunctions.handleLiteralStringDateForOracle(arguments.get(0).toString());
         arguments.set(0, datecolumn);
      }

      this.setFunctionArguments(arguments);
      this.functionName.setColumnName("EXTRACT");
      this.setTrailingString("MONTH");
      this.setFromInTrim("FROM");
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTH");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTH");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateColumn = StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString());
      arguments.set(0, dateColumn);
      this.setFunctionArguments(arguments);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTH");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTH");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      boolean canUseUDFFunction = from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.date.time");
      if (this.functionArguments.size() == 1 && this.functionArguments.elementAt(0) instanceof SelectColumn && ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().get(0) instanceof String) {
         qry = " timestamp ";
      }

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0) {
               this.handleStringLiteralForDateTime(from_sqs, i_count, false);
               ((SelectColumn)this.functionArguments.elementAt(i_count)).modifyCurrentTimeAsCurrentTimestamp(from_sqs);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      qry = " cast(" + extract.applyDatePartOrExtractWrapper(from_sqs, "MONTH", qry + arguments.get(0) + "") + " as int)";
      if (canUseUDFFunction) {
         qry = "MONTH(" + arguments.get(0).toString() + ")";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName().toUpperCase();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionArguments.size() == 1) {
         if (from_sqs != null && from_sqs.isHyperSql()) {
            arguments.set(0, StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString()));
         }

         this.functionName.setColumnName("MONTH");
         this.setFunctionArguments(arguments);
      } else {
         String fiscalStartMonth = "";
         if (arguments.elementAt(1) instanceof SelectColumn) {
            SelectColumn sc_FiscalStartMonth = (SelectColumn)arguments.elementAt(1);
            Vector vc_FiscalStartMonth = sc_FiscalStartMonth.getColumnExpression();
            if (!(vc_FiscalStartMonth.elementAt(0) instanceof String)) {
               throw new ConvertException("Invalid Argument Value for Function " + fnStr, "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{fnStr, "FISCAL_START_MONTH"});
            }

            fiscalStartMonth = (String)vc_FiscalStartMonth.elementAt(0);
            if (fiscalStartMonth.equalsIgnoreCase("null")) {
               fiscalStartMonth = "1";
            }

            fiscalStartMonth = fiscalStartMonth.replaceAll("'", "");
            this.validateFiscalStartMonth(fiscalStartMonth, this.functionName.getColumnName().toUpperCase());
         }

         Vector Args;
         if (fiscalStartMonth.equalsIgnoreCase("1")) {
            this.functionName.setColumnName("MONTH");
            Args = new Vector();
            Args.addElement(arguments.get(0));
            this.setFunctionArguments(Args);
         } else {
            this.functionName.setColumnName("");
            Args = new Vector();
            SelectColumn sc_fiscalMonth = new SelectColumn();
            Vector vc_fiscalMonth = new Vector();
            SelectColumn sc_fiscalMonthAddend = new SelectColumn();
            Vector vc_fiscalMonthAddend = new Vector();
            SelectColumn sc_fiscalMonthDividend = new SelectColumn();
            Vector vc_fiscalMonthDividend = new Vector();
            SelectColumn sc_month = new SelectColumn();
            FunctionCalls fn_month = new FunctionCalls();
            TableColumn tb_month = new TableColumn();
            tb_month.setColumnName("MONTH");
            fn_month.setFunctionName(tb_month);
            Vector vc_monthIn = new Vector();
            Vector vc_monthOut = new Vector();
            vc_monthIn.addElement(arguments.get(0));
            fn_month.setFunctionArguments(vc_monthIn);
            vc_monthOut.addElement(fn_month);
            sc_month.setOpenBrace("(");
            sc_month.setCloseBrace(")");
            sc_month.setColumnExpression(vc_monthOut);
            vc_fiscalMonthDividend.addElement(sc_month);
            vc_fiscalMonthDividend.addElement("+");
            vc_fiscalMonthDividend.addElement("12");
            vc_fiscalMonthDividend.addElement("-");
            vc_fiscalMonthDividend.addElement("(");
            vc_fiscalMonthDividend.addElement(fiscalStartMonth);
            vc_fiscalMonthDividend.addElement(")");
            sc_fiscalMonthDividend.setOpenBrace("(");
            sc_fiscalMonthDividend.setCloseBrace(")");
            sc_fiscalMonthDividend.setColumnExpression(vc_fiscalMonthDividend);
            vc_fiscalMonthAddend.addElement(sc_fiscalMonthDividend);
            vc_fiscalMonthAddend.addElement("%");
            vc_fiscalMonthAddend.addElement("12");
            sc_fiscalMonthAddend.setOpenBrace("(");
            sc_fiscalMonthAddend.setCloseBrace(")");
            sc_fiscalMonthAddend.setColumnExpression(vc_fiscalMonthAddend);
            vc_fiscalMonth.addElement(sc_fiscalMonthAddend);
            vc_fiscalMonth.addElement("+");
            vc_fiscalMonth.addElement("1");
            sc_fiscalMonth.setColumnExpression(vc_fiscalMonth);
            Args.addElement(sc_fiscalMonth);
            this.setFunctionArguments(Args);
         }
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTH");
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
      this.functionName.setColumnName("MONTH");
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
      this.functionName.setColumnName("TO_CHAR");
      Vector arguments = new Vector();
      if (this.functionArguments.size() == 1) {
         if (this.functionArguments.get(0) instanceof SelectColumn) {
            arguments.add(((SelectColumn)this.functionArguments.get(0)).toTimesTenSelect(to_sqs, from_sqs));
         } else {
            arguments.add(this.functionArguments.get(0));
         }

         arguments.add("'MM'");
      }

      this.setFunctionArguments(arguments);
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTH");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTH");
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
      this.functionName.setColumnName("MONTH");
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
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      qry = " cast(EXTRACT(MONTH FROM CAST(" + qry + arguments.get(0) + " AS TIMESTAMP)) as INT64)";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTH");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String convDate = "CAST(" + arguments.get(0).toString() + " AS DATETIME)";
      arguments.setElementAt(convDate, 0);
      this.setFunctionArguments(arguments);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTH");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String convDate = StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString());
      arguments.setElementAt(convDate, 0);
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

      String qry = "cast(strftime('%m'," + StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString()) + ") as integer)";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTH");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTH");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      arguments.set(0, StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString()));
      this.setFunctionArguments(arguments);
   }
}
