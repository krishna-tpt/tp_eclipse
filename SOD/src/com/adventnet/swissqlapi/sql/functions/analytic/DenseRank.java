package com.adventnet.swissqlapi.sql.functions.analytic;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
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
import com.adventnet.swissqlapi.sql.statement.select.WhenStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.ArrayList;
import java.util.Vector;

public class DenseRank extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String alias;
      if (this.getWithinGroup() != null) {
         if (this.functionArguments.size() > 1 || this.getOrderBy().getOrderItemList().size() > 1) {
            return;
         }

         alias = "<";
         SelectColumn funcArg = (SelectColumn)this.functionArguments.firstElement();
         funcArg = funcArg.toTeradataSelect(to_sqs, from_sqs);
         OrderItem orderByArg = (OrderItem)this.getOrderBy().getOrderItemList().firstElement();
         orderByArg = orderByArg.toTeradataSelect(to_sqs, from_sqs);
         SelectColumn orderByCol = orderByArg.getOrderSpecifier();
         if (orderByArg.getOrder() != null && orderByArg.getOrder().equalsIgnoreCase("DESC")) {
            alias = ">";
         }

         CaseStatement caseStmt = new CaseStatement();
         caseStmt.setCaseClause("CASE");
         Vector whenStmtList = new Vector();
         WhenStatement whenStmt = new WhenStatement();
         whenStmt.setWhenClause("WHEN");
         WhereExpression whereExp = new WhereExpression();
         WhereItem wi = new WhereItem();
         WhereColumn lwe = new WhereColumn();
         Vector lweExp = new Vector();
         lweExp.add(orderByCol);
         lwe.setColumnExpression(lweExp);
         wi.setLeftWhereExp(lwe);
         wi.setOperator(alias);
         WhereColumn rwe = new WhereColumn();
         Vector rweExp = new Vector();
         rweExp.add(funcArg);
         rwe.setColumnExpression(rweExp);
         wi.setRightWhereExp(rwe);
         Vector whereItemList = new Vector();
         whereItemList.add(wi);
         whereExp.setWhereItem(whereItemList);
         whenStmt.setWhenCondition(whereExp);
         whenStmt.setThenClause("THEN");
         whenStmt.setThenStatement(orderByCol);
         whenStmtList.add(whenStmt);
         caseStmt.setWhenStatementList(whenStmtList);
         caseStmt.setElseClause("ELSE");
         SelectColumn elseCol = new SelectColumn();
         Vector elseColExp = new Vector();
         elseColExp.add("NULL");
         elseCol.setColumnExpression(elseColExp);
         caseStmt.setElseStatement(elseCol);
         caseStmt.setEndClause("END");
         FunctionCalls countFunc = new FunctionCalls();
         TableColumn countFuncName = new TableColumn();
         countFuncName.setColumnName("COUNT");
         countFunc.setFunctionName(countFuncName);
         countFunc.setArgumentQualifier("DISTINCT");
         SelectColumn countFuncCol = new SelectColumn();
         Vector countFuncArgExp = new Vector();
         countFuncArgExp.add(caseStmt);
         countFuncCol.setColumnExpression(countFuncArgExp);
         Vector countFuncArgs = new Vector();
         countFuncArgs.add(countFuncCol);
         countFunc.setFunctionArguments(countFuncArgs);
         SelectColumn newArg = new SelectColumn();
         Vector newArgExp = new Vector();
         newArgExp.add("(");
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
         this.setWithinGroup((String)null);
      } else {
         from_sqs.setOlapFunctionPresent(true);
         alias = from_sqs.getFromClause().getLastElement().getAliasName();
         String idx = "" + from_sqs.getOlapDerivedTables().size();
         if (this.getPartitionByClause() != null) {
            String partitionString = "denserank" + this.getPartitionByClause().toString();
            if (from_sqs.getOlapDerivedTables().containsKey(partitionString)) {
               idx = "" + (from_sqs.getOlapDerivedTables().size() - 1);
            }

            from_sqs.addOlapDerivedTables(partitionString + this.obs, this.createTeradataDerivedTable(to_sqs, this, (SelectColumn)null, alias + idx));
         } else {
            if (from_sqs.getOlapDerivedTables().containsKey("denserank" + this.obs)) {
               idx = "" + (from_sqs.getOlapDerivedTables().size() - 1);
            }

            from_sqs.addOlapDerivedTables("denserank" + this.obs, this.createTeradataDerivedTable(to_sqs, this, (SelectColumn)null, alias + idx));
         }

         TableColumn newTabCol = new TableColumn();
         newTabCol.setTableName(alias + idx);
         newTabCol.setColumnName("rnk");
         this.setFunctionName(newTabCol);
         this.getFunctionArguments().clear();
         this.setOpenBracesForFunctionNameRequired(false);
         this.setPartitionByClause((QueryPartitionClause)null);
         this.setOver((String)null);
         this.setOrderBy((OrderByStatement)null);
      }

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
      lwcColExp.add(tc1);
      WhereColumn rwc = new WhereColumn();
      Vector rwcColExp = new Vector();
      rwcColExp.add("1");
      lwc.setColumnExpression(lwcColExp);
      rwc.setColumnExpression(rwcColExp);
      wi.setLeftWhereExp(lwc);
      wi.setRightWhereExp(rwc);
      wi.setOperator("=");
      qualifyExpression.addWhereItem(wi);
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

      Vector joinCondition;
      int k;
      if (fnName.equalsIgnoreCase("dense_rank") && fnCall.getOrderBy() != null) {
         OrderByStatement obs = fnCall.getOrderBy();
         joinCondition = obs.getOrderItemList();

         for(k = 0; k < joinCondition.size(); ++k) {
            OrderItem oi = (OrderItem)joinCondition.elementAt(k);
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
         joinCondition = new Vector();

         for(k = 1; k < newSelColList.size(); ++k) {
            gbsSC = new SelectColumn();
            Vector gbsSCExp = new Vector();
            gbsSCExp.add("" + (k + 1));
            gbsSC.setColumnExpression(gbsSCExp);
            joinCondition.add(gbsSC);
         }

         gbs.setGroupByItemList(joinCondition);
         derivedTable.setGroupByStatement(gbs);
      }

      FromTable derivedTableFromItem = new FromTable();
      derivedTableFromItem.setTableName(derivedTable);
      derivedTableFromItem.setAliasName(alias);
      derivedTableFromItem.setJoinClause("INNER JOIN ");
      derivedTableFromItem.setOnOrUsingJoin("ON");
      joinCondition = new Vector();
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
      FunctionCalls rownumFunc;
      if (origFnName.equalsIgnoreCase("count")) {
         rownumFunc = new FunctionCalls();
         rownumFunc.setFunctionName(fnCall.getFunctionName());
         rownumFunc.setFunctionArguments(fnCall.getFunctionArguments());
         rownumFunc.setArgumentQualifier(fnCall.getArgumentQualifier());
         Vector rownumSelColExp = new Vector();
         rownumSelColExp.add(rownumFunc);
         rownumSelCol.setColumnExpression(rownumSelColExp);
         rownumSelCol.setAliasName("cnt_" + argIndex);
      } else {
         rownumFunc = new FunctionCalls();
         TableColumn rownumFuncName = new TableColumn();
         rownumFuncName.setColumnName("ROW_NUMBER");
         rownumFunc.setFunctionName(rownumFuncName);
         rownumFunc.setFunctionArguments(new Vector());
         if (fnCall.getPartitionByClause() != null) {
            rownumFunc.setPartitionByClause(fnCall.getPartitionByClause());
         }

         if (fnCall.getOrderBy() != null) {
            OrderByStatement obs = fnCall.getOrderBy();
            if (origFnName.equalsIgnoreCase("last_value")) {
               Vector orderItemList = obs.getOrderItemList();

               for(int i_count = 0; i_count < orderItemList.size(); ++i_count) {
                  OrderItem oi = (OrderItem)orderItemList.elementAt(i_count);
                  if (oi != null) {
                     String orderType = oi.getOrder();
                     if (orderType != null && orderType.equalsIgnoreCase("ASC")) {
                        oi.setOrder("DESC");
                     }

                     if (orderType != null && orderType.equalsIgnoreCase("DESC")) {
                        oi.setOrder("ASC");
                     } else if (orderType == null) {
                        oi.setOrder("DESC");
                     }
                  }
               }
            }

            rownumFunc.setOrderBy(obs);
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
}
