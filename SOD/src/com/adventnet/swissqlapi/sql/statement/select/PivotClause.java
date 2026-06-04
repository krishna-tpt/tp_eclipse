package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class PivotClause {
   private String pivotStr;
   private TableColumn pivotColumn;
   private TableColumn unpivotColumn;
   private String forStr;
   private WhereItem inClause;
   private String openBrace;
   private String closedBrace;
   private FunctionCalls aggregateFunction;
   private String asStr;
   private String aliasName;
   private boolean isAS;
   private SelectQueryStatement subQuery;
   private FunctionCalls newFc;

   public void setAliasName(String an) {
      this.aliasName = an;
   }

   public void setIsAs(boolean is) {
      this.isAS = is;
   }

   public void setOpenBrace(String openBrace) {
      this.openBrace = openBrace;
   }

   public void setClosedBrace(String closedBrace) {
      this.closedBrace = closedBrace;
   }

   public void setAggregateFunction(FunctionCalls fc) {
      this.aggregateFunction = fc;
   }

   public void setPivot(String pivotStr) {
      this.pivotStr = pivotStr;
   }

   public void setPivotColumn(TableColumn tc) {
      this.pivotColumn = tc;
   }

   public void setUnPivotColumn(TableColumn tc) {
      this.unpivotColumn = tc;
   }

   public void setForStr(String forStr) {
      this.forStr = forStr;
   }

   public void setInClause(WhereItem wi) {
      this.inClause = wi;
   }

   public String getAliasName() {
      return this.aliasName;
   }

   public boolean getIsAs() {
      return this.isAS;
   }

   public String getOpenBrace() {
      return this.openBrace;
   }

   public String getClosedBrace() {
      return this.closedBrace;
   }

   public FunctionCalls getAggregateFunction() {
      return this.aggregateFunction;
   }

   public String getPivot() {
      return this.pivotStr;
   }

   public TableColumn getPivotColumn() {
      return this.pivotColumn;
   }

   public TableColumn getUnPivotColumn() {
      return this.unpivotColumn;
   }

   public String getForStr() {
      return this.forStr;
   }

   public WhereItem getInClause() {
      return this.inClause;
   }

   public void setSubQuery(SelectQueryStatement selectQueryStatement) {
      this.subQuery = selectQueryStatement;
   }

   public void toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.newFc = this.getAggregateFunction().toOracleSelect(to_sqs, from_sqs);
      new ArrayList();
      new ArrayList();
      new ArrayList();
      new Vector();
      SelectStatement newST = new SelectStatement();
      newST.setSelectClause("SELECT");
      ArrayList inClauseColumns = this.getInClauseColumns();
      ArrayList selectItems = this.getSelectColumns(from_sqs);
      ArrayList subQuerySelectItems = this.getSelectColumns(this.subQuery);
      Vector newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
      newST.setSelectItemList(newSelectItemList);
      GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
      if (gbs != null) {
         to_sqs.setGroupByStatement(gbs.toOracleSelect(to_sqs, from_sqs));
      }

      to_sqs.setSelectStatement(newST.toOracleSelect(to_sqs, from_sqs));
   }

   public void toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.newFc = this.getAggregateFunction().toDB2Select(to_sqs, from_sqs);
      new ArrayList();
      new ArrayList();
      new ArrayList();
      new Vector();
      SelectStatement newST = new SelectStatement();
      newST.setSelectClause("SELECT");
      ArrayList inClauseColumns = this.getInClauseColumns();
      ArrayList selectItems = this.getSelectColumns(from_sqs);
      ArrayList subQuerySelectItems = this.getSelectColumns(this.subQuery);
      Vector newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
      newST.setSelectItemList(newSelectItemList);
      GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
      if (gbs != null) {
         to_sqs.setGroupByStatement(gbs.toDB2Select(to_sqs, from_sqs));
      }

      to_sqs.setSelectStatement(newST.toDB2Select(to_sqs, from_sqs));
   }

   public void toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.newFc = this.getAggregateFunction().toInformixSelect(to_sqs, from_sqs);
      new ArrayList();
      new ArrayList();
      new ArrayList();
      new Vector();
      SelectStatement newST = new SelectStatement();
      newST.setSelectClause("SELECT");
      ArrayList inClauseColumns = this.getInClauseColumns();
      ArrayList selectItems = this.getSelectColumns(from_sqs);
      ArrayList subQuerySelectItems = this.getSelectColumns(this.subQuery);
      Vector newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
      newST.setSelectItemList(newSelectItemList);
      GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
      if (gbs != null) {
         to_sqs.setGroupByStatement(gbs.toInformixSelect(to_sqs, from_sqs));
      }

      to_sqs.setSelectStatement(newST.toInformixSelect(to_sqs, from_sqs));
   }

   public void toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.newFc = this.getAggregateFunction().toPostgreSQLSelect(to_sqs, from_sqs);
      new ArrayList();
      new ArrayList();
      new ArrayList();
      new Vector();
      SelectStatement newST = new SelectStatement();
      newST.setSelectClause("SELECT");
      ArrayList inClauseColumns = this.getInClauseColumns();
      ArrayList selectItems = this.getSelectColumns(from_sqs);
      ArrayList subQuerySelectItems = this.getSelectColumns(this.subQuery);
      Vector newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
      newST.setSelectItemList(newSelectItemList);
      GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
      if (gbs != null) {
         to_sqs.setGroupByStatement(gbs.toPostgreSQLSelect(to_sqs, from_sqs));
      }

      to_sqs.setSelectStatement(newST.toPostgreSQLSelect(to_sqs, from_sqs));
   }

   public void toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, FromTable ft) throws ConvertException {
      this.aliasName = removeFirstAndLastCharsIfStrIsEnclosedAndAddBackTip(this.aliasName);
      SelectQueryStatement pivotSubQuery;
      ArrayList inClauseColumnsAsStr;
      if (this.pivotStr.equalsIgnoreCase("PIVOT")) {
         ArrayList<Integer> groupbyItemsColIndex = new ArrayList();
         pivotSubQuery = this.validatePivotClauseAndGetFinalSQS(from_sqs, ft, groupbyItemsColIndex);
         inClauseColumnsAsStr = this.getInClauseColumnsAsString(from_sqs, false);
         this.getNewPivotedSelectAndGroupByItems(pivotSubQuery, to_sqs, from_sqs, inClauseColumnsAsStr);
         GroupByStatement localGroupByStatement = this.generateGroupByStmtFromSelectItems(groupbyItemsColIndex);
         if (localGroupByStatement != null) {
            pivotSubQuery.setGroupByStatement(localGroupByStatement);
         }

         ft.setAliasName(this.aliasName);
         pivotSubQuery.setPivotOperatorColumnList(this.getPivotResColAsString());
      } else if (this.pivotStr.equalsIgnoreCase("UNPIVOT")) {
         Map<String, String> colNameAlias = new HashMap();
         pivotSubQuery = this.validateUnPivotClauseAndGetFinalSQS(from_sqs, ft, colNameAlias);
         inClauseColumnsAsStr = this.getInClauseColumnsAsString(from_sqs, true);
         SelectQueryStatement unionSQSQuery = this.createUnionQueryFromPivotWhereInClause(inClauseColumnsAsStr, 0);
         FromTable unionFromTable = new FromTable();
         unionFromTable.setTableName(unionSQSQuery);
         unionFromTable.setIsAS(true);
         unionFromTable.setAliasName("__UnPivotTbl__");
         unionSQSQuery.setIsUnPivotUnionSQS(true);
         FromClause unPivotSubQueryFromCl = pivotSubQuery.getFromClause();
         unPivotSubQueryFromCl.fromItemList.addElement(unionFromTable);
         ft.setAliasName(this.aliasName);
         ArrayList inClauseColumns = this.getInClauseColumns();
         this.getNewUnPivotedSelectItems(pivotSubQuery, to_sqs, from_sqs, inClauseColumns, inClauseColumnsAsStr, colNameAlias);
         this.addWhereConditionForUnPivot(pivotSubQuery, to_sqs, from_sqs, inClauseColumns, inClauseColumnsAsStr, colNameAlias);
         pivotSubQuery.setPivotOperatorColumnList(this.getUnPivotResColAsString());
      }

      ft.setPivotClause((PivotClause)null);
   }

   public void toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.newFc = this.getAggregateFunction().toANSISelect(to_sqs, from_sqs);
      new ArrayList();
      new ArrayList();
      new ArrayList();
      new Vector();
      SelectStatement newST = new SelectStatement();
      newST.setSelectClause("SELECT");
      ArrayList inClauseColumns = this.getInClauseColumns();
      ArrayList selectItems = this.getSelectColumns(from_sqs);
      ArrayList subQuerySelectItems = this.getSelectColumns(this.subQuery);
      Vector newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
      newST.setSelectItemList(newSelectItemList);
      GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
      if (gbs != null) {
         to_sqs.setGroupByStatement(gbs.toANSISelect(to_sqs, from_sqs));
      }

      to_sqs.setSelectStatement(newST.toANSISelect(to_sqs, from_sqs));
   }

   public void toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.newFc = this.getAggregateFunction().toSybaseSelect(to_sqs, from_sqs);
      new ArrayList();
      new ArrayList();
      new ArrayList();
      new Vector();
      SelectStatement newST = new SelectStatement();
      newST.setSelectClause("SELECT");
      ArrayList inClauseColumns = this.getInClauseColumns();
      ArrayList selectItems = this.getSelectColumns(from_sqs);
      ArrayList subQuerySelectItems = this.getSelectColumns(this.subQuery);
      Vector newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
      newST.setSelectItemList(newSelectItemList);
      GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
      if (gbs != null) {
         to_sqs.setGroupByStatement(gbs.toSybaseSelect(to_sqs, from_sqs));
      }

      to_sqs.setSelectStatement(newST.toSybaseSelect(to_sqs, from_sqs));
   }

   public void toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.newFc = this.getAggregateFunction().toNetezzaSelect(to_sqs, from_sqs);
      new ArrayList();
      new ArrayList();
      new ArrayList();
      new Vector();
      SelectStatement newST = new SelectStatement();
      newST.setSelectClause("SELECT");
      ArrayList inClauseColumns = this.getInClauseColumns();
      ArrayList selectItems = this.getSelectColumns(from_sqs);
      ArrayList subQuerySelectItems = this.getSelectColumns(this.subQuery);
      Vector newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
      newST.setSelectItemList(newSelectItemList);
      GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
      if (gbs != null) {
         to_sqs.setGroupByStatement(gbs.toNetezzaSelect(to_sqs, from_sqs));
      }

      to_sqs.setSelectStatement(newST.toNetezzaSelect(to_sqs, from_sqs));
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.pivotStr != null) {
         sb.append("\n" + this.pivotStr + "\n");
      }

      if (this.openBrace != null) {
         sb.append(this.openBrace);
      }

      if (this.aggregateFunction != null) {
         sb.append(this.aggregateFunction.toString() + " ");
      }

      if (this.unpivotColumn != null) {
         sb.append(this.unpivotColumn.toString() + " ");
      }

      if (this.forStr != null) {
         sb.append(this.forStr + " ");
      }

      if (this.pivotColumn != null) {
         sb.append(this.pivotColumn.toString() + " ");
      }

      if (this.inClause != null) {
         sb.append(this.inClause.toString() + "\n");
      }

      if (this.closedBrace != null) {
         sb.append(this.closedBrace);
      }

      if (this.isAS) {
         sb.append(" AS ");
      }

      if (this.aliasName != null) {
         sb.append(this.aliasName);
      }

      return sb.toString();
   }

   private ArrayList getInClauseColumns() {
      ArrayList inClauseColumns = new ArrayList();
      Vector whereColumns = this.inClause.getRightWhereExp().getColumnExpression();

      for(int i = 0; i < whereColumns.size(); ++i) {
         if (whereColumns.get(i) instanceof WhereColumn) {
            WhereColumn wc = (WhereColumn)whereColumns.get(i);
            Vector pivotColumns = wc.getColumnExpression();
            TableColumn tc = (TableColumn)pivotColumns.get(0);
            inClauseColumns.add(tc);
         }
      }

      return inClauseColumns;
   }

   private ArrayList getSelectColumns(SelectQueryStatement from_sqs) {
      ArrayList colList = new ArrayList();
      SelectStatement st = from_sqs.getSelectStatement();
      Vector selectItemsList = st.getSelectItemList();

      for(int i = 0; i < selectItemsList.size(); ++i) {
         if (selectItemsList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)selectItemsList.get(i);
            colList.add(sc);
         }
      }

      return colList;
   }

   private Vector getNewSelectItems(ArrayList inClauseColumns, ArrayList selectItems, ArrayList subQuerySelectItems) throws ConvertException {
      Vector newSelectItems = new Vector();
      String alias = null;
      String cilumnName = null;

      for(int i = 0; i < selectItems.size(); ++i) {
         SelectColumn sc = (SelectColumn)selectItems.get(i);
         alias = sc.getAliasName();
         Vector colExp = sc.getColumnExpression();
         if (colExp.get(0) instanceof String) {
            newSelectItems.add(sc);
         } else if (colExp.get(0) instanceof TableColumn) {
            SelectColumn scTemp = this.generateNewSelectColumn(sc, subQuerySelectItems, inClauseColumns);
            this.setaliasForSelectColumn(scTemp, sc);
            newSelectItems.add(scTemp);
         }
      }

      return newSelectItems;
   }

   private SelectColumn generateNewSelectColumn(SelectColumn sc, ArrayList subQuerySelectItems, ArrayList inClauseColumns) throws ConvertException {
      SelectColumn newSc = new SelectColumn();
      newSc.setAliasName(sc.getAliasName());
      newSc.setIsAS(sc.getIsAS());
      newSc.setEndsWith(sc.getEndsWith());
      Vector colExp = sc.getColumnExpression();
      Vector newColExp = new Vector();

      for(int i = 0; i < colExp.size(); ++i) {
         Object obj = colExp.get(i);
         if (obj instanceof TableColumn) {
            TableColumn tc = (TableColumn)obj;
            boolean presentInSubQuery = this.changeColumnIfPresentInSubQueryItemsList(newColExp, subQuerySelectItems, tc);
            if (!presentInSubQuery) {
               this.changeColumnIfPresentInInClause(newColExp, inClauseColumns, tc);
            }
         } else {
            newColExp.add(obj);
         }
      }

      newSc.setColumnExpression(newColExp);
      return newSc;
   }

   private boolean changeColumnIfPresentInSubQueryItemsList(Vector cols, ArrayList subQuerySelectItems, TableColumn tc) {
      String colName = tc.getColumnName().trim();

      for(int i = 0; i < subQuerySelectItems.size(); ++i) {
         SelectColumn scTemp = (SelectColumn)subQuerySelectItems.get(i);
         String aliasName = scTemp.getAliasName();
         if (aliasName != null && (aliasName.startsWith("\"") || aliasName.startsWith("[") || aliasName.startsWith("`"))) {
            aliasName = aliasName.substring(1, aliasName.length() - 1);
         }

         if (colName.startsWith("\"") || colName.startsWith("[") || colName.startsWith("`")) {
            colName = colName.substring(1, colName.length() - 1);
         }

         if (colName.equalsIgnoreCase(aliasName)) {
            cols.add(scTemp.getColumnExpression().get(0));
            return true;
         }
      }

      return false;
   }

   private void changeColumnIfPresentInInClause(Vector newColExp, ArrayList inClauseColumns, TableColumn tc) throws ConvertException {
      String colName = tc.getColumnName();
      if (colName.startsWith("\"") || colName.startsWith("[") || colName.startsWith("`")) {
         colName = colName.substring(1, colName.length() - 1).trim();
      }

      for(int j = 0; j < inClauseColumns.size(); ++j) {
         TableColumn tcNew = (TableColumn)inClauseColumns.get(j);
         String inColName = tcNew.getColumnName();
         if (inColName.startsWith("\"") || inColName.startsWith("[") || inColName.startsWith("`")) {
            inColName = inColName.substring(1, inColName.length() - 1).trim();
         }

         if (colName.equalsIgnoreCase(inColName)) {
            newColExp.add(this.generateCaseStatementWithPivotedColumn(inColName));
         }
      }

   }

   private FunctionCalls generateCaseStatementWithPivotedColumn(String inColName) throws ConvertException {
      FunctionCalls fc = new FunctionCalls();
      new Vector();
      CaseStatement caseStmt = new CaseStatement();
      SelectColumn sc = (SelectColumn)this.aggregateFunction.getFunctionArguments().get(0);
      TableColumn thenCol = (TableColumn)((TableColumn)sc.getColumnExpression().elementAt(0));
      thenCol.setTableName((String)null);
      thenCol.setIsPivotColumn(true);
      SelectColumn thenSelCol = new SelectColumn();
      Vector thenVC = new Vector(1);
      thenVC.add(thenCol);
      thenSelCol.setColumnExpression(thenVC);
      Vector whenStmtList = new Vector();
      WhenStatement whenStmt = new WhenStatement();
      WhereExpression whenConditionWE = new WhereExpression();
      Vector whenConditionVector = new Vector();
      WhereItem whenWhereItem = new WhereItem();
      WhereColumn whenLeftWC = new WhereColumn();
      Vector whenColExp = new Vector();
      WhereColumn rightWhereExp = new WhereColumn();
      Vector rightWhereColExp = new Vector();
      SelectColumn thenStmt = new SelectColumn();
      Vector thenColExp = new Vector();
      SelectColumn elseStmt = new SelectColumn();
      Vector elseColExp = new Vector();
      String elseExpValue = "null";
      caseStmt.setCaseClause("CASE");
      whenStmt.setWhenClause("WHEN");
      whenStmt.setThenClause("THEN");
      caseStmt.setEndClause("END");
      this.pivotColumn.setTableName((String)null);
      this.pivotColumn.setIsPivotColumn(true);
      whenColExp.add(this.pivotColumn);
      whenLeftWC.setColumnExpression(whenColExp);
      whenWhereItem.setLeftWhereExp(whenLeftWC);
      whenConditionVector.add(whenWhereItem);
      whenConditionWE.setWhereItem(whenConditionVector);
      whenStmt.setWhenCondition(whenConditionWE);
      rightWhereColExp.add(inColName);
      rightWhereExp.setColumnExpression(rightWhereColExp);
      whenWhereItem.setRightWhereExp(rightWhereExp);
      whenWhereItem.setOperator("=");
      thenColExp.add(thenSelCol);
      thenStmt.setColumnExpression(thenColExp);
      whenStmt.setThenStatement(thenStmt);
      whenStmtList.add(whenStmt);
      elseColExp.add(elseExpValue);
      elseStmt.setColumnExpression(elseColExp);
      caseStmt.setWhenStatementList(whenStmtList);
      caseStmt.setElseClause("ELSE");
      caseStmt.setElseStatement(elseStmt);
      SelectColumn argument = new SelectColumn();
      Vector colExp = new Vector();
      colExp.add(caseStmt);
      argument.setColumnExpression(colExp);
      Vector arguments = new Vector();
      arguments.add(argument);
      fc.setFunctionArguments(arguments);
      fc.setFunctionName(this.getAggregateFunction().getFunctionName());
      return fc;
   }

   private void setaliasForSelectColumn(SelectColumn scTemp, SelectColumn originalSC) {
      String alias = scTemp.getAliasName();
      if (alias == null || alias.equals("")) {
         Vector colExp = originalSC.getColumnExpression();
         TableColumn tc = (TableColumn)colExp.get(0);
         scTemp.setAliasName(tc.getColumnName().trim());
      }

   }

   private GroupByStatement generateGroupByStatement(ArrayList subQuerySelectItems) {
      GroupByStatement gbs = new GroupByStatement();
      Vector functionArgs = this.getAggregateFunction().getFunctionArguments();
      String pivotedColumn = this.getPivotColumn().getColumnName().trim();
      String fArg = this.extractColumnNameFromSelectColumn((SelectColumn)functionArgs.get(0));
      Vector groupByList = new Vector();

      for(int i = 0; i < subQuerySelectItems.size(); ++i) {
         if (subQuerySelectItems.get(i) instanceof SelectColumn) {
            SelectColumn scTemp = (SelectColumn)subQuerySelectItems.get(i);
            String colName = this.extractColumnNameFromSelectColumn(scTemp).trim();
            if (!colName.equalsIgnoreCase(fArg) && !colName.equalsIgnoreCase(pivotedColumn)) {
               SelectColumn newSC = new SelectColumn();
               newSC.setColumnExpression(scTemp.getColumnExpression());
               groupByList.add(newSC);
            }
         }
      }

      gbs.setGroupClause("GROUP BY ");
      if (groupByList.size() > 0) {
         gbs.setGroupByItemList(groupByList);
         return gbs;
      } else {
         return null;
      }
   }

   private String extractColumnNameFromSelectColumn(SelectColumn sc) {
      String colName = "";
      Vector colExp = sc.getColumnExpression();
      if (colExp.get(0) instanceof TableColumn) {
         TableColumn tc = (TableColumn)colExp.get(0);
         colName = tc.getColumnName().trim();
         if (colName.startsWith("\"") || colName.startsWith("[") || colName.startsWith("`")) {
            colName = colName.substring(1, colName.length() - 1);
         }
      }

      return colName;
   }

   public void toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.newFc = this.getAggregateFunction().toVectorWiseSelect(to_sqs, from_sqs);
      new ArrayList();
      new ArrayList();
      new ArrayList();
      new Vector();
      SelectStatement newST = new SelectStatement();
      newST.setSelectClause("SELECT");
      ArrayList inClauseColumns = this.getInClauseColumns();
      ArrayList selectItems = this.getSelectColumns(from_sqs);
      ArrayList subQuerySelectItems = this.getSelectColumns(this.subQuery);
      Vector newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
      newST.setSelectItemList(newSelectItemList);
      GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
      if (gbs != null) {
         to_sqs.setGroupByStatement(gbs.toVectorWiseSelect(to_sqs, from_sqs));
      }

      to_sqs.setSelectStatement(newST.toVectorWiseSelect(to_sqs, from_sqs));
   }

   public PivotClause toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, String srcAliasName, SelectQueryStatement ft) throws ConvertException {
      PivotClause pivotClConv = new PivotClause();
      String srcTblName = GeneralUtil.trimIfTblColIsEnclosed(((FromTable)ft.getFromClause().getFromItemList().get(0)).getTableName().toString());
      HashMap finalTblColDetailsMap = new HashMap();
      finalTblColDetailsMap.putAll((Map)CastingUtil.getValueIgnoreCase(from_sqs.getTableColumnDetsMap(), srcAliasName));
      HashMap tableColumnDetsMap = new HashMap();
      tableColumnDetsMap.putAll((Map)CastingUtil.getValueIgnoreCase(from_sqs.getQueryConvDataHandler().getTableColumnDetsMap(), srcTblName));
      tableColumnDetsMap.putAll((HashMap)CastingUtil.getValueIgnoreCase(from_sqs.getTableColumnDetsMap(), srcAliasName));
      from_sqs.addTableColumnDets(srcAliasName, tableColumnDetsMap);
      pivotClConv.setPivot(this.getPivot());
      pivotClConv.setForStr(this.getForStr());
      String aliasName;
      String colName;
      if (this.getPivot().equalsIgnoreCase("PIVOT")) {
         if (this.pivotColumn != null) {
            pivotClConv.setPivotColumn(this.getPivotColumn().toReplaceTblCol(to_sqs, from_sqs));
            aliasName = GeneralUtil.trimIfTblColIsEnclosed(this.getPivotColumn().getColumnName());
            if (aliasName != null) {
               finalTblColDetailsMap.remove(aliasName);
            }
         }

         if (this.aggregateFunction != null) {
            pivotClConv.setAggregateFunction(this.getAggregateFunction().toReplaceTblCol(to_sqs, from_sqs));
            SelectColumn aggCol = (SelectColumn)this.aggregateFunction.getFunctionArguments().get(0);
            if (aggCol.getColumnExpression().get(0) instanceof TableColumn) {
               colName = GeneralUtil.trimIfTblColIsEnclosed(((TableColumn)aggCol.getColumnExpression().get(0)).getColumnName());
               if (colName != null) {
                  finalTblColDetailsMap.remove(colName);
               }
            }
         }
      } else if (this.getPivot().equalsIgnoreCase("UNPIVOT")) {
         TableColumn upc;
         if (this.pivotColumn != null) {
            upc = new TableColumn();
            colName = this.getPivotColumn().getColumnName();
            upc.setColumnName(colName);
            pivotClConv.setPivotColumn(upc);
            colName = GeneralUtil.trimIfTblColIsEnclosed(colName);
            finalTblColDetailsMap.put(colName, colName);
         }

         if (this.unpivotColumn != null) {
            upc = new TableColumn();
            colName = this.getUnPivotColumn().getColumnName();
            upc.setColumnName(colName);
            pivotClConv.setUnPivotColumn(upc);
            colName = GeneralUtil.trimIfTblColIsEnclosed(colName);
            finalTblColDetailsMap.put(colName, colName);
         }

         to_sqs.getSelColNameMap().putAll((Map)CastingUtil.getValueIgnoreCase(from_sqs.getQueryConvDataHandler().getTableColumnDetsMap(), srcTblName));
         to_sqs.getSelColNameMap().putAll(finalTblColDetailsMap);
      }

      if (this.inClause != null) {
         pivotClConv.setInClause(this.toReplaceInClauseForPivot(to_sqs, from_sqs, finalTblColDetailsMap));
      }

      if (this.subQuery != null) {
         pivotClConv.setSubQuery(this.subQuery.toReplaceTblCol());
      }

      pivotClConv.setOpenBrace("(");
      pivotClConv.setClosedBrace(")");
      pivotClConv.asStr = this.asStr;
      pivotClConv.setAliasName(this.getAliasName());
      pivotClConv.setIsAs(this.getIsAs());
      pivotClConv.newFc = this.newFc;
      aliasName = GeneralUtil.trimIfAliasNameIsEnclosed(this.getAliasName());
      from_sqs.addTableColumnDets(aliasName, finalTblColDetailsMap);
      from_sqs.addTableDets(aliasName, aliasName);
      from_sqs.getTableColumnDetsMap().remove(srcAliasName);
      from_sqs.getTableDetsMap().remove(srcAliasName);
      return pivotClConv;
   }

   private Map validateInClauseColumns(String tblAliasName, SelectQueryStatement from_sqs) throws ConvertException {
      Map<String, Integer> inClauseColAsStr = new HashMap();
      Vector whereColumns = this.inClause.getRightWhereExp().getColumnExpression();
      int whereColSize = whereColumns.size();
      int count = 0;

      int i;
      for(i = 0; i < whereColSize; ++i) {
         if (whereColumns.get(i) instanceof WhereColumn) {
            ++count;
            WhereColumn wc = (WhereColumn)whereColumns.get(i);
            Vector pivotColumns = wc.getColumnExpression();
            if (!(pivotColumns.elementAt(0) instanceof TableColumn)) {
               throw new ConvertException("PIVOT_INCLAUSE_VALUE_WITH_EXPR", "PIVOT_INCLAUSE_VALUE_WITH_EXPR", new Object[]{this.pivotStr});
            }

            TableColumn tc = (TableColumn)pivotColumns.get(0);
            if (this.pivotStr.equalsIgnoreCase("pivot") && tc.getTableName() != null) {
               String tblColName = removeFirstAndLastCharsIfStrIsEnclosed(tc.getTableName()) + "." + removeFirstAndLastCharsIfStrIsEnclosed(tc.getColumnName());
               throw new ConvertException("PIVOT_INVALID_INCLAUSE_VALUE", "PIVOT_INVALID_INCLAUSE_VALUE", new Object[]{tblColName});
            }

            if (this.pivotStr.equalsIgnoreCase("unpivot")) {
               this.validateTblColWithAliasName(tc, tblAliasName, removeFirstAndLastCharsIfStrIsEnclosed(tc.getColumnName()));
            }

            if (inClauseColAsStr.containsKey(removeFirstAndLastCharsIfStrIsEnclosed(tc.getColumnName().toLowerCase()))) {
               throw new ConvertException("PIVOT_DUPLICATE_INCLAUSE_VALUE", "PIVOT_DUPLICATE_INCLAUSE_VALUE", new Object[]{this.pivotStr, tc.getColumnName()});
            }

            inClauseColAsStr.put(removeFirstAndLastCharsIfStrIsEnclosed(tc.getColumnName().toLowerCase()), i);
         }
      }

      if (from_sqs.getQueryConvDataHandler() != null && from_sqs.getQueryConvDataHandler().getPivotClauseInValuesLimit() != null) {
         i = from_sqs.getQueryConvDataHandler().getPivotClauseInValuesLimit();
         if (count > i) {
            throw new ConvertException("PIVOT_EXCEEDS_MAX_INCLAUSE_VALUE", "PIVOT_EXCEEDS_MAX_INCLAUSE_VALUE", new Object[]{this.pivotStr, i});
         }
      }

      return inClauseColAsStr;
   }

   private void validateTblColWithAliasName(TableColumn tbCl, String srcTblAlias, String colName) throws ConvertException {
      if (tbCl.getTableName() != null) {
         String tblName = removeFirstAndLastCharsIfStrIsEnclosed(tbCl.getTableName());
         if (!tblName.equalsIgnoreCase(srcTblAlias)) {
            throw new ConvertException("Invalid column found ", "PIVOT_COLUMN_NAME_WITHOUT_ALIAS");
         }
      } else {
         throw new ConvertException("Invalid column found ", "PIVOT_COLUMN_NAME_WITHOUT_ALIAS");
      }
   }

   private SelectQueryStatement validatePivotClauseAndGetFinalSQS(SelectQueryStatement from_sqs, FromTable ft, ArrayList<Integer> groupByItemsIndex) throws ConvertException {
      SelectQueryStatement subQuerySQS = null;
      if (ft == null) {
         throw new ConvertException("PIVOT_TBLSRC_WITHOUT_FROM_CLAUSE", "PIVOT_TBLSRC_WITHOUT_FROM_CLAUSE", new Object[]{this.pivotStr});
      } else if (ft.getTableName() instanceof SelectQueryStatement) {
         if (ft.getAliasName() == null) {
            throw new ConvertException("PIVOT_INVALID_ALIAS_NAME", "PIVOT_INVALID_ALIAS_NAME");
         } else {
            subQuerySQS = (SelectQueryStatement)ft.getTableName();
            String tblAliasName = removeFirstAndLastCharsIfAliasNameIsEnclosed(ft.getAliasName());
            FromClause fromClause = subQuerySQS.getFromClause();
            if (fromClause.fromItemList.size() > 1) {
               throw new ConvertException("PIVOT_TBLSRC_WITH_MULTI_TABLES", "PIVOT_TBLSRC_WITH_MULTI_TABLES", new Object[]{this.pivotStr});
            } else if (subQuerySQS.getGroupByStatement() != null) {
               throw new ConvertException("PIVOT_TBLSRC_WITH_GROUPBY", "PIVOT_TBLSRC_WITH_GROUPBY");
            } else if (subQuerySQS.getHavingStatement() != null) {
               throw new ConvertException("PIVOT_TBLSRC_WITH_HAVING", "PIVOT_TBLSRC_WITH_HAVING");
            } else if (subQuerySQS.getOrderByStatement() != null) {
               throw new ConvertException("PIVOT_TBLSRC_WITH_ORDERBY", "PIVOT_TBLSRC_WITH_ORDERBY", new Object[]{this.pivotStr});
            } else if (subQuerySQS.getLimitClause() != null) {
               throw new ConvertException("PIVOT_TBLSRC_WITH_LIMITCLAUSE", "PIVOT_TBLSRC_WITH_LIMITCLAUSE", new Object[]{this.pivotStr});
            } else {
               SelectStatement selStmt = subQuerySQS.getSelectStatement();
               Vector selItemList = selStmt.getSelectItemList();
               if (from_sqs.getQueryConvDataHandler() != null && from_sqs.getQueryConvDataHandler().getPivotOperatorSelItemsLimit() != null) {
                  int selItemLimit = from_sqs.getQueryConvDataHandler().getPivotOperatorSelItemsLimit();
                  if (selItemList.size() > selItemLimit) {
                     throw new ConvertException("PIVOT_TBLSRC_WITH_MAX_SELCOLS", "PIVOT_TBLSRC_WITH_MAX_SELCOLS", new Object[]{selItemLimit});
                  }
               }

               TableColumn aggTblCol = null;
               String pivotColName;
               if (from_sqs.getValidationHandler() != null) {
                  FunctionCalls aggFunCl = this.getAggregateFunction();
                  pivotColName = aggFunCl.getFunctionName().getColumnName().toUpperCase();
                  if (from_sqs.getValidationHandler().getPivotAggFunctions() != null) {
                     ArrayList fnDetails = (ArrayList)from_sqs.getValidationHandler().getPivotAggFunctions();
                     if (fnDetails != null && !fnDetails.contains(pivotColName)) {
                        throw new ConvertException("PIVOT_INVALID_AGG_FUN", "PIVOT_INVALID_AGG_FUN", new Object[]{pivotColName});
                     }
                  }

                  Vector functionArgs = aggFunCl.getFunctionArguments();
                  Object tblCol = ((SelectColumn)functionArgs.elementAt(0)).getColumnExpression().elementAt(0);
                  if (functionArgs.size() != 1 || !(tblCol instanceof TableColumn)) {
                     throw new ConvertException("PIVOT_INVALID_AGG_FUN_EXPR", "PIVOT_INVALID_AGG_FUN_EXPR", new Object[]{pivotColName});
                  }

                  aggTblCol = (TableColumn)tblCol;
               } else {
                  aggTblCol = (TableColumn)((SelectColumn)this.getAggregateFunction().getFunctionArguments().elementAt(0)).getColumnExpression().elementAt(0);
               }

               String aggColName = removeFirstAndLastCharsIfStrIsEnclosed(aggTblCol.getColumnName());
               pivotColName = removeFirstAndLastCharsIfStrIsEnclosed(this.pivotColumn.getColumnName());
               this.validateTblColWithAliasName(aggTblCol, tblAliasName, aggColName);
               this.validateTblColWithAliasName(this.pivotColumn, tblAliasName, pivotColName);
               this.validateInClauseColumns(tblAliasName, from_sqs);
               int countOfAggTblCol = 0;
               int countOfPivotCol = 0;
               int index = 0;

               while(true) {
                  while(true) {
                     while(true) {
                        while(true) {
                           do {
                              if (index >= selItemList.size()) {
                                 if (countOfPivotCol == 0) {
                                    throw new ConvertException("PIVOT_TBLSRC_WITHOUT_PIVOTTING_COLUMN", "PIVOT_TBLSRC_WITHOUT_PIVOTTING_COLUMN", new Object[]{pivotColName});
                                 }

                                 if (countOfAggTblCol == 0) {
                                    throw new ConvertException("PIVOT_TBLSRC_WITHOUT_PIVOTTING_DATA_COLUMN", "PIVOT_TBLSRC_WITHOUT_PIVOTTING_DATA_COLUMN", new Object[]{aggColName});
                                 }

                                 return subQuerySQS;
                              }
                           } while(!(selItemList.get(index) instanceof SelectColumn));

                           SelectColumn scTemp = (SelectColumn)selItemList.get(index);
                           if (scTemp.hasStarInSelectColumn()) {
                              throw new ConvertException("PIVOT_TBLSRC_WITH_STAR_CLAUSE", "PIVOT_TBLSRC_WITH_STAR_CLAUSE", new Object[]{this.pivotStr});
                           }

                           if (scTemp.getColumnExpression().size() == 1 && scTemp.getColumnExpression().elementAt(0) instanceof TableColumn) {
                              TableColumn tbClTemp = (TableColumn)scTemp.getColumnExpression().elementAt(0);
                              String aliasName = scTemp.getAliasName() != null ? removeFirstAndLastCharsIfAliasNameIsEnclosed(scTemp.getAliasName()) : "";
                              String colName = tbClTemp.getColumnName() != null ? removeFirstAndLastCharsIfStrIsEnclosed(tbClTemp.getColumnName()) : "";
                              String tblColName = scTemp.getAliasName() != null ? removeFirstAndLastCharsIfAliasNameIsEnclosed(scTemp.getAliasName()) : removeFirstAndLastCharsIfStrIsEnclosed(tbClTemp.getTableName()) + "." + colName;
                              if (!aliasName.equalsIgnoreCase(aggColName) && !colName.equalsIgnoreCase(aggColName) && !tblColName.equalsIgnoreCase(aggColName)) {
                                 if (!aliasName.equalsIgnoreCase(pivotColName) && !colName.equalsIgnoreCase(pivotColName) && !tblColName.equalsIgnoreCase(pivotColName)) {
                                    groupByItemsIndex.add(index);
                                    ++index;
                                 } else {
                                    ++countOfPivotCol;
                                    selItemList.removeElementAt(index);
                                    if (scTemp.getAliasName() != null || tblColName.equalsIgnoreCase(pivotColName)) {
                                       this.pivotColumn.setColumnName(tbClTemp.getColumnName());
                                    }
                                 }
                              } else {
                                 ++countOfAggTblCol;
                                 selItemList.removeElementAt(index);
                                 if (scTemp.getAliasName() != null || tblColName.equalsIgnoreCase(aggColName)) {
                                    aggTblCol.setColumnName(tbClTemp.getColumnName());
                                 }
                              }
                           } else {
                              groupByItemsIndex.add(index);
                              ++index;
                           }
                        }
                     }
                  }
               }
            }
         }
      } else {
         throw new ConvertException("PIVOT_TBLSRC_WITHOUT_SUBQUERY", "PIVOT_TBLSRC_WITHOUT_SUBQUERY", new Object[]{this.pivotStr, ft.getTableName().toString()});
      }
   }

   private SelectQueryStatement validateUnPivotClauseAndGetFinalSQS(SelectQueryStatement from_sqs, FromTable ft, Map<String, String> colNameAlias) throws ConvertException {
      SelectQueryStatement subQuerySQS = null;
      if (ft == null) {
         throw new ConvertException("PIVOT_TBLSRC_WITHOUT_FROM_CLAUSE", "PIVOT_TBLSRC_WITHOUT_FROM_CLAUSE", new Object[]{this.pivotStr});
      } else if (!(ft.getTableName() instanceof SelectQueryStatement)) {
         throw new ConvertException("PIVOT_TBLSRC_WITHOUT_SUBQUERY", "PIVOT_TBLSRC_WITHOUT_SUBQUERY", new Object[]{this.pivotStr, ft.getTableName().toString()});
      } else if (ft.getAliasName() == null) {
         throw new ConvertException("PIVOT_INVALID_ALIAS_NAME", "PIVOT_INVALID_ALIAS_NAME");
      } else {
         String tblAliasName = removeFirstAndLastCharsIfAliasNameIsEnclosed(ft.getAliasName());
         subQuerySQS = (SelectQueryStatement)ft.getTableName();
         FromClause fromClause = subQuerySQS.getFromClause();
         if (fromClause != null && fromClause.fromItemList.size() > 1) {
            throw new ConvertException("PIVOT_TBLSRC_WITH_MULTI_TABLES", "PIVOT_TBLSRC_WITH_MULTI_TABLES", new Object[]{this.pivotStr});
         } else if (subQuerySQS.getOrderByStatement() != null) {
            throw new ConvertException("PIVOT_TBLSRC_WITH_ORDERBY", "PIVOT_TBLSRC_WITH_ORDERBY", new Object[]{this.pivotStr});
         } else if (subQuerySQS.getLimitClause() != null) {
            throw new ConvertException("PIVOT_TBLSRC_WITH_LIMITCLAUSE", "PIVOT_TBLSRC_WITH_LIMITCLAUSE", new Object[]{this.pivotStr});
         } else {
            SelectStatement selStmt = subQuerySQS.getSelectStatement();
            Vector selectItems = selStmt.getSelectItemList();
            if (this.unpivotColumn != null) {
               if (this.unpivotColumn.getTableName() != null) {
                  throw new ConvertException("PIVOT_INVALID_UNPIVOTTED_COLUMN", "PIVOT_INVALID_UNPIVOTTED_COLUMN", new Object[]{this.unpivotColumn.getColumnName()});
               }

               this.unpivotColumn.setColumnName(removeFirstAndLastCharsIfStrIsEnclosedAndAddBackTip(this.unpivotColumn.getColumnName()));
            }

            if (this.pivotColumn != null) {
               if (this.pivotColumn.getTableName() != null) {
                  throw new ConvertException("PIVOT_INVALID_UNPIVOTTED_COLUMN", "PIVOT_INVALID_UNPIVOTTED_COLUMN", new Object[]{this.pivotColumn.getColumnName()});
               }

               this.pivotColumn.setColumnName(removeFirstAndLastCharsIfStrIsEnclosedAndAddBackTip(this.pivotColumn.getColumnName()));
            }

            Map<String, Integer> inClauseValues = this.validateInClauseColumns(tblAliasName, from_sqs);
            int index = 0;

            while(true) {
               while(true) {
                  while(index < selectItems.size()) {
                     SelectColumn sc = (SelectColumn)selectItems.get(index);
                     if (sc.hasStarInSelectColumn()) {
                        throw new ConvertException("PIVOT_TBLSRC_WITH_STAR_CLAUSE", "PIVOT_TBLSRC_WITH_STAR_CLAUSE", new Object[]{this.pivotStr});
                     }

                     if (sc.getColumnExpression().size() == 1 && sc.getColumnExpression().get(0) instanceof TableColumn) {
                        TableColumn tblCol = (TableColumn)sc.getColumnExpression().get(0);
                        String aliasName = sc.getAliasName() != null ? removeFirstAndLastCharsIfAliasNameIsEnclosed(sc.getAliasName()).toLowerCase() : "";
                        String colName = tblCol.getColumnName() != null ? removeFirstAndLastCharsIfStrIsEnclosed(tblCol.getColumnName()).toLowerCase() : "";
                        String tblColName = sc.getAliasName() != null ? removeFirstAndLastCharsIfAliasNameIsEnclosed(sc.getAliasName()).toLowerCase() : (removeFirstAndLastCharsIfStrIsEnclosed(tblCol.getTableName()) + "." + colName).toLowerCase();
                        if (!inClauseValues.containsKey(aliasName) && !inClauseValues.containsKey(colName) && !inClauseValues.containsKey(tblColName)) {
                           ++index;
                        } else {
                           colName = inClauseValues.containsKey(aliasName) ? aliasName : (inClauseValues.containsKey(colName) ? colName : tblColName);
                           if (sc.getAliasName() != null || sc.getAliasForExpression() != null || inClauseValues.containsKey(tblColName)) {
                              int inClauseIndex = (Integer)inClauseValues.get(colName);
                              if (this.pivotStr.equalsIgnoreCase("unpivot") && from_sqs.getIsQtNewFlow()) {
                                 colNameAlias.put(colName, tblCol.getColumnName());
                              } else {
                                 this.replaceInClauseValueWithActualColumn(inClauseIndex, tblCol.getColumnName());
                              }
                           }

                           selectItems.remove(index);
                           inClauseValues.remove(colName);
                        }
                     } else {
                        ++index;
                     }
                  }

                  if (inClauseValues.size() != 0) {
                     String colValues = inClauseValues.keySet().toString();
                     colValues = colValues.substring(1, colValues.length() - 1);
                     throw new ConvertException("PIVOT_TBLSRC_WITHOUT_UNPIVOTING_COLUMN", "PIVOT_TBLSRC_WITHOUT_UNPIVOTING_COLUMN", new Object[]{colValues});
                  }

                  return subQuerySQS;
               }
            }
         }
      }
   }

   private void replaceInClauseValueWithActualColumn(Integer inClauseIndex, String colName) {
      Vector inClauseVC = this.inClause.getRightWhereExp().getColumnExpression();
      WhereColumn whCol = (WhereColumn)inClauseVC.get(inClauseIndex);
      TableColumn tbCl = (TableColumn)whCol.getColumnExpression().get(0);
      tbCl.setColumnName(colName);
   }

   private ArrayList<String> getPivotWhereInClauseColumns() {
      ArrayList<String> columnList = new ArrayList();
      String pivotTableAliasName = removeFirstAndLastCharsIfStrIsEnclosed(this.aliasName);
      Vector whereColumns = this.inClause.getRightWhereExp().getColumnExpression();

      for(int i = 0; i < whereColumns.size(); ++i) {
         if (whereColumns.get(i) instanceof WhereColumn) {
            WhereColumn wc = (WhereColumn)whereColumns.get(i);
            Vector inClauseCols = wc.getColumnExpression();
            if (inClauseCols.size() == 1 && inClauseCols.get(0) instanceof TableColumn) {
               TableColumn tc = (TableColumn)inClauseCols.get(0);
               String colName = removeFirstAndLastCharsIfStrIsEnclosed(tc.getColumnName());
               columnList.add(colName);
               columnList.add(pivotTableAliasName + "." + colName);
            }
         }
      }

      return columnList;
   }

   private ArrayList<ArrayList> getPivotRestrictedColList() {
      ArrayList<ArrayList> pivotRestrictedColList = new ArrayList();
      ArrayList<String> restrictedColList = new ArrayList();
      String aggCol = "";
      TableColumn aggregateCol = (TableColumn)((TableColumn)((SelectColumn)((SelectColumn)this.aggregateFunction.getFunctionArguments().elementAt(0))).getColumnExpression().elementAt(0));
      if (aggregateCol.getTableName() != null) {
         aggCol = removeFirstAndLastCharsIfStrIsEnclosed(aggregateCol.getTableName()) + ".";
      }

      aggCol = aggCol + removeFirstAndLastCharsIfStrIsEnclosed(aggregateCol.getColumnName());
      restrictedColList.add(aggCol);
      String pivotColName = "";
      if (this.pivotColumn.getTableName() != null) {
         pivotColName = removeFirstAndLastCharsIfStrIsEnclosed(this.pivotColumn.getTableName()) + ".";
      }

      pivotColName = pivotColName + removeFirstAndLastCharsIfStrIsEnclosed(this.pivotColumn.getColumnName());
      restrictedColList.add(pivotColName);
      pivotRestrictedColList.add(restrictedColList);
      ArrayList<String> standAloneColList = this.getPivotWhereInClauseColumns();
      pivotRestrictedColList.add(standAloneColList);
      return pivotRestrictedColList;
   }

   private ArrayList<ArrayList> getUnPivotRestrictedColList() {
      ArrayList<ArrayList> unPivotRestrictedColList = new ArrayList();
      ArrayList<String> restrictedColList = this.getPivotWhereInClauseColumns();
      unPivotRestrictedColList.add(restrictedColList);
      ArrayList<String> standAloneColList = new ArrayList();
      String pivotColName = removeFirstAndLastCharsIfStrIsEnclosed(this.unpivotColumn.getColumnName());
      standAloneColList.add(pivotColName);
      unPivotRestrictedColList.add(standAloneColList);
      return unPivotRestrictedColList;
   }

   private void getNewPivotedSelectAndGroupByItems(SelectQueryStatement pivotSubQuery, SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, ArrayList<String> inClauseColumnsAsStr) throws ConvertException {
      SelectStatement selStmt = pivotSubQuery.getSelectStatement();
      Vector selectItems = selStmt.getSelectItemList();
      if (selectItems.size() > 0) {
         SelectColumn lastCol = (SelectColumn)selectItems.get(selectItems.size() - 1);
         lastCol.setEndsWith(",");
      }

      int inClauseSize = inClauseColumnsAsStr.size();

      for(int i = 0; i < inClauseSize; ++i) {
         String inClauseVal = (String)inClauseColumnsAsStr.get(i);
         FunctionCalls aggFunCall = this.generateCaseStatementWithPivotedColumn(inClauseVal);
         if (aggFunCall != null) {
            SelectColumn sc = new SelectColumn();
            Vector colExp = new Vector(1);
            colExp.addElement(aggFunCall.toMySQLSelect(to_sqs, from_sqs));
            sc.setColumnExpression(colExp);
            sc.setIsAS("as");
            sc.setAliasName(inClauseVal);
            if (i + 1 != inClauseSize) {
               sc.setEndsWith(",");
            }

            selectItems.add(sc);
         }
      }

   }

   private GroupByStatement generateGroupByStmtFromSelectItems(ArrayList<Integer> groupbyItemsColIndex) {
      GroupByStatement gbs = new GroupByStatement();
      Vector groupByList = new Vector();

      for(int i = 0; i < groupbyItemsColIndex.size(); ++i) {
         SelectColumn newSC = new SelectColumn();
         Vector vc_newSC = new Vector(1);
         String ordinalNum = Integer.toString((Integer)groupbyItemsColIndex.get(i) + 1);
         vc_newSC.add(ordinalNum);
         newSC.setColumnExpression(vc_newSC);
         groupByList.add(newSC);
      }

      gbs.setGroupClause("GROUP BY ");
      if (groupByList.size() > 0) {
         gbs.setGroupByItemList(groupByList);
         return gbs;
      } else {
         return null;
      }
   }

   private ArrayList<String> getInClauseColumnsAsString(SelectQueryStatement fromSqs, boolean isUnpivot) throws ConvertException {
      ArrayList<String> inClauseColumns = new ArrayList();
      Vector whereColumns = this.inClause.getRightWhereExp().getColumnExpression();

      for(int i = 0; i < whereColumns.size(); ++i) {
         if (whereColumns.get(i) instanceof WhereColumn) {
            WhereColumn wc = (WhereColumn)whereColumns.get(i);
            Vector vc_unpivotColumns = wc.getColumnExpression();
            if (vc_unpivotColumns.size() == 1 && vc_unpivotColumns.get(0) instanceof TableColumn) {
               TableColumn tc = (TableColumn)vc_unpivotColumns.get(0);
               String colName = isUnpivot && fromSqs.getIsQtNewFlow() ? CastingUtil.getKeyForValueIgnoreCase(fromSqs.getSelColNameMap(), removeFirstAndLastCharsIfStrIsEnclosed(tc.getColumnName().trim())) : removeFirstAndLastCharsIfStrIsEnclosed(tc.getColumnName().trim());
               inClauseColumns.add("'" + colName.replace("\\", "\\\\").replace("'", "''") + "'");
            }
         }
      }

      return inClauseColumns;
   }

   private SelectQueryStatement createUnionQueryFromPivotWhereInClause(ArrayList inClauseColAsStr, int index) {
      SelectQueryStatement new_sqs = new SelectQueryStatement();
      SelectStatement new_selStmt = new SelectStatement();
      new_selStmt.setSelectClause("SELECT");
      SelectColumn new_selCol = new SelectColumn();
      Vector selColVCIn = new Vector(1);
      Vector selColVCOut = new Vector(1);
      selColVCIn.addElement(inClauseColAsStr.get(index));
      new_selCol.setColumnExpression(selColVCIn);
      if (index == 0) {
         new_selCol.setIsAS("AS");
         new_selCol.setAliasName(this.pivotColumn.getColumnName());
      }

      selColVCOut.addElement(new_selCol);
      new_selStmt.setSelectItemList(selColVCOut);
      new_sqs.setSelectStatement(new_selStmt);
      if (index + 1 < inClauseColAsStr.size()) {
         SetOperatorClause new_setOpCl = new SetOperatorClause();
         new_setOpCl.setSetClause("UNION");
         new_setOpCl.setSelectQueryStatement(this.createUnionQueryFromPivotWhereInClause(inClauseColAsStr, index + 1));
         new_sqs.setSetOperatorClause(new_setOpCl);
      }

      return new_sqs;
   }

   private void getNewUnPivotedSelectItems(SelectQueryStatement unPivotSubQuery, SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, ArrayList inClauseColumns, ArrayList inClauseColAsStr, Map<String, String> colNameAlias) {
      SelectStatement selStmt = unPivotSubQuery.getSelectStatement();
      Vector selectItems = selStmt.getSelectItemList();
      SelectColumn unPivotSC;
      if (selectItems.size() > 0) {
         unPivotSC = (SelectColumn)selectItems.get(selectItems.size() - 1);
         unPivotSC.setEndsWith(",");
      }

      unPivotSC = new SelectColumn();
      Vector unPivotVC = new Vector(1);
      TableColumn unPivotColValues = new TableColumn();
      unPivotColValues.setTableName("__UnPivotTbl__");
      String pivotColName = this.pivotColumn.getColumnName();
      unPivotColValues.setColumnName(pivotColName);
      unPivotVC.addElement(unPivotColValues);
      unPivotSC.setColumnExpression(unPivotVC);
      unPivotSC.setAliasName(pivotColName);
      unPivotSC.setEndsWith(",");
      selectItems.add(unPivotSC);
      SelectColumn unPivottedSC = new SelectColumn();
      Vector unPivottedVC = new Vector(1);
      String inClauseAliasName = ((FromTable)unPivotSubQuery.getFromClause().fromItemList.get(0)).getAliasName();
      CaseStatement caseStmt = this.createCaseStmtForUnpivot(to_sqs, from_sqs, inClauseColumns, inClauseColAsStr, inClauseAliasName, colNameAlias);
      unPivottedVC.add(caseStmt);
      unPivottedSC.setColumnExpression(unPivottedVC);
      unPivottedSC.setIsAS("as");
      unPivottedSC.setAliasName(this.unpivotColumn.getColumnName());
      selectItems.add(unPivottedSC);
   }

   private void addWhereConditionForUnPivot(SelectQueryStatement unpivotSubQuery, SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, ArrayList inClauseColumnsAsTblCol, ArrayList inClauseColAsStr, Map<String, String> colNameAlias) {
      WhereExpression whereExp = unpivotSubQuery.getWhereExpression();
      WhereItem whItem = new WhereItem();
      WhereColumn leftWhereExp = new WhereColumn();
      Vector vc_leftWhereExp = new Vector(1);
      String inClauseAliasName = ((FromTable)unpivotSubQuery.getFromClause().fromItemList.get(0)).getAliasName();
      vc_leftWhereExp.addElement(this.createCaseStmtForUnpivot(to_sqs, from_sqs, inClauseColumnsAsTblCol, inClauseColAsStr, inClauseAliasName, colNameAlias));
      leftWhereExp.setColumnExpression(vc_leftWhereExp);
      whItem.setLeftWhereExp(leftWhereExp);
      whItem.setOperator("IS NOT NULL");
      if (whereExp == null) {
         whereExp = new WhereExpression();
         Vector whItemList = new Vector(1);
         whItemList.addElement(whItem);
         whereExp.setWhereItem(whItemList);
      } else {
         whereExp.getWhereItem().addElement(whItem);
         whereExp.getOperator().addElement("AND");
      }

      unpivotSubQuery.setWhereExpression(whereExp);
   }

   private CaseStatement createCaseStmtForUnpivot(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, ArrayList inClauseColumns, ArrayList inClauseColAsStr, String inClauseTableAliasName, Map<String, String> colNameAlias) {
      CaseStatement caseStmt = new CaseStatement();
      String caseClause = "CASE";
      caseStmt.setCaseClause(caseClause);
      WhereExpression caseCondition = new WhereExpression();
      Vector caseCon_WhItemList = new Vector(1);
      WhereItem caseCon_WhItem = new WhereItem();
      WhereColumn caseCon_LeftWhExp = new WhereColumn();
      Vector caseCon_LeftWhColExp = new Vector(1);
      TableColumn pivottblCol = this.pivotColumn.toMySQLSelect(to_sqs, from_sqs);
      pivottblCol.setTableName("__UnPivotTbl__");
      caseCon_LeftWhColExp.addElement(pivottblCol);
      caseCon_LeftWhExp.setColumnExpression(caseCon_LeftWhColExp);
      caseCon_WhItem.setLeftWhereExp(caseCon_LeftWhExp);
      caseCon_WhItemList.addElement(caseCon_WhItem);
      caseCondition.setWhereItem(caseCon_WhItemList);
      caseStmt.setCaseCondition(caseCondition);
      Vector whenItemList = new Vector();
      int inClauseCol_Size = inClauseColumns.size();

      for(int i = 0; i < inClauseCol_Size; ++i) {
         WhenStatement whIt = new WhenStatement();
         String whenClause = "WHEN";
         whIt.setWhenClause(whenClause);
         WhereExpression whenCondition = new WhereExpression();
         Vector whenCon_WhItemList = new Vector(1);
         WhereItem whenCon_WhItem = new WhereItem();
         WhereColumn whenCon_LeftWhExp = new WhereColumn();
         Vector whenCon_LeftWhColExp = new Vector(1);
         whenCon_LeftWhColExp.addElement(inClauseColAsStr.get(i));
         whenCon_LeftWhExp.setColumnExpression(whenCon_LeftWhColExp);
         whenCon_WhItem.setLeftWhereExp(whenCon_LeftWhExp);
         whenCon_WhItemList.addElement(whenCon_WhItem);
         whenCondition.setWhereItem(whenCon_WhItemList);
         whIt.setWhenCondition(whenCondition);
         String thenClause = "THEN";
         whIt.setThenClause(thenClause);
         SelectColumn thenStmt = new SelectColumn();
         Vector thenStmtList = new Vector(1);
         TableColumn thenCol = ((TableColumn)inClauseColumns.get(i)).toMySQLSelect(to_sqs, from_sqs);
         if (from_sqs.getIsQtNewFlow()) {
            String colName = thenCol.getColumnName();
            colName = GeneralUtil.trimIfTblColIsEnclosed(colName);
            thenCol.setColumnName((String)colNameAlias.get(colName.toLowerCase()));
         }

         thenCol.setTableName(inClauseTableAliasName);
         thenCol.setIsPivotColumn(true);
         thenStmtList.addElement(thenCol);
         thenStmt.setColumnExpression(thenStmtList);
         whIt.setThenStatement(thenStmt);
         whenItemList.addElement(whIt);
      }

      caseStmt.setWhenStatementList(whenItemList);
      String elseClause = "ELSE";
      caseStmt.setElseClause(elseClause);
      SelectColumn elseStmt = new SelectColumn();
      Vector elseStmtList = new Vector(1);
      elseStmtList.addElement("NULL");
      elseStmt.setColumnExpression(elseStmtList);
      caseStmt.setElseStatement(elseStmt);
      String endClause = "END";
      caseStmt.setEndClause(endClause);
      return caseStmt;
   }

   public ArrayList<String> getPivotResColAsString() {
      ArrayList<String> pivottingCols = new ArrayList(4);
      String pivottingColStr = removeFirstAndLastCharsIfStrIsEnclosed(this.pivotColumn.getColumnName());
      pivottingCols.add(pivottingColStr);
      pivottingCols.add(this.aliasName + pivottingColStr);
      TableColumn aggTblCol = (TableColumn)((SelectColumn)this.aggregateFunction.getFunctionArguments().get(0)).getColumnExpression().get(0);
      String aggTblColStr = removeFirstAndLastCharsIfStrIsEnclosed(aggTblCol.getColumnName());
      pivottingCols.add(aggTblColStr);
      pivottingCols.add(this.aliasName + "." + aggTblColStr);
      return pivottingCols;
   }

   private ArrayList<String> getUnPivotResColAsString() throws ConvertException {
      ArrayList<String> inClauseColumns = new ArrayList();
      Vector whereColumns = this.inClause.getRightWhereExp().getColumnExpression();

      for(int i = 0; i < whereColumns.size(); ++i) {
         if (whereColumns.get(i) instanceof WhereColumn) {
            WhereColumn wc = (WhereColumn)whereColumns.get(i);
            Vector vc_unpivotColumns = wc.getColumnExpression();
            if (vc_unpivotColumns.size() == 1 && vc_unpivotColumns.get(0) instanceof TableColumn) {
               TableColumn tc = (TableColumn)vc_unpivotColumns.get(0);
               String colName = tc.getColumnName();
               colName = removeFirstAndLastCharsIfStrIsEnclosed(colName);
               inClauseColumns.add(colName);
               inClauseColumns.add(this.aliasName + "." + colName);
            }
         }
      }

      return inClauseColumns;
   }

   public static boolean isItEnclosedString(String str) {
      return str.startsWith("\"") && str.endsWith("\"") || str.startsWith("[") && str.endsWith("]") || str.startsWith("'") && str.endsWith("'");
   }

   public static String removeFirstAndLastCharsIfStrIsEnclosedAndAddBackTip(String str) {
      if (str != null && isItEnclosedString(str)) {
         str = str.substring(1, str.length() - 1);
         str = str.replace("`", "``");
         return "`" + str + "`";
      } else {
         return str;
      }
   }

   public static boolean isItEnclosedTableColStr(String str) {
      return str.startsWith("\"") && str.endsWith("\"") || str.startsWith("[") && str.endsWith("]") || str.startsWith("`") && str.endsWith("`");
   }

   public static String removeFirstAndLastCharsIfStrIsEnclosed(String str) {
      return str != null && isItEnclosedTableColStr(str) ? str.substring(1, str.length() - 1) : str;
   }

   public static boolean isItEnclosedTAliasName(String str) {
      return str.startsWith("\"") && str.endsWith("\"") || str.startsWith("'") && str.endsWith("'") || str.startsWith("`") && str.endsWith("`");
   }

   public static String removeFirstAndLastCharsIfAliasNameIsEnclosed(String str) {
      return str != null && isItEnclosedTAliasName(str) ? str.substring(1, str.length() - 1) : str;
   }

   public WhereItem toReplaceInClauseForPivot(SelectQueryStatement toSqs, SelectQueryStatement fromSqs, HashMap tblColDetailsMap) throws ConvertException {
      WhereItem inClause = new WhereItem();
      WhereColumn whereColumn = new WhereColumn();
      Vector wc_ce = new Vector();
      Vector inClauseColumns = this.inClause.getRightWhereExp().getColumnExpression();

      for(int i = 0; i < inClauseColumns.size(); ++i) {
         if (inClauseColumns.get(i) instanceof WhereColumn) {
            WhereColumn wc = new WhereColumn();
            Vector v_wc = new Vector();
            WhereColumn wc_1 = (WhereColumn)inClauseColumns.get(i);
            Vector vc_unpivotColumns = wc_1.getColumnExpression();
            if (vc_unpivotColumns.size() == 1 && vc_unpivotColumns.get(0) instanceof TableColumn) {
               TableColumn tc = new TableColumn();
               TableColumn tc_1 = (TableColumn)vc_unpivotColumns.get(0);
               tc.setTableName(tc_1.getTableName());
               tc.setColumnName(tc_1.getColumnName());
               String colName;
               if (this.getPivot().equalsIgnoreCase("PIVOT")) {
                  v_wc.add(tc);
                  colName = GeneralUtil.trimIfTblColIsEnclosed(tc.getColumnName());
                  tblColDetailsMap.put(colName, colName);
               } else {
                  v_wc.add(tc.toReplaceTblCol(toSqs, fromSqs));
                  colName = GeneralUtil.trimIfTblColIsEnclosed(tc.getColumnName());
                  if (colName != null) {
                     tblColDetailsMap.remove(colName);
                  }
               }

               wc.setColumnExpression(v_wc);
               wc_ce.add(wc);
            } else {
               wc_ce.add(inClauseColumns.get(i));
            }
         } else {
            wc_ce.add(inClauseColumns.get(i));
         }
      }

      whereColumn.setColumnExpression(wc_ce);
      inClause.setOperator("in");
      inClause.setRightWhereExp(whereColumn);
      return inClause;
   }
}
