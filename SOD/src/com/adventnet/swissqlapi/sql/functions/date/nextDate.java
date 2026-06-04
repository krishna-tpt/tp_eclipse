package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class nextDate extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String weekStDay;
      String weekMode;
      if (from_sqs != null && from_sqs.isHyperSql()) {
         weekMode = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
         if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
            weekStDay = "CASE WHEN CAST(" + weekMode + " AS DATE)  BETWEEN (TRUNC(" + arguments.get(2).toString() + ", 'MM') + 1 MONTH) AND LAST_DAY(" + arguments.get(2).toString() + " + " + arguments.get(1).toString() + " MONTH)  THEN 1 ELSE 0 END";
         } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
            weekStDay = "CASE WHEN CAST(" + weekMode + " AS DATE)  BETWEEN TRUNC(" + arguments.get(2).toString() + ", 'YY') + (QUARTER(" + arguments.get(2).toString() + ")*3) MONTH AND (TRUNC(" + arguments.get(2).toString() + ", 'YY') + ((QUARTER(" + arguments.get(2).toString() + ")+" + arguments.get(1).toString() + ")*3) MONTH - 1 DAY)  THEN 1 ELSE 0 END";
         } else {
            if (!fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
               this.setFunctionArguments(arguments);
               return;
            }

            String weekStartDay = arguments.get(1).toString();
            String dayofWeekOfCurrDate = arguments.get(2).toString();
            weekStDay = "CASE WHEN (CAST(DATEDIFF(DAY,CAST(DATEADD(DAY, (CASE WHEN(" + dayofWeekOfCurrDate + "  < " + weekStartDay + ") THEN (" + weekStartDay + " -" + dayofWeekOfCurrDate + ") ELSE (7 + (" + weekStartDay + " -" + dayofWeekOfCurrDate + ")) END),CURRENT_DATE) AS DATE) , CAST(" + weekMode + " AS DATE)) AS BIGINT)  BETWEEN 0  AND  6) THEN 1 ELSE 0 END";
         }

         this.functionName.setColumnName(weekStDay);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else if (from_sqs != null && from_sqs.isMySqlLive()) {
         if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
            weekStDay = "If(" + arguments.get(0).toString() + " BETWEEN date_add(LAST_DAY(" + arguments.get(2).toString() + "),INTERVAL 1 DAY) AND last_day(date_add(" + arguments.get(2).toString() + ",interval (" + arguments.get(1).toString() + ") month)),1,0)";
            this.functionName.setColumnName(weekStDay);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
            weekStDay = "IF(" + arguments.get(0).toString() + " BETWEEN  date_add(date_format(" + arguments.get(2).toString() + ",'%Y-01-01'),interval (quarter(" + arguments.get(2).toString() + ")) quarter) AND date_sub(date_add(date_format(" + arguments.get(2).toString() + ",'%Y-01-01'),interval (quarter(" + arguments.get(2).toString() + ") + (" + arguments.get(1).toString() + ")) quarter),interval 1 day) ,1,0)";
            this.functionName.setColumnName(weekStDay);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
            weekStDay = "if(datediff(" + arguments.get(0).toString() + ",date_add(current_date(), interval if(" + arguments.get(2).toString() + " < " + arguments.get(1).toString() + ", (" + arguments.get(1).toString() + "-" + arguments.get(2).toString() + "),(7+(" + arguments.get(1).toString() + "-" + arguments.get(2).toString() + "))) day)) between 0 and 6,1,0)";
            this.functionName.setColumnName(weekStDay);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         }
      } else {
         SelectColumn sc_wi;
         Vector vector3;
         WhereItem wi_if;
         WhereColumn if_left;
         Vector vc_DayOfWeekIn;
         WhereColumn if_left;
         Vector vc_numvalue;
         SelectColumn sc_if;
         FunctionCalls fn_Date;
         TableColumn tb_Date;
         Vector vc_DateIn;
         Vector vc_DateOut;
         SelectColumn sc_Limit;
         Vector vc_yearWeekleftOut;
         SelectColumn sc_dateSub;
         Vector vc_intr;
         Vector vc_weekdayOut;
         Vector vc_dayofWeek_trueIn;
         Vector vector1;
         if (!fnStr.equalsIgnoreCase("ISNEXT_NYEAR") && !fnStr.equalsIgnoreCase("IS_NEXT_NYEAR")) {
            if (!fnStr.equalsIgnoreCase("ISNEXT_NQUARTER") && !fnStr.equalsIgnoreCase("IS_NEXT_NQUARTER")) {
               if (!fnStr.equalsIgnoreCase("ISNEXT_NMONTH") && !fnStr.equalsIgnoreCase("IS_NEXT_NMONTH")) {
                  SelectColumn sc_intr;
                  Vector vc;
                  Vector vc_if;
                  Vector vc_wi;
                  FunctionCalls fn_dateSub;
                  TableColumn tb_dateSub;
                  Vector vc_dateSubIn;
                  Vector vc_dateSubOut;
                  SelectColumn sc_intr;
                  TableColumn tbCl_interval;
                  SelectColumn sc_weekday;
                  FunctionCalls fn_weekday;
                  TableColumn tb_yearWeekright;
                  Vector vc_weekdayIn;
                  if (!fnStr.equalsIgnoreCase("ISNEXTWEEK") && !fnStr.equalsIgnoreCase("IS_NEXT_WEEK")) {
                     Vector vc_dayofWeek_trueOut;
                     if (!fnStr.equalsIgnoreCase("ISNEXT_NDAY") && !fnStr.equalsIgnoreCase("IS_NEXT_NDAY")) {
                        SelectColumn sc_interval;
                        Vector vector2;
                        if (fnStr.equalsIgnoreCase("TOMORROW")) {
                           this.functionName.setColumnName("DATE_ADD");
                           arguments.addElement(this.current_date());
                           sc_interval = new SelectColumn();
                           vector2 = new Vector();
                           TableColumn tbCl_interval = new TableColumn();
                           tbCl_interval.setColumnName("INTERVAL");
                           vector2.add(tbCl_interval);
                           vector2.add("1 ");
                           vector2.add("DAY");
                           sc_interval.setColumnExpression(vector2);
                           arguments.addElement(sc_interval);
                           this.setFunctionArguments(arguments);
                        } else if (!fnStr.equalsIgnoreCase("NEXT_WEEKDAY") && !fnStr.equalsIgnoreCase("NEXT_WEEK_DAY")) {
                           TableColumn tbCl_interval;
                           if (fnStr.equalsIgnoreCase("NEXT_NDAY")) {
                              this.functionName.setColumnName("DATE_ADD");
                              sc_interval = new SelectColumn();
                              vector2 = new Vector();
                              vector2.addElement(arguments.get(0));
                              vector3 = new Vector();
                              tbCl_interval = new TableColumn();
                              tbCl_interval.setColumnName("INTERVAL");
                              vector3.addElement(tbCl_interval);
                              vector3.add("(");
                              vector3.addElement(arguments.get(1));
                              vector3.add(")");
                              vector3.add(" DAY");
                              sc_interval.setColumnExpression(vector3);
                              vector2.addElement(sc_interval);
                              this.setFunctionArguments(vector2);
                           } else if (fnStr.equalsIgnoreCase("NEXT_NMONTH")) {
                              this.functionName.setColumnName("DATE_ADD");
                              sc_interval = new SelectColumn();
                              vector2 = new Vector();
                              vector2.addElement(arguments.get(0));
                              vector3 = new Vector();
                              tbCl_interval = new TableColumn();
                              tbCl_interval.setColumnName("INTERVAL");
                              vector3.add(tbCl_interval);
                              vector3.add("(");
                              vector3.addElement(arguments.get(1));
                              vector3.add(")");
                              vector3.add(" MONTH");
                              sc_interval.setColumnExpression(vector3);
                              vector2.addElement(sc_interval);
                              this.setFunctionArguments(vector2);
                           }
                        } else {
                           this.functionName.setColumnName("DATE_ADD");
                           vector1 = new Vector();
                           vector2 = new Vector();
                           vector3 = new Vector();

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

                           if (vector1.elementAt(1) instanceof SelectColumn) {
                              sc_intr = (SelectColumn)vector1.elementAt(1);
                              vc = sc_intr.getColumnExpression();
                              if (!(vc.elementAt(0) instanceof String)) {
                                 throw new ConvertException("Invalid Argument Value for Function NEXT_WEEKDAY", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{fnStr.toUpperCase(), "WEEKDAY"});
                              }

                              String weekDay = (String)vc.elementAt(0);
                              if (weekDay.equalsIgnoreCase("null")) {
                                 weekDay = "1";
                              }

                              weekDay = weekDay.replaceAll("'", "");
                              this.validateWeekDay(weekDay, fnStr.toUpperCase());
                           }

                           sc_intr = new SelectColumn();
                           vc = new Vector();
                           TableColumn tbCl_interval = new TableColumn();
                           tbCl_interval.setColumnName("INTERVAL");
                           vc.add(tbCl_interval);
                           SelectColumn sc_numvalue = new SelectColumn();
                           vc_numvalue = new Vector();
                           sc_if = new SelectColumn();
                           vc_if = new Vector();
                           FunctionCalls fnCall_if = new FunctionCalls();
                           TableColumn tb_if = new TableColumn();
                           tb_if.setColumnName("IF");
                           fnCall_if.setFunctionName(tb_if);
                           WhereItem wi_if = new WhereItem();
                           vc_wi = new Vector();
                           WhereColumn if_left = new WhereColumn();
                           Vector vc_if_left = new Vector();
                           WhereColumn if_right = new WhereColumn();
                           Vector vc_if_right = new Vector();
                           SelectColumn sc_dayofWeek = new SelectColumn();
                           FunctionCalls fn_dayofWeek = new FunctionCalls();
                           TableColumn tb_dayofWeek = new TableColumn();
                           tb_dayofWeek.setColumnName("DAYOFWEEK");
                           fn_dayofWeek.setFunctionName(tb_dayofWeek);
                           vc_intr = new Vector();
                           Vector vc_dayofWeekOut = new Vector();
                           vc_intr.addElement(vector1.get(0));
                           fn_dayofWeek.setFunctionArguments(vc_intr);
                           vc_dayofWeekOut.addElement(fn_dayofWeek);
                           sc_dayofWeek.setColumnExpression(vc_dayofWeekOut);
                           vc_if_left.addElement(sc_dayofWeek);
                           if_left.setColumnExpression(vc_if_left);
                           wi_if.setLeftWhereExp(if_left);
                           wi_if.setOperator("<");
                           vc_if_right.add("(");
                           vc_if_right.addElement(vector1.get(1));
                           vc_if_right.add(")");
                           if_right.setColumnExpression(vc_if_right);
                           wi_if.setRightWhereExp(if_right);
                           vc_wi.addElement(wi_if);
                           sc_if.setColumnExpression(vc_wi);
                           vc_if.addElement(sc_if);
                           sc_weekday = new SelectColumn();
                           Vector vc_trueStmt = new Vector();
                           vc_trueStmt.add("(");
                           vc_trueStmt.addElement(vector2.get(1));
                           vc_trueStmt.add(")");
                           vc_trueStmt.add("-");
                           SelectColumn sc_dayofWeek_true = new SelectColumn();
                           FunctionCalls fn_dayofWeek_true = new FunctionCalls();
                           TableColumn tb_dayofWeek_true = new TableColumn();
                           tb_dayofWeek_true.setColumnName("DAYOFWEEK");
                           fn_dayofWeek_true.setFunctionName(tb_dayofWeek_true);
                           vc_dayofWeek_trueIn = new Vector();
                           vc_dayofWeek_trueOut = new Vector();
                           vc_dayofWeek_trueIn.addElement(vector2.get(0));
                           fn_dayofWeek_true.setFunctionArguments(vc_dayofWeek_trueIn);
                           vc_dayofWeek_trueOut.addElement(fn_dayofWeek_true);
                           sc_dayofWeek_true.setColumnExpression(vc_dayofWeek_trueOut);
                           vc_trueStmt.addElement(sc_dayofWeek_true);
                           sc_weekday.setOpenBrace("(");
                           sc_weekday.setCloseBrace(")");
                           sc_weekday.setColumnExpression(vc_trueStmt);
                           vc_if.addElement(sc_weekday);
                           SelectColumn sc_falseStmt = new SelectColumn();
                           Vector vc_falseStmt = new Vector();
                           vc_falseStmt.add("7");
                           vc_falseStmt.add("+");
                           vc_falseStmt.add("(");
                           vc_falseStmt.addElement(vector3.get(1));
                           vc_falseStmt.add(")");
                           vc_falseStmt.add("-");
                           SelectColumn sc_dayofWeek_false = new SelectColumn();
                           FunctionCalls fn_dayofWeek_false = new FunctionCalls();
                           TableColumn tb_dayofWeek_false = new TableColumn();
                           tb_dayofWeek_false.setColumnName("DAYOFWEEK");
                           fn_dayofWeek_false.setFunctionName(tb_dayofWeek_false);
                           Vector vc_dayofWeek_falseIn = new Vector();
                           Vector vc_dayofWeek_falseOut = new Vector();
                           vc_dayofWeek_falseIn.addElement(vector3.get(0));
                           fn_dayofWeek_false.setFunctionArguments(vc_dayofWeek_falseIn);
                           vc_dayofWeek_falseOut.addElement(fn_dayofWeek_false);
                           sc_dayofWeek_false.setColumnExpression(vc_dayofWeek_falseOut);
                           vc_falseStmt.addElement(sc_dayofWeek_false);
                           sc_falseStmt.setOpenBrace("(");
                           sc_falseStmt.setCloseBrace(")");
                           sc_falseStmt.setColumnExpression(vc_falseStmt);
                           vc_if.addElement(sc_falseStmt);
                           fnCall_if.setFunctionArguments(vc_if);
                           vc_numvalue.addElement(fnCall_if);
                           sc_numvalue.setColumnExpression(vc_numvalue);
                           sc_numvalue.setOpenBrace("(");
                           sc_numvalue.setCloseBrace(")");
                           vc.addElement(sc_numvalue);
                           vc.add("DAY");
                           sc_intr.setColumnExpression(vc);
                           arguments.removeElementAt(1);
                           arguments.addElement(sc_intr);
                           this.setFunctionArguments(arguments);
                        }
                     } else {
                        this.functionName.setColumnName("IF");
                        vector1 = new Vector();
                        sc_wi = new SelectColumn();
                        vector3 = new Vector();
                        wi_if = new WhereItem();
                        if_left = new WhereColumn();
                        vc_DayOfWeekIn = new Vector();
                        if_left = new WhereColumn();
                        vc_numvalue = new Vector();
                        sc_if = new SelectColumn();
                        fn_Date = new FunctionCalls();
                        tb_Date = new TableColumn();
                        tb_Date.setColumnName("DATE");
                        fn_Date.setFunctionName(tb_Date);
                        vc_DateIn = new Vector();
                        vc_DateOut = new Vector();
                        vc_DateIn.addElement(arguments.get(0));
                        fn_Date.setFunctionArguments(vc_DateIn);
                        vc_DateOut.addElement(fn_Date);
                        sc_if.setColumnExpression(vc_DateOut);
                        vc_DayOfWeekIn.addElement(sc_if);
                        if_left.setColumnExpression(vc_DayOfWeekIn);
                        wi_if.setLeftWhereExp(if_left);
                        wi_if.setOperator("BETWEEN");
                        sc_Limit = new SelectColumn();
                        vc_yearWeekleftOut = new Vector();
                        sc_dateSub = new SelectColumn();
                        fn_dateSub = new FunctionCalls();
                        tb_dateSub = new TableColumn();
                        tb_dateSub.setColumnName("DATE_ADD");
                        fn_dateSub.setFunctionName(tb_dateSub);
                        vc_dateSubIn = new Vector();
                        vc_dateSubOut = new Vector();
                        vc_dateSubIn.addElement(this.current_date());
                        sc_intr = new SelectColumn();
                        vc_intr = new Vector();
                        tbCl_interval = new TableColumn();
                        tbCl_interval.setColumnName("INTERVAL");
                        vc_intr.addElement(tbCl_interval);
                        vc_intr.add("(");
                        vc_intr.add("1");
                        vc_intr.add(")");
                        vc_intr.addElement(" day");
                        sc_intr.setColumnExpression(vc_intr);
                        vc_dateSubIn.addElement(sc_intr);
                        fn_dateSub.setFunctionArguments(vc_dateSubIn);
                        vc_dateSubOut.addElement(fn_dateSub);
                        sc_dateSub.setColumnExpression(vc_dateSubOut);
                        vc_yearWeekleftOut.addElement(sc_dateSub);
                        vc_yearWeekleftOut.addElement("AND");
                        sc_weekday = new SelectColumn();
                        fn_weekday = new FunctionCalls();
                        tb_yearWeekright = new TableColumn();
                        tb_yearWeekright.setColumnName("DATE_ADD");
                        fn_weekday.setFunctionName(tb_yearWeekright);
                        vc_weekdayIn = new Vector();
                        vc_weekdayOut = new Vector();
                        vc_weekdayIn.addElement(this.current_date());
                        SelectColumn sc_intrHigh = new SelectColumn();
                        vc_dayofWeek_trueOut = new Vector();
                        TableColumn tbCl_intervalHigh = new TableColumn();
                        tbCl_intervalHigh.setColumnName("INTERVAL");
                        vc_dayofWeek_trueOut.addElement(tbCl_intervalHigh);
                        vc_dayofWeek_trueOut.add("(");
                        vc_dayofWeek_trueOut.addElement(arguments.get(1));
                        vc_dayofWeek_trueOut.add(")");
                        vc_dayofWeek_trueOut.addElement(" day");
                        sc_intrHigh.setColumnExpression(vc_dayofWeek_trueOut);
                        vc_weekdayIn.addElement(sc_intrHigh);
                        fn_weekday.setFunctionArguments(vc_weekdayIn);
                        vc_weekdayOut.addElement(fn_weekday);
                        sc_weekday.setColumnExpression(vc_weekdayOut);
                        vc_yearWeekleftOut.addElement(sc_weekday);
                        sc_Limit.setColumnExpression(vc_yearWeekleftOut);
                        vc_numvalue.addElement(sc_Limit);
                        if_left.setColumnExpression(vc_numvalue);
                        wi_if.setRightWhereExp(if_left);
                        vector3.addElement(wi_if);
                        sc_wi.setColumnExpression(vector3);
                        vector1.addElement(sc_wi);
                        vector1.addElement("1");
                        vector1.addElement("0");
                        this.setFunctionArguments(vector1);
                     }
                  } else {
                     weekStDay = "";
                     weekMode = null;
                     SelectColumn sc_DayOfWeek;
                     if (arguments.size() > 1 && arguments.elementAt(1) instanceof SelectColumn) {
                        sc_DayOfWeek = (SelectColumn)arguments.elementAt(1);
                        Vector vc = sc_DayOfWeek.getColumnExpression();
                        if (!(vc.elementAt(0) instanceof String)) {
                           throw new ConvertException("Invalid Argument Value for Function ISNEXTWEEK", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"ISPREVIOUSWEEK", "WEEK_STARTDAY"});
                        }

                        weekStDay = (String)vc.elementAt(0);
                        weekStDay = weekStDay.replaceAll("'", "");
                        weekStDay = weekStDay.trim();
                        this.validateWeek_StartDay(weekStDay, fnStr.toUpperCase());
                        if (weekStDay.equals("0")) {
                           weekMode = "6";
                        } else if (weekStDay.equals("1")) {
                           weekMode = "3";
                        }
                     } else {
                        weekMode = "3";
                     }

                     if (weekMode != null) {
                        this.functionName.setColumnName("IF");
                        vector3 = new Vector();
                        sc_intr = new SelectColumn();
                        vc = new Vector();
                        WhereItem wi_if = new WhereItem();
                        if_left = new WhereColumn();
                        vc_numvalue = new Vector();
                        WhereColumn if_right = new WhereColumn();
                        vc_if = new Vector();
                        SelectColumn sc_yearWeekleft = new SelectColumn();
                        FunctionCalls fn_yearWeekleft = new FunctionCalls();
                        TableColumn tb_yearWeekleft = new TableColumn();
                        tb_yearWeekleft.setColumnName("YEARWEEK");
                        fn_yearWeekleft.setFunctionName(tb_yearWeekleft);
                        vc_wi = new Vector();
                        vc_yearWeekleftOut = new Vector();
                        sc_dateSub = new SelectColumn();
                        fn_dateSub = new FunctionCalls();
                        tb_dateSub = new TableColumn();
                        tb_dateSub.setColumnName("DATE_SUB");
                        fn_dateSub.setFunctionName(tb_dateSub);
                        vc_dateSubIn = new Vector();
                        vc_dateSubOut = new Vector();
                        vc_dateSubIn.addElement(arguments.get(0));
                        sc_intr = new SelectColumn();
                        vc_intr = new Vector();
                        tbCl_interval = new TableColumn();
                        tbCl_interval.setColumnName("INTERVAL");
                        vc_intr.add(tbCl_interval);
                        SelectColumn sc;
                        if (weekMode.equals("6")) {
                           sc_weekday = new SelectColumn();
                           fn_weekday = new FunctionCalls();
                           tb_yearWeekright = new TableColumn();
                           tb_yearWeekright.setColumnName("DAYOFWEEK");
                           fn_weekday.setFunctionName(tb_yearWeekright);
                           vc_weekdayIn = new Vector();
                           if (arguments.elementAt(0) instanceof SelectColumn) {
                              sc = ((SelectColumn)this.functionArguments.elementAt(0)).toMySQLSelect(to_sqs, from_sqs);
                              vc_weekdayIn.addElement(sc);
                           } else {
                              vc_weekdayIn.addElement(this.functionArguments.elementAt(0));
                           }

                           vc_weekdayOut = new Vector();
                           fn_weekday.setFunctionArguments(vc_weekdayIn);
                           vc_weekdayOut.addElement(fn_weekday);
                           sc_weekday.setColumnExpression(vc_weekdayOut);
                           sc_weekday.setOpenBrace("(");
                           sc_weekday.setCloseBrace(")");
                           vc_intr.addElement(sc_weekday);
                           vc_intr.add(" day");
                           sc_intr.setColumnExpression(vc_intr);
                        } else if (weekMode.equals("3")) {
                           sc_weekday = new SelectColumn();
                           fn_weekday = new FunctionCalls();
                           tb_yearWeekright = new TableColumn();
                           tb_yearWeekright.setColumnName("WEEKDAY");
                           fn_weekday.setFunctionName(tb_yearWeekright);
                           vc_weekdayIn = new Vector();
                           if (arguments.elementAt(0) instanceof SelectColumn) {
                              sc = ((SelectColumn)this.functionArguments.elementAt(0)).toMySQLSelect(to_sqs, from_sqs);
                              vc_weekdayIn.addElement(sc);
                           } else {
                              vc_weekdayIn.addElement(this.functionArguments.elementAt(0));
                           }

                           vc_weekdayOut = new Vector();
                           fn_weekday.setFunctionArguments(vc_weekdayIn);
                           vc_weekdayOut.addElement(fn_weekday);
                           sc_weekday.setColumnExpression(vc_weekdayOut);
                           vc_intr.add("(");
                           vc_intr.addElement(sc_weekday);
                           vc_intr.add("+");
                           vc_intr.add("1");
                           vc_intr.add(")");
                           vc_intr.add(" day");
                           sc_intr.setColumnExpression(vc_intr);
                        }

                        vc_dateSubIn.addElement(sc_intr);
                        fn_dateSub.setFunctionArguments(vc_dateSubIn);
                        vc_dateSubOut.addElement(fn_dateSub);
                        sc_dateSub.setOpenBrace("(");
                        sc_dateSub.setCloseBrace(")");
                        sc_dateSub.setColumnExpression(vc_dateSubOut);
                        vc_wi.addElement(sc_dateSub);
                        vc_wi.addElement(weekMode);
                        fn_yearWeekleft.setFunctionArguments(vc_wi);
                        vc_yearWeekleftOut.addElement(fn_yearWeekleft);
                        sc_yearWeekleft.setColumnExpression(vc_yearWeekleftOut);
                        vc_numvalue.addElement(sc_yearWeekleft);
                        if_left.setColumnExpression(vc_numvalue);
                        wi_if.setLeftWhereExp(if_left);
                        wi_if.setOperator("=");
                        sc_weekday = new SelectColumn();
                        fn_weekday = new FunctionCalls();
                        tb_yearWeekright = new TableColumn();
                        tb_yearWeekright.setColumnName("YEARWEEK");
                        fn_weekday.setFunctionName(tb_yearWeekright);
                        vc_weekdayIn = new Vector();
                        vc_weekdayOut = new Vector();
                        vc_weekdayIn.addElement(this.current_date());
                        vc_weekdayIn.addElement(weekMode);
                        fn_weekday.setFunctionArguments(vc_weekdayIn);
                        vc_weekdayOut.addElement(fn_weekday);
                        sc_weekday.setColumnExpression(vc_weekdayOut);
                        vc_if.addElement(sc_weekday);
                        if_right.setColumnExpression(vc_if);
                        wi_if.setRightWhereExp(if_right);
                        vc.addElement(wi_if);
                        sc_intr.setColumnExpression(vc);
                        vector3.addElement(sc_intr);
                        String t_num = "1";
                        String f_num = "0";
                        vector3.addElement(t_num);
                        vector3.addElement(f_num);
                        this.setFunctionArguments(vector3);
                     } else {
                        this.functionName.setColumnName("ZR_ISNEXTWEEK");
                        arguments.set(1, Integer.toString(Integer.parseInt(weekStDay) + 1));
                        sc_DayOfWeek = new SelectColumn();
                        FunctionCalls fn_DayOfWeek = new FunctionCalls();
                        TableColumn tb_DayOfWeek = new TableColumn();
                        tb_DayOfWeek.setColumnName("DAYOFWEEK");
                        fn_DayOfWeek.setFunctionName(tb_DayOfWeek);
                        vc_DayOfWeekIn = new Vector();
                        Vector vc_DayOfWeekOut = new Vector();
                        vc_DayOfWeekIn.addElement(this.current_date());
                        fn_DayOfWeek.setFunctionArguments(vc_DayOfWeekIn);
                        vc_DayOfWeekOut.addElement(fn_DayOfWeek);
                        sc_DayOfWeek.setColumnExpression(vc_DayOfWeekOut);
                        arguments.addElement(sc_DayOfWeek);
                        this.setFunctionArguments(arguments);
                     }
                  }
               } else {
                  this.functionName.setColumnName("ZR_ISNEXTMONTH");
                  arguments.addElement(this.current_date());
                  this.setFunctionArguments(arguments);
               }
            } else {
               this.functionName.setColumnName("ZR_ISNEXTQUARTER");
               arguments.addElement(this.current_date());
               this.setFunctionArguments(arguments);
            }
         } else {
            this.functionName.setColumnName("IF");
            vector1 = new Vector();
            sc_wi = new SelectColumn();
            vector3 = new Vector();
            wi_if = new WhereItem();
            if_left = new WhereColumn();
            vc_DayOfWeekIn = new Vector();
            if_left = new WhereColumn();
            vc_numvalue = new Vector();
            sc_if = new SelectColumn();
            fn_Date = new FunctionCalls();
            tb_Date = new TableColumn();
            tb_Date.setColumnName("YEAR");
            fn_Date.setFunctionName(tb_Date);
            vc_DateIn = new Vector();
            vc_DateOut = new Vector();
            vc_DateIn.addElement(arguments.get(0));
            fn_Date.setFunctionArguments(vc_DateIn);
            vc_DateOut.addElement(fn_Date);
            sc_if.setColumnExpression(vc_DateOut);
            vc_DayOfWeekIn.addElement(sc_if);
            if_left.setColumnExpression(vc_DayOfWeekIn);
            wi_if.setLeftWhereExp(if_left);
            wi_if.setOperator("BETWEEN");
            sc_Limit = new SelectColumn();
            vc_yearWeekleftOut = new Vector();
            sc_dateSub = new SelectColumn();
            Vector vc_LimitLow = new Vector();
            SelectColumn sc_StartDate = new SelectColumn();
            FunctionCalls fnCl_StartDate = new FunctionCalls();
            TableColumn tbCl_StartDate = new TableColumn();
            tbCl_StartDate.setColumnName("YEAR");
            fnCl_StartDate.setFunctionName(tbCl_StartDate);
            Vector vc_StartDateIn = new Vector();
            vc_intr = new Vector();
            vc_StartDateIn.addElement(this.current_date());
            fnCl_StartDate.setFunctionArguments(vc_StartDateIn);
            vc_intr.addElement(fnCl_StartDate);
            sc_StartDate.setColumnExpression(vc_intr);
            vc_LimitLow.addElement(sc_StartDate);
            vc_LimitLow.addElement("+");
            vc_LimitLow.addElement("1");
            sc_dateSub.setOpenBrace("(");
            sc_dateSub.setCloseBrace(")");
            sc_dateSub.setColumnExpression(vc_LimitLow);
            vc_yearWeekleftOut.addElement(sc_dateSub);
            vc_yearWeekleftOut.addElement("AND");
            SelectColumn sc_LimitUp = new SelectColumn();
            Vector vc_LimitUp = new Vector();
            SelectColumn sc_EndDate = new SelectColumn();
            FunctionCalls fnCl_EndDate = new FunctionCalls();
            TableColumn tbCl_EndDate = new TableColumn();
            tbCl_EndDate.setColumnName("YEAR");
            fnCl_EndDate.setFunctionName(tbCl_StartDate);
            vc_weekdayOut = new Vector();
            vc_dayofWeek_trueIn = new Vector();
            vc_weekdayOut.addElement(this.current_date());
            fnCl_EndDate.setFunctionArguments(vc_weekdayOut);
            vc_dayofWeek_trueIn.addElement(fnCl_EndDate);
            sc_EndDate.setColumnExpression(vc_dayofWeek_trueIn);
            vc_LimitUp.addElement(sc_EndDate);
            vc_LimitUp.addElement("+");
            vc_LimitUp.addElement(arguments.get(1));
            sc_LimitUp.setOpenBrace("(");
            sc_LimitUp.setCloseBrace(")");
            sc_LimitUp.setColumnExpression(vc_LimitUp);
            vc_yearWeekleftOut.addElement(sc_LimitUp);
            sc_Limit.setColumnExpression(vc_yearWeekleftOut);
            vc_numvalue.addElement(sc_Limit);
            if_left.setColumnExpression(vc_numvalue);
            wi_if.setRightWhereExp(if_left);
            vector3.addElement(wi_if);
            sc_wi.setColumnExpression(vector3);
            vector1.addElement(sc_wi);
            vector1.addElement("1");
            vector1.addElement("0");
            this.setFunctionArguments(vector1);
         }
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0) {
               this.handleStringLiteralForDate(from_sqs, i_count, !isPostgreLiveDbs);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (isPostgreLiveDbs) {
         String qry = "";
         if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
            qry = "case when CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('month',current_date)+interval '1' month) and (date_trunc('month',current_date + interval '1' month) + (interval '1' month * (" + arguments.get(1).toString() + ")) - interval '1' day) then 1 else 0 end";
         } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
            qry = "case when CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('quarter',current_date + interval '3' month)) and (date_trunc('quarter',current_date + interval '3' month)+ (interval '1' month *3 * (" + arguments.get(1).toString() + "))- interval '1' day) then 1 else 0 end";
         } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
            String weekStartDay = arguments.get(1).toString();
            String dayofWeekOfCurrDate = arguments.get(2).toString();
            qry = "CASE WHEN DATE_MI(DATE(" + arguments.get(0).toString() + "), DATE((CURRENT_DATE) + ( INTERVAL  '1'  day * ROUND((CASE WHEN " + dayofWeekOfCurrDate + "  < " + weekStartDay + " THEN (" + weekStartDay + " -" + dayofWeekOfCurrDate + ") ELSE (7 + (" + weekStartDay + " -" + dayofWeekOfCurrDate + ")) END))) ))  between 0  and  6 THEN 1 ELSE 0 END";
         }

         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         this.setFunctionArguments(arguments);
      }
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0) {
               this.handleStringLiteralForDate(from_sqs, i_count, false);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
         qry = "if(CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN (timestamp(LAST_DAY((CURRENT_DATE))) + INTERVAL  '1'  DAY * 1)  AND  LAST_DAY((timestamp((CURRENT_DATE)) + interval  '1'  month * (" + arguments.get(1).toString() + "))), 1, 0)";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
         qry = "if(CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('quarter',current_date + interval '3' month)) and (date_trunc('quarter',current_date + interval '3' month)+ (interval '3' month  * (" + arguments.get(1).toString() + "))- interval '1' day), 1,0)";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "if(TIMESTAMPDIFF(DAY,CAST((timestamp(CURRENT_DATE) + interval  '1'  day * (if(" + dayofWeekOfCurrDate + "  < " + weekStartDay + ", (" + weekStartDay + " -" + dayofWeekOfCurrDate + "), (7 + (" + weekStartDay + " -" + dayofWeekOfCurrDate + "))))) AS DATE) , CAST(" + arguments.get(0).toString() + " AS DATE))  between 0  and  6, 1, 0)";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN DATEADD(MONTH, 1, DATE_TRUNC(MONTH, " + arguments.get(2).toString() + ")) AND LAST_DAY(DATEADD(MONTH,(" + arguments.get(1).toString() + ")," + arguments.get(2).toString() + "))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN DATEADD(MONTH, 3, DATE_TRUNC(QUARTER, " + arguments.get(2).toString() + ")) AND DATEADD(DAY,-1,DATEADD(MONTH,((" + arguments.get(1).toString() + "+1)*3),DATE_TRUNC(QUARTER, " + arguments.get(2).toString() + ")))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(DATEDIFF(DAY,CAST(DATEADD(DAY, (CASE WHEN(" + dayofWeekOfCurrDate + "  < " + weekStartDay + ") THEN (" + weekStartDay + " -" + dayofWeekOfCurrDate + ") ELSE (7 + (" + weekStartDay + " -" + dayofWeekOfCurrDate + ")) END),CURRENT_DATE()) AS DATE) , CAST(" + arguments.get(0).toString() + " AS DATE)) AS BIGINT)  between 0  and  6) THEN 1 ELSE 0 END";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String dateString = StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString());
      if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE) BETWEEN (DATE_ADD('MONTH',1,DATE_TRUNC('MONTH', " + arguments.get(2).toString() + "))) AND (DATE_ADD('DAY',-1,DATE_ADD('MONTH',(" + arguments.get(1).toString() + "+1),DATE_TRUNC('MONTH', " + arguments.get(2).toString() + "))))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE) BETWEEN (DATE_ADD('MONTH',3,DATE_TRUNC('QUARTER', " + arguments.get(2).toString() + "))) AND (DATE_ADD('DAY',-1,DATE_ADD('MONTH',((" + arguments.get(1).toString() + "+1)*3),DATE_TRUNC('QUARTER', " + arguments.get(2).toString() + "))))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(DATE_DIFF('DAY',CAST(DATE_ADD('DAY', (CASE WHEN(" + dayofWeekOfCurrDate + "  < " + weekStartDay + ") THEN (" + weekStartDay + " -" + dayofWeekOfCurrDate + ") ELSE (7 + (" + weekStartDay + " -" + dayofWeekOfCurrDate + ")) END),CURRENT_DATE) AS DATE) , CAST(" + dateString + " AS DATE)) AS BIGINT)  between 0  and  6) THEN 1 ELSE 0 END";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN DATEADD(M, 1, DATEADD(D, -(DAY(" + arguments.get(2).toString() + ")-1), " + arguments.get(2).toString() + "))  AND  DATEADD(M, (" + arguments.get(1).toString() + "+1), DATEADD(D, -DAY(" + arguments.get(2).toString() + "), " + arguments.get(2).toString() + "))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN  DATEADD(qq, DATEDIFF(qq, 0, " + arguments.get(2).toString() + ") + 1, 0)  AND  DATEADD (dd, -1, DATEADD(qq, DATEDIFF(qq, 0, " + arguments.get(2).toString() + ") +(" + arguments.get(1).toString() + "+1), 0))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(DATEDIFF(DAY,CAST(DATEADD(DAY, (CASE WHEN(" + dayofWeekOfCurrDate + "  < " + weekStartDay + ") THEN (" + weekStartDay + " -" + dayofWeekOfCurrDate + ") ELSE (7 + (" + weekStartDay + " -" + dayofWeekOfCurrDate + ")) END),CONVERT(datetime,GETDATE(),102)) AS DATE) , CAST(" + arguments.get(0).toString() + " AS DATE)) AS BIGINT)  between 0  and  6) THEN 1 ELSE 0 END";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN  DATE_ADD(DATE_TRUNC(" + arguments.get(2).toString() + ", MONTH), INTERVAL 1 MONTH) AND LAST_DAY(DATE_ADD(" + arguments.get(2).toString() + ",INTERVAL (" + arguments.get(1).toString() + ") MONTH))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN  DATE_ADD(DATE_TRUNC(" + arguments.get(2).toString() + ", QUARTER), INTERVAL 3 MONTH) AND DATE_SUB(DATE_ADD(DATE_TRUNC(" + arguments.get(2).toString() + ",QUARTER),INTERVAL ((" + arguments.get(1).toString() + "+1)*3) MONTH),INTERVAL 1 DAY)  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(DATETIME_DIFF(CAST(DATETIME_ADD(CURRENT_DATETIME, INTERVAL (CASE WHEN(" + dayofWeekOfCurrDate + "  < " + weekStartDay + ") THEN (" + weekStartDay + " -" + dayofWeekOfCurrDate + ") ELSE (7 + (" + weekStartDay + " -" + dayofWeekOfCurrDate + ")) END) DAY ) AS DATETIME) , CAST(" + arguments.get(0).toString() + " AS DATETIME),DAY) AS INT64)  between 0  and  6) THEN 1 ELSE 0 END";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Boolean isDenodo = from_sqs != null && from_sqs.isDenodo();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String dateString = isDenodo ? StringFunctions.handleLiteralStringDateForDenodo(arguments.get(0).toString()) : StringFunctions.handleLiteralStringDateForOracle(arguments.get(0).toString());
      String addMonthSyntax = isDenodo ? "ADDMONTH" : "ADD_MONTHS";
      String weekStartDay;
      if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
         weekStartDay = isDenodo ? "LASTDAYOFMONTH" : "LAST_DAY";
         qry = "CASE WHEN CAST(" + dateString + " AS DATE)  BETWEEN  " + addMonthSyntax + "(TRUNC(" + arguments.get(2).toString() + ", 'MM'),1) AND " + weekStartDay + "(" + addMonthSyntax + "(" + arguments.get(2).toString() + "," + arguments.get(1).toString() + "))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
         if (isDenodo) {
            qry = "CASE WHEN " + dateString + " BETWEEN ADDMONTH(TRUNC(" + arguments.get(2).toString() + ", 'Q'),3) AND ADDDAY(ADDMONTH(TRUNC(" + arguments.get(2).toString() + " , 'Q'),(" + arguments.get(1).toString() + "+1)*3), -1) THEN 1 ELSE 0 END";
         } else {
            qry = "CASE WHEN " + dateString + " BETWEEN ADD_MONTHS(TRUNC(" + arguments.get(2).toString() + " , 'Q'),3) AND (ADD_MONTHS(TRUNC(" + arguments.get(2).toString() + " , 'Q'),(" + arguments.get(1).toString() + "+1)*3) -1) THEN 1 ELSE 0 END";
         }
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
         weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         if (isDenodo) {
            qry = "CASE WHEN (CAST(GETDAYSBETWEEN(CAST(ADDDAY(CURRENT_DATE, (CASE WHEN(" + dayofWeekOfCurrDate + "  < " + weekStartDay + ") THEN (" + weekStartDay + " -" + dayofWeekOfCurrDate + ") ELSE (7 + (" + weekStartDay + " -" + dayofWeekOfCurrDate + ")) END)) AS DATE),CAST(" + dateString + " AS DATE)) AS BIGINT) between 0  and  6) THEN 1 ELSE 0 END";
         } else {
            qry = "CASE WHEN (CAST(" + dateString + " AS DATE)-(TO_DATE(SYSDATE)+(INTERVAL '1' DAY) * (CASE WHEN " + dayofWeekOfCurrDate + " < " + weekStartDay + " THEN (" + weekStartDay + "-" + dayofWeekOfCurrDate + ") ELSE (7+(" + weekStartDay + "-" + dayofWeekOfCurrDate + ")) END))) BETWEEN 0 AND 6 THEN 1 ELSE 0 END";
         }
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String dateString = StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString());
      if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE)  BETWEEN  ADD_MONTHS(ADD_DAYS(LAST_DAY(ADD_MONTHS(" + arguments.get(2).toString() + ",-1)),1),1) AND LAST_DAY(ADD_MONTHS(" + arguments.get(2).toString() + "," + arguments.get(1).toString() + "))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
         qry = "CASE WHEN " + dateString + " BETWEEN ADD_MONTHS(ADD_DAYS( ADD_MONTHS(" + arguments.get(2).toString() + ",(MOD( MONTH(" + arguments.get(2).toString() + ") -1, 3 )) * -1) ,DAYOFMONTH(" + arguments.get(2).toString() + ")* -1 + 1 ),3) AND (ADD_MONTHS(ADD_DAYS( ADD_MONTHS(" + arguments.get(2).toString() + ",(MOD( MONTH(" + arguments.get(2).toString() + ") -1, 3 )) * -1) ,DAYOFMONTH(" + arguments.get(2).toString() + ")* -1 + 1 ),((" + arguments.get(1).toString() + "+1)*3) -1)) THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(DAYS_BETWEEN(CAST(ADD_DAYS(CURRENT_DATE, (CASE WHEN(" + dayofWeekOfCurrDate + "  < " + weekStartDay + ") THEN (" + weekStartDay + " -" + dayofWeekOfCurrDate + ") ELSE (7 + (" + weekStartDay + " -" + dayofWeekOfCurrDate + ")) END)) AS DATE),CAST(" + dateString + " AS DATE)) AS BIGINT) between 0  and  6) THEN 1 ELSE 0 END";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String dateString = StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString());
      if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
         qry = "CASE WHEN DATE(" + dateString + ")  BETWEEN  ADD_MONTHS((LAST_DAY(ADD_MONTHS(" + arguments.get(2).toString() + ",-1))+1 UNITS DAY),1) AND LAST_DAY(ADD_MONTHS(" + arguments.get(2).toString() + "," + arguments.get(1).toString() + "))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
         qry = "CASE WHEN DATE(" + dateString + ")  BETWEEN (YEAR(" + arguments.get(2).toString() + ")::LVARCHAR || '-01-01')::DATE + (QUARTER(" + arguments.get(2).toString() + ")*3) UNITS MONTH AND ((YEAR(" + arguments.get(2).toString() + ")::LVARCHAR || '-01-01')::DATE + ((QUARTER(" + arguments.get(2).toString() + ")+" + arguments.get(1).toString() + ")*3) UNITS MONTH - 1 UNITS DAY)  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN (CAST((DATE(" + dateString + ") - DATE((DATE(CURRENT) + (CASE WHEN(" + dayofWeekOfCurrDate + "  < " + weekStartDay + ") THEN (" + weekStartDay + " -" + dayofWeekOfCurrDate + ") ELSE (7 + (" + weekStartDay + " -" + dayofWeekOfCurrDate + ")) END) UNITS DAY))) AS BIGINT) between 0  and  6) THEN 1 ELSE 0 END ";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String dateString = StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString());
      if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
         qry = "CASE WHEN DATE(" + dateString + ") BETWEEN datetime(datetime(date(datetime(" + arguments.get(2).toString() + ",'-1 months'),'start of month','+1 month','-1 day'),'+1 days'),'+1 months') AND date(" + arguments.get(2).toString() + ",'start of month','+1 month','-1 day') THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
         qry = "CASE WHEN " + dateString + " BETWEEN datetime(datetime(datetime(" + arguments.get(2).toString() + ",((MOD( strftime('%m'," + arguments.get(2).toString() + ") -1, 3 )) * -1) || ' months'),strftime('%d'," + arguments.get(2).toString() + ")* -1+1),'+3 months') AND datetime(datetime(datetime(" + arguments.get(2).toString() + ",((MOD( strftime('%m'," + arguments.get(2).toString() + ") -1, 3 )) * -1) || ' months'),strftime('%d'," + arguments.get(2).toString() + ")* -1+1),'-' || ((((" + arguments.get(1).toString() + ")+1)*3) -1) || ' months') THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(-(julianday(DATE(DATETIME(DATE(),(CASE WHEN(" + dayofWeekOfCurrDate + "  < " + weekStartDay + ") THEN (" + weekStartDay + " -" + dayofWeekOfCurrDate + ") ELSE (7 + (" + weekStartDay + " -" + dayofWeekOfCurrDate + ")) END) || ' days'))) - julianday(DATE(" + dateString + "))) AS INTEGER) between 0 and 6) THEN 1 ELSE 0 END";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public SelectColumn current_date() {
      SelectColumn sc_CurrDate = new SelectColumn();
      FunctionCalls fn_CurrDate = new FunctionCalls();
      TableColumn tb_CurrDate = new TableColumn();
      tb_CurrDate.setColumnName("CURRENT_DATE");
      fn_CurrDate.setFunctionName(tb_CurrDate);
      Vector vc_CurrDateIn = new Vector();
      Vector vc_CurrDateOut = new Vector();
      fn_CurrDate.setFunctionArguments(vc_CurrDateIn);
      vc_CurrDateOut.addElement(fn_CurrDate);
      sc_CurrDate.setColumnExpression(vc_CurrDateOut);
      return sc_CurrDate;
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN (" + arguments.get(2).toString() + " - (DAY(" + arguments.get(2).toString() + ")-1) DAY + 1 MONTH) AND (LAST_DAY(" + arguments.get(2).toString() + "+" + arguments.get(1).toString() + " MONTH))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN CAST(CAST(YEAR(" + arguments.get(2).toString() + ") AS VARCHAR) || '-01-01' AS DATE) + (QUARTER(" + arguments.get(2).toString() + ")*3) MONTH AND (CAST(CAST(YEAR(" + arguments.get(2).toString() + ") AS VARCHAR) || '-01-01' AS DATE) + ((QUARTER(" + arguments.get(2).toString() + ")+" + arguments.get(1).toString() + ")*3) MONTH - 1 DAY)  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(TIMESTAMPDIFF(16,CHAR(CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) - CAST((CURRENT DATE + (CASE WHEN(" + dayofWeekOfCurrDate + "  < " + weekStartDay + ") THEN (" + weekStartDay + " -" + dayofWeekOfCurrDate + ") ELSE (7 + (" + weekStartDay + " -" + dayofWeekOfCurrDate + ")) END) DAYS ) AS TIMESTAMP) )) AS INTEGER)  between 0  and  6) THEN 1 ELSE 0 END";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
         qry = "Iif(CDate(" + arguments.get(0).toString() + ")  BETWEEN DATEADD('m', 1, DATEADD('d', -(DAY(" + arguments.get(2).toString() + ")-1), " + arguments.get(2).toString() + "))  AND  DATEADD('m', (" + arguments.get(1).toString() + "+1), DATEADD('d', -DAY(" + arguments.get(2).toString() + "), " + arguments.get(2).toString() + ")), 1,0)";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
         qry = "Iif(CDate(" + arguments.get(0).toString() + ")  BETWEEN  DATEADD('q', DATEDIFF('q', 0, " + arguments.get(2).toString() + ") + 1, 0)  AND  DATEADD ('d', -1, DATEADD('q', DATEDIFF('q', 0, " + arguments.get(2).toString() + ") +(" + arguments.get(1).toString() + "+1), 0)), 1, 0)";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "Iif((CLng(DATEDIFF('d',CDate(DATEADD('d', (Iif((" + dayofWeekOfCurrDate + "  < " + weekStartDay + ") , (" + weekStartDay + " -" + dayofWeekOfCurrDate + ") ,(7 + (" + weekStartDay + " -" + dayofWeekOfCurrDate + ")))),now())) , CDate(" + arguments.get(0).toString() + ")))  between 0  and  6),1 , 0)";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
      String qry;
      if (fnStr.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE)  BETWEEN (TRUNC(" + arguments.get(2).toString() + ", 'MM') + 1 MONTH) AND LAST_DAY(" + arguments.get(2).toString() + " + " + arguments.get(1).toString() + " MONTH)  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE)  BETWEEN TRUNC(" + arguments.get(2).toString() + ", 'YY') + (QUARTER(" + arguments.get(2).toString() + ")*3) MONTH AND (TRUNC(" + arguments.get(2).toString() + ", 'YY') + ((QUARTER(" + arguments.get(2).toString() + ")+" + arguments.get(1).toString() + ")*3) MONTH - 1 DAY)  THEN 1 ELSE 0 END";
      } else {
         if (!fnStr.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
            this.setFunctionArguments(arguments);
            return;
         }

         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(DATEDIFF('d',CAST(DATEADD('d', (CASE WHEN(" + dayofWeekOfCurrDate + "  < " + weekStartDay + ") THEN (" + weekStartDay + " -" + dayofWeekOfCurrDate + ") ELSE (7 + (" + weekStartDay + " -" + dayofWeekOfCurrDate + ")) END),CURRENT_DATE) AS DATE) , CAST(" + dateString + " AS DATE)) AS BIGINT)  BETWEEN 0  AND  6) THEN 1 ELSE 0 END";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }
}
