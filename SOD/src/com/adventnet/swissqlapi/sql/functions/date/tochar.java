package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import java.util.Vector;

public class tochar extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (SwisSQLOptions.removeFormatForOracleToCharFunction && this.functionName.getColumnName().equalsIgnoreCase("to_char") && arguments.size() > 1) {
         arguments.removeElementAt(1);
      }

      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CONVERT");
      Vector arguments = new Vector();

      String format;
      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else if (this.functionArguments.get(i) instanceof String) {
            format = (String)this.functionArguments.get(i);
            if (format.trim().equalsIgnoreCase("SYSDATE")) {
               format = "GETDATE()";
            }

            if (format.trim().equalsIgnoreCase("SYS_GUID")) {
               format = "NEWID()";
            }

            arguments.addElement(format);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      int length;
      int beginIndex;
      String arg2;
      SelectColumn fSc;
      TableColumn innerFunction;
      FunctionCalls fc;
      if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn) {
         fSc = (SelectColumn)this.functionArguments.get(1);
         String date;
         int scal;
         if (fSc.getColumnExpression().get(0) instanceof String) {
            format = (String)fSc.getColumnExpression().get(0);
            if (FunctionCalls.charToIntName) {
               if ((!format.trim().startsWith("'") || !format.endsWith("'")) && !format.trim().startsWith("@")) {
                  if (format.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                     date = format.trim().substring(30);
                     if (date.trim().startsWith("(") && date.trim().endsWith(")")) {
                        date = date.trim().substring(1, date.trim().length() - 1);
                        this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                        this.functionArguments.setElementAt("VARCHAR(23)", 0);
                        this.functionArguments.addElement(date);
                     } else {
                        arguments.add(2, this.functionArguments.get(1));
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR(23)", 0);
                     }
                  } else {
                     boolean isNotProperString = false;

                     for(beginIndex = 0; beginIndex < format.length(); ++beginIndex) {
                        if (!Character.isLetterOrDigit(format.charAt(beginIndex))) {
                           isNotProperString = true;
                        }
                     }

                     if (!isNotProperString) {
                        format = "dbo." + format.trim() + "INT()";
                        arguments.add(format);
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR(23)", 0);
                     } else if (format.indexOf(40) != -1) {
                        format = format.substring(0, format.indexOf(40)).trim() + "INT" + format.substring(format.indexOf("("));
                        arguments.add(format);
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR(23)", 0);
                     } else {
                        arguments.add(2, this.functionArguments.get(1));
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR(23)", 0);
                     }
                  }
               } else {
                  this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                  this.functionArguments.setElementAt("VARCHAR(23)", 0);
                  this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + format + ")");
               }
            } else if (format.toUpperCase().indexOf("MM/DD/YYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("101");
               } else {
                  this.functionArguments.addElement("101) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("DD.MM.YYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("104");
               } else {
                  this.functionArguments.addElement("104) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("DD/MM/YYYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("103");
               } else {
                  this.functionArguments.addElement("103) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("YYYY.MM.DD") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("102");
               } else {
                  this.functionArguments.addElement("102) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("YYY-MM-DD") != -1) {
               FunctionCalls fc = new FunctionCalls();
               TableColumn innerFunction = new TableColumn();
               innerFunction.setOwnerName(this.functionName.getOwnerName());
               if (format.toUpperCase().indexOf("HH24:MI:SS") != -1 && format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                  this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               } else {
                  innerFunction.setTableName(this.functionName.getTableName());
                  innerFunction.setColumnName("CONVERT");
                  fc.setFunctionName(innerFunction);
                  Vector args = new Vector();
                  args.addElement("VARCHAR(23)");
                  args.addElement(this.functionArguments.get(0));
                  args.addElement("112");
                  fc.setFunctionArguments(args);
                  this.functionArguments.setElementAt(fc, 1);
               }

               this.functionArguments.setElementAt("DATETIME", 0);
            } else if (format.toUpperCase().indexOf("YYYY/MM/DD") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "111");
               } else {
                  this.functionArguments.addElement("111) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("YYYYMMDD") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "112");
               } else {
                  this.functionArguments.addElement("112) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("DD MON YYYY HH:MI:SS") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "113");
               } else {
                  this.functionArguments.addElement("113) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("DD-MM-YYY") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "105");
               } else {
                  this.functionArguments.addElement("105) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("DD MON YYYY") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "106");
               } else {
                  this.functionArguments.addElement("106) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("MON DD, YYYY") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "107");
               } else {
                  this.functionArguments.addElement("107) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("MON DD YYYY HH:MI:SS") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "109");
               } else {
                  this.functionArguments.addElement("109) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("MM-DD-YYY") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "110");
               } else {
                  this.functionArguments.addElement("110) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("DD-MM-YY") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "5");
               } else {
                  this.functionArguments.addElement("5) + ' ' + CONVERT(VARCHAR(12)");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("YY.MM.DD") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "2");
               } else {
                  this.functionArguments.addElement("2) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("DD/MM/YY") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "3");
               } else {
                  this.functionArguments.addElement("3) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("MM/DD/YY") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "1");
               } else {
                  this.functionArguments.addElement("1) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("DD.MM.YY") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "4");
               } else {
                  this.functionArguments.addElement("4) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("DD MON YY") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "6");
               } else {
                  this.functionArguments.addElement("6) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("MON DD, YY") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "7");
               } else {
                  this.functionArguments.addElement("7) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("MON DD YYYY") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "9");
               } else {
                  this.functionArguments.addElement("9) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("MM-DD-YY") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "10");
               } else {
                  this.functionArguments.addElement("10) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("YY/MM/DD") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "11");
               } else {
                  this.functionArguments.addElement("11) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("YYMMDD") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "12");
               } else {
                  this.functionArguments.addElement("12) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("DD MON YYYY") != -1) {
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  arguments.add(2, "13");
               } else {
                  this.functionArguments.addElement("13) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("VARCHAR(23)", 0);
            } else if (format.toUpperCase().indexOf("HH24:MI:SS") != -1 || format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               this.functionArguments.addElement("108");
            } else if (format.trim().startsWith("'") && format.endsWith("'") && format.toLowerCase().indexOf(120) != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("BINARY(4)", 0);
            } else if (format.toUpperCase().equals("'YYYY'")) {
               this.functionName.setColumnName("DATEPART");
               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("YYYY", 0);
            } else if (format.toUpperCase().equals("'MM'")) {
               this.functionName.setColumnName("DATEPART");
               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("MM", 0);
            } else if (format.toUpperCase().equals("'DD'")) {
               this.functionName.setColumnName("DATEPART");
               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("DD", 0);
            } else if (format.toUpperCase().equals("'DDMMYYYYHHMMSS'")) {
               date = this.functionArguments.get(0).toString();
               String fmt = "CASE WHEN LEN(DATENAME(d, " + date + ")) = 1 THEN  '0'+ DATENAME(d, " + date + ") " + "ELSE DATENAME(d, " + date + ") END + " + "CAST(MONTH(" + date + ") AS VARCHAR) + " + "DATENAME(yyyy, " + date + ") + " + "DATENAME(hh, " + date + ") + " + "DATENAME(mi, " + date + ") + " + "DATENAME(ss, " + date + ")";
               this.functionName.setColumnName(fmt);
               this.setFunctionArguments(new Vector());
               this.setOpenBracesForFunctionNameRequired(false);
            } else if (format.toUpperCase().equals("'HH24'")) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(2)", 0);
               this.functionArguments.addElement("108");
            } else if (this.checkIfDecimalFormat(format)) {
               if (format.trim().startsWith("'") && format.trim().endsWith("'")) {
                  format = format.substring(1, format.length() - 1);
               }

               length = format.length() - 1;
               beginIndex = format.indexOf(".");
               scal = length - beginIndex;
               arg2 = "CONVERT( numeric(" + length + ", " + scal + "), " + this.functionArguments.get(0) + " )";
               this.functionArguments.setElementAt(arg2, 1);
               this.functionArguments.setElementAt("VARCHAR ", 0);
            } else {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
            }
         } else if (fSc.getColumnExpression().get(0) instanceof TableColumn) {
            TableColumn tc = (TableColumn)fSc.getColumnExpression().get(0);
            date = tc.toString();
            if (FunctionCalls.charToIntName) {
               if (date.trim().startsWith("@")) {
                  this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                  this.functionArguments.setElementAt("VARCHAR(23)", 0);
                  this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + date + ")");
               } else {
                  boolean isNotProperString = false;

                  for(scal = 0; scal < date.length(); ++scal) {
                     if (!Character.isLetterOrDigit(date.charAt(scal))) {
                        isNotProperString = true;
                     }
                  }

                  if (!isNotProperString) {
                     if (!date.toUpperCase().endsWith("INT")) {
                        date = "dbo." + date.trim() + "INT()";
                        arguments.add(date);
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR(23)", 0);
                     }
                  } else if (date.indexOf(40) != -1) {
                     date = date.substring(0, date.indexOf(40)).trim() + "INT" + date.substring(date.indexOf("("));
                     arguments.add(date);
                     arguments.setElementAt(this.functionArguments.get(0), 1);
                     arguments.setElementAt("VARCHAR(23)", 0);
                  } else {
                     arguments.add(2, this.functionArguments.get(1));
                     arguments.setElementAt(this.functionArguments.get(0), 1);
                     arguments.setElementAt("VARCHAR(23)", 0);
                  }
               }
            } else {
               this.functionArguments.add(2, this.functionArguments.get(1));
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
            }
         } else if (fSc.getColumnExpression().get(0) instanceof FunctionCalls) {
            fc = (FunctionCalls)fSc.getColumnExpression().get(0);
            if (FunctionCalls.charToIntName) {
               innerFunction = fc.getFunctionName();
               if (innerFunction.getColumnName().equalsIgnoreCase("CONVERTSQLSERVERDATEFORMAT")) {
                  innerFunction.setColumnName("");
                  innerFunction.setTableName((String)null);
                  innerFunction.setOwnerName((String)null);
               } else if (fc.getFunctionArguments().size() == 0) {
                  innerFunction.setColumnName(innerFunction.getColumnName().trim() + "INT");
               } else {
                  this.processFunctionArgumentsForRamco(fc.getFunctionArguments());
               }
            }

            arguments.add(2, this.functionArguments.get(1));
            arguments.setElementAt(this.functionArguments.get(0), 1);
            arguments.setElementAt("VARCHAR(23)", 0);
         }
      } else {
         String format;
         if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof String) {
            format = (String)this.functionArguments.get(1);
            if (FunctionCalls.charToIntName) {
               if ((!format.trim().startsWith("'") || !format.endsWith("'")) && !format.trim().startsWith("@")) {
                  if (format.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                     format = format.trim().substring(30);
                     if (format.trim().startsWith("(") && format.trim().endsWith(")")) {
                        format = format.trim().substring(1, format.trim().length() - 1);
                        this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                        this.functionArguments.setElementAt("VARCHAR(23)", 0);
                        this.functionArguments.addElement(format);
                     } else {
                        arguments.add(2, this.functionArguments.get(1));
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR(23)", 0);
                     }
                  } else {
                     boolean isNotProperString = false;

                     for(length = 0; length < format.length(); ++length) {
                        if (!Character.isLetterOrDigit(format.charAt(length))) {
                           isNotProperString = true;
                        }
                     }

                     if (!isNotProperString) {
                        format = "dbo." + format.trim() + "INT()";
                        arguments.add(format);
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR(23)", 0);
                     } else if (format.indexOf(40) != -1) {
                        format = format.substring(0, format.indexOf(40)).trim() + "INT" + format.substring(format.indexOf("("));
                        arguments.add(format);
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR(23)", 0);
                     } else {
                        arguments.add(2, this.functionArguments.get(1));
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR(23)", 0);
                     }
                  }
               } else {
                  this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                  this.functionArguments.setElementAt("VARCHAR(23)", 0);
                  this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + format + ")");
               }
            } else if (format.toUpperCase().indexOf("MM/DD/YYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("101");
               } else {
                  this.functionArguments.addElement("101) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("YYYY.MM.DD") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("102");
               } else {
                  this.functionArguments.addElement("102) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("DD/MM/YYYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("103");
               } else {
                  this.functionArguments.addElement("103) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("DD.MM.YYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("104");
               } else {
                  this.functionArguments.addElement("104) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("DD-MM-YYYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("105");
               } else {
                  this.functionArguments.addElement("105) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("DD MON YYYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("106");
               } else {
                  this.functionArguments.addElement("106) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("MON DD, YYYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("107");
               } else {
                  this.functionArguments.addElement("107) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("MON DD YYYY HH:MI:SS") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("109");
               } else {
                  this.functionArguments.addElement("109) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("MM-DD-YYYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("110");
               } else {
                  this.functionArguments.addElement("110) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("YYYY.MM.DD") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("102");
               } else {
                  this.functionArguments.addElement("102) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("YY.MM.DD") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("2");
               } else {
                  this.functionArguments.addElement("2) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("YYYY/MM/DD") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("111");
               } else {
                  this.functionArguments.addElement("111) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("YYYY MM DD") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("112");
               } else {
                  this.functionArguments.addElement("112) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("DD MON YYYY HH:MI:SS") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("113");
               } else {
                  this.functionArguments.addElement("113) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("MM/DD/YY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("1");
               } else {
                  this.functionArguments.addElement("1) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("YY.MM.DD") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("2");
               } else {
                  this.functionArguments.addElement("2) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("DD/MM/YY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("3");
               } else {
                  this.functionArguments.addElement("3) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("DD.MM.YY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("4");
               } else {
                  this.functionArguments.addElement("4) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("DD-MM-YY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("5");
               } else {
                  this.functionArguments.addElement("5) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("DD-MON YY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("6");
               } else {
                  this.functionArguments.addElement("6) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("MON DD, YY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("7");
               } else {
                  this.functionArguments.addElement("7) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("MON DD YYYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("9");
               } else {
                  this.functionArguments.addElement("9) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("MM-DD-YY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("10");
               } else {
                  this.functionArguments.addElement("10) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("YY/MM/DD") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("11");
               } else {
                  this.functionArguments.addElement("11) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("YYMMDD") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("12");
               } else {
                  this.functionArguments.addElement("12) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("12");
               }
            } else if (format.toUpperCase().indexOf("DD MON YYYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("13");
               } else {
                  this.functionArguments.addElement("13) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("YYY-MM-DD") != -1) {
               fc = new FunctionCalls();
               innerFunction = new TableColumn();
               innerFunction.setOwnerName(this.functionName.getOwnerName());
               if (format.toUpperCase().indexOf("HH24:MI:SS") != -1 && format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                  this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               } else {
                  innerFunction.setTableName(this.functionName.getTableName());
                  innerFunction.setColumnName("CONVERT");
                  fc.setFunctionName(innerFunction);
                  Vector args = new Vector();
                  args.addElement("VARCHAR(23)");
                  args.addElement(this.functionArguments.get(0));
                  args.addElement("112");
                  fc.setFunctionArguments(args);
                  this.functionArguments.setElementAt(fc, 1);
               }

               this.functionArguments.setElementAt("DATETIME", 0);
            } else if (format.toUpperCase().indexOf("HH24:MI:SS") == -1 && (format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1)) {
               if (format.trim().startsWith("'") && format.endsWith("'") && format.toLowerCase().indexOf(120) != -1) {
                  this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                  this.functionArguments.setElementAt("BINARY(4)", 0);
               } else {
                  arguments.add(2, this.functionArguments.get(1));
                  this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                  this.functionArguments.setElementAt("VARCHAR(23)", 0);
               }
            } else {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR(23)", 0);
               this.functionArguments.addElement("108");
            }
         } else if (this.functionArguments.size() == 1) {
            arguments.add(1, this.functionArguments.get(0));
            if (this.functionArguments.get(0) instanceof SelectColumn) {
               fSc = (SelectColumn)this.functionArguments.get(0);
               Vector fScVec = fSc.getColumnExpression();
               length = fScVec.size();

               for(beginIndex = 0; beginIndex < length; ++beginIndex) {
                  Object fScVecArg = fScVec.elementAt(beginIndex);
                  if (fScVecArg instanceof TableColumn) {
                     arg2 = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)fScVecArg);
                     if (arg2 != null && arg2.indexOf("(") != -1) {
                        String dtypeSize = arg2.substring(arg2.indexOf("(") + 1, arg2.indexOf(")"));
                        if (dtypeSize.indexOf(",") != -1) {
                           dtypeSize = dtypeSize.substring(0, dtypeSize.indexOf(","));
                        }

                        arguments.setElementAt("VARCHAR(" + dtypeSize + ")", 0);
                     } else {
                        arguments.setElementAt("VARCHAR(23)", 0);
                     }
                  } else if (fScVecArg instanceof String) {
                     arg2 = fScVecArg.toString();
                     arguments.setElementAt("VARCHAR(" + arg2.length() + ")", 0);
                  } else {
                     arguments.setElementAt("VARCHAR(23)", 0);
                  }
               }
            } else if (this.functionArguments.get(0) instanceof String) {
               format = this.functionArguments.get(0).toString();
               if (format.startsWith("'")) {
                  arguments.setElementAt("VARCHAR(" + format.length() + ")", 0);
               } else {
                  arguments.setElementAt("VARCHAR(23)", 0);
               }
            } else {
               arguments.setElementAt("VARCHAR(23)", 0);
            }
         }
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CONVERT");
      Vector arguments = new Vector();

      String format;
      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSybaseSelect(to_sqs, from_sqs));
         } else if (this.functionArguments.get(i) instanceof String) {
            format = (String)this.functionArguments.get(i);
            if (format.trim().equalsIgnoreCase("SYSDATE")) {
               format = "GETDATE()";
            }

            if (format.trim().equalsIgnoreCase("SYS_GUID")) {
               format = "NEWID()";
            }

            arguments.addElement(format);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      int j;
      int prec;
      String arg;
      SelectColumn fSc;
      TableColumn innerFunction;
      FunctionCalls fc;
      if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn) {
         fSc = (SelectColumn)this.functionArguments.get(1);
         int scal;
         if (fSc.getColumnExpression().get(0) instanceof String) {
            format = (String)fSc.getColumnExpression().get(0);
            if (format.toUpperCase().indexOf("MM/DD/YYYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("101");
               } else {
                  this.functionArguments.addElement("101) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("DD.MM.YYYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("104");
               } else {
                  this.functionArguments.addElement("104) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("YYYY-MM-DD") != -1) {
               FunctionCalls fc = new FunctionCalls();
               TableColumn innerFunction = new TableColumn();
               innerFunction.setOwnerName(this.functionName.getOwnerName());
               if (format.toUpperCase().indexOf("HH24:MI:SS") != -1 && format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                  this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               } else {
                  innerFunction.setTableName(this.functionName.getTableName());
                  innerFunction.setColumnName("CONVERT");
                  fc.setFunctionName(innerFunction);
                  Vector args = new Vector();
                  args.addElement("VARCHAR");
                  args.addElement(this.functionArguments.get(0));
                  args.addElement("112");
                  fc.setFunctionArguments(args);
                  this.functionArguments.setElementAt(fc, 1);
               }

               this.functionArguments.setElementAt("DATETIME", 0);
            } else if (format.toUpperCase().indexOf("HH24:MI:SS") == -1 && (format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1)) {
               if (this.checkIfDecimalFormat(format)) {
                  j = format.indexOf(".");
                  prec = format.length();
                  scal = prec - format.substring(j).length();
                  arg = "CONVERT( numeric(" + prec + ", " + scal + "), " + this.functionArguments.get(0) + " )";
                  this.functionArguments.setElementAt(arg, 1);
                  this.functionArguments.setElementAt("VARCHAR ", 0);
               } else {
                  this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                  this.functionArguments.setElementAt("VARCHAR", 0);
               }
            } else {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR", 0);
               this.functionArguments.addElement("108");
            }
         } else if (fSc.getColumnExpression().get(0) instanceof TableColumn) {
            TableColumn tc = (TableColumn)fSc.getColumnExpression().get(0);
            String tcStr = tc.toString();
            if (FunctionCalls.charToIntName) {
               if (tcStr.trim().startsWith("@")) {
                  this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                  this.functionArguments.setElementAt("VARCHAR", 0);
                  this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + tcStr + ")");
               } else {
                  boolean isNotProperString = false;

                  for(scal = 0; scal < tcStr.length(); ++scal) {
                     if (!Character.isLetterOrDigit(tcStr.charAt(scal))) {
                        isNotProperString = true;
                     }
                  }

                  if (!isNotProperString) {
                     if (!tcStr.toUpperCase().endsWith("INT")) {
                        tcStr = "dbo." + tcStr.trim() + "INT()";
                        arguments.add(tcStr);
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR", 0);
                     }
                  } else if (tcStr.indexOf(40) != -1) {
                     tcStr = tcStr.substring(0, tcStr.indexOf(40)).trim() + "INT" + tcStr.substring(tcStr.indexOf("("));
                     arguments.add(tcStr);
                     arguments.setElementAt(this.functionArguments.get(0), 1);
                     arguments.setElementAt("VARCHAR", 0);
                  } else {
                     arguments.add(2, this.functionArguments.get(1));
                     arguments.setElementAt(this.functionArguments.get(0), 1);
                     arguments.setElementAt("VARCHAR", 0);
                  }
               }
            } else {
               this.functionArguments.add(2, this.functionArguments.get(1));
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR", 0);
            }
         } else if (fSc.getColumnExpression().get(0) instanceof FunctionCalls) {
            fc = (FunctionCalls)fSc.getColumnExpression().get(0);
            if (FunctionCalls.charToIntName) {
               innerFunction = fc.getFunctionName();
               if (innerFunction.getColumnName().equalsIgnoreCase("CONVERTSQLSERVERDATEFORMAT")) {
                  innerFunction.setColumnName("");
                  innerFunction.setTableName((String)null);
                  innerFunction.setOwnerName((String)null);
               } else if (fc.getFunctionArguments().size() == 0) {
                  innerFunction.setColumnName(innerFunction.getColumnName().trim() + "INT");
               } else {
                  this.processFunctionArgumentsForRamco(fc.getFunctionArguments());
               }
            }

            arguments.add(2, this.functionArguments.get(1));
            arguments.setElementAt(this.functionArguments.get(0), 1);
            arguments.setElementAt("VARCHAR", 0);
         }
      } else {
         String format;
         if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof String) {
            format = (String)this.functionArguments.get(1);
            if (FunctionCalls.charToIntName) {
               if ((!format.trim().startsWith("'") || !format.endsWith("'")) && !format.trim().startsWith("@")) {
                  if (format.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                     format = format.trim().substring(30);
                     if (format.trim().startsWith("(") && format.trim().endsWith(")")) {
                        format = format.trim().substring(1, format.trim().length() - 1);
                        this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                        this.functionArguments.setElementAt("VARCHAR", 0);
                        this.functionArguments.addElement(format);
                     } else {
                        arguments.add(2, this.functionArguments.get(1));
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR", 0);
                     }
                  } else {
                     boolean isNotProperString = false;

                     for(j = 0; j < format.length(); ++j) {
                        if (!Character.isLetterOrDigit(format.charAt(j))) {
                           isNotProperString = true;
                        }
                     }

                     if (!isNotProperString) {
                        format = "dbo." + format.trim() + "INT()";
                        arguments.add(format);
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR", 0);
                     } else if (format.indexOf(40) != -1) {
                        format = format.substring(0, format.indexOf(40)).trim() + "INT" + format.substring(format.indexOf("("));
                        arguments.add(format);
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR", 0);
                     } else {
                        arguments.add(2, this.functionArguments.get(1));
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR", 0);
                     }
                  }
               } else {
                  this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                  this.functionArguments.setElementAt("VARCHAR", 0);
                  this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + format + ")");
               }
            } else if (format.toUpperCase().indexOf("MM/DD/YYYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("101");
               } else {
                  this.functionArguments.addElement("101) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("DD.MM.YYYY") != -1) {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR", 0);
               if ((format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) && format.toUpperCase().indexOf("HH24:MI:SS") == -1) {
                  this.functionArguments.addElement("104");
               } else {
                  this.functionArguments.addElement("104) + ' ' + CONVERT(VARCHAR");
                  this.functionArguments.addElement(this.functionArguments.get(1));
                  this.functionArguments.addElement("108");
               }
            } else if (format.toUpperCase().indexOf("YYYY-MM-DD") != -1) {
               fc = new FunctionCalls();
               innerFunction = new TableColumn();
               innerFunction.setOwnerName(this.functionName.getOwnerName());
               if (format.toUpperCase().indexOf("HH24:MI:SS") != -1 && format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                  this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               } else {
                  innerFunction.setTableName(this.functionName.getTableName());
                  innerFunction.setColumnName("CONVERT");
                  fc.setFunctionName(innerFunction);
                  Vector args = new Vector();
                  args.addElement("VARCHAR");
                  args.addElement(this.functionArguments.get(0));
                  args.addElement("112");
                  fc.setFunctionArguments(args);
                  this.functionArguments.setElementAt(fc, 1);
               }

               this.functionArguments.setElementAt("DATETIME", 0);
            } else if (format.toUpperCase().indexOf("HH24:MI:SS") == -1 && (format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1)) {
               arguments.add(2, this.functionArguments.get(1));
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR", 0);
            } else {
               this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
               this.functionArguments.setElementAt("VARCHAR", 0);
               this.functionArguments.addElement("108");
            }
         } else if (this.functionArguments.size() == 1) {
            arguments.add(1, this.functionArguments.get(0));
            if (this.functionArguments.get(0) instanceof SelectColumn) {
               fSc = (SelectColumn)this.functionArguments.get(0);
               Vector fScVec = fSc.getColumnExpression();
               j = fScVec.size();

               for(prec = 0; prec < j; ++prec) {
                  Object fScVecArg = fScVec.elementAt(prec);
                  if (fScVecArg instanceof TableColumn) {
                     arg = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)fScVecArg);
                     if (arg != null && arg.indexOf("(") != -1) {
                        String dtypeSize = arg.substring(arg.indexOf("(") + 1, arg.indexOf(")"));
                        if (dtypeSize.indexOf(",") != -1) {
                           dtypeSize = dtypeSize.substring(0, dtypeSize.indexOf(","));
                        }

                        arguments.setElementAt("VARCHAR(" + dtypeSize + ")", 0);
                     } else {
                        arguments.setElementAt("VARCHAR(23)", 0);
                     }
                  } else if (fScVecArg instanceof String) {
                     arg = fScVecArg.toString();
                     arguments.setElementAt("VARCHAR(" + arg.length() + ")", 0);
                  } else {
                     arguments.setElementAt("VARCHAR(23)", 0);
                  }
               }
            } else if (this.functionArguments.get(0) instanceof String) {
               format = this.functionArguments.get(0).toString();
               if (format.startsWith("'")) {
                  arguments.setElementAt("VARCHAR(" + format.length() + ")", 0);
               } else {
                  arguments.setElementAt("VARCHAR(23)", 0);
               }
            } else {
               arguments.setElementAt("VARCHAR(23)", 0);
            }
         }
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CHAR");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      if (SwisSQLOptions.UDBSQL) {
         if (arguments.size() == 2) {
            this.functionName.setColumnName("TO_CHAR");
         }

      } else {
         if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn) {
            SelectColumn forDatetimeToBeChanged = (SelectColumn)this.functionArguments.get(0);
            SelectColumn sc = (SelectColumn)this.functionArguments.get(1);
            String datetimeToBeChangedString = "";
            TableColumn tc = null;
            if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof TableColumn) {
               new TableColumn();
               tc = (TableColumn)forDatetimeToBeChanged.getColumnExpression().get(0);
               datetimeToBeChangedString = tc.getColumnName();
            }

            new Vector();
            if (sc.getColumnExpression().get(0) instanceof String) {
               String format = (String)sc.getColumnExpression().get(0);
               Vector arg = new Vector();
               arg.add(this.functionArguments.get(0));
               FunctionCalls dateFunction = new FunctionCalls();
               TableColumn dateColumn = new TableColumn();
               dateColumn.setColumnName("DATE");
               dateFunction.setFunctionName(dateColumn);
               dateFunction.setFunctionArguments(arg);
               this.functionArguments.setElementAt(dateFunction, 0);
               if (datetimeToBeChangedString.equalsIgnoreCase("CURRENT TIMESTAMP") || datetimeToBeChangedString.equalsIgnoreCase("CURRENT DATE")) {
                  this.functionArguments.setElementAt("CURRENT DATE", 0);
                  datetimeToBeChangedString = "CURRENT TIME";
               }

               if (format.toUpperCase().indexOf("MM/DD/YYYY") != -1) {
                  if (datetimeToBeChangedString != "") {
                     if (format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) {
                        if (format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                           this.functionArguments.setElementAt("USA) || ' ' || CHAR(" + datetimeToBeChangedString, 1);
                           this.functionArguments.addElement("ISO");
                        } else {
                           this.functionArguments.setElementAt("USA", 1);
                        }
                     } else {
                        this.functionArguments.setElementAt("USA) || ' ' || CHAR(" + datetimeToBeChangedString, 1);
                        this.functionArguments.addElement("USA");
                     }
                  } else {
                     this.functionArguments.setElementAt("USA", 1);
                  }
               } else if (format.toUpperCase().indexOf("DD.MM.YYYY") != -1) {
                  if (datetimeToBeChangedString != "") {
                     if (format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) {
                        if (format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                           this.functionArguments.setElementAt("EUR) || ' ' || CHAR(" + datetimeToBeChangedString, 1);
                           this.functionArguments.addElement("EUR");
                        } else {
                           this.functionArguments.setElementAt("USA", 1);
                        }
                     } else {
                        this.functionArguments.setElementAt("EUR) || ' ' || CHAR(" + datetimeToBeChangedString, 1);
                        this.functionArguments.addElement("USA");
                     }
                  } else {
                     this.functionArguments.setElementAt("EUR", 1);
                  }
               } else if (format.toUpperCase().indexOf("YYYY-MM-DD") != -1) {
                  if (datetimeToBeChangedString != "") {
                     if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                        this.functionArguments.setElementAt("ISO) || ' ' || CHAR(" + datetimeToBeChangedString, 1);
                        this.functionArguments.addElement("USA");
                     } else if (format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.setElementAt("ISO) || ' ' || CHAR(" + datetimeToBeChangedString, 1);
                        this.functionArguments.addElement("ISO");
                     } else {
                        this.functionArguments.setElementAt("USA", 1);
                     }
                  } else {
                     this.functionArguments.setElementAt("ISO", 1);
                  }
               } else if (format.toUpperCase().indexOf("DD-MM-YYYY") != -1) {
                  this.functionName.setColumnName("SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 1,2) || '-' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 4,2) || '-' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 7,4)");
                  this.setIfTime(datetimeToBeChangedString, format, tc);
                  this.setDummyArgs();
               } else if (format.toUpperCase().indexOf("YYYY-DD-MM") != -1) {
                  this.functionName.setColumnName("SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 7,4) || '-' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 1,2) || '-' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 4,2)");
                  this.setIfTime(datetimeToBeChangedString, format, tc);
                  this.setDummyArgs();
               } else if (format.toUpperCase().indexOf("YYYY.DD.MM") != -1) {
                  this.functionName.setColumnName("SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 7,4) || '.' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 1,2) || '.' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 4,2)");
                  this.setIfTime(datetimeToBeChangedString, format, tc);
                  this.setDummyArgs();
               } else if (format.toUpperCase().indexOf("YYYY.MM.DD") != -1) {
                  this.functionName.setColumnName("SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 7,4) || '.' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 4,2) || '.' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 1,2)");
                  this.setIfTime(datetimeToBeChangedString, format, tc);
                  this.setDummyArgs();
               } else if (format.toUpperCase().indexOf("YYYY/MM/DD") != -1) {
                  this.functionName.setColumnName("SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 7,4) || '/' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 4,2) || '/' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 1,2)");
                  this.setIfTime(datetimeToBeChangedString, format, tc);
                  this.setDummyArgs();
               } else if (format.toUpperCase().indexOf("DD/MM/YYYY") != -1) {
                  this.functionName.setColumnName("SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 1,2) || '/' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 4,2) || '/' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 7,4)");
                  this.setIfTime(datetimeToBeChangedString, format, tc);
                  this.setDummyArgs();
               } else if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1) && format.toUpperCase().indexOf("YY") == -1) {
                  if (datetimeToBeChangedString.equalsIgnoreCase("CURRENT TIMESTAMP") || datetimeToBeChangedString.equalsIgnoreCase("CURRENT TIME")) {
                     this.functionArguments.setElementAt("CURRENT TIME", 0);
                  }

                  this.functionArguments.setElementAt("USA", 1);
               } else if (format.toUpperCase().indexOf("HH24:MI:SS") != -1 && format.toUpperCase().indexOf("YY") == -1) {
                  if (datetimeToBeChangedString.equalsIgnoreCase("CURRENT TIMESTAMP") || datetimeToBeChangedString.equalsIgnoreCase("CURRENT TIME")) {
                     this.functionArguments.setElementAt("CURRENT TIME", 0);
                  }

                  this.functionArguments.setElementAt("ISO", 1);
               } else if (format.toUpperCase().indexOf("YYYY.IW") != -1) {
                  this.functionName.setColumnName("LTRIM(RTRIM(CHAR(YEAR(" + this.functionArguments.get(0) + ")))) || '.' || LTRIM(RTRIM(CHAR(WEEK(" + this.functionArguments.get(0) + "))))");
                  this.setDummyArgs();
               } else if (format.toUpperCase().indexOf("YYYY.MM") != -1) {
                  this.functionName.setColumnName("LTRIM(RTRIM(CHAR(YEAR(" + this.functionArguments.get(0) + ")))) || '.' || LTRIM(RTRIM(CHAR(MONTH(" + this.functionArguments.get(0) + "))))");
                  this.setDummyArgs();
               } else if (format.toUpperCase().indexOf("HH") == -1) {
                  this.functionArguments.setElementAt("USA", 1);
               } else {
                  this.functionName.setColumnName("CHAR(" + this.functionArguments.get(0) + ", USA)");
                  this.setIfTime(datetimeToBeChangedString, format, tc);
                  this.setDummyArgs();
               }
            }
         }

      }
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE_FORMAT");
      Vector arguments = new Vector();
      boolean convertToCast = false;

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      String mysqlFormat;
      SelectColumn sc;
      if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn) {
         sc = (SelectColumn)this.functionArguments.get(1);
         if (sc.getColumnExpression().get(0) instanceof String) {
            String format = (String)sc.getColumnExpression().get(0);
            mysqlFormat = "";
            if (format.toUpperCase().indexOf("MM/DD/YYYY") != -1) {
               mysqlFormat = "%m/%d/%Y";
               if (format.toUpperCase().indexOf("HH:MI:SS") != -1) {
                  mysqlFormat = mysqlFormat + " %T";
               } else if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                  mysqlFormat = mysqlFormat + " %r";
               }
            } else if (format.toUpperCase().indexOf("DD.MM.YYYY") != -1) {
               mysqlFormat = "%d.%m.%Y";
               if (format.toUpperCase().indexOf("HH:MI:SS") != -1) {
                  mysqlFormat = mysqlFormat + " %T";
               } else if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                  mysqlFormat = mysqlFormat + " %r";
               }
            } else if (format.toUpperCase().indexOf("DD/MM/YYYY") != -1) {
               mysqlFormat = "%d/%m/%Y";
               if (format.toUpperCase().indexOf("HH:MI:SS") != -1) {
                  mysqlFormat = mysqlFormat + " %T";
               } else if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                  mysqlFormat = mysqlFormat + " %r";
               }
            } else if (format.toUpperCase().indexOf("YYYY-MM-DD") != -1) {
               mysqlFormat = "%Y-%m-%d";
               if (format.toUpperCase().indexOf("HH:MI:SS") != -1) {
                  mysqlFormat = mysqlFormat + " %T";
               } else if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                  mysqlFormat = mysqlFormat + " %r";
               }
            } else if (format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
               mysqlFormat = "%T";
            } else {
               if (format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) {
                  throw new ConvertException("DATE FORMAT : " + format + " is yet to be supported in to_char()");
               }

               mysqlFormat = "%r";
            }

            mysqlFormat = "'" + mysqlFormat + "'";
            this.functionArguments.setElementAt(mysqlFormat, 1);
         }
      }

      if (this.functionArguments.size() == 1 && this.functionArguments.get(0) instanceof SelectColumn) {
         sc = (SelectColumn)this.functionArguments.get(0);
         Vector colExp = sc.getColumnExpression();
         if (colExp.size() == 1 && colExp.get(0) instanceof String) {
            mysqlFormat = (String)colExp.get(0);
            if (!mysqlFormat.startsWith("'")) {
               this.functionName.setColumnName("CAST");
               CharacterClass cc = new CharacterClass();
               cc.setDatatypeName("CHAR");
               this.setAsDatatype("AS");
               this.functionArguments.add(cc);
            }
         }
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      if (this.functionArguments.size() == 1) {
         SelectColumn sc = new SelectColumn();
         Vector colExpr = new Vector();
         this.functionName.setColumnName("CAST");
         CharacterClass cc = new CharacterClass();
         cc.setDatatypeName("VARCHAR");
         cc.setSize("4000");
         cc.setOpenBrace("(");
         cc.setClosedBrace(")");
         this.setAsDatatype("AS");
         colExpr.add(cc);
         sc.setColumnExpression(colExpr);
         this.functionArguments.add(sc);
      }

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
   }

   private void processFunctionArgumentsForRamco(Vector colExp) {
      if (colExp != null) {
         for(int i = 0; i < colExp.size(); ++i) {
            if (colExp.get(i) instanceof TableColumn) {
               if (((TableColumn)colExp.get(i)).getColumnName().startsWith("@")) {
                  FunctionCalls fc = new FunctionCalls();
                  TableColumn tc = new TableColumn();
                  SelectColumn sc = new SelectColumn();
                  Vector tableColVector = new Vector();
                  tableColVector.add(colExp.get(i));
                  sc.setColumnExpression(tableColVector);
                  Vector selectColumnVector = new Vector();
                  selectColumnVector.add(sc);
                  tc.setColumnName("FetchSqlDtFormat");
                  tc.setTableName("dbo");
                  fc.setFunctionName(tc);
                  colExp.set(i, fc);
               }
            } else if (colExp.get(i) instanceof String) {
               if (((String)colExp.get(i)).trim().startsWith("@")) {
                  colExp.set(i, "dbo.FetchSqlDtFormat(" + (String)colExp.get(i) + ")");
               }
            } else {
               Vector FunctionArgs;
               if (colExp.get(i) instanceof SelectColumn) {
                  FunctionArgs = ((SelectColumn)colExp.get(i)).getColumnExpression();
                  this.processFunctionArgumentsForRamco(FunctionArgs);
               } else if (colExp.get(i) instanceof FunctionCalls) {
                  FunctionArgs = ((FunctionCalls)colExp.get(i)).getFunctionArguments();
                  this.processFunctionArgumentsForRamco(FunctionArgs);
               }
            }
         }
      }

   }

   private void setDummyArgs() {
      Vector args = new Vector();
      this.setFunctionArguments(args);
      this.setOpenBracesForFunctionNameRequired(false);
   }

   private void setIfTime(String datetimeToBeChangedString, String format, TableColumn tc) {
      if (datetimeToBeChangedString.equals("CURRENT TIME")) {
         if (format.toUpperCase().indexOf("HH:MI") == -1 || format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1) {
            if (format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
               this.functionName.setColumnName(this.functionName.getColumnName() + " || ' ' || CHAR(" + datetimeToBeChangedString + ", ISO)");
            }
         } else {
            this.functionName.setColumnName(this.functionName.getColumnName() + " || ' ' || CHAR(" + datetimeToBeChangedString + ", USA)");
         }
      } else if (datetimeToBeChangedString != "") {
         if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1) && tc != null) {
            this.functionName.setColumnName(this.functionName.getColumnName() + " || ' ' || CHAR(TIME(" + tc.toString() + "), USA)");
         } else if (format.toUpperCase().indexOf("HH24:MI:SS") != -1 && tc != null) {
            this.functionName.setColumnName(this.functionName.getColumnName() + " || ' ' || CHAR(TIME(" + tc.toString() + "), ISO)");
         }
      }

   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector v = this.getFunctionArguments();
      if (v != null && v.elementAt(0) != null & v.elementAt(0) instanceof SelectColumn) {
         Vector colexpr = ((SelectColumn)v.elementAt(0)).getColumnExpression();
         if (colexpr.elementAt(0) != null && colexpr.elementAt(0) instanceof String && colexpr.elementAt(0).toString().startsWith("'")) {
            throw new ConvertException("\nTO_CHAR(literal_string) is not supported in TimesTen 5.1.21\n");
         }
      }

   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (arguments.size() == 1) {
         this.functionName.setColumnName("CAST");
         CharacterClass cc = new CharacterClass();
         cc.setDatatypeName("VARCHAR");
         cc.setSize("4000");
         cc.setOpenBrace("(");
         cc.setClosedBrace(")");
         this.setAsDatatype("AS");
         arguments.add(cc);
      }

      this.setFunctionArguments(arguments);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 3) {
         this.functionArguments.removeElementAt(2);
      }

      if (this.functionArguments.size() == 1) {
         this.functionName.setColumnName("CAST");
         CharacterClass cc = new CharacterClass();
         cc.setDatatypeName("VARCHAR");
         cc.setSize("50");
         if (SwisSQLOptions.castCharDatatypeAsCaseSpecific) {
            cc.setCaseSpecificPhrase("CASESPECIFIC");
         }

         cc.setOpenBrace("(");
         cc.setClosedBrace(")");
         this.setAsDatatype("AS");
         this.functionArguments.add(cc);
      } else if (this.functionArguments.size() == 2) {
         FunctionCalls newCastFunction = new FunctionCalls();
         TableColumn newTableColumn = new TableColumn();
         newTableColumn.setColumnName("CAST");
         Vector newFunctionArgs = new Vector();
         FunctionCalls dateFunc;
         TableColumn tocharFnName;
         DateClass dc;
         if (this.functionArguments.get(0) instanceof SelectColumn) {
            SelectColumn firstArg = (SelectColumn)this.functionArguments.get(0);
            if (firstArg.getColumnExpression().indexOf("+") != -1 || firstArg.getColumnExpression().indexOf("-") != -1) {
               for(int fi = 0; fi < firstArg.getColumnExpression().size(); ++fi) {
                  if (firstArg.getColumnExpression().get(fi) instanceof TableColumn) {
                     dateFunc = new FunctionCalls();
                     tocharFnName = new TableColumn();
                     dateFunc.setFunctionName(tocharFnName);
                     tocharFnName.setColumnName("CAST");
                     DateClass cc = new DateClass();
                     cc.setDatatypeName("DATE");
                     dateFunc.setAsDatatype("AS");
                     Vector castDateArgs = new Vector();
                     castDateArgs.add(firstArg.getColumnExpression().get(fi));
                     castDateArgs.add(cc);
                     dateFunc.setFunctionArguments(castDateArgs);
                     firstArg.getColumnExpression().setElementAt(dateFunc, fi);
                  } else if (firstArg.getColumnExpression().get(fi) instanceof FunctionCalls) {
                     dateFunc = (FunctionCalls)firstArg.getColumnExpression().get(fi);
                     if (dateFunc.getFunctionName() != null && SwisSQLUtils.getFunctionReturnType(dateFunc.getFunctionName().getColumnName(), dateFunc.getFunctionArguments()).equalsIgnoreCase("date")) {
                        FunctionCalls castDate = new FunctionCalls();
                        TableColumn castDateName = new TableColumn();
                        castDate.setFunctionName(castDateName);
                        castDateName.setColumnName("CAST");
                        dc = new DateClass();
                        dc.setDatatypeName("DATE");
                        castDate.setAsDatatype("AS");
                        Vector castDateArgs = new Vector();
                        castDateArgs.add(firstArg.getColumnExpression().get(fi));
                        castDateArgs.add(dc);
                        castDate.setFunctionArguments(castDateArgs);
                        firstArg.getColumnExpression().setElementAt(castDate, fi);
                     }
                  } else if (firstArg.getColumnExpression().get(fi) instanceof SelectColumn) {
                  }
               }
            }
         }

         newFunctionArgs.add(this.functionArguments.get(0));
         newCastFunction.setAsDatatype("AS");
         newCastFunction.setFunctionName(newTableColumn);
         Object formatString = this.functionArguments.get(1);
         boolean createCastFunction = true;
         if (formatString instanceof String) {
            newFunctionArgs.add(this.functionArguments.get(1));
         } else if (formatString instanceof SelectColumn) {
            SelectColumn scn = (SelectColumn)this.functionArguments.get(1);
            Object scnColObj = scn.getColumnExpression().get(0);
            if (scnColObj instanceof String) {
               String scnColStr = scnColObj.toString().toUpperCase();
               if (scnColStr.indexOf("$") == -1 && scnColStr.indexOf("FM") == -1 && scnColStr.indexOf("IW") == -1 && scnColStr.indexOf("J") == -1 && scnColStr.indexOf("Q") == -1 && scnColStr.indexOf("DAY") == -1 && scnColStr.indexOf("RN") == -1 && (scnColStr.indexOf("DD") != -1 || scnColStr.indexOf("DY") != -1 || scnColStr.indexOf("D") == -1) && (scnColStr.indexOf("DL") != -1 || scnColStr.indexOf("L") == -1) && (scnColStr.indexOf("FF") != -1 || scnColStr.length() <= 2 || !Character.isDigit(scnColStr.charAt(1)) && !Character.isDigit(scnColStr.charAt(scnColStr.length() - 2)))) {
                  if (scnColStr.indexOf("WW") != -1) {
                     createCastFunction = false;
                     scnColStr = scnColStr.replaceAll("WW", "IW");
                     scn.getColumnExpression().setElementAt(scnColStr, 0);
                  } else if (scnColStr.indexOf(":") == -1 && scnColStr.indexOf("HH") == -1) {
                     if (scnColStr.indexOf("G") == -1 && scnColStr.indexOf("X") == -1) {
                        if (scnColStr.indexOf("DAY") != -1 || scnColStr.indexOf("MON") != -1 || scnColStr.indexOf("YY") != -1 || scnColStr.indexOf("YEAR") != -1 || scnColStr.indexOf("RR") != -1 || scnColStr.indexOf("MM") != -1 || scnColStr.indexOf("IY") != -1 || scnColStr.indexOf("DD") != -1 || scnColStr.indexOf("DY") != -1 || scnColStr.indexOf("CC") != -1) {
                           if (scnColStr.indexOf("MONTH") != -1) {
                              scnColStr = scnColStr.replaceAll("MONTH", "MMMM");
                           }

                           if (scnColStr.indexOf("MON") != -1) {
                              scnColStr = scnColStr.replaceAll("MON", "MMM");
                           }

                           if (scnColStr.indexOf("RR") != -1) {
                              scnColStr = scnColStr.replaceAll("RR", "YY");
                           }

                           if (scnColStr.indexOf("IYYY") != -1) {
                              scnColStr = scnColStr.replaceAll("IYYY", "YYYY");
                           }

                           if (scnColStr.indexOf("DY") != -1) {
                              scnColStr = scnColStr.replaceAll("DY", "EEE");
                           }

                           scn.getColumnExpression().setElementAt(scnColStr.replaceAll(" ", "B"), 0);
                           dc = new DateClass();
                           dc.setDatatypeName("DATE");
                           scn.getColumnExpression().setElementAt(" FORMAT " + scnColStr.replaceAll(" ", "B"), 0);
                           scn.getColumnExpression().insertElementAt(dc, 0);
                        }
                     } else {
                        scn.getColumnExpression().setElementAt(scnColStr.replaceAll(" ", "B"), 0);
                     }
                  } else {
                     if (scnColStr.indexOf("HH24") != -1) {
                        scnColStr = scnColStr.replaceAll("HH24", "HH");
                     }

                     if (scnColStr.indexOf("AM") != -1) {
                        scnColStr = scnColStr.replaceAll("AM", "T");
                     } else if (scnColStr.indexOf("PM") != -1) {
                        scnColStr = scnColStr.replaceAll("PM", "T");
                     }

                     if (scnColStr.indexOf(" ") != -1) {
                        scnColStr = scnColStr.replaceAll(" ", "B");
                     } else if (scnColStr.indexOf(".") != -1) {
                        scnColStr = scnColStr.replaceAll("\\.", "B");
                     }

                     if (scnColStr.indexOf("DAY") != -1 || scnColStr.indexOf("MON") != -1 || scnColStr.indexOf("YY") != -1 || scnColStr.indexOf("YEAR") != -1 || scnColStr.indexOf("RR") != -1 || scnColStr.indexOf("MM") != -1 || scnColStr.indexOf("IY") != -1 || scnColStr.indexOf("DD") != -1 || scnColStr.indexOf("DY") != -1 || scnColStr.indexOf("CC") != -1) {
                        if (scnColStr.indexOf("MONTH") != -1) {
                           scnColStr = scnColStr.replaceAll("MONTH", "MMMM");
                        }

                        if (scnColStr.indexOf("MON") != -1) {
                           scnColStr = scnColStr.replaceAll("MON", "MMM");
                        }

                        if (scnColStr.indexOf("RR") != -1) {
                           scnColStr = scnColStr.replaceAll("RR", "YY");
                        }

                        if (scnColStr.indexOf("IYYY") != -1) {
                           scnColStr = scnColStr.replaceAll("IYYY", "YYYY");
                        }

                        if (scnColStr.indexOf("DY") != -1) {
                           scnColStr = scnColStr.replaceAll("DY", "EEE");
                        }
                     }

                     dc = new DateClass();
                     dc.setDatatypeName("TIMESTAMP");
                     dc.setOpenBrace("(");
                     dc.setClosedBrace(")");
                     dc.setSize("0");
                     scn.getColumnExpression().setElementAt(" FORMAT " + scnColStr, 0);
                     scn.getColumnExpression().insertElementAt(dc, 0);
                  }
               } else {
                  createCastFunction = false;
               }

               if (!scnColStr.startsWith("'") && !scnColStr.endsWith("'")) {
                  try {
                     Double.parseDouble(scnColStr);
                     scnColStr = "'" + scnColStr + "'";
                     scn.getColumnExpression().setElementAt(scnColStr, 0);
                     createCastFunction = false;
                  } catch (Exception var15) {
                  }
               }

               newFunctionArgs.add(this.functionArguments.get(1));
            }
         }

         newCastFunction.setFunctionArguments(newFunctionArgs);
         Vector fnArgColExp;
         if (createCastFunction) {
            this.functionName.setColumnName("CAST");
            CharacterClass cc = new CharacterClass();
            cc.setDatatypeName("VARCHAR");
            cc.setSize("50");
            if (SwisSQLOptions.castCharDatatypeAsCaseSpecific) {
               cc.setCaseSpecificPhrase("CASESPECIFIC");
            }

            cc.setOpenBrace("(");
            cc.setClosedBrace(")");
            this.setAsDatatype("AS");
            SelectColumn fnArgCol = new SelectColumn();
            fnArgColExp = new Vector();
            fnArgColExp.add(newCastFunction);
            fnArgCol.setColumnExpression(fnArgColExp);
            this.functionArguments.setElementAt(fnArgCol, 0);
            this.functionArguments.setElementAt(cc, 1);
         } else if (this.functionArguments.size() > 1 && this.functionArguments.elementAt(1).toString().equalsIgnoreCase("'d'")) {
            dateFunc = new FunctionCalls();
            tocharFnName = new TableColumn();
            tocharFnName.setColumnName(this.functionName.getColumnName());
            dateFunc.setFunctionName(tocharFnName);
            fnArgColExp = new Vector();
            fnArgColExp.addAll(this.functionArguments);
            dateFunc.setFunctionArguments(fnArgColExp);
            this.functionName.setColumnName("CAST");
            NumericClass nc = new NumericClass();
            nc.setDatatypeName("INTEGER");
            nc.setPrecision((String)null);
            nc.setScale((String)null);
            nc.setOpenBrace((String)null);
            nc.setClosedBrace((String)null);
            this.setAsDatatype("AS");
            SelectColumn fnArgCol = new SelectColumn();
            Vector fnArgColExp = new Vector();
            fnArgColExp.add(dateFunc);
            fnArgCol.setColumnExpression(fnArgColExp);
            this.functionArguments.setElementAt(fnArgCol, 0);
            this.functionArguments.setElementAt(nc, 1);
         }
      }

   }

   private boolean checkIfDecimalFormat(String format) {
      int beginIndex = format.indexOf(".");
      int lastIndex = format.lastIndexOf(".");
      if (beginIndex != -1 && beginIndex == lastIndex) {
         try {
            String tempFormat = format;
            if (format.startsWith("'")) {
               tempFormat = format.substring(1, format.length() - 1);
            }

            Integer.parseInt(tempFormat.substring(0, beginIndex - 1));
            Integer.parseInt(tempFormat.substring(beginIndex, tempFormat.length()));
            return true;
         } catch (NumberFormatException var5) {
            return false;
         }
      } else {
         return false;
      }
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
   }
}
