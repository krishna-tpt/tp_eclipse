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

public class lastDate extends FunctionCalls {
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

      String qry;
      if (from_sqs != null && from_sqs.isHyperSql()) {
         String dateString = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
         if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
            qry = "CASE WHEN CAST(" + dateString + " AS DATE)  BETWEEN (TRUNC(" + arguments.get(2).toString() + ", 'MM') - (" + arguments.get(1).toString() + "-1) MONTH) AND LAST_DAY(" + arguments.get(2).toString() + ")  THEN 1 ELSE 0 END";
         } else {
            if (!fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
               this.setFunctionArguments(arguments);
               return;
            }

            qry = "CASE WHEN CAST(" + dateString + " AS DATE)  BETWEEN (TRUNC(" + arguments.get(2).toString() + ",'YY')+((QUARTER(" + arguments.get(2).toString() + ")-1)*3) MONTH - (3*(" + arguments.get(1).toString() + "-1)) MONTH) AND LAST_DAY((TRUNC(" + arguments.get(2).toString() + ",'YY')+((QUARTER(" + arguments.get(2).toString() + ")-1)*3) MONTH + 2 MONTH))  THEN 1 ELSE 0 END";
         }

         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else if (from_sqs != null && from_sqs.isMySqlLive()) {
         if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
            qry = "IF(" + arguments.get(0).toString() + " BETWEEN date_sub(date_sub(" + arguments.get(2).toString() + ",interval ((" + arguments.get(1).toString() + ")-1) month),interval (day(" + arguments.get(2).toString() + ")-1) day) AND last_day(" + arguments.get(2).toString() + ") ,1,0)";
            this.functionName.setColumnName(qry);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         } else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
            qry = "IF(" + arguments.get(0).toString() + " BETWEEN  date_sub(STR_TO_DATE(CONCAT_WS('-',YEAR(" + arguments.get(2).toString() + "),((QUARTER(" + arguments.get(2).toString() + ")*3)-2),1),'%Y-%m-%d'),interval ((" + arguments.get(1).toString() + ")-1) quarter) AND last_day(date_add(" + arguments.get(2).toString() + ",interval ((quarter(" + arguments.get(2).toString() + ")*3)-(month(" + arguments.get(2).toString() + "))) month)),1,0)";
            this.functionName.setColumnName(qry);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         }
      } else {
         SelectColumn sc_interval;
         Vector colExp;
         WhereItem wi_if;
         WhereColumn if_left;
         Vector vc_if_left;
         WhereColumn if_right;
         Vector vc_if_right;
         SelectColumn sc_Date;
         FunctionCalls fn_Date;
         TableColumn tb_Date;
         Vector vc_DateIn;
         Vector vc_DateOut;
         SelectColumn sc_Limit;
         Vector vc_Limit;
         SelectColumn sc_dateSubLow;
         Vector vc_intrLow;
         Vector vc_lastmonth;
         if (!fnStr.equalsIgnoreCase("ISLAST_NYEAR") && !fnStr.equalsIgnoreCase("IS_LAST_NYEAR")) {
            if (!fnStr.equalsIgnoreCase("ISLAST_NQUARTER") && !fnStr.equalsIgnoreCase("IS_LAST_NQUARTER")) {
               if (!fnStr.equalsIgnoreCase("ISLAST_NMONTH") && !fnStr.equalsIgnoreCase("IS_LAST_NMONTH")) {
                  if (!fnStr.equalsIgnoreCase("ISLAST_NDAY") && !fnStr.equalsIgnoreCase("IS_LAST_NDAY")) {
                     TableColumn tbCl_interval;
                     if (fnStr.equalsIgnoreCase("LAST_NDAY")) {
                        this.functionName.setColumnName("DATE_SUB");
                        vc_lastmonth = new Vector();
                        vc_lastmonth.addElement(arguments.get(0));
                        sc_interval = new SelectColumn();
                        colExp = new Vector();
                        tbCl_interval = new TableColumn();
                        tbCl_interval.setColumnName("INTERVAL");
                        colExp.add(tbCl_interval);
                        colExp.add("(");
                        colExp.add("(");
                        colExp.addElement(arguments.get(1));
                        colExp.add(")");
                        colExp.addElement("-");
                        colExp.addElement("1");
                        colExp.add(")");
                        colExp.add(" DAY");
                        sc_interval.setColumnExpression(colExp);
                        vc_lastmonth.addElement(sc_interval);
                        this.setFunctionArguments(vc_lastmonth);
                     } else if (fnStr.equalsIgnoreCase("LAST_NMONTH")) {
                        this.functionName.setColumnName("DATE_SUB");
                        vc_lastmonth = new Vector();
                        vc_lastmonth.addElement(arguments.get(0));
                        sc_interval = new SelectColumn();
                        colExp = new Vector();
                        tbCl_interval = new TableColumn();
                        tbCl_interval.setColumnName("INTERVAL");
                        colExp.add(tbCl_interval);
                        colExp.add("(");
                        colExp.add("(");
                        colExp.addElement(arguments.get(1));
                        colExp.add(")");
                        colExp.addElement("-");
                        colExp.addElement("1");
                        colExp.add(")");
                        colExp.add(" MONTH");
                        sc_interval.setColumnExpression(colExp);
                        vc_lastmonth.addElement(sc_interval);
                        this.setFunctionArguments(vc_lastmonth);
                     }
                  } else {
                     this.functionName.setColumnName("IF");
                     vc_lastmonth = new Vector();
                     sc_interval = new SelectColumn();
                     colExp = new Vector();
                     wi_if = new WhereItem();
                     if_left = new WhereColumn();
                     vc_if_left = new Vector();
                     if_right = new WhereColumn();
                     vc_if_right = new Vector();
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
                     vc_if_left.addElement(sc_Date);
                     if_left.setColumnExpression(vc_if_left);
                     wi_if.setLeftWhereExp(if_left);
                     wi_if.setOperator("BETWEEN");
                     sc_Limit = new SelectColumn();
                     vc_Limit = new Vector();
                     sc_dateSubLow = new SelectColumn();
                     FunctionCalls fn_dateSubLow = new FunctionCalls();
                     TableColumn tb_dateSubLow = new TableColumn();
                     tb_dateSubLow.setColumnName("DATE_SUB");
                     fn_dateSubLow.setFunctionName(tb_dateSubLow);
                     Vector vc_dateSubLowIn = new Vector();
                     Vector vc_dateSubLowOut = new Vector();
                     vc_dateSubLowIn.addElement(this.current_date());
                     SelectColumn sc_intrLow = new SelectColumn();
                     vc_intrLow = new Vector();
                     TableColumn tbCl_interval = new TableColumn();
                     tbCl_interval.setColumnName("INTERVAL");
                     vc_intrLow.addElement(tbCl_interval);
                     vc_intrLow.add("(");
                     vc_intrLow.add("(");
                     vc_intrLow.addElement(arguments.get(1));
                     vc_intrLow.add(")");
                     vc_intrLow.add("-");
                     vc_intrLow.add("1");
                     vc_intrLow.add(")");
                     vc_intrLow.addElement(" day");
                     sc_intrLow.setColumnExpression(vc_intrLow);
                     vc_dateSubLowIn.addElement(sc_intrLow);
                     fn_dateSubLow.setFunctionArguments(vc_dateSubLowIn);
                     vc_dateSubLowOut.addElement(fn_dateSubLow);
                     sc_dateSubLow.setColumnExpression(vc_dateSubLowOut);
                     vc_Limit.addElement(sc_dateSubLow);
                     vc_Limit.addElement("AND");
                     vc_Limit.addElement(this.current_date());
                     sc_Limit.setColumnExpression(vc_Limit);
                     vc_if_right.addElement(sc_Limit);
                     if_right.setColumnExpression(vc_if_right);
                     wi_if.setRightWhereExp(if_right);
                     colExp.addElement(wi_if);
                     sc_interval.setColumnExpression(colExp);
                     vc_lastmonth.addElement(sc_interval);
                     vc_lastmonth.addElement("1");
                     vc_lastmonth.addElement("0");
                     this.setFunctionArguments(vc_lastmonth);
                  }
               } else {
                  this.functionName.setColumnName("ZR_ISLASTMONTH");
                  arguments.addElement(this.current_date());
                  this.setFunctionArguments(arguments);
               }
            } else {
               this.functionName.setColumnName("ZR_ISLASTQUARTER");
               arguments.addElement(this.current_date());
               this.setFunctionArguments(arguments);
            }
         } else {
            this.functionName.setColumnName("IF");
            vc_lastmonth = new Vector();
            sc_interval = new SelectColumn();
            colExp = new Vector();
            wi_if = new WhereItem();
            if_left = new WhereColumn();
            vc_if_left = new Vector();
            if_right = new WhereColumn();
            vc_if_right = new Vector();
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
            vc_if_left.addElement(sc_Date);
            if_left.setColumnExpression(vc_if_left);
            wi_if.setLeftWhereExp(if_left);
            wi_if.setOperator("BETWEEN");
            sc_Limit = new SelectColumn();
            vc_Limit = new Vector();
            sc_dateSubLow = new SelectColumn();
            Vector vc_LimitLow = new Vector();
            SelectColumn sc_StartDate = new SelectColumn();
            FunctionCalls fnCl_StartDate = new FunctionCalls();
            TableColumn tbCl_StartDate = new TableColumn();
            tbCl_StartDate.setColumnName("YEAR");
            fnCl_StartDate.setFunctionName(tbCl_StartDate);
            Vector vc_StartDateIn = new Vector();
            vc_intrLow = new Vector();
            vc_StartDateIn.addElement(this.current_date());
            fnCl_StartDate.setFunctionArguments(vc_StartDateIn);
            vc_intrLow.addElement(fnCl_StartDate);
            sc_StartDate.setColumnExpression(vc_intrLow);
            vc_LimitLow.addElement(sc_StartDate);
            vc_LimitLow.addElement("-");
            vc_LimitLow.addElement(arguments.get(1));
            sc_dateSubLow.setOpenBrace("(");
            sc_dateSubLow.setCloseBrace(")");
            sc_dateSubLow.setColumnExpression(vc_LimitLow);
            vc_Limit.addElement(sc_dateSubLow);
            vc_Limit.addElement("AND");
            SelectColumn sc_LimitUp = new SelectColumn();
            Vector vc_LimitUp = new Vector();
            SelectColumn sc_EndDate = new SelectColumn();
            FunctionCalls fnCl_EndDate = new FunctionCalls();
            TableColumn tbCl_EndDate = new TableColumn();
            tbCl_EndDate.setColumnName("YEAR");
            fnCl_EndDate.setFunctionName(tbCl_StartDate);
            Vector vc_EndDateIn = new Vector();
            Vector vc_EndDateOut = new Vector();
            vc_EndDateIn.addElement(this.current_date());
            fnCl_EndDate.setFunctionArguments(vc_EndDateIn);
            vc_EndDateOut.addElement(fnCl_EndDate);
            sc_EndDate.setColumnExpression(vc_EndDateOut);
            vc_LimitUp.addElement(sc_EndDate);
            sc_LimitUp.setOpenBrace("(");
            sc_LimitUp.setCloseBrace(")");
            sc_LimitUp.setColumnExpression(vc_LimitUp);
            vc_Limit.addElement(sc_LimitUp);
            sc_Limit.setColumnExpression(vc_Limit);
            vc_if_right.addElement(sc_Limit);
            if_right.setColumnExpression(vc_if_right);
            wi_if.setRightWhereExp(if_right);
            colExp.addElement(wi_if);
            sc_interval.setColumnExpression(colExp);
            vc_lastmonth.addElement(sc_interval);
            vc_lastmonth.addElement("1");
            vc_lastmonth.addElement("0");
            this.setFunctionArguments(vc_lastmonth);
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

      this.setFunctionArguments(arguments);
      String qry = "";
      if (isPostgreLiveDbs) {
         if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
            qry = "case when CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('month',current_date)-(interval '1' month * ((" + arguments.get(1).toString() + ")-1))) and (date_trunc('MONTH', current_date) + INTERVAL '1' MONTH - interval '1' day) then 1 else 0 end";
         } else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
            qry = "case when CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('quarter',current_date)-(interval '1' month *3 * ((" + arguments.get(1).toString() + ")-1))) and (date_trunc('quarter',current_date) + interval '1' month * 3-interval '1' day) then 1 else 0 end";
         }

         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
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
      if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
         qry = "if(CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('month',current_date-(interval '1' month * ((" + arguments.get(1).toString() + ")-1) ))) and (date_trunc('MONTH', current_date) + INTERVAL '1' MONTH - INTERVAL '1' day),1,0)";
      } else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
         qry = "if(CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('quarter',current_date)-(interval '1' month *3 * ((" + arguments.get(1).toString() + ")-1))) and (date_trunc('quarter',current_date + interval '1' month * 3)-interval '1' day),1,0)";
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
      if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN  DATEADD(M, -((" + arguments.get(1).toString() + ") -1), DATEADD(D, -(DAY(" + arguments.get(2).toString() + ") -1)," + arguments.get(2).toString() + "))  AND  DATEADD(M, 1, DATEADD(D, -DAY(" + arguments.get(2).toString() + ")," + arguments.get(2).toString() + "))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN (DATEADD(qq, DATEDIFF(qq, 0, " + arguments.get(2).toString() + ") -(" + arguments.get(1).toString() + "-1), 0)) AND (DATEADD (dd, -1, DATEADD(qq, DATEDIFF(qq, 0, " + arguments.get(2).toString() + ") +1, 0)))  THEN 1 ELSE 0 END";
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
      String lastDaySyntax = isDenodo ? "LASTDAYOFMONTH" : "LAST_DAY";
      if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE)  BETWEEN  " + addMonthSyntax + "(TRUNC(" + arguments.get(2).toString() + ", 'MM'), -(" + arguments.get(1).toString() + "-1)) AND " + lastDaySyntax + "(" + arguments.get(2).toString() + ")  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
         qry = "CASE WHEN " + dateString + " BETWEEN " + addMonthSyntax + "(TRUNC(" + arguments.get(2).toString() + " , 'Q'),-3*(" + arguments.get(1).toString() + "-1)) AND " + lastDaySyntax + "(" + addMonthSyntax + "(TRUNC(" + arguments.get(2).toString() + " , 'Q'),2)) THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN  DATE_SUB(DATE_TRUNC(" + arguments.get(2).toString() + ", MONTH), INTERVAL (" + arguments.get(1).toString() + "-1) MONTH) AND LAST_DAY(" + arguments.get(2).toString() + ")  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN  DATE_SUB(DATE_TRUNC(" + arguments.get(2).toString() + ", QUARTER), INTERVAL 3*(" + arguments.get(1).toString() + "-1) MONTH) AND LAST_DAY(DATE_ADD(DATE_TRUNC(" + arguments.get(2).toString() + ",QUARTER),INTERVAL 2 MONTH))  THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN DATEADD(MONTH, -(" + arguments.get(1).toString() + "-1), DATE_TRUNC(MONTH, " + arguments.get(2).toString() + ")) AND LAST_DAY(" + arguments.get(2).toString() + ")  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN DATEADD(MONTH, -3*(" + arguments.get(1).toString() + "-1), DATE_TRUNC(QUARTER, " + arguments.get(2).toString() + ")) AND LAST_DAY(DATEADD(MONTH,2,DATE_TRUNC(QUARTER, " + arguments.get(2).toString() + ")))  THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN (" + arguments.get(2).toString() + " - (DAY(" + arguments.get(2).toString() + ")-1) DAYS -(" + arguments.get(1).toString() + "-1) MONTH) AND (LAST_DAY(" + arguments.get(2).toString() + "))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
         qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN (CAST((CAST(YEAR(" + arguments.get(2).toString() + ") AS VARCHAR) || '-' || CAST(((QUARTER(" + arguments.get(2).toString() + ")*3)-2) AS VARCHAR) || '-01') AS DATE) - ((" + arguments.get(1).toString() + "-1)*3) MONTH) AND (LAST_DAY(" + arguments.get(2).toString() + " + ((QUARTER(" + arguments.get(2).toString() + ")*3)-(MONTH(" + arguments.get(2).toString() + "))) MONTH))  THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE) BETWEEN (DATE_ADD('MONTH',-(" + arguments.get(1).toString() + "-1),DATE_TRUNC('MONTH', " + arguments.get(2).toString() + "))) AND (DATE_ADD('DAY',-1,DATE_ADD('MONTH',1,DATE_TRUNC('MONTH', " + arguments.get(2).toString() + "))))  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE) BETWEEN (DATE_ADD('MONTH',-3*(" + arguments.get(1).toString() + "-1),DATE_TRUNC('QUARTER', " + arguments.get(2).toString() + "))) AND (DATE_ADD('DAY',-1,DATE_ADD('MONTH',3,DATE_TRUNC('QUARTER', " + arguments.get(2).toString() + "))))  THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE)  BETWEEN  ADD_MONTHS(ADD_DAYS(LAST_DAY(ADD_MONTHS(" + arguments.get(2).toString() + ",-1)),1), -(" + arguments.get(1).toString() + "-1)) AND LAST_DAY(" + arguments.get(2).toString() + ")  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
         qry = "CASE WHEN " + dateString + " BETWEEN ADD_MONTHS(ADD_DAYS( ADD_MONTHS(" + arguments.get(2).toString() + ",(MOD( MONTH(" + arguments.get(2).toString() + ") -1, 3 )) * -1) ,DAYOFMONTH(" + arguments.get(2).toString() + ")* -1 + 1 ),-3*(" + arguments.get(1).toString() + "-1)) AND LAST_DAY(ADD_MONTHS(ADD_DAYS( ADD_MONTHS(" + arguments.get(2).toString() + ",(MOD( MONTH(" + arguments.get(2).toString() + ") -1, 3 )) * -1) ,DAYOFMONTH(" + arguments.get(2).toString() + ")* -1 + 1 ),2)) THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
         qry = "CASE WHEN DATE(" + dateString + ")  BETWEEN  ADD_MONTHS((LAST_DAY(ADD_MONTHS(" + arguments.get(2).toString() + ",-1))+1 UNITS DAY), -(" + arguments.get(1).toString() + "-1)) AND LAST_DAY(" + arguments.get(2).toString() + ")  THEN 1 ELSE 0 END";
      } else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
         qry = "CASE WHEN DATE(" + dateString + ")  BETWEEN ((YEAR(" + arguments.get(2).toString() + ")::LVARCHAR || '-' || ((QUARTER(" + arguments.get(2).toString() + ")*3)-2)::LVARCHAR || '-01')::DATE - ((" + arguments.get(1).toString() + "-1)*3) UNITS MONTH) AND (LAST_DAY(" + arguments.get(2).toString() + " + ((QUARTER(" + arguments.get(2).toString() + ")*3)-(MONTH(" + arguments.get(2).toString() + "))) UNITS MONTH))  THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
         qry = "(CASE WHEN DATE(" + dateString + ") BETWEEN datetime(datetime(date(datetime(" + arguments.get(2).toString() + ",'-1 months'),'start of month','+1 month','-1 day'),'+1 days'),'-' || (" + arguments.get(1).toString() + "-1) || ' months') AND date(" + arguments.get(2).toString() + ",'start of month','+1 month','-1 day') THEN 1 ELSE 0 END)";
      } else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
         qry = "CASE WHEN " + dateString + " BETWEEN datetime(datetime(datetime(" + arguments.get(2).toString() + ",((MOD( strftime('%m'," + arguments.get(2).toString() + ") -1, 3 )) * -1) || ' months'),strftime('%d'," + arguments.get(2).toString() + ")* -1+1), -3*(" + arguments.get(1).toString() + "-1) || ' months') AND date(datetime(datetime(datetime(" + arguments.get(2).toString() + ",((MOD( strftime('%m'," + arguments.get(2).toString() + ") -1, 3 )) * -1) || ' months'),strftime('%d'," + arguments.get(2).toString() + ")* -1+1),'+2 months'),'start of month','+1 month','-1 day') THEN 1 ELSE 0 END";
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
      if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
         qry = "Iif(CDate(" + arguments.get(0).toString() + ")  BETWEEN  DATEADD('m', -((" + arguments.get(1).toString() + ") -1), DATEADD('d', -(DAY(" + arguments.get(2).toString() + ") -1)," + arguments.get(2).toString() + "))  AND  DATEADD('m', 1, DATEADD('d', -DAY(" + arguments.get(2).toString() + ")," + arguments.get(2).toString() + ")), 1, 0)";
      } else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
         qry = "Iif(CDate(" + arguments.get(0).toString() + ")  BETWEEN (DATEADD('q', DATEDIFF('q', 0, " + arguments.get(2).toString() + ") -(" + arguments.get(1).toString() + "-1), 0)) AND (DATEADD ('d', -1, DATEADD('q', DATEDIFF('q', 0, " + arguments.get(2).toString() + ") +1, 0))), 1, 0)";
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
      if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
         qry = "CASE WHEN CAST(" + dateString + " AS DATE)  BETWEEN (TRUNC(" + arguments.get(2).toString() + ", 'MM') - (" + arguments.get(1).toString() + "-1) MONTH) AND LAST_DAY(" + arguments.get(2).toString() + ")  THEN 1 ELSE 0 END";
      } else {
         if (!fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
            this.setFunctionArguments(arguments);
            return;
         }

         qry = "CASE WHEN CAST(" + dateString + " AS DATE)  BETWEEN (TRUNC(" + arguments.get(2).toString() + ",'YY')+((QUARTER(" + arguments.get(2).toString() + ")-1)*3) MONTH - (3*(" + arguments.get(1).toString() + "-1)) MONTH) AND LAST_DAY((TRUNC(" + arguments.get(2).toString() + ",'YY')+((QUARTER(" + arguments.get(2).toString() + ")-1)*3) MONTH + 2 MONTH))  THEN 1 ELSE 0 END";
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
}
