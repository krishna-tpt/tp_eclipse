package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.functions.misc.decode;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

public class WhereExpression {
   private String openBraces = new String("");
   private String closeBraces = new String("");
   private Vector whereItems = new Vector();
   private RownumClause rownumClause;
   private Vector operators = new Vector();
   private boolean topLevel = false;
   private String beginOperator;
   private boolean checkWhere;
   private String concatenation;
   private UserObjectContext context = null;
   private boolean isToOracle = false;
   private ArrayList fromTableList = null;
   private CommentClass commentObj;
   private CommentClass commentObjAfterToken;
   private String stmtTableName;
   private Vector functionColumnVector = new Vector();
   private Vector removedFromItemsList = null;
   private boolean selectStmtInJoin = false;
   private boolean fromDeleteQueryStatement = false;
   private boolean thetaJoinPresent = false;
   private boolean is_Case_Expression = false;
   private int targetDatabase;
   private int whereExpressionNestedIfCount;
   private String instanceDatatype = "UNDEFINED";

   public Vector getFunctionColumnVector() {
      return this.functionColumnVector;
   }

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void addWhereItem(WhereItem wi) {
      wi.setObjectContext(this.context);
      this.whereItems.addElement(wi);
   }

   public void setFromTableList(ArrayList fromTableList) {
      this.fromTableList = fromTableList;
   }

   public void setStmtTableName(String stmtTableName) {
      this.stmtTableName = stmtTableName;
   }

   public void addWhereExpression(WhereExpression we) {
      this.whereItems.addElement(we);
   }

   public void addOperator(String opr) {
      this.operators.addElement(opr);
   }

   public void addOpenBrace(String s) {
      this.openBraces = this.openBraces + s;
   }

   public void addCloseBrace(String s) {
      this.closeBraces = this.closeBraces + s;
   }

   public void removeBrace() {
      if (this.openBraces != null && this.openBraces.length() > 0) {
         this.openBraces = this.openBraces.substring(1);
         if (this.closeBraces != null && this.closeBraces.length() > 0) {
            this.closeBraces = this.closeBraces.substring(1);
         }
      }

   }

   public boolean removeOperator() {
      if (this.operators.size() != 0) {
         this.operators.removeElementAt(this.operators.size() - 1);
         return true;
      } else {
         return false;
      }
   }

   public void setTopLevel(boolean b) {
      this.topLevel = b;
   }

   public void setBeginOperator(String begin) {
      this.beginOperator = begin;
   }

   public void setWhereItem(Vector wi) {
      if (wi != null) {
         for(int i = 0; i < wi.size(); ++i) {
            if (wi.get(i) instanceof WhereItem) {
               ((WhereItem)wi.get(i)).setObjectContext(this.context);
            }
         }
      }

      this.whereItems = wi;
   }

   public void setOperator(Vector opr) {
      this.operators = opr;
   }

   public void setOpenBrace(String s_ob) {
      this.openBraces = s_ob;
   }

   public void setCloseBrace(String s_cb) {
      this.closeBraces = s_cb;
   }

   public void setCheckWhere(boolean b_cw) {
      this.checkWhere = b_cw;
   }

   public void setConcatenation(String concatenation) {
      this.concatenation = concatenation;
   }

   public void setRownumClause(RownumClause rc) {
      this.rownumClause = rc;
   }

   public void setToOracle(boolean isToOracle) {
      this.isToOracle = isToOracle;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public void setCommentClassAfterToken(CommentClass commentObj) {
      this.commentObjAfterToken = commentObj;
   }

   public void setCaseExpressionBool(boolean boolVal) {
      this.is_Case_Expression = boolVal;
   }

   public void setTargetDatabase(int targetDatabase) {
      this.targetDatabase = targetDatabase;
   }

   public void setWhereExpressionNestedIfCount(int val) {
      this.whereExpressionNestedIfCount = val;
   }

   public void setInstanceDatatype(String instanceDatatype) {
      this.instanceDatatype = instanceDatatype;
   }

   public int getWhereExpressionNestedIfCount() {
      return this.whereExpressionNestedIfCount;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public CommentClass getCommentClassAfterToken() {
      return this.commentObjAfterToken;
   }

   public String getConcatenation() {
      return this.concatenation;
   }

   public RownumClause getRownumClause() {
      return this.rownumClause;
   }

   public String getBeginOperator() {
      return this.beginOperator;
   }

   public Vector getWhereItems() {
      return this.whereItems;
   }

   public Vector getWhereItem() {
      return this.whereItems;
   }

   public Vector getOperator() {
      return this.operators;
   }

   public String getOpenBrace() {
      return this.openBraces;
   }

   public String getCloseBrace() {
      return this.closeBraces;
   }

   public boolean getCheckWhere() {
      return this.checkWhere;
   }

   public boolean getTopLevel() {
      return this.topLevel;
   }

   public String getInstanceDatatype() {
      return this.instanceDatatype;
   }

   public void setFromDeleteQueryStatement(boolean b) {
      this.fromDeleteQueryStatement = b;
   }

   public boolean isThetaJoinPresent() {
      return this.thetaJoinPresent;
   }

   public void setThetaJoinPresent(boolean val) {
      this.thetaJoinPresent = true;
   }

   public boolean isCaseExpression() {
      return this.is_Case_Expression;
   }

   public int getTargetDatabase() {
      return this.targetDatabase;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.commentObj != null) {
         sb.append(this.commentObj + " ");
      }

      if (this.beginOperator != null) {
         sb.append(this.beginOperator + " ");
      }

      if (this.openBraces != null && !this.openBraces.equals("")) {
         sb.append(this.openBraces);
      }

      for(int i = 0; i < this.whereItems.size(); ++i) {
         String whereItemString = null;
         if (this.whereItems.elementAt(i) != null) {
            if (this.whereItems.elementAt(i) instanceof WhereItem) {
               ((WhereItem)this.whereItems.elementAt(i)).setObjectContext(this.context);
            } else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               ((WhereExpression)this.whereItems.elementAt(i)).setObjectContext(this.context);
            }

            whereItemString = this.whereItems.elementAt(i).toString();
            if (whereItemString != null) {
               sb.append(whereItemString);
            }
         }

         if (i == this.operators.size() && this.whereItems.elementAt(i) instanceof WhereExpression && whereItemString == null) {
            String s = sb.toString().trim();
            if (s.endsWith("OR")) {
               s = s.substring(0, s.length() - 2);
            } else if (s.endsWith("AND")) {
               s = s.substring(0, s.length() - 3);
            }

            sb = new StringBuffer(s);
         }

         if ((i >= this.operators.size() || !this.operators.elementAt(i).toString().equalsIgnoreCase("&AND")) && i < this.operators.size() && !this.operators.elementAt(i).toString().equalsIgnoreCase("ANSIAND") && !this.operators.elementAt(i).toString().equalsIgnoreCase("ANSIOR")) {
            int j;
            if (this.whereItems.elementAt(i) instanceof WhereExpression && whereItemString == null) {
               if (whereItemString == null && !this.isToOracle) {
                  sb.append("\n");

                  for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
                     sb.append("\t");
                  }

                  sb.append(" " + this.operators.elementAt(i).toString().toUpperCase() + "\t");
               }
            } else {
               sb.append("\n");

               for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
                  sb.append("\t");
               }

               sb.append(" " + this.operators.elementAt(i).toString().toUpperCase() + "\t");
            }
         }
      }

      if (this.closeBraces != null && !this.closeBraces.equals("")) {
         sb.append(this.closeBraces);
      }

      if (this.commentObjAfterToken != null) {
         sb.append(" " + this.commentObjAfterToken);
      }

      String returnString = sb.toString();
      if (returnString.equals("()")) {
         return null;
      } else {
         return returnString;
      }
   }

   public void removeJoinConditionsFromWhereExpression(WhereExpression whereExp, SelectQueryStatement from_sqs) {
      if (!this.fromDeleteQueryStatement) {
         Vector whereItemsList = whereExp.getWhereItems();
         Vector tempOperators = whereExp.getOperator();

         int i;
         for(i = 0; i < whereItemsList.size(); ++i) {
            Object obj = whereItemsList.get(i);
            if (obj != null && obj instanceof WhereItem) {
               WhereItem wi = (WhereItem)obj;
               if (wi.getOperator() != null && wi.getOperator().trim().equals("=")) {
                  new FromTable();
                  new FromTable();
                  WhereColumn wcl = wi.getLeftWhereExp();
                  WhereColumn wcr = wi.getRightWhereExp();
                  Vector leftColumnExp = wcl.getColumnExpression();
                  Vector rightColumnExp = wcr.getColumnExpression();
                  if (leftColumnExp != null && rightColumnExp != null && leftColumnExp.size() == 1 && rightColumnExp.size() == 1 && leftColumnExp.get(0) instanceof TableColumn && rightColumnExp.get(0) instanceof TableColumn) {
                     TableColumn tc1 = (TableColumn)leftColumnExp.get(0);
                     TableColumn tc2 = (TableColumn)rightColumnExp.get(0);
                     FromTable ft1 = MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)tc1);
                     FromTable ft2 = MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)tc2);
                     String s1 = tc1.getColumnName().trim();
                     String s2 = tc2.getColumnName().trim();
                     if (!s1.startsWith("'") && !s2.startsWith("'") && !s1.startsWith("\"") && !s2.startsWith("\"") && ft1 != null && ft2 != null) {
                        whereItemsList.set(i, (Object)null);
                        if (i != 0) {
                           String op = (String)whereExp.getOperator().get(i - 1);
                           if (!op.equals("&AND")) {
                              whereExp.getOperator().setElementAt("&AND", i - 1);
                           } else if (whereExp.getOperator().size() > i) {
                              whereExp.getOperator().setElementAt("&AND", i);
                           }
                        } else if (whereExp.getOperator().size() > i) {
                           whereExp.getOperator().setElementAt("&AND", i);
                        }
                     }
                  }
               }
            }
         }

         for(i = 0; i < whereItemsList.size(); ++i) {
            if (whereItemsList.indexOf((Object)null) != -1) {
               whereItemsList.remove((Object)null);
               if (i >= whereItemsList.size() - 1) {
                  --i;
                  i = i;
               }
            }
         }

         for(i = 0; i < tempOperators.size(); ++i) {
            String s = (String)tempOperators.get(i);
            if (s.trim().equalsIgnoreCase("&AND")) {
               tempOperators.remove(i);
               --i;
               i = i;
            }
         }

         if (whereItemsList.size() == 1) {
            tempOperators.removeAllElements();
         }
      }

   }

   public Vector getJoinConditionsFromWhereExpression(WhereExpression whereExp, SelectQueryStatement from_sqs) {
      Vector joinConditions = new Vector();
      if (!this.fromDeleteQueryStatement) {
         Vector nonNullwhereItemsList = new Vector();

         WhereExpression we;
         int fi;
         for(int wi = 0; wi < whereExp.getWhereItems().size(); ++wi) {
            Object obj = whereExp.getWhereItems().get(wi);
            if (obj instanceof WhereItem) {
               if (obj != null) {
                  nonNullwhereItemsList.add(obj);
               }
            } else if (obj instanceof WhereExpression && obj.toString() != null && ((WhereExpression)obj).getWhereItem() != null && ((WhereExpression)obj).getWhereItem().size() > 0) {
               boolean nonNullWhereItemPresent = false;
               we = (WhereExpression)obj;

               for(fi = 0; fi < we.getWhereItem().size(); ++fi) {
                  if (we.getWhereItem().get(fi) != null) {
                     nonNullWhereItemPresent = true;
                  }
               }

               if (nonNullWhereItemPresent) {
                  nonNullwhereItemsList.add(obj);
               }
            }
         }

         Vector nonNullOperators = new Vector();

         int i;
         for(i = 0; i < whereExp.getOperator().size(); ++i) {
            String obj = whereExp.getOperator().get(i).toString();
            if (!obj.equalsIgnoreCase("&AND")) {
               nonNullOperators.add(obj);
            }
         }

         String sa;
         for(i = 0; i < nonNullwhereItemsList.size(); ++i) {
            Object obj = nonNullwhereItemsList.get(i);
            String op;
            if (obj != null && obj instanceof WhereItem) {
               WhereItem wi = (WhereItem)obj;
               if (wi.getOperator() != null && wi.getOperator().trim().equals("=")) {
                  sa = this.getTableAliasWhereL(wi);
                  op = this.getTableAliasWhereR(wi);
                  if (sa == null || op == null) {
                     WhereColumn wcl = wi.getLeftWhereExp();
                     WhereColumn wcr = wi.getRightWhereExp();
                     new FromTable();
                     new FromTable();
                     Vector leftColumnExp = wcl.getColumnExpression();
                     Vector rightColumnExp = wcr.getColumnExpression();
                     if (leftColumnExp != null && rightColumnExp != null && leftColumnExp.size() == 1 && rightColumnExp.size() == 1 && leftColumnExp.get(0) instanceof TableColumn && rightColumnExp.get(0) instanceof TableColumn) {
                        TableColumn tc1 = (TableColumn)leftColumnExp.get(0);
                        TableColumn tc2 = (TableColumn)rightColumnExp.get(0);
                        FromTable ft1 = MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)tc1);
                        FromTable ft2 = MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)tc2);
                        String s1 = tc1.getColumnName().trim();
                        String s2 = tc2.getColumnName().trim();
                        if (!s1.startsWith("'") && !s2.startsWith("'") && (ft1 != null && ft2 != null || tc1.getTableName() != null && tc2.getTableName() != null)) {
                           sa = tc1.getTableName();
                           op = tc2.getTableName();
                        }
                     }
                  }

                  if (sa != null && op != null && !op.equalsIgnoreCase("") && !sa.equalsIgnoreCase(op)) {
                     String opOuter = "";
                     if (i != 0) {
                        opOuter = (String)whereExp.getOperator().get(i - 1);
                     }

                     if (!opOuter.equalsIgnoreCase("OR")) {
                        joinConditions.add(obj);
                        nonNullwhereItemsList.set(i, (Object)null);
                        if (i != 0) {
                           String op = (String)nonNullOperators.get(i - 1);
                           if (!op.equalsIgnoreCase("OR")) {
                              if (!op.equals("&AND")) {
                                 nonNullOperators.setElementAt("&AND", i - 1);
                              } else if (nonNullOperators.size() > i) {
                                 nonNullOperators.setElementAt("&AND", i - 1);
                              }
                           }
                        } else if (nonNullOperators.size() > i) {
                           nonNullOperators.setElementAt("&AND", i);
                        }
                     }
                  }
               }
            } else if (obj != null && obj instanceof WhereExpression) {
               we = (WhereExpression)obj;
               if (we.getOperator().size() == 0) {
                  Vector localWI = this.getJoinConditionsFromWhereExpression(we, from_sqs);
                  if (localWI.size() > 0) {
                     if (i != 0) {
                        op = (String)nonNullOperators.get(i - 1);
                        if (!op.equalsIgnoreCase("OR")) {
                           if (!op.equals("&AND")) {
                              nonNullOperators.setElementAt("&AND", i - 1);
                           } else if (nonNullOperators.size() > i) {
                              nonNullOperators.setElementAt("&AND", i - 1);
                           }
                        }
                     } else if (nonNullOperators.size() > i) {
                        nonNullOperators.setElementAt("&AND", i);
                     }
                  }

                  joinConditions.addAll(localWI);
               } else if (!we.getOperator().contains("OR") && !we.getOperator().contains("or")) {
                  joinConditions.addAll(this.getJoinConditionsFromGroupedWhereExpression(we, from_sqs));
               }
            }
         }

         Vector newWhereItemsList = new Vector();

         for(int k = 0; k < nonNullwhereItemsList.size(); ++k) {
            Object obj = nonNullwhereItemsList.get(k);
            if (obj instanceof WhereItem) {
               if (obj != null) {
                  newWhereItemsList.add(nonNullwhereItemsList.get(k));
               }
            } else if (obj instanceof WhereExpression && ((WhereExpression)obj).getWhereItem() != null && ((WhereExpression)obj).getWhereItem().size() > 0) {
               boolean nonNullWhereItemPresent = false;
               WhereExpression wexpObj = (WhereExpression)obj;

               for(int wi = 0; wi < wexpObj.getWhereItem().size(); ++wi) {
                  if (wexpObj.getWhereItem().get(wi) != null) {
                     nonNullWhereItemPresent = true;
                  }
               }

               if (nonNullWhereItemPresent) {
                  newWhereItemsList.add(nonNullwhereItemsList.get(k));
               }
            }
         }

         Vector newOperatorsList = new Vector();

         for(int l = 0; l < nonNullOperators.size(); ++l) {
            sa = (String)nonNullOperators.get(l);
            if (!sa.trim().equalsIgnoreCase("&AND")) {
               newOperatorsList.add(sa);
            }
         }

         Vector finalOperatorsList = new Vector();
         if (newOperatorsList.size() > 0) {
            for(fi = 0; fi < newWhereItemsList.size() - 1; ++fi) {
               finalOperatorsList.add(newOperatorsList.get(fi));
            }
         }

         if (newWhereItemsList.size() == 1) {
            finalOperatorsList.removeAllElements();
         }

         whereExp.setWhereItem(newWhereItemsList);
         whereExp.setOperator(finalOperatorsList);
      }

      return joinConditions;
   }

   public Vector getJoinConditionsFromGroupedWhereExpression(WhereExpression whereExp, SelectQueryStatement from_sqs) {
      Vector joinConditions = new Vector();
      if (!this.fromDeleteQueryStatement) {
         Vector nonNullwhereItemsList = new Vector();

         WhereExpression we;
         int fi;
         for(int wi = 0; wi < whereExp.getWhereItems().size(); ++wi) {
            Object obj = whereExp.getWhereItems().get(wi);
            if (obj instanceof WhereItem) {
               if (obj != null) {
                  nonNullwhereItemsList.add(obj);
               }
            } else if (obj instanceof WhereExpression && obj.toString() != null && ((WhereExpression)obj).getWhereItem() != null && ((WhereExpression)obj).getWhereItem().size() > 0) {
               boolean nonNullWhereItemPresent = false;
               we = (WhereExpression)obj;

               for(fi = 0; fi < we.getWhereItem().size(); ++fi) {
                  if (we.getWhereItem().get(fi) != null) {
                     nonNullWhereItemPresent = true;
                  }
               }

               if (nonNullWhereItemPresent) {
                  nonNullwhereItemsList.add(obj);
               }
            }
         }

         Vector nonNullOperators = new Vector();

         int i;
         for(i = 0; i < whereExp.getOperator().size(); ++i) {
            String obj = whereExp.getOperator().get(i).toString();
            if (!obj.equalsIgnoreCase("&AND")) {
               nonNullOperators.add(obj);
            }
         }

         String sa;
         for(i = 0; i < nonNullwhereItemsList.size(); ++i) {
            Object obj = nonNullwhereItemsList.get(i);
            String op;
            if (obj != null && obj instanceof WhereItem) {
               WhereItem wi = (WhereItem)obj;
               if (wi.getOperator() != null && wi.getOperator().trim().equals("=")) {
                  sa = this.getTableAliasWhereL(wi);
                  op = this.getTableAliasWhereR(wi);
                  if (sa == null || op == null) {
                     WhereColumn wcl = wi.getLeftWhereExp();
                     WhereColumn wcr = wi.getRightWhereExp();
                     new FromTable();
                     new FromTable();
                     Vector leftColumnExp = wcl.getColumnExpression();
                     Vector rightColumnExp = wcr.getColumnExpression();
                     if (leftColumnExp != null && rightColumnExp != null && leftColumnExp.size() == 1 && rightColumnExp.size() == 1 && leftColumnExp.get(0) instanceof TableColumn && rightColumnExp.get(0) instanceof TableColumn) {
                        TableColumn tc1 = (TableColumn)leftColumnExp.get(0);
                        TableColumn tc2 = (TableColumn)rightColumnExp.get(0);
                        FromTable ft1 = MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)tc1);
                        FromTable ft2 = MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)tc2);
                        String s1 = tc1.getColumnName().trim();
                        String s2 = tc2.getColumnName().trim();
                        if (!s1.startsWith("'") && !s2.startsWith("'") && (ft1 != null && ft2 != null || tc1.getTableName() != null && tc2.getTableName() != null)) {
                           sa = tc1.getTableName();
                           op = tc2.getTableName();
                        }
                     }
                  }

                  if (sa != null && op != null && !op.equalsIgnoreCase("") && !sa.equalsIgnoreCase(op)) {
                     String opOuter = "";
                     if (i != 0) {
                        opOuter = (String)nonNullOperators.get(i - 1);
                     }

                     if (!opOuter.equalsIgnoreCase("OR")) {
                        joinConditions.add(obj);
                        nonNullwhereItemsList.set(i, (Object)null);
                        if (i != 0) {
                           String op = (String)nonNullOperators.get(i - 1);
                           if (!op.equalsIgnoreCase("OR")) {
                              if (!op.equals("&AND")) {
                                 nonNullOperators.setElementAt("&AND", i - 1);
                              } else if (nonNullOperators.size() > i) {
                                 nonNullOperators.setElementAt("&AND", i - 1);
                              }
                           }
                        } else if (nonNullOperators.size() > i) {
                           nonNullOperators.setElementAt("&AND", i);
                        }
                     }
                  }
               }
            } else if (obj != null && obj instanceof WhereExpression) {
               we = (WhereExpression)obj;
               Vector localWI = this.getJoinConditionsFromWhereExpression(we, from_sqs);
               if (localWI.size() > 0 && !we.hasNonNullWhereItem()) {
                  if (i != 0) {
                     op = (String)nonNullOperators.get(i - 1);
                     if (!op.equalsIgnoreCase("OR")) {
                        if (!op.equals("&AND")) {
                           nonNullOperators.setElementAt("&AND", i - 1);
                        } else if (nonNullOperators.size() > i) {
                           nonNullOperators.setElementAt("&AND", i - 1);
                        }
                     }
                  } else if (nonNullOperators.size() > i) {
                     nonNullOperators.setElementAt("&AND", i);
                  }
               }

               joinConditions.addAll(localWI);
            }
         }

         Vector newWhereItemsList = new Vector();

         for(int k = 0; k < nonNullwhereItemsList.size(); ++k) {
            Object obj = nonNullwhereItemsList.get(k);
            if (obj instanceof WhereItem) {
               if (obj != null) {
                  newWhereItemsList.add(nonNullwhereItemsList.get(k));
               }
            } else if (obj instanceof WhereExpression && ((WhereExpression)obj).getWhereItem() != null && ((WhereExpression)obj).getWhereItem().size() > 0) {
               boolean nonNullWhereItemPresent = false;
               WhereExpression wexpObj = (WhereExpression)obj;

               for(int wi = 0; wi < wexpObj.getWhereItem().size(); ++wi) {
                  if (wexpObj.getWhereItem().get(wi) != null) {
                     nonNullWhereItemPresent = true;
                  }
               }

               if (nonNullWhereItemPresent) {
                  newWhereItemsList.add(nonNullwhereItemsList.get(k));
               }
            }
         }

         Vector newOperatorsList = new Vector();

         for(int l = 0; l < nonNullOperators.size(); ++l) {
            sa = (String)nonNullOperators.get(l);
            if (!sa.trim().equalsIgnoreCase("&AND")) {
               newOperatorsList.add(sa);
            }
         }

         Vector finalOperatorsList = new Vector();
         if (newOperatorsList.size() > 0) {
            for(fi = 0; fi < newWhereItemsList.size() - 1; ++fi) {
               finalOperatorsList.add(newOperatorsList.get(fi));
            }
         }

         if (newWhereItemsList.size() == 1) {
            finalOperatorsList.removeAllElements();
         }

         whereExp.setWhereItem(newWhereItemsList);
         whereExp.setOperator(finalOperatorsList);
      }

      return joinConditions;
   }

   public boolean hasNonNullWhereItem() {
      boolean nonNullWhereItem = false;
      Vector nonNullwhereItemsList = this.getWhereItems();

      for(int k = 0; k < nonNullwhereItemsList.size(); ++k) {
         Object obj = nonNullwhereItemsList.get(k);
         if (obj instanceof WhereItem) {
            if (obj != null) {
               nonNullWhereItem = true;
               break;
            }
         } else if (obj instanceof WhereExpression && ((WhereExpression)obj).getWhereItem() != null && ((WhereExpression)obj).getWhereItem().size() > 0) {
            WhereExpression wexpObj = (WhereExpression)obj;

            for(int wi = 0; wi < wexpObj.getWhereItem().size(); ++wi) {
               if (wexpObj.getWhereItem().get(wi) != null) {
                  nonNullWhereItem = true;
                  break;
               }
            }
         }
      }

      return nonNullWhereItem;
   }

   public WhereExpression toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.rownumClause != null && to_sqs != null) {
         this.addLimitClause(to_sqs, from_sqs);
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      this.setTargetDatabase(5);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         String op;
         if (this.whereItems.elementAt(i) instanceof WhereItem) {
            if (this.whereItems.elementAt(i) == null) {
               whereItemList.addElement((Object)null);
            }

            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                  Vector whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();

                  for(int j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toMySQLSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                           we.getOperator().add("AND");
                        } else {
                           we.getOperator().add("OR");
                        }
                     }
                  }
               } else {
                  whereItemList.addElement(wi.toMySQLSelect(to_sqs, from_sqs));
               }

               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         } else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
            WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toMySQLSelect(to_sqs, from_sqs);
            whereItemList.addElement(internalWE);
            if (internalWE.toString() == null) {
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 5, we);
      }

      Vector WI = we.getWhereItems();
      int maxNestedIfCount = GeneralUtil.checkVectorandGetMaxNestedIfCount(WI, 1);
      we.setWhereExpressionNestedIfCount(maxNestedIfCount);
      return we;
   }

   public WhereExpression toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.rownumClause != null) {
         this.addFetchClause(to_sqs, from_sqs);
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      this.setTargetDatabase(3);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (!(this.whereItems.elementAt(i) instanceof WhereItem)) {
            if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toDB2Select(to_sqs, from_sqs);
               whereItemList.addElement(internalWE);
               if (internalWE.toString() == null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            }
         } else {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            wi.setFromTableList(this.fromTableList);
            wi.setStmtTableName(this.stmtTableName);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                  Vector whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();

                  for(int j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toDB2Select(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                           we.getOperator().add("AND");
                        } else {
                           we.getOperator().add("OR");
                        }
                     }
                  }
               } else {
                  whereItemList.addElement(wi.toDB2Select(to_sqs, from_sqs));
               }

               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 3, we);
      }

      return we;
   }

   public WhereExpression toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.rownumClause != null && to_sqs != null) {
         this.addLimitClause(to_sqs, from_sqs);
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      this.setTargetDatabase(14);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (!(this.whereItems.elementAt(i) instanceof WhereItem)) {
            if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toBigQuerySelect(to_sqs, from_sqs);
               whereItemList.addElement(internalWE);
               if (internalWE.toString() == null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            }
         } else {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               int j;
               Vector whereItemsListReplacingEqualsClause;
               if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingInClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toBigQuerySelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        we.getOperator().add("AND");
                     }
                  }
               } else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toBigQuerySelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                           we.getOperator().add("AND");
                        } else {
                           we.getOperator().add("OR");
                        }
                     }
                  }
               } else {
                  WhereItem wiNew = wi.toBigQuerySelect(to_sqs, from_sqs);
                  if (wiNew.isNullSafeEqualsOperator() && from_sqs != null) {
                     WhereExpression we1 = new WhereExpression();
                     we1.setOpenBrace("(");
                     we1.setCloseBrace(")");
                     WhereItem wi2 = new WhereItem();
                     wi2.setLeftWhereExp(wiNew.getLeftWhereExp());
                     wi2.setOperator("IS NULL");
                     WhereItem wi3 = new WhereItem();
                     wi3.setLeftWhereExp(wiNew.getRightWhereExp());
                     wi3.setOperator("IS NULL");
                     WhereExpression we2 = new WhereExpression();
                     we2.setOpenBrace("(");
                     we2.setCloseBrace(")");
                     Vector wiV = new Vector();
                     wiV.add(wi2);
                     wiV.add(wi3);
                     Vector opV = new Vector();
                     opV.add("AND");
                     we2.setWhereItem(wiV);
                     we2.setOperator(opV);
                     Vector wiV2 = new Vector();
                     wiV2.add(wiNew);
                     wiV2.add(we2);
                     Vector opV2 = new Vector();
                     opV2.add("OR");
                     we1.setWhereItem(wiV2);
                     we1.setOperator(opV2);
                     whereItemList.addElement(we1);
                  } else {
                     whereItemList.addElement(wiNew);
                  }
               }

               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 14, we);
      }

      return we;
   }

   public WhereExpression toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.rownumClause != null && to_sqs != null) {
         this.addLimitClause(to_sqs, from_sqs);
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      this.setTargetDatabase(16);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (!(this.whereItems.elementAt(i) instanceof WhereItem)) {
            if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toAthenaSelect(to_sqs, from_sqs);
               whereItemList.addElement(internalWE);
               if (internalWE.toString() == null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            }
         } else {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               int j;
               Vector whereItemsListReplacingEqualsClause;
               if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingInClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toAthenaSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        we.getOperator().add("AND");
                     }
                  }
               } else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toAthenaSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                           we.getOperator().add("AND");
                        } else {
                           we.getOperator().add("OR");
                        }
                     }
                  }
               } else {
                  WhereItem wiNew = wi.toAthenaSelect(to_sqs, from_sqs);
                  if (wiNew.isNullSafeEqualsOperator() && from_sqs != null) {
                     WhereExpression we1 = new WhereExpression();
                     we1.setOpenBrace("(");
                     we1.setCloseBrace(")");
                     WhereItem wi2 = new WhereItem();
                     wi2.setLeftWhereExp(wiNew.getLeftWhereExp());
                     wi2.setOperator("IS NULL");
                     WhereItem wi3 = new WhereItem();
                     wi3.setLeftWhereExp(wiNew.getRightWhereExp());
                     wi3.setOperator("IS NULL");
                     WhereExpression we2 = new WhereExpression();
                     we2.setOpenBrace("(");
                     we2.setCloseBrace(")");
                     Vector wiV = new Vector();
                     wiV.add(wi2);
                     wiV.add(wi3);
                     Vector opV = new Vector();
                     opV.add("AND");
                     we2.setWhereItem(wiV);
                     we2.setOperator(opV);
                     Vector wiV2 = new Vector();
                     wiV2.add(wiNew);
                     wiV2.add(we2);
                     Vector opV2 = new Vector();
                     opV2.add("OR");
                     we1.setWhereItem(wiV2);
                     we1.setOperator(opV2);
                     whereItemList.addElement(we1);
                  } else {
                     whereItemList.addElement(wiNew);
                  }
               }

               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 16, we);
      }

      return we;
   }

   public WhereExpression toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.rownumClause != null && to_sqs != null) {
         this.addLimitClause(to_sqs, from_sqs);
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      this.setTargetDatabase(17);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (!(this.whereItems.elementAt(i) instanceof WhereItem)) {
            if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toSapHanaSelect(to_sqs, from_sqs);
               whereItemList.addElement(internalWE);
               if (internalWE.toString() == null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            }
         } else {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               int j;
               Vector whereItemsListReplacingEqualsClause;
               if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingInClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toSapHanaSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        we.getOperator().add("AND");
                     }
                  }
               } else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toSapHanaSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                           we.getOperator().add("AND");
                        } else {
                           we.getOperator().add("OR");
                        }
                     }
                  }
               } else {
                  WhereItem wiNew = wi.toSapHanaSelect(to_sqs, from_sqs);
                  if (wiNew.isNullSafeEqualsOperator() && from_sqs != null) {
                     WhereExpression we1 = new WhereExpression();
                     we1.setOpenBrace("(");
                     we1.setCloseBrace(")");
                     WhereItem wi2 = new WhereItem();
                     wi2.setLeftWhereExp(wiNew.getLeftWhereExp());
                     wi2.setOperator("IS NULL");
                     WhereItem wi3 = new WhereItem();
                     wi3.setLeftWhereExp(wiNew.getRightWhereExp());
                     wi3.setOperator("IS NULL");
                     WhereExpression we2 = new WhereExpression();
                     we2.setOpenBrace("(");
                     we2.setCloseBrace(")");
                     Vector wiV = new Vector();
                     wiV.add(wi2);
                     wiV.add(wi3);
                     Vector opV = new Vector();
                     opV.add("AND");
                     we2.setWhereItem(wiV);
                     we2.setOperator(opV);
                     Vector wiV2 = new Vector();
                     wiV2.add(wiNew);
                     wiV2.add(we2);
                     Vector opV2 = new Vector();
                     opV2.add("OR");
                     we1.setWhereItem(wiV2);
                     we1.setOperator(opV2);
                     whereItemList.addElement(we1);
                  } else {
                     whereItemList.addElement(wiNew);
                  }
               }

               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 17, we);
      }

      return we;
   }

   public WhereExpression toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.rownumClause != null && to_sqs != null) {
         this.addLimitClause(to_sqs, from_sqs);
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      this.setTargetDatabase(18);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (!(this.whereItems.elementAt(i) instanceof WhereItem)) {
            if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toSqliteSelect(to_sqs, from_sqs);
               whereItemList.addElement(internalWE);
               if (internalWE.toString() == null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            }
         } else {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               int j;
               Vector whereItemsListReplacingEqualsClause;
               if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingInClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toSqliteSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        we.getOperator().add("AND");
                     }
                  }
               } else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toSqliteSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                           we.getOperator().add("AND");
                        } else {
                           we.getOperator().add("OR");
                        }
                     }
                  }
               } else {
                  WhereItem wiNew = wi.toSqliteSelect(to_sqs, from_sqs);
                  if (wiNew.isNullSafeEqualsOperator() && from_sqs != null) {
                     WhereExpression we1 = new WhereExpression();
                     we1.setOpenBrace("(");
                     we1.setCloseBrace(")");
                     WhereItem wi2 = new WhereItem();
                     wi2.setLeftWhereExp(wiNew.getLeftWhereExp());
                     wi2.setOperator("IS NULL");
                     WhereItem wi3 = new WhereItem();
                     wi3.setLeftWhereExp(wiNew.getRightWhereExp());
                     wi3.setOperator("IS NULL");
                     WhereExpression we2 = new WhereExpression();
                     we2.setOpenBrace("(");
                     we2.setCloseBrace(")");
                     Vector wiV = new Vector();
                     wiV.add(wi2);
                     wiV.add(wi3);
                     Vector opV = new Vector();
                     opV.add("AND");
                     we2.setWhereItem(wiV);
                     we2.setOperator(opV);
                     Vector wiV2 = new Vector();
                     wiV2.add(wiNew);
                     wiV2.add(we2);
                     Vector opV2 = new Vector();
                     opV2.add("OR");
                     we1.setWhereItem(wiV2);
                     we1.setOperator(opV2);
                     whereItemList.addElement(we1);
                  } else {
                     whereItemList.addElement(wiNew);
                  }
               }

               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 18, we);
      }

      return we;
   }

   public WhereExpression toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.rownumClause != null && to_sqs != null) {
         this.addLimitClause(to_sqs, from_sqs);
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      this.setTargetDatabase(20);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (!(this.whereItems.elementAt(i) instanceof WhereItem)) {
            if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toExcelSelect(to_sqs, from_sqs);
               whereItemList.addElement(internalWE);
               if (internalWE.toString() == null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            }
         } else {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               int j;
               Vector whereItemsListReplacingEqualsClause;
               if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingInClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toExcelSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        we.getOperator().add("AND");
                     }
                  }
               } else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toExcelSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                           we.getOperator().add("AND");
                        } else {
                           we.getOperator().add("OR");
                        }
                     }
                  }
               } else {
                  WhereItem wiNew = wi.toExcelSelect(to_sqs, from_sqs);
                  if (wiNew.isNullSafeEqualsOperator() && from_sqs != null) {
                     WhereExpression we1 = new WhereExpression();
                     we1.setOpenBrace("(");
                     we1.setCloseBrace(")");
                     WhereItem wi2 = new WhereItem();
                     wi2.setLeftWhereExp(wiNew.getLeftWhereExp());
                     wi2.setOperator("IS NULL");
                     WhereItem wi3 = new WhereItem();
                     wi3.setLeftWhereExp(wiNew.getRightWhereExp());
                     wi3.setOperator("IS NULL");
                     WhereExpression we2 = new WhereExpression();
                     we2.setOpenBrace("(");
                     we2.setCloseBrace(")");
                     Vector wiV = new Vector();
                     wiV.add(wi2);
                     wiV.add(wi3);
                     Vector opV = new Vector();
                     opV.add("AND");
                     we2.setWhereItem(wiV);
                     we2.setOperator(opV);
                     Vector wiV2 = new Vector();
                     wiV2.add(wiNew);
                     wiV2.add(we2);
                     Vector opV2 = new Vector();
                     opV2.add("OR");
                     we1.setWhereItem(wiV2);
                     we1.setOperator(opV2);
                     whereItemList.addElement(we1);
                  } else {
                     whereItemList.addElement(wiNew);
                  }
               }

               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 20, we);
      }

      return we;
   }

   public WhereExpression toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.rownumClause != null && to_sqs != null) {
         this.addLimitClause(to_sqs, from_sqs);
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      this.setTargetDatabase(21);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (!(this.whereItems.elementAt(i) instanceof WhereItem)) {
            if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toMsAccessJdbcSelect(to_sqs, from_sqs);
               whereItemList.addElement(internalWE);
               if (internalWE.toString() == null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            }
         } else {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               int j;
               Vector whereItemsListReplacingEqualsClause;
               if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingInClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toMsAccessJdbcSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        we.getOperator().add("AND");
                     }
                  }
               } else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toMsAccessJdbcSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                           we.getOperator().add("AND");
                        } else {
                           we.getOperator().add("OR");
                        }
                     }
                  }
               } else {
                  WhereItem wiNew = wi.toMsAccessJdbcSelect(to_sqs, from_sqs);
                  if (wiNew.isNullSafeEqualsOperator() && from_sqs != null) {
                     WhereExpression we1 = new WhereExpression();
                     we1.setOpenBrace("(");
                     we1.setCloseBrace(")");
                     WhereItem wi2 = new WhereItem();
                     wi2.setLeftWhereExp(wiNew.getLeftWhereExp());
                     wi2.setOperator("IS NULL");
                     WhereItem wi3 = new WhereItem();
                     wi3.setLeftWhereExp(wiNew.getRightWhereExp());
                     wi3.setOperator("IS NULL");
                     WhereExpression we2 = new WhereExpression();
                     we2.setOpenBrace("(");
                     we2.setCloseBrace(")");
                     Vector wiV = new Vector();
                     wiV.add(wi2);
                     wiV.add(wi3);
                     Vector opV = new Vector();
                     opV.add("AND");
                     we2.setWhereItem(wiV);
                     we2.setOperator(opV);
                     Vector wiV2 = new Vector();
                     wiV2.add(wiNew);
                     wiV2.add(we2);
                     Vector opV2 = new Vector();
                     opV2.add("OR");
                     we1.setWhereItem(wiV2);
                     we1.setOperator(opV2);
                     whereItemList.addElement(we1);
                  } else {
                     whereItemList.addElement(wiNew);
                  }
               }

               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 21, we);
      }

      return we;
   }

   public WhereExpression toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      if (this.rownumClause != null && to_sqs != null) {
         this.addLimitClause(to_sqs, from_sqs);
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      this.setTargetDatabase(4);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (!(this.whereItems.elementAt(i) instanceof WhereItem)) {
            if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs);
               whereItemList.addElement(internalWE);
               if (internalWE.toString() == null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            }
         } else {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               int j;
               Vector whereItemsListReplacingEqualsClause;
               if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingInClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toPostgreSQLSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        we.getOperator().add("AND");
                     }
                  }
               } else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toPostgreSQLSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                           we.getOperator().add("AND");
                        } else {
                           we.getOperator().add("OR");
                        }
                     }
                  }
               } else {
                  WhereItem wiNew = wi.toPostgreSQLSelect(to_sqs, from_sqs);
                  if (wiNew.isNullSafeEqualsOperator() && from_sqs != null && isPostgreLiveDbs) {
                     WhereExpression we1 = new WhereExpression();
                     we1.setOpenBrace("(");
                     we1.setCloseBrace(")");
                     WhereItem wi2 = new WhereItem();
                     wi2.setLeftWhereExp(wiNew.getLeftWhereExp());
                     wi2.setOperator("IS NULL");
                     WhereItem wi3 = new WhereItem();
                     wi3.setLeftWhereExp(wiNew.getRightWhereExp());
                     wi3.setOperator("IS NULL");
                     WhereExpression we2 = new WhereExpression();
                     we2.setOpenBrace("(");
                     we2.setCloseBrace(")");
                     Vector wiV = new Vector();
                     wiV.add(wi2);
                     wiV.add(wi3);
                     Vector opV = new Vector();
                     opV.add("AND");
                     we2.setWhereItem(wiV);
                     we2.setOperator(opV);
                     Vector wiV2 = new Vector();
                     wiV2.add(wiNew);
                     wiV2.add(we2);
                     Vector opV2 = new Vector();
                     opV2.add("OR");
                     we1.setWhereItem(wiV2);
                     we1.setOperator(opV2);
                     whereItemList.addElement(we1);
                  } else {
                     whereItemList.addElement(wiNew);
                  }
               }

               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 4, we);
      }

      return we;
   }

   public WhereExpression toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.setTargetDatabase(2);
      if (this.rownumClause != null && to_sqs != null) {
         String rownumValue = "0";
         if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
            throw new ConvertException();
         }

         if (this.rownumClause.getRownumValue() instanceof SelectQueryStatement) {
            throw new ConvertException("Conversion failure.. Subquery can't be converted");
         }

         if (this.rownumClause.getRownumValue() instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.rownumClause.getRownumValue();
            Vector colExp = sc.getColumnExpression();

            for(int i = 0; i < colExp.size(); ++i) {
               if (colExp.elementAt(i) instanceof FunctionCalls) {
                  throw new ConvertException("Conversion failure.. Function calls can't be converted");
               }

               if (colExp.elementAt(i) instanceof TableColumn) {
                  if (!this.rownumClause.getOperator().equals("<=") && !this.rownumClause.getOperator().equals("=")) {
                     to_sqs.getSelectStatement().setSelectRowCountVariable(((TableColumn)colExp.elementAt(i)).toString() + " - 1");
                  } else {
                     to_sqs.getSelectStatement().setSelectRowCountVariable(((TableColumn)colExp.elementAt(i)).toString());
                  }
               } else {
                  if (!(colExp.elementAt(i) instanceof String) || colExp.size() != 1) {
                     throw new ConvertException("Conversion failure.. Expression can't be converted");
                  }

                  rownumValue = (String)colExp.elementAt(i);
               }
            }
         }

         to_sqs.getSelectStatement().setSelectRowSpecifier("TOP");
         if (!this.rownumClause.getOperator().equals("<=") && !this.rownumClause.getOperator().equals("=")) {
            to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue) - 1);
         } else {
            to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue));
         }
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      we.setConcatenation(this.concatenation);
      we.setCommentClass(this.commentObj);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (!(this.whereItems.elementAt(i) instanceof WhereItem)) {
            if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs);
               whereItemList.addElement(internalWE);
               if (internalWE.toString() == null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            }
         } else {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               int j;
               Vector whereItemsListReplacingEqualsClause;
               if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingInClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toMSSQLServerSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        we.getOperator().add("AND");
                     }
                  }
               } else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toMSSQLServerSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                           we.getOperator().add("AND");
                        } else {
                           we.getOperator().add("OR");
                        }
                     }
                  }
               } else {
                  WhereColumn l_wc_check = wi.getLeftWhereExp();
                  int j;
                  if (wi.getOperator() != null && wi.getOperator().equalsIgnoreCase("IN") && wi.getRightWhereSubQuery() != null && l_wc_check != null && l_wc_check.getColumnExpression() != null && l_wc_check.getColumnExpression().size() > 1 && l_wc_check.getColumnExpression().contains(",")) {
                     if (SwisSQLOptions.splitMulitpleColumnsOfINClause) {
                        Vector left_col_list_v = new Vector();
                        WhereColumn l_wc = wi.getLeftWhereExp();
                        if (l_wc.getColumnExpression() != null && l_wc.getColumnExpression().size() != 1 && l_wc.getColumnExpression().contains(",")) {
                           Vector col_exp_v = l_wc.getColumnExpression();

                           for(int k = 0; k < col_exp_v.size(); ++k) {
                              if (!col_exp_v.elementAt(k).toString().equalsIgnoreCase(",")) {
                                 left_col_list_v.addElement(col_exp_v.elementAt(k));
                              }
                           }
                        }

                        SelectQueryStatement current_rw_subquery = wi.getRightWhereSubQuery();
                        Vector rw_subquery_selectlist = current_rw_subquery.getSelectStatement().getSelectItemList();

                        for(j = 0; j < left_col_list_v.size(); ++j) {
                           WhereItem wi1 = new WhereItem();
                           wi1.setLeftWhereExp((WhereColumn)left_col_list_v.elementAt(j));
                           wi1.setOperator("IN");
                           SelectQueryStatement sqs1 = new SelectQueryStatement();
                           SelectStatement ss1 = new SelectStatement();
                           Object only_select_item_list = rw_subquery_selectlist.elementAt(j);
                           if (only_select_item_list instanceof SelectColumn) {
                              ((SelectColumn)only_select_item_list).setEndsWith((String)null);
                           }

                           Vector v = new Vector();
                           v.addElement(only_select_item_list);
                           ss1.setSelectClause(current_rw_subquery.getSelectStatement().getSelectClause());
                           ss1.setSelectItemList(v);
                           sqs1.setSelectStatement(ss1);
                           sqs1.setFromClause(current_rw_subquery.getFromClause());
                           WhereExpression we1 = null;
                           if (current_rw_subquery.getWhereExpression() != null) {
                              we1 = current_rw_subquery.getWhereExpression().toMSSQLServerSelect(current_rw_subquery, sqs1);
                           }

                           sqs1.setWhereExpression(we1);
                           if (only_select_item_list instanceof SelectColumn && SwisSQLUtils.isAggregateFunction((SelectColumn)only_select_item_list)) {
                              sqs1.setGroupByStatement(current_rw_subquery.getGroupByStatement());
                           }

                           wi1.setRightWhereSubQuery(sqs1);
                           whereItemList.addElement(wi1.toMSSQLServerSelect(to_sqs, from_sqs));
                           if (j < left_col_list_v.size() - 1) {
                              we.addOperator("AND");
                           }
                        }
                     } else {
                        whereItemList.addElement(wi.toMSSQLServerSelect(to_sqs, from_sqs));
                     }
                  } else {
                     WhereItem mssqlWi;
                     if (wi.getOperator() != null && (wi.getOperator().equalsIgnoreCase("IN") || wi.getOperator().equalsIgnoreCase("NOT IN")) && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
                        mssqlWi = wi.toMSSQLServerSelect(to_sqs, from_sqs);
                        Vector v = mssqlWi.getRightWhereExp().getColumnExpression();
                        if (v != null) {
                           int count = v.size();
                           WhereExpression inWhereExp = null;

                           for(j = 0; j < count; ++j) {
                              Object val = v.get(j);
                              if (val instanceof WhereColumn) {
                                 String ss = ((WhereColumn)val).toString().trim();
                                 if (ss.equalsIgnoreCase("null")) {
                                    String op = mssqlWi.getOperator();
                                    inWhereExp = new WhereExpression();
                                    inWhereExp.addWhereItem(mssqlWi);
                                    inWhereExp.setOpenBrace("(");
                                    inWhereExp.setCloseBrace(")");
                                    WhereItem wet = new WhereItem();
                                    String newOp = op.equalsIgnoreCase("IN") ? "IS" : "IS NOT";
                                    wet.setOperator(newOp);
                                    wet.setLeftWhereExp(mssqlWi.getLeftWhereExp());
                                    Vector vv = new Vector();
                                    vv.add("NULL");
                                    WhereColumn wcc = new WhereColumn();
                                    wcc.setColumnExpression(vv);
                                    wet.setRightWhereExp(wcc);
                                    String andOrOp = op.equalsIgnoreCase("IN") ? "OR" : "AND";
                                    inWhereExp.addOperator(andOrOp);
                                    inWhereExp.addWhereItem(wet);
                                    if (j + 1 < count) {
                                       Object kk = v.get(j + 1);
                                       if (kk != null && kk.toString().trim().equals(",")) {
                                          v.remove(j + 1);
                                          v.remove(j);
                                          break;
                                       }
                                    }

                                    v.remove(j);
                                    if (j == v.size() - 1 && v.size() - 2 >= 0 && v.get(v.size() - 2).toString().trim().equals(",")) {
                                       v.remove(v.size() - 2);
                                    }
                                    break;
                                 }
                              }
                           }

                           j = v.size();
                           if (count == j) {
                              whereItemList.addElement(mssqlWi);
                           } else {
                              whereItemList.addElement(inWhereExp);
                           }
                        } else {
                           whereItemList.addElement(wi.toMSSQLServerSelect(to_sqs, from_sqs));
                        }
                     } else {
                        mssqlWi = wi.toMSSQLServerSelect(to_sqs, from_sqs);
                        if (mssqlWi.isNullSafeEqualsOperator()) {
                           WhereExpression we1 = new WhereExpression();
                           we1.setOpenBrace("(");
                           we1.setCloseBrace(")");
                           WhereItem wi2 = new WhereItem();
                           wi2.setLeftWhereExp(mssqlWi.getLeftWhereExp());
                           wi2.setOperator("IS NULL");
                           WhereItem wi3 = new WhereItem();
                           wi3.setLeftWhereExp(mssqlWi.getRightWhereExp());
                           wi3.setOperator("IS NULL");
                           WhereExpression we2 = new WhereExpression();
                           we2.setOpenBrace("(");
                           we2.setCloseBrace(")");
                           Vector wiV = new Vector();
                           wiV.add(wi2);
                           wiV.add(wi3);
                           Vector opV = new Vector();
                           opV.add("AND");
                           we2.setWhereItem(wiV);
                           we2.setOperator(opV);
                           Vector wiV2 = new Vector();
                           wiV2.add(mssqlWi);
                           wiV2.add(we2);
                           Vector opV2 = new Vector();
                           opV2.add("OR");
                           we1.setWhereItem(wiV2);
                           we1.setOperator(opV2);
                           whereItemList.addElement(we1);
                        } else {
                           whereItemList.addElement(mssqlWi);
                        }
                     }
                  }
               }

               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > 0) {
                     we.getOperator().setElementAt("&AND", 0);
                  }
               }
            } else if (!SwisSQLAPI.MSSQLSERVER_THETA) {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            } else {
               whereItemList.addElement(((WhereItem)this.whereItems.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (!SwisSQLAPI.MSSQLSERVER_THETA && this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 2, we);
      }

      return we;
   }

   public WhereExpression toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.rownumClause != null && to_sqs != null) {
         this.addLimitClause(to_sqs, from_sqs);
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      this.setTargetDatabase(15);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (!(this.whereItems.elementAt(i) instanceof WhereItem)) {
            if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toSnowflakeSelect(to_sqs, from_sqs);
               whereItemList.addElement(internalWE);
               if (internalWE.toString() == null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            }
         } else {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               int j;
               Vector whereItemsListReplacingEqualsClause;
               if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingInClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toSnowflakeSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        we.getOperator().add("AND");
                     }
                  }
               } else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toSnowflakeSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                           we.getOperator().add("AND");
                        } else {
                           we.getOperator().add("OR");
                        }
                     }
                  }
               } else {
                  WhereItem wiNew = wi.toSnowflakeSelect(to_sqs, from_sqs);
                  if (wiNew.isNullSafeEqualsOperator() && from_sqs != null) {
                     WhereExpression we1 = new WhereExpression();
                     we1.setOpenBrace("(");
                     we1.setCloseBrace(")");
                     WhereItem wi2 = new WhereItem();
                     wi2.setLeftWhereExp(wiNew.getLeftWhereExp());
                     wi2.setOperator("IS NULL");
                     WhereItem wi3 = new WhereItem();
                     wi3.setLeftWhereExp(wiNew.getRightWhereExp());
                     wi3.setOperator("IS NULL");
                     WhereExpression we2 = new WhereExpression();
                     we2.setOpenBrace("(");
                     we2.setCloseBrace(")");
                     Vector wiV = new Vector();
                     wiV.add(wi2);
                     wiV.add(wi3);
                     Vector opV = new Vector();
                     opV.add("AND");
                     we2.setWhereItem(wiV);
                     we2.setOperator(opV);
                     Vector wiV2 = new Vector();
                     wiV2.add(wiNew);
                     wiV2.add(we2);
                     Vector opV2 = new Vector();
                     opV2.add("OR");
                     we1.setWhereItem(wiV2);
                     we1.setOperator(opV2);
                     whereItemList.addElement(we1);
                  } else {
                     whereItemList.addElement(wiNew);
                  }
               }

               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 15, we);
      }

      return we;
   }

   public WhereExpression toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.setTargetDatabase(7);
      if (this.rownumClause != null && to_sqs != null) {
         String rownumValue = "0";
         if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
            throw new ConvertException();
         }

         if (this.rownumClause.getRownumValue() instanceof SelectQueryStatement) {
            throw new ConvertException("Conversion failure.. Subquery can't be converted");
         }

         if (this.rownumClause.getRownumValue() instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.rownumClause.getRownumValue();
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

         if (!this.rownumClause.getOperator().equals("<=") && !this.rownumClause.getOperator().equals("=")) {
            to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue) - 1);
         } else {
            to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue));
         }
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      we.setObjectContext(this.context);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (!(this.whereItems.elementAt(i) instanceof WhereItem)) {
            if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               ((WhereExpression)this.whereItems.elementAt(i)).setObjectContext(this.context);
               WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toSybaseSelect(to_sqs, from_sqs);
               whereItemList.addElement(internalWE);
               if (internalWE.toString() == null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            }
         } else {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            wi.setObjectContext(this.context);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               int j;
               Vector whereItemsListReplacingEqualsClause;
               if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingInClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     ((WhereItem)whereItemsListReplacingEqualsClause.get(j)).setObjectContext(this.context);
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toSybaseSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        we.getOperator().add("AND");
                     }
                  }
               } else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                  whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();

                  for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     ((WhereItem)whereItemsListReplacingEqualsClause.get(j)).setObjectContext(this.context);
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toSybaseSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                           we.getOperator().add("AND");
                        } else {
                           we.getOperator().add("OR");
                        }
                     }
                  }
               } else {
                  whereItemList.addElement(wi.toSybaseSelect(to_sqs, from_sqs));
               }

               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > 0) {
                     we.getOperator().setElementAt("&AND", 0);
                  }
               }
            } else if (!SwisSQLAPI.MSSQLSERVER_THETA) {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            } else {
               whereItemList.addElement(((WhereItem)this.whereItems.elementAt(i)).toSybaseSelect(to_sqs, from_sqs));
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (!SwisSQLAPI.MSSQLSERVER_THETA && this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 7, we);
      }

      return we;
   }

   public WhereExpression toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereExpression we = new WhereExpression();
      this.setTargetDatabase(1);
      Vector whereItemList;
      int i;
      String op;
      if (SwisSQLAPI.convert_OracleThetaJOIN_To_ANSIJOIN) {
         we.setOpenBrace(this.openBraces);
         we.setCloseBrace(this.closeBraces);
         if (this.beginOperator != null) {
            we.setBeginOperator(this.beginOperator);
         }

         if (this.operators != null) {
            whereItemList = new Vector();

            for(i = 0; i < this.operators.size(); ++i) {
               op = (String)this.operators.elementAt(i);
               whereItemList.addElement(op);
            }

            we.setOperator(whereItemList);
         }

         whereItemList = new Vector();

         for(i = 0; i < this.whereItems.size(); ++i) {
            if (this.whereItems.elementAt(i) == null) {
               whereItemList.addElement((Object)null);
            }

            String op;
            if (this.whereItems.elementAt(i) instanceof WhereItem) {
               WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
               if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
                  whereItemList.addElement(wi.toOracleSelect(to_sqs, from_sqs));
                  if (wi.getRownumClause() != null) {
                     if (i != 0) {
                        op = (String)we.getOperator().get(i - 1);
                        if (!op.equals("&AND")) {
                           we.getOperator().setElementAt("&AND", i - 1);
                        } else if (we.getOperator().size() > i) {
                           we.getOperator().setElementAt("&AND", i);
                        }
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  }
               } else {
                  whereItemList.addElement((Object)null);
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toOracleSelect(to_sqs, from_sqs);
               whereItemList.addElement(internalWE);
               if (internalWE.toString() == null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            }
         }

         we.setWhereItem(whereItemList);
         we.setCheckWhere(this.checkWhere);
         we.setRownumClause(this.rownumClause);
         if (this.topLevel) {
            this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 1, we);
         }
      } else {
         we.setOpenBrace(this.openBraces);
         we.setCloseBrace(this.closeBraces);
         we.setCommentClass(this.commentObj);
         if (this.beginOperator != null) {
            we.setBeginOperator(this.beginOperator);
         }

         if (this.operators != null) {
            whereItemList = new Vector();
            i = 0;

            while(true) {
               if (i >= this.operators.size()) {
                  we.setOperator(whereItemList);
                  break;
               }

               op = (String)this.operators.elementAt(i);
               if (op != null && op.equalsIgnoreCase("ANSIAND")) {
                  whereItemList.addElement("AND");
               } else if (op != null && op.equalsIgnoreCase("ANSIOR")) {
                  whereItemList.addElement("OR");
               } else {
                  whereItemList.addElement(op);
               }

               ++i;
            }
         }

         whereItemList = new Vector();

         for(i = 0; i < this.whereItems.size(); ++i) {
            op = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
            if (this.whereItems.elementAt(i) == null) {
               whereItemList.addElement((Object)null);
            } else if (this.whereItems.elementAt(i) instanceof WhereItem) {
               WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
               WhereItem wiNew = wi.toOracleSelect(to_sqs, from_sqs);
               if (wiNew != null) {
                  if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
                     if (wiNew.isNullSafeEqualsOperator()) {
                        WhereExpression we1 = new WhereExpression();
                        we1.setOpenBrace("(");
                        we1.setCloseBrace(")");
                        WhereItem wi2 = new WhereItem();
                        wi2.setLeftWhereExp(wiNew.getLeftWhereExp());
                        wi2.setOperator("IS NULL");
                        WhereItem wi3 = new WhereItem();
                        wi3.setLeftWhereExp(wiNew.getRightWhereExp());
                        wi3.setOperator("IS NULL");
                        WhereExpression we2 = new WhereExpression();
                        we2.setOpenBrace("(");
                        we2.setCloseBrace(")");
                        Vector wiV = new Vector();
                        wiV.add(wi2);
                        wiV.add(wi3);
                        Vector opV = new Vector();
                        opV.add("AND");
                        we2.setWhereItem(wiV);
                        we2.setOperator(opV);
                        Vector wiV2 = new Vector();
                        wiV2.add(wiNew);
                        wiV2.add(we2);
                        Vector opV2 = new Vector();
                        opV2.add("OR");
                        we1.setWhereItem(wiV2);
                        we1.setOperator(opV2);
                        whereItemList.addElement(we1);
                     } else {
                        whereItemList.addElement(wiNew);
                     }
                  } else {
                     whereItemList.addElement(wiNew);
                  }

                  String operator = wi.getOperator();
                  if (operator != null && (operator.equalsIgnoreCase("like") || operator.equalsIgnoreCase("not like"))) {
                     this.handleRegularExpression(whereItemList, i);
                  }
               }
            } else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               whereItemList.addElement(((WhereExpression)this.whereItems.elementAt(i)).toOracleSelect(to_sqs, from_sqs));
            }

            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = op;
         }

         we.setWhereItem(whereItemList);
         we.setCheckWhere(this.checkWhere);
         we.setRownumClause(this.rownumClause);
      }

      return we;
   }

   public WhereExpression toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.setTargetDatabase(6);
      if (this.rownumClause != null && to_sqs != null) {
         String rownumValue = "0";
         if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
            throw new ConvertException();
         }

         if (this.rownumClause.getRownumValue() instanceof SelectQueryStatement) {
            throw new ConvertException("Conversion failure.. Subquery can't be converted");
         }

         if (this.rownumClause.getRownumValue() instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.rownumClause.getRownumValue();
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

         to_sqs.getSelectStatement().setInformixRowSpecifier("FIRST");
         if (this.rownumClause.getOperator().equals("<=")) {
            to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue));
         } else {
            to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue) - 1);
         }
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            if (op != null && op.equalsIgnoreCase("ANSIAND")) {
               whereItemList.addElement("AND");
            } else if (op != null && op.equalsIgnoreCase("ANSIOR")) {
               whereItemList.addElement("OR");
            } else {
               whereItemList.addElement(op);
            }
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (!(this.whereItems.elementAt(i) instanceof WhereItem)) {
            if (this.whereItems.elementAt(i) instanceof WhereExpression) {
               WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toInformixSelect(to_sqs, from_sqs);
               whereItemList.addElement(internalWE);
               if (internalWE.toString() == null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            }
         } else {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            Vector whereItemsListReplacingEqualsClause;
            int j;
            if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
               whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingInClause();

               for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                  whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toInformixSelect(to_sqs, from_sqs));
                  if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                     we.getOperator().add("AND");
                  }
               }
            } else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
               whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();

               for(j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                  whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toInformixSelect(to_sqs, from_sqs));
                  if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                     if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                        we.getOperator().add("AND");
                     } else {
                        we.getOperator().add("OR");
                     }
                  }
               }
            } else {
               WhereItem wiNew = wi.toInformixSelect(to_sqs, from_sqs);
               if (wiNew.isNullSafeEqualsOperator() && from_sqs != null) {
                  WhereExpression we1 = new WhereExpression();
                  we1.setOpenBrace("(");
                  we1.setCloseBrace(")");
                  WhereItem wi2 = new WhereItem();
                  wi2.setLeftWhereExp(wiNew.getLeftWhereExp());
                  wi2.setOperator("IS NULL");
                  WhereItem wi3 = new WhereItem();
                  wi3.setLeftWhereExp(wiNew.getRightWhereExp());
                  wi3.setOperator("IS NULL");
                  WhereExpression we2 = new WhereExpression();
                  we2.setOpenBrace("(");
                  we2.setCloseBrace(")");
                  Vector wiV = new Vector();
                  wiV.add(wi2);
                  wiV.add(wi3);
                  Vector opV = new Vector();
                  opV.add("AND");
                  we2.setWhereItem(wiV);
                  we2.setOperator(opV);
                  Vector wiV2 = new Vector();
                  wiV2.add(wiNew);
                  wiV2.add(we2);
                  Vector opV2 = new Vector();
                  opV2.add("OR");
                  we1.setWhereItem(wiV2);
                  we1.setOperator(opV2);
                  whereItemList.addElement(we1);
               } else {
                  whereItemList.addElement(wiNew);
               }
            }

            if (wi.getRownumClause() != null) {
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > 0) {
                  we.getOperator().setElementAt("&AND", 0);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.topLevel) {
         this.setNewFromClauseWithInformixJoin(to_sqs, from_sqs, 6);
      }

      return we;
   }

   public WhereExpression toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (this.whereItems.elementAt(i) instanceof WhereItem) {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               whereItemList.addElement(wi.toANSISelect(to_sqs, from_sqs));
               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         } else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
            WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toANSISelect(to_sqs, from_sqs);
            whereItemList.addElement(internalWE);
            if (internalWE.toString() == null) {
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 8, we);
      }

      return we;
   }

   public WhereExpression toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      this.setTargetDatabase(12);
      this.thetaJoinPresent = false;
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (this.whereItems.elementAt(i) instanceof WhereItem) {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            wi.setCaseExpressionBool(this.isCaseExpression());
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               WhereItem teradataWi = wi.toTeradataSelect(to_sqs, from_sqs);
               whereItemList.addElement(teradataWi);
               if (teradataWi != null && teradataWi.getTeradataSysCalendarWhereItem() != null) {
                  whereItemList.addElement(teradataWi.getTeradataSysCalendarWhereItem());
                  we.getOperator().addElement("AND");
               }

               if (wi.getRownumClause() != null && !this.isCaseExpression()) {
                  if (i != 0) {
                     String op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else {
               if (!this.thetaJoinPresent) {
                  this.thetaJoinPresent = true;
               }

               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         } else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
            ((WhereExpression)this.whereItems.elementAt(i)).setCaseExpressionBool(this.isCaseExpression());
            WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toTeradataSelect(to_sqs, from_sqs);
            if (!this.thetaJoinPresent) {
               this.thetaJoinPresent = internalWE.isThetaJoinPresent();
            }

            whereItemList.addElement(internalWE);
            if (internalWE.toString() == null) {
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.thetaJoinPresent) {
         we.setThetaJoinPresent(true);
      }

      if (this.topLevel && this.thetaJoinPresent) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 12, we);
      }

      return we;
   }

   public WhereExpression toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.setTargetDatabase(10);
      if (this.rownumClause != null && to_sqs != null) {
         String rownumValue = "0";
         if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
            throw new ConvertException();
         }

         if (this.rownumClause.getRownumValue() instanceof SelectQueryStatement) {
            throw new ConvertException("Conversion failure.. Subquery can't be converted");
         }

         if (this.rownumClause.getRownumValue() instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.rownumClause.getRownumValue();
            Vector colExp = sc.getColumnExpression();

            for(int i = 0; i < colExp.size(); ++i) {
               if (colExp.elementAt(i) instanceof FunctionCalls) {
                  throw new ConvertException("Conversion failure.. Function calls can't be converted");
               }

               if (colExp.elementAt(i) instanceof TableColumn) {
                  if (!this.rownumClause.getOperator().equals("<=") && !this.rownumClause.getOperator().equals("=")) {
                     to_sqs.getSelectStatement().setSelectRowCountVariable(((TableColumn)colExp.elementAt(i)).toString() + " - 1");
                  } else {
                     to_sqs.getSelectStatement().setSelectRowCountVariable(((TableColumn)colExp.elementAt(i)).toString());
                  }
               } else {
                  if (!(colExp.elementAt(i) instanceof String) || colExp.size() != 1) {
                     throw new ConvertException("Conversion failure.. Expression can't be converted");
                  }

                  rownumValue = (String)colExp.elementAt(i);
               }
            }
         }

         to_sqs.getSelectStatement().setSelectRowSpecifier("FIRST");
         if (!this.rownumClause.getOperator().equals("<=") && !this.rownumClause.getOperator().equals("=")) {
            to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue) - 1);
         } else {
            to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue));
         }
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      String op;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            op = (String)this.operators.elementAt(i);
            if (op != null && op.equalsIgnoreCase("ANSIAND")) {
               whereItemList.addElement("AND");
            } else if (op != null && op.equalsIgnoreCase("ANSIOR")) {
               whereItemList.addElement("OR");
            } else {
               whereItemList.addElement(op);
            }
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         op = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
         SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         } else if (this.whereItems.elementAt(i) instanceof WhereItem) {
            whereItemList.addElement(((WhereItem)this.whereItems.elementAt(i)).toTimesTenSelect(to_sqs, from_sqs));
         } else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
            whereItemList.addElement(((WhereExpression)this.whereItems.elementAt(i)).toTimesTenSelect(to_sqs, from_sqs));
         }

         SelectQueryStatement.singleQueryConvertedToMultipleQueryList = op;
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      return we;
   }

   public WhereExpression toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      this.setTargetDatabase(11);
      if (this.rownumClause != null && to_sqs != null) {
         this.addLimitClause(to_sqs, from_sqs);
      }

      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         if (this.whereItems.elementAt(i) == null) {
            whereItemList.addElement((Object)null);
         }

         String op;
         if (this.whereItems.elementAt(i) instanceof WhereItem) {
            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               whereItemList.addElement(wi.toNetezzaSelect(to_sqs, from_sqs));
               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         } else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
            WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toNetezzaSelect(to_sqs, from_sqs);
            whereItemList.addElement(internalWE);
            if (internalWE.toString() == null) {
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 11, we);
      }

      return we;
   }

   private void handleRegularExpression(Vector whereItemsList, int i) {
      WhereItem wi = (WhereItem)whereItemsList.get(i);
      WhereColumn rwc = wi.getRightWhereExp();
      WhereColumn lwc = wi.getLeftWhereExp();
      String operator = wi.getOperator();
      boolean isNotLike = false;
      if (operator.equalsIgnoreCase("not like")) {
         isNotLike = true;
      }

      if (rwc != null) {
         Vector colExp = rwc.getColumnExpression();

         for(int j = 0; j < colExp.size(); ++j) {
            Object obj = colExp.get(j);
            if (obj instanceof String && ((String)obj).startsWith("'")) {
               String literal = (String)obj;
               int index1 = literal.indexOf("[");
               int index2 = literal.indexOf("]");
               if (index1 != -1 && index2 != -1 && index2 != index1 + 1) {
                  String subRegExp = literal.substring(index1 + 1, index2);
                  String replaceStr = "[" + subRegExp + "]";
                  if (subRegExp.startsWith("(") && subRegExp.endsWith(")") && subRegExp.length() > 2) {
                     subRegExp = subRegExp.substring(1, subRegExp.length() - 1);
                  }

                  WhereExpression we = new WhereExpression();
                  we.setOpenBrace("(");
                  we.setCloseBrace(")");
                  Vector newColExp;
                  char endInt;
                  String newLiteral;
                  WhereItem wi2;
                  WhereItem newWI;
                  WhereColumn newRWC;
                  WhereItem newWI;
                  Vector newRWCExp;
                  WhereColumn newLWC;
                  Vector newLWCExp;
                  Vector fnArgs;
                  if (subRegExp.startsWith("^")) {
                     subRegExp = subRegExp.substring(1);
                     if (subRegExp.startsWith("(") && subRegExp.endsWith(")") && subRegExp.length() > 2) {
                        subRegExp = subRegExp.substring(1, subRegExp.length() - 1);
                     }

                     ArrayList rightSideItems = new ArrayList();
                     if (subRegExp.length() == 3 && subRegExp.indexOf("-") == 1) {
                        endInt = subRegExp.charAt(0);
                        int endInt = subRegExp.charAt(2);

                        for(int s = endInt; s <= endInt; ++s) {
                           rightSideItems.add("'" + (char)s + "'");
                        }
                     } else {
                        for(int k = 0; k < subRegExp.length(); ++k) {
                           rightSideItems.add("'" + subRegExp.charAt(k) + "'");
                        }
                     }

                     String replaceFnLiteral;
                     if (literal.startsWith("'%")) {
                        newLiteral = StringFunctions.replaceFirst("_", replaceStr, literal);
                        colExp.setElementAt(newLiteral, j);
                        replaceFnLiteral = "'" + literal.substring(2);
                        if (replaceFnLiteral.endsWith("%'")) {
                           replaceFnLiteral = replaceFnLiteral.substring(0, replaceFnLiteral.length() - 2) + "'";
                        }

                        WhereExpression we1 = new WhereExpression();
                        we1.setOpenBrace("(");
                        we1.setCloseBrace(")");

                        for(int k = 0; k < rightSideItems.size(); ++k) {
                           String character = rightSideItems.get(k).toString();
                           character = character.substring(1, 2);
                           newWI = new WhereItem();
                           newLWC = new WhereColumn();
                           newLWCExp = new Vector();
                           newLWC.setColumnExpression(newLWCExp);
                           FunctionCalls fc = new FunctionCalls();
                           TableColumn tcfn = new TableColumn();
                           tcfn.setColumnName("REPLACE");
                           fc.setFunctionName(tcfn);
                           fnArgs = new Vector();
                           fc.setFunctionArguments(fnArgs);
                           SelectColumn sc = new SelectColumn();
                           sc.setColumnExpression(lwc.getColumnExpression());
                           fnArgs.add(sc);
                           String secondArg = StringFunctions.replaceFirst(character, replaceStr, replaceFnLiteral);
                           fnArgs.add(secondArg);
                           fnArgs.add("'x'");
                           newLWCExp.add(fc);
                           newWI.setLeftWhereExp(newLWC);
                           newWI.setOperator("LIKE");
                           newWI.setRightWhereExp(rwc);
                           if (isNotLike) {
                              we1.addWhereItem(newWI);
                           } else {
                              we.addWhereItem(newWI);
                           }

                           if (k + 1 < rightSideItems.size()) {
                              if (isNotLike) {
                                 we1.addOperator("AND");
                              } else {
                                 we.addOperator("AND");
                              }
                           }
                        }

                        if (isNotLike) {
                           we.setOpenBrace((String)null);
                           we.setCloseBrace((String)null);
                           newWI = new WhereItem();
                           newWI.setLeftWhereExp((WhereColumn)null);
                           newWI.setOperator("NOT");
                           newRWC = new WhereColumn();
                           newRWCExp = new Vector();
                           newRWCExp.add(we1);
                           newRWC.setColumnExpression(newRWCExp);
                           newWI.setRightWhereExp(newRWC);
                           we.addWhereItem(newWI);
                        }
                     } else {
                        WhereItem wi1 = new WhereItem();
                        wi1.setLeftWhereExp(lwc);
                        wi1.setOperator("LIKE");
                        replaceFnLiteral = StringFunctions.replaceFirst("_", replaceStr, literal);
                        colExp.setElementAt(replaceFnLiteral, j);
                        wi1.setRightWhereExp(rwc);
                        wi2 = new WhereItem();
                        WhereColumn lwc2 = new WhereColumn();
                        newColExp = new Vector();
                        FunctionCalls fc = new FunctionCalls();
                        Vector fnArgs = new Vector();
                        fnArgs.add(lwc);
                        fnArgs.add(index1 + "");
                        fnArgs.add("1");
                        TableColumn tcfn = new TableColumn();
                        tcfn.setColumnName("SUBSTR");
                        fc.setFunctionName(tcfn);
                        fc.setFunctionArguments(fnArgs);
                        newColExp.add(fc);
                        lwc2.setColumnExpression(newColExp);
                        wi2.setLeftWhereExp(lwc2);
                        if (isNotLike) {
                           wi2.setOperator("IN");
                        } else {
                           wi2.setOperator("NOT IN");
                        }

                        WhereColumn rwc2 = new WhereColumn();
                        Vector rwcExp = new Vector();
                        rwcExp.add("(");

                        for(int l = 0; l < rightSideItems.size(); ++l) {
                           rwcExp.add(rightSideItems.get(l));
                           if (l + 1 < rightSideItems.size()) {
                              rwcExp.add(",");
                           }
                        }

                        rwcExp.add(")");
                        rwc2.setColumnExpression(rwcExp);
                        wi2.setRightWhereExp(rwc2);
                        if (isNotLike) {
                           WhereExpression we1 = new WhereExpression();
                           we1.setOpenBrace("(");
                           we1.setCloseBrace(")");
                           we1.addWhereItem(wi1);
                           we1.addOperator("AND");
                           we1.addWhereItem(wi2);
                           we.addWhereExpression(we1);
                           we.addOperator("OR");
                           WhereItem wi3 = new WhereItem();
                           wi3.setLeftWhereExp(lwc);
                           wi3.setOperator("NOT LIKE");
                           wi3.setRightWhereExp(rwc);
                           we.addWhereItem(wi3);
                        } else {
                           we.addWhereItem(wi1);
                           we.addOperator("AND");
                           we.addWhereItem(wi2);
                        }
                     }

                     whereItemsList.setElementAt(we, i);
                  } else {
                     String first;
                     int k;
                     String newLiteral;
                     int n;
                     if (subRegExp.length() == 3 && subRegExp.indexOf("-") == 1) {
                        if (literal.startsWith("'%")) {
                           int startInt = subRegExp.charAt(0);
                           endInt = subRegExp.charAt(2);

                           for(k = startInt; k <= endInt; ++k) {
                              wi2 = new WhereItem();
                              wi2.setLeftWhereExp(lwc);
                              wi2.setOperator(wi.getOperator());
                              String newLiteral = StringFunctions.replaceFirst((char)k + "", replaceStr, literal);
                              colExp.setElementAt(newLiteral, j);
                              newColExp = new Vector();

                              for(n = 0; n < colExp.size(); ++n) {
                                 newColExp.add(colExp.get(n));
                              }

                              WhereColumn newRWC = new WhereColumn();
                              newRWC.setColumnExpression(newColExp);
                              newRWC.setOpenBrace(rwc.getOpenBrace());
                              newRWC.setCloseBrace(rwc.getCloseBrace());
                              wi2.setRightWhereExp(newRWC);
                              we.addWhereItem(wi2);
                              if (k + 1 <= endInt) {
                                 if (isNotLike) {
                                    we.addOperator("AND");
                                 } else {
                                    we.addOperator("OR");
                                 }
                              }
                           }

                           whereItemsList.setElementAt(we, i);
                        } else {
                           first = "'" + subRegExp.charAt(0) + "'";
                           newLiteral = "'" + subRegExp.charAt(2) + "'";
                           WhereItem wi1 = new WhereItem();
                           wi1.setLeftWhereExp(lwc);
                           wi1.setOperator("LIKE");
                           newLiteral = StringFunctions.replaceFirst("_", replaceStr, literal);
                           colExp.setElementAt(newLiteral, j);
                           wi1.setRightWhereExp(rwc);
                           newWI = new WhereItem();
                           newRWC = new WhereColumn();
                           newRWCExp = new Vector();
                           FunctionCalls fc = new FunctionCalls();
                           newLWCExp = new Vector();
                           newLWCExp.add(lwc);
                           newLWCExp.add(index1 + "");
                           newLWCExp.add("1");
                           TableColumn tcfn = new TableColumn();
                           tcfn.setColumnName("SUBSTR");
                           fc.setFunctionName(tcfn);
                           fc.setFunctionArguments(newLWCExp);
                           newRWCExp.add(fc);
                           newRWC.setColumnExpression(newRWCExp);
                           newWI.setLeftWhereExp(newRWC);
                           if (isNotLike) {
                              newWI.setOperator("NOT BETWEEN");
                           } else {
                              newWI.setOperator("BETWEEN");
                           }

                           WhereColumn rwc2 = new WhereColumn();
                           fnArgs = new Vector();
                           fnArgs.add(first);
                           fnArgs.add("AND");
                           fnArgs.add(newLiteral);
                           rwc2.setColumnExpression(fnArgs);
                           newWI.setRightWhereExp(rwc2);
                           if (isNotLike) {
                              WhereExpression we1 = new WhereExpression();
                              we1.setOpenBrace("(");
                              we1.setCloseBrace(")");
                              we1.addWhereItem(wi1);
                              we1.addOperator("AND");
                              we1.addWhereItem(newWI);
                              we.addWhereExpression(we1);
                              we.addOperator("OR");
                              WhereItem wi3 = new WhereItem();
                              wi3.setLeftWhereExp(lwc);
                              wi3.setOperator("NOT LIKE");
                              wi3.setRightWhereExp(rwc);
                              we.addWhereItem(wi3);
                           } else {
                              we.addWhereItem(wi1);
                              we.addOperator("AND");
                              we.addWhereItem(newWI);
                           }

                           whereItemsList.setElementAt(we, i);
                        }
                     } else {
                        first = literal.substring(0, index1);
                        newLiteral = literal.substring(index2 + 1);

                        for(k = 0; k < subRegExp.length(); ++k) {
                           wi2 = null;
                           boolean oracleWildCards = false;
                           if (subRegExp.length() != 1 || subRegExp.charAt(0) != '%' && subRegExp.charAt(0) != '_') {
                              newLiteral = first + subRegExp.charAt(k) + newLiteral;
                           } else {
                              newLiteral = first + "\\" + subRegExp.charAt(0) + newLiteral;
                              oracleWildCards = true;
                           }

                           colExp.setElementAt(newLiteral, j);
                           newColExp = new Vector();

                           for(n = 0; n < colExp.size(); ++n) {
                              newColExp.add(colExp.get(n));
                           }

                           if (oracleWildCards) {
                              newColExp.add("ESCAPE");
                              newColExp.add("'\\'");
                           }

                           newWI = new WhereItem();
                           newWI.setLeftWhereExp(lwc);
                           newWI.setOperator(operator);
                           newLWC = new WhereColumn();
                           newLWC.setColumnExpression(newColExp);
                           newLWC.setOpenBrace(rwc.getOpenBrace());
                           newLWC.setCloseBrace(rwc.getCloseBrace());
                           newWI.setRightWhereExp(newLWC);
                           we.addWhereItem(newWI);
                           if (k + 1 < subRegExp.length()) {
                              if (isNotLike) {
                                 we.addOperator("AND");
                              } else {
                                 we.addOperator("OR");
                              }
                           }
                        }

                        whereItemsList.setElementAt(we, i);
                     }
                  }
                  break;
               }
            }
         }
      }

   }

   private String getPartialMatchKey(Iterator it, String key) {
      while(true) {
         if (it.hasNext()) {
            String existingKey = it.next().toString();
            if (key.indexOf(existingKey) != -1) {
               return existingKey;
            }

            if (existingKey.indexOf(key) != -1) {
               return existingKey;
            }

            if (key.indexOf("-&&") == -1 || existingKey.indexOf(key.substring(0, key.indexOf("-&&"))) == -1) {
               continue;
            }

            return existingKey;
         }

         return null;
      }
   }

   private FromClause handleInnerJoin(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, int database, WhereExpression whereExpression, Vector remainingFromItemList, FromClause convertedFromClause) throws ConvertException {
      try {
         Vector fromItemList = convertedFromClause.getFromItemList();
         int fromItemSize = fromItemList.size();
         int fis = 0;
         int baseTableIndex = -1;
         LinkedHashMap joinedTables = new LinkedHashMap();
         LinkedHashMap newInnerJoinedTables = new LinkedHashMap();
         StringBuffer joinTable = new StringBuffer();

         boolean joinPresent;
         FromClause newFromClause;
         for(joinPresent = false; fis < fromItemSize; ++fis) {
            FromTable ft = (FromTable)fromItemList.get(fis);
            if (ft != null && ft.getJoinClause() == null) {
               Vector newFromItems;
               if (joinTable.length() > 0) {
                  newFromClause = new FromClause();
                  newFromItems = new Vector();
                  newFromItems.addAll(fromItemList.subList(baseTableIndex, fis));
                  newFromClause.setFromItemList(newFromItems);
                  newFromClause.setOpenBraces("(");
                  newFromClause.setClosedBraces(")");
                  joinedTables.put(joinTable.toString(), newFromClause);
                  joinTable = new StringBuffer();
               }

               baseTableIndex = fis;
               if (!remainingFromItemList.contains(ft)) {
                  if (ft.getAliasName() != null) {
                     joinTable.append("&" + ft.getAliasName().trim() + "&");
                  } else {
                     joinTable.append("&" + ft.getTableName().toString().trim() + "&");
                  }

                  if (fis == fromItemSize - 1) {
                     newFromClause = new FromClause();
                     newFromItems = new Vector();
                     newFromItems.add(fromItemList.get(fis));
                     newFromClause.setFromItemList(newFromItems);
                     newFromClause.setOpenBraces("(");
                     newFromClause.setClosedBraces(")");
                     joinedTables.put(joinTable.toString(), newFromClause);
                  }
               }
            } else if (ft != null && ft.getJoinClause() != null) {
               joinPresent = true;
               if (ft.getAliasName() != null) {
                  joinTable.append("-&" + ft.getAliasName().trim() + "&");
               } else {
                  joinTable.append("-&" + ft.getTableName().toString().trim() + "&");
               }
            } else if (ft == null) {
               baseTableIndex = fis;
            }
         }

         if (!joinPresent) {
            return null;
         } else {
            Vector randomInnerJoinCondition = this.getJoinConditionsFromWhereExpression(whereExpression, from_sqs);
            newFromClause = null;
            Vector innerJoinCondition;
            if (fromItemList.indexOf((Object)null) == -1) {
               innerJoinCondition = this.arrangeTheWhereItemListAccordingToTheOrderInFromItemList(randomInnerJoinCondition, fromItemList);
            } else {
               innerJoinCondition = randomInnerJoinCondition;
            }

            int innerJoinConditionSize = innerJoinCondition.size();
            int ijc = 0;

            LinkedHashMap innerJoinTables;
            ArrayList leftExpAliases;
            int j;
            for(innerJoinTables = new LinkedHashMap(); ijc < innerJoinConditionSize; ++ijc) {
               WhereItem wi = null;
               if (innerJoinCondition.get(ijc) instanceof WhereItem) {
                  wi = (WhereItem)innerJoinCondition.get(ijc);
               }

               leftExpAliases = this.getTableAliasesWhereL(wi);
               ArrayList rightExpAliases = this.getTableAliasesWhereR(wi);
               StringBuffer hashBuf = new StringBuffer();

               for(j = 0; j < leftExpAliases.size(); ++j) {
                  if (j == leftExpAliases.size() - 1) {
                     hashBuf.append("&" + leftExpAliases.get(j).toString().trim() + "&-");
                  } else {
                     hashBuf.append("&" + leftExpAliases.get(j).toString().trim() + "&-");
                  }
               }

               for(j = 0; j < rightExpAliases.size(); ++j) {
                  if (j == rightExpAliases.size() - 1) {
                     hashBuf.append("&" + rightExpAliases.get(j).toString().trim() + "&-");
                  } else {
                     hashBuf.append("&" + rightExpAliases.get(j).toString().trim() + "&-");
                  }
               }

               String hashKey = hashBuf.substring(0, hashBuf.lastIndexOf("-")).toString();
               String existingKey = null;
               if (innerJoinTables.containsKey(hashKey)) {
                  existingKey = hashKey;
               } else if (hashKey.indexOf("-") == hashKey.lastIndexOf("-")) {
                  existingKey = this.getPartialMatchKey(innerJoinTables.keySet().iterator(), hashKey);
               }

               if (existingKey != null) {
                  Object oldObj = innerJoinTables.get(existingKey);
                  if (oldObj instanceof WhereExpression) {
                     ((WhereExpression)oldObj).addWhereItem(wi);
                     ((WhereExpression)oldObj).addOperator("AND");
                  } else if (oldObj instanceof WhereItem) {
                     WhereExpression newWhereExp = new WhereExpression();
                     WhereItem oldItem = (WhereItem)oldObj;
                     newWhereExp.addWhereItem(oldItem);
                     newWhereExp.addOperator("AND");
                     newWhereExp.addWhereItem(wi);
                     innerJoinTables.put(hashKey, newWhereExp);
                  }
               } else {
                  WhereExpression newWhereExp = new WhereExpression();
                  newWhereExp.addWhereItem(wi);
                  innerJoinTables.put(hashKey, newWhereExp);
               }
            }

            new ArrayList(innerJoinTables.keySet());
            leftExpAliases = null;
            Vector reorderedRemainingFromItems = new Vector();
            Vector reorderedFromItems = new Vector(fromItemList.size());

            for(j = 0; j < fromItemList.size(); ++j) {
               reorderedFromItems.add((Object)null);
            }

            j = remainingFromItemList.size();
            Vector fromItemListCopy = new Vector(fromItemList.size());
            fromItemListCopy.addAll(fromItemList);
            Vector prevTables = new Vector();

            String ftName;
            String rightExpAlias;
            FromTable joinFromTable;
            int ind;
            int rf;
            FromTable ft;
            FromTable ft1;
            String jAlias;
            for(rf = 0; rf < fromItemListCopy.size(); ++rf) {
               ft = (FromTable)fromItemListCopy.get(rf);
               if (ft != null) {
                  ftName = ft.getAliasName();
                  if (ftName == null) {
                     ftName = ft.getTableName().toString().trim();
                  }

                  ftName = "&" + ftName + "&";
                  String key = this.getPartialMatchKey(innerJoinTables.keySet().iterator(), ftName);
                  if (key == null) {
                     if (reorderedFromItems.elementAt(rf) == null) {
                        if (ft.getJoinClause() != null) {
                           reorderedFromItems.add(reorderedFromItems.indexOf((Object)null), ft);
                        } else if (rf == 0) {
                           reorderedFromItems.insertElementAt(ft, rf);
                        } else {
                           reorderedFromItems.add(ft);
                        }
                     } else if (ft.getJoinClause() != null) {
                        reorderedFromItems.add(reorderedFromItems.indexOf((Object)null), ft);
                     } else {
                        reorderedFromItems.insertElementAt(ft, rf);
                     }

                     fromItemListCopy.setElementAt((Object)null, rf);
                  } else {
                     String leftExpAlias = key.substring(1, key.indexOf("-") - 1);
                     rightExpAlias = key.substring(key.indexOf("-") + 2, key.length() - 1);
                     this.getPartialMatchKey(joinedTables.keySet().iterator(), "&" + leftExpAlias + "&");
                     int insertIndex;
                     if (key.startsWith(ftName)) {
                        insertIndex = rf;
                        ft1 = null;

                        for(int j = rf; j < fromItemListCopy.size(); ++j) {
                           ft1 = (FromTable)fromItemListCopy.get(j);
                           if (ft1 != null) {
                              jAlias = ft1.getAliasName();
                              if (jAlias == null) {
                                 jAlias = ft1.getTableName().toString();
                              }

                              if (jAlias.equalsIgnoreCase(rightExpAlias)) {
                                 insertIndex = j;
                                 break;
                              }
                           }
                        }

                        if (insertIndex != rf) {
                           if (ft.getJoinClause() != null) {
                              if (reorderedFromItems.indexOf((Object)null) < insertIndex) {
                                 reorderedFromItems.add(reorderedFromItems.indexOf((Object)null), ft);
                                 reorderedFromItems.add(reorderedFromItems.indexOf((Object)null), ft1);
                              } else {
                                 reorderedFromItems.add(insertIndex, ft1);
                                 reorderedFromItems.add(insertIndex, ft);
                              }
                           } else if (ft1.getJoinClause() != null) {
                              reorderedFromItems.add(reorderedFromItems.indexOf((Object)null), ft);
                              reorderedFromItems.add(reorderedFromItems.indexOf((Object)null), ft1);
                           } else {
                              reorderedFromItems.add(reorderedFromItems.indexOf((Object)null), ft1);
                              reorderedFromItems.add(reorderedFromItems.indexOf((Object)null), ft);
                           }

                           fromItemListCopy.setElementAt((Object)null, rf);
                           fromItemListCopy.setElementAt((Object)null, insertIndex);
                        } else {
                           reorderedFromItems.add(reorderedFromItems.indexOf((Object)null), ft);
                           fromItemListCopy.setElementAt((Object)null, rf);
                        }
                     } else if (!key.endsWith(ftName)) {
                        reorderedFromItems.add(reorderedFromItems.indexOf((Object)null), ft);
                        fromItemListCopy.setElementAt((Object)null, rf);
                     } else {
                        insertIndex = rf;
                        int loopSize = fromItemListCopy.size();
                        joinFromTable = null;

                        for(ind = 0; ind < loopSize; ++ind) {
                           joinFromTable = (FromTable)fromItemListCopy.get(ind);
                           if (joinFromTable != null) {
                              String ft1Alias = joinFromTable.getAliasName();
                              if (ft1Alias == null) {
                                 ft1Alias = joinFromTable.getTableName().toString();
                              }

                              if (ft1Alias.equalsIgnoreCase(leftExpAlias)) {
                                 insertIndex = ind;
                                 break;
                              }
                           }
                        }

                        if (insertIndex != rf) {
                           if (ft.getJoinClause() != null) {
                              reorderedFromItems.add(rf, joinFromTable);
                              reorderedFromItems.add(rf, ft);
                           } else {
                              reorderedFromItems.add(rf, ft);
                              reorderedFromItems.insertElementAt(joinFromTable, rf);
                           }

                           fromItemListCopy.setElementAt((Object)null, rf);
                           fromItemListCopy.setElementAt((Object)null, insertIndex);
                        } else {
                           reorderedFromItems.add(reorderedFromItems.indexOf((Object)null), ft);
                           fromItemListCopy.setElementAt((Object)null, rf);
                        }
                     }
                  }
               }
            }

            int swapPosition;
            for(rf = 0; rf < fromItemList.size(); ++rf) {
               ft = (FromTable)reorderedFromItems.get(rf);
               if (ft != null) {
                  ftName = ft.getAliasName();
                  if (ftName == null) {
                     ftName = ft.getTableName().toString();
                  }

                  for(swapPosition = 0; swapPosition < remainingFromItemList.size(); ++swapPosition) {
                     FromTable ft1 = (FromTable)remainingFromItemList.get(swapPosition);
                     rightExpAlias = ft1.getAliasName();
                     if (rightExpAlias == null) {
                        rightExpAlias = ft1.getTableName().toString();
                     }

                     if (ftName.equalsIgnoreCase(rightExpAlias)) {
                        reorderedRemainingFromItems.add(ft);
                     }
                  }
               }
            }

            remainingFromItemList.removeAll(reorderedRemainingFromItems);
            reorderedRemainingFromItems.addAll(remainingFromItemList);
            Vector tablesWithoutJoins = new Vector();
            FromClause fromClauseWithInnerJoin = new FromClause();
            fromClauseWithInnerJoin.setFromClause("FROM");
            Vector fromClauseWithInnerJoinItems = new Vector();
            String alias;
            if (innerJoinTables.isEmpty()) {
               fromClauseWithInnerJoinItems.addAll(joinedTables.values());
               fromClauseWithInnerJoinItems.addAll(reorderedRemainingFromItems);
            } else {
               swapPosition = 1;

               for(int rfy = 0; rfy < reorderedFromItems.size(); ++rfy) {
                  FromTable ft = (FromTable)reorderedFromItems.get(rfy);
                  if (ft != null) {
                     alias = ft.getAliasName();
                     if (alias == null) {
                        alias = ft.getTableName().toString().trim();
                     }

                     alias = "&" + alias + "&";
                     boolean keyAdded = false;
                     ft1 = null;
                     if (fromClauseWithInnerJoinItems.size() == 0) {
                        fromClauseWithInnerJoinItems.add(ft);
                        prevTables.add(alias);
                     } else {
                        if (ft.getJoinClause() != null) {
                           continue;
                        }

                        if (!innerJoinTables.isEmpty()) {
                           Iterator len = innerJoinTables.keySet().iterator();

                           while(len.hasNext()) {
                              String key = len.next().toString();
                              ind = key.toLowerCase().indexOf(alias.toLowerCase());
                              if (ind != -1) {
                                 FromTable newFt = new FromTable();
                                 newFt.setJoinClause("INNER JOIN");
                                 newFt.setOnOrUsingJoin("ON");
                                 Vector we = new Vector();
                                 key = this.getValidKeyforInnerJoinCondition(innerJoinTables, prevTables, alias);
                                 if (key.equalsIgnoreCase("")) {
                                    if (fromItemListCopy.size() > rfy + swapPosition) {
                                       reorderedFromItems.add(rfy + swapPosition, reorderedFromItems.remove(rfy));
                                       reorderedFromItems.add(rfy, reorderedFromItems.remove(rfy + swapPosition - 1));
                                       --rfy;
                                       ++swapPosition;
                                       keyAdded = true;
                                    }
                                 } else {
                                    swapPosition = 1;
                                    we.add(innerJoinTables.get(key));
                                    innerJoinTables.remove(key);
                                    FromClause newFtFC = new FromClause();
                                    Vector newFtFCItems = new Vector();
                                    newFtFCItems.add(ft);
                                    newFtFC.setFromItemList(newFtFCItems);
                                    newFt.setTableName(newFtFC);
                                    newFt.setJoinExpression(we);
                                    fromClauseWithInnerJoinItems.add(newFt);
                                    keyAdded = true;
                                    prevTables.add(alias);
                                 }
                                 break;
                              }
                           }

                           if (!keyAdded) {
                              swapPosition = 1;
                              fromClauseWithInnerJoinItems.add(ft);
                              prevTables.add(alias);
                           }
                        } else {
                           swapPosition = 1;
                           fromClauseWithInnerJoinItems.add(ft);
                        }
                     }

                     for(joinFromTable = this.getJoinTableForTheFromItem(ft, joinedTables, reorderedFromItems); joinFromTable != null; joinFromTable = this.getJoinTableForTheFromItem(ft, joinedTables, reorderedFromItems)) {
                        fromClauseWithInnerJoinItems.add(joinFromTable);
                        jAlias = joinFromTable.getAliasName();
                        if (jAlias == null) {
                           jAlias = joinFromTable.getTableName().toString();
                        }

                        jAlias = "&" + jAlias + "&";
                        prevTables.add(jAlias);
                     }
                  }
               }
            }

            if (!innerJoinTables.isEmpty() && !newInnerJoinedTables.isEmpty()) {
               boolean fromClauseAdded = false;
               Iterator en = innerJoinTables.keySet().iterator();
               rightExpAlias = null;

               while(en.hasNext()) {
                  rightExpAlias = en.next().toString();
                  alias = rightExpAlias.substring(0, rightExpAlias.indexOf("-")).toLowerCase();
                  String joinedTableName2 = rightExpAlias.substring(rightExpAlias.indexOf("-") + 1).toLowerCase();
                  Iterator en2 = newInnerJoinedTables.keySet().iterator();
                  String en2Key = null;

                  label256: {
                     do {
                        if (!en2.hasNext()) {
                           break label256;
                        }

                        en2Key = en2.next().toString();
                     } while(en2Key.toLowerCase().indexOf(alias) == -1 && en2Key.toLowerCase().indexOf(joinedTableName2) == -1);

                     FromClause newInnerJoinTable = (FromClause)newInnerJoinedTables.get(en2Key);
                     ((WhereExpression)newInnerJoinTable.getFirstElement().getJoinExpression().lastElement()).getOperator().add("AND");
                     ((WhereExpression)newInnerJoinTable.getFirstElement().getJoinExpression().lastElement()).getWhereItems().add(innerJoinTables.get(rightExpAlias));
                     fromClauseAdded = true;
                  }

                  if (fromClauseAdded) {
                     newInnerJoinedTables.remove(en2Key);
                  }
               }
            } else if (!innerJoinTables.isEmpty()) {
               for(Iterator inIt = innerJoinTables.values().iterator(); inIt.hasNext(); whereExpression.getWhereItems().add(inIt.next())) {
                  if (!whereExpression.getWhereItems().isEmpty()) {
                     whereExpression.getOperator().add("AND");
                  }
               }
            }

            fromClauseWithInnerJoinItems.addAll(tablesWithoutJoins);
            fromClauseWithInnerJoin.setFromItemList(fromClauseWithInnerJoinItems);
            return fromClauseWithInnerJoin;
         }
      } catch (ArrayIndexOutOfBoundsException var42) {
         var42.printStackTrace();
         throw new ConvertException("Conversion failed.");
      }
   }

   private FromTable getJoinTableForTheFromItem(FromTable ft, LinkedHashMap joinMap, Vector fromItems) {
      FromTable joinFt = null;
      String alias = ft.getAliasName();
      if (ft.getAliasName() == null) {
         alias = ft.getTableName().toString();
      }

      alias = "&" + alias + "&";
      String jKey = this.getPartialMatchKey(joinMap.keySet().iterator(), alias);
      if (jKey != null) {
         int i = 0;

         for(int size = fromItems.size(); i < size; ++i) {
            if (fromItems.get(i) instanceof FromTable) {
               joinFt = (FromTable)fromItems.get(i);
               String joinAlias = joinFt.getAliasName();
               if (joinAlias == null) {
                  joinAlias = joinFt.getTableName().toString();
               }

               joinAlias = "&" + joinAlias + "&";
               if (jKey.indexOf(joinAlias) != -1 && !alias.equalsIgnoreCase(joinAlias)) {
                  String[] jKeyTables = jKey.split("-");
                  if (jKeyTables.length > 2) {
                     String newJoinKey = "";
                     int s = 0;

                     for(int len = jKeyTables.length; s < len; ++s) {
                        if (!jKeyTables[s].equalsIgnoreCase(joinAlias)) {
                           if (s != 0) {
                              newJoinKey = newJoinKey + "-";
                           }

                           newJoinKey = newJoinKey + jKeyTables[s];
                        }
                     }

                     joinMap.put(newJoinKey, joinMap.get(jKey));
                  }

                  joinMap.remove(jKey);
                  break;
               }
            }

            joinFt = null;
         }
      }

      return joinFt;
   }

   private String getValidKeyforInnerJoinCondition(LinkedHashMap joinMap, Vector prevTables, String currentTableAlias) {
      String key = "";
      Iterator it = joinMap.keySet().iterator();

      while(it.hasNext() && key.equalsIgnoreCase("")) {
         String key1 = it.next().toString().toLowerCase();
         String[] keyTables = null;
         keyTables = key1.split("-");

         for(int i = prevTables.size(); i > 0; --i) {
            String prevTableAlias = prevTables.get(i - 1).toString();
            if (key1.indexOf("-") == key1.lastIndexOf("-")) {
               if (key1.indexOf(prevTableAlias) != -1 && key1.indexOf(currentTableAlias) != -1) {
                  key = key1;
                  break;
               }
            } else {
               boolean canJoin = true;
               int kt = 0;

               for(int size = keyTables.length; kt < size; ++kt) {
                  if (!keyTables[kt].equalsIgnoreCase(currentTableAlias) && !prevTables.contains(keyTables[kt])) {
                     canJoin = false;
                     break;
                  }
               }

               if (canJoin) {
                  key = key1;
                  break;
               }
            }
         }
      }

      return key;
   }

   private void setNewFromClauseWithANSIJoin(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, int database, WhereExpression whereExpression) throws ConvertException {
      boolean isInformixOuterJoin = false;
      if (from_sqs != null) {
         FromClause fromClause = to_sqs.getFromClause();
         this.removedFromItemsList = new Vector();
         if (fromClause != null) {
            FromClause newFromClause = new FromClause();
            Vector newFromItemList = new Vector();
            newFromClause.setFromClause("FROM");
            newFromClause.setFromItemList(newFromItemList);
            newFromClause.setObjectContext(this.context);
            Vector fromItemList = fromClause.getFromItemList();
            Vector CheckOuterFromList = fromClause.changeOrderForOuter(fromItemList);

            for(int i = 0; i < fromItemList.size(); ++i) {
               if (fromItemList.get(i) instanceof FromTable) {
                  FromTable newFromTable = (FromTable)fromItemList.get(i);
                  if (newFromTable.getOuter() != null) {
                     isInformixOuterJoin = true;
                  }
               }
            }

            Vector whereItemList = new Vector();
            Vector operatorList = new Vector();
            this.loadWhereItemsOperators(whereItemList, operatorList);
            int size = fromItemList.size();
            if (size < whereItemList.size()) {
               size = whereItemList.size();
            }

            Vector[] joinExpression = new Vector[size];
            boolean fromAndWhereChanged = false;
            int j;
            String tableAliasRight;
            String tableOrAliasName;
            FromTable remainingFromItems;
            String weWhereItemLeftAlias;
            Object obj;
            String innerJoinOnCondition;
            FromTable existingFromTable;
            WhereItem whereItem;
            if (!isInformixOuterJoin) {
               if (database == 1) {
                  whereItemList = this.arrangeTheWhereItemListAccordingToTheOrderInFromItemList(whereItemList, fromItemList);
               }

               this.groupWhereItems(whereItemList, joinExpression, to_sqs, from_sqs, database);
               this.orderWhereItems(whereItemList, joinExpression);
               int index = 0;
               boolean isRemainingFromListItemsAdded = false;
               WhereExpression we = new WhereExpression();
               we.setObjectContext(this.context);
               int i = 0;

               while(true) {
                  WhereItem wit;
                  int k;
                  if (i >= whereItemList.size()) {
                     if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                        for(i = 0; i < we.getWhereItems().size(); ++i) {
                           wit = null;
                           weWhereItemLeftAlias = null;
                           obj = null;
                           if (we.getWhereItems().get(i) instanceof WhereItem) {
                              wit = (WhereItem)we.getWhereItems().get(i);
                           } else if (we.getWhereItems().get(i) instanceof WhereExpression && ((WhereExpression)we.getWhereItems().get(i)).getWhereItems().lastElement() instanceof WhereItem) {
                              wit = (WhereItem)((WhereExpression)we.getWhereItems().get(i)).getWhereItems().lastElement();
                           }

                           if (wit != null) {
                              weWhereItemLeftAlias = wit.getLeftWhereExp().getTableAlias();
                           }

                           boolean added = false;

                           for(j = newFromItemList.size() - 1; j >= 0; --j) {
                              if (newFromItemList.get(j) instanceof FromTable) {
                                 existingFromTable = (FromTable)newFromItemList.get(j);
                                 if (existingFromTable.getJoinExpression() != null && !existingFromTable.getJoinExpression().isEmpty()) {
                                    Vector joinExp = existingFromTable.getJoinExpression();

                                    for(k = joinExp.size() - 1; k >= 0; --k) {
                                       if (joinExp.get(k) instanceof WhereExpression) {
                                          WhereExpression joinWhereExp = (WhereExpression)joinExp.get(k);
                                          if (joinWhereExp.getWhereItems().lastElement() instanceof WhereItem) {
                                             whereItem = (WhereItem)joinWhereExp.getWhereItems().lastElement();
                                             String joinWhereItemLeftAlias = whereItem.getLeftWhereExp().getTableAlias();
                                             String joinWhereItemRightAlias = whereItem.getRightWhereExp().getTableAlias();
                                             if (weWhereItemLeftAlias != null && (joinWhereItemLeftAlias != null && weWhereItemLeftAlias.equalsIgnoreCase(joinWhereItemLeftAlias) || joinWhereItemRightAlias != null && weWhereItemLeftAlias.equalsIgnoreCase(joinWhereItemRightAlias))) {
                                                joinWhereExp.addWhereExpression(we);
                                                joinWhereExp.addOperator("AND");
                                                added = true;
                                                break;
                                             }

                                             if (weWhereItemLeftAlias == null) {
                                                joinWhereExp.addWhereExpression(we);
                                                joinWhereExp.addOperator("AND");
                                                added = true;
                                                break;
                                             }
                                          }
                                       }
                                    }

                                    if (added) {
                                       break;
                                    }
                                 }
                              }
                           }

                           if (!added) {
                              if (!whereExpression.getOperator().contains("AND") && !whereExpression.getOperator().contains("and")) {
                                 if (whereExpression.getWhereItems().size() > 0 && whereExpression.getWhereItems().lastElement() != null) {
                                    whereExpression.getOperator().add("AND");
                                 }
                              } else {
                                 whereExpression.getOperator().add("AND");
                              }

                              whereExpression.getWhereItems().add(we.getWhereItems().get(i));
                           }
                        }
                     }

                     if (fromItemList.size() <= 0) {
                        break;
                     }

                     for(i = 0; i < fromItemList.size(); ++i) {
                        FromTable remainingFromItems = null;
                        if (fromItemList.get(i) instanceof FromTable) {
                           remainingFromItems = (FromTable)fromItemList.get(i);
                        }

                        newFromItemList.add(remainingFromItems);
                     }

                     if (database != 12 && database != 8) {
                        break;
                     }

                     FromClause tempFromClause = this.handleInnerJoin(to_sqs, from_sqs, database, whereExpression, fromItemList, newFromClause);
                     if (tempFromClause != null) {
                        newFromClause = tempFromClause;
                     }
                     break;
                  }

                  wit = (WhereItem)whereItemList.get(i);
                  fromAndWhereChanged = true;
                  String tableAliasL = this.getTableAliasWhereL(wit);
                  FromTable ft;
                  if (i == 0) {
                     if (tableAliasL != null) {
                        newFromItemList.add(index, this.getFromItemAfterConversion(fromItemList, tableAliasL, database));
                     } else {
                        ft = null;
                        ft = this.getFromTableForNoAlias(wit, false, from_sqs, fromItemList, database);
                        newFromItemList.add(index, ft);
                     }

                     ft = (FromTable)newFromItemList.get(index);
                     if (ft != null && ft.getAliasName() == null) {
                        this.removedFromItemsList.add(ft);
                     }

                     ++index;
                     innerJoinOnCondition = this.getTableAliasWhereR(wit);
                     tableAliasRight = null;
                     if (innerJoinOnCondition != null) {
                        existingFromTable = this.getFromItemAfterConversion(fromItemList, innerJoinOnCondition, database);
                     } else {
                        existingFromTable = this.getFromTableForNoAlias(wit, true, from_sqs, fromItemList, database);
                     }

                     if (existingFromTable != null && existingFromTable.getAliasName() == null) {
                        this.removedFromItemsList.add(existingFromTable);
                     }

                     tableOrAliasName = this.getJoinType(wit, existingFromTable);
                     if (tableOrAliasName == null) {
                        if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                           we.addOperator("AND");
                        }

                        if (joinExpression != null && joinExpression[i] != null && !joinExpression[i].isEmpty() && joinExpression[i].get(0) instanceof WhereExpression) {
                           we.addWhereExpression((WhereExpression)joinExpression[i].get(0));
                        } else {
                           we.addWhereItem(wit);
                           wit.setLeftJoin((String)null);
                           wit.setRightJoin((String)null);
                        }
                     } else {
                        existingFromTable.setJoinClause(tableOrAliasName);
                        existingFromTable.setOnOrUsingJoin(" ON ");
                        if (this.selectStmtInJoin) {
                           WhereExpression we1 = (WhereExpression)joinExpression[i].get(0);
                           WhereItem wi1 = (WhereItem)we1.getWhereItems().get(0);
                           wi1.setRightWhereExp(wit.getRightWhereExp());
                           joinExpression[i].remove(0);
                           joinExpression[i].add(0, we1);
                           this.selectStmtInJoin = false;
                        }

                        existingFromTable.setJoinExpression(joinExpression[i]);
                        newFromItemList.add(index, existingFromTable);
                        ++index;
                     }
                  } else {
                     ft = this.getFromItemAfterConversion(fromItemList, tableAliasL, database);
                     if (ft == null) {
                        innerJoinOnCondition = this.getTableAliasWhereR(wit);
                        if (innerJoinOnCondition != null) {
                           ft = this.getFromItemAfterConversion(fromItemList, innerJoinOnCondition, database);
                        } else {
                           ft = this.getFromTableForNoAlias(wit, true, from_sqs, fromItemList, database);
                        }
                     }

                     if (ft != null && ft.getAliasName() == null) {
                        this.removedFromItemsList.add(ft);
                     }

                     innerJoinOnCondition = this.getJoinType(ft, whereItemList, i);
                     if (innerJoinOnCondition == null) {
                        if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                           we.addOperator("AND");
                        }

                        wit.setLeftJoin((String)null);
                        wit.setRightJoin((String)null);
                        if (joinExpression != null && joinExpression[i] != null && !joinExpression[i].isEmpty() && joinExpression[i].get(0) instanceof WhereExpression) {
                           we.addWhereExpression((WhereExpression)joinExpression[i].get(0));
                        } else {
                           we.addWhereItem(wit);
                        }
                     } else {
                        ft.setJoinClause(innerJoinOnCondition);
                        ft.setOnOrUsingJoin(" ON ");
                        ft.setJoinExpression(joinExpression[i]);
                        tableAliasRight = this.getTableAliasWhereR(wit);
                        tableOrAliasName = this.getTableAliasWhereL(wit);
                        if ((ft.getAliasName() == null || !ft.getAliasName().equalsIgnoreCase(tableAliasRight)) && (ft.getAliasName() != null || !ft.getTableName().toString().equalsIgnoreCase(tableAliasRight))) {
                           FromTable remainingFromItems;
                           if ((ft.getAliasName() == null || !ft.getAliasName().equalsIgnoreCase(tableOrAliasName)) && (ft.getAliasName() != null || !ft.getTableName().toString().equalsIgnoreCase(tableOrAliasName))) {
                              System.out.println("WhereExpression: It should never come here");
                              if (fromItemList.size() > 1) {
                                 k = fromItemList.size();
                                 if (i > fromItemList.size()) {
                                    remainingFromItems = null;
                                    if (fromItemList.get(0) instanceof FromTable) {
                                       remainingFromItems = (FromTable)fromItemList.get(0);
                                    }

                                    if (remainingFromItems != null) {
                                       remainingFromItems.setJoinClause(innerJoinOnCondition);
                                       remainingFromItems.setOnOrUsingJoin(" ON ");
                                       remainingFromItems.setJoinExpression(ft.getJoinExpression());
                                       ft.setJoinClause((String)null);
                                       ft.setJoinExpression((Vector)null);
                                       ft.setOnOrUsingJoin((String)null);
                                       remainingFromItems = ft;
                                       ft = remainingFromItems;
                                       remainingFromItems = remainingFromItems;
                                    }

                                    newFromItemList.add(index, remainingFromItems);
                                    ++index;
                                 } else {
                                    remainingFromItems = null;
                                    if (fromItemList.get(i - 1) instanceof FromTable) {
                                       remainingFromItems = (FromTable)fromItemList.get(i - 1);
                                    }

                                    if (remainingFromItems != null) {
                                       remainingFromItems.setJoinClause(innerJoinOnCondition);
                                       remainingFromItems.setOnOrUsingJoin(" ON ");
                                       remainingFromItems.setJoinExpression(ft.getJoinExpression());
                                       ft.setJoinClause((String)null);
                                       ft.setJoinExpression((Vector)null);
                                       ft.setOnOrUsingJoin((String)null);
                                       remainingFromItems = ft;
                                       ft = remainingFromItems;
                                       remainingFromItems = remainingFromItems;
                                    }

                                    newFromItemList.add(index, remainingFromItems);
                                    ++index;
                                 }

                                 isRemainingFromListItemsAdded = true;
                              } else if (fromItemList.size() == 1 && ft.getAliasName() != null) {
                                 FromTable remainingFromItems = null;
                                 if (fromItemList.get(0) instanceof FromTable) {
                                    remainingFromItems = (FromTable)fromItemList.get(0);
                                 }

                                 if (remainingFromItems != null) {
                                    remainingFromItems.setJoinClause(innerJoinOnCondition);
                                    remainingFromItems.setOnOrUsingJoin(" ON ");
                                    remainingFromItems.setJoinExpression(ft.getJoinExpression());
                                    ft.setJoinClause((String)null);
                                    ft.setJoinExpression((Vector)null);
                                    ft.setOnOrUsingJoin((String)null);
                                    remainingFromItems = ft;
                                    ft = remainingFromItems;
                                    remainingFromItems = remainingFromItems;
                                 }

                                 newFromItemList.add(index, remainingFromItems);
                                 ++index;
                                 isRemainingFromListItemsAdded = true;
                              }
                           } else if (fromItemList.size() > 1) {
                              k = fromItemList.size();
                              String tableAliasR = this.getTableAliasWhereR(wit);
                              remainingFromItems = this.getFromItemAfterConversion(fromItemList, tableAliasR, database);
                              if (remainingFromItems != null && remainingFromItems.getAliasName() == null) {
                                 this.removedFromItemsList.add(remainingFromItems);
                              }

                              innerJoinOnCondition = this.getJoinType(wit, remainingFromItems);
                              if (remainingFromItems != null) {
                                 remainingFromItems.setJoinClause(innerJoinOnCondition);
                                 remainingFromItems.setOnOrUsingJoin(" ON ");
                                 remainingFromItems.setJoinExpression(ft.getJoinExpression());
                                 ft.setJoinClause((String)null);
                                 ft.setJoinExpression((Vector)null);
                                 ft.setOnOrUsingJoin((String)null);
                                 FromTable tempFT = ft;
                                 ft = remainingFromItems;
                                 remainingFromItems = tempFT;
                              }

                              if (remainingFromItems != null) {
                                 if (i > fromItemList.size()) {
                                    newFromItemList.add(index, remainingFromItems);
                                    ++index;
                                 } else {
                                    newFromItemList.add(index, remainingFromItems);
                                    ++index;
                                 }
                              }

                              isRemainingFromListItemsAdded = true;
                           } else if (fromItemList.size() == 1) {
                              String tableAliasR = this.getTableAliasWhereR(wit);
                              remainingFromItems = this.getFromItemAfterConversion(fromItemList, tableAliasR, database);
                              if (remainingFromItems != null && remainingFromItems.getAliasName() == null) {
                                 this.removedFromItemsList.add(remainingFromItems);
                              }

                              innerJoinOnCondition = this.getJoinType(wit, remainingFromItems);
                              if (remainingFromItems != null) {
                                 remainingFromItems.setJoinClause(innerJoinOnCondition);
                                 remainingFromItems.setOnOrUsingJoin(" ON ");
                                 remainingFromItems.setJoinExpression(ft.getJoinExpression());
                                 ft.setJoinClause((String)null);
                                 ft.setJoinExpression((Vector)null);
                                 ft.setOnOrUsingJoin((String)null);
                                 remainingFromItems = ft;
                                 ft = remainingFromItems;
                                 remainingFromItems = remainingFromItems;
                              }

                              if (remainingFromItems != null) {
                                 newFromItemList.add(index, remainingFromItems);
                                 ++index;
                                 isRemainingFromListItemsAdded = true;
                              }
                           }
                        }

                        newFromItemList.add(index, ft);
                        ++index;
                     }
                  }

                  ++i;
               }
            } else {
               Vector outerTableNames = fromClause.getOuterFromTableNames(fromItemList);
               Hashtable whereItemsKeptInWhereClause = new Hashtable();
               Vector newWhereClause = new Vector();
               boolean isOuter = false;
               this.moveOuterWhereItemsAsANSIJoins(whereItemsKeptInWhereClause, whereItemList, outerTableNames);
               Vector tableOrAliasNameBeforeOuter;
               int i;
               if (whereItemList.size() > 0) {
                  tableOrAliasNameBeforeOuter = whereExpression.getOperator();
                  boolean removeOpFollowingFirstExpression = false;
                  int getsize = tableOrAliasNameBeforeOuter.size();
                  i = 0;

                  for(j = 0; j <= getsize; ++j) {
                     tableAliasRight = "" + j;
                     if (!whereItemsKeptInWhereClause.containsKey(tableAliasRight)) {
                        if (j - i != 0) {
                           if (tableOrAliasNameBeforeOuter.size() > 0) {
                              tableOrAliasNameBeforeOuter.removeElementAt(j - 1 - i);
                              ++i;
                           }
                        } else if (tableOrAliasNameBeforeOuter.size() > 0) {
                           tableOrAliasNameBeforeOuter.removeElementAt(j - i);
                           ++i;
                        }
                     }
                  }

                  whereExpression.setOperator(tableOrAliasNameBeforeOuter);
               }

               for(int i = 0; i < whereItemsKeptInWhereClause.size() + whereItemList.size(); ++i) {
                  weWhereItemLeftAlias = "" + i;
                  if (whereItemsKeptInWhereClause.get(weWhereItemLeftAlias) != null) {
                     obj = whereItemsKeptInWhereClause.get(weWhereItemLeftAlias);
                     if (obj instanceof WhereItem) {
                        if (database == 1) {
                           obj = ((WhereItem)obj).toOracleSelect(to_sqs, from_sqs);
                        } else if (database == 2) {
                           obj = ((WhereItem)obj).toMSSQLServerSelect(to_sqs, from_sqs);
                        } else if (database == 7) {
                           obj = ((WhereItem)obj).toSybaseSelect(to_sqs, from_sqs);
                        } else if (database == 3) {
                           obj = ((WhereItem)obj).toDB2Select(to_sqs, from_sqs);
                        } else if (database == 5) {
                           obj = ((WhereItem)obj).toMySQLSelect(to_sqs, from_sqs);
                        } else if (database == 8) {
                           obj = ((WhereItem)obj).toANSISelect(to_sqs, from_sqs);
                        } else if (database == 4) {
                           obj = ((WhereItem)obj).toPostgreSQLSelect(to_sqs, from_sqs);
                        } else if (database == 14) {
                           obj = ((WhereItem)obj).toBigQuerySelect(to_sqs, from_sqs);
                        } else if (database == 6) {
                           obj = ((WhereItem)obj).toInformixSelect(to_sqs, from_sqs);
                        } else if (database == 10) {
                           obj = ((WhereItem)obj).toTimesTenSelect(to_sqs, from_sqs);
                        } else if (database == 11) {
                           obj = ((WhereItem)obj).toNetezzaSelect(to_sqs, from_sqs);
                        } else if (database == 13) {
                           obj = ((WhereItem)obj).toVectorWiseSelect(to_sqs, from_sqs);
                        } else if (database == 15) {
                           obj = ((WhereItem)obj).toSnowflakeSelect(to_sqs, from_sqs);
                        } else if (database == 16) {
                           obj = ((WhereItem)obj).toAthenaSelect(to_sqs, from_sqs);
                        } else if (database == 17) {
                           obj = ((WhereItem)obj).toSapHanaSelect(to_sqs, from_sqs);
                        } else if (database == 18) {
                           obj = ((WhereItem)obj).toSqliteSelect(to_sqs, from_sqs);
                        } else if (database == 20) {
                           obj = ((WhereItem)obj).toExcelSelect(to_sqs, from_sqs);
                        } else if (database == 21) {
                           obj = ((WhereItem)obj).toMsAccessJdbcSelect(to_sqs, from_sqs);
                        }
                     }

                     newWhereClause.add(obj);
                  }
               }

               whereExpression.setWhereItem(newWhereClause);
               tableOrAliasNameBeforeOuter = new Vector();
               Vector WhereItemsInOuter = new Vector();
               boolean setAllTablesBeforeOuterasCrossJoin = false;

               for(i = 0; i < CheckOuterFromList.size(); ++i) {
                  innerJoinOnCondition = new String();
                  existingFromTable = (FromTable)CheckOuterFromList.get(i);
                  new String();
                  if (existingFromTable.getAliasName() != null) {
                     tableOrAliasName = existingFromTable.getAliasName();
                     tableOrAliasNameBeforeOuter.add(tableOrAliasName);
                  } else {
                     tableOrAliasName = existingFromTable.getTableName().toString();
                     tableOrAliasNameBeforeOuter.add(tableOrAliasName);
                  }

                  Vector addJoinExpression = new Vector();
                  int j;
                  if (existingFromTable.getOuter() != null || isOuter) {
                     fromAndWhereChanged = true;
                     if (!setAllTablesBeforeOuterasCrossJoin && (database == 2 || database == 4)) {
                        for(j = 1; j < i; ++j) {
                           remainingFromItems = (FromTable)CheckOuterFromList.get(j);
                           remainingFromItems.setJoinClause("CROSS JOIN");
                        }

                        setAllTablesBeforeOuterasCrossJoin = true;
                     } else if (!setAllTablesBeforeOuterasCrossJoin) {
                        for(j = 1; j < i; ++j) {
                           remainingFromItems = (FromTable)CheckOuterFromList.get(j);
                           remainingFromItems.setJoinClause("INNER JOIN");
                           remainingFromItems.setOnOrUsingJoin("ON");
                           Vector newWhereItems = new Vector();
                           WhereItem newWhereItem = new WhereItem();
                           WhereColumn newWhereColumn = new WhereColumn();
                           Vector whereColumnItems = new Vector();
                           whereColumnItems.add("1");
                           newWhereColumn.setColumnExpression(whereColumnItems);
                           newWhereItem.setLeftWhereExp(newWhereColumn);
                           newWhereItem.setRightWhereExp(newWhereColumn);
                           newWhereItem.setOperator("=");
                           newWhereItems.add(newWhereItem);
                           remainingFromItems.setJoinExpression(newWhereItems);
                        }

                        setAllTablesBeforeOuterasCrossJoin = true;
                     }

                     if (!isOuter) {
                        existingFromTable.setJoinClause("LEFT OUTER JOIN");
                     } else if (database != 2 && database != 4) {
                        existingFromTable.setJoinClause("INNER JOIN");
                        innerJoinOnCondition = "ON 1 = 1 ";
                     } else {
                        existingFromTable.setJoinClause("CROSS JOIN");
                     }

                     existingFromTable.setOuter((String)null);
                     if (existingFromTable.getOuterOpenBrace() != null) {
                        existingFromTable.setOuterOpenBrace((String)null);
                        isOuter = true;
                     }

                     if (whereItemList != null) {
                        for(j = 0; j < whereItemList.size(); ++j) {
                           whereItem = (WhereItem)whereItemList.get(j);
                           WhereColumn leftWhereExp = whereItem.getLeftWhereExp();
                           WhereColumn rightWhereExp = whereItem.getRightWhereExp();
                           String tableOrAliasNameForLeftExp = new String();
                           String tableOrAliasNameForRightExp = new String();
                           TableColumn tableColumn;
                           if (leftWhereExp != null && leftWhereExp.getColumnExpression().get(0) instanceof TableColumn) {
                              new TableColumn();
                              tableColumn = (TableColumn)leftWhereExp.getColumnExpression().get(0);
                              if (tableColumn.getOwnerName() != null) {
                                 tableOrAliasNameForLeftExp = tableColumn.getOwnerName() + "." + tableColumn.getTableName();
                              } else {
                                 tableOrAliasNameForLeftExp = tableColumn.getTableName();
                              }
                           }

                           String tableAliaswhere;
                           if (leftWhereExp != null && leftWhereExp.getColumnExpression().get(0) instanceof String) {
                              tableAliaswhere = (String)leftWhereExp.getColumnExpression().get(0);
                              if (tableAliaswhere.indexOf(".") != -1) {
                                 tableOrAliasNameForLeftExp = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                              }
                           }

                           if (rightWhereExp != null && rightWhereExp.getColumnExpression().get(0) instanceof TableColumn) {
                              new TableColumn();
                              tableColumn = (TableColumn)rightWhereExp.getColumnExpression().get(0);
                              if (tableColumn.getOwnerName() != null) {
                                 tableOrAliasNameForRightExp = tableColumn.getOwnerName() + "." + tableColumn.getTableName();
                              } else {
                                 tableOrAliasNameForRightExp = tableColumn.getTableName();
                              }
                           }

                           if (rightWhereExp != null && rightWhereExp.getColumnExpression().get(0) instanceof String) {
                              tableAliaswhere = (String)rightWhereExp.getColumnExpression().get(0);
                              if (tableAliaswhere.indexOf(".") != -1) {
                                 tableOrAliasNameForRightExp = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                              }
                           }

                           if (tableOrAliasName.equalsIgnoreCase(tableOrAliasNameForRightExp) && tableOrAliasNameBeforeOuter.contains(tableOrAliasNameForLeftExp) || tableOrAliasName.equalsIgnoreCase(tableOrAliasNameForLeftExp) && tableOrAliasNameBeforeOuter.contains(tableOrAliasNameForRightExp) || tableOrAliasName.equalsIgnoreCase(tableOrAliasNameForLeftExp) && rightWhereExp != null && rightWhereExp.getColumnExpression().get(0) instanceof String) {
                              if (!isOuter) {
                                 if (WhereItemsInOuter.size() == 0) {
                                    addJoinExpression.add(whereItem);
                                    existingFromTable.setOnOrUsingJoin("ON");
                                 }

                                 if (j > 0) {
                                    whereItem.setOperator1("AND");
                                 }
                              } else {
                                 WhereItemsInOuter.add(whereItem);
                                 if (WhereItemsInOuter.size() > 1) {
                                    whereItem.setOperator1("AND");
                                 }
                              }
                           }
                        }

                        existingFromTable.setJoinExpression(addJoinExpression);
                     }

                     if (existingFromTable.getOuterClosedBrace() != null) {
                        existingFromTable.setOuterClosedBrace((String)null);
                        existingFromTable.setOnOrUsingJoin(innerJoinOnCondition + "ON");
                        isOuter = false;

                        for(j = 0; j < WhereItemsInOuter.size(); ++j) {
                           addJoinExpression.add(WhereItemsInOuter.get(j));
                        }
                     }
                  }

                  if (existingFromTable.getOuterClosedBrace() != null) {
                     existingFromTable.setOuterClosedBrace((String)null);
                     existingFromTable.setOnOrUsingJoin(innerJoinOnCondition + "ON");

                     for(j = 0; j < WhereItemsInOuter.size(); ++j) {
                        addJoinExpression.add(WhereItemsInOuter.get(j));
                     }
                  }

                  newFromClause.setFromItemList(CheckOuterFromList);
               }
            }

            if (fromAndWhereChanged) {
               to_sqs.setFromClause(newFromClause);
            }
         }

      }
   }

   private FromTable getFromItem(Vector fromItemList, String tableAliasWhere) {
      for(int i = 0; i < fromItemList.size(); ++i) {
         FromTable ft = (FromTable)fromItemList.get(i);
         if (ft.getAliasName() != null && CustomizeUtil.compareQuotedIdentifiers(ft.getAliasName(), tableAliasWhere, false)) {
            fromItemList.remove(i);
            return ft;
         }

         if (CustomizeUtil.compareQuotedIdentifiers(ft.getTableName().toString(), tableAliasWhere, false)) {
            fromItemList.remove(i);
            return ft;
         }
      }

      return null;
   }

   private FromTable getFromItemAfterConversion(Vector fromItemList, String tableAliasWhere, int database) throws ConvertException {
      int i;
      FromTable ft;
      if (this.removedFromItemsList != null) {
         for(i = 0; i < this.removedFromItemsList.size(); ++i) {
            ft = (FromTable)this.removedFromItemsList.get(i);
            if (ft.getAliasName() != null && CustomizeUtil.compareQuotedIdentifiers(ft.getAliasName(), tableAliasWhere, true)) {
               return null;
            }

            if (tableAliasWhere != null && CustomizeUtil.compareQuotedIdentifiers(tableAliasWhere, ft.getTableName().toString(), true)) {
               return null;
            }
         }
      }

      for(i = 0; i < fromItemList.size(); ++i) {
         ft = (FromTable)fromItemList.get(i);
         if (ft.getAliasName() != null && CustomizeUtil.compareQuotedIdentifiers(ft.getAliasName(), tableAliasWhere, true)) {
            fromItemList.remove(i);
            return ft.convert((SelectQueryStatement)null, (SelectQueryStatement)null, database);
         }

         if (tableAliasWhere != null && CustomizeUtil.compareQuotedIdentifiers(tableAliasWhere, ft.getTableName().toString(), true)) {
            fromItemList.remove(i);
            return ft.convert((SelectQueryStatement)null, (SelectQueryStatement)null, database);
         }
      }

      return null;
   }

   private String getTableAliasWhereL(WhereItem wit) {
      WhereColumn wcl = wit.getLeftWhereExp();
      if (wcl != null && wcl.getColumnExpression() != null && wcl.getColumnExpression().size() > 0) {
         for(int i = 0; i < wcl.getColumnExpression().size(); ++i) {
            if (wcl.getColumnExpression().get(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)wcl.getColumnExpression().get(i);
               if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier && SwisSQLOptions.fullyQualifiedWithDatabaseName && !tc.getOwnerName().equalsIgnoreCase("dbo") && this.targetDatabase == 7) {
                  return tc.getOwnerName() + "." + "dbo." + tc.getTableName();
               }

               if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                  return tc.getOwnerName() + "." + tc.getTableName();
               }

               return tc.getTableName();
            }

            String tableAliaswhere;
            if (wcl.getColumnExpression().get(i) instanceof FunctionCalls) {
               tableAliaswhere = this.getTableAliasName((FunctionCalls)wcl.getColumnExpression().get(i));
               if (tableAliaswhere != null && !tableAliaswhere.equals("")) {
                  return tableAliaswhere;
               }
            } else if (wcl.getColumnExpression().get(i) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)wcl.getColumnExpression().get(i);
               String tableName = this.getTableAliasName(cs);
               if (tableName != null && !tableName.equals("")) {
                  return tableName;
               }
            } else if (wcl.getColumnExpression().get(i) instanceof SelectColumn) {
               tableAliaswhere = this.getTableAliasWhereR((SelectColumn)wcl.getColumnExpression().get(i));
               if (tableAliaswhere != null && !tableAliaswhere.equals("")) {
                  return tableAliaswhere;
               }
            } else if (wcl.getColumnExpression().get(i) instanceof String) {
               tableAliaswhere = (String)wcl.getColumnExpression().get(i);
               if (tableAliaswhere.indexOf(".") != -1 && (!tableAliaswhere.startsWith("/*") || !tableAliaswhere.endsWith("*/"))) {
                  return tableAliaswhere.substring(i, tableAliaswhere.indexOf("."));
               }
            }
         }
      }

      return null;
   }

   private String getTableAliasWhereR(WhereItem wit) {
      WhereColumn wcr = wit.getRightWhereExp();
      if (wcr != null && wcr.getColumnExpression() != null && wcr.getColumnExpression().size() > 0) {
         for(int i = 0; i < wcr.getColumnExpression().size(); ++i) {
            if (wcr.getColumnExpression().get(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)wcr.getColumnExpression().get(i);
               if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier && SwisSQLOptions.fullyQualifiedWithDatabaseName && !tc.getOwnerName().equalsIgnoreCase("dbo") && this.targetDatabase == 7) {
                  return tc.getOwnerName() + "." + "dbo." + tc.getTableName();
               }

               if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                  return tc.getOwnerName() + "." + tc.getTableName();
               }

               return tc.getTableName();
            }

            String tableAliaswhere;
            if (wcr.getColumnExpression().get(i) instanceof FunctionCalls) {
               tableAliaswhere = this.getTableAliasName((FunctionCalls)wcr.getColumnExpression().get(i));
               if (tableAliaswhere != null && !tableAliaswhere.equals("")) {
                  return tableAliaswhere;
               }
            } else if (wcr.getColumnExpression().get(i) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)wcr.getColumnExpression().get(i);
               String tableName = this.getTableAliasName(cs);
               if (tableName != null && !tableName.equals("")) {
                  return tableName;
               }
            } else if (wcr.getColumnExpression().get(i) instanceof SelectColumn) {
               tableAliaswhere = this.getTableAliasWhereR((SelectColumn)wcr.getColumnExpression().get(i));
               if (tableAliaswhere != null && !tableAliaswhere.equals("")) {
                  return tableAliaswhere;
               }
            } else if (wcr.getColumnExpression().get(i) instanceof String) {
               tableAliaswhere = (String)wcr.getColumnExpression().get(i);
               if (tableAliaswhere.startsWith("'")) {
                  return "";
               }

               if (tableAliaswhere.indexOf(".") != -1 && (!tableAliaswhere.startsWith("/*") || !tableAliaswhere.endsWith("*/"))) {
                  if (tableAliaswhere.indexOf(".") != tableAliaswhere.lastIndexOf(".")) {
                     if (SwisSQLOptions.removeDBSchemaQualifier) {
                        return tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1, tableAliaswhere.lastIndexOf("."));
                     }

                     return tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                  }

                  return tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
               }
            }
         }
      }

      return null;
   }

   private ArrayList getTableAliasesWhereL(WhereItem wit) {
      ArrayList list = new ArrayList();
      WhereColumn wcl = wit.getLeftWhereExp();
      if (wcl != null && wcl.getColumnExpression() != null && wcl.getColumnExpression().size() > 0) {
         for(int i = 0; i < wcl.getColumnExpression().size(); ++i) {
            if (wcl.getColumnExpression().get(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)wcl.getColumnExpression().get(i);
               if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                  list.add(tc.getOwnerName() + "." + tc.getTableName());
               } else {
                  list.add(tc.getTableName());
               }
            } else {
               String tableName;
               if (wcl.getColumnExpression().get(i) instanceof FunctionCalls) {
                  FunctionCalls fcl = (FunctionCalls)wcl.getColumnExpression().get(i);
                  tableName = this.getTableAliasName(fcl);
                  if (tableName != null && !tableName.equals("")) {
                     list.add(tableName);
                  }

                  if (fcl.getFunctionName() != null && fcl.getFunctionName().getColumnName().equalsIgnoreCase("COALESCE")) {
                     String tableName1 = this.getTableAliasWhereR((SelectColumn)fcl.getFunctionArguments().get(1));
                     if (tableName1 != null && !tableName1.equals("")) {
                        list.add(tableName1);
                     }
                  }
               } else if (wcl.getColumnExpression().get(i) instanceof CaseStatement) {
                  CaseStatement cs = (CaseStatement)wcl.getColumnExpression().get(i);
                  tableName = this.getTableAliasName(cs);
                  if (tableName != null && !tableName.equals("")) {
                     list.add(tableName);
                  }
               } else {
                  String tableAliaswhere;
                  if (wcl.getColumnExpression().get(i) instanceof SelectColumn) {
                     tableAliaswhere = this.getTableAliasWhereR((SelectColumn)wcl.getColumnExpression().get(i));
                     if (tableAliaswhere != null && !tableAliaswhere.equals("")) {
                        list.add(tableAliaswhere);
                     }
                  } else if (wcl.getColumnExpression().get(i) instanceof String) {
                     tableAliaswhere = (String)wcl.getColumnExpression().get(i);
                     if (tableAliaswhere.indexOf(".") != -1 && (!tableAliaswhere.startsWith("/*") || !tableAliaswhere.endsWith("*/"))) {
                        if (tableAliaswhere.indexOf(".") != tableAliaswhere.lastIndexOf(".")) {
                           if (SwisSQLOptions.removeDBSchemaQualifier) {
                              tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                           }

                           list.add(tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf(".")));
                        }

                        list.add(tableAliaswhere.substring(0, tableAliaswhere.indexOf(".")));
                     }
                  }
               }
            }
         }
      }

      return list;
   }

   private ArrayList getTableAliasesWhereR(WhereItem wit) {
      ArrayList list = new ArrayList();
      WhereColumn wcr = wit.getRightWhereExp();
      if (wcr != null && wcr.getColumnExpression() != null && wcr.getColumnExpression().size() > 0) {
         for(int i = 0; i < wcr.getColumnExpression().size(); ++i) {
            if (wcr.getColumnExpression().get(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)wcr.getColumnExpression().get(i);
               if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                  list.add(tc.getOwnerName() + "." + tc.getTableName());
               } else {
                  list.add(tc.getTableName());
               }
            } else {
               String tableAliaswhere;
               if (wcr.getColumnExpression().get(i) instanceof FunctionCalls) {
                  tableAliaswhere = this.getTableAliasName((FunctionCalls)wcr.getColumnExpression().get(i));
                  if (tableAliaswhere != null && !tableAliaswhere.equals("")) {
                     list.add(tableAliaswhere);
                  }
               } else if (wcr.getColumnExpression().get(i) instanceof CaseStatement) {
                  CaseStatement cs = (CaseStatement)wcr.getColumnExpression().get(i);
                  String tableName = this.getTableAliasName(cs);
                  if (tableName != null && !tableName.equals("")) {
                     list.add(tableName);
                  }
               } else if (wcr.getColumnExpression().get(i) instanceof SelectColumn) {
                  tableAliaswhere = this.getTableAliasWhereR((SelectColumn)wcr.getColumnExpression().get(i));
                  if (tableAliaswhere != null && !tableAliaswhere.equals("")) {
                     list.add(tableAliaswhere);
                  }
               } else if (wcr.getColumnExpression().get(i) instanceof String) {
                  tableAliaswhere = (String)wcr.getColumnExpression().get(i);
                  if (tableAliaswhere.startsWith("'")) {
                     list.add("");
                  }

                  if (tableAliaswhere.indexOf(".") != -1 && (!tableAliaswhere.startsWith("/*") || !tableAliaswhere.endsWith("*/"))) {
                     if (tableAliaswhere.indexOf(".") != tableAliaswhere.lastIndexOf(".")) {
                        if (SwisSQLOptions.removeDBSchemaQualifier) {
                           tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                        }

                        list.add(tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf(".")));
                     }

                     list.add(tableAliaswhere.substring(0, tableAliaswhere.indexOf(".")));
                  }
               }
            }
         }
      }

      return list;
   }

   private String getTableAliasWhereR(SelectColumn sc) {
      if (sc != null && sc.getColumnExpression() != null && sc.getColumnExpression().size() > 0) {
         for(int i = 0; i < sc.getColumnExpression().size(); ++i) {
            if (sc.getColumnExpression().get(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)sc.getColumnExpression().get(i);
               if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                  return tc.getOwnerName() + "." + tc.getTableName();
               }

               return tc.getTableName();
            }

            String tableName;
            if (sc.getColumnExpression().get(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)sc.getColumnExpression().get(i);
               tableName = this.getTableAliasName(fc);
               if (tableName != null && !tableName.equals("")) {
                  return tableName;
               }
            } else if (sc.getColumnExpression().get(i) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)sc.getColumnExpression().get(i);
               tableName = this.getTableAliasName(cs);
               if (tableName != null && !tableName.equals("")) {
                  return tableName;
               }
            } else {
               String tableAliaswhere;
               if (sc.getColumnExpression().get(i) instanceof SelectColumn) {
                  tableAliaswhere = this.getTableAliasWhereR((SelectColumn)sc.getColumnExpression().get(i));
                  if (tableAliaswhere != null && !tableAliaswhere.equals("")) {
                     return tableAliaswhere;
                  }
               } else if (sc.getColumnExpression().get(i) instanceof String) {
                  tableAliaswhere = (String)sc.getColumnExpression().get(i);
                  if (tableAliaswhere.startsWith("'")) {
                     return "";
                  }

                  if (tableAliaswhere.indexOf(".") != -1 && (!tableAliaswhere.startsWith("/*") || !tableAliaswhere.endsWith("*/"))) {
                     if (tableAliaswhere.indexOf(".") != tableAliaswhere.lastIndexOf(".")) {
                        if (SwisSQLOptions.removeDBSchemaQualifier) {
                           return tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1, tableAliaswhere.lastIndexOf("."));
                        }

                        return tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                     }

                     return tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                  }
               }
            }
         }
      }

      return null;
   }

   private FromTable getFromTableForNoAlias(WhereItem wit, boolean right, SelectQueryStatement from_sqs, Vector fromItemList, int database) throws ConvertException {
      WhereColumn whc = null;
      FromTable ft = null;
      if (right) {
         whc = wit.getRightWhereExp();
      } else {
         whc = wit.getLeftWhereExp();
      }

      if (whc != null) {
         Vector colExpr = whc.getColumnExpression();
         if (colExpr != null && colExpr.size() > 0) {
            for(int k = 0; k < colExpr.size(); ++k) {
               if (colExpr.get(k) instanceof TableColumn) {
                  ft = this.getFromTableForTableColumnOrString(colExpr.get(k), ft, from_sqs, fromItemList, database, whc);
               } else {
                  Vector fnArgs;
                  if (!(colExpr.get(k) instanceof FunctionCalls)) {
                     if (colExpr.get(k) instanceof SelectQueryStatement) {
                        SelectQueryStatement sqs = (SelectQueryStatement)colExpr.get(k);
                        fnArgs = sqs.getSelectStatement().getSelectItemList();
                        SelectColumn sc = (SelectColumn)fnArgs.get(0);
                        FromTable newFromTable = new FromTable();
                        sqs.setOpenBrace("(");
                        sqs.setCloseBrace(")");
                        if (fnArgs.size() == 1) {
                           sc.setAliasName("ALIAS_1");
                        }

                        newFromTable.setTableName(sqs);
                        newFromTable.setAliasName("TABLE_ALIAS1");
                        WhereColumn wc = new WhereColumn();
                        Vector newRightExpression = new Vector();
                        TableColumn tc1 = new TableColumn();
                        tc1.setTableName(newFromTable.getAliasName());
                        tc1.setColumnName(sc.getAliasName());
                        tc1.setDot(".");
                        newRightExpression.add(tc1);
                        wc.setColumnExpression(newRightExpression);
                        wit.setRightWhereExp(wc);
                        this.selectStmtInJoin = true;
                        return newFromTable;
                     }

                     if (colExpr.get(k) instanceof CaseStatement) {
                        CaseStatement cs = (CaseStatement)colExpr.get(k);
                        WhereItem cswi = null;
                        Object cssc;
                        if (cs.getCaseCondition() != null) {
                           cswi = (WhereItem)cs.getCaseCondition().getWhereItem().get(0);
                        } else if (((WhenStatement)cs.getWhenClauseList().get(0)).getWhenCondition() != null) {
                           cssc = ((WhenStatement)cs.getWhenClauseList().get(0)).getWhenCondition().getWhereItem().get(0);
                           if (cssc instanceof WhereItem) {
                              cswi = (WhereItem)cssc;
                           } else if (cssc instanceof WhereExpression) {
                              cswi = (WhereItem)((WhereExpression)cssc).getWhereItems().get(0);
                           }
                        }

                        cssc = cswi.getLeftWhereExp().getColumnExpression().get(0);
                        if (cssc instanceof SelectColumn) {
                           Vector scColExpr = ((SelectColumn)cssc).getColumnExpression();
                           if (scColExpr != null) {
                              for(int m = 0; m < scColExpr.size(); ++m) {
                                 if (scColExpr.get(m) instanceof TableColumn) {
                                    ft = this.getFromTableForTableColumnOrString(scColExpr.get(m), ft, from_sqs, fromItemList, database, whc);
                                 }
                              }
                           }
                        } else if (cssc instanceof TableColumn) {
                           ft = this.getFromTableForTableColumnOrString(cssc, ft, from_sqs, fromItemList, database, whc);
                        } else if (cssc instanceof String) {
                           ft = this.getFromTableForTableColumnOrString(cssc, ft, from_sqs, fromItemList, database, whc);
                        }
                     } else if (colExpr.get(k) instanceof String) {
                        ft = this.getFromTableForTableColumnOrString(colExpr.get(k), ft, from_sqs, fromItemList, database, whc);
                     }
                  } else {
                     FunctionCalls fc = (FunctionCalls)colExpr.get(k);
                     fnArgs = fc.getFunctionArguments();
                     if (fnArgs != null) {
                        for(int l = 0; l < fnArgs.size(); ++l) {
                           if (fnArgs.get(l) instanceof TableColumn) {
                              ft = this.getFromTableForTableColumnOrString(fnArgs.get(l), ft, from_sqs, fromItemList, database, whc);
                           }

                           if (fnArgs.get(l) instanceof SelectColumn) {
                              SelectColumn sc = (SelectColumn)fnArgs.get(l);
                              Vector scColExpr = sc.getColumnExpression();
                              if (scColExpr != null) {
                                 for(int m = 0; m < scColExpr.size(); ++m) {
                                    if (scColExpr.get(m) instanceof TableColumn) {
                                       ft = this.getFromTableForTableColumnOrString(scColExpr.get(m), ft, from_sqs, fromItemList, database, whc);
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

      return ft;
   }

   private FromTable getFromTableForTableColumnOrString(Object object, FromTable ft, SelectQueryStatement from_sqs, Vector fromItemList, int database, WhereColumn whc) throws ConvertException {
      if (object instanceof TableColumn) {
         TableColumn tc1 = (TableColumn)object;
         ft = MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)tc1);
      } else if (object instanceof String) {
         ft = MetadataInfoUtil.getTableOfColumn(from_sqs, (String)object.toString());
      }

      if (ft != null && ft.getTableName() != null) {
         String tableAlias = ft.getTableName().toString();
         String alias = ft.getAliasName();
         if (alias != null) {
            tableAlias = ft.getAliasName();
         }

         ft = this.getFromItemAfterConversion(fromItemList, tableAlias, database);

         for(int n = 0; n < whc.getColumnExpression().size(); ++n) {
            Object obj = whc.getColumnExpression().get(n);
            if (obj != null) {
               if (obj instanceof TableColumn) {
                  TableColumn tc2 = (TableColumn)obj;
                  tc2.setTableName(tableAlias);
               } else {
                  Vector colWCExpr;
                  int s;
                  if (!(obj instanceof FunctionCalls)) {
                     if (obj instanceof String) {
                        Object oldObj = obj;
                        Object obj = tableAlias + "." + obj.toString();
                        colWCExpr = whc.getColumnExpression();
                        if (colWCExpr != null) {
                           for(s = 0; s < colWCExpr.size(); ++s) {
                              if (colWCExpr.get(s) instanceof String && colWCExpr.get(s).toString().equalsIgnoreCase(oldObj.toString())) {
                                 colWCExpr.setElementAt(obj, s);
                              }
                           }
                        }
                     }
                  } else {
                     FunctionCalls fc = (FunctionCalls)obj;
                     colWCExpr = fc.getFunctionArguments();
                     if (colWCExpr != null) {
                        for(s = 0; s < colWCExpr.size(); ++s) {
                           if (colWCExpr.get(s) instanceof SelectColumn) {
                              SelectColumn sc = (SelectColumn)colWCExpr.get(s);
                              Vector colExpression = sc.getColumnExpression();
                              if (colExpression != null) {
                                 for(int m = 0; m < colExpression.size(); ++m) {
                                    if (colExpression.get(m) instanceof TableColumn) {
                                       TableColumn tc2 = (TableColumn)colExpression.get(m);
                                       tc2.setTableName(tableAlias);
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

         return ft;
      } else {
         return ft;
      }
   }

   private String getJoinType(WhereItem wit) {
      if (wit.getLeftJoin() != null) {
         return new String(" LEFT OUTER JOIN ");
      } else {
         return wit.getRightJoin() != null ? new String(" RIGHT OUTER JOIN ") : new String(" INNER JOIN ");
      }
   }

   private String getJoinType(FromTable ft, Vector whereItemList, int position_of_curr_wit) {
      boolean flg = false;
      WhereItem curr_wit = (WhereItem)whereItemList.get(position_of_curr_wit);
      WhereColumn curr_rwc = curr_wit.getRightWhereExp();

      for(int k = 0; k < position_of_curr_wit; ++k) {
         WhereItem wit_in_check = (WhereItem)whereItemList.get(k);
         if (wit_in_check != null && curr_wit != null) {
            WhereColumn prev_rwc = wit_in_check.getRightWhereExp();
            if (curr_rwc != null && prev_rwc != null) {
               String curr_alias = curr_rwc.getTableAlias();
               String prev_alias = prev_rwc.getTableAlias();
               if (curr_alias != null && prev_alias != null && curr_rwc.getTableAlias().equals(prev_rwc.getTableAlias())) {
                  flg = true;
                  break;
               }
            }
         }
      }

      if (flg) {
         WhereColumn curr_lwc = curr_wit.getLeftWhereExp();
         curr_wit.setRightWhereExp(curr_lwc);
         curr_wit.setLeftWhereExp(curr_rwc);
         String ljoin = curr_wit.getLeftJoin();
         String rjoin = curr_wit.getRightJoin();
         if (ljoin != null) {
            curr_wit.setRightJoin(ljoin);
            curr_wit.setLeftJoin((String)null);
         }

         if (rjoin != null) {
            curr_wit.setLeftJoin(rjoin);
            curr_wit.setRightJoin((String)null);
         }
      }

      return this.getJoinType(curr_wit, ft);
   }

   private String getJoinType(WhereItem wit, FromTable ft) {
      String currentTableName = null;
      if (ft != null) {
         if (ft.getAliasName() != null) {
            currentTableName = ft.getAliasName();
         } else {
            currentTableName = ft.getTableName().toString();
         }

         String leftTable = this.getTableAliasWhereL(wit);
         String rightTable = this.getTableAliasWhereR(wit);
         if (wit.getLeftJoin() != null) {
            if (ft != null && ft.getTableName() instanceof SelectQueryStatement) {
               leftTable = ft.getAliasName();
            }

            if (rightTable != null && CustomizeUtil.compareQuotedIdentifiers(rightTable, currentTableName, true)) {
               return new String(" LEFT OUTER JOIN ");
            }

            if (leftTable != null && CustomizeUtil.compareQuotedIdentifiers(leftTable, currentTableName, true)) {
               return new String(" RIGHT OUTER JOIN ");
            }
         } else {
            if (wit.getRightJoin() == null) {
               return new String(" INNER JOIN ");
            }

            if (ft != null && ft.getTableName() instanceof SelectQueryStatement) {
               rightTable = ft.getAliasName();
            }

            if (leftTable != null && CustomizeUtil.compareQuotedIdentifiers(leftTable, currentTableName, true)) {
               return new String(" LEFT OUTER JOIN ");
            }

            if (rightTable != null && CustomizeUtil.compareQuotedIdentifiers(rightTable, currentTableName, true)) {
               return new String(" RIGHT OUTER JOIN ");
            }
         }
      }

      return null;
   }

   public void loadWhereItemsOperators(Vector whereItemList, Vector operatorList) {
      for(int i = 0; i < this.whereItems.size(); ++i) {
         Object obj = this.whereItems.get(i);
         if (obj instanceof WhereItem) {
            whereItemList.add(obj);
            operatorList.add(this.operators);
         } else if (obj instanceof WhereExpression) {
            WhereExpression we = (WhereExpression)obj;
            we.loadWhereItemsOperators(whereItemList, operatorList);
         }
      }

   }

   private void groupWhereItems(Vector whereItemList, Vector[] joinExpression, WhereExpression newWhereExpression) throws ConvertException {
      String[] tableAliasNameL = new String[whereItemList.size()];
      String[] tableAliasNameR = new String[whereItemList.size()];
      Vector wiTables = new Vector();
      Vector whereExpressions = new Vector();
      Vector operators = new Vector();
      int operator = -1;

      int i;
      int in;
      for(i = 0; i < whereItemList.size(); ++i) {
         new TableColumn();
         WhereItem wit = (WhereItem)whereItemList.get(i);
         WhereColumn wcl = wit.getLeftWhereExp();
         WhereColumn wcr = wit.getRightWhereExp();
         if (wit.getLeftJoin() == null && wit.getRightJoin() == null) {
            whereExpressions.add(wit);
            whereItemList.remove(i);
            --i;
            ++operator;
         } else {
            TableColumn tc;
            String tableAliaswhere;
            if (wcl.getColumnExpression() != null) {
               if (wcl.getColumnExpression().get(0) instanceof TableColumn) {
                  tc = (TableColumn)wcl.getColumnExpression().get(0);
                  if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                     tableAliasNameL[i] = tc.getOwnerName() + "." + tc.getTableName();
                  } else {
                     tableAliasNameL[i] = tc.getTableName();
                  }
               }

               if (wcl.getColumnExpression().get(0) instanceof String) {
                  tableAliaswhere = (String)wcl.getColumnExpression().get(0);
                  if (tableAliaswhere.indexOf(46) != -1) {
                     if (tableAliaswhere.indexOf(".") != tableAliaswhere.lastIndexOf(".")) {
                        if (SwisSQLOptions.removeDBSchemaQualifier) {
                           tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                        }

                        tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                     } else {
                        tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                     }
                  }
               }
            }

            if (wcr.getColumnExpression() != null) {
               if (wcr.getColumnExpression().get(0) instanceof TableColumn) {
                  tc = (TableColumn)wcr.getColumnExpression().get(0);
                  if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                     tableAliasNameR[i] = tc.getOwnerName() + "." + tc.getTableName();
                  } else {
                     tableAliasNameR[i] = tc.getTableName();
                  }
               }

               if (wcr.getColumnExpression().get(0) instanceof String) {
                  tableAliaswhere = (String)wcr.getColumnExpression().get(0);
                  if (tableAliaswhere.indexOf(46) != -1) {
                     if (tableAliaswhere.indexOf(".") != tableAliaswhere.lastIndexOf(".")) {
                        if (SwisSQLOptions.removeDBSchemaQualifier) {
                           tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                        }

                        tableAliasNameR[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                     } else {
                        tableAliasNameR[i] = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                     }
                  }
               }
            }

            boolean added = false;

            for(in = 0; in < wiTables.size(); ++in) {
               String LR = (String)wiTables.get(in);
               if (LR.equals(tableAliasNameL[i] + tableAliasNameR[i])) {
                  wiTables.add(tableAliasNameL[i] + tableAliasNameR[i]);
                  added = true;
                  break;
               }

               if (LR.equals(tableAliasNameR[i] + tableAliasNameL[i])) {
                  wiTables.add(tableAliasNameR[i] + tableAliasNameL[i]);
                  added = true;
                  break;
               }
            }

            if (!added) {
               wiTables.add(tableAliasNameL[i] + tableAliasNameR[i]);
            }
         }
      }

      for(i = 0; i < operator; ++i) {
         operators.add(" AND ");
      }

      newWhereExpression.setOperator(operators);
      newWhereExpression.setWhereItem(whereExpressions);
      boolean isSame = false;
      int loopCount = wiTables.size();

      for(int i = 0; i < loopCount; ++i) {
         joinExpression[i] = new Vector();
         WhereItem joinWhereItem = (WhereItem)whereItemList.get(i);
         joinExpression[i].add(joinWhereItem);
         int[] indexArray = new int[whereItemList.size()];
         int index = 0;

         int j;
         for(j = i + 1; j < loopCount; ++j) {
            if (wiTables.get(i).equals(wiTables.get(j))) {
               indexArray[index++] = j;
               isSame = true;
            }
         }

         if (isSame) {
            for(in = 0; in < indexArray.length && indexArray[in] != 0; ++in) {
               joinExpression[i].add(new String("AND"));
               WhereItem wi = (WhereItem)whereItemList.remove(indexArray[in]);
               wi.setMovedToFromClause(true);
               wiTables.remove(indexArray[in]);
               this.decrement(indexArray);
               wi.setLeftJoin((String)null);
               wi.setRightJoin((String)null);
               joinExpression[i].add(wi);
               --loopCount;
               --j;
            }

            isSame = false;
         }
      }

   }

   private void groupWhereItems(Vector whereItemList, Vector[] joinExpression, SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, int database) throws ConvertException {
      String[] tableAliasNameL = new String[whereItemList.size()];
      String[] tableAliasNameR = new String[whereItemList.size()];
      Vector wiTables = new Vector();
      WhereExpression whereExpressions = null;
      Vector operators = new Vector();
      int operator = -1;

      int i;
      WhereItem wit;
      WhereColumn wcl;
      WhereColumn wcr;
      String ithElement;
      String tableAliaswhere;
      String fromTableAlias;
      String tableAliaswhere;
      for(i = 0; i < whereItemList.size(); ++i) {
         wit = (WhereItem)whereItemList.get(i);
         if (wit != null && wit.getRightJoin() != null) {
            wcl = wit.getLeftWhereExp();
            wcr = wit.getRightWhereExp();
            if (wcl != null && wcr != null) {
               String lalias = this.getTableAliasName(wcl);
               int rightnum = true;
               boolean isRightNum = false;

               try {
                  ithElement = wcr.toString().trim();
                  Vector rwhcolExp = wcr.getColumnExpression();

                  for(int rwhi = 0; rwhi < rwhcolExp.size(); ++rwhi) {
                     try {
                        tableAliaswhere = rwhcolExp.get(rwhi).toString();
                        if (tableAliaswhere.startsWith("(") && tableAliaswhere.endsWith(")")) {
                           tableAliaswhere = StringFunctions.replaceAll("", "(", tableAliaswhere);
                           tableAliaswhere = StringFunctions.replaceAll("", ")", tableAliaswhere);
                        }

                        if (!tableAliaswhere.equalsIgnoreCase("-") && !tableAliaswhere.equalsIgnoreCase("+") && !tableAliaswhere.equalsIgnoreCase("*") && !tableAliaswhere.equalsIgnoreCase("/") && !tableAliaswhere.equalsIgnoreCase("(") && !tableAliaswhere.equalsIgnoreCase(")")) {
                           Double.parseDouble(tableAliaswhere.trim());
                           isRightNum = true;
                        }
                     } catch (NumberFormatException var27) {
                        break;
                     }
                  }
               } catch (Exception var28) {
               }

               if (lalias != null && !lalias.equals("") && (wcr.toString().trim().startsWith("'") || wcr.toString().trim().startsWith("-") || isRightNum)) {
                  for(int k = i + 1; k < whereItemList.size(); ++k) {
                     WhereItem witem1 = (WhereItem)whereItemList.get(k);
                     if (witem1.getRightJoin() != null) {
                        WhereColumn lwhcol1 = witem1.getLeftWhereExp();
                        WhereColumn rwhcol1 = witem1.getRightWhereExp();
                        fromTableAlias = this.getTableAliasName(lwhcol1);
                        tableAliaswhere = this.getTableAliasName(rwhcol1);
                        if (fromTableAlias != null && !fromTableAlias.equals("") && tableAliaswhere != null && !tableAliaswhere.equals("") && fromTableAlias.equals(lalias)) {
                           WhereItem wi = (WhereItem)whereItemList.remove(i);
                           if (k + 1 > whereItemList.size()) {
                              whereItemList.add(wi);
                           } else {
                              whereItemList.add(k + 1, wi);
                           }

                           --i;
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      for(i = 0; i < whereItemList.size(); ++i) {
         wit = (WhereItem)whereItemList.get(i);
         wcl = wit.getLeftWhereExp();
         wcr = wit.getRightWhereExp();
         new TableColumn();
         new TableColumn();
         new FromTable();
         new FromTable();
         if (wcl != null && wcl.getColumnExpression() != null && wcl.getColumnExpression().get(0) instanceof TableColumn) {
            TableColumn tcLeft = (TableColumn)wcl.getColumnExpression().get(0);
            MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)tcLeft);
         }

         if (wcr != null && wcr.getColumnExpression() != null && wcr.getColumnExpression().get(0) instanceof TableColumn) {
            TableColumn tcRight = (TableColumn)wcr.getColumnExpression().get(0);
            MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)tcRight);
         }

         if (wit.getLeftJoin() == null && wit.getRightJoin() == null) {
            whereItemList.remove(i);
            --i;
            ++operator;
         } else {
            TableColumn tc = new TableColumn();
            String tableAliaswhere;
            FunctionCalls fc;
            Vector functionObjects;
            FromTable fromTableOfWhereColumn;
            Vector colExpr;
            int k;
            CaseStatement casestmt;
            SelectColumn sc;
            int index;
            CaseStatement casestmt;
            if (wcl != null && wcl.getColumnExpression() != null) {
               if (wcl.getColumnExpression().get(0) instanceof TableColumn) {
                  tc = (TableColumn)wcl.getColumnExpression().get(0);
                  if (tc.getTableName() != null && !tc.getColumnName().trim().equals("?")) {
                     if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                        tableAliasNameL[i] = tc.getOwnerName() + "." + tc.getTableName();
                     } else {
                        tableAliasNameL[i] = tc.getTableName();
                     }
                  } else {
                     tableAliaswhere = tc.getColumnName();
                     fromTableAlias = null;
                     fromTableOfWhereColumn = MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)tc);
                     if (fromTableOfWhereColumn != null) {
                        fromTableAlias = fromTableOfWhereColumn.getAliasName();
                        if (fromTableAlias != null) {
                           tableAliasNameL[i] = fromTableAlias;
                        } else {
                           tableAliasNameL[i] = fromTableOfWhereColumn.getTableName().toString();
                        }
                     } else {
                        tableAliasNameL[i] = "";
                     }
                  }
               }

               if (wcl.getColumnExpression().get(0) instanceof FunctionCalls) {
                  fc = (FunctionCalls)wcl.getColumnExpression().get(0);
                  functionObjects = this.putObjectsInOneList(fc.getFunctionArguments());
                  if (functionObjects.size() <= 0) {
                     if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                        tableAliasNameL[i] = tc.getOwnerName() + "." + tc.getTableName();
                     } else {
                        tableAliasNameL[i] = tc.getTableName();
                     }
                  } else if (functionObjects.get(0) instanceof TableColumn) {
                     tc = (TableColumn)functionObjects.get(0);
                     if (tc.getTableName() != null && !tc.getColumnName().trim().equals("?")) {
                        if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                           tableAliasNameL[i] = tc.getOwnerName() + "." + tc.getTableName();
                        } else {
                           tableAliasNameL[i] = tc.getTableName();
                        }
                     } else {
                        tableAliaswhere = tc.getColumnName();
                        tableAliasNameL[i] = "";
                     }
                  } else if (!(functionObjects.get(0) instanceof SelectColumn)) {
                     if (functionObjects.get(0) instanceof String) {
                        tableAliaswhere = (String)functionObjects.get(0);
                        if (tableAliaswhere.indexOf(46) != -1) {
                           if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46)) {
                              if (SwisSQLOptions.removeDBSchemaQualifier) {
                                 tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                              }

                              tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                           } else {
                              tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                           }
                        } else {
                           tableAliasNameL[i] = "";
                        }
                     } else if (functionObjects.get(0) instanceof NumericClass) {
                        if (functionObjects.size() == 2) {
                           if (functionObjects.get(1) instanceof TableColumn) {
                              TableColumn tc1 = (TableColumn)functionObjects.get(1);
                              tableAliasNameL[i] = tc1.getTableName();
                           } else if (functionObjects.get(1) instanceof String) {
                              tableAliaswhere = (String)functionObjects.get(1);
                              if (tableAliaswhere.indexOf(46) != -1) {
                                 if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46)) {
                                    if (SwisSQLOptions.removeDBSchemaQualifier) {
                                       tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                                    }

                                    tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                                 } else {
                                    tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                                 }
                              } else {
                                 tableAliasNameL[i] = "";
                              }
                           } else {
                              tableAliasNameL[i] = "";
                           }
                        } else {
                           tableAliasNameL[i] = "";
                        }
                     }
                  } else {
                     sc = (SelectColumn)functionObjects.get(0);
                     if (sc.getColumnExpression() != null) {
                        for(index = 0; index < sc.getColumnExpression().size(); ++index) {
                           if (sc.getColumnExpression().get(index) instanceof TableColumn) {
                              tc = (TableColumn)sc.getColumnExpression().get(index);
                              if (tc.getTableName() != null && !tc.getColumnName().trim().equals("?")) {
                                 if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                                    tableAliasNameL[i] = tc.getOwnerName() + "." + tc.getTableName();
                                 } else {
                                    tableAliasNameL[i] = tc.getTableName();
                                 }
                              } else {
                                 tableAliaswhere = tc.getColumnName();
                                 tableAliasNameL[i] = "";
                              }
                           }
                        }
                     }
                  }
               }

               if (wcl.getColumnExpression().get(0) instanceof SelectColumn) {
                  colExpr = ((SelectColumn)wcl.getColumnExpression().get(0)).getColumnExpression();

                  for(k = 0; k < colExpr.size(); ++k) {
                     if (colExpr.get(k) instanceof TableColumn) {
                        tc = (TableColumn)colExpr.get(k);
                        if (tc.getTableName() != null && !tc.getColumnName().trim().equals("?")) {
                           if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                              tableAliasNameL[i] = tc.getOwnerName() + "." + tc.getTableName();
                              break;
                           }

                           tableAliasNameL[i] = tc.getTableName();
                           break;
                        }

                        tableAliaswhere = tc.getColumnName();
                        tableAliasNameL[i] = "";
                        break;
                     }

                     if (colExpr.get(k) instanceof String) {
                        tableAliaswhere = (String)colExpr.get(k);
                        if (tableAliaswhere.indexOf(46) != -1) {
                           if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46)) {
                              if (SwisSQLOptions.removeDBSchemaQualifier) {
                                 tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                              }

                              tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                           } else {
                              tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                           }
                        } else {
                           tableAliasNameL[i] = "";
                        }
                     }

                     if (colExpr.get(k) instanceof CaseStatement) {
                        casestmt = (CaseStatement)colExpr.get(k);
                        tableAliasNameL[i] = this.getTableAliasName(casestmt);
                     }
                  }
               }

               if (wcl.getColumnExpression().get(0) instanceof CaseStatement) {
                  casestmt = (CaseStatement)wcl.getColumnExpression().get(0);
                  tableAliasNameL[i] = this.getTableAliasName(casestmt);
               }

               if (wcl.getColumnExpression().get(0) instanceof String) {
                  tableAliaswhere = (String)wcl.getColumnExpression().get(0);
                  if (tableAliaswhere.indexOf(46) != -1) {
                     if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46)) {
                        if (SwisSQLOptions.removeDBSchemaQualifier) {
                           tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                        }

                        tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                     } else {
                        tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                     }
                  } else {
                     tableAliasNameL[i] = "";
                  }
               }
            } else {
               tableAliasNameL[i] = "";
            }

            if (wcr != null && wcr.getColumnExpression() != null) {
               if (wcr.getColumnExpression().get(0) instanceof SelectColumn) {
                  colExpr = ((SelectColumn)wcr.getColumnExpression().get(0)).getColumnExpression();

                  for(k = 0; k < colExpr.size(); ++k) {
                     if (colExpr.get(k) instanceof TableColumn) {
                        tc = (TableColumn)colExpr.get(k);
                        if (tc.getTableName() != null && !tc.getColumnName().trim().equals("?")) {
                           if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                              tableAliasNameR[i] = tc.getOwnerName() + "." + tc.getTableName();
                              break;
                           }

                           tableAliasNameR[i] = tc.getTableName();
                           break;
                        }

                        tableAliaswhere = tc.getColumnName();
                        tableAliasNameR[i] = "";
                        break;
                     }

                     if (colExpr.get(k) instanceof String) {
                        tableAliaswhere = (String)colExpr.get(k);
                        if (tableAliaswhere.indexOf(46) != -1) {
                           if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46)) {
                              if (SwisSQLOptions.removeDBSchemaQualifier) {
                                 tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                              }

                              tableAliasNameR[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                           } else {
                              tableAliasNameR[i] = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                           }
                        } else {
                           tableAliasNameR[i] = "";
                        }
                     }

                     if (colExpr.get(k) instanceof CaseStatement) {
                        casestmt = (CaseStatement)colExpr.get(k);
                        tableAliasNameR[i] = this.getTableAliasName(casestmt);
                     }
                  }
               }

               if (wcr.getColumnExpression().get(0) instanceof TableColumn) {
                  tc = (TableColumn)wcr.getColumnExpression().get(0);
                  if (tc.getTableName() != null && !tc.getColumnName().trim().equals("?")) {
                     if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                        tableAliasNameR[i] = tc.getOwnerName() + "." + tc.getTableName();
                     } else {
                        tableAliasNameR[i] = tc.getTableName();
                     }
                  } else {
                     tableAliaswhere = tc.getColumnName();
                     fromTableAlias = null;
                     fromTableOfWhereColumn = MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)tc);
                     if (fromTableOfWhereColumn != null) {
                        fromTableAlias = fromTableOfWhereColumn.getAliasName();
                        if (fromTableAlias != null) {
                           tableAliasNameR[i] = fromTableAlias;
                        } else {
                           tableAliasNameR[i] = fromTableOfWhereColumn.getTableName().toString();
                        }
                     } else {
                        tableAliasNameR[i] = "";
                     }
                  }
               }

               if (wcr.getColumnExpression().get(0) instanceof FunctionCalls) {
                  fc = (FunctionCalls)wcr.getColumnExpression().get(0);
                  functionObjects = this.putObjectsInOneList(fc.getFunctionArguments());
                  if (functionObjects.size() <= 0) {
                     if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                        tableAliasNameR[i] = tc.getOwnerName() + "." + tc.getTableName();
                     } else {
                        tableAliasNameR[i] = tc.getTableName();
                     }
                  } else if (functionObjects.get(0) instanceof TableColumn) {
                     tc = (TableColumn)functionObjects.get(0);
                     if (tc.getTableName() != null && !tc.getColumnName().trim().equals("?")) {
                        if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                           tableAliasNameR[i] = tc.getOwnerName() + "." + tc.getTableName();
                        } else {
                           tableAliasNameR[i] = tc.getTableName();
                        }
                     } else {
                        tableAliaswhere = tc.getColumnName();
                        tableAliasNameR[i] = "";
                     }
                  } else if (!(functionObjects.get(0) instanceof SelectColumn)) {
                     if (functionObjects.get(0) instanceof String) {
                        tableAliaswhere = (String)functionObjects.get(0);
                        if (tableAliaswhere.indexOf(46) != -1) {
                           if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46)) {
                              if (SwisSQLOptions.removeDBSchemaQualifier) {
                                 tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                              }

                              tableAliasNameR[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                           } else {
                              tableAliasNameR[i] = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                           }
                        } else {
                           tableAliasNameR[i] = "";
                        }
                     }
                  } else {
                     sc = (SelectColumn)functionObjects.get(0);
                     if (sc.getColumnExpression() != null) {
                        for(index = 0; index < sc.getColumnExpression().size(); ++index) {
                           if (sc.getColumnExpression().get(index) instanceof TableColumn) {
                              tc = (TableColumn)sc.getColumnExpression().get(index);
                              if (tc.getTableName() == null && tc.getColumnName().trim().equals("?")) {
                                 tableAliaswhere = tc.getColumnName();
                                 tableAliasNameR[i] = "";
                              } else if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                                 tableAliasNameR[i] = tc.getOwnerName() + "." + tc.getTableName();
                              } else {
                                 tableAliasNameR[i] = tc.getTableName();
                              }
                           }
                        }
                     }
                  }
               }

               if (wcr.getColumnExpression().get(0) instanceof CaseStatement) {
                  casestmt = (CaseStatement)wcr.getColumnExpression().get(0);
                  tableAliasNameR[i] = this.getTableAliasName(casestmt);
               }

               if (wcr.getColumnExpression().get(0) instanceof String) {
                  tableAliaswhere = (String)wcr.getColumnExpression().get(0);
                  if (tableAliaswhere.indexOf(46) != -1) {
                     if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46)) {
                        if (SwisSQLOptions.removeDBSchemaQualifier) {
                           tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                        }

                        tableAliasNameR[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                     } else {
                        tableAliasNameR[i] = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                     }
                  } else {
                     tableAliasNameR[i] = "";
                  }
               }
            } else {
               tableAliasNameR[i] = "";
            }

            if ((database == 12 || database == 8) && (tableAliasNameL[i] != null && tableAliasNameL[i].equalsIgnoreCase("") || tableAliasNameR[i] != null && tableAliasNameR[i].equalsIgnoreCase("")) && (wit.getLeftJoin() != null || wit.getRightJoin() != null) && this.isColumnNameExistsInWhereColumn(wit.getRightWhereExp().getColumnExpression().get(0))) {
               tableAliaswhere = "SwisSQL Message : Tablename is not provided for one of the Columns in the Where Condition";
               tableAliaswhere = tableAliaswhere + " : " + wit.toString() + " \n";
               tableAliaswhere = tableAliaswhere + " Please provide the Tablename in the original query and then convert.";
               throw new ConvertException(tableAliaswhere);
            }

            boolean added = false;

            for(k = 0; k < wiTables.size(); ++k) {
               if (tableAliasNameL[i] != null && tableAliasNameR[i] != null) {
                  tableAliaswhere = (String)wiTables.get(k);
                  if (tableAliaswhere.equals(tableAliasNameL[i] + "-" + tableAliasNameR[i])) {
                     wiTables.add(tableAliasNameL[i] + "-" + tableAliasNameR[i]);
                     added = true;
                     break;
                  }

                  if (tableAliaswhere.equals(tableAliasNameR[i] + "-" + tableAliasNameL[i])) {
                     wiTables.add(tableAliasNameR[i] + "-" + tableAliasNameL[i]);
                     added = true;
                     break;
                  }
               }
            }

            if (!added && tableAliasNameL[i] != null && tableAliasNameR[i] != null) {
               wiTables.add(tableAliasNameL[i] + "-" + tableAliasNameR[i]);
            }
         }
      }

      for(i = 0; i < operator; ++i) {
         operators.add(" AND ");
      }

      boolean isSame = false;
      int loopCount = wiTables.size();

      for(int i = 0; i < loopCount; ++i) {
         whereExpressions = new WhereExpression();
         joinExpression[i] = new Vector();
         WhereItem joinWhereItem = (WhereItem)whereItemList.get(i);
         whereExpressions.addWhereItem(joinWhereItem.convert(to_sqs, from_sqs, database));
         int[] indexArray = new int[whereItemList.size()];
         int index = 0;

         int j;
         for(j = i + 1; j < loopCount; ++j) {
            if (wiTables.get(i).equals(wiTables.get(j))) {
               indexArray[index++] = j;
               isSame = true;
            } else {
               ithElement = (String)wiTables.get(i);
               String jthElement = (String)wiTables.get(j);
               if (jthElement.endsWith("-")) {
                  jthElement = jthElement.substring(0, jthElement.length() - 1);
               } else if (jthElement.startsWith("-")) {
                  jthElement = jthElement.substring(1);
               }

               ithElement = "&" + ithElement.replaceAll("-", "&-&") + "&";
               jthElement = "&" + jthElement.replaceAll("-", "&-&") + "&";
               if (ithElement.indexOf("-" + jthElement) != -1 || ithElement.indexOf(jthElement + "-") != -1) {
                  indexArray[index++] = j;
                  isSame = true;
               }
            }
         }

         if (isSame) {
            for(j = 0; j < indexArray.length && indexArray[j] != 0; ++j) {
               whereExpressions.addOperator("AND");
               WhereItem wi = (WhereItem)whereItemList.remove(indexArray[j] - j);
               wiTables.remove(indexArray[j] - j);
               whereExpressions.addWhereItem(wi.convert(to_sqs, from_sqs, database));
               --loopCount;
            }

            isSame = false;
         }

         joinExpression[i].add(whereExpressions);
      }

   }

   public void orderWhereItems(Vector whereItemList, Vector[] joinExpression) {
      int size = whereItemList.size();

      for(int i = 0; i < size; ++i) {
         WhereItem wi = (WhereItem)whereItemList.elementAt(i);
         String leftTable = this.getTableAliasWhereL(wi);
         String rightTable = this.getTableAliasWhereR(wi);
         int position = i + 1;

         for(int j = i + 1; j < size; ++j) {
            WhereItem nextWI = (WhereItem)whereItemList.elementAt(j);
            String nextLeftTable = this.getTableAliasWhereL(nextWI);
            String nextRightTable = this.getTableAliasWhereR(nextWI);
            if (leftTable != null && nextLeftTable != null && CustomizeUtil.compareQuotedIdentifiers(leftTable, nextLeftTable, true) || leftTable != null && nextRightTable != null && CustomizeUtil.compareQuotedIdentifiers(leftTable, nextRightTable, true) || rightTable != null && nextLeftTable != null && CustomizeUtil.compareQuotedIdentifiers(rightTable, nextLeftTable, true) || rightTable != null && nextRightTable != null && CustomizeUtil.compareQuotedIdentifiers(rightTable, nextRightTable, true)) {
               whereItemList.insertElementAt(whereItemList.remove(j), position);
               Vector v = joinExpression[position];

               for(int k = position; k < j; ++k) {
                  Vector vNext = joinExpression[k + 1];
                  joinExpression[k + 1] = v;
                  v = vNext;
               }

               joinExpression[position] = v;
               ++position;
            }
         }
      }

   }

   private void decrement(int[] indexArray) {
      for(int i = 0; i < indexArray.length && indexArray[i] != 0; ++i) {
         int var10002 = indexArray[i]--;
      }

   }

   private boolean isColumnNameExistsInWhereColumn(Object obj) {
      Vector vc;
      int i;
      int size;
      if (obj instanceof SelectColumn) {
         SelectColumn sc = (SelectColumn)obj;
         vc = sc.getColumnExpression();
         i = 0;

         for(size = vc.size(); i < size; ++i) {
            if (this.isColumnNameExistsInWhereColumn(vc.get(i))) {
               return true;
            }
         }

         return false;
      } else if (obj instanceof FunctionCalls) {
         FunctionCalls fc = (FunctionCalls)obj;
         vc = fc.getFunctionArguments();
         i = 0;

         for(size = vc.size(); i < size; ++i) {
            if (this.isColumnNameExistsInWhereColumn(vc.get(i))) {
               return true;
            }
         }

         return false;
      } else if (obj instanceof TableColumn) {
         return this.isColumnNameExistsInWhereColumn(((TableColumn)obj).getColumnName());
      } else if (obj instanceof String) {
         String str = obj.toString().trim();
         return !str.startsWith("'") && !this.isNumber(str) && !this.isOperator(str) && !this.isKeyword(str, 12) && !this.isKeyword(str, 8);
      } else {
         return true;
      }
   }

   private boolean isKeyword(String str, int database) {
      String[] keyList = SwisSQLUtils.getKeywords(database);
      if (keyList == null) {
         return false;
      } else {
         int i = 0;

         for(int size = keyList.length; i < size; ++i) {
            if (str.equalsIgnoreCase(keyList[i])) {
               return true;
            }
         }

         if (!str.equalsIgnoreCase("DUAL") && !str.equalsIgnoreCase("SYS.DUAL") && !str.equalsIgnoreCase("DATE") && !str.equalsIgnoreCase("CURRENT DATE") && !str.equalsIgnoreCase("\"rownum\"") && !str.equalsIgnoreCase("rownum") && !str.equalsIgnoreCase("SYSDATE") && !str.equalsIgnoreCase("SYS_GUID") && !str.equalsIgnoreCase("TIME") && !str.equalsIgnoreCase("CURRENT TIME") && !str.equalsIgnoreCase("USER") && !str.equalsIgnoreCase("SYSDATE") && !str.equalsIgnoreCase("TIMESTAMP") && !str.equalsIgnoreCase("SYSTIMESTAMP") && !str.equalsIgnoreCase("CURRENT TIMESTAMP") && !str.equalsIgnoreCase("CURRENT") && !str.equalsIgnoreCase("SYSTEM_USER")) {
            return false;
         } else {
            return true;
         }
      }
   }

   private boolean isOperator(String str) {
      return str.equalsIgnoreCase("+") || str.equalsIgnoreCase("-") || str.equalsIgnoreCase("*") || str.equalsIgnoreCase("/");
   }

   private boolean isNumber(String str) {
      try {
         Integer.parseInt(str.substring(0, 1));
         return true;
      } catch (NumberFormatException var3) {
         return false;
      }
   }

   public Vector arrangeTheWhereItemListAccordingToTheOrderInFromItemList(Vector whereItemList, Vector fromItemList) {
      Vector arrangeWhereItemList = new Vector();
      Vector originalWhereItemList = whereItemList;
      Vector originalFromItemList = fromItemList;

      int i;
      for(i = 0; i < originalFromItemList.size(); ++i) {
         FromTable ft1 = null;
         if (originalFromItemList.elementAt(i) instanceof FromTable) {
            ft1 = (FromTable)originalFromItemList.get(i);
         } else if (originalFromItemList.elementAt(i) instanceof FromClause) {
            Vector newFromItemVector = ((FromClause)originalFromItemList.get(i)).getFromItemList();

            for(int index = 0; index < newFromItemVector.size(); ++index) {
               if (newFromItemVector.elementAt(index) instanceof FromTable) {
                  ft1 = (FromTable)newFromItemVector.get(index);
               } else {
                  this.arrangeTheWhereItemListAccordingToTheOrderInFromItemList(whereItemList, newFromItemVector);
               }
            }
         }

         for(int count = i + 1; count < originalFromItemList.size(); ++count) {
            FromTable ft2 = null;
            if (originalFromItemList.elementAt(count) instanceof FromTable) {
               ft2 = (FromTable)originalFromItemList.get(count);
            } else if (originalFromItemList.elementAt(count) instanceof FromClause) {
               Vector newFromItemVector = ((FromClause)originalFromItemList.get(count)).getFromItemList();

               for(int index = 0; index < newFromItemVector.size(); ++index) {
                  if (newFromItemVector.elementAt(index) instanceof FromTable) {
                     ft2 = (FromTable)newFromItemVector.get(index);
                  } else {
                     this.arrangeTheWhereItemListAccordingToTheOrderInFromItemList(whereItemList, newFromItemVector);
                  }
               }
            }

            this.orderTheWhereItemAccordingToTheFromItem(ft1, ft2, arrangeWhereItemList, originalWhereItemList);
         }
      }

      for(i = 0; i < originalWhereItemList.size(); ++i) {
         arrangeWhereItemList.add(originalWhereItemList.get(i));
      }

      return arrangeWhereItemList;
   }

   public void orderTheWhereItemAccordingToTheFromItem(FromTable ft1, FromTable ft2, Vector arrangeWhereItemList, Vector originalWhereItemList) {
      int currentArrangedSize = arrangeWhereItemList.size();

      for(int i = 0; i < originalWhereItemList.size(); ++i) {
         WhereItem wi = (WhereItem)originalWhereItemList.elementAt(i);
         String leftTable = this.getTableAliasWhereL(wi);
         String rightTable = this.getTableAliasWhereR(wi);
         String fromTableAliasName1 = ft1.getAliasName();
         String fromTableAliasName2 = ft2.getAliasName();
         if (fromTableAliasName1 == null) {
            fromTableAliasName1 = ft1.getTableName().toString();
         }

         if (fromTableAliasName2 == null) {
            fromTableAliasName2 = ft2.getTableName().toString();
         }

         if (fromTableAliasName1.equalsIgnoreCase(leftTable) && fromTableAliasName2.equalsIgnoreCase(rightTable)) {
            if (arrangeWhereItemList.size() > currentArrangedSize) {
               arrangeWhereItemList.insertElementAt(wi, currentArrangedSize);
            } else {
               arrangeWhereItemList.add(wi);
            }

            originalWhereItemList.remove(i);
            --i;
         } else if (fromTableAliasName1.equalsIgnoreCase(rightTable) && fromTableAliasName2.equalsIgnoreCase(leftTable)) {
            WhereColumn newLeftWhereExp = wi.getLeftWhereExp();
            WhereColumn newRightWhereExp = wi.getRightWhereExp();
            String leftJoin = wi.getLeftJoin();
            String rightJoin = wi.getRightJoin();
            wi.setLeftJoin(rightJoin);
            wi.setRightJoin(leftJoin);
            wi.setLeftWhereExp(newRightWhereExp);
            wi.setRightWhereExp(newLeftWhereExp);
            if (arrangeWhereItemList.size() > currentArrangedSize) {
               arrangeWhereItemList.insertElementAt(wi, currentArrangedSize);
            } else {
               arrangeWhereItemList.add(wi);
            }

            originalWhereItemList.remove(i);
            --i;
         } else if (leftTable == null && fromTableAliasName1.equalsIgnoreCase(rightTable)) {
            arrangeWhereItemList.add(wi);
            originalWhereItemList.remove(i);
            --i;
         } else if (leftTable != null && leftTable.trim().equals("") && fromTableAliasName1.equalsIgnoreCase(rightTable)) {
            arrangeWhereItemList.add(wi);
            originalWhereItemList.remove(i);
            --i;
         } else if (rightTable == null && fromTableAliasName1.equalsIgnoreCase(leftTable)) {
            arrangeWhereItemList.add(wi);
            originalWhereItemList.remove(i);
            --i;
         } else if (rightTable != null && rightTable.trim().equals("") && fromTableAliasName1.equalsIgnoreCase(leftTable)) {
            arrangeWhereItemList.add(wi);
            originalWhereItemList.remove(i);
            --i;
         }
      }

   }

   private void addFetchClause(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (to_sqs != null) {
         FetchClause fetch_clause = new FetchClause();
         String rownumValue = "0";
         if (this.rownumClause.getRownumValue() instanceof SelectQueryStatement) {
            throw new ConvertException("Conversion failure.. Subquery can't be converted");
         } else {
            Vector scItem;
            int i;
            if (this.rownumClause.getRownumValue() instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.rownumClause.getRownumValue();
               scItem = sc.getColumnExpression();

               for(i = 0; i < scItem.size(); ++i) {
                  if (scItem.elementAt(i) instanceof FunctionCalls) {
                     throw new ConvertException("Conversion failure.. Function calls can't be converted");
                  }

                  if (scItem.elementAt(i) instanceof TableColumn) {
                     if (!this.rownumClause.getOperator().equals("<=") && !this.rownumClause.getOperator().equals("=")) {
                        fetch_clause.setFetchCountVariable(((TableColumn)scItem.elementAt(i)).toString() + " - 1");
                     } else {
                        fetch_clause.setFetchCountVariable(((TableColumn)scItem.elementAt(i)).toString());
                     }
                  } else {
                     if (!(scItem.elementAt(i) instanceof String) || scItem.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expression can't be converted");
                     }

                     rownumValue = (String)scItem.elementAt(i);
                  }
               }
            }

            fetch_clause.setFetchFirstClause("FETCH FIRST");
            fetch_clause.setRowOnlyClause("ROWS ONLY");
            if (!this.rownumClause.getOperator().equals("<=") && !this.rownumClause.getOperator().equals("=")) {
               fetch_clause.setFetchCount(Integer.parseInt(rownumValue) - 1 + "");
            } else {
               fetch_clause.setFetchCount(rownumValue);
            }

            if (to_sqs.getFetchClause() != null) {
               throw new ConvertException();
            } else {
               to_sqs.setFetchClause(fetch_clause);
               SelectStatement ss = to_sqs.getSelectStatement();
               if (ss != null) {
                  scItem = ss.getSelectItemList();
                  if (scItem != null) {
                     for(i = 0; i < scItem.size(); ++i) {
                        if (scItem.get(i) instanceof SelectColumn) {
                           SelectColumn sc = (SelectColumn)scItem.get(i);
                           if (sc != null) {
                              Vector cols = sc.getColumnExpression();
                              if (cols != null) {
                                 for(int j = 0; j < cols.size(); ++j) {
                                    if (cols.get(j) instanceof TableColumn) {
                                       TableColumn tc = (TableColumn)cols.get(j);
                                       if (tc != null) {
                                          String colName = tc.getColumnName();
                                          if (colName != null && colName.toLowerCase().equals("rownum")) {
                                             tc.setColumnName("ROW_NUMBER() OVER()");
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

               to_sqs.setLimitClause((LimitClause)null);
            }
         }
      }
   }

   private void addLimitClause(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause limitClause = new LimitClause();
      String rownumValue = new String("0");
      if (this.rownumClause.getRownumValue() instanceof SelectQueryStatement) {
         throw new ConvertException(" SubQuery are not allowed in Limit clause");
      } else {
         if (this.rownumClause.getRownumValue() instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.rownumClause.getRownumValue();
            Vector colExp = sc.getColumnExpression();

            for(int i = 0; i < colExp.size(); ++i) {
               if (colExp.elementAt(i) instanceof FunctionCalls) {
                  throw new ConvertException(" Function calls are not allowed in Limit clause");
               }

               if (colExp.elementAt(i) instanceof TableColumn) {
                  throw new ConvertException(" Identifier is not allowed in Limit clause");
               }

               if (!(colExp.elementAt(i) instanceof String) || colExp.size() != 1) {
                  throw new ConvertException(" Expression are not allowed in Limit clause");
               }

               rownumValue = (String)colExp.elementAt(i);
            }
         }

         limitClause.setLimitClause("LIMIT");
         if (this.rownumClause.getOperator().equals("<=")) {
            limitClause.setLimitValue(rownumValue);
         } else {
            limitClause.setLimitValue(Integer.parseInt(rownumValue) - 1 + "");
         }

         if (to_sqs.getLimitClause() != null) {
            throw new ConvertException();
         } else {
            to_sqs.setLimitClause(limitClause);
            to_sqs.setFetchClause((FetchClause)null);
         }
      }
   }

   private boolean isMetaDataRequired(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      new WhereExpression();
      FromClause fromClause = to_sqs.getFromClause();
      if (fromClause != null) {
         FromClause newFromClause = new FromClause();
         Vector newFromItemList = new Vector();
         newFromClause.setFromClause("FROM");
         newFromClause.setFromItemList(newFromItemList);
         Vector fromItemList = fromClause.getFromItemList();
         Vector whereItemList = new Vector();
         Vector operatorList = new Vector();
         Vector[] joinExpression = new Vector[fromItemList.size()];
         this.loadWhereItemsOperators(whereItemList, operatorList);
         String[] tableAliasNameL = new String[whereItemList.size()];
         String[] tableAliasNameR = new String[whereItemList.size()];
         Vector wiTables = new Vector();
         Vector whereExpressions = new Vector();
         new Vector();
         int operator = -1;

         for(int i = 0; i < whereItemList.size(); ++i) {
            new TableColumn();
            WhereItem wit = (WhereItem)whereItemList.get(i);
            WhereColumn wcl = wit.getLeftWhereExp();
            WhereColumn wcr = wit.getRightWhereExp();

            try {
               if (wit.getLeftJoin() != null || wit.getRightJoin() != null) {
                  TableColumn tc;
                  String tableAliaswhere;
                  if (wcl.getColumnExpression() != null) {
                     if (wcl.getColumnExpression().get(0) instanceof TableColumn) {
                        tc = (TableColumn)wcl.getColumnExpression().get(0);
                        if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                           tableAliasNameL[i] = tc.getOwnerName() + "." + tc.getTableName();
                        } else {
                           tableAliasNameL[i] = tc.getTableName();
                        }
                     }

                     if (wcl.getColumnExpression().get(0) instanceof String) {
                        tableAliaswhere = (String)wcl.getColumnExpression().get(0);
                        if (!tableAliaswhere.startsWith("'")) {
                           try {
                              Integer.parseInt(tableAliaswhere);
                           } catch (NumberFormatException var26) {
                              tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                           }
                        }
                     }
                  }

                  if (wcr.getColumnExpression() != null) {
                     if (wcr.getColumnExpression().get(0) instanceof TableColumn) {
                        tc = (TableColumn)wcr.getColumnExpression().get(0);
                        if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                           tableAliasNameR[i] = tc.getOwnerName() + "." + tc.getTableName();
                        } else {
                           tableAliasNameR[i] = tc.getTableName();
                        }
                     }

                     if (wcr.getColumnExpression().get(0) instanceof String) {
                        tableAliaswhere = (String)wcr.getColumnExpression().get(0);
                        if (!tableAliaswhere.startsWith("'")) {
                           try {
                              Integer.parseInt(tableAliaswhere);
                           } catch (NumberFormatException var25) {
                              tableAliasNameR[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                           }
                        }
                     }
                  }

                  wiTables.add(tableAliasNameL[i] + tableAliasNameR[i]);
                  return false;
               }

               whereExpressions.add(wit);
               whereItemList.remove(i);
               --i;
               ++operator;
            } catch (Exception var27) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean isWhereItemInClauseIsMultipleAndNotSubquery(WhereItem whereItem) {
      boolean isWhereItemNormal = false;
      String whereItemOperator = whereItem.getOperator();
      SelectQueryStatement rightWhereSubquery = whereItem.getRightWhereSubQuery();
      WhereColumn rightWhereExpression = whereItem.getRightWhereExp();
      if (whereItemOperator != null && (whereItemOperator.equalsIgnoreCase("IN") || whereItemOperator.equalsIgnoreCase("NOT IN")) && rightWhereExpression != null && rightWhereSubquery == null) {
         WhereColumn leftWhereColumn = whereItem.getLeftWhereExp();
         if (leftWhereColumn.getColumnExpression() != null && leftWhereColumn.getColumnExpression().size() != 1) {
            Vector colExpr = leftWhereColumn.getColumnExpression();

            for(int i = 0; i < colExpr.size(); ++i) {
               if (colExpr.get(i).toString().trim().equals(",")) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean isWhereItemEqualsClauseIsMultipleAndNotSubquery(WhereItem whereItem) {
      boolean isWhereItemNormal = false;
      String whereItemOperator = whereItem.getOperator2();
      SelectQueryStatement rightWhereSubquery = whereItem.getRightWhereSubQuery();
      WhereColumn rightWhereExpression = whereItem.getRightWhereExp();
      return whereItemOperator != null && (whereItemOperator.equalsIgnoreCase("ALL") || whereItemOperator.equalsIgnoreCase("ANY") || whereItemOperator.equalsIgnoreCase("SOME")) && rightWhereExpression != null && rightWhereSubquery == null;
   }

   private void setNewFromClauseWithInformixJoin(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, int database) throws ConvertException {
      if (from_sqs != null) {
         FromClause fromClause = to_sqs.getFromClause();
         if (fromClause != null) {
            FromClause newFromClause = new FromClause();
            Vector newFromItemList = new Vector();
            newFromClause.setFromClause("FROM");
            newFromClause.setFromItemList(newFromItemList);
            Vector fromItemList = fromClause.getFromItemList();
            Vector whereItemList = new Vector();
            Vector operatorList = new Vector();
            Vector[] joinExpression = new Vector[fromItemList.size()];
            this.loadWhereItemsOperators(whereItemList, operatorList);
            if (this.isMetaDataRequired(to_sqs, from_sqs)) {
               new String();
               String exceptionString = "The given query involves theta join to INFORMIX join.\nIt needs metadata for conversion";
               throw new ConvertException(exceptionString);
            }

            this.groupWhereItems(whereItemList, joinExpression, to_sqs, from_sqs, database);
            int index = false;
            boolean fromChanged = false;
            Vector outerJoinTableItems = new Vector();

            for(int i = 0; i < whereItemList.size(); ++i) {
               WhereItem wit = (WhereItem)whereItemList.get(i);
               if (wit.getLeftJoin() != null) {
                  if (!outerJoinTableItems.contains(this.getTableAliasWhereR(wit))) {
                     outerJoinTableItems.add(this.getTableAliasWhereR(wit));
                  }
               } else if (wit.getRightJoin() != null && !outerJoinTableItems.contains(this.getTableAliasWhereL(wit))) {
                  outerJoinTableItems.add(this.getTableAliasWhereL(wit));
               }

               this.getTableAliasWhereL(wit);
            }

            Vector newFromItemListWithOuter = new Vector();
            StringBuffer outerTableNames = new StringBuffer();
            boolean isOuterSet = false;

            for(int i = 0; i < fromItemList.size(); ++i) {
               FromTable fromTable = null;
               if (fromItemList.get(i) instanceof FromTable) {
                  fromTable = (FromTable)fromItemList.get(i);
               }

               if (outerJoinTableItems == null || fromTable == null || !outerJoinTableItems.contains(fromTable.getAliasName()) && !outerJoinTableItems.contains(fromTable.getTableName().toString())) {
                  newFromItemListWithOuter.add(fromTable);
               } else {
                  if (!isOuterSet) {
                     outerTableNames.append("OUTER (");
                  }

                  fromChanged = true;

                  for(int j = 0; j < outerJoinTableItems.size(); ++j) {
                     if (fromTable.getAliasName() != null && fromTable.getAliasName().equalsIgnoreCase((String)outerJoinTableItems.get(j))) {
                        if (isOuterSet) {
                           outerTableNames.append(", ");
                        } else {
                           isOuterSet = true;
                        }

                        outerTableNames.append(fromTable.getTableName().toString() + " ");
                        outerTableNames.append(fromTable.getAliasName());
                     } else if (((String)outerJoinTableItems.get(j)).equalsIgnoreCase(fromTable.getTableName().toString())) {
                        if (isOuterSet) {
                           outerTableNames.append(", ");
                        } else {
                           isOuterSet = true;
                        }

                        outerTableNames.append(fromTable.getTableName().toString() + " ");
                     }
                  }
               }
            }

            if (isOuterSet) {
               outerTableNames.append(") ");
               FromTable outerFromTable = new FromTable();
               outerFromTable.setTableName(outerTableNames.toString());
               newFromItemListWithOuter.insertElementAt(outerFromTable, 0);
            }

            newFromClause.setFromItemList(newFromItemListWithOuter);
            if (fromChanged) {
               to_sqs.setFromClause(newFromClause);
            }
         }

      }
   }

   private void moveOuterWhereItemsAsANSIJoins(Hashtable whereItemList, Vector whereItemsToBeMovedToFromClause, Vector outerTableNames) {
      int getsize = whereItemsToBeMovedToFromClause.size();
      int count = 0;
      int j;
      if (outerTableNames != null) {
         for(j = 0; j < getsize; ++j) {
            WhereItem whereItem = (WhereItem)whereItemsToBeMovedToFromClause.get(j - count);
            WhereColumn leftWhereExp = whereItem.getLeftWhereExp();
            WhereColumn rightWhereExp = whereItem.getRightWhereExp();
            String tableOrAliasNameForLeftExp = new String("");
            String tableOrAliasNameForRightExp = new String("");
            String obj;
            if (leftWhereExp != null && leftWhereExp.getColumnExpression() != null && rightWhereExp != null && rightWhereExp.getColumnExpression() != null) {
               TableColumn tableColumn;
               if (leftWhereExp.getColumnExpression().get(0) instanceof TableColumn) {
                  new TableColumn();
                  tableColumn = (TableColumn)leftWhereExp.getColumnExpression().get(0);
                  if (tableColumn.getOwnerName() != null) {
                     tableOrAliasNameForLeftExp = tableColumn.getOwnerName() + "." + tableColumn.getTableName();
                  } else {
                     tableOrAliasNameForLeftExp = tableColumn.getTableName();
                  }
               }

               if (leftWhereExp.getColumnExpression().get(0) instanceof String) {
                  obj = (String)leftWhereExp.getColumnExpression().get(0);
                  if (obj.indexOf(".") != -1) {
                     tableOrAliasNameForLeftExp = obj.substring(0, obj.lastIndexOf("."));
                  }
               }

               if (rightWhereExp.getColumnExpression().get(0) instanceof TableColumn) {
                  new TableColumn();
                  tableColumn = (TableColumn)rightWhereExp.getColumnExpression().get(0);
                  if (tableColumn.getOwnerName() != null) {
                     tableOrAliasNameForRightExp = tableColumn.getOwnerName() + "." + tableColumn.getTableName();
                  } else {
                     tableOrAliasNameForRightExp = tableColumn.getTableName();
                  }
               }

               if (rightWhereExp.getColumnExpression().get(0) instanceof String) {
                  obj = (String)rightWhereExp.getColumnExpression().get(0);
                  if (obj.indexOf(".") != -1) {
                     tableOrAliasNameForRightExp = obj.substring(0, obj.lastIndexOf("."));
                  }
               }

               if (!outerTableNames.contains(tableOrAliasNameForRightExp) && !outerTableNames.contains(tableOrAliasNameForLeftExp)) {
                  obj = "" + j;
                  whereItemList.put(obj, whereItemsToBeMovedToFromClause.get(j - count));
                  whereItemsToBeMovedToFromClause.removeElementAt(j - count);
                  ++count;
               }
            } else {
               obj = "" + j;
               whereItemList.put(obj, whereItemsToBeMovedToFromClause.get(j - count));
               whereItemsToBeMovedToFromClause.removeElementAt(j - count);
               ++count;
            }
         }
      } else {
         for(j = 0; j < getsize; ++j) {
            String obj = "" + j;
            whereItemList.put(obj, whereItemsToBeMovedToFromClause.get(j - count));
            whereItemsToBeMovedToFromClause.removeElementAt(j - count);
            ++count;
         }
      }

   }

   private Vector putObjectsInOneList(Vector columnList) {
      Vector returnObjects = new Vector();
      if (columnList != null) {
         for(int i = 0; i < columnList.size(); ++i) {
            Vector functionsList;
            if (columnList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)columnList.get(i);
               functionsList = this.putObjectsInOneList(sc.getColumnExpression());
               returnObjects.addAll(functionsList);
            } else if (columnList.get(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)columnList.get(i);
               functionsList = this.putObjectsInOneList(fc.getFunctionArguments());
               returnObjects.addAll(functionsList);
            } else {
               returnObjects.add(columnList.get(i));
            }
         }
      }

      return returnObjects;
   }

   private String getTableAliasName(WhereColumn wc) {
      TableColumn tc = new TableColumn();
      String aliasname = "";
      if (wc != null && wc.getColumnExpression() != null) {
         String tableAliaswhere;
         if (wc.getColumnExpression().get(0) instanceof TableColumn) {
            tc = (TableColumn)wc.getColumnExpression().get(0);
            if (tc.getTableName() == null && tc.getColumnName().trim().equals("?")) {
               tableAliaswhere = tc.getColumnName();
               aliasname = "";
            } else if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
               aliasname = tc.getOwnerName() + "." + tc.getTableName();
            } else {
               aliasname = tc.getTableName();
            }
         }

         if (wc.getColumnExpression().get(0) instanceof FunctionCalls) {
            FunctionCalls fc = (FunctionCalls)wc.getColumnExpression().get(0);
            Vector functionObjects = this.putObjectsInOneList(fc.getFunctionArguments());
            if (functionObjects.size() <= 0) {
               if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                  aliasname = tc.getOwnerName() + "." + tc.getTableName();
               } else {
                  aliasname = tc.getTableName();
               }
            } else {
               String tableAliaswhere;
               if (functionObjects.get(0) instanceof TableColumn) {
                  tc = (TableColumn)functionObjects.get(0);
                  if (tc.getTableName() != null && !tc.getColumnName().trim().equals("?")) {
                     if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                        aliasname = tc.getOwnerName() + "." + tc.getTableName();
                     } else {
                        aliasname = tc.getTableName();
                     }
                  } else {
                     tableAliaswhere = tc.getColumnName();
                     aliasname = "";
                  }
               } else if (!(functionObjects.get(0) instanceof SelectColumn)) {
                  if (functionObjects.get(0) instanceof String) {
                     tableAliaswhere = (String)functionObjects.get(0);
                     if (tableAliaswhere.indexOf(46) != -1) {
                        if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46)) {
                           if (SwisSQLOptions.removeDBSchemaQualifier) {
                              tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                           }

                           aliasname = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                        } else {
                           aliasname = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                        }
                     } else {
                        aliasname = "";
                     }
                  } else if (functionObjects.get(0) instanceof NumericClass && functionObjects.size() == 2) {
                     if (functionObjects.get(1) instanceof TableColumn) {
                        TableColumn tc1 = (TableColumn)functionObjects.get(1);
                        aliasname = tc1.getTableName();
                     } else if (functionObjects.get(1) instanceof String) {
                        tableAliaswhere = (String)functionObjects.get(1);
                        if (tableAliaswhere.indexOf(46) != -1) {
                           if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46)) {
                              if (SwisSQLOptions.removeDBSchemaQualifier) {
                                 tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                              }

                              aliasname = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                           } else {
                              aliasname = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                           }
                        } else {
                           aliasname = "";
                        }
                     } else {
                        aliasname = "";
                     }
                  }
               } else {
                  SelectColumn sc = (SelectColumn)functionObjects.get(0);
                  if (sc.getColumnExpression() != null) {
                     for(int index = 0; index < sc.getColumnExpression().size(); ++index) {
                        if (sc.getColumnExpression().get(index) instanceof TableColumn) {
                           tc = (TableColumn)sc.getColumnExpression().get(index);
                           if (tc.getTableName() != null && !tc.getColumnName().trim().equals("?")) {
                              if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                                 aliasname = tc.getOwnerName() + "." + tc.getTableName();
                              } else {
                                 aliasname = tc.getTableName();
                              }
                           } else {
                              String tableAliaswhere = tc.getColumnName();
                              aliasname = "";
                           }
                        }
                     }
                  }
               }
            }
         }

         if (wc.getColumnExpression().get(0) instanceof CaseStatement) {
            CaseStatement cs = (CaseStatement)wc.getColumnExpression().get(0);
            String tableName = this.getTableAliasName(cs);
            if (tableName != null && !tableName.equals("")) {
               aliasname = tableName;
            }
         }

         if (wc.getColumnExpression().get(0) instanceof String) {
            tableAliaswhere = (String)wc.getColumnExpression().get(0);
            if (tableAliaswhere.indexOf(46) != -1) {
               if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46)) {
                  if (SwisSQLOptions.removeDBSchemaQualifier) {
                     tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                  }

                  aliasname = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
               } else {
                  aliasname = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
               }
            } else {
               aliasname = "";
            }
         }
      } else {
         aliasname = "";
      }

      return aliasname;
   }

   private void addWhereExpToANSIJOIN(SelectQueryStatement to_sqs, WhereExpression we) {
      if (to_sqs != null && to_sqs.getFromClause() != null && to_sqs.getFromClause().getFromItemList() != null) {
         Vector fromItems = to_sqs.getFromClause().getFromItemList();

         for(int i = 0; i < fromItems.size(); ++i) {
            if (fromItems.get(i) instanceof FromTable) {
               FromTable fromTable = (FromTable)fromItems.get(i);
               if (fromTable != null && fromTable.getJoinExpression() != null) {
                  for(int j = 0; j < fromTable.getJoinExpression().size(); ++j) {
                     if (fromTable.getJoinExpression().get(j) instanceof WhereExpression) {
                        WhereExpression whereExp = (WhereExpression)fromTable.getJoinExpression().get(j);
                        if (whereExp != null) {
                           Vector whereItems = whereExp.getWhereItems();

                           for(int k = 0; k < whereItems.size(); ++k) {
                              if (whereItems.get(k) instanceof WhereItem) {
                                 WhereItem whereItem = (WhereItem)whereItems.get(k);
                                 if (whereItem.getLeftWhereExp() != null && whereItem.getRightWhereExp() != null) {
                                    WhereColumn leftColumn = whereItem.getLeftWhereExp();
                                    WhereColumn rightColumn = whereItem.getRightWhereExp();
                                    Vector to_sqsWhereItems = we.getWhereItems();
                                    Vector we_Operators = we.getOperator();

                                    for(int l = 0; l < to_sqsWhereItems.size(); ++l) {
                                       if (to_sqsWhereItems.get(l) instanceof WhereItem) {
                                          WhereItem to_sqsWhereItem = (WhereItem)to_sqsWhereItems.get(l);
                                          if (to_sqsWhereItem != null && to_sqsWhereItem.getLeftWhereExp() != null && to_sqsWhereItem.getRightWhereExp() == null) {
                                             WhereColumn to_sqlWhereColumn = to_sqsWhereItem.getLeftWhereExp();
                                             if (leftColumn.toString().contentEquals(new StringBuffer(to_sqlWhereColumn.toString())) || rightColumn.toString().contentEquals(new StringBuffer(to_sqlWhereColumn.toString()))) {
                                                if (l > 0) {
                                                   if (we_Operators.get(l - 1).toString().equalsIgnoreCase("&AND")) {
                                                      if (we_Operators.size() > 0) {
                                                         ((WhereExpression)((FromTable)fromItems.get(i)).getJoinExpression().get(j)).addOperator("AND");
                                                      }
                                                   } else if (we_Operators.size() > 0) {
                                                      ((WhereExpression)((FromTable)fromItems.get(i)).getJoinExpression().get(j)).addOperator((String)we_Operators.get(l - 1));
                                                   }
                                                } else if (l == 0 && we_Operators.size() > 0) {
                                                   ((WhereExpression)((FromTable)fromItems.get(i)).getJoinExpression().get(j)).addOperator((String)we_Operators.get(l));
                                                }

                                                ((WhereExpression)((FromTable)fromItems.get(i)).getJoinExpression().get(j)).addWhereItem(to_sqsWhereItem);
                                                we.getWhereItems().removeElement(to_sqsWhereItems.get(l));
                                                if (l == 0) {
                                                   if (we.getOperator().size() > 0) {
                                                      we.getOperator().removeElementAt(l);
                                                   }
                                                } else if (l > 0 && we.getOperator().size() > 0) {
                                                   we.getOperator().removeElementAt(l - 1);
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
         }
      }

   }

   public void replaceRownumTableColumn(Object newColumn) throws ConvertException {
      for(int j = 0; j < this.whereItems.size(); ++j) {
         Object wiObj = this.whereItems.get(j);
         if (wiObj != null) {
            if (wiObj instanceof WhereItem) {
               ((WhereItem)wiObj).replaceRownumTableColumn(newColumn);
            } else if (wiObj instanceof WhereExpression) {
               ((WhereExpression)wiObj).replaceRownumTableColumn(newColumn);
            }
         }
      }

   }

   private String getTableAliasName(FunctionCalls fc) {
      String aliasname = "";
      Vector functionArguments = fc.getFunctionArguments();
      if (functionArguments != null) {
         for(int j = 0; j < functionArguments.size(); ++j) {
            if (functionArguments.get(j) instanceof SelectColumn) {
               String tableName = this.getTableAliasWhereR((SelectColumn)functionArguments.get(j));
               if (tableName != null && !tableName.equals("")) {
                  return tableName;
               }
            }
         }
      } else if (functionArguments == null && fc instanceof decode && fc.toString().trim().toLowerCase().startsWith("case") && fc instanceof decode) {
         CaseStatement cs = ((decode)fc).getCaseStatement();
         WhereItem cswi = (WhereItem)cs.getCaseCondition().getWhereItem().get(0);
         SelectColumn cssc = (SelectColumn)cswi.getLeftWhereExp().getColumnExpression().get(0);
         String tableName = this.getTableAliasWhereR(cssc);
         if (tableName != null && !tableName.equals("")) {
            return tableName;
         }
      }

      return aliasname;
   }

   private String getTableAliasName(CaseStatement cs) {
      String aliasname = "";
      WhereItem cswi = null;
      Object cssc;
      if (cs.getCaseCondition() != null) {
         cssc = cs.getCaseCondition().getWhereItem().get(0);
         if (cssc instanceof WhereItem) {
            cswi = (WhereItem)cssc;
         } else if (cssc instanceof WhereExpression) {
            cswi = (WhereItem)((WhereExpression)cssc).getWhereItems().get(0);
         }
      } else {
         cssc = ((WhenStatement)cs.getWhenClauseList().get(0)).getWhenCondition().getWhereItem().get(0);
         if (cssc instanceof WhereItem) {
            cswi = (WhereItem)cssc;
         } else if (cssc instanceof WhereExpression) {
            if (((WhereExpression)cssc).getWhereItems().get(0) instanceof WhereItem) {
               cswi = (WhereItem)((WhereExpression)cssc).getWhereItems().get(0);
            } else if (((WhereExpression)cssc).getWhereItems().get(0) instanceof WhereExpression) {
               cswi = (WhereItem)((WhereExpression)((WhereExpression)cssc).getWhereItems().get(0)).getWhereItems().get(0);
            }
         }
      }

      if (cswi != null) {
         cssc = cswi.getLeftWhereExp().getColumnExpression().get(0);
         if (cssc instanceof SelectColumn) {
            String tableName = this.getTableAliasWhereR((SelectColumn)cssc);
            if (tableName != null && !tableName.equals("")) {
               return tableName;
            }
         } else {
            if (cssc instanceof TableColumn) {
               TableColumn cscol = (TableColumn)cssc;
               if (cscol.getOwnerName() != null) {
                  return cscol.getOwnerName() + "." + cscol.getTableName();
               }

               return cscol.getTableName();
            }

            if (cssc instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)cssc;
               String tableName = this.getTableAliasName(fc);
               if (tableName != null && !tableName.equals("")) {
                  return tableName;
               }
            }
         }
      }

      return aliasname;
   }

   public WhereExpression toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.rownumClause != null && to_sqs != null) {
         this.addLimitClause(to_sqs, from_sqs);
      }

      WhereExpression we = new WhereExpression();
      we.setOpenBrace(this.openBraces);
      we.setCloseBrace(this.closeBraces);
      this.setTargetDatabase(13);
      if (this.beginOperator != null) {
         we.setBeginOperator(this.beginOperator);
      }

      Vector whereItemList;
      int i;
      if (this.operators != null) {
         whereItemList = new Vector();

         for(i = 0; i < this.operators.size(); ++i) {
            String op = (String)this.operators.elementAt(i);
            whereItemList.addElement(op);
         }

         we.setOperator(whereItemList);
      }

      whereItemList = new Vector();

      for(i = 0; i < this.whereItems.size(); ++i) {
         String op;
         if (this.whereItems.elementAt(i) instanceof WhereItem) {
            if (this.whereItems.elementAt(i) == null) {
               whereItemList.addElement((Object)null);
            }

            WhereItem wi = (WhereItem)this.whereItems.elementAt(i);
            if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
               if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                  Vector whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();

                  for(int j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                     whereItemList.addElement(((WhereItem)whereItemsListReplacingEqualsClause.get(j)).toVectorWiseSelect(to_sqs, from_sqs));
                     if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                        if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                           we.getOperator().add("AND");
                        } else {
                           we.getOperator().add("OR");
                        }
                     }
                  }
               } else {
                  WhereItem wiNew = wi.toVectorWiseSelect(to_sqs, from_sqs);
                  if (wiNew.isNullSafeEqualsOperator()) {
                     WhereExpression we1 = new WhereExpression();
                     we1.setOpenBrace("(");
                     we1.setCloseBrace(")");
                     WhereItem wi2 = new WhereItem();
                     wi2.setLeftWhereExp(wiNew.getLeftWhereExp());
                     wi2.setOperator("IS NULL");
                     WhereItem wi3 = new WhereItem();
                     wi3.setLeftWhereExp(wiNew.getRightWhereExp());
                     wi3.setOperator("IS NULL");
                     WhereExpression we2 = new WhereExpression();
                     we2.setOpenBrace("(");
                     we2.setCloseBrace(")");
                     Vector wiV = new Vector();
                     wiV.add(wi2);
                     wiV.add(wi3);
                     Vector opV = new Vector();
                     opV.add("AND");
                     we2.setWhereItem(wiV);
                     we2.setOperator(opV);
                     Vector wiV2 = new Vector();
                     wiV2.add(wiNew);
                     wiV2.add(we2);
                     Vector opV2 = new Vector();
                     opV2.add("OR");
                     we1.setWhereItem(wiV2);
                     we1.setOperator(opV2);
                     whereItemList.addElement(we1);
                  } else {
                     whereItemList.addElement(wiNew);
                  }
               }

               if (wi.getRownumClause() != null) {
                  if (i != 0) {
                     op = (String)we.getOperator().get(i - 1);
                     if (!op.equals("&AND")) {
                        we.getOperator().setElementAt("&AND", i - 1);
                     } else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                     }
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               }
            } else {
               whereItemList.addElement((Object)null);
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         } else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
            WhereExpression internalWE = ((WhereExpression)this.whereItems.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs);
            whereItemList.addElement(internalWE);
            if (internalWE.toString() == null) {
               if (i != 0) {
                  op = (String)we.getOperator().get(i - 1);
                  if (!op.equals("&AND")) {
                     we.getOperator().setElementAt("&AND", i - 1);
                  } else if (we.getOperator().size() > i) {
                     we.getOperator().setElementAt("&AND", i);
                  }
               } else if (we.getOperator().size() > i) {
                  we.getOperator().setElementAt("&AND", i);
               }
            }
         }
      }

      we.setWhereItem(whereItemList);
      we.setCheckWhere(this.checkWhere);
      we.setRownumClause(this.rownumClause);
      if (this.topLevel) {
         this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 13, we);
      }

      return we;
   }

   public WhereExpression toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereExpression whExpConv = new WhereExpression();
      if (this.openBraces != null) {
         whExpConv.setOpenBrace(this.openBraces);
      }

      if (this.closeBraces != null) {
         whExpConv.setCloseBrace(this.closeBraces);
      }

      Vector newOperators;
      int i;
      if (this.whereItems != null) {
         newOperators = new Vector();

         for(i = 0; i < this.whereItems.size(); ++i) {
            if (this.whereItems.get(i) instanceof WhereItem) {
               newOperators.addElement(((WhereItem)this.whereItems.get(i)).toReplaceTblCol(to_sqs, from_sqs));
            } else if (this.whereItems.get(i) instanceof WhereExpression) {
               newOperators.addElement(((WhereExpression)this.whereItems.get(i)).toReplaceTblCol(to_sqs, from_sqs));
            }
         }

         whExpConv.setWhereItem(newOperators);
      }

      if (this.rownumClause != null) {
         whExpConv.setRownumClause(this.rownumClause.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.operators != null) {
         newOperators = new Vector();
         i = 0;

         for(int operatorSize = this.operators.size(); i < operatorSize; ++i) {
            String op = (String)this.operators.elementAt(i);
            newOperators.addElement(op);
         }

         whExpConv.setOperator(newOperators);
      }

      whExpConv.setTopLevel(this.topLevel);
      if (this.beginOperator != null) {
         whExpConv.setBeginOperator(this.beginOperator);
      }

      if (this.concatenation != null) {
         whExpConv.setConcatenation(this.concatenation);
      }

      if (this.commentObj != null) {
         whExpConv.setCommentClass(this.commentObj);
      }

      if (this.commentObjAfterToken != null) {
         whExpConv.setCommentClassAfterToken(this.commentObjAfterToken);
      }

      return whExpConv;
   }
}
