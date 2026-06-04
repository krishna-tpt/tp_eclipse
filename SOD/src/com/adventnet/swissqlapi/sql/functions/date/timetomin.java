package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class timetomin extends FunctionCalls {
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
      Vector ttmArguments = new Vector();
      FunctionCalls fn_tts = new FunctionCalls();
      TableColumn tb_tts = new TableColumn();
      tb_tts.setColumnName("TIME_TO_SEC");
      fn_tts.setFunctionName(tb_tts);
      Vector vc_ttsIn = new Vector();
      Vector vc_ttsOut = new Vector();
      vc_ttsIn.addElement(arguments.get(0));
      fn_tts.setFunctionArguments(vc_ttsIn);
      vc_ttsOut.addElement(fn_tts);
      SelectColumn sc_ttmin = new SelectColumn();
      Vector vc_ttmin = new Vector();
      vc_ttmin.addElement(fn_tts);
      vc_ttmin.addElement("/");
      vc_ttmin.addElement("60");
      sc_ttmin.setColumnExpression(vc_ttmin);
      ttmArguments.addElement(sc_ttmin);
      this.setFunctionArguments(ttmArguments);
   }
}
