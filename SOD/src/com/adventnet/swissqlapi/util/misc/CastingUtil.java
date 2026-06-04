package com.adventnet.swissqlapi.util.misc;

import com.adventnet.swissqlapi.SwisSQLAPI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CastingUtil {
   public static boolean functionParameter = false;

   public static String getDB2DataTypeCastedString(String sourceDataType, String targetDataType, String expr) {
      if (targetDataType == null) {
         return expr;
      } else if (expr.equalsIgnoreCase("sysdate")) {
         return expr;
      } else if (expr.trim().toLowerCase().startsWith("coalesce(")) {
         return expr;
      } else if ((targetDataType.trim().toLowerCase().startsWith("varchar") || targetDataType.trim().toLowerCase().startsWith("char")) && (expr.trim().toLowerCase().startsWith("char") || expr.trim().toLowerCase().startsWith("varchar") || expr.trim().startsWith("'") && expr.trim().endsWith("'") || expr.indexOf("||") != -1)) {
         return expr;
      } else if (targetDataType.trim().toLowerCase().startsWith("double") && expr.trim().toLowerCase().startsWith("double")) {
         return expr;
      } else if (targetDataType.trim().toLowerCase().startsWith("integer") && expr.trim().toLowerCase().startsWith("integer")) {
         return expr;
      } else if (targetDataType.trim().toLowerCase().startsWith("timestamp") && expr.trim().toLowerCase().startsWith("timestamp")) {
         return expr;
      } else if (targetDataType.trim().toLowerCase().startsWith("decimal") && expr.trim().toLowerCase().startsWith("decimal")) {
         return expr;
      } else {
         if (sourceDataType == null) {
            expr = expr.trim();
            if (expr.startsWith("'") && expr.endsWith("'")) {
               sourceDataType = "varchar";
            } else {
               try {
                  Integer.parseInt(expr);
                  sourceDataType = "integer";
               } catch (NumberFormatException var6) {
                  try {
                     Float.parseFloat(expr);
                     sourceDataType = "decimal";
                  } catch (NumberFormatException var5) {
                  }
               }
            }

            if (sourceDataType == null) {
               targetDataType = targetDataType.trim();
               if (!targetDataType.equalsIgnoreCase("float") && !targetDataType.trim().equalsIgnoreCase("double")) {
                  if (targetDataType.equalsIgnoreCase("decimal")) {
                     expr = "DECIMAL(" + expr + ")";
                  } else if (targetDataType.equalsIgnoreCase("integer")) {
                     expr = "INTEGER(" + expr + ")";
                  } else if (targetDataType.equalsIgnoreCase("timestamp")) {
                     if (expr.trim().equalsIgnoreCase("current timestamp")) {
                        return expr;
                     }

                     expr = "TIMESTAMP(" + expr + ")";
                  } else if (targetDataType.toLowerCase().startsWith("varchar")) {
                     expr = "VARCHAR(RTRIM(CHAR(" + expr + ")))";
                  } else if (targetDataType.equalsIgnoreCase("char")) {
                     expr = "RTRIM(CHAR(" + expr + "))";
                  } else {
                     expr = targetDataType.toUpperCase() + "(CHAR(" + expr + "))";
                  }
               } else {
                  expr = "DOUBLE(" + expr + ")";
               }

               return expr;
            }
         }

         sourceDataType = sourceDataType.trim();
         targetDataType = targetDataType.trim();
         if (!sourceDataType.toLowerCase().equals(targetDataType.toLowerCase())) {
            if (!targetDataType.toLowerCase().startsWith("float") && !targetDataType.toLowerCase().startsWith("double")) {
               if (targetDataType.toLowerCase().startsWith("decimal")) {
                  if (sourceDataType.toLowerCase().equals("varchar") || sourceDataType.toLowerCase().equals("char")) {
                     expr = "DECIMAL(" + expr + " ,31,18)";
                  }
               } else if (targetDataType.toLowerCase().equals("integer")) {
                  if (!sourceDataType.toLowerCase().equals("varchar") && !sourceDataType.toLowerCase().equals("char")) {
                     if (functionParameter && (sourceDataType.toLowerCase().equals("float") || sourceDataType.toLowerCase().equals("double"))) {
                        expr = "INTEGER(" + expr + ")";
                     }
                  } else {
                     expr = "INTEGER(" + expr + ")";
                  }
               } else if (!targetDataType.toLowerCase().equals("bigint") && !targetDataType.toLowerCase().equals("smallint")) {
                  if (targetDataType.toLowerCase().startsWith("varchar")) {
                     if (!sourceDataType.toLowerCase().equals("float") && !sourceDataType.toLowerCase().equals("double") && !sourceDataType.toLowerCase().equals("decimal") && !sourceDataType.toLowerCase().equals("smallint") && !sourceDataType.toLowerCase().equals("integer") && !sourceDataType.toLowerCase().equals("bigint") && !sourceDataType.toLowerCase().equals("int") && !sourceDataType.toLowerCase().equals("timestamp") && !sourceDataType.toLowerCase().equals("date")) {
                        if (!sourceDataType.toLowerCase().equals("char") && !sourceDataType.toLowerCase().startsWith("varchar")) {
                           expr = "VARCHAR(" + expr + ")";
                        }
                     } else {
                        expr = "VARCHAR(RTRIM(CHAR(" + expr + ")))";
                     }
                  } else if (targetDataType.toLowerCase().startsWith("char")) {
                     if (!sourceDataType.toLowerCase().equals("varchar")) {
                        expr = "RTRIM(CHAR(" + expr + "))";
                     }
                  } else if (targetDataType.toLowerCase().startsWith("timestamp")) {
                     if (expr.trim().equalsIgnoreCase("current timestamp")) {
                        return expr;
                     }

                     if (sourceDataType.toLowerCase().equals("varchar") || sourceDataType.toLowerCase().equals("char")) {
                        expr = "TIMESTAMP(" + expr + ")";
                     }
                  } else {
                     expr = targetDataType.toUpperCase() + "(" + expr + ")";
                  }
               } else if (sourceDataType.toLowerCase().equals("varchar") || sourceDataType.toLowerCase().equals("char")) {
                  expr = targetDataType.toUpperCase() + "(" + expr + ")";
               }
            } else {
               try {
                  Integer.parseInt(expr);
                  return expr;
               } catch (NumberFormatException var8) {
                  try {
                     Float.parseFloat(expr);
                     return expr;
                  } catch (NumberFormatException var7) {
                     if (sourceDataType.toLowerCase().equals("varchar") || sourceDataType.toLowerCase().equals("char")) {
                        expr = "DOUBLE(" + expr + ")";
                     }
                  }
               }
            }
         }

         return expr;
      }
   }

   public static String getDB2DataTypeCastedParameter(String sourceDataType, String targetDataType, String expr) {
      functionParameter = true;
      expr = getDB2DataTypeCastedString(sourceDataType, targetDataType, expr);
      functionParameter = false;
      return expr;
   }

   public static String getDataType(String dataType) {
      if (dataType == null) {
         return null;
      } else {
         return dataType.indexOf("(") != -1 ? dataType.substring(0, dataType.indexOf("(")) : dataType;
      }
   }

   public static String getReturnDataType(String builtInFunctionName) {
      return SwisSQLAPI.builtInFunctionDetails != null ? SwisSQLAPI.builtInFunctionDetails.getReturnDataType(builtInFunctionName) : null;
   }

   public static String getParameterDataType(String builtInFunctionName, int paramNum) {
      return SwisSQLAPI.builtInFunctionDetails != null ? SwisSQLAPI.builtInFunctionDetails.getParameterDataType(builtInFunctionName, paramNum) : null;
   }

   public static Object getValueIgnoreCase(Map ht, String variableName) {
      Object obj = ht.get(variableName);
      if (obj == null) {
         obj = ht.get(variableName.toLowerCase());
      }

      if (obj == null) {
         obj = ht.get(variableName.toUpperCase());
      }

      if (obj == null) {
         Set keys = ht.keySet();
         Iterator it = keys.iterator();

         while(it.hasNext()) {
            Object keyObj = it.next();
            if (keyObj.toString().equalsIgnoreCase(variableName)) {
               return ht.get(keyObj);
            }
         }
      }

      return obj;
   }

   public static boolean ContainsIgnoreCase(List list, String variableName) {
      if (!list.contains(variableName) && !list.contains(variableName.toLowerCase()) && !list.contains(variableName.toUpperCase())) {
         for(int i = 0; i < list.size(); ++i) {
            Object obj = list.get(i);
            if (obj instanceof String) {
               String listValue = (String)obj;
               if (listValue.equalsIgnoreCase(variableName)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public static String getKeyForValueIgnoreCase(Map hm, String value) {
      Set keys = hm.keySet();
      Iterator it = keys.iterator();

      Object key;
      do {
         if (!it.hasNext()) {
            return null;
         }

         key = it.next();
      } while(!hm.get(key).toString().equalsIgnoreCase(value));

      return key.toString();
   }
}
