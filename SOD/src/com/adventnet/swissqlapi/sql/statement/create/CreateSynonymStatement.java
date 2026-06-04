package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.drop.DropStatement;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import java.util.Vector;

public class CreateSynonymStatement implements SwisSQLStatement {
   private String createOrReplace;
   private String createString;
   private String synonymString;
   private String publicString;
   private String synonymName;
   private String forString;
   private TableObject tableName;
   private String schemaName;
   private UserObjectContext context = null;
   private String comment;
   DropStatement dropSynonym;

   public void setCreateOrReplace(String createOrReplace) {
      this.createOrReplace = createOrReplace;
   }

   public void setCreate(String createString) {
      this.createString = createString;
   }

   public void setSynonym(String synonymString) {
      this.synonymString = synonymString;
   }

   public void setPublic(String publicString) {
      this.publicString = publicString;
   }

   public void setSynonymName(String synonymName) {
      this.synonymName = synonymName;
   }

   public void setFor(String forString) {
      this.forString = forString;
   }

   public void setTableName(TableObject tableName) {
      this.tableName = tableName;
   }

   public void setSchemaName(String schemaName) {
      this.schemaName = schemaName;
   }

   public void setDropSynonym(DropStatement dropSynonym) {
      this.dropSynonym = dropSynonym;
   }

   public String getCreate() {
      return this.createString;
   }

   public String getCreateOrReplace() {
      return this.createOrReplace;
   }

   public String getSynonym() {
      return this.synonymString;
   }

   public String getPublic() {
      return this.publicString;
   }

   public String getSynonymName() {
      return this.synonymName;
   }

   public String getFor() {
      return this.forString;
   }

   public TableObject getTableName(TableObject tableName) {
      return this.tableName;
   }

   public String getSchemaName() {
      return this.schemaName;
   }

   public DropStatement getDropSynonym() {
      return this.dropSynonym;
   }

   public CommentClass getCommentClass() {
      return null;
   }

   public UserObjectContext getObjectContext() {
      return null;
   }

   public String removeIndent(String str) {
      return str;
   }

   public void setCommentClass(CommentClass commentClass) {
   }

   public void setObjectContext(UserObjectContext userObjectContext) {
      this.context = userObjectContext;
   }

   public String toANSIString() throws ConvertException {
      CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
      if (this.createString != null) {
         createSynonymStatement.setCreate(this.createString);
      }

      if (this.createOrReplace != null) {
         createSynonymStatement.setCreateOrReplace(this.createOrReplace);
      }

      if (this.publicString != null) {
         createSynonymStatement.setPublic((String)null);
      }

      if (this.synonymString != null) {
         createSynonymStatement.setSynonym(this.synonymString);
      }

      if (this.synonymName != null) {
         createSynonymStatement.setSynonymName(this.synonymName);
      }

      if (this.forString != null) {
         createSynonymStatement.setFor(this.forString);
      }

      if (this.tableName != null) {
         createSynonymStatement.setTableName(this.tableName);
      }

      return createSynonymStatement.toString();
   }

   public String toDB2String() throws ConvertException {
      CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
      if (this.createString != null) {
         createSynonymStatement.setCreate(this.createString);
      }

      if (this.createOrReplace != null) {
         createSynonymStatement.setCreateOrReplace(this.createOrReplace);
      }

      if (this.publicString != null) {
         createSynonymStatement.setPublic((String)null);
      }

      if (this.synonymString != null) {
         createSynonymStatement.setSynonym(this.synonymString);
      }

      if (this.synonymName != null) {
         createSynonymStatement.setSynonymName(this.synonymName);
      }

      if (this.forString != null) {
         createSynonymStatement.setFor(this.forString);
      }

      if (this.tableName != null) {
         createSynonymStatement.setTableName(this.tableName);
      }

      return createSynonymStatement.toString();
   }

   public String toInformixString() throws ConvertException {
      CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
      if (this.createString != null) {
         createSynonymStatement.setCreate(this.createString);
      }

      if (this.createOrReplace != null) {
         createSynonymStatement.setCreateOrReplace(this.createOrReplace);
      }

      if (this.publicString != null) {
         createSynonymStatement.setPublic((String)null);
      }

      if (this.synonymString != null) {
         createSynonymStatement.setSynonym(this.synonymString);
      }

      if (this.synonymName != null) {
         createSynonymStatement.setSynonymName(this.synonymName);
      }

      if (this.forString != null) {
         createSynonymStatement.setFor(this.forString);
      }

      if (this.tableName != null) {
         createSynonymStatement.setTableName(this.tableName);
      }

      return createSynonymStatement.toString();
   }

   public String toMSSQLServerString() throws ConvertException {
      CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
      if (this.createString != null) {
         createSynonymStatement.setCreate("/* Synonym does not exists in SQL Server -- " + this.createString);
      }

      if (this.createOrReplace != null) {
         createSynonymStatement.setCreateOrReplace(this.createOrReplace);
      }

      if (this.publicString != null) {
         createSynonymStatement.setPublic((String)null);
      }

      if (this.synonymString != null) {
         createSynonymStatement.setSynonym(this.synonymString);
      }

      if (this.synonymName != null) {
         createSynonymStatement.setSynonymName(this.synonymName);
      }

      if (this.forString != null) {
         createSynonymStatement.setFor(this.forString);
      }

      if (this.tableName != null) {
         createSynonymStatement.setTableName(this.tableName);
      }

      createSynonymStatement.comment = " */";
      return createSynonymStatement.toString();
   }

   public String toMySQLString() throws ConvertException {
      CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
      if (this.createString != null) {
         createSynonymStatement.setCreate(this.createString);
      }

      if (this.createOrReplace != null) {
         createSynonymStatement.setCreateOrReplace(this.createOrReplace);
      }

      if (this.publicString != null) {
         createSynonymStatement.setPublic((String)null);
      }

      if (this.synonymString != null) {
         createSynonymStatement.setSynonym(this.synonymString);
      }

      if (this.synonymName != null) {
         createSynonymStatement.setSynonymName(this.synonymName);
      }

      if (this.forString != null) {
         createSynonymStatement.setFor(this.forString);
      }

      if (this.tableName != null) {
         createSynonymStatement.setTableName(this.tableName);
      }

      return createSynonymStatement.toString();
   }

   public String toOracleString() throws ConvertException {
      CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
      if (this.createString != null) {
         createSynonymStatement.setCreate(this.createString);
      }

      if (this.createOrReplace != null) {
         createSynonymStatement.setCreateOrReplace(this.createOrReplace);
      }

      if (this.publicString != null) {
         createSynonymStatement.setPublic(this.publicString);
      }

      if (this.synonymString != null) {
         createSynonymStatement.setSynonym(this.synonymString);
      }

      if (this.synonymName != null) {
         createSynonymStatement.setSynonymName(this.synonymName);
      }

      if (this.forString != null) {
         createSynonymStatement.setFor(this.forString);
      }

      if (this.tableName != null) {
         createSynonymStatement.setTableName(this.tableName);
      }

      return createSynonymStatement.toString();
   }

   public String toPostgreSQLString() throws ConvertException {
      CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
      if (this.createString != null) {
         createSynonymStatement.setCreate(this.createString);
      }

      if (this.createOrReplace != null) {
         createSynonymStatement.setCreateOrReplace(this.createOrReplace);
      }

      if (this.publicString != null) {
         createSynonymStatement.setPublic((String)null);
      }

      if (this.synonymString != null) {
         createSynonymStatement.setSynonym(this.synonymString);
      }

      if (this.synonymName != null) {
         createSynonymStatement.setSynonymName(this.synonymName);
      }

      if (this.forString != null) {
         createSynonymStatement.setFor(this.forString);
      }

      if (this.tableName != null) {
         createSynonymStatement.setTableName(this.tableName);
      }

      return createSynonymStatement.toString();
   }

   public String toBigQueryString() throws ConvertException {
      CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
      if (this.createString != null) {
         createSynonymStatement.setCreate(this.createString);
      }

      if (this.createOrReplace != null) {
         createSynonymStatement.setCreateOrReplace(this.createOrReplace);
      }

      if (this.publicString != null) {
         createSynonymStatement.setPublic((String)null);
      }

      if (this.synonymString != null) {
         createSynonymStatement.setSynonym(this.synonymString);
      }

      if (this.synonymName != null) {
         createSynonymStatement.setSynonymName(this.synonymName);
      }

      if (this.forString != null) {
         createSynonymStatement.setFor(this.forString);
      }

      if (this.tableName != null) {
         createSynonymStatement.setTableName(this.tableName);
      }

      return createSynonymStatement.toString();
   }

   public String toSybaseString() throws ConvertException {
      CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
      createSynonymStatement.setObjectContext(this.context);
      if (this.createString != null) {
         createSynonymStatement.setCreate(this.createString);
      }

      if (this.createOrReplace != null) {
         createSynonymStatement.setCreateOrReplace(this.createOrReplace);
      }

      if (this.publicString != null) {
         createSynonymStatement.setPublic((String)null);
      }

      if (this.synonymString != null) {
         createSynonymStatement.setSynonym(this.synonymString);
      }

      if (this.synonymName != null) {
         createSynonymStatement.setSynonymName(this.synonymName);
      }

      if (this.forString != null) {
         createSynonymStatement.setFor(this.forString);
      }

      if (this.tableName != null) {
         createSynonymStatement.setTableName(this.tableName);
      }

      return createSynonymStatement.toString();
   }

   public String toTimesTenString() throws ConvertException {
      throw new ConvertException("\nUnsupported SQL\n");
   }

   public String toNetezzaString() throws ConvertException {
      CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
      if (this.createString != null) {
         createSynonymStatement.setCreate(this.createString);
      }

      if (this.publicString != null) {
         createSynonymStatement.setPublic((String)null);
      }

      if (this.synonymString != null) {
         createSynonymStatement.setSynonym(this.synonymString);
      }

      if (this.synonymName != null) {
         createSynonymStatement.setSynonymName(this.synonymName);
      }

      if (this.forString != null) {
         createSynonymStatement.setFor(this.forString);
      }

      if (this.tableName != null) {
         createSynonymStatement.setTableName(this.tableName);
      }

      if (this.createOrReplace != null) {
         createSynonymStatement.setCreateOrReplace((String)null);
         this.dropSynonym = new DropStatement();
         this.dropSynonym.setTableOrSequence("SYNONYM");
         Vector tableNameVector = new Vector();
         tableNameVector.add(this.tableName);
         this.dropSynonym.setTableNameVector(tableNameVector);
         this.dropSynonym.setDrop("DROP");
         createSynonymStatement.setDropSynonym(this.dropSynonym);
      }

      return createSynonymStatement.toString();
   }

   public String toSnowflakeString() throws ConvertException {
      CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
      if (this.createString != null) {
         createSynonymStatement.setCreate(this.createString);
      }

      if (this.createOrReplace != null) {
         createSynonymStatement.setCreateOrReplace(this.createOrReplace);
      }

      if (this.publicString != null) {
         createSynonymStatement.setPublic((String)null);
      }

      if (this.synonymString != null) {
         createSynonymStatement.setSynonym(this.synonymString);
      }

      if (this.synonymName != null) {
         createSynonymStatement.setSynonymName(this.synonymName);
      }

      if (this.forString != null) {
         createSynonymStatement.setFor(this.forString);
      }

      if (this.tableName != null) {
         createSynonymStatement.setTableName(this.tableName);
      }

      return createSynonymStatement.toString();
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

   public String toTeradataString() throws ConvertException {
      CreateSynonymStatement createSynonymStatement = new CreateSynonymStatement();
      if (this.createString != null) {
         createSynonymStatement.setCreate(this.createString);
      }

      if (this.createOrReplace != null) {
         createSynonymStatement.setCreateOrReplace(this.createOrReplace);
      }

      if (this.publicString != null) {
         createSynonymStatement.setPublic((String)null);
      }

      if (this.synonymString != null) {
         createSynonymStatement.setSynonym(this.synonymString);
      }

      if (this.synonymName != null) {
         createSynonymStatement.setSynonymName(this.synonymName);
      }

      if (this.forString != null) {
         createSynonymStatement.setFor(this.forString);
      }

      if (this.tableName != null) {
         createSynonymStatement.setTableName(this.tableName);
      }

      return createSynonymStatement.toString();
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      String indentString = "\n";
      if (this.dropSynonym != null) {
         sb.append(indentString + this.dropSynonym.toString() + ";" + indentString);
      }

      if (this.createString != null) {
         sb.append(indentString + this.createString.toUpperCase());
      }

      if (this.createOrReplace != null) {
         sb.append(indentString + this.createOrReplace.toUpperCase());
      }

      if (this.publicString != null) {
         sb.append(" " + this.publicString.toUpperCase());
      }

      if (this.synonymString != null) {
         sb.append(" " + this.synonymString.toUpperCase());
      }

      if (this.synonymName != null) {
         if (this.context != null) {
            String temp = this.context.getEquivalent(this.synonymName).toString();
            sb.append(" " + temp);
         } else {
            sb.append(" " + this.synonymName);
         }
      }

      if (this.forString != null) {
         sb.append(" " + this.forString.toUpperCase());
      }

      if (this.tableName != null) {
         this.tableName.setObjectContext(this.context);
         sb.append(" " + this.tableName.toString());
      }

      if (this.comment != null) {
         sb.append(" " + this.comment);
      }

      return sb.toString();
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
