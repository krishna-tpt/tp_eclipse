package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.sql.UserObjectContext;

public class WithClause {
   private String with = new String();
   private String readOnly = new String();
   private String checkOption = new String();
   private String constraint = new String();
   private String constraintName = new String();
   private UserObjectContext context = null;

   public void setWith(String s) {
      this.with = s;
   }

   public void setReadOnly(String s) {
      this.readOnly = s;
   }

   public void setCheckOption(String s) {
      this.checkOption = s;
   }

   public void setConstraint(String s) {
      this.constraint = s;
   }

   public void setConstraintName(String s) {
      this.constraintName = s;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   public String getWith() {
      return this.with;
   }

   public String getReadOnly() {
      return this.readOnly;
   }

   public String getCheckOption() {
      return this.checkOption;
   }

   public String getConstraint() {
      return this.constraint;
   }

   public String getConstraintName() {
      return this.constraintName;
   }

   public String toString() {
      StringBuffer stringbuffer = new StringBuffer();
      stringbuffer.append(" ");
      if (this.with != null) {
         stringbuffer.append(this.with.toUpperCase());
         stringbuffer.append(" ");
      }

      if (this.readOnly != null) {
         stringbuffer.append(this.readOnly.toUpperCase());
         stringbuffer.append(" ");
      }

      if (this.checkOption != null) {
         stringbuffer.append(this.checkOption.toUpperCase());
         stringbuffer.append(" ");
      }

      if (this.constraint != null) {
         stringbuffer.append(this.constraint.toUpperCase());
         stringbuffer.append(" ");
      }

      if (this.constraintName != null) {
         if (this.context != null) {
            stringbuffer.append(this.context.getEquivalent(this.constraintName));
         } else {
            stringbuffer.append(this.constraintName);
         }

         stringbuffer.append(" ");
      }

      return stringbuffer.toString();
   }
}
