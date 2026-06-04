package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class substring_count extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
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
      }

      SelectColumn sc = new SelectColumn();
      Vector finalArgument = new Vector();
      Vector arguments = new Vector();
      Vector args_Quotient = new Vector();
      SelectColumn sc_Quotient = new SelectColumn();
      Vector vc_Quotient = new Vector();
      SelectColumn sc_Remainder = new SelectColumn();
      Vector vc_Remainder = new Vector();
      FunctionCalls fnCl = new FunctionCalls();
      TableColumn TC = new TableColumn();
      TC.setColumnName("");
      fnCl.setFunctionName(TC);
      Vector args = new Vector();
      SelectColumn sc_left = new SelectColumn();
      Vector vc_left = new Vector();
      SelectColumn sc_Right = new SelectColumn();
      Vector vc_Right = new Vector();
      FunctionCalls fnCl_lenStr = new FunctionCalls();
      TableColumn tbCl_lenStr = new TableColumn();
      tbCl_lenStr.setColumnName("LENGTH");
      fnCl_lenStr.setFunctionName(tbCl_lenStr);
      Vector vc_lenStr = new Vector();
      vc_lenStr.addElement(vector1.get(0));
      fnCl_lenStr.setFunctionArguments(vc_lenStr);
      vc_left.addElement(fnCl_lenStr);
      sc_left.setColumnExpression(vc_left);
      vc_Quotient.addElement(sc_left);
      String Sub_Operator = "-";
      vc_Quotient.addElement(Sub_Operator);
      FunctionCalls fnCl_lenRepStr = new FunctionCalls();
      TableColumn tbCl_lenRepStr = new TableColumn();
      tbCl_lenRepStr.setColumnName("LENGTH");
      fnCl_lenRepStr.setFunctionName(tbCl_lenRepStr);
      Vector vc_lenRepStr = new Vector();
      SelectColumn sc_RepStr = new SelectColumn();
      Vector vc_RepStr = new Vector();
      FunctionCalls fnCl_RepStr = new FunctionCalls();
      TableColumn tbCl_RepStr = new TableColumn();
      tbCl_RepStr.setColumnName("REPLACE");
      fnCl_RepStr.setFunctionName(tbCl_RepStr);
      Vector vc_Replace = new Vector();
      vc_Replace.addElement(vector2.get(0));
      vc_Replace.addElement(vector2.get(1));
      String Replace_String = "''";
      vc_Replace.addElement(Replace_String);
      fnCl_RepStr.setFunctionArguments(vc_Replace);
      vc_RepStr.addElement(fnCl_RepStr);
      sc_RepStr.setColumnExpression(vc_RepStr);
      vc_lenRepStr.addElement(sc_RepStr);
      fnCl_lenRepStr.setFunctionArguments(vc_lenRepStr);
      vc_Right.addElement(fnCl_lenRepStr);
      sc_Right.setColumnExpression(vc_Right);
      vc_Quotient.addElement(sc_Right);
      sc_Quotient.setColumnExpression(vc_Quotient);
      args.addElement(sc_Quotient);
      fnCl.setFunctionArguments(args);
      SelectColumn sc1 = new SelectColumn();
      args_Quotient.addElement(fnCl);
      sc1.setColumnExpression(args_Quotient);
      arguments.addElement(sc1);
      String Div_Operator = "DIV";
      arguments.addElement(Div_Operator);
      FunctionCalls fnCl_lenRep = new FunctionCalls();
      TableColumn tbCl_lenRep = new TableColumn();
      tbCl_lenRep.setColumnName("LENGTH");
      fnCl_lenRep.setFunctionName(tbCl_lenRep);
      Vector vc_lenRep = new Vector();
      vc_lenRep.addElement(vector1.get(1));
      fnCl_lenRep.setFunctionArguments(vc_lenRep);
      vc_Remainder.addElement(fnCl_lenRep);
      sc_Remainder.setColumnExpression(vc_Remainder);
      arguments.addElement(sc_Remainder);
      sc.setColumnExpression(arguments);
      finalArgument.addElement(sc);
      this.setFunctionArguments(finalArgument);
   }
}
