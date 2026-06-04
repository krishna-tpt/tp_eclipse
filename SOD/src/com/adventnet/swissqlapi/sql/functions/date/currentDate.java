package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class currentDate extends FunctionCalls {
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

      String t_num;
      String weekMode;
      String weekStDay;
      if (from_sqs != null && from_sqs.isHyperSql()) {
         if (fnStr.equalsIgnoreCase("ZR_ISCURRENTWEEK")) {
            weekStDay = arguments.get(1).toString();
            weekMode = arguments.get(2).toString();
            t_num = "(CASE WHEN DATEDIFF('DAY',CAST(DATEADD('DAY',- ((CASE WHEN " + weekMode + "  >= " + weekStDay + " THEN (" + weekMode + "-" + weekStDay + ") ELSE (7 + (" + weekMode + "-" + weekStDay + ")) END)),CURRENT_DATE) AS DATE) , CAST(" + StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString()) + " AS DATE))  BETWEEN 0  AND  6 THEN 1 ELSE 0 END)";
            this.functionName.setColumnName(t_num);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         }
      } else if (from_sqs != null && from_sqs.isMySqlLive()) {
         if (fnStr.equalsIgnoreCase("ZR_ISCURRENTWEEK")) {
            weekStDay = "if(datediff(" + arguments.get(0).toString() + ",date_sub(current_date(), interval if(" + arguments.get(2).toString() + " >= " + arguments.get(1).toString() + ", (" + arguments.get(2).toString() + "-" + arguments.get(1).toString() + "), (7+(" + arguments.get(2).toString() + "-" + arguments.get(1).toString() + "))) day)) between 0 and 6,1,0)";
            this.functionName.setColumnName(weekStDay);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         }
      } else {
         String f_num;
         Vector vc_DayOfWeekIn;
         WhereColumn if_left;
         Vector vc_if_left;
         WhereColumn if_right;
         Vector vc_if_right;
         SelectColumn sc_yearWeekleft;
         FunctionCalls fn_yearWeekleft;
         TableColumn tb_yearWeekleft;
         Vector vc_yearWeekleftIn;
         Vector vc_yearWeekleftOut;
         SelectColumn sc_yearWeekright;
         FunctionCalls fn_yearWeekright;
         TableColumn tb_yearWeekright;
         Vector vc_yearWeekrightIn;
         Vector vc_yearWeekrightOut;
         if (!fnStr.equalsIgnoreCase("ISCURRENTYEAR") && !fnStr.equalsIgnoreCase("IS_CURRENT_YEAR")) {
            Vector vc_if;
            Vector vc_DayOfWeekOut;
            if (!fnStr.equalsIgnoreCase("ISCURRENTQUARTER") && !fnStr.equalsIgnoreCase("IS_CURRENT_QUARTER")) {
               SelectColumn sc_wi;
               Vector vc_wi;
               WhereItem wi_if;
               if (!fnStr.equalsIgnoreCase("ISCURRENTMONTH") && !fnStr.equalsIgnoreCase("IS_CURRENT_MONTH")) {
                  if (!fnStr.equalsIgnoreCase("ISCURRENTWEEK") && !fnStr.equalsIgnoreCase("IS_CURRENT_WEEK")) {
                     if (fnStr.equalsIgnoreCase("TODAY")) {
                        this.functionName.setColumnName("CURRENT_DATE");
                        this.setFunctionArguments(arguments);
                     }
                  } else {
                     weekStDay = "";
                     weekMode = null;
                     SelectColumn sc_DayOfWeek;
                     if (arguments.size() < 2) {
                        weekMode = "3";
                        arguments.add(weekStDay);
                     } else if (arguments.elementAt(1) instanceof SelectColumn) {
                        sc_DayOfWeek = (SelectColumn)arguments.elementAt(1);
                        Vector vc = sc_DayOfWeek.getColumnExpression();
                        if (!(vc.elementAt(0) instanceof String)) {
                           throw new ConvertException("Invalid Argument Value for Function ISCURRENTWEEK", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"ISCURRENTWEEK", "WEEK_STARTDAY"});
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
                     }

                     if (weekMode != null) {
                        this.functionName.setColumnName("IF");
                        Vector vc_if = new Vector();
                        sc_wi = new SelectColumn();
                        vc_wi = new Vector();
                        wi_if = new WhereItem();
                        if_left = new WhereColumn();
                        vc_if_left = new Vector();
                        if_right = new WhereColumn();
                        vc_if_right = new Vector();
                        sc_yearWeekleft = new SelectColumn();
                        fn_yearWeekleft = new FunctionCalls();
                        tb_yearWeekleft = new TableColumn();
                        tb_yearWeekleft.setColumnName("YEARWEEK");
                        fn_yearWeekleft.setFunctionName(tb_yearWeekleft);
                        vc_yearWeekleftIn = new Vector();
                        vc_yearWeekleftOut = new Vector();
                        vc_yearWeekleftIn.addElement(arguments.get(0));
                        vc_yearWeekleftIn.addElement(weekMode);
                        fn_yearWeekleft.setFunctionArguments(vc_yearWeekleftIn);
                        vc_yearWeekleftOut.addElement(fn_yearWeekleft);
                        sc_yearWeekleft.setColumnExpression(vc_yearWeekleftOut);
                        vc_if_left.addElement(sc_yearWeekleft);
                        if_left.setColumnExpression(vc_if_left);
                        wi_if.setLeftWhereExp(if_left);
                        wi_if.setOperator("=");
                        sc_yearWeekright = new SelectColumn();
                        fn_yearWeekright = new FunctionCalls();
                        tb_yearWeekright = new TableColumn();
                        tb_yearWeekright.setColumnName("YEARWEEK");
                        fn_yearWeekright.setFunctionName(tb_yearWeekright);
                        vc_yearWeekrightIn = new Vector();
                        vc_yearWeekrightOut = new Vector();
                        vc_yearWeekrightIn.addElement(this.current_date());
                        vc_yearWeekrightIn.addElement(weekMode);
                        fn_yearWeekright.setFunctionArguments(vc_yearWeekrightIn);
                        vc_yearWeekrightOut.addElement(fn_yearWeekright);
                        sc_yearWeekright.setColumnExpression(vc_yearWeekrightOut);
                        vc_if_right.addElement(sc_yearWeekright);
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
                        this.functionName.setColumnName("ZR_ISCURRENTWEEK");
                        arguments.set(1, Integer.toString(Integer.parseInt(weekStDay) + 1));
                        sc_DayOfWeek = new SelectColumn();
                        FunctionCalls fn_DayOfWeek = new FunctionCalls();
                        TableColumn tb_DayOfWeek = new TableColumn();
                        tb_DayOfWeek.setColumnName("DAYOFWEEK");
                        fn_DayOfWeek.setFunctionName(tb_DayOfWeek);
                        vc_DayOfWeekIn = new Vector();
                        vc_DayOfWeekOut = new Vector();
                        vc_DayOfWeekIn.addElement(this.current_date());
                        fn_DayOfWeek.setFunctionArguments(vc_DayOfWeekIn);
                        vc_DayOfWeekOut.addElement(fn_DayOfWeek);
                        sc_DayOfWeek.setColumnExpression(vc_DayOfWeekOut);
                        arguments.addElement(sc_DayOfWeek);
                        this.setFunctionArguments(arguments);
                     }
                  }
               } else {
                  this.functionName.setColumnName("IF");
                  vc_if = new Vector();
                  weekMode = "1";
                  t_num = "0";
                  sc_wi = new SelectColumn();
                  vc_wi = new Vector();
                  wi_if = new WhereItem();
                  if_left = new WhereColumn();
                  vc_if_left = new Vector();
                  if_right = new WhereColumn();
                  vc_if_right = new Vector();
                  sc_yearWeekleft = new SelectColumn();
                  fn_yearWeekleft = new FunctionCalls();
                  tb_yearWeekleft = new TableColumn();
                  tb_yearWeekleft.setColumnName("DATE_FORMAT");
                  fn_yearWeekleft.setFunctionName(tb_yearWeekleft);
                  vc_yearWeekleftIn = new Vector();
                  vc_yearWeekleftOut = new Vector();
                  vc_yearWeekleftIn.addElement(this.current_date());
                  vc_yearWeekleftIn.add("'%Y %M'");
                  fn_yearWeekleft.setFunctionArguments(vc_yearWeekleftIn);
                  vc_yearWeekleftOut.addElement(fn_yearWeekleft);
                  sc_yearWeekleft.setColumnExpression(vc_yearWeekleftOut);
                  vc_if_left.addElement(sc_yearWeekleft);
                  if_left.setColumnExpression(vc_if_left);
                  wi_if.setLeftWhereExp(if_left);
                  wi_if.setOperator("=");
                  sc_yearWeekright = new SelectColumn();
                  fn_yearWeekright = new FunctionCalls();
                  tb_yearWeekright = new TableColumn();
                  tb_yearWeekright.setColumnName("DATE_FORMAT");
                  fn_yearWeekright.setFunctionName(tb_yearWeekright);
                  vc_yearWeekrightIn = new Vector();
                  vc_yearWeekrightOut = new Vector();
                  vc_yearWeekrightIn.addElement(arguments.get(0));
                  vc_yearWeekrightIn.add("'%Y %M'");
                  fn_yearWeekright.setFunctionArguments(vc_yearWeekrightIn);
                  vc_yearWeekrightOut.addElement(fn_yearWeekright);
                  sc_yearWeekright.setColumnExpression(vc_yearWeekrightOut);
                  vc_if_right.addElement(sc_yearWeekright);
                  if_right.setColumnExpression(vc_if_right);
                  wi_if.setRightWhereExp(if_right);
                  vc_wi.addElement(wi_if);
                  sc_wi.setColumnExpression(vc_wi);
                  vc_if.addElement(sc_wi);
                  vc_if.addElement(weekMode);
                  vc_if.addElement(t_num);
                  this.setFunctionArguments(vc_if);
               }
            } else {
               this.functionName.setColumnName("IF");
               vc_if = new Vector();
               SelectColumn sc_wi = new SelectColumn();
               t_num = "1";
               f_num = "0";
               WhereExpression whExp = new WhereExpression();
               vc_DayOfWeekIn = new Vector();
               vc_DayOfWeekOut = new Vector();
               WhereItem wi_ifyear = new WhereItem();
               new Vector();
               WhereColumn if_leftyear = new WhereColumn();
               Vector vc_if_leftyear = new Vector();
               WhereColumn if_rightyear = new WhereColumn();
               Vector vc_if_rightyear = new Vector();
               SelectColumn sc_year_left = new SelectColumn();
               FunctionCalls fn_year_left = new FunctionCalls();
               TableColumn tb_year_left = new TableColumn();
               tb_year_left.setColumnName("YEAR");
               fn_year_left.setFunctionName(tb_year_left);
               Vector vc_yearIn_left = new Vector();
               Vector vc_yearOut_left = new Vector();
               vc_yearIn_left.addElement(this.current_date());
               fn_year_left.setFunctionArguments(vc_yearIn_left);
               vc_yearOut_left.addElement(fn_year_left);
               sc_year_left.setColumnExpression(vc_yearOut_left);
               vc_if_leftyear.addElement(sc_year_left);
               if_leftyear.setColumnExpression(vc_if_leftyear);
               wi_ifyear.setLeftWhereExp(if_leftyear);
               wi_ifyear.setOperator("=");
               SelectColumn sc_year_right = new SelectColumn();
               FunctionCalls fn_year_right = new FunctionCalls();
               TableColumn tb_year_right = new TableColumn();
               tb_year_right.setColumnName("YEAR");
               fn_year_right.setFunctionName(tb_year_right);
               Vector vc_yearIn_right = new Vector();
               Vector vc_yearOut_right = new Vector();
               vc_yearIn_right.addElement(arguments.get(0));
               fn_year_right.setFunctionArguments(vc_yearIn_right);
               vc_yearOut_right.addElement(fn_year_right);
               sc_year_right.setColumnExpression(vc_yearOut_right);
               vc_if_rightyear.addElement(sc_year_right);
               if_rightyear.setColumnExpression(vc_if_rightyear);
               wi_ifyear.setRightWhereExp(if_rightyear);
               vc_DayOfWeekOut.addElement(wi_ifyear);
               WhereItem wi_ifquarter = new WhereItem();
               new Vector();
               WhereColumn if_leftquarter = new WhereColumn();
               Vector vc_if_leftquarter = new Vector();
               WhereColumn if_rightquarter = new WhereColumn();
               Vector vc_if_rightquarter = new Vector();
               SelectColumn sc_quarter_left = new SelectColumn();
               FunctionCalls fn_quarter_left = new FunctionCalls();
               TableColumn tb_quarter_left = new TableColumn();
               tb_quarter_left.setColumnName("QUARTER");
               fn_quarter_left.setFunctionName(tb_quarter_left);
               Vector vc_quarterIn_left = new Vector();
               Vector vc_quarterOut_left = new Vector();
               vc_quarterIn_left.addElement(this.current_date());
               fn_quarter_left.setFunctionArguments(vc_quarterIn_left);
               vc_quarterOut_left.addElement(fn_quarter_left);
               sc_quarter_left.setColumnExpression(vc_quarterOut_left);
               vc_if_leftquarter.addElement(sc_quarter_left);
               if_leftquarter.setColumnExpression(vc_if_leftquarter);
               wi_ifquarter.setLeftWhereExp(if_leftquarter);
               wi_ifquarter.setOperator("=");
               SelectColumn sc_quarter_right = new SelectColumn();
               FunctionCalls fn_quarter_right = new FunctionCalls();
               TableColumn tb_quarter_right = new TableColumn();
               tb_quarter_right.setColumnName("QUARTER");
               fn_quarter_right.setFunctionName(tb_quarter_right);
               Vector vc_quarterIn_right = new Vector();
               Vector vc_quarterOut_right = new Vector();
               if (arguments.elementAt(0) instanceof SelectColumn) {
                  SelectColumn sc = ((SelectColumn)this.functionArguments.elementAt(0)).toMySQLSelect(to_sqs, from_sqs);
                  vc_quarterIn_right.addElement(sc);
               } else {
                  vc_quarterIn_right.addElement(this.functionArguments.elementAt(0));
               }

               fn_quarter_right.setFunctionArguments(vc_quarterIn_right);
               vc_quarterOut_right.addElement(fn_quarter_right);
               sc_quarter_right.setColumnExpression(vc_quarterOut_right);
               vc_if_rightquarter.addElement(sc_quarter_right);
               if_rightquarter.setColumnExpression(vc_if_rightquarter);
               wi_ifquarter.setRightWhereExp(if_rightquarter);
               vc_DayOfWeekOut.addElement(wi_ifquarter);
               whExp.setWhereItem(vc_DayOfWeekOut);
               Vector vc_Operator = new Vector();
               vc_Operator.addElement("and");
               whExp.setOperator(vc_Operator);
               vc_DayOfWeekIn.addElement(whExp);
               sc_wi.setColumnExpression(vc_DayOfWeekIn);
               vc_if.addElement(sc_wi);
               vc_if.addElement(t_num);
               vc_if.addElement(f_num);
               this.setFunctionArguments(vc_if);
            }
         } else {
            this.functionName.setColumnName("IF");
            SelectColumn sc_wi = new SelectColumn();
            Vector vc_wi = new Vector();
            t_num = "1";
            f_num = "0";
            WhereItem wi_if = new WhereItem();
            vc_DayOfWeekIn = new Vector();
            if_left = new WhereColumn();
            vc_if_left = new Vector();
            if_right = new WhereColumn();
            vc_if_right = new Vector();
            sc_yearWeekleft = new SelectColumn();
            fn_yearWeekleft = new FunctionCalls();
            tb_yearWeekleft = new TableColumn();
            tb_yearWeekleft.setColumnName("YEAR");
            fn_yearWeekleft.setFunctionName(tb_yearWeekleft);
            vc_yearWeekleftIn = new Vector();
            vc_yearWeekleftOut = new Vector();
            vc_yearWeekleftIn.addElement(this.current_date());
            fn_yearWeekleft.setFunctionArguments(vc_yearWeekleftIn);
            vc_yearWeekleftOut.addElement(fn_yearWeekleft);
            sc_yearWeekleft.setColumnExpression(vc_yearWeekleftOut);
            vc_if_left.addElement(sc_yearWeekleft);
            if_left.setColumnExpression(vc_if_left);
            wi_if.setLeftWhereExp(if_left);
            wi_if.setOperator("=");
            sc_yearWeekright = new SelectColumn();
            fn_yearWeekright = new FunctionCalls();
            tb_yearWeekright = new TableColumn();
            tb_yearWeekright.setColumnName("YEAR");
            fn_yearWeekright.setFunctionName(tb_yearWeekright);
            vc_yearWeekrightIn = new Vector();
            vc_yearWeekrightOut = new Vector();
            vc_yearWeekrightIn.addElement(arguments.get(0));
            fn_yearWeekright.setFunctionArguments(vc_yearWeekrightIn);
            vc_yearWeekrightOut.addElement(fn_yearWeekright);
            sc_yearWeekright.setColumnExpression(vc_yearWeekrightOut);
            vc_if_right.addElement(sc_yearWeekright);
            if_right.setColumnExpression(vc_if_right);
            wi_if.setRightWhereExp(if_right);
            vc_wi.addElement(wi_if);
            sc_wi.setColumnExpression(vc_wi);
            vc_DayOfWeekIn.addElement(sc_wi);
            vc_DayOfWeekIn.addElement(t_num);
            vc_DayOfWeekIn.addElement(f_num);
            this.setFunctionArguments(vc_DayOfWeekIn);
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
         if (fnStr.equalsIgnoreCase("ZR_ISCURRENTWEEK")) {
            String weekStartDay = arguments.get(1).toString();
            String dayofWeekOfCurrDate = arguments.get(2).toString();
            qry = "CASE WHEN DATE_MI(DATE(" + arguments.get(0).toString() + "), DATE((CURRENT_DATE) - ( INTERVAL  '1'  day * ROUND((CASE WHEN " + dayofWeekOfCurrDate + "  >= " + weekStartDay + " THEN (" + dayofWeekOfCurrDate + " - " + weekStartDay + ") ELSE (7 + (" + dayofWeekOfCurrDate + " - " + weekStartDay + ")) END))) ))  between 0  and  6 THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISCURRENTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "if(TIMESTAMPDIFF(DAY,CAST((timestamp(CURRENT_DATE) - interval  '1'  day * (if(" + dayofWeekOfCurrDate + "  >= " + weekStartDay + ", (" + dayofWeekOfCurrDate + "-" + weekStartDay + "), (7 + (" + dayofWeekOfCurrDate + "-" + weekStartDay + "))))) AS DATE) , CAST(" + arguments.get(0).toString() + " AS DATE))  between 0  and  6, 1, 0)";
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
      if (fnStr.equalsIgnoreCase("ZR_ISCURRENTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(DATEDIFF(DAY,CAST(" + arguments.get(0).toString() + " AS DATE),CAST(DATEADD(DAY, (CASE WHEN(" + dayofWeekOfCurrDate + "  >= " + weekStartDay + ") THEN (" + dayofWeekOfCurrDate + " -" + weekStartDay + ") ELSE (7 + (" + dayofWeekOfCurrDate + " -" + weekStartDay + ")) END),CONVERT(datetime,GETDATE(),102)) AS DATE) ) AS BIGINT)  between 0  and  6) THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISCURRENTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "if(TIMESTAMP_DIFF(CAST(TIMESTAMP_SUB(CURRENT_TIMESTAMP , interval  (if(" + dayofWeekOfCurrDate + "  >= " + weekStartDay + ", (" + dayofWeekOfCurrDate + "-" + weekStartDay + "), (7 + (" + dayofWeekOfCurrDate + "-" + weekStartDay + "))))  day ) AS TIMESTAMP) , CAST(" + arguments.get(0).toString() + " AS TIMESTAMP),DAY)  between 0  and  6, 1, 0)";
         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      }

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
      if (fnStr.equalsIgnoreCase("ZR_ISCURRENTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "IFF(TIMESTAMPDIFF(DAY,CAST((CURRENT_DATE - (IFF(" + dayofWeekOfCurrDate + "  >= " + weekStartDay + ", (" + dayofWeekOfCurrDate + "-" + weekStartDay + "), (7 + (" + dayofWeekOfCurrDate + "-" + weekStartDay + "))))) AS DATE) , CAST(" + arguments.get(0).toString() + " AS DATE))  between 0  and  6, 1, 0)";
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
      if (fnStr.equalsIgnoreCase("ZR_ISCURRENTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "IF(DATE_DIFF('DAY',CAST(DATE_ADD('DAY',- (IF(" + dayofWeekOfCurrDate + "  >= " + weekStartDay + ", (" + dayofWeekOfCurrDate + "-" + weekStartDay + "), (7 + (" + dayofWeekOfCurrDate + "-" + weekStartDay + ")))),CURRENT_DATE) AS DATE) , CAST(" + StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString()) + " AS DATE))  between 0  and  6, 1, 0)";
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
      if (fnStr.equalsIgnoreCase("ZR_ISCURRENTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN DAYS_BETWEEN(CAST(ADD_DAYS(CURRENT_DATE,- (CASE WHEN " + dayofWeekOfCurrDate + "  >= " + weekStartDay + " THEN (" + dayofWeekOfCurrDate + "-" + weekStartDay + ") ELSE (7 + (" + dayofWeekOfCurrDate + "-" + weekStartDay + ")) END )) AS DATE),CAST(" + StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString()) + " AS DATE))  between 0  and  6 THEN 1 ELSE 0 END";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (fnStr.equalsIgnoreCase("ZR_ISCURRENTWEEK")) {
         String datestring = StringFunctions.handleLiteralStringDateForOracle(arguments.get(0).toString());
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN (CAST(" + datestring + " AS DATE)-(TO_DATE(SYSDATE)-(INTERVAL '1' DAY) * (CASE WHEN " + dayofWeekOfCurrDate + " >= " + weekStartDay + " THEN " + dayofWeekOfCurrDate + "-" + weekStartDay + " ELSE (7+" + dayofWeekOfCurrDate + "-" + weekStartDay + ") END))) BETWEEN 0 AND 6 THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISCURRENTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN(TIMESTAMPDIFF(16,CHAR(CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) - CAST((CURRENT_TIMESTAMP - (CASE WHEN(" + dayofWeekOfCurrDate + "  >= " + weekStartDay + ") THEN (" + dayofWeekOfCurrDate + "-" + weekStartDay + ") ELSE (7 + (" + dayofWeekOfCurrDate + "-" + weekStartDay + "))END ) DAYS) AS TIMESTAMP) ))  between 0  and  6) THEN 1 ELSE 0 END";
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionName.setColumnName(qry);
      }

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
      if (fnStr.equalsIgnoreCase("ZR_ISCURRENTWEEK")) {
         String weekStartDay = arguments.get(1).toString();
         String dayofWeekOfCurrDate = arguments.get(2).toString();
         qry = "CASE WHEN (DATE(" + StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString()) + ") - DATE((DATE(CURRENT) - (CASE WHEN " + dayofWeekOfCurrDate + "  >= " + weekStartDay + " THEN (" + dayofWeekOfCurrDate + "-" + weekStartDay + ") ELSE (7 + (" + dayofWeekOfCurrDate + "-" + weekStartDay + ")) END ) UNITS DAY)))  BETWEEN 0  AND  6 THEN 1 ELSE 0 END ";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }
}
