package com.adventnet.swissqlapi.sql.statement.insert;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.CreateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WithStatement;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;
import com.adventnet.swissqlapi.sql.statement.update.ReturningClause;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

public class InsertQueryStatement implements SwisSQLStatement {
   private UserObjectContext objectContext = null;
   private InsertClause insertClause = null;
   private ValuesClause valuesClause = null;
   private SetClauseInsert setClause = null;
   private ReturningClause returningClause = null;
   private SelectQueryStatement subQuery = null;
   private SelectQueryStatement selectQueryStatement;
   private CommentClass commentObject;
   private ArrayList insertValList = null;
   private ArrayList commonTableExprList = new ArrayList();
   private String iqsString;
   private String cqsString;
   private String dqsString;
   private String generalComments;
   private String isolationLevel = null;
   private String withString = null;
   private ArrayList lockTableList = new ArrayList();
   public static ArrayList tablesWithRowIDColumnsList = new ArrayList();
   String subQueryOpenBrace = null;
   String subQueryCloseBrace = null;
   String multiInsertAll = null;
   private ArrayList multiTableInsertStmtList = new ArrayList();
   private ArrayList conditionalInsertClausesList = new ArrayList();
   public WithStatement withStmt = null;
   private ArrayList multiValuesInsertStmtList = null;
   private String onDuplicateKeyUpdate = null;
   private ArrayList onDuplicateKeyUpdateExpList = new ArrayList();

   public void setCommentClass(CommentClass commentObject) {
      this.commentObject = commentObject;
   }

   public CommentClass getCommentClass() {
      return this.commentObject;
   }

   public void setMultipleValuesInsertStmts(ArrayList insertStmts) {
      this.multiValuesInsertStmtList = insertStmts;
   }

   public void setInsertClause(InsertClause IC) {
      this.insertClause = IC;
   }

   public InsertClause getInsertClause() {
      return this.insertClause;
   }

   public void setValuesClause(ValuesClause VC) {
      this.valuesClause = VC;
   }

   public void setSetClause(SetClauseInsert SC) {
      this.setClause = SC;
   }

   public void setOnDuplicateKeyUpdate(String onDuplicateKeyUpdate) {
      this.onDuplicateKeyUpdate = onDuplicateKeyUpdate;
   }

   public void setOnDuplicateKeyUpdateExpression(ArrayList onDuplicateKeyUpdateExpList) {
      this.onDuplicateKeyUpdateExpList = onDuplicateKeyUpdateExpList;
   }

   public void setSubQuery(SelectQueryStatement squery) {
      this.subQuery = squery;
   }

   public void setReturningClause(ReturningClause RC) {
      this.returningClause = RC;
   }

   public void setSelectQueryStatement(SelectQueryStatement sqs) {
      this.selectQueryStatement = sqs;
   }

   public void setGeneralComments(String generalComments) {
      this.generalComments = generalComments;
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

   public void setCommonTableExprList(ArrayList commonTableExprList) {
      this.commonTableExprList = commonTableExprList;
   }

   public void setMultiInsertALL(String all) {
      this.multiInsertAll = all;
   }

   public void setMultiTableInsertStmtList(ArrayList insertStmtList) {
      this.multiTableInsertStmtList = insertStmtList;
   }

   public void setMultiTableInsertStmt(ArrayList insertStmtList) {
      this.multiTableInsertStmtList = insertStmtList;
   }

   public void setConditionalInsertClausesList(ArrayList conditionalInsertList) {
      this.conditionalInsertClausesList = conditionalInsertList;
   }

   public SelectQueryStatement getSubQuery() {
      return this.subQuery != null ? this.subQuery : this.selectQueryStatement;
   }

   public ValuesClause getValuesClause() {
      return this.valuesClause;
   }

   public SetClauseInsert getSetClause() {
      return this.setClause;
   }

   public String getOnDuplicateKeyUpdate() {
      return this.onDuplicateKeyUpdate;
   }

   public ArrayList getOnDuplicateKeyUpdateExpression() {
      return this.onDuplicateKeyUpdateExpList;
   }

   public ReturningClause getReturningClause() {
      return this.returningClause;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.commentObject != null) {
         String commentStr = this.commentObject.toString().trim();
         sb.append(commentStr + "\n ");
      }

      int i;
      for(i = 0; i < this.lockTableList.size(); ++i) {
         sb.append(this.lockTableList.get(i).toString() + ";\n");
      }

      if (this.singleQueryIntoMultipleQueriesForPLSQL() != null) {
         sb.append(this.singleQueryIntoMultipleQueriesForPLSQL());
         SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
      }

      if (this.cqsString != null) {
         sb.append(this.cqsString.trim() + ";\n\n");
      }

      if (this.generalComments != null) {
         sb.append(this.generalComments + "\n\n");
      }

      if (this.iqsString != null) {
         sb.append(this.iqsString.trim() + ";\n\n");
      }

      if (this.insertClause != null) {
         if (this.insertClause.getLockStatement() != null) {
            sb.append(this.insertClause.getLockStatement() + "\n");
         }

         if (this.objectContext != null) {
            this.insertClause.setObjectContext(this.objectContext);
         }

         sb.append(this.insertClause.toString() + " \n");
      }

      if (this.valuesClause != null) {
         this.valuesClause.setObjectContext(this.objectContext);
         sb.append(this.valuesClause.toString() + " \n");
      } else if (this.setClause != null) {
         this.setClause.setObjectContext(this.objectContext);
         sb.append(this.setClause.toString() + " \n");
      }

      if (this.onDuplicateKeyUpdate != null) {
         sb.append(this.onDuplicateKeyUpdate + " \n");
         if (this.onDuplicateKeyUpdateExpList != null && this.onDuplicateKeyUpdateExpList.size() != 0) {
            i = this.onDuplicateKeyUpdateExpList.size();

            for(int i = 0; i < i; ++i) {
               sb.append(this.onDuplicateKeyUpdateExpList.get(i) + " ");
            }
         }
      }

      if (this.returningClause != null) {
         sb.append(this.returningClause.toString() + " \n");
      }

      if (this.subQuery != null) {
         this.subQuery.setObjectContext(this.objectContext);
         if (this.subQueryOpenBrace != null) {
            sb.append(this.subQueryOpenBrace);
         }

         sb.append(this.subQuery.toString());
         if (this.subQueryCloseBrace != null) {
            sb.append(this.subQueryCloseBrace + " \n");
         }
      }

      if (!this.commonTableExprList.isEmpty()) {
         if (this.withStmt != null) {
            sb.append(this.withStmt.toString());
         } else {
            for(i = 0; i < this.commonTableExprList.size(); ++i) {
               sb.append(this.commonTableExprList.get(i));
               if (i + 1 < this.commonTableExprList.size()) {
                  sb.append(",\n");
               }
            }
         }

         sb.append("\n");
      }

      if (this.selectQueryStatement != null) {
         this.selectQueryStatement.setObjectContext(this.objectContext);
         sb.append(this.selectQueryStatement.toString());
      }

      if (this.dqsString != null) {
         StringBuffer sb1 = new StringBuffer();
         sb1.append(sb.toString().trim() + ";\n\n" + this.dqsString + "\n\n");
         return sb1.toString();
      } else {
         if (this.withString != null) {
            sb.append(this.withString + " ");
         }

         if (this.isolationLevel != null) {
            sb.append(this.isolationLevel);
         }

         if (this.multiValuesInsertStmtList != null) {
            for(i = 0; i < this.multiValuesInsertStmtList.size(); ++i) {
               Object obj = this.multiValuesInsertStmtList.get(i);
               if (obj instanceof InsertQueryStatement) {
                  sb.append(";\n");
                  InsertQueryStatement insQueryStmt = (InsertQueryStatement)obj;
                  sb.append(insQueryStmt.toString());
               }
            }
         }

         return sb.toString();
      }
   }

   public String removeIndent(String s_ri) {
      s_ri = s_ri.replace('\n', ' ');
      s_ri = s_ri.replace('\t', ' ');
      return s_ri;
   }

   public String toInformixString() throws ConvertException {
      if (this.onDuplicateKeyUpdate != null) {
         throw new ConvertException("On dupicate key update clause is yet to be supported");
      } else {
         this.withString = null;
         this.isolationLevel = null;
         this.insertClause.toInformix(this);
         if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
         }

         if (this.valuesClause != null) {
            ValuesClause tempValuesClause = this.valuesClause.toInformix();
            this.setValuesClause(tempValuesClause);
         }

         if (this.subQuery != null) {
            this.subQuery = this.subQuery.toInformix();
            this.subQueryOpenBrace = null;
            this.subQueryCloseBrace = null;
         }

         if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toInformix();
         }

         return this.toString();
      }
   }

   public String toOracleString() throws ConvertException {
      if (this.multiInsertAll != null) {
         throw new ConvertException("Multi table insert statement yet to be supported");
      } else if (this.onDuplicateKeyUpdate != null) {
         throw new ConvertException("On dupicate key update clause is yet to be supported");
      } else {
         this.insertClause.toOracle(this);
         if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
         }

         if (this.valuesClause != null) {
            this.valuesClause.setInsertQueryStatement(this);
            ValuesClause tempValuesClause = this.valuesClause.toOracle();
            this.setValuesClause(tempValuesClause);
         }

         if (this.subQuery != null) {
            this.subQuery = this.subQuery.toOracle();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
         }

         if (!this.commonTableExprList.isEmpty()) {
            this.withStmt = new WithStatement();
            this.withStmt.setWith("WITH");
            Vector cteList = new Vector();

            for(int i = 0; i < this.commonTableExprList.size(); ++i) {
               CommonTableExpression cte = (CommonTableExpression)this.commonTableExprList.get(i);
               String withStr = cte.getWith();
               if (withStr != null) {
                  cte.setWith((String)null);
               }

               cteList.add(cte);
               this.commonTableExprList.set(i, cte);
            }

            this.withStmt.setCommonTableExpressionList(cteList);
            this.withStmt.setWithSQS(this.selectQueryStatement);
            this.setSelectQueryStatement((SelectQueryStatement)null);
            this.withStmt.toOracle();
         }

         if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toOracle();
         }

         if (this.withString != null && this.isolationLevel != null) {
            String lockStatement = "LOCK TABLE ";
            ArrayList tableList = this.getInsertClause().getTableExpression().getTableClauseList();

            for(int i = 0; i < tableList.size(); ++i) {
               Object o = tableList.get(i);
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
   }

   public String toMySQLString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.insertClause.toMySQL(this);
      if (this.valuesClause != null) {
         ValuesClause tempValuesClause = this.valuesClause.toMySQL();
         this.setValuesClause(tempValuesClause);
      }

      if (this.subQuery != null) {
         throw new ConvertException();
      } else {
         return this.toString();
      }
   }

   public String toMSSQLServerString() throws ConvertException {
      if (this.onDuplicateKeyUpdate != null) {
         throw new ConvertException("On dupicate key update clause is yet to be supported");
      } else {
         this.withString = null;
         this.isolationLevel = null;
         this.insertClause.toSQLServer(this);
         if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
         }

         if (this.valuesClause != null) {
            this.valuesClause.setInsertQueryStatement(this);
            ValuesClause tempValuesClause = this.valuesClause.toMSSQLServer();
            this.setValuesClause(tempValuesClause);
         }

         if (this.subQuery != null) {
            this.subQuery = this.subQuery.toMSSQLServer();
            this.subQueryOpenBrace = null;
            this.subQueryCloseBrace = null;
         }

         if (this.returningClause != null) {
            if (SwisSQLOptions.isReturningClauseConversionNeeded) {
               SelectQueryStatement sqs = new SelectQueryStatement();
               SelectStatement ss = new SelectStatement();
               SelectColumn sc1 = new SelectColumn();
               ArrayList list = this.returningClause.getrhsVariableList();
               StringBuffer stringbuffer = new StringBuffer();
               int i = 0;

               for(int size = list.size(); i < size; ++i) {
                  stringbuffer.append(" " + list.get(i).toString() + " ");
               }

               Vector v = new Vector();
               v.add(stringbuffer + " = SCOPE_IDENTITY()");
               sc1.setColumnExpression(v);
               Vector items = new Vector();
               items.add(sc1);
               ss.setSelectItemList(items);
               ss.setSelectClause("SELECT");
               sqs.setSelectStatement(ss);
               this.selectQueryStatement = sqs;
            }

            this.returningClause = null;
         }

         if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toMSSQLServer();
         }

         return this.toString();
      }
   }

   public String toSybaseString() throws ConvertException {
      if (this.onDuplicateKeyUpdate != null) {
         throw new ConvertException("On dupicate key update clause is yet to be supported");
      } else {
         this.withString = null;
         this.isolationLevel = null;
         this.insertClause.toSybase(this);
         if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
         }

         if (this.valuesClause != null) {
            ValuesClause tempValuesClause = this.valuesClause.toSybase();
            this.setValuesClause(tempValuesClause);
         }

         if (this.subQuery != null) {
            this.subQuery = this.subQuery.toSybase();
            this.subQueryOpenBrace = null;
            this.subQueryCloseBrace = null;
         }

         if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toSybase();
         }

         return this.toString();
      }
   }

   public String toBigQueryString() throws ConvertException {
      if (this.onDuplicateKeyUpdate != null) {
         throw new ConvertException("On dupicate key update clause is yet to be supported");
      } else {
         this.withString = null;
         this.isolationLevel = null;
         this.insertClause.toBigQuery(this);
         if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
         }

         if (this.valuesClause != null) {
            ValuesClause tempValuesClause = this.valuesClause.toBigQuery();
            this.setValuesClause(tempValuesClause);
         }

         if (this.subQuery != null) {
            this.subQuery = this.subQuery.toBigQuery();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
         }

         if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toBigQuery();
         }

         return this.toString();
      }
   }

   public String toPostgreSQLString() throws ConvertException {
      if (this.onDuplicateKeyUpdate != null) {
         throw new ConvertException("On dupicate key update clause is yet to be supported");
      } else {
         this.withString = null;
         this.isolationLevel = null;
         this.insertClause.toPostgres(this);
         if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
         }

         if (this.valuesClause != null) {
            ValuesClause tempValuesClause = this.valuesClause.toPostgreSQL();
            this.setValuesClause(tempValuesClause);
         }

         if (this.subQuery != null) {
            this.subQuery = this.subQuery.toPostgreSQL();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
         }

         if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toPostgreSQL();
         }

         return this.toString();
      }
   }

   public String toDB2String() throws ConvertException {
      if (this.onDuplicateKeyUpdate != null) {
         throw new ConvertException("On dupicate key update clause is yet to be supported");
      } else {
         this.insertClause.toDB2(this);
         if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
         }

         TableExpression te;
         ArrayList tcList;
         String colName;
         if (this.subQuery != null) {
            te = this.insertClause.getTableExpression();
            tcList = te.getTableClauseList();
            if (tcList != null && tcList.size() == 1 && tcList.get(0) instanceof TableClause) {
               ArrayList columnList = this.insertClause.getColumnList();
               TableClause tableClause = (TableClause)tcList.get(0);
               TableObject to = tableClause.getTableObject();
               if (to != null) {
                  colName = to.getTableName().toLowerCase().trim();
                  if (SwisSQLOptions.addRowidColumnForAllDB2Tables && colName != null && tablesWithRowIDColumnsList.contains(colName)) {
                     SelectStatement ss = this.subQuery.getSelectStatement();
                     if (ss.getSelectItemList() != null) {
                        Vector selectList = ss.getSelectItemList();
                        SelectColumn sc = new SelectColumn();
                        TableColumn tc = new TableColumn();
                        tc.setColumnName("GENERATE_UNIQUE()");
                        Vector selectColumnList = new Vector();
                        selectColumnList.add(tc);
                        sc.setColumnExpression(selectColumnList);
                        sc.setEndsWith(",");
                        if (columnList != null && columnList.size() > 0) {
                           columnList.add(1, "RowId");
                           columnList.add(2, ",");
                        }

                        selectList.add(0, sc);
                     }
                  }
               }
            }

            this.subQuery = this.subQuery.toDB2();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
         }

         if (this.insertClause != null && this.insertClause.getColumnList() != null) {
            ArrayList columnList = this.insertClause.getColumnList();
            this.insertValList = new ArrayList();
            TableClause tableClause = (TableClause)this.insertClause.getTableExpression().getTableClauseList().get(0);
            String tableName = tableClause.getTableObject().getTableName();

            for(int i = 0; i < columnList.size(); ++i) {
               Object obj = columnList.get(i);
               if (obj instanceof String) {
                  colName = (String)obj;
                  if (!colName.trim().equals("(") && !colName.trim().equals(",") && !colName.trim().equals(")")) {
                     TableColumn tableColumn = new TableColumn();
                     tableColumn.setTableName(tableName);
                     tableColumn.setColumnName(colName);
                     this.insertValList.add(tableColumn);
                  }
               }
            }
         }

         if (this.valuesClause != null) {
            if (this.insertClause != null && this.insertValList != null && this.insertValList.size() > 0) {
               this.valuesClause.setInsertValList(this.insertValList);
            }

            te = this.insertClause.getTableExpression();
            tcList = te.getTableClauseList();
            if (tcList != null && tcList.size() == 1 && tcList.get(0) instanceof TableClause) {
               TableClause tableClause = (TableClause)tcList.get(0);
               TableObject to = tableClause.getTableObject();
               if (to != null) {
                  String tableName = to.getTableName().toLowerCase().trim();
                  ArrayList columnList = this.insertClause.getColumnList();
                  if (SwisSQLOptions.addRowidColumnForAllDB2Tables && tableName != null && tablesWithRowIDColumnsList.contains(tableName)) {
                     ArrayList valuesList = this.valuesClause.getValuesList();
                     SelectColumn sc = new SelectColumn();
                     TableColumn tc = new TableColumn();
                     tc.setColumnName("GENERATE_UNIQUE()");
                     Vector selectColumnList = new Vector();
                     selectColumnList.add(tc);
                     sc.setColumnExpression(selectColumnList);
                     sc.setEndsWith(",");
                     if (columnList != null && columnList.size() > 0) {
                        columnList.add(1, "RowId");
                        columnList.add(2, ",");
                     }

                     valuesList.add(1, sc);
                  }
               }
            }

            ValuesClause tempValuesClause = this.valuesClause.toDB2();
            this.setValuesClause(tempValuesClause);
         }

         if (this.selectQueryStatement != null) {
            if (this.insertClause != null && this.insertValList != null && this.insertValList.size() > 0) {
               this.selectQueryStatement.setInsertValList(this.insertValList);
            }

            this.selectQueryStatement = this.selectQueryStatement.toDB2();
         }

         return this.toString();
      }
   }

   public String toANSIString() throws ConvertException {
      if (this.onDuplicateKeyUpdate != null) {
         throw new ConvertException("On dupicate key update clause is yet to be supported");
      } else {
         this.withString = null;
         this.isolationLevel = null;
         this.insertClause.toANSISQL(this);
         if (this.valuesClause != null) {
            ValuesClause tempValuesClause = this.valuesClause.toANSI();
            this.setValuesClause(tempValuesClause);
         }

         if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
         }

         if (this.subQuery != null) {
            this.subQuery = this.subQuery.toANSI();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
         }

         if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toANSI();
         }

         return this.toString();
      }
   }

   public String toTeradataString() throws ConvertException {
      if (this.onDuplicateKeyUpdate != null) {
         throw new ConvertException("On dupicate key update clause is yet to be supported");
      } else {
         this.withString = null;
         this.isolationLevel = null;
         if (this.commentObject != null) {
            this.commentObject.setSQLDialect(12);
         }

         this.insertClause.toTeradata(this);
         if (this.valuesClause != null) {
            ValuesClause tempValuesClause = this.valuesClause.toTeradata();
            this.setValuesClause(tempValuesClause);
         }

         if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
         }

         if (this.subQuery != null) {
            this.subQuery = this.subQuery.toTeradata();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
         }

         if (this.selectQueryStatement != null) {
            this.selectQueryStatement.setTopLevel(true);
            this.selectQueryStatement = this.selectQueryStatement.toTeradata();
         }

         return this.toString();
      }
   }

   public String toTimesTenString() throws ConvertException {
      if (this.onDuplicateKeyUpdate != null) {
         throw new ConvertException("On dupicate key update clause is yet to be supported");
      } else {
         this.withString = null;
         this.isolationLevel = null;
         this.insertClause.toTimesTen(this);
         if (this.setClause != null) {
            this.setClause = null;
         }

         if (this.valuesClause != null) {
            ValuesClause tempValuesClause = this.valuesClause.toTimesTen();
            this.setValuesClause(tempValuesClause);
         }

         if (this.subQuery != null) {
            this.subQuery = this.subQuery.toTimesTen();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
         }

         if (this.selectQueryStatement != null) {
            TableExpression te = this.insertClause.getTableExpression();
            ArrayList tblClauseList = te.getTableClauseList();
            String iqsTableName = ((TableClause)tblClauseList.get(0)).getTableObject().getTableName();
            FromClause fc = this.selectQueryStatement.getFromClause();
            if (fc != null) {
               Vector fromItems = fc.getFromItemList();
               if (fromItems.size() == 1) {
                  Object obj = ((FromTable)fromItems.get(0)).getTableName();
                  if (obj instanceof String && obj.toString().equalsIgnoreCase(iqsTableName)) {
                     CreateQueryStatement cqs = SwisSQLUtils.constructCQS("ADV_IQSTABLE1", this.selectQueryStatement, this);
                     this.cqsString = cqs.toTimesTenString();
                     InsertQueryStatement newIQS = new InsertQueryStatement();
                     InsertClause ic = new InsertClause();
                     ic.setInsert("INSERT");
                     OptionalSpecifier optionalSpecifier = new OptionalSpecifier();
                     optionalSpecifier.setInto("INTO");
                     ic.setOptionalSpecifier(optionalSpecifier);
                     newIQS.setInsertClause(ic);
                     TableExpression texpr = new TableExpression();
                     ArrayList newList = new ArrayList();
                     TableClause tc = new TableClause();
                     TableObject to = new TableObject();
                     to.setTableName("ADV_IQSTABLE1");
                     tc.setTableObject(to);
                     newList.add(tc);
                     texpr.setTableClauseList(newList);
                     ic.setTableExpression(texpr);
                     SelectQueryStatement newSQS = new SelectQueryStatement();
                     newSQS.setSelectStatement(this.selectQueryStatement.getSelectStatement());
                     FromClause newFC = new FromClause();
                     newFC.setFromClause("FROM");
                     Vector newFromItems = new Vector();
                     FromTable newFT = new FromTable();
                     newFT.setTableName(obj.toString());
                     newFromItems.add(newFT);
                     newFC.setFromItemList(newFromItems);
                     newSQS.setFromClause(newFC);
                     newIQS.setSelectQueryStatement(newSQS);
                     Vector sourceSItems = this.selectQueryStatement.getSelectStatement().getSelectItemList();
                     boolean isAliasExists = false;

                     for(int k = 0; k < sourceSItems.size(); ++k) {
                        Object sourceObj = sourceSItems.get(k);
                        if (sourceObj instanceof SelectColumn && ((SelectColumn)sourceObj).getAliasName() != null) {
                           isAliasExists = true;
                           break;
                        }
                     }

                     if (!isAliasExists) {
                        Vector newSelItems = new Vector();
                        Vector colNames = cqs.getColumnNames();
                        ArrayList colList = new ArrayList();
                        colList.add("(");

                        for(int k = 0; k < colNames.size(); ++k) {
                           TableColumn tCol = new TableColumn();
                           tCol.setColumnName(((CreateColumn)colNames.get(k)).getColumnName());
                           SelectColumn sCol = new SelectColumn();
                           Vector colExpr = new Vector();
                           colExpr.add(tCol);
                           sCol.setColumnExpression(colExpr);
                           if (k != colNames.size() - 1) {
                              sCol.setEndsWith(",");
                           }

                           newSelItems.add(sCol);
                           colList.add(tCol.getColumnName());
                           if (k != colNames.size() - 1) {
                              colList.add(",");
                           }
                        }

                        colList.add(")");
                        this.insertClause.setColumnList(colList);
                        this.selectQueryStatement.getSelectStatement().setSelectItemList(newSelItems);
                     }

                     this.iqsString = newIQS.toString();
                     this.dqsString = "DROP TABLE ADV_IQSTABLE1;";
                     ((FromTable)fromItems.get(0)).setTableName("ADV_IQSTABLE1");
                  }
               }
            }

            this.selectQueryStatement = this.selectQueryStatement.toTimesTen();
         }

         this.setReturningClause((ReturningClause)null);
         return this.toString();
      }
   }

   public String toNetezzaString() throws ConvertException {
      if (this.onDuplicateKeyUpdate != null) {
         throw new ConvertException("On dupicate key update clause is yet to be supported");
      } else {
         this.withString = null;
         this.isolationLevel = null;
         InsertQueryStatement convInsertQuery;
         ArrayList convertedStmtList;
         StringBuffer convertedInsertStmtStr;
         int i;
         if (this.multiInsertAll != null && this.multiTableInsertStmtList.size() > 0) {
            convertedStmtList = this.splitMultitableInsertStatement(this.multiTableInsertStmtList);
            convertedInsertStmtStr = new StringBuffer();

            for(i = 0; i < convertedStmtList.size(); ++i) {
               convInsertQuery = (InsertQueryStatement)convertedStmtList.get(i);
               convertedInsertStmtStr.append(convInsertQuery.toNetezzaString() + "\n");
            }

            return convertedInsertStmtStr.toString();
         } else if (this.multiInsertAll != null && this.conditionalInsertClausesList.size() > 0) {
            convertedStmtList = new ArrayList();

            for(int i = 0; i < this.conditionalInsertClausesList.size(); ++i) {
               ConditionalInsertClause conditionalInsertCl = (ConditionalInsertClause)this.conditionalInsertClausesList.get(i);
               convInsertQuery = conditionalInsertCl.toNetezza(this);
               convertedStmtList.add(convInsertQuery);
            }

            convertedInsertStmtStr = new StringBuffer();

            for(i = 0; i < convertedStmtList.size(); ++i) {
               convInsertQuery = (InsertQueryStatement)convertedStmtList.get(i);
               convertedInsertStmtStr.append(convInsertQuery.toNetezzaString() + "\n");
            }

            return convertedInsertStmtStr.toString();
         } else {
            this.insertClause.toNetezza(this);
            if (this.valuesClause != null) {
               ValuesClause tempValuesClause = this.valuesClause.toNetezza();
               this.setValuesClause(tempValuesClause);
            }

            if (this.setClause != null) {
               this.setClause.toGeneric(this);
               this.setClause = null;
            }

            if (this.subQuery != null) {
               this.subQuery = this.subQuery.toNetezza();
               this.subQueryOpenBrace = "(";
               this.subQueryCloseBrace = ")";
            }

            if (this.selectQueryStatement != null) {
               this.selectQueryStatement = this.selectQueryStatement.toNetezza();
            }

            if (this.returningClause != null) {
               this.returningClause = null;
            }

            return this.toString();
         }
      }
   }

   public String toSnowflakeString() throws ConvertException {
      if (this.onDuplicateKeyUpdate != null) {
         throw new ConvertException("On dupicate key update clause is yet to be supported");
      } else {
         this.withString = null;
         this.isolationLevel = null;
         this.insertClause.toSnowflake(this);
         if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
         }

         if (this.valuesClause != null) {
            ValuesClause tempValuesClause = this.valuesClause.toSnowflake();
            this.setValuesClause(tempValuesClause);
         }

         if (this.subQuery != null) {
            this.subQuery = this.subQuery.toSnowflake();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
         }

         if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toSnowflake();
         }

         return this.toString();
      }
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

   private String singleQueryIntoMultipleQueriesForPLSQL() {
      return SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
   }

   public UserObjectContext getObjectContext() {
      return this.objectContext;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.objectContext = obj;
   }

   private ArrayList splitMultitableInsertStatement(ArrayList insertStmtList) {
      ArrayList convertedInsertStmtList = new ArrayList();

      for(int i = 0; i < insertStmtList.size(); ++i) {
         InsertQueryStatement insertQuery = (InsertQueryStatement)insertStmtList.get(i);
         SelectQueryStatement insertSubQuery = new SelectQueryStatement();
         SelectStatement insertSubQuerySelectStmt = new SelectStatement();
         insertSubQuerySelectStmt.setSelectClause("SELECT");
         Vector selectItems = new Vector();

         for(int j = 0; j < insertQuery.getValuesClause().getValuesList().size(); ++j) {
            Object obj = insertQuery.getValuesClause().getValuesList().get(j);
            if (!obj.toString().equalsIgnoreCase("(") && !obj.toString().equalsIgnoreCase(")")) {
               selectItems.add(obj);
            }
         }

         insertSubQuerySelectStmt.setSelectItemList(selectItems);
         FromClause fromClause = new FromClause();
         fromClause.setFromClause("FROM");
         FromTable fromTable = new FromTable();
         fromTable.setTableName(this.getSubQuery());
         Vector fromItems = new Vector();
         fromItems.add(fromTable);
         fromClause.setFromItemList(fromItems);
         insertSubQuery.setFromClause(fromClause);
         insertSubQuery.setSelectStatement(insertSubQuerySelectStmt);
         insertQuery.setSubQuery(insertSubQuery);
         insertQuery.setValuesClause((ValuesClause)null);
         convertedInsertStmtList.add(insertQuery);
      }

      return convertedInsertStmtList;
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

   static {
      try {
         FileInputStream fis = new FileInputStream("conf/TablesHavingRowIdColumns.conf");
         InputStreamReader isr = new InputStreamReader(fis);
         BufferedReader br = new BufferedReader(isr);
         new String();
         String functionString = br.readLine();

         label60:
         while(true) {
            while(true) {
               while(functionString != null) {
                  if (!functionString.trim().equals("") && !functionString.trim().startsWith("--")) {
                     StringTokenizer st;
                     if (functionString.trim().indexOf("--") != -1) {
                        functionString = functionString.trim().substring(0, functionString.trim().indexOf("--"));
                        st = new StringTokenizer(functionString.trim());
                        if (st.countTokens() == 1) {
                           tablesWithRowIDColumnsList.add(st.nextToken());
                        }
                     } else if (functionString.trim().indexOf("/*") == -1) {
                        tablesWithRowIDColumnsList.add(functionString.toLowerCase());
                        functionString = br.readLine();
                     } else {
                        if (!functionString.trim().startsWith("/*")) {
                           functionString = functionString.trim().substring(0, functionString.trim().indexOf("/*"));
                           st = new StringTokenizer(functionString.trim());
                           if (st.countTokens() == 1) {
                              tablesWithRowIDColumnsList.add(st.nextToken());
                           }
                        }

                        while(functionString != null && functionString.indexOf("*/") == -1) {
                           functionString = br.readLine();
                        }

                        if (functionString != null && functionString.trim().length() - 2 != functionString.trim().indexOf("*/")) {
                           functionString = functionString.trim().substring(functionString.trim().indexOf("*/") + 2);
                           tablesWithRowIDColumnsList.add(functionString.toLowerCase());
                        }
                     }
                  } else {
                     functionString = br.readLine();
                  }
               }

               br.close();
               isr.close();
               fis.close();
               break label60;
            }
         }
      } catch (Exception var5) {
      }

   }
}
