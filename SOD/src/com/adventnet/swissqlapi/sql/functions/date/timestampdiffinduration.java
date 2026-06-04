package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class timestampdiffinduration extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.functionName.setColumnName("TIMESTAMPDIFF");
      Vector timestdiffArguments = new Vector();
      SelectColumn sc_sec;
      Vector vc_sec;
      TableColumn tc1;
      SelectColumn sc_dateCol1;
      Vector vc_dateCol1;
      SelectColumn sc_now;
      Vector vc_now;
      if (arguments.size() == 2) {
         sc_sec = new SelectColumn();
         vc_sec = new Vector();
         tc1 = new TableColumn();
         tc1.setColumnName("SECOND");
         vc_sec.addElement(tc1);
         sc_sec.setColumnExpression(vc_sec);
         sc_dateCol1 = new SelectColumn();
         vc_dateCol1 = new Vector();
         vc_dateCol1.addElement(arguments.get(0));
         sc_dateCol1.setColumnExpression(vc_dateCol1);
         sc_now = new SelectColumn();
         vc_now = new Vector();
         vc_now.addElement(arguments.get(1));
         sc_now.setColumnExpression(vc_now);
         timestdiffArguments.addElement(sc_sec);
         timestdiffArguments.addElement(sc_dateCol1);
         timestdiffArguments.addElement(sc_now);
      } else if (arguments.size() == 1) {
         sc_sec = new SelectColumn();
         vc_sec = new Vector();
         tc1 = new TableColumn();
         tc1.setColumnName("SECOND");
         vc_sec.addElement(tc1);
         sc_sec.setColumnExpression(vc_sec);
         sc_dateCol1 = new SelectColumn();
         vc_dateCol1 = new Vector();
         vc_dateCol1.addElement(arguments.get(0));
         sc_dateCol1.setColumnExpression(vc_dateCol1);
         sc_now = new SelectColumn();
         vc_now = new Vector();
         FunctionCalls fn_now = new FunctionCalls();
         TableColumn tbCl_now = new TableColumn();
         tbCl_now.setColumnName("now");
         fn_now.setFunctionName(tbCl_now);
         vc_now.addElement(fn_now);
         sc_now.setColumnExpression(vc_now);
         timestdiffArguments.addElement(sc_sec);
         timestdiffArguments.addElement(sc_dateCol1);
         timestdiffArguments.addElement(sc_now);
      }

      this.setFunctionArguments(timestdiffArguments);
      this.setCustomDataType("DURATION");
   }
}
