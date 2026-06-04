package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class monthsofduration extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.functionName.setColumnName("FLOOR");
      Vector weekArg = new Vector();
      SelectColumn sc_floor = new SelectColumn();
      sc_floor.setOpenBrace("(");
      Vector vc_floor = new Vector();
      Object inArg0 = ((SelectColumn)arguments.get(0)).getColumnExpression().get(0);
      if (inArg0 instanceof String) {
         vc_floor.addElement(getNumericalDurationValue(inArg0));
      } else {
         vc_floor.addElement(arguments.get(0));
      }

      vc_floor.addElement("/");
      vc_floor.addElement("2629746");
      sc_floor.setColumnExpression(vc_floor);
      sc_floor.setCloseBrace(")");
      weekArg.addElement(sc_floor);
      this.setFunctionArguments(weekArg);
   }
}
