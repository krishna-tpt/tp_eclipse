package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.ArrayList;
import java.util.Vector;

public class OrderByStatement {
   private String OrderClause = new String("");
   private Vector OrderItemList = new Vector();
   private UserObjectContext context = null;
   private CommentClass commentObj;
   private CommentClass commentObjAfterToken;
   private String siblings;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setOrderClause(String s_oc) {
      this.OrderClause = s_oc;
   }

   public void setOrderItemList(Vector v_oil) {
      this.OrderItemList = v_oil;
   }

   public void setSiblings(String sib) {
      this.siblings = sib;
   }

   public String getSiblings() {
      return this.siblings;
   }

   public void addOrderItems(Vector orderItems) {
      if (this.OrderItemList != null) {
         if (orderItems != null) {
            for(int i = 0; i < orderItems.size(); ++i) {
               this.OrderItemList.add(orderItems.get(i));
            }
         }
      } else {
         this.OrderItemList = orderItems;
      }

   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public void setCommentClassAfterToken(CommentClass commentObj) {
      this.commentObjAfterToken = commentObj;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public Vector getOrderItemList() {
      return this.OrderItemList;
   }

   public OrderByStatement toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      new OrderItem();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();
      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = ((OrderItem)this.OrderItemList.elementAt(0)).toANSISelect(to_sqs, from_sqs);
         if (oi != null) {
            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toANSISelect(to_sqs, from_sqs);
            if (oi != null) {
               v_oil.addElement(oi);
            }
         }

         obs.setOrderItemList(v_oil);
      }

      return obs;
   }

   public OrderByStatement toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      new OrderItem();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();

      int count;
      for(count = 0; count < this.OrderItemList.size(); ++count) {
         OrderItem tempOI = (OrderItem)this.OrderItemList.get(count);
         if (tempOI.getNullsOrder() != null) {
            String nullsOrder = tempOI.getNullsOrder().trim().toUpperCase();
            String orderString = tempOI.getOrder();
            if (orderString != null) {
               if (orderString.trim().equalsIgnoreCase("ASC") || orderString.trim().equalsIgnoreCase("DESC")) {
                  if (orderString.trim().equalsIgnoreCase("ASC") && nullsOrder.indexOf("LAST") != -1) {
                     this.addCaseStatementToOrderItemsList(count, tempOI, "1", "0");
                     ++count;
                  } else if (orderString.trim().equalsIgnoreCase("DESC") && nullsOrder.indexOf("FIRST") != -1) {
                     this.addCaseStatementToOrderItemsList(count, tempOI, "0", "1");
                     ++count;
                  }
               }
            } else if (nullsOrder != null && nullsOrder.indexOf("LAST") != -1) {
               this.addCaseStatementToOrderItemsList(count, tempOI, "1", "0");
               ++count;
            }
         }
      }

      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = ((OrderItem)this.OrderItemList.elementAt(0)).toTeradataSelect(to_sqs, from_sqs);
         if (oi != null) {
            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         for(count = 0; count < this.OrderItemList.size(); ++count) {
            Object obj = this.OrderItemList.elementAt(count);
            if (obj instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.OrderItemList.elementAt(count);
               v_oil.addElement(cs.toTeradataSelect(to_sqs, from_sqs));
            } else if (obj instanceof OrderItem) {
               oi = ((OrderItem)this.OrderItemList.elementAt(count)).toTeradataSelect(to_sqs, from_sqs);
               if (oi != null) {
                  v_oil.addElement(oi);
               }
            } else {
               v_oil.addElement(this.OrderItemList.elementAt(count));
            }
         }

         obs.setOrderItemList(v_oil);
      }

      return obs;
   }

   public OrderByStatement toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();

      for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
         v_oil.addElement(((OrderItem)this.OrderItemList.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
      }

      obs.setOrderItemList(v_oil);
      return obs;
   }

   public OrderByStatement toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();

      for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
         v_oil.addElement(((OrderItem)this.OrderItemList.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
      }

      obs.setOrderItemList(v_oil);
      return obs;
   }

   public OrderByStatement toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      obs.setCommentClassAfterToken(this.commentObjAfterToken);
      new OrderItem();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();
      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = ((OrderItem)this.OrderItemList.elementAt(0)).toMySQLSelect(to_sqs, from_sqs);
         if (oi != null) {
            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs);
            if (oi != null) {
               v_oil.addElement(oi);
            }
         }

         obs.setOrderItemList(v_oil);
      }

      return obs;
   }

   public OrderByStatement toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();

      for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
         v_oil.addElement(((OrderItem)this.OrderItemList.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
      }

      obs.setOrderItemList(v_oil);
      return obs;
   }

   public OrderByStatement toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      obs.setCommentClass(this.commentObj);
      new OrderItem();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();
      obs.setObjectContext(this.context);
      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = ((OrderItem)this.OrderItemList.elementAt(0)).toAthenaSelect(to_sqs, from_sqs);
         if (oi != null) {
            if (this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs)) {
               oi = oi.toAthenaSelect(to_sqs, from_sqs);
            }

            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs);
            if (oi != null) {
               if (this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs)) {
                  oi = oi.toAthenaSelect(to_sqs, from_sqs);
               }

               v_oil.addElement(oi);
            }
         }

         obs.setOrderItemList(v_oil);
      }

      return obs;
   }

   public OrderByStatement toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      obs.setCommentClass(this.commentObj);
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();
      obs.setObjectContext(this.context);
      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = ((OrderItem)this.OrderItemList.elementAt(0)).toSapHanaSelect(to_sqs, from_sqs);
         if (oi != null) {
            if (this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs)) {
               oi = oi.toSapHanaSelect(to_sqs, from_sqs);
            }

            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs);
            if (oi != null) {
               if (this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs)) {
                  oi = oi.toSapHanaSelect(to_sqs, from_sqs);
               }

               v_oil.addElement(oi);
            }
         }

         obs.setOrderItemList(v_oil);
      }

      return obs;
   }

   public OrderByStatement toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      obs.setCommentClass(this.commentObj);
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();
      obs.setObjectContext(this.context);
      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = ((OrderItem)this.OrderItemList.elementAt(0)).toSqliteSelect(to_sqs, from_sqs);
         if (oi != null) {
            if (this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs)) {
               oi = oi.toSqliteSelect(to_sqs, from_sqs);
            }

            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs);
            if (oi != null) {
               if (this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs)) {
                  oi = oi.toSqliteSelect(to_sqs, from_sqs);
               }

               v_oil.addElement(oi);
            }
         }

         obs.setOrderItemList(v_oil);
      }

      return obs;
   }

   public OrderByStatement toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      obs.setCommentClass(this.commentObj);
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();
      obs.setObjectContext(this.context);
      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = ((OrderItem)this.OrderItemList.elementAt(0)).toExcelSelect(to_sqs, from_sqs);
         if (oi != null) {
            if (this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs)) {
               oi = oi.toExcelSelect(to_sqs, from_sqs);
            }

            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs);
            if (oi != null) {
               if (this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs)) {
                  oi = oi.toExcelSelect(to_sqs, from_sqs);
               }

               v_oil.addElement(oi);
            }
         }

         obs.setOrderItemList(v_oil);
      }

      return obs;
   }

   public OrderByStatement toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      obs.setCommentClassAfterToken(this.commentObjAfterToken);
      new OrderItem();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();
      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = ((OrderItem)this.OrderItemList.elementAt(0)).toMsAccessJdbcSelect(to_sqs, from_sqs);
         if (oi != null) {
            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs);
            if (oi != null) {
               v_oil.addElement(oi);
            }
         }

         obs.setOrderItemList(v_oil);
      }

      return obs;
   }

   public OrderByStatement toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      new OrderItem();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();
      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = ((OrderItem)this.OrderItemList.elementAt(0)).toDB2Select(to_sqs, from_sqs);
         if (oi != null) {
            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toDB2Select(to_sqs, from_sqs);
            if (this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs)) {
               oi = oi.toDB2Select(to_sqs, from_sqs);
            }

            if (oi != null) {
               v_oil.addElement(oi);
            }
         }

         obs.setOrderItemList(v_oil);
      }

      return obs;
   }

   public OrderByStatement toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement selStmt = from_sqs.getSelectStatement();
      String selectQualifier = selStmt.getSelectQualifier();
      boolean starIsThere = false;
      String orderByListColumnName;
      String selectColumnListName;
      Vector selectColumnList;
      OrderItem oi;
      SelectColumn addSelColumn;
      Object ob;
      if (SwisSQLOptions.RemoveOrderByColumnWhenColumnNotInSelectList) {
         if (selectQualifier != null && selectQualifier.toLowerCase().startsWith("distinct")) {
            selectColumnList = selStmt.getSelectItemList();
            Vector v = new Vector();

            for(int i = 0; i < this.OrderItemList.size(); ++i) {
               oi = (OrderItem)this.OrderItemList.get(i);
               SelectColumn oc = oi.getOrderSpecifier();
               int orderListExprSize = oc.getColumnExpression().size();
               Object o = oc.getColumnExpression().get(0);
               if (o instanceof TableColumn) {
                  TableColumn t1 = (TableColumn)o;
                  orderByListColumnName = t1.getColumnName();

                  for(int j = 0; j < selectColumnList.size(); ++j) {
                     addSelColumn = (SelectColumn)selectColumnList.get(j);
                     ob = addSelColumn.getColumnExpression().get(0);
                     if (ob instanceof TableColumn) {
                        TableColumn t2 = (TableColumn)ob;
                        selectColumnListName = t2.getColumnName();
                        String s1 = selectColumnListName;
                        String s2 = orderByListColumnName;
                        if (selectColumnListName.startsWith("'")) {
                           s1 = selectColumnListName.replaceAll("'", "");
                        }

                        if (selectColumnListName.startsWith("\"")) {
                           s1 = selectColumnListName.replaceAll("\"", "");
                        }

                        if (orderByListColumnName.startsWith("\"")) {
                           s2 = orderByListColumnName.replaceAll("\"", "");
                        }

                        if (orderByListColumnName.startsWith("'")) {
                           s2 = orderByListColumnName.replaceAll("'", "");
                        }

                        if (s1.equalsIgnoreCase(s2)) {
                           v.add(oi);
                           break;
                        }
                     } else if (ob instanceof String) {
                        selectColumnListName = (String)ob;
                        if (selectColumnListName.equals("*")) {
                           starIsThere = true;
                        }
                     }
                  }
               }
            }

            if (!starIsThere) {
               if (v.size() == 0) {
                  return null;
               }

               this.OrderItemList = v;
            }
         }
      } else {
         new Vector();
         if (selectQualifier != null && selectQualifier.toLowerCase().startsWith("distinct")) {
            selectColumnList = selStmt.getSelectItemList();
            new Vector();

            for(int i = 0; i < this.OrderItemList.size(); ++i) {
               boolean presenceFlag = false;
               OrderItem oi = (OrderItem)this.OrderItemList.get(i);
               SelectColumn oc = oi.getOrderSpecifier();
               int orderListExprSize = oc.getColumnExpression().size();
               Object o = oc.getColumnExpression().get(0);
               if (o instanceof TableColumn) {
                  TableColumn t1 = (TableColumn)o;
                  orderByListColumnName = t1.getColumnName();

                  for(int j = 0; j < selectColumnList.size(); ++j) {
                     ob = selectColumnList.get(j);
                     if (ob instanceof SelectColumn) {
                        SelectColumn sc = (SelectColumn)selectColumnList.get(j);
                        Object ob = sc.getColumnExpression().get(0);
                        if (ob instanceof TableColumn) {
                           TableColumn t2 = (TableColumn)ob;
                           selectColumnListName = t2.getColumnName();
                           String s1 = selectColumnListName;
                           String s2 = orderByListColumnName;
                           if (selectColumnListName.startsWith("'")) {
                              s1 = selectColumnListName.replaceAll("'", "");
                           }

                           if (selectColumnListName.startsWith("\"")) {
                              s1 = selectColumnListName.replaceAll("\"", "");
                           }

                           if (orderByListColumnName.startsWith("\"")) {
                              s2 = orderByListColumnName.replaceAll("\"", "");
                           }

                           if (orderByListColumnName.startsWith("'")) {
                              s2 = orderByListColumnName.replaceAll("'", "");
                           }

                           if (s1.equalsIgnoreCase(s2)) {
                              presenceFlag = true;
                              break;
                           }
                        } else if (ob instanceof String) {
                           selectColumnListName = (String)ob;
                           if (selectColumnListName.equals("*")) {
                              starIsThere = true;
                           }
                        }
                     }
                  }

                  if (!presenceFlag && !starIsThere) {
                     ((SelectColumn)selectColumnList.get(selectColumnList.size() - 1)).setEndsWith(",");
                     addSelColumn = new SelectColumn();
                     addSelColumn.setColumnExpression(oc.getColumnExpression());
                     selectColumnList.add(addSelColumn);
                  }
               }
            }

            selStmt.setSelectItemList(selectColumnList);
            to_sqs.setSelectStatement(selStmt.toMSSQLServerSelect(to_sqs, from_sqs));
         }
      }

      OrderByStatement obs = new OrderByStatement();
      new OrderItem();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();
      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = (OrderItem)this.OrderItemList.elementAt(0);
         SwisSQLUtils.checkAndReplaceGroupByItem(oi.getOrderSpecifier(), from_sqs);
         oi = oi.toMSSQLServerSelect(to_sqs, from_sqs);
         if (oi != null) {
            if (this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs)) {
               oi = oi.toMSSQLServerSelect(to_sqs, from_sqs);
            }

            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         ArrayList<String> checkDuplicates = new ArrayList();

         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            OrderItem ordItem = (OrderItem)this.OrderItemList.elementAt(i_count);
            SwisSQLUtils.checkAndReplaceGroupByItem(ordItem.getOrderSpecifier(), from_sqs);
            oi = ordItem.toMSSQLServerSelect(to_sqs, from_sqs);
            if (oi != null) {
               boolean isConverted = this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs);
               if (!checkDuplicates.contains(oi.toString())) {
                  checkDuplicates.add(oi.toString());
                  if (isConverted) {
                     oi = oi.toMSSQLServerSelect(to_sqs, from_sqs);
                  }

                  v_oil.addElement(oi);
               }
            }
         }

         obs.setOrderItemList(v_oil);
      }

      return obs;
   }

   public OrderByStatement toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      obs.setObjectContext(this.context);
      new OrderItem();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();
      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = ((OrderItem)this.OrderItemList.elementAt(0)).toSybaseSelect(to_sqs, from_sqs);
         if (oi != null) {
            oi.setObjectContext(this.context);
            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs);
            if (oi != null) {
               oi.setObjectContext(this.context);
               v_oil.addElement(oi);
            }
         }

         obs.setOrderItemList(v_oil);
      }

      return obs;
   }

   public OrderByStatement toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      obs.setCommentClass(this.commentObj);
      new OrderItem();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();
      obs.setObjectContext(this.context);
      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = ((OrderItem)this.OrderItemList.elementAt(0)).toOracleSelect(to_sqs, from_sqs);
         if (oi != null) {
            if (this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs)) {
               oi = oi.toOracleSelect(to_sqs, from_sqs);
            }

            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs);
            if (oi != null) {
               if (this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs)) {
                  oi = oi.toOracleSelect(to_sqs, from_sqs);
               }

               v_oil.addElement(oi);
            }
         }

         obs.setOrderItemList(v_oil);
      }

      ArrayList numberColList = new ArrayList();
      if (!numberColList.isEmpty() && numberColList.size() == obs.getOrderItemList().size()) {
         for(int i = 0; i < obs.getOrderItemList().size(); ++i) {
            if (obs.getOrderItemList().get(i) instanceof OrderItem) {
               OrderItem orderItem = (OrderItem)obs.getOrderItemList().get(i);
               SelectColumn sc = orderItem.getOrderSpecifier();
               if (sc.getColumnExpression() != null) {
                  Vector columnExpression = sc.getColumnExpression();

                  for(int j = 0; j < columnExpression.size(); ++j) {
                     if (columnExpression.get(j) instanceof TableColumn) {
                        columnExpression.set(j, numberColList.get(i));
                     }
                  }
               }
            }
         }
      }

      return obs;
   }

   private void convertOrdinalNumberToColumn(OrderByStatement obs, SelectQueryStatement from_sqs) {
      for(int i = 0; i < obs.getOrderItemList().size(); ++i) {
         OrderItem oi = (OrderItem)obs.getOrderItemList().get(i);
         if (oi.getOrderSpecifier().getColumnExpression().elementAt(0) instanceof String) {
            String ordinalNumber = (String)oi.getOrderSpecifier().getColumnExpression().elementAt(0);
            if (ordinalNumber.matches("^[1-9][0-9]*")) {
               Vector tc = ((SelectColumn)from_sqs.getSelectStatement().getSelectItemList().elementAt(Integer.parseInt(ordinalNumber) - 1)).getColumnExpression();
               oi.getOrderSpecifier().setColumnExpression(tc);
            }
         }

         obs.getOrderItemList().set(i, oi);
      }

   }

   private boolean convertOrdinalNumberToColumnOrderBy(OrderItem oi, SelectQueryStatement from_sqs) {
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      if (isdenodo) {
         return false;
      } else {
         if (oi.getOrderSpecifier().getColumnExpression().elementAt(0) instanceof String) {
            String ordinalNumber = (String)oi.getOrderSpecifier().getColumnExpression().elementAt(0);
            if (ordinalNumber.matches("^[1-9][0-9]*")) {
               Vector tc = ((SelectColumn)from_sqs.getSelectStatement().getSelectItemList().elementAt(Integer.parseInt(ordinalNumber) - 1)).getColumnExpression();
               oi.getOrderSpecifier().setColumnExpression(tc);
               return true;
            }
         }

         return false;
      }
   }

   public OrderByStatement toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      new OrderItem();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();

      for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
         OrderItem oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs);
         v_oil.addElement(oi);
      }

      obs.setOrderItemList(v_oil);
      return obs;
   }

   public OrderByStatement toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      new OrderItem();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();
      obs.setObjectContext(this.context);
      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = ((OrderItem)this.OrderItemList.elementAt(0)).toTimesTenSelect(to_sqs, from_sqs);
         if (oi != null) {
            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toTimesTenSelect(to_sqs, from_sqs);
            if (oi != null) {
               v_oil.addElement(oi);
            }
         }

         obs.setOrderItemList(v_oil);
      }

      return obs;
   }

   public OrderByStatement toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      new OrderItem();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();
      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = ((OrderItem)this.OrderItemList.elementAt(0)).toNetezzaSelect(to_sqs, from_sqs);
         if (oi != null) {
            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs);
            if (oi != null) {
               v_oil.addElement(oi);
            }
         }

         obs.setOrderItemList(v_oil);
      }

      return obs;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.commentObj != null) {
         sb.append(this.commentObj.toString().trim() + " ");
      }

      if (this.OrderClause != null) {
         sb.append(this.OrderClause.toUpperCase());
      }

      if (this.OrderItemList != null) {
         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);

         try {
            for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
               if (this.OrderItemList.elementAt(i_count) instanceof OrderItem) {
                  ((OrderItem)this.OrderItemList.elementAt(i_count)).setObjectContext(this.context);
               }

               if (i_count == this.OrderItemList.size() - 1) {
                  sb.append(" " + ((OrderItem)this.OrderItemList.elementAt(i_count)).toString());
               } else {
                  sb.append(" " + ((OrderItem)this.OrderItemList.elementAt(i_count)).toString() + ",");
                  sb.append("\n");

                  for(int i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
                     sb.append("\t");
                  }
               }
            }
         } catch (ArrayIndexOutOfBoundsException var4) {
         } catch (Exception var5) {
         }

         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
      }

      if (this.commentObjAfterToken != null) {
         sb.append(" " + this.commentObjAfterToken.toString().trim());
      }

      return sb.toString();
   }

   public Vector getSelectColumnList(SelectQueryStatement toSelectQueryStatement) {
      Vector columnNamesFromTableColumn = new Vector();
      Vector getSelectColumn = toSelectQueryStatement.getSelectStatement().getSelectItemList();
      if (getSelectColumn != null) {
         for(int i = 0; i < getSelectColumn.size(); ++i) {
            SelectColumn getSelectColumnForOrderBy = (SelectColumn)getSelectColumn.get(i);
            Vector getSelectItems = getSelectColumnForOrderBy.getColumnExpression();
            if (getSelectItems != null) {
               for(int j = 0; j > getSelectItems.size(); ++j) {
                  if (getSelectItems.get(j) instanceof TableColumn) {
                     TableColumn getTableColumn = (TableColumn)getSelectItems.get(j);
                     String getColumnName = getTableColumn.getColumnName();
                     columnNamesFromTableColumn.add(getColumnName);
                  }
               }
            }
         }
      }

      return columnNamesFromTableColumn;
   }

   public String checkIfSelectColumnInFunctionSelectColumnList(SelectQueryStatement toSelectQueryStatement, String orderbyColumnName, TableColumn tableColumn) {
      Vector getSelectColumn = toSelectQueryStatement.getSelectStatement().getSelectItemList();
      if (getSelectColumn != null) {
         for(int i = 0; i < getSelectColumn.size(); ++i) {
            SelectColumn getSelectColumnForOrderBy = (SelectColumn)getSelectColumn.get(i);
            Vector getSelectItems = getSelectColumnForOrderBy.getColumnExpression();
            if (getSelectItems != null) {
               for(int j = 0; j < getSelectItems.size(); ++j) {
                  if (getSelectItems.get(j) instanceof FunctionCalls) {
                     Vector functionArguments = ((FunctionCalls)getSelectItems.get(j)).getFunctionArguments();
                     if (functionArguments != null) {
                        for(int k = 0; k < functionArguments.size(); ++k) {
                           if (functionArguments.get(k) instanceof SelectColumn) {
                              SelectColumn selectColumn = (SelectColumn)functionArguments.get(k);
                              Vector functionColumns = selectColumn.getColumnExpression();
                              if (functionColumns != null) {
                                 for(int l = 0; l < functionColumns.size(); ++l) {
                                    if (functionColumns.get(l) instanceof TableColumn) {
                                       TableColumn getTableColumn = (TableColumn)functionColumns.get(l);
                                       String getColumnName = getTableColumn.getColumnName();
                                       if (getColumnName.equalsIgnoreCase(orderbyColumnName)) {
                                          orderbyColumnName = "" + (i + 1);
                                          tableColumn.setOwnerName((String)null);
                                          tableColumn.setTableName((String)null);
                                          tableColumn.setDot((String)null);
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
      }

      return orderbyColumnName;
   }

   private ArrayList convertOrderByColumnsToNumber(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ArrayList numberColList = new ArrayList();
      Vector from_SQS_SelectItems = from_sqs.getSelectStatement().getSelectItemList();
      ArrayList selectItemsTableColumn = new ArrayList();

      int index;
      String aliasName;
      for(index = 0; index < from_SQS_SelectItems.size(); ++index) {
         if (from_SQS_SelectItems.elementAt(index) instanceof SelectColumn) {
            SelectColumn fromSQLSelectCol = (SelectColumn)from_SQS_SelectItems.elementAt(index);
            if (fromSQLSelectCol.getAliasName() != null) {
               TableColumn tcAlias = new TableColumn();
               tcAlias.setColumnName(fromSQLSelectCol.getAliasName());
               selectItemsTableColumn.add(tcAlias);
            } else if (fromSQLSelectCol.getColumnExpression() != null) {
               Vector fromSQLSelectColExp = fromSQLSelectCol.getColumnExpression();

               for(int j = 0; j < fromSQLSelectColExp.size(); ++j) {
                  if (fromSQLSelectColExp.elementAt(j) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)fromSQLSelectColExp.elementAt(j);
                     selectItemsTableColumn.add(tc);
                  } else if (fromSQLSelectColExp.elementAt(j) instanceof String) {
                     String s_ce = (String)fromSQLSelectColExp.elementAt(j);
                     if (s_ce.indexOf("*") == -1) {
                        String tableOrAlias = s_ce;
                        FromClause fc = from_sqs.getFromClause();
                        Vector v_fil = fc.getFromItemList();
                        if (v_fil.size() > 1) {
                           for(int countNum = 0; countNum < v_fil.size(); ++countNum) {
                              if (v_fil.elementAt(countNum) instanceof FromTable) {
                                 FromTable ft = (FromTable)v_fil.elementAt(countNum);
                                 if (ft.getAliasName() == null) {
                                    Object o_tn = ft.getTableName();
                                    if (!(o_tn instanceof String)) {
                                       throw new ConvertException();
                                    }

                                    String tableName = (String)o_tn;
                                    if (tableName.toLowerCase().startsWith("dbo.")) {
                                       tableName = tableName.substring(4);
                                    } else if (tableName.toLowerCase().startsWith("[dbo].")) {
                                       tableName = tableName.substring(6);
                                    }

                                    if (tableOrAlias.equals(tableName)) {
                                       ArrayList colList = (ArrayList)SwisSQLAPI.getTableColumnListMetadata().get(tableOrAlias.trim());
                                       if (colList != null) {
                                          for(int colListInd = 0; colListInd < colList.size(); ++colListInd) {
                                             TableColumn tc1 = new TableColumn();
                                             tc1.setColumnName(colList.get(colListInd).toString());
                                             tc1.setTableName(tableOrAlias);
                                             selectItemsTableColumn.add(tc1);
                                          }
                                       }
                                    }
                                 } else {
                                    aliasName = ft.getAliasName();
                                    if (tableOrAlias.equals(aliasName)) {
                                       ArrayList colList = (ArrayList)SwisSQLAPI.getTableColumnListMetadata().get(ft.getTableName().toString().trim());
                                       if (colList != null) {
                                          for(int colListInd = 0; colListInd < colList.size(); ++colListInd) {
                                             TableColumn tc2 = new TableColumn();
                                             tc2.setColumnName(colList.get(colListInd).toString());
                                             tc2.setTableName(tableOrAlias);
                                             selectItemsTableColumn.add(tc2);
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
         }
      }

      for(index = 0; index < this.OrderItemList.size(); ++index) {
         if (this.OrderItemList.get(index) instanceof OrderItem) {
            OrderItem orderItem = (OrderItem)this.OrderItemList.get(index);
            orderItem.setObjectContext(this.context);
            SelectColumn sc = orderItem.getOrderSpecifier();
            if (sc.getColumnExpression() != null) {
               Vector columnExpression = sc.getColumnExpression();

               for(int i = 0; i < columnExpression.size(); ++i) {
                  if (columnExpression.get(i) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)columnExpression.get(i);

                     for(int sel_Ind = 0; sel_Ind < selectItemsTableColumn.size(); ++sel_Ind) {
                        TableColumn tableCol = (TableColumn)selectItemsTableColumn.get(sel_Ind);
                        if (tc.toString().trim().equals(tableCol.toString().trim())) {
                           TableColumn numberCol = new TableColumn();
                           int numberColVal = sel_Ind + 1;
                           aliasName = "" + numberColVal;
                           numberCol.setColumnName(aliasName.trim());
                           numberColList.add(numberCol);
                        }
                     }
                  }
               }
            }
         }
      }

      return numberColList;
   }

   private void addCaseStatementToOrderItemsList(int position, OrderItem oi, String thenClause, String elseClause) {
      OrderItem caseOrderItem = new OrderItem();
      SelectColumn caseColumn = new SelectColumn();
      Vector caseColumnExp = new Vector();
      CaseStatement caseStmtforNullsClause = new CaseStatement();
      SelectColumn scForThenCondition = new SelectColumn();
      Vector colExpForWhenCondition = new Vector();
      WhereExpression we = new WhereExpression();
      Vector whereItems = new Vector();
      WhereItem wi = new WhereItem();
      WhereColumn wc = new WhereColumn();
      Vector whereColumnsVector = new Vector();
      Vector thenStmts = new Vector();
      WhenStatement whenClause = new WhenStatement();
      SelectColumn scForElseStmt = new SelectColumn();
      Vector elseStmts = new Vector();
      caseStmtforNullsClause.setCaseClause("CASE");
      caseStmtforNullsClause.setElseClause("ELSE");
      caseStmtforNullsClause.setEndClause("END");
      whenClause.setWhenClause("WHEN");
      whereColumnsVector.add(oi.getOrderSpecifier());
      wc.setColumnExpression(whereColumnsVector);
      wi.setLeftWhereExp(wc);
      wi.setOperator("IS NULL");
      whereItems.add(wi);
      we.setWhereItem(whereItems);
      whenClause.setWhenCondition(we);
      whenClause.setThenClause("THEN");
      thenStmts.add(thenClause);
      scForThenCondition.setColumnExpression(thenStmts);
      whenClause.setThenStatement(scForThenCondition);
      colExpForWhenCondition.add(whenClause);
      elseStmts.add(elseClause);
      scForElseStmt.setColumnExpression(elseStmts);
      caseStmtforNullsClause.setWhenStatementList(colExpForWhenCondition);
      caseStmtforNullsClause.setElseStatement(scForElseStmt);
      caseColumnExp.add(caseStmtforNullsClause);
      caseColumn.setColumnExpression(caseColumnExp);
      caseOrderItem.setOrderSpecifier(caseColumn);
      this.OrderItemList.add(position, caseOrderItem);
      ++position;
   }

   public OrderByStatement toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obs = new OrderByStatement();
      new OrderItem();
      if (this.OrderClause != null) {
         obs.setOrderClause(this.OrderClause);
      }

      Vector v_oil = new Vector();
      OrderItem oi;
      if (this.OrderItemList.size() == 1) {
         oi = ((OrderItem)this.OrderItemList.elementAt(0)).toVectorWiseSelect(to_sqs, from_sqs);
         if (oi != null) {
            v_oil.addElement(oi);
            obs.setOrderItemList(v_oil);
         } else {
            obs = null;
         }
      } else {
         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs);
            if (oi != null) {
               v_oil.addElement(oi);
            }
         }

         obs.setOrderItemList(v_oil);
      }

      return obs;
   }

   public OrderByStatement toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderByStatement obsConv = new OrderByStatement();
      if (this.OrderClause != null) {
         obsConv.setOrderClause(this.OrderClause);
      }

      if (this.OrderItemList != null) {
         Vector vcOrderItemListConv = new Vector();
         from_sqs.setIsAliasReferenceClausesIteration(true);

         for(int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            new OrderItem();
            OrderItem oi = ((OrderItem)this.OrderItemList.elementAt(i_count)).toReplaceTblCol(to_sqs, from_sqs);
            if (oi != null) {
               vcOrderItemListConv.addElement(oi);
            }
         }

         obsConv.setOrderItemList(vcOrderItemListConv);
         from_sqs.setIsAliasReferenceClausesIteration(false);
      }

      if (this.commentObj != null) {
         obsConv.setCommentClass(this.commentObj);
      }

      if (this.commentObjAfterToken != null) {
         obsConv.setCommentClassAfterToken(this.commentObjAfterToken);
      }

      if (this.siblings != null) {
         obsConv.setSiblings(this.siblings);
      }

      return obsConv;
   }
}
