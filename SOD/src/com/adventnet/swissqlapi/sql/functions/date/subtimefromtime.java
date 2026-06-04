package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class subtimefromtime extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.functionName.setColumnName("TIME");
      Vector timeArguments = new Vector();
      SelectColumn sc_timediff = new SelectColumn();
      Vector vc_timediffIn = new Vector();
      Vector vc_timediffOut = new Vector();
      FunctionCalls fnCl_timediff = new FunctionCalls();
      TableColumn tbCl_timediff = new TableColumn();
      tbCl_timediff.setColumnName("TIMEDIFF");
      fnCl_timediff.setFunctionName(tbCl_timediff);
      vc_timediffIn.addElement(arguments.get(0));
      vc_timediffIn.addElement(arguments.get(1));
      fnCl_timediff.setFunctionArguments(vc_timediffIn);
      vc_timediffOut.addElement(fnCl_timediff);
      sc_timediff.setColumnExpression(vc_timediffOut);
      SelectColumn sc_abs = new SelectColumn();
      Vector vc_absIn = new Vector();
      Vector vc_absOut = new Vector();
      FunctionCalls fnCl_abs = new FunctionCalls();
      TableColumn tbCl_abs = new TableColumn();
      tbCl_abs.setColumnName("ABS");
      fnCl_abs.setFunctionName(tbCl_abs);
      vc_absIn.addElement(sc_timediff);
      fnCl_abs.setFunctionArguments(vc_absIn);
      vc_absOut.add(fnCl_abs);
      sc_abs.setColumnExpression(vc_absOut);
      timeArguments.addElement(sc_abs);
      this.setFunctionArguments(timeArguments);
   }
}
