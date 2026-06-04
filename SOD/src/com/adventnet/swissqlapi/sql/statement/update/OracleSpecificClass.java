package com.adventnet.swissqlapi.sql.statement.update;

import java.util.ArrayList;

public class OracleSpecificClass {
   public ArrayList expressionList = new ArrayList();
   public String into = new String();
   public ArrayList variableList = new ArrayList();
   public String returning = new String();

   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append(this.returning);
      sb.append(this.expressionList);
      return sb.toString();
   }
}
