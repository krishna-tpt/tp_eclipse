package com.adventnet.swissqlapi.sql.statement.update;

public class UpdateClause {
   private String update = null;
   private OptionalSpecifier optionalSpecifier = null;

   public void setUpdate(String s) {
      this.update = s;
   }

   public String getUpdate() {
      return this.update;
   }

   public void setOptionalSpecifier(OptionalSpecifier os) {
      this.optionalSpecifier = os;
   }

   public OptionalSpecifier getOptionalSpecifier() {
      return this.optionalSpecifier;
   }

   private void toGeneric() {
      this.optionalSpecifier = null;
   }

   public void toOracle() {
      this.toGeneric();
   }

   public void toDB2() {
      this.toGeneric();
   }

   public void toInformix() {
      this.toGeneric();
   }

   public void toSQLServer() {
      this.toGeneric();
   }

   public void toSybase() {
      this.toGeneric();
   }

   public void toMySQL() {
   }

   public void toPostgreSQL() {
      this.toGeneric();
   }

   public void toBigQuery() {
      this.toGeneric();
   }

   public void toANSISQL() {
      if (this.optionalSpecifier != null) {
         this.optionalSpecifier = null;
      }

   }

   public void toTimesTen() {
      this.toGeneric();
   }

   public void toNetezza() {
      this.toGeneric();
   }

   public void toTeradata() {
      this.toGeneric();
   }

   public void toSnowflake() {
      this.toGeneric();
   }

   public String toString() {
      StringBuffer stringbuffer = new StringBuffer();
      if (this.update != null) {
         stringbuffer.append(this.update.toUpperCase());
         stringbuffer.append(" ");
      }

      if (this.optionalSpecifier != null) {
         stringbuffer.append(this.optionalSpecifier.toString());
         stringbuffer.append(" ");
      }

      return stringbuffer.toString();
   }
}
