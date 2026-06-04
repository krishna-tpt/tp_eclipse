package com.adventnet.swissqlapi.sql.statement.insert;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

public class CommonTableExpression {
   String with;
   TableObject viewName;
   ArrayList columnList = new ArrayList();
   String as;
   SelectQueryStatement sqs;

   public void setWith(String with) {
      this.with = with;
   }

   public void setViewName(TableObject viewName) {
      this.viewName = viewName;
   }

   public void setColumnList(ArrayList columnList) {
      this.columnList = columnList;
   }

   public void setAs(String as) {
      this.as = as;
   }

   public void setSelectQueryStatement(SelectQueryStatement sqs) {
      this.sqs = sqs;
   }

   public String getWith() {
      return this.with;
   }

   public TableObject getViewName() {
      return this.viewName;
   }

   public ArrayList getColumnList() {
      return this.columnList;
   }

   public String getAs() {
      return this.as;
   }

   public SelectQueryStatement getSelectQueryStatement() {
      return this.sqs;
   }

   public CommonTableExpression toOracle(SwisSQLStatement fromSQL, SwisSQLStatement toSQL) throws ConvertException {
      CommonTableExpression commonTableExpr = this.copyObjectValues();
      TableObject srcViewName = commonTableExpr.getViewName();
      srcViewName.toOracle();
      ArrayList columnList = commonTableExpr.getColumnList();
      if (!columnList.isEmpty()) {
         for(int i = 0; i < columnList.size(); ++i) {
            String columnName = (String)columnList.get(i);
            if (!columnName.trim().equals(",") && !columnName.trim().equals("(") && !columnName.trim().equals(")")) {
               columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
            }

            if (columnName.startsWith("[") && columnName.endsWith("]") || columnName.startsWith("`") && columnName.endsWith("`")) {
               columnName = columnName.substring(1, columnName.length() - 1);
            }

            if (!columnName.startsWith("\"") && (SwisSQLOptions.retainQuotedIdentifierForOracle || columnName.indexOf(32) != -1)) {
               columnName = "\"" + columnName + "\"";
            }
         }
      }

      SelectQueryStatement srcSQS = commonTableExpr.getSelectQueryStatement();
      if (srcSQS != null) {
         commonTableExpr.setSelectQueryStatement(srcSQS.toOracle());
      }

      return commonTableExpr;
   }

   public CommonTableExpression toMSSQLServer(SwisSQLStatement fromSQL, SwisSQLStatement toSQL) throws ConvertException {
      return this;
   }

   public CommonTableExpression toSybase(SwisSQLStatement fromSQL, SwisSQLStatement toSQL) throws ConvertException {
      return this;
   }

   public CommonTableExpression toDB2(SwisSQLStatement fromSQL, SwisSQLStatement toSQL) throws ConvertException {
      return this;
   }

   public CommonTableExpression toMySQL() throws ConvertException {
      CommonTableExpression toCTE = new CommonTableExpression();
      if (GeneralUtil.isItEnclosedTblCol(this.viewName.toString())) {
         String viewName = this.viewName.toString();
         viewName = GeneralUtil.trimIfTblColIsEnclosed(viewName);
         this.viewName.setTableName("`" + viewName + "`");
         this.viewName.setOrigTableName("`" + viewName + "`");
      }

      toCTE.setViewName(this.getViewName());
      toCTE.setColumnList(this.setColumnListForCTE(this.columnList));
      toCTE.setAs(this.getAs());
      toCTE.setSelectQueryStatement(this.getSelectQueryStatement().toMySQL());
      return toCTE;
   }

   private ArrayList setColumnListForCTE(ArrayList columnList) {
      ArrayList columnListCopy = new ArrayList();

      for(int i = 0; i < columnList.size(); ++i) {
         String columnName = columnList.get(i).toString();
         if (columnName.startsWith("\"") && columnName.endsWith("\"")) {
            columnListCopy.add("`" + columnName.substring(1, columnName.length() - 1) + "`");
         } else {
            columnListCopy.add(columnName);
         }
      }

      return columnListCopy;
   }

   private ArrayList setColumnListForCTEInPostgres(ArrayList columnList) {
      ArrayList columnListCopy = new ArrayList();

      for(int i = 0; i < columnList.size(); ++i) {
         String columnName = columnList.get(i).toString();
         boolean isPGBuild = this.getSelectQueryStatement().getBooleanValues("is.pg.build");
         if (!columnName.equals("(") && !columnName.equals(",") && !columnName.equals(")")) {
            if (isPGBuild && !GeneralUtil.isItEnclosedTblCol(columnName)) {
               columnListCopy.add("\"" + GeneralUtil.trimIfTblColIsEnclosed(columnName) + "\"");
            } else {
               columnListCopy.add("\"" + GeneralUtil.trimIfTblColIsEnclosed(columnName).toLowerCase() + "\"");
            }
         } else {
            columnListCopy.add(columnName);
         }
      }

      return columnListCopy;
   }

   public CommonTableExpression toInformix(SwisSQLStatement fromSQL, SwisSQLStatement toSQL) throws ConvertException {
      return this;
   }

   public CommonTableExpression toPostgreSQL() throws ConvertException {
      CommonTableExpression toCTE = new CommonTableExpression();
      String viewName = this.viewName.toString();
      boolean isPGBuild = this.getSelectQueryStatement().getBooleanValues("is.pg.build");
      if (!isPGBuild || GeneralUtil.isItEnclosedTblCol(viewName)) {
         viewName = viewName.toLowerCase();
      }

      viewName = GeneralUtil.trimIfTblColIsEnclosed(viewName);
      this.viewName.setTableName("\"" + viewName + "\"");
      this.viewName.setOrigTableName("\"" + viewName + "\"");
      toCTE.setViewName(this.viewName);
      toCTE.setColumnList(this.setColumnListForCTEInPostgres(this.columnList));
      toCTE.setAs(this.getAs());
      toCTE.setSelectQueryStatement(this.getSelectQueryStatement().toPostgreSQL());
      return toCTE;
   }

   public CommonTableExpression toANSISQL(SwisSQLStatement fromSQL, SwisSQLStatement toSQL) throws ConvertException {
      CommonTableExpression commonTableExpr = this.copyObjectValues();
      TableObject srcViewName = commonTableExpr.getViewName();
      srcViewName.toANSISQL();
      ArrayList columnList = commonTableExpr.getColumnList();
      if (!columnList.isEmpty()) {
         for(int i = 0; i < columnList.size(); ++i) {
            String columnName = (String)columnList.get(i);
            if (!columnName.trim().equals(",") && !columnName.trim().equals("(") && !columnName.trim().equals(")")) {
               columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords(8), (ModifiedObjectAttr)null, 8);
            }

            if (columnName.startsWith("[") && columnName.endsWith("]") || columnName.startsWith("`") && columnName.endsWith("`")) {
               columnName = columnName.substring(1, columnName.length() - 1);
            }

            if (!columnName.startsWith("\"") && columnName.indexOf(32) != -1) {
               columnName = "\"" + columnName + "\"";
            }
         }
      }

      SelectQueryStatement srcSQS = commonTableExpr.getSelectQueryStatement();
      if (srcSQS != null) {
         commonTableExpr.setSelectQueryStatement(srcSQS.toANSI());
      }

      return commonTableExpr;
   }

   public CommonTableExpression toTeradata(SwisSQLStatement fromSQL, SwisSQLStatement toSQL) throws ConvertException {
      CommonTableExpression commonTableExpr = this.copyObjectValues();
      TableObject srcViewName = commonTableExpr.getViewName();
      srcViewName.toTeradata();
      ArrayList columnList = commonTableExpr.getColumnList();
      if (!columnList.isEmpty()) {
         for(int i = 0; i < columnList.size(); ++i) {
            String columnName = (String)columnList.get(i);
            if (!columnName.trim().equals(",") && !columnName.trim().equals("(") && !columnName.trim().equals(")")) {
               columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords(12), (ModifiedObjectAttr)null, 12);
            }

            if (columnName.startsWith("[") && columnName.endsWith("]") || columnName.startsWith("`") && columnName.endsWith("`")) {
               columnName = columnName.substring(1, columnName.length() - 1);
            }

            if (!columnName.startsWith("\"") && columnName.indexOf(32) != -1) {
               columnName = "\"" + columnName + "\"";
            }
         }
      }

      SelectQueryStatement srcSQS = commonTableExpr.getSelectQueryStatement();
      if (srcSQS != null) {
         commonTableExpr.setSelectQueryStatement(srcSQS.toTeradata());
      }

      return commonTableExpr;
   }

   public CommonTableExpression toTimesTen(SwisSQLStatement fromSQL, SwisSQLStatement toSQL) throws ConvertException {
      return this;
   }

   public CommonTableExpression toNetezza(SwisSQLStatement fromSQL, SwisSQLStatement toSQL) throws ConvertException {
      CommonTableExpression commonTableExpr = this.copyObjectValues();
      TableObject srcViewName = commonTableExpr.getViewName();
      srcViewName.toNetezza();
      ArrayList columnList = commonTableExpr.getColumnList();
      if (!columnList.isEmpty()) {
         for(int i = 0; i < columnList.size(); ++i) {
            String columnName = (String)columnList.get(i);
            if (!columnName.trim().equals(",") && !columnName.trim().equals("(") && !columnName.trim().equals(")")) {
               columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11);
            }

            if (columnName.startsWith("[") && columnName.endsWith("]") || columnName.startsWith("`") && columnName.endsWith("`")) {
               columnName = columnName.substring(1, columnName.length() - 1);
            }

            if (!columnName.startsWith("\"") && columnName.indexOf(32) != -1) {
               columnName = "\"" + columnName + "\"";
            }
         }
      }

      SelectQueryStatement srcSQS = commonTableExpr.getSelectQueryStatement();
      if (srcSQS != null) {
         commonTableExpr.setSelectQueryStatement(srcSQS.toNetezza());
      }

      return commonTableExpr;
   }

   public CommonTableExpression toReplaceTblCol(SelectQueryStatement from_sqs) throws ConvertException {
      CommonTableExpression comTblExprConv = new CommonTableExpression();
      if (this.with != null) {
         comTblExprConv.setWith(this.with);
      }

      if (this.viewName != null) {
         comTblExprConv.setViewName(this.viewName);
      }

      if (this.columnList != null) {
         ArrayList colListConv = new ArrayList();
         int i = 0;

         for(int colListSize = this.columnList.size(); i < colListSize; ++i) {
            colListConv.add((String)this.columnList.get(i));
         }

         comTblExprConv.setColumnList(colListConv);
      }

      if (this.as != null) {
         comTblExprConv.setAs(this.as);
      }

      if (this.sqs != null) {
         this.sqs.setPropAndHandlerFromSQS(from_sqs);
         this.sqs.setIsCTESupported(true);
         comTblExprConv.setSelectQueryStatement(this.sqs.toReplaceTblCol());
      }

      return comTblExprConv;
   }

   private CommonTableExpression copyObjectValues() {
      CommonTableExpression commonTableExpr = new CommonTableExpression();
      commonTableExpr.setWith(this.getWith());
      commonTableExpr.setViewName(this.getViewName());
      commonTableExpr.setColumnList(this.setColumnListForCTE(this.columnList));
      commonTableExpr.setAs(this.getAs());
      commonTableExpr.setSelectQueryStatement(this.getSelectQueryStatement());
      return commonTableExpr;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.with != null) {
         sb.append("WITH ");
      }

      if (this.viewName != null) {
         sb.append(this.viewName);
      }

      if (!this.columnList.isEmpty()) {
         for(int i = 0; i < this.columnList.size(); ++i) {
            sb.append(this.columnList.get(i));
         }
      }

      if (this.as != null) {
         sb.append(" AS ");
      }

      if (this.sqs != null) {
         sb.append("(" + this.sqs.toString().trim() + ")");
      }

      return sb.toString();
   }

   public LinkedHashMap getWithColDets() throws ConvertException {
      Vector selectItemList = this.getSelectQueryStatement().getSelectStatement().getSelectItemList();
      LinkedHashMap<String, String> columnList = (LinkedHashMap)this.getWithColumnListNames();
      LinkedHashMap<String, String> WithColumnMap = new LinkedHashMap();
      if (columnList.size() == 0) {
         SelectQueryStatement sqs = this.getSelectQueryStatement().toReplaceTblCol();
         WithColumnMap.putAll(sqs.getSelColNameMap());
      } else {
         if (columnList.size() != selectItemList.size() || selectItemList.get(0).toString().equals("*")) {
            throw new ConvertException("INVALID NUMBER OF ARGUMENTS FOR WITH CLAUSE", "CTE_ALIAS_COLUMNS_NOT_EQUAL");
         }

         WithColumnMap.putAll(columnList);
         List keySet = new ArrayList(columnList.keySet());

         for(int i = 0; i < selectItemList.size(); ++i) {
            ((SelectColumn)selectItemList.get(i)).setAliasForExpression(keySet.get(i).toString());
         }
      }

      return WithColumnMap;
   }

   public HashMap<String, String> getWithColumnListNames() throws ConvertException {
      Vector selectItemList = this.getSelectQueryStatement().getSelectStatement().getSelectItemList();
      HashMap<String, String> withColumnList = new LinkedHashMap();

      for(int i = 0; i < this.getColumnList().size(); ++i) {
         if (this.getColumnList().get(i) != "(" && this.getColumnList().get(i) != ")" && this.getColumnList().get(i) != ",") {
            String columnName = this.getColumnList().get(i).toString();
            if ((!columnName.startsWith("\"") || !columnName.endsWith("\"")) && (!columnName.startsWith("`") || !columnName.endsWith("`"))) {
               withColumnList.put(columnName, columnName);
            } else {
               withColumnList.put(columnName.substring(1, columnName.length() - 1), columnName.substring(1, columnName.length() - 1));
            }
         }
      }

      if (withColumnList.size() > 0 && selectItemList.size() != withColumnList.size() && selectItemList.get(0).toString().equals("*")) {
         throw new ConvertException("INVALID NUMBER OF ARGUMENTS FOR WITH CLAUSE", "CTE_ALIAS_COLUMNS_NOT_EQUAL");
      } else {
         return withColumnList;
      }
   }
}
