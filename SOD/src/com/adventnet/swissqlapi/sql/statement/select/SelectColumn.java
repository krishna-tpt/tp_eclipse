package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.parser.Token;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.update.UpdateQueryStatement;
import com.adventnet.swissqlapi.util.InstanceDataTypeHandler;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class SelectColumn {
   private String OpenBrace;
   private Vector columnExpression;
   private String CloseBrace;
   private String aliasName;
   private String isAS;
   private String endsWith;
   private UserObjectContext context = null;
   private String targetDataType = null;
   private String targetDataTypeWithSize = null;
   private TableColumn corrTableColumn = null;
   private boolean insideDecodeFunction = false;
   private boolean leftTableColProcessed = false;
   private boolean rightTableColProcessed = false;
   private boolean isSelectColFromUQS = false;
   private boolean inArithmeticExpr;
   private CommentClass commentObj;
   private CommentClass commentObjAfterToken;
   private UpdateQueryStatement fromUQS;
   private boolean isDateAddition = false;
   private Hashtable originalTableNameList = null;
   private String instanceDatatype = "UNDEFINED";
   private boolean isOrderItem = false;
   private String parentFunction = null;
   private String aliasForExpression;
   private boolean teradataUnionCastingDone = false;
   private String ignoreNulls;
   private boolean reportsMysqlConversion = false;
   private boolean castToText = true;
   private int scNestedIfCount = 0;
   private boolean mainSelectColumn = false;
   private boolean outerJoin = false;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public UserObjectContext getObjectContext() {
      return this.context;
   }

   public void setColumnExpression(Vector v_cn) {
      this.columnExpression = v_cn;
   }

   public void addColumnExpressionElement(Object o) {
      this.columnExpression.addElement(o);
   }

   public void setAliasName(String an) {
      this.aliasName = an;
   }

   public void setIsAS(String as) {
      this.isAS = as;
   }

   public void setEndsWith(String ew) {
      this.endsWith = ew;
   }

   public void setOpenBrace(String s_ob) {
      this.OpenBrace = s_ob;
   }

   public String getOpenBrace() {
      return this.OpenBrace;
   }

   public void setCloseBrace(String s_cb) {
      this.CloseBrace = s_cb;
   }

   public String getCloseBrace() {
      return this.CloseBrace;
   }

   public void setInsideDecodeFunction(boolean bool) {
      this.insideDecodeFunction = bool;
   }

   public void setOriginalTableNamesForUpdateSetClause(Hashtable tableList) {
      this.originalTableNameList = tableList;
   }

   public Hashtable getOriginalTableNamesForUpdateSetClause() {
      return this.originalTableNameList;
   }

   public boolean getInsideDecodeFunction() {
      return this.insideDecodeFunction;
   }

   public void setInArithmeticExpression(boolean inArithmeticExpr) {
      this.inArithmeticExpr = inArithmeticExpr;
   }

   public boolean getInArithmeticExpression() {
      return this.inArithmeticExpr;
   }

   public void setCorrespondingTableColumn(TableColumn corrTableColumn) {
      this.corrTableColumn = corrTableColumn;
      if (corrTableColumn != null) {
         this.targetDataTypeWithSize = MetadataInfoUtil.getTargetDataTypeForColumn(corrTableColumn);
         this.targetDataType = CastingUtil.getDataType(this.targetDataTypeWithSize);
      }

   }

   public TableColumn getCorrespondingTableColumn() {
      return this.corrTableColumn;
   }

   public void setTargetDataType(String targetDataType) {
      this.targetDataType = targetDataType;
   }

   public void setMainSelectColumn(boolean mainSelCol) {
      this.mainSelectColumn = mainSelCol;
   }

   public String getTargetDataType() {
      return this.targetDataType;
   }

   public void setSelectColFromUQSSetExpression(boolean isSelectColFromUQS) {
      this.isSelectColFromUQS = isSelectColFromUQS;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public void setCommentClassAfterToken(CommentClass commentObj) {
      this.commentObjAfterToken = commentObj;
   }

   public void setFromUQS(UpdateQueryStatement fromUQS) {
      this.fromUQS = fromUQS;
   }

   public void setIsOrderItem(boolean isOrderItem) {
      this.isOrderItem = isOrderItem;
   }

   public void setAliasForExpression(String aliasForExpr) {
      this.aliasForExpression = aliasForExpr;
   }

   public void setTeradataUnionCastingDone(boolean boolVal) {
      this.teradataUnionCastingDone = boolVal;
   }

   public void setIgnoreNulls(String ignoreNulls) {
      this.ignoreNulls = ignoreNulls;
   }

   public void setScNestedIfCount(int count) {
      this.scNestedIfCount = count;
   }

   public boolean getSelectColFromUQSSetExpression() {
      return this.isSelectColFromUQS;
   }

   public Vector getColumnExpression() {
      return this.columnExpression;
   }

   public String getAliasName() {
      return this.aliasName;
   }

   public String getIsAS() {
      return this.isAS;
   }

   public String getEndsWith() {
      return this.endsWith;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public CommentClass getCommentClassAfterToken() {
      return this.commentObjAfterToken;
   }

   public String getAliasForExpression() {
      return this.aliasForExpression;
   }

   public int getScNestedIfCount() {
      return this.scNestedIfCount;
   }

   public boolean isMainSelectColumn() {
      return this.mainSelectColumn;
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

   public boolean isTeradataUnionCastingDone() {
      return this.teradataUnionCastingDone;
   }

   public String getIgnoreNulls() {
      return this.ignoreNulls;
   }

   public void setReportsMysqlConversion(boolean reportsMysqlConversionValue) {
      this.reportsMysqlConversion = reportsMysqlConversionValue;
   }

   public boolean isReportsMysqlConversion() {
      return this.reportsMysqlConversion;
   }

   public void setCastToTextInsideIf(boolean flag) {
      this.castToText = flag;
   }

   public boolean castToTextType() {
      return this.castToText;
   }

   public void setInstanceDatatype(String instanceDatatype) {
      this.instanceDatatype = instanceDatatype;
   }

   public String getInstanceDatatype() {
      return this.instanceDatatype;
   }

   public SelectColumn toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass(this.commentObj);
      new String();
      Vector v_ce = new Vector();
      int sql_dialect = 0;
      boolean isDualTable = false;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
         FromClause fromClause = from_sqs.getFromClause();
         if (fromClause != null) {
            FromTable fromTable = fromClause.getFromTablefromTheVector();
            if (fromTable != null && fromTable.getTableName() instanceof String) {
               String fromTableName = (String)fromTable.getTableName();
               fromTableName = fromTableName.trim();
               if (fromTableName.equalsIgnoreCase("DUAL")) {
                  isDualTable = true;
               }
            }
         }
      }

      String[] keywords = null;
      if (SwisSQLUtils.getKeywords(2) != null) {
         keywords = (String[])SwisSQLUtils.getKeywords(2);
      }

      sc.setOpenBrace(this.OpenBrace);

      for(int i = 0; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            TableColumn tc = (TableColumn)this.columnExpression.elementAt(i);
            tc.setObjectContext(this.context);
            if (tc.getColumnName() != null) {
               String column_Name = tc.getColumnName();
               if (column_Name.trim().length() > 0 && !isDualTable) {
                  if (!SwisSQLOptions.TSQLQuotedIdentifier && column_Name.trim().startsWith("\"") && column_Name.trim().endsWith("\"")) {
                     String temp = column_Name.substring(1, column_Name.length() - 1);
                     if (temp.indexOf("'") != -1) {
                        temp = temp.replaceAll("'", "\"");
                     }

                     column_Name = "'" + temp + "'";
                  }

                  if (!column_Name.trim().equalsIgnoreCase("USER")) {
                     column_Name = CustomizeUtil.objectNamesToBracedIdentifier(column_Name, keywords, (ModifiedObjectAttr)null);
                  }
               }

               tc.setColumnName(column_Name);
            }

            v_ce.addElement(tc.toMSSQLServerSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
            if (((FunctionCalls)this.columnExpression.elementAt(i)).getFunctionName() == null) {
               v_ce.addElement((FunctionCalls)this.columnExpression.elementAt(i));
            } else {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
            }
         } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
            v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof WhereColumn) {
            v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
            v_ce.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
            v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
            v_ce.addElement(((WhereItem)this.columnExpression.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
            v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toMSSQLServer());
         } else if (!(this.columnExpression.elementAt(i) instanceof String)) {
            v_ce.addElement(this.columnExpression.elementAt(i));
         } else {
            String s_ce = (String)this.columnExpression.elementAt(i);
            if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
               v_ce.addElement("CURRENT_TIME");
            } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
               v_ce.addElement("GETDATE()");
            } else if (s_ce.equalsIgnoreCase("CURRENT")) {
               v_ce.addElement("GETDATE()");
            } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
               v_ce.addElement("CURRENT_TIMESTAMP");
            } else if (s_ce.equalsIgnoreCase("SYS_GUID")) {
               v_ce.addElement("NEWID()");
            } else if (s_ce.equalsIgnoreCase("SYSDATE")) {
               v_ce.addElement("GETDATE()");
            } else if (s_ce.equalsIgnoreCase("**")) {
               this.createPowerFunction(v_ce, this.columnExpression, i, true);
               ++i;
            } else {
               if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                  throw new ConvertException();
               }

               if (s_ce.equalsIgnoreCase("IS")) {
                  this.createDecodeFunction(v_ce, this.columnExpression, i);
                  FunctionCalls fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
                  v_ce.setElementAt(fc.toMSSQLServerSelect(to_sqs, from_sqs), v_ce.size() - 1);
               } else if (s_ce.trim().equals(":=")) {
                  v_ce.addElement("=");
               } else if (s_ce.startsWith(":")) {
                  if (s_ce.substring(0, 2) != null && !s_ce.substring(0, 2).equalsIgnoreCase("::")) {
                     v_ce.addElement("@" + s_ce.substring(1));
                  } else {
                     this.createCastFunction(v_ce, this.columnExpression, i);
                     Object object = v_ce.get(v_ce.size() - 1);
                     if (object instanceof FunctionCalls) {
                        v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toMSSQLServerSelect(to_sqs, from_sqs));
                     }

                     ++i;
                  }
               } else if (s_ce.trim().equalsIgnoreCase("DIV")) {
                  v_ce.addElement("/");
               } else if (!s_ce.equalsIgnoreCase("/")) {
                  if (s_ce.equalsIgnoreCase("||")) {
                     this.checkConcatenationString(v_ce, this.columnExpression, i, 2);
                     v_ce.addElement("+");
                  } else {
                     v_ce.addElement(s_ce);
                  }
               } else {
                  v_ce.addElement("/");
                  if (this.columnExpression.elementAt(i + 1) instanceof SelectColumn) {
                     v_ce.addElement("CONVERT(FLOAT, " + ((SelectColumn)this.columnExpression.elementAt(i + 1)).toMSSQLServerSelect(to_sqs, from_sqs) + ")");
                     ++i;
                  } else if (this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                     v_ce.addElement("CONVERT(FLOAT, " + ((TableColumn)this.columnExpression.elementAt(i + 1)).toMSSQLServerSelect(to_sqs, from_sqs) + ")");
                     ++i;
                  } else if (this.columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
                     v_ce.addElement("CONVERT(FLOAT, " + ((FunctionCalls)this.columnExpression.elementAt(i + 1)).toMSSQLServerSelect(to_sqs, from_sqs) + ")");
                     ++i;
                  } else if (this.columnExpression.elementAt(i + 1) instanceof CaseStatement) {
                     v_ce.addElement("CONVERT(FLOAT, " + ((CaseStatement)this.columnExpression.elementAt(i + 1)).toMSSQLServerSelect(to_sqs, from_sqs) + ")");
                     ++i;
                  } else if (this.columnExpression.elementAt(i + 1) instanceof SelectQueryStatement) {
                     v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i + 1)).toMSSQLServer());
                     ++i;
                  } else if (!(this.columnExpression.elementAt(i + 1) instanceof String)) {
                     if (s_ce.equalsIgnoreCase("||")) {
                        v_ce.addElement("+");
                        ++i;
                     } else {
                        v_ce.addElement("CONVERT(FLOAT, " + this.columnExpression.elementAt(i + 1) + ")");
                        ++i;
                     }
                  } else {
                     s_ce = (String)this.columnExpression.elementAt(i + 1);
                     if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                        v_ce.addElement("CONVERT(FLOAT, CURRENT_TIME)");
                     } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                        v_ce.addElement("CONVERT(FLOAT, CURRENT_DATE)");
                     } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                        v_ce.addElement("CONVERT(FLOAT, CURRENT_DATE)");
                     } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                        v_ce.addElement("CONVERT(FLOAT, CURRENT_TIMESTAMP)");
                     } else if (s_ce.equalsIgnoreCase("SYS_GUID")) {
                        v_ce.addElement("CONVERT(FLOAT, NEWID())");
                     } else if (s_ce.equalsIgnoreCase("SYSDATE")) {
                        v_ce.addElement("CONVERT(FLOAT, GETDATE())");
                     } else if (!s_ce.equalsIgnoreCase("**") && !s_ce.equalsIgnoreCase("^")) {
                        if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                           throw new ConvertException();
                        }

                        if (s_ce.equalsIgnoreCase("IS")) {
                           throw new ConvertException();
                        }

                        if (s_ce.startsWith(":")) {
                           v_ce.addElement("@" + s_ce.substring(1));
                        } else {
                           v_ce.addElement("CONVERT(FLOAT, " + s_ce + ")");
                        }
                     } else {
                        this.createPowerFunction(v_ce, this.columnExpression, i + 1, true);
                        i += 2;
                     }

                     ++i;
                  }
               }
            }
         }
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      sc.setIsAS(this.isAS);
      sc.setEndsWith(this.endsWith);
      sc.setAliasName(this.aliasName);
      return sc;
   }

   public SelectColumn toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass(this.commentObj);
      new String();
      Vector v_ce = new Vector();
      int sql_dialect = 0;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      sc.setOpenBrace(this.OpenBrace);
      sc.setObjectContext(this.context);

      for(int i = 0; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            ((TableColumn)this.columnExpression.elementAt(i)).setObjectContext(this.context);
            v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i)).toSybaseSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
            ((FunctionCalls)this.columnExpression.elementAt(i)).setObjectContext(this.context);
            if (((FunctionCalls)this.columnExpression.elementAt(i)).getFunctionName() == null) {
               v_ce.addElement((FunctionCalls)this.columnExpression.elementAt(i));
            } else {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toSybaseSelect(to_sqs, from_sqs));
            }
         } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
            v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toSybaseSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
            ((CaseStatement)this.columnExpression.elementAt(i)).setObjectContext(this.context);
            v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toSybaseSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
            v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toSybase());
         } else if (this.columnExpression.elementAt(i) instanceof String) {
            String s_ce = (String)this.columnExpression.elementAt(i);
            if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
               v_ce.addElement("CURRENT_TIME");
            } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
               v_ce.addElement("GETDATE()");
            } else if (s_ce.equalsIgnoreCase("CURRENT")) {
               v_ce.addElement("GETDATE()");
            } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
               v_ce.addElement("CURRENT_TIMESTAMP");
            } else if (s_ce.equalsIgnoreCase("SYS_GUID")) {
               v_ce.addElement("NEWID()");
            } else if (s_ce.equalsIgnoreCase("SYSDATE")) {
               v_ce.addElement("GETDATE()");
            } else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
               this.createPowerFunction(v_ce, this.columnExpression, i, true);
               ++i;
            } else {
               if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                  throw new ConvertException();
               }

               if (s_ce.equalsIgnoreCase("IS")) {
                  this.createDecodeFunction(v_ce, this.columnExpression, i);
                  FunctionCalls fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
                  v_ce.setElementAt(fc.toSybaseSelect(to_sqs, from_sqs), v_ce.size() - 1);
               } else if (s_ce.startsWith(":")) {
                  v_ce.addElement("@" + s_ce.substring(1));
               } else if (s_ce.equalsIgnoreCase("/")) {
                  v_ce.addElement("/");
                  if (this.columnExpression.elementAt(i + 1) instanceof SelectColumn) {
                     v_ce.addElement("CONVERT(FLOAT, " + ((SelectColumn)this.columnExpression.elementAt(i + 1)).toSybaseSelect(to_sqs, from_sqs) + ")");
                     ++i;
                  } else if (this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                     v_ce.addElement("CONVERT(FLOAT, " + ((TableColumn)this.columnExpression.elementAt(i + 1)).toSybaseSelect(to_sqs, from_sqs) + ")");
                     ++i;
                  } else if (this.columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
                     v_ce.addElement("CONVERT(FLOAT, " + ((FunctionCalls)this.columnExpression.elementAt(i + 1)).toSybaseSelect(to_sqs, from_sqs) + ")");
                     ++i;
                  } else if (this.columnExpression.elementAt(i + 1) instanceof CaseStatement) {
                     v_ce.addElement("CONVERT(FLOAT, " + ((CaseStatement)this.columnExpression.elementAt(i + 1)).toSybaseSelect(to_sqs, from_sqs) + ")");
                     ++i;
                  } else if (this.columnExpression.elementAt(i + 1) instanceof SelectQueryStatement) {
                     v_ce.addElement("CONVERT(FLOAT, " + ((SelectQueryStatement)this.columnExpression.elementAt(i + 1)).toSybase() + ")");
                     ++i;
                  } else if (this.columnExpression.elementAt(i + 1) instanceof String) {
                     s_ce = (String)this.columnExpression.elementAt(i + 1);
                     if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                        v_ce.addElement("CONVERT(FLOAT, CURRENT_TIME)");
                     } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                        v_ce.addElement("CONVERT(FLOAT, CURRENT_DATE)");
                     } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                        v_ce.addElement("CONVERT(FLOAT, CURRENT_DATE)");
                     } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                        v_ce.addElement("CONVERT(FLOAT, CURRENT_TIMESTAMP)");
                     } else if (s_ce.equalsIgnoreCase("SYS_GUID")) {
                        v_ce.addElement("CONVERT(FLOAT, NEWID())");
                     } else if (s_ce.equalsIgnoreCase("SYSDATE")) {
                        v_ce.addElement("CONVERT(FLOAT, GETDATE())");
                     } else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                        this.createPowerFunction(v_ce, this.columnExpression, i + 1, true);
                        i += 2;
                     } else {
                        if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                           throw new ConvertException();
                        }

                        if (s_ce.equalsIgnoreCase("IS")) {
                           throw new ConvertException();
                        }

                        if (s_ce.startsWith(":")) {
                           v_ce.addElement("@" + s_ce.substring(1));
                        } else {
                           v_ce.addElement("CONVERT(FLOAT, " + s_ce + ")");
                        }
                     }

                     ++i;
                  } else if (s_ce.equalsIgnoreCase("||")) {
                     v_ce.addElement("+");
                     ++i;
                  } else {
                     v_ce.addElement("CONVERT(FLOAT, " + this.columnExpression.elementAt(i + 1) + ")");
                     ++i;
                  }
               } else if (s_ce.equalsIgnoreCase("||")) {
                  v_ce.addElement("+");
               } else {
                  v_ce.addElement(s_ce);
               }
            }
         } else {
            v_ce.addElement(this.columnExpression.elementAt(i));
         }
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      sc.setIsAS(this.isAS);
      sc.setEndsWith(this.endsWith);
      if (from_sqs != null) {
         SelectStatement fromSS = from_sqs.getSelectStatement();
         if (fromSS != null && this.aliasName != null) {
            Vector fromSelectItems = fromSS.getSelectItemList();
            if (fromSelectItems != null) {
               for(int l = 0; l < fromSelectItems.size(); ++l) {
                  Object obj = fromSelectItems.get(l);
                  if (obj instanceof SelectColumn && !obj.equals(this)) {
                     Vector colExpr = ((SelectColumn)obj).getColumnExpression();
                     String currAliasName = ((SelectColumn)obj).getAliasName();
                     if (colExpr != null && colExpr.size() == 1) {
                        Object obj1 = colExpr.get(0);
                        if (obj1 instanceof TableColumn) {
                           String colName = ((TableColumn)obj1).getColumnName();
                           if (colName != null && colName.equals(this.aliasName) && (currAliasName == null || currAliasName != null && currAliasName.equals(this.aliasName))) {
                              this.aliasName = this.aliasName + "_ADV";
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      sc.setAliasName(this.aliasName);
      return sc;
   }

   public SelectColumn toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass(this.commentObj);
      if (this.commentObj != null) {
         this.commentObj.setSQLDialect(10);
      }

      int sql_dialect = false;
      Integer modulus = 0;
      if (from_sqs != null) {
         int var12 = from_sqs.getSQLDialect();
      }

      new String();
      Vector v_ce = new Vector();
      sc.setOpenBrace(this.OpenBrace);
      this.handleTableColumn(this.columnExpression, from_sqs, 10);

      for(int i = 0; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            TableColumn tc = (TableColumn)this.columnExpression.elementAt(i);
            tc.setObjectContext(this.context);
            v_ce.addElement(tc.toTimesTenSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
            ((FunctionCalls)this.columnExpression.elementAt(i)).setObjectContext(this.context);
            FunctionCalls fcs = (FunctionCalls)this.columnExpression.elementAt(i);
            if (fcs.toString().trim().toLowerCase().startsWith("user_name(")) {
               v_ce.addElement("USER");
            } else {
               FunctionCalls temp = fcs.toTimesTenSelect(to_sqs, from_sqs);
               v_ce.addElement(temp);
               this.columnExpression.setElementAt(temp, i);
            }
         } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
            SelectColumn temp = ((SelectColumn)this.columnExpression.elementAt(i)).toTimesTenSelect(to_sqs, from_sqs);
            v_ce.addElement(temp);
            this.columnExpression.setElementAt(temp, i);
         } else {
            if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
               throw new ConvertException("\nCASE statements are not supported in TimesTen 5.1.21\n");
            }

            if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
               SelectQueryStatement temp = ((SelectQueryStatement)this.columnExpression.elementAt(i)).toTimesTen();
               v_ce.addElement(temp);
               this.columnExpression.setElementAt(temp, i);
            } else if (this.columnExpression.elementAt(i) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i);
               if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                  v_ce.addElement("TO_CHAR(SYSDATE,'HH:MI:SS')");
               } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                  v_ce.addElement("SYSDATE");
               } else if (!s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                  if (s_ce.equalsIgnoreCase("CURRENT")) {
                     v_ce.addElement("SYSDATE");
                  } else if (s_ce.equalsIgnoreCase("%")) {
                     v_ce.addElement("%");
                     modulus = 1;
                  } else {
                     String newStr;
                     if (s_ce.trim().startsWith("$")) {
                        try {
                           newStr = s_ce.substring(1);
                           float numericValue = Float.parseFloat(newStr);
                           v_ce.addElement(newStr);
                        } catch (NumberFormatException var11) {
                           v_ce.addElement(s_ce);
                        }
                     } else if (!s_ce.equalsIgnoreCase("::") && !s_ce.equalsIgnoreCase("**") && !s_ce.equalsIgnoreCase("IS")) {
                        if (s_ce.equalsIgnoreCase("=")) {
                           if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                              sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
                           }

                           v_ce.setElementAt(" ", i - 1);
                        } else if (s_ce.startsWith("@")) {
                           v_ce.addElement(":" + s_ce.substring(1));
                        } else if (!s_ce.equalsIgnoreCase("+") && !s_ce.equalsIgnoreCase("||")) {
                           v_ce.addElement(s_ce);
                        } else {
                           newStr = null;
                           if (i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                              newStr = this.columnExpression.elementAt(i - 1).toString();
                              if (newStr.equalsIgnoreCase("''") && SwisSQLOptions.fromSybase) {
                                 newStr = "' '";
                              }

                              if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                                 v_ce.remove(v_ce.size() - 1);
                                 v_ce.addElement(this.concatFunction(newStr, this.columnExpression.elementAt(i + 1), to_sqs, from_sqs));
                                 this.columnExpression.remove(i + 1);
                              } else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1) instanceof String && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'")) {
                                 if (this.columnExpression.elementAt(i + 1).toString().equalsIgnoreCase("''") && SwisSQLOptions.fromSybase) {
                                    this.columnExpression.setElementAt("' '", i + 1);
                                 }

                                 v_ce.remove(v_ce.size() - 1);
                                 v_ce.addElement(this.concatFunction(newStr, this.columnExpression.elementAt(i + 1), to_sqs, from_sqs));
                                 this.columnExpression.remove(i + 1);
                              } else if (from_sqs != null) {
                                 if (from_sqs.getFromClause() == null && i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'")) {
                                    if (this.columnExpression.elementAt(i + 1).toString().equalsIgnoreCase("''") && SwisSQLOptions.fromSybase) {
                                       this.columnExpression.setElementAt("' '", i + 1);
                                    }

                                    v_ce.remove(v_ce.size() - 1);
                                    v_ce.addElement(this.concatFunction(newStr, this.columnExpression.elementAt(i + 1), to_sqs, from_sqs));
                                    this.columnExpression.remove(i + 1);
                                 } else {
                                    v_ce.addElement(s_ce);
                                 }
                              } else {
                                 v_ce.addElement(s_ce);
                              }
                           } else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'")) {
                              newStr = this.columnExpression.elementAt(i + 1).toString();
                              if (newStr.equalsIgnoreCase("''") && SwisSQLOptions.fromSybase) {
                                 newStr = "' '";
                              }

                              if (v_ce.size() > 0 && v_ce.lastElement() instanceof FunctionCalls && ((FunctionCalls)v_ce.get(v_ce.size() - 1)).getFunctionName().getColumnName().equalsIgnoreCase("CONCAT")) {
                                 v_ce.addElement(this.concatFunction(v_ce.lastElement(), newStr, to_sqs, from_sqs));
                                 v_ce.remove(v_ce.size() - 2);
                                 this.columnExpression.remove(i + 1);
                              } else if (i - 1 >= 0 && this.columnExpression.elementAt(i - 1) instanceof TableColumn) {
                                 v_ce.remove(v_ce.size() - 1);
                                 v_ce.addElement(this.concatFunction(this.columnExpression.elementAt(i - 1), newStr, to_sqs, from_sqs));
                                 this.columnExpression.remove(i + 1);
                              } else {
                                 v_ce.addElement(s_ce);
                              }
                           } else {
                              String dtype;
                              if (i - 1 >= 0 && this.columnExpression.elementAt(i - 1) instanceof TableColumn) {
                                 dtype = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)this.columnExpression.elementAt(i - 1));
                                 if (dtype != null && dtype.toLowerCase().indexOf("char") != -1) {
                                    v_ce.remove(v_ce.size() - 1);
                                    v_ce.addElement(this.concatFunction(this.columnExpression.elementAt(i - 1), this.columnExpression.elementAt(i + 1), to_sqs, from_sqs));
                                    this.columnExpression.remove(i + 1);
                                 } else {
                                    v_ce.addElement(s_ce);
                                 }
                              } else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                                 dtype = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)this.columnExpression.elementAt(i + 1));
                                 if (v_ce.size() > 0 && v_ce.lastElement() instanceof FunctionCalls && ((FunctionCalls)v_ce.get(v_ce.size() - 1)).getFunctionName().getColumnName().equalsIgnoreCase("CONCAT")) {
                                    v_ce.addElement(this.concatFunction(v_ce.lastElement(), this.columnExpression.elementAt(i + 1), to_sqs, from_sqs));
                                    v_ce.remove(v_ce.size() - 2);
                                    this.columnExpression.remove(i + 1);
                                 } else if (dtype != null && dtype.toLowerCase().indexOf("char") != -1) {
                                    v_ce.remove(v_ce.size() - 1);
                                    v_ce.addElement(this.concatFunction(this.columnExpression.elementAt(i - 1), this.columnExpression.elementAt(i + 1), to_sqs, from_sqs));
                                    this.columnExpression.remove(i + 1);
                                 } else {
                                    v_ce.addElement(s_ce);
                                 }
                              } else {
                                 v_ce.addElement(s_ce);
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i));
            }
         }

         modulus = handleModulusOperator(v_ce, modulus, i, 10);
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      sc.setIsAS(this.isAS);
      sc.setEndsWith(this.endsWith);
      if (sc != null && sc.getAliasName() == null) {
         if (this.aliasName != null && this.aliasName.charAt(0) == '\'') {
            sc.setAliasName(this.aliasName.replace('\'', '"'));
         } else if (this.aliasName != null && this.aliasName.startsWith("[")) {
            String tempAlias = this.aliasName.substring(1, this.aliasName.length() - 1);
            if (tempAlias.indexOf(" ") != -1) {
               sc.setAliasName("\"" + tempAlias + "\"");
            } else {
               tempAlias = CustomizeUtil.objectNamesToQuotedIdentifier(tempAlias, SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10);
               sc.setAliasName(tempAlias);
            }
         } else {
            this.aliasName = CustomizeUtil.objectNamesToQuotedIdentifier(this.aliasName, SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10);
            sc.setAliasName(this.aliasName);
         }
      }

      return sc;
   }

   public SelectColumn toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass(this.commentObj);
      int sql_dialect = 0;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      new String();
      Vector v_ce = new Vector();
      sc.setOpenBrace(this.OpenBrace);

      for(int i = 0; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i)).toNetezzaSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
            v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toNetezzaSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
            v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toNetezzaSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
            v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toNetezzaSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
            v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toNetezza());
         } else if (this.columnExpression.elementAt(i) instanceof String) {
            String s_ce = (String)this.columnExpression.elementAt(i);
            if (s_ce.charAt(0) == '\'') {
               v_ce.addElement(s_ce.replace('\'', '\''));
            } else if (s_ce.equalsIgnoreCase("**")) {
               this.createPowerFunction(v_ce, this.columnExpression, i, true);
               ++i;
            } else {
               if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                  throw new ConvertException();
               }

               if (s_ce.equalsIgnoreCase("=")) {
                  if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                     sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
                  }

                  v_ce.setElementAt(" ", i - 1);
               } else if (s_ce.equalsIgnoreCase("IS")) {
                  this.createDecodeFunction(v_ce, this.columnExpression, i);
                  FunctionCalls fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
                  v_ce.setElementAt(fc.toNetezzaSelect(to_sqs, from_sqs), v_ce.size() - 1);
               } else {
                  v_ce.addElement(s_ce);
               }
            }
         } else {
            v_ce.addElement(this.columnExpression.elementAt(i));
         }
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      sc.setIsAS(this.isAS);
      sc.setEndsWith(this.endsWith);
      if (sc != null && sc.getAliasName() == null) {
         if (this.aliasName != null && this.aliasName.charAt(0) == '\'') {
            sc.setAliasName(this.aliasName.replace('\'', '"'));
         } else if (this.aliasName != null && this.aliasName.charAt(0) == '"') {
            sc.setAliasName(this.aliasName);
         } else if (this.aliasName != null) {
            sc.setAliasName(this.aliasName);
         }
      }

      return sc;
   }

   private FunctionCalls concatFunction(Object firstArg, Object secondArg, SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      FunctionCalls concatFC = new FunctionCalls();
      Vector fnArgs = new Vector();
      TableColumn tc = new TableColumn();
      tc.setColumnName("CONCAT");
      if (firstArg instanceof TableColumn) {
         firstArg = ((TableColumn)firstArg).toTimesTenSelect(to_sqs, from_sqs);
      }

      if (secondArg instanceof TableColumn) {
         secondArg = ((TableColumn)secondArg).toTimesTenSelect(to_sqs, from_sqs);
      }

      fnArgs.add(firstArg);
      fnArgs.add(secondArg);
      concatFC.setFunctionName(tc);
      concatFC.setFunctionArguments(fnArgs);
      return concatFC;
   }

   public void createPowerFunction(Vector v_ce, Vector columnExpression, int i, boolean isPower) {
      SelectColumn sc_firstarg = new SelectColumn();
      SelectColumn sc_secondarg = new SelectColumn();
      Vector v_farg = new Vector();
      TableColumn tc = new TableColumn();
      FunctionCalls fc = new FunctionCalls();
      Vector vec_firstarg = new Vector();
      Vector vec_secondarg = new Vector();
      if (isPower) {
         tc.setColumnName("POWER");
      } else {
         tc.setColumnName("POW");
      }

      fc.setFunctionName(tc);
      vec_firstarg.addElement(columnExpression.elementAt(i - 1));
      v_ce.setElementAt(" ", i - 1);
      sc_firstarg.setColumnExpression(vec_firstarg);
      v_farg.addElement(sc_firstarg);
      vec_secondarg.addElement(columnExpression.elementAt(i + 1));
      sc_secondarg.setColumnExpression(vec_secondarg);
      v_farg.addElement(sc_secondarg);
      fc.setFunctionArguments(v_farg);
      v_ce.addElement(fc);
   }

   public void createXOREquivalentFunction(Vector v_ce, Vector columnExpression, int i, boolean isPower, SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, String str) throws ConvertException {
      SelectColumn sc_firstarg = new SelectColumn();
      SelectColumn sc_secondarg = new SelectColumn();
      new SelectColumn();
      Vector vec_secondarg = new Vector();
      v_ce.addElement("(");
      v_ce.addElement("(");
      if (columnExpression.elementAt(i - 1) instanceof TableColumn) {
         v_ce.addElement(((TableColumn)columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i - 1) instanceof FunctionCalls) {
         v_ce.addElement(((FunctionCalls)columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i - 1) instanceof SelectColumn) {
         v_ce.addElement(((SelectColumn)columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i - 1) instanceof CaseStatement) {
         v_ce.addElement(((CaseStatement)columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i - 1) instanceof SelectQueryStatement) {
         v_ce.addElement(((SelectQueryStatement)columnExpression.elementAt(i - 1)).toOracle());
      } else {
         v_ce.addElement(columnExpression.elementAt(i - 1));
      }

      v_ce.addElement("+");
      if (columnExpression.elementAt(i + 1) instanceof TableColumn) {
         v_ce.addElement(((TableColumn)columnExpression.elementAt(i + 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
         v_ce.addElement(((FunctionCalls)columnExpression.elementAt(i + 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i + 1) instanceof SelectColumn) {
         v_ce.addElement(((SelectColumn)columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i + 1) instanceof CaseStatement) {
         v_ce.addElement(((CaseStatement)columnExpression.elementAt(i + 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i + 1) instanceof SelectQueryStatement) {
         v_ce.addElement(((SelectQueryStatement)columnExpression.elementAt(i + 1)).toOracle());
      } else {
         v_ce.addElement(columnExpression.elementAt(i + 1));
      }

      v_ce.addElement(")");
      v_ce.addElement("-");
      Vector v_farg = new Vector();
      Vector vec_firstarg = new Vector();
      TableColumn tc = new TableColumn();
      FunctionCalls fc = new FunctionCalls();
      tc.setColumnName("BITAND");
      fc.setFunctionName(tc);
      if (columnExpression.elementAt(i - 1) instanceof TableColumn) {
         vec_firstarg.addElement(((TableColumn)columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i - 1) instanceof FunctionCalls) {
         vec_firstarg.addElement(((FunctionCalls)columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i - 1) instanceof SelectColumn) {
         vec_firstarg.addElement(((SelectColumn)columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i - 1) instanceof CaseStatement) {
         vec_firstarg.addElement(((CaseStatement)columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i - 1) instanceof SelectQueryStatement) {
         vec_firstarg.addElement(((SelectQueryStatement)columnExpression.elementAt(i - 1)).toOracle());
      } else {
         vec_firstarg.addElement(columnExpression.elementAt(i - 1));
      }

      v_ce.setElementAt(" ", i - 1);
      sc_firstarg.setColumnExpression(vec_firstarg);
      v_farg.addElement(sc_firstarg);
      if (columnExpression.elementAt(i + 1) instanceof TableColumn) {
         vec_secondarg.addElement(((TableColumn)columnExpression.elementAt(i + 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
         vec_secondarg.addElement(((FunctionCalls)columnExpression.elementAt(i + 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i + 1) instanceof SelectColumn) {
         vec_secondarg.addElement(((SelectColumn)columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i + 1) instanceof CaseStatement) {
         vec_secondarg.addElement(((CaseStatement)columnExpression.elementAt(i + 1)).toOracleSelect(to_sqs, from_sqs));
      } else if (columnExpression.elementAt(i + 1) instanceof SelectQueryStatement) {
         vec_secondarg.addElement(((SelectQueryStatement)columnExpression.elementAt(i + 1)).toOracle());
      } else {
         vec_secondarg.addElement(columnExpression.elementAt(i + 1));
      }

      columnExpression.setElementAt(" ", i + 1);
      sc_secondarg.setColumnExpression(vec_secondarg);
      v_farg.addElement(sc_secondarg);
      fc.setFunctionArguments(v_farg);
      v_ce.addElement(fc);
      if (str.equals("^")) {
         v_ce.addElement("*");
         v_ce.addElement("2");
      }

      v_ce.addElement(")");
   }

   public SelectColumn toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass(this.commentObj);
      int sql_dialect = 0;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      new String();
      Vector v_ce = new Vector();
      sc.setOpenBrace(this.OpenBrace);

      for(int i = 0; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i)).toANSISelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
            if (((FunctionCalls)this.columnExpression.elementAt(i)).getFunctionName() == null) {
               v_ce.addElement((FunctionCalls)this.columnExpression.elementAt(i));
            } else {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toANSISelect(to_sqs, from_sqs));
            }
         } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
            v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toANSISelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
            v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toANSISelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
            v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toANSI());
         } else if (this.columnExpression.elementAt(i) instanceof String) {
            String s_ce = (String)this.columnExpression.elementAt(i);
            if (s_ce.charAt(0) == '\'' && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects && FunctionCalls.functionArgsInSingleQuotesToDouble) {
               v_ce.addElement(s_ce.replace('\'', '"'));
            } else if (s_ce.equalsIgnoreCase("**")) {
               this.createPowerFunction(v_ce, this.columnExpression, i, true);
               ++i;
            } else {
               if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                  throw new ConvertException();
               }

               if (s_ce.equalsIgnoreCase("=")) {
                  if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                     sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
                  }

                  v_ce.setElementAt(" ", i - 1);
               } else if (s_ce.equalsIgnoreCase("IS")) {
                  this.createDecodeFunction(v_ce, this.columnExpression, i);
                  FunctionCalls fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
                  v_ce.setElementAt(fc.toANSISelect(to_sqs, from_sqs), v_ce.size() - 1);
               } else {
                  v_ce.addElement(s_ce);
               }
            }
         } else {
            v_ce.addElement(this.columnExpression.elementAt(i));
         }
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      sc.setIsAS(this.isAS);
      sc.setEndsWith(this.endsWith);
      if (sc != null && sc.getAliasName() == null) {
         if (this.aliasName != null && this.aliasName.charAt(0) == '\'' && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
            sc.setAliasName(this.aliasName.replace('\'', '"'));
         } else if (this.aliasName != null && this.aliasName.charAt(0) == '"') {
            sc.setAliasName(this.aliasName);
         } else if (this.aliasName != null && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
            sc.setAliasName("\"" + this.aliasName + "\"");
         } else if (this.aliasName != null) {
            sc.setAliasName(this.aliasName);
         }
      }

      return sc;
   }

   public SelectColumn toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass(this.commentObj);
      String teradataComTkn = "/*%SSTD%";
      if (this.commentObj != null) {
         String teradataComment = this.commentObj.toString().trim();
         if (teradataComment.indexOf(teradataComTkn) != -1) {
            sc.setCommentClass(this.commentObj);
            this.commentObj.setComment(teradataComment.substring(teradataComTkn.length(), teradataComment.length() - 2));
            this.commentObj.setSQLDialect(12);
         }
      }

      int sql_dialect = 0;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      new String();
      Vector v_ce = new Vector();
      sc.setOpenBrace(this.OpenBrace);

      for(int i = 0; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            TableColumn teradataTCN = ((TableColumn)this.columnExpression.elementAt(i)).toTeradataSelect(to_sqs, from_sqs);
            v_ce.addElement(teradataTCN);
            if (to_sqs != null) {
               to_sqs.addTableColumnToTableColumnList(teradataTCN);
            }
         } else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
            if (((FunctionCalls)this.columnExpression.elementAt(i)).getFunctionName() == null) {
               v_ce.addElement((FunctionCalls)this.columnExpression.elementAt(i));
            } else {
               try {
                  v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toTeradataSelect(to_sqs, from_sqs));
               } catch (ConvertException var15) {
                  throw var15;
               }
            }
         } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
            v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toTeradataSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
            v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toTeradataSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
            v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toTeradata());
         } else if (this.columnExpression.elementAt(i) instanceof String) {
            String teradataCom = "/*%SSTD%";
            String s_ce = (String)this.columnExpression.elementAt(i);
            if (s_ce.equalsIgnoreCase("**")) {
               v_ce.addElement(s_ce);
            } else if (!s_ce.equalsIgnoreCase("+") && !s_ce.equalsIgnoreCase("-")) {
               if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                  throw new ConvertException();
               }

               if (s_ce.equalsIgnoreCase("=")) {
                  if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                     sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
                  }

                  v_ce.setElementAt(" ", i - 1);
               } else if (s_ce.equalsIgnoreCase("IS")) {
                  this.createDecodeFunction(v_ce, this.columnExpression, i);
                  FunctionCalls fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
                  v_ce.setElementAt(fc.toTeradataSelect(to_sqs, from_sqs), v_ce.size() - 1);
               } else if (s_ce.indexOf(teradataCom) != -1) {
                  v_ce.addElement(s_ce.substring(teradataCom.length(), s_ce.length() - 2));
               } else if (s_ce.startsWith("--")) {
                  v_ce.add("/*" + s_ce + "*/");
               } else {
                  v_ce.addElement(s_ce);
               }
            } else {
               v_ce.addElement(s_ce);
               if (i > 0 && i < this.columnExpression.size() - 1) {
                  boolean isDateExpr = false;
                  Object obj = this.columnExpression.get(i - 1);
                  if (obj instanceof TableColumn) {
                     if (SwisSQLUtils.getFunctionReturnType(((TableColumn)obj).getColumnName(), (Vector)null).equalsIgnoreCase("date")) {
                        isDateExpr = true;
                     } else if (CastingUtil.getValueIgnoreCase(SwisSQLAPI.columnDatatypes, ((TableColumn)obj).getColumnName()) != null && CastingUtil.getValueIgnoreCase(SwisSQLAPI.columnDatatypes, ((TableColumn)obj).getColumnName()).toString().equalsIgnoreCase("timestamp")) {
                        isDateExpr = true;
                     }
                  } else if (obj instanceof FunctionCalls) {
                     FunctionCalls fc = (FunctionCalls)obj;
                     TableColumn tc = fc.getFunctionName();
                     if (tc != null) {
                        Vector args = fc.getFunctionArguments();
                        if (tc != null && SwisSQLUtils.getFunctionReturnType(tc.getColumnName(), args).equalsIgnoreCase("date")) {
                           isDateExpr = true;
                        }
                     }
                  }

                  if (isDateExpr) {
                     Object nextObj = this.columnExpression.get(i + 1);
                     if (nextObj instanceof String) {
                        Vector intervalVector = new Vector();
                        int increaseLoopCount = this.convertNumeralsToInterval(intervalVector, this.columnExpression.subList(i + 1, this.columnExpression.size()));
                        if (intervalVector.size() > 0) {
                           v_ce.addAll(intervalVector);
                           i += increaseLoopCount;
                        }
                     }
                  }
               }
            }
         } else {
            Object otherObj = this.columnExpression.elementAt(i);
            if (otherObj != null && (otherObj.toString().toLowerCase().startsWith("/*+") || otherObj.toString().toUpperCase().indexOf(teradataComTkn) != -1)) {
               if (otherObj.toString().toUpperCase().indexOf(teradataComTkn) != -1) {
                  v_ce.addElement(otherObj.toString().substring(teradataComTkn.length() + 1, otherObj.toString().length() - 2));
               } else {
                  if (this.commentObj == null) {
                     this.commentObj = new CommentClass();
                  }

                  ArrayList specTokenList = new ArrayList();
                  specTokenList.add(otherObj.toString());
                  this.commentObj.setSpecialToken(specTokenList);
                  sc.setCommentClass(this.commentObj);
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i));
            }
         }
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      sc.setIsAS(this.isAS);
      sc.setEndsWith(this.endsWith);
      sc.setAliasForExpression(this.aliasForExpression);
      if (this.aliasName != null && this.aliasName.startsWith("[")) {
         String tempAlias = this.aliasName.substring(1, this.aliasName.length() - 1);
         if (tempAlias.indexOf(" ") != -1) {
            sc.setAliasName("\"" + tempAlias + "\"");
         } else {
            tempAlias = CustomizeUtil.objectNamesToQuotedIdentifier(tempAlias, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
            sc.setAliasName(tempAlias);
         }
      } else if (this.aliasName != null) {
         this.aliasName = CustomizeUtil.objectNamesToQuotedIdentifier(this.aliasName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
         sc.setAliasName(this.aliasName);
      }

      return sc;
   }

   public SelectColumn toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass(this.commentObj);
      int sql_dialect = 0;
      boolean isDate = false;
      boolean openBraceisSet = false;
      boolean concatExpr = false;
      Integer modulus = 0;
      boolean two_or_more_date_column = false;
      int i = false;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      new String();
      Vector v_ce = new Vector();
      sc.setOpenBrace(this.OpenBrace);
      ArrayList dateColsWithArithExpr = new ArrayList();
      boolean arithmeticExprAssignedToVar = false;
      boolean arithmeticExpr = false;
      if (this.columnExpression.contains("||")) {
         concatExpr = true;
      }

      int m;
      String sourceDataType;
      Object rightExpr;
      TableColumn tc;
      String dataType;
      TableColumn tc;
      label585:
      for(m = 0; m < this.columnExpression.size(); ++m) {
         if (this.columnExpression.get(m) instanceof String) {
            sourceDataType = (String)this.columnExpression.get(m);
            sourceDataType = sourceDataType.trim();
            if ((sourceDataType.equals("*") || sourceDataType.equals("/") || sourceDataType.equals("+") || sourceDataType.equals("-")) && m != 0) {
               rightExpr = this.columnExpression.get(m - 1);
               if (rightExpr instanceof TableColumn) {
                  tc = (TableColumn)rightExpr;
                  dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                  if (dataType != null && dataType.toLowerCase().indexOf("char") != -1) {
                     break;
                  }
               }

               Object o1 = this.columnExpression.get(m + 1);
               if (o1 instanceof TableColumn) {
                  tc = (TableColumn)o1;
                  String datatype = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                  if (datatype != null && datatype.toLowerCase().indexOf("char") != -1) {
                     break;
                  }
               }

               if (sourceDataType.equals("+")) {
                  for(int k = 0; k < this.columnExpression.size(); ++k) {
                     Object obj = this.columnExpression.get(k);
                     if (obj instanceof String) {
                        String temp = obj.toString();
                        if (temp.startsWith("'") && temp.endsWith("'")) {
                           break label585;
                        }
                     } else if (obj instanceof FunctionCalls) {
                        FunctionCalls fc = (FunctionCalls)obj;
                        TableColumn tc = fc.getFunctionName();
                        Vector args = fc.getFunctionArguments();
                        if (tc != null && SwisSQLUtils.getFunctionReturnType(tc.getColumnName(), args).equalsIgnoreCase("string")) {
                           break label585;
                        }
                     }
                  }
               }

               arithmeticExpr = true;
               break;
            }
         }
      }

      if (this.targetDataType != null) {
         if (this.targetDataType.toLowerCase().startsWith("varchar")) {
            arithmeticExprAssignedToVar = arithmeticExpr;
         }

         if (!concatExpr && arithmeticExprAssignedToVar && !this.inArithmeticExpr) {
            v_ce.addElement("VARCHAR(CHAR(");
         }
      }

      if (concatExpr && arithmeticExpr) {
         v_ce.addElement("CHAR(");
      }

      FunctionCalls fn;
      for(int i = 0; i < this.columnExpression.size(); ++i) {
         TableColumn tableCol;
         String colDataType;
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            tableCol = (TableColumn)this.columnExpression.elementAt(i);
            if (tableCol.getColumnName() != null) {
               sourceDataType = tableCol.getColumnName();
               if (sourceDataType.equalsIgnoreCase("INCREMENT_BY")) {
                  sourceDataType = "INCREMENT";
                  tableCol.setColumnName(sourceDataType);
               } else if (sourceDataType.equalsIgnoreCase("NEXTVAL")) {
                  colDataType = tableCol.getTableName();
                  if (colDataType != null && !colDataType.equals("")) {
                     tableCol.setTableName("NEXTVAL FOR ");
                     tableCol.setColumnName(colDataType);
                  } else {
                     tableCol.setColumnName("NEXTVAL");
                  }
               } else if (sourceDataType.equalsIgnoreCase("CURRVAL")) {
                  colDataType = tableCol.getTableName();
                  tableCol.setTableName("PREVVAL FOR ");
                  tableCol.setColumnName(colDataType);
               }
            }

            sourceDataType = MetadataInfoUtil.getDatatypeName(from_sqs, tableCol);
            if (sourceDataType == null && SwisSQLAPI.variableDatatypeMapping != null) {
               sourceDataType = (String)CastingUtil.getValueIgnoreCase(SwisSQLAPI.variableDatatypeMapping, tableCol.getResultString());
            }

            if (sourceDataType != null && (sourceDataType.trim().toLowerCase().indexOf("date") != -1 || sourceDataType.trim().toLowerCase().indexOf("timestamp") != -1 || sourceDataType.toLowerCase().indexOf("time") != -1)) {
               isDate = true;
               dateColsWithArithExpr.add(tableCol);
               this.addInDateColsIfPrevFnCall(v_ce, dateColsWithArithExpr);
            }

            if (!isDate) {
               if (!arithmeticExpr && !this.inArithmeticExpr) {
                  tableCol.setTargetDataType(this.targetDataType);
               } else if (this.targetDataType == null) {
                  tableCol.setTargetDataType("DOUBLE");
               } else if (this.targetDataType == null || this.targetDataType.equalsIgnoreCase("varchar") && this.targetDataType.equalsIgnoreCase("char")) {
                  tableCol.setTargetDataType(this.targetDataType);
               } else {
                  tableCol.setTargetDataType("DOUBLE");
               }
            }

            v_ce.addElement(tableCol.toDB2Select(to_sqs, from_sqs));
         } else {
            FunctionCalls fc;
            if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
               fc = (FunctionCalls)this.columnExpression.elementAt(i);
               fc.setTargetDataType(this.targetDataType);
               TableColumn tc = fc.getFunctionName();
               if (tc != null && (tc.getColumnName().equalsIgnoreCase("TO_DATE") || tc.getColumnName().equalsIgnoreCase("ORACLE_TO_DATE"))) {
                  isDate = true;
               }

               if (arithmeticExpr && !isDate || this.inArithmeticExpr && !isDate) {
                  fc.setInArithmeticExpression(true);
               }

               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toDB2Select(to_sqs, from_sqs));
               if (tc != null && tc.getColumnName().equalsIgnoreCase("NVL")) {
                  colDataType = this.getReturnDataTypeForCoalesce(fc, "NVL", to_sqs, from_sqs);
                  if (colDataType != null && (colDataType.equalsIgnoreCase("timestamp") || colDataType.equalsIgnoreCase("date"))) {
                     isDate = true;
                     dateColsWithArithExpr.add(v_ce.get(v_ce.size() - 1));
                  }
               }
            } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
               if (arithmeticExpr) {
                  ((SelectColumn)this.columnExpression.elementAt(i)).setInArithmeticExpression(true);
               }

               ((SelectColumn)this.columnExpression.elementAt(i)).setCorrespondingTableColumn(this.corrTableColumn);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toDB2Select(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
               v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toDB2Select(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toDB2());
            } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
               v_ce.addElement(((WhereItem)this.columnExpression.elementAt(i)).toDB2Select(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
               v_ce.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toDB2Select(to_sqs, from_sqs));
            } else if (!(this.columnExpression.elementAt(i) instanceof String)) {
               v_ce.addElement(this.columnExpression.elementAt(i));
            } else {
               String s_ce = (String)this.columnExpression.elementAt(i);
               this.rightTableColProcessed = false;
               this.leftTableColProcessed = false;
               Vector args;
               Vector args;
               if (s_ce.equalsIgnoreCase("||")) {
                  if (!arithmeticExpr) {
                     if (this.columnExpression.elementAt(i - 1) instanceof TableColumn) {
                        tableCol = (TableColumn)this.columnExpression.elementAt(i - 1);
                        sourceDataType = null;
                        if (SwisSQLAPI.variableDatatypeMapping != null) {
                           sourceDataType = CastingUtil.getDataType((String)((String)SwisSQLAPI.variableDatatypeMapping.get(tableCol.getColumnName())));
                        }

                        if (sourceDataType != null && (sourceDataType.equalsIgnoreCase("CHAR") || sourceDataType.equalsIgnoreCase("VARCHAR"))) {
                           this.leftTableColProcessed = true;
                        }
                     }

                     if (this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                        tableCol = (TableColumn)this.columnExpression.elementAt(i + 1);
                        sourceDataType = null;
                        if (SwisSQLAPI.variableDatatypeMapping != null) {
                           sourceDataType = CastingUtil.getDataType((String)((String)SwisSQLAPI.variableDatatypeMapping.get(tableCol.getColumnName())));
                        }

                        if (sourceDataType != null && (sourceDataType.equalsIgnoreCase("CHAR") || sourceDataType.equalsIgnoreCase("VARCHAR"))) {
                           this.rightTableColProcessed = true;
                        }
                     }

                     if (i > 0 && this.columnExpression.elementAt(i - 1) instanceof FunctionCalls) {
                        fc = (FunctionCalls)this.columnExpression.get(i - 1);
                        if (fc.getFunctionName() != null && fc.getFunctionName().getColumnName().equalsIgnoreCase("CHAR")) {
                           FunctionCalls fc1;
                           if (this.columnExpression.size() > i + 1 && this.columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
                              fc1 = (FunctionCalls)this.columnExpression.get(i + 1);
                              if (fc1.getFunctionName() == null || !fc1.getFunctionName().getColumnName().equalsIgnoreCase("CHAR") || fc1.getFunctionArguments() == null || fc1.getFunctionArguments().size() != 1) {
                                 fn = new FunctionCalls();
                                 tc = new TableColumn();
                                 tc.setColumnName("CHAR");
                                 fn.setFunctionName(tc);
                                 args = new Vector();
                                 SelectColumn selCol = new SelectColumn();
                                 Vector selExp = new Vector();
                                 selExp.add(this.columnExpression.get(i + 1));
                                 selCol.setColumnExpression(selExp);
                                 args.add(selCol);
                                 fn.setFunctionArguments(args);
                                 this.columnExpression.set(i + 1, fn);
                              }
                           } else if (!this.rightTableColProcessed) {
                              fc1 = new FunctionCalls();
                              TableColumn tc = new TableColumn();
                              tc.setColumnName("CHAR");
                              fc1.setFunctionName(tc);
                              Vector v1 = new Vector();
                              SelectColumn selCol = new SelectColumn();
                              args = new Vector();
                              args.add(this.columnExpression.get(i + 1));
                              selCol.setColumnExpression(args);
                              v1.add(selCol);
                              fc1.setFunctionArguments(v1);
                              this.columnExpression.set(i + 1, fc1);
                           }
                        } else {
                           this.checkConcatenationString(v_ce, this.columnExpression, i, 3);
                        }
                     } else if (i == 1 && v_ce.elementAt(0).toString().trim().equals("CAST(NULL AS INT)")) {
                        v_ce.setElementAt("CAST(NULL AS CHAR)", 0);
                     } else {
                        this.checkConcatenationString(v_ce, this.columnExpression, i, 3);
                     }

                     v_ce.addElement(s_ce);
                  } else {
                     v_ce.addElement(") || CHAR(");
                  }
               } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                  v_ce.addElement("CURRENT DATE");
               } else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                  this.createPowerFunction(v_ce, this.columnExpression, i, true);
                  ++i;
               } else if (s_ce.equalsIgnoreCase("%")) {
                  v_ce.addElement("%");
                  modulus = 1;
               } else if (s_ce.trim().equals("DIV")) {
                  v_ce.addElement("/");
               } else if (s_ce.equalsIgnoreCase("::")) {
                  this.createCastFunction(v_ce, this.columnExpression, i);
                  Object object = v_ce.get(v_ce.size() - 1);
                  if (object instanceof FunctionCalls) {
                     v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toDB2Select(to_sqs, from_sqs));
                  }

                  ++i;
               } else {
                  if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                     throw new ConvertException();
                  }

                  if (s_ce.equalsIgnoreCase("=")) {
                     if (this.isSelectColFromUQS) {
                        if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                           sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
                        }

                        v_ce.setElementAt(" ", i - 1);
                     } else {
                        v_ce.addElement("=");
                     }
                  } else if (s_ce.equalsIgnoreCase("IS")) {
                     this.createDecodeFunction(v_ce, this.columnExpression, i);
                     fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
                     v_ce.setElementAt(fc.toDB2Select(to_sqs, from_sqs), v_ce.size() - 1);
                  } else if (s_ce.equalsIgnoreCase("NULL")) {
                     if (!this.insideDecodeFunction) {
                        if (this.targetDataTypeWithSize != null) {
                           v_ce.addElement("CAST(NULL AS " + this.targetDataTypeWithSize + ")");
                        } else if (this.targetDataType != null) {
                           v_ce.addElement("CAST(NULL AS " + this.targetDataType + ")");
                        } else {
                           v_ce.addElement("NULL");
                        }
                     } else {
                        v_ce.addElement("NULL");
                     }
                  } else {
                     boolean flag = false;
                     Object leftExpr;
                     if (s_ce.trim().equals("+")) {
                        leftExpr = null;
                        if (i - 1 >= 0) {
                           leftExpr = this.columnExpression.elementAt(i - 1);
                        }

                        rightExpr = this.columnExpression.elementAt(i + 1);
                        if (!(leftExpr instanceof String) && !(rightExpr instanceof String)) {
                           if (leftExpr instanceof FunctionCalls) {
                              FunctionCalls fc = (FunctionCalls)leftExpr;
                              tc = fc.getFunctionName();
                              args = fc.getFunctionArguments();
                              Object o2 = null;
                              if (args.size() >= 2) {
                                 o2 = args.elementAt(1);
                              }

                              if (o2 instanceof Datatype) {
                                 if (o2 instanceof CharacterClass) {
                                    flag = true;
                                    v_ce.addElement(" CONCAT ");
                                 }
                              } else if (tc != null && SwisSQLUtils.getFunctionReturnType(tc.getColumnName(), args).equalsIgnoreCase("string")) {
                                 flag = true;
                                 v_ce.addElement(" CONCAT ");
                              }
                           } else if (leftExpr instanceof TableColumn) {
                              tc = (TableColumn)leftExpr;
                              dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                              if (dataType != null) {
                                 dataType = dataType.toLowerCase();
                              }

                              if (dataType != null && dataType.indexOf("char") != -1) {
                                 flag = true;
                                 v_ce.addElement(" CONCAT ");
                              } else if (rightExpr instanceof FunctionCalls) {
                                 FunctionCalls fc = (FunctionCalls)rightExpr;
                                 TableColumn tc1 = fc.getFunctionName();
                                 Vector args = fc.getFunctionArguments();
                                 if (tc1 != null && SwisSQLUtils.getFunctionReturnType(tc1.getColumnName(), args).equalsIgnoreCase("string")) {
                                    flag = true;
                                    v_ce.addElement(" CONCAT ");
                                 }
                              }
                           }
                        } else if (leftExpr.toString().endsWith("'") || rightExpr.toString().startsWith("'")) {
                           flag = true;
                           v_ce.addElement(" CONCAT ");
                        }
                     }

                     if (isDate && s_ce != null && (s_ce.trim().equals("+") || s_ce.trim().equals("-")) && !openBraceisSet) {
                        boolean dateColWithNumCol = false;
                        if (this.columnExpression.elementAt(i + 1) != null && this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                           colDataType = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)this.columnExpression.elementAt(i + 1));
                           if (colDataType == null && SwisSQLAPI.variableDatatypeMapping != null) {
                              colDataType = (String)SwisSQLAPI.variableDatatypeMapping.get(((TableColumn)this.columnExpression.elementAt(i + 1)).getColumnName());
                           }

                           if (colDataType != null && (colDataType.toLowerCase().indexOf("num") != -1 || colDataType.toLowerCase().indexOf("int") != -1 || colDataType.toLowerCase().indexOf("decimal") != -1)) {
                              dateColWithNumCol = true;
                           }
                        }

                        if (!(this.columnExpression.elementAt(i + 1) instanceof TableColumn)) {
                           if (dateColsWithArithExpr.size() < 2) {
                              openBraceisSet = true;
                              v_ce.addElement(s_ce);
                              v_ce.addElement("(");
                           } else {
                              two_or_more_date_column = true;
                              v_ce.addElement(s_ce);
                           }
                        } else if (dateColWithNumCol) {
                           openBraceisSet = true;
                           v_ce.addElement(s_ce);
                           v_ce.addElement("(");
                        } else {
                           two_or_more_date_column = true;
                           v_ce.addElement(s_ce);
                        }
                     } else if (s_ce.trim().equals("/")) {
                        leftExpr = v_ce.lastElement();
                        fn = new FunctionCalls();
                        tc = new TableColumn();
                        tc.setColumnName("DECIMAL");
                        fn.setFunctionName(tc);
                        args = new Vector();
                        args.add(leftExpr);
                        fn.setFunctionArguments(args);
                        v_ce.setElementAt(fn, v_ce.size() - 1);
                        v_ce.addElement(s_ce);
                     } else if (!flag) {
                        if (this.targetDataType != null && (this.targetDataType.equalsIgnoreCase("decimal") || this.targetDataType.toLowerCase().indexOf("num") != -1 || this.targetDataType.indexOf("int") != -1) && s_ce.trim().startsWith("'")) {
                           v_ce.addElement(CastingUtil.getDB2DataTypeCastedString((String)null, this.targetDataType, s_ce.substring(1, s_ce.length() - 1)));
                        } else if (!arithmeticExpr && !s_ce.trim().equalsIgnoreCase("-")) {
                           if (!this.inArithmeticExpr) {
                              v_ce.addElement(CastingUtil.getDB2DataTypeCastedString((String)null, this.targetDataType, s_ce));
                           } else {
                              v_ce.addElement(CastingUtil.getDB2DataTypeCastedString((String)null, "DOUBLE", s_ce));
                           }
                        } else {
                           v_ce.addElement(s_ce);
                        }
                     }
                  }
               }

               modulus = handleModulusOperator(v_ce, modulus, i, 3);
            }
         }
      }

      if (!concatExpr && arithmeticExprAssignedToVar && !this.inArithmeticExpr) {
         v_ce.addElement("))");
      }

      if (concatExpr && arithmeticExpr) {
         v_ce.addElement(")");
      }

      if (!openBraceisSet && this.columnExpression.contains("-")) {
         for(m = 0; m < dateColsWithArithExpr.size(); ++m) {
            for(int l = 0; l < v_ce.size(); ++l) {
               if (v_ce.get(l).toString().equalsIgnoreCase(dateColsWithArithExpr.get(m).toString())) {
                  if (two_or_more_date_column) {
                     fn = new FunctionCalls();
                     tc = new TableColumn();
                     dataType = dateColsWithArithExpr.get(m).toString();
                     tc.setColumnName("DAYS(DATE(" + dataType + "))");
                     fn.setFunctionName(tc);
                     fn.setOpenBracesForFunctionNameRequired(false);
                     v_ce.removeElementAt(l);
                     v_ce.insertElementAt(fn, l);
                  } else {
                     fn = new FunctionCalls();
                     tc = new TableColumn();
                     dataType = dateColsWithArithExpr.get(m).toString();
                     tc.setColumnName("(YEAR(" + dataType + ")*365 + MONTH(" + dataType + ")*31 + DAY(" + dataType + ") + HOUR(" + dataType + ")/24.0 + MINUTE(" + dataType + ")/1440.0 + SECOND(" + dataType + ")/86400.0)");
                     fn.setFunctionName(tc);
                     fn.setOpenBracesForFunctionNameRequired(false);
                     v_ce.removeElementAt(l);
                     v_ce.insertElementAt(fn, l);
                  }
                  break;
               }
            }
         }
      }

      if (openBraceisSet) {
         v_ce.add(") DAYS");
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      sc.setIsAS(this.isAS);
      sc.setEndsWith(this.endsWith);
      if (sc != null && sc.getAliasName() == null) {
         if (this.aliasName != null && this.aliasName.charAt(0) == '\'') {
            sc.setAliasName(this.aliasName.replace('\'', '"'));
         } else {
            sc.setAliasName(this.aliasName);
         }
      }

      return sc;
   }

   private void addInDateColsIfPrevFnCall(Vector v_ce, ArrayList dateColsWithArithExpr) {
      int size = v_ce.size();
      if (size > 2) {
         Object obj = v_ce.get(size - 2);
         if (obj instanceof String) {
            String operator = (String)obj;
            if (operator.trim().equals("-")) {
               Object exprObj = v_ce.get(size - 3);
               if (exprObj instanceof FunctionCalls) {
                  dateColsWithArithExpr.add(exprObj);
               }
            }
         }
      }

   }

   public void checkConcatenationString(Vector v_ce, Vector columnExpression, int i, int database) {
      SelectColumn leftSideCastFunction = new SelectColumn();
      SelectColumn rightSideCastFunction = new SelectColumn();
      Vector leftColExp = new Vector();
      Vector rightColExp = new Vector();
      Vector leftSideFunctionArguments = new Vector();
      Vector rightSideFunctionArguments = new Vector();
      TableColumn leftSideTC = new TableColumn();
      FunctionCalls leftSideFC = new FunctionCalls();
      TableColumn rightSideTC = new TableColumn();
      FunctionCalls rightSideFC = new FunctionCalls();
      SelectColumn parentLeftSideCastFunction = new SelectColumn();
      Vector parentLeftColExp = new Vector();
      Vector parentLeftSideFunctionArguments = new Vector();
      TableColumn parentLeftSideTC = new TableColumn();
      FunctionCalls parentLeftSideFC = new FunctionCalls();
      SelectColumn parentRightSideCastFunction = new SelectColumn();
      Vector parentRightColExp = new Vector();
      Vector parentRightSideFunctionArguments = new Vector();
      TableColumn parentRightSideTC = new TableColumn();
      FunctionCalls parentRightSideFC = new FunctionCalls();
      if (database == 3) {
         leftSideTC.setColumnName("CHAR");
         parentLeftSideTC.setColumnName("RTRIM");
      } else {
         leftSideTC.setColumnName("CAST");
      }

      Object nextObject;
      if (i - 1 <= v_ce.size() - 1) {
         nextObject = v_ce.elementAt(i - 1);
         if ((!this.leftTableColProcessed || database != 3) && nextObject.toString().charAt(0) != '\'' && !nextObject.toString().toLowerCase().startsWith("cast") && !nextObject.toString().toLowerCase().startsWith("char(") && !nextObject.toString().toLowerCase().startsWith("rtrim(char(")) {
            leftColExp.add(nextObject);
            leftSideCastFunction.setColumnExpression(leftColExp);
            leftSideFunctionArguments.add(leftSideCastFunction);
            if (database == 2) {
               leftSideFunctionArguments.add("VARCHAR");
            }

            leftSideFC.setFunctionArguments(leftSideFunctionArguments);
            leftSideFC.setFunctionName(leftSideTC);
            if (database != 3) {
               leftSideFC.setAsDatatype("AS");
            }

            if (database == 3) {
               parentLeftColExp.add(leftSideFC);
               parentLeftSideCastFunction.setColumnExpression(parentLeftColExp);
               parentLeftSideFunctionArguments.add(parentLeftSideCastFunction);
               parentLeftSideFC.setFunctionArguments(parentLeftSideFunctionArguments);
               parentLeftSideFC.setFunctionName(parentLeftSideTC);
               v_ce.setElementAt(parentLeftSideFC, i - 1);
            } else {
               v_ce.setElementAt(leftSideFC, i - 1);
            }
         }
      }

      if (database == 3) {
         rightSideTC.setColumnName("CHAR");
         parentRightSideTC.setColumnName("RTRIM");
      } else {
         rightSideTC.setColumnName("CAST");
      }

      nextObject = columnExpression.elementAt(i + 1);
      if ((!this.rightTableColProcessed || database != 3) && nextObject.toString().charAt(0) != '\'' && !nextObject.toString().toLowerCase().startsWith("cast") && !nextObject.toString().toLowerCase().startsWith("char(") && !nextObject.toString().toLowerCase().startsWith("rtrim(char(")) {
         rightColExp.add(nextObject);
         rightSideCastFunction.setColumnExpression(rightColExp);
         rightSideFunctionArguments.add(rightSideCastFunction);
         if (database == 2) {
            rightSideFunctionArguments.add("VARCHAR");
         }

         rightSideFC.setFunctionArguments(rightSideFunctionArguments);
         rightSideFC.setFunctionName(rightSideTC);
         if (database != 3) {
            rightSideFC.setAsDatatype("AS");
         }

         if (database == 3) {
            parentRightColExp.add(rightSideFC);
            parentRightSideCastFunction.setColumnExpression(parentRightColExp);
            parentRightSideFunctionArguments.add(parentRightSideCastFunction);
            parentRightSideFC.setFunctionArguments(parentRightSideFunctionArguments);
            parentRightSideFC.setFunctionName(parentRightSideTC);
            columnExpression.setElementAt(parentRightSideFC, i + 1);
         } else {
            columnExpression.setElementAt(rightSideFC, i + 1);
         }
      }

   }

   public void checkConcatenationString(Vector v_ce, Vector columnExpression, int i, boolean bool) {
      String s_fce;
      String s_fce;
      FunctionCalls fc;
      TableColumn tc;
      if (v_ce.elementAt(i - 1) instanceof String) {
         s_fce = (String)v_ce.elementAt(i - 1);
         if (s_fce.charAt(0) != '\'' && s_fce.indexOf("cast") != 0) {
            s_fce = "CAST ( " + s_fce + " AS VARCHAR ) ";
            v_ce.setElementAt(s_fce, i - 1);
         }
      } else if (v_ce.elementAt(i - 1) instanceof FunctionCalls) {
         fc = (FunctionCalls)v_ce.elementAt(i - 1);
         if (fc.toString().indexOf("cast") != 0) {
            s_fce = "CAST ( " + fc.toString() + " AS VARCHAR ) ";
            v_ce.setElementAt(s_fce, i - 1);
         }
      } else if (v_ce.elementAt(i - 1) instanceof TableColumn) {
         tc = (TableColumn)v_ce.elementAt(i - 1);
         if (tc.toString().indexOf("cast") != 0 && tc.toString().charAt(0) != '\'') {
            s_fce = "CAST ( " + tc.toString() + " AS VARCHAR ) ";
            v_ce.setElementAt(s_fce, i - 1);
         }
      }

      if (columnExpression.elementAt(i + 1) instanceof String) {
         s_fce = (String)columnExpression.elementAt(i + 1);
         if (s_fce.charAt(0) != '\'' && s_fce.indexOf("cast") != 0) {
            s_fce = "CAST ( " + s_fce + " AS VARCHAR ) ";
            columnExpression.setElementAt(s_fce, i + 1);
         }
      } else if (columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
         fc = (FunctionCalls)columnExpression.elementAt(i + 1);
         if (fc.toString().indexOf("cast") != 0) {
            s_fce = "CAST ( " + fc.toString() + " AS VARCHAR ) ";
            columnExpression.setElementAt(s_fce, i + 1);
         }
      } else if (columnExpression.elementAt(i + 1) instanceof TableColumn) {
         tc = (TableColumn)columnExpression.elementAt(i + 1);
         if (tc.toString().indexOf("cast") != 0 && tc.toString().charAt(0) != '\'') {
            s_fce = "CAST ( " + tc.toString() + " AS VARCHAR ) ";
            columnExpression.setElementAt(s_fce, i + 1);
         }
      }

   }

   public SelectColumn toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass(this.commentObj);
      int sql_dialect = 0;
      int division = 0;
      int integer_division = 0;
      int div_index = 0;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      new String();
      Vector v_ce = new Vector();
      String[] keywords = null;
      Integer modulus = 0;
      if (SwisSQLUtils.getKeywords(1) != null) {
         keywords = (String[])SwisSQLUtils.getKeywords(1);
      }

      sc.setOpenBrace(this.OpenBrace);
      if (!this.isOrderItem && !from_sqs.isOracleLive()) {
         this.handleTableColumn(this.columnExpression, from_sqs, 1);
      }

      int cESize = this.columnExpression.size();

      for(int i = 0; i < cESize; ++i) {
         String second_arg;
         String s;
         Object vv;
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            TableColumn tc = (TableColumn)this.columnExpression.elementAt(i);
            if (this.fromUQS != null) {
               tc.setFromUQS(this.fromUQS);
            }

            tc.setObjectContext(this.context);
            if (tc.getColumnName() != null) {
               second_arg = tc.getColumnName();
               if (second_arg.equalsIgnoreCase("INCREMENT")) {
                  second_arg = "INCREMENT_BY";
               }

               tc.setColumnName(second_arg);
            }

            tc = tc.toOracleSelect(to_sqs, from_sqs);
            if (SwisSQLOptions.SetSybaseDoubleQuotedLiteralsToSingleQuotes && tc.getOwnerName() == null && tc.getTableName() == null) {
               second_arg = tc.getColumnName();
               if ((i != 0 || cESize <= 1 || !(this.columnExpression.elementAt(i + 1) instanceof String)) && second_arg.startsWith("\"")) {
                  second_arg = second_arg.replaceAll("'", "\"");
                  second_arg = "'" + second_arg.substring(1, second_arg.length() - 1) + "'";
                  tc.setColumnName(second_arg);
               }
            }

            v_ce.addElement(tc);
         } else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
            ((FunctionCalls)this.columnExpression.elementAt(i)).setObjectContext(this.context);
            FunctionCalls fcs = (FunctionCalls)this.columnExpression.elementAt(i);
            TableColumn tcol = fcs.getFunctionName();
            if (tcol != null && tcol.getTableName() != null && tcol.getTableName().equalsIgnoreCase("DBO")) {
               tcol.setTableName((String)null);
            }

            if (this.columnExpression.elementAt(i).toString().trim().toLowerCase().startsWith("user_name(")) {
               SelectQueryStatement userSQS = new SelectQueryStatement();
               SelectStatement userSS = new SelectStatement();
               userSS.setSelectClause("SELECT");
               Vector userSCV = new Vector();
               SelectColumn userSC = new SelectColumn();
               Vector colExpr = new Vector();
               TableColumn userTC = new TableColumn();
               userTC.setColumnName("USER");
               colExpr.add(userTC);
               userSC.setColumnExpression(colExpr);
               userSCV.add(userSC);
               userSS.setSelectItemList(userSCV);
               userSQS.setSelectStatement(userSS);
               FunctionCalls userFun = (FunctionCalls)this.columnExpression.elementAt(i);
               Vector userFunArgs = userFun.getFunctionArguments();
               if (userFunArgs != null && userFunArgs.size() == 1) {
                  WhereExpression userWe = new WhereExpression();
                  Vector userWIV = new Vector();
                  WhereItem userWI = new WhereItem();
                  WhereColumn userLWC = new WhereColumn();
                  Vector userLWColExpr = new Vector();
                  userLWColExpr.add("UID");
                  userLWC.setColumnExpression(userLWColExpr);
                  userWI.setLeftWhereExp(userLWC);
                  WhereColumn userRWC = new WhereColumn();
                  Vector userRWColExpr = new Vector();
                  userRWColExpr.add(userFunArgs.get(0));
                  userRWC.setColumnExpression(userRWColExpr);
                  userWI.setRightWhereExp(userRWC);
                  userWI.setOperator("=");
                  userWIV.add(userWI);
                  userWe.setWhereItem(userWIV);
                  userSQS.setWhereExpression(userWe);
               }

               String temp = userSQS.toOracleString();
               v_ce.addElement("(");
               v_ce.addElement(temp);
               v_ce.addElement(")");
               this.columnExpression.setElementAt(temp, i);
            } else {
               FunctionCalls fc = (FunctionCalls)this.columnExpression.elementAt(i);
               boolean isNtextFunction = false;
               if (fc.getFunctionName() != null && fc.getFunctionName().getColumnName().equalsIgnoreCase("CONVERT") && fc.getFunctionArguments().get(0) instanceof CharacterClass) {
                  CharacterClass characterclass = (CharacterClass)fc.getFunctionArguments().get(0);
                  String datatypeName = characterclass.getDatatypeName();
                  if (datatypeName.equalsIgnoreCase("NTEXT")) {
                     v_ce.addElement(fc.getFunctionArguments().get(1));
                     isNtextFunction = true;
                  }
               }

               if (!isNtextFunction) {
                  FunctionCalls temp = fc.toOracleSelect(to_sqs, from_sqs);
                  v_ce.addElement(temp);
               }
            }
         } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
            SelectColumn temp = ((SelectColumn)this.columnExpression.elementAt(i)).toOracleSelect(to_sqs, from_sqs);
            v_ce.addElement(temp);
         } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
            CaseStatement temp = ((CaseStatement)this.columnExpression.elementAt(i)).toOracleSelect(to_sqs, from_sqs);
            v_ce.addElement(temp);
         } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
            SelectQueryStatement temp = ((SelectQueryStatement)this.columnExpression.elementAt(i)).toOracle();
            temp.setObjectContext(this.context);
            v_ce.addElement(temp);
         } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
            v_ce.addElement(((WhereItem)this.columnExpression.elementAt(i)).toOracleSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
            v_ce.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toOracleSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof String) {
            String s_ce = (String)this.columnExpression.elementAt(i);
            if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().equalsIgnoreCase(".*")) {
               s_ce = CustomizeUtil.objectNamesToQuotedIdentifier(s_ce, keywords, (ModifiedObjectAttr)null, 1);
            }

            if (s_ce != null && (s_ce.startsWith("[") && s_ce.endsWith("]") || s_ce.startsWith("`") && s_ce.endsWith("`"))) {
               s_ce = s_ce.substring(1, s_ce.length() - 1).trim();
               if (SwisSQLOptions.retainQuotedIdentifierForOracle || s_ce.indexOf(32) != -1) {
                  s_ce = "\"" + s_ce + "\"";
               }
            }

            if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
               v_ce.addElement("TO_CHAR(SYSDATE,'HH:MI:SS')");
            } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
               v_ce.addElement("SYSDATE");
            } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
               v_ce.addElement("SYSTIMESTAMP");
            } else if (s_ce.equalsIgnoreCase("CURRENT")) {
               v_ce.addElement("SYSDATE");
            } else if (s_ce.equalsIgnoreCase("%")) {
               v_ce.addElement("%");
               modulus = 1;
            } else {
               String newStr;
               int h;
               if (s_ce.equalsIgnoreCase("&")) {
                  newStr = "";
                  if (i >= 1) {
                     if (this.columnExpression.elementAt(i - 1) instanceof SelectColumn) {
                        newStr = ((SelectColumn)this.columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs).toString();
                     } else if (this.columnExpression.elementAt(i - 1) instanceof FunctionCalls) {
                        newStr = ((FunctionCalls)this.columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs).toString();
                     } else {
                        for(h = i - 1; h >= 0; --h) {
                           s = null;
                           if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                              s = ((TableColumn)this.columnExpression.elementAt(h)).toOracleSelect(to_sqs, from_sqs).toString();
                           } else {
                              s = this.columnExpression.elementAt(h).toString();
                           }

                           if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ") || this.columnExpression.elementAt(h) instanceof SelectColumn) {
                              break;
                           }

                           newStr = newStr + this.columnExpression.elementAt(h).toString();
                        }
                     }
                  } else {
                     for(h = i - 1; h >= 0; --h) {
                        s = null;
                        if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                           s = ((TableColumn)this.columnExpression.elementAt(h)).toOracleSelect(to_sqs, from_sqs).toString();
                        } else {
                           s = this.columnExpression.elementAt(h).toString();
                        }

                        if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ") || this.columnExpression.elementAt(h) instanceof SelectColumn) {
                           break;
                        }

                        newStr = newStr + this.columnExpression.elementAt(h).toString();
                     }
                  }

                  second_arg = this.createBitAnd(newStr, this.columnExpression, i + 1, to_sqs, from_sqs);
                  v_ce.add(second_arg);
                  v_ce.setElementAt(" ", i - 1);
               } else if (s_ce.trim().startsWith("$")) {
                  try {
                     newStr = s_ce.substring(1);
                     float numericValue = Float.parseFloat(newStr);
                     v_ce.addElement(newStr);
                  } catch (NumberFormatException var31) {
                     v_ce.addElement(s_ce);
                  }
               } else if (s_ce.equalsIgnoreCase("::")) {
                  this.createCastFunction(v_ce, this.columnExpression, i);
                  vv = v_ce.get(v_ce.size() - 1);
                  if (vv instanceof FunctionCalls) {
                     v_ce.set(v_ce.size() - 1, ((FunctionCalls)vv).toOracleSelect(to_sqs, from_sqs));
                  }

                  ++i;
               } else if (s_ce.equalsIgnoreCase("**")) {
                  this.createPowerFunction(v_ce, this.columnExpression, i, true);
                  ++i;
               } else if (s_ce.equalsIgnoreCase("|")) {
                  newStr = "";
                  if (i >= 1) {
                     if (this.columnExpression.elementAt(i - 1) instanceof SelectColumn) {
                        newStr = ((SelectColumn)this.columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs).toString();
                     } else {
                        for(h = i - 1; h >= 0; --h) {
                           s = null;
                           if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                              s = ((TableColumn)this.columnExpression.elementAt(h)).toOracleSelect(to_sqs, from_sqs).toString();
                           } else {
                              s = this.columnExpression.elementAt(h).toString();
                           }

                           if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ") || this.columnExpression.elementAt(h) instanceof SelectColumn) {
                              break;
                           }

                           newStr = newStr + this.columnExpression.elementAt(h).toString();
                        }
                     }
                  } else {
                     for(h = i - 1; h >= 0; --h) {
                        s = null;
                        if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                           s = ((TableColumn)this.columnExpression.elementAt(h)).toOracleSelect(to_sqs, from_sqs).toString();
                        } else {
                           s = this.columnExpression.elementAt(h).toString();
                        }

                        if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ") || this.columnExpression.elementAt(h) instanceof SelectColumn) {
                           break;
                        }

                        newStr = newStr + this.columnExpression.elementAt(h).toString();
                     }
                  }

                  second_arg = this.createBitOR(newStr, this.columnExpression, i + 1, to_sqs, from_sqs);
                  v_ce.add(second_arg);
                  v_ce.setElementAt(" ", i - 1);
               } else if (s_ce.equalsIgnoreCase("~")) {
                  v_ce.setElementAt(" ", i);
                  vv = v_ce.get(i + 1);
                  if (vv instanceof TableColumn) {
                     second_arg = ((TableColumn)v_ce.get(i + 1)).toOracleSelect(to_sqs, from_sqs).toString();
                     v_ce.setElementAt("((0 - " + second_arg + ") + 1)", i + 1);
                  }

                  if (vv instanceof SelectColumn) {
                     second_arg = ((SelectColumn)v_ce.get(i + 1)).toOracleSelect(to_sqs, from_sqs).toString();
                     v_ce.setElementAt("((0 - " + second_arg + ") + 1)", i + 1);
                  }
               } else if (s_ce.equalsIgnoreCase("^")) {
                  newStr = "";
                  if (i >= 1) {
                     if (this.columnExpression.elementAt(i - 1) instanceof SelectColumn) {
                        newStr = ((SelectColumn)this.columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs).toString();
                     } else {
                        for(h = i - 1; h >= 0; --h) {
                           s = null;
                           if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                              s = ((TableColumn)this.columnExpression.elementAt(h)).toOracleSelect(to_sqs, from_sqs).toString();
                           } else {
                              s = this.columnExpression.elementAt(h).toString();
                           }

                           if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ") || this.columnExpression.elementAt(h) instanceof SelectColumn) {
                              break;
                           }

                           newStr = newStr + this.columnExpression.elementAt(h).toString();
                        }
                     }
                  } else {
                     for(h = i - 1; h >= 0; --h) {
                        s = null;
                        if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                           s = ((TableColumn)this.columnExpression.elementAt(h)).toOracleSelect(to_sqs, from_sqs).toString();
                        } else {
                           s = this.columnExpression.elementAt(h).toString();
                        }

                        if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ") || this.columnExpression.elementAt(h) instanceof SelectColumn) {
                           break;
                        }

                        newStr = newStr + this.columnExpression.elementAt(h).toString();
                     }
                  }

                  second_arg = this.createBitXOR(newStr, this.columnExpression, i + 1, to_sqs, from_sqs);
                  v_ce.add(second_arg);
                  v_ce.setElementAt(" ", i - 1);
               } else if (!s_ce.equalsIgnoreCase(">") && !s_ce.equalsIgnoreCase("<")) {
                  if (s_ce.equalsIgnoreCase("IS")) {
                     this.createDecodeFunction(v_ce, this.columnExpression, i);
                  } else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                     this.createDecodeFunction(v_ce, this.columnExpression, i);
                  } else if (s_ce.equalsIgnoreCase("=")) {
                     if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                        sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
                     }

                     v_ce.setElementAt(" ", i - 1);
                  } else if (s_ce.startsWith("@")) {
                     v_ce.addElement(":" + s_ce.substring(1));
                  } else if (!s_ce.startsWith("0x") && !s_ce.startsWith("0X")) {
                     if (!s_ce.equalsIgnoreCase("+") || (i - 1 < 0 || !this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'-") && !this.columnExpression.elementAt(i - 1).toString().trim().endsWith("-'")) && (i + 1 >= this.columnExpression.size() || !this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'-") && !this.columnExpression.elementAt(i + 1).toString().trim().endsWith("-'"))) {
                        if (!s_ce.equalsIgnoreCase("+")) {
                           if (s_ce.equalsIgnoreCase("CONCAT")) {
                              v_ce.addElement("||");
                           } else if (s_ce.trim().equals("/")) {
                              v_ce.addElement("/");
                              division = 1;
                           } else if (s_ce.trim().equalsIgnoreCase("DIV")) {
                              v_ce.addElement("/");
                              integer_division = 1;
                              div_index = v_ce.size() - 1;
                           } else if (!s_ce.trim().startsWith("--") && !s_ce.trim().startsWith("/*")) {
                              v_ce.addElement(s_ce);
                           } else {
                              CommentClass cm = new CommentClass();
                              cm.setComment(s_ce);
                              sc.setCommentClass(cm);
                           }
                        } else {
                           newStr = null;
                           if (i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                              newStr = this.columnExpression.elementAt(i - 1).toString();
                              if (!newStr.equals("''") && StringFunctions.replaceAll("", " ", newStr.substring(1, newStr.length() - 1)).length() != 0) {
                                 if (i + 1 < this.columnExpression.size() && (this.columnExpression.elementAt(i + 1) instanceof TableColumn || this.columnExpression.elementAt(i + 1) instanceof FunctionCalls || this.columnExpression.elementAt(i + 1) instanceof SelectColumn)) {
                                    v_ce.addElement("||");
                                 } else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1) instanceof String && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'")) {
                                    v_ce.addElement("||");
                                 } else if (from_sqs != null) {
                                    if (from_sqs.getFromClause() == null && i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'")) {
                                       v_ce.addElement("||");
                                    } else {
                                       v_ce.addElement(s_ce);
                                    }
                                 } else {
                                    v_ce.addElement(s_ce);
                                 }
                              } else {
                                 v_ce.addElement("||");
                              }
                           } else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'")) {
                              newStr = this.columnExpression.elementAt(i + 1).toString();
                              if (!newStr.equals("''") && StringFunctions.replaceAll("", " ", newStr.substring(1, newStr.length() - 1)).length() != 0) {
                                 if (i - 1 < 0 || !(this.columnExpression.elementAt(i - 1) instanceof TableColumn) && !(this.columnExpression.elementAt(i - 1) instanceof FunctionCalls) && !(this.columnExpression.elementAt(i + 1) instanceof SelectColumn)) {
                                    v_ce.addElement(s_ce);
                                 } else {
                                    v_ce.addElement("||");
                                 }
                              } else {
                                 v_ce.addElement("||");
                              }
                           } else if (i - 1 >= 0 && this.columnExpression.elementAt(i - 1) instanceof TableColumn) {
                              this.addConcatenationSymbol(from_sqs, v_ce, s_ce, (TableColumn)this.columnExpression.elementAt(i - 1));
                           } else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                              this.addConcatenationSymbol(from_sqs, v_ce, s_ce, (TableColumn)this.columnExpression.elementAt(i + 1));
                           } else if (i - 1 >= 0 && this.columnExpression.elementAt(i - 1) instanceof FunctionCalls) {
                              this.addConcatenationSymbol(from_sqs, v_ce, s_ce, (FunctionCalls)this.columnExpression.elementAt(i - 1));
                           } else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
                              this.addConcatenationSymbol(from_sqs, v_ce, s_ce, (FunctionCalls)this.columnExpression.elementAt(i + 1));
                           } else {
                              v_ce.addElement(s_ce);
                           }
                        }
                     } else {
                        v_ce.addElement("||");
                     }
                  } else {
                     newStr = s_ce.substring(2);
                     v_ce.addElement("HEX_TO_NUMBER('" + newStr + "')");
                  }
               } else {
                  newStr = "";
                  second_arg = "";
                  if (this.columnExpression.elementAt(i - 1) instanceof SelectColumn) {
                     newStr = ((SelectColumn)this.columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs).toString();
                  } else if (this.columnExpression.elementAt(i - 1) instanceof FunctionCalls) {
                     newStr = ((FunctionCalls)this.columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs).toString();
                  } else if (this.columnExpression.elementAt(i - 1) instanceof TableColumn) {
                     newStr = ((TableColumn)this.columnExpression.elementAt(i - 1)).toOracleSelect(to_sqs, from_sqs).toString();
                  }

                  if (this.columnExpression.elementAt(i + 1) instanceof SelectColumn) {
                     second_arg = ((SelectColumn)this.columnExpression.elementAt(i + 1)).toOracleSelect(to_sqs, from_sqs).toString();
                  } else if (this.columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
                     second_arg = ((FunctionCalls)this.columnExpression.elementAt(i + 1)).toOracleSelect(to_sqs, from_sqs).toString();
                  } else if (this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                     newStr = ((TableColumn)this.columnExpression.elementAt(i + 1)).toOracleSelect(to_sqs, from_sqs).toString();
                  }

                  s = "CASE WHEN " + newStr + " " + s_ce + " " + second_arg + " THEN 1 ELSE 0 END";
                  v_ce.add(s);
                  v_ce.setElementAt(" ", i - 1);
                  this.columnExpression.clear();
               }
            }
         } else {
            v_ce.addElement(this.columnExpression.elementAt(i));
         }

         if (division > 0) {
            if (division > 2) {
               vv = v_ce.get(i);
               second_arg = "-" + vv.toString();
               v_ce.remove(i);
               v_ce.remove(i - 1);
               v_ce.add("");
               s = modifyDividerValue(vv, second_arg, 1, true);
               v_ce.add(s);
               division = 0;
            } else if (division > 1) {
               vv = v_ce.get(i);
               second_arg = vv.toString();
               if (second_arg.equals("-")) {
                  ++division;
               } else {
                  v_ce.remove(i);
                  s = modifyDividerValue(vv, second_arg, 1, true);
                  v_ce.add(s);
                  division = 0;
               }
            } else {
               ++division;
            }
         }

         modulus = handleModulusOperator(v_ce, modulus, i, 1);
         integer_division = handleIntegerDivision(v_ce, integer_division, div_index, from_sqs, to_sqs);
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      sc.setIsAS(this.isAS);
      sc.setEndsWith(this.endsWith);
      if (sc != null && sc.getAliasName() == null) {
         this.aliasName = CustomizeUtil.objectNamesToQuotedIdentifier(this.aliasName, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
         if (this.aliasName != null && this.aliasName.charAt(0) == '\'') {
            sc.setAliasName(this.aliasName.replace('\'', '"'));
         } else if (this.aliasName != null && this.aliasName.startsWith("[")) {
            String tempAlias = this.aliasName.substring(1, this.aliasName.length() - 1);
            if (!SwisSQLOptions.retainQuotedIdentifierForOracle && tempAlias.indexOf(" ") == -1) {
               sc.setAliasName(tempAlias);
            } else {
               sc.setAliasName("\"" + tempAlias + "\"");
            }
         } else {
            if (from_sqs != null && from_sqs.isDenodo() && this.aliasName != null && !GeneralUtil.isItEnclosedTblCol(this.aliasName)) {
               this.aliasName = "\"" + this.aliasName + "\"";
            }

            sc.setAliasName(this.aliasName);
         }
      }

      return sc;
   }

   public void convertOrdinalNumberToColumn(SelectQueryStatement from_sqs, Vector v_ce) {
      String ordinalNumber = (String)v_ce.elementAt(0);
      if (ordinalNumber.matches("^[1-9][0-9]*")) {
         Vector tc = ((SelectColumn)from_sqs.getSelectStatement().getSelectItemList().elementAt(Integer.parseInt(ordinalNumber) - 1)).getColumnExpression();
         this.setColumnExpression(tc);
      }

   }

   private boolean isDecimal(String input) {
      try {
         double a = Double.parseDouble(input);
         return true;
      } catch (Exception var4) {
         return false;
      }
   }

   private void handleTableColumn(Vector colExpr, SelectQueryStatement from_sqs, int target) {
      for(int i = 0; i < colExpr.size(); ++i) {
         if (colExpr.elementAt(i) instanceof TableColumn && (i + 1 >= colExpr.size() || !colExpr.elementAt(i + 1).toString().equalsIgnoreCase("=") || !colExpr.elementAt(i).toString().startsWith("\""))) {
            TableColumn tc = (TableColumn)colExpr.elementAt(i);
            if (tc.getColumnName() != null && tc.getTableName() == null) {
               String columnName = tc.getColumnName();
               if (columnName.trim().length() > 0) {
                  if (columnName.equalsIgnoreCase("\"\"")) {
                     if (target == 10) {
                        if (SwisSQLOptions.fromSybase) {
                           tc.setColumnName("' '");
                        } else {
                           tc.setColumnName("''");
                        }
                     } else {
                        tc.setColumnName("''");
                     }
                  } else if (columnName.startsWith("\"")) {
                     boolean modify = true;
                     FromClause fc = null;
                     if (from_sqs != null) {
                        fc = from_sqs.getFromClause();
                     } else if (this.fromUQS != null) {
                        fc = new FromClause();
                        Vector newFromItems = new Vector();
                        fc.setFromItemList(newFromItems);
                        TableExpression tExpr = this.fromUQS.getTableExpression();
                        if (tExpr != null) {
                           ArrayList tabClauseList = tExpr.getTableClauseList();
                           if (tabClauseList != null) {
                              for(int j = 0; j < tabClauseList.size(); ++j) {
                                 Object obj = tabClauseList.get(j);
                                 if (obj instanceof TableClause) {
                                    TableObject tabObj = ((TableClause)obj).getTableObject();
                                    String tableName = tabObj.getTableName();
                                    FromTable fromTab = new FromTable();
                                    fromTab.setTableName(tableName);
                                    newFromItems.add(fromTab);
                                 }
                              }
                           }
                        }

                        FromClause uqsFC = this.fromUQS.getFromClause();
                        if (uqsFC != null) {
                           Vector fromItems = uqsFC.getFromItemList();
                           if (fromItems != null) {
                              for(int j = 0; j < fromItems.size(); ++j) {
                                 newFromItems.add(fromItems.get(j));
                              }
                           }
                        }
                     } else {
                        modify = false;
                     }

                     if (fc != null) {
                        String tempCol = columnName.trim().substring(1, columnName.trim().length() - 1);
                        Vector fromItems = fc.getFromItemList();
                        if (fromItems != null) {
                           for(int j = 0; j < fromItems.size(); ++j) {
                              Object obj = fromItems.get(j);
                              if (obj instanceof FromTable) {
                                 obj = ((FromTable)obj).getTableName();
                                 if (obj instanceof String) {
                                    String tableName = (String)obj;
                                    int index = tableName.lastIndexOf(".");
                                    if (index != -1) {
                                       tableName = tableName.substring(index + 1);
                                    }

                                    if (tableName.startsWith("\"") || tableName.startsWith("[") || tableName.startsWith("`")) {
                                       tableName = tableName.substring(1, tableName.length() - 1);
                                    }

                                    ArrayList colList = (ArrayList)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getTableColumnListMetadata(), tableName);
                                    if (colList == null) {
                                       break;
                                    }

                                    if (CastingUtil.ContainsIgnoreCase(colList, tempCol)) {
                                       modify = false;
                                       break;
                                    }
                                 }
                              }
                           }
                        }
                     }

                     if (modify) {
                        columnName = StringFunctions.replaceFirst("'", "\"", columnName);
                        columnName = columnName.trim().substring(0, columnName.trim().length() - 1) + "'";
                        tc.setColumnName(columnName);
                     }
                  }
               }
            }
         }
      }

   }

   private void addConcatenationSymbol(SelectQueryStatement from_sqs, Vector v_ce, String s_ce, TableColumn tc) {
      String dtype = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
      String newStr = tc.getColumnName();
      if (dtype != null && dtype.toLowerCase().indexOf("char") != -1) {
         v_ce.addElement("||");
      } else if (dtype == null && newStr.startsWith("\"") && newStr.endsWith("\"")) {
         String name_only = newStr.substring(1, newStr.length() - 1);
         if (name_only.trim().equals("")) {
            v_ce.addElement("||");
         } else {
            v_ce.addElement(s_ce);
         }
      } else {
         v_ce.addElement(s_ce);
      }

   }

   private void addConcatenationSymbol(SelectQueryStatement from_sqs, Vector v_ce, String s_ce, FunctionCalls fc) {
      String fnName = fc.getFunctionName().getColumnName().toLowerCase();
      if (!fnName.equalsIgnoreCase("suser_sname") && !fnName.equalsIgnoreCase("user_name") && !fnName.equalsIgnoreCase("to_char") && !fnName.equalsIgnoreCase("substr") && !fnName.equalsIgnoreCase("app_name") && !fnName.equalsIgnoreCase("sys_context")) {
         v_ce.addElement(s_ce);
      } else {
         v_ce.addElement("||");
      }

   }

   private String createBitAnd(String first_arg, Vector sec_arg, int startIndex, SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String bitand = "";
      String sec_arg_str = "";
      sec_arg.elementAt(startIndex);
      int i = startIndex;

      while(true) {
         label76: {
            if (i < sec_arg.size()) {
               Object data = sec_arg.elementAt(i);
               if (data instanceof String) {
                  String str = data.toString();
                  String f_arg;
                  if (str.equals("&")) {
                     f_arg = "BITAND(" + first_arg + "," + sec_arg_str + ")";
                     sec_arg.setElementAt(" ", i - 1);
                     sec_arg.setElementAt(" ", i);
                     return this.createBitAnd(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                  }

                  if (str.equals("|")) {
                     f_arg = "BITAND(" + first_arg + "," + sec_arg_str + ")";
                     sec_arg.setElementAt(" ", i - 1);
                     sec_arg.setElementAt(" ", i);
                     return this.createBitOR(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                  }

                  if (str.equals("^")) {
                     f_arg = "BITAND(" + first_arg + "," + sec_arg_str + ")";
                     sec_arg.setElementAt(" ", i - 1);
                     sec_arg.setElementAt(" ", i);
                     return this.createBitXOR(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                  }

                  if (str.equals("~")) {
                     f_arg = "BITAND(" + first_arg + "," + sec_arg + ")";
                     sec_arg.setElementAt(" ", i - 1);
                     sec_arg.setElementAt(" ", i);
                     Object ne = sec_arg.get(i + 1);
                     if (ne instanceof String) {
                        String ss = ne.toString();
                        return "BITAND(" + first_arg + ",((0-" + ne + ")+1))";
                     }

                     if (ne instanceof TableColumn) {
                        TableColumn col = ((TableColumn)ne).toOracleSelect(to_sqs, from_sqs);
                        sec_arg_str = sec_arg_str + col.toString();
                        return "BITAND(" + first_arg + ",((0-" + sec_arg_str + ")+1))";
                     }
                     break label76;
                  }

                  if (!str.trim().equals("+") && !str.trim().equals("-") && !str.trim().equals("*") && !str.trim().equals("/") && !str.trim().equals("%") && !str.trim().equals("**") && !str.trim().equals("^") && !str.trim().equals("|") && !str.trim().equals("&")) {
                     sec_arg_str = sec_arg_str + str;
                     break label76;
                  }
               } else {
                  if (data instanceof TableColumn) {
                     TableColumn col = ((TableColumn)data).toOracleSelect(to_sqs, from_sqs);
                     sec_arg_str = sec_arg_str + col.toString();
                     break label76;
                  }

                  if (data instanceof FunctionCalls) {
                     FunctionCalls col = ((FunctionCalls)data).toOracleSelect(to_sqs, from_sqs);
                     sec_arg_str = sec_arg_str + col.toString();
                     break label76;
                  }

                  if (data instanceof SelectQueryStatement) {
                     SelectQueryStatement sqs = ((SelectQueryStatement)data).toOracle();
                     sec_arg_str = sec_arg_str + sqs.toString();
                     break label76;
                  }

                  if (data instanceof CaseStatement) {
                     CaseStatement cs = ((CaseStatement)data).toOracleSelect(to_sqs, from_sqs);
                     sec_arg_str = sec_arg_str + cs.toString();
                     break label76;
                  }

                  if (data instanceof SelectColumn) {
                     SelectColumn sel = ((SelectColumn)data).toOracleSelect(to_sqs, from_sqs);
                     sec_arg_str = sec_arg_str + sel.toString();
                     break label76;
                  }
               }
            }

            sec_arg.setElementAt(" ", startIndex - 1);
            this.columnExpression.setElementAt(" ", startIndex);
            bitand = bitand + "BITAND(" + first_arg + "," + sec_arg_str + ")";
            return bitand;
         }

         ++i;
      }
   }

   private String createBitXOR(String first_arg, Vector sec_arg, int startIndex, SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String bitxor = "";
      String sec_arg_str = "";
      sec_arg.elementAt(startIndex);
      int i = startIndex;

      while(true) {
         label76: {
            if (i < sec_arg.size()) {
               Object data = sec_arg.elementAt(i);
               if (data instanceof String) {
                  String str = data.toString();
                  String f_arg;
                  if (str.equals("^")) {
                     f_arg = "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + ")*2)";
                     sec_arg.setElementAt(" ", i - 1);
                     sec_arg.setElementAt(" ", i);
                     return this.createBitXOR(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                  }

                  if (str.equals("|")) {
                     f_arg = "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + ")*2)";
                     sec_arg.setElementAt(" ", i - 1);
                     sec_arg.setElementAt(" ", i);
                     return this.createBitOR(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                  }

                  if (str.equals("&")) {
                     f_arg = "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + ")*2)";
                     sec_arg.setElementAt(" ", i - 1);
                     sec_arg.setElementAt(" ", i);
                     return this.createBitAnd(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                  }

                  if (str.equals("~")) {
                     f_arg = "BITAND(" + first_arg + "," + sec_arg + ")";
                     sec_arg.setElementAt(" ", i - 1);
                     sec_arg.setElementAt(" ", i);
                     Object ne = sec_arg.get(i + 1);
                     if (ne instanceof String) {
                        String ss = ne.toString();
                        sec_arg_str = "((0 - " + ne + ") + 1)";
                        return first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))*2";
                     }

                     if (ne instanceof TableColumn) {
                        TableColumn col = ((TableColumn)ne).toOracleSelect(to_sqs, from_sqs);
                        sec_arg_str = "((0 - " + sec_arg_str + col.toString() + ") + 1";
                        return first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))*2";
                     }
                     break label76;
                  }

                  if (!str.trim().equals("+") && !str.trim().equals("-") && !str.trim().equals("*") && !str.trim().equals("/") && !str.trim().equals("%") && !str.trim().equals("**") && !str.trim().equals("^") && !str.trim().equals("|") && !str.trim().equals("&")) {
                     sec_arg_str = sec_arg_str + str;
                     break label76;
                  }
               } else {
                  if (data instanceof TableColumn) {
                     TableColumn col = ((TableColumn)data).toOracleSelect(to_sqs, from_sqs);
                     sec_arg_str = sec_arg_str + col.toString();
                     break label76;
                  }

                  if (data instanceof FunctionCalls) {
                     FunctionCalls col = ((FunctionCalls)data).toOracleSelect(to_sqs, from_sqs);
                     sec_arg_str = sec_arg_str + col.toString();
                     break label76;
                  }

                  if (data instanceof SelectQueryStatement) {
                     SelectQueryStatement sqs = ((SelectQueryStatement)data).toOracle();
                     sec_arg_str = sec_arg_str + sqs.toString();
                     break label76;
                  }

                  if (data instanceof CaseStatement) {
                     CaseStatement cs = ((CaseStatement)data).toOracleSelect(to_sqs, from_sqs);
                     sec_arg_str = sec_arg_str + cs.toString();
                     break label76;
                  }

                  if (data instanceof SelectColumn) {
                     SelectColumn sel = ((SelectColumn)data).toOracleSelect(to_sqs, from_sqs);
                     sec_arg_str = sec_arg_str + sel.toString();
                     break label76;
                  }
               }
            }

            sec_arg.setElementAt(" ", startIndex - 1);
            this.columnExpression.setElementAt(" ", startIndex);
            bitxor = bitxor + "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + ")*2)";
            return bitxor;
         }

         ++i;
      }
   }

   private String createBitOR(String first_arg, Vector sec_arg, int startIndex, SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String bitor = "";
      String sec_arg_str = "";
      sec_arg.elementAt(startIndex);
      int i = startIndex;

      while(true) {
         label76: {
            if (i < sec_arg.size()) {
               Object data = sec_arg.elementAt(i);
               if (data instanceof String) {
                  String str = data.toString();
                  String f_arg;
                  if (str.equals("|")) {
                     f_arg = "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
                     sec_arg.setElementAt(" ", i - 1);
                     sec_arg.setElementAt(" ", i);
                     return this.createBitOR(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                  }

                  if (str.equals("^")) {
                     f_arg = "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
                     sec_arg.setElementAt(" ", i - 1);
                     sec_arg.setElementAt(" ", i);
                     return this.createBitXOR(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                  }

                  if (str.equals("&")) {
                     f_arg = "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
                     sec_arg.setElementAt(" ", i - 1);
                     sec_arg.setElementAt(" ", i);
                     return this.createBitAnd(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                  }

                  if (str.equals("~")) {
                     sec_arg.setElementAt(" ", i - 1);
                     sec_arg.setElementAt(" ", i);
                     Object ne = sec_arg.get(i + 1);
                     if (ne instanceof String) {
                        String ss = ne.toString();
                        sec_arg_str = "((0 - " + ne + ") + 1)";
                        return first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
                     }

                     if (ne instanceof TableColumn) {
                        TableColumn col = ((TableColumn)ne).toOracleSelect(to_sqs, from_sqs);
                        sec_arg_str = "((0 - " + sec_arg_str + col.toString() + ") + 1";
                        return first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
                     }
                     break label76;
                  }

                  if (!str.trim().equals("+") && !str.trim().equals("-") && !str.trim().equals("*") && !str.trim().equals("/") && !str.trim().equals("%") && !str.trim().equals("**") && !str.trim().equals("^") && !str.trim().equals("|") && !str.trim().equals("&")) {
                     sec_arg_str = sec_arg_str + str;
                     break label76;
                  }
               } else {
                  if (data instanceof TableColumn) {
                     TableColumn col = ((TableColumn)data).toOracleSelect(to_sqs, from_sqs);
                     sec_arg_str = sec_arg_str + col.toString();
                     break label76;
                  }

                  if (data instanceof FunctionCalls) {
                     FunctionCalls col = ((FunctionCalls)data).toOracleSelect(to_sqs, from_sqs);
                     sec_arg_str = sec_arg_str + col.toString();
                     break label76;
                  }

                  if (data instanceof SelectQueryStatement) {
                     SelectQueryStatement sqs = ((SelectQueryStatement)data).toOracle();
                     sec_arg_str = sec_arg_str + sqs.toString();
                     break label76;
                  }

                  if (data instanceof CaseStatement) {
                     CaseStatement cs = ((CaseStatement)data).toOracleSelect(to_sqs, from_sqs);
                     sec_arg_str = sec_arg_str + cs.toString();
                     break label76;
                  }

                  if (data instanceof SelectColumn) {
                     SelectColumn sel = ((SelectColumn)data).toOracleSelect(to_sqs, from_sqs);
                     sec_arg_str = sec_arg_str + sel.toString();
                     break label76;
                  }
               }
            }

            sec_arg.setElementAt(" ", startIndex - 1);
            this.columnExpression.setElementAt(" ", startIndex);
            bitor = bitor + "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
            return bitor;
         }

         ++i;
      }
   }

   public SelectColumn toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass(this.commentObj);
      Integer modulus = 0;
      int integer_division = 0;
      int div_index = 0;
      Vector v_ce = new Vector();
      sc.setOpenBrace(this.OpenBrace);

      for(int i = 0; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
            v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
            v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
            v_ce.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
            v_ce.addElement(((WhereItem)this.columnExpression.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
            v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
            v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toInformix());
         } else if (this.columnExpression.elementAt(i) instanceof Token) {
            String tokenStr = this.columnExpression.elementAt(i).toString().trim();
            if (tokenStr.startsWith("/*") && tokenStr.endsWith("*/")) {
               v_ce.addElement("");
            }
         } else if (this.columnExpression.elementAt(i) instanceof String) {
            String s_ce = (String)this.columnExpression.elementAt(i);
            if (s_ce.equalsIgnoreCase("%")) {
               v_ce.addElement("%");
               modulus = 1;
            } else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
               this.createPowerFunction(v_ce, this.columnExpression, i, false);
               ++i;
            } else if (s_ce.equalsIgnoreCase("IS")) {
               this.createDecodeFunction(v_ce, this.columnExpression, i);
            } else if (s_ce.equalsIgnoreCase("=")) {
               if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                  sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
               }

               v_ce.setElementAt(" ", i - 1);
            } else if (s_ce.trim().equals("DIV")) {
               v_ce.addElement("/");
               integer_division = 1;
               div_index = v_ce.size() - 1;
            } else if (!s_ce.trim().startsWith("/*") && !s_ce.trim().endsWith("*/")) {
               v_ce.addElement(s_ce);
            } else {
               v_ce.addElement("");
            }
         } else {
            v_ce.addElement(this.columnExpression.elementAt(i));
         }

         modulus = handleModulusOperator(v_ce, modulus, i, 6);
         integer_division = handleIntegerDivision(v_ce, integer_division, div_index, from_sqs, to_sqs);
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      sc.setIsAS(this.isAS);
      sc.setEndsWith(this.endsWith);
      if (sc.getAliasName() == null) {
         if (this.aliasName != null && this.aliasName.charAt(0) == '\'') {
            sc.setAliasName(this.aliasName.replace('\'', ' '));
         } else {
            sc.setAliasName(this.aliasName);
         }
      }

      return sc;
   }

   public static Integer handleModulusOperator(Vector v_ce, Integer modulus, int index, int dbtype) {
      if (modulus > 0) {
         Object obj;
         String val;
         if (modulus > 2) {
            obj = v_ce.get(index - 2);
            val = "-" + obj.toString();
            v_ce.setElementAt(val, index - 2);
            createModFunction(v_ce, dbtype);
            modulus = 0;
         } else if (modulus > 1) {
            obj = v_ce.get(index - 1);
            val = obj.toString();
            if (val.equals("-")) {
               v_ce.remove(index - 1);
               modulus = modulus + 1;
            } else {
               createModFunction(v_ce, dbtype);
               modulus = 0;
            }
         } else {
            v_ce.remove(index);
            modulus = modulus + 1;
         }
      }

      return modulus;
   }

   public static void createModFunction(Vector v_ce, int dbtype) {
      boolean isSqlite = dbtype == 18;
      SelectColumn sc_firstarg = new SelectColumn();
      SelectColumn sc_secondarg = new SelectColumn();
      Vector v_farg = new Vector();
      TableColumn tc = new TableColumn();
      FunctionCalls fc = new FunctionCalls();
      Vector vec_firstarg = new Vector();
      Vector vec_secondarg = new Vector();
      tc.setColumnName("MOD");
      fc.setFunctionName(tc);
      int v_ceSize = v_ce.size();
      vec_firstarg.addElement(v_ce.get(v_ceSize - 2));
      sc_firstarg.setColumnExpression(vec_firstarg);
      v_farg.addElement(sc_firstarg);
      vec_secondarg.addElement(v_ce.get(v_ceSize - 1));
      sc_secondarg.setColumnExpression(vec_secondarg);
      v_farg.addElement(sc_secondarg);
      fc.setFunctionArguments(v_farg);
      v_ce.remove(v_ceSize - 1);
      v_ce.remove(v_ceSize - 2);
      v_ce.addElement(isSqlite ? "CAST(" + fc + " AS INTEGER)" : fc);
   }

   public static Integer handleIntegerDivision(Vector v_ce, Integer division, int div_index, SelectQueryStatement from_sqs, SelectQueryStatement to_sqs) throws ConvertException {
      if (division > 0) {
         Object obj;
         String val;
         if (division > 2) {
            obj = v_ce.get(div_index - 1);
            val = "-" + obj.toString();
            v_ce.setElementAt(val, div_index - 1);
            createIntegerDivision(v_ce, div_index, from_sqs, to_sqs);
            division = 0;
         } else if (division > 1) {
            obj = v_ce.get(div_index + 1);
            val = obj.toString();
            if (val.equals("-")) {
               v_ce.remove(div_index + 1);
               division = division + 1;
            } else {
               createIntegerDivision(v_ce, div_index, from_sqs, to_sqs);
               division = 0;
            }
         } else {
            division = division + 1;
         }
      }

      return division;
   }

   public void createBitAndFunction(Vector v_ce, Vector columnExpression, int i) {
      SelectColumn sc_firstarg = new SelectColumn();
      SelectColumn sc_secondarg = new SelectColumn();
      Vector v_farg = new Vector();
      TableColumn tc = new TableColumn();
      FunctionCalls fc = new FunctionCalls();
      Vector vec_firstarg = new Vector();
      Vector vec_secondarg = new Vector();
      tc.setColumnName("BITAND");
      fc.setFunctionName(tc);
      vec_firstarg.addElement(columnExpression.elementAt(i - 1));
      v_ce.setElementAt(" ", i - 1);
      sc_firstarg.setColumnExpression(vec_firstarg);
      v_farg.addElement(sc_firstarg);
      vec_secondarg.addElement(columnExpression.elementAt(i + 1));
      columnExpression.setElementAt(" ", i + 1);
      sc_secondarg.setColumnExpression(vec_secondarg);
      v_farg.addElement(sc_secondarg);
      fc.setFunctionArguments(v_farg);
      v_ce.addElement(fc);
   }

   public void createCastFunction(Vector v_ce, Vector columnExpression, int i_count) {
      SelectColumn sc_firstarg = new SelectColumn();
      SelectColumn sc_secondarg = new SelectColumn();
      Vector v_farg = new Vector();
      Vector v_sarg = new Vector();
      TableColumn tc = new TableColumn();
      FunctionCalls fc = new FunctionCalls();
      Vector vec_functionarg = new Vector();
      tc.setColumnName("CAST");
      fc.setFunctionName(tc);
      fc.setAsDatatype("AS");
      v_farg.addElement(columnExpression.elementAt(i_count - 1));
      sc_firstarg.setColumnExpression(v_farg);
      v_ce.setElementAt(" ", i_count - 1);
      v_sarg.addElement(columnExpression.elementAt(i_count + 1).toString());
      sc_secondarg.setColumnExpression(v_sarg);
      vec_functionarg.addElement(sc_firstarg);
      vec_functionarg.addElement(sc_secondarg);
      fc.setFunctionArguments(vec_functionarg);
      v_ce.addElement(fc);
   }

   public void createDecodeFunction(Vector v_ce, Vector columnExpression, int i) {
      SelectColumn sc_firstarg = new SelectColumn();
      SelectColumn sc_secondarg = new SelectColumn();
      SelectColumn sc_thirdarg = new SelectColumn();
      SelectColumn sc_fourtharg = new SelectColumn();
      Vector v_farg = new Vector();
      TableColumn tc = new TableColumn();
      FunctionCalls fc = new FunctionCalls();
      Vector vec_firstarg = new Vector();
      Vector vec_secondarg = new Vector();
      Vector vec_thirdarg = new Vector();
      Vector vec_fourtharg = new Vector();
      tc.setColumnName("DECODE");
      fc.setFunctionName(tc);
      vec_firstarg.addElement(columnExpression.elementAt(i - 1));
      v_ce.setElementAt(" ", i - 1);
      sc_firstarg.setColumnExpression(vec_firstarg);
      v_farg.addElement(sc_firstarg);
      boolean isNotNull = false;
      Object obj = columnExpression.elementAt(i + 1);
      Object obj1 = columnExpression.elementAt(i);
      if (obj1.toString().trim().equalsIgnoreCase("is") && obj.toString().trim().equalsIgnoreCase("not null")) {
         obj = "NULL";
         isNotNull = true;
      }

      vec_secondarg.addElement(obj);
      columnExpression.setElementAt(" ", i + 1);
      sc_secondarg.setColumnExpression(vec_secondarg);
      v_farg.addElement(sc_secondarg);
      if (!isNotNull && obj1.toString().trim().equalsIgnoreCase("is")) {
         vec_thirdarg.addElement("1");
      } else {
         vec_thirdarg.addElement("0");
      }

      sc_thirdarg.setColumnExpression(vec_thirdarg);
      v_farg.addElement(sc_thirdarg);
      if (!isNotNull && obj1.toString().trim().equalsIgnoreCase("is")) {
         vec_fourtharg.addElement("0");
      } else {
         vec_fourtharg.addElement("1");
      }

      sc_fourtharg.setColumnExpression(vec_fourtharg);
      v_farg.addElement(sc_fourtharg);
      fc.setFunctionArguments(v_farg);
      v_ce.addElement(fc);
   }

   public SelectColumn toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass((CommentClass)null);
      sc.castToText = this.castToText;
      new String();
      Vector v_ce = new Vector();
      int sql_dialect = 0;
      int division = 0;
      int integer_division = 0;
      int div_index = 0;
      Integer modulus = 0;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      sc.setOpenBrace(this.OpenBrace);
      int i = 0;

      String str;
      String val;
      for(boolean var12 = false; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            TableColumn tcToBeChanged = (TableColumn)this.columnExpression.get(i);
            val = tcToBeChanged.getTableName() + ".";
            if (this.originalTableNameList != null) {
               if (this.originalTableNameList.containsKey(val)) {
                  TableColumn tc = (TableColumn)this.originalTableNameList.get(val);
                  tcToBeChanged.setTableName(tc.getTableName());
                  this.columnExpression.set(i, tcToBeChanged);
               } else {
                  this.columnExpression.set(i, tcToBeChanged);
               }
            }

            v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i)).toBigQuerySelect(to_sqs, from_sqs));
         } else if (!(this.columnExpression.elementAt(i) instanceof FunctionCalls)) {
            if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
               v_ce.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toBigQuerySelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
               v_ce.addElement(((WhereItem)this.columnExpression.elementAt(i)).toBigQuerySelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i);
               cs.setCastToTextInsideIf(this.castToText);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toBigQuerySelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(this.castToText);
                  v_ce.addElement(fc.toBigQuerySelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i)).setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toBigQuerySelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.columnExpression.elementAt(i)).setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toBigQuery());
            } else if (this.columnExpression.elementAt(i) instanceof Token) {
               str = this.columnExpression.elementAt(i).toString().trim();
               if (str.startsWith("/*") && str.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i);
               if (!s_ce.equalsIgnoreCase("CURRENT TIME") && !s_ce.equalsIgnoreCase("CURTIME")) {
                  if (!s_ce.equalsIgnoreCase("CURRENT DATE") && !s_ce.equalsIgnoreCase("CURDATE")) {
                     if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                        v_ce.addElement("CURRENT_TIMESTAMP");
                     } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                        v_ce.addElement("CURRENT_DATE");
                     } else if (s_ce.equalsIgnoreCase("**")) {
                        v_ce.addElement("^");
                     } else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 2) {
                        if (v_ce.elementAt(i - 1) instanceof String) {
                           sc.setAliasName((String)v_ce.elementAt(i - 1));
                        } else {
                           if (!(v_ce.elementAt(i - 1) instanceof TableColumn)) {
                              throw new ConvertException();
                           }

                           sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
                        }

                        v_ce.setElementAt(" ", i - 1);
                     } else if (s_ce.equalsIgnoreCase("NULL") && i > 1) {
                        if (this.columnExpression.elementAt(i - 1) instanceof String && this.columnExpression.elementAt(i - 1).equals("/")) {
                           throw new ConvertException("Conversion failure.. divided by NULL is not supported");
                        }

                        v_ce.addElement(s_ce);
                     } else if (s_ce.equalsIgnoreCase("IS")) {
                        this.createDecodeFunction(v_ce, this.columnExpression, i);
                        FunctionCalls fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
                        v_ce.setElementAt(fc.toBigQuerySelect(to_sqs, from_sqs), v_ce.size() - 1);
                     } else if (s_ce.trim().equals("+")) {
                        if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'") && i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                           v_ce.addElement("||");
                        } else {
                           v_ce.addElement("+");
                        }
                     } else if (!s_ce.startsWith("0x") && !s_ce.startsWith("0X")) {
                        if (s_ce.trim().equals("/")) {
                           v_ce.addElement("/");
                           division = 1;
                        } else if (s_ce.trim().equals("DIV")) {
                           v_ce.addElement("/");
                           integer_division = 1;
                           div_index = v_ce.size() - 1;
                        } else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                           v_ce.addElement("");
                        } else if (s_ce.trim().equals("%")) {
                           v_ce.addElement("%");
                           modulus = 1;
                        } else {
                           v_ce.addElement(s_ce);
                        }
                     } else {
                        str = s_ce.substring(2);
                        v_ce.addElement("'" + str + "'");
                     }
                  } else {
                     v_ce.addElement("CURRENT_DATE");
                  }
               } else {
                  v_ce.addElement("CURRENT_TIME");
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i));
            }
         } else {
            Vector funcArguments = ((FunctionCalls)this.columnExpression.get(i)).getFunctionArguments();
            if (funcArguments != null && this.originalTableNameList != null) {
               for(int j = 0; j < funcArguments.size(); ++j) {
                  if (funcArguments.elementAt(j) instanceof SelectColumn) {
                     SelectColumn sc1 = (SelectColumn)funcArguments.get(j);
                     sc1.setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
                  }
               }
            }

            v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toBigQuerySelect(to_sqs, from_sqs));
         }

         if (division > 0) {
            Object obj;
            String decimalVal;
            if (division > 2) {
               obj = v_ce.get(i);
               val = "-" + obj.toString();
               v_ce.remove(i);
               v_ce.remove(i - 1);
               v_ce.add("");
               decimalVal = modifyDividerValue(obj, val, 14);
               v_ce.add(decimalVal);
               division = 0;
            } else if (division > 1) {
               obj = v_ce.get(i);
               val = obj.toString();
               if (val.equals("-")) {
                  ++division;
               } else {
                  v_ce.remove(i);
                  decimalVal = modifyDividerValue(obj, val, 14);
                  v_ce.add(decimalVal);
                  division = 0;
               }
            } else {
               ++division;
            }
         }

         modulus = handleModulusOperator(v_ce, modulus, i, 14);
         integer_division = handleIntegerDivision(v_ce, integer_division, div_index, from_sqs, to_sqs);
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      if (this.aliasName != null && this.isAS == null) {
         sc.setIsAS("AS");
      } else {
         sc.setIsAS(this.isAS);
      }

      sc.setEndsWith(this.endsWith);
      if (sc != null && sc.getAliasName() == null) {
         if (this.aliasName != null && this.aliasName.startsWith("'") && this.aliasName.endsWith("'")) {
            this.aliasName = this.aliasName.substring(1, this.aliasName.length() - 1);
            this.aliasName = this.aliasName.replace("\\'", "''");
            this.aliasName = "`" + this.aliasName + "`";
         }

         String aliasNameNew = this.aliasName;
         if (aliasNameNew != null && !aliasNameNew.isEmpty() && to_sqs != null && from_sqs != null && from_sqs.getBooleanValues("can.handle.having.without.group.by")) {
            String isAsNew = sc.getIsAS();
            str = sc.getEndsWith();
            sc.setIsAS((String)null);
            sc.setEndsWith((String)null);
            val = aliasNameNew.toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
            to_sqs.getAliasVsSelectColExpMap().put(val, sc.toString());
            sc.setIsAS(isAsNew);
            sc.setEndsWith(str);
         }

         sc.setAliasName(aliasNameNew);
      }

      return sc;
   }

   public SelectColumn toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass((CommentClass)null);
      sc.castToText = this.castToText;
      new String();
      Vector v_ce = new Vector();
      int sql_dialect = 0;
      int division = 0;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      sc.setOpenBrace(this.OpenBrace);
      int i = 0;

      String str;
      String val;
      for(boolean var9 = false; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            TableColumn tcToBeChanged = (TableColumn)this.columnExpression.get(i);
            val = tcToBeChanged.getTableName() + ".";
            if (this.originalTableNameList != null) {
               if (this.originalTableNameList.containsKey(val)) {
                  TableColumn tc = (TableColumn)this.originalTableNameList.get(val);
                  tcToBeChanged.setTableName(tc.getTableName());
                  this.columnExpression.set(i, tcToBeChanged);
               } else {
                  this.columnExpression.set(i, tcToBeChanged);
               }
            }

            v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i)).toAthenaSelect(to_sqs, from_sqs));
         } else if (!(this.columnExpression.elementAt(i) instanceof FunctionCalls)) {
            if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
               v_ce.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toAthenaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
               v_ce.addElement(((WhereItem)this.columnExpression.elementAt(i)).toAthenaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i);
               cs.setCastToTextInsideIf(this.castToText);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toAthenaSelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(this.castToText);
                  v_ce.addElement(fc.toAthenaSelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i)).setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toAthenaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.columnExpression.elementAt(i)).setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toAthena());
            } else if (this.columnExpression.elementAt(i) instanceof Token) {
               str = this.columnExpression.elementAt(i).toString().trim();
               if (str.startsWith("/*") && str.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i);
               if (!s_ce.equalsIgnoreCase("CURRENT TIME") && !s_ce.equalsIgnoreCase("CURTIME")) {
                  if (!s_ce.equalsIgnoreCase("CURRENT DATE") && !s_ce.equalsIgnoreCase("CURDATE")) {
                     if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                        v_ce.addElement("CURRENT_TIMESTAMP");
                     } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                        v_ce.addElement("CURRENT_DATE");
                     } else if (s_ce.equalsIgnoreCase("**")) {
                        v_ce.addElement("^");
                     } else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 2) {
                        if (v_ce.elementAt(i - 1) instanceof String) {
                           sc.setAliasName((String)v_ce.elementAt(i - 1));
                        } else {
                           if (!(v_ce.elementAt(i - 1) instanceof TableColumn)) {
                              throw new ConvertException();
                           }

                           sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
                        }

                        v_ce.setElementAt(" ", i - 1);
                     } else if (s_ce.equalsIgnoreCase("NULL") && i > 1) {
                        if (this.columnExpression.elementAt(i - 1) instanceof String && this.columnExpression.elementAt(i - 1).equals("/")) {
                           throw new ConvertException("Conversion failure.. divided by NULL is not supported");
                        }

                        v_ce.addElement(s_ce);
                     } else if (s_ce.equalsIgnoreCase("IS")) {
                        this.createDecodeFunction(v_ce, this.columnExpression, i);
                        FunctionCalls fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
                        v_ce.setElementAt(fc.toAthenaSelect(to_sqs, from_sqs), v_ce.size() - 1);
                     } else if (s_ce.trim().equals("+")) {
                        if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'") && i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                           v_ce.addElement("||");
                        } else {
                           v_ce.addElement("+");
                        }
                     } else if (!s_ce.startsWith("0x") && !s_ce.startsWith("0X")) {
                        if (s_ce.trim().equals("/")) {
                           v_ce.addElement("/");
                           division = 1;
                        } else if (s_ce.trim().equals("DIV")) {
                           v_ce.addElement("/");
                        } else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                           v_ce.addElement("");
                        } else {
                           v_ce.addElement(s_ce);
                        }
                     } else {
                        str = s_ce.substring(2);
                        v_ce.addElement("'" + str + "'");
                     }
                  } else {
                     v_ce.addElement("CURRENT_DATE");
                  }
               } else {
                  v_ce.addElement("CURRENT_TIME");
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i));
            }
         } else {
            Vector funcArguments = ((FunctionCalls)this.columnExpression.get(i)).getFunctionArguments();
            if (funcArguments != null && this.originalTableNameList != null) {
               for(int j = 0; j < funcArguments.size(); ++j) {
                  if (funcArguments.elementAt(j) instanceof SelectColumn) {
                     SelectColumn sc1 = (SelectColumn)funcArguments.get(j);
                     sc1.setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
                  }
               }
            }

            v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toAthenaSelect(to_sqs, from_sqs));
         }

         if (division > 0) {
            Object obj;
            String decimalVal;
            if (division > 2) {
               obj = v_ce.get(i);
               val = "-" + obj.toString();
               v_ce.remove(i);
               v_ce.remove(i - 1);
               v_ce.add("");
               decimalVal = modifyDividerValue(obj, val, 16);
               v_ce.add(decimalVal);
               division = 0;
            } else if (division > 1) {
               obj = v_ce.get(i);
               val = obj.toString();
               if (val.equals("-")) {
                  ++division;
               } else {
                  v_ce.remove(i);
                  decimalVal = modifyDividerValue(obj, val, 16);
                  v_ce.add(decimalVal);
                  division = 0;
               }
            } else {
               ++division;
            }
         }
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      if (this.aliasName != null && this.isAS == null) {
         sc.setIsAS("AS");
      } else {
         sc.setIsAS(this.isAS);
      }

      sc.setEndsWith(this.endsWith);
      if (sc != null && sc.getAliasName() == null) {
         String aliasNameNew = this.aliasName;
         if (this.aliasName != null) {
            aliasNameNew = this.aliasName.replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
         }

         if (aliasNameNew != null && !aliasNameNew.isEmpty() && to_sqs != null && from_sqs != null && from_sqs.getBooleanValues("can.handle.having.without.group.by")) {
            String isAsNew = sc.getIsAS();
            str = sc.getEndsWith();
            sc.setIsAS((String)null);
            sc.setEndsWith((String)null);
            val = aliasNameNew.toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
            to_sqs.getAliasVsSelectColExpMap().put(val, sc.toString());
            sc.setIsAS(isAsNew);
            sc.setEndsWith(str);
         }

         sc.setAliasName(aliasNameNew);
      }

      return sc;
   }

   public SelectColumn toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass((CommentClass)null);
      sc.castToText = this.castToText;
      new String();
      Vector v_ce = new Vector();
      int sql_dialect = 0;
      int division = 0;
      int modulus = 0;
      int integer_division = 0;
      int div_index = 0;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      sc.setOpenBrace(this.OpenBrace);

      String str;
      String val;
      String decimalVal;
      for(int i = 0; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            TableColumn tcToBeChanged = (TableColumn)this.columnExpression.get(i);
            val = tcToBeChanged.getTableName() + ".";
            if (this.originalTableNameList != null) {
               if (this.originalTableNameList.containsKey(val)) {
                  TableColumn tc = (TableColumn)this.originalTableNameList.get(val);
                  tcToBeChanged.setTableName(tc.getTableName());
                  this.columnExpression.set(i, tcToBeChanged);
               } else {
                  this.columnExpression.set(i, tcToBeChanged);
               }
            }

            v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i)).toSapHanaSelect(to_sqs, from_sqs));
         } else if (!(this.columnExpression.elementAt(i) instanceof FunctionCalls)) {
            if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
               v_ce.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toSapHanaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
               v_ce.addElement(((WhereItem)this.columnExpression.elementAt(i)).toSapHanaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i);
               cs.setCastToTextInsideIf(this.castToText);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toSapHanaSelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(this.castToText);
                  v_ce.addElement(fc.toSapHanaSelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i)).setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toSapHanaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.columnExpression.elementAt(i)).setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toSapHana());
            } else if (this.columnExpression.elementAt(i) instanceof Token) {
               str = this.columnExpression.elementAt(i).toString().trim();
               if (str.startsWith("/*") && str.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i);
               if (!s_ce.equalsIgnoreCase("CURRENT TIME") && !s_ce.equalsIgnoreCase("CURTIME")) {
                  if (!s_ce.equalsIgnoreCase("CURRENT DATE") && !s_ce.equalsIgnoreCase("CURDATE")) {
                     if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                        v_ce.addElement("CURRENT_TIMESTAMP");
                     } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                        v_ce.addElement("CURRENT_DATE");
                     } else if (s_ce.equalsIgnoreCase("**")) {
                        v_ce.addElement("^");
                     } else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 2) {
                        if (v_ce.elementAt(i - 1) instanceof String) {
                           sc.setAliasName((String)v_ce.elementAt(i - 1));
                        } else {
                           if (!(v_ce.elementAt(i - 1) instanceof TableColumn)) {
                              throw new ConvertException();
                           }

                           sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
                        }

                        v_ce.setElementAt(" ", i - 1);
                     } else if (s_ce.equalsIgnoreCase("NULL") && i > 1) {
                        if (this.columnExpression.elementAt(i - 1) instanceof String && this.columnExpression.elementAt(i - 1).equals("/")) {
                           throw new ConvertException("Conversion failure.. divided by NULL is not supported");
                        }

                        v_ce.addElement(s_ce);
                     } else if (s_ce.equalsIgnoreCase("IS")) {
                        this.createDecodeFunction(v_ce, this.columnExpression, i);
                        FunctionCalls fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
                        v_ce.setElementAt(fc.toSapHanaSelect(to_sqs, from_sqs), v_ce.size() - 1);
                     } else if (s_ce.trim().equals("+")) {
                        if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'") && i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                           v_ce.addElement("||");
                        } else {
                           v_ce.addElement("+");
                        }
                     } else if (!s_ce.startsWith("0x") && !s_ce.startsWith("0X")) {
                        if (s_ce.trim().equals("/")) {
                           v_ce.addElement("/");
                           division = 1;
                        } else if (s_ce.trim().equals("DIV")) {
                           v_ce.addElement("/");
                           integer_division = 1;
                           div_index = v_ce.size() - 1;
                        } else if (s_ce.trim().equals("%")) {
                           v_ce.addElement("%");
                           modulus = 1;
                        } else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                           v_ce.addElement("");
                        } else {
                           v_ce.addElement(s_ce);
                        }
                     } else {
                        str = s_ce.substring(2);
                        v_ce.addElement("'" + str + "'");
                     }
                  } else {
                     v_ce.addElement("CURRENT_DATE");
                  }
               } else {
                  v_ce.addElement("CURRENT_TIME");
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i));
            }
         } else {
            Vector funcArguments = ((FunctionCalls)this.columnExpression.get(i)).getFunctionArguments();
            if (funcArguments != null && this.originalTableNameList != null) {
               for(int j = 0; j < funcArguments.size(); ++j) {
                  if (funcArguments.elementAt(j) instanceof SelectColumn) {
                     SelectColumn sc1 = (SelectColumn)funcArguments.get(j);
                     sc1.setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
                  }
               }
            }

            v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toSapHanaSelect(to_sqs, from_sqs));
         }

         if (division > 0) {
            Object obj;
            if (division > 2) {
               obj = v_ce.get(i);
               val = "-" + obj.toString();
               v_ce.remove(i);
               v_ce.remove(i - 1);
               v_ce.add("");
               decimalVal = modifyDividerValue(obj, val, 17);
               v_ce.add(decimalVal);
               division = 0;
            } else if (division > 1) {
               obj = v_ce.get(i);
               val = obj.toString();
               if (val.equals("-")) {
                  ++division;
               } else {
                  v_ce.remove(i);
                  decimalVal = modifyDividerValue(obj, val, 17);
                  v_ce.add(decimalVal);
                  division = 0;
               }
            } else {
               ++division;
            }
         }

         modulus = handleModulusOperator(v_ce, modulus, i, 17);
         integer_division = handleIntegerDivision(v_ce, integer_division, div_index, from_sqs, to_sqs);
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      if (this.aliasName != null && this.isAS == null) {
         sc.setIsAS("AS");
      } else {
         sc.setIsAS(this.isAS);
      }

      sc.setEndsWith(this.endsWith);
      if (sc != null && sc.getAliasName() == null) {
         String aliasNameNew = this.aliasName;
         if (this.aliasName != null) {
            aliasNameNew = this.aliasName.replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
         }

         if (aliasNameNew != null && !aliasNameNew.isEmpty() && to_sqs != null && from_sqs != null && from_sqs.getBooleanValues("can.handle.having.without.group.by")) {
            str = sc.getIsAS();
            val = sc.getEndsWith();
            sc.setIsAS((String)null);
            sc.setEndsWith((String)null);
            decimalVal = aliasNameNew.toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
            to_sqs.getAliasVsSelectColExpMap().put(decimalVal, sc.toString());
            sc.setIsAS(str);
            sc.setEndsWith(val);
         }

         sc.setAliasName(aliasNameNew);
      }

      return sc;
   }

   public SelectColumn toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass((CommentClass)null);
      sc.castToText = this.castToText;
      new String();
      Vector v_ce = new Vector();
      int sql_dialect = 0;
      int division = 0;
      int modulus = 0;
      int integer_division = 0;
      int div_index = 0;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      sc.setOpenBrace(this.OpenBrace);

      String str;
      String val;
      String decimalVal;
      for(int i = 0; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            TableColumn tcToBeChanged = (TableColumn)this.columnExpression.get(i);
            val = tcToBeChanged.getTableName() + ".";
            if (this.originalTableNameList != null) {
               if (this.originalTableNameList.containsKey(val)) {
                  TableColumn tc = (TableColumn)this.originalTableNameList.get(val);
                  tcToBeChanged.setTableName(tc.getTableName());
                  this.columnExpression.set(i, tcToBeChanged);
               } else {
                  this.columnExpression.set(i, tcToBeChanged);
               }
            }

            v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i)).toSqliteSelect(to_sqs, from_sqs));
         } else if (!(this.columnExpression.elementAt(i) instanceof FunctionCalls)) {
            if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
               v_ce.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toSqliteSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
               v_ce.addElement(((WhereItem)this.columnExpression.elementAt(i)).toSqliteSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i);
               cs.setCastToTextInsideIf(this.castToText);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toSqliteSelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(this.castToText);
                  v_ce.addElement(fc.toSqliteSelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i)).setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toSqliteSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.columnExpression.elementAt(i)).setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toSqlite());
            } else if (this.columnExpression.elementAt(i) instanceof Token) {
               str = this.columnExpression.elementAt(i).toString().trim();
               if (str.startsWith("/*") && str.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i);
               if (!s_ce.equalsIgnoreCase("CURRENT TIME") && !s_ce.equalsIgnoreCase("CURTIME")) {
                  if (!s_ce.equalsIgnoreCase("CURRENT DATE") && !s_ce.equalsIgnoreCase("CURDATE")) {
                     if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                        v_ce.addElement("DATETIME");
                     } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                        v_ce.addElement("DATE");
                     } else if (s_ce.equalsIgnoreCase("**")) {
                        v_ce.addElement("^");
                     } else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 2) {
                        if (v_ce.elementAt(i - 1) instanceof String) {
                           sc.setAliasName((String)v_ce.elementAt(i - 1));
                        } else {
                           if (!(v_ce.elementAt(i - 1) instanceof TableColumn)) {
                              throw new ConvertException();
                           }

                           sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
                        }

                        v_ce.setElementAt(" ", i - 1);
                     } else if (s_ce.equalsIgnoreCase("NULL") && i > 1) {
                        if (this.columnExpression.elementAt(i - 1) instanceof String && this.columnExpression.elementAt(i - 1).equals("/")) {
                           throw new ConvertException("Conversion failure.. divided by NULL is not supported");
                        }

                        v_ce.addElement(s_ce);
                     } else if (s_ce.equalsIgnoreCase("IS")) {
                        this.createDecodeFunction(v_ce, this.columnExpression, i);
                        FunctionCalls fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
                        v_ce.setElementAt(fc.toSqliteSelect(to_sqs, from_sqs), v_ce.size() - 1);
                     } else if (s_ce.trim().equals("+")) {
                        if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'") && i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                           v_ce.addElement("||");
                        } else {
                           v_ce.addElement("+");
                        }
                     } else if (!s_ce.startsWith("0x") && !s_ce.startsWith("0X")) {
                        if (s_ce.trim().equals("/")) {
                           v_ce.addElement("/");
                           division = 1;
                        } else if (s_ce.trim().equals("DIV")) {
                           v_ce.addElement("/");
                        } else if (s_ce.trim().equals("%")) {
                           v_ce.addElement("%");
                           modulus = 1;
                        } else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                           v_ce.addElement("");
                        } else {
                           v_ce.addElement(s_ce);
                        }
                     } else {
                        str = s_ce.substring(2);
                        v_ce.addElement("'" + str + "'");
                     }
                  } else {
                     v_ce.addElement("DATE");
                  }
               } else {
                  v_ce.addElement("TIME");
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i));
            }
         } else {
            Vector funcArguments = ((FunctionCalls)this.columnExpression.get(i)).getFunctionArguments();
            if (funcArguments != null && this.originalTableNameList != null) {
               for(int j = 0; j < funcArguments.size(); ++j) {
                  if (funcArguments.elementAt(j) instanceof SelectColumn) {
                     SelectColumn sc1 = (SelectColumn)funcArguments.get(j);
                     sc1.setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
                  }
               }
            }

            v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toSqliteSelect(to_sqs, from_sqs));
         }

         if (division > 0) {
            Object obj;
            if (division > 2) {
               obj = v_ce.get(i);
               val = "-" + obj.toString();
               v_ce.remove(i);
               v_ce.remove(i - 1);
               v_ce.add("");
               decimalVal = modifyDividerValue(obj, val, 18, true);
               v_ce.add(decimalVal);
               division = 0;
            } else if (division > 1) {
               obj = v_ce.get(i);
               val = obj.toString();
               if (val.equals("-")) {
                  ++division;
               } else {
                  v_ce.remove(i);
                  decimalVal = modifyDividerValue(obj, val, 18, true);
                  v_ce.add(decimalVal);
                  division = 0;
               }
            } else {
               ++division;
            }
         }

         modulus = handleModulusOperator(v_ce, modulus, i, 18);
         integer_division = handleIntegerDivision(v_ce, integer_division, div_index, from_sqs, to_sqs);
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      if (this.aliasName != null && this.isAS == null) {
         sc.setIsAS("AS");
      } else {
         sc.setIsAS(this.isAS);
      }

      sc.setEndsWith(this.endsWith);
      if (sc != null && sc.getAliasName() == null) {
         String aliasNameNew = this.aliasName;
         if (this.aliasName != null) {
            aliasNameNew = this.aliasName.replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
         }

         if (aliasNameNew != null && !aliasNameNew.isEmpty() && to_sqs != null && from_sqs != null && from_sqs.getBooleanValues("can.handle.having.without.group.by")) {
            str = sc.getIsAS();
            val = sc.getEndsWith();
            sc.setIsAS((String)null);
            sc.setEndsWith((String)null);
            decimalVal = aliasNameNew.toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
            to_sqs.getAliasVsSelectColExpMap().put(decimalVal, sc.toString());
            sc.setIsAS(str);
            sc.setEndsWith(val);
         }

         sc.setAliasName(aliasNameNew);
      }

      return sc;
   }

   public SelectColumn toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass((CommentClass)null);
      sc.castToText = this.castToText;
      new String();
      Vector v_ce = new Vector();
      int sql_dialect = 0;
      int division = 0;
      int integer_division = 0;
      int div_index = 0;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      sc.setOpenBrace(this.OpenBrace);

      String str;
      String val;
      String decimalVal;
      for(int i = 0; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            TableColumn tcToBeChanged = (TableColumn)this.columnExpression.get(i);
            val = tcToBeChanged.getTableName() + ".";
            if (this.originalTableNameList != null) {
               if (this.originalTableNameList.containsKey(val)) {
                  TableColumn tc = (TableColumn)this.originalTableNameList.get(val);
                  tcToBeChanged.setTableName(tc.getTableName());
                  this.columnExpression.set(i, tcToBeChanged);
               } else {
                  this.columnExpression.set(i, tcToBeChanged);
               }
            }

            v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i)).toExcelSelect(to_sqs, from_sqs));
         } else if (!(this.columnExpression.elementAt(i) instanceof FunctionCalls)) {
            if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
               v_ce.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toExcelSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
               v_ce.addElement(((WhereItem)this.columnExpression.elementAt(i)).toExcelSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i);
               cs.setCastToTextInsideIf(this.castToText);
               from_sqs.setCanUseIFFunctionForPGCaseWhenExp(true);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toExcelSelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(this.castToText);
                  v_ce.addElement(fc.toExcelSelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i)).setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toExcelSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.columnExpression.elementAt(i)).setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toExcel());
            } else if (this.columnExpression.elementAt(i) instanceof Token) {
               str = this.columnExpression.elementAt(i).toString().trim();
               if (str.startsWith("/*") && str.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i);
               if (!s_ce.equalsIgnoreCase("CURRENT TIME") && !s_ce.equalsIgnoreCase("CURTIME")) {
                  if (!s_ce.equalsIgnoreCase("CURRENT DATE") && !s_ce.equalsIgnoreCase("CURDATE")) {
                     if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                        v_ce.addElement("DATETIME");
                     } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                        v_ce.addElement("DATE");
                     } else if (s_ce.equalsIgnoreCase("**")) {
                        v_ce.addElement("^");
                     } else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 2) {
                        if (v_ce.elementAt(i - 1) instanceof String) {
                           sc.setAliasName((String)v_ce.elementAt(i - 1));
                        } else {
                           if (!(v_ce.elementAt(i - 1) instanceof TableColumn)) {
                              throw new ConvertException();
                           }

                           sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
                        }

                        v_ce.setElementAt(" ", i - 1);
                     } else if (s_ce.equalsIgnoreCase("NULL") && i > 1) {
                        if (this.columnExpression.elementAt(i - 1) instanceof String && this.columnExpression.elementAt(i - 1).equals("/")) {
                           throw new ConvertException("Conversion failure.. divided by NULL is not supported");
                        }

                        v_ce.addElement(s_ce);
                     } else if (s_ce.equalsIgnoreCase("IS")) {
                        this.createDecodeFunction(v_ce, this.columnExpression, i);
                        FunctionCalls fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
                        v_ce.setElementAt(fc.toExcelSelect(to_sqs, from_sqs), v_ce.size() - 1);
                     } else if (s_ce.trim().equals("+")) {
                        if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'") && i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                           v_ce.addElement("||");
                        } else {
                           v_ce.addElement("+");
                        }
                     } else if (!s_ce.startsWith("0x") && !s_ce.startsWith("0X")) {
                        if (s_ce.trim().equals("/")) {
                           v_ce.addElement("/");
                           division = 1;
                        } else if (s_ce.trim().equals("DIV")) {
                           v_ce.addElement("/");
                           integer_division = 1;
                           div_index = v_ce.size() - 1;
                        } else if (s_ce.trim().equals("%")) {
                           v_ce.addElement("MOD");
                        } else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                           v_ce.addElement("");
                        } else {
                           v_ce.addElement(s_ce);
                        }
                     } else {
                        str = s_ce.substring(2);
                        v_ce.addElement("'" + str + "'");
                     }
                  } else {
                     v_ce.addElement("DATE");
                  }
               } else {
                  v_ce.addElement("TIME");
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i));
            }
         } else {
            Vector funcArguments = ((FunctionCalls)this.columnExpression.get(i)).getFunctionArguments();
            if (funcArguments != null && this.originalTableNameList != null) {
               for(int j = 0; j < funcArguments.size(); ++j) {
                  if (funcArguments.elementAt(j) instanceof SelectColumn) {
                     SelectColumn sc1 = (SelectColumn)funcArguments.get(j);
                     sc1.setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
                  }
               }
            }

            v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toExcelSelect(to_sqs, from_sqs));
         }

         if (division > 0) {
            Object obj;
            if (division > 2) {
               obj = v_ce.get(i);
               val = "-" + obj.toString();
               v_ce.remove(i);
               v_ce.remove(i - 1);
               v_ce.add("");
               decimalVal = modifyDividerValue(obj, val, 20, true);
               v_ce.add(decimalVal);
               division = 0;
            } else if (division > 1) {
               obj = v_ce.get(i);
               val = obj.toString();
               if (val.equals("-")) {
                  ++division;
               } else {
                  v_ce.remove(i);
                  decimalVal = modifyDividerValue(obj, val, 20, true);
                  v_ce.add(decimalVal);
                  division = 0;
               }
            } else {
               ++division;
            }
         }

         integer_division = handleIntegerDivision(v_ce, integer_division, div_index, from_sqs, to_sqs);
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      if (this.aliasName != null && this.isAS == null) {
         sc.setIsAS("AS");
      } else {
         sc.setIsAS(this.isAS);
      }

      sc.setEndsWith(this.endsWith);
      if (sc != null && sc.getAliasName() == null) {
         String aliasNameNew = this.aliasName;
         if (this.aliasName != null) {
            aliasNameNew = this.aliasName.replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
         }

         if (aliasNameNew != null && !aliasNameNew.isEmpty() && to_sqs != null && from_sqs != null && from_sqs.getBooleanValues("can.handle.having.without.group.by")) {
            str = sc.getIsAS();
            val = sc.getEndsWith();
            sc.setIsAS((String)null);
            sc.setEndsWith((String)null);
            decimalVal = aliasNameNew.toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
            to_sqs.getAliasVsSelectColExpMap().put(decimalVal, sc.toString());
            sc.setIsAS(str);
            sc.setEndsWith(val);
         }

         sc.setAliasName(aliasNameNew);
      }

      return sc;
   }

   public SelectColumn toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass((CommentClass)null);
      sc.castToText = this.castToText;
      new String();
      Vector v_ce = new Vector();
      int sql_dialect = 0;
      int division = 0;
      int modulus = 0;
      int integer_division = 0;
      int div_index = 0;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      sc.setOpenBrace(this.OpenBrace);

      String str;
      String val;
      String decimalVal;
      for(int i = 0; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            TableColumn tcToBeChanged = (TableColumn)this.columnExpression.get(i);
            val = tcToBeChanged.getTableName() + ".";
            if (this.originalTableNameList != null) {
               if (this.originalTableNameList.containsKey(val)) {
                  TableColumn tc = (TableColumn)this.originalTableNameList.get(val);
                  tcToBeChanged.setTableName(tc.getTableName());
                  this.columnExpression.set(i, tcToBeChanged);
               } else {
                  this.columnExpression.set(i, tcToBeChanged);
               }
            }

            v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else if (!(this.columnExpression.elementAt(i) instanceof FunctionCalls)) {
            if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
               v_ce.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toMsAccessJdbcSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
               v_ce.addElement(((WhereItem)this.columnExpression.elementAt(i)).toMsAccessJdbcSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i);
               cs.setCastToTextInsideIf(this.castToText);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toMsAccessJdbcSelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(this.castToText);
                  v_ce.addElement(fc.toMsAccessJdbcSelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i)).setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toMsAccessJdbcSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.columnExpression.elementAt(i)).setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toMsAccessJdbc());
            } else if (this.columnExpression.elementAt(i) instanceof Token) {
               str = this.columnExpression.elementAt(i).toString().trim();
               if (str.startsWith("/*") && str.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i);
               if (!s_ce.equalsIgnoreCase("CURRENT TIME") && !s_ce.equalsIgnoreCase("CURTIME")) {
                  if (!s_ce.equalsIgnoreCase("CURRENT DATE") && !s_ce.equalsIgnoreCase("CURDATE")) {
                     if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                        v_ce.addElement("CURRENT_TIMESTAMP");
                     } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                        v_ce.addElement("CURRENT_DATE");
                     } else if (s_ce.equalsIgnoreCase("**")) {
                        v_ce.addElement("^");
                     } else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 2) {
                        if (v_ce.elementAt(i - 1) instanceof String) {
                           sc.setAliasName((String)v_ce.elementAt(i - 1));
                        } else {
                           if (!(v_ce.elementAt(i - 1) instanceof TableColumn)) {
                              throw new ConvertException();
                           }

                           sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
                        }

                        v_ce.setElementAt(" ", i - 1);
                     } else if (s_ce.equalsIgnoreCase("NULL") && i > 1) {
                        if (this.columnExpression.elementAt(i - 1) instanceof String && this.columnExpression.elementAt(i - 1).equals("/")) {
                           throw new ConvertException("Conversion failure.. divided by NULL is not supported");
                        }

                        v_ce.addElement(s_ce);
                     } else if (s_ce.equalsIgnoreCase("IS")) {
                        this.createDecodeFunction(v_ce, this.columnExpression, i);
                        FunctionCalls fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
                        v_ce.setElementAt(fc.toMsAccessJdbcSelect(to_sqs, from_sqs), v_ce.size() - 1);
                     } else if (s_ce.trim().equals("+")) {
                        if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'") && i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                           v_ce.addElement("||");
                        } else {
                           v_ce.addElement("+");
                        }
                     } else if (!s_ce.startsWith("0x") && !s_ce.startsWith("0X")) {
                        if (s_ce.trim().equals("/")) {
                           v_ce.addElement("/");
                           division = 1;
                        } else if (s_ce.trim().equals("DIV")) {
                           v_ce.addElement("/");
                        } else if (s_ce.trim().equals("%")) {
                           v_ce.addElement("%");
                           modulus = 1;
                        } else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                           v_ce.addElement("");
                        } else {
                           v_ce.addElement(s_ce);
                        }
                     } else {
                        str = s_ce.substring(2);
                        v_ce.addElement("'" + str + "'");
                     }
                  } else {
                     v_ce.addElement("CURRENT_DATE");
                  }
               } else {
                  v_ce.addElement("CURRENT_TIME");
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i));
            }
         } else {
            Vector funcArguments = ((FunctionCalls)this.columnExpression.get(i)).getFunctionArguments();
            if (funcArguments != null && this.originalTableNameList != null) {
               for(int j = 0; j < funcArguments.size(); ++j) {
                  if (funcArguments.elementAt(j) instanceof SelectColumn) {
                     SelectColumn sc1 = (SelectColumn)funcArguments.get(j);
                     sc1.setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
                  }
               }
            }

            v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         }

         if (division > 0) {
            Object obj;
            if (division > 2) {
               obj = v_ce.get(i);
               val = "-" + obj.toString();
               v_ce.remove(i);
               v_ce.remove(i - 1);
               v_ce.add("");
               decimalVal = modifyDividerValue(obj, val, 21, true);
               v_ce.add(decimalVal);
               division = 0;
            } else if (division > 1) {
               obj = v_ce.get(i);
               val = obj.toString();
               if (val.equals("-")) {
                  ++division;
               } else {
                  v_ce.remove(i);
                  decimalVal = modifyDividerValue(obj, val, 21, true);
                  v_ce.add(decimalVal);
                  division = 0;
               }
            } else {
               ++division;
            }
         }

         modulus = handleModulusOperator(v_ce, modulus, i, 21);
         integer_division = handleIntegerDivision(v_ce, integer_division, div_index, from_sqs, to_sqs);
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      if (this.aliasName != null && this.isAS == null) {
         sc.setIsAS("AS");
      } else {
         sc.setIsAS(this.isAS);
      }

      sc.setEndsWith(this.endsWith);
      if (sc != null && sc.getAliasName() == null) {
         String aliasNameNew = this.aliasName;
         if (this.aliasName != null) {
            aliasNameNew = this.aliasName.replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
         }

         if (aliasNameNew != null && !aliasNameNew.isEmpty() && to_sqs != null && from_sqs != null && from_sqs.getBooleanValues("can.handle.having.without.group.by")) {
            str = sc.getIsAS();
            val = sc.getEndsWith();
            sc.setIsAS((String)null);
            sc.setEndsWith((String)null);
            decimalVal = aliasNameNew.toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
            to_sqs.getAliasVsSelectColExpMap().put(decimalVal, sc.toString());
            sc.setIsAS(str);
            sc.setEndsWith(val);
         }

         sc.setAliasName(aliasNameNew);
      }

      return sc;
   }

   public SelectColumn toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass((CommentClass)null);
      sc.castToText = this.castToText;
      new String();
      Vector v_ce = new Vector();
      int sql_dialect = 0;
      int division = 0;
      Integer modulus = 0;
      int integer_division = 0;
      int div_index = 0;
      boolean multipication = false;
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      sc.setOpenBrace(this.OpenBrace);
      int i = 0;
      byte interval = 0;

      String val;
      String val;
      while(i < this.columnExpression.size()) {
         Vector vc_intervalVal;
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            TableColumn tcToBeChanged = (TableColumn)this.columnExpression.get(i);
            val = tcToBeChanged.getTableName() + ".";
            if (this.originalTableNameList != null) {
               if (this.originalTableNameList.containsKey(val)) {
                  TableColumn tc = (TableColumn)this.originalTableNameList.get(val);
                  tcToBeChanged.setTableName(tc.getTableName());
                  this.columnExpression.set(i, tcToBeChanged);
               } else {
                  this.columnExpression.set(i, tcToBeChanged);
               }
            }

            v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs));
            if (((TableColumn)this.columnExpression.elementAt(i)).toString().equalsIgnoreCase("INTERVAL")) {
               interval = 1;
               v_ce.remove(v_ce.size() - 1);
               v_ce.addElement("INTERVAL");
               v_ce.addElement(" '1' ");
            }
         } else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
            ((FunctionCalls)this.columnExpression.elementAt(i)).setCastToTextInsideIf(this.castToText);
            vc_intervalVal = ((FunctionCalls)this.columnExpression.get(i)).getFunctionArguments();
            if (vc_intervalVal != null && this.originalTableNameList != null) {
               for(int j = 0; j < vc_intervalVal.size(); ++j) {
                  if (vc_intervalVal.elementAt(j) instanceof SelectColumn) {
                     SelectColumn sc1 = (SelectColumn)vc_intervalVal.get(j);
                     sc1.setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
                  }
               }
            }

            v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
            v_ce.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
            v_ce.addElement(((WhereItem)this.columnExpression.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
            CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i);
            cs.setCastToTextInsideIf(this.castToText);
            FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
            if (fc == null) {
               v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               fc.setCastToTextInsideIf(this.castToText);
               v_ce.addElement(fc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
         } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
            ((SelectColumn)this.columnExpression.elementAt(i)).setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
            v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
            ((SelectQueryStatement)this.columnExpression.elementAt(i)).setQueryConversionPropHandler(from_sqs.getQueryConvPropHandler());
            ((SelectQueryStatement)this.columnExpression.elementAt(i)).setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
            v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toPostgreSQL());
         } else if (this.columnExpression.elementAt(i) instanceof Token) {
            val = this.columnExpression.elementAt(i).toString().trim();
            if (val.startsWith("/*") && val.endsWith("*/")) {
               v_ce.addElement("");
            }
         } else if (!(this.columnExpression.elementAt(i) instanceof String)) {
            v_ce.addElement(this.columnExpression.elementAt(i));
         } else {
            String s_ce = (String)this.columnExpression.elementAt(i);
            if (!s_ce.equalsIgnoreCase("CURRENT TIME") && !s_ce.equalsIgnoreCase("CURTIME")) {
               if (!s_ce.equalsIgnoreCase("CURRENT DATE") && !s_ce.equalsIgnoreCase("CURDATE")) {
                  if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                     v_ce.addElement("CURRENT_TIMESTAMP");
                  } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                     v_ce.addElement("CURRENT_DATE");
                  } else if (s_ce.equalsIgnoreCase("**")) {
                     v_ce.addElement("^");
                  } else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 2) {
                     if (v_ce.elementAt(i - 1) instanceof String) {
                        sc.setAliasName((String)v_ce.elementAt(i - 1));
                     } else {
                        if (!(v_ce.elementAt(i - 1) instanceof TableColumn)) {
                           throw new ConvertException();
                        }

                        sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
                     }

                     v_ce.setElementAt(" ", i - 1);
                  } else if (s_ce.equalsIgnoreCase("NULL") && i > 1) {
                     if (this.columnExpression.elementAt(i - 1) instanceof String && this.columnExpression.elementAt(i - 1).equals("/")) {
                        throw new ConvertException("Conversion failure.. divided by NULL is not supported");
                     }

                     v_ce.addElement(s_ce);
                  } else if (s_ce.equalsIgnoreCase("IS")) {
                     this.createDecodeFunction(v_ce, this.columnExpression, i);
                     FunctionCalls fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
                     v_ce.setElementAt(fc.toPostgreSQLSelect(to_sqs, from_sqs), v_ce.size() - 1);
                  } else if (s_ce.trim().equals("+")) {
                     if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'") && i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                        v_ce.addElement("||");
                     } else {
                        v_ce.addElement("+");
                     }
                  } else if (!s_ce.startsWith("0x") && !s_ce.startsWith("0X")) {
                     if (s_ce.trim().equals("/")) {
                        v_ce.addElement("/");
                        division = 1;
                     } else if (s_ce.trim().equals("*")) {
                        if (i < this.columnExpression.size() - 1 && i != 0 && !(this.columnExpression.elementAt(i + 1) instanceof String) && this.columnExpression.elementAt(i - 1) instanceof String) {
                           int maxLength = v_ce.size();
                           v_ce.remove(maxLength - 1);
                           val = (String)this.columnExpression.elementAt(i - 1);
                           val = StringFunctions.castMultiplicationOperand(val);
                           v_ce.add(val);
                           v_ce.add(s_ce);
                        } else {
                           v_ce.add(s_ce);
                           multipication = true;
                        }
                     } else if (s_ce.trim().equals("DIV")) {
                        v_ce.addElement("/");
                        integer_division = 1;
                        div_index = v_ce.size() - 1;
                     } else if (s_ce.trim().equals("%")) {
                        v_ce.addElement("%");
                        modulus = 1;
                     } else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                        v_ce.addElement("");
                     } else if (multipication) {
                        val = StringFunctions.castMultiplicationOperand(s_ce);
                        v_ce.add(val);
                        if (val.equalsIgnoreCase("-")) {
                           multipication = true;
                        } else {
                           multipication = false;
                        }
                     } else if (from_sqs != null && from_sqs.getBooleanValues("can.cast.string.literal.to.text.in.select.column.expression") && i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().equals("+") && (s_ce.trim().equals("'+'") || s_ce.trim().equals("'-'") || s_ce.trim().equals("'.'") || s_ce.trim().matches("'.*[a-zA-Z<>\"']+.*'"))) {
                        v_ce.addElement(from_sqs.isVerticaDb() ? "cast(" + s_ce + " as varchar)" : "cast(" + s_ce + " as text)");
                     } else {
                        v_ce.addElement(s_ce);
                     }
                  } else {
                     val = s_ce.substring(2);
                     v_ce.addElement("'" + val + "'");
                  }
               } else {
                  v_ce.addElement("CURRENT_DATE");
               }
            } else {
               v_ce.addElement("CURRENT_TIME");
            }
         }

         if (division > 0) {
            Object obj;
            String decimalVal;
            if (division > 2) {
               obj = v_ce.get(i);
               val = "-" + obj.toString();
               v_ce.remove(i);
               v_ce.remove(i - 1);
               v_ce.add("");
               decimalVal = from_sqs != null && from_sqs.isAmazonRedShift() ? modifyDividerValue(obj, val, -1, true) : modifyDividerValue(obj, val, 4, false);
               v_ce.add(decimalVal);
               division = 0;
            } else if (division > 1) {
               obj = v_ce.get(i);
               val = obj.toString();
               if (val.equals("-")) {
                  ++division;
               } else {
                  v_ce.remove(i);
                  decimalVal = from_sqs != null && from_sqs.isAmazonRedShift() ? modifyDividerValue(obj, val, -1, true) : modifyDividerValue(obj, val, 4, false);
                  v_ce.add(decimalVal);
                  division = 0;
               }
            } else {
               ++division;
            }
         }

         if (from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("can.use.mod.function")) {
            modulus = handleModulusOperator(v_ce, modulus, i, 4);
         }

         if (from_sqs != null && from_sqs.isVerticaDb()) {
            integer_division = handleIntegerDivision(v_ce, integer_division, div_index, from_sqs, to_sqs);
         }

         if (interval == 1) {
            i += 2;
            interval = 2;
         } else if (interval == 2) {
            --i;
            if (v_ce.get(i + 1).toString().equalsIgnoreCase("week")) {
               v_ce.remove(i + 1);
               v_ce.add("day * 7 * ");
            } else if (v_ce.get(i + 1).toString().equalsIgnoreCase("quarter")) {
               v_ce.remove(i + 1);
               v_ce.add("month * 3 * ");
            } else if (v_ce.get(i + 1).toString().equalsIgnoreCase("microsecond")) {
               v_ce.remove(i + 1);
               v_ce.add("second / 1000000 * ");
               if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                  vc_intervalVal = ((SelectColumn)this.columnExpression.elementAt(i)).getColumnExpression();
                  Object lastEl = vc_intervalVal.lastElement();
                  if (lastEl instanceof String && lastEl.equals("1000.0")) {
                     vc_intervalVal.setElementAt("1000", vc_intervalVal.size() - 1);
                  }
               }
            } else {
               v_ce.add("*");
            }

            interval = 3;
         } else if (interval == 3) {
            i += 2;
            interval = 0;
         } else {
            ++i;
         }
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      if (this.aliasName != null && this.isAS == null) {
         sc.setIsAS("AS");
      } else {
         sc.setIsAS(this.isAS);
      }

      sc.setEndsWith(this.endsWith);
      if (sc != null && sc.getAliasName() == null) {
         if (this.aliasName != null) {
            if (this.aliasName.startsWith("'") && this.aliasName.endsWith("'")) {
               this.aliasName = this.aliasName.substring(1, this.aliasName.length() - 1);
               this.aliasName = this.aliasName.replace("\\'", "''");
               this.aliasName = "\"" + this.aliasName + "\"";
            } else if (this.aliasName.startsWith("`") && this.aliasName.endsWith("`")) {
               this.aliasName = this.aliasName.substring(1, this.aliasName.length() - 1);
               this.aliasName = this.aliasName.replace("\"", "\"\"");
               this.aliasName = "\"" + this.aliasName + "\"";
            }
         }

         String aliasNameNew = SelectStatement.checkandRemoveDoubleQuoteForPostgresIdentifier(this.aliasName, from_sqs == null ? false : from_sqs.getReportsMeta());
         if (aliasNameNew != null && !aliasNameNew.isEmpty() && to_sqs != null && from_sqs != null && from_sqs.getBooleanValues("can.handle.having.without.group.by")) {
            String isAsNew = sc.getIsAS();
            val = sc.getEndsWith();
            sc.setIsAS((String)null);
            sc.setEndsWith((String)null);
            val = aliasNameNew.toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
            to_sqs.getAliasVsSelectColExpMap().put(val, sc.toString());
            sc.setIsAS(isAsNew);
            sc.setEndsWith(val);
         }

         sc.setAliasName(aliasNameNew);
      }

      return sc;
   }

   public SelectColumn toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      return this.toMySQLSelect(to_sqs, from_sqs, false);
   }

   public SelectColumn toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, boolean fromIfClause) throws ConvertException {
      return this.toMySQLSelect(to_sqs, from_sqs, fromIfClause, false);
   }

   public SelectColumn toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, boolean fromIfClause, boolean fromOrigQuery) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass(this.commentObj);
      IntoStatement into = new IntoStatement();
      new String();
      Vector v_ce = new Vector();
      boolean intoClause = false;
      boolean concat = false;
      int sql_dialect = 0;
      int modulus = 0;
      String aliasname_temp = null;
      int integer_division = 0;
      int div_index = 0;
      boolean isHyperSql = from_sqs != null && from_sqs.isHyperSql();
      Vector colExpr = this.columnExpression;
      sc.setReportsMysqlConversion(true);
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      sc.setOpenBrace(this.OpenBrace);
      if (from_sqs.getValidationHandler() != null && from_sqs.getinstanceDataTypeHandler() != null) {
         try {
            if (this.isEqualOperatorUsedForAliasAssignment(this.isMainSelectColumn())) {
               aliasname_temp = ((TableColumn)this.columnExpression.get(0)).getColumnName();
               colExpr.remove(1);
               colExpr.remove(0);
            }

            PostfixFlowForMySQLSelect postFixToMySQL = new PostfixFlowForMySQLSelect();
            Object[] convertedCeNDataType = postFixToMySQL.postFixToMySQLFlowForSc(to_sqs, from_sqs, colExpr);
            v_ce = (Vector)convertedCeNDataType[0];
            sc.setInstanceDatatype((String)convertedCeNDataType[1]);
         } catch (Exception var20) {
            if (from_sqs.getinstanceDataTypeHandler() != null) {
               from_sqs.setInstanceDataTypeHandler((InstanceDataTypeHandler)null);
            }

            if (!fromOrigQuery) {
               throw var20;
            }

            this.standardMySQLSelectFlow(to_sqs, from_sqs, fromIfClause, sc, into, v_ce, intoClause, concat, sql_dialect, modulus, integer_division, div_index, isHyperSql);
         }
      } else {
         this.standardMySQLSelectFlow(to_sqs, from_sqs, fromIfClause, sc, into, v_ce, intoClause, concat, sql_dialect, modulus, integer_division, div_index, isHyperSql);
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      sc.setIsAS(this.isAS);
      sc.setEndsWith(this.endsWith);
      if (from_sqs.getinstanceDataTypeHandler() != null && aliasname_temp != null) {
         this.aliasName = aliasname_temp;
      }

      if (sc != null && sc.getAliasName() == null) {
         if (this.aliasName != null) {
            if (this.aliasName.startsWith("[") && this.aliasName.endsWith("]")) {
               this.aliasName = this.aliasName.substring(1, this.aliasName.length() - 1);
               this.aliasName = "`" + this.aliasName + "`";
            } else if (from_sqs != null && from_sqs.isHyperSql() && this.aliasName.startsWith("'") && this.aliasName.endsWith("'")) {
               this.aliasName = this.aliasName.substring(1, this.aliasName.length() - 1);
            }
         }

         sc.setAliasName(this.aliasName);
      }

      if (this.aliasForExpression != null) {
         if (this.aliasForExpression.startsWith("[") && this.aliasForExpression.endsWith("]")) {
            this.aliasForExpression = this.aliasForExpression.substring(1, this.aliasForExpression.length() - 1);
            this.aliasForExpression = "`" + this.aliasForExpression + "`";
         }

         sc.setAliasForExpression(this.aliasForExpression);
      }

      Vector colexp = sc.getColumnExpression();
      int maxIfcount = GeneralUtil.checkVectorandGetMaxNestedIfCount(colexp, 3);
      sc.setScNestedIfCount(maxIfcount);
      return sc;
   }

   private void standardMySQLSelectFlow(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, boolean fromIfClause, SelectColumn sc, IntoStatement into, Vector v_ce, boolean intoClause, boolean concat, int sql_dialect, int modulus, int integer_division, int div_index, boolean isHyperSql) throws ConvertException {
      boolean isNumeric = false;
      if (this.columnExpression.contains("+") || this.columnExpression.contains("||")) {
         isNumeric = this.isColumnTypeNumeric(from_sqs, this);
         if ((!this.columnExpression.contains("+") || !isNumeric) && !this.isDateAddition) {
            v_ce.add("concat(");
            concat = true;
         }
      }

      int i;
      Object object;
      for(i = 0; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
            v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
            v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
            v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toMySQLSelect(to_sqs, from_sqs, fromIfClause));
         } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
            SelectQueryStatement sqs = (SelectQueryStatement)this.columnExpression.elementAt(i);
            sqs.setQueryConversionPropHandler(from_sqs.getQueryConvPropHandler());
            v_ce.addElement(sqs.toMySQL());
         } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
            v_ce.addElement(((WhereItem)this.columnExpression.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
            v_ce.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof String) {
            String s_ce = (String)this.columnExpression.elementAt(i);
            if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
               v_ce.addElement("CURRENT_TIME");
            } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
               v_ce.addElement("CURRENT_DATE");
            } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
               v_ce.addElement("CURRENT_TIMESTAMP");
            } else if (s_ce.equalsIgnoreCase("CURRENT")) {
               v_ce.addElement("CURRENT_DATE");
            } else if (s_ce.equalsIgnoreCase("::")) {
               this.createCastFunction(v_ce, this.columnExpression, i);
               object = v_ce.get(v_ce.size() - 1);
               if (object instanceof FunctionCalls) {
                  v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toMySQLSelect(to_sqs, from_sqs));
               }

               ++i;
            } else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
               this.createPowerFunction(v_ce, this.columnExpression, i, true);
               ++i;
            } else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 2) {
               if (v_ce.elementAt(i - 1) instanceof String) {
                  sc.setAliasName((String)v_ce.elementAt(i - 1));
               } else {
                  if (!(v_ce.elementAt(i - 1) instanceof TableColumn)) {
                     throw new ConvertException();
                  }

                  sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
               }

               v_ce.setElementAt(" ", i - 1);
            } else if (this.isMainSelectColumn() && s_ce.equalsIgnoreCase("=")) {
               if (fromIfClause) {
                  v_ce.addElement(s_ce);
               } else {
                  int index = concat ? i : i - 1;
                  if (v_ce.elementAt(index) instanceof TableColumn) {
                     String colName = ((TableColumn)v_ce.elementAt(index)).getColumnName();
                     if (colName.startsWith("@")) {
                        into.setIntoClause("INTO");
                        into.setTableOrFileName(colName);
                        intoClause = true;
                     } else {
                        sc.setAliasName(((TableColumn)v_ce.elementAt(index)).getColumnName());
                     }
                  }

                  v_ce.setElementAt(" ", index);
               }
            } else if (!s_ce.trim().equals("+") && !s_ce.trim().equals("||")) {
               if (isHyperSql && s_ce.trim().equalsIgnoreCase("%")) {
                  v_ce.addElement("%");
                  modulus = 1;
               } else if (isHyperSql && s_ce.trim().equals("DIV")) {
                  v_ce.addElement("/");
                  integer_division = 1;
                  div_index = v_ce.size() - 1;
               } else {
                  v_ce.addElement(s_ce);
               }
            } else if (s_ce.trim().equals("+") && !concat) {
               v_ce.addElement("+");
            } else {
               v_ce.addElement(",");
            }
         } else {
            v_ce.addElement(this.columnExpression.elementAt(i));
         }

         modulus = handleModulusOperator(v_ce, modulus, i, 5);
         integer_division = handleIntegerDivision(v_ce, integer_division, div_index, from_sqs, to_sqs);
      }

      if (intoClause) {
         v_ce.addElement(into);
      }

      if (concat) {
         for(i = 1; i < v_ce.size(); ++i) {
            object = v_ce.get(i);
            if (object != null && !(object instanceof String)) {
               FunctionCalls fc = new FunctionCalls();
               TableColumn tc = new TableColumn();
               tc.setColumnName("CAST");
               CharacterClass charCluse = new CharacterClass();
               charCluse.setDatatypeName("CHAR");
               Vector newFunctionArgs = new Vector();
               if (object instanceof SelectColumn) {
                  newFunctionArgs.add((SelectColumn)object);
               } else {
                  SelectColumn sc1 = new SelectColumn();
                  Vector columnExpression = new Vector();
                  columnExpression.add(object);
                  sc1.setColumnExpression(columnExpression);
                  newFunctionArgs.add(sc1);
               }

               newFunctionArgs.add(charCluse);
               fc.setFunctionName(tc);
               fc.setFunctionArguments(newFunctionArgs);
               fc.setAsDatatype("as");
               v_ce.set(i, fc);
            }
         }

         v_ce.addElement(")");
      }

   }

   public SelectColumn toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      sc.setCommentClass((CommentClass)null);
      sc.castToText = this.castToText;
      new String();
      Vector v_ce = new Vector();
      int sql_dialect = 0;
      int division = 0;
      int integer_division = 0;
      int div_index = 0;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      sc.setOpenBrace(this.OpenBrace);
      int i = 0;
      byte interval = 0;

      String str;
      String val;
      while(i < this.columnExpression.size()) {
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            TableColumn tcToBeChanged = (TableColumn)this.columnExpression.get(i);
            val = tcToBeChanged.getTableName() + ".";
            if (this.originalTableNameList != null) {
               if (this.originalTableNameList.containsKey(val)) {
                  TableColumn tc = (TableColumn)this.originalTableNameList.get(val);
                  tcToBeChanged.setTableName(tc.getTableName());
                  this.columnExpression.set(i, tcToBeChanged);
               } else {
                  this.columnExpression.set(i, tcToBeChanged);
               }
            }

            v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i)).toSnowflakeSelect(to_sqs, from_sqs));
         } else if (!(this.columnExpression.elementAt(i) instanceof FunctionCalls)) {
            if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
               v_ce.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toSnowflakeSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
               v_ce.addElement(((WhereItem)this.columnExpression.elementAt(i)).toSnowflakeSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i);
               cs.setCastToTextInsideIf(this.castToText);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toSnowflakeSelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(this.castToText);
                  v_ce.addElement(fc.toSnowflakeSelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i)).setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toSnowflakeSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.columnExpression.elementAt(i)).setReportsMeta(from_sqs == null ? false : from_sqs.getReportsMeta());
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toSnowflake());
            } else if (this.columnExpression.elementAt(i) instanceof Token) {
               str = this.columnExpression.elementAt(i).toString().trim();
               if (str.startsWith("/*") && str.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i);
               if (!s_ce.equalsIgnoreCase("CURRENT TIME") && !s_ce.equalsIgnoreCase("CURTIME")) {
                  if (!s_ce.equalsIgnoreCase("CURRENT DATE") && !s_ce.equalsIgnoreCase("CURDATE")) {
                     if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                        v_ce.addElement("CURRENT_TIMESTAMP");
                     } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                        v_ce.addElement("CURRENT_DATE");
                     } else if (s_ce.equalsIgnoreCase("**")) {
                        v_ce.addElement("^");
                     } else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 2) {
                        if (v_ce.elementAt(i - 1) instanceof String) {
                           sc.setAliasName((String)v_ce.elementAt(i - 1));
                        } else {
                           if (!(v_ce.elementAt(i - 1) instanceof TableColumn)) {
                              throw new ConvertException();
                           }

                           sc.setAliasName(((TableColumn)v_ce.elementAt(i - 1)).getColumnName());
                        }

                        v_ce.setElementAt(" ", i - 1);
                     } else if (s_ce.equalsIgnoreCase("NULL") && i > 1) {
                        if (this.columnExpression.elementAt(i - 1) instanceof String && this.columnExpression.elementAt(i - 1).equals("/")) {
                           throw new ConvertException("Conversion failure.. divided by NULL is not supported");
                        }

                        v_ce.addElement(s_ce);
                     } else if (s_ce.equalsIgnoreCase("IS")) {
                        this.createDecodeFunction(v_ce, this.columnExpression, i);
                        FunctionCalls fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
                        v_ce.setElementAt(fc.toSnowflakeSelect(to_sqs, from_sqs), v_ce.size() - 1);
                     } else if (s_ce.trim().equals("+")) {
                        if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'") && i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                           v_ce.addElement("||");
                        } else {
                           v_ce.addElement("+");
                        }
                     } else if (!s_ce.startsWith("0x") && !s_ce.startsWith("0X")) {
                        if (s_ce.trim().equals("/")) {
                           v_ce.addElement("/");
                           division = 1;
                        } else if (s_ce.trim().equals("DIV")) {
                           v_ce.addElement("/");
                           integer_division = 1;
                           div_index = v_ce.size() - 1;
                        } else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                           v_ce.addElement("");
                        } else {
                           v_ce.addElement(s_ce);
                        }
                     } else {
                        str = s_ce.substring(2);
                        v_ce.addElement("'" + str + "'");
                     }
                  } else {
                     v_ce.addElement("CURRENT_DATE");
                  }
               } else {
                  v_ce.addElement("CURRENT_TIME");
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i));
            }
         } else {
            ((FunctionCalls)this.columnExpression.elementAt(i)).setCastToTextInsideIf(this.castToText);
            Vector funcArguments = ((FunctionCalls)this.columnExpression.get(i)).getFunctionArguments();
            if (funcArguments != null && this.originalTableNameList != null) {
               for(int j = 0; j < funcArguments.size(); ++j) {
                  if (funcArguments.elementAt(j) instanceof SelectColumn) {
                     SelectColumn sc1 = (SelectColumn)funcArguments.get(j);
                     sc1.setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
                  }
               }
            }

            v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toSnowflakeSelect(to_sqs, from_sqs));
         }

         if (division > 0) {
            Object obj;
            String decimalVal;
            if (division > 2) {
               obj = v_ce.get(i);
               val = "-" + obj.toString();
               v_ce.remove(i);
               v_ce.remove(i - 1);
               v_ce.add("");
               decimalVal = modifyDividerValue(obj, val, 15);
               v_ce.add(decimalVal);
               division = 0;
            } else if (division > 1) {
               obj = v_ce.get(i);
               val = obj.toString();
               if (val.equals("-")) {
                  ++division;
               } else {
                  v_ce.remove(i);
                  decimalVal = modifyDividerValue(obj, val, 15);
                  v_ce.add(decimalVal);
                  division = 0;
               }
            } else {
               ++division;
            }
         }

         integer_division = handleIntegerDivision(v_ce, integer_division, div_index, from_sqs, to_sqs);
         if (interval == 1) {
            i += 2;
            interval = 2;
         } else if (interval == 2) {
            --i;
            if (v_ce.get(i + 1).toString().equalsIgnoreCase("week")) {
               v_ce.remove(i + 1);
               v_ce.add("day * 7 * ");
            } else if (v_ce.get(i + 1).toString().equalsIgnoreCase("quarter")) {
               v_ce.remove(i + 1);
               v_ce.add("month * 3 * ");
            } else if (v_ce.get(i + 1).toString().equalsIgnoreCase("microsecond")) {
               v_ce.remove(i + 1);
               v_ce.add("second / 1000000 * ");
            } else {
               v_ce.add("*");
            }

            interval = 3;
         } else if (interval == 3) {
            i += 2;
            interval = 0;
         } else {
            ++i;
         }
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      if (this.aliasName != null && this.isAS == null) {
         sc.setIsAS("AS");
      } else {
         sc.setIsAS(this.isAS);
      }

      sc.setEndsWith(this.endsWith);
      if (sc != null && sc.getAliasName() == null) {
         String aliasNameNew = this.aliasName;
         if (this.aliasName != null) {
            aliasNameNew = this.aliasName.replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
         }

         if (aliasNameNew != null && !aliasNameNew.isEmpty() && to_sqs != null && from_sqs != null && from_sqs.getBooleanValues("can.handle.having.without.group.by")) {
            String isAsNew = sc.getIsAS();
            str = sc.getEndsWith();
            sc.setIsAS((String)null);
            sc.setEndsWith((String)null);
            val = aliasNameNew.toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
            to_sqs.getAliasVsSelectColExpMap().put(val, sc.toString());
            sc.setIsAS(isAsNew);
            sc.setEndsWith(str);
         }

         sc.setAliasName(aliasNameNew);
      }

      return sc;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.commentObj != null) {
         sb.append(this.commentObj.toString().trim() + " ");
      }

      if (this.OpenBrace != null) {
         sb.append(this.OpenBrace);
      }

      for(int i = 0; i < this.columnExpression.size(); ++i) {
         if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
            sb.append("(" + this.columnExpression.elementAt(i).toString() + ")");
         } else {
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
               ((TableColumn)this.columnExpression.elementAt(i)).setObjectContext(this.context);
            } else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
               ((FunctionCalls)this.columnExpression.elementAt(i)).setObjectContext(this.context);
            } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i)).setObjectContext(this.context);
            } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
               ((CaseStatement)this.columnExpression.elementAt(i)).setObjectContext(this.context);
            } else if (this.columnExpression.elementAt(i) instanceof String && i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).equals(".*") && this.context != null) {
               this.columnExpression.setElementAt(this.context.getEquivalent(this.columnExpression.get(i)), i);
            }

            if (i + 1 == this.columnExpression.size()) {
               if (this.columnExpression.elementAt(i) instanceof String && SwisSQLAPI.getProperSelExp()) {
                  sb.append(GeneralUtil.trimIfAliasNameIsEnclosed(this.columnExpression.elementAt(i).toString()).trim());
               } else {
                  sb.append(this.columnExpression.elementAt(i).toString());
               }
            } else {
               String colExprStr = this.columnExpression.elementAt(i).toString() + " ";
               if (colExprStr.trim().equals("-") && !this.isReportsMysqlConversion()) {
                  colExprStr = colExprStr.trim();
               }

               if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).equals(".*")) {
                  colExprStr = colExprStr.trim();
               }

               if (colExprStr.startsWith("--")) {
                  colExprStr = StringFunctions.replaceFirst("/*", "--", colExprStr);
                  colExprStr = colExprStr + "*//*";
               }

               sb.append(colExprStr);
            }
         }
      }

      if (this.CloseBrace != null) {
         sb.append(this.CloseBrace);
      }

      if (this.commentObjAfterToken != null) {
         sb.append(" " + this.commentObjAfterToken.toString().trim());
      }

      if (this.isAS != null && !SwisSQLAPI.getProperSelExp()) {
         sb.append(" " + this.isAS);
      }

      if (this.aliasName != null && !SwisSQLAPI.getProperSelExp()) {
         sb.append(" " + this.aliasName);
      }

      if (this.endsWith != null && !SwisSQLAPI.getProperSelExp()) {
         sb.append(this.endsWith);
      }

      return sb.toString();
   }

   public String getTheCoreSelectItem() {
      StringBuffer sb = new StringBuffer();

      for(int i = 0; i < this.columnExpression.size(); ++i) {
         sb.append(this.columnExpression.elementAt(i).toString() + " ");
      }

      return sb.toString();
   }

   public boolean isAggregateFunction() {
      boolean conta = this.containsAggregateFunction(this);
      return conta;
   }

   public boolean containsAggregateFunction(SelectColumn sCol) {
      Vector colExpr = sCol.getColumnExpression();

      for(int i = 0; i < colExpr.size(); ++i) {
         if (colExpr.elementAt(i) instanceof FunctionCalls) {
            String functionName = ((FunctionCalls)colExpr.elementAt(i)).getFunctionNameAsAString();
            if (functionName != null) {
               if (functionName.equalsIgnoreCase("avg") || functionName.equalsIgnoreCase("count") || functionName.equalsIgnoreCase("max") || functionName.equalsIgnoreCase("min") || functionName.equalsIgnoreCase("sum")) {
                  return true;
               }

               Vector funcArgs = ((FunctionCalls)colExpr.elementAt(i)).getFunctionArguments();
               if (funcArgs != null) {
                  for(int j = 0; j < funcArgs.size(); ++j) {
                     if (funcArgs.get(j) instanceof SelectColumn && this.containsAggregateFunction((SelectColumn)funcArgs.get(j))) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean getOuterJoin() {
      return this.outerJoin;
   }

   public void setOuterJoin(boolean oj) {
      this.outerJoin = oj;
   }

   public String generateFunctionForSubQuery(SelectQueryStatement sqs, Vector parameters) {
      String functionString = "CREATE OR REPLACE FUNCTION ";
      String alias = "TUSER.HASCHILDREN";
      if (this.aliasName != null) {
         alias = "TUSER." + this.aliasName;
      }

      String parameterString = new String();
      String singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
      SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
      String sqlStr = sqs.toString();
      SelectQueryStatement.singleQueryConvertedToMultipleQueryList = singleQueryConvertedToMultipleQueryList;
      sqlStr = StringFunctions.replaceAll("\n\t", "\n", sqlStr);
      if (parameters != null && parameters.size() > 0) {
         for(int i = 0; i < parameters.size(); ++i) {
            if (parameters.get(i) instanceof String) {
               if (i > 0) {
                  parameterString = parameterString + ",\n\t" + (String)parameters.get(i) + " INTEGER";
               } else {
                  parameterString = parameterString + (String)parameters.get(i) + " INTEGER";
               }
            }
         }
      }

      functionString = functionString + alias + "\n(\n\t" + parameterString + "\n)" + "\nRETURN INTEGER" + "\nAS\n\t" + "FUNCTION_VAR INTEGER" + ";\nBEGIN\n\t" + sqlStr.trim() + ";\n\tRETURN FUNCTION_VAR;\nEND;\n/";
      return functionString;
   }

   public Vector findTheParametersToBePassed(SelectQueryStatement sqs) {
      Vector parameters = new Vector();
      Vector fromItemList = new Vector();
      SelectStatement ss = sqs.getSelectStatement();
      Vector selectItemList = ss.getSelectItemList();
      FromClause fc = sqs.getFromClause();
      WhereExpression we = sqs.getWhereExpression();
      if (fc != null) {
         Vector fromTablesList = fc.getFromItemList();
         if (fromTablesList != null) {
            for(int i = 0; i < fromTablesList.size(); ++i) {
               if (fromTablesList.get(i) instanceof FromTable) {
                  FromTable ft = (FromTable)fromTablesList.get(i);
                  if (ft.getAliasName() == null) {
                     Object tableName = ft.getTableName();
                     fromItemList.add(tableName.toString().trim().toLowerCase());
                  } else {
                     if (ft.getTableName() instanceof SelectQueryStatement) {
                        SelectQueryStatement subSQS = (SelectQueryStatement)ft.getTableName();
                        Vector parametersInsideSubquery = this.findTheParametersToBePassed(subSQS);
                        if (parametersInsideSubquery != null && parametersInsideSubquery.size() > 0) {
                           for(int j = 0; j < parametersInsideSubquery.size(); ++j) {
                              if (!parameters.contains(parametersInsideSubquery.get(j))) {
                                 parameters.add(parametersInsideSubquery.get(j));
                              }
                           }
                        }
                     }

                     fromItemList.add(ft.getAliasName().trim().toLowerCase());
                  }
               }
            }
         }
      }

      if (selectItemList != null) {
         for(int i = 0; i < selectItemList.size(); ++i) {
            if (selectItemList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)selectItemList.get(i);
               Vector columnExpression = sc.getColumnExpression();
               this.addParametersForTheFunction(parameters, columnExpression, fromItemList);
            }
         }
      }

      this.processWhereExpressionColumns(we, parameters, fromItemList);
      return parameters;
   }

   public void processWhereExpressionColumns(WhereExpression we, Vector parameters, Vector fromItemList) {
      if (we != null) {
         Vector whereItemsList = we.getWhereItems();
         if (whereItemsList != null) {
            for(int i = 0; i < whereItemsList.size(); ++i) {
               if (whereItemsList.get(i) instanceof WhereItem) {
                  WhereItem wi = (WhereItem)whereItemsList.get(i);
                  WhereColumn leftWC = wi.getLeftWhereExp();
                  if (leftWC != null) {
                     Vector leftColExp = leftWC.getColumnExpression();
                     this.addParametersForTheFunction(parameters, leftColExp, fromItemList);
                  }

                  WhereColumn rightWC = wi.getRightWhereExp();
                  if (rightWC != null) {
                     Vector rightColExp = rightWC.getColumnExpression();
                     this.addParametersForTheFunction(parameters, rightColExp, fromItemList);
                  }
               } else if (whereItemsList.get(i) instanceof WhereExpression) {
                  WhereExpression whereExp = (WhereExpression)whereItemsList.get(i);
                  this.processWhereExpressionColumns(whereExp, parameters, fromItemList);
               }
            }
         }
      }

   }

   public void addParametersForTheFunction(Vector parameters, Vector colExp, Vector fromItemList) {
      if (colExp != null) {
         for(int i = 0; i < colExp.size(); ++i) {
            if (colExp.get(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)colExp.get(i);
               if (tc.getTableName() != null && (fromItemList == null || !fromItemList.contains(tc.getTableName().toLowerCase())) && !parameters.contains(tc.toString().trim())) {
                  parameters.add(tc.toString().trim());
               }
            } else if (colExp.get(i) instanceof String) {
               if (((String)colExp.get(i)).trim().startsWith("@")) {
                  parameters.add(colExp.get(i));
               }
            } else {
               Vector selectColArgs;
               if (colExp.get(i) instanceof FunctionCalls) {
                  selectColArgs = ((FunctionCalls)colExp.get(i)).getFunctionArguments();
                  this.addParametersForTheFunction(parameters, selectColArgs, fromItemList);
               } else if (colExp.get(i) instanceof SelectColumn) {
                  selectColArgs = ((SelectColumn)colExp.get(i)).getColumnExpression();
                  this.addParametersForTheFunction(parameters, selectColArgs, fromItemList);
               }
            }
         }
      }

   }

   public String getReturnDataTypeForCoalesce(FunctionCalls functionCalls, String builtInFunctionName, SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      Vector functionArguments = functionCalls.getFunctionArguments();
      if (functionArguments != null) {
         for(int i = 0; i < functionArguments.size(); ++i) {
            if (functionArguments.get(i) instanceof SelectColumn) {
               SelectColumn selCol = (SelectColumn)functionArguments.get(i);

               try {
                  selCol.toDB2Select(to_sqs, from_sqs);
               } catch (Exception var11) {
                  var11.printStackTrace();
               }

               String str = selCol.toString();
               TableColumn tableCol = new TableColumn();
               tableCol.setColumnName(str);
               String dType = MetadataInfoUtil.getTargetDataTypeForColumn(tableCol);
               if (dType != null) {
                  return dType;
               }

               if (this.corrTableColumn != null) {
                  tableCol.setTableName(this.corrTableColumn.getTableName());
                  return MetadataInfoUtil.getDatatypeName(from_sqs, tableCol);
               }
            }
         }
      }

      return null;
   }

   private int convertNumeralsToInterval(Vector intervalVector, List subList) {
      int processedElements = 0;
      String previousOperator = "";

      for(int k = 0; k < subList.size(); ++k) {
         Object obj = subList.get(k);
         if (obj instanceof String) {
            String objStr = obj.toString().trim();
            if (!this.isDecimal(objStr) || !previousOperator.equalsIgnoreCase("") && !previousOperator.equalsIgnoreCase("+") && !previousOperator.equalsIgnoreCase("-")) {
               if (objStr.length() != 1) {
                  break;
               }

               if (!objStr.equalsIgnoreCase("(") && !objStr.equalsIgnoreCase(")") && !objStr.equalsIgnoreCase("+") && !objStr.equalsIgnoreCase("-") && !objStr.equalsIgnoreCase("*") && !objStr.equalsIgnoreCase("%") && !objStr.equalsIgnoreCase("^")) {
                  if (this.isDecimal(objStr)) {
                     intervalVector.add(objStr);
                     ++processedElements;
                  }
               } else {
                  intervalVector.add(objStr);
                  previousOperator = objStr;
                  ++processedElements;
               }
            } else {
               String intervalStr = SwisSQLUtils.convertDayToInterval(objStr);
               ++processedElements;
               if (k != subList.size() - 1 && subList.get(k + 1).toString().equalsIgnoreCase("/") && subList.get(k + 2).toString().trim().equalsIgnoreCase("24")) {
                  intervalStr = intervalStr.replaceFirst("DAY", "HOUR");
                  k += 2;
                  processedElements += 2;
               }

               intervalVector.add(intervalStr);
            }
         }
      }

      return processedElements;
   }

   public void replaceRownumTableColumn(Object newColumn) throws ConvertException {
      Vector colExp = this.columnExpression;

      for(int k = 0; k < colExp.size(); ++k) {
         Object obj = colExp.get(k);
         if (obj instanceof TableColumn) {
            TableColumn tcnMod = (TableColumn)obj;
            if (tcnMod.getColumnName().equalsIgnoreCase("rownum")) {
               colExp.setElementAt(newColumn, k);
            }
         } else if (obj instanceof FunctionCalls) {
            Vector funcArgs = ((FunctionCalls)obj).getFunctionArguments();
            if (funcArgs != null) {
               for(int j = 0; j < funcArgs.size(); ++j) {
                  if (funcArgs.get(j) instanceof SelectColumn) {
                     ((SelectColumn)funcArgs.get(j)).replaceRownumTableColumn(newColumn);
                  } else if (funcArgs.get(j) instanceof TableColumn) {
                     TableColumn tcnMod = (TableColumn)funcArgs.get(j);
                     if (tcnMod.getColumnName().equalsIgnoreCase("rownum")) {
                        funcArgs.setElementAt(newColumn, j);
                     }
                  }
               }
            }
         } else if (obj instanceof SelectColumn) {
            ((SelectColumn)obj).replaceRownumTableColumn(newColumn);
         } else if (obj instanceof CaseStatement) {
            CaseStatement csObj = (CaseStatement)obj;
            csObj.replaceRownumTableColumn(newColumn);
         } else if (!(obj instanceof SelectQueryStatement) && obj instanceof String) {
         }
      }

   }

   private boolean isColumnTypeNumeric(SelectQueryStatement from_sqs, SelectColumn sc) {
      return this.isColumnTypeNumeric(from_sqs, sc, false);
   }

   private boolean isColumnTypeNumeric(SelectQueryStatement from_sqs, SelectColumn sc, boolean isVW) {
      return this.isColumnTypeNumeric(from_sqs, sc, isVW, false);
   }

   private boolean isColumnTypeNumeric(SelectQueryStatement from_sqs, SelectColumn sc, boolean isVW, boolean isColExpContainsPlus) {
      if (from_sqs != null && from_sqs.getBooleanValues("allow.new.flow.for.plus")) {
         int NUMERIC = 0;
         int STRING = 1;
         int DATE = 2;
         int UNDEFINED = -2;
         boolean isNumeric = true;
         ArrayList<Integer> argDataType = new ArrayList();
         ArrayList<Integer> plusIndexs = new ArrayList();
         Vector columnExpression = sc.getColumnExpression();
         if (!columnExpression.contains("+")) {
            return true;
         } else {
            try {
               int j;
               for(j = 1; j < columnExpression.size(); ++j) {
                  if (columnExpression.elementAt(j).equals("+") && !columnExpression.elementAt(j - 1).equals("-") && !columnExpression.elementAt(j - 1).equals("*") && !columnExpression.elementAt(j - 1).equals("/") && !columnExpression.elementAt(j - 1).equals("^")) {
                     plusIndexs.add(j);
                  }
               }

               label325:
               for(j = 0; j < plusIndexs.size(); ++j) {
                  int plusPos = (Integer)plusIndexs.get(j);
                  int iterCount = plusPos + 2;
                  if (columnExpression.elementAt(plusPos + 1).equals("-") || columnExpression.elementAt(plusPos + 1).equals("+")) {
                     iterCount = plusPos + 3;
                     if (columnExpression.elementAt(plusPos + 1).equals("+")) {
                        plusIndexs.remove(plusIndexs.indexOf(plusPos) + 1);
                     }
                  }

                  for(int i = plusPos - 1; i < iterCount; ++i) {
                     if (i - 2 >= 0 && columnExpression.elementAt(i - 2).toString().equalsIgnoreCase("INTERVAL")) {
                        this.isDateAddition = true;
                     } else {
                        String functionName;
                        if (columnExpression.elementAt(i) instanceof TableColumn) {
                           TableColumn tc = (TableColumn)columnExpression.elementAt(i);
                           functionName = tc.getOrigTableName();
                           if (functionName == null && from_sqs != null) {
                              if (tc != null && tc.getColumnName().trim().equalsIgnoreCase("INTERVAL") && columnExpression.contains("+")) {
                                 this.isDateAddition = true;
                                 continue;
                              }

                              try {
                                 Object fromTableObj = MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)tc).getTableName();
                                 if (fromTableObj instanceof String) {
                                    functionName = fromTableObj.toString();
                                 }
                              } catch (NullPointerException var22) {
                              }
                           }

                           if (functionName != null) {
                              String dtype = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                              if (dtype != null) {
                                 dtype = dtype.trim().toUpperCase();
                                 if (dtype.indexOf("PLAIN") != -1) {
                                    argDataType.add(Integer.valueOf(STRING));
                                    break label325;
                                 }

                                 if (dtype.indexOf("INT") == -1 && dtype.indexOf("NUM") == -1) {
                                    if (dtype.indexOf("DATE") != -1) {
                                       this.isDateAddition(argDataType, NUMERIC);
                                       argDataType.add(Integer.valueOf(DATE));
                                    }
                                 } else {
                                    this.isDateAddition(argDataType, DATE);
                                    argDataType.add(Integer.valueOf(NUMERIC));
                                 }
                              } else {
                                 argDataType.add(Integer.valueOf(UNDEFINED));
                              }
                           }
                        } else if (columnExpression.elementAt(i) instanceof String) {
                           String colExpStr = columnExpression.elementAt(i).toString();
                           if (colExpStr.startsWith("'")) {
                              colExpStr = colExpStr.substring(1, colExpStr.length() - 1);
                           }

                           if (!colExpStr.equalsIgnoreCase("+") && !colExpStr.equalsIgnoreCase("-")) {
                              try {
                                 Double.parseDouble(colExpStr);
                                 sc.getColumnExpression().setElementAt(colExpStr, i);
                                 this.isDateAddition(argDataType, DATE);
                                 argDataType.add(Integer.valueOf(NUMERIC));
                              } catch (NumberFormatException var24) {
                                 argDataType.add(Integer.valueOf(STRING));
                                 break label325;
                              }
                           }
                        } else if (columnExpression.elementAt(i) instanceof FunctionCalls) {
                           if (columnExpression.contains("+") || isColExpContainsPlus) {
                              FunctionCalls fc = (FunctionCalls)columnExpression.elementAt(i);
                              functionName = fc.getFunctionName().toString();
                              if (!from_sqs.getBooleanValues("allow.for.plus.operator.as.arthematic.operator")) {
                                 if (functionName.equalsIgnoreCase("max") || functionName.equalsIgnoreCase("min") || functionName.equalsIgnoreCase("count") || functionName.equalsIgnoreCase("avg") || functionName.equalsIgnoreCase("sum") || functionName.equalsIgnoreCase("to_number") || functionName.equalsIgnoreCase("pow")) {
                                    argDataType.add(Integer.valueOf(NUMERIC));
                                 }
                              } else if (from_sqs.getBooleanValues("allow.for.plus.operator.as.arthematic.operator") && from_sqs.getValidationHandler() != null) {
                                 if (from_sqs.getValidationHandler().getStringTypeFunctions() != null && from_sqs.getValidationHandler().getStringTypeFunctions().contains(functionName.toUpperCase())) {
                                    argDataType.add(Integer.valueOf(STRING));
                                    break label325;
                                 }

                                 if (from_sqs.getValidationHandler().getNumericTypeFunctions() != null && from_sqs.getValidationHandler().getNumericTypeFunctions().contains(functionName.toUpperCase())) {
                                    this.isDateAddition(argDataType, DATE);
                                    argDataType.add(Integer.valueOf(NUMERIC));
                                 } else if (from_sqs.getValidationHandler().getAllDateTypeFunctions() != null && from_sqs.getValidationHandler().getAllDateTypeFunctions().contains(functionName.toUpperCase())) {
                                    this.isDateAddition(argDataType, NUMERIC);
                                    argDataType.add(Integer.valueOf(DATE));
                                 } else {
                                    argDataType.add(Integer.valueOf(UNDEFINED));
                                 }
                              }
                           }
                        } else if (columnExpression.elementAt(i) instanceof CaseStatement) {
                           CaseStatement cs = (CaseStatement)columnExpression.elementAt(i);
                           Vector v = cs.getWhenClauseList();

                           for(int vi = 0; vi < v.size(); ++vi) {
                              WhenStatement ws = (WhenStatement)v.get(vi);
                              SelectColumn whenSC = ws.getThenStatement();
                              isNumeric = this.isColumnTypeNumeric(from_sqs, whenSC, isVW, isColExpContainsPlus);
                              if (!isNumeric && !this.isDateAddition) {
                                 break label325;
                              }
                           }
                        } else if (columnExpression.elementAt(i) instanceof SelectColumn) {
                           SelectColumn selCol = (SelectColumn)columnExpression.elementAt(i);
                           if (selCol.getColumnExpression().size() != 1 || !(selCol.getColumnExpression().get(0) instanceof FunctionCalls) || !isVW && (from_sqs == null || !from_sqs.isMySqlLive())) {
                              isNumeric = this.isColumnTypeNumeric(from_sqs, (SelectColumn)columnExpression.elementAt(i), isVW, isColExpContainsPlus);
                           } else {
                              isNumeric = this.isColumnTypeNumeric(from_sqs, (SelectColumn)columnExpression.elementAt(i), isVW, true);
                           }

                           if (!isNumeric && !this.isDateAddition) {
                              break label325;
                           }
                        }
                     }
                  }
               }
            } catch (ArrayIndexOutOfBoundsException var25) {
            }

            Iterator var42 = argDataType.iterator();

            while(var42.hasNext()) {
               Integer i = (Integer)var42.next();
               if (i == 1) {
                  isNumeric = false;
                  this.isDateAddition = false;
                  break;
               }

               if (i == 0) {
                  isNumeric = true;
               } else if (i == 2) {
                  isNumeric = false;
               } else if (i == -1) {
                  isNumeric = true;
               }
            }

            return isNumeric;
         }
      } else {
         boolean isNumeric = false;
         Vector columnExpression = sc.getColumnExpression();

         for(int i = 0; i < columnExpression.size(); ++i) {
            String functionName;
            if (columnExpression.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)columnExpression.elementAt(i);
               functionName = tc.getOrigTableName();
               if (functionName == null && from_sqs != null) {
                  if (tc != null && tc.getColumnName().trim().equalsIgnoreCase("INTERVAL") && columnExpression.contains("+")) {
                     this.isDateAddition = true;
                  }

                  try {
                     Object fromTableObj = MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)tc).getTableName();
                     if (fromTableObj instanceof String) {
                        functionName = fromTableObj.toString();
                     }
                  } catch (NullPointerException var23) {
                  }
               }

               if (functionName != null) {
                  String dtype = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                  if (dtype != null) {
                     dtype = dtype.trim().toUpperCase();
                     if (dtype.indexOf("INT") != -1 || dtype.indexOf("NUM") != -1) {
                        isNumeric = true;
                        break;
                     }

                     isNumeric = false;
                  } else {
                     isNumeric = true;
                  }
               }
            } else if (columnExpression.elementAt(i) instanceof String) {
               String colExpStr = columnExpression.elementAt(i).toString();
               if (colExpStr.startsWith("'")) {
                  colExpStr = colExpStr.substring(1, colExpStr.length() - 1);
               }

               if (!colExpStr.equalsIgnoreCase("+")) {
                  try {
                     Double.parseDouble(colExpStr);
                     isNumeric = true;
                     break;
                  } catch (NumberFormatException var26) {
                     isNumeric = false;
                  }
               }
            } else if (columnExpression.elementAt(i) instanceof FunctionCalls) {
               if (columnExpression.contains("+") || isColExpContainsPlus) {
                  FunctionCalls fc = (FunctionCalls)columnExpression.elementAt(i);
                  functionName = fc.getFunctionName().toString();
                  if (!from_sqs.getBooleanValues("allow.for.plus.operator.as.arthematic.operator")) {
                     if (functionName.equalsIgnoreCase("max") || functionName.equalsIgnoreCase("min") || functionName.equalsIgnoreCase("count") || functionName.equalsIgnoreCase("avg") || functionName.equalsIgnoreCase("sum") || functionName.equalsIgnoreCase("to_number") || functionName.equalsIgnoreCase("pow")) {
                        isNumeric = true;
                        break;
                     }
                  } else if (from_sqs.getBooleanValues("allow.for.plus.operator.as.arthematic.operator") && from_sqs.getValidationHandler() != null && from_sqs.getValidationHandler().getNumericTypeFunctions() != null && from_sqs.getValidationHandler().getNumericTypeFunctions().contains(functionName.toUpperCase())) {
                     isNumeric = true;
                     break;
                  }
               }
            } else if (columnExpression.elementAt(i) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)columnExpression.elementAt(i);
               Vector v = cs.getWhenClauseList();

               for(int vi = 0; vi < v.size(); ++vi) {
                  WhenStatement ws = (WhenStatement)v.get(vi);
                  SelectColumn whenSC = ws.getThenStatement();
                  isNumeric = this.isColumnTypeNumeric(from_sqs, whenSC, isVW, isColExpContainsPlus);
                  if (isNumeric) {
                     return isNumeric;
                  }
               }
            } else if (columnExpression.elementAt(i) instanceof SelectColumn) {
               SelectColumn selCol = (SelectColumn)columnExpression.elementAt(i);
               if (selCol.getColumnExpression().size() != 1 || !(selCol.getColumnExpression().get(0) instanceof FunctionCalls) || !isVW && (from_sqs == null || !from_sqs.isMySqlLive())) {
                  isNumeric = this.isColumnTypeNumeric(from_sqs, (SelectColumn)columnExpression.elementAt(i), isVW, isColExpContainsPlus);
               } else {
                  isNumeric = this.isColumnTypeNumeric(from_sqs, (SelectColumn)columnExpression.elementAt(i), isVW, true);
               }

               if (isNumeric) {
                  break;
               }
            }
         }

         return isNumeric;
      }
   }

   public SelectColumn toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = new SelectColumn();
      IntoStatement into = new IntoStatement();
      new String();
      Vector v_ce = new Vector();
      boolean intoClause = false;
      boolean concat = false;
      int sql_dialect = 0;
      int division = 0;
      Integer modulus = 0;
      if (from_sqs != null) {
         from_sqs.getSQLDialect();
      }

      sc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression.contains("+") || this.columnExpression.contains("||")) {
         boolean isNumeric = true;
         if ((!this.columnExpression.contains("+") || !isNumeric) && !this.isDateAddition) {
            v_ce.add("concat(");
            concat = true;
         }
      }

      int i = 0;
      byte interval = 0;

      String tokenStr;
      while(i < this.columnExpression.size()) {
         Object object;
         if (this.columnExpression.elementAt(i) instanceof TableColumn) {
            v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs));
            if (((TableColumn)this.columnExpression.elementAt(i)).toString().equalsIgnoreCase("INTERVAL")) {
               interval = 1;
               v_ce.addElement(" '1' ");
            }
         } else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
            v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
            v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
            v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
            v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i)).toVectorWise());
         } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
            v_ce.addElement(((WhereItem)this.columnExpression.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
            v_ce.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs));
         } else if (this.columnExpression.elementAt(i) instanceof Token) {
            tokenStr = this.columnExpression.elementAt(i).toString().trim();
            if (tokenStr.startsWith("/*") && tokenStr.endsWith("*/")) {
               v_ce.addElement("");
            }
         } else if (this.columnExpression.elementAt(i) instanceof String) {
            String s_ce = (String)this.columnExpression.elementAt(i);
            if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
               v_ce.addElement("CURRENT_TIME");
            } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
               v_ce.addElement("CURRENT_DATE");
            } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
               v_ce.addElement("CURRENT_TIMESTAMP");
            } else if (s_ce.equalsIgnoreCase("CURRENT")) {
               v_ce.addElement("CURRENT_DATE");
            } else if (s_ce.equalsIgnoreCase("::")) {
               this.createCastFunction(v_ce, this.columnExpression, i);
               object = v_ce.get(v_ce.size() - 1);
               if (object instanceof FunctionCalls) {
                  v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toVectorWiseSelect(to_sqs, from_sqs));
               }

               ++i;
            } else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
               this.createPowerFunction(v_ce, this.columnExpression, i, true);
               object = v_ce.get(v_ce.size() - 1);
               if (object instanceof FunctionCalls) {
                  v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toVectorWiseSelect(to_sqs, from_sqs));
               }

               ++i;
            } else if (s_ce.equalsIgnoreCase("%")) {
               v_ce.addElement("%");
               modulus = 1;
            } else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 2) {
               if (v_ce.elementAt(i - 1) instanceof String) {
                  sc.setAliasName(SelectStatement.changeBackTip((String)v_ce.elementAt(i - 1)));
               } else {
                  if (!(v_ce.elementAt(i - 1) instanceof TableColumn)) {
                     throw new ConvertException();
                  }

                  sc.setAliasName(SelectStatement.changeBackTip(((TableColumn)v_ce.elementAt(i - 1)).getColumnName()));
               }

               v_ce.setElementAt(" ", i - 1);
            } else if (!s_ce.trim().equals("+") && !s_ce.trim().equals("||")) {
               if (s_ce.trim().equals("/")) {
                  v_ce.addElement("/");
                  division = 1;
               } else if (s_ce.trim().equals("DIV")) {
                  v_ce.addElement("/");
               } else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                  v_ce.addElement("");
               } else {
                  v_ce.addElement(s_ce);
               }
            } else if (s_ce.trim().equals("+") && !concat) {
               v_ce.addElement("+");
            } else {
               v_ce.addElement(",");
            }
         } else {
            v_ce.addElement(this.columnExpression.elementAt(i));
         }

         if (division > 0) {
            String val;
            String decimalVal;
            if (division > 2) {
               object = v_ce.get(i);
               val = "-" + object.toString();
               v_ce.remove(i);
               v_ce.remove(i - 1);
               v_ce.add("");
               decimalVal = modifyDividerValue(object, val, 13, true);
               v_ce.add(decimalVal);
               division = 0;
            } else if (division > 1) {
               object = v_ce.get(i);
               val = object.toString();
               if (val.equals("-")) {
                  ++division;
               } else {
                  v_ce.remove(i);
                  decimalVal = modifyDividerValue(object, val, 13, true);
                  v_ce.add(decimalVal);
                  division = 0;
               }
            } else {
               ++division;
            }
         }

         modulus = handleModulusOperator(v_ce, modulus, i, 13);
         if (interval == 1) {
            i += 2;
            interval = 2;
         } else if (interval == 2) {
            --i;
            if (v_ce.get(i + 1).toString().equalsIgnoreCase("week")) {
               v_ce.remove(i + 1);
               v_ce.add("day * 7 * ");
            } else if (v_ce.get(i + 1).toString().equalsIgnoreCase("quarter")) {
               v_ce.remove(i + 1);
               v_ce.add("month * 3 * ");
            } else if (v_ce.get(i + 1).toString().equalsIgnoreCase("microsecond")) {
               v_ce.remove(i + 1);
               v_ce.add("second / 1000000 * ");
            } else {
               v_ce.add("*");
            }

            interval = 3;
         } else if (interval == 3) {
            i += 2;
            interval = 0;
         } else {
            ++i;
         }
      }

      if (intoClause) {
         v_ce.addElement(into);
      }

      if (concat) {
         for(i = 1; i < v_ce.size(); ++i) {
            Object obj = v_ce.get(i);
            if (obj != null && !(obj instanceof String)) {
               FunctionCalls fc = new FunctionCalls();
               TableColumn tc = new TableColumn();
               tc.setColumnName("CAST");
               CharacterClass charCluse = new CharacterClass();
               charCluse.setDatatypeName("CHAR");
               Vector newFunctionArgs = new Vector();
               if (obj instanceof SelectColumn) {
                  newFunctionArgs.add((SelectColumn)obj);
               } else {
                  SelectColumn sc1 = new SelectColumn();
                  Vector columnExpression = new Vector();
                  columnExpression.add(obj);
                  sc1.setColumnExpression(columnExpression);
                  newFunctionArgs.add(sc1);
               }

               newFunctionArgs.add(charCluse);
               fc.setFunctionName(tc);
               fc.setFunctionArguments(newFunctionArgs);
               fc.setAsDatatype("as");
               v_ce.set(i, fc);
            }
         }

         v_ce.addElement(")");
      }

      sc.setColumnExpression(v_ce);
      sc.setCloseBrace(this.CloseBrace);
      sc.setIsAS(this.isAS);
      sc.setEndsWith(this.endsWith);
      if (sc != null && sc.getAliasName() == null) {
         String alias;
         String oldAlias;
         if (this.aliasName != null && this.aliasName.charAt(0) == '\'') {
            this.aliasName = this.aliasName.replace('\'', '"');
         } else if (this.aliasName != null && this.aliasName.startsWith("[")) {
            this.aliasName = this.aliasName.replace('[', '"');
            this.aliasName = this.aliasName.replace(']', '"');
         } else if (this.aliasName != null && this.aliasName.startsWith("`")) {
            if (this.aliasName.trim().endsWith("`") && this.aliasName.contains("\"")) {
               oldAlias = this.aliasName;

               try {
                  alias = this.aliasName.trim().substring(1, this.aliasName.trim().length() - 2);
                  alias = alias.replaceAll("\"", "");
                  alias = "\"" + alias + "\"";
                  this.aliasName = alias;
               } catch (Exception var20) {
                  this.aliasName = oldAlias.replace('`', '"');
               }
            } else {
               this.aliasName = this.aliasName.replace('`', '"');
            }
         }

         if (this.aliasName != null && !this.aliasName.isEmpty() && to_sqs != null && from_sqs != null && from_sqs.getBooleanValues("can.handle.having.without.group.by")) {
            oldAlias = sc.getIsAS();
            alias = sc.getEndsWith();
            sc.setIsAS((String)null);
            sc.setEndsWith((String)null);
            tokenStr = this.aliasName.toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
            to_sqs.getAliasVsSelectColExpMap().put(tokenStr, sc.toString());
            sc.setIsAS(oldAlias);
            sc.setEndsWith(alias);
         }

         sc.setAliasName(this.aliasName);
      }

      return sc;
   }

   public SelectColumn toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn selColConv = new SelectColumn();
      if (this.commentObj != null) {
         selColConv.setCommentClass(this.commentObj);
      }

      new IntoStatement();
      Vector colExpConv = new Vector();
      if (this.OpenBrace != null) {
         selColConv.setOpenBrace(this.OpenBrace);
      }

      int i;
      String aliasName;
      for(i = 0; i < this.columnExpression.size(); ++i) {
         if (!(this.columnExpression.elementAt(i) instanceof TableColumn)) {
            if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
               colExpConv.addElement(((FunctionCalls)this.columnExpression.elementAt(i)).toReplaceTblCol(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
               colExpConv.addElement(((CaseStatement)this.columnExpression.elementAt(i)).toReplaceTblCol(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
               colExpConv.addElement(((SelectColumn)this.columnExpression.elementAt(i)).toReplaceTblCol(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)this.columnExpression.elementAt(i);
               sqs.setPropAndHandlerFromSQS(from_sqs);
               colExpConv.addElement(sqs.toReplaceTblCol());
            } else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
               colExpConv.addElement(((WhereItem)this.columnExpression.elementAt(i)).toReplaceTblCol(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
               colExpConv.addElement(((WhereExpression)this.columnExpression.elementAt(i)).toReplaceTblCol(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof String) {
               if (this.columnExpression.size() > i + 1 && String.valueOf(this.columnExpression.get(i + 1)).equals(".*")) {
                  String[] tblDets = GeneralUtil.getReplacedTblColDets(from_sqs.getTableDetsMap(), (String)this.columnExpression.get(i));
                  colExpConv.addElement(tblDets[2]);
               } else if (this.columnExpression.size() > i + 1 && this.columnExpression.elementAt(i).equals("::")) {
                  colExpConv.addElement(this.columnExpression.get(i));
                  colExpConv.addElement(this.columnExpression.get(i + 1));
                  ++i;
               } else {
                  colExpConv.addElement((String)this.columnExpression.elementAt(i));
               }
            } else {
               colExpConv.addElement(this.columnExpression.elementAt(i));
            }
         } else if (i == 0 && this.isEqualOperatorUsedForAliasAssignment(this.isMainSelectColumn())) {
            try {
               SwisSQLAPI.setProperSelExpTL();
               aliasName = this.toString().trim();
               this.aliasForExpression = GeneralUtil.trimIfAliasNameIsEnclosed(((TableColumn)this.columnExpression.get(i)).getColumnName()).trim();
               from_sqs.getAliasColumns().add(this.aliasForExpression);
               if (to_sqs.getSelColNameMap().containsKey(aliasName)) {
                  to_sqs.getSelColNameMap().remove(aliasName);
                  to_sqs.getSelColNameMap().put(this.aliasForExpression, this.aliasForExpression);
               }
            } finally {
               SwisSQLAPI.clearProperSelExpTL();
            }

            colExpConv.addElement(this.columnExpression.get(i));
         } else if (((TableColumn)this.columnExpression.elementAt(i)).toString().trim().equalsIgnoreCase("INTERVAL") && this.columnExpression.size() > i + 2 && SwisSQLAPI.keyWordsAsTblColList.contains(this.columnExpression.elementAt(i + 2).toString().toLowerCase().trim())) {
            colExpConv.addElement(this.columnExpression.elementAt(i));
         } else {
            colExpConv.addElement(((TableColumn)this.columnExpression.elementAt(i)).toReplaceTblCol(to_sqs, from_sqs));
         }
      }

      selColConv.setColumnExpression(colExpConv);
      if (this.CloseBrace != null) {
         selColConv.setCloseBrace(this.CloseBrace);
      }

      if (this.isAS != null) {
         selColConv.setIsAS(this.isAS);
      }

      if (this.aliasName != null) {
         selColConv.setAliasName(this.aliasName);
      }

      if (this.endsWith != null) {
         selColConv.setEndsWith(this.endsWith);
      }

      if (this.commentObjAfterToken != null) {
         selColConv.setCommentClassAfterToken(this.commentObjAfterToken);
      }

      if (this.aliasForExpression != null) {
         selColConv.setAliasForExpression(this.aliasForExpression);
      }

      if (selColConv.getAliasName() == null && (from_sqs.isSubQuerySource() || from_sqs.isCTESupported() && from_sqs.getCTEColumnList().size() == 0) && !selColConv.hasStarInSelectColumn() && this.aliasForExpression != null && !this.isEqualOperatorUsedForAliasAssignment(this.isMainSelectColumn())) {
         i = from_sqs.getSelectStatement().getAliasNameVersion();
         aliasName = null;
         if (i == 1) {
            aliasName = SelectStatement.getProperAliasName(selColConv.toString());
         }

         if (i == 2) {
            try {
               SwisSQLAPI.setProperSelExpTL();
               aliasName = selColConv.toString().trim();
            } catch (Exception var16) {
               new ConvertException("Provide Alias Name");
            } finally {
               SwisSQLAPI.clearProperSelExpTL();
            }
         }

         to_sqs.addSelColNameMap(this.aliasForExpression, aliasName);
         selColConv.setAliasForExpression(aliasName);
      }

      return selColConv;
   }

   public void modifyCurrentTimeAsCurrentTimestamp(SelectQueryStatement from_sqs) {
      try {
         boolean isNotLiveDBs = from_sqs != null && !from_sqs.isAmazonRedShift() && !from_sqs.isPgsqlLive() && !from_sqs.isVerticaDb();
         if (isNotLiveDBs && this.getColumnExpression().size() == 1 && this.getColumnExpression().get(0) != null && this.getColumnExpression().get(0) instanceof FunctionCalls && ((FunctionCalls)this.getColumnExpression().get(0)).getFunctionNameAsAString() != null && ((FunctionCalls)this.getColumnExpression().get(0)).getFunctionNameAsAString().equalsIgnoreCase("CURRENT_TIME")) {
            TableColumn fnName = ((FunctionCalls)this.getColumnExpression().get(0)).getFunctionName();
            fnName.setColumnName("CURRENT_TIMESTAMP");
         }
      } catch (Exception var4) {
      }

   }

   public void convertAsNumericUDFFunctionCall(SelectQueryStatement from_sqs) {
      try {
         boolean isPropEnabled = from_sqs != null && !from_sqs.isAmazonRedShift() && !from_sqs.isPgsqlLive() && !from_sqs.isVerticaDb() && from_sqs.getBooleanValues("can.use.udf.for.decimal.casting");
         if (isPropEnabled) {
            FunctionCalls fn = new FunctionCalls();
            TableColumn fnName = new TableColumn();
            fnName.setColumnName("TONUMERIC_UDF");
            SelectColumn fnSel = new SelectColumn();
            fnSel.setColumnExpression(this.columnExpression);
            this.setCloseBrace((String)null);
            this.setOpenBrace((String)null);
            fn.setFunctionName(fnName);
            Vector fnArgVec = new Vector();
            fnArgVec.addElement(fnSel);
            fn.setFunctionArguments(fnArgVec);
            Vector selColExpVec = new Vector();
            selColExpVec.addElement(fn);
            this.setColumnExpression(selColExpVec);
         }
      } catch (Exception var8) {
      }

   }

   public void castCurrentDateAsTimeStamp(SelectQueryStatement from_sqs) {
      try {
         boolean isNotLiveDBs = from_sqs != null && !from_sqs.isAmazonRedShift() && !from_sqs.isPgsqlLive() && !from_sqs.isVerticaDb();
         if (isNotLiveDBs && this.getColumnExpression().size() == 1 && this.getColumnExpression().get(0) != null && this.getColumnExpression().get(0) instanceof FunctionCalls && ((FunctionCalls)this.getColumnExpression().get(0)).getFunctionNameAsAString() != null && ((FunctionCalls)this.getColumnExpression().get(0)).getFunctionNameAsAString().equalsIgnoreCase("CURRENT_DATE")) {
            FunctionCalls fn = new FunctionCalls();
            TableColumn fnName = new TableColumn();
            fnName.setColumnName("TIMESTAMP");
            SelectColumn fnSel = new SelectColumn();
            fnSel.setColumnExpression(this.columnExpression);
            this.setCloseBrace((String)null);
            this.setOpenBrace((String)null);
            fn.setFunctionName(fnName);
            Vector fnArgVec = new Vector();
            fnArgVec.addElement(fnSel);
            fn.setFunctionArguments(fnArgVec);
            Vector selColExpVec = new Vector();
            selColExpVec.addElement(fn);
            this.setColumnExpression(selColExpVec);
         }
      } catch (Exception var8) {
      }

   }

   public void trackIndexPositionsForCastingNULLString(int indexPosition, boolean isFirstSelect, Set indexPositionsSet) {
      try {
         if (isFirstSelect || indexPositionsSet.contains(indexPosition)) {
            boolean containsNULLString = this.needsCastingForNULLString();
            if (containsNULLString) {
               if (isFirstSelect && indexPositionsSet != null) {
                  indexPositionsSet.add(indexPosition);
               }
            } else if (indexPositionsSet != null) {
               indexPositionsSet.remove(indexPosition);
            }
         }
      } catch (Exception var5) {
      }

   }

   public boolean needsCastingForNULLString() {
      boolean needsCasting = false;

      try {
         Vector colExp = this.getColumnExpression();
         if (colExp != null && colExp.size() == 1 && colExp.get(0) instanceof String) {
            String exp = colExp.get(0).toString().replaceAll("\\s", "");
            if (exp.equalsIgnoreCase("NULL")) {
               needsCasting = true;
            }
         }
      } catch (Exception var4) {
      }

      return needsCasting;
   }

   public boolean needsCastingForStringLiterals() {
      return this.needsCastingForStringLiterals(false);
   }

   public boolean needsCastingForStringLiterals(boolean checkOtherTypes) {
      boolean needsCasting = false;

      try {
         Vector colExp = this.getColumnExpression();
         if (colExp != null && colExp.size() == 1) {
            if (colExp.get(0) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)colExp.get(0);
               needsCasting = sc.needsCastingForStringLiterals(true);
            } else if (colExp.get(0) instanceof String) {
               String exp = colExp.get(0).toString().replaceAll("\\s", "");
               if (exp.equals("''") || exp.startsWith("'") && exp.endsWith("'")) {
                  needsCasting = true;
               }
            }
         }
      } catch (Exception | StackOverflowError var5) {
      }

      return needsCasting;
   }

   public void convertSelectColumnToTextDataType() {
      this.convertSelectColumnToTextDataType(true, "CHAR");
   }

   public void convertSelectColumnToTextDataType(String dataType) {
      this.convertSelectColumnToTextDataType(true, dataType);
   }

   public Vector getStringFunctionLists() {
      Vector fnList = new Vector();
      fnList.add("CONCAT");
      fnList.add("CONCAT_WS");
      fnList.add("STRING_CONCAT");
      fnList.add("SUBSTRING");
      fnList.add("SUBSTR");
      fnList.add("SUBSTRING_UDF");
      fnList.add("SUBSTRING_INDEX");
      fnList.add("UPPER");
      fnList.add("LOWER");
      fnList.add("LEFT");
      fnList.add("LEFT_UDF");
      fnList.add("RIGHT");
      fnList.add("RIGHT_UDF");
      fnList.add("REVERSE");
      fnList.add("REPEAT");
      fnList.add("REPLACE");
      fnList.add("TRIM");
      fnList.add("LTRIM");
      fnList.add("RTRIM");
      fnList.add("LPAD");
      fnList.add("RPAD");
      fnList.add("SOUNDEX");
      fnList.add("SPACE");
      fnList.add("ZR_TEXTBETWEEN");
      fnList.add("LCASE");
      fnList.add("UCASE");
      return fnList;
   }

   public boolean needsCastingForStringFunctions() {
      boolean needsCasting = false;

      try {
         Vector colExp = this.getColumnExpression();
         if (colExp != null && colExp.size() == 1) {
            if (colExp.get(0) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)colExp.get(0);
               needsCasting = sc.needsCastingForStringFunctions();
            } else if (colExp.get(0) instanceof FunctionCalls) {
               String fnName = ((FunctionCalls)colExp.get(0)).getFunctionNameAsAString();
               if (fnName != null && !fnName.isEmpty() && this.getStringFunctionLists().contains(fnName.toUpperCase())) {
                  needsCasting = true;
               }
            }
         }
      } catch (Exception | StackOverflowError var4) {
      }

      return needsCasting;
   }

   public boolean needsCastingForStringFunctionsInsideIfFunction() {
      boolean needsCasting = false;

      try {
         Vector colExp = this.getColumnExpression();
         if (colExp != null && colExp.size() == 1) {
            if (colExp.get(0) instanceof FunctionCalls && ((FunctionCalls)colExp.get(0)).getFunctionNameAsAString() != null && ((FunctionCalls)colExp.get(0)).getFunctionNameAsAString().equalsIgnoreCase("if")) {
               FunctionCalls fc = (FunctionCalls)colExp.get(0);
               Vector fnArgs = fc.getFunctionArguments();
               if (fnArgs.size() == 3) {
                  SelectColumn sc1 = (SelectColumn)fnArgs.get(1);
                  needsCasting = sc1.needsCastingForStringFunctionsInsideIfFunction();
                  if (!needsCasting) {
                     SelectColumn sc2 = (SelectColumn)fnArgs.get(2);
                     needsCasting = sc2.needsCastingForStringFunctionsInsideIfFunction();
                  }
               }
            } else {
               needsCasting = this.needsCastingForStringFunctions();
            }
         }
      } catch (Exception | StackOverflowError var7) {
      }

      return needsCasting;
   }

   public boolean needsCastingForStringLiteralsInsideIfFunction() {
      boolean needsCasting = false;

      try {
         Vector colExp = this.getColumnExpression();
         if (colExp != null && colExp.size() == 1) {
            if (colExp.get(0) instanceof FunctionCalls && ((FunctionCalls)colExp.get(0)).getFunctionNameAsAString() != null && ((FunctionCalls)colExp.get(0)).getFunctionNameAsAString().equalsIgnoreCase("if")) {
               FunctionCalls fc = (FunctionCalls)colExp.get(0);
               Vector fnArgs = fc.getFunctionArguments();
               if (fnArgs.size() == 3) {
                  SelectColumn sc1 = (SelectColumn)fnArgs.get(1);
                  needsCasting = sc1.needsCastingForStringLiteralsInsideIfFunction();
                  if (!needsCasting) {
                     SelectColumn sc2 = (SelectColumn)fnArgs.get(2);
                     needsCasting = sc2.needsCastingForStringLiteralsInsideIfFunction();
                  }
               }
            } else {
               needsCasting = this.needsCastingForStringLiterals(true);
            }
         }
      } catch (StackOverflowError var7) {
      } catch (Exception var8) {
      }

      return needsCasting;
   }

   public void convertFromNumericArgToStringLiteralArg(boolean needsConversion) {
      try {
         if (needsConversion) {
            Vector colExp = this.getColumnExpression();
            if (colExp != null && colExp.size() == 1) {
               if (colExp.get(0) instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)colExp.get(0);
                  sc.convertFromNumericArgToStringLiteralArg(needsConversion);
               } else if (colExp.get(0) instanceof String) {
                  String exp = colExp.get(0).toString().replaceAll("\\s", "");
                  if (exp.equals("0")) {
                     Vector expNew = new Vector();
                     expNew.add("'0'");
                     this.setColumnExpression(expNew);
                  }
               }
            }
         }
      } catch (Exception | StackOverflowError var5) {
      }

   }

   public void convertSelectColumnNumericArgToStringLiteralArg(boolean needsConversion) {
      try {
         if (needsConversion) {
            Vector colExp = this.getColumnExpression();
            if (colExp != null && colExp.size() == 1 && colExp.get(0) instanceof FunctionCalls && ((FunctionCalls)colExp.get(0)).getFunctionNameAsAString() != null && ((FunctionCalls)colExp.get(0)).getFunctionNameAsAString().equalsIgnoreCase("if")) {
               FunctionCalls fc = (FunctionCalls)colExp.get(0);
               Vector fnArgs = fc.getFunctionArguments();
               if (fnArgs.size() == 3) {
                  SelectColumn sc1 = (SelectColumn)fnArgs.get(1);
                  sc1.convertFromNumericArgToStringLiteralArg(needsConversion);
                  SelectColumn sc2 = (SelectColumn)fnArgs.get(2);
                  sc2.convertFromNumericArgToStringLiteralArg(needsConversion);
               }
            }
         }
      } catch (Exception var7) {
      }

   }

   public void convertSelectColumnArgsToTextDataType(boolean needsCasting) {
      try {
         if (needsCasting) {
            Vector colExp = this.getColumnExpression();
            if (colExp != null) {
               if (colExp.size() == 1 && colExp.get(0) instanceof FunctionCalls && ((FunctionCalls)colExp.get(0)).getFunctionNameAsAString() != null && ((FunctionCalls)colExp.get(0)).getFunctionNameAsAString().equalsIgnoreCase("if")) {
                  FunctionCalls fc = (FunctionCalls)colExp.get(0);
                  Vector fnArgs = fc.getFunctionArguments();
                  if (fnArgs.size() == 3) {
                     SelectColumn sc1 = (SelectColumn)fnArgs.get(1);
                     sc1.setCastToTextInsideIf(true);
                     sc1.convertSelectColumnArgsToTextDataType(needsCasting);
                     sc1.setCastToTextInsideIf(false);
                     SelectColumn sc2 = (SelectColumn)fnArgs.get(2);
                     sc2.setCastToTextInsideIf(true);
                     sc2.convertSelectColumnArgsToTextDataType(needsCasting);
                     sc2.setCastToTextInsideIf(false);
                  }
               } else {
                  this.convertSelectColumnToTextDataType(needsCasting, "CHAR");
               }
            }
         }
      } catch (Exception var7) {
      }

   }

   public void convertSelectColumnToTextDataType(boolean needsCasting) {
      this.convertSelectColumnToTextDataType(needsCasting, "CHAR");
   }

   public void convertSelectColumnToTextDataType(boolean needsCasting, String dataType) {
      try {
         if (needsCasting) {
            Vector colExp = this.getColumnExpression();
            Vector castToCharExp = FunctionCalls.castToCharClass(colExp, dataType);
            if (castToCharExp != null) {
               this.setColumnExpression(castToCharExp);
            }
         }
      } catch (Exception var5) {
      }

   }

   public void convertSelectColumnToTextDataTypeIfChildSelectHasStringLiterals(int selectItemPosition, Set indexPositionsSet) {
      try {
         boolean needsCasting = this.needsCastingForStringLiterals() || indexPositionsSet.contains(selectItemPosition);
         if (needsCasting) {
            this.convertSelectColumnToTextDataType(needsCasting);
            if (indexPositionsSet != null) {
               indexPositionsSet.add(selectItemPosition);
            }
         }
      } catch (Exception var4) {
      }

   }

   public void convertSelectColumnToNumericDataType(boolean needsCasting, String dataType) {
      try {
         if (needsCasting) {
            Vector colExp = this.getColumnExpression();
            Vector castToCharExp = FunctionCalls.castToNumericClass(colExp, dataType);
            if (castToCharExp != null) {
               this.setColumnExpression(castToCharExp);
            }
         }
      } catch (Exception var5) {
      }

   }

   public void convertSelectColumnToNumericDataType(String dataType) {
      this.convertSelectColumnToNumericDataType(this.needsCastingForNULLString(), dataType);
   }

   public void convertWhereExpAloneInsideFunctionTo_IF_Function(int argumentsSize) {
      try {
         Vector colExp = this.getColumnExpression();
         boolean containsWhereExp = false;
         if (argumentsSize == 1 && colExp != null) {
            if (colExp.size() == 1 && (colExp.get(0) instanceof WhereExpression || colExp.get(0) instanceof WhereItem)) {
               containsWhereExp = true;
            } else if (colExp.size() == 3 && colExp.get(0) instanceof String && colExp.get(2) instanceof String && (colExp.get(1) instanceof WhereExpression || colExp.get(1) instanceof WhereItem) && colExp.get(0).toString().trim().equals("(") && colExp.get(2).toString().trim().equals(")")) {
               containsWhereExp = true;
            } else {
               Iterator var4 = colExp.iterator();

               while(var4.hasNext()) {
                  Object argument = var4.next();
                  if (argument instanceof WhereItem) {
                     containsWhereExp = true;
                     break;
                  }

                  if (argument instanceof WhereExpression) {
                     containsWhereExp = true;
                     break;
                  }
               }
            }

            if (containsWhereExp) {
               FunctionCalls fc = new FunctionCalls();
               TableColumn functionName = new TableColumn();
               functionName.setColumnName("IF");
               fc.setFunctionName(functionName);
               Vector subFunctionArgs = new Vector();
               SelectColumn firstArgSC = new SelectColumn();
               Vector firstArgColExp = new Vector();
               firstArgColExp.addAll(colExp);
               firstArgSC.setColumnExpression(firstArgColExp);
               SelectColumn secArgSC = new SelectColumn();
               Vector secArgExp = new Vector();
               secArgExp.add("1");
               secArgSC.setColumnExpression(secArgExp);
               SelectColumn thirdArgSC = new SelectColumn();
               Vector thirdArgExp = new Vector();
               thirdArgExp.add("0");
               thirdArgSC.setColumnExpression(thirdArgExp);
               subFunctionArgs.add(firstArgSC);
               subFunctionArgs.add(secArgSC);
               subFunctionArgs.add(thirdArgSC);
               fc.setFunctionArguments(subFunctionArgs);
               Vector colExpr = new Vector();
               colExpr.add(fc);
               this.setColumnExpression(colExpr);
            }
         }
      } catch (Exception var14) {
      }

   }

   public boolean hasStarInSelectColumn() {
      Vector vc = this.getColumnExpression();
      if (vc.size() == 1 && vc.get(0).toString().trim().equals("*")) {
         return true;
      } else {
         return vc.size() == 2 && vc.get(1).toString().trim().equals(".*");
      }
   }

   public boolean isEqualOperatorUsedForAliasAssignment(boolean isFromMainSqs) {
      Vector vc = this.getColumnExpression();
      return isFromMainSqs && vc.size() >= 3 && vc.get(1).toString().trim().equals("=") && vc.get(0) instanceof TableColumn;
   }

   public static String modifyDividerValue(Object obj, String val, int dbtype) {
      return modifyDividerValue(obj, val, dbtype, false);
   }

   public static String modifyDividerValue(Object obj, String val, int dbtype, boolean isNullIfNotSupportedDBs) {
      boolean isPgSQL = dbtype == 4;
      boolean isExcel = dbtype == 20;
      String nonNullValue;
      if (isPgSQL) {
         nonNullValue = "0.0 :: double precision";
      } else {
         nonNullValue = "0.0";
      }

      String decimalVal = "NULLIF(" + val + "," + nonNullValue + ")";
      if (obj instanceof String) {
         String modLiteralString = StringFunctions.getDecimalString(val, decimalVal);
         if (!decimalVal.equals(modLiteralString)) {
            if (isPgSQL) {
               decimalVal = "(" + modLiteralString + " :: double precision)";
            } else if (isExcel) {
               decimalVal = modLiteralString;
            }
         }
      } else if (obj instanceof SelectQueryStatement) {
         decimalVal = "NULLIF((" + val + "), " + nonNullValue + ")";
      }

      if (isNullIfNotSupportedDBs) {
         decimalVal = "(" + decimalVal + "* 1.0)";
      }

      return decimalVal;
   }

   public static void createIntegerDivision(Vector v_ce, int div_index, SelectQueryStatement from_sqs, SelectQueryStatement to_sqs) throws ConvertException {
      boolean isSaphana = from_sqs != null && from_sqs.isSapHanaDb();
      boolean isExcel = from_sqs != null && from_sqs.isExcel();
      SelectColumn truncFunction = new SelectColumn();
      Vector truncColExp = new Vector();
      Vector truncFunctionArguments = new Vector();
      TableColumn truncTC = new TableColumn();
      FunctionCalls truncFC = new FunctionCalls();
      SelectColumn parentCastFunction = new SelectColumn();
      Vector parentColExp = new Vector();
      Vector parentFunctionArguments = new Vector();
      TableColumn parentTC = new TableColumn();
      FunctionCalls parentFC = new FunctionCalls();
      truncFunction.setColumnExpression(truncColExp);
      truncTC.setColumnName("TRUNC");
      truncFC.setFunctionName(truncTC);
      StringBuilder cast = new StringBuilder();
      int i = div_index - 1;

      while(i <= div_index + 1 && i < v_ce.size()) {
         cast.append(v_ce.elementAt(i).toString());
         v_ce.remove(i);
      }

      truncFunctionArguments.add(cast);
      truncFC.setFunctionArguments(truncFunctionArguments);
      parentTC.setColumnName("CAST");
      parentFC.setAsDatatype("AS");
      parentColExp.add(truncFC);
      parentCastFunction.setColumnExpression(parentColExp);
      parentFunctionArguments.add(parentCastFunction);
      String intSyntax = "INT";
      if (from_sqs != null) {
         if (from_sqs.isBigQueryLive()) {
            intSyntax = "INT64";
         } else if (from_sqs.isDenodo()) {
            intSyntax = "INT8";
         } else if (from_sqs.isHyperSql() || from_sqs.getSQLDialect() == 6) {
            intSyntax = "BIGINT";
         }
      }

      parentFunctionArguments.add(intSyntax);
      parentFC.setFunctionArguments(parentFunctionArguments);
      parentFC.setFunctionName(parentTC);
      v_ce.addElement(parentFC);
      FunctionCalls fc = (FunctionCalls)v_ce.get(v_ce.size() - 1);
      if (isSaphana) {
         v_ce.setElementAt(fc.toSapHanaSelect(to_sqs, from_sqs), v_ce.size() - 1);
      } else if (isExcel) {
         v_ce.setElementAt(fc.toExcelSelect(to_sqs, from_sqs), v_ce.size() - 1);
      } else {
         v_ce.setElementAt(fc, v_ce.size() - 1);
      }

   }

   private void isDateAddition(ArrayList<Integer> argDataType, int type) {
      if (argDataType.size() >= 1 && (Integer)argDataType.get(argDataType.size() - 1) == type) {
         this.isDateAddition = true;
      }

   }
}
