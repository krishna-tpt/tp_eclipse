package com.adventnet.swissqlapi.sql.statement.alter;

import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintType;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.sql.statement.create.DefaultConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.PartitionListAttributes;
import com.adventnet.swissqlapi.sql.statement.create.PhysicalAttributesClause;
import com.adventnet.swissqlapi.sql.statement.create.PrimaryOrUniqueConstraintClause;
import java.util.Vector;

public class AlterTable {
   private UserObjectContext context = null;
   private AddClause addClause;
   private ModifyClause modifyClause;
   private DropClause dropClause;
   private AlterColumnClause alterColumnClause;
   private String data;
   private String capture;
   private String noneOrChanges;
   private String rename;
   private String toTableName;
   private String asOrTo;
   private String renameColumn;
   private String tableName;
   private String toColumn;
   private String columnName;
   private String overflow;
   private String storage;
   private String storageClauseString;
   private String move;
   private String truncate;
   private String split;
   private String exchange;
   private PartitionListAttributes partitionListAttributes;
   private String allocateExtent;
   private String allocateExtentIdentifier;
   private String deAllocateUnused;
   private String deAllocateUnusedIdentifier;
   private String checkOrNoCheck;
   private String constraint;
   private String constraintAll;
   private Vector constraintNameVector;
   private String owner;
   private String ownerTo;
   private String ownerName;
   private String change;
   private String changeColumn;
   private CreateColumn createColumn;
   private Vector triggerNameVector;
   private String enableOrDisable;
   private String trigger;
   private String triggerAll;
   private String with;
   private String comma;
   private PhysicalAttributesClause physicalAttributesClause;
   private Vector physicalAttributesClauseVector;
   private DatatypeMapping datatypeMapping;
   private String alterTableName;
   private String tableOption;
   private String tableOptionParameter;
   private String sp_renameStmt = null;
   private String origColumn = null;

   public void setAddClause(AddClause addClause) {
      this.addClause = addClause;
   }

   public void setModifyClause(ModifyClause modifyClause) {
      this.modifyClause = modifyClause;
   }

   public void setDropClause(DropClause dropClause) {
      this.dropClause = dropClause;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   public void setAlterColumnClause(AlterColumnClause alterColumnClause) {
      this.alterColumnClause = alterColumnClause;
   }

   public void setData(String data) {
      this.data = data;
   }

   public void setCapture(String capture) {
      this.capture = capture;
   }

   public void setNoneOrChanges(String noneOrChanges) {
      this.noneOrChanges = noneOrChanges;
   }

   public void setRename(String rename) {
      this.rename = rename;
   }

   public void setToTableName(String toTableName) {
      this.toTableName = toTableName;
   }

   public void setAsOrTo(String asOrTo) {
      this.asOrTo = asOrTo;
   }

   public void setRenameColumn(String renameColumn) {
      this.renameColumn = renameColumn;
   }

   public void setTableName(String tableName) {
      this.tableName = tableName;
   }

   public void setAlterTableName(String alterTableName) {
      this.alterTableName = alterTableName;
   }

   public void setToColumn(String toColumn) {
      this.toColumn = toColumn;
   }

   public void setColumnName(String columnName) {
      this.columnName = columnName;
   }

   public void setOverflow(String overflow) {
      this.overflow = overflow;
   }

   public void setStorage(String storage) {
      this.storage = storage;
   }

   public void setStorageClause(String storageClauseString) {
      this.storageClauseString = storageClauseString;
   }

   public void setMove(String move) {
      this.move = move;
   }

   public void setTruncate(String truncate) {
      this.truncate = truncate;
   }

   public void setSplit(String split) {
      this.split = split;
   }

   public void setExchange(String exchange) {
      this.exchange = exchange;
   }

   public void setPartition(PartitionListAttributes partitionListAttributes) {
      this.partitionListAttributes = partitionListAttributes;
   }

   public void setAllocateExtent(String allocateExtent) {
      this.allocateExtent = allocateExtent;
   }

   public void setAllocateExtentIdentifier(String allocateExtentIdentifier) {
      this.allocateExtentIdentifier = allocateExtentIdentifier;
   }

   public void setDeAllocateUnused(String deAllocateUnused) {
      this.deAllocateUnused = deAllocateUnused;
   }

   public void setDeAllocateUnusedIdentifier(String deAllocateUnusedIdentifier) {
      this.deAllocateUnusedIdentifier = deAllocateUnusedIdentifier;
   }

   public void setCheckOrNoCheck(String checkOrNoCheck) {
      this.checkOrNoCheck = checkOrNoCheck;
   }

   public void setConstraint(String constraint) {
      this.constraint = constraint;
   }

   public void setConstraintAll(String constraintAll) {
      this.constraintAll = constraintAll;
   }

   public void setConstraintNameVector(Vector constraintNameVector) {
      this.constraintNameVector = constraintNameVector;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public void setOwnerTo(String ownerTo) {
      this.ownerTo = ownerTo;
   }

   public void setOwnerName(String ownerName) {
      this.ownerName = ownerName;
   }

   public void setChange(String change) {
      this.change = change;
   }

   public void setChangeColumn(String changeColumn) {
      this.changeColumn = changeColumn;
   }

   public void setCreateColumn(CreateColumn createColumn) {
      this.createColumn = createColumn;
   }

   public void setTriggerNameVector(Vector triggerNameVector) {
      this.triggerNameVector = triggerNameVector;
   }

   public void setEnableOrDisable(String enableOrDisable) {
      this.enableOrDisable = enableOrDisable;
   }

   public void setTrigger(String trigger) {
      this.trigger = trigger;
   }

   public void setTriggerAll(String triggerAll) {
      this.triggerAll = triggerAll;
   }

   public void setWith(String with) {
      this.with = with;
   }

   public void setComma(String comma) {
      this.comma = comma;
   }

   public void setPhysicalAttributesClause(PhysicalAttributesClause physicalAttributesClause) {
      this.physicalAttributesClause = physicalAttributesClause;
   }

   public void setPhysicalAttributesClauseVector(Vector physicalAttributesClauseVector) {
      this.physicalAttributesClauseVector = physicalAttributesClauseVector;
   }

   public void setDatatypeMapping(DatatypeMapping mapping) {
      this.datatypeMapping = mapping;
   }

   public void setTableOption(String tableOption) {
      this.tableOption = tableOption;
   }

   public void setTableOptionParameter(String tableOptionParam) {
      this.tableOptionParameter = tableOptionParam;
   }

   public void setsp_renameStmt(String sp_renameStmt) {
      this.sp_renameStmt = sp_renameStmt;
   }

   public void setOrigColumn(String origCol) {
      this.origColumn = origCol;
   }

   public AddClause getAddClause() {
      return this.addClause;
   }

   public ModifyClause getModifyClause() {
      return this.modifyClause;
   }

   public DropClause getDropClause() {
      return this.dropClause;
   }

   public AlterColumnClause getAlterColumnClause() {
      return this.alterColumnClause;
   }

   public String getData() {
      return this.data;
   }

   public String getCapture() {
      return this.capture;
   }

   public String getNoneOrChanges() {
      return this.noneOrChanges;
   }

   public String getRename() {
      return this.rename;
   }

   public String getAsOrTo() {
      return this.asOrTo;
   }

   public String getRenameColumn() {
      return this.renameColumn;
   }

   public String getTableName() {
      return this.tableName;
   }

   public String getToColumn() {
      return this.toColumn;
   }

   public String getColumnName() {
      return this.columnName;
   }

   public String getOverflow() {
      return this.overflow;
   }

   public String getStorage() {
      return this.storage;
   }

   public String getStorageClause() {
      return this.storageClauseString;
   }

   public String getMove() {
      return this.move;
   }

   public String getTruncate() {
      return this.truncate;
   }

   public String getSplit() {
      return this.split;
   }

   public String getExchange() {
      return this.exchange;
   }

   public PartitionListAttributes getPartition() {
      return this.partitionListAttributes;
   }

   public String getAllocateExtent() {
      return this.allocateExtent;
   }

   public String getAllocateExtentIdentifier() {
      return this.allocateExtentIdentifier;
   }

   public String getDeAllocateUnused() {
      return this.deAllocateUnused;
   }

   public String getDeAllocateUnusedIdentifier() {
      return this.deAllocateUnusedIdentifier;
   }

   public String getCheckOrNoCheck() {
      return this.checkOrNoCheck;
   }

   public String getConstraint() {
      return this.constraint;
   }

   public String getConstraintAll() {
      return this.constraintAll;
   }

   public Vector getConstraintNameVector() {
      return this.constraintNameVector;
   }

   public String getOwner() {
      return this.owner;
   }

   public String getOwnerTo() {
      return this.ownerTo;
   }

   public String getOwnerName() {
      return this.ownerName;
   }

   public String getChange() {
      return this.change;
   }

   public String getChangeColumn() {
      return this.changeColumn;
   }

   public CreateColumn getCreateColumn() {
      return this.createColumn;
   }

   public Vector getTriggerNameVector() {
      return this.triggerNameVector;
   }

   public String getEnableOrDisable() {
      return this.enableOrDisable;
   }

   public String getTrigger() {
      return this.trigger;
   }

   public String getTriggerAll() {
      return this.triggerAll;
   }

   public PhysicalAttributesClause getPhysicalAttributesClause() {
      return this.physicalAttributesClause;
   }

   public Vector getPhysicalAttributesClauseVector() {
      return this.physicalAttributesClauseVector;
   }

   public String getWith() {
      return this.with;
   }

   public String getTableOption() {
      return this.tableOption;
   }

   public String getTableOptionParameter() {
      return this.tableOptionParameter;
   }

   public String getsp_renameStmt() {
      return this.sp_renameStmt;
   }

   public String getOrigColumn() {
      return this.origColumn;
   }

   public AlterTable toOracle() throws ConvertException {
      AlterTable tempAlterTable = this.copyObjectValues();
      if (tempAlterTable.getAddClause() != null) {
         AddClause tempAddClause = tempAlterTable.getAddClause();
         AddClause oracleAddClause = tempAddClause.toOracle();
         tempAlterTable.setAddClause(oracleAddClause);
      }

      tempAlterTable.setData((String)null);
      tempAlterTable.setCapture((String)null);
      tempAlterTable.setNoneOrChanges((String)null);
      if (tempAlterTable.getModifyClause() != null) {
         ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
         ModifyClause oracleModifyClause = tempModifyClause.toOracle();
         tempAlterTable.setModifyClause(oracleModifyClause);
      }

      if (tempAlterTable.getDropClause() != null) {
         DropClause tempDropClause = tempAlterTable.getDropClause();
         DropClause oracleDropClause = tempDropClause.toOracle();
         tempAlterTable.setDropClause(oracleDropClause);
      }

      if (tempAlterTable.getAlterColumnClause() != null) {
         AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
         AlterColumnClause oracleAlterColumnClause = tempAlterColumnClause.toOracle();
         tempAlterTable.setAlterColumnClause(oracleAlterColumnClause);
      }

      String tempAsOrTo;
      if (tempAlterTable.getRename() != null) {
         tempAsOrTo = tempAlterTable.getRename();
      }

      if (tempAlterTable.getAsOrTo() != null) {
         tempAsOrTo = tempAlterTable.getAsOrTo();
         tempAsOrTo = tempAsOrTo.trim();
         if (tempAsOrTo.equalsIgnoreCase("AS")) {
            tempAlterTable.setAsOrTo("TO");
         } else {
            tempAlterTable.setAsOrTo(tempAsOrTo);
         }
      }

      if (tempAlterTable.getRenameColumn() != null) {
         tempAsOrTo = tempAlterTable.getRenameColumn();
      }

      if (this.toTableName != null) {
         tempAlterTable.setToTableName("AS");
      }

      if (tempAlterTable.getTableName() != null) {
         tempAsOrTo = tempAlterTable.getTableName();
      }

      if (tempAlterTable.getToColumn() != null) {
         tempAsOrTo = tempAlterTable.getToColumn();
      }

      if (tempAlterTable.getColumnName() != null) {
         tempAsOrTo = tempAlterTable.getColumnName();
      }

      if (tempAlterTable.getOverflow() != null) {
         tempAsOrTo = tempAlterTable.getOverflow();
      }

      if (tempAlterTable.getStorage() != null) {
         tempAsOrTo = tempAlterTable.getStorage();
      }

      if (tempAlterTable.getStorageClause() != null) {
         tempAsOrTo = tempAlterTable.getStorageClause();
      }

      if (tempAlterTable.getMove() != null) {
         tempAsOrTo = tempAlterTable.getMove();
      }

      if (tempAlterTable.getTruncate() != null) {
         tempAsOrTo = tempAlterTable.getTruncate();
      }

      if (tempAlterTable.getSplit() != null) {
         tempAsOrTo = tempAlterTable.getSplit();
      }

      if (tempAlterTable.getExchange() != null) {
         tempAsOrTo = tempAlterTable.getExchange();
      }

      if (tempAlterTable.getPartition() != null) {
         PartitionListAttributes var28 = tempAlterTable.getPartition();
      }

      if (tempAlterTable.getAllocateExtent() != null) {
         tempAsOrTo = tempAlterTable.getAllocateExtent();
      }

      if (tempAlterTable.getAllocateExtentIdentifier() != null) {
         tempAsOrTo = tempAlterTable.getAllocateExtentIdentifier();
      }

      if (tempAlterTable.getDeAllocateUnused() != null) {
         tempAsOrTo = tempAlterTable.getDeAllocateUnused();
      }

      if (tempAlterTable.getDeAllocateUnusedIdentifier() != null) {
         tempAsOrTo = tempAlterTable.getDeAllocateUnusedIdentifier();
      }

      if (tempAlterTable.getCheckOrNoCheck() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterTable.setConstraint((String)null);
         tempAlterTable.setConstraintAll((String)null);
         tempAlterTable.setConstraintNameVector((Vector)null);
         if (tempAlterTable.getOwner() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            tempAlterTable.setOwnerTo((String)null);
            tempAlterTable.setOwnerName((String)null);
            if (tempAlterTable.getChange() != null) {
               tempAlterTable.setChange("MODIFY");
            }

            tempAlterTable.setChangeColumn((String)null);
            if (tempAlterTable.getCreateColumn() != null) {
               CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
               tempCreateColumn.toOracleString();
            }

            if (tempAlterTable.getEnableOrDisable() != null) {
               tempAlterTable.setEnableOrDisable(this.enableOrDisable);
               tempAlterTable.setToTableName((String)null);
               tempAlterTable.setTrigger((String)null);
               StringBuffer sb = new StringBuffer();
               if (this.triggerNameVector != null) {
                  for(int i = 0; i < this.triggerNameVector.size(); ++i) {
                     if (this.triggerNameVector.elementAt(i) instanceof String) {
                        if (i > 0) {
                           sb.append(",");
                        }

                        sb.append((String)this.triggerNameVector.get(i));
                     } else if (this.triggerNameVector.elementAt(i) instanceof CreateColumn) {
                        CreateColumn createColumn = (CreateColumn)this.triggerNameVector.get(i);
                        if (i > 0) {
                           sb.append(",");
                        }

                        sb.append(createColumn.toString());
                     }
                  }

                  tempAlterTable.setTableName(sb.toString());
               }
            }

            Vector colVector;
            Vector columnVector;
            if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
               columnVector = new Vector();
               colVector = tempAlterTable.getPhysicalAttributesClauseVector();

               for(int i = 0; i < colVector.size(); ++i) {
                  PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)colVector.get(i);
                  PhysicalAttributesClause oraclePhysicalAttributesClause = tempPhysicalAttributesClause.toOracle();
                  columnVector.add(oraclePhysicalAttributesClause);
               }

               tempAlterTable.setPhysicalAttributesClauseVector(columnVector);
            }

            tempAlterTable.setWith((String)null);
            tempAlterTable.setTriggerAll((String)null);
            tempAlterTable.setTriggerNameVector((Vector)null);
            if (tempAlterTable.getAddClause() != null) {
               columnVector = tempAlterTable.getAddClause().getCreateColumnVector();
               colVector = new Vector();
               ModifyClause modify = new ModifyClause();
               AddClause addClause = new AddClause();
               Vector pkClauseVector = new Vector();
               if (columnVector != null) {
                  boolean isModify = false;
                  boolean isAdd = false;

                  for(int i = 0; i < columnVector.size(); ++i) {
                     Vector constrClauseVector = ((CreateColumn)columnVector.get(i)).getConstraintClause();
                     if (constrClauseVector != null) {
                        for(int j = 0; j < constrClauseVector.size(); ++j) {
                           ConstraintType constrType = ((ConstraintClause)constrClauseVector.get(j)).getConstraintType();
                           if (constrType != null && constrType instanceof DefaultConstraintClause) {
                              if (((DefaultConstraintClause)constrType).getForClause() != null) {
                                 modify.setModify("MODIFY");
                                 modify.setClosedBrace(")");
                                 modify.setOpenBrace("(");
                                 CreateColumn column = new CreateColumn();
                                 String colName = ((DefaultConstraintClause)constrType).getColumnName();
                                 if (colName.indexOf("[") != -1) {
                                    colName = colName.substring(1, colName.length() - 1);
                                 }

                                 column.setColumnName(colName);
                                 column.setDefault("DEFAULT");
                                 column.setDefaultValue(((DefaultConstraintClause)constrType).getDefaultValue());
                                 colVector.add(column);
                                 isModify = true;
                              }
                           } else if (constrType != null) {
                              pkClauseVector.add((ConstraintClause)constrClauseVector.get(j));
                              isAdd = true;
                           }
                        }
                     }
                  }

                  if (isModify) {
                     modify.setCreateColumnVector(colVector);
                     if (isAdd) {
                        addClause.setAdd("ADD");
                        addClause.setConstraintClauseVector(pkClauseVector);
                        tempAlterTable.setAddClause(addClause);
                     } else {
                        tempAlterTable.setAddClause((AddClause)null);
                     }

                     tempAlterTable.setModifyClause(modify);
                  }
               }
            }

            return tempAlterTable;
         }
      }
   }

   public AlterTable toMSSQLServer() throws ConvertException {
      AlterTable tempAlterTable = this.copyObjectValues();
      if (tempAlterTable.getAddClause() != null) {
         AddClause tempAddClause = tempAlterTable.getAddClause();
         AddClause msSQLServerAddClause = tempAddClause.toMSSQLServer();
         tempAlterTable.setAddClause(msSQLServerAddClause);
      }

      tempAlterTable.setData((String)null);
      tempAlterTable.setCapture((String)null);
      tempAlterTable.setNoneOrChanges((String)null);
      if (tempAlterTable.getModifyClause() != null) {
         ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
         ModifyClause msSQLServerModifyClause = tempModifyClause.toMSSQLServer();
         tempAlterTable.setModifyClause(msSQLServerModifyClause);
      }

      if (tempAlterTable.getDropClause() != null) {
         DropClause tempDropClause = tempAlterTable.getDropClause();
         DropClause msSQLServerDropClause = tempDropClause.toMSSQLServer();
         tempAlterTable.setDropClause(msSQLServerDropClause);
      }

      if (tempAlterTable.getAlterColumnClause() != null) {
         AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
         AlterColumnClause msSQLServerAlterColumnClause = tempAlterColumnClause.toMSSQLServer();
         tempAlterTable.setAlterColumnClause(msSQLServerAlterColumnClause);
      }

      if (tempAlterTable.getRename() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterTable.setAsOrTo((String)null);
         tempAlterTable.setToTableName((String)null);
         tempAlterTable.setRenameColumn((String)null);
         tempAlterTable.setTableName((String)null);
         tempAlterTable.setToColumn((String)null);
         tempAlterTable.setColumnName((String)null);
         if (tempAlterTable.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            tempAlterTable.setStorage((String)null);
            tempAlterTable.setStorageClause((String)null);
            if (tempAlterTable.getMove() == null && tempAlterTable.getTruncate() == null && tempAlterTable.getSplit() == null && tempAlterTable.getExchange() == null && tempAlterTable.getAllocateExtent() == null && tempAlterTable.getDeAllocateUnused() == null) {
               tempAlterTable.setTruncate((String)null);
               tempAlterTable.setSplit((String)null);
               tempAlterTable.setExchange((String)null);
               tempAlterTable.setPartition((PartitionListAttributes)null);
               tempAlterTable.setAllocateExtent((String)null);
               tempAlterTable.setAllocateExtentIdentifier((String)null);
               tempAlterTable.setDeAllocateUnused((String)null);
               tempAlterTable.setDeAllocateUnusedIdentifier((String)null);
               if (tempAlterTable.getOwner() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  tempAlterTable.setOwnerTo((String)null);
                  tempAlterTable.setOwnerName((String)null);
                  String tempWith;
                  if (tempAlterTable.getChange() != null) {
                     tempAlterTable.setChange("ALTER");
                     tempAlterTable.setChangeColumn("COLUMN");
                     if (tempAlterTable.getOrigColumn() != null) {
                        tempWith = tempAlterTable.getOrigColumn();
                        if (tempWith.startsWith("`") && tempWith.endsWith("`")) {
                           tempWith = tempWith.substring(1, tempWith.length() - 1);
                           tempAlterTable.setOrigColumn(tempWith);
                        }
                     }
                  }

                  if (tempAlterTable.getCreateColumn() != null) {
                     CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
                     tempCreateColumn.toMSSQLServerString();
                  }

                  if (tempAlterTable.getCheckOrNoCheck() != null) {
                     tempWith = tempAlterTable.getCheckOrNoCheck();
                  }

                  if (tempAlterTable.getConstraint() != null) {
                     tempWith = tempAlterTable.getConstraint();
                  }

                  if (tempAlterTable.getConstraintAll() != null) {
                     tempWith = tempAlterTable.getConstraintAll();
                  }

                  int i;
                  Vector msSQLServerPhysicalAttributesClauseVector;
                  if (tempAlterTable.getConstraintNameVector() != null) {
                     msSQLServerPhysicalAttributesClauseVector = tempAlterTable.getConstraintNameVector();

                     for(i = 0; i < msSQLServerPhysicalAttributesClauseVector.size(); ++i) {
                        String var4 = (String)msSQLServerPhysicalAttributesClauseVector.get(i);
                     }
                  }

                  if (tempAlterTable.getEnableOrDisable() != null) {
                     tempWith = tempAlterTable.getEnableOrDisable();
                  }

                  if (tempAlterTable.getTrigger() != null) {
                     tempWith = tempAlterTable.getTriggerAll();
                  }

                  if (tempAlterTable.getTriggerAll() != null) {
                     tempWith = tempAlterTable.getTriggerAll();
                  }

                  if (tempAlterTable.getTriggerNameVector() != null) {
                     msSQLServerPhysicalAttributesClauseVector = tempAlterTable.getTriggerNameVector();

                     for(i = 0; i < msSQLServerPhysicalAttributesClauseVector.size(); ++i) {
                        CreateColumn tempCreateColumn = (CreateColumn)msSQLServerPhysicalAttributesClauseVector.get(i);
                        tempCreateColumn.toMSSQLServerString();
                     }
                  }

                  if (this.getWith() != null) {
                     tempWith = this.getWith();
                     tempAlterTable.setWith(tempWith);
                  }

                  if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
                     msSQLServerPhysicalAttributesClauseVector = new Vector();
                     Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();

                     PhysicalAttributesClause msSQLServerPhysicalAttributesClause;
                     for(int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                        msSQLServerPhysicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributesClauseVector.get(i);
                        PhysicalAttributesClause msSQLServerPhysicalAttributesClause = msSQLServerPhysicalAttributesClause.toMSSQLServer();
                        msSQLServerPhysicalAttributesClauseVector.add(msSQLServerPhysicalAttributesClause);
                     }

                     tempAlterTable.setPhysicalAttributesClauseVector(msSQLServerPhysicalAttributesClauseVector);
                     PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributesClauseVector.get(0);
                     msSQLServerPhysicalAttributesClause = tempPhysicalAttributesClause.toMSSQLServer();
                     if (msSQLServerPhysicalAttributesClause.getWith() == null && (msSQLServerPhysicalAttributesClause.getFillFactor() != null || msSQLServerPhysicalAttributesClause.getPadIndex() != null || msSQLServerPhysicalAttributesClause.getDiskAttr() != null && msSQLServerPhysicalAttributesClause.getDiskAttr().get("IGNORE_DUP_KEY") != null || msSQLServerPhysicalAttributesClause.getDropExisting() != null || msSQLServerPhysicalAttributesClause.getStatisticsNoreCompute() != null)) {
                        tempAlterTable.setWith("WITH");
                     }
                  }

                  tempAlterTable.setComma(",");
                  return tempAlterTable;
               }
            } else {
               throw new ConvertException();
            }
         }
      }
   }

   public AlterTable toSybase() throws ConvertException {
      AlterTable tempAlterTable = this.copyObjectValues();
      if (tempAlterTable.getAddClause() != null) {
         AddClause tempAddClause = tempAlterTable.getAddClause();
         AddClause sybaseAddClause = tempAddClause.toSybase();
         tempAlterTable.setAddClause(sybaseAddClause);
      }

      tempAlterTable.setData((String)null);
      tempAlterTable.setCapture((String)null);
      tempAlterTable.setNoneOrChanges((String)null);
      if (tempAlterTable.getModifyClause() != null) {
         ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
         ModifyClause sybaseModifyClause = tempModifyClause.toSybase();
         tempAlterTable.setModifyClause(sybaseModifyClause);
      }

      if (tempAlterTable.getDropClause() != null) {
         DropClause tempDropClause = tempAlterTable.getDropClause();
         DropClause sybaseDropClause = tempDropClause.toSybase();
         tempAlterTable.setDropClause(sybaseDropClause);
      }

      if (tempAlterTable.getAlterColumnClause() != null) {
         AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
         AlterColumnClause sybaseAlterColumnClause = tempAlterColumnClause.toSybase();
         tempAlterTable.setAlterColumnClause(sybaseAlterColumnClause);
      }

      String tempWith;
      if (tempAlterTable.getRename() != null) {
         tempWith = tempAlterTable.getRename();
      }

      if (tempAlterTable.getAsOrTo() != null) {
         tempWith = tempAlterTable.getAsOrTo();
         tempWith = tempWith.trim();
         if (tempWith.equalsIgnoreCase("AS")) {
            tempAlterTable.setAsOrTo("TO");
         } else {
            tempAlterTable.setAsOrTo(tempWith);
         }
      }

      if (tempAlterTable.getTableName() != null) {
         tempWith = tempAlterTable.getTableName();
      }

      if (tempAlterTable.getToColumn() != null) {
         tempWith = tempAlterTable.getToColumn();
      }

      if (tempAlterTable.getColumnName() != null) {
         tempWith = tempAlterTable.getColumnName();
      }

      tempAlterTable.setRenameColumn((String)null);
      tempAlterTable.setToTableName((String)null);
      if (tempAlterTable.getOverflow() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterTable.setStorage((String)null);
         tempAlterTable.setStorageClause((String)null);
         if (tempAlterTable.getMove() == null && tempAlterTable.getTruncate() == null && tempAlterTable.getSplit() == null && tempAlterTable.getExchange() == null && tempAlterTable.getAllocateExtent() == null && tempAlterTable.getDeAllocateUnused() == null) {
            tempAlterTable.setTruncate((String)null);
            tempAlterTable.setSplit((String)null);
            tempAlterTable.setExchange((String)null);
            tempAlterTable.setPartition((PartitionListAttributes)null);
            tempAlterTable.setAllocateExtent((String)null);
            tempAlterTable.setAllocateExtentIdentifier((String)null);
            tempAlterTable.setDeAllocateUnused((String)null);
            tempAlterTable.setDeAllocateUnusedIdentifier((String)null);
            if (tempAlterTable.getOwner() != null) {
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               tempAlterTable.setOwnerTo((String)null);
               tempAlterTable.setOwnerName((String)null);
               if (tempAlterTable.getChange() != null) {
                  tempAlterTable.setChange("ALTER");
                  tempAlterTable.setChangeColumn("COLUMN");
               }

               if (tempAlterTable.getCreateColumn() != null) {
                  CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
                  tempCreateColumn.toSybaseString();
               }

               if (tempAlterTable.getCheckOrNoCheck() != null) {
                  tempWith = tempAlterTable.getCheckOrNoCheck();
               }

               if (tempAlterTable.getConstraint() != null) {
                  tempWith = tempAlterTable.getConstraint();
               }

               if (tempAlterTable.getConstraintAll() != null) {
                  tempWith = tempAlterTable.getConstraintAll();
               }

               int i;
               Vector tempTriggerNameVector;
               if (tempAlterTable.getConstraintNameVector() != null) {
                  tempTriggerNameVector = tempAlterTable.getConstraintNameVector();

                  for(i = 0; i < tempTriggerNameVector.size(); ++i) {
                     String var4 = (String)tempTriggerNameVector.get(i);
                  }
               }

               if (tempAlterTable.getEnableOrDisable() != null) {
                  tempWith = tempAlterTable.getEnableOrDisable();
               }

               if (tempAlterTable.getTrigger() != null) {
                  tempWith = tempAlterTable.getTriggerAll();
               }

               if (tempAlterTable.getTriggerAll() != null) {
                  tempWith = tempAlterTable.getTriggerAll();
               }

               if (tempAlterTable.getTriggerNameVector() != null) {
                  tempTriggerNameVector = tempAlterTable.getTriggerNameVector();

                  for(i = 0; i < tempTriggerNameVector.size(); ++i) {
                     CreateColumn tempCreateColumn = (CreateColumn)tempTriggerNameVector.get(i);
                     tempCreateColumn.toSybaseString();
                  }
               }

               if (this.getWith() != null) {
                  tempWith = this.getWith();
                  tempAlterTable.setWith(tempWith);
               }

               if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
                  tempAlterTable.setPhysicalAttributesClauseVector((Vector)null);
               }

               tempAlterTable.setComma((String)null);
               return tempAlterTable;
            }
         } else {
            throw new ConvertException();
         }
      }
   }

   public AlterTable toDB2() throws ConvertException {
      AlterTable tempAlterTable = this.copyObjectValues();
      if (tempAlterTable.getAddClause() != null) {
         AddClause tempAddClause = tempAlterTable.getAddClause();
         tempAddClause.setDatatypeMapping(this.datatypeMapping);
         tempAddClause.setStmtTableName(this.alterTableName);
         AddClause db2AddClause = tempAddClause.toDB2();
         tempAlterTable.setAddClause(db2AddClause);
      }

      String var7;
      if (tempAlterTable.getData() != null) {
         var7 = tempAlterTable.getData();
      }

      if (tempAlterTable.getCapture() != null) {
         var7 = tempAlterTable.getCapture();
      }

      if (tempAlterTable.getNoneOrChanges() != null) {
         var7 = tempAlterTable.getNoneOrChanges();
      }

      if (tempAlterTable.getDropClause() != null) {
         DropClause tempDropClause = tempAlterTable.getDropClause();
         DropClause db2DropClause = tempDropClause.toDB2();
         tempAlterTable.setDropClause(db2DropClause);
      }

      AlterColumnClause alterClause;
      if (tempAlterTable.getAlterColumnClause() != null) {
         AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
         alterClause = tempAlterColumnClause.toDB2();
         tempAlterTable.setAlterColumnClause(alterClause);
      }

      if (tempAlterTable.getModifyClause() != null) {
         ModifyClause mc = tempAlterTable.getModifyClause();
         alterClause = new AlterColumnClause();
         alterClause.setAlter("ALTER");
         alterClause.setColumn("COLUMN");
         Vector createColVec = mc.getCreateColumnVector();
         if (createColVec != null) {
            CreateColumn cc = (CreateColumn)createColVec.get(0);
            cc.setDatatypeMapping(this.datatypeMapping);
            cc.toDB2String();
            alterClause.setCreateColumn(cc);
            if (cc.getDefault() != null) {
               alterClause.setDefaultString(cc.getDefault());
               cc.setDefault((String)null);
               alterClause.setSetString("SET");
            }

            if (cc.getDefaultValue() != null) {
               alterClause.setDefaultValue(cc.getDefaultValue());
               cc.setDefaultValue((String)null);
            }

            if (cc.getDatatype() != null) {
               Datatype dt = cc.getDatatype();
               alterClause.setSetString("SET");
               alterClause.setDataTypeStr("DATA TYPE");
               dt.toDB2String();
               cc.setDatatype((Datatype)null);
               alterClause.setDataType(dt);
            }
         }

         tempAlterTable.setAlterColumnClause(alterClause);
         tempAlterTable.setModifyClause((ModifyClause)null);
      }

      if (tempAlterTable.getRename() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterTable.setAsOrTo((String)null);
         tempAlterTable.setToTableName((String)null);
         tempAlterTable.setRenameColumn((String)null);
         tempAlterTable.setTableName((String)null);
         tempAlterTable.setToColumn((String)null);
         tempAlterTable.setColumnName((String)null);
         if (tempAlterTable.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            tempAlterTable.setStorage((String)null);
            tempAlterTable.setStorageClause((String)null);
            if (tempAlterTable.getMove() == null && tempAlterTable.getTruncate() == null && tempAlterTable.getSplit() == null && tempAlterTable.getExchange() == null && tempAlterTable.getAllocateExtent() == null && tempAlterTable.getDeAllocateUnused() == null) {
               tempAlterTable.setTruncate((String)null);
               tempAlterTable.setSplit((String)null);
               tempAlterTable.setExchange((String)null);
               tempAlterTable.setPartition((PartitionListAttributes)null);
               tempAlterTable.setAllocateExtent((String)null);
               tempAlterTable.setAllocateExtentIdentifier((String)null);
               tempAlterTable.setDeAllocateUnused((String)null);
               tempAlterTable.setDeAllocateUnusedIdentifier((String)null);
               if (tempAlterTable.getOwner() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  tempAlterTable.setOwnerTo((String)null);
                  tempAlterTable.setOwnerName((String)null);
                  if (tempAlterTable.getChange() != null) {
                     throw new ConvertException("Conversion Failure.. Invalid Query");
                  } else {
                     tempAlterTable.setChangeColumn((String)null);
                     tempAlterTable.setCreateColumn((CreateColumn)null);
                     if (tempAlterTable.getCheckOrNoCheck() != null) {
                        throw new ConvertException("Conversion Failure.. Invalid Query");
                     } else {
                        tempAlterTable.setConstraint((String)null);
                        tempAlterTable.setConstraintAll((String)null);
                        tempAlterTable.setConstraintNameVector((Vector)null);
                        if (tempAlterTable.getEnableOrDisable() != null) {
                           throw new ConvertException("Conversion Failure.. Invalid Query");
                        } else {
                           Vector columnVector;
                           Vector constrClauseVector;
                           if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
                              columnVector = new Vector();
                              constrClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();

                              for(int i = 0; i < constrClauseVector.size(); ++i) {
                                 PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)constrClauseVector.get(i);
                                 PhysicalAttributesClause db2PhysicalAttributesClause = tempPhysicalAttributesClause.toDB2();
                                 columnVector.add(db2PhysicalAttributesClause);
                              }

                              tempAlterTable.setPhysicalAttributesClauseVector(columnVector);
                           }

                           tempAlterTable.setWith((String)null);
                           tempAlterTable.setTrigger((String)null);
                           tempAlterTable.setTriggerAll((String)null);
                           tempAlterTable.setTriggerNameVector((Vector)null);
                           if (tempAlterTable.getAddClause() != null) {
                              columnVector = tempAlterTable.getAddClause().getCreateColumnVector();
                              if (columnVector != null) {
                                 constrClauseVector = ((CreateColumn)columnVector.get(0)).getConstraintClause();
                                 if (constrClauseVector != null) {
                                    ConstraintType constrType = ((ConstraintClause)constrClauseVector.get(0)).getConstraintType();
                                    if (constrType != null) {
                                       if (constrType instanceof DefaultConstraintClause) {
                                          if (((DefaultConstraintClause)constrType).getForClause() != null) {
                                             throw new ConvertException("Conversion Failure.. DB2 does not support Alter / Modify Clause for a Column in a table with default value");
                                          }
                                       } else if (constrType instanceof PrimaryOrUniqueConstraintClause) {
                                          ((CreateColumn)columnVector.get(0)).setNullStatus((String)null);
                                       }
                                    }
                                 }
                              }
                           }

                           return tempAlterTable;
                        }
                     }
                  }
               }
            } else {
               throw new ConvertException();
            }
         }
      }
   }

   public AlterTable toANSI() throws ConvertException {
      AlterTable tempAlterTable = this.copyObjectValues();
      if (tempAlterTable.getAddClause() != null) {
         AddClause tempAddClause = tempAlterTable.getAddClause();
         AddClause ansiAddClause = tempAddClause.toANSI();
         tempAlterTable.setAddClause(ansiAddClause);
      }

      if (tempAlterTable.getDropClause() != null) {
         DropClause tempDropClause = tempAlterTable.getDropClause();
         DropClause ansiDropClause = tempDropClause.toANSI();
         tempAlterTable.setDropClause(ansiDropClause);
      }

      if (tempAlterTable.getAlterColumnClause() != null) {
         AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
         AlterColumnClause ansiAlterColumnClause = tempAlterColumnClause.toANSI();
         tempAlterTable.setAlterColumnClause(ansiAlterColumnClause);
      }

      if (tempAlterTable.getModifyClause() != null) {
         ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
         ModifyClause ansiModifyClause = tempModifyClause.toANSI();
         tempAlterTable.setModifyClause(ansiModifyClause);
      }

      tempAlterTable.setData((String)null);
      tempAlterTable.setCapture((String)null);
      tempAlterTable.setNoneOrChanges((String)null);
      if (tempAlterTable.getRename() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterTable.setAsOrTo((String)null);
         tempAlterTable.setToTableName((String)null);
         tempAlterTable.setRenameColumn((String)null);
         tempAlterTable.setTableName((String)null);
         tempAlterTable.setToColumn((String)null);
         tempAlterTable.setColumnName((String)null);
         if (tempAlterTable.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            tempAlterTable.setStorage((String)null);
            tempAlterTable.setStorageClause((String)null);
            if (tempAlterTable.getMove() == null && tempAlterTable.getTruncate() == null && tempAlterTable.getSplit() == null && tempAlterTable.getExchange() == null && tempAlterTable.getAllocateExtent() == null && tempAlterTable.getDeAllocateUnused() == null) {
               tempAlterTable.setTruncate((String)null);
               tempAlterTable.setSplit((String)null);
               tempAlterTable.setExchange((String)null);
               tempAlterTable.setPartition((PartitionListAttributes)null);
               tempAlterTable.setAllocateExtent((String)null);
               tempAlterTable.setAllocateExtentIdentifier((String)null);
               tempAlterTable.setDeAllocateUnused((String)null);
               tempAlterTable.setDeAllocateUnusedIdentifier((String)null);
               if (tempAlterTable.getOwner() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  tempAlterTable.setOwnerTo((String)null);
                  tempAlterTable.setOwnerName((String)null);
                  if (tempAlterTable.getChange() != null) {
                     tempAlterTable.setChange("ALTER");
                  }

                  if (tempAlterTable.getChangeColumn() != null) {
                     String var11 = tempAlterTable.getChangeColumn();
                  }

                  if (tempAlterTable.getCreateColumn() != null) {
                     CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
                     tempCreateColumn.toANSIString();
                  }

                  if (tempAlterTable.getCheckOrNoCheck() != null) {
                     throw new ConvertException("Conversion Failure.. Invalid Query");
                  } else {
                     tempAlterTable.setConstraint((String)null);
                     tempAlterTable.setConstraintAll((String)null);
                     tempAlterTable.setConstraintNameVector((Vector)null);
                     if (tempAlterTable.getEnableOrDisable() != null) {
                        throw new ConvertException("Conversion Failure.. Invalid Query");
                     } else {
                        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
                           Vector ansiPhysicalAttributesClauseVector = new Vector();
                           Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();

                           for(int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                              PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributesClauseVector.get(i);
                              PhysicalAttributesClause ansiPhysicalAttributesClause = tempPhysicalAttributesClause.toANSI();
                              ansiPhysicalAttributesClauseVector.add(ansiPhysicalAttributesClause);
                           }

                           tempAlterTable.setPhysicalAttributesClauseVector(ansiPhysicalAttributesClauseVector);
                        }

                        tempAlterTable.setWith((String)null);
                        tempAlterTable.setTrigger((String)null);
                        tempAlterTable.setTriggerAll((String)null);
                        tempAlterTable.setTriggerNameVector((Vector)null);
                        return tempAlterTable;
                     }
                  }
               }
            } else {
               throw new ConvertException();
            }
         }
      }
   }

   public AlterTable toInformix() throws ConvertException {
      AlterTable tempAlterTable = this.copyObjectValues();
      if (tempAlterTable.getAddClause() != null) {
         AddClause tempAddClause = tempAlterTable.getAddClause();
         AddClause informixAddClause = tempAddClause.toInformix();
         tempAlterTable.setAddClause(informixAddClause);
      }

      tempAlterTable.setData((String)null);
      tempAlterTable.setCapture((String)null);
      tempAlterTable.setNoneOrChanges((String)null);
      if (tempAlterTable.getModifyClause() != null) {
         ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
         ModifyClause informixModifyClause = tempModifyClause.toInformix();
         tempAlterTable.setModifyClause(informixModifyClause);
      }

      if (tempAlterTable.getDropClause() != null) {
         DropClause tempDropClause = tempAlterTable.getDropClause();
         DropClause informixDropClause = tempDropClause.toInformix();
         tempAlterTable.setDropClause(informixDropClause);
      }

      if (tempAlterTable.getAlterColumnClause() != null) {
         AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
         AlterColumnClause informixAlterColumnClause = tempAlterColumnClause.toInformix();
         tempAlterTable.setAlterColumnClause(informixAlterColumnClause);
      }

      tempAlterTable.setData((String)null);
      tempAlterTable.setCapture((String)null);
      tempAlterTable.setNoneOrChanges((String)null);
      if (tempAlterTable.getRename() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterTable.setAsOrTo((String)null);
         tempAlterTable.setToTableName((String)null);
         tempAlterTable.setRenameColumn((String)null);
         tempAlterTable.setTableName((String)null);
         tempAlterTable.setToColumn((String)null);
         tempAlterTable.setColumnName((String)null);
         if (tempAlterTable.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            tempAlterTable.setStorage((String)null);
            tempAlterTable.setStorageClause((String)null);
            if (tempAlterTable.getMove() == null && tempAlterTable.getTruncate() == null && tempAlterTable.getSplit() == null && tempAlterTable.getExchange() == null && tempAlterTable.getAllocateExtent() == null && tempAlterTable.getDeAllocateUnused() == null) {
               tempAlterTable.setTruncate((String)null);
               tempAlterTable.setSplit((String)null);
               tempAlterTable.setExchange((String)null);
               tempAlterTable.setPartition((PartitionListAttributes)null);
               tempAlterTable.setAllocateExtent((String)null);
               tempAlterTable.setAllocateExtentIdentifier((String)null);
               tempAlterTable.setDeAllocateUnused((String)null);
               tempAlterTable.setDeAllocateUnusedIdentifier((String)null);
               if (tempAlterTable.getOwner() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  tempAlterTable.setOwnerTo((String)null);
                  tempAlterTable.setOwnerName((String)null);
                  if (tempAlterTable.getChange() != null) {
                     tempAlterTable.setChange("MODIFY");
                  }

                  if (tempAlterTable.getChangeColumn() != null) {
                     String var11 = tempAlterTable.getChangeColumn();
                  }

                  if (tempAlterTable.getCreateColumn() != null) {
                     CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
                     tempCreateColumn.toInformixString();
                  }

                  if (tempAlterTable.getCheckOrNoCheck() != null) {
                     throw new ConvertException("Conversion Failure.. Invalid Query");
                  } else {
                     tempAlterTable.setConstraint((String)null);
                     tempAlterTable.setConstraintAll((String)null);
                     tempAlterTable.setConstraintNameVector((Vector)null);
                     if (tempAlterTable.getEnableOrDisable() != null) {
                        throw new ConvertException("Conversion Failure.. Invalid Query");
                     } else {
                        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
                           Vector informixPhysicalAttributesClauseVector = new Vector();
                           Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();

                           for(int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                              PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributesClauseVector.get(i);
                              PhysicalAttributesClause informixPhysicalAttributesClause = tempPhysicalAttributesClause.toInformix();
                              informixPhysicalAttributesClauseVector.add(informixPhysicalAttributesClause);
                           }

                           tempAlterTable.setPhysicalAttributesClauseVector(informixPhysicalAttributesClauseVector);
                        }

                        tempAlterTable.setWith((String)null);
                        tempAlterTable.setTrigger((String)null);
                        tempAlterTable.setTriggerAll((String)null);
                        tempAlterTable.setTriggerNameVector((Vector)null);
                        return tempAlterTable;
                     }
                  }
               }
            } else {
               throw new ConvertException();
            }
         }
      }
   }

   public AlterTable toBigQuery() throws ConvertException {
      AlterTable tempAlterTable = this.copyObjectValues();
      if (tempAlterTable.getAddClause() != null) {
         AddClause tempAddClause = tempAlterTable.getAddClause();
         AddClause bigqueryAddClause = tempAddClause.toBigQuery();
         tempAlterTable.setAddClause(bigqueryAddClause);
      }

      if (tempAlterTable.getDropClause() != null) {
         DropClause tempDropClause = tempAlterTable.getDropClause();
         DropClause bigqueryDropClause = tempDropClause.toBigQuery();
         tempAlterTable.setDropClause(bigqueryDropClause);
      }

      if (tempAlterTable.getAlterColumnClause() != null) {
         AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
         AlterColumnClause bigqueryAlterColumnClause = tempAlterColumnClause.toBigQuery();
         tempAlterTable.setAlterColumnClause(bigqueryAlterColumnClause);
      }

      String tempAsOrTo;
      if (tempAlterTable.getRename() != null) {
         tempAsOrTo = tempAlterTable.getRename();
      }

      tempAlterTable.setToTableName((String)null);
      if (tempAlterTable.getAsOrTo() != null) {
         tempAsOrTo = tempAlterTable.getAsOrTo();
         tempAsOrTo = tempAsOrTo.trim();
         if (tempAsOrTo.equalsIgnoreCase("AS")) {
            tempAlterTable.setAsOrTo("TO");
         } else {
            tempAlterTable.setAsOrTo(tempAsOrTo);
         }
      }

      if (tempAlterTable.getRenameColumn() != null) {
         tempAsOrTo = tempAlterTable.getRenameColumn();
      }

      if (tempAlterTable.getTableName() != null) {
         tempAsOrTo = tempAlterTable.getTableName();
      }

      if (tempAlterTable.getToColumn() != null) {
         tempAsOrTo = tempAlterTable.getToColumn();
      }

      if (tempAlterTable.getColumnName() != null) {
         tempAsOrTo = tempAlterTable.getColumnName();
      }

      if (tempAlterTable.getModifyClause() != null) {
         ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
         ModifyClause bigquerySQLModifyClause = tempModifyClause.toBigQuery();
         tempAlterTable.setModifyClause(bigquerySQLModifyClause);
      }

      tempAlterTable.setData((String)null);
      tempAlterTable.setCapture((String)null);
      tempAlterTable.setNoneOrChanges((String)null);
      if (tempAlterTable.getOverflow() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterTable.setStorage((String)null);
         tempAlterTable.setStorageClause((String)null);
         if (tempAlterTable.getMove() == null && tempAlterTable.getTruncate() == null && tempAlterTable.getSplit() == null && tempAlterTable.getExchange() == null && tempAlterTable.getAllocateExtent() == null && tempAlterTable.getDeAllocateUnused() == null) {
            tempAlterTable.setTruncate((String)null);
            tempAlterTable.setSplit((String)null);
            tempAlterTable.setExchange((String)null);
            tempAlterTable.setPartition((PartitionListAttributes)null);
            tempAlterTable.setAllocateExtent((String)null);
            tempAlterTable.setAllocateExtentIdentifier((String)null);
            tempAlterTable.setDeAllocateUnused((String)null);
            tempAlterTable.setDeAllocateUnusedIdentifier((String)null);
            if (tempAlterTable.getOwner() != null) {
               tempAsOrTo = tempAlterTable.getOwner();
            }

            if (tempAlterTable.getOwnerTo() != null) {
               tempAsOrTo = tempAlterTable.getOwnerTo();
            }

            if (tempAlterTable.getOwnerName() != null) {
               tempAsOrTo = tempAlterTable.getOwnerName();
            }

            if (tempAlterTable.getChange() != null) {
               tempAlterTable.setChange("ALTER");
            }

            if (tempAlterTable.getChangeColumn() != null) {
               tempAsOrTo = tempAlterTable.getChangeColumn();
            }

            if (tempAlterTable.getCreateColumn() != null) {
               CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
               tempCreateColumn.toBigQueryString();
            }

            if (tempAlterTable.getCheckOrNoCheck() != null) {
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               tempAlterTable.setConstraint((String)null);
               tempAlterTable.setConstraintAll((String)null);
               tempAlterTable.setConstraintNameVector((Vector)null);
               if (tempAlterTable.getEnableOrDisable() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
                     Vector bigquerySQLPhysicalAttributesClauseVector = new Vector();
                     Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();

                     for(int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                        PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributesClauseVector.get(i);
                        PhysicalAttributesClause bigquerySQLPhysicalAttributesClause = tempPhysicalAttributesClause.toBigQuery();
                        bigquerySQLPhysicalAttributesClauseVector.add(bigquerySQLPhysicalAttributesClause);
                     }

                     tempAlterTable.setPhysicalAttributesClauseVector(bigquerySQLPhysicalAttributesClauseVector);
                  }

                  tempAlterTable.setWith((String)null);
                  tempAlterTable.setTrigger((String)null);
                  tempAlterTable.setTriggerAll((String)null);
                  tempAlterTable.setTriggerNameVector((Vector)null);
                  return tempAlterTable;
               }
            }
         } else {
            throw new ConvertException();
         }
      }
   }

   public AlterTable toPostgreSQL() throws ConvertException {
      AlterTable tempAlterTable = this.copyObjectValues();
      if (tempAlterTable.getAddClause() != null) {
         AddClause tempAddClause = tempAlterTable.getAddClause();
         AddClause postgreAddClause = tempAddClause.toPostgreSQL();
         tempAlterTable.setAddClause(postgreAddClause);
      }

      if (tempAlterTable.getDropClause() != null) {
         DropClause tempDropClause = tempAlterTable.getDropClause();
         DropClause postgreDropClause = tempDropClause.toPostgreSQL();
         tempAlterTable.setDropClause(postgreDropClause);
      }

      if (tempAlterTable.getAlterColumnClause() != null) {
         AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
         AlterColumnClause postgreAlterColumnClause = tempAlterColumnClause.toPostgreSQL();
         tempAlterTable.setAlterColumnClause(postgreAlterColumnClause);
      }

      String tempAsOrTo;
      if (tempAlterTable.getRename() != null) {
         tempAsOrTo = tempAlterTable.getRename();
      }

      tempAlterTable.setToTableName((String)null);
      if (tempAlterTable.getAsOrTo() != null) {
         tempAsOrTo = tempAlterTable.getAsOrTo();
         tempAsOrTo = tempAsOrTo.trim();
         if (tempAsOrTo.equalsIgnoreCase("AS")) {
            tempAlterTable.setAsOrTo("TO");
         } else {
            tempAlterTable.setAsOrTo(tempAsOrTo);
         }
      }

      if (tempAlterTable.getRenameColumn() != null) {
         tempAsOrTo = tempAlterTable.getRenameColumn();
      }

      if (tempAlterTable.getTableName() != null) {
         tempAsOrTo = tempAlterTable.getTableName();
      }

      if (tempAlterTable.getToColumn() != null) {
         tempAsOrTo = tempAlterTable.getToColumn();
      }

      if (tempAlterTable.getColumnName() != null) {
         tempAsOrTo = tempAlterTable.getColumnName();
      }

      if (tempAlterTable.getModifyClause() != null) {
         ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
         ModifyClause postgreSQLModifyClause = tempModifyClause.toPostgreSQL();
         tempAlterTable.setModifyClause(postgreSQLModifyClause);
      }

      tempAlterTable.setData((String)null);
      tempAlterTable.setCapture((String)null);
      tempAlterTable.setNoneOrChanges((String)null);
      if (tempAlterTable.getOverflow() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterTable.setStorage((String)null);
         tempAlterTable.setStorageClause((String)null);
         if (tempAlterTable.getMove() == null && tempAlterTable.getTruncate() == null && tempAlterTable.getSplit() == null && tempAlterTable.getExchange() == null && tempAlterTable.getAllocateExtent() == null && tempAlterTable.getDeAllocateUnused() == null) {
            tempAlterTable.setTruncate((String)null);
            tempAlterTable.setSplit((String)null);
            tempAlterTable.setExchange((String)null);
            tempAlterTable.setPartition((PartitionListAttributes)null);
            tempAlterTable.setAllocateExtent((String)null);
            tempAlterTable.setAllocateExtentIdentifier((String)null);
            tempAlterTable.setDeAllocateUnused((String)null);
            tempAlterTable.setDeAllocateUnusedIdentifier((String)null);
            if (tempAlterTable.getOwner() != null) {
               tempAsOrTo = tempAlterTable.getOwner();
            }

            if (tempAlterTable.getOwnerTo() != null) {
               tempAsOrTo = tempAlterTable.getOwnerTo();
            }

            if (tempAlterTable.getOwnerName() != null) {
               tempAsOrTo = tempAlterTable.getOwnerName();
            }

            if (tempAlterTable.getChange() != null) {
               tempAlterTable.setChange("ALTER");
            }

            if (tempAlterTable.getChangeColumn() != null) {
               tempAsOrTo = tempAlterTable.getChangeColumn();
            }

            if (tempAlterTable.getCreateColumn() != null) {
               CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
               tempCreateColumn.toPostgreSQLString();
            }

            if (tempAlterTable.getCheckOrNoCheck() != null) {
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               tempAlterTable.setConstraint((String)null);
               tempAlterTable.setConstraintAll((String)null);
               tempAlterTable.setConstraintNameVector((Vector)null);
               if (tempAlterTable.getEnableOrDisable() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
                     Vector postgreSQLPhysicalAttributesClauseVector = new Vector();
                     Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();

                     for(int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                        PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributesClauseVector.get(i);
                        PhysicalAttributesClause postgreSQLPhysicalAttributesClause = tempPhysicalAttributesClause.toPostgreSQL();
                        postgreSQLPhysicalAttributesClauseVector.add(postgreSQLPhysicalAttributesClause);
                     }

                     tempAlterTable.setPhysicalAttributesClauseVector(postgreSQLPhysicalAttributesClauseVector);
                  }

                  tempAlterTable.setWith((String)null);
                  tempAlterTable.setTrigger((String)null);
                  tempAlterTable.setTriggerAll((String)null);
                  tempAlterTable.setTriggerNameVector((Vector)null);
                  return tempAlterTable;
               }
            }
         } else {
            throw new ConvertException();
         }
      }
   }

   public AlterTable toMySQL() throws ConvertException {
      AlterTable tempAlterTable = this.copyObjectValues();
      if (tempAlterTable.getAddClause() != null) {
         AddClause tempAddClause = tempAlterTable.getAddClause();
         AddClause mySQLAddClause = tempAddClause.toMySQL();
         tempAlterTable.setAddClause(mySQLAddClause);
      }

      if (tempAlterTable.getDropClause() != null) {
         DropClause tempDropClause = tempAlterTable.getDropClause();
         DropClause mySQLDropClause = tempDropClause.toMySQL();
         tempAlterTable.setDropClause(mySQLDropClause);
      }

      if (tempAlterTable.getAlterColumnClause() != null) {
         AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
         AlterColumnClause mySQLAlterColumnClause = tempAlterColumnClause.toMySQL();
         tempAlterTable.setAlterColumnClause(mySQLAlterColumnClause);
      }

      if (tempAlterTable.getModifyClause() != null) {
         ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
         ModifyClause mySQLModifyClause = tempModifyClause.toMySQL();
         tempAlterTable.setModifyClause(mySQLModifyClause);
      }

      String tempAsOrTo;
      if (tempAlterTable.getRename() != null) {
         tempAsOrTo = tempAlterTable.getRename();
      }

      if (this.toTableName != null) {
         tempAlterTable.setToTableName("TO");
      }

      if (tempAlterTable.getAsOrTo() != null) {
         tempAsOrTo = tempAlterTable.getAsOrTo();
         tempAsOrTo = tempAsOrTo.trim();
         if (tempAsOrTo.equalsIgnoreCase("As")) {
            tempAlterTable.setAsOrTo("TO");
         } else {
            tempAlterTable.setAsOrTo(tempAsOrTo);
         }
      }

      if (tempAlterTable.getRenameColumn() == null && tempAlterTable.getColumnName() == null) {
         if (tempAlterTable.getToColumn() != null) {
            tempAsOrTo = tempAlterTable.getToColumn();
         }

         if (tempAlterTable.getTableName() != null) {
            tempAsOrTo = tempAlterTable.getTableName();
         }

         tempAlterTable.setData((String)null);
         tempAlterTable.setCapture((String)null);
         tempAlterTable.setNoneOrChanges((String)null);
         if (tempAlterTable.getOwner() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            tempAlterTable.setOwnerTo((String)null);
            tempAlterTable.setOwnerName((String)null);
            if (tempAlterTable.getOverflow() != null) {
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               tempAlterTable.setStorage((String)null);
               tempAlterTable.setStorageClause((String)null);
               if (tempAlterTable.getMove() == null && tempAlterTable.getTruncate() == null && tempAlterTable.getSplit() == null && tempAlterTable.getExchange() == null && tempAlterTable.getAllocateExtent() == null && tempAlterTable.getDeAllocateUnused() == null) {
                  tempAlterTable.setTruncate((String)null);
                  tempAlterTable.setSplit((String)null);
                  tempAlterTable.setExchange((String)null);
                  tempAlterTable.setPartition((PartitionListAttributes)null);
                  tempAlterTable.setAllocateExtent((String)null);
                  tempAlterTable.setAllocateExtentIdentifier((String)null);
                  tempAlterTable.setDeAllocateUnused((String)null);
                  tempAlterTable.setDeAllocateUnusedIdentifier((String)null);
                  if (tempAlterTable.getChange() != null) {
                     tempAsOrTo = tempAlterTable.getChange();
                  }

                  if (tempAlterTable.getChangeColumn() != null) {
                     tempAsOrTo = tempAlterTable.getChangeColumn();
                  }

                  if (tempAlterTable.getCreateColumn() != null) {
                     CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
                     tempCreateColumn.toMySQLString();
                  }

                  if (tempAlterTable.getCheckOrNoCheck() != null) {
                     throw new ConvertException("Conversion Failure.. Invalid Query");
                  } else {
                     tempAlterTable.setConstraint((String)null);
                     tempAlterTable.setConstraintAll((String)null);
                     tempAlterTable.setConstraintNameVector((Vector)null);
                     if (tempAlterTable.getEnableOrDisable() != null) {
                        throw new ConvertException("Conversion Failure.. Invalid Query");
                     } else {
                        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
                           Vector mySQLPhysicalAttributesClauseVector = new Vector();
                           Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();

                           for(int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                              PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributesClauseVector.get(i);
                              PhysicalAttributesClause mySQLPhysicalAttributesClause = tempPhysicalAttributesClause.toMySQL();
                              mySQLPhysicalAttributesClauseVector.add(mySQLPhysicalAttributesClause);
                           }

                           tempAlterTable.setPhysicalAttributesClauseVector(mySQLPhysicalAttributesClauseVector);
                        }

                        tempAlterTable.setWith((String)null);
                        tempAlterTable.setTrigger((String)null);
                        tempAlterTable.setTriggerAll((String)null);
                        tempAlterTable.setTriggerNameVector((Vector)null);
                        return tempAlterTable;
                     }
                  }
               } else {
                  throw new ConvertException();
               }
            }
         }
      } else {
         tempAlterTable.setToColumn((String)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      }
   }

   public AlterTable toSnowflake() throws ConvertException {
      AlterTable tempAlterTable = this.copyObjectValues();
      if (tempAlterTable.getAddClause() != null) {
         AddClause tempAddClause = tempAlterTable.getAddClause();
         AddClause snowflakeAddClause = tempAddClause.toSnowflake();
         tempAlterTable.setAddClause(snowflakeAddClause);
      }

      if (tempAlterTable.getDropClause() != null) {
         DropClause tempDropClause = tempAlterTable.getDropClause();
         DropClause snowflakeDropClause = tempDropClause.toSnowflake();
         tempAlterTable.setDropClause(snowflakeDropClause);
      }

      if (tempAlterTable.getAlterColumnClause() != null) {
         AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
         AlterColumnClause snowflakeAlterColumnClause = tempAlterColumnClause.toSnowflake();
         tempAlterTable.setAlterColumnClause(snowflakeAlterColumnClause);
      }

      String tempAsOrTo;
      if (tempAlterTable.getRename() != null) {
         tempAsOrTo = tempAlterTable.getRename();
      }

      tempAlterTable.setToTableName((String)null);
      if (tempAlterTable.getAsOrTo() != null) {
         tempAsOrTo = tempAlterTable.getAsOrTo();
         tempAsOrTo = tempAsOrTo.trim();
         if (tempAsOrTo.equalsIgnoreCase("AS")) {
            tempAlterTable.setAsOrTo("TO");
         } else {
            tempAlterTable.setAsOrTo(tempAsOrTo);
         }
      }

      if (tempAlterTable.getRenameColumn() != null) {
         tempAsOrTo = tempAlterTable.getRenameColumn();
      }

      if (tempAlterTable.getTableName() != null) {
         tempAsOrTo = tempAlterTable.getTableName();
      }

      if (tempAlterTable.getToColumn() != null) {
         tempAsOrTo = tempAlterTable.getToColumn();
      }

      if (tempAlterTable.getColumnName() != null) {
         tempAsOrTo = tempAlterTable.getColumnName();
      }

      if (tempAlterTable.getModifyClause() != null) {
         ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
         ModifyClause snowflakeModifyClause = tempModifyClause.toSnowflake();
         tempAlterTable.setModifyClause(snowflakeModifyClause);
      }

      tempAlterTable.setData((String)null);
      tempAlterTable.setCapture((String)null);
      tempAlterTable.setNoneOrChanges((String)null);
      if (tempAlterTable.getOverflow() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterTable.setStorage((String)null);
         tempAlterTable.setStorageClause((String)null);
         if (tempAlterTable.getMove() == null && tempAlterTable.getTruncate() == null && tempAlterTable.getSplit() == null && tempAlterTable.getExchange() == null && tempAlterTable.getAllocateExtent() == null && tempAlterTable.getDeAllocateUnused() == null) {
            tempAlterTable.setTruncate((String)null);
            tempAlterTable.setSplit((String)null);
            tempAlterTable.setExchange((String)null);
            tempAlterTable.setPartition((PartitionListAttributes)null);
            tempAlterTable.setAllocateExtent((String)null);
            tempAlterTable.setAllocateExtentIdentifier((String)null);
            tempAlterTable.setDeAllocateUnused((String)null);
            tempAlterTable.setDeAllocateUnusedIdentifier((String)null);
            if (tempAlterTable.getOwner() != null) {
               tempAsOrTo = tempAlterTable.getOwner();
            }

            if (tempAlterTable.getOwnerTo() != null) {
               tempAsOrTo = tempAlterTable.getOwnerTo();
            }

            if (tempAlterTable.getOwnerName() != null) {
               tempAsOrTo = tempAlterTable.getOwnerName();
            }

            if (tempAlterTable.getChange() != null) {
               tempAlterTable.setChange("ALTER");
            }

            if (tempAlterTable.getChangeColumn() != null) {
               tempAsOrTo = tempAlterTable.getChangeColumn();
            }

            if (tempAlterTable.getCreateColumn() != null) {
               CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
               tempCreateColumn.toSnowflakeString();
            }

            if (tempAlterTable.getCheckOrNoCheck() != null) {
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               tempAlterTable.setConstraint((String)null);
               tempAlterTable.setConstraintAll((String)null);
               tempAlterTable.setConstraintNameVector((Vector)null);
               if (tempAlterTable.getEnableOrDisable() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
                     Vector snowflakePhysicalAttributesClauseVector = new Vector();
                     Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();

                     for(int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                        PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributesClauseVector.get(i);
                        PhysicalAttributesClause snowflakePhysicalAttributesClause = tempPhysicalAttributesClause.toSnowflake();
                        snowflakePhysicalAttributesClauseVector.add(snowflakePhysicalAttributesClause);
                     }

                     tempAlterTable.setPhysicalAttributesClauseVector(snowflakePhysicalAttributesClauseVector);
                  }

                  tempAlterTable.setWith((String)null);
                  tempAlterTable.setTrigger((String)null);
                  tempAlterTable.setTriggerAll((String)null);
                  tempAlterTable.setTriggerNameVector((Vector)null);
                  return tempAlterTable;
               }
            }
         } else {
            throw new ConvertException();
         }
      }
   }

   public AlterTable toTimesTen() throws ConvertException {
      AlterTable tempAlterTable = this.copyObjectValues();
      if (tempAlterTable.getAddClause() != null) {
         AddClause tempAddClause = tempAlterTable.getAddClause();
         AddClause timesTenAddClause = tempAddClause.toTimesTen();
         tempAlterTable.setAddClause(timesTenAddClause);
      }

      tempAlterTable.setData((String)null);
      tempAlterTable.setCapture((String)null);
      tempAlterTable.setNoneOrChanges((String)null);
      if (tempAlterTable.getModifyClause() != null) {
         throw new ConvertException("\nMODIFY clause is not supported in TimesTen 5.1.21\n");
      } else {
         if (tempAlterTable.getDropClause() != null) {
            DropClause tempDropClause = tempAlterTable.getDropClause();
            DropClause timesTenDropClause = tempDropClause.toTimesTen();
            tempAlterTable.setDropClause(timesTenDropClause);
         }

         if (tempAlterTable.getAlterColumnClause() != null) {
         }

         if (tempAlterTable.getCheckOrNoCheck() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            tempAlterTable.setConstraint((String)null);
            tempAlterTable.setConstraintAll((String)null);
            tempAlterTable.setConstraintNameVector((Vector)null);
            if (tempAlterTable.getOwner() != null) {
               throw new ConvertException("\nUnsupported SQL in TimesTen 5.1.21\n");
            } else {
               tempAlterTable.setOwnerTo((String)null);
               tempAlterTable.setOwnerName((String)null);
               tempAlterTable.setChangeColumn((String)null);
               if (tempAlterTable.getCreateColumn() != null) {
                  CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
                  tempCreateColumn.toTimesTenString();
               }

               if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
                  tempAlterTable.setPhysicalAttributesClauseVector((Vector)null);
               }

               tempAlterTable.setWith((String)null);
               tempAlterTable.setTriggerAll((String)null);
               tempAlterTable.setTriggerNameVector((Vector)null);
               return tempAlterTable;
            }
         }
      }
   }

   public AlterTable toNetezza() throws ConvertException {
      AlterTable tempAlterTable = this.copyObjectValues();
      if (tempAlterTable.getAddClause() != null) {
         AddClause tempAddClause = tempAlterTable.getAddClause();
         AddClause netezzaAddClause = tempAddClause.toNetezza();
         tempAlterTable.setAddClause(netezzaAddClause);
      }

      if (tempAlterTable.getDropClause() != null) {
         DropClause tempDropClause = tempAlterTable.getDropClause();
         DropClause netezzaDropClause = tempDropClause.toNetezza();
         tempAlterTable.setDropClause(netezzaDropClause);
      }

      if (tempAlterTable.getAlterColumnClause() != null) {
         AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
         AlterColumnClause netezzaAlterColumnClause = tempAlterColumnClause.toNetezza();
         tempAlterTable.setAlterColumnClause(netezzaAlterColumnClause);
      }

      if (tempAlterTable.getModifyClause() != null) {
         ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
         ModifyClause netezzaModifyClause = tempModifyClause.toNetezza();
         tempAlterTable.setModifyClause(netezzaModifyClause);
      }

      tempAlterTable.setData((String)null);
      tempAlterTable.setCapture((String)null);
      tempAlterTable.setNoneOrChanges((String)null);
      if (tempAlterTable.getOverflow() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterTable.setStorage((String)null);
         tempAlterTable.setStorageClause((String)null);
         if (tempAlterTable.getMove() == null && tempAlterTable.getTruncate() == null && tempAlterTable.getSplit() == null && tempAlterTable.getExchange() == null && tempAlterTable.getAllocateExtent() == null && tempAlterTable.getDeAllocateUnused() == null) {
            tempAlterTable.setTruncate((String)null);
            tempAlterTable.setSplit((String)null);
            tempAlterTable.setExchange((String)null);
            tempAlterTable.setPartition((PartitionListAttributes)null);
            tempAlterTable.setAllocateExtent((String)null);
            tempAlterTable.setAllocateExtentIdentifier((String)null);
            tempAlterTable.setDeAllocateUnused((String)null);
            tempAlterTable.setDeAllocateUnusedIdentifier((String)null);
            if (tempAlterTable.getOwner() != null) {
            }

            if (tempAlterTable.getChange() != null) {
               tempAlterTable.setChange("ALTER");
            }

            if (tempAlterTable.getChangeColumn() != null) {
               String var11 = tempAlterTable.getChangeColumn();
            }

            if (tempAlterTable.getCreateColumn() != null) {
               CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
               tempCreateColumn.toNetezzaString();
            }

            if (tempAlterTable.getCheckOrNoCheck() != null) {
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               tempAlterTable.setConstraint((String)null);
               tempAlterTable.setConstraintAll((String)null);
               tempAlterTable.setConstraintNameVector((Vector)null);
               if (tempAlterTable.getEnableOrDisable() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
                     Vector netezzaPhysicalAttributesClauseVector = new Vector();
                     Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();

                     for(int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                        PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributesClauseVector.get(i);
                        PhysicalAttributesClause netezzaPhysicalAttributesClause = tempPhysicalAttributesClause.toNetezza();
                        netezzaPhysicalAttributesClauseVector.add(netezzaPhysicalAttributesClause);
                     }

                     tempAlterTable.setPhysicalAttributesClauseVector(netezzaPhysicalAttributesClauseVector);
                  }

                  tempAlterTable.setWith((String)null);
                  tempAlterTable.setTrigger((String)null);
                  tempAlterTable.setTriggerAll((String)null);
                  tempAlterTable.setTriggerNameVector((Vector)null);
                  return tempAlterTable;
               }
            }
         } else {
            throw new ConvertException("/*SwisSQL  Message - Statements which alter Oracle specific  properties are not converted*/");
         }
      }
   }

   public AlterTable toTeradata() throws ConvertException {
      AlterTable tempAlterTable = this.copyObjectValues();
      if (tempAlterTable.getAddClause() != null) {
         AddClause tempAddClause = tempAlterTable.getAddClause();
         AddClause teradataAddClause = tempAddClause.toTeradata();
         tempAlterTable.setAddClause(teradataAddClause);
      }

      if (tempAlterTable.getDropClause() != null) {
         DropClause tempDropClause = tempAlterTable.getDropClause();
         DropClause teradataDropClause = tempDropClause.toTeradata();
         tempAlterTable.setDropClause(teradataDropClause);
      }

      if (tempAlterTable.getAlterColumnClause() != null) {
         AlterColumnClause tempAlterColumnClause = tempAlterTable.getAlterColumnClause();
         AlterColumnClause teradataAlterColumnClause = tempAlterColumnClause.toTeradata();
         tempAlterTable.setAlterColumnClause(teradataAlterColumnClause);
      }

      if (tempAlterTable.getModifyClause() != null) {
         ModifyClause tempModifyClause = tempAlterTable.getModifyClause();
         ModifyClause teradataModifyClause = tempModifyClause.toTeradata();
         tempAlterTable.setModifyClause(teradataModifyClause);
      }

      tempAlterTable.setData((String)null);
      tempAlterTable.setCapture((String)null);
      tempAlterTable.setNoneOrChanges((String)null);
      if (tempAlterTable.getRename() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterTable.setAsOrTo((String)null);
         tempAlterTable.setToTableName((String)null);
         tempAlterTable.setRenameColumn((String)null);
         tempAlterTable.setTableName((String)null);
         tempAlterTable.setToColumn((String)null);
         tempAlterTable.setColumnName((String)null);
         if (tempAlterTable.getOverflow() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            tempAlterTable.setStorage((String)null);
            tempAlterTable.setStorageClause((String)null);
            if (tempAlterTable.getMove() == null && tempAlterTable.getTruncate() == null && tempAlterTable.getSplit() == null && tempAlterTable.getExchange() == null && tempAlterTable.getAllocateExtent() == null && tempAlterTable.getDeAllocateUnused() == null) {
               tempAlterTable.setTruncate((String)null);
               tempAlterTable.setSplit((String)null);
               tempAlterTable.setExchange((String)null);
               tempAlterTable.setPartition((PartitionListAttributes)null);
               tempAlterTable.setAllocateExtent((String)null);
               tempAlterTable.setAllocateExtentIdentifier((String)null);
               tempAlterTable.setDeAllocateUnused((String)null);
               tempAlterTable.setDeAllocateUnusedIdentifier((String)null);
               if (tempAlterTable.getOwner() != null) {
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  tempAlterTable.setOwnerTo((String)null);
                  tempAlterTable.setOwnerName((String)null);
                  if (tempAlterTable.getChange() != null) {
                     tempAlterTable.setChange("ALTER");
                  }

                  if (tempAlterTable.getChangeColumn() != null) {
                     String var11 = tempAlterTable.getChangeColumn();
                  }

                  if (tempAlterTable.getCreateColumn() != null) {
                     CreateColumn tempCreateColumn = tempAlterTable.getCreateColumn();
                     tempCreateColumn.toTeradataString();
                  }

                  if (tempAlterTable.getCheckOrNoCheck() != null) {
                     throw new ConvertException("Conversion Failure.. Invalid Query");
                  } else {
                     tempAlterTable.setConstraint((String)null);
                     tempAlterTable.setConstraintAll((String)null);
                     tempAlterTable.setConstraintNameVector((Vector)null);
                     if (tempAlterTable.getEnableOrDisable() != null) {
                        throw new ConvertException("Conversion Failure.. Invalid Query");
                     } else {
                        if (tempAlterTable.getPhysicalAttributesClauseVector() != null) {
                           Vector teradataPhysicalAttributesClauseVector = new Vector();
                           Vector tempPhysicalAttributesClauseVector = tempAlterTable.getPhysicalAttributesClauseVector();

                           for(int i = 0; i < tempPhysicalAttributesClauseVector.size(); ++i) {
                              PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributesClauseVector.get(i);
                              PhysicalAttributesClause teradataPhysicalAttributesClause = tempPhysicalAttributesClause.toTeradata();
                              teradataPhysicalAttributesClauseVector.add(teradataPhysicalAttributesClause);
                           }

                           tempAlterTable.setPhysicalAttributesClauseVector(teradataPhysicalAttributesClauseVector);
                        }

                        tempAlterTable.setWith((String)null);
                        tempAlterTable.setTrigger((String)null);
                        tempAlterTable.setTriggerAll((String)null);
                        tempAlterTable.setTriggerNameVector((Vector)null);
                        return tempAlterTable;
                     }
                  }
               }
            } else {
               throw new ConvertException();
            }
         }
      }
   }

   public AlterTable copyObjectValues() {
      AlterTable dupAlterTable = new AlterTable();
      dupAlterTable.setAddClause(this.getAddClause());
      dupAlterTable.setModifyClause(this.getModifyClause());
      dupAlterTable.setDropClause(this.getDropClause());
      dupAlterTable.setAlterColumnClause(this.getAlterColumnClause());
      dupAlterTable.setData(this.getData());
      dupAlterTable.setCapture(this.getCapture());
      dupAlterTable.setNoneOrChanges(this.getNoneOrChanges());
      dupAlterTable.setRename(this.getRename());
      dupAlterTable.setToTableName(this.toTableName);
      dupAlterTable.setAsOrTo(this.getAsOrTo());
      dupAlterTable.setRenameColumn(this.getRenameColumn());
      dupAlterTable.setTableName(this.getTableName());
      dupAlterTable.setToColumn(this.getToColumn());
      dupAlterTable.setColumnName(this.getColumnName());
      dupAlterTable.setObjectContext(this.context);
      dupAlterTable.setOverflow(this.getOverflow());
      dupAlterTable.setStorage(this.getStorage());
      dupAlterTable.setStorageClause(this.getStorageClause());
      dupAlterTable.setMove(this.getMove());
      dupAlterTable.setTruncate(this.getTruncate());
      dupAlterTable.setSplit(this.getSplit());
      dupAlterTable.setExchange(this.getExchange());
      dupAlterTable.setPartition(this.getPartition());
      dupAlterTable.setAllocateExtent(this.getAllocateExtent());
      dupAlterTable.setAllocateExtentIdentifier(this.getAllocateExtentIdentifier());
      dupAlterTable.setDeAllocateUnused(this.getDeAllocateUnused());
      dupAlterTable.setDeAllocateUnusedIdentifier(this.getDeAllocateUnusedIdentifier());
      dupAlterTable.setCheckOrNoCheck(this.getCheckOrNoCheck());
      dupAlterTable.setConstraint(this.getConstraint());
      dupAlterTable.setConstraintAll(this.getConstraintAll());
      dupAlterTable.setConstraintNameVector(this.getConstraintNameVector());
      dupAlterTable.setOwner(this.getOwner());
      dupAlterTable.setOwnerTo(this.getOwnerTo());
      dupAlterTable.setOwnerName(this.getOwnerName());
      dupAlterTable.setChange(this.getChange());
      dupAlterTable.setChangeColumn(this.getChangeColumn());
      dupAlterTable.setCreateColumn(this.getCreateColumn());
      dupAlterTable.setTriggerNameVector(this.getTriggerNameVector());
      dupAlterTable.setEnableOrDisable(this.getEnableOrDisable());
      dupAlterTable.setTrigger(this.getTrigger());
      dupAlterTable.setTriggerAll(this.getTriggerAll());
      dupAlterTable.setPhysicalAttributesClauseVector(this.getPhysicalAttributesClauseVector());
      dupAlterTable.setTableOption(this.getTableOption());
      dupAlterTable.setTableOptionParameter(this.getTableOptionParameter());
      dupAlterTable.setOrigColumn(this.getOrigColumn());
      return dupAlterTable;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.addClause != null) {
         this.addClause.setObjectContext(this.context);
         sb.append("\n" + this.addClause.toString());
      }

      if (this.dropClause != null) {
         this.dropClause.setObjectContext(this.context);
         sb.append("\n" + this.dropClause.toString());
      }

      if (this.data != null) {
         sb.append(" " + this.data.toUpperCase());
      }

      if (this.capture != null) {
         sb.append(" " + this.capture.toUpperCase());
      }

      if (this.noneOrChanges != null) {
         sb.append(" " + this.noneOrChanges);
      }

      if (this.modifyClause != null) {
         this.modifyClause.setObjectContext(this.context);
         sb.append("\n" + this.modifyClause.toString());
      }

      if (this.alterColumnClause != null) {
         this.alterColumnClause.setObjectContext(this.context);
         sb.append("\n" + this.alterColumnClause.toString());
      }

      if (this.rename != null) {
         sb.append("\n" + this.rename.toUpperCase());
      }

      if (this.renameColumn != null) {
         sb.append(" " + this.renameColumn.toUpperCase());
      }

      if (this.toTableName != null) {
         sb.append(" " + this.toTableName.toUpperCase());
      }

      if (this.tableName != null) {
         sb.append(" " + this.tableName);
      }

      if (this.asOrTo != null) {
         sb.append(" " + this.asOrTo.toUpperCase());
      }

      if (this.toColumn != null) {
         sb.append(" " + this.toColumn.toUpperCase());
      }

      if (this.columnName != null) {
         sb.append(" " + this.columnName);
      }

      if (this.overflow != null) {
         sb.append("\n" + this.overflow.toUpperCase());
      }

      if (this.storage != null) {
         sb.append(" " + this.storage.toUpperCase());
      }

      if (this.storageClauseString != null) {
         sb.append(" (" + this.storageClauseString + ")");
      }

      if (this.move != null) {
         sb.append("\n" + this.move.toUpperCase());
      }

      if (this.truncate != null) {
         sb.append(" " + this.truncate.toUpperCase());
      }

      if (this.split != null) {
         sb.append(" " + this.split.toUpperCase());
      }

      if (this.exchange != null) {
         sb.append(" " + this.exchange.toUpperCase());
      }

      if (this.partitionListAttributes != null) {
         sb.append("\n\t" + this.partitionListAttributes.toString());
      }

      if (this.allocateExtent != null) {
         sb.append("\n" + this.allocateExtent.toUpperCase());
      }

      if (this.allocateExtentIdentifier != null) {
         sb.append(" " + this.allocateExtentIdentifier);
      }

      if (this.deAllocateUnused != null) {
         sb.append("\n" + this.deAllocateUnused.toUpperCase());
      }

      if (this.deAllocateUnusedIdentifier != null) {
         sb.append(" " + this.deAllocateUnusedIdentifier);
      }

      if (this.checkOrNoCheck != null) {
         sb.append("\n" + this.checkOrNoCheck.toUpperCase());
      }

      if (this.constraint != null) {
         sb.append(" " + this.constraint.toUpperCase());
      }

      if (this.constraintAll != null) {
         sb.append(" " + this.constraintAll.toUpperCase());
      }

      int i;
      if (this.constraintNameVector != null) {
         for(i = 0; i < this.constraintNameVector.size(); ++i) {
            String tempConstraintName = (String)this.constraintNameVector.get(i);
            if (i == 0) {
               sb.append(" " + tempConstraintName);
            } else {
               sb.append("," + tempConstraintName);
            }
         }
      }

      if (this.owner != null) {
         sb.append("\n" + this.owner.toUpperCase());
      }

      if (this.ownerTo != null) {
         sb.append(" " + this.ownerTo.toUpperCase());
      }

      if (this.ownerName != null) {
         sb.append(" " + this.ownerName);
      }

      if (this.change != null) {
         sb.append("\n" + this.change.toUpperCase());
      }

      if (this.changeColumn != null) {
         sb.append(" " + this.changeColumn.toUpperCase());
      }

      if (this.createColumn != null) {
         this.createColumn.setObjectContext(this.context);
         sb.append(" " + this.createColumn.toString());
      }

      if (this.enableOrDisable != null) {
         sb.append("\n" + this.enableOrDisable.toUpperCase());
      }

      if (this.trigger != null) {
         sb.append(" " + this.trigger.toUpperCase());
      }

      if (this.triggerAll != null) {
         sb.append(" " + this.triggerAll.toUpperCase());
      }

      if (this.triggerNameVector != null) {
         for(i = 0; i < this.triggerNameVector.size(); ++i) {
            CreateColumn tempCreateColumn = (CreateColumn)this.triggerNameVector.get(i);
            tempCreateColumn.setObjectContext(this.context);
            if (i == 0) {
               sb.append("  " + tempCreateColumn.toString());
            } else {
               sb.append("," + tempCreateColumn.toString());
            }
         }
      }

      if (this.with != null) {
         sb.append("\n" + this.with.toUpperCase());
      }

      if (this.physicalAttributesClauseVector != null) {
         for(i = 0; i < this.physicalAttributesClauseVector.size(); ++i) {
            PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)this.physicalAttributesClauseVector.get(i);
            if (i == 0) {
               sb.append("\n\t" + tempPhysicalAttributesClause.toString());
            } else if (this.comma != null) {
               sb.append(",\n\t" + tempPhysicalAttributesClause.toString());
            } else {
               sb.append("\n\t" + tempPhysicalAttributesClause.toString());
            }
         }
      }

      if (this.tableOption != null) {
         sb.append(this.tableOption);
      }

      return sb.toString();
   }
}
