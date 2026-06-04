package com.adventnet.swissqlapi.sql.statement.alter;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.PartitionListAttributes;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import java.util.Vector;

public class DropClause {
   private UserObjectContext context = null;
   private String drop;
   private String restrictOrCascade;
   private String checkOrNoCheck;
   private String constraintOrColumnName;
   private String constraintTypeOrTrigger;
   private ConstraintClause constraintClause;
   private CreateColumn createColumn;
   private String column;
   private String openBraces;
   private String closedBraces;
   private Vector columnNamesVector;
   private String all;
   private Vector columnOrConstraintOrTriggerNameVector;
   private boolean isOpenBracesForConstraintSet;
   private boolean isColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne = false;
   private String db2ConstraintName;
   private PartitionListAttributes partitionListAttributes;
   private String partitioningKey;
   private String index;
   private String indexName;

   public void setDrop(String drop) {
      this.drop = drop;
   }

   public void setColumn(String column) {
      this.column = column;
   }

   public void setOpenBraces(String openBraces) {
      this.openBraces = openBraces;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   public void setColumnNamesVector(Vector columnNamesVector) {
      this.columnNamesVector = columnNamesVector;
   }

   public void setClosedBraces(String closedBraces) {
      this.closedBraces = closedBraces;
   }

   public void setRestrictOrCascade(String restrictOrCascade) {
      this.restrictOrCascade = restrictOrCascade;
   }

   public void setCreateColumn(CreateColumn createColumn) {
      this.createColumn = createColumn;
   }

   public void setConstraintClause(ConstraintClause constraintClause) {
      this.constraintClause = constraintClause;
   }

   public void setConstraintTypeOrTrigger(String constraintTypeOrTrigger) {
      this.constraintTypeOrTrigger = constraintTypeOrTrigger;
   }

   public void setConstraintOrColumnName(String constraintOrColumnName) {
      this.constraintOrColumnName = constraintOrColumnName;
   }

   public void setCheckOrNoCheck(String checkOrNoCheck) {
      this.checkOrNoCheck = checkOrNoCheck;
   }

   public void setAll(String all) {
      this.all = all;
   }

   public void setDB2ConstraintName(String db2ConstraintName) {
      this.db2ConstraintName = db2ConstraintName;
   }

   public void setOpenBracesForConstraint(boolean isOpenBracesForConstraintSet) {
      this.isOpenBracesForConstraintSet = isOpenBracesForConstraintSet;
   }

   public void setColumnOrConstraintOrTriggerNameVector(Vector columnOrConstraintOrTriggerNameVector) {
      this.columnOrConstraintOrTriggerNameVector = columnOrConstraintOrTriggerNameVector;
   }

   public void setColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne(boolean isColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne) {
      this.isColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne = isColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne;
   }

   public void setPartition(PartitionListAttributes partitionListAttributes) {
      this.partitionListAttributes = partitionListAttributes;
   }

   public void setPartitioningKey(String partitioningKey) {
      this.partitioningKey = partitioningKey;
   }

   public void setIndex(String index) {
      this.index = index;
   }

   public void setIndexName(String indexName) {
      this.indexName = indexName;
   }

   public String getDrop() {
      return this.drop;
   }

   public String getColumn() {
      return this.column;
   }

   public String getOpenBraces() {
      return this.openBraces;
   }

   public Vector getColumnNamesVector() {
      return this.columnNamesVector;
   }

   public String getClosedBraces() {
      return this.closedBraces;
   }

   public String getAll() {
      return this.all;
   }

   public Vector getColumnOrConstraintOrTriggerNameVector() {
      return this.columnOrConstraintOrTriggerNameVector;
   }

   public String getRestrictOrCascade() {
      return this.restrictOrCascade;
   }

   public String getConstraintTypeOrTrigger() {
      return this.constraintTypeOrTrigger;
   }

   public String getConstraintOrColumnName() {
      return this.constraintOrColumnName;
   }

   public String getCheckOrNoCheck() {
      return this.checkOrNoCheck;
   }

   public CreateColumn getCreateColumn() {
      return this.createColumn;
   }

   public ConstraintClause getConstraintClause() {
      return this.constraintClause;
   }

   public PartitionListAttributes getPartition() {
      return this.partitionListAttributes;
   }

   public String getPartitioningKey() {
      return this.partitioningKey;
   }

   public String getIndex() {
      return this.index;
   }

   public String getIndexName() {
      return this.indexName;
   }

   public DropClause toOracle() throws ConvertException {
      DropClause tempDropClause = this.copyObjectValues();
      String tempRestrictOrCascade;
      if (tempDropClause.getDrop() != null) {
         tempRestrictOrCascade = tempDropClause.getDrop();
         if (tempRestrictOrCascade.toUpperCase().equalsIgnoreCase("DELETE")) {
            tempDropClause.setDrop("DROP");
         }
      }

      if (tempDropClause.getColumn() != null) {
         tempRestrictOrCascade = tempDropClause.getColumn();
      }

      if (tempDropClause.getOpenBraces() != null) {
         tempRestrictOrCascade = tempDropClause.getOpenBraces();
      }

      if (tempDropClause.getClosedBraces() != null) {
         tempRestrictOrCascade = tempDropClause.getClosedBraces();
      }

      int i;
      String columnOrConstraintOrTriggerName;
      Vector tempColumnOrConstraintOrTriggerNameVector;
      if (tempDropClause.getColumnNamesVector() != null) {
         tempColumnOrConstraintOrTriggerNameVector = tempDropClause.getColumnNamesVector();

         for(i = 0; i < tempColumnOrConstraintOrTriggerNameVector.size(); ++i) {
            columnOrConstraintOrTriggerName = (String)tempColumnOrConstraintOrTriggerNameVector.get(i);
            boolean addQuotes = false;
            if (columnOrConstraintOrTriggerName.startsWith("\"") && columnOrConstraintOrTriggerName.endsWith("\"")) {
               columnOrConstraintOrTriggerName = columnOrConstraintOrTriggerName.substring(1, columnOrConstraintOrTriggerName.length() - 1);
               addQuotes = true;
            }

            if (columnOrConstraintOrTriggerName.length() > 30) {
               columnOrConstraintOrTriggerName = columnOrConstraintOrTriggerName.substring(0, 30);
               if (addQuotes) {
                  columnOrConstraintOrTriggerName = "\"" + columnOrConstraintOrTriggerName + "\"";
               }

               tempColumnOrConstraintOrTriggerNameVector.setElementAt(columnOrConstraintOrTriggerName, i);
            }
         }

         if (tempColumnOrConstraintOrTriggerNameVector.size() > 1) {
            tempDropClause.setOpenBraces("(");
            tempDropClause.setClosedBraces(")");
            tempDropClause.setColumn((String)null);
         }
      }

      if (tempDropClause.getPartition() != null) {
         PartitionListAttributes tempPartitionListAttributes = tempDropClause.getPartition();
         PartitionListAttributes var8 = tempPartitionListAttributes.toOracle();
      }

      tempDropClause.setConstraintClause((ConstraintClause)null);
      if (tempDropClause.getConstraintTypeOrTrigger() != null) {
         tempRestrictOrCascade = tempDropClause.getConstraintTypeOrTrigger();
         tempRestrictOrCascade = tempRestrictOrCascade.trim();
         tempRestrictOrCascade = tempRestrictOrCascade.toUpperCase();
         if (!tempRestrictOrCascade.equalsIgnoreCase("CONSTRAINT") && !tempRestrictOrCascade.equalsIgnoreCase("UNIQUE") && !tempRestrictOrCascade.startsWith("PRIMARY")) {
            tempDropClause.setColumnOrConstraintOrTriggerNameVector((Vector)null);
            throw new ConvertException();
         }

         tempDropClause.setConstraintTypeOrTrigger(tempRestrictOrCascade);
         if (tempRestrictOrCascade.equalsIgnoreCase("UNIQUE")) {
            tempDropClause.setOpenBracesForConstraint(true);
         } else {
            tempDropClause.setOpenBracesForConstraint(false);
         }
      }

      if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
         tempColumnOrConstraintOrTriggerNameVector = tempDropClause.getColumnOrConstraintOrTriggerNameVector();

         for(i = 0; i < tempColumnOrConstraintOrTriggerNameVector.size(); ++i) {
            columnOrConstraintOrTriggerName = (String)tempColumnOrConstraintOrTriggerNameVector.get(i);
            if (columnOrConstraintOrTriggerName.startsWith("[") && columnOrConstraintOrTriggerName.endsWith("]") || columnOrConstraintOrTriggerName.startsWith("`") && columnOrConstraintOrTriggerName.endsWith("`")) {
               columnOrConstraintOrTriggerName = columnOrConstraintOrTriggerName.substring(1, columnOrConstraintOrTriggerName.length() - 1);
               if (SwisSQLOptions.retainQuotedIdentifierForOracle || columnOrConstraintOrTriggerName.indexOf(32) != -1) {
                  columnOrConstraintOrTriggerName = "\"" + columnOrConstraintOrTriggerName + "\"";
               }
            }

            columnOrConstraintOrTriggerName = CustomizeUtil.objectNamesToQuotedIdentifier(columnOrConstraintOrTriggerName, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
            tempColumnOrConstraintOrTriggerNameVector.setElementAt(columnOrConstraintOrTriggerName, i);
         }
      }

      if (tempDropClause.getRestrictOrCascade() != null) {
         tempRestrictOrCascade = tempDropClause.getRestrictOrCascade();
         tempRestrictOrCascade = tempRestrictOrCascade.trim();
         tempRestrictOrCascade = tempRestrictOrCascade.toUpperCase();
         if (tempRestrictOrCascade.equalsIgnoreCase("CASCADE")) {
            tempDropClause.setRestrictOrCascade("CASCADE");
         } else {
            tempDropClause.setRestrictOrCascade((String)null);
         }
      }

      if (tempDropClause.getAll() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempDropClause.setCheckOrNoCheck((String)null);
         tempDropClause.setAll((String)null);
         if (tempDropClause.getPartitioningKey() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            return tempDropClause;
         }
      }
   }

   public DropClause toMSSQLServer() throws ConvertException {
      DropClause tempDropClause = this.copyObjectValues();
      tempDropClause.setOpenBraces((String)null);
      tempDropClause.setClosedBraces((String)null);
      String tempConstraintTypeOrTrigger;
      if (tempDropClause.getDrop() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getDrop();
         if (tempConstraintTypeOrTrigger.toUpperCase().equalsIgnoreCase("DELETE")) {
            tempDropClause.setDrop("DROP");
         }
      }

      Vector tempColumnNamesVector;
      if (tempDropClause.getColumnNamesVector() != null) {
         tempDropClause.setColumn("COLUMN");
         tempColumnNamesVector = tempDropClause.getColumnNamesVector();

         for(int i = 0; i < tempColumnNamesVector.size(); ++i) {
            String var4 = (String)tempColumnNamesVector.get(i);
         }
      }

      tempDropClause.setOpenBracesForConstraint(false);
      if (tempDropClause.getCheckOrNoCheck() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getCheckOrNoCheck();
      }

      if (tempDropClause.getConstraintTypeOrTrigger() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
         tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
         tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
         if (tempConstraintTypeOrTrigger.equalsIgnoreCase("CONSTRAINT")) {
            tempDropClause.setConstraintTypeOrTrigger("CONSTRAINT");
         } else if (!tempConstraintTypeOrTrigger.startsWith("ENABLE") && !tempConstraintTypeOrTrigger.startsWith("DISABLE")) {
            if (!tempConstraintTypeOrTrigger.equalsIgnoreCase("FOREIGN KEY")) {
               tempDropClause.setConstraintTypeOrTrigger("MSSQLSERVER DOES NOT SUPPORT THIS QUERY OF DROP CLAUSE");
               tempDropClause.setColumnOrConstraintOrTriggerNameVector((Vector)null);
               throw new ConvertException();
            }

            tempDropClause.setConstraintTypeOrTrigger("CONSTRAINT");
         } else {
            tempDropClause.setConstraintTypeOrTrigger(tempConstraintTypeOrTrigger);
         }
      }

      if (tempDropClause.getAll() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getAll();
      }

      if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
         tempColumnNamesVector = tempDropClause.getColumnOrConstraintOrTriggerNameVector();
      }

      if (tempDropClause.getPartition() != null) {
         tempDropClause.setPartition((PartitionListAttributes)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempDropClause.setRestrictOrCascade((String)null);
         tempDropClause.setConstraintClause((ConstraintClause)null);
         if (tempDropClause.getPartitioningKey() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            return tempDropClause;
         }
      }
   }

   public DropClause toSybase() throws ConvertException {
      DropClause tempDropClause = this.copyObjectValues();
      tempDropClause.setOpenBraces((String)null);
      tempDropClause.setClosedBraces((String)null);
      String tempConstraintTypeOrTrigger;
      if (tempDropClause.getDrop() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getDrop();
      }

      Vector tempColumnNamesVector;
      if (tempDropClause.getColumnNamesVector() != null) {
         tempDropClause.setColumn((String)null);
         tempColumnNamesVector = tempDropClause.getColumnNamesVector();

         for(int i = 0; i < tempColumnNamesVector.size(); ++i) {
            String var4 = (String)tempColumnNamesVector.get(i);
         }
      }

      tempDropClause.setOpenBracesForConstraint(false);
      if (tempDropClause.getCheckOrNoCheck() != null) {
         tempDropClause.setCheckOrNoCheck("CONSTRAINT");
      }

      if (tempDropClause.getConstraintTypeOrTrigger() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
         tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
         tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
         if (tempConstraintTypeOrTrigger.equalsIgnoreCase("CONSTRAINT")) {
            tempDropClause.setConstraintTypeOrTrigger("CONSTRAINT");
         } else {
            if (!tempConstraintTypeOrTrigger.startsWith("ENABLE") && !tempConstraintTypeOrTrigger.startsWith("DISABLE")) {
               tempDropClause.setConstraintTypeOrTrigger("Sybase DOES NOT SUPPORT THIS QUERY OF DROP CLAUSE");
               tempDropClause.setColumnOrConstraintOrTriggerNameVector((Vector)null);
               throw new ConvertException();
            }

            tempDropClause.setConstraintTypeOrTrigger(tempConstraintTypeOrTrigger);
         }
      }

      if (tempDropClause.getAll() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getAll();
      }

      if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
         tempColumnNamesVector = tempDropClause.getColumnOrConstraintOrTriggerNameVector();
      }

      if (tempDropClause.getPartition() != null) {
         tempDropClause.setPartition((PartitionListAttributes)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempDropClause.setRestrictOrCascade((String)null);
         tempDropClause.setConstraintClause((ConstraintClause)null);
         if (tempDropClause.getPartitioningKey() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            return tempDropClause;
         }
      }
   }

   public DropClause toDB2() throws ConvertException {
      DropClause tempDropClause = this.copyObjectValues();
      String tempConstraintTypeOrTrigger;
      if (tempDropClause.getDrop() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getDrop();
         if (tempConstraintTypeOrTrigger.toUpperCase().equalsIgnoreCase("DELETE")) {
            tempDropClause.setDrop("DROP");
         }
      }

      if (tempDropClause.getCheckOrNoCheck() != null) {
         tempDropClause.setColumnOrConstraintOrTriggerNameVector((Vector)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempDropClause.setOpenBracesForConstraint(false);
         if (tempDropClause.getConstraintTypeOrTrigger() != null) {
            tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
            if (!tempConstraintTypeOrTrigger.equalsIgnoreCase("CONSTRAINT") && !tempConstraintTypeOrTrigger.equalsIgnoreCase("PRIMARY KEY") && !tempConstraintTypeOrTrigger.equalsIgnoreCase("FOREIGN KEY") && !tempConstraintTypeOrTrigger.equalsIgnoreCase("UNIQUE") && !tempConstraintTypeOrTrigger.equalsIgnoreCase("CHECK")) {
               tempDropClause.setConstraintTypeOrTrigger("DB2 DOES NOT SUPPORT THIS QUERY OF DROP CLAUSE");
               tempDropClause.setDB2ConstraintName((String)null);
               throw new ConvertException();
            }

            tempDropClause.setConstraintTypeOrTrigger(tempConstraintTypeOrTrigger);
            tempDropClause.setDB2ConstraintName(tempConstraintTypeOrTrigger);
         }

         if (tempDropClause.getPartitioningKey() != null) {
            tempConstraintTypeOrTrigger = tempDropClause.getPartitioningKey();
         }

         Vector tempColumnOrConstraintOrTriggerNameVector;
         if (tempDropClause.getColumnNamesVector() != null) {
            tempDropClause.setColumnNamesVector((Vector)null);
            tempColumnOrConstraintOrTriggerNameVector = tempDropClause.getColumnNamesVector();
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
               tempColumnOrConstraintOrTriggerNameVector = tempDropClause.getColumnOrConstraintOrTriggerNameVector();
               if (tempColumnOrConstraintOrTriggerNameVector.size() > 1) {
                  tempDropClause.setColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne(true);
               }
            }

            tempDropClause.setOpenBraces((String)null);
            tempDropClause.setClosedBraces((String)null);
            tempDropClause.setColumn((String)null);
            tempDropClause.setConstraintClause((ConstraintClause)null);
            if (tempDropClause.getAll() != null) {
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               tempDropClause.setAll((String)null);
               tempDropClause.setRestrictOrCascade((String)null);
               if (tempDropClause.getPartition() != null) {
                  tempDropClause.setPartition((PartitionListAttributes)null);
                  throw new ConvertException("Conversion Failure.. Invalid Query");
               } else {
                  return tempDropClause;
               }
            }
         }
      }
   }

   public DropClause toANSI() throws ConvertException {
      DropClause tempDropClause = this.copyObjectValues();
      String tempConstraintTypeOrTrigger;
      if (tempDropClause.getDrop() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getDrop();
         if (tempConstraintTypeOrTrigger.toUpperCase().equalsIgnoreCase("DELETE")) {
            tempDropClause.setDrop("DROP");
         }
      }

      if (tempDropClause.getColumn() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getColumn();
      }

      Vector tempColumnNamesVector;
      if (tempDropClause.getColumnNamesVector() != null) {
         tempColumnNamesVector = tempDropClause.getColumnNamesVector();

         for(int i = 0; i < tempColumnNamesVector.size(); ++i) {
            if (tempColumnNamesVector.get(i) instanceof CreateColumn) {
               String var4 = (String)tempColumnNamesVector.get(i);
            }
         }
      }

      tempDropClause.setOpenBracesForConstraint(false);
      if (tempDropClause.getConstraintTypeOrTrigger() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
         tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
         tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
         if (!tempConstraintTypeOrTrigger.equalsIgnoreCase("CONSTRAINT")) {
            throw new ConvertException();
         }

         tempDropClause.setConstraintTypeOrTrigger("CONSTRAINT");
      }

      if (tempDropClause.getRestrictOrCascade() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getRestrictOrCascade();
      }

      if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
         tempColumnNamesVector = tempDropClause.getColumnOrConstraintOrTriggerNameVector();
      }

      tempDropClause.setOpenBraces((String)null);
      tempDropClause.setClosedBraces((String)null);
      tempDropClause.setConstraintClause((ConstraintClause)null);
      if (tempDropClause.getAll() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempDropClause.setCheckOrNoCheck((String)null);
         tempDropClause.setAll((String)null);
         if (tempDropClause.getPartition() != null) {
            tempDropClause.setPartition((PartitionListAttributes)null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else if (tempDropClause.getPartitioningKey() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            return tempDropClause;
         }
      }
   }

   public DropClause toInformix() throws ConvertException {
      DropClause tempDropClause = this.copyObjectValues();
      String tempDrop;
      if (tempDropClause.getDrop() != null) {
         tempDrop = tempDropClause.getDrop();
         if (tempDrop.toUpperCase().equalsIgnoreCase("DELETE")) {
            tempDropClause.setDrop("DROP");
         }
      }

      if (tempDropClause.getColumn() != null) {
         tempDrop = tempDropClause.getColumn();
      }

      Vector tempColumnNamesVector;
      if (tempDropClause.getColumnNamesVector() != null) {
         tempColumnNamesVector = tempDropClause.getColumnNamesVector();

         for(int i = 0; i < tempColumnNamesVector.size(); ++i) {
            if (tempColumnNamesVector.get(i) instanceof CreateColumn) {
               String var4 = (String)tempColumnNamesVector.get(i);
            }
         }
      }

      tempDropClause.setOpenBracesForConstraint(false);
      if (tempDropClause.getConstraintTypeOrTrigger() != null) {
         tempDrop = tempDropClause.getConstraintTypeOrTrigger();
      }

      if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
         tempColumnNamesVector = tempDropClause.getColumnOrConstraintOrTriggerNameVector();
      }

      tempDropClause.setOpenBraces((String)null);
      tempDropClause.setClosedBraces((String)null);
      tempDropClause.setConstraintClause((ConstraintClause)null);
      if (tempDropClause.getPartition() != null) {
         tempDropClause.setPartition((PartitionListAttributes)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else if (tempDropClause.getPartitioningKey() != null) {
         tempDropClause.setPartitioningKey("INFORMIX DOES NOT SUPPORT PARTITIONING KEY");
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else if (tempDropClause.getAll() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempDropClause.setCheckOrNoCheck((String)null);
         tempDropClause.setRestrictOrCascade((String)null);
         tempDropClause.setAll((String)null);
         return tempDropClause;
      }
   }

   public DropClause toBigQuery() throws ConvertException {
      DropClause tempDropClause = this.copyObjectValues();
      tempDropClause.setRestrictOrCascade((String)null);
      tempDropClause.setConstraintClause((ConstraintClause)null);
      tempDropClause.setConstraintTypeOrTrigger((String)null);
      tempDropClause.setOpenBraces((String)null);
      tempDropClause.setClosedBraces((String)null);
      tempDropClause.setOpenBracesForConstraint(false);
      tempDropClause.setCheckOrNoCheck((String)null);
      tempDropClause.setAll((String)null);
      tempDropClause.setColumnOrConstraintOrTriggerNameVector((Vector)null);
      if (tempDropClause.getPartition() != null) {
         tempDropClause.setPartition((PartitionListAttributes)null);
         tempDropClause.setDrop("BigQuery does not support Drop Clause with partition of Tables");
      }

      if (tempDropClause.getPartitioningKey() != null) {
         tempDropClause.setPartitioningKey("BigQuery DOES NOT SUPPORT PARTITIONING KEY");
      }

      return tempDropClause;
   }

   public DropClause toPostgreSQL() throws ConvertException {
      DropClause tempDropClause = this.copyObjectValues();
      tempDropClause.setRestrictOrCascade((String)null);
      tempDropClause.setConstraintClause((ConstraintClause)null);
      tempDropClause.setConstraintTypeOrTrigger((String)null);
      tempDropClause.setOpenBraces((String)null);
      tempDropClause.setClosedBraces((String)null);
      tempDropClause.setOpenBracesForConstraint(false);
      tempDropClause.setCheckOrNoCheck((String)null);
      tempDropClause.setAll((String)null);
      tempDropClause.setColumnOrConstraintOrTriggerNameVector((Vector)null);
      if (tempDropClause.getPartition() != null) {
         tempDropClause.setPartition((PartitionListAttributes)null);
         tempDropClause.setDrop("PostgreSQL does not support Drop Clause with partition of Tables");
      }

      if (tempDropClause.getPartitioningKey() != null) {
         tempDropClause.setPartitioningKey("POSTGRESQL DOES NOT SUPPORT PARTITIONING KEY");
      }

      return tempDropClause;
   }

   public DropClause toMySQL() throws ConvertException {
      DropClause tempDropClause = this.copyObjectValues();
      String tempConstraintTypeOrTrigger;
      if (tempDropClause.getDrop() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getDrop();
         if (tempConstraintTypeOrTrigger.toUpperCase().equalsIgnoreCase("DELETE")) {
            tempDropClause.setDrop("DROP");
         }
      }

      if (tempDropClause.getColumn() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getColumn();
      }

      Vector tempColumnNamesVector;
      if (tempDropClause.getColumnNamesVector() != null) {
         tempColumnNamesVector = tempDropClause.getColumnNamesVector();

         for(int i = 0; i < tempColumnNamesVector.size(); ++i) {
            if (tempColumnNamesVector.get(i) instanceof CreateColumn) {
               String var4 = (String)tempColumnNamesVector.get(i);
            }
         }
      }

      if (tempDropClause.getConstraintTypeOrTrigger() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
         tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
         tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
         if (!tempConstraintTypeOrTrigger.startsWith("PRIMARY") && !tempConstraintTypeOrTrigger.equalsIgnoreCase("INDEX")) {
            throw new ConvertException();
         }

         tempDropClause.setConstraintTypeOrTrigger(tempConstraintTypeOrTrigger);
      }

      tempDropClause.setOpenBracesForConstraint(false);
      if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
         tempColumnNamesVector = tempDropClause.getColumnOrConstraintOrTriggerNameVector();
      }

      tempDropClause.setOpenBraces((String)null);
      tempDropClause.setClosedBraces((String)null);
      tempDropClause.setRestrictOrCascade((String)null);
      tempDropClause.setConstraintClause((ConstraintClause)null);
      tempDropClause.setCheckOrNoCheck((String)null);
      tempDropClause.setAll((String)null);
      if (tempDropClause.getPartition() != null) {
         tempDropClause.setPartition((PartitionListAttributes)null);
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else if (tempDropClause.getPartitioningKey() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         return tempDropClause;
      }
   }

   public DropClause toSnowflake() throws ConvertException {
      DropClause tempDropClause = this.copyObjectValues();
      tempDropClause.setRestrictOrCascade((String)null);
      tempDropClause.setConstraintClause((ConstraintClause)null);
      tempDropClause.setConstraintTypeOrTrigger((String)null);
      tempDropClause.setOpenBraces((String)null);
      tempDropClause.setClosedBraces((String)null);
      tempDropClause.setOpenBracesForConstraint(false);
      tempDropClause.setCheckOrNoCheck((String)null);
      tempDropClause.setAll((String)null);
      tempDropClause.setColumnOrConstraintOrTriggerNameVector((Vector)null);
      if (tempDropClause.getPartition() != null) {
         tempDropClause.setPartition((PartitionListAttributes)null);
         tempDropClause.setDrop("Snowflake does not support Drop Clause with partition of Tables");
      }

      if (tempDropClause.getPartitioningKey() != null) {
         tempDropClause.setPartitioningKey("Snowflake DOES NOT SUPPORT PARTITIONING KEY");
      }

      return tempDropClause;
   }

   public DropClause toTimesTen() throws ConvertException {
      DropClause tempDropClause = this.copyObjectValues();
      if (tempDropClause.getDrop() != null) {
         String tempDrop = tempDropClause.getDrop();
         if (tempDrop.toUpperCase().equalsIgnoreCase("DELETE")) {
            tempDropClause.setDrop("DROP");
         }
      }

      tempDropClause.setOpenBracesForConstraint(false);
      if (this.columnNamesVector != null && this.columnNamesVector.size() > 1) {
         tempDropClause.setOpenBraces("(");
         tempDropClause.setClosedBraces(")");
      }

      tempDropClause.setConstraintClause((ConstraintClause)null);
      if (tempDropClause.getAll() != null) {
         throw new ConvertException("\nUnsupported SQL.\n");
      } else {
         tempDropClause.setCheckOrNoCheck((String)null);
         tempDropClause.setAll((String)null);
         if (tempDropClause.getPartition() != null) {
            tempDropClause.setPartition((PartitionListAttributes)null);
            throw new ConvertException("\nUnsupported SQL.\n");
         } else if (tempDropClause.getPartitioningKey() != null) {
            throw new ConvertException("\nUnsupported SQL.\n");
         } else {
            return tempDropClause;
         }
      }
   }

   public DropClause toNetezza() throws ConvertException {
      DropClause tempDropClause = this.copyObjectValues();
      String tempConstraintTypeOrTrigger;
      if (tempDropClause.getDrop() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getDrop();
         if (tempConstraintTypeOrTrigger.toUpperCase().equalsIgnoreCase("DELETE")) {
            tempDropClause.setDrop("DROP");
         } else if (tempConstraintTypeOrTrigger.toUpperCase().equalsIgnoreCase("SET UNUSED")) {
            throw new ConvertException("/*SwisSQL Message: Netezza does not support SET UNUSED clause*/");
         }
      }

      if (tempDropClause.getColumn() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getColumn();
         throw new ConvertException("/*SwisSQL Message: Netezza does not support dropping of table columns*/");
      } else {
         Vector tempColumnNamesVector;
         if (tempDropClause.getColumnNamesVector() != null) {
            tempColumnNamesVector = tempDropClause.getColumnNamesVector();

            for(int i = 0; i < tempColumnNamesVector.size(); ++i) {
               if (tempColumnNamesVector.get(i) instanceof CreateColumn) {
                  String var4 = (String)tempColumnNamesVector.get(i);
               }
            }
         }

         tempDropClause.setOpenBracesForConstraint(false);
         if (tempDropClause.getConstraintTypeOrTrigger() != null) {
            tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
            tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
            if (tempConstraintTypeOrTrigger.equalsIgnoreCase("CONSTRAINT")) {
               tempDropClause.setConstraintTypeOrTrigger("CONSTRAINT");
            } else if (tempConstraintTypeOrTrigger.toLowerCase().startsWith("primary key")) {
               tempDropClause.setConstraintTypeOrTrigger(tempConstraintTypeOrTrigger.toUpperCase().replaceFirst("PRIMARY KEY", "CONSTRAINT"));
            } else {
               if (!tempConstraintTypeOrTrigger.equalsIgnoreCase("unique") && !tempConstraintTypeOrTrigger.equalsIgnoreCase("foreign key")) {
                  throw new ConvertException();
               }

               tempDropClause.setConstraintTypeOrTrigger("CONSTRAINT");
            }
         }

         if (tempDropClause.getRestrictOrCascade() != null) {
            tempConstraintTypeOrTrigger = tempDropClause.getRestrictOrCascade();
         } else {
            tempDropClause.setRestrictOrCascade("CASCADE");
         }

         if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
            tempColumnNamesVector = tempDropClause.getColumnOrConstraintOrTriggerNameVector();
         }

         tempDropClause.setOpenBraces((String)null);
         tempDropClause.setClosedBraces((String)null);
         tempDropClause.setConstraintClause((ConstraintClause)null);
         if (tempDropClause.getAll() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            tempDropClause.setCheckOrNoCheck((String)null);
            tempDropClause.setAll((String)null);
            if (tempDropClause.getPartition() != null) {
               tempDropClause.setPartition((PartitionListAttributes)null);
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else if (tempDropClause.getPartitioningKey() != null) {
               throw new ConvertException("Conversion Failure.. Invalid Query");
            } else {
               return tempDropClause;
            }
         }
      }
   }

   public DropClause toTeradata() throws ConvertException {
      DropClause tempDropClause = this.copyObjectValues();
      String tempConstraintTypeOrTrigger;
      if (tempDropClause.getDrop() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getDrop();
         if (tempConstraintTypeOrTrigger.toUpperCase().equalsIgnoreCase("DELETE")) {
            tempDropClause.setDrop("DROP");
         }
      }

      if (tempDropClause.getColumn() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getColumn();
      }

      Vector tempColumnNamesVector;
      if (tempDropClause.getColumnNamesVector() != null) {
         tempColumnNamesVector = tempDropClause.getColumnNamesVector();

         for(int i = 0; i < tempColumnNamesVector.size(); ++i) {
            if (tempColumnNamesVector.get(i) instanceof CreateColumn) {
               String var4 = (String)tempColumnNamesVector.get(i);
            }
         }
      }

      tempDropClause.setOpenBracesForConstraint(false);
      if (tempDropClause.getConstraintTypeOrTrigger() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getConstraintTypeOrTrigger();
         tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.trim();
         tempConstraintTypeOrTrigger = tempConstraintTypeOrTrigger.toUpperCase();
         if (!tempConstraintTypeOrTrigger.equalsIgnoreCase("CONSTRAINT")) {
            throw new ConvertException();
         }

         tempDropClause.setConstraintTypeOrTrigger("CONSTRAINT");
      }

      if (tempDropClause.getRestrictOrCascade() != null) {
         tempConstraintTypeOrTrigger = tempDropClause.getRestrictOrCascade();
      }

      if (tempDropClause.getColumnOrConstraintOrTriggerNameVector() != null) {
         tempColumnNamesVector = tempDropClause.getColumnOrConstraintOrTriggerNameVector();
      }

      tempDropClause.setOpenBraces((String)null);
      tempDropClause.setClosedBraces((String)null);
      tempDropClause.setConstraintClause((ConstraintClause)null);
      if (tempDropClause.getAll() != null) {
         throw new ConvertException("Conversion Failure.. Invalid Query");
      } else {
         tempDropClause.setCheckOrNoCheck((String)null);
         tempDropClause.setAll((String)null);
         if (tempDropClause.getPartition() != null) {
            tempDropClause.setPartition((PartitionListAttributes)null);
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else if (tempDropClause.getPartitioningKey() != null) {
            throw new ConvertException("Conversion Failure.. Invalid Query");
         } else {
            return tempDropClause;
         }
      }
   }

   public DropClause copyObjectValues() {
      DropClause dupDropClause = new DropClause();
      dupDropClause.setDrop(this.getDrop());
      dupDropClause.setColumn(this.getColumn());
      dupDropClause.setColumnNamesVector(this.getColumnNamesVector());
      dupDropClause.setRestrictOrCascade(this.getRestrictOrCascade());
      dupDropClause.setObjectContext(this.context);
      dupDropClause.setCreateColumn(this.getCreateColumn());
      dupDropClause.setConstraintClause(this.getConstraintClause());
      dupDropClause.setConstraintTypeOrTrigger(this.getConstraintTypeOrTrigger());
      dupDropClause.setCheckOrNoCheck(this.getCheckOrNoCheck());
      dupDropClause.setAll(this.getAll());
      dupDropClause.setColumnOrConstraintOrTriggerNameVector(this.getColumnOrConstraintOrTriggerNameVector());
      dupDropClause.setPartition(this.getPartition());
      dupDropClause.setPartitioningKey(this.getPartitioningKey());
      dupDropClause.setClosedBraces(this.getClosedBraces());
      dupDropClause.setOpenBraces(this.getOpenBraces());
      return dupDropClause;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.drop != null) {
         sb.append(this.drop.toUpperCase());
      }

      if (this.column != null) {
         sb.append(" " + this.column.toUpperCase());
      }

      if (this.openBraces != null) {
         sb.append("\n" + this.openBraces);
      }

      int i;
      String columnOrConstraintOrTriggerName;
      if (this.columnNamesVector != null) {
         for(i = 0; i < this.columnNamesVector.size(); ++i) {
            columnOrConstraintOrTriggerName = (String)this.columnNamesVector.get(i);
            if (i == 0) {
               sb.append("\n\t" + columnOrConstraintOrTriggerName);
            } else {
               sb.append(",\n\t" + columnOrConstraintOrTriggerName);
            }
         }
      }

      if (this.closedBraces != null) {
         sb.append("\n" + this.closedBraces);
      }

      if (this.createColumn != null) {
         this.createColumn.setObjectContext(this.context);
         sb.append(this.createColumn.toString());
      }

      if (this.constraintClause != null) {
         this.constraintClause.setObjectContext(this.context);
         sb.append("\n" + this.constraintClause.toString());
      }

      if (this.partitionListAttributes != null) {
         sb.append("\n" + this.partitionListAttributes.toString());
      }

      if (this.partitioningKey != null) {
         sb.append(" " + this.partitioningKey.toUpperCase());
      }

      if (this.checkOrNoCheck != null) {
         sb.append(" " + this.checkOrNoCheck.toUpperCase());
      }

      if (this.constraintTypeOrTrigger != null) {
         sb.append(" " + this.constraintTypeOrTrigger.toUpperCase());
      }

      if (this.all != null) {
         sb.append(" " + this.all.toUpperCase());
      }

      if (this.columnOrConstraintOrTriggerNameVector != null) {
         if (this.isOpenBracesForConstraintSet) {
            sb.append("\n(\n");

            for(i = 0; i < this.columnOrConstraintOrTriggerNameVector.size(); ++i) {
               columnOrConstraintOrTriggerName = (String)this.columnOrConstraintOrTriggerNameVector.get(i);
               if (i == 0) {
                  sb.append("\t" + columnOrConstraintOrTriggerName);
               } else {
                  sb.append(", " + columnOrConstraintOrTriggerName);
               }
            }

            sb.append("\n)");
         } else {
            for(i = 0; i < this.columnOrConstraintOrTriggerNameVector.size(); ++i) {
               columnOrConstraintOrTriggerName = (String)this.columnOrConstraintOrTriggerNameVector.get(i);
               if (i == 0) {
                  sb.append("\n\t " + columnOrConstraintOrTriggerName);
               } else if (this.isColumnOrConstraintOrTriggerNameVectorSizeGreaterThanOne) {
                  if (this.db2ConstraintName != null) {
                     sb.append("\nDROP " + this.db2ConstraintName.toUpperCase() + "\n\t " + columnOrConstraintOrTriggerName);
                  } else {
                     sb.append("\nDROP \n\t " + columnOrConstraintOrTriggerName);
                  }
               } else {
                  sb.append(", " + columnOrConstraintOrTriggerName);
               }
            }
         }
      }

      if (this.restrictOrCascade != null) {
         sb.append(" " + this.restrictOrCascade.toUpperCase());
      }

      return sb.toString();
   }
}
