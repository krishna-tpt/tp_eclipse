package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class trigh extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.getColumnName().toUpperCase();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fname = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn argument;
      Object x;
      Vector arg;
      FunctionCalls exp1;
      FunctionCalls exp2;
      TableColumn innerFunction;
      Vector argList1;
      Vector argList2;
      TableColumn outerFunction;
      if (fname.equalsIgnoreCase("sinh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("cosh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("tanh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         arg.add(argument);
         this.setFunctionArguments(arg);
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fname = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn argument;
      Object x;
      Vector arg;
      FunctionCalls exp1;
      FunctionCalls exp2;
      TableColumn innerFunction;
      Vector argList1;
      Vector argList2;
      TableColumn outerFunction;
      if (fname.equalsIgnoreCase("sinh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("cosh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("tanh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         arg.add(argument);
         this.setFunctionArguments(arg);
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fname = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn argument;
      Object x;
      Vector arg;
      FunctionCalls exp1;
      FunctionCalls exp2;
      TableColumn innerFunction;
      Vector argList1;
      Vector argList2;
      TableColumn outerFunction;
      if (fname.equalsIgnoreCase("sinh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("cosh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("tanh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         arg.add(argument);
         this.setFunctionArguments(arg);
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fname = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn argument;
      Object x;
      Vector arg;
      FunctionCalls exp1;
      FunctionCalls exp2;
      TableColumn innerFunction;
      Vector argList1;
      Vector argList2;
      TableColumn outerFunction;
      if (fname.equalsIgnoreCase("sinh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("cosh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("tanh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         arg.add(argument);
         this.setFunctionArguments(arg);
      }

   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fname = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn argument;
      Object x;
      Vector arg;
      FunctionCalls exp1;
      FunctionCalls exp2;
      TableColumn innerFunction;
      Vector argList1;
      Vector argList2;
      TableColumn outerFunction;
      if (fname.equalsIgnoreCase("sinh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("cosh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("tanh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         arg.add(argument);
         this.setFunctionArguments(arg);
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fname = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn argument;
      Object x;
      Vector arg;
      FunctionCalls exp1;
      FunctionCalls exp2;
      TableColumn innerFunction;
      Vector argList1;
      Vector argList2;
      TableColumn outerFunction;
      if (fname.equalsIgnoreCase("sinh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("cosh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("tanh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         arg.add(argument);
         this.setFunctionArguments(arg);
      }

   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fname = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fname = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fname = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn argument;
      Object x;
      Vector arg;
      FunctionCalls exp1;
      FunctionCalls exp2;
      TableColumn innerFunction;
      Vector argList1;
      Vector argList2;
      TableColumn outerFunction;
      if (fname.equalsIgnoreCase("sinh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("cosh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("tanh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         arg.add(argument);
         this.setFunctionArguments(arg);
      }

   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fname = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn argument;
      Object x;
      Vector arg;
      FunctionCalls exp1;
      FunctionCalls exp2;
      TableColumn innerFunction;
      Vector argList1;
      Vector argList2;
      TableColumn outerFunction;
      if (fname.equalsIgnoreCase("sinh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("cosh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("2");
         arg.add(argument);
         this.setFunctionArguments(arg);
      } else if (fname.equalsIgnoreCase("tanh")) {
         outerFunction = new TableColumn();
         outerFunction.setOwnerName(this.functionName.getOwnerName());
         outerFunction.setTableName(this.functionName.getTableName());
         outerFunction.setColumnName("");
         this.setFunctionName(outerFunction);
         argument = new SelectColumn();
         x = this.functionArguments.get(0);
         arg = new Vector();
         exp1 = new FunctionCalls();
         exp2 = new FunctionCalls();
         innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("EXP");
         exp1.setFunctionName(innerFunction);
         exp2.setFunctionName(innerFunction);
         argList1 = new Vector();
         argList2 = new Vector();
         argList1.add(x);
         argList2.add("-(" + x + ")");
         exp1.setFunctionArguments(argList1);
         exp2.setFunctionArguments(argList2);
         argument.setColumnExpression(new Vector());
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("-");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         argument.addColumnExpressionElement("/");
         argument.addColumnExpressionElement("(");
         argument.addColumnExpressionElement(exp1);
         argument.addColumnExpressionElement("+");
         argument.addColumnExpressionElement(exp2);
         argument.addColumnExpressionElement(")");
         arg.add(argument);
         this.setFunctionArguments(arg);
      }

   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("Given function " + this.functionName.getColumnName() + "is not supported");
   }
}
