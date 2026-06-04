package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import java.util.Vector;

public class ForeignConstraintClause implements ConstraintType {
   private Vector constraintColumnNames;
   private String constraintName;
   private String openBrace;
   private String closedBrace;
   private String referenceOpenBrace;
   private String referenceClosedBrace;
   private String reference;
   private TableObject referenceTable;
   private Vector referenceTableColumnNames;
   private String matchLevel;
   private String onUpdate;
   private String onDelete;
   private String actionOnDelete;
   private String actionOnUpdate;
   private String columnName;
   private UserObjectContext context = null;
   private String tableNameFromCQS;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setConstraintColumnNames(Vector constraintColumnNames) {
      this.constraintColumnNames = constraintColumnNames;
   }

   public void setConstraintName(String constraintName) {
      this.constraintName = constraintName;
   }

   public void setReference(String reference) {
      this.reference = reference;
   }

   public void setTableName(TableObject referenceTable) {
      this.referenceTable = referenceTable;
   }

   public void setReferenceTableColumnNames(Vector referenceTableColumnNames) {
      this.referenceTableColumnNames = referenceTableColumnNames;
   }

   public void setMatch(String matchLevel) {
      this.matchLevel = matchLevel;
   }

   public void setOnUpdate(String onUpdate) {
      this.onUpdate = onUpdate;
   }

   public void setOnDelete(String onDelete) {
      this.onDelete = onDelete;
   }

   public void setActionOnUpdate(String actionOnUpdate) {
      this.actionOnUpdate = actionOnUpdate;
   }

   public void setActionOnDelete(String actionOnDelete) {
      this.actionOnDelete = actionOnDelete;
   }

   public void setOpenBrace(String openBrace) {
      this.openBrace = openBrace;
   }

   public void setClosedBrace(String closedBrace) {
      this.closedBrace = closedBrace;
   }

   public void setReferenceOpenBrace(String referenceOpenBrace) {
      this.referenceOpenBrace = referenceOpenBrace;
   }

   public void setReferenceClosedBrace(String referenceClosedBrace) {
      this.referenceClosedBrace = referenceClosedBrace;
   }

   public void setColumnName(String columnName) {
      this.columnName = columnName;
   }

   public void setTableNameFromCQS(String tableNameFromCQS) {
      this.tableNameFromCQS = tableNameFromCQS;
   }

   public String getColumnName() {
      return this.columnName;
   }

   public Vector getConstraintColumnNames() {
      return this.constraintColumnNames;
   }

   public String getConstraintName() {
      return this.constraintName;
   }

   public String getReference() {
      return this.reference;
   }

   public TableObject getTableName() {
      return this.referenceTable;
   }

   public Vector getReferenceTableColumnNames() {
      return this.referenceTableColumnNames;
   }

   public String getMatch() {
      return this.matchLevel;
   }

   public String getOnUpdate() {
      return this.onUpdate;
   }

   public String getOnDelete() {
      return this.onDelete;
   }

   public String getActionOnUpdate() {
      return this.actionOnUpdate;
   }

   public String getActionOnDelete() {
      return this.actionOnDelete;
   }

   public void toDB2String() throws ConvertException {
      this.setMatch((String)null);
      Vector oracleColumnVector;
      int i;
      String constraintColumn;
      if (this.constraintColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               constraintColumn = (String)this.constraintColumnNames.get(i);
               if ((!constraintColumn.startsWith("[") || !constraintColumn.endsWith("]")) && (!constraintColumn.startsWith("`") || !constraintColumn.endsWith("`"))) {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.add(constraintColumn);
               }
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

      if (this.referenceTable != null) {
         String ownerName = this.referenceTable.getOwner();
         String tableName = this.referenceTable.getTableName();
         constraintColumn = this.referenceTable.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         if (constraintColumn != null && (constraintColumn.startsWith("[") && constraintColumn.endsWith("]") || constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
            constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
            if (constraintColumn.indexOf(32) != -1) {
               constraintColumn = "\"" + constraintColumn + "\"";
            }
         }

         this.referenceTable.setOwner(ownerName);
         this.referenceTable.setTableName(tableName);
         this.referenceTable.setUser(constraintColumn);
         this.referenceTable.toDB2();
      }

      if (this.referenceTableColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.referenceTableColumnNames.size(); ++i) {
            if (!(this.referenceTableColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.referenceTableColumnNames.get(i));
            } else {
               constraintColumn = (String)this.referenceTableColumnNames.get(i);
               if (constraintColumn.startsWith("[") && constraintColumn.endsWith("]") || constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.add(constraintColumn);
               } else {
                  oracleColumnVector.add(this.referenceTableColumnNames.get(i));
               }
            }
         }

         this.setReferenceTableColumnNames(oracleColumnVector);
      }

      if (this.getActionOnUpdate() != null && (this.getActionOnUpdate().equalsIgnoreCase("SET DEFAULT") || this.getActionOnUpdate().equalsIgnoreCase("SET NULL") || this.getActionOnUpdate().equalsIgnoreCase("CASCADE"))) {
         this.setActionOnUpdate((String)null);
         this.setOnUpdate((String)null);
      }

      if (this.getActionOnDelete() != null && this.getActionOnDelete().equalsIgnoreCase("SET DEFAULT")) {
         this.setActionOnDelete("SET NULL");
      }

      if (this.getColumnName() != null && this.getConstraintName() != null) {
         this.setConstraintName((String)null);
         this.setConstraintColumnNames((Vector)null);
      }

   }

   public void toMSSQLServerString() throws ConvertException {
      Vector oracleColumnVector;
      int i;
      String userName;
      if (this.constraintColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (this.constraintColumnNames.elementAt(i) instanceof String) {
               userName = (String)this.constraintColumnNames.get(i);
               if (userName.startsWith("`") && userName.endsWith("`")) {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  oracleColumnVector.add(userName);
               } else {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               }
            } else {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

      if (this.referenceTableColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.referenceTableColumnNames.size(); ++i) {
            if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
               userName = (String)this.referenceTableColumnNames.get(i);
               if (userName.startsWith("`") && userName.endsWith("`")) {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  oracleColumnVector.add(userName);
               } else {
                  oracleColumnVector.add(this.referenceTableColumnNames.get(i));
               }
            } else {
               oracleColumnVector.add(this.referenceTableColumnNames.get(i));
            }
         }

         this.setReferenceTableColumnNames(oracleColumnVector);
      }

      if (this.referenceTable != null) {
         String ownerName = this.referenceTable.getOwner();
         String tableName = this.referenceTable.getTableName();
         userName = this.referenceTable.getUser();
         if (ownerName != null && ownerName.startsWith("`") && ownerName.endsWith("`")) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (tableName != null && tableName.startsWith("`") && tableName.endsWith("`")) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         if (userName != null && userName.startsWith("`") && userName.endsWith("`")) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         this.referenceTable.setOwner(ownerName);
         this.referenceTable.setTableName(tableName);
         this.referenceTable.setUser(userName);
      }

      if (this.getActionOnUpdate() != null) {
         if (!this.getActionOnUpdate().equalsIgnoreCase("SET DEFAULT") && !this.getActionOnUpdate().equalsIgnoreCase("SET NULL")) {
            if (this.getActionOnUpdate().equalsIgnoreCase("RESTRICT")) {
               this.setActionOnUpdate("NO ACTION");
            }
         } else {
            this.setOnUpdate((String)null);
            this.setActionOnUpdate((String)null);
         }
      }

      if (this.getActionOnDelete() != null) {
         if (!SwisSQLOptions.sqlServerTriggers || !this.getActionOnDelete().equalsIgnoreCase("SET DEFAULT") && !this.getActionOnDelete().equalsIgnoreCase("SET NULL")) {
            if (this.getActionOnDelete().equalsIgnoreCase("RESTRICT")) {
               this.setActionOnDelete("NO ACTION");
            }
         } else {
            this.setOnDelete((String)null);
            this.setActionOnDelete((String)null);
         }
      }

   }

   public void toSybaseString() throws ConvertException {
      Vector oracleColumnVector;
      int i;
      String userName;
      if (this.constraintColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (this.constraintColumnNames.elementAt(i) instanceof String) {
               userName = (String)this.constraintColumnNames.get(i);
               if (userName.startsWith("`") && userName.endsWith("`")) {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  oracleColumnVector.add(userName);
               } else {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               }
            } else {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

      if (this.referenceTableColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.referenceTableColumnNames.size(); ++i) {
            if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
               userName = (String)this.referenceTableColumnNames.get(i);
               if (userName.startsWith("`") && userName.endsWith("`")) {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  oracleColumnVector.add(userName);
               } else {
                  oracleColumnVector.add(this.referenceTableColumnNames.get(i));
               }
            } else {
               oracleColumnVector.add(this.referenceTableColumnNames.get(i));
            }
         }

         this.setReferenceTableColumnNames(oracleColumnVector);
      }

      if (this.referenceTable != null) {
         String ownerName = this.referenceTable.getOwner();
         String tableName = this.referenceTable.getTableName();
         userName = this.referenceTable.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         this.referenceTable.setOwner(ownerName);
         this.referenceTable.setTableName(tableName);
         this.referenceTable.setUser(userName);
      }

      if (this.getActionOnUpdate() != null) {
         if (!this.getActionOnUpdate().equalsIgnoreCase("SET DEFAULT") && !this.getActionOnUpdate().equalsIgnoreCase("SET NULL")) {
            if (this.getActionOnUpdate().equalsIgnoreCase("RESTRICT")) {
               this.setActionOnUpdate("NO ACTION");
            }
         } else {
            this.setOnUpdate((String)null);
            this.setActionOnUpdate((String)null);
         }

         this.setOnUpdate((String)null);
         this.setActionOnUpdate((String)null);
      }

      if (this.getActionOnDelete() != null) {
         if (!this.getActionOnDelete().equalsIgnoreCase("SET DEFAULT") && !this.getActionOnDelete().equalsIgnoreCase("SET NULL")) {
            if (this.getActionOnDelete().equalsIgnoreCase("RESTRICT")) {
               this.setActionOnDelete("NO ACTION");
            }
         } else {
            this.setOnDelete((String)null);
            this.setActionOnDelete((String)null);
         }

         this.setOnDelete((String)null);
         this.setActionOnDelete((String)null);
      }

   }

   public void toOracleString() throws ConvertException {
      this.setMatch((String)null);
      if (this.getActionOnUpdate() != null) {
         this.setOnUpdate((String)null);
         this.setActionOnUpdate((String)null);
      }

      if (this.getActionOnDelete() != null) {
         if (this.getActionOnDelete().equalsIgnoreCase("SET DEFAULT")) {
            this.setActionOnDelete("SET NULL");
         } else if (this.getActionOnDelete().equalsIgnoreCase("RESTRICT") || this.getActionOnDelete().equalsIgnoreCase("NO ACTION")) {
            this.setOnDelete((String)null);
            this.setActionOnDelete((String)null);
         }
      }

      if (this.getColumnName() != null && this.getConstraintName() != null) {
         this.setConstraintName((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setOpenBrace((String)null);
         this.setClosedBrace((String)null);
      }

      Vector oracleColumnVector;
      int i;
      String constraintColumn;
      boolean addQuotes;
      if (this.constraintColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               constraintColumn = (String)this.constraintColumnNames.get(i);
               constraintColumn = CustomizeUtil.objectNamesToQuotedIdentifier(constraintColumn, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
               if ((!constraintColumn.startsWith("[") || !constraintColumn.endsWith("]")) && (!constraintColumn.startsWith("`") || !constraintColumn.endsWith("`"))) {
                  oracleColumnVector.add(constraintColumn);
               } else {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (SwisSQLOptions.retainQuotedIdentifierForOracle || constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.add(constraintColumn);
               }

               if (this.tableNameFromCQS == null) {
                  addQuotes = false;
                  if (constraintColumn.startsWith("\"") && constraintColumn.endsWith("\"")) {
                     constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                     addQuotes = true;
                  }

                  if (constraintColumn.length() > 30) {
                     constraintColumn = constraintColumn.substring(0, 30);
                     if (addQuotes) {
                        constraintColumn = "\"" + constraintColumn + "\"";
                     }

                     oracleColumnVector.setElementAt(constraintColumn, oracleColumnVector.size() - 1);
                  }
               }
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

      if (this.referenceTableColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.referenceTableColumnNames.size(); ++i) {
            if (!(this.referenceTableColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.referenceTableColumnNames.get(i));
            } else {
               constraintColumn = (String)this.referenceTableColumnNames.get(i);
               constraintColumn = CustomizeUtil.objectNamesToQuotedIdentifier(constraintColumn, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
               if (constraintColumn.startsWith("[") && constraintColumn.endsWith("]") || constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (SwisSQLOptions.retainQuotedIdentifierForOracle || constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.add(constraintColumn);
               } else {
                  oracleColumnVector.add(constraintColumn);
               }

               addQuotes = false;
               if (constraintColumn.startsWith("\"") && constraintColumn.endsWith("\"")) {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  addQuotes = true;
               }

               if (constraintColumn.length() > 30) {
                  constraintColumn = constraintColumn.substring(0, 30);
                  if (addQuotes) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.setElementAt(constraintColumn, oracleColumnVector.size() - 1);
               }
            }
         }

         this.setReferenceTableColumnNames(oracleColumnVector);
      }

      if (this.referenceTable != null) {
         String ownerName = this.referenceTable.getOwner();
         String tableName = this.referenceTable.getTableName();
         constraintColumn = this.referenceTable.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
         if (constraintColumn != null && (constraintColumn.startsWith("[") && constraintColumn.endsWith("]") || constraintColumn.startsWith("`") && constraintColumn.endsWith("`"))) {
            constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || constraintColumn.indexOf(32) != -1) {
               constraintColumn = "\"" + constraintColumn + "\"";
            }
         }

         this.referenceTable.setOwner(ownerName);
         this.referenceTable.setTableName(tableName);
         this.referenceTable.setUser(constraintColumn);
      }

   }

   public void toBigQueryString() throws ConvertException {
      this.setMatch((String)null);
      Vector bigqueryColumnVector;
      int i;
      String userName;
      if (this.constraintColumnNames != null) {
         bigqueryColumnVector = new Vector();

         for(i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               bigqueryColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               userName = (String)this.constraintColumnNames.get(i);
               if ((!userName.startsWith("[") || !userName.endsWith("]")) && (!userName.startsWith("`") || !userName.endsWith("`"))) {
                  bigqueryColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  bigqueryColumnVector.add(userName);
               }
            }
         }

         this.setConstraintColumnNames(bigqueryColumnVector);
      }

      if (this.referenceTableColumnNames != null) {
         bigqueryColumnVector = new Vector();

         for(i = 0; i < this.referenceTableColumnNames.size(); ++i) {
            if (!(this.referenceTableColumnNames.elementAt(i) instanceof String)) {
               bigqueryColumnVector.add(this.referenceTableColumnNames.get(i));
            } else {
               userName = (String)this.referenceTableColumnNames.get(i);
               if ((!userName.startsWith("[") || !userName.endsWith("]")) && (!userName.startsWith("`") || !userName.endsWith("`"))) {
                  bigqueryColumnVector.add(this.referenceTableColumnNames.get(i));
               } else {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  bigqueryColumnVector.add(userName);
               }
            }
         }

         this.setReferenceTableColumnNames(bigqueryColumnVector);
      }

      if (this.referenceTable != null) {
         String ownerName = this.referenceTable.getOwner();
         String tableName = this.referenceTable.getTableName();
         userName = this.referenceTable.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("\"") && ownerName.endsWith("\""))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "`" + ownerName + "`";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("\"") && tableName.endsWith("\""))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "`" + tableName + "`";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("\"") && userName.endsWith("\""))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "`" + userName + "`";
            }
         }

         this.referenceTable.setOwner(ownerName);
         this.referenceTable.setTableName(tableName);
         this.referenceTable.setUser(userName);
      }

      if (this.getColumnName() != null && this.getConstraintName() != null) {
         this.setConstraintName((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setOpenBrace((String)null);
         this.setClosedBrace((String)null);
      }

   }

   public void toPostgreSQLString() throws ConvertException {
      this.setMatch((String)null);
      Vector oracleColumnVector;
      int i;
      String userName;
      if (this.constraintColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               userName = (String)this.constraintColumnNames.get(i);
               if ((!userName.startsWith("[") || !userName.endsWith("]")) && (!userName.startsWith("`") || !userName.endsWith("`"))) {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  oracleColumnVector.add(userName);
               }
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

      if (this.referenceTableColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.referenceTableColumnNames.size(); ++i) {
            if (!(this.referenceTableColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.referenceTableColumnNames.get(i));
            } else {
               userName = (String)this.referenceTableColumnNames.get(i);
               if ((!userName.startsWith("[") || !userName.endsWith("]")) && (!userName.startsWith("`") || !userName.endsWith("`"))) {
                  oracleColumnVector.add(this.referenceTableColumnNames.get(i));
               } else {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  oracleColumnVector.add(userName);
               }
            }
         }

         this.setReferenceTableColumnNames(oracleColumnVector);
      }

      if (this.referenceTable != null) {
         String ownerName = this.referenceTable.getOwner();
         String tableName = this.referenceTable.getTableName();
         userName = this.referenceTable.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         this.referenceTable.setOwner(ownerName);
         this.referenceTable.setTableName(tableName);
         this.referenceTable.setUser(userName);
      }

      if (this.getColumnName() != null && this.getConstraintName() != null) {
         this.setConstraintName((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setOpenBrace((String)null);
         this.setClosedBrace((String)null);
      }

   }

   public void toANSIString() throws ConvertException {
      this.setMatch((String)null);
      Vector oracleColumnVector;
      int i;
      String userName;
      if (this.constraintColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               userName = (String)this.constraintColumnNames.get(i);
               if ((!userName.startsWith("[") || !userName.endsWith("]")) && (!userName.startsWith("`") || !userName.endsWith("`"))) {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  oracleColumnVector.add(userName);
               }
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

      if (this.referenceTableColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.referenceTableColumnNames.size(); ++i) {
            if (!(this.referenceTableColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.referenceTableColumnNames.get(i));
            } else {
               userName = (String)this.referenceTableColumnNames.get(i);
               if ((!userName.startsWith("[") || !userName.endsWith("]")) && (!userName.startsWith("`") || !userName.endsWith("`"))) {
                  oracleColumnVector.add(this.referenceTableColumnNames.get(i));
               } else {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  oracleColumnVector.add(userName);
               }
            }
         }

         this.setReferenceTableColumnNames(oracleColumnVector);
      }

      if (this.referenceTable != null) {
         String ownerName = this.referenceTable.getOwner();
         String tableName = this.referenceTable.getTableName();
         userName = this.referenceTable.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         this.referenceTable.setOwner(ownerName);
         this.referenceTable.setTableName(tableName);
         this.referenceTable.setUser(userName);
      }

      if (this.getColumnName() != null && this.getConstraintName() != null) {
         this.setConstraintName((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setOpenBrace((String)null);
         this.setClosedBrace((String)null);
      }

   }

   public void toMySQLString() throws ConvertException {
      Vector oracleColumnVector;
      int i;
      String userName;
      if (this.constraintColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (this.constraintColumnNames.elementAt(i) instanceof String) {
               userName = (String)this.constraintColumnNames.get(i);
               if (userName.startsWith("[") && userName.endsWith("]")) {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "`" + userName + "`";
                  }

                  oracleColumnVector.add(userName);
               } else {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               }
            } else {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

      if (this.referenceTableColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.referenceTableColumnNames.size(); ++i) {
            if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
               userName = (String)this.referenceTableColumnNames.get(i);
               if (userName.startsWith("[") && userName.endsWith("]")) {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "`" + userName + "`";
                  }

                  oracleColumnVector.add(userName);
               } else {
                  oracleColumnVector.add(this.referenceTableColumnNames.get(i));
               }
            } else {
               oracleColumnVector.add(this.referenceTableColumnNames.get(i));
            }
         }

         this.setReferenceTableColumnNames(oracleColumnVector);
      }

      if (this.referenceTable != null) {
         String ownerName = this.referenceTable.getOwner();
         String tableName = this.referenceTable.getTableName();
         userName = this.referenceTable.getUser();
         if (ownerName != null && ownerName.startsWith("[") && ownerName.endsWith("]")) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "`" + ownerName + "`";
            }
         }

         if (tableName != null && tableName.startsWith("[") && tableName.endsWith("]")) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "`" + tableName + "`";
            }
         }

         if (userName != null && userName.startsWith("[") && userName.endsWith("]")) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "`" + userName + "`";
            }
         }

         this.referenceTable.setOwner(ownerName);
         this.referenceTable.setTableName(tableName);
         this.referenceTable.setUser(userName);
      }

      this.setMatch((String)null);
      if (this.getColumnName() != null && this.getConstraintName() != null) {
         this.setConstraintName((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setOpenBrace((String)null);
         this.setClosedBrace((String)null);
      }

   }

   public void toSnowflakeString() throws ConvertException {
      this.setMatch((String)null);
      Vector snowflakeColumnVector;
      int i;
      String userName;
      if (this.constraintColumnNames != null) {
         snowflakeColumnVector = new Vector();

         for(i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               snowflakeColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               userName = (String)this.constraintColumnNames.get(i);
               if ((!userName.startsWith("[") || !userName.endsWith("]")) && (!userName.startsWith("`") || !userName.endsWith("`"))) {
                  snowflakeColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  snowflakeColumnVector.add(userName);
               }
            }
         }

         this.setConstraintColumnNames(snowflakeColumnVector);
      }

      if (this.referenceTableColumnNames != null) {
         snowflakeColumnVector = new Vector();

         for(i = 0; i < this.referenceTableColumnNames.size(); ++i) {
            if (!(this.referenceTableColumnNames.elementAt(i) instanceof String)) {
               snowflakeColumnVector.add(this.referenceTableColumnNames.get(i));
            } else {
               userName = (String)this.referenceTableColumnNames.get(i);
               if ((!userName.startsWith("[") || !userName.endsWith("]")) && (!userName.startsWith("`") || !userName.endsWith("`"))) {
                  snowflakeColumnVector.add(this.referenceTableColumnNames.get(i));
               } else {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  snowflakeColumnVector.add(userName);
               }
            }
         }

         this.setReferenceTableColumnNames(snowflakeColumnVector);
      }

      if (this.referenceTable != null) {
         String ownerName = this.referenceTable.getOwner();
         String tableName = this.referenceTable.getTableName();
         userName = this.referenceTable.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         this.referenceTable.setOwner(ownerName);
         this.referenceTable.setTableName(tableName);
         this.referenceTable.setUser(userName);
      }

      if (this.getColumnName() != null && this.getConstraintName() != null) {
         this.setConstraintName((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setOpenBrace((String)null);
         this.setClosedBrace((String)null);
      }

   }

   public void toInformixString() throws ConvertException {
      Vector oracleColumnVector;
      int i;
      String userName;
      if (this.constraintColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               userName = (String)this.constraintColumnNames.get(i);
               if ((!userName.startsWith("[") || !userName.endsWith("]")) && (!userName.startsWith("`") || !userName.endsWith("`"))) {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  oracleColumnVector.add(userName);
               }
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

      if (this.referenceTableColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.referenceTableColumnNames.size(); ++i) {
            if (!(this.referenceTableColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.referenceTableColumnNames.get(i));
            } else {
               userName = (String)this.referenceTableColumnNames.get(i);
               if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  oracleColumnVector.add(userName);
               } else {
                  oracleColumnVector.add(this.referenceTableColumnNames.get(i));
               }
            }
         }

         this.setReferenceTableColumnNames(oracleColumnVector);
      }

      if (this.referenceTable != null) {
         String ownerName = this.referenceTable.getOwner();
         String tableName = this.referenceTable.getTableName();
         userName = this.referenceTable.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         this.referenceTable.setOwner(ownerName);
         this.referenceTable.setTableName(tableName);
         this.referenceTable.setUser(userName);
      }

      this.setMatch((String)null);
      if (this.getActionOnUpdate() != null) {
         this.setOnUpdate((String)null);
         this.setActionOnUpdate((String)null);
      }

      if (this.getActionOnDelete() != null && (this.getActionOnDelete().equalsIgnoreCase("SET DEFAULT") || this.getActionOnDelete().equalsIgnoreCase("RESTRICT") || this.getActionOnDelete().equalsIgnoreCase("NO ACTION") || this.getActionOnDelete().equalsIgnoreCase("SET NULL"))) {
         this.setOnDelete((String)null);
         this.setActionOnDelete((String)null);
      }

      if (this.getColumnName() != null && this.getConstraintName() != null) {
         this.setConstraintName((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setOpenBrace((String)null);
         this.setClosedBrace((String)null);
      }

   }

   public void toTimesTenString() throws ConvertException {
      Vector columnVector;
      int i;
      String constraintColumn;
      if (this.constraintColumnNames != null) {
         columnVector = new Vector();

         for(i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (this.constraintColumnNames.elementAt(i) instanceof String) {
               constraintColumn = (String)this.constraintColumnNames.get(i);
               constraintColumn = CustomizeUtil.objectNamesToQuotedIdentifier(constraintColumn, SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10);
               if (constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  columnVector.add(constraintColumn);
               } else {
                  columnVector.add(constraintColumn);
               }
            } else {
               columnVector.add(this.constraintColumnNames.get(i));
            }
         }

         this.setConstraintColumnNames(columnVector);
      }

      if (this.referenceTableColumnNames != null) {
         columnVector = new Vector();

         for(i = 0; i < this.referenceTableColumnNames.size(); ++i) {
            if (this.referenceTableColumnNames.elementAt(i) instanceof String) {
               constraintColumn = (String)this.referenceTableColumnNames.get(i);
               constraintColumn = CustomizeUtil.objectNamesToQuotedIdentifier(constraintColumn, SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10);
               if (constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  columnVector.add(constraintColumn);
               } else {
                  columnVector.add(constraintColumn);
               }
            } else {
               columnVector.add(this.referenceTableColumnNames.get(i));
            }
         }

         this.setReferenceTableColumnNames(columnVector);
      }

      if (this.referenceTable != null) {
         String ownerName = this.referenceTable.getOwner();
         String tableName = this.referenceTable.getTableName();
         constraintColumn = this.referenceTable.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10);
         if (constraintColumn != null) {
            constraintColumn = null;
         }

         this.referenceTable.setOwner(ownerName);
         this.referenceTable.setTableName(tableName);
         this.referenceTable.setUser(constraintColumn);
      }

      if (this.getActionOnUpdate() != null) {
         this.setOnUpdate((String)null);
         this.setActionOnUpdate((String)null);
      }

      if (this.getActionOnDelete() != null) {
         this.setOnDelete((String)null);
         this.setActionOnDelete((String)null);
      }

      this.setMatch((String)null);
   }

   public void toNetezzaString() throws ConvertException {
      this.setMatch((String)null);
      Vector oracleColumnVector;
      int i;
      String userName;
      if (this.constraintColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               userName = (String)this.constraintColumnNames.get(i);
               if ((!userName.startsWith("[") || !userName.endsWith("]")) && (!userName.startsWith("`") || !userName.endsWith("`"))) {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  oracleColumnVector.add(userName);
               }
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

      if (this.referenceTableColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.referenceTableColumnNames.size(); ++i) {
            if (!(this.referenceTableColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.referenceTableColumnNames.get(i));
            } else {
               userName = (String)this.referenceTableColumnNames.get(i);
               if ((!userName.startsWith("[") || !userName.endsWith("]")) && (!userName.startsWith("`") || !userName.endsWith("`"))) {
                  oracleColumnVector.add(this.referenceTableColumnNames.get(i));
               } else {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  oracleColumnVector.add(userName);
               }
            }
         }

         this.setReferenceTableColumnNames(oracleColumnVector);
      }

      if (this.referenceTable != null) {
         String ownerName = this.referenceTable.getOwner();
         String tableName = this.referenceTable.getTableName();
         userName = this.referenceTable.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         this.referenceTable.setOwner(ownerName);
         this.referenceTable.setTableName(tableName);
         this.referenceTable.setUser(userName);
      }

      if (this.getColumnName() != null && this.getConstraintName() != null) {
         this.setConstraintName((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setOpenBrace((String)null);
         this.setClosedBrace((String)null);
      }

   }

   public void toTeradataString() throws ConvertException {
      this.setMatch((String)null);
      Vector oracleColumnVector;
      int i;
      String userName;
      if (this.constraintColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               userName = (String)this.constraintColumnNames.get(i);
               if ((!userName.startsWith("[") || !userName.endsWith("]")) && (!userName.startsWith("`") || !userName.endsWith("`"))) {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  oracleColumnVector.add(userName);
               }
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

      if (this.referenceTableColumnNames != null) {
         oracleColumnVector = new Vector();

         for(i = 0; i < this.referenceTableColumnNames.size(); ++i) {
            if (!(this.referenceTableColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.referenceTableColumnNames.get(i));
            } else {
               userName = (String)this.referenceTableColumnNames.get(i);
               if ((!userName.startsWith("[") || !userName.endsWith("]")) && (!userName.startsWith("`") || !userName.endsWith("`"))) {
                  oracleColumnVector.add(this.referenceTableColumnNames.get(i));
               } else {
                  userName = userName.substring(1, userName.length() - 1);
                  if (userName.indexOf(32) != -1) {
                     userName = "\"" + userName + "\"";
                  }

                  oracleColumnVector.add(userName);
               }
            }
         }

         this.setReferenceTableColumnNames(oracleColumnVector);
      }

      if (this.referenceTable != null) {
         String ownerName = this.referenceTable.getOwner();
         String tableName = this.referenceTable.getTableName();
         userName = this.referenceTable.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         this.referenceTable.setOwner(ownerName);
         this.referenceTable.setTableName(tableName);
         this.referenceTable.setUser(userName);
      }

      if (this.getColumnName() != null && this.getConstraintName() != null) {
         this.setConstraintName((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setOpenBrace((String)null);
         this.setClosedBrace((String)null);
      }

   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.constraintName != null) {
         sb.append(this.constraintName.toUpperCase() + " ");
      }

      if (this.openBrace != null) {
         sb.append(this.openBrace);
      }

      int i;
      String col;
      String temp;
      if (this.constraintColumnNames != null) {
         for(i = 0; i < this.constraintColumnNames.size(); ++i) {
            col = this.constraintColumnNames.get(i).toString();
            if (i == 0) {
               if (this.context != null) {
                  temp = this.context.getEquivalent(col).toString();
                  sb.append(temp);
               } else {
                  sb.append(col);
               }
            } else if (this.context != null) {
               temp = this.context.getEquivalent(col).toString();
               sb.append(", " + temp);
            } else {
               sb.append(", " + col);
            }
         }
      }

      if (this.closedBrace != null) {
         sb.append(this.closedBrace + " ");
      }

      if (this.reference != null) {
         sb.append(this.reference.toUpperCase() + " ");
      }

      if (this.referenceTable != null) {
         if (this.context != null) {
            this.referenceTable.setObjectContext(this.context);
         }

         sb.append(this.referenceTable.toString() + " ");
      }

      if (this.referenceOpenBrace != null) {
         sb.append(this.referenceOpenBrace);
      }

      if (this.referenceTableColumnNames != null) {
         for(i = 0; i < this.referenceTableColumnNames.size(); ++i) {
            if (this.referenceTableColumnNames.get(i) instanceof String) {
               col = this.referenceTableColumnNames.get(i).toString();
               if (this.referenceTable != null && this.context != null) {
                  temp = this.referenceTable.getTableName() + "." + col;
                  String sss = this.context.getEquivalent(temp).toString();
                  if (!temp.equals(sss)) {
                     col = sss;
                  }
               }

               if (i == 0) {
                  if (this.context != null) {
                     temp = this.context.getEquivalent(col).toString();
                     sb.append(temp);
                  } else {
                     sb.append(col);
                  }
               } else if (this.context != null) {
                  temp = this.context.getEquivalent(col).toString();
                  sb.append(", " + temp);
               } else {
                  sb.append(", " + col);
               }
            }
         }
      }

      if (this.referenceClosedBrace != null) {
         sb.append(this.referenceClosedBrace + " ");
      }

      if (this.matchLevel != null) {
         sb.append(this.matchLevel + " ");
      }

      if (this.onUpdate != null) {
         sb.append(this.onUpdate + " ");
      }

      if (this.actionOnUpdate != null) {
         sb.append(this.actionOnUpdate + " ");
      }

      if (this.onDelete != null) {
         sb.append(this.onDelete + " ");
      }

      if (this.actionOnDelete != null) {
         sb.append(this.actionOnDelete + " ");
      }

      return sb.toString();
   }

   public ConstraintType copyObjectValues() {
      ForeignConstraintClause dupForeignConstraintClause = new ForeignConstraintClause();
      dupForeignConstraintClause.setClosedBrace(this.closedBrace);
      dupForeignConstraintClause.setConstraintColumnNames(this.getConstraintColumnNames());
      dupForeignConstraintClause.setConstraintName(this.getConstraintName());
      dupForeignConstraintClause.setOpenBrace(this.openBrace);
      dupForeignConstraintClause.setActionOnUpdate(this.getActionOnUpdate());
      dupForeignConstraintClause.setActionOnDelete(this.getActionOnDelete());
      dupForeignConstraintClause.setMatch(this.getMatch());
      dupForeignConstraintClause.setOnUpdate(this.getOnUpdate());
      dupForeignConstraintClause.setOnDelete(this.getOnDelete());
      dupForeignConstraintClause.setReference(this.getReference());
      dupForeignConstraintClause.setReferenceClosedBrace(this.referenceClosedBrace);
      dupForeignConstraintClause.setReferenceOpenBrace(this.referenceOpenBrace);
      dupForeignConstraintClause.setReferenceTableColumnNames(this.getReferenceTableColumnNames());
      dupForeignConstraintClause.setTableName(this.getTableName());
      dupForeignConstraintClause.setObjectContext(this.context);
      return dupForeignConstraintClause;
   }
}
