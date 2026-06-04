package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class UpdateLimitClause {
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

   public void toOracleRowNum(UpdateQueryStatement uqs) {
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
         uqs.getWhereExpression().addOperator(" AND ");
         uqs.getWhereExpression().addWhereItem(newWhereItem);
      } else {
         WhereExpression newWhereExpression = new WhereExpression();
         newWhereExpression.addWhereItem(newWhereItem);
         uqs.setWhereClause(newWhereExpression);
      }

   }

   public void toMySQLSelect() throws ConvertException {
   }

   public void toOracleSelect() throws ConvertException {
   }

   public void toMSSQLServerSelect() throws ConvertException {
   }

   public void toSybaseSelect() throws ConvertException {
   }

   public void toPostgreSQLSelect() throws ConvertException {
   }

   public void toDB2Select() throws ConvertException {
   }

   public void toInformixSelect() throws ConvertException {
   }

   public String toString() {
      StringBuffer stringbuffer = new StringBuffer();
      stringbuffer.append(" " + this.limit.toUpperCase() + " ");
      stringbuffer.append(" " + this.dimension + " ");
      return stringbuffer.toString();
   }
}
