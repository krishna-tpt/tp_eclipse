package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class BinToNum extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CONV");
      Vector arguments = new Vector();
      StringBuffer sb = new StringBuffer();
      String arg = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = ((SelectColumn)((SelectColumn)this.functionArguments.elementAt(i_count))).toMySQLSelect(to_sqs, from_sqs);
            Vector colExp = sc.getColumnExpression();
            if (colExp.get(0) instanceof String) {
               sb.append((String)colExp.get(0));
            }
         }
      }

      arg = sb.toString();
      SelectColumn argSelectColumn = new SelectColumn();
      Vector v = new Vector();
      v.add(arg);
      argSelectColumn.setColumnExpression(v);
      arguments.add(argSelectColumn);
      v = new Vector();
      argSelectColumn = new SelectColumn();
      v.add("2");
      argSelectColumn.setColumnExpression(v);
      arguments.add(argSelectColumn);
      v = new Vector();
      argSelectColumn = new SelectColumn();
      v.add("10");
      argSelectColumn.setColumnExpression(v);
      arguments.add(argSelectColumn);
      this.setFunctionArguments(arguments);
   }
}
