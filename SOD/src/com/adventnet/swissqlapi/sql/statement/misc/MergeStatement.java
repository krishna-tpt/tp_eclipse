package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.delete.DeleteQueryStatement;
import com.adventnet.swissqlapi.sql.statement.insert.InsertClause;
import com.adventnet.swissqlapi.sql.statement.insert.InsertQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.update.HintClause;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;
import com.adventnet.swissqlapi.sql.statement.update.SetClause;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.update.UpdateClause;
import com.adventnet.swissqlapi.sql.statement.update.UpdateQueryStatement;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.util.Vector;

public class MergeStatement implements SwisSQLStatement {
   private String mergeInto;
   private String tableAlias;
   private String using;
   private TableObject tableObj;
   private FromTable fromTable;
   private String onClause;
   private String onOpenBrace;
   private String onClosedBrace;
   private String whenMatchedThen;
   private String whenNotMatchedThen;
   private UpdateQueryStatement upQueryStmt;
   private InsertQueryStatement insertQueryStmt;
   private DeleteQueryStatement deleteQueryStmt;
   private WhereExpression whereExp;
   private String upQueryStmtString;
   private String insertQueryStmtString;
   private UpdateQueryStatement convertedUpdQueryStmt;
   private InsertQueryStatement convertedInsQueryStmt;
   private DeleteQueryStatement convertedDelQueryStmt;
   private WhereExpression insertQueryWhereExp;
   private HintClause hintClause;
   private CommentClass commentObj;

   public void setMergeInto(String mergeInto) {
      this.mergeInto = mergeInto;
   }

   public void setTableObject(TableObject tableObj) {
      this.tableObj = tableObj;
   }

   public void setTableAlias(String tableAlias) {
      this.tableAlias = tableAlias;
   }

   public void setUsing(String using) {
      this.using = using;
   }

   public void setFromTable(FromTable ft) {
      this.fromTable = ft;
   }

   public void setON(String onClause) {
      this.onClause = onClause;
   }

   public void setONOpenBrace(String onOpenBrace) {
      this.onOpenBrace = onOpenBrace;
   }

   public void setONClosedBrace(String onClosedBrace) {
      this.onClosedBrace = onClosedBrace;
   }

   public void setWhenMatchedThen(String whenMatchedThen) {
      this.whenMatchedThen = whenMatchedThen;
   }

   public void setWhenNotMatchedThen(String whenNotMatchedThen) {
      this.whenNotMatchedThen = whenNotMatchedThen;
   }

   public void setUpdateQueryStatement(UpdateQueryStatement upQueryStmt) {
      this.upQueryStmt = upQueryStmt;
   }

   public void setInsertQueryStatement(InsertQueryStatement insertQueryStmt) {
      this.insertQueryStmt = insertQueryStmt;
   }

   public void setDeleteQueryStatement(DeleteQueryStatement deleteQueryStmt) {
      this.deleteQueryStmt = deleteQueryStmt;
   }

   public void setWhereExpression(WhereExpression whereExp) {
      this.whereExp = whereExp;
   }

   public void setInsertWhereExp(WhereExpression iWe) {
      this.insertQueryWhereExp = iWe;
   }

   public void setUpdateQueryStatementString(String upQueryStmtString) {
      this.upQueryStmtString = upQueryStmtString;
   }

   public void setInsertQueryStatementString(String insertQueryStmtString) {
      this.insertQueryStmtString = insertQueryStmtString;
   }

   public void setHintClause(HintClause hintClause) {
      this.hintClause = hintClause;
   }

   public String getMergeInto() {
      return this.mergeInto;
   }

   public TableObject getTableObject() {
      return this.tableObj;
   }

   public String getTableAlias() {
      return this.tableAlias;
   }

   public String getUsing() {
      return this.using;
   }

   public FromTable getFromTable() {
      return this.fromTable;
   }

   public String getON() {
      return this.onClause;
   }

   public String getONOpenBrace() {
      return this.onOpenBrace;
   }

   public String getONClosedBrace() {
      return this.onClosedBrace;
   }

   public String getWhenMatchedThen() {
      return this.whenMatchedThen;
   }

   public String getWhenNotMatchedThen() {
      return this.whenNotMatchedThen;
   }

   public UpdateQueryStatement getUpdateQueryStatement() {
      return this.upQueryStmt;
   }

   public InsertQueryStatement getInsertQueryStatement() {
      return this.insertQueryStmt;
   }

   public DeleteQueryStatement getDeleteQueryStatement() {
      return this.deleteQueryStmt;
   }

   public WhereExpression getWhereExpression() {
      return this.whereExp;
   }

   public WhereExpression getInsertWhereExp() {
      return this.insertQueryWhereExp;
   }

   public String getUpdateQueryStatementString() {
      return this.upQueryStmtString;
   }

   public String getInsertQueryStatementString() {
      return this.insertQueryStmtString;
   }

   public HintClause getHintClause() {
      return this.hintClause;
   }

   public String toANSIString() throws ConvertException {
      return this.toANSIMerge().toString();
   }

   public String toTeradataString() throws ConvertException {
      return this.toTeradataMerge().toString();
   }

   public String toDB2String() throws ConvertException {
      return this.toDB2Merge().toString();
   }

   public String toInformixString() throws ConvertException {
      return this.toInformixMerge().toString();
   }

   public String toMSSQLServerString() throws ConvertException {
      return this.toMSSQLServerMerge().toString();
   }

   public String toMySQLString() throws ConvertException {
      return this.toMySQLMerge().toString();
   }

   public String toOracleString() throws ConvertException {
      return this.toOracleMerge().toString();
   }

   public String toPostgreSQLString() throws ConvertException {
      return this.toPostgreSQLMerge().toString();
   }

   public String toBigQueryString() throws ConvertException {
      return this.toBigQueryMerge().toString();
   }

   public String toSybaseString() throws ConvertException {
      return this.toSybaseMerge().toString();
   }

   public String toTimesTenString() throws ConvertException {
      return this.toTimesTenMerge().toString();
   }

   public String toNetezzaString() throws ConvertException {
      return this.toNetezzaMerge().toString();
   }

   public String toSnowflakeString() throws ConvertException {
      return this.toSnowflakeMerge().toString();
   }

   public String toAthenaString() throws ConvertException {
      return null;
   }

   public String toSapHanaString() throws ConvertException {
      return null;
   }

   public String toSqliteString() throws ConvertException {
      return null;
   }

   public String toExcelString() throws ConvertException {
      return null;
   }

   public String toMsAccessJdbcString() throws ConvertException {
      return null;
   }

   public MergeStatement toANSIMerge() throws ConvertException {
      try {
         MergeStatement mergeStmt = new MergeStatement();
         if (this.mergeInto != null) {
            mergeStmt.setMergeInto(this.mergeInto);
         }

         if (this.hintClause != null) {
            mergeStmt.setHintClause(this.hintClause);
         }

         if (this.tableObj != null) {
            this.tableObj.toANSISQL();
            mergeStmt.setTableObject(this.tableObj);
         }

         if (this.tableAlias != null) {
            mergeStmt.setTableAlias(this.tableAlias);
         }

         if (this.using != null) {
            mergeStmt.setUsing(this.using);
         }

         if (this.fromTable != null) {
            mergeStmt.setFromTable(this.fromTable.toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         }

         if (this.onClause != null) {
            mergeStmt.setON(this.onClause);
         }

         if (this.onOpenBrace != null) {
            mergeStmt.setONOpenBrace(this.onOpenBrace);
         }

         if (this.whereExp != null) {
            mergeStmt.setWhereExpression(this.whereExp.toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         }

         if (this.onClosedBrace != null) {
            mergeStmt.setONClosedBrace(this.onClosedBrace);
         }

         if (this.whenMatchedThen != null) {
            mergeStmt.setWhenMatchedThen(this.whenMatchedThen);
         }

         if (this.upQueryStmt != null) {
            mergeStmt.setUpdateQueryStatementString(this.upQueryStmt.toANSIString());
         }

         if (this.whenNotMatchedThen != null) {
            mergeStmt.setWhenNotMatchedThen(this.whenNotMatchedThen);
         }

         if (this.insertQueryStmt != null) {
            String insertInto = this.insertQueryStmt.toANSIString();
            insertInto = StringFunctions.replaceFirst("", "INTO", insertInto);
            mergeStmt.setInsertQueryStatementString(insertInto);
         }

         return mergeStmt;
      } catch (ConvertException var3) {
         throw var3;
      }
   }

   public MergeStatement toTeradataMerge() throws ConvertException {
      try {
         MergeStatement mergeStmt = new MergeStatement();
         if (this.commentObj != null) {
            this.commentObj.setSQLDialect(12);
            mergeStmt.setCommentClass(this.commentObj);
         }

         if (this.mergeInto != null) {
            mergeStmt.setMergeInto(this.mergeInto);
         }

         if (this.hintClause != null) {
            mergeStmt.setHintClause(this.hintClause);
         }

         if (this.tableObj != null) {
            this.tableObj.toTeradata();
            mergeStmt.setTableObject(this.tableObj);
         }

         if (this.tableAlias != null) {
            this.tableAlias = CustomizeUtil.objectNamesToQuotedIdentifier(this.tableAlias, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
            if (this.tableAlias.equalsIgnoreCase("dual") || this.tableAlias.equalsIgnoreCase("sys.dual")) {
               this.tableAlias = "\"DUAL\"";
            }

            mergeStmt.setTableAlias(this.tableAlias);
         }

         if (this.using != null) {
            mergeStmt.setUsing(this.using);
         }

         if (this.fromTable != null) {
            if (this.fromTable.getTableName() != null && this.fromTable.getTableName() instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.fromTable.getTableName()).setTopLevel(true);
            }

            mergeStmt.setFromTable(this.fromTable.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         }

         if (this.onClause != null) {
            mergeStmt.setON(this.onClause);
         }

         if (this.onOpenBrace != null) {
            mergeStmt.setONOpenBrace(this.onOpenBrace);
         }

         if (this.whereExp != null) {
            mergeStmt.setWhereExpression(this.whereExp.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         }

         if (this.onClosedBrace != null) {
            mergeStmt.setONClosedBrace(this.onClosedBrace);
         }

         if (this.whenMatchedThen != null) {
            mergeStmt.setWhenMatchedThen(this.whenMatchedThen);
         }

         if (this.upQueryStmt != null) {
            mergeStmt.setUpdateQueryStatementString(this.upQueryStmt.toTeradataString());
         }

         if (this.whenNotMatchedThen != null) {
            mergeStmt.setWhenNotMatchedThen(this.whenNotMatchedThen);
         }

         if (this.insertQueryStmt != null) {
            String insertInto = this.insertQueryStmt.toTeradataString();
            insertInto = StringFunctions.replaceFirst("", "INTO", insertInto);
            mergeStmt.setInsertQueryStatementString(insertInto);
         }

         return mergeStmt;
      } catch (ConvertException var3) {
         throw var3;
      }
   }

   public MergeStatement toDB2Merge() throws ConvertException {
      try {
         throw new ConvertException("Merge query yet to be supported");
      } catch (ConvertException var2) {
         throw var2;
      }
   }

   public MergeStatement toOracleMerge() throws ConvertException {
      MergeStatement mergeStmt = new MergeStatement();
      if (this.mergeInto != null) {
         mergeStmt.setMergeInto(this.mergeInto);
      }

      if (this.tableObj != null) {
         this.tableObj.toOracle();
         mergeStmt.setTableObject(this.tableObj);
      }

      if (this.tableAlias != null) {
         mergeStmt.setTableAlias(this.tableAlias);
      }

      if (this.using != null) {
         mergeStmt.setUsing(this.using);
      }

      if (this.fromTable != null) {
         mergeStmt.setFromTable(this.fromTable.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (this.onClause != null) {
         mergeStmt.setON(this.onClause);
      }

      if (this.onOpenBrace != null) {
         mergeStmt.setONOpenBrace(this.onOpenBrace);
      }

      if (this.whereExp != null) {
         mergeStmt.setWhereExpression(this.whereExp.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (this.onClosedBrace != null) {
         mergeStmt.setONClosedBrace(this.onClosedBrace);
      }

      if (this.whenMatchedThen != null) {
         mergeStmt.setWhenMatchedThen(this.whenMatchedThen);
      }

      if (this.upQueryStmt != null) {
         mergeStmt.setUpdateQueryStatementString(this.upQueryStmt.toOracleString());
      }

      if (this.whenNotMatchedThen != null) {
         mergeStmt.setWhenNotMatchedThen(this.whenNotMatchedThen);
      }

      if (this.insertQueryStmt != null) {
         String insertInto = this.insertQueryStmt.toOracleString();
         insertInto = StringFunctions.replaceFirst("", "INTO", insertInto);
         mergeStmt.setInsertQueryStatementString(insertInto);
      }

      return mergeStmt;
   }

   public MergeStatement toMSSQLServerMerge() throws ConvertException {
      this.convertedUpdQueryStmt = new UpdateQueryStatement();
      UpdateClause updClause = new UpdateClause();
      updClause.setUpdate("UPDATE");
      this.convertedUpdQueryStmt.setUpdateClause(updClause);
      TableExpression tblExp = new TableExpression();
      ArrayList tblList = new ArrayList();
      this.tableObj.toMSSQLServer();
      tblList.add(this.tableObj);
      tblExp.setTableClauseList(tblList);
      this.convertedUpdQueryStmt.setTableExpression(tblExp);
      SetClause setClause = new SetClause();
      setClause.setSet("SET");
      setClause.setExpression(this.upQueryStmt.getSetClause().getExpression());
      ArrayList exprList = setClause.getExpression();

      for(int j = 0; j < exprList.size(); ++j) {
         Object obj = exprList.get(j);
         if (obj instanceof TableColumn) {
            TableColumn tc = (TableColumn)obj;
            tc = tc.toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            tc.setTableName((String)null);
            tc.setDot((String)null);
            exprList.set(j, tc);
         }
      }

      this.convertedUpdQueryStmt.setSetClause(setClause);
      FromClause fromClause = new FromClause();
      fromClause.setFromClause("FROM");
      Vector fromClauseSubQuery = new Vector();
      fromClauseSubQuery.add(this.fromTable);
      fromClause.setFromItemList(fromClauseSubQuery);
      this.convertedUpdQueryStmt.setFromClause(fromClause);
      this.convertedUpdQueryStmt.setWhereClause(this.whereExp);
      WhereExpression whereExpr = this.convertedUpdQueryStmt.getWhereExpression();
      Vector whereItems = whereExpr.getWhereItems();

      for(int i = 0; i < whereItems.size(); ++i) {
         WhereItem whereItem = (WhereItem)whereItems.get(i);
         WhereColumn whereColumn = whereItem.getLeftWhereExp();
         Vector whereColExpr = whereColumn.getColumnExpression();

         for(int j = 0; j < whereColExpr.size(); ++j) {
            Object obj = whereColExpr.get(j);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               if (tc.getTableName().trim().equalsIgnoreCase(this.tableAlias)) {
                  tc.setTableName(this.tableObj.getTableName());
               }
            }
         }
      }

      this.convertedInsQueryStmt = new InsertQueryStatement();
      InsertClause insertClause = new InsertClause();
      insertClause.setInsert("INSERT");
      TableExpression insertTblExp = new TableExpression();
      ArrayList insertTblList = new ArrayList();
      this.tableObj.toMSSQLServer();
      insertTblList.add(this.tableObj);
      insertTblExp.setTableClauseList(tblList);
      insertClause.setTableExpression(insertTblExp);
      Object obj1 = this.insertQueryStmt.getInsertClause();
      if (obj1 != null) {
         ArrayList insertColumnList = this.insertQueryStmt.getInsertClause().getColumnList();
         insertClause.setColumnList(insertColumnList);
      }

      OptionalSpecifier optSpec = new OptionalSpecifier();
      optSpec.setInto("INTO");
      insertClause.setOptionalSpecifier(optSpec);
      this.convertedInsQueryStmt.setInsertClause(insertClause);
      SelectQueryStatement insertSelect = new SelectQueryStatement();
      SelectStatement selectStmt = new SelectStatement();
      selectStmt.setSelectClause("SELECT");
      Vector selectStmtItemList = new Vector();
      ArrayList mergeValuesList = this.insertQueryStmt.getValuesClause().getValuesList();

      for(int i = 0; i < mergeValuesList.size(); ++i) {
         if (mergeValuesList.get(i) instanceof String) {
            String mergeVal = mergeValuesList.get(i).toString();
            if (!mergeVal.equalsIgnoreCase("(") && !mergeVal.equalsIgnoreCase(")")) {
               selectStmtItemList.add(mergeValuesList.get(i));
            }
         } else {
            selectStmtItemList.add(mergeValuesList.get(i));
         }
      }

      selectStmt.setSelectItemList(selectStmtItemList);
      insertSelect.setSelectStatement(selectStmt);
      FromClause insertFromClause = new FromClause();
      insertFromClause.setFromClause("FROM");
      FromTable insertFromTable = this.fromTable;
      Vector insertFromItemList = new Vector();
      insertFromItemList.add(insertFromTable);
      insertFromClause.setFromItemList(insertFromItemList);
      insertSelect.setFromClause(insertFromClause);
      ArrayList onClauseLeftWhereColumns = new ArrayList();
      WhereExpression onClauseWhereExp = this.whereExp;
      Vector onClauseWhereItems = whereExpr.getWhereItems();

      WhereItem insertWhereItem;
      WhereColumn rightWhereColumn;
      Vector rightWhereColExpr;
      for(int i = 0; i < whereItems.size(); ++i) {
         insertWhereItem = (WhereItem)whereItems.get(i);
         WhereColumn leftWhereColumn = insertWhereItem.getLeftWhereExp();
         Vector leftWhereColExpr = leftWhereColumn.getColumnExpression();

         for(int j = 0; j < leftWhereColExpr.size(); ++j) {
            Object obj = leftWhereColExpr.get(j);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               if (tc.getTableName().trim().equalsIgnoreCase(this.tableAlias) || tc.getTableName().trim().equalsIgnoreCase(this.tableObj.getTableName())) {
                  onClauseLeftWhereColumns.add(tc.getColumnName());
               }
            }
         }

         rightWhereColumn = insertWhereItem.getRightWhereExp();
         rightWhereColExpr = rightWhereColumn.getColumnExpression();

         for(int j = 0; j < rightWhereColExpr.size(); ++j) {
            Object obj = rightWhereColExpr.get(j);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               if (tc.getTableName().trim().equalsIgnoreCase(this.tableAlias) || tc.getTableName().trim().equalsIgnoreCase(this.tableObj.getTableName())) {
                  onClauseLeftWhereColumns.add(tc.getColumnName());
               }
            }
         }
      }

      WhereExpression insertWhereExp = new WhereExpression();
      insertWhereItem = new WhereItem();
      insertWhereItem.setOperator("NOT IN");
      Vector insertWhereItemLeftWhereExpList = new Vector();
      if (onClauseLeftWhereColumns.size() == 1) {
         insertWhereItemLeftWhereExpList.add(this.convertTableColumnToWhereColumn(onClauseLeftWhereColumns.get(0)));
      } else {
         insertWhereItemLeftWhereExpList.add("(");

         for(int i = 0; i < onClauseLeftWhereColumns.size(); ++i) {
            insertWhereItemLeftWhereExpList.add(this.convertTableColumnToWhereColumn(onClauseLeftWhereColumns.get(i)));
            if (i != onClauseLeftWhereColumns.size() - 1) {
               insertWhereItemLeftWhereExpList.add(",");
            }
         }

         insertWhereItemLeftWhereExpList.add(")");
      }

      WhereColumn leftWhereColumn = new WhereColumn();
      leftWhereColumn.setColumnExpression(insertWhereItemLeftWhereExpList);
      insertWhereItem.setLeftWhereExp(leftWhereColumn);
      rightWhereColumn = new WhereColumn();
      rightWhereColExpr = new Vector();
      rightWhereColExpr.add("(");
      rightWhereColExpr.add(")");
      insertWhereItem.setRightWhereExp(rightWhereColumn);
      SelectQueryStatement notInSelectQueryStmt = new SelectQueryStatement();
      SelectStatement notInSelectStmt = new SelectStatement();
      notInSelectStmt.setSelectClause("SELECT");
      Vector notInSelectStmtItemList = new Vector();

      Vector notInFromItemList;
      for(int i = 0; i < onClauseLeftWhereColumns.size(); ++i) {
         SelectColumn selCol = new SelectColumn();
         notInFromItemList = new Vector();
         notInFromItemList.add(onClauseLeftWhereColumns.get(i));
         selCol.setColumnExpression(notInFromItemList);
         notInSelectStmtItemList.add(selCol);
         if (i != onClauseLeftWhereColumns.size() - 1) {
            notInSelectStmtItemList.add(",");
         }
      }

      notInSelectStmt.setSelectItemList(notInSelectStmtItemList);
      FromClause notInFromClause = new FromClause();
      notInFromClause.setFromClause("FROM");
      FromTable notInFromTable = new FromTable();
      notInFromTable.setTableName(this.tableObj);
      notInFromItemList = new Vector();
      notInFromItemList.add(notInFromTable);
      notInFromClause.setFromItemList(notInFromItemList);
      notInSelectQueryStmt.setSelectStatement(notInSelectStmt);
      notInSelectQueryStmt.setFromClause(notInFromClause);
      insertWhereItem.setRightWhereSubQuery(notInSelectQueryStmt);
      Vector insertWhereItemVector = new Vector();
      insertWhereItemVector.add(insertWhereItem);
      insertWhereExp.setWhereItem(insertWhereItemVector);
      insertSelect.setWhereExpression(insertWhereExp);
      this.convertedInsQueryStmt.setSelectQueryStatement(insertSelect);
      return this;
   }

   public MergeStatement toMySQLMerge() throws ConvertException {
      try {
         throw new ConvertException("Merge query yet to be supported");
      } catch (ConvertException var2) {
         throw var2;
      }
   }

   public MergeStatement toBigQueryMerge() throws ConvertException {
      try {
         throw new ConvertException("Merge query yet to be supported");
      } catch (ConvertException var2) {
         throw var2;
      }
   }

   public MergeStatement toPostgreSQLMerge() throws ConvertException {
      try {
         throw new ConvertException("Merge query yet to be supported");
      } catch (ConvertException var2) {
         throw var2;
      }
   }

   public MergeStatement toInformixMerge() throws ConvertException {
      try {
         throw new ConvertException("Merge query yet to be supported");
      } catch (ConvertException var2) {
         throw var2;
      }
   }

   public MergeStatement toSybaseMerge() throws ConvertException {
      this.convertedUpdQueryStmt = new UpdateQueryStatement();
      UpdateClause updClause = new UpdateClause();
      updClause.setUpdate("UPDATE");
      this.convertedUpdQueryStmt.setUpdateClause(updClause);
      TableExpression tblExp = new TableExpression();
      ArrayList tblList = new ArrayList();
      this.tableObj.toSybase();
      tblList.add(this.tableObj);
      tblExp.setTableClauseList(tblList);
      this.convertedUpdQueryStmt.setTableExpression(tblExp);
      SetClause setClause = new SetClause();
      setClause.setSet("SET");
      setClause.setExpression(this.upQueryStmt.getSetClause().getExpression());
      ArrayList exprList = setClause.getExpression();

      for(int j = 0; j < exprList.size(); ++j) {
         Object obj = exprList.get(j);
         if (obj instanceof TableColumn) {
            TableColumn tc = (TableColumn)obj;
            tc = tc.toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            tc.setTableName((String)null);
            tc.setDot((String)null);
            exprList.set(j, tc);
         }
      }

      this.convertedUpdQueryStmt.setSetClause(setClause);
      FromClause fromClause = new FromClause();
      fromClause.setFromClause("FROM");
      Vector fromClauseSubQuery = new Vector();
      fromClauseSubQuery.add(this.fromTable);
      fromClause.setFromItemList(fromClauseSubQuery);
      this.convertedUpdQueryStmt.setFromClause(fromClause);
      this.convertedUpdQueryStmt.setWhereClause(this.whereExp);
      WhereExpression whereExpr = this.convertedUpdQueryStmt.getWhereExpression();
      Vector whereItems = whereExpr.getWhereItems();

      for(int i = 0; i < whereItems.size(); ++i) {
         WhereItem whereItem = (WhereItem)whereItems.get(i);
         WhereColumn whereColumn = whereItem.getLeftWhereExp();
         Vector whereColExpr = whereColumn.getColumnExpression();

         for(int j = 0; j < whereColExpr.size(); ++j) {
            Object obj = whereColExpr.get(j);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               if (tc.getTableName().trim().equalsIgnoreCase(this.tableAlias)) {
                  tc.setTableName(this.tableObj.getTableName());
               }
            }
         }
      }

      this.convertedInsQueryStmt = new InsertQueryStatement();
      InsertClause insertClause = new InsertClause();
      insertClause.setInsert("INSERT");
      TableExpression insertTblExp = new TableExpression();
      ArrayList insertTblList = new ArrayList();
      this.tableObj.toSybase();
      insertTblList.add(this.tableObj);
      insertTblExp.setTableClauseList(tblList);
      insertClause.setTableExpression(insertTblExp);
      Object obj1 = this.insertQueryStmt.getInsertClause();
      if (obj1 != null) {
         ArrayList insertColumnList = this.insertQueryStmt.getInsertClause().getColumnList();
         insertClause.setColumnList(insertColumnList);
      }

      OptionalSpecifier optSpec = new OptionalSpecifier();
      optSpec.setInto("INTO");
      insertClause.setOptionalSpecifier(optSpec);
      this.convertedInsQueryStmt.setInsertClause(insertClause);
      SelectQueryStatement insertSelect = new SelectQueryStatement();
      SelectStatement selectStmt = new SelectStatement();
      selectStmt.setSelectClause("SELECT");
      Vector selectStmtItemList = new Vector();
      ArrayList mergeValuesList = this.insertQueryStmt.getValuesClause().getValuesList();

      for(int i = 0; i < mergeValuesList.size(); ++i) {
         if (mergeValuesList.get(i) instanceof String) {
            String mergeVal = mergeValuesList.get(i).toString();
            if (!mergeVal.equalsIgnoreCase("(") && !mergeVal.equalsIgnoreCase(")")) {
               selectStmtItemList.add(mergeValuesList.get(i));
            }
         } else {
            selectStmtItemList.add(mergeValuesList.get(i));
         }
      }

      selectStmt.setSelectItemList(selectStmtItemList);
      insertSelect.setSelectStatement(selectStmt);
      FromClause insertFromClause = new FromClause();
      insertFromClause.setFromClause("FROM");
      FromTable insertFromTable = this.fromTable;
      Vector insertFromItemList = new Vector();
      insertFromItemList.add(insertFromTable);
      insertFromClause.setFromItemList(insertFromItemList);
      insertSelect.setFromClause(insertFromClause);
      ArrayList onClauseLeftWhereColumns = new ArrayList();
      WhereExpression onClauseWhereExp = this.whereExp;
      Vector onClauseWhereItems = whereExpr.getWhereItems();

      WhereItem insertWhereItem;
      WhereColumn rightWhereColumn;
      Vector rightWhereColExpr;
      for(int i = 0; i < whereItems.size(); ++i) {
         insertWhereItem = (WhereItem)whereItems.get(i);
         WhereColumn leftWhereColumn = insertWhereItem.getLeftWhereExp();
         Vector leftWhereColExpr = leftWhereColumn.getColumnExpression();

         for(int j = 0; j < leftWhereColExpr.size(); ++j) {
            Object obj = leftWhereColExpr.get(j);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               if (tc.getTableName().trim().equalsIgnoreCase(this.tableAlias) || tc.getTableName().trim().equalsIgnoreCase(this.tableObj.getTableName())) {
                  onClauseLeftWhereColumns.add(tc.getColumnName());
               }
            }
         }

         rightWhereColumn = insertWhereItem.getRightWhereExp();
         rightWhereColExpr = rightWhereColumn.getColumnExpression();

         for(int j = 0; j < rightWhereColExpr.size(); ++j) {
            Object obj = rightWhereColExpr.get(j);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               if (tc.getTableName().trim().equalsIgnoreCase(this.tableAlias) || tc.getTableName().trim().equalsIgnoreCase(this.tableObj.getTableName())) {
                  onClauseLeftWhereColumns.add(tc.getColumnName());
               }
            }
         }
      }

      WhereExpression insertWhereExp = new WhereExpression();
      insertWhereItem = new WhereItem();
      insertWhereItem.setOperator("NOT IN");
      Vector insertWhereItemLeftWhereExpList = new Vector();
      if (onClauseLeftWhereColumns.size() == 1) {
         insertWhereItemLeftWhereExpList.add(this.convertTableColumnToWhereColumn(onClauseLeftWhereColumns.get(0)));
      } else {
         insertWhereItemLeftWhereExpList.add("(");

         for(int i = 0; i < onClauseLeftWhereColumns.size(); ++i) {
            insertWhereItemLeftWhereExpList.add(this.convertTableColumnToWhereColumn(onClauseLeftWhereColumns.get(i)));
            if (i != onClauseLeftWhereColumns.size() - 1) {
               insertWhereItemLeftWhereExpList.add(",");
            }
         }

         insertWhereItemLeftWhereExpList.add(")");
      }

      WhereColumn leftWhereColumn = new WhereColumn();
      leftWhereColumn.setColumnExpression(insertWhereItemLeftWhereExpList);
      insertWhereItem.setLeftWhereExp(leftWhereColumn);
      rightWhereColumn = new WhereColumn();
      rightWhereColExpr = new Vector();
      rightWhereColExpr.add("(");
      rightWhereColExpr.add(")");
      insertWhereItem.setRightWhereExp(rightWhereColumn);
      SelectQueryStatement notInSelectQueryStmt = new SelectQueryStatement();
      SelectStatement notInSelectStmt = new SelectStatement();
      notInSelectStmt.setSelectClause("SELECT");
      Vector notInSelectStmtItemList = new Vector();

      Vector notInFromItemList;
      for(int i = 0; i < onClauseLeftWhereColumns.size(); ++i) {
         SelectColumn selCol = new SelectColumn();
         notInFromItemList = new Vector();
         notInFromItemList.add(onClauseLeftWhereColumns.get(i));
         selCol.setColumnExpression(notInFromItemList);
         notInSelectStmtItemList.add(selCol);
         if (i != onClauseLeftWhereColumns.size() - 1) {
            notInSelectStmtItemList.add(",");
         }
      }

      notInSelectStmt.setSelectItemList(notInSelectStmtItemList);
      FromClause notInFromClause = new FromClause();
      notInFromClause.setFromClause("FROM");
      FromTable notInFromTable = new FromTable();
      notInFromTable.setTableName(this.tableObj);
      notInFromItemList = new Vector();
      notInFromItemList.add(notInFromTable);
      notInFromClause.setFromItemList(notInFromItemList);
      notInSelectQueryStmt.setSelectStatement(notInSelectStmt);
      notInSelectQueryStmt.setFromClause(notInFromClause);
      insertWhereItem.setRightWhereSubQuery(notInSelectQueryStmt);
      Vector insertWhereItemVector = new Vector();
      insertWhereItemVector.add(insertWhereItem);
      insertWhereExp.setWhereItem(insertWhereItemVector);
      insertSelect.setWhereExpression(insertWhereExp);
      this.convertedInsQueryStmt.setSelectQueryStatement(insertSelect);
      return this;
   }

   public MergeStatement toTimesTenMerge() throws ConvertException {
      try {
         throw new ConvertException("Merge query yet to be supported");
      } catch (ConvertException var2) {
         throw var2;
      }
   }

   public MergeStatement toNetezzaMerge() throws ConvertException {
      this.convertedUpdQueryStmt = new UpdateQueryStatement();
      UpdateClause updClause = new UpdateClause();
      updClause.setUpdate("UPDATE");
      this.convertedUpdQueryStmt.setUpdateClause(updClause);
      TableExpression tblExp = new TableExpression();
      ArrayList tblList = new ArrayList();
      this.tableObj.toNetezza();
      tblList.add(this.tableObj);
      tblExp.setTableClauseList(tblList);
      this.convertedUpdQueryStmt.setTableExpression(tblExp);
      SetClause setClause = new SetClause();
      setClause.setSet("SET");
      this.upQueryStmt.getSetClause().toNetezza();
      setClause.setExpression(this.upQueryStmt.getSetClause().getExpression());
      ArrayList exprList = setClause.getExpression();

      for(int j = 0; j < exprList.size(); ++j) {
         Object obj = exprList.get(j);
         if (obj instanceof TableColumn) {
            TableColumn tc = (TableColumn)obj;
            tc = tc.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            tc.setTableName((String)null);
            tc.setDot((String)null);
            exprList.set(j, tc);
         }
      }

      this.convertedUpdQueryStmt.setSetClause(setClause);
      FromClause fromClause = new FromClause();
      fromClause.setFromClause("FROM");
      Vector fromClauseSubQuery = new Vector();
      fromClauseSubQuery.add(this.fromTable.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      fromClause.setFromItemList(fromClauseSubQuery);
      this.convertedUpdQueryStmt.setFromClause(fromClause);
      if (this.upQueryStmt.getWhereExpression() != null) {
         this.whereExp.addOperator("AND");
         this.whereExp.addWhereExpression(this.upQueryStmt.getWhereExpression());
      }

      this.convertedUpdQueryStmt.setWhereClause(this.whereExp.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      WhereExpression whereExpr = this.convertedUpdQueryStmt.getWhereExpression();
      Vector whereItems = whereExpr.getWhereItems();

      for(int i = 0; i < whereItems.size(); ++i) {
         WhereItem whereItem = (WhereItem)whereItems.get(i);
         WhereColumn whereColumn = whereItem.getLeftWhereExp();
         Vector whereColExpr = whereColumn.getColumnExpression();

         for(int j = 0; j < whereColExpr.size(); ++j) {
            Object obj = whereColExpr.get(j);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               if (tc.getTableName().trim().equalsIgnoreCase(this.tableAlias)) {
                  tc.setTableName(this.tableObj.getTableName());
               }
            }
         }
      }

      this.convertedInsQueryStmt = new InsertQueryStatement();
      InsertClause insertClause = new InsertClause();
      insertClause.setInsert("INSERT");
      TableExpression insertTblExp = new TableExpression();
      ArrayList insertTblList = new ArrayList();
      this.tableObj.toNetezza();
      insertTblList.add(this.tableObj);
      insertTblExp.setTableClauseList(insertTblList);
      insertClause.setTableExpression(insertTblExp);
      this.insertQueryStmt.getInsertClause().toNetezza(this.insertQueryStmt);
      ArrayList insertColumnList = this.insertQueryStmt.getInsertClause().getColumnList();
      insertClause.setColumnList(insertColumnList);
      OptionalSpecifier optSpec = new OptionalSpecifier();
      optSpec.setInto("INTO");
      insertClause.setOptionalSpecifier(optSpec);
      this.convertedInsQueryStmt.setInsertClause(insertClause);
      SelectQueryStatement insertSelect = new SelectQueryStatement();
      SelectStatement selectStmt = new SelectStatement();
      selectStmt.setSelectClause("SELECT");
      Vector selectStmtItemList = new Vector();
      ArrayList mergeValuesList = this.insertQueryStmt.getValuesClause().toNetezza().getValuesList();

      for(int i = 0; i < mergeValuesList.size(); ++i) {
         if (mergeValuesList.get(i) instanceof String) {
            String mergeVal = mergeValuesList.get(i).toString();
            if (!mergeVal.equalsIgnoreCase("(") && !mergeVal.equalsIgnoreCase(")")) {
               selectStmtItemList.add(mergeValuesList.get(i));
            }
         } else {
            selectStmtItemList.add(mergeValuesList.get(i));
         }
      }

      selectStmt.setSelectItemList(selectStmtItemList);
      insertSelect.setSelectStatement(selectStmt);
      FromClause insertFromClause = new FromClause();
      insertFromClause.setFromClause("FROM");
      FromTable insertFromTable = this.fromTable.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      Vector insertFromItemList = new Vector();
      insertFromItemList.add(insertFromTable);
      insertFromClause.setFromItemList(insertFromItemList);
      insertSelect.setFromClause(insertFromClause);
      ArrayList onClauseLeftWhereColumns = new ArrayList();
      WhereExpression onClauseWhereExp = this.whereExp.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      Vector onClauseWhereItems = whereExpr.getWhereItems();

      WhereItem insertWhereItem;
      WhereColumn rightWhereColumn;
      Vector rightWhereColExpr;
      for(int i = 0; i < whereItems.size(); ++i) {
         insertWhereItem = (WhereItem)whereItems.get(i);
         WhereColumn leftWhereColumn = insertWhereItem.getLeftWhereExp();
         Vector leftWhereColExpr = leftWhereColumn.getColumnExpression();

         for(int j = 0; j < leftWhereColExpr.size(); ++j) {
            Object obj = leftWhereColExpr.get(j);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               if (tc.getTableName().trim().equalsIgnoreCase(this.tableAlias) || tc.getTableName().trim().equalsIgnoreCase(this.tableObj.getTableName())) {
                  onClauseLeftWhereColumns.add(tc.getColumnName());
               }
            }
         }

         rightWhereColumn = insertWhereItem.getRightWhereExp();
         rightWhereColExpr = rightWhereColumn.getColumnExpression();

         for(int j = 0; j < rightWhereColExpr.size(); ++j) {
            Object obj = rightWhereColExpr.get(j);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               if (tc.getTableName().trim().equalsIgnoreCase(this.tableAlias) || tc.getTableName().trim().equalsIgnoreCase(this.tableObj.getTableName())) {
                  onClauseLeftWhereColumns.add(tc.getColumnName());
               }
            }
         }
      }

      WhereExpression insertWhereExp = new WhereExpression();
      insertWhereItem = new WhereItem();
      insertWhereItem.setOperator("NOT IN");
      Vector insertWhereItemLeftWhereExpList = new Vector();
      if (onClauseLeftWhereColumns.size() == 1) {
         insertWhereItemLeftWhereExpList.add(this.convertTableColumnToWhereColumn(onClauseLeftWhereColumns.get(0)));
      } else {
         insertWhereItemLeftWhereExpList.add("(");

         for(int i = 0; i < onClauseLeftWhereColumns.size(); ++i) {
            insertWhereItemLeftWhereExpList.add(this.convertTableColumnToWhereColumn(onClauseLeftWhereColumns.get(i)));
            if (i != onClauseLeftWhereColumns.size() - 1) {
               insertWhereItemLeftWhereExpList.add(",");
            }
         }

         insertWhereItemLeftWhereExpList.add(")");
      }

      WhereColumn leftWhereColumn = new WhereColumn();
      leftWhereColumn.setColumnExpression(insertWhereItemLeftWhereExpList);
      insertWhereItem.setLeftWhereExp(leftWhereColumn);
      rightWhereColumn = new WhereColumn();
      rightWhereColExpr = new Vector();
      rightWhereColExpr.add("(");
      rightWhereColExpr.add(")");
      insertWhereItem.setRightWhereExp(rightWhereColumn);
      SelectQueryStatement notInSelectQueryStmt = new SelectQueryStatement();
      SelectStatement notInSelectStmt = new SelectStatement();
      notInSelectStmt.setSelectClause("SELECT");
      Vector notInSelectStmtItemList = new Vector();

      Vector notInFromItemList;
      for(int i = 0; i < onClauseLeftWhereColumns.size(); ++i) {
         SelectColumn selCol = new SelectColumn();
         notInFromItemList = new Vector();
         notInFromItemList.add(onClauseLeftWhereColumns.get(i));
         selCol.setColumnExpression(notInFromItemList);
         notInSelectStmtItemList.add(selCol);
         if (i != onClauseLeftWhereColumns.size() - 1) {
            notInSelectStmtItemList.add(",");
         }
      }

      notInSelectStmt.setSelectItemList(notInSelectStmtItemList);
      FromClause notInFromClause = new FromClause();
      notInFromClause.setFromClause("FROM");
      FromTable notInFromTable = new FromTable();
      notInFromTable.setTableName(this.tableObj);
      notInFromItemList = new Vector();
      notInFromItemList.add(notInFromTable);
      notInFromClause.setFromItemList(notInFromItemList);
      notInSelectQueryStmt.setSelectStatement(notInSelectStmt);
      notInSelectQueryStmt.setFromClause(notInFromClause);
      insertWhereItem.setRightWhereSubQuery(notInSelectQueryStmt);
      Vector insertWhereItemVector = new Vector();
      insertWhereItemVector.add(insertWhereItem);
      insertWhereExp.setWhereItem(insertWhereItemVector);
      if (this.insertQueryWhereExp != null) {
         insertWhereExp.addOperator("AND");
         insertWhereExp.addWhereExpression(this.insertQueryWhereExp);
      }

      insertSelect.setWhereExpression(insertWhereExp);
      this.convertedInsQueryStmt.setSelectQueryStatement(insertSelect);
      if (this.deleteQueryStmt != null) {
         this.convertedDelQueryStmt = new DeleteQueryStatement();
         OptionalSpecifier opSpec = new OptionalSpecifier();
         opSpec.setFrom("FROM");
         this.deleteQueryStmt.getDeleteClause().setOptionalSpecifier(opSpec);
         this.deleteQueryStmt.getDeleteClause().toNetezza();
         this.convertedDelQueryStmt.setDeleteClause(this.deleteQueryStmt.getDeleteClause());
         TableExpression deleteTableExp = new TableExpression();
         ArrayList deleteTblList = new ArrayList();
         this.tableObj.toNetezza();
         deleteTblList.add(this.tableObj);
         deleteTableExp.setTableClauseList(deleteTblList);
         this.convertedDelQueryStmt.setTableExpression(deleteTableExp);
         this.convertedDelQueryStmt.setWhereClause(this.deleteQueryStmt.getWhereExpression().toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      return this;
   }

   public MergeStatement toSnowflakeMerge() throws ConvertException {
      try {
         throw new ConvertException("Merge query yet to be supported");
      } catch (ConvertException var2) {
         throw var2;
      }
   }

   public String toString() {
      StringBuffer upsertStr = new StringBuffer();
      if (this.convertedUpdQueryStmt != null) {
         upsertStr.append(this.convertedUpdQueryStmt.toString() + ";\n");
      }

      if (this.convertedDelQueryStmt != null) {
         upsertStr.append(this.convertedDelQueryStmt.toString() + ";\n");
      }

      if (this.convertedInsQueryStmt != null) {
         upsertStr.append(this.convertedInsQueryStmt.toString() + "\n");
         return upsertStr.toString();
      } else {
         StringBuffer sb = new StringBuffer();
         if (this.commentObj != null) {
            sb.append(this.commentObj.toString() + "\n");
         }

         if (this.mergeInto != null) {
            sb.append(this.getMergeInto() + " ");
         }

         if (this.hintClause != null) {
            sb.append(this.hintClause.toString() + " ");
         }

         if (this.tableObj != null) {
            sb.append(this.getTableObject().toString() + " ");
         }

         if (this.tableAlias != null) {
            sb.append(this.getTableAlias() + "\n");
         }

         if (this.using != null) {
            sb.append(this.getUsing() + " ");
         }

         if (this.fromTable != null) {
            sb.append(this.getFromTable().toString() + "\n");
         }

         if (this.onClause != null) {
            sb.append(this.getON() + " ");
         }

         if (this.onOpenBrace != null) {
            sb.append(this.getONOpenBrace());
         }

         if (this.whereExp != null) {
            sb.append(this.getWhereExpression().toString());
         }

         if (this.onClosedBrace != null) {
            sb.append(this.getONClosedBrace() + "\n");
         }

         if (this.whenMatchedThen != null) {
            sb.append(this.getWhenMatchedThen() + "\n");
         }

         if (this.getUpdateQueryStatementString() != null) {
            sb.append(this.getUpdateQueryStatementString() + "\n");
         } else if (this.upQueryStmt != null) {
            sb.append(this.upQueryStmt + "\n");
         }

         if (this.whenNotMatchedThen != null) {
            sb.append(this.getWhenNotMatchedThen() + "\n");
         }

         if (this.getInsertQueryStatementString() != null) {
            sb.append(this.getInsertQueryStatementString());
         } else if (this.insertQueryStmt != null) {
            sb.append(this.insertQueryStmt + "\n");
         }

         return sb.toString();
      }
   }

   public void setCommentClass(CommentClass commentObject) {
      this.commentObj = commentObject;
   }

   public CommentClass getCommentClass() {
      return new CommentClass();
   }

   public UserObjectContext getObjectContext() {
      return null;
   }

   public void setObjectContext(UserObjectContext obj) {
   }

   public String removeIndent(String formattedSqlString) {
      formattedSqlString = formattedSqlString.replace('\n', ' ');
      formattedSqlString = formattedSqlString.replace('\t', ' ');
      return formattedSqlString;
   }

   private WhereColumn convertTableColumnToWhereColumn(Object obj) {
      TableColumn tableCol = new TableColumn();
      tableCol.setColumnName(obj.toString());
      WhereColumn whereCol = new WhereColumn();
      Vector columnExpr = new Vector();
      columnExpr.add(tableCol);
      whereCol.setColumnExpression(columnExpr);
      return whereCol;
   }

   public String toVectorWiseString() throws ConvertException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public SwisSQLStatement toVectorWise() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toInformix() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toNetezza() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toTimesTen() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toOracle() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSqlite() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSapHana() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toAthena() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSnowflake() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toMySQL() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toPostgreSQL() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toBigQuery() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toDB2() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSybase() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toMSSQLServer() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toTeradata() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toANSI() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toMsAccessJdbc() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toExcel() throws ConvertException {
      return null;
   }
}
