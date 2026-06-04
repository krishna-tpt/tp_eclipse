package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class iscurrenthour extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.functionName.setColumnName("IF");
      Vector vc_isCurrhr = new Vector();
      WhereExpression whereExpression = new WhereExpression();
      SelectColumn sc_whIt = new SelectColumn();
      Vector vc_whIt = new Vector();
      WhereItem whIt_isCurrHour = new WhereItem();
      WhereColumn whCol_LeftExp = new WhereColumn();
      Vector vc_leftExp = new Vector();
      WhereColumn whCol_RightExp = new WhereColumn();
      Vector vc_rightExp = new Vector();
      SelectColumn sc_lefthr = new SelectColumn();
      Vector vc_lefthrIn = new Vector();
      Vector vc_lefthrOut = new Vector();
      FunctionCalls fnCl_lefthr = new FunctionCalls();
      TableColumn tbCl_lefthr = new TableColumn();
      tbCl_lefthr.setColumnName("HOUR");
      fnCl_lefthr.setFunctionName(tbCl_lefthr);
      vc_lefthrIn.addElement(arguments.get(0));
      fnCl_lefthr.setFunctionArguments(vc_lefthrIn);
      vc_lefthrOut.addElement(fnCl_lefthr);
      sc_lefthr.setColumnExpression(vc_lefthrOut);
      vc_leftExp.addElement(sc_lefthr);
      whCol_LeftExp.setColumnExpression(vc_leftExp);
      SelectColumn sc_righthr = new SelectColumn();
      Vector vc_righthrIn = new Vector();
      Vector vc_righthrOut = new Vector();
      FunctionCalls fnCl_righthr = new FunctionCalls();
      TableColumn tbCl_righthr = new TableColumn();
      tbCl_righthr.setColumnName("HOUR");
      fnCl_righthr.setFunctionName(tbCl_righthr);
      SelectColumn sc_currtime = new SelectColumn();
      FunctionCalls fn_currtime = new FunctionCalls();
      TableColumn tc_currtime = new TableColumn();
      tc_currtime.setColumnName("current_time");
      fn_currtime.setFunctionName(tc_currtime);
      Vector vc_currtime = new Vector();
      vc_currtime.addElement(fn_currtime);
      sc_currtime.setColumnExpression(vc_currtime);
      vc_righthrIn.addElement(sc_currtime);
      fnCl_righthr.setFunctionArguments(vc_righthrIn);
      vc_righthrOut.addElement(fnCl_righthr);
      sc_righthr.setColumnExpression(vc_righthrOut);
      vc_rightExp.addElement(sc_righthr);
      whCol_RightExp.setColumnExpression(vc_rightExp);
      whIt_isCurrHour.setLeftWhereExp(whCol_LeftExp);
      whIt_isCurrHour.setOperator("=");
      whIt_isCurrHour.setRightWhereExp(whCol_RightExp);
      vc_whIt.addElement(whIt_isCurrHour);
      whereExpression.setWhereItem(vc_whIt);
      Vector vc_whExp = new Vector();
      vc_whExp.addElement(whereExpression);
      sc_whIt.setColumnExpression(vc_whExp);
      SelectColumn sc_trueStmt = new SelectColumn();
      Vector vc_trueStmt = new Vector();
      vc_trueStmt.addElement("1");
      sc_trueStmt.setColumnExpression(vc_trueStmt);
      SelectColumn sc_falseStmt = new SelectColumn();
      Vector vc_falseStmt = new Vector();
      vc_falseStmt.addElement("0");
      sc_falseStmt.setColumnExpression(vc_falseStmt);
      vc_isCurrhr.addElement(sc_whIt);
      vc_isCurrhr.addElement(sc_trueStmt);
      vc_isCurrhr.addElement(sc_falseStmt);
      this.setFunctionArguments(vc_isCurrhr);
   }
}
