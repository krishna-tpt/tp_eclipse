package com.adventnet.swissqlapi.sql.statement.alter;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.create.CheckConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintType;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.ForeignConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.PartitionListAttributes;
import com.adventnet.swissqlapi.sql.statement.create.PrimaryOrUniqueConstraintClause;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class AddClause {
   private UserObjectContext context = null;
   private String with;
   private String add;
   private String checkOrNoCheck;
   private String before;
   private String first;
   private String after;
   private String beforeOrAfterColumnName;
   private boolean beforeOrAfterOrFirstCalled = false;
   private CreateColumn createColumn;
   private Vector createColumnVector;
   private boolean commaIsSet;
   private ArrayList columnArrayList;
   private PartitionListAttributes partition;
   private String partitioning;
   private String key;
   private String using;
   private String hashing;
   private String openBrace;
   private String closeBrace;
   private String overflow;
   private String storage;
   private String physicalStorageAttributes;
   private Vector constraintClauseVector;
   private ConstraintClause constraintClause;
   private String column;
   private String diskAttributes;
   private String physicalCharacteristics;
   private ConstraintClause tableConstraint;
   private String uniqueOrPrimaryOrIndexOrFullText;
   private String uniqueConstraintName;
   private Vector indexColumnVector;
   private DatatypeMapping datatypeMapping;
   private String stmtTableName;

   public void setAdd(String add) {
      this.add = add;
   }

   public void setWith(String with) {
      this.with = with;
   }

   public void setCheckOrNoCheck(String checkOrNoCheck) {
      this.checkOrNoCheck = checkOrNoCheck;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   public void setConstraintClauseVector(Vector constraintClauseVector) {
      this.constraintClauseVector = constraintClauseVector;
   }

   public void setConstraintClause(ConstraintClause constraintClause) {
      this.constraintClause = constraintClause;
   }

   public void setOpenBrace(String openBrace) {
      this.openBrace = openBrace;
   }

   public void setColumn(String column) {
      this.column = column;
   }

   public void setCreateColumnVector(Vector createColumnVector) {
      this.createColumnVector = createColumnVector;
   }

   public void setCommaBooleanValue(boolean commaIsSet) {
      this.commaIsSet = commaIsSet;
   }

   public void setBefore(String before) {
      this.before = before;
   }

   public void setFirst(String first) {
      this.first = first;
   }

   public void setAfter(String after) {
      this.after = after;
   }

   public void setBeforeOrAfterColumnName(String beforeOrAfterColumnName) {
      this.beforeOrAfterColumnName = beforeOrAfterColumnName;
   }

   public void setBeforeOrAfterOrFirstBooleanValue(boolean before) {
      this.beforeOrAfterOrFirstCalled = true;
   }

   public void setClosedBrace(String closeBrace) {
      this.closeBrace = closeBrace;
   }

   public void setPartition(PartitionListAttributes partition) {
      this.partition = partition;
   }

   public void setPartitionKey(String key) {
      this.key = key;
   }

   public void setPartitioning(String partitioning) {
      this.partitioning = partitioning;
   }

   public void setUsing(String using) {
      this.using = using;
   }

   public void setHashing(String hashing) {
      this.hashing = hashing;
   }

   public void setColumnArrayList(ArrayList columnArrayList) {
      this.columnArrayList = columnArrayList;
   }

   public void setOverflow(String overflow) {
      this.overflow = overflow;
   }

   public void setStorage(String storage) {
      this.storage = storage;
   }

   public void setPhysicalStorageAttributes(String physicalStorageAttributes) {
      this.physicalStorageAttributes = physicalStorageAttributes;
   }

   public void setUniqueOrPrimaryOrIndexOrFullText(String uniqueOrPrimaryOrIndexOrFullText) {
      this.uniqueOrPrimaryOrIndexOrFullText = uniqueOrPrimaryOrIndexOrFullText;
   }

   public void setUniqueConstraintName(String uniqueConstraintName) {
      this.uniqueConstraintName = uniqueConstraintName;
   }

   public void setIndexColumnVector(Vector indexColumnVector) {
      this.indexColumnVector = indexColumnVector;
   }

   public void setDiskAttributes(String diskAttributes) {
      this.diskAttributes = diskAttributes;
   }

   public void setPhysicalCharacteristics(String physicalCharacteristics) {
      this.physicalCharacteristics = physicalCharacteristics;
   }

   public void setIndexClause() {
   }

   public void setDatatypeMapping(DatatypeMapping mapping) {
      this.datatypeMapping = mapping;
   }

   public void setStmtTableName(String stmtTableName) {
      this.stmtTableName = stmtTableName;
   }

   public String getAdd() {
      return this.add;
   }

   public String getWith() {
      return this.with;
   }

   public String getCheckOrNoCheck() {
      return this.checkOrNoCheck;
   }

   public String getColumn() {
      return this.column;
   }

   public Vector getCreateColumnVector() {
      return this.createColumnVector;
   }

   public boolean getCommaBooleanValue() {
      return this.commaIsSet;
   }

   public Vector getConstraintClauseVector() {
      return this.constraintClauseVector;
   }

   public ConstraintClause getConstraintClause() {
      return this.constraintClause;
   }

   public String getDiskAttributes() {
      return this.diskAttributes;
   }

   public String getPhysicalCharacteristics() {
      return this.physicalCharacteristics;
   }

   public PartitionListAttributes getPartition() {
      return this.partition;
   }

   public String getPartitionKey() {
      return this.key;
   }

   public String getPartitioning() {
      return this.partitioning;
   }

   public String getUsing() {
      return this.using;
   }

   public String getHashing() {
      return this.hashing;
   }

   public ArrayList getColumnArraylist() {
      return this.columnArrayList;
   }

   public String getOpenBrace() {
      return this.openBrace;
   }

   public String getClosedBrace() {
      return this.closeBrace;
   }

   public String getOverflow() {
      return this.overflow;
   }

   public String getStorage() {
      return this.storage;
   }

   public String getPhysicalStorageAttributes() {
      return this.physicalStorageAttributes;
   }

   public String getFirst() {
      return this.first;
   }

   public String getAfter() {
      return this.after;
   }

   public String getBefore() {
      return this.before;
   }

   public void getIndexClause() {
   }

   public String getBeforeOrAfterColumnName() {
      return this.beforeOrAfterColumnName;
   }

   public String getUniqueOrPrimaryOrIndexOrFullText() {
      return this.uniqueOrPrimaryOrIndexOrFullText;
   }

   public String getUniqueConstraintName() {
      return this.uniqueConstraintName;
   }

   public Vector getIndexColumnVector() {
      return this.indexColumnVector;
   }

   public AddClause copyObjectValues() {
      AddClause dupAddClause = new AddClause();
      dupAddClause.setAdd(this.getAdd());
      dupAddClause.setWith(this.getWith());
      dupAddClause.setAfter(this.getAfter());
      dupAddClause.setCheckOrNoCheck(this.getCheckOrNoCheck());
      dupAddClause.setClosedBrace(this.getClosedBrace());
      dupAddClause.setCreateColumnVector(this.getCreateColumnVector());
      dupAddClause.setCommaBooleanValue(this.getCommaBooleanValue());
      dupAddClause.setHashing(this.getHashing());
      dupAddClause.setColumnArrayList(this.getColumnArraylist());
      dupAddClause.setOpenBrace(this.getOpenBrace());
      dupAddClause.setOverflow(this.getOverflow());
      dupAddClause.setStorage(this.getStorage());
      dupAddClause.setPhysicalStorageAttributes(this.getPhysicalStorageAttributes());
      dupAddClause.setPartition(this.getPartition());
      dupAddClause.setPartitionKey(this.getPartitionKey());
      dupAddClause.setPartitioning(this.getPartitioning());
      dupAddClause.setUsing(this.getUsing());
      dupAddClause.setFirst(this.getFirst());
      dupAddClause.setColumn(this.getColumn());
      dupAddClause.setIndexClause();
      dupAddClause.setObjectContext(this.context);
      dupAddClause.setDiskAttributes(this.getDiskAttributes());
      dupAddClause.setPhysicalCharacteristics(this.getPhysicalCharacteristics());
      dupAddClause.setConstraintClause(this.getConstraintClause());
      dupAddClause.setConstraintClauseVector(this.getConstraintClauseVector());
      dupAddClause.setBefore(this.getBefore());
      dupAddClause.setBeforeOrAfterColumnName(this.getBeforeOrAfterColumnName());
      dupAddClause.setUniqueOrPrimaryOrIndexOrFullText(this.getUniqueOrPrimaryOrIndexOrFullText());
      dupAddClause.setUniqueConstraintName(this.getUniqueConstraintName());
      dupAddClause.setIndexColumnVector(this.getIndexColumnVector());
      return dupAddClause;
   }

   public AddClause toOracle() throws ConvertException {
      AddClause dupAddClause = this.copyObjectValues();
      dupAddClause.setWith((String)null);
      dupAddClause.setCheckOrNoCheck((String)null);
      dupAddClause.setColumn((String)null);
      String temp = dupAddClause.getUniqueOrPrimaryOrIndexOrFullText();
      if (temp != null) {
         if (!temp.equalsIgnoreCase("primary key")) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         }

         Vector columns = dupAddClause.getIndexColumnVector();
         if (columns != null) {
            for(int i = 0; i < columns.size(); ++i) {
               Object obj = columns.get(i);
               if (obj instanceof String) {
                  String columnName = (String)obj;
                  if (columnName.startsWith("`")) {
                     columnName = columnName.substring(1, columnName.length() - 1);
                     if (columnName.length() > 30) {
                        columnName = columnName.substring(0, 30);
                     }

                     if (SwisSQLOptions.retainQuotedIdentifierForOracle || columnName.indexOf(32) != -1) {
                        columnName = "\"" + columnName + "\"";
                     }
                  } else if (columnName.startsWith("\"")) {
                     if (columnName.length() > 32) {
                        columnName = columnName.substring(0, 31) + "\"";
                     }
                  } else if (columnName.length() > 30) {
                     columnName = columnName.substring(0, 30);
                  }

                  columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
                  columns.setElementAt(columnName, i);
               }
            }
         }
      }

      String var15;
      if (dupAddClause.getOpenBrace() != null) {
         var15 = dupAddClause.getOpenBrace();
      }

      if (dupAddClause.getClosedBrace() != null) {
         var15 = dupAddClause.getClosedBrace();
      }

      if (dupAddClause.getCommaBooleanValue()) {
         boolean var16 = dupAddClause.getCommaBooleanValue();
      }

      ArrayList columnList = new ArrayList();
      if (dupAddClause.getCreateColumnVector() != null) {
         Vector columnNamesVector = dupAddClause.getCreateColumnVector();

         for(int i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               changeCreateColumn.toOracleString();
               Vector getConstraintVector = changeCreateColumn.getConstraintClause();
               Vector constraintVector = new Vector();
               if (getConstraintVector != null) {
                  for(int j = 0; j < getConstraintVector.size(); ++j) {
                     ConstraintClause toOracleConstraintClause = (ConstraintClause)getConstraintVector.get(j);
                     if (toOracleConstraintClause != null) {
                        ConstraintType changeConstraintType = toOracleConstraintClause.getConstraintType();
                        if (changeConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                           PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)changeConstraintType;
                           primaryOrUniqueConstraintClause.setClustered((String)null);
                           if (primaryOrUniqueConstraintClause.getWith() != null) {
                              Object fillfactor = primaryOrUniqueConstraintClause.getDiskAttr().get("FILLFACTOR");
                              if (fillfactor != null) {
                                 String percent = fillfactor.toString();
                                 dupAddClause.setDiskAttributes("PCTFREE " + percent);
                              }

                              primaryOrUniqueConstraintClause.setWith((String)null);
                              primaryOrUniqueConstraintClause.setDiskAttr((HashMap)null);
                           }

                           if (changeCreateColumn.getColumnName() != null) {
                              primaryOrUniqueConstraintClause.setOpenBrace((String)null);
                              primaryOrUniqueConstraintClause.setConstraintColumnNames((Vector)null);
                              primaryOrUniqueConstraintClause.setClosedBrace((String)null);
                           }
                        }

                        toOracleConstraintClause.toOracleString();
                     }

                     constraintVector.add(toOracleConstraintClause);
                  }

                  changeCreateColumn.setConstraintClause(constraintVector);
               }
            }
         }

         if (dupAddClause.getCreateColumnVector() != null) {
            Vector tempCreateColumnVector = dupAddClause.getCreateColumnVector();
            Vector oracleCreateColumnVector = new Vector();
            if (tempCreateColumnVector.size() > 1) {
               dupAddClause.setOpenBrace("(");
               dupAddClause.setClosedBrace(")");
            }

            for(int i = 0; i < tempCreateColumnVector.size(); ++i) {
               if (tempCreateColumnVector.elementAt(i) instanceof CreateColumn) {
                  CreateColumn createColn = (CreateColumn)tempCreateColumnVector.get(i);
                  createColn.toOracleString();
                  if (createColn.getColumnName() != null) {
                     columnList.add(createColn.getColumnName());
                  }

                  oracleCreateColumnVector.add(createColn);
               }
            }

            dupAddClause.setCreateColumnVector(oracleCreateColumnVector);
            HashMap truncatedNames = SwisSQLUtils.truncateNames(columnList, 30);

            for(int i = 0; i < oracleCreateColumnVector.size(); ++i) {
               Object obj = oracleCreateColumnVector.get(i);
               if (obj instanceof CreateColumn) {
                  CreateColumn changeCreateColumn = (CreateColumn)obj;
                  String oracleColName = changeCreateColumn.getColumnName();
                  String truncatedName = (String)truncatedNames.get(oracleColName);
                  if (truncatedName != null) {
                     changeCreateColumn.setColumnName(truncatedName);
                  }
               }
            }
         }

         if (dupAddClause.getPhysicalCharacteristics() != null) {
            String var23 = dupAddClause.getPhysicalCharacteristics();
         }
      }

      String var19;
      if (dupAddClause.getOverflow() != null) {
         var19 = dupAddClause.getOverflow();
      }

      if (dupAddClause.getStorage() != null) {
         var19 = dupAddClause.getStorage();
      }

      if (dupAddClause.getPhysicalStorageAttributes() != null) {
         var19 = dupAddClause.getPhysicalStorageAttributes();
      }

      if (dupAddClause.getPartition() != null) {
         PartitionListAttributes tempPartition = dupAddClause.getPartition();
         PartitionListAttributes oraclePartitionListAttributesClause = tempPartition.toOracle();
         dupAddClause.setPartition(oraclePartitionListAttributesClause);
      }

      dupAddClause.setFirst((String)null);
      dupAddClause.setAfter((String)null);
      dupAddClause.setBefore((String)null);
      dupAddClause.setBeforeOrAfterColumnName((String)null);
      dupAddClause.setHashing((String)null);
      dupAddClause.setUsing((String)null);
      if (dupAddClause.getPartitioning() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         dupAddClause.setPartitionKey((String)null);
         dupAddClause.setColumnArrayList((ArrayList)null);
         return dupAddClause;
      }
   }

   public AddClause toSybase() throws ConvertException {
      AddClause dupAddClause = this.copyObjectValues();
      dupAddClause.setOpenBrace((String)null);
      dupAddClause.setClosedBrace((String)null);
      dupAddClause.setWith((String)null);
      dupAddClause.setCheckOrNoCheck((String)null);
      dupAddClause.setColumn((String)null);
      if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         dupAddClause.setUniqueConstraintName((String)null);
         dupAddClause.setIndexColumnVector((Vector)null);
         dupAddClause.setFirst((String)null);
         dupAddClause.setAfter((String)null);
         dupAddClause.setBefore((String)null);
         dupAddClause.setBeforeOrAfterColumnName((String)null);
         dupAddClause.setHashing((String)null);
         dupAddClause.setUsing((String)null);
         if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            dupAddClause.setPartitionKey((String)null);
            dupAddClause.setColumnArrayList((ArrayList)null);
            if (dupAddClause.getPartition() != null) {
               dupAddClause.setPartition((PartitionListAttributes)null);
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               dupAddClause.setIndexClause();
               if (dupAddClause.getCommaBooleanValue()) {
                  boolean var2 = dupAddClause.getCommaBooleanValue();
               }

               if (dupAddClause.getOverflow() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  dupAddClause.setStorage((String)null);
                  dupAddClause.setPhysicalStorageAttributes((String)null);
                  dupAddClause.setDiskAttributes((String)null);
                  dupAddClause.setPhysicalCharacteristics((String)null);
                  if (dupAddClause.getCreateColumnVector() != null) {
                     String notNullStr = "";
                     Vector columnNamesVector = dupAddClause.getCreateColumnVector();

                     for(int i = 0; i < columnNamesVector.size(); ++i) {
                        if (columnNamesVector.get(i) instanceof CreateColumn) {
                           CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
                           changeCreateColumn.toSybaseString();
                           Vector constraintVector = changeCreateColumn.getConstraintClause();
                           if (constraintVector != null) {
                              for(int j = 0; j < constraintVector.size(); ++j) {
                                 ConstraintClause toSybaseConstraintClause = (ConstraintClause)constraintVector.get(j);
                                 if (toSybaseConstraintClause != null) {
                                    ConstraintType toSybaseConstraintType = toSybaseConstraintClause.getConstraintType();
                                    toSybaseConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                    if (toSybaseConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                                       PrimaryOrUniqueConstraintClause primaryClause = (PrimaryOrUniqueConstraintClause)toSybaseConstraintType;
                                       primaryClause.setWith((String)null);
                                       primaryClause.setDiskAttr((HashMap)null);
                                    }

                                    if (toSybaseConstraintType instanceof ForeignConstraintClause) {
                                       ForeignConstraintClause foreignClause = (ForeignConstraintClause)toSybaseConstraintType;
                                       foreignClause.setOnDelete((String)null);
                                       foreignClause.setActionOnDelete((String)null);
                                       foreignClause.setOnUpdate((String)null);
                                       foreignClause.setActionOnUpdate((String)null);
                                    }

                                    toSybaseConstraintClause.toSybaseString();
                                 }
                              }
                           }
                        }
                     }
                  }

                  return dupAddClause;
               }
            }
         }
      }
   }

   public AddClause toMSSQLServer() throws ConvertException {
      AddClause dupAddClause = this.copyObjectValues();
      boolean handleUnique = false;
      dupAddClause.setOpenBrace((String)null);
      dupAddClause.setClosedBrace((String)null);
      String notNullStr;
      if (dupAddClause.getWith() != null) {
         notNullStr = dupAddClause.getWith();
      }

      if (dupAddClause.getCheckOrNoCheck() != null) {
         notNullStr = dupAddClause.getCheckOrNoCheck();
      }

      dupAddClause.setColumn((String)null);
      if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
         if (!dupAddClause.getUniqueOrPrimaryOrIndexOrFullText().equalsIgnoreCase("UNIQUE")) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         }

         handleUnique = true;
         dupAddClause.setUniqueOrPrimaryOrIndexOrFullText("CONSTRAINT");
      }

      Vector columnNamesVector;
      int i;
      if (!handleUnique) {
         dupAddClause.setUniqueConstraintName((String)null);
         dupAddClause.setIndexColumnVector((Vector)null);
      } else {
         notNullStr = dupAddClause.getUniqueConstraintName();
         if (notNullStr.startsWith("`") || notNullStr.startsWith("\"")) {
            notNullStr = notNullStr.substring(1, notNullStr.length() - 1);
         }

         dupAddClause.setUniqueConstraintName(notNullStr + " " + "UNIQUE");
         columnNamesVector = dupAddClause.getIndexColumnVector();
         if (columnNamesVector != null) {
            for(i = 0; i < columnNamesVector.size(); ++i) {
               Object obj = columnNamesVector.get(i);
               if (obj instanceof String) {
                  String columnName = (String)obj;
                  if (columnName.startsWith("`") || columnName.startsWith("\"")) {
                     columnName = columnName.substring(1, columnName.length() - 1);
                  }

                  columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords(2), (ModifiedObjectAttr)null, 1);
                  columnNamesVector.setElementAt(columnName, i);
               }
            }
         }
      }

      dupAddClause.setFirst((String)null);
      dupAddClause.setAfter((String)null);
      dupAddClause.setBefore((String)null);
      dupAddClause.setBeforeOrAfterColumnName((String)null);
      dupAddClause.setHashing((String)null);
      dupAddClause.setUsing((String)null);
      if (dupAddClause.getPartitioning() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         dupAddClause.setPartitionKey((String)null);
         dupAddClause.setColumnArrayList((ArrayList)null);
         if (dupAddClause.getPartition() != null) {
            dupAddClause.setPartition((PartitionListAttributes)null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            dupAddClause.setIndexClause();
            if (dupAddClause.getCommaBooleanValue()) {
               boolean var16 = dupAddClause.getCommaBooleanValue();
            }

            if (dupAddClause.getOverflow() != null) {
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               dupAddClause.setStorage((String)null);
               dupAddClause.setPhysicalStorageAttributes((String)null);
               if (dupAddClause.getCreateColumnVector() != null) {
                  notNullStr = "";
                  columnNamesVector = dupAddClause.getCreateColumnVector();

                  for(i = 0; i < columnNamesVector.size(); ++i) {
                     if (columnNamesVector.get(i) instanceof CreateColumn) {
                        CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
                        changeCreateColumn.toMSSQLServerString();
                        Vector getConstraintVector = changeCreateColumn.getConstraintClause();
                        if (getConstraintVector != null) {
                           for(int j = 0; j < getConstraintVector.size(); ++j) {
                              ConstraintClause toSQLServerConstraintClause = (ConstraintClause)getConstraintVector.get(j);
                              ConstraintType toSQLServerConstraintType = toSQLServerConstraintClause.getConstraintType();
                              toSQLServerConstraintClause.toMSSQLServerString();
                              if (toSQLServerConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                                 int fillIntValue = 0;
                                 PrimaryOrUniqueConstraintClause pcc = (PrimaryOrUniqueConstraintClause)toSQLServerConstraintType;
                                 String fillfactor = dupAddClause.getDiskAttributes();
                                 String tempFillfactor = "";
                                 if (fillfactor != null) {
                                    tempFillfactor = fillfactor.substring(0, 7);
                                    tempFillfactor = fillfactor.toUpperCase();
                                    if (tempFillfactor.startsWith("PCTFREE")) {
                                       fillfactor = fillfactor.substring(8);
                                    } else if (tempFillfactor.startsWith("PCTUSED")) {
                                       fillfactor = fillfactor.substring(8);
                                       fillIntValue = Integer.parseInt(fillfactor);
                                       fillIntValue = 100 - fillIntValue;
                                       (new StringBuilder()).append("").append(fillIntValue).toString();
                                    }

                                    pcc.setWith("WITH");
                                    HashMap diskAttr = new HashMap();
                                    diskAttr.put("FILLFACTOR", new String(fillIntValue + ""));
                                    pcc.setDiskAttr(diskAttr);
                                    dupAddClause.setDiskAttributes((String)null);
                                    break;
                                 }
                              }
                           }
                        } else if (dupAddClause.getDiskAttributes() != null) {
                           dupAddClause.setDiskAttributes((String)null);
                        }
                     }
                  }
               }

               dupAddClause.setPhysicalCharacteristics((String)null);
               return dupAddClause;
            }
         }
      }
   }

   public AddClause toDB2() throws ConvertException {
      AddClause dupAddClause = this.copyObjectValues();
      dupAddClause.setOpenBrace((String)null);
      dupAddClause.setClosedBrace((String)null);
      dupAddClause.setWith((String)null);
      dupAddClause.setCheckOrNoCheck((String)null);
      String var2;
      if (dupAddClause.getColumn() != null) {
         var2 = dupAddClause.getColumn();
      }

      if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         dupAddClause.setUniqueConstraintName((String)null);
         dupAddClause.setIndexColumnVector((Vector)null);
         dupAddClause.setFirst((String)null);
         dupAddClause.setAfter((String)null);
         dupAddClause.setBefore((String)null);
         dupAddClause.setBeforeOrAfterColumnName((String)null);
         if (dupAddClause.getPartition() != null) {
            dupAddClause.setPartition((PartitionListAttributes)null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else if (dupAddClause.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            dupAddClause.setStorage((String)null);
            dupAddClause.setPhysicalStorageAttributes((String)null);
            dupAddClause.setIndexClause();
            dupAddClause.setCommaBooleanValue(false);
            Vector columnNamesVector;
            if (dupAddClause.getCreateColumnVector() != null) {
               columnNamesVector = dupAddClause.getCreateColumnVector();

               for(int i = 0; i < columnNamesVector.size(); ++i) {
                  if (columnNamesVector.get(i) instanceof CreateColumn) {
                     CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
                     changeCreateColumn.setAlterStatement(true);
                     changeCreateColumn.setDatatypeMapping(this.datatypeMapping);
                     Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                     int j;
                     ConstraintClause toDB2ConstraintClause;
                     if (changeConstraintVector != null) {
                        for(j = 0; j < changeConstraintVector.size(); ++j) {
                           toDB2ConstraintClause = (ConstraintClause)changeConstraintVector.get(j);
                           if (toDB2ConstraintClause != null && changeCreateColumn.getColumnName() == null && toDB2ConstraintClause.getConstraintType() != null && toDB2ConstraintClause.getConstraintType() instanceof CheckConstraintClause) {
                              CheckConstraintClause ccClause = (CheckConstraintClause)toDB2ConstraintClause.getConstraintType();
                              ccClause.setStmtTableName(this.stmtTableName);
                           }
                        }
                     }

                     changeCreateColumn.toDB2String();
                     changeConstraintVector = changeCreateColumn.getConstraintClause();
                     if (changeConstraintVector != null) {
                        for(j = 0; j < changeConstraintVector.size(); ++j) {
                           toDB2ConstraintClause = (ConstraintClause)changeConstraintVector.get(j);
                           if (toDB2ConstraintClause != null) {
                              toDB2ConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                              toDB2ConstraintClause.toDB2String();
                           }
                        }
                     }
                  }
               }
            }

            if (dupAddClause.getCreateColumnVector() != null) {
               columnNamesVector = dupAddClause.getCreateColumnVector();
               if (columnNamesVector.size() > 1) {
                  dupAddClause.setColumn((String)null);
               }
            }

            if (dupAddClause.getPartitioning() != null) {
               var2 = dupAddClause.getPartitioning();
            }

            if (dupAddClause.getPartitionKey() != null) {
               var2 = dupAddClause.getPartitionKey();
            }

            if (dupAddClause.getColumnArraylist() != null) {
               ArrayList var10 = dupAddClause.getColumnArraylist();
            }

            if (dupAddClause.getUsing() != null) {
               var2 = dupAddClause.getUsing();
            }

            if (dupAddClause.getHashing() != null) {
               var2 = dupAddClause.getHashing();
            }

            dupAddClause.setPhysicalCharacteristics((String)null);
            dupAddClause.setDiskAttributes((String)null);
            return dupAddClause;
         }
      }
   }

   public AddClause toANSI() throws ConvertException {
      AddClause dupAddClause = this.copyObjectValues();
      dupAddClause.setOpenBrace((String)null);
      dupAddClause.setClosedBrace((String)null);
      dupAddClause.setWith((String)null);
      dupAddClause.setCheckOrNoCheck((String)null);
      if (dupAddClause.getColumn() != null) {
         String var2 = dupAddClause.getColumn();
      }

      if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         dupAddClause.setUniqueConstraintName((String)null);
         dupAddClause.setIndexColumnVector((Vector)null);
         dupAddClause.setFirst((String)null);
         dupAddClause.setAfter((String)null);
         dupAddClause.setBefore((String)null);
         dupAddClause.setBeforeOrAfterColumnName((String)null);
         dupAddClause.setPhysicalCharacteristics((String)null);
         dupAddClause.setDiskAttributes((String)null);
         dupAddClause.setHashing((String)null);
         dupAddClause.setUsing((String)null);
         if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            dupAddClause.setPartitionKey((String)null);
            dupAddClause.setColumnArrayList((ArrayList)null);
            if (dupAddClause.getPartition() != null) {
               dupAddClause.setPartition((PartitionListAttributes)null);
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               dupAddClause.setIndexClause();
               if (dupAddClause.getCommaBooleanValue()) {
                  boolean var9 = dupAddClause.getCommaBooleanValue();
               }

               Vector columnNamesVector;
               if (dupAddClause.getCreateColumnVector() != null) {
                  columnNamesVector = dupAddClause.getCreateColumnVector();

                  for(int i = 0; i < columnNamesVector.size(); ++i) {
                     if (columnNamesVector.get(i) instanceof CreateColumn) {
                        CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
                        changeCreateColumn.toANSIString();
                        Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                        if (changeConstraintVector != null) {
                           for(int j = 0; j < changeConstraintVector.size(); ++j) {
                              ConstraintClause toANSISQLConstraintClause = (ConstraintClause)changeConstraintVector.get(j);
                              if (toANSISQLConstraintClause != null) {
                                 ConstraintType toANSIConstraintType = toANSISQLConstraintClause.getConstraintType();
                                 toANSISQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                 toANSISQLConstraintClause.toANSIString();
                              }
                           }
                        }
                     }
                  }
               }

               if (dupAddClause.getCreateColumnVector() != null) {
                  columnNamesVector = dupAddClause.getCreateColumnVector();
                  if (columnNamesVector.size() > 1) {
                     dupAddClause.setColumn((String)null);
                  }
               }

               if (dupAddClause.getOverflow() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  dupAddClause.setStorage((String)null);
                  dupAddClause.setPhysicalStorageAttributes((String)null);
                  return dupAddClause;
               }
            }
         }
      }
   }

   public AddClause toInformix() throws ConvertException {
      AddClause dupAddClause = this.copyObjectValues();
      String var2;
      if (dupAddClause.getOpenBrace() != null) {
         var2 = dupAddClause.getOpenBrace();
      }

      if (dupAddClause.getClosedBrace() != null) {
         var2 = dupAddClause.getClosedBrace();
      }

      dupAddClause.setWith((String)null);
      dupAddClause.setCheckOrNoCheck((String)null);
      dupAddClause.setColumn((String)null);
      if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         dupAddClause.setUniqueConstraintName((String)null);
         dupAddClause.setIndexColumnVector((Vector)null);
         dupAddClause.setFirst((String)null);
         dupAddClause.setAfter((String)null);
         if (dupAddClause.getBefore() != null) {
            var2 = dupAddClause.getBefore();
         }

         if (dupAddClause.getBeforeOrAfterColumnName() != null) {
            var2 = dupAddClause.getBeforeOrAfterColumnName();
         }

         dupAddClause.setPhysicalCharacteristics((String)null);
         dupAddClause.setDiskAttributes((String)null);
         dupAddClause.setHashing((String)null);
         dupAddClause.setUsing((String)null);
         if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            dupAddClause.setPartitionKey((String)null);
            dupAddClause.setColumnArrayList((ArrayList)null);
            if (dupAddClause.getPartition() != null) {
               dupAddClause.setPartition((PartitionListAttributes)null);
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               dupAddClause.setIndexClause();
               if (dupAddClause.getCommaBooleanValue()) {
                  boolean var9 = dupAddClause.getCommaBooleanValue();
               }

               Vector columnNamesVector;
               if (dupAddClause.getCreateColumnVector() != null) {
                  columnNamesVector = dupAddClause.getCreateColumnVector();

                  for(int i = 0; i < columnNamesVector.size(); ++i) {
                     if (columnNamesVector.get(i) instanceof CreateColumn) {
                        CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
                        changeCreateColumn.toInformixString();
                        Vector constraintVector = changeCreateColumn.getConstraintClause();
                        if (constraintVector != null) {
                           for(int j = 0; j < constraintVector.size(); ++j) {
                              ConstraintClause toInformixConstraintClause = (ConstraintClause)constraintVector.get(j);
                              if (toInformixConstraintClause != null) {
                                 ConstraintType toInformixConstraintType = toInformixConstraintClause.getConstraintType();
                                 toInformixConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                 toInformixConstraintClause.toInformixString();
                              }
                           }
                        }
                     }
                  }
               }

               if (dupAddClause.getCreateColumnVector() != null) {
                  columnNamesVector = dupAddClause.getCreateColumnVector();
                  if (columnNamesVector.size() > 1) {
                     dupAddClause.setOpenBrace("(");
                     dupAddClause.setClosedBrace(")");
                  }
               }

               if (dupAddClause.getOverflow() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  dupAddClause.setStorage((String)null);
                  dupAddClause.setPhysicalStorageAttributes((String)null);
                  return dupAddClause;
               }
            }
         }
      }
   }

   public AddClause toBigQuery() throws ConvertException {
      AddClause dupAddClause = this.copyObjectValues();
      dupAddClause.setOpenBrace((String)null);
      dupAddClause.setClosedBrace((String)null);
      dupAddClause.setWith((String)null);
      dupAddClause.setCheckOrNoCheck((String)null);
      if (dupAddClause.getColumn() != null) {
         String var2 = dupAddClause.getColumn();
      }

      if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         dupAddClause.setUniqueConstraintName((String)null);
         dupAddClause.setIndexColumnVector((Vector)null);
         dupAddClause.setFirst((String)null);
         dupAddClause.setAfter((String)null);
         dupAddClause.setBefore((String)null);
         dupAddClause.setBeforeOrAfterColumnName((String)null);
         dupAddClause.setPhysicalCharacteristics((String)null);
         dupAddClause.setDiskAttributes((String)null);
         dupAddClause.setHashing((String)null);
         dupAddClause.setUsing((String)null);
         if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            dupAddClause.setPartitionKey((String)null);
            dupAddClause.setColumnArrayList((ArrayList)null);
            if (dupAddClause.getPartition() != null) {
               dupAddClause.setPartition((PartitionListAttributes)null);
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               dupAddClause.setIndexClause();
               if (dupAddClause.getCreateColumnVector() != null) {
                  Vector columnNamesVector = dupAddClause.getCreateColumnVector();

                  for(int i = 0; i < columnNamesVector.size(); ++i) {
                     if (columnNamesVector.get(i) instanceof CreateColumn) {
                        CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
                        changeCreateColumn.toBigQueryString();
                        Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                        if (changeConstraintVector != null) {
                           for(int j = 0; j < changeConstraintVector.size(); ++j) {
                              ConstraintClause toBigQueryConstraintClause = (ConstraintClause)changeConstraintVector.get(j);
                              if (toBigQueryConstraintClause != null) {
                                 toBigQueryConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                 toBigQueryConstraintClause.toBigQueryString();
                              }
                           }
                        }
                     }
                  }
               }

               if (dupAddClause.getCommaBooleanValue()) {
                  dupAddClause.setCommaBooleanValue(false);
                  dupAddClause.setCreateColumnVector((Vector)null);
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else if (dupAddClause.getOverflow() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  dupAddClause.setStorage((String)null);
                  dupAddClause.setPhysicalStorageAttributes((String)null);
                  return dupAddClause;
               }
            }
         }
      }
   }

   public AddClause toPostgreSQL() throws ConvertException {
      AddClause dupAddClause = this.copyObjectValues();
      dupAddClause.setOpenBrace((String)null);
      dupAddClause.setClosedBrace((String)null);
      dupAddClause.setWith((String)null);
      dupAddClause.setCheckOrNoCheck((String)null);
      if (dupAddClause.getColumn() != null) {
         String var2 = dupAddClause.getColumn();
      }

      if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         dupAddClause.setUniqueConstraintName((String)null);
         dupAddClause.setIndexColumnVector((Vector)null);
         dupAddClause.setFirst((String)null);
         dupAddClause.setAfter((String)null);
         dupAddClause.setBefore((String)null);
         dupAddClause.setBeforeOrAfterColumnName((String)null);
         dupAddClause.setPhysicalCharacteristics((String)null);
         dupAddClause.setDiskAttributes((String)null);
         dupAddClause.setHashing((String)null);
         dupAddClause.setUsing((String)null);
         if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            dupAddClause.setPartitionKey((String)null);
            dupAddClause.setColumnArrayList((ArrayList)null);
            if (dupAddClause.getPartition() != null) {
               dupAddClause.setPartition((PartitionListAttributes)null);
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               dupAddClause.setIndexClause();
               if (dupAddClause.getCreateColumnVector() != null) {
                  Vector columnNamesVector = dupAddClause.getCreateColumnVector();

                  for(int i = 0; i < columnNamesVector.size(); ++i) {
                     if (columnNamesVector.get(i) instanceof CreateColumn) {
                        CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
                        changeCreateColumn.toPostgreSQLString();
                        Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                        if (changeConstraintVector != null) {
                           for(int j = 0; j < changeConstraintVector.size(); ++j) {
                              ConstraintClause toPostgreSQLConstraintClause = (ConstraintClause)changeConstraintVector.get(j);
                              if (toPostgreSQLConstraintClause != null) {
                                 toPostgreSQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                 toPostgreSQLConstraintClause.toPostgreSQLString();
                              }
                           }
                        }
                     }
                  }
               }

               if (dupAddClause.getCommaBooleanValue()) {
                  dupAddClause.setCommaBooleanValue(false);
                  dupAddClause.setCreateColumnVector((Vector)null);
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else if (dupAddClause.getOverflow() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  dupAddClause.setStorage((String)null);
                  dupAddClause.setPhysicalStorageAttributes((String)null);
                  return dupAddClause;
               }
            }
         }
      }
   }

   public AddClause toMySQL() throws ConvertException {
      AddClause dupAddClause = this.copyObjectValues();
      Vector tempIndexColumnNamesVector;
      int i;
      if (dupAddClause.getCreateColumnVector() != null) {
         tempIndexColumnNamesVector = dupAddClause.getCreateColumnVector();

         for(i = 0; i < tempIndexColumnNamesVector.size(); ++i) {
            if (tempIndexColumnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)tempIndexColumnNamesVector.get(i);
               changeCreateColumn.toMySQLString();
               Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
               if (changeConstraintVector != null) {
                  for(int j = 0; j < changeConstraintVector.size(); ++j) {
                     ConstraintClause toMySQLConstraintClause = (ConstraintClause)changeConstraintVector.get(j);
                     if (toMySQLConstraintClause != null) {
                        if (changeCreateColumn.getIdentity() != null) {
                           toMySQLConstraintClause.setAutoIncrement("AUTO_INCREMENT");
                           changeCreateColumn.setIdentity((String)null);
                        }

                        ConstraintType toMySQLConstraintType = toMySQLConstraintClause.getConstraintType();
                        toMySQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                        toMySQLConstraintClause.toMySQLString();
                     }
                  }
               }
            }
         }
      }

      if (dupAddClause.getCommaBooleanValue()) {
         boolean var9 = dupAddClause.getCommaBooleanValue();
      }

      String var10;
      if (dupAddClause.getOpenBrace() != null) {
         var10 = dupAddClause.getOpenBrace();
      }

      if (dupAddClause.getClosedBrace() != null) {
         var10 = dupAddClause.getClosedBrace();
      }

      if (dupAddClause.getCreateColumnVector() != null) {
         tempIndexColumnNamesVector = dupAddClause.getCreateColumnVector();
         if (tempIndexColumnNamesVector.size() > 1) {
            dupAddClause.setOpenBrace("(");
            dupAddClause.setClosedBrace(")");
            dupAddClause.setColumn((String)null);
         }
      }

      dupAddClause.setWith((String)null);
      dupAddClause.setCheckOrNoCheck((String)null);
      if (dupAddClause.getColumn() != null) {
         var10 = dupAddClause.getColumn();
      }

      if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
         var10 = dupAddClause.getUniqueOrPrimaryOrIndexOrFullText();
      }

      if (dupAddClause.getUniqueConstraintName() != null) {
         var10 = dupAddClause.getUniqueConstraintName();
      }

      if (dupAddClause.getIndexColumnVector() != null) {
         tempIndexColumnNamesVector = dupAddClause.getIndexColumnVector();

         for(i = 0; i < tempIndexColumnNamesVector.size(); ++i) {
            String var11 = (String)tempIndexColumnNamesVector.get(i);
         }
      }

      if (dupAddClause.getFirst() != null) {
         var10 = dupAddClause.getFirst();
      }

      if (dupAddClause.getAfter() != null) {
         var10 = dupAddClause.getAfter();
      }

      if (dupAddClause.getBeforeOrAfterColumnName() != null) {
         var10 = dupAddClause.getBeforeOrAfterColumnName();
      }

      if (dupAddClause.getBefore() != null) {
         dupAddClause.setBefore((String)null);
         dupAddClause.setBeforeOrAfterColumnName((String)null);
      }

      dupAddClause.setPhysicalCharacteristics((String)null);
      dupAddClause.setDiskAttributes((String)null);
      dupAddClause.setHashing((String)null);
      dupAddClause.setUsing((String)null);
      if (dupAddClause.getPartitioning() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         dupAddClause.setPartitionKey((String)null);
         dupAddClause.setColumnArrayList((ArrayList)null);
         if (dupAddClause.getPartition() != null) {
            dupAddClause.setPartition((PartitionListAttributes)null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            dupAddClause.setIndexClause();
            if (dupAddClause.getOverflow() != null) {
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               dupAddClause.setStorage((String)null);
               dupAddClause.setPhysicalStorageAttributes((String)null);
               return dupAddClause;
            }
         }
      }
   }

   public AddClause toSnowflake() throws ConvertException {
      AddClause dupAddClause = this.copyObjectValues();
      dupAddClause.setOpenBrace((String)null);
      dupAddClause.setClosedBrace((String)null);
      dupAddClause.setWith((String)null);
      dupAddClause.setCheckOrNoCheck((String)null);
      if (dupAddClause.getColumn() != null) {
         String var2 = dupAddClause.getColumn();
      }

      if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         dupAddClause.setUniqueConstraintName((String)null);
         dupAddClause.setIndexColumnVector((Vector)null);
         dupAddClause.setFirst((String)null);
         dupAddClause.setAfter((String)null);
         dupAddClause.setBefore((String)null);
         dupAddClause.setBeforeOrAfterColumnName((String)null);
         dupAddClause.setPhysicalCharacteristics((String)null);
         dupAddClause.setDiskAttributes((String)null);
         dupAddClause.setHashing((String)null);
         dupAddClause.setUsing((String)null);
         if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            dupAddClause.setPartitionKey((String)null);
            dupAddClause.setColumnArrayList((ArrayList)null);
            if (dupAddClause.getPartition() != null) {
               dupAddClause.setPartition((PartitionListAttributes)null);
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               dupAddClause.setIndexClause();
               if (dupAddClause.getCreateColumnVector() != null) {
                  Vector columnNamesVector = dupAddClause.getCreateColumnVector();

                  for(int i = 0; i < columnNamesVector.size(); ++i) {
                     if (columnNamesVector.get(i) instanceof CreateColumn) {
                        CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
                        changeCreateColumn.toSnowflakeString();
                        Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                        if (changeConstraintVector != null) {
                           for(int j = 0; j < changeConstraintVector.size(); ++j) {
                              ConstraintClause toSnowflakeConstraintClause = (ConstraintClause)changeConstraintVector.get(j);
                              if (toSnowflakeConstraintClause != null) {
                                 toSnowflakeConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                 toSnowflakeConstraintClause.toSnowflakeString();
                              }
                           }
                        }
                     }
                  }
               }

               if (dupAddClause.getCommaBooleanValue()) {
                  dupAddClause.setCommaBooleanValue(false);
                  dupAddClause.setCreateColumnVector((Vector)null);
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else if (dupAddClause.getOverflow() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  dupAddClause.setStorage((String)null);
                  dupAddClause.setPhysicalStorageAttributes((String)null);
                  return dupAddClause;
               }
            }
         }
      }
   }

   public AddClause toTimesTen() throws ConvertException {
      AddClause dupAddClause = this.copyObjectValues();
      dupAddClause.setWith((String)null);
      dupAddClause.setCheckOrNoCheck((String)null);
      dupAddClause.setColumn((String)null);
      if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
         throw new ConvertException("\nUnsupported SQL\n");
      } else {
         dupAddClause.setUniqueConstraintName((String)null);
         dupAddClause.setIndexColumnVector((Vector)null);
         if (dupAddClause.getCreateColumnVector() != null) {
            Vector columnNamesVector = dupAddClause.getCreateColumnVector();

            for(int i = 0; i < columnNamesVector.size(); ++i) {
               if (columnNamesVector.get(i) instanceof CreateColumn) {
                  CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
                  changeCreateColumn.setAlterStatement(true);
                  changeCreateColumn.setColumnName(CustomizeUtil.objectNamesToQuotedIdentifier(changeCreateColumn.getColumnName(), SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10));
                  if (changeCreateColumn.getDefault() != null) {
                     if (AlterStatement.generalComment.trim().length() > 0 && AlterStatement.generalComment.indexOf("DEFAULT Constraint") == -1) {
                        AlterStatement.generalComment = AlterStatement.generalComment + "\n/* SwisSQL Message : DEFAULT Constraint is not supported TimesTen 5.1.21 */";
                     } else if (AlterStatement.generalComment.indexOf("DEFAULT Constraint") == -1) {
                        AlterStatement.generalComment = "/* SwisSQL Message : DEFAULT Constraint is not supported in TimesTen 5.1.21 */";
                     }
                  }

                  changeCreateColumn.toTimesTenString();
                  if (changeCreateColumn.getNullStatus() != null) {
                     if (changeCreateColumn.getNullStatus().equalsIgnoreCase("not null")) {
                        if (AlterStatement.generalComment.trim().length() > 0 && AlterStatement.generalComment.indexOf("NOT NULL Constraint") == -1) {
                           AlterStatement.generalComment = AlterStatement.generalComment + "\n/* SwisSQL Message : NOT NULL Constraint is not supported in ALTER Queries in TimesTen 5.1.21 */";
                        } else if (AlterStatement.generalComment.indexOf("NOT NULL Constraint") == -1) {
                           AlterStatement.generalComment = "/* SwisSQL Message : NOT NULL Constraint is not supported in ALTER Queries in TimesTen 5.1.21 */";
                        }
                     }

                     changeCreateColumn.setNullStatus((String)null);
                  }

                  Vector getConstraintVector = changeCreateColumn.getConstraintClause();
                  Vector constraintVector = new Vector();
                  if (getConstraintVector != null) {
                     for(int j = 0; j < getConstraintVector.size(); ++j) {
                        ConstraintClause toTimesTenConstraintClause = (ConstraintClause)getConstraintVector.get(j);
                        if (toTimesTenConstraintClause != null) {
                           ConstraintType changeConstraintType = toTimesTenConstraintClause.getConstraintType();
                           if (changeConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                              throw new ConvertException("\nPrimary Key Constraints are not supported in ALTER Queries in TimesTen 5.1.21\n");
                           }
                        }

                        constraintVector.add(toTimesTenConstraintClause);
                     }

                     changeCreateColumn.setConstraintClause(constraintVector);
                  }
               }
            }

            if (dupAddClause.getCreateColumnVector() != null) {
               Vector tempCreateColumnVector = dupAddClause.getCreateColumnVector();
               if (tempCreateColumnVector.size() > 1) {
                  dupAddClause.setOpenBrace("(");
                  dupAddClause.setClosedBrace(")");
               }
            }
         }

         if (dupAddClause.getPartition() != null) {
            dupAddClause.setPartition((PartitionListAttributes)null);
         }

         dupAddClause.setFirst((String)null);
         dupAddClause.setAfter((String)null);
         dupAddClause.setBefore((String)null);
         dupAddClause.setBeforeOrAfterColumnName((String)null);
         dupAddClause.setHashing((String)null);
         dupAddClause.setUsing((String)null);
         if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("\nUnsupported SQL\n");
         } else {
            dupAddClause.setPartitionKey((String)null);
            dupAddClause.setColumnArrayList((ArrayList)null);
            return dupAddClause;
         }
      }
   }

   public AddClause toNetezza() throws ConvertException {
      AddClause dupAddClause = this.copyObjectValues();
      dupAddClause.setOpenBrace((String)null);
      dupAddClause.setClosedBrace((String)null);
      dupAddClause.setWith((String)null);
      dupAddClause.setCheckOrNoCheck((String)null);
      if (dupAddClause.getColumn() != null) {
         String var2 = dupAddClause.getColumn();
      }

      if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         dupAddClause.setUniqueConstraintName((String)null);
         dupAddClause.setIndexColumnVector((Vector)null);
         dupAddClause.setFirst((String)null);
         dupAddClause.setAfter((String)null);
         dupAddClause.setBefore((String)null);
         dupAddClause.setBeforeOrAfterColumnName((String)null);
         dupAddClause.setPhysicalCharacteristics((String)null);
         dupAddClause.setDiskAttributes((String)null);
         dupAddClause.setHashing((String)null);
         dupAddClause.setUsing((String)null);
         if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            dupAddClause.setPartitionKey((String)null);
            dupAddClause.setColumnArrayList((ArrayList)null);
            if (dupAddClause.getPartition() != null) {
               dupAddClause.setPartition((PartitionListAttributes)null);
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               dupAddClause.setIndexClause();
               if (dupAddClause.getCommaBooleanValue()) {
                  boolean var9 = dupAddClause.getCommaBooleanValue();
               }

               Vector columnNamesVector;
               if (dupAddClause.getCreateColumnVector() != null) {
                  columnNamesVector = dupAddClause.getCreateColumnVector();

                  for(int i = 0; i < columnNamesVector.size(); ++i) {
                     if (columnNamesVector.get(i) instanceof CreateColumn) {
                        CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
                        if (changeCreateColumn.getColumnName() != null) {
                           throw new ConvertException("/*SwisSQL Message : Netezza does not support adding columns to table.*/");
                        }

                        Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                        if (changeConstraintVector != null) {
                           for(int j = 0; j < changeConstraintVector.size(); ++j) {
                              ConstraintClause toNetezzaSQLConstraintClause = (ConstraintClause)changeConstraintVector.get(j);
                              if (toNetezzaSQLConstraintClause != null) {
                                 ConstraintType toNetezzaConstraintType = toNetezzaSQLConstraintClause.getConstraintType();
                                 if (toNetezzaConstraintType instanceof CheckConstraintClause) {
                                    SwisSQLUtils.swissqlMessageList.add("CHECK Constraint is not supported in Netezza. Manual intervention required");
                                 } else {
                                    toNetezzaSQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                    toNetezzaSQLConstraintClause.toNetezzaString();
                                 }
                              }
                           }
                        }

                        changeCreateColumn.toNetezzaString();
                     }
                  }
               }

               if (dupAddClause.getCreateColumnVector() != null) {
                  columnNamesVector = dupAddClause.getCreateColumnVector();
                  if (columnNamesVector.size() > 1) {
                     dupAddClause.setColumn((String)null);
                  }
               }

               if (dupAddClause.getOverflow() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  dupAddClause.setStorage((String)null);
                  dupAddClause.setPhysicalStorageAttributes((String)null);
                  return dupAddClause;
               }
            }
         }
      }
   }

   public AddClause toTeradata() throws ConvertException {
      AddClause dupAddClause = this.copyObjectValues();
      dupAddClause.setOpenBrace((String)null);
      dupAddClause.setClosedBrace((String)null);
      dupAddClause.setWith((String)null);
      dupAddClause.setCheckOrNoCheck((String)null);
      if (dupAddClause.getColumn() != null) {
         String var2 = dupAddClause.getColumn();
      }

      if (dupAddClause.getUniqueOrPrimaryOrIndexOrFullText() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         dupAddClause.setUniqueConstraintName((String)null);
         dupAddClause.setIndexColumnVector((Vector)null);
         dupAddClause.setFirst((String)null);
         dupAddClause.setAfter((String)null);
         dupAddClause.setBefore((String)null);
         dupAddClause.setBeforeOrAfterColumnName((String)null);
         dupAddClause.setPhysicalCharacteristics((String)null);
         dupAddClause.setDiskAttributes((String)null);
         dupAddClause.setHashing((String)null);
         dupAddClause.setUsing((String)null);
         if (dupAddClause.getPartitioning() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            dupAddClause.setPartitionKey((String)null);
            dupAddClause.setColumnArrayList((ArrayList)null);
            if (dupAddClause.getPartition() != null) {
               dupAddClause.setPartition((PartitionListAttributes)null);
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               dupAddClause.setIndexClause();
               if (dupAddClause.getCommaBooleanValue()) {
                  boolean var9 = dupAddClause.getCommaBooleanValue();
               }

               Vector columnNamesVector;
               if (dupAddClause.getCreateColumnVector() != null) {
                  columnNamesVector = dupAddClause.getCreateColumnVector();

                  for(int i = 0; i < columnNamesVector.size(); ++i) {
                     if (columnNamesVector.get(i) instanceof CreateColumn) {
                        CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
                        changeCreateColumn.toTeradataString();
                        Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
                        if (changeConstraintVector != null) {
                           for(int j = 0; j < changeConstraintVector.size(); ++j) {
                              ConstraintClause toTeradataSQLConstraintClause = (ConstraintClause)changeConstraintVector.get(j);
                              if (toTeradataSQLConstraintClause != null) {
                                 ConstraintType toTeradataConstraintType = toTeradataSQLConstraintClause.getConstraintType();
                                 toTeradataSQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                                 toTeradataSQLConstraintClause.toTeradataString();
                              }
                           }
                        }
                     }
                  }
               }

               if (dupAddClause.getCreateColumnVector() != null) {
                  columnNamesVector = dupAddClause.getCreateColumnVector();
                  if (columnNamesVector.size() > 1) {
                     dupAddClause.setColumn((String)null);
                  }
               }

               if (dupAddClause.getOverflow() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  dupAddClause.setStorage((String)null);
                  dupAddClause.setPhysicalStorageAttributes((String)null);
                  return dupAddClause;
               }
            }
         }
      }
   }

   public String removeIndent(String str) {
      str = str.replace('\n', ' ');
      str = str.replace('\t', ' ');
      return str;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.with != null) {
         sb.append(this.with.toUpperCase());
      }

      if (this.checkOrNoCheck != null) {
         sb.append(" " + this.checkOrNoCheck.toUpperCase() + " ");
      }

      if (this.add != null) {
         sb.append(this.add.toUpperCase());
      }

      if (this.column != null) {
         sb.append(" " + this.column.toUpperCase());
      }

      int i;
      if (this.constraintClauseVector != null) {
         for(i = 0; i < this.constraintClauseVector.size(); ++i) {
            ConstraintClause tempConstraintClause = (ConstraintClause)this.constraintClauseVector.get(i);
            tempConstraintClause.setObjectContext(this.context);
            sb.append(" " + tempConstraintClause.toString());
         }
      }

      if (this.constraintClause != null) {
         this.constraintClause.setObjectContext(this.context);
         sb.append(" " + this.constraintClause.toString());
      }

      if (this.openBrace != null) {
         sb.append("\n" + this.openBrace);
      }

      if (this.createColumnVector != null) {
         if (this.createColumnVector.size() == 1) {
            CreateColumn createColumnObject = (CreateColumn)this.createColumnVector.get(0);
            createColumnObject.setObjectContext(this.context);
            sb.append("\n\t" + createColumnObject.toString());
            if (this.first != null) {
               sb.append(" " + this.first.toUpperCase());
            }

            if (this.after != null) {
               sb.append(" " + this.after.toUpperCase());
            }

            if (this.before != null) {
               sb.append(" " + this.before);
            }

            if (this.beforeOrAfterColumnName != null) {
               sb.append(" " + this.beforeOrAfterColumnName);
            }
         } else {
            for(i = 0; i < this.createColumnVector.size(); ++i) {
               CreateColumn createColumnObject = (CreateColumn)this.createColumnVector.get(i);
               createColumnObject.setObjectContext(this.context);
               if (i == 0) {
                  sb.append("\n\t" + createColumnObject.toString());
               } else if (this.commaIsSet) {
                  sb.append(",\n\t" + createColumnObject.toString());
               } else {
                  sb.append("\nADD\n\t" + createColumnObject.toString());
               }
            }
         }
      }

      if (this.closeBrace != null) {
         sb.append("\n" + this.closeBrace);
      }

      if (this.diskAttributes != null) {
         sb.append("\n\t" + this.diskAttributes.toUpperCase());
      }

      if (this.physicalCharacteristics != null) {
         sb.append("\n\t" + this.physicalCharacteristics.toUpperCase());
      }

      if (this.partition != null) {
         sb.append("\n\t" + this.partition.toString());
      }

      if (this.partitioning != null) {
         sb.append(" " + this.partitioning.toUpperCase());
      }

      if (this.key != null) {
         sb.append(" " + this.key.toUpperCase());
      }

      if (this.columnArrayList != null) {
         sb.append("\n(");

         for(i = 0; i < this.columnArrayList.size(); ++i) {
            if (i == 0) {
               sb.append("\n\t" + this.columnArrayList.get(i));
            } else {
               sb.append("," + this.columnArrayList.get(i));
            }
         }

         sb.append("\n)");
      }

      if (this.using != null) {
         sb.append("\n" + this.using.toUpperCase());
      }

      if (this.hashing != null) {
         sb.append(" " + this.hashing.toUpperCase());
      }

      if (this.overflow != null) {
         sb.append(" " + this.overflow.toUpperCase());
      }

      if (this.storage != null) {
         sb.append(" " + this.storage.toUpperCase());
      }

      if (this.physicalStorageAttributes != null) {
         sb.append(" (" + this.physicalStorageAttributes.toUpperCase() + ")");
      }

      if (this.uniqueOrPrimaryOrIndexOrFullText != null) {
         sb.append("\n" + this.uniqueOrPrimaryOrIndexOrFullText.toUpperCase());
      }

      if (this.uniqueConstraintName != null) {
         if (this.context != null) {
            String temp = this.context.getEquivalent(this.uniqueConstraintName).toString();
            sb.append(" " + temp);
         } else {
            sb.append(" " + this.uniqueConstraintName);
         }
      }

      if (this.indexColumnVector != null) {
         sb.append(" (");

         for(i = 0; i < this.indexColumnVector.size(); ++i) {
            String tempIndexColumnObject = (String)this.indexColumnVector.get(i);
            if (i == 0) {
               sb.append(tempIndexColumnObject);
            } else {
               sb.append(", " + tempIndexColumnObject);
            }
         }

         sb.append(")");
      }

      return sb.toString();
   }
}
