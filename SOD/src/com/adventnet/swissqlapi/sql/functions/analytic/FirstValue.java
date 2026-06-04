package com.adventnet.swissqlapi.sql.functions.analytic;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.GroupByStatement;
import com.adventnet.swissqlapi.sql.statement.select.HavingStatement;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.OrderItem;
import com.adventnet.swissqlapi.sql.statement.select.QueryPartitionClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.ArrayList;
import java.util.Vector;

public class FirstValue extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      from_sqs.setOlapFunctionPresent(true);
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn newArgSelCol = new SelectColumn();
      SelectColumn arg = (SelectColumn)this.getFunctionArguments().get(0);
      newArgSelCol.setColumnExpression(arg.getColumnExpression());
      newArgSelCol.setIgnoreNulls(arg.getIgnoreNulls());
      String alias = from_sqs.getFromClause().getLastElement().getAliasName();
      String idx = "" + from_sqs.getOlapDerivedTables().size();
      String partitionString;
      FromTable derivedTable;
      Vector existingSelectItems;
      int siz;
      SelectColumn selCol;
      if (this.getPartitionByClause() != null) {
         partitionString = "first_value" + this.getPartitionByClause().toString() + this.obs;
         if (from_sqs.getOlapDerivedTables().containsKey(partitionString)) {
            derivedTable = (FromTable)from_sqs.getOlapDerivedTables().get(partitionString);
            existingSelectItems = ((SelectQueryStatement)derivedTable.getTableName()).getSelectStatement().getSelectItemList();
            siz = existingSelectItems.size();
            newArgSelCol.setAliasName("arg_" + siz);
            selCol = (SelectColumn)existingSelectItems.get(siz - 1);
            if (selCol.getEndsWith() == null) {
               selCol.setEndsWith(",");
            }

            existingSelectItems.add(newArgSelCol);
            alias = derivedTable.getAliasName();
            idx = "" + (from_sqs.getOlapDerivedTables().size() - 1);
         } else {
            newArgSelCol.setAliasName("arg_0");
            newArgSelCol.setEndsWith(",");
            from_sqs.addOlapDerivedTables(partitionString, this.createTeradataDerivedTable(to_sqs, this, newArgSelCol, alias + idx));
            alias = alias + idx;
         }
      } else {
         partitionString = "first_value" + this.obs;
         if (from_sqs.getOlapDerivedTables().containsKey(partitionString)) {
            derivedTable = (FromTable)from_sqs.getOlapDerivedTables().get(partitionString);
            existingSelectItems = ((SelectQueryStatement)derivedTable.getTableName()).getSelectStatement().getSelectItemList();
            siz = existingSelectItems.size();
            newArgSelCol.setAliasName("arg_" + siz);
            selCol = (SelectColumn)existingSelectItems.get(siz - 1);
            if (selCol.getEndsWith() == null) {
               selCol.setEndsWith(",");
            }

            existingSelectItems.add(newArgSelCol);
            alias = derivedTable.getAliasName();
            idx = "" + (from_sqs.getOlapDerivedTables().size() - 1);
         } else {
            newArgSelCol.setAliasName("arg_0");
            from_sqs.addOlapDerivedTables(partitionString, this.createTeradataDerivedTable(to_sqs, this, newArgSelCol, alias + idx));
            alias = alias + idx;
         }
      }

      TableColumn newTabCol = new TableColumn();
      newTabCol.setTableName(alias);
      newTabCol.setColumnName(newArgSelCol.getAliasName());
      this.setFunctionName(newTabCol);
      this.getFunctionArguments().clear();
      this.setOpenBracesForFunctionNameRequired(false);
      this.setPartitionByClause((QueryPartitionClause)null);
      this.setOver((String)null);
      this.setOrderBy((OrderByStatement)null);
   }

   public FromTable createTeradataDerivedTable(SelectQueryStatement to_sqs, FunctionCalls fnCall, SelectColumn functionArgument, String alias) throws ConvertException {
      String fnName = fnCall.getFunctionName().getColumnName();
      SelectQueryStatement derivedTable = new SelectQueryStatement();
      SelectStatement selStmt = new SelectStatement();
      selStmt.setSelectClause("SELECT");
      Vector newSelColList = new Vector();
      Vector newWhereItemList = new Vector();
      HavingStatement qualifyStmt = new HavingStatement();
      qualifyStmt.setHavingClause("QUALIFY");
      WhereExpression qualifyExpression = new WhereExpression();
      Vector qualifyItems = new Vector();
      SelectColumn rownumCol = this.getNewSelectColumnForDerivedTable(fnCall, 0);
      newSelColList.add(rownumCol);
      WhereItem wi = new WhereItem();
      WhereColumn lwc = new WhereColumn();
      Vector lwcColExp = new Vector();
      TableColumn tc1 = new TableColumn();
      tc1.setColumnName(rownumCol.getAliasName());
      tc1.setTableName(alias);
      lwcColExp.add(tc1);
      WhereColumn rwc = new WhereColumn();
      Vector rwcColExp = new Vector();
      rwcColExp.add("1");
      lwc.setColumnExpression(lwcColExp);
      rwc.setColumnExpression(rwcColExp);
      wi.setLeftWhereExp(lwc);
      wi.setRightWhereExp(rwc);
      wi.setOperator("=");
      WhereItem qualifyWi = new WhereItem();
      WhereColumn qualifyWilwc = new WhereColumn();
      Vector qualifyWilwcColExp = new Vector();
      TableColumn qualifyWitc1 = new TableColumn();
      qualifyWitc1.setColumnName(rownumCol.getAliasName());
      qualifyWilwcColExp.add(qualifyWitc1);
      WhereColumn qualifyWirwc = new WhereColumn();
      Vector qualifyWirwcColExp = new Vector();
      qualifyWirwcColExp.add("1");
      qualifyWilwc.setColumnExpression(qualifyWilwcColExp);
      qualifyWirwc.setColumnExpression(qualifyWirwcColExp);
      qualifyWi.setLeftWhereExp(qualifyWilwc);
      qualifyWi.setRightWhereExp(qualifyWirwc);
      qualifyWi.setOperator("=");
      qualifyExpression.addWhereItem(qualifyWi);
      qualifyItems.add(qualifyExpression);
      if (functionArgument != null) {
         newSelColList.add(functionArgument);
      }

      SelectColumn gbsSC;
      if (fnCall.getPartitionByClause() != null) {
         ArrayList selColsList = fnCall.getPartitionByClause().getSelectColumnList();

         for(int k = 0; k < selColsList.size(); ++k) {
            if (selColsList.get(k) instanceof SelectColumn) {
               SelectColumn partSelCol = (SelectColumn)selColsList.get(k);
               gbsSC = new SelectColumn();
               gbsSC.setColumnExpression(partSelCol.getColumnExpression());
               gbsSC.setAliasName("partition_" + k);
               gbsSC.setEndsWith(",");
               newWhereItemList.add(this.generateWhereItems(partSelCol, alias, "partition_" + k));
               newSelColList.add(gbsSC);
            }
         }
      } else {
         newWhereItemList.add(wi);
      }

      Vector groupByItems;
      int k;
      if (fnName.equalsIgnoreCase("dense_rank") && fnCall.getOrderBy() != null) {
         OrderByStatement obs = fnCall.getOrderBy();
         groupByItems = obs.getOrderItemList();

         for(k = 0; k < groupByItems.size(); ++k) {
            OrderItem oi = (OrderItem)groupByItems.elementAt(k);
            if (oi != null) {
               SelectColumn orderSelCol = oi.getOrderSpecifier();
               SelectColumn newOrderSelCol = new SelectColumn();
               newOrderSelCol.setColumnExpression(orderSelCol.getColumnExpression());
               newOrderSelCol.setAliasName("order_" + k);
               newOrderSelCol.setEndsWith(",");
               newWhereItemList.add(this.generateWhereItems(orderSelCol, alias, "order_" + k));
               newSelColList.add(newOrderSelCol);
            }
         }
      }

      ((SelectColumn)newSelColList.lastElement()).setEndsWith((String)null);
      selStmt.setSelectItemList(newSelColList);
      derivedTable.setSelectStatement(selStmt);
      qualifyStmt.setHavingItems(qualifyItems);
      if (!fnName.equalsIgnoreCase("dense_rank") && !fnName.equalsIgnoreCase("count")) {
         derivedTable.setHavingStatement(qualifyStmt);
      } else {
         GroupByStatement gbs = new GroupByStatement();
         gbs.setGroupClause("GROUP BY");
         groupByItems = new Vector();

         for(k = 1; k < newSelColList.size(); ++k) {
            gbsSC = new SelectColumn();
            Vector gbsSCExp = new Vector();
            gbsSCExp.add("" + (k + 1));
            gbsSC.setColumnExpression(gbsSCExp);
            groupByItems.add(gbsSC);
         }

         gbs.setGroupByItemList(groupByItems);
         derivedTable.setGroupByStatement(gbs);
      }

      WhereExpression weForDerivedTable = new WhereExpression();
      groupByItems = new Vector();
      WhereItem newWI = new WhereItem();
      WhereColumn newWC = new WhereColumn();
      if (functionArgument.getIgnoreNulls() != null) {
         newWI.setOperator("IS NOT NULL");
         newWC.setColumnExpression(functionArgument.getColumnExpression());
         newWI.setLeftWhereExp(newWC);
         groupByItems.add(newWI);
         weForDerivedTable.setWhereItem(groupByItems);
         derivedTable.setWhereExpression(weForDerivedTable);
      }

      FromTable derivedTableFromItem = new FromTable();
      derivedTableFromItem.setTableName(derivedTable);
      derivedTableFromItem.setAliasName(alias);
      derivedTableFromItem.setJoinClause("INNER JOIN ");
      derivedTableFromItem.setOnOrUsingJoin("ON");
      Vector joinCondition = new Vector();
      WhereExpression we = new WhereExpression();
      we.setWhereItem(newWhereItemList);
      Vector operators = new Vector();

      for(int s = 0; s < newWhereItemList.size() - 1; ++s) {
         operators.add("AND");
      }

      we.setOperator(operators);
      joinCondition.add(we);
      derivedTableFromItem.setJoinExpression(joinCondition);
      return derivedTableFromItem;
   }

   private SelectColumn getNewSelectColumnForDerivedTable(FunctionCalls fnCall, int argIndex) throws ConvertException {
      SelectColumn rownumSelCol = new SelectColumn();
      String origFnName = fnCall.getFunctionName().getColumnName();
      FunctionCalls rownumFunc = new FunctionCalls();
      TableColumn rownumFuncName = new TableColumn();
      rownumFuncName.setColumnName("ROW_NUMBER");
      rownumFunc.setFunctionName(rownumFuncName);
      rownumFunc.setFunctionArguments(new Vector());
      if (fnCall.getPartitionByClause() != null) {
         rownumFunc.setPartitionByClause(fnCall.getPartitionByClause());
      }

      OrderByStatement partitionObs;
      Vector orderItemList;
      int i_count;
      OrderItem oi;
      if (fnCall.getOrderBy() != null) {
         partitionObs = fnCall.getOrderBy();
         if (origFnName.equalsIgnoreCase("first_value")) {
            orderItemList = partitionObs.getOrderItemList();

            for(i_count = 0; i_count < orderItemList.size(); ++i_count) {
               oi = (OrderItem)orderItemList.elementAt(i_count);
               if (oi != null) {
                  String orderType = oi.getOrder();
                  if (orderType != null && orderType.equalsIgnoreCase("ASC")) {
                     oi.setOrder("ASC");
                  }

                  if (orderType != null && orderType.equalsIgnoreCase("DESC")) {
                     oi.setOrder("DESC");
                  } else if (orderType == null) {
                     oi.setOrder("ASC");
                  }
               }
            }
         }

         rownumFunc.setOrderBy(partitionObs);
      } else {
         partitionObs = new OrderByStatement();
         partitionObs.setOrderClause("ORDER BY");
         orderItemList = new Vector();

         for(i_count = 0; i_count < fnCall.getPartitionByClause().getSelectColumnList().size(); ++i_count) {
            if (fnCall.getPartitionByClause().getSelectColumnList().get(i_count) instanceof SelectColumn) {
               oi = new OrderItem();
               oi.setOrderSpecifier((SelectColumn)fnCall.getPartitionByClause().getSelectColumnList().get(i_count));
               orderItemList.add(oi);
            }
         }

         partitionObs.setOrderItemList(orderItemList);
         rownumFunc.setOrderBy(partitionObs);
      }

      rownumFunc.setOver("OVER");
      Vector rownumSelColExp = new Vector();
      rownumSelColExp.add(rownumFunc);
      rownumSelCol.setColumnExpression(rownumSelColExp);
      if (origFnName.equalsIgnoreCase("dense_rank")) {
         rownumSelCol.setAliasName("rnk");
      } else {
         rownumSelCol.setAliasName("rownum_" + argIndex);
      }

      rownumSelCol.setEndsWith(",");
      return rownumSelCol;
   }

   private WhereItem generateWhereItems(SelectColumn selCol, String derivedTableAlias, String derivedTableColumn) throws ConvertException {
      WhereItem wi = new WhereItem();
      WhereColumn lwc = new WhereColumn();
      Vector lwcColExp = new Vector();
      lwcColExp.add(selCol);
      WhereColumn rwc = new WhereColumn();
      Vector rwcColExp = new Vector();
      if (selCol != null) {
         TableColumn rsc = new TableColumn();
         rsc.setTableName(derivedTableAlias);
         rsc.setColumnName(derivedTableColumn);
         rwcColExp.add(rsc);
      }

      lwc.setColumnExpression(lwcColExp);
      rwc.setColumnExpression(rwcColExp);
      wi.setLeftWhereExp(lwc);
      wi.setRightWhereExp(rwc);
      wi.setOperator("=");
      return wi;
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("first_value");
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
