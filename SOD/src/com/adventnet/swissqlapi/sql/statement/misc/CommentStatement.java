package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class CommentStatement implements SwisSQLStatement {
   private UserObjectContext objectContext;
   private CommentClass commentClass;
   private String comment = null;
   private String on = null;
   private String commentType = null;
   private CommentTableObject commentObject = null;
   private String is = null;
   private String commentString = null;
   private String extendedProperty = null;

   public String toOracleString() throws ConvertException {
      CommentStatement to_cs = new CommentStatement();
      if (this.objectContext != null) {
         to_cs.setObjectContext(this.objectContext);
      }

      if (this.commentClass != null) {
         to_cs.setCommentClass(this.commentClass);
      }

      if (this.comment != null) {
         to_cs.setComment(this.comment);
      }

      if (this.on != null) {
         to_cs.setOn(this.on);
      }

      if (this.commentType != null) {
         to_cs.setCommentType(this.commentType);
      }

      if (this.commentObject != null) {
         CommentTableObject to_cto = this.commentObject.toOracleCommentObject();
         to_cs.setCommentObject(to_cto);
      }

      if (this.is != null) {
         to_cs.setIs(this.is);
      }

      if (this.commentString != null) {
         to_cs.setCommentString(this.commentString);
      }

      return to_cs.toString();
   }

   public String toMSSQLServerString() throws ConvertException {
      CommentStatement to_cs = new CommentStatement();
      if (this.comment != null) {
         to_cs.setComment("EXEC SP_ADDEXTENDEDPROPERTY");
      }

      if (this.commentObject != null) {
         CommentTableObject commentObj = this.commentObject.toMSSQLServerCommentObject(this.commentType);
         if (this.commentString != null) {
            commentObj.addLevelTypeAndName("@VALUE", this.commentString);
            this.commentString = null;
         }

         to_cs.setCommentObject(commentObj);
      }

      return to_cs.toString();
   }

   public String toSybaseString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toDB2String() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toPostgreSQLString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toBigQueryString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toMySQLString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toANSIString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toInformixString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toTimesTenString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toNetezzaString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toTeradataString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toSnowflakeString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toAthenaString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toSapHanaString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toSqliteString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toExcelString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toMsAccessJdbcString() throws ConvertException {
      throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
   }

   public String toString() {
      StringBuffer commentStr = new StringBuffer("");
      if (this.getComment() != null) {
         commentStr.append(this.getComment());
      }

      if (this.getOn() != null) {
         commentStr.append(" ");
         commentStr.append(this.getOn());
      }

      if (this.getCommentType() != null) {
         commentStr.append(" ");
         commentStr.append(this.getCommentType());
      }

      if (this.getCommentObject() != null) {
         String tabObject = this.getCommentObject().toString();
         commentStr.append(" ");
         commentStr.append(tabObject.trim());
      }

      if (this.getIs() != null) {
         commentStr.append(" ");
         commentStr.append(this.getIs());
      }

      if (this.getCommentString() != null) {
         commentStr.append(" ");
         commentStr.append(this.getCommentString().trim());
      }

      return commentStr.toString();
   }

   public String removeIndent(String formattedSqlString) {
      return formattedSqlString;
   }

   public UserObjectContext getObjectContext() {
      return this.objectContext;
   }

   public void setObjectContext(UserObjectContext objectContext) {
      this.objectContext = objectContext;
   }

   public CommentClass getCommentClass() {
      return this.commentClass;
   }

   public void setCommentClass(CommentClass commentClass) {
      this.commentClass = commentClass;
   }

   public String getComment() {
      return this.comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public String getOn() {
      return this.on;
   }

   public void setOn(String on) {
      this.on = on;
   }

   public String getCommentType() {
      return this.commentType;
   }

   public void setCommentType(String commentType) {
      this.commentType = commentType;
   }

   public CommentTableObject getCommentObject() {
      return this.commentObject;
   }

   public void setCommentObject(CommentTableObject commentObject) {
      this.commentObject = commentObject;
   }

   public String getIs() {
      return this.is;
   }

   public void setIs(String is) {
      this.is = is;
   }

   public String getCommentString() {
      return this.commentString;
   }

   public void setCommentString(String commentString) {
      this.commentString = commentString;
   }

   public String getExtendedProperty() {
      return this.extendedProperty;
   }

   public void setExtendedProperty(String extendedProperty) {
      this.extendedProperty = extendedProperty;
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
