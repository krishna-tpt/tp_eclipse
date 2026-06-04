package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class FromTZ extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      int j;
      Object obj;
      String objStr;
      SelectColumn convAtTimeZoneRegion;
      if (arguments.size() > 1) {
         convAtTimeZoneRegion = (SelectColumn)arguments.get(1);

         for(j = 0; j < convAtTimeZoneRegion.getColumnExpression().size(); ++j) {
            obj = convAtTimeZoneRegion.getColumnExpression().get(j);
            if (obj instanceof String) {
               objStr = obj.toString().trim();
               if (objStr.startsWith("'")) {
                  objStr = objStr.substring(1, objStr.length() - 1).trim();
               }

               if (objStr.startsWith(":")) {
                  objStr = objStr.substring(1);
               }

               convAtTimeZoneRegion.getColumnExpression().setElementAt("'" + objStr + "'", j);
            }
         }
      }

      if (this.atTimeZoneRegion != null) {
         convAtTimeZoneRegion = this.atTimeZoneRegion.toTeradataSelect(to_sqs, from_sqs);

         for(j = 0; j < convAtTimeZoneRegion.getColumnExpression().size(); ++j) {
            obj = convAtTimeZoneRegion.getColumnExpression().get(j);
            if (obj instanceof String) {
               objStr = obj.toString().trim();
               if (objStr.startsWith("'")) {
                  objStr = objStr.substring(1, objStr.length() - 1).trim();
               }

               if (objStr.startsWith(":")) {
                  objStr = objStr.substring(1);
               }

               convAtTimeZoneRegion.getColumnExpression().setElementAt("'" + objStr + "'", j);
            }
         }

         arguments.add(convAtTimeZoneRegion);
         this.atTimeZoneRegion = null;
      } else if (arguments.size() == 2 && arguments.get(1) instanceof SelectColumn) {
         arguments.add(arguments.get(1));
      }

      this.setFunctionArguments(arguments);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments;
      int i_count;
      if (this.functionName.getColumnName().equalsIgnoreCase("from_unixtime")) {
         this.functionName.setColumnName("FROM_UNIXTIME");
         arguments = new Vector();

         for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
      } else {
         StringBuffer arguments;
         if (this.functionName.getColumnName().equalsIgnoreCase("to_days")) {
            arguments = new StringBuffer();

            for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  if (i_count == 0) {
                     this.handleStringLiteralForDateTime(from_sqs, i_count, false);
                  }

                  arguments.append(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
               } else {
                  arguments.append(this.functionArguments.elementAt(i_count));
               }
            }

            this.functionName.setColumnName("date_part('day',(cast(" + arguments + " as timestamp)+interval '1'day *364)-timestamp('0001-01-01'))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("from_days")) {
            arguments = new StringBuffer();

            for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  arguments.append(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
               } else {
                  arguments.append(this.functionArguments.elementAt(i_count));
               }
            }

            this.functionName.setColumnName(" (timestamp('01-01-0001 00:00:00')+interval '1' day *(" + arguments + "-364))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("dayname")) {
            this.functionName.setColumnName("DATE_FORMAT");
            arguments = new Vector();

            for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  if (i_count == 0) {
                     this.handleStringLiteralForDateTime(from_sqs, i_count, true);
                  }

                  arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i_count));
               }
            }

            arguments.addElement("'%W'");
            this.setFunctionArguments(arguments);
         }
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      boolean canUseUDFFunction = from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.date.time");
      StringBuffer arguments;
      int i_count;
      if (this.functionName.getColumnName().equalsIgnoreCase("from_days")) {
         arguments = new StringBuffer();

         for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.append(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               arguments.append(this.functionArguments.elementAt(i_count));
            }
         }

         this.functionName.setColumnName("(TO_TIMESTAMP(0) + interval '1' day * (ROUND(" + arguments.toString() + ")-719528))");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("to_days")) {
         arguments = new StringBuffer();

         for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               if (i_count == 0) {
                  this.handleStringLiteralForDateTime(from_sqs, i_count, canUseUDFFunction);
               }

               arguments.append(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               arguments.append(this.functionArguments.elementAt(i_count));
            }
         }

         this.functionName.setColumnName("((" + extract.applyDatePartOrExtractWrapper(from_sqs, "EPOCH", "DATE(" + arguments.toString() + ")") + "/86400)::int+719528)");
         if (canUseUDFFunction) {
            this.functionName.setColumnName("TO_DAYS(" + arguments.toString() + ")");
         }

         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("FROM_UNIXTIME")) {
         Vector arguments = new Vector();

         for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String columnName = arguments.get(0).toString();
         columnName = columnName.replace("'", "");
         if (from_sqs != null && from_sqs.isAmazonRedShift()) {
            double seconds = 0.0D;

            try {
               seconds = Double.parseDouble(columnName);
            } catch (Exception var10) {
               seconds = 0.0D;
            }

            if (seconds < 0.0D) {
               columnName = "NULL";
            } else if (seconds > 0.0D) {
               columnName = "(timestamp '1970-01-01 00:00:00' + interval '1' second * " + seconds + ")";
            } else {
               columnName = "(timestamp '1970-01-01 00:00:00' + interval '1' second * " + arguments.get(0).toString() + ")";
            }

            if (this.functionArguments.size() != 2) {
               this.functionName.setColumnName(columnName);
            } else {
               this.functionName.setColumnName("TO_CHAR(" + columnName + "," + this.getPgSQLDateFormatForMySQLWildCard(arguments.get(1).toString()) + ")");
            }

            this.setOpenBracesForFunctionNameRequired(false);
            this.setFunctionArguments(new Vector());
         } else if (from_sqs != null && from_sqs.isVerticaDb()) {
            if (this.functionArguments.size() < 2) {
               this.functionName.setColumnName("TO_TIMESTAMP(" + arguments.get(0).toString() + ")+interval '5 hour'+interval '30 minutes'");
               this.setOpenBracesForFunctionNameRequired(false);
               this.setFunctionArguments(new Vector());
            } else {
               this.functionName.setColumnName("TO_CHAR(TO_TIMESTAMP(" + arguments.get(0).toString() + ")+interval '5 hour'+interval '30 minutes'," + this.getPgSQLDateFormatForMySQLWildCard(arguments.get(1).toString()) + ")");
               this.setOpenBracesForFunctionNameRequired(false);
               this.setFunctionArguments(new Vector());
            }
         } else if (from_sqs != null && from_sqs.isPgsqlLive()) {
            if (this.functionArguments.size() < 2) {
               this.functionName.setColumnName("TO_TIMESTAMP");
               this.setFunctionArguments(arguments);
            } else {
               this.functionName.setColumnName("TO_CHAR(TO_TIMESTAMP(" + arguments.get(0).toString() + ")," + this.getPgSQLDateFormatForMySQLWildCard(arguments.get(1).toString()) + ")");
               this.setOpenBracesForFunctionNameRequired(false);
               this.setFunctionArguments(new Vector());
            }
         } else {
            this.functionName.setColumnName("TO_TIMESTAMP");

            try {
               Double.parseDouble(columnName);
            } catch (Exception var11) {
               if (from_sqs != null && from_sqs.getBooleanValues("use.udf.functions.for.numeric")) {
                  columnName = "TOINTEGER_UDF(" + arguments.get(0).toString() + ")";
               } else {
                  columnName = "(" + columnName + ")::integer";
               }
            }

            this.functionName.setColumnName("TO_TIMESTAMP(" + columnName + ")::TIMESTAMP");
            if (this.functionArguments.size() == 2) {
               this.functionName.setColumnName("TO_CHAR(TO_TIMESTAMP(" + columnName + ")::TIMESTAMP," + this.getPgSQLDateFormatForMySQLWildCard(arguments.get(1).toString()) + ")");
            }

            this.setOpenBracesForFunctionNameRequired(false);
            this.setFunctionArguments(new Vector());
         }
      }

   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("FROM_UNIXTIME")) {
         Vector arguments = new Vector();

         for(int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toBigQuerySelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         long seconds = 0L;
         String columnName = "timestamp '1970-01-01 00:00:00'";

         try {
            seconds = Long.parseLong(arguments.get(0).toString().replace("'", ""));
         } catch (Exception var8) {
            seconds = 0L;
         }

         if (seconds < 0L) {
            columnName = "NULL";
         } else if (seconds > 0L) {
            columnName = "TIMESTAMP_SECONDS(" + seconds + ")";
         }

         if (this.functionArguments.size() != 2) {
            this.functionName.setColumnName(columnName);
            this.setOpenBracesForFunctionNameRequired(false);
            this.setFunctionArguments(new Vector());
            return;
         }

         arguments.set(0, columnName);
         if (this.functionArguments.size() == 2) {
            String arg1 = arguments.get(1).toString();
            arg1 = arg1.replace("%s", "%S");
            arg1 = arg1.replace("%f", "%E*S");
            arg1 = arg1.replace("%M", "%B");
            arg1 = arg1.replace("%W", "%A");
            arg1 = arg1.replace("%h", "%I");
            arg1 = arg1.replace("%i", "%M");
            arguments.set(1, arg1);
            this.functionName.setColumnName("FORMAT_TIMESTAMP(" + arguments.get(1).toString() + ",CAST(" + arguments.get(0).toString() + " AS TIMESTAMP))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.setFunctionArguments(new Vector());
         }
      }

   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("FROM_UNIXTIME")) {
         this.functionName.setColumnName("TO_TIMESTAMP");
         Vector arguments = new Vector();

         for(int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSnowflakeSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         this.setFunctionArguments(arguments);
         long seconds = 0L;
         String columnName = "timestamp '1970-01-01 00:00:00'";

         try {
            seconds = Long.parseLong(arguments.get(0).toString().replaceAll("'", ""));
         } catch (Exception var8) {
            seconds = 0L;
         }

         if (seconds < 0L) {
            columnName = "NULL";
         } else if (seconds > 0L) {
            columnName = "DATEADD(s,CAST( " + seconds + " AS BIGINT),timestamp '1970-01-01 00:00:00')";
         }

         if (this.functionArguments.size() != 2) {
            this.functionName.setColumnName(columnName);
            this.setOpenBracesForFunctionNameRequired(false);
            this.setFunctionArguments(new Vector());
         }

         arguments.set(0, columnName);
         if (arguments.size() > 1) {
            String arg1 = arguments.get(1).toString();
            arg1 = arg1.replaceAll("%a", "Dy");
            arg1 = arg1.replaceAll("%b", "Mon");
            arg1 = arg1.replaceAll("%c", "MM");
            arg1 = arg1.replaceAll("%D", "DD");
            arg1 = arg1.replaceAll("%d", "DD");
            arg1 = arg1.replaceAll("%e", "DD");
            arg1 = arg1.replaceAll("%f", "FF5");
            arg1 = arg1.replaceAll("%H", "HH24");
            arg1 = arg1.replaceAll("%h", "HH12");
            arg1 = arg1.replaceAll("%I", "HH12");
            arg1 = arg1.replaceAll("%i", "MI");
            arg1 = arg1.replaceAll("%k", "HH24");
            arg1 = arg1.replaceAll("%l", "HH12");
            arg1 = arg1.replaceAll("%M", "MMMM");
            arg1 = arg1.replaceAll("%m", "MM");
            arg1 = arg1.replaceAll("%p", "AM");
            arg1 = arg1.replaceAll("%r", "HH12:MI:SS AM");
            arg1 = arg1.replaceAll("%S", "SS");
            arg1 = arg1.replaceAll("%s", "SS");
            arg1 = arg1.replaceAll("%T", "HH24:MI:SS");
            arg1 = arg1.replaceAll("%W", "dy");
            arg1 = arg1.replaceAll("%X", "YYYY");
            arg1 = arg1.replaceAll("%x", "YYYY");
            arg1 = arg1.replaceAll("%Y", "YYYY");
            arg1 = arg1.replaceAll("%y", "YY");
            arguments.set(1, arg1);
            this.functionName.setColumnName("TO_CHAR(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + ")");
            this.setOpenBracesForFunctionNameRequired(false);
            this.setFunctionArguments(new Vector());
         }
      }

   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("FROM_UNIXTIME")) {
         this.functionName.setColumnName("TO_TIMESTAMP");
         Vector arguments = new Vector();

         for(int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSapHanaSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         this.setFunctionArguments(arguments);
         long seconds = 0L;
         String columnName = "TO_TIMESTAMP('1970-01-01 00:00:00')";

         try {
            seconds = Long.parseLong(arguments.get(0).toString().replaceAll("'", ""));
         } catch (Exception var8) {
            seconds = 0L;
         }

         if (seconds < 0L) {
            columnName = "NULL";
         } else if (seconds > 0L) {
            columnName = "ADD_SECONDS(TO_TIMESTAMP('1970-01-01 00:00:00'),CAST(" + seconds + " AS BIGINT))";
         }

         arguments.set(0, columnName);
         if (arguments.size() > 1) {
            String arg1 = arguments.get(1).toString();
            arg1 = arg1.replaceAll("%a", "Dy");
            arg1 = arg1.replaceAll("%b", "Mon");
            arg1 = arg1.replaceAll("%c", "MM");
            arg1 = arg1.replaceAll("%D", "DD");
            arg1 = arg1.replaceAll("%d", "DD");
            arg1 = arg1.replaceAll("%e", "DD");
            arg1 = arg1.replaceAll("%f", "FF5");
            arg1 = arg1.replaceAll("%H", "HH24");
            arg1 = arg1.replaceAll("%h", "HH12");
            arg1 = arg1.replaceAll("%I", "HH12");
            arg1 = arg1.replaceAll("%i", "MI");
            arg1 = arg1.replaceAll("%k", "HH24");
            arg1 = arg1.replaceAll("%l", "HH12");
            arg1 = arg1.replaceAll("%M", "Month");
            arg1 = arg1.replaceAll("%m", "MM");
            arg1 = arg1.replaceAll("%p", "AM");
            arg1 = arg1.replaceAll("%r", "HH12:MI:SS AM");
            arg1 = arg1.replaceAll("%S", "SS");
            arg1 = arg1.replaceAll("%s", "SS");
            arg1 = arg1.replaceAll("%T", "HH24:MI:SS");
            arg1 = arg1.replaceAll("%U", "WW");
            arg1 = arg1.replaceAll("%u", "WW");
            arg1 = arg1.replaceAll("%V", "WW");
            arg1 = arg1.replaceAll("%v", "WW");
            arg1 = arg1.replaceAll("%W", "Day");
            arg1 = arg1.replaceAll("%X", "YYYY");
            arg1 = arg1.replaceAll("%x", "YYYY");
            arg1 = arg1.replaceAll("%Y", "YYYY");
            arg1 = arg1.replaceAll("%y", "YY");
            arguments.set(1, arg1);
            this.functionName.setColumnName("TO_CHAR(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + ")");
            this.setOpenBracesForFunctionNameRequired(false);
            this.setFunctionArguments(new Vector());
         }
      }

   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("FROM_UNIXTIME")) {
         Vector arguments = new Vector();

         for(int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         this.setFunctionArguments(arguments);
         long seconds = 0L;

         try {
            seconds = Long.parseLong(arguments.get(0).toString().replaceAll("'", ""));
         } catch (Exception var8) {
            seconds = 0L;
         }

         String qry;
         String arg1;
         if (seconds < 0L) {
            qry = "NULL";
         } else {
            arg1 = seconds > 0L ? String.valueOf(seconds) : arguments.get(0).toString();
            qry = "(DATETIME(1970-01-01 00:00:00.00000) YEAR TO FRACTION(5) + (" + arg1 + " / (24 * 60 * 60))::INTEGER UNITS DAY + (" + arg1 + " - ((" + arg1 + " / (24 * 60 * 60))::INTEGER * 24 * 60 * 60))::DECIMAL(11,5) UNITS FRACTION(5))";
         }

         if (arguments.size() > 1) {
            arg1 = arguments.get(1).toString();
            arg1 = arg1.replace("%M", "%B");
            arg1 = arg1.replace("%W", "%A");
            arg1 = arg1.replace("%h", "%I");
            arg1 = arg1.replace("%i", "%M");
            arg1 = arg1.replace("%s", "%S");
            qry = "TO_CHAR(" + qry + "," + arg1 + ")";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }
}
