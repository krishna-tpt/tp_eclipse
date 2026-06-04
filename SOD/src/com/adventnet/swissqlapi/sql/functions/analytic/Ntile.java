package com.adventnet.swissqlapi.sql.functions.analytic;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.QueryPartitionClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class Ntile extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn ntileArg = ((SelectColumn)this.functionArguments.get(0)).toTeradataSelect(to_sqs, from_sqs);
      FunctionCalls countFunc = new FunctionCalls();
      TableColumn countFuncName = new TableColumn();
      countFuncName.setColumnName("COUNT");
      countFunc.setFunctionName(countFuncName);
      SelectColumn countFuncCol = new SelectColumn();
      Vector countFuncArgExp = new Vector();
      countFuncArgExp.add("*");
      countFuncCol.setColumnExpression(countFuncArgExp);
      Vector countFuncArgs = new Vector();
      countFuncArgs.add(countFuncCol);
      countFunc.setFunctionArguments(countFuncArgs);
      countFunc.setOver("OVER");
      QueryPartitionClause countFuncPart = new QueryPartitionClause();
      if (this.getPartitionByClause() != null) {
         countFuncPart = this.getPartitionByClause().toTeradataSelect(to_sqs, from_sqs);
         countFunc.setPartitionByClause(countFuncPart);
      }

      FunctionCalls rankFunc = new FunctionCalls();
      TableColumn rankFuncName = new TableColumn();
      rankFuncName.setColumnName("RANK");
      rankFunc.setFunctionName(rankFuncName);
      rankFunc.setOver("OVER");
      rankFunc.setPartitionByClause(countFuncPart);
      rankFunc.setOrderBy(this.getOrderBy().toTeradataSelect(to_sqs, from_sqs));
      SelectColumn newArg = new SelectColumn();
      Vector newArgExp = new Vector();
      newArgExp.add("(");
      newArgExp.add("(");
      newArgExp.add(rankFunc);
      newArgExp.add("-");
      newArgExp.add("1");
      newArgExp.add(")");
      newArgExp.add("*");
      newArgExp.add(ntileArg);
      newArgExp.add("/");
      newArgExp.add(countFunc);
      newArgExp.add("+");
      newArgExp.add("1");
      newArg.setColumnExpression(newArgExp);
      this.setFunctionName((TableColumn)null);
      Vector arguments = new Vector();
      arguments.add(newArg);
      this.setFunctionArguments(arguments);
      this.setOver((String)null);
      this.setOrderBy((OrderByStatement)null);
      this.setPartitionByClause((QueryPartitionClause)null);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("NTILE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }
}
