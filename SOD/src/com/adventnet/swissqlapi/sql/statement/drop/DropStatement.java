package com.adventnet.swissqlapi.sql.statement.drop;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import java.util.Vector;

public class DropStatement implements SwisSQLStatement {
   private String drop;
   private String tableOrSequence;
   private String ifExists;
   private Vector tableObjectVector;
   private String restrictOrCascade;
   private String constraints;
   private UserObjectContext objectContext = null;
   private String multipleQuery;
   private String materializedObject;
   private CommentClass commentObject;

   public void setDrop(String drop) {
      this.drop = drop;
   }

   public void setTableOrSequence(String tableOrSequence) {
      this.tableOrSequence = tableOrSequence;
   }

   public void setIfExists(String ifExists) {
      this.ifExists = ifExists;
   }

   public void setTableNameVector(Vector tableObjectVector) {
      this.tableObjectVector = tableObjectVector;
   }

   public void setRestrictOrCascade(String restrictOrCascade) {
      this.restrictOrCascade = restrictOrCascade;
   }

   public void setConstraints(String constraints) {
      this.constraints = constraints;
   }

   public void setCommentClass(CommentClass commentObject) {
      this.commentObject = commentObject;
   }

   public void setMultipleQuery(String multipleQuery) {
      this.multipleQuery = multipleQuery;
   }

   public void setMaterializedView(String materialized) {
      this.materializedObject = materialized;
   }

   public String getMaterializedView() {
      return this.materializedObject;
   }

   public String getTableOrSequence() {
      return this.tableOrSequence;
   }

   public String getConstraints() {
      return this.constraints;
   }

   public Vector getTableObjectVector() {
      return this.tableObjectVector;
   }

   public CommentClass getCommentClass() {
      return this.commentObject;
   }

   public String toANSIString() throws ConvertException {
      DropStatement dropStatement = this.copyObjectValues();
      if (dropStatement.getTableObjectVector() != null) {
         Vector tableObjVector = dropStatement.getTableObjectVector();

         for(int i = 0; i < tableObjVector.size(); ++i) {
            TableObject tableObj = (TableObject)tableObjVector.get(i);
            tableObj.toANSISQL();
         }
      }

      dropStatement.setIfExists((String)null);
      dropStatement.setConstraints((String)null);
      return dropStatement.toString();
   }

   public String toTeradataString() throws ConvertException {
      DropStatement dropStatement = this.copyObjectValues();
      if (dropStatement.commentObject != null) {
         dropStatement.commentObject.setSQLDialect(12);
      }

      if (dropStatement.getTableObjectVector() != null) {
         Vector tableObjVector = dropStatement.getTableObjectVector();

         for(int i = 0; i < tableObjVector.size(); ++i) {
            TableObject tableObj = (TableObject)tableObjVector.get(i);
            tableObj.toTeradata();
         }
      }

      dropStatement.setIfExists((String)null);
      dropStatement.setConstraints((String)null);
      return dropStatement.toString();
   }

   public String toDB2String() throws ConvertException {
      DropStatement dropStatement = this.copyObjectValues();
      if (dropStatement.getTableObjectVector() != null) {
         Vector tableObjVector = dropStatement.getTableObjectVector();

         for(int i = 0; i < tableObjVector.size(); ++i) {
            TableObject tableObj = (TableObject)tableObjVector.get(i);
            tableObj.toDB2();
         }
      }

      dropStatement.setIfExists((String)null);
      dropStatement.setRestrictOrCascade((String)null);
      dropStatement.setConstraints((String)null);
      return dropStatement.toString();
   }

   public String toInformixString() throws ConvertException {
      DropStatement dropStatement = this.copyObjectValues();
      if (dropStatement.getTableObjectVector() != null) {
         Vector tableObjVector = dropStatement.getTableObjectVector();

         for(int i = 0; i < tableObjVector.size(); ++i) {
            TableObject tableObj = (TableObject)tableObjVector.get(i);
            tableObj.toInformix();
         }
      }

      dropStatement.setIfExists((String)null);
      dropStatement.setConstraints((String)null);
      return dropStatement.toString();
   }

   public String toMSSQLServerString() throws ConvertException {
      DropStatement dropStatement = this.copyObjectValues();
      if (dropStatement.getTableObjectVector() != null) {
         Vector tableObjVector = dropStatement.getTableObjectVector();

         for(int i = 0; i < tableObjVector.size(); ++i) {
            TableObject tableObj = (TableObject)tableObjVector.get(i);
            if (this.tableOrSequence.equalsIgnoreCase("INDEX")) {
               tableObj.setTableType("INDEX");
               if (tableObj.getUser() != null && SwisSQLOptions.fromSybase) {
                  tableObj.setOwner(tableObj.getUser());
                  tableObj.setUser((String)null);
               }
            }

            tableObj.toMSSQLServer();
         }
      }

      dropStatement.setRestrictOrCascade((String)null);
      dropStatement.setIfExists((String)null);
      dropStatement.setConstraints((String)null);
      return dropStatement.toString();
   }

   public String toMySQLString() throws ConvertException {
      DropStatement dropStatement = this.copyObjectValues();
      if (dropStatement.getTableObjectVector() != null) {
         Vector tableObjVector = dropStatement.getTableObjectVector();

         for(int i = 0; i < tableObjVector.size(); ++i) {
            TableObject tableObj = (TableObject)tableObjVector.get(i);
            tableObj.toMySQL();
         }
      }

      dropStatement.setRestrictOrCascade((String)null);
      dropStatement.setConstraints((String)null);
      return dropStatement.toString();
   }

   public String toOracleString() throws ConvertException {
      DropStatement dropStatement = this.copyObjectValues();
      if (dropStatement.getTableObjectVector() != null) {
         Vector tableObjVector = dropStatement.getTableObjectVector();

         for(int i = 0; i < tableObjVector.size(); ++i) {
            TableObject tableObj = (TableObject)tableObjVector.get(i);
            tableObj.toOracle();
            String tableOrIndex = dropStatement.getTableOrSequence();
            if (tableOrIndex != null && tableOrIndex.equalsIgnoreCase("index")) {
               tableObj.setUser((String)null);
            }

            String tableName = tableObj.getTableName();
            if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
               tableName = tableName.substring(1, tableName.length() - 1);
               if (SwisSQLOptions.retainQuotedIdentifierForOracle || tableName.indexOf(" ") != -1) {
                  tableName = "\"" + tableName + "\"";
               }

               tableObj.setTableName(tableName);
            }
         }
      }

      dropStatement.setIfExists((String)null);
      return dropStatement.toString();
   }

   public String toPostgreSQLString() throws ConvertException {
      DropStatement dropStatement = this.copyObjectValues();
      if (dropStatement.getTableObjectVector() != null) {
         Vector tableObjVector = dropStatement.getTableObjectVector();

         for(int i = 0; i < tableObjVector.size(); ++i) {
            TableObject tableObj = (TableObject)tableObjVector.get(i);
            tableObj.toPostgreSQL();
         }
      }

      dropStatement.setRestrictOrCascade((String)null);
      dropStatement.setIfExists((String)null);
      dropStatement.setConstraints((String)null);
      return dropStatement.toString();
   }

   public String toBigQueryString() throws ConvertException {
      DropStatement dropStatement = this.copyObjectValues();
      if (dropStatement.getTableObjectVector() != null) {
         Vector tableObjVector = dropStatement.getTableObjectVector();

         for(int i = 0; i < tableObjVector.size(); ++i) {
            TableObject tableObj = (TableObject)tableObjVector.get(i);
            tableObj.toBigQuery();
         }
      }

      dropStatement.setRestrictOrCascade((String)null);
      dropStatement.setIfExists((String)null);
      dropStatement.setConstraints((String)null);
      return dropStatement.toString();
   }

   public String toSybaseString() throws ConvertException {
      DropStatement dropStatement = this.copyObjectValues();
      if (dropStatement.getTableObjectVector() != null) {
         Vector tableObjVector = dropStatement.getTableObjectVector();

         for(int i = 0; i < tableObjVector.size(); ++i) {
            TableObject tableObj = (TableObject)tableObjVector.get(i);
            tableObj.toSybase();
         }
      }

      dropStatement.setRestrictOrCascade((String)null);
      dropStatement.setIfExists((String)null);
      dropStatement.setConstraints((String)null);
      return dropStatement.toString();
   }

   public String toTimesTenString() throws ConvertException {
      DropStatement dropStatement = this.copyObjectValues();
      if (dropStatement.getTableObjectVector() != null) {
         Vector tableObjVector = dropStatement.getTableObjectVector();
         this.multipleQuery = "";

         for(int i = 0; i < tableObjVector.size(); ++i) {
            TableObject tableObj = (TableObject)tableObjVector.get(i);
            tableObj.toTimesTen();
            if (i > 0) {
               tableObjVector.remove(i);
               --i;
               this.multipleQuery = this.multipleQuery + "DROP " + this.tableOrSequence + " " + tableObj.toString().trim() + ";\n\n";
            }
         }
      }

      if (!this.multipleQuery.equalsIgnoreCase("")) {
         dropStatement.setMultipleQuery(this.multipleQuery);
      }

      dropStatement.setIfExists((String)null);
      dropStatement.setRestrictOrCascade((String)null);
      dropStatement.setConstraints((String)null);
      return dropStatement.toString();
   }

   public String toNetezzaString() throws ConvertException {
      DropStatement dropStatement = this.copyObjectValues();
      if (dropStatement.getTableObjectVector() != null) {
         Vector tableObjVector = dropStatement.getTableObjectVector();

         for(int i = 0; i < tableObjVector.size(); ++i) {
            TableObject tableObj = (TableObject)tableObjVector.get(i);
            tableObj.toNetezza();
         }
      }

      dropStatement.setIfExists((String)null);
      dropStatement.setConstraints((String)null);
      dropStatement.setRestrictOrCascade((String)null);
      return dropStatement.toString();
   }

   public String toSnowflakeString() throws ConvertException {
      DropStatement dropStatement = this.copyObjectValues();
      if (dropStatement.getTableObjectVector() != null) {
         Vector tableObjVector = dropStatement.getTableObjectVector();

         for(int i = 0; i < tableObjVector.size(); ++i) {
            TableObject tableObj = (TableObject)tableObjVector.get(i);
            tableObj.toSnowflake();
         }
      }

      dropStatement.setRestrictOrCascade((String)null);
      dropStatement.setIfExists((String)null);
      dropStatement.setConstraints((String)null);
      return dropStatement.toString();
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

   public DropStatement copyObjectValues() {
      DropStatement dropStmt = new DropStatement();
      dropStmt.setConstraints(this.constraints);
      dropStmt.setCommentClass(this.commentObject);
      dropStmt.setDrop(this.drop);
      dropStmt.setIfExists(this.ifExists);
      dropStmt.setRestrictOrCascade(this.restrictOrCascade);
      dropStmt.setTableOrSequence(this.tableOrSequence);
      dropStmt.setTableNameVector(this.tableObjectVector);
      dropStmt.setObjectContext(this.objectContext);
      dropStmt.setMaterializedView(this.materializedObject);
      return dropStmt;
   }

   public String removeIndent(String str) {
      str = str.replace('\n', ' ');
      str = str.replace('\t', ' ');
      return str;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.commentObject != null) {
         sb.append(this.commentObject.toString() + "\n");
      }

      if (this.drop != null) {
         sb.append(this.drop.toUpperCase());
      }

      if (this.materializedObject != null) {
         sb.append(" " + this.getMaterializedView() + " ");
      }

      if (this.tableOrSequence != null) {
         sb.append(" " + this.tableOrSequence.toUpperCase());
      }

      if (this.ifExists != null) {
         sb.append("  " + this.ifExists.toUpperCase());
      }

      if (this.tableObjectVector != null) {
         for(int i = 0; i < this.tableObjectVector.size(); ++i) {
            TableObject tableObject = (TableObject)this.tableObjectVector.get(i);
            tableObject.setObjectContext(this.objectContext);
            if (i == 0) {
               sb.append(" " + tableObject);
            } else {
               sb.append(",\n\t" + tableObject);
            }
         }
      }

      if (this.restrictOrCascade != null) {
         sb.append("\n\t" + this.restrictOrCascade.toUpperCase());
      }

      if (this.constraints != null) {
         sb.append(" " + this.constraints.toUpperCase());
      }

      if (this.multipleQuery != null) {
         StringBuffer sb1 = new StringBuffer();
         sb1.append(sb.toString().trim() + ";\n\n" + this.multipleQuery);
         return sb1.toString();
      } else {
         return sb.toString();
      }
   }

   public UserObjectContext getObjectContext() {
      return this.objectContext;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.objectContext = obj;
   }

   public String toVectorWiseString() throws ConvertException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public SwisSQLStatement toVectorWise() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toInformix() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toNetezza() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toTimesTen() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toOracle() throws ConvertException {
      return null;
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
      return null;
   }

   public SwisSQLStatement toMySQL() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toPostgreSQL() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toBigQuery() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toDB2() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSybase() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toMSSQLServer() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toTeradata() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toANSI() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toMsAccessJdbc() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toExcel() throws ConvertException {
      return null;
   }
}
