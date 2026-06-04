package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class addtimetotime extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector vector1 = new Vector(1);
      Vector vector2 = new Vector(1);

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector1.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            vector2.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vector1.addElement(this.functionArguments.elementAt(i_count));
            vector2.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.functionName.setColumnName("");
      Vector timeArguments = new Vector();
      SelectColumn sc = new SelectColumn();
      Vector vc_Out = new Vector();
      Vector vc_tts1 = new Vector();
      FunctionCalls fnCl_tts1 = new FunctionCalls();
      TableColumn tbCl_tts1 = new TableColumn();
      tbCl_tts1.setColumnName("TIME_TO_SEC");
      fnCl_tts1.setFunctionName(tbCl_tts1);
      vc_tts1.addElement(vector1.get(0));
      fnCl_tts1.setFunctionArguments(vc_tts1);
      Vector vc_tts2 = new Vector();
      FunctionCalls fnCl_tts2 = new FunctionCalls();
      TableColumn tbCl_tts2 = new TableColumn();
      tbCl_tts2.setColumnName("TIME_TO_SEC");
      fnCl_tts2.setFunctionName(tbCl_tts2);
      vc_tts2.addElement(vector1.get(1));
      fnCl_tts2.setFunctionArguments(vc_tts2);
      SelectColumn sc_hr = new SelectColumn();
      sc_hr.setOpenBrace("(");
      Vector vc_sc_hr_in = new Vector();
      SelectColumn sc_ms_add = new SelectColumn();
      sc_ms_add.setOpenBrace("(");
      Vector vc_fns_sc_ms_add = new Vector();
      FunctionCalls fnCl_ms1 = new FunctionCalls();
      TableColumn tbCl_ms1 = new TableColumn();
      tbCl_ms1.setColumnName("MICROSECOND");
      fnCl_ms1.setFunctionName(tbCl_ms1);
      Vector vc_ms1 = new Vector();
      SelectColumn fnCl_ms1_arg = new SelectColumn();
      Vector vc_fnCl_ms1_arg = new Vector();
      TableColumn tc1 = new TableColumn();
      tc1.setColumnName(((TableColumn)((TableColumn)((SelectColumn)vector2.get(0)).getColumnExpression().get(0))).getColumnName());
      vc_fnCl_ms1_arg.addElement(tc1);
      fnCl_ms1_arg.setColumnExpression(vc_fnCl_ms1_arg);
      vc_ms1.addElement(fnCl_ms1_arg);
      fnCl_ms1.setFunctionArguments(vc_ms1);
      vc_fns_sc_ms_add.addElement(fnCl_ms1);
      vc_fns_sc_ms_add.addElement("+");
      FunctionCalls fnCl_ms2 = new FunctionCalls();
      TableColumn tbCl_ms2 = new TableColumn();
      tbCl_ms2.setColumnName("MICROSECOND");
      fnCl_ms2.setFunctionName(tbCl_ms2);
      Vector vc_ms2 = new Vector();
      SelectColumn fnCl_ms2_arg = new SelectColumn();
      Vector vc_fnCl_ms2_arg = new Vector();
      TableColumn tc2 = new TableColumn();
      tc2.setColumnName(((TableColumn)((TableColumn)((SelectColumn)vector2.get(1)).getColumnExpression().get(0))).getColumnName());
      vc_fnCl_ms2_arg.addElement(tc2);
      fnCl_ms2_arg.setColumnExpression(vc_fnCl_ms2_arg);
      vc_ms2.addElement(fnCl_ms2_arg);
      fnCl_ms2.setFunctionArguments(vc_ms2);
      vc_fns_sc_ms_add.addElement(fnCl_ms2);
      sc_ms_add.setColumnExpression(vc_fns_sc_ms_add);
      sc_ms_add.setCloseBrace(")");
      vc_sc_hr_in.add(sc_ms_add);
      vc_sc_hr_in.addElement("/");
      vc_sc_hr_in.addElement("1000000");
      sc_hr.setColumnExpression(vc_sc_hr_in);
      sc_hr.setCloseBrace(")");
      vc_Out.addElement(fnCl_tts1);
      vc_Out.addElement("+");
      vc_Out.addElement(fnCl_tts2);
      vc_Out.addElement("+");
      vc_Out.addElement(sc_hr);
      sc.setColumnExpression(vc_Out);
      timeArguments.addElement(sc);
      this.setFunctionArguments(timeArguments);
      this.setCustomDataType("DURATION");
   }
}
