package com.adventnet.swissqlapi.util;

import java.util.Vector;

public class Condition {
   private String operator;
   private Vector expressions;

   public Vector getExpressions() {
      return this.expressions;
   }

   public void setExpressions(Vector rightExpression) {
      this.expressions = rightExpression;
   }

   public String getOperator() {
      return this.operator;
   }

   public void setOperator(String operator) {
      this.operator = operator;
   }

   public boolean equals(Object obj) {
      if (obj instanceof Condition) {
         Condition cond = (Condition)obj;
         if (this.operator.equals(cond.getOperator())) {
            return this.compareVector(this.expressions, cond.getExpressions());
         }
      }

      return false;
   }

   private boolean compareVector(Vector vec1, Vector vec2) {
      if (vec1 == null && vec2 == null) {
         return true;
      } else if (vec1 != null && vec2 != null && vec1.size() == vec2.size()) {
         for(int i = 0; i < vec1.size(); ++i) {
            boolean result = true;
            if (!result) {
               return false;
            }

            boolean var10000;
            if (result && vec2.contains(vec1.get(i))) {
               var10000 = true;
            } else {
               var10000 = false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
