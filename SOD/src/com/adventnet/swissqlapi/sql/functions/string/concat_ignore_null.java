package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class concat_ignore_null extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("NULLIF");
      Vector arguments = new Vector();
      SelectColumn sc_concat = new SelectColumn();
      FunctionCalls fn_concat = new FunctionCalls();
      TableColumn tb_concat = new TableColumn();
      tb_concat.setColumnName("CONCAT");
      fn_concat.setFunctionName(tb_concat);
      Vector vc_concatIn = new Vector();
      Vector vc_concatOut = new Vector();
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

         SelectColumn sc_coalesce = new SelectColumn();
         FunctionCalls fn_coalesce = new FunctionCalls();
         TableColumn tb_coalesce = new TableColumn();
         tb_coalesce.setColumnName("COALESCE");
         fn_coalesce.setFunctionName(tb_coalesce);
         Vector vc_coalesceIn = new Vector();
         vc_coalesceIn.addElement(vector1.get(i_count));
         vc_coalesceIn.addElement("''");
         Vector vc_coalesceOut = new Vector();
         fn_coalesce.setFunctionArguments(vc_coalesceIn);
         vc_coalesceOut.addElement(fn_coalesce);
         sc_coalesce.setColumnExpression(vc_coalesceOut);
         vc_concatIn.addElement(sc_coalesce);
      }

      fn_concat.setFunctionArguments(vc_concatIn);
      vc_concatOut.addElement(fn_concat);
      sc_concat.setColumnExpression(vc_concatOut);
      arguments.addElement(sc_concat);
      arguments.addElement("''");
      this.setFunctionArguments(arguments);
   }
}
