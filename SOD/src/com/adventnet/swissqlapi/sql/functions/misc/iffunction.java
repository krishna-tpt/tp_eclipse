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

public class iffunction extends FunctionCalls {
   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement caseStmt = new CaseStatement();
      WhenStatement whenStmt = new WhenStatement();
      WhereExpression whereExpr = new WhereExpression();
      WhereItem whereItem = new WhereItem();
      WhereColumn whereCln = new WhereColumn();
      caseStmt.setCaseClause("CASE");
      whenStmt.setWhenClause("WHEN");
      whenStmt.setThenClause("THEN");
      caseStmt.setElseClause("ELSE");
      caseStmt.setEndClause("END");
      Vector whenStmtList = new Vector();
      Vector whereItemVector = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.get(i);
            switch(i) {
            case 0:
               sc = sc.toMSSQLServerSelect(to_sqs, from_sqs);
               whereCln.setColumnExpression(sc.getColumnExpression());
               whereItem.setLeftWhereExp(whereCln);
               whereItemVector.add(whereItem);
               whereExpr.setWhereItem(whereItemVector);
               whenStmt.setWhenCondition(whereExpr);
               break;
            case 1:
               whenStmt.setThenStatement(sc.toMSSQLServerSelect(to_sqs, from_sqs));
               whenStmtList.add(whenStmt);
               caseStmt.setWhenStatementList(whenStmtList);
               break;
            case 2:
               caseStmt.setElseStatement(sc.toMSSQLServerSelect(to_sqs, from_sqs));
            }
         }
      }

      SelectColumn caseStmtSC = new SelectColumn();
      Vector selectColumnExpr = new Vector();
      selectColumnExpr.add(caseStmt);
      caseStmtSC.setColumnExpression(selectColumnExpr);
      Vector funcArgVector = new Vector();
      funcArgVector.add(caseStmtSC);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionName((TableColumn)null);
      this.setFunctionArguments(funcArgVector);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (from_sqs != null && from_sqs.isHyperSql()) {
         CaseStatement caseStmt = new CaseStatement();
         WhenStatement whenStmt = new WhenStatement();
         WhereExpression whereExpr = new WhereExpression();
         WhereItem whereItem = new WhereItem();
         WhereColumn whereCln = new WhereColumn();
         caseStmt.setCaseClause("CASE");
         whenStmt.setWhenClause("WHEN");
         whenStmt.setThenClause("THEN");
         caseStmt.setElseClause("ELSE");
         caseStmt.setEndClause("END");
         Vector whenStmtList = new Vector();
         Vector whereItemVector = new Vector();

         for(int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.functionArguments.get(i);
               switch(i) {
               case 0:
                  sc = sc.toMySQLSelect(to_sqs, from_sqs, true);
                  whereCln.setColumnExpression(sc.getColumnExpression());
                  whereItem.setLeftWhereExp(whereCln);
                  whereItemVector.add(whereItem);
                  whereExpr.setWhereItem(whereItemVector);
                  whenStmt.setWhenCondition(whereExpr);
                  break;
               case 1:
                  whenStmt.setThenStatement(sc.toMySQLSelect(to_sqs, from_sqs, true));
                  whenStmtList.add(whenStmt);
                  caseStmt.setWhenStatementList(whenStmtList);
                  break;
               case 2:
                  caseStmt.setElseStatement(sc.toMySQLSelect(to_sqs, from_sqs, true));
               }
            }
         }

         SelectColumn caseStmtSC = new SelectColumn();
         Vector selectColumnExpr = new Vector();
         selectColumnExpr.add(caseStmt);
         caseStmtSC.setColumnExpression(selectColumnExpr);
         Vector funcArgVector = new Vector();
         funcArgVector.add(caseStmtSC);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionName((TableColumn)null);
         this.setFunctionArguments(funcArgVector);
      } else {
         this.functionName.setColumnName("IF");
         Vector arguments = new Vector();

         for(int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMySQLSelect(to_sqs, from_sqs, true));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         this.setFunctionArguments(arguments);
      }

   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (!(this.functionArguments.elementAt(i) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i));
         } else {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i);
            Vector v = sc.getColumnExpression();
            if (i == 0 && v != null && v.size() == 1 && !(v.get(0) instanceof WhereItem) && !(v.get(0) instanceof WhereExpression)) {
               Vector colExp = null;
               Object object = null;
               boolean isTableColumn = false;
               boolean isListedFunction = false;
               boolean isListedStringFunction = false;
               boolean isDateSelColExp = false;
               if (v.get(0) instanceof SelectColumn) {
                  colExp = ((SelectColumn)v.get(0)).getColumnExpression();
                  if (colExp != null) {
                     if (colExp.size() == 1) {
                        object = colExp.get(0);
                     } else if (colExp.size() == 5) {
                        Object arg1 = colExp.get(0);
                        Object arg5 = colExp.get(4);
                        if (arg1 instanceof FunctionCalls && ((FunctionCalls)arg1).getFunctionNameAsAString() != null && ((FunctionCalls)arg1).getFunctionNameAsAString().trim().equalsIgnoreCase("FROM_UNIXTIME") && arg5 instanceof String && arg5.toString().trim().equalsIgnoreCase("microsecond")) {
                           isDateSelColExp = true;
                        }
                     }
                  }
               } else {
                  object = v.get(0);
               }

               Vector strFnList;
               if (object != null) {
                  if (object instanceof TableColumn) {
                     isTableColumn = true;
                  } else if (object instanceof FunctionCalls) {
                     TableColumn newfunctionName = ((FunctionCalls)object).getFunctionName();
                     Vector fnList = this.getFunctionListsForMissingWhereItems();
                     strFnList = this.getStringFunctionListsForMissingWhereItems();
                     if (newfunctionName != null && newfunctionName.getColumnName() != null) {
                        if (this.ifFunctionWithNumberArguments(newfunctionName.getColumnName().trim(), ((FunctionCalls)object).getFunctionArguments())) {
                           isListedFunction = true;
                        } else if (fnList.contains(newfunctionName.getColumnName().trim().toUpperCase())) {
                           isListedFunction = true;
                        } else if (strFnList.contains(newfunctionName.getColumnName().trim().toUpperCase())) {
                           isListedStringFunction = true;
                        }
                     }
                  }
               }

               if (isTableColumn || isListedFunction || isListedStringFunction || isDateSelColExp) {
                  WhereItem wi = new WhereItem();
                  WhereColumn wcL = new WhereColumn();
                  wcL.setColumnExpression(v);
                  wi.setLeftWhereExp(wcL);
                  if (!isDateSelColExp && !isListedStringFunction) {
                     strFnList = new Vector();
                     strFnList.add("0");
                     WhereColumn wcR = new WhereColumn();
                     wcR.setColumnExpression(strFnList);
                     wi.setRightWhereExp(wcR);
                     wi.setOperator(">");
                  } else {
                     wi.setOperator("IS NOT NULL");
                  }

                  strFnList = new Vector();
                  strFnList.add(wi);
                  sc.setColumnExpression(strFnList);
               }
            }

            this.typeCastToCharForEmptyString(i, arguments, to_sqs, from_sqs, "VW");
            arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("IF");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("IF");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement caseStmt = new CaseStatement();
      WhenStatement whenStmt = new WhenStatement();
      WhereExpression whereExpr = new WhereExpression();
      WhereItem whereItem = new WhereItem();
      WhereColumn whereCln = new WhereColumn();
      caseStmt.setCaseClause("CASE");
      whenStmt.setWhenClause("WHEN");
      whenStmt.setThenClause("THEN");
      caseStmt.setElseClause("ELSE");
      caseStmt.setEndClause("END");
      Vector whenStmtList = new Vector();
      Vector whereItemVector = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.get(i);
            switch(i) {
            case 0:
               sc = sc.toSapHanaSelect(to_sqs, from_sqs);
               whereCln.setColumnExpression(sc.getColumnExpression());
               whereItem.setLeftWhereExp(whereCln);
               whereItemVector.add(whereItem);
               whereExpr.setWhereItem(whereItemVector);
               whenStmt.setWhenCondition(whereExpr);
               break;
            case 1:
               whenStmt.setThenStatement(sc.toSapHanaSelect(to_sqs, from_sqs));
               whenStmtList.add(whenStmt);
               caseStmt.setWhenStatementList(whenStmtList);
               break;
            case 2:
               caseStmt.setElseStatement(sc.toSapHanaSelect(to_sqs, from_sqs));
            }
         }
      }

      SelectColumn caseStmtSC = new SelectColumn();
      Vector selectColumnExpr = new Vector();
      selectColumnExpr.add(caseStmt);
      caseStmtSC.setColumnExpression(selectColumnExpr);
      Vector funcArgVector = new Vector();
      funcArgVector.add(caseStmtSC);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionName((TableColumn)null);
      this.setFunctionArguments(funcArgVector);
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement caseStmt = new CaseStatement();
      WhenStatement whenStmt = new WhenStatement();
      WhereExpression whereExpr = new WhereExpression();
      WhereItem whereItem = new WhereItem();
      WhereColumn whereCln = new WhereColumn();
      caseStmt.setCaseClause("CASE");
      whenStmt.setWhenClause("WHEN");
      whenStmt.setThenClause("THEN");
      caseStmt.setElseClause("ELSE");
      caseStmt.setEndClause("END");
      Vector whenStmtList = new Vector();
      Vector whereItemVector = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.get(i);
            switch(i) {
            case 0:
               sc = sc.toSqliteSelect(to_sqs, from_sqs);
               whereCln.setColumnExpression(sc.getColumnExpression());
               whereItem.setLeftWhereExp(whereCln);
               whereItemVector.add(whereItem);
               whereExpr.setWhereItem(whereItemVector);
               whenStmt.setWhenCondition(whereExpr);
               break;
            case 1:
               whenStmt.setThenStatement(sc.toSqliteSelect(to_sqs, from_sqs));
               whenStmtList.add(whenStmt);
               caseStmt.setWhenStatementList(whenStmtList);
               break;
            case 2:
               caseStmt.setElseStatement(sc.toSqliteSelect(to_sqs, from_sqs));
            }
         }
      }

      SelectColumn caseStmtSC = new SelectColumn();
      Vector selectColumnExpr = new Vector();
      selectColumnExpr.add(caseStmt);
      caseStmtSC.setColumnExpression(selectColumnExpr);
      Vector funcArgVector = new Vector();
      funcArgVector.add(caseStmtSC);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionName((TableColumn)null);
      this.setFunctionArguments(funcArgVector);
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("Iif");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement caseStmt = new CaseStatement();
      WhenStatement whenStmt = new WhenStatement();
      WhereExpression whereExpr = new WhereExpression();
      WhereItem whereItem = new WhereItem();
      WhereColumn whereCln = new WhereColumn();
      caseStmt.setCaseClause("CASE");
      whenStmt.setWhenClause("WHEN");
      whenStmt.setThenClause("THEN");
      caseStmt.setElseClause("ELSE");
      caseStmt.setEndClause("END");
      Vector whenStmtList = new Vector();
      Vector whereItemVector = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.get(i);
            switch(i) {
            case 0:
               sc = sc.toMsAccessJdbcSelect(to_sqs, from_sqs);
               whereCln.setColumnExpression(sc.getColumnExpression());
               whereItem.setLeftWhereExp(whereCln);
               whereItemVector.add(whereItem);
               whereExpr.setWhereItem(whereItemVector);
               whenStmt.setWhenCondition(whereExpr);
               break;
            case 1:
               whenStmt.setThenStatement(sc.toMsAccessJdbcSelect(to_sqs, from_sqs));
               whenStmtList.add(whenStmt);
               caseStmt.setWhenStatementList(whenStmtList);
               break;
            case 2:
               caseStmt.setElseStatement(sc.toMsAccessJdbcSelect(to_sqs, from_sqs));
            }
         }
      }

      SelectColumn caseStmtSC = new SelectColumn();
      Vector selectColumnExpr = new Vector();
      selectColumnExpr.add(caseStmt);
      caseStmtSC.setColumnExpression(selectColumnExpr);
      Vector funcArgVector = new Vector();
      funcArgVector.add(caseStmtSC);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionName((TableColumn)null);
      this.setFunctionArguments(funcArgVector);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("IF");
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      boolean castingFlagEnabled = this.castToTextInsideIf() && to_sqs != null && to_sqs.getBooleanValues("set.can.cast.all.to.text.columns");
      boolean castAllArgs = castingFlagEnabled;
      boolean castingAllowedForStringFns = from_sqs != null && from_sqs.getBooleanValues("can.cast.string.functions.to.text.inside.if.function");
      int i_count;
      SelectColumn sc;
      if (!castingFlagEnabled) {
         for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (i_count != 0) {
               sc = (SelectColumn)this.functionArguments.elementAt(i_count);
               if (sc.needsCastingForStringLiteralsInsideIfFunction() || castingAllowedForStringFns && sc.needsCastingForStringFunctionsInsideIfFunction()) {
                  castAllArgs = true;
                  break;
               }
            }
         }
      }

      for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            Vector v = sc.getColumnExpression();
            if (i_count == 0 && v != null && v.size() == 1 && !(v.get(0) instanceof WhereItem) && !(v.get(0) instanceof WhereExpression)) {
               Vector colExp = null;
               Object object = null;
               boolean isTableColumn = false;
               boolean isListedFunction = false;
               boolean isListedStringFunction = false;
               boolean isDateSelColExp = false;
               if (v.get(0) instanceof SelectColumn) {
                  colExp = ((SelectColumn)v.get(0)).getColumnExpression();
                  if (colExp != null) {
                     if (colExp.size() == 1) {
                        object = colExp.get(0);
                     } else if (colExp.size() == 5) {
                        Object arg1 = colExp.get(0);
                        Object arg5 = colExp.get(4);
                        if (arg1 instanceof FunctionCalls && ((FunctionCalls)arg1).getFunctionNameAsAString() != null && ((FunctionCalls)arg1).getFunctionNameAsAString().trim().equalsIgnoreCase("FROM_UNIXTIME") && arg5 instanceof String && arg5.toString().trim().equalsIgnoreCase("microsecond")) {
                           isDateSelColExp = true;
                        }
                     }
                  }
               } else {
                  object = v.get(0);
               }

               Vector strFnList;
               if (object != null) {
                  if (object instanceof TableColumn) {
                     isTableColumn = true;
                  } else if (object instanceof FunctionCalls) {
                     TableColumn newfunctionName = ((FunctionCalls)object).getFunctionName();
                     Vector fnList = this.getFunctionListsForMissingWhereItems();
                     strFnList = this.getStringFunctionListsForMissingWhereItems();
                     if (newfunctionName != null && newfunctionName.getColumnName() != null) {
                        if (this.ifFunctionWithNumberArguments(newfunctionName.getColumnName().trim(), ((FunctionCalls)object).getFunctionArguments())) {
                           isListedFunction = true;
                        } else if (fnList.contains(newfunctionName.getColumnName().trim().toUpperCase())) {
                           isListedFunction = true;
                        } else if (strFnList.contains(newfunctionName.getColumnName().trim().toUpperCase())) {
                           isListedStringFunction = true;
                        }
                     }
                  }
               }

               if (isTableColumn || isListedFunction || isListedStringFunction || isDateSelColExp) {
                  WhereItem wi = new WhereItem();
                  WhereColumn wcL = new WhereColumn();
                  wcL.setColumnExpression(v);
                  wi.setLeftWhereExp(wcL);
                  if (!isDateSelColExp && !isListedStringFunction) {
                     strFnList = new Vector();
                     strFnList.add("0");
                     WhereColumn wcR = new WhereColumn();
                     wcR.setColumnExpression(strFnList);
                     wi.setRightWhereExp(wcR);
                     wi.setOperator(">");
                  } else {
                     wi.setOperator("IS NOT NULL");
                  }

                  strFnList = new Vector();
                  strFnList.add(wi);
                  sc.setColumnExpression(strFnList);
               }
            }

            if (i_count != 0 && castAllArgs) {
               sc.convertSelectColumnArgsToTextDataType(true);
            } else {
               sc.convertSelectColumnNumericArgToStringLiteralArg(castingFlagEnabled);
            }

            arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
         }
      }

      this.setFunctionArguments(arguments);
      if (to_sqs != null) {
         to_sqs.addCurrentIndexToIfFunctionList();
      }

      if (from_sqs == null || !from_sqs.canUseIFFunctionForPGCaseWhenExp() || isPostgreLiveDbs) {
         String elseArgString = arguments.get(2).toString().trim();
         String finalElseString = elseArgString;
         String elseString = " ELSE ";

         try {
            if (elseArgString.toUpperCase().startsWith("(CASE WHEN") && elseArgString.toUpperCase().endsWith("END)")) {
               finalElseString = elseArgString.substring(5, elseArgString.length() - 4);
               elseString = "";
            }
         } catch (Exception var21) {
         }

         String qry = "(CASE WHEN " + arguments.get(0) + " THEN " + arguments.get(1) + elseString + finalElseString + " END)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("IF");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            Vector v = sc.getColumnExpression();
            if (i_count == 0 && v != null && v.size() == 1 && !(v.get(0) instanceof WhereItem) && !(v.get(0) instanceof WhereExpression)) {
               Vector colExp = null;
               Object object = null;
               boolean isTableColumn = false;
               boolean isListedFunction = false;
               boolean isListedStringFunction = false;
               boolean isDateSelColExp = false;
               if (v.get(0) instanceof SelectColumn) {
                  colExp = ((SelectColumn)v.get(0)).getColumnExpression();
                  if (colExp != null) {
                     if (colExp.size() == 1) {
                        object = colExp.get(0);
                     } else if (colExp.size() == 5) {
                        Object arg1 = colExp.get(0);
                        Object arg5 = colExp.get(4);
                        if (arg1 instanceof FunctionCalls && ((FunctionCalls)arg1).getFunctionNameAsAString() != null && ((FunctionCalls)arg1).getFunctionNameAsAString().trim().equalsIgnoreCase("FROM_UNIXTIME") && arg5 instanceof String && arg5.toString().trim().equalsIgnoreCase("microsecond")) {
                           isDateSelColExp = true;
                        }
                     }
                  }
               } else {
                  object = v.get(0);
               }

               Vector strFnList;
               if (object != null) {
                  if (object instanceof TableColumn) {
                     isTableColumn = true;
                  } else if (object instanceof FunctionCalls) {
                     TableColumn newfunctionName = ((FunctionCalls)object).getFunctionName();
                     Vector fnList = this.getFunctionListsForMissingWhereItems();
                     strFnList = this.getStringFunctionListsForMissingWhereItems();
                     if (newfunctionName != null && newfunctionName.getColumnName() != null) {
                        if (this.ifFunctionWithNumberArguments(newfunctionName.getColumnName().trim(), ((FunctionCalls)object).getFunctionArguments())) {
                           isListedFunction = true;
                        } else if (fnList.contains(newfunctionName.getColumnName().trim().toUpperCase())) {
                           isListedFunction = true;
                        } else if (strFnList.contains(newfunctionName.getColumnName().trim().toUpperCase())) {
                           isListedStringFunction = true;
                        }
                     }
                  }
               }

               if (isTableColumn || isListedFunction || isListedStringFunction || isDateSelColExp) {
                  WhereItem wi = new WhereItem();
                  WhereColumn wcL = new WhereColumn();
                  wcL.setColumnExpression(v);
                  wi.setLeftWhereExp(wcL);
                  if (!isDateSelColExp && !isListedStringFunction) {
                     strFnList = new Vector();
                     strFnList.add("0");
                     WhereColumn wcR = new WhereColumn();
                     wcR.setColumnExpression(strFnList);
                     wi.setRightWhereExp(wcR);
                     wi.setOperator(">");
                  } else {
                     wi.setOperator("IS NOT NULL");
                  }

                  strFnList = new Vector();
                  strFnList.add(wi);
                  sc.setColumnExpression(strFnList);
               }
            }

            arguments.addElement(sc.toOracleSelect(to_sqs, from_sqs));
         }
      }

      this.setFunctionArguments(arguments);
      if (to_sqs != null) {
         to_sqs.addCurrentIndexToIfFunctionList();
      }

      if (from_sqs == null || from_sqs.isOracleLive()) {
         String elseArgString = arguments.get(2).toString().trim();
         String finalElseString = elseArgString;
         String elseString = " ELSE ";

         try {
            if (elseArgString.toUpperCase().startsWith("(CASE WHEN") && elseArgString.toUpperCase().endsWith("END)")) {
               finalElseString = elseArgString.substring(5, elseArgString.length() - 4);
               elseString = "";
            }
         } catch (Exception var17) {
         }

         String qry = "(CASE WHEN " + arguments.get(0) + " THEN " + arguments.get(1) + elseString + finalElseString + " END)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public boolean ifFunctionWithNumberArguments(String functionName, Vector fnArgs) {
      try {
         if (functionName.trim().equalsIgnoreCase("IF") && fnArgs != null && fnArgs.size() == 3 && fnArgs.get(1) instanceof SelectColumn && fnArgs.get(2) instanceof SelectColumn) {
            SelectColumn sc2 = (SelectColumn)fnArgs.get(1);
            SelectColumn sc3 = (SelectColumn)fnArgs.get(2);
            if (sc2 != null && sc3 != null && sc2.getColumnExpression() != null && sc3.getColumnExpression() != null && sc2.getColumnExpression().size() == 1 && sc3.getColumnExpression().size() == 1 && sc2.getColumnExpression().get(0) instanceof String && sc3.getColumnExpression().get(0) instanceof String) {
               String sc2value = (String)sc2.getColumnExpression().get(0);
               String sc3value = (String)sc3.getColumnExpression().get(0);

               try {
                  if (sc2value != null && sc3value != null) {
                     int nullcount = 0;
                     if (sc2value.equalsIgnoreCase("null")) {
                        sc2value = "0";
                        ++nullcount;
                     }

                     if (sc3value.equalsIgnoreCase("null")) {
                        sc3value = "0";
                        ++nullcount;
                     }

                     if (nullcount != 2) {
                        sc2value = sc2value.replaceAll("'", "");
                        sc3value = sc3value.replaceAll("'", "");
                        Double.parseDouble(sc2value);
                        Double.parseDouble(sc3value);
                        return true;
                     }
                  }
               } catch (Exception var8) {
               }
            }
         }
      } catch (Exception var9) {
      }

      return false;
   }

   public void typeCastToCharForEmptyString(int argPosition, Vector arguments, SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, String dbType) {
      try {
         if (argPosition > 0) {
            int replacePos = argPosition == 1 ? 2 : 1;
            SelectColumn selCol = (SelectColumn)this.functionArguments.elementAt(argPosition);
            boolean needsCasting = selCol.needsCastingForStringLiterals(true);
            if (needsCasting) {
               SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(replacePos);
               sc.convertSelectColumnToTextDataType();
               if (argPosition == 2) {
                  if (dbType.equalsIgnoreCase("VW")) {
                     arguments.setElementAt(((SelectColumn)this.functionArguments.elementAt(replacePos)).toVectorWiseSelect(to_sqs, from_sqs), replacePos);
                  } else {
                     arguments.setElementAt(((SelectColumn)this.functionArguments.elementAt(replacePos)).toPostgreSQLSelect(to_sqs, from_sqs), replacePos);
                  }
               }
            }
         }
      } catch (Exception var10) {
      }

   }

   public Vector getStringFunctionListsForMissingWhereItems() {
      Vector fnList = new Vector();
      fnList.add("CONCAT");
      fnList.add("CONCAT_WS");
      fnList.add("STRING_CONCAT");
      fnList.add("SUBSTRING");
      fnList.add("SUBSTR");
      fnList.add("SUBSTRING_UDF");
      fnList.add("SUBSTRING_INDEX");
      fnList.add("UPPER");
      fnList.add("LOWER");
      fnList.add("LEFT");
      fnList.add("LEFT_UDF");
      fnList.add("RIGHT");
      fnList.add("RIGHT_UDF");
      fnList.add("REVERSE");
      fnList.add("REPEAT");
      fnList.add("REPLACE");
      fnList.add("TRIM");
      fnList.add("LTRIM");
      fnList.add("RTRIM");
      fnList.add("LPAD");
      fnList.add("RPAD");
      fnList.add("SOUNDEX");
      fnList.add("SPACE");
      fnList.add("ZR_TEXTBETWEEN");
      fnList.add("LCASE");
      fnList.add("UCASE");
      return fnList;
   }

   public Vector getFunctionListsForMissingWhereItems() {
      Vector fnList = new Vector();
      fnList.add("ISNULL");
      fnList.add("LENGTH");
      fnList.add("CHAR_LENGTH");
      fnList.add("CHARACTER_LENGTH");
      fnList.add("LOCATE");
      fnList.add("POSITION");
      fnList.add("TO_DECIMAL");
      fnList.add("TO_INTEGER");
      fnList.add("INDEXOF");
      fnList.add("INSTR");
      fnList.add("ABS");
      fnList.add("CEIL");
      fnList.add("CEILING");
      fnList.add("FLOOR");
      fnList.add("RAND");
      fnList.add("ROUND");
      fnList.add("AVG");
      fnList.add("MIN");
      fnList.add("MAX");
      fnList.add("SUM");
      fnList.add("COUNT");
      fnList.add("STD");
      fnList.add("VARIANCE");
      fnList.add("DATEDIFF");
      fnList.add("DAYOFMONTH");
      fnList.add("DAYOFYEAR");
      fnList.add("HOUR");
      fnList.add("LAST_DAY");
      fnList.add("MICROSECOND");
      fnList.add("MINUTE");
      fnList.add("MONTH");
      fnList.add("PERIOD_ADD");
      fnList.add("PERIOD_DIFF");
      fnList.add("QUARTER");
      fnList.add("TIMESTAMPDIFF");
      fnList.add("TIME_TO_SEC");
      fnList.add("TO_DAYS");
      fnList.add("UNIX_TIMESTAMP");
      fnList.add("WEEK");
      fnList.add("WEEKDAY");
      fnList.add("WEEKOFYEAR");
      fnList.add("YEAR");
      fnList.add("YEARWEEK");
      fnList.add("DATEANDTIMEDIFF");
      fnList.add("ZR_FYEAR");
      fnList.add("ZR_FYEARDT");
      fnList.add("ZR_FYEARINTRVAL");
      fnList.add("ZR_FQUARTER");
      fnList.add("ZR_FQUARTERDT");
      fnList.add("ZR_FQUARTERINTRVAL");
      fnList.add("ZR_FQUARTERYEAR");
      fnList.add("ZR_FQUARTERYEARDT");
      fnList.add("ZR_FQUARTERYEARINTRVAL");
      fnList.add("ZR_FWEEK");
      fnList.add("ZR_FWEEKDTNWKSTRTDAY");
      fnList.add("ZR_FWEEKINTRVALNWKSTRTDAY");
      fnList.add("ZR_FWEEKDT");
      fnList.add("ZR_FWEEKINTRVAL");
      fnList.add("ZR_FWEEKYEAR");
      fnList.add("ZR_FWEEKYEARDTNWKSTRTDAY");
      fnList.add("ZR_FWEEKYEARINTRVALNWKSTRTDAY");
      fnList.add("ZR_FWEEKYEARDT");
      fnList.add("ZR_FWEEKYEARINTRVAL");
      fnList.add("ZR_WEEKYEARDTNWKSTRTDAY");
      fnList.add("ZR_WEEKDTNWKSTRTDAY");
      fnList.add("ZR_WEEKYEARINTRVALNWKSTRTDAY");
      fnList.add("ZR_WEEKINTRVALNWKSTRTDAY");
      fnList.add("ZR_ISPREVIOUSMONTH");
      fnList.add("ZR_ISPREVIOUSQUARTER");
      fnList.add("ZR_ISPREVIOUSWEEK");
      fnList.add("ZR_ISLASTMONTH");
      fnList.add("ZR_ISLASTQUARTER");
      fnList.add("ZR_ISNEXTMONTH");
      fnList.add("ZR_ISNEXTQUARTER");
      fnList.add("ZR_ISNEXTWEEK");
      fnList.add("ZR_ISCURRENTWEEK");
      fnList.add("ZR_BUSINESS_DAYS");
      fnList.add("ZR_BUSINESS_HOURS");
      fnList.add("ZR_BUSINESS_MINUTES");
      fnList.add("ZR_BUSINESS_ENDDAY");
      return fnList;
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement caseStmt = new CaseStatement();
      WhenStatement whenStmt = new WhenStatement();
      WhereExpression whereExpr = new WhereExpression();
      WhereItem whereItem = new WhereItem();
      WhereColumn whereCln = new WhereColumn();
      caseStmt.setCaseClause("CASE");
      whenStmt.setWhenClause("WHEN");
      whenStmt.setThenClause("THEN");
      caseStmt.setElseClause("ELSE");
      caseStmt.setEndClause("END");
      Vector whenStmtList = new Vector();
      Vector whereItemVector = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.get(i);
            switch(i) {
            case 0:
               sc = sc.toSnowflakeSelect(to_sqs, from_sqs);
               whereCln.setColumnExpression(sc.getColumnExpression());
               whereItem.setLeftWhereExp(whereCln);
               whereItemVector.add(whereItem);
               whereExpr.setWhereItem(whereItemVector);
               whenStmt.setWhenCondition(whereExpr);
               break;
            case 1:
               whenStmt.setThenStatement(sc.toSnowflakeSelect(to_sqs, from_sqs));
               whenStmtList.add(whenStmt);
               caseStmt.setWhenStatementList(whenStmtList);
               break;
            case 2:
               caseStmt.setElseStatement(sc.toSnowflakeSelect(to_sqs, from_sqs));
            }
         }
      }

      SelectColumn caseStmtSC = new SelectColumn();
      Vector selectColumnExpr = new Vector();
      selectColumnExpr.add(caseStmt);
      caseStmtSC.setColumnExpression(selectColumnExpr);
      Vector funcArgVector = new Vector();
      funcArgVector.add(caseStmtSC);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionName((TableColumn)null);
      this.setFunctionArguments(funcArgVector);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement caseStmt = new CaseStatement();
      WhenStatement whenStmt = new WhenStatement();
      WhereExpression whereExpr = new WhereExpression();
      WhereItem whereItem = new WhereItem();
      WhereColumn whereCln = new WhereColumn();
      caseStmt.setCaseClause("CASE");
      whenStmt.setWhenClause("WHEN");
      whenStmt.setThenClause("THEN");
      caseStmt.setElseClause("ELSE");
      caseStmt.setEndClause("END");
      Vector whenStmtList = new Vector();
      Vector whereItemVector = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.get(i);
            switch(i) {
            case 0:
               sc = sc.toDB2Select(to_sqs, from_sqs);
               whereCln.setColumnExpression(sc.getColumnExpression());
               whereItem.setLeftWhereExp(whereCln);
               whereItemVector.add(whereItem);
               whereExpr.setWhereItem(whereItemVector);
               whenStmt.setWhenCondition(whereExpr);
               break;
            case 1:
               whenStmt.setThenStatement(sc.toDB2Select(to_sqs, from_sqs));
               whenStmtList.add(whenStmt);
               caseStmt.setWhenStatementList(whenStmtList);
               break;
            case 2:
               caseStmt.setElseStatement(sc.toDB2Select(to_sqs, from_sqs));
            }
         }
      }

      SelectColumn caseStmtSC = new SelectColumn();
      Vector selectColumnExpr = new Vector();
      selectColumnExpr.add(caseStmt);
      caseStmtSC.setColumnExpression(selectColumnExpr);
      Vector funcArgVector = new Vector();
      funcArgVector.add(caseStmtSC);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionName((TableColumn)null);
      this.setFunctionArguments(funcArgVector);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement caseStmt = new CaseStatement();
      WhenStatement whenStmt = new WhenStatement();
      WhereExpression whereExpr = new WhereExpression();
      WhereItem whereItem = new WhereItem();
      WhereColumn whereCln = new WhereColumn();
      caseStmt.setCaseClause("CASE");
      whenStmt.setWhenClause("WHEN");
      whenStmt.setThenClause("THEN");
      caseStmt.setElseClause("ELSE");
      caseStmt.setEndClause("END");
      Vector whenStmtList = new Vector();
      Vector whereItemVector = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.get(i);
            switch(i) {
            case 0:
               sc = sc.toInformixSelect(to_sqs, from_sqs);
               whereCln.setColumnExpression(sc.getColumnExpression());
               whereItem.setLeftWhereExp(whereCln);
               whereItemVector.add(whereItem);
               whereExpr.setWhereItem(whereItemVector);
               whenStmt.setWhenCondition(whereExpr);
               break;
            case 1:
               whenStmt.setThenStatement(sc.toInformixSelect(to_sqs, from_sqs));
               whenStmtList.add(whenStmt);
               caseStmt.setWhenStatementList(whenStmtList);
               break;
            case 2:
               caseStmt.setElseStatement(sc.toInformixSelect(to_sqs, from_sqs));
            }
         }
      }

      SelectColumn caseStmtSC = new SelectColumn();
      Vector selectColumnExpr = new Vector();
      selectColumnExpr.add(caseStmt);
      caseStmtSC.setColumnExpression(selectColumnExpr);
      Vector funcArgVector = new Vector();
      funcArgVector.add(caseStmtSC);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionName((TableColumn)null);
      this.setFunctionArguments(funcArgVector);
   }
}
