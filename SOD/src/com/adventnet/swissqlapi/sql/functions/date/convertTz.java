package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class convertTz extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      boolean canUseUDFFunction = from_sqs != null && !isPostgreLiveDbs && (from_sqs.getBooleanValues("use.udf.functions.for.date.time") || from_sqs.getBooleanValues("can.use.offset.computation.for.convert.tz"));
      boolean isVertica = from_sqs != null && from_sqs.isVerticaDb();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0) {
               this.handleStringLiteralForDateTime(from_sqs, i_count, true);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String arg1;
      String arg2;
      if (from_sqs != null && from_sqs.isAmazonRedShift()) {
         this.functionName.setColumnName("CONVERT_TIMEZONE");
         Vector args = new Vector();
         if (arguments.size() == 2) {
            arg1 = arguments.get(1).toString().replaceAll("'", "");
            if (arg1.contains("+")) {
               arg1 = arg1.replaceAll("\\+", "-");
            } else {
               arg1 = arg1.replaceAll("-", "+");
            }

            args.addElement("'UTC+00:00'");
            args.addElement("'UTC" + arg1 + "'");
            args.addElement(arguments.get(0));
         } else {
            arg1 = arguments.get(1).toString().replaceAll("'", "");
            arg2 = arguments.get(2).toString().replaceAll("'", "");
            if (arg1.contains("+")) {
               arg1 = arg1.replaceAll("\\+", "-");
            } else {
               arg1 = arg1.replaceAll("-", "+");
            }

            if (arg2.contains("+")) {
               arg2 = arg2.replaceAll("\\+", "-");
            } else {
               arg2 = arg2.replaceAll("-", "+");
            }

            args.addElement("'UTC" + arg1 + "'");
            args.addElement("'UTC" + arg2 + "'");
            args.addElement(arguments.get(0));
         }

         this.setFunctionArguments(args);
      } else {
         String fromTZValue = isVertica ? arguments.get(1).toString() : this.processLiteralTZValue((SelectColumn)this.functionArguments.get(1), arguments.get(1).toString());
         arg1 = isVertica ? arguments.get(2).toString() : this.processLiteralTZValue((SelectColumn)this.functionArguments.get(2), arguments.get(2).toString());
         arg2 = arguments.get(0).toString().trim().equalsIgnoreCase("(CURRENT_TIME)") ? "cast(" + arguments.get(0).toString() + " as time)" : "cast(" + arguments.get(0) + " as timestamp)";
         String qry = isVertica ? "((" + arg2 + " at time zone interval " + fromTZValue + ") at time zone interval " + arg1 + ")" : "((" + arg2 + " at time zone " + fromTZValue + ") at time zone " + arg1 + ")";
         boolean var11 = false;

         try {
            if (canUseUDFFunction && arguments.size() == 3 && arguments.get(1) != null && arguments.get(2) != null) {
               String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
               String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
               int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
               int currentTZMin = currentTZ.length == 2 ? Integer.parseInt(currentTZ[1]) : 0;
               int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
               int toTZMin = toTZ.length == 2 ? Integer.parseInt(toTZ[1]) : 0;
               int toTZOverallMins = toTZHour * 60 + toTZMin;
               int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
               if (currentTZ[0].contains("-")) {
                  currentTZHour *= -1;
               }

               if (toTZ[0].contains("-")) {
                  toTZHour *= -1;
               }

               int finalMins;
               if (currentTZHour < 0) {
                  if (toTZHour < 0) {
                     finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                  } else {
                     finalMins = toTZOverallMins - currentTZOverallMins * -1;
                  }
               } else if (toTZHour < 0) {
                  finalMins = toTZOverallMins * -1 - currentTZOverallMins;
               } else {
                  finalMins = toTZOverallMins - currentTZOverallMins;
               }

               qry = " (" + arguments.get(0) + " + ( INTERVAL  '1'  MINUTE * " + finalMins + ") )";
            }
         } catch (Exception var20) {
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("convert_tz")) {
         int finalMins = 0;

         try {
            if (arguments.size() == 3 && arguments.get(1) != null && arguments.get(2) != null && arguments.get(1).toString().contains(":") && arguments.get(2).toString().contains(":")) {
               String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
               String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
               int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
               int currentTZMin = Integer.parseInt(currentTZ[1]);
               int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
               int toTZMin = Integer.parseInt(toTZ[1]);
               int toTZOverallMins = toTZHour * 60 + toTZMin;
               int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
               if (currentTZ[0].contains("-")) {
                  currentTZHour *= -1;
               }

               if (toTZ[0].contains("-")) {
                  toTZHour *= -1;
               }

               if (currentTZHour < 0) {
                  if (toTZHour < 0) {
                     finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                  } else {
                     finalMins = toTZOverallMins - currentTZOverallMins * -1;
                  }
               } else if (toTZHour < 0) {
                  finalMins = toTZOverallMins * -1 - currentTZOverallMins;
               } else {
                  finalMins = toTZOverallMins - currentTZOverallMins;
               }
            }
         } catch (Exception var14) {
         }

         qry = "DATETIME_ADD(Cast(" + arguments.get(0).toString() + " AS DATETIME), INTERVAL " + finalMins + " MINUTE)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("convert_tz")) {
         int finalMins = 0;

         try {
            if (arguments.size() == 3 && arguments.get(1) != null && arguments.get(2) != null && arguments.get(1).toString().contains(":") && arguments.get(2).toString().contains(":")) {
               String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
               String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
               int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
               int currentTZMin = Integer.parseInt(currentTZ[1]);
               int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
               int toTZMin = Integer.parseInt(toTZ[1]);
               int toTZOverallMins = toTZHour * 60 + toTZMin;
               int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
               if (currentTZ[0].contains("-")) {
                  currentTZHour *= -1;
               }

               if (toTZ[0].contains("-")) {
                  toTZHour *= -1;
               }

               if (currentTZHour < 0) {
                  if (toTZHour < 0) {
                     finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                  } else {
                     finalMins = toTZOverallMins - currentTZOverallMins * -1;
                  }
               } else if (toTZHour < 0) {
                  finalMins = toTZOverallMins * -1 - currentTZOverallMins;
               } else {
                  finalMins = toTZOverallMins - currentTZOverallMins;
               }
            }
         } catch (Exception var14) {
         }

         qry = "DATE_ADD('MINUTE'," + finalMins + ",CAST(" + StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString()) + " AS TIMESTAMP))";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("convert_tz")) {
         int finalMins = 0;

         try {
            if (arguments.size() == 3 && arguments.get(1) != null && arguments.get(2) != null && arguments.get(1).toString().contains(":") && arguments.get(2).toString().contains(":")) {
               String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
               String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
               int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
               int currentTZMin = Integer.parseInt(currentTZ[1]);
               int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
               int toTZMin = Integer.parseInt(toTZ[1]);
               int toTZOverallMins = toTZHour * 60 + toTZMin;
               int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
               if (currentTZ[0].contains("-")) {
                  currentTZHour *= -1;
               }

               if (toTZ[0].contains("-")) {
                  toTZHour *= -1;
               }

               if (currentTZHour < 0) {
                  if (toTZHour < 0) {
                     finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                  } else {
                     finalMins = toTZOverallMins - currentTZOverallMins * -1;
                  }
               } else if (toTZHour < 0) {
                  finalMins = toTZOverallMins * -1 - currentTZOverallMins;
               } else {
                  finalMins = toTZOverallMins - currentTZOverallMins;
               }
            }
         } catch (Exception var14) {
         }

         qry = "ADD_SECONDS(TO_TIMESTAMP(" + arguments.get(0).toString() + ")," + finalMins * 60 + ")";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("convert_tz")) {
         int finalMins = 0;

         try {
            if (arguments.size() == 3 && arguments.get(1) != null && arguments.get(2) != null && arguments.get(1).toString().contains(":") && arguments.get(2).toString().contains(":")) {
               String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
               String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
               int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
               int currentTZMin = Integer.parseInt(currentTZ[1]);
               int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
               int toTZMin = Integer.parseInt(toTZ[1]);
               int toTZOverallMins = toTZHour * 60 + toTZMin;
               int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
               if (currentTZ[0].contains("-")) {
                  currentTZHour *= -1;
               }

               if (toTZ[0].contains("-")) {
                  toTZHour *= -1;
               }

               if (currentTZHour < 0) {
                  if (toTZHour < 0) {
                     finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                  } else {
                     finalMins = toTZOverallMins - currentTZOverallMins * -1;
                  }
               } else if (toTZHour < 0) {
                  finalMins = toTZOverallMins * -1 - currentTZOverallMins;
               } else {
                  finalMins = toTZOverallMins - currentTZOverallMins;
               }
            }
         } catch (Exception var14) {
         }

         qry = "DATETIME(" + arguments.get(0).toString() + ",'" + finalMins * 60 + " seconds')";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("convert_tz")) {
         int finalMins = 0;

         try {
            if (arguments.size() != 3 || arguments.get(1) == null || arguments.get(2) == null || !arguments.get(1).toString().contains(":") || !arguments.get(2).toString().contains(":")) {
               throw new ConvertException("\nThe function argument is not supported");
            }

            String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
            String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
            int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
            int currentTZMin = Integer.parseInt(currentTZ[1]);
            int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
            int toTZMin = Integer.parseInt(toTZ[1]);
            int toTZOverallMins = toTZHour * 60 + toTZMin;
            int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
            if (currentTZ[0].contains("-")) {
               currentTZHour *= -1;
            }

            if (toTZ[0].contains("-")) {
               toTZHour *= -1;
            }

            if (currentTZHour < 0) {
               if (toTZHour < 0) {
                  finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
               } else {
                  finalMins = toTZOverallMins - currentTZOverallMins * -1;
               }
            } else if (toTZHour < 0) {
               finalMins = toTZOverallMins * -1 - currentTZOverallMins;
            } else {
               finalMins = toTZOverallMins - currentTZOverallMins;
            }
         } catch (Exception var14) {
         }

         qry = "(CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) + (" + finalMins + ") MINUTES)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      String qry = "";
      if ((from_sqs != null && from_sqs.isMongoDb() || from_sqs.isHyperSql()) && this.functionName.getColumnName().equalsIgnoreCase("convert_tz")) {
         int finalMins = 0;
         boolean firstArg = arguments.get(1) != null && ((SelectColumn)arguments.get(1)).getColumnExpression().size() == 1 && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0) != null && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof String;
         boolean secondArg = arguments.get(2) != null && ((SelectColumn)arguments.get(2)).getColumnExpression().size() == 1 && ((SelectColumn)arguments.get(2)).getColumnExpression().get(0) != null && ((SelectColumn)arguments.get(2)).getColumnExpression().get(0) instanceof String;
         boolean isStrigInstance = arguments.size() == 3 && firstArg && secondArg;

         try {
            if (isStrigInstance && arguments.get(1).toString().contains(":") && arguments.get(2).toString().contains(":")) {
               String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
               String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
               int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
               int currentTZMin = Integer.parseInt(currentTZ[1]);
               int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
               int toTZMin = Integer.parseInt(toTZ[1]);
               int toTZOverallMins = toTZHour * 60 + toTZMin;
               int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
               if (currentTZ[0].contains("-")) {
                  currentTZHour *= -1;
               }

               if (toTZ[0].contains("-")) {
                  toTZHour *= -1;
               }

               if (currentTZHour < 0) {
                  if (toTZHour < 0) {
                     finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                  } else {
                     finalMins = toTZOverallMins - currentTZOverallMins * -1;
                  }
               } else if (toTZHour < 0) {
                  finalMins = toTZOverallMins * -1 - currentTZOverallMins;
               } else {
                  finalMins = toTZOverallMins - currentTZOverallMins;
               }
            }
         } catch (Exception var17) {
            throw new ConvertException("\nThe function argument is not supported");
         }

         qry = "DATE_ADD(" + arguments.get(0).toString() + ",INTERVAL " + finalMins + " MINUTE)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("convert_tz")) {
         int finalMins = 0;

         try {
            if (arguments.size() == 3 && arguments.get(1) != null && arguments.get(2) != null && arguments.get(1).toString().contains(":") && arguments.get(2).toString().contains(":")) {
               String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
               String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
               int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
               int currentTZMin = Integer.parseInt(currentTZ[1]);
               int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
               int toTZMin = Integer.parseInt(toTZ[1]);
               int toTZOverallMins = toTZHour * 60 + toTZMin;
               int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
               if (currentTZ[0].contains("-")) {
                  currentTZHour *= -1;
               }

               if (toTZ[0].contains("-")) {
                  toTZHour *= -1;
               }

               if (currentTZHour < 0) {
                  if (toTZHour < 0) {
                     finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                  } else {
                     finalMins = toTZOverallMins - currentTZOverallMins * -1;
                  }
               } else if (toTZHour < 0) {
                  finalMins = toTZOverallMins * -1 - currentTZOverallMins;
               } else {
                  finalMins = toTZOverallMins - currentTZOverallMins;
               }
            }
         } catch (Exception var14) {
         }

         qry = "(" + StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString()) + " + " + finalMins + " UNITS MINUTE)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public String processLiteralTZValue(SelectColumn scArg, String convArg) {
      Vector vcTZValue = scArg.getColumnExpression();
      String tzValue;
      if (vcTZValue.size() == 1 && vcTZValue.get(0) instanceof String) {
         tzValue = (String)vcTZValue.get(0);
         if (tzValue.charAt(1) == '+') {
            tzValue = tzValue.replace('+', '-');
         } else if (tzValue.charAt(1) == '-') {
            tzValue = tzValue.replace('-', '+');
         }
      } else {
         tzValue = "case when left(" + convArg + ",1)='+' then replace(" + convArg + ",'+','-') when left(" + convArg + ",1)='-' then replace(" + convArg + ",'-','+') else " + convArg + " end";
      }

      return tzValue;
   }
}
