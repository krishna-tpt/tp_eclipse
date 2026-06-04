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

public class previousDate extends FunctionCalls {
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
         if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
            weekStDay = "CASE WHEN CAST(" + weekMode + " AS DATE)  BETWEEN  (TRUNC(" + arguments.get(2).toString() + ", 'MM') - (" + arguments.get(1).toString() + ") MONTH) AND LAST_DAY((" + arguments.get(2).toString() + " - 1 MONTH))  THEN 1 ELSE 0 END";
         } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
            weekStDay = "CASE WHEN CAST(" + weekMode + " AS DATE)  BETWEEN  (TRUNC(" + arguments.get(2).toString() + ", 'YY') + ((QUARTER(" + arguments.get(2).toString() + ")-1)*3) MONTH -  (3*(" + arguments.get(1).toString() + ")) MONTH) AND (TRUNC(" + arguments.get(2).toString() + ", 'YY') + ((QUARTER(" + arguments.get(2).toString() + ")-1)*3) MONTH - 1 DAY)  THEN 1 ELSE 0 END";
         } else {
            if (!fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
               this.setFunctionArguments(arguments);
               return;
            }

            String weekStartDay = arguments.get(1).toString();
            String dayofWeekOfDateCol = arguments.get(2).toString();
            weekStDay = "CASE WHEN (CAST(DATEDIFF(DAY, CAST((CURRENT_DATE - (CASE WHEN(" + dayofWeekOfDateCol + "  >= " + weekStartDay + ") THEN (" + dayofWeekOfDateCol + " -" + weekStartDay + ") ELSE (7 + (" + dayofWeekOfDateCol + " -" + weekStartDay + ")) END ) DAY) AS DATE),CAST(" + weekMode + " AS DATE))AS BIGINT)  BETWEEN 1  AND  7) THEN 1 ELSE 0 END";
         }

         this.functionName.setColumnName(weekStDay);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else if (from_sqs != null && from_sqs.isMySqlLive()) {
         if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
            weekStDay = "IF(" + arguments.get(0).toString() + " BETWEEN  date_sub(STR_TO_DATE(CONCAT_WS('-',YEAR(" + arguments.get(2).toString() + "),((QUARTER(" + arguments.get(2).toString() + ")*3)-2),1),'%Y-%m-%d'),interval (" + arguments.get(1).toString() + ") quarter) AND date_sub(date_add(date_format(" + arguments.get(2).toString() + ",'%Y-01-01'),interval (quarter(" + arguments.get(2).toString() + ")-1) quarter) , interval 1 day),1,0)";
            this.functionName.setColumnName(weekStDay);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
            weekStDay = "If(" + arguments.get(0).toString() + " BETWEEN date_sub(date_sub(" + arguments.get(2).toString() + ",interval (" + arguments.get(1).toString() + ") month),interval (day(" + arguments.get(2).toString() + ")-1) day) and date_sub(" + arguments.get(2).toString() + ",interval (day(" + arguments.get(2).toString() + ")) day),1,0)";
            this.functionName.setColumnName(weekStDay);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
            weekStDay = "if(datediff(date_sub(current_date(), interval if(" + arguments.get(2).toString() + " >= " + arguments.get(1).toString() + ", (" + arguments.get(2).toString() + "-" + arguments.get(1).toString() + "), (7+(" + arguments.get(2).toString() + "-" + arguments.get(1).toString() + "))) day)," + arguments.get(0).toString() + ") between 1 and 7,1,0)";
            this.functionName.setColumnName(weekStDay);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         }
      } else {
         SelectColumn sc_wi;
         Vector vc_if;
         WhereItem wi_if;
         WhereColumn if_left;
         Vector vc_DayOfWeekIn;
         WhereColumn if_left;
         Vector vc_if_left;
         SelectColumn sc_Date;
         FunctionCalls fn_Date;
         TableColumn tb_Date;
         Vector vc_DateIn;
         Vector vc_DateOut;
         SelectColumn sc_Limit;
         Vector vc_yearWeekleftOut;
         SelectColumn sc_dateAdd;
         Vector vc_intr;
         Vector vc_dayofweekOut;
         Vector vc_ifStatement;
         if (!fnStr.equalsIgnoreCase("ISPREVIOUS_NYEAR") && !fnStr.equalsIgnoreCase("IS_PREVIOUS_NYEAR")) {
            if (!fnStr.equalsIgnoreCase("ISPREVIOUS_NQUARTER") && !fnStr.equalsIgnoreCase("IS_PREVIOUS_NQUARTER")) {
               if (!fnStr.equalsIgnoreCase("ISPREVIOUS_NMONTH") && !fnStr.equalsIgnoreCase("IS_PREVIOUS_NMONTH")) {
                  FunctionCalls fn_dateAdd;
                  TableColumn tb_dateAdd;
                  Vector vc_dateAddIn;
                  Vector vc_dateAddOut;
                  SelectColumn sc_intr;
                  TableColumn tbCl_interval;
                  SelectColumn sc_weekday;
                  FunctionCalls fn_weekday;
                  TableColumn tb_yearWeekright;
                  Vector vc_weekdayIn;
                  if (!fnStr.equalsIgnoreCase("ISPREVIOUSWEEK") && !fnStr.equalsIgnoreCase("IS_PREVIOUS_WEEK")) {
                     if (!fnStr.equalsIgnoreCase("ISPREVIOUS_NDAY") && !fnStr.equalsIgnoreCase("IS_PREVIOUS_NDAY")) {
                        SelectColumn sc_interval;
                        Vector vc_prevnmonth;
                        if (fnStr.equalsIgnoreCase("YESTERDAY")) {
                           this.functionName.setColumnName("DATE_SUB");
                           arguments.addElement(this.current_date());
                           sc_interval = new SelectColumn();
                           vc_prevnmonth = new Vector();
                           TableColumn tbCl_interval = new TableColumn();
                           tbCl_interval.setColumnName("INTERVAL");
                           vc_prevnmonth.addElement(tbCl_interval);
                           vc_prevnmonth.add("1 ");
                           vc_prevnmonth.add("DAY");
                           sc_interval.setColumnExpression(vc_prevnmonth);
                           arguments.addElement(sc_interval);
                           this.setFunctionArguments(arguments);
                        } else {
                           TableColumn tbCl_interval;
                           if (fnStr.equalsIgnoreCase("PREVIOUS_NDAY")) {
                              this.functionName.setColumnName("DATE_SUB");
                              sc_interval = new SelectColumn();
                              vc_prevnmonth = new Vector();
                              vc_prevnmonth.addElement(arguments.get(0));
                              vc_if = new Vector();
                              tbCl_interval = new TableColumn();
                              tbCl_interval.setColumnName("INTERVAL");
                              vc_if.addElement(tbCl_interval);
                              vc_if.add("(");
                              vc_if.addElement(arguments.elementAt(1));
                              vc_if.add(")");
                              vc_if.add(" DAY");
                              sc_interval.setColumnExpression(vc_if);
                              vc_prevnmonth.addElement(sc_interval);
                              this.setFunctionArguments(vc_prevnmonth);
                           } else if (fnStr.equalsIgnoreCase("PREVIOUS_NMONTH")) {
                              this.functionName.setColumnName("DATE_SUB");
                              sc_interval = new SelectColumn();
                              vc_prevnmonth = new Vector();
                              vc_prevnmonth.addElement(arguments.get(0));
                              vc_if = new Vector();
                              tbCl_interval = new TableColumn();
                              tbCl_interval.setColumnName("INTERVAL");
                              vc_if.addElement(tbCl_interval);
                              vc_if.add("(");
                              vc_if.addElement(arguments.get(1));
                              vc_if.add(")");
                              vc_if.add(" MONTH");
                              sc_interval.setColumnExpression(vc_if);
                              vc_prevnmonth.addElement(sc_interval);
                              this.setFunctionArguments(vc_prevnmonth);
                           }
                        }
                     } else {
                        this.functionName.setColumnName("IF");
                        vc_ifStatement = new Vector();
                        sc_wi = new SelectColumn();
                        vc_if = new Vector();
                        wi_if = new WhereItem();
                        if_left = new WhereColumn();
                        vc_DayOfWeekIn = new Vector();
                        if_left = new WhereColumn();
                        vc_if_left = new Vector();
                        sc_Date = new SelectColumn();
                        fn_Date = new FunctionCalls();
                        tb_Date = new TableColumn();
                        tb_Date.setColumnName("DATE");
                        fn_Date.setFunctionName(tb_Date);
                        vc_DateIn = new Vector();
                        vc_DateOut = new Vector();
                        vc_DateIn.addElement(arguments.get(0));
                        fn_Date.setFunctionArguments(vc_DateIn);
                        vc_DateOut.addElement(fn_Date);
                        sc_Date.setColumnExpression(vc_DateOut);
                        vc_DayOfWeekIn.addElement(sc_Date);
                        if_left.setColumnExpression(vc_DayOfWeekIn);
                        wi_if.setLeftWhereExp(if_left);
                        wi_if.setOperator("BETWEEN");
                        sc_Limit = new SelectColumn();
                        vc_yearWeekleftOut = new Vector();
                        sc_dateAdd = new SelectColumn();
                        fn_dateAdd = new FunctionCalls();
                        tb_dateAdd = new TableColumn();
                        tb_dateAdd.setColumnName("DATE_SUB");
                        fn_dateAdd.setFunctionName(tb_dateAdd);
                        vc_dateAddIn = new Vector();
                        vc_dateAddOut = new Vector();
                        vc_dateAddIn.addElement(this.current_date());
                        sc_intr = new SelectColumn();
                        vc_intr = new Vector();
                        tbCl_interval = new TableColumn();
                        tbCl_interval.setColumnName("INTERVAL");
                        vc_intr.addElement(tbCl_interval);
                        vc_intr.add("(");
                        vc_intr.addElement(arguments.get(1));
                        vc_intr.add(")");
                        vc_intr.addElement(" day");
                        sc_intr.setColumnExpression(vc_intr);
                        vc_dateAddIn.addElement(sc_intr);
                        fn_dateAdd.setFunctionArguments(vc_dateAddIn);
                        vc_dateAddOut.addElement(fn_dateAdd);
                        sc_dateAdd.setColumnExpression(vc_dateAddOut);
                        vc_yearWeekleftOut.addElement(sc_dateAdd);
                        vc_yearWeekleftOut.addElement("AND");
                        sc_weekday = new SelectColumn();
                        fn_weekday = new FunctionCalls();
                        tb_yearWeekright = new TableColumn();
                        tb_yearWeekright.setColumnName("DATE_SUB");
                        fn_weekday.setFunctionName(tb_yearWeekright);
                        vc_weekdayIn = new Vector();
                        vc_dayofweekOut = new Vector();
                        vc_weekdayIn.addElement(this.current_date());
                        SelectColumn sc_intrHigh = new SelectColumn();
                        Vector vc_intrHigh = new Vector();
                        TableColumn tbCl_intervalHigh = new TableColumn();
                        tbCl_intervalHigh.setColumnName("INTERVAL");
                        vc_intrHigh.addElement(tbCl_intervalHigh);
                        vc_intrHigh.add("(");
                        vc_intrHigh.add("1");
                        vc_intrHigh.add(")");
                        vc_intrHigh.addElement(" day");
                        sc_intrHigh.setColumnExpression(vc_intrHigh);
                        vc_weekdayIn.addElement(sc_intrHigh);
                        fn_weekday.setFunctionArguments(vc_weekdayIn);
                        vc_dayofweekOut.addElement(fn_weekday);
                        sc_weekday.setColumnExpression(vc_dayofweekOut);
                        vc_yearWeekleftOut.addElement(sc_weekday);
                        sc_Limit.setColumnExpression(vc_yearWeekleftOut);
                        vc_if_left.addElement(sc_Limit);
                        if_left.setColumnExpression(vc_if_left);
                        wi_if.setRightWhereExp(if_left);
                        vc_if.addElement(wi_if);
                        sc_wi.setColumnExpression(vc_if);
                        vc_ifStatement.addElement(sc_wi);
                        vc_ifStatement.addElement("1");
                        vc_ifStatement.addElement("0");
                        this.setFunctionArguments(vc_ifStatement);
                     }
                  } else {
                     weekStDay = "";
                     weekMode = null;
                     SelectColumn sc_DayOfWeek;
                     if (arguments.size() > 1 && arguments.elementAt(1) instanceof SelectColumn) {
                        sc_DayOfWeek = (SelectColumn)arguments.elementAt(1);
                        Vector vc = sc_DayOfWeek.getColumnExpression();
                        if (!(vc.elementAt(0) instanceof String)) {
                           throw new ConvertException("Invalid Argument Value for Function ISPREVIOUSWEEK", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"ISPREVIOUSWEEK", "WEEK_STARTDAY"});
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
                        vc_if = new Vector();
                        SelectColumn sc_wi = new SelectColumn();
                        Vector vc_wi = new Vector();
                        WhereItem wi_if = new WhereItem();
                        if_left = new WhereColumn();
                        vc_if_left = new Vector();
                        WhereColumn if_right = new WhereColumn();
                        Vector vc_if_right = new Vector();
                        SelectColumn sc_yearWeekleft = new SelectColumn();
                        FunctionCalls fn_yearWeekleft = new FunctionCalls();
                        TableColumn tb_yearWeekleft = new TableColumn();
                        tb_yearWeekleft.setColumnName("YEARWEEK");
                        fn_yearWeekleft.setFunctionName(tb_yearWeekleft);
                        Vector vc_yearWeekleftIn = new Vector();
                        vc_yearWeekleftOut = new Vector();
                        sc_dateAdd = new SelectColumn();
                        fn_dateAdd = new FunctionCalls();
                        tb_dateAdd = new TableColumn();
                        tb_dateAdd.setColumnName("DATE_ADD");
                        fn_dateAdd.setFunctionName(tb_dateAdd);
                        vc_dateAddIn = new Vector();
                        vc_dateAddOut = new Vector();
                        vc_dateAddIn.addElement(arguments.get(0));
                        sc_intr = new SelectColumn();
                        vc_intr = new Vector();
                        tbCl_interval = new TableColumn();
                        tbCl_interval.setColumnName("INTERVAL");
                        vc_intr.addElement(tbCl_interval);
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

                           vc_dayofweekOut = new Vector();
                           fn_weekday.setFunctionArguments(vc_weekdayIn);
                           vc_dayofweekOut.addElement(fn_weekday);
                           sc_weekday.setColumnExpression(vc_dayofweekOut);
                           vc_intr.add("(");
                           vc_intr.add("8");
                           vc_intr.add("-");
                           vc_intr.addElement(sc_weekday);
                           vc_intr.add(")");
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

                           vc_dayofweekOut = new Vector();
                           fn_weekday.setFunctionArguments(vc_weekdayIn);
                           vc_dayofweekOut.addElement(fn_weekday);
                           sc_weekday.setColumnExpression(vc_dayofweekOut);
                           vc_intr.add("(");
                           vc_intr.add("7");
                           vc_intr.add("-");
                           vc_intr.addElement(sc_weekday);
                           vc_intr.add(")");
                           vc_intr.add(" day");
                           sc_intr.setColumnExpression(vc_intr);
                        }

                        vc_dateAddIn.addElement(sc_intr);
                        fn_dateAdd.setFunctionArguments(vc_dateAddIn);
                        vc_dateAddOut.addElement(fn_dateAdd);
                        sc_dateAdd.setOpenBrace("(");
                        sc_dateAdd.setCloseBrace(")");
                        sc_dateAdd.setColumnExpression(vc_dateAddOut);
                        vc_yearWeekleftIn.addElement(sc_dateAdd);
                        vc_yearWeekleftIn.addElement(weekMode);
                        fn_yearWeekleft.setFunctionArguments(vc_yearWeekleftIn);
                        vc_yearWeekleftOut.addElement(fn_yearWeekleft);
                        sc_yearWeekleft.setColumnExpression(vc_yearWeekleftOut);
                        vc_if_left.addElement(sc_yearWeekleft);
                        if_left.setColumnExpression(vc_if_left);
                        wi_if.setLeftWhereExp(if_left);
                        wi_if.setOperator("=");
                        sc_weekday = new SelectColumn();
                        fn_weekday = new FunctionCalls();
                        tb_yearWeekright = new TableColumn();
                        tb_yearWeekright.setColumnName("YEARWEEK");
                        fn_weekday.setFunctionName(tb_yearWeekright);
                        vc_weekdayIn = new Vector();
                        vc_dayofweekOut = new Vector();
                        vc_weekdayIn.addElement(this.current_date());
                        vc_weekdayIn.addElement(weekMode);
                        fn_weekday.setFunctionArguments(vc_weekdayIn);
                        vc_dayofweekOut.addElement(fn_weekday);
                        sc_weekday.setColumnExpression(vc_dayofweekOut);
                        vc_if_right.addElement(sc_weekday);
                        if_right.setColumnExpression(vc_if_right);
                        wi_if.setRightWhereExp(if_right);
                        vc_wi.addElement(wi_if);
                        sc_wi.setColumnExpression(vc_wi);
                        vc_if.addElement(sc_wi);
                        String t_num = "1";
                        String f_num = "0";
                        vc_if.addElement(t_num);
                        vc_if.addElement(f_num);
                        this.setFunctionArguments(vc_if);
                     } else {
                        this.functionName.setColumnName("ZR_ISPREVIOUSWEEK");
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
                  this.functionName.setColumnName("ZR_ISPREVIOUSMONTH");
                  arguments.addElement(this.current_date());
                  this.setFunctionArguments(arguments);
               }
            } else {
               this.functionName.setColumnName("ZR_ISPREVIOUSQUARTER");
               arguments.addElement(this.current_date());
               this.setFunctionArguments(arguments);
            }
         } else {
            this.functionName.setColumnName("IF");
            vc_ifStatement = new Vector();
            sc_wi = new SelectColumn();
            vc_if = new Vector();
            wi_if = new WhereItem();
            if_left = new WhereColumn();
            vc_DayOfWeekIn = new Vector();
            if_left = new WhereColumn();
            vc_if_left = new Vector();
            sc_Date = new SelectColumn();
            fn_Date = new FunctionCalls();
            tb_Date = new TableColumn();
            tb_Date.setColumnName("YEAR");
            fn_Date.setFunctionName(tb_Date);
            vc_DateIn = new Vector();
            vc_DateOut = new Vector();
            vc_DateIn.addElement(arguments.get(0));
            fn_Date.setFunctionArguments(vc_DateIn);
            vc_DateOut.addElement(fn_Date);
            sc_Date.setColumnExpression(vc_DateOut);
            vc_DayOfWeekIn.addElement(sc_Date);
            if_left.setColumnExpression(vc_DayOfWeekIn);
            wi_if.setLeftWhereExp(if_left);
            wi_if.setOperator(" BETWEEN ");
            sc_Limit = new SelectColumn();
            vc_yearWeekleftOut = new Vector();
            sc_dateAdd = new SelectColumn();
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
            vc_LimitLow.addElement("-");
            vc_LimitLow.addElement(arguments.get(1));
            sc_dateAdd.setOpenBrace("(");
            sc_dateAdd.setCloseBrace(")");
            sc_dateAdd.setColumnExpression(vc_LimitLow);
            vc_yearWeekleftOut.addElement(sc_dateAdd);
            vc_yearWeekleftOut.addElement(" AND ");
            SelectColumn sc_LimitUp = new SelectColumn();
            Vector vc_LimitUp = new Vector();
            SelectColumn sc_EndDate = new SelectColumn();
            FunctionCalls fnCl_EndDate = new FunctionCalls();
            TableColumn tbCl_EndDate = new TableColumn();
            tbCl_EndDate.setColumnName("YEAR");
            fnCl_EndDate.setFunctionName(tbCl_StartDate);
            vc_dayofweekOut = new Vector();
            Vector vc_EndDateOut = new Vector();
            vc_dayofweekOut.addElement(this.current_date());
            fnCl_EndDate.setFunctionArguments(vc_dayofweekOut);
            vc_EndDateOut.addElement(fnCl_EndDate);
            sc_EndDate.setColumnExpression(vc_EndDateOut);
            vc_LimitUp.addElement(sc_EndDate);
            vc_LimitUp.addElement("-");
            vc_LimitUp.addElement("1");
            sc_LimitUp.setOpenBrace("(");
            sc_LimitUp.setCloseBrace(")");
            sc_LimitUp.setColumnExpression(vc_LimitUp);
            vc_yearWeekleftOut.addElement(sc_LimitUp);
            sc_Limit.setColumnExpression(vc_yearWeekleftOut);
            vc_if_left.addElement(sc_Limit);
            if_left.setColumnExpression(vc_if_left);
            wi_if.setRightWhereExp(if_left);
            vc_if.addElement(wi_if);
            sc_wi.setColumnExpression(vc_if);
            vc_ifStatement.addElement(sc_wi);
            vc_ifStatement.addElement("1");
            vc_ifStatement.addElement("0");
            this.setFunctionArguments(vc_ifStatement);
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
         if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
            qry = "case when CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('month',current_date-(interval '1' month * (" + arguments.get(1).toString() + ")))) and (date_trunc('month',current_date)-interval '1' day) then 1 else 0 end";
         } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
            qry = "case when CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('quarter',current_date)-(interval '1' month *3 * (" + arguments.get(1).toString() + "))) and (date_trunc('quarter',current_date)-interval '1' day) then 1 else 0 end";
         } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
            String weekStartDay = arguments.get(1).toString();
            String dayofWeekOfDateCol = arguments.get(2).toString();
            qry = "(CASE WHEN DATE_MI(DATE(((CURRENT_DATE) - ( INTERVAL  '1'  day * ROUND(((CASE WHEN  " + dayofWeekOfDateCol + "   >= " + weekStartDay + " THEN ( " + dayofWeekOfDateCol + "  -" + weekStartDay + ") ELSE (7 + ( " + dayofWeekOfDateCol + "  -" + weekStartDay + ")) END)))) )), DATE(" + arguments.get(0).toString() + "))  between 1  and  7 THEN 1 ELSE 0 END)";
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
      if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
         qry = "if(CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('month',current_date-(interval '1' month * (" + arguments.get(1).toString() + ")))) and (date_trunc('month',current_date)-interval '1' day) ,1 ,0)";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
         qry = "if(CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('quarter',current_date)-(interval '1' month *3 *(" + arguments.get(1).toString() + "))) and (date_trunc('quarter',current_date)-interval '1' day) ,1,0)";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfDateCol = arguments.get(2).toString();
         qry = "if(TIMESTAMPDIFF(DAY,CAST(" + arguments.get(0).toString() + " AS DATE) , CAST((CURRENT_DATE - interval  '1'  day * (if(" + dayofWeekOfDateCol + "  >= " + weekStartDay + ", (" + dayofWeekOfDateCol + " -" + weekStartDay + "), (7 + (" + dayofWeekOfDateCol + " -" + weekStartDay + "))))) AS DATE))  between 1  and  7, 1, 0)";
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
      if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN DATEADD(M, -(" + arguments.get(1).toString() + "),DATEADD(D, -(DAY(" + arguments.get(2).toString() + ") -1), " + arguments.get(2).toString() + "))  AND  DATEADD(D, -(DAY(" + arguments.get(2).toString() + ")), " + arguments.get(2).toString() + ")  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN (DATEADD(qq, DATEDIFF(qq, 0, " + arguments.get(2).toString() + ") -(" + arguments.get(1).toString() + "), 0)) AND (DATEADD (dd, -1, DATEADD(qq, DATEDIFF(qq, 0, " + arguments.get(2).toString() + "), 0)))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfDateCol = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(DATEDIFF(DAY,CAST(" + arguments.get(0).toString() + " AS DATE) , CAST(DATEADD(DAY , - (CASE WHEN(" + dayofWeekOfDateCol + "  >= " + weekStartDay + ") THEN (" + dayofWeekOfDateCol + " -" + weekStartDay + ") ELSE (7 + (" + dayofWeekOfDateCol + " -" + weekStartDay + ")) END ),GETDATE()) AS DATE))AS BIGINT)  between 1  and  7) THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN  DATE_SUB(DATE_TRUNC(" + arguments.get(2).toString() + ", MONTH), INTERVAL (" + arguments.get(1).toString() + ") MONTH) AND LAST_DAY(DATE_SUB(" + arguments.get(2).toString() + ",INTERVAL 1 MONTH))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN  DATE_SUB(DATE_TRUNC(" + arguments.get(2).toString() + ", QUARTER), INTERVAL 3*(" + arguments.get(1).toString() + ") MONTH) AND DATE_SUB(DATE_TRUNC(" + arguments.get(2).toString() + ",QUARTER),INTERVAL 1 DAY)  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfDateCol = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(DATE_DIFF(CAST(" + arguments.get(0).toString() + " AS DATE) , CAST(DATE_SUB(CURRENT_DATE(),INTERVAL (CASE WHEN(" + dayofWeekOfDateCol + "  >= " + weekStartDay + ") THEN (" + dayofWeekOfDateCol + " -" + weekStartDay + ") ELSE (7 + (" + dayofWeekOfDateCol + " -" + weekStartDay + ")) END ) DAY) AS DATE),DAY)AS INT64)  between 1  and  7) THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN DATEADD(MONTH, -(" + arguments.get(1).toString() + "), DATE_TRUNC(MONTH, " + arguments.get(2).toString() + ")) AND LAST_DAY(DATEADD(MONTH,-1," + arguments.get(2).toString() + "))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN DATEADD(MONTH, -3*(" + arguments.get(1).toString() + "), DATE_TRUNC(QUARTER, " + arguments.get(2).toString() + ")) AND DATEADD(DAY,-1,DATE_TRUNC(QUARTER, " + arguments.get(2).toString() + "))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfDateCol = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(DATEDIFF(DAY,CAST(" + arguments.get(0).toString() + " AS DATE) , CAST(DATEADD(DAY , - (CASE WHEN(" + dayofWeekOfDateCol + "  >= " + weekStartDay + ") THEN (" + dayofWeekOfDateCol + " -" + weekStartDay + ") ELSE (7 + (" + dayofWeekOfDateCol + " -" + weekStartDay + ")) END ),CURRENT_DATE()) AS DATE))AS BIGINT)  between 1  and  7) THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE) BETWEEN (DATE_ADD('MONTH',-(" + arguments.get(1).toString() + "),DATE_TRUNC('MONTH', " + arguments.get(2).toString() + "))) AND (DATE_ADD('DAY',-1,DATE_TRUNC('MONTH', " + arguments.get(2).toString() + ")))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE) BETWEEN (DATE_ADD('MONTH',-3*(" + arguments.get(1).toString() + "),DATE_TRUNC('QUARTER', " + arguments.get(2).toString() + "))) AND (DATE_ADD('DAY',-1,DATE_TRUNC('QUARTER', " + arguments.get(2).toString() + ")))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfDateCol = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(DATE_DIFF('DAY',CAST(" + dateString + " AS DATE) , CAST(DATE_ADD('DAY' , - (CASE WHEN(" + dayofWeekOfDateCol + "  >= " + weekStartDay + ") THEN (" + dayofWeekOfDateCol + " -" + weekStartDay + ") ELSE (7 + (" + dayofWeekOfDateCol + " -" + weekStartDay + ")) END ),CURRENT_DATE) AS DATE))AS BIGINT)  between 1  and  7) THEN 1 ELSE 0 END";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String dateString = isdenodo ? StringFunctions.handleLiteralStringDateForDenodo(arguments.get(0).toString()) : StringFunctions.handleLiteralStringDateForOracle(arguments.get(0).toString());
      String addMonthSyntax = isdenodo ? "ADDMONTH" : "ADD_MONTHS";
      String weekStartDay;
      if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
         weekStartDay = isdenodo ? "LASTDAYOFMONTH" : "LAST_DAY";
         qry = "CASE WHEN CAST(" + dateString + " AS DATE)  BETWEEN  " + addMonthSyntax + "(TRUNC(" + arguments.get(2).toString() + ", 'MM'), -(" + arguments.get(1).toString() + ")) AND " + weekStartDay + "(" + addMonthSyntax + "(" + arguments.get(2).toString() + ",-1))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
         if (isdenodo) {
            qry = "CASE WHEN " + dateString + " BETWEEN ADDMONTH(TRUNC(" + arguments.get(2).toString() + " , 'Q'),-3*(" + arguments.get(1).toString() + ")) AND ADDDAY(TRUNC(" + arguments.get(2).toString() + " , 'Q'),-1) THEN 1 ELSE 0 END";
         } else {
            qry = "CASE WHEN " + dateString + " BETWEEN ADD_MONTHS(TRUNC(" + arguments.get(2).toString() + " , 'Q'),-3*(" + arguments.get(1).toString() + ")) AND (TRUNC(" + arguments.get(2).toString() + " , 'Q')-1) THEN 1 ELSE 0 END";
         }
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
         weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         if (isdenodo) {
            qry = "CASE WHEN (CAST(GETDAYSBETWEEN(CAST(" + dateString + " AS DATE) , CAST(ADDDAY(CURRENT_DATE, - (CASE WHEN (" + dayofWeekOfCurrDate + "  >= " + weekStartDay + ") THEN (" + dayofWeekOfCurrDate + " -" + weekStartDay + ") ELSE (7 + (" + dayofWeekOfCurrDate + " -" + weekStartDay + ")) END )) AS DATE)) AS BIGINT)  between 1  and  7) THEN 1 ELSE 0 END";
         } else {
            qry = "CASE WHEN ((TO_DATE(SYSDATE)-(INTERVAL '1' DAY) * (CASE WHEN " + dayofWeekOfCurrDate + " >= " + weekStartDay + " THEN " + dayofWeekOfCurrDate + "-" + weekStartDay + " ELSE (7+" + dayofWeekOfCurrDate + "-" + weekStartDay + ") END)) - CAST(" + dateString + " AS DATE)) BETWEEN 1 AND 7 THEN 1 ELSE 0 END";
         }
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
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
      if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN (" + arguments.get(2).toString() + " - (DAY(" + arguments.get(2).toString() + ")-1) DAY -(" + arguments.get(1).toString() + ") MONTH) AND (LAST_DAY(" + arguments.get(2).toString() + " - 1 MONTH))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN (CAST((CAST(YEAR(" + arguments.get(2).toString() + ") AS VARCHAR) || '-' || CAST(((QUARTER(" + arguments.get(2).toString() + ")*3)-2) AS VARCHAR) || '-01') AS DATE) - (" + arguments.get(1).toString() + "*3) MONTH) AND (CAST(CAST(YEAR(" + arguments.get(2).toString() + ") AS VARCHAR) || '-01-01' AS DATE) + ((QUARTER(" + arguments.get(2).toString() + ")-1)*3) MONTH - 1 DAY)  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfDateCol = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(TIMESTAMPDIFF(16,CHAR(CAST(" + arguments.get(0).toString() + " AS DATE) - CAST((CURRENT DATE - (CASE WHEN(" + dayofWeekOfDateCol + "  >= " + weekStartDay + ") THEN (" + dayofWeekOfDateCol + " -" + weekStartDay + ") ELSE (7 + (" + dayofWeekOfDateCol + " -" + weekStartDay + ")) END ) DAYS) AS DATE)))AS INTEGER)  between 1  and  7) THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE)  BETWEEN  ADD_MONTHS(ADD_DAYS(LAST_DAY(ADD_MONTHS(" + arguments.get(2).toString() + ",-1)),1), -(" + arguments.get(1).toString() + ")) AND LAST_DAY(ADD_MONTHS(" + arguments.get(2).toString() + ",-1))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
         qry = "CASE WHEN " + dateString + " BETWEEN ADD_MONTHS(ADD_DAYS( ADD_MONTHS(" + arguments.get(2).toString() + ",(MOD( MONTH(" + arguments.get(2).toString() + ") -1, 3 )) * -1) ,DAYOFMONTH(" + arguments.get(2).toString() + ")* -1 + 1 ),-3*(" + arguments.get(1).toString() + ")) AND (ADD_DAYS( ADD_MONTHS( " + arguments.get(2).toString() + ",(MOD( MONTH(" + arguments.get(2).toString() + ") -1, 3 )) * -1) ,(DAYOFMONTH(" + arguments.get(2).toString() + ")* -1 + 1 )-1)) THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(DAYS_BETWEEN(CAST(" + dateString + " AS DATE) , CAST(ADD_DAYS(CURRENT_DATE, - (CASE WHEN(" + dayofWeekOfCurrDate + "  >= " + weekStartDay + ") THEN (" + dayofWeekOfCurrDate + " -" + weekStartDay + ") ELSE (6 + (" + dayofWeekOfCurrDate + " -" + weekStartDay + ")) END )) AS DATE))AS BIGINT)  between 1  and  7) THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
         qry = "CASE WHEN DATE(" + dateString + ")  BETWEEN  ADD_MONTHS((LAST_DAY(ADD_MONTHS(" + arguments.get(2).toString() + ",-1))+1 UNITS DAY), -(" + arguments.get(1).toString() + ")) AND LAST_DAY(ADD_MONTHS(" + arguments.get(2).toString() + ",-1))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
         qry = "CASE WHEN DATE(" + dateString + ")  BETWEEN ((YEAR(" + arguments.get(2).toString() + ")::LVARCHAR || '-' || ((QUARTER(" + arguments.get(2).toString() + ")*3)-2)::LVARCHAR || '-01')::DATE - (" + arguments.get(1).toString() + "*3) UNITS MONTH) AND ((YEAR(" + arguments.get(2).toString() + ")::VARCHAR || '-01-01')::DATE + ((QUARTER(" + arguments.get(2).toString() + ")-1)*3) UNITS MONTH - 1 UNITS DAY)  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN (CAST((DATE((DATE(CURRENT) - (CASE WHEN(" + dayofWeekOfCurrDate + "  >= " + weekStartDay + ") THEN (" + dayofWeekOfCurrDate + " -" + weekStartDay + ") ELSE (6 + (" + dayofWeekOfCurrDate + " -" + weekStartDay + ")) END ) UNITS DAY)) - DATE(" + dateString + "))AS BIGINT)  BETWEEN 1  AND  7) THEN 1 ELSE 0 END ";
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
      if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
         qry = "(CASE WHEN DATE(" + dateString + ") BETWEEN datetime(datetime(date(datetime(" + arguments.get(2).toString() + ",'-1 months'),'start of month','+1 month','-1 day'),'+1 days'),'-' || (" + arguments.get(1).toString() + ") || ' months') AND date(datetime(" + arguments.get(2).toString() + ",'-1 month'),'start of month','+1 month','-1 day') THEN 1 ELSE 0 END)";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
         qry = "(CASE WHEN " + dateString + " BETWEEN datetime(datetime(datetime(" + arguments.get(2).toString() + ",((MOD( strftime('%m'," + arguments.get(2).toString() + ") -1, 3 )) * -1) || ' months'),strftime('%d'," + arguments.get(2).toString() + ")* -1+1), -3*(" + arguments.get(1).toString() + ") || ' months') AND (datetime(datetime(" + arguments.get(2).toString() + ",((MOD( strftime('%m'," + arguments.get(2).toString() + ") -1, 3 )) * -1) || ' months'),strftime('%d'," + arguments.get(2).toString() + ")* -1+1)-1) THEN 1 ELSE 0 END)";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "(CASE WHEN (CAST(-(julianday(DATE(" + dateString + ") - julianday(DATE(DATETIME(DATE(),'-' || (CASE WHEN(" + dayofWeekOfCurrDate + "  < " + weekStartDay + ") THEN (" + dayofWeekOfCurrDate + " -" + weekStartDay + ") ELSE (6 + (" + dayofWeekOfCurrDate + " -" + weekStartDay + ")) END) || ' days'))))) AS INTEGER) between 1 and 7) THEN 1 ELSE 0 END)";
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
      if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
         qry = "Iif(CDate(" + arguments.get(0).toString() + ")  BETWEEN DATEADD('m', -(" + arguments.get(1).toString() + "),DATEADD('d', -(DAY(" + arguments.get(2).toString() + ") -1), " + arguments.get(2).toString() + "))  AND  DATEADD('d', -(DAY(" + arguments.get(2).toString() + ")), " + arguments.get(2).toString() + ") , 1, 0 )";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
         qry = "Iif(CDate(" + arguments.get(0).toString() + ")  BETWEEN (DATEADD('q', DATEDIFF('q', 0, " + arguments.get(2).toString() + ") -(" + arguments.get(1).toString() + "), 0)) AND (DATEADD ('d', -1, DATEADD('q', DATEDIFF('q', 0, " + arguments.get(2).toString() + "), 0))), 1 ,0)";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfDateCol = arguments.get(2).toString();
         qry = "Iif( (CLng(DATEDIFF('d',CDate(" + arguments.get(0).toString() + ") , CDate(DATEADD('d' , - (Iif((" + dayofWeekOfDateCol + "  >= " + weekStartDay + ") , (" + dayofWeekOfDateCol + " -" + weekStartDay + ") , (7 + (" + dayofWeekOfDateCol + " -" + weekStartDay + "))) ),now()))))  between 1  and  7) , 1 , 0 )";
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
      if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE)  BETWEEN  (TRUNC(" + arguments.get(2).toString() + ", 'MM') - (" + arguments.get(1).toString() + ") MONTH) AND LAST_DAY((" + arguments.get(2).toString() + " - 1 MONTH))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE)  BETWEEN  (TRUNC(" + arguments.get(2).toString() + ", 'YY') + ((QUARTER(" + arguments.get(2).toString() + ")-1)*3) MONTH -  (3*(" + arguments.get(1).toString() + ")) MONTH) AND (TRUNC(" + arguments.get(2).toString() + ", 'YY') + ((QUARTER(" + arguments.get(2).toString() + ")-1)*3) MONTH - 1 DAY)  THEN 1 ELSE 0 END";
      } else {
         if (!fnStr.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
            this.setFunctionArguments(arguments);
            return;
         }

         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfDateCol = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(DATEDIFF('d', CAST((CURRENT_DATE - (CASE WHEN(" + dayofWeekOfDateCol + "  >= " + weekStartDay + ") THEN (" + dayofWeekOfDateCol + " -" + weekStartDay + ") ELSE (7 + (" + dayofWeekOfDateCol + " -" + weekStartDay + ")) END ) DAY) AS DATE),CAST(" + dateString + " AS DATE))AS BIGINT)  BETWEEN 1  AND  7) THEN 1 ELSE 0 END";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }
}
