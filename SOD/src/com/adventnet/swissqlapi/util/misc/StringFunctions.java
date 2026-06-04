package com.adventnet.swissqlapi.util.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

public class StringFunctions {
   private static ThreadLocal dateFormat = new ThreadLocal() {
      public SimpleDateFormat initialValue() {
         return new SimpleDateFormat("yyyy-MM-dd");
      }
   };
   private static ThreadLocal dateTimeFormat = new ThreadLocal() {
      public SimpleDateFormat initialValue() {
         return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      }
   };
   private static final String[] formats = new String[]{"yyyy-MM-dd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS", "yyyy/MM/dd HH:mm:ss.SSS"};

   public static SimpleDateFormat getDateFormat() {
      return (SimpleDateFormat)dateFormat.get();
   }

   public static SimpleDateFormat getDateTimeFormat() {
      return (SimpleDateFormat)dateTimeFormat.get();
   }

   public static String replaceFirst(String replaceWith, String replaceThis, String original) {
      StringBuffer sb = new StringBuffer(original);
      int lengthOfReplaceThis = replaceThis.length();
      int indexOfReplaceThis = original.indexOf(replaceThis);
      if (indexOfReplaceThis >= 0) {
         sb = sb.replace(indexOfReplaceThis, indexOfReplaceThis + lengthOfReplaceThis, replaceWith);
      }

      return sb.toString();
   }

   public static String replaceAll(String replaceWith, String replaceThis, String original) {
      String replacedString = original;
      StringBuffer sb = new StringBuffer();

      while(true) {
         int index = replacedString.indexOf(replaceThis);
         if (index == -1) {
            if (replacedString.length() > 0) {
               sb.append(replacedString);
            }

            return sb.toString();
         }

         sb.append(replacedString.substring(0, index) + replaceWith);
         replacedString = replacedString.substring(index + replaceThis.length());
      }
   }

   public static boolean isLowerCase(String s) {
      if (s == null) {
         return false;
      } else {
         int l = s.length();

         for(int i = 0; i < l; ++i) {
            if (!Character.isDigit(s.charAt(i)) && s.charAt(i) != '_' && !Character.isLowerCase(s.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isUpperCase(String s) {
      if (s == null) {
         return false;
      } else {
         int l = s.length();

         for(int i = 0; i < l; ++i) {
            if (!Character.isDigit(s.charAt(i)) && s.charAt(i) != '_' && !Character.isUpperCase(s.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static String getLastStrToken(String str, String delim) {
      String[] split = str.split(delim);
      return split[split.length - 1];
   }

   public static String convertToAnsiTimeLiteral(String argument, boolean canHandleStringLiterals, boolean forToChar) {
      String resultString = argument;
      if (canHandleStringLiterals) {
         resultString = convertToAnsiDateFormatIfDateLiteralString(argument, false, true, forToChar);
         if (resultString.equalsIgnoreCase("NULL")) {
            resultString = convertToAnsiTimeFormatIfTimeLiteralString(argument, false, true, forToChar);
         }
      }

      return resultString;
   }

   public static String convertToAnsiDateLiteral(String argument, boolean canHandleStringLiterals) {
      String resultString = argument;
      if (canHandleStringLiterals) {
         resultString = convertToAnsiDateFormatIfDateLiteralString(argument, false, true, false);
      }

      return resultString;
   }

   public static String convertToAnsiDateFormatIfDateLiteralString(String argument, boolean isCoalesceWithNull) {
      return convertToAnsiDateFormatIfDateLiteralString(argument, isCoalesceWithNull, false, false);
   }

   private static String convertToAnsiDateFormatIfDateLiteralString(String argument, boolean isCoalesceWithNull, boolean canHandleStringLiterals, boolean forAnsiTime) {
      String resultString = argument;
      if (argument.trim().startsWith("'") && argument.trim().endsWith("'")) {
         Pattern pattern = Pattern.compile("^'\\d{1,4}/\\d{1,2}/\\d{1,4}(\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}(.\\d{1,6})?)?'$");
         Pattern pattern2 = Pattern.compile("^'\\d{1,4}-\\d{1,2}-\\d{1,2}(\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}(.\\d{1,6})?)?'$");

         try {
            boolean slashPattern = pattern.matcher(argument.trim()).matches();
            boolean hyphenPattern = pattern2.matcher(argument.trim()).matches();
            if (!slashPattern && !hyphenPattern) {
               if (canHandleStringLiterals) {
                  resultString = "NULL";
               }
            } else {
               String splitString = hyphenPattern ? "-" : "/";
               String[] arg = argument.replaceAll("'", "").split("\\s+");
               String datepart = arg[0].trim();
               String timepart = arg.length == 2 ? arg[1].trim() : "";
               String timewithoutmicropart = "";
               boolean isDateTimeFormat = false;
               boolean isNULL = false;
               if (timepart != null && !timepart.isEmpty()) {
                  timepart = convertToAnsiTimeFormatIfTimeLiteralString("'" + timepart + "'", false, canHandleStringLiterals, false);
                  if (timepart.equalsIgnoreCase("NULL")) {
                     isNULL = true;
                  } else {
                     timepart = " " + timepart.replaceAll("'", "");
                     timewithoutmicropart = timepart.split("[.]")[0];
                     isDateTimeFormat = true;
                  }
               } else if (forAnsiTime) {
                  timepart = " 00:00:00";
               }

               String[] arr = datepart.split(splitString);
               if (!isNULL && arr != null && arr.length == 3) {
                  int year = false;
                  int month = false;
                  int day = false;
                  String yearStr = "";
                  String monthStr = "";
                  String dayStr = "";

                  try {
                     int year = Integer.parseInt(arr[0]);
                     int month = Integer.parseInt(arr[1]);
                     int day = Integer.parseInt(arr[2]);
                     if (year == 0 && month == 0 && day == 0) {
                        isNULL = true;
                     } else if ((year > 0 || arr[0].equalsIgnoreCase("00")) && month > 0 && day > 0) {
                        if (month <= 12 && day <= 31) {
                           if (year == 0) {
                              yearStr = "2000";
                           } else if (year <= 9) {
                              yearStr = "000" + year;
                           } else if (year >= 10 && year <= 69) {
                              yearStr = "20" + year;
                           } else if (year >= 70 && year <= 99) {
                              yearStr = "19" + year;
                           } else if (year < 1000) {
                              yearStr = "0" + year;
                           } else {
                              yearStr = String.valueOf(year);
                           }

                           if (month <= 9) {
                              monthStr = "0" + month;
                           } else {
                              monthStr = String.valueOf(month);
                           }

                           if (day <= 9) {
                              dayStr = "0" + day;
                           } else {
                              dayStr = String.valueOf(day);
                           }

                           String tempStr = "" + yearStr + "-" + monthStr + "-" + dayStr;
                           String tempwithoutmicroStr = tempStr + timewithoutmicropart;
                           tempStr = tempStr + timepart;

                           try {
                              Date date = isDateTimeFormat ? getDateTimeFormat().parse(tempwithoutmicroStr) : getDateFormat().parse(tempwithoutmicroStr);
                              String result = isDateTimeFormat ? getDateTimeFormat().format(date) : getDateFormat().format(date);
                              if (!result.equals(tempwithoutmicroStr)) {
                                 isNULL = true;
                              } else {
                                 resultString = "'" + tempStr + "'";
                              }
                           } catch (ParseException var27) {
                              isNULL = true;
                           }
                        } else {
                           isNULL = true;
                        }
                     }
                  } catch (Exception var28) {
                  }
               }

               if (isNULL) {
                  if (isCoalesceWithNull) {
                     resultString = "COALESCE(NULL)";
                  } else {
                     resultString = "NULL";
                  }
               }
            }
         } catch (Exception var29) {
         }
      }

      return resultString;
   }

   private static String convertToAnsiTimeFormatIfTimeLiteralString(String argument, boolean isCoalesceWithNull, boolean canHandleStringLiterals, boolean forToChar) {
      String resultString = argument;
      if (argument.trim().startsWith("'") && argument.trim().endsWith("'")) {
         Pattern pattern = Pattern.compile("^'\\d{1,2}:\\d{1,2}:\\d{1,2}(.\\d+)?'$");

         try {
            if (pattern.matcher(argument.trim()).matches()) {
               String[] array = argument.trim().replaceAll("'", "").split("[.]");
               String microStr = "";
               String microsecondStr = "0";
               if (array.length == 2) {
                  microsecondStr = array[1];
                  microStr = "." + microsecondStr;
               }

               String[] arr = array[0].trim().split(":");
               boolean isNULL = false;
               if (arr != null && arr.length == 3) {
                  int hour = false;
                  int minute = false;
                  int second = false;
                  int microsecond = false;
                  String hourStr = "";
                  String minuteStr = "";
                  String secondStr = "";

                  try {
                     int hour = Integer.parseInt(arr[0].toString());
                     int minute = Integer.parseInt(arr[1].toString());
                     int second = Integer.parseInt(arr[2].toString());
                     int microsecond = Integer.parseInt(microsecondStr);
                     if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59 && second >= 0 && second <= 59 && microsecond >= 0 && microsecond <= 999999) {
                        hourStr = String.valueOf(hour);
                        minuteStr = String.valueOf(minute);
                        secondStr = String.valueOf(second);
                        if (hour <= 9) {
                           hourStr = "0" + hourStr;
                        }

                        if (minute <= 9) {
                           minuteStr = "0" + minuteStr;
                        }

                        if (second <= 9) {
                           secondStr = "0" + secondStr;
                        }

                        String dateStr = "";
                        if (forToChar) {
                           dateStr = "0001-01-01 ";
                        }

                        resultString = "'" + dateStr + hourStr + ":" + minuteStr + ":" + secondStr + microStr + "'";
                     } else {
                        isNULL = true;
                     }

                     if (isNULL) {
                        if (isCoalesceWithNull) {
                           resultString = "COALESCE(NULL)";
                        } else {
                           resultString = "NULL";
                        }
                     }
                  } catch (Exception var19) {
                  }
               }
            } else if (canHandleStringLiterals) {
               resultString = "NULL";
            }
         } catch (Exception var20) {
         }
      }

      return resultString;
   }

   public static boolean isNumericValue(String argument) {
      boolean numericBool = false;

      try {
         Pattern numberPattern = Pattern.compile("'(-)?[0-9]+(.[0-9]+)?'");
         if (numberPattern.matcher(argument.trim()).matches()) {
            numericBool = true;
         }
      } catch (Exception var3) {
      }

      return numericBool;
   }

   public static boolean isDateTimeValue(String argument) {
      boolean dateTimeBool = false;

      try {
         Pattern pattern = Pattern.compile("^'\\d{1,4}/\\d{1,2}/\\d{1,4}(\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}(.\\d{1,6})?)?'$");
         Pattern pattern2 = Pattern.compile("^'\\d{1,4}-\\d{1,2}-\\d{1,2}(\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}(.\\d{1,6})?)?'$");
         if (pattern.matcher(argument.trim()).matches() || pattern2.matcher(argument.trim()).matches()) {
            dateTimeBool = true;
         }
      } catch (Exception var4) {
      }

      return dateTimeBool;
   }

   public static String getDecimalString(String argument, String defaultString) {
      String decimalStr = null;
      Pattern pattern = Pattern.compile("(-)?[0-9]+(.[0-9]+)?");
      if (pattern.matcher(argument).matches()) {
         try {
            int value = Integer.parseInt(argument);
            if (Math.abs(value) > 0) {
               decimalStr = value + ".0";
               return decimalStr;
            }
         } catch (Exception var8) {
         }

         try {
            long value = Long.parseLong(argument);
            if (Math.abs(value) > 0L) {
               return defaultString;
            }
         } catch (Exception var7) {
         }

         try {
            double value = Double.parseDouble(argument);
            if (Math.abs(value) > 0.0D) {
               return argument;
            }
         } catch (Exception var6) {
         }
      }

      return defaultString;
   }

   public static String castMultiplicationOperand(String str) {
      boolean isEnclosedWithSingleQuotes = false;
      if (str.startsWith("'") && str.endsWith("'")) {
         str = str.substring(1, str.length() - 1);
         isEnclosedWithSingleQuotes = true;
      }

      try {
         int value = Integer.parseInt(str);
         if (isEnclosedWithSingleQuotes) {
            str = "('" + value + "' :: bigint)";
         } else {
            str = "(" + value + " :: bigint)";
         }
      } catch (Exception var6) {
      }

      try {
         long value = Long.parseLong(str);
         if (isEnclosedWithSingleQuotes) {
            str = "('" + value + "' :: bigint)";
         } else {
            str = "(" + value + " :: bigint)";
         }
      } catch (Exception var5) {
      }

      try {
         double value = Double.parseDouble(str);
         if (isEnclosedWithSingleQuotes) {
            str = "('" + value + "' :: double precision)";
         } else {
            str = "(" + value + " :: double precision)";
         }
      } catch (Exception var4) {
      }

      return str;
   }

   public static Integer getIntegerValue(String numberString) {
      Integer value = null;
      if (numberString != null && !numberString.isEmpty()) {
         numberString = numberString.replaceAll("'", "").trim();

         try {
            value = Integer.parseInt(numberString);
            return value;
         } catch (Exception var5) {
            try {
               double indexd = Double.parseDouble(numberString);
               value = (int)Math.round(indexd);
               return value;
            } catch (Exception var4) {
               value = null;
            }
         }
      }

      return null;
   }

   public static Vector getStringFunctionsListForCasting() {
      Vector list = new Vector();
      list.add("CHAR");
      list.add("CONCAT");
      list.add("CONCAT_WS");
      list.add("LPAD");
      list.add("LEFT");
      list.add("RPAD");
      list.add("RIGHT");
      list.add("LTRIM");
      list.add("RTRIM");
      list.add("REPEAT");
      list.add("REPLACE");
      list.add("REVERSE");
      list.add("SOUNDEX");
      list.add("SPACE");
      list.add("STRCMP");
      list.add("SUBSTR");
      list.add("SUBSTRING");
      list.add("SUBSTRING_INDEX");
      list.add("TRIM");
      list.add("UPPER");
      list.add("LOWER");
      list.add("LCASE");
      list.add("UCASE");
      list.add("INSERT");
      list.add("MID");
      list.add("MD5");
      return list;
   }

   public static boolean isCastingRequiredForFunction(String functionName) {
      List<String> functionList = Arrays.asList("CONCAT", "CONCAT_WS", "LPAD", "LEFT", "RPAD", "RIGHT", "LTRIM", "RTRIM", "REPEAT", "REPLACE", "REVERSE", "SOUNDEX", "SPACE", "STRCMP", "SUBSTR", "SUBSTRING", "SUBSTRING_INDEX", "TRIM", "UPPER", "LOWER", "LCASE", "UCASE", "INSERT", "MID");
      Iterator var2 = functionList.iterator();

      String function;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         function = (String)var2.next();
      } while(!functionName.startsWith(function));

      return true;
   }

   public static String getStringDataType(SelectQueryStatement sqs) {
      return sqs != null ? (sqs.getBooleanValues("can.use.citext.over.text") ? "CITEXT" : (sqs.isVerticaDb() ? "VARCHAR" : "TEXT")) : "TEXT";
   }

   public static String identifyDateFormate(String date) {
      if (date != null) {
         String[] var1 = formats;
         int var2 = var1.length;
         int var3 = 0;

         while(var3 < var2) {
            String parse = var1[var3];

            try {
               DateTimeFormatter formatter = DateTimeFormatter.ofPattern(parse);
               LocalDate.parse(date, formatter);
               return parse;
            } catch (Exception var7) {
               ++var3;
            }
         }
      }

      return null;
   }

   public static String identifyTimeFormate(String date, String[] formats) {
      if (date != null) {
         String[] var2 = formats;
         int var3 = formats.length;
         int var4 = 0;

         while(var4 < var3) {
            String parse = var2[var4];

            try {
               DateTimeFormatter formatter = DateTimeFormatter.ofPattern(parse);
               LocalTime.parse(date, formatter);
               return parse;
            } catch (Exception var8) {
               ++var4;
            }
         }
      }

      return null;
   }

   public static String handleLiteralStringDateForOracle(String date) throws ConvertException {
      String date1 = date.replaceAll("\\(", "").replace(")", "");
      if (date1.trim().startsWith("'") && date1.trim().endsWith("'")) {
         String datevalue = date1.trim().replaceAll("'", "");
         String dateformat = identifyDateFormate(datevalue);
         if (dateformat != null) {
            dateformat = dateformat.replace("mm", "mi").replace("HH", "HH24");
            if (dateformat.contains("SSS")) {
               dateformat = dateformat.replace("SSS", "FF");
               return "TO_TIMESTAMP('" + datevalue + "', '" + dateformat + "')";
            } else {
               return "TO_DATE('" + datevalue + "', '" + dateformat + "')";
            }
         } else {
            throw new ConvertException("Date format not supported, Please give in formats : " + Arrays.toString(formats));
         }
      } else {
         return date;
      }
   }

   public static String handleLiteralStringDateForAthena(String date) throws ConvertException {
      String date1 = date.replaceAll("\\(", "").replace(")", "");
      if (date1.trim().startsWith("'") && date1.trim().endsWith("'")) {
         String datevalue = date1.trim().replaceAll("'", "");
         String dateformat = identifyDateFormate(datevalue);
         if (dateformat != null) {
            dateformat = dateformat.replace("yyyy", "%Y").replace("MM", "%m").replace("dd", "%d").replace("HH", "%H").replace("mm", "%i").replace("ss", "%s").replace("SSS", "%f");
            return "date_parse('" + datevalue + "', '" + dateformat + "')";
         } else {
            throw new ConvertException("Date format not supported, Please give in formats : " + Arrays.toString(formats));
         }
      } else {
         return date;
      }
   }

   public static String handleLiteralStringDateForInformix(String date) throws ConvertException {
      String date1 = date.replaceAll("\\(", "").replace(")", "");
      if (date1.trim().startsWith("'") && date1.trim().endsWith("'")) {
         String datevalue = date1.trim().replaceAll("'", "");
         String dateformat = identifyDateFormate(datevalue);
         if (dateformat != null) {
            dateformat = dateformat.replace("yyyy", "%Y").replace("MM", "%m").replace("dd", "%d").replace("HH", "%H").replace("mm", "%M").replace("ss", "%S").replace("SSS", "%F3");
            return "TO_DATE('" + datevalue + "', '" + dateformat + "')";
         } else {
            throw new ConvertException("Date format not supported, Please give in formats : " + Arrays.toString(formats));
         }
      } else {
         return date;
      }
   }

   public static String handleLiteralStringDateForSapHana(String date) throws ConvertException {
      String date1 = date.replaceAll("\\(", "").replace(")", "");
      if (date1.trim().startsWith("'") && date1.trim().endsWith("'")) {
         String datevalue = date1.trim().replaceAll("'", "");
         String dateformat = identifyDateFormate(datevalue);
         if (dateformat != null) {
            dateformat = dateformat.replace("mm", "mi").replace("HH", "HH24");
            if (!dateformat.contains("SSS") && !dateformat.contains("HH24") && !dateformat.contains("mi") && !dateformat.contains("ss")) {
               return "TO_DATE('" + datevalue + "', '" + dateformat + "')";
            } else {
               dateformat = dateformat.replace("SSS", "FF6");
               return "TO_TIMESTAMP('" + datevalue + "', '" + dateformat + "')";
            }
         } else {
            throw new ConvertException("Date format not supported, Please give in formats : " + Arrays.toString(formats));
         }
      } else {
         return date;
      }
   }

   public static String handleLiteralStringDateForSqlite(String date) throws ConvertException {
      String date1 = date.replaceAll("\\(", "").replace(")", "");
      if (date1.trim().startsWith("'") && date1.trim().endsWith("'")) {
         String datevalue = date1.trim().replaceAll("'", "").replaceAll("/", "-");
         String dateformat = identifyDateFormate(datevalue);
         if (dateformat != null) {
            dateformat = dateformat.replace("yyyy", "%Y").replace("MM", "%m").replace("dd", "%d").replace("HH", "%H").replace("mm", "%M").replace("ss", "%S");
            if (dateformat.contains("SSS")) {
               dateformat = dateformat.replace("%S.", "").replace("SSS", "%f");
            }

            return "strftime('" + dateformat + "', '" + datevalue + "')";
         } else {
            throw new ConvertException("Date format not supported, Please give in formats : " + Arrays.toString(new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS"}));
         }
      } else {
         return date;
      }
   }

   public static String handleLiteralStringDateForDenodo(String date) throws ConvertException {
      String date1 = date.replaceAll("\\(", "").replace(")", "");
      if (date1.trim().startsWith("'") && date1.trim().endsWith("'")) {
         String datevalue = date1.trim().replaceAll("'", "");
         String dateformat = identifyDateFormate(datevalue);
         if (dateformat != null) {
            return !dateformat.contains("HH") && !dateformat.contains("mm") && !dateformat.contains("ss") ? "TO_LOCALDATE('" + dateformat + "','" + datevalue + "')" : "TO_TIMESTAMP('" + dateformat + "','" + datevalue + "')";
         } else {
            dateformat = identifyTimeFormate(datevalue, new String[]{"HH:mm:ss", "HH:mm:ss.S", "HH:mm:ss.SS", "HH:mm:ss.SSS", "HH:mm:ss.SSSS", "HH:mm:ss.SSSSS", "HH:mm:ss.SSSSSS", "yyyy-MM-dd HH:mm:ss.S", "yyyy-MM-dd HH:mm:ss.SS", "yyyy-MM-dd HH:mm:ss.SSSS", "yyyy-MM-dd HH:mm:ss.SSSSS", "yyyy-MM-dd HH:mm:ss.SSSSSS"});
            if (dateformat != null) {
               return "TO_TIMESTAMP('" + dateformat + "','" + datevalue + "')";
            } else {
               throw new ConvertException("Date format not supported, Please give in formats : " + Arrays.toString(new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS"}));
            }
         }
      } else {
         return date;
      }
   }

   public static String handleLiteralStringDateForHyperSql(String date) throws ConvertException {
      String date1 = date.replaceAll("\\(", "").replace(")", "");
      if (date1.trim().startsWith("'") && date1.trim().endsWith("'")) {
         String datevalue = date1.trim().replaceAll("'", "");
         String dateformat = identifyDateFormate(datevalue);
         if (dateformat != null) {
            dateformat = dateformat.replace("HH", "HH24").replace("mm", "mi").replace("SSS", "ff");
            return dateformat.contains("mi") ? "TO_TIMESTAMP('" + datevalue + "','" + dateformat + "')" : "TO_DATE('" + datevalue + "','" + dateformat + "')";
         } else {
            return "TIMESTAMP('" + datevalue + "')";
         }
      } else {
         return date;
      }
   }

   public static String handleLiteralStringDateForExcel(String date) throws ConvertException {
      String date1 = date.replaceAll("\\(", "").replace(")", "");
      if (date1.trim().startsWith("'") && date1.trim().endsWith("'")) {
         String datevalue = date1.trim().replaceAll("'", "");
         String dateformat = identifyDateFormate(datevalue);
         if (dateformat != null) {
            return !dateformat.contains("HH") && !dateformat.contains("mm") && !dateformat.contains("ss") ? "CDate(" + datevalue + ")" : "CDate('1970-01-01 " + datevalue + "')";
         } else {
            dateformat = identifyTimeFormate(datevalue, new String[]{"HH:mm:ss", "HH:mm:ss.S", "HH:mm:ss.SS", "HH:mm:ss.SSS", "HH:mm:ss.SSSS", "HH:mm:ss.SSSSS", "HH:mm:ss.SSSSSS", "yyyy-MM-dd HH:mm:ss.S", "yyyy-MM-dd HH:mm:ss.SS", "yyyy-MM-dd HH:mm:ss.SSSS", "yyyy-MM-dd HH:mm:ss.SSSSS", "yyyy-MM-dd HH:mm:ss.SSSSSS"});
            if (dateformat != null) {
               return "CDate('1970-01-01 " + datevalue + "')";
            } else {
               throw new ConvertException("Date format not supported, Please give in formats : " + Arrays.toString(new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS"}));
            }
         }
      } else {
         return date;
      }
   }
}
