package com.adventnet.swissqlapi.sql.statement.alter;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.CreateIndexClause;
import com.adventnet.swissqlapi.sql.statement.create.CreateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.create.ForeignConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.IndexColumn;
import com.adventnet.swissqlapi.sql.statement.drop.DropStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

public class AlterStatement implements SwisSQLStatement {
   private UserObjectContext objectContext = null;
   private String alter;
   private String ignore;
   private String tableOrView;
   private TableObject tableName;
   private AlterTable alterTable;
   private Vector alterStatementVector;
   private boolean commaIsSet;
   private CommentClass commentObject;
   private String onString;
   private String quotedIdentifierString;
   private String multiAlterStatement;
   private String alterSession;
   private String setString;
   private String parameter;
   private String parameterValue;
   public static String generalComment = "";
   private DatatypeMapping datatypeMapping;
   private CreateQueryStatement indexStatement = null;
   private DropStatement dropIndexStatement = null;
   private String sequence;
   private String deltek_triggers;

   public void setAlter(String alter) {
      this.alter = alter;
   }

   public void setIgnore(String ignore) {
      this.ignore = ignore;
   }

   public void setTableOrView(String tableOrView) {
      this.tableOrView = tableOrView;
   }

   public void setSession(String alterSession) {
      this.alterSession = alterSession;
   }

   public void setSetString(String setString) {
      this.setString = setString;
   }

   public void setTableName(TableObject tableName) {
      this.tableName = tableName;
   }

   public void setAlterTable(AlterTable alterTable) {
      this.alterTable = alterTable;
   }

   public void setAlterStatementVector(Vector alterStatementVector) {
      this.alterStatementVector = alterStatementVector;
   }

   public void setCommaBooleanValue(boolean commaIsSet) {
      this.commaIsSet = commaIsSet;
   }

   public void setDropClause() {
   }

   public void setCommentClass(CommentClass commentObject) {
      this.commentObject = commentObject;
   }

   public void setOnCondition(String onString) {
      this.onString = onString;
   }

   public void setQuotedIdentifier(String quotedIdentifierString) {
      this.quotedIdentifierString = quotedIdentifierString;
   }

   public void setMultiAlterStatement(String multiAlterStatement) {
      this.multiAlterStatement = multiAlterStatement;
   }

   public void setParameter(String parameter) {
      this.parameter = parameter;
   }

   public void setParameterValue(String parameterValue) {
      this.parameterValue = parameterValue;
   }

   public void setDatatypeMapping(DatatypeMapping mapping) {
      this.datatypeMapping = mapping;
   }

   public void setIndexStatement(CreateQueryStatement cqs) {
      this.indexStatement = cqs;
   }

   public void setDropIndexStatement(DropStatement dropStmt) {
      this.dropIndexStatement = dropStmt;
   }

   public void setSequence(String seq) {
      this.sequence = seq;
   }

   public void setTriggers(String s) {
      this.deltek_triggers = s;
   }

   public String getTriggers() {
      return this.deltek_triggers;
   }

   public String getAlter() {
      return this.alter;
   }

   public String getIgnore() {
      return this.ignore;
   }

   public String getTableOrView() {
      return this.tableOrView;
   }

   public TableObject getTableName() {
      return this.tableName;
   }

   public AlterTable getAlterTable() {
      return this.alterTable;
   }

   public Vector getAlterStatementVector() {
      return this.alterStatementVector;
   }

   public boolean getCommaBooleanValue() {
      return this.commaIsSet;
   }

   public CommentClass getCommentClass() {
      return this.commentObject;
   }

   private String getMultiAlterStatement() {
      return this.multiAlterStatement;
   }

   public String getSession() {
      return this.alterSession;
   }

   public String getSetString() {
      return this.setString;
   }

   public String getParameter() {
      return this.parameter;
   }

   public String getParameterValue() {
      return this.parameterValue;
   }

   public String getSequence() {
      return this.sequence;
   }

   public String toOracleString() throws ConvertException {
      return this.toOracleAlter().toString();
   }

   public String toMSSQLServerString() throws ConvertException {
      return this.toMSSQLServerAlter().toString();
   }

   public String toSybaseString() throws ConvertException {
      return this.toSybaseAlter().toString();
   }

   public String toDB2String() throws ConvertException {
      return this.toDB2Alter().toString();
   }

   public String toANSIString() throws ConvertException {
      return this.toANSIAlter().toString();
   }

   public String toPostgreSQLString() throws ConvertException {
      return this.toPostgreSQLAlter().toString();
   }

   public String toBigQueryString() throws ConvertException {
      return this.toBigQueryAlter().toString();
   }

   public String toInformixString() throws ConvertException {
      return this.toInformixAlter().toString();
   }

   public String toMySQLString() throws ConvertException {
      return this.toMySQLAlter().toString();
   }

   public String toTimesTenString() throws ConvertException {
      return this.toTimesTenAlter().toString();
   }

   public String toNetezzaString() throws ConvertException {
      return this.toNetezzaAlter().toString();
   }

   public String toTeradataString() throws ConvertException {
      return this.toTeradataAlter().toString();
   }

   public String toSnowflakeString() throws ConvertException {
      return this.toSnowflakeAlter().toString();
   }

   public String toAthenaString() throws ConvertException {
      return null;
   }

   public String toSapHanaString() throws ConvertException {
      return null;
   }

   public String toSqliteString() throws ConvertException {
      return null;
   }

   public String toExcelString() throws ConvertException {
      return null;
   }

   public String toMsAccessJdbcString() throws ConvertException {
      return null;
   }

   public AlterStatement toOracleAlter() throws ConvertException {
      AlterStatement dupAlterStatement = this.copyObjectValues();
      dupAlterStatement.setOnCondition((String)null);
      dupAlterStatement.setQuotedIdentifier((String)null);
      dupAlterStatement.setMultiAlterStatement((String)null);
      String var2;
      if (dupAlterStatement.getAlter() != null) {
         var2 = dupAlterStatement.getAlter();
      }

      dupAlterStatement.setIgnore((String)null);
      if (dupAlterStatement.getTableOrView() != null) {
         var2 = dupAlterStatement.getTableOrView();
      }

      if (dupAlterStatement.getTableName() != null) {
         StringBuffer tableStringBuffer = new StringBuffer();
         TableObject orgTableObject = dupAlterStatement.getTableName();
         String table_name = orgTableObject.getTableName();
         String ownerName = orgTableObject.getOwner();
         String userName = orgTableObject.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         orgTableObject.setOwner(ownerName);
         orgTableObject.setUser(userName);
         StringTokenizer st = new StringTokenizer(table_name, ".");
         int count = 0;

         Vector tokenVector;
         for(tokenVector = new Vector(); st.hasMoreTokens(); ++count) {
            tokenVector.add(st.nextToken());
         }

         for(int i_count = 0; i_count < tokenVector.size(); ++i_count) {
            String oracleTableName = (String)tokenVector.get(i_count);
            if (oracleTableName != null && (oracleTableName.startsWith("[") && oracleTableName.endsWith("]") || oracleTableName.startsWith("`") && oracleTableName.endsWith("`"))) {
               oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
               if (SwisSQLOptions.retainQuotedIdentifierForOracle || oracleTableName.indexOf(32) != -1) {
                  oracleTableName = "\"" + oracleTableName + "\"";
               }
            }

            if (i_count > 0) {
               tableStringBuffer.append(".");
            }

            tableStringBuffer.append(oracleTableName);
         }

         orgTableObject.setTableName(tableStringBuffer.toString());
         orgTableObject.toOracle();
         dupAlterStatement.setTableName(orgTableObject);
      }

      if (dupAlterStatement.getAlterStatementVector() != null) {
         Vector oracleAlterStatementVector = new Vector();
         Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();

         for(int i = 0; i < tempAlterStatementVector.size(); ++i) {
            AlterTable tempAlterTable = (AlterTable)tempAlterStatementVector.get(i);
            AlterTable oracleAlterTable = tempAlterTable.toOracle();
            if (tempAlterTable.getEnableOrDisable() != null) {
               dupAlterStatement.setTableOrView("TRIGGER");
               dupAlterStatement.setTableName((TableObject)null);
            }

            oracleAlterStatementVector.add(oracleAlterTable);
         }

         dupAlterStatement.setAlterStatementVector(oracleAlterStatementVector);
      }

      dupAlterStatement.setCommaBooleanValue(false);
      return dupAlterStatement;
   }

   public String convertOnDeleteSetNullToTriggers() {
      ForeignConstraintClause foreigncontraint = null;
      String onDelete = null;
      String setNull = null;
      String referencingTable = null;
      String origTableName = this.getTableName().getTableName();
      String constraintName = null;
      Vector constraintColumnNames = new Vector();
      String insertTrigger = null;
      String deleteTrigger = null;
      String updateTrigger1 = null;
      String updateTrigger2 = null;
      if (this.alterStatementVector != null && this.alterStatementVector.size() == 1) {
         Object obj = this.alterStatementVector.get(0);
         if (obj != null && obj instanceof AlterTable) {
            AlterTable altertable = (AlterTable)this.alterStatementVector.get(0);
            if (altertable != null && altertable.getAddClause() != null) {
               AddClause ac = altertable.getAddClause();
               if (ac != null && ac.getCreateColumnVector() != null && ac.getCreateColumnVector().size() == 1) {
                  Vector constraintBody = ac.getCreateColumnVector();
                  if (constraintBody != null && constraintBody.size() == 1) {
                     CreateColumn cc = (CreateColumn)constraintBody.get(0);
                     Vector constraintsVector = cc.getConstraintClause();
                     if (constraintsVector != null && constraintsVector.size() == 1) {
                        Object obj1 = constraintsVector.get(0);
                        if (obj1 instanceof ConstraintClause) {
                           ConstraintClause constraintClause = (ConstraintClause)obj1;
                           constraintName = constraintClause.getConstraintName();
                           if (constraintClause.getConstraintType() != null && constraintClause.getConstraintType() instanceof ForeignConstraintClause) {
                              foreigncontraint = (ForeignConstraintClause)constraintClause.getConstraintType();
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      if (foreigncontraint != null) {
         if (foreigncontraint.getOnDelete() != null && foreigncontraint.getActionOnDelete() != null) {
            onDelete = foreigncontraint.getOnDelete();
            if (foreigncontraint.getActionOnDelete().trim().equalsIgnoreCase("SET NULL")) {
               setNull = foreigncontraint.getActionOnDelete().trim();
            }
         }

         if (foreigncontraint.getReference() != null) {
            referencingTable = foreigncontraint.getTableName().toString();
         }

         if (foreigncontraint.getConstraintColumnNames().size() >= 1) {
            constraintColumnNames = foreigncontraint.getConstraintColumnNames();
         }
      }

      String inserted1 = "";
      String inserted2 = "";
      String delete1 = "";
      String delete2 = "";
      String update1 = "";
      String update2 = "";
      String update3 = "";
      String equals = " = ";
      String isnull = " is null ";
      String t;
      String t1;
      String del1;
      if (constraintColumnNames.size() >= 1) {
         for(int i = 0; i < constraintColumnNames.size(); ++i) {
            String temp = referencingTable + "." + constraintColumnNames.get(i);
            String temp1 = "INSERTED." + constraintColumnNames.get(i);
            String temp_del = origTableName + "." + constraintColumnNames.get(i);
            String temp_del1 = "DELETED." + constraintColumnNames.get(i);
            String temp_update1 = "update(" + constraintColumnNames.get(i) + ")";
            String temp_update3 = temp_del + equals + temp_del1 + "\n AND " + temp_del1 + " != " + temp1;
            String del2;
            String upd1;
            String upd3;
            if (i != constraintColumnNames.size() - 1) {
               t = temp + equals + temp1 + " AND \n";
               t1 = temp1 + isnull + " OR \n";
               del1 = temp_del + equals + " null ,\n";
               del2 = temp_del + equals + temp_del1 + " AND \n";
               upd1 = temp_update1 + " OR ";
               upd3 = temp_update3 + " AND \n";
            } else {
               upd3 = temp_update3;
               upd1 = temp_update1;
               del2 = temp_del + equals + temp_del1;
               del1 = temp_del + equals + " null \n";
               t = temp + equals + temp1;
               t1 = temp1 + isnull;
            }

            update3 = update3 + upd3;
            inserted1 = inserted1 + t;
            inserted2 = inserted2 + t1;
            delete1 = delete1 + del1;
            delete2 = delete2 + del2;
            update1 = update1 + upd1;
         }
      }

      if (this.tableOrView.trim().equalsIgnoreCase("TABLE") && foreigncontraint != null && onDelete != null && setNull != null && foreigncontraint.getConstraintName().trim().equalsIgnoreCase("FOREIGN KEY") && onDelete.trim().equalsIgnoreCase("ON DELETE") && setNull.trim().equalsIgnoreCase("SET NULL")) {
         String insertTriggerName = origTableName + "_INSTRIG";
         insertTrigger = "/* INSERT TRIGGER FOR: " + origTableName + "*/ \n \n CREATE TRIGGER " + insertTriggerName + " \n" + "ON " + origTableName + " FOR INSERT \n AS \n declare @num_rows int \n" + "select @num_rows = @@rowcount \n If @num_rows = 0 \n" + "return \n\n" + "/* " + constraintName + " insert restrict */" + "\n IF (SELECT COUNT(*) FROM " + referencingTable + " , inserted \n WHERE \n " + inserted1 + ") !=" + " @num_rows \n BEGIN \n " + "RAISERROR 40002 'NOT IN " + referencingTable + " for child " + origTableName + "' \n" + "ROLLBACK TRAN RETURN \n \n END \n go";
         t = referencingTable + "_DELTRIG";
         deleteTrigger = "/* DELETE TRIGGER FOR: " + referencingTable + " */ \n\n CREATE TRIGGER " + t + " \n" + "ON " + referencingTable + " FOR DELETE \n AS \n declare @num_rows int \n" + "select @num_rows = @@rowcount \n \n If @num_rows = 0  \n return \n\n /* " + constraintName + " Delete Set Null */ \n" + "UPDATE " + origTableName + " SET \n" + delete1 + "\n" + "from " + origTableName + " , deleted where " + delete2 + "\n" + "\n go";
         t1 = origTableName + "_UPDTRIG";
         updateTrigger1 = " /* UPDATE TRIGGER for: " + origTableName + " */ \n\n CREATE TRIGGER " + t1 + " \n ON " + origTableName + " FOR UPDATE \n\n AS \n" + "declare @num_rows int \n select @num_rows = @@rowcount \n" + "If @num_rows = 0 \n \n  return \n if " + update1 + " \n\n" + "BEGIN \n\n" + "/* " + constraintName + " update change restrict */\n\n" + "IF (SELECT COUNT(*) FROM " + referencingTable + ", inserted where\n" + inserted1 + ") != @num_rows \n\n BEGIN \n RAISERROR 40005 'not in " + referencingTable + " when changing child " + origTableName + "' \n ROLLBACK TRAN RETURN" + "\n \n  END \n END \ngo";
         del1 = referencingTable + "_UPDTRIG";
         updateTrigger2 = "/* UPDATE TRIGGER for: " + referencingTable + " */ \n\n CREATE TRIGGER " + del1 + "\n ON " + referencingTable + " FOR UPDATE \n\n AS \n" + "declare @num_rows int \n select @num_rows = @@rowcount \n" + "If @num_rows = 0 \n \n  return \n if " + update1 + " \n\n" + "BEGIN \n\n" + "/* Update Primary Key restrict if no dependants found for" + referencingTable + " */\n\n" + "IF (SELECT COUNT(*) FROM " + origTableName + ",deleted,inserted where \n" + update3 + ") != 0 \n\n BEGIN \n RAISERROR 40001" + " 'Parent table " + referencingTable + " cannot change Primary Key with dependant rows in " + origTableName + "' \n ROLLBACK TRAN RETURN" + "\n \n  END \n END \ngo";
         String triggers = insertTrigger + "\n\n" + deleteTrigger + "\n\n" + updateTrigger2 + "\n\n" + updateTrigger1;
         return triggers;
      } else {
         return null;
      }
   }

   public AlterStatement toMSSQLServerAlter() throws ConvertException {
      if (SwisSQLOptions.sqlServerTriggers) {
         this.deltek_triggers = this.convertOnDeleteSetNullToTriggers();
      }

      AlterStatement dupAlterStatement = this.copyObjectValues();
      if (this.alterSession != null) {
         throw new ConvertException("Query yet to be supported");
      } else {
         String var2;
         if (dupAlterStatement.getAlter() != null) {
            var2 = dupAlterStatement.getAlter();
         }

         dupAlterStatement.setIgnore((String)null);
         if (dupAlterStatement.getTableOrView() != null) {
            var2 = dupAlterStatement.getTableOrView();
         }

         if (dupAlterStatement.getTableName() != null) {
            TableObject var12 = dupAlterStatement.getTableName();
         }

         String space;
         if (dupAlterStatement.getAlterStatementVector() != null) {
            Vector msSQLServerAlterStatementVector = new Vector();
            Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            AlterTable msSQLServerAlterTable;
            if (tempAlterStatementVector.size() == 1) {
               AlterTable tempAlterTable = (AlterTable)tempAlterStatementVector.get(0);
               if (tempAlterTable.toString().equalsIgnoreCase("comment") || tempAlterTable.toString().equalsIgnoreCase("engine")) {
                  throw new ConvertException(tempAlterTable.toString().toUpperCase() + " table option not supported in SQL Server");
               }

               if (tempAlterTable.getAddClause() != null && tempAlterTable.getAddClause().getUniqueOrPrimaryOrIndexOrFullText() != null && tempAlterTable.getAddClause().getUniqueOrPrimaryOrIndexOrFullText().equalsIgnoreCase("index")) {
                  dupAlterStatement.setIndexStatement((CreateQueryStatement)this.handleAddIndexClause(dupAlterStatement, tempAlterTable));
               } else if (tempAlterTable.getDropClause() != null && tempAlterTable.getDropClause().getIndex() != null) {
                  dupAlterStatement.setDropIndexStatement((DropStatement)this.handleDropIndexClause(dupAlterStatement, tempAlterTable));
               } else {
                  msSQLServerAlterTable = tempAlterTable.toMSSQLServer();
                  if (msSQLServerAlterTable.getChange() != null && !msSQLServerAlterTable.getOrigColumn().trim().equalsIgnoreCase(msSQLServerAlterTable.getCreateColumn().getColumnName().trim())) {
                     space = " ";
                     StringBuffer sp_rename = new StringBuffer();
                     sp_rename.append("exec sp_rename" + space);
                     String tableName = this.convertTableObjectToString(dupAlterStatement.getTableName());
                     sp_rename.append("'" + tableName + "." + msSQLServerAlterTable.getOrigColumn() + "'");
                     sp_rename.append(space + "," + space);
                     sp_rename.append("'" + msSQLServerAlterTable.getCreateColumn().getColumnName() + "'");
                     sp_rename.append(space + "," + space);
                     sp_rename.append("'column'");
                     msSQLServerAlterTable.setsp_renameStmt(sp_rename.toString());
                  }

                  msSQLServerAlterStatementVector.add(msSQLServerAlterTable);
                  dupAlterStatement.setAlterStatementVector(msSQLServerAlterStatementVector);
               }

               dupAlterStatement.setMultiAlterStatement((String)null);
            } else {
               dupAlterStatement.setMultiAlterStatement("ALTER");

               for(int i = 0; i < tempAlterStatementVector.size(); ++i) {
                  msSQLServerAlterTable = (AlterTable)tempAlterStatementVector.get(i);
                  SwisSQLStatement swisStmt;
                  if (msSQLServerAlterTable.getAddClause() != null && msSQLServerAlterTable.getAddClause().getUniqueOrPrimaryOrIndexOrFullText() != null && msSQLServerAlterTable.getAddClause().getUniqueOrPrimaryOrIndexOrFullText().equalsIgnoreCase("index")) {
                     swisStmt = this.handleAddIndexClause(dupAlterStatement, msSQLServerAlterTable);
                     msSQLServerAlterStatementVector.add(swisStmt);
                  } else if (msSQLServerAlterTable.getDropClause() != null && msSQLServerAlterTable.getDropClause().getIndex() != null) {
                     swisStmt = this.handleDropIndexClause(dupAlterStatement, msSQLServerAlterTable);
                     msSQLServerAlterStatementVector.add(swisStmt);
                  } else {
                     AlterTable msSQLServerAlterTable = msSQLServerAlterTable.toMSSQLServer();
                     if (msSQLServerAlterTable.getChange() != null && !msSQLServerAlterTable.getOrigColumn().trim().equalsIgnoreCase(msSQLServerAlterTable.getCreateColumn().getColumnName().trim())) {
                        String space = " ";
                        StringBuffer sp_rename = new StringBuffer();
                        sp_rename.append("exec sp_rename" + space);
                        String tableName = this.convertTableObjectToString(dupAlterStatement.getTableName());
                        sp_rename.append("'" + tableName + "." + msSQLServerAlterTable.getOrigColumn() + "'");
                        sp_rename.append(space + "," + space);
                        sp_rename.append("'" + msSQLServerAlterTable.getCreateColumn().getColumnName() + "'");
                        sp_rename.append(space + "," + space);
                        sp_rename.append("'column'");
                        msSQLServerAlterTable.setsp_renameStmt(sp_rename.toString());
                     }

                     msSQLServerAlterStatementVector.add(msSQLServerAlterTable);
                  }

                  dupAlterStatement.setAlterStatementVector(msSQLServerAlterStatementVector);
               }
            }
         }

         if (dupAlterStatement.getTableName() != null) {
            StringBuffer tableStringBuffer = new StringBuffer();
            TableObject orgTableObject = dupAlterStatement.getTableName();
            String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            space = orgTableObject.getUser();
            if (ownerName != null && ownerName.startsWith("`") && ownerName.endsWith("`")) {
               ownerName = ownerName.substring(1, ownerName.length() - 1);
               if (ownerName.indexOf(32) != -1) {
                  ownerName = "\"" + ownerName + "\"";
               }
            }

            if (space != null && space.startsWith("`") && space.endsWith("`")) {
               space = space.substring(1, space.length() - 1);
               if (space.indexOf(32) != -1) {
                  space = "\"" + space + "\"";
               }
            }

            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(space);
            StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;

            Vector tokenVector;
            for(tokenVector = new Vector(); st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            for(int i_count = 0; i_count < tokenVector.size(); ++i_count) {
               String oracleTableName = (String)tokenVector.get(i_count);
               if (oracleTableName != null && oracleTableName.startsWith("`") && oracleTableName.endsWith("`")) {
                  oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                  if (oracleTableName.indexOf(32) != -1) {
                     oracleTableName = "\"" + oracleTableName + "\"";
                  }
               }

               if (i_count > 0) {
                  tableStringBuffer.append(".");
               }

               tableStringBuffer.append(oracleTableName);
            }

            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toMSSQLServer();
            dupAlterStatement.setTableName(orgTableObject);
         }

         return dupAlterStatement;
      }
   }

   public AlterStatement toSybaseAlter() throws ConvertException {
      AlterStatement dupAlterStatement = this.copyObjectValues();
      if (this.alterSession != null) {
         throw new ConvertException("Query yet to be supported");
      } else {
         dupAlterStatement.setOnCondition((String)null);
         dupAlterStatement.setQuotedIdentifier((String)null);
         String var2;
         if (dupAlterStatement.getAlter() != null) {
            var2 = dupAlterStatement.getAlter();
         }

         dupAlterStatement.setIgnore((String)null);
         if (dupAlterStatement.getTableOrView() != null) {
            var2 = dupAlterStatement.getTableOrView();
         }

         if (dupAlterStatement.getTableName() != null) {
            StringBuffer tableStringBuffer = new StringBuffer();
            TableObject orgTableObject = dupAlterStatement.getTableName();
            String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
               ownerName = ownerName.substring(1, ownerName.length() - 1);
               if (ownerName.indexOf(32) != -1) {
                  ownerName = "\"" + ownerName + "\"";
               }
            }

            if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
               userName = userName.substring(1, userName.length() - 1);
               if (userName.indexOf(32) != -1) {
                  userName = "\"" + userName + "\"";
               }
            }

            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;

            Vector tokenVector;
            for(tokenVector = new Vector(); st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            for(int i_count = 0; i_count < tokenVector.size(); ++i_count) {
               String oracleTableName = (String)tokenVector.get(i_count);
               if (oracleTableName != null && (oracleTableName.startsWith("[") && oracleTableName.endsWith("]") || oracleTableName.startsWith("`") && oracleTableName.endsWith("`"))) {
                  oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                  if (oracleTableName.indexOf(32) != -1) {
                     oracleTableName = "\"" + oracleTableName + "\"";
                  }
               }

               if (i_count > 0) {
                  tableStringBuffer.append(".");
               }

               tableStringBuffer.append(oracleTableName);
            }

            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toSybase();
            dupAlterStatement.setTableName(orgTableObject);
         }

         if (dupAlterStatement.getAlterStatementVector() != null) {
            Vector sybaseAlterStatementVector = new Vector();
            Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            AlterTable tempAlterTable;
            if (tempAlterStatementVector.size() == 1) {
               AlterTable tempAlterTable = (AlterTable)tempAlterStatementVector.get(0);
               tempAlterTable = tempAlterTable.toSybase();
               sybaseAlterStatementVector.add(tempAlterTable);
               dupAlterStatement.setAlterStatementVector(sybaseAlterStatementVector);
               dupAlterStatement.setMultiAlterStatement((String)null);
            } else {
               dupAlterStatement.setMultiAlterStatement("ALTER");

               for(int i = 0; i < tempAlterStatementVector.size(); ++i) {
                  tempAlterTable = (AlterTable)tempAlterStatementVector.get(i);
                  AlterTable sybaseAlterTable = tempAlterTable.toSybase();
                  sybaseAlterStatementVector.add(sybaseAlterTable);
                  dupAlterStatement.setAlterStatementVector(sybaseAlterStatementVector);
               }
            }
         }

         return dupAlterStatement;
      }
   }

   public AlterStatement toDB2Alter() throws ConvertException {
      AlterStatement dupAlterStatement = this.copyObjectValues();
      if (this.alterSession != null) {
         throw new ConvertException("Query yet to be supported");
      } else {
         dupAlterStatement.setOnCondition((String)null);
         dupAlterStatement.setQuotedIdentifier((String)null);
         String var2;
         if (dupAlterStatement.getAlter() != null) {
            var2 = dupAlterStatement.getAlter();
         }

         dupAlterStatement.setIgnore((String)null);
         if (dupAlterStatement.getTableOrView() != null) {
            var2 = dupAlterStatement.getTableOrView();
         }

         if (dupAlterStatement.getTableName() != null) {
            TableObject var12 = dupAlterStatement.getTableName();
         }

         if (dupAlterStatement.getAlterStatementVector() != null) {
            Vector db2AlterStatementVector = new Vector();
            Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();

            for(int i = 0; i < tempAlterStatementVector.size(); ++i) {
               AlterTable tempAlterTable = (AlterTable)tempAlterStatementVector.get(i);
               tempAlterTable.setDatatypeMapping(this.datatypeMapping);
               if (dupAlterStatement.getTableName() != null) {
                  tempAlterTable.setAlterTableName(dupAlterStatement.getTableName().getTableName());
               }

               AlterTable db2AlterTable = tempAlterTable.toDB2();
               db2AlterStatementVector.add(db2AlterTable);
            }

            dupAlterStatement.setAlterStatementVector(db2AlterStatementVector);
         }

         dupAlterStatement.setCommaBooleanValue(false);
         if (dupAlterStatement.getTableName() != null) {
            StringBuffer tableStringBuffer = new StringBuffer();
            TableObject orgTableObject = dupAlterStatement.getTableName();
            String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
               ownerName = ownerName.substring(1, ownerName.length() - 1);
               if (ownerName.indexOf(32) != -1) {
                  ownerName = "\"" + ownerName + "\"";
               }
            }

            if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
               userName = userName.substring(1, userName.length() - 1);
               if (userName.indexOf(32) != -1) {
                  userName = "\"" + userName + "\"";
               }
            }

            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;

            Vector tokenVector;
            for(tokenVector = new Vector(); st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            for(int i_count = 0; i_count < tokenVector.size(); ++i_count) {
               String oracleTableName = (String)tokenVector.get(i_count);
               if (oracleTableName != null && (oracleTableName.startsWith("[") && oracleTableName.endsWith("]") || oracleTableName.startsWith("`") && oracleTableName.endsWith("`"))) {
                  oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                  if (oracleTableName.indexOf(32) != -1) {
                     oracleTableName = "\"" + oracleTableName + "\"";
                  }
               }

               if (i_count > 0) {
                  tableStringBuffer.append(".");
               }

               tableStringBuffer.append(oracleTableName);
            }

            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toDB2();
            dupAlterStatement.setTableName(orgTableObject);
         }

         return dupAlterStatement;
      }
   }

   public AlterStatement toBigQueryAlter() throws ConvertException {
      AlterStatement dupAlterStatement = this.copyObjectValues();
      if (this.alterSession != null) {
         throw new ConvertException("Query yet to be supported");
      } else {
         dupAlterStatement.setOnCondition((String)null);
         dupAlterStatement.setQuotedIdentifier((String)null);
         String var2;
         if (dupAlterStatement.getAlter() != null) {
            var2 = dupAlterStatement.getAlter();
         }

         dupAlterStatement.setIgnore((String)null);
         if (dupAlterStatement.getTableOrView() != null) {
            var2 = dupAlterStatement.getTableOrView();
         }

         if (dupAlterStatement.getTableName() != null) {
            TableObject var12 = dupAlterStatement.getTableName();
         }

         if (dupAlterStatement.getAlterStatementVector() != null) {
            Vector bigqueryAlterStatementVector = new Vector();
            Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            AlterTable tempAlterTable;
            if (tempAlterStatementVector.size() == 1) {
               AlterTable tempAlterTable = (AlterTable)tempAlterStatementVector.get(0);
               tempAlterTable = tempAlterTable.toBigQuery();
               bigqueryAlterStatementVector.add(tempAlterTable);
               dupAlterStatement.setAlterStatementVector(bigqueryAlterStatementVector);
               dupAlterStatement.setMultiAlterStatement((String)null);
            } else {
               dupAlterStatement.setMultiAlterStatement("ALTER");

               for(int i = 0; i < tempAlterStatementVector.size(); ++i) {
                  tempAlterTable = (AlterTable)tempAlterStatementVector.get(i);
                  AlterTable bigqueryAlterTable = tempAlterTable.toBigQuery();
                  bigqueryAlterStatementVector.add(bigqueryAlterTable);
                  dupAlterStatement.setAlterStatementVector(bigqueryAlterStatementVector);
               }
            }
         }

         if (dupAlterStatement.getTableName() != null) {
            StringBuffer tableStringBuffer = new StringBuffer();
            TableObject orgTableObject = dupAlterStatement.getTableName();
            String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
               ownerName = ownerName.substring(1, ownerName.length() - 1);
               if (ownerName.indexOf(32) != -1) {
                  ownerName = "\"" + ownerName + "\"";
               }
            }

            if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
               userName = userName.substring(1, userName.length() - 1);
               if (userName.indexOf(32) != -1) {
                  userName = "\"" + userName + "\"";
               }
            }

            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;

            Vector tokenVector;
            for(tokenVector = new Vector(); st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            for(int i_count = 0; i_count < tokenVector.size(); ++i_count) {
               String oracleTableName = (String)tokenVector.get(i_count);
               if (oracleTableName != null && (oracleTableName.startsWith("[") && oracleTableName.endsWith("]") || oracleTableName.startsWith("`") && oracleTableName.endsWith("`"))) {
                  oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                  if (oracleTableName.indexOf(32) != -1) {
                     oracleTableName = "\"" + oracleTableName + "\"";
                  }
               }

               if (i_count > 0) {
                  tableStringBuffer.append(".");
               }

               tableStringBuffer.append(oracleTableName);
            }

            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toBigQuery();
            dupAlterStatement.setTableName(orgTableObject);
         }

         return dupAlterStatement;
      }
   }

   public AlterStatement toPostgreSQLAlter() throws ConvertException {
      AlterStatement dupAlterStatement = this.copyObjectValues();
      if (this.alterSession != null) {
         throw new ConvertException("Query yet to be supported");
      } else {
         dupAlterStatement.setOnCondition((String)null);
         dupAlterStatement.setQuotedIdentifier((String)null);
         String var2;
         if (dupAlterStatement.getAlter() != null) {
            var2 = dupAlterStatement.getAlter();
         }

         dupAlterStatement.setIgnore((String)null);
         if (dupAlterStatement.getTableOrView() != null) {
            var2 = dupAlterStatement.getTableOrView();
         }

         if (dupAlterStatement.getTableName() != null) {
            TableObject var12 = dupAlterStatement.getTableName();
         }

         if (dupAlterStatement.getAlterStatementVector() != null) {
            Vector postgreSQLAlterStatementVector = new Vector();
            Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            AlterTable tempAlterTable;
            if (tempAlterStatementVector.size() == 1) {
               AlterTable tempAlterTable = (AlterTable)tempAlterStatementVector.get(0);
               tempAlterTable = tempAlterTable.toPostgreSQL();
               postgreSQLAlterStatementVector.add(tempAlterTable);
               dupAlterStatement.setAlterStatementVector(postgreSQLAlterStatementVector);
               dupAlterStatement.setMultiAlterStatement((String)null);
            } else {
               dupAlterStatement.setMultiAlterStatement("ALTER");

               for(int i = 0; i < tempAlterStatementVector.size(); ++i) {
                  tempAlterTable = (AlterTable)tempAlterStatementVector.get(i);
                  AlterTable postgreAlterTable = tempAlterTable.toPostgreSQL();
                  postgreSQLAlterStatementVector.add(postgreAlterTable);
                  dupAlterStatement.setAlterStatementVector(postgreSQLAlterStatementVector);
               }
            }
         }

         if (dupAlterStatement.getTableName() != null) {
            StringBuffer tableStringBuffer = new StringBuffer();
            TableObject orgTableObject = dupAlterStatement.getTableName();
            String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
               ownerName = ownerName.substring(1, ownerName.length() - 1);
               if (ownerName.indexOf(32) != -1) {
                  ownerName = "\"" + ownerName + "\"";
               }
            }

            if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
               userName = userName.substring(1, userName.length() - 1);
               if (userName.indexOf(32) != -1) {
                  userName = "\"" + userName + "\"";
               }
            }

            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;

            Vector tokenVector;
            for(tokenVector = new Vector(); st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            for(int i_count = 0; i_count < tokenVector.size(); ++i_count) {
               String oracleTableName = (String)tokenVector.get(i_count);
               if (oracleTableName != null && (oracleTableName.startsWith("[") && oracleTableName.endsWith("]") || oracleTableName.startsWith("`") && oracleTableName.endsWith("`"))) {
                  oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                  if (oracleTableName.indexOf(32) != -1) {
                     oracleTableName = "\"" + oracleTableName + "\"";
                  }
               }

               if (i_count > 0) {
                  tableStringBuffer.append(".");
               }

               tableStringBuffer.append(oracleTableName);
            }

            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toPostgreSQL();
            dupAlterStatement.setTableName(orgTableObject);
         }

         return dupAlterStatement;
      }
   }

   public AlterStatement toMySQLAlter() throws ConvertException {
      AlterStatement dupAlterStatement = this.copyObjectValues();
      if (this.alterSession != null) {
         throw new ConvertException("Query yet to be supported");
      } else {
         dupAlterStatement.setOnCondition((String)null);
         dupAlterStatement.setQuotedIdentifier((String)null);
         dupAlterStatement.setMultiAlterStatement((String)null);
         String var2;
         if (dupAlterStatement.getAlter() != null) {
            var2 = dupAlterStatement.getAlter();
         }

         if (dupAlterStatement.getIgnore() != null) {
            var2 = dupAlterStatement.getIgnore();
         }

         if (dupAlterStatement.getTableOrView() != null) {
            var2 = dupAlterStatement.getTableOrView();
         }

         if (dupAlterStatement.getTableName() != null) {
            StringBuffer tableStringBuffer = new StringBuffer();
            TableObject orgTableObject = dupAlterStatement.getTableName();
            String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && ownerName.startsWith("[") && ownerName.endsWith("]")) {
               ownerName = ownerName.substring(1, ownerName.length() - 1);
               if (ownerName.indexOf(32) != -1) {
                  ownerName = "\"" + ownerName + "\"";
               }
            }

            if (userName != null && userName.startsWith("[") && userName.endsWith("]")) {
               userName = userName.substring(1, userName.length() - 1);
               if (userName.indexOf(32) != -1) {
                  userName = "\"" + userName + "\"";
               }
            }

            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;

            Vector tokenVector;
            for(tokenVector = new Vector(); st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            for(int i_count = 0; i_count < tokenVector.size(); ++i_count) {
               String oracleTableName = (String)tokenVector.get(i_count);
               if (oracleTableName != null && oracleTableName.startsWith("[") && oracleTableName.endsWith("]")) {
                  oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                  if (oracleTableName.indexOf(32) != -1) {
                     oracleTableName = "\"" + oracleTableName + "\"";
                  }
               }

               if (i_count > 0) {
                  tableStringBuffer.append(".");
               }

               tableStringBuffer.append(oracleTableName);
            }

            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toMySQL();
            dupAlterStatement.setTableName(orgTableObject);
         }

         if (dupAlterStatement.getAlterStatementVector() != null) {
            Vector mySQLAlterStatementVector = new Vector();
            Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();

            for(int i = 0; i < tempAlterStatementVector.size(); ++i) {
               AlterTable tempAlterTable = (AlterTable)tempAlterStatementVector.get(i);
               AlterTable mySQLAlterTable = tempAlterTable.toMySQL();
               mySQLAlterStatementVector.add(mySQLAlterTable);
            }

            dupAlterStatement.setAlterStatementVector(mySQLAlterStatementVector);
         }

         dupAlterStatement.setCommaBooleanValue(true);
         return dupAlterStatement;
      }
   }

   public AlterStatement toSnowflakeAlter() throws ConvertException {
      AlterStatement dupAlterStatement = this.copyObjectValues();
      if (this.alterSession != null) {
         throw new ConvertException("Query yet to be supported");
      } else {
         dupAlterStatement.setOnCondition((String)null);
         dupAlterStatement.setQuotedIdentifier((String)null);
         String var2;
         if (dupAlterStatement.getAlter() != null) {
            var2 = dupAlterStatement.getAlter();
         }

         dupAlterStatement.setIgnore((String)null);
         if (dupAlterStatement.getTableOrView() != null) {
            var2 = dupAlterStatement.getTableOrView();
         }

         if (dupAlterStatement.getTableName() != null) {
            TableObject var12 = dupAlterStatement.getTableName();
         }

         if (dupAlterStatement.getAlterStatementVector() != null) {
            Vector snowflakeAlterStatementVector = new Vector();
            Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            AlterTable tempAlterTable;
            if (tempAlterStatementVector.size() == 1) {
               AlterTable tempAlterTable = (AlterTable)tempAlterStatementVector.get(0);
               tempAlterTable = tempAlterTable.toSnowflake();
               snowflakeAlterStatementVector.add(tempAlterTable);
               dupAlterStatement.setAlterStatementVector(snowflakeAlterStatementVector);
               dupAlterStatement.setMultiAlterStatement((String)null);
            } else {
               dupAlterStatement.setMultiAlterStatement("ALTER");

               for(int i = 0; i < tempAlterStatementVector.size(); ++i) {
                  tempAlterTable = (AlterTable)tempAlterStatementVector.get(i);
                  AlterTable snowflakeAlterTable = tempAlterTable.toSnowflake();
                  snowflakeAlterStatementVector.add(snowflakeAlterTable);
                  dupAlterStatement.setAlterStatementVector(snowflakeAlterStatementVector);
               }
            }
         }

         if (dupAlterStatement.getTableName() != null) {
            StringBuffer tableStringBuffer = new StringBuffer();
            TableObject orgTableObject = dupAlterStatement.getTableName();
            String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
               ownerName = ownerName.substring(1, ownerName.length() - 1);
               if (ownerName.indexOf(32) != -1) {
                  ownerName = "\"" + ownerName + "\"";
               }
            }

            if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
               userName = userName.substring(1, userName.length() - 1);
               if (userName.indexOf(32) != -1) {
                  userName = "\"" + userName + "\"";
               }
            }

            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;

            Vector tokenVector;
            for(tokenVector = new Vector(); st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            for(int i_count = 0; i_count < tokenVector.size(); ++i_count) {
               String snowflakeTableName = (String)tokenVector.get(i_count);
               if (snowflakeTableName != null && (snowflakeTableName.startsWith("[") && snowflakeTableName.endsWith("]") || snowflakeTableName.startsWith("`") && snowflakeTableName.endsWith("`"))) {
                  snowflakeTableName = snowflakeTableName.substring(1, snowflakeTableName.length() - 1);
                  if (snowflakeTableName.indexOf(32) != -1) {
                     snowflakeTableName = "\"" + snowflakeTableName + "\"";
                  }
               }

               if (i_count > 0) {
                  tableStringBuffer.append(".");
               }

               tableStringBuffer.append(snowflakeTableName);
            }

            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toSnowflake();
            dupAlterStatement.setTableName(orgTableObject);
         }

         return dupAlterStatement;
      }
   }

   public AlterStatement toANSIAlter() throws ConvertException {
      AlterStatement dupAlterStatement = this.copyObjectValues();
      if (this.alterSession != null) {
         throw new ConvertException("Query yet to be supported");
      } else {
         dupAlterStatement.setOnCondition((String)null);
         dupAlterStatement.setQuotedIdentifier((String)null);
         String var2;
         if (dupAlterStatement.getAlter() != null) {
            var2 = dupAlterStatement.getAlter();
         }

         dupAlterStatement.setIgnore((String)null);
         if (dupAlterStatement.getTableOrView() != null) {
            var2 = dupAlterStatement.getTableOrView();
         }

         if (dupAlterStatement.getTableName() != null) {
            StringBuffer tableStringBuffer = new StringBuffer();
            TableObject orgTableObject = dupAlterStatement.getTableName();
            String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
               ownerName = ownerName.substring(1, ownerName.length() - 1);
               if (ownerName.indexOf(32) != -1) {
                  ownerName = "\"" + ownerName + "\"";
               }
            }

            if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
               userName = userName.substring(1, userName.length() - 1);
               if (userName.indexOf(32) != -1) {
                  userName = "\"" + userName + "\"";
               }
            }

            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;

            Vector tokenVector;
            for(tokenVector = new Vector(); st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            for(int i_count = 0; i_count < tokenVector.size(); ++i_count) {
               String oracleTableName = (String)tokenVector.get(i_count);
               if (oracleTableName != null && (oracleTableName.startsWith("[") && oracleTableName.endsWith("]") || oracleTableName.startsWith("`") && oracleTableName.endsWith("`"))) {
                  oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                  if (oracleTableName.indexOf(32) != -1) {
                     oracleTableName = "\"" + oracleTableName + "\"";
                  }
               }

               if (i_count > 0) {
                  tableStringBuffer.append(".");
               }

               tableStringBuffer.append(oracleTableName);
            }

            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toANSISQL();
            dupAlterStatement.setTableName(orgTableObject);
         }

         if (dupAlterStatement.getAlterStatementVector() != null) {
            Vector ansiAlterStatementVector = new Vector();
            Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            AlterTable tempAlterTable;
            if (tempAlterStatementVector.size() == 1) {
               AlterTable tempAlterTable = (AlterTable)tempAlterStatementVector.get(0);
               tempAlterTable = tempAlterTable.toANSI();
               ansiAlterStatementVector.add(tempAlterTable);
               dupAlterStatement.setAlterStatementVector(ansiAlterStatementVector);
               dupAlterStatement.setMultiAlterStatement((String)null);
            } else {
               dupAlterStatement.setMultiAlterStatement("ALTER");

               for(int i = 0; i < tempAlterStatementVector.size(); ++i) {
                  tempAlterTable = (AlterTable)tempAlterStatementVector.get(i);
                  AlterTable ansiAlterTable = tempAlterTable.toANSI();
                  ansiAlterStatementVector.add(ansiAlterTable);
                  dupAlterStatement.setAlterStatementVector(ansiAlterStatementVector);
               }
            }
         }

         return dupAlterStatement;
      }
   }

   public AlterStatement toInformixAlter() throws ConvertException {
      AlterStatement dupAlterStatement = this.copyObjectValues();
      if (this.alterSession != null) {
         throw new ConvertException("Query yet to be supported");
      } else {
         dupAlterStatement.setOnCondition((String)null);
         dupAlterStatement.setQuotedIdentifier((String)null);
         dupAlterStatement.setMultiAlterStatement((String)null);
         String var2;
         if (dupAlterStatement.getAlter() != null) {
            var2 = dupAlterStatement.getAlter();
         }

         dupAlterStatement.setIgnore((String)null);
         if (dupAlterStatement.getTableOrView() != null) {
            var2 = dupAlterStatement.getTableOrView();
         }

         if (dupAlterStatement.getTableName() != null) {
            StringBuffer tableStringBuffer = new StringBuffer();
            TableObject orgTableObject = dupAlterStatement.getTableName();
            String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
               ownerName = ownerName.substring(1, ownerName.length() - 1);
               if (ownerName.indexOf(32) != -1) {
                  ownerName = "\"" + ownerName + "\"";
               }
            }

            if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
               userName = userName.substring(1, userName.length() - 1);
               if (userName.indexOf(32) != -1) {
                  userName = "\"" + userName + "\"";
               }
            }

            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;

            Vector tokenVector;
            for(tokenVector = new Vector(); st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            for(int i_count = 0; i_count < tokenVector.size(); ++i_count) {
               String oracleTableName = (String)tokenVector.get(i_count);
               if (oracleTableName != null && (oracleTableName.startsWith("[") && oracleTableName.endsWith("]") || oracleTableName.startsWith("`") && oracleTableName.endsWith("`"))) {
                  oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                  if (oracleTableName.indexOf(32) != -1) {
                     oracleTableName = "\"" + oracleTableName + "\"";
                  }
               }

               if (i_count > 0) {
                  tableStringBuffer.append(".");
               }

               tableStringBuffer.append(oracleTableName);
            }

            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toInformix();
            dupAlterStatement.setTableName(orgTableObject);
         }

         if (dupAlterStatement.getAlterStatementVector() != null) {
            Vector informixAlterStatementVector = new Vector();
            Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();

            for(int i = 0; i < tempAlterStatementVector.size(); ++i) {
               AlterTable tempAlterTable = (AlterTable)tempAlterStatementVector.get(i);
               AlterTable informixAlterTable = tempAlterTable.toInformix();
               informixAlterStatementVector.add(informixAlterTable);
            }

            dupAlterStatement.setAlterStatementVector(informixAlterStatementVector);
         }

         dupAlterStatement.setCommaBooleanValue(true);
         return dupAlterStatement;
      }
   }

   public AlterStatement toTimesTenAlter() throws ConvertException {
      AlterStatement dupAlterStatement = this.copyObjectValues();
      generalComment = "";
      dupAlterStatement.setOnCondition((String)null);
      dupAlterStatement.setQuotedIdentifier((String)null);
      dupAlterStatement.setMultiAlterStatement((String)null);
      dupAlterStatement.setIgnore((String)null);
      if (this.alterSession != null) {
         throw new ConvertException("\n'ALTER SESSION'  is not supported in TimesTen 5.1.21\n\n");
      } else {
         if (dupAlterStatement.getTableName() != null) {
            StringBuffer tableStringBuffer = new StringBuffer();
            TableObject orgTableObject = dupAlterStatement.getTableName();
            String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
               ownerName = ownerName.substring(1, ownerName.length() - 1);
               if (ownerName.indexOf(32) != -1) {
                  ownerName = "\"" + ownerName + "\"";
               }
            }

            if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
               userName = userName.substring(1, userName.length() - 1);
               if (userName.indexOf(32) != -1) {
                  userName = "\"" + userName + "\"";
               }
            }

            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;

            Vector tokenVector;
            for(tokenVector = new Vector(); st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            for(int i_count = 0; i_count < tokenVector.size(); ++i_count) {
               String timesTenTableName = (String)tokenVector.get(i_count);
               if (timesTenTableName != null && (timesTenTableName.startsWith("[") && timesTenTableName.endsWith("]") || timesTenTableName.startsWith("`") && timesTenTableName.endsWith("`"))) {
                  timesTenTableName = timesTenTableName.substring(1, timesTenTableName.length() - 1);
                  if (timesTenTableName.indexOf(32) != -1) {
                     timesTenTableName = "\"" + timesTenTableName + "\"";
                  }
               }

               if (i_count > 0) {
                  tableStringBuffer.append(".");
               }

               tableStringBuffer.append(timesTenTableName);
            }

            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toTimesTen();
            dupAlterStatement.setTableName(orgTableObject);
         }

         if (dupAlterStatement.getAlterStatementVector() != null) {
            Vector timesTenAlterStatementVector = new Vector();
            Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();

            for(int i = 0; i < tempAlterStatementVector.size(); ++i) {
               AlterTable tempAlterTable = (AlterTable)tempAlterStatementVector.get(i);
               AlterTable timesTenAlterTable = tempAlterTable.toTimesTen();
               timesTenAlterStatementVector.add(timesTenAlterTable);
            }

            dupAlterStatement.setAlterStatementVector(timesTenAlterStatementVector);
         }

         dupAlterStatement.setCommaBooleanValue(false);
         return dupAlterStatement;
      }
   }

   public AlterStatement toNetezzaAlter() throws ConvertException {
      AlterStatement dupAlterStatement = this.copyObjectValues();
      if (this.alterSession != null) {
         throw new ConvertException("/*SwisSQL Message : Netezza does not support the ALTER SESSION clauses of Oracle*/");
      } else {
         dupAlterStatement.setOnCondition((String)null);
         dupAlterStatement.setQuotedIdentifier((String)null);
         String tempTableOrView;
         if (dupAlterStatement.getAlter() != null) {
            tempTableOrView = dupAlterStatement.getAlter();
         }

         dupAlterStatement.setIgnore((String)null);
         if (dupAlterStatement.getTableOrView() != null) {
            tempTableOrView = dupAlterStatement.getTableOrView();
            if (tempTableOrView.equalsIgnoreCase("view")) {
               throw new ConvertException("/*SwisSQL Message : Netezza does not support the ALTER VIEW clauses of Oracle*/");
            }
         }

         if (dupAlterStatement.getTableName() != null) {
            StringBuffer tableStringBuffer = new StringBuffer();
            TableObject orgTableObject = dupAlterStatement.getTableName();
            String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
               ownerName = ownerName.substring(1, ownerName.length() - 1);
               if (ownerName.indexOf(32) != -1) {
                  ownerName = "\"" + ownerName + "\"";
               }
            }

            if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
               userName = userName.substring(1, userName.length() - 1);
               if (userName.indexOf(32) != -1) {
                  userName = "\"" + userName + "\"";
               }
            }

            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;

            Vector tokenVector;
            for(tokenVector = new Vector(); st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            for(int i_count = 0; i_count < tokenVector.size(); ++i_count) {
               String oracleTableName = (String)tokenVector.get(i_count);
               if (oracleTableName != null && (oracleTableName.startsWith("[") && oracleTableName.endsWith("]") || oracleTableName.startsWith("`") && oracleTableName.endsWith("`"))) {
                  oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                  if (oracleTableName.indexOf(32) != -1) {
                     oracleTableName = "\"" + oracleTableName + "\"";
                  }
               }

               if (i_count > 0) {
                  tableStringBuffer.append(".");
               }

               tableStringBuffer.append(oracleTableName);
            }

            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toNetezza();
            dupAlterStatement.setTableName(orgTableObject);
         }

         if (dupAlterStatement.getAlterStatementVector() != null) {
            Vector netezzaAlterStatementVector = new Vector();
            Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            AlterTable tempAlterTable;
            if (tempAlterStatementVector.size() == 1) {
               AlterTable tempAlterTable = (AlterTable)tempAlterStatementVector.get(0);
               tempAlterTable = tempAlterTable.toNetezza();
               netezzaAlterStatementVector.add(tempAlterTable);
               dupAlterStatement.setAlterStatementVector(netezzaAlterStatementVector);
               dupAlterStatement.setMultiAlterStatement((String)null);
            } else {
               dupAlterStatement.setMultiAlterStatement("ALTER");

               for(int i = 0; i < tempAlterStatementVector.size(); ++i) {
                  tempAlterTable = (AlterTable)tempAlterStatementVector.get(i);
                  AlterTable netezzaAlterTable = tempAlterTable.toNetezza();
                  netezzaAlterStatementVector.add(netezzaAlterTable);
                  dupAlterStatement.setAlterStatementVector(netezzaAlterStatementVector);
               }
            }
         }

         return dupAlterStatement;
      }
   }

   public AlterStatement toTeradataAlter() throws ConvertException {
      AlterStatement dupAlterStatement = this.copyObjectValues();
      if (this.alterSession != null) {
         throw new ConvertException("Query yet to be supported");
      } else {
         if (dupAlterStatement.getCommentClass() != null) {
            dupAlterStatement.getCommentClass().setSQLDialect(12);
         }

         dupAlterStatement.setOnCondition((String)null);
         dupAlterStatement.setQuotedIdentifier((String)null);
         String var2;
         if (dupAlterStatement.getAlter() != null) {
            var2 = dupAlterStatement.getAlter();
         }

         dupAlterStatement.setIgnore((String)null);
         if (dupAlterStatement.getTableOrView() != null) {
            var2 = dupAlterStatement.getTableOrView();
         }

         if (dupAlterStatement.getTableName() != null) {
            StringBuffer tableStringBuffer = new StringBuffer();
            TableObject orgTableObject = dupAlterStatement.getTableName();
            String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
               ownerName = ownerName.substring(1, ownerName.length() - 1);
               if (ownerName.indexOf(32) != -1) {
                  ownerName = "\"" + ownerName + "\"";
               }
            }

            if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
               userName = userName.substring(1, userName.length() - 1);
               if (userName.indexOf(32) != -1) {
                  userName = "\"" + userName + "\"";
               }
            }

            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;

            Vector tokenVector;
            for(tokenVector = new Vector(); st.hasMoreTokens(); ++count) {
               tokenVector.add(st.nextToken());
            }

            for(int i_count = 0; i_count < tokenVector.size(); ++i_count) {
               String oracleTableName = (String)tokenVector.get(i_count);
               if (oracleTableName != null && (oracleTableName.startsWith("[") && oracleTableName.endsWith("]") || oracleTableName.startsWith("`") && oracleTableName.endsWith("`"))) {
                  oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                  if (oracleTableName.indexOf(32) != -1) {
                     oracleTableName = "\"" + oracleTableName + "\"";
                  }
               }

               if (i_count > 0) {
                  tableStringBuffer.append(".");
               }

               tableStringBuffer.append(oracleTableName);
            }

            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toTeradata();
            dupAlterStatement.setTableName(orgTableObject);
         }

         if (dupAlterStatement.getAlterStatementVector() != null) {
            Vector teradataAlterStatementVector = new Vector();
            Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            AlterTable tempAlterTable;
            if (tempAlterStatementVector.size() == 1) {
               AlterTable tempAlterTable = (AlterTable)tempAlterStatementVector.get(0);
               tempAlterTable = tempAlterTable.toTeradata();
               teradataAlterStatementVector.add(tempAlterTable);
               dupAlterStatement.setAlterStatementVector(teradataAlterStatementVector);
               dupAlterStatement.setMultiAlterStatement((String)null);
            } else {
               dupAlterStatement.setMultiAlterStatement("ALTER");

               for(int i = 0; i < tempAlterStatementVector.size(); ++i) {
                  tempAlterTable = (AlterTable)tempAlterStatementVector.get(i);
                  AlterTable teradataAlterTable = tempAlterTable.toTeradata();
                  teradataAlterStatementVector.add(teradataAlterTable);
                  dupAlterStatement.setAlterStatementVector(teradataAlterStatementVector);
               }
            }
         }

         return dupAlterStatement;
      }
   }

   public String removeIndent(String str) {
      str = str.replace('\n', ' ');
      str = str.replace('\t', ' ');
      return str;
   }

   public AlterStatement copyObjectValues() {
      AlterStatement dupAlterStatement = new AlterStatement();
      dupAlterStatement.setTriggers(this.getTriggers());
      dupAlterStatement.setAlter(this.getAlter());
      dupAlterStatement.setIgnore(this.getIgnore());
      dupAlterStatement.setTableOrView(this.getTableOrView());
      dupAlterStatement.setTableName(this.getTableName());
      dupAlterStatement.setSession(this.getSession());
      dupAlterStatement.setSetString(this.getSetString());
      dupAlterStatement.setParameter(this.getParameter());
      dupAlterStatement.setParameterValue(this.getParameterValue());
      dupAlterStatement.setSequence(this.getSequence());
      dupAlterStatement.setAlterStatementVector(this.getAlterStatementVector());
      dupAlterStatement.setCommaBooleanValue(this.getCommaBooleanValue());
      dupAlterStatement.setOnCondition(this.onString);
      dupAlterStatement.setQuotedIdentifier(this.quotedIdentifierString);
      dupAlterStatement.setCommentClass(this.commentObject);
      dupAlterStatement.setObjectContext(this.objectContext);
      return dupAlterStatement;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      int i;
      if (!SwisSQLUtils.swissqlMessageList.isEmpty()) {
         sb.append("/* SwisSQL Messages :\n");

         for(i = 0; i < SwisSQLUtils.swissqlMessageList.size(); ++i) {
            sb.append(SwisSQLUtils.swissqlMessageList.get(i).toString() + "\n");
         }

         sb.append("*/\n");
         SwisSQLUtils.swissqlMessageList.clear();
      }

      if (this.commentObject != null) {
         sb.append(this.commentObject.toString() + "\n");
      }

      if (this.indexStatement != null) {
         return this.indexStatement.toString();
      } else if (this.dropIndexStatement != null) {
         return this.dropIndexStatement.toString();
      } else {
         if (this.singleQueryIntoMultipleQueriesForPLSQL() != null) {
            sb.append(this.singleQueryIntoMultipleQueriesForPLSQL());
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
         }

         if (this.alter != null) {
            sb.append(this.alter.toUpperCase() + " ");
         }

         if (this.ignore != null) {
            sb.append(this.ignore.toUpperCase() + " ");
         }

         if (this.tableOrView != null) {
            sb.append(this.tableOrView.toUpperCase() + " ");
         }

         if (this.alterSession != null) {
            sb.append(" " + this.alterSession.toUpperCase());
         }

         if (this.setString != null) {
            sb.append(" " + this.setString.toUpperCase());
         }

         if (this.parameter != null) {
            sb.append(" " + this.parameter.toUpperCase());
         }

         if (this.parameterValue != null) {
            sb.append(" = " + this.parameterValue);
         }

         if (this.tableName != null) {
            this.tableName.setObjectContext(this.objectContext);
            sb.append(this.tableName + " ");
         }

         if (this.alterStatementVector != null) {
            for(i = 0; i < this.alterStatementVector.size(); ++i) {
               SwisSQLStatement swisStmt = null;
               if (this.alterStatementVector.get(i) instanceof CreateQueryStatement) {
                  swisStmt = (SwisSQLStatement)this.alterStatementVector.get(i);
                  sb.append(swisStmt.toString() + " ");
               } else if (this.alterStatementVector.get(i) instanceof DropStatement) {
                  swisStmt = (SwisSQLStatement)this.alterStatementVector.get(i);
                  sb.append(swisStmt.toString() + " ");
               } else if (this.alterStatementVector.get(i) instanceof AlterTable) {
                  AlterTable tempAlterTable = (AlterTable)this.alterStatementVector.get(i);
                  tempAlterTable.setObjectContext(this.objectContext);
                  if (i == 0) {
                     if (tempAlterTable.getsp_renameStmt() != null) {
                        sb.insert(0, tempAlterTable.getsp_renameStmt() + "\n");
                     }

                     sb.append(tempAlterTable);
                  } else if (this.multiAlterStatement == null) {
                     if (this.commaIsSet) {
                        sb.append("," + tempAlterTable);
                     } else {
                        sb.append(tempAlterTable);
                     }
                  } else if (tempAlterTable.toString().trim().length() < 1) {
                     sb.append(" ");
                  } else {
                     if (tempAlterTable.getsp_renameStmt() != null) {
                        sb.append(tempAlterTable.getsp_renameStmt() + "\n");
                     }

                     sb.append("\n\n" + this.multiAlterStatement + " ");
                     if (this.tableOrView != null) {
                        sb.append(this.tableOrView.toUpperCase() + " ");
                     }

                     if (this.tableName != null) {
                        sb.append(this.tableName + " ");
                     }

                     sb.append(tempAlterTable);
                  }
               }
            }
         }

         if (this.onString != null) {
            sb.append("\n\t" + this.onString);
         }

         if (this.quotedIdentifierString != null) {
            sb.append(" " + this.quotedIdentifierString);
         }

         if (!generalComment.equalsIgnoreCase("")) {
            sb.append("\n\n" + generalComment + "\n\n");
         }

         if (this.deltek_triggers != null) {
            sb.append("\n\n" + this.deltek_triggers);
         }

         return sb.toString();
      }
   }

   private String singleQueryIntoMultipleQueriesForPLSQL() {
      return SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
   }

   public UserObjectContext getObjectContext() {
      return this.objectContext;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.objectContext = obj;
   }

   private SwisSQLStatement handleAddIndexClause(AlterStatement dupAlterStatement, AlterTable tempAlterTable) {
      this.indexStatement = new CreateQueryStatement();
      CreateIndexClause createIndexClauseObject = new CreateIndexClause();
      new AddClause();
      createIndexClauseObject.setIndexOrKey("CREATE INDEX");
      TableObject indexNameObj = new TableObject();
      String indexName = tempAlterTable.getAddClause().getUniqueConstraintName();
      if (indexName != null && indexName.startsWith("`") && indexName.endsWith("`")) {
         indexName = indexName.substring(1, indexName.length() - 1);
         if (indexName.indexOf(32) != -1) {
            indexName = "\"" + indexName + "\"";
         }
      }

      indexNameObj.setTableName(indexName);
      createIndexClauseObject.setIndexName(indexNameObj);
      createIndexClauseObject.setOn("ON");
      createIndexClauseObject.setTableOrView(dupAlterStatement.getTableName());
      createIndexClauseObject.setOpenBraces("(");
      Vector indexColumnVect = tempAlterTable.getAddClause().getIndexColumnVector();
      ArrayList indexColumnList = new ArrayList();

      for(int i = 0; i < indexColumnVect.size(); ++i) {
         SelectColumn selCol = new SelectColumn();
         Vector selectColExp = new Vector();
         String indexColName = indexColumnVect.get(i).toString();
         if (indexColName != null && indexColName.startsWith("`") && indexColName.endsWith("`")) {
            indexColName = indexColName.substring(1, indexColName.length() - 1);
            if (indexColName.indexOf(32) != -1) {
               indexColName = "\"" + indexColName + "\"";
            }
         }

         selectColExp.addElement(indexColName);
         selCol.setColumnExpression(selectColExp);
         IndexColumn indexCol = new IndexColumn();
         indexCol.setIndexColumnName(selCol);
         indexColumnList.add(indexCol);
      }

      createIndexClauseObject.setIndexColumns(indexColumnList);
      createIndexClauseObject.setClosedBraces(")");
      this.indexStatement.setCreateIndexClause(createIndexClauseObject);
      return this.indexStatement;
   }

   private SwisSQLStatement handleDropIndexClause(AlterStatement dupAlterStatement, AlterTable tempAlterTable) {
      this.dropIndexStatement = new DropStatement();
      this.dropIndexStatement.setDrop("DROP");
      this.dropIndexStatement.setTableOrSequence("INDEX");
      String tableName = dupAlterStatement.getTableName().getTableName();
      if (tableName.startsWith("`") && tableName.endsWith("`")) {
         tableName = tableName.substring(1, tableName.length() - 1);
      }

      tableName = CustomizeUtil.objectNamesToBracedIdentifier(tableName, SwisSQLUtils.getKeywords(2), (ModifiedObjectAttr)null);
      TableObject indexObject = new TableObject();
      String indexName = tempAlterTable.getDropClause().getIndexName();
      if (indexName.startsWith("`") && indexName.endsWith("`")) {
         indexName = indexName.substring(1, indexName.length() - 1);
      }

      indexName = CustomizeUtil.objectNamesToBracedIdentifier(indexName, SwisSQLUtils.getKeywords(2), (ModifiedObjectAttr)null);
      indexObject.setUser(tableName);
      indexObject.setDot(".");
      indexObject.setTableName(indexName);
      indexObject.setOrigTableName(indexName);
      Vector tableObjectVector = new Vector();
      tableObjectVector.add(indexObject);
      this.dropIndexStatement.setTableNameVector(tableObjectVector);
      return this.dropIndexStatement;
   }

   public String convertTableObjectToString(TableObject tableObject) {
      StringBuffer stringbuffer = new StringBuffer();
      if (tableObject.getUser() != null) {
         stringbuffer.append(this.handleDelimiters(tableObject.getUser()));
         if (tableObject.getOwner() != null) {
            stringbuffer.append(".");
         } else {
            stringbuffer.append("..");
         }
      }

      if (tableObject.getOwner() != null) {
         stringbuffer.append(this.handleDelimiters(tableObject.getOwner()));
         stringbuffer.append(".");
      }

      if (tableObject.getTableName() != null) {
         stringbuffer.append(this.handleDelimiters(tableObject.getTableName()));
      }

      if (tableObject.getDatabaseName() != null) {
         stringbuffer.append(this.handleDelimiters(tableObject.getDatabaseName()));
      }

      return stringbuffer.toString();
   }

   private String handleDelimiters(String obj) {
      if (obj.startsWith("`") && obj.endsWith("`")) {
         obj = obj.substring(1, obj.length() - 1);
      }

      return obj;
   }

   public String toVectorWiseString() throws ConvertException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public SwisSQLStatement toVectorWise() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toInformix() throws ConvertException {
      return this.toInformixAlter();
   }

   public SwisSQLStatement toNetezza() throws ConvertException {
      return this.toNetezzaAlter();
   }

   public SwisSQLStatement toTimesTen() throws ConvertException {
      return this.toTimesTenAlter();
   }

   public SwisSQLStatement toOracle() throws ConvertException {
      return this.toOracleAlter();
   }

   public SwisSQLStatement toSqlite() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSapHana() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toAthena() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSnowflake() throws ConvertException {
      return this.toSnowflakeAlter();
   }

   public SwisSQLStatement toMySQL() throws ConvertException {
      return this.toMySQLAlter();
   }

   public SwisSQLStatement toPostgreSQL() throws ConvertException {
      return this.toPostgreSQLAlter();
   }

   public SwisSQLStatement toBigQuery() throws ConvertException {
      return this.toBigQueryAlter();
   }

   public SwisSQLStatement toDB2() throws ConvertException {
      return this.toDB2Alter();
   }

   public SwisSQLStatement toSybase() throws ConvertException {
      return this.toSybaseAlter();
   }

   public SwisSQLStatement toMSSQLServer() throws ConvertException {
      return this.toMSSQLServerAlter();
   }

   public SwisSQLStatement toTeradata() throws ConvertException {
      return this.toTeradataAlter();
   }

   public SwisSQLStatement toANSI() throws ConvertException {
      return this.toANSIAlter();
   }

   public SwisSQLStatement toMsAccessJdbc() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toExcel() throws ConvertException {
      return null;
   }
}
