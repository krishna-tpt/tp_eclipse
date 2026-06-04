package com.adventnet.swissqlapi.sql.statement.create;

public class GeometryClass implements Datatype {
   private String datatypeName;
   private String openBrace;
   private String closedBrace;
   private String size;
   private String schemaName;
   private String arrayStr;

   public void setDatatypeName(String datatypeName) {
      this.datatypeName = datatypeName;
   }

   public void setOpenBrace(String openBrace) {
      this.openBrace = openBrace;
   }

   public void setClosedBrace(String closedBrace) {
      this.closedBrace = closedBrace;
   }

   public void setSize(String size) {
      this.size = size;
   }

   public void setSchemaName(String schemaName) {
      this.schemaName = schemaName;
   }

   public String getDatatypeName() {
      return this.datatypeName;
   }

   public String getSize() {
      return this.size;
   }

   public String getOpenBrace() {
      return this.openBrace;
   }

   public String getClosedBrace() {
      return this.closedBrace;
   }

   public String getSchemaName() {
      return this.schemaName;
   }

   public void toInformixString() {
   }

   public void toDB2String() {
      if (this.datatypeName != null && this.datatypeName.equalsIgnoreCase("sdo_geometry")) {
         this.setDatatypeName("ST_GEOMETRY");
         this.setSchemaName("DB2GSE");
      }

   }

   public void toOracleString() {
   }

   public void toMSSQLServerString() {
   }

   public void toSybaseString() {
   }

   public void toPostgreSQLString() {
   }

   public void toBigQueryString() {
   }

   public void toMySQLString() {
   }

   public void toANSIString() {
   }

   public void toTimesTenString() {
   }

   public void toNetezzaString() {
   }

   public void toTeradataString() {
   }

   public void toSnowflakeString() {
   }

   public void toAthenaString() {
   }

   public void toSapHanaString() {
   }

   public void toSqliteString() {
   }

   public void toDenodoString() {
   }

   public void toExcelString() {
   }

   public void toMsAccessJdbcString() {
   }

   public Datatype copyObjectValues() {
      GeometryClass geometryClass = new GeometryClass();
      geometryClass.setClosedBrace(this.closedBrace);
      geometryClass.setDatatypeName(this.getDatatypeName());
      geometryClass.setOpenBrace(this.openBrace);
      geometryClass.setSize(this.getSize());
      geometryClass.setSchemaName(this.getSchemaName());
      return geometryClass;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.schemaName != null) {
         sb.append(this.schemaName + ".");
      }

      if (this.datatypeName != null) {
         sb.append(this.datatypeName + " ");
      }

      if (this.openBrace != null) {
         sb.append(this.openBrace);
      }

      if (this.size != null) {
         sb.append(this.size);
      }

      if (this.closedBrace != null) {
         sb.append(this.closedBrace);
      }

      if (this.arrayStr != null) {
         sb.append(this.arrayStr);
      }

      return sb.toString();
   }

   public void setArray(String arrayStr) {
   }

   public String getArray() {
      return this.arrayStr;
   }
}
