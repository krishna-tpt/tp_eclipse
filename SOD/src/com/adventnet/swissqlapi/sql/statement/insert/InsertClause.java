package com.adventnet.swissqlapi.sql.statement.insert;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

public class InsertClause {
   private String insert = null;
   private OptionalSpecifier optionalSpecifier = null;
   private TableExpression tblExp = null;
   private ArrayList columnList = null;
   private String with;
   private String lock;
   private String lockStatement;
   public static boolean isOracleDEFColTruncated = false;
   private UserObjectContext context = null;
   private CommentClass commentObj;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setInsert(String s) {
      this.insert = s;
   }

   public void setOptionalSpecifier(OptionalSpecifier ps) {
      this.optionalSpecifier = ps;
   }

   public void setTableExpression(TableExpression tec) {
      this.tblExp = tec;
   }

   public void setColumnList(ArrayList v) {
      this.columnList = v;
   }

   public void setWith(String with) {
      this.with = with;
   }

   public void setLock(String lock) {
      this.lock = lock;
   }

   public void setLockStatement(String lockStatement) {
      this.lockStatement = lockStatement;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public ArrayList getColumnList() {
      return this.columnList;
   }

   public OptionalSpecifier getOptionalSpecifier() {
      return this.optionalSpecifier;
   }

   public TableExpression getTableExpression() {
      return this.tblExp;
   }

   public String getWith() {
      return this.with;
   }

   public String getLock() {
      return this.lock;
   }

   public String getLockStatement() {
      return this.lockStatement;
   }

   public void toOracle(InsertQueryStatement q) throws ConvertException {
      String[] keywords = null;
      if (SwisSQLUtils.getKeywords(1) != null) {
         keywords = (String[])SwisSQLUtils.getKeywords(1);
      }

      this.optionalSpecifier = new OptionalSpecifier();
      this.optionalSpecifier.setInto("INTO");
      this.tblExp.toOracle();
      ArrayList oracleColTruncForDEFAULT = new ArrayList();
      ArrayList vcList = new ArrayList();
      ValuesClause vc = q.getValuesClause();
      ArrayList tablesList = this.tblExp.getTableClauseList();
      String tableName = null;
      if (tablesList != null && tablesList.size() > 0) {
         Object obj = tablesList.get(0);
         if (obj instanceof TableClause) {
            TableClause tc = (TableClause)obj;
            TableObject to = tc.getTableObject();
            tableName = to.getTableName();
            tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, keywords, (ModifiedObjectAttr)null, 1);
            to.setTableName(tableName);
         }
      }

      if (this.with != null && this.lock != null) {
         if (this.lock.equalsIgnoreCase("TABLOCK") || this.lock.equalsIgnoreCase("UPDLOCK")) {
            this.lock = "SHARE";
         }

         if (this.lock.equalsIgnoreCase("TABLOCKX")) {
            this.lock = "EXCLUSIVE";
         }

         if (!this.lock.equalsIgnoreCase("NOLOCK") && !this.lock.equalsIgnoreCase("ROWLOCK") && !this.lock.equalsIgnoreCase("XLOCK")) {
            String lockTableStatement = "LOCK TABLE " + tableName + " IN " + this.lock + " MODE;";
            if (SwisSQLOptions.handleLOCK_HINTSforOracle) {
               this.setLockStatement(lockTableStatement);
            }
         }
      }

      if (vc != null) {
         vcList = vc.getValuesList();
      }

      if (vcList != null && vcList.size() > 3) {
         for(int j = 0; j < vcList.size(); ++j) {
            if (vcList.get(j).toString().trim().toLowerCase().equals("default")) {
               oracleColTruncForDEFAULT.add(new Integer(j));
               if (j != vcList.size() - 2) {
                  oracleColTruncForDEFAULT.add(new Integer(j + 1));
               } else {
                  oracleColTruncForDEFAULT.add(new Integer(j - 1));
               }
            }
         }
      }

      ArrayList dateTypeColumnIndex = new ArrayList();
      ArrayList dateTypeColumnNames = new ArrayList();
      int i;
      String value;
      String insertColumnName;
      if (tableName != null) {
         ArrayList colList = (ArrayList)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getTableColumnListMetadata(), tableName);
         Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getDataTypesFromMetaDataHT(), tableName);
         if (colList != null && colDatatypeTable != null) {
            for(i = 0; i < colList.size(); ++i) {
               value = (String)colList.get(i);
               insertColumnName = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable, value);
               if (insertColumnName != null && (insertColumnName.toLowerCase().indexOf("datetime") != -1 || insertColumnName.toLowerCase().indexOf("date") != -1)) {
                  if (this.columnList != null) {
                     dateTypeColumnNames.add(value.toLowerCase());
                  } else {
                     dateTypeColumnIndex.add(new Integer(i + 1));
                  }
               }
            }
         }
      }

      Object obj;
      String value;
      int valueCount;
      int i;
      if (vcList != null) {
         valueCount = 0;

         for(i = 0; i < vcList.size(); ++i) {
            Object selObj = vcList.get(i);
            if (selObj instanceof SelectColumn && ((SelectColumn)selObj).getColumnExpression().size() == 1) {
               value = selObj.toString().trim();
               if (!value.equals(",") && !value.equals("(") && !value.equals(")")) {
                  ++valueCount;
               }

               insertColumnName = null;
               if (this.columnList != null && vcList.size() == this.columnList.size()) {
                  insertColumnName = this.columnList.get(i).toString().trim().toLowerCase();
                  if (insertColumnName.startsWith("\"") || insertColumnName.startsWith("[")) {
                     insertColumnName = insertColumnName.substring(1, insertColumnName.length() - 1);
                  }
               }

               if (value.startsWith("'") && (dateTypeColumnIndex.contains(new Integer(valueCount)) || insertColumnName != null && dateTypeColumnNames.contains(insertColumnName))) {
                  obj = vcList.get(i);
                  if (obj instanceof SelectColumn) {
                     value = SwisSQLUtils.getDateFormat(value, 1);
                     FunctionCalls fc = new FunctionCalls();
                     TableColumn tc = new TableColumn();
                     tc.setColumnName("TO_DATE");
                     Vector fnArgs = new Vector();
                     if (value != null) {
                        if (value.startsWith("'1900")) {
                           fnArgs.add(value);
                           fnArgs.add("'YYYY-MM-DD HH24:MI:SS'");
                        } else {
                           fnArgs.add(value);
                           fnArgs.add(value);
                        }

                        fc.setFunctionName(tc);
                        fc.setFunctionArguments(fnArgs);
                        ((SelectColumn)obj).getColumnExpression().setElementAt(fc, 0);
                     }
                  }
               }
            }
         }
      }

      if (this.columnList != null) {
         valueCount = this.columnList.size();

         String columnName;
         for(i = 0; i < this.columnList.size(); ++i) {
            if (this.columnList.get(i) instanceof String) {
               columnName = (String)this.columnList.get(i);
               if (!columnName.trim().equals(",") && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                  columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, keywords, (ModifiedObjectAttr)null, 1);
               }

               if (columnName.startsWith("[") && columnName.endsWith("]") || columnName.startsWith("`") && columnName.endsWith("`")) {
                  columnName = columnName.substring(1, columnName.length() - 1);
               }

               if (!columnName.startsWith("\"") && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--") && (SwisSQLOptions.retainQuotedIdentifierForOracle || columnName.indexOf(32) != -1)) {
                  columnName = "\"" + columnName + "\"";
               }

               boolean addQuotes = false;
               if (columnName.startsWith("\"") && columnName.endsWith("\"")) {
                  columnName = columnName.substring(1, columnName.length() - 1);
                  addQuotes = true;
               }

               if (columnName.length() > 30 && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                  columnName = columnName.substring(0, 30);
               }

               if (addQuotes) {
                  columnName = "\"" + columnName + "\"";
               }

               if (!oracleColTruncForDEFAULT.contains(new Integer(i))) {
                  this.columnList.set(i, columnName);
               } else {
                  isOracleDEFColTruncated = true;
                  this.columnList.set(i, (Object)null);
               }
            }
         }

         if (isOracleDEFColTruncated) {
            for(i = 0; i < this.columnList.size(); ++i) {
               this.columnList.remove((Object)null);
               if (i == this.columnList.size() - 2 && this.columnList.get(i) != null) {
                  columnName = (String)this.columnList.get(i);
                  if (columnName.equals(",")) {
                     this.columnList.remove(i);
                  }
               }
            }
         }
      }

      if (q.getSubQuery() != null) {
         valueCount = 0;
         ArrayList dateColumnIndexList = new ArrayList();
         if (this.columnList != null) {
            for(i = 0; i < this.columnList.size(); ++i) {
               if (this.columnList.get(i) instanceof String) {
                  value = this.columnList.get(i).toString();
                  if (!value.equals(",") && !value.equals("(") && !value.equals(")")) {
                     ++valueCount;
                  }

                  if (dateTypeColumnNames.contains(value.toLowerCase())) {
                     dateColumnIndexList.add(new Integer(valueCount));
                  }
               }
            }
         }

         SelectQueryStatement local_Stmt = q.getSubQuery();
         Vector selectQueryColumns = local_Stmt.getSelectStatement().getSelectItemList();

         for(int i = 0; i < selectQueryColumns.size(); ++i) {
            if (dateColumnIndexList.contains(new Integer(i + 1))) {
               obj = selectQueryColumns.get(i);
               if (obj instanceof SelectColumn) {
                  value = obj.toString();
                  if (value.startsWith("'")) {
                     if (value.indexOf(",") == value.length() - 1) {
                        value = value.substring(0, value.length() - 1);
                     }

                     String format = SwisSQLUtils.getDateFormat(value, 1);
                     FunctionCalls fc = new FunctionCalls();
                     TableColumn tc = new TableColumn();
                     tc.setColumnName("TO_DATE");
                     Vector fnArgs = new Vector();
                     if (format != null) {
                        if (format.startsWith("'1900")) {
                           fnArgs.add(format);
                           fnArgs.add("'YYYY-MM-DD HH24:MI:SS'");
                        } else {
                           fnArgs.add(value);
                           fnArgs.add(format);
                        }

                        fc.setFunctionName(tc);
                        fc.setFunctionArguments(fnArgs);
                        ((SelectColumn)obj).getColumnExpression().setElementAt(fc, 0);
                     }
                  }
               }
            }
         }
      }

   }

   public void toSQLServer(InsertQueryStatement q) throws ConvertException {
      this.setCommentClass((CommentClass)null);
      if (this.optionalSpecifier.toString().indexOf("INTO") != -1) {
         this.optionalSpecifier = new OptionalSpecifier();
         this.optionalSpecifier.setInto("INTO");
      } else {
         this.optionalSpecifier = null;
      }

      this.tblExp.toMSSQLServer();
      if (this.columnList != null) {
         for(int i = 0; i < this.columnList.size(); ++i) {
            if (this.columnList.get(i) instanceof String) {
               String columnName = (String)this.columnList.get(i);
               if (!columnName.trim().equals(",") && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                  columnName = CustomizeUtil.objectNamesToBracedIdentifier(columnName, SwisSQLUtils.getKeywords(2), (ModifiedObjectAttr)null);
               }

               if (columnName.startsWith("`") && columnName.endsWith("`")) {
                  columnName = columnName.substring(1, columnName.length() - 1);
                  if (columnName.indexOf(32) != -1) {
                     columnName = "\"" + columnName + "\"";
                  }
               }

               this.columnList.set(i, columnName);
            }
         }
      }

   }

   public void toSybase(InsertQueryStatement q) throws ConvertException {
      this.setCommentClass((CommentClass)null);
      if (this.optionalSpecifier.toString().indexOf("INTO") != -1) {
         this.optionalSpecifier = new OptionalSpecifier();
         this.optionalSpecifier.setInto("INTO");
      } else {
         this.optionalSpecifier = null;
      }

      this.tblExp.toSybase();
      if (this.columnList != null) {
         for(int i = 0; i < this.columnList.size(); ++i) {
            if (this.columnList.get(i) instanceof String) {
               String columnName = (String)this.columnList.get(i);
               if (columnName.startsWith("`") && columnName.endsWith("`")) {
                  columnName = columnName.substring(1, columnName.length() - 1);
                  if (columnName.indexOf(32) != -1) {
                     columnName = "\"" + columnName + "\"";
                  }
               }

               this.columnList.set(i, columnName);
            }
         }
      }

   }

   public void toDB2(InsertQueryStatement q) throws ConvertException {
      this.setCommentClass((CommentClass)null);
      this.optionalSpecifier = new OptionalSpecifier();
      this.optionalSpecifier.setInto("INTO");
      this.tblExp.setTableNameforAliasNameInDB2Insert(true);
      this.tblExp.toDB2();
      if (this.columnList != null) {
         for(int i = 0; i < this.columnList.size(); ++i) {
            if (this.columnList.get(i) instanceof String) {
               String columnName = (String)this.columnList.get(i);
               if (columnName.startsWith("[") && columnName.endsWith("]") || columnName.startsWith("`") && columnName.endsWith("`")) {
                  columnName = columnName.substring(1, columnName.length() - 1);
               }

               if (columnName.indexOf(32) != -1 && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                  columnName = "\"" + columnName + "\"";
               }

               this.columnList.set(i, columnName);
            }
         }
      }

   }

   public void toPostgres(InsertQueryStatement q) throws ConvertException {
      this.setCommentClass((CommentClass)null);
      this.optionalSpecifier = new OptionalSpecifier();
      this.optionalSpecifier.setInto("INTO");
      this.tblExp.toPostgreSQL();
   }

   public void toBigQuery(InsertQueryStatement q) throws ConvertException {
      this.setCommentClass((CommentClass)null);
      this.optionalSpecifier = new OptionalSpecifier();
      this.optionalSpecifier.setInto("INTO");
      this.tblExp.toBigQuery();
   }

   public void toANSISQL(InsertQueryStatement q) throws ConvertException {
      this.setCommentClass((CommentClass)null);
      this.optionalSpecifier = new OptionalSpecifier();
      this.optionalSpecifier.setInto("INTO");
      this.tblExp.toANSISQL();
      if (this.columnList != null) {
         for(int i = 0; i < this.columnList.size(); ++i) {
            if (this.columnList.get(i) instanceof String) {
               String columnName = (String)this.columnList.get(i);
               if (columnName.startsWith("[") && columnName.endsWith("]") || columnName.startsWith("`") && columnName.endsWith("`")) {
                  columnName = columnName.substring(1, columnName.length() - 1);
               }

               if (columnName.indexOf(32) != -1 && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                  columnName = "\"" + columnName + "\"";
               }

               this.columnList.set(i, columnName);
            }
         }
      }

   }

   public void toTeradata(InsertQueryStatement q) throws ConvertException {
      this.setCommentClass((CommentClass)null);
      this.optionalSpecifier = new OptionalSpecifier();
      this.optionalSpecifier.setInto("INTO");
      this.tblExp.toTeradata();
      if (this.columnList != null) {
         for(int i = 0; i < this.columnList.size(); ++i) {
            if (this.columnList.get(i) instanceof String) {
               String columnName = (String)this.columnList.get(i);
               if (columnName.startsWith("[") && columnName.endsWith("]") || columnName.startsWith("`") && columnName.endsWith("`")) {
                  columnName = columnName.substring(1, columnName.length() - 1);
               }

               if (columnName.indexOf(32) != -1 && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                  columnName = "\"" + columnName + "\"";
               }

               if (!columnName.equalsIgnoreCase("(") && !columnName.equalsIgnoreCase(")") && !columnName.equalsIgnoreCase(",") && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                  columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
               }

               this.columnList.set(i, columnName);
            }
         }
      }

   }

   public void toMySQL(InsertQueryStatement q) throws ConvertException {
      this.setCommentClass((CommentClass)null);
      this.tblExp.toMySQL();
      if (this.columnList != null) {
         for(int i = 0; i < this.columnList.size(); ++i) {
            if (this.columnList.get(i) instanceof String) {
               String columnName = (String)this.columnList.get(i);
               if (columnName.startsWith("[") && columnName.endsWith("]")) {
                  columnName = columnName.substring(1, columnName.length() - 1);
                  if (columnName.indexOf(32) != -1) {
                     columnName = "\"" + columnName + "\"";
                  }
               }

               this.columnList.set(i, columnName);
            }
         }
      }

   }

   public void toSnowflake(InsertQueryStatement q) throws ConvertException {
      this.setCommentClass((CommentClass)null);
      this.optionalSpecifier = new OptionalSpecifier();
      this.optionalSpecifier.setInto("INTO");
      this.tblExp.toSnowflake();
   }

   public void toInformix(InsertQueryStatement q) throws ConvertException {
      this.setCommentClass((CommentClass)null);
      this.optionalSpecifier = new OptionalSpecifier();
      this.optionalSpecifier.setInto("INTO");
      this.tblExp.toInformix();
   }

   public void toTimesTen(InsertQueryStatement q) throws ConvertException {
      this.setCommentClass((CommentClass)null);
      this.optionalSpecifier = new OptionalSpecifier();
      this.optionalSpecifier.setInto("INTO");
      this.tblExp.toTimesTen();
      ArrayList vcList = new ArrayList();
      ValuesClause vc = q.getValuesClause();
      ArrayList tblList = this.tblExp.getTableClauseList();
      String tableName = null;
      if (tblList != null) {
         Object obj = tblList.get(0);
         if (obj instanceof TableClause) {
            tableName = ((TableClause)obj).getTableObject().getTableName();
         }
      }

      ArrayList dateTypeColumnIndex = new ArrayList();
      ArrayList dateTypeColumnNames = new ArrayList();
      ArrayList dateTimeColumnIndex = new ArrayList();
      ArrayList dateTimeColumnNames = new ArrayList();
      ArrayList unicodeColumnIndex = new ArrayList();
      ArrayList unicodeColumnNames = new ArrayList();
      String format;
      if (tableName != null) {
         ArrayList colList = (ArrayList)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getTableColumnListMetadata(), tableName);
         Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getDataTypesFromMetaDataHT(), tableName);
         if (colList != null && colDatatypeTable != null) {
            for(int i = 0; i < colList.size(); ++i) {
               String columnName = (String)colList.get(i);
               format = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable, columnName);
               if (format != null) {
                  if (format.toLowerCase().indexOf("date") == -1 && format.toLowerCase().indexOf("time") == -1) {
                     if (format.indexOf("unichar") != -1 || format.indexOf("univarchar") != -1 || format.indexOf("nchar") != -1 || format.indexOf("nvarchar") != -1) {
                        if (this.columnList != null) {
                           unicodeColumnNames.add(columnName.toLowerCase());
                        } else {
                           unicodeColumnIndex.add(new Integer(i + 1));
                        }
                     }
                  } else {
                     if (this.columnList != null) {
                        dateTypeColumnNames.add(columnName.toLowerCase());
                     } else {
                        dateTypeColumnIndex.add(new Integer(i + 1));
                     }

                     if (format.toLowerCase().indexOf("datetime") != -1) {
                        if (this.columnList != null) {
                           dateTimeColumnNames.add(columnName.toLowerCase());
                        } else {
                           dateTimeColumnIndex.add(new Integer(i + 1));
                        }
                     }
                  }
               }
            }
         }
      }

      int valueCount;
      int j;
      String value;
      if (this.columnList != null) {
         valueCount = this.columnList.size();

         for(j = 0; j < this.columnList.size(); ++j) {
            if (this.columnList.get(j) instanceof String) {
               value = (String)this.columnList.get(j);
               if (value.startsWith("[") && value.endsWith("]") || value.startsWith("`") && value.endsWith("`")) {
                  value = value.substring(1, value.length() - 1);
                  this.columnList.set(j, value);
               }

               if (!value.startsWith("\"") && value.indexOf(32) != -1) {
                  value = "\"" + value + "\"";
                  this.columnList.set(j, value);
               }
            }
         }
      }

      if (vc != null) {
         vcList = vc.getValuesList();
      }

      if (vcList != null) {
         valueCount = 0;

         for(j = 0; j < vcList.size(); ++j) {
            if (vcList.get(j).toString().trim().toLowerCase().equals("default")) {
               throw new ConvertException("\n DEFAULT values are not supported in TimesTen 5.1.21\n");
            }

            value = vcList.get(j).toString().trim();
            if (!value.equals(",") && !value.equals("(") && !value.equals(")")) {
               ++valueCount;
            }

            Object obj;
            if (!value.startsWith("'") || !dateTypeColumnIndex.contains(new Integer(valueCount)) && (this.columnList == null || !dateTypeColumnNames.contains(this.columnList.get(j).toString().trim().toLowerCase()))) {
               if (value.startsWith("'") && (unicodeColumnIndex.contains(new Integer(valueCount)) || this.columnList != null && unicodeColumnNames.contains(this.columnList.get(j).toString().trim().toLowerCase()))) {
                  obj = vcList.get(j);
                  if (obj instanceof SelectColumn) {
                     Vector colExpr = ((SelectColumn)obj).getColumnExpression();
                     if (colExpr.size() == 1) {
                        colExpr.setElementAt("N" + value, 0);
                     }
                  }
               }
            } else {
               obj = vcList.get(j);
               if (obj instanceof SelectColumn) {
                  format = SwisSQLUtils.getDateFormat(value, 10);
                  if (format != null && (format.equals("YYYY-MM-DD") || format.equals("HH24:MI:SS"))) {
                     if (dateTimeColumnIndex.contains(new Integer(valueCount)) || this.columnList != null && dateTypeColumnNames.contains(this.columnList.get(j).toString().trim().toLowerCase())) {
                        if (format.equals("YYYY-MM-DD")) {
                           value = value.substring(0, value.length() - 1) + " 00:00:00'";
                        } else {
                           value = "'1900-01-01 " + value.substring(1);
                        }

                        ((SelectColumn)obj).getColumnExpression().setElementAt(value, 0);
                     }

                     format = null;
                  }

                  if (format != null) {
                     if (format.startsWith("'1900")) {
                        ((SelectColumn)obj).getColumnExpression().setElementAt(format, 0);
                     } else if (format.equals(value)) {
                        value = value.substring(1, value.length() - 1);
                        String time = "";
                        int index = false;
                        int index;
                        if ((index = value.indexOf(" ")) != -1) {
                           time = value.substring(index + 1);
                           value = value.substring(0, index);
                        }

                        int len = value.length();
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

                        if ((dateTimeColumnIndex.contains(new Integer(valueCount)) || this.columnList != null && dateTypeColumnNames.contains(this.columnList.get(j).toString().trim().toLowerCase())) && time == "") {
                           value = value + " 00:00:00";
                        } else if (time != "") {
                           value = value + " " + time;
                        }

                        ((SelectColumn)obj).getColumnExpression().setElementAt("'" + value + "'", 0);
                     } else {
                        FunctionCalls fc = new FunctionCalls();
                        TableColumn tc = new TableColumn();
                        tc.setColumnName("TO_DATE");
                        Vector fnArgs = new Vector();
                        fnArgs.add(value);
                        fnArgs.add(format);
                        fc.setFunctionName(tc);
                        fc.setFunctionArguments(fnArgs);
                        ((SelectColumn)obj).getColumnExpression().setElementAt(fc, 0);
                     }
                  }
               }
            }
         }
      }

   }

   public void toNetezza(InsertQueryStatement q) throws ConvertException {
      this.setCommentClass((CommentClass)null);
      this.optionalSpecifier = new OptionalSpecifier();
      this.optionalSpecifier.setInto("INTO");
      SelectQueryStatement tempSubQuery = this.tblExp.getSubQuery();
      if (this.columnList == null && tempSubQuery != null) {
         ArrayList newColumnList = new ArrayList();
         SelectQueryStatement tblExpSubQuery = tempSubQuery;
         Vector tblExpSubQuerySelectItems = tempSubQuery.getSelectStatement().getSelectItemList();
         newColumnList.add("(");

         for(int i = 0; i < tblExpSubQuerySelectItems.size(); ++i) {
            if (tblExpSubQuerySelectItems.elementAt(i) instanceof SelectColumn) {
               newColumnList.add(((SelectColumn)tblExpSubQuerySelectItems.elementAt(i)).toNetezzaSelect(tblExpSubQuery, tblExpSubQuery));
            } else {
               newColumnList.add((String)tblExpSubQuerySelectItems.elementAt(i));
            }
         }

         newColumnList.add(")");
         this.setColumnList(newColumnList);
      }

      this.tblExp.toNetezza();
      if (this.columnList != null) {
         for(int i = 0; i < this.columnList.size(); ++i) {
            if (this.columnList.get(i) instanceof String) {
               String columnName = (String)this.columnList.get(i);
               if (columnName.startsWith("[") && columnName.endsWith("]") || columnName.startsWith("`") && columnName.endsWith("`")) {
                  columnName = columnName.substring(1, columnName.length() - 1);
               }

               if (columnName.indexOf(32) != -1 && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                  columnName = "\"" + columnName + "\"";
               }

               if (!columnName.equalsIgnoreCase("(") && !columnName.equalsIgnoreCase(")") && !columnName.equalsIgnoreCase(",") && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                  columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11);
               }

               this.columnList.set(i, columnName);
            }
         }
      }

      if (this.columnList == null && tempSubQuery != null && this.tblExp.getSubQuery() == null) {
         SelectQueryStatement tblExpSubQuery = tempSubQuery;
         Vector tblExpSubQuerySelectItems = tempSubQuery.getSelectStatement().getSelectItemList();

         for(int i = 0; i < tblExpSubQuerySelectItems.size(); ++i) {
            if (tblExpSubQuerySelectItems.elementAt(i) instanceof SelectColumn) {
               this.columnList.add(((SelectColumn)tblExpSubQuerySelectItems.elementAt(i)).toNetezzaSelect(tblExpSubQuery, tblExpSubQuery));
            } else {
               this.columnList.add((String)tblExpSubQuerySelectItems.elementAt(i));
            }
         }
      }

   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append(this.insert.toUpperCase());
      sb.append(" ");
      if (this.commentObj != null) {
         sb.append(this.commentObj.toString().trim() + " ");
      }

      if (this.optionalSpecifier != null) {
         sb.append(this.optionalSpecifier.toString());
         sb.append(" ");
      }

      if (this.tblExp != null) {
         if (this.context != null) {
            this.tblExp.setObjectContext(this.context);
         }

         sb.append(this.tblExp.toString());
         sb.append(" ");
      }

      if (this.columnList != null) {
         int size = this.columnList.size();
         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 2);

         for(int i = 0; i < size; ++i) {
            String isCommaOrOpenBrace = "";
            if (this.columnList.get(i) instanceof String) {
               isCommaOrOpenBrace = (String)this.columnList.get(i);
            }

            int j;
            if (isCommaOrOpenBrace.trim().equals("(")) {
               sb.append("\n");

               for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
                  sb.append("\t");
               }
            }

            if (this.context != null) {
               String temp = this.context.getEquivalent(this.columnList.get(i)).toString();
               sb.append(temp + " ");
            } else {
               sb.append(this.columnList.get(i) + " ");
            }

            if (isCommaOrOpenBrace.equals(",")) {
               sb.append("\n");

               for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
                  sb.append("\t");
               }
            }
         }

         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 2);
      }

      return sb.toString();
   }
}
