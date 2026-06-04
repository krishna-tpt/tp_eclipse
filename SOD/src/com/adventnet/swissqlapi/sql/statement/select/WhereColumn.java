package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.functions.misc.decode;
import com.adventnet.swissqlapi.sql.parser.Token;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.delete.DeleteQueryStatement;
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
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class WhereColumn {
   private String OpenBrace;
   private Vector columnExpression;
   private String CloseBrace;
   private UserObjectContext context = null;
   private TableColumn corrTableColumn = null;
   private String targetDataType = null;
   private boolean lhs = false;
   private ArrayList fromTableList = null;
   private String tableName = null;
   private String sourceDataType = null;
   private UpdateQueryStatement fromUQS;
   private DeleteQueryStatement fromDQS;
   private CommentClass commentObj;
   private CommentClass commentObjAfterToken;
   private String stmtTableName;

   public void setColumnExpression(Vector v_cn) {
      this.columnExpression = v_cn;
   }

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setOpenBrace(String s_ob) {
      this.OpenBrace = s_ob;
   }

   public void setCloseBrace(String s_cb) {
      this.CloseBrace = s_cb;
   }

   public void setFromUQS(UpdateQueryStatement fromUQS) {
      this.fromUQS = fromUQS;
   }

   public void setFromDQS(DeleteQueryStatement fromDQS) {
      this.fromDQS = fromDQS;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public void setCommentClassAfterToken(CommentClass commentObj) {
      this.commentObjAfterToken = commentObj;
   }

   public void setStmtTableName(String stmtTableName) {
      this.stmtTableName = stmtTableName;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public CommentClass getCommentClassAfterToken() {
      return this.commentObjAfterToken;
   }

   public Vector getColumnExpression() {
      return this.columnExpression;
   }

   public String getOpenBrace() {
      return this.OpenBrace;
   }

   public String getCloseBrace() {
      return this.CloseBrace;
   }

   public void setSourceDataType(String sourceDataType) {
      this.sourceDataType = sourceDataType;
   }

   public String getSourceDataType() {
      return this.sourceDataType;
   }

   public void setTargetDataType(String targetDataType) {
      this.targetDataType = targetDataType;
   }

   public String getTargetDataType() {
      return this.targetDataType;
   }

   public void setLHSExpr(boolean lhs) {
      this.lhs = lhs;
   }

   public boolean isLHSExpr() {
      return this.lhs;
   }

   public TableColumn getCorrTableColumn() {
      return this.corrTableColumn;
   }

   public void setFromTableList(ArrayList fromTableList) {
      this.fromTableList = fromTableList;
   }

   public WhereColumn toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.columnExpression.elementAt(i_count);
               tc.setObjectContext(this.context);
               if (tc.getColumnName() != null) {
                  String column_Name = tc.getColumnName();
                  if (column_Name.trim().length() > 0 && !SwisSQLOptions.TSQLQuotedIdentifier && column_Name.trim().startsWith("\"") && column_Name.trim().endsWith("\"")) {
                     String temp = column_Name.substring(1, column_Name.length() - 1);
                     column_Name = "'" + temp + "'";
                  }

                  tc.setColumnName(column_Name);
               }

               v_ce.addElement(tc.toMSSQLServerSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               if (((FunctionCalls)this.columnExpression.elementAt(i_count)).getFunctionName() == null) {
                  v_ce.addElement((FunctionCalls)this.columnExpression.elementAt(i_count));
               } else {
                  v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toMSSQLServer());
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (SwisSQLOptions.removeDBSchemaQualifier && s_ce.indexOf(".") != -1 && s_ce.indexOf(".") != s_ce.lastIndexOf(".")) {
                  s_ce = s_ce.substring(s_ce.indexOf(".") + 1);
               }

               if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                  v_ce.addElement("CURRENT_TIME");
               } else if (s_ce.equalsIgnoreCase("SYS_GUID")) {
                  v_ce.addElement("NEWID()");
               } else if (s_ce.equalsIgnoreCase("SYSDATE")) {
                  v_ce.addElement("GETDATE()");
               } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                  v_ce.addElement("CURRENT_TIMESTAMP");
               } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                  this.createPowerFunction(v_ce, this.columnExpression, i_count);
               } else if (s_ce.equalsIgnoreCase("::")) {
                  this.createCastFunction(v_ce, this.columnExpression, i_count);
                  ++i_count;
               } else if (s_ce.startsWith(":")) {
                  v_ce.addElement("@" + s_ce.substring(1));
               } else if (s_ce.equalsIgnoreCase("||")) {
                  v_ce.addElement("+");
               } else {
                  v_ce.addElement(s_ce);
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            }
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   public WhereColumn toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               ((FunctionCalls)this.columnExpression.elementAt(i_count)).setObjectContext(this.context);
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i_count)).setObjectContext(this.context);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               ((CaseStatement)this.columnExpression.elementAt(i_count)).setObjectContext(this.context);
               v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.columnExpression.elementAt(i_count)).setObjectContext(this.context);
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toSybase());
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (s_ce.charAt(0) == '"') {
                  v_ce.addElement(s_ce.replace('"', '\''));
               } else if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                  v_ce.addElement("CURRENT_TIME");
               } else if (s_ce.equalsIgnoreCase("SYS_GUID")) {
                  v_ce.addElement("NEWID()");
               } else if (s_ce.equalsIgnoreCase("SYSDATE")) {
                  v_ce.addElement("GETDATE()");
               } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                  v_ce.addElement("CURRENT_TIMESTAMP");
               } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                  this.createPowerFunction(v_ce, this.columnExpression, i_count);
               } else if (s_ce.equalsIgnoreCase("::")) {
                  this.createCastFunction(v_ce, this.columnExpression, i_count);
               } else if (s_ce.startsWith(":")) {
                  v_ce.addElement("@" + s_ce.substring(1));
               } else if (s_ce.equalsIgnoreCase("||")) {
                  v_ce.addElement("+");
               } else {
                  v_ce.addElement(s_ce);
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            }
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      wc.setObjectContext(this.context);
      return wc;
   }

   public void createPowerFunction(Vector v_ce, Vector columnExpression, int i_count) {
      SelectColumn wc_firstarg = new SelectColumn();
      SelectColumn wc_secondarg = new SelectColumn();
      Vector v_farg = new Vector();
      TableColumn tc = new TableColumn();
      FunctionCalls fc = new FunctionCalls();
      Vector vec_firstarg = new Vector();
      Vector vec_secondarg = new Vector();
      tc.setColumnName("POWER");
      fc.setFunctionName(tc);
      vec_firstarg.addElement(columnExpression.elementAt(i_count - 1));
      v_ce.setElementAt(" ", i_count - 1);
      wc_firstarg.setColumnExpression(vec_firstarg);
      v_farg.addElement(wc_firstarg);
      vec_secondarg.addElement(columnExpression.elementAt(i_count + 1));
      wc_secondarg.setColumnExpression(vec_secondarg);
      v_farg.addElement(wc_secondarg);
      fc.setFunctionArguments(v_farg);
      v_ce.addElement(fc);
   }

   public WhereColumn toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toANSI());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
            } else if (!(this.columnExpression.elementAt(i_count) instanceof String)) {
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            } else {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (s_ce.equalsIgnoreCase("**")) {
                  this.createPowerFunction(v_ce, this.columnExpression, i_count);
               } else if (s_ce.trim().startsWith("\"")) {
                  v_ce.addElement(s_ce);
               } else if (s_ce.indexOf(".") == -1) {
                  v_ce.addElement(s_ce);
               } else {
                  Vector tokenVector = new Vector();
                  StringTokenizer st = new StringTokenizer(s_ce, ".");

                  while(st.hasMoreTokens()) {
                     tokenVector.add(st.nextToken());
                  }

                  if (tokenVector.size() == 2) {
                     String table_Column = "\"" + tokenVector.get(0) + "\"" + "." + "\"" + tokenVector.get(1) + "\"";
                     v_ce.addElement(table_Column);
                  } else {
                     v_ce.addElement(s_ce);
                  }
               }
            }
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   public WhereColumn toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               FunctionCalls wcFunc = (FunctionCalls)this.columnExpression.elementAt(i_count);
               if (wcFunc.getFunctionName() == null) {
                  v_ce.addElement((FunctionCalls)this.columnExpression.elementAt(i_count));
               } else {
                  try {
                     wcFunc = wcFunc.toTeradataSelect(to_sqs, from_sqs);
                     v_ce.addElement(wcFunc);
                  } catch (ConvertException var12) {
                     throw var12;
                  }
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toTeradata());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (s_ce.equalsIgnoreCase("**")) {
                  v_ce.addElement(s_ce);
               } else if (!s_ce.startsWith("/*") && !s_ce.startsWith("--")) {
                  if (!s_ce.equalsIgnoreCase("+") && !s_ce.equalsIgnoreCase("-")) {
                     if (s_ce.indexOf(".") != -1 && s_ce.charAt(0) != '\'' && s_ce.charAt(0) != '.') {
                        String[] elements = s_ce.split("\\.");
                        int esize = elements.length;
                        if (esize <= 0) {
                           v_ce.addElement(CustomizeUtil.objectNamesToQuotedIdentifier(s_ce, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1));
                        } else {
                           StringBuffer newS_ce = new StringBuffer();

                           for(int es = 0; es < esize; ++es) {
                              String elem = elements[es];
                              if (!CustomizeUtil.isStartsWithNum(elem)) {
                                 elem = CustomizeUtil.objectNamesToQuotedIdentifier(elem, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
                              }

                              if (elem.equalsIgnoreCase("dual") || elem.equalsIgnoreCase("sys.dual")) {
                                 elem = "\"DUAL\"";
                              }

                              if (es == 0) {
                                 newS_ce.append(elem);
                              } else {
                                 newS_ce.append("." + elem);
                              }
                           }

                           v_ce.addElement(newS_ce.toString());
                           this.columnExpression.setElementAt(newS_ce.toString(), i_count);
                        }
                     } else {
                        v_ce.addElement(s_ce);
                     }
                  } else {
                     v_ce.addElement(s_ce);
                     if (i_count > 0 && i_count < this.columnExpression.size() - 1) {
                        boolean isDateExpr = false;
                        Object obj = this.columnExpression.get(i_count - 1);
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
                           Object nextObj = this.columnExpression.get(i_count + 1);
                           if (nextObj instanceof String) {
                              Vector intervalVector = new Vector();
                              int increaseLoopCount = this.convertNumeralsToInterval(intervalVector, this.columnExpression.subList(i_count + 1, this.columnExpression.size()));
                              if (intervalVector.size() > 0) {
                                 v_ce.addAll(intervalVector);
                                 i_count += increaseLoopCount;
                              }
                           }
                        }
                     }
                  }
               } else {
                  v_ce.addElement(s_ce);
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            }
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   public WhereColumn toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      Integer modulus = 0;
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.columnExpression.elementAt(i_count);
               if (tc.getColumnName() != null) {
                  String column_Name = tc.getColumnName();
                  if (column_Name.equalsIgnoreCase("SEQUENCE_NAME")) {
                     column_Name = "SEQNAME";
                  }

                  tc.setColumnName(column_Name);
               }

               TableColumn newTC;
               if (this.isLHSExpr()) {
                  newTC = new TableColumn();
                  newTC.setColumnName(tc.getColumnName());
                  if (tc.getTableName() == null) {
                     if (this.stmtTableName != null) {
                        newTC.setTableName(this.stmtTableName);
                        tc.setTableName(this.stmtTableName);
                     } else {
                        newTC.setTableName(MetadataInfoUtil.getFromTable(this.fromTableList, tc));
                     }
                  } else {
                     newTC.setTableName(tc.getTableName());
                  }

                  if (this.sourceDataType == null) {
                     this.sourceDataType = MetadataInfoUtil.getTargetDataTypeForColumn(newTC);
                  }
               } else {
                  tc.setTargetDataType(CastingUtil.getDataType(this.targetDataType));
               }

               newTC = tc.toDB2Select(to_sqs, from_sqs);
               if (newTC != null) {
                  if (this.sourceDataType != null && !this.sourceDataType.equalsIgnoreCase("")) {
                     newTC.setSourceDataType(this.sourceDataType);
                  }

                  if (this.stmtTableName != null) {
                     newTC.setTableName((String)null);
                  }
               }

               v_ce.addElement(newTC);
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toDB2());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (s_ce.charAt(0) == '"') {
                  v_ce.addElement(s_ce.replace('"', '\''));
               } else if (s_ce.startsWith("'") && (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1)) {
                  if (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1) {
                     if (s_ce.indexOf("-") != -1) {
                        v_ce.addElement(this.convertToOracleDateFormat(s_ce, "-", true));
                     } else if (s_ce.indexOf("/") != -1) {
                        v_ce.addElement(this.convertToOracleDateFormat(s_ce, "/", true));
                     }
                  }
               } else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                  this.createPowerFunction(v_ce, this.columnExpression, i_count);
               } else if (s_ce.equalsIgnoreCase("%")) {
                  v_ce.addElement("%");
                  modulus = 1;
               } else if (s_ce.equalsIgnoreCase("::")) {
                  this.createCastFunction(v_ce, this.columnExpression, i_count);
               } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                  v_ce.addElement("CURRENT DATE");
               } else if (s_ce.equalsIgnoreCase("NULL")) {
                  v_ce.addElement("CAST(NULL AS INT)");
               } else {
                  Object o1;
                  if (s_ce.trim().equals("/")) {
                     o1 = v_ce.lastElement();
                     FunctionCalls fn = new FunctionCalls();
                     TableColumn tc = new TableColumn();
                     tc.setColumnName("CAST");
                     fn.setFunctionName(tc);
                     Vector args = new Vector();
                     args.add(o1 + " AS DECIMAL");
                     fn.setFunctionArguments(args);
                     v_ce.setElementAt(fn, v_ce.size() - 1);
                     v_ce.addElement(s_ce);
                  } else {
                     Object obj;
                     String datatype1;
                     if (s_ce.trim().equals("+") && i_count != 0 && i_count != this.columnExpression.size() - 1) {
                        o1 = this.columnExpression.get(i_count - 1);
                        obj = this.columnExpression.get(i_count + 1);
                        if (o1.toString().startsWith("'") && o1.toString().endsWith("'") || obj.toString().startsWith("'") && obj.toString().endsWith("'")) {
                           v_ce.add("CONCAT");
                        } else if (o1 instanceof TableColumn && obj instanceof TableColumn) {
                           datatype1 = MetadataInfoUtil.getDatatypeName((SwisSQLStatement)null, (TableColumn)o1);
                           String datatype2 = MetadataInfoUtil.getDatatypeName((SwisSQLStatement)null, (TableColumn)obj);
                           if (datatype1 != null && datatype2 != null) {
                              if (datatype1.trim().toLowerCase().startsWith("varchar") || datatype2.trim().toLowerCase().startsWith("varchar")) {
                                 v_ce.add("CONCAT");
                              }

                              if (datatype1.trim().toLowerCase().startsWith("char") || datatype2.trim().toLowerCase().startsWith("char")) {
                                 v_ce.add("CONCAT");
                              }
                           } else {
                              v_ce.add(s_ce);
                           }
                        } else {
                           v_ce.add(s_ce);
                        }
                     } else {
                        v_ce.addElement(s_ce);
                        int size = v_ce.size();
                        if (size > 2) {
                           obj = v_ce.get(size - 2);
                           if (obj instanceof String) {
                              datatype1 = (String)obj;
                              if (datatype1.trim().equals("+") || datatype1.trim().equals("-")) {
                                 Object exprObj = v_ce.get(size - 3);
                                 if (exprObj instanceof TableColumn) {
                                    TableColumn tc = (TableColumn)exprObj;
                                    String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                                    if (dataType != null && (dataType.equalsIgnoreCase("TIMESTAMP") || dataType.equalsIgnoreCase("DATE"))) {
                                       v_ce.addElement("DAYS");
                                    } else if (SwisSQLAPI.variableDatatypeMapping != null) {
                                       String varDataType = (String)SwisSQLAPI.variableDatatypeMapping.get(tc.getColumnName());
                                       if (varDataType != null && (varDataType.equalsIgnoreCase("TIMESTAMP") || varDataType.equalsIgnoreCase("DATE"))) {
                                          v_ce.addElement("DAYS");
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            }

            modulus = SelectColumn.handleModulusOperator(v_ce, modulus, i_count, 3);
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   public WhereColumn toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      Integer modulus = 0;
      wc.setOpenBrace(this.OpenBrace);
      wc.setCommentClass(this.commentObj);
      if (this.columnExpression != null) {
         for(int i = 0; i < this.columnExpression.size(); ++i) {
            String bitxor;
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.columnExpression.elementAt(i);
               if (this.fromUQS != null) {
                  tc.setFromUQS(this.fromUQS);
               } else if (this.fromDQS != null) {
                  tc.setFromDQS(this.fromDQS);
               }

               if (!this.isLHSExpr()) {
                  this.handleTableColumn(tc, from_sqs, 1);
               }

               if (tc.getColumnName() != null) {
                  bitxor = tc.getColumnName();
                  if (bitxor.equalsIgnoreCase("SEQNAME")) {
                     bitxor = "SEQUENCE_NAME";
                  }

                  tc.setColumnName(bitxor);
               }

               tc = tc.toOracleSelect(to_sqs, from_sqs);
               if (SwisSQLOptions.SetSybaseDoubleQuotedLiteralsToSingleQuotes && tc.getOwnerName() == null && tc.getTableName() == null) {
                  bitxor = tc.getColumnName();
                  if (bitxor.startsWith("\"")) {
                     bitxor = bitxor.replaceAll("'", "\"");
                     bitxor = "'" + bitxor.substring(1, bitxor.length() - 1) + "'";
                     tc.setColumnName(bitxor);
                  }
               }

               v_ce.addElement(tc);
            } else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls temp = ((FunctionCalls)this.columnExpression.elementAt(i)).toOracleSelect(to_sqs, from_sqs);
               v_ce.addElement(temp);
               this.columnExpression.setElementAt(temp, i);
            } else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
               SelectColumn temp = ((SelectColumn)this.columnExpression.elementAt(i)).toOracleSelect(to_sqs, from_sqs);
               v_ce.addElement(temp);
               this.columnExpression.setElementAt(temp, i);
            } else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
               CaseStatement temp = ((CaseStatement)this.columnExpression.elementAt(i)).toOracleSelect(to_sqs, from_sqs);
               v_ce.addElement(temp);
               this.columnExpression.setElementAt(temp, i);
            } else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
               SelectQueryStatement temp = ((SelectQueryStatement)this.columnExpression.elementAt(i)).toOracle();
               v_ce.addElement(temp);
               this.columnExpression.setElementAt(temp, i);
            } else if (this.columnExpression.elementAt(i) instanceof WhereColumn) {
               WhereColumn temp = ((WhereColumn)this.columnExpression.elementAt(i)).toOracleSelect(to_sqs, from_sqs);
               v_ce.addElement(temp);
               this.columnExpression.setElementAt(temp, i);
            } else if (this.columnExpression.elementAt(i) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i);
               if (s_ce.charAt(0) == '"') {
                  v_ce.addElement(s_ce.replace('"', '\''));
               } else if (s_ce.startsWith("'") && (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1)) {
                  if (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1) {
                     if (s_ce.indexOf("-") != -1) {
                        v_ce.addElement(this.convertToOracleDateFormat(s_ce, "-", false));
                     } else if (s_ce.indexOf("/") != -1) {
                        v_ce.addElement(this.convertToOracleDateFormat(s_ce, "/", false));
                     }
                  }
               } else if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
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
                  String s;
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

                     bitxor = this.createBitAnd(newStr, this.columnExpression, i + 1, to_sqs, from_sqs);
                     v_ce.add(bitxor);
                     v_ce.setElementAt(" ", i - 1);
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

                     bitxor = this.createBitOR(newStr, this.columnExpression, i + 1, to_sqs, from_sqs);
                     v_ce.add(bitxor);
                     v_ce.setElementAt(" ", i - 1);
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

                     bitxor = this.createBitXOR(newStr, this.columnExpression, i + 1, to_sqs, from_sqs);
                     v_ce.add(bitxor);
                     v_ce.setElementAt(" ", i - 1);
                  } else if (s_ce.equalsIgnoreCase("::")) {
                     this.createCastFunction(v_ce, this.columnExpression, i);
                  } else if (s_ce.equalsIgnoreCase("**")) {
                     this.createPowerFunction(v_ce, this.columnExpression, i);
                  } else if (!s_ce.trim().equalsIgnoreCase("+")) {
                     if (s_ce.startsWith("@")) {
                        v_ce.addElement(":" + s_ce.substring(1));
                     } else if (!s_ce.startsWith("0x") && !s_ce.startsWith("0X")) {
                        v_ce.addElement(s_ce);
                     } else {
                        newStr = s_ce.substring(2);
                        v_ce.addElement("HEX_TO_NUMBER('" + newStr + "')");
                     }
                  } else {
                     newStr = null;
                     if (i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                        newStr = this.columnExpression.elementAt(i - 1).toString();
                        if (!newStr.equals("''") && StringFunctions.replaceAll("", " ", newStr.substring(1, newStr.length() - 1)).length() != 0) {
                           if (i + 1 >= this.columnExpression.size() || !(this.columnExpression.elementAt(i + 1) instanceof TableColumn) && !(this.columnExpression.elementAt(i + 1) instanceof FunctionCalls)) {
                              if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1) instanceof String && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'")) {
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
                        } else {
                           v_ce.addElement("||");
                        }
                     } else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'")) {
                        newStr = this.columnExpression.elementAt(i + 1).toString();
                        if (!newStr.equals("''") && StringFunctions.replaceAll("", " ", newStr.substring(1, newStr.length() - 1)).length() != 0) {
                           if (i - 1 < 0 || !(this.columnExpression.elementAt(i - 1) instanceof TableColumn) && !(this.columnExpression.elementAt(i - 1) instanceof FunctionCalls)) {
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
                     } else {
                        v_ce.addElement(s_ce);
                     }
                  }
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i));
            }

            modulus = SelectColumn.handleModulusOperator(v_ce, modulus, i, 1);
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
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
                     f_arg = "BITAND(" + first_arg + "," + sec_arg + ")";
                     sec_arg.setElementAt(" ", i - 1);
                     sec_arg.setElementAt(" ", i);
                     Object ne = sec_arg.get(i + 1);
                     if (ne instanceof String) {
                        String ss = ne.toString();
                        sec_arg_str = "((0 - " + ne + ") + 1)";
                        return "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
                     }

                     if (ne instanceof TableColumn) {
                        TableColumn col = ((TableColumn)ne).toOracleSelect(to_sqs, from_sqs);
                        sec_arg_str = "((0 - " + sec_arg_str + col.toString() + ") + 1";
                        return "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
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

   private String convertToOracleDateFormat(String originalString, String hiphenOrSlash, boolean convertDateFormatForDB2) {
      StringTokenizer st = new StringTokenizer(originalString, hiphenOrSlash);
      int count = st.countTokens();
      Vector tokenVector = new Vector();
      if (count != 3) {
         return originalString;
      } else {
         while(st.hasMoreTokens()) {
            tokenVector.add(st.nextToken());
         }

         String str1 = (String)tokenVector.get(0);
         String str2 = (String)tokenVector.get(1);
         String str3 = (String)tokenVector.get(2);
         boolean validDate = true;
         if (str1.startsWith("'")) {
            str1 = str1.substring(1);
         }

         if (str3.endsWith("'")) {
            str3 = str3.substring(0, str3.length() - 1);
         }

         int var11;
         try {
            var11 = Integer.parseInt(str3);
         } catch (NumberFormatException var15) {
            validDate = false;
         }

         try {
            var11 = Integer.parseInt(str1);
         } catch (NumberFormatException var14) {
            validDate = false;
         }

         String convertedSting = "";
         int monthValue;
         String temp;
         if (str1.length() == 2 && str2.length() == 2 && str3.length() == 2 && validDate || str1.length() == 2 && str2.length() == 2 && str3.length() == 4 && validDate || str1.length() == 2 && str2.length() == 3 && str3.length() == 4 && validDate || str1.length() == 2 && str2.length() > 3 && str3.length() == 4 && validDate || str1.length() == 2 && str2.length() > 3 && str3.length() == 2 && validDate) {
            try {
               monthValue = Integer.parseInt(str2);
               if (monthValue > 12 && convertDateFormatForDB2) {
                  temp = str2;
                  str2 = str1;
                  str1 = temp;
               }
            } catch (NumberFormatException var17) {
               if (convertDateFormatForDB2) {
                  str2 = this.convertMonthsToEquivalentMonthValue(str2);
               }
            }

            convertedSting = str3 + hiphenOrSlash + str2 + hiphenOrSlash + str1;
         } else if ((str1.length() != 4 || str2.length() != 3 || str3.length() != 2 || !validDate) && (str1.length() != 4 || str2.length() <= 3 || str3.length() != 2 || !validDate) && (str1.length() != 4 || str2.length() != 2 || str3.length() != 2 || !validDate)) {
            convertedSting = str1 + hiphenOrSlash + str2 + hiphenOrSlash + str3;
         } else {
            try {
               monthValue = Integer.parseInt(str2);
               if (monthValue > 12 && convertDateFormatForDB2) {
                  temp = str2;
                  str2 = str3;
                  str3 = temp;
               }
            } catch (NumberFormatException var16) {
               if (convertDateFormatForDB2) {
                  str2 = this.convertMonthsToEquivalentMonthValue(str2);
               }
            }

            convertedSting = str1 + hiphenOrSlash + str2 + hiphenOrSlash + str3;
         }

         return '\'' + convertedSting + '\'';
      }
   }

   private String convertMonthsToEquivalentMonthValue(String monthName) {
      monthName = monthName.trim().toUpperCase();
      if (!monthName.equals("JAN") && !monthName.equals("JANUARY")) {
         if (!monthName.equals("FEB") && !monthName.equals("FEBRUARY")) {
            if (!monthName.equals("MAR") && !monthName.equals("MARCH")) {
               if (!monthName.equals("APR") && !monthName.equals("APRIL")) {
                  if (monthName.equals("MAY")) {
                     return "05";
                  } else if (!monthName.equals("JUN") && !monthName.equals("JUNE")) {
                     if (!monthName.equals("JUL") && !monthName.equals("JULY")) {
                        if (!monthName.equals("AUG") && !monthName.equals("AUGUST")) {
                           if (!monthName.equals("SEP") && !monthName.equals("SEPTEMBER")) {
                              if (!monthName.equals("OCT") && !monthName.equals("OCTOBER")) {
                                 if (!monthName.equals("NOV") && !monthName.equals("NOVEMBER")) {
                                    return !monthName.equals("DEC") && !monthName.equals("DECEMBER") ? monthName : "12";
                                 } else {
                                    return "11";
                                 }
                              } else {
                                 return "10";
                              }
                           } else {
                              return "09";
                           }
                        } else {
                           return "08";
                        }
                     } else {
                        return "07";
                     }
                  } else {
                     return "06";
                  }
               } else {
                  return "04";
               }
            } else {
               return "03";
            }
         } else {
            return "02";
         }
      } else {
         return "01";
      }
   }

   public WhereColumn toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      Integer modulus = 0;
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toInformix());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof Token) {
               String tokenStr = this.columnExpression.elementAt(i_count).toString().trim();
               if (tokenStr.startsWith("/*") && tokenStr.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                  v_ce.addElement("CURRENT");
               } else if (!s_ce.startsWith("'") || s_ce.indexOf("-") == -1 && s_ce.indexOf("/") == -1) {
                  if (s_ce.equalsIgnoreCase("%")) {
                     v_ce.addElement("%");
                     modulus = 1;
                  } else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                     this.createPowerFunction(v_ce, this.columnExpression, i_count);
                  } else {
                     v_ce.addElement(s_ce);
                  }
               } else if (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1) {
                  if (s_ce.indexOf("-") != -1) {
                     v_ce.addElement(this.convertToOracleDateFormat(s_ce, "-", false));
                  } else if (s_ce.indexOf("/") != -1) {
                     v_ce.addElement(this.convertToOracleDateFormat(s_ce, "/", false));
                  }
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            }

            modulus = SelectColumn.handleModulusOperator(v_ce, modulus, i_count, 6);
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
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

   public WhereColumn toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      int division = 0;
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i_count);
               cs.setCastToTextInsideIf(false);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(false);
                  v_ce.addElement(fc.toBigQuerySelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toBigQuery());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i_count)).setCastToTextInsideIf(false);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof Token) {
               String tokenStr = this.columnExpression.elementAt(i_count).toString().trim();
               if (tokenStr.startsWith("/*") && tokenStr.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                  v_ce.addElement("CURRENT_TIME");
               } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                  v_ce.addElement("CURRENT_TIMESTAMP");
               } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("**")) {
                  v_ce.addElement("^");
               } else if (s_ce.trim().equals("/")) {
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
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            }

            if (division > 0) {
               String decimalVal;
               Object obj;
               String val;
               if (division > 2) {
                  obj = v_ce.get(i_count);
                  val = "-" + obj.toString();
                  v_ce.remove(i_count);
                  v_ce.remove(i_count - 1);
                  v_ce.add("");
                  decimalVal = SelectColumn.modifyDividerValue(obj, val, 14);
                  v_ce.add(decimalVal);
                  division = 0;
               } else if (division > 1) {
                  obj = v_ce.get(i_count);
                  val = obj.toString();
                  if (val.equals("-")) {
                     ++division;
                  } else {
                     v_ce.remove(i_count);
                     decimalVal = SelectColumn.modifyDividerValue(obj, val, 14);
                     v_ce.add(decimalVal);
                     division = 0;
                  }
               } else {
                  ++division;
               }
            }
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   public WhereColumn toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      int division = 0;
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i_count);
               cs.setCastToTextInsideIf(false);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(false);
                  v_ce.addElement(fc.toAthenaSelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toAthena());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i_count)).setCastToTextInsideIf(false);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof Token) {
               String tokenStr = this.columnExpression.elementAt(i_count).toString().trim();
               if (tokenStr.startsWith("/*") && tokenStr.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                  v_ce.addElement("CURRENT_TIME");
               } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                  v_ce.addElement("CURRENT_TIMESTAMP");
               } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("**")) {
                  v_ce.addElement("^");
               } else if (s_ce.trim().equals("/")) {
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
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            }

            if (division > 0) {
               String decimalVal;
               Object obj;
               String val;
               if (division > 2) {
                  obj = v_ce.get(i_count);
                  val = "-" + obj.toString();
                  v_ce.remove(i_count);
                  v_ce.remove(i_count - 1);
                  v_ce.add("");
                  decimalVal = SelectColumn.modifyDividerValue(obj, val, 16);
                  v_ce.add(decimalVal);
                  division = 0;
               } else if (division > 1) {
                  obj = v_ce.get(i_count);
                  val = obj.toString();
                  if (val.equals("-")) {
                     ++division;
                  } else {
                     v_ce.remove(i_count);
                     decimalVal = SelectColumn.modifyDividerValue(obj, val, 16);
                     v_ce.add(decimalVal);
                     division = 0;
                  }
               } else {
                  ++division;
               }
            }
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   public WhereColumn toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      int division = 0;
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i_count);
               cs.setCastToTextInsideIf(false);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(false);
                  v_ce.addElement(fc.toSapHanaSelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toSapHana());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i_count)).setCastToTextInsideIf(false);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof Token) {
               String tokenStr = this.columnExpression.elementAt(i_count).toString().trim();
               if (tokenStr.startsWith("/*") && tokenStr.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                  v_ce.addElement("CURRENT_TIME");
               } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                  v_ce.addElement("CURRENT_TIMESTAMP");
               } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("**")) {
                  v_ce.addElement("^");
               } else if (s_ce.trim().equals("/")) {
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
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            }

            if (division > 0) {
               String decimalVal;
               Object obj;
               String val;
               if (division > 2) {
                  obj = v_ce.get(i_count);
                  val = "-" + obj.toString();
                  v_ce.remove(i_count);
                  v_ce.remove(i_count - 1);
                  v_ce.add("");
                  decimalVal = SelectColumn.modifyDividerValue(obj, val, 17);
                  v_ce.add(decimalVal);
                  division = 0;
               } else if (division > 1) {
                  obj = v_ce.get(i_count);
                  val = obj.toString();
                  if (val.equals("-")) {
                     ++division;
                  } else {
                     v_ce.remove(i_count);
                     decimalVal = SelectColumn.modifyDividerValue(obj, val, 17);
                     v_ce.add(decimalVal);
                     division = 0;
                  }
               } else {
                  ++division;
               }
            }
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   public WhereColumn toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      int division = 0;
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i_count);
               cs.setCastToTextInsideIf(false);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(false);
                  v_ce.addElement(fc.toSqliteSelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toSqlite());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i_count)).setCastToTextInsideIf(false);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof Token) {
               String tokenStr = this.columnExpression.elementAt(i_count).toString().trim();
               if (tokenStr.startsWith("/*") && tokenStr.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                  v_ce.addElement("TIME");
               } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                  v_ce.addElement("DATE");
               } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                  v_ce.addElement("DATETIME");
               } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                  v_ce.addElement("DATE");
               } else if (s_ce.equalsIgnoreCase("**")) {
                  v_ce.addElement("^");
               } else if (s_ce.trim().equals("/")) {
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
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            }

            if (division > 0) {
               String decimalVal;
               Object obj;
               String val;
               if (division > 2) {
                  obj = v_ce.get(i_count);
                  val = "-" + obj.toString();
                  v_ce.remove(i_count);
                  v_ce.remove(i_count - 1);
                  v_ce.add("");
                  decimalVal = SelectColumn.modifyDividerValue(obj, val, 18);
                  v_ce.add(decimalVal);
                  division = 0;
               } else if (division > 1) {
                  obj = v_ce.get(i_count);
                  val = obj.toString();
                  if (val.equals("-")) {
                     ++division;
                  } else {
                     v_ce.remove(i_count);
                     decimalVal = SelectColumn.modifyDividerValue(obj, val, 18);
                     v_ce.add(decimalVal);
                     division = 0;
                  }
               } else {
                  ++division;
               }
            }
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   public WhereColumn toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      int division = 0;
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i_count);
               cs.setCastToTextInsideIf(false);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(false);
                  v_ce.addElement(fc.toExcelSelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toExcel());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i_count)).setCastToTextInsideIf(false);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof Token) {
               String tokenStr = this.columnExpression.elementAt(i_count).toString().trim();
               if (tokenStr.startsWith("/*") && tokenStr.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                  v_ce.addElement("TIME");
               } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                  v_ce.addElement("DATE");
               } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                  v_ce.addElement("DATETIME");
               } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                  v_ce.addElement("DATE");
               } else if (s_ce.equalsIgnoreCase("**")) {
                  v_ce.addElement("^");
               } else if (s_ce.trim().equals("/")) {
                  v_ce.addElement("/");
                  division = 1;
               } else if (s_ce.trim().equals("DIV")) {
                  v_ce.addElement("/");
               } else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                  v_ce.addElement("");
               } else if (s_ce.trim().length() > 1 && StringFunctions.identifyTimeFormate(s_ce.trim().substring(1, s_ce.trim().length() - 1), new String[]{"yyyy-MM-dd HH:mm:ss.S", "yyyy-MM-dd HH:mm:ss.SS", "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss.SSSS", "yyyy-MM-dd HH:mm:ss.SSSSS", "yyyy-MM-dd HH:mm:ss.SSSSSS"}) != null) {
                  s_ce = s_ce.substring(0, s_ce.indexOf(46));
                  v_ce.addElement(s_ce + "'");
               } else {
                  v_ce.addElement(s_ce);
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            }

            if (division > 0) {
               String decimalVal;
               Object obj;
               String val;
               if (division > 2) {
                  obj = v_ce.get(i_count);
                  val = "-" + obj.toString();
                  v_ce.remove(i_count);
                  v_ce.remove(i_count - 1);
                  v_ce.add("");
                  decimalVal = SelectColumn.modifyDividerValue(obj, val, 20);
                  v_ce.add(decimalVal);
                  division = 0;
               } else if (division > 1) {
                  obj = v_ce.get(i_count);
                  val = obj.toString();
                  if (val.equals("-")) {
                     ++division;
                  } else {
                     v_ce.remove(i_count);
                     decimalVal = SelectColumn.modifyDividerValue(obj, val, 20);
                     v_ce.add(decimalVal);
                     division = 0;
                  }
               } else {
                  ++division;
               }
            }
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   public WhereColumn toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      int division = 0;
      int modulus = 0;
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i_count);
               cs.setCastToTextInsideIf(false);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(false);
                  v_ce.addElement(fc.toMsAccessJdbcSelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toMsAccessJdbc());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i_count)).setCastToTextInsideIf(false);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof Token) {
               String tokenStr = this.columnExpression.elementAt(i_count).toString().trim();
               if (tokenStr.startsWith("/*") && tokenStr.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                  v_ce.addElement("CURRENT_TIME");
               } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                  v_ce.addElement("CURRENT_TIMESTAMP");
               } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("**")) {
                  v_ce.addElement("^");
               } else if (s_ce.trim().equals("/")) {
                  v_ce.addElement("/");
                  division = 1;
               } else if (s_ce.trim().equals("DIV")) {
                  v_ce.addElement("/");
               } else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                  v_ce.addElement("");
               } else if (s_ce.equalsIgnoreCase("%")) {
                  v_ce.addElement("%");
                  modulus = 1;
               } else {
                  v_ce.addElement(s_ce);
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            }

            if (division > 0) {
               String decimalVal;
               Object obj;
               String val;
               if (division > 2) {
                  obj = v_ce.get(i_count);
                  val = "-" + obj.toString();
                  v_ce.remove(i_count);
                  v_ce.remove(i_count - 1);
                  v_ce.add("");
                  decimalVal = SelectColumn.modifyDividerValue(obj, val, 21);
                  v_ce.add(decimalVal);
                  division = 0;
               } else if (division > 1) {
                  obj = v_ce.get(i_count);
                  val = obj.toString();
                  if (val.equals("-")) {
                     ++division;
                  } else {
                     v_ce.remove(i_count);
                     decimalVal = SelectColumn.modifyDividerValue(obj, val, 21);
                     v_ce.add(decimalVal);
                     division = 0;
                  }
               } else {
                  ++division;
               }
            }

            modulus = SelectColumn.handleModulusOperator(v_ce, modulus, i_count, 21);
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   public WhereColumn toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      int division = 0;
      Integer modulus = 0;
      boolean multipication = false;
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            Object element;
            String val;
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               ((FunctionCalls)this.columnExpression.elementAt(i_count)).setCastToTextInsideIf(false);
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i_count);
               cs.setCastToTextInsideIf(false);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(false);
                  v_ce.addElement(fc.toPostgreSQLSelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toPostgreSQL());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i_count)).setCastToTextInsideIf(false);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               String val;
               if (this.columnExpression.elementAt(i_count) instanceof Token) {
                  val = this.columnExpression.elementAt(i_count).toString().trim();
                  if (val.startsWith("/*") && val.endsWith("*/")) {
                     v_ce.addElement("");
                  }
               } else if (this.columnExpression.elementAt(i_count) instanceof String) {
                  String s_ce = (String)this.columnExpression.elementAt(i_count);
                  if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                     v_ce.addElement("CURRENT_TIME");
                  } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                     v_ce.addElement("CURRENT_DATE");
                  } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                     v_ce.addElement("CURRENT_TIMESTAMP");
                  } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                     v_ce.addElement("CURRENT_DATE");
                  } else if (s_ce.equalsIgnoreCase("**")) {
                     v_ce.addElement("^");
                  } else if (s_ce.trim().equals("/")) {
                     v_ce.addElement("/");
                     division = 1;
                  } else if (s_ce.trim().equals("*")) {
                     if (i_count < this.columnExpression.size() - 1 && i_count != 0 && !(this.columnExpression.elementAt(i_count + 1) instanceof String) && this.columnExpression.elementAt(i_count - 1) instanceof String) {
                        int maxLength = v_ce.size();
                        v_ce.remove(maxLength - 1);
                        val = (String)this.columnExpression.elementAt(i_count - 1);
                        val = StringFunctions.castMultiplicationOperand(val);
                        v_ce.add(val);
                        v_ce.add(s_ce);
                     } else {
                        v_ce.add(s_ce);
                        multipication = true;
                     }
                  } else if (s_ce.trim().equals("DIV")) {
                     v_ce.addElement("/");
                  } else if (s_ce.trim().equals("%")) {
                     v_ce.addElement("%");
                     modulus = 1;
                  } else if (s_ce.trim().startsWith("'") && s_ce.trim().endsWith("'") && from_sqs != null && !isPostgreLiveDbs) {
                     element = s_ce;
                     boolean canHandleStringLiterals = from_sqs.getBooleanValues("can.handle.string.literals.for.date.time");
                     boolean canHandleStringLiteralsForNumeric = from_sqs.getBooleanValues("can.handle.string.literals.for.numeric");
                     boolean isDateTimeValue = StringFunctions.isDateTimeValue(s_ce);
                     boolean isNumericValue = StringFunctions.isNumericValue(s_ce);
                     if (canHandleStringLiterals) {
                        s_ce = StringFunctions.convertToAnsiDateFormatIfDateLiteralString(s_ce, false);
                     }

                     if (s_ce.equalsIgnoreCase("NULL")) {
                        element = s_ce;
                     } else {
                        try {
                           if (canHandleStringLiteralsForNumeric && isNumericValue) {
                              element = s_ce.trim().replaceAll("'", "");
                           } else if (from_sqs.getBooleanValues("can.cast.string.literal.to.text") && !isDateTimeValue) {
                              FunctionCalls castFn = FunctionCalls.castToCharFunctionCall(s_ce);
                              if (castFn != null) {
                                 element = castFn.toPostgreSQLSelect(to_sqs, from_sqs);
                              }
                           } else {
                              element = s_ce;
                           }
                        } catch (Exception var17) {
                           element = s_ce;
                        }
                     }

                     v_ce.addElement(element);
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
                  } else {
                     v_ce.addElement(s_ce);
                  }
               } else {
                  v_ce.addElement(this.columnExpression.elementAt(i_count));
               }
            }

            if (division > 0) {
               String decimalVal;
               if (division > 2) {
                  element = v_ce.get(i_count);
                  val = "-" + element.toString();
                  v_ce.remove(i_count);
                  v_ce.remove(i_count - 1);
                  v_ce.add("");
                  decimalVal = from_sqs != null && from_sqs.isAmazonRedShift() ? SelectColumn.modifyDividerValue(element, val, -1, true) : SelectColumn.modifyDividerValue(element, val, 4, false);
                  v_ce.add(decimalVal);
                  division = 0;
               } else if (division > 1) {
                  element = v_ce.get(i_count);
                  val = element.toString();
                  if (val.equals("-")) {
                     ++division;
                  } else {
                     v_ce.remove(i_count);
                     decimalVal = from_sqs != null && from_sqs.isAmazonRedShift() ? SelectColumn.modifyDividerValue(element, val, -1, true) : SelectColumn.modifyDividerValue(element, val, 4, false);
                     v_ce.add(decimalVal);
                     division = 0;
                  }
               } else {
                  ++division;
               }
            }

            if (from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("can.use.mod.function")) {
               modulus = SelectColumn.handleModulusOperator(v_ce, modulus, i_count, 4);
            }
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   public WhereColumn toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      int division = 0;
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            Object element;
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               ((FunctionCalls)this.columnExpression.elementAt(i_count)).setCastToTextInsideIf(false);
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               CaseStatement cs = (CaseStatement)this.columnExpression.elementAt(i_count);
               cs.setCastToTextInsideIf(false);
               FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
               if (fc == null) {
                  v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
               } else {
                  fc.setCastToTextInsideIf(false);
                  v_ce.addElement(fc.toSnowflakeSelect(to_sqs, from_sqs));
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toSnowflake());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               ((SelectColumn)this.columnExpression.elementAt(i_count)).setCastToTextInsideIf(false);
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof Token) {
               String tokenStr = this.columnExpression.elementAt(i_count).toString().trim();
               if (tokenStr.startsWith("/*") && tokenStr.endsWith("*/")) {
                  v_ce.addElement("");
               }
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                  v_ce.addElement("CURRENT_TIME");
               } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                  v_ce.addElement("CURRENT_TIMESTAMP");
               } else if (s_ce.equalsIgnoreCase("CURRENT")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("**")) {
                  v_ce.addElement("^");
               } else if (s_ce.trim().equals("/")) {
                  v_ce.addElement("/");
                  division = 1;
               } else if (s_ce.trim().equals("DIV")) {
                  v_ce.addElement("/");
               } else if (s_ce.trim().startsWith("'") && s_ce.trim().endsWith("'")) {
                  element = s_ce;
                  boolean canHandleStringLiterals = from_sqs.getBooleanValues("can.handle.string.literals.for.date.time");
                  boolean canHandleStringLiteralsForNumeric = from_sqs.getBooleanValues("can.handle.string.literals.for.numeric");
                  boolean isDateTimeValue = StringFunctions.isDateTimeValue(s_ce);
                  boolean isNumericValue = StringFunctions.isNumericValue(s_ce);
                  if (canHandleStringLiterals) {
                     s_ce = StringFunctions.convertToAnsiDateFormatIfDateLiteralString(s_ce, false);
                  }

                  if (s_ce.equalsIgnoreCase("NULL")) {
                     element = s_ce;
                  } else {
                     try {
                        if (canHandleStringLiteralsForNumeric && isNumericValue) {
                           element = s_ce.trim().replaceAll("'", "");
                        } else if (from_sqs.getBooleanValues("can.cast.string.literal.to.text") && !isDateTimeValue) {
                           FunctionCalls castFn = FunctionCalls.castToCharFunctionCall(s_ce);
                           if (castFn != null) {
                              element = castFn.toSnowflakeSelect(to_sqs, from_sqs);
                           }
                        } else {
                           element = s_ce;
                        }
                     } catch (Exception var14) {
                        element = s_ce;
                     }
                  }

                  v_ce.addElement(element);
               } else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                  v_ce.addElement("");
               } else {
                  v_ce.addElement(s_ce);
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            }

            if (division > 0) {
               String val;
               String decimalVal;
               if (division > 2) {
                  element = v_ce.get(i_count);
                  val = "-" + element.toString();
                  v_ce.remove(i_count);
                  v_ce.remove(i_count - 1);
                  v_ce.add("");
                  decimalVal = SelectColumn.modifyDividerValue(element, val, 15);
                  v_ce.add(decimalVal);
                  division = 0;
               } else if (division > 1) {
                  element = v_ce.get(i_count);
                  val = element.toString();
                  if (val.equals("-")) {
                     ++division;
                  } else {
                     v_ce.remove(i_count);
                     decimalVal = SelectColumn.modifyDividerValue(element, val, 15);
                     v_ce.add(decimalVal);
                     division = 0;
                  }
               } else {
                  ++division;
               }
            }
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   public WhereColumn toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      wc.setOpenBrace(this.OpenBrace);

      try {
         if (from_sqs.getValidationHandler() != null && from_sqs.getinstanceDataTypeHandler() != null) {
            PostfixFlowForMySQLSelect toMySQL = new PostfixFlowForMySQLSelect();
            Object[] convertedCeNDataType = toMySQL.postFixToMySQLFlowForSc(to_sqs, from_sqs, this.getColumnExpression());
            v_ce = (Vector)convertedCeNDataType[0];
            wc.setColumnExpression(v_ce);
         } else {
            this.oldMySQLSelectFlow(to_sqs, from_sqs, wc, v_ce);
         }
      } catch (Exception var8) {
         if (from_sqs.getinstanceDataTypeHandler() != null) {
            from_sqs.setInstanceDataTypeHandler((InstanceDataTypeHandler)null);
         }

         this.oldMySQLSelectFlow(to_sqs, from_sqs, wc, v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   private void oldMySQLSelectFlow(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, WhereColumn wc, Vector v_ce) throws ConvertException {
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)this.columnExpression.elementAt(i_count);
               sqs.setQueryConversionPropHandler(from_sqs.getQueryConvPropHandler());
               v_ce.addElement(sqs.toMySQL());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                  v_ce.addElement("CURRENT_TIME");
               } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                  v_ce.addElement("CURRENT_DATE");
               } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                  v_ce.addElement("CURRENT_TIMESTAMP");
               } else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                  this.createPowerFunction(v_ce, this.columnExpression, i_count);
                  ++i_count;
               } else if (s_ce.equalsIgnoreCase("::")) {
                  this.createCastFunction(v_ce, this.columnExpression, i_count);
                  ++i_count;
               } else if (s_ce.indexOf(".") != -1 && s_ce.indexOf(".") == s_ce.lastIndexOf(".") && !s_ce.startsWith("'") && !this.isDecimal(s_ce)) {
                  String s_ceTableName = s_ce.substring(0, s_ce.lastIndexOf("."));
                  String s_ceColName = s_ce.substring(s_ce.lastIndexOf(".") + 1);
                  TableColumn tc = new TableColumn();
                  tc.setColumnName(s_ceColName);
                  tc.setTableName(s_ceTableName);
                  v_ce.addElement(tc.toMySQLSelect(to_sqs, from_sqs));
               } else {
                  v_ce.addElement(s_ce);
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            }
         }

         wc.setColumnExpression(v_ce);
      }

   }

   public WhereColumn toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      Integer modulus = 0;
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i = 0; i < this.columnExpression.size(); ++i) {
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.columnExpression.elementAt(i);
               if (!this.isLHSExpr()) {
                  this.handleTableColumn(tc, from_sqs, 10);
               }

               v_ce.addElement(tc.toTimesTenSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls temp = ((FunctionCalls)this.columnExpression.elementAt(i)).toTimesTenSelect(to_sqs, from_sqs);
               v_ce.addElement(temp);
               this.columnExpression.setElementAt(temp, i);
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
               } else if (this.columnExpression.elementAt(i) instanceof WhereColumn) {
                  WhereColumn temp = ((WhereColumn)this.columnExpression.elementAt(i)).toTimesTenSelect(to_sqs, from_sqs);
                  v_ce.addElement(temp);
                  this.columnExpression.setElementAt(temp, i);
               } else if (this.columnExpression.elementAt(i) instanceof String) {
                  String s_ce = (String)this.columnExpression.elementAt(i);
                  if (s_ce.charAt(0) == '"') {
                     v_ce.addElement(s_ce.replace('"', '\''));
                  } else if (s_ce.startsWith("'") && (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1)) {
                     if (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1) {
                        if (s_ce.indexOf("-") != -1) {
                           v_ce.addElement(this.convertToOracleDateFormat(s_ce, "-", false));
                        } else if (s_ce.indexOf("/") != -1) {
                           v_ce.addElement(this.convertToOracleDateFormat(s_ce, "/", false));
                        }
                     }
                  } else if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
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
                        String first_arg;
                        int h;
                        String s;
                        String bitxor;
                        if (s_ce.equalsIgnoreCase("&")) {
                           first_arg = "";
                           if (i >= 1) {
                              if (this.columnExpression.elementAt(i - 1) instanceof SelectColumn) {
                                 first_arg = ((SelectColumn)this.columnExpression.elementAt(i - 1)).toTimesTenSelect(to_sqs, from_sqs).toString();
                              } else if (this.columnExpression.elementAt(i - 1) instanceof FunctionCalls) {
                                 first_arg = ((FunctionCalls)this.columnExpression.elementAt(i - 1)).toTimesTenSelect(to_sqs, from_sqs).toString();
                              } else {
                                 for(h = i - 1; h >= 0; --h) {
                                    s = null;
                                    if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                       s = ((TableColumn)this.columnExpression.elementAt(h)).toTimesTenSelect(to_sqs, from_sqs).toString();
                                    } else {
                                       s = this.columnExpression.elementAt(h).toString();
                                    }

                                    if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ") || this.columnExpression.elementAt(h) instanceof SelectColumn) {
                                       break;
                                    }

                                    first_arg = first_arg + this.columnExpression.elementAt(h).toString();
                                 }
                              }
                           } else {
                              for(h = i - 1; h >= 0; --h) {
                                 s = null;
                                 if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                    s = ((TableColumn)this.columnExpression.elementAt(h)).toTimesTenSelect(to_sqs, from_sqs).toString();
                                 } else {
                                    s = this.columnExpression.elementAt(h).toString();
                                 }

                                 if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ") || this.columnExpression.elementAt(h) instanceof SelectColumn) {
                                    break;
                                 }

                                 first_arg = first_arg + this.columnExpression.elementAt(h).toString();
                              }
                           }

                           bitxor = this.createBitAnd(first_arg, this.columnExpression, i + 1, to_sqs, from_sqs);
                           v_ce.add(bitxor);
                           v_ce.setElementAt(" ", i - 1);
                        } else if (s_ce.equalsIgnoreCase("|")) {
                           first_arg = "";
                           if (i >= 1) {
                              if (this.columnExpression.elementAt(i - 1) instanceof SelectColumn) {
                                 first_arg = ((SelectColumn)this.columnExpression.elementAt(i - 1)).toTimesTenSelect(to_sqs, from_sqs).toString();
                              } else {
                                 for(h = i - 1; h >= 0; --h) {
                                    s = null;
                                    if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                       s = ((TableColumn)this.columnExpression.elementAt(h)).toTimesTenSelect(to_sqs, from_sqs).toString();
                                    } else {
                                       s = this.columnExpression.elementAt(h).toString();
                                    }

                                    if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ") || this.columnExpression.elementAt(h) instanceof SelectColumn) {
                                       break;
                                    }

                                    first_arg = first_arg + this.columnExpression.elementAt(h).toString();
                                 }
                              }
                           } else {
                              for(h = i - 1; h >= 0; --h) {
                                 s = null;
                                 if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                    s = ((TableColumn)this.columnExpression.elementAt(h)).toTimesTenSelect(to_sqs, from_sqs).toString();
                                 } else {
                                    s = this.columnExpression.elementAt(h).toString();
                                 }

                                 if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ") || this.columnExpression.elementAt(h) instanceof SelectColumn) {
                                    break;
                                 }

                                 first_arg = first_arg + this.columnExpression.elementAt(h).toString();
                              }
                           }

                           bitxor = this.createBitOR(first_arg, this.columnExpression, i + 1, to_sqs, from_sqs);
                           v_ce.add(bitxor);
                           v_ce.setElementAt(" ", i - 1);
                        } else if (!s_ce.equalsIgnoreCase("^")) {
                           if (!s_ce.equalsIgnoreCase("::") && !s_ce.equalsIgnoreCase("**")) {
                              if (s_ce.startsWith("@")) {
                                 v_ce.addElement(":" + s_ce.substring(1));
                              } else if (!s_ce.startsWith("0x") && !s_ce.startsWith("0X")) {
                                 v_ce.addElement(s_ce);
                              } else {
                                 first_arg = s_ce.substring(2);
                                 v_ce.addElement("HEX_TO_NUMBER('" + first_arg + "')");
                              }
                           }
                        } else {
                           first_arg = "";
                           if (i >= 1) {
                              if (this.columnExpression.elementAt(i - 1) instanceof SelectColumn) {
                                 first_arg = ((SelectColumn)this.columnExpression.elementAt(i - 1)).toTimesTenSelect(to_sqs, from_sqs).toString();
                              } else {
                                 for(h = i - 1; h >= 0; --h) {
                                    s = null;
                                    if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                       s = ((TableColumn)this.columnExpression.elementAt(h)).toTimesTenSelect(to_sqs, from_sqs).toString();
                                    } else {
                                       s = this.columnExpression.elementAt(h).toString();
                                    }

                                    if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ") || this.columnExpression.elementAt(h) instanceof SelectColumn) {
                                       break;
                                    }

                                    first_arg = first_arg + this.columnExpression.elementAt(h).toString();
                                 }
                              }
                           } else {
                              for(h = i - 1; h >= 0; --h) {
                                 s = null;
                                 if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                    s = ((TableColumn)this.columnExpression.elementAt(h)).toTimesTenSelect(to_sqs, from_sqs).toString();
                                 } else {
                                    s = this.columnExpression.elementAt(h).toString();
                                 }

                                 if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ") || this.columnExpression.elementAt(h) instanceof SelectColumn) {
                                    break;
                                 }

                                 first_arg = first_arg + this.columnExpression.elementAt(h).toString();
                              }
                           }

                           bitxor = this.createBitXOR(first_arg, this.columnExpression, i + 1, to_sqs, from_sqs);
                           v_ce.add(bitxor);
                           v_ce.setElementAt(" ", i - 1);
                        }
                     }
                  }
               } else {
                  v_ce.addElement(this.columnExpression.elementAt(i));
               }
            }

            modulus = SelectColumn.handleModulusOperator(v_ce, modulus, i, 10);
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   public WhereColumn toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toNetezza());
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               String s_ce = (String)this.columnExpression.elementAt(i_count);
               if (s_ce.charAt(0) == '"') {
                  v_ce.addElement(s_ce.replace('"', '\''));
               } else if (s_ce.equalsIgnoreCase("**")) {
                  this.createPowerFunction(v_ce, this.columnExpression, i_count);
               } else {
                  v_ce.addElement(s_ce);
               }
            } else {
               v_ce.addElement(this.columnExpression.elementAt(i_count));
            }
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   private boolean isDecimal(String input) {
      try {
         double a = Double.parseDouble(input);
         return true;
      } catch (Exception var4) {
         return false;
      }
   }

   private void handleTableColumn(TableColumn tc, SelectQueryStatement from_sqs, int target) {
      String columnName = tc.getColumnName();
      if (columnName != null && tc.getTableName() == null && columnName.trim().length() > 0) {
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
            } else {
               Vector newFromItems;
               TableExpression tExpr;
               ArrayList tabClauseList;
               int j;
               Object obj;
               TableObject tabObj;
               String tableName;
               FromTable fromTab;
               FromClause delFC;
               Vector fromItems;
               int j;
               if (this.fromUQS != null) {
                  fc = new FromClause();
                  newFromItems = new Vector();
                  fc.setFromItemList(newFromItems);
                  tExpr = this.fromUQS.getTableExpression();
                  if (tExpr != null) {
                     tabClauseList = tExpr.getTableClauseList();
                     if (tabClauseList != null) {
                        for(j = 0; j < tabClauseList.size(); ++j) {
                           obj = tabClauseList.get(j);
                           if (obj instanceof TableClause) {
                              tabObj = ((TableClause)obj).getTableObject();
                              tableName = tabObj.getTableName();
                              fromTab = new FromTable();
                              fromTab.setTableName(tableName);
                              newFromItems.add(fromTab);
                           }
                        }
                     }
                  }

                  delFC = this.fromUQS.getFromClause();
                  if (delFC != null) {
                     fromItems = delFC.getFromItemList();
                     if (fromItems != null) {
                        for(j = 0; j < fromItems.size(); ++j) {
                           newFromItems.add(fromItems.get(j));
                        }
                     }
                  }
               } else if (this.fromDQS == null) {
                  modify = false;
               } else {
                  fc = new FromClause();
                  newFromItems = new Vector();
                  fc.setFromItemList(newFromItems);
                  tExpr = this.fromDQS.getTableExpression();
                  if (tExpr != null) {
                     tabClauseList = tExpr.getTableClauseList();
                     if (tabClauseList != null) {
                        for(j = 0; j < tabClauseList.size(); ++j) {
                           obj = tabClauseList.get(j);
                           if (obj instanceof TableClause) {
                              tabObj = ((TableClause)obj).getTableObject();
                              tableName = tabObj.getTableName();
                              fromTab = new FromTable();
                              fromTab.setTableName(tableName);
                              newFromItems.add(fromTab);
                           }
                        }
                     }
                  }

                  delFC = this.fromDQS.getFromClause();
                  if (delFC != null) {
                     fromItems = delFC.getFromItemList();
                     if (fromItems != null) {
                        for(j = 0; j < fromItems.size(); ++j) {
                           newFromItems.add(fromItems.get(j));
                        }
                     }
                  }
               }
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
                              tableName.substring(index + 1);
                           }

                           if (tableName.startsWith("\"") || tableName.startsWith("[") || tableName.startsWith("`")) {
                              tableName = tableName.substring(1, tableName.length() - 1);
                           }

                           ArrayList colList = (ArrayList)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getTableColumnListMetadata(), tableName);
                           if (colList == null) {
                              modify = false;
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

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.commentObj != null) {
         sb.append(this.commentObj.toString().trim() + " ");
      }

      if (this.OpenBrace != null) {
         sb.append(this.OpenBrace);
      }

      for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
         if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
            ((TableColumn)this.columnExpression.elementAt(i_count)).setObjectContext(this.context);
         } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
            ((SelectColumn)this.columnExpression.elementAt(i_count)).setObjectContext(this.context);
         } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
            ((FunctionCalls)this.columnExpression.elementAt(i_count)).setObjectContext(this.context);
         }

         if (this.columnExpression.elementAt(i_count) != null) {
            if (this.columnExpression.elementAt(i_count) instanceof String && SwisSQLAPI.getProperSelExp()) {
               sb.append(GeneralUtil.trimIfAliasNameIsEnclosed(this.columnExpression.elementAt(i_count).toString()).trim());
            } else {
               sb.append(this.columnExpression.elementAt(i_count).toString() + " ");
            }
         }
      }

      if (this.CloseBrace != null) {
         sb.append(this.CloseBrace);
      }

      if (this.commentObjAfterToken != null) {
         sb.append(" " + this.commentObjAfterToken.toString().trim());
      }

      return sb.toString();
   }

   public String getTableAlias() {
      if (this.getColumnExpression() != null && this.getColumnExpression().size() > 0) {
         for(int i = 0; i < this.getColumnExpression().size(); ++i) {
            if (this.getColumnExpression().get(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.getColumnExpression().get(i);
               return tc.getTableName();
            }

            if (this.getColumnExpression().get(i) instanceof FunctionCalls) {
               Vector functionArguments = ((FunctionCalls)this.getColumnExpression().get(i)).getFunctionArguments();
               if (functionArguments != null) {
                  for(int j = 0; j < functionArguments.size(); ++j) {
                     if (functionArguments.get(j) instanceof SelectColumn) {
                        String tableName = this.getTableAlias((SelectColumn)functionArguments.get(j));
                        if (tableName != null && !tableName.equals("")) {
                           return tableName;
                        }
                     }
                  }
               } else if (functionArguments == null && this.getColumnExpression().get(i) instanceof decode) {
                  FunctionCalls fnc = (FunctionCalls)this.getColumnExpression().get(i);
                  if (fnc.toString().trim().toLowerCase().startsWith("case") && fnc instanceof decode) {
                     CaseStatement cs = ((decode)fnc).getCaseStatement();
                     WhereItem cswi = (WhereItem)cs.getCaseCondition().getWhereItem().get(0);
                     SelectColumn cssc = (SelectColumn)cswi.getLeftWhereExp().getColumnExpression().get(0);
                     String tableName = this.getTableAlias(cssc);
                     if (tableName != null && !tableName.equals("")) {
                        return tableName;
                     }
                  }
               }
            } else if (this.getColumnExpression().get(i) instanceof String) {
               String tableAliaswhere = (String)this.getColumnExpression().get(i);
               if (tableAliaswhere.startsWith("'")) {
                  return "";
               }

               if (tableAliaswhere.indexOf(".") != -1) {
                  if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46) && SwisSQLOptions.removeDBSchemaQualifier) {
                     return tableAliaswhere.substring(tableAliaswhere.indexOf(46) + 1, tableAliaswhere.lastIndexOf("."));
                  }

                  return tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
               }
            }
         }
      }

      return null;
   }

   private String getTableAlias(SelectColumn sc) {
      if (sc != null && sc.getColumnExpression() != null && sc.getColumnExpression().size() > 0) {
         for(int i = 0; i < sc.getColumnExpression().size(); ++i) {
            if (sc.getColumnExpression().get(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)sc.getColumnExpression().get(i);
               return tc.getTableName();
            }

            if (sc.getColumnExpression().get(i) instanceof FunctionCalls) {
               Vector functionArguments = ((FunctionCalls)sc.getColumnExpression().get(i)).getFunctionArguments();
               if (functionArguments != null) {
                  for(int j = 0; j < functionArguments.size(); ++j) {
                     if (functionArguments.get(j) instanceof SelectColumn) {
                        String tableName = this.getTableAlias((SelectColumn)functionArguments.get(j));
                        if (tableName != null) {
                           return tableName;
                        }
                     }
                  }
               }
            } else if (sc.getColumnExpression().get(i) instanceof String) {
               String tableAliaswhere = (String)sc.getColumnExpression().get(i);
               if (tableAliaswhere.startsWith("'")) {
                  return "";
               }

               if (tableAliaswhere.indexOf(".") != -1) {
                  if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46) && SwisSQLOptions.removeDBSchemaQualifier) {
                     return tableAliaswhere.substring(tableAliaswhere.indexOf(46) + 1, tableAliaswhere.lastIndexOf("."));
                  }

                  return tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
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
      for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
         Object obj = this.columnExpression.elementAt(i_count);
         if (obj instanceof TableColumn) {
            TableColumn tcnMod = (TableColumn)obj;
            if (tcnMod.getColumnName().equalsIgnoreCase("rownum")) {
               this.columnExpression.setElementAt(newColumn, i_count);
            }
         } else if (obj instanceof FunctionCalls) {
            FunctionCalls wcFunc = (FunctionCalls)obj;
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
         } else if (obj instanceof WhereColumn) {
            ((WhereColumn)obj).replaceRownumTableColumn(newColumn);
         } else if (obj instanceof CaseStatement) {
            ((CaseStatement)obj).replaceRownumTableColumn(newColumn);
         } else if (!(obj instanceof SelectQueryStatement)) {
            if (obj instanceof SelectColumn) {
               ((SelectColumn)obj).replaceRownumTableColumn(newColumn);
            } else if (obj instanceof String) {
            }
         }
      }

   }

   public WhereColumn toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn wc = new WhereColumn();
      new String();
      Vector v_ce = new Vector();
      int division = 0;
      Integer modulus = 0;
      wc.setOpenBrace(this.OpenBrace);
      if (this.columnExpression != null) {
         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            String element;
            Object object;
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               v_ce.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               v_ce.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               v_ce.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               v_ce.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               v_ce.addElement(((SelectQueryStatement)this.columnExpression.elementAt(i_count)).toVectorWise());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               v_ce.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
            } else {
               String s_ceTableName;
               if (this.columnExpression.elementAt(i_count) instanceof Token) {
                  s_ceTableName = this.columnExpression.elementAt(i_count).toString().trim();
                  if (s_ceTableName.startsWith("/*") && s_ceTableName.endsWith("*/")) {
                     v_ce.addElement("");
                  }
               } else if (!(this.columnExpression.elementAt(i_count) instanceof String)) {
                  v_ce.addElement(this.columnExpression.elementAt(i_count));
               } else {
                  String s_ce = (String)this.columnExpression.elementAt(i_count);
                  if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                     v_ce.addElement("CURRENT_TIME");
                  } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                     v_ce.addElement("CURRENT_DATE");
                  } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                     v_ce.addElement("CURRENT_TIMESTAMP");
                  } else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                     this.createPowerFunction(v_ce, this.columnExpression, i_count);
                     object = v_ce.get(v_ce.size() - 1);
                     if (object instanceof FunctionCalls) {
                        v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toVectorWiseSelect(to_sqs, from_sqs));
                     }
                  } else if (s_ce.equalsIgnoreCase("::")) {
                     this.createCastFunction(v_ce, this.columnExpression, i_count);
                     object = v_ce.get(v_ce.size() - 1);
                     if (object instanceof FunctionCalls) {
                        v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toVectorWiseSelect(to_sqs, from_sqs));
                     }
                  } else if (s_ce.indexOf(".") != -1 && s_ce.indexOf(".") == s_ce.lastIndexOf(".") && !s_ce.startsWith("'") && !this.isDecimal(s_ce)) {
                     s_ceTableName = s_ce.substring(0, s_ce.lastIndexOf("."));
                     element = s_ce.substring(s_ce.lastIndexOf(".") + 1);
                     TableColumn tc = new TableColumn();
                     tc.setColumnName(element);
                     tc.setTableName(s_ceTableName);
                     v_ce.addElement(tc.toVectorWiseSelect(to_sqs, from_sqs));
                  } else if (s_ce.trim().equals("%")) {
                     v_ce.addElement("%");
                     modulus = 1;
                  } else if (s_ce.trim().equals("/")) {
                     v_ce.addElement("/");
                     division = 1;
                  } else if (s_ce.trim().equals("DIV")) {
                     v_ce.addElement("/");
                  } else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                     v_ce.addElement("");
                  } else {
                     boolean canHandleStringLiterals = from_sqs != null && from_sqs.getBooleanValues("can.handle.string.literals.for.date.time");
                     element = s_ce;
                     if (canHandleStringLiterals) {
                        element = StringFunctions.convertToAnsiDateFormatIfDateLiteralString(s_ce, true);
                     }

                     v_ce.addElement(element);
                  }
               }
            }

            if (division > 0) {
               String decimalVal;
               if (division > 2) {
                  object = v_ce.get(i_count);
                  element = "-" + object.toString();
                  v_ce.remove(i_count);
                  v_ce.remove(i_count - 1);
                  v_ce.add("");
                  decimalVal = SelectColumn.modifyDividerValue(object, element, 13, true);
                  v_ce.add(decimalVal);
                  division = 0;
               } else if (division > 1) {
                  object = v_ce.get(i_count);
                  element = object.toString();
                  if (element.equals("-")) {
                     ++division;
                  } else {
                     v_ce.remove(i_count);
                     decimalVal = SelectColumn.modifyDividerValue(object, element, 13, true);
                     v_ce.add(decimalVal);
                     division = 0;
                  }
               } else {
                  ++division;
               }
            }

            modulus = SelectColumn.handleModulusOperator(v_ce, modulus, i_count, 13);
         }

         wc.setColumnExpression(v_ce);
      }

      wc.setCloseBrace(this.CloseBrace);
      return wc;
   }

   public WhereColumn toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereColumn whColConv = new WhereColumn();
      if (this.OpenBrace != null) {
         whColConv.setOpenBrace(this.OpenBrace);
      }

      if (this.columnExpression != null) {
         Vector colExprConv = new Vector();

         for(int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
               colExprConv.addElement(((TableColumn)this.columnExpression.elementAt(i_count)).toReplaceTblCol(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
               colExprConv.addElement(((FunctionCalls)this.columnExpression.elementAt(i_count)).toReplaceTblCol(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
               colExprConv.addElement(((WhereColumn)this.columnExpression.elementAt(i_count)).toReplaceTblCol(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
               colExprConv.addElement(((CaseStatement)this.columnExpression.elementAt(i_count)).toReplaceTblCol(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)this.columnExpression.elementAt(i_count);
               sqs.setPropAndHandlerFromSQS(from_sqs);
               colExprConv.addElement(sqs.toReplaceTblCol());
            } else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
               colExprConv.addElement(((SelectColumn)this.columnExpression.elementAt(i_count)).toReplaceTblCol(to_sqs, from_sqs));
            } else if (this.columnExpression.elementAt(i_count) instanceof String) {
               colExprConv.addElement(this.columnExpression.get(i_count));
               if (this.columnExpression.elementAt(i_count).equals("::")) {
                  colExprConv.addElement(this.columnExpression.get(i_count + 1));
                  ++i_count;
               }
            } else {
               colExprConv.addElement(this.columnExpression.get(i_count));
            }
         }

         whColConv.setColumnExpression(colExprConv);
      }

      if (this.CloseBrace != null) {
         whColConv.setCloseBrace(this.CloseBrace);
      }

      if (this.commentObj != null) {
         whColConv.setCommentClass(this.commentObj);
      }

      if (this.commentObjAfterToken != null) {
         whColConv.setCommentClassAfterToken(this.commentObjAfterToken);
      }

      return whColConv;
   }
}
