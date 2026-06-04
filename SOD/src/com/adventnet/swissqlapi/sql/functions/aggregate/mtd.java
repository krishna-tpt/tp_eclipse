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

public class mtd extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
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

      String aggFunName = null;
      SelectColumn dispCol = null;
      if (vector1.elementAt(0) instanceof SelectColumn) {
         SelectColumn typeofAggFun = (SelectColumn)vector1.elementAt(0);
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
         Vector arguments = new Vector();
         String yrSt = "year";
         String mtSt = "month";
         String dySt = "day";
         String AndOperator = "AND";
         String OrOperator = "OR";
         SelectColumn sc_if = new SelectColumn();
         FunctionCalls fn_if = new FunctionCalls();
         TableColumn tb_if = new TableColumn();
         tb_if.setColumnName("IF");
         fn_if.setFunctionName(tb_if);
         Vector vc_ifIn = new Vector();
         Vector vc_ifOut = new Vector();
         SelectColumn sc_ifCon = new SelectColumn();
         Vector vc_ifCon = new Vector();
         WhereExpression whExp_if = new WhereExpression();
         WhereItem whIt_curryearCheck = this.constructWhItem(to_sqs, from_sqs, yrSt, "<=");
         whExp_if.addWhereItem(whIt_curryearCheck);
         WhereItem whIt_dayCheck = this.constructWhItem(to_sqs, from_sqs, dySt, "<=");
         whExp_if.addWhereItem(whIt_dayCheck);
         WhereExpression whExp_yearMonthCheck = new WhereExpression();
         Vector vc_whItemListYearMonthCheck = new Vector();
         WhereItem whIt_currMonthCheck = this.constructWhItem(to_sqs, from_sqs, mtSt, "<=");
         vc_whItemListYearMonthCheck.addElement(whIt_currMonthCheck);
         WhereItem whIt_prevYearCheck = this.constructWhItem(to_sqs, from_sqs, yrSt, "<");
         vc_whItemListYearMonthCheck.addElement(whIt_prevYearCheck);
         whExp_yearMonthCheck.setWhereItem(vc_whItemListYearMonthCheck);
         whExp_yearMonthCheck.setOpenBrace("(");
         whExp_yearMonthCheck.setCloseBrace(")");
         Vector vc_OperatorMonthCheck = new Vector();
         vc_OperatorMonthCheck.addElement(OrOperator);
         whExp_yearMonthCheck.setOperator(vc_OperatorMonthCheck);
         whExp_if.addWhereExpression(whExp_yearMonthCheck);
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
         vc_ifOut.addElement(fn_if);
         sc_if.setColumnExpression(vc_ifOut);
         arguments.addElement(sc_if);
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
