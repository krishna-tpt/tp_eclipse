package com.adventnet.swissqlapi.sql.statement.update;

public class WhereCurrentClause {
   private String whereClause;
   private String currentClause;
   private String ofClause;
   private String cursorId;

   public void setWhereClause(String wc) {
      this.whereClause = wc;
   }

   public void setCurrentClause(String cc) {
      this.currentClause = cc;
   }

   public void setOfClause(String oc) {
      this.ofClause = oc;
   }

   public void setCursorId(String cid) {
      this.cursorId = cid;
   }

   public String getCursorId() {
      return this.cursorId;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append(" " + this.whereClause + " ");
      sb.append(this.currentClause + " ");
      sb.append(this.ofClause + " ");
      sb.append(this.cursorId + " ");
      return sb.toString();
   }
}
