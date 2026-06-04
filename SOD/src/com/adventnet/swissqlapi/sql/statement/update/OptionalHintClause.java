package com.adventnet.swissqlapi.sql.statement.update;

import java.util.ArrayList;

public class OptionalHintClause {
   private String option;
   private ArrayList queryHintList = new ArrayList();

   public void setOption(String s) {
      this.option = s;
   }

   public void setQueryHintList(ArrayList list) {
      this.queryHintList = list;
   }

   public String toString() {
      StringBuffer stringbuffer = new StringBuffer();
      stringbuffer.append(this.option);

      for(int i = 0; i < this.queryHintList.size(); ++i) {
         stringbuffer.append(" " + this.queryHintList.get(i));
      }

      return stringbuffer.toString();
   }
}
