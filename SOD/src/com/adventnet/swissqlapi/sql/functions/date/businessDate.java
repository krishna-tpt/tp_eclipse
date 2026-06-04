package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class businessDate extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName().toUpperCase();
      Vector arguments;
      String weekendPattern;
      if (from_sqs != null && from_sqs.isMySqlLive()) {
         arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "";
         if (from_sqs.isHyperSql()) {
            weekendPattern = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
               qry = "CASE WHEN " + arguments.get(1).toString() + " < 0 THEN NULL WHEN " + arguments.get(1).toString() + " = 0 THEN " + weekendPattern + " ELSE DATEADD(DAY, ((TRUNC( (" + arguments.get(1).toString() + ") / (" + arguments.get(2).toString() + ") ) * 7) + CAST(SUBSTRING(" + arguments.get(4).toString() + ", (( (" + arguments.get(3).toString() + ") * MOD(DAYOFWEEK(" + weekendPattern + ") +5 , 7) ) + ( MOD( (" + arguments.get(1).toString() + ") , (" + arguments.get(2).toString() + ") ) * 2 ) + 1), 2) AS INT)), " + weekendPattern + ") END";
            } else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
               qry = "CASE WHEN (" + weekendPattern + ">" + arguments.get(1).toString() + ") THEN 0 WHEN (" + weekendPattern + "=" + arguments.get(1).toString() + ") THEN (CASE WHEN ((SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1') OR (" + arguments.get(3).toString() + " <= " + arguments.get(2).toString() + ") OR (" + arguments.get(3).toString() + " <= " + arguments.get(6).toString() + ") OR (" + arguments.get(2).toString() + " >= " + arguments.get(7).toString() + ")) THEN 0 ELSE TRUNC((LEAST(" + arguments.get(7).toString() + ", " + arguments.get(3).toString() + ")-GREATEST(" + arguments.get(6).toString() + ", " + arguments.get(2).toString() + ")) / 3600) END) ELSE TRUNC(( (CASE WHEN SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1' THEN 0 ELSE ((CASE WHEN " + arguments.get(2).toString() + " < " + arguments.get(6).toString() + " THEN " + arguments.get(8).toString() + " ELSE (CASE WHEN " + arguments.get(2).toString() + " > " + arguments.get(7).toString() + " THEN 0 ELSE (" + arguments.get(7).toString() + "-" + arguments.get(2).toString() + ") END) END)) END) + (((" + arguments.get(9).toString() + ") * TRUNC(DATEDIFF(" + arguments.get(1).toString() + ", " + weekendPattern + ") / 7)) + CAST(SUBSTRING(" + arguments.get(10).toString() + ", ((7 * " + arguments.get(4).toString() + ") + (" + arguments.get(5).toString() + " + 1)), 1) AS INT) - (CASE WHEN SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1' THEN 0 ELSE 1 END))* " + arguments.get(8).toString() + " + (CASE WHEN SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(5).toString() + "+1),1)='1' THEN 0 ELSE ((CASE WHEN " + arguments.get(3).toString() + " < " + arguments.get(6).toString() + " THEN 0 ELSE (CASE WHEN " + arguments.get(3).toString() + " > " + arguments.get(7).toString() + " THEN " + arguments.get(8).toString() + " ELSE (" + arguments.get(3).toString() + "-" + arguments.get(6).toString() + ") END) END )) END) ) / 3600 ) END";
            } else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
               qry = "CASE WHEN (" + weekendPattern + ">" + arguments.get(1).toString() + ") THEN 0 WHEN (" + weekendPattern + "=" + arguments.get(1).toString() + ") THEN (CASE WHEN ((SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1') OR (" + arguments.get(3).toString() + " <= " + arguments.get(2).toString() + ") OR (" + arguments.get(3).toString() + " <= " + arguments.get(6).toString() + ") OR (" + arguments.get(2).toString() + " >= " + arguments.get(7).toString() + ")) THEN 0 ELSE TRUNC((LEAST(" + arguments.get(7).toString() + ", " + arguments.get(3).toString() + ")-GREATEST(" + arguments.get(6).toString() + ", " + arguments.get(2).toString() + ")) / 60) END) ELSE TRUNC(( (CASE WHEN SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1' THEN 0 ELSE ( (CASE WHEN " + arguments.get(2).toString() + " < " + arguments.get(6).toString() + " THEN " + arguments.get(8).toString() + " ELSE (CASE WHEN " + arguments.get(2).toString() + " > " + arguments.get(7).toString() + " THEN 0 ELSE (" + arguments.get(7).toString() + "-" + arguments.get(2).toString() + ") END ) END ) ) END ) + (((" + arguments.get(9).toString() + ") * TRUNC(DATEDIFF(" + arguments.get(1).toString() + ", " + weekendPattern + ") / 7)) + CAST(SUBSTRING(" + arguments.get(10).toString() + ", ((7 * " + arguments.get(4).toString() + ") + (" + arguments.get(5).toString() + " + 1)), 1) AS INT) - ( CASE WHEN SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1' THEN 0 ELSE 1 END ) ) * " + arguments.get(8).toString() + " + ( CASE WHEN SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(5).toString() + "+1),1)='1' THEN 0 ELSE ( (CASE WHEN " + arguments.get(3).toString() + " < " + arguments.get(6).toString() + " THEN 0 ELSE ( CASE WHEN " + arguments.get(3).toString() + " > " + arguments.get(7).toString() + " THEN " + arguments.get(8).toString() + " ELSE (" + arguments.get(3).toString() + "-" + arguments.get(6).toString() + ") END ) END ) ) END) ) / 60 ) END";
            } else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
               qry = "CASE WHEN (" + weekendPattern + ">" + arguments.get(1).toString() + ") THEN 0 ELSE ((" + arguments.get(2).toString() + " * TRUNC(DATEDIFF(" + arguments.get(1).toString() + ", " + weekendPattern + ") / 7)) + CAST(SUBSTRING(" + arguments.get(3).toString() + ", ((7 * MOD(DAYOFWEEK(" + weekendPattern + ") +5 , 7)) + (MOD(DAYOFWEEK(" + arguments.get(1).toString() + ") +5 , 7) + 1)), 1) AS INT)) END";
            }
         } else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
            qry = "CASE WHEN " + arguments.get(1).toString() + " < 0 THEN NULL WHEN " + arguments.get(1).toString() + " = 0 THEN " + arguments.get(0).toString() + "ELSE DATE_ADD(" + arguments.get(0).toString() + ", INTERVAL  ((( (" + arguments.get(1).toString() + ")  DIV  (" + arguments.get(2).toString() + ") ) * 7) + CAST(MID(" + arguments.get(4).toString() + ", (( (" + arguments.get(3).toString() + ") * WEEKDAY(" + arguments.get(0).toString() + ") ) + ( ( (" + arguments.get(1).toString() + ") % (" + arguments.get(2).toString() + ") ) * 2 ) + 1), 2) AS SIGNED))  DAY) END";
         } else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
            qry = "CASE WHEN (" + arguments.get(0).toString() + ">" + arguments.get(1).toString() + ") THEN 0 WHEN (" + arguments.get(0).toString() + "=" + arguments.get(1).toString() + ") THEN if( ((MID(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1') OR (" + arguments.get(3).toString() + " <= " + arguments.get(2).toString() + ") OR (" + arguments.get(3).toString() + " <= " + arguments.get(6).toString() + ") OR (" + arguments.get(2).toString() + " >= " + arguments.get(7).toString() + ")), 0, (LEAST(" + arguments.get(7).toString() + ", " + arguments.get(3).toString() + ")-GREATEST(" + arguments.get(6).toString() + ", " + arguments.get(2).toString() + ")) div 3600  ) ELSE (( if(MID(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1', 0, (if(" + arguments.get(2).toString() + " < " + arguments.get(6).toString() + " , " + arguments.get(8).toString() + " , if(" + arguments.get(2).toString() + " > " + arguments.get(7).toString() + " , 0 , (" + arguments.get(7).toString() + "-" + arguments.get(2).toString() + ") )))) + (((" + arguments.get(9).toString() + ") * (DATEDIFF(" + arguments.get(1).toString() + ", " + arguments.get(0).toString() + ") DIV 7)) + CAST(MID(" + arguments.get(10).toString() + ", ((7 * " + arguments.get(4).toString() + ") + (" + arguments.get(5).toString() + " + 1)), 1) AS SIGNED) - if(MID(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1', 0,1))* " + arguments.get(8).toString() + " + if(MID(" + arguments.get(11).toString() + ",(" + arguments.get(5).toString() + "+1),1)='1', 0,(if(" + arguments.get(3).toString() + " < " + arguments.get(6).toString() + " , 0 , if(" + arguments.get(3).toString() + " > " + arguments.get(7).toString() + " , " + arguments.get(8).toString() + " , (" + arguments.get(3).toString() + "-" + arguments.get(6).toString() + ") )))) ) div 3600 ) END";
         } else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
            qry = "CASE WHEN (" + arguments.get(0).toString() + ">" + arguments.get(1).toString() + ") THEN 0 WHEN (" + arguments.get(0).toString() + "=" + arguments.get(1).toString() + ") THEN if( ((MID(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1') OR (" + arguments.get(3).toString() + " <= " + arguments.get(2).toString() + ") OR (" + arguments.get(3).toString() + " <= " + arguments.get(6).toString() + ") OR (" + arguments.get(2).toString() + " >= " + arguments.get(7).toString() + ")), 0, (LEAST(" + arguments.get(7).toString() + ", " + arguments.get(3).toString() + ")-GREATEST(" + arguments.get(6).toString() + ", " + arguments.get(2).toString() + ")) div 60  ) ELSE (( if(MID(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1', 0, (if(" + arguments.get(2).toString() + " < " + arguments.get(6).toString() + " , " + arguments.get(8).toString() + " , if(" + arguments.get(2).toString() + " > " + arguments.get(7).toString() + " , 0 , (" + arguments.get(7).toString() + "-" + arguments.get(2).toString() + ") )))) + (((" + arguments.get(9).toString() + ") * (DATEDIFF(" + arguments.get(1).toString() + ", " + arguments.get(0).toString() + ") DIV 7)) + CAST(MID(" + arguments.get(10).toString() + ", ((7 * " + arguments.get(4).toString() + ") + (" + arguments.get(5).toString() + " + 1)), 1) AS SIGNED) - if(MID(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1', 0,1))* " + arguments.get(8).toString() + " + if(MID(" + arguments.get(11).toString() + ",(" + arguments.get(5).toString() + "+1),1)='1', 0,(if(" + arguments.get(3).toString() + " < " + arguments.get(6).toString() + " , 0 , if(" + arguments.get(3).toString() + " > " + arguments.get(7).toString() + " , " + arguments.get(8).toString() + " , (" + arguments.get(3).toString() + "-" + arguments.get(6).toString() + ") )))) ) div 60 ) END";
         } else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
            qry = "CASE WHEN (" + arguments.get(0).toString() + ">" + arguments.get(1).toString() + ") THEN 0 ELSE ((" + arguments.get(2).toString() + " * (DATEDIFF(" + arguments.get(1).toString() + ", " + arguments.get(0).toString() + ") DIV 7)) + CAST(MID(" + arguments.get(3).toString() + ", ((7 * WEEKDAY(" + arguments.get(0).toString() + ")) + (WEEKDAY(" + arguments.get(1).toString() + ") + 1)), 1) as signed)) END";
         }

         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         Vector vector1;
         int i_count;
         String str_weekdays_len;
         Vector remainingDays;
         int i;
         String weekendPattern;
         if (fnStr.equalsIgnoreCase("BUSINESS_DAYS")) {
            this.functionName.setColumnName("ZR_BUSINESS_DAYS");
            arguments = new Vector();
            vector1 = new Vector();

            for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  vector1.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
               } else {
                  vector1.addElement(this.functionArguments.elementAt(i_count));
               }
            }

            weekendPattern = null;
            int weekdays = false;
            str_weekdays_len = null;
            if (this.functionArguments.size() >= 3 && vector1.elementAt(2) instanceof SelectColumn) {
               SelectColumn sc_weekend = (SelectColumn)vector1.elementAt(2);
               remainingDays = sc_weekend.getColumnExpression();
               if (!(remainingDays.elementAt(0) instanceof String)) {
                  throw new ConvertException("Invalid Argument Value for Function" + this.functionName.getColumnName(), "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{fnStr, "EXCLUDE_WEEKENDS"});
               }

               weekendPattern = (String)remainingDays.elementAt(0);
               weekendPattern = weekendPattern.replaceAll("'", "");
               if (weekendPattern.length() >= 7 && !weekendPattern.contains(",")) {
                  if (weekendPattern.length() != 7) {
                     throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "EXCLUDE_WEEKENDS", "Provide values between 1 to 7 or combination between 1 to 7 separeted by comma"});
                  }

                  StringBuilder sb = new StringBuilder();
                  this.validateExcludeWeekendAsString(weekendPattern, fnStr);

                  for(i = 0; i < 7; ++i) {
                     if (weekendPattern.charAt(i) == '1') {
                        int a = i + 1;
                        sb.append(a);
                     }
                  }

                  weekendPattern = sb.toString();
               } else {
                  this.validateExcludeWeekendAsCharArray(weekendPattern, fnStr);
                  weekendPattern = weekendPattern.replaceAll(",", "");
                  char[] tobesorted = weekendPattern.toCharArray();
                  Arrays.sort(tobesorted);
                  weekendPattern = new String(tobesorted);
               }
            }

            int weekdays;
            if (weekendPattern != null) {
               weekdays = 7 - weekendPattern.length();
               str_weekdays_len = this.getBusiness_DaysFnRemainingDaysMatrixMap(weekendPattern);
               if (str_weekdays_len == null) {
                  str_weekdays_len = this.getRemainingDaysForBusiness_DaysFnAndBusiness_HoursFn(weekendPattern);
               }
            } else {
               str_weekdays_len = "'0123455401234434012332340122123401101234000123450'";
               weekdays = 5;
            }

            weekendPattern = Integer.toString(weekdays);
            arguments.addElement(vector1.get(0));
            arguments.addElement(vector1.get(1));
            arguments.addElement(weekendPattern);
            arguments.addElement(str_weekdays_len);
            this.setFunctionArguments(arguments);
         } else {
            Vector vc_weekend;
            char[] tobesorted;
            int k;
            int[] weekend;
            int weekdays_lentwice;
            SelectColumn sc_Date;
            StringBuilder sb;
            int index;
            int b;
            if (!fnStr.equalsIgnoreCase("BUSINESS_HOURS") && !fnStr.equalsIgnoreCase("BUSINESS_MINUTES")) {
               if (fnStr.equalsIgnoreCase("BUSINESS_COMPLETION_DAY")) {
                  this.functionName.setColumnName("ZR_BUSINESS_ENDDAY");
                  arguments = new Vector();
                  vector1 = new Vector();

                  for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                     if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                        vector1.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
                     } else {
                        vector1.addElement(this.functionArguments.elementAt(i_count));
                     }
                  }

                  new String();
                  String weekend_input = null;
                  str_weekdays_len = "";
                  weekendPattern = "";
                  int weekend_len = 0;
                  weekdays_lentwice = 0;
                  i = 0;
                  int x;
                  if (this.functionArguments.size() > 2 && vector1.elementAt(2) instanceof SelectColumn) {
                     sc_Date = (SelectColumn)vector1.elementAt(2);
                     vc_weekend = sc_Date.getColumnExpression();
                     if (!(vc_weekend.elementAt(0) instanceof String)) {
                        throw new ConvertException("Invalid Argument Value for Function" + this.functionName.getColumnName(), "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{fnStr, "EXCLUDE_WEEKENDS"});
                     }

                     weekend_input = (String)vc_weekend.elementAt(0);
                     weekend_input = weekend_input.replaceAll("'", "");
                     if (weekend_input.length() >= 7 && !weekend_input.contains(",")) {
                        if (weekend_input.length() != 7) {
                           throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "EXCLUDE_WEEKENDS", "Provide values between 1 to 7 or combination between 1 to 7 separeted by comma"});
                        }

                        sb = new StringBuilder();
                        this.validateExcludeWeekendAsString(weekend_input, fnStr);

                        for(k = 0; k < 7; ++k) {
                           if (weekend_input.charAt(k) == '1') {
                              x = k + 1;
                              sb.append(x);
                           }
                        }

                        weekend_input = sb.toString();
                     } else {
                        this.validateExcludeWeekendAsCharArray(weekend_input, fnStr);
                        weekend_input = weekend_input.replaceAll(",", "");
                        tobesorted = weekend_input.toCharArray();
                        Arrays.sort(tobesorted);
                        weekend_input = new String(tobesorted);
                     }

                     weekend_len = weekend_input.length();
                     i = 7 - weekend_len;
                     weekdays_lentwice = i * 2;
                  }

                  if (weekend_input == null) {
                     weekendPattern = "'00010203040001020306000102050600010405060003040506-102030405-201020304'";
                     i = 5;
                     weekdays_lentwice = 10;
                  } else {
                     weekendPattern = this.getBusinessEndDateFnRemainingDaysForWeekendPatterns(weekend_input);
                     if (weekendPattern == null) {
                        char[][] arr = new char[7][weekdays_lentwice];
                        int b = false;
                        weekend = new int[weekend_len];
                        int[] wwkk = new int[weekend_len];

                        int i;
                        for(i = 0; i < weekend_len; ++i) {
                           if (weekend_input.charAt(i) == '1') {
                              weekend[i] = 6;
                              wwkk[i] = 6;
                           } else {
                              weekend[i] = weekend_input.charAt(i) - 48 - 2;
                              wwkk[i] = weekend_input.charAt(i) - 48 - 2;
                           }
                        }

                        i = 0;

                        while(true) {
                           int j;
                           if (i >= 7) {
                              StringBuilder sb = new StringBuilder();
                              sb.append("'");

                              for(i = 0; i < 7; ++i) {
                                 for(j = 0; j < weekdays_lentwice; ++j) {
                                    sb.append(arr[i][j]);
                                 }
                              }

                              sb.append("'");
                              weekendPattern = sb.toString();
                              break;
                           }

                           k = i;

                           for(index = 0; index < weekend_len; ++index) {
                              if (k > weekend[index]) {
                                 weekend[index] += 7;
                              }

                              Arrays.sort(weekend);
                           }

                           for(j = 0; j < weekdays_lentwice; ++j) {
                              if (j == 0) {
                                 for(index = 0; index < weekend_len; ++index) {
                                    if (k == weekend[index]) {
                                       arr[i][j] = '-';
                                       break;
                                    }

                                    arr[i][j] = '0';
                                 }
                              } else if (j == 1) {
                                 b = 0;
                                 int l = i;

                                 for(index = weekend_len - 1; index >= 0; --index) {
                                    if (l == wwkk[index]) {
                                       ++b;
                                       --l;
                                    }

                                    if (l == -1) {
                                       l = 6;
                                    }
                                 }

                                 if (b == 0) {
                                    arr[i][j] = '0';
                                 } else {
                                    arr[i][j] = (char)(b + 48);
                                 }
                              } else if (j % 2 == 0) {
                                 arr[i][j] = '0';
                              } else {
                                 x = 0;

                                 for(index = 0; index < weekend_len; ++index) {
                                    if (k < weekend[index] && j / 2 + k + x >= weekend[index]) {
                                       ++x;
                                    }
                                 }

                                 int A = j / 2 + x;
                                 arr[i][j] = (char)(A + 48);
                              }
                           }

                           ++i;
                        }
                     }
                  }

                  str_weekdays_len = Integer.toString(i);
                  weekendPattern = Integer.toString(weekdays_lentwice);
                  sc_Date = new SelectColumn();
                  FunctionCalls fn_Date = new FunctionCalls();
                  TableColumn tb_Date = new TableColumn();
                  tb_Date.setColumnName("DATE");
                  fn_Date.setFunctionName(tb_Date);
                  Vector vc_DateIn = new Vector();
                  Vector vc_DateOut = new Vector();
                  vc_DateIn.addElement(vector1.get(0));
                  fn_Date.setFunctionArguments(vc_DateIn);
                  vc_DateOut.addElement(fn_Date);
                  sc_Date.setColumnExpression(vc_DateOut);
                  arguments.addElement(sc_Date);
                  arguments.addElement(vector1.get(1));
                  arguments.addElement(str_weekdays_len);
                  arguments.addElement(weekendPattern);
                  arguments.addElement(weekendPattern);
                  this.setFunctionArguments(arguments);
               }
            } else {
               if (fnStr.equalsIgnoreCase("BUSINESS_HOURS")) {
                  this.functionName.setColumnName("ZR_BUSINESS_HOURS");
               } else {
                  this.functionName.setColumnName("ZR_BUSINESS_MINUTES");
               }

               arguments = new Vector();
               vector1 = new Vector();
               Vector vector2 = new Vector();
               Vector vector3 = new Vector();

               for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                  if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                     vector1.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
                     vector2.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
                     vector3.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
                  } else {
                     vector1.addElement(this.functionArguments.elementAt(i_count));
                     vector2.addElement(this.functionArguments.elementAt(i_count));
                     vector3.addElement(this.functionArguments.elementAt(i_count));
                  }
               }

               StringBuilder notinWeekEndIndex = new StringBuilder();
               weekendPattern = null;
               remainingDays = null;
               int weekdays = false;
               if (this.functionArguments.size() > 4 && vector1.elementAt(4) instanceof SelectColumn) {
                  sc_Date = (SelectColumn)vector1.elementAt(4);
                  vc_weekend = sc_Date.getColumnExpression();
                  if (!(vc_weekend.elementAt(0) instanceof String)) {
                     throw new ConvertException("Invalid Argument Value for Function" + this.functionName.getColumnName(), "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{fnStr, "EXCLUDE_WEEKENDS"});
                  }

                  weekendPattern = (String)vc_weekend.elementAt(0);
                  weekendPattern = weekendPattern.replaceAll("'", "");
                  if (weekendPattern.length() >= 7 && !weekendPattern.contains(",")) {
                     if (weekendPattern.length() != 7) {
                        throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "EXCLUDE_WEEKENDS", "Provide values between 1 to 7 or combination between 1 to 7 separeted by comma"});
                     }

                     this.validateExcludeWeekendAsString(weekendPattern, fnStr);
                     sb = new StringBuilder();

                     for(i = 0; i < 7; ++i) {
                        if (weekendPattern.charAt(i) == '1') {
                           k = i + 1;
                           sb.append(k);
                        }
                     }

                     weekendPattern = sb.toString();
                  } else {
                     this.validateExcludeWeekendAsCharArray(weekendPattern, fnStr);
                     weekendPattern = weekendPattern.replaceAll(",", "");
                     tobesorted = weekendPattern.toCharArray();
                     Arrays.sort(tobesorted);
                     weekendPattern = new String(tobesorted);
                  }
               }

               String remainingDays;
               if (weekendPattern == null) {
                  remainingDays = "'0123455401234434012332340122123401101234000123450'";
                  weekdays_lentwice = 5;
                  notinWeekEndIndex.append("'0000011'");
               } else {
                  weekdays_lentwice = 7 - weekendPattern.length();
                  remainingDays = this.getBusiness_DaysFnRemainingDaysMatrixMap(weekendPattern);
                  if (remainingDays == null) {
                     remainingDays = this.getRemainingDaysForBusiness_DaysFnAndBusiness_HoursFn(weekendPattern);
                  }

                  notinWeekEndIndex.append("'");
                  int[] weekend = new int[7];

                  for(i = 0; i < weekendPattern.length(); ++i) {
                     if (weekendPattern.charAt(i) == '1') {
                        weekend[6] = 1;
                     } else {
                        weekend[weekendPattern.charAt(i) - 48 - 2] = 1;
                     }
                  }

                  for(i = 0; i < 7; ++i) {
                     notinWeekEndIndex.append(weekend[i]);
                  }

                  notinWeekEndIndex.append("'");
               }

               String str_weekDaysCount = Integer.toString(weekdays_lentwice);
               String str_weekEndIndex = notinWeekEndIndex.toString();
               new String();
               k = 0;
               if (vector1.elementAt(2) instanceof SelectColumn) {
                  SelectColumn sc_wst = (SelectColumn)vector1.elementAt(2);
                  Vector vc_wst = sc_wst.getColumnExpression();
                  if (!(vc_wst.elementAt(0) instanceof String)) {
                     throw new ConvertException("Invalid Argument Value for Function" + this.functionName.getColumnName(), "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{fnStr, "WORK_START_TIME"});
                  }

                  String wst = (String)vc_wst.elementAt(0);
                  wst = wst.replaceAll("'", "");
                  wst = wst.trim();
                  this.validateWorkStartAndEndTime(wst, "WORK_START_TIME", fnStr, 0);
                  String[] array = wst.split(":");
                  int[] a = new int[3];

                  for(i = 0; i < 3; ++i) {
                     a[i] = Integer.parseInt(array[i]);
                  }

                  k += a[0] * 3600 + a[1] * 60 + a[2];
               }

               new String();
               index = 0;
               if (vector1.elementAt(3) instanceof SelectColumn) {
                  SelectColumn sc_wet = (SelectColumn)vector1.elementAt(3);
                  Vector vc_wet = sc_wet.getColumnExpression();
                  if (!(vc_wet.elementAt(0) instanceof String)) {
                     throw new ConvertException("Invalid Argument Value for Function" + this.functionName.getColumnName(), "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{fnStr, "WORK_END_TIME"});
                  }

                  String wet = (String)vc_wet.elementAt(0);
                  wet = wet.replaceAll("'", "");
                  wet = wet.trim();
                  this.validateWorkStartAndEndTime(wet, "WORK_END_TIME", fnStr, k);
                  String[] array = wet.split(":");
                  weekend = new int[3];

                  for(i = 0; i < 3; ++i) {
                     weekend[i] = Integer.parseInt(array[i]);
                  }

                  index += weekend[0] * 3600 + weekend[1] * 60 + weekend[2];
               }

               b = index - k;
               String str_wst = Integer.toString(k);
               String str_wet = Integer.toString(index);
               String str_wt = Integer.toString(b);
               arguments.addElement(this.date(vector1, 0));
               arguments.addElement(this.date(vector1, 1));
               arguments.addElement(this.time_to_sec(vector2, 0));
               arguments.addElement(this.time_to_sec(vector2, 1));
               arguments.addElement(this.weekday(vector3, 0));
               arguments.addElement(this.weekday(vector3, 1));
               arguments.addElement(str_wst);
               arguments.addElement(str_wet);
               arguments.addElement(str_wt);
               arguments.addElement(str_weekDaysCount);
               arguments.addElement(remainingDays);
               arguments.addElement(str_weekEndIndex);
               this.setFunctionArguments(arguments);
            }
         }
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      boolean isVertica = from_sqs != null && from_sqs.isVerticaDb();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            if (i_count == 0 || i_count == 1 && (fnStr.trim().equalsIgnoreCase("ZR_BUSINESS_DAYS") || fnStr.trim().equalsIgnoreCase("ZR_BUSINESS_HOURS") || fnStr.trim().equalsIgnoreCase("ZR_BUSINESS_MINUTES"))) {
               this.handleStringLiteralForDate(from_sqs, i_count, !isPostgreLiveDbs);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         }
      }

      this.setFunctionArguments(arguments);
      if (isPostgreLiveDbs) {
         String qry = "";
         String dataType = isVertica ? "VARCHAR" : "TEXT";
         String divType = isVertica ? "//" : "/";
         String start_date;
         String end_date;
         String secof_startdate;
         String secof_enddate;
         if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
            start_date = arguments.get(0).toString();
            end_date = arguments.get(1).toString();
            secof_startdate = arguments.get(2).toString();
            secof_enddate = arguments.get(3).toString();
            qry = "CASE WHEN (DATE(" + start_date + ") > DATE(" + end_date + ")) THEN 0 ELSE ((" + secof_startdate + " * (DATE_MI(DATE(" + end_date + "), DATE(" + start_date + ")) " + divType + " 7)) + cast(substring((" + secof_enddate + ")::" + dataType + ",((7 *  mod(cast(date_part('dow' ,DATE(" + start_date + ")) as int) +6, 7)) + ( mod(cast(date_part('dow' ,DATE(" + end_date + ")) as int) +6, 7) + 1)),1) as BIGINT)) END";
         } else {
            String weekdayof_startdate;
            if (!fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS") && !fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
               if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
                  start_date = arguments.get(0).toString();
                  end_date = arguments.get(1).toString();
                  secof_startdate = arguments.get(2).toString();
                  secof_enddate = arguments.get(3).toString();
                  weekdayof_startdate = arguments.get(4).toString();
                  qry = "(CASE WHEN " + end_date + " < 0 THEN NULL ELSE (CASE WHEN " + end_date + "  = 0 THEN " + start_date + " ELSE (" + start_date + " + ( INTERVAL '1 DAY' * ROUND(((((" + end_date + ") " + divType + " (" + secof_startdate + ")) * 7) + cast(substring((" + weekdayof_startdate + ")::" + dataType + ",(((" + secof_enddate + ") *  mod(cast(date_part('dow' ," + start_date + ") as int) +6, 7)) + (((" + end_date + ") % (" + secof_startdate + ")) * 2) + 1),2) as BIGINT)))) ) END) END)";
               }
            } else {
               start_date = arguments.get(0).toString();
               end_date = arguments.get(1).toString();
               secof_startdate = arguments.get(2).toString();
               secof_enddate = arguments.get(3).toString();
               weekdayof_startdate = arguments.get(4).toString();
               String weekdayof_enddate = arguments.get(5).toString();
               String work_starttime = arguments.get(6).toString();
               String work_endtime = arguments.get(7).toString();
               String work_time = arguments.get(8).toString();
               String weekdays_count = arguments.get(9).toString();
               String remaining_days = arguments.get(10).toString();
               String weekend_string = arguments.get(11).toString();
               if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
                  qry = "(CASE WHEN (DATE(" + start_date + ") > DATE(" + end_date + ")) THEN 0 ELSE (CASE WHEN (DATE(" + start_date + ")  = DATE(" + end_date + ")) THEN (CASE WHEN ((substring((" + weekend_string + ")::" + dataType + ",(" + weekdayof_startdate + " + 1),1)  = '1') OR\t(" + secof_enddate + "  <= " + secof_startdate + ") OR\t(" + secof_enddate + "  <= " + work_starttime + ") OR (" + secof_startdate + "  >= " + work_endtime + ")) THEN 0 ELSE (LEAST(" + work_endtime + ", " + secof_enddate + ") -GREATEST(" + work_starttime + ", " + secof_startdate + ")) " + divType + " 3600 END) ELSE ((CASE WHEN substring((" + weekend_string + ")::" + dataType + ",(" + weekdayof_startdate + " + 1),1)  = '1' THEN 0 ELSE ((CASE WHEN " + secof_startdate + "  < " + work_starttime + " THEN " + work_time + " WHEN " + secof_startdate + "  > " + work_endtime + " THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ")  END)) END) + (((" + weekdays_count + ") * (DATE_MI(DATE(" + end_date + "), DATE(" + start_date + ")) " + divType + " 7)) + cast(substring((" + remaining_days + ")::" + dataType + ",((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)),1) as BIGINT) -(CASE WHEN substring((" + weekend_string + ")::" + dataType + ",(" + weekdayof_startdate + " + 1),1)  = '1' THEN 0 ELSE 1 END)) * " + work_time + " + (CASE WHEN substring((" + weekend_string + ")::" + dataType + ",(" + weekdayof_enddate + " + 1),1)  = '1' THEN 0 ELSE ((CASE WHEN " + secof_enddate + "  < " + work_starttime + " THEN 0 WHEN " + secof_enddate + "  > " + work_endtime + " THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ")  END)) END)) " + divType + " 3600 END) END)";
               } else {
                  qry = "(CASE WHEN (DATE(" + start_date + ") > DATE(" + end_date + ")) THEN 0 ELSE (CASE WHEN (DATE(" + start_date + ")  = DATE(" + end_date + ")) THEN (CASE WHEN ((substring((" + weekend_string + ")::" + dataType + ",(" + weekdayof_startdate + " + 1),1)  = '1') OR\t(" + secof_enddate + "  <= " + secof_startdate + ") OR\t(" + secof_enddate + "  <= " + work_starttime + ") OR (" + secof_startdate + "  >= " + work_endtime + ")) THEN 0 ELSE (LEAST(" + work_endtime + ", " + secof_enddate + ") -GREATEST(" + work_starttime + ", " + secof_startdate + ")) " + divType + " 60 END) ELSE ((CASE WHEN substring((" + weekend_string + ")::" + dataType + ",(" + weekdayof_startdate + " + 1),1)  = '1' THEN 0 ELSE ((CASE WHEN " + secof_startdate + "  < " + work_starttime + " THEN " + work_time + " WHEN " + secof_startdate + "  > " + work_endtime + " THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ")  END)) END) + (((" + weekdays_count + ") * (DATE_MI(DATE(" + end_date + "), DATE(" + start_date + ")) " + divType + " 7)) + cast(substring((" + remaining_days + ")::" + dataType + ",((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)),1) as BIGINT) -(CASE WHEN substring((" + weekend_string + ")::" + dataType + ",(" + weekdayof_startdate + " + 1),1)  = '1' THEN 0 ELSE 1 END)) * " + work_time + " + (CASE WHEN substring((" + weekend_string + ")::" + dataType + ",(" + weekdayof_enddate + " + 1),1)  = '1' THEN 0 ELSE ((CASE WHEN " + secof_enddate + "  < " + work_starttime + " THEN 0 WHEN " + secof_enddate + "  > " + work_endtime + " THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ")  END)) END)) " + divType + " 60 END) END)";
               }
            }
         }

         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      }

   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            if (i_count == 0 || i_count == 1 && (fnStr.trim().equalsIgnoreCase("ZR_BUSINESS_DAYS") || fnStr.trim().equalsIgnoreCase("ZR_BUSINESS_HOURS") || fnStr.trim().equalsIgnoreCase("ZR_BUSINESS_MINUTES"))) {
               this.handleStringLiteralForDate(from_sqs, i_count, false);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         }
      }

      String end_date;
      String secof_startdate;
      String secof_enddate;
      String start_date;
      if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
         start_date = arguments.get(0).toString();
         end_date = arguments.get(1).toString();
         secof_startdate = arguments.get(2).toString();
         secof_enddate = arguments.get(3).toString();
         qry = "CAST(((" + secof_startdate + " * (day(CAST(" + end_date + " AS DATE) - CAST(" + start_date + " AS DATE)) / 7)) + cast(SUBSTRING(CAST(" + secof_enddate + " as VARCHAR), ((7 * mod(int(dayofweek(" + start_date + ")+5),7)) + (mod(int(dayofweek(" + end_date + ")+5),7) + 1)), 1) as BIGINT)) AS BIGINT)";
      } else {
         String weekdayof_startdate;
         if (!fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS") && !fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
               start_date = arguments.get(0).toString();
               end_date = arguments.get(1).toString();
               secof_startdate = arguments.get(2).toString();
               secof_enddate = arguments.get(3).toString();
               weekdayof_startdate = arguments.get(4).toString();
               qry = "IF(" + end_date + "  = 0, " + start_date + ", (timestamp(" + start_date + ") + INTERVAL  '1'  DAY * ((((" + end_date + ") / (" + secof_startdate + ")) * 7) + cast(SUBSTRING(CAST(" + weekdayof_startdate + " as VARCHAR), (((" + secof_enddate + ") * mod(int(dayofweek(" + start_date + ")+5),7)) + ((  MOD((" + end_date + "), (" + secof_startdate + "))  ) * 2) + 1),2) as BIGINT))))";
            }
         } else {
            start_date = arguments.get(0).toString();
            end_date = arguments.get(1).toString();
            secof_startdate = arguments.get(2).toString();
            secof_enddate = arguments.get(3).toString();
            weekdayof_startdate = arguments.get(4).toString();
            String weekdayof_enddate = arguments.get(5).toString();
            String work_starttime = arguments.get(6).toString();
            String work_endtime = arguments.get(7).toString();
            String work_time = arguments.get(8).toString();
            String weekdays_count = arguments.get(9).toString();
            String remaining_days = arguments.get(10).toString();
            String weekend_string = arguments.get(11).toString();
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
               qry = "CAST(IF((CAST(" + start_date + " AS DATE)  = CAST(" + end_date + " AS DATE)), if(((SUBSTRING(CAST(" + weekend_string + " as VARCHAR), (" + weekdayof_startdate + "+1),  1)  = '1') OR (" + secof_enddate + "  <= " + secof_startdate + ") OR (" + secof_enddate + "  <= " + work_starttime + ") OR (" + secof_startdate + "  >= " + work_endtime + ")), 0, (LEAST(" + work_endtime + ", " + secof_enddate + ") -GREATEST(" + work_starttime + ", " + secof_startdate + ")) / 3600), (if(SUBSTRING(CAST(" + weekend_string + " as VARCHAR), (" + weekdayof_startdate + "+1), 1) = '1', 0, (if(" + secof_startdate + "  < " + work_starttime + ", " + work_time + ", if(" + secof_startdate + "  > " + work_endtime + ", 0, (" + work_endtime + " -" + secof_startdate + "))))) + (((" + weekdays_count + ") * (day(CAST(" + end_date + " AS DATE) - CAST(" + start_date + " AS DATE)) / 7)) + cast(SUBSTRING(CAST(" + remaining_days + " as VARCHAR), ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) as BIGINT) -if(SUBSTRING(CAST(" + weekend_string + " as VARCHAR), (" + weekdayof_startdate + " + 1), 1)  = '1', 0, 1)) * " + work_time + " + if(SUBSTRING(CAST(" + weekend_string + " as VARCHAR), (" + weekdayof_enddate + " + 1), 1)  = '1', 0, (if(" + secof_enddate + "  < " + work_starttime + ", 0, if(" + secof_enddate + "  > " + work_endtime + ", " + work_time + ", (" + secof_enddate + " -" + work_starttime + ")))))) / 3600) AS BIGINT)";
            } else {
               qry = "CAST(IF((CAST(" + start_date + " AS DATE)  = CAST(" + end_date + " AS DATE)), if(((SUBSTRING(CAST(" + weekend_string + " as VARCHAR), (" + weekdayof_startdate + "+1),  1)  = '1') OR (" + secof_enddate + "  <= " + secof_startdate + ") OR (" + secof_enddate + "  <= " + work_starttime + ") OR (" + secof_startdate + "  >= " + work_endtime + ")), 0, (LEAST(" + work_endtime + ", " + secof_enddate + ") -GREATEST(" + work_starttime + ", " + secof_startdate + ")) / 60), (if(SUBSTRING(CAST(" + weekend_string + " as VARCHAR), (" + weekdayof_startdate + "+1), 1) = '1', 0, (if(" + secof_startdate + "  < " + work_starttime + ", " + work_time + ", if(" + secof_startdate + "  > " + work_endtime + ", 0, (" + work_endtime + " -" + secof_startdate + "))))) + (((" + weekdays_count + ") * (day(CAST(" + end_date + " AS DATE) - CAST(" + start_date + " AS DATE)) / 7)) + cast(SUBSTRING(CAST(" + remaining_days + " as VARCHAR), ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) as BIGINT) -if(SUBSTRING(CAST(" + weekend_string + " as VARCHAR), (" + weekdayof_startdate + " + 1), 1)  = '1', 0, 1)) * " + work_time + " + if(SUBSTRING(CAST(" + weekend_string + " as VARCHAR), (" + weekdayof_enddate + " + 1), 1)  = '1', 0, (if(" + secof_enddate + "  < " + work_starttime + ", 0, if(" + secof_enddate + "  > " + work_endtime + ", " + work_time + ", (" + secof_enddate + " -" + work_starttime + ")))))) / 60) AS BIGINT)";
            }
         }
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String end_date;
      String secof_startdate;
      String secof_enddate;
      String start_date;
      if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
         start_date = arguments.get(0).toString();
         end_date = arguments.get(1).toString();
         secof_startdate = arguments.get(2).toString();
         secof_enddate = arguments.get(3).toString();
         qry = "CASE WHEN " + start_date + " > " + end_date + " THEN 0 ELSE ((" + secof_startdate + " * (DATEDIFF(dd, " + start_date + ", " + end_date + ") / 7)) + CAST(SUBSTRING(" + secof_enddate + ", ((7 * ((datepart(dw," + start_date + ")+5)%7)) + (((datepart(dw," + end_date + ")+5)%7) + 1)), 1) AS INT)) END";
      } else {
         String weekdayof_startdate;
         if (!fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS") && !fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
               start_date = arguments.get(0).toString();
               end_date = arguments.get(1).toString();
               secof_startdate = arguments.get(2).toString();
               secof_enddate = arguments.get(3).toString();
               weekdayof_startdate = arguments.get(4).toString();
               qry = "(CASE WHEN " + end_date + " < 0 THEN NULL ELSE (CASE WHEN " + end_date + "  = 0 THEN " + start_date + " ELSE DATEADD(DAY, ((((" + end_date + ") / (" + secof_startdate + ")) * 7) + CAST(SUBSTRING(" + weekdayof_startdate + ", (((" + secof_enddate + ") * ((datepart(dw," + start_date + ")+5)%7)) + (((" + end_date + ") % (" + secof_startdate + ")) * 2) + 1), 2) AS INT)), CAST(" + start_date + " AS DATE)) END) END)";
            }
         } else {
            start_date = arguments.get(0).toString();
            end_date = arguments.get(1).toString();
            secof_startdate = arguments.get(2).toString();
            secof_enddate = arguments.get(3).toString();
            weekdayof_startdate = arguments.get(4).toString();
            String weekdayof_enddate = arguments.get(5).toString();
            String work_starttime = arguments.get(6).toString();
            String work_endtime = arguments.get(7).toString();
            String work_time = arguments.get(8).toString();
            String weekdays_count = arguments.get(9).toString();
            String remaining_days = arguments.get(10).toString();
            String weekend_string = arguments.get(11).toString();
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
               qry = "(CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + "  = " + end_date + " THEN CASE WHEN ((SUBSTRING(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 3600) END ELSE ((CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * (DATEDIFF(dd, " + start_date + ", " + end_date + ") / 7)) + CAST(SUBSTRING(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 3600) END) END)";
            } else {
               qry = "(CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + "  = " + end_date + " THEN CASE WHEN ((SUBSTRING(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 60) END ELSE ((CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * (DATEDIFF(dd, " + start_date + ", " + end_date + ") / 7)) + CAST(SUBSTRING(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 60) END) END)";
            }
         }
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();
      String qry = "";
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      String intSyntax = isdenodo ? "INT4" : "INT";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String end_date;
      String secof_startdate;
      String secof_enddate;
      String start_date;
      if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
         start_date = isdenodo ? StringFunctions.handleLiteralStringDateForDenodo(arguments.get(0).toString()) : StringFunctions.handleLiteralStringDateForOracle(arguments.get(0).toString());
         end_date = isdenodo ? StringFunctions.handleLiteralStringDateForDenodo(arguments.get(1).toString()) : StringFunctions.handleLiteralStringDateForOracle(arguments.get(1).toString());
         secof_startdate = arguments.get(2).toString();
         secof_enddate = arguments.get(3).toString();
         qry = "CASE WHEN " + start_date + " > " + end_date + " THEN 0 ELSE ((" + secof_startdate + " * TRUNC((TRUNC(" + end_date + ") - TRUNC(" + start_date + ")) / 7)) + CAST(SUBSTR(" + secof_enddate + ", CAST((7 * (TRUNC(" + start_date + ")-TRUNC(" + start_date + ",'IW'))) + ((TRUNC(" + end_date + ")-TRUNC(" + end_date + ",'IW')) + 1) AS " + intSyntax + "), 1) as " + intSyntax + ")) END";
      } else {
         String weekdayof_startdate;
         if (!fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS") && !fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
               start_date = arguments.get(0).toString();
               end_date = arguments.get(1).toString();
               secof_startdate = arguments.get(2).toString();
               secof_enddate = arguments.get(3).toString();
               weekdayof_startdate = arguments.get(4).toString();
               qry = "(CASE WHEN " + end_date + " < 0 THEN NULL ELSE (CASE WHEN " + end_date + "  = 0 THEN " + start_date + " ELSE   CAST(" + start_date + " AS DATE)+ (INTERVAL '1' DAY *((TRUNC((" + end_date + ") / (" + secof_startdate + ")) * 7) + CAST(SUBSTR(" + weekdayof_startdate + ", CAST((((" + secof_enddate + ") * (TRUNC(" + start_date + ")-TRUNC(" + start_date + ",'IW'))) + (MOD((" + end_date + ") , (" + secof_startdate + ")) * 2) + 1) AS " + intSyntax + "), 2) AS " + intSyntax + "))) END) END)";
            }
         } else {
            start_date = arguments.get(0).toString();
            end_date = arguments.get(1).toString();
            secof_startdate = arguments.get(2).toString();
            secof_enddate = arguments.get(3).toString();
            weekdayof_startdate = arguments.get(4).toString();
            String weekdayof_enddate = arguments.get(5).toString();
            String work_starttime = arguments.get(6).toString();
            String work_endtime = arguments.get(7).toString();
            String work_time = arguments.get(8).toString();
            String weekdays_count = arguments.get(9).toString();
            String remaining_days = arguments.get(10).toString();
            String weekend_string = arguments.get(11).toString();
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
               qry = "(CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + "  = " + end_date + " THEN CASE WHEN ((SUBSTR(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (TRUNC(( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 3600)) END ELSE (TRUNC((CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * TRUNC((TRUNC(" + end_date + ") - TRUNC(" + start_date + ")) / 7)) + CAST(SUBSTR(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS " + intSyntax + ") - CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 3600)) END) END)";
            } else {
               qry = "(CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + "  = " + end_date + " THEN CASE WHEN ((SUBSTR(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (TRUNC(( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 60)) END ELSE (TRUNC((CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * TRUNC((TRUNC(" + end_date + ") - TRUNC(" + start_date + ")) / 7)) + CAST(SUBSTR(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS " + intSyntax + ") - CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 60)) END) END)";
            }
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String end_date;
      String secof_startdate;
      String secof_enddate;
      String start_date;
      if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
         start_date = "CAST(" + arguments.get(0).toString() + " AS DATE)";
         end_date = "CAST(" + arguments.get(1).toString() + " AS DATE)";
         secof_startdate = arguments.get(2).toString();
         secof_enddate = arguments.get(3).toString();
         qry = "CASE WHEN " + start_date + " > " + end_date + " THEN 0 ELSE ((" + secof_startdate + " * (DIV(DATE_DIFF(" + end_date + ", " + start_date + ",DAY) , 7))) + CAST(SUBSTR(" + secof_enddate + ", ((7 * (CAST( FORMAT_TIMESTAMP( '%u', " + start_date + ") AS INT64) - 1)) + (CAST( FORMAT_TIMESTAMP( '%u', " + end_date + ") AS INT64))), 1) AS INT64)) END";
      } else {
         String weekdayof_startdate;
         if (!fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS") && !fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
               start_date = arguments.get(0).toString();
               end_date = arguments.get(1).toString();
               secof_startdate = arguments.get(2).toString();
               secof_enddate = arguments.get(3).toString();
               weekdayof_startdate = arguments.get(4).toString();
               qry = "(CASE WHEN " + end_date + " < 0 THEN NULL ELSE (CASE WHEN " + end_date + "  = 0 THEN " + start_date + " ELSE DATE_ADD(" + start_date + ", INTERVAL  ((DIV( (" + end_date + "),(" + secof_startdate + ") ) * 7) + CAST(SUBSTR(" + weekdayof_startdate + ", (( (" + secof_enddate + ") * (CAST( FORMAT_TIMESTAMP( '%u', " + start_date + ") AS INT64) - 1) ) + ( MOD( (" + end_date + ") , (" + secof_startdate + ") ) * 2 ) + 1), 2) AS INT64))  DAY) END) END)";
            }
         } else {
            start_date = arguments.get(0).toString();
            end_date = arguments.get(1).toString();
            secof_startdate = arguments.get(2).toString();
            secof_enddate = arguments.get(3).toString();
            weekdayof_startdate = arguments.get(4).toString();
            String weekdayof_enddate = arguments.get(5).toString();
            String work_starttime = arguments.get(6).toString();
            String work_endtime = arguments.get(7).toString();
            String work_time = arguments.get(8).toString();
            String weekdays_count = arguments.get(9).toString();
            String remaining_days = arguments.get(10).toString();
            String weekend_string = arguments.get(11).toString();
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
               qry = "(CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + " = " + end_date + " THEN CASE WHEN ((SUBSTR(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (DIV(( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) , 3600)) END ELSE (DIV((CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * (DIV(DATE_DIFF(" + end_date + ", " + start_date + ",DAY) , 7))) + CAST(SUBSTR(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) , 3600)) END) END)";
            } else {
               qry = "(CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + " = " + end_date + " THEN CASE WHEN ((SUBSTR(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (DIV(( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) , 60)) END ELSE (DIV((CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * (DIV(DATE_DIFF(" + end_date + ", " + start_date + ",DAY) , 7))) + CAST(SUBSTR(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) , 60)) END) END)";
            }
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String end_date;
      String secof_startdate;
      String secof_enddate;
      String start_date;
      if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
         start_date = "CAST(" + arguments.get(0).toString() + " AS DATE)";
         end_date = "CAST(" + arguments.get(1).toString() + " AS DATE)";
         secof_startdate = arguments.get(2).toString();
         secof_enddate = arguments.get(3).toString();
         qry = "CASE WHEN " + start_date + " > " + end_date + " THEN 0 ELSE ((" + secof_startdate + " * (TRUNC(DATEDIFF(DAY," + start_date + ", " + end_date + ") / 7))) + CAST(SUBSTRING(" + secof_enddate + ", ((7 * (((DAYOFWEEKISO(" + start_date + ")+6)%7))) + (((DAYOFWEEKISO(" + end_date + ")+6)%7))+1), 1) AS INT)) END";
      } else {
         String weekdayof_startdate;
         if (!fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS") && !fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
               start_date = arguments.get(0).toString();
               end_date = arguments.get(1).toString();
               secof_startdate = arguments.get(2).toString();
               secof_enddate = arguments.get(3).toString();
               weekdayof_startdate = arguments.get(4).toString();
               qry = "(CASE WHEN " + end_date + " < 0 THEN NULL ELSE (CASE WHEN " + end_date + "  = 0 THEN " + start_date + " ELSE DATEADD(DAY,((TRUNC( (" + end_date + ") / (" + secof_startdate + ") ) * 7) + CAST(SUBSTRING(" + weekdayof_startdate + ", (( (" + secof_enddate + ") * ((DAYOFWEEKISO(" + start_date + ")+6)%7) ) + ( ( (" + end_date + ") % (" + secof_startdate + ") ) * 2 ) + 1), 2) AS INT))," + start_date + ") END) END)";
            }
         } else {
            start_date = arguments.get(0).toString();
            end_date = arguments.get(1).toString();
            secof_startdate = arguments.get(2).toString();
            secof_enddate = arguments.get(3).toString();
            weekdayof_startdate = arguments.get(4).toString();
            String weekdayof_enddate = arguments.get(5).toString();
            String work_starttime = arguments.get(6).toString();
            String work_endtime = arguments.get(7).toString();
            String work_time = arguments.get(8).toString();
            String weekdays_count = arguments.get(9).toString();
            String remaining_days = arguments.get(10).toString();
            String weekend_string = arguments.get(11).toString();
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
               qry = "(CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + "  = " + end_date + " THEN CASE WHEN ((SUBSTRING(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (TRUNC(( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 3600)) END ELSE (TRUNC((CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * TRUNC(DATEDIFF(DAY," + start_date + ", " + end_date + ") / 7)) + CAST(SUBSTRING(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 3600)) END) END)";
            } else {
               qry = "(CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + "  = " + end_date + " THEN CASE WHEN ((SUBSTRING(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (TRUNC(( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 60)) END ELSE (TRUNC((CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * TRUNC(DATEDIFF(DAY," + start_date + ", " + end_date + ") / 7)) + CAST(SUBSTRING(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 60)) END) END)";
            }
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String end_date;
      String secof_startdate;
      String secof_enddate;
      String start_date;
      if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
         start_date = "CAST(" + arguments.get(0).toString() + " AS DATE)";
         end_date = "CAST(" + arguments.get(1).toString() + " AS DATE)";
         secof_startdate = arguments.get(2).toString();
         secof_enddate = arguments.get(3).toString();
         qry = "CASE WHEN " + start_date + " > " + end_date + " THEN 0 ELSE ((" + secof_startdate + " * ((DAYS(" + end_date + ") - DAYS(" + start_date + "))/ 7)) + CAST(SUBSTR(" + secof_enddate + ", ((7 * (((DAYOFWEEK_ISO(" + start_date + ")+6)%7))) + (((DAYOFWEEK_ISO(" + end_date + ")+6)%7))+1), 1) AS INT)) END";
      } else {
         String weekdayof_startdate;
         if (!fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS") && !fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
               start_date = arguments.get(0).toString();
               end_date = arguments.get(1).toString();
               secof_startdate = arguments.get(2).toString();
               secof_enddate = arguments.get(3).toString();
               weekdayof_startdate = arguments.get(4).toString();
               qry = "(CASE WHEN " + end_date + " < 0 THEN NULL ELSE (CASE WHEN " + end_date + "  = 0 THEN " + start_date + " ELSE (" + start_date + "+ ((( (" + end_date + ")  /  (" + secof_startdate + ") ) * 7) + CAST(SUBSTR(" + weekdayof_startdate + ", (( (" + secof_enddate + ") * ((DAYOFWEEK_ISO(" + start_date + ")+6)%7) ) + ( ( (" + end_date + ") % (" + secof_startdate + ") ) * 2 ) + 1), 2) AS INT)) DAY) END) END)";
            }
         } else {
            start_date = arguments.get(0).toString();
            end_date = arguments.get(1).toString();
            secof_startdate = arguments.get(2).toString();
            secof_enddate = arguments.get(3).toString();
            weekdayof_startdate = arguments.get(4).toString();
            String weekdayof_enddate = arguments.get(5).toString();
            String work_starttime = arguments.get(6).toString();
            String work_endtime = arguments.get(7).toString();
            String work_time = arguments.get(8).toString();
            String weekdays_count = arguments.get(9).toString();
            String remaining_days = arguments.get(10).toString();
            String weekend_string = arguments.get(11).toString();
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
               qry = "(CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + "  = " + end_date + " THEN CASE WHEN ((SUBSTR(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 3600) END ELSE ((CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * ((DAYS(" + end_date + ") - DAYS(" + start_date + ")) / 7)) + CAST(SUBSTR(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 3600) END) END)";
            } else {
               qry = "(CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + "  = " + end_date + " THEN CASE WHEN ((SUBSTR(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 60) END ELSE ((CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * ((DAYS(" + end_date + ") - DAYS(" + start_date + ")) / 7)) + CAST(SUBSTR(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 60) END) END)";
            }
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String end_date;
      String secof_startdate;
      String secof_enddate;
      String start_date;
      if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
         start_date = StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString());
         end_date = StringFunctions.handleLiteralStringDateForAthena(arguments.get(1).toString());
         secof_startdate = arguments.get(2).toString();
         secof_enddate = arguments.get(3).toString();
         qry = "CASE WHEN " + start_date + " > " + end_date + " THEN 0 ELSE ((" + secof_startdate + " * (DATE_DIFF('DAY', " + start_date + ", " + end_date + ") / 7)) + CAST(SUBSTR(" + secof_enddate + ", ((7 * ((DAY_OF_WEEK(" + start_date + ")+6)%7)) + (((DAY_OF_WEEK(" + end_date + ")+6)%7) + 1)), 1) AS INT)) END";
      } else {
         String weekdayof_startdate;
         if (!fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS") && !fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
               start_date = arguments.get(0).toString();
               end_date = arguments.get(1).toString();
               secof_startdate = arguments.get(2).toString();
               secof_enddate = arguments.get(3).toString();
               weekdayof_startdate = arguments.get(4).toString();
               qry = "(CASE WHEN " + end_date + " < 0 THEN NULL ELSE (CASE WHEN " + end_date + "  = 0 THEN " + start_date + " ELSE DATE_ADD('DAY', ((( (" + end_date + ") / (" + secof_startdate + ") ) * 7) + CAST(SUBSTR(" + weekdayof_startdate + ", (( (" + secof_enddate + ") * ((DAY_OF_WEEK(" + start_date + ")+6)%7) ) + ( ( (" + end_date + ") % (" + secof_startdate + ") ) * 2 ) + 1), 2) AS INT))," + start_date + ") END) END)";
            }
         } else {
            start_date = arguments.get(0).toString();
            end_date = arguments.get(1).toString();
            secof_startdate = arguments.get(2).toString();
            secof_enddate = arguments.get(3).toString();
            weekdayof_startdate = arguments.get(4).toString();
            String weekdayof_enddate = arguments.get(5).toString();
            String work_starttime = arguments.get(6).toString();
            String work_endtime = arguments.get(7).toString();
            String work_time = arguments.get(8).toString();
            String weekdays_count = arguments.get(9).toString();
            String remaining_days = arguments.get(10).toString();
            String weekend_string = arguments.get(11).toString();
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
               qry = "(CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + "  = " + end_date + " THEN CASE WHEN ((SUBSTR(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 3600) END ELSE ((CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * (DATE_DIFF('DAY', " + start_date + ", " + end_date + ") / 7)) + CAST(SUBSTR(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 3600) END) END)";
            } else {
               qry = "(CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + "  = " + end_date + " THEN CASE WHEN ((SUBSTR(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 60) END ELSE ((CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * (DATE_DIFF('DAY', " + start_date + ", " + end_date + ") / 7)) + CAST(SUBSTR(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 60) END) END)";
            }
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String end_date;
      String secof_startdate;
      String secof_enddate;
      String start_date;
      if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
         start_date = StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString());
         end_date = StringFunctions.handleLiteralStringDateForSapHana(arguments.get(1).toString());
         secof_startdate = arguments.get(2).toString();
         secof_enddate = arguments.get(3).toString();
         qry = "CASE WHEN " + start_date + " > " + end_date + " THEN 0 ELSE ((" + secof_startdate + " * CAST((DAYS_BETWEEN(" + start_date + ", " + end_date + ") / 7) AS INTEGER) + CAST(SUBSTRING(" + secof_enddate + ", ((7 * MOD((WEEKDAY(" + start_date + ")+7),7)) + (MOD((WEEKDAY(" + end_date + ")+7),7) + 1)), 1) AS INT))) END";
      } else {
         String weekdayof_startdate;
         if (!fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS") && !fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
               start_date = arguments.get(0).toString();
               end_date = arguments.get(1).toString();
               secof_startdate = arguments.get(2).toString();
               secof_enddate = arguments.get(3).toString();
               weekdayof_startdate = arguments.get(4).toString();
               qry = "(CASE WHEN " + end_date + " < 0 THEN NULL ELSE (CASE WHEN " + end_date + "  = 0 THEN " + start_date + " ELSE ADD_DAYS(" + start_date + ",((( (" + end_date + ") / (" + secof_startdate + ") ) * 7) + CAST(SUBSTRING(" + weekdayof_startdate + ", (( (" + secof_enddate + ") * MOD((WEEKDAY(" + start_date + ")+7),7) ) + ( MOD( (" + end_date + ") , (" + secof_startdate + ") ) * 2 ) + 1), 2) AS INT))) END) END)";
            }
         } else {
            start_date = arguments.get(0).toString();
            end_date = arguments.get(1).toString();
            secof_startdate = arguments.get(2).toString();
            secof_enddate = arguments.get(3).toString();
            weekdayof_startdate = arguments.get(4).toString();
            String weekdayof_enddate = arguments.get(5).toString();
            String work_starttime = arguments.get(6).toString();
            String work_endtime = arguments.get(7).toString();
            String work_time = arguments.get(8).toString();
            String weekdays_count = arguments.get(9).toString();
            String remaining_days = arguments.get(10).toString();
            String weekend_string = arguments.get(11).toString();
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
               qry = "CAST((CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + "  = " + end_date + " THEN CASE WHEN ((SUBSTRING(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 3600) END ELSE ((CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * CAST((DAYS_BETWEEN(" + start_date + ", " + end_date + ") / 7) AS INTEGER)) + CAST(SUBSTRING(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 3600) END) END) AS INTEGER)";
            } else {
               qry = "CAST((CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + "  = " + end_date + " THEN CASE WHEN ((SUBSTRING(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 60) END ELSE ((CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * CAST((DAYS_BETWEEN(" + start_date + ", " + end_date + ") / 7) AS INTEGER)) + CAST(SUBSTRING(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 60) END) END) AS INTEGER)";
            }
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String end_date;
      String secof_startdate;
      String secof_enddate;
      String start_date;
      if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
         start_date = StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString());
         end_date = StringFunctions.handleLiteralStringDateForSqlite(arguments.get(1).toString());
         secof_startdate = arguments.get(2).toString();
         secof_enddate = arguments.get(3).toString();
         qry = "CASE WHEN " + start_date + " > " + end_date + " THEN 0 ELSE ((" + secof_startdate + " * CAST((CAST(-(JULIANDAY(" + start_date + ") - JULIANDAY(" + end_date + ")) AS INTEGER)/7) AS INTEGER)+CAST(SUBSTR(" + secof_enddate + ",((7*MOD((CAST((strftime('%w'," + start_date + ")+6)%7 AS INTEGER)+7),7)) + (MOD((CAST((strftime('%w'," + end_date + ")+6)%7 AS INTEGER)+7),7)+1)),1) AS INTEGER))) END";
      } else {
         String weekdayof_startdate;
         if (!fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS") && !fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
               start_date = arguments.get(0).toString();
               end_date = arguments.get(1).toString();
               secof_startdate = arguments.get(2).toString();
               secof_enddate = arguments.get(3).toString();
               weekdayof_startdate = arguments.get(4).toString();
               qry = "(CASE WHEN " + end_date + " < 0 THEN NULL ELSE (CASE WHEN " + end_date + "  = 0 THEN " + start_date + " ELSE DATETIME(" + start_date + ",((( (" + end_date + ") / (" + secof_startdate + ") ) * 7) + CAST(SUBSTR(" + weekdayof_startdate + ", (( (" + secof_enddate + ") * MOD((CAST((strftime('%w'," + start_date + ")+6)%7 AS INTEGER)+7),7) ) + ( MOD( (" + end_date + ") , (" + secof_startdate + ") ) * 2 ) + 1), 2) AS INTEGER)) || ' days') END) END)";
            }
         } else {
            start_date = arguments.get(0).toString();
            end_date = arguments.get(1).toString();
            secof_startdate = arguments.get(2).toString();
            secof_enddate = arguments.get(3).toString();
            weekdayof_startdate = arguments.get(4).toString();
            String weekdayof_enddate = arguments.get(5).toString();
            String work_starttime = arguments.get(6).toString();
            String work_endtime = arguments.get(7).toString();
            String work_time = arguments.get(8).toString();
            String weekdays_count = arguments.get(9).toString();
            String remaining_days = arguments.get(10).toString();
            String weekend_string = arguments.get(11).toString();
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
               qry = "CAST((CASE WHEN(" + start_date + ">" + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + " = " + end_date + " THEN CASE WHEN ((SUBSTR(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + ")) THEN 0 ELSE ((CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 3600) END ELSE ((CASE WHEN SUBSTR(" + weekend_string + ",(" + weekdayof_startdate + "+1),1) = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + " < " + work_starttime + " THEN " + work_time + " ELSE CASE WHEN" + secof_startdate + " > " + work_endtime + " THEN 0 ELSE (" + work_endtime + " - " + secof_startdate + ") END END) END + (((" + weekdays_count + ") * CAST((CAST(-(JULIANDAY(" + start_date + ")-JULIANDAY(" + end_date + "))/7 AS INTEGER)) AS INTEGER))+ CAST(SUBSTRING(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 3600) END) END) AS INTEGER)";
            } else {
               qry = "CAST((CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + "  = " + end_date + " THEN CASE WHEN ((SUBSTR(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE (( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 60) END ELSE ((CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * CAST((CAST(-(JULIANDAY(" + start_date + ")-JULIANDAY(" + end_date + "))/7 AS INTEGER)) AS INTEGER)) + CAST(SUBSTR(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 60) END) END) AS INTEGER)";
            }
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String end_date;
      String secof_startdate;
      String secof_enddate;
      String start_date;
      if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
         start_date = StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString());
         end_date = StringFunctions.handleLiteralStringDateForInformix(arguments.get(1).toString());
         secof_startdate = arguments.get(2).toString();
         secof_enddate = arguments.get(3).toString();
         qry = "CASE WHEN " + start_date + " > " + end_date + " THEN 0 ELSE ((" + secof_startdate + " * (((DATE(" + end_date + ")-DATE(" + start_date + ")) / 7)::INT)) + SUBSTR(" + secof_enddate + ", ((7 * WEEKDAY(" + start_date + " - 1 UNITS DAY)) + WEEKDAY(" + end_date + " - 1 UNITS DAY)), 1)::INT) END";
      } else {
         String weekdayof_startdate;
         if (!fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS") && !fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
               start_date = arguments.get(0).toString();
               end_date = arguments.get(1).toString();
               secof_startdate = arguments.get(2).toString();
               secof_enddate = arguments.get(3).toString();
               weekdayof_startdate = arguments.get(4).toString();
               qry = "(CASE WHEN " + end_date + " < 0 THEN NULL ELSE (CASE WHEN " + end_date + "  = 0 THEN " + start_date + " ELSE DATE(" + start_date + " + ((( (" + end_date + ")/(" + secof_startdate + ") )::INT * 7) + SUBSTR(" + weekdayof_startdate + ", (( (" + secof_enddate + ") * WEEKDAY(" + start_date + " - 1 UNITS DAY) ) + ( MOD( (" + end_date + ") , (" + secof_startdate + ") ) * 2 ) + 1), 2)::INT) UNITS DAY) END) END)";
            }
         } else {
            start_date = arguments.get(0).toString();
            end_date = arguments.get(1).toString();
            secof_startdate = arguments.get(2).toString();
            secof_enddate = arguments.get(3).toString();
            weekdayof_startdate = arguments.get(4).toString();
            String weekdayof_enddate = arguments.get(5).toString();
            String work_starttime = arguments.get(6).toString();
            String work_endtime = arguments.get(7).toString();
            String work_time = arguments.get(8).toString();
            String weekdays_count = arguments.get(9).toString();
            String remaining_days = arguments.get(10).toString();
            String weekend_string = arguments.get(11).toString();
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
               qry = " (CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + " = " + end_date + " THEN CASE WHEN ((SUBSTR(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE ((( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 3600)::INT) END ELSE (((CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * (((DATE(" + end_date + ") - DATE(" + start_date + ")) / 7)::INT)) + CAST(SUBSTR(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) - CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 3600)::INT) END) END)";
            } else {
               qry = " (CASE WHEN (" + start_date + " > " + end_date + ") THEN 0 ELSE (CASE WHEN " + start_date + " = " + end_date + " THEN CASE WHEN ((SUBSTR(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  THEN 0 ELSE  ((( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END - CASE WHEN " + work_starttime + " > " + secof_startdate + "  THEN " + work_starttime + " ELSE " + secof_startdate + " END) / 60)::INT) END ELSE (((CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0  ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count + ") * (((DATE(" + end_date + ") - DATE(" + start_date + ")) / 7)::INT)) +  CAST(SUBSTR(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS INT) -  CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " +  CASE WHEN SUBSTR(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0  ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) / 60)::INT) END) END)";
            }
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String end_date;
      String secof_startdate;
      String secof_enddate;
      String start_date;
      if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
         start_date = arguments.get(0).toString();
         end_date = arguments.get(1).toString();
         secof_startdate = arguments.get(2).toString();
         secof_enddate = arguments.get(3).toString();
         qry = "Iif( " + start_date + " > " + end_date + " , 0 , ((" + secof_startdate + " * Int(DATEDIFF('d', " + start_date + ", " + end_date + ") / 7)) + CInt(Mid(" + secof_enddate + ", ((7 * ((datepart('w'," + start_date + ")+5) MOD 7)) + (((datepart('w'," + end_date + ")+5) MOD 7) + 1)), 1))) )";
      } else {
         String weekdayof_startdate;
         if (!fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS") && !fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
               start_date = arguments.get(0).toString();
               end_date = arguments.get(1).toString();
               secof_startdate = arguments.get(2).toString();
               secof_enddate = arguments.get(3).toString();
               weekdayof_startdate = arguments.get(4).toString();
               qry = "(Iif( " + end_date + " < 0 , NULL , (Iif( " + end_date + "  = 0 , " + start_date + " , DATEADD('d', ((((" + end_date + ") / (" + secof_startdate + ")) * 7) + CInt(Mid(" + weekdayof_startdate + ", (((" + secof_enddate + ") * ((datepart('w'," + start_date + ")+5) MOD 7)) + (((" + end_date + ") MOD (" + secof_startdate + ")) * 2) + 1), 2))), CDate(" + start_date + ")))) ))";
            }
         } else {
            start_date = arguments.get(0).toString();
            end_date = arguments.get(1).toString();
            secof_startdate = arguments.get(2).toString();
            secof_enddate = arguments.get(3).toString();
            weekdayof_startdate = arguments.get(4).toString();
            String weekdayof_enddate = arguments.get(5).toString();
            String work_starttime = arguments.get(6).toString();
            String work_endtime = arguments.get(7).toString();
            String work_time = arguments.get(8).toString();
            String weekdays_count = arguments.get(9).toString();
            String remaining_days = arguments.get(10).toString();
            String weekend_string = arguments.get(11).toString();
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
               qry = "(Iif( (" + start_date + " > " + end_date + ") , 0 , (Iif( " + start_date + "  = " + end_date + " , Iif( ((Mid(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  , 0 , (( Iif( " + work_endtime + " < " + secof_enddate + " , " + work_endtime + " , " + secof_enddate + " ) - Iif( " + work_starttime + " > " + secof_startdate + " , " + work_starttime + " , " + secof_startdate + " )) / 3600) ) , ((Iif( Mid(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' , 0 , (Iif(" + secof_startdate + "  < " + work_starttime + "  , " + work_time + " , Iif( " + secof_startdate + "  > " + work_endtime + "  , 0 , (" + work_endtime + " -" + secof_startdate + ") ) )) ) + (((" + weekdays_count + ") * Int(DATEDIFF('d', " + start_date + ", " + end_date + ") / 7)) + CInt(Mid(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1)) - Iif( Mid(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  , 0 , 1 )) * " + work_time + " + Iif( Mid(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  , 0 , (Iif( " + secof_enddate + "  < " + work_starttime + "  , 0 , Iif( " + secof_enddate + "  > " + work_endtime + "  , " + work_time + " , (" + secof_enddate + " -" + work_starttime + ") ) )) )) / 3600) )) ))";
            } else {
               qry = "(Iif( (" + start_date + " > " + end_date + ") , 0 , (Iif( " + start_date + "  = " + end_date + " , Iif( ((Mid(" + weekend_string + ",(" + weekdayof_startdate + "+1),1)='1') OR (" + secof_enddate + " <= " + secof_startdate + ") OR (" + secof_enddate + " <= " + work_starttime + ") OR (" + secof_startdate + " >= " + work_endtime + "))  , 0 , (( Iif( " + work_endtime + " < " + secof_enddate + " , " + work_endtime + " , " + secof_enddate + " ) - Iif( " + work_starttime + " > " + secof_startdate + " , " + work_starttime + " , " + secof_startdate + " )) / 60) ) , ((Iif( Mid(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1' , 0 , (Iif( " + secof_startdate + "  < " + work_starttime + "  , " + work_time + " , Iif( " + secof_startdate + "  > " + work_endtime + "  , 0 , (" + work_endtime + " -" + secof_startdate + ") ) )) ) + (((" + weekdays_count + ") * Int(DATEDIFF('d', " + start_date + ", " + end_date + ") / 7)) + CInt(Mid(" + remaining_days + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1)) - Iif( Mid(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  , 0 , 1 )) * " + work_time + " + Iif( Mid(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  , 0 , (Iif( " + secof_enddate + "  < " + work_starttime + "  , 0 , Iif( " + secof_enddate + "  > " + work_endtime + "  , " + work_time + " , (" + secof_enddate + " -" + work_starttime + ") ) )) )) / 60) )) ))";
            }
         }
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName().toUpperCase();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String start_date = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
      String end_date = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(1).toString());
      if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
         qry = "CASE WHEN " + end_date + " < 0 THEN NULL WHEN " + end_date + " = 0 THEN " + start_date + " ELSE DATEADD('d', CAST(((TRUNC( (" + end_date + ") / (" + arguments.get(2).toString() + ") ) * 7) + CAST(SUBSTRING(" + arguments.get(4).toString() + ", (( (" + arguments.get(3).toString() + ") * MOD(DAYOFWEEK(" + start_date + ") +5 , 7) ) + ( MOD( (" + end_date + ") , (" + arguments.get(2).toString() + ") ) * 2 ) + 1), 2) AS INT)) AS INT), " + start_date + ") END";
      } else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
         qry = "CASE WHEN (" + start_date + ">" + end_date + ") THEN 0 WHEN (" + start_date + "=" + end_date + ") THEN (CASE WHEN ((SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1') OR (" + arguments.get(3).toString() + " <= " + arguments.get(2).toString() + ") OR (" + arguments.get(3).toString() + " <= " + arguments.get(6).toString() + ") OR (" + arguments.get(2).toString() + " >= " + arguments.get(7).toString() + ")) THEN 0 ELSE TRUNC((LEAST(" + arguments.get(7).toString() + ", " + arguments.get(3).toString() + ")-GREATEST(" + arguments.get(6).toString() + ", " + arguments.get(2).toString() + ")) / 3600) END) ELSE TRUNC(( (CASE WHEN SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1' THEN 0 ELSE ((CASE WHEN " + arguments.get(2).toString() + " < " + arguments.get(6).toString() + " THEN " + arguments.get(8).toString() + " ELSE (CASE WHEN " + arguments.get(2).toString() + " > " + arguments.get(7).toString() + " THEN 0 ELSE (" + arguments.get(7).toString() + "-" + arguments.get(2).toString() + ") END) END)) END) + (((" + arguments.get(9).toString() + ") * TRUNC(DATEDIFF('d'," + start_date + ", " + end_date + ") / 7)) + CAST(SUBSTRING(" + arguments.get(10).toString() + ", ((7 * " + arguments.get(4).toString() + ") + (" + arguments.get(5).toString() + " + 1)), 1) AS INT) - (CASE WHEN SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1' THEN 0 ELSE 1 END))* " + arguments.get(8).toString() + " + (CASE WHEN SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(5).toString() + "+1),1)='1' THEN 0 ELSE ((CASE WHEN " + arguments.get(3).toString() + " < " + arguments.get(6).toString() + " THEN 0 ELSE (CASE WHEN " + arguments.get(3).toString() + " > " + arguments.get(7).toString() + " THEN " + arguments.get(8).toString() + " ELSE (" + arguments.get(3).toString() + "-" + arguments.get(6).toString() + ") END) END )) END) ) / 3600 ) END";
      } else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
         qry = "CASE WHEN (" + start_date + ">" + end_date + ") THEN 0 WHEN (" + start_date + "=" + end_date + ") THEN (CASE WHEN ((SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1') OR (" + arguments.get(3).toString() + " <= " + arguments.get(2).toString() + ") OR (" + arguments.get(3).toString() + " <= " + arguments.get(6).toString() + ") OR (" + arguments.get(2).toString() + " >= " + arguments.get(7).toString() + ")) THEN 0 ELSE TRUNC((LEAST(" + arguments.get(7).toString() + ", " + arguments.get(3).toString() + ")-GREATEST(" + arguments.get(6).toString() + ", " + arguments.get(2).toString() + ")) / 60) END) ELSE TRUNC(( (CASE WHEN SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1' THEN 0 ELSE ( (CASE WHEN " + arguments.get(2).toString() + " < " + arguments.get(6).toString() + " THEN " + arguments.get(8).toString() + " ELSE (CASE WHEN " + arguments.get(2).toString() + " > " + arguments.get(7).toString() + " THEN 0 ELSE (" + arguments.get(7).toString() + "-" + arguments.get(2).toString() + ") END ) END ) ) END ) + (((" + arguments.get(9).toString() + ") * TRUNC(DATEDIFF('d'," + start_date + ", " + end_date + ") / 7)) + CAST(SUBSTRING(" + arguments.get(10).toString() + ", ((7 * " + arguments.get(4).toString() + ") + (" + arguments.get(5).toString() + " + 1)), 1) AS INT) - ( CASE WHEN SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(4).toString() + "+1),1)='1' THEN 0 ELSE 1 END ) ) * " + arguments.get(8).toString() + " + ( CASE WHEN SUBSTRING(" + arguments.get(11).toString() + ",(" + arguments.get(5).toString() + "+1),1)='1' THEN 0 ELSE ( (CASE WHEN " + arguments.get(3).toString() + " < " + arguments.get(6).toString() + " THEN 0 ELSE ( CASE WHEN " + arguments.get(3).toString() + " > " + arguments.get(7).toString() + " THEN " + arguments.get(8).toString() + " ELSE (" + arguments.get(3).toString() + "-" + arguments.get(6).toString() + ") END ) END ) ) END) ) / 60 ) END";
      } else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
         qry = "CASE WHEN (" + start_date + ">" + end_date + ") THEN 0 ELSE ((" + arguments.get(2).toString() + " * TRUNC(DATEDIFF('d'," + start_date + ", " + end_date + ") / 7)) + CAST(SUBSTRING(" + arguments.get(3).toString() + ", ((7 * MOD(DAYOFWEEK(" + start_date + ") +5 , 7)) + (MOD(DAYOFWEEK(" + end_date + ") +5 , 7) + 1)), 1) AS INT)) END";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public String getBusinessEndDateFnRemainingDaysForWeekendPatterns(String weekendPattern) {
      HashMap<String, String> remainingDaysMap = new HashMap();
      remainingDaysMap.put("17", "'00010203040001020306000102050600010405060003040506-102030405-201020304'");
      remainingDaysMap.put("12", "'-20102030400010203040001020306000102050600010405060003040506-102030405'");
      remainingDaysMap.put("23", "'-102030405-20102030400010203040001020306000102050600010405060003040506'");
      remainingDaysMap.put("34", "'0003040506-102030405-2010203040001020304000102030600010205060001040506'");
      remainingDaysMap.put("45", "'00010405060003040506-102030405-201020304000102030400010203060001020506'");
      remainingDaysMap.put("56", "'000102050600010405060003040506-102030405-20102030400010203040001020306'");
      remainingDaysMap.put("67", "'0001020306000102050600010405060003040506-102030405-2010203040001020304'");
      remainingDaysMap.put("1", "'000102030405000102030406000102030506000102040506000103040506000203040506-10102030405'");
      remainingDaysMap.put("2", "'-10102030405000102030405000102030406000102030506000102040506000103040506000203040506'");
      remainingDaysMap.put("3", "'000203040506-10102030405000102030405000102030406000102030506000102040506000103040506'");
      remainingDaysMap.put("4", "'000103040506000203040506-10102030405000102030405000102030406000102030506000102040506'");
      remainingDaysMap.put("5", "'000102040506000103040506000203040506-10102030405000102030405000102030406000102030506'");
      remainingDaysMap.put("6", "'000102030506000102040506000103040506000203040506-10102030405000102030405000102030406'");
      remainingDaysMap.put("7", "'000102030406000102030506000102040506000103040506000203040506-10102030405000102030405'");
      remainingDaysMap.put("13", "'0002030405-1010203040001020305000102040600010305060002040506-101030405'");
      remainingDaysMap.put("14", "'00010304050002030406-101020305000102040500010304060002030506-101020405'");
      remainingDaysMap.put("15", "'000102040500010304060002030506-10102040500010304050002030406-101020305'");
      remainingDaysMap.put("16", "'0001020305000102040600010305060002040506-1010304050002030405-101020304'");
      remainingDaysMap.put("24", "'-1010304050002030405-1010203040001020305000102040600010305060002040506'");
      remainingDaysMap.put("25", "'-10102040500010304050002030406-101020305000102040500010304060002030506'");
      remainingDaysMap.put("26", "'-101020305000102040500010304060002030506-10102040500010304050002030406'");
      remainingDaysMap.put("27", "'-1010203040001020305000102040600010305060002040506-1010304050002030405'");
      remainingDaysMap.put("35", "'0002040506-1010304050002030405-101020304000102030500010204060001030506'");
      remainingDaysMap.put("36", "'0002030506-10102040500010304050002030406-10102030500010204050001030406'");
      remainingDaysMap.put("37", "'0002030406-101020305000102040500010304060002030506-1010204050001030405'");
      remainingDaysMap.put("46", "'00010305060002040506-1010304050002030405-10102030400010203050001020406'");
      remainingDaysMap.put("47", "'00010304060002030506-10102040500010304050002030406-1010203050001020405'");
      remainingDaysMap.put("57", "'000102040600010305060002040506-1010304050002030405-1010203040001020305'");
      return (String)remainingDaysMap.get(weekendPattern);
   }

   public String getBusiness_DaysFnRemainingDaysMatrixMap(String weekendPattern) {
      HashMap<String, String> remainingDaysMap = new HashMap();
      remainingDaysMap.put("17", "'0123455401234434012332340122123401101234000123450'");
      remainingDaysMap.put("12", "'0012345501234544012343340123223401211234010012340'");
      remainingDaysMap.put("23", "'0001234500123455012344440123333401222234011112340'");
      remainingDaysMap.put("34", "'0111234400012345001234550123344401223334011222340'");
      remainingDaysMap.put("45", "'0122234401112334000123450012345501223444011233340'");
      remainingDaysMap.put("56", "'0123334401222334011122340001234500123455011234440'");
      remainingDaysMap.put("67", "'0123444401233334012222340111123400012345001234550'");
      remainingDaysMap.put("1", "'0123456501234545012343450123234501212345010123450'");
      remainingDaysMap.put("2", "'0012345601234555012344450123334501222345011123450'");
      remainingDaysMap.put("3", "'0112345500123456012344550123344501223345011223450'");
      remainingDaysMap.put("4", "'0122345501123445001234560123345501223445011233450'");
      remainingDaysMap.put("5", "'0123345501223445011233450012345601223455011234450'");
      remainingDaysMap.put("6", "'0123445501233445012233450112234500123456011234550'");
      remainingDaysMap.put("7", "'0123455501234445012333450122234501112345001234560'");
      remainingDaysMap.put("13", "'0112345400123445012343440123233401212234010112340'");
      remainingDaysMap.put("14", "'0122345401123434001233450123234401212334010122340'");
      remainingDaysMap.put("15", "'0123345401223434011232340012234501212344010123340'");
      remainingDaysMap.put("16", "'0123445401233434012232340112123400112345010123440'");
      remainingDaysMap.put("24", "'0011234501123444001234450123334401222334011122340'");
      remainingDaysMap.put("25", "'0012234501223444011233340012334501222344011123340'");
      remainingDaysMap.put("26", "'0012334501233444012233340112223400122345011123440'");
      remainingDaysMap.put("27", "'0012344501234444012333340122223401111234001123450'");
      remainingDaysMap.put("35", "'0112234400112345011233440012344501223344011223340'");
      remainingDaysMap.put("36", "'0112334400122345012233440112233400123345011223440'");
      remainingDaysMap.put("37", "'0112344400123345012333440122233401112234001223450'");
      remainingDaysMap.put("46", "'0122334401122334001123450112234400123445011233440'");
      remainingDaysMap.put("47", "'0122344401123334001223450122234401112334001233450'");
      remainingDaysMap.put("57", "'0123344401223334011222340011234501112344001234450'");
      return (String)remainingDaysMap.get(weekendPattern);
   }

   public String getRemainingDaysForBusiness_DaysFnAndBusiness_HoursFn(String weekendPattern) {
      String remainingDays = null;
      int weekdays = false;
      int weekdays = 7 - weekendPattern.length();
      StringBuilder sb = new StringBuilder();
      int[][] arr = new int[7][7];
      int[] weekend = new int[7];
      sb.append("'");

      int i;
      for(i = 0; i < weekendPattern.length(); ++i) {
         if (weekendPattern.charAt(i) == '1') {
            weekend[6] = 1;
         } else {
            weekend[weekendPattern.charAt(i) - 48 - 2] = 1;
         }
      }

      for(i = 0; i < 7; ++i) {
         int j;
         for(j = i - 1; j >= 0; --j) {
            if (j == i - 1) {
               if (weekend[j] == 1) {
                  arr[i][j] = weekdays;
               } else {
                  arr[i][j] = weekdays - 1;
               }
            } else if (weekend[j] == 1) {
               arr[i][j] = arr[i][j + 1];
            } else {
               arr[i][j] = arr[i][j + 1] - 1;
            }
         }

         for(j = i; j < 7; ++j) {
            if (i == j) {
               arr[i][j] = 0;
            } else if (weekend[j - 1] == 1) {
               arr[i][j] = arr[i][j - 1];
            } else {
               arr[i][j] = arr[i][j - 1] + 1;
            }
         }

         for(j = 0; j < 7; ++j) {
            sb.append(arr[i][j]);
         }
      }

      sb.append("'");
      remainingDays = sb.toString();
      return remainingDays;
   }

   public SelectColumn weekday(Vector vector, int sded) {
      SelectColumn sc_weekday = new SelectColumn();
      FunctionCalls fn_weekday = new FunctionCalls();
      TableColumn tb_weekday = new TableColumn();
      tb_weekday.setColumnName("WEEKDAY");
      fn_weekday.setFunctionName(tb_weekday);
      Vector vc_weekdayIn = new Vector();
      Vector vc_weekdayOut = new Vector();
      vc_weekdayIn.addElement(vector.get(sded));
      fn_weekday.setFunctionArguments(vc_weekdayIn);
      vc_weekdayOut.addElement(fn_weekday);
      sc_weekday.setColumnExpression(vc_weekdayOut);
      return sc_weekday;
   }

   public SelectColumn time_to_sec(Vector vector, int sded) {
      SelectColumn sc_timetosec = new SelectColumn();
      FunctionCalls fn_timetosec = new FunctionCalls();
      TableColumn tb_timetosec = new TableColumn();
      tb_timetosec.setColumnName("TIME_TO_SEC");
      fn_timetosec.setFunctionName(tb_timetosec);
      Vector vc_timetosecIn = new Vector();
      Vector vc_timetosecOut = new Vector();
      if (vector.get(sded) instanceof SelectColumn) {
         SelectColumn dateArg = (SelectColumn)vector.get(sded);
         Object val = dateArg.getColumnExpression().get(0);
         if (val instanceof String) {
            SelectColumn sc_timestamp = new SelectColumn();
            FunctionCalls fn_timestamp = new FunctionCalls();
            TableColumn tb_timestamp = new TableColumn();
            tb_timestamp.setColumnName("TIMESTAMP");
            fn_timestamp.setFunctionName(tb_timestamp);
            Vector vc_timestampIn = new Vector(1);
            Vector vc_timestampOut = new Vector(1);
            vc_timestampIn.addElement(dateArg);
            fn_timestamp.setFunctionArguments(vc_timestampIn);
            vc_timestampOut.addElement(fn_timestamp);
            sc_timestamp.setColumnExpression(vc_timestampOut);
            vc_timetosecIn.addElement(sc_timestamp);
         } else {
            vc_timetosecIn.addElement(dateArg);
         }
      }

      fn_timetosec.setFunctionArguments(vc_timetosecIn);
      vc_timetosecOut.addElement(fn_timetosec);
      sc_timetosec.setColumnExpression(vc_timetosecOut);
      return sc_timetosec;
   }

   public SelectColumn date(Vector vector, int sded) {
      SelectColumn sc_date = new SelectColumn();
      FunctionCalls fn_date = new FunctionCalls();
      TableColumn tb_date = new TableColumn();
      tb_date.setColumnName("DATE");
      fn_date.setFunctionName(tb_date);
      Vector vc_dateIn = new Vector();
      Vector vc_dateOut = new Vector();
      vc_dateIn.addElement(vector.get(sded));
      fn_date.setFunctionArguments(vc_dateIn);
      vc_dateOut.addElement(fn_date);
      sc_date.setColumnExpression(vc_dateOut);
      return sc_date;
   }
}
