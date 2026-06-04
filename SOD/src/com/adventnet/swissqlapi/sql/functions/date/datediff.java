package com.adventnet.swissqlapi.sql.functions.date;

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

public class datediff extends FunctionCalls {
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

            return formatString;
         } else {
            if (stringArrayForHiphen.size() == 4) {
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

            return formatString;
         }
      } else if (indexOfSlash == -1) {
         if (formatString.equals("")) {
            formatString = SwisSQLUtils.getDateFormat(str, 1);
            if (formatString != null) {
               if (formatString.startsWith("'1900")) {
                  formatString = formatString + ", 'YYYY-MM-DD HH24:MI:SS')";
               } else {
                  formatString = str + ", " + formatString + ")";
               }
            } else {
               formatString = null;
            }
         }

         return formatString;
      } else if (stringArrayForSlash.size() == 3) {
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

         return formatString;
      } else {
         if (stringArrayForSlash.size() == 4) {
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

         return formatString;
      }
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
      if (this.functionArguments.size() > 2) {
         throw new ConvertException("Number of arguments have exceeded the allowed limit");
      } else {
         Vector vector = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
            } else {
               vector.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String date1 = vector.get(0).toString();
         String date2 = vector.get(1).toString();
         date1 = StringFunctions.handleLiteralStringDateForOracle(date1);
         date2 = StringFunctions.handleLiteralStringDateForOracle(date2);
         String query = " TRUNC(" + date1 + ") - TRUNC(" + date2 + ")";
         this.functionName.setColumnName(query);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.getFunctionName().getColumnName().equalsIgnoreCase("unix_timestamp")) {
         arguments.add(0, "ss");
         arguments.add(1, "'1970-01-01 00:00:00'");
         if (arguments.get(2) instanceof SelectColumn) {
            SelectColumn argFuncSC = (SelectColumn)arguments.get(2);
            Vector argFuncSCColExpr = argFuncSC.getColumnExpression();
            if (argFuncSCColExpr != null && argFuncSCColExpr.get(0) instanceof FunctionCalls) {
               FunctionCalls argFunc = (FunctionCalls)argFuncSCColExpr.get(0);
               if (argFunc.getFunctionName().getColumnName().equalsIgnoreCase("getdate")) {
                  argFunc.getFunctionName().setColumnName("GETUTCDATE");
               }
            }
         }
      }

      if (from_sqs != null && from_sqs.isMSAzure()) {
         String arg1 = arguments.get(0).toString();
         String arg2 = arguments.get(1).toString();
         arguments = new Vector();
         arguments.addElement("dd");
         arguments.addElement(arg2);
         arguments.addElement(arg1);
      }

      this.functionName.setColumnName("DATEDIFF");
      this.setFunctionArguments(arguments);
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector newArguments = new Vector();
      if (arguments.size() == 2) {
         newArguments.addElement("day");
         newArguments.addElement(arguments.get(0));
         newArguments.addElement(arguments.get(1));
         this.setFunctionArguments(newArguments);
      } else {
         this.setFunctionArguments(arguments);
      }
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
      if (this.functionArguments.size() == 3) {
         Object obj = this.functionArguments.get(0);
         String arg1 = obj.toString().trim();
         Object arg3;
         SelectColumn selectArg1;
         Vector newArgument;
         Object arg2;
         TableColumn innerFunction;
         FunctionCalls weekfunction1;
         Vector week1arg;
         TableColumn innerFunction1;
         FunctionCalls weekfunction2;
         Vector week2arg;
         Vector argument;
         if (!arg1.equalsIgnoreCase("month") && !arg1.equalsIgnoreCase("mm") && !arg1.equalsIgnoreCase("m")) {
            if (!arg1.equalsIgnoreCase("day") && !arg1.equalsIgnoreCase("dd") && !arg1.equalsIgnoreCase("d")) {
               if (!arg1.equalsIgnoreCase("week") && !arg1.equalsIgnoreCase("wk") && !arg1.equalsIgnoreCase("ww")) {
                  if (!arg1.equalsIgnoreCase("year") && !arg1.equalsIgnoreCase("yy")) {
                     if (!arg1.equalsIgnoreCase("quarter") && !arg1.equalsIgnoreCase("qq")) {
                        if (!arg1.equalsIgnoreCase("dayofyear") && !arg1.equalsIgnoreCase("dy")) {
                           if (!arg1.equalsIgnoreCase("weekday") && !arg1.equalsIgnoreCase("dw")) {
                              if (!arg1.equalsIgnoreCase("hour") && !arg1.equalsIgnoreCase("hh")) {
                                 if (!arg1.equalsIgnoreCase("minute") && !arg1.equalsIgnoreCase("mi")) {
                                    if (arg1.equalsIgnoreCase("second") || arg1.equalsIgnoreCase("ss")) {
                                       this.dateDiffToDB2("SECOND");
                                    }
                                 } else {
                                    this.dateDiffToDB2("MINUTE");
                                 }
                              } else {
                                 this.dateDiffToDB2("HOUR");
                              }
                           } else {
                              this.dateDiffToDB2("DAYOFWEEK");
                           }
                        } else {
                           this.dateDiffToDB2("DAYOFYEAR");
                        }
                     } else {
                        this.dateDiffToDB2("QUARTER");
                     }
                  } else {
                     this.dateDiffToDB2("YEAR");
                  }
               } else {
                  this.functionName.setColumnName("");
                  arg3 = this.functionArguments.get(2);
                  if (arg3 instanceof SelectColumn) {
                     this.functionName.setColumnName("");
                     selectArg1 = new SelectColumn();
                     newArgument = new Vector();
                     arg2 = this.functionArguments.get(1);
                     innerFunction = new TableColumn();
                     innerFunction.setColumnName("WEEK");
                     weekfunction1 = new FunctionCalls();
                     week1arg = new Vector();
                     week1arg.add(arg3);
                     weekfunction1.setFunctionName(innerFunction);
                     weekfunction1.setFunctionArguments(week1arg);
                     newArgument.add(weekfunction1);
                     newArgument.add(" - ");
                     innerFunction1 = new TableColumn();
                     innerFunction1.setColumnName("WEEK");
                     weekfunction2 = new FunctionCalls();
                     week2arg = new Vector();
                     week2arg.add(arg2);
                     weekfunction2.setFunctionName(innerFunction1);
                     weekfunction2.setFunctionArguments(week2arg);
                     newArgument.add(weekfunction2);
                     selectArg1.setColumnExpression(newArgument);
                     argument = new Vector();
                     argument.add(selectArg1);
                     this.setFunctionArguments(argument);
                  } else if (arg3 instanceof String) {
                     this.functionArguments.setElementAt(arg3 + " - " + this.functionArguments.get(1).toString() + "*7", 0);
                     this.functionArguments.setSize(1);
                  }
               }
            } else {
               this.functionName.setColumnName("");
               arg3 = this.functionArguments.get(2);
               if (arg3 instanceof SelectColumn) {
                  this.functionName.setColumnName("");
                  selectArg1 = new SelectColumn();
                  newArgument = new Vector();
                  arg2 = this.functionArguments.get(1);
                  innerFunction = new TableColumn();
                  innerFunction.setColumnName("DAYS");
                  weekfunction1 = new FunctionCalls();
                  week1arg = new Vector();
                  week1arg.add(arg3);
                  weekfunction1.setFunctionName(innerFunction);
                  weekfunction1.setFunctionArguments(week1arg);
                  newArgument.add(weekfunction1);
                  newArgument.add(" - ");
                  innerFunction1 = new TableColumn();
                  innerFunction1.setColumnName("DAYS");
                  weekfunction2 = new FunctionCalls();
                  week2arg = new Vector();
                  week2arg.add(arg2);
                  weekfunction2.setFunctionName(innerFunction1);
                  weekfunction2.setFunctionArguments(week2arg);
                  newArgument.add(weekfunction2);
                  selectArg1.setColumnExpression(newArgument);
                  argument = new Vector();
                  argument.add(selectArg1);
                  this.setFunctionArguments(argument);
               } else if (arg3 instanceof String) {
                  this.functionArguments.setElementAt(arg3 + " - " + this.functionArguments.get(1).toString(), 0);
                  this.functionArguments.setSize(1);
               }
            }
         } else {
            this.functionName.setColumnName("");
            arg3 = this.functionArguments.get(2);
            if (arg3 instanceof SelectColumn) {
               this.functionName.setColumnName("");
               selectArg1 = new SelectColumn();
               newArgument = new Vector();
               arg2 = this.functionArguments.get(1);
               innerFunction = new TableColumn();
               innerFunction.setColumnName("MONTH");
               weekfunction1 = new FunctionCalls();
               week1arg = new Vector();
               week1arg.add(arg3);
               weekfunction1.setFunctionName(innerFunction);
               weekfunction1.setFunctionArguments(week1arg);
               newArgument.add(weekfunction1);
               newArgument.add(" - ");
               innerFunction1 = new TableColumn();
               innerFunction1.setColumnName("MONTH");
               weekfunction2 = new FunctionCalls();
               week2arg = new Vector();
               week2arg.add(arg2);
               weekfunction2.setFunctionName(innerFunction1);
               weekfunction2.setFunctionArguments(week2arg);
               newArgument.add(weekfunction2);
               selectArg1.setColumnExpression(newArgument);
               argument = new Vector();
               argument.add(selectArg1);
               this.setFunctionArguments(argument);
            } else if (arg3 instanceof String) {
               this.functionArguments.setElementAt(arg3 + " - " + this.functionArguments.get(1).toString(), 0);
               this.functionArguments.setSize(1);
            }
         }
      }

   }

   private void dateDiffToDB2(String fnName) {
      this.functionName.setColumnName("");
      Object arg3 = this.functionArguments.get(2);
      if (arg3 instanceof SelectColumn) {
         this.functionName.setColumnName("");
         SelectColumn selectArg1 = new SelectColumn();
         Vector newArgument = new Vector();
         Object arg2 = this.functionArguments.get(1);
         TableColumn innerFunction = new TableColumn();
         innerFunction.setColumnName(fnName);
         FunctionCalls function1 = new FunctionCalls();
         Vector week1arg = new Vector();
         week1arg.add(arg3);
         function1.setFunctionName(innerFunction);
         function1.setFunctionArguments(week1arg);
         newArgument.add(function1);
         newArgument.add(" - ");
         TableColumn innerFunction1 = new TableColumn();
         innerFunction1.setColumnName(fnName);
         FunctionCalls function2 = new FunctionCalls();
         Vector week2arg = new Vector();
         week2arg.add(arg2);
         function2.setFunctionName(innerFunction1);
         function2.setFunctionArguments(week2arg);
         newArgument.add(function2);
         selectArg1.setColumnExpression(newArgument);
         Vector argument = new Vector();
         argument.add(selectArg1);
         this.setFunctionArguments(argument);
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() == 1) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported. \n Please ensure that the correct number of arguments are passed\n");
      } else {
         this.functionName.setColumnName("DATE_MI");
         Vector arguments = new Vector();

         int i_count;
         for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            } else {
               if (this.functionArguments.size() == 2 || i_count > 0 && this.functionArguments.size() == 3) {
                  this.handleStringLiteralForDateTime(from_sqs, i_count, false);
               }

               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            }
         }

         for(i_count = 0; i_count < arguments.size(); ++i_count) {
            if (arguments.get(i_count).toString().trim().equalsIgnoreCase("now()")) {
               arguments.set(i_count, "cast(now() as date)");
            }
         }

         this.setFunctionArguments(arguments);
         if (this.functionArguments.size() == 3) {
            Object obj = this.functionArguments.get(0);
            String arg1 = obj.toString().trim();
            if (!arg1.equalsIgnoreCase("day") && !arg1.equalsIgnoreCase("dd") && !arg1.equalsIgnoreCase("d") && !arg1.equalsIgnoreCase("dayofyear") && !arg1.equalsIgnoreCase("dy") && !arg1.equalsIgnoreCase("y") && !arg1.equalsIgnoreCase("weekday") && !arg1.equalsIgnoreCase("dw")) {
               if (!SwisSQLOptions.passFunctionsWithOutThrowingConvertException) {
                  throw new ConvertException("DATEDIFF function is supported only for 'DAY' in PostgreSQL");
               }

               this.functionName.setColumnName("DATEDIFF");
            } else {
               Object arg3 = this.functionArguments.get(2);
               this.functionArguments.setElementAt(arg3, 0);
               this.functionArguments.setElementAt(this.functionArguments.get(1), 1);
               this.functionArguments.setSize(2);
            }
         }

         this.functionArguments.setElementAt("DATE(" + this.functionArguments.get(0) + ")", 0);
         this.functionArguments.setElementAt("DATE(" + this.functionArguments.get(1) + ")", 1);
      }
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("DATE_DIFF")) {
         this.functionName.setColumnName("DATEDIFF");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 3) {
         this.validateDateAddFunUnits(arguments, "DATEDIFF");
         Vector newArguments = new Vector();
         String format = null;
         TableColumn tc = null;
         SelectColumn newSelectColumn;
         Vector colExp;
         if (this.functionArguments.get(0) instanceof SelectColumn) {
            newSelectColumn = (SelectColumn)this.functionArguments.get(0);
            colExp = newSelectColumn.getColumnExpression();
            if (colExp.get(0) instanceof TableColumn) {
               tc = (TableColumn)colExp.get(0);
               format = tc.getColumnName();
            }
         }

         if (format != null) {
            if (!format.equalsIgnoreCase("dd") && !format.equalsIgnoreCase("d") && !format.equalsIgnoreCase("dy") && !format.equalsIgnoreCase("y") && !format.equalsIgnoreCase("day") && !format.equalsIgnoreCase("dayofyear") && !format.equalsIgnoreCase("weekday") && !format.equalsIgnoreCase("dw") && !format.equalsIgnoreCase("w")) {
               if (!format.equalsIgnoreCase("mm") && !format.equalsIgnoreCase("m") && !format.equalsIgnoreCase("month")) {
                  if (!format.equalsIgnoreCase("hour") && !format.equalsIgnoreCase("hh")) {
                     if (!format.equalsIgnoreCase("minute") && !format.equalsIgnoreCase("mi") && !format.equalsIgnoreCase("n")) {
                        if (!format.equalsIgnoreCase("second") && !format.equalsIgnoreCase("ss") && !format.equalsIgnoreCase("s")) {
                           if (!format.equalsIgnoreCase("millisecond") && !format.equalsIgnoreCase("ms") && !format.equalsIgnoreCase("nanosecond") && !format.equalsIgnoreCase("ns")) {
                              if (!format.equalsIgnoreCase("microsecond") && !format.equalsIgnoreCase("mcs")) {
                                 if (!format.equalsIgnoreCase("quarter") && !format.equalsIgnoreCase("qq") && !format.equalsIgnoreCase("q")) {
                                    if (!format.equalsIgnoreCase("week") && !format.equalsIgnoreCase("ww") && !format.equalsIgnoreCase("wk")) {
                                       if (format.equalsIgnoreCase("yy") || format.equalsIgnoreCase("year") || format.equalsIgnoreCase("yyyy")) {
                                          newSelectColumn = new SelectColumn();
                                          colExp = new Vector();
                                          SelectColumn fromDate = (SelectColumn)this.functionArguments.get(2);
                                          FunctionCalls newFunction1 = this.getNewFunction("EXTRACT", fromDate);
                                          newFunction1.setTrailingString("YEAR");
                                          newFunction1.setFromInTrim("FROM");
                                          SelectColumn toDate = (SelectColumn)this.functionArguments.get(1);
                                          FunctionCalls newFunction2 = this.getNewFunction("EXTRACT", toDate);
                                          newFunction2.setTrailingString("YEAR");
                                          newFunction2.setFromInTrim("FROM");
                                          colExp.add(newFunction1);
                                          colExp.add(" - ");
                                          colExp.add(newFunction2);
                                          newSelectColumn.setColumnExpression(colExp);
                                          newArguments.add(newSelectColumn);
                                          this.functionName.setColumnName("");
                                          this.setFunctionArguments(newArguments);
                                       }
                                    } else {
                                       tc.setColumnName("WEEK");
                                       this.functionName.setColumnName("TIMESTAMPDIFF");
                                       this.setFunctionArguments(arguments);
                                    }
                                 } else {
                                    tc.setColumnName("QUARTER");
                                    this.functionName.setColumnName("TIMESTAMPDIFF");
                                    this.setFunctionArguments(arguments);
                                 }
                              } else {
                                 tc.setColumnName("MICROSECOND");
                                 this.functionName.setColumnName("TIMESTAMPDIFF");
                                 this.setFunctionArguments(arguments);
                              }
                           } else {
                              String defVal = "1000";
                              if (format.equalsIgnoreCase("nanosecond") || format.equalsIgnoreCase("ns")) {
                                 defVal = "1000000000";
                              }

                              tc.setColumnName("SECOND");
                              SelectColumn selColumn = new SelectColumn();
                              Vector colExp = new Vector();
                              Vector fnArgs = new Vector();
                              FunctionCalls newFunction1 = this.getNewFunction("TIMESTAMPDIFF", arguments);
                              colExp.add(newFunction1);
                              colExp.add("*");
                              colExp.add(defVal);
                              selColumn.setColumnExpression(colExp);
                              fnArgs.add(selColumn);
                              this.functionName.setColumnName("");
                              this.setFunctionArguments(fnArgs);
                           }
                        } else {
                           tc.setColumnName("SECOND");
                           this.functionName.setColumnName("TIMESTAMPDIFF");
                           this.setFunctionArguments(arguments);
                        }
                     } else {
                        tc.setColumnName("MINUTE");
                        this.functionName.setColumnName("TIMESTAMPDIFF");
                        this.setFunctionArguments(this.getNewFunctionArgs("'%Y-%m-%d %H:%i:00'", arguments));
                     }
                  } else {
                     tc.setColumnName("HOUR");
                     this.functionName.setColumnName("TIMESTAMPDIFF");
                     this.setFunctionArguments(this.getNewFunctionArgs("'%Y-%m-%d %H:00:00'", arguments));
                  }
               } else {
                  tc.setColumnName("MONTH");
                  this.functionName.setColumnName("TIMESTAMPDIFF");
                  this.setFunctionArguments(this.getNewFunctionArgs("'%Y-%m-01'", arguments));
               }
            } else {
               newArguments.add(this.functionArguments.get(2));
               newArguments.add(this.functionArguments.get(1));
               this.setFunctionArguments(newArguments);
            }
         }
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

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String toDate = StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString());
      String fromDate = StringFunctions.handleLiteralStringDateForInformix(arguments.get(1).toString());
      String qry = "(DATE(" + toDate + ") - DATE(" + fromDate + "))";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nThe function DATEDIFF is not supported in TimesTen 5.1.21\n");
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

   private FunctionCalls getNewFunction(String newFunctionName, SelectColumn sc) {
      FunctionCalls newFunction = new FunctionCalls();
      Vector args = new Vector();
      TableColumn tc = new TableColumn();
      tc.setColumnName(newFunctionName);
      args.add(sc);
      newFunction.setFunctionName(tc);
      newFunction.setFunctionArguments(args);
      return newFunction;
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int i_count;
      if (this.functionName.getColumnName().equalsIgnoreCase("datediff")) {
         String[] arguments = new String[2];

         for(i_count = 0; i_count < 2; ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               this.handleStringLiteralForDateTime(from_sqs, i_count, false);
               arguments[i_count] = "" + ((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs);
            } else {
               arguments[i_count] = "" + this.functionArguments.elementAt(i_count);
            }
         }

         this.functionName.setColumnName("TIMESTAMPDIFF(DAY,CAST(" + arguments[1] + " AS DATE) , CAST(" + arguments[0] + " AS DATE))");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("unix_timestamp")) {
         Vector arguments = new Vector();

         for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               this.handleStringLiteralForDateTime(from_sqs, 0, false);
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
      }

   }

   private FunctionCalls getNewFunction(String newFunctionName, SelectColumn[] scArr) {
      FunctionCalls newFunction = new FunctionCalls();
      Vector args = new Vector();
      TableColumn tc = new TableColumn();
      tc.setColumnName(newFunctionName);
      if (scArr != null) {
         SelectColumn[] var6 = scArr;
         int var7 = scArr.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            SelectColumn sc = var6[var8];
            args.add(sc);
         }
      }

      newFunction.setFunctionName(tc);
      newFunction.setFunctionArguments(args);
      return newFunction;
   }

   private FunctionCalls getNewFunction(String newFunctionName, Vector fnArgs) {
      FunctionCalls newFunction = new FunctionCalls();
      Vector args = new Vector();
      TableColumn tc = new TableColumn();
      tc.setColumnName(newFunctionName);
      if (fnArgs != null) {
         args.addAll(fnArgs);
      }

      newFunction.setFunctionName(tc);
      newFunction.setFunctionArguments(args);
      return newFunction;
   }

   private Vector getNewFunctionArgs(String format, Vector arguments) {
      SelectColumn newSelectColumn1 = new SelectColumn();
      Vector colExp1 = new Vector();
      SelectColumn newSelectColumn2 = new SelectColumn();
      Vector colExp2 = new Vector();
      SelectColumn fromDate = (SelectColumn)arguments.get(1);
      SelectColumn toDate = (SelectColumn)arguments.get(2);
      SelectColumn[] arr = new SelectColumn[]{fromDate, this.getStartDate(fromDate)};
      FunctionCalls newFunction1 = this.getNewFunction("DATE_SUB", arr);
      colExp1.add(newFunction1);
      newSelectColumn1.setColumnExpression(colExp1);
      SelectColumn[] arr2 = new SelectColumn[]{toDate, this.getStartDate(toDate)};
      FunctionCalls newFunction2 = this.getNewFunction("DATE_SUB", arr2);
      colExp2.add(newFunction2);
      newSelectColumn2.setColumnExpression(colExp2);
      Vector newArgs = new Vector();
      newArgs.add(arguments.get(0));
      newArgs.add(newSelectColumn1);
      newArgs.add(newSelectColumn2);
      return newArgs;
   }

   public SelectColumn getStartDate(SelectColumn Date) {
      SelectColumn selectColumn = new SelectColumn();
      Vector colExp1 = new Vector();
      TableColumn interval = new TableColumn();
      interval.setColumnName("INTERVAL");
      colExp1.add(interval);
      SelectColumn dOM = new SelectColumn();
      Vector dOM_Fu = new Vector();
      dOM.setOpenBrace("(");
      FunctionCalls day_of_month = this.getNewFunction("DAYOFMONTH", Date);
      dOM_Fu.add(day_of_month);
      dOM_Fu.add("- 1 ");
      dOM.setCloseBrace(")");
      dOM.setColumnExpression(dOM_Fu);
      colExp1.add(dOM);
      colExp1.add("DAY");
      selectColumn.setColumnExpression(colExp1);
      return selectColumn;
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

      Vector argumentnew = new Vector();
      if (arguments.size() == 3) {
         argumentnew.addElement("CAST( " + arguments.get(1).toString() + " AS DATETIME)");
         argumentnew.addElement("CAST( " + arguments.get(2).toString() + " AS DATETIME)");
         argumentnew.addElement(arguments.get(0).toString());
      } else if (arguments.size() == 2) {
         argumentnew.addElement("CAST( " + arguments.get(0).toString() + " AS DATETIME)");
         argumentnew.addElement("CAST( " + arguments.get(1).toString() + " AS DATETIME)");
         argumentnew.addElement("DAY");
      } else if (arguments.size() == 1) {
         argumentnew.addElement("CAST( " + arguments.get(0).toString() + " AS DATETIME)");
         argumentnew.addElement("CURRENT_TIME");
         argumentnew.addElement("DAY");
      }

      this.functionName.setColumnName("DATETIME_DIFF");
      this.setFunctionArguments(argumentnew);
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE_DIFF");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector newArguments = new Vector();
      if (arguments.size() == 2) {
         newArguments.addElement("'day'");
         newArguments.addElement(StringFunctions.handleLiteralStringDateForAthena(arguments.get(1).toString()));
         newArguments.addElement(StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString()));
         this.setFunctionArguments(newArguments);
      } else {
         this.setFunctionArguments(arguments);
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

      String qry = "-(DAYS_BETWEEN(CAST(" + StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString()) + " AS DATE),CAST(" + StringFunctions.handleLiteralStringDateForSapHana(arguments.get(1).toString()) + " AS DATE)))";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
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

      String qry = "cast((julianday(date(" + StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString()) + ")) - julianday(date(" + StringFunctions.handleLiteralStringDateForSqlite(arguments.get(1).toString()) + "))) as integer)";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }
}
