package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;

public class VariableColumn {
   private String variableName = null;
   private String operatorsInvolved = null;
   private String instanceDatatype;

   public void setVariableName(String varName) {
      this.variableName = varName;
   }

   public void setOperatorsInvolved(String operator) {
      this.operatorsInvolved = operator;
   }

   public void setInstanceDatatype(String instanceDatatype) {
      this.instanceDatatype = instanceDatatype;
   }

   public String getVariableName() {
      return this.variableName;
   }

   public String getOperatorsInvolved() {
      return this.operatorsInvolved;
   }

   public String getActualVariableName() {
      return this.variableName.substring(2, this.variableName.length() - 1);
   }

   public String getInstanceDatatype() {
      return this.instanceDatatype;
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public String toString() {
      return this.variableName;
   }
}
