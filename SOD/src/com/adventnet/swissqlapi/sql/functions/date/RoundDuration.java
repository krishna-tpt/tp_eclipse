package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class RoundDuration extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String durUnit = arguments.get(1).toString().trim().toLowerCase();
      this.functionName.setColumnName("");
      Vector round_arg = new Vector();
      SelectColumn sc_round = new SelectColumn();
      Vector vc_round = new Vector();
      vc_round.addElement("ROUND");
      vc_round.addElement("(");
      if (!durUnit.equals("millisecond")) {
         vc_round.addElement("(");
      }

      Object inArg0 = ((SelectColumn)arguments.get(0)).getColumnExpression().get(0);
      if (inArg0 instanceof String) {
         vc_round.addElement(getNumericalDurationValue(inArg0));
      } else {
         vc_round.addElement(arguments.get(0));
      }

      if (durUnit.equals("day")) {
         vc_round.addElement("/86400))*86400");
      } else if (durUnit.equals("hour")) {
         vc_round.addElement("/3600))*3600");
      } else if (durUnit.equals("minute")) {
         vc_round.addElement("/60))*60");
      } else if (durUnit.equals("second")) {
         vc_round.addElement("))");
      } else {
         if (!durUnit.equals("millisecond")) {
            throw new ConvertException("Invalid Argument Value for Function ROUND_DURATION", "INVALID_ARGUMENT_VALUE", new Object[]{"ROUND_DURATION", "DURATION_SCALE", "Provide any one of the following value , DAY, HOUR, MINUTE, SECOND, MILLISECOND"});
         }

         vc_round.addElement(",3)");
      }

      sc_round.setColumnExpression(vc_round);
      round_arg.addElement(sc_round);
      this.setFunctionArguments(round_arg);
      this.setCustomDataType("DURATION");
   }
}
