package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class CreateColumn {
   private String columnName;
   private String nullStatus;
   private String onDefault;
   private String defaultValue;
   private String defaultOpenBrace;
   private String defaultClosedBrace;
   private FunctionCalls functionCall;
   private String identity;
   Datatype datatype;
   private String userDefinedDatatype;
   private Vector constraintVector;
   private Vector constraintNullVector;
   private NotNull notNull;
   private String tableNameFromCQS;
   private String startString;
   private String withString;
   private String increment;
   private String byString;
   private String generated;
   private String always;
   private String byForAlways;
   private String defaultForIdentity;
   private String asForIdentity;
   private boolean notNullSetFromCreateQueryStatement = false;
   private String collate;
   private String collationName;
   private String autoIncrement;
   private boolean booleanOracle = false;
   private boolean booleanDb2 = false;
   private UserObjectContext context = null;
   private DatatypeMapping datatypeMapping;
   private boolean isAlterStatement = false;
   private String datapageStorageLevel;
   private String createSequenceStr;
   private ArrayList defaultExpList;
   private String onUpdateClause = null;
   Datatype castDatatype;
   private SelectColumn computedColumnExpression;
   private String computedColumnAS;
   private String sparseStr;
   private static Hashtable userdefinedDatatypes = new Hashtable();
   private static Hashtable userConfiguredDatatypes = new Hashtable();

   public void setColumnName(String columnName) {
      this.columnName = columnName;
   }

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setDatatype(Datatype datatype) {
      this.datatype = datatype;
   }

   public void setUserDefinedDatatype(String userDefinedDatatype) {
      this.userDefinedDatatype = userDefinedDatatype;
   }

   public void setDefault(String onDefault) {
      this.onDefault = onDefault;
   }

   public void setDefaultOpenBrace(String defaultOpenBrace) {
      this.defaultOpenBrace = defaultOpenBrace;
   }

   public void setDefaultClosedBrace(String defaultClosedBrace) {
      this.defaultClosedBrace = defaultClosedBrace;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }

   public void setDefaultFunction(FunctionCalls functionCall) {
      this.functionCall = functionCall;
   }

   public void setDefaultExpression(ArrayList defaultExpList) {
      this.defaultExpList = defaultExpList;
   }

   public void setIdentity(String identity) {
      this.identity = identity;
   }

   public void setCastDatatype(Datatype castDatatype) {
      this.castDatatype = castDatatype;
   }

   public void setNotNull(NotNull notNull) {
      this.notNull = notNull;
      this.identity = notNull.getIdentity();
      this.nullStatus = notNull.getNullStatus();
      this.startString = notNull.getStart();
      this.withString = notNull.getWith();
      this.increment = notNull.getIncrement();
      this.byString = notNull.getBy();
   }

   public void setTableNameFromCreateQueryStmt(String tableNameFromCQS) {
      this.tableNameFromCQS = tableNameFromCQS;
   }

   public void setNullStatus(String nullStatus) {
      this.nullStatus = nullStatus;
   }

   public void setNotNullSetFromCreateQueryStatement(boolean setNotNullValue) {
      this.notNullSetFromCreateQueryStatement = setNotNullValue;
   }

   public void setConstraintClause(Vector constraintVector) {
      this.constraintVector = constraintVector;
   }

   public void setCollate(String collate) {
      this.collate = collate;
   }

   public void setCollationName(String collationName) {
      this.collationName = collationName;
   }

   public void setGenerated(String generated) {
      this.generated = generated;
   }

   public void setAlways(String always) {
      this.always = always;
   }

   public void setOnUpdateClause(String val) {
      this.onUpdateClause = val;
   }

   public void setSparseString(String sparse) {
      this.sparseStr = sparse;
   }

   public String getOnUpdateClause() {
      return this.onUpdateClause;
   }

   public String getGenerated() {
      return this.generated;
   }

   public void setByForAlways(String byForAlways) {
      this.byForAlways = byForAlways;
   }

   public void setDefaultForIdentity(String defaultForIdentity) {
      this.defaultForIdentity = defaultForIdentity;
   }

   public void setIdentityAs(String asForIdentity) {
      this.asForIdentity = asForIdentity;
   }

   public void setAutoIncrement(String autoIncrement) {
      this.autoIncrement = autoIncrement;
   }

   public void setDatatypeMapping(DatatypeMapping mapping) {
      this.datatypeMapping = mapping;
   }

   public void setAlterStatement(boolean isAlterStatement) {
      this.isAlterStatement = isAlterStatement;
   }

   public void setDatapageStorageLevel(String datapageStorageLevel) {
      this.datapageStorageLevel = datapageStorageLevel;
   }

   public void setCreateSequenceString(String createSequenceStr) {
      this.createSequenceStr = createSequenceStr;
   }

   public void setComputedColumnExpression(SelectColumn computedColExpr) {
      this.computedColumnExpression = computedColExpr;
   }

   public void setComputedColumnAS(String as) {
      this.computedColumnAS = as;
   }

   public String getColumnName() {
      return this.columnName;
   }

   public Datatype getDatatype() {
      return this.datatype;
   }

   public String getUserDefinedDatatype() {
      return this.userDefinedDatatype;
   }

   public String getDefault() {
      return this.onDefault;
   }

   public String getDefaultValue() {
      return this.defaultValue;
   }

   public String getAutoIncrement() {
      return this.autoIncrement;
   }

   public FunctionCalls getDefaultFunction() {
      return this.functionCall;
   }

   public ArrayList getDefaultExpression() {
      return this.defaultExpList;
   }

   public String getIdentity() {
      return this.identity;
   }

   public Vector getConstraintClause() {
      return this.constraintVector;
   }

   public String getNullStatus() {
      return this.nullStatus;
   }

   public String getDatapageStorageLevel() {
      return this.datapageStorageLevel;
   }

   public String getCreateSequenceString() {
      return this.createSequenceStr;
   }

   public Datatype getCastDatatype() {
      return this.castDatatype;
   }

   public SelectColumn getComputedColumnExpression() {
      return this.computedColumnExpression;
   }

   public String getComputedColumnAS() {
      return this.computedColumnAS;
   }

   public String getSparseString() {
      return this.sparseStr;
   }

   public void toDB2String() throws ConvertException {
      this.setDatapageStorageLevel((String)null);
      if (this.columnName != null && (this.columnName.startsWith("[") && this.columnName.endsWith("]") || this.columnName.startsWith("`") && this.columnName.endsWith("`"))) {
         this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
         if (this.columnName.indexOf(32) != -1) {
            this.columnName = "\"" + this.columnName + "\"";
         }
      }

      this.constraintNullVector = new Vector();
      StringBuffer temp_SB;
      if (this.identity != null) {
         temp_SB = new StringBuffer();
         if (this.identity.trim().equalsIgnoreCase("IDENTITY")) {
            if (this.always == null) {
               temp_SB.append("GENERATED");
               temp_SB.append(" BY");
               temp_SB.append(" DEFAULT");
               temp_SB.append(" AS ");
            }

            temp_SB.append("IDENTITY(START");
            temp_SB.append(" WITH");
            temp_SB.append(" 1");
            temp_SB.append(" INCREMENT BY");
            temp_SB.append(" 1)");
            this.identity = temp_SB.toString();
         } else {
            if (this.always == null) {
               temp_SB.append("GENERATED");
               temp_SB.append(" BY");
               temp_SB.append(" DEFAULT");
               temp_SB.append(" AS ");
            }

            String tempIdentity = this.identity.trim().substring(8).trim();
            tempIdentity = tempIdentity.substring(1, tempIdentity.length() - 1);
            StringTokenizer st = new StringTokenizer(tempIdentity, ",");
            String token1 = st.nextToken();
            temp_SB.append("IDENTITY(START");
            temp_SB.append(" WITH");
            temp_SB.append(" " + token1);
            if (st.countTokens() > 0) {
               String token2 = st.nextToken();
               temp_SB.append(" INCREMENT BY");
               temp_SB.append(" " + token2 + ")");
            } else {
               temp_SB.append(" INCREMENT BY");
               temp_SB.append(" 1)");
            }

            this.identity = temp_SB.toString();
         }
      }

      this.setCollate((String)null);
      this.setCollationName((String)null);
      int i;
      if (this.getConstraintClause() != null) {
         Vector changeConstraintVector = this.getConstraintClause();
         if (changeConstraintVector != null) {
            for(i = 0; i < changeConstraintVector.size(); ++i) {
               ConstraintClause toDB2ConstraintClause = (ConstraintClause)changeConstraintVector.get(i);
               if (toDB2ConstraintClause != null) {
                  if (toDB2ConstraintClause.getConstraintType() != null) {
                     ConstraintType toDB2ConstraintType = toDB2ConstraintClause.getConstraintType();
                     if (toDB2ConstraintType instanceof PrimaryOrUniqueConstraintClause && (this.getColumnName() != null || toDB2ConstraintClause.getConstraintName() != null) && !this.notNullSetFromCreateQueryStatement) {
                        this.setNullStatus("NOT NULL");
                     }
                  }

                  toDB2ConstraintClause.setColumnName(this.getColumnName());
                  toDB2ConstraintClause.toDB2String();
                  this.constraintNullVector.add(toDB2ConstraintClause);
               }
            }
         }
      }

      if (this.getNullStatus() != null && !this.getNullStatus().trim().equalsIgnoreCase("NULL") && !this.isAlterStatement) {
         this.constraintNullVector.add(this.getNullStatus());
      }

      Datatype toDB2Datatype;
      BinClass binClass;
      if (this.getDatatype() != null) {
         if (!this.mapDatatype(this, this.datatypeMapping)) {
            toDB2Datatype = this.getDatatype();
            if (toDB2Datatype instanceof DateClass) {
               DateClass dateClass = (DateClass)toDB2Datatype;
               if (dateClass.getDatatypeName().equalsIgnoreCase("DATE")) {
                  dateClass.setDatatypeName("TIMESTAMP");
               }

               dateClass.toDB2String();
            } else if (toDB2Datatype instanceof BinClass) {
               binClass = (BinClass)toDB2Datatype;
               if (binClass.getDatatypeName().trim().equalsIgnoreCase("BOOLEAN")) {
                  this.booleanDb2 = true;
               } else {
                  binClass.toDB2String();
               }
            } else {
               toDB2Datatype.toDB2String();
               if (toDB2Datatype instanceof CharacterClass) {
                  this.enumValuesConvertedToCheckConstraints(toDB2Datatype, this.constraintNullVector);
               }
            }
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() > 0) {
         if (!this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
            if (userdefinedDatatypes.containsKey(this.userDefinedDatatype.toLowerCase().trim())) {
               this.setUserDefinedDatatype((String)userdefinedDatatypes.get(this.userDefinedDatatype.toLowerCase().trim()));
            } else if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
               this.setUserDefinedDatatype("VARGRAPHIC(36)");
            } else if (this.userDefinedDatatype.equalsIgnoreCase("ROWID")) {
               this.setUserDefinedDatatype("CHAR(16) FOR BIT DATA");
            } else if (this.userDefinedDatatype.equalsIgnoreCase("SQL_VARIANT")) {
               this.setUserDefinedDatatype("VARCHAR(800)");
            }
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
         if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
            this.setUserDefinedDatatype("VARGRAPHIC(36)");
         } else if (this.userDefinedDatatype.equalsIgnoreCase("ROWID")) {
            this.setUserDefinedDatatype("CHAR(16) FOR BIT DATA");
         } else if (this.userDefinedDatatype.equalsIgnoreCase("SQL_VARIANT")) {
            this.setUserDefinedDatatype("VARCHAR(800)");
         }
      }

      if (this.getComputedColumnExpression() != null) {
         this.setComputedColumnExpression(this.getComputedColumnExpression().toDB2Select((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (this.getDefaultValue() != null) {
         this.setDefaultOpenBrace((String)null);
         this.setDefaultClosedBrace((String)null);
         if (this.getDatatype() != null) {
            toDB2Datatype = this.getDatatype();
            if (toDB2Datatype instanceof NumericClass && this.getDefaultValue().startsWith("'") && this.getDefaultValue().endsWith("'")) {
               this.setDefaultValue(this.defaultValue.substring(1, this.defaultValue.length() - 1));
            } else if (toDB2Datatype instanceof CharacterClass && this.getDefaultValue().startsWith("\"") && this.getDefaultValue().endsWith("\"")) {
               this.setDefaultValue("'" + this.defaultValue.substring(1, this.defaultValue.length() - 1) + "'");
            } else if (this.getDatatype() instanceof DateClass) {
               if (!this.defaultValue.equals("'0000-00-00 00:00:00'") && !this.defaultValue.equals("0000-00-00 00:00:00")) {
                  if (!this.defaultValue.equals("'0000-00-00'") && !this.defaultValue.equals("0000-00-00")) {
                     if (this.defaultValue.trim().equalsIgnoreCase("SYSDATE")) {
                        this.setDefaultValue("CURRENT TIMESTAMP");
                     }
                  } else {
                     this.setDefaultValue("'0001-01-01'");
                  }
               } else {
                  this.setDefaultValue("'0001-01-01 00:00:00'");
               }
            } else if (toDB2Datatype instanceof BinClass) {
               binClass = (BinClass)toDB2Datatype;
               if (binClass.getDatatypeName().trim().equalsIgnoreCase("BLOB")) {
                  this.setDefaultValue("BLOB(" + this.defaultValue + ")");
               }
            }
         } else if (this.defaultValue.trim().equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT_DATE")) {
            this.setDefaultValue("CURRENT DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT_TIME")) {
            this.setDefaultValue("CURRENT TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
            this.setDefaultValue("CURRENT TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT DATE");
         } else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
            this.setDefaultValue("USER");
         }
      } else if (this.getDefaultFunction() != null) {
         if (this.getDefaultFunction().getFunctionName() != null) {
            String functionColName = this.getDefaultFunction().getFunctionName().getColumnName();
            if (functionColName.equalsIgnoreCase("GETDATE")) {
               this.setDefaultClosedBrace((String)null);
               this.setDefaultOpenBrace((String)null);
               this.functionCall.setOpenBracesForFunctionNameRequired(false);
            } else if (functionColName.equalsIgnoreCase("EMPTY_CLOB")) {
               this.setDefaultFunction((FunctionCalls)null);
            }
         }

         if (this.functionCall != null) {
            this.setDefaultFunction(this.functionCall.toDB2Select((SelectQueryStatement)null, (SelectQueryStatement)null));
         }
      } else if (this.getDefaultExpression() != null) {
         ArrayList newExpList = new ArrayList();

         for(i = 0; i < this.defaultExpList.size(); ++i) {
            if (!(this.defaultExpList.get(i) instanceof SelectColumn)) {
               newExpList.add(this.defaultExpList.get(i));
            } else {
               SelectColumn sc = (SelectColumn)this.defaultExpList.get(i);
               SelectColumn db2SC = sc.toDB2Select((SelectQueryStatement)null, (SelectQueryStatement)null);
               Vector colExpr = db2SC.getColumnExpression();
               if (colExpr != null) {
                  for(int n = 0; n < colExpr.size(); ++n) {
                     Object obj = colExpr.get(n);
                     if (obj instanceof FunctionCalls) {
                        ((FunctionCalls)obj).setOpenBracesForFunctionNameRequired(false);
                     }

                     if (obj instanceof SelectColumn) {
                        SelectColumn scTemp = (SelectColumn)obj;
                        Vector vtemp = scTemp.getColumnExpression();
                        if (scTemp.getOpenBrace() != null && scTemp.getCloseBrace() != null && vtemp.size() == 1) {
                           scTemp.setOpenBrace((String)null);
                           scTemp.setCloseBrace((String)null);
                        }
                     }
                  }
               }

               newExpList.add(db2SC);
            }
         }

         this.setDefaultExpression(newExpList);
         this.setDefaultOpenBrace((String)null);
         this.setDefaultClosedBrace((String)null);
      }

      if (this.autoIncrement != null) {
         temp_SB = new StringBuffer();
         temp_SB.append("GENERATED");
         temp_SB.append(" BY");
         temp_SB.append(" DEFAULT");
         temp_SB.append(" AS ");
         temp_SB.append("IDENTITY(START");
         temp_SB.append(" WITH");
         temp_SB.append(" 1");
         temp_SB.append(" INCREMENT BY");
         temp_SB.append(" 1)");
         this.identity = temp_SB.toString();
         this.autoIncrement = null;
      }

      if (this.getCastDatatype() != null) {
      }

   }

   public void toMSSQLServerString() throws ConvertException {
      this.setDatapageStorageLevel((String)null);
      this.constraintNullVector = new Vector();
      if (this.columnName != null && this.columnName.equalsIgnoreCase("FUNCTION")) {
         this.setColumnName(this.columnName + "_COLUMN");
      }

      this.generated = null;
      this.always = null;
      this.asForIdentity = null;
      this.byForAlways = null;
      this.defaultForIdentity = null;
      this.asForIdentity = null;
      if (this.nullStatus == null && SwisSQLOptions.sybaseNotNullConstraint && SwisSQLOptions.fromSybase && (this.getDatatype() != null || this.getUserDefinedDatatype() != null)) {
         this.nullStatus = "NOT NULL";
      }

      if (this.columnName != null) {
         if (this.columnName.startsWith("`") && this.columnName.endsWith("`")) {
            this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
            if (this.columnName.indexOf(32) != -1) {
               this.columnName = "\"" + this.columnName + "\"";
            }
         }

         String[] keywords = null;
         if (SwisSQLUtils.getKeywords(2) != null) {
            keywords = (String[])SwisSQLUtils.getKeywords(2);
            if (this.columnName.trim().length() > 0) {
               this.columnName = CustomizeUtil.objectNamesToBracedIdentifier(this.columnName, keywords, (ModifiedObjectAttr)null);
            }
         }
      }

      int i;
      if (this.getConstraintClause() != null) {
         Vector changeConstraintVector = this.getConstraintClause();
         if (changeConstraintVector != null) {
            for(i = 0; i < changeConstraintVector.size(); ++i) {
               ConstraintClause toSQLServerConstraintClause = (ConstraintClause)changeConstraintVector.get(i);
               if (toSQLServerConstraintClause != null) {
                  if (toSQLServerConstraintClause.getConstraintType() instanceof PrimaryOrUniqueConstraintClause) {
                     toSQLServerConstraintClause.setColumnName(this.getColumnName());
                     PrimaryOrUniqueConstraintClause primaryConstraintClause = (PrimaryOrUniqueConstraintClause)toSQLServerConstraintClause.getConstraintType();
                     boolean ccToBeAdded = false;
                     if (primaryConstraintClause.getConstraintColumnNames() != null && this.columnName != null) {
                        this.constraintNullVector.add(",\n\t");
                        CreateColumn cc = new CreateColumn();
                        if (this.getConstraintClause() != null) {
                           Vector primaryConstraintVector = new Vector();

                           for(int index = 0; index < this.getConstraintClause().size(); ++index) {
                              if (this.getConstraintClause().get(index) instanceof PrimaryOrUniqueConstraintClause) {
                                 primaryConstraintVector.add(this.getConstraintClause().get(index));
                                 cc.setNullStatus((String)null);
                                 cc.setDefaultValue((String)null);
                                 cc.setDefault((String)null);
                                 cc.toOracleString();
                                 NotNull notnullObj = new NotNull();
                                 notnullObj.setNullStatus((String)null);
                                 notnullObj.setIdentity((String)null);
                                 cc.setNotNull(notnullObj);
                                 ccToBeAdded = true;
                              }
                           }

                           cc.setConstraintClause(primaryConstraintVector);
                        }

                        if (ccToBeAdded) {
                           this.constraintNullVector.add(cc);
                        }
                     }

                     if (primaryConstraintClause.getConstraintColumnNames() != null && !this.notNullSetFromCreateQueryStatement) {
                        this.setNullStatus("");
                     }
                  }

                  if (toSQLServerConstraintClause.getAutoIncrement() != null) {
                     this.setIdentity("IDENTITY (1,1)");
                     toSQLServerConstraintClause.setAutoIncrement((String)null);
                  }

                  toSQLServerConstraintClause.setColumnName(this.getColumnName());
                  toSQLServerConstraintClause.toMSSQLServerString();
                  this.constraintNullVector.add(toSQLServerConstraintClause);
               }
            }
         }
      }

      if (this.getComputedColumnExpression() != null) {
         this.setComputedColumnAS("AS");
         this.setComputedColumnExpression(this.getComputedColumnExpression().toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (this.getNullStatus() != null) {
         this.constraintNullVector.add(this.getNullStatus());
      }

      Datatype type;
      if (this.getDatatype() != null && !this.mapDatatype(this, this.datatypeMapping)) {
         type = this.getDatatype();
         type.toMSSQLServerString();
         if (type instanceof CharacterClass) {
            this.enumValuesConvertedToCheckConstraints(type, this.constraintNullVector);
         }
      }

      SelectColumn sc;
      if (this.getDefaultExpression() != null) {
         new ArrayList();
         i = this.defaultExpList.size();
         if (i == 1) {
            if (this.defaultExpList.get(0) instanceof SelectColumn) {
               sc = (SelectColumn)this.defaultExpList.get(0);
               Vector expList = sc.getColumnExpression();
               if (expList.size() == 1) {
                  Object obj = expList.get(0);
                  if (obj instanceof String) {
                     this.setDefaultValue((String)obj);
                  } else if (obj instanceof TableColumn) {
                     TableColumn tc = (TableColumn)obj;
                     String colName = tc.getColumnName();
                     if (colName != null) {
                        this.setDefaultValue(colName);
                     }
                  }
               }
            } else {
               ArrayList expWithoutSc = this.getDefaultExpression();
               if (expWithoutSc.size() == 1 && expWithoutSc.get(0) instanceof String) {
                  this.setDefaultValue((String)expWithoutSc.get(0));
               }
            }
         }
      }

      String fnName;
      if (this.getDefaultValue() != null) {
         if (this.defaultValue.toUpperCase().startsWith("G'") && this.defaultValue.toUpperCase().endsWith("'")) {
            fnName = this.defaultValue.substring(1);
            this.setDefaultValue(fnName);
         }

         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("GETDATE()");
         }

         if (this.defaultValue.equalsIgnoreCase("SYS_GUID")) {
            this.setDefaultValue("NEWID()");
         } else if (!this.defaultValue.equalsIgnoreCase("CURRENT DATE") && !this.defaultValue.equalsIgnoreCase("CURRENT_DATE")) {
            if (!this.defaultValue.equalsIgnoreCase("CURRENT TIME") && !this.defaultValue.equalsIgnoreCase("CURRENT_TIME")) {
               if (!this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP") && !this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                  if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                     this.setDefaultValue("GETDATE()");
                  } else if (!this.defaultValue.equalsIgnoreCase("USER") && !this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                     if (!this.defaultValue.toUpperCase().startsWith("EMPTY_BLOB") && !this.defaultValue.toUpperCase().startsWith("EMPTY_CLOB")) {
                        if (!this.defaultValue.equals("'0000-00-00 00:00:00'") && !this.defaultValue.equals("0000-00-00 00:00:00")) {
                           if (this.defaultValue.equals("\"\"")) {
                              this.setDefaultValue("' '");
                           } else if (this.defaultValue.startsWith("\"") && this.defaultValue.endsWith("\"")) {
                              this.setDefaultValue("'" + this.defaultValue.substring(1, this.defaultValue.length() - 1) + "'");
                           } else if (this.defaultValue.equalsIgnoreCase("true")) {
                              this.setDefaultValue("1");
                           } else if (this.defaultValue.equalsIgnoreCase("false")) {
                              this.setDefaultValue("0");
                           } else if (this.getDefault() != null && this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
                              this.setDefault("DEFAULT ");
                           }
                        } else {
                           this.setDefaultValue("'1753-01-01 00:00:00'");
                        }
                     } else {
                        this.setDefaultValue("''");
                     }
                  } else {
                     this.setDefaultValue("SYSTEM_USER");
                  }
               } else {
                  this.setDefaultValue("GETDATE()");
               }
            } else {
               this.setDefaultValue("GETDATE()");
            }
         } else {
            this.setDefaultValue("GETDATE()");
         }

         if (this.defaultValue.startsWith("'")) {
            type = this.getDatatype();
            if (type != null && type instanceof BinClass) {
               String typeName = type.getDatatypeName();
               if (typeName != null && typeName.equalsIgnoreCase("varbinary")) {
                  this.setDefaultValue("CONVERT(VARBINARY, " + this.defaultValue + ")");
               }
            }
         }
      } else if (this.getDefaultFunction() != null) {
         fnName = this.functionCall.getFunctionName().getColumnName();
         if (fnName == null || !fnName.toUpperCase().startsWith("EMPTY_BLOB") && !fnName.toUpperCase().startsWith("EMPTY_CLOB")) {
            this.setDefaultFunction(this.functionCall.toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         } else {
            this.functionCall.setFunctionName((TableColumn)null);
            Vector fnArgs = new Vector();
            fnArgs.add("''");
            this.functionCall.setFunctionArguments(fnArgs);
            this.functionCall.setOpenBracesForFunctionNameRequired(false);
         }
      } else if (this.getDefaultExpression() != null) {
         ArrayList newExpList = new ArrayList();

         for(i = 0; i < this.defaultExpList.size(); ++i) {
            if (this.defaultExpList.get(i) instanceof SelectColumn) {
               sc = (SelectColumn)this.defaultExpList.get(i);
               newExpList.add(sc.toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            } else {
               newExpList.add(this.defaultExpList.get(i));
            }
         }

         this.setDefaultExpression(newExpList);
      } else if (this.getDefault() != null) {
         if (this.getDatatype() != null) {
            if (this.getDatatype() instanceof NumericClass) {
               this.setDefaultValue("0");
            } else if (this.getDatatype() instanceof BinClass) {
               this.setDefaultValue("''");
            } else if (this.getDatatype() instanceof CharacterClass) {
               this.setDefaultValue("' '");
            } else if (this.getDatatype() instanceof DateClass) {
               this.setDefaultValue("'0001-01-01 00:00:00'");
            } else {
               this.setDefaultValue("NULL");
            }
         } else {
            this.setDefaultValue("NULL");
         }

         if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
            this.setDefault("DEFAULT ");
         }
      }

      if (this.userDefinedDatatype != null && userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype) && this.userDefinedDatatype.equalsIgnoreCase("ROWID")) {
         this.setUserDefinedDatatype("UNIQUEIDENTIFIER");
      }

      if (this.autoIncrement != null) {
         this.identity = "IDENTITY(1,1)";
         this.autoIncrement = null;
      }

      if (this.getCastDatatype() != null) {
         this.getCastDatatype().toMSSQLServerString();
         this.createCastFunction();
      }

   }

   public void toSybaseString() throws ConvertException {
      this.constraintNullVector = new Vector();
      this.generated = null;
      this.always = null;
      this.byForAlways = null;
      this.defaultForIdentity = null;
      if (this.columnName != null && this.columnName.startsWith("`") && this.columnName.endsWith("`")) {
         this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
         if (this.columnName.indexOf(32) != -1) {
            this.columnName = "\"" + this.columnName + "\"";
         }
      }

      boolean nullStatusExists = false;
      int i;
      if (this.getConstraintClause() != null) {
         Vector changeConstraintVector = this.getConstraintClause();
         if (changeConstraintVector != null) {
            for(i = 0; i < changeConstraintVector.size(); ++i) {
               ConstraintClause toSybaseConstraintClause = (ConstraintClause)changeConstraintVector.get(i);
               if (toSybaseConstraintClause != null) {
                  ConstraintType toSybaseConstraintType = toSybaseConstraintClause.getConstraintType();
                  toSybaseConstraintClause.setColumnName(this.getColumnName());
                  toSybaseConstraintClause.toSybaseString();
                  if (toSybaseConstraintType instanceof DefaultConstraintClause) {
                     this.constraintNullVector.insertElementAt(toSybaseConstraintClause, 0);
                  } else {
                     if (toSybaseConstraintClause.getAutoIncrement() != null) {
                        this.setIdentity("IDENTITY");
                        toSybaseConstraintClause.setAutoIncrement((String)null);
                     }

                     toSybaseConstraintClause.setColumnName(this.getColumnName());
                     if (this.getIdentity() != null) {
                        this.setIdentity("IDENTITY");
                        if (this.getDatatype() != null) {
                           Datatype datatype = this.getDatatype();
                           NumericClass numericClass = (NumericClass)datatype;
                           String dataTypeName = numericClass.getDatatypeName();
                           if (dataTypeName.indexOf("NUMERIC") == -1) {
                              numericClass.setDatatypeName("NUMERIC (5,0)");
                           }
                        }
                     }

                     if (toSybaseConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                        PrimaryOrUniqueConstraintClause tempPrimaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toSybaseConstraintType;
                        if (tempPrimaryOrUniqueConstraintClause.getConstraintColumnNames() != null && this.columnName != null) {
                           this.constraintNullVector.add(",\n\t");
                           if (!this.notNullSetFromCreateQueryStatement) {
                              this.setNullStatus("");
                           }
                        }
                     }

                     if (toSybaseConstraintType instanceof ForeignConstraintClause) {
                        ForeignConstraintClause tempForeignConstraintClause = (ForeignConstraintClause)toSybaseConstraintType;
                        if (tempForeignConstraintClause.getConstraintColumnNames() != null && this.columnName != null) {
                           this.constraintNullVector.add(",\n\t");
                        } else if (tempForeignConstraintClause.getConstraintColumnNames() == null && this.columnName == null) {
                           tempForeignConstraintClause.setConstraintName((String)null);
                        }
                     }

                     toSybaseConstraintClause.toSybaseString();
                     if (toSybaseConstraintClause.getNotNull() != null && toSybaseConstraintClause.getNotNull().getNullStatus() != null && !toSybaseConstraintClause.getNotNull().getNullStatus().trim().equals("")) {
                        nullStatusExists = true;
                     }

                     this.constraintNullVector.add(toSybaseConstraintClause);
                  }
               }
            }
         }
      }

      if (this.onDefault != null && this.onDefault.trim().equalsIgnoreCase("DEFAULT") && this.getDefaultValue() != null && this.getDefaultValue().trim().equalsIgnoreCase("null") && this.notNull == null) {
         NotNull newNotNull = new NotNull();
         newNotNull.setNullStatus("NULL");
         this.setNotNull(newNotNull);
      }

      if (this.getComputedColumnExpression() != null) {
         this.setComputedColumnAS("AS");
         this.setComputedColumnExpression(this.getComputedColumnExpression().toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (this.getNullStatus() != null) {
         this.constraintNullVector.add(this.getNullStatus());
      } else if (!SwisSQLOptions.fromSybase && this.tableNameFromCQS != null && this.columnName != null && this.identity == null && !nullStatusExists) {
         this.tableNameFromCQS = this.removeDelimiter(this.tableNameFromCQS);
         String colName = this.removeDelimiter(this.columnName);
         ArrayList tempCols = (ArrayList)CastingUtil.getValueIgnoreCase(SwisSQLAPI.primaryKeyMetaData, this.tableNameFromCQS);
         if (tempCols != null) {
            if (!tempCols.contains(colName) && !tempCols.contains(colName.toLowerCase()) && !tempCols.contains(colName.toUpperCase())) {
               if (this.notNull == null) {
                  NotNull newNotNull = new NotNull();
                  newNotNull.setNullStatus("NULL");
                  this.setNotNull(newNotNull);
               } else {
                  this.notNull.setNullStatus("NULL");
               }

               this.constraintNullVector.add(this.getNullStatus());
            }
         } else if (SwisSQLAPI.getDataTypesFromMetaDataHT().size() > 0) {
            Hashtable colTypeHT = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getDataTypesFromMetaDataHT(), this.tableNameFromCQS);
            if (colTypeHT != null) {
               String type = (String)CastingUtil.getValueIgnoreCase(colTypeHT, colName);
               if (type != null) {
                  if (this.notNull == null) {
                     NotNull newNotNull = new NotNull();
                     newNotNull.setNullStatus("NULL");
                     this.setNotNull(newNotNull);
                  } else {
                     this.notNull.setNullStatus("NULL");
                  }

                  this.constraintNullVector.add(this.getNullStatus());
               }
            }
         }
      }

      if (this.getDatatype() != null) {
         if (this.identity == null && this.asForIdentity == null) {
            if (!this.mapDatatype(this, this.datatypeMapping)) {
               Datatype toSybaseDatatype = this.getDatatype();
               toSybaseDatatype.toSybaseString();
               if (toSybaseDatatype instanceof CharacterClass) {
                  this.enumValuesConvertedToCheckConstraints(toSybaseDatatype, this.constraintNullVector);
               }
            }
         } else {
            NumericClass ncl = new NumericClass();
            ncl.setDatatypeName("NUMERIC");
            this.setDatatype(ncl);
            if (this.identity != null && this.identity.indexOf("(") != -1) {
               this.identity = this.identity.substring(0, this.identity.indexOf("("));
            }
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() > 0) {
         if (!this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
            if (userdefinedDatatypes.containsKey(this.userDefinedDatatype.toLowerCase().trim())) {
               this.setUserDefinedDatatype((String)userdefinedDatatypes.get(this.userDefinedDatatype.toLowerCase().trim()));
            } else if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
               this.setUserDefinedDatatype("CHAR(36)");
            }
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype) && this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
         this.setUserDefinedDatatype("CHAR(36)");
      }

      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("GETDATE()");
         }

         if (this.defaultValue.equalsIgnoreCase("SYS_GUID")) {
            this.setDefaultValue("NEWID()");
         } else if (!this.defaultValue.equalsIgnoreCase("CURRENT DATE") && !this.defaultValue.equalsIgnoreCase("CURRENT_DATE")) {
            if (!this.defaultValue.equalsIgnoreCase("CURRENT TIME") && !this.defaultValue.equalsIgnoreCase("CURRENT_TIME")) {
               if (!this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP") && !this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                  if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                     this.setDefaultValue("GETDATE()");
                  } else if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                     if (this.defaultValue.indexOf("\"") != -1) {
                        this.defaultValue = "'" + this.defaultValue.substring(1, this.defaultValue.length() - 1) + "'";
                        this.setDefaultValue(this.defaultValue);
                     } else if (this.defaultValue.equalsIgnoreCase("true")) {
                        this.setDefaultValue("1");
                     } else if (this.defaultValue.equalsIgnoreCase("false")) {
                        this.setDefaultValue("0");
                     } else if (this.getDefault() != null && this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
                        this.setDefault("DEFAULT ");
                     }
                  } else {
                     this.setDefaultValue("USER");
                  }
               } else {
                  this.setDefaultValue("GETDATE()");
               }
            } else {
               this.setDefaultValue("GETDATE()");
            }
         } else {
            this.setDefaultValue("GETDATE()");
         }
      } else if (this.getDefaultFunction() != null) {
         this.setDefaultFunction(this.functionCall.toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      } else if (this.getDefaultExpression() != null) {
         ArrayList newExpList = new ArrayList();

         for(i = 0; i < this.defaultExpList.size(); ++i) {
            if (this.defaultExpList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.defaultExpList.get(i);
               newExpList.add(sc.toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            } else {
               newExpList.add(this.defaultExpList.get(i));
            }
         }

         this.setDefaultExpression(newExpList);
      } else if (this.getDefault() != null) {
         if (this.getDatatype() != null) {
            if (this.getDatatype() instanceof NumericClass) {
               this.setDefaultValue("0");
            } else if (this.getDatatype() instanceof BinClass) {
               this.setDefaultValue("''");
            } else if (this.getDatatype() instanceof CharacterClass) {
               this.setDefaultValue("' '");
            } else if (this.getDatatype() instanceof DateClass) {
               this.setDefaultValue("'0001-01-01 00:00:00'");
            } else if (this.getNullStatus() != null && !this.getNullStatus().trim().equalsIgnoreCase("NOT NULL")) {
               this.setDefaultValue("NULL");
            }
         } else if (this.getNullStatus() != null && !this.getNullStatus().trim().equalsIgnoreCase("NOT NULL")) {
            this.setDefaultValue("NULL");
         }

         if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
            this.setDefault("DEFAULT ");
         }
      }

      if (this.getCastDatatype() != null) {
         this.getCastDatatype().toSybaseString();
         this.createCastFunction();
      }

      if (this.autoIncrement != null) {
         this.identity = "IDENTITY";
         this.autoIncrement = null;
      }

      this.asForIdentity = null;
   }

   public void toOracleString() throws ConvertException {
      this.constraintNullVector = new Vector();
      int indexOfNotNull = 0;
      boolean notNullAddedForPrimaryorUniqueConstraint = true;
      this.setCollate((String)null);
      this.setDatapageStorageLevel((String)null);
      this.setCollationName((String)null);
      this.generated = null;
      this.always = null;
      this.asForIdentity = null;
      this.byForAlways = null;
      this.defaultForIdentity = null;
      this.asForIdentity = null;
      if (this.nullStatus == null && SwisSQLOptions.sybaseNotNullConstraint && SwisSQLOptions.fromSybase && (this.getDatatype() != null || this.getUserDefinedDatatype() != null)) {
         this.nullStatus = "NOT NULL";
      }

      if (this.columnName != null) {
         if (this.columnName.startsWith("[") && this.columnName.endsWith("]") || this.columnName.startsWith("`") && this.columnName.endsWith("`")) {
            this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || this.columnName.indexOf(32) != -1) {
               this.columnName = "\"" + this.columnName + "\"";
            }
         }

         if (this.columnName.equalsIgnoreCase("SIZE")) {
            this.columnName = "SIZE_1";
         }

         this.columnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.columnName, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
      }

      CreateSequenceStatement createSequenceObj;
      TableObject tableObj;
      String oracleColumnName;
      String str;
      String defVal;
      String format;
      String dataTypeWithoutLength;
      if (this.identity != null) {
         createSequenceObj = new CreateSequenceStatement();
         tableObj = new TableObject();
         createSequenceObj.setSequence("SEQUENCE");
         oracleColumnName = this.columnName;
         if (!oracleColumnName.startsWith("[") && !oracleColumnName.startsWith("\"")) {
            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + "_" + oracleColumnName + "_SEQ";
               defVal = this.tableNameFromCQS + "_" + oracleColumnName;
               format = this.tableNameFromCQS;
               if (str.length() > 29) {
                  if (defVal.length() > 25) {
                     str = defVal.substring(0, 26) + "_SEQ";
                  } else if (format.length() > 25) {
                     str = format.substring(0, 26) + "_SEQ";
                  }

                  tableObj.setTableName(str);
                  dataTypeWithoutLength = tableObj.getTableName();
                  if (dataTypeWithoutLength.startsWith("\"")) {
                     dataTypeWithoutLength = StringFunctions.replaceAll("", "\"", dataTypeWithoutLength);
                     dataTypeWithoutLength = "\"" + dataTypeWithoutLength + "\"";
                     tableObj.setTableName(dataTypeWithoutLength);
                  }
               } else {
                  tableObj.setTableName(this.tableNameFromCQS + "_" + oracleColumnName + "_SEQ");
                  dataTypeWithoutLength = tableObj.getTableName();
                  if (dataTypeWithoutLength.startsWith("\"")) {
                     dataTypeWithoutLength = StringFunctions.replaceAll("", "\"", dataTypeWithoutLength);
                     dataTypeWithoutLength = "\"" + dataTypeWithoutLength + "\"";
                     tableObj.setTableName(dataTypeWithoutLength);
                  }
               }
            } else {
               tableObj.setTableName(this.columnName + "_SEQ");
               str = tableObj.getTableName();
               if (str.startsWith("\"")) {
                  str = StringFunctions.replaceAll("", "\"", str);
                  str = "\"" + str + "\"";
                  tableObj.setTableName(str);
               }
            }
         } else {
            oracleColumnName = "\"" + oracleColumnName.substring(1);
            if (oracleColumnName.endsWith("]") || oracleColumnName.endsWith("\"")) {
               oracleColumnName = oracleColumnName.substring(0, oracleColumnName.length() - 1) + "\"";
            }

            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + oracleColumnName.substring(1, oracleColumnName.length() - 1) + "_SEQ";
               defVal = this.tableNameFromCQS + oracleColumnName.substring(1, oracleColumnName.length() - 1);
               format = this.tableNameFromCQS;
               if (str.length() > 29) {
                  if (defVal.length() > 25) {
                     str = defVal.substring(0, 26) + "_SEQ";
                     if (str.startsWith("\"")) {
                        str = StringFunctions.replaceAll("", "\"", str);
                        str = "\"" + str + "\"";
                     }
                  } else if (format.length() > 25) {
                     str = format.substring(0, 26) + "_SEQ";
                     if (format.startsWith("\"")) {
                        str = StringFunctions.replaceAll("", "\"", format);
                        str = "\"" + str + "\"";
                     }
                  }

                  if (str.length() > 27) {
                     tableObj.setTableName("\"" + str.substring(0, 28) + "\"");
                     dataTypeWithoutLength = tableObj.getTableName();
                     if (dataTypeWithoutLength.startsWith("\"")) {
                        dataTypeWithoutLength = StringFunctions.replaceAll("", "\"", dataTypeWithoutLength);
                        dataTypeWithoutLength = "\"" + dataTypeWithoutLength + "\"";
                        tableObj.setTableName(dataTypeWithoutLength);
                     }
                  } else {
                     tableObj.setTableName("\"" + str + "\"");
                     dataTypeWithoutLength = tableObj.getTableName();
                     if (dataTypeWithoutLength.startsWith("\"")) {
                        dataTypeWithoutLength = StringFunctions.replaceAll("", "\"", dataTypeWithoutLength);
                        dataTypeWithoutLength = "\"" + dataTypeWithoutLength + "\"";
                        tableObj.setTableName(dataTypeWithoutLength);
                     }
                  }
               } else if (str.length() > 27) {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + oracleColumnName + "_S" + "\"");
                  dataTypeWithoutLength = tableObj.getTableName();
                  if (dataTypeWithoutLength.startsWith("\"")) {
                     dataTypeWithoutLength = StringFunctions.replaceAll("", "\"", dataTypeWithoutLength);
                     dataTypeWithoutLength = "\"" + dataTypeWithoutLength + "\"";
                     tableObj.setTableName(dataTypeWithoutLength);
                  }
               } else {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + oracleColumnName + "_SEQ" + "\"");
                  dataTypeWithoutLength = tableObj.getTableName();
                  if (dataTypeWithoutLength.startsWith("\"")) {
                     dataTypeWithoutLength = StringFunctions.replaceAll("", "\"", dataTypeWithoutLength);
                     dataTypeWithoutLength = "\"" + dataTypeWithoutLength + "\"";
                     tableObj.setTableName(dataTypeWithoutLength);
                  }
               }
            } else {
               tableObj.setTableName(oracleColumnName + "_SEQ");
               str = tableObj.getTableName();
               if (str.startsWith("\"")) {
                  str = StringFunctions.replaceAll("", "\"", str);
                  str = "\"" + str + "\"";
                  tableObj.setTableName(str);
               }
            }
         }

         createSequenceObj.setSchemaName(tableObj);
         if (this.identity.trim().equalsIgnoreCase("IDENTITY")) {
            createSequenceObj.setStart("START");
            createSequenceObj.setWith("WITH");
            createSequenceObj.setStartValue("1");
            createSequenceObj.setIncrementString("INCREMENT BY");
            createSequenceObj.setIncrementValue("1");
         } else {
            str = this.identity.trim().substring(8).trim();
            str = str.substring(1, str.length() - 1);
            StringTokenizer st = new StringTokenizer(str, ",");
            format = st.nextToken();
            createSequenceObj.setStart("START");
            createSequenceObj.setWith("WITH");
            createSequenceObj.setStartValue(format);
            if (Integer.parseInt(format) == 0) {
               createSequenceObj.setMinValueOrNoMinValue("MINVALUE 0");
            }

            if (st.countTokens() > 0) {
               dataTypeWithoutLength = st.nextToken();
               createSequenceObj.setIncrementString("INCREMENT BY");
               createSequenceObj.setIncrementValue(dataTypeWithoutLength);
            } else {
               createSequenceObj.setIncrementString("INCREMENT BY");
               createSequenceObj.setIncrementValue("1");
            }
         }

         if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n/" + "\n/\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
         } else {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n/" + "\n\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
         }

         this.setCreateSequenceString("CREATE " + createSequenceObj.toString());
         this.setIdentity((String)null);
      }

      Vector fnArgs;
      Vector defaultList;
      int i;
      boolean sizeIsMax;
      if (this.getConstraintClause() != null) {
         boolean primaryOrUniqueClauseEncountered = false;
         defaultList = this.getConstraintClause();
         if (defaultList != null) {
            Vector defaultConstraintVector = new Vector();

            ConstraintClause toOracleConstraintClause;
            for(i = 0; i < defaultList.size(); ++i) {
               toOracleConstraintClause = (ConstraintClause)defaultList.get(i);
               if (toOracleConstraintClause != null && toOracleConstraintClause.getConstraintType() instanceof DefaultConstraintClause) {
                  defaultConstraintVector.add(toOracleConstraintClause);
                  defaultList.remove(i);
               }
            }

            if (defaultConstraintVector.size() > 0) {
               for(i = 0; i < defaultConstraintVector.size(); ++i) {
                  if (i < defaultList.size()) {
                     defaultList.add(i, defaultConstraintVector.get(i));
                  } else {
                     defaultList.add(defaultConstraintVector.get(i));
                  }
               }
            }

            for(i = 0; i < defaultList.size(); ++i) {
               toOracleConstraintClause = (ConstraintClause)defaultList.get(i);
               toOracleConstraintClause.setTableNameFromCQS(this.tableNameFromCQS);
               toOracleConstraintClause.setColumnNameForSequence(this.columnName);
               if (toOracleConstraintClause != null) {
                  if (!(toOracleConstraintClause.getConstraintType() instanceof PrimaryOrUniqueConstraintClause)) {
                     if (toOracleConstraintClause.getConstraintType() instanceof DefaultConstraintClause) {
                        DefaultConstraintClause defaultConstraint = (DefaultConstraintClause)toOracleConstraintClause.getConstraintType();
                        if (defaultConstraint.getDefaultValue() != null && defaultConstraint.getDefaultValue().equalsIgnoreCase("NULL") && SwisSQLOptions.fromSybase && this.nullStatus != null && this.nullStatus.equalsIgnoreCase("NOT NULL")) {
                           this.nullStatus = "NULL";
                        }

                        if (this.columnName != null && defaultConstraint.getConstraintName() != null) {
                           toOracleConstraintClause.setConstraint((String)null);
                           toOracleConstraintClause.setConstraintName((String)null);
                           toOracleConstraintClause.toOracleString();
                           this.constraintNullVector.add(toOracleConstraintClause);
                        } else {
                           toOracleConstraintClause.toOracleString();
                           this.constraintNullVector.add(toOracleConstraintClause);
                        }
                     } else if (toOracleConstraintClause.getConstraintType() instanceof CheckConstraintClause) {
                        toOracleConstraintClause.toOracleString();
                        this.constraintNullVector.add(toOracleConstraintClause);
                     } else if (toOracleConstraintClause.getConstraintType() instanceof ForeignConstraintClause) {
                        toOracleConstraintClause.toOracleString();
                        this.constraintNullVector.add(toOracleConstraintClause);
                     } else if (toOracleConstraintClause.getNotNull() != null) {
                        notNullAddedForPrimaryorUniqueConstraint = false;
                        indexOfNotNull = i;
                        toOracleConstraintClause.toOracleString();
                        if (!primaryOrUniqueClauseEncountered) {
                           this.constraintNullVector.add(toOracleConstraintClause);
                        }
                     } else {
                        toOracleConstraintClause.toOracleString();
                        this.constraintNullVector.add(toOracleConstraintClause);
                     }
                  } else {
                     primaryOrUniqueClauseEncountered = true;
                     toOracleConstraintClause.setColumnName(this.getColumnName());
                     PrimaryOrUniqueConstraintClause primaryConstraintClause = (PrimaryOrUniqueConstraintClause)toOracleConstraintClause.getConstraintType();
                     sizeIsMax = false;
                     if (primaryConstraintClause.getConstraintColumnNames() != null && this.columnName != null) {
                        this.constraintNullVector.add(",\n\t");
                        CreateColumn cc = new CreateColumn();
                        if (this.getConstraintClause() != null) {
                           fnArgs = new Vector();

                           for(int index = 0; index < this.getConstraintClause().size(); ++index) {
                              if (this.getConstraintClause().get(index) instanceof PrimaryOrUniqueConstraintClause) {
                                 fnArgs.add(this.getConstraintClause().get(index));
                                 cc.setNullStatus((String)null);
                                 cc.setDefaultValue((String)null);
                                 cc.setDefault((String)null);
                                 cc.toOracleString();
                                 NotNull notnullObj = new NotNull();
                                 notnullObj.setNullStatus((String)null);
                                 notnullObj.setIdentity((String)null);
                                 cc.setNotNull(notnullObj);
                                 sizeIsMax = true;
                              }
                           }

                           cc.setConstraintClause(fnArgs);
                        }

                        if (sizeIsMax) {
                           this.constraintNullVector.add(cc);
                        }

                        notNullAddedForPrimaryorUniqueConstraint = false;
                        indexOfNotNull = i;
                     }

                     toOracleConstraintClause.toOracleString();
                     this.constraintNullVector.add(toOracleConstraintClause);
                  }
               }
            }
         }
      }

      if (this.getComputedColumnExpression() != null) {
         this.setComputedColumnExpression(this.getComputedColumnExpression().toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (this.getNullStatus() != null) {
         if (!notNullAddedForPrimaryorUniqueConstraint) {
            this.constraintNullVector.add(indexOfNotNull, this.getNullStatus());
         } else if (SwisSQLOptions.fromSybase) {
            if (this.getDefaultValue() != null && this.getDefaultValue().equalsIgnoreCase("NULL")) {
               this.constraintNullVector.add("NULL");
            } else {
               this.constraintNullVector.add(this.getNullStatus());
            }
         } else {
            this.constraintNullVector.add(this.getNullStatus());
         }
      }

      if (this.getDatatype() != null) {
         if (!this.mapDatatype(this, this.datatypeMapping)) {
            Datatype toOracleDatatype = this.getDatatype();
            BinClass bt;
            if (toOracleDatatype instanceof BinClass) {
               bt = (BinClass)toOracleDatatype;
               if (bt.getDatatypeName().trim().equalsIgnoreCase("BOOLEAN")) {
                  this.booleanOracle = true;
               }
            }

            CharacterClass ct;
            if (userConfiguredDatatypes.isEmpty()) {
               if (toOracleDatatype instanceof CharacterClass) {
                  ct = (CharacterClass)toOracleDatatype;
                  toOracleDatatype.toOracleString();
                  if ((toOracleDatatype.getDatatypeName().equalsIgnoreCase("varchar2") || toOracleDatatype.getDatatypeName().equalsIgnoreCase("nvarchar2")) && ct.getSize() == null) {
                     ct.setSize("1");
                     ct.setOpenBrace("(");
                     ct.setClosedBrace(")");
                  }

                  this.enumValuesConvertedToCheckConstraints(toOracleDatatype, this.constraintNullVector);
               } else {
                  toOracleDatatype.toOracleString();
               }
            } else {
               String length;
               boolean braceComes;
               if (toOracleDatatype instanceof DateClass) {
                  DateClass dt = (DateClass)toOracleDatatype;
                  oracleColumnName = new String();
                  braceComes = false;
                  oracleColumnName = oracleColumnName + dt.getDatatypeName().trim();
                  defVal = oracleColumnName;
                  if (dt.getSize() != null && !dt.getSize().trim().equals("")) {
                     oracleColumnName = oracleColumnName + "(" + dt.getSize().trim() + ")";
                     braceComes = true;
                  }

                  if (userConfiguredDatatypes.containsKey(oracleColumnName.toLowerCase())) {
                     format = userConfiguredDatatypes.get(oracleColumnName.toLowerCase()).toString();
                     if (format.indexOf("(") != -1 && format.indexOf(")") != -1 && format.indexOf("(") < format.indexOf(")")) {
                        dataTypeWithoutLength = format.substring(0, format.indexOf("("));
                        dt.setDatatypeName(dataTypeWithoutLength);
                        length = format.substring(format.indexOf("(") + 1, format.indexOf(")"));
                        dt.setSize(length);
                        dt.setOpenBrace("(");
                        dt.setClosedBrace(")");
                     } else {
                        dt.setDatatypeName(format);
                        if (!braceComes) {
                           dt.setSize((String)null);
                           dt.setOpenBrace((String)null);
                           dt.setClosedBrace((String)null);
                        }
                     }
                  } else if (userConfiguredDatatypes.containsKey(defVal.toLowerCase())) {
                     format = userConfiguredDatatypes.get(defVal.toLowerCase()).toString();
                     if (format.indexOf("(") != -1 && format.indexOf(")") != -1 && format.indexOf("(") < format.indexOf(")")) {
                        if (!braceComes) {
                           dataTypeWithoutLength = format.substring(0, format.indexOf("("));
                           dt.setDatatypeName(dataTypeWithoutLength);
                           length = format.substring(format.indexOf("(") + 1, format.indexOf(")"));
                           dt.setSize(length);
                           dt.setOpenBrace("(");
                           dt.setClosedBrace(")");
                        } else {
                           toOracleDatatype.toOracleString();
                        }
                     } else {
                        dt.setDatatypeName(format);
                        if (!braceComes) {
                           dt.setSize((String)null);
                           dt.setOpenBrace((String)null);
                           dt.setClosedBrace((String)null);
                        }
                     }
                  } else {
                     toOracleDatatype.toOracleString();
                  }
               } else {
                  boolean braceComes;
                  String precision;
                  String scale;
                  if (toOracleDatatype instanceof NumericClass) {
                     NumericClass nt = (NumericClass)toOracleDatatype;
                     oracleColumnName = new String();
                     oracleColumnName = oracleColumnName + nt.getDatatypeName().trim();
                     str = oracleColumnName;
                     braceComes = false;
                     if (nt.getPrecision() != null && !nt.getPrecision().trim().equals("")) {
                        if (nt.getScale() != null && !nt.getScale().trim().equals("")) {
                           oracleColumnName = oracleColumnName + "(" + nt.getPrecision().trim() + "," + nt.getScale() + ")";
                        } else {
                           oracleColumnName = oracleColumnName + "(" + nt.getPrecision().trim() + ")";
                        }

                        braceComes = true;
                     }

                     if (userConfiguredDatatypes.containsKey(oracleColumnName.toLowerCase())) {
                        format = userConfiguredDatatypes.get(oracleColumnName.toLowerCase()).toString();
                        if (format.indexOf("(") != -1 && format.indexOf(")") != -1 && format.indexOf("(") < format.indexOf(")")) {
                           dataTypeWithoutLength = format.substring(0, format.indexOf("("));
                           nt.setDatatypeName(dataTypeWithoutLength);
                           length = format.substring(format.indexOf("(") + 1, format.indexOf(")"));
                           if (length.indexOf(",") != -1) {
                              precision = length.substring(0, length.indexOf(","));
                              nt.setPrecision(precision);
                              scale = length.substring(length.indexOf(",") + 1);
                              nt.setScale(scale);
                              nt.setOpenBrace("(");
                              nt.setClosedBrace(")");
                           } else {
                              nt.setPrecision(length);
                              nt.setOpenBrace("(");
                              nt.setClosedBrace(")");
                           }
                        } else {
                           nt.setDatatypeName(format);
                           if (!braceComes) {
                              nt.setPrecision((String)null);
                              nt.setScale((String)null);
                              nt.setOpenBrace((String)null);
                              nt.setClosedBrace((String)null);
                           }
                        }
                     } else if (userConfiguredDatatypes.containsKey(str.toLowerCase())) {
                        format = userConfiguredDatatypes.get(str.toLowerCase()).toString();
                        if (format.indexOf("(") != -1 && format.indexOf(")") != -1 && format.indexOf("(") < format.indexOf(")")) {
                           if (!braceComes) {
                              dataTypeWithoutLength = format.substring(0, format.indexOf("("));
                              nt.setDatatypeName(dataTypeWithoutLength);
                              length = format.substring(format.indexOf("(") + 1, format.indexOf(")"));
                              if (length.indexOf(",") != -1) {
                                 precision = length.substring(0, length.indexOf(","));
                                 nt.setPrecision(precision);
                                 scale = length.substring(length.indexOf(",") + 1);
                                 nt.setScale(scale);
                                 nt.setOpenBrace("(");
                                 nt.setClosedBrace(")");
                              } else {
                                 nt.setPrecision(length);
                                 nt.setOpenBrace("(");
                                 nt.setClosedBrace(")");
                              }
                           } else {
                              toOracleDatatype.toOracleString();
                           }
                        } else {
                           nt.setDatatypeName(format);
                           if (!braceComes) {
                              nt.setPrecision((String)null);
                              nt.setScale((String)null);
                              nt.setOpenBrace((String)null);
                              nt.setClosedBrace((String)null);
                           }
                        }
                     } else {
                        toOracleDatatype.toOracleString();
                     }
                  } else if (toOracleDatatype instanceof CharacterClass) {
                     ct = (CharacterClass)toOracleDatatype;
                     oracleColumnName = new String();
                     oracleColumnName = oracleColumnName + ct.getDatatypeName().trim();
                     braceComes = false;
                     defVal = oracleColumnName;
                     if (ct.getSize() != null && !ct.getSize().trim().equals("")) {
                        oracleColumnName = oracleColumnName + "(" + ct.getSize().trim() + ")";
                        braceComes = true;
                        if (ct.getSize().equalsIgnoreCase("max")) {
                           braceComes = false;
                        }
                     }

                     if (userConfiguredDatatypes.containsKey(oracleColumnName.toLowerCase())) {
                        format = userConfiguredDatatypes.get(oracleColumnName.toLowerCase()).toString();
                        if (format.indexOf("(") != -1 && format.indexOf(")") != -1 && format.indexOf("(") < format.indexOf(")")) {
                           dataTypeWithoutLength = format.substring(0, format.indexOf("("));
                           ct.setDatatypeName(dataTypeWithoutLength);
                           length = format.substring(format.indexOf("(") + 1, format.indexOf(")"));
                           ct.setSize(length);
                           ct.setOpenBrace("(");
                           ct.setClosedBrace(")");
                        } else {
                           ct.setDatatypeName(format);
                           if (!braceComes) {
                              ct.setSize((String)null);
                              ct.setOpenBrace((String)null);
                              ct.setClosedBrace((String)null);
                           }
                        }
                     } else if (userConfiguredDatatypes.containsKey(defVal.toLowerCase())) {
                        format = userConfiguredDatatypes.get(defVal.toLowerCase()).toString();
                        sizeIsMax = ct.getSize().equalsIgnoreCase("max");
                        if (format.indexOf("(") != -1 && format.indexOf(")") != -1 && format.indexOf("(") < format.indexOf(")")) {
                           if (!braceComes) {
                              length = format.substring(0, format.indexOf("("));
                              ct.setDatatypeName(length);
                              precision = format.substring(format.indexOf("(") + 1, format.indexOf(")"));
                              ct.setSize(precision);
                              ct.setOpenBrace("(");
                              ct.setClosedBrace(")");
                           } else {
                              toOracleDatatype.toOracleString();
                           }
                        } else {
                           ct.setDatatypeName(format);
                           if (!braceComes) {
                              ct.setSize((String)null);
                              ct.setOpenBrace((String)null);
                              ct.setClosedBrace((String)null);
                           }
                        }

                        if (sizeIsMax) {
                           if (defVal.equalsIgnoreCase("varchar")) {
                              ct.setSize("4000");
                           } else if (defVal.equalsIgnoreCase("nvarchar")) {
                              ct.setSize("2000");
                           }

                           ct.setOpenBrace("(");
                           ct.setClosedBrace(")");
                        }
                     } else {
                        toOracleDatatype.toOracleString();
                        if ((toOracleDatatype.getDatatypeName().equalsIgnoreCase("varchar2") || toOracleDatatype.getDatatypeName().equalsIgnoreCase("nvarchar2")) && ct.getSize() == null) {
                           ct.setSize("1");
                           ct.setOpenBrace("(");
                           ct.setClosedBrace(")");
                        }
                     }
                  } else if (toOracleDatatype instanceof BinClass) {
                     bt = (BinClass)toOracleDatatype;
                     oracleColumnName = new String();
                     oracleColumnName = oracleColumnName + bt.getDatatypeName().trim();
                     braceComes = false;
                     defVal = oracleColumnName;
                     if (bt.getSize() != null && !bt.getSize().trim().equals("")) {
                        oracleColumnName = oracleColumnName + "(" + bt.getSize().trim() + ")";
                        braceComes = true;
                     }

                     if (userConfiguredDatatypes.containsKey(oracleColumnName.toLowerCase())) {
                        format = userConfiguredDatatypes.get(oracleColumnName.toLowerCase()).toString();
                        if (format.indexOf("(") != -1 && format.indexOf(")") != -1 && format.indexOf("(") < format.indexOf(")")) {
                           dataTypeWithoutLength = format.substring(0, format.indexOf("("));
                           bt.setDatatypeName(dataTypeWithoutLength);
                           length = format.substring(format.indexOf("(") + 1, format.indexOf(")"));
                           bt.setSize(length);
                           bt.setOpenBrace("(");
                           bt.setClosedBrace(")");
                        } else {
                           bt.setDatatypeName(format);
                           if (!braceComes) {
                              bt.setSize((String)null);
                              bt.setOpenBrace((String)null);
                              bt.setClosedBrace((String)null);
                           }
                        }
                     } else if (userConfiguredDatatypes.containsKey(defVal.toLowerCase())) {
                        format = userConfiguredDatatypes.get(defVal.toLowerCase()).toString();
                        if (format.indexOf("(") != -1 && format.indexOf(")") != -1 && format.indexOf("(") < format.indexOf(")")) {
                           if (!braceComes) {
                              dataTypeWithoutLength = format.substring(0, format.indexOf("("));
                              bt.setDatatypeName(dataTypeWithoutLength);
                              length = format.substring(format.indexOf("(") + 1, format.indexOf(")"));
                              bt.setSize(length);
                              bt.setOpenBrace("(");
                              bt.setClosedBrace(")");
                           } else {
                              toOracleDatatype.toOracleString();
                           }
                        } else {
                           bt.setDatatypeName(format);
                           if (!braceComes) {
                              bt.setSize((String)null);
                              bt.setOpenBrace((String)null);
                              bt.setClosedBrace((String)null);
                           }
                        }
                     } else {
                        toOracleDatatype.toOracleString();
                     }
                  } else if (toOracleDatatype instanceof QuotedIdentifierDatatype) {
                     QuotedIdentifierDatatype nt = (QuotedIdentifierDatatype)toOracleDatatype;
                     oracleColumnName = new String();
                     oracleColumnName = oracleColumnName + nt.getDatatypeName().trim();
                     str = oracleColumnName;
                     braceComes = false;
                     if (nt.getPrecision() != null && !nt.getPrecision().trim().equals("")) {
                        if (nt.getScale() != null && !nt.getScale().trim().equals("")) {
                           oracleColumnName = oracleColumnName + "(" + nt.getPrecision().trim() + "," + nt.getScale() + ")";
                        } else {
                           oracleColumnName = oracleColumnName + "(" + nt.getPrecision().trim() + ")";
                        }

                        braceComes = true;
                     }

                     if (userConfiguredDatatypes.containsKey(oracleColumnName.toLowerCase())) {
                        format = userConfiguredDatatypes.get(oracleColumnName.toLowerCase()).toString();
                        if (format.indexOf("(") != -1 && format.indexOf(")") != -1 && format.indexOf("(") < format.indexOf(")")) {
                           dataTypeWithoutLength = format.substring(0, format.indexOf("("));
                           nt.setDatatypeName(dataTypeWithoutLength);
                           length = format.substring(format.indexOf("(") + 1, format.indexOf(")"));
                           if (length.indexOf(",") != -1) {
                              precision = length.substring(0, length.indexOf(","));
                              nt.setPrecision(precision);
                              scale = length.substring(length.indexOf(",") + 1);
                              nt.setScale(scale);
                              nt.setOpenBrace("(");
                              nt.setClosedBrace(")");
                           } else {
                              nt.setPrecision(length);
                              nt.setOpenBrace("(");
                              nt.setClosedBrace(")");
                           }
                        } else {
                           nt.setDatatypeName(format);
                           if (!braceComes) {
                              nt.setPrecision((String)null);
                              nt.setScale((String)null);
                              nt.setOpenBrace((String)null);
                              nt.setClosedBrace((String)null);
                           }
                        }
                     } else if (userConfiguredDatatypes.containsKey(str.toLowerCase())) {
                        format = userConfiguredDatatypes.get(str.toLowerCase()).toString();
                        if (format.indexOf("(") != -1 && format.indexOf(")") != -1 && format.indexOf("(") < format.indexOf(")")) {
                           if (!braceComes) {
                              dataTypeWithoutLength = format.substring(0, format.indexOf("("));
                              nt.setDatatypeName(dataTypeWithoutLength);
                              length = format.substring(format.indexOf("(") + 1, format.indexOf(")"));
                              if (length.indexOf(",") != -1) {
                                 precision = length.substring(0, length.indexOf(","));
                                 nt.setPrecision(precision);
                                 scale = length.substring(length.indexOf(",") + 1);
                                 nt.setScale(scale);
                                 nt.setOpenBrace("(");
                                 nt.setClosedBrace(")");
                              } else {
                                 nt.setPrecision(length);
                                 nt.setOpenBrace("(");
                                 nt.setClosedBrace(")");
                              }
                           } else {
                              toOracleDatatype.toOracleString();
                           }
                        } else {
                           nt.setDatatypeName(format);
                           if (!braceComes) {
                              nt.setPrecision((String)null);
                              nt.setScale((String)null);
                              nt.setOpenBrace((String)null);
                              nt.setClosedBrace((String)null);
                           }
                        }
                     } else {
                        toOracleDatatype.toOracleString();
                     }
                  }
               }
            }
         } else if (this.getDatatype() != null && this.getDatatype().getDatatypeName() != null && this.getDatatype().getSize() != null && this.getDatatype().getDatatypeName().startsWith("varchar") && this.getDatatype().getSize().equalsIgnoreCase("max")) {
            this.getDatatype().setSize((String)null);
            this.getDatatype().setOpenBrace((String)null);
            this.getDatatype().setClosedBrace((String)null);
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() > 0) {
         if (!this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
            if (userdefinedDatatypes.containsKey(this.userDefinedDatatype.toLowerCase().trim())) {
               this.setUserDefinedDatatype((String)userdefinedDatatypes.get(this.userDefinedDatatype.toLowerCase().trim()));
            } else if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
               this.setUserDefinedDatatype("CHAR(36)");
            } else if (this.userDefinedDatatype.equalsIgnoreCase("SQL_VARIANT")) {
               this.setUserDefinedDatatype("SYS.ANYDATA");
            }
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
         if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
            this.setUserDefinedDatatype("CHAR(36)");
         } else if (this.userDefinedDatatype.equalsIgnoreCase("SQL_VARIANT")) {
            this.setUserDefinedDatatype("SYS.ANYDATA");
         }
      }

      if (this.getDefaultValue() != null) {
         if (!this.defaultValue.equalsIgnoreCase("CURRENT_DATE") && !this.defaultValue.equalsIgnoreCase("CURRENT_TIME") && !this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP") && !this.defaultValue.equalsIgnoreCase("CURRENT DATE") && !this.defaultValue.equalsIgnoreCase("CURRENT TIME") && !this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP") && !this.defaultValue.equalsIgnoreCase("CURRENT")) {
            if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
               if (this.defaultValue.startsWith("'") || this.defaultValue.startsWith("\"")) {
                  if (this.getDatatype() instanceof DateClass) {
                     if (this.defaultValue.startsWith("\"")) {
                        this.defaultValue = "'" + this.defaultValue.substring(1);
                     }

                     if (this.defaultValue.endsWith("\"")) {
                        this.defaultValue = this.defaultValue.substring(0, this.defaultValue.length() - 1) + "'";
                     }

                     if (!this.defaultValue.equals("'0000-00-00 00:00:00'") && !this.defaultValue.equals("0000-00-00 00:00:00")) {
                        if (!this.defaultValue.equals("'0000-00-00'") && !this.defaultValue.equals("0000-00-00")) {
                           String format = SwisSQLUtils.getDateFormat(this.defaultValue, 1);
                           if (format != null) {
                              FunctionCalls fc = new FunctionCalls();
                              TableColumn tc = new TableColumn();
                              tc.setColumnName("TO_DATE");
                              Vector fnArgs = new Vector();
                              if (format.startsWith("'1900")) {
                                 fnArgs.add(format);
                                 fnArgs.add("'YYYY-MM-DD HH24:MI:SS'");
                              } else {
                                 fnArgs.add(this.defaultValue);
                                 fnArgs.add(format);
                              }

                              fc.setFunctionName(tc);
                              fc.setFunctionArguments(fnArgs);
                              this.setDefaultFunction(fc);
                              this.setDefaultValue((String)null);
                           }
                        } else {
                           this.setDefaultValue("TO_DATE('0001-01-01', 'YYYY-MM-DD')");
                        }
                     } else {
                        this.setDefaultValue("TO_DATE('0001-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')");
                     }
                  } else if (this.getDatatype() instanceof CharacterClass) {
                     CharacterClass cc = (CharacterClass)this.getDatatype();
                     String dtype = cc.getDatatypeName();
                     if (dtype != null && (dtype.indexOf("char") != -1 || dtype.indexOf("CHAR") != -1) && cc.getSize() != null) {
                        int size = Integer.parseInt(cc.getSize());
                        i = this.defaultValue.length() - 2;
                        if (i > size) {
                           this.setDefaultValue((String)null);
                           this.setDefault((String)null);
                           this.setDefaultOpenBrace((String)null);
                           this.setDefaultClosedBrace((String)null);
                        }
                     }
                  }
               }
            } else {
               this.setDefaultValue("USER");
            }
         } else {
            this.setDefaultValue("SYSDATE");
         }

         if (this.getDefaultValue() != null) {
            StringTokenizer st = new StringTokenizer(this.getDefaultValue(), ".");
            defaultList = new Vector();
            StringBuffer sb = new StringBuffer();

            while(st.hasMoreTokens()) {
               defaultList.add(st.nextToken());
            }

            i = 0;

            while(true) {
               if (i >= defaultList.size()) {
                  if (this.datatype instanceof BinClass) {
                     BinClass binClass = (BinClass)this.datatype;
                     if (binClass.getDatatypeName() != null && binClass.getDatatypeName().trim().equalsIgnoreCase("BOOLEAN")) {
                        if (this.getDefaultValue().trim().equalsIgnoreCase("TRUE")) {
                           this.setDefaultValue("1");
                        } else {
                           this.setDefaultValue("0");
                        }
                        break;
                     }

                     this.setDefaultValue(sb.toString());
                     break;
                  }

                  this.setDefaultValue(sb.toString());
                  break;
               }

               defVal = (String)defaultList.get(i);
               if (defVal.startsWith("[") || defVal.startsWith("\"")) {
                  defVal = "'" + defVal.substring(1);
               }

               if (defVal.endsWith("]") || defVal.endsWith("\"")) {
                  defVal = defVal.substring(0, defVal.length() - 1) + "'";
               }

               if (i > 0) {
                  sb.append(".");
               }

               sb.append(defVal);
               ++i;
            }
         }

         if (this.getDefault() != null && this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
            this.setDefault("DEFAULT ");
         }

         if (SwisSQLOptions.fromSybase && this.getDefaultValue() != null && this.getDefaultValue().equalsIgnoreCase("NULL")) {
            this.nullStatus = "NULL";
         }
      } else if (this.getDefaultFunction() != null) {
         this.setDefaultFunction(this.functionCall.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      } else if (this.getDefaultExpression() != null) {
         ArrayList newExpList = new ArrayList();

         for(int i = 0; i < this.defaultExpList.size(); ++i) {
            if (!(this.defaultExpList.get(i) instanceof SelectColumn)) {
               newExpList.add(this.defaultExpList.get(i));
            } else {
               SelectColumn sc = ((SelectColumn)this.defaultExpList.get(i)).toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               Object sc_val;
               if (this.getDatatype() instanceof DateClass && sc.getColumnExpression().size() == 1) {
                  sc_val = sc.getColumnExpression().get(0);
                  if (sc_val instanceof String && sc_val.toString().startsWith("'") && sc_val.toString().length() == 10) {
                     sc.getColumnExpression().setElementAt("to_date(" + sc_val.toString() + ",'YYYYMMDD')", 0);
                  } else if (sc_val instanceof String) {
                     defVal = sc_val.toString();
                     if (defVal.startsWith("N") && defVal.endsWith("'")) {
                        defVal = defVal.substring(defVal.indexOf("'"));
                     }

                     format = SwisSQLUtils.getDateFormat(defVal, 1);
                     if (format != null) {
                        FunctionCalls fc = new FunctionCalls();
                        TableColumn tc = new TableColumn();
                        if (format.toLowerCase().indexOf("ff") != -1) {
                           tc.setColumnName("TO_TIMESTAMP");
                        } else {
                           tc.setColumnName("TO_DATE");
                        }

                        fnArgs = new Vector();
                        if (format.startsWith("'1900")) {
                           fnArgs.add(format);
                           fnArgs.add("'YYYY-MM-DD HH24:MI:SS'");
                        } else {
                           fnArgs.add(defVal);
                           fnArgs.add(format);
                        }

                        fc.setFunctionName(tc);
                        fc.setFunctionArguments(fnArgs);
                        this.setDefaultFunction(fc);
                        this.setDefaultValue((String)null);
                     }
                  }
               } else if (this.getDatatype() instanceof BinClass && sc.getColumnExpression().size() == 1) {
                  sc_val = sc.getColumnExpression().get(0);
                  if (sc_val instanceof String) {
                     defVal = sc_val.toString();
                     if (!defVal.trim().equalsIgnoreCase("'false'") && !defVal.trim().equalsIgnoreCase("'f'")) {
                        if (defVal.trim().equalsIgnoreCase("'true'") || defVal.trim().equalsIgnoreCase("'t'")) {
                           sc.getColumnExpression().setElementAt("1", 0);
                        }
                     } else {
                        sc.getColumnExpression().setElementAt("0", 0);
                     }
                  }
               }

               newExpList.add(sc);
            }
         }

         this.setDefaultExpression(newExpList);
      } else if (this.getDefault() != null) {
         if (this.getDatatype() != null) {
            if (this.getDatatype() instanceof NumericClass) {
               this.setDefaultValue("0");
            } else if (this.getDatatype() instanceof BinClass) {
               this.setDefaultValue("''");
            } else if (this.getDatatype() instanceof CharacterClass) {
               this.setDefaultValue("' '");
            } else if (this.getDatatype() instanceof DateClass) {
               this.setDefaultValue("TO_DATE('0001-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')");
            } else {
               this.setDefaultValue("NULL");
            }
         } else {
            this.setDefaultValue("NULL");
         }

         if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
            this.setDefault("DEFAULT ");
         }
      }

      if (this.autoIncrement != null) {
         createSequenceObj = new CreateSequenceStatement();
         tableObj = new TableObject();
         createSequenceObj.setSequence("SEQUENCE");
         oracleColumnName = this.columnName;
         if (!oracleColumnName.startsWith("[") && !oracleColumnName.startsWith("\"")) {
            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + oracleColumnName + "_SEQ";
               defVal = this.tableNameFromCQS + oracleColumnName;
               format = this.tableNameFromCQS;
               if (str.length() > 29) {
                  if (defVal.length() > 25) {
                     str = defVal.substring(0, 26) + "_SEQ";
                  } else if (format.length() > 25) {
                     str = format.substring(0, 26) + "_SEQ";
                  }

                  tableObj.setTableName(str);
               } else {
                  tableObj.setTableName(this.tableNameFromCQS + "_" + oracleColumnName + "_SEQ");
               }
            } else {
               tableObj.setTableName(this.columnName + "_SEQ");
            }
         } else {
            oracleColumnName = "\"" + oracleColumnName.substring(1);
            if (oracleColumnName.endsWith("]") || oracleColumnName.endsWith("\"")) {
               oracleColumnName = oracleColumnName.substring(0, oracleColumnName.length() - 1) + "\"";
            }

            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + oracleColumnName.substring(1, oracleColumnName.length() - 1) + "_SEQ";
               defVal = this.tableNameFromCQS + oracleColumnName.substring(1, oracleColumnName.length() - 1);
               format = this.tableNameFromCQS;
               if (str.length() > 29) {
                  if (defVal.length() > 25) {
                     str = defVal.substring(0, 26) + "_SEQ";
                  } else if (format.length() > 25) {
                     str = format.substring(0, 26) + "_SEQ";
                  }

                  if (str.length() > 27) {
                     tableObj.setTableName("\"" + str.substring(0, 28) + "\"");
                  } else {
                     tableObj.setTableName("\"" + str + "\"");
                  }
               } else if (str.length() > 27) {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + oracleColumnName + "_S" + "\"");
               } else {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + oracleColumnName + "_SEQ" + "\"");
               }
            } else {
               tableObj.setTableName(oracleColumnName + "_SEQ");
            }
         }

         createSequenceObj.setSchemaName(tableObj);
         createSequenceObj.setStart("START");
         createSequenceObj.setWith("WITH");
         createSequenceObj.setStartValue("1");
         createSequenceObj.setIncrementString("INCREMENT BY");
         createSequenceObj.setIncrementValue("1");
         if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n/" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
         } else {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n/" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
         }

         this.setAutoIncrement((String)null);
      }

      if (this.getCastDatatype() != null) {
         this.getCastDatatype().toOracleString();
         this.createCastFunction();
      }

   }

   public void toBigQueryString() throws ConvertException {
      int indexOfNotNull = false;
      boolean notNullAddedForPrimaryorUniqueConstraint = true;
      this.constraintNullVector = new Vector();
      this.setCollate((String)null);
      this.setCollationName((String)null);
      this.setDatapageStorageLevel((String)null);
      this.generated = null;
      this.always = null;
      this.asForIdentity = null;
      this.byForAlways = null;
      this.defaultForIdentity = null;
      this.asForIdentity = null;
      if (this.columnName != null) {
         if (this.columnName.startsWith("[") && this.columnName.endsWith("]") || this.columnName.startsWith("`") && this.columnName.endsWith("`")) {
            this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
            if (this.columnName.indexOf(32) != -1) {
               this.columnName = "\"" + this.columnName + "\"";
            }
         }

         this.columnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.columnName, SwisSQLUtils.getKeywords(14), (ModifiedObjectAttr)null, 14);
      }

      CreateSequenceStatement createSequenceObj;
      TableObject tableObj;
      String bigqueryColumnName;
      String str;
      String str1;
      String str2;
      if (this.identity != null) {
         createSequenceObj = new CreateSequenceStatement();
         tableObj = new TableObject();
         createSequenceObj.setSequence("SEQUENCE");
         bigqueryColumnName = this.columnName;
         String table_name_str;
         if (!bigqueryColumnName.startsWith("[") && !bigqueryColumnName.startsWith("\"")) {
            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + "_" + bigqueryColumnName + "_SEQ";
               str1 = this.tableNameFromCQS + "_" + bigqueryColumnName;
               str2 = this.tableNameFromCQS;
               if (str.length() > 63) {
                  if (str1.length() > 59) {
                     str = str1.substring(0, 60) + "_SEQ";
                  } else if (str2.length() > 59) {
                     str = str2.substring(0, 60) + "_SEQ";
                  }

                  tableObj.setTableName(str);
                  table_name_str = tableObj.getTableName();
                  if (table_name_str.startsWith("\"")) {
                     table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                     table_name_str = "\"" + table_name_str + "\"";
                     tableObj.setTableName(table_name_str);
                  }
               } else {
                  tableObj.setTableName(this.tableNameFromCQS + "_" + bigqueryColumnName + "_SEQ");
                  table_name_str = tableObj.getTableName();
                  if (table_name_str.startsWith("\"")) {
                     table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                     table_name_str = "\"" + table_name_str + "\"";
                     tableObj.setTableName(table_name_str);
                  }
               }
            } else {
               tableObj.setTableName(this.columnName + "_SEQ");
               str = tableObj.getTableName();
               if (str.startsWith("\"")) {
                  str = StringFunctions.replaceAll("", "\"", str);
                  str = "\"" + str + "\"";
                  tableObj.setTableName(str);
               }
            }
         } else {
            bigqueryColumnName = "\"" + bigqueryColumnName.substring(1);
            if (bigqueryColumnName.endsWith("]") || bigqueryColumnName.endsWith("\"")) {
               bigqueryColumnName = bigqueryColumnName.substring(0, bigqueryColumnName.length() - 1) + "\"";
            }

            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + bigqueryColumnName.substring(1, bigqueryColumnName.length() - 1) + "_SEQ";
               str1 = this.tableNameFromCQS + bigqueryColumnName.substring(1, bigqueryColumnName.length() - 1);
               str2 = this.tableNameFromCQS;
               if (str.length() > 63) {
                  if (str1.length() > 59) {
                     str = str1.substring(0, 60) + "_SEQ";
                     if (str.startsWith("\"")) {
                        str = StringFunctions.replaceAll("", "\"", str);
                        str = "\"" + str + "\"";
                     }
                  } else if (str2.length() > 59) {
                     str = str2.substring(0, 60) + "_SEQ";
                     if (str2.startsWith("\"")) {
                        str = StringFunctions.replaceAll("", "\"", str2);
                        str = "\"" + str + "\"";
                     }
                  }

                  if (str.length() > 60) {
                     tableObj.setTableName("\"" + str.substring(0, 61) + "\"");
                     table_name_str = tableObj.getTableName();
                     if (table_name_str.startsWith("\"")) {
                        table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                        table_name_str = "\"" + table_name_str + "\"";
                        tableObj.setTableName(table_name_str);
                     }
                  } else {
                     tableObj.setTableName("\"" + str + "\"");
                     table_name_str = tableObj.getTableName();
                     if (table_name_str.startsWith("\"")) {
                        table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                        table_name_str = "\"" + table_name_str + "\"";
                        tableObj.setTableName(table_name_str);
                     }
                  }
               } else if (str.length() > 60) {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + bigqueryColumnName + "_S" + "\"");
                  table_name_str = tableObj.getTableName();
                  if (table_name_str.startsWith("\"")) {
                     table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                     table_name_str = "\"" + table_name_str + "\"";
                     tableObj.setTableName(table_name_str);
                  }
               } else {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + bigqueryColumnName + "_SEQ" + "\"");
                  table_name_str = tableObj.getTableName();
                  if (table_name_str.startsWith("\"")) {
                     table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                     table_name_str = "\"" + table_name_str + "\"";
                     tableObj.setTableName(table_name_str);
                  }
               }
            } else {
               tableObj.setTableName(bigqueryColumnName + "_SEQ");
               str = tableObj.getTableName();
               if (str.startsWith("\"")) {
                  str = StringFunctions.replaceAll("", "\"", str);
                  str = "\"" + str + "\"";
                  tableObj.setTableName(str);
               }
            }
         }

         createSequenceObj.setSchemaName(tableObj);
         if (this.identity.trim().equalsIgnoreCase("IDENTITY")) {
            createSequenceObj.setStart("START");
            createSequenceObj.setStartValue("1");
            createSequenceObj.setIncrementString("INCREMENT ");
            createSequenceObj.setIncrementValue("1");
         } else {
            str = this.identity.trim().substring(8).trim();
            str = str.substring(1, str.length() - 1);
            StringTokenizer st = new StringTokenizer(str, ",");
            str2 = st.nextToken();
            createSequenceObj.setStart("START");
            createSequenceObj.setStartValue(str2);
            if (Integer.parseInt(str2) == 0) {
               createSequenceObj.setMinValueOrNoMinValue("MINVALUE 0");
            }

            if (st.countTokens() > 0) {
               table_name_str = st.nextToken();
               createSequenceObj.setIncrementString("INCREMENT ");
               createSequenceObj.setIncrementValue(table_name_str);
            } else {
               createSequenceObj.setIncrementString("INCREMENT ");
               createSequenceObj.setIncrementValue("1");
            }
         }

         if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n;" + "\n/\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
         } else {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
         }

         this.setCreateSequenceString("CREATE " + createSequenceObj.toString());
         this.setIdentity((String)null);
      }

      if (this.getConstraintClause() != null) {
         boolean primaryOrUniqueClauseEncountered = false;
         Vector changeConstraintVector = this.getConstraintClause();
         if (changeConstraintVector != null) {
            Vector defaultConstraintVector = new Vector();

            ConstraintClause toBigQueryConstraintClause;
            int index;
            for(index = 0; index < changeConstraintVector.size(); ++index) {
               toBigQueryConstraintClause = (ConstraintClause)changeConstraintVector.get(index);
               if (toBigQueryConstraintClause != null && toBigQueryConstraintClause.getConstraintType() instanceof DefaultConstraintClause) {
                  defaultConstraintVector.add(toBigQueryConstraintClause);
                  changeConstraintVector.remove(index);
               }
            }

            if (defaultConstraintVector.size() > 0) {
               for(index = 0; index < defaultConstraintVector.size(); ++index) {
                  if (index < changeConstraintVector.size()) {
                     changeConstraintVector.add(index, defaultConstraintVector.get(index));
                  } else {
                     changeConstraintVector.add(defaultConstraintVector.get(index));
                  }
               }
            }

            for(index = 0; index < changeConstraintVector.size(); ++index) {
               toBigQueryConstraintClause = (ConstraintClause)changeConstraintVector.get(index);
               toBigQueryConstraintClause.setTableNameFromCQS(this.tableNameFromCQS);
               toBigQueryConstraintClause.setColumnNameForSequence(this.columnName);
               if (toBigQueryConstraintClause != null) {
                  if (!(toBigQueryConstraintClause.getConstraintType() instanceof PrimaryOrUniqueConstraintClause)) {
                     if (toBigQueryConstraintClause.getConstraintType() instanceof DefaultConstraintClause) {
                        DefaultConstraintClause defaultConstraint = (DefaultConstraintClause)toBigQueryConstraintClause.getConstraintType();
                        if (defaultConstraint.getDefaultValue() != null && defaultConstraint.getDefaultValue().equalsIgnoreCase("NULL") && SwisSQLOptions.fromSybase && this.nullStatus != null && this.nullStatus.equalsIgnoreCase("NOT NULL")) {
                           this.nullStatus = "NULL";
                        }

                        if (this.columnName != null && defaultConstraint.getConstraintName() != null) {
                           toBigQueryConstraintClause.setConstraint((String)null);
                           toBigQueryConstraintClause.setConstraintName((String)null);
                           toBigQueryConstraintClause.toBigQueryString();
                           this.constraintNullVector.add(toBigQueryConstraintClause);
                        } else {
                           toBigQueryConstraintClause.toBigQueryString();
                           this.constraintNullVector.add(toBigQueryConstraintClause);
                        }
                     } else if (toBigQueryConstraintClause.getConstraintType() instanceof CheckConstraintClause) {
                        toBigQueryConstraintClause.toBigQueryString();
                        this.constraintNullVector.add(toBigQueryConstraintClause);
                     } else if (toBigQueryConstraintClause.getConstraintType() instanceof ForeignConstraintClause) {
                        toBigQueryConstraintClause.toBigQueryString();
                        this.constraintNullVector.add(toBigQueryConstraintClause);
                     } else if (toBigQueryConstraintClause.getNotNull() != null) {
                        notNullAddedForPrimaryorUniqueConstraint = false;
                        toBigQueryConstraintClause.toBigQueryString();
                        if (!primaryOrUniqueClauseEncountered) {
                           this.constraintNullVector.add(toBigQueryConstraintClause);
                        }
                     } else {
                        toBigQueryConstraintClause.toBigQueryString();
                        this.constraintNullVector.add(toBigQueryConstraintClause);
                     }
                  } else {
                     primaryOrUniqueClauseEncountered = true;
                     toBigQueryConstraintClause.setColumnName(this.getColumnName());
                     PrimaryOrUniqueConstraintClause primaryConstraintClause = (PrimaryOrUniqueConstraintClause)toBigQueryConstraintClause.getConstraintType();
                     boolean ccToBeAdded = false;
                     if (primaryConstraintClause.getConstraintColumnNames() != null && this.columnName != null) {
                        this.constraintNullVector.add(",\n\t");
                        CreateColumn cc = new CreateColumn();
                        if (this.getConstraintClause() != null) {
                           Vector primaryConstraintVector = new Vector();

                           for(int index = 0; index < this.getConstraintClause().size(); ++index) {
                              if (this.getConstraintClause().get(index) instanceof PrimaryOrUniqueConstraintClause) {
                                 primaryConstraintVector.add(this.getConstraintClause().get(index));
                                 cc.setNullStatus((String)null);
                                 cc.setDefaultValue((String)null);
                                 cc.setDefault((String)null);
                                 cc.toBigQueryString();
                                 NotNull notnullObj = new NotNull();
                                 notnullObj.setNullStatus((String)null);
                                 notnullObj.setIdentity((String)null);
                                 cc.setNotNull(notnullObj);
                                 ccToBeAdded = true;
                              }
                           }

                           cc.setConstraintClause(primaryConstraintVector);
                        }

                        if (ccToBeAdded) {
                           this.constraintNullVector.add(cc);
                        }

                        notNullAddedForPrimaryorUniqueConstraint = false;
                     }

                     toBigQueryConstraintClause.toBigQueryString();
                     this.constraintNullVector.add(toBigQueryConstraintClause);
                  }
               }
            }
         }
      }

      if (this.getComputedColumnExpression() != null) {
         this.setComputedColumnExpression(this.getComputedColumnExpression().toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (this.getNullStatus() != null) {
         this.constraintNullVector.add(this.getNullStatus());
      }

      if (this.getDatatype() != null) {
         if (!this.mapDatatype(this, this.datatypeMapping)) {
            Datatype toBigQueryDatatype = this.getDatatype();
            toBigQueryDatatype.toBigQueryString();
            if (toBigQueryDatatype instanceof CharacterClass) {
               this.enumValuesConvertedToCheckConstraints(toBigQueryDatatype, this.constraintNullVector);
            }
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() > 0) {
         if (!this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
            if (userdefinedDatatypes.containsKey(this.userDefinedDatatype.toLowerCase().trim())) {
               this.setUserDefinedDatatype((String)userdefinedDatatypes.get(this.userDefinedDatatype.toLowerCase().trim()));
            } else if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
               this.setUserDefinedDatatype("CHAR(36)");
            }
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype) && this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
         this.setUserDefinedDatatype("CHAR(36)");
      }

      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("CURRENT_TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("USER")) {
            if (this.defaultValue.startsWith("0x") || this.defaultValue.startsWith("0X")) {
               this.setDefaultValue("'" + this.defaultValue.substring(1, this.defaultValue.length()) + "'");
            }
         } else {
            this.setDefaultValue("CURRENT_USER");
         }
      } else if (this.getDefaultFunction() != null) {
         this.setDefaultFunction(this.functionCall.toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      } else if (this.getDefaultExpression() != null) {
         ArrayList newExpList = new ArrayList();

         for(int i = 0; i < this.defaultExpList.size(); ++i) {
            if (this.defaultExpList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.defaultExpList.get(i);
               newExpList.add(sc.toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            } else {
               newExpList.add(this.defaultExpList.get(i));
            }
         }

         this.setDefaultExpression(newExpList);
      } else if (this.getDefault() != null) {
         String def = this.getDefault();
         if (def.equalsIgnoreCase("WITH DEFAULT ")) {
            this.setDefault("DEFAULT ");
         }
      }

      if (this.autoIncrement != null) {
         createSequenceObj = new CreateSequenceStatement();
         tableObj = new TableObject();
         createSequenceObj.setSequence("SEQUENCE");
         bigqueryColumnName = this.columnName;
         if (!bigqueryColumnName.startsWith("[") && !bigqueryColumnName.startsWith("\"")) {
            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + bigqueryColumnName + "_SEQ";
               str1 = this.tableNameFromCQS + bigqueryColumnName;
               str2 = this.tableNameFromCQS;
               if (str.length() > 63) {
                  if (str1.length() > 59) {
                     str = str1.substring(0, 60) + "_SEQ";
                  } else if (str2.length() > 59) {
                     str = str2.substring(0, 60) + "_SEQ";
                  }

                  tableObj.setTableName(str);
               } else {
                  tableObj.setTableName(this.tableNameFromCQS + "_" + bigqueryColumnName + "_SEQ");
               }
            } else {
               tableObj.setTableName(this.columnName + "_SEQ");
            }
         } else {
            bigqueryColumnName = "\"" + bigqueryColumnName.substring(1);
            if (bigqueryColumnName.endsWith("]") || bigqueryColumnName.endsWith("\"")) {
               bigqueryColumnName = bigqueryColumnName.substring(0, bigqueryColumnName.length() - 1) + "\"";
            }

            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + bigqueryColumnName.substring(1, bigqueryColumnName.length() - 1) + "_SEQ";
               str1 = this.tableNameFromCQS + bigqueryColumnName.substring(1, bigqueryColumnName.length() - 1);
               str2 = this.tableNameFromCQS;
               if (str.length() > 63) {
                  if (str1.length() > 59) {
                     str = str1.substring(0, 60) + "_SEQ";
                  } else if (str2.length() > 59) {
                     str = str2.substring(0, 60) + "_SEQ";
                  }

                  if (str.length() > 60) {
                     tableObj.setTableName("\"" + str.substring(0, 61) + "\"");
                  } else {
                     tableObj.setTableName("\"" + str + "\"");
                  }
               } else if (str.length() > 60) {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + bigqueryColumnName + "_S" + "\"");
               } else {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + bigqueryColumnName + "_SEQ" + "\"");
               }
            } else {
               tableObj.setTableName(bigqueryColumnName + "_SEQ");
            }
         }

         createSequenceObj.setSchemaName(tableObj);
         createSequenceObj.setStart("START");
         createSequenceObj.setStartValue("1");
         createSequenceObj.setIncrementString("INCREMENT BY");
         createSequenceObj.setIncrementValue("1");
         if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
         } else {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
         }

         this.setCreateSequenceString("CREATE " + createSequenceObj.toString());
         this.setAutoIncrement((String)null);
      }

      if (this.getCastDatatype() != null) {
         this.getCastDatatype().toBigQueryString();
         this.setDefaultValue(this.getDefaultValue() + "::" + this.getCastDatatype());
      }

   }

   public void toPostgreSQLString() throws ConvertException {
      int indexOfNotNull = false;
      boolean notNullAddedForPrimaryorUniqueConstraint = true;
      this.constraintNullVector = new Vector();
      this.setCollate((String)null);
      this.setCollationName((String)null);
      this.setDatapageStorageLevel((String)null);
      this.generated = null;
      this.always = null;
      this.asForIdentity = null;
      this.byForAlways = null;
      this.defaultForIdentity = null;
      this.asForIdentity = null;
      if (this.columnName != null) {
         if (this.columnName.startsWith("[") && this.columnName.endsWith("]") || this.columnName.startsWith("`") && this.columnName.endsWith("`")) {
            this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
            if (this.columnName.indexOf(32) != -1) {
               this.columnName = "\"" + this.columnName + "\"";
            }
         }

         this.columnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.columnName, SwisSQLUtils.getKeywords(4), (ModifiedObjectAttr)null, 4);
      }

      CreateSequenceStatement createSequenceObj;
      TableObject tableObj;
      String postgresqlColumnName;
      String str;
      String str1;
      String str2;
      if (this.identity != null) {
         createSequenceObj = new CreateSequenceStatement();
         tableObj = new TableObject();
         createSequenceObj.setSequence("SEQUENCE");
         postgresqlColumnName = this.columnName;
         String table_name_str;
         if (!postgresqlColumnName.startsWith("[") && !postgresqlColumnName.startsWith("\"")) {
            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + "_" + postgresqlColumnName + "_SEQ";
               str1 = this.tableNameFromCQS + "_" + postgresqlColumnName;
               str2 = this.tableNameFromCQS;
               if (str.length() > 63) {
                  if (str1.length() > 59) {
                     str = str1.substring(0, 60) + "_SEQ";
                  } else if (str2.length() > 59) {
                     str = str2.substring(0, 60) + "_SEQ";
                  }

                  tableObj.setTableName(str);
                  table_name_str = tableObj.getTableName();
                  if (table_name_str.startsWith("\"")) {
                     table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                     table_name_str = "\"" + table_name_str + "\"";
                     tableObj.setTableName(table_name_str);
                  }
               } else {
                  tableObj.setTableName(this.tableNameFromCQS + "_" + postgresqlColumnName + "_SEQ");
                  table_name_str = tableObj.getTableName();
                  if (table_name_str.startsWith("\"")) {
                     table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                     table_name_str = "\"" + table_name_str + "\"";
                     tableObj.setTableName(table_name_str);
                  }
               }
            } else {
               tableObj.setTableName(this.columnName + "_SEQ");
               str = tableObj.getTableName();
               if (str.startsWith("\"")) {
                  str = StringFunctions.replaceAll("", "\"", str);
                  str = "\"" + str + "\"";
                  tableObj.setTableName(str);
               }
            }
         } else {
            postgresqlColumnName = "\"" + postgresqlColumnName.substring(1);
            if (postgresqlColumnName.endsWith("]") || postgresqlColumnName.endsWith("\"")) {
               postgresqlColumnName = postgresqlColumnName.substring(0, postgresqlColumnName.length() - 1) + "\"";
            }

            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + postgresqlColumnName.substring(1, postgresqlColumnName.length() - 1) + "_SEQ";
               str1 = this.tableNameFromCQS + postgresqlColumnName.substring(1, postgresqlColumnName.length() - 1);
               str2 = this.tableNameFromCQS;
               if (str.length() > 63) {
                  if (str1.length() > 59) {
                     str = str1.substring(0, 60) + "_SEQ";
                     if (str.startsWith("\"")) {
                        str = StringFunctions.replaceAll("", "\"", str);
                        str = "\"" + str + "\"";
                     }
                  } else if (str2.length() > 59) {
                     str = str2.substring(0, 60) + "_SEQ";
                     if (str2.startsWith("\"")) {
                        str = StringFunctions.replaceAll("", "\"", str2);
                        str = "\"" + str + "\"";
                     }
                  }

                  if (str.length() > 60) {
                     tableObj.setTableName("\"" + str.substring(0, 61) + "\"");
                     table_name_str = tableObj.getTableName();
                     if (table_name_str.startsWith("\"")) {
                        table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                        table_name_str = "\"" + table_name_str + "\"";
                        tableObj.setTableName(table_name_str);
                     }
                  } else {
                     tableObj.setTableName("\"" + str + "\"");
                     table_name_str = tableObj.getTableName();
                     if (table_name_str.startsWith("\"")) {
                        table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                        table_name_str = "\"" + table_name_str + "\"";
                        tableObj.setTableName(table_name_str);
                     }
                  }
               } else if (str.length() > 60) {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + postgresqlColumnName + "_S" + "\"");
                  table_name_str = tableObj.getTableName();
                  if (table_name_str.startsWith("\"")) {
                     table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                     table_name_str = "\"" + table_name_str + "\"";
                     tableObj.setTableName(table_name_str);
                  }
               } else {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + postgresqlColumnName + "_SEQ" + "\"");
                  table_name_str = tableObj.getTableName();
                  if (table_name_str.startsWith("\"")) {
                     table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                     table_name_str = "\"" + table_name_str + "\"";
                     tableObj.setTableName(table_name_str);
                  }
               }
            } else {
               tableObj.setTableName(postgresqlColumnName + "_SEQ");
               str = tableObj.getTableName();
               if (str.startsWith("\"")) {
                  str = StringFunctions.replaceAll("", "\"", str);
                  str = "\"" + str + "\"";
                  tableObj.setTableName(str);
               }
            }
         }

         createSequenceObj.setSchemaName(tableObj);
         if (this.identity.trim().equalsIgnoreCase("IDENTITY")) {
            createSequenceObj.setStart("START");
            createSequenceObj.setStartValue("1");
            createSequenceObj.setIncrementString("INCREMENT ");
            createSequenceObj.setIncrementValue("1");
         } else {
            str = this.identity.trim().substring(8).trim();
            str = str.substring(1, str.length() - 1);
            StringTokenizer st = new StringTokenizer(str, ",");
            str2 = st.nextToken();
            createSequenceObj.setStart("START");
            createSequenceObj.setStartValue(str2);
            if (Integer.parseInt(str2) == 0) {
               createSequenceObj.setMinValueOrNoMinValue("MINVALUE 0");
            }

            if (st.countTokens() > 0) {
               table_name_str = st.nextToken();
               createSequenceObj.setIncrementString("INCREMENT ");
               createSequenceObj.setIncrementValue(table_name_str);
            } else {
               createSequenceObj.setIncrementString("INCREMENT ");
               createSequenceObj.setIncrementValue("1");
            }
         }

         if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n;" + "\n/\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
         } else {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
         }

         this.setCreateSequenceString("CREATE " + createSequenceObj.toString());
         this.setIdentity((String)null);
      }

      if (this.getConstraintClause() != null) {
         boolean primaryOrUniqueClauseEncountered = false;
         Vector changeConstraintVector = this.getConstraintClause();
         if (changeConstraintVector != null) {
            Vector defaultConstraintVector = new Vector();

            ConstraintClause toPostgreSQLConstraintClause;
            int index;
            for(index = 0; index < changeConstraintVector.size(); ++index) {
               toPostgreSQLConstraintClause = (ConstraintClause)changeConstraintVector.get(index);
               if (toPostgreSQLConstraintClause != null && toPostgreSQLConstraintClause.getConstraintType() instanceof DefaultConstraintClause) {
                  defaultConstraintVector.add(toPostgreSQLConstraintClause);
                  changeConstraintVector.remove(index);
               }
            }

            if (defaultConstraintVector.size() > 0) {
               for(index = 0; index < defaultConstraintVector.size(); ++index) {
                  if (index < changeConstraintVector.size()) {
                     changeConstraintVector.add(index, defaultConstraintVector.get(index));
                  } else {
                     changeConstraintVector.add(defaultConstraintVector.get(index));
                  }
               }
            }

            for(index = 0; index < changeConstraintVector.size(); ++index) {
               toPostgreSQLConstraintClause = (ConstraintClause)changeConstraintVector.get(index);
               toPostgreSQLConstraintClause.setTableNameFromCQS(this.tableNameFromCQS);
               toPostgreSQLConstraintClause.setColumnNameForSequence(this.columnName);
               if (toPostgreSQLConstraintClause != null) {
                  if (!(toPostgreSQLConstraintClause.getConstraintType() instanceof PrimaryOrUniqueConstraintClause)) {
                     if (toPostgreSQLConstraintClause.getConstraintType() instanceof DefaultConstraintClause) {
                        DefaultConstraintClause defaultConstraint = (DefaultConstraintClause)toPostgreSQLConstraintClause.getConstraintType();
                        if (defaultConstraint.getDefaultValue() != null && defaultConstraint.getDefaultValue().equalsIgnoreCase("NULL") && SwisSQLOptions.fromSybase && this.nullStatus != null && this.nullStatus.equalsIgnoreCase("NOT NULL")) {
                           this.nullStatus = "NULL";
                        }

                        if (this.columnName != null && defaultConstraint.getConstraintName() != null) {
                           toPostgreSQLConstraintClause.setConstraint((String)null);
                           toPostgreSQLConstraintClause.setConstraintName((String)null);
                           toPostgreSQLConstraintClause.toPostgreSQLString();
                           this.constraintNullVector.add(toPostgreSQLConstraintClause);
                        } else {
                           toPostgreSQLConstraintClause.toPostgreSQLString();
                           this.constraintNullVector.add(toPostgreSQLConstraintClause);
                        }
                     } else if (toPostgreSQLConstraintClause.getConstraintType() instanceof CheckConstraintClause) {
                        toPostgreSQLConstraintClause.toPostgreSQLString();
                        this.constraintNullVector.add(toPostgreSQLConstraintClause);
                     } else if (toPostgreSQLConstraintClause.getConstraintType() instanceof ForeignConstraintClause) {
                        toPostgreSQLConstraintClause.toPostgreSQLString();
                        this.constraintNullVector.add(toPostgreSQLConstraintClause);
                     } else if (toPostgreSQLConstraintClause.getNotNull() != null) {
                        notNullAddedForPrimaryorUniqueConstraint = false;
                        toPostgreSQLConstraintClause.toPostgreSQLString();
                        if (!primaryOrUniqueClauseEncountered) {
                           this.constraintNullVector.add(toPostgreSQLConstraintClause);
                        }
                     } else {
                        toPostgreSQLConstraintClause.toPostgreSQLString();
                        this.constraintNullVector.add(toPostgreSQLConstraintClause);
                     }
                  } else {
                     primaryOrUniqueClauseEncountered = true;
                     toPostgreSQLConstraintClause.setColumnName(this.getColumnName());
                     PrimaryOrUniqueConstraintClause primaryConstraintClause = (PrimaryOrUniqueConstraintClause)toPostgreSQLConstraintClause.getConstraintType();
                     boolean ccToBeAdded = false;
                     if (primaryConstraintClause.getConstraintColumnNames() != null && this.columnName != null) {
                        this.constraintNullVector.add(",\n\t");
                        CreateColumn cc = new CreateColumn();
                        if (this.getConstraintClause() != null) {
                           Vector primaryConstraintVector = new Vector();

                           for(int index = 0; index < this.getConstraintClause().size(); ++index) {
                              if (this.getConstraintClause().get(index) instanceof PrimaryOrUniqueConstraintClause) {
                                 primaryConstraintVector.add(this.getConstraintClause().get(index));
                                 cc.setNullStatus((String)null);
                                 cc.setDefaultValue((String)null);
                                 cc.setDefault((String)null);
                                 cc.toPostgreSQLString();
                                 NotNull notnullObj = new NotNull();
                                 notnullObj.setNullStatus((String)null);
                                 notnullObj.setIdentity((String)null);
                                 cc.setNotNull(notnullObj);
                                 ccToBeAdded = true;
                              }
                           }

                           cc.setConstraintClause(primaryConstraintVector);
                        }

                        if (ccToBeAdded) {
                           this.constraintNullVector.add(cc);
                        }

                        notNullAddedForPrimaryorUniqueConstraint = false;
                     }

                     toPostgreSQLConstraintClause.toPostgreSQLString();
                     this.constraintNullVector.add(toPostgreSQLConstraintClause);
                  }
               }
            }
         }
      }

      if (this.getComputedColumnExpression() != null) {
         this.setComputedColumnExpression(this.getComputedColumnExpression().toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (this.getNullStatus() != null) {
         this.constraintNullVector.add(this.getNullStatus());
      }

      if (this.getDatatype() != null) {
         if (!this.mapDatatype(this, this.datatypeMapping)) {
            Datatype toPostgreSQLDatatype = this.getDatatype();
            toPostgreSQLDatatype.toPostgreSQLString();
            if (toPostgreSQLDatatype instanceof CharacterClass) {
               this.enumValuesConvertedToCheckConstraints(toPostgreSQLDatatype, this.constraintNullVector);
            }
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() > 0) {
         if (!this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
            if (userdefinedDatatypes.containsKey(this.userDefinedDatatype.toLowerCase().trim())) {
               this.setUserDefinedDatatype((String)userdefinedDatatypes.get(this.userDefinedDatatype.toLowerCase().trim()));
            } else if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
               this.setUserDefinedDatatype("CHAR(36)");
            }
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype) && this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
         this.setUserDefinedDatatype("CHAR(36)");
      }

      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("CURRENT_TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("USER")) {
            if (this.defaultValue.startsWith("0x") || this.defaultValue.startsWith("0X")) {
               this.setDefaultValue("'" + this.defaultValue.substring(1, this.defaultValue.length()) + "'");
            }
         } else {
            this.setDefaultValue("CURRENT_USER");
         }
      } else if (this.getDefaultFunction() != null) {
         this.setDefaultFunction(this.functionCall.toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      } else if (this.getDefaultExpression() != null) {
         ArrayList newExpList = new ArrayList();

         for(int i = 0; i < this.defaultExpList.size(); ++i) {
            if (this.defaultExpList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.defaultExpList.get(i);
               newExpList.add(sc.toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            } else {
               newExpList.add(this.defaultExpList.get(i));
            }
         }

         this.setDefaultExpression(newExpList);
      } else if (this.getDefault() != null) {
         String def = this.getDefault();
         if (def.equalsIgnoreCase("WITH DEFAULT ")) {
            this.setDefault("DEFAULT ");
         }
      }

      if (this.autoIncrement != null) {
         createSequenceObj = new CreateSequenceStatement();
         tableObj = new TableObject();
         createSequenceObj.setSequence("SEQUENCE");
         postgresqlColumnName = this.columnName;
         if (!postgresqlColumnName.startsWith("[") && !postgresqlColumnName.startsWith("\"")) {
            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + postgresqlColumnName + "_SEQ";
               str1 = this.tableNameFromCQS + postgresqlColumnName;
               str2 = this.tableNameFromCQS;
               if (str.length() > 63) {
                  if (str1.length() > 59) {
                     str = str1.substring(0, 60) + "_SEQ";
                  } else if (str2.length() > 59) {
                     str = str2.substring(0, 60) + "_SEQ";
                  }

                  tableObj.setTableName(str);
               } else {
                  tableObj.setTableName(this.tableNameFromCQS + "_" + postgresqlColumnName + "_SEQ");
               }
            } else {
               tableObj.setTableName(this.columnName + "_SEQ");
            }
         } else {
            postgresqlColumnName = "\"" + postgresqlColumnName.substring(1);
            if (postgresqlColumnName.endsWith("]") || postgresqlColumnName.endsWith("\"")) {
               postgresqlColumnName = postgresqlColumnName.substring(0, postgresqlColumnName.length() - 1) + "\"";
            }

            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + postgresqlColumnName.substring(1, postgresqlColumnName.length() - 1) + "_SEQ";
               str1 = this.tableNameFromCQS + postgresqlColumnName.substring(1, postgresqlColumnName.length() - 1);
               str2 = this.tableNameFromCQS;
               if (str.length() > 63) {
                  if (str1.length() > 59) {
                     str = str1.substring(0, 60) + "_SEQ";
                  } else if (str2.length() > 59) {
                     str = str2.substring(0, 60) + "_SEQ";
                  }

                  if (str.length() > 60) {
                     tableObj.setTableName("\"" + str.substring(0, 61) + "\"");
                  } else {
                     tableObj.setTableName("\"" + str + "\"");
                  }
               } else if (str.length() > 60) {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + postgresqlColumnName + "_S" + "\"");
               } else {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + postgresqlColumnName + "_SEQ" + "\"");
               }
            } else {
               tableObj.setTableName(postgresqlColumnName + "_SEQ");
            }
         }

         createSequenceObj.setSchemaName(tableObj);
         createSequenceObj.setStart("START");
         createSequenceObj.setStartValue("1");
         createSequenceObj.setIncrementString("INCREMENT BY");
         createSequenceObj.setIncrementValue("1");
         if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
         } else {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
         }

         this.setCreateSequenceString("CREATE " + createSequenceObj.toString());
         this.setAutoIncrement((String)null);
      }

      if (this.getCastDatatype() != null) {
         this.getCastDatatype().toPostgreSQLString();
         this.setDefaultValue(this.getDefaultValue() + "::" + this.getCastDatatype());
      }

   }

   public void toMySQLString() throws ConvertException {
      this.constraintNullVector = new Vector();
      this.setCollate((String)null);
      this.setCollationName((String)null);
      this.generated = null;
      this.always = null;
      this.asForIdentity = null;
      this.byForAlways = null;
      this.setDatapageStorageLevel((String)null);
      this.defaultForIdentity = null;
      this.asForIdentity = null;
      if (this.columnName != null) {
         if ((!this.columnName.startsWith("[") || !this.columnName.endsWith("]")) && (!this.columnName.startsWith("\"") || !this.columnName.endsWith("\""))) {
            if (!this.columnName.startsWith("`") && !this.columnName.endsWith("`")) {
               this.columnName = "`" + this.columnName + "`";
            }
         } else {
            this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
            this.columnName = "`" + this.columnName + "`";
         }
      }

      int size;
      Datatype toMySQLDatatype;
      if (this.getConstraintClause() != null) {
         Vector changeConstraintVector = this.getConstraintClause();
         if (changeConstraintVector != null) {
            for(size = 0; size < changeConstraintVector.size(); ++size) {
               ConstraintClause toMySQLConstraintClause = (ConstraintClause)changeConstraintVector.get(size);
               if (toMySQLConstraintClause != null) {
                  if (toMySQLConstraintClause.getNotNull() != null) {
                     NotNull notNullClause = toMySQLConstraintClause.getNotNull();
                     if (notNullClause.getIdentity() != null) {
                        notNullClause.setIdentity((String)null);
                        this.setAutoIncrement("AUTO_INCREMENT");
                     }
                  }

                  if (this.getIdentity() != null) {
                     toMySQLConstraintClause.setAutoIncrement("AUTO_INCREMENT");
                     this.setIdentity((String)null);
                     if (this.getDatatype() != null) {
                        Datatype datatype = this.getDatatype();
                        NumericClass numericClass = (NumericClass)datatype;
                        String dataTypeName = numericClass.getDatatypeName();
                        if (dataTypeName != null) {
                           numericClass.setDatatypeName("INT");
                           numericClass.setSize((String)null);
                           numericClass.setPrecision((String)null);
                           numericClass.setScale((String)null);
                           numericClass.setOpenBrace((String)null);
                           numericClass.setClosedBrace((String)null);
                        }
                     }
                  }

                  toMySQLConstraintClause.setColumnName(this.getColumnName());
                  if (toMySQLConstraintClause.getConstraintType() instanceof PrimaryOrUniqueConstraintClause) {
                     PrimaryOrUniqueConstraintClause primaryConstraintClause = (PrimaryOrUniqueConstraintClause)toMySQLConstraintClause.getConstraintType();
                     boolean ccToBeAdded = false;
                     if (primaryConstraintClause.getConstraintColumnNames() != null && this.columnName != null) {
                        this.constraintNullVector.add(",\n\t");
                        CreateColumn cc = new CreateColumn();
                        if (this.getConstraintClause() != null) {
                           Vector primaryConstraintVector = new Vector();

                           for(int index = 0; index < this.getConstraintClause().size(); ++index) {
                              if (this.getConstraintClause().get(index) instanceof PrimaryOrUniqueConstraintClause) {
                                 primaryConstraintVector.add(this.getConstraintClause().get(index));
                                 cc.setNullStatus((String)null);
                                 cc.setDefaultValue((String)null);
                                 cc.setDefault((String)null);
                                 cc.toOracleString();
                                 NotNull notnullObj = new NotNull();
                                 notnullObj.setNullStatus((String)null);
                                 notnullObj.setIdentity((String)null);
                                 cc.setNotNull(notnullObj);
                                 ccToBeAdded = true;
                              }
                           }

                           cc.setConstraintClause(primaryConstraintVector);
                        }

                        if (ccToBeAdded) {
                           this.constraintNullVector.add(cc);
                        }
                     }
                  }

                  toMySQLConstraintClause.toMySQLString();
                  this.constraintNullVector.add(toMySQLConstraintClause);
               }
            }
         }
      } else {
         if (this.identity != null) {
            this.setAutoIncrement("AUTO_INCREMENT");
            if (this.getDatatype() != null) {
               toMySQLDatatype = this.getDatatype();
               if (toMySQLDatatype instanceof NumericClass) {
                  NumericClass numericClass = (NumericClass)toMySQLDatatype;
                  String dataTypeName = numericClass.getDatatypeName();
                  if (dataTypeName != null) {
                     numericClass.setDatatypeName("INT");
                     numericClass.setSize((String)null);
                     numericClass.setPrecision((String)null);
                     numericClass.setScale((String)null);
                     numericClass.setOpenBrace((String)null);
                     numericClass.setClosedBrace((String)null);
                  }
               }
            }
         }

         this.setIdentity((String)null);
      }

      if (this.getComputedColumnExpression() != null) {
         this.setComputedColumnExpression(this.getComputedColumnExpression().toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (this.getNullStatus() != null) {
         this.constraintNullVector.add(this.getNullStatus());
      }

      if (this.getDefaultExpression() != null) {
         new ArrayList();
         size = this.defaultExpList.size();
         if (size == 1) {
            if (this.defaultExpList.get(0) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.defaultExpList.get(0);
               Vector expList = sc.getColumnExpression();
               if (expList.size() == 1) {
                  Object obj = expList.get(0);
                  if (obj instanceof String) {
                     this.setDefaultValue((String)obj);
                  } else if (obj instanceof TableColumn) {
                     TableColumn tc = (TableColumn)obj;
                     String colName = tc.getColumnName();
                     if (colName != null) {
                        this.setDefaultValue(colName);
                     }
                  }
               }
            } else {
               ArrayList expWithoutSc = this.getDefaultExpression();
               if (expWithoutSc.size() == 1 && expWithoutSc.get(0) instanceof String) {
                  this.setDefaultValue((String)expWithoutSc.get(0));
               }
            }
         }
      }

      if (this.getDatatype() != null) {
         if (!this.mapDatatype(this, this.datatypeMapping)) {
            toMySQLDatatype = this.getDatatype();
            if (toMySQLDatatype instanceof CharacterClass) {
               CharacterClass cc = (CharacterClass)toMySQLDatatype;
               if (cc.getDatatypeName() != null && cc.getDatatypeName().equalsIgnoreCase("LONG")) {
                  this.setDefault((String)null);
                  this.setDefaultValue((String)null);
               }
            }

            if (toMySQLDatatype instanceof DateClass) {
               DateClass dc = (DateClass)toMySQLDatatype;
               if (dc.getDatatypeName() != null && dc.getDatatypeName().equalsIgnoreCase("TIMESTAMP") && this.getDefault() != null && this.getDefaultValue() != null && this.getDefaultValue().trim().equalsIgnoreCase("NULL")) {
                  this.setDefaultValue("0");
               }
            }

            toMySQLDatatype.toMySQLString();
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() > 0) {
         if (!this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
            if (userdefinedDatatypes.containsKey(this.userDefinedDatatype.toLowerCase().trim())) {
               this.setUserDefinedDatatype((String)userdefinedDatatypes.get(this.userDefinedDatatype.toLowerCase().trim()));
            } else if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
               this.setUserDefinedDatatype("CHAR(36)");
            }
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype) && this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
         this.setUserDefinedDatatype("CHAR(36)");
      }

      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("'CURRENT_DATE'");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("'CURRENT_DATE'");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("'CURRENT_TIME'");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("'CURRENT_DATE'");
         } else if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
            if (this.defaultValue.equalsIgnoreCase("true")) {
               this.setDefaultValue("1");
            } else if (this.defaultValue.equalsIgnoreCase("false")) {
               this.setDefaultValue("0");
            }
         } else {
            this.setDefaultValue("USER()");
         }

         this.setDefaultOpenBrace((String)null);
         this.setDefaultClosedBrace((String)null);
      } else if (this.getDefaultFunction() != null) {
         FunctionCalls fnCall = this.getDefaultFunction();
         TableColumn tabCol = fnCall.getFunctionName();
         if (tabCol != null && tabCol.getColumnName().equalsIgnoreCase("getdate")) {
            this.setDefaultValue("'CURRENT_DATE'");
            this.setDefaultFunction((FunctionCalls)null);
         } else {
            this.setDefaultFunction(this.functionCall.toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         }
      } else if (this.getDefault() != null) {
         this.setDefaultOpenBrace((String)null);
         this.setDefaultClosedBrace((String)null);
         this.setDefaultValue("NULL");
         if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
            this.setDefault("DEFAULT ");
         }
      }

      if (this.getCastDatatype() != null) {
      }

   }

   public void toSnowflakeString() throws ConvertException {
      int indexOfNotNull = false;
      boolean notNullAddedForPrimaryorUniqueConstraint = true;
      this.constraintNullVector = new Vector();
      this.setCollate((String)null);
      this.setCollationName((String)null);
      this.setDatapageStorageLevel((String)null);
      this.generated = null;
      this.always = null;
      this.asForIdentity = null;
      this.byForAlways = null;
      this.defaultForIdentity = null;
      this.asForIdentity = null;
      if (this.columnName != null) {
         if (this.columnName.startsWith("[") && this.columnName.endsWith("]") || this.columnName.startsWith("`") && this.columnName.endsWith("`")) {
            this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
            if (this.columnName.indexOf(32) != -1) {
               this.columnName = "\"" + this.columnName + "\"";
            }
         }

         this.columnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.columnName, SwisSQLUtils.getKeywords(15), (ModifiedObjectAttr)null, 15);
      }

      CreateSequenceStatement createSequenceObj;
      TableObject tableObj;
      String snowflakeColumnName;
      String str;
      String str1;
      String str2;
      if (this.identity != null) {
         createSequenceObj = new CreateSequenceStatement();
         tableObj = new TableObject();
         createSequenceObj.setSequence("SEQUENCE");
         snowflakeColumnName = this.columnName;
         String table_name_str;
         if (!snowflakeColumnName.startsWith("[") && !snowflakeColumnName.startsWith("\"")) {
            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + "_" + snowflakeColumnName + "_SEQ";
               str1 = this.tableNameFromCQS + "_" + snowflakeColumnName;
               str2 = this.tableNameFromCQS;
               if (str.length() > 63) {
                  if (str1.length() > 59) {
                     str = str1.substring(0, 60) + "_SEQ";
                  } else if (str2.length() > 59) {
                     str = str2.substring(0, 60) + "_SEQ";
                  }

                  tableObj.setTableName(str);
                  table_name_str = tableObj.getTableName();
                  if (table_name_str.startsWith("\"")) {
                     table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                     table_name_str = "\"" + table_name_str + "\"";
                     tableObj.setTableName(table_name_str);
                  }
               } else {
                  tableObj.setTableName(this.tableNameFromCQS + "_" + snowflakeColumnName + "_SEQ");
                  table_name_str = tableObj.getTableName();
                  if (table_name_str.startsWith("\"")) {
                     table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                     table_name_str = "\"" + table_name_str + "\"";
                     tableObj.setTableName(table_name_str);
                  }
               }
            } else {
               tableObj.setTableName(this.columnName + "_SEQ");
               str = tableObj.getTableName();
               if (str.startsWith("\"")) {
                  str = StringFunctions.replaceAll("", "\"", str);
                  str = "\"" + str + "\"";
                  tableObj.setTableName(str);
               }
            }
         } else {
            snowflakeColumnName = "\"" + snowflakeColumnName.substring(1);
            if (snowflakeColumnName.endsWith("]") || snowflakeColumnName.endsWith("\"")) {
               snowflakeColumnName = snowflakeColumnName.substring(0, snowflakeColumnName.length() - 1) + "\"";
            }

            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + snowflakeColumnName.substring(1, snowflakeColumnName.length() - 1) + "_SEQ";
               str1 = this.tableNameFromCQS + snowflakeColumnName.substring(1, snowflakeColumnName.length() - 1);
               str2 = this.tableNameFromCQS;
               if (str.length() > 63) {
                  if (str1.length() > 59) {
                     str = str1.substring(0, 60) + "_SEQ";
                     if (str.startsWith("\"")) {
                        str = StringFunctions.replaceAll("", "\"", str);
                        str = "\"" + str + "\"";
                     }
                  } else if (str2.length() > 59) {
                     str = str2.substring(0, 60) + "_SEQ";
                     if (str2.startsWith("\"")) {
                        str = StringFunctions.replaceAll("", "\"", str2);
                        str = "\"" + str + "\"";
                     }
                  }

                  if (str.length() > 60) {
                     tableObj.setTableName("\"" + str.substring(0, 61) + "\"");
                     table_name_str = tableObj.getTableName();
                     if (table_name_str.startsWith("\"")) {
                        table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                        table_name_str = "\"" + table_name_str + "\"";
                        tableObj.setTableName(table_name_str);
                     }
                  } else {
                     tableObj.setTableName("\"" + str + "\"");
                     table_name_str = tableObj.getTableName();
                     if (table_name_str.startsWith("\"")) {
                        table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                        table_name_str = "\"" + table_name_str + "\"";
                        tableObj.setTableName(table_name_str);
                     }
                  }
               } else if (str.length() > 60) {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + snowflakeColumnName + "_S" + "\"");
                  table_name_str = tableObj.getTableName();
                  if (table_name_str.startsWith("\"")) {
                     table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                     table_name_str = "\"" + table_name_str + "\"";
                     tableObj.setTableName(table_name_str);
                  }
               } else {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + snowflakeColumnName + "_SEQ" + "\"");
                  table_name_str = tableObj.getTableName();
                  if (table_name_str.startsWith("\"")) {
                     table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                     table_name_str = "\"" + table_name_str + "\"";
                     tableObj.setTableName(table_name_str);
                  }
               }
            } else {
               tableObj.setTableName(snowflakeColumnName + "_SEQ");
               str = tableObj.getTableName();
               if (str.startsWith("\"")) {
                  str = StringFunctions.replaceAll("", "\"", str);
                  str = "\"" + str + "\"";
                  tableObj.setTableName(str);
               }
            }
         }

         createSequenceObj.setSchemaName(tableObj);
         if (this.identity.trim().equalsIgnoreCase("IDENTITY")) {
            createSequenceObj.setStart("START");
            createSequenceObj.setStartValue("1");
            createSequenceObj.setIncrementString("INCREMENT ");
            createSequenceObj.setIncrementValue("1");
         } else {
            str = this.identity.trim().substring(8).trim();
            str = str.substring(1, str.length() - 1);
            StringTokenizer st = new StringTokenizer(str, ",");
            str2 = st.nextToken();
            createSequenceObj.setStart("START");
            createSequenceObj.setStartValue(str2);
            if (Integer.parseInt(str2) == 0) {
               createSequenceObj.setMinValueOrNoMinValue("MINVALUE 0");
            }

            if (st.countTokens() > 0) {
               table_name_str = st.nextToken();
               createSequenceObj.setIncrementString("INCREMENT ");
               createSequenceObj.setIncrementValue(table_name_str);
            } else {
               createSequenceObj.setIncrementString("INCREMENT ");
               createSequenceObj.setIncrementValue("1");
            }
         }

         if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n;" + "\n/\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
         } else {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
         }

         this.setCreateSequenceString("CREATE " + createSequenceObj.toString());
         this.setIdentity((String)null);
      }

      if (this.getConstraintClause() != null) {
         boolean primaryOrUniqueClauseEncountered = false;
         Vector changeConstraintVector = this.getConstraintClause();
         if (changeConstraintVector != null) {
            Vector defaultConstraintVector = new Vector();

            ConstraintClause toSnowflakeConstraintClause;
            int index;
            for(index = 0; index < changeConstraintVector.size(); ++index) {
               toSnowflakeConstraintClause = (ConstraintClause)changeConstraintVector.get(index);
               if (toSnowflakeConstraintClause != null && toSnowflakeConstraintClause.getConstraintType() instanceof DefaultConstraintClause) {
                  defaultConstraintVector.add(toSnowflakeConstraintClause);
                  changeConstraintVector.remove(index);
               }
            }

            if (defaultConstraintVector.size() > 0) {
               for(index = 0; index < defaultConstraintVector.size(); ++index) {
                  if (index < changeConstraintVector.size()) {
                     changeConstraintVector.add(index, defaultConstraintVector.get(index));
                  } else {
                     changeConstraintVector.add(defaultConstraintVector.get(index));
                  }
               }
            }

            for(index = 0; index < changeConstraintVector.size(); ++index) {
               toSnowflakeConstraintClause = (ConstraintClause)changeConstraintVector.get(index);
               toSnowflakeConstraintClause.setTableNameFromCQS(this.tableNameFromCQS);
               toSnowflakeConstraintClause.setColumnNameForSequence(this.columnName);
               if (toSnowflakeConstraintClause != null) {
                  if (!(toSnowflakeConstraintClause.getConstraintType() instanceof PrimaryOrUniqueConstraintClause)) {
                     if (toSnowflakeConstraintClause.getConstraintType() instanceof DefaultConstraintClause) {
                        DefaultConstraintClause defaultConstraint = (DefaultConstraintClause)toSnowflakeConstraintClause.getConstraintType();
                        if (defaultConstraint.getDefaultValue() != null && defaultConstraint.getDefaultValue().equalsIgnoreCase("NULL") && SwisSQLOptions.fromSybase && this.nullStatus != null && this.nullStatus.equalsIgnoreCase("NOT NULL")) {
                           this.nullStatus = "NULL";
                        }

                        if (this.columnName != null && defaultConstraint.getConstraintName() != null) {
                           toSnowflakeConstraintClause.setConstraint((String)null);
                           toSnowflakeConstraintClause.setConstraintName((String)null);
                           toSnowflakeConstraintClause.toSnowflakeString();
                           this.constraintNullVector.add(toSnowflakeConstraintClause);
                        } else {
                           toSnowflakeConstraintClause.toSnowflakeString();
                           this.constraintNullVector.add(toSnowflakeConstraintClause);
                        }
                     } else if (toSnowflakeConstraintClause.getConstraintType() instanceof CheckConstraintClause) {
                        toSnowflakeConstraintClause.toSnowflakeString();
                        this.constraintNullVector.add(toSnowflakeConstraintClause);
                     } else if (toSnowflakeConstraintClause.getConstraintType() instanceof ForeignConstraintClause) {
                        toSnowflakeConstraintClause.toSnowflakeString();
                        this.constraintNullVector.add(toSnowflakeConstraintClause);
                     } else if (toSnowflakeConstraintClause.getNotNull() != null) {
                        notNullAddedForPrimaryorUniqueConstraint = false;
                        toSnowflakeConstraintClause.toSnowflakeString();
                        if (!primaryOrUniqueClauseEncountered) {
                           this.constraintNullVector.add(toSnowflakeConstraintClause);
                        }
                     } else {
                        toSnowflakeConstraintClause.toSnowflakeString();
                        this.constraintNullVector.add(toSnowflakeConstraintClause);
                     }
                  } else {
                     primaryOrUniqueClauseEncountered = true;
                     toSnowflakeConstraintClause.setColumnName(this.getColumnName());
                     PrimaryOrUniqueConstraintClause primaryConstraintClause = (PrimaryOrUniqueConstraintClause)toSnowflakeConstraintClause.getConstraintType();
                     boolean ccToBeAdded = false;
                     if (primaryConstraintClause.getConstraintColumnNames() != null && this.columnName != null) {
                        this.constraintNullVector.add(",\n\t");
                        CreateColumn cc = new CreateColumn();
                        if (this.getConstraintClause() != null) {
                           Vector primaryConstraintVector = new Vector();

                           for(int index = 0; index < this.getConstraintClause().size(); ++index) {
                              if (this.getConstraintClause().get(index) instanceof PrimaryOrUniqueConstraintClause) {
                                 primaryConstraintVector.add(this.getConstraintClause().get(index));
                                 cc.setNullStatus((String)null);
                                 cc.setDefaultValue((String)null);
                                 cc.setDefault((String)null);
                                 cc.toSnowflakeString();
                                 NotNull notnullObj = new NotNull();
                                 notnullObj.setNullStatus((String)null);
                                 notnullObj.setIdentity((String)null);
                                 cc.setNotNull(notnullObj);
                                 ccToBeAdded = true;
                              }
                           }

                           cc.setConstraintClause(primaryConstraintVector);
                        }

                        if (ccToBeAdded) {
                           this.constraintNullVector.add(cc);
                        }
                     }

                     toSnowflakeConstraintClause.toSnowflakeString();
                     this.constraintNullVector.add(toSnowflakeConstraintClause);
                  }
               }
            }
         }
      }

      if (this.getComputedColumnExpression() != null) {
         this.setComputedColumnExpression(this.getComputedColumnExpression().toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (this.getNullStatus() != null) {
         this.constraintNullVector.add(this.getNullStatus());
      }

      if (this.getDatatype() != null) {
         if (!this.mapDatatype(this, this.datatypeMapping)) {
            Datatype toSnowflakeDatatype = this.getDatatype();
            toSnowflakeDatatype.toSnowflakeString();
            if (toSnowflakeDatatype instanceof CharacterClass) {
               this.enumValuesConvertedToCheckConstraints(toSnowflakeDatatype, this.constraintNullVector);
            }
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() > 0) {
         if (!this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
            if (userdefinedDatatypes.containsKey(this.userDefinedDatatype.toLowerCase().trim())) {
               this.setUserDefinedDatatype((String)userdefinedDatatypes.get(this.userDefinedDatatype.toLowerCase().trim()));
            } else if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
               this.setUserDefinedDatatype("CHAR(36)");
            }
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype) && this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
         this.setUserDefinedDatatype("CHAR(36)");
      }

      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("CURRENT_TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("USER")) {
            if (this.defaultValue.startsWith("0x") || this.defaultValue.startsWith("0X")) {
               this.setDefaultValue("'" + this.defaultValue.substring(1, this.defaultValue.length()) + "'");
            }
         } else {
            this.setDefaultValue("CURRENT_USER");
         }
      } else if (this.getDefaultFunction() != null) {
         this.setDefaultFunction(this.functionCall.toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      } else if (this.getDefaultExpression() != null) {
         ArrayList newExpList = new ArrayList();

         for(int i = 0; i < this.defaultExpList.size(); ++i) {
            if (this.defaultExpList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.defaultExpList.get(i);
               newExpList.add(sc.toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            } else {
               newExpList.add(this.defaultExpList.get(i));
            }
         }

         this.setDefaultExpression(newExpList);
      } else if (this.getDefault() != null) {
         String def = this.getDefault();
         if (def.equalsIgnoreCase("WITH DEFAULT ")) {
            this.setDefault("DEFAULT ");
         }
      }

      if (this.autoIncrement != null) {
         createSequenceObj = new CreateSequenceStatement();
         tableObj = new TableObject();
         createSequenceObj.setSequence("SEQUENCE");
         snowflakeColumnName = this.columnName;
         if (!snowflakeColumnName.startsWith("[") && !snowflakeColumnName.startsWith("\"")) {
            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + snowflakeColumnName + "_SEQ";
               str1 = this.tableNameFromCQS + snowflakeColumnName;
               str2 = this.tableNameFromCQS;
               if (str.length() > 63) {
                  if (str1.length() > 59) {
                     str = str1.substring(0, 60) + "_SEQ";
                  } else if (str2.length() > 59) {
                     str = str2.substring(0, 60) + "_SEQ";
                  }

                  tableObj.setTableName(str);
               } else {
                  tableObj.setTableName(this.tableNameFromCQS + "_" + snowflakeColumnName + "_SEQ");
               }
            } else {
               tableObj.setTableName(this.columnName + "_SEQ");
            }
         } else {
            snowflakeColumnName = "\"" + snowflakeColumnName.substring(1);
            if (snowflakeColumnName.endsWith("]") || snowflakeColumnName.endsWith("\"")) {
               snowflakeColumnName = snowflakeColumnName.substring(0, snowflakeColumnName.length() - 1) + "\"";
            }

            if (this.tableNameFromCQS != null) {
               str = this.tableNameFromCQS + snowflakeColumnName.substring(1, snowflakeColumnName.length() - 1) + "_SEQ";
               str1 = this.tableNameFromCQS + snowflakeColumnName.substring(1, snowflakeColumnName.length() - 1);
               str2 = this.tableNameFromCQS;
               if (str.length() > 63) {
                  if (str1.length() > 59) {
                     str = str1.substring(0, 60) + "_SEQ";
                  } else if (str2.length() > 59) {
                     str = str2.substring(0, 60) + "_SEQ";
                  }

                  if (str.length() > 60) {
                     tableObj.setTableName("\"" + str.substring(0, 61) + "\"");
                  } else {
                     tableObj.setTableName("\"" + str + "\"");
                  }
               } else if (str.length() > 60) {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + snowflakeColumnName + "_S" + "\"");
               } else {
                  tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + snowflakeColumnName + "_SEQ" + "\"");
               }
            } else {
               tableObj.setTableName(snowflakeColumnName + "_SEQ");
            }
         }

         createSequenceObj.setSchemaName(tableObj);
         createSequenceObj.setStart("START");
         createSequenceObj.setStartValue("1");
         createSequenceObj.setIncrementString("INCREMENT BY");
         createSequenceObj.setIncrementValue("1");
         if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
         } else {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
         }

         this.setCreateSequenceString("CREATE " + createSequenceObj.toString());
         this.setAutoIncrement((String)null);
      }

      if (this.getCastDatatype() != null) {
         this.getCastDatatype().toSnowflakeString();
         this.setDefaultValue(this.getDefaultValue() + "::" + this.getCastDatatype());
      }

   }

   public void toANSIString() throws ConvertException {
      this.constraintNullVector = new Vector();
      this.setIdentity((String)null);
      this.setDatapageStorageLevel((String)null);
      this.setAutoIncrement((String)null);
      this.setCollate((String)null);
      this.setCollationName((String)null);
      this.generated = null;
      this.always = null;
      this.asForIdentity = null;
      this.byForAlways = null;
      this.defaultForIdentity = null;
      this.asForIdentity = null;
      if (this.columnName != null) {
         if (this.columnName.startsWith("[") && this.columnName.endsWith("]") || this.columnName.startsWith("`") && this.columnName.endsWith("`")) {
            this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
            if (this.columnName.indexOf(32) != -1) {
               this.columnName = "\"" + this.columnName + "\"";
            }
         } else {
            this.columnName = "\"" + this.columnName + "\"";
         }
      }

      int size;
      if (this.getConstraintClause() != null) {
         Vector changeConstraintVector = this.getConstraintClause();
         if (changeConstraintVector != null) {
            for(size = 0; size < changeConstraintVector.size(); ++size) {
               ConstraintClause toANSISQLConstraintClause = (ConstraintClause)changeConstraintVector.get(size);
               if (toANSISQLConstraintClause != null) {
                  toANSISQLConstraintClause.setColumnName(this.getColumnName());
                  toANSISQLConstraintClause.toANSIString();
                  this.constraintNullVector.add(toANSISQLConstraintClause);
               }
            }
         }
      }

      if (this.getComputedColumnExpression() != null) {
         this.setComputedColumnAS("AS");
         this.setComputedColumnExpression(this.getComputedColumnExpression().toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (this.getNullStatus() != null) {
         this.constraintNullVector.add(this.getNullStatus());
      }

      if (this.getDatatype() != null && !this.mapDatatype(this, this.datatypeMapping)) {
         Datatype toANSISQLDatatype = this.getDatatype();
         toANSISQLDatatype.toANSIString();
         if (toANSISQLDatatype instanceof CharacterClass) {
            this.enumValuesConvertedToCheckConstraints(toANSISQLDatatype, this.constraintNullVector);
         }
      }

      if (this.getDefaultExpression() != null) {
         new ArrayList();
         size = this.defaultExpList.size();
         if (size == 1) {
            if (this.defaultExpList.get(0) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.defaultExpList.get(0);
               Vector expList = sc.getColumnExpression();
               if (expList.size() == 1) {
                  Object obj = expList.get(0);
                  if (obj instanceof String) {
                     this.setDefaultValue((String)obj);
                  } else if (obj instanceof TableColumn) {
                     TableColumn tc = (TableColumn)obj;
                     String colName = tc.getColumnName();
                     if (colName != null) {
                        this.setDefaultValue(colName);
                     }
                  }
               }
            } else {
               ArrayList expWithoutSc = this.getDefaultExpression();
               if (expWithoutSc.size() == 1 && expWithoutSc.get(0) instanceof String) {
                  this.setDefaultValue((String)expWithoutSc.get(0));
               }
            }
         }
      }

      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("CURRENT_TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("USER")) {
            this.setDefaultValue("CURRENT_USER");
         }
      } else if (this.getDefaultFunction() != null) {
         this.setDefaultFunction(this.functionCall.toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      } else if (this.getDefault() != null) {
         this.setDefaultValue("NULL");
         if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
            this.setDefault("DEFAULT ");
         }
      }

      if (this.getCastDatatype() != null) {
         this.getCastDatatype().toANSIString();
         this.createCastFunction();
      }

   }

   public void toInformixString() throws ConvertException {
      this.constraintNullVector = new Vector();
      this.setIdentity((String)null);
      this.setAutoIncrement((String)null);
      this.setDatapageStorageLevel((String)null);
      this.setCollate((String)null);
      this.setCollationName((String)null);
      this.generated = null;
      this.always = null;
      this.asForIdentity = null;
      this.byForAlways = null;
      this.defaultForIdentity = null;
      this.asForIdentity = null;
      if (this.columnName != null && (this.columnName.startsWith("[") && this.columnName.endsWith("]") || this.columnName.startsWith("`") && this.columnName.endsWith("`"))) {
         this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
         if (this.columnName.indexOf(32) != -1) {
            this.columnName = "\"" + this.columnName + "\"";
         }
      }

      if (this.getNullStatus() != null) {
         if (this.getNullStatus().trim().equalsIgnoreCase("NULL")) {
            this.setNullStatus((String)null);
         } else {
            this.constraintNullVector.add(this.getNullStatus());
         }
      }

      int i;
      if (this.getConstraintClause() != null) {
         Vector constraintVector = this.getConstraintClause();
         if (constraintVector != null) {
            for(i = 0; i < constraintVector.size(); ++i) {
               ConstraintClause toInformixConstraintClause = (ConstraintClause)constraintVector.get(i);
               if (toInformixConstraintClause != null) {
                  ConstraintType toInformixConstraintType = toInformixConstraintClause.getConstraintType();
                  toInformixConstraintClause.setColumnName(this.getColumnName());
                  toInformixConstraintClause.toInformixString();
                  if (toInformixConstraintType instanceof DefaultConstraintClause) {
                     this.constraintNullVector.insertElementAt(toInformixConstraintClause, 0);
                  } else {
                     this.constraintNullVector.add(toInformixConstraintClause);
                  }
               }
            }
         }
      }

      if (this.getDatatype() != null && !this.mapDatatype(this, this.datatypeMapping)) {
         Datatype toInformixDatatype = this.getDatatype();
         toInformixDatatype.toInformixString();
         if (toInformixDatatype instanceof CharacterClass) {
            this.enumValuesConvertedToCheckConstraints(toInformixDatatype, this.constraintNullVector);
         }
      }

      if (this.getComputedColumnExpression() != null) {
         this.setComputedColumnExpression(this.getComputedColumnExpression().toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (this.getDefaultValue() != null) {
         this.setDefaultOpenBrace((String)null);
         this.setDefaultClosedBrace((String)null);
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("TODAY");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("TODAY");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("TODAY");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("TODAY");
         } else if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
            if (this.defaultValue.indexOf("'") != -1) {
               this.setDefaultValue(this.defaultValue.replace('\'', '"'));
            }
         } else {
            this.setDefaultValue("USER");
         }
      } else if (this.getDefaultFunction() != null) {
         this.setDefaultFunction(this.functionCall.toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      } else if (this.getDefaultExpression() != null) {
         ArrayList newExpList = new ArrayList();

         for(i = 0; i < this.defaultExpList.size(); ++i) {
            if (this.defaultExpList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.defaultExpList.get(i);
               newExpList.add(sc.toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            } else {
               newExpList.add(this.defaultExpList.get(i));
            }
         }

         this.setDefaultExpression(newExpList);
      } else if (this.getDefault() != null) {
         this.setDefaultValue("NULL");
         if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
            this.setDefault("DEFAULT ");
         }
      }

   }

   public void toTimesTenString() throws ConvertException {
      this.constraintNullVector = new Vector();
      this.generated = null;
      this.always = null;
      this.byForAlways = null;
      this.defaultForIdentity = null;
      this.setDatapageStorageLevel((String)null);
      if (this.columnName != null && (this.columnName.startsWith("[") && this.columnName.endsWith("]") || this.columnName.startsWith("`") && this.columnName.endsWith("`"))) {
         this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
         if (this.columnName.indexOf(32) != -1) {
            this.columnName = "\"" + this.columnName + "\"";
         }
      }

      boolean isPKColumn = false;
      int i;
      if (this.getConstraintClause() != null) {
         Vector changeConstraintVector = this.getConstraintClause();
         if (changeConstraintVector != null) {
            for(i = 0; i < changeConstraintVector.size(); ++i) {
               ConstraintClause toTimesTenConstraintClause = (ConstraintClause)changeConstraintVector.get(i);
               if (toTimesTenConstraintClause != null) {
                  ConstraintType toTimesTenConstraintType = toTimesTenConstraintClause.getConstraintType();
                  toTimesTenConstraintClause.setColumnName(this.getColumnName());
                  toTimesTenConstraintClause.toTimesTenString();
                  if (toTimesTenConstraintType instanceof DefaultConstraintClause) {
                     if (CreateQueryStatement.commentWhenConstraintNameTruncated.trim().length() > 0 && CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("DEFAULT Constraint") == -1) {
                        CreateQueryStatement.commentWhenConstraintNameTruncated = CreateQueryStatement.commentWhenConstraintNameTruncated + "\n/* SwisSQL Message : DEFAULT Constraint is not supported in TimesTen 5.1.21 */";
                     } else if (CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("DEFAULT Constraint") == -1) {
                        CreateQueryStatement.commentWhenConstraintNameTruncated = "/* SwisSQL Message : DEFAULT Constraint is not supported in TimesTen 5.1.21 */";
                     }
                  } else {
                     if (toTimesTenConstraintClause.getAutoIncrement() != null) {
                        toTimesTenConstraintClause.setAutoIncrement((String)null);
                     }

                     toTimesTenConstraintClause.setColumnName(this.getColumnName());
                     if (toTimesTenConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                        PrimaryOrUniqueConstraintClause tempPrimaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toTimesTenConstraintType;
                        if (toTimesTenConstraintClause.getConstraint() != null) {
                           toTimesTenConstraintClause.setConstraint((String)null);
                           toTimesTenConstraintClause.setConstraintName((String)null);
                        }

                        if (tempPrimaryOrUniqueConstraintClause.getConstraintName().equalsIgnoreCase("PRIMARY KEY")) {
                           isPKColumn = true;
                        }

                        toTimesTenConstraintClause.toTimesTenString();
                        this.constraintNullVector.add(toTimesTenConstraintClause);
                     } else {
                        toTimesTenConstraintClause.toTimesTenString();
                        this.constraintNullVector.add(toTimesTenConstraintClause);
                     }
                  }
               }
            }
         }
      }

      if (this.onDefault != null && this.onDefault.trim().equalsIgnoreCase("DEFAULT") && this.getDefaultValue() != null && this.getDefaultValue().trim().equalsIgnoreCase("null") && this.notNull == null) {
         NotNull newNotNull = new NotNull();
         newNotNull.setNullStatus("NULL");
         this.setNotNull(newNotNull);
      }

      if (this.getComputedColumnExpression() != null) {
         this.setComputedColumnExpression(this.getComputedColumnExpression().toTimesTenSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (!this.isAlterStatement) {
         if (this.getNullStatus() != null) {
            this.constraintNullVector.add(this.getNullStatus());
         } else if (!isPKColumn && this.getColumnName() != null && SwisSQLOptions.fromSybase && SwisSQLOptions.sybaseNotNullConstraint) {
            this.constraintNullVector.add("NOT NULL");
         }
      }

      Datatype type;
      if (this.getDatatype() != null) {
         if (!this.mapDatatype(this, this.datatypeMapping)) {
            type = this.getDatatype();
            type.toTimesTenString();
            type.setDatatypeName(type.getDatatypeName().toUpperCase());
            if (type instanceof CharacterClass) {
               this.enumValuesConvertedToCheckConstraints(type, this.constraintNullVector);
            }
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() > 0) {
         if (!this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
            if (userdefinedDatatypes.containsKey(this.userDefinedDatatype.toLowerCase().trim())) {
               String baseType = (String)userdefinedDatatypes.get(this.userDefinedDatatype.toLowerCase().trim());
               Datatype ttType = SwisSQLUtils.constructDatatype(baseType);
               if (ttType != null) {
                  ttType.toTimesTenString();
                  this.setUserDefinedDatatype(ttType.toString());
               } else {
                  this.setUserDefinedDatatype(baseType);
               }
            } else if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
            }
         }
      } else if (this.userDefinedDatatype != null && userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype) && this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
      }

      if (this.getDefaultFunction() != null || this.getDefaultValue() != null || this.getDefault() != null) {
         if (CreateQueryStatement.commentWhenConstraintNameTruncated.trim().length() > 0 && CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("DEFAULT Constraint") == -1) {
            CreateQueryStatement.commentWhenConstraintNameTruncated = CreateQueryStatement.commentWhenConstraintNameTruncated + "\n/* SwisSQL Message : DEFAULT Constraint is not supported in TimesTen 5.1.21 */";
         } else if (CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("DEFAULT Constraint") == -1) {
            CreateQueryStatement.commentWhenConstraintNameTruncated = "/* SwisSQL Message : DEFAULT Constraint is not supported in TimesTen 5.1.21 */";
         }
      }

      if (this.getDefaultValue() != null) {
         this.setDefault((String)null);
         this.setDefaultValue((String)null);
         this.setDefaultClosedBrace((String)null);
         this.setDefaultOpenBrace((String)null);
      } else if (this.getDefaultFunction() != null) {
         this.setDefaultFunction((FunctionCalls)null);
         this.setDefault((String)null);
      } else if (this.getDefaultExpression() != null) {
         ArrayList newExpList = new ArrayList();

         for(i = 0; i < this.defaultExpList.size(); ++i) {
            if (this.defaultExpList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.defaultExpList.get(i);
               newExpList.add(sc.toTimesTenSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            } else {
               newExpList.add(this.defaultExpList.get(i));
            }
         }

         this.setDefaultExpression(newExpList);
      } else if (this.getDefault() != null) {
         this.setDefault((String)null);
      }

      if (this.identity != null) {
         type = this.getDatatype();
         if (type instanceof NumericClass) {
            NumericClass nc = (NumericClass)type;
            nc.setDatatypeName("INT");
            nc.setSize((String)null);
            nc.setPrecision((String)null);
            nc.setScale((String)null);
            nc.setOpenBrace((String)null);
            nc.setClosedBrace((String)null);
         }

         CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
         TableObject tableObj = new TableObject();
         createSequenceObj.setSequence("SEQUENCE");
         String timesTenColumnName = this.columnName;
         this.setTableNameAfterTruncation(timesTenColumnName, tableObj);
         createSequenceObj.setSchemaName(tableObj);
         if (!this.identity.trim().equalsIgnoreCase("IDENTITY")) {
            String tempIdentity = this.identity.trim().substring(8).trim();
            tempIdentity = tempIdentity.substring(1, tempIdentity.length() - 1);
            StringTokenizer st = new StringTokenizer(tempIdentity, ",");
            String token1 = st.nextToken();
            createSequenceObj.setMinValueOrNoMinValue("MINVALUE " + token1);
            if (st.countTokens() > 0) {
               String token2 = st.nextToken();
               createSequenceObj.setIncrementString("INCREMENT BY");
               createSequenceObj.setIncrementValue(token2);
            } else {
               createSequenceObj.setIncrementString("INCREMENT BY");
               createSequenceObj.setIncrementValue("1");
            }
         }

         if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + ";\n\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
         } else {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + ";\n\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
         }

         this.setIdentity((String)null);
      }

      if (this.autoIncrement != null) {
         this.getDatatype().setDatatypeName("INT");
         this.getDatatype().setSize((String)null);
         this.getDatatype().setOpenBrace((String)null);
         this.getDatatype().setClosedBrace((String)null);
         CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
         TableObject tableObj = new TableObject();
         createSequenceObj.setSequence("SEQUENCE");
         String timesTenColumnName = this.columnName;
         this.setTableNameAfterTruncation(timesTenColumnName, tableObj);
         createSequenceObj.setSchemaName(tableObj);
         if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + ";\n\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
         } else {
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + ";\n\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
         }

         this.autoIncrement = null;
      }

      this.asForIdentity = null;
   }

   public void toNetezzaString() throws ConvertException {
      this.constraintNullVector = new Vector();
      this.setIdentity((String)null);
      this.setDatapageStorageLevel((String)null);
      this.setAutoIncrement((String)null);
      this.setCollate((String)null);
      this.setCollationName((String)null);
      this.generated = null;
      this.always = null;
      this.asForIdentity = null;
      this.byForAlways = null;
      this.defaultForIdentity = null;
      this.asForIdentity = null;
      if (this.columnName != null) {
         if (this.columnName.startsWith("[") && this.columnName.endsWith("]") || this.columnName.startsWith("`") && this.columnName.endsWith("`")) {
            this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
            if (this.columnName.indexOf(32) != -1) {
               this.columnName = "\"" + this.columnName + "\"";
            }
         }

         this.columnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.columnName, SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11);
      }

      int i;
      if (this.getConstraintClause() != null) {
         Vector changeConstraintVector = this.getConstraintClause();
         if (changeConstraintVector != null) {
            for(i = 0; i < changeConstraintVector.size(); ++i) {
               ConstraintClause toNetezzaSQLConstraintClause = (ConstraintClause)changeConstraintVector.get(i);
               if (toNetezzaSQLConstraintClause != null) {
                  toNetezzaSQLConstraintClause.setColumnName(this.getColumnName());
                  toNetezzaSQLConstraintClause.toNetezzaString();
                  this.constraintNullVector.add(toNetezzaSQLConstraintClause);
               }
            }
         }
      }

      if (this.getComputedColumnExpression() != null) {
         this.setComputedColumnAS("AS");
         this.setComputedColumnExpression(this.getComputedColumnExpression().toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (this.getNullStatus() != null) {
         this.constraintNullVector.add(this.getNullStatus());
      }

      if (this.getDatatype() != null && !this.mapDatatype(this, this.datatypeMapping)) {
         Datatype toNetezzaSQLDatatype = this.getDatatype();
         toNetezzaSQLDatatype.toNetezzaString();
         if (toNetezzaSQLDatatype instanceof CharacterClass) {
            this.enumValuesConvertedToCheckConstraints(toNetezzaSQLDatatype, this.constraintNullVector);
         }
      }

      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("CURRENT_TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("USER")) {
            this.setDefaultValue("CURRENT_USER");
         }
      } else if (this.getDefaultFunction() != null) {
         this.setDefaultFunction(this.functionCall.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      } else if (this.getDefaultExpression() != null) {
         ArrayList newExpList = new ArrayList();

         for(i = 0; i < this.defaultExpList.size(); ++i) {
            if (this.defaultExpList.get(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.defaultExpList.get(i);
               newExpList.add(sc.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            } else {
               newExpList.add(this.defaultExpList.get(i));
            }
         }

         this.setDefaultExpression(newExpList);
      } else if (this.getDefault() != null) {
         this.setDefaultValue("NULL");
         if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
            this.setDefault("DEFAULT ");
         }
      }

      if (this.getCastDatatype() != null) {
         this.getCastDatatype().toNetezzaString();
         this.createCastFunction();
      }

   }

   public void toTeradataString() throws ConvertException {
      this.constraintNullVector = new Vector();
      this.setIdentity((String)null);
      this.setDatapageStorageLevel((String)null);
      this.setAutoIncrement((String)null);
      this.setCollate((String)null);
      this.setCollationName((String)null);
      this.generated = null;
      this.always = null;
      this.asForIdentity = null;
      this.byForAlways = null;
      this.defaultForIdentity = null;
      this.asForIdentity = null;
      if (this.columnName != null) {
         if (this.columnName.startsWith("[") && this.columnName.endsWith("]") || this.columnName.startsWith("`") && this.columnName.endsWith("`")) {
            this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
            if (this.columnName.indexOf(32) != -1) {
               this.columnName = "\"" + this.columnName + "\"";
            }
         } else {
            this.columnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.columnName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
         }
      }

      int size;
      if (this.getConstraintClause() != null) {
         Vector changeConstraintVector = this.getConstraintClause();
         if (changeConstraintVector != null) {
            for(size = 0; size < changeConstraintVector.size(); ++size) {
               ConstraintClause toTeradataSQLConstraintClause = (ConstraintClause)changeConstraintVector.get(size);
               if (toTeradataSQLConstraintClause != null) {
                  toTeradataSQLConstraintClause.setColumnName(this.getColumnName());
                  toTeradataSQLConstraintClause.toTeradataString();
                  this.constraintNullVector.add(toTeradataSQLConstraintClause);
               }
            }
         }
      }

      if (this.getComputedColumnExpression() != null) {
         this.setComputedColumnAS("AS");
         this.setComputedColumnExpression(this.getComputedColumnExpression().toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      }

      if (this.getNullStatus() != null) {
         this.constraintNullVector.add(this.getNullStatus());
      }

      if (this.getDatatype() != null && !this.mapDatatype(this, this.datatypeMapping)) {
         Datatype toTeradataSQLDatatype = this.getDatatype();
         toTeradataSQLDatatype.toTeradataString();
         if (toTeradataSQLDatatype instanceof CharacterClass) {
            this.enumValuesConvertedToCheckConstraints(toTeradataSQLDatatype, this.constraintNullVector);
         }
      }

      if (this.getDefaultExpression() != null) {
         new ArrayList();
         size = this.defaultExpList.size();
         if (size == 1) {
            if (this.defaultExpList.get(0) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.defaultExpList.get(0);
               Vector expList = sc.getColumnExpression();
               if (expList.size() == 1) {
                  Object obj = expList.get(0);
                  if (obj instanceof String) {
                     this.setDefaultValue((String)obj);
                  } else if (obj instanceof TableColumn) {
                     TableColumn tc = (TableColumn)obj;
                     String colName = tc.getColumnName();
                     if (colName != null) {
                        this.setDefaultValue(colName);
                     }
                  }
               }
            } else {
               ArrayList expWithoutSc = this.getDefaultExpression();
               if (expWithoutSc.size() == 1 && expWithoutSc.get(0) instanceof String) {
                  this.setDefaultValue((String)expWithoutSc.get(0));
               }
            }
         }
      }

      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("CURRENT_TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("USER")) {
            this.setDefaultValue("CURRENT_USER");
         }
      } else if (this.getDefaultFunction() != null) {
         this.setDefaultFunction(this.functionCall.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
      } else if (this.getDefault() != null) {
         this.setDefaultValue("NULL");
         if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
            this.setDefault("DEFAULT ");
         }
      }

      if (this.getCastDatatype() != null) {
         this.getCastDatatype().toTeradataString();
         this.createCastFunction();
      }

   }

   private void setTableNameAfterTruncation(String sourceColumnName, TableObject tableObj) {
      String str;
      String str1;
      String str2;
      String table_name_str;
      if (!sourceColumnName.startsWith("[") && !sourceColumnName.startsWith("\"")) {
         if (this.tableNameFromCQS != null) {
            str = this.tableNameFromCQS + "_" + sourceColumnName + "_SEQ";
            str1 = this.tableNameFromCQS + "_" + sourceColumnName;
            str2 = this.tableNameFromCQS;
            if (str.length() > 29) {
               if (str1.length() > 25) {
                  str = str1.substring(0, 26) + "_SEQ";
               } else if (str2.length() > 25) {
                  str = str2.substring(0, 26) + "_SEQ";
               }

               tableObj.setTableName(str);
               table_name_str = tableObj.getTableName();
               if (table_name_str.startsWith("\"")) {
                  table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                  table_name_str = "\"" + table_name_str + "\"";
                  tableObj.setTableName(table_name_str);
               }
            } else {
               tableObj.setTableName(this.tableNameFromCQS + "_" + sourceColumnName + "_SEQ");
               table_name_str = tableObj.getTableName();
               if (table_name_str.startsWith("\"")) {
                  table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                  table_name_str = "\"" + table_name_str + "\"";
                  tableObj.setTableName(table_name_str);
               }
            }
         } else {
            tableObj.setTableName(this.columnName + "_SEQ");
            str = tableObj.getTableName();
            if (str.startsWith("\"")) {
               str = StringFunctions.replaceAll("", "\"", str);
               str = "\"" + str + "\"";
               tableObj.setTableName(str);
            }
         }
      } else {
         sourceColumnName = "\"" + sourceColumnName.substring(1);
         if (sourceColumnName.endsWith("]") || sourceColumnName.endsWith("\"")) {
            sourceColumnName = sourceColumnName.substring(0, sourceColumnName.length() - 1) + "\"";
         }

         if (this.tableNameFromCQS != null) {
            str = this.tableNameFromCQS + sourceColumnName.substring(1, sourceColumnName.length() - 1) + "_SEQ";
            str1 = this.tableNameFromCQS + sourceColumnName.substring(1, sourceColumnName.length() - 1);
            str2 = this.tableNameFromCQS;
            if (str.length() > 29) {
               if (str1.length() > 25) {
                  str = str1.substring(0, 26) + "_SEQ";
                  if (str.startsWith("\"")) {
                     str = StringFunctions.replaceAll("", "\"", str);
                     str = "\"" + str + "\"";
                  }
               } else if (str2.length() > 25) {
                  str = str2.substring(0, 26) + "_SEQ";
                  if (str2.startsWith("\"")) {
                     str = StringFunctions.replaceAll("", "\"", str2);
                     str = "\"" + str + "\"";
                  }
               }

               if (str.length() > 27) {
                  tableObj.setTableName("\"" + str.substring(0, 28) + "\"");
                  table_name_str = tableObj.getTableName();
                  if (table_name_str.startsWith("\"")) {
                     table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                     table_name_str = "\"" + table_name_str + "\"";
                     tableObj.setTableName(table_name_str);
                  }
               } else {
                  tableObj.setTableName("\"" + str + "\"");
                  table_name_str = tableObj.getTableName();
                  if (table_name_str.startsWith("\"")) {
                     table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                     table_name_str = "\"" + table_name_str + "\"";
                     tableObj.setTableName(table_name_str);
                  }
               }
            } else if (str.length() > 27) {
               tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + sourceColumnName + "_S" + "\"");
               table_name_str = tableObj.getTableName();
               if (table_name_str.startsWith("\"")) {
                  table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                  table_name_str = "\"" + table_name_str + "\"";
                  tableObj.setTableName(table_name_str);
               }
            } else {
               tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + sourceColumnName + "_SEQ" + "\"");
               table_name_str = tableObj.getTableName();
               if (table_name_str.startsWith("\"")) {
                  table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                  table_name_str = "\"" + table_name_str + "\"";
                  tableObj.setTableName(table_name_str);
               }
            }
         } else {
            tableObj.setTableName(sourceColumnName + "_SEQ");
            str = tableObj.getTableName();
            if (str.startsWith("\"")) {
               str = StringFunctions.replaceAll("", "\"", str);
               str = "\"" + str + "\"";
               tableObj.setTableName(str);
            }
         }
      }

   }

   public CreateColumn copyObjectValues() {
      CreateColumn dupCreateColumn = new CreateColumn();
      Vector orgConstraintVector = this.getConstraintClause();
      if (orgConstraintVector != null) {
         Vector newConstraintVector = new Vector();

         for(int i = 0; i < orgConstraintVector.size(); ++i) {
            ConstraintClause orgConstraintClause = (ConstraintClause)orgConstraintVector.get(i);
            if (orgConstraintClause != null) {
               ConstraintClause newConstraintClause = orgConstraintClause.copyObjectValues();
               newConstraintVector.add(newConstraintClause);
            }
         }

         dupCreateColumn.setConstraintClause(newConstraintVector);
      }

      dupCreateColumn.setColumnName(this.getColumnName());
      dupCreateColumn.setObjectContext(this.context);
      Datatype orgDatatype = this.getDatatype();
      if (orgDatatype != null) {
         Datatype dupDatatype = null;
         if (orgDatatype instanceof NumericClass) {
            NumericClass numericDatatype = (NumericClass)orgDatatype;
            dupDatatype = numericDatatype.copyObjectValues();
         } else if (orgDatatype instanceof CharacterClass) {
            CharacterClass characterDatatype = (CharacterClass)orgDatatype;
            dupDatatype = characterDatatype.copyObjectValues();
         } else if (orgDatatype instanceof DateClass) {
            DateClass dateDatatype = (DateClass)orgDatatype;
            dupDatatype = dateDatatype.copyObjectValues();
         } else if (orgDatatype instanceof BinClass) {
            BinClass binDatatype = (BinClass)orgDatatype;
            dupDatatype = binDatatype.copyObjectValues();
         } else if (orgDatatype instanceof GeometryClass) {
            GeometryClass geometryDatatype = (GeometryClass)orgDatatype;
            dupDatatype = geometryDatatype.copyObjectValues();
         } else if (orgDatatype instanceof QuotedIdentifierDatatype) {
            QuotedIdentifierDatatype quotedDatatype = (QuotedIdentifierDatatype)orgDatatype;
            dupDatatype = quotedDatatype.copyObjectValues();
         }

         dupCreateColumn.setDatatype(dupDatatype);
      }

      dupCreateColumn.setUserDefinedDatatype(this.userDefinedDatatype);
      dupCreateColumn.setDefault(this.getDefault());
      dupCreateColumn.setDefaultOpenBrace(this.defaultOpenBrace);
      dupCreateColumn.setDefaultValue(this.getDefaultValue());
      dupCreateColumn.setDefaultClosedBrace(this.defaultClosedBrace);
      dupCreateColumn.setIdentity(this.getIdentity());
      dupCreateColumn.setNullStatus(this.getNullStatus());
      dupCreateColumn.setDefaultFunction(this.getDefaultFunction());
      dupCreateColumn.setCollate(this.collate);
      dupCreateColumn.setCollationName(this.collationName);
      dupCreateColumn.setGenerated(this.generated);
      dupCreateColumn.setAlways(this.always);
      dupCreateColumn.setByForAlways(this.byForAlways);
      dupCreateColumn.setDefaultForIdentity(this.defaultForIdentity);
      dupCreateColumn.setIdentityAs(this.asForIdentity);
      dupCreateColumn.setAutoIncrement(this.autoIncrement);
      dupCreateColumn.setDatapageStorageLevel(this.datapageStorageLevel);
      dupCreateColumn.setDefaultExpression(this.defaultExpList);
      dupCreateColumn.setCastDatatype(this.castDatatype);
      dupCreateColumn.setComputedColumnExpression(this.computedColumnExpression);
      return dupCreateColumn;
   }

   private boolean mapDatatype(CreateColumn changeCreateColumn, DatatypeMapping mapping) {
      Datatype changeCreateColumnDatatype = changeCreateColumn.getDatatype();
      if (changeCreateColumnDatatype != null) {
         String datatypeName = changeCreateColumnDatatype.getDatatypeName();
         if (datatypeName != null) {
            if (SwisSQLAPI.objectContext != null) {
               String tableName = this.tableNameFromCQS;
               String columnName = changeCreateColumn.getColumnName();
               Object val = null;
               String newDatatypeName;
               if ((datatypeName.equalsIgnoreCase("varchar") || datatypeName.equalsIgnoreCase("nvarchar")) && changeCreateColumn.getDatatype().getSize() != null && changeCreateColumn.getDatatype().getSize().equalsIgnoreCase("max")) {
                  newDatatypeName = datatypeName.trim() + "(" + changeCreateColumn.getDatatype().getSize() + ")";
                  val = SwisSQLAPI.objectContext.getMappedDatatype(tableName, columnName, newDatatypeName);
               } else {
                  val = SwisSQLAPI.objectContext.getMappedDatatype(tableName, columnName, datatypeName);
               }

               if (val != null) {
                  newDatatypeName = (String)val;
                  if (changeCreateColumn.getDatatype().getSize() != null && changeCreateColumn.getDatatype().getSize().equalsIgnoreCase("max") && (!newDatatypeName.startsWith("varchar") || !newDatatypeName.startsWith("nvarchar"))) {
                     changeCreateColumnDatatype.setOpenBrace((String)null);
                     changeCreateColumnDatatype.setClosedBrace((String)null);
                     changeCreateColumnDatatype.setSize((String)null);
                  }

                  if (newDatatypeName.indexOf("(") != -1) {
                     changeCreateColumnDatatype.setDatatypeName(newDatatypeName.substring(0, newDatatypeName.indexOf("(")));
                     changeCreateColumnDatatype.setOpenBrace("(");
                     changeCreateColumnDatatype.setClosedBrace(")");
                     changeCreateColumnDatatype.setSize(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                     if (changeCreateColumnDatatype instanceof QuotedIdentifierDatatype) {
                        ((QuotedIdentifierDatatype)changeCreateColumnDatatype).setPrecision(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                     }
                  } else {
                     changeCreateColumnDatatype.setDatatypeName(newDatatypeName);
                  }

                  return true;
               }
            }

            if (mapping != null) {
               Hashtable tableSpecificMapping = mapping.getTableSpecificDatatypeMapping();
               String newDatatypeName;
               String origDatatypeName;
               NumericClass nC;
               if (tableSpecificMapping != null && this.tableNameFromCQS != null) {
                  boolean contain = tableSpecificMapping.containsKey(this.tableNameFromCQS.toLowerCase());
                  origDatatypeName = null;
                  if (!contain) {
                     origDatatypeName = this.removeDelimiter(this.tableNameFromCQS.toLowerCase());
                     contain = tableSpecificMapping.containsKey(origDatatypeName);
                  }

                  if (contain) {
                     Hashtable column = (Hashtable)tableSpecificMapping.get(this.tableNameFromCQS.toLowerCase());
                     if (column == null) {
                        column = (Hashtable)tableSpecificMapping.get(origDatatypeName);
                     }

                     if (column != null) {
                        newDatatypeName = (String)column.get(changeCreateColumn.getColumnName().toLowerCase());
                        if (newDatatypeName == null) {
                           newDatatypeName = (String)column.get(this.removeDelimiter(changeCreateColumn.getColumnName().toLowerCase()));
                        }

                        if (newDatatypeName != null) {
                           if (newDatatypeName.indexOf("(") != -1) {
                              changeCreateColumnDatatype.setDatatypeName(newDatatypeName.substring(0, newDatatypeName.indexOf("(")));
                              changeCreateColumnDatatype.setOpenBrace("(");
                              changeCreateColumnDatatype.setClosedBrace(")");
                              changeCreateColumnDatatype.setSize(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                              if (changeCreateColumnDatatype instanceof QuotedIdentifierDatatype) {
                                 ((QuotedIdentifierDatatype)changeCreateColumnDatatype).setPrecision(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                              }
                           } else {
                              changeCreateColumnDatatype.setDatatypeName(newDatatypeName);
                              if (changeCreateColumnDatatype.getOpenBrace() != null) {
                                 if (changeCreateColumnDatatype instanceof NumericClass) {
                                    nC = (NumericClass)changeCreateColumnDatatype;
                                    if (nC.getPrecision() != null) {
                                       nC.setPrecision((String)null);
                                       if (nC.getScale() != null) {
                                          nC.setScale((String)null);
                                       }
                                    }
                                 }

                                 changeCreateColumnDatatype.setOpenBrace((String)null);
                                 changeCreateColumnDatatype.setSize((String)null);
                                 changeCreateColumnDatatype.setClosedBrace((String)null);
                              }
                           }

                           return true;
                        }
                     }
                  }
               }

               Hashtable globalMapping = mapping.getGlobalDatatypeMapping();
               if (globalMapping != null) {
                  origDatatypeName = datatypeName;
                  if (changeCreateColumnDatatype instanceof NumericClass) {
                     NumericClass nc = (NumericClass)changeCreateColumnDatatype;
                     if (nc.getPrecision() != null) {
                        datatypeName = datatypeName + "(" + nc.getPrecision();
                        if (nc.getScale() != null) {
                           datatypeName = datatypeName + "," + nc.getScale();
                        }

                        datatypeName = datatypeName + ")";
                     }
                  } else if (changeCreateColumnDatatype.getOpenBrace() != null) {
                     if (changeCreateColumnDatatype instanceof QuotedIdentifierDatatype) {
                        if (changeCreateColumnDatatype.getSize() != null) {
                           datatypeName = datatypeName + "(" + changeCreateColumnDatatype.getSize() + ")";
                        } else if (((QuotedIdentifierDatatype)changeCreateColumnDatatype).getPrecision() != null) {
                           datatypeName = datatypeName + "(" + ((QuotedIdentifierDatatype)changeCreateColumnDatatype).getPrecision() + ")";
                        }
                     } else {
                        datatypeName = datatypeName + "(" + changeCreateColumnDatatype.getSize() + ")";
                     }
                  }

                  CharacterClass cc = null;
                  if (changeCreateColumnDatatype instanceof CharacterClass) {
                     cc = (CharacterClass)changeCreateColumnDatatype;
                     if (cc.getBinary() != null && cc.getBinary().toUpperCase().indexOf("FOR BIT DATA") != -1) {
                        datatypeName = datatypeName + " FOR BIT DATA";
                     }
                  }

                  if (globalMapping.containsKey(datatypeName.toLowerCase())) {
                     newDatatypeName = (String)globalMapping.get(datatypeName.toLowerCase());
                     if (newDatatypeName.indexOf("(") != -1) {
                        changeCreateColumnDatatype.setDatatypeName(newDatatypeName.substring(0, newDatatypeName.indexOf("(")));
                        changeCreateColumnDatatype.setOpenBrace("(");
                        changeCreateColumnDatatype.setClosedBrace(")");
                        changeCreateColumnDatatype.setSize(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                        if (changeCreateColumnDatatype instanceof QuotedIdentifierDatatype) {
                           ((QuotedIdentifierDatatype)changeCreateColumnDatatype).setPrecision(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                        }
                     } else if (datatypeName.indexOf("(") != -1) {
                        changeCreateColumnDatatype.setDatatypeName(newDatatypeName);
                        changeCreateColumnDatatype.setOpenBrace((String)null);
                        changeCreateColumnDatatype.setClosedBrace((String)null);
                        changeCreateColumnDatatype.setSize((String)null);
                        if (changeCreateColumnDatatype instanceof NumericClass) {
                           nC = (NumericClass)changeCreateColumnDatatype;
                           nC.setPrecision((String)null);
                           nC.setScale((String)null);
                        }
                     } else {
                        changeCreateColumnDatatype.setDatatypeName(newDatatypeName);
                     }

                     if (cc != null && cc.getBinary() != null) {
                        cc.setBinary((String)null);
                     }

                     return true;
                  }

                  if (globalMapping.containsKey(origDatatypeName.toLowerCase())) {
                     newDatatypeName = (String)globalMapping.get(origDatatypeName.toLowerCase());
                     if (newDatatypeName.indexOf("(") != -1) {
                        String size = newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")"));
                        changeCreateColumnDatatype.setOpenBrace("(");
                        changeCreateColumnDatatype.setClosedBrace(")");
                        changeCreateColumnDatatype.setSize(size);
                        newDatatypeName = newDatatypeName.substring(0, newDatatypeName.indexOf("("));
                     }

                     changeCreateColumnDatatype.setDatatypeName(newDatatypeName);
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean mapUserDatatype(CreateColumn changeCreateColumn, DatatypeMapping mapping, String datatypeName) {
      if (SwisSQLAPI.objectContext != null) {
         String tableName = this.tableNameFromCQS;
         String columnName = changeCreateColumn.getColumnName();
         Object val = SwisSQLAPI.objectContext.getMappedDatatype(tableName, columnName, datatypeName);
         if (val != null) {
            this.setUserDefinedDatatype((String)val);
            return true;
         }
      }

      if (mapping != null) {
         Hashtable tableSpecificMapping = mapping.getTableSpecificDatatypeMapping();
         String tableNameWithoutDelimiter;
         if (tableSpecificMapping != null && this.tableNameFromCQS != null) {
            boolean contain = tableSpecificMapping.containsKey(this.tableNameFromCQS.toLowerCase());
            tableNameWithoutDelimiter = null;
            if (!contain) {
               tableNameWithoutDelimiter = this.removeDelimiter(this.tableNameFromCQS.toLowerCase());
               contain = tableSpecificMapping.containsKey(tableNameWithoutDelimiter);
            }

            if (contain) {
               Hashtable column = (Hashtable)tableSpecificMapping.get(this.tableNameFromCQS.toLowerCase());
               if (column == null) {
                  column = (Hashtable)tableSpecificMapping.get(tableNameWithoutDelimiter);
               }

               if (column != null) {
                  String newDatatypeName = (String)column.get(changeCreateColumn.getColumnName().toLowerCase());
                  if (newDatatypeName == null) {
                     newDatatypeName = (String)column.get(this.removeDelimiter(changeCreateColumn.getColumnName().toLowerCase()));
                  }

                  if (newDatatypeName != null) {
                     this.setUserDefinedDatatype(newDatatypeName);
                     return true;
                  }
               }
            }
         }

         Hashtable globalMapping = mapping.getGlobalDatatypeMapping();
         if (globalMapping != null && globalMapping.containsKey(datatypeName.toLowerCase())) {
            tableNameWithoutDelimiter = (String)globalMapping.get(datatypeName.toLowerCase());
            this.setUserDefinedDatatype(tableNameWithoutDelimiter);
            return true;
         }
      }

      return false;
   }

   private String removeDelimiter(String name) {
      return (!name.startsWith("[") || !name.endsWith("]")) && (!name.startsWith("\"") || !name.endsWith("\"")) && (!name.startsWith("`") || !name.endsWith("`")) ? name : name.substring(1, name.length() - 1);
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      String str;
      if (this.columnName != null) {
         if (this.context != null) {
            str = this.context.getEquivalent(this.columnName).toString();
            sb.append(str + " ");
         } else {
            sb.append(this.columnName + " ");
         }
      }

      if (this.datatype != null) {
         if (this.datatype.toString().trim().equalsIgnoreCase("BOOLEAN")) {
            if (this.booleanDb2) {
               sb.append("SMALLINT CHECK(" + this.columnName + "=0 or " + this.columnName + "=1) ");
            } else if (this.booleanOracle) {
               if (this.getDefaultValue() != null && this.onDefault != null) {
                  sb.append("NUMBER(1) " + this.onDefault + " " + this.getDefaultValue() + " CHECK(" + this.columnName + "=0 or " + this.columnName + "=1) ");
               } else {
                  sb.append("NUMBER(1) CHECK(" + this.columnName + "=0 or " + this.columnName + "=1) ");
               }
            } else {
               sb.append(this.datatype.toString() + " ");
            }
         } else {
            sb.append(this.datatype.toString() + " ");
         }
      } else if (this.userDefinedDatatype != null) {
         sb.append(this.userDefinedDatatype + " ");
      }

      if (this.computedColumnExpression != null) {
         if (this.computedColumnAS != null) {
            sb.append(this.computedColumnAS + " ");
         }

         sb.append(this.computedColumnExpression.toString() + " ");
      }

      if (this.collate != null) {
         sb.append(this.collate + " ");
      }

      if (this.collationName != null) {
         sb.append(this.collationName + " ");
      }

      if (this.sparseStr != null) {
         sb.append(this.sparseStr + " ");
      }

      if (this.onDefault != null && !this.booleanOracle) {
         sb.append(this.onDefault.toUpperCase() + " ");
      }

      if (this.defaultOpenBrace != null) {
         sb.append(this.defaultOpenBrace + " ");
      }

      int i;
      if (this.defaultValue != null && !this.booleanOracle) {
         sb.append(this.defaultValue + " ");
      } else if (this.getDefaultFunction() != null) {
         this.functionCall.setObjectContext(this.context);
         sb.append(this.functionCall + " ");
      } else if (this.getDefaultExpression() != null) {
         for(i = 0; i < this.defaultExpList.size(); ++i) {
            sb.append(this.defaultExpList.get(i) + " ");
         }
      }

      if (this.defaultClosedBrace != null) {
         sb.append(this.defaultClosedBrace + " ");
      }

      if (this.generated != null) {
         sb.append(" " + this.generated.toUpperCase());
      }

      if (this.always != null) {
         sb.append(" " + this.always.toUpperCase());
      }

      if (this.byForAlways != null) {
         sb.append(" " + this.byForAlways.toUpperCase());
      }

      if (this.defaultForIdentity != null) {
         sb.append(" " + this.defaultForIdentity);
      }

      if (this.asForIdentity != null) {
         sb.append(" " + this.asForIdentity.toUpperCase() + " ");
      }

      if (this.identity != null) {
         sb.append(this.identity + " ");
      }

      ConstraintClause constraintClause;
      if (this.constraintNullVector != null) {
         for(i = 0; i < this.constraintNullVector.size(); ++i) {
            if (this.constraintNullVector.get(i) instanceof String) {
               String str = sb.toString().trim();
               if (((String)this.constraintNullVector.get(i)).trim().toUpperCase().equals("NOT NULL")) {
                  if (str.toUpperCase().indexOf("NOT NULL") == -1) {
                     sb.append(((String)this.constraintNullVector.get(i)).toUpperCase() + " ");
                  }
               } else if (!((String)this.constraintNullVector.get(i)).equalsIgnoreCase("null") && !((String)this.constraintNullVector.get(i)).equalsIgnoreCase("not null")) {
                  sb.append((String)this.constraintNullVector.get(i) + " ");
               } else {
                  sb.append(((String)this.constraintNullVector.get(i)).toUpperCase() + " ");
               }
            } else if (this.constraintNullVector.get(i) instanceof ConstraintClause) {
               constraintClause = (ConstraintClause)this.constraintNullVector.get(i);
               constraintClause.setObjectContext(this.context);
               sb.append(constraintClause.toString());
            } else if (this.constraintNullVector.get(i) instanceof CreateColumn) {
               CreateColumn createCol = (CreateColumn)this.constraintNullVector.get(i);
               createCol.setObjectContext(this.context);
               sb.append(createCol.toString());
            }
         }
      } else if (this.constraintVector != null || this.getNullStatus() != null) {
         if (this.constraintVector != null) {
            for(i = 0; i < this.constraintVector.size(); ++i) {
               constraintClause = (ConstraintClause)this.constraintVector.get(i);
               constraintClause.setObjectContext(this.context);
               sb.append(constraintClause.toString());
            }
         }

         if (this.getNullStatus() != null) {
            sb.append(this.nullStatus.toUpperCase() + " ");
         }
      }

      if (this.autoIncrement != null) {
         sb.append(" " + this.autoIncrement.toUpperCase() + " ");
      }

      if (this.getNullStatus() != null && !this.getNullStatus().trim().toUpperCase().equals("NULL")) {
         str = sb.toString().trim();
         if (str.toUpperCase().indexOf("NOT NULL") == -1) {
            sb.append(this.nullStatus.toUpperCase() + " ");
         }
      }

      if (this.datapageStorageLevel != null) {
         sb.append(this.datapageStorageLevel.toUpperCase() + " ROW");
      }

      return sb.toString();
   }

   public static void addToUserDefinedDataTypes(String type, String basetype) {
      if (type != null && basetype != null) {
         userdefinedDatatypes.put(type, basetype);
         userConfiguredDatatypes.put(type, basetype);
      }

   }

   public static Hashtable getUserDefinedDatatypes() {
      return userdefinedDatatypes;
   }

   public static boolean isTypePresent(String type) {
      return userConfiguredDatatypes.containsKey(type);
   }

   public static void readUserDefinedDatatypes() {
      try {
         FileInputStream fis = new FileInputStream("conf/SQLServerUDDBaseTypeEquivalents.conf");
         InputStreamReader isr = new InputStreamReader(fis);
         BufferedReader br = new BufferedReader(isr);
         new String();
         String functionString = br.readLine();

         while(true) {
            while(functionString != null) {
               if (functionString.trim().equals("")) {
                  functionString = br.readLine();
               } else {
                  int index;
                  StringTokenizer st;
                  String udd;
                  String oracleBaseDatatype;
                  if ((index = functionString.trim().indexOf("/*")) != -1) {
                     if (index > 0) {
                        functionString = functionString.substring(0, functionString.indexOf("/*"));
                        st = new StringTokenizer(functionString, "=");
                        if (st.countTokens() == 2) {
                           udd = st.nextToken().trim().toLowerCase();
                           oracleBaseDatatype = st.nextToken().trim();
                           userdefinedDatatypes.put(udd, oracleBaseDatatype);
                        }
                     }

                     while(functionString != null && functionString.indexOf("*/") == -1) {
                        functionString = br.readLine();
                     }
                  } else {
                     if (functionString.trim().indexOf("--") != -1) {
                        functionString = functionString.substring(0, functionString.indexOf("--"));
                     }

                     st = new StringTokenizer(functionString, "=");
                     if (st.countTokens() == 2) {
                        udd = st.nextToken().trim().toLowerCase();
                        oracleBaseDatatype = st.nextToken().trim();
                        userdefinedDatatypes.put(udd, oracleBaseDatatype);
                     }

                     functionString = br.readLine();
                  }
               }
            }

            br.close();
            isr.close();
            fis.close();
            break;
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }

   }

   private void enumValuesConvertedToCheckConstraints(Datatype datatype, Vector constraintNullVector) {
      ArrayList enumValues = ((CharacterClass)datatype).getEnumValues();
      if (enumValues != null && !enumValues.isEmpty()) {
         WhereExpression checkWE = new WhereExpression();
         WhereColumn lwc = new WhereColumn();
         Vector lwcV = new Vector();
         lwcV.add(this.columnName);
         lwc.setColumnExpression(lwcV);

         for(int i = 0; i < enumValues.size(); ++i) {
            WhereItem wi = new WhereItem();
            wi.setLeftWhereExp(lwc);
            WhereColumn rwc = new WhereColumn();
            Vector rwcV = new Vector();
            rwcV.add(enumValues.get(i));
            rwc.setColumnExpression(rwcV);
            wi.setRightWhereExp(rwc);
            wi.setOperator("=");
            checkWE.addWhereItem(wi);
            if (i != enumValues.size() - 1) {
               checkWE.addOperator("OR");
            }
         }

         Vector constrClaV = this.getConstraintClause();
         boolean isCheckConstrExists = false;
         if (constrClaV != null) {
            for(int i = 0; i < constrClaV.size(); ++i) {
               ConstraintClause constrCla = (ConstraintClause)constrClaV.get(i);
               ConstraintType constrType = constrCla.getConstraintType();
               if (constrType != null && constrType instanceof CheckConstraintClause) {
                  isCheckConstrExists = true;
                  CheckConstraintClause checkConstr = (CheckConstraintClause)constrType;
                  WhereExpression we = checkConstr.getWhereExpression();
                  if (we != null) {
                     we.addOperator("OR");
                     we.addWhereExpression(checkWE);
                  }
               }
            }
         }

         if (!isCheckConstrExists) {
            ConstraintClause constrCla = new ConstraintClause();
            CheckConstraintClause chConstrClause = new CheckConstraintClause();
            chConstrClause.setOpenBrace("(");
            chConstrClause.setClosedBrace(")");
            chConstrClause.setWhereExpression(checkWE);
            chConstrClause.setConstraintName("CHECK");
            constrCla.setConstraintType(chConstrClause);
            constraintNullVector.addElement(constrCla);
         }
      }

      ((CharacterClass)datatype).setEnumValues((ArrayList)null);
   }

   public static void readUserConfiguredDatatypes() {
      try {
         FileInputStream fis = new FileInputStream("conf/MSSQLServerToOracleDatatype.conf");
         InputStreamReader isr = new InputStreamReader(fis);
         BufferedReader br = new BufferedReader(isr);
         new String();
         String functionString = br.readLine();

         while(true) {
            while(functionString != null) {
               if (functionString.trim().equals("")) {
                  functionString = br.readLine();
               } else {
                  int index;
                  StringTokenizer st;
                  String udd;
                  String oracleBaseDatatype;
                  if ((index = functionString.trim().indexOf("/*")) != -1) {
                     if (index > 0) {
                        functionString = functionString.substring(0, functionString.indexOf("/*"));
                        st = new StringTokenizer(functionString, "=");
                        if (st.countTokens() == 2) {
                           udd = st.nextToken().trim().toLowerCase();
                           oracleBaseDatatype = st.nextToken().trim();
                           userConfiguredDatatypes.put(udd.toLowerCase(), oracleBaseDatatype);
                        }
                     }

                     while(functionString != null && functionString.indexOf("*/") == -1) {
                        functionString = br.readLine();
                     }
                  } else {
                     if (functionString.trim().indexOf("--") != -1) {
                        functionString = functionString.substring(0, functionString.indexOf("--"));
                     }

                     st = new StringTokenizer(functionString, "=");
                     if (st.countTokens() == 2) {
                        udd = st.nextToken().trim().toLowerCase();
                        oracleBaseDatatype = st.nextToken().trim();
                        userConfiguredDatatypes.put(udd.toLowerCase(), oracleBaseDatatype);
                     }

                     functionString = br.readLine();
                  }
               }
            }

            br.close();
            isr.close();
            fis.close();
            break;
         }
      } catch (Exception var8) {
      }

   }

   public void createCastFunction() throws ConvertException {
      SelectColumn firstArgInCastFunction = new SelectColumn();
      new SelectColumn();
      Vector functionArguments = new Vector();
      TableColumn tc = new TableColumn();
      FunctionCalls fc = new FunctionCalls();
      Vector vec_firstarg = new Vector();
      new Vector();
      tc.setColumnName("CAST");
      fc.setFunctionName(tc);
      vec_firstarg.addElement(this.getDefaultValue());
      vec_firstarg.addElement(" AS ");
      vec_firstarg.addElement(this.getCastDatatype());
      firstArgInCastFunction.setColumnExpression(vec_firstarg);
      functionArguments.addElement(firstArgInCastFunction);
      fc.setFunctionArguments(functionArguments);
      this.setDefaultFunction(fc);
      this.setDefaultValue((String)null);
   }
}
