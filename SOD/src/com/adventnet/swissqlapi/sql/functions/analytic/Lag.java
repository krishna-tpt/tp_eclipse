package com.adventnet.swissqlapi.sql.functions.analytic;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.QueryPartitionClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WindowingClause;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.Vector;

public class Lag extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MIN");
      String and = "AND";
      String between = "BETWEEN";
      String preceding = "PRECEDING";
      String rows = "ROWS";
      String defaultValue = null;
      SelectColumn defaultValueColumn = null;
      SelectColumn firstWindowExp = null;
      SelectColumn secondWindowExp = null;
      WindowingClause windowingClause = new WindowingClause();
      WindowingClause firstWindowingClause = new WindowingClause();
      WindowingClause secondWindowingClause = new WindowingClause();
      Vector newFunctionArgs = new Vector();
      String offset = null;
      String lag_Default = null;

      Vector colExp;
      for(int count = 0; count < this.functionArguments.size(); ++count) {
         Object obj = this.functionArguments.get(count);
         if (obj instanceof SelectColumn) {
            if (count == 0) {
               newFunctionArgs.add(((SelectColumn)this.functionArguments.get(count)).toTeradataSelect(to_sqs, from_sqs));
               if (this.functionArguments.size() == 1) {
                  firstWindowExp = new SelectColumn();
                  colExp = new Vector();
                  colExp.add("1");
                  firstWindowExp.setColumnExpression(colExp);
               }
            } else if (count == 1) {
               firstWindowExp = ((SelectColumn)this.functionArguments.get(count)).toTeradataSelect(to_sqs, from_sqs);
            } else if (count == 2) {
               secondWindowExp = ((SelectColumn)this.functionArguments.get(count)).toTeradataSelect(to_sqs, from_sqs);
               if (secondWindowExp != null) {
                  colExp = secondWindowExp.getColumnExpression();
                  defaultValue = colExp.get(0).toString();
                  defaultValueColumn = (SelectColumn)this.functionArguments.get(count);
               }
            }
         }
      }

      firstWindowingClause.setPreceding(preceding);
      firstWindowingClause.setWindowExpr(firstWindowExp);
      secondWindowingClause.setPreceding(preceding);
      secondWindowingClause.setWindowExpr(firstWindowExp);
      windowingClause.setAnd(and);
      windowingClause.setBetween(between);
      windowingClause.setRowsOrRange(rows);
      windowingClause.setFirstWindow(firstWindowingClause);
      windowingClause.setSecondWindow(secondWindowingClause);
      this.setFunctionArguments(newFunctionArgs);
      this.setWindowingClause(windowingClause);
      if (defaultValue != null && defaultValue.trim().equals("0")) {
         new FunctionCalls();
         new TableColumn();
         colExp = new Vector();
         SelectColumn scForFirstArgument = new SelectColumn();
         Vector colExpForFirstArgument = new Vector();
         SelectColumn scForSecondArgument = new SelectColumn();
         Vector colExpForSecondArgument = new Vector();
         colExpForFirstArgument.add(this.toTeradataSelect(to_sqs, from_sqs));
         scForFirstArgument.setColumnExpression(colExpForFirstArgument);
         colExpForSecondArgument.add("0");
         scForSecondArgument.setColumnExpression(colExpForSecondArgument);
         colExp.add(scForFirstArgument);
         colExp.add(scForSecondArgument);
         this.setFunctionArguments(colExp);
         this.setOver((String)null);
         this.setOrderBy((OrderByStatement)null);
         this.setPartitionByClause((QueryPartitionClause)null);
         this.setWindowingClause((WindowingClause)null);
         this.functionName.setColumnName("COALESCE");
      } else if (defaultValue != null && !defaultValue.trim().equalsIgnoreCase("null")) {
         boolean isDateArg = false;
         if (defaultValueColumn != null) {
            if (defaultValueColumn.getColumnExpression().get(0) instanceof FunctionCalls) {
               FunctionCalls dateFunc = (FunctionCalls)defaultValueColumn.getColumnExpression().get(0);
               if (dateFunc.getFunctionName() != null && SwisSQLUtils.getFunctionReturnType(dateFunc.getFunctionName().getColumnName(), dateFunc.getFunctionArguments()).equalsIgnoreCase("date")) {
                  isDateArg = true;
               }
            } else if (defaultValueColumn.getColumnExpression().get(0) instanceof TableColumn) {
               TableColumn dateFunc = (TableColumn)defaultValueColumn.getColumnExpression().get(0);
               if (SwisSQLUtils.getFunctionReturnType(dateFunc.getColumnName(), (Vector)null).equalsIgnoreCase("date")) {
                  isDateArg = true;
               }
            }
         }

         if (isDateArg) {
            new FunctionCalls();
            new TableColumn();
            Vector newFunctionArguments = new Vector();
            SelectColumn scForFirstArgument = new SelectColumn();
            Vector colExpForFirstArgument = new Vector();
            SelectColumn scForSecondArgument = new SelectColumn();
            Vector colExpForSecondArgument = new Vector();
            colExpForFirstArgument.add(this.toTeradataSelect(to_sqs, from_sqs));
            scForFirstArgument.setColumnExpression(colExpForFirstArgument);
            colExpForSecondArgument.add(defaultValueColumn.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            scForSecondArgument.setColumnExpression(colExpForSecondArgument);
            newFunctionArguments.add(scForFirstArgument);
            newFunctionArguments.add(scForSecondArgument);
            this.setFunctionArguments(newFunctionArguments);
            this.setOver((String)null);
            this.setOrderBy((OrderByStatement)null);
            this.setPartitionByClause((QueryPartitionClause)null);
            this.setWindowingClause((WindowingClause)null);
            this.functionName.setColumnName("COALESCE");
            Vector newArguments = new Vector();

            for(int k = 0; k < this.functionArguments.size(); ++k) {
               FunctionCalls castTimestamp = new FunctionCalls();
               TableColumn castTcn = new TableColumn();
               castTcn.setColumnName("CAST");
               castTimestamp.setFunctionName(castTcn);
               castTimestamp.setAsDatatype("AS");
               DateClass castDatatype = new DateClass();
               castDatatype.setDatatypeName("TIMESTAMP");
               castDatatype.setOpenBrace("(");
               castDatatype.setSize("0");
               castDatatype.setClosedBrace(")");
               Vector castTimestampArgs = new Vector();
               castTimestampArgs.add(this.functionArguments.get(k));
               castTimestampArgs.add(castDatatype);
               castTimestamp.setFunctionArguments(castTimestampArgs);
               newArguments.add(castTimestamp);
            }

            this.setFunctionArguments(newArguments);
         }
      }

   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
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
