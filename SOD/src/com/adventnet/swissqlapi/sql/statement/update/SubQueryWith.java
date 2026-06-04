package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;

public class SubQueryWith {
   private SelectQueryStatement subQuery = new SelectQueryStatement();
   private WithClause withClause = new WithClause();

   public void setSubQuery(SelectQueryStatement s) {
      this.subQuery = s;
   }

   public void setWithClause(WithClause wc) {
      this.withClause = wc;
   }

   public void toMySQL() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toMySQL();
      }

   }

   public void toOracle() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toOracle();
      }

   }

   public void toMSSQLServer() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toMSSQLServer();
      }

   }

   public void toSybase() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toSybase();
      }

   }

   public void toPostgreSQL() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toPostgreSQL();
      }

   }

   public void toDB2() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toDB2();
      }

   }

   public void toSnowflake() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toSnowflake();
      }

   }

   public void toInformix() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toInformix();
      }

   }

   public void toANSISQL() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toANSI();
      }

   }

   public void toTeradata() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toTeradata();
      }

   }

   public void toNetezza() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toNetezza();
      }

   }

   public void toBigQuery() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toBigQuery();
      }

   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.subQuery != null) {
         sb.append(this.subQuery.toString());
      }

      if (this.withClause != null) {
         sb.append(this.withClause.toString());
      }

      return sb.toString();
   }
}
