package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class start_datetime extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      this.functionName.setColumnName("TIMESTAMP");
      Vector finalArgument = new Vector();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String units = "";
      SelectColumn sc_timeSmp = new SelectColumn();
      SelectColumn sc2;
      Vector vc;
      TableColumn tb_unit;
      if (fnStr.equalsIgnoreCase("START_DATETIME")) {
         if (arguments.elementAt(1) instanceof SelectColumn) {
            sc2 = (SelectColumn)arguments.elementAt(1);
            vc = sc2.getColumnExpression();
            if (vc.elementAt(0) instanceof TableColumn) {
               tb_unit = (TableColumn)vc.elementAt(0);
               units = tb_unit.getColumnName();
               units = units.replaceAll("'", "");
            } else {
               units = (String)vc.elementAt(0);
               units = units.replaceAll("'", "");
            }
         }

         sc_timeSmp = this.generalDateUnits(to_sqs, from_sqs, arguments, "START_DATETIME", units);
         if (sc_timeSmp == null) {
            sc_timeSmp = this.startDateTimeUnits(to_sqs, from_sqs, arguments, units);
         }
      }

      if (fnStr.equalsIgnoreCase("DATE_TRUNC")) {
         if (arguments.elementAt(0) instanceof SelectColumn) {
            sc2 = (SelectColumn)arguments.elementAt(0);
            vc = sc2.getColumnExpression();
            if (vc.elementAt(0) instanceof TableColumn) {
               tb_unit = (TableColumn)vc.elementAt(0);
               units = tb_unit.getColumnName();
               units = units.replaceAll("'", "");
            } else {
               units = (String)vc.elementAt(0);
               units = units.replaceAll("'", "");
            }
         }

         this.validateDateTruncUnits(arguments, "DATE_TRUNC");
         sc_timeSmp = this.generalDateUnits(to_sqs, from_sqs, arguments, "DATE_TRUNC", units);
         if (sc_timeSmp == null) {
            sc_timeSmp = this.dateTruncUnits(to_sqs, from_sqs, arguments, units);
         }
      }

      finalArgument.addElement(sc_timeSmp);
      this.setFunctionArguments(finalArgument);
   }

   public SelectColumn generalDateUnits(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, Vector arguments, String fnName, String units) throws ConvertException {
      int arg0 = 0;
      if (fnName.equalsIgnoreCase("DATE_TRUNC")) {
         arg0 = 1;
      }

      if (units.equalsIgnoreCase("SECOND")) {
         return this.getDateFormat(arguments.get(arg0), "'%Y-%m-%d %H:%i:%S'");
      } else if (units.equalsIgnoreCase("MINUTE")) {
         return this.getDateFormat(arguments.get(arg0), "'%Y-%m-%d %H:%i:00'");
      } else if (units.equalsIgnoreCase("HOUR")) {
         return this.getDateFormat(arguments.get(arg0), "'%Y-%m-%d %H:00:00'");
      } else if (units.equalsIgnoreCase("DAY")) {
         return this.getDateFormat(arguments.get(arg0), "'%Y-%m-%d 00:00:00'");
      } else {
         SelectColumn sc_Date;
         FunctionCalls fn_Date;
         TableColumn tb_Date;
         Vector vc_DateIn;
         Vector vc_DateOut;
         SelectColumn sc_concatws;
         TableColumn tb_concatws;
         if (!units.equalsIgnoreCase("WEEK") && !units.equalsIgnoreCase("WEEK_MONDAY")) {
            if (units.equalsIgnoreCase("MONTH")) {
               return this.getDateFormat(arguments.get(arg0), "'%Y-%m-01 00:00:00'");
            } else if (units.equalsIgnoreCase("QUARTER")) {
               sc_Date = new SelectColumn();
               fn_Date = new FunctionCalls();
               tb_Date = new TableColumn();
               tb_Date.setColumnName("DATE");
               fn_Date.setFunctionName(tb_Date);
               vc_DateIn = new Vector();
               vc_DateOut = new Vector();
               sc_concatws = new SelectColumn();
               FunctionCalls fnCall_concatws = new FunctionCalls();
               tb_concatws = new TableColumn();
               tb_concatws.setColumnName("CONCAT_WS");
               fnCall_concatws.setFunctionName(tb_concatws);
               Vector vc_concatwsIn = new Vector();
               Vector vc_concatwsOut = new Vector();
               vc_concatwsIn.add("'-'");
               SelectColumn sc_year = new SelectColumn();
               FunctionCalls fn_year = new FunctionCalls();
               TableColumn tb_year = new TableColumn();
               tb_year.setColumnName("YEAR");
               fn_year.setFunctionName(tb_year);
               Vector vc_yearIn = new Vector();
               Vector vc_yearOut = new Vector();
               vc_yearIn.addElement(arguments.get(arg0));
               fn_year.setFunctionArguments(vc_yearIn);
               vc_yearOut.addElement(fn_year);
               sc_year.setColumnExpression(vc_yearOut);
               vc_concatwsIn.addElement(sc_year);
               SelectColumn sc_value = new SelectColumn();
               Vector vc_value = new Vector();
               SelectColumn sc_quarter = new SelectColumn();
               FunctionCalls fn_quarter = new FunctionCalls();
               TableColumn tb_quarter = new TableColumn();
               tb_quarter.setColumnName("QUARTER");
               fn_quarter.setFunctionName(tb_quarter);
               Vector vc_quarterIn = new Vector();
               Vector vc_quarterOut = new Vector();
               if (arguments.elementAt(arg0) instanceof SelectColumn) {
                  vc_quarterIn.addElement(((SelectColumn)this.functionArguments.elementAt(arg0)).toMySQLSelect(to_sqs, from_sqs));
               } else {
                  vc_quarterIn.addElement(this.functionArguments.elementAt(arg0));
               }

               fn_quarter.setFunctionArguments(vc_quarterIn);
               vc_quarterOut.addElement(fn_quarter);
               sc_quarter.setColumnExpression(vc_quarterOut);
               vc_value.addElement("(");
               vc_value.addElement(sc_quarter);
               vc_value.add("*");
               vc_value.add("3");
               vc_value.addElement(")");
               vc_value.add("-");
               vc_value.add("2");
               sc_value.setOpenBrace("(");
               sc_value.setCloseBrace(")");
               sc_value.setColumnExpression(vc_value);
               vc_concatwsIn.addElement(sc_value);
               vc_concatwsIn.add("1");
               fnCall_concatws.setFunctionArguments(vc_concatwsIn);
               vc_concatwsOut.addElement(fnCall_concatws);
               sc_concatws.setColumnExpression(vc_concatwsOut);
               vc_DateIn.addElement(sc_concatws);
               fn_Date.setFunctionArguments(vc_DateIn);
               vc_DateOut.addElement(fn_Date);
               sc_Date.setColumnExpression(vc_DateOut);
               return this.getDateFormat(sc_Date, "'%Y-%m-01 00:00:00'");
            } else {
               return units.equalsIgnoreCase("YEAR") ? this.getDateFormat(arguments.get(arg0), "'%Y-01-01 00:00:00'") : null;
            }
         } else {
            sc_Date = new SelectColumn();
            fn_Date = new FunctionCalls();
            tb_Date = new TableColumn();
            tb_Date.setColumnName("DATE_SUB");
            fn_Date.setFunctionName(tb_Date);
            vc_DateIn = new Vector();
            vc_DateOut = new Vector();
            vc_DateIn.addElement(arguments.get(arg0));
            sc_concatws = new SelectColumn();
            Vector vc_intr = new Vector();
            tb_concatws = new TableColumn();
            tb_concatws.setColumnName("INTERVAL");
            vc_intr.add(tb_concatws);
            SelectColumn sc_dateWeek = new SelectColumn();
            FunctionCalls fnCall_dateWeek = new FunctionCalls();
            TableColumn tb_dateWeek = new TableColumn();
            tb_dateWeek.setColumnName("WEEKDAY");
            fnCall_dateWeek.setFunctionName(tb_dateWeek);
            Vector vc_dateWeekIn = new Vector();
            Vector vc_dateWeekOut = new Vector();
            if (arguments.elementAt(arg0) instanceof SelectColumn) {
               vc_dateWeekIn.addElement(((SelectColumn)this.functionArguments.elementAt(arg0)).toMySQLSelect(to_sqs, from_sqs));
            } else {
               vc_dateWeekIn.addElement(this.functionArguments.elementAt(arg0));
            }

            fnCall_dateWeek.setFunctionArguments(vc_dateWeekIn);
            vc_dateWeekOut.addElement(fnCall_dateWeek);
            sc_dateWeek.setColumnExpression(vc_dateWeekOut);
            vc_intr.addElement(sc_dateWeek);
            vc_intr.add(" DAY");
            sc_concatws.setColumnExpression(vc_intr);
            vc_DateIn.addElement(sc_concatws);
            fn_Date.setFunctionArguments(vc_DateIn);
            vc_DateOut.addElement(fn_Date);
            sc_Date.setColumnExpression(vc_DateOut);
            return this.getDateFormat(sc_Date, "'%Y-%m-%d 00:00:00'");
         }
      }
   }

   public SelectColumn dateTruncUnits(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, Vector arguments, String units) throws ConvertException {
      if (units.equalsIgnoreCase("MILLISECOND")) {
         SelectColumn sc_dateSub = new SelectColumn();
         FunctionCalls fn_dateSub = new FunctionCalls();
         TableColumn tb_dateSub = new TableColumn();
         tb_dateSub.setColumnName("DATE_SUB");
         fn_dateSub.setFunctionName(tb_dateSub);
         Vector vc_dateSubIn = new Vector();
         Vector vc_dateSubOut = new Vector();
         vc_dateSubIn.addElement(arguments.get(1));
         SelectColumn sc_interval = new SelectColumn();
         Vector vc_interval = new Vector();
         TableColumn tc_interval = new TableColumn();
         tc_interval.setColumnName("INTERVAL");
         vc_interval.addElement(tc_interval);
         SelectColumn sc_values = new SelectColumn();
         Vector vc_values = new Vector();
         SelectColumn sc_microsecond = new SelectColumn();
         FunctionCalls fn_microsecond = new FunctionCalls();
         TableColumn tb_microsecond = new TableColumn();
         tb_microsecond.setColumnName("MICROSECOND");
         fn_microsecond.setFunctionName(tb_microsecond);
         Vector vc_microsecondIn = new Vector();
         Vector vc_microsecondOut = new Vector();
         if (arguments.elementAt(1) instanceof SelectColumn) {
            vc_microsecondIn.addElement(((SelectColumn)this.functionArguments.elementAt(1)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vc_microsecondIn.addElement(this.functionArguments.elementAt(1));
         }

         fn_microsecond.setFunctionArguments(vc_microsecondIn);
         vc_microsecondOut.addElement(fn_microsecond);
         sc_microsecond.setColumnExpression(vc_microsecondOut);
         vc_values.addElement(sc_microsecond);
         vc_values.addElement("%");
         vc_values.addElement("1000");
         sc_values.setOpenBrace("(");
         sc_values.setCloseBrace(")");
         sc_values.setColumnExpression(vc_values);
         vc_interval.addElement(sc_values);
         vc_interval.add("MICROSECOND");
         sc_interval.setColumnExpression(vc_interval);
         vc_dateSubIn.addElement(sc_interval);
         fn_dateSub.setFunctionArguments(vc_dateSubIn);
         vc_dateSubOut.addElement(fn_dateSub);
         sc_dateSub.setColumnExpression(vc_dateSubOut);
         return sc_dateSub;
      } else if (units.equalsIgnoreCase("MICROSECOND")) {
         return this.getDateFormat(arguments.get(1), "'%Y-%m-%d %H:%i:%S.%f'");
      } else if (units.equalsIgnoreCase("DECADE")) {
         return this.getDateFormat(this.getConvertedDateTrunc(to_sqs, from_sqs, "DECADE"), "'%Y-01-01 00:00:00'");
      } else if (units.equalsIgnoreCase("CENTURY")) {
         return this.getDateFormat(this.getConvertedDateTrunc(to_sqs, from_sqs, "CENTURY"), "'%Y-01-01 00:00:00'");
      } else {
         return units.equalsIgnoreCase("MILLENNIUM") ? this.getDateFormat(this.getConvertedDateTrunc(to_sqs, from_sqs, "MILLENNIUM"), "'%Y-01-01 00:00:00'") : null;
      }
   }

   public SelectColumn startDateTimeUnits(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, Vector arguments, String units) throws ConvertException {
      if (units.equalsIgnoreCase("WEEK_SUNDAY")) {
         SelectColumn sc_dateSub = new SelectColumn();
         FunctionCalls fnCall_dateSub = new FunctionCalls();
         TableColumn tb_dateSub = new TableColumn();
         tb_dateSub.setColumnName("DATE_SUB");
         fnCall_dateSub.setFunctionName(tb_dateSub);
         Vector vc_dateSubIn = new Vector();
         Vector vc_dateSubOut = new Vector();
         vc_dateSubIn.addElement(arguments.get(0));
         SelectColumn sc_intr = new SelectColumn();
         Vector vc_intr = new Vector();
         TableColumn tbCl_intr = new TableColumn();
         tbCl_intr.setColumnName("INTERVAL");
         vc_intr.add(tbCl_intr);
         SelectColumn sc_value = new SelectColumn();
         Vector vc_value = new Vector();
         SelectColumn sc_dateWeek = new SelectColumn();
         FunctionCalls fnCall_dateWeek = new FunctionCalls();
         TableColumn tb_dateWeek = new TableColumn();
         tb_dateWeek.setColumnName("DAYOFWEEK");
         fnCall_dateWeek.setFunctionName(tb_dateWeek);
         Vector vc_dateWeekIn = new Vector();
         Vector vc_dateWeekOut = new Vector();
         if (arguments.elementAt(0) instanceof SelectColumn) {
            vc_dateWeekIn.addElement(((SelectColumn)this.functionArguments.elementAt(0)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vc_dateWeekIn.addElement(this.functionArguments.elementAt(0));
         }

         fnCall_dateWeek.setFunctionArguments(vc_dateWeekIn);
         vc_dateWeekOut.addElement(fnCall_dateWeek);
         sc_dateWeek.setColumnExpression(vc_dateWeekOut);
         vc_value.addElement(sc_dateWeek);
         vc_value.add("-");
         vc_value.add("1");
         sc_value.setOpenBrace("(");
         sc_value.setCloseBrace(")");
         sc_value.setColumnExpression(vc_value);
         vc_intr.addElement(sc_value);
         vc_intr.add(" DAY");
         sc_intr.setColumnExpression(vc_intr);
         vc_dateSubIn.addElement(sc_intr);
         fnCall_dateSub.setFunctionArguments(vc_dateSubIn);
         vc_dateSubOut.addElement(fnCall_dateSub);
         sc_dateSub.setColumnExpression(vc_dateSubOut);
         return this.getDateFormat(sc_dateSub, "'%Y-%m-%d 00:00:00'");
      } else {
         throw new ConvertException("Invalid Argument Value for Function START_DATETIME", "INVALID_ARGUMENT_VALUE", new Object[]{"START_DATETIME", "DATE_UNITS", "Provide any one of the following value week, week_sunday, week_monday, month, quarter or year"});
      }
   }

   public SelectColumn getConvertedDateTrunc(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, String unit) throws ConvertException {
      Vector fn_arguments = new Vector();
      SelectColumn sc_DateSub = new SelectColumn();
      FunctionCalls fn_DateSub = new FunctionCalls();
      TableColumn tb_DateSub = new TableColumn();
      tb_DateSub.setColumnName("DATE_SUB");
      fn_DateSub.setFunctionName(tb_DateSub);
      Vector vc_DateSubIn = new Vector();
      Vector vc_DateSubOut = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            fn_arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            fn_arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      vc_DateSubIn.addElement(fn_arguments.get(1));
      if (unit.equalsIgnoreCase("DECADE")) {
         vc_DateSubIn.addElement(this.getYearIntervalParam(fn_arguments, "10", true));
      } else if (unit.equalsIgnoreCase("CENTURY")) {
         vc_DateSubIn.addElement(this.getYearIntervalParam(fn_arguments, "100", false));
      } else if (unit.equalsIgnoreCase("MILLENNIUM")) {
         vc_DateSubIn.addElement(this.getYearIntervalParam(fn_arguments, "1000", false));
      }

      fn_DateSub.setFunctionArguments(vc_DateSubIn);
      vc_DateSubOut.addElement(fn_DateSub);
      sc_DateSub.setColumnExpression(vc_DateSubOut);
      return sc_DateSub;
   }

   public SelectColumn getYearIntervalParam(Vector fn_arguments, String value, boolean isDecade) {
      SelectColumn sc_interval = new SelectColumn();
      Vector vc_interval = new Vector();
      TableColumn tbCl_interval = new TableColumn();
      tbCl_interval.setColumnName("Interval");
      vc_interval.addElement(tbCl_interval);
      SelectColumn sc_value = new SelectColumn();
      Vector vc_value = new Vector();
      SelectColumn sc_year = new SelectColumn();
      FunctionCalls fn_year = new FunctionCalls();
      TableColumn tb_year = new TableColumn();
      tb_year.setColumnName("YEAR");
      fn_year.setFunctionName(tb_year);
      Vector vc_YearIn = new Vector();
      Vector vc_YearOut = new Vector();
      vc_YearIn.add(fn_arguments.elementAt(1));
      fn_year.setFunctionArguments(vc_YearIn);
      vc_YearOut.add(fn_year);
      sc_year.setOpenBrace("(");
      sc_year.setCloseBrace(")");
      sc_year.setColumnExpression(vc_YearOut);
      vc_value.addElement(sc_year);
      vc_value.add("%");
      vc_value.add(value);
      sc_value.setOpenBrace("(");
      sc_value.setCloseBrace(")");
      if (!isDecade) {
         vc_value.add("-");
         vc_value.add("1");
      }

      sc_value.setColumnExpression(vc_value);
      vc_interval.addElement(sc_value);
      vc_interval.add("YEAR");
      sc_interval.setColumnExpression(vc_interval);
      return sc_interval;
   }

   public SelectColumn getDateFormat(Object arg, String format) {
      SelectColumn sc_dateFmt = new SelectColumn();
      FunctionCalls fnCall_dateFmt = new FunctionCalls();
      TableColumn tb_dateFmt = new TableColumn();
      tb_dateFmt.setColumnName("DATE_FORMAT");
      fnCall_dateFmt.setFunctionName(tb_dateFmt);
      Vector vc_dateFmtIn = new Vector();
      Vector vc_dateFmtOut = new Vector();
      vc_dateFmtIn.addElement(arg);
      vc_dateFmtIn.addElement(format);
      fnCall_dateFmt.setFunctionArguments(vc_dateFmtIn);
      vc_dateFmtOut.addElement(fnCall_dateFmt);
      sc_dateFmt.setColumnExpression(vc_dateFmtOut);
      return sc_dateFmt;
   }
}
