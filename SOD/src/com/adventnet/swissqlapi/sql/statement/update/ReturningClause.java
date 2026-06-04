package com.adventnet.swissqlapi.sql.statement.update;

import java.util.ArrayList;

public class ReturningClause {
   public ArrayList lhsExpressionList = new ArrayList();
   public String into = new String();
   public ArrayList rhsVariableList = new ArrayList();
   public String returning = new String();

   public void setReturning(String s) {
      this.returning = s;
   }

   public void setInto(String s) {
      this.into = s;
   }

   public void setExpressionList(ArrayList al) {
      this.lhsExpressionList = al;
   }

   public void setrhsVariableList(ArrayList al) {
      this.rhsVariableList = al;
   }

   public String getReturning() {
      return this.returning;
   }

   public String getInto() {
      return this.into;
   }

   public ArrayList getExpressionList() {
      return this.lhsExpressionList;
   }

   public ArrayList getrhsVariableList() {
      return this.rhsVariableList;
   }

   public String toString() {
      StringBuffer stringbuffer = new StringBuffer();
      stringbuffer.append(this.returning.toUpperCase() + " ");
      int i = 0;

      int size;
      for(size = this.lhsExpressionList.size(); i < size; ++i) {
         stringbuffer.append(this.lhsExpressionList.get(i).toString() + " ");
      }

      stringbuffer.append(this.into.toUpperCase());
      i = 0;

      for(size = this.rhsVariableList.size(); i < size; ++i) {
         stringbuffer.append(" " + this.rhsVariableList.get(i).toString() + " ");
      }

      return stringbuffer.toString();
   }
}
