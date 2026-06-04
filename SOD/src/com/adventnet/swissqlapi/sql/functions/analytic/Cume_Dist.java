package com.adventnet.swissqlapi.sql.functions.analytic;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.OrderItem;
import com.adventnet.swissqlapi.sql.statement.select.QueryPartitionClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhenStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class Cume_Dist extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector lweExp;
      Vector rweExp;
      Vector whereItemList;
      if (this.getWithinGroup() != null) {
         if (this.functionArguments.size() > 1 || this.getOrderBy().getOrderItemList().size() > 1) {
            return;
         }

         String caseOper = "<=";
         SelectColumn funcArg = (SelectColumn)this.functionArguments.firstElement();
         funcArg = funcArg.toTeradataSelect(to_sqs, from_sqs);
         OrderItem orderByArg = (OrderItem)this.getOrderBy().getOrderItemList().firstElement();
         orderByArg = orderByArg.toTeradataSelect(to_sqs, from_sqs);
         SelectColumn orderByCol = orderByArg.getOrderSpecifier();
         if (orderByArg.getOrder() != null && orderByArg.getOrder().equalsIgnoreCase("DESC")) {
            caseOper = ">=";
         }

         CaseStatement caseStmt = new CaseStatement();
         caseStmt.setCaseClause("CASE");
         Vector whenStmtList = new Vector();
         WhenStatement whenStmt = new WhenStatement();
         whenStmt.setWhenClause("WHEN");
         WhereExpression whereExp = new WhereExpression();
         WhereItem wi = new WhereItem();
         WhereColumn lwe = new WhereColumn();
         lweExp = new Vector();
         lweExp.add(orderByCol);
         lwe.setColumnExpression(lweExp);
         wi.setLeftWhereExp(lwe);
         wi.setOperator(caseOper);
         WhereColumn rwe = new WhereColumn();
         rweExp = new Vector();
         rweExp.add(funcArg);
         rwe.setColumnExpression(rweExp);
         wi.setRightWhereExp(rwe);
         whereItemList = new Vector();
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
         SelectColumn countFuncCol = new SelectColumn();
         Vector countFuncArgExp = new Vector();
         countFuncArgExp.add(caseStmt);
         countFuncCol.setColumnExpression(countFuncArgExp);
         Vector countFuncArgs = new Vector();
         countFuncArgs.add(countFuncCol);
         countFunc.setFunctionArguments(countFuncArgs);
         FunctionCalls castFunc = new FunctionCalls();
         TableColumn castName = new TableColumn();
         castName.setColumnName("CAST");
         castFunc.setFunctionName(castName);
         Vector castFuncArgs = new Vector();
         castFuncArgs.add(countFunc);
         castFuncArgs.add("DECIMAL(8,6)");
         castFunc.setAsDatatype("AS");
         castFunc.setFunctionArguments(castFuncArgs);
         FunctionCalls countAllFunc = new FunctionCalls();
         TableColumn countAllFuncName = new TableColumn();
         countAllFuncName.setColumnName("COUNT");
         countAllFunc.setFunctionName(countAllFuncName);
         SelectColumn countAllFuncCol = new SelectColumn();
         Vector countAllFuncArgExp = new Vector();
         countAllFuncArgExp.add("*");
         countAllFuncCol.setColumnExpression(countAllFuncArgExp);
         Vector countAllFuncArgs = new Vector();
         countAllFuncArgs.add(countAllFuncCol);
         countAllFunc.setFunctionArguments(countAllFuncArgs);
         SelectColumn newArg = new SelectColumn();
         Vector newArgExp = new Vector();
         newArgExp.add("(");
         newArgExp.add("(");
         newArgExp.add(castFunc);
         newArgExp.add("+");
         newArgExp.add("1");
         newArgExp.add(")");
         newArgExp.add("/");
         newArgExp.add("(");
         newArgExp.add(countAllFunc);
         newArgExp.add("+");
         newArgExp.add("1");
         newArgExp.add(")");
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
         FunctionCalls castFunc = new FunctionCalls();
         TableColumn castName = new TableColumn();
         castName.setColumnName("CAST");
         castFunc.setFunctionName(castName);
         lweExp = new Vector();
         lweExp.add(rankFunc);
         lweExp.add("DECIMAL(8,6)");
         castFunc.setAsDatatype("AS");
         castFunc.setFunctionArguments(lweExp);
         SelectColumn newArg = new SelectColumn();
         rweExp = new Vector();
         rweExp.add("(");
         rweExp.add(castFunc);
         rweExp.add("/");
         rweExp.add(countFunc);
         newArg.setColumnExpression(rweExp);
         this.setFunctionName((TableColumn)null);
         whereItemList = new Vector();
         whereItemList.add(newArg);
         this.setFunctionArguments(whereItemList);
         this.setOver((String)null);
         this.setOrderBy((OrderByStatement)null);
         this.setPartitionByClause((QueryPartitionClause)null);
      }

   }
}
