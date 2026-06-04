package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

public class dateadd extends FunctionCalls {
   private String dateFormatString = null;
   private String monthFormat = null;
   private String yearFormat = null;
   private int yearSize;

   public String yearFormat(int num) {
      this.yearSize = num;
      switch(this.yearSize) {
      case 1:
         this.yearFormat = "Y";
         break;
      case 2:
         this.yearFormat = "YY";
      case 3:
      default:
         break;
      case 4:
         this.yearFormat = "YYYY";
      }

      return this.yearFormat;
   }

   public String dateFormatConversion(String str) {
      int setFormat = 0;
      this.dateFormatString = str;
      this.dateFormatString = this.dateFormatString.trim();
      int length = this.dateFormatString.length();
      new StringBuffer();
      new StringBuffer();
      int indexOfHiphen = this.dateFormatString.indexOf(45);
      int indexOfSlash = this.dateFormatString.indexOf(47);
      ArrayList stringArrayForSlash = new ArrayList();
      StringTokenizer stringTokenForSlash = new StringTokenizer(this.dateFormatString, "/ '");

      while(stringTokenForSlash.hasMoreTokens()) {
         stringArrayForSlash.add(stringTokenForSlash.nextToken());
      }

      ArrayList stringArrayForHiphen = new ArrayList();
      StringTokenizer stringTokenForHiphen = new StringTokenizer(this.dateFormatString, "- '");

      while(stringTokenForHiphen.hasMoreTokens()) {
         stringArrayForHiphen.add(stringTokenForHiphen.nextToken());
      }

      String formatString = "";
      String monthString;
      String year;
      String yearString;
      String yearString;
      int yearLength;
      String date;
      int yearLength;
      String date;
      if (indexOfHiphen != -1) {
         if (stringArrayForHiphen.size() == 3) {
            monthString = (String)stringArrayForHiphen.get(1);
            monthString = monthString.trim();
            year = null;
            if (!monthString.equalsIgnoreCase("Jan") && !monthString.equalsIgnoreCase("feb") && !monthString.equalsIgnoreCase("mar") && !monthString.equalsIgnoreCase("apr") && !monthString.equalsIgnoreCase("may") && !monthString.equalsIgnoreCase("jun") && !monthString.equalsIgnoreCase("jul") && !monthString.equalsIgnoreCase("aug") && !monthString.equalsIgnoreCase("sep") && !monthString.equalsIgnoreCase("oct") && !monthString.equalsIgnoreCase("nov") && !monthString.equalsIgnoreCase("dec")) {
               if (((String)stringArrayForHiphen.get(0)).trim().length() == 4) {
                  setFormat = 2;
                  yearString = (String)stringArrayForHiphen.get(0);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForHiphen.get(1);
                  monthString = monthString.trim();
                  date = (String)stringArrayForHiphen.get(2);
                  date = date.trim();
                  formatString = yearString + "-" + monthString + "-" + date;
               } else if (((String)stringArrayForHiphen.get(2)).trim().length() == 4) {
                  setFormat = 1;
                  yearString = (String)stringArrayForHiphen.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForHiphen.get(0);
                  monthString = monthString.trim();
                  date = (String)stringArrayForHiphen.get(1);
                  date = date.trim();
                  formatString = monthString + "-" + date + "-" + yearString;
               } else if (((String)stringArrayForHiphen.get(1)).trim().length() == 4) {
                  setFormat = 3;
                  yearString = (String)stringArrayForHiphen.get(1);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForHiphen.get(0);
                  monthString = monthString.trim();
                  date = (String)stringArrayForHiphen.get(2);
                  date = date.trim();
                  formatString = monthString + "-" + yearString + "-" + date;
               } else if (((String)stringArrayForHiphen.get(2)).trim().length() == 2 || ((String)stringArrayForHiphen.get(2)).trim().length() == 1) {
                  setFormat = 1;
                  yearString = (String)stringArrayForHiphen.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForHiphen.get(0);
                  monthString = monthString.trim();
                  date = (String)stringArrayForHiphen.get(1);
                  date = date.trim();
                  formatString = monthString + "-" + date + "-" + yearString;
               }
            } else {
               yearString = this.monthFormat(monthString);
               if (((String)stringArrayForHiphen.get(2)).length() == 4) {
                  setFormat = 1;
                  yearString = (String)stringArrayForHiphen.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  date = (String)stringArrayForHiphen.get(0);
                  date = date.trim();
                  formatString = yearString + "-" + date + "-" + yearString;
               } else if (((String)stringArrayForHiphen.get(0)).trim().length() == 4) {
                  setFormat = 2;
                  yearString = (String)stringArrayForHiphen.get(0);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  date = (String)stringArrayForHiphen.get(2);
                  date = date.trim();
                  formatString = yearString + "-" + yearString + "-" + date;
               } else if (((String)stringArrayForHiphen.get(2)).trim().length() == 2 || ((String)stringArrayForHiphen.get(2)).trim().length() == 1) {
                  setFormat = 1;
                  yearString = (String)stringArrayForHiphen.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  date = (String)stringArrayForHiphen.get(0);
                  date = date.trim();
                  formatString = yearString + "-" + date + "-" + yearString;
               }
            }

            if (setFormat == 1) {
               formatString = "'" + formatString + "'" + " ," + "'MM-DD-" + year + "')";
            } else if (setFormat == 2) {
               formatString = "'" + formatString + "'" + " ," + "'" + year + "-MM-DD" + "')";
            } else if (setFormat == 3) {
               formatString = "'" + formatString + "'" + " ," + "'MM-" + year + "-DD" + "')";
            }
         } else if (stringArrayForHiphen.size() == 4) {
            monthString = (String)stringArrayForHiphen.get(1);
            monthString = monthString.trim();
            year = null;
            if (!monthString.equalsIgnoreCase("Jan") && !monthString.equalsIgnoreCase("feb") && !monthString.equalsIgnoreCase("mar") && !monthString.equalsIgnoreCase("apr") && !monthString.equalsIgnoreCase("may") && !monthString.equalsIgnoreCase("jun") && !monthString.equalsIgnoreCase("jul") && !monthString.equalsIgnoreCase("aug") && !monthString.equalsIgnoreCase("sep") && !monthString.equalsIgnoreCase("oct") && !monthString.equalsIgnoreCase("nov") && !monthString.equalsIgnoreCase("dec")) {
               if (((String)stringArrayForHiphen.get(0)).trim().length() == 4) {
                  setFormat = 2;
                  yearString = (String)stringArrayForHiphen.get(0);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForHiphen.get(1);
                  monthString = monthString.trim();
                  date = (String)stringArrayForHiphen.get(2);
                  date = date.trim();
                  formatString = yearString + "-" + monthString + "-" + date;
               } else if (((String)stringArrayForHiphen.get(2)).trim().length() == 4) {
                  setFormat = 1;
                  yearString = (String)stringArrayForHiphen.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForHiphen.get(0);
                  monthString = monthString.trim();
                  date = (String)stringArrayForHiphen.get(1);
                  date = date.trim();
                  formatString = monthString + "-" + date + "-" + yearString;
               } else if (((String)stringArrayForHiphen.get(1)).trim().length() == 4) {
                  setFormat = 3;
                  yearString = (String)stringArrayForHiphen.get(1);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForHiphen.get(0);
                  monthString = monthString.trim();
                  date = (String)stringArrayForHiphen.get(2);
                  date = date.trim();
                  formatString = monthString + "-" + yearString + "-" + date;
               } else if (((String)stringArrayForHiphen.get(2)).trim().length() == 2 || ((String)stringArrayForHiphen.get(2)).trim().length() == 1) {
                  setFormat = 1;
                  yearString = (String)stringArrayForHiphen.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForHiphen.get(0);
                  monthString = monthString.trim();
                  date = (String)stringArrayForHiphen.get(1);
                  date = date.trim();
                  formatString = monthString + "-" + date + "-" + yearString;
               }
            } else {
               yearString = this.monthFormat(monthString);
               if (((String)stringArrayForHiphen.get(2)).length() == 4) {
                  setFormat = 1;
                  yearString = (String)stringArrayForHiphen.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  date = (String)stringArrayForHiphen.get(0);
                  date = date.trim();
                  formatString = yearString + "-" + date + "-" + yearString;
               } else if (((String)stringArrayForHiphen.get(0)).trim().length() == 4) {
                  setFormat = 2;
                  yearString = (String)stringArrayForHiphen.get(0);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  date = (String)stringArrayForHiphen.get(2);
                  date = date.trim();
                  formatString = yearString + "-" + yearString + "-" + date;
               } else if (((String)stringArrayForHiphen.get(2)).trim().length() == 2 || ((String)stringArrayForHiphen.get(2)).trim().length() == 1) {
                  setFormat = 1;
                  yearString = (String)stringArrayForHiphen.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  date = (String)stringArrayForHiphen.get(0);
                  date = date.trim();
                  formatString = yearString + "-" + date + "-" + yearString;
               }
            }

            formatString = formatString + " " + stringArrayForHiphen.get(3);
            if (setFormat == 1) {
               formatString = "'" + formatString + "'" + " ," + "'MM-DD-" + year + " HH24:MI:SS')";
            } else if (setFormat == 2) {
               formatString = "'" + formatString + "'" + " ," + "'" + year + "-MM-DD" + " HH24:MI:SS')";
            } else if (setFormat == 3) {
               formatString = "'" + formatString + "'" + " ," + "'MM-" + year + "-DD" + " HH24:MI:SS')";
            }
         }
      } else if (indexOfSlash != -1) {
         if (stringArrayForSlash.size() == 3) {
            monthString = (String)stringArrayForSlash.get(1);
            monthString = monthString.trim();
            year = null;
            if (!monthString.equalsIgnoreCase("Jan") && !monthString.equalsIgnoreCase("feb") && !monthString.equalsIgnoreCase("mar") && !monthString.equalsIgnoreCase("apr") && !monthString.equalsIgnoreCase("may") && !monthString.equalsIgnoreCase("jun") && !monthString.equalsIgnoreCase("jul") && !monthString.equalsIgnoreCase("aug") && !monthString.equalsIgnoreCase("sep") && !monthString.equalsIgnoreCase("oct") && !monthString.equalsIgnoreCase("nov") && !monthString.equalsIgnoreCase("dec")) {
               if (((String)stringArrayForSlash.get(0)).trim().length() == 4) {
                  setFormat = 2;
                  yearString = (String)stringArrayForSlash.get(0);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForSlash.get(1);
                  monthString = monthString.trim();
                  date = (String)stringArrayForSlash.get(2);
                  date = date.trim();
                  formatString = yearString + "/" + monthString + "/" + date;
               } else if (((String)stringArrayForSlash.get(2)).trim().length() == 4) {
                  setFormat = 1;
                  yearString = (String)stringArrayForSlash.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForSlash.get(0);
                  monthString = monthString.trim();
                  date = (String)stringArrayForSlash.get(1);
                  date = date.trim();
                  formatString = monthString + "/" + date + "/" + yearString;
               } else if (((String)stringArrayForSlash.get(1)).trim().length() == 4) {
                  setFormat = 3;
                  yearString = (String)stringArrayForSlash.get(1);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForSlash.get(0);
                  monthString = monthString.trim();
                  date = (String)stringArrayForSlash.get(2);
                  date = date.trim();
                  formatString = monthString + "/" + yearString + "/" + date;
               } else if (((String)stringArrayForSlash.get(2)).trim().length() == 2 || ((String)stringArrayForSlash.get(2)).trim().length() == 1) {
                  setFormat = 1;
                  yearString = (String)stringArrayForSlash.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForSlash.get(0);
                  monthString = monthString.trim();
                  date = (String)stringArrayForSlash.get(1);
                  date = date.trim();
                  formatString = monthString + "/" + date + "/" + yearString;
               }
            } else {
               yearString = this.monthFormat(monthString);
               if (((String)stringArrayForSlash.get(2)).length() == 4) {
                  setFormat = 1;
                  yearString = (String)stringArrayForSlash.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  date = (String)stringArrayForSlash.get(0);
                  date = date.trim();
                  formatString = yearString + "/" + date + "/" + yearString;
               } else if (((String)stringArrayForSlash.get(0)).trim().length() == 4) {
                  setFormat = 2;
                  yearString = (String)stringArrayForSlash.get(0);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  date = (String)stringArrayForSlash.get(2);
                  date = date.trim();
                  formatString = yearString + "/" + yearString + "/" + date;
               } else if (((String)stringArrayForSlash.get(2)).trim().length() == 2 || ((String)stringArrayForSlash.get(2)).trim().length() == 1) {
                  setFormat = 1;
                  yearString = (String)stringArrayForSlash.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  date = (String)stringArrayForSlash.get(0);
                  date = date.trim();
                  formatString = yearString + "/" + date + "/" + yearString;
               }
            }

            if (setFormat == 1) {
               formatString = "'" + formatString + "'" + " ," + "'MM/DD/" + year + "')";
            } else if (setFormat == 2) {
               formatString = "'" + formatString + "'" + " ," + "'" + year + "/MM/DD" + "')";
            } else if (setFormat == 3) {
               formatString = "'" + formatString + "'" + " ," + "'MM/" + year + "/DD" + "')";
            }
         } else if (stringArrayForSlash.size() == 4) {
            monthString = (String)stringArrayForSlash.get(1);
            monthString = monthString.trim();
            year = null;
            if (!monthString.equalsIgnoreCase("Jan") && !monthString.equalsIgnoreCase("feb") && !monthString.equalsIgnoreCase("mar") && !monthString.equalsIgnoreCase("apr") && !monthString.equalsIgnoreCase("may") && !monthString.equalsIgnoreCase("jun") && !monthString.equalsIgnoreCase("jul") && !monthString.equalsIgnoreCase("aug") && !monthString.equalsIgnoreCase("sep") && !monthString.equalsIgnoreCase("oct") && !monthString.equalsIgnoreCase("nov") && !monthString.equalsIgnoreCase("dec")) {
               if (((String)stringArrayForSlash.get(0)).trim().length() == 4) {
                  setFormat = 2;
                  yearString = (String)stringArrayForSlash.get(0);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForSlash.get(1);
                  monthString = monthString.trim();
                  date = (String)stringArrayForSlash.get(2);
                  date = date.trim();
                  formatString = yearString + "/" + monthString + "/" + date;
               } else if (((String)stringArrayForSlash.get(2)).trim().length() == 4) {
                  setFormat = 1;
                  yearString = (String)stringArrayForSlash.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForSlash.get(0);
                  monthString = monthString.trim();
                  date = (String)stringArrayForSlash.get(1);
                  date = date.trim();
                  formatString = monthString + "/" + date + "/" + yearString;
               } else if (((String)stringArrayForSlash.get(1)).trim().length() == 4) {
                  setFormat = 3;
                  yearString = (String)stringArrayForSlash.get(1);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForSlash.get(0);
                  monthString = monthString.trim();
                  date = (String)stringArrayForSlash.get(2);
                  date = date.trim();
                  formatString = monthString + "/" + yearString + "/" + date;
               } else if (((String)stringArrayForSlash.get(2)).trim().length() == 2 || ((String)stringArrayForSlash.get(2)).trim().length() == 1) {
                  setFormat = 1;
                  yearString = (String)stringArrayForSlash.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  monthString = (String)stringArrayForSlash.get(0);
                  monthString = monthString.trim();
                  date = (String)stringArrayForSlash.get(1);
                  date = date.trim();
                  formatString = monthString + "/" + date + "/" + yearString;
               }
            } else {
               yearString = this.monthFormat(monthString);
               if (((String)stringArrayForSlash.get(2)).length() == 4) {
                  setFormat = 1;
                  yearString = (String)stringArrayForSlash.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  date = (String)stringArrayForSlash.get(0);
                  date = date.trim();
                  formatString = yearString + "/" + date + "/" + yearString;
               } else if (((String)stringArrayForSlash.get(0)).trim().length() == 4) {
                  setFormat = 2;
                  yearString = (String)stringArrayForSlash.get(0);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  date = (String)stringArrayForSlash.get(2);
                  date = date.trim();
                  formatString = yearString + "/" + yearString + "/" + date;
               } else if (((String)stringArrayForSlash.get(2)).trim().length() == 2 || ((String)stringArrayForSlash.get(2)).trim().length() == 1) {
                  setFormat = 1;
                  yearString = (String)stringArrayForSlash.get(2);
                  yearString = yearString.trim();
                  yearLength = yearString.length();
                  year = this.yearFormat(yearLength);
                  date = (String)stringArrayForSlash.get(0);
                  date = date.trim();
                  formatString = yearString + "/" + date + "/" + yearString;
               }
            }

            formatString = formatString + " " + stringArrayForSlash.get(3);
            if (setFormat == 1) {
               formatString = "'" + formatString + "'" + " ," + "'MM/DD/" + year + " HH24:MI:SS')";
            } else if (setFormat == 2) {
               formatString = "'" + formatString + "'" + " ," + "'" + year + "/MM/DD" + " HH24:MI:SS')";
            } else if (setFormat == 3) {
               formatString = "'" + formatString + "'" + " ," + "'MM/" + year + "/DD" + " HH24:MI:SS')";
            }
         }
      }

      if (formatString.equals("")) {
         formatString = SwisSQLUtils.getDateFormat(str, 1);
         if (formatString == null) {
            return str;
         }

         if (formatString.startsWith("'1900")) {
            formatString = formatString + ", 'YYYY-MM-DD HH24:MI:SS')";
         } else {
            formatString = str + ", " + formatString + ")";
         }
      }

      return formatString;
   }

   public String monthFormat(String str) {
      this.monthFormat = str;
      if (this.monthFormat.equalsIgnoreCase("Jan")) {
         this.monthFormat = "01";
      } else if (this.monthFormat.equalsIgnoreCase("Feb")) {
         this.monthFormat = "02";
      } else if (this.monthFormat.equalsIgnoreCase("Mar")) {
         this.monthFormat = "03";
      } else if (this.monthFormat.equalsIgnoreCase("Apr")) {
         this.monthFormat = "04";
      } else if (this.monthFormat.equalsIgnoreCase("May")) {
         this.monthFormat = "05";
      } else if (this.monthFormat.equalsIgnoreCase("Jun")) {
         this.monthFormat = "06";
      } else if (this.monthFormat.equalsIgnoreCase("Jul")) {
         this.monthFormat = "07";
      } else if (this.monthFormat.equalsIgnoreCase("Aug")) {
         this.monthFormat = "08";
      } else if (this.monthFormat.equalsIgnoreCase("Sep")) {
         this.monthFormat = "09";
      } else if (this.monthFormat.equalsIgnoreCase("Oct")) {
         this.monthFormat = "10";
      } else if (this.monthFormat.equalsIgnoreCase("Nov")) {
         this.monthFormat = "11";
      } else if (this.monthFormat.equalsIgnoreCase("Dec")) {
         this.monthFormat = "12";
      }

      return this.monthFormat;
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String funName = this.functionName.getColumnName();
      boolean monthOrQuarterOrYearNotConverted = true;
      boolean isDate_AddOrDate_Sub = false;
      this.functionName.setColumnName("TO_DATE");
      Vector arguments = new Vector();
      Vector tempArguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            tempArguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            tempArguments.addElement(this.functionArguments.elementAt(i_count));
         }

         if (i_count == 2 && this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         }

         if (funName != null && (funName.trim().equalsIgnoreCase("DATE_ADD") || funName.trim().equalsIgnoreCase("DATE_SUB")) && i_count == 0 && this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
            isDate_AddOrDate_Sub = true;
         }

         if (funName != null && (funName.trim().equalsIgnoreCase("DATE_ADD") || funName.trim().equalsIgnoreCase("DATE_SUB")) && i_count == 1 && this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
            isDate_AddOrDate_Sub = true;
         }
      }

      new String();
      SelectColumn dateSelectColumn = (SelectColumn)arguments.elementAt(0);
      String dateSelectColumnString = dateSelectColumn.toString();
      String newDateFormat = this.dateFormatConversion(dateSelectColumnString);
      String toDateExpression = null;
      SelectColumn tempSelectColumn;
      SelectColumn sc;
      String exprStr;
      int indexOfSlash;
      String timeString;
      if (tempArguments.elementAt(0) instanceof SelectColumn) {
         tempSelectColumn = ((SelectColumn)tempArguments.elementAt(0)).toOracleSelect(to_sqs, from_sqs);
         timeString = tempSelectColumn.toString();
         timeString = timeString.trim();
         StringBuffer monthBuffer;
         int indexOfHiphen;
         if (!timeString.equalsIgnoreCase("year") && !timeString.equalsIgnoreCase("yy") && !timeString.equalsIgnoreCase("yyyy")) {
            if (!timeString.equalsIgnoreCase("quarter") && !timeString.equalsIgnoreCase("qq") && !timeString.equalsIgnoreCase("q")) {
               if (!timeString.equalsIgnoreCase("month") && !timeString.equalsIgnoreCase("mm") && !timeString.equalsIgnoreCase("m")) {
                  if (!timeString.equalsIgnoreCase("week") && !timeString.equalsIgnoreCase("wk") && !timeString.equalsIgnoreCase("ww")) {
                     if (!timeString.equalsIgnoreCase("dayofyear") && !timeString.equalsIgnoreCase("day") && !timeString.equalsIgnoreCase("y") && !timeString.equalsIgnoreCase("dy") && !timeString.equalsIgnoreCase("dd") && !timeString.equalsIgnoreCase("d") && !timeString.equalsIgnoreCase("weekday") && !timeString.equalsIgnoreCase("dw")) {
                        if (!timeString.equalsIgnoreCase("hour") && !timeString.equalsIgnoreCase("hh")) {
                           if (!timeString.equalsIgnoreCase("minute") && !timeString.equalsIgnoreCase("mi") && !timeString.equalsIgnoreCase("n")) {
                              if (!timeString.equalsIgnoreCase("second") && !timeString.equalsIgnoreCase("ss") && !timeString.equalsIgnoreCase("s")) {
                                 if (timeString.equalsIgnoreCase("millisecond") || timeString.equalsIgnoreCase("ms")) {
                                    this.functionName.setColumnName("TO_TIMESTAMP");
                                    toDateExpression = "1/24/60/60/60";
                                    if (newDateFormat.endsWith(":SS')")) {
                                       newDateFormat = newDateFormat.substring(0, newDateFormat.length() - 2) + ".FF')";
                                    }
                                 }
                              } else {
                                 toDateExpression = "1/24/60/60";
                                 if (dateSelectColumnString.equalsIgnoreCase("(Sysdate)")) {
                                    this.functionName.setColumnName("");
                                 }
                              }
                           } else {
                              toDateExpression = "1/24/60";
                              if (dateSelectColumnString.equalsIgnoreCase("(Sysdate)")) {
                                 this.functionName.setColumnName("");
                              }
                           }
                        } else {
                           toDateExpression = "1/24";
                           if (dateSelectColumnString.equalsIgnoreCase("(Sysdate)")) {
                              this.functionName.setColumnName("");
                           }
                        }
                     } else {
                        this.setToDateExpression("1");
                     }
                  } else {
                     this.setToDateExpression("7");
                  }
               } else {
                  monthOrQuarterOrYearNotConverted = false;
                  monthBuffer = new StringBuffer();
                  this.functionName.setColumnName("ADD_MONTHS");
                  if (arguments.elementAt(0) instanceof SelectColumn) {
                     sc = ((SelectColumn)arguments.elementAt(0)).toOracleSelect(to_sqs, from_sqs);
                     exprStr = sc.toString();
                     exprStr = exprStr.trim();
                     indexOfSlash = exprStr.indexOf(47);
                     indexOfHiphen = exprStr.indexOf(45);
                     if (exprStr.equalsIgnoreCase("(Sysdate)")) {
                        monthBuffer.append(exprStr);
                     } else if (indexOfHiphen == -1 && indexOfSlash == -1 && newDateFormat.equals(dateSelectColumnString)) {
                        monthBuffer.append(exprStr);
                     } else {
                        if (!dateSelectColumnString.equalsIgnoreCase(newDateFormat)) {
                           monthBuffer.append("to_date(");
                        }

                        monthBuffer.append(newDateFormat);
                     }
                  }

                  if (tempArguments.elementAt(1) instanceof SelectColumn) {
                     sc = ((SelectColumn)tempArguments.elementAt(1)).toOracleSelect(to_sqs, from_sqs);
                     timeString = sc.toString();
                     monthBuffer.append("," + timeString);
                  }

                  arguments.setElementAt(monthBuffer, 0);
               }
            } else {
               monthOrQuarterOrYearNotConverted = false;
               monthBuffer = new StringBuffer();
               this.functionName.setColumnName("ADD_MONTHS");
               if (arguments.elementAt(0) instanceof SelectColumn) {
                  sc = ((SelectColumn)arguments.elementAt(0)).toOracleSelect(to_sqs, from_sqs);
                  exprStr = sc.toString();
                  exprStr = exprStr.trim();
                  indexOfSlash = exprStr.indexOf(47);
                  indexOfHiphen = exprStr.indexOf(45);
                  if (exprStr.equalsIgnoreCase("(Sysdate)")) {
                     monthBuffer.append(exprStr);
                  } else if (indexOfSlash == -1 && indexOfHiphen == -1 && newDateFormat.equals(dateSelectColumnString)) {
                     monthBuffer.append(exprStr);
                  } else {
                     if (!dateSelectColumnString.equalsIgnoreCase(newDateFormat)) {
                        monthBuffer.append("to_date(");
                     }

                     monthBuffer.append(newDateFormat);
                  }
               }

               if (tempArguments.elementAt(1) instanceof SelectColumn) {
                  sc = ((SelectColumn)tempArguments.elementAt(1)).toOracleSelect(to_sqs, from_sqs);
                  timeString = sc.toString();
                  monthBuffer.append("," + timeString + "*3");
               }

               arguments.setElementAt(monthBuffer, 0);
            }
         } else {
            monthOrQuarterOrYearNotConverted = false;
            monthBuffer = new StringBuffer();
            this.functionName.setColumnName("ADD_MONTHS");
            if (arguments.elementAt(0) instanceof SelectColumn) {
               sc = ((SelectColumn)arguments.elementAt(0)).toOracleSelect(to_sqs, from_sqs);
               exprStr = sc.toString();
               indexOfSlash = exprStr.indexOf(47);
               indexOfHiphen = exprStr.indexOf(45);
               exprStr = exprStr.trim();
               if (exprStr.equalsIgnoreCase("(Sysdate)")) {
                  monthBuffer.append(exprStr);
               } else if (indexOfSlash == -1 && indexOfHiphen == -1 && newDateFormat.equals(dateSelectColumnString)) {
                  monthBuffer.append(exprStr);
               } else {
                  if (!dateSelectColumnString.equalsIgnoreCase(newDateFormat)) {
                     monthBuffer.append("to_date(");
                  }

                  monthBuffer.append(newDateFormat);
               }
            }

            if (tempArguments.elementAt(1) instanceof SelectColumn) {
               sc = ((SelectColumn)tempArguments.elementAt(1)).toOracleSelect(to_sqs, from_sqs);
               timeString = sc.toString();
               monthBuffer.append("," + timeString + "*12");
            }

            arguments.setElementAt(monthBuffer, 0);
         }
      }

      String numFunc;
      if (monthOrQuarterOrYearNotConverted) {
         if (arguments.elementAt(0) instanceof SelectColumn) {
            tempSelectColumn = ((SelectColumn)arguments.elementAt(0)).toOracleSelect(to_sqs, from_sqs);
            numFunc = tempSelectColumn.toString();
            numFunc = numFunc.trim();
            int indexOfSlash = numFunc.indexOf(47);
            int indexOfHiphen = numFunc.indexOf(45);
            if (indexOfSlash != -1 || indexOfHiphen != -1 || !newDateFormat.equals(dateSelectColumnString)) {
               if (!dateSelectColumnString.equalsIgnoreCase(newDateFormat)) {
                  newDateFormat = newDateFormat.substring(0, newDateFormat.length() - 1);
               }

               arguments.setElementAt(newDateFormat, 0);
            }
         }

         if (tempArguments.elementAt(1) instanceof SelectColumn) {
            tempSelectColumn = ((SelectColumn)tempArguments.elementAt(1)).toOracleSelect(to_sqs, from_sqs);
            timeString = tempSelectColumn.toString();
            if (toDateExpression == null) {
               this.setToDateSymbolValue(timeString);
            } else {
               Object obj = arguments.elementAt(0);
               if (obj instanceof SelectColumn) {
                  sc = (SelectColumn)obj;
                  Vector colExpr = sc.getColumnExpression();
                  colExpr.add("+ (" + toDateExpression + " * " + timeString + ")");
                  arguments.setElementAt(sc, 0);
               } else {
                  this.setToDateExpression(toDateExpression);
                  this.setToDateSymbolValue(timeString);
               }
            }
         }
      }

      if (isDate_AddOrDate_Sub) {
         StringBuffer dateAddInOracleSB = new StringBuffer();
         numFunc = null;
         String unitStr = null;
         exprStr = null;
         indexOfSlash = arguments.size();
         if (indexOfSlash == 2) {
            if (arguments.get(0) instanceof SelectColumn) {
               dateAddInOracleSB.append(((SelectColumn)arguments.get(0)).toString());
               if (funName.trim().equalsIgnoreCase("DATE_ADD")) {
                  dateAddInOracleSB.append(" + ");
               } else if (funName.trim().equalsIgnoreCase("DATE_SUB")) {
                  dateAddInOracleSB.append(" - ");
               }
            }

            if (arguments.get(1) instanceof SelectColumn) {
               SelectColumn selCol = (SelectColumn)arguments.get(1);
               Vector colExp = selCol.getColumnExpression();
               if (colExp != null) {
                  Vector colExpNew = new Vector();
                  int i = 0;

                  while(true) {
                     if (i >= colExp.size()) {
                        dateAddInOracleSB.append(numFunc + "(" + exprStr + "," + unitStr + ")");
                        colExpNew.add(dateAddInOracleSB.toString());
                        colExp.clear();
                        colExp.addAll(colExpNew);
                        break;
                     }

                     if (colExp.get(i) instanceof String) {
                        String str1 = (String)colExp.get(i);
                        if (!str1.trim().equalsIgnoreCase("DAY") && !str1.trim().equalsIgnoreCase("HOUR") && !str1.trim().equalsIgnoreCase("MINUTE") && !str1.trim().equalsIgnoreCase("SECOND")) {
                           if (!str1.trim().equalsIgnoreCase("MONTH") && !str1.trim().equalsIgnoreCase("YEAR")) {
                              exprStr = str1.trim();
                           } else {
                              numFunc = "NUMTOYMINTERVAL";
                              unitStr = "'" + str1.trim() + "'";
                           }
                        } else {
                           numFunc = "NUMTODSINTERVAL";
                           unitStr = "'" + str1.trim() + "'";
                        }
                     }

                     ++i;
                  }
               }
            }
         }

         this.setFunctionName((TableColumn)null);
         this.setToDateExpression((String)null);
         this.setToDateSymbolValue((String)null);
         this.setOpenBracesForFunctionNameRequired(false);
         arguments.remove(0);
      }

      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String funName = this.functionName.getColumnName();
      boolean isdate_add = false;
      if (funName != null && funName.trim().equalsIgnoreCase("DATE_ADD")) {
         isdate_add = true;
      }

      int size;
      for(size = 0; size < this.functionArguments.size(); ++size) {
         if (this.functionArguments.elementAt(size) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(size)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(size));
         }
      }

      String intervalString;
      if (from_sqs != null && from_sqs.isMSAzure()) {
         Vector azureArgs = new Vector();
         boolean isMicrosecond = false;
         intervalString = (((SelectColumn)arguments.get(1)).getColumnExpression().size() == 3 && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0).toString().equalsIgnoreCase("interval") ? ((SelectColumn)arguments.get(1)).getColumnExpression().get(2).toString() : "DAY").toString();
         if (intervalString.equalsIgnoreCase("microsecond")) {
            azureArgs.addElement("MILLISECOND");
            isMicrosecond = true;
         } else {
            azureArgs.addElement(intervalString);
         }

         String interval = "";
         if (((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)((TableColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(0))).getColumnName().equalsIgnoreCase("interval")) {
            if (((SelectColumn)arguments.get(1)).getColumnExpression().get(1) instanceof SelectColumn) {
               if (((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().size() == 1) {
                  interval = ((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().get(0).toString().replaceAll("N'", "").replaceAll("'", "");
               } else {
                  interval = ((SelectColumn)((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1))).toString();
               }
            } else {
               interval = ((SelectColumn)arguments.get(1)).getColumnExpression().get(1).toString().replaceAll("N'", "").replaceAll("'", "");
            }
         } else {
            interval = ((SelectColumn)arguments.get(1)).toString().replaceAll("N'", "").replaceAll("'", "");
         }

         if (!this.functionName.getColumnName().equalsIgnoreCase("date_sub") && !this.functionName.getColumnName().equalsIgnoreCase("subdate")) {
            if (isMicrosecond) {
               azureArgs.addElement(interval + "/1000");
            } else {
               azureArgs.addElement(interval);
            }
         } else if (isMicrosecond) {
            azureArgs.addElement("-" + interval + "/1000");
         } else {
            azureArgs.addElement("-(" + interval + ")");
         }

         azureArgs.addElement("CAST(" + arguments.get(0).toString() + " AS DATETIME )");
         this.functionName.setColumnName("DATEADD");
         this.setFunctionArguments(azureArgs);
      } else {
         this.functionName.setColumnName("DATEADD");
         String arg1;
         if (isdate_add) {
            size = arguments.size();
            arg1 = null;
            intervalString = null;
            if (size == 2) {
               SelectColumn selCol1;
               Vector colExp;
               TableColumn tabCol;
               String funName1;
               if (arguments.get(1) instanceof SelectColumn) {
                  selCol1 = (SelectColumn)arguments.get(1);
                  colExp = selCol1.getColumnExpression();
                  if (colExp != null) {
                     Vector colExpNew = new Vector();
                     int i = 0;

                     while(true) {
                        if (i >= colExp.size()) {
                           colExp.clear();
                           colExp.addAll(colExpNew);
                           break;
                        }

                        if (colExp.get(i) instanceof String) {
                           String str1 = (String)colExp.get(i);
                           if (str1.trim().equalsIgnoreCase("DAY")) {
                              arg1 = "dd";
                           } else {
                              colExpNew.add(colExp.get(i));
                           }
                        } else if (colExp.get(i) instanceof TableColumn) {
                           tabCol = (TableColumn)colExp.get(i);
                           funName1 = tabCol.getColumnName();
                           if (funName1 == null || !funName1.trim().equalsIgnoreCase("INTERVAL")) {
                              colExpNew.add(colExp.get(i));
                           }
                        } else {
                           colExpNew.add(colExp.get(i));
                        }

                        ++i;
                     }
                  }
               }

               if (arg1 != null) {
                  selCol1 = null;
                  if (arguments.get(0) instanceof SelectColumn) {
                     selCol1 = (SelectColumn)arguments.remove(0);
                  }

                  arguments.add(0, "dd");
                  if (selCol1 != null) {
                     colExp = selCol1.getColumnExpression();
                     if (colExp != null && colExp.size() == 1 && colExp.get(0) instanceof SelectColumn) {
                        SelectColumn selCol2 = (SelectColumn)colExp.get(0);
                        Vector colExp1 = selCol2.getColumnExpression();
                        if (colExp1 != null && colExp1.size() == 1 && colExp1.get(0) instanceof TableColumn) {
                           tabCol = (TableColumn)colExp1.get(0);
                           funName1 = tabCol.getColumnName();
                           if (funName1 != null && funName1.trim().equalsIgnoreCase("[CURRENT_TIMESTAMP]")) {
                              funName1 = funName1.substring(1, funName1.length() - 1);
                              tabCol.setColumnName(funName1);
                           }
                        }
                     }

                     arguments.add(selCol1);
                  }
               }
            }
         }

         if (FunctionCalls.charToIntName && this.functionArguments.size() == 3) {
            Object obj = this.functionArguments.get(0);
            arg1 = obj.toString().trim();
            if (arg1.equalsIgnoreCase("HH24")) {
               arg1 = "HH";
               arguments.setElementAt(arg1, 0);
            } else if (arg1.startsWith("'") && arg1.endsWith("'")) {
               if (arg1.substring(1, arg1.length() - 1).equalsIgnoreCase("HH24")) {
                  arg1 = "HH";
                  arguments.setElementAt(arg1, 0);
               } else if (arg1.substring(1, arg1.length() - 1).equalsIgnoreCase("MI")) {
                  arg1 = "MI";
                  arguments.setElementAt(arg1, 0);
               } else if (arg1.substring(1, arg1.length() - 1).equalsIgnoreCase("SS")) {
                  arg1 = "SS";
                  arguments.setElementAt(arg1, 0);
               }
            }
         }

      }
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String funName = this.functionName.getColumnName();
      boolean isdate_add = false;
      if (funName != null && (funName.trim().equalsIgnoreCase("DATE_ADD") || funName.trim().equalsIgnoreCase("adddate"))) {
         isdate_add = true;
      }

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector snowflakeArgs = new Vector();
      String type = (((SelectColumn)arguments.get(1)).getColumnExpression().size() == 3 && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0).toString().equalsIgnoreCase("interval") ? ((SelectColumn)arguments.get(1)).getColumnExpression().get(2).toString() : "DAY").toString();
      snowflakeArgs.addElement(type);
      String interval = "";
      if (((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)((TableColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(0))).getColumnName().equalsIgnoreCase("interval")) {
         if (((SelectColumn)arguments.get(1)).getColumnExpression().get(1) instanceof SelectColumn) {
            if (((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().size() == 1) {
               interval = ((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().get(0).toString();
            } else {
               interval = ((SelectColumn)((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1))).toString();
            }
         } else {
            interval = ((SelectColumn)arguments.get(1)).getColumnExpression().get(1).toString();
         }
      } else {
         interval = ((SelectColumn)arguments.get(1)).toString();
      }

      if (interval.startsWith("'") && interval.endsWith("'")) {
         interval = interval.replaceAll("'", "");
      }

      if (!this.functionName.getColumnName().equalsIgnoreCase("date_sub") && !this.functionName.getColumnName().equalsIgnoreCase("subdate")) {
         snowflakeArgs.addElement(interval);
      } else {
         snowflakeArgs.addElement("(-" + interval + ")");
      }

      this.functionName.setColumnName("DATEADD");
      snowflakeArgs.addElement("CAST(" + arguments.get(0).toString() + " AS DATETIME )");
      this.setFunctionArguments(snowflakeArgs);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (FunctionCalls.charToIntName && this.functionArguments.size() == 3) {
         Object obj = this.functionArguments.get(0);
         String arg1 = obj.toString().trim();
         if (arg1.equalsIgnoreCase("HH24")) {
            arg1 = "HH";
            arguments.setElementAt(arg1, 0);
         } else if (arg1.startsWith("'") && arg1.endsWith("'")) {
            if (arg1.substring(1, arg1.length() - 1).equalsIgnoreCase("HH24")) {
               arg1 = "HH";
               arguments.setElementAt(arg1, 0);
            } else if (arg1.substring(1, arg1.length() - 1).equalsIgnoreCase("MI")) {
               arg1 = "MI";
               arguments.setElementAt(arg1, 0);
            } else if (arg1.substring(1, arg1.length() - 1).equalsIgnoreCase("SS")) {
               arg1 = "SS";
               arguments.setElementAt(arg1, 0);
            }
         }
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

      this.setFunctionArguments(arguments);
      String arg1;
      if (arguments.size() == 2) {
         Vector newArg = new Vector();
         arg1 = arguments.get(1).toString();
         if (arg1.toLowerCase().contains("microsecond")) {
            newArg.addElement("MICROSECOND");
         } else if (arg1.toLowerCase().contains("second")) {
            newArg.addElement("SECOND");
         } else if (arg1.toLowerCase().contains("minute")) {
            newArg.addElement("MINUTE");
         } else if (arg1.toLowerCase().contains("hour")) {
            newArg.addElement("HOUR");
         } else if (arg1.toLowerCase().contains("day")) {
            newArg.addElement("DAY");
         } else if (arg1.toLowerCase().contains("month")) {
            newArg.addElement("MONTH");
         } else if (arg1.toLowerCase().contains("week")) {
            newArg.addElement("WEEK");
         } else if (arg1.toLowerCase().contains("quarter")) {
            newArg.addElement("QUARTER");
         } else if (arg1.toLowerCase().contains("year")) {
            newArg.addElement("YEAR");
         } else {
            newArg.addElement("DAY");
         }

         String interval = "";
         if (((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)((TableColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(0))).getColumnName().equalsIgnoreCase("interval")) {
            if (((SelectColumn)arguments.get(1)).getColumnExpression().get(1) instanceof SelectColumn) {
               if (((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().size() == 1) {
                  interval = ((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().get(0).toString().replaceAll("'", "");
               } else {
                  interval = ((SelectColumn)((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1))).toString();
               }
            } else {
               interval = ((SelectColumn)arguments.get(1)).getColumnExpression().get(1).toString().replaceAll("'", "");
            }
         } else {
            interval = ((SelectColumn)arguments.get(1)).toString().replaceAll("'", "");
         }

         if (!this.functionName.getColumnName().equalsIgnoreCase("subdate") && !this.functionName.getColumnName().equalsIgnoreCase("date_sub")) {
            newArg.addElement(interval);
         } else {
            newArg.addElement(" -" + interval);
         }

         newArg.addElement(arguments.get(0));
         arguments = newArg;
         this.setFunctionArguments(newArg);
      }

      if (arguments.size() == 3) {
         Object obj = arguments.get(0);
         arg1 = obj.toString().trim();
         Vector colExp;
         Object arg3;
         if (!arg1.equalsIgnoreCase("month") && !arg1.equalsIgnoreCase("mm") && !arg1.equalsIgnoreCase("m")) {
            if (!arg1.equalsIgnoreCase("day") && !arg1.equalsIgnoreCase("dd") && !arg1.equalsIgnoreCase("d")) {
               if (!arg1.equalsIgnoreCase("week") && !arg1.equalsIgnoreCase("wk") && !arg1.equalsIgnoreCase("ww")) {
                  if (!arg1.equalsIgnoreCase("year") && !arg1.equalsIgnoreCase("yy")) {
                     if (!arg1.equalsIgnoreCase("quarter") && !arg1.equalsIgnoreCase("qq")) {
                        if (!arg1.equalsIgnoreCase("hour") && !arg1.equalsIgnoreCase("hh")) {
                           if (!arg1.equalsIgnoreCase("minute") && !arg1.equalsIgnoreCase("mi")) {
                              if (!arg1.equalsIgnoreCase("second") && !arg1.equalsIgnoreCase("ss")) {
                                 if (arg1.equalsIgnoreCase("microsecond") || arg1.equalsIgnoreCase("ff")) {
                                    this.dateaddToDB2("MICROSECONDS");
                                 }
                              } else {
                                 this.dateaddToDB2("SECONDS");
                              }
                           } else {
                              this.dateaddToDB2("MINUTES");
                           }
                        } else {
                           this.dateaddToDB2("HOURS");
                        }
                     } else {
                        this.functionName.setColumnName("");
                        arg3 = this.functionArguments.get(2);
                        if (arg3 instanceof SelectColumn) {
                           colExp = ((SelectColumn)arg3).getColumnExpression();
                           if (colExp.size() == 1 && colExp.get(0).toString().startsWith("'")) {
                              colExp.setElementAt("DATE(" + colExp.get(0).toString() + ")", 0);
                           }

                           colExp.addElement("+");
                           colExp.addElement("(" + this.functionArguments.get(1).toString() + " * 91) DAYS");
                           this.functionArguments.setElementAt(arg3, 0);
                           this.functionArguments.setSize(1);
                        }
                     }
                  } else {
                     this.dateaddToDB2("YEARS");
                  }
               } else {
                  this.functionName.setColumnName("");
                  arg3 = this.functionArguments.get(2);
                  if (arg3 instanceof SelectColumn) {
                     colExp = ((SelectColumn)arg3).getColumnExpression();
                     if (colExp.size() == 1 && colExp.get(0).toString().startsWith("'")) {
                        colExp.setElementAt("DATE(" + colExp.get(0).toString() + ")", 0);
                     }

                     colExp.addElement("+");
                     colExp.addElement("(" + this.functionArguments.get(1).toString() + " * 7) DAYS");
                     this.functionArguments.setElementAt(arg3, 0);
                     this.functionArguments.setSize(1);
                  } else if (arg3 instanceof String) {
                     this.functionArguments.setElementAt(arg3 + " + " + this.functionArguments.get(1).toString() + "*7", 0);
                     this.functionArguments.setSize(1);
                  }
               }
            } else {
               this.functionName.setColumnName("");
               arg3 = this.functionArguments.get(2);
               if (arg3 instanceof SelectColumn) {
                  colExp = ((SelectColumn)arg3).getColumnExpression();
                  if (colExp.size() == 1 && colExp.get(0).toString().startsWith("'")) {
                     colExp.setElementAt("DATE(" + colExp.get(0).toString() + ")", 0);
                  }

                  colExp.addElement("+");
                  colExp.addElement("( " + this.functionArguments.get(1).toString() + ")" + " DAYS");
                  this.functionArguments.setElementAt(arg3, 0);
                  this.functionArguments.setSize(1);
               } else if (arg3 instanceof String) {
                  this.functionArguments.setElementAt(arg3 + " + " + this.functionArguments.get(1).toString(), 0);
                  this.functionArguments.setSize(1);
               }
            }
         } else {
            this.functionName.setColumnName("");
            arg3 = this.functionArguments.get(2);
            if (arg3 instanceof SelectColumn) {
               colExp = ((SelectColumn)arg3).getColumnExpression();
               if (colExp.size() == 1 && colExp.get(0).toString().startsWith("'")) {
                  colExp.setElementAt("DATE(" + colExp.get(0).toString() + ")", 0);
               }

               colExp.addElement("+");
               colExp.addElement(this.functionArguments.get(1).toString() + " MONTHS");
               this.functionArguments.setElementAt(arg3, 0);
               this.functionArguments.setSize(1);
            } else if (arg3 instanceof String) {
               this.functionArguments.setElementAt(arg3 + " + " + this.functionArguments.get(1).toString(), 0);
               this.functionArguments.setSize(1);
            }
         }
      }

   }

   private void dateaddToDB2(String fnName) {
      this.functionName.setColumnName("");
      Object arg3 = this.functionArguments.get(2);
      if (arg3 instanceof SelectColumn) {
         Vector colExp = ((SelectColumn)arg3).getColumnExpression();
         if (colExp.size() == 1 && colExp.get(0).toString().startsWith("'")) {
            colExp.setElementAt("DATE(" + colExp.get(0).toString() + ")", 0);
         }

         colExp.addElement("+");
         colExp.addElement(this.functionArguments.get(1).toString() + " " + fnName);
         this.functionArguments.setElementAt(arg3, 0);
         this.functionArguments.setSize(1);
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean isDateSub = false;
      boolean isDateAdd = false;
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      boolean canUseUDFFunction = from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.date.time");
      boolean forYear = false;
      if (this.functionName.getColumnName().trim().equalsIgnoreCase("date_sub") || this.functionName.getColumnName().trim().equalsIgnoreCase("subdate")) {
         isDateSub = true;
      }

      if (this.functionName.getColumnName().trim().equalsIgnoreCase("date_add") || this.functionName.getColumnName().trim().equalsIgnoreCase("adddate")) {
         isDateAdd = true;
      }

      Vector arguments = new Vector();

      String argLowerCase;
      Vector v;
      String intervalNum;
      String datepart;
      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0) instanceof String) {
               argLowerCase = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0).toString();
               argLowerCase = this.handleStringLiteralForDateTime(argLowerCase, from_sqs);
               argLowerCase = "CAST(" + argLowerCase + " AS TIMESTAMP)";
               ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().set(0, argLowerCase);
            }

            if (this.functionArguments.size() == 2 && i_count == 1 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().size() == 3) {
               if (((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(1) instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(1);
                  if (sc.getOpenBrace() == null) {
                     sc.setOpenBrace("(");
                     sc.setCloseBrace(")");
                  }
               }

               try {
                  if (((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)((TableColumn)((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0))).getColumnName().equalsIgnoreCase("interval")) {
                     Object interval = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(1);
                     if (interval instanceof SelectColumn && ((SelectColumn)interval).getColumnExpression() != null && ((SelectColumn)interval).getColumnExpression().size() == 1 && ((SelectColumn)interval).getColumnExpression().get(0) instanceof FunctionCalls && ((FunctionCalls)((SelectColumn)interval).getColumnExpression().get(0)).getFunctionNameAsAString() != null && ((FunctionCalls)((SelectColumn)interval).getColumnExpression().get(0)).getFunctionNameAsAString().equalsIgnoreCase("ROUND") || !(interval instanceof FunctionCalls) || ((FunctionCalls)interval).getFunctionName() == null || !((FunctionCalls)interval).getFunctionName().toString().equalsIgnoreCase("ROUND")) {
                        if (from_sqs != null && from_sqs.isAmazonRedShift()) {
                           intervalNum = interval.toString();
                           if (interval instanceof String && interval.toString().startsWith("'") && interval.toString().endsWith("'")) {
                              intervalNum = interval.toString().replaceAll("'", "");
                           } else if (interval instanceof SelectColumn) {
                              if (((SelectColumn)interval).getColumnExpression().size() == 1 && ((SelectColumn)interval).getColumnExpression().get(0) instanceof String) {
                                 intervalNum = ((SelectColumn)interval).getColumnExpression().get(0).toString().replaceAll("'", "");
                              } else {
                                 intervalNum = ((SelectColumn)interval).toPostgreSQLSelect(to_sqs, from_sqs).toString();
                              }
                           }

                           datepart = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression() != null && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().size() == 3 ? ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(2).toString() : this.functionArguments.get(1).toString();
                           arguments.addElement(intervalNum);
                           if (datepart.toLowerCase().contains("microsecond")) {
                              arguments.addElement("MICROSECOND");
                           } else if (datepart.toLowerCase().contains("second")) {
                              arguments.addElement("SECOND");
                           } else if (datepart.toLowerCase().contains("minute")) {
                              arguments.addElement("MINUTE");
                           } else if (datepart.toLowerCase().contains("hour")) {
                              arguments.addElement("HOUR");
                           } else if (datepart.toLowerCase().contains("day")) {
                              arguments.addElement("DAY");
                           } else if (datepart.toLowerCase().contains("month")) {
                              arguments.addElement("MONTH");
                           } else if (datepart.toLowerCase().contains("week")) {
                              arguments.addElement("WEEK");
                           } else if (datepart.toLowerCase().contains("quarter")) {
                              arguments.addElement("QUARTER");
                           } else if (datepart.toLowerCase().contains("year")) {
                              arguments.addElement("YEAR");
                           }
                           continue;
                        }

                        FunctionCalls fc = new FunctionCalls();
                        TableColumn tc = new TableColumn();
                        tc.setColumnName("round");
                        fc.setFunctionName(tc);
                        v = new Vector();
                        String intervalNum;
                        if (interval instanceof String && interval.toString().startsWith("'") && interval.toString().endsWith("'")) {
                           intervalNum = interval.toString().replaceAll("'", "");

                           try {
                              Double.parseDouble(intervalNum);
                              interval = intervalNum;
                           } catch (Exception var17) {
                           }
                        } else if (interval instanceof SelectColumn && ((SelectColumn)interval).getColumnExpression().size() == 1 && ((SelectColumn)interval).getColumnExpression().get(0) instanceof String) {
                           intervalNum = ((SelectColumn)interval).getColumnExpression().get(0).toString().replaceAll("'", "");

                           try {
                              Double.parseDouble(intervalNum);
                              interval = intervalNum;
                           } catch (Exception var16) {
                           }
                        }

                        v.addElement(interval);
                        SelectColumn colExp = new SelectColumn();
                        colExp.setColumnExpression(v);
                        Vector fnV = new Vector();
                        fnV.add(colExp);
                        fc.setFunctionArguments(fnV);
                        ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().remove(1);
                        ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().add(1, fc);
                     }
                  }
               } catch (Exception var18) {
               }
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if ((this.functionArguments.size() != 2 || !this.functionName.getColumnName().equalsIgnoreCase("DATE_ADD")) && !this.functionName.getColumnName().equalsIgnoreCase("DATE_SUB") && !this.functionName.getColumnName().equalsIgnoreCase("subdate") && !this.functionName.getColumnName().equalsIgnoreCase("adddate")) {
         this.functionName.setColumnName("DATE_PLI");
         this.setFunctionArguments(arguments);
         if (this.functionArguments.size() == 3) {
            Object obj = this.functionArguments.get(0);
            argLowerCase = obj.toString().trim();
            intervalNum = null;
            if (!argLowerCase.equalsIgnoreCase("day") && !argLowerCase.equalsIgnoreCase("dd") && !argLowerCase.equalsIgnoreCase("d")) {
               if (argLowerCase.equalsIgnoreCase("month") || argLowerCase.equalsIgnoreCase("mm") || argLowerCase.equalsIgnoreCase("m")) {
                  intervalNum = "MONTHS";
               }
            } else {
               intervalNum = "DAY";
            }

            if (intervalNum != null) {
               this.functionName.setColumnName((String)null);
               SelectColumn arg = new SelectColumn();
               v = new Vector();
               v.addElement("DATE");
               v.addElement(this.functionArguments.get(2));
               v.addElement("+");
               v.addElement("INTERVAL '");
               v.addElement(this.functionArguments.get(1));
               v.addElement(intervalNum);
               v.addElement("'");
               arg.setColumnExpression(v);
               this.functionArguments.setElementAt(arg, 0);
               this.functionArguments.setSize(1);
            } else {
               if (intervalNum == null && !SwisSQLOptions.passFunctionsWithOutThrowingConvertException) {
                  throw new ConvertException("DATEADD function is supported only for 'DAY' and 'MONTH' in PostgreSQL");
               }

               if (isDateSub) {
                  this.functionName.setColumnName("DATE_SUB");
               } else if (isDateAdd) {
                  this.functionName.setColumnName("DATE_ADD");
               }
            }
         }

      } else {
         String arg = arguments.get(1).toString();
         argLowerCase = arg.replaceAll(" ", "").toLowerCase().trim();
         intervalNum = "+";
         if (isDateSub) {
            intervalNum = "-";
         }

         if (from_sqs != null && from_sqs.isAmazonRedShift()) {
            datepart = "";
            if (arguments.size() == 3) {
               if (isDateSub) {
                  datepart = "DATEADD(" + arguments.get(2).toString() + ",-(" + arguments.get(1).toString() + ") ," + arguments.get(0).toString() + ")";
               } else {
                  datepart = "DATEADD(" + arguments.get(2).toString() + "," + arguments.get(1).toString() + "," + arguments.get(0).toString() + ")";
               }
            } else if (arguments.size() == 2) {
               if (isDateSub) {
                  datepart = "DATEADD(DAY,-" + arguments.get(1).toString() + "," + arguments.get(0).toString() + ")";
               } else {
                  datepart = "DATEADD(DAY," + arguments.get(1).toString() + "," + arguments.get(0).toString() + ")";
               }
            }

            this.functionName.setColumnName(datepart);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (!argLowerCase.startsWith("interval'1'year") && !argLowerCase.startsWith("interval'1'month") && !argLowerCase.startsWith("interval'1'day") && !argLowerCase.startsWith("interval'1'hour") && !argLowerCase.startsWith("interval'1'minute") && !argLowerCase.startsWith("interval'1'second")) {
            datepart = "(" + arguments.get(0) + " " + intervalNum + "interval '1' day * (" + arguments.get(1) + "))";
            if (canUseUDFFunction) {
               if (isDateAdd) {
                  datepart = "DATE_ADD(" + arguments.get(0) + ", " + "INTERVAL '1' DAY * ROUND(" + arguments.get(1) + "))";
               } else {
                  datepart = "DATE_SUB(" + arguments.get(0) + ", " + "INTERVAL '1' DAY * ROUND(" + arguments.get(1) + "))";
               }
            }

            this.functionName.setColumnName(datepart);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else {
            datepart = "(" + arguments.get(0) + " " + intervalNum + " ( " + arguments.get(1) + ") )";
            if (isPostgreLiveDbs && argLowerCase.equalsIgnoreCase("interval'1'second/1000000*round(1)")) {
               datepart = "(TO_TIMESTAMP(TO_CHAR(" + arguments.get(0) + ",'DD Mon YYYY HH24:MI:SS.US'),'DD Mon YYYY HH24:MI:SS.US')" + " " + intervalNum + " ( " + arguments.get(1) + ") )";
            }

            if (canUseUDFFunction) {
               if (isDateAdd) {
                  datepart = "DATE_ADD(" + arguments.get(0) + ", " + arguments.get(1) + ")";
               } else {
                  datepart = "DATE_SUB(" + arguments.get(0) + ", " + arguments.get(1) + ")";
               }
            }

            this.functionName.setColumnName(datepart);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }
      }
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String funName = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 3) {
         Vector finalArgs = new Vector(2);
         if (funName.equalsIgnoreCase("DATEADD")) {
            this.functionName.setColumnName("DATE_ADD");
         } else if (funName.equalsIgnoreCase("DATESUB")) {
            this.functionName.setColumnName("DATE_SUB");
         }

         this.validateDateAddFunUnits(arguments, funName.toUpperCase());
         String arg1 = arguments.get(0).toString();
         finalArgs.addElement(arguments.get(2));
         SelectColumn arg = new SelectColumn();
         Vector colExp = new Vector();
         colExp.addElement("INTERVAL ");
         SelectColumn intrVal = this.modifyIntervalValuesBasedOnDatePart(arg1, (SelectColumn)arguments.get(1));
         intrVal.setOpenBrace("(");
         intrVal.setCloseBrace(")");
         colExp.addElement(intrVal);
         colExp.addElement(SwisSQLAPI.MsSQLServerIntervalList.get(arg1.toLowerCase()));
         arg.setColumnExpression(colExp);
         finalArgs.addElement(arg);
         this.setFunctionArguments(finalArgs);
      } else {
         if (funName.equalsIgnoreCase("DATE_SUB")) {
            this.functionName.setColumnName("DATE_SUB");
         } else {
            this.functionName.setColumnName("DATE_ADD");
         }

         boolean isDateSub = funName.equalsIgnoreCase("DATE_SUB") || funName.equalsIgnoreCase("SUBDATE");
         if (from_sqs != null && (isDateSub && from_sqs.isMongoDb() || from_sqs.isHyperSql())) {
            Vector args = new Vector();
            Vector v = this.getIntervalAndType(arguments);
            String interval = (String)v.get(0);
            String type = (String)v.get(1);
            if (from_sqs.isHyperSql()) {
               this.functionName.setColumnName("DATEADD");
               args.add(type);
               args.add(isDateSub ? "-(" + interval + ")" : interval);
               args.add(StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString()));
            } else {
               this.functionName.setColumnName("DATE_ADD");
               SelectColumn sc = new SelectColumn();
               Vector vc = new Vector();
               vc.add("INTERVAL ");
               vc.add("-(" + interval + ") ");
               vc.add(type);
               sc.setColumnExpression(vc);
               args.add(arguments.get(0));
               args.add(sc);
            }

            this.setFunctionArguments(args);
            return;
         }

         this.setFunctionArguments(arguments);
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String funName = this.functionName.getColumnName();
      String operator = "+";
      if (funName != null && (funName.trim().equalsIgnoreCase("DATE_SUB") || funName.trim().equalsIgnoreCase("subdate"))) {
         operator = "-";
      }

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String interval = "";
      if (((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)((TableColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(0))).getColumnName().equalsIgnoreCase("interval")) {
         if (((SelectColumn)arguments.get(1)).getColumnExpression().get(1) instanceof SelectColumn) {
            if (((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().size() == 1) {
               interval = ((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().get(0).toString();
            } else {
               interval = ((SelectColumn)arguments.get(1)).getColumnExpression().get(1).toString();
            }
         } else {
            interval = ((SelectColumn)arguments.get(1)).getColumnExpression().get(1).toString();
         }
      } else {
         interval = arguments.get(1).toString();
      }

      if (interval.startsWith("'") && interval.endsWith("'")) {
         interval = interval.replaceAll("'", "");
      }

      String dateColumn = StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString());
      String type = ((SelectColumn)arguments.get(1)).getColumnExpression().size() == 3 && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0).toString().equalsIgnoreCase("interval") ? ((SelectColumn)arguments.get(1)).getColumnExpression().get(2).toString() : "DAY";
      String qry;
      if (type.equalsIgnoreCase("microsecond")) {
         qry = " (EXTEND(" + dateColumn + ", YEAR TO FRACTION(5)) + ((" + operator + "1)*(" + interval + "/1000000)) UNITS FRACTION(5)) ";
      } else if (type.equalsIgnoreCase("week")) {
         qry = " (" + dateColumn + " + ((" + operator + "1)*(" + interval + " * 7 )) UNITS DAY)";
      } else if (type.equalsIgnoreCase("quarter")) {
         qry = " (" + dateColumn + " + ((" + operator + "1)*(" + interval + " * 3 )) UNITS MONTH)";
      } else {
         qry = " (" + dateColumn + " + ((" + operator + "1)*(" + interval + ")) UNITS " + type + ") ";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nThe function DATEADD is not supported in TimesTen 5.1.21\n");
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String funName = this.functionName.getColumnName();
      String functionOperation = "+";
      if (funName != null && funName.trim().equalsIgnoreCase("DATE_SUB")) {
         functionOperation = "-";
      }

      StringBuffer arguments = new StringBuffer();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (i_count == 1) {
            arguments.append(") " + functionOperation + " ");
         } else if (i_count == 0) {
            arguments.append("timestamp(");
         }

         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0) {
               this.handleStringLiteralForDateTime(from_sqs, i_count, false);
            }

            arguments.append(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.append(this.functionArguments.elementAt(i_count));
         }
      }

      this.functionName.setColumnName("(" + arguments + ")");
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public SelectColumn modifyIntervalValuesBasedOnDatePart(String datepart, SelectColumn intrVal) throws ConvertException {
      SelectColumn sc;
      Vector vc;
      if (!datepart.equalsIgnoreCase("millisecond") && !datepart.equalsIgnoreCase("ms")) {
         if (!datepart.equalsIgnoreCase("nanosecond") && !datepart.equalsIgnoreCase("ns")) {
            return intrVal;
         } else {
            sc = new SelectColumn();
            vc = new Vector();
            vc.addElement(intrVal);
            vc.add("/ 1000");
            sc.setColumnExpression(vc);
            return sc;
         }
      } else {
         sc = new SelectColumn();
         vc = new Vector();
         vc.addElement(intrVal);
         vc.add("* 1000");
         sc.setColumnExpression(vc);
         return sc;
      }
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean isDateSub = false;
      if (this.functionName.getColumnName().trim().equalsIgnoreCase("date_sub") || this.functionName.getColumnName().trim().equalsIgnoreCase("subdate")) {
         isDateSub = true;
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String interval = "";
      if (((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)((TableColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(0))).getColumnName().equalsIgnoreCase("interval")) {
         if (((SelectColumn)arguments.get(1)).getColumnExpression().get(1) instanceof SelectColumn) {
            if (((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().size() == 1) {
               interval = ((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().get(0).toString();
            } else {
               interval = ((SelectColumn)((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1))).toString();
            }
         } else {
            interval = ((SelectColumn)arguments.get(1)).getColumnExpression().get(1).toString();
         }
      }

      if (!this.functionName.getColumnName().equalsIgnoreCase("DATE_ADD") && !this.functionName.getColumnName().equalsIgnoreCase("DATE_SUB") && !this.functionName.getColumnName().equalsIgnoreCase("subdate") && !this.functionName.getColumnName().equalsIgnoreCase("adddate")) {
         this.setFunctionArguments(arguments);
      } else {
         String qry1 = "";
         if (arguments.size() == 3) {
            if (!interval.isEmpty()) {
               if (isDateSub) {
                  qry1 = "DATETIME_SUB( CAST(" + arguments.get(0).toString() + " AS DATETIME), " + interval + " " + arguments.get(2).toString() + ")";
               } else {
                  qry1 = "DATETIME_ADD( CAST(" + arguments.get(0).toString() + " AS DATETIME), " + interval + " " + arguments.get(2).toString() + ")";
               }
            } else if (isDateSub) {
               qry1 = "DATETIME_SUB( CAST(" + arguments.get(0).toString() + " AS DATETIME), INTERVAL (" + interval + ")" + arguments.get(2).toString() + ")";
            } else {
               qry1 = "DATETIME_ADD( CAST(" + arguments.get(0).toString() + " AS DATETIME), INTERVAL (" + interval + ") " + arguments.get(2).toString() + ")";
            }
         } else if (arguments.size() > 1) {
            if (interval.isEmpty()) {
               if (isDateSub) {
                  qry1 = "DATETIME_SUB( CAST(" + arguments.get(0).toString() + " AS DATETIME), INTERVAL (" + arguments.get(1).toString() + ") DAY)";
               } else {
                  qry1 = "DATETIME_ADD( CAST(" + arguments.get(0).toString() + " AS DATETIME), INTERVAL (" + arguments.get(1).toString() + ") DAY)";
               }
            } else if (isDateSub) {
               qry1 = "DATETIME_SUB( CAST(" + arguments.get(0).toString() + " AS DATETIME), " + arguments.get(1).toString() + ")";
            } else {
               qry1 = "DATETIME_ADD( CAST(" + arguments.get(0).toString() + " AS DATETIME), " + arguments.get(1).toString() + ")";
            }
         }

         this.functionName.setColumnName(qry1);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String funName = this.functionName.getColumnName();
      String operator = "+";
      if (funName != null && (funName.trim().equalsIgnoreCase("DATE_SUB") || funName.trim().equalsIgnoreCase("subdate"))) {
         operator = "-";
      }

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = arguments.get(0).toString();
      String interval = "";
      if (((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)((TableColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(0))).getColumnName().equalsIgnoreCase("interval")) {
         if (((SelectColumn)arguments.get(1)).getColumnExpression().get(1) instanceof SelectColumn) {
            if (((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().size() == 1) {
               interval = ((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().get(0).toString();
            } else {
               interval = ((SelectColumn)((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1))).toString();
            }
         } else {
            interval = ((SelectColumn)arguments.get(1)).getColumnExpression().get(1).toString();
         }
      } else {
         interval = ((SelectColumn)arguments.get(1)).toString();
      }

      if (interval.startsWith("'") && interval.endsWith("'")) {
         interval = interval.replaceAll("'", "");
      }

      String type = (((SelectColumn)arguments.get(1)).getColumnExpression().size() == 3 && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0).toString().equalsIgnoreCase("interval") ? ((SelectColumn)arguments.get(1)).getColumnExpression().get(2).toString() : "DAY").toString();
      if (type.equalsIgnoreCase("hour")) {
         arguments.setElementAt("'h'", 0);
      } else if (type.equalsIgnoreCase("minute")) {
         arguments.setElementAt("'n'", 0);
      } else if (type.equalsIgnoreCase("second")) {
         arguments.setElementAt("'s'", 0);
      } else if (type.equalsIgnoreCase("year")) {
         arguments.setElementAt("'yyyy'", 0);
      } else if (type.equalsIgnoreCase("month")) {
         arguments.setElementAt("'m'", 0);
      } else if (type.equalsIgnoreCase("quarter")) {
         arguments.setElementAt("'q'", 0);
      } else if (type.equalsIgnoreCase("week")) {
         arguments.setElementAt("'ww'", 0);
      } else if (type.equalsIgnoreCase("day")) {
         arguments.setElementAt("'d'", 0);
      } else if (type.equalsIgnoreCase("microsecond")) {
         interval = interval + "/1000000";
         arguments.setElementAt("'s'", 0);
      } else {
         arguments.setElementAt("'d'", 0);
      }

      arguments.setElementAt(operator + "(" + interval + ")", 1);
      arguments.addElement(dateString);
      this.functionName.setColumnName("DATEADD");
      this.setFunctionArguments(arguments);
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String funName = this.functionName.getColumnName();
      String operator = "+";
      if (funName != null && (funName.trim().equalsIgnoreCase("DATE_SUB") || funName.trim().equalsIgnoreCase("subdate"))) {
         operator = "-";
      }

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector athenaArgs = new Vector();
      String interval = "";
      if (((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)((TableColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(0))).getColumnName().equalsIgnoreCase("interval")) {
         if (((SelectColumn)arguments.get(1)).getColumnExpression().get(1) instanceof SelectColumn) {
            if (((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().size() == 1) {
               interval = ((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().get(0).toString();
            } else {
               interval = ((SelectColumn)((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1))).toString();
            }
         } else {
            interval = ((SelectColumn)arguments.get(1)).getColumnExpression().get(1).toString();
         }
      } else {
         interval = ((SelectColumn)arguments.get(1)).toString();
      }

      if (interval.startsWith("'") && interval.endsWith("'")) {
         interval = interval.replaceAll("'", "");
      }

      String type = (((SelectColumn)arguments.get(1)).getColumnExpression().size() == 3 && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0).toString().equalsIgnoreCase("interval") ? ((SelectColumn)arguments.get(1)).getColumnExpression().get(2).toString() : "DAY").toString();
      if (type.equalsIgnoreCase("microsecond")) {
         athenaArgs.addElement("'MILLISECOND'");
         athenaArgs.addElement("(" + operator + "1)*(" + interval + "/1000)");
      } else {
         athenaArgs.addElement("'" + type + "'");
         athenaArgs.addElement("(" + operator + "1)*(" + interval + ")");
      }

      this.functionName.setColumnName("DATE_ADD");
      athenaArgs.addElement("CAST(" + StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString()) + " AS TIMESTAMP )");
      this.setFunctionArguments(athenaArgs);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String funName = this.functionName.getColumnName();
      String operator = "+";
      if (funName != null && (funName.trim().equalsIgnoreCase("DATE_SUB") || funName.trim().equalsIgnoreCase("subdate"))) {
         operator = "-";
      }

      this.functionName.setColumnName("ADD_DAYS");

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String interval = "";
      if (((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)((TableColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(0))).getColumnName().equalsIgnoreCase("interval")) {
         if (((SelectColumn)arguments.get(1)).getColumnExpression().get(1) instanceof SelectColumn) {
            if (((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().size() == 1) {
               interval = ((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().get(0).toString();
            } else {
               interval = ((SelectColumn)((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1))).toString();
            }
         } else {
            interval = ((SelectColumn)arguments.get(1)).getColumnExpression().get(1).toString();
         }
      } else {
         interval = ((SelectColumn)arguments.get(1)).toString();
      }

      if (interval.startsWith("'") && interval.endsWith("'")) {
         interval = interval.replaceAll("'", "");
      }

      String type = (((SelectColumn)arguments.get(1)).getColumnExpression().size() == 3 && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0).toString().equalsIgnoreCase("interval") ? ((SelectColumn)arguments.get(1)).getColumnExpression().get(2).toString() : "DAY").toString();
      if (type.equalsIgnoreCase("hour")) {
         this.functionName.setColumnName("ADD_SECONDS");
         interval = String.valueOf(Integer.valueOf(interval) * 3600);
      } else if (type.equalsIgnoreCase("minute")) {
         this.functionName.setColumnName("ADD_SECONDS");
         interval = String.valueOf(Integer.valueOf(interval) * 60);
      } else if (type.equalsIgnoreCase("second")) {
         this.functionName.setColumnName("ADD_SECONDS");
      } else if (type.equalsIgnoreCase("microsecond")) {
         this.functionName.setColumnName("ADD_SECONDS");
         interval = interval + "/1000000";
         arguments.setElementAt("TO_CHAR(" + StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString()) + ",'YYYY-MM-DD HH24:MI:SS.FF6')", 0);
      } else if (type.equalsIgnoreCase("year")) {
         this.functionName.setColumnName("ADD_YEARS");
      } else if (type.equalsIgnoreCase("month")) {
         this.functionName.setColumnName("ADD_MONTHS");
      } else if (type.equalsIgnoreCase("quarter")) {
         this.functionName.setColumnName("ADD_MONTHS");
         interval = String.valueOf(Integer.valueOf(interval) * 3);
      } else if (type.equalsIgnoreCase("week")) {
         this.functionName.setColumnName("ADD_DAYS");
         interval = String.valueOf(Integer.valueOf(interval) * 7);
      }

      arguments.setElementAt(operator + "(" + interval + ")", 1);
      if (!type.equalsIgnoreCase("microsecond")) {
         arguments.setElementAt(StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString()), 0);
      }

      this.setFunctionArguments(arguments);
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String funName = this.functionName.getColumnName();
      String operator = "+";
      if (funName != null && (funName.trim().equalsIgnoreCase("DATE_SUB") || funName.trim().equalsIgnoreCase("subdate"))) {
         operator = "-";
      }

      this.functionName.setColumnName("DATETIME");

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String interval = "";
      if (((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)((TableColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(0))).getColumnName().equalsIgnoreCase("interval")) {
         if (((SelectColumn)arguments.get(1)).getColumnExpression().get(1) instanceof SelectColumn) {
            if (((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().size() == 1) {
               interval = ((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().get(0).toString();
            } else {
               interval = ((SelectColumn)((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1))).toString();
            }
         } else {
            interval = ((SelectColumn)arguments.get(1)).getColumnExpression().get(1).toString();
         }
      } else {
         interval = ((SelectColumn)arguments.get(1)).toString();
      }

      if (interval.startsWith("'") && interval.endsWith("'")) {
         interval = interval.replaceAll("'", "");
      }

      String type = (((SelectColumn)arguments.get(1)).getColumnExpression().size() == 3 && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0).toString().equalsIgnoreCase("interval") ? ((SelectColumn)arguments.get(1)).getColumnExpression().get(2).toString() : "DAY").toString();
      if (type.equalsIgnoreCase("hour")) {
         interval = interval + " || ' hours'";
      } else if (type.equalsIgnoreCase("minute")) {
         interval = interval + " || ' minutes'";
      } else if (type.equalsIgnoreCase("second")) {
         interval = interval + " || ' seconds'";
      } else if (type.equalsIgnoreCase("year")) {
         interval = interval + " || ' years'";
      } else if (type.equalsIgnoreCase("month")) {
         interval = interval + " || ' months'";
      } else if (type.equalsIgnoreCase("quarter")) {
         interval = Integer.valueOf(interval) * 3 + " || ' months'";
      } else if (type.equalsIgnoreCase("week")) {
         interval = Integer.valueOf(interval) * 7 + " || ' days'";
      } else {
         if (type.equalsIgnoreCase("microsecond")) {
            interval = Float.valueOf(interval) / 1000000.0F + " || ' seconds'";
            String qry = "strftime('%Y-%m-%d %H:%M:%f'," + StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString()) + ",'" + operator + "' || " + interval + ")";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
            return;
         }

         interval = interval + " || ' days'";
      }

      arguments.setElementAt("'" + operator + "' || " + interval, 1);
      arguments.setElementAt(StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString()), 0);
      this.setFunctionArguments(arguments);
   }

   public Vector getIntervalAndType(Vector arguments) {
      Vector v = new Vector(2);
      String interval = "";
      if (((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)((TableColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(0))).getColumnName().equalsIgnoreCase("interval")) {
         if (((SelectColumn)arguments.get(1)).getColumnExpression().get(1) instanceof SelectColumn) {
            if (((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().size() == 1) {
               interval = ((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().get(0).toString();
            } else {
               interval = ((SelectColumn)arguments.get(1)).getColumnExpression().get(1).toString();
            }
         } else {
            interval = ((SelectColumn)arguments.get(1)).getColumnExpression().get(1).toString();
         }
      } else {
         interval = arguments.get(1).toString();
      }

      if (interval.startsWith("'") && interval.endsWith("'")) {
         interval = interval.replaceAll("'", "");
      }

      String type = ((SelectColumn)arguments.get(1)).getColumnExpression().size() == 3 && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0).toString().equalsIgnoreCase("interval") ? ((SelectColumn)arguments.get(1)).getColumnExpression().get(2).toString() : "DAY";
      v.add(interval);
      v.add(type);
      return v;
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String funName = this.functionName.getColumnName();
      String operator = "+";
      if (funName != null && (funName.trim().equalsIgnoreCase("DATE_SUB") || funName.trim().equalsIgnoreCase("subdate"))) {
         operator = "-";
      }

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = arguments.get(0).toString();
      String interval = "";
      if (((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)((TableColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(0))).getColumnName().equalsIgnoreCase("interval")) {
         if (((SelectColumn)arguments.get(1)).getColumnExpression().get(1) instanceof SelectColumn) {
            if (((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().size() == 1) {
               interval = ((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1)).getColumnExpression().get(0).toString();
            } else {
               interval = ((SelectColumn)((SelectColumn)((SelectColumn)arguments.get(1)).getColumnExpression().get(1))).toString();
            }
         } else {
            interval = ((SelectColumn)arguments.get(1)).getColumnExpression().get(1).toString();
         }
      } else {
         interval = ((SelectColumn)arguments.get(1)).toString();
      }

      if (interval.startsWith("'") && interval.endsWith("'")) {
         interval = interval.replaceAll("'", "");
      }

      String type = (((SelectColumn)arguments.get(1)).getColumnExpression().size() == 3 && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0).toString().equalsIgnoreCase("interval") ? ((SelectColumn)arguments.get(1)).getColumnExpression().get(2).toString() : "DAY").toString();
      if (type.equalsIgnoreCase("hour")) {
         arguments.setElementAt("'h'", 0);
      } else if (type.equalsIgnoreCase("minute")) {
         arguments.setElementAt("'n'", 0);
      } else if (type.equalsIgnoreCase("second")) {
         arguments.setElementAt("'s'", 0);
      } else if (type.equalsIgnoreCase("year")) {
         arguments.setElementAt("'yyyy'", 0);
      } else if (type.equalsIgnoreCase("month")) {
         arguments.setElementAt("'m'", 0);
      } else if (type.equalsIgnoreCase("quarter")) {
         arguments.setElementAt("'q'", 0);
      } else if (type.equalsIgnoreCase("week")) {
         arguments.setElementAt("'ww'", 0);
      } else if (type.equalsIgnoreCase("day")) {
         arguments.setElementAt("'d'", 0);
      } else if (type.equalsIgnoreCase("microsecond")) {
         interval = interval + "/1000000";
         arguments.setElementAt("'s'", 0);
      } else {
         arguments.setElementAt("'d'", 0);
      }

      arguments.setElementAt(operator + "(" + interval + ")", 1);
      arguments.addElement(dateString);
      this.functionName.setColumnName("DATEADD");
      this.setFunctionArguments(arguments);
   }
}
