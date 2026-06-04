package com.adventnet.swissqlapi.sql.statement.delete;

import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class DeleteLimitClause {
   private String limit;
   private String dimension;

   public void setLimit(String s) {
      this.limit = s;
   }

   public void setDimension(String s) {
      this.dimension = s;
   }

   public String getLimit() {
      return this.limit;
   }

   public String getDimension() {
      return this.dimension;
   }

   public void toOracleRowNum(DeleteQueryStatement uqs) {
      WhereExpression we = new WhereExpression();
      WhereItem newWhereItem = new WhereItem();
      WhereColumn lWhereColumn = new WhereColumn();
      Vector lcolExp = new Vector();
      lcolExp.addElement("ROWNUM");
      lWhereColumn.setColumnExpression(lcolExp);
      newWhereItem.setLeftWhereExp(lWhereColumn);
      newWhereItem.setOperator("<");
      WhereColumn rWhereColumn = new WhereColumn();
      Vector rcolExp = new Vector();
      rcolExp.addElement(Integer.parseInt(this.dimension) + 1 + "");
      rWhereColumn.setColumnExpression(rcolExp);
      newWhereItem.setRightWhereExp(rWhereColumn);
      if (uqs.getWhereExpression() != null) {
         uqs.getWhereExpression().addOperator("AND");
         uqs.getWhereExpression().addWhereItem(newWhereItem);
      } else {
         we.addWhereItem(newWhereItem);
         uqs.setWhereClause(we);
      }

   }

   public String toString() {
      StringBuffer stringbuffer = new StringBuffer();
      stringbuffer.append(this.limit.toUpperCase());
      stringbuffer.append(" ");
      stringbuffer.append(this.dimension);
      return stringbuffer.toString();
   }
}
