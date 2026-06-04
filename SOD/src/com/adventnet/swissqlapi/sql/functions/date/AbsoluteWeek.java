package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class AbsoluteWeek extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName().toUpperCase();
      Vector vector1 = new Vector();
      Vector vector2 = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector1.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            vector2.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vector1.addElement(this.functionArguments.elementAt(i_count));
            vector2.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String weekStartDay = null;
      String weekMode = null;
      String fiscalStartMonth = null;
      int fiscalStartMonth_int = false;
      int weekMode_int = false;
      int weekStartDay_int = false;
      boolean isJanFiscalStMonth = false;
      boolean isISOWeekMode = false;
      this.functionName.setColumnName("CONCAT");
      Vector concatArguments = new Vector();
      concatArguments.addElement("'W'");
      SelectColumn sc_weekNum = new SelectColumn();
      Vector vc_weekNum = new Vector();
      SelectColumn sc_weekNumArgs = new SelectColumn();
      Vector vc_weekNumArgs = new Vector();
      SelectColumn sc_absoluteWeek = null;
      SelectColumn sc_absoluteYear = null;
      SelectColumn sc;
      Vector vc;
      if (vector1.size() == 4) {
         if (vector1.elementAt(3) instanceof SelectColumn) {
            sc = (SelectColumn)vector1.elementAt(3);
            vc = sc.getColumnExpression();
            if (!(vc.elementAt(0) instanceof String)) {
               throw new ConvertException("Invalid Argument Value for Function ABSWEEK", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"ABSWEEK", "FISCAL_START_MONTH"});
            }

            fiscalStartMonth = (String)vc.elementAt(0);
            if (fiscalStartMonth.equalsIgnoreCase("null")) {
               fiscalStartMonth = "1";
            }

            fiscalStartMonth = fiscalStartMonth.replaceAll("'", "");
            this.validateFiscalStartMonth(fiscalStartMonth, fnStr);
         }

         if (fiscalStartMonth.equals("1")) {
            isJanFiscalStMonth = true;
         } else {
            int fiscalStartMonth_int = Integer.parseInt(fiscalStartMonth);
            if (vector1.elementAt(2) instanceof SelectColumn) {
               sc = (SelectColumn)vector1.elementAt(2);
               vc = sc.getColumnExpression();
               if (!(vc.elementAt(0) instanceof String)) {
                  throw new ConvertException("Invalid Argument Value for Function ABSWEEK", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"ABSWEEK", "WEEK_MODE"});
               }

               weekMode = (String)vc.elementAt(0);
               if (weekMode.equalsIgnoreCase("null")) {
                  weekMode = "1";
               }

               weekMode = weekMode.replaceAll("'", "");
               this.validateWeekMode(weekMode, fnStr);
            }

            if (weekMode.equals("2")) {
               sc_absoluteWeek = this.WeekMode2FiscalWeeKAndYear_Query(to_sqs, from_sqs, fiscalStartMonth);
               sc_absoluteYear = this.WeekMode2FiscalWeeKAndYear_Query(to_sqs, from_sqs, fiscalStartMonth);
            } else {
               if (vector1.elementAt(1) instanceof SelectColumn) {
                  sc = (SelectColumn)vector1.elementAt(1);
                  vc = sc.getColumnExpression();
                  if (!(vc.elementAt(0) instanceof String)) {
                     throw new ConvertException("Invalid Argument Value for Function ABSWEEK", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"ABSWEEK", "WEEK_START_DAY"});
                  }

                  weekStartDay = (String)vc.elementAt(0);
                  if (weekStartDay.equalsIgnoreCase("null")) {
                     weekStartDay = "1";
                  }

                  weekStartDay = weekStartDay.replaceAll("'", "");
                  this.validateWeek_Start_Day(weekStartDay, fnStr);
               }

               sc_absoluteWeek = this.WeekMode1Args4(vector1, weekStartDay, fiscalStartMonth);
               sc_absoluteYear = this.WeekMode1Args4(vector2, weekStartDay, fiscalStartMonth);
            }
         }
      }

      if (vector1.size() == 3 || isJanFiscalStMonth) {
         if (vector1.elementAt(2) instanceof SelectColumn) {
            sc = (SelectColumn)vector1.elementAt(2);
            vc = sc.getColumnExpression();
            if (!(vc.elementAt(0) instanceof String)) {
               throw new ConvertException("Invalid Argument Value for Function ABSWEEK", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"ABSWEEK", "WEEK_MODE"});
            }

            weekMode = (String)vc.elementAt(0);
            if (weekMode.equalsIgnoreCase("null")) {
               weekMode = "1";
            }

            weekMode = weekMode.replaceAll("'", "");
            this.validateWeekMode(weekMode, fnStr);
         }

         if (weekMode.equals("2")) {
            sc_absoluteWeek = this.WeekMode2WeeKAndYear_Query(to_sqs, from_sqs);
            sc_absoluteYear = this.WeekMode2WeeKAndYear_Query(to_sqs, from_sqs);
         } else {
            isISOWeekMode = true;
         }
      }

      if (vector1.size() == 2 || isISOWeekMode) {
         if (vector1.elementAt(1) instanceof SelectColumn) {
            sc = (SelectColumn)vector1.elementAt(1);
            vc = sc.getColumnExpression();
            if (!(vc.elementAt(0) instanceof String)) {
               throw new ConvertException("Invalid Argument Value for Function ABSWEEK", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"ABSWEEK", "WEEK_START_DAY"});
            }

            weekStartDay = (String)vc.elementAt(0);
            if (weekStartDay.equalsIgnoreCase("null")) {
               weekStartDay = "1";
            }

            weekStartDay = weekStartDay.replaceAll("'", "");
            this.validateWeek_Start_Day(weekStartDay, fnStr);
         }

         sc_absoluteWeek = this.WeekMode1Args2(vector1, weekStartDay);
         sc_absoluteYear = this.WeekMode1Args2(vector2, weekStartDay);
      }

      if (vector1.size() == 1) {
         sc_absoluteWeek = this.WeekMode1Args2(vector1, "1");
         sc_absoluteYear = this.WeekMode1Args2(vector2, "1");
      }

      sc = new SelectColumn();
      vc = new Vector();
      Vector vc_ConvertWeekNumtoSignedOut = new Vector();
      FunctionCalls fnCl_ConvertWeekNumtoSigned = new FunctionCalls();
      TableColumn tbCl_ConvertWeekNumtoSigned = new TableColumn();
      tbCl_ConvertWeekNumtoSigned.setColumnName("CONVERT");
      fnCl_ConvertWeekNumtoSigned.setFunctionName(tbCl_ConvertWeekNumtoSigned);
      vc.addElement(sc_absoluteWeek);
      vc.addElement("SIGNED");
      fnCl_ConvertWeekNumtoSigned.setFunctionArguments(vc);
      vc_ConvertWeekNumtoSignedOut.addElement(fnCl_ConvertWeekNumtoSigned);
      sc.setColumnExpression(vc_ConvertWeekNumtoSignedOut);
      vc_weekNumArgs.addElement(sc);
      vc_weekNumArgs.addElement("%");
      vc_weekNumArgs.addElement("100");
      sc_weekNumArgs.setOpenBrace("(");
      sc_weekNumArgs.setCloseBrace(")");
      sc_weekNumArgs.setColumnExpression(vc_weekNumArgs);
      vc_weekNum.addElement(sc_weekNumArgs);
      sc_weekNum.setColumnExpression(vc_weekNum);
      concatArguments.addElement(sc_weekNum);
      concatArguments.addElement("', '");
      SelectColumn sc_FloorTheYearNum = new SelectColumn();
      Vector vc_FloorTheYearNumIn = new Vector();
      Vector vc_FloorTheYearNumOut = new Vector();
      FunctionCalls fnCl_FloorTheYearNum = new FunctionCalls();
      TableColumn tbCl_FloorTheYearNum = new TableColumn();
      tbCl_FloorTheYearNum.setColumnName("FLOOR");
      fnCl_FloorTheYearNum.setFunctionName(tbCl_FloorTheYearNum);
      SelectColumn sc_FloorTheYearNumArgs = new SelectColumn();
      Vector vc_FloorTheYearNumArgs = new Vector();
      vc_FloorTheYearNumArgs.addElement(sc_absoluteYear);
      vc_FloorTheYearNumArgs.addElement("/");
      vc_FloorTheYearNumArgs.addElement("100");
      sc_FloorTheYearNumArgs.setColumnExpression(vc_FloorTheYearNumArgs);
      vc_FloorTheYearNumIn.addElement(sc_FloorTheYearNumArgs);
      fnCl_FloorTheYearNum.setFunctionArguments(vc_FloorTheYearNumIn);
      vc_FloorTheYearNumOut.addElement(fnCl_FloorTheYearNum);
      sc_FloorTheYearNum.setColumnExpression(vc_FloorTheYearNumOut);
      if (vector1.size() == 4) {
         concatArguments.addElement("'FY '");
      }

      concatArguments.addElement(sc_FloorTheYearNum);
      this.setFunctionArguments(concatArguments);
   }

   public SelectColumn WeekMode1Args2(Vector vector, String weekStDay) {
      int weekStDay_int = Integer.parseInt(weekStDay);
      SelectColumn sc_yearWeek = new SelectColumn();
      Vector vc_yearWeekIn = new Vector();
      Vector vc_yearWeekOut = new Vector();
      FunctionCalls fnCl_yearWeek = new FunctionCalls();
      TableColumn tbCl_yearWeek = new TableColumn();
      vc_yearWeekIn.addElement(vector.get(0));
      if (weekStDay_int == 1) {
         tbCl_yearWeek.setColumnName("YEARWEEK");
         vc_yearWeekIn.addElement("6");
      } else if (weekStDay_int == 2) {
         tbCl_yearWeek.setColumnName("YEARWEEK");
         vc_yearWeekIn.addElement("3");
      } else if (weekStDay_int > 2 && weekStDay_int < 8) {
         tbCl_yearWeek.setColumnName("ZR_WEEKYEARDTNWKSTRTDAY");
         weekStDay_int = getWeekStartDayValue(1, weekStDay_int);
         weekStDay = Integer.toString(weekStDay_int);
         vc_yearWeekIn.addElement(weekStDay);
      }

      fnCl_yearWeek.setFunctionName(tbCl_yearWeek);
      fnCl_yearWeek.setFunctionArguments(vc_yearWeekIn);
      vc_yearWeekOut.addElement(fnCl_yearWeek);
      sc_yearWeek.setColumnExpression(vc_yearWeekOut);
      sc_yearWeek.setOpenBrace("(");
      sc_yearWeek.setCloseBrace(")");
      return sc_yearWeek;
   }

   public SelectColumn WeekMode1Args4(Vector vector, String weekStDay, String fiscalStartMonth) {
      SelectColumn sc_fiscalYearWeek = new SelectColumn();
      FunctionCalls fn_fiscalYearWeek = new FunctionCalls();
      TableColumn tb_fiscalYearWeek = new TableColumn();
      Vector vc_fiscalYearWeekIn = new Vector();
      Vector vc_fiscalYearWeekOut = new Vector();
      vc_fiscalYearWeekIn.addElement(vector.get(0));
      vc_fiscalYearWeekIn.addElement(fiscalStartMonth);
      int startMonth = Integer.parseInt(fiscalStartMonth);
      String startDate = startMonth < 10 ? "'-0" + startMonth + "-01'" : "'-" + startMonth + "-01'";
      vc_fiscalYearWeekIn.addElement(startDate);
      int fiscalStartMonth_int = Integer.parseInt(fiscalStartMonth);
      int weekStDay_int = Integer.parseInt(weekStDay);
      if (weekStDay.equalsIgnoreCase("0")) {
         tb_fiscalYearWeek.setColumnName("ZR_FWEEKYEARDT");
      } else {
         tb_fiscalYearWeek.setColumnName("ZR_FWEEKYEARDTNWKSTRTDAY");
         weekStDay_int = getWeekStartDayValue(fiscalStartMonth_int, weekStDay_int);
         weekStDay = Integer.toString(weekStDay_int);
         vc_fiscalYearWeekIn.addElement(weekStDay);
      }

      vc_fiscalYearWeekIn.addElement("100");
      fn_fiscalYearWeek.setFunctionName(tb_fiscalYearWeek);
      fn_fiscalYearWeek.setFunctionArguments(vc_fiscalYearWeekIn);
      vc_fiscalYearWeekOut.addElement(fn_fiscalYearWeek);
      sc_fiscalYearWeek.setColumnExpression(vc_fiscalYearWeekOut);
      sc_fiscalYearWeek.setOpenBrace("(");
      sc_fiscalYearWeek.setCloseBrace(")");
      return sc_fiscalYearWeek;
   }

   public SelectColumn WeekMode2WeeKAndYear_Query(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector vector1 = new Vector();
      Vector vector2 = new Vector();
      Vector vector3 = new Vector();

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

      String weekStartDay = null;
      SelectColumn sc_WM2WeekandYear;
      Vector vc_WM2WeekandYear;
      if (vector1.elementAt(1) instanceof SelectColumn) {
         sc_WM2WeekandYear = (SelectColumn)vector1.elementAt(1);
         vc_WM2WeekandYear = sc_WM2WeekandYear.getColumnExpression();
         if (!(vc_WM2WeekandYear.elementAt(0) instanceof String)) {
            throw new ConvertException("Invalid Argument Value for Function ABSWEEK", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"ABSWEEK", "WEEK_START_DAY"});
         }

         weekStartDay = (String)vc_WM2WeekandYear.elementAt(0);
      }

      if (weekStartDay.equalsIgnoreCase("null")) {
         weekStartDay = "1";
      }

      weekStartDay = weekStartDay.replaceAll("'", "");
      this.validateWeek_Start_Day(weekStartDay, "ABSWEEK");
      sc_WM2WeekandYear = new SelectColumn();
      vc_WM2WeekandYear = new Vector();
      SelectColumn sc_yearWeekFloor = new SelectColumn();
      Vector vc_yearWeekFloor = new Vector();
      SelectColumn sc_year = new SelectColumn();
      FunctionCalls fn_year = new FunctionCalls();
      TableColumn tb_year = new TableColumn();
      tb_year.setColumnName("YEAR");
      fn_year.setFunctionName(tb_year);
      Vector vc_yearIn = new Vector();
      Vector vc_yearOut = new Vector();
      vc_yearIn.addElement(vector1.get(0));
      fn_year.setFunctionArguments(vc_yearIn);
      vc_yearOut.addElement(fn_year);
      sc_year.setColumnExpression(vc_yearOut);
      vc_yearWeekFloor.addElement(sc_year);
      vc_yearWeekFloor.addElement("*");
      vc_yearWeekFloor.addElement("100");
      sc_yearWeekFloor.setColumnExpression(vc_yearWeekFloor);
      vc_WM2WeekandYear.addElement(sc_yearWeekFloor);
      vc_WM2WeekandYear.addElement("+");
      SelectColumn sc_ceil = new SelectColumn();
      FunctionCalls fnCl_ceil = new FunctionCalls();
      TableColumn tbCl_ceil = new TableColumn();
      tbCl_ceil.setColumnName("CEIL");
      fnCl_ceil.setFunctionName(tbCl_ceil);
      Vector vc_ceilIn = new Vector();
      Vector vc_ceilOut = new Vector();
      SelectColumn sc_ceilArgsDividend = new SelectColumn();
      Vector vc_ceilArgsDividend = new Vector();
      SelectColumn sc_ceilArgsMultiplicand = new SelectColumn();
      Vector vc_ceilArgsMultiplicand = new Vector();
      SelectColumn sc_partsofMultiplicand = new SelectColumn();
      Vector vc_partsofMultiplicand = new Vector();
      SelectColumn sc_ceilArgsAddend = new SelectColumn();
      FunctionCalls fn_ceilArgsAddend = new FunctionCalls();
      TableColumn tb_ceilArgsAddend = new TableColumn();
      tb_ceilArgsAddend.setColumnName("DAYOFYEAR");
      fn_ceilArgsAddend.setFunctionName(tb_ceilArgsAddend);
      Vector vc_ceilArgsAddendIn = new Vector();
      vc_ceilArgsAddendIn.addElement(vector2.get(0));
      Vector vc_ceilArgsAddendOut = new Vector();
      fn_ceilArgsAddend.setFunctionArguments(vc_ceilArgsAddendIn);
      vc_ceilArgsAddendOut.addElement(fn_ceilArgsAddend);
      sc_ceilArgsAddend.setColumnExpression(vc_ceilArgsAddendOut);
      SelectColumn sc_ceilArgsAdded = new SelectColumn();
      Vector vc_ceilArgsAdded = new Vector();
      SelectColumn sc_ceilArgsAddedDividend = new SelectColumn();
      Vector vc_ceilArgsAddedDividend = new Vector();
      SelectColumn sc_dayofweek = new SelectColumn();
      FunctionCalls fn_dayofweek = new FunctionCalls();
      TableColumn tb_dayofweek = new TableColumn();
      tb_dayofweek.setColumnName("DAYOFWEEK");
      fn_dayofweek.setFunctionName(tb_dayofweek);
      Vector vc_dayofweekIn = new Vector();
      Vector vc_dayofweekOut = new Vector();
      SelectColumn sc_strtodate = new SelectColumn();
      FunctionCalls fn_strtodate = new FunctionCalls();
      TableColumn tb_strtodate = new TableColumn();
      tb_strtodate.setColumnName("STR_TO_DATE");
      fn_strtodate.setFunctionName(tb_strtodate);
      Vector vc_strtodateIn = new Vector();
      Vector vc_strtodateOut = new Vector();
      SelectColumn sc_concat = new SelectColumn();
      FunctionCalls fn_concat = new FunctionCalls();
      TableColumn tb_concat = new TableColumn();
      tb_concat.setColumnName("CONCAT");
      fn_concat.setFunctionName(tb_concat);
      Vector vc_concatIn = new Vector();
      Vector vc_concatOut = new Vector();
      SelectColumn sc_yearInCeilArgs = new SelectColumn();
      FunctionCalls fn_yearInCeilArgs = new FunctionCalls();
      TableColumn tb_yearInCeilArgs = new TableColumn();
      tb_yearInCeilArgs.setColumnName("YEAR");
      fn_yearInCeilArgs.setFunctionName(tb_yearInCeilArgs);
      Vector vc_yearInCeilArgsIn = new Vector();
      Vector vc_yearInCeilArgsOut = new Vector();
      vc_yearInCeilArgsIn.addElement(vector3.get(0));
      fn_yearInCeilArgs.setFunctionArguments(vc_yearInCeilArgsIn);
      vc_yearInCeilArgsOut.addElement(fn_yearInCeilArgs);
      sc_yearInCeilArgs.setColumnExpression(vc_yearInCeilArgsOut);
      vc_concatIn.addElement(sc_yearInCeilArgs);
      vc_concatIn.addElement("'-01-01'");
      fn_concat.setFunctionArguments(vc_concatIn);
      vc_concatOut.addElement(fn_concat);
      sc_concat.setColumnExpression(vc_concatOut);
      vc_strtodateIn.addElement(sc_concat);
      vc_strtodateIn.addElement("'%Y-%m-%d'");
      fn_strtodate.setFunctionArguments(vc_strtodateIn);
      vc_strtodateOut.addElement(fn_strtodate);
      sc_strtodate.setColumnExpression(vc_strtodateOut);
      vc_dayofweekIn.addElement(sc_strtodate);
      fn_dayofweek.setFunctionArguments(vc_dayofweekIn);
      vc_dayofweekOut.addElement(fn_dayofweek);
      sc_dayofweek.setColumnExpression(vc_dayofweekOut);
      vc_ceilArgsAddedDividend.addElement(sc_dayofweek);
      vc_ceilArgsAddedDividend.addElement("+");
      vc_ceilArgsAddedDividend.addElement("(");
      vc_ceilArgsAddedDividend.addElement("7");
      vc_ceilArgsAddedDividend.addElement("-");
      vc_ceilArgsAddedDividend.addElement(weekStartDay);
      vc_ceilArgsAddedDividend.addElement(")");
      sc_ceilArgsAddedDividend.setColumnExpression(vc_ceilArgsAddedDividend);
      vc_ceilArgsAdded.addElement("(");
      sc_ceilArgsAddedDividend.setOpenBrace("(");
      sc_ceilArgsAddedDividend.setCloseBrace(")");
      vc_ceilArgsAdded.addElement(sc_ceilArgsAddedDividend);
      vc_ceilArgsAdded.addElement("%");
      vc_ceilArgsAdded.addElement("7");
      vc_ceilArgsAdded.addElement(")");
      sc_ceilArgsAdded.setColumnExpression(vc_ceilArgsAdded);
      vc_partsofMultiplicand.addElement(sc_ceilArgsAddend);
      vc_partsofMultiplicand.addElement("+");
      vc_partsofMultiplicand.addElement(sc_ceilArgsAdded);
      sc_partsofMultiplicand.setColumnExpression(vc_partsofMultiplicand);
      vc_ceilArgsMultiplicand.addElement("(");
      vc_ceilArgsMultiplicand.addElement(sc_partsofMultiplicand);
      vc_ceilArgsMultiplicand.addElement(")");
      vc_ceilArgsMultiplicand.addElement("*");
      vc_ceilArgsMultiplicand.addElement("1.0");
      sc_ceilArgsMultiplicand.setColumnExpression(vc_ceilArgsMultiplicand);
      vc_ceilArgsDividend.addElement("(");
      vc_ceilArgsDividend.addElement(sc_ceilArgsMultiplicand);
      vc_ceilArgsDividend.addElement(")");
      vc_ceilArgsDividend.addElement("/");
      vc_ceilArgsDividend.addElement("7");
      sc_ceilArgsDividend.setColumnExpression(vc_ceilArgsDividend);
      vc_ceilIn.addElement(sc_ceilArgsDividend);
      fnCl_ceil.setFunctionArguments(vc_ceilIn);
      vc_ceilOut.addElement(fnCl_ceil);
      sc_ceil.setColumnExpression(vc_ceilOut);
      vc_WM2WeekandYear.addElement(sc_ceil);
      sc_WM2WeekandYear.setColumnExpression(vc_WM2WeekandYear);
      sc_WM2WeekandYear.setOpenBrace("(");
      sc_WM2WeekandYear.setCloseBrace(")");
      return sc_WM2WeekandYear;
   }

   public SelectColumn WeekMode2FiscalWeeKAndYear_Query(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, String fiscalStartMonth) throws ConvertException {
      SelectColumn sc_WM2FiscalWeekAndYear = new SelectColumn();
      Vector vc_WM2FiscalWeekAndYear = new Vector();
      Vector vector1 = new Vector();
      Vector vector2 = new Vector();
      Vector vector3 = new Vector();
      Vector vector4 = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector1.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            vector2.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            vector3.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            vector4.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vector1.addElement(this.functionArguments.elementAt(i_count));
            vector2.addElement(this.functionArguments.elementAt(i_count));
            vector3.addElement(this.functionArguments.elementAt(i_count));
            vector4.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String weekStartDay = "";
      SelectColumn sc_ifYearWeek;
      Vector vc_ifYearWeek;
      if (vector1.elementAt(1) instanceof SelectColumn) {
         sc_ifYearWeek = (SelectColumn)vector1.elementAt(1);
         vc_ifYearWeek = sc_ifYearWeek.getColumnExpression();
         if (!(vc_ifYearWeek.elementAt(0) instanceof String)) {
            throw new ConvertException("Invalid Argument Value for Function ABSWEEK", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"ABSWEEK", "WEEK_START_DAY"});
         }

         weekStartDay = (String)vc_ifYearWeek.elementAt(0);
      }

      if (weekStartDay.equalsIgnoreCase("null")) {
         weekStartDay = "1";
      }

      weekStartDay = weekStartDay.replaceAll("'", "");
      this.validateWeek_Start_Day(weekStartDay, "ABSWEEK");
      sc_ifYearWeek = new SelectColumn();
      vc_ifYearWeek = new Vector();
      SelectColumn sc_ifYearWeekFloor = new SelectColumn();
      FunctionCalls fn_ifYearWeekFloor = new FunctionCalls();
      TableColumn tb_ifYearWeekFloor = new TableColumn();
      tb_ifYearWeekFloor.setColumnName("IF");
      fn_ifYearWeekFloor.setFunctionName(tb_ifYearWeekFloor);
      Vector vc_ifYearWeekFloorIn = new Vector();
      Vector vc_ifYearWeekFloorOut = new Vector();
      SelectColumn sc_ifYWFCon = new SelectColumn();
      Vector vc_ifYWFCon = new Vector();
      WhereItem whIt_YWCon = new WhereItem();
      WhereColumn whCol_YWConLeftExp = new WhereColumn();
      Vector vc_YWConLeftExp = new Vector();
      SelectColumn sc_yearWeekMonth = new SelectColumn();
      FunctionCalls fn_yearWeekMonth = new FunctionCalls();
      TableColumn tb_yearWeekMonth = new TableColumn();
      tb_yearWeekMonth.setColumnName("MONTH");
      fn_yearWeekMonth.setFunctionName(tb_yearWeekMonth);
      Vector vc_yearWeekMonthIn = new Vector();
      Vector vc_yearWeekMonthOut = new Vector();
      vc_yearWeekMonthIn.addElement(vector1.get(0));
      fn_yearWeekMonth.setFunctionArguments(vc_yearWeekMonthIn);
      vc_yearWeekMonthOut.addElement(fn_yearWeekMonth);
      sc_yearWeekMonth.setColumnExpression(vc_yearWeekMonthOut);
      vc_YWConLeftExp.addElement(sc_yearWeekMonth);
      whCol_YWConLeftExp.setColumnExpression(vc_YWConLeftExp);
      whIt_YWCon.setLeftWhereExp(whCol_YWConLeftExp);
      whIt_YWCon.setOperator("<");
      WhereColumn whCol_YWConRightExp = new WhereColumn();
      Vector vc_YWConRightExp = new Vector();
      vc_YWConRightExp.addElement(fiscalStartMonth);
      whCol_YWConRightExp.setColumnExpression(vc_YWConRightExp);
      whIt_YWCon.setRightWhereExp(whCol_YWConRightExp);
      vc_ifYWFCon.addElement(whIt_YWCon);
      sc_ifYWFCon.setColumnExpression(vc_ifYWFCon);
      SelectColumn sc_YWTrueStmt = new SelectColumn();
      FunctionCalls fn_YWTrueStmt = new FunctionCalls();
      TableColumn tb_YWTrueStmt = new TableColumn();
      tb_YWTrueStmt.setColumnName("YEAR");
      fn_YWTrueStmt.setFunctionName(tb_YWTrueStmt);
      Vector vc_YWTrueStmtIn = new Vector();
      Vector vc_YWTrueStmtOut = new Vector();
      vc_YWTrueStmtIn.addElement(vector2.get(0));
      fn_YWTrueStmt.setFunctionArguments(vc_YWTrueStmtIn);
      vc_YWTrueStmtOut.addElement(fn_YWTrueStmt);
      sc_YWTrueStmt.setColumnExpression(vc_YWTrueStmtOut);
      SelectColumn sc_YWFalseStmt = new SelectColumn();
      Vector vc_YWFalseStmt = new Vector();
      SelectColumn sc_YWYear = new SelectColumn();
      FunctionCalls fn_YWYear = new FunctionCalls();
      TableColumn tb_YWYear = new TableColumn();
      tb_YWYear.setColumnName("YEAR");
      fn_YWYear.setFunctionName(tb_YWYear);
      Vector vc_YWYearIn = new Vector();
      Vector vc_YWYearOut = new Vector();
      vc_YWYearIn.addElement(vector3.get(0));
      fn_YWYear.setFunctionArguments(vc_YWYearIn);
      vc_YWYearOut.addElement(fn_YWYear);
      sc_YWYear.setColumnExpression(vc_YWYearOut);
      vc_YWFalseStmt.addElement("(");
      vc_YWFalseStmt.addElement(sc_YWYear);
      vc_YWFalseStmt.addElement("+");
      vc_YWFalseStmt.addElement("1");
      vc_YWFalseStmt.addElement(")");
      sc_YWFalseStmt.setColumnExpression(vc_YWFalseStmt);
      vc_ifYearWeekFloorIn.addElement(sc_ifYWFCon);
      vc_ifYearWeekFloorIn.addElement(sc_YWTrueStmt);
      vc_ifYearWeekFloorIn.addElement(sc_YWFalseStmt);
      fn_ifYearWeekFloor.setFunctionArguments(vc_ifYearWeekFloorIn);
      vc_ifYearWeekFloorOut.addElement(fn_ifYearWeekFloor);
      sc_ifYearWeekFloor.setColumnExpression(vc_ifYearWeekFloorOut);
      vc_ifYearWeek.addElement(sc_ifYearWeekFloor);
      vc_ifYearWeek.addElement("*");
      vc_ifYearWeek.addElement("100");
      sc_ifYearWeek.setColumnExpression(vc_ifYearWeek);
      SelectColumn sc_ceil = new SelectColumn();
      FunctionCalls fnCl_ceil = new FunctionCalls();
      TableColumn tbCl_ceil = new TableColumn();
      tbCl_ceil.setColumnName("CEIL");
      fnCl_ceil.setFunctionName(tbCl_ceil);
      Vector vc_ceilIn = new Vector();
      Vector vc_ceilOut = new Vector();
      SelectColumn sc_ceilArgs = new SelectColumn();
      Vector vc_ceilArgs = new Vector();
      SelectColumn sc_ceilDividend = new SelectColumn();
      Vector vc_ceilDividend = new Vector();
      SelectColumn sc_ceilMultiplicand = new SelectColumn();
      Vector vc_ceilMultiplicand = new Vector();
      SelectColumn sc_datediffAddend = new SelectColumn();
      FunctionCalls fn_datediffAddend = new FunctionCalls();
      TableColumn tb_datediffAddend = new TableColumn();
      tb_datediffAddend.setColumnName("DATEDIFF");
      fn_datediffAddend.setFunctionName(tb_datediffAddend);
      Vector vc_datediffAddendIn = new Vector();
      Vector vc_datediffAddendOut = new Vector();
      vc_datediffAddendIn.addElement(vector4.get(0));
      vc_datediffAddendIn.addElement(this.WeekMode2FiscalYear(to_sqs, from_sqs, fiscalStartMonth));
      fn_datediffAddend.setFunctionArguments(vc_datediffAddendIn);
      vc_datediffAddendOut.addElement(fn_datediffAddend);
      sc_datediffAddend.setColumnExpression(vc_datediffAddendOut);
      SelectColumn sc_Added = new SelectColumn();
      Vector vc_Added = new Vector();
      SelectColumn sc_AddedDividend = new SelectColumn();
      Vector vc_AddedDividend = new Vector();
      SelectColumn sc_dayofWeek = new SelectColumn();
      FunctionCalls fn_dayofWeek = new FunctionCalls();
      TableColumn tb_dayofWeek = new TableColumn();
      tb_dayofWeek.setColumnName("DAYOFWEEK");
      fn_dayofWeek.setFunctionName(tb_dayofWeek);
      Vector vc_dayofWeekIn = new Vector();
      vc_dayofWeekIn.addElement(this.WeekMode2FiscalYear(to_sqs, from_sqs, fiscalStartMonth));
      Vector vc_dayofWeekOut = new Vector();
      fn_dayofWeek.setFunctionArguments(vc_dayofWeekIn);
      vc_dayofWeekOut.addElement(fn_dayofWeek);
      sc_dayofWeek.setColumnExpression(vc_dayofWeekOut);
      vc_AddedDividend.addElement(sc_dayofWeek);
      vc_AddedDividend.addElement("+");
      vc_AddedDividend.addElement("7");
      vc_AddedDividend.addElement("-");
      vc_AddedDividend.addElement(weekStartDay);
      sc_AddedDividend.setColumnExpression(vc_AddedDividend);
      vc_Added.addElement("(");
      vc_Added.addElement(sc_AddedDividend);
      vc_Added.addElement(")");
      vc_Added.addElement("%");
      vc_Added.addElement("7");
      sc_Added.setColumnExpression(vc_Added);
      vc_ceilMultiplicand.addElement("(");
      vc_ceilMultiplicand.addElement(sc_datediffAddend);
      vc_ceilMultiplicand.addElement("+");
      vc_ceilMultiplicand.addElement("1");
      vc_ceilMultiplicand.addElement("+");
      vc_ceilMultiplicand.addElement(sc_Added);
      vc_ceilMultiplicand.addElement(")");
      sc_ceilMultiplicand.setColumnExpression(vc_ceilMultiplicand);
      vc_ceilDividend.addElement("(");
      vc_ceilDividend.addElement(sc_ceilMultiplicand);
      vc_ceilDividend.addElement("*");
      vc_ceilDividend.addElement("1.0");
      vc_ceilDividend.addElement(")");
      sc_ceilDividend.setColumnExpression(vc_ceilDividend);
      vc_ceilArgs.addElement(sc_ceilDividend);
      vc_ceilArgs.addElement("/");
      vc_ceilArgs.addElement("7");
      sc_ceilArgs.setColumnExpression(vc_ceilArgs);
      vc_ceilIn.addElement(sc_ceilArgs);
      fnCl_ceil.setFunctionArguments(vc_ceilIn);
      vc_ceilOut.addElement(fnCl_ceil);
      sc_ceil.setColumnExpression(vc_ceilOut);
      vc_WM2FiscalWeekAndYear.addElement(sc_ifYearWeek);
      vc_WM2FiscalWeekAndYear.addElement("+");
      vc_WM2FiscalWeekAndYear.addElement(sc_ceil);
      sc_WM2FiscalWeekAndYear.setColumnExpression(vc_WM2FiscalWeekAndYear);
      sc_WM2FiscalWeekAndYear.setOpenBrace("(");
      sc_WM2FiscalWeekAndYear.setCloseBrace(")");
      return sc_WM2FiscalWeekAndYear;
   }

   public SelectColumn WeekMode2FiscalYear(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, String fiscalStartMonth) throws ConvertException {
      Vector vector1 = new Vector();
      Vector vector2 = new Vector();
      Vector vector3 = new Vector();

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

      SelectColumn sc_dateDiffStr_To_Date = new SelectColumn();
      FunctionCalls fn_dateDiffStr_To_Date = new FunctionCalls();
      TableColumn tb_dateDiffStr_To_Date = new TableColumn();
      tb_dateDiffStr_To_Date.setColumnName("STR_TO_DATE");
      fn_dateDiffStr_To_Date.setFunctionName(tb_dateDiffStr_To_Date);
      Vector vc_dateDiffStr_To_DateIn = new Vector();
      Vector vc_dateDiffStr_To_DateOut = new Vector();
      SelectColumn sc_dateDiffConcat = new SelectColumn();
      FunctionCalls fn_dateDiffConcat = new FunctionCalls();
      TableColumn tb_dateDiffConcat = new TableColumn();
      tb_dateDiffConcat.setColumnName("CONCAT");
      fn_dateDiffConcat.setFunctionName(tb_dateDiffConcat);
      Vector vc_dateDiffConcatIn = new Vector();
      Vector vc_dateDiffConcatOut = new Vector();
      SelectColumn sc_ifYearWeekFloor = new SelectColumn();
      FunctionCalls fn_ifYearWeekFloor = new FunctionCalls();
      TableColumn tb_ifYearWeekFloor = new TableColumn();
      tb_ifYearWeekFloor.setColumnName("IF");
      fn_ifYearWeekFloor.setFunctionName(tb_ifYearWeekFloor);
      Vector vc_ifYearWeekFloorIn = new Vector();
      Vector vc_ifYearWeekFloorOut = new Vector();
      SelectColumn sc_ifYWFCon = new SelectColumn();
      Vector vc_ifYWFCon = new Vector();
      WhereItem whIt_YWCon = new WhereItem();
      WhereColumn whCol_YWConLeftExp = new WhereColumn();
      Vector vc_YWConLeftExp = new Vector();
      SelectColumn sc_yearWeekMonth = new SelectColumn();
      FunctionCalls fn_yearWeekMonth = new FunctionCalls();
      TableColumn tb_yearWeekMonth = new TableColumn();
      tb_yearWeekMonth.setColumnName("MONTH");
      fn_yearWeekMonth.setFunctionName(tb_yearWeekMonth);
      Vector vc_yearWeekMonthIn = new Vector();
      Vector vc_yearWeekMonthOut = new Vector();
      vc_yearWeekMonthIn.addElement(vector1.get(0));
      fn_yearWeekMonth.setFunctionArguments(vc_yearWeekMonthIn);
      vc_yearWeekMonthOut.addElement(fn_yearWeekMonth);
      sc_yearWeekMonth.setColumnExpression(vc_yearWeekMonthOut);
      vc_YWConLeftExp.addElement(sc_yearWeekMonth);
      whCol_YWConLeftExp.setColumnExpression(vc_YWConLeftExp);
      whIt_YWCon.setLeftWhereExp(whCol_YWConLeftExp);
      whIt_YWCon.setOperator("<");
      WhereColumn whCol_YWConRightExp = new WhereColumn();
      Vector vc_YWConRightExp = new Vector();
      vc_YWConRightExp.addElement(fiscalStartMonth);
      whCol_YWConRightExp.setColumnExpression(vc_YWConRightExp);
      whIt_YWCon.setRightWhereExp(whCol_YWConRightExp);
      vc_ifYWFCon.addElement(whIt_YWCon);
      sc_ifYWFCon.setColumnExpression(vc_ifYWFCon);
      SelectColumn sc_YWTrueStmt = new SelectColumn();
      Vector vc_YWTrueStmt = new Vector();
      SelectColumn sc_YWYear = new SelectColumn();
      FunctionCalls fn_YWYear = new FunctionCalls();
      TableColumn tb_YWYear = new TableColumn();
      tb_YWYear.setColumnName("YEAR");
      fn_YWYear.setFunctionName(tb_YWYear);
      Vector vc_YWYearIn = new Vector();
      Vector vc_YWYearOut = new Vector();
      vc_YWYearIn.addElement(vector3.get(0));
      fn_YWYear.setFunctionArguments(vc_YWYearIn);
      vc_YWYearOut.addElement(fn_YWYear);
      sc_YWYear.setColumnExpression(vc_YWYearOut);
      vc_YWTrueStmt.addElement("(");
      vc_YWTrueStmt.addElement(sc_YWYear);
      vc_YWTrueStmt.addElement("-");
      vc_YWTrueStmt.addElement("1");
      vc_YWTrueStmt.addElement(")");
      sc_YWTrueStmt.setColumnExpression(vc_YWTrueStmt);
      SelectColumn sc_YWFalseStmt = new SelectColumn();
      FunctionCalls fn_YWFalseStmt = new FunctionCalls();
      TableColumn tb_YWFalseStmt = new TableColumn();
      tb_YWFalseStmt.setColumnName("YEAR");
      fn_YWFalseStmt.setFunctionName(tb_YWFalseStmt);
      Vector vc_YWFalseStmtIn = new Vector();
      Vector vc_YWFalseStmtOut = new Vector();
      vc_YWFalseStmtIn.addElement(vector2.get(0));
      fn_YWFalseStmt.setFunctionArguments(vc_YWFalseStmtIn);
      vc_YWFalseStmtOut.addElement(fn_YWFalseStmt);
      sc_YWFalseStmt.setColumnExpression(vc_YWFalseStmtOut);
      vc_ifYearWeekFloorIn.addElement(sc_ifYWFCon);
      vc_ifYearWeekFloorIn.addElement(sc_YWTrueStmt);
      vc_ifYearWeekFloorIn.addElement(sc_YWFalseStmt);
      fn_ifYearWeekFloor.setFunctionArguments(vc_ifYearWeekFloorIn);
      vc_ifYearWeekFloorOut.addElement(fn_ifYearWeekFloor);
      sc_ifYearWeekFloor.setColumnExpression(vc_ifYearWeekFloorOut);
      vc_dateDiffConcatIn.addElement(sc_ifYearWeekFloor);
      vc_dateDiffConcatIn.addElement("'-'");
      vc_dateDiffConcatIn.addElement(fiscalStartMonth);
      vc_dateDiffConcatIn.addElement("'-01'");
      fn_dateDiffConcat.setFunctionArguments(vc_dateDiffConcatIn);
      vc_dateDiffConcatOut.addElement(fn_dateDiffConcat);
      sc_dateDiffConcat.setColumnExpression(vc_dateDiffConcatOut);
      vc_dateDiffStr_To_DateIn.addElement(sc_dateDiffConcat);
      vc_dateDiffStr_To_DateIn.addElement("'%Y-%m-%d'");
      fn_dateDiffStr_To_Date.setFunctionArguments(vc_dateDiffStr_To_DateIn);
      vc_dateDiffStr_To_DateOut.addElement(fn_dateDiffStr_To_Date);
      sc_dateDiffStr_To_Date.setColumnExpression(vc_dateDiffStr_To_DateOut);
      return sc_dateDiffStr_To_Date;
   }
}
