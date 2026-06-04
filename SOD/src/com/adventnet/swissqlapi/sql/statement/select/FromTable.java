package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.parser.Token;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.insert.CommonTableExpression;
import com.adventnet.swissqlapi.sql.statement.insert.ValuesClause;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class FromTable {
   protected Object tableName;
   private String aliasName;
   private String joinClause;
   private String tableKeyword;
   private Vector joinExpression;
   private Vector UsingList;
   private String onOrUsingJoin;
   private String outer;
   private String outerOpenBrace;
   private String outerClosedBrace;
   private boolean isAS;
   private SetOperatorClause setOperatorClauseForFullJoin;
   private String updateLock;
   private String holdLock;
   private UserObjectContext context = null;
   private boolean isTenroxRequirement = false;
   private String fromClauseOpenBraces;
   private String fromClauseClosedBraces;
   private FromClause fc;
   private String lock;
   private String with;
   private String lockTableStatement;
   private String indexHint;
   private String origTableName;
   private ArrayList setOperatorClauseListForSubQuery = new ArrayList();
   private CommentClass commentObj;
   private CommentClass commentObjAfterToken;
   private CommentClass commentobjAfterAsKeyword;
   private CommentClass commentobjAfterAliasName;
   private QueryPartitionClause queryPartitionClause = null;
   private FromTable crossJoinForPartitionClause = null;
   private SelectQueryStatement crossJoinSelectQuery = null;
   private WhereExpression crossJoinExpression = null;
   private ArrayList columnAliasList;
   private ArrayList rowValuesList = new ArrayList();
   private PivotClause pivotClause = null;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setIndexHint(String s) {
      this.indexHint = s;
   }

   public void setSetOperatorClause(SetOperatorClause setOperatorClauseForFullJoin) {
      this.setOperatorClauseForFullJoin = setOperatorClauseForFullJoin;
   }

   public void setTableName(Object tn) {
      this.tableName = tn;
   }

   public void setAliasName(String an) {
      this.aliasName = an;
   }

   public void setIsAS(boolean is) {
      this.isAS = is;
   }

   public void setJoinClause(String jc) {
      this.joinClause = jc;
   }

   public void setOnOrUsingJoin(String s_onou) {
      this.onOrUsingJoin = s_onou;
   }

   public void setJoinExpression(Vector v_je) {
      this.joinExpression = v_je;
   }

   public void setUsingList(Vector v_ul) {
      this.UsingList = v_ul;
   }

   public void setLockTableStatement(String lockTableStatement) {
      this.lockTableStatement = lockTableStatement;
   }

   public void setWith(String with) {
      this.with = with;
   }

   public void setLock(String lock) {
      this.lock = lock;
   }

   public void setTableKeyword(String s_tk) {
      this.tableKeyword = s_tk;
   }

   public void setOuter(String outer) {
      this.outer = outer;
   }

   public void setOuterOpenBrace(String outerOpenBrace) {
      this.outerOpenBrace = outerOpenBrace;
   }

   public void setOuterClosedBrace(String outerClosedBrace) {
      this.outerClosedBrace = outerClosedBrace;
   }

   public void setUpdateLock(String updateLock) {
      this.updateLock = updateLock;
   }

   public void setHoldLock(String lock) {
      this.holdLock = lock;
   }

   public void setFromClauseOpenBraces(String fromClauseOpenBraces) {
      this.fromClauseOpenBraces = fromClauseOpenBraces;
   }

   public void setFromClauseClosedBraces(String fromClauseClosedBraces) {
      this.fromClauseClosedBraces = fromClauseClosedBraces;
   }

   public void setFromClause(FromClause fc) {
      this.fc = fc;
   }

   public void setColumnAliasList(ArrayList columnAliasList) {
      this.columnAliasList = columnAliasList;
   }

   public void setOrigTableName(String origTableName) {
      this.origTableName = origTableName;
   }

   public void setSetOperatorClauseListForSubQuery(ArrayList socList) {
      this.setOperatorClauseListForSubQuery = socList;
   }

   public void setQueryPartitionClause(QueryPartitionClause qpc) {
      this.queryPartitionClause = qpc;
   }

   public void setCrossJoinForPartitionClause(FromTable crossJoinForPartitionClause) {
      this.crossJoinForPartitionClause = crossJoinForPartitionClause;
   }

   public void setCrossJoinSelectQuery(SelectQueryStatement crossJoinSelectQuery) {
      this.crossJoinSelectQuery = crossJoinSelectQuery;
   }

   public void setCrossJoinExpression(WhereExpression crossJoinExpression) {
      this.crossJoinExpression = crossJoinExpression;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public void setCommentClassAfterToken(CommentClass commentObj) {
      this.commentObjAfterToken = commentObj;
   }

   public void setCommentobjAfterAsKeyword(CommentClass commentObj) {
      this.commentobjAfterAsKeyword = commentObj;
   }

   public void setCommentobjAfterAliasName(CommentClass commentObj) {
      this.commentobjAfterAliasName = commentObj;
   }

   public void setPivotClause(PivotClause pc) {
      this.pivotClause = pc;
   }

   public Object getTableName() {
      return this.tableName;
   }

   public String getOrigTableName() {
      return this.origTableName;
   }

   public String getLockTableStatement() {
      return this.lockTableStatement;
   }

   public String getWith() {
      return this.with;
   }

   public String getLock() {
      return this.lock;
   }

   public String getTableKeyword() {
      return this.tableKeyword;
   }

   public String getAliasName() {
      return this.aliasName;
   }

   public boolean getIsAS() {
      return this.isAS;
   }

   public String getJoinClause() {
      return this.joinClause;
   }

   public Vector getJoinExpression() {
      return this.joinExpression;
   }

   public String getOuter() {
      return this.outer;
   }

   public String getOuterOpenBrace() {
      return this.outerOpenBrace;
   }

   public String getOuterClosedBrace() {
      return this.outerClosedBrace;
   }

   public String getFromClauseOpenBraces() {
      return this.fromClauseOpenBraces;
   }

   public String getUpdateLock() {
      return this.updateLock;
   }

   public String getFromClauseClosedBraces() {
      return this.fromClauseClosedBraces;
   }

   public FromClause getFromClause() {
      return this.fc;
   }

   public String getOnOrUsingJoin() {
      return this.onOrUsingJoin;
   }

   public Vector getUsingList() {
      return this.UsingList;
   }

   public ArrayList getSetOperatorClauseListForSubQuery() {
      return this.setOperatorClauseListForSubQuery;
   }

   public QueryPartitionClause getQueryPartitionClause() {
      return this.queryPartitionClause;
   }

   public FromTable getCrossJoinForPartitionClause() {
      return this.crossJoinForPartitionClause;
   }

   public SelectQueryStatement getCrossJoinSelectQuery() {
      return this.crossJoinSelectQuery;
   }

   public WhereExpression getCrossJoinExpression() {
      return this.crossJoinExpression;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public CommentClass getCommentClassAfterToken() {
      return this.commentObjAfterToken;
   }

   public CommentClass getCommentobjAfterAsKeyword() {
      return this.commentobjAfterAsKeyword;
   }

   public CommentClass getCommentobjAfterAliasName() {
      return this.commentobjAfterAliasName;
   }

   public ArrayList getColumnAliasList() {
      return this.columnAliasList;
   }

   public PivotClause getPivotClause() {
      return this.pivotClause;
   }

   public void addCommentClassAfterToken(Token commentObj) {
      this.addCommentObject(this.commentObjAfterToken, commentObj, 1);
   }

   public void addCommentobjAfterAsKeyword(Token commentObj) {
      this.addCommentObject(this.commentobjAfterAsKeyword, commentObj, 2);
   }

   public void addCommentobjAfterAliasName(Token commentObj) {
      this.addCommentObject(this.commentobjAfterAliasName, commentObj, 3);
   }

   public void addCommentObject(CommentClass obj, Token commentObj, int i) {
      if (commentObj != null) {
         Token test = commentObj.specialToken;
         ArrayList specialTokenList;
         if (obj != null) {
            specialTokenList = obj.getSpecialToken();

            for(int lastIndex = specialTokenList.size(); test != null; test = test.specialToken) {
               specialTokenList.add(lastIndex, test.image);
            }
         } else if (test != null) {
            for(specialTokenList = new ArrayList(); test != null; test = test.specialToken) {
               specialTokenList.add(0, test.image);
            }

            CommentClass commentObjToBeInserted = new CommentClass();
            commentObjToBeInserted.setSpecialToken(specialTokenList);
            if (i == 1) {
               this.commentObjAfterToken = commentObjToBeInserted;
            } else if (i == 2) {
               this.commentobjAfterAsKeyword = commentObjToBeInserted;
            } else if (i == 3) {
               this.commentobjAfterAliasName = commentObjToBeInserted;
            }
         }

         commentObj.specialToken = null;
      }

   }

   public FromTable convert(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, int database) throws ConvertException {
      if (database == 8) {
         return this.toANSISelect(to_sqs, from_sqs);
      } else if (database == 3) {
         return this.toDB2Select(to_sqs, from_sqs);
      } else if (database == 2) {
         return this.toMSSQLServerSelect(to_sqs, from_sqs);
      } else if (database == 7) {
         return this.toSybaseSelect(to_sqs, from_sqs);
      } else if (database == 5) {
         return this.toMySQLSelect(to_sqs, from_sqs);
      } else if (database == 4) {
         return this.toPostgreSQLSelect(to_sqs, from_sqs);
      } else if (database == 6) {
         return this.toInformixSelect(to_sqs, from_sqs);
      } else if (database == 1) {
         return this.toOracleSelect(to_sqs, from_sqs);
      } else if (database == 15) {
         return this.toSnowflakeSelect(to_sqs, from_sqs);
      } else if (database == 11) {
         return this.toNetezzaSelect(to_sqs, from_sqs);
      } else if (database == 12) {
         return this.toTeradataSelect(to_sqs, from_sqs);
      } else if (database == 13) {
         return this.toVectorWiseSelect(to_sqs, from_sqs);
      } else if (database == 14) {
         return this.toBigQuerySelect(to_sqs, from_sqs);
      } else if (database == 16) {
         return this.toAthenaSelect(to_sqs, from_sqs);
      } else if (database == 17) {
         return this.toSapHanaSelect(to_sqs, from_sqs);
      } else if (database == 18) {
         return this.toSqliteSelect(to_sqs, from_sqs);
      } else if (database == 20) {
         return this.toExcelSelect(to_sqs, from_sqs);
      } else {
         return database == 21 ? this.toMsAccessJdbcSelect(to_sqs, from_sqs) : null;
      }
   }

   public FromTable toMySQLSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass(this.commentObj);
      boolean isFullJoin = false;
      Vector tokenVector;
      int i;
      int i;
      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
            throw new ConvertException("Conversion failure..Natural join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
            throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
            throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
            throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
            throw new ConvertException("Conversion failure..Natural right join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         if (this.joinClause.trim().equalsIgnoreCase("OUTER")) {
            ft.setJoinClause("OUTER JOIN");
         } else if (vendorSQS.getBooleanValues("allow.multiple.full.join")) {
            this.getFullJoinCount(vendorSQS, vendorSQS.getIntegerValues("full.join.count"));
         } else if (!vendorSQS.getBooleanValues("allow.multiple.full.join") && (this.joinClause.trim().equalsIgnoreCase("FULL JOIN") || this.joinClause.trim().equalsIgnoreCase("FULL OUTER JOIN"))) {
            FromClause fc = new FromClause();
            FromTable newFromTable = new FromTable();
            newFromTable.setAliasName(this.aliasName);
            newFromTable.setIsAS(this.isAS);
            newFromTable.setJoinClause("RIGHT OUTER JOIN");
            newFromTable.setJoinExpression(this.joinExpression);
            newFromTable.setOnOrUsingJoin(this.onOrUsingJoin);
            newFromTable.setTableKeyword(this.tableKeyword);
            newFromTable.setTableName(this.tableName);
            newFromTable.setUsingList(this.UsingList);
            if (vendorSQS.getFromClause() != null) {
               fc.setFromClause("FROM");
               tokenVector = vendorSQS.getFromClause().getFromItemList();
               Vector newFromList = new Vector();
               int fullJoinCount = 0;

               for(i = 0; i < tokenVector.size(); ++i) {
                  Object fromObj = tokenVector.get(i);
                  if (fromObj instanceof FromTable) {
                     FromTable newFt = (FromTable)((FromTable)fromObj).clone();
                     if (newFt.getJoinClause() != null && (newFt.getJoinClause().equalsIgnoreCase("FULL JOIN") || newFt.getJoinClause().equalsIgnoreCase("FULL OUTER JOIN"))) {
                        ++fullJoinCount;
                        newFt.setJoinClause("RIGHT OUTER JOIN");
                     }

                     newFromList.add(newFt);
                  } else if (fromObj instanceof FromClause) {
                     FromClause newFc = (FromClause)((FromClause)fromObj).clone();
                     Vector newFromItemList = newFc.getFromItemList();

                     for(int index = 0; index < newFromItemList.size(); ++index) {
                        if (newFromItemList.elementAt(i) instanceof FromTable) {
                           FromTable newFt = (FromTable)newFromItemList.get(i);
                           if (newFt.getJoinClause() != null && (newFt.getJoinClause().equalsIgnoreCase("FULL JOIN") || newFt.getJoinClause().equalsIgnoreCase("FULL OUTER JOIN"))) {
                              ++fullJoinCount;
                              newFt.setJoinClause("RIGHT OUTER JOIN");
                           }
                        }
                     }

                     newFromList.add(newFc);
                  }
               }

               if (fullJoinCount > 1) {
                  throw new ConvertException("Only One Full Join operator is allowed.", "ONE_FULL_JOIN_ALLOWED");
               }

               fc.setFromItemList(newFromList);
            }

            SelectQueryStatement sqs = new SelectQueryStatement();
            sqs.setFromClause(fc.toMySQLSelect(vembuSQS, vendorSQS));
            if (vendorSQS.getSelectStatement() != null) {
               sqs.setSelectStatement(vendorSQS.getSelectStatement().toMySQLSelect(vembuSQS, vendorSQS));
            }

            if (vendorSQS.getFetchClause() != null) {
               sqs.setFetchClause(vendorSQS.getFetchClause().toMySQLSelect(vembuSQS, vendorSQS));
            }

            if (vendorSQS.getForUpdateStatement() != null) {
               sqs.setForUpdateStatement((ForUpdateStatement)null);
            }

            if (vendorSQS.getGroupByStatement() != null) {
               sqs.setGroupByStatement(vendorSQS.getGroupByStatement().toMySQLSelect(vembuSQS, vendorSQS));
            }

            if (vendorSQS.getHavingStatement() != null) {
               sqs.setHavingStatement(vendorSQS.getHavingStatement().toMySQLSelect(vembuSQS, vendorSQS));
            }

            if (vendorSQS.getHierarchicalQueryClause() != null) {
               sqs.setHierarchicalQueryClause((HierarchicalQueryClause)null);
            }

            if (vendorSQS.getSetOperatorClause() != null) {
               sqs.setSetOperatorClause(vendorSQS.getSetOperatorClause().toMySQLSelect(vembuSQS, vendorSQS));
            }

            vendorSQS.setSetOperatorClause((SetOperatorClause)null);
            if (vendorSQS.getWhereExpression() != null) {
               sqs.setWhereExpression(vendorSQS.getWhereExpression().toMySQLSelect(vembuSQS, vendorSQS));
            }

            this.setOperatorClauseForFullJoin = new SetOperatorClause();
            this.setOperatorClauseForFullJoin.setSelectQueryStatement(sqs);
            this.setOperatorClauseForFullJoin.setSetClause("UNION");
            vembuSQS.setSetOperatorClause(this.setOperatorClauseForFullJoin);
            isFullJoin = true;
         }

         if (this.onOrUsingJoin != null && this.onOrUsingJoin.toUpperCase().equals("USING")) {
            throw new ConvertException("The USING keyword is not supported currently. Please use the ON condition in your query instead.", "USING_KEYWORD_NOT_SUPPORTED");
         }

         WhereExpression we;
         Vector operatorList;
         if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            we = ((WhereExpression)this.joinExpression.elementAt(0)).toMySQLSelect(vembuSQS, vendorSQS);
            operatorList = new Vector();
            operatorList.addElement(we);
            ft.setJoinExpression(operatorList);
         } else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            we = (WhereExpression)this.joinExpression.elementAt(0);
            operatorList = we.getOperator();
            we.setOperator(operatorList);
            we = we.toMySQLSelect(vembuSQS, vendorSQS);
            tokenVector = new Vector();
            tokenVector.addElement(we);
            ft.setJoinExpression(tokenVector);
         }

         ft.setOnOrUsingJoin(this.onOrUsingJoin);
         if (this.UsingList != null) {
            Vector v = new Vector();

            for(i = 0; i < this.UsingList.size(); ++i) {
               if (this.UsingList.elementAt(i) instanceof TableColumn) {
                  v.addElement(((TableColumn)this.UsingList.elementAt(i)).toMySQLSelect(vembuSQS, vendorSQS));
               } else {
                  v.addElement(this.UsingList.elementAt(i));
               }
            }

            ft.setUsingList(v);
         }

         if (!vendorSQS.getBooleanValues("allow.multiple.full.join") && isFullJoin) {
            ft.setJoinClause("LEFT OUTER JOIN");
         } else {
            ft.setJoinClause(this.joinClause);
         }

         if (this.joinClause.trim().equalsIgnoreCase("JOIN")) {
            ft.setJoinClause("INNER JOIN");
         }
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      String tempTableName;
      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            SelectQueryStatement subQryStmt = (SelectQueryStatement)this.tableName;
            subQryStmt.setMySqlLiveFlag(vendorSQS.isMySqlLive());
            subQryStmt.setMongoDbFlag(vendorSQS.isMongoDb());
            subQryStmt.setSQLDialect(vendorSQS.getSQLDialect());
            subQryStmt.setValidationHandler(vendorSQS.getValidationHandler());
            subQryStmt.setQueryConvDataHandler(vendorSQS.getQueryConvDataHandler());
            subQryStmt.setQueryConversionPropHandler(vendorSQS.getQueryConvPropHandler());
            subQryStmt.setIsQtNewFlow(vembuSQS.getIsQtNewFlow());
            subQryStmt.setInstanceDataTypeHandler(vendorSQS.getinstanceDataTypeHandler());
            ft.setTableName(subQryStmt.toMySQL());
            if (this.aliasName == null && this.pivotClause == null && this.setOperatorClauseListForSubQuery.size() == 0) {
               ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
            }
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException();
            }

            if (this.tableName instanceof WithStatement) {
               ft.setTableName(((WithStatement)this.tableName).toMySQL());
            } else if (this.tableName instanceof FromClause) {
               if (this.fromClauseOpenBraces != null) {
                  ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
               }

               ft.setTableName(((FromClause)this.tableName).toMySQLSelect(vembuSQS, vendorSQS));
               if (this.fromClauseClosedBraces != null) {
                  ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
               }
            } else {
               if (vendorSQS != null && vendorSQS.getBooleanValues("replace.double.dots.in.table.name")) {
                  tempTableName = (String)this.tableName;
                  i = tempTableName.indexOf("@");
                  ft.setTableName(StringFunctions.replaceFirst(".", "..", (String)this.tableName));
                  tokenVector = new Vector();
                  String table_Name = (String)this.tableName;
                  StringTokenizer st = new StringTokenizer(table_Name, ".");

                  for(i = 0; st.hasMoreTokens(); ++i) {
                     tokenVector.add(st.nextToken());
                  }

                  String dataBaseTableName;
                  String sqlTableName;
                  String sqlDataBaseName;
                  if (i == 1 && i != -1) {
                     dataBaseTableName = (String)tokenVector.elementAt(0);
                     String sqlTableName = dataBaseTableName.substring(0, i);
                     sqlTableName = dataBaseTableName.substring(i + 1);
                     sqlDataBaseName = sqlTableName + "." + sqlTableName;
                     ft.setTableName(sqlDataBaseName);
                  } else if (i == 2 && i != -1) {
                     dataBaseTableName = (String)tokenVector.elementAt(1);
                     int tableAtIndex = dataBaseTableName.indexOf("@");
                     sqlTableName = dataBaseTableName.substring(0, tableAtIndex);
                     sqlDataBaseName = dataBaseTableName.substring(tableAtIndex + 1);
                     String sqlDataBaseAndTableName = sqlDataBaseName + "." + (String)tokenVector.elementAt(0) + "." + sqlTableName;
                     ft.setTableName(sqlDataBaseAndTableName);
                  }
               } else {
                  ft.setTableName((String)this.tableName);
               }

               if (vendorSQS != null && !vendorSQS.isHyperSql()) {
                  tempTableName = (String)ft.getTableName();
                  if (tempTableName.startsWith("\"") && tempTableName.endsWith("\"") || tempTableName.startsWith("[") && tempTableName.endsWith("]")) {
                     tempTableName = tempTableName.substring(1, tempTableName.length() - 1);
                     tempTableName = "`" + tempTableName + "`";
                     ft.setTableName(tempTableName);
                  }
               }
            }
         }

         if (this.setOperatorClauseListForSubQuery.size() != 0) {
            ArrayList setOperatorClauseList = new ArrayList();

            for(i = 0; i < this.setOperatorClauseListForSubQuery.size(); ++i) {
               setOperatorClauseList.add(((SetOperatorClause)this.setOperatorClauseListForSubQuery.get(i)).toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)this.tableName));
            }

            ft.setSetOperatorClauseListForSubQuery(setOperatorClauseList);
         }
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         tempTableName = this.aliasName;
         if (GeneralUtil.isItEnclosedAliasName(this.aliasName)) {
            tempTableName = tempTableName.substring(1, tempTableName.length() - 1).trim();
            tempTableName = tempTableName.replace("`", "``");
            tempTableName = "`" + tempTableName + "`";
            ft.setAliasName(tempTableName);
         } else {
            ft.setAliasName(this.aliasName);
         }
      }

      if (this.pivotClause != null) {
         this.pivotClause.toMySQLSelect(vembuSQS, vendorSQS, ft);
      }

      return ft;
   }

   public FromTable toBigQuerySelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass((CommentClass)null);
      int startIndex;
      int atIndex;
      Vector tokenVector;
      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         WhereExpression we;
         Vector operatorList;
         if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            we = ((WhereExpression)this.joinExpression.elementAt(0)).toBigQuerySelect(vembuSQS, vendorSQS);
            operatorList = new Vector();
            operatorList.addElement(we);
            ft.setJoinExpression(operatorList);
         } else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            we = (WhereExpression)this.joinExpression.elementAt(0);
            operatorList = we.getOperator();

            for(startIndex = 0; startIndex < operatorList.size(); ++startIndex) {
               if (operatorList.elementAt(startIndex) instanceof String && ((String)operatorList.get(startIndex)).equalsIgnoreCase("AND")) {
                  operatorList.setElementAt("AND", startIndex);
               }
            }

            we.setOperator(operatorList);
            we = we.toBigQuerySelect(vembuSQS, vendorSQS);
            tokenVector = new Vector();
            tokenVector.addElement(we);
            ft.setJoinExpression(tokenVector);
         }

         ft.setOnOrUsingJoin(this.onOrUsingJoin);
         if (this.UsingList != null) {
            Vector v = new Vector();

            for(atIndex = 0; atIndex < this.UsingList.size(); ++atIndex) {
               if (this.UsingList.elementAt(atIndex) instanceof TableColumn) {
                  v.addElement(((TableColumn)this.UsingList.elementAt(atIndex)).toBigQuerySelect(vembuSQS, vendorSQS));
               } else {
                  v.addElement(this.UsingList.elementAt(atIndex));
               }
            }

            ft.setUsingList(v);
         }

         this.removeJoinExpressionForCrossJoin(ft);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      String tempTableName;
      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            ((SelectQueryStatement)this.tableName).setReportsMeta(vendorSQS == null ? false : vendorSQS.getReportsMeta());
            ((SelectQueryStatement)this.tableName).setBigQueryFlag(vendorSQS.isBigQueryLive());
            ((SelectQueryStatement)this.tableName).setQueryConversionPropHandler(vendorSQS.getQueryConvPropHandler());
            ft.setTableName(((SelectQueryStatement)this.tableName).toBigQuery());
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException("Function calls in the place of Table name yet to be supported");
            }

            if (this.tableName instanceof WithStatement) {
               ft.setTableName(((WithStatement)this.tableName).toBigQuery());
            } else if (this.tableName instanceof FromClause) {
               ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
               ft.setTableName(((FromClause)this.tableName).toBigQuerySelect(vembuSQS, vendorSQS));
               ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
            } else {
               if (((String)this.tableName).indexOf("[") != 0) {
                  ft.setTableName(this.tableName);
               } else {
                  tempTableName = (String)this.tableName;
                  String tableNameSubString = "";
                  startIndex = tempTableName.indexOf("[");
                  if (startIndex != -1) {
                     while(startIndex != -1) {
                        if (startIndex == 0) {
                           tableNameSubString = tempTableName.substring(1);
                           tableNameSubString = "\"" + tableNameSubString;
                           tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                           tempTableName = tableNameSubString;
                           startIndex = tableNameSubString.indexOf("[");
                        } else {
                           tableNameSubString = tempTableName.substring(0, startIndex);
                           tableNameSubString = tableNameSubString + "\"";
                           tableNameSubString = tableNameSubString + tempTableName.substring(startIndex + 1);
                           tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                           tempTableName = tableNameSubString;
                           startIndex = tableNameSubString.indexOf("[");
                        }
                     }

                     ft.setTableName(tableNameSubString);
                  }
               }

               tempTableName = (String)this.tableName;
               atIndex = tempTableName.indexOf("@");
               tokenVector = new Vector();
               String table_Name = (String)this.tableName;
               StringTokenizer st = new StringTokenizer(table_Name, ".");

               int count;
               for(count = 0; st.hasMoreTokens(); ++count) {
                  tokenVector.add(st.nextToken());
               }

               String tempTableName;
               String tableNameSubString;
               if (count == 1 && atIndex != -1) {
                  tempTableName = (String)tokenVector.elementAt(0);
                  tableNameSubString = tempTableName.substring(0, atIndex);
                  tempTableName.substring(atIndex + 1);
                  ft.setTableName(tableNameSubString);
               }

               tempTableName = (String)ft.getTableName();
               tableNameSubString = "";
               int startIndex = tempTableName.indexOf("[");
               if (startIndex != -1) {
                  if (startIndex == 0) {
                     tableNameSubString = tempTableName.substring(1);
                     tableNameSubString = "\"" + tableNameSubString;
                     tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                     tempTableName = tableNameSubString;
                  }

                  ft.setTableName(tempTableName);
               }

               String tblName = (String)ft.getTableName();
               ft.setTableName(tblName);
               tblName = (String)ft.getTableName();
               ft.setTableName(tblName);
            }
         }
      }

      if (this.setOperatorClauseListForSubQuery.size() != 0) {
         ArrayList setOperatorClauseList = new ArrayList();

         for(atIndex = 0; atIndex < this.setOperatorClauseListForSubQuery.size(); ++atIndex) {
            setOperatorClauseList.add(((SetOperatorClause)this.setOperatorClauseListForSubQuery.get(atIndex)).toBigQuerySelect(vembuSQS, vendorSQS));
         }

         ft.setSetOperatorClauseListForSubQuery(setOperatorClauseList);
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         tempTableName = this.aliasName;
         if ((!tempTableName.startsWith("\"") || !tempTableName.endsWith("\"")) && (!tempTableName.startsWith("'") || !tempTableName.endsWith("'"))) {
            ft.setAliasName(this.aliasName);
         } else {
            tempTableName = tempTableName.substring(1, tempTableName.length() - 1).trim();
            tempTableName = tempTableName.replace("`", "``");
            tempTableName = "`" + tempTableName + "`";
            ft.setAliasName(tempTableName);
         }
      } else if (this.tableName instanceof SelectQueryStatement && this.setOperatorClauseListForSubQuery.size() == 0) {
         SelectQueryStatement sqs = (SelectQueryStatement)this.tableName;
         if (sqs.getSetOperatorClause() != null) {
            SetOperatorClause soc = sqs.getSetOperatorClause();
            if (soc == null || soc.getSetClause() == null || !soc.getSetClause().toUpperCase().startsWith("UNION") && !soc.getSetClause().toUpperCase().startsWith("MINUS") && !soc.getSetClause().toUpperCase().startsWith("INTERSECT") && !soc.getSetClause().toUpperCase().startsWith("EXCEPT")) {
               ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
            }
         } else {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
         }
      }

      if (this.joinClause != null && this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
         ft.setOnOrUsingJoin((String)null);
      }

      ft.setJoinClause(this.joinClause);
      return ft;
   }

   public FromTable toAthenaSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass((CommentClass)null);
      int startIndex;
      int atIndex;
      Vector tokenVector;
      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         WhereExpression we;
         Vector operatorList;
         if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            we = ((WhereExpression)this.joinExpression.elementAt(0)).toAthenaSelect(vembuSQS, vendorSQS);
            operatorList = new Vector();
            operatorList.addElement(we);
            ft.setJoinExpression(operatorList);
         } else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            we = (WhereExpression)this.joinExpression.elementAt(0);
            operatorList = we.getOperator();

            for(startIndex = 0; startIndex < operatorList.size(); ++startIndex) {
               if (operatorList.elementAt(startIndex) instanceof String && ((String)operatorList.get(startIndex)).equalsIgnoreCase("AND")) {
                  operatorList.setElementAt("AND", startIndex);
               }
            }

            we.setOperator(operatorList);
            we = we.toAthenaSelect(vembuSQS, vendorSQS);
            tokenVector = new Vector();
            tokenVector.addElement(we);
            ft.setJoinExpression(tokenVector);
         }

         ft.setOnOrUsingJoin(this.onOrUsingJoin);
         if (this.UsingList != null) {
            Vector v = new Vector();

            for(atIndex = 0; atIndex < this.UsingList.size(); ++atIndex) {
               if (this.UsingList.elementAt(atIndex) instanceof TableColumn) {
                  v.addElement(((TableColumn)this.UsingList.elementAt(atIndex)).toAthenaSelect(vembuSQS, vendorSQS));
               } else {
                  v.addElement(this.UsingList.elementAt(atIndex));
               }
            }

            ft.setUsingList(v);
         }

         this.removeJoinExpressionForCrossJoin(ft);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      String tempTableName;
      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            ((SelectQueryStatement)this.tableName).setReportsMeta(vendorSQS == null ? false : vendorSQS.getReportsMeta());
            ((SelectQueryStatement)this.tableName).setAthenaFlag(vendorSQS.isAthena());
            ((SelectQueryStatement)this.tableName).setQueryConversionPropHandler(vendorSQS.getQueryConvPropHandler());
            ft.setTableName(((SelectQueryStatement)this.tableName).toAthena());
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException("Function calls in the place of Table name yet to be supported");
            }

            if (this.tableName instanceof WithStatement) {
               ft.setTableName(((WithStatement)this.tableName).toAthena());
            } else if (this.tableName instanceof FromClause) {
               ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
               ft.setTableName(((FromClause)this.tableName).toAthenaSelect(vembuSQS, vendorSQS));
               ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
            } else {
               if (((String)this.tableName).indexOf("[") != 0) {
                  ft.setTableName(this.tableName);
               } else {
                  tempTableName = (String)this.tableName;
                  String tableNameSubString = "";
                  startIndex = tempTableName.indexOf("[");
                  if (startIndex != -1) {
                     while(startIndex != -1) {
                        if (startIndex == 0) {
                           tableNameSubString = tempTableName.substring(1);
                           tableNameSubString = "\"" + tableNameSubString;
                           tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                           tempTableName = tableNameSubString;
                           startIndex = tableNameSubString.indexOf("[");
                        } else {
                           tableNameSubString = tempTableName.substring(0, startIndex);
                           tableNameSubString = tableNameSubString + "\"";
                           tableNameSubString = tableNameSubString + tempTableName.substring(startIndex + 1);
                           tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                           tempTableName = tableNameSubString;
                           startIndex = tableNameSubString.indexOf("[");
                        }
                     }

                     ft.setTableName(tableNameSubString);
                  }
               }

               tempTableName = (String)this.tableName;
               atIndex = tempTableName.indexOf("@");
               tokenVector = new Vector();
               String table_Name = (String)this.tableName;
               StringTokenizer st = new StringTokenizer(table_Name, ".");

               int count;
               for(count = 0; st.hasMoreTokens(); ++count) {
                  tokenVector.add(st.nextToken());
               }

               String tempTableName;
               String tableNameSubString;
               if (count == 1 && atIndex != -1) {
                  tempTableName = (String)tokenVector.elementAt(0);
                  tableNameSubString = tempTableName.substring(0, atIndex);
                  tempTableName.substring(atIndex + 1);
                  ft.setTableName(tableNameSubString);
               }

               tempTableName = (String)ft.getTableName();
               tableNameSubString = "";
               int startIndex = tempTableName.indexOf("[");
               if (startIndex != -1) {
                  if (startIndex == 0) {
                     tableNameSubString = tempTableName.substring(1);
                     tableNameSubString = "\"" + tableNameSubString;
                     tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                     tempTableName = tableNameSubString;
                  }

                  ft.setTableName(tempTableName);
               }

               String tblName = (String)ft.getTableName();
               ft.setTableName(tblName);
               tblName = (String)ft.getTableName();
               ft.setTableName(tblName);
            }
         }
      }

      if (this.setOperatorClauseListForSubQuery.size() != 0) {
         ArrayList setOperatorClauseList = new ArrayList();

         for(atIndex = 0; atIndex < this.setOperatorClauseListForSubQuery.size(); ++atIndex) {
            setOperatorClauseList.add(((SetOperatorClause)this.setOperatorClauseListForSubQuery.get(atIndex)).toAthenaSelect(vembuSQS, vendorSQS));
         }

         ft.setSetOperatorClauseListForSubQuery(setOperatorClauseList);
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         tempTableName = this.aliasName;
         if ((!tempTableName.startsWith("\"") || !tempTableName.endsWith("\"")) && (!tempTableName.startsWith("'") || !tempTableName.endsWith("'"))) {
            ft.setAliasName(this.aliasName);
         } else {
            tempTableName = tempTableName.substring(1, tempTableName.length() - 1).trim();
            tempTableName = tempTableName.replace("`", "``");
            tempTableName = "`" + tempTableName + "`";
            ft.setAliasName(tempTableName);
         }
      } else if (this.tableName instanceof SelectQueryStatement && this.setOperatorClauseListForSubQuery.size() == 0) {
         SelectQueryStatement sqs = (SelectQueryStatement)this.tableName;
         if (sqs.getSetOperatorClause() != null) {
            SetOperatorClause soc = sqs.getSetOperatorClause();
            if (soc == null || soc.getSetClause() == null || !soc.getSetClause().toUpperCase().startsWith("UNION") && !soc.getSetClause().toUpperCase().startsWith("MINUS") && !soc.getSetClause().toUpperCase().startsWith("INTERSECT") && !soc.getSetClause().toUpperCase().startsWith("EXCEPT")) {
               ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
            }
         } else {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
         }
      }

      if (this.joinClause != null && this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
         ft.setOnOrUsingJoin((String)null);
      }

      ft.setJoinClause(this.joinClause);
      return ft;
   }

   public FromTable toSapHanaSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass((CommentClass)null);
      int startIndex;
      int atIndex;
      Vector tokenVector;
      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         WhereExpression we;
         Vector operatorList;
         if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            we = ((WhereExpression)this.joinExpression.elementAt(0)).toSapHanaSelect(vembuSQS, vendorSQS);
            operatorList = new Vector();
            operatorList.addElement(we);
            ft.setJoinExpression(operatorList);
         } else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            we = (WhereExpression)this.joinExpression.elementAt(0);
            operatorList = we.getOperator();

            for(startIndex = 0; startIndex < operatorList.size(); ++startIndex) {
               if (operatorList.elementAt(startIndex) instanceof String && ((String)operatorList.get(startIndex)).equalsIgnoreCase("AND")) {
                  operatorList.setElementAt("AND", startIndex);
               }
            }

            we.setOperator(operatorList);
            we = we.toSapHanaSelect(vembuSQS, vendorSQS);
            tokenVector = new Vector();
            tokenVector.addElement(we);
            ft.setJoinExpression(tokenVector);
         }

         ft.setOnOrUsingJoin(this.onOrUsingJoin);
         if (this.UsingList != null) {
            Vector v = new Vector();

            for(atIndex = 0; atIndex < this.UsingList.size(); ++atIndex) {
               if (this.UsingList.elementAt(atIndex) instanceof TableColumn) {
                  v.addElement(((TableColumn)this.UsingList.elementAt(atIndex)).toSapHanaSelect(vembuSQS, vendorSQS));
               } else {
                  v.addElement(this.UsingList.elementAt(atIndex));
               }
            }

            ft.setUsingList(v);
         }

         this.removeJoinExpressionForCrossJoin(ft);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      String tempTableName;
      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            ((SelectQueryStatement)this.tableName).setReportsMeta(vendorSQS == null ? false : vendorSQS.getReportsMeta());
            ((SelectQueryStatement)this.tableName).setSapHanaFlag(vendorSQS.isSapHanaDb());
            ((SelectQueryStatement)this.tableName).setQueryConversionPropHandler(vendorSQS.getQueryConvPropHandler());
            ft.setTableName(((SelectQueryStatement)this.tableName).toSapHana());
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException("Function calls in the place of Table name yet to be supported");
            }

            if (this.tableName instanceof WithStatement) {
               ft.setTableName(((WithStatement)this.tableName).toSapHana());
            } else if (this.tableName instanceof FromClause) {
               ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
               ft.setTableName(((FromClause)this.tableName).toSapHanaSelect(vembuSQS, vendorSQS));
               ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
            } else {
               if (((String)this.tableName).indexOf("[") != 0) {
                  ft.setTableName(this.tableName);
               } else {
                  tempTableName = (String)this.tableName;
                  String tableNameSubString = "";
                  startIndex = tempTableName.indexOf("[");
                  if (startIndex != -1) {
                     while(startIndex != -1) {
                        if (startIndex == 0) {
                           tableNameSubString = tempTableName.substring(1);
                           tableNameSubString = "\"" + tableNameSubString;
                           tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                           tempTableName = tableNameSubString;
                           startIndex = tableNameSubString.indexOf("[");
                        } else {
                           tableNameSubString = tempTableName.substring(0, startIndex);
                           tableNameSubString = tableNameSubString + "\"";
                           tableNameSubString = tableNameSubString + tempTableName.substring(startIndex + 1);
                           tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                           tempTableName = tableNameSubString;
                           startIndex = tableNameSubString.indexOf("[");
                        }
                     }

                     ft.setTableName(tableNameSubString);
                  }
               }

               tempTableName = (String)this.tableName;
               atIndex = tempTableName.indexOf("@");
               tokenVector = new Vector();
               String table_Name = (String)this.tableName;
               StringTokenizer st = new StringTokenizer(table_Name, ".");

               int count;
               for(count = 0; st.hasMoreTokens(); ++count) {
                  tokenVector.add(st.nextToken());
               }

               String tempTableName;
               String tableNameSubString;
               if (count == 1 && atIndex != -1) {
                  tempTableName = (String)tokenVector.elementAt(0);
                  tableNameSubString = tempTableName.substring(0, atIndex);
                  tempTableName.substring(atIndex + 1);
                  ft.setTableName(tableNameSubString);
               }

               tempTableName = (String)ft.getTableName();
               tableNameSubString = "";
               int startIndex = tempTableName.indexOf("[");
               if (startIndex != -1) {
                  if (startIndex == 0) {
                     tableNameSubString = tempTableName.substring(1);
                     tableNameSubString = "\"" + tableNameSubString;
                     tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                     tempTableName = tableNameSubString;
                  }

                  ft.setTableName(tempTableName);
               }

               String tblName = (String)ft.getTableName();
               ft.setTableName(tblName);
               tblName = (String)ft.getTableName();
               ft.setTableName(tblName);
            }
         }
      }

      if (this.setOperatorClauseListForSubQuery.size() != 0) {
         ArrayList setOperatorClauseList = new ArrayList();

         for(atIndex = 0; atIndex < this.setOperatorClauseListForSubQuery.size(); ++atIndex) {
            setOperatorClauseList.add(((SetOperatorClause)this.setOperatorClauseListForSubQuery.get(atIndex)).toSapHanaSelect(vembuSQS, vendorSQS));
         }

         ft.setSetOperatorClauseListForSubQuery(setOperatorClauseList);
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         tempTableName = this.aliasName;
         if ((!tempTableName.startsWith("\"") || !tempTableName.endsWith("\"")) && (!tempTableName.startsWith("'") || !tempTableName.endsWith("'"))) {
            ft.setAliasName(this.aliasName);
         } else {
            tempTableName = tempTableName.substring(1, tempTableName.length() - 1).trim();
            tempTableName = tempTableName.replace("`", "``");
            tempTableName = "`" + tempTableName + "`";
            ft.setAliasName(tempTableName);
         }
      } else if (this.tableName instanceof SelectQueryStatement && this.setOperatorClauseListForSubQuery.size() == 0) {
         SelectQueryStatement sqs = (SelectQueryStatement)this.tableName;
         if (sqs.getSetOperatorClause() != null) {
            SetOperatorClause soc = sqs.getSetOperatorClause();
            if (soc == null || soc.getSetClause() == null || !soc.getSetClause().toUpperCase().startsWith("UNION") && !soc.getSetClause().toUpperCase().startsWith("MINUS") && !soc.getSetClause().toUpperCase().startsWith("INTERSECT") && !soc.getSetClause().toUpperCase().startsWith("EXCEPT")) {
               ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
            }
         } else {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
         }
      }

      if (this.joinClause != null && this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
         ft.setOnOrUsingJoin((String)null);
      }

      ft.setJoinClause(this.joinClause);
      return ft;
   }

   public FromTable toSqliteSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass((CommentClass)null);
      int startIndex;
      int atIndex;
      Vector tokenVector;
      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         WhereExpression we;
         Vector operatorList;
         if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            we = ((WhereExpression)this.joinExpression.elementAt(0)).toSqliteSelect(vembuSQS, vendorSQS);
            operatorList = new Vector();
            operatorList.addElement(we);
            ft.setJoinExpression(operatorList);
         } else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            we = (WhereExpression)this.joinExpression.elementAt(0);
            operatorList = we.getOperator();

            for(startIndex = 0; startIndex < operatorList.size(); ++startIndex) {
               if (operatorList.elementAt(startIndex) instanceof String && ((String)operatorList.get(startIndex)).equalsIgnoreCase("AND")) {
                  operatorList.setElementAt("AND", startIndex);
               }
            }

            we.setOperator(operatorList);
            we = we.toSqliteSelect(vembuSQS, vendorSQS);
            tokenVector = new Vector();
            tokenVector.addElement(we);
            ft.setJoinExpression(tokenVector);
         }

         ft.setOnOrUsingJoin(this.onOrUsingJoin);
         if (this.UsingList != null) {
            Vector v = new Vector();

            for(atIndex = 0; atIndex < this.UsingList.size(); ++atIndex) {
               if (this.UsingList.elementAt(atIndex) instanceof TableColumn) {
                  v.addElement(((TableColumn)this.UsingList.elementAt(atIndex)).toSqliteSelect(vembuSQS, vendorSQS));
               } else {
                  v.addElement(this.UsingList.elementAt(atIndex));
               }
            }

            ft.setUsingList(v);
         }

         this.removeJoinExpressionForCrossJoin(ft);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      String tempTableName;
      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            ((SelectQueryStatement)this.tableName).setReportsMeta(vendorSQS == null ? false : vendorSQS.getReportsMeta());
            ((SelectQueryStatement)this.tableName).setSqliteFlag(vendorSQS.isSqlite());
            ((SelectQueryStatement)this.tableName).setQueryConversionPropHandler(vendorSQS.getQueryConvPropHandler());
            ft.setTableName(((SelectQueryStatement)this.tableName).toSqlite());
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException("Function calls in the place of Table name yet to be supported");
            }

            if (this.tableName instanceof WithStatement) {
               ft.setTableName(((WithStatement)this.tableName).toSqlite());
            } else if (this.tableName instanceof FromClause) {
               ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
               ft.setTableName(((FromClause)this.tableName).toSqliteSelect(vembuSQS, vendorSQS));
               ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
            } else {
               if (((String)this.tableName).indexOf("[") != 0) {
                  ft.setTableName(this.tableName);
               } else {
                  tempTableName = (String)this.tableName;
                  String tableNameSubString = "";
                  startIndex = tempTableName.indexOf("[");
                  if (startIndex != -1) {
                     while(startIndex != -1) {
                        if (startIndex == 0) {
                           tableNameSubString = tempTableName.substring(1);
                           tableNameSubString = "\"" + tableNameSubString;
                           tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                           tempTableName = tableNameSubString;
                           startIndex = tableNameSubString.indexOf("[");
                        } else {
                           tableNameSubString = tempTableName.substring(0, startIndex);
                           tableNameSubString = tableNameSubString + "\"";
                           tableNameSubString = tableNameSubString + tempTableName.substring(startIndex + 1);
                           tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                           tempTableName = tableNameSubString;
                           startIndex = tableNameSubString.indexOf("[");
                        }
                     }

                     ft.setTableName(tableNameSubString);
                  }
               }

               tempTableName = (String)this.tableName;
               atIndex = tempTableName.indexOf("@");
               tokenVector = new Vector();
               String table_Name = (String)this.tableName;
               StringTokenizer st = new StringTokenizer(table_Name, ".");

               int count;
               for(count = 0; st.hasMoreTokens(); ++count) {
                  tokenVector.add(st.nextToken());
               }

               String tempTableName;
               String tableNameSubString;
               if (count == 1 && atIndex != -1) {
                  tempTableName = (String)tokenVector.elementAt(0);
                  tableNameSubString = tempTableName.substring(0, atIndex);
                  tempTableName.substring(atIndex + 1);
                  ft.setTableName(tableNameSubString);
               }

               tempTableName = (String)ft.getTableName();
               tableNameSubString = "";
               int startIndex = tempTableName.indexOf("[");
               if (startIndex != -1) {
                  if (startIndex == 0) {
                     tableNameSubString = tempTableName.substring(1);
                     tableNameSubString = "\"" + tableNameSubString;
                     tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                     tempTableName = tableNameSubString;
                  }

                  ft.setTableName(tempTableName);
               }

               String tblName = (String)ft.getTableName();
               ft.setTableName(tblName);
               tblName = (String)ft.getTableName();
               ft.setTableName(tblName);
            }
         }
      }

      if (this.setOperatorClauseListForSubQuery.size() != 0) {
         ArrayList setOperatorClauseList = new ArrayList();

         for(atIndex = 0; atIndex < this.setOperatorClauseListForSubQuery.size(); ++atIndex) {
            setOperatorClauseList.add(((SetOperatorClause)this.setOperatorClauseListForSubQuery.get(atIndex)).toSqliteSelect(vembuSQS, vendorSQS));
         }

         ft.setSetOperatorClauseListForSubQuery(setOperatorClauseList);
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         tempTableName = this.aliasName;
         if ((!tempTableName.startsWith("\"") || !tempTableName.endsWith("\"")) && (!tempTableName.startsWith("'") || !tempTableName.endsWith("'"))) {
            ft.setAliasName(this.aliasName);
         } else {
            tempTableName = tempTableName.substring(1, tempTableName.length() - 1).trim();
            tempTableName = tempTableName.replace("`", "``");
            tempTableName = "`" + tempTableName + "`";
            ft.setAliasName(tempTableName);
         }
      } else if (this.tableName instanceof SelectQueryStatement && this.setOperatorClauseListForSubQuery.size() == 0) {
         SelectQueryStatement sqs = (SelectQueryStatement)this.tableName;
         if (sqs.getSetOperatorClause() != null) {
            SetOperatorClause soc = sqs.getSetOperatorClause();
            if (soc == null || soc.getSetClause() == null || !soc.getSetClause().toUpperCase().startsWith("UNION") && !soc.getSetClause().toUpperCase().startsWith("MINUS") && !soc.getSetClause().toUpperCase().startsWith("INTERSECT") && !soc.getSetClause().toUpperCase().startsWith("EXCEPT")) {
               ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
            }
         } else {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
         }
      }

      if (this.joinClause != null && this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
         ft.setOnOrUsingJoin((String)null);
      }

      ft.setJoinClause(this.joinClause);
      return ft;
   }

   public FromTable toExcelSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass((CommentClass)null);
      int i;
      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         WhereExpression we;
         Vector operatorList;
         if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            we = ((WhereExpression)this.joinExpression.elementAt(0)).toExcelSelect(vembuSQS, vendorSQS);
            operatorList = new Vector();
            operatorList.addElement(we);
            ft.setJoinExpression(operatorList);
         } else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            we = (WhereExpression)this.joinExpression.elementAt(0);
            operatorList = we.getOperator();

            for(int i = 0; i < operatorList.size(); ++i) {
               if (operatorList.elementAt(i) instanceof String && ((String)operatorList.get(i)).equalsIgnoreCase("AND")) {
                  operatorList.setElementAt("AND", i);
               }
            }

            we.setOperator(operatorList);
            we = we.toExcelSelect(vembuSQS, vendorSQS);
            Vector v = new Vector();
            v.addElement(we);
            ft.setJoinExpression(v);
         }

         ft.setOnOrUsingJoin(this.onOrUsingJoin);
         if (this.UsingList != null) {
            Vector v = new Vector();

            for(i = 0; i < this.UsingList.size(); ++i) {
               if (this.UsingList.elementAt(i) instanceof TableColumn) {
                  v.addElement(((TableColumn)this.UsingList.elementAt(i)).toExcelSelect(vembuSQS, vendorSQS));
               } else {
                  v.addElement(this.UsingList.elementAt(i));
               }
            }

            ft.setUsingList(v);
         }

         this.removeJoinExpressionForCrossJoin(ft);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            ((SelectQueryStatement)this.tableName).setReportsMeta(vendorSQS == null ? false : vendorSQS.getReportsMeta());
            ((SelectQueryStatement)this.tableName).setExcelFlag(vendorSQS.isExcel());
            ((SelectQueryStatement)this.tableName).setQueryConversionPropHandler(vendorSQS.getQueryConvPropHandler());
            ft.setTableName(((SelectQueryStatement)this.tableName).toExcel());
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException("Function calls in the place of Table name yet to be supported");
            }

            if (this.tableName instanceof WithStatement) {
               ft.setTableName(((WithStatement)this.tableName).toExcel());
            } else if (this.tableName instanceof FromClause) {
               ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
               ft.setTableName(((FromClause)this.tableName).toExcelSelect(vembuSQS, vendorSQS));
               ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
            } else if (this.tableName.toString().startsWith("\"") && this.tableName.toString().endsWith("\"") || this.tableName.toString().startsWith("`") && this.tableName.toString().endsWith("`")) {
               this.tableName = this.tableName.toString().substring(1, this.tableName.toString().length() - 1);
               ft.setTableName("[" + this.tableName + "]");
            } else {
               ft.setTableName(this.tableName.toString().startsWith("[") && this.tableName.toString().endsWith("]") ? this.tableName : "[" + this.tableName + "]");
            }
         }
      }

      if (this.setOperatorClauseListForSubQuery.size() != 0) {
         ArrayList setOperatorClauseList = new ArrayList();

         for(i = 0; i < this.setOperatorClauseListForSubQuery.size(); ++i) {
            setOperatorClauseList.add(((SetOperatorClause)this.setOperatorClauseListForSubQuery.get(i)).toExcelSelect(vembuSQS, vendorSQS));
         }

         ft.setSetOperatorClauseListForSubQuery(setOperatorClauseList);
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         String tempAliasName = this.aliasName;
         if (tempAliasName.startsWith("\"") && tempAliasName.endsWith("\"") || tempAliasName.startsWith("'") && tempAliasName.endsWith("'")) {
            tempAliasName = tempAliasName.substring(1, tempAliasName.length() - 1).trim();
            tempAliasName = "[" + tempAliasName + "]";
            ft.setAliasName(tempAliasName);
         } else {
            ft.setAliasName(this.aliasName);
         }
      } else if (this.tableName instanceof SelectQueryStatement && this.setOperatorClauseListForSubQuery.size() == 0) {
         SelectQueryStatement sqs = (SelectQueryStatement)this.tableName;
         if (sqs.getSetOperatorClause() != null) {
            SetOperatorClause soc = sqs.getSetOperatorClause();
            if (soc == null || soc.getSetClause() == null || !soc.getSetClause().toUpperCase().startsWith("UNION") && !soc.getSetClause().toUpperCase().startsWith("MINUS") && !soc.getSetClause().toUpperCase().startsWith("INTERSECT") && !soc.getSetClause().toUpperCase().startsWith("EXCEPT")) {
               ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
            }
         } else {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
         }
      }

      if (this.joinClause != null && this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
         ft.setOnOrUsingJoin((String)null);
      }

      ft.setJoinClause(this.joinClause);
      return ft;
   }

   public FromTable toMsAccessJdbcSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass((CommentClass)null);
      int i;
      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         WhereExpression we;
         Vector operatorList;
         if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            we = ((WhereExpression)this.joinExpression.elementAt(0)).toMsAccessJdbcSelect(vembuSQS, vendorSQS);
            operatorList = new Vector();
            operatorList.addElement(we);
            ft.setJoinExpression(operatorList);
         } else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            we = (WhereExpression)this.joinExpression.elementAt(0);
            operatorList = we.getOperator();

            for(int i = 0; i < operatorList.size(); ++i) {
               if (operatorList.elementAt(i) instanceof String && ((String)operatorList.get(i)).equalsIgnoreCase("AND")) {
                  operatorList.setElementAt("AND", i);
               }
            }

            we.setOperator(operatorList);
            we = we.toMsAccessJdbcSelect(vembuSQS, vendorSQS);
            Vector v = new Vector();
            v.addElement(we);
            ft.setJoinExpression(v);
         }

         ft.setOnOrUsingJoin(this.onOrUsingJoin);
         if (this.UsingList != null) {
            Vector v = new Vector();

            for(i = 0; i < this.UsingList.size(); ++i) {
               if (this.UsingList.elementAt(i) instanceof TableColumn) {
                  v.addElement(((TableColumn)this.UsingList.elementAt(i)).toMsAccessJdbcSelect(vembuSQS, vendorSQS));
               } else {
                  v.addElement(this.UsingList.elementAt(i));
               }
            }

            ft.setUsingList(v);
         }

         this.removeJoinExpressionForCrossJoin(ft);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            ((SelectQueryStatement)this.tableName).setReportsMeta(vendorSQS == null ? false : vendorSQS.getReportsMeta());
            ((SelectQueryStatement)this.tableName).setMsAccessJdbcFlag(vendorSQS.isMsAccess());
            ((SelectQueryStatement)this.tableName).setQueryConversionPropHandler(vendorSQS.getQueryConvPropHandler());
            ft.setTableName(((SelectQueryStatement)this.tableName).toMsAccessJdbc());
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException("Function calls in the place of Table name yet to be supported");
            }

            if (this.tableName instanceof WithStatement) {
               ft.setTableName(((WithStatement)this.tableName).toMsAccessJdbcString());
            } else if (this.tableName instanceof FromClause) {
               ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
               ft.setTableName(((FromClause)this.tableName).toMsAccessJdbcSelect(vembuSQS, vendorSQS));
               ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
            } else if (this.tableName.toString().startsWith("\"") && this.tableName.toString().endsWith("\"") || this.tableName.toString().startsWith("`") && this.tableName.toString().endsWith("`")) {
               this.tableName = this.tableName.toString().substring(1, this.tableName.toString().length() - 1);
               ft.setTableName("[" + this.tableName + "]");
            } else {
               ft.setTableName(this.tableName.toString().startsWith("[") && this.tableName.toString().endsWith("]") ? this.tableName : "[" + this.tableName + "]");
            }
         }
      }

      if (this.setOperatorClauseListForSubQuery.size() != 0) {
         ArrayList setOperatorClauseList = new ArrayList();

         for(i = 0; i < this.setOperatorClauseListForSubQuery.size(); ++i) {
            setOperatorClauseList.add(((SetOperatorClause)this.setOperatorClauseListForSubQuery.get(i)).toMsAccessJdbcSelect(vembuSQS, vendorSQS));
         }

         ft.setSetOperatorClauseListForSubQuery(setOperatorClauseList);
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         String tempAliasName = this.aliasName;
         if (tempAliasName.startsWith("\"") && tempAliasName.endsWith("\"") || tempAliasName.startsWith("'") && tempAliasName.endsWith("'")) {
            tempAliasName = tempAliasName.substring(1, tempAliasName.length() - 1).trim();
            tempAliasName = "[" + tempAliasName + "]";
            ft.setAliasName(tempAliasName);
         } else {
            ft.setAliasName(this.aliasName);
         }
      } else if (this.tableName instanceof SelectQueryStatement && this.setOperatorClauseListForSubQuery.size() == 0) {
         SelectQueryStatement sqs = (SelectQueryStatement)this.tableName;
         if (sqs.getSetOperatorClause() != null) {
            SetOperatorClause soc = sqs.getSetOperatorClause();
            if (soc == null || soc.getSetClause() == null || !soc.getSetClause().toUpperCase().startsWith("UNION") && !soc.getSetClause().toUpperCase().startsWith("MINUS") && !soc.getSetClause().toUpperCase().startsWith("INTERSECT") && !soc.getSetClause().toUpperCase().startsWith("EXCEPT")) {
               ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
            }
         } else {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
         }
      }

      if (this.joinClause != null && this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
         ft.setOnOrUsingJoin((String)null);
      }

      ft.setJoinClause(this.joinClause);
      return ft;
   }

   public FromTable toPostgreSQLSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass((CommentClass)null);
      int atIndex;
      int i;
      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         WhereExpression we;
         Vector operatorList;
         if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            we = ((WhereExpression)this.joinExpression.elementAt(0)).toPostgreSQLSelect(vembuSQS, vendorSQS);
            operatorList = new Vector();
            operatorList.addElement(we);
            ft.setJoinExpression(operatorList);
         } else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            we = (WhereExpression)this.joinExpression.elementAt(0);
            operatorList = we.getOperator();

            for(atIndex = 0; atIndex < operatorList.size(); ++atIndex) {
               if (operatorList.elementAt(atIndex) instanceof String && ((String)operatorList.get(atIndex)).equalsIgnoreCase("AND")) {
                  operatorList.setElementAt("AND", atIndex);
               }
            }

            we.setOperator(operatorList);
            we = we.toPostgreSQLSelect(vembuSQS, vendorSQS);
            Vector v = new Vector();
            v.addElement(we);
            ft.setJoinExpression(v);
         }

         ft.setOnOrUsingJoin(this.onOrUsingJoin);
         if (this.UsingList != null) {
            Vector v = new Vector();

            for(i = 0; i < this.UsingList.size(); ++i) {
               if (this.UsingList.elementAt(i) instanceof TableColumn) {
                  v.addElement(((TableColumn)this.UsingList.elementAt(i)).toPostgreSQLSelect(vembuSQS, vendorSQS));
               } else {
                  v.addElement(this.UsingList.elementAt(i));
               }
            }

            ft.setUsingList(v);
         }

         ft.setJoinClause(this.joinClause);
         this.removeJoinExpressionForCrossJoin(ft);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      SelectQueryStatement sqs;
      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            sqs = (SelectQueryStatement)this.tableName;
            sqs.setReportsMeta(vendorSQS == null ? false : vendorSQS.getReportsMeta());
            sqs.setAmazonRedShiftFlag(vendorSQS.isAmazonRedShift());
            sqs.setPgsqlFlag(vendorSQS.isPgsqlLive());
            sqs.setVerticaFlag(vendorSQS.isVerticaDb());
            sqs.setMSAzureFlag(vendorSQS.isMSAzure());
            sqs.setMSAzureWareHouseFlag(vendorSQS.isMSAzureWareHouse());
            sqs.setOracleLiveFlag(vendorSQS.isOracleLive());
            sqs.setDenodoFlag(vendorSQS.isDenodo());
            sqs.setMySqlLiveFlag(vendorSQS.isMySqlLive());
            sqs.setMongoDbFlag(vendorSQS.isMongoDb());
            sqs.setQueryConversionPropHandler(vendorSQS.getQueryConvPropHandler());
            if (this.setOperatorClauseListForSubQuery.size() > 0) {
               sqs.setSQSWithSetOperatorClList(true);
            }

            ft.setTableName(sqs.toPostgreSQL());
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException("Function calls in the place of Table name yet to be supported");
            }

            if (this.tableName instanceof WithStatement) {
               ft.setTableName(((WithStatement)this.tableName).toPostgreSQL());
            } else if (this.tableName instanceof FromClause) {
               ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
               ft.setTableName(((FromClause)this.tableName).toPostgreSQLSelect(vembuSQS, vendorSQS));
               ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
            } else {
               boolean isPostgreLiveDbs = vendorSQS != null && (vendorSQS.isAmazonRedShift() || vendorSQS.isPgsqlLive() || vendorSQS.isVerticaDb());
               int atIndex;
               String tempTableName;
               int startIndex;
               String tempTableName;
               String tableNameSubString;
               if (((String)this.tableName).indexOf(".") > 0 && !isPostgreLiveDbs) {
                  tempTableName = (String)this.tableName;
                  if (tempTableName.indexOf("..") > 0) {
                     ft.setTableName(tempTableName.substring(tempTableName.indexOf("..") + 2, tempTableName.length()));
                  } else {
                     tableNameSubString = (String)this.tableName;
                     atIndex = tableNameSubString.indexOf("@");
                     ft.setTableName(tempTableName.substring(tempTableName.indexOf(".") + 1, tempTableName.length()));
                     Vector tokenVector = new Vector();
                     String table_Name = (String)this.tableName;
                     StringTokenizer st = new StringTokenizer(table_Name, ".");

                     int count;
                     for(count = 0; st.hasMoreTokens(); ++count) {
                        tokenVector.add(st.nextToken());
                     }

                     String tableNameSubString;
                     if (count == 1 && atIndex != -1) {
                        tempTableName = (String)tokenVector.elementAt(0);
                        tableNameSubString = tempTableName.substring(0, atIndex);
                        tempTableName.substring(atIndex + 1);
                        ft.setTableName(tableNameSubString);
                     } else if (count == 2 && atIndex != -1) {
                        tempTableName = (String)tokenVector.elementAt(1);
                        startIndex = tempTableName.indexOf("@");
                        String sqlTableName = tempTableName.substring(0, startIndex);
                        tempTableName.substring(startIndex + 1);
                        ft.setTableName(sqlTableName);
                     }

                     if (((String)this.tableName).indexOf("[") >= 0) {
                        tempTableName = (String)ft.getTableName();
                        tableNameSubString = "";
                        int startIndex = tempTableName.indexOf("[");
                        if (startIndex != -1) {
                           while(startIndex != -1) {
                              if (startIndex == 0) {
                                 tableNameSubString = tempTableName.substring(1);
                                 tableNameSubString = "\"" + tableNameSubString;
                                 tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                                 tempTableName = tableNameSubString;
                                 startIndex = tableNameSubString.indexOf("[");
                              } else {
                                 tableNameSubString = tempTableName.substring(0, startIndex);
                                 tableNameSubString = tableNameSubString + "\"";
                                 tableNameSubString = tableNameSubString + tempTableName.substring(startIndex + 1);
                                 tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                                 tempTableName = tableNameSubString;
                                 startIndex = tableNameSubString.indexOf("[");
                              }
                           }

                           ft.setTableName(tableNameSubString);
                        }
                     }
                  }
               } else {
                  if (((String)this.tableName).indexOf("[") == 0) {
                     tempTableName = (String)this.tableName;
                     tableNameSubString = "";
                     atIndex = tempTableName.indexOf("[");
                     if (atIndex != -1) {
                        while(atIndex != -1) {
                           if (atIndex == 0) {
                              tableNameSubString = tempTableName.substring(1);
                              tableNameSubString = "\"" + tableNameSubString;
                              tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                              tempTableName = tableNameSubString;
                              atIndex = tableNameSubString.indexOf("[");
                           } else {
                              tableNameSubString = tempTableName.substring(0, atIndex);
                              tableNameSubString = tableNameSubString + "\"";
                              tableNameSubString = tableNameSubString + tempTableName.substring(atIndex + 1);
                              tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                              tempTableName = tableNameSubString;
                              atIndex = tableNameSubString.indexOf("[");
                           }
                        }

                        ft.setTableName(tableNameSubString);
                     }
                  } else if (vendorSQS.isCTESupported()) {
                     if (!vendorSQS.getBooleanValues("is.pg.build")) {
                        ft.setTableName("\"" + GeneralUtil.trimIfTblColIsEnclosed((String)this.tableName) + "\"");
                     } else {
                        ft.setTableName("\"" + GeneralUtil.trimIfTblColIsEnclosed((String)this.tableName).toLowerCase() + "\"");
                     }
                  } else {
                     ft.setTableName(this.tableName);
                  }

                  tempTableName = (String)this.tableName;
                  atIndex = tempTableName.indexOf("@");
                  Vector tokenVector = new Vector();
                  String table_Name = (String)this.tableName;
                  StringTokenizer st = new StringTokenizer(table_Name, ".");

                  int count;
                  for(count = 0; st.hasMoreTokens(); ++count) {
                     tokenVector.add(st.nextToken());
                  }

                  String tempTableName;
                  if (count == 1 && atIndex != -1) {
                     tempTableName = (String)tokenVector.elementAt(0);
                     tempTableName = tempTableName.substring(0, atIndex);
                     tempTableName.substring(atIndex + 1);
                     ft.setTableName(tempTableName);
                  }

                  tempTableName = (String)ft.getTableName();
                  tempTableName = "";
                  startIndex = tempTableName.indexOf("[");
                  if (startIndex != -1) {
                     if (startIndex == 0) {
                        tempTableName = tempTableName.substring(1);
                        tempTableName = "\"" + tempTableName;
                        tempTableName = StringFunctions.replaceFirst("\"", "]", tempTableName);
                        tempTableName = tempTableName;
                     }

                     ft.setTableName(tempTableName);
                  }
               }

               tempTableName = (String)ft.getTableName();
               if (tempTableName.charAt(0) == '\'') {
                  ft.setTableName(tempTableName.replace('\'', '"'));
               } else if (tempTableName.charAt(0) == '`') {
                  ft.setTableName(tempTableName.replace('`', '"'));
               } else {
                  ft.setTableName(tempTableName);
               }

               tempTableName = (String)ft.getTableName();
               ft.setTableName(SelectStatement.checkandRemoveDoubleQuoteForPostgresIdentifier(tempTableName, vendorSQS == null ? false : vendorSQS.getReportsMeta()));
            }
         }
      }

      if (this.setOperatorClauseListForSubQuery.size() != 0) {
         ArrayList setOperatorClauseList = new ArrayList();

         for(i = 0; i < this.setOperatorClauseListForSubQuery.size(); ++i) {
            setOperatorClauseList.add(((SetOperatorClause)this.setOperatorClauseListForSubQuery.get(i)).toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)this.tableName));
         }

         ft.setSetOperatorClauseListForSubQuery(setOperatorClauseList);
         if (vendorSQS.getBooleanValues("can.cast.set.query.list.sel.cols")) {
            for(i = 0; i < setOperatorClauseList.size(); ++i) {
               SelectQueryStatement setSQS = ((SetOperatorClause)setOperatorClauseList.get(i)).getSelectQueryStatement();
               ((SelectQueryStatement)this.tableName).replaceSelectItemsForStringLiteralsAndNULLString(setSQS, StringFunctions.getStringDataType(setSQS));
            }
         }
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         if (this.aliasName.charAt(0) == '\'') {
            ft.setAliasName(this.aliasName.replace('\'', '"'));
         } else if (this.aliasName.charAt(0) == '`') {
            ft.setAliasName(this.aliasName.replace('`', '"'));
         } else if (this.aliasName.charAt(0) == '"') {
            ft.setAliasName(this.aliasName);
         } else {
            ft.setAliasName("\"" + this.aliasName + "\"");
         }

         ft.setAliasName(SelectStatement.checkandRemoveDoubleQuoteForPostgresIdentifier(ft.getAliasName(), vendorSQS == null ? false : vendorSQS.getReportsMeta()));
      } else if (this.tableName instanceof SelectQueryStatement && this.setOperatorClauseListForSubQuery.size() == 0) {
         sqs = (SelectQueryStatement)this.tableName;
         if (sqs.getSetOperatorClause() != null) {
            SetOperatorClause soc = sqs.getSetOperatorClause();
            if (soc == null || soc.getSetClause() == null || !soc.getSetClause().toUpperCase().startsWith("UNION") && !soc.getSetClause().toUpperCase().startsWith("MINUS") && !soc.getSetClause().toUpperCase().startsWith("INTERSECT") && !soc.getSetClause().toUpperCase().startsWith("EXCEPT")) {
               ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
            }
         } else {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
         }
      }

      if (this.joinClause != null && this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
         ft.setOnOrUsingJoin((String)null);
      }

      return ft;
   }

   public FromTable toDB2Select(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass(this.commentObj);
      Vector tokenVector;
      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
            throw new ConvertException("Conversion failure..Natural join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("CROSS JOIN")) {
            this.joinClause = "INNER JOIN";
            this.setOnOrUsingJoin("ON");
            Vector newWhereItems = new Vector();
            WhereItem newWhereItem = new WhereItem();
            WhereColumn newWhereColumn = new WhereColumn();
            Vector whereColumnItems = new Vector();
            whereColumnItems.add("1");
            newWhereColumn.setColumnExpression(whereColumnItems);
            newWhereItem.setLeftWhereExp(newWhereColumn);
            newWhereItem.setRightWhereExp(newWhereColumn);
            newWhereItem.setOperator("=");
            WhereExpression newWhereExpression = new WhereExpression();
            newWhereExpression.addWhereItem(newWhereItem);
            newWhereItems.add(newWhereExpression);
            this.setJoinExpression(newWhereItems);
         } else {
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
               throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }

            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
               throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
            }

            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
               throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }

            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
               throw new ConvertException("Conversion failure..Natural right join can't be converted");
            }

            if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
               throw new ConvertException("Conversion failure..Key join is not supported");
            }
         }

         WhereExpression we;
         Vector operatorList;
         if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            we = ((WhereExpression)this.joinExpression.elementAt(0)).toDB2Select(vembuSQS, vendorSQS);
            operatorList = new Vector();
            operatorList.addElement(we);
            ft.setJoinExpression(operatorList);
         } else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
            this.changeToOnJoin(ft, this.getPreviousFromTableFromTheFromItemList(vendorSQS));
         } else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            we = (WhereExpression)this.joinExpression.elementAt(0);
            operatorList = we.getOperator();
            we.setOperator(operatorList);
            we = we.toDB2Select(vembuSQS, vendorSQS);
            tokenVector = new Vector();
            tokenVector.addElement(we);
            ft.setJoinExpression(tokenVector);
         }
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      String ownerName;
      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            ft.setTableKeyword(this.tableKeyword);
            ft.setTableName(((SelectQueryStatement)this.tableName).toDB2());
         } else if (this.tableName instanceof FunctionCalls) {
            ft.setTableKeyword(this.tableKeyword);
            ft.setTableName(((FunctionCalls)this.tableName).toDB2Select(vembuSQS, vendorSQS));
         } else if (this.tableName instanceof WithStatement) {
            ft.setTableName(((WithStatement)this.tableName).toDB2());
         } else if (this.tableName instanceof FromClause) {
            ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
            ft.setTableName(((FromClause)this.tableName).toDB2Select(vembuSQS, vendorSQS));
            ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
         } else {
            ownerName = (String)this.tableName;
            int atIndex = ownerName.indexOf("@");
            ft.setTableName(StringFunctions.replaceFirst(".", "..", (String)this.tableName));
            tokenVector = new Vector();
            String table_Name = (String)this.tableName;
            StringTokenizer st = new StringTokenizer(table_Name, ".");

            int count;
            for(count = 0; st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            String tempTableName;
            String sqlTableName;
            String sqlDataBaseName;
            String tableNameSubString;
            if (count == 1 && atIndex != -1) {
               tempTableName = (String)tokenVector.elementAt(0);
               tableNameSubString = tempTableName.substring(0, atIndex);
               sqlTableName = tempTableName.substring(atIndex + 1);
               sqlDataBaseName = sqlTableName + "." + tableNameSubString;
               ft.setTableName(sqlDataBaseName);
            } else if (count == 2 && atIndex != -1) {
               tempTableName = (String)tokenVector.elementAt(1);
               int tableAtIndex = tempTableName.indexOf("@");
               sqlTableName = tempTableName;
               if (tableAtIndex != -1) {
                  sqlTableName = tempTableName.substring(0, tableAtIndex);
               }

               sqlDataBaseName = tempTableName.substring(tableAtIndex + 1);
               String sqlDataBaseAndTableName = sqlDataBaseName + "." + (String)tokenVector.elementAt(0) + "." + sqlTableName;
               ft.setTableName(sqlDataBaseAndTableName);
            }

            tempTableName = (String)ft.getTableName();
            tableNameSubString = "";
            int startIndex = tempTableName.indexOf("[");
            if (startIndex != -1) {
               while(startIndex != -1) {
                  if (startIndex == 0) {
                     tableNameSubString = tempTableName.substring(1);
                     tableNameSubString = "\"" + tableNameSubString;
                     tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                     tempTableName = tableNameSubString;
                     startIndex = tableNameSubString.indexOf("[");
                  } else {
                     tableNameSubString = tempTableName.substring(0, startIndex);
                     tableNameSubString = tableNameSubString + "\"";
                     tableNameSubString = tableNameSubString + tempTableName.substring(startIndex + 1);
                     tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                     tempTableName = tableNameSubString;
                     startIndex = tableNameSubString.indexOf("[");
                  }
               }

               ft.setTableName(tableNameSubString);
            }
         }
      }

      if (!this.setOperatorClauseListForSubQuery.isEmpty()) {
         ArrayList setOperatorClauseList = new ArrayList();
         Iterator var20 = this.setOperatorClauseListForSubQuery.iterator();

         while(var20.hasNext()) {
            Object soc = var20.next();
            setOperatorClauseList.add(((SetOperatorClause)soc).toDB2Select(vembuSQS, vendorSQS));
         }

         ft.setSetOperatorClauseListForSubQuery(setOperatorClauseList);
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         if (this.aliasName.charAt(0) == '\'') {
            ft.setAliasName(this.aliasName.replace('\'', '"'));
         } else {
            ft.setAliasName(this.aliasName);
         }
      } else if (this.tableName instanceof SelectQueryStatement && this.setOperatorClauseListForSubQuery.isEmpty()) {
         ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
      }

      ft.setJoinClause(this.joinClause);
      ownerName = (String)SwisSQLAPI.objectsOwnerName.get(new Integer(3));
      if (ownerName != null && ft.getTableName() instanceof String && ft.getTableName().toString().toLowerCase().indexOf(ownerName + ".") == -1) {
         ft.setTableName(ownerName + "." + ft.getTableName());
      }

      return ft;
   }

   public FromTable toANSISelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass(this.commentObj);
      ft.setTableKeyword(this.tableKeyword);
      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            ft.setTableName(((SelectQueryStatement)this.tableName).toANSI());
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException("Functions yet to be supported in table names");
            }

            if (this.tableName instanceof WithStatement) {
               ft.setTableName(((WithStatement)this.tableName).toANSISQL());
            } else if (this.tableName instanceof FromClause) {
               ft.setTableName(((FromClause)this.tableName).toANSISelect(vembuSQS, vendorSQS));
            } else {
               String table_Name_String = (String)this.tableName;
               int atIndex = table_Name_String.indexOf("@");
               int lastIndexOfDot = table_Name_String.lastIndexOf(".");
               String remoteLinkName = "";
               if (atIndex != -1) {
                  remoteLinkName = table_Name_String.substring(atIndex + 1);
               }

               if (remoteLinkName.indexOf(".") != -1) {
                  remoteLinkName = "\"" + remoteLinkName + "\"";
               }

               ft.setTableName(StringFunctions.replaceFirst(".", "..", (String)this.tableName));
               Vector tokenVector = new Vector();
               String table_Name = (String)this.tableName;
               if (atIndex != -1 && lastIndexOfDot > atIndex) {
                  table_Name = table_Name.substring(0, atIndex);
               }

               StringTokenizer st = new StringTokenizer(table_Name, ".");

               int count;
               for(count = 0; st.hasMoreTokens(); ++count) {
                  tokenVector.add(st.nextToken());
               }

               String tempTableName;
               String tableNameSubString;
               if (count == 1 && atIndex != -1) {
                  tempTableName = (String)tokenVector.elementAt(0);
                  tableNameSubString = tempTableName.substring(0, atIndex);
                  String sqlDataBaseAndTableName = remoteLinkName + "." + tableNameSubString;
                  ft.setTableName(sqlDataBaseAndTableName);
               } else if (count == 2 && atIndex != -1) {
                  tempTableName = (String)tokenVector.elementAt(1);
                  tableNameSubString = remoteLinkName + "." + (String)tokenVector.elementAt(0) + "." + tempTableName;
                  ft.setTableName(tableNameSubString);
               }

               tempTableName = (String)ft.getTableName();
               tableNameSubString = "";
               int startIndex = tempTableName.indexOf("[");
               if (startIndex == -1) {
                  String databaseName;
                  if (count == 1 && atIndex == -1) {
                     databaseName = tokenVector.get(0).toString().trim();
                     if (!databaseName.startsWith("\"") && !databaseName.startsWith("'")) {
                        if (SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
                           ft.setTableName("\"" + ft.getTableName() + "\"");
                        } else {
                           ft.setTableName(ft.getTableName());
                        }
                     } else {
                        ft.setTableName(databaseName);
                     }
                  } else {
                     String databaseSchemaName;
                     if (count == 2 && atIndex == -1) {
                        databaseName = tokenVector.get(0).toString();
                        databaseSchemaName = tokenVector.get(1).toString();
                        if (!databaseName.startsWith("\"") && !databaseSchemaName.startsWith("\"") && !databaseName.startsWith("'") && !databaseSchemaName.startsWith("'") && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
                           ft.setTableName("\"" + databaseName + "\"" + "." + "\"" + databaseSchemaName + "\"");
                        } else {
                           ft.setTableName(ft.getTableName());
                        }
                     } else if (count == 3 && atIndex == -1) {
                        databaseName = tokenVector.get(0).toString();
                        databaseSchemaName = tokenVector.get(1).toString();
                        String databaseTableName = tokenVector.get(2).toString();
                        if (!databaseSchemaName.startsWith("\"") && !databaseTableName.startsWith("\"") && !databaseSchemaName.startsWith("'") && !databaseTableName.startsWith("'") && !databaseName.startsWith("\"") && !databaseName.startsWith("'") && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
                           ft.setTableName("\"" + databaseName + "\"" + "." + "\"" + databaseSchemaName + "\"" + "." + "\"" + databaseTableName + "\"");
                        } else {
                           ft.setTableName(ft.getTableName());
                        }
                     } else if (SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
                        ft.setTableName("\"" + ft.getTableName() + "\"");
                     } else {
                        ft.setTableName(ft.getTableName());
                     }
                  }
               } else {
                  while(startIndex != -1) {
                     if (startIndex == 0) {
                        tableNameSubString = tempTableName.substring(1);
                        tableNameSubString = "\"" + tableNameSubString;
                        tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                        tempTableName = tableNameSubString;
                        startIndex = tableNameSubString.indexOf("[");
                     } else {
                        tableNameSubString = tempTableName.substring(0, startIndex);
                        tableNameSubString = tableNameSubString + "\"";
                        tableNameSubString = tableNameSubString + tempTableName.substring(startIndex + 1);
                        tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                        tempTableName = tableNameSubString;
                        startIndex = tableNameSubString.indexOf("[");
                     }
                  }

                  ft.setTableName(tableNameSubString);
               }
            }
         }
      }

      WhereExpression we;
      Vector operatorList;
      if (this.onOrUsingJoin == null && this.joinExpression != null) {
         ft.setOnOrUsingJoin("ON");
         we = ((WhereExpression)this.joinExpression.elementAt(0)).toDB2Select(vembuSQS, vendorSQS);
         operatorList = new Vector();
         operatorList.addElement(we);
         ft.setJoinExpression(operatorList);
      } else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
         this.changeToOnJoin(ft, this.getPreviousFromTableFromTheFromItemList(vendorSQS));
      } else if (this.joinExpression != null) {
         ft.setOnOrUsingJoin(this.onOrUsingJoin);
         we = (WhereExpression)this.joinExpression.elementAt(0);
         operatorList = we.getOperator();
         we.setOperator(operatorList);
         Vector v = new Vector();
         v.addElement(we.toANSISelect(vembuSQS, vendorSQS));
         ft.setJoinExpression(v);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         if (this.aliasName.charAt(0) == '\'' && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
            ft.setAliasName(this.aliasName.replace('\'', '"'));
         } else if (this.aliasName.charAt(0) == '"') {
            ft.setAliasName(this.aliasName);
         } else if (SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
            ft.setAliasName("\"" + this.aliasName + "\"");
         } else {
            ft.setAliasName(this.aliasName);
         }
      } else if (this.tableName instanceof SelectQueryStatement) {
         ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
      }

      ft.setJoinClause(this.joinClause);
      return ft;
   }

   public FromTable toTeradataSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass(this.commentObj);
      ft.setTableKeyword(this.tableKeyword);
      Vector selectItemList;
      String origStr;
      String origStrBase;
      Vector tokenVector;
      int j;
      if (this.tableName != null) {
         int dotDotIndex;
         String aliasForExpr;
         if (this.tableName instanceof SelectQueryStatement) {
            SelectQueryStatement subQueryStmt = (SelectQueryStatement)this.tableName;
            if (!this.setOperatorClauseListForSubQuery.isEmpty()) {
               this.buildSetOperatorClauseForSubQuery(subQueryStmt);
            }

            selectItemList = subQueryStmt.getSelectStatement().getSelectItemList();

            for(dotDotIndex = 0; dotDotIndex < selectItemList.size(); ++dotDotIndex) {
               if (selectItemList.elementAt(dotDotIndex) instanceof SelectColumn) {
                  SelectColumn sc1 = (SelectColumn)selectItemList.elementAt(dotDotIndex);
                  if (vendorSQS != null && !vendorSQS.getTopLevel()) {
                     if (sc1.getColumnExpression().size() == 1 && !(sc1.getColumnExpression().get(0) instanceof TableColumn) && sc1.getAliasName() == null) {
                        aliasForExpr = sc1.getTheCoreSelectItem().trim();
                        if (sc1.getColumnExpression().get(0) instanceof SelectColumn && ((SelectColumn)sc1.getColumnExpression().get(0)).getColumnExpression().size() == 1) {
                           if (!(((SelectColumn)sc1.getColumnExpression().get(0)).getColumnExpression().get(0) instanceof TableColumn)) {
                              aliasForExpr = ((SelectColumn)sc1.getColumnExpression().get(0)).toString();
                           } else {
                              aliasForExpr = "";
                           }
                        }

                        if (aliasForExpr.lastIndexOf(",") != -1) {
                           aliasForExpr = aliasForExpr.substring(0, aliasForExpr.lastIndexOf(",")).trim();
                        }

                        if (aliasForExpr.indexOf("*/") != -1) {
                           aliasForExpr = aliasForExpr.substring(aliasForExpr.indexOf("*/") + 2).trim();
                        }

                        boolean isNum = false;

                        try {
                           Double.parseDouble(aliasForExpr);
                           isNum = true;
                        } catch (NumberFormatException var24) {
                           isNum = false;
                        }

                        if (!isNum && !aliasForExpr.toLowerCase().startsWith("case") && aliasForExpr.indexOf(".") != -1 && aliasForExpr.indexOf(".") == aliasForExpr.lastIndexOf(".") && aliasForExpr.indexOf("(") == -1) {
                           aliasForExpr = aliasForExpr.substring(aliasForExpr.lastIndexOf(".") + 1);
                        }

                        if (aliasForExpr.indexOf("/*") != -1) {
                           aliasForExpr = aliasForExpr.substring(0, aliasForExpr.indexOf("/*")).trim();
                        }

                        if (!aliasForExpr.equalsIgnoreCase("*") && !aliasForExpr.startsWith("*") && !aliasForExpr.endsWith("*")) {
                           if (aliasForExpr.length() > 30) {
                              aliasForExpr = aliasForExpr.substring(0, 29);
                           }

                           if (aliasForExpr.length() > 0) {
                              sc1.setAliasForExpression("\"" + aliasForExpr.replaceAll("\n", " ").replaceAll("\t", " ") + "\"");
                           }
                        }
                     }

                     if (sc1.getAliasName() == null && sc1.getAliasForExpression() != null) {
                        sc1.setAliasName(sc1.getAliasForExpression());
                     }
                  }
               }
            }

            SelectQueryStatement subQuery = subQueryStmt;
            if (!subQueryStmt.isConverted()) {
               subQuery = subQueryStmt.toTeradata();
            }

            if (subQuery.getWithStatement() != null) {
               vendorSQS.getListOfWithStatements().add(subQuery.getWithStatement());
               SelectQueryStatement tempSubQuery = subQuery.getWithStatement().getWithSQS();
               subQuery.getWithStatement().setWithSQS((SelectQueryStatement)null);
               subQuery = tempSubQuery;
            }

            ft.setTableName(subQuery);
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException("Functions yet to be supported in table names");
            }

            if (this.tableName instanceof WithStatement) {
               ft.setTableName(((WithStatement)this.tableName).toTeradata());
            } else if (this.tableName instanceof FromClause) {
               FromClause teradataFromClause = ((FromClause)this.tableName).toTeradataSelect(vembuSQS, vendorSQS);
               ft.setTableName(teradataFromClause);
               if (this.aliasName == null && ft.getAliasName() == null && teradataFromClause.getAliasName() != null) {
                  ft.setAliasName(teradataFromClause.getAliasName());
               }
            } else {
               String table_Name_String = (String)this.tableName;
               int atIndex = table_Name_String.indexOf("@");
               if (table_Name_String.indexOf("..") != -1) {
                  ft.setTableName(StringFunctions.replaceFirst(".", "..", (String)this.tableName));
               }

               dotDotIndex = table_Name_String.indexOf("..");
               ft.setTableName(table_Name_String);
               String ownerName;
               String tableName;
               if (dotDotIndex != -1) {
                  String localTableName = table_Name_String.substring(table_Name_String.lastIndexOf(".") + 1, table_Name_String.length());
                  ft.setTableName(CustomizeUtil.objectNamesToQuotedIdentifier(localTableName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1));
               } else {
                  tokenVector = new Vector();
                  StringTokenizer st = new StringTokenizer(table_Name_String, ".");

                  int count;
                  for(count = 0; st.hasMoreTokens(); ++count) {
                     tokenVector.add(st.nextToken());
                  }

                  if (count == 1) {
                     ft.setTableName(CustomizeUtil.objectNamesToQuotedIdentifier(table_Name_String, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1));
                  } else {
                     String ownerName;
                     if (count == 2) {
                        ownerName = (String)tokenVector.elementAt(0);
                        ownerName = (String)tokenVector.elementAt(1);
                        if (!ownerName.startsWith("\"")) {
                           ownerName = CustomizeUtil.objectNamesToQuotedIdentifier(ownerName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
                        }

                        if (ownerName.equalsIgnoreCase("DBO")) {
                           ft.setTableName(ownerName);
                        } else {
                           ft.setTableName(ownerName + "." + ownerName);
                        }
                     } else if (count == 3) {
                        ownerName = (String)tokenVector.elementAt(0);
                        ownerName = (String)tokenVector.elementAt(1);
                        tableName = (String)tokenVector.elementAt(2);
                        if (!tableName.startsWith("\"")) {
                           tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
                        }

                        if (ownerName.equalsIgnoreCase("DBO")) {
                           ft.setTableName(tableName);
                        } else {
                           ft.setTableName(ownerName + "." + tableName);
                        }
                     }
                  }
               }

               tokenVector = new Vector();
               aliasForExpr = (String)this.tableName;
               StringTokenizer st = new StringTokenizer(aliasForExpr, ".");

               int count;
               for(count = 0; st.hasMoreTokens(); ++count) {
                  tokenVector.add(st.nextToken());
               }

               String sqlTableName;
               String databaseName;
               if (count == 1 && atIndex != -1) {
                  ownerName = (String)tokenVector.elementAt(0);
                  tableName = ownerName.substring(0, atIndex);
                  sqlTableName = ownerName.substring(atIndex + 1);
                  tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
                  databaseName = sqlTableName + "." + tableName;
                  ft.setTableName(databaseName);
               } else if (count == 2 && atIndex != -1) {
                  ownerName = (String)tokenVector.elementAt(1);
                  int tableAtIndex = ownerName.indexOf("@");
                  sqlTableName = ownerName.substring(0, tableAtIndex);
                  databaseName = ownerName.substring(tableAtIndex + 1);
                  sqlTableName = CustomizeUtil.objectNamesToQuotedIdentifier(sqlTableName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
                  origStr = databaseName + "." + (String)tokenVector.elementAt(0) + "." + sqlTableName;
                  ft.setTableName(origStr);
               }

               ownerName = (String)ft.getTableName();
               tableName = "";
               j = ownerName.indexOf("[");
               if (j == -1) {
                  if (count == 2 && atIndex == -1) {
                     databaseName = tokenVector.get(0).toString();
                     origStr = tokenVector.get(1).toString();
                     if (!databaseName.startsWith("\"") && !origStr.startsWith("\"") && !databaseName.startsWith("'") && !origStr.startsWith("'")) {
                        databaseName = CustomizeUtil.objectNamesToQuotedIdentifier(databaseName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
                        origStr = CustomizeUtil.objectNamesToQuotedIdentifier(origStr, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
                        ft.setTableName(databaseName + "." + origStr);
                     } else {
                        ft.setTableName(ft.getTableName());
                     }
                  } else if (count == 3 && atIndex == -1) {
                     databaseName = tokenVector.get(0).toString();
                     origStr = tokenVector.get(1).toString();
                     origStrBase = tokenVector.get(2).toString();
                     if (!origStr.startsWith("\"") && !origStrBase.startsWith("\"") && !origStr.startsWith("'") && !origStrBase.startsWith("'") && !databaseName.startsWith("\"") && !databaseName.startsWith("'")) {
                        databaseName = CustomizeUtil.objectNamesToQuotedIdentifier(databaseName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
                        origStr = CustomizeUtil.objectNamesToQuotedIdentifier(origStr, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
                        origStrBase = CustomizeUtil.objectNamesToQuotedIdentifier(origStrBase, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
                        ft.setTableName(databaseName + "." + origStr + "." + origStrBase);
                     } else {
                        ft.setTableName(ft.getTableName());
                     }
                  }
               } else {
                  while(j != -1) {
                     if (j == 0) {
                        tableName = ownerName.substring(1);
                        tableName = "\"" + tableName;
                        tableName = StringFunctions.replaceFirst("\"", "]", tableName);
                        ownerName = tableName;
                        j = tableName.indexOf("[");
                     } else {
                        tableName = ownerName.substring(0, j);
                        tableName = tableName + "\"";
                        tableName = tableName + ownerName.substring(j + 1);
                        tableName = StringFunctions.replaceFirst("\"", "]", tableName);
                        ownerName = tableName;
                        j = tableName.indexOf("[");
                     }
                  }

                  ft.setTableName(tableName);
               }
            }
         }
      }

      WhereExpression we;
      if (this.onOrUsingJoin == null && this.joinExpression != null) {
         ft.setOnOrUsingJoin("ON");
         we = ((WhereExpression)this.joinExpression.elementAt(0)).toDB2Select(vembuSQS, vendorSQS);
         selectItemList = new Vector();
         selectItemList.addElement(we);
         ft.setJoinExpression(selectItemList);
      } else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
         this.changeToOnJoin(ft, this.getPreviousFromTableFromTheFromItemList(vendorSQS));
      } else if (this.joinExpression != null) {
         ft.setOnOrUsingJoin(this.onOrUsingJoin);
         we = (WhereExpression)this.joinExpression.elementAt(0);
         selectItemList = we.getOperator();
         we.setOperator(selectItemList);
         Vector v = new Vector();
         WhereExpression teradataWhereExp = we.toTeradataSelect(vembuSQS, vendorSQS);
         if (this.crossJoinExpression != null) {
            teradataWhereExp.addWhereExpression(this.crossJoinExpression);
            teradataWhereExp.addOperator("AND");
         }

         v.addElement(teradataWhereExp);
         ft.setJoinExpression(v);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      if (this.fromClauseOpenBraces != null) {
         ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
         ft.setFromClauseClosedBraces(")");
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         this.aliasName = CustomizeUtil.objectNamesToQuotedIdentifier(this.aliasName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
         ft.setAliasName(this.aliasName);
      } else if (this.tableName instanceof SelectQueryStatement && vendorSQS != null) {
         ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
      }

      ft.setJoinClause(this.joinClause);
      if (this.queryPartitionClause != null) {
         FromTable crossJoinFt = new FromTable();
         SelectQueryStatement crossJoinQuery = new SelectQueryStatement();
         SelectStatement crossJoinSelect = new SelectStatement();
         crossJoinSelect.setSelectClause("SELECT");
         crossJoinSelect.setSelectQualifier("DISTINCT");
         tokenVector = new Vector();

         for(int ki = 0; ki < this.queryPartitionClause.getSelectColumnList().size(); ++ki) {
            if (this.queryPartitionClause.getSelectColumnList().get(ki) instanceof SelectColumn) {
               SelectColumn sc1 = (SelectColumn)this.queryPartitionClause.getSelectColumnList().get(ki);
               if (sc1.getEndsWith() == null && ki < this.queryPartitionClause.getSelectColumnList().size() - 1) {
                  sc1.setEndsWith(",");
               }

               tokenVector.add(sc1.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }
         }

         crossJoinSelect.setSelectItemList(tokenVector);
         crossJoinQuery.setSelectStatement(crossJoinSelect);
         FromClause fc = new FromClause();
         fc.setFromClause("FROM");
         Vector fcFromItems = new Vector();
         FromTable crossJoinTable = new FromTable();
         crossJoinTable.setTableName(ft.getTableName());
         crossJoinTable.setAliasName(ft.getAliasName());
         fcFromItems.add(crossJoinTable);
         fc.setFromItemList(fcFromItems);
         crossJoinQuery.setFromClause(fc);
         if (ft.getAliasName() != null) {
            crossJoinFt.setAliasName(ft.getAliasName() + 1);
         } else if (ft.getTableName() instanceof String) {
            crossJoinFt.setAliasName(ft.getTableName().toString() + 1);
         } else {
            crossJoinFt.setAliasName("cj1");
         }

         crossJoinFt.setTableName(crossJoinQuery);
         crossJoinFt.setJoinClause("CROSS JOIN");
         ft.setQueryPartitionClause(this.queryPartitionClause);
         ft.setCrossJoinForPartitionClause(crossJoinFt);
         WhereExpression crossJoinWhereExp = new WhereExpression();
         Vector toSQSSelectItems = new Vector();
         if (vembuSQS != null) {
            toSQSSelectItems = vembuSQS.getSelectStatement().getSelectItemList();
         }

         for(j = 0; j < tokenVector.size(); ++j) {
            SelectColumn orig = (SelectColumn)tokenVector.get(j);
            origStr = orig.getTheCoreSelectItem().trim();
            origStrBase = origStr;
            if (!origStr.toLowerCase().startsWith("case") && origStr.indexOf(".") != -1 && origStr.indexOf(".") == origStr.lastIndexOf(".") && origStr.indexOf("(") == -1) {
               origStr = origStr.substring(origStr.lastIndexOf(".") + 1);
            }

            if (origStr.length() > 30) {
               origStr = origStr.substring(0, 29);
            }

            if (!origStr.startsWith("\"") && !origStr.endsWith("\"")) {
               origStr = "\"" + origStr + "\"";
            }

            orig.setAliasName(origStr);
            WhereItem crossJoinWhereItem = new WhereItem();
            WhereColumn crossJoinWhereCol = new WhereColumn();
            Vector crossJoinColExp = new Vector();
            TableColumn crossJoinCol = new TableColumn();
            crossJoinCol.setTableName(crossJoinFt.getAliasName());
            crossJoinCol.setColumnName(orig.getAliasName());
            crossJoinColExp.add(crossJoinCol);
            crossJoinWhereCol.setColumnExpression(crossJoinColExp);
            crossJoinWhereItem.setLeftWhereExp(crossJoinWhereCol);

            SelectColumn toSQSCol;
            for(int ji = 0; ji < toSQSSelectItems.size(); ++ji) {
               toSQSCol = (SelectColumn)toSQSSelectItems.get(ji);
               if (origStrBase.equalsIgnoreCase(toSQSCol.getTheCoreSelectItem().trim())) {
                  toSQSCol.setColumnExpression(crossJoinColExp);
                  if (toSQSCol.getAliasName() == null) {
                     toSQSCol.setAliasName(orig.getAliasName());
                  }
               }
            }

            WhereColumn origWhereCol = new WhereColumn();
            toSQSCol = new SelectColumn();
            toSQSCol.setColumnExpression(orig.getColumnExpression());
            Vector origWhereColExp = new Vector();
            origWhereColExp.add(toSQSCol);
            origWhereCol.setColumnExpression(origWhereColExp);
            crossJoinWhereItem.setRightWhereExp(origWhereCol);
            crossJoinWhereExp.addWhereItem(crossJoinWhereItem);
            crossJoinWhereItem.setOperator("=");
            if (j != 0) {
               crossJoinWhereExp.addOperator("AND");
            }
         }

         if (ft.getJoinExpression() != null) {
            ((WhereExpression)ft.getJoinExpression().elementAt(0)).addWhereExpression(crossJoinWhereExp);
            ((WhereExpression)ft.getJoinExpression().elementAt(0)).addOperator("AND");
         } else {
            ft.setCrossJoinExpression(crossJoinWhereExp);
         }
      }

      if (this.crossJoinForPartitionClause != null) {
         ft.setCrossJoinForPartitionClause(this.crossJoinForPartitionClause);
      }

      return ft;
   }

   public FromTable toMSSQLServerSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass(this.commentObj);
      Vector tokenVector;
      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
            throw new ConvertException("Conversion failure..Natural join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
            throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
            throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
            throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
            throw new ConvertException("Conversion failure..Natural right join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         WhereExpression we;
         Vector operatorList;
         if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            we = ((WhereExpression)this.joinExpression.elementAt(0)).toMSSQLServerSelect(vembuSQS, vendorSQS);
            operatorList = new Vector();
            operatorList.addElement(we);
            ft.setJoinExpression(operatorList);
         } else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
            this.changeToOnJoin(ft, this.getPreviousFromTableFromTheFromItemList(vendorSQS));
         } else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            we = (WhereExpression)this.joinExpression.elementAt(0);
            operatorList = we.getOperator();
            we.setOperator(operatorList);
            tokenVector = new Vector();
            tokenVector.addElement(we.toMSSQLServerSelect(vembuSQS, vendorSQS));
            ft.setJoinExpression(tokenVector);
         }
      }

      if (vendorSQS != null && vendorSQS.getForUpdateStatement() != null) {
         ft.setUpdateLock("(UPDLOCK)");
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            if (vendorSQS != null) {
               ((SelectQueryStatement)this.tableName).setMSAzureWareHouseFlag(vendorSQS.isMSAzureWareHouse());
            }

            ft.setTableName(((SelectQueryStatement)this.tableName).toMSSQLServer());
         } else if (this.tableName instanceof FunctionCalls) {
            if (this.tableKeyword != null) {
               FunctionCalls fn = ((FunctionCalls)this.tableName).toMSSQLServerSelect(vembuSQS, vendorSQS);
               if (fn != null && fn.toString().indexOf("CAST") != -1) {
                  ft.setTableKeyword("TABLE(");
               }

               ft.setTableName(fn);
            } else {
               ft.setTableName(((FunctionCalls)this.tableName).toMSSQLServerSelect(vembuSQS, vendorSQS));
            }
         } else if (this.tableName instanceof WithStatement) {
            ft.setTableName(((WithStatement)this.tableName).toMSSQLServer());
         } else if (this.tableName instanceof FromClause) {
            ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
            ft.setTableName(((FromClause)this.tableName).toMSSQLServerSelect(vembuSQS, vendorSQS));
            ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
         } else {
            String table_Name_String = (String)this.tableName;
            int atIndex = table_Name_String.indexOf("@");
            if (((String)this.tableName).indexOf("..") < 0) {
               if (SwisSQLOptions.removeDBSchemaQualifier) {
                  ft.setTableName(table_Name_String.substring(table_Name_String.lastIndexOf(".") + 1));
               } else {
                  ft.setTableName((String)this.tableName);
               }
            } else {
               ft.setTableName((String)this.tableName);
            }

            tokenVector = new Vector();
            String table_Name = (String)this.tableName;
            StringTokenizer st = new StringTokenizer(table_Name, ".");

            int count;
            for(count = 0; st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            String dataBaseTableName;
            String sqlTableName;
            String sqlDataBaseName;
            if (count == 1 && atIndex != -1 && atIndex != 0) {
               dataBaseTableName = (String)tokenVector.elementAt(0);
               String sqlTableName = dataBaseTableName.substring(0, atIndex);
               sqlTableName = dataBaseTableName.substring(atIndex + 1);
               sqlDataBaseName = sqlTableName + ".." + sqlTableName;
               ft.setTableName(sqlDataBaseName);
            } else if (count == 2 && atIndex != -1) {
               dataBaseTableName = (String)tokenVector.elementAt(1);
               int tableAtIndex = dataBaseTableName.indexOf("@");
               sqlTableName = dataBaseTableName.substring(0, tableAtIndex);
               sqlDataBaseName = dataBaseTableName.substring(tableAtIndex + 1);
               String sqlDataBaseAndTableName = sqlDataBaseName + "." + (String)tokenVector.elementAt(0) + "." + sqlTableName;
               ft.setTableName(sqlDataBaseAndTableName);
            }
         }
      }

      if (!this.setOperatorClauseListForSubQuery.isEmpty()) {
         ArrayList setOperatorClauseList = new ArrayList();
         Iterator var19 = this.setOperatorClauseListForSubQuery.iterator();

         while(var19.hasNext()) {
            Object soc = var19.next();
            setOperatorClauseList.add(((SetOperatorClause)soc).toMSSQLServerSelect(vembuSQS, vendorSQS));
         }

         ft.setSetOperatorClauseListForSubQuery(setOperatorClauseList);
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         if (this.aliasName.charAt(0) == '\'') {
            ft.setAliasName(this.aliasName.replace('\'', '"'));
         } else if (this.aliasName.equalsIgnoreCase("noholdlock")) {
            if (ft.getUpdateLock() == null) {
               ft.setAliasName("WITH(NOLOCK)");
            } else {
               ft.setAliasName("WITH(NOLOCK, UPDLOCK)");
               ft.setUpdateLock((String)null);
            }
         } else if (this.aliasName.equalsIgnoreCase("holdlock")) {
            if (ft.getUpdateLock() == null) {
               ft.setAliasName("WITH(HOLDLOCK)");
            } else {
               ft.setAliasName("WITH(HOLDLOCK, UPDLOCK)");
               ft.setUpdateLock((String)null);
            }
         } else {
            ft.setAliasName(this.aliasName);
         }
      } else if (this.tableName instanceof SelectQueryStatement && this.setOperatorClauseListForSubQuery.isEmpty()) {
         ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
      }

      if (this.holdLock != null) {
         if (this.holdLock.equalsIgnoreCase("noholdlock")) {
            if (ft.getUpdateLock() == null) {
               ft.setHoldLock("WITH(NOLOCK)");
            } else {
               ft.setHoldLock("WITH(NOLOCK, UPDLOCK)");
               ft.setUpdateLock((String)null);
            }
         } else if (this.holdLock.equalsIgnoreCase("holdlock")) {
            if (ft.getUpdateLock() == null) {
               ft.setHoldLock("WITH(HOLDLOCK)");
            } else {
               ft.setHoldLock("WITH(HOLDLOCK, UPDLOCK)");
               ft.setUpdateLock((String)null);
            }
         }
      }

      if (this.joinClause != null && this.joinClause.equalsIgnoreCase("JOIN")) {
         ft.setJoinClause(" INNER " + this.joinClause);
      } else {
         ft.setJoinClause(this.joinClause);
      }

      if (this.indexHint != null) {
         if (this.indexHint.trim().startsWith("WITH")) {
            ft.setIndexHint(this.indexHint);
         } else if (ft.getUpdateLock() == null) {
            ft.setIndexHint(" WITH(index(" + this.indexHint + "))");
         } else {
            ft.setIndexHint(" WITH(index(" + this.indexHint + "), UPDLOCK)");
            ft.setUpdateLock((String)null);
         }
      }

      return ft;
   }

   public FromTable toSnowflakeSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass((CommentClass)null);
      int startIndex;
      int atIndex;
      Vector tokenVector;
      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         WhereExpression we;
         Vector operatorList;
         if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            we = ((WhereExpression)this.joinExpression.elementAt(0)).toSnowflakeSelect(vembuSQS, vendorSQS);
            operatorList = new Vector();
            operatorList.addElement(we);
            ft.setJoinExpression(operatorList);
         } else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            we = (WhereExpression)this.joinExpression.elementAt(0);
            operatorList = we.getOperator();

            for(startIndex = 0; startIndex < operatorList.size(); ++startIndex) {
               if (operatorList.elementAt(startIndex) instanceof String && ((String)operatorList.get(startIndex)).equalsIgnoreCase("AND")) {
                  operatorList.setElementAt("AND", startIndex);
               }
            }

            we.setOperator(operatorList);
            we = we.toSnowflakeSelect(vembuSQS, vendorSQS);
            tokenVector = new Vector();
            tokenVector.addElement(we);
            ft.setJoinExpression(tokenVector);
         }

         ft.setOnOrUsingJoin(this.onOrUsingJoin);
         if (this.UsingList != null) {
            Vector v = new Vector();

            for(atIndex = 0; atIndex < this.UsingList.size(); ++atIndex) {
               if (this.UsingList.elementAt(atIndex) instanceof TableColumn) {
                  v.addElement(((TableColumn)this.UsingList.elementAt(atIndex)).toSnowflakeSelect(vembuSQS, vendorSQS));
               } else {
                  v.addElement(this.UsingList.elementAt(atIndex));
               }
            }

            ft.setUsingList(v);
         }

         this.removeJoinExpressionForCrossJoin(ft);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            ((SelectQueryStatement)this.tableName).setReportsMeta(vendorSQS == null ? false : vendorSQS.getReportsMeta());
            ((SelectQueryStatement)this.tableName).setAmazonRedShiftFlag(vendorSQS.isAmazonRedShift());
            ((SelectQueryStatement)this.tableName).setPgsqlFlag(vendorSQS.isPgsqlLive());
            ((SelectQueryStatement)this.tableName).setVerticaFlag(vendorSQS.isVerticaDb());
            ((SelectQueryStatement)this.tableName).setSnowflakeFlag(vendorSQS.isSnowflake());
            ((SelectQueryStatement)this.tableName).setMSAzureFlag(vendorSQS.isMSAzure());
            ((SelectQueryStatement)this.tableName).setMSAzureWareHouseFlag(vendorSQS.isMSAzureWareHouse());
            ((SelectQueryStatement)this.tableName).setOracleLiveFlag(vendorSQS.isOracleLive());
            ((SelectQueryStatement)this.tableName).setDenodoFlag(vendorSQS.isDenodo());
            ((SelectQueryStatement)this.tableName).setMySqlLiveFlag(vendorSQS.isMySqlLive());
            ((SelectQueryStatement)this.tableName).setMongoDbFlag(vendorSQS.isMongoDb());
            ((SelectQueryStatement)this.tableName).setQueryConversionPropHandler(vendorSQS.getQueryConvPropHandler());
            ft.setTableName(((SelectQueryStatement)this.tableName).toSnowflake());
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException("Function calls in the place of Table name yet to be supported");
            }

            if (this.tableName instanceof WithStatement) {
               ft.setTableName(((WithStatement)this.tableName).toSnowflake());
            } else if (this.tableName instanceof FromClause) {
               ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
               ft.setTableName(((FromClause)this.tableName).toSnowflakeSelect(vembuSQS, vendorSQS));
               ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
            } else {
               String tempTableName;
               if (((String)this.tableName).indexOf("[") != 0) {
                  ft.setTableName(this.tableName);
               } else {
                  tempTableName = (String)this.tableName;
                  String tableNameSubString = "";
                  startIndex = tempTableName.indexOf("[");
                  if (startIndex != -1) {
                     while(startIndex != -1) {
                        if (startIndex == 0) {
                           tableNameSubString = tempTableName.substring(1);
                           tableNameSubString = "\"" + tableNameSubString;
                           tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                           tempTableName = tableNameSubString;
                           startIndex = tableNameSubString.indexOf("[");
                        } else {
                           tableNameSubString = tempTableName.substring(0, startIndex);
                           tableNameSubString = tableNameSubString + "\"";
                           tableNameSubString = tableNameSubString + tempTableName.substring(startIndex + 1);
                           tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                           tempTableName = tableNameSubString;
                           startIndex = tableNameSubString.indexOf("[");
                        }
                     }

                     ft.setTableName(tableNameSubString);
                  }
               }

               tempTableName = (String)this.tableName;
               atIndex = tempTableName.indexOf("@");
               tokenVector = new Vector();
               String table_Name = (String)this.tableName;
               StringTokenizer st = new StringTokenizer(table_Name, ".");

               int count;
               for(count = 0; st.hasMoreTokens(); ++count) {
                  tokenVector.add(st.nextToken());
               }

               String tempTableName;
               String tableNameSubString;
               if (count == 1 && atIndex != -1) {
                  tempTableName = (String)tokenVector.elementAt(0);
                  tableNameSubString = tempTableName.substring(0, atIndex);
                  tempTableName.substring(atIndex + 1);
                  ft.setTableName(tableNameSubString);
               }

               tempTableName = (String)ft.getTableName();
               tableNameSubString = "";
               int startIndex = tempTableName.indexOf("[");
               if (startIndex != -1) {
                  if (startIndex == 0) {
                     tableNameSubString = tempTableName.substring(1);
                     tableNameSubString = "\"" + tableNameSubString;
                     tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                     tempTableName = tableNameSubString;
                  }

                  ft.setTableName(tempTableName);
               }

               String tblName = (String)ft.getTableName();
               if (tblName.charAt(0) == '\'') {
                  ft.setTableName(tblName.replace('\'', '"'));
               } else if (tblName.charAt(0) == '`') {
                  ft.setTableName(tblName.replace('`', '"'));
               } else {
                  ft.setTableName(tblName);
               }

               tblName = (String)ft.getTableName();
               ft.setTableName(tblName);
            }
         }
      }

      if (this.setOperatorClauseListForSubQuery.size() != 0) {
         ArrayList setOperatorClauseList = new ArrayList();

         for(atIndex = 0; atIndex < this.setOperatorClauseListForSubQuery.size(); ++atIndex) {
            setOperatorClauseList.add(((SetOperatorClause)this.setOperatorClauseListForSubQuery.get(atIndex)).toSnowflakeSelect(vembuSQS, vendorSQS));
         }

         ft.setSetOperatorClauseListForSubQuery(setOperatorClauseList);
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         if (this.aliasName.charAt(0) == '\'') {
            ft.setAliasName(this.aliasName.replace('\'', ' '));
         } else if (this.aliasName.charAt(0) == '`') {
            ft.setAliasName(this.aliasName.replace('`', ' '));
         } else if (this.aliasName.charAt(0) == '"') {
            ft.setAliasName(this.aliasName.replace('"', ' '));
         } else {
            ft.setAliasName(this.aliasName);
         }

         ft.setAliasName(ft.getAliasName());
      } else if (this.tableName instanceof SelectQueryStatement && this.setOperatorClauseListForSubQuery.size() == 0) {
         SelectQueryStatement sqs = (SelectQueryStatement)this.tableName;
         if (sqs.getSetOperatorClause() != null) {
            SetOperatorClause soc = sqs.getSetOperatorClause();
            if (soc == null || soc.getSetClause() == null || !soc.getSetClause().toUpperCase().startsWith("UNION") && !soc.getSetClause().toUpperCase().startsWith("MINUS") && !soc.getSetClause().toUpperCase().startsWith("INTERSECT") && !soc.getSetClause().toUpperCase().startsWith("EXCEPT")) {
               ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
            }
         } else {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
         }
      }

      if (this.joinClause != null && this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
         ft.setOnOrUsingJoin((String)null);
      }

      ft.setJoinClause(this.joinClause);
      return ft;
   }

   public FromTable toSybaseSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass(this.commentObj);
      ft.setObjectContext(this.context);
      boolean isFullJoin = false;
      Vector tokenVector;
      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
            throw new ConvertException("Conversion failure..Natural join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
            throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
            throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
            throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
            throw new ConvertException("Conversion failure..Natural right join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         if (this.joinClause.trim().equalsIgnoreCase("CROSS JOIN")) {
            this.joinClause = null;
         } else if (this.joinClause.trim().equalsIgnoreCase("NATURAL INNER JOIN")) {
            this.joinClause = "INNER JOIN";
         } else if (this.joinClause.trim().equalsIgnoreCase("FULL JOIN") || this.joinClause.trim().equalsIgnoreCase("FULL OUTER JOIN")) {
            FromClause fc = new FromClause();
            fc.setObjectContext(this.context);
            FromTable newFromTable = new FromTable();
            newFromTable.setAliasName(this.aliasName);
            newFromTable.setObjectContext(this.context);
            newFromTable.setIsAS(this.isAS);
            newFromTable.setJoinClause("RIGHT OUTER JOIN");
            newFromTable.setJoinExpression(this.joinExpression);
            newFromTable.setOnOrUsingJoin(this.onOrUsingJoin);
            newFromTable.setTableKeyword(this.tableKeyword);
            newFromTable.setTableName(this.tableName);
            newFromTable.setUsingList(this.UsingList);
            if (vendorSQS.getFromClause() != null) {
               fc.setFromClause("FROM");
               tokenVector = vendorSQS.getFromClause().getFromItemList();
               Vector newFromList = new Vector();

               for(int i = 0; i < tokenVector.size(); ++i) {
                  if (tokenVector.get(i) instanceof FromTable) {
                     FromTable getFT = (FromTable)tokenVector.get(i);
                     if (getFT != null && getFT.equals(this)) {
                        newFromList.add(newFromTable);
                     } else if (tokenVector.get(i) instanceof FromTable) {
                        newFromList.add(((FromTable)tokenVector.get(i)).toSybaseSelect(vembuSQS, vendorSQS));
                     }
                  } else if (tokenVector.get(i) instanceof FromClause) {
                     ((FromClause)tokenVector.get(i)).toSybaseSelect(vembuSQS, vendorSQS);
                  }
               }

               fc.setFromItemList(newFromList);
            }

            SelectQueryStatement sqs = new SelectQueryStatement();
            sqs.setObjectContext(this.context);
            sqs.setFromClause(fc);
            if (vendorSQS.getSelectStatement() != null) {
               sqs.setSelectStatement(vendorSQS.getSelectStatement().toSybaseSelect(vembuSQS, vendorSQS));
            }

            if (vendorSQS.getFetchClause() != null) {
               sqs.setFetchClause(vendorSQS.getFetchClause().toSybaseSelect(vembuSQS, vendorSQS));
            }

            if (vendorSQS.getForUpdateStatement() != null) {
               sqs.setForUpdateStatement(vendorSQS.getForUpdateStatement().toSybaseSelect(vembuSQS, vendorSQS));
            }

            if (vendorSQS.getGroupByStatement() != null) {
               sqs.setGroupByStatement(vendorSQS.getGroupByStatement().toSybaseSelect(vembuSQS, vendorSQS));
            }

            if (vendorSQS.getHavingStatement() != null) {
               sqs.setHavingStatement(vendorSQS.getHavingStatement().toSybaseSelect(vembuSQS, vendorSQS));
            }

            if (vendorSQS.getHierarchicalQueryClause() != null) {
               sqs.setHierarchicalQueryClause(vendorSQS.getHierarchicalQueryClause().toSybaseSelect(vembuSQS, vendorSQS));
            }

            if (vendorSQS.getLimitClause() != null) {
               sqs.setLimitClause(vendorSQS.getLimitClause().toSybaseSelect(vembuSQS, vendorSQS));
            }

            if (vendorSQS.getSetOperatorClause() != null) {
               sqs.setSetOperatorClause(vendorSQS.getSetOperatorClause().toSybaseSelect(vembuSQS, vendorSQS));
            }

            if (vendorSQS.getWhereExpression() != null) {
               sqs.setWhereExpression(vendorSQS.getWhereExpression().toSybaseSelect(vembuSQS, vendorSQS));
            }

            this.setOperatorClauseForFullJoin = new SetOperatorClause();
            this.setOperatorClauseForFullJoin.setSelectQueryStatement(sqs);
            this.setOperatorClauseForFullJoin.setSetClause("UNION");
            this.setOperatorClauseForFullJoin.setObjectContext(this.context);
            if (vendorSQS.getWhereExpression() != null) {
               this.setOperatorClauseForFullJoin.setWhereExpression(vendorSQS.getWhereExpression().toSybaseSelect(vembuSQS, vendorSQS));
               vendorSQS.setWhereExpression((WhereExpression)null);
            }

            ft.setSetOperatorClause(this.setOperatorClauseForFullJoin);
            isFullJoin = true;
         }

         WhereExpression we;
         Vector operatorList;
         if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            we = ((WhereExpression)this.joinExpression.elementAt(0)).toSybaseSelect(vembuSQS, vendorSQS);
            operatorList = new Vector();
            operatorList.addElement(we);
            ft.setJoinExpression(operatorList);
         } else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
            this.changeToOnJoin(ft, this.getPreviousFromTableFromTheFromItemList(vendorSQS));
         } else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            we = (WhereExpression)this.joinExpression.elementAt(0);
            operatorList = we.getOperator();

            for(int i = 0; i < operatorList.size(); ++i) {
               if (operatorList.elementAt(i) instanceof String && ((String)operatorList.get(i)).equalsIgnoreCase("AND") && !isFullJoin) {
                  operatorList.setElementAt("AND", i);
               }
            }

            we.setOperator(operatorList);
            we.toSybaseSelect(vembuSQS, vendorSQS);
            tokenVector = new Vector();
            tokenVector.addElement(we);
            ft.setJoinExpression(tokenVector);
         }
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            ft.setTableName(((SelectQueryStatement)this.tableName).toSybase());
         } else if (this.tableName instanceof FunctionCalls) {
            ft.setTableName(((FunctionCalls)this.tableName).toSybaseSelect(vembuSQS, vendorSQS));
         } else if (this.tableName instanceof WithStatement) {
            ft.setTableName(((WithStatement)this.tableName).toSybase());
         } else if (this.tableName instanceof FromClause) {
            ft.setTableName(((FromClause)this.tableName).toSybaseSelect(vembuSQS, vendorSQS));
         } else {
            String table_Name_String = (String)this.tableName;
            int atIndex = table_Name_String.indexOf("@");
            if (((String)this.tableName).indexOf("..") < 0) {
               new StringTokenizer((String)this.tableName, ".");
               ft.setTableName((String)this.tableName);
            } else {
               ft.setTableName((String)this.tableName);
            }

            tokenVector = new Vector();
            String table_Name = (String)this.tableName;
            StringTokenizer st = new StringTokenizer(table_Name, ".");

            int count;
            for(count = 0; st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            String tempTableName;
            String sqlTableName;
            String sqlDataBaseName;
            String tableNameSubString;
            if (count == 1 && atIndex != -1 && atIndex != 0) {
               tempTableName = (String)tokenVector.elementAt(0);
               tableNameSubString = tempTableName.substring(0, atIndex);
               sqlTableName = tempTableName.substring(atIndex + 1);
               sqlDataBaseName = sqlTableName + ".." + tableNameSubString;
               ft.setTableName(sqlDataBaseName);
            } else if (count == 2 && atIndex != -1) {
               tempTableName = (String)tokenVector.elementAt(1);
               int tableAtIndex = tempTableName.indexOf("@");
               sqlTableName = tempTableName;
               if (tableAtIndex != -1) {
                  sqlTableName = tempTableName.substring(0, tableAtIndex);
               }

               sqlDataBaseName = tempTableName.substring(tableAtIndex + 1);
               String sqlDataBaseAndTableName = sqlDataBaseName + "." + (String)tokenVector.elementAt(0) + "." + sqlTableName;
               ft.setTableName(sqlDataBaseAndTableName);
            }

            tempTableName = (String)ft.getTableName();
            tableNameSubString = "";
            int startIndex = tempTableName.indexOf("[");
            if (startIndex != -1) {
               while(startIndex != -1) {
                  if (startIndex == 0) {
                     tableNameSubString = tempTableName.substring(1);
                     tableNameSubString = "\"" + tableNameSubString;
                     tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                     tempTableName = tableNameSubString;
                     startIndex = tableNameSubString.indexOf("[");
                  } else {
                     tableNameSubString = tempTableName.substring(0, startIndex);
                     tableNameSubString = tableNameSubString + "\"";
                     tableNameSubString = tableNameSubString + tempTableName.substring(startIndex + 1);
                     tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                     tempTableName = tableNameSubString;
                     startIndex = tableNameSubString.indexOf("[");
                  }
               }

               ft.setTableName(tableNameSubString);
            }
         }
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         if (this.aliasName.charAt(0) == '\'') {
            ft.setAliasName(this.aliasName.replace('\'', '"'));
         } else {
            ft.setAliasName(this.aliasName);
         }
      } else if (this.tableName instanceof SelectQueryStatement) {
         ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
      }

      if (!isFullJoin) {
         ft.setJoinClause(this.joinClause);
      } else {
         ft.setJoinClause("LEFT OUTER JOIN");
      }

      if (this.indexHint != null) {
         ft.setIndexHint(" ( index " + this.indexHint + ")");
      }

      return ft;
   }

   public void changeToOnJoin(FromTable to_ft, FromTable from_ft) throws ConvertException {
      int i = false;
      String s_cn = null;
      if (from_ft != null) {
         if (from_ft.getAliasName() != null) {
            s_cn = from_ft.getAliasName();
         } else {
            s_cn = from_ft.getTableName().toString();
         }
      }

      Vector v_ul = new Vector();
      v_ul.addElement("(");

      for(int i = 0; i < this.UsingList.size(); ++i) {
         if (this.UsingList.elementAt(i) instanceof TableColumn) {
            new Vector();
            TableColumn tc = (TableColumn)this.UsingList.elementAt(i);
            tc.setTableName(s_cn);
            v_ul.addElement(tc);
            v_ul.addElement("=");
            TableColumn tcr = new TableColumn();
            tcr.setColumnName(((TableColumn)this.UsingList.elementAt(i)).getColumnName());
            if (this.aliasName != null) {
               tcr.setTableName(this.aliasName);
            } else {
               tcr.setTableName(this.tableName.toString());
            }

            v_ul.addElement(tcr);
         } else {
            String s_cd = (String)this.UsingList.elementAt(i);
            if (s_cd.equals(",")) {
               v_ul.addElement("AND");
            } else {
               v_ul.addElement(s_cd);
            }
         }
      }

      v_ul.addElement(")");
      to_ft.setJoinExpression(v_ul);
      to_ft.setOnOrUsingJoin("ON");
   }

   public Object clone() {
      FromTable ft = new FromTable();
      ft.setObjectContext(this.context);
      ft.setIndexHint(this.indexHint);
      ft.setSetOperatorClause(this.setOperatorClauseForFullJoin);
      ft.setTableName(this.tableName);
      ft.setAliasName(this.aliasName);
      ft.setIsAS(this.isAS);
      ft.setJoinClause(this.joinClause);
      ft.setOnOrUsingJoin(this.onOrUsingJoin);
      ft.setJoinExpression(this.joinExpression);
      ft.setUsingList(this.UsingList);
      ft.setLockTableStatement(this.lockTableStatement);
      ft.setWith(this.with);
      ft.setLock(this.lock);
      ft.setTableKeyword(this.tableKeyword);
      ft.setOuter(this.outer);
      ft.setOuterOpenBrace(this.outerOpenBrace);
      ft.setOuterClosedBrace(this.outerClosedBrace);
      ft.setUpdateLock(this.updateLock);
      ft.setHoldLock(this.holdLock);
      ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
      ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
      ft.setFromClause(this.fc);
      ft.setOrigTableName(this.origTableName);
      ft.setCommentClass(this.commentObj);
      return ft;
   }

   private WhereExpression getClonedWhereExpression(WhereExpression whereExpression) {
      WhereExpression clonedWhereExpression = new WhereExpression();
      clonedWhereExpression.setOpenBrace(whereExpression.getOpenBrace());
      clonedWhereExpression.setCloseBrace(whereExpression.getCloseBrace());
      clonedWhereExpression.setOperator(whereExpression.getOperator());
      Vector whereItemList = new Vector();
      Vector whereItems = whereExpression.getWhereItems();
      new Vector();
      clonedWhereExpression.setOperator((Vector)whereExpression.getOperator().clone());

      for(int i = 0; i < whereItems.size(); ++i) {
         if (whereItems.elementAt(i) instanceof WhereItem) {
            WhereItem whereItem = (WhereItem)((WhereItem)whereItems.elementAt(i)).clone();
            whereItemList.addElement(whereItem);
         } else if (whereItems.elementAt(i) instanceof WhereExpression) {
            whereItemList.addElement(this.getClonedWhereExpression((WhereExpression)whereItems.elementAt(i)));
         }
      }

      clonedWhereExpression.setWhereItem(whereItemList);
      return clonedWhereExpression;
   }

   private Vector getTablesOnlyList(Vector vendorList) {
      Vector vembuList = new Vector();

      for(int i = 0; i < vendorList.size(); ++i) {
         if (vendorList.elementAt(i) instanceof FromTable) {
            FromTable ft = (FromTable)vendorList.get(i);
            FromTable fromTable = new FromTable();
            fromTable.setTableName(ft.getTableName());
            fromTable.setAliasName(ft.getAliasName());
            fromTable.setJoinClause((String)null);
            fromTable.setJoinExpression((Vector)null);
            fromTable.setOnOrUsingJoin((String)null);
            fromTable.setUsingList((Vector)null);
            vembuList.addElement(fromTable);
         } else if (vendorList.elementAt(i) instanceof FromClause) {
            FromClause newFC = (FromClause)vendorList.get(i);
            Vector newFromItemsList = newFC.getFromItemList();
            this.getTablesOnlyList(newFromItemsList);
         }
      }

      return vembuList;
   }

   public FromTable toOracleSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      boolean isdenodo = vendorSQS != null && vendorSQS.isDenodo();
      FromTable ft = new FromTable();
      ft.setCommentClass(this.commentObj);
      int dotDotIndex;
      Vector weOperators;
      int i;
      String tempTableName;
      SelectQueryStatement sqs;
      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            sqs = (SelectQueryStatement)this.tableName;
            if (vendorSQS != null) {
               sqs.setDenodoFlag(vendorSQS.isDenodo());
               sqs.setOracleLiveFlag(vendorSQS.isOracleLive());
               sqs.setCanUseOracleFetch(vendorSQS.getCanUseOracleFetch());
            }

            if (isdenodo) {
               ((SelectQueryStatement)this.tableName).setLimitClause((LimitClause)null);
            }

            ft.setTableName(sqs.toOracle());
         } else if (this.tableName instanceof FunctionCalls) {
            if (this.tableKeyword != null) {
               FunctionCalls fn = ((FunctionCalls)this.tableName).toOracleSelect(vembuSQS, vendorSQS);
               if (fn != null && fn.toString().indexOf("CAST") != -1) {
                  ft.setTableKeyword("TABLE(");
               }

               ft.setTableName(fn);
            } else if (this.tableName.toString().toUpperCase().indexOf("(NOLOCK)") == -1) {
               ft.setTableKeyword("TABLE(");
               ft.setTableName(((FunctionCalls)this.tableName).toOracleSelect(vembuSQS, vendorSQS));
            } else {
               ft.setTableName(((FunctionCalls)this.tableName).getFunctionNameAsAString());
            }
         } else if (this.tableName instanceof WithStatement) {
            ft.setTableName(((WithStatement)this.tableName).toOracle());
         } else if (this.tableName instanceof FromClause) {
            FromClause fromCl = (FromClause)this.tableName;
            ft.setTableName(fromCl.toOracleSelect(vembuSQS, vendorSQS));
         } else if (this.tableName instanceof ValuesClause && this.columnAliasList != null) {
            ValuesClause vc = (ValuesClause)this.tableName;
            this.convertTableValueConstructorToSelectUnion(vc, this.columnAliasList, vembuSQS, vendorSQS);
            ft.setTableName("DUAL");
         } else {
            String table_Name_String = (String)this.tableName;
            dotDotIndex = table_Name_String.indexOf("..");
            String table_name = StringFunctions.replaceFirst(".", "..", (String)this.tableName);
            if (!(ft.getTableName() instanceof SelectQueryStatement)) {
               ft.setTableName(table_name);
            }

            weOperators = new Vector();
            StringTokenizer st = new StringTokenizer(table_name, ".");

            for(i = 0; st.hasMoreTokens(); ++i) {
               weOperators.add(st.nextToken());
            }

            boolean ownerExistsInSrc = false;
            if (i == 3) {
               weOperators.remove(1);
               i = 2;
               ownerExistsInSrc = true;
            }

            String tableNameSubString;
            if (i == 1) {
               ft.setTableName(CustomizeUtil.objectNamesToQuotedIdentifier((String)ft.getTableName(), SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1));
            } else {
               String oracleDataBaseAndTableName;
               if (i == 2) {
                  tempTableName = (String)weOperators.elementAt(0);
                  tableNameSubString = (String)weOperators.elementAt(1);
                  oracleDataBaseAndTableName = "";
                  tableNameSubString = CustomizeUtil.objectNamesToQuotedIdentifier(tableNameSubString, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
                  if (tempTableName.equalsIgnoreCase("DBO") && this.isTenroxRequirement) {
                     oracleDataBaseAndTableName = "PUSER." + tableNameSubString;
                  } else if (tempTableName.equalsIgnoreCase("DBO") || tempTableName.equalsIgnoreCase("tempdb") && (ownerExistsInSrc || dotDotIndex != -1)) {
                     oracleDataBaseAndTableName = tableNameSubString;
                  } else if (SwisSQLOptions.removeOracleSchemaQualifier) {
                     oracleDataBaseAndTableName = tableNameSubString;
                  } else {
                     oracleDataBaseAndTableName = tempTableName + "." + tableNameSubString;
                  }

                  ft.setTableName(oracleDataBaseAndTableName);
               } else if (i == 4) {
                  tempTableName = (String)weOperators.elementAt(0);
                  tableNameSubString = (String)weOperators.elementAt(1);
                  oracleDataBaseAndTableName = (String)weOperators.elementAt(3);
                  oracleDataBaseAndTableName = CustomizeUtil.objectNamesToQuotedIdentifier(oracleDataBaseAndTableName, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
                  ft.setTableName(tableNameSubString + "." + oracleDataBaseAndTableName + "@" + tempTableName);
               }
            }

            tempTableName = (String)ft.getTableName();
            tableNameSubString = "";
            int startIndex = tempTableName.indexOf("[");
            String tt;
            if (startIndex != -1) {
               while(true) {
                  if (startIndex == -1) {
                     ft.setTableName(tableNameSubString);
                     break;
                  }

                  if (startIndex != 0) {
                     tableNameSubString = tempTableName.substring(0, startIndex);
                     tt = tempTableName.substring(startIndex + 1);
                     int endIndex = tt.indexOf("]");
                     String token = tt.substring(0, endIndex);
                     if (!SwisSQLOptions.retainQuotedIdentifierForOracle && token.indexOf(" ") == -1) {
                        tableNameSubString = tableNameSubString + token + tt.substring(endIndex + 1);
                     } else {
                        tableNameSubString = tableNameSubString + "\"" + token + "\"" + tt.substring(endIndex + 1);
                     }
                  } else {
                     tableNameSubString = tempTableName.substring(1);
                     int endIndex = tableNameSubString.indexOf("]");
                     String temp = tableNameSubString.substring(0, endIndex);
                     if (SwisSQLOptions.retainQuotedIdentifierForOracle || temp.indexOf(" ") != -1) {
                        temp = "\"" + temp + "\"";
                     }

                     tableNameSubString = temp + tableNameSubString.substring(endIndex + 1);
                  }

                  tempTableName = tableNameSubString;
                  startIndex = tableNameSubString.indexOf("[");
               }
            }

            tt = (String)ft.getTableName();
            if (tt.startsWith("#")) {
               tt = tt.substring(1);
            }

            if (tt.startsWith("#")) {
               tt = tt.substring(1);
            }

            if (tt.startsWith("@")) {
               tt = "\"" + tt + "\"";
            }

            ft.setTableName(tt);
            if (this.with != null && this.lock != null) {
               ft.setLock(this.lock);
               if (this.lock.equalsIgnoreCase("TABLOCK") || this.lock.equalsIgnoreCase("UPDLOCK")) {
                  this.lock = "SHARE";
               }

               if (this.lock.equalsIgnoreCase("TABLOCKX")) {
                  this.lock = "EXCLUSIVE";
               }

               if (!this.lock.equalsIgnoreCase("NOLOCK") && !this.lock.equalsIgnoreCase("ROWLOCK") && !this.lock.equalsIgnoreCase("XLOCK")) {
                  this.lockTableStatement = "LOCK TABLE " + tt + " IN " + this.lock + " MODE;";
                  if (SwisSQLOptions.handleLOCK_HINTSforOracle) {
                     ft.setLockTableStatement(this.lockTableStatement);
                  }
               }

               if (this.lock.equalsIgnoreCase("HOLDLOCK")) {
                  ft.setLockTableStatement((String)null);
                  ForUpdateStatement forUpdateStmt = new ForUpdateStatement();
                  forUpdateStmt.setForUpdateClause("FOR UPDATE");
                  forUpdateStmt.setForUpdateQualifier("OF");
                  forUpdateStmt.setNoWaitQualifier("NOWAIT");
                  if (SwisSQLOptions.handleLOCK_HINTSforOracle) {
                     vembuSQS.setForUpdateStatement(forUpdateStmt);
                  }
               }
            }
         }

         if (this.tableName.toString().startsWith("@")) {
            this.tableName = "\"" + this.tableName + "\"";
         }
      }

      if (this.origTableName != null) {
         ft.setOrigTableName(this.origTableName);
      }

      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
            throw new ConvertException(" Natural join are not allowed in Oracle");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
            throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
            throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
            throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
            throw new ConvertException("Conversion failure..Natural right join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         Vector newJoinExpression;
         WhereItem wit;
         if (SwisSQLAPI.ANSIJOIN_ForOracle) {
            ft.setJoinClause(this.joinClause);
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            ft.setUsingList(this.UsingList);
            newJoinExpression = new Vector();
            if (this.joinExpression != null) {
               for(dotDotIndex = 0; dotDotIndex < this.joinExpression.size(); ++dotDotIndex) {
                  Object o = this.joinExpression.get(dotDotIndex);
                  if (o instanceof String) {
                     newJoinExpression.add(o);
                  } else if (o instanceof FunctionCalls) {
                     FunctionCalls fc = (FunctionCalls)o;
                     newJoinExpression.add(fc.toOracleSelect(vembuSQS, vendorSQS));
                  } else if (o instanceof SelectQueryStatement) {
                     SelectQueryStatement sqsNew = (SelectQueryStatement)o;
                     newJoinExpression.add(sqsNew.toOracle());
                  } else if (o instanceof FromClause) {
                     FromClause fcl = (FromClause)o;
                     newJoinExpression.add(fcl.toOracleSelect(vembuSQS, vendorSQS));
                  } else if (o instanceof WhereExpression) {
                     WhereExpression wexp = (WhereExpression)o;
                     newJoinExpression.add(wexp.toOracleSelect(vembuSQS, vendorSQS));
                  } else if (o instanceof FromTable) {
                     FromTable ftNew = (FromTable)o;
                     newJoinExpression.add(ftNew.toOracleSelect(vembuSQS, vendorSQS));
                  } else if (o instanceof TableColumn) {
                     TableColumn tcl = (TableColumn)o;
                     newJoinExpression.add(tcl.toOracleSelect(vembuSQS, vendorSQS));
                  } else if (o instanceof WhereItem) {
                     wit = (WhereItem)o;
                     newJoinExpression.add(wit.toOracleSelect(vembuSQS, vendorSQS));
                  } else if (o instanceof SelectColumn) {
                     SelectColumn scl = (SelectColumn)o;
                     newJoinExpression.add(scl.toOracleSelect(vembuSQS, vendorSQS));
                  } else if (o instanceof WhereColumn) {
                     WhereColumn wcl = (WhereColumn)o;
                     newJoinExpression.add(wcl.toOracleSelect(vembuSQS, vendorSQS));
                  } else if (o instanceof SelectStatement) {
                     SelectStatement sst = (SelectStatement)o;
                     newJoinExpression.add(sst.toOracleSelect(vembuSQS, vendorSQS));
                  } else {
                     newJoinExpression.add(o.toString());
                  }
               }
            }

            ft.setJoinExpression(newJoinExpression);
         } else if (!this.joinClause.trim().equalsIgnoreCase("FULL JOIN") && !this.joinClause.trim().equalsIgnoreCase("FULL OUTER JOIN")) {
            if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("ON") || this.onOrUsingJoin == null && this.joinExpression != null) {
               if (!this.joinClause.equalsIgnoreCase("JOIN") && !this.joinClause.equalsIgnoreCase("INNER JOIN")) {
                  this.moveJoinExpressionToWhereExpression(ft, vembuSQS, vendorSQS, true);
               } else {
                  this.insertItem(vembuSQS, vendorSQS, 1, true);
               }
            } else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
               this.insertItem(vembuSQS, vendorSQS, 3, true);
            } else {
               WhereExpression we;
               if (this.joinClause.trim().equalsIgnoreCase("cross apply")) {
                  ft.setJoinClause(" CROSS JOIN ");
                  if (ft.getTableName() instanceof SelectQueryStatement) {
                     sqs = (SelectQueryStatement)ft.getTableName();
                     if (sqs.getWhereExpression() != null) {
                        we = sqs.getWhereExpression();
                        if (vendorSQS.getWhereExpression() == null) {
                           vendorSQS.setWhereExpression(we);
                           sqs.setWhereExpression((WhereExpression)null);
                        } else {
                           WhereExpression weSrc = vendorSQS.getWhereExpression();
                           weOperators = we.getOperator();
                           Vector colExp = we.getWhereItems();

                           for(i = 0; i < colExp.size(); ++i) {
                              if (colExp.get(i) instanceof WhereItem) {
                                 weSrc.addWhereItem((WhereItem)colExp.get(i));
                                 if (i == 0) {
                                    weSrc.addOperator("AND");
                                 } else {
                                    weSrc.addOperator((String)weOperators.get(i - 1));
                                 }
                              }

                              sqs.setWhereExpression((WhereExpression)null);
                           }
                        }
                     }
                  }
               } else if (this.joinClause.trim().equalsIgnoreCase("outer apply")) {
                  ft.setJoinClause(" LEFT OUTER JOIN ");
                  ft.setOnOrUsingJoin("ON");
                  newJoinExpression = new Vector();
                  we = new WhereExpression();
                  Vector wiVector = new Vector();
                  wit = new WhereItem();
                  WhereColumn leftWhereCol = new WhereColumn();
                  WhereColumn rightWhereCol = new WhereColumn();
                  Vector whereCols = new Vector();
                  tempTableName = "1";
                  whereCols.add(tempTableName);
                  leftWhereCol.setColumnExpression(whereCols);
                  rightWhereCol.setColumnExpression(whereCols);
                  wit.setLeftWhereExp(leftWhereCol);
                  wit.setRightWhereExp(rightWhereCol);
                  wit.setOperator("=");
                  wiVector.add(wit);
                  we.setWhereItem(wiVector);
                  newJoinExpression.add(we);
                  ft.setJoinExpression(newJoinExpression);
               }
            }
         } else {
            this.insertItem(vembuSQS, vendorSQS, 2, true);
         }
      }

      ft.setIsAS(false);
      if (this.aliasName != null) {
         this.aliasName = CustomizeUtil.objectNamesToQuotedIdentifier(this.aliasName, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
         if (this.aliasName.charAt(0) == '\'') {
            ft.setAliasName(this.aliasName.replace('\'', '"'));
         } else if (isdenodo && !GeneralUtil.isItEnclosedTblCol(this.aliasName)) {
            ft.setAliasName("\"" + this.aliasName + "\"");
         } else {
            ft.setAliasName(this.aliasName);
         }

         if (this.aliasName.startsWith("[") && this.aliasName.endsWith("]") || this.aliasName.startsWith("`") && this.aliasName.endsWith("`")) {
            this.aliasName = this.aliasName.substring(1, this.aliasName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || this.aliasName.indexOf(32) != -1) {
               this.aliasName = "\"" + this.aliasName + "\"";
            }

            ft.setAliasName(this.aliasName);
         }
      }

      if (this.setOperatorClauseListForSubQuery.size() != 0) {
         ArrayList setOperatorClauseList = new ArrayList();

         for(dotDotIndex = 0; dotDotIndex < this.setOperatorClauseListForSubQuery.size(); ++dotDotIndex) {
            setOperatorClauseList.add(((SetOperatorClause)this.setOperatorClauseListForSubQuery.get(dotDotIndex)).toOracleSelect(vembuSQS, vendorSQS));
         }

         ft.setSetOperatorClauseListForSubQuery(setOperatorClauseList);
      }

      return ft;
   }

   public FromTable toInformixSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass((CommentClass)null);
      int startIndex;
      int atIndex;
      Vector tokenVector;
      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         WhereExpression we;
         Vector operatorList;
         if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            we = ((WhereExpression)this.joinExpression.elementAt(0)).toInformixSelect(vembuSQS, vendorSQS);
            operatorList = new Vector();
            operatorList.addElement(we);
            ft.setJoinExpression(operatorList);
         } else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            we = (WhereExpression)this.joinExpression.elementAt(0);
            operatorList = we.getOperator();

            for(startIndex = 0; startIndex < operatorList.size(); ++startIndex) {
               if (operatorList.elementAt(startIndex) instanceof String && ((String)operatorList.get(startIndex)).equalsIgnoreCase("AND")) {
                  operatorList.setElementAt("AND", startIndex);
               }
            }

            we.setOperator(operatorList);
            we = we.toInformixSelect(vembuSQS, vendorSQS);
            tokenVector = new Vector();
            tokenVector.addElement(we);
            ft.setJoinExpression(tokenVector);
         }

         ft.setOnOrUsingJoin(this.onOrUsingJoin);
         if (this.UsingList != null) {
            Vector v = new Vector();

            for(atIndex = 0; atIndex < this.UsingList.size(); ++atIndex) {
               if (this.UsingList.elementAt(atIndex) instanceof TableColumn) {
                  v.addElement(((TableColumn)this.UsingList.elementAt(atIndex)).toInformixSelect(vembuSQS, vendorSQS));
               } else {
                  v.addElement(this.UsingList.elementAt(atIndex));
               }
            }

            ft.setUsingList(v);
         }

         this.removeJoinExpressionForCrossJoin(ft);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            ft.setTableName(((SelectQueryStatement)this.tableName).toInformix());
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException("Function calls in the place of Table name yet to be supported");
            }

            if (this.tableName instanceof WithStatement) {
               ft.setTableName(((WithStatement)this.tableName).toInformix());
            } else if (this.tableName instanceof FromClause) {
               ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
               ft.setTableName(((FromClause)this.tableName).toInformixSelect(vembuSQS, vendorSQS));
               ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
            } else {
               String tempTableName;
               if (((String)this.tableName).indexOf("[") != 0) {
                  ft.setTableName(this.tableName);
               } else {
                  tempTableName = (String)this.tableName;
                  String tableNameSubString = "";
                  startIndex = tempTableName.indexOf("[");
                  if (startIndex != -1) {
                     while(startIndex != -1) {
                        if (startIndex == 0) {
                           tableNameSubString = tempTableName.substring(1);
                           tableNameSubString = "\"" + tableNameSubString;
                           tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                           tempTableName = tableNameSubString;
                           startIndex = tableNameSubString.indexOf("[");
                        } else {
                           tableNameSubString = tempTableName.substring(0, startIndex);
                           tableNameSubString = tableNameSubString + "\"";
                           tableNameSubString = tableNameSubString + tempTableName.substring(startIndex + 1);
                           tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                           tempTableName = tableNameSubString;
                           startIndex = tableNameSubString.indexOf("[");
                        }
                     }

                     ft.setTableName(tableNameSubString);
                  }
               }

               tempTableName = (String)this.tableName;
               atIndex = tempTableName.indexOf("@");
               tokenVector = new Vector();
               String table_Name = (String)this.tableName;
               StringTokenizer st = new StringTokenizer(table_Name, ".");

               int count;
               for(count = 0; st.hasMoreTokens(); ++count) {
                  tokenVector.add(st.nextToken());
               }

               String tempTableName;
               String tableNameSubString;
               if (count == 1 && atIndex != -1) {
                  tempTableName = (String)tokenVector.elementAt(0);
                  tableNameSubString = tempTableName.substring(0, atIndex);
                  tempTableName.substring(atIndex + 1);
                  ft.setTableName(tableNameSubString);
               }

               tempTableName = (String)ft.getTableName();
               tableNameSubString = "";
               int startIndex = tempTableName.indexOf("[");
               if (startIndex != -1) {
                  if (startIndex == 0) {
                     tableNameSubString = tempTableName.substring(1);
                     tableNameSubString = "\"" + tableNameSubString;
                     tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                     tempTableName = tableNameSubString;
                  }

                  ft.setTableName(tempTableName);
               }

               String tblName = (String)ft.getTableName();
               ft.setTableName(tblName);
               tblName = (String)ft.getTableName();
               ft.setTableName(tblName);
            }
         }
      }

      if (this.setOperatorClauseListForSubQuery.size() != 0) {
         ArrayList setOperatorClauseList = new ArrayList();

         for(atIndex = 0; atIndex < this.setOperatorClauseListForSubQuery.size(); ++atIndex) {
            setOperatorClauseList.add(((SetOperatorClause)this.setOperatorClauseListForSubQuery.get(atIndex)).toInformixSelect(vembuSQS, vendorSQS));
         }

         ft.setSetOperatorClauseListForSubQuery(setOperatorClauseList);
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         if (this.aliasName.charAt(0) == '\'') {
            ft.setAliasName(this.aliasName.replace('\'', ' '));
         } else if (this.aliasName.charAt(0) == '`') {
            ft.setAliasName(this.aliasName.replace('`', ' '));
         } else if (this.aliasName.charAt(0) == '"') {
            ft.setAliasName(this.aliasName.replace('"', ' '));
         } else {
            ft.setAliasName(this.aliasName);
         }
      } else if (this.tableName instanceof SelectQueryStatement && this.setOperatorClauseListForSubQuery.size() == 0) {
         SelectQueryStatement sqs = (SelectQueryStatement)this.tableName;
         if (sqs.getSetOperatorClause() != null) {
            SetOperatorClause soc = sqs.getSetOperatorClause();
            if (soc == null || soc.getSetClause() == null || !soc.getSetClause().toUpperCase().startsWith("UNION") && !soc.getSetClause().toUpperCase().startsWith("MINUS") && !soc.getSetClause().toUpperCase().startsWith("INTERSECT") && !soc.getSetClause().toUpperCase().startsWith("EXCEPT")) {
               ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
            }
         } else {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
         }
      }

      if (this.joinClause != null && this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
         ft.setOnOrUsingJoin((String)null);
      }

      ft.setJoinClause(this.joinClause);
      return ft;
   }

   public FromTable toTimesTenSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            ft.setTableName(((SelectQueryStatement)this.tableName).toTimesTen());
         } else if (!(this.tableName instanceof FunctionCalls)) {
            if (this.tableName instanceof FromClause) {
               FromClause fromCl = (FromClause)this.tableName;
               ft.setTableName(fromCl.toTimesTenSelect(vembuSQS, vendorSQS));
            } else {
               String table_Name_String = (String)this.tableName;
               int dotDotIndex = table_Name_String.indexOf("..");
               ft.setTableName(table_Name_String);
               int count;
               String ownerName;
               String ownerName;
               String tableName;
               if (dotDotIndex != -1) {
                  ft.setTableName(table_Name_String.substring(table_Name_String.lastIndexOf(".") + 1, table_Name_String.length()));
                  ft.setTableName(CustomizeUtil.objectNamesToQuotedIdentifier((String)ft.getTableName(), SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10));
               } else {
                  Vector tokenVector = new Vector();
                  StringTokenizer st = new StringTokenizer(table_Name_String, ".");

                  for(count = 0; st.hasMoreTokens(); ++count) {
                     tokenVector.add(st.nextToken());
                  }

                  if (count == 1) {
                     ft.setTableName(CustomizeUtil.objectNamesToQuotedIdentifier((String)ft.getTableName(), SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10));
                  } else if (count == 2) {
                     ownerName = (String)tokenVector.elementAt(0);
                     ownerName = (String)tokenVector.elementAt(1);
                     ownerName = CustomizeUtil.objectNamesToQuotedIdentifier(ownerName, SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10);
                     if (ownerName.equalsIgnoreCase("DBO")) {
                        ft.setTableName(ownerName);
                     } else {
                        ft.setTableName(ownerName + "." + ownerName);
                     }
                  } else if (count == 3) {
                     ownerName = (String)tokenVector.elementAt(0);
                     ownerName = (String)tokenVector.elementAt(1);
                     tableName = (String)tokenVector.elementAt(2);
                     tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10);
                     if (ownerName.equalsIgnoreCase("DBO")) {
                        ft.setTableName(tableName);
                     } else {
                        ft.setTableName(ownerName + "." + tableName);
                     }
                  }
               }

               String tempTableName = (String)ft.getTableName();
               String tableNameSubString = "";
               count = tempTableName.indexOf("[");
               if (count != -1) {
                  while(count != -1) {
                     if (count == 0) {
                        tableNameSubString = tempTableName.substring(1);
                        int endIndex = tableNameSubString.indexOf("]");
                        ownerName = tableNameSubString.substring(0, endIndex);
                        if (ownerName.indexOf(" ") != -1) {
                           ownerName = "\"" + ownerName + "\"";
                        }

                        tableNameSubString = ownerName + tableNameSubString.substring(endIndex + 1);
                     } else {
                        tableNameSubString = tempTableName.substring(0, count);
                        ownerName = tempTableName.substring(count + 1);
                        int endIndex = ownerName.indexOf("]");
                        tableName = ownerName.substring(0, endIndex);
                        if (tableName.indexOf(" ") != -1) {
                           tableNameSubString = tableNameSubString + "\"" + tableName + "\"" + ownerName.substring(endIndex + 1);
                        } else {
                           tableNameSubString = tableNameSubString + tableName + ownerName.substring(endIndex + 1);
                        }
                     }

                     tempTableName = tableNameSubString;
                     count = tableNameSubString.indexOf("[");
                  }

                  ft.setTableName(tableNameSubString);
               }

               ownerName = (String)ft.getTableName();
               if (ownerName.equalsIgnoreCase("dual") || ownerName.equalsIgnoreCase("sys.dual")) {
                  ownerName = "MONITOR";
               }

               ft.setTableName(ownerName);
            }
         }
      }

      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
            throw new ConvertException("Natural joins are not allowed in TimesTen");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
            throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
            throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
            throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
            throw new ConvertException("Conversion failure..Natural right join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         if (!this.joinClause.trim().equalsIgnoreCase("FULL JOIN") && !this.joinClause.trim().equalsIgnoreCase("FULL OUTER JOIN")) {
            if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("ON") || this.onOrUsingJoin == null && this.joinExpression != null) {
               if (!this.joinClause.equalsIgnoreCase("JOIN") && !this.joinClause.equalsIgnoreCase("INNER JOIN")) {
                  this.moveJoinExpressionToWhereExpression(ft, vembuSQS, vendorSQS, false);
               } else {
                  this.insertItem(vembuSQS, vendorSQS, 1, false);
               }
            } else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
               this.insertItem(vembuSQS, vendorSQS, 3, false);
            }
         } else {
            this.insertItem(vembuSQS, vendorSQS, 2, false);
         }
      }

      ft.setIsAS(false);
      if (this.aliasName != null) {
         if (this.aliasName.charAt(0) == '\'') {
            ft.setAliasName(this.aliasName.replace('\'', '"'));
         } else {
            this.aliasName = CustomizeUtil.objectNamesToQuotedIdentifier(this.aliasName, SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10);
            ft.setAliasName(this.aliasName);
         }
      }

      return ft;
   }

   public FromTable toNetezzaSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      ft.setCommentClass(this.commentObj);
      ft.setTableKeyword(this.tableKeyword);
      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            ft.setTableName(((SelectQueryStatement)this.tableName).toNetezza());
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException("Functions yet to be supported in table names");
            }

            if (this.tableName instanceof WithStatement) {
               ft.setTableName(((WithStatement)this.tableName).toNetezza());
            } else if (this.tableName instanceof FromClause) {
               ft.setTableName(((FromClause)this.tableName).toNetezzaSelect(vembuSQS, vendorSQS));
            } else {
               String table_Name_String = (String)this.tableName;
               int atIndex = table_Name_String.indexOf("@");
               String replaceDotDot = StringFunctions.replaceFirst(".", "..", (String)this.tableName);
               ft.setTableName(CustomizeUtil.objectNamesToQuotedIdentifier(replaceDotDot, SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11));
               Vector tokenVector = new Vector();
               String table_Name = (String)this.tableName;
               StringTokenizer st = new StringTokenizer(table_Name, ".");

               int count;
               for(count = 0; st.hasMoreTokens(); ++count) {
                  tokenVector.add(st.nextToken());
               }

               String tempTableName;
               String sqlTableName;
               String dataBaseName;
               String ownerName;
               String tableNameSubString;
               if (count == 1 && atIndex != -1) {
                  tempTableName = (String)tokenVector.elementAt(0);
                  tableNameSubString = tempTableName.substring(0, atIndex);
                  sqlTableName = tempTableName.substring(atIndex + 1);
                  dataBaseName = sqlTableName + "." + tableNameSubString;
                  ft.setTableName(dataBaseName);
               } else if (count == 2 && atIndex != -1) {
                  tempTableName = (String)tokenVector.elementAt(1);
                  int tableAtIndex = tempTableName.indexOf("@");
                  if (tableAtIndex != -1) {
                     sqlTableName = tempTableName.substring(0, tableAtIndex);
                     dataBaseName = tempTableName.substring(tableAtIndex + 1);
                     ownerName = dataBaseName + "." + (String)tokenVector.elementAt(0) + "." + sqlTableName;
                     ft.setTableName(ownerName);
                  } else {
                     ft.setTableName(this.tableName);
                  }
               }

               tempTableName = (String)ft.getTableName();
               tableNameSubString = "";
               int startIndex = tempTableName.indexOf("[");
               if (startIndex != -1) {
                  while(startIndex != -1) {
                     if (startIndex == 0) {
                        tableNameSubString = tempTableName.substring(1);
                        tableNameSubString = "\"" + tableNameSubString;
                        tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                        tempTableName = tableNameSubString;
                        startIndex = tableNameSubString.indexOf("[");
                     } else {
                        tableNameSubString = tempTableName.substring(0, startIndex);
                        tableNameSubString = tableNameSubString + "\"";
                        tableNameSubString = tableNameSubString + tempTableName.substring(startIndex + 1);
                        tableNameSubString = StringFunctions.replaceFirst("\"", "]", tableNameSubString);
                        tempTableName = tableNameSubString;
                        startIndex = tableNameSubString.indexOf("[");
                     }
                  }

                  ft.setTableName(tableNameSubString);
               }

               if (count == 1 && atIndex == -1) {
                  ft.setTableName(CustomizeUtil.objectNamesToQuotedIdentifier((String)ft.getTableName(), SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11));
               } else if (count == 2 && atIndex == -1) {
                  dataBaseName = (String)tokenVector.elementAt(0);
                  ownerName = (String)tokenVector.elementAt(1);
                  dataBaseName = CustomizeUtil.objectNamesToQuotedIdentifier(dataBaseName, SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11);
                  ownerName = CustomizeUtil.objectNamesToQuotedIdentifier(ownerName, SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11);
                  if (dataBaseName.equalsIgnoreCase("DBO")) {
                     ft.setTableName(ownerName);
                  } else if (SwisSQLOptions.renameTableNameAsSchemName_TableName && !dataBaseName.startsWith("\"") && !ownerName.startsWith("\"")) {
                     ft.setTableName(dataBaseName + "." + dataBaseName + "_" + ownerName);
                  } else {
                     ft.setTableName(dataBaseName + "." + ownerName);
                  }
               } else if (count == 3 && atIndex == -1) {
                  dataBaseName = (String)tokenVector.elementAt(0);
                  ownerName = (String)tokenVector.elementAt(1);
                  String tableName = (String)tokenVector.elementAt(2);
                  dataBaseName = CustomizeUtil.objectNamesToQuotedIdentifier(dataBaseName, SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11);
                  ownerName = CustomizeUtil.objectNamesToQuotedIdentifier(ownerName, SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11);
                  tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11);
                  if (ownerName.equalsIgnoreCase("DBO")) {
                     ft.setTableName(tableName);
                  } else if (SwisSQLOptions.renameTableNameAsSchemName_TableName && !ownerName.startsWith("\"") && !tableName.startsWith("\"") && !dataBaseName.startsWith("\"")) {
                     ft.setTableName(dataBaseName + "." + ownerName + "." + ownerName + "_" + tableName);
                  } else {
                     ft.setTableName(dataBaseName + "." + ownerName + "." + tableName);
                  }
               }
            }
         }
      }

      WhereExpression we;
      Vector operatorList;
      if (this.onOrUsingJoin == null && this.joinExpression != null) {
         ft.setOnOrUsingJoin("ON");
         we = ((WhereExpression)this.joinExpression.elementAt(0)).toNetezzaSelect(vembuSQS, vendorSQS);
         operatorList = new Vector();
         operatorList.addElement(we);
         ft.setJoinExpression(operatorList);
      } else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
         this.changeToOnJoin(ft, this.getPreviousFromTableFromTheFromItemList(vendorSQS));
      } else if (this.joinExpression != null) {
         ft.setOnOrUsingJoin(this.onOrUsingJoin);
         we = (WhereExpression)this.joinExpression.elementAt(0);
         operatorList = we.getOperator();
         we.setOperator(operatorList);
         we.toNetezzaSelect(vembuSQS, vendorSQS);
         Vector v = new Vector();
         v.addElement(we);
         ft.setJoinExpression(v);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null && (!this.aliasName.equalsIgnoreCase("partition") || this.columnAliasList == null)) {
         if (this.aliasName.charAt(0) == '\'') {
            ft.setAliasName(this.aliasName.replace('\'', '"'));
         } else {
            ft.setAliasName(this.aliasName);
         }
      } else if (this.tableName instanceof SelectQueryStatement) {
         ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
      }

      ft.setJoinClause(this.joinClause);
      return ft;
   }

   private void moveJoinExpressionToWhereExpression(FromTable ft, SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS, boolean toOracle) throws ConvertException {
      WhereExpression joinWE = null;
      Vector whereItemList = new Vector();
      Vector operatorList = new Vector();
      new Vector();
      new Vector();
      Object obj = this.joinExpression.elementAt(0);
      if (obj instanceof WhereExpression) {
         joinWE = (WhereExpression)obj;
         joinWE.loadWhereItemsOperators(whereItemList, operatorList);
      }

      SelectQueryStatement selectQueryStatement = null;
      SelectStatement selectStatement = null;
      Vector selectItemList = null;
      FromClause fromClause = null;
      Vector fromItemList = null;
      FromTable fromTable = null;
      WhereExpression whereExpression = null;
      boolean isRightOuterJoin = false;

      for(int i = 0; i < whereItemList.size(); ++i) {
         WhereItem whereItem = (WhereItem)whereItemList.elementAt(i);
         Vector operators = (Vector)operatorList.elementAt(i);
         WhereColumn leftItem = null;
         Vector leftColumnExpression = null;
         WhereColumn rightItem = null;
         Vector rightColumnExpression = null;
         if (whereItem != null) {
            leftItem = whereItem.getLeftWhereExp();
            if (leftItem != null) {
               leftColumnExpression = leftItem.getColumnExpression();
            }

            rightItem = whereItem.getRightWhereExp();
            if (rightItem != null) {
               rightColumnExpression = rightItem.getColumnExpression();
            }
         }

         if (rightColumnExpression != null) {
         }

         TableColumn rightTableColumn;
         Vector v;
         String previousAliasName;
         if ((leftColumnExpression != null && !(leftColumnExpression.elementAt(0) instanceof TableColumn) || rightColumnExpression != null && !(rightColumnExpression.elementAt(0) instanceof TableColumn) || leftColumnExpression == null || rightColumnExpression == null) && toOracle) {
            String oneOfTheTableNames = null;
            if (leftColumnExpression != null && leftColumnExpression.elementAt(0) instanceof TableColumn) {
               rightTableColumn = (TableColumn)leftColumnExpression.elementAt(0);
               oneOfTheTableNames = rightTableColumn.getTableName();
            } else if (rightColumnExpression != null && rightColumnExpression.elementAt(0) instanceof TableColumn) {
               rightTableColumn = (TableColumn)rightColumnExpression.elementAt(0);
               oneOfTheTableNames = rightTableColumn.getTableName();
            }

            if (this.aliasName == null && this.tableName instanceof String) {
               this.aliasName = this.tableName.toString();
            } else if (this.aliasName == null && !(this.tableName instanceof String)) {
            }

            if (!this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") && !this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
               if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") || this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                  FromTable previousFromTable = this.getPreviousFromTableFromTheFromItemList(vendorSQS);
                  Object previousTableName = null;
                  previousAliasName = null;
                  if (previousFromTable != null) {
                     if (previousFromTable.getTableName() instanceof String) {
                        previousTableName = previousFromTable.getTableName();
                     }

                     previousAliasName = previousFromTable.getAliasName();
                     if (previousAliasName == null && previousTableName instanceof String) {
                        previousAliasName = (String)previousTableName;
                     } else if (previousAliasName == null && !(previousTableName instanceof String)) {
                     }
                  }

                  FromTable toBeChangedConvertedPreviousFromTable = null;
                  FromClause vendorFromClause;
                  if (vembuSQS.getFromClause() != null && vembuSQS.getFromClause().getFromItemList() != null && vembuSQS.getFromClause().getFromItemList().size() > 0 && vembuSQS.getFromClause().getFromItemList().lastElement() != null && vembuSQS.getFromClause().getFromItemList().lastElement() instanceof FromTable) {
                     toBeChangedConvertedPreviousFromTable = (FromTable)vembuSQS.getFromClause().getFromItemList().lastElement();
                  } else if (vembuSQS.getFromClause() != null && vembuSQS.getFromClause().getFromItemList() != null && vembuSQS.getFromClause().getFromItemList().size() > 0 && vembuSQS.getFromClause().getFromItemList().lastElement() != null && vembuSQS.getFromClause().getFromItemList().lastElement() instanceof FromClause) {
                     vendorFromClause = (FromClause)vembuSQS.getFromClause().getFromItemList().lastElement();
                     v = vendorFromClause.getFromItemList();
                     toBeChangedConvertedPreviousFromTable = vendorFromClause.getLastElement();
                  }

                  if (previousTableName != null && (previousTableName.toString().equalsIgnoreCase(oneOfTheTableNames) || previousAliasName.equalsIgnoreCase(oneOfTheTableNames) || oneOfTheTableNames == null)) {
                     WhereExpression weClone;
                     if (selectQueryStatement == null) {
                        selectQueryStatement = new SelectQueryStatement();
                        selectStatement = new SelectStatement();
                        selectStatement.setSelectClause("SELECT");
                        selectItemList = new Vector();
                        selectItemList.addElement("*");
                        selectStatement.setSelectItemList(selectItemList);
                        fromClause = new FromClause();
                        fromClause.setFromClause("FROM");
                        fromItemList = new Vector();
                        fromTable = new FromTable();
                        fromTable.setTableName(previousTableName);
                        fromTable.setAliasName(previousAliasName);
                        fromItemList.add(fromTable);
                        fromClause.setFromItemList(fromItemList);
                        selectQueryStatement.setSelectStatement(selectStatement);
                        selectQueryStatement.setFromClause(fromClause);
                        weClone = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                        whereExpression = this.createNewWhereExpressionByRemovingTheJOINConditions(weClone);
                        selectQueryStatement.setWhereExpression(whereExpression);
                     } else {
                        weClone = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                        whereExpression = this.createNewWhereExpressionByRemovingTheJOINConditions(weClone);
                        selectQueryStatement.setWhereExpression(whereExpression);
                     }

                     toBeChangedConvertedPreviousFromTable.setAliasName(previousAliasName);
                     toBeChangedConvertedPreviousFromTable.setTableName(selectQueryStatement);
                  } else {
                     if (selectQueryStatement == null) {
                        selectQueryStatement = new SelectQueryStatement();
                        selectStatement = new SelectStatement();
                        selectStatement.setSelectClause("SELECT");
                        selectItemList = new Vector();
                        selectItemList.addElement(this.aliasName + ".*");
                        selectStatement.setSelectItemList(selectItemList);
                        fromClause = new FromClause();
                        vendorFromClause = vendorSQS.getFromClause();
                        v = vendorFromClause.getFromItemList();
                        new Vector();
                        Vector vembuFromItemList = this.getTablesOnlyList(v);
                        fromClause.setFromClause("FROM");
                        fromClause.setFromItemList(vembuFromItemList);
                        new WhereExpression();
                        whereExpression = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                        selectQueryStatement.setSelectStatement(selectStatement);
                        selectQueryStatement.setFromClause(fromClause);
                        selectQueryStatement.setWhereExpression(whereExpression);
                     }

                     if (toBeChangedConvertedPreviousFromTable != null) {
                        toBeChangedConvertedPreviousFromTable.setAliasName(this.aliasName);
                        toBeChangedConvertedPreviousFromTable.setTableName(selectQueryStatement);
                     }
                  }
               }
            } else if ((!(this.tableName instanceof String) || !this.tableName.toString().equalsIgnoreCase(oneOfTheTableNames)) && (this.aliasName == null || !this.aliasName.equalsIgnoreCase(oneOfTheTableNames)) && oneOfTheTableNames != null) {
               if (selectQueryStatement == null) {
                  selectQueryStatement = new SelectQueryStatement();
                  selectStatement = new SelectStatement();
                  selectStatement.setSelectClause("SELECT");
                  selectItemList = new Vector();
                  selectItemList.addElement(this.aliasName + ".*");
                  selectStatement.setSelectItemList(selectItemList);
                  fromClause = new FromClause();
                  FromClause vendorFromClause = vendorSQS.getFromClause();
                  Vector vendorFromItemList = vendorFromClause.getFromItemList();
                  new Vector();
                  Vector vembuFromItemList = this.getTablesOnlyList(vendorFromItemList);
                  fromClause.setFromClause("FROM");
                  fromClause.setFromItemList(vembuFromItemList);
                  new WhereExpression();
                  whereExpression = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                  selectQueryStatement.setSelectStatement(selectStatement);
                  selectQueryStatement.setFromClause(fromClause);
                  selectQueryStatement.setWhereExpression(whereExpression);
               }

               ft.setAliasName(this.aliasName);
               ft.setTableName(selectQueryStatement);
            } else {
               WhereExpression orgWhereExpression;
               if (selectQueryStatement == null) {
                  if (this.tableName != null && this.tableName instanceof SelectQueryStatement) {
                     if (((SelectQueryStatement)this.tableName).getWhereExpression() != null) {
                        orgWhereExpression = ((SelectQueryStatement)this.tableName).getWhereExpression();
                        WhereExpression weClone = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                        whereExpression = this.createNewWhereExpressionByRemovingTheJOINConditions(weClone);
                        this.collectAllWhereColumnsInWhereExpression(whereExpression);
                        orgWhereExpression.addWhereExpression(whereExpression);
                        orgWhereExpression.addOperator("AND");
                        ft.setAliasName(this.aliasName);
                        ft.setTableName(this.tableName);
                        selectQueryStatement = ((SelectQueryStatement)this.tableName).toOracle();
                     } else {
                        orgWhereExpression = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                        whereExpression = this.createNewWhereExpressionByRemovingTheJOINConditions(orgWhereExpression);
                        ((SelectQueryStatement)this.tableName).setWhereExpression(whereExpression);
                        this.collectAllWhereColumnsInWhereExpression(whereExpression);
                        ft.setAliasName(this.aliasName);
                        ft.setTableName(this.tableName);
                        selectQueryStatement = ((SelectQueryStatement)this.tableName).toOracle();
                     }
                  } else {
                     selectQueryStatement = new SelectQueryStatement();
                     selectStatement = new SelectStatement();
                     selectStatement.setSelectClause("SELECT");
                     selectItemList = new Vector();
                     selectItemList.addElement("*");
                     selectStatement.setSelectItemList(selectItemList);
                     fromClause = new FromClause();
                     fromClause.setFromClause("FROM");
                     fromItemList = new Vector();
                     fromTable = new FromTable();
                     fromTable.setTableName(this.tableName);
                     fromTable.setAliasName(this.aliasName);
                     fromItemList.add(fromTable);
                     fromClause.setFromItemList(fromItemList);
                     selectQueryStatement.setSelectStatement(selectStatement);
                     selectQueryStatement.setFromClause(fromClause);
                     orgWhereExpression = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                     whereExpression = this.createNewWhereExpressionByRemovingTheJOINConditions(orgWhereExpression);
                     selectQueryStatement.setWhereExpression(whereExpression);
                  }
               } else {
                  orgWhereExpression = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                  whereExpression = this.createNewWhereExpressionByRemovingTheJOINConditions(orgWhereExpression);
                  selectQueryStatement.setWhereExpression(whereExpression);
               }

               ft.setAliasName(this.aliasName);
               ft.setTableName(selectQueryStatement);
            }
         } else if (leftColumnExpression != null && leftColumnExpression.elementAt(0) instanceof TableColumn) {
            TableColumn leftTableColumn = (TableColumn)leftColumnExpression.elementAt(0);
            if (leftTableColumn != null && leftTableColumn.getTableName() != null) {
               if (rightColumnExpression != null && rightColumnExpression.elementAt(0) instanceof TableColumn) {
                  rightTableColumn = (TableColumn)rightColumnExpression.elementAt(0);
                  if (rightTableColumn != null && rightTableColumn.getTableName() != null) {
                     String rightTableName = rightTableColumn.getTableName();
                     Vector fromItemListVector;
                     String tabName;
                     SelectColumn sc;
                     String temp;
                     FromClause fromClauseInsideFT;
                     FunctionCalls fc;
                     String tableNameExtracted;
                     if (!this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") && !this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
                        if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") || this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                           isRightOuterJoin = true;
                           if (this.tableName instanceof String) {
                              previousAliasName = null;
                              if (this.tableName.toString().lastIndexOf(".") != -1) {
                                 previousAliasName = this.tableName.toString().substring(this.tableName.toString().lastIndexOf(".") + 1);
                              }

                              if ((previousAliasName == null || !previousAliasName.equalsIgnoreCase(rightTableName)) && !((String)this.tableName).equalsIgnoreCase(rightTableName) && (this.aliasName == null || !this.aliasName.equalsIgnoreCase(rightTableName))) {
                                 whereItem.setLeftJoin("+");
                              } else {
                                 whereItem.setRightJoin("+");
                              }
                           } else if (this.tableName instanceof FunctionCalls) {
                              fc = (FunctionCalls)this.tableName;
                              tableNameExtracted = null;
                              tabName = fc.getFunctionName().getColumnName();
                              if (tabName.toString().lastIndexOf(".") != -1) {
                                 tableNameExtracted = tabName.toString().substring(tabName.toString().lastIndexOf(".") + 1);
                              }

                              v = fc.getFunctionArguments();
                              if (v.size() == 1 && v.get(0) instanceof SelectColumn) {
                                 sc = (SelectColumn)v.get(0);
                                 temp = sc.toString();
                                 if (temp.trim().equalsIgnoreCase("NOLOCK")) {
                                    if ((tableNameExtracted == null || !tableNameExtracted.equalsIgnoreCase(rightTableName)) && !tabName.equalsIgnoreCase(rightTableName) && (this.aliasName == null || !this.aliasName.equalsIgnoreCase(rightTableName))) {
                                       whereItem.setLeftJoin("+");
                                    } else {
                                       whereItem.setRightJoin("+");
                                    }
                                 }
                              }
                           } else if (this.tableName instanceof SelectQueryStatement) {
                              if (this.aliasName != null && this.aliasName.equalsIgnoreCase(rightTableName)) {
                                 whereItem.setRightJoin("+");
                              } else {
                                 whereItem.setLeftJoin("+");
                              }
                           } else if (this.tableName instanceof FromClause) {
                              fromClauseInsideFT = (FromClause)this.tableName;
                              fromItemListVector = fromClauseInsideFT.getFromItemList();
                              this.getRightJoinInWhereItemForTableNameAsFromClause(fromItemListVector, rightTableName, this.aliasName, whereItem);
                           }
                        }
                     } else if (this.tableName instanceof String) {
                        previousAliasName = null;
                        if (this.tableName.toString().lastIndexOf(".") != -1) {
                           previousAliasName = this.tableName.toString().substring(this.tableName.toString().lastIndexOf(".") + 1);
                        }

                        if ((previousAliasName == null || !previousAliasName.equalsIgnoreCase(rightTableName)) && !((String)this.tableName).equalsIgnoreCase(rightTableName) && (this.aliasName == null || !this.aliasName.equalsIgnoreCase(rightTableName))) {
                           whereItem.setRightJoin("+");
                        } else {
                           whereItem.setLeftJoin("+");
                        }
                     } else if (this.tableName instanceof FunctionCalls) {
                        fc = (FunctionCalls)this.tableName;
                        tableNameExtracted = null;
                        tabName = fc.getFunctionName().getColumnName();
                        if (tabName.toString().lastIndexOf(".") != -1) {
                           tableNameExtracted = tabName.toString().substring(tabName.toString().lastIndexOf(".") + 1);
                        }

                        v = fc.getFunctionArguments();
                        if (v.size() == 1 && v.get(0) instanceof SelectColumn) {
                           sc = (SelectColumn)v.get(0);
                           temp = sc.toString();
                           if (temp.trim().equalsIgnoreCase("NOLOCK")) {
                              if ((tableNameExtracted == null || !tableNameExtracted.equalsIgnoreCase(rightTableName)) && !tabName.equalsIgnoreCase(rightTableName) && (this.aliasName == null || !this.aliasName.equalsIgnoreCase(rightTableName))) {
                                 whereItem.setRightJoin("+");
                              } else {
                                 whereItem.setLeftJoin("+");
                              }
                           }
                        }
                     } else if (this.tableName instanceof SelectQueryStatement) {
                        if (this.aliasName != null && this.aliasName.equalsIgnoreCase(rightTableName)) {
                           whereItem.setLeftJoin("+");
                        } else {
                           whereItem.setRightJoin("+");
                        }
                     } else if (this.tableName instanceof FromClause) {
                        fromClauseInsideFT = (FromClause)this.tableName;
                        fromItemListVector = fromClauseInsideFT.getFromItemList();
                        this.getLeftJoinInWhereItemForTableNameAsFromClause(fromItemListVector, rightTableName, this.aliasName, whereItem);
                     }
                  } else if (rightTableColumn != null) {
                     this.setJoinType(whereItem, leftTableColumn.getTableName(), true);
                  }
               } else if (rightColumnExpression != null && rightColumnExpression.elementAt(0) instanceof String && !toOracle && isRightOuterJoin && this.tableName instanceof String) {
                  whereItem.setRightJoin("+");
               }
            } else if (leftTableColumn != null && rightColumnExpression != null && rightColumnExpression.elementAt(0) instanceof TableColumn) {
               rightTableColumn = (TableColumn)rightColumnExpression.elementAt(0);
               if (rightTableColumn != null && rightTableColumn.getTableName() != null) {
                  this.setJoinType(whereItem, rightTableColumn.getTableName(), false);
               } else if (rightTableColumn != null) {
                  FromTable ftLeftExpr = MetadataInfoUtil.getTableOfColumn(vendorSQS, (TableColumn)leftTableColumn);
                  if (ftLeftExpr != null) {
                     Object tblObj = ftLeftExpr.getTableName();
                     if (tblObj instanceof String && this.tableName instanceof String) {
                        if (((String)tblObj).equalsIgnoreCase((String)this.tableName)) {
                           if (!this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") && !this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
                              if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") || this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                                 whereItem.setLeftJoin("+");
                              }
                           } else {
                              whereItem.setRightJoin("+");
                           }
                        } else if (!this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") && !this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
                           if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") || this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                              whereItem.setRightJoin("+");
                           }
                        } else {
                           whereItem.setLeftJoin("+");
                        }
                     }
                  } else {
                     vembuSQS.setGeneralComments("/* SwisSQL Message : Metadata required for accurate conversions. */");
                     if (!this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") && !this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
                        if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") || this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                           whereItem.setRightJoin("+");
                        }
                     } else {
                        whereItem.setLeftJoin("+");
                     }
                  }
               }
            }
         }
      }

      if (toOracle) {
         this.insertItem(vembuSQS, vendorSQS, 1, true);
      } else {
         this.insertItem(vembuSQS, vendorSQS, 1, false);
      }

   }

   private void setJoinType(WhereItem whereItem, String wcTableName, boolean leftColumn) {
      String tableNameExtracted;
      if (!this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") && !this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
         if ((this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") || this.joinClause.equalsIgnoreCase("RIGHT JOIN")) && this.tableName instanceof String) {
            tableNameExtracted = null;
            if (this.tableName.toString().lastIndexOf(".") != -1) {
               tableNameExtracted = this.tableName.toString().substring(this.tableName.toString().lastIndexOf(".") + 1);
            }

            if (tableNameExtracted != null && tableNameExtracted.equalsIgnoreCase(wcTableName) || ((String)this.tableName).equalsIgnoreCase(wcTableName) || this.aliasName != null && this.aliasName.equalsIgnoreCase(wcTableName)) {
               if (leftColumn) {
                  whereItem.setLeftJoin("+");
               } else {
                  whereItem.setRightJoin("+");
               }
            } else if (leftColumn) {
               whereItem.setRightJoin("+");
            } else {
               whereItem.setLeftJoin("+");
            }
         }
      } else if (this.tableName instanceof String) {
         tableNameExtracted = null;
         if (this.tableName.toString().lastIndexOf(".") != -1) {
            tableNameExtracted = this.tableName.toString().substring(this.tableName.toString().lastIndexOf(".") + 1);
         }

         if (tableNameExtracted != null && tableNameExtracted.equalsIgnoreCase(wcTableName) || ((String)this.tableName).equalsIgnoreCase(wcTableName) || this.aliasName != null && this.aliasName.equalsIgnoreCase(wcTableName)) {
            if (leftColumn) {
               whereItem.setRightJoin("+");
            } else {
               whereItem.setLeftJoin("+");
            }
         } else if (leftColumn) {
            whereItem.setLeftJoin("+");
         } else {
            whereItem.setRightJoin("+");
         }
      }

   }

   private void getLeftJoinInWhereItemForTableNameAsFromClause(Vector fromItemListVector, String rightTableName, String aliasName, WhereItem whereItem) {
      for(int count = 0; count < fromItemListVector.size(); ++count) {
         if (fromItemListVector.elementAt(count) instanceof FromTable) {
            FromTable fromTableFromFC = (FromTable)fromItemListVector.get(count);
            Object fromTableName = fromTableFromFC.getTableName();
            if (!(fromTableName instanceof String)) {
               if (fromTableName instanceof SelectQueryStatement) {
                  if (aliasName != null && aliasName.equalsIgnoreCase(rightTableName)) {
                     whereItem.setLeftJoin("+");
                  } else {
                     whereItem.setRightJoin("+");
                  }
               }
            } else if (!((String)fromTableName).equalsIgnoreCase(rightTableName) && (aliasName == null || !aliasName.equalsIgnoreCase(rightTableName))) {
               whereItem.setRightJoin("+");
            } else {
               whereItem.setLeftJoin("+");
            }
         } else if (fromItemListVector.elementAt(count) instanceof FromClause) {
            FromClause newFC = (FromClause)fromItemListVector.get(count);
            Vector newFromItemList = newFC.getFromItemList();
            this.getLeftJoinInWhereItemForTableNameAsFromClause(newFromItemList, rightTableName, aliasName, whereItem);
         }
      }

   }

   private void getRightJoinInWhereItemForTableNameAsFromClause(Vector fromItemListVector, String rightTableName, String aliasName, WhereItem whereItem) {
      for(int count = 0; count < fromItemListVector.size(); ++count) {
         if (fromItemListVector.elementAt(count) instanceof FromTable) {
            FromTable fromTableFromFC = (FromTable)fromItemListVector.get(count);
            Object fromTableName = fromTableFromFC.getTableName();
            if (!(fromTableName instanceof String)) {
               if (fromTableName instanceof SelectQueryStatement) {
                  if (aliasName != null && aliasName.equalsIgnoreCase(rightTableName)) {
                     whereItem.setRightJoin("+");
                  } else {
                     whereItem.setLeftJoin("+");
                  }
               }
            } else if (!((String)fromTableName).equalsIgnoreCase(rightTableName) && (aliasName == null || !aliasName.equalsIgnoreCase(rightTableName))) {
               whereItem.setLeftJoin("+");
            } else {
               whereItem.setRightJoin("+");
            }
         } else if (fromItemListVector.elementAt(count) instanceof FromClause) {
            FromClause newFC = (FromClause)fromItemListVector.get(count);
            Vector newFromItemList = newFC.getFromItemList();
            this.getRightJoinInWhereItemForTableNameAsFromClause(newFromItemList, rightTableName, aliasName, whereItem);
         }
      }

   }

   private WhereExpression createNewWhereExpressionByRemovingTheJOINConditions(WhereExpression weClone) {
      new WhereExpression();
      Vector whereItemsANDExpressions = weClone.getWhereItems();

      for(int i = 0; i < whereItemsANDExpressions.size(); ++i) {
         Object obj = whereItemsANDExpressions.elementAt(i);
         if (obj instanceof WhereExpression) {
            WhereExpression we = (WhereExpression)obj;
            this.createNewWhereExpressionByRemovingTheJOINConditions(we);
         } else if (obj instanceof WhereItem) {
            WhereItem whereItem = (WhereItem)obj;
            WhereColumn leftItem = null;
            Vector leftColumnExpression = null;
            WhereColumn rightItem = null;
            Vector rightColumnExpression = null;
            if (whereItem != null) {
               leftItem = whereItem.getLeftWhereExp();
               if (leftItem != null) {
                  leftColumnExpression = leftItem.getColumnExpression();
               }

               rightItem = whereItem.getRightWhereExp();
               if (rightItem != null) {
                  rightColumnExpression = rightItem.getColumnExpression();
               }
            }

            if (leftColumnExpression != null && leftColumnExpression.elementAt(0) instanceof TableColumn && rightColumnExpression != null && rightColumnExpression.elementAt(0) instanceof TableColumn) {
               whereItemsANDExpressions.removeElementAt(i);
               if (i != 0 && weClone.getOperator().size() > 0) {
                  weClone.getOperator().removeElementAt(i - 1);
               } else if (weClone.getOperator().size() > i) {
                  weClone.getOperator().removeElementAt(i);
               }

               --i;
            }
         }
      }

      weClone.setToOracle(true);
      return weClone;
   }

   public FromTable getPreviousFromTableFromTheFromItemList(SelectQueryStatement vendorSQS) throws ConvertException {
      FromClause fc = vendorSQS.getFromClause();
      Vector v_fl = fc.getFromItemList();
      int i_cnt = false;

      int i_cnt;
      for(i_cnt = 0; i_cnt < v_fl.size(); ++i_cnt) {
         if (v_fl.elementAt(i_cnt) instanceof FromTable) {
            if (v_fl.elementAt(i_cnt).hashCode() == this.hashCode()) {
               break;
            }
         } else if (v_fl.elementAt(i_cnt) instanceof FromClause) {
            return null;
         }
      }

      --i_cnt;
      if (i_cnt == -1) {
         return null;
      } else {
         FromTable t_ft = null;
         if (v_fl.elementAt(i_cnt) instanceof FromTable) {
            t_ft = (FromTable)v_fl.elementAt(i_cnt);
         }

         return t_ft;
      }
   }

   private int getFromTableIndexFromFromItemList(SelectQueryStatement vendorSQS) throws ConvertException {
      FromClause fc = vendorSQS.getFromClause();
      Vector v_fl = fc.getFromItemList();
      int i_cnt = false;

      int i_cnt;
      for(i_cnt = 0; i_cnt < v_fl.size(); ++i_cnt) {
         if (v_fl.elementAt(i_cnt) instanceof FromTable) {
            if (v_fl.elementAt(i_cnt).hashCode() == this.hashCode()) {
               break;
            }
         } else if (v_fl.elementAt(i_cnt) instanceof FromClause) {
            return 0;
         }
      }

      return i_cnt;
   }

   private void markToBeRemovedOperators(WhereExpression whereExpression) {
      Vector whereItems = whereExpression.getWhereItems();
      Vector operators = whereExpression.getOperator();
      Object obj = null;

      for(int i = 0; i < whereItems.size(); ++i) {
         obj = whereItems.elementAt(i);
         if (obj instanceof WhereItem) {
            WhereItem wi = (WhereItem)obj;
            if (wi.getMovedToFromClause()) {
               if (i != 0) {
                  operators.setElementAt("&AND", i - 1);
               } else if (operators.size() > i) {
                  operators.setElementAt("&AND", i);
               }
            }
         } else if (obj instanceof WhereExpression) {
            this.markToBeRemovedOperators((WhereExpression)obj);
         }
      }

   }

   private void markNonJoinWhereItems(WhereExpression whereExpression, boolean toOracle) {
      Vector whereItems = whereExpression.getWhereItems();
      Vector operators = whereExpression.getOperator();
      Object obj = null;

      for(int i = 0; i < whereItems.size(); ++i) {
         obj = whereItems.elementAt(i);
         if (obj instanceof WhereItem) {
            WhereItem whereItem = (WhereItem)obj;
            WhereColumn leftItem = null;
            Vector leftColumnExpression = null;
            WhereColumn rightItem = null;
            Vector rightColumnExpression = null;
            if (whereItem != null) {
               leftItem = whereItem.getLeftWhereExp();
               if (leftItem != null) {
                  leftColumnExpression = leftItem.getColumnExpression();
               }

               rightItem = whereItem.getRightWhereExp();
               if (rightItem != null) {
                  rightColumnExpression = rightItem.getColumnExpression();
               }
            }

            if ((leftColumnExpression != null && !(leftColumnExpression.elementAt(0) instanceof TableColumn) || rightColumnExpression != null && !(rightColumnExpression.elementAt(0) instanceof TableColumn) || leftColumnExpression == null || rightColumnExpression == null) && toOracle) {
               whereItem.setMovedToFromClause(true);
               if (i != 0) {
                  operators.setElementAt("&AND", i - 1);
               } else if (operators.size() > i) {
                  operators.setElementAt("&AND", i);
               }
            }
         } else if (obj instanceof WhereExpression) {
            this.markNonJoinWhereItems((WhereExpression)obj, toOracle);
         }
      }

   }

   private Object convertTableNameToOracle(Object tableName, SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      if (tableName instanceof SelectQueryStatement) {
         return ((SelectQueryStatement)tableName).toOracle();
      } else if (tableName instanceof FunctionCalls) {
         return ((FunctionCalls)tableName).toOracleSelect(vembuSQS, vendorSQS);
      } else {
         String table_name = StringFunctions.replaceFirst(".", "..", (String)tableName);
         return table_name;
      }
   }

   private void addLeftJoin(WhereExpression whereExpression) {
      Vector whereItems = whereExpression.getWhereItems();
      Object obj = null;

      for(int i = 0; i < whereItems.size(); ++i) {
         obj = whereItems.elementAt(i);
         if (obj instanceof WhereItem) {
            WhereItem wi = (WhereItem)obj;
            if (wi.isItAJoinItem()) {
               wi.setLeftJoin("+");
            }
         } else if (obj instanceof WhereExpression) {
            this.addLeftJoin((WhereExpression)obj);
         }
      }

   }

   private void addRightJoin(WhereExpression whereExpression) {
      Vector whereItems = whereExpression.getWhereItems();
      Object obj = null;

      for(int i = 0; i < whereItems.size(); ++i) {
         obj = whereItems.elementAt(i);
         if (obj instanceof WhereItem) {
            WhereItem wi = (WhereItem)obj;
            if (wi.isItAJoinItem()) {
               wi.setRightJoin("+");
            }
         } else if (obj instanceof WhereExpression) {
            this.addRightJoin((WhereExpression)obj);
         }
      }

   }

   public void insertItem(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS, int type, boolean toOracle) throws ConvertException {
      WhereExpression f_we = vendorSQS.getWhereExpression();
      WhereExpression t_we = new WhereExpression();
      if (type == 1) {
         t_we = (WhereExpression)this.joinExpression.elementAt(0);
         if (!this.joinClause.equalsIgnoreCase("JOIN") && !this.joinClause.equalsIgnoreCase("INNER JOIN")) {
            this.markNonJoinWhereItems(t_we, toOracle);
         }

         if (vembuSQS.getWhereExpression() != null) {
            vembuSQS.getWhereExpression().addOperator("AND");
            t_we.setCloseBrace(")");
            t_we.setOpenBrace("(");
            if (toOracle) {
               vembuSQS.getWhereExpression().addWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
            } else {
               vembuSQS.getWhereExpression().addWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
            }
         } else if (toOracle) {
            vembuSQS.setWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
         } else {
            vembuSQS.setWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
         }
      } else if (type == 2) {
         if (this.joinExpression != null) {
            t_we = (WhereExpression)this.joinExpression.elementAt(0);
            t_we = this.getClonedWhereExpression(t_we);
            this.addRightJoin(t_we);
            if (vembuSQS.getWhereExpression() != null) {
               vembuSQS.getWhereExpression().addOperator("AND");
               t_we.setCloseBrace(")");
               t_we.setOpenBrace("(");
               if (toOracle) {
                  vembuSQS.getWhereExpression().addWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
               } else {
                  vembuSQS.getWhereExpression().addWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
               }
            } else if (toOracle) {
               vembuSQS.setWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
            } else {
               vembuSQS.setWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
            }
         } else if (toOracle) {
            vembuSQS.setWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
         } else {
            vembuSQS.setWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
         }

         SetOperatorClause soc = new SetOperatorClause();
         soc.setSetClause(" UNION ");
         SelectQueryStatement sqs = new SelectQueryStatement();
         sqs.setSelectStatement(vendorSQS.getSelectStatement());
         FromClause fromClause = new FromClause();
         FromClause vendorFromClause = vendorSQS.getFromClause();
         Vector vendorFromItemList = vendorFromClause.getFromItemList();
         new Vector();
         Vector vembuFromItemList = this.getTablesOnlyList(vendorFromItemList);
         fromClause.setFromClause("FROM");
         fromClause.setFromItemList(vembuFromItemList);
         sqs.setFromClause(fromClause);
         if (this.joinExpression != null) {
            t_we = (WhereExpression)this.joinExpression.elementAt(0);
            t_we = this.getClonedWhereExpression(t_we);
            this.addLeftJoin(t_we);
            if (vendorSQS.getWhereExpression() != null) {
               if (toOracle) {
                  sqs.setWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
               } else {
                  sqs.setWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
               }

               sqs.getWhereExpression().addOperator("AND");
               vendorSQS.getWhereExpression().setCloseBrace(")");
               vendorSQS.getWhereExpression().setOpenBrace("(");
               sqs.getWhereExpression().addWhereExpression(vendorSQS.getWhereExpression().toOracleSelect(vembuSQS, vendorSQS));
            } else if (toOracle) {
               sqs.setWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
            } else {
               sqs.setWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
            }
         } else if (toOracle) {
            sqs.setWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
         } else {
            sqs.setWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
         }

         sqs.setGroupByStatement(vendorSQS.getGroupByStatement());
         sqs.setForUpdateStatement(vendorSQS.getForUpdateStatement());
         sqs.setHavingStatement(vendorSQS.getHavingStatement());
         soc.setSelectQueryStatement(sqs);
         vembuSQS.setSetOperatorClause(soc);
      } else if (type == 3) {
         this.convertUsingListToWhereExp(this.UsingList, t_we, this.getPreviousFromTableFromTheFromItemList(vendorSQS));
         if (vembuSQS.getWhereExpression() != null) {
            vembuSQS.getWhereExpression().addOperator("AND");
            t_we.setCloseBrace(")");
            t_we.setOpenBrace("(");
            if (toOracle) {
               vembuSQS.getWhereExpression().addWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
            } else {
               vembuSQS.getWhereExpression().addWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
            }
         } else if (toOracle) {
            vembuSQS.setWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
         } else {
            vembuSQS.setWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
         }
      } else if (type == 4) {
         t_we = (WhereExpression)this.joinExpression.elementAt(0);
         if (vembuSQS.getWhereExpression() != null) {
            vembuSQS.getWhereExpression().addOperator("AND");
            t_we.setCloseBrace(")");
            t_we.setOpenBrace("(");
            vembuSQS.getWhereExpression().addWhereExpression(t_we.toInformixSelect(vembuSQS, vendorSQS));
         } else {
            vembuSQS.setWhereExpression(t_we.toInformixSelect(vembuSQS, vendorSQS));
         }
      }

   }

   public void convertUsingListToWhereExp(Vector v_ul, WhereExpression t_we, FromTable fc) throws ConvertException {
      WhereItem wi = new WhereItem();
      int i = false;
      String s_cn = null;
      if (fc != null) {
         if (fc.getAliasName() != null) {
            s_cn = fc.getAliasName();
         } else {
            s_cn = fc.getTableName().toString();
         }
      }

      for(int i = 0; i < v_ul.size(); ++i) {
         if (v_ul.elementAt(i) instanceof TableColumn) {
            Vector v_wi = new Vector();
            WhereColumn wc_wi = new WhereColumn();
            TableColumn tc = (TableColumn)v_ul.elementAt(i);
            tc.setTableName(s_cn);
            v_wi.addElement(tc);
            wc_wi.setColumnExpression(v_wi);
            wi.setLeftWhereExp(wc_wi);
            TableColumn tcr = new TableColumn();
            tcr.setColumnName(((TableColumn)v_ul.elementAt(i)).getColumnName());
            Vector v_wir = new Vector();
            WhereColumn wc_wir = new WhereColumn();
            if (this.aliasName != null) {
               tcr.setTableName(this.aliasName);
            } else {
               tcr.setTableName(this.tableName.toString());
            }

            v_wir.addElement(tcr);
            wc_wir.setColumnExpression(v_wir);
            wi.setRightWhereExp(wc_wir);
            wi.setOperator("=");
            if (!this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") && !this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
               if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") || this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                  wi.setRightJoin("+");
               }
            } else {
               wi.setLeftJoin("+");
            }

            t_we.addWhereItem(wi);
            wi = new WhereItem();
         } else {
            String s_cd = (String)this.UsingList.elementAt(i);
            if (s_cd.equals(",")) {
               t_we.addOperator("AND");
            } else {
               t_we.addOperator(s_cd);
            }
         }
      }

   }

   public void convertJoinExpToWhereExp(Vector v_jex, WhereExpression t_we, FromTable fc) throws ConvertException {
      WhereItem wi = new WhereItem();
      boolean LRflag = true;
      int i = false;
      String s_cn = null;
      if (fc != null) {
         if (fc.getAliasName() != null) {
            s_cn = fc.getAliasName();
         } else {
            s_cn = fc.getTableName().toString();
         }
      }

      int i;
      for(i = 0; i < v_jex.size(); ++i) {
         if (v_jex.elementAt(i) instanceof Vector) {
            this.convertJoinExpToWhereExp((Vector)v_jex.elementAt(i), t_we, fc);
         } else if (v_jex.elementAt(i) instanceof TableColumn) {
            Vector v_wi = new Vector();
            WhereColumn wc_wi = new WhereColumn();
            TableColumn tc = (TableColumn)v_jex.elementAt(i);
            if (fc != null && tc.getTableName() == null) {
               tc.setTableName(s_cn);
            }

            if (LRflag) {
               if (fc != null && tc.getTableName() == null) {
                  tc.setTableName(s_cn);
               }

               v_wi.addElement(tc);
               wc_wi.setColumnExpression(v_wi);
               wi.setLeftWhereExp(wc_wi);
               LRflag = false;
            } else {
               if (this.aliasName != null) {
                  if (tc.getTableName() == null) {
                     tc.setTableName(this.aliasName);
                  }
               } else if (tc.getTableName() == null) {
                  tc.setTableName(this.tableName.toString());
               }

               v_wi.addElement(tc);
               wc_wi.setColumnExpression(v_wi);
               wi.setRightWhereExp(wc_wi);
               LRflag = true;
            }
         } else if (v_jex.elementAt(i) instanceof String) {
            if (((String)v_jex.elementAt(i)).equalsIgnoreCase("=")) {
               TableColumn tc = new TableColumn();
               if (v_jex.elementAt(i + 1) instanceof TableColumn) {
                  tc = (TableColumn)v_jex.elementAt(i + 1);
               }

               if (!tc.getTableName().equalsIgnoreCase(this.aliasName) && !tc.getTableName().equalsIgnoreCase((String)this.tableName)) {
                  if (this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") | this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
                     wi.setLeftJoin("+");
                  } else if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") | this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                     wi.setRightJoin("+");
                  }
               } else if (this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") | this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
                  wi.setRightJoin("+");
               } else if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") | this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                  wi.setLeftJoin("+");
               }

               wi.setOperator("=");
            } else if (((String)v_jex.elementAt(i)).equalsIgnoreCase("AND") | ((String)v_jex.elementAt(i)).equalsIgnoreCase("OR")) {
               t_we.addOperator((String)v_jex.elementAt(i));
               t_we.addWhereItem(wi);
               wi = new WhereItem();
            }
         }
      }

      --i;
      if (!(v_jex.elementAt(i) instanceof Vector)) {
         t_we.addWhereItem(wi);
      }

   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      new SelectStatement();
      if (this.joinClause != null) {
         if (!SwisSQLAPI.getCanFullJoinExecute() || !this.joinClause.trim().equalsIgnoreCase("FULL JOIN") && !this.joinClause.trim().equalsIgnoreCase("FULL OUTER JOIN")) {
            sb.append("\n" + this.joinClause.toUpperCase());
         } else {
            sb.append("\nINNER JOIN");
         }
      }

      if (this.commentObj != null) {
         sb.append(" " + this.commentObj.toString().trim());
      }

      if (this.outer != null) {
         sb.append(" " + this.outer.toUpperCase());
      }

      if (this.outerOpenBrace != null) {
         sb.append(" " + this.outerOpenBrace.toUpperCase());
      }

      int i;
      String temp;
      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            ((SelectQueryStatement)this.tableName).setObjectContext(this.context);
            if (this.tableKeyword != null) {
               sb.append(" " + this.tableKeyword.toUpperCase());
            }

            sb.append("(" + this.tableName.toString() + ")");
            if (this.setOperatorClauseListForSubQuery != null) {
               for(i = 0; i < this.setOperatorClauseListForSubQuery.size(); ++i) {
                  sb.append(((SetOperatorClause)this.setOperatorClauseListForSubQuery.get(i)).toString());
               }
            }
         } else if (this.tableName instanceof FunctionCalls) {
            ((FunctionCalls)this.tableName).setObjectContext(this.context);
            if (this.tableKeyword != null) {
               sb.append(" " + this.tableKeyword + this.tableName.toString() + ")");
            } else {
               sb.append(this.tableName.toString());
            }
         } else if (this.tableName instanceof FromClause) {
            ((FromClause)this.tableName).setObjectContext(this.context);
            if (this.fromClauseOpenBraces != null) {
               sb.append(" (");
            }

            sb.append(this.tableName.toString());
            if (this.fromClauseClosedBraces != null) {
               sb.append(")");
            }
         } else if (this.context != null) {
            temp = null;
            if (this.origTableName == null) {
               temp = this.context.getEquivalent(this.tableName.toString()).toString();
            } else {
               Object obj = this.context.getEquivalent(this.origTableName);
               if ((obj == null || !obj.toString().equals(this.origTableName)) && obj != null) {
                  temp = obj.toString();
               } else {
                  temp = this.context.getEquivalent(this.tableName.toString()).toString();
               }
            }

            sb.append(" " + temp);
         } else {
            sb.append(" " + this.tableName.toString());
         }
      }

      if (this.commentObjAfterToken != null) {
         sb.append(" " + this.commentObjAfterToken.toString().trim());
      }

      if (this.isAS) {
         sb.append(" AS ");
      }

      if (this.commentobjAfterAsKeyword != null) {
         sb.append(" " + this.commentobjAfterAsKeyword.toString().trim());
      }

      if (this.aliasName != null) {
         if (this.context != null) {
            temp = this.context.getEquivalent(this.aliasName).toString();
            sb.append(" " + temp);
         } else {
            sb.append(" " + this.aliasName);
         }
      }

      if (this.commentobjAfterAliasName != null) {
         sb.append(" " + this.commentobjAfterAliasName.toString().trim());
      }

      if (this.pivotClause != null) {
         sb.append(this.pivotClause.toString());
      }

      if (this.holdLock != null) {
         sb.append("  " + this.holdLock);
      }

      if (this.queryPartitionClause == null && this.crossJoinForPartitionClause != null) {
         sb.append(" " + this.crossJoinForPartitionClause + " ");
      }

      if (this.outerClosedBrace != null) {
         sb.append(" " + this.outerClosedBrace.toUpperCase());
      }

      if (this.indexHint != null) {
         sb.append(this.indexHint);
      }

      if (this.onOrUsingJoin != null) {
         if (this.onOrUsingJoin.equalsIgnoreCase("using")) {
            if (this.joinExpression != null) {
               sb.append(" " + this.convertJoinExpToString(this.joinExpression));
            }

            sb.append(" " + this.onOrUsingJoin.toUpperCase() + " ");
            sb.append("(");

            for(i = 0; i < this.UsingList.size(); ++i) {
               sb.append(this.UsingList.elementAt(i).toString() + " ");
            }

            sb.append(")");
         } else {
            sb.append(" " + this.onOrUsingJoin.toUpperCase());
            if (this.joinExpression != null) {
               sb.append(" " + this.convertJoinExpToString(this.joinExpression));
            }
         }
      } else if (this.joinExpression != null) {
         sb.append(" " + this.convertJoinExpToString(this.joinExpression));
      }

      if (this.updateLock != null) {
         sb.append(" " + this.updateLock + " ");
      }

      if (this.setOperatorClauseForFullJoin != null) {
         sb.append(this.setOperatorClauseForFullJoin);
      }

      return sb.toString();
   }

   public String convertJoinExpToString(Vector v_jex) {
      StringBuffer sb = new StringBuffer();

      for(int i = 0; i < v_jex.size(); ++i) {
         if (v_jex.elementAt(i) instanceof Vector) {
            sb.append(this.convertJoinExpToString((Vector)v_jex.elementAt(i)));
         } else {
            sb.append(v_jex.elementAt(i).toString() + " ");
         }
      }

      return sb.toString();
   }

   private void collectAllWhereColumnsInWhereExpression(WhereExpression sqlWhereExpression) {
      if (sqlWhereExpression != null) {
         Vector sqlWhereItemsList = sqlWhereExpression.getWhereItems();
         if (sqlWhereItemsList != null) {
            for(int i = 0; i < sqlWhereItemsList.size(); ++i) {
               if (sqlWhereItemsList.get(0) instanceof WhereItem) {
                  WhereColumn leftColumnExpression = ((WhereItem)sqlWhereItemsList.get(i)).getLeftWhereExp();
                  WhereColumn rightColumnExpression = ((WhereItem)sqlWhereItemsList.get(i)).getRightWhereExp();
                  this.collectWhereColumnItems(leftColumnExpression);
                  this.collectWhereColumnItems(rightColumnExpression);
               }
            }
         }
      }

   }

   private void collectWhereColumnItems(WhereColumn sqlWhereColumn) {
      if (sqlWhereColumn != null) {
         Vector sqlWhereColumnList = sqlWhereColumn.getColumnExpression();
         this.removeAllTableReferenceFromColumnsInWhereExpression(sqlWhereColumnList);
      }

   }

   private void removeAllTableReferenceFromColumnsInWhereExpression(Vector sqlWhereColumnList) {
      if (sqlWhereColumnList != null) {
         for(int i = 0; i < sqlWhereColumnList.size(); ++i) {
            if (sqlWhereColumnList.get(i) instanceof TableColumn) {
               TableColumn sqlTableColumn = (TableColumn)sqlWhereColumnList.get(i);
               sqlTableColumn.setTableName((String)null);
               sqlTableColumn.setOwnerName((String)null);
            } else {
               Vector colExp;
               if (sqlWhereColumnList.get(i) instanceof FunctionCalls) {
                  colExp = ((FunctionCalls)sqlWhereColumnList.get(i)).getFunctionArguments();
                  this.removeAllTableReferenceFromColumnsInWhereExpression(colExp);
               } else if (sqlWhereColumnList.get(i) instanceof SelectColumn) {
                  colExp = ((SelectColumn)sqlWhereColumnList.get(i)).getColumnExpression();
                  this.removeAllTableReferenceFromColumnsInWhereExpression(colExp);
               }
            }
         }
      }

   }

   private void buildSetOperatorClauseForSubQuery(SelectQueryStatement tabName) {
      SetOperatorClause newSOC = new SetOperatorClause();

      for(int i = this.setOperatorClauseListForSubQuery.size() - 1; i >= 0; --i) {
         SetOperatorClause tempSOC = new SetOperatorClause();
         Object obj = this.setOperatorClauseListForSubQuery.get(i);
         new SetOperatorClause();
         if (obj != null && obj instanceof SetOperatorClause) {
            new SelectQueryStatement();
            SetOperatorClause socTemp = (SetOperatorClause)obj;
            String setClause = socTemp.getSetClause();
            SelectQueryStatement sqs = socTemp.getSelectQueryStatement();
            if (i == this.setOperatorClauseListForSubQuery.size() - 1) {
               newSOC = socTemp;
            } else {
               sqs.setSetOperatorClause(newSOC);
               tempSOC.setSetClause(setClause);
               tempSOC.setSelectQueryStatement(sqs);
               newSOC = tempSOC;
            }
         }
      }

      tabName.setSetOperatorClause(newSOC);
   }

   private void convertTableValueConstructorToSelectUnion(ValuesClause vc, ArrayList columnAliasList, SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      new ArrayList();

      Object obj;
      for(int j = 0; j < columnAliasList.size(); ++j) {
         obj = columnAliasList.get(j);
         if (obj instanceof String) {
            String str = ((String)obj).trim();
            if (str.equals("(") || str.equals(",") || str.equals(")")) {
               columnAliasList.remove(j);
            }
         }
      }

      this.groupRowValues(vc.getValuesList());
      ArrayList selectQueries = this.convertValuesToSelectQueryStatements(this.rowValuesList, columnAliasList);
      SelectQueryStatement lastSQS = null;
      obj = null;
      int size = selectQueries.size() - 1;

      for(int j = size; j != -1; --j) {
         SetOperatorClause currentSOC = new SetOperatorClause();
         if (j == size) {
            lastSQS = (SelectQueryStatement)selectQueries.get(size);
         } else {
            currentSOC.setSetClause("UNION ALL");
            currentSOC.setSelectQueryStatement(lastSQS);
            SelectQueryStatement tempSQS = (SelectQueryStatement)selectQueries.get(j);
            tempSQS.setSetOperatorClause(currentSOC);
            lastSQS = tempSQS;
         }
      }

      vendorSQS.setSetOperatorClause(lastSQS.getSetOperatorClause());
      vembuSQS.setSelectStatement(lastSQS.getSelectStatement().toOracleSelect(vembuSQS, vendorSQS));
      vembuSQS.setFromClause(lastSQS.getFromClause());
   }

   private void groupRowValues(List valuesList) throws ConvertException {
      Vector newValuesList = new Vector();
      int multiValListSize = valuesList.size();
      int firstOpenBraceIndex = valuesList.indexOf("(");
      int firstCloseBraceIndex = valuesList.indexOf(")");
      int lastCloseBraceIndex = valuesList.lastIndexOf(")");
      List firstValuesSet = valuesList.subList(firstOpenBraceIndex, firstCloseBraceIndex + 1);
      int firstValuesSetSize = firstValuesSet.size();

      for(int i = 0; i < firstValuesSetSize; ++i) {
         if (firstValuesSet.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)firstValuesSet.get(i);
            if (i != firstValuesSetSize - 2) {
               sc.setEndsWith(",");
            }

            newValuesList.add(sc);
         }
      }

      this.rowValuesList.add(newValuesList);
      if (firstCloseBraceIndex != lastCloseBraceIndex) {
         this.groupRowValues(valuesList.subList(firstCloseBraceIndex + 1, multiValListSize));
      }
   }

   private ArrayList convertValuesToSelectQueryStatements(ArrayList rowValuesList, ArrayList columnAliasList) throws ConvertException {
      ArrayList selectQueriesList = new ArrayList();
      FromClause fcTemp = new FromClause();
      FromTable ft1 = new FromTable();
      Vector fromItems = new Vector();
      fcTemp.setFromClause("FROM");
      ft1.setTableName("DUAL");
      fromItems.add(ft1);
      fcTemp.setFromItemList(fromItems);

      for(int j = 0; j < rowValuesList.size(); ++j) {
         if (j == 0) {
            Vector v = (Vector)rowValuesList.get(0);

            for(int k = 0; k < v.size(); ++k) {
               if (v.size() == columnAliasList.size()) {
                  Object obj = v.get(k);
                  if (obj instanceof SelectColumn) {
                     SelectColumn scTemp = (SelectColumn)obj;
                     scTemp.setAliasName(columnAliasList.get(k).toString());
                  }
               }
            }
         }

         SelectQueryStatement sqs = new SelectQueryStatement();
         SelectStatement selectStmt = new SelectStatement();
         selectStmt.setSelectClause("SELECT");
         selectStmt.setSelectItemList((Vector)rowValuesList.get(j));
         sqs.setSelectStatement(selectStmt);
         sqs.setFromClause(fcTemp);
         selectQueriesList.add(sqs);
      }

      return selectQueriesList;
   }

   public FromTable toVectorWiseSelect(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable ft = new FromTable();
      boolean isFullJoin = false;
      Vector tokenVector;
      int i;
      if (this.joinClause != null) {
         if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
            throw new ConvertException("Conversion failure..Natural join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
            throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
            throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
            throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
            throw new ConvertException("Conversion failure..Natural right join can't be converted");
         }

         if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
            throw new ConvertException("Conversion failure..Key join is not supported");
         }

         if (this.joinClause.trim().equalsIgnoreCase("OUTER")) {
            ft.setJoinClause("OUTER JOIN");
         }

         WhereExpression we;
         Vector operatorList;
         if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            we = ((WhereExpression)this.joinExpression.elementAt(0)).toVectorWiseSelect(vembuSQS, vendorSQS);
            operatorList = new Vector();
            we = we.toVectorWiseSelect(vendorSQS, vembuSQS);
            operatorList.addElement(we);
            ft.setJoinExpression(operatorList);
         } else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            we = (WhereExpression)this.joinExpression.elementAt(0);
            operatorList = we.getOperator();
            we.setOperator(operatorList);
            we = we.toVectorWiseSelect(vembuSQS, vendorSQS);
            tokenVector = new Vector();
            tokenVector.addElement(we);
            ft.setJoinExpression(tokenVector);
         }

         ft.setOnOrUsingJoin(this.onOrUsingJoin);
         if (this.UsingList != null) {
            Vector v = new Vector();

            for(i = 0; i < this.UsingList.size(); ++i) {
               if (this.UsingList.elementAt(i) instanceof TableColumn) {
                  v.addElement(((TableColumn)this.UsingList.elementAt(i)).toVectorWiseSelect(vembuSQS, vendorSQS));
               } else {
                  v.addElement(this.UsingList.elementAt(i));
               }
            }

            ft.setUsingList(v);
         }

         if (!isFullJoin) {
            ft.setJoinClause(this.joinClause);
         } else {
            ft.setJoinClause("LEFT OUTER JOIN");
         }

         if (this.joinClause.trim().equalsIgnoreCase("JOIN")) {
            ft.setJoinClause("INNER JOIN");
         }

         this.removeJoinExpressionForCrossJoin(ft);
      }

      if (this.getOuter() != null) {
         ft.setOuter(this.outer);
         if (this.getOuterOpenBrace() != null) {
            ft.setOuterOpenBrace(this.outerOpenBrace);
         }

         if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
         }
      }

      if (this.outerClosedBrace != null) {
         ft.setOuterClosedBrace(this.outerClosedBrace);
      }

      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            SelectQueryStatement subQueryStatement = (SelectQueryStatement)this.tableName;
            if (vendorSQS != null) {
               subQueryStatement.setReportsMeta(vendorSQS == null ? false : vendorSQS.getReportsMeta());
               subQueryStatement.setAmazonRedShiftFlag(vendorSQS.isAmazonRedShift());
               subQueryStatement.setPgsqlFlag(vendorSQS.isPgsqlLive());
               subQueryStatement.setMSAzureFlag(vendorSQS.isMSAzure());
               subQueryStatement.setMSAzureWareHouseFlag(vendorSQS.isMSAzureWareHouse());
               subQueryStatement.setOracleLiveFlag(vendorSQS.isOracleLive());
               subQueryStatement.setDenodoFlag(vendorSQS.isDenodo());
               subQueryStatement.setQueryConversionPropHandler(vendorSQS.getQueryConvPropHandler());
            }

            subQueryStatement.setOrderByStatement((OrderByStatement)null);
            subQueryStatement.setLimitClause((LimitClause)null);
            subQueryStatement.setFetchClause((FetchClause)null);
            ft.setTableName(subQueryStatement.toVectorWise());
            if (this.aliasName == null && this.setOperatorClauseListForSubQuery.size() == 0) {
               ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
            }
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException();
            }

            if (this.tableName instanceof WithStatement) {
               ft.setTableName(((WithStatement)this.tableName).toVectorWise());
            } else if (this.tableName instanceof FromClause) {
               ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
               ft.setTableName(((FromClause)this.tableName).toVectorWiseSelect(vembuSQS, vendorSQS));
               ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
            } else {
               String table_Name_String = (String)this.tableName;
               i = table_Name_String.indexOf("@");
               ft.setTableName(StringFunctions.replaceFirst(".", "..", (String)this.tableName));
               tokenVector = new Vector();
               String table_Name = (String)this.tableName;
               StringTokenizer st = new StringTokenizer(table_Name, ".");

               int count;
               for(count = 0; st.hasMoreTokens(); ++count) {
                  tokenVector.add(st.nextToken());
               }

               String tempTableName;
               String quotedIdenStartString;
               String quotedIdenEndString;
               String tableNameSubString;
               if (count == 1 && i != -1) {
                  tempTableName = (String)tokenVector.elementAt(0);
                  tableNameSubString = tempTableName.substring(0, i);
                  quotedIdenStartString = tempTableName.substring(i + 1);
                  quotedIdenEndString = quotedIdenStartString + "." + tableNameSubString;
                  ft.setTableName(quotedIdenEndString);
               } else if (count == 2 && i != -1) {
                  tempTableName = (String)tokenVector.elementAt(1);
                  int tableAtIndex = tempTableName.indexOf("@");
                  quotedIdenStartString = tempTableName.substring(0, tableAtIndex);
                  quotedIdenEndString = tempTableName.substring(tableAtIndex + 1);
                  String sqlDataBaseAndTableName = quotedIdenEndString + "." + (String)tokenVector.elementAt(0) + "." + quotedIdenStartString;
                  ft.setTableName(sqlDataBaseAndTableName);
               }

               tempTableName = (String)ft.getTableName();
               tableNameSubString = "";
               quotedIdenStartString = "[";
               quotedIdenEndString = "]";
               if (tempTableName.startsWith("`")) {
                  quotedIdenStartString = "`";
               }

               if (tempTableName.endsWith("`")) {
                  quotedIdenEndString = "`";
               }

               int startIndex = tempTableName.indexOf(quotedIdenStartString);
               if (startIndex != -1) {
                  while(startIndex != -1) {
                     if (startIndex == 0) {
                        tableNameSubString = tempTableName.substring(1);
                        tableNameSubString = "\"" + tableNameSubString;
                        tableNameSubString = StringFunctions.replaceFirst("`", quotedIdenEndString, tableNameSubString);
                        tempTableName = tableNameSubString;
                        startIndex = tableNameSubString.indexOf(quotedIdenStartString);
                     } else {
                        tableNameSubString = tempTableName.substring(0, startIndex);
                        tableNameSubString = tableNameSubString + "\"";
                        tableNameSubString = tableNameSubString + tempTableName.substring(startIndex + 1);
                        tableNameSubString = StringFunctions.replaceFirst("\"", quotedIdenEndString, tableNameSubString);
                        tempTableName = tableNameSubString;
                        startIndex = tableNameSubString.indexOf(quotedIdenStartString);
                     }
                  }

                  ft.setTableName(tableNameSubString);
               }
            }
         }
      }

      if (this.setOperatorClauseListForSubQuery.size() != 0) {
         ArrayList setOperatorClauseList = new ArrayList();

         for(i = 0; i < this.setOperatorClauseListForSubQuery.size(); ++i) {
            setOperatorClauseList.add(((SetOperatorClause)this.setOperatorClauseListForSubQuery.get(i)).toVectorWiseSelect(vembuSQS, vendorSQS));
         }

         ft.setSetOperatorClauseListForSubQuery(setOperatorClauseList);
      }

      ft.setIsAS(this.isAS);
      if (this.aliasName != null) {
         if (this.aliasName.trim().equalsIgnoreCase("`at`") || this.aliasName.trim().equalsIgnoreCase("at")) {
            this.aliasName = "\"" + this.aliasName.trim().replaceAll("`", "") + "\"";
         }

         if (this.aliasName.charAt(0) == '\'') {
            ft.setAliasName(this.aliasName.replace('\'', ' ').trim());
         } else if (this.aliasName.charAt(0) == '`') {
            ft.setAliasName(this.aliasName.replace('`', ' ').trim());
         } else {
            ft.setAliasName(this.aliasName);
         }
      }

      return ft;
   }

   public FromTable toReplaceTblCol(SelectQueryStatement vembuSQS, SelectQueryStatement vendorSQS) throws ConvertException {
      FromTable fromtblConv = new FromTable();
      String inTblName;
      HashMap cteViewDetsMap;
      if (this.tableName != null) {
         if (this.tableName instanceof SelectQueryStatement) {
            if (this.aliasName == null) {
               throw new ConvertException("Alias name is not provided for Subquery ", "ALIAS_FOR_FROM_SUB_QUERY_REQUIRED");
            }

            if (vendorSQS.isCTESupported()) {
               throw new ConvertException("Subquery is not supported inside CTE", "SUB_QUERY_NOT_SUPPORTED_INSIDE_CTE");
            }

            String tblKeyStr = GeneralUtil.trimIfAliasNameIsEnclosed(this.aliasName);
            if (tblKeyStr.isEmpty()) {
               throw new ConvertException("Alias Name cannot be empty", "SQL_ALIASNAME_EMPTY_IN_QT");
            }

            vendorSQS.addTableDets(tblKeyStr, tblKeyStr);
            SelectQueryStatement subQueryOrig = (SelectQueryStatement)this.tableName;
            subQueryOrig.setIsSubQuerySource(true);
            subQueryOrig.setPropAndHandlerFromSQS(vendorSQS);
            subQueryOrig.setCTEViewDetsMap(vendorSQS.getCTEViewDetsMap());
            SelectQueryStatement subQueryConv = subQueryOrig.toReplaceTblCol();
            vendorSQS.addTableColumnDets(tblKeyStr, subQueryConv.getSelColNameMap());
            vendorSQS.setAliasColumns(subQueryConv.getAliasColumns());
            fromtblConv.setTableName(subQueryConv);
         } else {
            if (this.tableName instanceof FunctionCalls) {
               throw new ConvertException();
            }

            if (this.tableName instanceof WithStatement) {
               throw new ConvertException("CTE is not supported in Subquery", "CTE_NOT_SUPPORTED_IN_SUBQUERY");
            }

            if (this.tableName instanceof FromClause) {
               fromtblConv.setTableName(((FromClause)this.tableName).toReplaceTblCol(vembuSQS, vendorSQS));
            } else if (this.tableName instanceof String) {
               if (vendorSQS.getQueryConvDataHandler() != null) {
                  cteViewDetsMap = vendorSQS.getQueryConvDataHandler().getTableDetsMap();
                  HashMap<String, HashMap<String, String>> tblColDets = vendorSQS.getQueryConvDataHandler().getTableColumnDetsMap();
                  HashMap<String, CommonTableExpression> cteTblColDets = vendorSQS.getCTEViewDetsMap();
                  String[] replacedTblDets = null;
                  inTblName = GeneralUtil.trimIfTblColIsEnclosed(this.tableName.toString());
                  String tblKeyStr;
                  if (CastingUtil.getValueIgnoreCase(cteViewDetsMap, inTblName) != null) {
                     replacedTblDets = GeneralUtil.getReplacedTblColDets(cteViewDetsMap, this.tableName.toString());
                     String rawTblName = replacedTblDets[0];
                     tblKeyStr = replacedTblDets[1];
                     String outTblName = replacedTblDets[2];
                     String tblKeyStr = rawTblName;
                     if (this.aliasName != null) {
                        tblKeyStr = GeneralUtil.trimIfAliasNameIsEnclosed(this.aliasName);
                        if (tblKeyStr.isEmpty()) {
                           throw new ConvertException("Alias Name cannot be empty", "SQL_ALIASNAME_EMPTY_IN_QT");
                        }

                        if (vendorSQS.getTableDetsMap() != null && vendorSQS.getTableDetsMap().containsKey(tblKeyStr) && !vendorSQS.isCTESupported()) {
                           if (!vendorSQS.getCanAllowExceptionStacking()) {
                              throw new ConvertException("The same table alias is used for more than one table. \\n\\n * Kindly use distinct alias names.", "NOT_UNIQUE_TABLE_OR_ALIAS_TOP", new Object[]{tblKeyStr});
                           }

                           vendorSQS.getQueryConvDataHandler().addExceptionMap("NOT_UNIQUE_TABLE_OR_ALIAS_TOP", new Object[]{tblKeyStr, null});
                        }

                        vendorSQS.addTableDets(tblKeyStr, tblKeyStr);
                     } else {
                        vendorSQS.addTableDets(rawTblName, tblKeyStr);
                     }

                     vendorSQS.addTableColumnDets(tblKeyStr, (HashMap)CastingUtil.getValueIgnoreCase(tblColDets, rawTblName));
                     outTblName = GeneralUtil.checkAndEncloseColumnName(outTblName);
                     fromtblConv.setTableName(outTblName);
                  } else if (cteTblColDets != null && cteTblColDets.containsKey(inTblName)) {
                     tblKeyStr = inTblName;
                     if (this.aliasName != null) {
                        tblKeyStr = GeneralUtil.trimIfAliasNameIsEnclosed(this.aliasName);
                        if (vendorSQS.getTableDetsMap() != null && vendorSQS.getTableDetsMap().containsKey(tblKeyStr) && !vendorSQS.isCTESupported()) {
                           if (!vendorSQS.getCanAllowExceptionStacking()) {
                              throw new ConvertException("The same table alias is used for more than one table. \\n\\n * Kindly use distinct alias names.", "NOT_UNIQUE_TABLE_OR_ALIAS_TOP", new Object[]{tblKeyStr});
                           }

                           vendorSQS.getQueryConvDataHandler().addExceptionMap("NOT_UNIQUE_TABLE_OR_ALIAS_TOP", new Object[]{tblKeyStr, null});
                        }

                        vendorSQS.addTableDets(tblKeyStr, tblKeyStr);
                     } else {
                        vendorSQS.addTableDets(inTblName, inTblName);
                     }

                     CommonTableExpression commonTableExpression = (CommonTableExpression)cteTblColDets.get(inTblName);
                     vendorSQS.addTableColumnDets(tblKeyStr, commonTableExpression.getWithColDets());
                     fromtblConv.setTableName(this.tableName);
                  } else {
                     fromtblConv.setTableName(this.tableName);
                     if (!vendorSQS.getCanAllowExceptionStacking()) {
                        throw new ConvertException("Invalid table used ", "INVALID_TABLE", new Object[]{inTblName});
                     }

                     vendorSQS.getQueryConvDataHandler().addExceptionMap("INVALID_TABLE", new Object[]{inTblName, null});
                  }
               } else {
                  fromtblConv.setTableName(this.tableName);
               }
            }
         }
      }

      if (this.aliasName != null) {
         fromtblConv.setAliasName(this.aliasName);
      }

      if (this.joinClause != null) {
         fromtblConv.setJoinClause(this.joinClause);
      }

      if (this.tableKeyword != null) {
         fromtblConv.setTableKeyword(this.tableKeyword);
      }

      if (this.pivotClause != null) {
         if (!(this.getTableName() instanceof SelectQueryStatement)) {
            throw new ConvertException("PIVOT_TBLSRC_WITHOUT_SUBQUERY", "PIVOT_TBLSRC_WITHOUT_SUBQUERY", new Object[]{this.pivotClause.getPivot(), this.getTableName().toString()});
         }

         if (((SelectQueryStatement)this.getTableName()).getCTEViewDetsMap() != null && ((SelectQueryStatement)this.getTableName()).getCTEViewDetsMap().size() > 0) {
            throw new ConvertException("PIVOT_INVALID_INCTE_VALUE", "PIVOT_INVALID_INCTE_VALUE", new Object[]{this.pivotClause.getPivot(), this.getTableName().toString()});
         }

         fromtblConv.setPivotClause(this.pivotClause.toReplaceTblCol(vembuSQS, vendorSQS, GeneralUtil.trimIfAliasNameIsEnclosed(this.aliasName), (SelectQueryStatement)this.getTableName()));
      }

      Vector usingListConv;
      if (this.joinExpression != null) {
         usingListConv = new Vector();
         WhereExpression whExpConv = (WhereExpression)this.joinExpression.elementAt(0);
         usingListConv.addElement(whExpConv.toReplaceTblCol(vembuSQS, vendorSQS));
         fromtblConv.setJoinExpression(usingListConv);
      }

      int i;
      int setOprClListSize;
      if (this.UsingList != null) {
         usingListConv = new Vector();
         i = 0;

         for(setOprClListSize = this.UsingList.size(); i < setOprClListSize; ++i) {
            if (this.UsingList.elementAt(i) instanceof TableColumn) {
               TableColumn tbCl = (TableColumn)this.UsingList.elementAt(i);
               inTblName = this.getAliasName() != null ? this.getAliasName() : (String)this.getTableName();
               tbCl.setTableName(inTblName);
               tbCl = tbCl.toReplaceTblCol(vembuSQS, vendorSQS);
               tbCl.setTableName((String)null);
               usingListConv.addElement(tbCl);
            } else {
               usingListConv.addElement(this.UsingList.elementAt(i));
            }
         }

         fromtblConv.setUsingList(usingListConv);
      }

      if (this.onOrUsingJoin != null) {
         fromtblConv.setOnOrUsingJoin(this.onOrUsingJoin);
      }

      if (this.outer != null) {
         fromtblConv.setOuter(this.outer);
      }

      if (this.outerOpenBrace != null) {
         fromtblConv.setOuterOpenBrace(this.outerOpenBrace);
      }

      if (this.outerClosedBrace != null) {
         fromtblConv.setOuterClosedBrace(this.outerClosedBrace);
      }

      fromtblConv.setIsAS(this.isAS);
      if (this.holdLock != null) {
         fromtblConv.setHoldLock(this.holdLock);
      }

      if (this.fromClauseOpenBraces != null) {
         fromtblConv.setFromClauseOpenBraces(this.fromClauseOpenBraces);
      }

      if (this.fromClauseClosedBraces != null) {
         fromtblConv.setFromClauseClosedBraces(this.fromClauseClosedBraces);
      }

      if (this.lock != null) {
         fromtblConv.setLock(this.lock);
      }

      if (this.with != null) {
         fromtblConv.setWith(this.with);
      }

      if (this.indexHint != null) {
         fromtblConv.setIndexHint(this.indexHint);
      }

      if (this.origTableName != null) {
         fromtblConv.setOrigTableName(this.origTableName);
      }

      if (this.setOperatorClauseListForSubQuery != null) {
         ArrayList setOprClListConv = new ArrayList();
         i = 0;

         for(setOprClListSize = this.setOperatorClauseListForSubQuery.size(); i < setOprClListSize; ++i) {
            setOprClListConv.add(((SetOperatorClause)this.setOperatorClauseListForSubQuery.get(i)).toReplaceTblCol(vembuSQS, vendorSQS));
         }

         fromtblConv.setSetOperatorClauseListForSubQuery(setOprClListConv);
      }

      if (this.commentObj != null) {
         fromtblConv.setCommentClass(this.commentObj);
      }

      if (this.commentObjAfterToken != null) {
         fromtblConv.setCommentClassAfterToken(this.commentObjAfterToken);
      }

      if (this.commentobjAfterAsKeyword != null) {
         fromtblConv.setCommentobjAfterAsKeyword(this.commentobjAfterAsKeyword);
      }

      if (this.commentobjAfterAliasName != null) {
         fromtblConv.setCommentobjAfterAliasName(this.commentobjAfterAliasName);
      }

      if (this.queryPartitionClause != null) {
         fromtblConv.setQueryPartitionClause(this.queryPartitionClause.toReplaceTblCol(vembuSQS, vendorSQS));
      }

      if (this.pivotClause != null) {
         cteViewDetsMap = vendorSQS.getCTEViewDetsMap();
         if (vendorSQS.isCTESupported() || cteViewDetsMap != null && cteViewDetsMap.containsKey(this.getTableName().toString())) {
            throw new ConvertException("PIVOT_INVALID_INCTE_VALUE", "PIVOT_INVALID_INCTE_VALUE", new Object[]{this.pivotClause.getPivot(), this.getTableName().toString()});
         }
      }

      return fromtblConv;
   }

   private void removeJoinExpressionForCrossJoin(FromTable ft) {
      try {
         if (this.joinClause != null && this.onOrUsingJoin != null && this.joinExpression != null && this.joinClause.equalsIgnoreCase("CROSS JOIN") && this.onOrUsingJoin.equalsIgnoreCase("ON")) {
            WhereExpression we = (WhereExpression)this.joinExpression.elementAt(0);
            if (we != null) {
               Vector wi = we.getWhereItems();
               if (wi != null && wi.size() == 1 && ((WhereItem)((WhereItem)wi.get(0))).getLeftWhereExp().toString().trim().equalsIgnoreCase("1") && ((WhereItem)((WhereItem)wi.get(0))).getRightWhereExp().toString().trim().equalsIgnoreCase("1") && ((WhereItem)((WhereItem)wi.get(0))).getOperator() != null && ((WhereItem)((WhereItem)wi.get(0))).getOperator().trim().equals("=")) {
                  ft.setJoinExpression((Vector)null);
                  ft.setOnOrUsingJoin((String)null);
               } else {
                  ft.setJoinClause("INNER JOIN");
               }
            }
         }
      } catch (Exception var4) {
      }

   }

   public void getFullJoinCount(SelectQueryStatement sqs, int allowedCount) throws ConvertException {
      if (sqs.getFromClause() != null) {
         Vector fromList = sqs.getFromClause().getFromItemList();
         int fullJoinCount = 0;

         for(int i = 0; i < fromList.size(); ++i) {
            Object fromObj = fromList.get(i);
            if (fromObj instanceof FromTable) {
               FromTable newFt = (FromTable)((FromTable)fromObj).clone();
               if (newFt.getJoinClause() != null && (newFt.getJoinClause().equalsIgnoreCase("FULL JOIN") || newFt.getJoinClause().equalsIgnoreCase("FULL OUTER JOIN"))) {
                  ++fullJoinCount;
               }
            } else if (fromObj instanceof FromClause) {
               FromClause newFc = (FromClause)((FromClause)fromObj).clone();
               Vector newFromItemList = newFc.getFromItemList();

               for(int index = 0; index < newFromItemList.size(); ++index) {
                  if (newFromItemList.elementAt(i) instanceof FromTable) {
                     FromTable newFt = (FromTable)newFromItemList.get(i);
                     if (newFt.getJoinClause() != null && (newFt.getJoinClause().equalsIgnoreCase("FULL JOIN") || newFt.getJoinClause().equalsIgnoreCase("FULL OUTER JOIN"))) {
                        ++fullJoinCount;
                     }
                  }
               }
            }
         }

         if (fullJoinCount > allowedCount) {
            throw new ConvertException("Only " + allowedCount + " Full Join operator is allowed.", "MULTIPLE_FULL_JOIN_ALLOWED", new Object[]{allowedCount});
         }
      }

   }
}
