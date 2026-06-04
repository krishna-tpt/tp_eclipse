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

public class Rank extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.getWithinGroup() != null) {
         if (this.functionArguments.size() > 1 || this.getOrderBy().getOrderItemList().size() > 1) {
            return;
         }

         String caseOper = "<";
         SelectColumn funcArg = (SelectColumn)this.functionArguments.firstElement();
         funcArg = funcArg.toTeradataSelect(to_sqs, from_sqs);
         OrderItem orderByArg = (OrderItem)this.getOrderBy().getOrderItemList().firstElement();
         orderByArg = orderByArg.toTeradataSelect(to_sqs, from_sqs);
         SelectColumn orderByCol = orderByArg.getOrderSpecifier();
         if (orderByArg.getOrder() != null && orderByArg.getOrder().equalsIgnoreCase("DESC")) {
            caseOper = ">";
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
         wi.setOperator(caseOper);
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
         SelectColumn countFuncCol = new SelectColumn();
         Vector countFuncArgExp = new Vector();
         countFuncArgExp.add(caseStmt);
         countFuncCol.setColumnExpression(countFuncArgExp);
         Vector countFuncArgs = new Vector();
         countFuncArgs.add(countFuncCol);
         countFunc.setFunctionArguments(countFuncArgs);
         SelectColumn newArg = new SelectColumn();
         Vector newArgExp = new Vector();
         if (this.functionName.getColumnName().equalsIgnoreCase("percent_rank")) {
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
            newArgExp.add("(");
            newArgExp.add("(");
            newArgExp.add(castFunc);
            newArgExp.add(")");
            newArgExp.add("/");
            newArgExp.add(countAllFunc);
         } else if (this.functionName.getColumnName().equalsIgnoreCase("rank")) {
            newArgExp.add("(");
            newArgExp.add(countFunc);
            newArgExp.add("+");
            newArgExp.add("1");
         }

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
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
         if (this.obs != null) {
            this.setOrderBy(this.obs.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         }

         if (this.getPartitionByClause() != null) {
            this.setPartitionByClause(this.getPartitionByClause().toTeradataSelect(to_sqs, from_sqs));
         }

         if (this.getWindowingClause() != null) {
            this.setWindowingClause(this.getWindowingClause().toTeradata(to_sqs, from_sqs));
         }
      }

   }
}
