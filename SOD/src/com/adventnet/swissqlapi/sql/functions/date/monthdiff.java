package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class monthdiff extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TIMESTAMPDIFF");
      Vector finalArguments = new Vector();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      finalArguments.addElement("MONTH");
      SelectColumn toDate = this.getStartDay(to_sqs, from_sqs, 0, arguments);
      SelectColumn fromDate = this.getStartDay(to_sqs, from_sqs, 1, arguments);
      finalArguments.addElement(fromDate);
      finalArguments.addElement(toDate);
      this.setFunctionArguments(finalArguments);
   }

   public SelectColumn getStartDay(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, int arg, Vector arguments) throws ConvertException {
      SelectColumn sc_date = new SelectColumn();
      FunctionCalls fn_date = new FunctionCalls();
      TableColumn tc_date = new TableColumn();
      tc_date.setColumnName("DATE");
      fn_date.setFunctionName(tc_date);
      Vector vc_dateIn = new Vector();
      Vector vc_dateOut = new Vector();
      SelectColumn sc_dateSub = new SelectColumn();
      FunctionCalls fn_dateSub = new FunctionCalls();
      TableColumn tc_dateSub = new TableColumn();
      tc_dateSub.setColumnName("DATE_SUB");
      fn_dateSub.setFunctionName(tc_dateSub);
      Vector vc_dateSubIn = new Vector();
      Vector vc_dateSubOut = new Vector();
      vc_dateSubIn.addElement(arguments.get(arg));
      SelectColumn sc_interval = new SelectColumn();
      Vector vc_interval = new Vector();
      TableColumn tbCl_interval = new TableColumn();
      tbCl_interval.setColumnName("interval");
      vc_interval.addElement(tbCl_interval);
      SelectColumn sc_value = new SelectColumn();
      Vector vc_value = new Vector();
      SelectColumn sc_dayOfMonth = new SelectColumn();
      FunctionCalls fn_dayOfMonth = new FunctionCalls();
      TableColumn tc_dayOfMonth = new TableColumn();
      tc_dayOfMonth.setColumnName("dayofmonth");
      fn_dayOfMonth.setFunctionName(tc_dayOfMonth);
      Vector vc_dayOfMonthIn = new Vector();
      Vector vc_dayOfMonthOut = new Vector();
      if (arguments.elementAt(arg) instanceof SelectColumn) {
         vc_dayOfMonthIn.addElement(((SelectColumn)this.functionArguments.elementAt(arg)).toMySQLSelect(to_sqs, from_sqs));
      } else {
         vc_dayOfMonthIn.addElement(this.functionArguments.elementAt(arg));
      }

      fn_dayOfMonth.setFunctionArguments(vc_dayOfMonthIn);
      vc_dayOfMonthOut.addElement(fn_dayOfMonth);
      sc_dayOfMonth.setOpenBrace("(");
      sc_dayOfMonth.setCloseBrace(")");
      sc_dayOfMonth.setColumnExpression(vc_dayOfMonthOut);
      vc_value.addElement(sc_dayOfMonth);
      vc_value.add("-");
      vc_value.add("1");
      sc_value.setOpenBrace("(");
      sc_value.setCloseBrace(")");
      sc_value.setColumnExpression(vc_value);
      vc_interval.addElement(sc_value);
      vc_interval.addElement(" DAY");
      sc_interval.setColumnExpression(vc_interval);
      vc_dateSubIn.addElement(sc_interval);
      fn_dateSub.setFunctionArguments(vc_dateSubIn);
      vc_dateSubOut.addElement(fn_dateSub);
      sc_dateSub.setColumnExpression(vc_dateSubOut);
      vc_dateIn.addElement(sc_dateSub);
      fn_date.setFunctionArguments(vc_dateIn);
      vc_dateOut.addElement(fn_date);
      sc_date.setColumnExpression(vc_dateOut);
      return sc_date;
   }
}
