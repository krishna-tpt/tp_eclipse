package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class endofhour extends FunctionCalls {
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
      SelectColumn sc_hr = new SelectColumn();
      FunctionCalls fn_hr = new FunctionCalls();
      TableColumn tb_hr = new TableColumn();
      tb_hr.setColumnName("HOUR");
      fn_hr.setFunctionName(tb_hr);
      Vector vc_hrIn = new Vector();
      vc_hrIn.addElement(arguments.get(0));
      fn_hr.setFunctionArguments(vc_hrIn);
      Vector vc_hrOut = new Vector();
      vc_hrOut.addElement(fn_hr);
      sc_hr.setColumnExpression(vc_hrOut);
      SelectColumn sc_con = new SelectColumn();
      FunctionCalls fn_con = new FunctionCalls();
      TableColumn tb_con = new TableColumn();
      tb_con.setColumnName("CONCAT");
      fn_con.setFunctionName(tb_con);
      Vector vc_conIn = new Vector();
      vc_conIn.addElement(sc_hr);
      SelectColumn sc_2 = new SelectColumn();
      Vector vc_2 = new Vector();
      vc_2.addElement("':59:59.999999'");
      sc_2.setColumnExpression(vc_2);
      vc_conIn.addElement(sc_2);
      fn_con.setFunctionArguments(vc_conIn);
      Vector vc_conOut = new Vector();
      vc_conOut.addElement(fn_con);
      sc_con.setColumnExpression(vc_conOut);
      Vector timeArguments = new Vector();
      timeArguments.addElement(sc_con);
      this.setFunctionArguments(timeArguments);
   }
}
