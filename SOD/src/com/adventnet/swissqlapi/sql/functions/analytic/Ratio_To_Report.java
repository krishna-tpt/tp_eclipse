package com.adventnet.swissqlapi.sql.functions.analytic;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.QueryPartitionClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class Ratio_To_Report extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn funcArg = ((SelectColumn)this.functionArguments.get(0)).toTeradataSelect(to_sqs, from_sqs);
      FunctionCalls sumFunc = new FunctionCalls();
      TableColumn sumFuncName = new TableColumn();
      sumFuncName.setColumnName("SUM");
      sumFunc.setFunctionName(sumFuncName);
      SelectColumn sumFuncCol = new SelectColumn();
      Vector sumFuncArgExp = new Vector();
      sumFuncArgExp.add(funcArg);
      sumFuncCol.setColumnExpression(sumFuncArgExp);
      Vector sumFuncArgs = new Vector();
      sumFuncArgs.add(sumFuncCol);
      sumFunc.setFunctionArguments(sumFuncArgs);
      sumFunc.setOver("OVER");
      new QueryPartitionClause();
      if (this.getPartitionByClause() != null) {
         QueryPartitionClause sumFuncPart = this.getPartitionByClause().toTeradataSelect(to_sqs, from_sqs);
         sumFunc.setPartitionByClause(sumFuncPart);
      }

      SelectColumn newArg = new SelectColumn();
      Vector newArgExp = new Vector();
      newArgExp.add("(");
      newArgExp.add(funcArg);
      newArgExp.add("/");
      newArgExp.add(sumFunc);
      newArg.setColumnExpression(newArgExp);
      this.setFunctionName((TableColumn)null);
      Vector arguments = new Vector();
      arguments.add(newArg);
      this.setFunctionArguments(arguments);
      this.setOver((String)null);
      this.setOrderBy((OrderByStatement)null);
      this.setPartitionByClause((QueryPartitionClause)null);
   }
}
