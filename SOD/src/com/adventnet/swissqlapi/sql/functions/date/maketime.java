package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class maketime extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CAST");
      this.setAsDatatype("as");
      Vector finalArgument = new Vector();
      SelectColumn sc_makeTime = new SelectColumn();
      FunctionCalls fn_makeTime = new FunctionCalls();
      TableColumn tc_makeTime = new TableColumn();
      tc_makeTime.setColumnName("maketime");
      fn_makeTime.setFunctionName(tc_makeTime);
      Vector vc_makeTimeIn = new Vector();
      Vector vc_makeTimeOut = new Vector();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() >= 1) {
         vc_makeTimeIn.addElement(arguments.get(0));
         if (arguments.size() >= 2) {
            vc_makeTimeIn.addElement(arguments.get(1));
            if (arguments.size() == 3) {
               vc_makeTimeIn.addElement(arguments.get(2));
            } else {
               vc_makeTimeIn.addElement("0");
            }
         } else {
            vc_makeTimeIn.addElement("0");
            vc_makeTimeIn.addElement("0");
         }
      }

      fn_makeTime.setFunctionArguments(vc_makeTimeIn);
      vc_makeTimeOut.addElement(fn_makeTime);
      sc_makeTime.setColumnExpression(vc_makeTimeOut);
      finalArgument.addElement(sc_makeTime);
      finalArgument.add("char");
      this.setFunctionArguments(finalArgument);
   }
}
