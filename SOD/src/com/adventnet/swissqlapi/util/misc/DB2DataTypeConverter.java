package com.adventnet.swissqlapi.util.misc;

public class DB2DataTypeConverter {
   public static String convertPLSQLTypeToDB2Type(String datatype) {
      datatype = datatype.toLowerCase();
      if (datatype.startsWith("integer")) {
         datatype = "INTEGER";
      } else if (datatype.startsWith("number(38)") || datatype.startsWith("natural") || datatype.startsWith("positive")) {
         datatype = "DECIMAL(31,0)";
      }

      if (datatype.startsWith("numeric")) {
         if (datatype.indexOf(",") != -1) {
            datatype = datatype.replaceFirst("numeric", "DECIMAL");
         } else {
            datatype = "DECIMAL(31,0)";
         }
      } else {
         String stringBeforeOpenBrace;
         int numericValueOfArgument;
         if (!datatype.startsWith("number") && !datatype.startsWith("float")) {
            if (datatype.startsWith("raw")) {
               stringBeforeOpenBrace = datatype.substring(datatype.indexOf("(") + 1, datatype.indexOf(")"));

               try {
                  numericValueOfArgument = Integer.parseInt(stringBeforeOpenBrace);
                  if (numericValueOfArgument <= 254) {
                     datatype = datatype.replaceFirst("raw", "CHAR");
                  } else if (numericValueOfArgument > 254 && numericValueOfArgument <= 32672) {
                     datatype = datatype.replaceFirst("raw", "VARCHAR");
                  } else if (numericValueOfArgument > 32672) {
                     datatype = datatype.replaceFirst("raw", "BLOB");
                  }
               } catch (NumberFormatException var11) {
                  System.out.println("EXCEPTION IN VARIABLEDECLARATION IN DATATYPE : " + datatype);
                  var11.printStackTrace();
                  datatype = "BLOB";
               }
            } else if (datatype.startsWith("decimal")) {
               if (datatype.indexOf(",") != -1) {
                  datatype = datatype.replaceFirst("decimal", "DECIMAL");
               } else {
                  datatype = "DECIMAL(31,0)";
               }
            } else {
               String argumentValue;
               int numericValueOfArgument;
               if (datatype.startsWith("varchar")) {
                  if (datatype.indexOf("(") != -1) {
                     stringBeforeOpenBrace = datatype.substring(0, datatype.indexOf("("));
                     argumentValue = datatype.substring(datatype.indexOf("(") + 1, datatype.indexOf(")"));

                     try {
                        numericValueOfArgument = Integer.parseInt(argumentValue);
                        if (numericValueOfArgument < 32672) {
                           datatype = datatype.replaceFirst(stringBeforeOpenBrace, "VARCHAR");
                        } else {
                           datatype = "VARCHAR(32672)";
                        }
                     } catch (Exception var9) {
                        datatype = "VARCHAR(3999)";
                     }
                  } else if (datatype.equalsIgnoreCase("VARCHAR2")) {
                     datatype = "VARCHAR(32672)";
                  }
               } else if (datatype.startsWith("string")) {
                  stringBeforeOpenBrace = datatype.substring(0, datatype.indexOf("("));
                  argumentValue = datatype.substring(datatype.indexOf("(") + 1, datatype.indexOf(")"));

                  try {
                     numericValueOfArgument = Integer.parseInt(argumentValue);
                     if (numericValueOfArgument < 4000) {
                        datatype = datatype.replaceFirst(stringBeforeOpenBrace, "VARCHAR");
                     } else {
                        datatype = "VARCHAR(4000)";
                     }
                  } catch (Exception var8) {
                     datatype = "VARCHAR(3999)";
                  }
               } else if (datatype.startsWith("char")) {
                  stringBeforeOpenBrace = datatype.substring(0, datatype.indexOf("("));
                  argumentValue = datatype.substring(datatype.indexOf("(") + 1, datatype.indexOf(")"));

                  try {
                     numericValueOfArgument = Integer.parseInt(argumentValue);
                     if (numericValueOfArgument <= 254) {
                        datatype = datatype.replaceFirst(stringBeforeOpenBrace, "CHAR");
                     } else {
                        datatype = "VARCHAR(4000)";
                     }
                  } catch (Exception var7) {
                     datatype = "VARCHAR(3999)";
                  }
               } else if (!datatype.startsWith("national") && !datatype.startsWith("nvarchar") && !datatype.startsWith("nchar")) {
                  if (!datatype.startsWith("bfile") && !datatype.startsWith("longraw") && !datatype.startsWith("blob")) {
                     if (datatype.equalsIgnoreCase("DATE")) {
                        datatype = "TIMESTAMP";
                     }
                  } else {
                     stringBeforeOpenBrace = datatype.substring(0, datatype.indexOf("("));
                     datatype = datatype.replaceFirst(stringBeforeOpenBrace, "BLOB");
                  }
               } else {
                  stringBeforeOpenBrace = datatype.substring(0, datatype.indexOf("("));
                  datatype = datatype.replaceFirst(stringBeforeOpenBrace, "VARCHAR");
               }
            }
         } else if (datatype.indexOf(",") != -1) {
            datatype = datatype.replaceFirst("number", "DECIMAL");
            stringBeforeOpenBrace = datatype.substring(datatype.indexOf("(") + 1, datatype.indexOf(","));
            numericValueOfArgument = Integer.parseInt(stringBeforeOpenBrace);
            if (numericValueOfArgument > 31) {
               String datatypeStr = datatype.substring(0, datatype.indexOf("("));
               String tempStr = datatype.substring(datatype.indexOf("(") + 1, datatype.length());
               String precision = tempStr.substring(tempStr.indexOf(",") + 1, tempStr.indexOf(")"));
               int pre = Integer.parseInt(precision);
               if (pre > 31) {
                  precision = "31";
               }

               datatype = datatypeStr + "(" + 31 + "," + precision + ")";
            }
         } else if (datatype.equals("float")) {
            datatype = "FLOAT";
         } else if (datatype.trim().equalsIgnoreCase("number")) {
            datatype = "DECIMAL(32,0)";
         } else {
            stringBeforeOpenBrace = datatype.substring(datatype.indexOf("(") + 1, datatype.indexOf(")"));

            try {
               numericValueOfArgument = Integer.parseInt(stringBeforeOpenBrace);
               if (numericValueOfArgument < 5) {
                  datatype = "SMALLINT";
               } else if (numericValueOfArgument < 10) {
                  datatype = "INTEGER";
               } else if (numericValueOfArgument < 19) {
                  datatype = "BIGINT";
               } else if (numericValueOfArgument < 32) {
                  datatype = "DECIMAL(" + numericValueOfArgument + ")";
               } else if (numericValueOfArgument >= 32) {
                  datatype = "DECIMAL(32,0)";
               }
            } catch (NumberFormatException var10) {
               System.out.println("EXCEPTION IN VARIABLEDECLARATION IN DATATYPE : " + datatype);
               var10.printStackTrace();
               datatype = "FLOAT";
            }
         }
      }

      return datatype;
   }
}
