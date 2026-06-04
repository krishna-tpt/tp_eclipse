package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class groupFirstLast extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int argLength = this.functionArguments.size();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < argLength; ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Object arg1 = arguments.get(0);
      SelectColumn arg1SC = new SelectColumn();
      Vector arg1SCColExp = new Vector();
      arg1SCColExp.addElement(arg1);
      arg1SC.setColumnExpression(arg1SCColExp);
      SelectColumn GCnctFSC = new SelectColumn();
      Vector GCntFSCColExp = new Vector();
      FunctionCalls GCnctFC = new FunctionCalls();
      GCnctFC.getFunctionName().setColumnName("group_concat");
      Vector GCnctFCArgs = new Vector();
      GCnctFCArgs.addElement(arg1);
      GCnctFCArgs.addElement("'#@#'");
      GCnctFC.setFunctionArguments(GCnctFCArgs);
      GCnctFC.setSeparatorString("SEPARATOR");
      GCntFSCColExp.addElement(GCnctFC);
      GCnctFSC.setColumnExpression(GCntFSCColExp);
      SelectColumn subInFSC = new SelectColumn();
      Vector subInFSCColExp = new Vector();
      FunctionCalls subInFC = new FunctionCalls();
      subInFC.getFunctionName().setColumnName("substring_index");
      Vector subInFCArgs = new Vector();
      subInFCArgs.addElement(GCnctFSC);
      subInFCArgs.addElement("'#@#'");
      subInFCArgs.addElement(this.functionName.toString().equalsIgnoreCase("group_first") ? "1" : "-1");
      subInFC.setFunctionArguments(subInFCArgs);
      subInFSCColExp.addElement(subInFC);
      subInFSC.setColumnExpression(subInFSCColExp);
      this.functionName.setColumnName("");
      Vector fnArgs = new Vector();
      fnArgs.addElement(subInFSC);
      this.setFunctionArguments(fnArgs);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int argLength = this.functionArguments.size();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < argLength; ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dataType = from_sqs != null && from_sqs.isVerticaDb() ? "VARCHAR" : "TEXT";
      Object arg1 = arguments.get(0);
      SelectColumn arg1SC = new SelectColumn();
      Vector arg1SCColExp = new Vector();
      arg1SCColExp.addElement(arg1);
      arg1SC.setColumnExpression(arg1SCColExp);
      SelectColumn castFSC = new SelectColumn();
      Vector castFSCColExp = new Vector();
      FunctionCalls castFC = new FunctionCalls();
      castFC.getFunctionName().setColumnName("cast");
      Vector castFCArgs = new Vector();
      castFCArgs.addElement(arg1SC);
      castFC.setAsDatatype("as");
      castFCArgs.addElement(dataType);
      castFC.setFunctionArguments(castFCArgs);
      castFSCColExp.addElement(castFC);
      castFSC.setColumnExpression(castFSCColExp);
      SelectColumn strAggFSC = new SelectColumn();
      Vector strAggFSCColExp = new Vector();
      FunctionCalls strAggFC = new FunctionCalls();
      strAggFC.getFunctionName().setColumnName("string_agg");
      Vector strAggFCArgs = new Vector();
      strAggFCArgs.addElement(castFSC);
      strAggFCArgs.addElement("'#@#'");
      strAggFC.setFunctionArguments(strAggFCArgs);
      strAggFSCColExp.addElement(strAggFC);
      strAggFSC.setColumnExpression(strAggFSCColExp);
      SelectColumn subInFSC = new SelectColumn();
      Vector subInFSCColExp = new Vector();
      FunctionCalls subInFC = new FunctionCalls();
      subInFC.getFunctionName().setColumnName("substring_index");
      Vector subInFCArgs = new Vector();
      subInFCArgs.addElement(strAggFSC);
      subInFCArgs.addElement("'#@#'");
      subInFCArgs.addElement(this.functionName.toString().equalsIgnoreCase("group_first") ? "1" : "-1");
      subInFC.setFunctionArguments(subInFCArgs);
      subInFSCColExp.addElement(subInFC);
      subInFSC.setColumnExpression(subInFSCColExp);
      this.functionName.setColumnName("");
      Vector fnArgs = new Vector();
      fnArgs.addElement(subInFSC);
      this.setFunctionArguments(fnArgs);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int argLength = this.functionArguments.size();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < argLength; ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String arg1 = arguments.get(0).toString();
      arguments = new Vector();
      arguments.addElement("string_agg(CAST(" + arg1 + " as TEXT), '#@#')");
      arguments.addElement("'#@#'");
      arguments.addElement(this.functionName.toString().equalsIgnoreCase("group_first") ? "1" : "-1");
      this.functionName.setColumnName("substring_index");
      this.setFunctionArguments(arguments);
   }
}
