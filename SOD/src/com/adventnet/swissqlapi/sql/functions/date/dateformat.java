package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class dateformat extends FunctionCalls {
   static Hashtable dateFormats = new Hashtable();

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      Boolean isDenodo = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String date_format;
      String query;
      if (isDenodo) {
         if (this.functionName.getColumnName().equalsIgnoreCase("date_format")) {
            this.functionName.setColumnName("FORMATDATE");
            if (arguments.size() == 2) {
               Vector v = new Vector();
               date_format = arguments.get(1).toString();
               List invalid_formats = Arrays.asList("%D", "%e", "%U", "%u", "%v", "%V", "%X", "%x");
               Iterator var8 = invalid_formats.iterator();

               while(var8.hasNext()) {
                  Object invalid_format = var8.next();
                  if (date_format.contains((String)invalid_format)) {
                     throw new ConvertException("The specific format is not supported");
                  }
               }

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
               changed_format = changed_format.replace("%r", "hh:mm:ss a");
               changed_format = changed_format.replace("%s", "ss");
               changed_format = changed_format.replace("%S", "ss");
               changed_format = changed_format.replace("%T", "HH:mm:ss");
               changed_format = changed_format.replace("%w", "D");
               changed_format = changed_format.replace("%W", "EEEE");
               changed_format = changed_format.replace("%Y", "yyyy");
               changed_format = changed_format.replace("%y", "yy");
               changed_format = changed_format.replace(".0", "");
               v.add(0, changed_format);
               String date = arguments.get(0).toString();
               arguments.set(0, changed_format);
               arguments.set(1, StringFunctions.handleLiteralStringDateForDenodo(date));
            }

            this.setFunctionArguments(arguments);
            return;
         }

         if (this.functionName.getColumnName().equalsIgnoreCase("from_unixtime")) {
            query = "CAST(ADDSECOND(TO_LOCALDATE('yyyy-MM-dd','1970-01-01'),CAST(" + arguments.get(0).toString() + " AS INT8)) AS TIMESTAMP)";
            if (arguments.size() == 1) {
               this.functionName.setColumnName(query);
               this.setOpenBracesForFunctionNameRequired(false);
               this.functionArguments = new Vector();
            } else {
               FunctionCalls fc = new FunctionCalls();
               arguments.set(0, query);
               TableColumn tb = new TableColumn();
               tb.setColumnName("DATE_FORMAT");
               fc.setFunctionName(tb);
               fc.setFunctionArguments(arguments);
               fc = fc.toOracleSelect(to_sqs, from_sqs);
               this.functionName = fc.getFunctionName();
               this.functionArguments = fc.getFunctionArguments();
            }

            return;
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("from_unixtime")) {
         query = arguments.get(0).toString();
         date_format = "cast((to_date('1970-01-01 ','YYYY-MM-DD ') + numtodsinterval(" + query + ",'SECOND')) as TIMESTAMP)";
         if (arguments.size() == 1) {
            this.functionName.setColumnName(date_format);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
            return;
         }

         if (arguments.size() == 2) {
            arguments.set(0, date_format);
            this.functionName.setColumnName("TO_CHAR");
         }
      }

      this.setFunctionArguments(arguments);
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

      String qry;
      if (from_sqs != null && from_sqs.isMongoDb() && this.functionName.getColumnName().equalsIgnoreCase("time_format")) {
         arguments = new Vector();
         this.functionName.setColumnName("");
         qry = this.functionArguments.get(0).toString();
         String qry = "DATE_FORMAT(DATE(NOW())+INTERVAL HOUR(" + qry + ") HOUR+INTERVAL MINUTE(" + qry + ") MINUTE+INTERVAL SECOND(" + qry + ") SECOND+INTERVAL MICROSECOND(" + qry + ") MICROSECOND," + this.functionArguments.get(1).toString() + ")";
         arguments.addElement(qry);
      }

      if (from_sqs != null && from_sqs.isHyperSql()) {
         if (this.functionName.getColumnName().equalsIgnoreCase("from_unixtime")) {
            qry = "TIMESTAMP(CAST(" + arguments.get(0).toString() + " AS BIGINT))";
            if (arguments.size() == 1) {
               this.functionName.setColumnName(qry);
               this.setOpenBracesForFunctionNameRequired(false);
               this.setFunctionArguments(new Vector());
               return;
            }

            arguments.set(0, qry);
         } else if (this.functionName.getColumnName().equalsIgnoreCase("time_format")) {
            arguments.set(0, "CAST(('1970-01-01 ' || " + arguments.get(0) + ") AS TIMESTAMP)");
         }

         this.functionName.setColumnName("TO_CHAR");
         qry = StrToDate.changedDateFormatForHyperSql(arguments.get(1).toString());
         arguments.set(1, qry);
      }

      this.setFunctionArguments(arguments);
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

      if (this.functionName.getColumnName().equalsIgnoreCase("STR_TO_DATE")) {
         this.functionName.setColumnName("date_parse");
      } else {
         String arg;
         if (this.functionName.getColumnName().equalsIgnoreCase("From_unixtime")) {
            if (from_sqs != null && from_sqs.isTrino()) {
               arg = arguments.get(0).toString();
               if (arg.startsWith("'") && arg.endsWith("'")) {
                  arguments.setElementAt("CAST(" + arguments.get(0).toString() + " AS BIGINT)", 0);
               }

               if (arguments.size() == 1) {
                  this.functionName.setColumnName("CAST(from_unixtime(" + arguments.get(0).toString() + ") AS TIMESTAMP)");
                  this.setFunctionArguments(new Vector());
                  this.setOpenBracesForFunctionNameRequired(false);
                  return;
               }
            }

            if (arguments.size() == 2) {
               Vector subFunctionArgs = new Vector();
               this.functionName.setColumnName("DATE_FORMAT");
               FunctionCalls subFunction = new FunctionCalls();
               TableColumn subTC = new TableColumn();
               subTC.setColumnName("From_unixtime");
               subFunction.setFunctionName(subTC);
               Vector scColumnExpression = new Vector();
               SelectColumn sc = new SelectColumn();
               subFunctionArgs.add(arguments.get(0).toString());
               subFunction.setFunctionArguments(subFunctionArgs);
               scColumnExpression.add(subFunction);
               sc.setColumnExpression(scColumnExpression);
               arguments.set(0, sc);
            }
         } else if (this.functionName.getColumnName().equalsIgnoreCase("time_format")) {
            arg = "CAST(CAST(" + arguments.get(0).toString() + " AS TIME) AS TIMESTAMP)";
            arguments.set(0, arg);
            this.functionName.setColumnName("DATE_FORMAT");
         } else {
            arg = StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString());
            arguments.set(0, arg);
         }
      }

      this.setFunctionArguments(arguments);
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

      String dateColumn = arguments.get(0).toString();
      String date_format = arguments.size() > 1 ? arguments.get(1).toString() : "";
      List invalid_formats = Arrays.asList("%U", "%u", "%v", "%V", "%X", "%x");
      Iterator var7 = invalid_formats.iterator();

      Object invalid_format;
      do {
         if (!var7.hasNext()) {
            String dateColumnQry;
            String date1;
            if (this.functionName.getColumnName().equalsIgnoreCase("FROM_UNIXTIME")) {
               if (arguments.size() == 1) {
                  date_format = "'%Y-%m-%d %H:%i:%S.%f'";
               }

               dateColumnQry = "DATETIME(" + dateColumn + ",'unixepoch')";
            } else if (this.functionName.getColumnName().equalsIgnoreCase("time_format")) {
               dateColumnQry = "strftime('%Y-%m-%d %H:%M:%f','1970-01-01' || " + dateColumn + ")";
            } else {
               date1 = dateColumn.replaceAll("\\(", "").replace(")", "");
               if (date1.trim().startsWith("'") && date1.trim().endsWith("'")) {
                  String datevalue = date1.trim().replaceAll("'", "").replaceAll("/", "-");
                  String dateformat = StringFunctions.identifyDateFormate(datevalue);
                  if (dateformat == null) {
                     throw new ConvertException("Date format not supported, Please give in formats : " + Arrays.toString(new String[]{"yyyy-MM-dd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS", "yyyy/MM/dd HH:mm:ss.SSS"}));
                  }
               }

               dateColumnQry = dateColumn;
            }

            date1 = this.convertDateFormatForSqlite(StringFunctions.handleLiteralStringDateForSqlite(dateColumnQry), date_format);
            this.functionName.setColumnName(date1);
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

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      String date_format = arguments.size() > 1 ? arguments.get(1).toString().replace("'", "") : "";
      date_format = date_format.replace(".0", "");
      date_format = date_format.replace(".%f", "");
      List invalid_formats = Arrays.asList("%D", "%e", "%U", "%u", "%v", "%V", "%X", "%x");
      Iterator var6 = invalid_formats.iterator();

      Object invalid_format;
      do {
         if (!var6.hasNext()) {
            String dateColumn = arguments.get(0).toString();
            String query;
            if (this.functionName.getColumnName().equalsIgnoreCase("FROM_UNIXTIME")) {
               query = "DATEADD('s',CLng( " + arguments.get(0).toString() + "),'1970-01-01 00:00:00')";
               if (arguments.size() == 1) {
                  this.functionName.setColumnName(query);
                  this.setOpenBracesForFunctionNameRequired(false);
                  this.functionArguments = new Vector();
                  return;
               }

               arguments.set(0, query);
               dateColumn = query;
            } else if (this.functionName.getColumnName().equalsIgnoreCase("TIME_FORMAT")) {
               dateColumn = StringFunctions.handleLiteralStringDateForExcel(arguments.get(0).toString());
            } else {
               dateColumn = "CDate(" + arguments.get(0).toString() + ")";
            }

            query = this.convertDateFormatForExcel(dateColumn, date_format);
            this.functionName.setColumnName(query);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
            return;
         }

         invalid_format = var6.next();
      } while(!date_format.contains((String)invalid_format));

      throw new ConvertException("The specific format is not supported");
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      String dateFormat;
      if (this.functionName.getColumnName().equalsIgnoreCase("from_unixtime")) {
         dateFormat = "TIMESTAMP(CAST(" + arguments.get(0).toString() + " AS BIGINT))";
         if (arguments.size() == 1) {
            this.functionName.setColumnName(dateFormat);
            this.setOpenBracesForFunctionNameRequired(false);
            this.setFunctionArguments(new Vector());
            return;
         }

         arguments.set(0, dateFormat);
      } else if (this.functionName.getColumnName().equalsIgnoreCase("time_format")) {
         arguments.set(0, "CAST(('1970-01-01 ' || " + arguments.get(0) + ") AS TIMESTAMP)");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("date_format")) {
         arguments.set(0, StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString()));
      }

      this.functionName.setColumnName("TO_CHAR");
      dateFormat = StrToDate.changedDateFormatForHyperSql(arguments.get(1).toString());
      arguments.set(1, dateFormat);
      this.setFunctionArguments(arguments);
   }

   private String convertDateFormatForSqlite(String dateColumn, String date_format) {
      String delimiter = "";
      String qry = "";
      date_format = date_format.replace("'", "");
      String[] formatList = date_format.split("%");
      String[] var6 = formatList;
      int var7 = formatList.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String format = var6[var8];
         format = !format.isEmpty() ? "%" + format.substring(0, 1) : format;
         delimiter = "'" + date_format.substring(date_format.indexOf(format) + 2, date_format.indexOf(format) + 3 >= date_format.length() ? date_format.indexOf(format) + 2 : date_format.indexOf(format) + 3) + "'";
         byte var11 = -1;
         switch(format.hashCode()) {
         case 1215:
            if (format.equals("%D")) {
               var11 = 13;
            }
         case 1216:
         case 1217:
         case 1218:
         case 1221:
         case 1222:
         case 1223:
         case 1225:
         case 1226:
         case 1227:
         case 1228:
         case 1229:
         case 1232:
         case 1233:
         case 1235:
         case 1237:
         case 1238:
         case 1239:
         case 1240:
         case 1241:
         case 1242:
         case 1243:
         case 1250:
         case 1253:
         case 1257:
         case 1258:
         case 1260:
         case 1263:
         case 1264:
         case 1265:
         case 1266:
         case 1267:
         default:
            break;
         case 1219:
            if (format.equals("%H")) {
               var11 = 20;
            }
            break;
         case 1220:
            if (format.equals("%I")) {
               var11 = 15;
            }
            break;
         case 1224:
            if (format.equals("%M")) {
               var11 = 12;
            }
            break;
         case 1230:
            if (format.equals("%S")) {
               var11 = 6;
            }
            break;
         case 1231:
            if (format.equals("%T")) {
               var11 = 7;
            }
            break;
         case 1234:
            if (format.equals("%W")) {
               var11 = 10;
            }
            break;
         case 1236:
            if (format.equals("%Y")) {
               var11 = 0;
            }
            break;
         case 1244:
            if (format.equals("%a")) {
               var11 = 9;
            }
            break;
         case 1245:
            if (format.equals("%b")) {
               var11 = 11;
            }
            break;
         case 1246:
            if (format.equals("%c")) {
               var11 = 1;
            }
            break;
         case 1247:
            if (format.equals("%d")) {
               var11 = 3;
            }
            break;
         case 1248:
            if (format.equals("%e")) {
               var11 = 14;
            }
            break;
         case 1249:
            if (format.equals("%f")) {
               var11 = 8;
            }
            break;
         case 1251:
            if (format.equals("%h")) {
               var11 = 16;
            }
            break;
         case 1252:
            if (format.equals("%i")) {
               var11 = 4;
            }
            break;
         case 1254:
            if (format.equals("%k")) {
               var11 = 19;
            }
            break;
         case 1255:
            if (format.equals("%l")) {
               var11 = 17;
            }
            break;
         case 1256:
            if (format.equals("%m")) {
               var11 = 2;
            }
            break;
         case 1259:
            if (format.equals("%p")) {
               var11 = 18;
            }
            break;
         case 1261:
            if (format.equals("%r")) {
               var11 = 21;
            }
            break;
         case 1262:
            if (format.equals("%s")) {
               var11 = 5;
            }
            break;
         case 1268:
            if (format.equals("%y")) {
               var11 = 22;
            }
         }

         switch(var11) {
         case 0:
            qry = qry + "STRFTIME('%Y'," + dateColumn + ") || " + delimiter + " || ";
            break;
         case 1:
            qry = qry + "CAST(STRFTIME('%m'," + dateColumn + ") AS INTEGER) || " + delimiter + " || ";
            break;
         case 2:
            qry = qry + "(STRFTIME('%m'," + dateColumn + ")) || " + delimiter + " || ";
            break;
         case 3:
            qry = qry + "STRFTIME('%d'," + dateColumn + ") || " + delimiter + " || ";
            break;
         case 4:
            qry = qry + "STRFTIME('%M'," + dateColumn + ") || " + delimiter + " || ";
            break;
         case 5:
         case 6:
            qry = qry + "STRFTIME('%S'," + dateColumn + ") || " + delimiter + " || ";
            break;
         case 7:
            qry = qry + "(CASE WHEN (CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER) = 24) THEN '00' ELSE (STRFTIME('%H'," + dateColumn + ")) END) || ':' || (STRFTIME('%M:%S'," + dateColumn + ")) || " + delimiter + " || ";
            break;
         case 8:
            qry = qry + "(CASE WHEN ((SUBSTR(" + dateColumn + ",21) = '') || (CAST(SUBSTR(" + dateColumn + ",21) AS INTEGER) = 0)) THEN '000000' ELSE (CASE WHEN LENGTH(SUBSTR(" + dateColumn + ",21))<6 THEN CAST(SUBSTR(" + dateColumn + ",21)*POW(10,6-LENGTH(SUBSTR(" + dateColumn + ",21))) AS INTEGER) ELSE SUBSTR(" + dateColumn + ",21) END) END) || " + delimiter + " || ";
            break;
         case 9:
            qry = qry + "(CASE CAST(STRFTIME('%w'," + dateColumn + ") AS INTEGER) WHEN 0 THEN 'Sun' WHEN 1 THEN 'Mon' WHEN 2 THEN 'Tue' WHEN 3 THEN 'Wed' WHEN 4 THEN 'Thu' WHEN 5 THEN 'Fri' WHEN 6 THEN 'Sat' END) || " + delimiter + " || ";
            break;
         case 10:
            qry = qry + "(CASE CAST(STRFTIME('%w'," + dateColumn + ") AS INTEGER) WHEN 0 THEN 'Sunday' WHEN 1 THEN 'Monday' WHEN 2 THEN 'Tuesday' WHEN 3 THEN 'Wednesday' WHEN 4 THEN 'Thursday' WHEN 5 THEN 'Friday' WHEN 6 THEN 'Saturday' END) || " + delimiter + " || ";
            break;
         case 11:
            qry = qry + "(CASE CAST(STRFTIME('%m'," + dateColumn + ") AS INTEGER) WHEN 1 THEN 'Jan' WHEN 2 THEN 'Feb' WHEN 3 THEN 'Mar' WHEN 4 THEN 'Apr' WHEN 5 THEN 'May' WHEN 6 THEN 'Jun' WHEN 7 THEN 'Jul' WHEN 8 THEN 'Aug' WHEN 9 THEN 'Sep' WHEN 10 THEN 'Oct' WHEN 11 THEN 'Nov' ELSE 'Dec' END) || " + delimiter + " || ";
            break;
         case 12:
            qry = qry + "(CASE CAST(STRFTIME('%m'," + dateColumn + ") AS INTEGER) WHEN 1 THEN 'January' WHEN 2 THEN 'February' WHEN 3 THEN 'March' WHEN 4 THEN 'April' WHEN 5 THEN 'May' WHEN 6 THEN 'June' WHEN 7 THEN 'July' WHEN 8 THEN 'August' WHEN 9 THEN 'September' WHEN 10 THEN 'October' WHEN 11 THEN 'November' ELSE 'December' END) || " + delimiter + " || ";
            break;
         case 13:
            qry = qry + "(CASE WHEN (CAST(STRFTIME('%d'," + dateColumn + ") AS INTEGER) BETWEEN 11 AND 13) THEN (CAST(STRFTIME('%d'," + dateColumn + ") AS INTEGER) || 'th') WHEN ((CAST(STRFTIME('%d'," + dateColumn + ") AS INTEGER)%10)=1) THEN (CAST(STRFTIME('%d'," + dateColumn + ") AS INTEGER) || 'st') WHEN ((CAST(STRFTIME('%d'," + dateColumn + ") AS INTEGER)%10)=2) THEN (CAST(STRFTIME('%d'," + dateColumn + ") AS INTEGER) || 'nd') WHEN ((CAST(STRFTIME('%d'," + dateColumn + ") AS INTEGER)%10)=3) THEN (CAST(STRFTIME('%d'," + dateColumn + ") AS INTEGER) || 'rd') ELSE (CAST(STRFTIME('%d'," + dateColumn + ") AS INTEGER) || 'th') END) || " + delimiter + " || ";
            break;
         case 14:
            qry = qry + "CAST(STRFTIME('%d'," + dateColumn + ") AS INTEGER) || " + delimiter + " || ";
            break;
         case 15:
         case 16:
            qry = qry + "(CASE WHEN (CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER)%12 = 0) THEN 12 ELSE (CASE WHEN (CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER)%12) < 10 THEN ('0' || (CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER)%12)) ELSE (CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER)%12) END) END) || " + delimiter + " || ";
            break;
         case 17:
            qry = qry + "(CASE WHEN (CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER)%12 = 0) THEN 12 ELSE (CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER)%12) END) || " + delimiter + " || ";
            break;
         case 18:
            qry = qry + "(CASE WHEN CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER) = 24 THEN 'AM' WHEN CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER)>=12 THEN 'PM' ELSE 'AM' END) || " + delimiter + " || ";
            break;
         case 19:
         case 20:
            qry = qry + "(CASE WHEN (CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER) = 24) THEN '00' ELSE (STRFTIME('%H'," + dateColumn + ")) END) || " + delimiter + " || ";
            break;
         case 21:
            qry = qry + "(CASE WHEN (CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER)%12 = 0) THEN 12 ELSE (CASE WHEN (CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER)%12) < 10 THEN ('0' || (CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER)%12)) ELSE (CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER)%12) END) END) || ':' || (STRFTIME('%M'," + dateColumn + ")) || ':' || (STRFTIME('%S'," + dateColumn + ")) || ' ' || (CASE WHEN CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER) = 24 THEN 'AM' WHEN CAST(STRFTIME('%H'," + dateColumn + ") AS INTEGER)>=12 THEN 'PM' ELSE 'AM' END) || " + delimiter + " || ";
            break;
         case 22:
            qry = qry + "(SUBSTR(CAST(STRFTIME('%Y'," + dateColumn + ") AS INTEGER),3)) || " + delimiter + " || ";
         }
      }

      return qry + " ''";
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

      arguments.set(0, arguments.get(0).toString());
      String arg1 = arguments.get(1).toString();
      arg1 = arg1.replaceAll("%a", "Dy");
      arg1 = arg1.replaceAll("%b", "Mon");
      arg1 = arg1.replaceAll("%c", "MM");
      arg1 = arg1.replaceAll("%D", "DD");
      arg1 = arg1.replaceAll("%d", "DD");
      arg1 = arg1.replaceAll("%e", "DD");
      arg1 = arg1.replaceAll("%f", "FF6");
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
      arg1 = arg1.replace(".0", "");
      arguments.set(1, arg1);
      if (this.functionName.getColumnName().equalsIgnoreCase("time_format")) {
         String arg = "CAST('1970-01-01 ' || " + arguments.get(0).toString() + " AS TIMESTAMP)";
         arguments.set(0, arg);
      }

      this.functionName.setColumnName("TO_CHAR");
      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String temp = null;
      String s_ce = null;
      Vector arguments = new Vector();
      String arg1;
      String datestr;
      if (from_sqs.isMSAzure()) {
         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         if (this.functionName.getColumnName().equalsIgnoreCase("STR_TO_DATE")) {
            datestr = "TRY_CONVERT(DATETIME," + arguments.get(0).toString() + ",102)";
            this.functionName.setColumnName(datestr);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
            return;
         }

         if (this.functionName.getColumnName().equalsIgnoreCase("time_format")) {
            arguments.set(0, "CONVERT(DATETIME2," + arguments.get(0).toString() + ")");
         } else if (this.functionName.getColumnName().equalsIgnoreCase("FROM_UNIXTIME")) {
            datestr = "DATEADD(s,CAST( " + arguments.get(0).toString() + " AS BIGINT),'1970-01-01 00:00:00')";
            if (arguments.size() == 1) {
               this.functionName.setColumnName(datestr);
               this.setOpenBracesForFunctionNameRequired(false);
               this.functionArguments = new Vector();
               return;
            }

            arguments.set(0, datestr);
         }

         this.functionName.setColumnName("format");
         datestr = arguments.get(0).toString();
         datestr = "CAST(" + datestr + " AS DATETIME)";
         arguments.set(0, datestr);
         arg1 = arguments.get(1).toString();
         arg1 = arg1.replaceAll("%a", "ddd");
         arg1 = arg1.replaceAll("%b", "MMM");
         arg1 = arg1.replaceAll("%c", "MM");
         arg1 = arg1.replaceAll("%d", "dd");
         arg1 = arg1.replaceAll("%e", "d");
         arg1 = arg1.replaceAll("%f", "ffffff");
         arg1 = arg1.replaceAll("%H", "HH");
         arg1 = arg1.replaceAll("%h", "hh");
         arg1 = arg1.replaceAll("%I", "hh");
         arg1 = arg1.replaceAll("%i", "mm");
         arg1 = arg1.replaceAll("%j", "DDD");
         arg1 = arg1.replaceAll("%k", "H");
         arg1 = arg1.replaceAll("%l", "h");
         arg1 = arg1.replaceAll("%M", "MMMM");
         arg1 = arg1.replaceAll("%m", "MM");
         arg1 = arg1.replaceAll("%p", "tt");
         arg1 = arg1.replaceAll("%r", "hh:mm:ss tt");
         arg1 = arg1.replaceAll("%S", "ss");
         arg1 = arg1.replaceAll("%s", "ss");
         arg1 = arg1.replaceAll("%T", "hh:mm:ss");
         arg1 = arg1.replaceAll("%U", "ww");
         arg1 = arg1.replaceAll("%u", "ww");
         arg1 = arg1.replaceAll("%V", "ww");
         arg1 = arg1.replaceAll("%v", "ww");
         arg1 = arg1.replaceAll("%W", "dddd");
         arg1 = arg1.replaceAll("%X", "yyyy");
         arg1 = arg1.replaceAll("%x", "yyyy");
         arg1 = arg1.replaceAll("%Y", "yyyy");
         arg1 = arg1.replaceAll("%y", "yy");
         arguments.set(1, arg1);
         this.setFunctionArguments(arguments);
      } else {
         datestr = null;
         if (this.functionName.getColumnName().equalsIgnoreCase("DATE_FORMAT")) {
            this.functionName.setColumnName("format");
         }

         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            datestr = ((SelectColumn)this.functionArguments.elementAt(0)).toMSSQLServerSelect(to_sqs, from_sqs).toString();
         } else {
            datestr = this.functionArguments.elementAt(0).toString();
         }

         arg1 = this.functionArguments.get(1).toString();
         if (arg1.startsWith("'") && arg1.endsWith("'")) {
            arg1 = arg1.substring(1, arg1.length() - 1);
         }

         if (arg1.lastIndexOf("%") == 0) {
            if (dateFormats.containsKey(arg1)) {
               String singleType = "CAST(" + dateFormats.get(arg1).toString() + datestr + ")" + " AS VARCHAR)";
               arguments.add(singleType);
            }
         } else {
            int seperatorIndex = arg1.lastIndexOf("%") - 1;
            String ss = arg1.substring(seperatorIndex, seperatorIndex + 1);
            if (arg1.indexOf(ss) != -1 && arg1.lastIndexOf("%") != 0) {
               ss = ss.equals(".") ? "\\." : ss;
               String[] tokens = arg1.split(ss);

               for(int i = 0; i < tokens.length; ++i) {
                  if (dateFormats.containsKey(tokens[i])) {
                     tokens[i] = dateFormats.get(tokens[i]).toString();
                     if (i == 0) {
                        temp = "CAST(" + tokens[0] + datestr + ")" + " AS VARCHAR)";
                        if (tokens.length == 1) {
                           arguments.add(temp);
                        }
                     } else {
                        temp = temp + "+'" + ss + "'+" + "CAST(" + tokens[i] + datestr + ")" + " AS VARCHAR)";
                     }
                  }
               }

               if (tokens.length > 1) {
                  arguments.add(temp);
               }
            }
         }

         if (arguments.size() != 0) {
            this.setFunctionArguments(arguments);
         } else {
            this.setFunctionArguments(this.functionArguments);
         }
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String oldFunctionName = this.functionName.getColumnName();
      boolean canUseUDFFunction = false;
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      if (this.functionName.getColumnName().equalsIgnoreCase("STR_TO_DATE")) {
         this.functionName.setColumnName("TO_TIMESTAMP");
         canUseUDFFunction = from_sqs != null && !isPostgreLiveDbs && (from_sqs.getBooleanValues("use.udf.functions.for.text") || from_sqs.getBooleanValues("use.udf.functions.for.str.to.date"));
         if (canUseUDFFunction) {
            this.functionName.setColumnName("ZR_STR_TO_DATE");
         }
      } else {
         this.functionName.setColumnName("TO_CHAR");
      }

      Vector arguments = new Vector();

      String dateOrTimeString;
      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (i_count == 0 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0) instanceof String) {
            dateOrTimeString = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0).toString();
            if (oldFunctionName.equalsIgnoreCase("TIME_FORMAT")) {
               dateOrTimeString = "CAST(" + this.handleStringLiteralForTime(dateOrTimeString, from_sqs) + " AS TIME)";
               ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().set(0, dateOrTimeString);
            } else if (oldFunctionName.equalsIgnoreCase("DATE_FORMAT")) {
               dateOrTimeString = "CAST(" + this.handleStringLiteralForDateTime(dateOrTimeString, from_sqs) + " AS TIMESTAMP)";
               ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().set(0, dateOrTimeString);
            } else if (from_sqs != null && !isPostgreLiveDbs) {
               dateOrTimeString = "CAST(" + dateOrTimeString + " AS TEXT)";
               ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().set(0, dateOrTimeString);
            }
         }

         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            if (i_count == 0 && canUseUDFFunction) {
               sc.convertSelectColumnToTextDataType();
            }

            arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String arg1 = arguments.get(1).toString();
      arg1 = this.getPgSQLDateFormatForMySQLWildCard(arg1);
      arguments.set(1, arg1);
      if (this.functionName.getColumnName().equalsIgnoreCase("TO_DATE")) {
         dateOrTimeString = arguments.get(0).toString();
         dateOrTimeString = "cast(" + dateOrTimeString + " as text)";
         arguments.set(0, dateOrTimeString);
      }

      this.setFunctionArguments(arguments);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String oldfunName = this.functionName.getColumnName().toString();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      String arg1;
      if (oldfunName.equalsIgnoreCase("STR_TO_DATE")) {
         this.functionName.setColumnName("TO_DATE");
      } else if (oldfunName.equalsIgnoreCase("TIME_FORMAT")) {
         this.functionName.setColumnName("TO_CHAR");
         arg1 = " EXTEND(TO_DATE('1970-01-01 00:00:00'),YEAR TO FRACTION(5)) + SUBSTR(" + arguments.get(0).toString() + ",1,2) UNITS HOUR +SUBSTR(" + arguments.get(0).toString() + ",4,2) UNITS MINUTE +SUBSTR(" + arguments.get(0).toString() + ",7,2) UNITS SECOND +(CASE WHEN INSTR(" + arguments.get(0).toString() + ",'.')>0 THEN RPAD(SUBSTR(" + arguments.get(0).toString() + ",10,5),6,'0')*0.000001 ELSE 0 END) UNITS FRACTION(5) ";
         arguments.set(0, arg1);
      } else {
         this.functionName.setColumnName("TO_CHAR");
         arguments.set(0, StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString()));
      }

      if (arguments.size() == 2) {
         arg1 = arguments.get(1).toString();
         arg1 = arg1.replace("%M", "%B");
         arg1 = arg1.replace("%W", "%A");
         arg1 = arg1.replace("%h", "%I");
         arg1 = arg1.replace("%i", "%M");
         arg1 = arg1.replace("%s", "%S");
         arg1 = arg1.replace("%f", "%F50");
         arguments.set(1, arg1);
      }

      this.setFunctionArguments(arguments);
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String oldFunctionName = this.functionName.getColumnName();
      this.functionName.setColumnName("DATE_FORMAT");
      boolean isTimeFunction = false;
      if (oldFunctionName.equalsIgnoreCase("TIME_FORMAT")) {
         this.functionName.setColumnName("TIME_FORMAT");
         isTimeFunction = true;
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0) {
               if (isTimeFunction) {
                  this.handleStringLiteralForTime(from_sqs, i_count, true);
               } else {
                  this.handleStringLiteralForDateTime(from_sqs, i_count, true);
               }
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String oldFunctionName = this.functionName.getColumnName();
      Vector arguments = new Vector();

      String qry;
      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (i_count == 0 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0) instanceof String) {
            qry = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0).toString();
            if (oldFunctionName.equalsIgnoreCase("TIME_FORMAT")) {
               qry = "CAST(" + this.handleStringLiteralForTime(qry, from_sqs) + " AS TIME)";
               ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().set(0, qry);
            } else if (oldFunctionName.equalsIgnoreCase("DATE_FORMAT")) {
               qry = "CAST(" + this.handleStringLiteralForDateTime(qry, from_sqs) + " AS TIMESTAMP)";
               ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().set(0, qry);
            } else {
               qry = "CAST(" + qry + " AS STRING)";
               ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().set(0, qry);
            }
         }

         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            arguments.addElement(sc.toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String arg1 = arguments.get(1).toString();
      arg1 = arg1.replace("%s", "%S");
      arg1 = arg1.replace("%f", "%E*S");
      arg1 = arg1.replace("%M", "%B");
      arg1 = arg1.replace("%W", "%A");
      arg1 = arg1.replace("%h", "%I");
      arg1 = arg1.replace("%i", "%M");
      if (oldFunctionName.equalsIgnoreCase("DATE_FORMAT")) {
         qry = "FORMAT_TIMESTAMP(" + arg1 + ",CAST(" + arguments.get(0).toString() + " AS TIMESTAMP))";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      } else if (oldFunctionName.equalsIgnoreCase("STR_TO_DATE")) {
         Vector newarg = new Vector();
         newarg.addElement(arg1);
         newarg.addElement(arguments.get(0));
         this.functionName.setColumnName("PARSE_TIMESTAMP");
         this.setFunctionArguments(newarg);
      } else if (oldFunctionName.equalsIgnoreCase("TIME_FORMAT")) {
         qry = "FORMAT_TIME(" + arg1 + "," + arguments.get(0).toString() + " )";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String oldFunctionName = this.functionName.getColumnName();
      boolean canUseUDFFunction = false;
      if (this.functionName.getColumnName().equalsIgnoreCase("STR_TO_DATE")) {
         this.functionName.setColumnName("TO_TIMESTAMP");
      } else {
         this.functionName.setColumnName("TO_CHAR");
      }

      Vector arguments = new Vector();

      String dateOrTimeString;
      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (i_count == 0 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0) instanceof String) {
            dateOrTimeString = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0).toString();
            if (oldFunctionName.equalsIgnoreCase("TIME_FORMAT")) {
               dateOrTimeString = "CAST(" + this.handleStringLiteralForTime(dateOrTimeString, from_sqs) + " AS TIME)";
               ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().set(0, dateOrTimeString);
            } else if (oldFunctionName.equalsIgnoreCase("DATE_FORMAT")) {
               dateOrTimeString = "CAST(" + this.handleStringLiteralForDateTime(dateOrTimeString, from_sqs) + " AS TIMESTAMP)";
               ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().set(0, dateOrTimeString);
            }
         }

         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            arguments.addElement(sc.toSnowflakeSelect(to_sqs, from_sqs));
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
      if (this.functionName.getColumnName().equalsIgnoreCase("TO_DATE")) {
         dateOrTimeString = arguments.get(0).toString();
         dateOrTimeString = "cast(" + dateOrTimeString + " as text)";
         arguments.set(0, dateOrTimeString);
      }

      this.setFunctionArguments(arguments);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String oldfunName = this.functionName.getColumnName().toString();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      String qry;
      if (oldfunName.equalsIgnoreCase("STR_TO_DATE")) {
         this.functionName.setColumnName("TIMESTAMP_FORMAT");
      } else if (oldfunName.equalsIgnoreCase("FROM_UNIXTIME")) {
         arguments.set(0, "TIMESTAMP '1970-01-01 00:00:00' + CAST(" + arguments.get(0).toString() + " AS BIGINT) SECOND");
         if (arguments.size() == 2) {
            this.functionName.setColumnName("VARCHAR_FORMAT");
         } else {
            this.functionName.setColumnName("");
         }
      } else if (oldfunName.equalsIgnoreCase("TIME_FORMAT")) {
         qry = "CAST((TIMESTAMP('1970-01-01 00:00:00') + (Hour(" + arguments.get(0).toString() + ") Hours) + (Minute(" + arguments.get(0).toString() + ") Minutes) + (Second(" + arguments.get(0).toString() + ") Seconds)) AS TIMESTAMP)";
         arguments.set(0, qry);
         this.functionName.setColumnName("VARCHAR_FORMAT");
      } else {
         this.functionName.setColumnName("VARCHAR_FORMAT");
      }

      if (arguments.size() == 2) {
         qry = arguments.get(1).toString();
         qry = qry.replaceAll("%a", "Dy");
         qry = qry.replaceAll("%b", "Mon");
         qry = qry.replaceAll("%c", "MM");
         qry = qry.replaceAll("%d", "dd");
         qry = qry.replaceAll("%e", "dd");
         qry = qry.replaceAll("%f", "FF");
         qry = qry.replaceAll("%H", "HH24");
         qry = qry.replaceAll("%h", "hh");
         qry = qry.replaceAll("%I", "hh");
         qry = qry.replaceAll("%i", "MI");
         qry = qry.replaceAll("%j", "DDD");
         qry = qry.replaceAll("%k", "HH24");
         qry = qry.replaceAll("%l", "HH");
         qry = qry.replaceAll("%M", "MONTH");
         qry = qry.replaceAll("%m", "MM");
         qry = qry.replaceAll("%p", "AM");
         qry = qry.replaceAll("%r", "HH:MI:SS AM");
         qry = qry.replaceAll("%S", "SS");
         qry = qry.replaceAll("%s", "SS");
         qry = qry.replaceAll("%T", "HH:MI:SS");
         qry = qry.replaceAll("%U", "ww");
         qry = qry.replaceAll("%u", "ww");
         qry = qry.replaceAll("%V", "ww");
         qry = qry.replaceAll("%v", "ww");
         qry = qry.replaceAll("%W", "Day");
         qry = qry.replaceAll("%X", "yyyy");
         qry = qry.replaceAll("%x", "yyyy");
         qry = qry.replaceAll("%Y", "yyyy");
         qry = qry.replaceAll("%y", "yy");
         qry = qry.replace(".0", "");
         arguments.set(1, qry);
      }

      this.setFunctionArguments(arguments);
   }

   private String convertDateFormatForExcel(String dateColumn, String date_format) {
      String delimiter = "";
      String qry = "";
      date_format = date_format.replace("'", "");
      String[] formatList = date_format.split("%");
      String[] var6 = formatList;
      int var7 = formatList.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String format = var6[var8];
         format = !format.isEmpty() ? "%" + format.substring(0, 1) : format;
         delimiter = "'" + date_format.substring(date_format.indexOf(format) + 2, date_format.indexOf(format) + 3 >= date_format.length() ? date_format.indexOf(format) + 2 : date_format.indexOf(format) + 3) + "'";
         byte var11 = -1;
         switch(format.hashCode()) {
         case 1215:
            if (format.equals("%D")) {
               var11 = 4;
            }
         case 1216:
         case 1217:
         case 1218:
         case 1221:
         case 1222:
         case 1223:
         case 1225:
         case 1226:
         case 1227:
         case 1228:
         case 1229:
         case 1232:
         case 1233:
         case 1235:
         case 1237:
         case 1238:
         case 1239:
         case 1240:
         case 1241:
         case 1242:
         case 1243:
         case 1250:
         case 1253:
         case 1257:
         case 1258:
         case 1260:
         case 1263:
         case 1264:
         case 1265:
         case 1266:
         case 1267:
         default:
            break;
         case 1219:
            if (format.equals("%H")) {
               var11 = 7;
            }
            break;
         case 1220:
            if (format.equals("%I")) {
               var11 = 16;
            }
            break;
         case 1224:
            if (format.equals("%M")) {
               var11 = 15;
            }
            break;
         case 1230:
            if (format.equals("%S")) {
               var11 = 12;
            }
            break;
         case 1231:
            if (format.equals("%T")) {
               var11 = 13;
            }
            break;
         case 1234:
            if (format.equals("%W")) {
               var11 = 14;
            }
            break;
         case 1236:
            if (format.equals("%Y")) {
               var11 = 22;
            }
            break;
         case 1244:
            if (format.equals("%a")) {
               var11 = 0;
            }
            break;
         case 1245:
            if (format.equals("%b")) {
               var11 = 1;
            }
            break;
         case 1246:
            if (format.equals("%c")) {
               var11 = 2;
            }
            break;
         case 1247:
            if (format.equals("%d")) {
               var11 = 3;
            }
            break;
         case 1248:
            if (format.equals("%e")) {
               var11 = 5;
            }
            break;
         case 1249:
            if (format.equals("%f")) {
               var11 = 6;
            }
            break;
         case 1251:
            if (format.equals("%h")) {
               var11 = 8;
            }
            break;
         case 1252:
            if (format.equals("%i")) {
               var11 = 10;
            }
            break;
         case 1254:
            if (format.equals("%k")) {
               var11 = 19;
            }
            break;
         case 1255:
            if (format.equals("%l")) {
               var11 = 17;
            }
            break;
         case 1256:
            if (format.equals("%m")) {
               var11 = 9;
            }
            break;
         case 1259:
            if (format.equals("%p")) {
               var11 = 18;
            }
            break;
         case 1261:
            if (format.equals("%r")) {
               var11 = 20;
            }
            break;
         case 1262:
            if (format.equals("%s")) {
               var11 = 11;
            }
            break;
         case 1268:
            if (format.equals("%y")) {
               var11 = 21;
            }
         }

         switch(var11) {
         case 0:
            qry = qry + "Format(" + dateColumn + ",'ddd') & " + delimiter + " & ";
            break;
         case 1:
            qry = qry + "Format(" + dateColumn + ",'mmm') & " + delimiter + " & ";
            break;
         case 2:
            qry = qry + "Format(" + dateColumn + ",'mm') & " + delimiter + " & ";
            break;
         case 3:
         case 4:
         case 5:
            qry = qry + "Format(" + dateColumn + ",'dd') & " + delimiter + " & ";
            break;
         case 6:
            qry = qry + "Iif(InStrRev(CStr(" + dateColumn + "),'.') > 0, CLng(Mid(CStr(" + dateColumn + "),InStrRev(CStr(" + dateColumn + "),'.')+1)*(10^(6-Len (Mid(CStr(" + dateColumn + "),InStrRev(CStr(" + dateColumn + "),'.')+1))))) , 0) & " + delimiter + " & ";
            break;
         case 7:
            qry = qry + "Format(" + dateColumn + ",'hh') & " + delimiter + " & ";
            break;
         case 8:
            qry = qry + "Iif((hour(" + dateColumn + ") mod 12) = 0, '12', Iif((hour(" + dateColumn + ") mod 12) < 10, ('0' & hour(" + dateColumn + ") mod 12), hour(" + dateColumn + ") mod 12)) & " + delimiter + " & ";
            break;
         case 9:
            qry = qry + "Format(" + dateColumn + ",'mm') & " + delimiter + " & ";
            break;
         case 10:
            qry = qry + "Format(" + dateColumn + ",'nn') & " + delimiter + " & ";
            break;
         case 11:
         case 12:
            qry = qry + "Format(" + dateColumn + ",'ss') & " + delimiter + " & ";
            break;
         case 13:
            qry = qry + "HOUR(" + dateColumn + ") & ':' & Iif(MINUTE(" + dateColumn + ") < 10 , 0 & MINUTE(" + dateColumn + "), MINUTE(" + dateColumn + ")) & ':' &  Iif(SECOND(" + dateColumn + ") < 10 , 0 & SECOND(" + dateColumn + "), SECOND(" + dateColumn + ")) & " + delimiter + " & ";
            break;
         case 14:
            qry = qry + "WeekdayName(Weekday(" + dateColumn + ")) & " + delimiter + " & ";
            break;
         case 15:
            qry = qry + "Format(" + dateColumn + ",'mmmm') & " + delimiter + " & ";
            break;
         case 16:
         case 17:
            qry = qry + "Iif((hour(" + dateColumn + ") mod 12) = 0, 12, (hour(" + dateColumn + ") mod 12)) & " + delimiter + " & ";
            break;
         case 18:
            qry = qry + "Iif(hour(" + dateColumn + ") = 24,'AM', Iif(hour(" + dateColumn + ") >= 12, 'PM','AM')) & " + delimiter + " & ";
            break;
         case 19:
         case 20:
            qry = qry + "(Iif((hour(" + dateColumn + ") mod 12) = 0, 12, Iif((hour(" + dateColumn + ") mod 12) < 10,'0' & (hour(" + dateColumn + ") mod 12),hour(" + dateColumn + ") mod 12)) & ':' & month(" + dateColumn + ") & ':' & second(" + dateColumn + ") & ' ' & (Iif(hour(" + dateColumn + ") = 24,'AM', Iif(hour(" + dateColumn + ") >= 12, 'PM', 'AM')))) & " + delimiter + " & ";
            break;
         case 21:
            qry = qry + "Format(" + dateColumn + ",'yy') & " + delimiter + " & ";
            break;
         case 22:
            qry = qry + "DatePart('yyyy'," + dateColumn + ") & " + delimiter + " & ";
         }
      }

      return qry + " ''";
   }

   static {
      dateFormats.put("%c", "DATEPART(month,");
      dateFormats.put("%d", "DATEPART(day,");
      dateFormats.put("%e", "DATEPART(day,");
      dateFormats.put("%H", "DATEPART(hour,");
      dateFormats.put("%i", "DATEPART(minute,");
      dateFormats.put("%j", "DATEPART(dayofyear,");
      dateFormats.put("%k", "DATEPART(hour,");
      dateFormats.put("%M", "DATEPART(month,");
      dateFormats.put("%m", "DATEPART(month,");
      dateFormats.put("%s", "DATEPART(second,");
      dateFormats.put("%S", "DATEPART(second,");
      dateFormats.put("%U", "DATEPART(week,");
      dateFormats.put("%X", "DATEPART(year,");
      dateFormats.put("%x", "DATEPART(year,");
      dateFormats.put("%Y", "DATEPART(year,");
   }
}
