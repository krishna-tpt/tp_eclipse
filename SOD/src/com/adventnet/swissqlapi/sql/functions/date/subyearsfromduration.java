package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class subyearsfromduration extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.functionName.setColumnName("");
      Vector weekArg = new Vector();
      SelectColumn sc_floor = new SelectColumn();
      Vector vc_floor = new Vector();
      Object inArg0 = ((SelectColumn)arguments.get(0)).getColumnExpression().get(0);
      if (inArg0 instanceof String) {
         vc_floor.addElement(getNumericalDurationValue(inArg0));
      } else {
         vc_floor.addElement(arguments.get(0));
      }

      vc_floor.addElement("-");
      SelectColumn sc1 = new SelectColumn();
      Vector vc_sc1 = new Vector();
      sc1.setOpenBrace("(");
      if (((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof TableColumn) {
         TableColumn tc2 = new TableColumn();
         tc2.setColumnName(((TableColumn)((TableColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(0))).getColumnName());
         vc_sc1.addElement(tc2);
      } else {
         vc_sc1.addElement(arguments.get(1));
      }

      vc_sc1.addElement("*");
      vc_sc1.addElement("31556952");
      sc1.setColumnExpression(vc_sc1);
      sc1.setCloseBrace(")");
      vc_floor.addElement(sc1);
      sc_floor.setColumnExpression(vc_floor);
      weekArg.addElement(sc_floor);
      this.setFunctionArguments(weekArg);
      this.setCustomDataType("DURATION");
   }
}
