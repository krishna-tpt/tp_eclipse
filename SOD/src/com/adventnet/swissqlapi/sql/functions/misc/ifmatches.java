package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhenStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class ifmatches extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();
      new Vector();
      if (this.functionArguments.size() % 2 == 1) {
         SelectColumn sc_null = new SelectColumn();
         Vector vc_null = new Vector();
         vc_null.addElement("null");
         sc_null.setColumnExpression(vc_null);
         this.functionArguments.addElement(sc_null);
      }

      arguments.addElement(this.ifStatements(to_sqs, from_sqs, 1));
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(arguments);
   }

   public SelectColumn ifStatements(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, int index) throws ConvertException {
      Vector vector = new Vector();
      int i_count = false;

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vector.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      int arg_len = this.functionArguments.size();
      SelectColumn sc_caseStmt = new SelectColumn();
      Vector sc_colexp = new Vector();
      CaseStatement casestmt = new CaseStatement();
      casestmt.setCaseClause("case");
      Vector whenItemList = new Vector();
      boolean canApplySwitchCase = false;

      int i;
      Vector colexp;
      for(i = 1; i < arg_len - 1; i += 2) {
         if (vector.get(i) instanceof SelectColumn) {
            colexp = ((SelectColumn)vector.get(i)).getColumnExpression();

            for(int j = 0; j < colexp.size(); ++j) {
               if (!(colexp.get(j) instanceof String)) {
                  canApplySwitchCase = false;
                  break;
               }

               canApplySwitchCase = true;
            }
         }

         if (!canApplySwitchCase) {
            break;
         }
      }

      Vector v_then;
      WhenStatement when_stmt;
      SelectColumn sc_then;
      if (canApplySwitchCase) {
         casestmt.setCaseCondition(this.createcondition(vector.get(0)));

         for(i = 1; i < arg_len - 1; i += 2) {
            when_stmt = new WhenStatement();
            when_stmt.setWhenClause("when");
            when_stmt.setWhenCondition(this.createcondition(vector.get(i)));
            when_stmt.setThenClause("then");
            sc_then = new SelectColumn();
            v_then = new Vector();
            v_then.addElement(vector.get(i + 1));
            sc_then.setColumnExpression(v_then);
            when_stmt.setThenStatement(sc_then);
            whenItemList.addElement(when_stmt);
         }

         casestmt.setWhenStatementList(whenItemList);
      } else {
         for(i = 1; i < arg_len - 1; i += 2) {
            when_stmt = new WhenStatement();
            when_stmt.setWhenClause("when");
            when_stmt.setWhenCondition(this.subFunctions(to_sqs, from_sqs, vector, i));
            when_stmt.setThenClause("then");
            sc_then = new SelectColumn();
            v_then = new Vector();
            v_then.addElement(vector.get(i + 1));
            sc_then.setColumnExpression(v_then);
            when_stmt.setThenStatement(sc_then);
            whenItemList.addElement(when_stmt);
         }

         casestmt.setWhenStatementList(whenItemList);
      }

      casestmt.setElseClause("else");
      SelectColumn case_elsestmt = new SelectColumn();
      colexp = new Vector();
      colexp.addElement(vector.get(vector.size() - 1));
      case_elsestmt.setColumnExpression(colexp);
      casestmt.setElseStatement(case_elsestmt);
      casestmt.setEndClause("end");
      sc_colexp.addElement(casestmt);
      sc_caseStmt.setColumnExpression(sc_colexp);
      return sc_caseStmt;
   }

   public WhereExpression subFunctions(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, Vector searchValue_vector, int index) throws ConvertException {
      Vector vector = new Vector();
      String operator = "=";
      String fnName = "";
      new FunctionCalls();
      new TableColumn();
      new Vector();
      SelectColumn sc_whExp = new SelectColumn();
      Vector vc_whExp = new Vector();
      WhereExpression whExp = new WhereExpression();
      if (searchValue_vector.elementAt(index) instanceof SelectColumn) {
         SelectColumn sc_searchValue = (SelectColumn)searchValue_vector.elementAt(index);
         Vector vc_searchValue = sc_searchValue.getColumnExpression();
         WhereItem wi_if;
         WhereColumn if_left;
         Vector vc_if_left;
         WhereColumn if_right;
         Vector vc_if_right;
         if (vc_searchValue.elementAt(0) instanceof FunctionCalls) {
            FunctionCalls fnCall_subfn = (FunctionCalls)vc_searchValue.elementAt(0);
            TableColumn tbCl_subfn = fnCall_subfn.getFunctionName();
            Vector vc_subfn = fnCall_subfn.getFunctionArguments();
            fnName = tbCl_subfn.getColumnName();
            if (!fnName.equalsIgnoreCase("equals")) {
               operator = "like";

               for(int i_count = 0; i_count < vc_subfn.size(); ++i_count) {
                  WhereItem wi_if = new WhereItem();
                  WhereColumn if_left = new WhereColumn();
                  Vector vc_if_left = new Vector();
                  WhereColumn if_right = new WhereColumn();
                  Vector vc_if_right = new Vector();
                  if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                     vector.addElement(((SelectColumn)this.functionArguments.elementAt(0)).toMySQLSelect(to_sqs, from_sqs));
                  } else {
                     vector.addElement(this.functionArguments.elementAt(0));
                  }

                  vc_if_left.addElement(vector.get(i_count));
                  if_left.setColumnExpression(vc_if_left);
                  wi_if.setLeftWhereExp(if_left);
                  wi_if.setOperator(operator);
                  SelectColumn sc_concat = new SelectColumn();
                  FunctionCalls fn_concat = new FunctionCalls();
                  TableColumn tb_concat = new TableColumn();
                  tb_concat.setColumnName("CONCAT");
                  fn_concat.setFunctionName(tb_concat);
                  Vector vc_concatIn = new Vector();
                  if (fnName.equalsIgnoreCase("STARTSWITH")) {
                     vc_concatIn.addElement(vc_subfn.get(i_count));
                     vc_concatIn.addElement("'%'");
                  } else if (fnName.equalsIgnoreCase("ENDSWITH")) {
                     vc_concatIn.addElement("'%'");
                     vc_concatIn.addElement(vc_subfn.get(i_count));
                  } else if (fnName.equalsIgnoreCase("CONTAINS")) {
                     vc_concatIn.addElement("'%'");
                     vc_concatIn.addElement(vc_subfn.get(i_count));
                     vc_concatIn.addElement("'%'");
                  } else if (vc_subfn.size() == 1) {
                     vc_concatIn.addElement("'%'");
                     vc_concatIn.addElement(vc_subfn.get(i_count));
                     vc_concatIn.addElement("'%'");
                  }

                  Vector vc_concatOut = new Vector();
                  fn_concat.setFunctionArguments(vc_concatIn);
                  vc_concatOut.addElement(fn_concat);
                  sc_concat.setOpenBrace("(");
                  sc_concat.setCloseBrace(")");
                  sc_concat.setColumnExpression(vc_concatOut);
                  vc_if_right.addElement(sc_concat);
                  if_right.setColumnExpression(vc_if_right);
                  wi_if.setRightWhereExp(if_right);
                  wi_if.setOpenBrace("(");
                  wi_if.setCloseBrace(")");
                  whExp.addWhereItem(wi_if);
                  if (i_count < vc_subfn.size() - 1) {
                     whExp.addOperator("OR");
                  }
               }
            } else {
               operator = "IN";
               wi_if = new WhereItem();
               if_left = new WhereColumn();
               vc_if_left = new Vector();
               if_right = new WhereColumn();
               vc_if_right = new Vector();
               if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                  vector.addElement(((SelectColumn)this.functionArguments.elementAt(0)).toMySQLSelect(to_sqs, from_sqs));
               } else {
                  vector.addElement(this.functionArguments.elementAt(0));
               }

               vc_if_left.addElement(vector.get(vector.size() - 1));
               if_left.setColumnExpression(vc_if_left);
               wi_if.setLeftWhereExp(if_left);
               wi_if.setOperator(operator);
               SelectColumn sc_rightExp = new SelectColumn();
               Vector vc_rightExp = new Vector();
               vc_rightExp.addElement(vc_subfn.get(0));

               for(int i = 0; i < vc_subfn.size(); ++i) {
                  vc_rightExp.addElement(",");
                  vc_rightExp.addElement(vc_subfn.get(i));
               }

               sc_rightExp.setColumnExpression(vc_rightExp);
               sc_rightExp.setOpenBrace("(");
               sc_rightExp.setCloseBrace(")");
               vc_if_right.addElement(sc_rightExp);
               if_right.setColumnExpression(vc_if_right);
               wi_if.setRightWhereExp(if_right);
               whExp.addWhereItem(wi_if);
            }

            whExp.setOpenBrace("(");
            whExp.setCloseBrace(")");
            vc_whExp.addElement(whExp);
         } else {
            wi_if = new WhereItem();
            if_left = new WhereColumn();
            vc_if_left = new Vector();
            if_right = new WhereColumn();
            vc_if_right = new Vector();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
               vector.addElement(((SelectColumn)this.functionArguments.elementAt(0)).toMySQLSelect(to_sqs, from_sqs));
            } else {
               vector.addElement(this.functionArguments.elementAt(0));
            }

            vc_if_left.addElement(vector.get(0));
            if_left.setColumnExpression(vc_if_left);
            wi_if.setLeftWhereExp(if_left);
            vc_if_right.addElement(searchValue_vector.elementAt(index));
            if_right.setColumnExpression(vc_if_right);
            wi_if.setRightWhereExp(if_right);
            wi_if.setOpenBrace("(");
            wi_if.setCloseBrace(")");
            wi_if.setOperator(operator);
            whExp.addWhereItem(wi_if);
            vc_whExp.addElement(whExp);
         }
      }

      sc_whExp.setColumnExpression(vc_whExp);
      return whExp;
   }

   public WhereExpression createcondition(Object obj) {
      WhereExpression whereExpression = new WhereExpression();
      Vector whereItemsforwhereExpression = new Vector();
      WhereItem wi = new WhereItem();
      WhereColumn wc = new WhereColumn();
      Vector whereColumnColExp = new Vector();
      whereColumnColExp.add(obj);
      wc.setColumnExpression(whereColumnColExp);
      wi.setLeftWhereExp(wc);
      whereItemsforwhereExpression.add(wi);
      whereExpression.setWhereItem(whereItemsforwhereExpression);
      return whereExpression;
   }
}
