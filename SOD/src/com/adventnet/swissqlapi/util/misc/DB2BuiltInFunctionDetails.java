package com.adventnet.swissqlapi.util.misc;

import java.util.ArrayList;
import java.util.HashMap;

public class DB2BuiltInFunctionDetails implements BuiltInFunctionDetails {
   private HashMap returnTypeHash = new HashMap();
   private HashMap parameterTypeHash = new HashMap();

   public DB2BuiltInFunctionDetails() {
      this.populateDetails();
   }

   public String getReturnDataType(String builtInFunctionName) {
      return (String)this.returnTypeHash.get(builtInFunctionName.trim().toLowerCase());
   }

   public String getParameterDataType(String functionName, int paramNum) {
      ArrayList aList = (ArrayList)this.parameterTypeHash.get(functionName.trim().toLowerCase());
      return aList != null && paramNum < aList.size() ? (String)aList.get(paramNum) : null;
   }

   private void populateDetails() {
      this.populateReturnType();
      this.populateParameterTypes();
   }

   private void populateReturnType() {
      this.returnTypeHash.put("oracle_substr", "varchar");
      this.returnTypeHash.put("oracle_to_char", "varchar");
      this.returnTypeHash.put("double", "double");
      this.returnTypeHash.put("oracle_add_months", "timestamp");
      this.returnTypeHash.put("oracle_lastday", "timestamp");
      this.returnTypeHash.put("oracle_to_date", "timestamp");
      this.returnTypeHash.put("ltrim", "varchar");
      this.returnTypeHash.put("rtrim", "varchar");
      this.returnTypeHash.put("oracle_new_time", "timestamp");
   }

   private void populateParameterTypes() {
      String[] param1 = new String[]{"varchar", "integer", "integer"};
      this.parameterTypeHash.put("oracle_substr", this.addAndReturnList(param1));
      String[] param2 = new String[]{"timestamp", "varchar"};
      this.parameterTypeHash.put("oracle_to_char", this.addAndReturnList(param2));
      String[] param4 = new String[]{"timestamp", "integer"};
      this.parameterTypeHash.put("oracle_add_months", this.addAndReturnList(param4));
      String[] param5 = new String[]{"timestamp"};
      this.parameterTypeHash.put("oracle_lastday", this.addAndReturnList(param5));
      String[] param6 = new String[]{"varchar", "varchar"};
      this.parameterTypeHash.put("oracle_to_date", this.addAndReturnList(param6));
      String[] param7 = new String[]{"varchar"};
      this.parameterTypeHash.put("ltrim", this.addAndReturnList(param7));
      String[] param8 = new String[]{"varchar"};
      this.parameterTypeHash.put("rtrim", this.addAndReturnList(param8));
      String[] param9 = new String[]{"double"};
      this.parameterTypeHash.put("abs", this.addAndReturnList(param9));
      String[] param10 = new String[]{"timestamp", "varchar", "varchar"};
      this.parameterTypeHash.put("oracle_new_time", this.addAndReturnList(param10));
   }

   private ArrayList addAndReturnList(String[] arr) {
      if (arr == null) {
         return null;
      } else {
         ArrayList aList = new ArrayList();

         for(int i = 0; i < arr.length; ++i) {
            aList.add(arr[i]);
         }

         return aList;
      }
   }
}
