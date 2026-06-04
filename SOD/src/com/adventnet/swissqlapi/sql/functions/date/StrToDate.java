package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class StrToDate extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TO_DATE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TO_TIMESTAMP");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

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
      this.setFunctionArguments(arguments);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TO_TIMESTAMP");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

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
      this.setFunctionArguments(arguments);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName(this.functionName.getColumnName());
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (i_count == 0) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement("cast(" + ((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs) + " as varchar)");
            } else {
               arguments.addElement("cast(" + this.functionArguments.elementAt(i_count) + " as varchar)");
            }
         } else if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      Boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateColumn = arguments.get(0).toString();
      String date_format = arguments.get(1).toString();
      List invalid_formats = Arrays.asList("%D", "%e", "%U", "%u", "%v", "%V", "%X", "%x");
      Iterator var8 = invalid_formats.iterator();

      while(var8.hasNext()) {
         Object invalid_format = var8.next();
         if (date_format.contains((String)invalid_format)) {
            throw new ConvertException("The specific format is not supported");
         }
      }

      String changed_format;
      if (isdenodo) {
         changed_format = this.changedDateFormatForDenodo(date_format);
      } else {
         changed_format = date_format.replace("%b", "Mon");
         changed_format = changed_format.replace("%a", "Dy");
         changed_format = changed_format.replace("%c", "MM");
         changed_format = changed_format.replace("%d", "DD");
         changed_format = changed_format.replace("%f", "FF");
         changed_format = changed_format.replace("%H", "HH24");
         changed_format = changed_format.replace("%h", "HH");
         changed_format = changed_format.replace("%I", "HH");
         changed_format = changed_format.replace("%i", "MI");
         changed_format = changed_format.replace("%j", "DDD");
         changed_format = changed_format.replace("%k", "HH24");
         changed_format = changed_format.replace("%l", "HH");
         changed_format = changed_format.replace("%M", "FMMonthFM");
         changed_format = changed_format.replace("%m", "MM");
         changed_format = changed_format.replace("%p", "AM");
         changed_format = changed_format.replace("%r", "HH12:MI:SS AM");
         changed_format = changed_format.replace("%s", "SS");
         changed_format = changed_format.replace("%S", "SS");
         changed_format = changed_format.replace("%T", "HH24:MI:SS");
         changed_format = changed_format.replace("%w", "D");
         changed_format = changed_format.replace("%W", "FMDayFM");
         changed_format = changed_format.replace("%Y", "YYYY");
         changed_format = changed_format.replace("%y", "YY");
      }

      String query = "";
      if (this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("time_format")) {
         query = isdenodo ? "FORMATDATE(" + changed_format + "," + StringFunctions.handleLiteralStringDateForDenodo(dateColumn) + ")" : "to_char(to_timestamp(" + dateColumn + ",'HH24:MI:SS.FF')," + changed_format + ")";
         this.functionName.setColumnName(query);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         List time_formats = Arrays.asList("%H", "%h", "%I", "%i", "%k", "%l", "%f", "%r", "%s", "%S", "%T", "%p");
         Boolean istimeformat = false;
         Iterator var12 = time_formats.iterator();

         while(var12.hasNext()) {
            Object time_format = var12.next();
            if (date_format.contains((String)time_format)) {
               istimeformat = true;
            }
         }

         if (isdenodo) {
            if (istimeformat) {
               query = "TO_TIMESTAMP(" + changed_format + "," + dateColumn + ")";
            } else {
               query = "TO_LOCALDATE(" + changed_format + "," + dateColumn + ")";
            }
         } else if (istimeformat) {
            query = "TO_TIMESTAMP(" + dateColumn + "," + changed_format + ")";
         } else {
            query = "TO_DATE(" + dateColumn + "," + changed_format + ")";
         }

         this.functionName.setColumnName(query);
         this.setOpenBracesForFunctionNameRequired(false);
      }

      this.functionArguments = new Vector();
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

      String dateColumn = StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString());
      String date_format = arguments.get(1).toString().replace("'", "");
      List invalid_formats = Arrays.asList("%U", "%u", "%v", "%V", "%X", "%x");
      Iterator var7 = invalid_formats.iterator();

      Object invalid_format;
      do {
         if (!var7.hasNext()) {
            String qry = "strftime('%Y'," + dateColumn + ") || '-' || strftime('%m'," + dateColumn + ") || '-' || strftime('%d'," + dateColumn + ")";
            if (date_format.contains("%H")) {
               qry = qry + " || ' ' || strftime('%H'," + dateColumn + ")";
            }

            if (date_format.contains("%i")) {
               qry = qry + " || ':' || strftime('%M'," + dateColumn + ")";
            }

            if (date_format.contains("%s")) {
               qry = qry + " || ':' || strftime('%S'," + dateColumn + ")";
            }

            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
            return;
         }

         invalid_format = var7.next();
      } while(!date_format.contains((String)invalid_format));

      throw new ConvertException("Given format is not supported in Sqlite DB");
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateColumn = "CDate(" + arguments.get(0).toString() + ")";
      String date_format = arguments.get(1).toString().replace("'", "");
      List invalid_formats = Arrays.asList("%U", "%u", "%v", "%V", "%X", "%x");
      Iterator var7 = invalid_formats.iterator();

      while(var7.hasNext()) {
         Object invalid_format = var7.next();
         if (date_format.contains((String)invalid_format)) {
            throw new ConvertException("Given format is not supported in Sqlite DB");
         }
      }

      String qry = "Format(" + dateColumn + ",'yyyy-MM-dd')";
      if (date_format.contains("%H") || date_format.contains("HH")) {
         qry = qry + " & ' ' & Format(" + dateColumn + ",'hh')";
      }

      if (date_format.contains("%i") || date_format.contains("mm")) {
         qry = qry + " & ':' & Format(" + dateColumn + ",'nn')";
      }

      if (date_format.contains("%s") || date_format.contains("ss")) {
         qry = qry + " & ':' & Format(" + dateColumn + ",'ss')";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TO_TIMESTAMP");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateFormat = changedDateFormatForHyperSql(arguments.get(1).toString());
      arguments.set(0, StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString()));
      arguments.set(1, dateFormat);
      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("STR_TO_DATE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (from_sqs != null && from_sqs.isMongoDb()) {
         arguments = new Vector();
         if (this.functionArguments.size() == 2 && this.functionArguments.get(0).toString().contains("CONCAT_WS")) {
            String[] concatFormat = this.functionArguments.get(0).toString().split(",");
            String commaSeparte = ",";
            String qry = concatFormat[0] + commaSeparte;

            for(int j = 1; j < concatFormat.length; ++j) {
               if (j == concatFormat.length - 1) {
                  concatFormat[j] = concatFormat[j].charAt(concatFormat[j].length() - 1) == ')' ? concatFormat[j].substring(0, concatFormat[j].length() - 1) : concatFormat[j];
                  commaSeparte = "";
               }

               qry = qry + "CONCAT(IF(" + concatFormat[j] + " < 10 , \"0\",\"\")," + concatFormat[j] + ")" + commaSeparte;
            }

            qry = qry + ")";
            arguments.add(qry);
            arguments.add(this.functionArguments.get(1));
            this.setFunctionArguments(arguments);
         }
      }

      if (from_sqs != null && from_sqs.isHyperSql()) {
         this.functionName.setColumnName("TO_TIMESTAMP");
         String dateFormat = changedDateFormatForHyperSql(arguments.get(1).toString());
         arguments.set(1, dateFormat);
      }

   }

   private String changedDateFormatForDenodo(String date_format) {
      String changed_format = date_format.replace("%b", "MMM");
      changed_format = changed_format.replace("%a", "EEE");
      changed_format = changed_format.replace("%c", "MM");
      changed_format = changed_format.replace("%d", "dd");
      changed_format = changed_format.replace("%f", "SSSSSS");
      changed_format = changed_format.replace("%H", "HH");
      changed_format = changed_format.replace("%h", "hh");
      changed_format = changed_format.replace("%I", "hh");
      changed_format = changed_format.replace("%i", "mm");
      changed_format = changed_format.replace("%j", "DDD");
      changed_format = changed_format.replace("%k", "HH");
      changed_format = changed_format.replace("%l", "hh");
      changed_format = changed_format.replace("%M", "MMMM");
      changed_format = changed_format.replace("%m", "MM");
      changed_format = changed_format.replace("%p", "a");
      changed_format = changed_format.replace("%r", "HH:mm:ss a");
      changed_format = changed_format.replace("%s", "ss");
      changed_format = changed_format.replace("%S", "ss");
      changed_format = changed_format.replace("%T", "HH:mm:ss");
      changed_format = changed_format.replace("%w", "D");
      changed_format = changed_format.replace("%W", "EEEE");
      changed_format = changed_format.replace("%Y", "yyyy");
      changed_format = changed_format.replace("%y", "yy");
      return changed_format;
   }

   public static String changedDateFormatForHyperSql(String date_format) {
      String changed_format = date_format.replace("%b", "MON");
      changed_format = changed_format.replace("%a", "DY");
      changed_format = changed_format.replace("%c", "MM");
      changed_format = changed_format.replace("%d", "DD");
      changed_format = changed_format.replace("%f", "FF");
      changed_format = changed_format.replace("%H", "HH24");
      changed_format = changed_format.replace("%h", "HH");
      changed_format = changed_format.replace("%I", "HH");
      changed_format = changed_format.replace("%i", "MI");
      changed_format = changed_format.replace("%j", "DDD");
      changed_format = changed_format.replace("%k", "HH24");
      changed_format = changed_format.replace("%l", "HH");
      changed_format = changed_format.replace("%M", "MONTH");
      changed_format = changed_format.replace("%m", "MM");
      changed_format = changed_format.replace("%p", "A.M.");
      changed_format = changed_format.replace("%r", "HH:MI:SS A.M.");
      changed_format = changed_format.replace("%s", "SS");
      changed_format = changed_format.replace("%S", "SS");
      changed_format = changed_format.replace("%T", "HH24:MI:SS");
      changed_format = changed_format.replace("%W", "DAY");
      changed_format = changed_format.replace("%Y", "YYYY");
      changed_format = changed_format.replace("%y", "YY");
      changed_format = changed_format.replace(".0", "");
      return changed_format;
   }
}
