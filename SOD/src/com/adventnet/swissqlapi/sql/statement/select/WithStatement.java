package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.insert.CommonTableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.util.FunctionValidateHandler;
import com.adventnet.swissqlapi.util.QueryConvDataHandler;
import com.adventnet.swissqlapi.util.QueryConvPropsHandler;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class WithStatement implements SwisSQLStatement {
   private CommentClass commentObject;
   private UserObjectContext objectContext = null;
   String with;
   String recursive;
   SelectQueryStatement withSQS = null;
   Vector commonTableExprList = null;
   SelectQueryStatement derivedTableQuery = null;
   QueryConvPropsHandler queryConvPropsHandler;
   QueryConvDataHandler queryConvDataHandler;
   FunctionValidateHandler validationHandler;
   Vector a = new Vector();
   Hashtable cteAliasQueryMap = new Hashtable();

   public void setWith(String withStr) {
      this.with = withStr;
   }

   public void setRecursive(String recursiveStr) {
      this.recursive = recursiveStr;
   }

   public void setWithSQS(SelectQueryStatement sqs) {
      this.withSQS = sqs;
   }

   public void setCommonTableExpressionList(Vector cteList) {
      this.commonTableExprList = cteList;
   }

   public void setHandlers(QueryConvPropsHandler propsHandler, QueryConvDataHandler dataHandler, FunctionValidateHandler funValHandler) {
      this.queryConvPropsHandler = propsHandler;
      this.queryConvDataHandler = dataHandler;
      this.validationHandler = funValHandler;
   }

   public String getWith() {
      return this.with;
   }

   public String getRecursive() {
      return this.recursive;
   }

   public SelectQueryStatement getWithSQS() {
      return this.withSQS;
   }

   public Vector getCommonTableExpressionList() {
      return this.commonTableExprList;
   }

   public void setQueryConversionPropHandler(QueryConvPropsHandler handler) {
      this.queryConvPropsHandler = handler;
   }

   public void setQueryConvDataHandler(QueryConvDataHandler handler) {
      this.queryConvDataHandler = handler;
   }

   public void setValidationHandler(FunctionValidateHandler handler) {
      this.validationHandler = handler;
   }

   public QueryConvPropsHandler getQueryConvPropHandler() {
      return this.queryConvPropsHandler;
   }

   public QueryConvDataHandler getQueryConvDataHandler() {
      return this.queryConvDataHandler;
   }

   public FunctionValidateHandler getValidationHandler() {
      return this.validationHandler;
   }

   public SwisSQLStatement toOracle() throws ConvertException {
      WithStatement ws = this.copyObjectValues();
      String withTemp = ws.getWith();
      if (withTemp != null) {
         ws.setWith((String)null);
      }

      Vector commonTableExpressionList = ws.getCommonTableExpressionList();
      new Vector();
      SelectQueryStatement withSelectStmt = ws.getWithSQS();
      withSelectStmt = withSelectStmt.toOracle();
      Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
      Hashtable addSelectStmt = new Hashtable();
      Vector aliasNamesList = new Vector();

      for(int i = 0; i < commonTableExpressionList.size(); ++i) {
         CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
         String viewNameTemp = cte.getViewName().toString();
         ArrayList newColumnList = new ArrayList();
         ArrayList columnList = cte.getColumnList();
         if (columnList != null) {
            for(int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
               if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                  newColumnList.add(columnList.get(colListInd));
               }
            }
         }

         Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
         int k;
         if (cteSQSSelectColList.size() == newColumnList.size()) {
            for(k = 0; k < cteSQSSelectColList.size(); ++k) {
               if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                  SelectColumn cteSelCol = (SelectColumn)cteSQSSelectColList.get(k);
                  cteSelCol.setIsAS("AS");
                  cteSelCol.setAliasName(newColumnList.get(k).toString());
               }
            }
         }

         for(k = 0; k < withSelectStmtFromItems.size(); ++k) {
            FromTable fromTable = (FromTable)withSelectStmtFromItems.get(k);
            if (fromTable.getTableName() instanceof String) {
               new Vector();
               String fromTableName = fromTable.getTableName().toString();
               new SelectQueryStatement();
               String hashKey = cte.getViewName().toString();
               SelectQueryStatement ss;
               if (addSelectStmt.containsKey(hashKey.trim())) {
                  ss = (SelectQueryStatement)addSelectStmt.get(hashKey.trim());
               } else {
                  ss = cte.getSelectQueryStatement().toOracle();
                  addSelectStmt.put(hashKey.trim(), ss);
               }

               String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
               aliasNamesList.add(cte.getViewName().getTableName());
               if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }

               if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                  Vector ssFromItems = ss.getFromClause().getFromItemList();

                  for(int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                     for(int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                        FromTable ssFromTable = (FromTable)ssFromItems.get(ssIndex);
                        if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                           ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                           ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                           SelectQueryStatement ss1 = (SelectQueryStatement)ssFromTable.getTableName();
                           Vector ssFromTableFromItems = ss1.getFromClause().getFromItemList();

                           for(int x = 0; x < ssFromTableFromItems.size(); ++x) {
                              for(int y = 0; y < aliasNamesList.size(); ++y) {
                                 if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                    FromTable ss1FromTable = (FromTable)ssFromTableFromItems.get(x);
                                    ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                    ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                 }
                              }
                           }
                        }
                     }
                  }

                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }
            }
         }
      }

      this.derivedTableQuery = withSelectStmt;
      return this.derivedTableQuery;
   }

   public SwisSQLStatement toMSSQLServer() throws ConvertException {
      WithStatement ws = this.copyObjectValues();
      String withTemp = ws.getWith();
      if (withTemp != null) {
         ws.setWith((String)null);
      }

      Vector commonTableExpressionList = ws.getCommonTableExpressionList();
      new Vector();
      SelectQueryStatement withSelectStmt = ws.getWithSQS();
      withSelectStmt = withSelectStmt.toMSSQLServer();
      Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
      Hashtable addSelectStmt = new Hashtable();
      Vector aliasNamesList = new Vector();

      for(int i = 0; i < commonTableExpressionList.size(); ++i) {
         CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
         String viewNameTemp = cte.getViewName().toString();
         ArrayList newColumnList = new ArrayList();
         ArrayList columnList = cte.getColumnList();
         if (columnList != null) {
            for(int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
               if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                  newColumnList.add(columnList.get(colListInd));
               }
            }
         }

         Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
         int k;
         if (cteSQSSelectColList.size() == newColumnList.size()) {
            for(k = 0; k < cteSQSSelectColList.size(); ++k) {
               if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                  SelectColumn cteSelCol = (SelectColumn)cteSQSSelectColList.get(k);
                  cteSelCol.setIsAS("AS");
                  cteSelCol.setAliasName(newColumnList.get(k).toString());
               }
            }
         }

         for(k = 0; k < withSelectStmtFromItems.size(); ++k) {
            FromTable fromTable = (FromTable)withSelectStmtFromItems.get(k);
            if (fromTable.getTableName() instanceof String) {
               new Vector();
               String fromTableName = fromTable.getTableName().toString();
               SelectQueryStatement ss = cte.getSelectQueryStatement().toMSSQLServer();
               String hashKey = cte.getViewName().toString();
               String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
               addSelectStmt.put(hashKey.trim(), ss);
               aliasNamesList.add(cte.getViewName().getTableName());
               if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }

               if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                  Vector ssFromItems = ss.getFromClause().getFromItemList();

                  for(int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                     for(int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                        FromTable ssFromTable = (FromTable)ssFromItems.get(ssIndex);
                        if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                           ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                           ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                           SelectQueryStatement ss1 = (SelectQueryStatement)ssFromTable.getTableName();
                           Vector ssFromTableFromItems = ss1.getFromClause().getFromItemList();

                           for(int x = 0; x < ssFromTableFromItems.size(); ++x) {
                              for(int y = 0; y < aliasNamesList.size(); ++y) {
                                 if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                    FromTable ss1FromTable = (FromTable)ssFromTableFromItems.get(x);
                                    ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                    ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                 }
                              }
                           }
                        }
                     }
                  }

                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }
            }
         }
      }

      this.derivedTableQuery = withSelectStmt;
      return this.derivedTableQuery;
   }

   public SwisSQLStatement toSybase() throws ConvertException {
      WithStatement ws = this.copyObjectValues();
      String withTemp = ws.getWith();
      if (withTemp != null) {
         ws.setWith((String)null);
      }

      Vector commonTableExpressionList = ws.getCommonTableExpressionList();
      new Vector();
      SelectQueryStatement withSelectStmt = ws.getWithSQS();
      withSelectStmt = withSelectStmt.toSybase();
      Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
      Hashtable addSelectStmt = new Hashtable();
      Vector aliasNamesList = new Vector();

      for(int i = 0; i < commonTableExpressionList.size(); ++i) {
         CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
         String viewNameTemp = cte.getViewName().toString();
         ArrayList newColumnList = new ArrayList();
         ArrayList columnList = cte.getColumnList();
         if (columnList != null) {
            for(int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
               if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                  newColumnList.add(columnList.get(colListInd));
               }
            }
         }

         Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
         int k;
         if (cteSQSSelectColList.size() == newColumnList.size()) {
            for(k = 0; k < cteSQSSelectColList.size(); ++k) {
               if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                  SelectColumn cteSelCol = (SelectColumn)cteSQSSelectColList.get(k);
                  cteSelCol.setIsAS("AS");
                  cteSelCol.setAliasName(newColumnList.get(k).toString());
               }
            }
         }

         for(k = 0; k < withSelectStmtFromItems.size(); ++k) {
            FromTable fromTable = (FromTable)withSelectStmtFromItems.get(k);
            if (fromTable.getTableName() instanceof String) {
               new Vector();
               String fromTableName = fromTable.getTableName().toString();
               SelectQueryStatement ss = cte.getSelectQueryStatement().toSybase();
               String hashKey = cte.getViewName().toString();
               String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
               addSelectStmt.put(hashKey.trim(), ss);
               aliasNamesList.add(cte.getViewName().getTableName());
               if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }

               if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                  Vector ssFromItems = ss.getFromClause().getFromItemList();

                  for(int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                     for(int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                        FromTable ssFromTable = (FromTable)ssFromItems.get(ssIndex);
                        if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                           ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                           ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                           SelectQueryStatement ss1 = (SelectQueryStatement)ssFromTable.getTableName();
                           Vector ssFromTableFromItems = ss1.getFromClause().getFromItemList();

                           for(int x = 0; x < ssFromTableFromItems.size(); ++x) {
                              for(int y = 0; y < aliasNamesList.size(); ++y) {
                                 if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                    FromTable ss1FromTable = (FromTable)ssFromTableFromItems.get(x);
                                    ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                    ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                 }
                              }
                           }
                        }
                     }
                  }

                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }
            }
         }
      }

      this.derivedTableQuery = withSelectStmt;
      return this.derivedTableQuery;
   }

   public SwisSQLStatement toDB2() throws ConvertException {
      return this;
   }

   public SwisSQLStatement toSnowflake() throws ConvertException {
      WithStatement ws = this.copyObjectValues();
      String withTemp = ws.getWith();
      if (withTemp != null) {
         ws.setWith((String)null);
      }

      Vector commonTableExpressionList = ws.getCommonTableExpressionList();
      new Vector();
      SelectQueryStatement withSelectStmt = ws.getWithSQS();
      withSelectStmt = withSelectStmt.toSnowflake();
      Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
      Hashtable addSelectStmt = new Hashtable();
      Vector aliasNamesList = new Vector();

      for(int i = 0; i < commonTableExpressionList.size(); ++i) {
         CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
         String viewNameTemp = cte.getViewName().toString();
         ArrayList newColumnList = new ArrayList();
         ArrayList columnList = cte.getColumnList();
         if (columnList != null) {
            for(int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
               if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                  newColumnList.add(columnList.get(colListInd));
               }
            }
         }

         Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
         int k;
         if (cteSQSSelectColList.size() == newColumnList.size()) {
            for(k = 0; k < cteSQSSelectColList.size(); ++k) {
               if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                  SelectColumn cteSelCol = (SelectColumn)cteSQSSelectColList.get(k);
                  cteSelCol.setIsAS("AS");
                  cteSelCol.setAliasName(newColumnList.get(k).toString());
               }
            }
         }

         for(k = 0; k < withSelectStmtFromItems.size(); ++k) {
            FromTable fromTable = (FromTable)withSelectStmtFromItems.get(k);
            if (fromTable.getTableName() instanceof String) {
               new Vector();
               String fromTableName = fromTable.getTableName().toString();
               SelectQueryStatement ss = cte.getSelectQueryStatement().toSnowflake();
               String hashKey = cte.getViewName().toString();
               String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
               addSelectStmt.put(hashKey.trim(), ss);
               aliasNamesList.add(cte.getViewName().getTableName());
               if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }

               if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                  Vector ssFromItems = ss.getFromClause().getFromItemList();

                  for(int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                     for(int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                        FromTable ssFromTable = (FromTable)ssFromItems.get(ssIndex);
                        if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                           ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                           ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                           SelectQueryStatement ss1 = (SelectQueryStatement)ssFromTable.getTableName();
                           Vector ssFromTableFromItems = ss1.getFromClause().getFromItemList();

                           for(int x = 0; x < ssFromTableFromItems.size(); ++x) {
                              for(int y = 0; y < aliasNamesList.size(); ++y) {
                                 if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                    FromTable ss1FromTable = (FromTable)ssFromTableFromItems.get(x);
                                    ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                    ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                 }
                              }
                           }
                        }
                     }
                  }

                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }
            }
         }
      }

      this.derivedTableQuery = withSelectStmt;
      return this.derivedTableQuery;
   }

   public SwisSQLStatement toMySQL() throws ConvertException {
      WithStatement toWS;
      Vector commonTableExpressionList;
      if (this.queryConvPropsHandler != null && this.queryConvPropsHandler.isCTEQuerySupport()) {
         toWS = new WithStatement();
         toWS.setWith(this.getWith());
         toWS.setRecursive(this.getRecursive());
         SelectQueryStatement withSQS = this.getWithSQS();
         withSQS.setHandlers(this.queryConvPropsHandler, this.queryConvDataHandler, this.validationHandler);
         toWS.setHandlers(this.queryConvPropsHandler, this.queryConvDataHandler, this.validationHandler);
         toWS.setWithSQS(withSQS.toMySQL());
         commonTableExpressionList = new Vector();
         Vector fromCommonTableExprList = this.getCommonTableExpressionList();
         CommonTableExpression tCte = new CommonTableExpression();

         for(int i = 0; i < fromCommonTableExprList.size(); ++i) {
            Object fCte = fromCommonTableExprList.get(i);
            if (fCte instanceof CommonTableExpression) {
               ((CommonTableExpression)fCte).getSelectQueryStatement().setHandlers(this.queryConvPropsHandler, this.queryConvDataHandler, this.validationHandler);
               commonTableExpressionList.add(((CommonTableExpression)fCte).toMySQL());
            } else {
               commonTableExpressionList.addElement(tCte);
            }
         }

         toWS.setCommonTableExpressionList(commonTableExpressionList);
         return toWS;
      } else {
         toWS = this.copyObjectValues();
         String withTemp = toWS.getWith();
         if (withTemp != null) {
            toWS.setWith((String)null);
         }

         commonTableExpressionList = toWS.getCommonTableExpressionList();
         new Vector();
         SelectQueryStatement withSelectStmt = toWS.getWithSQS();
         withSelectStmt = withSelectStmt.toMySQL();
         Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
         Hashtable addSelectStmt = new Hashtable();
         Vector aliasNamesList = new Vector();

         for(int i = 0; i < commonTableExpressionList.size(); ++i) {
            CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
            String viewNameTemp = cte.getViewName().toString();
            ArrayList newColumnList = new ArrayList();
            ArrayList columnList = cte.getColumnList();
            if (columnList != null) {
               for(int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
                  if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                     newColumnList.add(columnList.get(colListInd));
                  }
               }
            }

            Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
            int k;
            if (cteSQSSelectColList.size() == newColumnList.size()) {
               for(k = 0; k < cteSQSSelectColList.size(); ++k) {
                  if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                     SelectColumn cteSelCol = (SelectColumn)cteSQSSelectColList.get(k);
                     cteSelCol.setIsAS("AS");
                     cteSelCol.setAliasName(newColumnList.get(k).toString());
                  }
               }
            }

            for(k = 0; k < withSelectStmtFromItems.size(); ++k) {
               FromTable fromTable = (FromTable)withSelectStmtFromItems.get(k);
               if (fromTable.getTableName() instanceof String) {
                  new Vector();
                  String fromTableName = fromTable.getTableName().toString();
                  SelectQueryStatement ss = cte.getSelectQueryStatement().toMySQL();
                  String hashKey = cte.getViewName().toString();
                  String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
                  addSelectStmt.put(hashKey.trim(), ss);
                  aliasNamesList.add(cte.getViewName().getTableName());
                  if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                     fromTable.setTableName(ss);
                     fromTable.setAliasName(fromTableName);
                  }

                  if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                     Vector ssFromItems = ss.getFromClause().getFromItemList();

                     for(int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                        for(int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                           FromTable ssFromTable = (FromTable)ssFromItems.get(ssIndex);
                           if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                              ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                              ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                              SelectQueryStatement ss1 = (SelectQueryStatement)ssFromTable.getTableName();
                              Vector ssFromTableFromItems = ss1.getFromClause().getFromItemList();

                              for(int x = 0; x < ssFromTableFromItems.size(); ++x) {
                                 for(int y = 0; y < aliasNamesList.size(); ++y) {
                                    if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                       FromTable ss1FromTable = (FromTable)ssFromTableFromItems.get(x);
                                       ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                       ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                    }
                                 }
                              }
                           }
                        }
                     }

                     fromTable.setTableName(ss);
                     fromTable.setAliasName(fromTableName);
                  }
               }
            }
         }

         this.derivedTableQuery = withSelectStmt;
         return this.derivedTableQuery;
      }
   }

   public SwisSQLStatement toInformix() throws ConvertException {
      WithStatement ws = this.copyObjectValues();
      String withTemp = ws.getWith();
      if (withTemp != null) {
         ws.setWith((String)null);
      }

      Vector commonTableExpressionList = ws.getCommonTableExpressionList();
      new Vector();
      SelectQueryStatement withSelectStmt = ws.getWithSQS();
      withSelectStmt = withSelectStmt.toInformix();
      Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
      Hashtable addSelectStmt = new Hashtable();
      Vector aliasNamesList = new Vector();

      for(int i = 0; i < commonTableExpressionList.size(); ++i) {
         CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
         String viewNameTemp = cte.getViewName().toString();
         ArrayList newColumnList = new ArrayList();
         ArrayList columnList = cte.getColumnList();
         if (columnList != null) {
            for(int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
               if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                  newColumnList.add(columnList.get(colListInd));
               }
            }
         }

         Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
         int k;
         if (cteSQSSelectColList.size() == newColumnList.size()) {
            for(k = 0; k < cteSQSSelectColList.size(); ++k) {
               if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                  SelectColumn cteSelCol = (SelectColumn)cteSQSSelectColList.get(k);
                  cteSelCol.setIsAS("AS");
                  cteSelCol.setAliasName(newColumnList.get(k).toString());
               }
            }
         }

         for(k = 0; k < withSelectStmtFromItems.size(); ++k) {
            FromTable fromTable = (FromTable)withSelectStmtFromItems.get(k);
            if (fromTable.getTableName() instanceof String) {
               new Vector();
               String fromTableName = fromTable.getTableName().toString();
               SelectQueryStatement ss = cte.getSelectQueryStatement().toInformix();
               String hashKey = cte.getViewName().toString();
               String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
               addSelectStmt.put(hashKey.trim(), ss);
               aliasNamesList.add(cte.getViewName().getTableName());
               if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }

               if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                  Vector ssFromItems = ss.getFromClause().getFromItemList();

                  for(int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                     for(int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                        FromTable ssFromTable = (FromTable)ssFromItems.get(ssIndex);
                        if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                           ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                           ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                           SelectQueryStatement ss1 = (SelectQueryStatement)ssFromTable.getTableName();
                           Vector ssFromTableFromItems = ss1.getFromClause().getFromItemList();

                           for(int x = 0; x < ssFromTableFromItems.size(); ++x) {
                              for(int y = 0; y < aliasNamesList.size(); ++y) {
                                 if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                    FromTable ss1FromTable = (FromTable)ssFromTableFromItems.get(x);
                                    ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                    ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                 }
                              }
                           }
                        }
                     }
                  }

                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }
            }
         }
      }

      this.derivedTableQuery = withSelectStmt;
      return this.derivedTableQuery;
   }

   public SwisSQLStatement toBigQuery() throws ConvertException {
      WithStatement ws = this.copyObjectValues();
      String withTemp = ws.getWith();
      if (withTemp != null) {
         ws.setWith((String)null);
      }

      Vector commonTableExpressionList = ws.getCommonTableExpressionList();
      new Vector();
      SelectQueryStatement withSelectStmt = ws.getWithSQS();
      withSelectStmt = withSelectStmt.toBigQuery();
      Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
      Hashtable addSelectStmt = new Hashtable();
      Vector aliasNamesList = new Vector();

      for(int i = 0; i < commonTableExpressionList.size(); ++i) {
         CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
         String viewNameTemp = cte.getViewName().toString();
         ArrayList newColumnList = new ArrayList();
         ArrayList columnList = cte.getColumnList();
         if (columnList != null) {
            for(int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
               if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                  newColumnList.add(columnList.get(colListInd));
               }
            }
         }

         Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
         int k;
         if (cteSQSSelectColList.size() == newColumnList.size()) {
            for(k = 0; k < cteSQSSelectColList.size(); ++k) {
               if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                  SelectColumn cteSelCol = (SelectColumn)cteSQSSelectColList.get(k);
                  cteSelCol.setIsAS("AS");
                  cteSelCol.setAliasName(newColumnList.get(k).toString());
               }
            }
         }

         for(k = 0; k < withSelectStmtFromItems.size(); ++k) {
            FromTable fromTable = (FromTable)withSelectStmtFromItems.get(k);
            if (fromTable.getTableName() instanceof String) {
               new Vector();
               String fromTableName = fromTable.getTableName().toString();
               SelectQueryStatement ss = cte.getSelectQueryStatement().toBigQuery();
               String hashKey = cte.getViewName().toString();
               String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
               addSelectStmt.put(hashKey.trim(), ss);
               aliasNamesList.add(cte.getViewName().getTableName());
               if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }

               if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                  Vector ssFromItems = ss.getFromClause().getFromItemList();

                  for(int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                     for(int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                        FromTable ssFromTable = (FromTable)ssFromItems.get(ssIndex);
                        if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                           ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                           ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                           SelectQueryStatement ss1 = (SelectQueryStatement)ssFromTable.getTableName();
                           Vector ssFromTableFromItems = ss1.getFromClause().getFromItemList();

                           for(int x = 0; x < ssFromTableFromItems.size(); ++x) {
                              for(int y = 0; y < aliasNamesList.size(); ++y) {
                                 if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                    FromTable ss1FromTable = (FromTable)ssFromTableFromItems.get(x);
                                    ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                    ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                 }
                              }
                           }
                        }
                     }
                  }

                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }
            }
         }
      }

      this.derivedTableQuery = withSelectStmt;
      return this.derivedTableQuery;
   }

   public SwisSQLStatement toAthena() throws ConvertException {
      WithStatement ws = this.copyObjectValues();
      String withTemp = ws.getWith();
      if (withTemp != null) {
         ws.setWith((String)null);
      }

      Vector commonTableExpressionList = ws.getCommonTableExpressionList();
      new Vector();
      SelectQueryStatement withSelectStmt = ws.getWithSQS();
      withSelectStmt = withSelectStmt.toAthena();
      Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
      Hashtable addSelectStmt = new Hashtable();
      Vector aliasNamesList = new Vector();

      for(int i = 0; i < commonTableExpressionList.size(); ++i) {
         CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
         String viewNameTemp = cte.getViewName().toString();
         ArrayList newColumnList = new ArrayList();
         ArrayList columnList = cte.getColumnList();
         if (columnList != null) {
            for(int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
               if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                  newColumnList.add(columnList.get(colListInd));
               }
            }
         }

         Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
         int k;
         if (cteSQSSelectColList.size() == newColumnList.size()) {
            for(k = 0; k < cteSQSSelectColList.size(); ++k) {
               if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                  SelectColumn cteSelCol = (SelectColumn)cteSQSSelectColList.get(k);
                  cteSelCol.setIsAS("AS");
                  cteSelCol.setAliasName(newColumnList.get(k).toString());
               }
            }
         }

         for(k = 0; k < withSelectStmtFromItems.size(); ++k) {
            FromTable fromTable = (FromTable)withSelectStmtFromItems.get(k);
            if (fromTable.getTableName() instanceof String) {
               new Vector();
               String fromTableName = fromTable.getTableName().toString();
               SelectQueryStatement ss = cte.getSelectQueryStatement().toAthena();
               String hashKey = cte.getViewName().toString();
               String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
               addSelectStmt.put(hashKey.trim(), ss);
               aliasNamesList.add(cte.getViewName().getTableName());
               if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }

               if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                  Vector ssFromItems = ss.getFromClause().getFromItemList();

                  for(int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                     for(int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                        FromTable ssFromTable = (FromTable)ssFromItems.get(ssIndex);
                        if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                           ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                           ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                           SelectQueryStatement ss1 = (SelectQueryStatement)ssFromTable.getTableName();
                           Vector ssFromTableFromItems = ss1.getFromClause().getFromItemList();

                           for(int x = 0; x < ssFromTableFromItems.size(); ++x) {
                              for(int y = 0; y < aliasNamesList.size(); ++y) {
                                 if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                    FromTable ss1FromTable = (FromTable)ssFromTableFromItems.get(x);
                                    ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                    ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                 }
                              }
                           }
                        }
                     }
                  }

                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }
            }
         }
      }

      this.derivedTableQuery = withSelectStmt;
      return this.derivedTableQuery;
   }

   public SwisSQLStatement toSapHana() throws ConvertException {
      WithStatement ws = this.copyObjectValues();
      String withTemp = ws.getWith();
      if (withTemp != null) {
         ws.setWith((String)null);
      }

      Vector commonTableExpressionList = ws.getCommonTableExpressionList();
      SelectQueryStatement withSelectStmt = ws.getWithSQS();
      withSelectStmt = withSelectStmt.toSapHana();
      Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
      Hashtable addSelectStmt = new Hashtable();
      Vector aliasNamesList = new Vector();

      for(int i = 0; i < commonTableExpressionList.size(); ++i) {
         CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
         String viewNameTemp = cte.getViewName().toString();
         ArrayList newColumnList = new ArrayList();
         ArrayList columnList = cte.getColumnList();
         if (columnList != null) {
            for(int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
               if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                  newColumnList.add(columnList.get(colListInd));
               }
            }
         }

         Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
         int k;
         if (cteSQSSelectColList.size() == newColumnList.size()) {
            for(k = 0; k < cteSQSSelectColList.size(); ++k) {
               if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                  SelectColumn cteSelCol = (SelectColumn)cteSQSSelectColList.get(k);
                  cteSelCol.setIsAS("AS");
                  cteSelCol.setAliasName(newColumnList.get(k).toString());
               }
            }
         }

         for(k = 0; k < withSelectStmtFromItems.size(); ++k) {
            FromTable fromTable = (FromTable)withSelectStmtFromItems.get(k);
            if (fromTable.getTableName() instanceof String) {
               new Vector();
               String fromTableName = fromTable.getTableName().toString();
               SelectQueryStatement ss = cte.getSelectQueryStatement().toSapHana();
               String hashKey = cte.getViewName().toString();
               String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
               addSelectStmt.put(hashKey.trim(), ss);
               aliasNamesList.add(cte.getViewName().getTableName());
               if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }

               if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                  Vector ssFromItems = ss.getFromClause().getFromItemList();

                  for(int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                     for(int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                        FromTable ssFromTable = (FromTable)ssFromItems.get(ssIndex);
                        if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                           ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                           ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                           SelectQueryStatement ss1 = (SelectQueryStatement)ssFromTable.getTableName();
                           Vector ssFromTableFromItems = ss1.getFromClause().getFromItemList();

                           for(int x = 0; x < ssFromTableFromItems.size(); ++x) {
                              for(int y = 0; y < aliasNamesList.size(); ++y) {
                                 if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                    FromTable ss1FromTable = (FromTable)ssFromTableFromItems.get(x);
                                    ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                    ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                 }
                              }
                           }
                        }
                     }
                  }

                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }
            }
         }
      }

      this.derivedTableQuery = withSelectStmt;
      return this.derivedTableQuery;
   }

   public SwisSQLStatement toSqlite() throws ConvertException {
      WithStatement ws = this.copyObjectValues();
      String withTemp = ws.getWith();
      if (withTemp != null) {
         ws.setWith((String)null);
      }

      Vector commonTableExpressionList = ws.getCommonTableExpressionList();
      SelectQueryStatement withSelectStmt = ws.getWithSQS();
      withSelectStmt = withSelectStmt.toSapHana();
      Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
      Hashtable addSelectStmt = new Hashtable();
      Vector aliasNamesList = new Vector();

      for(int i = 0; i < commonTableExpressionList.size(); ++i) {
         CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
         String viewNameTemp = cte.getViewName().toString();
         ArrayList newColumnList = new ArrayList();
         ArrayList columnList = cte.getColumnList();
         if (columnList != null) {
            for(int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
               if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                  newColumnList.add(columnList.get(colListInd));
               }
            }
         }

         Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
         int k;
         if (cteSQSSelectColList.size() == newColumnList.size()) {
            for(k = 0; k < cteSQSSelectColList.size(); ++k) {
               if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                  SelectColumn cteSelCol = (SelectColumn)cteSQSSelectColList.get(k);
                  cteSelCol.setIsAS("AS");
                  cteSelCol.setAliasName(newColumnList.get(k).toString());
               }
            }
         }

         for(k = 0; k < withSelectStmtFromItems.size(); ++k) {
            FromTable fromTable = (FromTable)withSelectStmtFromItems.get(k);
            if (fromTable.getTableName() instanceof String) {
               new Vector();
               String fromTableName = fromTable.getTableName().toString();
               SelectQueryStatement ss = cte.getSelectQueryStatement().toSapHana();
               String hashKey = cte.getViewName().toString();
               String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
               addSelectStmt.put(hashKey.trim(), ss);
               aliasNamesList.add(cte.getViewName().getTableName());
               if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }

               if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                  Vector ssFromItems = ss.getFromClause().getFromItemList();

                  for(int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                     for(int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                        FromTable ssFromTable = (FromTable)ssFromItems.get(ssIndex);
                        if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                           ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                           ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                           SelectQueryStatement ss1 = (SelectQueryStatement)ssFromTable.getTableName();
                           Vector ssFromTableFromItems = ss1.getFromClause().getFromItemList();

                           for(int x = 0; x < ssFromTableFromItems.size(); ++x) {
                              for(int y = 0; y < aliasNamesList.size(); ++y) {
                                 if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                    FromTable ss1FromTable = (FromTable)ssFromTableFromItems.get(x);
                                    ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                    ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                 }
                              }
                           }
                        }
                     }
                  }

                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }
            }
         }
      }

      this.derivedTableQuery = withSelectStmt;
      return this.derivedTableQuery;
   }

   public SwisSQLStatement toExcel() throws ConvertException {
      WithStatement ws = this.copyObjectValues();
      String withTemp = ws.getWith();
      if (withTemp != null) {
         ws.setWith((String)null);
      }

      Vector commonTableExpressionList = ws.getCommonTableExpressionList();
      SelectQueryStatement withSelectStmt = ws.getWithSQS();
      withSelectStmt = withSelectStmt.toSapHana();
      Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
      Hashtable addSelectStmt = new Hashtable();
      Vector aliasNamesList = new Vector();

      for(int i = 0; i < commonTableExpressionList.size(); ++i) {
         CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
         String viewNameTemp = cte.getViewName().toString();
         ArrayList newColumnList = new ArrayList();
         ArrayList columnList = cte.getColumnList();
         if (columnList != null) {
            for(int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
               if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                  newColumnList.add(columnList.get(colListInd));
               }
            }
         }

         Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
         int k;
         if (cteSQSSelectColList.size() == newColumnList.size()) {
            for(k = 0; k < cteSQSSelectColList.size(); ++k) {
               if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                  SelectColumn cteSelCol = (SelectColumn)cteSQSSelectColList.get(k);
                  cteSelCol.setIsAS("AS");
                  cteSelCol.setAliasName(newColumnList.get(k).toString());
               }
            }
         }

         for(k = 0; k < withSelectStmtFromItems.size(); ++k) {
            FromTable fromTable = (FromTable)withSelectStmtFromItems.get(k);
            if (fromTable.getTableName() instanceof String) {
               new Vector();
               String fromTableName = fromTable.getTableName().toString();
               SelectQueryStatement ss = cte.getSelectQueryStatement().toSapHana();
               String hashKey = cte.getViewName().toString();
               String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
               addSelectStmt.put(hashKey.trim(), ss);
               aliasNamesList.add(cte.getViewName().getTableName());
               if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }

               if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                  Vector ssFromItems = ss.getFromClause().getFromItemList();

                  for(int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                     for(int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                        FromTable ssFromTable = (FromTable)ssFromItems.get(ssIndex);
                        if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                           ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                           ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                           SelectQueryStatement ss1 = (SelectQueryStatement)ssFromTable.getTableName();
                           Vector ssFromTableFromItems = ss1.getFromClause().getFromItemList();

                           for(int x = 0; x < ssFromTableFromItems.size(); ++x) {
                              for(int y = 0; y < aliasNamesList.size(); ++y) {
                                 if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                    FromTable ss1FromTable = (FromTable)ssFromTableFromItems.get(x);
                                    ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                    ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                 }
                              }
                           }
                        }
                     }
                  }

                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }
            }
         }
      }

      this.derivedTableQuery = withSelectStmt;
      return this.derivedTableQuery;
   }

   public SwisSQLStatement toPostgreSQL() throws ConvertException {
      WithStatement toWS;
      Vector commonTableExpressionList;
      if (this.queryConvPropsHandler != null && this.queryConvPropsHandler.isCTEQuerySupport()) {
         toWS = new WithStatement();
         toWS.setWith(this.getWith());
         toWS.setRecursive(this.getRecursive());
         SelectQueryStatement withSQS = this.getWithSQS();
         withSQS.setHandlers(this.queryConvPropsHandler, this.queryConvDataHandler, this.validationHandler);
         withSQS.setIsCTESupported(true);
         toWS.setWithSQS(withSQS.toPostgreSQL());
         commonTableExpressionList = new Vector();
         Vector fromCommonTableExprList = this.getCommonTableExpressionList();
         CommonTableExpression tCte = new CommonTableExpression();

         for(int i = 0; i < fromCommonTableExprList.size(); ++i) {
            Object fCte = fromCommonTableExprList.get(i);
            if (fCte instanceof CommonTableExpression) {
               ((CommonTableExpression)fCte).getSelectQueryStatement().setReportsMeta(this.withSQS.getReportsMeta());
               ((CommonTableExpression)fCte).getSelectQueryStatement().setHandlers(this.queryConvPropsHandler, this.queryConvDataHandler, this.validationHandler);
               commonTableExpressionList.add(((CommonTableExpression)fCte).toPostgreSQL());
            } else {
               commonTableExpressionList.addElement(tCte);
            }
         }

         toWS.setCommonTableExpressionList(commonTableExpressionList);
         return toWS;
      } else {
         toWS = this.copyObjectValues();
         String withTemp = toWS.getWith();
         if (withTemp != null) {
            toWS.setWith((String)null);
         }

         commonTableExpressionList = toWS.getCommonTableExpressionList();
         new Vector();
         SelectQueryStatement withSelectStmt = toWS.getWithSQS();
         withSelectStmt = withSelectStmt.toPostgreSQL();
         Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
         Hashtable addSelectStmt = new Hashtable();
         Vector aliasNamesList = new Vector();

         for(int i = 0; i < commonTableExpressionList.size(); ++i) {
            CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
            String viewNameTemp = cte.getViewName().toString();
            ArrayList newColumnList = new ArrayList();
            ArrayList columnList = cte.getColumnList();
            if (columnList != null) {
               for(int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
                  if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                     newColumnList.add(columnList.get(colListInd));
                  }
               }
            }

            Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
            int k;
            if (cteSQSSelectColList.size() == newColumnList.size()) {
               for(k = 0; k < cteSQSSelectColList.size(); ++k) {
                  if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                     SelectColumn cteSelCol = (SelectColumn)cteSQSSelectColList.get(k);
                     cteSelCol.setIsAS("AS");
                     cteSelCol.setAliasName(newColumnList.get(k).toString());
                  }
               }
            }

            for(k = 0; k < withSelectStmtFromItems.size(); ++k) {
               FromTable fromTable = (FromTable)withSelectStmtFromItems.get(k);
               if (fromTable.getTableName() instanceof String) {
                  new Vector();
                  String fromTableName = fromTable.getTableName().toString();
                  SelectQueryStatement ss = cte.getSelectQueryStatement().toPostgreSQL();
                  String hashKey = cte.getViewName().toString();
                  String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
                  addSelectStmt.put(hashKey.trim(), ss);
                  aliasNamesList.add(cte.getViewName().getTableName());
                  if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                     fromTable.setTableName(ss);
                     fromTable.setAliasName(fromTableName);
                  }

                  if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                     Vector ssFromItems = ss.getFromClause().getFromItemList();

                     for(int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                        for(int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                           FromTable ssFromTable = (FromTable)ssFromItems.get(ssIndex);
                           if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                              ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                              ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                              SelectQueryStatement ss1 = (SelectQueryStatement)ssFromTable.getTableName();
                              Vector ssFromTableFromItems = ss1.getFromClause().getFromItemList();

                              for(int x = 0; x < ssFromTableFromItems.size(); ++x) {
                                 for(int y = 0; y < aliasNamesList.size(); ++y) {
                                    if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                       FromTable ss1FromTable = (FromTable)ssFromTableFromItems.get(x);
                                       ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                       ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                    }
                                 }
                              }
                           }
                        }
                     }

                     fromTable.setTableName(ss);
                     fromTable.setAliasName(fromTableName);
                  }
               }
            }
         }

         this.derivedTableQuery = withSelectStmt;
         return this.derivedTableQuery;
      }
   }

   public SwisSQLStatement toANSISQL() throws ConvertException {
      WithStatement ws = this.copyObjectValues();
      Vector commonTableExpressionList = ws.getCommonTableExpressionList();
      Vector convertedCommonTableExpressionList = new Vector();

      for(int i = 0; i < commonTableExpressionList.size(); ++i) {
         CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
         cte = cte.toANSISQL(ws, ws);
         convertedCommonTableExpressionList.add(cte);
      }

      ws.setCommonTableExpressionList(convertedCommonTableExpressionList);
      SelectQueryStatement withSelectSQS = ws.getWithSQS();
      withSelectSQS = withSelectSQS.toANSI();
      return ws;
   }

   public SwisSQLStatement toTeradata() throws ConvertException {
      WithStatement ws = this.copyObjectValues();
      Vector commonTableExpressionList = ws.getCommonTableExpressionList();
      Vector convertedCommonTableExpressionList = new Vector();

      for(int i = 0; i < commonTableExpressionList.size(); ++i) {
         CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
         convertedCommonTableExpressionList.add(cte);
      }

      ws.setCommonTableExpressionList(convertedCommonTableExpressionList);
      SelectQueryStatement withSelectSQS = ws.getWithSQS();
      withSelectSQS = withSelectSQS.toTeradata();
      return ws;
   }

   public SwisSQLStatement toTimesTen() throws ConvertException {
      return this;
   }

   public SwisSQLStatement toNetezza() throws ConvertException {
      WithStatement ws = this.copyObjectValues();
      String withTemp = ws.getWith();
      if (withTemp != null) {
         ws.setWith((String)null);
      }

      Vector commonTableExpressionList = ws.getCommonTableExpressionList();
      int cteListSize = commonTableExpressionList.size();

      for(int i = 0; i < cteListSize; ++i) {
         CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
         TableObject aliasTO = cte.getViewName();
         aliasTO.toNetezza();
         ArrayList newColumnList = new ArrayList();
         ArrayList columnList = cte.getColumnList();
         if (columnList != null) {
            for(int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
               if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                  newColumnList.add(columnList.get(colListInd));
               }
            }
         }

         Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
         if (cteSQSSelectColList.size() == newColumnList.size()) {
            for(int k = 0; k < cteSQSSelectColList.size(); ++k) {
               if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                  SelectColumn cteSelCol = (SelectColumn)cteSQSSelectColList.get(k);
                  cteSelCol.setIsAS("AS");
                  cteSelCol.setAliasName(newColumnList.get(k).toString());
               }
            }
         }

         this.cteAliasQueryMap.put(aliasTO.toString().toUpperCase(), cte.getSelectQueryStatement().toNetezza());
      }

      Enumeration cteEnum = this.cteAliasQueryMap.elements();

      SelectQueryStatement withSelectStmt;
      while(cteEnum.hasMoreElements()) {
         withSelectStmt = (SelectQueryStatement)cteEnum.nextElement();
         this.convertCTEAliastoDerivedTable(withSelectStmt);
      }

      withSelectStmt = ws.getWithSQS().toNetezza();
      this.convertCTEAliastoDerivedTable(withSelectStmt);
      this.derivedTableQuery = withSelectStmt;
      return this.derivedTableQuery;
   }

   private SelectQueryStatement convertCTEAliastoDerivedTable(SelectQueryStatement sQuery) {
      Vector localQueryFromItems = sQuery.getFromClause().getFromItemList();

      for(int j = 0; j < localQueryFromItems.size(); ++j) {
         FromTable fromTable = (FromTable)localQueryFromItems.get(j);
         if (fromTable.getTableName() instanceof String) {
            new Vector();
            String fromTableName = fromTable.getTableName().toString();
            if (this.cteAliasQueryMap.containsKey(fromTableName.toUpperCase())) {
               fromTable.setTableName(this.cteAliasQueryMap.get(fromTableName.toUpperCase()));
               fromTable.setAliasName(fromTableName);
            }
         } else if (fromTable.getTableName() instanceof SelectQueryStatement) {
            SelectQueryStatement subQuery = this.convertCTEAliastoDerivedTable((SelectQueryStatement)fromTable.getTableName());
            fromTable.setTableName(subQuery);
         }
      }

      return sQuery;
   }

   private WithStatement copyObjectValues() {
      WithStatement ws = new WithStatement();
      ws.setWith(this.getWith());
      ws.setCommonTableExpressionList(this.getCommonTableExpressionList());
      ws.setWithSQS(this.getWithSQS());
      return ws;
   }

   public String toString() {
      if (this.derivedTableQuery != null) {
         return this.derivedTableQuery.toString();
      } else {
         StringBuffer sb = new StringBuffer();
         if (this.with != null) {
            sb.append(this.with.toUpperCase() + " ");
         }

         if (this.recursive != null) {
            sb.append(this.recursive.toUpperCase() + " ");
         }

         if (this.commonTableExprList != null) {
            for(int i = 0; i < this.commonTableExprList.size(); ++i) {
               sb.append(this.commonTableExprList.get(i).toString());
               if (i != this.commonTableExprList.size() - 1) {
                  sb.append(" ,\n");
               }
            }
         }

         if (this.withSQS != null) {
            sb.append("\n" + this.withSQS.toString() + " ");
         }

         return sb.toString();
      }
   }

   public String toOracleString() throws ConvertException {
      return this.toOracle().toString();
   }

   public String toMSSQLServerString() throws ConvertException {
      return this.toMSSQLServer().toString();
   }

   public String toSybaseString() throws ConvertException {
      return this.toSybase().toString();
   }

   public String toDB2String() throws ConvertException {
      return this.toDB2().toString();
   }

   public String toPostgreSQLString() throws ConvertException {
      return this.toPostgreSQL().toString();
   }

   public String toMySQLString() throws ConvertException {
      return this.toMySQL().toString();
   }

   public String toANSIString() throws ConvertException {
      return this.toANSISQL().toString();
   }

   public String toTeradataString() throws ConvertException {
      return this.toTeradata().toString();
   }

   public String toInformixString() throws ConvertException {
      return this.toInformix().toString();
   }

   public String toTimesTenString() throws ConvertException {
      return this.toTimesTen().toString();
   }

   public String toNetezzaString() throws ConvertException {
      return this.toNetezza().toString();
   }

   public String toBigQueryString() throws ConvertException {
      return this.toBigQuery().toString();
   }

   public String toSnowflakeString() throws ConvertException {
      return this.toSnowflake().toString();
   }

   public String toAthenaString() throws ConvertException {
      return this.toAthena().toString();
   }

   public String toSapHanaString() throws ConvertException {
      return this.toSapHana().toString();
   }

   public String toSqliteString() throws ConvertException {
      return this.toSqlite().toString();
   }

   public String toExcelString() throws ConvertException {
      return this.toExcel().toString();
   }

   public String toMsAccessJdbcString() throws ConvertException {
      return null;
   }

   public void setCommentClass(CommentClass commentObject) {
      this.commentObject = commentObject;
   }

   public CommentClass getCommentClass() {
      return this.commentObject;
   }

   public UserObjectContext getObjectContext() {
      return this.objectContext;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.objectContext = obj;
   }

   public String removeIndent(String formattedSqlString) {
      String s_ri = formattedSqlString.replace('\n', ' ');
      s_ri = s_ri.replace('\t', ' ');
      return s_ri;
   }

   public SwisSQLStatement toVectorWise() throws ConvertException {
      WithStatement ws = this.copyObjectValues();
      String withTemp = ws.getWith();
      if (withTemp != null) {
         ws.setWith((String)null);
      }

      Vector commonTableExpressionList = ws.getCommonTableExpressionList();
      new Vector();
      SelectQueryStatement withSelectStmt = ws.getWithSQS();
      withSelectStmt = withSelectStmt.toMySQL();
      Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
      Hashtable addSelectStmt = new Hashtable();
      Vector aliasNamesList = new Vector();

      for(int i = 0; i < commonTableExpressionList.size(); ++i) {
         CommonTableExpression cte = (CommonTableExpression)commonTableExpressionList.get(i);
         String viewNameTemp = cte.getViewName().toString();
         ArrayList newColumnList = new ArrayList();
         ArrayList columnList = cte.getColumnList();
         if (columnList != null) {
            for(int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
               if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                  newColumnList.add(columnList.get(colListInd));
               }
            }
         }

         Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
         int k;
         if (cteSQSSelectColList.size() == newColumnList.size()) {
            for(k = 0; k < cteSQSSelectColList.size(); ++k) {
               if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                  SelectColumn cteSelCol = (SelectColumn)cteSQSSelectColList.get(k);
                  cteSelCol.setIsAS("AS");
                  cteSelCol.setAliasName(newColumnList.get(k).toString());
               }
            }
         }

         for(k = 0; k < withSelectStmtFromItems.size(); ++k) {
            FromTable fromTable = (FromTable)withSelectStmtFromItems.get(k);
            if (fromTable.getTableName() instanceof String) {
               new Vector();
               String fromTableName = fromTable.getTableName().toString();
               SelectQueryStatement ss = cte.getSelectQueryStatement().toMySQL();
               String hashKey = cte.getViewName().toString();
               String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
               addSelectStmt.put(hashKey.trim(), ss);
               aliasNamesList.add(cte.getViewName().getTableName());
               if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }

               if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                  Vector ssFromItems = ss.getFromClause().getFromItemList();

                  for(int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                     for(int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                        FromTable ssFromTable = (FromTable)ssFromItems.get(ssIndex);
                        if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                           ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                           ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                           SelectQueryStatement ss1 = (SelectQueryStatement)ssFromTable.getTableName();
                           Vector ssFromTableFromItems = ss1.getFromClause().getFromItemList();

                           for(int x = 0; x < ssFromTableFromItems.size(); ++x) {
                              for(int y = 0; y < aliasNamesList.size(); ++y) {
                                 if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                    FromTable ss1FromTable = (FromTable)ssFromTableFromItems.get(x);
                                    ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                    ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                 }
                              }
                           }
                        }
                     }
                  }

                  fromTable.setTableName(ss);
                  fromTable.setAliasName(fromTableName);
               }
            }
         }
      }

      this.derivedTableQuery = withSelectStmt;
      return this.derivedTableQuery;
   }

   public String toVectorWiseString() throws ConvertException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public SwisSQLStatement toANSI() throws ConvertException {
      return null;
   }

   public WithStatement toReplaceTblCol() throws ConvertException {
      WithStatement withStmtConv = new WithStatement();
      if (this.recursive != null) {
         throw new ConvertException("RECURSIVE CTE IS NOT SUPPORTED", "CTE_RECURSIVE_FOUND");
      } else {
         if (this.with != null) {
            withStmtConv.setWith(this.with);
         }

         if (this.commentObject != null) {
            withStmtConv.setCommentClass(this.commentObject);
         }

         if (this.commonTableExprList != null) {
            Vector comTblExprListConv = new Vector();
            if (this.commonTableExprList.size() > 3) {
               throw new ConvertException("More than three CTEs is not supported", "MORE_THAN_3_CTE_NOT_SUPPORTED");
            }

            int i = 0;

            for(int comTblExprListSize = this.commonTableExprList.size(); i < comTblExprListSize; ++i) {
               CommonTableExpression commonTableExpression = (CommonTableExpression)this.commonTableExprList.get(i);
               SelectQueryStatement selectQueryStatement = commonTableExpression.getSelectQueryStatement();
               selectQueryStatement.setPropAndHandlerFromWS(this);
               List cteColumnListKeySet = new ArrayList(commonTableExpression.getWithColumnListNames().keySet());
               selectQueryStatement.setCTEColumnList((ArrayList)cteColumnListKeySet);
               selectQueryStatement.setCTEViewDetsMap(this.withSQS.getCTEViewDetsMap());
               selectQueryStatement.setIsQtNewFlow(true);
               comTblExprListConv.addElement(((CommonTableExpression)this.commonTableExprList.get(i)).toReplaceTblCol(((CommonTableExpression)this.commonTableExprList.get(i)).getSelectQueryStatement()));
            }

            withStmtConv.setCommonTableExpressionList(comTblExprListConv);
         }

         if (this.withSQS != null) {
            this.withSQS.setPropAndHandlerFromWS(this);
            withStmtConv.setWithSQS(this.withSQS.toReplaceTblCol());
         }

         return withStmtConv;
      }
   }

   public SwisSQLStatement toMsAccessJdbc() throws ConvertException {
      return null;
   }
}
