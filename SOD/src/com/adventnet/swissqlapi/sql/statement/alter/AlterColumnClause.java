package com.adventnet.swissqlapi.sql.statement.alter;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintType;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.sql.statement.create.PrimaryOrUniqueConstraintClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.HashMap;
import java.util.Vector;

public class AlterColumnClause {
   private String alter;
   private String column;
   private String columnName;
   private String add;
   private String scope;
   private String addScopeIdentifier;
   private String drop;
   private String setString;
   private String defaultString;
   private String restrictOrCascade;
   private String defaultValue;
   private FunctionCalls defaultFunctionCalls;
   private SelectColumn selectColumn;
   private CreateColumn createColumn;
   private String diskAttributes;
   private String physicalCharacteristics;
   private String collate;
   private String collationName;
   private String nullOrNotNull;
   private String addOrDropRowguidcol;
   private String rowguidcol;
   private UserObjectContext context = null;
   private String dataTypeStr = null;
   private Datatype dataType = null;

   public void setAlter(String alter) {
      this.alter = alter;
   }

   public void setColumn(String column) {
      this.column = column;
   }

   public void setColumnName(String columnName) {
      this.columnName = columnName;
   }

   public void setAdd(String add) {
      this.add = add;
   }

   public void setScope(String scope) {
      this.scope = scope;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   public void setAddScopeIdentifier(String addScopeIdentifier) {
      this.addScopeIdentifier = addScopeIdentifier;
   }

   public void setDrop(String drop) {
      this.drop = drop;
   }

   public void setSetString(String setString) {
      this.setString = setString;
   }

   public void setDefaultString(String defaultString) {
      this.defaultString = defaultString;
   }

   public void setRestrictOrCascade(String restrictOrCascade) {
      this.restrictOrCascade = restrictOrCascade;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }

   public void setDefaultFunctionCalls(SelectColumn selectColumn) {
      this.selectColumn = selectColumn;
   }

   public void setCreateColumn(CreateColumn createColumn) {
      this.createColumn = createColumn;
   }

   public void setDiskAttributes(String diskAttributes) {
      this.diskAttributes = diskAttributes;
   }

   public void setPhysicalCharacteristics(String physicalCharacteristics) {
      this.physicalCharacteristics = physicalCharacteristics;
   }

   public void setCollate(String collate) {
      this.collate = collate;
   }

   public void setCollationName(String collationName) {
      this.collationName = collationName;
   }

   public void setNullOrNotNull(String nullOrNotNull) {
      this.nullOrNotNull = nullOrNotNull;
   }

   public void setAddOrDropRowguidcol(String addOrDropRowguidcol) {
      this.addOrDropRowguidcol = addOrDropRowguidcol;
   }

   public void setRowguidcol(String rowguidcol) {
      this.rowguidcol = rowguidcol;
   }

   public void setDataTypeStr(String datatype) {
      this.dataTypeStr = datatype;
   }

   public void setDataType(Datatype datatype) {
      this.dataType = datatype;
   }

   public String getAlter() {
      return this.alter;
   }

   public String getColumn() {
      return this.column;
   }

   public String getColumnName() {
      return this.columnName;
   }

   public String getAdd() {
      return this.add;
   }

   public String getScope() {
      return this.scope;
   }

   public String getAddScopeIdentifier() {
      return this.addScopeIdentifier;
   }

   public String getDrop() {
      return this.drop;
   }

   public String getSetString() {
      return this.setString;
   }

   public String getDefaultString() {
      return this.defaultString;
   }

   public String getRestrictOrCascade() {
      return this.restrictOrCascade;
   }

   public String getDefaultValue() {
      return this.defaultValue;
   }

   public SelectColumn getDefaultFunctionCalls() {
      return this.selectColumn;
   }

   public CreateColumn getCreateColumn() {
      return this.createColumn;
   }

   public String getPhysicalCharacteristics() {
      return this.physicalCharacteristics;
   }

   public String getDiskAttributes() {
      return this.diskAttributes;
   }

   public String getCollate() {
      return this.collate;
   }

   public String getCollationName() {
      return this.collationName;
   }

   public String getNullorNotNull() {
      return this.nullOrNotNull;
   }

   public String getAddOrDropRowguidcol() {
      return this.addOrDropRowguidcol;
   }

   public String getRowguidcol() {
      return this.rowguidcol;
   }

   public String getDataTypeStr() {
      return this.dataTypeStr;
   }

   public Datatype getDataType() {
      return this.dataType;
   }

   public AlterColumnClause toOracle() throws ConvertException {
      AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
      if (tempAlterColumnClause.getAlter() != null) {
         tempAlterColumnClause.setAlter("MODIFY");
      }

      tempAlterColumnClause.setColumn((String)null);
      if (tempAlterColumnClause.getCreateColumn() != null) {
         CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
         changeCreateColumn.toOracleString();
         String colName = changeCreateColumn.getColumnName();
         if (colName != null) {
            boolean addQuotes = false;
            if (colName.startsWith("\"") && colName.endsWith("\"")) {
               colName = colName.substring(1, colName.length() - 1);
               addQuotes = true;
            }

            if (colName.length() > 30) {
               colName = colName.substring(0, 30);
               if (addQuotes) {
                  colName = "\"" + colName + "\"";
               }

               changeCreateColumn.setColumnName(colName);
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

      String var11;
      if (tempAlterColumnClause.getPhysicalCharacteristics() != null) {
         var11 = tempAlterColumnClause.getPhysicalCharacteristics();
      }

      tempAlterColumnClause.setCollate((String)null);
      tempAlterColumnClause.setCollationName((String)null);
      tempAlterColumnClause.setAdd((String)null);
      tempAlterColumnClause.setAddScopeIdentifier((String)null);
      if (tempAlterColumnClause.getDrop() != null) {
         var11 = tempAlterColumnClause.getDrop();
      }

      if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
         tempAlterColumnClause.setAddOrDropRowguidcol((String)null);
         tempAlterColumnClause.setDrop((String)null);
      }

      if (tempAlterColumnClause.getDefaultString() != null) {
         tempAlterColumnClause.setSetString((String)null);
         tempAlterColumnClause.setDefaultValue((String)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         return tempAlterColumnClause;
      }
   }

   public AlterColumnClause toMSSQLServer() throws ConvertException {
      AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
      String var2;
      if (tempAlterColumnClause.getAlter() != null) {
         var2 = tempAlterColumnClause.getAlter();
      }

      tempAlterColumnClause.setColumn("COLUMN");
      if (tempAlterColumnClause.getCreateColumn() != null) {
         CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
         changeCreateColumn.toMSSQLServerString();
         if (tempAlterColumnClause.getDiskAttributes() != null) {
            changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            Vector getConstraintVector = changeCreateColumn.getConstraintClause();
            if (getConstraintVector != null) {
               for(int j = 0; j < getConstraintVector.size(); ++j) {
                  ConstraintClause toSQLServerConstraintClause = (ConstraintClause)getConstraintVector.get(j);
                  ConstraintType toSQLServerConstraintType = toSQLServerConstraintClause.getConstraintType();
                  if (toSQLServerConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                     PrimaryOrUniqueConstraintClause pcc = (PrimaryOrUniqueConstraintClause)toSQLServerConstraintType;
                     String fillfactor = tempAlterColumnClause.getDiskAttributes();
                     String tempFillfactor = "";
                     if (fillfactor != null) {
                        int fillIntValue = 0;
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
                        tempAlterColumnClause.setDiskAttributes((String)null);
                        break;
                     }
                  }
               }
            }
         }
      }

      if (tempAlterColumnClause.getAdd() != null) {
         var2 = tempAlterColumnClause.getAdd();
      }

      if (tempAlterColumnClause.getDrop() != null) {
         var2 = tempAlterColumnClause.getDrop();
      }

      if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
         var2 = tempAlterColumnClause.getAddOrDropRowguidcol();
      }

      tempAlterColumnClause.setColumnName((String)null);
      tempAlterColumnClause.setScope((String)null);
      tempAlterColumnClause.setAddScopeIdentifier((String)null);
      if (tempAlterColumnClause.getDefaultString() != null) {
         tempAlterColumnClause.setSetString((String)null);
         tempAlterColumnClause.setDefaultValue((String)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterColumnClause.setDefaultFunctionCalls((SelectColumn)null);
         tempAlterColumnClause.setRestrictOrCascade((String)null);
         if (tempAlterColumnClause.getCollate() != null) {
            var2 = tempAlterColumnClause.getCollate();
         }

         if (tempAlterColumnClause.getCollationName() != null) {
            var2 = tempAlterColumnClause.getCollationName();
         }

         if (tempAlterColumnClause.getNullorNotNull() != null) {
            var2 = tempAlterColumnClause.getNullorNotNull();
         }

         return tempAlterColumnClause;
      }
   }

   public AlterColumnClause toSybase() throws ConvertException {
      AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
      if (tempAlterColumnClause.getAlter() != null) {
         tempAlterColumnClause.setAlter("MODIFY");
      }

      tempAlterColumnClause.setColumn((String)null);
      if (tempAlterColumnClause.getCreateColumn() != null) {
         CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
         changeCreateColumn.toSybaseString();
      }

      tempAlterColumnClause.setDiskAttributes((String)null);
      tempAlterColumnClause.setPhysicalCharacteristics((String)null);
      tempAlterColumnClause.setAdd((String)null);
      tempAlterColumnClause.setDrop((String)null);
      tempAlterColumnClause.setAddOrDropRowguidcol((String)null);
      tempAlterColumnClause.setColumnName((String)null);
      tempAlterColumnClause.setScope((String)null);
      tempAlterColumnClause.setAddScopeIdentifier((String)null);
      if (tempAlterColumnClause.getDefaultString() != null) {
         tempAlterColumnClause.setSetString((String)null);
         tempAlterColumnClause.setDefaultValue((String)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterColumnClause.setDefaultFunctionCalls((SelectColumn)null);
         tempAlterColumnClause.setRestrictOrCascade((String)null);
         if (this.collate != null) {
            tempAlterColumnClause.setCollate("SORTKEY");
         }

         if (this.collationName != null) {
            tempAlterColumnClause.setCollationName(this.collationName);
         }

         if (this.nullOrNotNull != null) {
            tempAlterColumnClause.setNullOrNotNull(this.nullOrNotNull);
         }

         return tempAlterColumnClause;
      }
   }

   public AlterColumnClause toDB2() throws ConvertException {
      AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
      if (tempAlterColumnClause.getAlter() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterColumnClause.setColumn((String)null);
         tempAlterColumnClause.setColumnName((String)null);
         tempAlterColumnClause.setAdd((String)null);
         tempAlterColumnClause.setScope((String)null);
         tempAlterColumnClause.setCreateColumn((CreateColumn)null);
         tempAlterColumnClause.setDiskAttributes((String)null);
         tempAlterColumnClause.setPhysicalCharacteristics((String)null);
         tempAlterColumnClause.setAddOrDropRowguidcol((String)null);
         tempAlterColumnClause.setRowguidcol((String)null);
         tempAlterColumnClause.setAddScopeIdentifier((String)null);
         tempAlterColumnClause.setDrop((String)null);
         tempAlterColumnClause.setSetString((String)null);
         tempAlterColumnClause.setDefaultString((String)null);
         tempAlterColumnClause.setRestrictOrCascade((String)null);
         tempAlterColumnClause.setDefaultValue((String)null);
         tempAlterColumnClause.setDefaultFunctionCalls((SelectColumn)null);
         tempAlterColumnClause.setCollate((String)null);
         tempAlterColumnClause.setCollationName((String)null);
         tempAlterColumnClause.setNullOrNotNull((String)null);
         return tempAlterColumnClause;
      }
   }

   public AlterColumnClause toANSI() throws ConvertException {
      AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
      String var2;
      if (tempAlterColumnClause.getAlter() != null) {
         var2 = tempAlterColumnClause.getAlter();
      }

      if (tempAlterColumnClause.getColumn() != null) {
         var2 = tempAlterColumnClause.getColumn();
      }

      if (tempAlterColumnClause.getColumnName() != null) {
         var2 = tempAlterColumnClause.getColumnName();
      }

      tempAlterColumnClause.setAdd((String)null);
      if (tempAlterColumnClause.getDrop() != null) {
         var2 = tempAlterColumnClause.getDrop();
      }

      if (tempAlterColumnClause.getScope() != null) {
         var2 = tempAlterColumnClause.getScope();
      }

      if (tempAlterColumnClause.getAddScopeIdentifier() != null) {
         var2 = tempAlterColumnClause.getAddScopeIdentifier();
      }

      if (tempAlterColumnClause.getRestrictOrCascade() != null) {
         var2 = tempAlterColumnClause.getRestrictOrCascade();
      }

      if (tempAlterColumnClause.getSetString() != null) {
         tempAlterColumnClause.setDefaultString((String)null);
         tempAlterColumnClause.setDefaultValue((String)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         if (tempAlterColumnClause.getCreateColumn() != null && tempAlterColumnClause.getCreateColumn() instanceof CreateColumn) {
            CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toANSIString();
         }

         tempAlterColumnClause.setDiskAttributes((String)null);
         tempAlterColumnClause.setPhysicalCharacteristics((String)null);
         tempAlterColumnClause.setCollate((String)null);
         tempAlterColumnClause.setCollationName((String)null);
         tempAlterColumnClause.setNullOrNotNull((String)null);
         if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
            tempAlterColumnClause.setAddOrDropRowguidcol((String)null);
            tempAlterColumnClause.setDrop((String)null);
         }

         tempAlterColumnClause.setRowguidcol((String)null);
         return tempAlterColumnClause;
      }
   }

   public AlterColumnClause toMySQL() throws ConvertException {
      AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
      String var2;
      if (tempAlterColumnClause.getAlter() != null) {
         var2 = tempAlterColumnClause.getAlter();
      }

      if (tempAlterColumnClause.getColumn() != null) {
         var2 = tempAlterColumnClause.getColumn();
      }

      if (tempAlterColumnClause.getColumnName() != null) {
         var2 = tempAlterColumnClause.getColumnName();
      }

      if (tempAlterColumnClause.getSetString() != null) {
         var2 = tempAlterColumnClause.getSetString();
      }

      if (tempAlterColumnClause.getDefaultString() != null) {
         var2 = tempAlterColumnClause.getDefaultString();
      }

      if (tempAlterColumnClause.getDrop() != null) {
         var2 = tempAlterColumnClause.getDrop();
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
         } else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
            this.setDefaultValue("USER()");
         }
      } else if (this.getDefaultFunctionCalls() != null) {
         SelectColumn tempSelectColumn = tempAlterColumnClause.getDefaultFunctionCalls();
         SelectColumn oracleSelectColumn = tempSelectColumn.toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
         tempAlterColumnClause.setDefaultFunctionCalls(oracleSelectColumn);
      }

      tempAlterColumnClause.setAdd((String)null);
      tempAlterColumnClause.setScope((String)null);
      if (tempAlterColumnClause.getCreateColumn() != null && tempAlterColumnClause.getCreateColumn() instanceof CreateColumn) {
         CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
         changeCreateColumn.toMySQLString();
      }

      tempAlterColumnClause.setDiskAttributes((String)null);
      tempAlterColumnClause.setPhysicalCharacteristics((String)null);
      if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
         tempAlterColumnClause.setAddOrDropRowguidcol((String)null);
         tempAlterColumnClause.setDrop((String)null);
      }

      tempAlterColumnClause.setRowguidcol((String)null);
      tempAlterColumnClause.setAddScopeIdentifier((String)null);
      tempAlterColumnClause.setRestrictOrCascade((String)null);
      tempAlterColumnClause.setCollate((String)null);
      tempAlterColumnClause.setCollationName((String)null);
      tempAlterColumnClause.setNullOrNotNull((String)null);
      return tempAlterColumnClause;
   }

   public AlterColumnClause toBigQuery() throws ConvertException {
      AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
      String var2;
      if (tempAlterColumnClause.getAlter() != null) {
         var2 = tempAlterColumnClause.getAlter();
      }

      if (tempAlterColumnClause.getColumn() != null) {
         var2 = tempAlterColumnClause.getColumn();
      }

      if (tempAlterColumnClause.getColumnName() != null) {
         var2 = tempAlterColumnClause.getColumnName();
      }

      if (tempAlterColumnClause.getSetString() != null) {
         var2 = tempAlterColumnClause.getSetString();
      }

      if (tempAlterColumnClause.getDefaultString() != null) {
         var2 = tempAlterColumnClause.getDefaultString();
      }

      if (tempAlterColumnClause.getDrop() != null) {
         var2 = tempAlterColumnClause.getDrop();
      }

      if (tempAlterColumnClause.getSetString() != null) {
         tempAlterColumnClause.setDefaultString((String)null);
         tempAlterColumnClause.setDefaultValue((String)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterColumnClause.setAdd((String)null);
         tempAlterColumnClause.setScope((String)null);
         if (tempAlterColumnClause.getCreateColumn() != null && tempAlterColumnClause.getCreateColumn() instanceof CreateColumn) {
            CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toBigQueryString();
         }

         tempAlterColumnClause.setDiskAttributes((String)null);
         tempAlterColumnClause.setPhysicalCharacteristics((String)null);
         if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
            tempAlterColumnClause.setAddOrDropRowguidcol((String)null);
            tempAlterColumnClause.setDrop((String)null);
         }

         tempAlterColumnClause.setRowguidcol((String)null);
         tempAlterColumnClause.setAddScopeIdentifier((String)null);
         tempAlterColumnClause.setRestrictOrCascade((String)null);
         tempAlterColumnClause.setCollate((String)null);
         tempAlterColumnClause.setCollationName((String)null);
         tempAlterColumnClause.setNullOrNotNull((String)null);
         return tempAlterColumnClause;
      }
   }

   public AlterColumnClause toPostgreSQL() throws ConvertException {
      AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
      String var2;
      if (tempAlterColumnClause.getAlter() != null) {
         var2 = tempAlterColumnClause.getAlter();
      }

      if (tempAlterColumnClause.getColumn() != null) {
         var2 = tempAlterColumnClause.getColumn();
      }

      if (tempAlterColumnClause.getColumnName() != null) {
         var2 = tempAlterColumnClause.getColumnName();
      }

      if (tempAlterColumnClause.getSetString() != null) {
         var2 = tempAlterColumnClause.getSetString();
      }

      if (tempAlterColumnClause.getDefaultString() != null) {
         var2 = tempAlterColumnClause.getDefaultString();
      }

      if (tempAlterColumnClause.getDrop() != null) {
         var2 = tempAlterColumnClause.getDrop();
      }

      if (tempAlterColumnClause.getSetString() != null) {
         tempAlterColumnClause.setDefaultString((String)null);
         tempAlterColumnClause.setDefaultValue((String)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterColumnClause.setAdd((String)null);
         tempAlterColumnClause.setScope((String)null);
         if (tempAlterColumnClause.getCreateColumn() != null && tempAlterColumnClause.getCreateColumn() instanceof CreateColumn) {
            CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toPostgreSQLString();
         }

         tempAlterColumnClause.setDiskAttributes((String)null);
         tempAlterColumnClause.setPhysicalCharacteristics((String)null);
         if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
            tempAlterColumnClause.setAddOrDropRowguidcol((String)null);
            tempAlterColumnClause.setDrop((String)null);
         }

         tempAlterColumnClause.setRowguidcol((String)null);
         tempAlterColumnClause.setAddScopeIdentifier((String)null);
         tempAlterColumnClause.setRestrictOrCascade((String)null);
         tempAlterColumnClause.setCollate((String)null);
         tempAlterColumnClause.setCollationName((String)null);
         tempAlterColumnClause.setNullOrNotNull((String)null);
         return tempAlterColumnClause;
      }
   }

   public AlterColumnClause toInformix() throws ConvertException {
      AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
      if (tempAlterColumnClause.getAlter() != null) {
         tempAlterColumnClause.setAlter("MODIFY");
      }

      if (tempAlterColumnClause.getCreateColumn() != null) {
         CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
         changeCreateColumn.toInformixString();
      }

      tempAlterColumnClause.setColumn((String)null);
      tempAlterColumnClause.setColumnName((String)null);
      tempAlterColumnClause.setAdd((String)null);
      tempAlterColumnClause.setScope((String)null);
      tempAlterColumnClause.setDiskAttributes((String)null);
      tempAlterColumnClause.setPhysicalCharacteristics((String)null);
      tempAlterColumnClause.setAddOrDropRowguidcol((String)null);
      tempAlterColumnClause.setRowguidcol((String)null);
      tempAlterColumnClause.setAddScopeIdentifier((String)null);
      tempAlterColumnClause.setDrop((String)null);
      if (tempAlterColumnClause.getSetString() != null) {
         tempAlterColumnClause.setDefaultString((String)null);
         tempAlterColumnClause.setDefaultValue((String)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterColumnClause.setRestrictOrCascade((String)null);
         tempAlterColumnClause.setDefaultFunctionCalls((SelectColumn)null);
         tempAlterColumnClause.setCollate((String)null);
         tempAlterColumnClause.setCollationName((String)null);
         tempAlterColumnClause.setNullOrNotNull((String)null);
         return tempAlterColumnClause;
      }
   }

   public AlterColumnClause toNetezza() throws ConvertException {
      AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
      String var2;
      if (tempAlterColumnClause.getAlter() != null) {
         var2 = tempAlterColumnClause.getAlter();
      }

      if (tempAlterColumnClause.getColumn() != null) {
         var2 = tempAlterColumnClause.getColumn();
      }

      if (tempAlterColumnClause.getColumnName() != null) {
         var2 = tempAlterColumnClause.getColumnName();
      }

      tempAlterColumnClause.setAdd((String)null);
      if (tempAlterColumnClause.getDrop() != null) {
         var2 = tempAlterColumnClause.getDrop();
      }

      if (tempAlterColumnClause.getScope() != null) {
         var2 = tempAlterColumnClause.getScope();
      }

      if (tempAlterColumnClause.getAddScopeIdentifier() != null) {
         var2 = tempAlterColumnClause.getAddScopeIdentifier();
      }

      if (tempAlterColumnClause.getRestrictOrCascade() != null) {
         var2 = tempAlterColumnClause.getRestrictOrCascade();
      }

      if (tempAlterColumnClause.getSetString() != null) {
         tempAlterColumnClause.setDefaultString((String)null);
         tempAlterColumnClause.setDefaultValue((String)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         if (tempAlterColumnClause.getCreateColumn() != null && tempAlterColumnClause.getCreateColumn() instanceof CreateColumn) {
            CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toNetezzaString();
         }

         tempAlterColumnClause.setDiskAttributes((String)null);
         tempAlterColumnClause.setPhysicalCharacteristics((String)null);
         tempAlterColumnClause.setCollate((String)null);
         tempAlterColumnClause.setCollationName((String)null);
         tempAlterColumnClause.setNullOrNotNull((String)null);
         if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
            tempAlterColumnClause.setAddOrDropRowguidcol((String)null);
            tempAlterColumnClause.setDrop((String)null);
         }

         tempAlterColumnClause.setRowguidcol((String)null);
         return tempAlterColumnClause;
      }
   }

   public AlterColumnClause toSnowflake() throws ConvertException {
      AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
      String var2;
      if (tempAlterColumnClause.getAlter() != null) {
         var2 = tempAlterColumnClause.getAlter();
      }

      if (tempAlterColumnClause.getColumn() != null) {
         var2 = tempAlterColumnClause.getColumn();
      }

      if (tempAlterColumnClause.getColumnName() != null) {
         var2 = tempAlterColumnClause.getColumnName();
      }

      if (tempAlterColumnClause.getSetString() != null) {
         var2 = tempAlterColumnClause.getSetString();
      }

      if (tempAlterColumnClause.getDefaultString() != null) {
         var2 = tempAlterColumnClause.getDefaultString();
      }

      if (tempAlterColumnClause.getDrop() != null) {
         var2 = tempAlterColumnClause.getDrop();
      }

      if (tempAlterColumnClause.getSetString() != null) {
         tempAlterColumnClause.setDefaultString((String)null);
         tempAlterColumnClause.setDefaultValue((String)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempAlterColumnClause.setAdd((String)null);
         tempAlterColumnClause.setScope((String)null);
         if (tempAlterColumnClause.getCreateColumn() != null && tempAlterColumnClause.getCreateColumn() instanceof CreateColumn) {
            CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toSnowflakeString();
         }

         tempAlterColumnClause.setDiskAttributes((String)null);
         tempAlterColumnClause.setPhysicalCharacteristics((String)null);
         if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
            tempAlterColumnClause.setAddOrDropRowguidcol((String)null);
            tempAlterColumnClause.setDrop((String)null);
         }

         tempAlterColumnClause.setRowguidcol((String)null);
         tempAlterColumnClause.setAddScopeIdentifier((String)null);
         tempAlterColumnClause.setRestrictOrCascade((String)null);
         tempAlterColumnClause.setCollate((String)null);
         tempAlterColumnClause.setCollationName((String)null);
         tempAlterColumnClause.setNullOrNotNull((String)null);
         return tempAlterColumnClause;
      }
   }

   public AlterColumnClause toTeradata() throws ConvertException {
      AlterColumnClause tempAlterColumnClause = this.copyObjectValues();
      String var2;
      if (tempAlterColumnClause.getAlter() != null) {
         var2 = tempAlterColumnClause.getAlter();
      }

      if (tempAlterColumnClause.getColumn() != null) {
         var2 = tempAlterColumnClause.getColumn();
      }

      if (tempAlterColumnClause.getColumnName() != null) {
         var2 = tempAlterColumnClause.getColumnName();
      }

      tempAlterColumnClause.setAdd((String)null);
      if (tempAlterColumnClause.getDrop() != null) {
         var2 = tempAlterColumnClause.getDrop();
      }

      if (tempAlterColumnClause.getScope() != null) {
         var2 = tempAlterColumnClause.getScope();
      }

      if (tempAlterColumnClause.getAddScopeIdentifier() != null) {
         var2 = tempAlterColumnClause.getAddScopeIdentifier();
      }

      if (tempAlterColumnClause.getRestrictOrCascade() != null) {
         var2 = tempAlterColumnClause.getRestrictOrCascade();
      }

      if (tempAlterColumnClause.getSetString() != null) {
         tempAlterColumnClause.setDefaultString((String)null);
         tempAlterColumnClause.setDefaultValue((String)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         if (tempAlterColumnClause.getCreateColumn() != null && tempAlterColumnClause.getCreateColumn() instanceof CreateColumn) {
            CreateColumn changeCreateColumn = tempAlterColumnClause.getCreateColumn();
            changeCreateColumn.toTeradataString();
         }

         tempAlterColumnClause.setDiskAttributes((String)null);
         tempAlterColumnClause.setPhysicalCharacteristics((String)null);
         tempAlterColumnClause.setCollate((String)null);
         tempAlterColumnClause.setCollationName((String)null);
         tempAlterColumnClause.setNullOrNotNull((String)null);
         if (tempAlterColumnClause.getAddOrDropRowguidcol() != null) {
            tempAlterColumnClause.setAddOrDropRowguidcol((String)null);
            tempAlterColumnClause.setDrop((String)null);
         }

         tempAlterColumnClause.setRowguidcol((String)null);
         return tempAlterColumnClause;
      }
   }

   public AlterColumnClause copyObjectValues() {
      AlterColumnClause dupAlterColumnClause = new AlterColumnClause();
      dupAlterColumnClause.setAlter(this.getAlter());
      dupAlterColumnClause.setColumn(this.getColumn());
      dupAlterColumnClause.setColumnName(this.getCollationName());
      dupAlterColumnClause.setAdd(this.getAdd());
      dupAlterColumnClause.setScope(this.getScope());
      dupAlterColumnClause.setAddScopeIdentifier(this.getAddScopeIdentifier());
      dupAlterColumnClause.setDrop(this.getDrop());
      dupAlterColumnClause.setSetString(this.getSetString());
      dupAlterColumnClause.setDefaultString(this.getDefaultString());
      dupAlterColumnClause.setRestrictOrCascade(this.getRestrictOrCascade());
      dupAlterColumnClause.setDefaultValue(this.getDefaultValue());
      dupAlterColumnClause.setDefaultFunctionCalls(this.getDefaultFunctionCalls());
      dupAlterColumnClause.setCreateColumn(this.getCreateColumn());
      dupAlterColumnClause.setDiskAttributes(this.getDiskAttributes());
      dupAlterColumnClause.setPhysicalCharacteristics(this.getPhysicalCharacteristics());
      dupAlterColumnClause.setCollate(this.getCollate());
      dupAlterColumnClause.setCollationName(this.getCollationName());
      dupAlterColumnClause.setNullOrNotNull(this.getNullorNotNull());
      dupAlterColumnClause.setAddOrDropRowguidcol(this.getAddOrDropRowguidcol());
      dupAlterColumnClause.setObjectContext(this.context);
      return dupAlterColumnClause;
   }

   public String removeIndent(String str) {
      str = str.replace('\n', ' ');
      str = str.replace('\t', ' ');
      return str;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.alter != null) {
         sb.append(this.alter.toUpperCase());
      }

      if (this.column != null) {
         sb.append(" " + this.column.toUpperCase());
      }

      if (this.createColumn != null) {
         this.createColumn.setObjectContext(this.context);
         sb.append("\n\t" + this.createColumn.toString());
      }

      if (this.diskAttributes != null) {
         sb.append(" " + this.diskAttributes.toUpperCase());
      }

      if (this.physicalCharacteristics != null) {
         sb.append(" " + this.physicalCharacteristics.toUpperCase());
      }

      if (this.collate != null) {
         sb.append("\n" + this.collate.toUpperCase());
      }

      if (this.collationName != null) {
         sb.append(" " + this.collationName);
      }

      if (this.nullOrNotNull != null) {
         sb.append(" " + this.nullOrNotNull.toUpperCase());
      }

      if (this.add != null) {
         sb.append("\n" + this.add.toUpperCase());
      }

      if (this.drop != null) {
         sb.append("\n" + this.drop.toUpperCase());
      }

      if (this.setString != null) {
         sb.append("\n" + this.setString.toUpperCase());
      }

      if (this.defaultString != null) {
         sb.append(" " + this.defaultString.toUpperCase());
      }

      if (this.scope != null) {
         sb.append(" " + this.scope.toUpperCase());
      }

      if (this.addScopeIdentifier != null) {
         sb.append(" " + this.addScopeIdentifier);
      }

      if (this.restrictOrCascade != null) {
         sb.append(" " + this.restrictOrCascade.toUpperCase());
      }

      if (this.defaultValue != null) {
         sb.append(" " + this.defaultValue);
      }

      if (this.defaultFunctionCalls != null) {
         this.defaultFunctionCalls.setObjectContext(this.context);
         sb.append(" " + this.defaultFunctionCalls);
      }

      if (this.dataTypeStr != null) {
         sb.append(" " + this.dataTypeStr);
      }

      if (this.dataType != null) {
         sb.append(" " + this.dataType);
      }

      if (this.addOrDropRowguidcol != null) {
         sb.append(" " + this.addOrDropRowguidcol.toUpperCase());
      }

      return sb.toString();
   }
}
