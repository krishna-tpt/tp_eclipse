package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;
import java.util.Vector;

public class HavingStatement {
   private String HavingClause;
   private Vector HavingItems;
   private UserObjectContext context = null;
   private CommentClass commentObj;

   public void setHavingClause(String s_hc) {
      this.HavingClause = s_hc;
   }

   public void addHavingItems(Object o_hi) {
      this.HavingItems.addElement(o_hi);
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   public void setHavingItems(Vector v_hi) {
      this.HavingItems = v_hi;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public String getHavingClause() {
      return this.HavingClause;
   }

   public Vector getHavingItems() {
      return this.HavingItems;
   }

   public HavingStatement toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int ansi = 6;
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      int i_count;
      if (from_sqs.getGroupByStatement() == null) {
         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               WhereExpression we = (WhereExpression)this.HavingItems.elementAt(i_count);
               this.processWhereExpressionInHaving(to_sqs, we, ansi);
               if (to_sqs.getWhereExpression() != null) {
                  WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                  to_SQSWhereExp.addWhereExpression(we);
                  to_SQSWhereExp.addOperator("AND");
               } else {
                  to_sqs.setWhereExpression(we);
               }
            }

            hs = null;
         }
      } else {
         hs.setHavingClause(this.HavingClause);

         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
            }
         }

         hs.setHavingItems(v_hi);
      }

      return hs;
   }

   public HavingStatement toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int ansi = 6;
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      int i_count;
      if (from_sqs.getGroupByStatement() == null && this.HavingClause != null && this.HavingClause.trim().equalsIgnoreCase("having")) {
         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               WhereExpression we = (WhereExpression)this.HavingItems.elementAt(i_count);
               this.processWhereExpressionInHaving(to_sqs, we, ansi);
               if (to_sqs.getWhereExpression() != null) {
                  WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                  to_SQSWhereExp.addWhereExpression(we);
                  to_SQSWhereExp.addOperator("AND");
               } else {
                  to_sqs.setWhereExpression(we);
               }
            }

            hs = null;
         }
      } else if (this.HavingClause != null && this.HavingClause.trim().equalsIgnoreCase("QUALIFY")) {
         hs.setHavingClause(this.HavingClause);

         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
            }
         }

         hs.setHavingItems(v_hi);
      } else {
         hs.setHavingClause(this.HavingClause);

         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
            }
         }

         hs.setHavingItems(v_hi);
      }

      return hs;
   }

   public HavingStatement toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      int db2 = 2;
      int i_count;
      if (from_sqs.getGroupByStatement() == null) {
         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               WhereExpression we = (WhereExpression)this.HavingItems.elementAt(i_count);
               this.processWhereExpressionInHaving(to_sqs, we, db2);
               if (to_sqs.getWhereExpression() != null) {
                  WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                  to_SQSWhereExp.addWhereExpression(we);
                  to_SQSWhereExp.addOperator("AND");
               } else {
                  to_sqs.setWhereExpression(we);
               }
            }

            hs = null;
         }
      } else {
         hs.setHavingClause(this.HavingClause);

         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
            }
         }

         hs.setHavingItems(v_hi);
      }

      return hs;
   }

   public HavingStatement toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      hs.setHavingClause(this.HavingClause);

      for(int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
         if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
            v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         }
      }

      hs.setHavingItems(v_hi);
      return hs;
   }

   public HavingStatement toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      hs.setHavingClause(this.HavingClause);

      for(int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
         if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
            v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         }
      }

      hs.setHavingItems(v_hi);
      return hs;
   }

   public HavingStatement toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      hs.setHavingClause(this.HavingClause);

      for(int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
         if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
            v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         }
      }

      hs.setHavingItems(v_hi);
      return hs;
   }

   public HavingStatement toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      hs.setHavingClause(this.HavingClause);

      for(int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
         if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
            v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         }
      }

      hs.setHavingItems(v_hi);
      return hs;
   }

   public HavingStatement toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      hs.setHavingClause(this.HavingClause);

      for(int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
         if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
            v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         }
      }

      hs.setHavingItems(v_hi);
      return hs;
   }

   public HavingStatement toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      hs.setHavingClause(this.HavingClause);

      for(int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
         if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
            v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         }
      }

      hs.setHavingItems(v_hi);
      return hs;
   }

   public HavingStatement toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      hs.setHavingClause(this.HavingClause);

      for(int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
         if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
            v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         }
      }

      hs.setHavingItems(v_hi);
      return hs;
   }

   public HavingStatement toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      int postgreSQL = true;
      hs.setHavingClause(this.HavingClause);

      for(int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
         if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
            v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         }
      }

      hs.setHavingItems(v_hi);
      return hs;
   }

   public HavingStatement toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      int msSQLServer = 1;
      int i_count;
      if (from_sqs.getGroupByStatement() == null) {
         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               WhereExpression we = (WhereExpression)this.HavingItems.elementAt(i_count);
               this.processWhereExpressionInHaving(to_sqs, we, msSQLServer);
               if (to_sqs.getWhereExpression() != null) {
                  WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                  we = we.toMSSQLServerSelect(to_sqs, from_sqs);
                  to_SQSWhereExp.addWhereExpression(we);
                  to_SQSWhereExp.addOperator("AND");
               } else {
                  we = we.toMSSQLServerSelect(to_sqs, from_sqs);
                  to_sqs.setWhereExpression(we);
               }
            }

            hs = null;
         }
      } else {
         hs.setHavingClause(this.HavingClause);

         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
            }
         }

         hs.setHavingItems(v_hi);
      }

      return hs;
   }

   public HavingStatement toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      hs.setHavingClause(this.HavingClause);

      for(int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
         if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
            v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         }
      }

      hs.setHavingItems(v_hi);
      return hs;
   }

   public HavingStatement toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      hs.setHavingClause(this.HavingClause);

      for(int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
         if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
            v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         }
      }

      hs.setHavingItems(v_hi);
      return hs;
   }

   public HavingStatement toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      hs.setCommentClass(this.commentObj);
      Vector v_hi = new Vector();
      int oracle = 0;
      int i_count;
      if (from_sqs.getGroupByStatement() == null) {
         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               WhereExpression we = (WhereExpression)this.HavingItems.elementAt(i_count);
               this.processWhereExpressionInHaving(to_sqs, we, oracle);
               if (to_sqs.getWhereExpression() != null) {
                  WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                  to_SQSWhereExp.addWhereExpression(we);
                  to_SQSWhereExp.addOperator("AND");
               } else {
                  to_sqs.setWhereExpression(we);
               }
            }

            hs = null;
         }
      } else {
         hs.setHavingClause(this.HavingClause);

         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
            }
         }

         hs.setHavingItems(v_hi);
      }

      return hs;
   }

   public HavingStatement toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      int informix = 3;
      int i_count;
      if (from_sqs.getGroupByStatement() == null) {
         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               WhereExpression we = (WhereExpression)this.HavingItems.elementAt(i_count);
               this.processWhereExpressionInHaving(to_sqs, we, informix);
               if (to_sqs.getWhereExpression() != null) {
                  WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                  to_SQSWhereExp.addWhereExpression(we);
                  to_SQSWhereExp.addOperator("AND");
               } else {
                  to_sqs.setWhereExpression(we);
               }
            }

            hs = null;
         }
      } else {
         hs.setHavingClause(this.HavingClause);

         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
            }
         }

         hs.setHavingItems(v_hi);
      }

      return hs;
   }

   public HavingStatement toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      int i_count;
      if (from_sqs.getGroupByStatement() == null) {
         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               WhereExpression we = (WhereExpression)this.HavingItems.elementAt(i_count);
               this.processWhereExpressionInHaving(to_sqs, we, 10);
               if (to_sqs.getWhereExpression() != null) {
                  WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                  to_SQSWhereExp.addWhereExpression(we);
                  to_SQSWhereExp.addOperator("AND");
               } else {
                  to_sqs.setWhereExpression(we);
               }
            }

            hs = null;
         }
      } else {
         hs.setHavingClause(this.HavingClause);

         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toTimesTenSelect(to_sqs, from_sqs));
            }
         }

         hs.setHavingItems(v_hi);
      }

      return hs;
   }

   public HavingStatement toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int ansi = 6;
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      int i_count;
      if (from_sqs.getGroupByStatement() == null) {
         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               WhereExpression we = (WhereExpression)this.HavingItems.elementAt(i_count);
               this.processWhereExpressionInHaving(to_sqs, we, ansi);
               if (to_sqs.getWhereExpression() != null) {
                  WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                  to_SQSWhereExp.addWhereExpression(we);
                  to_SQSWhereExp.addOperator("AND");
               } else {
                  to_sqs.setWhereExpression(we);
               }
            }

            hs = null;
         }
      } else {
         hs.setHavingClause(this.HavingClause);

         for(i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
            }
         }

         hs.setHavingItems(v_hi);
      }

      return hs;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.commentObj != null) {
         sb.append(this.commentObj.toString().trim() + " ");
      }

      sb.append(this.HavingClause.toUpperCase() + " ");

      for(int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
         if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
            ((WhereExpression)this.HavingItems.elementAt(i_count)).setObjectContext(this.context);
         } else if (this.HavingItems.elementAt(i_count) instanceof SelectQueryStatement) {
            ((SelectQueryStatement)this.HavingItems.elementAt(i_count)).setObjectContext(this.context);
         } else if (this.HavingItems.elementAt(i_count) instanceof SelectColumn) {
            ((SelectColumn)this.HavingItems.elementAt(i_count)).setObjectContext(this.context);
         } else if (this.HavingItems.elementAt(i_count) instanceof FunctionCalls) {
            ((FunctionCalls)this.HavingItems.elementAt(i_count)).setObjectContext(this.context);
         }

         sb.append(this.HavingItems.elementAt(i_count).toString() + " ");
      }

      return sb.toString();
   }

   public void convertAggregateFunctionIntoSubQuery(Vector columnExpression, SelectQueryStatement toSQS, int database) throws ConvertException {
      for(int i = 0; i < columnExpression.size(); ++i) {
         if (columnExpression.elementAt(i) instanceof FunctionCalls) {
            FunctionCalls fnCall = (FunctionCalls)columnExpression.elementAt(i);
            String functionName = fnCall.getFunctionNameAsAString();
            if (functionName != null) {
               if (!functionName.equalsIgnoreCase("avg") && !functionName.equalsIgnoreCase("count") && !functionName.equalsIgnoreCase("max") && !functionName.equalsIgnoreCase("min") && !functionName.equalsIgnoreCase("sum")) {
                  if (database == 1) {
                     Vector fnArgs = fnCall.getFunctionArguments();
                     if (fnArgs != null) {
                        for(int j = 0; j < fnArgs.size(); ++j) {
                           Object obj = fnArgs.get(j);
                           if (obj instanceof SelectColumn) {
                              this.convertAggregateFunctionIntoSubQuery(((SelectColumn)obj).getColumnExpression(), toSQS, database);
                           }
                        }
                     }
                  }
               } else {
                  SelectQueryStatement sqs = new SelectQueryStatement();
                  SelectStatement ss = new SelectStatement();
                  Vector selectItems = new Vector();
                  SelectColumn sc = new SelectColumn();
                  Vector aggrFunctionExpr = new Vector();
                  sqs.setCloseBrace(")");
                  sqs.setOpenBrace("(");
                  aggrFunctionExpr.add((FunctionCalls)columnExpression.elementAt(i));
                  sc.setColumnExpression(aggrFunctionExpr);
                  selectItems.add(sc);
                  ss.setSelectClause("SELECT");
                  ss.setSelectItemList(selectItems);
                  sqs.setSelectStatement(ss);
                  if (toSQS != null && toSQS.getFromClause() != null) {
                     FromClause toFromClause = toSQS.getFromClause();
                     FromClause fc = new FromClause();
                     fc.setFromClause(toFromClause.getFromClause());
                     Vector vembuFromItems = toFromClause.getFromItemList();
                     if (vembuFromItems != null) {
                        Vector newFromItemList = new Vector();

                        for(int j = 0; j < vembuFromItems.size(); ++j) {
                           if (vembuFromItems.get(j) instanceof FromTable) {
                              newFromItemList.add(vembuFromItems.get(j));
                              break;
                           }
                        }

                        fc.setFromItemList(newFromItemList);
                     }

                     sqs.setFromClause(fc);
                  }

                  columnExpression.set(i, sqs);
                  if (database == 5) {
                     throw new ConvertException("Conversion Failure.. Invalid Query..");
                  }
               }
            }
         }
      }

   }

   public void processWhereExpressionInHaving(SelectQueryStatement sqs, WhereExpression we, int database) throws ConvertException {
      if (we != null) {
         for(int i = 0; i < we.getWhereItems().size(); ++i) {
            if (we.getWhereItems().get(i) instanceof WhereExpression) {
               WhereExpression newWE = (WhereExpression)we.getWhereItems().get(i);
               this.processWhereExpressionInHaving(sqs, newWE, database);
            }

            if (we.getWhereItems().get(i) instanceof WhereItem) {
               WhereItem wi = (WhereItem)we.getWhereItems().get(i);
               WhereColumn leftSideCol = wi.getLeftWhereExp();
               if (leftSideCol != null) {
                  Vector leftExpr = leftSideCol.getColumnExpression();
                  if (leftExpr != null) {
                     this.convertAggregateFunctionIntoSubQuery(leftExpr, sqs, database);
                  }
               }

               WhereColumn rightSideCol = wi.getRightWhereExp();
               if (rightSideCol != null) {
                  Vector rightExpr = rightSideCol.getColumnExpression();
                  if (rightExpr != null) {
                     this.convertAggregateFunctionIntoSubQuery(rightExpr, sqs, database);
                  }
               }
            }
         }
      }

   }

   public HavingStatement toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      hs.setHavingClause(this.HavingClause);

      for(int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
         if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
            v_hi.addElement(((WhereExpression)this.HavingItems.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         }
      }

      hs.setHavingItems(v_hi);
      return hs;
   }

   public HavingStatement toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      HavingStatement havingStmtConv = new HavingStatement();
      havingStmtConv.setHavingClause(this.HavingClause);
      from_sqs.setIsAliasReferenceClausesIteration(true);
      if (this.HavingItems != null) {
         Vector havingItemsConv = new Vector();

         for(int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
               WhereExpression we = (WhereExpression)this.HavingItems.elementAt(i_count);
               this.checkAndReplaceTblCol(we, from_sqs, to_sqs);
               havingItemsConv.addElement(we);
            }
         }

         havingStmtConv.setHavingItems(havingItemsConv);
      }

      from_sqs.setIsAliasReferenceClausesIteration(false);
      if (this.commentObj != null) {
         havingStmtConv.setCommentClass(this.commentObj);
      }

      return havingStmtConv;
   }

   public void checkAndReplaceTblCol(WhereExpression we, SelectQueryStatement from_sqs, SelectQueryStatement to_sqs) throws ConvertException {
      for(int i = 0; i < we.getWhereItems().size(); ++i) {
         if (!(we.getWhereItems().get(i) instanceof WhereItem)) {
            if (we.getWhereItems().get(i) instanceof WhereExpression) {
               this.checkAndReplaceTblCol((WhereExpression)we.getWhereItems().get(i), from_sqs, to_sqs);
            }
         } else {
            WhereItem wi = (WhereItem)we.getWhereItems().get(i);
            if (wi.getLeftWhereExp() != null && (wi.getLeftWhereExp().getColumnExpression() == null || wi.getLeftWhereExp().getColumnExpression().size() != 1 || !(wi.getLeftWhereExp().getColumnExpression().get(0) instanceof TableColumn) || ((TableColumn)wi.getLeftWhereExp().getColumnExpression().get(0)).getTableName() != null || !CastingUtil.ContainsIgnoreCase(from_sqs.getAliasColumns(), GeneralUtil.trimIfAliasNameIsEnclosed(((TableColumn)wi.getLeftWhereExp().getColumnExpression().get(0)).getColumnName())))) {
               wi.setLeftWhereExp(wi.getLeftWhereExp().toReplaceTblCol(to_sqs, from_sqs));
            }

            if (wi.getRightWhereExp() != null && (wi.getRightWhereExp().getColumnExpression() == null || wi.getRightWhereExp().getColumnExpression().size() != 1 || !(wi.getRightWhereExp().getColumnExpression().get(0) instanceof TableColumn) || ((TableColumn)wi.getRightWhereExp().getColumnExpression().get(0)).getTableName() != null || !CastingUtil.ContainsIgnoreCase(from_sqs.getAliasColumns(), GeneralUtil.trimIfAliasNameIsEnclosed(((TableColumn)wi.getRightWhereExp().getColumnExpression().get(0)).getColumnName())))) {
               wi.setRightWhereExp(wi.getRightWhereExp().toReplaceTblCol(to_sqs, from_sqs));
            }

            if (wi.getRightWhereSubQuery() != null) {
               wi.getRightWhereSubQuery().setPropAndHandlerFromSQS(from_sqs);
               wi.setRightWhereSubQuery(wi.getRightWhereSubQuery().toReplaceTblCol());
               if (wi.getRightWhereSubQueryExp() != null) {
                  wi.setRightWhereSubQueryExp(wi.getRightWhereSubQueryExp().toReplaceTblCol(to_sqs, from_sqs));
               }
            }
         }
      }

   }
}
