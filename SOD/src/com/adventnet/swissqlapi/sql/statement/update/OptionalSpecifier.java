package com.adventnet.swissqlapi.sql.statement.update;

public class OptionalSpecifier {
   private String low_priority = null;
   private String delayed = null;
   private String ignore = null;
   private String into = null;
   private String from = null;
   private String only = null;

   public void setLowPriority(String s) {
      this.low_priority = s;
   }

   public String getLowPriority() {
      return this.low_priority;
   }

   public void setDelayed(String s) {
      this.delayed = s;
   }

   public String getDelayed() {
      return this.delayed;
   }

   public String getFrom() {
      return this.from;
   }

   public void setFrom(String s) {
      this.from = s;
   }

   public void setIgnore(String s) {
      this.ignore = s;
   }

   public String getIgnore() {
      return this.ignore;
   }

   public void setInto(String s) {
      this.into = s;
   }

   public String getInto() {
      return this.into;
   }

   public void setOnly(String s) {
      this.only = s;
   }

   public String getOnly() {
      return this.only;
   }

   public void toPostgreSQL() {
      this.low_priority = null;
      this.delayed = null;
      this.ignore = null;
      this.into = null;
   }

   public void toBigQuery() {
      this.low_priority = null;
      this.delayed = null;
      this.ignore = null;
      this.into = null;
   }

   public void toMySQL() {
      this.only = null;
   }

   public void toSnowflake() {
      this.low_priority = null;
      this.delayed = null;
      this.ignore = null;
      this.into = null;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.low_priority != null) {
         sb.append(this.low_priority.toUpperCase());
         sb.append(" ");
      }

      if (this.delayed != null) {
         sb.append(this.delayed.toUpperCase());
         sb.append(" ");
      }

      if (this.ignore != null) {
         sb.append(this.ignore.toUpperCase());
         sb.append(" ");
      }

      if (this.into != null) {
         sb.append(this.into.toUpperCase());
         sb.append(" ");
      }

      if (this.from != null) {
         sb.append(this.from.toUpperCase());
         sb.append(" ");
      }

      if (this.only != null) {
         sb.append(this.only.toUpperCase());
         sb.append(" ");
      }

      return sb.toString();
   }
}
