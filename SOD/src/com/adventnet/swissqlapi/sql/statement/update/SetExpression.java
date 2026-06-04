package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.util.Vector;

public class SetExpression {
   private ArrayList columnList = new ArrayList();
   private String equalto = new String();
   private ArrayList expressionList = new ArrayList();
   private SelectQueryStatement subQuery;
   private UserObjectContext context = null;
   private int setExpressionId;

   public void setColumnList(ArrayList list) {
      this.columnList = list;
   }

   public ArrayList getColumnList() {
      return this.columnList;
   }

   public void setEqualTo(String s) {
      this.equalto = s;
   }

   public String getEqualTo() {
      return this.equalto;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   public void setSubQuery(SelectQueryStatement s) {
      this.subQuery = s;
   }

   public SelectQueryStatement getSubQuery() {
      return this.subQuery;
   }

   public void setExpressionList(ArrayList list) {
      this.expressionList = list;
   }

   public ArrayList getExpressionList() {
      return this.expressionList;
   }

   public void setSetExpressionId(int exprId) {
      this.setExpressionId = exprId;
   }

   public int getSetExpressionId() {
      return this.setExpressionId;
   }

   public void toMySQL() throws ConvertException {
      int i;
      int size;
      Object obj;
      if (this.expressionList == null && this.subQuery != null) {
         this.subQuery = this.subQuery.toMySQL();
      } else {
         i = 0;

         for(size = this.expressionList.size(); i < size; ++i) {
            obj = this.expressionList.get(i);
            if (obj instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)obj;
               sqs = sqs.toMySQL();
               this.expressionList.set(i, sqs);
            } else if (obj instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)obj;
               sc = sc.toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, sc);
            }
         }
      }

      if (this.columnList != null) {
         i = 0;

         for(size = this.columnList.size(); i < size; ++i) {
            obj = this.columnList.get(i);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               tc = tc.toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.columnList.set(i, tc);
            }
         }
      }

   }

   public void toOracle() throws ConvertException {
      int i;
      int size;
      Object obj;
      TableColumn tc;
      if (this.expressionList == null && this.subQuery != null) {
         this.subQuery = this.subQuery.toOracle();
      } else {
         i = 0;

         for(size = this.expressionList.size(); i < size; ++i) {
            obj = this.expressionList.get(i);
            if (obj instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)obj;
               sqs = sqs.toOracle();
               this.expressionList.set(i, sqs);
            } else if (obj instanceof TableColumn) {
               tc = (TableColumn)obj;
               tc = tc.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, tc);
            } else if (obj instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)obj;
               sc = sc.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, sc);
            }
         }
      }

      if (this.columnList != null) {
         i = 0;

         for(size = this.columnList.size(); i < size; ++i) {
            obj = this.columnList.get(i);
            if (obj instanceof TableColumn) {
               tc = (TableColumn)obj;
               tc = tc.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.columnList.set(i, tc);
            }
         }
      }

   }

   public void toMSSQLServer() throws ConvertException {
      int i;
      int size;
      Object obj;
      if (this.expressionList == null && this.subQuery != null) {
         boolean subqueryHasAggregateFunction = false;
         SelectStatement subSelectStatement = this.subQuery.getSelectStatement();
         Vector subSelectCol = subSelectStatement.getSelectItemList();
         if (subSelectCol != null) {
            for(int i = 0; i < subSelectCol.size(); ++i) {
               if (subSelectCol.get(i) instanceof SelectColumn && (((SelectColumn)subSelectCol.get(i)).isAggregateFunction() || this.selectColumnHasAggrFunction(((SelectColumn)subSelectCol.get(i)).getColumnExpression(), false))) {
                  subqueryHasAggregateFunction = true;
               }
            }
         }

         if (!subqueryHasAggregateFunction) {
            this.newConversionForRamco();
            return;
         }

         this.subQuery = this.subQuery.toMSSQLServer();
         ArrayList newExpressionList = new ArrayList();
         SelectStatement selectStatement = this.subQuery.getSelectStatement();
         Vector selectList = selectStatement.getSelectItemList();
         if (selectList != null) {
            new ArrayList();

            for(int i = 0; i < selectList.size(); ++i) {
               SelectColumn newSelectColumn = new SelectColumn();
               if (selectList.get(i) instanceof SelectColumn) {
                  SelectColumn oldSelectColumn = (SelectColumn)selectList.get(i);
                  Vector colExp = oldSelectColumn.getColumnExpression();
                  Vector newColExp = new Vector();
                  if (colExp != null) {
                     for(int j = 0; j < colExp.size(); ++j) {
                        newColExp.add(colExp.get(j).toString());
                     }
                  }

                  newSelectColumn.setColumnExpression(newColExp);
               }

               Vector selectItem = new Vector();
               selectItem.add(newSelectColumn);
               SelectQueryStatement eachColumnStmt = new SelectQueryStatement();
               SelectStatement newSelectStatement = new SelectStatement();
               newSelectStatement.setDistinctList(selectStatement.getDistinctList());
               newSelectStatement.setSelectClause(selectStatement.getSelectClause());
               newSelectStatement.setSelectQualifier(selectStatement.getSelectQualifier());
               newSelectStatement.setSelectRowCount(selectStatement.getSelectRowCount());
               newSelectStatement.setSelectRowSpecifier(selectStatement.getSelectRowSpecifier());
               newSelectStatement.setSelectSpecialQualifier(selectStatement.getSelectSpecialQualifier());
               newSelectStatement.setSelectItemList(selectItem);
               eachColumnStmt.setSelectStatement(newSelectStatement);
               eachColumnStmt.setFromClause(this.subQuery.getFromClause());
               eachColumnStmt.setWhereExpression(this.subQuery.getWhereExpression());
               eachColumnStmt.setGroupByStatement(this.subQuery.getGroupByStatement());
               eachColumnStmt.setForUpdateStatement(this.subQuery.getForUpdateStatement());
               eachColumnStmt.setIntoStatement(this.subQuery.getIntoStatement());
               eachColumnStmt.setSetOperatorClause(this.subQuery.getSetOperatorClause());
               eachColumnStmt.setHavingStatement(this.subQuery.getHavingStatement());
               if (selectList.size() > 1) {
                  newExpressionList.add("");
               }

               String queryString = eachColumnStmt.toMSSQLServerString();
               queryString = StringFunctions.replaceAll("\n\t\t\t\t", "\n", queryString);
               newExpressionList.add("\n\t\t\t\t(\n\t\t\t\t" + queryString + ")" + "\n\t");
            }
         }

         this.setSubQuery((SelectQueryStatement)null);
         this.setExpressionList(newExpressionList);
      } else {
         i = 0;

         for(size = this.expressionList.size(); i < size; ++i) {
            obj = this.expressionList.get(i);
            if (obj instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)obj;
               sqs = sqs.toMSSQLServer();
               this.expressionList.set(i, sqs);
            } else if (obj instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)obj;
               sc = sc.toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, sc);
            }
         }
      }

      if (this.columnList != null) {
         i = 0;

         for(size = this.columnList.size(); i < size; ++i) {
            obj = this.columnList.get(i);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               tc = tc.toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.columnList.set(i, tc);
            }
         }
      }

   }

   public void toSybase() throws ConvertException {
      int i;
      int size;
      Object obj;
      if (this.expressionList == null && this.subQuery != null) {
         boolean subqueryHasAggregateFunction = false;
         SelectStatement subSelectStatement = this.subQuery.getSelectStatement();
         Vector subSelectCol = subSelectStatement.getSelectItemList();
         if (subSelectCol != null) {
            for(int i = 0; i < subSelectCol.size(); ++i) {
               if (subSelectCol.get(i) instanceof SelectColumn && ((SelectColumn)subSelectCol.get(i)).isAggregateFunction()) {
                  subqueryHasAggregateFunction = true;
               }
            }
         }

         if (!subqueryHasAggregateFunction) {
            this.newConversionForRamco();
            return;
         }

         this.subQuery = this.subQuery.toSybase();
         ArrayList newExpressionList = new ArrayList();
         SelectStatement selectStatement = this.subQuery.getSelectStatement();
         Vector selectList = selectStatement.getSelectItemList();
         if (selectList != null) {
            new ArrayList();

            for(int i = 0; i < selectList.size(); ++i) {
               SelectColumn newSelectColumn = new SelectColumn();
               if (selectList.get(i) instanceof SelectColumn) {
                  SelectColumn oldSelectColumn = (SelectColumn)selectList.get(i);
                  Vector colExp = oldSelectColumn.getColumnExpression();
                  Vector newColExp = new Vector();
                  if (colExp != null) {
                     for(int j = 0; j < colExp.size(); ++j) {
                        newColExp.add(colExp.get(j).toString());
                     }
                  }

                  newSelectColumn.setColumnExpression(newColExp);
               }

               Vector selectItem = new Vector();
               selectItem.add(newSelectColumn);
               SelectQueryStatement eachColumnStmt = new SelectQueryStatement();
               SelectStatement newSelectStatement = new SelectStatement();
               newSelectStatement.setDistinctList(selectStatement.getDistinctList());
               newSelectStatement.setSelectClause(selectStatement.getSelectClause());
               newSelectStatement.setSelectQualifier(selectStatement.getSelectQualifier());
               newSelectStatement.setSelectRowCount(selectStatement.getSelectRowCount());
               newSelectStatement.setSelectRowSpecifier(selectStatement.getSelectRowSpecifier());
               newSelectStatement.setSelectSpecialQualifier(selectStatement.getSelectSpecialQualifier());
               newSelectStatement.setSelectItemList(selectItem);
               eachColumnStmt.setSelectStatement(newSelectStatement);
               eachColumnStmt.setFromClause(this.subQuery.getFromClause());
               eachColumnStmt.setWhereExpression(this.subQuery.getWhereExpression());
               eachColumnStmt.setGroupByStatement(this.subQuery.getGroupByStatement());
               eachColumnStmt.setForUpdateStatement(this.subQuery.getForUpdateStatement());
               eachColumnStmt.setIntoStatement(this.subQuery.getIntoStatement());
               eachColumnStmt.setSetOperatorClause(this.subQuery.getSetOperatorClause());
               eachColumnStmt.setHavingStatement(this.subQuery.getHavingStatement());
               if (selectList.size() > 1) {
                  newExpressionList.add("");
               }

               String queryString = eachColumnStmt.toSybaseString();
               queryString = StringFunctions.replaceAll("\n\t\t\t\t", "\n", queryString);
               newExpressionList.add("\n\t\t\t\t(\n\t\t\t\t" + queryString + ")" + "\n\t");
            }
         }

         this.setSubQuery((SelectQueryStatement)null);
         this.setExpressionList(newExpressionList);
      } else {
         i = 0;

         for(size = this.expressionList.size(); i < size; ++i) {
            obj = this.expressionList.get(i);
            if (obj instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)obj;
               sqs.setObjectContext(this.context);
               sqs = sqs.toSybase();
               this.expressionList.set(i, sqs);
            } else if (obj instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)obj;
               sc.setObjectContext(this.context);
               sc = sc.toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, sc);
            }
         }
      }

      if (this.columnList != null) {
         i = 0;

         for(size = this.columnList.size(); i < size; ++i) {
            obj = this.columnList.get(i);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               tc = tc.toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.columnList.set(i, tc);
            }
         }
      }

   }

   public void toBigQuery() throws ConvertException {
      int i;
      int size;
      Object obj;
      if (this.expressionList == null && this.subQuery != null) {
         this.subQuery = this.subQuery.toBigQuery();
      } else {
         i = 0;

         for(size = this.expressionList.size(); i < size; ++i) {
            obj = this.expressionList.get(i);
            if (obj instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)obj;
               sqs = sqs.toBigQuery();
               this.expressionList.set(i, sqs);
            } else if (obj instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)obj;
               sc = sc.toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, sc);
            }
         }
      }

      if (this.columnList != null) {
         i = 0;

         for(size = this.columnList.size(); i < size; ++i) {
            obj = this.columnList.get(i);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               tc = tc.toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.columnList.set(i, tc);
            }
         }
      }

   }

   public void toPostgreSQL() throws ConvertException {
      int i;
      int size;
      Object obj;
      if (this.expressionList == null && this.subQuery != null) {
         this.subQuery = this.subQuery.toPostgreSQL();
      } else {
         i = 0;

         for(size = this.expressionList.size(); i < size; ++i) {
            obj = this.expressionList.get(i);
            if (obj instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)obj;
               sqs = sqs.toPostgreSQL();
               this.expressionList.set(i, sqs);
            } else if (obj instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)obj;
               sc = sc.toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, sc);
            }
         }
      }

      if (this.columnList != null) {
         i = 0;

         for(size = this.columnList.size(); i < size; ++i) {
            obj = this.columnList.get(i);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               tc = tc.toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.columnList.set(i, tc);
            }
         }
      }

   }

   public void toDB2() throws ConvertException {
      int i;
      int size;
      Object obj;
      if (this.expressionList == null && this.subQuery != null) {
         this.subQuery = this.subQuery.toDB2();
      } else {
         i = 0;

         for(size = this.expressionList.size(); i < size; ++i) {
            obj = this.expressionList.get(i);
            if (obj instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)obj;
               sqs = sqs.toDB2();
               this.expressionList.set(i, sqs);
            } else if (obj instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)obj;
               sc.setSelectColFromUQSSetExpression(true);
               sc = sc.toDB2Select((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, sc);
            }
         }
      }

      if (this.columnList != null) {
         i = 0;

         for(size = this.columnList.size(); i < size; ++i) {
            obj = this.columnList.get(i);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               tc = tc.toDB2Select((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.columnList.set(i, tc);
            }
         }
      }

   }

   public void toSnowflake() throws ConvertException {
      int i;
      int size;
      Object obj;
      if (this.expressionList == null && this.subQuery != null) {
         this.subQuery = this.subQuery.toSnowflake();
      } else {
         i = 0;

         for(size = this.expressionList.size(); i < size; ++i) {
            obj = this.expressionList.get(i);
            if (obj instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)obj;
               sqs = sqs.toSnowflake();
               this.expressionList.set(i, sqs);
            } else if (obj instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)obj;
               sc = sc.toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, sc);
            }
         }
      }

      if (this.columnList != null) {
         i = 0;

         for(size = this.columnList.size(); i < size; ++i) {
            obj = this.columnList.get(i);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               tc = tc.toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.columnList.set(i, tc);
            }
         }
      }

   }

   public void toInformix() throws ConvertException {
      int i;
      int size;
      Object obj;
      if (this.expressionList == null && this.subQuery != null) {
         this.subQuery = this.subQuery.toInformix();
      } else {
         i = 0;

         for(size = this.expressionList.size(); i < size; ++i) {
            obj = this.expressionList.get(i);
            if (obj instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)obj;
               sqs = sqs.toInformix();
               this.expressionList.set(i, sqs);
            } else if (obj instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)obj;
               sc = sc.toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, sc);
            }
         }
      }

      if (this.columnList != null) {
         i = 0;

         for(size = this.columnList.size(); i < size; ++i) {
            obj = this.columnList.get(i);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               tc = tc.toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.columnList.set(i, tc);
            }
         }
      }

   }

   public void toANSISQL() throws ConvertException {
      int i;
      int size;
      Object obj;
      if (this.expressionList == null && this.subQuery != null) {
         this.subQuery = this.subQuery.toANSI();
      } else {
         i = 0;

         for(size = this.expressionList.size(); i < size; ++i) {
            obj = this.expressionList.get(i);
            if (obj instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)obj;
               sqs = sqs.toANSI();
               this.expressionList.set(i, sqs);
            } else if (obj instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)obj;
               sc = sc.toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, sc);
            }
         }
      }

      if (this.columnList != null) {
         i = 0;

         for(size = this.columnList.size(); i < size; ++i) {
            obj = this.columnList.get(i);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               tc = tc.toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.columnList.set(i, tc);
            }
         }
      }

   }

   public void toTeradata() throws ConvertException {
      int i;
      int size;
      Object obj;
      if (this.expressionList == null && this.subQuery != null) {
         this.subQuery = this.subQuery.toTeradata();
      } else {
         i = 0;

         for(size = this.expressionList.size(); i < size; ++i) {
            obj = this.expressionList.get(i);
            if (obj instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)obj;
               sqs = sqs.toTeradata();
               this.expressionList.set(i, sqs);
            } else if (obj instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)obj;
               sc = sc.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, sc);
            }
         }
      }

      if (this.columnList != null) {
         i = 0;

         for(size = this.columnList.size(); i < size; ++i) {
            obj = this.columnList.get(i);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               tc = tc.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.columnList.set(i, tc);
            }
         }
      }

   }

   public void toTimesTen() throws ConvertException {
      if (this.expressionList == null && this.subQuery != null) {
         this.subQuery = this.subQuery.toTimesTen();
      } else {
         int i = 0;

         int size;
         Object obj;
         TableColumn tc;
         for(size = this.expressionList.size(); i < size; ++i) {
            obj = this.expressionList.get(i);
            if (obj instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)obj;
               sqs = sqs.toTimesTen();
               this.expressionList.set(i, sqs);
            } else if (obj instanceof TableColumn) {
               tc = (TableColumn)obj;
               tc = tc.toTimesTenSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, tc);
            } else if (obj instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)obj;
               sc = sc.toTimesTenSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, sc);
            }
         }

         if (this.columnList != null) {
            i = 0;

            for(size = this.columnList.size(); i < size; ++i) {
               obj = this.columnList.get(i);
               if (obj instanceof TableColumn) {
                  tc = (TableColumn)obj;
                  tc = tc.toTimesTenSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
                  this.columnList.set(i, tc);
               }
            }
         }
      }

   }

   public void toNetezzaSQL() throws ConvertException {
      int i;
      int size;
      Object obj;
      if (this.expressionList == null && this.subQuery != null) {
         boolean subqueryHasAggregateFunction = false;
         SelectStatement subSelectStatement = this.subQuery.getSelectStatement();
         Vector subSelectCol = subSelectStatement.getSelectItemList();
         if (subSelectCol != null) {
            for(int i = 0; i < subSelectCol.size(); ++i) {
               if (subSelectCol.get(i) instanceof SelectColumn && (((SelectColumn)subSelectCol.get(i)).isAggregateFunction() || this.selectColumnHasAggrFunction(((SelectColumn)subSelectCol.get(i)).getColumnExpression(), false))) {
                  subqueryHasAggregateFunction = true;
               }
            }
         }

         if (!subqueryHasAggregateFunction) {
            this.newConversionForRamco();
            return;
         }

         this.subQuery = this.subQuery.toNetezza();
         ArrayList newExpressionList = new ArrayList();
         SelectStatement selectStatement = this.subQuery.getSelectStatement();
         Vector selectList = selectStatement.getSelectItemList();
         if (selectList != null) {
            new ArrayList();

            for(int i = 0; i < selectList.size(); ++i) {
               SelectColumn newSelectColumn = new SelectColumn();
               if (selectList.get(i) instanceof SelectColumn) {
                  SelectColumn oldSelectColumn = (SelectColumn)selectList.get(i);
                  Vector colExp = oldSelectColumn.getColumnExpression();
                  Vector newColExp = new Vector();
                  if (colExp != null) {
                     for(int j = 0; j < colExp.size(); ++j) {
                        newColExp.add(colExp.get(j).toString());
                     }
                  }

                  newSelectColumn.setColumnExpression(newColExp);
               }

               Vector selectItem = new Vector();
               selectItem.add(newSelectColumn);
               SelectQueryStatement eachColumnStmt = new SelectQueryStatement();
               SelectStatement newSelectStatement = new SelectStatement();
               newSelectStatement.setDistinctList(selectStatement.getDistinctList());
               newSelectStatement.setSelectClause(selectStatement.getSelectClause());
               newSelectStatement.setSelectQualifier(selectStatement.getSelectQualifier());
               newSelectStatement.setSelectRowCount(selectStatement.getSelectRowCount());
               newSelectStatement.setSelectRowSpecifier(selectStatement.getSelectRowSpecifier());
               newSelectStatement.setSelectSpecialQualifier(selectStatement.getSelectSpecialQualifier());
               newSelectStatement.setSelectItemList(selectItem);
               eachColumnStmt.setSelectStatement(newSelectStatement);
               eachColumnStmt.setFromClause(this.subQuery.getFromClause());
               eachColumnStmt.setWhereExpression(this.subQuery.getWhereExpression());
               eachColumnStmt.setGroupByStatement(this.subQuery.getGroupByStatement());
               eachColumnStmt.setForUpdateStatement(this.subQuery.getForUpdateStatement());
               eachColumnStmt.setIntoStatement(this.subQuery.getIntoStatement());
               eachColumnStmt.setSetOperatorClause(this.subQuery.getSetOperatorClause());
               eachColumnStmt.setHavingStatement(this.subQuery.getHavingStatement());
               if (selectList.size() > 1) {
                  newExpressionList.add("");
               }

               String queryString = eachColumnStmt.toNetezzaString();
               queryString = StringFunctions.replaceAll("\n\t\t\t\t", "\n", queryString);
               newExpressionList.add("\n\t\t\t\t(\n\t\t\t\t" + queryString + ")" + "\n\t");
            }
         }

         this.setSubQuery((SelectQueryStatement)null);
         this.setExpressionList(newExpressionList);
      } else {
         i = 0;

         for(size = this.expressionList.size(); i < size; ++i) {
            obj = this.expressionList.get(i);
            if (obj instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)obj;
               sqs = sqs.toNetezza();
               this.expressionList.set(i, sqs);
            } else if (obj instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)obj;
               sc = sc.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.expressionList.set(i, sc);
            }
         }
      }

      if (this.columnList != null) {
         i = 0;

         for(size = this.columnList.size(); i < size; ++i) {
            obj = this.columnList.get(i);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               tc = tc.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               this.columnList.set(i, tc);
            }
         }
      }

   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      int i = 0;

      int size;
      for(size = this.columnList.size(); i < size; ++i) {
         if (this.columnList.get(i) instanceof TableColumn) {
            ((TableColumn)this.columnList.get(i)).setObjectContext(this.context);
         }

         sb.append(this.columnList.get(i).toString());
      }

      sb.append(" " + this.equalto + " ");
      if (this.expressionList == null) {
         for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
            sb.append("\t");
         }

         sb.append("(");
         sb.append(this.subQuery.toString());
         sb.append(")");
      } else {
         i = 0;

         for(size = this.expressionList.size(); i < size; ++i) {
            sb.append("\n");

            for(int j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }

            sb.append(this.expressionList.get(i).toString());
         }
      }

      return sb.toString();
   }

   public void newConversionForRamco() throws ConvertException {
      ArrayList newExpressionList = new ArrayList();
      this.subQuery = this.subQuery.toMSSQLServer();
      SelectStatement selectStatement = this.subQuery.getSelectStatement();
      Vector selectList = selectStatement.getSelectItemList();
      FromClause fromClause = this.subQuery.getFromClause();
      Vector fromList = fromClause.getFromItemList();
      ArrayList commaRemovedColumnList = new ArrayList();

      int i;
      for(i = 0; i < this.columnList.size(); ++i) {
         if (!this.columnList.get(i).toString().trim().equals(",") && !this.columnList.get(i).toString().trim().equals("(") && !this.columnList.get(i).toString().trim().equals(")")) {
            commaRemovedColumnList.add(this.columnList.get(i));
         } else {
            this.columnList.remove(i);
            --i;
         }
      }

      if (selectList.size() != commaRemovedColumnList.size()) {
         String message = "ColumnList size does not match Select column size";
         throw new ConvertException(message);
      } else {
         for(i = 0; i < commaRemovedColumnList.size(); ++i) {
            newExpressionList.add(commaRemovedColumnList.get(i));
            newExpressionList.add(" = ");
            FromTable fromTableObject = (FromTable)fromList.get(0);
            String tableName = null;
            if (fromTableObject.getAliasName() != null) {
               tableName = fromTableObject.getAliasName();
            } else {
               tableName = fromTableObject.getTableName().toString();
            }

            Object selectObj = ((SelectColumn)selectList.get(i)).getColumnExpression().get(0);
            if (selectObj instanceof TableColumn) {
               TableColumn selectTableColumn = (TableColumn)selectObj;
               if (selectTableColumn.getTableName() == null && !selectTableColumn.getColumnName().equalsIgnoreCase("GETDATE()") && !selectTableColumn.getColumnName().equalsIgnoreCase("SYSTEM_USER") && !selectTableColumn.getColumnName().equalsIgnoreCase("CURRENT_TIMESTAMP") && !selectTableColumn.getColumnName().trim().startsWith("@")) {
                  selectTableColumn.setTableName(tableName);
               }

               newExpressionList.add(selectList.get(i));
            } else {
               newExpressionList.add(selectList.get(i));
            }
         }

         this.expressionList = newExpressionList;
         this.columnList = null;
      }
   }

   private boolean selectColumnHasAggrFunction(Vector colExp, boolean inputVal) {
      boolean bool = inputVal;
      if (colExp != null) {
         for(int i = 0; i < colExp.size(); ++i) {
            Vector selColExp;
            if (colExp.get(i) instanceof SelectColumn) {
               if (((SelectColumn)colExp.get(i)).isAggregateFunction()) {
                  return true;
               }

               selColExp = ((SelectColumn)colExp.get(i)).getColumnExpression();
               bool = this.selectColumnHasAggrFunction(selColExp, bool);
            } else if (colExp.get(i) instanceof FunctionCalls) {
               selColExp = ((FunctionCalls)colExp.get(i)).getFunctionArguments();
               bool = this.selectColumnHasAggrFunction(selColExp, bool);
            }
         }
      }

      return bool;
   }
}
