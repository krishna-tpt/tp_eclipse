package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class makeduration extends FunctionCalls {
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
      Vector mkdArg = new Vector();
      SelectColumn sc_floor = new SelectColumn();
      Vector vc_hrOut = new Vector();
      vc_hrOut.addElement("(");
      this.changeArgumentValue(arguments, vc_hrOut, 0);
      vc_hrOut.addElement("*");
      vc_hrOut.addElement("31556952");
      vc_hrOut.addElement(")");
      vc_hrOut.addElement("+");
      vc_hrOut.addElement("(");
      this.changeArgumentValue(arguments, vc_hrOut, 1);
      vc_hrOut.addElement("*");
      vc_hrOut.addElement("2629746");
      vc_hrOut.addElement(")");
      vc_hrOut.addElement("+");
      vc_hrOut.addElement("(");
      this.changeArgumentValue(arguments, vc_hrOut, 2);
      vc_hrOut.addElement("*");
      vc_hrOut.addElement("604800");
      vc_hrOut.addElement(")");
      vc_hrOut.addElement("+");
      vc_hrOut.addElement("(");
      this.changeArgumentValue(arguments, vc_hrOut, 3);
      vc_hrOut.addElement("*");
      vc_hrOut.addElement("86400");
      vc_hrOut.addElement(")");
      vc_hrOut.addElement("+");
      vc_hrOut.addElement("(");
      this.changeArgumentValue(arguments, vc_hrOut, 4);
      vc_hrOut.addElement("*");
      vc_hrOut.addElement("3600");
      vc_hrOut.addElement(")");
      vc_hrOut.addElement("+");
      vc_hrOut.addElement("(");
      this.changeArgumentValue(arguments, vc_hrOut, 5);
      vc_hrOut.addElement("*");
      vc_hrOut.addElement("60");
      vc_hrOut.addElement(")");
      vc_hrOut.addElement("+");
      vc_hrOut.addElement("(");
      this.changeArgumentValue(arguments, vc_hrOut, 6);
      vc_hrOut.addElement(")");
      sc_floor.setColumnExpression(vc_hrOut);
      mkdArg.addElement(sc_floor);
      this.setFunctionArguments(mkdArg);
      this.setCustomDataType("DURATION");
   }

   private void changeArgumentValue(Vector arguments, Vector vc_hrOut, int index) {
      boolean isZero = ((SelectColumn)arguments.get(index)).getColumnExpression().get(0).equals("0");
      if (isZero) {
         vc_hrOut.addElement("0");
      } else {
         vc_hrOut.addElement(arguments.get(index));
      }

   }
}
