package com.adventnet.swissqlapi.sql.statement.alter;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintType;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.PartitionListAttributes;
import com.adventnet.swissqlapi.sql.statement.create.PrimaryOrUniqueConstraintClause;
import java.util.HashMap;
import java.util.Vector;

public class ModifyClause {
   private UserObjectContext context = null;
   private String modify;
   private String constraint;
   private String column;
   private String openBrace;
   private Vector createColumnVector;
   private String notNullStr;
   private String closedBrace;
   private String diskAttributes;
   private String physicalCharacteristics;
   private ConstraintClause constraintClause;
   private DropClause dropClause;
   private String storage;
   private String physicalStorageAttributes;
   private boolean isSQLServerAlterSet;
   private PartitionListAttributes partitionListAttributes;
   private String loggingOrNoLogging;
   private String monitoringOrNoMonitoring;
   private String allocateOrDeAllocate;
   private String allocateOrDeAllocateIdentifier;
   private String cacheOrNoCache;

   public void setModify(String modify) {
      this.modify = modify;
   }

   public void setConstraint(String constraint) {
      this.constraint = constraint;
   }

   public void setColumn(String column) {
      this.column = column;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   public void setOpenBrace(String openBrace) {
      this.openBrace = openBrace;
   }

   public void setCreateColumnVector(Vector createColumnVector) {
      this.createColumnVector = createColumnVector;
   }

   public void setConstraintClause(ConstraintClause constraintClause) {
      this.constraintClause = constraintClause;
   }

   public void setClosedBrace(String closedBrace) {
      this.closedBrace = closedBrace;
   }

   public void setDiskAttributes(String diskAttributes) {
      this.diskAttributes = diskAttributes;
   }

   public void setPhysicalCharacteristics(String physicalCharacteristics) {
      this.physicalCharacteristics = physicalCharacteristics;
   }

   public void setDropClause(DropClause dropClause) {
      this.dropClause = dropClause;
   }

   public void setStorage(String storage) {
      this.storage = storage;
   }

   public void setPhysicalStorageAttributes(String physicalStorageAttributes) {
      this.physicalStorageAttributes = physicalStorageAttributes;
   }

   public void setPartition(PartitionListAttributes partitionListAttributes) {
      this.partitionListAttributes = partitionListAttributes;
   }

   public void setLoggingOrNoLogging(String loggingOrNoLogging) {
      this.loggingOrNoLogging = loggingOrNoLogging;
   }

   public void setMonitoringOrNoMonitoring(String monitoringOrNoMonitoring) {
      this.monitoringOrNoMonitoring = monitoringOrNoMonitoring;
   }

   public void setAllocateOrDeAllocate(String allocateOrDeAllocate) {
      this.allocateOrDeAllocate = allocateOrDeAllocate;
   }

   public void setAllocateOrDeAllocateIdentifier(String allocateOrDeAllocateIdentifier) {
      this.allocateOrDeAllocateIdentifier = allocateOrDeAllocateIdentifier;
   }

   public void setCacheOrNoCache(String cacheOrNoCache) {
      this.cacheOrNoCache = cacheOrNoCache;
   }

   public void setSQLServerMultipleQueries(boolean isSQLServerAlterSet) {
      this.isSQLServerAlterSet = isSQLServerAlterSet;
   }

   public String getModify() {
      return this.modify;
   }

   public String getConstraint() {
      return this.constraint;
   }

   public String getColumn() {
      return this.column;
   }

   public ConstraintClause getConstraintClause() {
      return this.constraintClause;
   }

   public Vector getCreateColumnVector() {
      return this.createColumnVector;
   }

   public String getDiskAttributes() {
      return this.diskAttributes;
   }

   public String getPhysicalCharacteristics() {
      return this.physicalCharacteristics;
   }

   public DropClause getDropClause() {
      return this.dropClause;
   }

   public String getStorage() {
      return this.storage;
   }

   public String getPhysicalStorageAttributes() {
      return this.physicalStorageAttributes;
   }

   public PartitionListAttributes getPartition() {
      return this.partitionListAttributes;
   }

   public String getLoggingOrNoLogging() {
      return this.loggingOrNoLogging;
   }

   public String getMonitoringOrNoMonitoring() {
      return this.monitoringOrNoMonitoring;
   }

   public String getAllocateOrDeAllocate() {
      return this.allocateOrDeAllocate;
   }

   public String getAllocateOrDeAllocateIdentifier() {
      return this.allocateOrDeAllocateIdentifier;
   }

   public String getCacheOrNoCache() {
      return this.cacheOrNoCache;
   }

   public ModifyClause toOracle() throws ConvertException {
      ModifyClause tempModifyClause = this.copyObjectValues();
      String var2;
      if (tempModifyClause.getModify() != null) {
         var2 = tempModifyClause.getModify();
      }

      tempModifyClause.setColumn((String)null);
      if (tempModifyClause.getCreateColumnVector() != null) {
         Vector columnNamesVector = tempModifyClause.getCreateColumnVector();
         if (columnNamesVector.size() > 1) {
            tempModifyClause.setOpenBrace("(");
            tempModifyClause.setClosedBrace(")");
         }

         for(int i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               changeCreateColumn.toOracleString();
               String tempColumnName = changeCreateColumn.getColumnName();
               if (tempColumnName != null) {
                  boolean addQuotes = false;
                  if (tempColumnName.startsWith("\"") && tempColumnName.endsWith("\"")) {
                     tempColumnName = tempColumnName.substring(1, tempColumnName.length() - 1);
                     addQuotes = true;
                  }

                  if (tempColumnName.length() > 30) {
                     tempColumnName = tempColumnName.substring(0, 30);
                     if (addQuotes) {
                        tempColumnName = "\"" + tempColumnName + "\"";
                     }

                     changeCreateColumn.setColumnName(tempColumnName);
                  }
               }

               Vector getConstraintVector = changeCreateColumn.getConstraintClause();
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
                                 tempModifyClause.setDiskAttributes("PCTFREE " + percent);
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
                  }
               }
            }
         }

         if (tempModifyClause.getPhysicalCharacteristics() != null) {
            String var15 = tempModifyClause.getPhysicalCharacteristics();
         }
      }

      if (tempModifyClause.getDropClause() != null) {
         DropClause tempDropClause = tempModifyClause.getDropClause();
         DropClause var16 = tempDropClause.toOracle();
      }

      if (tempModifyClause.getStorage() != null) {
         var2 = tempModifyClause.getStorage();
      }

      if (tempModifyClause.getPhysicalStorageAttributes() != null) {
         var2 = tempModifyClause.getPhysicalStorageAttributes();
      }

      if (tempModifyClause.getAllocateOrDeAllocate() != null) {
         var2 = tempModifyClause.getAllocateOrDeAllocate();
      }

      if (tempModifyClause.getAllocateOrDeAllocateIdentifier() != null) {
         var2 = tempModifyClause.getAllocateOrDeAllocateIdentifier();
      }

      if (tempModifyClause.getPartition() != null) {
         PartitionListAttributes var17 = tempModifyClause.getPartition();
      }

      if (tempModifyClause.getCacheOrNoCache() != null) {
         var2 = tempModifyClause.getCacheOrNoCache();
      }

      if (tempModifyClause.getLoggingOrNoLogging() != null) {
         var2 = tempModifyClause.getLoggingOrNoLogging();
      }

      if (tempModifyClause.getMonitoringOrNoMonitoring() != null) {
         var2 = tempModifyClause.getMonitoringOrNoMonitoring();
      }

      return tempModifyClause;
   }

   public ModifyClause toMSSQLServer() throws ConvertException {
      ModifyClause tempModifyClause = this.copyObjectValues();
      if (tempModifyClause.getModify() != null) {
         tempModifyClause.setModify("ALTER COLUMN");
      }

      String notNullStr;
      if (tempModifyClause.getColumn() != null) {
         notNullStr = tempModifyClause.getColumn();
      }

      if (tempModifyClause.getCreateColumnVector() != null) {
         notNullStr = "";
         Vector columnNamesVector = tempModifyClause.getCreateColumnVector();
         if (columnNamesVector.size() > 1) {
            tempModifyClause.setSQLServerMultipleQueries(true);
         }

         for(int i = 0; i < columnNamesVector.size(); ++i) {
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
                        String fillfactor = tempModifyClause.getDiskAttributes();
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
                           tempModifyClause.setDiskAttributes((String)null);
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      tempModifyClause.setDropClause((DropClause)null);
      if (tempModifyClause.getStorage() == null && tempModifyClause.getPhysicalStorageAttributes() == null && tempModifyClause.getLoggingOrNoLogging() == null && tempModifyClause.getMonitoringOrNoMonitoring() == null && tempModifyClause.getAllocateOrDeAllocate() == null && tempModifyClause.getCacheOrNoCache() == null && tempModifyClause.getPartition() == null) {
         tempModifyClause.setStorage((String)null);
         tempModifyClause.setPhysicalStorageAttributes((String)null);
         tempModifyClause.setLoggingOrNoLogging((String)null);
         tempModifyClause.setMonitoringOrNoMonitoring((String)null);
         tempModifyClause.setAllocateOrDeAllocate((String)null);
         tempModifyClause.setAllocateOrDeAllocateIdentifier((String)null);
         tempModifyClause.setCacheOrNoCache((String)null);
         tempModifyClause.setPartition((PartitionListAttributes)null);
         return tempModifyClause;
      } else {
         throw new ConvertException();
      }
   }

   public ModifyClause toSybase() throws ConvertException {
      ModifyClause tempModifyClause = this.copyObjectValues();
      if (tempModifyClause.getModify() != null) {
         tempModifyClause.setModify("MODIFY");
      }

      String notNullStr;
      if (tempModifyClause.getColumn() != null) {
         notNullStr = tempModifyClause.getColumn();
      }

      if (tempModifyClause.getCreateColumnVector() != null) {
         notNullStr = "";
         Vector columnNamesVector = tempModifyClause.getCreateColumnVector();

         for(int i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               changeCreateColumn.toSybaseString();
               Vector getConstraintVector = changeCreateColumn.getConstraintClause();
               if (getConstraintVector != null) {
                  for(int j = 0; j < getConstraintVector.size(); ++j) {
                     ConstraintClause toSQLServerConstraintClause = (ConstraintClause)getConstraintVector.get(j);
                     ConstraintType toSQLServerConstraintType = toSQLServerConstraintClause.getConstraintType();
                     toSQLServerConstraintClause.toSybaseString();
                     if (toSQLServerConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                        int fillIntValue = 0;
                        PrimaryOrUniqueConstraintClause pcc = (PrimaryOrUniqueConstraintClause)toSQLServerConstraintType;
                        String fillfactor = tempModifyClause.getDiskAttributes();
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
                           tempModifyClause.setDiskAttributes((String)null);
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      tempModifyClause.setDropClause((DropClause)null);
      if (tempModifyClause.getStorage() == null && tempModifyClause.getPhysicalStorageAttributes() == null && tempModifyClause.getLoggingOrNoLogging() == null && tempModifyClause.getMonitoringOrNoMonitoring() == null && tempModifyClause.getAllocateOrDeAllocate() == null && tempModifyClause.getCacheOrNoCache() == null && tempModifyClause.getPartition() == null) {
         tempModifyClause.setStorage((String)null);
         tempModifyClause.setPhysicalStorageAttributes((String)null);
         tempModifyClause.setLoggingOrNoLogging((String)null);
         tempModifyClause.setMonitoringOrNoMonitoring((String)null);
         tempModifyClause.setAllocateOrDeAllocate((String)null);
         tempModifyClause.setAllocateOrDeAllocateIdentifier((String)null);
         tempModifyClause.setCacheOrNoCache((String)null);
         tempModifyClause.setPartition((PartitionListAttributes)null);
         return tempModifyClause;
      } else {
         throw new ConvertException();
      }
   }

   public ModifyClause toDB2() throws ConvertException {
      ModifyClause tempModifyClause = this.copyObjectValues();
      if (tempModifyClause.getModify() != null) {
         throw new ConvertException("Conversion Failure.. DB2 does not support Modify Clause for a table");
      } else {
         tempModifyClause.setColumn((String)null);
         tempModifyClause.setCreateColumnVector((Vector)null);
         tempModifyClause.setDiskAttributes((String)null);
         tempModifyClause.setPhysicalCharacteristics((String)null);
         tempModifyClause.setConstraintClause((ConstraintClause)null);
         tempModifyClause.setDropClause((DropClause)null);
         tempModifyClause.setStorage((String)null);
         tempModifyClause.setPhysicalStorageAttributes((String)null);
         tempModifyClause.setLoggingOrNoLogging((String)null);
         tempModifyClause.setMonitoringOrNoMonitoring((String)null);
         tempModifyClause.setAllocateOrDeAllocate((String)null);
         tempModifyClause.setAllocateOrDeAllocateIdentifier((String)null);
         tempModifyClause.setCacheOrNoCache((String)null);
         tempModifyClause.setPartition((PartitionListAttributes)null);
         return tempModifyClause;
      }
   }

   public ModifyClause toMySQL() throws ConvertException {
      ModifyClause tempModifyClause = this.copyObjectValues();
      String var2;
      if (tempModifyClause.getModify() != null) {
         var2 = tempModifyClause.getModify();
      }

      if (tempModifyClause.getColumn() != null) {
         var2 = tempModifyClause.getColumn();
      }

      if (tempModifyClause.getCreateColumnVector() != null) {
         Vector columnNamesVector = tempModifyClause.getCreateColumnVector();

         for(int i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               changeCreateColumn.toMySQLString();
               Vector getConstraintVector = changeCreateColumn.getConstraintClause();
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
                                 this.setDiskAttributes("PCTFREE " + percent);
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
                     }
                  }
               }
            }
         }
      }

      tempModifyClause.setPhysicalCharacteristics((String)null);
      tempModifyClause.setConstraintClause((ConstraintClause)null);
      tempModifyClause.setDropClause((DropClause)null);
      if (tempModifyClause.getStorage() == null && tempModifyClause.getPhysicalStorageAttributes() == null && tempModifyClause.getLoggingOrNoLogging() == null && tempModifyClause.getMonitoringOrNoMonitoring() == null && tempModifyClause.getAllocateOrDeAllocate() == null && tempModifyClause.getCacheOrNoCache() == null && tempModifyClause.getPartition() == null) {
         tempModifyClause.setStorage((String)null);
         tempModifyClause.setPhysicalStorageAttributes((String)null);
         tempModifyClause.setLoggingOrNoLogging((String)null);
         tempModifyClause.setMonitoringOrNoMonitoring((String)null);
         tempModifyClause.setAllocateOrDeAllocate((String)null);
         tempModifyClause.setAllocateOrDeAllocateIdentifier((String)null);
         tempModifyClause.setCacheOrNoCache((String)null);
         tempModifyClause.setPartition((PartitionListAttributes)null);
         return tempModifyClause;
      } else {
         throw new ConvertException();
      }
   }

   public ModifyClause toANSI() throws ConvertException {
      ModifyClause tempModifyClause = this.copyObjectValues();
      if (tempModifyClause.getModify() != null) {
         tempModifyClause.setModify("ALTER");
      }

      if (tempModifyClause.getColumn() != null) {
         String var2 = tempModifyClause.getColumn();
      }

      if (tempModifyClause.getCreateColumnVector() != null) {
         Vector columnNamesVector = tempModifyClause.getCreateColumnVector();

         for(int i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               changeCreateColumn.toANSIString();
               Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
               if (changeConstraintVector != null) {
                  for(int j = 0; j < changeConstraintVector.size(); ++j) {
                     ConstraintClause toANSISQLConstraintClause = (ConstraintClause)changeConstraintVector.get(j);
                     if (toANSISQLConstraintClause != null) {
                        toANSISQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                        toANSISQLConstraintClause.toANSIString();
                     }
                  }
               }
            }
         }
      }

      tempModifyClause.setDiskAttributes((String)null);
      tempModifyClause.setPhysicalCharacteristics((String)null);
      tempModifyClause.setConstraintClause((ConstraintClause)null);
      tempModifyClause.setDropClause((DropClause)null);
      if (tempModifyClause.getStorage() == null && tempModifyClause.getPhysicalStorageAttributes() == null && tempModifyClause.getLoggingOrNoLogging() == null && tempModifyClause.getMonitoringOrNoMonitoring() == null && tempModifyClause.getAllocateOrDeAllocate() == null && tempModifyClause.getCacheOrNoCache() == null && tempModifyClause.getPartition() == null) {
         tempModifyClause.setStorage((String)null);
         tempModifyClause.setPhysicalStorageAttributes((String)null);
         tempModifyClause.setLoggingOrNoLogging((String)null);
         tempModifyClause.setMonitoringOrNoMonitoring((String)null);
         tempModifyClause.setAllocateOrDeAllocate((String)null);
         tempModifyClause.setAllocateOrDeAllocateIdentifier((String)null);
         tempModifyClause.setCacheOrNoCache((String)null);
         tempModifyClause.setPartition((PartitionListAttributes)null);
         return tempModifyClause;
      } else {
         throw new ConvertException();
      }
   }

   public ModifyClause toInformix() throws ConvertException {
      ModifyClause tempModifyClause = this.copyObjectValues();
      String var2;
      if (tempModifyClause.getModify() != null) {
         var2 = tempModifyClause.getModify();
      }

      tempModifyClause.setOpenBrace("(");
      tempModifyClause.setClosedBrace(")");
      if (tempModifyClause.getColumn() != null) {
         var2 = tempModifyClause.getColumn();
      }

      if (tempModifyClause.getCreateColumnVector() != null) {
         Vector columnNamesVector = tempModifyClause.getCreateColumnVector();

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

      tempModifyClause.setDiskAttributes((String)null);
      tempModifyClause.setPhysicalCharacteristics((String)null);
      tempModifyClause.setConstraintClause((ConstraintClause)null);
      tempModifyClause.setDropClause((DropClause)null);
      if (tempModifyClause.getStorage() == null && tempModifyClause.getPhysicalStorageAttributes() == null && tempModifyClause.getLoggingOrNoLogging() == null && tempModifyClause.getMonitoringOrNoMonitoring() == null && tempModifyClause.getAllocateOrDeAllocate() == null && tempModifyClause.getCacheOrNoCache() == null && tempModifyClause.getPartition() == null) {
         tempModifyClause.setStorage((String)null);
         tempModifyClause.setPhysicalStorageAttributes((String)null);
         tempModifyClause.setLoggingOrNoLogging((String)null);
         tempModifyClause.setMonitoringOrNoMonitoring((String)null);
         tempModifyClause.setAllocateOrDeAllocate((String)null);
         tempModifyClause.setAllocateOrDeAllocateIdentifier((String)null);
         tempModifyClause.setCacheOrNoCache((String)null);
         tempModifyClause.setPartition((PartitionListAttributes)null);
         return tempModifyClause;
      } else {
         throw new ConvertException();
      }
   }

   public ModifyClause toBigQuery() throws ConvertException {
      ModifyClause tempModifyClause = this.copyObjectValues();
      if (tempModifyClause.getModify() != null) {
         tempModifyClause.setModify("ALTER");
      }

      if (tempModifyClause.getColumn() != null) {
         String var2 = tempModifyClause.getColumn();
      }

      if (tempModifyClause.getCreateColumnVector() != null) {
         Vector columnNamesVector = tempModifyClause.getCreateColumnVector();

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

      tempModifyClause.setDiskAttributes((String)null);
      tempModifyClause.setPhysicalCharacteristics((String)null);
      tempModifyClause.setConstraintClause((ConstraintClause)null);
      tempModifyClause.setDropClause((DropClause)null);
      if (tempModifyClause.getStorage() == null && tempModifyClause.getPhysicalStorageAttributes() == null && tempModifyClause.getLoggingOrNoLogging() == null && tempModifyClause.getMonitoringOrNoMonitoring() == null && tempModifyClause.getAllocateOrDeAllocate() == null && tempModifyClause.getCacheOrNoCache() == null && tempModifyClause.getPartition() == null) {
         tempModifyClause.setStorage((String)null);
         tempModifyClause.setPhysicalStorageAttributes((String)null);
         tempModifyClause.setLoggingOrNoLogging((String)null);
         tempModifyClause.setMonitoringOrNoMonitoring((String)null);
         tempModifyClause.setAllocateOrDeAllocate((String)null);
         tempModifyClause.setAllocateOrDeAllocateIdentifier((String)null);
         tempModifyClause.setCacheOrNoCache((String)null);
         tempModifyClause.setPartition((PartitionListAttributes)null);
         return tempModifyClause;
      } else {
         throw new ConvertException();
      }
   }

   public ModifyClause toPostgreSQL() throws ConvertException {
      ModifyClause tempModifyClause = this.copyObjectValues();
      if (tempModifyClause.getModify() != null) {
         tempModifyClause.setModify("ALTER");
      }

      if (tempModifyClause.getColumn() != null) {
         String var2 = tempModifyClause.getColumn();
      }

      if (tempModifyClause.getCreateColumnVector() != null) {
         Vector columnNamesVector = tempModifyClause.getCreateColumnVector();

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

      tempModifyClause.setDiskAttributes((String)null);
      tempModifyClause.setPhysicalCharacteristics((String)null);
      tempModifyClause.setConstraintClause((ConstraintClause)null);
      tempModifyClause.setDropClause((DropClause)null);
      if (tempModifyClause.getStorage() == null && tempModifyClause.getPhysicalStorageAttributes() == null && tempModifyClause.getLoggingOrNoLogging() == null && tempModifyClause.getMonitoringOrNoMonitoring() == null && tempModifyClause.getAllocateOrDeAllocate() == null && tempModifyClause.getCacheOrNoCache() == null && tempModifyClause.getPartition() == null) {
         tempModifyClause.setStorage((String)null);
         tempModifyClause.setPhysicalStorageAttributes((String)null);
         tempModifyClause.setLoggingOrNoLogging((String)null);
         tempModifyClause.setMonitoringOrNoMonitoring((String)null);
         tempModifyClause.setAllocateOrDeAllocate((String)null);
         tempModifyClause.setAllocateOrDeAllocateIdentifier((String)null);
         tempModifyClause.setCacheOrNoCache((String)null);
         tempModifyClause.setPartition((PartitionListAttributes)null);
         return tempModifyClause;
      } else {
         throw new ConvertException();
      }
   }

   public ModifyClause toNetezza() throws ConvertException {
      ModifyClause tempModifyClause = this.copyObjectValues();
      if (tempModifyClause.getModify() != null) {
         tempModifyClause.setModify("ALTER");
      }

      if (tempModifyClause.getColumn() != null) {
         String var2 = tempModifyClause.getColumn();
      }

      if (tempModifyClause.getCreateColumnVector() != null) {
         Vector columnNamesVector = tempModifyClause.getCreateColumnVector();

         for(int i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               changeCreateColumn.toNetezzaString();
               if (changeCreateColumn.getDatatype() != null && changeCreateColumn.getDatatype() instanceof CharacterClass && changeCreateColumn.getDatatype().getDatatypeName().equalsIgnoreCase("varchar")) {
                  tempModifyClause.setModify("MODIFY");
               } else {
                  if (changeCreateColumn.getDatatype() != null || changeCreateColumn.getDefault() == null) {
                     throw new ConvertException("/*SwisSQL Message: Netezza does not support modifying columns with datatype other than varchar*/");
                  }

                  changeCreateColumn.setDefault("SET DEFAULT");
               }

               Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
               if (changeConstraintVector != null) {
                  for(int j = 0; j < changeConstraintVector.size(); ++j) {
                     ConstraintClause toNetezzaSQLConstraintClause = (ConstraintClause)changeConstraintVector.get(j);
                     if (toNetezzaSQLConstraintClause != null) {
                        toNetezzaSQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                        toNetezzaSQLConstraintClause.toNetezzaString();
                     }
                  }
               }
            }
         }
      }

      tempModifyClause.setDiskAttributes((String)null);
      tempModifyClause.setPhysicalCharacteristics((String)null);
      tempModifyClause.setConstraintClause((ConstraintClause)null);
      tempModifyClause.setDropClause((DropClause)null);
      if (tempModifyClause.getStorage() == null && tempModifyClause.getPhysicalStorageAttributes() == null && tempModifyClause.getLoggingOrNoLogging() == null && tempModifyClause.getMonitoringOrNoMonitoring() == null && tempModifyClause.getAllocateOrDeAllocate() == null && tempModifyClause.getCacheOrNoCache() == null && tempModifyClause.getPartition() == null) {
         tempModifyClause.setStorage((String)null);
         tempModifyClause.setPhysicalStorageAttributes((String)null);
         tempModifyClause.setLoggingOrNoLogging((String)null);
         tempModifyClause.setMonitoringOrNoMonitoring((String)null);
         tempModifyClause.setAllocateOrDeAllocate((String)null);
         tempModifyClause.setAllocateOrDeAllocateIdentifier((String)null);
         tempModifyClause.setCacheOrNoCache((String)null);
         tempModifyClause.setPartition((PartitionListAttributes)null);
         return tempModifyClause;
      } else {
         throw new ConvertException();
      }
   }

   public ModifyClause toSnowflake() throws ConvertException {
      ModifyClause tempModifyClause = this.copyObjectValues();
      if (tempModifyClause.getModify() != null) {
         tempModifyClause.setModify("ALTER");
      }

      if (tempModifyClause.getColumn() != null) {
         String var2 = tempModifyClause.getColumn();
      }

      if (tempModifyClause.getCreateColumnVector() != null) {
         Vector columnNamesVector = tempModifyClause.getCreateColumnVector();

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

      tempModifyClause.setDiskAttributes((String)null);
      tempModifyClause.setPhysicalCharacteristics((String)null);
      tempModifyClause.setConstraintClause((ConstraintClause)null);
      tempModifyClause.setDropClause((DropClause)null);
      if (tempModifyClause.getStorage() == null && tempModifyClause.getPhysicalStorageAttributes() == null && tempModifyClause.getLoggingOrNoLogging() == null && tempModifyClause.getMonitoringOrNoMonitoring() == null && tempModifyClause.getAllocateOrDeAllocate() == null && tempModifyClause.getCacheOrNoCache() == null && tempModifyClause.getPartition() == null) {
         tempModifyClause.setStorage((String)null);
         tempModifyClause.setPhysicalStorageAttributes((String)null);
         tempModifyClause.setLoggingOrNoLogging((String)null);
         tempModifyClause.setMonitoringOrNoMonitoring((String)null);
         tempModifyClause.setAllocateOrDeAllocate((String)null);
         tempModifyClause.setAllocateOrDeAllocateIdentifier((String)null);
         tempModifyClause.setCacheOrNoCache((String)null);
         tempModifyClause.setPartition((PartitionListAttributes)null);
         return tempModifyClause;
      } else {
         throw new ConvertException();
      }
   }

   public ModifyClause toTeradata() throws ConvertException {
      ModifyClause tempModifyClause = this.copyObjectValues();
      if (tempModifyClause.getModify() != null) {
         tempModifyClause.setModify("ALTER");
      }

      if (tempModifyClause.getColumn() != null) {
         String var2 = tempModifyClause.getColumn();
      }

      if (tempModifyClause.getCreateColumnVector() != null) {
         Vector columnNamesVector = tempModifyClause.getCreateColumnVector();

         for(int i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               changeCreateColumn.toTeradataString();
               Vector changeConstraintVector = changeCreateColumn.getConstraintClause();
               if (changeConstraintVector != null) {
                  for(int j = 0; j < changeConstraintVector.size(); ++j) {
                     ConstraintClause toTeradataSQLConstraintClause = (ConstraintClause)changeConstraintVector.get(j);
                     if (toTeradataSQLConstraintClause != null) {
                        toTeradataSQLConstraintClause.setColumnName(changeCreateColumn.getColumnName());
                        toTeradataSQLConstraintClause.toTeradataString();
                     }
                  }
               }
            }
         }
      }

      tempModifyClause.setDiskAttributes((String)null);
      tempModifyClause.setPhysicalCharacteristics((String)null);
      tempModifyClause.setConstraintClause((ConstraintClause)null);
      tempModifyClause.setDropClause((DropClause)null);
      if (tempModifyClause.getStorage() == null && tempModifyClause.getPhysicalStorageAttributes() == null && tempModifyClause.getLoggingOrNoLogging() == null && tempModifyClause.getMonitoringOrNoMonitoring() == null && tempModifyClause.getAllocateOrDeAllocate() == null && tempModifyClause.getCacheOrNoCache() == null && tempModifyClause.getPartition() == null) {
         tempModifyClause.setStorage((String)null);
         tempModifyClause.setPhysicalStorageAttributes((String)null);
         tempModifyClause.setLoggingOrNoLogging((String)null);
         tempModifyClause.setMonitoringOrNoMonitoring((String)null);
         tempModifyClause.setAllocateOrDeAllocate((String)null);
         tempModifyClause.setAllocateOrDeAllocateIdentifier((String)null);
         tempModifyClause.setCacheOrNoCache((String)null);
         tempModifyClause.setPartition((PartitionListAttributes)null);
         return tempModifyClause;
      } else {
         throw new ConvertException();
      }
   }

   public String removeIndent(String str) {
      str = str.replace('\n', ' ');
      str = str.replace('\t', ' ');
      return str;
   }

   public ModifyClause copyObjectValues() {
      ModifyClause dupModifyClause = new ModifyClause();
      dupModifyClause.setModify(this.getModify());
      dupModifyClause.setConstraint(this.getConstraint());
      dupModifyClause.setColumn(this.getColumn());
      dupModifyClause.setCreateColumnVector(this.getCreateColumnVector());
      dupModifyClause.setDiskAttributes(this.getDiskAttributes());
      dupModifyClause.setPhysicalCharacteristics(this.getPhysicalCharacteristics());
      dupModifyClause.setConstraintClause(this.getConstraintClause());
      dupModifyClause.setDropClause(this.getDropClause());
      dupModifyClause.setStorage(this.getStorage());
      dupModifyClause.setPhysicalStorageAttributes(this.getPhysicalStorageAttributes());
      dupModifyClause.setLoggingOrNoLogging(this.getLoggingOrNoLogging());
      dupModifyClause.setMonitoringOrNoMonitoring(this.getMonitoringOrNoMonitoring());
      dupModifyClause.setAllocateOrDeAllocate(this.getAllocateOrDeAllocate());
      dupModifyClause.setAllocateOrDeAllocateIdentifier(this.getAllocateOrDeAllocateIdentifier());
      dupModifyClause.setCacheOrNoCache(this.getCacheOrNoCache());
      dupModifyClause.setPartition(this.getPartition());
      dupModifyClause.setObjectContext(this.context);
      return dupModifyClause;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.modify != null) {
         sb.append(this.modify.toUpperCase());
      }

      if (this.constraint != null) {
         sb.append(" " + this.constraint.toUpperCase());
      }

      if (this.openBrace != null) {
         sb.append("\n" + this.openBrace);
      }

      if (this.createColumnVector != null) {
         for(int i = 0; i < this.createColumnVector.size(); ++i) {
            CreateColumn tempCreateColumn = (CreateColumn)this.createColumnVector.get(i);
            tempCreateColumn.setObjectContext(this.context);
            if (i == 0) {
               sb.append("\n\t" + tempCreateColumn.toString());
            } else if (this.isSQLServerAlterSet) {
               sb.append(",\nALTER\n\t" + tempCreateColumn.toString());
            } else {
               sb.append(",\n\t" + tempCreateColumn.toString());
            }
         }
      }

      if (this.closedBrace != null) {
         sb.append("\n" + this.closedBrace);
      }

      if (this.dropClause != null) {
         this.dropClause.setObjectContext(this.context);
         sb.append(this.dropClause.toString());
      }

      if (this.storage != null) {
         sb.append(this.storage.toUpperCase());
      }

      if (this.physicalStorageAttributes != null) {
         sb.append("( " + this.physicalStorageAttributes.toUpperCase() + ")");
      }

      if (this.loggingOrNoLogging != null) {
         sb.append(" " + this.loggingOrNoLogging.toUpperCase());
      }

      if (this.monitoringOrNoMonitoring != null) {
         sb.append(" " + this.monitoringOrNoMonitoring.toUpperCase());
      }

      if (this.partitionListAttributes != null) {
         sb.append(this.partitionListAttributes);
      }

      if (this.allocateOrDeAllocate != null) {
         sb.append(" " + this.allocateOrDeAllocate.toUpperCase());
      }

      if (this.allocateOrDeAllocateIdentifier != null) {
         sb.append(" " + this.allocateOrDeAllocateIdentifier);
      }

      if (this.cacheOrNoCache != null) {
         sb.append(" " + this.cacheOrNoCache.toUpperCase());
      }

      return sb.toString();
   }
}
