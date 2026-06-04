package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import java.util.Vector;

public class ShowStatement implements SwisSQLStatement {
   private String showStr;
   private String selectColumnStr;
   private String fromStr;
   private FromTable fromTableItem;
   private String likeStr;
   private String likePattern;
   private String status;

   public void setShow(String show) {
      this.showStr = show;
   }

   public void setSelectColumn(String selectColumnString) {
      this.selectColumnStr = selectColumnString;
   }

   public void setFrom(String from) {
      this.fromStr = from;
   }

   public void setFromItem(FromTable fromItem) {
      this.fromTableItem = fromItem;
   }

   public void setLike(String like) {
      this.likeStr = like;
   }

   public void setLikePattern(String likePattern) {
      this.likePattern = likePattern;
   }

   public String getShow() {
      return this.showStr;
   }

   public String getSelectColumn() {
      return this.selectColumnStr;
   }

   public String getFrom() {
      return this.fromStr;
   }

   public FromTable getFromItem() {
      return this.fromTableItem;
   }

   public String getLike() {
      return this.likeStr;
   }

   public String getLikePattern() {
      return this.likePattern;
   }

   public String toOracleString() throws ConvertException {
      return this.toOracleShow().toString();
   }

   public String toMSSQLServerString() throws ConvertException {
      return this.toMSSQLServerShow().toString();
   }

   public String toSybaseString() throws ConvertException {
      return this.toSybaseShow().toString();
   }

   public String toDB2String() throws ConvertException {
      return this.toDB2Show().toString();
   }

   public String toPostgreSQLString() throws ConvertException {
      return this.toPostgreSQLShow().toString();
   }

   public String toBigQueryString() throws ConvertException {
      return this.toBigQueryShow().toString();
   }

   public String toMySQLString() throws ConvertException {
      return this.toMySQLShow().toString();
   }

   public String toANSIString() throws ConvertException {
      return this.toANSIShow().toString();
   }

   public String toTeradataString() throws ConvertException {
      return this.toTeradataShow().toString();
   }

   public String toInformixString() throws ConvertException {
      return this.toInformixShow().toString();
   }

   public String toTimesTenString() throws ConvertException {
      return this.toTimesTenShow().toString();
   }

   public String toNetezzaString() throws ConvertException {
      return this.toNetezzaShow().toString();
   }

   public String toSnowflakeString() throws ConvertException {
      return this.toSnowflakeShow().toString();
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

   public void setCommentClass(CommentClass commentObject) {
   }

   public CommentClass getCommentClass() {
      return null;
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

   public SwisSQLStatement toOracleShow() throws ConvertException {
      SelectQueryStatement selectQueryStmt = new SelectQueryStatement();
      SelectStatement selectStmt = new SelectStatement();
      boolean isShowTables = false;
      if (this.getShow() != null) {
         selectStmt.setSelectClause("SELECT");
      }

      Vector selectItems;
      Vector rightColExpr;
      if (this.getSelectColumn() != null) {
         String selectColStr = this.getSelectColumn();
         selectItems = new Vector();
         SelectColumn selectCol = new SelectColumn();
         Vector selectColVector = new Vector();
         if (this.selectColumnStr.equalsIgnoreCase("TABLES")) {
            selectColVector.add("TABLE_NAME");
            isShowTables = true;
            FromClause fromClause = new FromClause();
            fromClause.setFromClause("FROM");
            rightColExpr = new Vector();
            rightColExpr.add("USER_TABLES");
            fromClause.setFromItemList(rightColExpr);
            selectQueryStmt.setFromClause(fromClause);
         } else if (this.selectColumnStr.equalsIgnoreCase("FIELDS") || this.selectColumnStr.equalsIgnoreCase("COLUMNS")) {
            selectColVector.add("*");
            isShowTables = false;
         }

         selectCol.setColumnExpression(selectColVector);
         selectItems.add(selectCol);
         selectStmt.setSelectItemList(selectItems);
         selectQueryStmt.setSelectStatement(selectStmt);
      }

      Vector whereItemVector;
      if (this.getFrom() != null && this.getFromItem() != null) {
         FromClause fromClause = new FromClause();
         fromClause.setFromClause("FROM");
         selectItems = new Vector();
         selectItems.add("USER_TAB_COLUMNS");
         fromClause.setFromItemList(selectItems);
         selectQueryStmt.setFromClause(fromClause);
         WhereExpression whereExp = new WhereExpression();
         new Vector();
         WhereItem whereItem = new WhereItem();
         WhereColumn leftWhereColumn = new WhereColumn();
         whereItemVector = new Vector();
         TableColumn leftTableColumn = new TableColumn();
         leftTableColumn.setColumnName("TABLE_NAME");
         whereItemVector.add(leftTableColumn);
         leftWhereColumn.setColumnExpression(whereItemVector);
         WhereColumn rightWhereColumn = new WhereColumn();
         Vector rightColExpr = new Vector();
         FromTable fromItemTable = this.getFromItem();
         Object fromItemTableName = fromItemTable.getTableName();
         if (fromItemTableName instanceof TableObject) {
            TableObject fromItemTableObject = (TableObject)fromItemTableName;
            String fromItemTableObjectName = fromItemTableObject.getTableName();
            rightColExpr.add("'" + fromItemTableObjectName.toUpperCase() + "'");
            rightWhereColumn.setColumnExpression(rightColExpr);
         } else if (fromItemTableName instanceof String) {
            rightColExpr.add("'" + fromItemTableName.toString().toUpperCase() + "'");
            rightWhereColumn.setColumnExpression(rightColExpr);
         }

         whereItem.setLeftWhereExp(leftWhereColumn);
         whereItem.setRightWhereExp(rightWhereColumn);
         whereItem.setOperator("=");
         Vector whereItemVector = new Vector();
         whereItemVector.add(whereItem);
         whereExp.setWhereItem(whereItemVector);
         selectQueryStmt.setWhereExpression(whereExp);
      }

      if (this.getLike() != null && this.getLikePattern() != null) {
         WhereItem likeWhereItem = new WhereItem();
         WhereColumn leftWhereColumn = new WhereColumn();
         Vector leftColExpr = new Vector();
         TableColumn leftTableColumn = new TableColumn();
         if (isShowTables) {
            leftTableColumn.setColumnName("TABLE_NAME");
         } else {
            leftTableColumn.setColumnName("COLUMN_NAME");
         }

         leftColExpr.add(leftTableColumn);
         leftWhereColumn.setColumnExpression(leftColExpr);
         WhereColumn rightWhereColumn = new WhereColumn();
         rightColExpr = new Vector();
         rightColExpr.add(this.getLikePattern());
         rightWhereColumn.setColumnExpression(rightColExpr);
         likeWhereItem.setLeftWhereExp(leftWhereColumn);
         likeWhereItem.setRightWhereExp(rightWhereColumn);
         likeWhereItem.setOperator("LIKE");
         whereItemVector = new Vector();
         whereItemVector.add(likeWhereItem);
         if (selectQueryStmt.getWhereExpression() != null) {
            if (selectQueryStmt.getWhereExpression().getOperator() != null) {
               selectQueryStmt.getWhereExpression().getOperator().add("AND");
            }

            selectQueryStmt.getWhereExpression().getWhereItem().add(likeWhereItem);
         } else {
            WhereExpression whereExp = new WhereExpression();
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
         }
      }

      return selectQueryStmt;
   }

   public SwisSQLStatement toMSSQLServerShow() throws ConvertException {
      SelectQueryStatement selectQueryStmt = new SelectQueryStatement();
      SelectStatement selectStmt = new SelectStatement();
      boolean isShowTables = false;
      if (this.getShow() != null) {
         selectStmt.setSelectClause("SELECT");
      }

      Vector selectItems;
      Vector rightColExpr;
      if (this.getSelectColumn() != null) {
         String selectColStr = this.getSelectColumn();
         selectItems = new Vector();
         SelectColumn selectCol = new SelectColumn();
         Vector selectColVector = new Vector();
         if (this.selectColumnStr.equalsIgnoreCase("TABLES")) {
            selectColVector.add("TABLE_NAME");
            isShowTables = true;
            FromClause fromClause = new FromClause();
            fromClause.setFromClause("FROM");
            rightColExpr = new Vector();
            rightColExpr.add("INFORMATION_SCHEMA.TABLES");
            fromClause.setFromItemList(rightColExpr);
            selectQueryStmt.setFromClause(fromClause);
         } else if (this.selectColumnStr.equalsIgnoreCase("FIELDS") || this.selectColumnStr.equalsIgnoreCase("COLUMNS")) {
            selectColVector.add("*");
            isShowTables = false;
         }

         selectCol.setColumnExpression(selectColVector);
         selectItems.add(selectCol);
         selectStmt.setSelectItemList(selectItems);
         selectQueryStmt.setSelectStatement(selectStmt);
      }

      Vector whereItemVector;
      if (this.getFrom() != null && this.getFromItem() != null) {
         FromClause fromClause = new FromClause();
         fromClause.setFromClause("FROM");
         selectItems = new Vector();
         selectItems.add("INFORMATION_SCHEMA.COLUMNS");
         fromClause.setFromItemList(selectItems);
         selectQueryStmt.setFromClause(fromClause);
         WhereExpression whereExp = new WhereExpression();
         new Vector();
         WhereItem whereItem = new WhereItem();
         WhereColumn leftWhereColumn = new WhereColumn();
         whereItemVector = new Vector();
         TableColumn leftTableColumn = new TableColumn();
         leftTableColumn.setColumnName("TABLE_NAME");
         whereItemVector.add(leftTableColumn);
         leftWhereColumn.setColumnExpression(whereItemVector);
         WhereColumn rightWhereColumn = new WhereColumn();
         Vector rightColExpr = new Vector();
         FromTable fromItemTable = this.getFromItem();
         Object fromItemTableName = fromItemTable.getTableName();
         if (fromItemTableName instanceof TableObject) {
            TableObject fromItemTableObject = (TableObject)fromItemTableName;
            String fromItemTableObjectName = fromItemTableObject.getTableName();
            rightColExpr.add("'" + fromItemTableObjectName + "'");
            rightWhereColumn.setColumnExpression(rightColExpr);
         } else if (fromItemTableName instanceof String) {
            rightColExpr.add("'" + fromItemTableName.toString() + "'");
            rightWhereColumn.setColumnExpression(rightColExpr);
         }

         whereItem.setLeftWhereExp(leftWhereColumn);
         whereItem.setRightWhereExp(rightWhereColumn);
         whereItem.setOperator("=");
         Vector whereItemVector = new Vector();
         whereItemVector.add(whereItem);
         whereExp.setWhereItem(whereItemVector);
         selectQueryStmt.setWhereExpression(whereExp);
      }

      if (this.getLike() != null && this.getLikePattern() != null) {
         WhereItem likeWhereItem = new WhereItem();
         WhereColumn leftWhereColumn = new WhereColumn();
         Vector leftColExpr = new Vector();
         TableColumn leftTableColumn = new TableColumn();
         if (isShowTables) {
            leftTableColumn.setColumnName("TABLE_NAME");
         } else {
            leftTableColumn.setColumnName("COLUMN_NAME");
         }

         leftColExpr.add(leftTableColumn);
         leftWhereColumn.setColumnExpression(leftColExpr);
         WhereColumn rightWhereColumn = new WhereColumn();
         rightColExpr = new Vector();
         rightColExpr.add(this.getLikePattern());
         rightWhereColumn.setColumnExpression(rightColExpr);
         likeWhereItem.setLeftWhereExp(leftWhereColumn);
         likeWhereItem.setRightWhereExp(rightWhereColumn);
         likeWhereItem.setOperator("LIKE");
         whereItemVector = new Vector();
         whereItemVector.add(likeWhereItem);
         if (selectQueryStmt.getWhereExpression() != null) {
            if (selectQueryStmt.getWhereExpression().getOperator() != null) {
               selectQueryStmt.getWhereExpression().getOperator().add("AND");
            }

            selectQueryStmt.getWhereExpression().getWhereItem().add(likeWhereItem);
         } else {
            WhereExpression whereExp = new WhereExpression();
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
         }
      }

      return selectQueryStmt;
   }

   public SwisSQLStatement toSybaseShow() throws ConvertException {
      SelectQueryStatement selectQueryStmt = new SelectQueryStatement();
      SelectStatement selectStmt = new SelectStatement();
      boolean isShowTables = false;
      if (this.getShow() != null) {
         selectStmt.setSelectClause("SELECT");
      }

      Vector selectItems;
      Vector rightColExpr;
      if (this.getSelectColumn() != null) {
         String selectColStr = this.getSelectColumn();
         selectItems = new Vector();
         SelectColumn selectCol = new SelectColumn();
         Vector selectColVector = new Vector();
         if (this.selectColumnStr.equalsIgnoreCase("TABLES")) {
            selectColVector.add("name");
            isShowTables = true;
            FromClause fromClause = new FromClause();
            fromClause.setFromClause("FROM");
            rightColExpr = new Vector();
            rightColExpr.add("sysobjects");
            fromClause.setFromItemList(rightColExpr);
            selectQueryStmt.setFromClause(fromClause);
            WhereExpression whereExp = new WhereExpression();
            new Vector();
            WhereItem whereItem = new WhereItem();
            WhereColumn leftWhereColumn = new WhereColumn();
            Vector leftColExpr = new Vector();
            TableColumn leftTableColumn = new TableColumn();
            leftTableColumn.setColumnName("type");
            leftColExpr.add(leftTableColumn);
            leftWhereColumn.setColumnExpression(leftColExpr);
            WhereColumn rightWhereColumn = new WhereColumn();
            Vector rightColExpr = new Vector();
            rightColExpr.add("'U'");
            rightWhereColumn.setColumnExpression(rightColExpr);
            whereItem.setLeftWhereExp(leftWhereColumn);
            whereItem.setRightWhereExp(rightWhereColumn);
            whereItem.setOperator("=");
            Vector whereItemVector = new Vector();
            whereItemVector.add(whereItem);
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
         } else if (this.selectColumnStr.equalsIgnoreCase("FIELDS") || this.selectColumnStr.equalsIgnoreCase("COLUMNS")) {
            selectColVector.add("syscolumns.*");
            isShowTables = false;
         }

         selectCol.setColumnExpression(selectColVector);
         selectItems.add(selectCol);
         selectStmt.setSelectItemList(selectItems);
         selectQueryStmt.setSelectStatement(selectStmt);
      }

      Vector whereItemVector;
      if (this.getFrom() != null && this.getFromItem() != null) {
         FromClause fromClause = new FromClause();
         fromClause.setFromClause("FROM");
         selectItems = new Vector();
         selectItems.add("syscolumns");
         selectItems.add("sysobjects");
         fromClause.setFromItemList(selectItems);
         selectQueryStmt.setFromClause(fromClause);
         WhereExpression whereExp = new WhereExpression();
         new Vector();
         WhereItem whereItem1 = new WhereItem();
         WhereColumn leftWhereColumn1 = new WhereColumn();
         whereItemVector = new Vector();
         TableColumn leftTableColumn1 = new TableColumn();
         leftTableColumn1.setColumnName("syscolumns.id");
         whereItemVector.add(leftTableColumn1);
         leftWhereColumn1.setColumnExpression(whereItemVector);
         WhereColumn rightWhereColumn1 = new WhereColumn();
         Vector rightColExpr1 = new Vector();
         rightColExpr1.add("sysobjects.id");
         rightWhereColumn1.setColumnExpression(rightColExpr1);
         whereItem1.setLeftWhereExp(leftWhereColumn1);
         whereItem1.setRightWhereExp(rightWhereColumn1);
         whereItem1.setOperator("=");
         WhereItem whereItem2 = new WhereItem();
         WhereColumn leftWhereColumn2 = new WhereColumn();
         Vector leftColExpr2 = new Vector();
         TableColumn leftTableColumn2 = new TableColumn();
         leftTableColumn2.setColumnName("sysobjects.name");
         leftColExpr2.add(leftTableColumn2);
         leftWhereColumn2.setColumnExpression(leftColExpr2);
         WhereColumn rightWhereColumn2 = new WhereColumn();
         Vector rightColExpr2 = new Vector();
         FromTable fromItemTable = this.getFromItem();
         Object fromItemTableName = fromItemTable.getTableName();
         if (fromItemTableName instanceof TableObject) {
            TableObject fromItemTableObject = (TableObject)fromItemTableName;
            String fromItemTableObjectName = fromItemTableObject.getTableName();
            rightColExpr2.add("'" + fromItemTableObjectName + "'");
            rightWhereColumn2.setColumnExpression(rightColExpr2);
         } else if (fromItemTableName instanceof String) {
            rightColExpr2.add("'" + fromItemTableName.toString() + "'");
            rightWhereColumn2.setColumnExpression(rightColExpr2);
         }

         whereItem2.setLeftWhereExp(leftWhereColumn2);
         whereItem2.setRightWhereExp(rightWhereColumn2);
         whereItem2.setOperator("=");
         Vector whereItemVector = new Vector();
         whereItemVector.add(whereItem1);
         whereItemVector.add(whereItem2);
         whereExp.setWhereItem(whereItemVector);
         Vector whereExpOperatorVec = new Vector();
         whereExpOperatorVec.add("AND");
         whereExp.setOperator(whereExpOperatorVec);
         selectQueryStmt.setWhereExpression(whereExp);
      }

      if (this.getLike() != null && this.getLikePattern() != null) {
         WhereItem likeWhereItem = new WhereItem();
         WhereColumn leftWhereColumn = new WhereColumn();
         Vector leftColExpr = new Vector();
         TableColumn leftTableColumn = new TableColumn();
         if (isShowTables) {
            leftTableColumn.setColumnName("name");
         } else {
            leftTableColumn.setColumnName("syscolumns.name");
         }

         leftColExpr.add(leftTableColumn);
         leftWhereColumn.setColumnExpression(leftColExpr);
         WhereColumn rightWhereColumn = new WhereColumn();
         rightColExpr = new Vector();
         rightColExpr.add(this.getLikePattern());
         rightWhereColumn.setColumnExpression(rightColExpr);
         likeWhereItem.setLeftWhereExp(leftWhereColumn);
         likeWhereItem.setRightWhereExp(rightWhereColumn);
         likeWhereItem.setOperator("LIKE");
         whereItemVector = new Vector();
         whereItemVector.add(likeWhereItem);
         if (selectQueryStmt.getWhereExpression() != null) {
            if (selectQueryStmt.getWhereExpression().getOperator() != null) {
               selectQueryStmt.getWhereExpression().getOperator().add("AND");
            }

            selectQueryStmt.getWhereExpression().getWhereItem().add(likeWhereItem);
         } else {
            WhereExpression whereExp = new WhereExpression();
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
         }
      }

      return selectQueryStmt;
   }

   public SwisSQLStatement toDB2Show() throws ConvertException {
      SelectQueryStatement selectQueryStmt = new SelectQueryStatement();
      SelectStatement selectStmt = new SelectStatement();
      boolean isShowTables = false;
      if (this.getShow() != null) {
         selectStmt.setSelectClause("SELECT");
      }

      Vector selectItems;
      Vector rightColExpr;
      if (this.getSelectColumn() != null) {
         String selectColStr = this.getSelectColumn();
         selectItems = new Vector();
         SelectColumn selectCol = new SelectColumn();
         Vector selectColVector = new Vector();
         if (this.selectColumnStr.equalsIgnoreCase("TABLES")) {
            selectColVector.add("TABNAME");
            isShowTables = true;
            FromClause fromClause = new FromClause();
            fromClause.setFromClause("FROM");
            rightColExpr = new Vector();
            rightColExpr.add("SYSCAT.TABLES");
            fromClause.setFromItemList(rightColExpr);
            selectQueryStmt.setFromClause(fromClause);
         } else if (this.selectColumnStr.equalsIgnoreCase("FIELDS") || this.selectColumnStr.equalsIgnoreCase("COLUMNS")) {
            selectColVector.add("*");
            isShowTables = false;
         }

         selectCol.setColumnExpression(selectColVector);
         selectItems.add(selectCol);
         selectStmt.setSelectItemList(selectItems);
         selectQueryStmt.setSelectStatement(selectStmt);
      }

      Vector whereItemVector;
      if (this.getFrom() != null && this.getFromItem() != null) {
         FromClause fromClause = new FromClause();
         fromClause.setFromClause("FROM");
         selectItems = new Vector();
         selectItems.add("SYSCAT.COLUMNS");
         fromClause.setFromItemList(selectItems);
         selectQueryStmt.setFromClause(fromClause);
         WhereExpression whereExp = new WhereExpression();
         new Vector();
         WhereItem whereItem = new WhereItem();
         WhereColumn leftWhereColumn = new WhereColumn();
         whereItemVector = new Vector();
         TableColumn leftTableColumn = new TableColumn();
         leftTableColumn.setColumnName("TABNAME");
         whereItemVector.add(leftTableColumn);
         leftWhereColumn.setColumnExpression(whereItemVector);
         WhereColumn rightWhereColumn = new WhereColumn();
         Vector rightColExpr = new Vector();
         FromTable fromItemTable = this.getFromItem();
         Object fromItemTableName = fromItemTable.getTableName();
         if (fromItemTableName instanceof TableObject) {
            TableObject fromItemTableObject = (TableObject)fromItemTableName;
            String fromItemTableObjectName = fromItemTableObject.getTableName();
            rightColExpr.add("'" + fromItemTableObjectName.toUpperCase() + "'");
            rightWhereColumn.setColumnExpression(rightColExpr);
         } else if (fromItemTableName instanceof String) {
            rightColExpr.add("'" + fromItemTableName.toString().toUpperCase() + "'");
            rightWhereColumn.setColumnExpression(rightColExpr);
         }

         whereItem.setLeftWhereExp(leftWhereColumn);
         whereItem.setRightWhereExp(rightWhereColumn);
         whereItem.setOperator("=");
         Vector whereItemVector = new Vector();
         whereItemVector.add(whereItem);
         whereExp.setWhereItem(whereItemVector);
         selectQueryStmt.setWhereExpression(whereExp);
      }

      if (this.getLike() != null && this.getLikePattern() != null) {
         WhereItem likeWhereItem = new WhereItem();
         WhereColumn leftWhereColumn = new WhereColumn();
         Vector leftColExpr = new Vector();
         TableColumn leftTableColumn = new TableColumn();
         if (isShowTables) {
            leftTableColumn.setColumnName("TABNAME");
         } else {
            leftTableColumn.setColumnName("COLNAME");
         }

         leftColExpr.add(leftTableColumn);
         leftWhereColumn.setColumnExpression(leftColExpr);
         WhereColumn rightWhereColumn = new WhereColumn();
         rightColExpr = new Vector();
         rightColExpr.add(this.getLikePattern());
         rightWhereColumn.setColumnExpression(rightColExpr);
         likeWhereItem.setLeftWhereExp(leftWhereColumn);
         likeWhereItem.setRightWhereExp(rightWhereColumn);
         likeWhereItem.setOperator("LIKE");
         whereItemVector = new Vector();
         whereItemVector.add(likeWhereItem);
         if (selectQueryStmt.getWhereExpression() != null) {
            if (selectQueryStmt.getWhereExpression().getOperator() != null) {
               selectQueryStmt.getWhereExpression().getOperator().add("AND");
            }

            selectQueryStmt.getWhereExpression().getWhereItem().add(likeWhereItem);
         } else {
            WhereExpression whereExp = new WhereExpression();
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
         }
      }

      return selectQueryStmt;
   }

   public SwisSQLStatement toBigQueryShow() throws ConvertException {
      SelectQueryStatement selectQueryStmt = new SelectQueryStatement();
      SelectStatement selectStmt = new SelectStatement();
      boolean isShowTables = false;
      if (this.getShow() != null) {
         selectStmt.setSelectClause("SELECT");
      }

      Vector selectItems;
      Vector rightColExpr;
      if (this.getSelectColumn() != null) {
         String selectColStr = this.getSelectColumn();
         selectItems = new Vector();
         SelectColumn selectCol = new SelectColumn();
         Vector selectColVector = new Vector();
         if (this.selectColumnStr.equalsIgnoreCase("TABLES")) {
            selectColVector.add("TABLE_NAME");
            isShowTables = true;
            FromClause fromClause = new FromClause();
            fromClause.setFromClause("FROM");
            rightColExpr = new Vector();
            rightColExpr.add("INFORMATION_SCHEMA.TABLES");
            fromClause.setFromItemList(rightColExpr);
            selectQueryStmt.setFromClause(fromClause);
            WhereExpression whereExp = new WhereExpression();
            new Vector();
            WhereItem whereItem = new WhereItem();
            WhereColumn leftWhereColumn = new WhereColumn();
            Vector leftColExpr = new Vector();
            TableColumn leftTableColumn = new TableColumn();
            leftTableColumn.setColumnName("TABLE_SCHEMA");
            leftColExpr.add(leftTableColumn);
            leftWhereColumn.setColumnExpression(leftColExpr);
            WhereColumn rightWhereColumn = new WhereColumn();
            Vector rightColExpr = new Vector();
            rightColExpr.add("'public'");
            rightWhereColumn.setColumnExpression(rightColExpr);
            whereItem.setLeftWhereExp(leftWhereColumn);
            whereItem.setRightWhereExp(rightWhereColumn);
            whereItem.setOperator("=");
            Vector whereItemVector = new Vector();
            whereItemVector.add(whereItem);
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
         } else if (this.selectColumnStr.equalsIgnoreCase("FIELDS") || this.selectColumnStr.equalsIgnoreCase("COLUMNS")) {
            selectColVector.add("*");
            isShowTables = false;
         }

         selectCol.setColumnExpression(selectColVector);
         selectItems.add(selectCol);
         selectStmt.setSelectItemList(selectItems);
         selectQueryStmt.setSelectStatement(selectStmt);
      }

      Vector whereItemVector;
      if (this.getFrom() != null && this.getFromItem() != null) {
         FromClause fromClause = new FromClause();
         fromClause.setFromClause("FROM");
         selectItems = new Vector();
         selectItems.add("INFORMATION_SCHEMA.COLUMNS");
         fromClause.setFromItemList(selectItems);
         selectQueryStmt.setFromClause(fromClause);
         WhereExpression whereExp = new WhereExpression();
         new Vector();
         WhereItem whereItem = new WhereItem();
         WhereColumn leftWhereColumn = new WhereColumn();
         whereItemVector = new Vector();
         TableColumn leftTableColumn = new TableColumn();
         leftTableColumn.setColumnName("TABLE_NAME");
         whereItemVector.add(leftTableColumn);
         leftWhereColumn.setColumnExpression(whereItemVector);
         WhereColumn rightWhereColumn = new WhereColumn();
         Vector rightColExpr = new Vector();
         FromTable fromItemTable = this.getFromItem();
         Object fromItemTableName = fromItemTable.getTableName();
         if (fromItemTableName instanceof TableObject) {
            TableObject fromItemTableObject = (TableObject)fromItemTableName;
            String fromItemTableObjectName = fromItemTableObject.getTableName();
            rightColExpr.add("'" + fromItemTableObjectName + "'");
            rightWhereColumn.setColumnExpression(rightColExpr);
         } else if (fromItemTableName instanceof String) {
            rightColExpr.add("'" + fromItemTableName.toString() + "'");
            rightWhereColumn.setColumnExpression(rightColExpr);
         }

         whereItem.setLeftWhereExp(leftWhereColumn);
         whereItem.setRightWhereExp(rightWhereColumn);
         whereItem.setOperator("=");
         Vector whereItemVector = new Vector();
         whereItemVector.add(whereItem);
         whereExp.setWhereItem(whereItemVector);
         selectQueryStmt.setWhereExpression(whereExp);
      }

      if (this.getLike() != null && this.getLikePattern() != null) {
         WhereItem likeWhereItem = new WhereItem();
         WhereColumn leftWhereColumn = new WhereColumn();
         Vector leftColExpr = new Vector();
         TableColumn leftTableColumn = new TableColumn();
         if (isShowTables) {
            leftTableColumn.setColumnName("TABLE_NAME");
         } else {
            leftTableColumn.setColumnName("COLUMN_NAME");
         }

         leftColExpr.add(leftTableColumn);
         leftWhereColumn.setColumnExpression(leftColExpr);
         WhereColumn rightWhereColumn = new WhereColumn();
         rightColExpr = new Vector();
         rightColExpr.add(this.getLikePattern());
         rightWhereColumn.setColumnExpression(rightColExpr);
         likeWhereItem.setLeftWhereExp(leftWhereColumn);
         likeWhereItem.setRightWhereExp(rightWhereColumn);
         likeWhereItem.setOperator("LIKE");
         whereItemVector = new Vector();
         whereItemVector.add(likeWhereItem);
         if (selectQueryStmt.getWhereExpression() != null) {
            if (selectQueryStmt.getWhereExpression().getOperator() != null) {
               selectQueryStmt.getWhereExpression().getOperator().add("AND");
            }

            selectQueryStmt.getWhereExpression().getWhereItem().add(likeWhereItem);
         } else {
            WhereExpression whereExp = new WhereExpression();
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
         }
      }

      return selectQueryStmt;
   }

   public SwisSQLStatement toPostgreSQLShow() throws ConvertException {
      SelectQueryStatement selectQueryStmt = new SelectQueryStatement();
      SelectStatement selectStmt = new SelectStatement();
      boolean isShowTables = false;
      if (this.getShow() != null) {
         selectStmt.setSelectClause("SELECT");
      }

      Vector selectItems;
      Vector rightColExpr;
      if (this.getSelectColumn() != null) {
         String selectColStr = this.getSelectColumn();
         selectItems = new Vector();
         SelectColumn selectCol = new SelectColumn();
         Vector selectColVector = new Vector();
         if (this.selectColumnStr.equalsIgnoreCase("TABLES")) {
            selectColVector.add("TABLE_NAME");
            isShowTables = true;
            FromClause fromClause = new FromClause();
            fromClause.setFromClause("FROM");
            rightColExpr = new Vector();
            rightColExpr.add("INFORMATION_SCHEMA.TABLES");
            fromClause.setFromItemList(rightColExpr);
            selectQueryStmt.setFromClause(fromClause);
            WhereExpression whereExp = new WhereExpression();
            new Vector();
            WhereItem whereItem = new WhereItem();
            WhereColumn leftWhereColumn = new WhereColumn();
            Vector leftColExpr = new Vector();
            TableColumn leftTableColumn = new TableColumn();
            leftTableColumn.setColumnName("TABLE_SCHEMA");
            leftColExpr.add(leftTableColumn);
            leftWhereColumn.setColumnExpression(leftColExpr);
            WhereColumn rightWhereColumn = new WhereColumn();
            Vector rightColExpr = new Vector();
            rightColExpr.add("'public'");
            rightWhereColumn.setColumnExpression(rightColExpr);
            whereItem.setLeftWhereExp(leftWhereColumn);
            whereItem.setRightWhereExp(rightWhereColumn);
            whereItem.setOperator("=");
            Vector whereItemVector = new Vector();
            whereItemVector.add(whereItem);
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
         } else if (this.selectColumnStr.equalsIgnoreCase("FIELDS") || this.selectColumnStr.equalsIgnoreCase("COLUMNS")) {
            selectColVector.add("*");
            isShowTables = false;
         }

         selectCol.setColumnExpression(selectColVector);
         selectItems.add(selectCol);
         selectStmt.setSelectItemList(selectItems);
         selectQueryStmt.setSelectStatement(selectStmt);
      }

      Vector whereItemVector;
      if (this.getFrom() != null && this.getFromItem() != null) {
         FromClause fromClause = new FromClause();
         fromClause.setFromClause("FROM");
         selectItems = new Vector();
         selectItems.add("INFORMATION_SCHEMA.COLUMNS");
         fromClause.setFromItemList(selectItems);
         selectQueryStmt.setFromClause(fromClause);
         WhereExpression whereExp = new WhereExpression();
         new Vector();
         WhereItem whereItem = new WhereItem();
         WhereColumn leftWhereColumn = new WhereColumn();
         whereItemVector = new Vector();
         TableColumn leftTableColumn = new TableColumn();
         leftTableColumn.setColumnName("TABLE_NAME");
         whereItemVector.add(leftTableColumn);
         leftWhereColumn.setColumnExpression(whereItemVector);
         WhereColumn rightWhereColumn = new WhereColumn();
         Vector rightColExpr = new Vector();
         FromTable fromItemTable = this.getFromItem();
         Object fromItemTableName = fromItemTable.getTableName();
         if (fromItemTableName instanceof TableObject) {
            TableObject fromItemTableObject = (TableObject)fromItemTableName;
            String fromItemTableObjectName = fromItemTableObject.getTableName();
            rightColExpr.add("'" + fromItemTableObjectName + "'");
            rightWhereColumn.setColumnExpression(rightColExpr);
         } else if (fromItemTableName instanceof String) {
            rightColExpr.add("'" + fromItemTableName.toString() + "'");
            rightWhereColumn.setColumnExpression(rightColExpr);
         }

         whereItem.setLeftWhereExp(leftWhereColumn);
         whereItem.setRightWhereExp(rightWhereColumn);
         whereItem.setOperator("=");
         Vector whereItemVector = new Vector();
         whereItemVector.add(whereItem);
         whereExp.setWhereItem(whereItemVector);
         selectQueryStmt.setWhereExpression(whereExp);
      }

      if (this.getLike() != null && this.getLikePattern() != null) {
         WhereItem likeWhereItem = new WhereItem();
         WhereColumn leftWhereColumn = new WhereColumn();
         Vector leftColExpr = new Vector();
         TableColumn leftTableColumn = new TableColumn();
         if (isShowTables) {
            leftTableColumn.setColumnName("TABLE_NAME");
         } else {
            leftTableColumn.setColumnName("COLUMN_NAME");
         }

         leftColExpr.add(leftTableColumn);
         leftWhereColumn.setColumnExpression(leftColExpr);
         WhereColumn rightWhereColumn = new WhereColumn();
         rightColExpr = new Vector();
         rightColExpr.add(this.getLikePattern());
         rightWhereColumn.setColumnExpression(rightColExpr);
         likeWhereItem.setLeftWhereExp(leftWhereColumn);
         likeWhereItem.setRightWhereExp(rightWhereColumn);
         likeWhereItem.setOperator("LIKE");
         whereItemVector = new Vector();
         whereItemVector.add(likeWhereItem);
         if (selectQueryStmt.getWhereExpression() != null) {
            if (selectQueryStmt.getWhereExpression().getOperator() != null) {
               selectQueryStmt.getWhereExpression().getOperator().add("AND");
            }

            selectQueryStmt.getWhereExpression().getWhereItem().add(likeWhereItem);
         } else {
            WhereExpression whereExp = new WhereExpression();
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
         }
      }

      return selectQueryStmt;
   }

   public SwisSQLStatement toMySQLShow() throws ConvertException {
      ShowStatement show = new ShowStatement();
      if (this.getShow() != null) {
         show.setShow("SHOW");
      }

      String status;
      if (this.getSelectColumn() != null) {
         status = this.getSelectColumn();
         show.setSelectColumn(status);
      }

      if (this.getStatus() != null) {
         status = this.getStatus();
         show.setStatus(status);
      }

      if (this.getFrom() != null && this.getFromItem() != null) {
         show.setFrom("FROM");
         show.setFromItem(this.getFromItem());
      }

      if (this.getLike() != null && this.getLikePattern() != null) {
         show.setLike("LIKE");
         show.setLikePattern(this.getLikePattern());
      }

      return show;
   }

   public SwisSQLStatement toSnowflakeShow() throws ConvertException {
      SelectQueryStatement selectQueryStmt = new SelectQueryStatement();
      SelectStatement selectStmt = new SelectStatement();
      boolean isShowTables = false;
      if (this.getShow() != null) {
         selectStmt.setSelectClause("SELECT");
      }

      Vector selectItems;
      Vector rightColExpr;
      if (this.getSelectColumn() != null) {
         String selectColStr = this.getSelectColumn();
         selectItems = new Vector();
         SelectColumn selectCol = new SelectColumn();
         Vector selectColVector = new Vector();
         if (this.selectColumnStr.equalsIgnoreCase("TABLES")) {
            selectColVector.add("TABLE_NAME");
            isShowTables = true;
            FromClause fromClause = new FromClause();
            fromClause.setFromClause("FROM");
            rightColExpr = new Vector();
            rightColExpr.add("INFORMATION_SCHEMA.TABLES");
            fromClause.setFromItemList(rightColExpr);
            selectQueryStmt.setFromClause(fromClause);
            WhereExpression whereExp = new WhereExpression();
            new Vector();
            WhereItem whereItem = new WhereItem();
            WhereColumn leftWhereColumn = new WhereColumn();
            Vector leftColExpr = new Vector();
            TableColumn leftTableColumn = new TableColumn();
            leftTableColumn.setColumnName("TABLE_SCHEMA");
            leftColExpr.add(leftTableColumn);
            leftWhereColumn.setColumnExpression(leftColExpr);
            WhereColumn rightWhereColumn = new WhereColumn();
            Vector rightColExpr = new Vector();
            rightColExpr.add("'public'");
            rightWhereColumn.setColumnExpression(rightColExpr);
            whereItem.setLeftWhereExp(leftWhereColumn);
            whereItem.setRightWhereExp(rightWhereColumn);
            whereItem.setOperator("=");
            Vector whereItemVector = new Vector();
            whereItemVector.add(whereItem);
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
         } else if (this.selectColumnStr.equalsIgnoreCase("FIELDS") || this.selectColumnStr.equalsIgnoreCase("COLUMNS")) {
            selectColVector.add("*");
            isShowTables = false;
         }

         selectCol.setColumnExpression(selectColVector);
         selectItems.add(selectCol);
         selectStmt.setSelectItemList(selectItems);
         selectQueryStmt.setSelectStatement(selectStmt);
      }

      Vector whereItemVector;
      if (this.getFrom() != null && this.getFromItem() != null) {
         FromClause fromClause = new FromClause();
         fromClause.setFromClause("FROM");
         selectItems = new Vector();
         selectItems.add("INFORMATION_SCHEMA.COLUMNS");
         fromClause.setFromItemList(selectItems);
         selectQueryStmt.setFromClause(fromClause);
         WhereExpression whereExp = new WhereExpression();
         new Vector();
         WhereItem whereItem = new WhereItem();
         WhereColumn leftWhereColumn = new WhereColumn();
         whereItemVector = new Vector();
         TableColumn leftTableColumn = new TableColumn();
         leftTableColumn.setColumnName("TABLE_NAME");
         whereItemVector.add(leftTableColumn);
         leftWhereColumn.setColumnExpression(whereItemVector);
         WhereColumn rightWhereColumn = new WhereColumn();
         Vector rightColExpr = new Vector();
         FromTable fromItemTable = this.getFromItem();
         Object fromItemTableName = fromItemTable.getTableName();
         if (fromItemTableName instanceof TableObject) {
            TableObject fromItemTableObject = (TableObject)fromItemTableName;
            String fromItemTableObjectName = fromItemTableObject.getTableName();
            rightColExpr.add("'" + fromItemTableObjectName + "'");
            rightWhereColumn.setColumnExpression(rightColExpr);
         } else if (fromItemTableName instanceof String) {
            rightColExpr.add("'" + fromItemTableName.toString() + "'");
            rightWhereColumn.setColumnExpression(rightColExpr);
         }

         whereItem.setLeftWhereExp(leftWhereColumn);
         whereItem.setRightWhereExp(rightWhereColumn);
         whereItem.setOperator("=");
         Vector whereItemVector = new Vector();
         whereItemVector.add(whereItem);
         whereExp.setWhereItem(whereItemVector);
         selectQueryStmt.setWhereExpression(whereExp);
      }

      if (this.getLike() != null && this.getLikePattern() != null) {
         WhereItem likeWhereItem = new WhereItem();
         WhereColumn leftWhereColumn = new WhereColumn();
         Vector leftColExpr = new Vector();
         TableColumn leftTableColumn = new TableColumn();
         if (isShowTables) {
            leftTableColumn.setColumnName("TABLE_NAME");
         } else {
            leftTableColumn.setColumnName("COLUMN_NAME");
         }

         leftColExpr.add(leftTableColumn);
         leftWhereColumn.setColumnExpression(leftColExpr);
         WhereColumn rightWhereColumn = new WhereColumn();
         rightColExpr = new Vector();
         rightColExpr.add(this.getLikePattern());
         rightWhereColumn.setColumnExpression(rightColExpr);
         likeWhereItem.setLeftWhereExp(leftWhereColumn);
         likeWhereItem.setRightWhereExp(rightWhereColumn);
         likeWhereItem.setOperator("LIKE");
         whereItemVector = new Vector();
         whereItemVector.add(likeWhereItem);
         if (selectQueryStmt.getWhereExpression() != null) {
            if (selectQueryStmt.getWhereExpression().getOperator() != null) {
               selectQueryStmt.getWhereExpression().getOperator().add("AND");
            }

            selectQueryStmt.getWhereExpression().getWhereItem().add(likeWhereItem);
         } else {
            WhereExpression whereExp = new WhereExpression();
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
         }
      }

      return selectQueryStmt;
   }

   public SwisSQLStatement toANSIShow() throws ConvertException {
      throw new ConvertException("Show statement yet to be supported");
   }

   public SwisSQLStatement toTeradataShow() throws ConvertException {
      throw new ConvertException("Show statement yet to be supported");
   }

   public SwisSQLStatement toInformixShow() throws ConvertException {
      SelectQueryStatement selectQueryStmt = new SelectQueryStatement();
      SelectStatement selectStmt = new SelectStatement();
      boolean isShowTables = false;
      if (this.getShow() != null) {
         selectStmt.setSelectClause("SELECT");
      }

      Vector selectItems;
      Vector rightColExpr;
      if (this.getSelectColumn() != null) {
         String selectColStr = this.getSelectColumn();
         selectItems = new Vector();
         SelectColumn selectCol = new SelectColumn();
         Vector selectColVector = new Vector();
         if (this.selectColumnStr.equalsIgnoreCase("TABLES")) {
            selectColVector.add("tabname");
            isShowTables = true;
            FromClause fromClause = new FromClause();
            fromClause.setFromClause("FROM");
            rightColExpr = new Vector();
            rightColExpr.add("systables");
            fromClause.setFromItemList(rightColExpr);
            selectQueryStmt.setFromClause(fromClause);
            WhereExpression whereExp = new WhereExpression();
            new Vector();
            WhereItem whereItem = new WhereItem();
            WhereColumn leftWhereColumn = new WhereColumn();
            Vector leftColExpr = new Vector();
            TableColumn leftTableColumn = new TableColumn();
            leftTableColumn.setColumnName("tabid");
            leftColExpr.add(leftTableColumn);
            leftWhereColumn.setColumnExpression(leftColExpr);
            WhereColumn rightWhereColumn = new WhereColumn();
            Vector rightColExpr = new Vector();
            rightColExpr.add("99");
            rightWhereColumn.setColumnExpression(rightColExpr);
            whereItem.setLeftWhereExp(leftWhereColumn);
            whereItem.setRightWhereExp(rightWhereColumn);
            whereItem.setOperator(">");
            Vector whereItemVector = new Vector();
            whereItemVector.add(whereItem);
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
         } else if (this.selectColumnStr.equalsIgnoreCase("FIELDS") || this.selectColumnStr.equalsIgnoreCase("COLUMNS")) {
            selectColVector.add("syscolumns.*");
            isShowTables = false;
         }

         selectCol.setColumnExpression(selectColVector);
         selectItems.add(selectCol);
         selectStmt.setSelectItemList(selectItems);
         selectQueryStmt.setSelectStatement(selectStmt);
      }

      Vector whereItemVector;
      if (this.getFrom() != null && this.getFromItem() != null) {
         FromClause fromClause = new FromClause();
         fromClause.setFromClause("FROM");
         selectItems = new Vector();
         selectItems.add("syscolumns");
         selectItems.add("systables");
         fromClause.setFromItemList(selectItems);
         selectQueryStmt.setFromClause(fromClause);
         WhereExpression whereExp = new WhereExpression();
         new Vector();
         WhereItem whereItem1 = new WhereItem();
         WhereColumn leftWhereColumn1 = new WhereColumn();
         whereItemVector = new Vector();
         TableColumn leftTableColumn1 = new TableColumn();
         leftTableColumn1.setColumnName("systables.tabid");
         whereItemVector.add(leftTableColumn1);
         leftWhereColumn1.setColumnExpression(whereItemVector);
         WhereColumn rightWhereColumn1 = new WhereColumn();
         Vector rightColExpr1 = new Vector();
         rightColExpr1.add("syscolumns.tabid");
         rightWhereColumn1.setColumnExpression(rightColExpr1);
         whereItem1.setLeftWhereExp(leftWhereColumn1);
         whereItem1.setRightWhereExp(rightWhereColumn1);
         whereItem1.setOperator("=");
         WhereItem whereItem2 = new WhereItem();
         WhereColumn leftWhereColumn2 = new WhereColumn();
         Vector leftColExpr2 = new Vector();
         TableColumn leftTableColumn2 = new TableColumn();
         leftTableColumn2.setColumnName("systables.tabname");
         leftColExpr2.add(leftTableColumn2);
         leftWhereColumn2.setColumnExpression(leftColExpr2);
         WhereColumn rightWhereColumn2 = new WhereColumn();
         Vector rightColExpr2 = new Vector();
         FromTable fromItemTable = this.getFromItem();
         Object fromItemTableName = fromItemTable.getTableName();
         if (fromItemTableName instanceof TableObject) {
            TableObject fromItemTableObject = (TableObject)fromItemTableName;
            String fromItemTableObjectName = fromItemTableObject.getTableName();
            rightColExpr2.add("'" + fromItemTableObjectName + "'");
            rightWhereColumn2.setColumnExpression(rightColExpr2);
         } else if (fromItemTableName instanceof String) {
            rightColExpr2.add("'" + fromItemTableName.toString() + "'");
            rightWhereColumn2.setColumnExpression(rightColExpr2);
         }

         whereItem2.setLeftWhereExp(leftWhereColumn2);
         whereItem2.setRightWhereExp(rightWhereColumn2);
         whereItem2.setOperator("=");
         Vector whereItemVector = new Vector();
         whereItemVector.add(whereItem1);
         whereItemVector.add(whereItem2);
         whereExp.setWhereItem(whereItemVector);
         Vector whereExpOperatorVec = new Vector();
         whereExpOperatorVec.add("AND");
         whereExp.setOperator(whereExpOperatorVec);
         selectQueryStmt.setWhereExpression(whereExp);
      }

      if (this.getLike() != null && this.getLikePattern() != null) {
         WhereItem likeWhereItem = new WhereItem();
         WhereColumn leftWhereColumn = new WhereColumn();
         Vector leftColExpr = new Vector();
         TableColumn leftTableColumn = new TableColumn();
         if (isShowTables) {
            leftTableColumn.setColumnName("tabname");
         } else {
            leftTableColumn.setColumnName("syscolumns.colname");
         }

         leftColExpr.add(leftTableColumn);
         leftWhereColumn.setColumnExpression(leftColExpr);
         WhereColumn rightWhereColumn = new WhereColumn();
         rightColExpr = new Vector();
         rightColExpr.add(this.getLikePattern());
         rightWhereColumn.setColumnExpression(rightColExpr);
         likeWhereItem.setLeftWhereExp(leftWhereColumn);
         likeWhereItem.setRightWhereExp(rightWhereColumn);
         likeWhereItem.setOperator("LIKE");
         whereItemVector = new Vector();
         whereItemVector.add(likeWhereItem);
         if (selectQueryStmt.getWhereExpression() != null) {
            if (selectQueryStmt.getWhereExpression().getOperator() != null) {
               selectQueryStmt.getWhereExpression().getOperator().add("AND");
            }

            selectQueryStmt.getWhereExpression().getWhereItem().add(likeWhereItem);
         } else {
            WhereExpression whereExp = new WhereExpression();
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
         }
      }

      return selectQueryStmt;
   }

   public SwisSQLStatement toTimesTenShow() throws ConvertException {
      throw new ConvertException("Show statement yet to be supported");
   }

   public SwisSQLStatement toNetezzaShow() throws ConvertException {
      throw new ConvertException("Show statement yet to be supported");
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.getShow() != null) {
         sb.append("SHOW ");
      }

      if (this.getSelectColumn() != null) {
         String selectColStr = this.getSelectColumn();
         sb.append(selectColStr + " ");
      }

      if (this.getStatus() != null) {
         sb.append(this.getStatus() + " ");
      }

      if (this.getFrom() != null && this.getFromItem() != null) {
         sb.append("FROM ");
         sb.append(this.getFromItem().toString() + " ");
      }

      if (this.getLike() != null && this.getLikePattern() != null) {
         sb.append("LIKE ");
         sb.append(this.getLikePattern() + " ");
      }

      return sb.toString();
   }

   public String getStatus() {
      return this.status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String toVectorWiseString() throws ConvertException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public SwisSQLStatement toVectorWise() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toInformix() throws ConvertException {
      return this.toInformixShow();
   }

   public SwisSQLStatement toNetezza() throws ConvertException {
      return this.toNetezzaShow();
   }

   public SwisSQLStatement toTimesTen() throws ConvertException {
      return this.toTimesTenShow();
   }

   public SwisSQLStatement toOracle() throws ConvertException {
      return this.toOracleShow();
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
      return this.toSnowflakeShow();
   }

   public SwisSQLStatement toMySQL() throws ConvertException {
      return this.toMySQLShow();
   }

   public SwisSQLStatement toPostgreSQL() throws ConvertException {
      return this.toPostgreSQLShow();
   }

   public SwisSQLStatement toBigQuery() throws ConvertException {
      return this.toBigQueryShow();
   }

   public SwisSQLStatement toDB2() throws ConvertException {
      return this.toDB2Show();
   }

   public SwisSQLStatement toSybase() throws ConvertException {
      return this.toSybaseShow();
   }

   public SwisSQLStatement toMSSQLServer() throws ConvertException {
      return this.toMSSQLServerShow();
   }

   public SwisSQLStatement toTeradata() throws ConvertException {
      return this.toTeradataShow();
   }

   public SwisSQLStatement toANSI() throws ConvertException {
      return this.toANSIShow();
   }

   public SwisSQLStatement toMsAccessJdbc() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toExcel() throws ConvertException {
      return null;
   }
}
