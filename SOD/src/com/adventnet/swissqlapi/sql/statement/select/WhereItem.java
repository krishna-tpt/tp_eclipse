package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.functions.misc.cast;
import com.adventnet.swissqlapi.sql.functions.misc.ifnull;
import com.adventnet.swissqlapi.sql.parser.Token;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class WhereItem {
   private String openBraces = new String("");
   private String closeBraces = new String("");
   private WhereColumn leftWhereExp;
   private WhereColumn rightWhereExp;
   private String LeftJoin;
   private String RightJoin;
   private SelectQueryStatement rightWhereSubQuery;
   private WhereColumn rightWhereSubQueryExp;
   private String beginOperator;
   private UserObjectContext context = null;
   private boolean movedToFromClause = false;
   private boolean isContainsFunction = false;
   private boolean isNullSafeEqualsOperator = false;
   private String operator1;
   private String operator;
   private String operator2;
   private String operator3;
   private RownumClause rownumClause;
   private ArrayList fromTableList;
   private String stmtTableName;
   private WhereExpression lnnvlWhereExp;
   private CaseStatement casestmtFromLNNVLClause;
   private WhereItem teradataSysCalendarWI;
   private boolean is_Case_Expression = false;
   private Vector regExp;
   private CommentClass commentObj;
   private CommentClass commentObjAfterToken;
   private int whereItemNestedIfCount;
   private Long instanceDatatype;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setLeftWhereExp(WhereColumn lwe) {
      this.leftWhereExp = lwe;
      if (this.leftWhereExp != null) {
         this.leftWhereExp.setLHSExpr(true);
      }

   }

   public void setMovedToFromClause(boolean b) {
      this.movedToFromClause = b;
   }

   public void setOpenBrace(String s_ob) {
      this.openBraces = s_ob;
   }

   public void setCloseBrace(String s_cb) {
      this.closeBraces = s_cb;
   }

   public void setRightWhereExp(WhereColumn rwe) {
      this.rightWhereExp = rwe;
   }

   public void setOperator(String opr) {
      this.operator = opr;
   }

   public void setOperator1(String opr) {
      this.operator1 = opr;
   }

   public void setOperator2(String opr) {
      this.operator2 = opr;
   }

   public void setOperator3(String s_opr) {
      this.operator3 = s_opr;
   }

   public void setBeginOperator(String begin) {
      this.beginOperator = begin;
   }

   public void setRightWhereSubQuery(SelectQueryStatement qs) {
      this.rightWhereSubQuery = qs;
   }

   public void setRightWhereSubQueryExp(WhereColumn wc) {
      this.rightWhereSubQueryExp = wc;
   }

   public void setFromTableList(ArrayList fromTableList) {
      this.fromTableList = fromTableList;
   }

   public void setRightJoin(String s_rj) {
      this.RightJoin = s_rj;
   }

   public void setLeftJoin(String s_lj) {
      this.LeftJoin = s_lj;
   }

   public void setStmtTableName(String stmtTableName) {
      this.stmtTableName = stmtTableName;
   }

   public void setWhereExpForLNNVL(WhereExpression we) {
      this.lnnvlWhereExp = we;
   }

   public void setCaseStatementForLNNVLFunc(CaseStatement cs) {
      this.casestmtFromLNNVLClause = cs;
   }

   public void setTeradataSysCalendarWhereItem(WhereItem tdWI) {
      this.teradataSysCalendarWI = tdWI;
   }

   public void setCaseExpressionBool(boolean boolVal) {
      this.is_Case_Expression = boolVal;
   }

   public void setRegExp(Vector exp) {
      this.regExp = exp;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public void setCommentClassAfterToken(CommentClass commentObj) {
      this.commentObjAfterToken = commentObj;
   }

   public void setWhereItemNestedIfCount(int val) {
      this.whereItemNestedIfCount = val;
   }

   public void setInstanceDatatype(Long instanceDatatype) {
      this.instanceDatatype = instanceDatatype;
   }

   public int getWhereItemNestedIfCount() {
      return this.whereItemNestedIfCount;
   }

   public CaseStatement getCaseStatementForLNNVLFunc() {
      return this.casestmtFromLNNVLClause;
   }

   public WhereExpression getWhereExpForLNNVL() {
      return this.lnnvlWhereExp;
   }

   public String getRightJoin() {
      return this.RightJoin;
   }

   public String getLeftJoin() {
      return this.LeftJoin;
   }

   public String getBeginOperator() {
      return this.beginOperator;
   }

   public WhereColumn getLeftWhereExp() {
      return this.leftWhereExp;
   }

   public WhereColumn getRightWhereExp() {
      return this.rightWhereExp;
   }

   public SelectQueryStatement getRightWhereSubQuery() {
      return this.rightWhereSubQuery;
   }

   public WhereColumn getRightWhereSubQueryExp() {
      return this.rightWhereSubQueryExp;
   }

   public String getOperator() {
      return this.operator;
   }

   public String getOperator1() {
      return this.operator1;
   }

   public String getOperator2() {
      return this.operator2;
   }

   public String getOperator3() {
      return this.operator3;
   }

   public void setRownumClause(RownumClause rc) {
      this.rownumClause = rc;
   }

   public String getOpenBrace() {
      return this.openBraces;
   }

   public String getCloseBrace() {
      return this.closeBraces;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public CommentClass getCommentClassAfterToken() {
      return this.commentObjAfterToken;
   }

   public void removeBraces() {
      if (this.openBraces != null && this.openBraces.length() > 0) {
         this.openBraces = this.openBraces.substring(1);
         if (this.closeBraces != null && this.closeBraces.length() > 0) {
            this.closeBraces = this.closeBraces.substring(1);
         }
      }

   }

   public Vector getRegExp() {
      return this.regExp;
   }

   public RownumClause getRownumClause() {
      return this.rownumClause;
   }

   public boolean getMovedToFromClause() {
      return this.movedToFromClause;
   }

   public WhereItem getTeradataSysCalendarWhereItem() {
      return this.teradataSysCalendarWI;
   }

   public Long getInstanceDatatype() {
      return this.instanceDatatype;
   }

   public boolean isNullSafeEqualsOperator() {
      return this.isNullSafeEqualsOperator;
   }

   public WhereItem toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         if (this.commentObj != null) {
            wi.setCommentClass(this.commentObj);
         }

         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         this.modifyWhereItemsForEqualAndNotEqualsOperationWithEmptyStringOnRHS();
         if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toMySQLSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toMySQLSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereSubQuery != null) {
            SelectStatement WhereSelect = this.rightWhereSubQuery.getSelectStatement();
            WhereSelect.setAliasNameVersion(2);
            this.rightWhereSubQuery.setMySqlLiveFlag(from_sqs.isMySqlLive());
            this.rightWhereSubQuery.setMongoDbFlag(from_sqs.isMongoDb());
            this.rightWhereSubQuery.setSQLDialect(from_sqs.getSQLDialect());
            this.rightWhereSubQuery.setValidationHandler(from_sqs.getValidationHandler());
            this.rightWhereSubQuery.setIsQtNewFlow(from_sqs.getIsQtNewFlow());
            this.rightWhereSubQuery.setQueryConvDataHandler(from_sqs.getQueryConvDataHandler());
            this.rightWhereSubQuery.setQueryConversionPropHandler(from_sqs.getQueryConvPropHandler());
            this.rightWhereSubQuery.setInstanceDataTypeHandler(from_sqs.getinstanceDataTypeHandler());
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toMySQL());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toMySQLSelect(to_sqs, from_sqs));
            }
         }

         Vector newColumnExpression;
         WhereColumn whereColumn;
         if (this.operator != null) {
            if (this.operator.equalsIgnoreCase("^=")) {
               wi.setOperator("<>");
            } else if (this.operator.equalsIgnoreCase("!>")) {
               wi.setOperator("<=");
            } else if (this.operator.equalsIgnoreCase("!<")) {
               wi.setOperator(">=");
            } else {
               Vector v_nsce;
               String ColumnExpressionAsString;
               if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                  wi.setOperator("LIKE");
                  if (wi.getRightWhereSubQuery() != null) {
                     throw new ConvertException("Conversion failure.. Subquery can't be converted");
                  }

                  whereColumn = wi.getRightWhereExp();
                  v_nsce = whereColumn.getColumnExpression();
                  newColumnExpression = new Vector();
                  if (v_nsce != null) {
                     if (v_nsce.size() != 1) {
                        throw new ConvertException("Conversion failure..Expressions can't be converted");
                     }

                     ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                     if (ColumnExpressionAsString.charAt(0) == '\'') {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('\'', ' ').trim();
                        ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                     } else if (ColumnExpressionAsString.charAt(0) == '"') {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('"', ' ').trim();
                        ColumnExpressionAsString = "\"%" + ColumnExpressionAsString + "%\"";
                     } else {
                        ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                     }

                     newColumnExpression.addElement(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(newColumnExpression);
                  }

                  wi.setRightWhereExp(whereColumn);
               } else if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                  if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null) {
                     whereColumn = wi.getRightWhereExp();
                     v_nsce = new Vector();
                     newColumnExpression = whereColumn.getColumnExpression();
                     if (newColumnExpression != null && newColumnExpression.size() > 0 && !(newColumnExpression.elementAt(0) instanceof String)) {
                        v_nsce.addElement("(");

                        for(int i = 0; i < newColumnExpression.size(); ++i) {
                           v_nsce.addElement(newColumnExpression.elementAt(i));
                        }

                        v_nsce.addElement(")");
                        whereColumn.setColumnExpression(v_nsce);
                     }

                     wi.setOperator(this.operator);
                  } else if (from_sqs != null && from_sqs.isHyperSql() && this.operator.equalsIgnoreCase("<=>")) {
                     wi.setOperator("IS NOT DISTINCT FROM");
                  } else {
                     wi.setOperator(this.operator);
                  }
               } else {
                  if (this.operator.equalsIgnoreCase("MATCHES")) {
                     wi.setOperator("LIKE");
                  } else {
                     wi.setOperator("NOT LIKE");
                  }

                  whereColumn = wi.getRightWhereExp();
                  v_nsce = whereColumn.getColumnExpression();
                  newColumnExpression = new Vector();
                  if (v_nsce != null) {
                     ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                     if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                     }

                     if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                     }

                     newColumnExpression.add(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(newColumnExpression);
                  }
               }
            }
         }

         if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            whereColumn = wi.getRightWhereExp();
            WhereColumn l_wc = wi.getLeftWhereExp();
            if (whereColumn != null && whereColumn.getColumnExpression().size() != 1) {
               newColumnExpression = new Vector();
               Vector v_nrsce = new Vector();
               Vector v_lsce = l_wc.getColumnExpression();
               Vector v_rsce = whereColumn.getColumnExpression();
               Vector v_removedcomma_right_column_exp = new Vector();
               Vector v_removedcomma_left_column_exp = new Vector();

               int i;
               String s_ce;
               for(i = 0; i < v_lsce.size(); ++i) {
                  if (v_lsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_lsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                  }
               }

               for(i = 0; i < v_rsce.size(); ++i) {
                  if (v_rsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_rsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                  }
               }

               newColumnExpression.addElement(v_removedcomma_left_column_exp.elementAt(0));
               v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
               l_wc.setColumnExpression(newColumnExpression);
               whereColumn.setColumnExpression(v_nrsce);

               for(i = 1; i < v_removedcomma_right_column_exp.size(); ++i) {
                  WhereItem n_wi = new WhereItem();
                  WhereColumn l_nwc = new WhereColumn();
                  WhereColumn r_nwc = new WhereColumn();
                  Vector v_lnsc = new Vector();
                  Vector v_rnsc = new Vector();
                  v_lnsc.addElement(v_removedcomma_left_column_exp.elementAt(0));
                  v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(i));
                  l_nwc.setColumnExpression(v_lnsc);
                  r_nwc.setColumnExpression(v_rnsc);
                  n_wi.setLeftWhereExp(l_nwc);
                  n_wi.setRightWhereExp(r_nwc);
                  n_wi.setOperator(this.operator);
                  if (i == v_lsce.size() - 1) {
                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  } else {
                     if (this.operator2.equalsIgnoreCase("ALL")) {
                        from_sqs.getWhereExpression().addOperator("AND");
                     } else {
                        from_sqs.getWhereExpression().addOperator("OR");
                     }

                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  }
               }
            }
         }

         if (this.commentObjAfterToken != null) {
            wi.setCommentClassAfterToken(this.commentObjAfterToken);
         }

         if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.LeftJoin != null) {
            wi.setLeftJoin((String)null);
         }

         if (this.RightJoin != null) {
            wi.setRightJoin((String)null);
         }

         int leftWhereExpIfcount = 0;
         int rightWhereExpIfcount = 0;
         WhereColumn wc;
         if (wi.getLeftWhereExp() instanceof WhereColumn) {
            wc = wi.getLeftWhereExp();
            leftWhereExpIfcount = GeneralUtil.checkVectorandGetMaxNestedIfCount(wc.getColumnExpression(), 2);
         }

         if (wi.getRightWhereExp() instanceof WhereColumn) {
            wc = wi.getLeftWhereExp();
            rightWhereExpIfcount = GeneralUtil.checkVectorandGetMaxNestedIfCount(wc.getColumnExpression(), 2);
         }

         if (leftWhereExpIfcount >= rightWhereExpIfcount) {
            wi.setWhereItemNestedIfCount(leftWhereExpIfcount);
         } else if (rightWhereExpIfcount > leftWhereExpIfcount) {
            wi.setWhereItemNestedIfCount(rightWhereExpIfcount);
         }

         return wi;
      }
   }

   public boolean convertRownumToLimitClause(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.leftWhereExp != null) {
         Vector v_ce = this.leftWhereExp.getColumnExpression();
         if (v_ce == null || v_ce.size() != 1 || !(v_ce.elementAt(0) instanceof TableColumn)) {
            return false;
         }

         TableColumn tc = (TableColumn)v_ce.elementAt(0);
         if (!tc.getColumnName().equalsIgnoreCase("ROWNUM")) {
            return false;
         }
      }

      if (this.operator != null && this.operator.equals("<")) {
         if (this.rightWhereExp != null) {
            if (from_sqs.getLimitClause() != null) {
               throw new ConvertException();
            } else {
               LimitClause lc = new LimitClause();
               lc.setLimitClause("LIMIT");
               lc.setLimitValue(this.rightWhereExp.toMySQLSelect(to_sqs, from_sqs).toString());
               to_sqs.setLimitClause(lc);
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public WhereItem toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         this.modifyWhereItemsForLikeOperatorWithoutConstantOnRHS(false);
         this.modifyWhereItemsForEqualAndNotEqualsOperationWithEmptyStringOnRHS();
         if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toSnowflakeSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toSnowflakeSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereSubQuery != null) {
            this.rightWhereSubQuery.setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toSnowflake());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toSnowflakeSelect(to_sqs, from_sqs));
            }
         }

         WhereColumn whereColumn;
         Vector newColumnExpression;
         if (this.operator != null) {
            this.modifyWhereExpForLikeOperator(wi, this.operator, "TEXT");
            if (this.operator.equalsIgnoreCase("^=")) {
               wi.setOperator("<>");
            } else if (this.operator.equalsIgnoreCase("!>")) {
               wi.setOperator("<=");
            } else if (this.operator.equalsIgnoreCase("!<")) {
               wi.setOperator(">=");
            } else if (this.operator.equalsIgnoreCase("<=>")) {
               wi.setOperator("=");
               wi.isNullSafeEqualsOperator = true;
            } else {
               Vector v_nsce;
               if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                  if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null) {
                     whereColumn = wi.getRightWhereExp();
                     v_nsce = new Vector();
                     newColumnExpression = whereColumn.getColumnExpression();
                     if (newColumnExpression != null && newColumnExpression.size() > 0 && !(newColumnExpression.elementAt(0) instanceof String)) {
                        v_nsce.addElement("(");

                        for(int i = 0; i < newColumnExpression.size(); ++i) {
                           v_nsce.addElement(newColumnExpression.elementAt(i));
                        }

                        v_nsce.addElement(")");
                        whereColumn.setColumnExpression(v_nsce);
                     }

                     wi.setOperator(this.operator);
                  } else if (this.operator.trim().equalsIgnoreCase("LNNVl") && this.lnnvlWhereExp != null) {
                     WhereExpression we1 = this.lnnvlWhereExp.toSnowflakeSelect(to_sqs, from_sqs);
                     this.convertLNNVLtoStatement(we1);
                     wi.setCaseStatementForLNNVLFunc(this.casestmtFromLNNVLClause);
                  } else if (this.operator.trim().equalsIgnoreCase("REGEXP_LIKE") && this.regExp != null) {
                     wi.setOperator("REGEXP_LIKE");
                     Vector newExp = new Vector();

                     for(int i = 0; i < this.regExp.size(); ++i) {
                        if (this.regExp.get(i) instanceof SelectColumn) {
                           SelectColumn sc1 = (SelectColumn)this.regExp.get(i);
                           newExp.add(sc1.toSnowflakeSelect(to_sqs, from_sqs));
                        } else {
                           newExp.add(this.regExp.get(i));
                        }
                     }

                     wi.setRegExp(newExp);
                  } else {
                     wi.setOperator(this.operator);
                  }
               } else {
                  if (this.operator.equalsIgnoreCase("MATCHES")) {
                     wi.setOperator("LIKE");
                  } else {
                     wi.setOperator("NOT LIKE");
                  }

                  whereColumn = wi.getRightWhereExp();
                  v_nsce = whereColumn.getColumnExpression();
                  newColumnExpression = new Vector();
                  if (v_nsce != null) {
                     String ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                     if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                     }

                     if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                     }

                     newColumnExpression.add(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(newColumnExpression);
                  }
               }
            }
         }

         if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            whereColumn = wi.getRightWhereExp();
            WhereColumn l_wc = wi.getLeftWhereExp();
            if (whereColumn.getColumnExpression() != null && whereColumn.getColumnExpression().size() != 1) {
               newColumnExpression = new Vector();
               Vector v_nrsce = new Vector();
               Vector v_lsce = l_wc.getColumnExpression();
               Vector v_rsce = whereColumn.getColumnExpression();
               Vector v_removedcomma_right_column_exp = new Vector();
               Vector v_removedcomma_left_column_exp = new Vector();

               int i;
               String s_ce;
               for(i = 0; i < v_lsce.size(); ++i) {
                  if (v_lsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_lsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                  }
               }

               for(i = 0; i < v_rsce.size(); ++i) {
                  if (v_rsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_rsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                  }
               }

               newColumnExpression.addElement(v_removedcomma_left_column_exp.elementAt(0));
               v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
               l_wc.setColumnExpression(newColumnExpression);
               whereColumn.setColumnExpression(v_nrsce);

               for(i = 1; i < v_removedcomma_right_column_exp.size(); ++i) {
                  WhereItem n_wi = new WhereItem();
                  WhereColumn l_nwc = new WhereColumn();
                  WhereColumn r_nwc = new WhereColumn();
                  Vector v_lnsc = new Vector();
                  Vector v_rnsc = new Vector();
                  v_lnsc.addElement(v_removedcomma_left_column_exp.elementAt(0));
                  v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(i));
                  l_nwc.setColumnExpression(v_lnsc);
                  r_nwc.setColumnExpression(v_rnsc);
                  n_wi.setLeftWhereExp(l_nwc);
                  n_wi.setRightWhereExp(r_nwc);
                  n_wi.setOperator(this.operator);
                  if (i == v_lsce.size() - 1) {
                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  } else {
                     if (this.operator2.equalsIgnoreCase("ALL")) {
                        from_sqs.getWhereExpression().addOperator("AND");
                     } else {
                        from_sqs.getWhereExpression().addOperator("OR");
                     }

                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  }
               }
            }
         }

         if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.LeftJoin != null) {
            wi.setLeftJoin((String)null);
         }

         if (this.RightJoin != null) {
            wi.setRightJoin((String)null);
         }

         return wi;
      }
   }

   public WhereItem toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         wi.setFromTableList(this.fromTableList);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         if (this.leftWhereExp != null) {
            this.leftWhereExp.setStmtTableName(this.stmtTableName);
            this.leftWhereExp.setFromTableList(this.fromTableList);
            wi.setLeftWhereExp(this.leftWhereExp.toDB2Select(to_sqs, from_sqs));
         }

         if (this.rightWhereExp != null) {
            if (this.leftWhereExp != null) {
               this.rightWhereExp.setTargetDataType(this.leftWhereExp.getSourceDataType());
            }

            this.rightWhereExp.setStmtTableName(this.stmtTableName);
            this.rightWhereExp.setFromTableList(this.fromTableList);
            wi.setRightWhereExp(this.rightWhereExp.toDB2Select(to_sqs, from_sqs));
         }

         if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toDB2());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toDB2Select(to_sqs, from_sqs));
            }
         }

         WhereColumn whereColumn;
         Vector newColumnExpression;
         if (this.operator != null) {
            Vector columnExpression;
            String ColumnExpressionAsString;
            if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
               wi.setOperator("LIKE");
               if (wi.getRightWhereSubQuery() != null) {
                  throw new ConvertException("Conversion failure.. Subquery can't be converted");
               }

               whereColumn = wi.getRightWhereExp();
               columnExpression = whereColumn.getColumnExpression();
               newColumnExpression = new Vector();
               if (columnExpression != null) {
                  if (columnExpression.size() != 1) {
                     throw new ConvertException("Conversion failure.. Expressions can't be converted");
                  }

                  ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                  if (ColumnExpressionAsString.charAt(0) == '\'') {
                     ColumnExpressionAsString = ColumnExpressionAsString.replace('\'', ' ').trim();
                     ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                  } else if (ColumnExpressionAsString.charAt(0) == '"') {
                     ColumnExpressionAsString = ColumnExpressionAsString.replace('"', ' ').trim();
                     ColumnExpressionAsString = "\"%" + ColumnExpressionAsString + "%\"";
                  } else {
                     ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                  }

                  newColumnExpression.addElement(ColumnExpressionAsString);
                  whereColumn.setColumnExpression(newColumnExpression);
               }

               wi.setRightWhereExp(whereColumn);
            } else if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
               if (this.operator.equalsIgnoreCase("<=>")) {
                  wi.setOperator("=");
                  wi.isNullSafeEqualsOperator = true;
               } else {
                  wi.setOperator(this.operator);
               }
            } else {
               if (this.operator.equalsIgnoreCase("MATCHES")) {
                  wi.setOperator("LIKE");
               } else {
                  wi.setOperator("NOT LIKE");
               }

               whereColumn = wi.getRightWhereExp();
               columnExpression = whereColumn.getColumnExpression();
               newColumnExpression = new Vector();
               if (columnExpression != null) {
                  ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                  if (ColumnExpressionAsString.indexOf("*") != -1) {
                     ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                  }

                  if (ColumnExpressionAsString.indexOf("_") != -1) {
                     ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                  }

                  newColumnExpression.add(ColumnExpressionAsString);
                  whereColumn.setColumnExpression(newColumnExpression);
               }
            }
         }

         Vector lwcolexp;
         Vector colExpr;
         Vector rColExpr;
         Vector args;
         Vector newColExpV;
         if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            whereColumn = wi.getRightWhereExp();
            WhereColumn l_wc = wi.getLeftWhereExp();
            if (whereColumn.getColumnExpression() != null && whereColumn.getColumnExpression().size() != 1) {
               newColumnExpression = new Vector();
               Vector v_nrsce = new Vector();
               lwcolexp = l_wc.getColumnExpression();
               colExpr = whereColumn.getColumnExpression();
               rColExpr = new Vector();
               args = new Vector();

               int i;
               String s_ce;
               for(i = 0; i < lwcolexp.size(); ++i) {
                  if (lwcolexp.elementAt(i) instanceof String) {
                     s_ce = (String)lwcolexp.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        args.addElement(lwcolexp.elementAt(i));
                     }
                  } else {
                     args.addElement(lwcolexp.elementAt(i));
                  }
               }

               for(i = 0; i < colExpr.size(); ++i) {
                  if (colExpr.elementAt(i) instanceof String) {
                     s_ce = (String)colExpr.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        rColExpr.addElement(colExpr.elementAt(i));
                     }
                  } else {
                     rColExpr.addElement(colExpr.elementAt(i));
                  }
               }

               newColumnExpression.addElement(args.elementAt(0));
               v_nrsce.addElement(rColExpr.elementAt(0));
               l_wc.setColumnExpression(newColumnExpression);
               whereColumn.setColumnExpression(v_nrsce);

               for(i = 1; i < rColExpr.size(); ++i) {
                  WhereItem n_wi = new WhereItem();
                  WhereColumn l_nwc = new WhereColumn();
                  WhereColumn r_nwc = new WhereColumn();
                  newColExpV = new Vector();
                  Vector v_rnsc = new Vector();
                  newColExpV.addElement(args.elementAt(0));
                  v_rnsc.addElement(rColExpr.elementAt(i));
                  l_nwc.setColumnExpression(newColExpV);
                  r_nwc.setColumnExpression(v_rnsc);
                  n_wi.setLeftWhereExp(l_nwc);
                  n_wi.setRightWhereExp(r_nwc);
                  n_wi.setOperator(this.operator);
                  if (i == lwcolexp.size() - 1) {
                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  } else {
                     if (this.operator2.equalsIgnoreCase("ALL")) {
                        from_sqs.getWhereExpression().addOperator("AND");
                     } else {
                        from_sqs.getWhereExpression().addOperator("OR");
                     }

                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  }
               }
            }
         }

         if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.LeftJoin != null) {
            wi.setLeftJoin((String)null);
         }

         if (this.RightJoin != null) {
            wi.setRightJoin((String)null);
         }

         boolean change_rightwhere_expression = false;
         String leftside_function_name = "";
         WhereColumn lwcol = wi.getLeftWhereExp();
         if (lwcol != null) {
            Object obj = null;
            lwcolexp = lwcol.getColumnExpression();
            if (lwcolexp != null) {
               obj = lwcolexp.get(0);
            }

            if (obj != null && obj instanceof FunctionCalls) {
               colExpr = ((FunctionCalls)obj).getFunctionArguments();
               String functionname = ((FunctionCalls)obj).getFunctionNameAsAString();
               if (functionname != null) {
                  if (functionname.equalsIgnoreCase("TIMESTAMP")) {
                     leftside_function_name = "TIMESTAMP";
                     change_rightwhere_expression = true;
                  } else if (functionname.equalsIgnoreCase("DATE")) {
                     leftside_function_name = "DATE";
                     change_rightwhere_expression = true;
                  }
               }
            }
         }

         WhereColumn rwcol;
         if (change_rightwhere_expression) {
            rwcol = wi.getRightWhereExp();
            if (rwcol != null) {
               Object objr = null;
               colExpr = rwcol.getColumnExpression();
               if (colExpr != null) {
                  objr = colExpr.get(0);
               }

               if (objr != null && objr instanceof FunctionCalls) {
                  FunctionCalls currentFnCall = (FunctionCalls)objr;
                  args = currentFnCall.getFunctionArguments();
                  String functionname = currentFnCall.getFunctionNameAsAString();
                  if (functionname != null && (functionname.equalsIgnoreCase("CHAR") || functionname.equalsIgnoreCase("SUBSTR"))) {
                     Vector newFunctionArgumentsV = new Vector();
                     newFunctionArgumentsV.addElement(currentFnCall);
                     newFunctionArgumentsV.addElement("'00.00.00'");
                     FunctionCalls newFnCall = new FunctionCalls();
                     TableColumn tc = new TableColumn();
                     tc.setColumnName("TIMESTAMP");
                     newFnCall.setFunctionName(tc);
                     newFnCall.setFunctionArguments(newFunctionArgumentsV);
                     newColExpV = new Vector();
                     newColExpV.addElement(newFnCall);
                     rwcol.setColumnExpression(newColExpV);
                     wi.setRightWhereExp(rwcol);
                  }
               }
            }
         }

         rwcol = wi.getLeftWhereExp();
         String leftDataType = null;
         if (rwcol != null) {
            colExpr = rwcol.getColumnExpression();
            if (colExpr != null) {
               for(int i = 0; i < colExpr.size(); ++i) {
                  Object obj = colExpr.get(i);
                  if (obj instanceof TableColumn) {
                     leftDataType = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)obj);
                  }
               }
            }
         }

         WhereColumn db2RWE = wi.getRightWhereExp();
         if (leftDataType != null && (leftDataType.indexOf("int") != -1 || leftDataType.indexOf("num") != -1) && db2RWE != null && db2RWE.getColumnExpression() != null && db2RWE.toString().trim().startsWith("'")) {
            rColExpr = db2RWE.getColumnExpression();
            if (rColExpr != null && rColExpr.size() == 1) {
               rColExpr.setElementAt(rColExpr.get(0).toString().trim().substring(1, rColExpr.get(0).toString().trim().length() - 1), 0);
            }
         }

         return wi;
      }
   }

   public WhereItem toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toANSISelect(to_sqs, from_sqs));
         }

         if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toANSISelect(to_sqs, from_sqs));
         }

         if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toANSI());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toANSISelect(to_sqs, from_sqs));
            }
         }

         if (this.operator != null) {
            if (!this.operator.equalsIgnoreCase("^=") && !this.operator.equalsIgnoreCase("!=")) {
               if (this.operator.equalsIgnoreCase("!>")) {
                  wi.setOperator("<=");
               } else if (this.operator.equalsIgnoreCase("!<")) {
                  wi.setOperator(">=");
               } else {
                  WhereColumn whereColumn;
                  Vector v_nsce;
                  Vector newColumnExpression;
                  String ColumnExpressionAsString;
                  if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                     wi.setOperator("LIKE");
                     if (wi.getRightWhereSubQuery() != null) {
                        throw new ConvertException("Conversion failure.. Subquery can't be converted");
                     }

                     whereColumn = wi.getRightWhereExp();
                     v_nsce = whereColumn.getColumnExpression();
                     newColumnExpression = new Vector();
                     if (v_nsce != null) {
                        if (v_nsce.size() != 1) {
                           throw new ConvertException("Conversion failure.. Expressions can't be converted");
                        }

                        ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                        if (ColumnExpressionAsString.charAt(0) == '\'') {
                           ColumnExpressionAsString = ColumnExpressionAsString.replace('\'', ' ').trim();
                           ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                        } else if (ColumnExpressionAsString.charAt(0) == '"') {
                           ColumnExpressionAsString = ColumnExpressionAsString.replace('"', ' ').trim();
                           ColumnExpressionAsString = "\"%" + ColumnExpressionAsString + "%\"";
                        } else {
                           ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                        }

                        newColumnExpression.addElement(ColumnExpressionAsString);
                        whereColumn.setColumnExpression(newColumnExpression);
                     }

                     wi.setRightWhereExp(whereColumn);
                  } else if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                     if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null) {
                        whereColumn = wi.getRightWhereExp();
                        v_nsce = new Vector();
                        newColumnExpression = whereColumn.getColumnExpression();
                        if (newColumnExpression != null && newColumnExpression.size() > 0 && !(newColumnExpression.elementAt(0) instanceof String)) {
                           v_nsce.addElement("(");

                           for(int i = 0; i < newColumnExpression.size(); ++i) {
                              v_nsce.addElement(newColumnExpression.elementAt(i));
                           }

                           v_nsce.addElement(")");
                           whereColumn.setColumnExpression(v_nsce);
                        }

                        wi.setOperator(this.operator);
                     } else {
                        wi.setOperator(this.operator);
                     }
                  } else {
                     if (this.operator.equalsIgnoreCase("MATCHES")) {
                        wi.setOperator("LIKE");
                     } else {
                        wi.setOperator("NOT LIKE");
                     }

                     whereColumn = wi.getRightWhereExp();
                     v_nsce = whereColumn.getColumnExpression();
                     newColumnExpression = new Vector();
                     if (v_nsce != null) {
                        ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                        if (ColumnExpressionAsString.indexOf("*") != -1) {
                           ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                        }

                        if (ColumnExpressionAsString.indexOf("_") != -1) {
                           ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                        }

                        newColumnExpression.add(ColumnExpressionAsString);
                        whereColumn.setColumnExpression(newColumnExpression);
                     }
                  }
               }
            } else {
               wi.setOperator("<>");
            }
         }

         if (this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.LeftJoin != null) {
            wi.setLeftJoin((String)null);
         }

         if (this.RightJoin != null) {
            wi.setRightJoin((String)null);
         }

         return wi;
      }
   }

   public WhereItem toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         if (this.rownumClause != null && !this.is_Case_Expression) {
            to_sqs.setRownumClause(this.rownumClause);
            return null;
         } else {
            wi.setRownumClause(this.rownumClause);
            wi.setOpenBrace(this.openBraces);
            wi.setCloseBrace(this.closeBraces);
            if (this.beginOperator != null) {
               wi.setBeginOperator(this.beginOperator);
            }

            if (this.operator1 != null) {
               wi.setOperator1(this.operator1);
            }

            if (this.leftWhereExp != null) {
               wi.setLeftWhereExp(this.leftWhereExp.toTeradataSelect(to_sqs, from_sqs));
            }

            WhereColumn whereColumn;
            if (this.rightWhereExp != null) {
               whereColumn = this.rightWhereExp;
               whereColumn = whereColumn.toTeradataSelect(to_sqs, from_sqs);
               wi.setRightWhereExp(whereColumn);
            }

            if (this.rightWhereSubQuery != null) {
               SelectQueryStatement subQuery = this.rightWhereSubQuery.toTeradata();
               if (subQuery.getWithStatement() != null) {
                  from_sqs.getListOfWithStatements().add(subQuery.getWithStatement());
                  SelectQueryStatement tempSubQuery = subQuery.getWithStatement().getWithSQS();
                  subQuery.getWithStatement().setWithSQS((SelectQueryStatement)null);
                  subQuery = tempSubQuery;
               }

               wi.setRightWhereSubQuery(subQuery);
               if (this.rightWhereSubQueryExp != null) {
                  wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toTeradataSelect(to_sqs, from_sqs));
               }
            }

            if (this.operator != null) {
               if (!this.operator.equalsIgnoreCase("^=") && !this.operator.equalsIgnoreCase("!=")) {
                  if (this.operator.equalsIgnoreCase("!>")) {
                     wi.setOperator("<=");
                  } else if (this.operator.equalsIgnoreCase("!<")) {
                     wi.setOperator(">=");
                  } else {
                     Vector newColumnExpression;
                     String ColumnExpressionAsString;
                     Vector v_nsce;
                     if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                        wi.setOperator("LIKE");
                        if (wi.getRightWhereSubQuery() != null) {
                           throw new ConvertException("Conversion failure.. Subquery can't be converted");
                        }

                        whereColumn = wi.getRightWhereExp();
                        v_nsce = whereColumn.getColumnExpression();
                        newColumnExpression = new Vector();
                        if (v_nsce != null) {
                           if (v_nsce.size() != 1) {
                              throw new ConvertException("Conversion failure.. Expressions can't be converted");
                           }

                           ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                           if (ColumnExpressionAsString.charAt(0) == '\'') {
                              ColumnExpressionAsString = ColumnExpressionAsString.replace('\'', ' ').trim();
                              ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                           } else if (ColumnExpressionAsString.charAt(0) == '"') {
                              ColumnExpressionAsString = ColumnExpressionAsString.replace('"', ' ').trim();
                              ColumnExpressionAsString = "\"%" + ColumnExpressionAsString + "%\"";
                           } else {
                              ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                           }

                           newColumnExpression.addElement(ColumnExpressionAsString);
                           whereColumn.setColumnExpression(newColumnExpression);
                        }

                        wi.setRightWhereExp(whereColumn);
                     } else if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                        if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null) {
                           whereColumn = wi.getRightWhereExp();
                           v_nsce = new Vector();
                           newColumnExpression = whereColumn.getColumnExpression();
                           if (newColumnExpression != null && newColumnExpression.size() > 0 && !(newColumnExpression.elementAt(0) instanceof String)) {
                              v_nsce.addElement("(");

                              for(int i = 0; i < newColumnExpression.size(); ++i) {
                                 v_nsce.addElement(newColumnExpression.elementAt(i));
                              }

                              v_nsce.addElement(")");
                              whereColumn.setColumnExpression(v_nsce);
                           }

                           Vector newInItems = new Vector();
                           WhereExpression orWhereExp = new WhereExpression();
                           Vector orWhereItems = new Vector();
                           newColumnExpression = whereColumn.getColumnExpression();
                           Vector orWhereOperators = new Vector();
                           Vector nonWhereColumnExpr = new Vector();

                           for(int k = 0; k < newColumnExpression.size(); ++k) {
                              Object obj = newColumnExpression.get(k);
                              if (obj instanceof WhereColumn) {
                                 WhereColumn inWhereCol = (WhereColumn)obj;
                                 if (inWhereCol.getColumnExpression().get(0) instanceof String) {
                                    newInItems.add(inWhereCol);
                                    newInItems.add(",");
                                 } else {
                                    WhereItem inWhereItem = new WhereItem();
                                    inWhereItem.setLeftWhereExp(wi.getLeftWhereExp());
                                    inWhereItem.setRightWhereExp(inWhereCol);
                                    if (this.operator.equalsIgnoreCase("IN")) {
                                       inWhereItem.setOperator("=");
                                    } else {
                                       inWhereItem.setOperator("!=");
                                    }

                                    orWhereItems.add(inWhereItem);
                                    orWhereOperators.add("OR");
                                 }
                              } else if (!(obj instanceof String)) {
                                 if (obj != null && obj.toString().startsWith("/*") && obj.toString().endsWith("*/")) {
                                    nonWhereColumnExpr.add(obj);
                                 } else {
                                    nonWhereColumnExpr.add("/*" + obj + "*/");
                                 }
                              }
                           }

                           if (newInItems.size() > 0) {
                              WhereItem newInWhereItem = new WhereItem();
                              newInWhereItem.setLeftWhereExp(wi.getLeftWhereExp());
                              WhereColumn newInWhereRightColumn = new WhereColumn();
                              Vector newInWhereRightColumnExpr = new Vector();
                              newInWhereRightColumnExpr.add("(");
                              newInWhereRightColumnExpr.addAll(newInItems.subList(0, newInItems.size() - 1));
                              newInWhereRightColumnExpr.add(")");
                              newInWhereRightColumn.setColumnExpression(newInWhereRightColumnExpr);
                              newInWhereItem.setRightWhereExp(newInWhereRightColumn);
                              newInWhereItem.setOperator(this.operator);
                              orWhereItems.add(newInWhereItem);
                              orWhereOperators.add("OR");
                           }

                           if (orWhereItems.size() > 0) {
                              orWhereExp.setWhereItem(orWhereItems);
                              if (orWhereOperators.size() > 0) {
                                 orWhereExp.setOperator(new Vector(orWhereOperators.subList(0, orWhereOperators.size() - 1)));
                              }

                              WhereColumn newInLeftCol = new WhereColumn();
                              Vector newInLeftColExp = new Vector();
                              newInLeftColExp.add(orWhereExp);
                              newInLeftColExp.addAll(nonWhereColumnExpr);
                              newInLeftCol.setColumnExpression(newInLeftColExp);
                              if (orWhereItems.size() > 1) {
                                 newInLeftCol.setOpenBrace("(");
                                 newInLeftCol.setCloseBrace(")");
                              }

                              wi.setLeftWhereExp(newInLeftCol);
                              wi.setRightWhereExp((WhereColumn)null);
                              wi.setOperator((String)null);
                           } else {
                              wi.setOperator(this.operator);
                           }
                        } else {
                           wi.setOperator(this.operator);
                        }
                     } else {
                        if (this.operator.equalsIgnoreCase("MATCHES")) {
                           wi.setOperator("LIKE");
                        } else {
                           wi.setOperator("NOT LIKE");
                        }

                        whereColumn = wi.getRightWhereExp();
                        v_nsce = whereColumn.getColumnExpression();
                        newColumnExpression = new Vector();
                        if (v_nsce != null) {
                           ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                           if (ColumnExpressionAsString.indexOf("*") != -1) {
                              ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                           }

                           if (ColumnExpressionAsString.indexOf("_") != -1) {
                              ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                           }

                           newColumnExpression.add(ColumnExpressionAsString);
                           whereColumn.setColumnExpression(newColumnExpression);
                        }
                     }
                  }
               } else {
                  wi.setOperator("<>");
               }
            }

            if (this.operator2 != null) {
               wi.setOperator2(this.operator2);
            }

            if (this.operator3 != null) {
               wi.setOperator3(this.operator3);
            }

            if (this.LeftJoin != null) {
               wi.setLeftJoin((String)null);
            }

            if (this.RightJoin != null) {
               wi.setRightJoin((String)null);
            }

            return wi;
         }
      }
   }

   public WhereItem toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         this.modifyWhereItemsForLikeOperatorWithoutConstantOnRHS(false);
         if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toBigQuerySelect(to_sqs, from_sqs));
         }

         if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toBigQuerySelect(to_sqs, from_sqs));
         }

         if (this.rightWhereSubQuery != null) {
            this.rightWhereSubQuery.setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toBigQuery());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toBigQuerySelect(to_sqs, from_sqs));
            }
         }

         WhereColumn whereColumn;
         Vector newColumnExpression;
         if (this.operator != null) {
            this.modifyWhereExpForLikeOperator(wi, this.operator, "STRING");
            if (this.operator.equalsIgnoreCase("^=")) {
               wi.setOperator("<>");
            } else if (this.operator.equalsIgnoreCase("!>")) {
               wi.setOperator("<=");
            } else if (this.operator.equalsIgnoreCase("!<")) {
               wi.setOperator(">=");
            } else if (this.operator.equalsIgnoreCase("<=>")) {
               if (from_sqs != null) {
                  wi.setOperator("=");
                  wi.isNullSafeEqualsOperator = true;
               } else {
                  wi.setOperator("IS NOT DISTINCT FROM");
               }
            } else {
               Vector v_nsce;
               if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                  if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null) {
                     whereColumn = wi.getRightWhereExp();
                     v_nsce = new Vector();
                     newColumnExpression = whereColumn.getColumnExpression();
                     if (newColumnExpression != null && newColumnExpression.size() > 0 && !(newColumnExpression.elementAt(0) instanceof String)) {
                        v_nsce.addElement("(");

                        for(int i = 0; i < newColumnExpression.size(); ++i) {
                           v_nsce.addElement(newColumnExpression.elementAt(i));
                        }

                        v_nsce.addElement(")");
                        whereColumn.setColumnExpression(v_nsce);
                     }

                     wi.setOperator(this.operator);
                  } else if (this.operator.trim().equalsIgnoreCase("LNNVl") && this.lnnvlWhereExp != null) {
                     WhereExpression we1 = this.lnnvlWhereExp.toBigQuerySelect(to_sqs, from_sqs);
                     this.convertLNNVLtoStatement(we1);
                     wi.setCaseStatementForLNNVLFunc(this.casestmtFromLNNVLClause);
                  } else if (this.operator.trim().equalsIgnoreCase("REGEXP_LIKE") && this.regExp != null) {
                     wi.setOperator("REGEXP_LIKE");
                     Vector newExp = new Vector();

                     for(int i = 0; i < this.regExp.size(); ++i) {
                        if (this.regExp.get(i) instanceof SelectColumn) {
                           SelectColumn sc1 = (SelectColumn)this.regExp.get(i);
                           newExp.add(sc1.toBigQuerySelect(to_sqs, from_sqs));
                        } else {
                           newExp.add(this.regExp.get(i));
                        }
                     }

                     wi.setRegExp(newExp);
                  } else {
                     wi.setOperator(this.operator);
                  }
               } else {
                  if (this.operator.equalsIgnoreCase("MATCHES")) {
                     wi.setOperator("LIKE");
                  } else {
                     wi.setOperator("NOT LIKE");
                  }

                  whereColumn = wi.getRightWhereExp();
                  v_nsce = whereColumn.getColumnExpression();
                  newColumnExpression = new Vector();
                  if (v_nsce != null) {
                     String ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                     if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                     }

                     if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                     }

                     newColumnExpression.add(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(newColumnExpression);
                  }
               }
            }
         }

         if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            whereColumn = wi.getRightWhereExp();
            WhereColumn l_wc = wi.getLeftWhereExp();
            if (whereColumn.getColumnExpression() != null && whereColumn.getColumnExpression().size() != 1) {
               newColumnExpression = new Vector();
               Vector v_nrsce = new Vector();
               Vector v_lsce = l_wc.getColumnExpression();
               Vector v_rsce = whereColumn.getColumnExpression();
               Vector v_removedcomma_right_column_exp = new Vector();
               Vector v_removedcomma_left_column_exp = new Vector();

               int i;
               String s_ce;
               for(i = 0; i < v_lsce.size(); ++i) {
                  if (v_lsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_lsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                  }
               }

               for(i = 0; i < v_rsce.size(); ++i) {
                  if (v_rsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_rsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                  }
               }

               newColumnExpression.addElement(v_removedcomma_left_column_exp.elementAt(0));
               v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
               l_wc.setColumnExpression(newColumnExpression);
               whereColumn.setColumnExpression(v_nrsce);

               for(i = 1; i < v_removedcomma_right_column_exp.size(); ++i) {
                  WhereItem n_wi = new WhereItem();
                  WhereColumn l_nwc = new WhereColumn();
                  WhereColumn r_nwc = new WhereColumn();
                  Vector v_lnsc = new Vector();
                  Vector v_rnsc = new Vector();
                  v_lnsc.addElement(v_removedcomma_left_column_exp.elementAt(0));
                  v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(i));
                  l_nwc.setColumnExpression(v_lnsc);
                  r_nwc.setColumnExpression(v_rnsc);
                  n_wi.setLeftWhereExp(l_nwc);
                  n_wi.setRightWhereExp(r_nwc);
                  n_wi.setOperator(this.operator);
                  if (i == v_lsce.size() - 1) {
                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  } else {
                     if (this.operator2.equalsIgnoreCase("ALL")) {
                        from_sqs.getWhereExpression().addOperator("AND");
                     } else {
                        from_sqs.getWhereExpression().addOperator("OR");
                     }

                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  }
               }
            }
         }

         if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.LeftJoin != null) {
            wi.setLeftJoin((String)null);
         }

         if (this.RightJoin != null) {
            wi.setRightJoin((String)null);
         }

         return wi;
      }
   }

   public WhereItem toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         this.modifyWhereItemsForLikeOperatorWithoutConstantOnRHS(false);
         if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toAthenaSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toAthenaSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereSubQuery != null) {
            this.rightWhereSubQuery.setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toAthena());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toAthenaSelect(to_sqs, from_sqs));
            }
         }

         WhereColumn whereColumn;
         Vector newColumnExpression;
         if (this.operator != null) {
            this.modifyWhereExpForLikeOperator(wi, this.operator, "VARCHAR");
            if (this.operator.equalsIgnoreCase("^=")) {
               wi.setOperator("<>");
            } else if (this.operator.equalsIgnoreCase("!>")) {
               wi.setOperator("<=");
            } else if (this.operator.equalsIgnoreCase("!<")) {
               wi.setOperator(">=");
            } else if (this.operator.equalsIgnoreCase("<=>")) {
               if (from_sqs != null) {
                  wi.setOperator("=");
                  wi.isNullSafeEqualsOperator = true;
               } else {
                  wi.setOperator("IS NOT DISTINCT FROM");
               }
            } else {
               Vector v_nsce;
               if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                  if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null) {
                     whereColumn = wi.getRightWhereExp();
                     v_nsce = new Vector();
                     newColumnExpression = whereColumn.getColumnExpression();
                     if (newColumnExpression != null && newColumnExpression.size() > 0 && !(newColumnExpression.elementAt(0) instanceof String)) {
                        v_nsce.addElement("(");

                        for(int i = 0; i < newColumnExpression.size(); ++i) {
                           v_nsce.addElement(newColumnExpression.elementAt(i));
                        }

                        v_nsce.addElement(")");
                        whereColumn.setColumnExpression(v_nsce);
                     }

                     wi.setOperator(this.operator);
                  } else if (this.operator.trim().equalsIgnoreCase("LNNVl") && this.lnnvlWhereExp != null) {
                     WhereExpression we1 = this.lnnvlWhereExp.toAthenaSelect(to_sqs, from_sqs);
                     this.convertLNNVLtoStatement(we1);
                     wi.setCaseStatementForLNNVLFunc(this.casestmtFromLNNVLClause);
                  } else if (this.operator.trim().equalsIgnoreCase("REGEXP_LIKE") && this.regExp != null) {
                     wi.setOperator("REGEXP_LIKE");
                     Vector newExp = new Vector();

                     for(int i = 0; i < this.regExp.size(); ++i) {
                        if (this.regExp.get(i) instanceof SelectColumn) {
                           SelectColumn sc1 = (SelectColumn)this.regExp.get(i);
                           newExp.add(sc1.toAthenaSelect(to_sqs, from_sqs));
                        } else {
                           newExp.add(this.regExp.get(i));
                        }
                     }

                     wi.setRegExp(newExp);
                  } else {
                     wi.setOperator(this.operator);
                  }
               } else {
                  if (this.operator.equalsIgnoreCase("MATCHES")) {
                     wi.setOperator("LIKE");
                  } else {
                     wi.setOperator("NOT LIKE");
                  }

                  whereColumn = wi.getRightWhereExp();
                  v_nsce = whereColumn.getColumnExpression();
                  newColumnExpression = new Vector();
                  if (v_nsce != null) {
                     String ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                     if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                     }

                     if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                     }

                     newColumnExpression.add(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(newColumnExpression);
                  }
               }
            }
         }

         if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            whereColumn = wi.getRightWhereExp();
            WhereColumn l_wc = wi.getLeftWhereExp();
            if (whereColumn.getColumnExpression() != null && whereColumn.getColumnExpression().size() != 1) {
               newColumnExpression = new Vector();
               Vector v_nrsce = new Vector();
               Vector v_lsce = l_wc.getColumnExpression();
               Vector v_rsce = whereColumn.getColumnExpression();
               Vector v_removedcomma_right_column_exp = new Vector();
               Vector v_removedcomma_left_column_exp = new Vector();

               int i;
               String s_ce;
               for(i = 0; i < v_lsce.size(); ++i) {
                  if (v_lsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_lsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                  }
               }

               for(i = 0; i < v_rsce.size(); ++i) {
                  if (v_rsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_rsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                  }
               }

               newColumnExpression.addElement(v_removedcomma_left_column_exp.elementAt(0));
               v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
               l_wc.setColumnExpression(newColumnExpression);
               whereColumn.setColumnExpression(v_nrsce);

               for(i = 1; i < v_removedcomma_right_column_exp.size(); ++i) {
                  WhereItem n_wi = new WhereItem();
                  WhereColumn l_nwc = new WhereColumn();
                  WhereColumn r_nwc = new WhereColumn();
                  Vector v_lnsc = new Vector();
                  Vector v_rnsc = new Vector();
                  v_lnsc.addElement(v_removedcomma_left_column_exp.elementAt(0));
                  v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(i));
                  l_nwc.setColumnExpression(v_lnsc);
                  r_nwc.setColumnExpression(v_rnsc);
                  n_wi.setLeftWhereExp(l_nwc);
                  n_wi.setRightWhereExp(r_nwc);
                  n_wi.setOperator(this.operator);
                  if (i == v_lsce.size() - 1) {
                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  } else {
                     if (this.operator2.equalsIgnoreCase("ALL")) {
                        from_sqs.getWhereExpression().addOperator("AND");
                     } else {
                        from_sqs.getWhereExpression().addOperator("OR");
                     }

                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  }
               }
            }
         }

         if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.LeftJoin != null) {
            wi.setLeftJoin((String)null);
         }

         if (this.RightJoin != null) {
            wi.setRightJoin((String)null);
         }

         return wi;
      }
   }

   public WhereItem toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         this.modifyWhereItemsForLikeOperatorWithoutConstantOnRHS(false);
         if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toSapHanaSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toSapHanaSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereSubQuery != null) {
            this.rightWhereSubQuery.setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toSapHana());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toSapHanaSelect(to_sqs, from_sqs));
            }
         }

         WhereColumn whereColumn;
         Vector newColumnExpression;
         if (this.operator != null) {
            this.modifyWhereExpForLikeOperator(wi, this.operator, "VARCHAR");
            if (this.operator.equalsIgnoreCase("^=")) {
               wi.setOperator("<>");
            } else if (this.operator.equalsIgnoreCase("!>")) {
               wi.setOperator("<=");
            } else if (this.operator.equalsIgnoreCase("!<")) {
               wi.setOperator(">=");
            } else if (this.operator.equalsIgnoreCase("<=>")) {
               if (from_sqs != null) {
                  wi.setOperator("=");
                  wi.isNullSafeEqualsOperator = true;
               } else {
                  wi.setOperator("IS NOT DISTINCT FROM");
               }
            } else {
               Vector v_nsce;
               if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                  if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null) {
                     whereColumn = wi.getRightWhereExp();
                     v_nsce = new Vector();
                     newColumnExpression = whereColumn.getColumnExpression();
                     if (newColumnExpression != null && newColumnExpression.size() > 0 && !(newColumnExpression.elementAt(0) instanceof String)) {
                        v_nsce.addElement("(");

                        for(int i = 0; i < newColumnExpression.size(); ++i) {
                           v_nsce.addElement(newColumnExpression.elementAt(i));
                        }

                        v_nsce.addElement(")");
                        whereColumn.setColumnExpression(v_nsce);
                     }

                     wi.setOperator(this.operator);
                  } else if (this.operator.trim().equalsIgnoreCase("LNNVl") && this.lnnvlWhereExp != null) {
                     WhereExpression we1 = this.lnnvlWhereExp.toSapHanaSelect(to_sqs, from_sqs);
                     this.convertLNNVLtoStatement(we1);
                     wi.setCaseStatementForLNNVLFunc(this.casestmtFromLNNVLClause);
                  } else if (this.operator.trim().equalsIgnoreCase("REGEXP_LIKE") && this.regExp != null) {
                     wi.setOperator("REGEXP_LIKE");
                     Vector newExp = new Vector();

                     for(int i = 0; i < this.regExp.size(); ++i) {
                        if (this.regExp.get(i) instanceof SelectColumn) {
                           SelectColumn sc1 = (SelectColumn)this.regExp.get(i);
                           newExp.add(sc1.toSapHanaSelect(to_sqs, from_sqs));
                        } else {
                           newExp.add(this.regExp.get(i));
                        }
                     }

                     wi.setRegExp(newExp);
                  } else {
                     wi.setOperator(this.operator);
                  }
               } else {
                  if (this.operator.equalsIgnoreCase("MATCHES")) {
                     wi.setOperator("LIKE");
                  } else {
                     wi.setOperator("NOT LIKE");
                  }

                  whereColumn = wi.getRightWhereExp();
                  v_nsce = whereColumn.getColumnExpression();
                  newColumnExpression = new Vector();
                  if (v_nsce != null) {
                     String ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                     if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                     }

                     if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                     }

                     newColumnExpression.add(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(newColumnExpression);
                  }
               }
            }
         }

         if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            whereColumn = wi.getRightWhereExp();
            WhereColumn l_wc = wi.getLeftWhereExp();
            if (whereColumn.getColumnExpression() != null && whereColumn.getColumnExpression().size() != 1) {
               newColumnExpression = new Vector();
               Vector v_nrsce = new Vector();
               Vector v_lsce = l_wc.getColumnExpression();
               Vector v_rsce = whereColumn.getColumnExpression();
               Vector v_removedcomma_right_column_exp = new Vector();
               Vector v_removedcomma_left_column_exp = new Vector();

               int i;
               String s_ce;
               for(i = 0; i < v_lsce.size(); ++i) {
                  if (v_lsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_lsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                  }
               }

               for(i = 0; i < v_rsce.size(); ++i) {
                  if (v_rsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_rsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                  }
               }

               newColumnExpression.addElement(v_removedcomma_left_column_exp.elementAt(0));
               v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
               l_wc.setColumnExpression(newColumnExpression);
               whereColumn.setColumnExpression(v_nrsce);

               for(i = 1; i < v_removedcomma_right_column_exp.size(); ++i) {
                  WhereItem n_wi = new WhereItem();
                  WhereColumn l_nwc = new WhereColumn();
                  WhereColumn r_nwc = new WhereColumn();
                  Vector v_lnsc = new Vector();
                  Vector v_rnsc = new Vector();
                  v_lnsc.addElement(v_removedcomma_left_column_exp.elementAt(0));
                  v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(i));
                  l_nwc.setColumnExpression(v_lnsc);
                  r_nwc.setColumnExpression(v_rnsc);
                  n_wi.setLeftWhereExp(l_nwc);
                  n_wi.setRightWhereExp(r_nwc);
                  n_wi.setOperator(this.operator);
                  if (i == v_lsce.size() - 1) {
                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  } else {
                     if (this.operator2.equalsIgnoreCase("ALL")) {
                        from_sqs.getWhereExpression().addOperator("AND");
                     } else {
                        from_sqs.getWhereExpression().addOperator("OR");
                     }

                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  }
               }
            }
         }

         if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.LeftJoin != null) {
            wi.setLeftJoin((String)null);
         }

         if (this.RightJoin != null) {
            wi.setRightJoin((String)null);
         }

         return wi;
      }
   }

   public WhereItem toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         this.modifyWhereItemsForLikeOperatorWithoutConstantOnRHS(false);
         if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toSqliteSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toSqliteSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereSubQuery != null) {
            this.rightWhereSubQuery.setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toSqlite());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toSqliteSelect(to_sqs, from_sqs));
            }
         }

         WhereColumn whereColumn;
         Vector newColumnExpression;
         if (this.operator != null) {
            this.modifyWhereExpForLikeOperator(wi, this.operator, "TEXT");
            if (this.operator.equalsIgnoreCase("^=")) {
               wi.setOperator("<>");
            } else if (this.operator.equalsIgnoreCase("!>")) {
               wi.setOperator("<=");
            } else if (this.operator.equalsIgnoreCase("!<")) {
               wi.setOperator(">=");
            } else if (this.operator.equalsIgnoreCase("<=>")) {
               wi.setOperator("=");
               wi.isNullSafeEqualsOperator = true;
            } else {
               Vector v_nsce;
               if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                  if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null) {
                     whereColumn = wi.getRightWhereExp();
                     v_nsce = new Vector();
                     newColumnExpression = whereColumn.getColumnExpression();
                     if (newColumnExpression != null && newColumnExpression.size() > 0 && !(newColumnExpression.elementAt(0) instanceof String)) {
                        v_nsce.addElement("(");

                        for(int i = 0; i < newColumnExpression.size(); ++i) {
                           v_nsce.addElement(newColumnExpression.elementAt(i));
                        }

                        v_nsce.addElement(")");
                        whereColumn.setColumnExpression(v_nsce);
                     }

                     wi.setOperator(this.operator);
                  } else if (this.operator.trim().equalsIgnoreCase("LNNVl") && this.lnnvlWhereExp != null) {
                     WhereExpression we1 = this.lnnvlWhereExp.toSqliteSelect(to_sqs, from_sqs);
                     this.convertLNNVLtoStatement(we1);
                     wi.setCaseStatementForLNNVLFunc(this.casestmtFromLNNVLClause);
                  } else if (this.operator.trim().equalsIgnoreCase("REGEXP_LIKE") && this.regExp != null) {
                     wi.setOperator("REGEXP_LIKE");
                     Vector newExp = new Vector();

                     for(int i = 0; i < this.regExp.size(); ++i) {
                        if (this.regExp.get(i) instanceof SelectColumn) {
                           SelectColumn sc1 = (SelectColumn)this.regExp.get(i);
                           newExp.add(sc1.toSqliteSelect(to_sqs, from_sqs));
                        } else {
                           newExp.add(this.regExp.get(i));
                        }
                     }

                     wi.setRegExp(newExp);
                  } else {
                     wi.setOperator(this.operator);
                  }
               } else {
                  if (this.operator.equalsIgnoreCase("MATCHES")) {
                     wi.setOperator("LIKE");
                  } else {
                     wi.setOperator("NOT LIKE");
                  }

                  whereColumn = wi.getRightWhereExp();
                  v_nsce = whereColumn.getColumnExpression();
                  newColumnExpression = new Vector();
                  if (v_nsce != null) {
                     String ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                     if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                     }

                     if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                     }

                     newColumnExpression.add(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(newColumnExpression);
                  }
               }
            }
         }

         if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            whereColumn = wi.getRightWhereExp();
            WhereColumn l_wc = wi.getLeftWhereExp();
            if (whereColumn.getColumnExpression() != null && whereColumn.getColumnExpression().size() != 1) {
               newColumnExpression = new Vector();
               Vector v_nrsce = new Vector();
               Vector v_lsce = l_wc.getColumnExpression();
               Vector v_rsce = whereColumn.getColumnExpression();
               Vector v_removedcomma_right_column_exp = new Vector();
               Vector v_removedcomma_left_column_exp = new Vector();

               int i;
               String s_ce;
               for(i = 0; i < v_lsce.size(); ++i) {
                  if (v_lsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_lsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                  }
               }

               for(i = 0; i < v_rsce.size(); ++i) {
                  if (v_rsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_rsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                  }
               }

               newColumnExpression.addElement(v_removedcomma_left_column_exp.elementAt(0));
               v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
               l_wc.setColumnExpression(newColumnExpression);
               whereColumn.setColumnExpression(v_nrsce);

               for(i = 1; i < v_removedcomma_right_column_exp.size(); ++i) {
                  WhereItem n_wi = new WhereItem();
                  WhereColumn l_nwc = new WhereColumn();
                  WhereColumn r_nwc = new WhereColumn();
                  Vector v_lnsc = new Vector();
                  Vector v_rnsc = new Vector();
                  v_lnsc.addElement(v_removedcomma_left_column_exp.elementAt(0));
                  v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(i));
                  l_nwc.setColumnExpression(v_lnsc);
                  r_nwc.setColumnExpression(v_rnsc);
                  n_wi.setLeftWhereExp(l_nwc);
                  n_wi.setRightWhereExp(r_nwc);
                  n_wi.setOperator(this.operator);
                  if (i == v_lsce.size() - 1) {
                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  } else {
                     if (this.operator2.equalsIgnoreCase("ALL")) {
                        from_sqs.getWhereExpression().addOperator("AND");
                     } else {
                        from_sqs.getWhereExpression().addOperator("OR");
                     }

                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  }
               }
            }
         }

         if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.LeftJoin != null) {
            wi.setLeftJoin((String)null);
         }

         if (this.RightJoin != null) {
            wi.setRightJoin((String)null);
         }

         return wi;
      }
   }

   public WhereItem toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         this.modifyWhereItemsForLikeOperatorWithoutConstantOnRHS(false);
         if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toExcelSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toExcelSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereSubQuery != null) {
            this.rightWhereSubQuery.setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toExcel());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toExcelSelect(to_sqs, from_sqs));
            }
         }

         Vector v_lsce;
         WhereColumn whereColumn;
         Vector newColumnExpression;
         if (this.operator != null) {
            Vector newExp;
            if (this.operator.equalsIgnoreCase("LIKE") || this.operator.equalsIgnoreCase("NOT LIKE") || this.operator.equalsIgnoreCase("MATCHES") || this.operator.equalsIgnoreCase("NOT MATCHES") || this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
               newExp = null;
               if (this.checkForCasting(wi.getLeftWhereExp().getColumnExpression())) {
                  cast castFunction = new cast();
                  TableColumn tc1 = new TableColumn();
                  CharacterClass charClass = new CharacterClass();
                  tc1.setColumnName("CAST");
                  castFunction.setFunctionName(tc1);
                  castFunction.setAsDatatype("AS");
                  charClass.setDatatypeName("TEXT");
                  v_lsce = new Vector();
                  SelectColumn sc = new SelectColumn();
                  sc.setColumnExpression(wi.getLeftWhereExp().getColumnExpression());
                  v_lsce.add(0, sc);
                  v_lsce.add(1, charClass);
                  castFunction.setFunctionArguments(v_lsce);
                  newExp = new Vector();
                  newExp.add(0, castFunction.toExcelSelect(to_sqs, from_sqs));
                  wi.getLeftWhereExp().setColumnExpression(newExp);
               }
            }

            if (this.operator.equalsIgnoreCase("^=")) {
               wi.setOperator("<>");
            } else if (this.operator.equalsIgnoreCase("!>")) {
               wi.setOperator("<=");
            } else if (this.operator.equalsIgnoreCase("!<")) {
               wi.setOperator(">=");
            } else if (this.operator.equalsIgnoreCase("<=>")) {
               wi.setOperator("=");
               wi.isNullSafeEqualsOperator = true;
            } else {
               Vector v_nsce;
               if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                  if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null) {
                     whereColumn = wi.getRightWhereExp();
                     v_nsce = new Vector();
                     newColumnExpression = whereColumn.getColumnExpression();
                     if (newColumnExpression != null && newColumnExpression.size() > 0 && !(newColumnExpression.elementAt(0) instanceof String)) {
                        v_nsce.addElement("(");

                        for(int i = 0; i < newColumnExpression.size(); ++i) {
                           v_nsce.addElement(newColumnExpression.elementAt(i));
                        }

                        v_nsce.addElement(")");
                        whereColumn.setColumnExpression(v_nsce);
                     }

                     wi.setOperator(this.operator);
                  } else if (this.operator.trim().equalsIgnoreCase("LNNVl") && this.lnnvlWhereExp != null) {
                     WhereExpression we1 = this.lnnvlWhereExp.toExcelSelect(to_sqs, from_sqs);
                     this.convertLNNVLtoStatement(we1);
                     wi.setCaseStatementForLNNVLFunc(this.casestmtFromLNNVLClause);
                  } else if (this.operator.trim().equalsIgnoreCase("REGEXP_LIKE") && this.regExp != null) {
                     wi.setOperator("REGEXP_LIKE");
                     newExp = new Vector();

                     for(int i = 0; i < this.regExp.size(); ++i) {
                        if (this.regExp.get(i) instanceof SelectColumn) {
                           SelectColumn sc1 = (SelectColumn)this.regExp.get(i);
                           newExp.add(sc1.toExcelSelect(to_sqs, from_sqs));
                        } else {
                           newExp.add(this.regExp.get(i));
                        }
                     }

                     wi.setRegExp(newExp);
                  } else {
                     wi.setOperator(this.operator);
                  }
               } else {
                  if (this.operator.equalsIgnoreCase("MATCHES")) {
                     wi.setOperator("LIKE");
                  } else {
                     wi.setOperator("NOT LIKE");
                  }

                  whereColumn = wi.getRightWhereExp();
                  v_nsce = whereColumn.getColumnExpression();
                  newColumnExpression = new Vector();
                  if (v_nsce != null) {
                     String ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                     if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                     }

                     if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                     }

                     newColumnExpression.add(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(newColumnExpression);
                  }
               }
            }
         }

         if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            whereColumn = wi.getRightWhereExp();
            WhereColumn l_wc = wi.getLeftWhereExp();
            if (whereColumn.getColumnExpression() != null && whereColumn.getColumnExpression().size() != 1) {
               newColumnExpression = new Vector();
               Vector v_nrsce = new Vector();
               v_lsce = l_wc.getColumnExpression();
               Vector v_rsce = whereColumn.getColumnExpression();
               Vector v_removedcomma_right_column_exp = new Vector();
               Vector v_removedcomma_left_column_exp = new Vector();

               int i;
               String s_ce;
               for(i = 0; i < v_lsce.size(); ++i) {
                  if (v_lsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_lsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                  }
               }

               for(i = 0; i < v_rsce.size(); ++i) {
                  if (v_rsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_rsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                  }
               }

               newColumnExpression.addElement(v_removedcomma_left_column_exp.elementAt(0));
               v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
               l_wc.setColumnExpression(newColumnExpression);
               whereColumn.setColumnExpression(v_nrsce);

               for(i = 1; i < v_removedcomma_right_column_exp.size(); ++i) {
                  WhereItem n_wi = new WhereItem();
                  WhereColumn l_nwc = new WhereColumn();
                  WhereColumn r_nwc = new WhereColumn();
                  Vector v_lnsc = new Vector();
                  Vector v_rnsc = new Vector();
                  v_lnsc.addElement(v_removedcomma_left_column_exp.elementAt(0));
                  v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(i));
                  l_nwc.setColumnExpression(v_lnsc);
                  r_nwc.setColumnExpression(v_rnsc);
                  n_wi.setLeftWhereExp(l_nwc);
                  n_wi.setRightWhereExp(r_nwc);
                  n_wi.setOperator(this.operator);
                  if (i == v_lsce.size() - 1) {
                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  } else {
                     if (this.operator2.equalsIgnoreCase("ALL")) {
                        from_sqs.getWhereExpression().addOperator("AND");
                     } else {
                        from_sqs.getWhereExpression().addOperator("OR");
                     }

                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  }
               }
            }
         }

         if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.LeftJoin != null) {
            wi.setLeftJoin((String)null);
         }

         if (this.RightJoin != null) {
            wi.setRightJoin((String)null);
         }

         return wi;
      }
   }

   public WhereItem toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         this.modifyWhereItemsForLikeOperatorWithoutConstantOnRHS(false);
         if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toMsAccessJdbcSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toMsAccessJdbcSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereSubQuery != null) {
            this.rightWhereSubQuery.setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toMsAccessJdbc());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toMsAccessJdbcSelect(to_sqs, from_sqs));
            }
         }

         Vector v_lsce;
         WhereColumn whereColumn;
         Vector newColumnExpression;
         if (this.operator != null) {
            Vector newExp;
            if (this.operator.equalsIgnoreCase("LIKE") || this.operator.equalsIgnoreCase("NOT LIKE") || this.operator.equalsIgnoreCase("MATCHES") || this.operator.equalsIgnoreCase("NOT MATCHES") || this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
               newExp = null;
               if (this.checkForCasting(wi.getLeftWhereExp().getColumnExpression())) {
                  cast castFunction = new cast();
                  TableColumn tc1 = new TableColumn();
                  CharacterClass charClass = new CharacterClass();
                  tc1.setColumnName("CAST");
                  castFunction.setFunctionName(tc1);
                  castFunction.setAsDatatype("AS");
                  charClass.setDatatypeName("VARCHAR");
                  v_lsce = new Vector();
                  SelectColumn sc = new SelectColumn();
                  sc.setColumnExpression(wi.getLeftWhereExp().getColumnExpression());
                  v_lsce.add(0, sc);
                  v_lsce.add(1, charClass);
                  castFunction.setFunctionArguments(v_lsce);
                  newExp = new Vector();
                  newExp.add(0, castFunction.toMsAccessJdbcSelect(to_sqs, from_sqs));
                  wi.getLeftWhereExp().setColumnExpression(newExp);
               }
            }

            if (this.operator.equalsIgnoreCase("^=")) {
               wi.setOperator("<>");
            } else if (this.operator.equalsIgnoreCase("!>")) {
               wi.setOperator("<=");
            } else if (this.operator.equalsIgnoreCase("!<")) {
               wi.setOperator(">=");
            } else if (this.operator.equalsIgnoreCase("<=>")) {
               wi.setOperator("=");
               wi.isNullSafeEqualsOperator = true;
            } else {
               Vector v_nsce;
               if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                  if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null) {
                     whereColumn = wi.getRightWhereExp();
                     v_nsce = new Vector();
                     newColumnExpression = whereColumn.getColumnExpression();
                     if (newColumnExpression != null && newColumnExpression.size() > 0 && !(newColumnExpression.elementAt(0) instanceof String)) {
                        v_nsce.addElement("(");

                        for(int i = 0; i < newColumnExpression.size(); ++i) {
                           v_nsce.addElement(newColumnExpression.elementAt(i));
                        }

                        v_nsce.addElement(")");
                        whereColumn.setColumnExpression(v_nsce);
                     }

                     wi.setOperator(this.operator);
                  } else if (this.operator.trim().equalsIgnoreCase("LNNVl") && this.lnnvlWhereExp != null) {
                     WhereExpression we1 = this.lnnvlWhereExp.toMsAccessJdbcSelect(to_sqs, from_sqs);
                     this.convertLNNVLtoStatement(we1);
                     wi.setCaseStatementForLNNVLFunc(this.casestmtFromLNNVLClause);
                  } else if (this.operator.trim().equalsIgnoreCase("REGEXP_LIKE") && this.regExp != null) {
                     wi.setOperator("REGEXP_LIKE");
                     newExp = new Vector();

                     for(int i = 0; i < this.regExp.size(); ++i) {
                        if (this.regExp.get(i) instanceof SelectColumn) {
                           SelectColumn sc1 = (SelectColumn)this.regExp.get(i);
                           newExp.add(sc1.toMsAccessJdbcSelect(to_sqs, from_sqs));
                        } else {
                           newExp.add(this.regExp.get(i));
                        }
                     }

                     wi.setRegExp(newExp);
                  } else {
                     wi.setOperator(this.operator);
                  }
               } else {
                  if (this.operator.equalsIgnoreCase("MATCHES")) {
                     wi.setOperator("LIKE");
                  } else {
                     wi.setOperator("NOT LIKE");
                  }

                  whereColumn = wi.getRightWhereExp();
                  v_nsce = whereColumn.getColumnExpression();
                  newColumnExpression = new Vector();
                  if (v_nsce != null) {
                     String ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                     if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                     }

                     if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                     }

                     newColumnExpression.add(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(newColumnExpression);
                  }
               }
            }
         }

         if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            whereColumn = wi.getRightWhereExp();
            WhereColumn l_wc = wi.getLeftWhereExp();
            if (whereColumn.getColumnExpression() != null && whereColumn.getColumnExpression().size() != 1) {
               newColumnExpression = new Vector();
               Vector v_nrsce = new Vector();
               v_lsce = l_wc.getColumnExpression();
               Vector v_rsce = whereColumn.getColumnExpression();
               Vector v_removedcomma_right_column_exp = new Vector();
               Vector v_removedcomma_left_column_exp = new Vector();

               int i;
               String s_ce;
               for(i = 0; i < v_lsce.size(); ++i) {
                  if (v_lsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_lsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                  }
               }

               for(i = 0; i < v_rsce.size(); ++i) {
                  if (v_rsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_rsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                  }
               }

               newColumnExpression.addElement(v_removedcomma_left_column_exp.elementAt(0));
               v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
               l_wc.setColumnExpression(newColumnExpression);
               whereColumn.setColumnExpression(v_nrsce);

               for(i = 1; i < v_removedcomma_right_column_exp.size(); ++i) {
                  WhereItem n_wi = new WhereItem();
                  WhereColumn l_nwc = new WhereColumn();
                  WhereColumn r_nwc = new WhereColumn();
                  Vector v_lnsc = new Vector();
                  Vector v_rnsc = new Vector();
                  v_lnsc.addElement(v_removedcomma_left_column_exp.elementAt(0));
                  v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(i));
                  l_nwc.setColumnExpression(v_lnsc);
                  r_nwc.setColumnExpression(v_rnsc);
                  n_wi.setLeftWhereExp(l_nwc);
                  n_wi.setRightWhereExp(r_nwc);
                  n_wi.setOperator(this.operator);
                  if (i == v_lsce.size() - 1) {
                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  } else {
                     if (this.operator2.equalsIgnoreCase("ALL")) {
                        from_sqs.getWhereExpression().addOperator("AND");
                     } else {
                        from_sqs.getWhereExpression().addOperator("OR");
                     }

                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  }
               }
            }
         }

         if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.LeftJoin != null) {
            wi.setLeftJoin((String)null);
         }

         if (this.RightJoin != null) {
            wi.setRightJoin((String)null);
         }

         return wi;
      }
   }

   public WhereItem toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         this.modifyWhereItemsForLikeOperatorWithoutConstantOnRHS(false);
         this.modifyWhereItemsForEqualAndNotEqualsOperationWithEmptyStringOnRHS();
         this.modifyWhereItemsForStringLiteralsOnRHS(from_sqs != null && from_sqs.getBooleanValues("can.cast.string.literal.to.text.in.where.item"));
         if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toPostgreSQLSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toPostgreSQLSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereSubQuery != null) {
            if (from_sqs != null) {
               this.rightWhereSubQuery.setReportsMeta(from_sqs.getReportsMeta());
               this.rightWhereSubQuery.setAmazonRedShiftFlag(from_sqs.isAmazonRedShift());
               this.rightWhereSubQuery.setPgsqlFlag(from_sqs.isPgsqlLive());
               this.rightWhereSubQuery.setVerticaFlag(from_sqs.isVerticaDb());
               this.rightWhereSubQuery.setMSAzureFlag(from_sqs.isMSAzure());
               this.rightWhereSubQuery.setMSAzureWareHouseFlag(from_sqs.isMSAzureWareHouse());
               this.rightWhereSubQuery.setOracleLiveFlag(from_sqs.isOracleLive());
               this.rightWhereSubQuery.setDenodoFlag(from_sqs.isDenodo());
               this.rightWhereSubQuery.setMySqlLiveFlag(from_sqs.isMySqlLive());
               this.rightWhereSubQuery.setMongoDbFlag(from_sqs.isMongoDb());
               this.rightWhereSubQuery.setQueryConversionPropHandler(from_sqs.getQueryConvPropHandler());
            }

            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toPostgreSQL());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toPostgreSQLSelect(to_sqs, from_sqs));
            }
         }

         WhereColumn whereColumn;
         Vector newColumnExpression;
         if (this.operator != null) {
            this.modifyWhereExpForLikeOperator(wi, this.operator, StringFunctions.getStringDataType(from_sqs));
            if (this.operator.equalsIgnoreCase("^=")) {
               wi.setOperator("<>");
            } else if (this.operator.equalsIgnoreCase("!>")) {
               wi.setOperator("<=");
            } else if (this.operator.equalsIgnoreCase("!<")) {
               wi.setOperator(">=");
            } else if (this.operator.equalsIgnoreCase("<=>")) {
               if (isPostgreLiveDbs) {
                  wi.setOperator("=");
                  wi.isNullSafeEqualsOperator = true;
               } else {
                  wi.setOperator("IS NOT DISTINCT FROM");
               }
            } else {
               Vector v_nsce;
               if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                  if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null) {
                     whereColumn = wi.getRightWhereExp();
                     v_nsce = new Vector();
                     newColumnExpression = whereColumn.getColumnExpression();
                     if (newColumnExpression != null && newColumnExpression.size() > 0 && !(newColumnExpression.elementAt(0) instanceof String)) {
                        v_nsce.addElement("(");

                        for(int i = 0; i < newColumnExpression.size(); ++i) {
                           v_nsce.addElement(newColumnExpression.elementAt(i));
                        }

                        v_nsce.addElement(")");
                        whereColumn.setColumnExpression(v_nsce);
                     }

                     wi.setOperator(this.operator);
                  } else if (!this.operator.equalsIgnoreCase("REGEXP") && !this.operator.equalsIgnoreCase("RLIKE")) {
                     if (!this.operator.equalsIgnoreCase("NOT REGEXP") && !this.operator.equalsIgnoreCase("NOT RLIKE")) {
                        if (this.operator.trim().equalsIgnoreCase("LNNVl") && this.lnnvlWhereExp != null) {
                           WhereExpression we1 = this.lnnvlWhereExp.toPostgreSQLSelect(to_sqs, from_sqs);
                           this.convertLNNVLtoStatement(we1);
                           wi.setCaseStatementForLNNVLFunc(this.casestmtFromLNNVLClause);
                        } else if (this.operator.trim().equalsIgnoreCase("REGEXP_LIKE") && this.regExp != null) {
                           wi.setOperator("REGEXP_LIKE");
                           Vector newExp = new Vector();

                           for(int i = 0; i < this.regExp.size(); ++i) {
                              if (this.regExp.get(i) instanceof SelectColumn) {
                                 SelectColumn sc1 = (SelectColumn)this.regExp.get(i);
                                 newExp.add(sc1.toPostgreSQLSelect(to_sqs, from_sqs));
                              } else {
                                 newExp.add(this.regExp.get(i));
                              }
                           }

                           wi.setRegExp(newExp);
                        } else {
                           wi.setOperator(this.operator);
                        }
                     } else {
                        wi.setOperator("!~*");
                     }
                  } else {
                     wi.setOperator("~*");
                  }
               } else {
                  if (this.operator.equalsIgnoreCase("MATCHES")) {
                     wi.setOperator("LIKE");
                  } else {
                     wi.setOperator("NOT LIKE");
                  }

                  whereColumn = wi.getRightWhereExp();
                  v_nsce = whereColumn.getColumnExpression();
                  newColumnExpression = new Vector();
                  if (v_nsce != null) {
                     String ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                     if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                     }

                     if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                     }

                     newColumnExpression.add(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(newColumnExpression);
                  }
               }
            }
         }

         if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            whereColumn = wi.getRightWhereExp();
            WhereColumn l_wc = wi.getLeftWhereExp();
            if (whereColumn.getColumnExpression() != null && whereColumn.getColumnExpression().size() != 1) {
               newColumnExpression = new Vector();
               Vector v_nrsce = new Vector();
               Vector v_lsce = l_wc.getColumnExpression();
               Vector v_rsce = whereColumn.getColumnExpression();
               Vector v_removedcomma_right_column_exp = new Vector();
               Vector v_removedcomma_left_column_exp = new Vector();

               int i;
               String s_ce;
               for(i = 0; i < v_lsce.size(); ++i) {
                  if (v_lsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_lsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                  }
               }

               for(i = 0; i < v_rsce.size(); ++i) {
                  if (v_rsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_rsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                  }
               }

               newColumnExpression.addElement(v_removedcomma_left_column_exp.elementAt(0));
               v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
               l_wc.setColumnExpression(newColumnExpression);
               whereColumn.setColumnExpression(v_nrsce);

               for(i = 1; i < v_removedcomma_right_column_exp.size(); ++i) {
                  WhereItem n_wi = new WhereItem();
                  WhereColumn l_nwc = new WhereColumn();
                  WhereColumn r_nwc = new WhereColumn();
                  Vector v_lnsc = new Vector();
                  Vector v_rnsc = new Vector();
                  v_lnsc.addElement(v_removedcomma_left_column_exp.elementAt(0));
                  v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(i));
                  l_nwc.setColumnExpression(v_lnsc);
                  r_nwc.setColumnExpression(v_rnsc);
                  n_wi.setLeftWhereExp(l_nwc);
                  n_wi.setRightWhereExp(r_nwc);
                  n_wi.setOperator(this.operator);
                  if (i == v_lsce.size() - 1) {
                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  } else {
                     if (this.operator2.equalsIgnoreCase("ALL")) {
                        from_sqs.getWhereExpression().addOperator("AND");
                     } else {
                        from_sqs.getWhereExpression().addOperator("OR");
                     }

                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  }
               }
            }
         }

         if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.LeftJoin != null) {
            wi.setLeftJoin((String)null);
         }

         if (this.RightJoin != null) {
            wi.setRightJoin((String)null);
         }

         return wi;
      }
   }

   public WhereItem toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         boolean noChangeRequired = false;
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         Vector rightColumnExpression;
         if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toMSSQLServerSelect(to_sqs, from_sqs));
            rightColumnExpression = this.leftWhereExp.getColumnExpression();
            if (rightColumnExpression != null && this.containsColumnVariable(rightColumnExpression, false)) {
               noChangeRequired = true;
            }
         }

         if (this.rightWhereExp != null && !this.isContainsFunction) {
            wi.setRightWhereExp(this.rightWhereExp.toMSSQLServerSelect(to_sqs, from_sqs));
            rightColumnExpression = this.rightWhereExp.getColumnExpression();
            if (rightColumnExpression != null && this.containsColumnVariable(rightColumnExpression, false)) {
               noChangeRequired = true;
            }
         }

         if (!SwisSQLAPI.MSSQLSERVER_THETA) {
            if (this.LeftJoin != null) {
               wi.setLeftJoin((String)null);
            }

            if (this.RightJoin != null) {
               wi.setLeftJoin((String)null);
            }
         } else if (!noChangeRequired) {
            if (this.LeftJoin != null) {
               if (this.LeftJoin.equalsIgnoreCase("+")) {
                  wi.setLeftJoin("*");
               } else {
                  wi.setLeftJoin(this.LeftJoin);
               }
            }

            if (this.RightJoin != null) {
               if (this.RightJoin.equalsIgnoreCase("+")) {
                  wi.setRightJoin("*");
               } else {
                  wi.setRightJoin(this.RightJoin);
               }
            }
         }

         if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toMSSQLServer());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toMSSQLServerSelect(to_sqs, from_sqs));
            }
         }

         Vector v_removedcomma_left_column_exp;
         Vector v_sitl;
         WhereColumn r_wc_1;
         Vector v_rce;
         WhereColumn whereColumn;
         WhereColumn l_wc;
         Vector v_sce;
         Vector v_nsce;
         if (this.operator != null && !this.isContainsFunction) {
            if (this.operator.equalsIgnoreCase("^=")) {
               wi.setOperator("<>");
            } else if (this.operator.equalsIgnoreCase("<=>")) {
               wi.setOperator("=");
               wi.isNullSafeEqualsOperator = true;
            } else {
               Vector rightExpr;
               String ColumnExpressionAsString;
               if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                  wi.setOperator("LIKE");
                  if (wi.getRightWhereSubQuery() != null) {
                     throw new ConvertException("Conversion failure.. Subquery can't be converted");
                  }

                  whereColumn = wi.getRightWhereExp();
                  rightExpr = whereColumn.getColumnExpression();
                  v_removedcomma_left_column_exp = new Vector();
                  if (rightExpr != null) {
                     if (rightExpr.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expressions can't be converted");
                     }

                     ColumnExpressionAsString = rightExpr.elementAt(0).toString();
                     if (ColumnExpressionAsString.charAt(0) == '\'') {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('\'', ' ').trim();
                        ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                     } else if (ColumnExpressionAsString.charAt(0) == '"') {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('"', ' ').trim();
                        ColumnExpressionAsString = "\"%" + ColumnExpressionAsString + "%\"";
                     } else {
                        ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                     }

                     v_removedcomma_left_column_exp.addElement(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(v_removedcomma_left_column_exp);
                  }

                  wi.setRightWhereExp(whereColumn);
               } else if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                  String f_tablename_1;
                  if ((this.operator.equalsIgnoreCase("BETWEEN") || this.operator.equalsIgnoreCase("NOT BETWEEN")) && wi.getRightWhereExp() != null) {
                     whereColumn = wi.getRightWhereExp();
                     rightExpr = whereColumn.getColumnExpression();
                     if (rightExpr != null) {
                        v_removedcomma_left_column_exp = new Vector();

                        for(int i = 0; i < rightExpr.size(); ++i) {
                           if (rightExpr.get(i) instanceof String) {
                              f_tablename_1 = (String)rightExpr.get(i);
                              if (!f_tablename_1.equalsIgnoreCase("(") && !f_tablename_1.equalsIgnoreCase(")")) {
                                 v_removedcomma_left_column_exp.add(f_tablename_1);
                              }
                           } else {
                              v_removedcomma_left_column_exp.add(rightExpr.get(i));
                           }
                        }

                        whereColumn.setColumnExpression(v_removedcomma_left_column_exp);
                     }

                     wi.setOperator(this.operator);
                  } else if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null && wi.getRightWhereSubQuery() == null) {
                     whereColumn = wi.getRightWhereExp();
                     l_wc = wi.getLeftWhereExp();
                     WhereColumn wc = wi.getRightWhereExp();
                     v_nsce = new Vector();
                     v_sce = wc.getColumnExpression();
                     if (v_sce != null && v_sce.size() > 0 && !(v_sce.elementAt(0) instanceof String)) {
                        v_nsce.addElement("(");

                        for(int i = 0; i < v_sce.size(); ++i) {
                           v_nsce.addElement(v_sce.elementAt(i));
                        }

                        v_nsce.addElement(")");
                        wc.setColumnExpression(v_nsce);
                     }

                     wi.setOperator(this.operator);
                  } else if ((this.operator.equalsIgnoreCase("IN") || this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereSubQuery() != null) {
                     String f_tablename = null;
                     if (from_sqs != null) {
                        rightExpr = null;
                        if (from_sqs.getFromClause() != null && from_sqs.getFromClause().getFromItemList().elementAt(0) instanceof FromTable) {
                           FromTable ft = (FromTable)from_sqs.getFromClause().getFromItemList().elementAt(0);
                           if (ft.getAliasName() != null) {
                              f_tablename = ft.getAliasName();
                           } else if (ft.getTableName() instanceof String) {
                              f_tablename = ft.getTableName().toString();
                              int pos = f_tablename.indexOf(".");
                              if (pos == -1) {
                                 pos = f_tablename.indexOf("..");
                              }

                              f_tablename = f_tablename.substring(pos + 1);
                           } else {
                              f_tablename = ft.getTableName().toString();
                           }
                        }
                     }

                     l_wc = wi.getLeftWhereExp();
                     if (l_wc.getColumnExpression() != null && l_wc.getColumnExpression().size() != 1 && l_wc.getColumnExpression().contains(",")) {
                        v_removedcomma_left_column_exp = new Vector();
                        this.removeCommaBracesFromExpressionList(l_wc, v_removedcomma_left_column_exp, f_tablename);
                        wi.setLeftWhereExp((WhereColumn)null);
                        if (this.operator.equalsIgnoreCase("IN")) {
                           wi.setOperator("EXISTS");
                        } else if (this.operator.equalsIgnoreCase("NOT IN")) {
                           wi.setOperator("NOT EXISTS");
                        }

                        SelectQueryStatement sqs = wi.getRightWhereSubQuery();
                        f_tablename_1 = null;
                        FromTable ft_1 = (FromTable)sqs.getFromClause().getFromItemList().elementAt(0);
                        if (ft_1.getAliasName() == null) {
                        }

                        if (ft_1.getAliasName() != null) {
                           f_tablename_1 = ft_1.getAliasName();
                        } else {
                           f_tablename_1 = ft_1.getTableName().toString();
                        }

                        v_sitl = sqs.getSelectStatement().getSelectItemList();

                        for(int i = 0; i < v_sitl.size(); ++i) {
                           WhereItem wi_1 = new WhereItem();
                           WhereColumn l_wc_1 = new WhereColumn();
                           r_wc_1 = new WhereColumn();
                           Vector v_lce = new Vector();
                           v_rce = new Vector();
                           SelectColumn sc = (SelectColumn)v_sitl.elementAt(i);
                           Vector v_ce = sc.getColumnExpression();
                           String where_column_name = null;
                           if (v_ce.elementAt(0) instanceof String) {
                              where_column_name = (String)v_ce.elementAt(0);
                           } else if (v_ce.elementAt(0) instanceof TableColumn) {
                              TableColumn tc = (TableColumn)v_ce.elementAt(0);
                              if (tc.getTableName() == null) {
                                 tc.setTableName(f_tablename_1);
                              }

                              where_column_name = tc.toString();
                           } else if (v_ce.elementAt(0) instanceof SelectColumn) {
                              SelectColumn selectColumnInsideSelectColumn = (SelectColumn)v_ce.elementAt(0);
                              Vector newColumnExp = selectColumnInsideSelectColumn.getColumnExpression();
                              if (newColumnExp.elementAt(0) instanceof String) {
                                 where_column_name = (String)newColumnExp.elementAt(0);
                              } else if (newColumnExp.elementAt(0) instanceof TableColumn) {
                                 TableColumn tableColumn = (TableColumn)newColumnExp.elementAt(0);
                                 if (tableColumn.getTableName() == null) {
                                    tableColumn.setTableName(f_tablename_1);
                                 }

                                 where_column_name = tableColumn.toString();
                              }
                           }

                           v_lce.addElement(where_column_name);
                           l_wc_1.setColumnExpression(v_lce);
                           v_rce.addElement(v_removedcomma_left_column_exp.elementAt(i));
                           r_wc_1.setColumnExpression(v_rce);
                           wi_1.setOperator("=");
                           wi_1.setLeftWhereExp(l_wc_1);
                           wi_1.setRightWhereExp(r_wc_1);
                           if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().equalsIgnoreCase("")) {
                              sqs.getWhereExpression().addWhereItem(wi_1);
                              sqs.getWhereExpression().addOperator("AND");
                           } else {
                              WhereExpression we = new WhereExpression();
                              we.addWhereItem(wi_1);
                              sqs.setWhereExpression(we);
                           }
                        }

                        wi.setRightWhereSubQuery(sqs);
                     } else {
                        wi.setOperator(this.operator);
                     }
                  } else if (!this.getOperator().trim().equals("=") && !this.getOperator().trim().equals("!=") && !this.getOperator().trim().equals("<>")) {
                     wi.setOperator(this.operator);
                  } else {
                     rightColumnExpression = this.rightWhereExp.getColumnExpression();
                     if (rightColumnExpression != null && rightColumnExpression.size() == 1) {
                        Object obj = rightColumnExpression.get(0);
                        if (obj instanceof String && obj.toString().trim().equalsIgnoreCase("null")) {
                           String op = this.getOperator();
                           if (op != null && op.equals("=")) {
                              op = " IS ";
                           }

                           if (op != null && (op.equals("!=") || op.equals("<>"))) {
                              op = " IS NOT ";
                           }

                           wi.setOperator(op);
                        } else {
                           wi.setOperator(this.operator);
                        }
                     } else {
                        wi.setOperator(this.operator);
                     }
                  }
               } else {
                  if (this.operator.equalsIgnoreCase("MATCHES")) {
                     wi.setOperator("LIKE");
                  } else {
                     wi.setOperator("NOT LIKE");
                  }

                  whereColumn = wi.getRightWhereExp();
                  rightExpr = whereColumn.getColumnExpression();
                  v_removedcomma_left_column_exp = new Vector();
                  if (rightExpr != null) {
                     ColumnExpressionAsString = rightExpr.elementAt(0).toString();
                     if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                     }

                     if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                     }

                     v_removedcomma_left_column_exp.add(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(v_removedcomma_left_column_exp);
                  }
               }
            }
         }

         if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            whereColumn = wi.getRightWhereExp();
            l_wc = wi.getLeftWhereExp();
            if (whereColumn.getColumnExpression() != null && whereColumn.getColumnExpression().size() != 1) {
               v_removedcomma_left_column_exp = new Vector();
               v_nsce = new Vector();
               v_sce = l_wc.getColumnExpression();
               Vector v_rsce = whereColumn.getColumnExpression();
               v_sitl = new Vector();
               Vector v_removedcomma_left_column_exp = new Vector();

               int i;
               String s_ce;
               for(i = 0; i < v_sce.size(); ++i) {
                  if (v_sce.elementAt(i) instanceof String) {
                     s_ce = (String)v_sce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_left_column_exp.addElement(v_sce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_left_column_exp.addElement(v_sce.elementAt(i));
                  }
               }

               for(i = 0; i < v_rsce.size(); ++i) {
                  if (v_rsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_rsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_sitl.addElement(v_rsce.elementAt(i));
                     }
                  } else {
                     v_sitl.addElement(v_rsce.elementAt(i));
                  }
               }

               v_removedcomma_left_column_exp.addElement(v_removedcomma_left_column_exp.elementAt(0));
               v_nsce.addElement(v_sitl.elementAt(0));
               l_wc.setColumnExpression(v_removedcomma_left_column_exp);
               whereColumn.setColumnExpression(v_nsce);

               for(i = 1; i < v_sitl.size(); ++i) {
                  WhereItem n_wi = new WhereItem();
                  r_wc_1 = new WhereColumn();
                  WhereColumn r_nwc = new WhereColumn();
                  v_rce = new Vector();
                  Vector v_rnsc = new Vector();
                  v_rce.addElement(v_removedcomma_left_column_exp.elementAt(0));
                  v_rnsc.addElement(v_sitl.elementAt(i));
                  r_wc_1.setColumnExpression(v_rce);
                  r_nwc.setColumnExpression(v_rnsc);
                  n_wi.setLeftWhereExp(r_wc_1);
                  n_wi.setRightWhereExp(r_nwc);
                  n_wi.setOperator(this.operator);
                  if (i == v_sce.size() - 1) {
                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  } else {
                     if (this.operator2.equalsIgnoreCase("ALL")) {
                        from_sqs.getWhereExpression().addOperator("AND");
                     } else {
                        from_sqs.getWhereExpression().addOperator("OR");
                     }

                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  }
               }
            }
         }

         if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.isContainsFunction && (this.getOperator().trim().equals("=") || this.getOperator().trim().equals("<"))) {
            wi.setOperator1("NOT");
         }

         return wi;
      }
   }

   public WhereItem toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         boolean noChangeRequired = false;
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         Vector colExp;
         int i;
         Vector ft;
         if (this.leftWhereExp != null) {
            this.leftWhereExp.setObjectContext(this.context);
            colExp = this.leftWhereExp.getColumnExpression();
            if (colExp != null) {
               for(i = 0; i < colExp.size(); ++i) {
                  if (colExp.elementAt(i) instanceof TableColumn) {
                     ((TableColumn)colExp.elementAt(i)).setObjectContext(this.context);
                  } else if (colExp.elementAt(i) instanceof SelectColumn) {
                     ((SelectColumn)colExp.elementAt(i)).setObjectContext(this.context);
                  } else if (colExp.elementAt(i) instanceof FunctionCalls) {
                     ((FunctionCalls)colExp.elementAt(i)).setObjectContext(this.context);
                  }
               }
            }

            wi.setObjectContext(this.context);
            wi.setLeftWhereExp(this.leftWhereExp.toSybaseSelect(to_sqs, from_sqs));
            ft = this.leftWhereExp.getColumnExpression();
            if (ft != null && this.containsColumnVariable(ft, false)) {
               noChangeRequired = true;
            }
         }

         if (this.rightWhereExp != null) {
            this.rightWhereExp.setObjectContext(this.context);
            wi.setObjectContext(this.context);
            colExp = this.rightWhereExp.getColumnExpression();
            if (colExp != null) {
               for(i = 0; i < colExp.size(); ++i) {
                  if (colExp.elementAt(i) instanceof TableColumn) {
                     ((TableColumn)colExp.elementAt(i)).setObjectContext(this.context);
                  } else if (colExp.elementAt(i) instanceof SelectColumn) {
                     ((SelectColumn)colExp.elementAt(i)).setObjectContext(this.context);
                  } else if (colExp.elementAt(i) instanceof FunctionCalls) {
                     ((FunctionCalls)colExp.elementAt(i)).setObjectContext(this.context);
                  }
               }
            }

            wi.setRightWhereExp(this.rightWhereExp.toSybaseSelect(to_sqs, from_sqs));
            ft = this.rightWhereExp.getColumnExpression();
            int countVarOrConst = false;
            if (ft != null && this.containsColumnVariable(ft, false)) {
               noChangeRequired = true;
            }
         }

         if (!SwisSQLAPI.MSSQLSERVER_THETA) {
            if (this.LeftJoin != null) {
               wi.setLeftJoin((String)null);
            }

            if (this.RightJoin != null) {
               wi.setLeftJoin((String)null);
            }
         } else if (!noChangeRequired) {
            if (this.LeftJoin != null) {
               if (this.LeftJoin.equalsIgnoreCase("+")) {
                  wi.setLeftJoin("*");
               } else {
                  wi.setLeftJoin(this.LeftJoin);
               }
            }

            if (this.RightJoin != null) {
               if (this.RightJoin.equalsIgnoreCase("+")) {
                  wi.setRightJoin("*");
               } else {
                  wi.setRightJoin(this.RightJoin);
               }
            }
         }

         if (this.rightWhereSubQuery != null) {
            this.rightWhereSubQuery.setObjectContext(this.context);
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toSybase());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toSybaseSelect(to_sqs, from_sqs));
            }
         }

         Vector v_sitl;
         WhereColumn r_wc_1;
         Vector v_rce;
         WhereColumn whereColumn;
         Vector v_removedcomma_left_column_exp;
         WhereColumn l_wc;
         Vector v_sce;
         Vector v_nsce;
         Vector tempVector;
         if (this.operator != null) {
            if (this.operator.equalsIgnoreCase("^=")) {
               wi.setOperator("<>");
            } else {
               String ColumnExpressionAsString;
               if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                  wi.setOperator("LIKE");
                  if (wi.getRightWhereSubQuery() != null) {
                     throw new ConvertException("Conversion failure.. Subquery can't be converted");
                  }

                  whereColumn = wi.getRightWhereExp();
                  ft = whereColumn.getColumnExpression();
                  v_removedcomma_left_column_exp = new Vector();
                  if (ft != null) {
                     if (ft.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expressions can't be converted");
                     }

                     ColumnExpressionAsString = ft.elementAt(0).toString();
                     if (ColumnExpressionAsString.charAt(0) == '\'') {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('\'', ' ').trim();
                        ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                     } else if (ColumnExpressionAsString.charAt(0) == '"') {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('"', ' ').trim();
                        ColumnExpressionAsString = "\"%" + ColumnExpressionAsString + "%\"";
                     } else {
                        ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                     }

                     v_removedcomma_left_column_exp.addElement(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(v_removedcomma_left_column_exp);
                  }

                  wi.setRightWhereExp(whereColumn);
               } else {
                  String f_tablename_1;
                  if ((this.operator.equalsIgnoreCase("BETWEEN") || this.operator.equalsIgnoreCase("NOT BETWEEN")) && wi.getRightWhereExp() != null) {
                     whereColumn = wi.getRightWhereExp();
                     ft = whereColumn.getColumnExpression();
                     if (ft != null) {
                        v_removedcomma_left_column_exp = new Vector();

                        for(int i = 0; i < ft.size(); ++i) {
                           if (ft.get(i) instanceof String) {
                              f_tablename_1 = (String)ft.get(i);
                              if (!f_tablename_1.equalsIgnoreCase("(") && !f_tablename_1.equalsIgnoreCase(")")) {
                                 v_removedcomma_left_column_exp.add(f_tablename_1);
                              }
                           } else {
                              v_removedcomma_left_column_exp.add(ft.get(i));
                           }
                        }

                        whereColumn.setColumnExpression(v_removedcomma_left_column_exp);
                     }

                     wi.setOperator(this.operator);
                  } else if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                     if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null && wi.getRightWhereSubQuery() == null) {
                        whereColumn = wi.getRightWhereExp();
                        l_wc = wi.getLeftWhereExp();
                        WhereColumn wc = wi.getRightWhereExp();
                        v_nsce = new Vector();
                        v_sce = wc.getColumnExpression();
                        if (v_sce != null && v_sce.size() > 0 && !(v_sce.elementAt(0) instanceof String)) {
                           v_nsce.addElement("(");

                           for(int i = 0; i < v_sce.size(); ++i) {
                              v_nsce.addElement(v_sce.elementAt(i));
                           }

                           v_nsce.addElement(")");
                           wc.setColumnExpression(v_nsce);
                        }

                        wi.setOperator(this.operator);
                     } else if ((this.operator.equalsIgnoreCase("IN") || this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereSubQuery() != null) {
                        String f_tablename = null;
                        if (from_sqs != null) {
                           ft = null;
                           if (from_sqs.getFromClause().getFromItemList().elementAt(0) instanceof FromTable) {
                              FromTable ft = (FromTable)from_sqs.getFromClause().getFromItemList().elementAt(0);
                              if (ft.getAliasName() != null) {
                                 f_tablename = ft.getAliasName();
                              } else if (ft.getTableName() instanceof String) {
                                 f_tablename = ft.getTableName().toString();
                                 int pos = f_tablename.indexOf(".");
                                 if (pos == -1) {
                                    pos = f_tablename.indexOf("..");
                                 }

                                 f_tablename = f_tablename.substring(pos + 1);
                              } else {
                                 f_tablename = ft.getTableName().toString();
                              }
                           }
                        }

                        l_wc = wi.getLeftWhereExp();
                        if (l_wc.getColumnExpression() != null && l_wc.getColumnExpression().size() != 1 && l_wc.getColumnExpression().contains(",")) {
                           v_removedcomma_left_column_exp = new Vector();
                           this.removeCommaBracesFromExpressionList(l_wc, v_removedcomma_left_column_exp, f_tablename);
                           wi.setLeftWhereExp((WhereColumn)null);
                           if (this.operator.equalsIgnoreCase("IN")) {
                              wi.setOperator("EXISTS");
                           } else if (this.operator.equalsIgnoreCase("NOT IN")) {
                              wi.setOperator("NOT EXISTS");
                           }

                           SelectQueryStatement sqs = wi.getRightWhereSubQuery();
                           f_tablename_1 = null;
                           FromTable ft_1 = (FromTable)sqs.getFromClause().getFromItemList().elementAt(0);
                           if (ft_1.getAliasName() == null) {
                           }

                           if (ft_1.getAliasName() != null) {
                              f_tablename_1 = ft_1.getAliasName();
                           } else {
                              f_tablename_1 = ft_1.getTableName().toString();
                           }

                           v_sitl = sqs.getSelectStatement().getSelectItemList();

                           for(int i = 0; i < v_sitl.size(); ++i) {
                              WhereItem wi_1 = new WhereItem();
                              wi_1.setObjectContext(this.context);
                              WhereColumn l_wc_1 = new WhereColumn();
                              r_wc_1 = new WhereColumn();
                              Vector v_lce = new Vector();
                              v_rce = new Vector();
                              SelectColumn sc = (SelectColumn)v_sitl.elementAt(i);
                              Vector v_ce = sc.getColumnExpression();
                              String where_column_name = null;
                              if (v_ce.elementAt(0) instanceof String) {
                                 where_column_name = (String)v_ce.elementAt(0);
                              } else if (v_ce.elementAt(0) instanceof TableColumn) {
                                 TableColumn tc = (TableColumn)v_ce.elementAt(0);
                                 if (tc.getTableName() == null) {
                                    tc.setTableName(f_tablename_1);
                                 }

                                 where_column_name = tc.toString();
                              } else if (v_ce.elementAt(0) instanceof SelectColumn) {
                                 SelectColumn selectColumnInsideSelectColumn = (SelectColumn)v_ce.elementAt(0);
                                 Vector newColumnExp = selectColumnInsideSelectColumn.getColumnExpression();
                                 if (newColumnExp.elementAt(0) instanceof String) {
                                    where_column_name = (String)newColumnExp.elementAt(0);
                                 } else if (newColumnExp.elementAt(0) instanceof TableColumn) {
                                    TableColumn tableColumn = (TableColumn)newColumnExp.elementAt(0);
                                    if (tableColumn.getTableName() == null) {
                                       tableColumn.setTableName(f_tablename_1);
                                    }

                                    where_column_name = tableColumn.toString();
                                 }
                              }

                              v_lce.addElement(where_column_name);
                              l_wc_1.setColumnExpression(v_lce);
                              v_rce.addElement(v_removedcomma_left_column_exp.elementAt(i));
                              r_wc_1.setColumnExpression(v_rce);
                              wi_1.setOperator("=");
                              wi_1.setLeftWhereExp(l_wc_1);
                              wi_1.setRightWhereExp(r_wc_1);
                              if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().equalsIgnoreCase("")) {
                                 sqs.getWhereExpression().addWhereItem(wi_1);
                                 sqs.getWhereExpression().addOperator("AND");
                              } else {
                                 WhereExpression we = new WhereExpression();
                                 we.addWhereItem(wi_1);
                                 sqs.setWhereExpression(we);
                              }
                           }

                           tempVector = new Vector();
                           tempVector.add("*");
                           sqs.getSelectStatement().setSelectItemList(tempVector);
                           wi.setRightWhereSubQuery(sqs);
                        } else {
                           wi.setOperator(this.operator);
                        }
                     } else {
                        wi.setOperator(this.operator);
                     }
                  } else {
                     if (this.operator.equalsIgnoreCase("MATCHES")) {
                        wi.setOperator("LIKE");
                     } else {
                        wi.setOperator("NOT LIKE");
                     }

                     whereColumn = wi.getRightWhereExp();
                     ft = whereColumn.getColumnExpression();
                     v_removedcomma_left_column_exp = new Vector();
                     if (ft != null) {
                        ColumnExpressionAsString = ft.elementAt(0).toString();
                        if (ColumnExpressionAsString.indexOf("*") != -1) {
                           ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                        }

                        if (ColumnExpressionAsString.indexOf("_") != -1) {
                           ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                        }

                        v_removedcomma_left_column_exp.add(ColumnExpressionAsString);
                        whereColumn.setColumnExpression(v_removedcomma_left_column_exp);
                     }
                  }
               }
            }
         }

         if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            whereColumn = wi.getRightWhereExp();
            l_wc = wi.getLeftWhereExp();
            if (whereColumn.getColumnExpression() != null && whereColumn.getColumnExpression().size() != 1) {
               v_removedcomma_left_column_exp = new Vector();
               v_nsce = new Vector();
               v_sce = l_wc.getColumnExpression();
               Vector v_rsce = whereColumn.getColumnExpression();
               v_sitl = new Vector();
               tempVector = new Vector();

               int i;
               String s_ce;
               for(i = 0; i < v_sce.size(); ++i) {
                  if (v_sce.elementAt(i) instanceof String) {
                     s_ce = (String)v_sce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        tempVector.addElement(v_sce.elementAt(i));
                     }
                  } else {
                     tempVector.addElement(v_sce.elementAt(i));
                  }
               }

               for(i = 0; i < v_rsce.size(); ++i) {
                  if (v_rsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_rsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_sitl.addElement(v_rsce.elementAt(i));
                     }
                  } else {
                     v_sitl.addElement(v_rsce.elementAt(i));
                  }
               }

               v_removedcomma_left_column_exp.addElement(tempVector.elementAt(0));
               v_nsce.addElement(v_sitl.elementAt(0));
               l_wc.setColumnExpression(v_removedcomma_left_column_exp);
               whereColumn.setColumnExpression(v_nsce);

               for(i = 1; i < v_sitl.size(); ++i) {
                  WhereItem n_wi = new WhereItem();
                  r_wc_1 = new WhereColumn();
                  WhereColumn r_nwc = new WhereColumn();
                  v_rce = new Vector();
                  Vector v_rnsc = new Vector();
                  v_rce.addElement(tempVector.elementAt(0));
                  v_rnsc.addElement(v_sitl.elementAt(i));
                  r_wc_1.setColumnExpression(v_rce);
                  r_nwc.setColumnExpression(v_rnsc);
                  n_wi.setLeftWhereExp(r_wc_1);
                  n_wi.setRightWhereExp(r_nwc);
                  n_wi.setOperator(this.operator);
                  if (i == v_sce.size() - 1) {
                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  } else {
                     if (this.operator2.equalsIgnoreCase("ALL")) {
                        from_sqs.getWhereExpression().addOperator("AND");
                     } else {
                        from_sqs.getWhereExpression().addOperator("OR");
                     }

                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  }
               }
            }
         }

         if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         return wi;
      }
   }

   public void removeCommaBracesFromExpressionList(WhereColumn wc, Vector v_nce, String table_name) {
      Vector v_ce = wc.getColumnExpression();
      if (v_ce != null) {
         for(int i = 0; i < v_ce.size(); ++i) {
            if (v_ce.elementAt(i) instanceof String) {
               String s_ce = (String)v_ce.elementAt(i);
               if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                  v_nce.addElement(v_ce.elementAt(i));
               }
            } else if (v_ce.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)v_ce.elementAt(i);
               if (tc.getTableName() == null) {
                  tc.setTableName(table_name);
               }

               v_nce.addElement(tc);
            } else if (v_ce.elementAt(i) instanceof WhereColumn) {
               this.removeCommaBracesFromExpressionList((WhereColumn)((WhereColumn)v_ce.elementAt(i)), v_nce, table_name);
            }
         }
      }

   }

   public WhereItem toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         WhereColumn whereColumn;
         Vector columnExpression;
         Vector newColumnExpression;
         String ColumnExpressionAsString;
         int i;
         if (SwisSQLAPI.convert_OracleThetaJOIN_To_ANSIJOIN) {
            wi.setOpenBrace(this.openBraces);
            wi.setCloseBrace(this.closeBraces);
            if (this.beginOperator != null) {
               wi.setBeginOperator(this.beginOperator);
            }

            if (this.operator1 != null) {
               wi.setOperator1(this.operator1);
            }

            if (this.leftWhereExp != null) {
               wi.setLeftWhereExp(this.leftWhereExp.toOracleSelect(to_sqs, from_sqs));
            }

            if (this.rightWhereExp != null) {
               wi.setRightWhereExp(this.rightWhereExp.toOracleSelect(to_sqs, from_sqs));
            }

            if (this.rightWhereSubQuery != null) {
               if (from_sqs != null) {
                  this.rightWhereSubQuery.setCanUseOracleFetch(from_sqs.getCanUseOracleFetch());
               }

               wi.setRightWhereSubQuery(this.rightWhereSubQuery.toOracle());
               if (this.rightWhereSubQueryExp != null) {
                  wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toOracleSelect(to_sqs, from_sqs));
               }
            }

            if (this.operator != null) {
               if (!this.operator.equalsIgnoreCase("^=") && !this.operator.equalsIgnoreCase("!=")) {
                  if (this.operator.equalsIgnoreCase("!>")) {
                     wi.setOperator("<=");
                  } else if (this.operator.equalsIgnoreCase("!<")) {
                     wi.setOperator(">=");
                  } else if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                     wi.setOperator("LIKE");
                     if (wi.getRightWhereSubQuery() != null) {
                        throw new ConvertException("Conversion failure.. Subquery can't be converted");
                     }

                     whereColumn = wi.getRightWhereExp();
                     columnExpression = whereColumn.getColumnExpression();
                     newColumnExpression = new Vector();
                     if (columnExpression != null) {
                        if (columnExpression.size() != 1) {
                           throw new ConvertException("Conversion failure.. Expressions can't be converted");
                        }

                        ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                        if (ColumnExpressionAsString.charAt(0) == '\'') {
                           ColumnExpressionAsString = ColumnExpressionAsString.replace('\'', ' ').trim();
                           ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                        } else if (ColumnExpressionAsString.charAt(0) == '"') {
                           ColumnExpressionAsString = ColumnExpressionAsString.replace('"', ' ').trim();
                           ColumnExpressionAsString = "\"%" + ColumnExpressionAsString + "%\"";
                        } else {
                           ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                        }

                        newColumnExpression.addElement(ColumnExpressionAsString);
                        whereColumn.setColumnExpression(newColumnExpression);
                     }

                     wi.setRightWhereExp(whereColumn);
                  } else if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                     if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null) {
                        whereColumn = wi.getRightWhereExp();
                        columnExpression = new Vector();
                        newColumnExpression = whereColumn.getColumnExpression();
                        if (newColumnExpression != null && newColumnExpression.size() > 0 && !(newColumnExpression.elementAt(0) instanceof String)) {
                           columnExpression.addElement("(");

                           for(i = 0; i < newColumnExpression.size(); ++i) {
                              columnExpression.addElement(newColumnExpression.elementAt(i));
                           }

                           columnExpression.addElement(")");
                           whereColumn.setColumnExpression(columnExpression);
                        }

                        wi.setOperator(this.operator);
                     } else {
                        wi.setOperator(this.operator);
                     }
                  } else {
                     if (this.operator.equalsIgnoreCase("MATCHES")) {
                        wi.setOperator("LIKE");
                     } else {
                        wi.setOperator("NOT LIKE");
                     }

                     whereColumn = wi.getRightWhereExp();
                     columnExpression = whereColumn.getColumnExpression();
                     newColumnExpression = new Vector();
                     if (columnExpression != null) {
                        ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                        if (ColumnExpressionAsString.indexOf("*") != -1) {
                           ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                        }

                        if (ColumnExpressionAsString.indexOf("_") != -1) {
                           ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                        }

                        newColumnExpression.add(ColumnExpressionAsString);
                        whereColumn.setColumnExpression(newColumnExpression);
                     }
                  }
               } else {
                  wi.setOperator("<>");
               }
            }

            if (this.operator2 != null) {
               wi.setOperator2(this.operator2);
            }

            if (this.operator3 != null) {
               wi.setOperator3(this.operator3);
            }

            if (this.LeftJoin != null) {
               wi.setLeftJoin((String)null);
            }

            if (this.RightJoin != null) {
               wi.setRightJoin((String)null);
            }
         } else {
            wi.setOpenBrace(this.openBraces);
            wi.setCloseBrace(this.closeBraces);
            if (this.rownumClause != null) {
               if (from_sqs != null) {
                  FromClause fc = from_sqs.getFromClause();
                  if (fc != null) {
                     FromTable ft = MetadataInfoUtil.getTableOfColumn(from_sqs, (String)"rownum");
                     if (ft != null) {
                        this.rownumClause.setRownumClause(CustomizeUtil.objectNamesToQuotedIdentifier(this.rownumClause.getRownumClause(), SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1));
                     }
                  }
               }

               wi.setRownumClause(this.rownumClause);
               return wi;
            }

            if (this.beginOperator != null) {
               wi.setBeginOperator(this.beginOperator);
            }

            if (this.operator1 != null) {
               wi.setOperator1(this.operator1);
            }

            if (this.leftWhereExp != null) {
               wi.setObjectContext(this.context);
               wi.setLeftWhereExp(this.leftWhereExp.toOracleSelect(to_sqs, from_sqs));
            }

            String rightString;
            String value;
            String format;
            TableColumn tc;
            Vector rightColExpr;
            boolean isJoinSet;
            if (this.rightWhereExp != null) {
               wi.setObjectContext(this.context);
               rightColExpr = this.rightWhereExp.getColumnExpression();
               WhereColumn lwc;
               if (rightColExpr != null && rightColExpr.size() == 1) {
                  if ((SwisSQLOptions.convertNullStringToOracleISNULL || from_sqs.isOracleLive()) && rightColExpr.get(0) instanceof String && rightColExpr.get(0).toString().trim().equalsIgnoreCase("''")) {
                     String condition = rightColExpr.get(0).toString().trim();
                     wi.setRightWhereExp((WhereColumn)null);
                     if (this.operator.trim().equals("=")) {
                        this.operator = "IS NULL";
                     } else if (this.operator.trim().equals("<>") || this.operator.trim().equals("!=")) {
                        this.operator = "IS NOT NULL";
                     }
                  } else if (rightColExpr.get(0) instanceof String && rightColExpr.get(0).toString().trim().equalsIgnoreCase("null")) {
                     wi.setRightWhereExp((WhereColumn)null);
                     if (this.operator.trim().equalsIgnoreCase("=")) {
                        this.operator = "IS NULL";
                     } else if (this.operator.trim().equalsIgnoreCase("!=") || this.operator.trim().equalsIgnoreCase("<>")) {
                        this.operator = "IS NOT NULL";
                     }
                  } else if (rightColExpr.get(0) instanceof String && rightColExpr.get(0).toString().trim().startsWith("'")) {
                     lwc = wi.getLeftWhereExp();
                     isJoinSet = false;
                     if (lwc != null) {
                        Object obj = lwc.getColumnExpression().get(0);
                        if (obj instanceof TableColumn) {
                           rightString = MetadataInfoUtil.getDatatypeName(to_sqs, (TableColumn)obj);
                           if (rightString != null) {
                              value = rightColExpr.get(0).toString().trim();
                              if (rightString.toLowerCase().endsWith("datetime")) {
                                 format = SwisSQLUtils.getDateFormat(value, 1);
                                 if (format != null) {
                                    FunctionCalls fc = new FunctionCalls();
                                    tc = new TableColumn();
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
                                    rightColExpr.setElementAt(fc, 0);
                                    wi.setRightWhereExp(this.rightWhereExp);
                                    isJoinSet = true;
                                 }
                              }
                           }
                        }
                     }

                     if (!isJoinSet) {
                        wi.setRightWhereExp(this.rightWhereExp.toOracleSelect(to_sqs, from_sqs));
                     }
                  } else {
                     wi.setRightWhereExp(this.rightWhereExp.toOracleSelect(to_sqs, from_sqs));
                  }
               } else if (rightColExpr != null && rightColExpr.size() > 1 && this.operator.trim().equalsIgnoreCase("IN")) {
                  if (rightColExpr.get(1) instanceof WhereColumn) {
                     lwc = (WhereColumn)rightColExpr.get(1);
                     newColumnExpression = lwc.getColumnExpression();
                     if (newColumnExpression != null && newColumnExpression.get(0) instanceof String) {
                        ColumnExpressionAsString = newColumnExpression.get(0).toString().trim();
                        if (ColumnExpressionAsString.equals("''") && SwisSQLOptions.convertNullStringToOracleISNULL) {
                           newColumnExpression.set(0, "NULL");
                        }
                     }
                  }

                  wi.setRightWhereExp(this.rightWhereExp.toOracleSelect(to_sqs, from_sqs));
               } else {
                  wi.setRightWhereExp(this.rightWhereExp.toOracleSelect(to_sqs, from_sqs));
               }
            }

            if (from_sqs != null && from_sqs.getFromClause() != null) {
               rightColExpr = from_sqs.getFromClause().getFromItemList();
               columnExpression = from_sqs.getFromClause().getOuterFromTableNames(rightColExpr);
               isJoinSet = false;
               if (columnExpression != null && wi.getLeftWhereExp() != null && wi.getRightWhereExp() != null) {
                  WhereColumn leftWhereColumn = wi.getLeftWhereExp();
                  WhereColumn rightWhereColumn = wi.getRightWhereExp();
                  if (leftWhereColumn.getColumnExpression() != null) {
                     Vector columnExpression = leftWhereColumn.getColumnExpression();
                     format = new String();

                     for(int i = 0; i < columnExpression.size(); ++i) {
                        String tableOrAliasNameRight;
                        if (leftWhereColumn.getColumnExpression().get(i) instanceof TableColumn) {
                           tc = (TableColumn)leftWhereColumn.getColumnExpression().get(i);
                           format = tc.getTableName();
                        } else if (leftWhereColumn.getColumnExpression().get(i) instanceof String) {
                           tableOrAliasNameRight = (String)leftWhereColumn.getColumnExpression().get(i);
                           if (tableOrAliasNameRight.indexOf(".") != -1) {
                              format = tableOrAliasNameRight.substring(0, tableOrAliasNameRight.indexOf("."));
                           }
                        }

                        if (columnExpression.contains(format)) {
                           tableOrAliasNameRight = new String();
                           if (rightWhereColumn.getColumnExpression().get(i) instanceof TableColumn) {
                              TableColumn tableColumn = (TableColumn)rightWhereColumn.getColumnExpression().get(i);
                              tableOrAliasNameRight = tableColumn.getTableName();
                           } else if (rightWhereColumn.getColumnExpression().get(i) instanceof String) {
                              String tableAliasInWhere = (String)rightWhereColumn.getColumnExpression().get(i);
                              if (tableAliasInWhere.indexOf(".") != -1) {
                                 tableOrAliasNameRight = tableAliasInWhere.substring(0, tableAliasInWhere.indexOf("."));
                              }
                           }

                           int indexLeft = columnExpression.indexOf(format);
                           int indexRight = columnExpression.indexOf(tableOrAliasNameRight);
                           if (indexLeft > indexRight) {
                              wi.setRightJoin("+");
                              isJoinSet = true;
                           }
                        }
                     }
                  }

                  if (rightWhereColumn.getColumnExpression() != null) {
                     value = new String();

                     for(int i = 0; i < rightWhereColumn.getColumnExpression().size(); ++i) {
                        if (rightWhereColumn.getColumnExpression().get(i) instanceof TableColumn) {
                           TableColumn tableColumn = (TableColumn)rightWhereColumn.getColumnExpression().get(i);
                           value = tableColumn.getTableName();
                        } else if (rightWhereColumn.getColumnExpression().get(i) instanceof String) {
                           String tableAliasInWhere = (String)rightWhereColumn.getColumnExpression().get(i);
                           if (tableAliasInWhere.indexOf(".") != -1) {
                              value = tableAliasInWhere.substring(0, tableAliasInWhere.indexOf("."));
                           }
                        }

                        if (columnExpression.contains(value) && !isJoinSet) {
                           wi.setLeftJoin("+");
                        }
                     }
                  }
               }
            }

            if (this.LeftJoin != null) {
               if (this.LeftJoin.equalsIgnoreCase("*")) {
                  wi.setLeftJoin("+");
               } else {
                  wi.setLeftJoin(this.LeftJoin);
               }
            }

            if (this.RightJoin != null) {
               if (this.RightJoin.equalsIgnoreCase("*")) {
                  wi.setRightJoin("+");
               } else {
                  wi.setRightJoin(this.RightJoin);
               }
            }

            if (this.rightWhereSubQuery != null) {
               if (from_sqs != null) {
                  this.rightWhereSubQuery.setCanUseOracleFetch(from_sqs.getCanUseOracleFetch());
               }

               wi.setRightWhereSubQuery(this.rightWhereSubQuery.toOracle());
               if (this.rightWhereSubQueryExp != null) {
                  wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toOracleSelect(to_sqs, from_sqs));
               }
            }

            if (this.operator != null) {
               if (this.operator.equalsIgnoreCase("!>")) {
                  wi.setOperator("<=");
               } else if (this.operator.equalsIgnoreCase("!<")) {
                  wi.setOperator(">=");
               } else if (this.operator.equalsIgnoreCase("<=>")) {
                  wi.setOperator("=");
                  wi.isNullSafeEqualsOperator = true;
               } else if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                  wi.setOperator("LIKE");
                  if (wi.getRightWhereSubQuery() != null) {
                     throw new ConvertException("Conversion failure.. Subquery can't be converted");
                  }

                  whereColumn = wi.getRightWhereExp();
                  columnExpression = whereColumn.getColumnExpression();
                  newColumnExpression = new Vector();
                  if (columnExpression != null) {
                     if (columnExpression.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expression can't be converted");
                     }

                     ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                     if (ColumnExpressionAsString.charAt(0) == '\'') {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('\'', ' ').trim();
                        ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                     } else if (ColumnExpressionAsString.charAt(0) == '"') {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('"', ' ').trim();
                        ColumnExpressionAsString = "\"%" + ColumnExpressionAsString + "%\"";
                     } else {
                        ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                     }

                     newColumnExpression.addElement(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(newColumnExpression);
                  }

                  wi.setRightWhereExp(whereColumn);
               } else if ((this.operator.equalsIgnoreCase("BETWEEN") || this.operator.equalsIgnoreCase("NOT BETWEEN")) && wi.getRightWhereExp() != null) {
                  whereColumn = wi.getRightWhereExp();
                  columnExpression = whereColumn.getColumnExpression();
                  if (columnExpression != null) {
                     newColumnExpression = new Vector();

                     for(i = 0; i < columnExpression.size(); ++i) {
                        if (columnExpression.get(i) instanceof String) {
                           rightString = (String)columnExpression.get(i);
                           if (!rightString.equalsIgnoreCase("(") && !rightString.equalsIgnoreCase(")")) {
                              newColumnExpression.add(rightString);
                           }
                        } else {
                           newColumnExpression.add(columnExpression.get(i));
                        }
                     }

                     whereColumn.setColumnExpression(newColumnExpression);
                  }

                  wi.setOperator(this.operator);
               } else if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                  wi.setOperator(this.operator);
               } else {
                  if (this.operator.equalsIgnoreCase("MATCHES")) {
                     wi.setOperator("LIKE");
                  } else {
                     wi.setOperator("NOT LIKE");
                  }

                  whereColumn = wi.getRightWhereExp();
                  columnExpression = whereColumn.getColumnExpression();
                  newColumnExpression = new Vector();
                  if (columnExpression != null) {
                     ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                     if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                     }

                     if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                     }

                     newColumnExpression.add(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(newColumnExpression);
                  }
               }
            }

            if (this.operator2 != null) {
               wi.setOperator2(this.operator2);
            }

            if (this.operator3 != null) {
               wi.setOperator3(this.operator3);
            }
         }

         return wi;
      }
   }

   public WhereItem toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toInformixSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toInformixSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toInformix());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toInformixSelect(to_sqs, from_sqs));
            }
         }

         if (this.operator != null) {
            if (this.operator.equalsIgnoreCase("^=")) {
               wi.setOperator("<>");
            } else if (this.operator.equalsIgnoreCase("!>")) {
               wi.setOperator("<=");
            } else if (this.operator.equalsIgnoreCase("!<")) {
               wi.setOperator(">=");
            } else if (this.operator.equalsIgnoreCase("<=>")) {
               wi.setOperator("=");
               wi.isNullSafeEqualsOperator = true;
            } else {
               WhereColumn wc;
               Vector v_ce;
               Vector v_removedcomma_left_column_exp;
               if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                  wi.setOperator("LIKE");
                  if (wi.getRightWhereSubQuery() != null) {
                     throw new ConvertException("Conversion failure.. Subquery can't be converted");
                  }

                  wc = wi.getRightWhereExp();
                  v_ce = wc.getColumnExpression();
                  v_removedcomma_left_column_exp = new Vector();
                  if (v_ce != null) {
                     if (v_ce.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expressions can't be converted");
                     }

                     String s_ce = v_ce.elementAt(0).toString();
                     if (s_ce.charAt(0) == '\'') {
                        s_ce = s_ce.replace('\'', ' ').trim();
                        s_ce = "'%" + s_ce + "%'";
                     } else if (s_ce.charAt(0) == '"') {
                        s_ce = s_ce.replace('"', ' ').trim();
                        s_ce = "\"%" + s_ce + "%\"";
                     } else {
                        s_ce = "'%" + s_ce + "%'";
                     }

                     v_removedcomma_left_column_exp.addElement(s_ce);
                     wc.setColumnExpression(v_removedcomma_left_column_exp);
                  }

                  wi.setRightWhereExp(wc);
               } else {
                  WhereColumn l_wc;
                  if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null && wi.getRightWhereSubQuery() == null) {
                     wc = wi.getRightWhereExp();
                     l_wc = wi.getLeftWhereExp();
                     WhereColumn wc = wi.getRightWhereExp();
                     Vector v_nsce = new Vector();
                     Vector v_sce = wc.getColumnExpression();
                     if (v_sce != null && v_sce.size() > 0 && !(v_sce.elementAt(0) instanceof String)) {
                        v_nsce.addElement("(");

                        for(int i = 0; i < v_sce.size(); ++i) {
                           v_nsce.addElement(v_sce.elementAt(i));
                        }

                        v_nsce.addElement(")");
                        wc.setColumnExpression(v_nsce);
                     }

                     wi.setOperator(this.operator);
                  } else if ((this.operator.equalsIgnoreCase("IN") || this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereSubQuery() != null) {
                     String f_tablename = null;
                     if (from_sqs != null) {
                        v_ce = null;
                        if (from_sqs.getFromClause().getFromItemList().elementAt(0) instanceof FromTable) {
                           FromTable ft = (FromTable)from_sqs.getFromClause().getFromItemList().elementAt(0);
                           if (ft.getAliasName() != null) {
                              f_tablename = ft.getAliasName();
                           } else if (ft.getTableName() instanceof String) {
                              f_tablename = ft.getTableName().toString();
                              int pos = f_tablename.indexOf(".");
                              if (pos == -1) {
                                 pos = f_tablename.indexOf("..");
                              }

                              f_tablename = f_tablename.substring(pos + 1);
                           } else {
                              f_tablename = ft.getTableName().toString();
                           }
                        }
                     }

                     l_wc = wi.getLeftWhereExp();
                     if (l_wc.getColumnExpression() != null && l_wc.getColumnExpression().size() != 1 && l_wc.getColumnExpression().contains(",")) {
                        v_removedcomma_left_column_exp = new Vector();
                        this.removeCommaBracesFromExpressionList(l_wc, v_removedcomma_left_column_exp, f_tablename);
                        wi.setLeftWhereExp((WhereColumn)null);
                        if (this.operator.equalsIgnoreCase("IN")) {
                           wi.setOperator("EXISTS");
                        } else if (this.operator.equalsIgnoreCase("NOT IN")) {
                           wi.setOperator("NOT EXISTS");
                        }

                        SelectQueryStatement sqs = wi.getRightWhereSubQuery();
                        String f_tablename_1 = null;
                        FromTable ft_1 = (FromTable)sqs.getFromClause().getFromItemList().elementAt(0);
                        if (ft_1.getAliasName() == null) {
                        }

                        if (ft_1.getAliasName() != null) {
                           f_tablename_1 = ft_1.getAliasName();
                        } else {
                           f_tablename_1 = ft_1.getTableName().toString();
                        }

                        Vector v_sitl = sqs.getSelectStatement().getSelectItemList();

                        for(int i = 0; i < v_sitl.size(); ++i) {
                           WhereItem wi_1 = new WhereItem();
                           WhereColumn l_wc_1 = new WhereColumn();
                           WhereColumn r_wc_1 = new WhereColumn();
                           Vector v_lce = new Vector();
                           Vector v_rce = new Vector();
                           SelectColumn sc = (SelectColumn)v_sitl.elementAt(i);
                           Vector v_ce = sc.getColumnExpression();
                           String where_column_name = null;
                           if (v_ce.elementAt(0) instanceof String) {
                              where_column_name = (String)v_ce.elementAt(0);
                           } else if (v_ce.elementAt(0) instanceof TableColumn) {
                              TableColumn tc = (TableColumn)v_ce.elementAt(0);
                              if (tc.getTableName() == null) {
                                 tc.setTableName(f_tablename_1);
                              }

                              where_column_name = tc.toString();
                           } else if (v_ce.elementAt(0) instanceof SelectColumn) {
                              SelectColumn selectColumnInsideSelectColumn = (SelectColumn)v_ce.elementAt(0);
                              Vector newColumnExp = selectColumnInsideSelectColumn.getColumnExpression();
                              if (newColumnExp.elementAt(0) instanceof String) {
                                 where_column_name = (String)newColumnExp.elementAt(0);
                              } else if (newColumnExp.elementAt(0) instanceof TableColumn) {
                                 TableColumn tableColumn = (TableColumn)newColumnExp.elementAt(0);
                                 if (tableColumn.getTableName() == null) {
                                    tableColumn.setTableName(f_tablename_1);
                                 }

                                 where_column_name = tableColumn.toString();
                              }
                           }

                           v_lce.addElement(where_column_name);
                           l_wc_1.setColumnExpression(v_lce);
                           v_rce.addElement(v_removedcomma_left_column_exp.elementAt(i));
                           r_wc_1.setColumnExpression(v_rce);
                           wi_1.setOperator("=");
                           wi_1.setLeftWhereExp(l_wc_1);
                           wi_1.setRightWhereExp(r_wc_1);
                           if (sqs.getWhereExpression() != null) {
                              sqs.getWhereExpression().addWhereItem(wi_1);
                              sqs.getWhereExpression().addOperator("AND");
                           } else {
                              WhereExpression we = new WhereExpression();
                              we.addWhereItem(wi_1);
                              sqs.setWhereExpression(we);
                           }
                        }

                        wi.setRightWhereSubQuery(sqs);
                     } else {
                        wi.setOperator(this.operator);
                     }
                  } else {
                     wi.setOperator(this.operator);
                  }
               }
            }
         }

         if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.LeftJoin != null) {
            wi.setLeftJoin((String)null);
         }

         if (this.RightJoin != null) {
            wi.setRightJoin((String)null);
         }

         return wi;
      }
   }

   public WhereItem convert(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, int database) throws ConvertException {
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

   public WhereItem toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         if (this.leftWhereExp != null) {
            wi.setObjectContext(this.context);
            wi.setLeftWhereExp(this.leftWhereExp.toTimesTenSelect(to_sqs, from_sqs));
         }

         Vector rightColExpr;
         boolean isJoinSet;
         String datatype;
         String value;
         String format;
         String time;
         int len;
         TableColumn tc;
         if (this.rightWhereExp != null) {
            wi.setObjectContext(this.context);
            rightColExpr = this.rightWhereExp.getColumnExpression();
            if (rightColExpr != null && rightColExpr.size() == 1) {
               if (rightColExpr.get(0) instanceof String && rightColExpr.get(0).toString().trim().equalsIgnoreCase("null")) {
                  wi.setRightWhereExp((WhereColumn)null);
                  if (this.operator.trim().equalsIgnoreCase("=")) {
                     this.operator = "IS NULL";
                  } else if (this.operator.trim().equalsIgnoreCase("!=") || this.operator.trim().equalsIgnoreCase("<>")) {
                     this.operator = "IS NOT NULL";
                  }
               } else if (rightColExpr.get(0) instanceof String && rightColExpr.get(0).toString().trim().startsWith("'")) {
                  WhereColumn lwc = wi.getLeftWhereExp();
                  isJoinSet = false;
                  if (lwc != null) {
                     Object obj = lwc.getColumnExpression().get(0);
                     if (obj instanceof TableColumn) {
                        datatype = MetadataInfoUtil.getDatatypeName(to_sqs, (TableColumn)obj);
                        if (datatype != null) {
                           value = rightColExpr.get(0).toString().trim();
                           if (datatype.toLowerCase().indexOf("date") == -1 && datatype.toLowerCase().indexOf("time") == -1) {
                              if (datatype.indexOf("unichar") != -1 || datatype.indexOf("univarchar") != -1 || datatype.indexOf("nchar") != -1 || datatype.indexOf("nvarchar") != -1) {
                                 rightColExpr.setElementAt("N" + value, 0);
                                 wi.setRightWhereExp(this.rightWhereExp);
                                 isJoinSet = true;
                              }
                           } else {
                              format = SwisSQLUtils.getDateFormat(value, 10);
                              if (format != null && (format.equals("YYYY-MM-DD") || format.equals("HH24:MI:SS"))) {
                                 if (datatype.toLowerCase().indexOf("datetime") != -1) {
                                    if (format.equals("YYYY-MM-DD")) {
                                       value = value.substring(0, value.length() - 1) + " 00:00:00'";
                                    } else {
                                       value = "'1900-01-01 " + value.substring(1);
                                    }

                                    rightColExpr.setElementAt(value, 0);
                                    wi.setRightWhereExp(this.rightWhereExp);
                                    isJoinSet = true;
                                 }

                                 format = null;
                              }

                              if (format != null) {
                                 if (format.startsWith("'1900")) {
                                    rightColExpr.setElementAt(format, 0);
                                    wi.setRightWhereExp(this.rightWhereExp);
                                    isJoinSet = true;
                                 } else if (!format.equals(value)) {
                                    FunctionCalls fc = new FunctionCalls();
                                    tc = new TableColumn();
                                    tc.setColumnName("TO_DATE");
                                    Vector fnArgs = new Vector();
                                    fnArgs.add(value);
                                    fnArgs.add(format);
                                    fc.setFunctionName(tc);
                                    fc.setFunctionArguments(fnArgs);
                                    rightColExpr.setElementAt(fc, 0);
                                    wi.setRightWhereExp(this.rightWhereExp);
                                    isJoinSet = true;
                                 } else {
                                    value = value.substring(1, value.length() - 1);
                                    time = "";
                                    int index = false;
                                    int index;
                                    if ((index = value.indexOf(" ")) != -1) {
                                       time = value.substring(index + 1);
                                       value = value.substring(0, index);
                                    }

                                    len = value.length();
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

                                    if (datatype.toLowerCase().equalsIgnoreCase("time") && time == "") {
                                       value = value + " 00:00:00";
                                    } else if (time != "") {
                                       value = value + " " + time;
                                    }

                                    rightColExpr.setElementAt(value, 0);
                                    wi.setRightWhereExp(this.rightWhereExp);
                                    isJoinSet = true;
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (!isJoinSet) {
                     wi.setRightWhereExp(this.rightWhereExp.toTimesTenSelect(to_sqs, from_sqs));
                  }
               } else if (rightColExpr.get(0).toString().trim().startsWith("\"") && this.operator.equalsIgnoreCase("like")) {
                  rightColExpr.setElementAt(StringFunctions.replaceAll("'", "\"", rightColExpr.get(0).toString()), 0);
                  wi.setRightWhereExp(this.rightWhereExp.toTimesTenSelect(to_sqs, from_sqs));
               } else {
                  wi.setRightWhereExp(this.rightWhereExp.toTimesTenSelect(to_sqs, from_sqs));
               }
            } else if (rightColExpr != null && rightColExpr.size() == 3 && rightColExpr.get(1).toString().equalsIgnoreCase("escape") && this.operator.equals("like")) {
               if (rightColExpr.get(0).toString().trim().startsWith("\"")) {
                  rightColExpr.setElementAt(StringFunctions.replaceAll("'", "\"", rightColExpr.get(0).toString()), 0);
               }

               if (rightColExpr.get(2).toString().trim().startsWith("\"")) {
                  rightColExpr.setElementAt(StringFunctions.replaceAll("'", "\"", rightColExpr.get(2).toString()), 2);
               }

               wi.setRightWhereExp(this.rightWhereExp.toTimesTenSelect(to_sqs, from_sqs));
            } else {
               wi.setRightWhereExp(this.rightWhereExp.toTimesTenSelect(to_sqs, from_sqs));
            }
         }

         Vector columnExpression;
         if (from_sqs != null && from_sqs.getFromClause() != null) {
            rightColExpr = from_sqs.getFromClause().getFromItemList();
            columnExpression = from_sqs.getFromClause().getOuterFromTableNames(rightColExpr);
            isJoinSet = false;
            if (columnExpression != null && wi.getLeftWhereExp() != null && wi.getRightWhereExp() != null) {
               WhereColumn leftWhereColumn = wi.getLeftWhereExp();
               WhereColumn rightWhereColumn = wi.getRightWhereExp();
               if (leftWhereColumn.getColumnExpression() != null) {
                  Vector columnExpression = leftWhereColumn.getColumnExpression();
                  format = new String();

                  for(int i = 0; i < columnExpression.size(); ++i) {
                     String tableOrAliasNameRight;
                     if (leftWhereColumn.getColumnExpression().get(i) instanceof TableColumn) {
                        tc = (TableColumn)leftWhereColumn.getColumnExpression().get(i);
                        format = tc.getTableName();
                     } else if (leftWhereColumn.getColumnExpression().get(i) instanceof String) {
                        tableOrAliasNameRight = (String)leftWhereColumn.getColumnExpression().get(i);
                        if (tableOrAliasNameRight.indexOf(".") != -1) {
                           format = tableOrAliasNameRight.substring(0, tableOrAliasNameRight.indexOf("."));
                        }
                     }

                     if (columnExpression.contains(format)) {
                        tableOrAliasNameRight = new String();
                        if (rightWhereColumn.getColumnExpression().get(i) instanceof TableColumn) {
                           TableColumn tableColumn = (TableColumn)rightWhereColumn.getColumnExpression().get(i);
                           tableOrAliasNameRight = tableColumn.getTableName();
                        } else if (rightWhereColumn.getColumnExpression().get(i) instanceof String) {
                           String tableAliasInWhere = (String)rightWhereColumn.getColumnExpression().get(i);
                           if (tableAliasInWhere.indexOf(".") != -1) {
                              tableOrAliasNameRight = tableAliasInWhere.substring(0, tableAliasInWhere.indexOf("."));
                           }
                        }

                        len = columnExpression.indexOf(format);
                        int indexRight = columnExpression.indexOf(tableOrAliasNameRight);
                        if (len > indexRight) {
                           wi.setRightJoin("+");
                           isJoinSet = true;
                        }
                     }
                  }
               }

               if (rightWhereColumn.getColumnExpression() != null) {
                  value = new String();

                  for(int i = 0; i < rightWhereColumn.getColumnExpression().size(); ++i) {
                     if (rightWhereColumn.getColumnExpression().get(i) instanceof TableColumn) {
                        TableColumn tableColumn = (TableColumn)rightWhereColumn.getColumnExpression().get(i);
                        value = tableColumn.getTableName();
                     } else if (rightWhereColumn.getColumnExpression().get(i) instanceof String) {
                        time = (String)rightWhereColumn.getColumnExpression().get(i);
                        if (time.indexOf(".") != -1) {
                           value = time.substring(0, time.indexOf("."));
                        }
                     }

                     if (columnExpression.contains(value) && !isJoinSet) {
                        wi.setLeftJoin("+");
                     }
                  }
               }
            }
         }

         WhereColumn whereColumn;
         if (this.LeftJoin != null) {
            if (this.LeftJoin.equalsIgnoreCase("*")) {
               wi.setLeftJoin("+");
               whereColumn = wi.getRightWhereExp();
               if (whereColumn != null && whereColumn.getColumnExpression().get(0) instanceof String) {
                  wi.setRightJoin("+");
                  wi.setLeftJoin((String)null);
               }
            } else {
               wi.setLeftJoin(this.LeftJoin);
            }
         }

         if (this.RightJoin != null) {
            if (this.RightJoin.equalsIgnoreCase("*")) {
               wi.setRightJoin("+");
            } else {
               wi.setRightJoin(this.RightJoin);
            }
         }

         if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toTimesTen());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toTimesTenSelect(to_sqs, from_sqs));
            }
         }

         if (this.operator != null) {
            if (this.operator.equalsIgnoreCase("!>")) {
               wi.setOperator("<=");
            } else if (this.operator.equalsIgnoreCase("!<")) {
               wi.setOperator(">=");
            } else {
               String ColumnExpressionAsString;
               Vector newColumnExpression;
               if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                  wi.setOperator("LIKE");
                  if (wi.getRightWhereSubQuery() != null) {
                     throw new ConvertException("Conversion failure.. Subquery can't be converted");
                  }

                  whereColumn = wi.getRightWhereExp();
                  columnExpression = whereColumn.getColumnExpression();
                  newColumnExpression = new Vector();
                  if (columnExpression != null) {
                     if (columnExpression.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expression can't be converted");
                     }

                     ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                     if (ColumnExpressionAsString.charAt(0) == '\'') {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('\'', ' ').trim();
                        ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                     } else if (ColumnExpressionAsString.charAt(0) == '"') {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('"', ' ').trim();
                        ColumnExpressionAsString = "\"%" + ColumnExpressionAsString + "%\"";
                     } else {
                        ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                     }

                     newColumnExpression.addElement(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(newColumnExpression);
                  }

                  wi.setRightWhereExp(whereColumn);
               } else if ((this.operator.equalsIgnoreCase("BETWEEN") || this.operator.equalsIgnoreCase("NOT BETWEEN")) && wi.getRightWhereExp() != null) {
                  whereColumn = wi.getRightWhereExp();
                  columnExpression = whereColumn.getColumnExpression();
                  if (columnExpression != null) {
                     newColumnExpression = new Vector();

                     for(int i = 0; i < columnExpression.size(); ++i) {
                        if (columnExpression.get(i) instanceof String) {
                           datatype = (String)columnExpression.get(i);
                           if (!datatype.equalsIgnoreCase("(") && !datatype.equalsIgnoreCase(")")) {
                              newColumnExpression.add(datatype);
                           }
                        } else {
                           newColumnExpression.add(columnExpression.get(i));
                        }
                     }

                     whereColumn.setColumnExpression(newColumnExpression);
                  }

                  wi.setOperator(this.operator);
               } else if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                  wi.setOperator(this.operator);
               } else {
                  if (this.operator.equalsIgnoreCase("MATCHES")) {
                     wi.setOperator("LIKE");
                  } else {
                     wi.setOperator("NOT LIKE");
                  }

                  whereColumn = wi.getRightWhereExp();
                  columnExpression = whereColumn.getColumnExpression();
                  newColumnExpression = new Vector();
                  if (columnExpression != null) {
                     ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                     if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                     }

                     if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                     }

                     newColumnExpression.add(ColumnExpressionAsString);
                     whereColumn.setColumnExpression(newColumnExpression);
                  }
               }
            }
         }

         if (this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         return wi;
      }
   }

   public WhereItem toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toNetezzaSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toNetezzaSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toNetezza());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toNetezzaSelect(to_sqs, from_sqs));
            }
         }

         if (this.operator != null) {
            if (!this.operator.equalsIgnoreCase("^=") && !this.operator.equalsIgnoreCase("!=")) {
               if (this.operator.equalsIgnoreCase("!>")) {
                  wi.setOperator("<=");
               } else if (this.operator.equalsIgnoreCase("!<")) {
                  wi.setOperator(">=");
               } else {
                  WhereColumn whereColumn;
                  Vector v_nsce;
                  Vector newColumnExpression;
                  String ColumnExpressionAsString;
                  if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                     wi.setOperator("LIKE");
                     if (wi.getRightWhereSubQuery() != null) {
                        throw new ConvertException("Conversion failure.. Subquery can't be converted");
                     }

                     whereColumn = wi.getRightWhereExp();
                     v_nsce = whereColumn.getColumnExpression();
                     newColumnExpression = new Vector();
                     if (v_nsce != null) {
                        if (v_nsce.size() != 1) {
                           throw new ConvertException("Conversion failure.. Expressions can't be converted");
                        }

                        ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                        if (ColumnExpressionAsString.charAt(0) == '\'') {
                           ColumnExpressionAsString = ColumnExpressionAsString.replace('\'', ' ').trim();
                           ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                        } else if (ColumnExpressionAsString.charAt(0) == '"') {
                           ColumnExpressionAsString = ColumnExpressionAsString.replace('"', ' ').trim();
                           ColumnExpressionAsString = "\"%" + ColumnExpressionAsString + "%\"";
                        } else {
                           ColumnExpressionAsString = "'%" + ColumnExpressionAsString + "%'";
                        }

                        newColumnExpression.addElement(ColumnExpressionAsString);
                        whereColumn.setColumnExpression(newColumnExpression);
                     }

                     wi.setRightWhereExp(whereColumn);
                  } else if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                     if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null) {
                        whereColumn = wi.getRightWhereExp();
                        v_nsce = new Vector();
                        newColumnExpression = whereColumn.getColumnExpression();
                        if (newColumnExpression != null && newColumnExpression.size() > 0 && !(newColumnExpression.elementAt(0) instanceof String)) {
                           v_nsce.addElement("(");

                           for(int i = 0; i < newColumnExpression.size(); ++i) {
                              v_nsce.addElement(newColumnExpression.elementAt(i));
                           }

                           v_nsce.addElement(")");
                           whereColumn.setColumnExpression(v_nsce);
                        }

                        wi.setOperator(this.operator);
                     } else {
                        wi.setOperator(this.operator);
                     }
                  } else {
                     if (this.operator.equalsIgnoreCase("MATCHES")) {
                        wi.setOperator("LIKE");
                     } else {
                        wi.setOperator("NOT LIKE");
                     }

                     whereColumn = wi.getRightWhereExp();
                     v_nsce = whereColumn.getColumnExpression();
                     newColumnExpression = new Vector();
                     if (v_nsce != null) {
                        ColumnExpressionAsString = v_nsce.elementAt(0).toString();
                        if (ColumnExpressionAsString.indexOf("*") != -1) {
                           ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                        }

                        if (ColumnExpressionAsString.indexOf("_") != -1) {
                           ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                        }

                        newColumnExpression.add(ColumnExpressionAsString);
                        whereColumn.setColumnExpression(newColumnExpression);
                     }
                  }
               }
            } else {
               wi.setOperator("<>");
            }
         }

         if (this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.LeftJoin != null) {
            wi.setLeftJoin((String)null);
         }

         if (this.RightJoin != null) {
            wi.setRightJoin((String)null);
         }

         return wi;
      }
   }

   public String toString() {
      if (this.rownumClause != null) {
         return this.rownumClause.toString();
      } else {
         StringBuffer sb = new StringBuffer();
         if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
         }

         if (this.openBraces != null && !this.openBraces.equals("")) {
            sb.append(this.openBraces);
         }

         if (this.beginOperator != null) {
            sb.append(this.beginOperator + " ");
         }

         if (this.operator1 != null) {
            sb.append(this.operator1 + " ");
         }

         if (this.leftWhereExp != null) {
            this.leftWhereExp.setObjectContext(this.context);
            Vector colExp = this.leftWhereExp.getColumnExpression();
            if (colExp != null) {
               for(int i = 0; i < colExp.size(); ++i) {
                  if (colExp.elementAt(i) instanceof TableColumn) {
                     ((TableColumn)colExp.elementAt(i)).setObjectContext(this.context);
                  } else if (colExp.elementAt(i) instanceof SelectColumn) {
                     ((SelectColumn)colExp.elementAt(i)).setObjectContext(this.context);
                  } else if (colExp.elementAt(i) instanceof FunctionCalls) {
                     ((FunctionCalls)colExp.elementAt(i)).setObjectContext(this.context);
                  }
               }
            }

            sb.append(this.leftWhereExp.toString());
            if (this.RightJoin != null && this.RightJoin.equals("+")) {
               sb.append("(" + this.RightJoin + ")");
            }

            if (this.LeftJoin != null && this.LeftJoin.equals("*")) {
               sb.append(" *");
            }
         }

         if (this.operator != null) {
            if (this.LeftJoin != null && this.LeftJoin.equals("*")) {
               sb.append(this.operator);
            } else {
               sb.append(" " + this.operator);
            }

            if (this.RightJoin != null && this.RightJoin.equals("*")) {
               sb.append(this.RightJoin);
            }
         }

         if (this.lnnvlWhereExp != null) {
            sb.append(this.lnnvlWhereExp.toString());
         }

         int i;
         if (this.regExp != null) {
            for(i = 0; i < this.regExp.size(); ++i) {
               if (this.regExp.get(i) instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)this.regExp.get(i);
                  sb.append(sc.toString());
               } else {
                  sb.append(this.regExp.get(i).toString());
               }
            }
         }

         if (this.operator2 != null) {
            sb.append(" " + this.operator2.toUpperCase());
         }

         if (this.operator3 != null) {
            sb.append(" " + this.operator3.toUpperCase());
         }

         int i;
         boolean isLeftJoinAdded;
         Vector colExp;
         if (this.rightWhereSubQuery != null) {
            sb.append("\n");
            SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);

            for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
               sb.append("\t");
            }

            sb.append("(");
            sb.append("\n ");
            this.rightWhereSubQuery.setObjectContext(this.context);
            sb.append(this.rightWhereSubQuery.toString());
            if (this.rightWhereSubQueryExp != null) {
               isLeftJoinAdded = false;
               colExp = this.rightWhereSubQueryExp.getColumnExpression();
               if (colExp != null) {
                  for(i = 0; i < colExp.size(); ++i) {
                     if (colExp.elementAt(i) instanceof TableColumn) {
                        ((TableColumn)colExp.elementAt(i)).setObjectContext(this.context);
                     }

                     if (colExp.elementAt(i) instanceof TableColumn && !isLeftJoinAdded) {
                        sb.append(" " + colExp.elementAt(i).toString());
                        if (this.LeftJoin != null && this.LeftJoin.equals("+")) {
                           sb.append(" (" + this.LeftJoin + ")");
                           isLeftJoinAdded = true;
                        }
                     } else {
                        sb.append(" " + colExp.elementAt(i).toString());
                     }

                     if (this.LeftJoin != null && this.LeftJoin.equals("+") && !isLeftJoinAdded) {
                        sb.append(" (" + this.LeftJoin + ")");
                        isLeftJoinAdded = true;
                     }
                  }
               }
            }

            for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
               sb.append("\t");
            }

            sb.append(")");
            SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
         } else if (this.rightWhereExp != null) {
            this.rightWhereExp.setObjectContext(this.context);
            isLeftJoinAdded = false;
            if (this.rightWhereExp.getCommentClass() != null) {
               sb.append(this.rightWhereExp.getCommentClass().toString());
            }

            if (this.rightWhereExp.getOpenBrace() != null) {
               sb.append(this.rightWhereExp.getOpenBrace());
            }

            colExp = this.rightWhereExp.getColumnExpression();
            if (colExp != null) {
               for(i = 0; i < colExp.size(); ++i) {
                  if (colExp.elementAt(i) instanceof TableColumn && !isLeftJoinAdded) {
                     TableColumn tc = (TableColumn)colExp.elementAt(i);
                     if (tc.getCommentClass() != null) {
                        CommentClass co = tc.getCommentClass();
                        if (co != null) {
                           sb.append(" " + co.toString().trim());
                        }
                     }

                     tc.setObjectContext(this.context);
                     sb.append(" " + colExp.elementAt(i).toString());
                     if (this.LeftJoin != null && this.LeftJoin.equals("+")) {
                        sb.append(" (" + this.LeftJoin + ")");
                        isLeftJoinAdded = true;
                     }
                  } else if (colExp.elementAt(i) instanceof SelectColumn) {
                     ((SelectColumn)colExp.elementAt(i)).setObjectContext(this.context);
                     sb.append(" " + colExp.elementAt(i).toString());
                  } else if (colExp.elementAt(i) instanceof FunctionCalls) {
                     ((FunctionCalls)colExp.elementAt(i)).setObjectContext(this.context);
                     sb.append(" " + colExp.elementAt(i).toString());
                  } else if (colExp.elementAt(i) instanceof WhereColumn) {
                     ((WhereColumn)colExp.elementAt(i)).setObjectContext(this.context);
                     sb.append(" " + colExp.elementAt(i).toString());
                  } else if (colExp.elementAt(i) instanceof String && SwisSQLAPI.getProperSelExp()) {
                     sb.append(" " + GeneralUtil.trimIfAliasNameIsEnclosed(colExp.elementAt(i).toString()).trim());
                  } else {
                     sb.append(" " + colExp.elementAt(i).toString());
                  }

                  if (this.LeftJoin != null && this.LeftJoin.equals("+") && !isLeftJoinAdded) {
                     sb.append(" (" + this.LeftJoin + ")");
                     isLeftJoinAdded = true;
                  }
               }

               if (this.rightWhereExp.getCloseBrace() != null) {
                  sb.append(this.rightWhereExp.getCloseBrace());
               }

               if (this.rightWhereExp.getCommentClassAfterToken() != null) {
                  sb.append(" " + this.rightWhereExp.getCommentClassAfterToken().toString());
               }
            }
         }

         if (this.closeBraces != null && !this.closeBraces.equals("")) {
            sb.append(this.closeBraces);
         }

         if (this.commentObjAfterToken != null) {
            sb.append(" " + this.commentObjAfterToken.toString().trim());
         }

         if (this.casestmtFromLNNVLClause != null) {
            sb.append(this.casestmtFromLNNVLClause.toString());
         }

         String returnString = sb.toString();
         return returnString.equals("()") ? null : returnString;
      }
   }

   public Object clone() {
      WhereItem item = new WhereItem();
      item.setRownumClause(this.rownumClause);
      item.setOpenBrace(this.openBraces);
      item.setOperator1(this.operator1);
      item.setLeftWhereExp(this.leftWhereExp);
      item.setOperator(this.operator);
      item.setOperator2(this.operator2);
      item.setOperator3(this.operator3);
      item.setRightWhereSubQuery(this.rightWhereSubQuery);
      item.setRightWhereExp(this.rightWhereExp);
      item.setCloseBrace(this.closeBraces);
      item.setLeftJoin(this.LeftJoin);
      item.setRightJoin(this.RightJoin);
      item.setObjectContext(this.context);
      return item;
   }

   public boolean isItAJoinItem() {
      Vector leftColumnExpression = null;
      Vector rightColumnExpression = null;
      WhereColumn leftItem = this.getLeftWhereExp();
      if (leftItem != null) {
         leftColumnExpression = leftItem.getColumnExpression();
      }

      WhereColumn rightItem = this.getRightWhereExp();
      if (rightItem != null) {
         rightColumnExpression = rightItem.getColumnExpression();
      }

      return (leftColumnExpression == null || leftColumnExpression.elementAt(0) instanceof TableColumn) && (rightColumnExpression == null || rightColumnExpression.elementAt(0) instanceof TableColumn) && leftColumnExpression != null && rightColumnExpression != null;
   }

   public Vector getWhereItemsReplacingInClause() {
      Vector whereItemsListReplacingInClause = new Vector();
      String whereItemOperator = this.getOperator();
      SelectQueryStatement rightWhereSubquery = this.getRightWhereSubQuery();
      WhereColumn rightWhereExpression = this.getRightWhereExp();
      WhereColumn leftWhereColumn = this.getLeftWhereExp();
      WhereColumn rightWhereColumn = this.getRightWhereExp();
      Vector leftWhereColumnExp = leftWhereColumn.getColumnExpression();
      Vector rightWhereColumnExp = rightWhereColumn.getColumnExpression();
      new Vector();
      new Vector();
      Vector removeCommaInRightColumnExp = new Vector();
      Vector removeCommaInLeftColumnExp = new Vector();

      int i;
      String s_ce;
      for(i = 0; i < leftWhereColumnExp.size(); ++i) {
         if (leftWhereColumnExp.elementAt(i) instanceof String) {
            s_ce = (String)leftWhereColumnExp.elementAt(i);
            if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
               removeCommaInLeftColumnExp.addElement(leftWhereColumnExp.elementAt(i));
            }
         } else {
            removeCommaInLeftColumnExp.addElement(leftWhereColumnExp.elementAt(i));
         }
      }

      for(i = 0; i < rightWhereColumnExp.size(); ++i) {
         if (rightWhereColumnExp.elementAt(i) instanceof String) {
            s_ce = (String)rightWhereColumnExp.elementAt(i);
            if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
               removeCommaInRightColumnExp.addElement(rightWhereColumnExp.elementAt(i));
            }
         } else {
            removeCommaInRightColumnExp.addElement(rightWhereColumnExp.elementAt(i));
         }
      }

      for(i = 0; i < removeCommaInLeftColumnExp.size(); ++i) {
         WhereItem newWhereItem = new WhereItem();
         WhereColumn leftWhereColumnInNewWhereItem = new WhereColumn();
         WhereColumn rightWhereColumnInNewWhereItem = new WhereColumn();
         Vector v_lnsc = new Vector();
         Vector v_rnsc = new Vector();
         v_lnsc.addElement(removeCommaInLeftColumnExp.elementAt(i));
         v_rnsc.addElement(removeCommaInRightColumnExp.elementAt(i));
         leftWhereColumnInNewWhereItem.setColumnExpression(v_lnsc);
         rightWhereColumnInNewWhereItem.setColumnExpression(v_rnsc);
         newWhereItem.setLeftWhereExp(leftWhereColumnInNewWhereItem);
         newWhereItem.setRightWhereExp(rightWhereColumnInNewWhereItem);
         if (this.operator.equalsIgnoreCase("IN")) {
            newWhereItem.setOperator("=");
         } else {
            newWhereItem.setOperator("!=");
         }

         whereItemsListReplacingInClause.add(newWhereItem);
      }

      return whereItemsListReplacingInClause;
   }

   public Vector getWhereItemsReplacingEqualsClause() {
      Vector whereItemsListReplacingEqualsClause = new Vector();
      String whereItemOperator = this.getOperator2();
      SelectQueryStatement rightWhereSubquery = this.getRightWhereSubQuery();
      WhereColumn rightWhereExpression = this.getRightWhereExp();
      WhereColumn leftWhereColumn = this.getLeftWhereExp();
      WhereColumn rightWhereColumn = this.getRightWhereExp();
      Vector leftWhereColumnExp = leftWhereColumn.getColumnExpression();
      Vector rightWhereColumnExp = rightWhereColumn.getColumnExpression();
      new Vector();
      new Vector();
      Vector removeCommaInRightColumnExp = new Vector();
      Vector removeCommaInLeftColumnExp = new Vector();

      int i;
      String s_ce;
      for(i = 0; i < leftWhereColumnExp.size(); ++i) {
         if (leftWhereColumnExp.elementAt(i) instanceof String) {
            s_ce = (String)leftWhereColumnExp.elementAt(i);
            if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
               removeCommaInLeftColumnExp.addElement(leftWhereColumnExp.elementAt(i));
            }
         } else {
            removeCommaInLeftColumnExp.addElement(leftWhereColumnExp.elementAt(i));
         }
      }

      for(i = 0; i < rightWhereColumnExp.size(); ++i) {
         if (rightWhereColumnExp.elementAt(i) instanceof String) {
            s_ce = (String)rightWhereColumnExp.elementAt(i);
            if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
               removeCommaInRightColumnExp.addElement(rightWhereColumnExp.elementAt(i));
            }
         } else {
            removeCommaInRightColumnExp.addElement(rightWhereColumnExp.elementAt(i));
         }
      }

      for(i = 0; i < removeCommaInRightColumnExp.size(); ++i) {
         WhereItem newWhereItem = new WhereItem();
         WhereColumn leftWhereColumnInNewWhereItem = new WhereColumn();
         WhereColumn rightWhereColumnInNewWhereItem = new WhereColumn();
         Vector v_lnsc = new Vector();
         Vector v_rnsc = new Vector();
         v_lnsc.addElement(removeCommaInLeftColumnExp.elementAt(0));
         v_rnsc.addElement(removeCommaInRightColumnExp.elementAt(i));
         leftWhereColumnInNewWhereItem.setColumnExpression(v_lnsc);
         rightWhereColumnInNewWhereItem.setColumnExpression(v_rnsc);
         newWhereItem.setLeftWhereExp(leftWhereColumnInNewWhereItem);
         newWhereItem.setRightWhereExp(rightWhereColumnInNewWhereItem);
         if (this.operator2.equalsIgnoreCase("ALL") || this.operator2.equalsIgnoreCase("ANY") || this.operator2.equalsIgnoreCase("SOME")) {
            newWhereItem.setOperator(this.getOperator());
         }

         whereItemsListReplacingEqualsClause.add(newWhereItem);
      }

      return whereItemsListReplacingEqualsClause;
   }

   private boolean containsColumnVariable(Vector colExp, boolean bool) {
      if (colExp != null) {
         for(int i = 0; i < colExp.size(); ++i) {
            if (colExp.get(i) instanceof TableColumn) {
               if (((TableColumn)colExp.get(i)).toString().startsWith("@")) {
                  bool = true;
               }
            } else if (colExp.get(i) instanceof SelectColumn) {
               Vector selColExp = ((SelectColumn)colExp.get(i)).getColumnExpression();
               bool = this.containsColumnVariable(selColExp, bool);
            } else if (colExp.get(i) instanceof FunctionCalls) {
               FunctionCalls fc1 = (FunctionCalls)colExp.get(i);
               Vector FunctionArgs = fc1.getFunctionArguments();
               String fc1Name = fc1.getFunctionNameAsAString();
               if (fc1Name != null && fc1Name.trim().equalsIgnoreCase("CONTAINS")) {
                  this.isContainsFunction = true;
               }

               bool = this.containsColumnVariable(FunctionArgs, bool);
            } else if (colExp.get(i) instanceof String) {
               String str = (String)colExp.get(i);
               if (str.trim().startsWith("@")) {
                  bool = true;
               } else if (str.trim().startsWith("'")) {
                  bool = true;
               } else {
                  try {
                     int intValue = Integer.parseInt(str);
                     bool = true;
                  } catch (NumberFormatException var7) {
                  }
               }
            }
         }
      }

      return bool;
   }

   private void convertLNNVLtoStatement(WhereExpression we) {
      this.casestmtFromLNNVLClause = new CaseStatement();
      this.operator = null;
      SelectColumn scForThenCondition = new SelectColumn();
      Vector colExpForWhenCondition = new Vector();
      TableColumn tc = new TableColumn();
      Vector thenStmts = new Vector();
      WhenStatement whenClause = new WhenStatement();
      SelectColumn scForElseStmt = new SelectColumn();
      Vector elseStmts = new Vector();
      TableColumn tcForElseStmts = new TableColumn();
      this.casestmtFromLNNVLClause.setCaseClause("CASE");
      this.casestmtFromLNNVLClause.setElseClause("ELSE");
      this.casestmtFromLNNVLClause.setEndClause("END");
      whenClause.setWhenClause("WHEN");
      whenClause.setWhenCondition(we);
      whenClause.setThenClause("THEN");
      tc.setColumnName("FALSE");
      thenStmts.add(tc);
      scForThenCondition.setColumnExpression(thenStmts);
      whenClause.setThenStatement(scForThenCondition);
      colExpForWhenCondition.add(whenClause);
      tcForElseStmts.setColumnName("TRUE");
      elseStmts.add(tcForElseStmts);
      scForElseStmt.setColumnExpression(elseStmts);
      this.casestmtFromLNNVLClause.setWhenStatementList(colExpForWhenCondition);
      this.casestmtFromLNNVLClause.setElseStatement(scForElseStmt);
   }

   public boolean checkForWeekOfDayConversion(FunctionCalls funcCall) {
      boolean needsWeekOfDayConversion = false;
      String funcStr = funcCall.toString().toUpperCase();
      if (funcCall.getFunctionName().getColumnName().equalsIgnoreCase("TRUNC") && funcStr.indexOf("MOD") != -1 && funcStr.indexOf("TO_NUMBER") != -1 && funcStr.indexOf("TO_CHAR") != -1 && funcStr.indexOf("TO_DATE") != -1) {
         needsWeekOfDayConversion = true;
      } else if (funcCall.getFunctionName().getColumnName().equalsIgnoreCase("MOD") && funcStr.indexOf("TO_CHAR") != -1) {
         needsWeekOfDayConversion = true;
      }

      return needsWeekOfDayConversion;
   }

   private void handleJulianOrDayFormatForTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, WhereItem wi, SelectColumn sc) throws ConvertException {
      Vector colExp = this.rightWhereExp.getColumnExpression();
      Vector newColExp = new Vector();

      for(int ci = 0; ci < colExp.size(); ++ci) {
         Object colObj = colExp.elementAt(ci);
         if (colObj instanceof FunctionCalls) {
            FunctionCalls wcFunc = (FunctionCalls)colObj;
            if (ci == 0 && wcFunc.getFunctionName().getColumnName().equalsIgnoreCase("trunc") && colExp.size() == 3 && colExp.lastElement().toString().equalsIgnoreCase("1") && colExp.get(colExp.size() - 2).toString().equalsIgnoreCase("+") && this.checkForWeekOfDayConversion(wcFunc)) {
               newColExp.add(this.generateTeradataSysCalendarConstructs(to_sqs, wi, sc));
               colExp.remove(colExp.size() - 1);
               colExp.remove(colExp.size() - 2);
            } else {
               if (!wcFunc.getFunctionName().getColumnName().equalsIgnoreCase("mod") || !this.checkForWeekOfDayConversion(wcFunc)) {
                  throw new ConvertException("Exception occurred in conversion of Oracle 'J' and 'D' date formats to Teradata");
               }

               newColExp.add(this.generateTeradataSysCalendarConstructs(to_sqs, wi, sc));
            }
         } else if (colObj instanceof TableColumn) {
            newColExp.addElement(((TableColumn)colObj).toTeradataSelect(to_sqs, from_sqs));
         } else if (colObj instanceof WhereColumn) {
            newColExp.addElement(((WhereColumn)colObj).toTeradataSelect(to_sqs, from_sqs));
         } else if (colObj instanceof CaseStatement) {
            newColExp.addElement(((CaseStatement)colObj).toTeradataSelect(to_sqs, from_sqs));
         } else if (colObj instanceof SelectQueryStatement) {
            newColExp.addElement(((SelectQueryStatement)colObj).toTeradata());
         } else if (colObj instanceof SelectColumn) {
            newColExp.addElement(((SelectColumn)colObj).toTeradataSelect(to_sqs, from_sqs));
         } else if (colObj instanceof String) {
            String s_ce = (String)colObj;
            if (s_ce.equalsIgnoreCase("**")) {
               this.rightWhereExp.createPowerFunction(newColExp, colExp, ci);
            } else if (s_ce.indexOf(".") != -1 && s_ce.charAt(0) != '\'') {
               String[] elements = s_ce.split("\\.");
               int esize = elements.length;
               if (esize > 0) {
                  StringBuffer newS_ce = new StringBuffer();

                  for(int es = 0; es < esize; ++es) {
                     String elem = elements[es];
                     elem = CustomizeUtil.objectNamesToQuotedIdentifier(elem, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
                     if (elem.equalsIgnoreCase("dual") || elem.equalsIgnoreCase("sys.dual")) {
                        elem = "\"DUAL\"";
                     }

                     if (es == 0) {
                        newS_ce.append(elem);
                     } else {
                        newS_ce.append("." + elem);
                     }
                  }

                  newColExp.addElement(newS_ce.toString());
                  colExp.setElementAt(newS_ce.toString(), ci);
               } else {
                  newColExp.addElement(CustomizeUtil.objectNamesToQuotedIdentifier(s_ce, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1));
               }
            } else {
               newColExp.addElement(s_ce);
            }
         } else {
            newColExp.addElement(colObj);
         }
      }

      WhereColumn newWhereCol = new WhereColumn();
      newWhereCol.setColumnExpression(newColExp);
      wi.setRightWhereExp(newWhereCol);
   }

   private TableColumn generateTeradataSysCalendarConstructs(SelectQueryStatement to_sqs, WhereItem wi, SelectColumn sc) throws ConvertException {
      TableColumn tdSysCalColumn = new TableColumn();
      tdSysCalColumn.setColumnName("day_of_week");
      tdSysCalColumn.setTableName("calendar");
      tdSysCalColumn.setOwnerName("sys_calendar");
      FromTable tdSysCalTable = new FromTable();
      tdSysCalTable.setTableName("sys_calendar.calendar");
      to_sqs.getFromClause().getFromItemList().add(tdSysCalTable);
      WhereItem tdSysCalWi = new WhereItem();
      TableColumn tdSysCalWeek = new TableColumn();
      tdSysCalWeek.setColumnName("calendar_date");
      tdSysCalWeek.setTableName("calendar");
      tdSysCalWeek.setOwnerName("sys_calendar");
      WhereColumn tdlwe = new WhereColumn();
      Vector tdlweExp = new Vector();
      tdlweExp.add(tdSysCalWeek);
      tdlwe.setColumnExpression(tdlweExp);
      tdSysCalWi.setLeftWhereExp(tdlwe);
      WhereColumn tdrwe = new WhereColumn();
      Vector tdrweExp = new Vector();
      tdrweExp.add(sc.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      tdrwe.setColumnExpression(tdrweExp);
      tdSysCalWi.setRightWhereExp(tdrwe);
      tdSysCalWi.setOperator("=");
      wi.setTeradataSysCalendarWhereItem(tdSysCalWi);
      return tdSysCalColumn;
   }

   public void replaceRownumTableColumn(Object newColumn) throws ConvertException {
      if (this.rownumClause != null) {
         this.rownumClause.setRownumClause(newColumn.toString());
      }

      if (this.leftWhereExp != null) {
         this.leftWhereExp.replaceRownumTableColumn(newColumn);
      }

      if (this.rightWhereExp != null) {
         this.rightWhereExp.replaceRownumTableColumn(newColumn);
      }

   }

   public WhereItem toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.movedToFromClause) {
         return null;
      } else if (this.rownumClause != null) {
         return null;
      } else {
         WhereItem wi = new WhereItem();
         wi.setOpenBrace(this.openBraces);
         wi.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
         }

         if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
         }

         this.modifyWhereItemsForLikeOperatorWithoutConstantOnRHS(true);
         this.modifyWhereItemsForEqualAndNotEqualsOperationWithEmptyStringOnRHS();
         if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toVectorWiseSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toVectorWiseSelect(to_sqs, from_sqs));
         }

         if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toVectorWise());
            if (this.rightWhereSubQueryExp != null) {
               wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toVectorWiseSelect(to_sqs, from_sqs));
            }
         }

         WhereColumn wc;
         Vector v_sce;
         Vector newColExp;
         if (this.operator != null) {
            this.modifyWhereExpForLikeOperator(wi, this.operator, "VARCHAR");
            if (this.operator.equalsIgnoreCase("^=")) {
               wi.setOperator("<>");
            } else if (this.operator.equalsIgnoreCase("!>")) {
               wi.setOperator("<=");
            } else if (this.operator.equalsIgnoreCase("!<")) {
               wi.setOperator(">=");
            } else if (this.operator.equalsIgnoreCase("<=>")) {
               if (from_sqs != null && from_sqs.getBooleanValues("can.use.distinct.from.null.safe.equals.operator")) {
                  wi.setOperator("IS NOT DISTINCT FROM");
               } else {
                  wi.setOperator("=");
                  wi.isNullSafeEqualsOperator = true;
               }
            } else {
               Vector v_nsce;
               String s_ce;
               if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                  wi.setOperator("LIKE");
                  if (wi.getRightWhereSubQuery() != null) {
                     throw new ConvertException("Conversion failure.. Subquery can't be converted");
                  }

                  wc = wi.getRightWhereExp();
                  v_nsce = wc.getColumnExpression();
                  v_sce = new Vector();
                  if (v_nsce != null) {
                     if (v_nsce.size() != 1) {
                        throw new ConvertException("Conversion failure..Expressions can't be converted");
                     }

                     s_ce = v_nsce.elementAt(0).toString();
                     if (s_ce.charAt(0) == '\'') {
                        s_ce = s_ce.replace('\'', ' ').trim();
                        s_ce = "'%" + s_ce + "%'";
                     } else if (s_ce.charAt(0) == '"') {
                        s_ce = s_ce.replace('"', ' ').trim();
                        s_ce = "\"%" + s_ce + "%\"";
                     } else {
                        s_ce = "'%" + s_ce + "%'";
                     }

                     v_sce.addElement(s_ce);
                     wc.setColumnExpression(v_sce);
                  }

                  wi.setRightWhereExp(wc);
               } else if (!this.operator.equalsIgnoreCase("MATCHES") && !this.operator.equalsIgnoreCase("NOT MATCHES")) {
                  if (this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN") && wi.getRightWhereExp() != null) {
                     wc = wi.getRightWhereExp();
                     v_nsce = new Vector();
                     v_sce = wc.getColumnExpression();
                     this.modifyUnionQueryAndReplaceAllFetchAndOrderByInsideINClause(wi);
                     if (v_sce != null) {
                        if (v_sce.size() > 0 && !(v_sce.elementAt(0) instanceof String)) {
                           v_nsce.addElement("(");

                           for(int i = 0; i < v_sce.size(); ++i) {
                              v_nsce.addElement(v_sce.elementAt(i));
                           }

                           v_nsce.addElement(")");
                           wc.setColumnExpression(v_nsce);
                        }

                        boolean canHandleNullsInsideINClause = from_sqs != null && from_sqs.getBooleanValues("can.handle.nulls.inside.in.clause");
                        newColExp = this.replaceNULLWithIFNULLFn(wc.getColumnExpression(), canHandleNullsInsideINClause);
                        if (newColExp != null && !newColExp.isEmpty()) {
                           wc.setColumnExpression(newColExp);
                        }
                     }

                     wi.setOperator(this.operator);
                  } else {
                     wi.setOperator(this.operator);
                  }
               } else {
                  if (this.operator.equalsIgnoreCase("MATCHES")) {
                     wi.setOperator("LIKE");
                  } else {
                     wi.setOperator("NOT LIKE");
                  }

                  wc = wi.getRightWhereExp();
                  v_nsce = wc.getColumnExpression();
                  v_sce = new Vector();
                  if (v_nsce != null) {
                     s_ce = v_nsce.elementAt(0).toString();
                     if (s_ce.indexOf("*") != -1) {
                        s_ce = s_ce.replace('*', '%');
                     }

                     v_sce.add(s_ce);
                     wc.setColumnExpression(v_sce);
                  }
               }
            }

            this.modifyOperatorForNULL(wi, this.operator);
         }

         if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            wc = wi.getRightWhereExp();
            WhereColumn l_wc = wi.getLeftWhereExp();
            if (wc != null && wc.getColumnExpression().size() != 1) {
               v_sce = new Vector();
               Vector v_nrsce = new Vector();
               newColExp = l_wc.getColumnExpression();
               Vector v_rsce = wc.getColumnExpression();
               Vector v_removedcomma_right_column_exp = new Vector();
               Vector v_removedcomma_left_column_exp = new Vector();

               int i;
               String s_ce;
               for(i = 0; i < newColExp.size(); ++i) {
                  if (newColExp.elementAt(i) instanceof String) {
                     s_ce = (String)newColExp.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_left_column_exp.addElement(newColExp.elementAt(i));
                     }
                  } else {
                     v_removedcomma_left_column_exp.addElement(newColExp.elementAt(i));
                  }
               }

               for(i = 0; i < v_rsce.size(); ++i) {
                  if (v_rsce.elementAt(i) instanceof String) {
                     s_ce = (String)v_rsce.elementAt(i);
                     if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                     }
                  } else {
                     v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                  }
               }

               v_sce.addElement(v_removedcomma_left_column_exp.elementAt(0));
               v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
               l_wc.setColumnExpression(v_sce);
               wc.setColumnExpression(v_nrsce);

               for(i = 1; i < v_removedcomma_right_column_exp.size(); ++i) {
                  WhereItem n_wi = new WhereItem();
                  WhereColumn l_nwc = new WhereColumn();
                  WhereColumn r_nwc = new WhereColumn();
                  Vector v_lnsc = new Vector();
                  Vector v_rnsc = new Vector();
                  v_lnsc.addElement(v_removedcomma_left_column_exp.elementAt(0));
                  v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(i));
                  l_nwc.setColumnExpression(v_lnsc);
                  r_nwc.setColumnExpression(v_rnsc);
                  n_wi.setLeftWhereExp(l_nwc);
                  n_wi.setRightWhereExp(r_nwc);
                  n_wi.setOperator(this.operator);
                  if (i == newColExp.size() - 1) {
                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  } else {
                     if (this.operator2.equalsIgnoreCase("ALL")) {
                        from_sqs.getWhereExpression().addOperator("AND");
                     } else {
                        from_sqs.getWhereExpression().addOperator("OR");
                     }

                     from_sqs.getWhereExpression().addWhereItem(n_wi);
                  }
               }
            }
         }

         if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
         }

         if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
         }

         if (this.LeftJoin != null) {
            wi.setLeftJoin((String)null);
         }

         if (this.RightJoin != null) {
            wi.setRightJoin((String)null);
         }

         return wi;
      }
   }

   public WhereItem toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereItem whItemConv = new WhereItem();
      if (this.openBraces != null) {
         whItemConv.setOpenBrace(this.openBraces);
      }

      if (this.closeBraces != null) {
         whItemConv.setCloseBrace(this.closeBraces);
      }

      if (this.leftWhereExp != null) {
         whItemConv.setLeftWhereExp(this.leftWhereExp.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.rightWhereExp != null) {
         whItemConv.setRightWhereExp(this.rightWhereExp.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.LeftJoin != null) {
         whItemConv.setLeftJoin(this.LeftJoin);
      }

      if (this.RightJoin != null) {
         whItemConv.setRightJoin(this.RightJoin);
      }

      if (this.rightWhereSubQuery != null) {
         this.rightWhereSubQuery.setPropAndHandlerFromSQS(from_sqs);
         whItemConv.setRightWhereSubQuery(this.rightWhereSubQuery.toReplaceTblCol());
         if (this.rightWhereSubQueryExp != null) {
            whItemConv.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toReplaceTblCol(to_sqs, from_sqs));
         }
      }

      if (this.beginOperator != null) {
         whItemConv.setBeginOperator(this.beginOperator);
      }

      if (this.operator != null) {
         whItemConv.setOperator(this.operator);
      }

      if (this.operator1 != null) {
         whItemConv.setOperator(this.operator1);
      }

      if (this.operator2 != null) {
         whItemConv.setOperator2(this.operator2);
      }

      if (this.operator3 != null) {
         whItemConv.setOperator2(this.operator3);
      }

      if (this.rownumClause != null) {
         whItemConv.setRownumClause(this.rownumClause.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.regExp != null) {
         Vector regExpConv = new Vector();
         int i = 0;

         for(int regExpSize = this.regExp.size(); i < regExpSize; ++i) {
            if (this.regExp.get(i) instanceof SelectColumn) {
               regExpConv.addElement(((SelectColumn)this.regExp.get(i)).toReplaceTblCol(to_sqs, from_sqs));
            } else {
               regExpConv.addElement(this.regExp.get(i));
            }
         }

         whItemConv.setRegExp(regExpConv);
      }

      if (this.commentObj != null) {
         whItemConv.setCommentClass(this.commentObj);
      }

      if (this.commentObjAfterToken != null) {
         whItemConv.setCommentClassAfterToken(this.commentObjAfterToken);
      }

      return whItemConv;
   }

   public void modifyWhereItemsForEqualAndNotEqualsOperationWithEmptyStringOnRHS() {
      if (this.operator != null && (this.operator.equals("=") || this.operator.equals("!=") || this.operator.equals("<>")) && this.rightWhereExp != null && this.rightWhereExp instanceof WhereColumn) {
         Vector rightWhereColExp = this.rightWhereExp.getColumnExpression();
         if (this.isRightWhereExpContainsToken(rightWhereColExp) && rightWhereColExp.get(0) instanceof String && (rightWhereColExp.get(0).toString().replaceAll("\\s", "").equals("''") || rightWhereColExp.get(0).toString().replaceAll("\\s", "").equalsIgnoreCase("'null'")) && this.leftWhereExp != null) {
            Vector newColumnExp = this.modifyColumnExpressionToCastExp(this.leftWhereExp.getColumnExpression(), "CHAR");
            if (newColumnExp != null) {
               this.leftWhereExp.setColumnExpression(newColumnExp);
            }
         }
      }

   }

   public void modifyWhereItemsForStringLiteralsOnRHS(boolean isCastingAllowed) {
      if (isCastingAllowed && this.operator != null) {
         Vector rightWhereColExp;
         Vector newColumnExp;
         if (!this.operator.equals("=") && !this.operator.equals("!=") && !this.operator.equals("<>")) {
            if (!this.operator.trim().equalsIgnoreCase("in") && !this.operator.trim().equalsIgnoreCase("not in")) {
               if ((this.operator.equals(">") || this.operator.equals("<") || this.operator.equals("<=") || this.operator.equals(">=")) && this.rightWhereExp != null && this.rightWhereExp instanceof WhereColumn) {
                  rightWhereColExp = this.rightWhereExp.getColumnExpression();
                  if (this.isRightWhereExpContainsToken(rightWhereColExp) && rightWhereColExp.get(0) instanceof String && rightWhereColExp.get(0).toString().matches("'.*[a-zA-Z<>\"']+.*'") && this.leftWhereExp != null) {
                     newColumnExp = this.modifyColumnExpressionToCastExp(this.rightWhereExp.getColumnExpression(), "CHAR");
                     if (newColumnExp != null) {
                        this.rightWhereExp.setColumnExpression(newColumnExp);
                     }
                  }
               }
            } else if (this.rightWhereExp != null && this.rightWhereExp instanceof WhereColumn) {
               rightWhereColExp = this.rightWhereExp.getColumnExpression();
               if (rightWhereColExp != null && !rightWhereColExp.isEmpty()) {
                  boolean isStringLiteralsPresent = false;
                  Iterator var4 = rightWhereColExp.iterator();

                  while(var4.hasNext()) {
                     Object object = var4.next();
                     if (object != null && object instanceof WhereColumn) {
                        Vector expVec = ((WhereColumn)object).getColumnExpression();
                        if (this.isRightWhereExpContainsToken(expVec) && expVec.get(0) instanceof String && expVec.get(0).toString().trim().matches("'.*[a-zA-Z<>\"']+.*'")) {
                           isStringLiteralsPresent = true;
                           break;
                        }
                     }
                  }

                  if (isStringLiteralsPresent && this.leftWhereExp != null) {
                     Vector newColumnExp = this.modifyColumnExpressionToCastExp(this.leftWhereExp.getColumnExpression(), "CHAR");
                     if (newColumnExp != null) {
                        this.leftWhereExp.setColumnExpression(newColumnExp);
                     }
                  }
               }
            }
         } else if (this.rightWhereExp != null && this.rightWhereExp instanceof WhereColumn) {
            rightWhereColExp = this.rightWhereExp.getColumnExpression();
            if (this.isRightWhereExpContainsToken(rightWhereColExp) && rightWhereColExp.get(0) instanceof String && rightWhereColExp.get(0).toString().matches("'.*[a-zA-Z<>\"']+.*'") && this.leftWhereExp != null) {
               newColumnExp = this.modifyColumnExpressionToCastExp(this.leftWhereExp.getColumnExpression(), "CHAR");
               if (newColumnExp != null) {
                  this.leftWhereExp.setColumnExpression(newColumnExp);
               }
            }
         }
      }

   }

   public boolean isRightWhereExpContainsToken(Vector colExp) {
      boolean isRightWhereExpContainsToken = false;

      try {
         if (colExp != null && (colExp.size() == 1 || colExp.size() == 2 && (colExp.get(1) instanceof Token && colExp.get(1).toString().startsWith("/*") && colExp.get(1).toString().endsWith("*/") || colExp.get(1) instanceof String && colExp.get(1).toString().isEmpty()))) {
            isRightWhereExpContainsToken = true;
         }
      } catch (Exception var4) {
         isRightWhereExpContainsToken = false;
      }

      return isRightWhereExpContainsToken;
   }

   public void modifyWhereItemsForLikeOperatorWithoutConstantOnRHS(boolean isVectorwise) {
      boolean castBothSides = false;
      String dataType = "CHAR";
      if (this.operator != null && (this.operator.equalsIgnoreCase("like") || this.operator.equalsIgnoreCase("not like") || this.operator.equalsIgnoreCase("matches") || this.operator.equalsIgnoreCase("not matches"))) {
         String newOperator = "=";
         if (this.operator.equalsIgnoreCase("not like") || this.operator.equalsIgnoreCase("not matches")) {
            newOperator = "!=";
         }

         if (this.rightWhereExp != null && this.rightWhereExp instanceof WhereColumn) {
            Vector rightWhereColExp = this.rightWhereExp.getColumnExpression();
            if (this.isRightWhereExpContainsToken(rightWhereColExp)) {
               boolean convertToSubs = false;
               int positionFound = false;
               Object object = rightWhereColExp.get(0);
               if (object instanceof TableColumn) {
                  this.operator = newOperator;
                  castBothSides = true;
               } else if (object instanceof FunctionCalls) {
                  boolean startsWith = false;
                  boolean endsWith = false;
                  FunctionCalls fc = (FunctionCalls)object;
                  Vector fnArgs = fc.getFunctionArguments();
                  String functionName = fc.getFunctionName().getColumnName();
                  if (functionName.equalsIgnoreCase("CONCAT")) {
                     if (fnArgs != null) {
                        for(int i = 0; i < fnArgs.size(); ++i) {
                           Object argObj = fnArgs.get(i);
                           if (argObj instanceof SelectColumn) {
                              SelectColumn sc = (SelectColumn)argObj;
                              Vector colExp = sc.getColumnExpression();
                              if (colExp != null && colExp.size() == 1 && colExp.get(0) != null && colExp.get(0) instanceof String && colExp.get(0).toString().contains("%")) {
                                 if (!isVectorwise) {
                                    convertToSubs = true;
                                    break;
                                 }

                                 String str = colExp.get(0).toString();
                                 str = str.replaceAll("'", "");
                                 if (str.startsWith("%") && i == 0) {
                                    startsWith = true;
                                    convertToSubs = true;
                                 } else if (str.endsWith("%") && i + 1 == fnArgs.size()) {
                                    endsWith = true;
                                    convertToSubs = true;
                                 }
                              }
                           }
                        }

                        if (convertToSubs) {
                           if (isVectorwise && this.leftWhereExp != null && this.leftWhereExp instanceof WhereColumn) {
                              if (startsWith && !endsWith) {
                                 this.modifyLeftWhereExpForLikeExpWithoutConstantPatternOnRHS(fc, "RIGHT");
                                 castBothSides = true;
                              } else if (endsWith && !startsWith) {
                                 this.modifyLeftWhereExpForLikeExpWithoutConstantPatternOnRHS(fc, "LEFT");
                                 castBothSides = true;
                              } else if (startsWith && endsWith) {
                                 this.modifyLikeExpUsingPositionFunctionForSearchingInBetweenString(fc);
                              }
                           }
                        } else {
                           this.operator = newOperator;
                           castBothSides = true;
                        }
                     }
                  } else if ((!functionName.equalsIgnoreCase("CAST") || fnArgs.size() != 2) && !functionName.equalsIgnoreCase("UPPER") && !functionName.equalsIgnoreCase("LOWER")) {
                     if (isVectorwise) {
                        this.operator = newOperator;
                     }

                     castBothSides = true;
                  } else if (fnArgs.get(0) instanceof SelectColumn) {
                     SelectColumn sc = (SelectColumn)fnArgs.get(0);
                     Vector colExp = sc.getColumnExpression();
                     if (colExp != null && colExp.size() == 1 && colExp.get(0) != null) {
                        if (colExp.get(0) instanceof String && !colExp.get(0).toString().contains("%")) {
                           this.operator = newOperator;
                           castBothSides = true;
                        } else if (colExp.get(0) instanceof TableColumn) {
                           this.operator = newOperator;
                           castBothSides = true;
                        }
                     }
                  } else if (fnArgs.get(0) instanceof TableColumn) {
                     this.operator = newOperator;
                     castBothSides = true;
                  }
               }
            }
         }
      }

      if (castBothSides) {
         Vector newColumnExp;
         if (this.leftWhereExp != null) {
            newColumnExp = this.modifyColumnExpressionToCastExp(this.leftWhereExp.getColumnExpression(), "CHAR");
            if (newColumnExp != null) {
               this.leftWhereExp.setColumnExpression(newColumnExp);
            }
         }

         if (this.rightWhereExp != null) {
            newColumnExp = this.modifyColumnExpressionToCastExp(this.rightWhereExp.getColumnExpression(), "CHAR");
            if (newColumnExp != null) {
               this.rightWhereExp.setColumnExpression(newColumnExp);
            }
         }
      }

   }

   public void modifyLikeExpUsingPositionFunctionForSearchingInBetweenString(FunctionCalls fc) {
      FunctionCalls positionFC = new FunctionCalls();
      TableColumn tc = new TableColumn();
      tc.setColumnName("LOCATE");
      positionFC.setFunctionName(tc);
      SelectColumn sc2 = new SelectColumn();
      Vector colExp2 = new Vector();
      Vector newExp = this.modifyColumnExpressionToCastExp(this.leftWhereExp.getColumnExpression(), "CHAR");
      if (newExp != null) {
         colExp2.addAll(newExp);
      } else {
         colExp2.addAll(this.leftWhereExp.getColumnExpression());
      }

      sc2.setColumnExpression(colExp2);
      Vector colExpr;
      if (fc != null) {
         Vector fnArgs = fc.getFunctionArguments();
         if (fnArgs != null) {
            for(int i = 0; i < fnArgs.size(); ++i) {
               Object element = fnArgs.get(i);
               if (element instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)element;
                  colExpr = sc.getColumnExpression();
                  if (colExpr != null && colExpr.size() == 1 && colExpr.get(0) != null && colExpr.get(0) instanceof String && colExpr.get(0).toString().contains("%")) {
                     Vector ce = new Vector();
                     ce.add(colExpr.get(0).toString().replaceAll("%", ""));
                     sc.setColumnExpression(ce);
                  }
               }
            }
         }
      }

      SelectColumn sc1 = new SelectColumn();
      Vector colExp1 = new Vector();
      colExp1.add(fc);
      sc1.setColumnExpression(colExp1);
      Vector fnArgs = new Vector();
      fnArgs.add(sc1);
      fnArgs.add(sc2);
      positionFC.setFunctionArguments(fnArgs);
      Vector finalExp = new Vector();
      finalExp.add(positionFC);
      this.leftWhereExp.setColumnExpression(finalExp);
      this.operator = ">";
      colExpr = new Vector();
      colExpr.add("0");
      this.rightWhereExp.setColumnExpression(colExpr);
   }

   public void modifyLeftWhereExpForLikeExpWithoutConstantPatternOnRHS(FunctionCalls fc, String functionNameToChange) {
      FunctionCalls rightFC = new FunctionCalls();
      TableColumn tc = new TableColumn();
      tc.setColumnName(functionNameToChange);
      rightFC.setFunctionName(tc);
      SelectColumn selCol = new SelectColumn();
      Vector colExp = new Vector();
      colExp.addAll(this.leftWhereExp.getColumnExpression());
      selCol.setColumnExpression(colExp);
      FunctionCalls lengthFC = new FunctionCalls();
      TableColumn tc1 = new TableColumn();
      tc1.setColumnName("LENGTH");
      lengthFC.setFunctionName(tc1);
      Vector fnArgs;
      Vector colExpr;
      if (fc != null) {
         fnArgs = fc.getFunctionArguments();
         if (fnArgs != null) {
            for(int i = 0; i < fnArgs.size(); ++i) {
               Object element = fnArgs.get(i);
               if (element instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)element;
                  colExpr = sc.getColumnExpression();
                  if (colExpr != null && colExpr.size() == 1 && colExpr.get(0) != null && colExpr.get(0) instanceof String && colExpr.get(0).toString().contains("%")) {
                     Vector ce = new Vector();
                     ce.add(colExpr.get(0).toString().replaceAll("%", ""));
                     sc.setColumnExpression(ce);
                  }
               }
            }
         }
      }

      fnArgs = new Vector();
      fnArgs.add(fc);
      lengthFC.setFunctionArguments(fnArgs);
      SelectColumn selCol2 = new SelectColumn();
      Vector colExp3 = new Vector();
      colExp3.add(lengthFC);
      selCol2.setColumnExpression(colExp3);
      Vector colExp4 = new Vector();
      colExp4.add(selCol);
      colExp4.add(selCol2);
      rightFC.setFunctionArguments(colExp4);
      colExpr = new Vector();
      colExpr.add(rightFC);
      this.leftWhereExp.setColumnExpression(colExpr);
      this.operator = "=";
   }

   public boolean checkForCasting(Vector colExp) {
      boolean needsCasting = true;

      try {
         if (colExp != null && colExp.size() == 1 && colExp.get(0) instanceof FunctionCalls) {
            FunctionCalls fc = (FunctionCalls)colExp.get(0);
            String functionNameStr = fc.getFunctionNameAsAString() != null ? fc.getFunctionNameAsAString().trim().toUpperCase() : "";
            if (!functionNameStr.isEmpty() && functionNameStr.equalsIgnoreCase("CAST") && fc.getFunctionArguments().size() == 2 && fc.getFunctionArguments().get(1) instanceof CharacterClass) {
               needsCasting = false;
            } else if (fc.getFunctionArguments() == null || !fc.getFunctionArguments().isEmpty() || !functionNameStr.startsWith("CAST(") || !functionNameStr.endsWith("CHAR)") && !functionNameStr.endsWith("VARCHAR)") && !functionNameStr.endsWith("TEXT)")) {
               Vector list = StringFunctions.getStringFunctionsListForCasting();

               for(int i = 0; i < list.size(); ++i) {
                  String functionNameString = list.get(i).toString();
                  if (functionNameStr.startsWith(functionNameString)) {
                     needsCasting = false;
                     break;
                  }
               }
            } else {
               needsCasting = false;
            }
         }
      } catch (Exception var8) {
      }

      return needsCasting;
   }

   public Vector modifyColumnExpressionToCastExp(Vector colExp, String dataType) {
      Vector newColumnExpr = null;

      try {
         if (this.checkForCasting(colExp)) {
            cast castFunction = new cast();
            TableColumn tc1 = new TableColumn();
            CharacterClass charClass = new CharacterClass();
            tc1.setColumnName("CAST");
            castFunction.setFunctionName(tc1);
            castFunction.setAsDatatype("AS");
            charClass.setDatatypeName(dataType);
            Vector newFunctionArgs = new Vector();
            SelectColumn sc = new SelectColumn();
            sc.setColumnExpression(colExp);
            newFunctionArgs.add(0, sc);
            newFunctionArgs.add(1, charClass);
            castFunction.setFunctionArguments(newFunctionArgs);
            newColumnExpr = new Vector();
            newColumnExpr.add(0, castFunction);
         }
      } catch (Exception var9) {
      }

      return newColumnExpr;
   }

   public void modifyWhereExpForLikeOperator(WhereItem wi, String operator, String dataType) {
      if (wi != null && wi.getLeftWhereExp() != null && wi.getLeftWhereExp().getColumnExpression() != null) {
         WhereColumn whereLeftExp = wi.getLeftWhereExp();
         Vector whereLeftColExpOld = whereLeftExp.getColumnExpression();

         try {
            if ((operator.equalsIgnoreCase("LIKE") || operator.equalsIgnoreCase("NOT LIKE") || operator.equalsIgnoreCase("MATCHES") || operator.equalsIgnoreCase("NOT MATCHES") || operator.equalsIgnoreCase("~") | operator.equalsIgnoreCase("~*")) && this.checkForCasting(whereLeftColExpOld)) {
               Vector newColumnExpr = this.modifyColumnExpressionToCastExp(whereLeftColExpOld, dataType);
               if (newColumnExpr != null) {
                  whereLeftExp.setColumnExpression(newColumnExpr);
               }
            }
         } catch (Exception var7) {
            if (whereLeftColExpOld != null) {
               whereLeftExp.setColumnExpression(whereLeftColExpOld);
            }
         }
      }

   }

   public void modifyOperatorForNULL(WhereItem wi, String operator) {
      Vector oldExp = null;

      try {
         if ((operator.equalsIgnoreCase("=") || operator.equalsIgnoreCase("!=") || operator.equalsIgnoreCase("<>")) && wi.getRightWhereExp() != null && wi.getRightWhereExp() instanceof WhereColumn && this.isRightWhereExpContainsToken(wi.getRightWhereExp().getColumnExpression()) && wi.getRightWhereExp().getColumnExpression().get(0) instanceof String && wi.getRightWhereExp().getColumnExpression().get(0).toString().equalsIgnoreCase("null")) {
            oldExp = wi.getRightWhereExp().getColumnExpression();
            wi.getRightWhereExp().setColumnExpression(this.getColumnExpressionForNULL());
         }
      } catch (Exception var5) {
         if (oldExp != null) {
            wi.getRightWhereExp().setColumnExpression(oldExp);
         }
      }

   }

   public Vector replaceNULLWithIFNULLFn(Vector colExp, boolean canHandleNullsInsideINClause) {
      Vector newColExp = null;

      try {
         if (colExp != null && colExp.size() > 0) {
            Vector removeItems = new Vector();

            int index;
            for(index = 0; index < colExp.size(); ++index) {
               if (colExp.get(index) instanceof WhereColumn) {
                  WhereColumn wherCol = (WhereColumn)colExp.get(index);
                  Vector whereColExp = wherCol.getColumnExpression();

                  try {
                     if (whereColExp != null && whereColExp.size() == 1 && (whereColExp.get(0) instanceof String && (whereColExp.get(0).toString().equalsIgnoreCase("null") || whereColExp.get(0).toString().equalsIgnoreCase("coalesce(null)")) || whereColExp.get(0) instanceof SelectColumn && ((SelectColumn)whereColExp.get(0)).getColumnExpression() != null && ((SelectColumn)whereColExp.get(0)).getColumnExpression().size() == 1 && (((SelectColumn)whereColExp.get(0)).getColumnExpression().get(0).toString().equalsIgnoreCase("null") || ((SelectColumn)whereColExp.get(0)).getColumnExpression().get(0).toString().equalsIgnoreCase("coalesce(null)")))) {
                        removeItems.add(index);
                     }
                  } catch (Exception var10) {
                     removeItems.clear();
                     break;
                  }
               }
            }

            try {
               if (!removeItems.isEmpty()) {
                  if (removeItems.size() == 1) {
                     index = Integer.parseInt(removeItems.get(0).toString());
                     Object exp = colExp.get(index);
                     if (exp instanceof WhereColumn) {
                        ((WhereColumn)exp).setColumnExpression(this.getColumnExpressionForNULL());
                     } else if (exp instanceof String) {
                        colExp.setElementAt("COALESCE(NULL)", index);
                     }
                  } else if (canHandleNullsInsideINClause) {
                     Vector tempColExp = new Vector();

                     int k;
                     for(k = 0; k < colExp.size(); ++k) {
                        if (!removeItems.contains(k) && (!(colExp.get(k) instanceof String) || !colExp.get(k).toString().isEmpty() && !colExp.get(k).toString().equals(",") && !colExp.get(k).toString().equals("(") && !colExp.get(k).toString().equals(")"))) {
                           tempColExp.add(colExp.get(k));
                        }
                     }

                     if (tempColExp.isEmpty()) {
                        newColExp = new Vector();
                        newColExp.add("(");
                        newColExp.add("COALESCE(NULL)");
                        newColExp.add(")");
                     } else {
                        newColExp = new Vector();
                        newColExp.add("(");

                        for(k = 0; k < tempColExp.size(); ++k) {
                           if (k > 0) {
                              newColExp.add(",");
                           }

                           newColExp.add(tempColExp.get(k));
                        }

                        newColExp.add(")");
                     }
                  }
               }
            } catch (Exception var9) {
               newColExp = null;
            }
         }
      } catch (Exception var11) {
         newColExp = null;
      }

      return newColExp;
   }

   public Vector getColumnExpressionForNULL() throws Exception {
      ifnull ifnullfunction = new ifnull();
      TableColumn tc1 = new TableColumn();
      tc1.setColumnName("COALESCE");
      ifnullfunction.setFunctionName(tc1);
      Vector newFunctionArgs = new Vector();
      Vector nullExp = new Vector();
      nullExp.add("NULL");
      SelectColumn sc = new SelectColumn();
      sc.setColumnExpression(nullExp);
      newFunctionArgs.add(0, sc);
      ifnullfunction.setFunctionArguments(newFunctionArgs);
      Vector newColumnExpr = new Vector();
      newColumnExpr.add(0, ifnullfunction);
      return newColumnExpr;
   }

   public void modifyUnionQueryAndReplaceAllFetchAndOrderByInsideINClause(WhereItem wi) {
      try {
         if (wi.getRightWhereSubQuery() != null && wi.getRightWhereSubQuery() instanceof SelectQueryStatement) {
            SelectQueryStatement queryStatement;
            if (wi.getRightWhereSubQuery().getSetOperatorClause() != null) {
               queryStatement = new SelectQueryStatement();
               SelectStatement selectStatement = new SelectStatement();
               selectStatement.setSelectClause("SELECT");
               SelectColumn selectColumn = new SelectColumn();
               Vector selectItemList = new Vector();
               Vector selectColExp = new Vector();
               selectColExp.add("*");
               selectColumn.setColumnExpression(selectColExp);
               selectItemList.add(selectColumn);
               selectStatement.setSelectItemList(selectItemList);
               Vector fromItemList = new Vector();
               FromTable fromTable = new FromTable();
               fromTable.setTableName(wi.getRightWhereSubQuery());
               fromTable.setAliasName("T0_ALIAS");
               fromTable.setIsAS(true);
               fromItemList.add(fromTable);
               FromClause fromClause = new FromClause();
               fromClause.setFromClause("FROM");
               fromClause.setFromItemList(fromItemList);
               queryStatement.setSelectStatement(selectStatement);
               queryStatement.setFromClause(fromClause);
               wi.setRightWhereSubQuery(queryStatement);
            }

            queryStatement = wi.getRightWhereSubQuery();
            queryStatement.setFetchClause((FetchClause)null);
            queryStatement.setLimitClause((LimitClause)null);
            queryStatement.setOrderByStatement((OrderByStatement)null);
            if (queryStatement.getFromClause() != null && queryStatement.getFromClause().getFromItemList() != null && queryStatement.getFromClause().getFromItemList().size() > 0) {
               Vector fromItemList = queryStatement.getFromClause().getFromItemList();

               for(int i = 0; i < fromItemList.size(); ++i) {
                  Object fromTable = fromItemList.get(i);
                  if (fromTable instanceof FromTable && ((FromTable)fromTable).getTableName() instanceof SelectQueryStatement) {
                     SelectQueryStatement qStatement = (SelectQueryStatement)((SelectQueryStatement)((FromTable)fromTable).getTableName());
                     qStatement.setFetchClause((FetchClause)null);
                     qStatement.setLimitClause((LimitClause)null);
                     qStatement.setOrderByStatement((OrderByStatement)null);
                  }
               }
            }
         }
      } catch (Exception var10) {
      }

   }
}
