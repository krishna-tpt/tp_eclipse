package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.RownumClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

public class UpdateQueryStatement implements SwisSQLStatement {
   private UserObjectContext objectContext = null;
   private UpdateClause updateClause;
   private TableExpression tableExp;
   private HintClause hintClause;
   private SetClause setClause;
   private FromClause fromClause;
   private WhereCurrentClause whereCurrentClause;
   private WhereExpression whereExpression;
   private ReturningClause returningClause;
   private OrderByStatement orderByStatement;
   private UpdateLimitClause updateLimitClause;
   private OptionalHintClause optionalHintClause;
   private OracleSpecificClass OracleSpecificInstance;
   private CommentClass commentObject;
   private String isolationLevel = null;
   private String withString = null;
   private ArrayList lockTableList = new ArrayList();
   private int timesTenFirst = -1;
   private ArrayList messageArray = new ArrayList();
   private Hashtable withTableColumnandIndex = null;
   private boolean selectQueryForUpdateStmt = false;

   public int getTimesTenFirst() {
      return this.timesTenFirst;
   }

   public void setTimesTenFirst(int i) {
      this.timesTenFirst = i;
   }

   public void setCommentClass(CommentClass commentObject) {
      this.commentObject = commentObject;
   }

   public CommentClass getCommentClass() {
      return this.commentObject;
   }

   public UpdateQueryStatement(UpdateQueryStatement q) {
      this.updateClause = q.updateClause;
      this.tableExp = q.tableExp;
      this.hintClause = q.hintClause;
      this.setClause = q.setClause;
      this.fromClause = q.fromClause;
      this.whereExpression = q.whereExpression;
      this.returningClause = q.returningClause;
      this.orderByStatement = q.orderByStatement;
      this.updateLimitClause = q.updateLimitClause;
      this.optionalHintClause = q.optionalHintClause;
      this.OracleSpecificInstance = q.OracleSpecificInstance;
   }

   public UpdateQueryStatement() {
      this.updateClause = null;
      this.fromClause = null;
      this.setClause = null;
      this.tableExp = null;
      this.updateLimitClause = null;
      this.whereExpression = null;
      this.hintClause = null;
   }

   public void setUpdateClause(UpdateClause updateclause) {
      this.updateClause = updateclause;
   }

   public void setHintClause(HintClause hc) {
      this.hintClause = hc;
   }

   public void setWhereCurrentClause(WhereCurrentClause wcc) {
      this.whereCurrentClause = wcc;
   }

   public String getWithString() {
      return this.withString;
   }

   public OrderByStatement getOrderByStatement() {
      return this.orderByStatement;
   }

   public OptionalHintClause getOptionalHintClause() {
      return this.optionalHintClause;
   }

   public HintClause getHintClause() {
      return this.hintClause;
   }

   public void setWhereClause(WhereExpression we) {
      this.whereExpression = we;
   }

   public WhereExpression getWhereExpression() {
      return this.whereExpression;
   }

   public void setSetClause(SetClause setclause) {
      this.setClause = setclause;
   }

   public void setTableExpression(TableExpression tableexpression) {
      this.tableExp = tableexpression;
   }

   public TableExpression getTableExpression() {
      return this.tableExp;
   }

   public void setOrderByStatement(OrderByStatement obs) {
      this.orderByStatement = obs;
   }

   public void setReturningClause(ReturningClause rc) {
      this.returningClause = rc;
   }

   public void setWithString(String w) {
      this.withString = w;
   }

   public void setIsolationLevel(String il) {
      this.isolationLevel = il;
   }

   public String getIsolationLevel() {
      return this.isolationLevel;
   }

   public void addLockTableList(String lockTableStt) {
      this.lockTableList.add(lockTableStt);
   }

   public ReturningClause getReturningClause() {
      return this.returningClause;
   }

   public void setUpdateLimitClause(UpdateLimitClause limitclause) {
      this.updateLimitClause = limitclause;
   }

   public void setOracleSpecificInstance(OracleSpecificClass oraclespecificclass) {
      this.OracleSpecificInstance = oraclespecificclass;
   }

   public void setFromClause(FromClause fc) {
      this.fromClause = fc;
   }

   public void setOptionalHintClause(OptionalHintClause ohc) {
      this.optionalHintClause = ohc;
   }

   public UpdateLimitClause getUpdateLimitClause() {
      return this.updateLimitClause;
   }

   public FromClause getFromClause() {
      return this.fromClause;
   }

   public WhereCurrentClause getWhereCurrentClause() {
      return this.whereCurrentClause;
   }

   public SetClause getSetClause() {
      return this.setClause;
   }

   public UpdateClause getUpdateClause() {
      return this.updateClause;
   }

   public boolean columnsHaveAggregateFunction() {
      return true;
   }

   public String toString() {
      StringBuffer stringbuffer = new StringBuffer();
      int k;
      if (!SwisSQLUtils.swissqlMessageList.isEmpty()) {
         stringbuffer.append("/* SwisSQL Messages :\n");

         for(k = 0; k < SwisSQLUtils.swissqlMessageList.size(); ++k) {
            stringbuffer.append(SwisSQLUtils.swissqlMessageList.get(k).toString() + "\n");
         }

         stringbuffer.append("*/\n");
         SwisSQLUtils.swissqlMessageList.clear();
      }

      if (this.commentObject != null) {
         stringbuffer.append(this.commentObject.toString() + "\n");
      }

      for(k = 0; k < this.lockTableList.size(); ++k) {
         stringbuffer.append(this.lockTableList.get(k).toString() + ";\n");
      }

      if (this.singleQueryIntoMultipleQueriesForPLSQL() != null) {
         stringbuffer.append(this.singleQueryIntoMultipleQueriesForPLSQL());
         SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
      }

      if (this.updateClause != null) {
         stringbuffer.append(this.updateClause.toString() + " ");
      }

      if (this.getTimesTenFirst() != -1) {
         stringbuffer.append("FIRST " + (this.getTimesTenFirst() - 1) + " ");
      }

      if (this.tableExp != null) {
         if (this.objectContext != null) {
            this.tableExp.setObjectContext(this.objectContext);
         }

         stringbuffer.append(this.tableExp.toString() + " \n");
      }

      if (this.hintClause != null && this.hintClause.toString() != null) {
         stringbuffer.append(this.hintClause.toString() + " \n");
      }

      if (this.setClause != null) {
         if (this.objectContext != null) {
            this.setClause.setObjectContext(this.objectContext);
         }

         stringbuffer.append(this.setClause.toString() + " \n");
      }

      if (this.fromClause != null) {
         this.fromClause.setObjectContext(this.objectContext);
         stringbuffer.append(this.fromClause.toString() + " \n");
      }

      if (this.whereCurrentClause != null) {
         stringbuffer.append(this.whereCurrentClause.toString() + " \n");
      }

      if (this.whereExpression != null) {
         if (!this.whereExpression.toString().trim().equals("")) {
            if (this.whereExpression.getConcatenation() != null) {
               stringbuffer.append("+ ");
            }

            stringbuffer.append("WHERE ");
            this.whereExpression.setObjectContext(this.objectContext);
            if (this.whereExpression.toString().trim().indexOf("AND") == 0) {
               stringbuffer.append(StringFunctions.replaceFirst(" ", "AND", this.whereExpression.toString()));
            } else {
               stringbuffer.append(" " + this.whereExpression.toString());
            }
         }

         stringbuffer.append(" \n");
      }

      if (this.orderByStatement != null) {
         stringbuffer.append(this.orderByStatement.toString() + " \n");
      }

      if (this.updateLimitClause != null) {
         stringbuffer.append(this.updateLimitClause.toString() + " \n");
      }

      if (this.optionalHintClause != null) {
         stringbuffer.append(this.optionalHintClause.toString() + " \n");
      }

      if (this.returningClause != null) {
         stringbuffer.append(this.returningClause.toString());
      }

      if (this.withString != null) {
         stringbuffer.append(this.withString + " ");
      }

      if (this.isolationLevel != null) {
         stringbuffer.append(this.isolationLevel);
      }

      return stringbuffer.toString();
   }

   public String removeIndent(String s_ri) {
      s_ri = s_ri.replace('\n', ' ');
      s_ri = s_ri.replace('\t', ' ');
      return s_ri;
   }

   public String toInformixString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.updateClause.toInformix();
      if (this.setClause != null) {
         if (this.tableExp.getTableClauseList() != null) {
            ArrayList tableList = this.tableExp.getTableClauseList();

            for(int i = 0; i < tableList.size(); ++i) {
               Hashtable withTableColumnandIndex = new Hashtable();
               if (((TableClause)tableList.get(i)).getAlias() != null) {
                  TableColumn tc = new TableColumn();
                  TableObject setTableObject = ((TableClause)tableList.get(i)).getTableObject();
                  String setOriginalTableNameForAlias = setTableObject.getTableName();
                  tc.setTableName(setOriginalTableNameForAlias);
                  tc.setOwnerName(setTableObject.getUser());
                  String keyString = ((TableClause)tableList.get(i)).getAlias() + ".";
                  withTableColumnandIndex.put(keyString, tc);
                  this.setClause.setOriginalTableName(withTableColumnandIndex);
               }
            }
         }

         this.setClause.toInformix();
      }

      if (this.hintClause != null) {
         this.hintClause.toInformix();
      }

      if (this.hintClause != null) {
         this.hintClause = null;
      }

      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      if (this.fromClause != null) {
         this.fromClause.convertToSubQuery((UpdateQueryStatement)this, 6, this.getFromClause());
      }

      this.convertAliasNameToTableName();
      this.tableExp.toInformix();
      this.optionalHintClause = null;
      this.returningClause = null;
      this.updateLimitClause = null;
      return this.toString();
   }

   public String toOracleString() throws ConvertException {
      new StringBuffer();
      boolean convertToSubQueryNotReqd = false;
      this.updateClause.toOracle();
      this.tableExp.toOracle();
      if (this.setClause != null) {
         if (this.setClause.getExpression() != null) {
            ArrayList expList = this.setClause.getExpression();
            convertToSubQueryNotReqd = this.processSetClauseIntoASingleSubQuery(expList);
         }

         this.setClause.setFromUpdateQuerySatetemnt(this);
         this.setClause.toOracle();
      }

      if (this.hintClause != null) {
         this.hintClause.toOracle();
      }

      if (this.whereExpression != null) {
         this.setUQSForWhereColumn(this.whereExpression);
         this.whereExpression = this.whereExpression.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      ArrayList list;
      if (this.fromClause != null) {
         this.fromClause.setBaseFromClauseFound(true);
         if (convertToSubQueryNotReqd) {
            this.setFromClause((FromClause)null);
         } else {
            SetClause sc = this.getSetClause();
            list = sc.getExpression();
            Vector selectItemVector = new Vector();
            Vector colVector = new Vector();
            ArrayList setClauseList = new ArrayList();
            SetClause newSetClause = new SetClause();
            if (list != null && list.toString().toLowerCase().indexOf("select") == -1) {
               colVector.add(list.get(0));

               int i;
               for(i = 1; i < list.size(); ++i) {
                  if (list.get(i).toString().trim().equals("=")) {
                     ++i;
                     selectItemVector.add(list.get(i));
                     if (i + 2 < list.size()) {
                        selectItemVector.add(",");
                     }
                  }

                  ++i;
                  if (i < list.size() && list.get(i).toString().equals(",")) {
                     i += 2;
                     colVector.add(list.get(i));
                  }
               }

               setClauseList.add("(");

               for(i = 0; i < colVector.size(); ++i) {
                  if (colVector.get(i) instanceof TableColumn) {
                     ((TableColumn)colVector.get(i)).setTableName((String)null);
                  }

                  setClauseList.add(colVector.get(i));
                  if (i + 1 < colVector.size()) {
                     setClauseList.add(",");
                  }
               }

               setClauseList.add(")");
               setClauseList.add("=");
               setClauseList.add("(");
               SelectQueryStatement fromSQS = new SelectQueryStatement();
               SelectStatement fromSS = new SelectStatement();
               fromSS.setSelectClause("SELECT");
               fromSS.setSelectItemList(selectItemVector);
               fromSQS.setSelectStatement(fromSS);
               fromSQS.setWhereExpression(this.getWhereExpression());
               fromSQS.setFromClause(this.getFromClause());
               SelectQueryStatement convertedSQS = fromSQS.toOracle();
               if (this.objectContext != null) {
                  this.setObjectContextForSQS(convertedSQS);
               }

               setClauseList.add(convertedSQS);
               setClauseList.add(")");
               newSetClause.setSetExpressionList(setClauseList);
               newSetClause.setSet("SET");
               this.setSetClause(newSetClause);
               this.setFromClause((FromClause)null);
               this.setWhereClause((WhereExpression)null);
            }

            ArrayList expList = this.setClause.getExpression();
            if (this.selectQueryForUpdateStmt) {
               expList.add(0, "(");
               expList.add(expList.size() - 1, ")");
               expList.add(expList.size() - 1, "=");
               expList.add(expList.size() - 1, "(");
               expList.add(")");

               for(int i = 0; i < expList.size(); ++i) {
                  Object obj = expList.get(i);
                  if (obj instanceof SelectQueryStatement) {
                     if (this.objectContext != null) {
                        this.setObjectContextForSQS((SelectQueryStatement)obj);
                     }
                  } else if (obj instanceof TableColumn) {
                     ((TableColumn)obj).setTableName((String)null);
                  }
               }

               this.setClause.setExpression(expList);
               this.setFromClause((FromClause)null);
               this.setWhereClause((WhereExpression)null);
            }
         }
      }

      this.optionalHintClause = null;
      if (this.updateLimitClause != null) {
         this.updateLimitClause.toOracleRowNum(this);
      }

      this.updateLimitClause = null;
      if (this.withString != null && this.isolationLevel != null) {
         String lockStatement = "LOCK TABLE ";
         list = this.getTableExpression().getTableClauseList();

         for(int i = 0; i < list.size(); ++i) {
            Object o = list.get(i);
            if (o instanceof TableClause) {
               TableClause tc = (TableClause)o;
               String s = tc.getTableObject().getTableName();
               lockStatement = lockStatement + s + " IN ";
               if (this.isolationLevel.trim().equalsIgnoreCase("RR") || this.isolationLevel.trim().equalsIgnoreCase("RS")) {
                  lockStatement = lockStatement + "EXCLUSIVE MODE";
                  this.addLockTableList(lockStatement);
               }
            }
         }
      }

      this.withString = null;
      this.isolationLevel = null;
      return this.toString();
   }

   private void setObjectContextForSQS(SelectQueryStatement convertedSQS) {
      convertedSQS.setObjectContext(this.objectContext);
      Vector fromItems;
      int i;
      Object obj;
      if (convertedSQS.getFromClause() != null) {
         convertedSQS.getFromClause().setObjectContext(this.objectContext);
         fromItems = convertedSQS.getFromClause().getFromItemList();
         if (fromItems != null) {
            for(i = 0; i < fromItems.size(); ++i) {
               obj = fromItems.get(i);
               if (obj instanceof FromTable) {
                  ((FromTable)fromItems.get(i)).setObjectContext(this.objectContext);
               } else if (obj instanceof FromClause) {
                  ((FromClause)fromItems.get(i)).setObjectContext(this.objectContext);
               } else if (obj instanceof SelectQueryStatement) {
                  ((SelectQueryStatement)fromItems.get(i)).setObjectContext(this.objectContext);
               }
            }
         }
      }

      if (convertedSQS.getWhereExpression() != null) {
         convertedSQS.getWhereExpression().setObjectContext(this.objectContext);
         fromItems = convertedSQS.getWhereExpression().getWhereItems();
         if (fromItems != null) {
            for(i = 0; i < fromItems.size(); ++i) {
               obj = fromItems.get(i);
               if (obj instanceof WhereItem) {
                  ((WhereItem)obj).setObjectContext(this.objectContext);
                  WhereColumn wc = ((WhereItem)obj).getLeftWhereExp();
                  if (wc != null) {
                     wc.setObjectContext(this.objectContext);
                  }

                  wc = ((WhereItem)obj).getRightWhereExp();
                  if (wc != null) {
                     wc.setObjectContext(this.objectContext);
                  }
               }
            }
         }
      }

   }

   public String toMSSQLServerString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.updateClause.toSQLServer();
      boolean whereExprConverted = false;
      if (this.setClause != null) {
         FromTable ft;
         if (this.tableExp.getTableClauseList() != null) {
            ArrayList tableList = this.tableExp.getTableClauseList();

            for(int i = 0; i < tableList.size(); ++i) {
               Hashtable withTableColumnandIndex = new Hashtable();
               ((TableClause)tableList.get(i)).setToMSSQLServer(true);
               if (((TableClause)tableList.get(i)).getAlias() != null) {
                  TableColumn tc = new TableColumn();
                  TableObject setTableObject = ((TableClause)tableList.get(i)).getTableObject();
                  String setOriginalTableNameForAlias = setTableObject.getTableName();
                  tc.setTableName(setOriginalTableNameForAlias);
                  tc.setOwnerName(setTableObject.getUser());
                  String keyString = ((TableClause)tableList.get(i)).getAlias() + ".";
                  withTableColumnandIndex.put(keyString, tc);
                  if (!((TableClause)tableList.get(i)).getAlias().trim().equals("")) {
                     if (keyString.startsWith(".")) {
                        keyString = keyString.substring(1, keyString.length());
                     }

                     if (keyString.endsWith(".")) {
                        keyString = keyString.substring(0, keyString.length() - 1);
                     }

                     FromClause fc1 = new FromClause();
                     ft = new FromTable();
                     ft.setAliasName(keyString);
                     ft.setTableName(setOriginalTableNameForAlias);
                     fc1.addFromItem(ft);
                     fc1.setFromClause("FROM");
                     this.setFromClause(fc1);
                  }
               }
            }
         }

         this.setClause.toMSSQLServer();
         TableClause sqlTableClause;
         String aliasName;
         boolean subqueryHasAggregateFunction;
         Vector fromItemList;
         Vector fromList;
         int k;
         if (this.setClause.getExpression() != null) {
            subqueryHasAggregateFunction = false;
            ArrayList expressionList = this.setClause.getExpression();
            if (expressionList != null) {
               for(int ii = 0; ii < expressionList.size(); ++ii) {
                  if (expressionList.get(ii) instanceof SelectQueryStatement) {
                     SelectQueryStatement subQuery = (SelectQueryStatement)expressionList.get(ii);
                     SelectStatement subSelectStatement = subQuery.getSelectStatement();
                     FromClause sqlFromClause = subQuery.getFromClause();
                     fromList = sqlFromClause.getFromItemList();
                     fromItemList = subSelectStatement.getSelectItemList();

                     for(k = 0; k < fromItemList.size(); ++k) {
                        if (fromItemList.get(k) instanceof SelectColumn && (((SelectColumn)fromItemList.get(k)).isAggregateFunction() || this.selectColumnHasAggrFunction(((SelectColumn)fromItemList.get(k)).getColumnExpression(), false))) {
                           subqueryHasAggregateFunction = true;
                           if (fromList != null) {
                              for(int k = 0; k < fromList.size(); ++k) {
                                 if (fromList.get(k) instanceof FromTable) {
                                    FromTable sqlFromTable = (FromTable)fromList.get(k);
                                    if (sqlFromTable.getAliasName() == null && this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().size() > 0 && this.tableExp.getTableClauseList().get(0) instanceof TableClause) {
                                       sqlTableClause = (TableClause)this.tableExp.getTableClauseList().get(0);
                                       aliasName = sqlTableClause.getTableObject().toString();
                                       String aliasName = sqlTableClause.getAlias();
                                       if (sqlFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasName.trim())) {
                                          sqlFromTable.setAliasName(aliasName);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (this.setClause.getSetExpressionList() != null) {
            subqueryHasAggregateFunction = false;
            SetExpression se = (SetExpression)this.setClause.getSetExpressionList().get(0);
            if (se.getSubQuery() != null) {
               SelectQueryStatement subQuery = se.getSubQuery();
               SelectStatement subSelectStatement = subQuery.getSelectStatement();
               FromClause sqlFromClause = subQuery.getFromClause();
               Vector fromList = sqlFromClause.getFromItemList();
               fromList = subSelectStatement.getSelectItemList();
               TableClause sqlTableClause;
               String tableName;
               if (fromList != null) {
                  for(int i = 0; i < fromList.size(); ++i) {
                     if (fromList.get(i) instanceof SelectColumn && ((SelectColumn)fromList.get(i)).isAggregateFunction()) {
                        subqueryHasAggregateFunction = true;
                        if (fromList != null) {
                           for(k = 0; k < fromList.size(); ++k) {
                              if (fromList.get(k) instanceof FromTable) {
                                 FromTable sqlFromTable = (FromTable)fromList.get(k);
                                 if (sqlFromTable.getAliasName() == null && this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().size() > 0 && this.tableExp.getTableClauseList().get(0) instanceof TableClause) {
                                    sqlTableClause = (TableClause)this.tableExp.getTableClauseList().get(0);
                                    tableName = sqlTableClause.getTableObject().toString();
                                    aliasName = sqlTableClause.getAlias();
                                    if (sqlFromTable.getTableName().toString().trim().equalsIgnoreCase(tableName.trim())) {
                                       sqlFromTable.setAliasName(aliasName);
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (!subqueryHasAggregateFunction) {
                  fromItemList = subQuery.getFromClause().getFromItemList();
                  ft = new FromTable();
                  ArrayList tableClauseList = this.tableExp.getTableClauseList();
                  sqlTableClause = (TableClause)tableClauseList.get(0);
                  sqlTableClause = null;
                  aliasName = null;
                  tableName = sqlTableClause.getTableObject().toString();
                  aliasName = sqlTableClause.getAlias();
                  boolean addTableName = true;

                  for(int i = 0; i < fromItemList.size(); ++i) {
                     String existingTableName = ((FromTable)fromItemList.get(i)).getTableName().toString();
                     String existingAliasName = ((FromTable)fromItemList.get(i)).getAliasName();
                     if (existingTableName.equalsIgnoreCase(tableName)) {
                        addTableName = false;
                        if (existingAliasName == null) {
                           ((FromTable)fromItemList.get(i)).setAliasName(aliasName);
                        }
                        break;
                     }

                     if (existingAliasName != null && existingAliasName.equalsIgnoreCase(aliasName)) {
                        addTableName = false;
                        break;
                     }
                  }

                  if (addTableName) {
                     ft.setTableName(tableName);
                     ft.setAliasName(aliasName);
                     fromItemList.insertElementAt(ft, 0);
                  }

                  Vector subQueryFromList;
                  int i;
                  if (this.getFromClause() == null) {
                     this.setFromClause(subQuery.getFromClause());
                  } else {
                     Vector updateFromItemList = this.fromClause.getFromItemList();
                     if (subQuery.getFromClause() != null) {
                        subQueryFromList = subQuery.getFromClause().getFromItemList();

                        for(i = 0; i < subQueryFromList.size(); ++i) {
                           boolean alreadyExistsInFromClause = false;

                           for(int index = 0; index < updateFromItemList.size(); ++index) {
                              String multipleQueryString = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
                              if (updateFromItemList.get(index).toString().equalsIgnoreCase(subQueryFromList.get(i).toString())) {
                                 alreadyExistsInFromClause = true;
                                 break;
                              }

                              SelectQueryStatement.singleQueryConvertedToMultipleQueryList = multipleQueryString;
                           }

                           if (!alreadyExistsInFromClause) {
                              updateFromItemList.add(subQueryFromList.get(i));
                           }
                        }
                     }
                  }

                  if (this.getWhereExpression() == null) {
                     this.setWhereClause(subQuery.getWhereExpression());
                     whereExprConverted = true;
                  } else {
                     WhereExpression wExp = subQuery.getWhereExpression();
                     if (wExp != null) {
                        subQueryFromList = wExp.getWhereItems();

                        for(i = 0; i < subQueryFromList.size(); ++i) {
                           if (subQueryFromList.get(i) instanceof WhereItem) {
                              WhereItem wi = (WhereItem)subQueryFromList.get(i);
                              WhereColumn lwc = wi.getLeftWhereExp();
                              if (lwc != null) {
                                 Vector where_col_expression = lwc.getColumnExpression();
                                 if (where_col_expression != null && where_col_expression.get(0) instanceof TableColumn) {
                                    TableColumn tc = (TableColumn)where_col_expression.get(0);
                                    FromTable ftable = MetadataInfoUtil.getTableOfColumn(subQuery, (TableColumn)tc);
                                    if (ftable != null) {
                                       tc.setTableName(ftable.toString());
                                    }
                                 }
                              }

                              WhereColumn rwc = wi.getRightWhereExp();
                              if (rwc != null) {
                                 Vector where_col_expression = rwc.getColumnExpression();
                                 if (where_col_expression != null && where_col_expression.get(0) instanceof TableColumn) {
                                    TableColumn tc = (TableColumn)where_col_expression.get(0);
                                    FromTable ftable = MetadataInfoUtil.getTableOfColumn(subQuery, (TableColumn)tc);
                                    if (ftable != null) {
                                       tc.setTableName(ftable.toString());
                                    }
                                 }
                              }
                           }
                        }
                     }

                     wExp.addWhereExpression(this.getWhereExpression().toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                     wExp.addOperator("AND");
                     this.setWhereClause(wExp);
                     whereExprConverted = true;
                  }

                  this.setOrderByStatement(subQuery.getOrderByStatement());
               }
            }
         }
      }

      if (this.hintClause != null) {
         this.hintClause.toSQLServer();
      }

      if (this.fromClause != null) {
         this.fromClause = this.fromClause.toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.convertAliasNameToTableName();
      this.tableExp.toMSSQLServer();
      if (this.whereExpression != null && !whereExprConverted) {
         if (!SwisSQLAPI.MSSQLSERVER_THETA) {
            SelectQueryStatement sqs1 = new SelectQueryStatement();
            sqs1.setFromClause(this.fromClause);
            sqs1.setWhereExpression(this.whereExpression);
            this.whereExpression = this.whereExpression.toMSSQLServerSelect(sqs1, sqs1);
            this.fromClause = sqs1.getFromClause();
         } else {
            this.whereExpression = this.whereExpression.toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
         }
      }

      this.returningClause = null;
      this.updateLimitClause = null;
      return this.toString();
   }

   public String toSybaseString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.updateClause.toSQLServer();
      boolean whereExprConverted = false;
      if (this.setClause != null) {
         FromTable ft;
         if (this.tableExp.getTableClauseList() != null) {
            ArrayList tableList = this.tableExp.getTableClauseList();

            for(int i = 0; i < tableList.size(); ++i) {
               Hashtable withTableColumnandIndex = new Hashtable();
               if (((TableClause)tableList.get(i)).getAlias() != null) {
                  TableColumn tc = new TableColumn();
                  tc.setObjectContext(this.objectContext);
                  TableObject setTableObject = ((TableClause)tableList.get(i)).getTableObject();
                  String setOriginalTableNameForAlias = setTableObject.getTableName();
                  tc.setTableName(setOriginalTableNameForAlias);
                  tc.setOwnerName(setTableObject.getUser());
                  String keyString = ((TableClause)tableList.get(i)).getAlias() + ".";
                  withTableColumnandIndex.put(keyString, tc);
                  if (!((TableClause)tableList.get(i)).getAlias().trim().equals("")) {
                     if (keyString.startsWith(".")) {
                        keyString = keyString.substring(1, keyString.length());
                     }

                     if (keyString.endsWith(".")) {
                        keyString = keyString.substring(0, keyString.length() - 1);
                     }

                     FromClause fc1 = new FromClause();
                     ft = new FromTable();
                     ft.setAliasName(keyString);
                     ft.setTableName(setOriginalTableNameForAlias);
                     ft.setObjectContext(this.objectContext);
                     fc1.addFromItem(ft);
                     fc1.setFromClause("FROM");
                     fc1.setObjectContext(this.objectContext);
                     this.setFromClause(fc1);
                  }
               }
            }
         }

         this.setClause.setObjectContext(this.objectContext);
         this.setClause.toSybase();
         TableClause sqlTableClause;
         String aliasName;
         boolean subqueryHasAggregateFunction;
         Vector fromItemList;
         Vector fromList;
         int k;
         if (this.setClause.getExpression() != null) {
            subqueryHasAggregateFunction = false;
            ArrayList expressionList = this.setClause.getExpression();
            if (expressionList != null) {
               for(int ii = 0; ii < expressionList.size(); ++ii) {
                  if (expressionList.get(ii) instanceof SelectQueryStatement) {
                     SelectQueryStatement subQuery = (SelectQueryStatement)expressionList.get(ii);
                     SelectStatement subSelectStatement = subQuery.getSelectStatement();
                     FromClause sqlFromClause = subQuery.getFromClause();
                     fromList = sqlFromClause.getFromItemList();
                     fromItemList = subSelectStatement.getSelectItemList();

                     for(k = 0; k < fromItemList.size(); ++k) {
                        if (fromItemList.get(k) instanceof SelectColumn && ((SelectColumn)fromItemList.get(k)).isAggregateFunction()) {
                           subqueryHasAggregateFunction = true;
                           if (fromList != null) {
                              for(int k = 0; k < fromList.size(); ++k) {
                                 if (fromList.get(k) instanceof FromTable) {
                                    FromTable sqlFromTable = (FromTable)fromList.get(k);
                                    if (sqlFromTable.getAliasName() == null && this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().size() > 0 && this.tableExp.getTableClauseList().get(0) instanceof TableClause) {
                                       sqlTableClause = (TableClause)this.tableExp.getTableClauseList().get(0);
                                       aliasName = sqlTableClause.getTableObject().toString();
                                       String aliasName = sqlTableClause.getAlias();
                                       if (sqlFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasName.trim())) {
                                          sqlFromTable.setAliasName(aliasName);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (this.setClause.getSetExpressionList() != null) {
            subqueryHasAggregateFunction = false;
            SetExpression se = (SetExpression)this.setClause.getSetExpressionList().get(0);
            if (se.getSubQuery() != null) {
               SelectQueryStatement subQuery = se.getSubQuery();
               SelectStatement subSelectStatement = subQuery.getSelectStatement();
               FromClause sqlFromClause = subQuery.getFromClause();
               Vector fromList = sqlFromClause.getFromItemList();
               fromList = subSelectStatement.getSelectItemList();
               TableClause sqlTableClause;
               String tableName;
               if (fromList != null) {
                  for(int i = 0; i < fromList.size(); ++i) {
                     if (fromList.get(i) instanceof SelectColumn && ((SelectColumn)fromList.get(i)).isAggregateFunction()) {
                        subqueryHasAggregateFunction = true;
                        if (fromList != null) {
                           for(k = 0; k < fromList.size(); ++k) {
                              if (fromList.get(k) instanceof FromTable) {
                                 FromTable sqlFromTable = (FromTable)fromList.get(k);
                                 if (sqlFromTable.getAliasName() == null && this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().size() > 0 && this.tableExp.getTableClauseList().get(0) instanceof TableClause) {
                                    sqlTableClause = (TableClause)this.tableExp.getTableClauseList().get(0);
                                    tableName = sqlTableClause.getTableObject().toString();
                                    aliasName = sqlTableClause.getAlias();
                                    if (sqlFromTable.getTableName().toString().trim().equalsIgnoreCase(tableName.trim())) {
                                       sqlFromTable.setAliasName(aliasName);
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (!subqueryHasAggregateFunction) {
                  fromItemList = subQuery.getFromClause().getFromItemList();
                  ft = new FromTable();
                  ft.setObjectContext(this.objectContext);
                  ArrayList tableClauseList = this.tableExp.getTableClauseList();
                  sqlTableClause = (TableClause)tableClauseList.get(0);
                  sqlTableClause = null;
                  aliasName = null;
                  tableName = sqlTableClause.getTableObject().toString();
                  aliasName = sqlTableClause.getAlias();
                  boolean addTableName = true;

                  for(int i = 0; i < fromItemList.size(); ++i) {
                     String existingTableName = ((FromTable)fromItemList.get(i)).getTableName().toString();
                     String existingAliasName = ((FromTable)fromItemList.get(i)).getAliasName();
                     if (existingTableName.equalsIgnoreCase(tableName)) {
                        addTableName = false;
                        if (existingAliasName == null) {
                           ((FromTable)fromItemList.get(i)).setAliasName(aliasName);
                        }
                        break;
                     }

                     if (existingAliasName != null && existingAliasName.equalsIgnoreCase(aliasName)) {
                        addTableName = false;
                        break;
                     }
                  }

                  if (addTableName) {
                     ft.setTableName(tableName);
                     ft.setAliasName(aliasName);
                     fromItemList.insertElementAt(ft, 0);
                  }

                  this.setFromClause(subQuery.getFromClause());
                  if (this.getWhereExpression() == null) {
                     if (subQuery.getWhereExpression() != null) {
                        subQuery.getWhereExpression().setObjectContext(this.objectContext);
                     }

                     this.setWhereClause(subQuery.getWhereExpression());
                     whereExprConverted = true;
                  } else {
                     WhereExpression wExp = subQuery.getWhereExpression();
                     wExp.addWhereExpression(this.getWhereExpression().toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                     wExp.addOperator("AND");
                     wExp.setObjectContext(this.objectContext);
                     this.setWhereClause(wExp);
                     whereExprConverted = true;
                  }

                  this.setOrderByStatement(subQuery.getOrderByStatement());
               }
            }
         }
      }

      if (this.hintClause != null) {
         this.hintClause.toSQLServer();
      }

      if (this.fromClause != null) {
         this.fromClause = this.fromClause.toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.convertAliasNameToTableName();
      this.tableExp.toSybase();
      if (this.whereExpression != null && !whereExprConverted) {
         this.whereExpression.setObjectContext(this.objectContext);
         this.whereExpression = this.whereExpression.toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.returningClause = null;
      this.updateLimitClause = null;
      return this.toString();
   }

   public String toDB2String() throws ConvertException {
      this.updateClause.toDB2();
      this.tableExp.toDB2();
      if (this.hintClause != null) {
         this.hintClause.toDB2();
      }

      if (this.setClause != null) {
         this.setClause.setFromUpdateQuerySatetemnt(this);
         this.setClause.toDB2();
      }

      if (this.hintClause != null) {
         this.hintClause = null;
      }

      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toDB2Select((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      if (this.fromClause != null) {
         this.fromClause.convertToSubQuery((UpdateQueryStatement)this, 3, this.getFromClause());
      }

      this.optionalHintClause = null;
      this.returningClause = null;
      this.updateLimitClause = null;
      return this.toString();
   }

   public String toMySQLString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.updateClause.toMySQL();
      if (this.hintClause != null) {
         this.hintClause.toMySQL();
      }

      if (this.setClause != null) {
         this.setClause.setFromUpdateQuerySatetemnt(this);
         if (this.tableExp.getTableClauseList() != null) {
            ArrayList tableList = this.tableExp.getTableClauseList();

            for(int i = 0; i < tableList.size(); ++i) {
               Hashtable withTableColumnandIndex = new Hashtable();
               if (tableList.get(i) instanceof TableClause && ((TableClause)tableList.get(i)).getAlias() != null) {
                  TableColumn tc = new TableColumn();
                  TableObject setTableObject = ((TableClause)tableList.get(i)).getTableObject();
                  String setOriginalTableNameForAlias = setTableObject.getTableName();
                  tc.setTableName(setOriginalTableNameForAlias);
                  tc.setOwnerName(setTableObject.getUser());
                  String keyString = ((TableClause)tableList.get(i)).getAlias() + ".";
                  withTableColumnandIndex.put(keyString, tc);
                  this.setClause.setOriginalTableName(withTableColumnandIndex);
               }
            }
         }

         this.setClause.toMySQL();
      }

      if (this.fromClause != null) {
         throw new ConvertException("from clause has to be changed into subquery");
      } else {
         TableExpression.isUpdateStatement = true;
         this.tableExp.toMySQL();
         TableExpression.isUpdateStatement = false;
         this.convertRownumToUpdateLimitClause();
         if (this.whereExpression != null) {
            this.whereExpression = this.whereExpression.toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
         }

         this.optionalHintClause = null;
         this.returningClause = null;
         return this.toString();
      }
   }

   public String toBigQueryString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.updateClause.toBigQuery();
      if (this.hintClause != null) {
         this.hintClause.toBigQuery();
      }

      if (this.setClause != null) {
         if (this.tableExp.getTableClauseList() != null) {
            ArrayList tableList = this.tableExp.getTableClauseList();
            this.withTableColumnandIndex = new Hashtable();

            for(int i = 0; i < tableList.size(); ++i) {
               if (((TableClause)tableList.get(i)).getAlias() != null) {
                  TableColumn tc = new TableColumn();
                  TableObject setTableObject = ((TableClause)tableList.get(i)).getTableObject();
                  String setOriginalTableNameForAlias = setTableObject.getTableName();
                  tc.setTableName(setOriginalTableNameForAlias);
                  tc.setOwnerName(setTableObject.getUser());
                  String keyString = ((TableClause)tableList.get(i)).getAlias() + ".";
                  this.withTableColumnandIndex.put(keyString, tc);
                  this.setClause.setOriginalTableName(this.withTableColumnandIndex);
               }
            }
         }

         this.setClause.toBigQuery();
      }

      if (this.fromClause != null) {
         this.fromClause = this.fromClause.toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.convertAliasNameToTableName();
      this.tableExp.toBigQuery();
      this.convertRownumToUpdateLimitClause();
      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.optionalHintClause = null;
      this.returningClause = null;
      return this.toString();
   }

   public String toPostgreSQLString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.updateClause.toPostgreSQL();
      if (this.hintClause != null) {
         this.hintClause.toPostgreSQL();
      }

      if (this.setClause != null) {
         if (this.tableExp.getTableClauseList() != null) {
            ArrayList tableList = this.tableExp.getTableClauseList();
            this.withTableColumnandIndex = new Hashtable();

            for(int i = 0; i < tableList.size(); ++i) {
               if (((TableClause)tableList.get(i)).getAlias() != null) {
                  TableColumn tc = new TableColumn();
                  TableObject setTableObject = ((TableClause)tableList.get(i)).getTableObject();
                  String setOriginalTableNameForAlias = setTableObject.getTableName();
                  tc.setTableName(setOriginalTableNameForAlias);
                  tc.setOwnerName(setTableObject.getUser());
                  String keyString = ((TableClause)tableList.get(i)).getAlias() + ".";
                  this.withTableColumnandIndex.put(keyString, tc);
                  this.setClause.setOriginalTableName(this.withTableColumnandIndex);
               }
            }
         }

         this.setClause.toPostgreSQL();
      }

      if (this.fromClause != null) {
         this.fromClause = this.fromClause.toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.convertAliasNameToTableName();
      this.tableExp.toPostgreSQL();
      this.convertRownumToUpdateLimitClause();
      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.optionalHintClause = null;
      this.returningClause = null;
      return this.toString();
   }

   private void convertRownumToUpdateLimitClause() throws ConvertException {
      RownumClause rownumClause = null;
      if (this.whereExpression != null) {
         rownumClause = this.whereExpression.getRownumClause();
      }

      if (rownumClause != null) {
         UpdateLimitClause ulimitClause = new UpdateLimitClause();
         String rownumValue = "0";
         if (rownumClause.getRownumValue() instanceof SelectQueryStatement) {
            throw new ConvertException("Conversion failure.. Subquery can't be converted");
         }

         if (rownumClause.getRownumValue() instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)rownumClause.getRownumValue();
            Vector colExp = sc.getColumnExpression();

            for(int i = 0; i < colExp.size(); ++i) {
               if (colExp.elementAt(i) instanceof FunctionCalls) {
                  throw new ConvertException("Conversion failure.. Function calls can't be converted");
               }

               if (colExp.elementAt(i) instanceof TableColumn) {
                  throw new ConvertException("Conversion failure.. Identifier can't be converted");
               }

               if (!(colExp.elementAt(i) instanceof String) || colExp.size() != 1) {
                  throw new ConvertException("Conversion failure.. Expression can't be converted");
               }

               rownumValue = (String)colExp.elementAt(i);
            }
         }

         ulimitClause.setLimit("LIMIT");
         if (rownumClause.getOperator().equals("<=")) {
            ulimitClause.setDimension(rownumValue);
         } else {
            ulimitClause.setDimension(Integer.parseInt(rownumValue) - 1 + "");
         }

         if (this.getUpdateLimitClause() != null) {
            throw new ConvertException();
         }

         this.setUpdateLimitClause(ulimitClause);
         this.whereExpression.setRownumClause((RownumClause)null);
      }

   }

   public String toANSIString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.updateClause.toANSISQL();
      if (this.setClause != null) {
         if (this.tableExp.getTableClauseList() != null) {
            ArrayList tableList = this.tableExp.getTableClauseList();

            for(int i = 0; i < tableList.size(); ++i) {
               Hashtable withTableColumnandIndex = new Hashtable();
               if (((TableClause)tableList.get(i)).getAlias() != null) {
                  TableColumn tc = new TableColumn();
                  TableObject setTableObject = ((TableClause)tableList.get(i)).getTableObject();
                  String setOriginalTableNameForAlias = setTableObject.getTableName();
                  tc.setTableName(setOriginalTableNameForAlias);
                  String keyString = ((TableClause)tableList.get(i)).getAlias() + ".";
                  withTableColumnandIndex.put(keyString, tc);
                  this.setClause.setOriginalTableName(withTableColumnandIndex);
               }
            }
         }

         this.setClause.toANSISQL();
      }

      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      if (this.fromClause != null) {
         this.fromClause.convertToSubQuery((UpdateQueryStatement)this, 8, this.getFromClause());
      }

      this.convertAliasNameToTableName();
      this.tableExp.toANSISQL();
      this.optionalHintClause = null;
      this.returningClause = null;
      return this.toString();
   }

   public String toSnowflakeString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.updateClause.toSnowflake();
      if (this.hintClause != null) {
         this.hintClause.toSnowflake();
      }

      if (this.setClause != null) {
         if (this.tableExp.getTableClauseList() != null) {
            ArrayList tableList = this.tableExp.getTableClauseList();
            this.withTableColumnandIndex = new Hashtable();

            for(int i = 0; i < tableList.size(); ++i) {
               if (((TableClause)tableList.get(i)).getAlias() != null) {
                  TableColumn tc = new TableColumn();
                  TableObject setTableObject = ((TableClause)tableList.get(i)).getTableObject();
                  String setOriginalTableNameForAlias = setTableObject.getTableName();
                  tc.setTableName(setOriginalTableNameForAlias);
                  tc.setOwnerName(setTableObject.getUser());
                  String keyString = ((TableClause)tableList.get(i)).getAlias() + ".";
                  this.withTableColumnandIndex.put(keyString, tc);
                  this.setClause.setOriginalTableName(this.withTableColumnandIndex);
               }
            }
         }

         this.setClause.toSnowflake();
      }

      if (this.fromClause != null) {
         this.fromClause = this.fromClause.toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.convertAliasNameToTableName();
      this.tableExp.toSnowflake();
      this.convertRownumToUpdateLimitClause();
      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.optionalHintClause = null;
      this.returningClause = null;
      return this.toString();
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

   public String toTeradataString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      if (this.commentObject != null) {
         this.commentObject.setSQLDialect(12);
      }

      this.updateClause.toTeradata();
      if (this.setClause != null) {
         if (this.tableExp.getTableClauseList() != null) {
            ArrayList tableList = this.tableExp.getTableClauseList();

            for(int i = 0; i < tableList.size(); ++i) {
               Hashtable withTableColumnandIndex = new Hashtable();
               if (((TableClause)tableList.get(i)).getAlias() != null) {
                  TableColumn tc = new TableColumn();
                  TableObject setTableObject = ((TableClause)tableList.get(i)).getTableObject();
                  String setOriginalTableNameForAlias = setTableObject.getTableName();
                  tc.setTableName(setOriginalTableNameForAlias);
                  String keyString = ((TableClause)tableList.get(i)).getAlias() + ".";
                  withTableColumnandIndex.put(keyString, tc);
                  this.setClause.setOriginalTableName(withTableColumnandIndex);
               }
            }
         }

         this.setClause.toTeradata();
      }

      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      if (this.fromClause != null) {
         this.fromClause.convertToSubQuery((UpdateQueryStatement)this, 12, this.getFromClause());
      }

      this.convertAliasNameToTableName();
      this.tableExp.toTeradata();
      this.optionalHintClause = null;
      this.returningClause = null;
      return this.toString();
   }

   public String toTimesTenString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      new StringBuffer();
      this.updateClause.toTimesTen();
      this.tableExp.toTimesTen();
      if (this.setClause != null) {
         this.setClause.setFromUpdateQuerySatetemnt(this);
         this.setClause.toTimesTen();
      }

      if (this.whereExpression != null) {
         this.setUQSForWhereColumnInTimesTen(this.whereExpression);
         this.whereExpression = this.whereExpression.toTimesTenSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      if (this.fromClause != null) {
         throw new ConvertException("\nFROM clause is not supported in UPDATE statement in TimesTen 5.1.21\n");
      } else {
         this.optionalHintClause = null;
         this.updateLimitClause = null;
         this.returningClause = null;
         return this.toString();
      }
   }

   public String toNetezzaString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.updateClause.toNetezza();
      boolean whereExprConverted = false;
      if (this.setClause != null) {
         FromTable ft;
         if (this.tableExp.getTableClauseList() != null) {
            ArrayList tableList = this.tableExp.getTableClauseList();

            for(int i = 0; i < tableList.size(); ++i) {
               Hashtable withTableColumnandIndex = new Hashtable();
               if (((TableClause)tableList.get(i)).getAlias() != null) {
                  TableColumn tc = new TableColumn();
                  TableObject setTableObject = ((TableClause)tableList.get(i)).getTableObject();
                  String setOriginalTableNameForAlias = setTableObject.getTableName();
                  tc.setTableName(setOriginalTableNameForAlias);
                  tc.setOwnerName(setTableObject.getUser());
                  String keyString = ((TableClause)tableList.get(i)).getAlias() + ".";
                  withTableColumnandIndex.put(keyString, tc);
                  if (!((TableClause)tableList.get(i)).getAlias().trim().equals("")) {
                     if (keyString.startsWith(".")) {
                        keyString = keyString.substring(1, keyString.length());
                     }

                     if (keyString.endsWith(".")) {
                        keyString = keyString.substring(0, keyString.length() - 1);
                     }

                     FromClause fc1 = new FromClause();
                     ft = new FromTable();
                     ft.setAliasName(keyString);
                     ft.setTableName(setOriginalTableNameForAlias);
                     fc1.addFromItem(ft);
                     fc1.setFromClause("FROM");
                     this.setFromClause(fc1);
                  }
               }
            }
         }

         this.setClause.toNetezza();
         TableClause sqlTableClause;
         String aliasName;
         boolean subqueryHasAggregateFunction;
         Vector fromItemList;
         Vector fromList;
         int k;
         if (this.setClause.getExpression() != null) {
            subqueryHasAggregateFunction = false;
            ArrayList expressionList = this.setClause.getExpression();
            if (expressionList != null) {
               for(int ii = 0; ii < expressionList.size(); ++ii) {
                  if (expressionList.get(ii) instanceof SelectQueryStatement) {
                     SelectQueryStatement subQuery = (SelectQueryStatement)expressionList.get(ii);
                     SelectStatement subSelectStatement = subQuery.getSelectStatement();
                     FromClause sqlFromClause = subQuery.getFromClause();
                     fromList = sqlFromClause.getFromItemList();
                     fromItemList = subSelectStatement.getSelectItemList();

                     for(k = 0; k < fromItemList.size(); ++k) {
                        if (fromItemList.get(k) instanceof SelectColumn && (((SelectColumn)fromItemList.get(k)).isAggregateFunction() || this.selectColumnHasAggrFunction(((SelectColumn)fromItemList.get(k)).getColumnExpression(), false))) {
                           subqueryHasAggregateFunction = true;
                           if (fromList != null) {
                              for(int k = 0; k < fromList.size(); ++k) {
                                 if (fromList.get(k) instanceof FromTable) {
                                    FromTable sqlFromTable = (FromTable)fromList.get(k);
                                    if (sqlFromTable.getAliasName() == null && this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().size() > 0 && this.tableExp.getTableClauseList().get(0) instanceof TableClause) {
                                       sqlTableClause = (TableClause)this.tableExp.getTableClauseList().get(0);
                                       aliasName = sqlTableClause.getTableObject().toString();
                                       String aliasName = sqlTableClause.getAlias();
                                       System.out.println("The alais of table clause is" + aliasName);
                                       if (sqlFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasName.trim())) {
                                          sqlFromTable.setAliasName(aliasName);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (this.setClause.getSetExpressionList() != null) {
            subqueryHasAggregateFunction = false;
            SetExpression se = (SetExpression)this.setClause.getSetExpressionList().get(0);
            if (se.getSubQuery() != null) {
               SelectQueryStatement subQuery = se.getSubQuery();
               SelectStatement subSelectStatement = subQuery.getSelectStatement();
               FromClause sqlFromClause = subQuery.getFromClause();
               Vector fromList = sqlFromClause.getFromItemList();
               fromList = subSelectStatement.getSelectItemList();
               TableClause sqlTableClause;
               String tableName;
               if (fromList != null) {
                  for(int i = 0; i < fromList.size(); ++i) {
                     if (fromList.get(i) instanceof SelectColumn && ((SelectColumn)fromList.get(i)).isAggregateFunction()) {
                        subqueryHasAggregateFunction = true;
                        if (fromList != null) {
                           for(k = 0; k < fromList.size(); ++k) {
                              if (fromList.get(k) instanceof FromTable) {
                                 FromTable sqlFromTable = (FromTable)fromList.get(k);
                                 if (sqlFromTable.getAliasName() == null && this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().size() > 0 && this.tableExp.getTableClauseList().get(0) instanceof TableClause) {
                                    sqlTableClause = (TableClause)this.tableExp.getTableClauseList().get(0);
                                    tableName = sqlTableClause.getTableObject().toString();
                                    aliasName = sqlTableClause.getAlias();
                                    if (sqlFromTable.getTableName().toString().trim().equalsIgnoreCase(tableName.trim())) {
                                       sqlFromTable.setAliasName(aliasName);
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (!subqueryHasAggregateFunction) {
                  fromItemList = subQuery.getFromClause().getFromItemList();
                  ft = new FromTable();
                  ArrayList tableClauseList = this.tableExp.getTableClauseList();
                  sqlTableClause = (TableClause)tableClauseList.get(0);
                  sqlTableClause = null;
                  aliasName = null;
                  tableName = sqlTableClause.getTableObject().toString();
                  aliasName = sqlTableClause.getAlias();
                  boolean addTableName = true;

                  for(int i = 0; i < fromItemList.size(); ++i) {
                     String existingTableName = ((FromTable)fromItemList.get(i)).getTableName().toString();
                     String existingAliasName = ((FromTable)fromItemList.get(i)).getAliasName();
                     if (existingTableName.equalsIgnoreCase(tableName)) {
                        addTableName = false;
                        if (existingAliasName == null) {
                           ((FromTable)fromItemList.get(i)).setAliasName(aliasName);
                        }
                        break;
                     }

                     if (existingAliasName != null && existingAliasName.equalsIgnoreCase(aliasName)) {
                        addTableName = false;
                        break;
                     }
                  }

                  if (addTableName) {
                     ft.setTableName(tableName);
                     ft.setAliasName(aliasName);
                     fromItemList.insertElementAt(ft, 0);
                  }

                  Vector subQueryFromList;
                  int i;
                  if (this.getFromClause() == null) {
                     this.setFromClause(subQuery.getFromClause());
                  } else {
                     Vector updateFromItemList = this.fromClause.getFromItemList();
                     if (subQuery.getFromClause() != null) {
                        subQueryFromList = subQuery.getFromClause().getFromItemList();

                        for(i = 0; i < subQueryFromList.size(); ++i) {
                           boolean alreadyExistsInFromClause = false;

                           for(int index = 0; index < updateFromItemList.size(); ++index) {
                              String multipleQueryString = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
                              if (updateFromItemList.get(index).toString().equalsIgnoreCase(subQueryFromList.get(i).toString())) {
                                 alreadyExistsInFromClause = true;
                                 break;
                              }

                              SelectQueryStatement.singleQueryConvertedToMultipleQueryList = multipleQueryString;
                           }

                           if (!alreadyExistsInFromClause) {
                              updateFromItemList.add(subQueryFromList.get(i));
                           }
                        }
                     }
                  }

                  if (this.getWhereExpression() == null) {
                     this.setWhereClause(subQuery.getWhereExpression());
                     whereExprConverted = true;
                  } else {
                     WhereExpression wExp = subQuery.getWhereExpression();
                     if (wExp != null) {
                        subQueryFromList = wExp.getWhereItems();

                        for(i = 0; i < subQueryFromList.size(); ++i) {
                           if (subQueryFromList.get(i) instanceof WhereItem) {
                              WhereItem wi = (WhereItem)subQueryFromList.get(i);
                              WhereColumn lwc = wi.getLeftWhereExp();
                              if (lwc != null) {
                                 Vector where_col_expression = lwc.getColumnExpression();
                                 if (where_col_expression != null && where_col_expression.get(0) instanceof TableColumn) {
                                    TableColumn tc = (TableColumn)where_col_expression.get(0);
                                    FromTable ftable = MetadataInfoUtil.getTableOfColumn(subQuery, (TableColumn)tc);
                                    if (ftable != null) {
                                       tc.setTableName(ftable.toString());
                                    }
                                 }
                              }

                              WhereColumn rwc = wi.getRightWhereExp();
                              if (rwc != null) {
                                 Vector where_col_expression = rwc.getColumnExpression();
                                 if (where_col_expression != null && where_col_expression.get(0) instanceof TableColumn) {
                                    TableColumn tc = (TableColumn)where_col_expression.get(0);
                                    FromTable ftable = MetadataInfoUtil.getTableOfColumn(subQuery, (TableColumn)tc);
                                    if (ftable != null) {
                                       tc.setTableName(ftable.toString());
                                    }
                                 }
                              }
                           }
                        }
                     }

                     wExp.addWhereExpression(this.getWhereExpression().toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                     wExp.addOperator("AND");
                     this.setWhereClause(wExp);
                     whereExprConverted = true;
                  }

                  this.setOrderByStatement(subQuery.getOrderByStatement());
               }
            }
         }
      }

      if (this.hintClause != null) {
         this.hintClause.toSQLServer();
      }

      if (this.fromClause != null) {
         this.fromClause = this.fromClause.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.convertAliasNameToTableName();
      this.tableExp.toNetezza();
      if (this.whereExpression != null && !whereExprConverted) {
         if (!SwisSQLAPI.MSSQLSERVER_THETA) {
            SelectQueryStatement sqs1 = new SelectQueryStatement();
            sqs1.setFromClause(this.fromClause);
            sqs1.setWhereExpression(this.whereExpression);
            this.whereExpression = this.whereExpression.toNetezzaSelect(sqs1, sqs1);
            this.fromClause = sqs1.getFromClause();
         } else {
            this.whereExpression = this.whereExpression.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
         }
      }

      this.returningClause = null;
      this.updateLimitClause = null;
      return this.toString();
   }

   private void setUQSForWhereColumnInTimesTen(WhereExpression we) throws ConvertException {
      Vector wis = we.getWhereItems();
      if (wis != null) {
         for(int i = 0; i < wis.size(); ++i) {
            Object obj = wis.get(i);
            if (obj instanceof WhereItem) {
               WhereItem wi = (WhereItem)obj;
               if (wi.getRownumClause() != null) {
                  wis.remove(i);
                  we.getOperator().remove(i - 1);
                  RownumClause rnc = wi.getRownumClause();
                  String rownum_operator = rnc.getOperator();
                  if (!rownum_operator.trim().equalsIgnoreCase("<")) {
                     throw new ConvertException("\nROWNUM conditions with lessthan('<') is supported.\n");
                  }

                  if (rnc.getRownumValue() != null) {
                     int rownum_value = -1;

                     try {
                        Object rownum_value_obj = rnc.getRownumValue();
                        rownum_value = Integer.parseInt(rownum_value_obj + "");
                     } catch (Exception var10) {
                     }

                     this.setTimesTenFirst(rownum_value);
                  }

                  --i;
               } else {
                  WhereColumn lwc = wi.getLeftWhereExp();
                  if (lwc != null) {
                     lwc.setFromUQS(this);
                  }

                  WhereColumn rwc = wi.getRightWhereExp();
                  if (rwc != null) {
                     rwc.setFromUQS(this);
                  }
               }
            }
         }
      }

   }

   private void setUQSForWhereColumn(WhereExpression we) {
      Vector wis = we.getWhereItems();
      if (wis != null) {
         for(int i = 0; i < wis.size(); ++i) {
            Object obj = wis.get(i);
            if (obj instanceof WhereItem) {
               WhereItem wi = (WhereItem)obj;
               WhereColumn lwc = wi.getLeftWhereExp();
               if (lwc != null) {
                  lwc.setFromUQS(this);
               }

               WhereColumn rwc = wi.getRightWhereExp();
               if (rwc != null) {
                  rwc.setFromUQS(this);
               }
            }
         }
      }

   }

   public void convertAliasNameToTableName() throws ConvertException {
      ArrayList al_tcl = this.tableExp.getTableClauseList();
      if (al_tcl != null && al_tcl.get(0) instanceof TableClause) {
         TableClause tc = (TableClause)al_tcl.get(0);
         String s_an = tc.getAlias();
         String s_tn = tc.getTableObject().toString();
         if (this.whereExpression != null && this.fromClause == null) {
            this.changeWhereColumn(this.whereExpression, s_an, s_tn);
         }
      }

   }

   public void changeWhereColumn(WhereExpression we, String s_an, String s_tn) {
      Vector v_wi = we.getWhereItems();

      for(int i_count = 0; i_count < v_wi.size(); ++i_count) {
         if (!(v_wi.elementAt(i_count) instanceof WhereItem)) {
            if (v_wi.elementAt(i_count) instanceof WhereExpression) {
               this.changeWhereColumn((WhereExpression)v_wi.elementAt(i_count), s_an, s_tn);
            }
         } else {
            WhereItem wi = (WhereItem)v_wi.elementAt(i_count);
            WhereColumn lwc = wi.getLeftWhereExp();
            WhereColumn rwc = wi.getRightWhereExp();
            Vector v_lce;
            int i_icount;
            TableColumn tc;
            String s_tablename;
            if (lwc != null) {
               v_lce = lwc.getColumnExpression();
               if (v_lce != null) {
                  for(i_icount = 0; i_icount < v_lce.size(); ++i_icount) {
                     if (v_lce.elementAt(i_icount) instanceof TableColumn) {
                        tc = (TableColumn)v_lce.elementAt(i_icount);
                        s_tablename = tc.getTableName();
                        if (s_tablename != null && s_tablename.equalsIgnoreCase(s_an)) {
                           tc.setTableName(s_tn);
                        }
                     } else if (v_lce.elementAt(i_icount) instanceof FunctionCalls) {
                        FunctionCalls fc = (FunctionCalls)v_lce.elementAt(i_icount);
                        Vector functionArguments = fc.getFunctionArguments();
                        if (functionArguments != null) {
                           for(int i = 0; i < functionArguments.size(); ++i) {
                              if (functionArguments.get(i) instanceof SelectColumn) {
                                 SelectColumn sc = (SelectColumn)functionArguments.get(i);
                                 sc.setOriginalTableNamesForUpdateSetClause(this.withTableColumnandIndex);
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (rwc != null) {
               v_lce = rwc.getColumnExpression();
               if (v_lce != null) {
                  for(i_icount = 0; i_icount < v_lce.size(); ++i_icount) {
                     if (v_lce.elementAt(i_icount) instanceof TableColumn) {
                        tc = (TableColumn)v_lce.elementAt(i_icount);
                        s_tablename = tc.getTableName();
                        if (s_tablename != null && s_tablename.equalsIgnoreCase(s_an)) {
                           tc.setTableName(s_tn);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private String singleQueryIntoMultipleQueriesForPLSQL() {
      return SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
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

   private boolean processSetClauseIntoASingleSubQuery(ArrayList expList) {
      boolean bool = false;
      Vector aliasOrTableList = new Vector();
      Vector selectColumnVector = new Vector();

      for(int index = 0; index < expList.size(); ++index) {
         if (expList.get(index) instanceof SelectColumn && index > 1 && expList.get(index - 1) instanceof String && expList.get(index - 1).toString().trim().equals("=")) {
            SelectColumn sc = (SelectColumn)expList.get(index);
            selectColumnVector.add(sc);
            if (sc.getColumnExpression() != null) {
               Vector colExp = sc.getColumnExpression();
               this.addAllTheElementsInColumnExpression(colExp, aliasOrTableList, bool);
            }
         }
      }

      if (aliasOrTableList.size() > 0) {
         this.selectQueryForUpdateStmt = true;
         ArrayList columnList = new ArrayList();
         expList.add(this.convertSetClauseExpressionIntoASingleSubQuery(selectColumnVector, aliasOrTableList));

         for(int index = 0; index < expList.size(); ++index) {
            if (expList.get(index) instanceof SelectColumn && index > 1 && expList.get(index - 1) instanceof String && expList.get(index - 1).toString().trim().equals("=")) {
               expList.remove(index - 1);
               columnList.add(expList.remove(index - 1));
            }
         }
      }

      if (this.fromClause != null && this.fromClause.getFromItemList() != null) {
         Vector fromItems = this.fromClause.getFromItemList();
         Vector tableOrAliasList = new Vector();

         for(int j = 0; j < fromItems.size(); ++j) {
            if (fromItems.get(j) instanceof FromTable) {
               FromTable ft = (FromTable)fromItems.get(j);
               if (ft.getAliasName() != null) {
                  tableOrAliasList.add(ft.getAliasName());
               } else if (ft.getTableName() != null && ft.getTableName() instanceof String) {
                  tableOrAliasList.add(ft.getTableName().toString());
               }
            } else if (fromItems.get(j) instanceof FromClause) {
               this.addAllTheFromItemsInFromItemList(fromItems, tableOrAliasList);
            }
         }

         if (bool) {
            this.removeWhereItemsAfterAllProcess(this.whereExpression, tableOrAliasList);
         }
      }

      return bool;
   }

   private void addAllTheFromItemsInFromItemList(Vector fromItems, Vector tableOrAliasList) {
      for(int i = 0; i < fromItems.size(); ++i) {
         if (fromItems.get(i) instanceof FromTable) {
            FromTable ft = (FromTable)fromItems.get(i);
            if (ft.getAliasName() != null) {
               tableOrAliasList.add(ft.getAliasName());
            } else if (ft.getTableName() != null && ft.getTableName() instanceof String) {
               tableOrAliasList.add(ft.getTableName().toString());
            }
         } else if (fromItems.get(i) instanceof FromClause) {
            Vector newFromItemList = ((FromClause)fromItems.get(i)).getFromItemList();
            this.addAllTheFromItemsInFromItemList(newFromItemList, tableOrAliasList);
         }
      }

   }

   private void addAllTheElementsInColumnExpression(Vector colExp, Vector aliasOrTableList, boolean bool) {
      for(int i = 0; i < colExp.size(); ++i) {
         Vector fromItems;
         if (colExp.get(i) instanceof TableColumn) {
            TableColumn tc = (TableColumn)colExp.get(i);
            if (this.fromClause != null && this.fromClause.getFromItemList() != null && tc.getTableName() != null) {
               fromItems = this.fromClause.getFromItemList();

               for(int j = 0; j < fromItems.size(); ++j) {
                  if (fromItems.get(j) instanceof FromTable) {
                     FromTable ft = (FromTable)fromItems.get(j);
                     if (ft.getAliasName() != null && ft.getAliasName().equalsIgnoreCase(tc.getTableName())) {
                        bool = true;
                        aliasOrTableList.add(ft.getAliasName());
                     } else if (ft.getTableName() != null && ft.getTableName() instanceof String && ft.getTableName().toString().equalsIgnoreCase(tc.getTableName())) {
                        bool = true;
                        aliasOrTableList.add(ft.getTableName().toString());
                     }
                  } else if (fromItems.get(j) instanceof FromClause) {
                     Vector newFromItems = ((FromClause)fromItems.get(j)).getFromItemList();
                     this.addAllTheFromItemsInFromItemList(newFromItems, aliasOrTableList);
                  }
               }
            }
         } else {
            Vector fromItems;
            int j;
            FromTable ft;
            if (colExp.get(i) instanceof CaseStatement) {
               if (this.fromClause != null && this.fromClause.getFromItemList() != null) {
                  fromItems = this.fromClause.getFromItemList();

                  for(j = 0; j < fromItems.size(); ++j) {
                     if (fromItems.get(j) instanceof FromTable) {
                        ft = (FromTable)fromItems.get(j);
                        if (ft.getAliasName() != null) {
                           aliasOrTableList.add(ft.getAliasName());
                        } else if (ft.getTableName() != null && ft.getTableName() instanceof String) {
                           aliasOrTableList.add(ft.getTableName().toString());
                        }
                     }
                  }

                  bool = true;
               }
            } else if (!(colExp.get(i) instanceof FunctionCalls)) {
               if (colExp.get(i) instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)colExp.get(i);
                  fromItems = sc.getColumnExpression();
                  this.addAllTheElementsInColumnExpression(fromItems, aliasOrTableList, bool);
               }
            } else if (this.fromClause != null && this.fromClause.getFromItemList() != null) {
               fromItems = this.fromClause.getFromItemList();

               for(j = 0; j < fromItems.size(); ++j) {
                  if (fromItems.get(j) instanceof FromTable) {
                     ft = (FromTable)fromItems.get(j);
                     if (ft.getAliasName() != null) {
                        aliasOrTableList.add(ft.getAliasName());
                     } else if (ft.getTableName() != null && ft.getTableName() instanceof String) {
                        aliasOrTableList.add(ft.getTableName().toString());
                     }
                  }
               }

               bool = true;
            }
         }
      }

   }

   private void removeTheFromTableForUpdate(Vector fromItems) {
      String tableName = "";
      TableExpression te = this.tableExp;
      ArrayList tableClauseList = te.getTableClauseList();
      if (tableClauseList != null && tableClauseList.size() > 0 && tableClauseList.get(0) instanceof TableClause) {
         tableName = ((TableClause)tableClauseList.get(0)).getTableObject().getTableName();
      }

      for(int i = 0; i < fromItems.size(); ++i) {
         if (fromItems.get(i) instanceof FromTable) {
            FromTable ft = (FromTable)fromItems.get(i);
            if (ft.getAliasName() != null && ft.getAliasName().equalsIgnoreCase(tableName)) {
               fromItems.remove(i);
            } else if (ft.getTableName() != null && ft.getTableName() instanceof String && ft.getTableName().toString().equalsIgnoreCase(tableName)) {
               fromItems.remove(i);
            }
         } else if (fromItems.get(i) instanceof FromClause) {
            Vector newFCItems = ((FromClause)fromItems.get(i)).getFromItemList();
            this.removeTheFromTableForUpdate(newFCItems);
         }
      }

   }

   public SelectQueryStatement convertSetClauseExpressionIntoASingleSubQuery(Vector selectColumns, Vector tableOrAliasList) {
      SelectQueryStatement sqs = new SelectQueryStatement();
      SelectStatement ss = new SelectStatement();
      ss.setSelectClause("SELECT");

      for(int i = 0; i < selectColumns.size(); ++i) {
         SelectColumn sc = (SelectColumn)selectColumns.get(i);
         if (i < selectColumns.size() - 1) {
            sc.setEndsWith(",");
         }
      }

      ss.setSelectItemList(selectColumns);
      sqs.setSelectStatement(ss);
      FromClause fc = new FromClause();
      fc.setFromClause("FROM");
      new Vector();
      Vector fromItems = this.fromClause.getFromItemList();
      fc.setFromItemList(fromItems);
      sqs.setFromClause(fc);
      if (this.whereExpression != null && this.whereExpression.getWhereItems() != null) {
         new WhereExpression();
         sqs.setWhereExpression(this.whereExpression);
      }

      return sqs;
   }

   public void processWhereExpressionForConvertingForSingleSubQuery(WhereExpression we, WhereExpression whereExpression, Vector tableOrAliasList) {
      Vector whereExpressionList = whereExpression.getWhereItems();
      Vector operators = whereExpression.getOperator();

      for(int i = 0; i < whereExpressionList.size(); ++i) {
         if (whereExpressionList.get(i) instanceof WhereItem) {
            WhereItem wi = (WhereItem)whereExpressionList.get(i);
            boolean isremoved = false;
            int j;
            int k;
            String tableOrAliasReferenceName;
            TableColumn tc;
            String op;
            WhereColumn rwc;
            if (wi.getLeftWhereExp() != null) {
               rwc = wi.getLeftWhereExp();
               if (rwc.getColumnExpression() != null) {
                  for(j = 0; j < rwc.getColumnExpression().size(); ++j) {
                     if (rwc.getColumnExpression().get(j) instanceof TableColumn) {
                        for(k = 0; k < tableOrAliasList.size(); ++k) {
                           tableOrAliasReferenceName = tableOrAliasList.get(k).toString();
                           tc = (TableColumn)rwc.getColumnExpression().get(j);
                           if (tc.getTableName() != null && tc.getTableName().equalsIgnoreCase(tableOrAliasReferenceName) && !isremoved) {
                              isremoved = true;
                              if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                                 if (i < whereExpressionList.size()) {
                                    we.addWhereItem((WhereItem)whereExpressionList.get(i));
                                 }

                                 if (i != 0) {
                                    if (operators.get(i - 1).toString().equalsIgnoreCase("&AND")) {
                                       we.addOperator("AND");
                                    } else if (operators.get(i - 1).toString().equalsIgnoreCase("&OR")) {
                                       we.addOperator("OR");
                                    } else {
                                       we.addOperator(operators.get(i - 1).toString());
                                    }
                                 } else {
                                    we.addOperator(operators.get(i).toString());
                                 }
                              } else {
                                 we.addOperator(operators.get(i).toString());
                                 we.addWhereItem((WhereItem)whereExpressionList.get(i));
                              }

                              if (i != 0) {
                                 op = (String)operators.get(i - 1);
                                 if (!op.equals("&AND")) {
                                    operators.setElementAt("&AND", i - 1);
                                 } else if (operators.size() > i) {
                                    operators.setElementAt("&AND", i);
                                 }
                              } else if (operators.size() > i) {
                                 operators.setElementAt("", i);
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (wi.getRightWhereExp() != null && !isremoved) {
               rwc = wi.getRightWhereExp();
               if (rwc.getColumnExpression() != null) {
                  for(j = 0; j < rwc.getColumnExpression().size(); ++j) {
                     if (rwc.getColumnExpression().get(j) instanceof TableColumn) {
                        for(k = 0; k < tableOrAliasList.size(); ++k) {
                           tableOrAliasReferenceName = tableOrAliasList.get(k).toString();
                           tc = (TableColumn)rwc.getColumnExpression().get(j);
                           if (tc.getTableName() != null && tc.getTableName().equalsIgnoreCase(tableOrAliasReferenceName) && !isremoved) {
                              if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                                 if (i < whereExpressionList.size()) {
                                    we.addWhereItem((WhereItem)whereExpressionList.get(i));
                                 }

                                 isremoved = true;
                                 if (i != 0) {
                                    if (operators.get(i - 1).toString().equalsIgnoreCase("&AND")) {
                                       we.addOperator("AND");
                                    } else if (operators.get(i - 1).toString().equalsIgnoreCase("&OR")) {
                                       we.addOperator("OR");
                                    } else {
                                       we.addOperator(operators.get(i - 1).toString());
                                    }
                                 } else {
                                    we.addOperator(operators.get(i).toString());
                                 }
                              } else {
                                 we.addWhereItem((WhereItem)whereExpressionList.get(i));
                              }

                              if (whereExpressionList.size() > i) {
                              }

                              if (i != 0) {
                                 op = (String)operators.get(i - 1);
                                 if (!op.equals("&AND")) {
                                    operators.setElementAt("&AND", i - 1);
                                 } else if (operators.size() > i) {
                                    operators.setElementAt("&AND", i);
                                 }
                              } else if (operators.size() > i) {
                                 operators.setElementAt("&AND", i);
                              }
                           }
                        }
                     }
                  }
               }
            }
         } else if (whereExpressionList.get(i) instanceof WhereExpression) {
            WhereExpression subWhereExp = (WhereExpression)whereExpressionList.get(i);
            WhereExpression newWE = new WhereExpression();
            newWE.setOpenBrace(subWhereExp.getOpenBrace());
            newWE.setCloseBrace(subWhereExp.getCloseBrace());
            this.processWhereExpressionForConvertingForSingleSubQuery(newWE, subWhereExp, tableOrAliasList);

            for(int countNum = 0; countNum < subWhereExp.getOperator().size(); ++countNum) {
            }

            we.addWhereExpression(newWE);
         }
      }

   }

   private boolean processSetClauseExpressionIntoSubQuery(ArrayList expList) {
      boolean bool = false;

      for(int index = 0; index < expList.size(); ++index) {
         if (expList.get(index) instanceof SelectColumn && index > 1 && expList.get(index - 1) instanceof String && expList.get(index - 1).toString().trim().equals("=")) {
            SelectColumn sc = (SelectColumn)expList.get(index);
            if (sc.getColumnExpression() != null) {
               Vector colExp = sc.getColumnExpression();

               for(int i = 0; i < colExp.size(); ++i) {
                  Vector aliasOrTableList;
                  if (colExp.get(i) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)colExp.get(i);
                     if (this.fromClause != null && this.fromClause.getFromItemList() != null && tc.getTableName() != null) {
                        aliasOrTableList = this.fromClause.getFromItemList();
                        Vector aliasOrTableList = new Vector();

                        for(int j = 0; j < aliasOrTableList.size(); ++j) {
                           if (aliasOrTableList.get(j) instanceof FromTable) {
                              FromTable ft = (FromTable)aliasOrTableList.get(j);
                              if (ft.getAliasName() != null) {
                                 aliasOrTableList.add(ft.getAliasName());
                              } else if (ft.getTableName() != null && ft.getTableName() instanceof String) {
                                 aliasOrTableList.add(ft.getTableName().toString());
                              }
                           }
                        }

                        expList.set(index, this.convertSetClauseExpressionIntoSubQuery(sc, aliasOrTableList));
                        expList.add(index, "(");
                        expList.add(index + 2, ")");
                        bool = true;
                     }
                  } else {
                     Vector fromItems;
                     int j;
                     FromTable ft;
                     if (colExp.get(i) instanceof CaseStatement) {
                        if (this.fromClause != null && this.fromClause.getFromItemList() != null) {
                           fromItems = this.fromClause.getFromItemList();
                           aliasOrTableList = new Vector();

                           for(j = 0; j < fromItems.size(); ++j) {
                              if (fromItems.get(j) instanceof FromTable) {
                                 ft = (FromTable)fromItems.get(j);
                                 if (ft.getAliasName() != null) {
                                    aliasOrTableList.add(ft.getAliasName());
                                 } else if (ft.getTableName() != null && ft.getTableName() instanceof String) {
                                    aliasOrTableList.add(ft.getTableName().toString());
                                 }
                              }
                           }

                           expList.set(index, this.convertSetClauseExpressionIntoSubQuery(sc, aliasOrTableList));
                           expList.add(index, "(");
                           expList.add(index + 2, ")");
                           bool = true;
                        }
                     } else if (colExp.get(i) instanceof FunctionCalls && this.fromClause != null && this.fromClause.getFromItemList() != null) {
                        fromItems = this.fromClause.getFromItemList();
                        aliasOrTableList = new Vector();

                        for(j = 0; j < fromItems.size(); ++j) {
                           if (fromItems.get(j) instanceof FromTable) {
                              ft = (FromTable)fromItems.get(j);
                              if (ft.getAliasName() != null) {
                                 aliasOrTableList.add(ft.getAliasName());
                              } else if (ft.getTableName() != null && ft.getTableName() instanceof String) {
                                 aliasOrTableList.add(ft.getTableName().toString());
                              }
                           }
                        }

                        expList.set(index, this.convertSetClauseExpressionIntoSubQuery(sc, aliasOrTableList));
                        expList.add(index, "(");
                        expList.add(index + 2, ")");
                        bool = true;
                     }
                  }
               }
            }
         }
      }

      if (this.fromClause != null && this.fromClause.getFromItemList() != null) {
         Vector fromItems = this.fromClause.getFromItemList();
         Vector tableOrAliasList = new Vector();

         for(int j = 0; j < fromItems.size(); ++j) {
            if (fromItems.get(j) instanceof FromTable) {
               FromTable ft = (FromTable)fromItems.get(j);
               if (ft.getAliasName() != null) {
                  tableOrAliasList.add(ft.getAliasName());
               } else if (ft.getTableName() != null && ft.getTableName() instanceof String) {
                  tableOrAliasList.add(ft.getTableName().toString());
               }
            }
         }

         if (bool) {
            this.removeWhereItemsAfterAllProcess(this.whereExpression, tableOrAliasList);
         }
      }

      return bool;
   }

   public void removeWhereItemsAfterAllProcess(WhereExpression we, Vector tableOrAliasList) {
      if (we != null) {
         Vector whereExpressionList = we.getWhereItems();

         for(int i = 0; i < whereExpressionList.size(); ++i) {
            if (!(whereExpressionList.get(i) instanceof WhereItem)) {
               if (whereExpressionList.get(i) instanceof WhereExpression) {
                  new WhereExpression();
                  WhereExpression subWhereExp = (WhereExpression)whereExpressionList.get(i);
                  this.removeWhereItemsAfterAllProcess(subWhereExp, tableOrAliasList);
               }
            } else {
               WhereItem wi = (WhereItem)whereExpressionList.get(i);
               boolean isremoved = false;
               WhereColumn rwc;
               int j;
               int k;
               String tableOrAliasReferenceName;
               TableColumn tc;
               if (wi.getLeftWhereExp() != null) {
                  rwc = wi.getLeftWhereExp();
                  if (rwc.getColumnExpression() != null) {
                     for(j = 0; j < rwc.getColumnExpression().size(); ++j) {
                        if (rwc.getColumnExpression().get(j) instanceof TableColumn) {
                           for(k = 0; k < tableOrAliasList.size(); ++k) {
                              tableOrAliasReferenceName = tableOrAliasList.get(k).toString();
                              tc = (TableColumn)rwc.getColumnExpression().get(j);
                              if (tc.getTableName() != null && tc.getTableName().trim().equalsIgnoreCase(tableOrAliasReferenceName.trim())) {
                                 isremoved = true;
                                 if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                                    whereExpressionList.set(i, (Object)null);
                                 } else {
                                    whereExpressionList.set(i, (Object)null);
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (wi.getRightWhereExp() != null && !isremoved) {
                  rwc = wi.getRightWhereExp();
                  if (rwc.getColumnExpression() != null) {
                     for(j = 0; j < rwc.getColumnExpression().size(); ++j) {
                        if (rwc.getColumnExpression().get(j) instanceof TableColumn) {
                           for(k = 0; k < tableOrAliasList.size(); ++k) {
                              tableOrAliasReferenceName = tableOrAliasList.get(k).toString();
                              tc = (TableColumn)rwc.getColumnExpression().get(j);
                              if (tc.getTableName() != null && tc.getTableName().trim().equalsIgnoreCase(tableOrAliasReferenceName.trim())) {
                                 if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                                    whereExpressionList.set(i, (Object)null);
                                 } else {
                                    whereExpressionList.set(i, (Object)null);
                                 }

                                 if (whereExpressionList.size() > i) {
                                    whereExpressionList.set(i, (Object)null);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public SelectQueryStatement convertSetClauseExpressionIntoSubQuery(SelectColumn sc, Vector tableOrAliasList) {
      SelectQueryStatement sqs = new SelectQueryStatement();
      SelectStatement ss = new SelectStatement();
      ss.setSelectClause("SELECT");
      Vector selectColumns = new Vector();
      selectColumns.add(sc);
      ss.setSelectItemList(selectColumns);
      sqs.setSelectStatement(ss);
      FromClause fc = new FromClause();
      fc.setFromClause("FROM");
      Vector fromItems = new Vector();
      fromItems.addAll(this.fromClause.getFromItemList());
      fc.setFromItemList(fromItems);
      sqs.setFromClause(fc);
      if (this.whereExpression != null && this.whereExpression.getWhereItems() != null) {
         WhereExpression we = new WhereExpression();
         this.processWhereExpressionForConvertingToSubQuery(we, this.whereExpression, tableOrAliasList);
         sqs.setWhereExpression(we);
      }

      return sqs;
   }

   public void processWhereExpressionForConvertingToSubQuery(WhereExpression we, WhereExpression whereExpression, Vector tableOrAliasList) {
      Vector whereExpressionList = whereExpression.getWhereItems();
      Vector operators = whereExpression.getOperator();

      for(int i = 0; i < whereExpressionList.size(); ++i) {
         if (whereExpressionList.get(i) instanceof WhereItem) {
            WhereItem wi = (WhereItem)whereExpressionList.get(i);
            boolean isremoved = false;
            int j;
            int k;
            String tableOrAliasReferenceName;
            TableColumn tc;
            String op;
            WhereColumn rwc;
            if (wi.getLeftWhereExp() != null) {
               rwc = wi.getLeftWhereExp();
               if (rwc.getColumnExpression() != null) {
                  for(j = 0; j < rwc.getColumnExpression().size(); ++j) {
                     if (rwc.getColumnExpression().get(j) instanceof TableColumn) {
                        for(k = 0; k < tableOrAliasList.size(); ++k) {
                           tableOrAliasReferenceName = tableOrAliasList.get(k).toString();
                           tc = (TableColumn)rwc.getColumnExpression().get(j);
                           if (tc.getTableName() != null && tc.getTableName().equalsIgnoreCase(tableOrAliasReferenceName)) {
                              isremoved = true;
                              if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                                 we.addWhereItem((WhereItem)whereExpressionList.get(i));
                                 if (i != 0) {
                                    if (operators.get(i - 1).toString().equalsIgnoreCase("&AND")) {
                                       we.addOperator("AND");
                                    } else if (operators.get(i - 1).toString().equalsIgnoreCase("&OR")) {
                                       we.addOperator("AND");
                                    } else {
                                       we.addOperator(operators.get(i - 1).toString());
                                    }
                                 } else {
                                    we.addOperator("AND");
                                 }
                              } else {
                                 we.addWhereItem((WhereItem)whereExpressionList.get(i));
                              }

                              if (i != 0) {
                                 op = (String)operators.get(i - 1);
                                 if (!op.equals("&AND")) {
                                    operators.setElementAt("&AND", i - 1);
                                 } else if (operators.size() > i) {
                                    operators.setElementAt("&AND", i);
                                 }
                              } else if (operators.size() > i) {
                                 operators.setElementAt("&AND", i);
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (wi.getRightWhereExp() != null && !isremoved) {
               rwc = wi.getRightWhereExp();
               if (rwc.getColumnExpression() != null) {
                  for(j = 0; j < rwc.getColumnExpression().size(); ++j) {
                     if (rwc.getColumnExpression().get(j) instanceof TableColumn) {
                        for(k = 0; k < tableOrAliasList.size(); ++k) {
                           tableOrAliasReferenceName = tableOrAliasList.get(k).toString();
                           tc = (TableColumn)rwc.getColumnExpression().get(j);
                           if (tc.getTableName() != null && tc.getTableName().equalsIgnoreCase(tableOrAliasReferenceName)) {
                              if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                                 we.addWhereItem((WhereItem)whereExpressionList.get(i));
                                 if (i != 0) {
                                    if (operators.get(i - 1).toString().equalsIgnoreCase("&AND")) {
                                       we.addOperator("AND");
                                    } else if (operators.get(i - 1).toString().equalsIgnoreCase("&OR")) {
                                       we.addOperator("OR");
                                    } else {
                                       we.addOperator("AND");
                                    }
                                 } else {
                                    we.addOperator("AND");
                                 }
                              } else {
                                 we.addWhereItem((WhereItem)whereExpressionList.get(i));
                              }

                              if (i != 0) {
                                 op = (String)operators.get(i - 1);
                                 if (!op.equals("&AND")) {
                                    operators.setElementAt("&AND", i - 1);
                                 } else if (operators.size() > i) {
                                    operators.setElementAt("&AND", i);
                                 }
                              } else if (operators.size() > i) {
                                 operators.setElementAt("&AND", i);
                              }
                           }
                        }
                     }
                  }
               }
            }
         } else if (whereExpressionList.get(i) instanceof WhereExpression) {
            WhereExpression subWhereExp = (WhereExpression)whereExpressionList.get(i);
            WhereExpression newWE = new WhereExpression();
            newWE.setOpenBrace(subWhereExp.getOpenBrace());
            newWE.setCloseBrace(subWhereExp.getCloseBrace());
            this.processWhereExpressionForConvertingToSubQuery(newWE, subWhereExp, tableOrAliasList);

            for(int countNum = 0; countNum < subWhereExp.getOperator().size(); ++countNum) {
               newWE.addOperator((String)subWhereExp.getOperator().get(countNum));
            }

            we.addWhereExpression(newWE);
         }
      }

   }

   public UserObjectContext getObjectContext() {
      return this.objectContext;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.objectContext = obj;
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
