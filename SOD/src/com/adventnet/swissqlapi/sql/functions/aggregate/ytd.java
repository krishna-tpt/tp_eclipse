package com.adventnet.swissqlapi.sql.functions.aggregate;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class ytd extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      Vector vector = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vector.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String aggFunName = null;
      SelectColumn dispCol = null;
      if (vector.elementAt(0) instanceof SelectColumn) {
         SelectColumn typeofAggFun = (SelectColumn)vector.elementAt(0);
         Vector vc_typeofAggFun = typeofAggFun.getColumnExpression();
         if (vc_typeofAggFun.elementAt(0) instanceof FunctionCalls) {
            FunctionCalls funCallofAggFun = (FunctionCalls)vc_typeofAggFun.elementAt(0);
            TableColumn tblColofAggFun = funCallofAggFun.getFunctionName();
            aggFunName = tblColofAggFun.getColumnName();
            Vector vcofAggFun = funCallofAggFun.getFunctionArguments();
            dispCol = (SelectColumn)vcofAggFun.elementAt(0);
         }
      }

      if (aggFunName != null) {
         this.functionName.setColumnName(aggFunName);
         String yrSt = "year";
         String mtSt = "month";
         String dySt = "day";
         String AndOperator = "AND";
         String OrOperator = "OR";
         Vector vc_ifIn;
         WhereItem whIt_if;
         if (this.functionArguments.size() > 2) {
            String fiscalStartMonth = "null";
            SelectColumn sc_if;
            if (this.functionArguments.size() > 2 && vector.elementAt(2) instanceof SelectColumn) {
               sc_if = (SelectColumn)vector.elementAt(2);
               Vector vc = sc_if.getColumnExpression();
               if (!(vc.elementAt(0) instanceof String)) {
                  throw new ConvertException("Invalid Argument Value for Function YTD", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"YTD", "FISCAL_START_MONTH"});
               }

               fiscalStartMonth = (String)vc.elementAt(0);
            }

            if (fiscalStartMonth.equalsIgnoreCase("null")) {
               fiscalStartMonth = "1";
            }

            fiscalStartMonth = fiscalStartMonth.replaceAll("'", "");
            this.validateFiscalStartMonth(fiscalStartMonth, "YTD");
            sc_if = new SelectColumn();
            FunctionCalls fn_if = new FunctionCalls();
            TableColumn tb_if = new TableColumn();
            tb_if.setColumnName("IF");
            fn_if.setFunctionName(tb_if);
            vc_ifIn = new Vector();
            Vector vc_ifOut = new Vector();
            SelectColumn sc_ifCon = new SelectColumn();
            Vector vc_ifCon = new Vector();
            whIt_if = new WhereItem();
            WhereColumn whColLeftExp = new WhereColumn();
            Vector vc_whColLeftExp = new Vector();
            WhereColumn whColRightExp = new WhereColumn();
            Vector vc_whColRightExp = new Vector();
            SelectColumn sc_ifFiscal = new SelectColumn();
            FunctionCalls fn_ifFiscal = new FunctionCalls();
            TableColumn tb_ifFiscal = new TableColumn();
            tb_ifFiscal.setColumnName("IF");
            fn_ifFiscal.setFunctionName(tb_ifFiscal);
            Vector vc_ifFiscalIn = new Vector();
            Vector vc_ifFiscalOut = new Vector();
            SelectColumn sc_ifFiscalCon = new SelectColumn();
            Vector vc_ifFiscalCon = new Vector();
            WhereItem whIt_ifFiscalCon = new WhereItem();
            WhereColumn whCol_FiscalConLeftExp = new WhereColumn();
            Vector vc_whCol_FiscalConLeftExp = new Vector();
            WhereColumn whCol_FiscalConRightExp = new WhereColumn();
            Vector vc_whCol_FiscalConRightExp = new Vector();
            vc_whCol_FiscalConLeftExp.addElement(fiscalStartMonth);
            Vector vc_currMonth = new Vector();
            vc_currMonth.addElement(this.now());
            vc_whCol_FiscalConRightExp.addElement(this.date_fun(vc_currMonth, 0, "month"));
            whCol_FiscalConLeftExp.setColumnExpression(vc_whCol_FiscalConLeftExp);
            whCol_FiscalConRightExp.setColumnExpression(vc_whCol_FiscalConRightExp);
            whIt_ifFiscalCon.setLeftWhereExp(whCol_FiscalConLeftExp);
            whIt_ifFiscalCon.setOperator("<");
            whIt_ifFiscalCon.setRightWhereExp(whCol_FiscalConRightExp);
            vc_ifFiscalCon.addElement(whIt_ifFiscalCon);
            sc_ifFiscalCon.setColumnExpression(vc_ifFiscalCon);
            vc_ifFiscalIn.addElement(sc_ifFiscalCon);
            vc_ifFiscalIn.addElement(this.ytd_FiscalStmt(to_sqs, from_sqs, fiscalStartMonth, vector, AndOperator));
            vc_ifFiscalIn.addElement(this.ytd_FiscalStmt(to_sqs, from_sqs, fiscalStartMonth, vector, OrOperator));
            fn_ifFiscal.setFunctionArguments(vc_ifFiscalIn);
            vc_ifFiscalOut.addElement(fn_ifFiscal);
            sc_ifFiscal.setColumnExpression(vc_ifFiscalOut);
            vc_whColLeftExp.addElement(sc_ifFiscal);
            vc_whColRightExp.addElement("1");
            whColLeftExp.setColumnExpression(vc_whColLeftExp);
            whColRightExp.setColumnExpression(vc_whColRightExp);
            whIt_if.setLeftWhereExp(whColLeftExp);
            whIt_if.setOperator("=");
            whIt_if.setRightWhereExp(whColRightExp);
            vc_ifCon.addElement(whIt_if);
            sc_ifCon.setColumnExpression(vc_ifCon);
            vc_ifIn.addElement(sc_ifCon);
            vc_ifIn.addElement(dispCol);
            vc_ifIn.addElement("null");
            fn_if.setFunctionArguments(vc_ifIn);
            vc_ifOut.addElement(fn_if);
            sc_if.setColumnExpression(vc_ifOut);
            arguments.addElement(sc_if);
         } else {
            SelectColumn sc_if = new SelectColumn();
            FunctionCalls fn_if = new FunctionCalls();
            TableColumn tb_if = new TableColumn();
            tb_if.setColumnName("IF");
            fn_if.setFunctionName(tb_if);
            Vector vc_ifIn = new Vector();
            vc_ifIn = new Vector();
            SelectColumn sc_ifCon = new SelectColumn();
            Vector vc_ifCon = new Vector();
            WhereExpression whExp_if = new WhereExpression();
            whIt_if = this.constructWhItem(to_sqs, from_sqs, yrSt, "<=");
            whExp_if.addWhereItem(whIt_if);
            WhereItem whIt_currmonthCheck = this.constructWhItem(to_sqs, from_sqs, mtSt, "<=");
            whExp_if.addWhereItem(whIt_currmonthCheck);
            WhereExpression whExp_dayMonthCheck = new WhereExpression();
            Vector vc_whItemListDayMonthCheck = new Vector();
            WhereItem whIt_dayCheck = this.constructWhItem(to_sqs, from_sqs, dySt, "<=");
            WhereItem whIt_prevMonthCheck = this.constructWhItem(to_sqs, from_sqs, mtSt, "<");
            vc_whItemListDayMonthCheck.addElement(whIt_dayCheck);
            vc_whItemListDayMonthCheck.addElement(whIt_prevMonthCheck);
            whExp_dayMonthCheck.setWhereItem(vc_whItemListDayMonthCheck);
            whExp_dayMonthCheck.setOpenBrace("(");
            whExp_dayMonthCheck.setCloseBrace(")");
            Vector vc_OperatorMonthCheck = new Vector();
            vc_OperatorMonthCheck.addElement(OrOperator);
            whExp_dayMonthCheck.setOperator(vc_OperatorMonthCheck);
            whExp_if.addWhereExpression(whExp_dayMonthCheck);
            Vector vc_Operator = new Vector();
            vc_Operator.addElement(AndOperator);
            vc_Operator.addElement(AndOperator);
            whExp_if.setOperator(vc_Operator);
            whExp_if.setOpenBrace("(");
            whExp_if.setCloseBrace(")");
            vc_ifCon.addElement(whExp_if);
            sc_ifCon.setColumnExpression(vc_ifCon);
            vc_ifIn.addElement(sc_ifCon);
            vc_ifIn.addElement(dispCol);
            vc_ifIn.addElement("null");
            fn_if.setFunctionArguments(vc_ifIn);
            vc_ifIn.addElement(fn_if);
            sc_if.setColumnExpression(vc_ifIn);
            arguments.addElement(sc_if);
         }

         this.setFunctionArguments(arguments);
      }

   }

   public WhereItem constructWhItem(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, String type, String operator) throws ConvertException {
      Vector vector = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vector.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      WhereItem whIt_dtType = new WhereItem();
      WhereColumn whColLeftYearExp = new WhereColumn();
      Vector vc_whColLeftYearExp = new Vector();
      WhereColumn whColRightYearExp = new WhereColumn();
      Vector vc_whColRightYearExp = new Vector();
      SelectColumn sc_leftYearExp = this.date_fun(vector, 1, type);
      vc_whColLeftYearExp.addElement(sc_leftYearExp);
      whColLeftYearExp.setColumnExpression(vc_whColLeftYearExp);
      whIt_dtType.setLeftWhereExp(whColLeftYearExp);
      SelectColumn rightYearExp_Now = this.now();
      Vector vc_rightYearExp_Now = new Vector();
      vc_rightYearExp_Now.addElement(rightYearExp_Now);
      SelectColumn sc_rightYearExp = this.date_fun(vc_rightYearExp_Now, 0, type);
      vc_whColRightYearExp.addElement(sc_rightYearExp);
      whColRightYearExp.setColumnExpression(vc_whColRightYearExp);
      whIt_dtType.setRightWhereExp(whColRightYearExp);
      whIt_dtType.setOperator(operator);
      return whIt_dtType;
   }

   public SelectColumn ytd_FiscalStmt(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, String fiscalStartMonth, Vector vector, String Operator) throws ConvertException {
      String yrSt = "year";
      String mtSt = "month";
      String dySt = "day";
      String AndOperator = "AND";
      String OrOperator = "OR";
      SelectColumn sc_ifFiscalTrueStmt = new SelectColumn();
      FunctionCalls fn_ifFiscalTrueStmt = new FunctionCalls();
      TableColumn tb_ifFiscalTrueStmt = new TableColumn();
      tb_ifFiscalTrueStmt.setColumnName("IF");
      fn_ifFiscalTrueStmt.setFunctionName(tb_ifFiscalTrueStmt);
      Vector vc_ifFiscalTrueStmtIn = new Vector();
      Vector vc_ifFiscalTrueStmtOut = new Vector();
      SelectColumn sc_ifFiscalTrueStmtCon = new SelectColumn();
      Vector vc_ifFiscalTrueStmtCon = new Vector();
      WhereExpression whExp_ifFiscalTrueStmt = new WhereExpression();
      WhereItem whIt_curryearCheck = this.constructWhItem(to_sqs, from_sqs, yrSt, "<=");
      WhereExpression whExp_dayMonthCheck = new WhereExpression();
      WhereItem whIt_currmonthCheck = this.constructWhItem(to_sqs, from_sqs, mtSt, "<=");
      WhereExpression whExp_dayPrevMonthCheck = new WhereExpression();
      Vector vc_whItemListdayPrevMonthCheck = new Vector();
      WhereItem whIt_dayCheck = this.constructWhItem(to_sqs, from_sqs, dySt, "<=");
      WhereItem whIt_prevMonthCheck = this.constructWhItem(to_sqs, from_sqs, mtSt, "<");
      vc_whItemListdayPrevMonthCheck.addElement(whIt_dayCheck);
      vc_whItemListdayPrevMonthCheck.addElement(whIt_prevMonthCheck);
      whExp_dayPrevMonthCheck.setWhereItem(vc_whItemListdayPrevMonthCheck);
      whExp_dayPrevMonthCheck.setOpenBrace("(");
      whExp_dayPrevMonthCheck.setCloseBrace(")");
      Vector vc_OperatorPrevMonthCheck = new Vector();
      vc_OperatorPrevMonthCheck.addElement(OrOperator);
      whExp_dayPrevMonthCheck.setOperator(vc_OperatorPrevMonthCheck);
      WhereItem whIt_fiscalMonthCheck = new WhereItem();
      WhereColumn whCol_fiscalMonthCheckLeftExp = new WhereColumn();
      Vector vc_whCol_fiscalMonthCheckLeftExp = new Vector();
      WhereColumn whCol_fiscalMonthCheckRightExp = new WhereColumn();
      Vector vc_whCol_fiscalMonthCheckRightExp = new Vector();
      vc_whCol_fiscalMonthCheckLeftExp.addElement(this.date_fun(vector, 1, mtSt));
      vc_whCol_fiscalMonthCheckRightExp.addElement(fiscalStartMonth);
      whCol_fiscalMonthCheckLeftExp.setColumnExpression(vc_whCol_fiscalMonthCheckLeftExp);
      whCol_fiscalMonthCheckRightExp.setColumnExpression(vc_whCol_fiscalMonthCheckRightExp);
      whIt_fiscalMonthCheck.setLeftWhereExp(whCol_fiscalMonthCheckLeftExp);
      whIt_fiscalMonthCheck.setRightWhereExp(whCol_fiscalMonthCheckRightExp);
      whIt_fiscalMonthCheck.setOperator(">=");
      whExp_dayMonthCheck.addWhereItem(whIt_currmonthCheck);
      whExp_dayMonthCheck.addWhereExpression(whExp_dayPrevMonthCheck);
      whExp_dayMonthCheck.addWhereItem(whIt_fiscalMonthCheck);
      whExp_dayMonthCheck.setOpenBrace("(");
      whExp_dayMonthCheck.setCloseBrace(")");
      Vector vc_OperatorMonthCheck = new Vector();
      vc_OperatorMonthCheck.addElement(AndOperator);
      vc_OperatorMonthCheck.addElement(Operator);
      whExp_dayMonthCheck.setOperator(vc_OperatorMonthCheck);
      Vector vc_Operator = new Vector();
      vc_Operator.addElement(AndOperator);
      whExp_ifFiscalTrueStmt.setOperator(vc_Operator);
      whExp_ifFiscalTrueStmt.setOpenBrace("(");
      whExp_ifFiscalTrueStmt.setCloseBrace(")");
      whExp_ifFiscalTrueStmt.addWhereItem(whIt_curryearCheck);
      whExp_ifFiscalTrueStmt.addWhereExpression(whExp_dayMonthCheck);
      vc_ifFiscalTrueStmtCon.addElement(whExp_ifFiscalTrueStmt);
      sc_ifFiscalTrueStmtCon.setColumnExpression(vc_ifFiscalTrueStmtCon);
      vc_ifFiscalTrueStmtIn.addElement(sc_ifFiscalTrueStmtCon);
      vc_ifFiscalTrueStmtIn.addElement("1");
      vc_ifFiscalTrueStmtIn.addElement("0");
      fn_ifFiscalTrueStmt.setFunctionArguments(vc_ifFiscalTrueStmtIn);
      vc_ifFiscalTrueStmtOut.addElement(fn_ifFiscalTrueStmt);
      sc_ifFiscalTrueStmt.setColumnExpression(vc_ifFiscalTrueStmtOut);
      return sc_ifFiscalTrueStmt;
   }

   public SelectColumn date_fun(Vector vector, int arg_index, String dt_type) {
      SelectColumn sc_dtType = new SelectColumn();
      FunctionCalls fn_dtType = new FunctionCalls();
      TableColumn tb_dtType = new TableColumn();
      tb_dtType.setColumnName(dt_type.toUpperCase());
      fn_dtType.setFunctionName(tb_dtType);
      Vector vc_dtTypeIn = new Vector();
      Vector vc_dtTypeOut = new Vector();
      vc_dtTypeIn.addElement(vector.get(arg_index));
      fn_dtType.setFunctionArguments(vc_dtTypeIn);
      vc_dtTypeOut.addElement(fn_dtType);
      sc_dtType.setColumnExpression(vc_dtTypeOut);
      return sc_dtType;
   }

   public SelectColumn now() {
      SelectColumn sc_Now = new SelectColumn();
      FunctionCalls fn_Now = new FunctionCalls();
      TableColumn tb_Now = new TableColumn();
      tb_Now.setColumnName("NOW");
      fn_Now.setFunctionName(tb_Now);
      Vector vc_NowIn = new Vector();
      Vector vc_NowOut = new Vector();
      fn_Now.setFunctionArguments(vc_NowIn);
      vc_NowOut.addElement(fn_Now);
      sc_Now.setColumnExpression(vc_NowOut);
      return sc_Now;
   }
}
