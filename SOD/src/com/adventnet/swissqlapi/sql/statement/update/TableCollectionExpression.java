package com.adventnet.swissqlapi.sql.statement.update;

import java.util.ArrayList;

public class TableCollectionExpression {
   private String table = new String();
   private String optionalPlus = new String();
   public ArrayList collectionExpList = new ArrayList();

   public void setTable(String s) {
      this.table = s;
   }

   public void setOptionalPlus(String s) {
      this.optionalPlus = s;
   }

   public void setCollectionExpList(ArrayList al) {
      this.collectionExpList = al;
   }

   public String getTable() {
      return this.table;
   }

   public String getOptionalPlus() {
      return this.optionalPlus;
   }

   public ArrayList getCollectionExpList() {
      return this.collectionExpList;
   }

   public String toString() {
      StringBuffer stringbuffer = new StringBuffer();
      stringbuffer.append(this.table);
      int i = 0;

      for(int size = this.collectionExpList.size(); i < size; ++i) {
         stringbuffer.append(this.collectionExpList.get(i).toString());
      }

      if (this.optionalPlus != null) {
         stringbuffer.append(this.optionalPlus);
      }

      return stringbuffer.toString();
   }
}
