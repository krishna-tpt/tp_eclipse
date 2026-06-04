package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

public class SetClause {
   private String set;
   private ArrayList setExpressionList;
   private ArrayList expressionList = new ArrayList();
   private Hashtable originalTableNameList;
   private UpdateQueryStatement fromUQS;
   private UserObjectContext context = null;
   private CommentClass commentObj;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setSet(String s) {
      this.set = s;
   }

   public String getSet() {
      return this.set;
   }

   public void setExpression(ArrayList list) {
      this.expressionList = list;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public ArrayList getExpression() {
      return this.expressionList;
   }

   public ArrayList getSetExpressionList() {
      return this.setExpressionList;
   }

   public void setOriginalTableName(Hashtable tableList) {
      this.originalTableNameList = tableList;
   }

   public Hashtable getOriginalTableName() {
      return this.originalTableNameList;
   }

   public void setSetExpressionList(ArrayList list) {
      this.setExpressionList = list;
   }

   public void setFromUpdateQuerySatetemnt(UpdateQueryStatement fromUQS) {
      this.fromUQS = fromUQS;
   }

   public void toMySQL() throws ConvertException {
      this.setCommentClass((CommentClass)null);
      String updateTableName = null;
      if (this.fromUQS != null && this.fromUQS.getTableExpression() != null && this.fromUQS.getTableExpression().getTableClauseList() != null) {
         ArrayList tabList = this.fromUQS.getTableExpression().getTableClauseList();
         if (tabList != null && tabList.size() == 1 && tabList.get(0) instanceof TableClause) {
            TableClause tc = (TableClause)tabList.get(0);
            TableObject tb = tc.getTableObject();
            updateTableName = tb.getOrigTableName();
         }
      }

      int i;
      if (this.expressionList != null && this.expressionList.size() != 0) {
         for(i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toMySQL());
            } else if (this.expressionList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.expressionList.get(i);
               if (updateTableName != null) {
                  Vector colExp = sc.getColumnExpression();
                  if (colExp != null) {
                     for(int k = 0; k < colExp.size(); ++k) {
                        if (colExp.get(k) instanceof TableColumn) {
                           TableColumn tc = (TableColumn)colExp.get(k);
                           tc.setOrigTableName(updateTableName);
                        }
                     }
                  }
               }

               this.expressionList.set(i, sc.toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }

            if (this.expressionList.get(i) instanceof TableColumn) {
               TableColumn tcToBeChanged = (TableColumn)this.expressionList.get(i);
               String checkForAliasName = tcToBeChanged.getTableName() + ".";
               if (this.originalTableNameList != null) {
                  if (this.originalTableNameList.containsKey(checkForAliasName)) {
                     TableColumn tc = (TableColumn)this.originalTableNameList.get(checkForAliasName);
                     tcToBeChanged.setTableName(tc.getTableName());
                     tcToBeChanged.setOwnerName(tc.getOwnerName());
                     this.expressionList.set(i, tcToBeChanged.toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  } else {
                     this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  }
               } else {
                  this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
               }
            }
         }
      } else {
         i = this.setExpressionList.size();

         for(int i = 0; i < i; i += 2) {
            SetExpression setExpression = (SetExpression)this.setExpressionList.get(i);
            setExpression.toMySQL();
         }

         this.convertSetExpressionListToExpressionList(this.setExpressionList, 5);
      }

   }

   public void toOracle() throws ConvertException {
      if (this.expressionList != null && this.expressionList.size() != 0) {
         boolean datetime = false;

         for(int i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toOracle());
            } else if (this.expressionList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.expressionList.get(i);
               sc.setFromUQS(this.fromUQS);
               boolean added = false;
               Vector colExpr = sc.getColumnExpression();
               if (colExpr.size() == 1 && colExpr.get(0) instanceof String && colExpr.get(0).toString().trim().startsWith("'")) {
                  String value = colExpr.get(0).toString().trim();
                  if (datetime) {
                     String format = SwisSQLUtils.getDateFormat(value, 1);
                     if (format != null) {
                        FunctionCalls fc = new FunctionCalls();
                        TableColumn tc = new TableColumn();
                        tc.setColumnName("TO_DATE");
                        Vector fnArgs = new Vector();
                        if (format.startsWith("'1900")) {
                           fnArgs.add(format);
                           fnArgs.add("'YYYY-MM-DD HH24:MI:SS'");
                        } else {
                           fnArgs.add(value);
                           fnArgs.add(format);
                        }

                        fc.setFunctionName(tc);
                        fc.setFunctionArguments(fnArgs);
                        colExpr.setElementAt(fc, 0);
                        this.expressionList.set(i, sc);
                        added = true;
                     }
                  }
               }

               if (!added) {
                  this.expressionList.set(i, ((SelectColumn)this.expressionList.get(i)).toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
               }
            }

            if (this.expressionList.get(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.expressionList.get(i);
               tc.setFromUQS(this.fromUQS);
               String datatype = MetadataInfoUtil.getDatatypeName(this.fromUQS, tc);
               datetime = false;
               if (datatype != null && datatype.toLowerCase().endsWith("datetime")) {
                  datetime = true;
               }

               this.expressionList.set(i, tc.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }

            if (this.expressionList.get(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.expressionList.get(i);
               fc.getFunctionArguments().add(this.expressionList.get(i - 2));
               this.expressionList.set(i - 1, " = ");
               this.expressionList.set(i, ((FunctionCalls)this.expressionList.get(i)).toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }
         }
      } else {
         int size = this.setExpressionList.size();

         for(int i = 0; i < size; i += 2) {
            if (this.setExpressionList.get(i) instanceof SetExpression) {
               SetExpression setExpression = (SetExpression)this.setExpressionList.get(i);
               setExpression.toOracle();
            }
         }

         this.convertSetExpressionListToExpressionList(this.setExpressionList, 1);
      }

   }

   public void toMSSQLServer() throws ConvertException {
      this.setCommentClass((CommentClass)null);
      boolean sqsFound = false;
      int count;
      int i;
      if (this.expressionList != null && this.expressionList.size() != 0) {
         count = 0;
         int tableColumnCount = 0;

         for(i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               ++count;
            } else if (this.expressionList.get(i) instanceof TableColumn) {
               ++tableColumnCount;
            }
         }

         if (count == 1) {
            sqsFound = true;
         }

         for(i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectColumn) {
               this.expressionList.set(i, ((SelectColumn)this.expressionList.get(i)).toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            } else if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               boolean subqueryHasAggregateFunction = false;
               SelectStatement subSelectStatement = ((SelectQueryStatement)this.expressionList.get(i)).getSelectStatement();
               Vector subSelectCol = subSelectStatement.getSelectItemList();
               if (subSelectCol != null) {
                  for(int j = 0; j < subSelectCol.size(); ++j) {
                     if (subSelectCol.get(j) instanceof SelectColumn && (((SelectColumn)subSelectCol.get(j)).isAggregateFunction() || this.selectColumnHasAggrFunction(((SelectColumn)subSelectCol.get(j)).getColumnExpression(), false))) {
                        subqueryHasAggregateFunction = true;
                     }
                  }
               }

               if (count == 1 && !subqueryHasAggregateFunction && tableColumnCount <= 1) {
                  if (i < 3) {
                     this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toMSSQLServer());
                  } else {
                     SetExpression setExp = new SetExpression();
                     ArrayList columnList = new ArrayList();
                     columnList.add("(");
                     columnList.add(this.expressionList.get(i - 3));
                     columnList.add(")");
                     setExp.setColumnList(columnList);
                     setExp.setEqualTo("=");
                     setExp.setSubQuery((SelectQueryStatement)this.expressionList.get(i));
                     setExp.setExpressionList((ArrayList)null);
                     ArrayList setExpressionArrayList = new ArrayList();
                     setExpressionArrayList.add(setExp);
                     this.setSetExpressionList(setExpressionArrayList);
                     setExp.toMSSQLServer();

                     for(int j = i + 1; j > i - 3; --j) {
                        this.expressionList.remove(j);
                     }

                     this.expressionList = setExp.getExpressionList();
                     i -= 3;
                  }
               } else {
                  this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toMSSQLServer());
               }
            }

            if (this.expressionList.get(i) instanceof TableColumn) {
               TableColumn tcToBeChanged = (TableColumn)this.expressionList.get(i);
               String checkForAliasName = tcToBeChanged.getTableName() + ".";
               if (this.originalTableNameList != null && !sqsFound) {
                  if (this.originalTableNameList.containsKey(checkForAliasName)) {
                     TableColumn tc = (TableColumn)this.originalTableNameList.get(checkForAliasName);
                     tcToBeChanged.setTableName(tc.getTableName());
                     tcToBeChanged.setOwnerName(tc.getOwnerName());
                     this.expressionList.set(i, tcToBeChanged.toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  } else {
                     this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  }
               } else {
                  this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
               }
            }
         }
      } else {
         count = this.setExpressionList.size();

         for(i = 0; i < count; i += 2) {
            SetExpression setExpression = (SetExpression)this.setExpressionList.get(i);
            setExpression.toMSSQLServer();
         }

         this.convertSetExpressionListToExpressionList(this.setExpressionList, 2);
      }

   }

   public void toSybase() throws ConvertException {
      this.setCommentClass((CommentClass)null);
      boolean sqsFound = false;
      int count;
      if (this.expressionList != null && this.expressionList.size() != 0) {
         count = 0;

         int i;
         for(i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               ++count;
            }
         }

         if (count == 1) {
            sqsFound = true;
         }

         for(i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectColumn) {
               ((SelectColumn)this.expressionList.get(i)).setObjectContext(this.context);
               this.expressionList.set(i, ((SelectColumn)this.expressionList.get(i)).toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            } else if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               boolean subqueryHasAggregateFunction = false;
               SelectStatement subSelectStatement = ((SelectQueryStatement)this.expressionList.get(i)).getSelectStatement();
               Vector subSelectCol = subSelectStatement.getSelectItemList();
               if (subSelectCol != null) {
                  for(int j = 0; j < subSelectCol.size(); ++j) {
                     if (subSelectCol.get(j) instanceof SelectColumn && ((SelectColumn)subSelectCol.get(j)).isAggregateFunction()) {
                        subqueryHasAggregateFunction = true;
                     }
                  }
               }

               if (count == 1 && !subqueryHasAggregateFunction) {
                  if (i < 3) {
                     this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toSybase());
                  } else {
                     SetExpression setExp = new SetExpression();
                     ArrayList columnList = new ArrayList();
                     columnList.add("(");
                     columnList.add(this.expressionList.get(i - 3));
                     columnList.add(")");
                     setExp.setColumnList(columnList);
                     setExp.setEqualTo("=");
                     setExp.setSubQuery((SelectQueryStatement)this.expressionList.get(i));
                     setExp.setExpressionList((ArrayList)null);
                     ArrayList setExpressionArrayList = new ArrayList();
                     setExpressionArrayList.add(setExp);
                     this.setSetExpressionList(setExpressionArrayList);
                     setExp.toSybase();

                     for(int j = i + 1; j > i - 3; --j) {
                        this.expressionList.remove(j);
                     }

                     this.expressionList = setExp.getExpressionList();
                     i -= 3;
                  }
               } else {
                  this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toSybase());
               }
            }

            if (this.expressionList.get(i) instanceof TableColumn) {
               TableColumn tcToBeChanged = (TableColumn)this.expressionList.get(i);
               String checkForAliasName = tcToBeChanged.getTableName() + ".";
               if (this.originalTableNameList != null && !sqsFound) {
                  if (this.originalTableNameList.containsKey(checkForAliasName)) {
                     TableColumn tc = (TableColumn)this.originalTableNameList.get(checkForAliasName);
                     tcToBeChanged.setTableName(tc.getTableName());
                     tcToBeChanged.setOwnerName(tc.getOwnerName());
                     this.expressionList.set(i, tcToBeChanged.toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  } else {
                     this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  }
               } else {
                  this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
               }
            }
         }
      } else {
         count = this.setExpressionList.size();

         for(int i = 0; i < count; i += 2) {
            SetExpression setExpression = (SetExpression)this.setExpressionList.get(i);
            setExpression.toSybase();
         }

         this.convertSetExpressionListToExpressionList(this.setExpressionList, 7);
      }

   }

   public void toBigQuery() throws ConvertException {
      this.setCommentClass((CommentClass)null);
      int i;
      if (this.expressionList != null && this.expressionList.size() != 0) {
         for(i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toBigQuery());
            } else if (this.expressionList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.expressionList.get(i);
               if (this.originalTableNameList != null) {
                  sc.setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
               }

               this.expressionList.set(i, sc.toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }

            if (this.expressionList.get(i) instanceof TableColumn) {
               TableColumn tcToBeChanged = (TableColumn)this.expressionList.get(i);
               String checkForAliasName = tcToBeChanged.getTableName() + ".";
               if (this.originalTableNameList != null) {
                  if (this.originalTableNameList.containsKey(checkForAliasName)) {
                     TableColumn tc = (TableColumn)this.originalTableNameList.get(checkForAliasName);
                     tcToBeChanged.setTableName(tc.getTableName());
                     this.expressionList.set(i, tcToBeChanged.toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  } else {
                     this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  }
               } else {
                  this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null));
               }
            }
         }
      } else {
         i = this.setExpressionList.size();

         for(int i = 0; i < i; i += 2) {
            SetExpression setExpression = (SetExpression)this.setExpressionList.get(i);
            setExpression.toBigQuery();
         }

         this.convertSetExpressionListToExpressionList(this.setExpressionList, 14);
      }

   }

   public void toPostgreSQL() throws ConvertException {
      this.setCommentClass((CommentClass)null);
      int i;
      if (this.expressionList != null && this.expressionList.size() != 0) {
         for(i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toPostgreSQL());
            } else if (this.expressionList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.expressionList.get(i);
               if (this.originalTableNameList != null) {
                  sc.setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
               }

               this.expressionList.set(i, sc.toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }

            if (this.expressionList.get(i) instanceof TableColumn) {
               TableColumn tcToBeChanged = (TableColumn)this.expressionList.get(i);
               String checkForAliasName = tcToBeChanged.getTableName() + ".";
               if (this.originalTableNameList != null) {
                  if (this.originalTableNameList.containsKey(checkForAliasName)) {
                     TableColumn tc = (TableColumn)this.originalTableNameList.get(checkForAliasName);
                     tcToBeChanged.setTableName(tc.getTableName());
                     this.expressionList.set(i, tcToBeChanged.toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  } else {
                     this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  }
               } else {
                  this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
               }
            }
         }
      } else {
         i = this.setExpressionList.size();

         for(int i = 0; i < i; i += 2) {
            SetExpression setExpression = (SetExpression)this.setExpressionList.get(i);
            setExpression.toPostgreSQL();
         }

         this.convertSetExpressionListToExpressionList(this.setExpressionList, 4);
      }

   }

   public void toDB2() throws ConvertException {
      this.setCommentClass((CommentClass)null);
      int i;
      if (this.expressionList != null && this.expressionList.size() != 0) {
         for(i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toDB2());
            } else if (this.expressionList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.expressionList.get(i);
               Vector colExpr = sc.getColumnExpression();
               boolean added = false;
               Object obj;
               if (colExpr != null && colExpr.size() == 1) {
                  obj = colExpr.get(0);
                  if (obj instanceof String) {
                     String str = (String)obj;
                     if (str.equalsIgnoreCase("NULL")) {
                        added = true;
                        this.expressionList.set(i, sc);
                     }
                  }
               }

               if (!added) {
                  if (i - 2 >= 0) {
                     obj = this.expressionList.get(i - 2);
                     if (obj instanceof TableColumn) {
                        TableColumn tableColumn = (TableColumn)obj;
                        FromTable fromTable = MetadataInfoUtil.getTableOfColumn(this.fromUQS, (TableColumn)tableColumn);
                        if (fromTable != null) {
                           if (fromTable.getTableName() instanceof String) {
                              tableColumn.setTableName((String)fromTable.getTableName());
                           }

                           sc.setCorrespondingTableColumn(tableColumn);
                        }
                     }
                  }

                  this.expressionList.set(i, sc.toDB2Select((SelectQueryStatement)null, (SelectQueryStatement)null));
               }
            }

            if (this.expressionList.get(i) instanceof TableColumn) {
               this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toDB2Select((SelectQueryStatement)null, (SelectQueryStatement)null));
               String dataType = MetadataInfoUtil.getDatatypeName(this.fromUQS, (TableColumn)this.expressionList.get(i));
               if (dataType != null && (dataType.toLowerCase().trim().indexOf("int") != -1 || dataType.toLowerCase().trim().indexOf("num") != -1) && i + 2 < this.expressionList.size()) {
                  Object obj = this.expressionList.get(i + 2);
                  if (obj instanceof SelectColumn) {
                     SelectColumn sc = (SelectColumn)obj;
                     Vector colExpr = sc.getColumnExpression();
                     if (colExpr != null && colExpr.size() == 1) {
                        Object scObj = colExpr.get(0);
                        if (scObj instanceof String) {
                           String str = (String)scObj;
                           if (str.startsWith("'")) {
                              colExpr.setElementAt(str.substring(1, str.length() - 1), 0);
                           }
                        }
                     }
                  }
               }
            }
         }
      } else {
         i = this.setExpressionList.size();

         for(int i = 0; i < i; i += 2) {
            SetExpression setExpression = (SetExpression)this.setExpressionList.get(i);
            setExpression.toDB2();
         }
      }

   }

   public void toSnowflake() throws ConvertException {
      this.setCommentClass((CommentClass)null);
      int i;
      if (this.expressionList != null && this.expressionList.size() != 0) {
         for(i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toSnowflake());
            } else if (this.expressionList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.expressionList.get(i);
               if (this.originalTableNameList != null) {
                  sc.setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
               }

               this.expressionList.set(i, sc.toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }

            if (this.expressionList.get(i) instanceof TableColumn) {
               TableColumn tcToBeChanged = (TableColumn)this.expressionList.get(i);
               String checkForAliasName = tcToBeChanged.getTableName() + ".";
               if (this.originalTableNameList != null) {
                  if (this.originalTableNameList.containsKey(checkForAliasName)) {
                     TableColumn tc = (TableColumn)this.originalTableNameList.get(checkForAliasName);
                     tcToBeChanged.setTableName(tc.getTableName());
                     this.expressionList.set(i, tcToBeChanged.toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  } else {
                     this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  }
               } else {
                  this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
               }
            }
         }
      } else {
         i = this.setExpressionList.size();

         for(int i = 0; i < i; i += 2) {
            SetExpression setExpression = (SetExpression)this.setExpressionList.get(i);
            setExpression.toSnowflake();
         }

         this.convertSetExpressionListToExpressionList(this.setExpressionList, 15);
      }

   }

   public void toInformix() throws ConvertException {
      this.setCommentClass((CommentClass)null);
      int i;
      if (this.expressionList != null && this.expressionList.size() != 0) {
         for(i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toInformix());
            } else if (this.expressionList.get(i) instanceof SelectColumn) {
               this.expressionList.set(i, ((SelectColumn)this.expressionList.get(i)).toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }

            if (this.expressionList.get(i) instanceof TableColumn) {
               TableColumn tcToBeChanged = (TableColumn)this.expressionList.get(i);
               String checkForAliasName = tcToBeChanged.getTableName() + ".";
               if (this.originalTableNameList != null) {
                  if (this.originalTableNameList.containsKey(checkForAliasName)) {
                     TableColumn tc = (TableColumn)this.originalTableNameList.get(checkForAliasName);
                     tcToBeChanged.setTableName(tc.getTableName());
                     tcToBeChanged.setOwnerName(tc.getOwnerName());
                     this.expressionList.set(i, tcToBeChanged.toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  } else {
                     this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  }
               } else {
                  this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
               }
            }
         }
      } else {
         i = this.setExpressionList.size();

         for(int i = 0; i < i; i += 2) {
            SetExpression setExpression = (SetExpression)this.setExpressionList.get(i);
            setExpression.toInformix();
         }
      }

   }

   public void toANSISQL() throws ConvertException {
      this.setCommentClass((CommentClass)null);
      int i;
      if (this.expressionList != null && this.expressionList.size() != 0) {
         for(i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toANSI());
            } else if (this.expressionList.get(i) instanceof SelectColumn) {
               this.expressionList.set(i, ((SelectColumn)this.expressionList.get(i)).toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }

            if (this.expressionList.get(i) instanceof TableColumn) {
               TableColumn tcToBeChanged = (TableColumn)this.expressionList.get(i);
               String checkForAliasName = tcToBeChanged.getTableName() + ".";
               if (this.originalTableNameList != null) {
                  if (this.originalTableNameList.containsKey(checkForAliasName)) {
                     TableColumn tc = (TableColumn)this.originalTableNameList.get(checkForAliasName);
                     tcToBeChanged.setTableName(tc.getTableName());
                     tcToBeChanged.setOwnerName(tc.getOwnerName());
                     this.expressionList.set(i, tcToBeChanged.toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  } else {
                     this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  }
               } else {
                  this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null));
               }
            }
         }
      } else {
         i = this.setExpressionList.size();

         for(int i = 0; i < i; i += 2) {
            SetExpression setExpression = (SetExpression)this.setExpressionList.get(i);
            setExpression.toANSISQL();
         }

         this.convertSetExpressionListToExpressionList(this.setExpressionList, 8);
      }

   }

   public void toTeradata() throws ConvertException {
      this.setCommentClass((CommentClass)null);
      int i;
      if (this.expressionList != null && this.expressionList.size() != 0) {
         for(i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toTeradata());
            } else if (this.expressionList.get(i) instanceof SelectColumn) {
               this.expressionList.set(i, ((SelectColumn)this.expressionList.get(i)).toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }

            if (this.expressionList.get(i) instanceof TableColumn) {
               TableColumn tcToBeChanged = (TableColumn)this.expressionList.get(i);
               String checkForAliasName = tcToBeChanged.getTableName() + ".";
               TableColumn tc;
               if (this.originalTableNameList != null) {
                  if (this.originalTableNameList.containsKey(checkForAliasName)) {
                     tc = (TableColumn)this.originalTableNameList.get(checkForAliasName);
                     tcToBeChanged.setTableName(tc.getTableName());
                     tcToBeChanged.setOwnerName(tc.getOwnerName());
                     this.expressionList.set(i, tcToBeChanged.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  } else {
                     this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  }
               } else {
                  tc = ((TableColumn)this.expressionList.get(i)).toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
                  if (tc.getTableName() != null) {
                     tc.setTableName((String)null);
                  }

                  this.expressionList.set(i, tc);
               }
            }
         }
      } else {
         i = this.setExpressionList.size();

         for(int i = 0; i < i; i += 2) {
            SetExpression setExpression = (SetExpression)this.setExpressionList.get(i);
            setExpression.toTeradata();
         }

         this.convertSetExpressionListToExpressionList(this.setExpressionList, 12);
      }

   }

   public void toTimesTen() throws ConvertException {
      this.setCommentClass((CommentClass)null);
      if (this.expressionList != null && this.expressionList.size() != 0) {
         boolean datetime = false;
         boolean timestamp = false;
         boolean unicode = false;

         for(int i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               throw new ConvertException("\nSubqueries are not supported in UPDATE statement's SET Clause in TimesTen 5.1.21\n");
            }

            if (this.expressionList.get(i) instanceof SelectColumn) {
               if (this.expressionList.get(i).toString().equalsIgnoreCase("default")) {
                  throw new ConvertException("\nDEFAULT clause not supported in the UPDATE statemnet in TimesTen 5.1.21\n");
               }

               boolean added = false;
               SelectColumn sc = (SelectColumn)this.expressionList.get(i);
               sc.setFromUQS(this.fromUQS);
               Vector colExpr = sc.getColumnExpression();
               if (colExpr.size() == 1 && colExpr.get(0) instanceof String && colExpr.get(0).toString().trim().startsWith("'")) {
                  String value = colExpr.get(0).toString().trim();
                  if (datetime) {
                     String format = SwisSQLUtils.getDateFormat(value, 10);
                     if (format != null && (format.equals("YYYY-MM-DD") || format.equals("HH24:MI:SS"))) {
                        if (timestamp) {
                           if (format.equals("YYYY-MM-DD")) {
                              value = value.substring(0, value.length() - 1) + " 00:00:00'";
                           } else {
                              value = "'1900-01-01 " + value.substring(1);
                           }

                           colExpr.setElementAt(value, 0);
                           this.expressionList.set(i, sc);
                           added = true;
                        }

                        format = null;
                     }

                     if (format != null) {
                        if (format.startsWith("'1900")) {
                           colExpr.setElementAt(format, 0);
                           this.expressionList.set(i, sc);
                           added = true;
                        } else if (format.equals(value)) {
                           value = value.substring(1, value.length() - 1);
                           String time = "";
                           int index = false;
                           int index;
                           if ((index = value.indexOf(" ")) != -1) {
                              time = value.substring(index + 1);
                              value = value.substring(0, index);
                           }

                           int len = value.length();
                           if (len == 8) {
                              value = value.substring(0, 4) + "-" + value.substring(4, 6) + "-" + value.substring(6);
                           } else if (len == 6) {
                              String yearStr = value.substring(0, 2);
                              int year = Integer.parseInt(yearStr);
                              if (year < 50) {
                                 yearStr = "20" + yearStr;
                              } else {
                                 yearStr = "19" + yearStr;
                              }

                              value = yearStr + "-" + value.substring(2, 4) + "-" + value.substring(4);
                           }

                           if (timestamp && time == "") {
                              value = value + " 00:00:00";
                           } else if (time != "") {
                              value = value + " " + time;
                           }

                           colExpr.setElementAt(value, 0);
                           this.expressionList.set(i, sc);
                           added = true;
                        } else {
                           FunctionCalls fc = new FunctionCalls();
                           TableColumn tc = new TableColumn();
                           tc.setColumnName("TO_DATE");
                           Vector fnArgs = new Vector();
                           fnArgs.add(value);
                           fnArgs.add(format);
                           fc.setFunctionName(tc);
                           fc.setFunctionArguments(fnArgs);
                           colExpr.setElementAt(fc, 0);
                           this.expressionList.set(i, sc);
                           added = true;
                        }
                     }
                  } else if (unicode) {
                     colExpr.setElementAt("N" + value, 0);
                     this.expressionList.set(i, sc);
                     added = true;
                  }
               }

               if (!added) {
                  this.expressionList.set(i, ((SelectColumn)this.expressionList.get(i)).toTimesTenSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
               }
            }

            if (this.expressionList.get(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.expressionList.get(i);
               String datatype = MetadataInfoUtil.getDatatypeName(this.fromUQS, tc);
               datetime = false;
               timestamp = false;
               unicode = false;
               if (datatype != null) {
                  if (datatype.indexOf("date") == -1 && datatype.indexOf("time") == -1) {
                     if (datatype.indexOf("unichar") != -1 || datatype.indexOf("univarchar") != -1 || datatype.indexOf("nchar") != -1 || datatype.indexOf("nvarchar") != -1) {
                        unicode = true;
                     }
                  } else {
                     datetime = true;
                     if (datatype.toLowerCase().indexOf("datetime") != -1) {
                        timestamp = true;
                     }
                  }
               }

               this.expressionList.set(i, tc.toTimesTenSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }
         }
      } else {
         int size = this.setExpressionList.size();

         for(int i = 0; i < size; i += 2) {
            if (this.setExpressionList.get(i) instanceof SetExpression) {
               SetExpression setExpression = (SetExpression)this.setExpressionList.get(i);
               setExpression.toTimesTen();
            }
         }

         this.convertSetExpressionListToExpressionList(this.setExpressionList, 10);
      }

   }

   public void toNetezza() throws ConvertException {
      this.setCommentClass((CommentClass)null);
      boolean sqsFound = false;
      int count;
      int i;
      if (this.expressionList != null && this.expressionList.size() != 0) {
         count = 0;
         int tableColumnCount = 0;

         for(i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               ++count;
            } else if (this.expressionList.get(i) instanceof TableColumn) {
               ++tableColumnCount;
            }
         }

         if (count == 1) {
            sqsFound = true;
         }

         for(i = 0; i < this.expressionList.size(); ++i) {
            if (this.expressionList.get(i) instanceof SelectColumn) {
               this.expressionList.set(i, ((SelectColumn)this.expressionList.get(i)).toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            } else if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               boolean subqueryHasAggregateFunction = false;
               SelectStatement subSelectStatement = ((SelectQueryStatement)this.expressionList.get(i)).getSelectStatement();
               Vector subSelectCol = subSelectStatement.getSelectItemList();
               if (subSelectCol != null) {
                  for(int j = 0; j < subSelectCol.size(); ++j) {
                     if (subSelectCol.get(j) instanceof SelectColumn && (((SelectColumn)subSelectCol.get(j)).isAggregateFunction() || this.selectColumnHasAggrFunction(((SelectColumn)subSelectCol.get(j)).getColumnExpression(), false))) {
                        subqueryHasAggregateFunction = true;
                     }
                  }
               }

               if (count == 1 && !subqueryHasAggregateFunction && tableColumnCount <= 1) {
                  if (i < 3) {
                     this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toNetezza());
                  } else {
                     SetExpression setExp = new SetExpression();
                     ArrayList columnList = new ArrayList();
                     columnList.add("(");
                     columnList.add(this.expressionList.get(i - 3));
                     columnList.add(")");
                     setExp.setColumnList(columnList);
                     setExp.setEqualTo("=");
                     setExp.setSubQuery((SelectQueryStatement)this.expressionList.get(i));
                     setExp.setExpressionList((ArrayList)null);
                     ArrayList setExpressionArrayList = new ArrayList();
                     setExpressionArrayList.add(setExp);
                     this.setSetExpressionList(setExpressionArrayList);
                     setExp.toNetezzaSQL();

                     for(int j = i + 1; j > i - 3; --j) {
                        this.expressionList.remove(j);
                     }

                     this.expressionList = setExp.getExpressionList();
                     i -= 3;
                  }
               } else {
                  this.expressionList.set(i, ((SelectQueryStatement)this.expressionList.get(i)).toNetezza());
               }
            }

            if (this.expressionList.get(i) instanceof TableColumn) {
               TableColumn tcToBeChanged = (TableColumn)this.expressionList.get(i);
               String checkForAliasName = tcToBeChanged.getTableName() + ".";
               if (this.originalTableNameList != null && !sqsFound) {
                  if (this.originalTableNameList.containsKey(checkForAliasName)) {
                     TableColumn tc = (TableColumn)this.originalTableNameList.get(checkForAliasName);
                     tcToBeChanged.setTableName(tc.getTableName());
                     tcToBeChanged.setOwnerName(tc.getOwnerName());
                     this.expressionList.set(i, tcToBeChanged.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  } else {
                     this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  }
               } else {
                  this.expressionList.set(i, ((TableColumn)this.expressionList.get(i)).toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
               }
            }
         }
      } else {
         count = this.setExpressionList.size();

         for(i = 0; i < count; i += 2) {
            SetExpression setExpression = (SetExpression)this.setExpressionList.get(i);
            setExpression.toNetezzaSQL();
         }

         this.convertSetExpressionListToExpressionList(this.setExpressionList, 2);
      }

   }

   private void convertSetExpressionListToExpressionList(ArrayList setExpressionList, int database) throws ConvertException {
      int size = setExpressionList.size();

      for(int i = 0; i < size; ++i) {
         Object obj = setExpressionList.get(i);
         if (!(obj instanceof SetExpression)) {
            this.expressionList.add(obj.toString());
         } else {
            SetExpression setExpression = (SetExpression)setExpressionList.get(i);
            ArrayList columnList = setExpression.getColumnList();
            ArrayList valueList = setExpression.getExpressionList();
            if (database == 10 && columnList.size() > 1) {
               throw new ConvertException("\nSET with multiple LHS COLUMNs is not supported in UPDATE statement in TimesTen 5.1.21\n");
            }

            if (valueList == null) {
               return;
            }

            int size1;
            if (columnList == null && valueList != null) {
               if (database != 11) {
                  this.expressionList = valueList;
                  return;
               }

               int j1 = true;
               if (size == 1) {
                  this.expressionList = valueList;
                  return;
               }

               size1 = valueList.size();

               for(int vS = 0; vS < size1; ++vS) {
                  this.expressionList.add(valueList.get(vS));
               }

               if (i >= size - 1) {
                  return;
               }

               this.expressionList.add(", ");
            } else {
               int i1 = 1;
               size1 = columnList.size();
               if (size1 == 1) {
                  this.expressionList.add(columnList.get(0).toString());
                  this.expressionList.add(" = ");
                  if (valueList.size() == 1 || valueList.size() == 2) {
                     this.expressionList.add(valueList.get(0).toString());
                  }
               } else {
                  for(; i1 < size1; i1 += 2) {
                     this.expressionList.add(columnList.get(i1).toString());
                     this.expressionList.add(" = ");
                     if (valueList.size() != 1 && valueList.size() != 2) {
                        this.expressionList.add(valueList.get(i1).toString());
                     } else {
                        this.expressionList.add(valueList.get(0).toString());
                     }

                     if (i1 < size1 - 2) {
                        this.expressionList.add(", ");
                     }
                  }
               }
            }
         }
      }

   }

   public String toString() {
      StringBuffer stringbuffer = new StringBuffer();
      if (this.commentObj != null) {
         stringbuffer.append(this.commentObj.toString().trim() + " ");
      }

      stringbuffer.append(this.set.toUpperCase());
      stringbuffer.append("\t");
      int i;
      if (this.expressionList != null && this.expressionList.size() != 0) {
         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);
         i = 0;

         for(int size = this.expressionList.size(); i < size; ++i) {
            if (this.expressionList.get(i) instanceof SelectQueryStatement) {
               stringbuffer.append("\n");
               SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 3);
               ((SelectQueryStatement)this.expressionList.get(i)).setObjectContext(this.context);
               stringbuffer.append(this.expressionList.get(i));
               SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 3);
               stringbuffer.append("\t\t\t\t");
               ++i;
            } else if (this.expressionList.get(i) instanceof String && ((String)this.expressionList.get(i)).trim().startsWith("(SELECT ")) {
            }

            if (this.expressionList.get(i) instanceof TableColumn) {
               ((TableColumn)this.expressionList.get(i)).setObjectContext(this.context);
               stringbuffer.append(this.expressionList.get(i));
            } else if (this.expressionList.get(i) instanceof FunctionCalls) {
               ((FunctionCalls)this.expressionList.get(i)).setObjectContext(this.context);
               stringbuffer.append(this.expressionList.get(i));
            } else if (this.expressionList.get(i) instanceof SelectColumn) {
               ((SelectColumn)this.expressionList.get(i)).setObjectContext(this.context);
               stringbuffer.append(this.expressionList.get(i));
            } else if (this.context != null) {
               stringbuffer.append(this.context.getEquivalent(this.expressionList.get(i).toString()));
            } else {
               stringbuffer.append(this.expressionList.get(i));
            }

            if (this.expressionList.get(i) instanceof SelectColumn) {
               ((SelectColumn)this.expressionList.get(i)).setObjectContext(this.context);
               if (this.expressionList.get(i).toString().trim().endsWith(",")) {
                  stringbuffer.append("\n\t");
               }
            } else if (this.expressionList.get(i) instanceof String && ((String)this.expressionList.get(i)).endsWith(",")) {
               stringbuffer.append("\t");
            }
         }

         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
      } else {
         i = this.setExpressionList.size();

         for(int i = 0; i < i; ++i) {
            if (this.setExpressionList.get(i) instanceof TableColumn) {
               ((TableColumn)this.setExpressionList.get(i)).setObjectContext(this.context);
            }

            stringbuffer.append(this.setExpressionList.get(i).toString());
         }
      }

      return stringbuffer.toString();
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
