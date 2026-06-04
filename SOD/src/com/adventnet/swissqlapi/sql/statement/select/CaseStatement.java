package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.Iterator;
import java.util.Vector;

public class CaseStatement {
   private String caseClause = null;
   private WhereExpression caseCondition = null;
   private SelectQueryStatement subquery;
   private Vector whenStatementList = null;
   private String elseClause = null;
   private SelectColumn elseStatement = null;
   private String endClause = null;
   private UserObjectContext context = null;
   private String decodeFunction = null;
   private CommentClass commentObj;
   private CommentClass commentObjAfterToken;
   private boolean castToTextInsideIf = true;
   private String instanceDatatype = "UNDEFINED";
   private Vector curr_whereItem = new Vector();

   public void setCastToTextInsideIf(boolean yes) {
      this.castToTextInsideIf = yes;
   }

   public void setCaseClause(String s_case_clause) {
      this.caseClause = s_case_clause;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   public void setCaseCondition(WhereExpression we_case_condition) {
      this.caseCondition = we_case_condition;
   }

   public void setSubQuery(SelectQueryStatement subquery) {
      this.subquery = subquery;
   }

   public void setWhenStatementList(Vector v_when_clause_list) {
      this.whenStatementList = v_when_clause_list;
   }

   public void setElseClause(String s_else_clause) {
      this.elseClause = s_else_clause;
   }

   public void setElseStatement(SelectColumn sc_else_statement) {
      this.elseStatement = sc_else_statement;
   }

   public void setEndClause(String s_end_clause) {
      this.endClause = s_end_clause;
   }

   public void setDecodeFunction(String s_decode_function) {
      this.decodeFunction = s_decode_function;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public void setCommentClassAfterToken(CommentClass endCommentObj) {
      this.commentObjAfterToken = endCommentObj;
   }

   public void setInstanceDatatype(String instanceDatatype) {
      this.instanceDatatype = instanceDatatype;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public CommentClass getCommentClassAfterToken() {
      return this.commentObjAfterToken;
   }

   public String getDecodeFunction() {
      return this.decodeFunction;
   }

   public String getCaseClause() {
      return this.caseClause;
   }

   public WhereExpression getCaseCondition() {
      return this.caseCondition;
   }

   public SelectQueryStatement getSubQuery() {
      return this.subquery;
   }

   public Vector getWhenClauseList() {
      return this.whenStatementList;
   }

   public String getElseClause() {
      return this.elseClause;
   }

   public SelectColumn getElseStatement() {
      return this.elseStatement;
   }

   public String getEndClause() {
      return this.endClause;
   }

   public String getInstanceDatatype() {
      return this.instanceDatatype;
   }

   public String convertToBooleanFunction(WhereItem wi) throws ConvertException {
      StringBuffer sb = new StringBuffer();
      String s_operator;
      if (wi.getRightWhereExp() == null && wi.getRightWhereSubQuery() == null) {
         s_operator = wi.toString().toUpperCase();
         if (s_operator.indexOf("NOT") == -1) {
            sb.append("DECODE( " + wi.getLeftWhereExp().toString() + ", NULL , 1 , 0 )");
         } else {
            sb.append("DECODE( " + wi.getLeftWhereExp().toString() + ", NULL , 0 , 1 )");
         }
      } else {
         WhereColumn wc;
         if (wi.getRightWhereExp() != null && wi.getRightWhereSubQuery() == null) {
            s_operator = wi.getOperator();
            if (s_operator.equals("=")) {
               sb.append("DECODE( " + wi.getLeftWhereExp().toString() + "," + wi.getRightWhereExp().toString());
               sb.append(",1,0)");
            } else if (!s_operator.equals("!=") && !s_operator.equals("<>") && !s_operator.equals("^=")) {
               if (s_operator.equals("<")) {
                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),");
                  sb.append("(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),0,0,0,1 )");
               } else if (s_operator.equals(">")) {
                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),0,0, ");
                  sb.append("(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),1,0 )");
               } else if (s_operator.equals("<=")) {
                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),");
                  sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),0,1 )");
               } else if (s_operator.equals(">=")) {
                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),");
                  sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),1,0)");
               } else {
                  WhereColumn wc;
                  int i;
                  if (s_operator.equalsIgnoreCase("IN")) {
                     sb.append("DECODE( " + wi.getLeftWhereExp());
                     sb.append(",");
                     wc = wi.getRightWhereExp();
                     if (wc.getColumnExpression() != null) {
                        if (wc.getColumnExpression().size() == 1) {
                           sb.append(wc.getColumnExpression().get(0).toString() + ",1");
                        } else {
                           for(i = 0; i < wc.getColumnExpression().size(); ++i) {
                              if (!")".equals(wc.getColumnExpression().get(i).toString().trim()) && !"(".equals(wc.getColumnExpression().get(i).toString().trim()) && !",".equals(wc.getColumnExpression().get(i).toString().trim())) {
                                 sb.append(wc.getColumnExpression().get(i).toString() + ",");
                                 sb.append("1");
                                 if (i != wc.getColumnExpression().size() - 2) {
                                    sb.append(",");
                                 }
                              }
                           }
                        }
                     }

                     sb.append(",0 )");
                  } else if (s_operator.equalsIgnoreCase("NOT IN")) {
                     sb.append("DECODE( " + wi.getLeftWhereExp());
                     sb.append(",");
                     wc = wi.getRightWhereExp();
                     if (wc.getColumnExpression() != null) {
                        if (wc.getColumnExpression().size() == 1) {
                           sb.append(wc.getColumnExpression().get(0).toString() + ",0");
                        } else {
                           for(i = 0; i < wc.getColumnExpression().size(); ++i) {
                              if (i < wc.getColumnExpression().size() - 1) {
                                 if (!")".equals(wc.getColumnExpression().get(i).toString().trim()) && !"(".equals(wc.getColumnExpression().get(i).toString().trim()) && !",".equals(wc.getColumnExpression().get(i).toString().trim())) {
                                    sb.append(wc.getColumnExpression().get(i).toString() + ",");
                                    sb.append("0");
                                    if (i != wc.getColumnExpression().size() - 2) {
                                       sb.append(",");
                                    }
                                 }
                              } else if (!")".equals(wc.getColumnExpression().get(i).toString().trim()) && !"(".equals(wc.getColumnExpression().get(i).toString().trim()) && !",".equals(wc.getColumnExpression().get(i).toString().trim())) {
                                 sb.append(wc.getColumnExpression().get(i).toString() + ",");
                                 sb.append("1");
                                 if (i != wc.getColumnExpression().size() - 2) {
                                    sb.append(",");
                                 }
                              }
                           }
                        }
                     }

                     sb.append(",1 )");
                  } else {
                     if (!s_operator.equalsIgnoreCase("BETWEEN")) {
                        throw new ConvertException("Current operator yet to be supported ");
                     }

                     String var = wi.getLeftWhereExp().toString();
                     sb.append("DECODE( GREATEST(" + var + ",");
                     String v1 = "";
                     String v2 = "";
                     wc = wi.getRightWhereExp();
                     if (wc.getColumnExpression() != null && wc.getColumnExpression().size() == 3 && wc.getColumnExpression().get(1).toString().trim().equalsIgnoreCase("AND")) {
                        v1 = wc.getColumnExpression().get(0).toString();
                        v2 = wc.getColumnExpression().get(2).toString();
                     }

                     sb.append(v1 + "), " + var + ", DECODE(LEAST(" + var + "," + v2 + ")," + var + ",1,0),0)");
                  }
               }
            } else {
               sb.append("DECODE(" + wi.getLeftWhereExp().toString() + "," + wi.getRightWhereExp().toString());
               sb.append(",0,1)");
            }
         } else if (wi.getRightWhereSubQuery() != null) {
            s_operator = wi.getOperator();
            if (s_operator.equals("=")) {
               sb.append("DECODE( " + wi.getLeftWhereExp().toString() + "," + "(" + wi.getRightWhereSubQuery().toString() + ")");
               sb.append(",1,0)");
            } else if (!s_operator.equals("!=") && !s_operator.equals("<>") && !s_operator.equals("^=")) {
               if (s_operator.equals("<")) {
                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + " ) ),");
                  sb.append("(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + " ) ),0,0,0,1 )");
               } else if (s_operator.equals(">")) {
                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),0,0 ,");
                  sb.append("(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),1,0 )");
               } else if (s_operator.equals("<=")) {
                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + " ) ),");
                  sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),0,1 )");
               } else if (s_operator.equals(">=")) {
                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),");
                  sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),1,0)");
               } else {
                  if (!"exists".equalsIgnoreCase(s_operator.trim())) {
                     throw new ConvertException("Current operator yet to be supported");
                  }

                  sb.append("DECODE( 1 , (");
                  SelectQueryStatement selectQueryStatement = wi.getRightWhereSubQuery();
                  Vector v = new Vector();
                  v.add("1");
                  WhereItem whereItem = new WhereItem();
                  wc = new WhereColumn();
                  WhereColumn whereColumnR = new WhereColumn();
                  Vector colExpL = new Vector();
                  Vector colExpR = new Vector();
                  colExpL.add("ROWNUM");
                  colExpR.add("2");
                  wc.setColumnExpression(colExpL);
                  whereColumnR.setColumnExpression(colExpR);
                  whereItem.setLeftWhereExp(wc);
                  whereItem.setRightWhereExp(whereColumnR);
                  whereItem.setOperator("<");
                  WhereExpression whereExpression = selectQueryStatement.getWhereExpression();
                  if (whereExpression != null) {
                     whereExpression.addOperator("AND");
                     whereExpression.addWhereItem(whereItem);
                  } else {
                     whereExpression = new WhereExpression();
                     whereExpression.addWhereItem(whereItem);
                     selectQueryStatement.setWhereExpression(whereExpression);
                  }

                  selectQueryStatement.getSelectStatement().setSelectItemList(v);
                  sb.append(selectQueryStatement.toString() + " ),1,0 )");
               }
            } else {
               sb.append("DECODE(" + wi.getLeftWhereExp().toString() + "," + "(" + wi.getRightWhereSubQuery().toString() + ")");
               sb.append(",0,1)");
            }
         }
      }

      return sb.toString();
   }

   public String convertToBooleanFunction(WhereExpression caseCondition) throws ConvertException {
      StringBuffer sb = new StringBuffer();
      if (caseCondition.getOpenBrace() == null && caseCondition.getWhereItems().size() > 1) {
         throw new ConvertException("Multiple Conditions yet to be supported  1");
      } else {
         if (caseCondition.getOpenBrace() != null && caseCondition.getWhereItems().size() == 1) {
            if (caseCondition.getWhereItems().elementAt(0) instanceof WhereExpression) {
               WhereExpression we = (WhereExpression)caseCondition.getWhereItems().elementAt(0);
               if (we.getWhereItems().size() > 1) {
                  throw new ConvertException("Conversion Failure..Multiple Conditions are not supported 2");
               }
            }
         } else if (caseCondition.getWhereItems().get(1) instanceof WhereExpression) {
            String str = this.convertToBooleanFunction((WhereExpression)caseCondition.getWhereItems().get(1));
            sb.append("DECODE(2,");
            sb.append(str);
            sb.append(" + ");
         }

         new WhereItem();
         WhereItem wi;
         if (caseCondition.getWhereItems().elementAt(0) instanceof WhereExpression) {
            WhereExpression we = (WhereExpression)caseCondition.getWhereItems().elementAt(0);
            wi = (WhereItem)we.getWhereItems().elementAt(0);
         } else {
            wi = (WhereItem)caseCondition.getWhereItems().elementAt(0);
         }

         String s_operator;
         if (wi.getRightWhereExp() == null && wi.getRightWhereSubQuery() == null) {
            s_operator = wi.toString().toUpperCase();
            if (s_operator.indexOf("NOT") == -1) {
               sb.append("DECODE( " + wi.getLeftWhereExp().toString() + ", NULL , 1 , 0 )");
            } else {
               sb.append("DECODE( " + wi.getLeftWhereExp().toString() + ", NULL , 0 , 1 )");
            }
         } else if (wi.getRightWhereExp() != null && wi.getRightWhereSubQuery() == null) {
            s_operator = wi.getOperator();
            if (s_operator.equals("=")) {
               sb.append("DECODE( " + wi.getLeftWhereExp().toString() + "," + wi.getRightWhereExp().toString());
               sb.append(",1,0)");
            } else if (!s_operator.equals("!=") && !s_operator.equals("<>") && !s_operator.equals("^=")) {
               if (s_operator.equals("<")) {
                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),");
                  sb.append("(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),0,0,0,1 )");
               } else if (s_operator.equals(">")) {
                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),0,0, ");
                  sb.append("(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),1,0 )");
               } else if (s_operator.equals("<=")) {
                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),");
                  sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),0,1 )");
               } else {
                  if (!s_operator.equals(">=")) {
                     throw new ConvertException("Conversion Failure..operator not supported");
                  }

                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),");
                  sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),1,0)");
               }
            } else {
               sb.append("DECODE(" + wi.getLeftWhereExp().toString() + "," + wi.getRightWhereExp().toString());
               sb.append(",0,1)");
            }
         } else if (wi.getRightWhereSubQuery() != null) {
            s_operator = wi.getOperator();
            if (s_operator.equals("=")) {
               sb.append("DECODE( " + wi.getLeftWhereExp().toString() + "," + "(" + wi.getRightWhereSubQuery().toString() + ")");
               sb.append(",1,0)");
            } else if (!s_operator.equals("!=") && !s_operator.equals("<>") && !s_operator.equals("^=")) {
               if (s_operator.equals("<")) {
                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + " ) ),");
                  sb.append("(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + " ) ),0,0,0,1 )");
               } else if (s_operator.equals(">")) {
                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),0,0 ,");
                  sb.append("(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),1,0 )");
               } else if (s_operator.equals("<=")) {
                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + " ) ),");
                  sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),0,1 )");
               } else {
                  if (!s_operator.equals(">=")) {
                     throw new ConvertException("Conversion Failure..operator not supported");
                  }

                  sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),");
                  sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),1,0)");
               }
            } else {
               sb.append("DECODE(" + wi.getLeftWhereExp().toString() + "," + "(" + wi.getRightWhereSubQuery().toString() + ")");
               sb.append(",0,1)");
            }
         }

         return sb.toString();
      }
   }

   public CaseStatement toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      cs.setCaseClause(this.caseClause);
      if (this.caseCondition != null) {
         cs.setCaseCondition(this.caseCondition.toMySQLSelect(to_sqs, from_sqs));
      }

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         v_when_statement_list.addElement(((WhenStatement)this.whenStatementList.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         cs.setElseStatement(this.elseStatement.toMySQLSelect(to_sqs, from_sqs));
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      return cs;
   }

   public CaseStatement toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      boolean needsCasting = false;
      cs.setCaseClause(this.caseClause);
      if (this.caseCondition != null) {
         cs.setCaseCondition(this.caseCondition.toBigQuerySelect(to_sqs, from_sqs));
      }

      needsCasting = this.needsCastingForStringLiterals() || this.castToTextInsideIf && to_sqs != null && to_sqs.getBooleanValues("set.can.cast.all.to.text.columns");

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         WhenStatement ws = (WhenStatement)this.whenStatementList.elementAt(i);
         SelectColumn sc = ws.getThenStatement();
         if (sc != null && needsCasting) {
            Vector colExp = sc.getColumnExpression();
            Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "STRING");
            if (castToCharExp != null) {
               sc.setColumnExpression(castToCharExp);
            }
         }

         v_when_statement_list.addElement(ws.toBigQuerySelect(to_sqs, from_sqs));
      }

      if (this.subquery != null) {
         cs.setSubQuery(this.subquery.toBigQuery());
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         if (needsCasting) {
            Vector colExp = this.elseStatement.getColumnExpression();
            Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "STRING");
            if (castToCharExp != null) {
               this.elseStatement.setColumnExpression(castToCharExp);
            }
         }

         cs.setElseStatement(this.elseStatement.toBigQuerySelect(to_sqs, from_sqs));
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      if (to_sqs != null) {
         to_sqs.addCurrentIndexToIfFunctionList();
      }

      return cs;
   }

   public CaseStatement toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      boolean needsCasting = false;
      cs.setCaseClause(this.caseClause);
      if (this.caseCondition != null) {
         cs.setCaseCondition(this.caseCondition.toAthenaSelect(to_sqs, from_sqs));
      }

      needsCasting = this.needsCastingForStringLiterals() || this.castToTextInsideIf && to_sqs != null && to_sqs.getBooleanValues("set.can.cast.all.to.text.columns");

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         WhenStatement ws = (WhenStatement)this.whenStatementList.elementAt(i);
         SelectColumn sc = ws.getThenStatement();
         if (sc != null && needsCasting) {
            Vector colExp = sc.getColumnExpression();
            Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "STRING");
            if (castToCharExp != null) {
               sc.setColumnExpression(castToCharExp);
            }
         }

         v_when_statement_list.addElement(ws.toAthenaSelect(to_sqs, from_sqs));
      }

      if (this.subquery != null) {
         cs.setSubQuery(this.subquery.toAthena());
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         if (needsCasting) {
            Vector colExp = this.elseStatement.getColumnExpression();
            Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "STRING");
            if (castToCharExp != null) {
               this.elseStatement.setColumnExpression(castToCharExp);
            }
         }

         cs.setElseStatement(this.elseStatement.toAthenaSelect(to_sqs, from_sqs));
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      if (to_sqs != null) {
         to_sqs.addCurrentIndexToIfFunctionList();
      }

      return cs;
   }

   public CaseStatement toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      cs.setCaseClause(this.caseClause);
      if (this.caseCondition != null) {
         cs.setCaseCondition(this.caseCondition.toSapHanaSelect(to_sqs, from_sqs));
      }

      boolean needsCasting = this.needsCastingForStringLiterals() || this.castToTextInsideIf && to_sqs != null && to_sqs.getBooleanValues("set.can.cast.all.to.text.columns");

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         WhenStatement ws = (WhenStatement)this.whenStatementList.elementAt(i);
         SelectColumn sc = ws.getThenStatement();
         if (sc != null && needsCasting) {
            Vector colExp = sc.getColumnExpression();
            Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "VARCHAR");
            if (castToCharExp != null) {
               sc.setColumnExpression(castToCharExp);
            }
         }

         v_when_statement_list.addElement(ws.toSapHanaSelect(to_sqs, from_sqs));
      }

      if (this.subquery != null) {
         cs.setSubQuery(this.subquery.toSapHana());
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         if (needsCasting) {
            Vector colExp = this.elseStatement.getColumnExpression();
            Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "VARCHAR");
            if (castToCharExp != null) {
               this.elseStatement.setColumnExpression(castToCharExp);
            }
         }

         cs.setElseStatement(this.elseStatement.toSapHanaSelect(to_sqs, from_sqs));
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      if (to_sqs != null) {
         to_sqs.addCurrentIndexToIfFunctionList();
      }

      return cs;
   }

   public CaseStatement toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      cs.setCaseClause(this.caseClause);
      if (this.caseCondition != null) {
         cs.setCaseCondition(this.caseCondition.toSqliteSelect(to_sqs, from_sqs));
      }

      boolean needsCasting = this.needsCastingForStringLiterals() || this.castToTextInsideIf && to_sqs != null && to_sqs.getBooleanValues("set.can.cast.all.to.text.columns");

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         WhenStatement ws = (WhenStatement)this.whenStatementList.elementAt(i);
         SelectColumn sc = ws.getThenStatement();
         if (sc != null && needsCasting) {
            Vector colExp = sc.getColumnExpression();
            Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "TEXT");
            if (castToCharExp != null) {
               sc.setColumnExpression(castToCharExp);
            }
         }

         v_when_statement_list.addElement(ws.toSqliteSelect(to_sqs, from_sqs));
      }

      if (this.subquery != null) {
         cs.setSubQuery(this.subquery.toSqlite());
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         if (needsCasting) {
            Vector colExp = this.elseStatement.getColumnExpression();
            Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "TEXT");
            if (castToCharExp != null) {
               this.elseStatement.setColumnExpression(castToCharExp);
            }
         }

         cs.setElseStatement(this.elseStatement.toSqliteSelect(to_sqs, from_sqs));
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      if (to_sqs != null) {
         to_sqs.addCurrentIndexToIfFunctionList();
      }

      return cs;
   }

   public CaseStatement toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector fcList = new Vector();
      FunctionCalls fcFinal = this.getFunctionCall();
      FunctionCalls fc = fcFinal;
      FunctionCalls reference = null;

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         WhenStatement ws = (WhenStatement)this.whenStatementList.elementAt(i);
         if (i != 0) {
            fc = reference;
         }

         SelectColumn sc1 = new SelectColumn();
         Vector colExp = new Vector();
         colExp.add(ws.getWhenCondition());
         sc1.setColumnExpression(colExp);
         SelectColumn sc2 = ws.getThenStatement();
         SelectColumn sc3 = new SelectColumn();
         Vector colExp2 = new Vector();
         if (i + 1 < this.whenStatementList.size()) {
            reference = this.getFunctionCall();
            colExp2.add(reference);
         } else if (this.elseStatement != null) {
            colExp2.addAll(this.elseStatement.getColumnExpression());
         } else {
            colExp2.add("NULL");
         }

         sc3.setColumnExpression(colExp2);
         Vector fnArgs = new Vector();
         fnArgs.add(sc1);
         fnArgs.add(sc2);
         fnArgs.add(sc3);
         fc.setFunctionArguments(fnArgs);
         fcList.add(fc);
      }

      this.setDecodeFunction(fcFinal.toExcelSelect(to_sqs, from_sqs).toString());
      return this;
   }

   public CaseStatement toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      boolean needsCasting = false;
      cs.setCaseClause(this.caseClause);
      if (this.caseCondition != null) {
         cs.setCaseCondition(this.caseCondition.toMsAccessJdbcSelect(to_sqs, from_sqs));
      }

      needsCasting = this.needsCastingForStringLiterals() || this.castToTextInsideIf && to_sqs != null && to_sqs.getBooleanValues("set.can.cast.all.to.text.columns");

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         WhenStatement ws = (WhenStatement)this.whenStatementList.elementAt(i);
         SelectColumn sc = ws.getThenStatement();
         if (sc != null && needsCasting) {
            Vector colExp = sc.getColumnExpression();
            Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "VARCHAR");
            if (castToCharExp != null) {
               sc.setColumnExpression(castToCharExp);
            }
         }

         v_when_statement_list.addElement(ws.toMsAccessJdbcSelect(to_sqs, from_sqs));
      }

      if (this.subquery != null) {
         cs.setSubQuery(this.subquery.toBigQuery());
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         if (needsCasting) {
            Vector colExp = this.elseStatement.getColumnExpression();
            Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "VARCHAR");
            if (castToCharExp != null) {
               this.elseStatement.setColumnExpression(castToCharExp);
            }
         }

         cs.setElseStatement(this.elseStatement.toMsAccessJdbcSelect(to_sqs, from_sqs));
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      if (to_sqs != null) {
         to_sqs.addCurrentIndexToIfFunctionList();
      }

      return cs;
   }

   public CaseStatement toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      boolean needsCasting = false;
      boolean isConditionalFormatting = false;
      boolean useCitext = from_sqs != null && from_sqs.getBooleanValues("can.use.citext.over.text");
      boolean castingFlagEnabled = this.castToTextInsideIf && to_sqs != null && from_sqs.getBooleanValues("set.can.cast.all.to.text.columns");
      boolean castingEnabledForStringLiteralsInWhereItem = from_sqs != null && from_sqs.getBooleanValues("can.cast.string.literal.to.text.in.where.item");
      boolean isSwitchCaseOperator = false;
      boolean castSwitchCaseOperatorToText = false;
      cs.setCaseClause(this.caseClause);
      Vector colExp;
      Vector whereItemVector;
      Vector castToCharExp;
      if (this.caseCondition != null) {
         if (castingEnabledForStringLiteralsInWhereItem) {
            colExp = this.caseCondition.getWhereItems();
            if (colExp != null && colExp.size() == 1 && colExp.get(0) instanceof WhereItem) {
               WhereItem wi = (WhereItem)colExp.get(0);
               if (wi.getLeftWhereExp() != null && wi.getRightWhereExp() == null) {
                  isSwitchCaseOperator = true;
               }
            }

            if (isSwitchCaseOperator && this.whenStatementList != null && !this.whenStatementList.isEmpty()) {
               Iterator var20 = this.whenStatementList.iterator();

               label182:
               while(true) {
                  do {
                     do {
                        do {
                           if (!var20.hasNext()) {
                              break label182;
                           }

                           Object obj = var20.next();
                           WhereExpression we = ((WhenStatement)obj).getWhenCondition();
                           whereItemVector = we.getWhereItems();
                        } while(whereItemVector == null);
                     } while(whereItemVector.size() != 1);
                  } while(!(whereItemVector.get(0) instanceof WhereItem));

                  WhereItem wim = (WhereItem)whereItemVector.get(0);
                  if (wim.getLeftWhereExp() == null || wim.getRightWhereExp() != null || wim.getLeftWhereExp().getColumnExpression() == null || wim.getLeftWhereExp().getColumnExpression().size() != 1 || !(wim.getLeftWhereExp().getColumnExpression().get(0) instanceof String) || !wim.getLeftWhereExp().getColumnExpression().get(0).toString().matches("'.*[a-zA-Z<>\"']+.*'")) {
                     castSwitchCaseOperatorToText = false;
                     break;
                  }

                  castSwitchCaseOperatorToText = true;
               }
            }

            if (isSwitchCaseOperator && castSwitchCaseOperatorToText) {
               castToCharExp = this.caseCondition.getWhereItems();
               if (castToCharExp != null && castToCharExp.size() == 1 && castToCharExp.get(0) instanceof WhereItem) {
                  WhereItem wi = (WhereItem)castToCharExp.get(0);
                  if (wi.getLeftWhereExp() != null && wi.getRightWhereExp() == null) {
                     WhereColumn wc = wi.getLeftWhereExp();
                     whereItemVector = wc.getColumnExpression();
                     Vector castToCharExp = FunctionCalls.castToCharClass(whereItemVector, "CHAR");
                     if (castToCharExp != null) {
                        wc.setColumnExpression(castToCharExp);
                     }
                  }
               }
            }
         }

         cs.setCaseCondition(this.caseCondition.toPostgreSQLSelect(to_sqs, from_sqs));
      }

      needsCasting = this.needsCastingForStringLiterals() || castingFlagEnabled || from_sqs != null && from_sqs.getBooleanValues("can.cast.string.functions.to.text.inside.if.function") && this.needsCastingForStringFunctions();
      if (useCitext) {
         Iterator var18 = this.whenStatementList.iterator();

         while(var18.hasNext()) {
            Object obj = var18.next();
            isConditionalFormatting = ((WhenStatement)obj).getThenStatement().getColumnExpression().size() == 1 && ((WhenStatement)obj).getThenStatement().getColumnExpression().get(0) instanceof String && ((String)((WhenStatement)obj).getThenStatement().getColumnExpression().get(0)).trim().matches("'#[A-z0-9]{6}'");
            if (!isConditionalFormatting) {
               break;
            }
         }

         if (isConditionalFormatting) {
            needsCasting = false;
         }
      }

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         WhenStatement ws = (WhenStatement)this.whenStatementList.elementAt(i);
         SelectColumn sc = ws.getThenStatement();
         if (sc != null) {
            if (needsCasting) {
               Vector colExp = sc.getColumnExpression();
               whereItemVector = FunctionCalls.castToCharClass(colExp, "CHAR");
               if (whereItemVector != null) {
                  sc.setColumnExpression(whereItemVector);
               }
            } else {
               sc.convertFromNumericArgToStringLiteralArg(castingFlagEnabled);
            }
         }

         v_when_statement_list.addElement(ws.toPostgreSQLSelect(to_sqs, from_sqs));
      }

      if (this.subquery != null) {
         cs.setSubQuery(this.subquery.toPostgreSQL());
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         if (needsCasting) {
            colExp = this.elseStatement.getColumnExpression();
            castToCharExp = FunctionCalls.castToCharClass(colExp, "CHAR");
            if (castToCharExp != null) {
               this.elseStatement.setColumnExpression(castToCharExp);
            }
         } else {
            this.elseStatement.convertFromNumericArgToStringLiteralArg(castingFlagEnabled);
         }

         cs.setElseStatement(this.elseStatement.toPostgreSQLSelect(to_sqs, from_sqs));
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      if (to_sqs != null) {
         to_sqs.addCurrentIndexToIfFunctionList();
      }

      return cs;
   }

   public CaseStatement toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      cs.setCaseClause(this.caseClause);
      if (this.caseCondition != null) {
         cs.setCaseCondition(this.caseCondition.toANSISelect(to_sqs, from_sqs));
      }

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         v_when_statement_list.addElement(((WhenStatement)this.whenStatementList.elementAt(i)).toANSISelect(to_sqs, from_sqs));
      }

      if (this.subquery != null) {
         cs.setSubQuery(this.subquery.toANSI());
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         cs.setElseStatement(this.elseStatement.toANSISelect(to_sqs, from_sqs));
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      return cs;
   }

   public CaseStatement toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      cs.setCaseClause(this.caseClause);
      if (this.caseCondition != null) {
         this.caseCondition.setCaseExpressionBool(true);
         WhereExpression caseExp = this.caseCondition.toTeradataSelect(to_sqs, from_sqs);
         if (this.caseCondition.getRownumClause() != null) {
            caseExp.setRownumClause(this.caseCondition.getRownumClause());
         }

         cs.setCaseCondition(caseExp);
      }

      boolean isDatePresent = false;
      int dateWhenStmtIdx = true;

      int i;
      for(i = 0; i < this.whenStatementList.size(); ++i) {
         SelectColumn sc1 = ((WhenStatement)this.whenStatementList.elementAt(i)).getThenStatement();

         for(int n = 0; n < sc1.getColumnExpression().size(); ++n) {
            Object obj = sc1.getColumnExpression().get(n);
            if (obj instanceof FunctionCalls) {
               FunctionCalls fcObj = (FunctionCalls)obj;
               if (fcObj.getFunctionName() != null) {
                  String fnName = fcObj.getFunctionName().getColumnName();
                  if (SwisSQLUtils.getFunctionReturnType(fnName, fcObj.getFunctionArguments()).equalsIgnoreCase("date")) {
                     isDatePresent = true;
                  }
               }
            }
         }
      }

      FunctionCalls fcObj;
      if (this.elseStatement != null) {
         for(i = 0; i < this.elseStatement.getColumnExpression().size(); ++i) {
            Object obj = this.elseStatement.getColumnExpression().get(i);
            if (obj instanceof FunctionCalls) {
               fcObj = (FunctionCalls)obj;
               if (fcObj.getFunctionName() != null) {
                  String fnName = fcObj.getFunctionName().getColumnName();
                  if (SwisSQLUtils.getFunctionReturnType(fnName, fcObj.getFunctionArguments()).equalsIgnoreCase("date")) {
                     isDatePresent = true;
                  }
               }
            }
         }
      }

      for(i = 0; i < this.whenStatementList.size(); ++i) {
         WhenStatement convertedWhenStmt = ((WhenStatement)this.whenStatementList.elementAt(i)).toTeradataSelect(to_sqs, from_sqs);
         if (isDatePresent) {
            fcObj = new FunctionCalls();
            TableColumn fnName = new TableColumn();
            fnName.setColumnName("CAST");
            fcObj.setFunctionName(fnName);
            Vector fnArgs = new Vector();
            fnArgs.add(convertedWhenStmt.getThenStatement());
            fcObj.setAsDatatype("AS");
            DateClass timestamp = new DateClass();
            timestamp.setDatatypeName("TIMESTAMP");
            timestamp.setSize("0");
            timestamp.setOpenBrace("(");
            timestamp.setClosedBrace(")");
            fnArgs.add(timestamp);
            fcObj.setFunctionArguments(fnArgs);
            SelectColumn newSelCol = new SelectColumn();
            Vector colExp = new Vector();
            colExp.add(fcObj);
            newSelCol.setColumnExpression(colExp);
            convertedWhenStmt.setThenStatement(newSelCol);
            v_when_statement_list.addElement(convertedWhenStmt);
         } else {
            v_when_statement_list.addElement(convertedWhenStmt);
         }
      }

      if (this.subquery != null) {
         cs.setSubQuery(this.subquery.toTeradata());
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         SelectColumn convertedElseStatement = this.elseStatement.toTeradataSelect(to_sqs, from_sqs);
         if (isDatePresent) {
            FunctionCalls caseFunc = new FunctionCalls();
            TableColumn fnName = new TableColumn();
            fnName.setColumnName("CAST");
            caseFunc.setFunctionName(fnName);
            Vector fnArgs = new Vector();
            fnArgs.add(convertedElseStatement);
            caseFunc.setAsDatatype("AS");
            DateClass timestamp = new DateClass();
            timestamp.setDatatypeName("TIMESTAMP");
            timestamp.setSize("0");
            timestamp.setOpenBrace("(");
            timestamp.setClosedBrace(")");
            fnArgs.add(timestamp);
            caseFunc.setFunctionArguments(fnArgs);
            SelectColumn newSelCol = new SelectColumn();
            Vector colExp = new Vector();
            colExp.add(caseFunc);
            newSelCol.setColumnExpression(colExp);
            cs.setElseStatement(newSelCol);
         } else {
            cs.setElseStatement(convertedElseStatement);
         }
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      return cs;
   }

   public CaseStatement toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      cs.setCaseClause(this.caseClause);
      if (this.caseCondition != null) {
         cs.setCaseCondition(this.caseCondition.toDB2Select(to_sqs, from_sqs));
      }

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         v_when_statement_list.addElement(((WhenStatement)this.whenStatementList.elementAt(i)).toDB2Select(to_sqs, from_sqs));
      }

      if (this.subquery != null) {
         cs.setSubQuery(this.subquery.toDB2());
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         cs.setElseStatement(this.elseStatement.toDB2Select(to_sqs, from_sqs));
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      return cs;
   }

   public CaseStatement toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      cs.setCaseClause(this.caseClause);
      cs.setCaseCondition(this.caseCondition);
      if (this.caseCondition != null) {
         cs.setCaseCondition(this.caseCondition.toMSSQLServerSelect(to_sqs, from_sqs));
      }

      if (this.subquery != null) {
         cs.setSubQuery(this.subquery.toMSSQLServer());
      }

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         v_when_statement_list.addElement(((WhenStatement)this.whenStatementList.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         cs.setElseStatement(this.elseStatement.toMSSQLServerSelect(to_sqs, from_sqs));
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      return cs;
   }

   public CaseStatement toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      boolean needsCasting = false;
      cs.setCaseClause(this.caseClause);
      if (this.caseCondition != null) {
         cs.setCaseCondition(this.caseCondition.toSnowflakeSelect(to_sqs, from_sqs));
      }

      needsCasting = this.needsCastingForStringLiterals() || this.castToTextInsideIf && to_sqs != null && to_sqs.getBooleanValues("set.can.cast.all.to.text.columns");

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         WhenStatement ws = (WhenStatement)this.whenStatementList.elementAt(i);
         SelectColumn sc = ws.getThenStatement();
         if (sc != null && needsCasting) {
            Vector colExp = sc.getColumnExpression();
            Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "CHAR");
            if (castToCharExp != null) {
               sc.setColumnExpression(castToCharExp);
            }
         }

         v_when_statement_list.addElement(ws.toSnowflakeSelect(to_sqs, from_sqs));
      }

      if (this.subquery != null) {
         cs.setSubQuery(this.subquery.toSnowflake());
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         if (needsCasting) {
            Vector colExp = this.elseStatement.getColumnExpression();
            Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "CHAR");
            if (castToCharExp != null) {
               this.elseStatement.setColumnExpression(castToCharExp);
            }
         }

         cs.setElseStatement(this.elseStatement.toSnowflakeSelect(to_sqs, from_sqs));
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      if (to_sqs != null) {
         to_sqs.addCurrentIndexToIfFunctionList();
      }

      return cs;
   }

   public CaseStatement toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      cs.setCaseClause(this.caseClause);
      cs.setObjectContext(this.context);
      cs.setCaseCondition(this.caseCondition);
      if (this.caseCondition != null) {
         cs.setCaseCondition(this.caseCondition.toSybaseSelect(to_sqs, from_sqs));
      }

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         v_when_statement_list.addElement(((WhenStatement)this.whenStatementList.elementAt(i)).toSybaseSelect(to_sqs, from_sqs));
      }

      if (this.subquery != null) {
         cs.setSubQuery(this.subquery.toSybase());
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         cs.setElseStatement(this.elseStatement.toSybaseSelect(to_sqs, from_sqs));
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      return cs;
   }

   public CaseStatement toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      if (SwisSQLAPI.convertCaseToDecode && !isdenodo) {
         StringBuffer sb = new StringBuffer();
         if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
         }

         sb.append("DECODE(");
         if (this.caseCondition == null) {
            int j;
            if (this.subquery == null) {
               sb.append("1 ,");

               for(int i = 0; i < this.whenStatementList.size(); ++i) {
                  WhenStatement when_statement = ((WhenStatement)this.whenStatementList.elementAt(i)).toOracleSelect(to_sqs, from_sqs);
                  Vector whereItemList = new Vector();
                  Vector operator = new Vector();
                  this.curr_whereItem = when_statement.getWhenCondition().getWhereItems();
                  this.loadWhereItemsOperators(whereItemList, operator);
                  sb.append(" DECODE( " + whereItemList.size() + " ,");

                  for(j = 0; j < whereItemList.size(); ++j) {
                     sb.append(this.convertToBooleanFunction((WhereItem)whereItemList.get(j)));
                     if (j != whereItemList.size() - 1) {
                        sb.append(" + ");
                     }
                  }

                  sb.append(" , 1 , 0 ) ");
                  sb.append(",");
                  sb.append(when_statement.getThenStatement().toString());
                  sb.append(",");
               }
            } else {
               SelectColumn caseSelectColumn = new SelectColumn();
               Vector items = new Vector();
               items.add(this.subquery);
               caseSelectColumn.setColumnExpression(items);
               SelectColumn decodeCondition = caseSelectColumn.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               String singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
               SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
               sb.append(decodeCondition.toString());
               SelectQueryStatement.singleQueryConvertedToMultipleQueryList = singleQueryConvertedToMultipleQueryList;
               sb.append(",");

               for(j = 0; j < this.whenStatementList.size(); ++j) {
                  WhenStatement when_statement = ((WhenStatement)this.whenStatementList.elementAt(j)).toOracleSelect(to_sqs, from_sqs);
                  new Vector();
                  new Vector();
                  this.curr_whereItem = when_statement.getWhenCondition().getWhereItems();
                  if (this.curr_whereItem.get(0) != null) {
                     sb.append(this.curr_whereItem.get(0).toString());
                  }

                  sb.append(",");
                  sb.append(when_statement.getThenStatement().toString());
                  sb.append(",");
               }
            }
         } else {
            WhereExpression decodeCondition = this.caseCondition.toOracleSelect(to_sqs, from_sqs);
            String singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
            sb.append(decodeCondition.toString());
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = singleQueryConvertedToMultipleQueryList;
            sb.append(",");

            for(int i = 0; i < this.whenStatementList.size(); ++i) {
               WhenStatement when_statement = ((WhenStatement)this.whenStatementList.elementAt(i)).toOracleSelect(to_sqs, from_sqs);
               new Vector();
               new Vector();
               this.curr_whereItem = when_statement.getWhenCondition().getWhereItems();
               if (this.curr_whereItem.get(0) != null) {
                  sb.append(this.curr_whereItem.get(0).toString());
               }

               sb.append(",");
               sb.append(when_statement.getThenStatement().toString());
               sb.append(",");
            }
         }

         if (this.elseStatement != null) {
            sb.append(this.elseStatement.toOracleSelect(to_sqs, from_sqs));
         } else {
            sb.deleteCharAt(sb.toString().lastIndexOf(44));
         }

         sb.append(")");
         this.decodeFunction = sb.toString();
         return this;
      } else {
         CaseStatement cs = new CaseStatement();
         Vector v_when_statement_list = new Vector();
         cs.setCaseClause(this.caseClause);
         cs.setCommentClass(this.commentObj);
         if (this.caseCondition != null) {
            cs.setCaseCondition(this.caseCondition.toOracleSelect(to_sqs, from_sqs));
         }

         for(int i = 0; i < this.whenStatementList.size(); ++i) {
            v_when_statement_list.addElement(((WhenStatement)this.whenStatementList.elementAt(i)).toOracleSelect(to_sqs, from_sqs));
         }

         if (this.subquery != null) {
            cs.setSubQuery(this.subquery.toOracle());
         }

         cs.setWhenStatementList(v_when_statement_list);
         cs.setElseClause(this.elseClause);
         if (this.elseStatement != null) {
            cs.setElseStatement(this.elseStatement.toOracleSelect(to_sqs, from_sqs));
         }

         cs.setEndClause(this.endClause);
         cs.setDecodeFunction(this.decodeFunction);
         return cs;
      }
   }

   public CaseStatement toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      cs.setCaseClause(this.caseClause);
      if (this.caseCondition != null) {
         cs.setCaseCondition(this.caseCondition.toNetezzaSelect(to_sqs, from_sqs));
      }

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         v_when_statement_list.addElement(((WhenStatement)this.whenStatementList.elementAt(i)).toNetezzaSelect(to_sqs, from_sqs));
      }

      if (this.subquery != null) {
         cs.setSubQuery(this.subquery.toNetezza());
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         cs.setElseStatement(this.elseStatement.toNetezzaSelect(to_sqs, from_sqs));
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      return cs;
   }

   public void loadWhereItemsOperators(Vector whereItemList, Vector operatorList) {
      for(int i = 0; i < this.curr_whereItem.size(); ++i) {
         Object obj = this.curr_whereItem.get(i);
         if (obj instanceof WhereItem) {
            whereItemList.add(obj);
         } else if (obj instanceof WhereExpression) {
            WhereExpression we = (WhereExpression)obj;
            we.loadWhereItemsOperators(whereItemList, operatorList);
         }
      }

   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.decodeFunction != null) {
         sb.append(this.decodeFunction);
      } else {
         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);
         int j;
         if (this.commentObj != null) {
            sb.append("\n");

            for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }

            sb.append(this.commentObj.toString().trim());
         }

         sb.append("\n");

         for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
            sb.append("\t");
         }

         sb.append(this.caseClause);
         if (this.caseCondition != null) {
            this.caseCondition.setObjectContext(this.context);
            sb.append(" " + this.caseCondition.toString());
         }

         if (this.subquery != null) {
            this.subquery.setObjectContext(this.context);
            sb.append("\n");

            for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }

            sb.append("(");
            sb.append(" " + this.subquery.toString());
            sb.append("\n");

            for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }

            sb.append(")");
         }

         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);

         for(j = 0; j < this.whenStatementList.size(); ++j) {
            ((WhenStatement)this.whenStatementList.elementAt(j)).setObjectContext(this.context);
            sb.append("\n");

            for(int j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }

            sb.append(" " + this.whenStatementList.elementAt(j).toString());
         }

         if (this.elseClause != null) {
            sb.append("\n");

            for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }

            sb.append(" " + this.elseClause);
            this.elseStatement.setObjectContext(this.context);
            sb.append(" " + this.elseStatement.toString());
         }

         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
         sb.append("\n");

         for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
            sb.append("\t");
         }

         sb.append(" " + this.endClause.toString());
         if (this.commentObjAfterToken != null) {
            sb.append(this.commentObjAfterToken.toString().trim());
         }

         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
      }

      return sb.toString();
   }

   public CaseStatement toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();
      Vector v_when_statement_list = new Vector();
      cs.setCaseClause(this.caseClause);
      if (this.caseCondition != null) {
         cs.setCaseCondition(this.caseCondition.toInformixSelect(to_sqs, from_sqs));
      }

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         v_when_statement_list.addElement(((WhenStatement)this.whenStatementList.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
      }

      if (this.subquery != null) {
         cs.setSubQuery(this.subquery.toInformix());
      }

      cs.setWhenStatementList(v_when_statement_list);
      cs.setElseClause(this.elseClause);
      if (this.elseStatement != null) {
         cs.setElseStatement(this.elseStatement.toInformixSelect(to_sqs, from_sqs));
      }

      cs.setEndClause(this.endClause);
      cs.setDecodeFunction(this.decodeFunction);
      return cs;
   }

   public void replaceRownumTableColumn(Object newColumn) throws ConvertException {
      if (this.caseCondition != null) {
         this.caseCondition.replaceRownumTableColumn(newColumn);
      }

      for(int i = 0; i < this.whenStatementList.size(); ++i) {
         WhenStatement whenSt = (WhenStatement)this.whenStatementList.get(i);
         if (whenSt.getThenStatement() != null) {
            whenSt.getThenStatement().replaceRownumTableColumn(newColumn);
         }

         if (whenSt.getWhenCondition() != null) {
            whenSt.getWhenCondition().replaceRownumTableColumn(newColumn);
         }
      }

      if (this.elseStatement != null) {
         this.elseStatement.replaceRownumTableColumn(newColumn);
      }

   }

   public CaseStatement toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement cs = new CaseStatement();

      try {
         Vector v_when_statement_list = new Vector();
         boolean needsCasting = false;
         cs.setCaseClause(this.caseClause);
         if (this.caseCondition != null) {
            cs.setCaseCondition(this.caseCondition.toVectorWiseSelect(to_sqs, from_sqs));
         }

         needsCasting = this.needsCastingForStringLiterals();

         for(int i = 0; i < this.whenStatementList.size(); ++i) {
            WhenStatement ws = (WhenStatement)this.whenStatementList.elementAt(i);
            SelectColumn sc = ws.getThenStatement();
            if (sc != null && needsCasting) {
               Vector colExp = sc.getColumnExpression();
               Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "CHAR");
               if (castToCharExp != null) {
                  sc.setColumnExpression(castToCharExp);
               }
            }

            v_when_statement_list.addElement(ws.toVectorWiseSelect(to_sqs, from_sqs));
         }

         cs.setWhenStatementList(v_when_statement_list);
         cs.setElseClause(this.elseClause);
         if (this.elseStatement != null) {
            if (needsCasting) {
               Vector colExp = this.elseStatement.getColumnExpression();
               Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "CHAR");
               if (castToCharExp != null) {
                  this.elseStatement.setColumnExpression(castToCharExp);
               }
            }

            cs.setElseStatement(this.elseStatement.toVectorWiseSelect(to_sqs, from_sqs));
         }

         cs.setEndClause(this.endClause);
         cs.setDecodeFunction(this.decodeFunction);
      } catch (Exception var11) {
         System.err.println("Exception in Query : " + from_sqs);
         var11.printStackTrace();
      }

      return cs;
   }

   public CaseStatement toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement caseStmtConv = new CaseStatement();
      if (this.caseClause != null) {
         caseStmtConv.setCaseClause(this.caseClause);
      }

      if (this.caseCondition != null) {
         caseStmtConv.setCaseCondition(this.caseCondition.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.subquery != null) {
         this.subquery.setPropAndHandlerFromSQS(from_sqs);
         caseStmtConv.setSubQuery(this.subquery.toReplaceTblCol());
      }

      if (this.whenStatementList != null) {
         Vector whenStmtListConv = new Vector();
         int i = 0;

         for(int whenStmtListSize = this.whenStatementList.size(); i < whenStmtListSize; ++i) {
            whenStmtListConv.addElement(((WhenStatement)this.whenStatementList.get(i)).toReplaceTblCol(to_sqs, from_sqs));
         }

         caseStmtConv.setWhenStatementList(whenStmtListConv);
      }

      if (this.elseClause != null) {
         caseStmtConv.setElseClause(this.elseClause);
      }

      if (this.elseStatement != null) {
         caseStmtConv.setElseStatement(this.elseStatement.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.endClause != null) {
         caseStmtConv.setEndClause(this.endClause);
      }

      if (this.commentObj != null) {
         caseStmtConv.setCommentClass(this.commentObj);
      }

      if (this.commentObjAfterToken != null) {
         caseStmtConv.setCommentClassAfterToken(this.commentObjAfterToken);
      }

      return caseStmtConv;
   }

   public FunctionCalls getFunctionCall() {
      FunctionCalls fc = new FunctionCalls();
      TableColumn tc = new TableColumn();
      tc.setColumnName("IF");
      fc.setFunctionName(tc);
      return fc;
   }

   public FunctionCalls convertToFunctionCall(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      FunctionCalls fcFinal = null;
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());

      try {
         if (from_sqs != null && from_sqs.canUseIFFunctionForPGCaseWhenExp() && !isPostgreLiveDbs && this.caseCondition == null && this.subquery == null) {
            Vector fcList = new Vector();
            fcFinal = this.getFunctionCall();
            FunctionCalls fc = fcFinal;
            FunctionCalls reference = null;

            for(int i = 0; i < this.whenStatementList.size(); ++i) {
               WhenStatement ws = (WhenStatement)this.whenStatementList.elementAt(i);
               if (i != 0) {
                  fc = reference;
               }

               SelectColumn sc1 = new SelectColumn();
               Vector colExp = new Vector();
               colExp.add(ws.getWhenCondition());
               sc1.setColumnExpression(colExp);
               SelectColumn sc2 = ws.getThenStatement();
               SelectColumn sc3 = new SelectColumn();
               Vector colExp2 = new Vector();
               if (i + 1 < this.whenStatementList.size()) {
                  reference = this.getFunctionCall();
                  colExp2.add(reference);
               } else if (this.elseStatement != null) {
                  colExp2.addAll(this.elseStatement.getColumnExpression());
               } else {
                  colExp2.add("NULL");
               }

               sc3.setColumnExpression(colExp2);
               Vector fnArgs = new Vector();
               fnArgs.add(sc1);
               fnArgs.add(sc2);
               fnArgs.add(sc3);
               fc.setFunctionArguments(fnArgs);
               fcList.add(fc);
            }
         }
      } catch (Exception var16) {
         fcFinal = null;
      }

      return fcFinal;
   }

   public boolean needsCastingForStringFunctions() {
      boolean needsCasting = false;

      for(int j = 0; j < this.whenStatementList.size(); ++j) {
         SelectColumn sc = ((WhenStatement)this.whenStatementList.elementAt(j)).getThenStatement();
         if (sc.needsCastingForStringFunctions()) {
            needsCasting = true;
            break;
         }
      }

      if (this.elseStatement != null && !needsCasting) {
         needsCasting = this.elseStatement.needsCastingForStringFunctions();
      }

      return needsCasting;
   }

   public boolean needsCastingForStringLiterals() {
      boolean needsCasting = false;

      for(int j = 0; j < this.whenStatementList.size(); ++j) {
         SelectColumn sc = ((WhenStatement)this.whenStatementList.elementAt(j)).getThenStatement();
         if (sc.needsCastingForStringLiterals(true)) {
            needsCasting = true;
            break;
         }
      }

      if (this.elseStatement != null && !needsCasting) {
         needsCasting = this.elseStatement.needsCastingForStringLiterals(true);
      }

      return needsCasting;
   }
}
