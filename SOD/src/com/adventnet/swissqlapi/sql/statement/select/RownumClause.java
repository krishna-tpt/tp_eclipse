package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;

public class RownumClause {
   private String rownumClause;
   private String operator;
   private Object rownumValue;

   public void setRownumClause(String s_rnc) {
      this.rownumClause = s_rnc;
   }

   public void setOperator(String s_op) {
      this.operator = s_op;
   }

   public void setRownumValue(Object o_rnv) {
      this.rownumValue = o_rnv;
   }

   public String getRownumClause() {
      return this.rownumClause;
   }

   public String getOperator() {
      return this.operator;
   }

   public Object getRownumValue() {
      return this.rownumValue;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append(this.rownumClause);
      sb.append(" " + this.operator.toUpperCase());
      sb.append(" " + this.rownumValue.toString());
      return sb.toString();
   }

   public RownumClause toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      RownumClause rowNumClConv = new RownumClause();
      if (this.rownumClause != null) {
         rowNumClConv.setRownumClause(this.rownumClause);
      }

      if (this.operator != null) {
         rowNumClConv.setOperator(this.operator);
      }

      if (this.rownumValue != null) {
         if (this.rownumValue instanceof SelectQueryStatement) {
            SelectQueryStatement sqs = (SelectQueryStatement)this.rownumValue;
            sqs.setPropAndHandlerFromSQS(from_sqs);
            rowNumClConv.setRownumValue(sqs.toReplaceTblCol());
         } else if (this.rownumValue instanceof SelectColumn) {
            rowNumClConv.setRownumValue(((SelectColumn)this.rownumValue).toReplaceTblCol(to_sqs, from_sqs));
         } else if (this.rownumValue instanceof String) {
            rowNumClConv.setRownumValue(this.rownumValue);
         }
      }

      return rowNumClConv;
   }
}
