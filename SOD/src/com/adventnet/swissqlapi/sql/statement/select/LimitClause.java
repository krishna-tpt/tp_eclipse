package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.parser.Token;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class LimitClause {
   public String limitClause;
   public String limitValue;
   public String offsetClause;
   public String offsetStart;
   public String RowOnlyClause;
   private CommentClass commentObj;
   private CommentClass commentObjAfterToken;

   public void setLimitClause(String s_lc) {
      this.limitClause = s_lc;
   }

   public void setLimitValue(String s_lv) {
      this.limitValue = s_lv;
   }

   public void setOffSetClause(String s_osc) {
      this.offsetClause = s_osc;
   }

   public void setOffSetStart(String s_os) {
      this.offsetStart = s_os;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public void setCommentClassAfterToken(CommentClass commentObj) {
      this.commentObjAfterToken = commentObj;
   }

   public void setRowOnlyClause(String s_roc) {
      this.RowOnlyClause = s_roc;
   }

   public String getLimitValue() {
      return this.limitValue;
   }

   public String getOffSetClause() {
      return this.offsetClause;
   }

   public String getOffSetStart() {
      return this.offsetStart;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public CommentClass getCommentClassAfterToken() {
      return this.commentObjAfterToken;
   }

   public String getRowOnlyClause() {
      return this.RowOnlyClause;
   }

   public void addCommentClassAfterToken(Token commentObj) {
      if (commentObj != null) {
         Token test = commentObj.specialToken;
         ArrayList specialTokenList;
         if (this.commentObjAfterToken != null) {
            specialTokenList = this.commentObjAfterToken.getSpecialToken();

            for(int lastIndex = specialTokenList.size(); test != null; test = test.specialToken) {
               specialTokenList.add(lastIndex, test.image);
            }
         } else if (test != null) {
            for(specialTokenList = new ArrayList(); test != null; test = test.specialToken) {
               specialTokenList.add(0, test.image);
            }

            CommentClass commentObjToBeInserted = new CommentClass();
            commentObjToBeInserted.setSpecialToken(specialTokenList);
            this.commentObjAfterToken = commentObjToBeInserted;
         }

         commentObj.specialToken = null;
      }

   }

   public LimitClause toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (this.limitValue != null && !this.limitValue.equalsIgnoreCase("ALL")) {
         if (this.offsetStart != null) {
            lc.setLimitClause(this.limitClause);
            lc.setLimitValue(this.offsetStart);
            lc.setOffSetStart(this.limitValue);
         } else {
            lc.setLimitValue(this.limitValue);
            lc.setOffSetStart(this.offsetStart);
            lc.setLimitClause(this.limitClause);
         }
      }

      if (this.limitValue == null && this.offsetStart != null) {
         throw new ConvertException(" OFFSET cannot be used without a LIMIT clause.", "OFFSET_USED_WITHOUT_LIMIT");
      } else {
         return lc;
      }
   }

   public LimitClause toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (this.limitValue != null) {
         if (this.offsetStart != null) {
            lc.setLimitClause(this.limitClause);
            lc.setLimitValue(this.limitValue);
            lc.setOffSetClause("OFFSET");
            lc.setOffSetStart(this.offsetStart);
         } else {
            lc.setLimitValue(this.limitValue);
            lc.setOffSetStart(this.offsetStart);
            lc.setLimitClause(this.limitClause);
         }
      }

      return lc;
   }

   public LimitClause toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (this.limitValue != null) {
         if (this.offsetStart != null) {
            lc.setLimitClause(this.limitClause);
            lc.setLimitValue(this.limitValue);
            lc.setOffSetClause("OFFSET");
            lc.setOffSetStart(this.offsetStart);
         } else {
            lc.setLimitValue(this.limitValue);
            lc.setOffSetStart(this.offsetStart);
            lc.setLimitClause(this.limitClause);
         }
      }

      return lc;
   }

   public LimitClause toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector subSelItems;
      if (from_sqs != null && from_sqs.isMSAzure() && !from_sqs.isMSAzureWareHouse()) {
         FetchClause fc = new FetchClause();
         if (this.limitValue != null) {
            if ((this.limitValue.equalsIgnoreCase("1") || this.limitValue.equalsIgnoreCase("0")) && this.offsetStart == null) {
               to_sqs.getSelectStatement().setSelectRowSpecifier("TOP");
               to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.limitValue));
            } else if (!this.limitValue.equalsIgnoreCase("ALL")) {
               if (to_sqs.getOrderByStatement() == null) {
                  OrderByStatement obs = new OrderByStatement();
                  OrderItem oi = new OrderItem();
                  SelectColumn sc = new SelectColumn();
                  oi.setOrder("ASC");
                  obs.setOrderClause("ORDER BY ");
                  Vector vc = ((SelectColumn)to_sqs.getSelectStatement().getSelectItemList().elementAt(0)).getColumnExpression();
                  sc.setColumnExpression(vc);
                  oi.setOrderSpecifier(sc);
                  subSelItems = new Vector();
                  subSelItems.add(oi);
                  obs.setOrderItemList(subSelItems);
                  to_sqs.setOrderByStatement(obs);
               }

               String offSet = " 0 ROWS";
               if (this.offsetStart != null) {
                  offSet = this.offsetStart + " ROWS ";
               }

               fc.setFetchFirstClause("FETCH NEXT");
               fc.setRowOnlyClause("ROWS ONLY");
               if (this.limitValue.equalsIgnoreCase("0")) {
                  this.limitValue = "1";
               }

               fc.setFetchCount(this.limitValue);
               fc.setFetchOffSetCount(offSet);
               to_sqs.setFetchClause(fc);
            }
         }
      } else if (this.limitClause != null && !this.limitValue.equalsIgnoreCase("ALL")) {
         to_sqs.getSelectStatement().setSelectRowSpecifier("TOP");
         to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.limitValue));
         if (this.offsetStart != null) {
            int offset = 0;

            try {
               offset = Integer.parseInt(this.offsetStart);
            } catch (Exception var26) {
            }

            if (offset > 0) {
               SelectQueryStatement sqs = new SelectQueryStatement();
               sqs.setDatatypeMapping(to_sqs.getDatatypeMapping());
               sqs.setFromClause(to_sqs.getFromClause());
               sqs.setGroupByStatement(to_sqs.getGroupByStatement());
               sqs.setHavingStatement(to_sqs.getHavingStatement());
               sqs.setIntoStatement(to_sqs.getIntoStatement());
               sqs.setOrderByStatement(to_sqs.getOrderByStatement());
               sqs.setSetOperatorClause(to_sqs.getSetOperatorClause());
               sqs.setForUpdateStatement(to_sqs.getForUpdateStatement());
               if (to_sqs.getWhereExpression() != null) {
                  sqs.setWhereExpression(this.getClonedWhereExpression(to_sqs.getWhereExpression()));
               }

               SelectStatement toSS = to_sqs.getSelectStatement();
               Vector toSelItems = toSS.getSelectItemList();
               SelectStatement ss = new SelectStatement();
               ss.setSelectClause("SELECT");
               ss.setSelectRowSpecifier("TOP");
               ss.setSelectRowCount(Integer.parseInt(this.offsetStart));
               sqs.setSelectStatement(ss);
               subSelItems = new Vector();
               ss.setSelectItemList(subSelItems);
               SelectColumn subSC = new SelectColumn();
               WhereItem wi = new WhereItem();
               WhereColumn lwc = new WhereColumn();
               Object obj = toSelItems.get(0);
               boolean isStar = false;
               Vector lwcColExpr;
               if (toSelItems.size() == 1 && obj instanceof SelectColumn) {
                  lwcColExpr = ((SelectColumn)obj).getColumnExpression();
                  Object exprObj;
                  if (lwcColExpr.size() == 1) {
                     exprObj = lwcColExpr.get(0);
                     if (exprObj instanceof String && exprObj.toString().equals("*")) {
                        isStar = true;
                     }
                  } else if (lwcColExpr.size() == 2) {
                     exprObj = lwcColExpr.get(1);
                     if (exprObj instanceof String && exprObj.toString().equals(".*")) {
                        isStar = true;
                     }
                  }
               }

               int k;
               Object ob;
               Vector wce;
               if (isStar) {
                  FromClause fc = sqs.getFromClause();
                  if (fc != null) {
                     wce = fc.getFromItemList();

                     for(k = 0; k < wce.size(); ++k) {
                        ob = wce.get(k);
                        if (ob instanceof FromTable) {
                           Object tblObj = ((FromTable)ob).getTableName();
                           String alias = ((FromTable)ob).getAliasName();
                           if (tblObj instanceof String) {
                              String tableName = tblObj.toString();
                              if (alias == null) {
                                 alias = tableName;
                              }

                              int index = tableName.indexOf(".");
                              if (index != -1) {
                                 tableName = tableName.substring(index + 1, tableName.length());
                              }

                              Hashtable colDatatypeTable = (Hashtable)((Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getDataTypesFromMetaDataHT(), tableName));
                              if (colDatatypeTable == null) {
                                 to_sqs.setGeneralComments("/* SwisSQL Message : Metadata of the source database required for accurate conversion */");
                              } else {
                                 Set keys = colDatatypeTable.keySet();
                                 Iterator it = keys.iterator();
                                 Vector lwcColExpr = new Vector();
                                 lwcColExpr.add(alias + "." + it.next().toString());
                                 subSC.setColumnExpression(lwcColExpr);
                                 subSelItems.add(subSC);
                                 lwc.setColumnExpression(lwcColExpr);
                                 wi.setLeftWhereExp(lwc);
                              }
                              break;
                           }
                        }
                     }
                  }
               } else if (obj instanceof SelectColumn) {
                  lwcColExpr = ((SelectColumn)obj).getColumnExpression();
                  subSC.setColumnExpression(lwcColExpr);
                  subSC.setAliasName(((SelectColumn)obj).getAliasName());
                  subSelItems.add(subSC);
                  lwc.setColumnExpression(lwcColExpr);
                  wi.setLeftWhereExp(lwc);
               }

               boolean hasAggregateFunctions = false;
               if (lwc != null && lwc.getColumnExpression() != null) {
                  wce = lwc.getColumnExpression();

                  for(k = 0; k < wce.size(); ++k) {
                     ob = wce.get(k);
                     if (ob instanceof FunctionCalls) {
                        String fnName = ((FunctionCalls)ob).getFunctionNameAsAString();
                        if (fnName != null && (fnName.equalsIgnoreCase("SUM") || fnName.equalsIgnoreCase("AVG") || fnName.equalsIgnoreCase("MIN") || fnName.equalsIgnoreCase("MAX") || fnName.equalsIgnoreCase("STDDEV") || fnName.equalsIgnoreCase("COUNT") || fnName.equalsIgnoreCase("VARIANCE") || fnName.equalsIgnoreCase("STD") || fnName.equalsIgnoreCase("STDDEV_POP") || fnName.equalsIgnoreCase("STDDEV_SAMP") || fnName.equalsIgnoreCase("VAR_POP") || fnName.equalsIgnoreCase("VAR_SAMP"))) {
                           hasAggregateFunctions = true;
                           break;
                        }
                     }
                  }
               }

               wi.setOperator("NOT IN");
               wi.setRightWhereSubQuery(sqs);
               WhereExpression toWE;
               Vector havingItems;
               Vector hItems;
               if (hasAggregateFunctions) {
                  if (to_sqs.getHavingStatement() != null) {
                     HavingStatement havingSt = to_sqs.getHavingStatement();
                     havingItems = havingSt.getHavingItems();
                     if (havingItems.size() == 1) {
                        WhereExpression havExp = (WhereExpression)havingItems.get(0);
                        if (havExp != null) {
                           Vector operators = havExp.getOperator();
                           if (operators != null && operators.size() > 0) {
                              operators.add("AND");
                           } else {
                              operators = new Vector();
                              operators.add("AND");
                              havExp.setOperator(operators);
                           }

                           havExp.getWhereItems().add(wi);
                        }
                     }
                  } else {
                     toWE = new WhereExpression();
                     havingItems = new Vector();
                     havingItems.add(wi);
                     toWE.setWhereItem(havingItems);
                     hItems = new Vector();
                     hItems.add(toWE);
                     HavingStatement having = new HavingStatement();
                     having.setHavingClause("HAVING");
                     having.setHavingItems(hItems);
                     to_sqs.setHavingStatement(having);
                  }
               } else {
                  toWE = to_sqs.getWhereExpression();
                  if (toWE != null) {
                     havingItems = toWE.getOperator();
                     if (havingItems != null && havingItems.size() > 0) {
                        havingItems.add("AND");
                     } else {
                        havingItems = new Vector();
                        havingItems.add("AND");
                        toWE.setOperator(havingItems);
                     }

                     toWE.getWhereItems().add(wi);
                  } else {
                     WhereExpression newWE = new WhereExpression();
                     hItems = new Vector();
                     hItems.add(wi);
                     newWE.setWhereItem(hItems);
                     to_sqs.setWhereExpression(newWE);
                  }
               }
            }
         }
      }

      return null;
   }

   public LimitClause toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.limitClause != null && !this.limitValue.equalsIgnoreCase("ALL")) {
         to_sqs.getSelectStatement().setSelectRowSpecifier("TOP");
         to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.limitValue));
      }

      return null;
   }

   public LimitClause toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (this.limitValue != null) {
         if (this.offsetStart != null) {
            lc.setLimitClause(this.limitClause);
            lc.setLimitValue(this.limitValue);
            lc.setOffSetClause("OFFSET");
            lc.setOffSetStart(this.offsetStart);
         } else {
            lc.setLimitValue(this.limitValue);
            lc.setOffSetStart(this.offsetStart);
            lc.setLimitClause(this.limitClause);
         }
      }

      return lc;
   }

   public LimitClause toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.limitClause != null && !this.limitValue.equalsIgnoreCase("ALL")) {
         if (this.offsetClause != null && Integer.parseInt(this.offsetStart) != 0) {
            to_sqs.getSelectStatement().setInformixRowSpecifier("SKIP");
            to_sqs.getSelectStatement().setIfxSelectRowCount(Integer.parseInt(this.offsetStart));
            to_sqs.getSelectStatement().setSelectRowSpecifier("FIRST");
            to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.limitValue) != 0 ? Integer.parseInt(this.limitValue) : 1);
         } else {
            to_sqs.getSelectStatement().setInformixRowSpecifier("FIRST");
            int limit = Integer.parseInt(this.limitValue);
            to_sqs.getSelectStatement().setSelectRowCount(limit != 0 ? limit : 1);
         }

         if (Integer.parseInt(this.limitValue) == 0) {
            WhereItem wi = new WhereItem();
            Vector v_temp = new Vector();
            WhereColumn wc_temp = new WhereColumn();
            v_temp.addElement("1");
            wc_temp.setColumnExpression(v_temp);
            wi.setLeftWhereExp(wc_temp);
            wi.setOperator("=");
            v_temp = new Vector();
            wc_temp = new WhereColumn();
            v_temp.addElement("0");
            wc_temp.setColumnExpression(v_temp);
            wi.setRightWhereExp(wc_temp);
            WhereExpression t_we = to_sqs.getWhereExpression();
            if (t_we != null) {
               t_we.addOperator("AND");
               t_we.addWhereItem(wi);
            } else {
               t_we = new WhereExpression();
               t_we.addWhereItem(wi);
               to_sqs.setWhereExpression(t_we);
            }
         }
      }

      return null;
   }

   public LimitClause toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FetchClause fc = new FetchClause();
      if (to_sqs.getFetchClause() != null) {
         throw new ConvertException();
      } else {
         if (this.limitClause != null && !this.limitValue.equalsIgnoreCase("ALL")) {
            fc.setFetchFirstClause("FETCH FIRST");
            fc.setFetchCount(this.limitValue);
            fc.setRowOnlyClause("ROWS ONLY");
            to_sqs.setFetchClause(fc);
         }

         return null;
      }
   }

   public LimitClause toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (this.limitValue != null) {
         if (this.offsetStart != null && !this.offsetStart.equals("0")) {
            SelectQueryStatement innerSelectQueryStatement = new SelectQueryStatement();
            SelectStatement selectStatement = new SelectStatement();
            selectStatement.setSelectClause("SELECT");
            Vector selectItemList = new Vector(to_sqs.getSelectStatement().getSelectItemList());
            SelectColumn selCol = new SelectColumn();
            Vector selColExp = new Vector();
            FunctionCalls func = new FunctionCalls();
            TableColumn tableColumn = new TableColumn();
            tableColumn.setColumnName("ROW_NUMBER");
            func.setFunctionName(tableColumn);
            func.setOver("OVER");
            func.setOrderBy(to_sqs.getOrderByStatement());
            selColExp.add(func);
            selCol.setColumnExpression(selColExp);
            selCol.setIsAS("AS");
            selCol.setAliasName("RN");
            ((SelectColumn)selectItemList.get(selectItemList.size() - 1)).setEndsWith(",");
            selectItemList.add(selCol);
            selectStatement.setSelectItemList(selectItemList);
            innerSelectQueryStatement.setSelectStatement(selectStatement);
            innerSelectQueryStatement.setFromClause(to_sqs.getFromClause());
            innerSelectQueryStatement.setWhereExpression(to_sqs.getWhereExpression());
            innerSelectQueryStatement.setGroupByStatement(to_sqs.getGroupByStatement());
            innerSelectQueryStatement.setHavingStatement(to_sqs.getHavingStatement());
            innerSelectQueryStatement.setSetOperatorClause(to_sqs.getSetOperatorClause());
            innerSelectQueryStatement.setIntoStatement(to_sqs.getIntoStatement());
            SelectStatement outerSelectStatement = new SelectStatement();
            outerSelectStatement.setSelectClause("SELECT");
            Vector outerSelectItemList = new Vector();
            TableColumn outerTc = new TableColumn();
            outerTc.setColumnName("*");
            outerSelectItemList.add(outerTc);
            outerSelectStatement.setSelectItemList(outerSelectItemList);
            to_sqs.setSelectStatement(outerSelectStatement);
            FromClause outerFromClause = new FromClause();
            FromTable outerFt = new FromTable();
            Vector outerFromItemList = new Vector();
            outerFt.setTableName(innerSelectQueryStatement);
            outerFromItemList.add(outerFt);
            outerFromClause.setFromClause("FROM");
            outerFromClause.setFromItemList(outerFromItemList);
            to_sqs.setFromClause(outerFromClause);
            WhereExpression outerWhereExpression = new WhereExpression();
            WhereItem whereItem = new WhereItem();
            WhereColumn leftItem = new WhereColumn();
            WhereColumn rightItem = new WhereColumn();
            Vector leftWhereExp = new Vector();
            Vector rightWhereExp = new Vector();
            TableColumn leftTableColumn = new TableColumn();
            leftTableColumn.setColumnName("RN");
            leftWhereExp.add(leftTableColumn);
            leftItem.setColumnExpression(leftWhereExp);
            rightWhereExp.add(Integer.parseInt(this.offsetStart) + 1);
            rightWhereExp.add("AND");
            rightWhereExp.add(Integer.parseInt(this.offsetStart) + Integer.parseInt(this.limitValue));
            rightItem.setColumnExpression(rightWhereExp);
            whereItem.setLeftWhereExp(leftItem);
            whereItem.setRightWhereExp(rightItem);
            whereItem.setOperator("BETWEEN");
            outerWhereExpression.addWhereItem(whereItem);
            to_sqs.setWhereExpression(outerWhereExpression);
            to_sqs.setHavingStatement((HavingStatement)null);
            to_sqs.setGroupByStatement((GroupByStatement)null);
            to_sqs.setOrderByStatement((OrderByStatement)null);
            to_sqs.setSetOperatorClause((SetOperatorClause)null);
            to_sqs.setIntoStatement((IntoStatement)null);
         } else {
            lc.setLimitValue(this.limitValue);
            lc.setLimitClause(this.limitClause);
         }
      }

      return lc;
   }

   public LimitClause toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (this.limitValue != null) {
         if (this.offsetStart != null) {
            lc.setLimitClause(this.limitClause);
            lc.setLimitValue(this.limitValue);
            lc.setOffSetClause("OFFSET");
            lc.setOffSetStart(this.offsetStart);
         } else {
            lc.setLimitValue(this.limitValue);
            lc.setOffSetStart(this.offsetStart);
            lc.setLimitClause(this.limitClause);
         }
      }

      return lc;
   }

   public LimitClause toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (this.limitValue != null) {
         if (this.offsetStart != null) {
            lc.setLimitClause(this.limitClause);
            lc.setLimitValue(this.limitValue);
            lc.setOffSetClause("OFFSET");
            lc.setOffSetStart(this.offsetStart);
         } else {
            lc.setLimitValue(this.limitValue);
            lc.setOffSetStart(this.offsetStart);
            lc.setLimitClause(this.limitClause);
         }
      }

      return lc;
   }

   public LimitClause toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int offset;
      if (this.limitClause != null && !this.limitValue.equalsIgnoreCase("ALL")) {
         to_sqs.getSelectStatement().setSelectRowSpecifier("TOP");
         if (this.offsetClause == null) {
            offset = Integer.parseInt(this.limitValue);
            to_sqs.getSelectStatement().setSelectRowCount(offset == 0 ? offset + 1 : offset);
         }
      }

      Vector innerSelectSubQueryCols;
      Vector innerOrderByCols;
      Vector outerOrdrItemList;
      if (to_sqs.getOrderByStatement() == null && this.offsetClause != null && Integer.parseInt(this.offsetStart) > 0) {
         OrderByStatement obs = new OrderByStatement();
         OrderItem oi = new OrderItem();
         SelectColumn sc = new SelectColumn();
         oi.setOrder("ASC");
         obs.setOrderClause("ORDER BY ");
         innerSelectSubQueryCols = new Vector();
         TableColumn tc = new TableColumn();
         tc.setColumnName("1");
         tc.setTableName((String)null);
         SelectColumn sc1 = new SelectColumn();
         innerOrderByCols = new Vector();
         innerOrderByCols.add(tc);
         sc1.setColumnExpression(innerOrderByCols);
         innerSelectSubQueryCols.add(sc1);
         ((SelectColumn)innerSelectSubQueryCols.lastElement()).setEndsWith((String)null);
         sc.setColumnExpression(innerSelectSubQueryCols);
         oi.setOrderSpecifier(sc);
         outerOrdrItemList = new Vector();
         outerOrdrItemList.add(oi);
         obs.setOrderItemList(outerOrdrItemList);
         to_sqs.setOrderByStatement(obs);
      }

      if (this.limitClause != null && !this.limitValue.equalsIgnoreCase("ALL") && this.offsetClause != null) {
         to_sqs.getSelectStatement().setSelectRowSpecifier("TOP");
         to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.limitValue));
         if (this.offsetStart != null) {
            offset = 0;

            try {
               offset = Integer.parseInt(this.offsetStart);
            } catch (Exception var25) {
            }

            if (offset > 0) {
               to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.limitValue) + offset);
               SelectQueryStatement innerSelectSubQuery = new SelectQueryStatement();
               SelectQueryStatement innerSelectQuery = new SelectQueryStatement();
               innerSelectSubQuery.setSelectStatement(to_sqs.getSelectStatement());
               innerSelectSubQuery.setFromClause(to_sqs.getFromClause());
               innerSelectSubQuery.setWhereExpression(to_sqs.getWhereExpression());
               innerSelectSubQuery.setOrderByStatement(to_sqs.getOrderByStatement());
               innerSelectSubQuery.setGroupByStatement(to_sqs.getGroupByStatement());
               innerSelectSubQuery.setHavingStatement(to_sqs.getHavingStatement());
               innerSelectSubQuery.setSetOperatorClause(to_sqs.getSetOperatorClause());
               innerSelectSubQuery.setIntoStatement(to_sqs.getIntoStatement());
               innerSelectSubQueryCols = innerSelectSubQuery.getSelectStatement().getSelectItemList();
               int colNum = 1;
               Vector outerSelectItemList = new Vector();
               Iterator var31 = innerSelectSubQueryCols.iterator();

               while(var31.hasNext()) {
                  Object Col = var31.next();
                  if (Col instanceof SelectColumn) {
                     String colAliasName = ((SelectColumn)Col).getAliasName();
                     if (colAliasName == null) {
                        colAliasName = "COLALIAS_" + colNum;
                        ((SelectColumn)Col).setIsAS("AS");
                        ((SelectColumn)Col).setAliasName(colAliasName);
                        ++colNum;
                     }

                     SelectColumn outerSc = new SelectColumn();
                     TableColumn outerTc = new TableColumn();
                     outerTc.setColumnName(colAliasName);
                     outerTc.setTableName("TABLEALIAS_2");
                     Vector outerSelColExp = new Vector();
                     outerSelColExp.add(outerTc);
                     outerSc.setColumnExpression(outerSelColExp);
                     outerSc.setEndsWith(",");
                     outerSelectItemList.add(outerSc);
                  }
               }

               ((SelectColumn)outerSelectItemList.lastElement()).setEndsWith((String)null);
               innerOrderByCols = innerSelectSubQuery.getOrderByStatement().getOrderItemList();
               outerOrdrItemList = new Vector();
               Vector innerOrdrItemList = new Vector();
               Iterator var34 = innerOrderByCols.iterator();

               while(true) {
                  Vector innerSelColExp;
                  Object Col;
                  do {
                     if (!var34.hasNext()) {
                        SelectStatement innerSelectStatement = new SelectStatement();
                        Vector innerSelectItemList = new Vector();
                        SelectColumn innerSc1 = new SelectColumn();
                        Vector innerSelColExp1 = new Vector();
                        innerSelColExp1.add("TABLEALIAS_1.*");
                        innerSc1.setColumnExpression(innerSelColExp1);
                        innerSelectItemList.add(innerSc1);
                        innerSelectStatement.setSelectClause("SELECT");
                        innerSelectStatement.setSelectRowSpecifier("TOP");
                        innerSelectStatement.setSelectRowCount(Integer.parseInt(this.limitValue));
                        innerSelectStatement.setSelectItemList(innerSelectItemList);
                        innerSelectQuery.setSelectStatement(innerSelectStatement);
                        FromClause innerFc = new FromClause();
                        Vector innerFromItemList = new Vector();
                        FromTable innerFt = new FromTable();
                        innerFt.setTableName(innerSelectSubQuery);
                        innerFt.setAliasName("TABLEALIAS_1");
                        innerFromItemList.add(innerFt);
                        innerFc.setFromClause("FROM");
                        innerFc.setFromItemList(innerFromItemList);
                        innerSelectQuery.setFromClause(innerFc);
                        OrderByStatement innerOrderBy = new OrderByStatement();
                        innerOrderBy.setOrderClause("ORDER BY");
                        innerOrderBy.setOrderItemList(innerOrdrItemList);
                        innerSelectQuery.setOrderByStatement(innerOrderBy);
                        SelectStatement outerSelectStatement = new SelectStatement();
                        outerSelectStatement.setSelectClause("SELECT");
                        outerSelectStatement.setSelectRowSpecifier("TOP");
                        outerSelectStatement.setSelectRowCount(Integer.parseInt(this.limitValue));
                        outerSelectStatement.setSelectItemList(outerSelectItemList);
                        to_sqs.setSelectStatement(outerSelectStatement);
                        FromClause outerFromClause = new FromClause();
                        FromTable outerFt = new FromTable();
                        innerSelColExp = new Vector();
                        outerFt.setTableName(innerSelectQuery);
                        outerFt.setAliasName("TABLEALIAS_2");
                        innerSelColExp.add(outerFt);
                        outerFromClause.setFromClause("FROM");
                        outerFromClause.setFromItemList(innerSelColExp);
                        to_sqs.setFromClause(outerFromClause);
                        OrderByStatement outerOrderBy = new OrderByStatement();
                        outerOrderBy.setOrderClause("ORDER BY");
                        outerOrderBy.setOrderItemList(outerOrdrItemList);
                        to_sqs.setOrderByStatement(outerOrderBy);
                        to_sqs.setWhereExpression((WhereExpression)null);
                        to_sqs.setHavingStatement((HavingStatement)null);
                        to_sqs.setGroupByStatement((GroupByStatement)null);
                        to_sqs.setSetOperatorClause((SetOperatorClause)null);
                        to_sqs.setIntoStatement((IntoStatement)null);
                        return null;
                     }

                     Col = var34.next();
                  } while(!(Col instanceof OrderItem));

                  String order = ((OrderItem)Col).getOrder();
                  SelectColumn orderSpecifier = ((OrderItem)Col).getOrderSpecifier();
                  String colAliasName = orderSpecifier.getAliasName();
                  if (colAliasName == null) {
                     label114: {
                        Iterator var17 = innerSelectSubQueryCols.iterator();

                        Object selCol;
                        String orderExp;
                        do {
                           do {
                              if (!var17.hasNext()) {
                                 break label114;
                              }

                              selCol = var17.next();
                              orderExp = orderSpecifier.getColumnExpression().toString().substring(1, orderSpecifier.getColumnExpression().toString().length() - 1);
                           } while(!(selCol instanceof SelectColumn));
                        } while(!((SelectColumn)selCol).getColumnExpression().toString().contains(orderExp) && !orderExp.equalsIgnoreCase("1"));

                        colAliasName = ((SelectColumn)selCol).getAliasName();
                     }
                  }

                  SelectColumn outerSc = new SelectColumn();
                  TableColumn outerTc = new TableColumn();
                  outerTc.setColumnName(colAliasName);
                  outerTc.setTableName("TABLEALIAS_2");
                  Vector outerSelColExp = new Vector();
                  outerSelColExp.add(outerTc);
                  outerSc.setColumnExpression(outerSelColExp);
                  OrderItem orderItem = new OrderItem();
                  orderItem.setOrderSpecifier(outerSc);
                  orderItem.setOrder(order);
                  outerOrdrItemList.add(orderItem);
                  SelectColumn innerSc = new SelectColumn();
                  TableColumn innerTc = new TableColumn();
                  innerTc.setColumnName(colAliasName);
                  innerTc.setTableName("TABLEALIAS_1");
                  innerSelColExp = new Vector();
                  innerSelColExp.add(innerTc);
                  innerSc.setColumnExpression(innerSelColExp);
                  OrderItem innerOrderItem = new OrderItem();
                  innerOrderItem.setOrderSpecifier(innerSc);
                  innerOrderItem.setOrder(order.equalsIgnoreCase("ASC") ? "DESC" : "ASC");
                  innerOrdrItemList.add(innerOrderItem);
               }
            }
         }
      }

      return null;
   }

   public LimitClause toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (this.limitValue != null && !this.limitValue.equalsIgnoreCase("ALL")) {
         if (this.offsetStart != null) {
            lc.setLimitClause(this.limitClause);
            lc.setLimitValue(this.limitValue);
            lc.setOffSetClause("OFFSET");
            lc.setOffSetStart(this.offsetStart);
         } else {
            lc.setLimitValue(this.limitValue);
            lc.setOffSetStart(this.offsetStart);
            lc.setLimitClause(this.limitClause);
         }
      }

      return lc;
   }

   public LimitClause toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.offsetStart != null && this.offsetStart.equalsIgnoreCase("ALL")) {
         throw new ConvertException("Invalid 'OFFSET' value");
      } else {
         new LimitClause();
         if (from_sqs != null && from_sqs.getCanUseOracleFetch()) {
            FetchClause fetch_clause = new FetchClause();
            if (this.limitValue != null && !this.limitValue.equalsIgnoreCase("ALL")) {
               if (this.offsetStart != null) {
                  fetch_clause.setFetchFirstClause("FETCH NEXT");
                  fetch_clause.setRowOnlyClause("ROWS ONLY");
                  fetch_clause.setFetchCount(this.limitValue);
                  fetch_clause.setFetchOffSetCount(this.offsetStart + " ROWS");
                  to_sqs.setFetchClause(fetch_clause);
               } else {
                  fetch_clause.setFetchFirstClause("FETCH FIRST");
                  fetch_clause.setFetchCount(this.limitValue);
                  fetch_clause.setRowOnlyClause("ROWS ONLY");
                  to_sqs.setFetchClause(fetch_clause);
               }
            }
         } else if (to_sqs != null && this.limitClause != null && !this.limitValue.equalsIgnoreCase("ALL")) {
            int offSetValue = this.offsetStart != null ? Integer.parseInt(this.offsetStart) : 0;
            SelectQueryStatement innerSelectSubQuery = new SelectQueryStatement();
            SelectQueryStatement innerSelectQuery = new SelectQueryStatement();
            innerSelectSubQuery.setSelectStatement(to_sqs.getSelectStatement());
            innerSelectSubQuery.setFromClause(to_sqs.getFromClause());
            innerSelectSubQuery.setWhereExpression(to_sqs.getWhereExpression());
            innerSelectSubQuery.setOrderByStatement(to_sqs.getOrderByStatement());
            innerSelectSubQuery.setGroupByStatement(to_sqs.getGroupByStatement());
            innerSelectSubQuery.setHavingStatement(to_sqs.getHavingStatement());
            innerSelectSubQuery.setSetOperatorClause(to_sqs.getSetOperatorClause());
            innerSelectSubQuery.setIntoStatement(to_sqs.getIntoStatement());
            Vector innerSelectSubQueryCols = innerSelectSubQuery.getSelectStatement().getSelectItemList();
            int colNum = 1;
            Vector outerSelectItemList = new Vector();
            Iterator var10 = innerSelectSubQueryCols.iterator();

            Vector outerSelColExp;
            while(var10.hasNext()) {
               Object Col = var10.next();
               if (Col instanceof SelectColumn) {
                  String colAliasName = ((SelectColumn)Col).getAliasName();
                  if (colAliasName == null) {
                     colAliasName = "COLALIAS_" + colNum;
                     ((SelectColumn)Col).setAliasName(colAliasName);
                     ++colNum;
                  }

                  SelectColumn outerSc = new SelectColumn();
                  TableColumn outerTc = new TableColumn();
                  outerTc.setColumnName(colAliasName);
                  outerTc.setTableName("TABLEALIAS_2");
                  outerSelColExp = new Vector();
                  outerSelColExp.add(outerTc);
                  outerSc.setColumnExpression(outerSelColExp);
                  outerSc.setEndsWith(",");
                  outerSelectItemList.add(outerSc);
               }
            }

            ((SelectColumn)outerSelectItemList.lastElement()).setEndsWith((String)null);
            SelectStatement innerSelectStatement = new SelectStatement();
            Vector innerSelectItemList = new Vector();
            SelectColumn innerSc1 = new SelectColumn();
            Vector innerSelColExp1 = new Vector();
            innerSelColExp1.add("TABLEALIAS_1.*");
            innerSc1.setEndsWith(",");
            innerSc1.setColumnExpression(innerSelColExp1);
            SelectColumn innerSc2 = new SelectColumn();
            outerSelColExp = new Vector();
            TableColumn innerTc = new TableColumn();
            innerTc.setColumnName("ROWNUM");
            outerSelColExp.add(innerTc);
            innerSc2.setAliasName("RN");
            innerSc2.setColumnExpression(outerSelColExp);
            innerSelectItemList.add(innerSc1);
            innerSelectItemList.add(innerSc2);
            innerSelectStatement.setSelectClause("SELECT");
            innerSelectStatement.setSelectItemList(innerSelectItemList);
            innerSelectQuery.setSelectStatement(innerSelectStatement);
            FromClause innerFc = new FromClause();
            Vector innerFromItemList = new Vector();
            FromTable innerFt = new FromTable();
            innerFt.setTableName(innerSelectSubQuery);
            innerFt.setAliasName("TABLEALIAS_1");
            innerFromItemList.add(innerFt);
            innerFc.setFromClause("FROM");
            innerFc.setFromItemList(innerFromItemList);
            innerSelectQuery.setFromClause(innerFc);
            WhereExpression innerWhereExp = new WhereExpression();
            WhereItem innerWhereItem = new WhereItem();
            RownumClause innerRownumClause = new RownumClause();
            innerRownumClause.setRownumClause("ROWNUM");
            innerRownumClause.setOperator("<=");
            innerRownumClause.setRownumValue(offSetValue + Integer.parseInt(this.limitValue));
            innerWhereItem.setRownumClause(innerRownumClause);
            innerWhereExp.addWhereItem(innerWhereItem);
            innerSelectQuery.setWhereExpression(innerWhereExp);
            SelectStatement outerSelectStatement = new SelectStatement();
            outerSelectStatement.setSelectClause("SELECT");
            outerSelectStatement.setSelectItemList(outerSelectItemList);
            to_sqs.setSelectStatement(outerSelectStatement);
            FromClause outerFromClause = new FromClause();
            FromTable outerFt = new FromTable();
            Vector outerFromItemList = new Vector();
            outerFt.setTableName(innerSelectQuery);
            outerFt.setAliasName("TABLEALIAS_2");
            outerFromItemList.add(outerFt);
            outerFromClause.setFromClause("FROM");
            outerFromClause.setFromItemList(outerFromItemList);
            to_sqs.setFromClause(outerFromClause);
            if (this.offsetStart != null && !this.offsetStart.equalsIgnoreCase("0")) {
               WhereExpression outerWhereExpression = new WhereExpression();
               WhereItem whereItem = new WhereItem();
               WhereColumn leftItem = new WhereColumn();
               WhereColumn rightItem = new WhereColumn();
               Vector leftWhereExp = new Vector();
               Vector rightWhereExp = new Vector();
               TableColumn leftTableColumn = new TableColumn();
               leftTableColumn.setColumnName("RN");
               leftWhereExp.add(leftTableColumn);
               leftItem.setColumnExpression(leftWhereExp);
               rightWhereExp.add(offSetValue + 1);
               rightItem.setColumnExpression(rightWhereExp);
               whereItem.setLeftWhereExp(leftItem);
               whereItem.setRightWhereExp(rightItem);
               whereItem.setOperator(">=");
               outerWhereExpression.addWhereItem(whereItem);
               to_sqs.setWhereExpression(outerWhereExpression);
            } else {
               to_sqs.setWhereExpression((WhereExpression)null);
            }

            to_sqs.setHavingStatement((HavingStatement)null);
            to_sqs.setGroupByStatement((GroupByStatement)null);
            to_sqs.setOrderByStatement((OrderByStatement)null);
            to_sqs.setSetOperatorClause((SetOperatorClause)null);
            to_sqs.setIntoStatement((IntoStatement)null);
         }

         LimitClause lc = null;
         return (LimitClause)lc;
      }
   }

   private WhereExpression getClonedWhereExpression(WhereExpression whereExpression) {
      WhereExpression clonedWhereExpression = new WhereExpression();
      Vector whereItemList = new Vector();
      new Vector();
      clonedWhereExpression.setOperator((Vector)whereExpression.getOperator().clone());
      Vector whereItems = whereExpression.getWhereItems();

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

   public LimitClause toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.limitClause != null && !this.limitValue.equalsIgnoreCase("ALL")) {
         to_sqs.getSelectStatement().setSelectRowSpecifier("FIRST");
         to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.limitValue));
      }

      return null;
   }

   public LimitClause toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (this.limitValue != null) {
         if (this.offsetStart != null) {
            lc.setLimitClause(this.limitClause);
            lc.setLimitValue(this.offsetStart);
            lc.setOffSetStart(this.limitValue);
         } else {
            lc.setLimitValue(this.limitValue);
            lc.setOffSetStart(this.offsetStart);
            lc.setLimitClause(this.limitClause);
         }
      }

      return lc;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.commentObj != null) {
         sb.append(this.commentObj.toString() + " ");
      }

      if (this.limitClause != null) {
         sb.append(this.limitClause.toUpperCase());
      }

      if (this.limitValue != null) {
         sb.append(" " + this.limitValue.toUpperCase());
      }

      if (this.offsetClause != null) {
         sb.append(" " + this.offsetClause.toUpperCase());
      }

      if (this.offsetStart != null) {
         if (this.offsetClause == null) {
            sb.append("," + this.offsetStart.toUpperCase());
         } else {
            sb.append(" " + this.offsetStart.toUpperCase());
         }
      }

      if (this.RowOnlyClause != null) {
         sb.append(" " + this.RowOnlyClause.toUpperCase());
      }

      if (this.commentObjAfterToken != null) {
         sb.append(" " + this.commentObjAfterToken.toString());
      }

      return sb.toString();
   }

   public LimitClause toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FetchClause fc = new FetchClause();
      if (this.limitValue != null && !this.limitValue.equalsIgnoreCase("ALL")) {
         if (this.offsetStart != null) {
            fc.setFetchFirstClause("FETCH NEXT");
            fc.setRowOnlyClause("ROWS ONLY");
            fc.setFetchCount(this.limitValue);
            fc.setFetchOffSetCount(this.offsetStart);
            to_sqs.setFetchClause(fc);
         } else {
            fc.setFetchFirstClause("FETCH FIRST");
            fc.setFetchCount(this.limitValue);
            fc.setRowOnlyClause("ROWS ONLY");
            to_sqs.setFetchClause(fc);
         }
      }

      return null;
   }

   public LimitClause toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause limitClConv = new LimitClause();
      if (this.limitClause != null) {
         limitClConv.setLimitClause(this.limitClause);
      }

      if (this.limitValue != null) {
         limitClConv.setLimitValue(this.limitValue);
      }

      if (this.offsetClause != null) {
         limitClConv.setOffSetClause(this.offsetClause);
      }

      if (this.offsetStart != null) {
         limitClConv.setOffSetStart(this.offsetStart);
      }

      if (this.RowOnlyClause != null) {
         limitClConv.setRowOnlyClause(this.RowOnlyClause);
      }

      if (this.commentObj != null) {
         limitClConv.setCommentClass(this.commentObj);
      }

      if (this.commentObjAfterToken != null) {
         limitClConv.setCommentClassAfterToken(this.commentObjAfterToken);
      }

      return limitClConv;
   }
}
