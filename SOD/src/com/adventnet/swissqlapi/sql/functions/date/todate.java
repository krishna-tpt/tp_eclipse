package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.Vector;

public class todate extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CONVERT");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      String datetimeToBeChangedString;
      String format;
      if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn && this.functionArguments.get(0) instanceof SelectColumn) {
         SelectColumn forDatetimeToBeChanged = (SelectColumn)this.functionArguments.get(0);
         SelectColumn sc = (SelectColumn)this.functionArguments.get(1);
         datetimeToBeChangedString = "";
         if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof TableColumn) {
            new TableColumn();
            TableColumn tc = (TableColumn)forDatetimeToBeChanged.getColumnExpression().get(0);
            datetimeToBeChangedString = tc.getColumnName();
         } else if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof String) {
            datetimeToBeChangedString = (String)forDatetimeToBeChanged.getColumnExpression().get(0);
         } else if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof FunctionCalls) {
            datetimeToBeChangedString = forDatetimeToBeChanged.getColumnExpression().get(0).toString();
         }

         new Vector();
         if (sc.getColumnExpression().get(0) instanceof String) {
            format = (String)sc.getColumnExpression().get(0);
            if (format.trim().startsWith("'") && format.trim().endsWith("'")) {
               if (FunctionCalls.charToIntName) {
                  if (format.trim().startsWith("'") && format.endsWith("'")) {
                     this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                     this.functionArguments.setElementAt("DATETIME", 0);
                     this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + format + ")");
                  }
               } else if (!format.equalsIgnoreCase("'mon dd yyyy hh:miAM'") && !format.equalsIgnoreCase("'mon dd yyyy hh:miPM'")) {
                  if (!format.equalsIgnoreCase("'mon dd yyyy hh:mi:ss:mmmAM'") && !format.equalsIgnoreCase("'mon dd yyyy hh:mi:ss:mmmPM'")) {
                     if (format.equalsIgnoreCase("'dd mon yyyy hh:mm:ss:mmm'")) {
                        arguments.add(2, "113");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.equalsIgnoreCase("'dd mon yy hh:mm:ss:mmm'")) {
                        arguments.add(2, "13");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("MM/DD/YYY") != -1) {
                        arguments.add(2, "101");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("MM/DD/YY") != -1) {
                        arguments.add(2, "1");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("YYYY.MM.DD") != -1) {
                        arguments.add(2, "102");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("YY.MM.DD") != -1) {
                        arguments.add(2, "2");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("DD/MM/YYY") != -1) {
                        arguments.add(2, "103");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("DD/MM/YY") != -1) {
                        arguments.add(2, "3");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("DD.MM.YYY") != -1) {
                        arguments.add(2, "104");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("DD.MM.YY") != -1) {
                        arguments.add(2, "4");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("DD-MM-YYY") != -1) {
                        arguments.add(2, "105");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("DD-MM-YY") != -1) {
                        arguments.add(2, "5");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("DD MON YYYY") != -1) {
                        arguments.add(2, "106");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("DD MON YY") != -1) {
                        arguments.add(2, "6");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("MON DD, YYYY") != -1) {
                        arguments.add(2, "107");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("MON DD, YY") != -1) {
                        arguments.add(2, "7");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("HH:MM:SS") != -1) {
                        arguments.add(2, "108");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("MM-DD-YYY") != -1) {
                        arguments.add(2, "110");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("MM-DD-YY") != -1) {
                        arguments.add(2, "10");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("YYY/MM/DD") != -1) {
                        arguments.add(2, "111");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("YY/MM/DD") != -1) {
                        arguments.add(2, "11");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("YYYYMMDD") != -1) {
                        arguments.add(2, "112");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("YYMMDD") != -1) {
                        arguments.add(2, "12");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("HH:MI:SS:MMM") != -1) {
                        arguments.add(2, "114");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else if (format.toUpperCase().indexOf("YYY-MM-DD") != -1) {
                        arguments.add(2, "121");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     } else {
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                     }
                  } else {
                     arguments.add(2, "109");
                     arguments.setElementAt(this.functionArguments.get(0), 1);
                     arguments.setElementAt("DATETIME", 0);
                  }
               } else {
                  arguments.add(2, "100");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               }
            } else {
               if (FunctionCalls.charToIntName && format.trim().startsWith("@")) {
                  this.functionArguments.setElementAt("dbo.FetchSqlDtFormat(" + format + ")", 2);
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("DATETIME", 0);
            }
         } else if (sc.getColumnExpression().get(0) instanceof FunctionCalls) {
            arguments.add(2, this.functionArguments.get(1));
            if (FunctionCalls.charToIntName) {
               FunctionCalls fc = (FunctionCalls)sc.getColumnExpression().get(0);
               TableColumn tc = fc.getFunctionName();
               if (tc.getColumnName().equalsIgnoreCase("CONVERTSQLSERVERDATEFORMAT") && fc.getFunctionArguments() != null && fc.getFunctionArguments().size() == 1) {
                  tc.setColumnName("");
                  tc.setTableName((String)null);
                  tc.setOwnerName((String)null);
               } else if (fc.getFunctionArguments().size() == 0) {
                  if (sc.getColumnExpression().size() > 1 && sc.getColumnExpression().get(1) instanceof String && ((String)sc.getColumnExpression().get(1)).trim().equals("+")) {
                     if (!this.ramcoSpecificDateFormatHandling(arguments)) {
                        arguments.setElementAt("dbo.FetchSqlDtFormat(" + this.functionArguments.get(1).toString() + ")", 2);
                     }
                  } else {
                     tc.setColumnName(tc.getColumnName().trim() + "INT");
                  }
               } else {
                  this.processFunctionArgumentsForRamco(fc.getFunctionArguments());
               }
            }

            arguments.setElementAt(this.functionArguments.get(0), 1);
            arguments.setElementAt("DATETIME", 0);
         } else {
            arguments.add(2, this.functionArguments.get(1));
            if (FunctionCalls.charToIntName && arguments.elementAt(2) instanceof SelectColumn) {
               SelectColumn selectcolumn = (SelectColumn)arguments.get(2);
               Vector expression = selectcolumn.getColumnExpression();
               if (expression != null) {
                  for(int j = 0; j < expression.size(); ++j) {
                     if (expression.get(j) instanceof TableColumn) {
                        TableColumn tc = (TableColumn)expression.get(j);
                        String arg2 = tc.getColumnName();
                        if (arg2.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                           String newformat = arg2.trim().substring(30);
                           if (newformat.trim().startsWith("(") && newformat.trim().endsWith(")")) {
                              newformat = newformat.trim().substring(1, newformat.trim().length() - 1);
                              this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                              this.functionArguments.setElementAt(newformat, 2);
                           } else {
                              if (arg2.indexOf(40) == -1) {
                                 arg2 = "dbo." + arg2.trim() + "INT()";
                              }

                              arguments.setElementAt(arg2, 2);
                           }
                        } else if (arg2.trim().startsWith("@")) {
                           arg2 = "dbo.FetchSqlFormat(" + arg2 + ")";
                           arguments.setElementAt(arg2, 2);
                        } else {
                           if (expression.size() > 1 && expression.get(1) instanceof String && ((String)expression.get(1)).trim().equals("+")) {
                              if (!this.ramcoSpecificDateFormatHandling(arguments)) {
                                 arguments.setElementAt("dbo.FetchSqlDtFormat(" + this.functionArguments.get(1).toString() + ")", 2);
                              } else {
                                 SelectColumn selCol = (SelectColumn)this.functionArguments.get(0);
                                 Vector colExp = selCol.getColumnExpression();
                                 arguments.setElementAt(colExp.get(0), 0);
                                 arg2 = "dbo." + arg2.trim() + "INT()) + (" + colExp.get(4);
                                 tc.setColumnName(arg2);
                                 Vector newColExp = new Vector();
                                 newColExp.add(tc);
                                 SelectColumn newSelCol = new SelectColumn();
                                 newSelCol.setColumnExpression(newColExp);
                                 arguments.setElementAt(newSelCol, 2);
                              }
                              break;
                           }

                           arg2 = "dbo." + arg2.trim() + "INT()";
                           tc.setColumnName(arg2);
                           arguments.setElementAt(this.functionArguments.get(1), 2);
                        }
                     } else if (expression.get(j) instanceof FunctionCalls) {
                        FunctionCalls fncalls = (FunctionCalls)expression.get(j);
                        if (fncalls.getFunctionName().getColumnName().equalsIgnoreCase("CONVERTSQLSERVERDATEFORMAT") && fncalls.getFunctionArguments() != null && fncalls.getFunctionArguments().size() == 1) {
                           arguments.setElementAt(fncalls.getFunctionArguments().get(0), 2);
                        }
                     }
                  }
               }
            }

            arguments.setElementAt(this.functionArguments.get(0), 1);
            arguments.setElementAt("DATETIME", 0);
         }
      } else if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof String) {
         new Vector();
         if (this.functionArguments.get(1) instanceof String) {
            String format = (String)this.functionArguments.get(1);
            datetimeToBeChangedString = (String)this.functionArguments.get(1);
            if (format.trim().startsWith("'") && format.trim().endsWith("'")) {
               if (FunctionCalls.charToIntName) {
                  if (format.trim().startsWith("'") && format.endsWith("'")) {
                     this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                     this.functionArguments.setElementAt("DATETIME", 0);
                     this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + format + ")");
                  }
               } else if (format.toUpperCase().indexOf("DD/MM/YYY") != -1) {
                  arguments.add(2, "103");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (format.toUpperCase().indexOf("MM/DD/YYY") != -1) {
                  arguments.add(2, "101");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (format.toUpperCase().indexOf("YYY/MM/DD") != -1) {
                  arguments.add(2, "111");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (format.toUpperCase().indexOf("DD-MM-YYY") != -1) {
                  arguments.add(2, "105");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (format.toUpperCase().indexOf("MM-DD-YYY") != -1) {
                  arguments.add(2, "110");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (format.toUpperCase().indexOf("YYY-MM-DD") != -1) {
                  arguments.add(2, "121");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (format.toUpperCase().indexOf("DD-MM-YY") != -1) {
                  arguments.add(2, "5");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (format.toUpperCase().indexOf("YYYYMMDD") != -1) {
                  arguments.add(2, "112");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (format.toUpperCase().indexOf("YYMMDD") != -1) {
                  arguments.add(2, "12");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (format.toUpperCase().indexOf("YYYY.MM.DD") != -1) {
                  arguments.add(2, "102");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (format.toUpperCase().indexOf("YY.MM.DD") != -1) {
                  arguments.add(2, "2");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else {
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               }
            } else {
               arguments.add(2, this.functionArguments.get(1));
               String arg2 = (String)arguments.get(2);
               if (FunctionCalls.charToIntName && arguments.elementAt(2) instanceof String) {
                  if (format.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                     format = format.trim().substring(30);
                     if (format.trim().startsWith("(") && format.trim().endsWith(")")) {
                        format = format.trim().substring(1, format.trim().length() - 1);
                        this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                        this.functionArguments.setElementAt(format, 2);
                     } else {
                        if (arg2.indexOf(40) == -1) {
                           arg2 = "dbo." + arg2.trim() + "INT()";
                        } else {
                           arg2 = arg2.substring(0, arg2.indexOf(40)).trim() + "INT" + arg2.substring(arg2.indexOf("("));
                        }

                        arguments.setElementAt(arg2, 2);
                     }
                  } else if (arg2.trim().startsWith("@")) {
                     arg2 = "dbo.FetchSqlFormat(" + arg2 + ")";
                     arguments.setElementAt(arg2, 2);
                  } else {
                     if (arg2.indexOf(40) == -1) {
                        arg2 = "dbo." + arg2.trim() + "INT()";
                     } else {
                        arg2 = arg2.substring(0, arg2.indexOf(40)).trim() + "INT" + arg2.substring(arg2.indexOf("("));
                     }

                     arguments.setElementAt(arg2, 2);
                  }
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("DATETIME", 0);
            }
         }
      } else if (this.functionArguments.size() == 1) {
         arguments.add(1, this.functionArguments.get(0));
         arguments.setElementAt("DATETIME", 0);
      } else {
         arguments.setElementAt("DATETIME", 0);
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CONVERT");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      String newformat;
      String newformat;
      if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn && this.functionArguments.get(0) instanceof SelectColumn) {
         SelectColumn forDatetimeToBeChanged = (SelectColumn)this.functionArguments.get(0);
         SelectColumn sc = (SelectColumn)this.functionArguments.get(1);
         newformat = "";
         if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof TableColumn) {
            new TableColumn();
            TableColumn tc = (TableColumn)forDatetimeToBeChanged.getColumnExpression().get(0);
            newformat = tc.getColumnName();
         } else if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof String) {
            newformat = (String)forDatetimeToBeChanged.getColumnExpression().get(0);
         } else if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof FunctionCalls) {
            newformat = forDatetimeToBeChanged.getColumnExpression().get(0).toString();
         }

         new Vector();
         if (sc.getColumnExpression().get(0) instanceof String) {
            newformat = (String)sc.getColumnExpression().get(0);
            if (newformat.trim().startsWith("'") && newformat.trim().endsWith("'")) {
               if (FunctionCalls.charToIntName) {
                  if (newformat.trim().startsWith("'") && newformat.endsWith("'")) {
                     this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                     this.functionArguments.setElementAt("DATETIME", 0);
                     this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + newformat + ")");
                  }
               } else if (newformat.toUpperCase().indexOf("DD/MM/YYYY") != -1) {
                  arguments.add(2, "103");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (newformat.toUpperCase().indexOf("MM/DD/YYYY") != -1) {
                  arguments.add(2, "102");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (newformat.toUpperCase().indexOf("YYYY-MM-DD") != -1) {
                  arguments.add(2, "121");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (newformat.toUpperCase().indexOf("DD-MM-YY") != -1) {
                  arguments.add(2, "5");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (newformat.toUpperCase().indexOf("YYYYMMDD") != -1) {
                  arguments.add(2, "112");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (newformat.toUpperCase().indexOf("YYMMDD") != -1) {
                  arguments.add(2, "12");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else {
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               }
            } else {
               arguments.add(2, this.functionArguments.get(1));
               if (FunctionCalls.charToIntName && newformat.trim().startsWith("@")) {
                  this.functionArguments.setElementAt("dbo.FetchSqlDtFormat(" + newformat + ")", 2);
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("DATETIME", 0);
            }
         } else if (sc.getColumnExpression().get(0) instanceof FunctionCalls) {
            arguments.add(2, this.functionArguments.get(1));
            if (FunctionCalls.charToIntName) {
               FunctionCalls fc = (FunctionCalls)sc.getColumnExpression().get(0);
               TableColumn tc = fc.getFunctionName();
               if (tc.getColumnName().equalsIgnoreCase("CONVERTSQLSERVERDATEFORMAT") && fc.getFunctionArguments() != null && fc.getFunctionArguments().size() == 1) {
                  tc.setColumnName("");
                  tc.setTableName((String)null);
                  tc.setOwnerName((String)null);
               } else if (fc.getFunctionArguments().size() == 0) {
                  if (sc.getColumnExpression().size() > 1 && sc.getColumnExpression().get(1) instanceof String && ((String)sc.getColumnExpression().get(1)).trim().equals("+")) {
                     if (!this.ramcoSpecificDateFormatHandling(arguments)) {
                        arguments.setElementAt("dbo.FetchSqlDtFormat(" + this.functionArguments.get(1).toString() + ")", 2);
                     }
                  } else {
                     tc.setColumnName(tc.getColumnName().trim() + "INT");
                  }
               } else {
                  this.processFunctionArgumentsForRamco(fc.getFunctionArguments());
               }
            }

            arguments.setElementAt(this.functionArguments.get(0), 1);
            arguments.setElementAt("DATETIME", 0);
         } else {
            arguments.add(2, this.functionArguments.get(1));
            if (FunctionCalls.charToIntName && arguments.elementAt(2) instanceof SelectColumn) {
               SelectColumn selectcolumn = (SelectColumn)arguments.get(2);
               Vector expression = selectcolumn.getColumnExpression();
               if (expression != null) {
                  for(int j = 0; j < expression.size(); ++j) {
                     if (expression.get(j) instanceof TableColumn) {
                        TableColumn tc = (TableColumn)expression.get(j);
                        String arg2 = tc.getColumnName();
                        if (arg2.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                           String newformat = arg2.trim().substring(30);
                           if (newformat.trim().startsWith("(") && newformat.trim().endsWith(")")) {
                              newformat = newformat.trim().substring(1, newformat.trim().length() - 1);
                              this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                              this.functionArguments.setElementAt(newformat, 2);
                           } else {
                              if (arg2.indexOf(40) == -1) {
                                 arg2 = "dbo." + arg2.trim() + "INT()";
                              }

                              arguments.setElementAt(arg2, 2);
                           }
                        } else if (arg2.trim().startsWith("@")) {
                           arg2 = "dbo.FetchSqlFormat(" + arg2 + ")";
                           arguments.setElementAt(arg2, 2);
                        } else {
                           if (expression.size() > 1 && expression.get(1) instanceof String && ((String)expression.get(1)).trim().equals("+")) {
                              if (!this.ramcoSpecificDateFormatHandling(arguments)) {
                                 arguments.setElementAt("dbo.FetchSqlDtFormat(" + this.functionArguments.get(1).toString() + ")", 2);
                              } else {
                                 SelectColumn selCol = (SelectColumn)this.functionArguments.get(0);
                                 Vector colExp = selCol.getColumnExpression();
                                 arguments.setElementAt(colExp.get(0), 0);
                                 arg2 = "dbo." + arg2.trim() + "INT()) + (" + colExp.get(4);
                                 tc.setColumnName(arg2);
                                 Vector newColExp = new Vector();
                                 newColExp.add(tc);
                                 SelectColumn newSelCol = new SelectColumn();
                                 newSelCol.setColumnExpression(newColExp);
                                 arguments.setElementAt(newSelCol, 2);
                              }
                              break;
                           }

                           arg2 = "dbo." + arg2.trim() + "INT()";
                           tc.setColumnName(arg2);
                           arguments.setElementAt(this.functionArguments.get(1), 2);
                        }
                     } else if (expression.get(j) instanceof FunctionCalls) {
                        FunctionCalls fncalls = (FunctionCalls)expression.get(j);
                        if (fncalls.getFunctionName().getColumnName().equalsIgnoreCase("CONVERTSQLSERVERDATEFORMAT") && fncalls.getFunctionArguments() != null && fncalls.getFunctionArguments().size() == 1) {
                           arguments.setElementAt(fncalls.getFunctionArguments().get(0), 2);
                        }
                     }
                  }
               }
            }

            arguments.setElementAt(this.functionArguments.get(0), 1);
            arguments.setElementAt("DATETIME", 0);
         }
      } else if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof String) {
         new Vector();
         String format;
         if (this.functionArguments.get(1) instanceof String) {
            format = (String)this.functionArguments.get(1);
            newformat = (String)this.functionArguments.get(1);
            String arg2;
            if (format.trim().startsWith("'") && format.trim().endsWith("'")) {
               if (FunctionCalls.charToIntName) {
                  if (format.trim().startsWith("'") && format.endsWith("'")) {
                     this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                     this.functionArguments.setElementAt("DATETIME", 0);
                     this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + format + ")");
                  }
               } else if (format.toUpperCase().indexOf("DD/MM/YYYY") != -1) {
                  arguments.add(2, "103");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (format.toUpperCase().indexOf("MM/DD/YYYY") != -1) {
                  arguments.add(2, "102");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else if (format.toUpperCase().indexOf("YYYY-MM-DD") != -1) {
                  arguments.add(2, "121");
                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               } else {
                  arguments.add(2, this.functionArguments.get(1));
                  arg2 = (String)arguments.get(2);
                  if (FunctionCalls.charToIntName && arguments.elementAt(2) instanceof String) {
                     if (format.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                        newformat = format.trim().substring(30);
                        if (newformat.trim().startsWith("(") && newformat.trim().endsWith(")")) {
                           newformat = newformat.trim().substring(1, newformat.trim().length() - 1);
                           this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                           this.functionArguments.setElementAt(newformat, 2);
                        } else {
                           if (arg2.indexOf(40) == -1) {
                              arg2 = "dbo." + arg2.trim() + "INT()";
                           } else {
                              arg2 = arg2.substring(0, arg2.indexOf(40)).trim() + "INT" + arg2.substring(arg2.indexOf("("));
                           }

                           arguments.setElementAt(arg2, 2);
                        }
                     } else if (arg2.trim().startsWith("@")) {
                        arg2 = "dbo.FetchSqlFormat(" + arg2 + ")";
                        arguments.setElementAt(arg2, 2);
                     } else {
                        if (arg2.indexOf(40) == -1) {
                           arg2 = "dbo." + arg2.trim() + "INT()";
                        } else {
                           arg2 = arg2.substring(0, arg2.indexOf(40)).trim() + "INT" + arg2.substring(arg2.indexOf("("));
                        }

                        arguments.setElementAt(arg2, 2);
                     }
                  }

                  arguments.setElementAt(this.functionArguments.get(0), 1);
                  arguments.setElementAt("DATETIME", 0);
               }
            } else {
               arguments.add(2, this.functionArguments.get(1));
               arg2 = (String)arguments.get(2);
               if (FunctionCalls.charToIntName && arguments.elementAt(2) instanceof String) {
                  if (format.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                     newformat = format.trim().substring(30);
                     if (newformat.trim().startsWith("(") && newformat.trim().endsWith(")")) {
                        newformat = newformat.trim().substring(1, newformat.trim().length() - 1);
                        this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                        this.functionArguments.setElementAt(newformat, 2);
                     } else {
                        if (arg2.indexOf(40) == -1) {
                           arg2 = "dbo." + arg2.trim() + "INT()";
                        } else {
                           arg2 = arg2.substring(0, arg2.indexOf(40)).trim() + "INT" + arg2.substring(arg2.indexOf("("));
                        }

                        arguments.setElementAt(arg2, 2);
                     }
                  } else if (arg2.trim().startsWith("@")) {
                     arg2 = "dbo.FetchSqlFormat(" + arg2 + ")";
                     arguments.setElementAt(arg2, 2);
                  } else {
                     if (arg2.indexOf(40) == -1) {
                        arg2 = "dbo." + arg2.trim() + "INT()";
                     } else {
                        arg2 = arg2.substring(0, arg2.indexOf(40)).trim() + "INT" + arg2.substring(arg2.indexOf("("));
                     }

                     arguments.setElementAt(arg2, 2);
                  }
               }

               arguments.setElementAt(this.functionArguments.get(0), 1);
               arguments.setElementAt("DATETIME", 0);
            }
         } else {
            arguments.add(2, this.functionArguments.get(1));
            if (FunctionCalls.charToIntName && arguments.elementAt(2) instanceof String) {
               format = (String)arguments.get(2);
               if (format.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                  newformat = format.trim().substring(30);
                  if (newformat.trim().startsWith("(") && newformat.trim().endsWith(")")) {
                     newformat = newformat.trim().substring(1, newformat.trim().length() - 1);
                     this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                     this.functionArguments.setElementAt(newformat, 2);
                  } else {
                     if (format.indexOf(40) == -1) {
                        format = "dbo." + format.trim() + "INT()";
                     } else {
                        format = format.substring(0, format.indexOf(40)).trim() + "INT" + format.substring(format.indexOf("("));
                     }

                     arguments.setElementAt(format, 2);
                  }
               } else if (format.trim().startsWith("@")) {
                  format = "dbo.FetchSqlFormat(" + format + ")";
                  arguments.setElementAt(format, 2);
               } else {
                  if (format.indexOf(40) == -1) {
                     format = "dbo." + format.trim() + "INT()";
                  } else {
                     format = format.substring(0, format.indexOf(40)).trim() + "INT" + format.substring(format.indexOf("("));
                  }

                  arguments.setElementAt(format, 2);
               }
            }

            arguments.setElementAt(this.functionArguments.get(0), 1);
            arguments.setElementAt("DATETIME", 0);
         }
      } else if (this.functionArguments.size() == 1) {
         arguments.add(1, this.functionArguments.get(0));
         arguments.setElementAt("DATETIME", 0);
      } else {
         arguments.setElementAt("DATETIME", 0);
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TIMESTAMP");
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
            this.functionName.setColumnName("TO_DATE");
         }

      } else {
         FunctionCalls subFunction = new FunctionCalls();
         TableColumn subTC = new TableColumn();
         subTC.setColumnName("DATE");
         subFunction.setFunctionName(subTC);
         SelectColumn forDatetimeToBeChanged;
         if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn) {
            forDatetimeToBeChanged = (SelectColumn)this.functionArguments.get(0);
            SelectColumn sc = (SelectColumn)this.functionArguments.get(1);
            String datetimeToBeChangedString = "";
            if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof TableColumn) {
               new TableColumn();
               TableColumn tc = (TableColumn)forDatetimeToBeChanged.getColumnExpression().get(0);
               datetimeToBeChangedString = tc.getColumnName();
            } else if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof String) {
               datetimeToBeChangedString = (String)forDatetimeToBeChanged.getColumnExpression().get(0);
            } else if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof FunctionCalls) {
               datetimeToBeChangedString = ((FunctionCalls)forDatetimeToBeChanged.getColumnExpression().get(0)).toString();
            }

            new Vector();
            if (sc.getColumnExpression().get(0) instanceof String) {
               String format = (String)sc.getColumnExpression().get(0);
               if (format.toUpperCase().indexOf("MONTH") != -1) {
                  throw new ConvertException("MONTH in TO_DATE() is not supported");
               }

               if (format.toUpperCase().indexOf("Y") != -1) {
                  if (format.toUpperCase().indexOf("YY") != -1) {
                     if (format.toUpperCase().indexOf("YYY") != -1) {
                        if (format.toUpperCase().indexOf("YYYY") != -1) {
                           if (datetimeToBeChangedString.indexOf("'") != -1 && datetimeToBeChangedString.length() < 12 && datetimeToBeChangedString.length() > 7) {
                              if (datetimeToBeChangedString.length() == 10) {
                                 this.functionArguments.setElementAt("SUBSTR(CHAR(" + datetimeToBeChangedString + "),5,4) || '-' || ", 0);
                              } else if (datetimeToBeChangedString.length() == 11) {
                                 this.functionArguments.setElementAt("SUBSTR(CHAR(" + datetimeToBeChangedString + "),6,4) || '-' || ", 0);
                              }
                           } else {
                              this.functionArguments.setElementAt("SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("YYYY") + ",4) || '-' || ", 0);
                           }
                        } else {
                           this.functionArguments.setElementAt("CHAR(YEAR(CURRENT DATE) - MOD(YEAR(CURRENT DATE),1000) + INT(SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("YYY") + ",3))) || '-' || ", 0);
                        }
                     } else {
                        this.functionArguments.setElementAt("CHAR(YEAR(CURRENT DATE) - MOD(YEAR(CURRENT DATE),100) + INT(SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("YY") + ",2))) || '-' || ", 0);
                     }
                  } else {
                     this.functionArguments.setElementAt("CHAR(YEAR(CURRENT DATE) - MOD(YEAR(CURRENT DATE),10) + INT(SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("Y") + ",1))) || '-' || ", 0);
                  }
               } else {
                  this.functionArguments.setElementAt("CHAR(YEAR(CURRENT DATE)) || '-' || ", 0);
               }

               if (format.toUpperCase().indexOf("MM") != -1) {
                  if (datetimeToBeChangedString.indexOf("'") != -1 && datetimeToBeChangedString.length() < 12 && datetimeToBeChangedString.length() > 7) {
                     if (datetimeToBeChangedString.length() == 10) {
                        this.functionArguments.setElementAt("SUBSTR(CHAR(" + datetimeToBeChangedString + "),3,1) || '-' || ", 1);
                     } else if (datetimeToBeChangedString.length() == 11) {
                        this.functionArguments.setElementAt("SUBSTR(CHAR(" + datetimeToBeChangedString + "),3,2) || '-' || ", 1);
                     }
                  } else {
                     this.functionArguments.setElementAt("SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("MM") + ",2) || '-' || ", 1);
                  }
               } else {
                  this.functionArguments.setElementAt("CHAR(MONTH(CURRENT DATE)) || '-' || ", 1);
               }

               if (format.toUpperCase().indexOf("D") != -1) {
                  if (format.toUpperCase().indexOf("DD") != -1) {
                     if (datetimeToBeChangedString.indexOf("'") != -1 && datetimeToBeChangedString.length() < 12 && datetimeToBeChangedString.length() > 7) {
                        this.functionArguments.add("SUBSTR(CHAR(" + datetimeToBeChangedString + "),1,1)");
                     } else {
                        this.functionArguments.add("SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("DD") + ",2)");
                     }
                  } else {
                     this.functionArguments.add("01");
                  }
               } else {
                  this.functionArguments.add("CHAR(DAY(CURRENT DATE))");
               }

               if (format.toUpperCase().indexOf("HH") != -1) {
                  if (format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                     this.functionArguments.add(" || ' ' || SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("HH24:MI:SS") + ", 8)");
                  } else if (format.toUpperCase().indexOf("HH:MI:SS") != -1) {
                     this.functionArguments.add(" || ' ' || SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("HH:MI:SS") + ", 8)");
                  } else {
                     this.functionArguments.add(" || ' 00:00:00'");
                  }
               } else {
                  this.functionArguments.add(" || ' 00:00:00'");
               }

               String newArgString = this.functionArguments.get(0).toString() + this.functionArguments.get(1).toString() + this.functionArguments.get(2).toString() + this.functionArguments.get(3).toString();
               Vector newArgument = new Vector();
               newArgument.add(newArgString);
               this.setFunctionArguments(newArgument);
            }
         } else {
            forDatetimeToBeChanged = new SelectColumn();
            Vector newArgs = new Vector();
            Vector scArgs = new Vector();
            subFunction.setFunctionArguments(arguments);
            scArgs.add(subFunction);
            forDatetimeToBeChanged.setColumnExpression(scArgs);
            newArgs.add(forDatetimeToBeChanged);
            newArgs.add("'00:00:00'");
            this.setFunctionArguments(newArgs);
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
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn) {
         this.functionName.setColumnName("CONCAT");
         SelectColumn forDatetimeToBeChanged = (SelectColumn)this.functionArguments.get(0);
         SelectColumn sc = (SelectColumn)this.functionArguments.get(1);
         String datetimeToBeChangedString = "";
         if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof TableColumn) {
            new TableColumn();
            TableColumn tc = (TableColumn)forDatetimeToBeChanged.getColumnExpression().get(0);
            datetimeToBeChangedString = tc.getColumnName();
         } else if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof SelectColumn) {
            datetimeToBeChangedString = forDatetimeToBeChanged.getColumnExpression().get(0).toString();
         } else {
            datetimeToBeChangedString = (String)forDatetimeToBeChanged.getColumnExpression().get(0);
         }

         new Vector();
         if (sc.getColumnExpression().get(0) instanceof String) {
            String format = (String)sc.getColumnExpression().get(0);
            if (format.toUpperCase().indexOf("MONTH") != -1) {
               throw new ConvertException("MONTH in TO_DATE() is not supported");
            }

            if (format.toUpperCase().indexOf("Y") != -1) {
               if (format.toUpperCase().indexOf("YY") != -1) {
                  if (format.toUpperCase().indexOf("YYY") != -1) {
                     if (format.toUpperCase().indexOf("YYYY") != -1) {
                        this.functionArguments.setElementAt("SUBSTRING(" + datetimeToBeChangedString + ", " + format.toUpperCase().indexOf("YYYY") + ",4) , '-' ", 0);
                     } else {
                        this.functionArguments.setElementAt("YEAR(CURRENT_DATE) - MOD(YEAR(CURRENT_DATE),1000) + SUBSTRING(" + datetimeToBeChangedString + ", " + format.toUpperCase().indexOf("YYY") + ",3) , '-' ", 0);
                     }
                  } else {
                     this.functionArguments.setElementAt("YEAR(CURRENT_DATE) - MOD(YEAR(CURRENT_DATE),100) + SUBSTRING(" + datetimeToBeChangedString + ", " + format.toUpperCase().indexOf("YYY") + ",3) , '-' ", 0);
                  }
               } else {
                  this.functionArguments.setElementAt("YEAR(CURRENT_DATE) - MOD(YEAR(CURRENT_DATE),10) + SUBSTRING(" + datetimeToBeChangedString + ", " + format.toUpperCase().indexOf("YYY") + ",3) , '-' ", 0);
               }
            } else {
               this.functionArguments.setElementAt("YEAR(CURRENT_DATE) , '-' ", 0);
            }

            if (format.toUpperCase().indexOf("MM") != -1) {
               this.functionArguments.setElementAt("SUBSTRING(" + datetimeToBeChangedString + ", " + format.toUpperCase().indexOf("MM") + ",2) , '-' ", 1);
            } else {
               this.functionArguments.setElementAt("MONTH(CURRENT_DATE) , '-' ", 1);
            }

            if (format.toUpperCase().indexOf("D") != -1) {
               if (format.toUpperCase().indexOf("DD") != -1) {
                  this.functionArguments.add("SUBSTRING(" + datetimeToBeChangedString + ", " + format.toUpperCase().indexOf("DD") + ",2)");
               } else {
                  this.functionArguments.add("01");
               }
            } else {
               this.functionArguments.add("DAY(CURRENT_DATE)");
            }

            this.setFunctionArguments(this.functionArguments);
         }
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String orgFnName = this.functionName.getColumnName();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            functionArgsInSingleQuotesToDouble = false;
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toANSISelect(to_sqs, from_sqs));
            functionArgsInSingleQuotesToDouble = true;
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      DateClass dc;
      if (arguments.size() == 1) {
         this.functionName.setColumnName("CAST");
         dc = new DateClass();
         if (orgFnName.equalsIgnoreCase("to_date")) {
            dc.setDatatypeName("DATE");
         } else if (orgFnName.equalsIgnoreCase("to_timestamp")) {
            dc.setDatatypeName("TIMESTAMP");
         }

         this.setAsDatatype("AS");
         arguments.add(dc);
         this.setFunctionArguments(arguments);
      } else if (arguments.size() == 2) {
         dc = null;
         String arg2 = null;
         if (arguments.get(0) instanceof SelectColumn && arguments.get(1) instanceof SelectColumn) {
            SelectColumn sc1 = (SelectColumn)arguments.get(0);
            SelectColumn sc2 = (SelectColumn)arguments.get(1);
            Vector colExpr1 = sc1.getColumnExpression();
            Vector colExpr2 = sc2.getColumnExpression();
            if (colExpr1.size() == 1 && colExpr2.size() == 1 && colExpr2.get(0) instanceof String) {
               arg2 = (String)colExpr2.get(0);
               if (arg2.indexOf(":") != -1) {
                  arg2 = "TIMESTAMP FORMAT " + arg2;
                  colExpr2.set(0, arg2);
                  this.functionName.setColumnName("CAST");
                  this.setAsDatatype("AS");
                  this.setFunctionArguments(arguments);
               } else {
                  arg2 = "DATE FORMAT " + arg2;
                  colExpr2.set(0, arg2);
                  this.functionName.setColumnName("CAST");
                  this.setAsDatatype("AS");
                  this.setFunctionArguments(arguments);
               }
            }
         } else {
            this.functionName.setColumnName("CAST");
            DateClass dc = new DateClass();
            this.setAsDatatype("AS");
            if (orgFnName.equalsIgnoreCase("to_date")) {
               dc.setDatatypeName("DATE");
            } else if (orgFnName.equalsIgnoreCase("to_timestamp")) {
               dc.setDatatypeName("TIMESTAMP");
            }

            this.setFunctionArguments(arguments);
         }
      }

   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      Object arg;
      String literalValue;
      String format;
      String separator;
      String yearpart;
      if (this.functionArguments.size() == 1) {
         arg = this.functionArguments.elementAt(0);
         if (arg.toString().trim().startsWith("'")) {
            literalValue = arg.toString().trim();
            format = SwisSQLUtils.getDateFormat(literalValue, 10);
            format = format.trim();
            if (format != null) {
               if (!format.equalsIgnoreCase("'DD-MON-YY'") && !format.equalsIgnoreCase("'DD/MON/YY'") && !format.equalsIgnoreCase("'DD:MON:YY'")) {
                  arguments.addElement(literalValue);
                  if (format.startsWith("'") && format.endsWith("'")) {
                     arguments.addElement(format);
                  } else {
                     arguments.addElement("'" + format + "'");
                  }
               } else {
                  separator = format.substring(3, 4);
                  yearpart = literalValue.substring(literalValue.lastIndexOf(separator) + 1, literalValue.length() - 1);
                  int yearpart_int = 0;

                  try {
                     yearpart_int = Integer.parseInt(yearpart);
                  } catch (Exception var11) {
                  }

                  if (yearpart_int < 50) {
                     if (yearpart_int < 10) {
                        yearpart = "200" + yearpart_int;
                     } else {
                        yearpart = "20" + yearpart_int;
                     }
                  } else {
                     yearpart = "19" + yearpart_int;
                  }

                  literalValue = literalValue.substring(0, literalValue.length() - 3) + yearpart + "'";
                  arguments.addElement(literalValue);
                  format = "DD" + separator + "MON" + separator + "YYYY";
                  arguments.addElement("'" + format + "'");
               }

               this.setFunctionArguments(arguments);
            }
         }
      } else {
         arg = this.functionArguments.elementAt(0);
         if (arg.toString().trim().startsWith("'")) {
            literalValue = arg.toString().trim();
            format = this.functionArguments.elementAt(1).toString().trim();
            if (format != null) {
               if (!format.equalsIgnoreCase("'DD-MON-YY'") && !format.equalsIgnoreCase("'DD/MON/YY'") && !format.equalsIgnoreCase("'DD:MON:YY'")) {
                  if (!format.equalsIgnoreCase("'MM-DD-YY'") && !format.equalsIgnoreCase("'MM/DD/YY'") && !format.equalsIgnoreCase("'MM:DD:YY'")) {
                     arguments.addElement(literalValue);
                     if (format.startsWith("'") && format.endsWith("'")) {
                        arguments.addElement(format);
                     } else {
                        arguments.addElement("'" + format + "'");
                     }
                  } else {
                     separator = format.substring(3, 4);
                     yearpart = literalValue.substring(literalValue.lastIndexOf(separator) + 1, literalValue.length() - 1);
                     yearpart = "20" + yearpart;
                     literalValue = literalValue.substring(0, literalValue.length() - 3) + yearpart + "'";
                     arguments.addElement(literalValue);
                     format = "MM" + separator + "DD" + separator + "YYYY";
                     arguments.addElement("'" + format + "'");
                  }
               } else {
                  separator = format.substring(3, 4);
                  yearpart = literalValue.substring(literalValue.lastIndexOf(separator) + 1, literalValue.length() - 1);
                  yearpart = "20" + yearpart;
                  literalValue = literalValue.substring(0, literalValue.length() - 3) + yearpart + "'";
                  arguments.addElement(literalValue);
                  format = "DD" + separator + "MON" + separator + "YYYY";
                  arguments.addElement("'" + format + "'");
               }

               this.setFunctionArguments(arguments);
            }
         }
      }

   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String orgFnName = this.functionName.getColumnName();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      DateClass dc;
      if (arguments.size() == 1) {
         this.functionName.setColumnName("CAST");
         dc = new DateClass();
         if (orgFnName.equalsIgnoreCase("to_date")) {
            dc.setDatatypeName("DATE");
         } else if (orgFnName.equalsIgnoreCase("to_timestamp")) {
            dc.setDatatypeName("TIMESTAMP");
         }

         this.setAsDatatype("AS");
         arguments.add(dc);
      } else if (arguments.size() == 2) {
         dc = null;
         String arg2 = null;
         if (arguments.get(0) instanceof SelectColumn && arguments.get(1) instanceof SelectColumn) {
            SelectColumn sc1 = (SelectColumn)arguments.get(0);
            SelectColumn sc2 = (SelectColumn)arguments.get(1);
            Vector colExpr1 = sc1.getColumnExpression();
            Vector colExpr2 = sc2.getColumnExpression();
            if (colExpr1.size() == 1 && colExpr2.size() == 1 && colExpr2.get(0) instanceof String) {
               arg2 = colExpr2.get(0).toString().toUpperCase();
               if (arg2.indexOf(":") != -1 && arg2.startsWith("'") && arg2.indexOf(".") != -1) {
                  arg2 = arg2.replaceAll("\\.", " ");
                  colExpr2.set(0, arg2);
               }
            }
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String orgFnName = this.functionName.getColumnName();

      boolean isAM;
      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i);
            isAM = false;
            if (orgFnName.equalsIgnoreCase("to_timestamp_tz") && sc.getColumnExpression().firstElement() instanceof FunctionCalls && ((FunctionCalls)sc.getColumnExpression().firstElement()).getFunctionName().getColumnName().equalsIgnoreCase("to_char")) {
               isAM = true;
            }

            sc = sc.toTeradataSelect(to_sqs, from_sqs);
            if (isAM) {
               Object toCharFn = ((FunctionCalls)sc.getColumnExpression().firstElement()).getFunctionArguments().get(0);
               sc.getColumnExpression().setElementAt(toCharFn, 0);
            }

            arguments.addElement(sc);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (arguments.size() == 3) {
         arguments.removeElementAt(2);
      }

      if (arguments.size() == 1) {
         this.functionName.setColumnName("CAST");
         DateClass dc = new DateClass();
         if (orgFnName.equalsIgnoreCase("to_date")) {
            if (SwisSQLOptions.convert_Oracle_TO_DATE_To_Timestamp) {
               dc.setDatatypeName("TIMESTAMP");
            } else {
               dc.setDatatypeName("DATE");
            }
         } else if (orgFnName.equalsIgnoreCase("to_timestamp")) {
            dc.setDatatypeName("TIMESTAMP");
         }

         this.setAsDatatype("AS");
         arguments.add(dc);
         this.setFunctionArguments(arguments);
      } else if (arguments.size() == 2) {
         boolean threeLetterMonthFormat = false;
         boolean monthExpanded = false;
         isAM = false;
         boolean isPM = false;
         boolean ishyphen = false;
         String arg1 = null;
         String arg2 = null;
         if (arguments.get(0) instanceof SelectColumn && arguments.get(1) instanceof SelectColumn) {
            SelectColumn sc1 = (SelectColumn)arguments.get(0);
            SelectColumn sc2 = (SelectColumn)arguments.get(1);
            Vector colExpr1 = sc1.getColumnExpression();
            Vector colExpr2 = sc2.getColumnExpression();
            String[] dateParts = new String[3];
            String monthPart = "";
            String secondsPart = "";
            boolean splitDate = false;
            if (colExpr1.size() > 0 && colExpr2.size() == 1 && colExpr2.get(0) instanceof String) {
               arg2 = colExpr2.get(0).toString().toUpperCase();
               if (colExpr1.size() == 1) {
                  arg1 = colExpr1.get(0).toString();
               } else {
                  arg1 = "";

                  for(int k = 0; k < colExpr1.size(); ++k) {
                     arg1 = arg1 + colExpr1.elementAt(k).toString() + " ";
                  }
               }

               if (colExpr1.get(0) instanceof String) {
                  if (arg1.length() < arg2.length() && arg1.startsWith("'") && arg2.startsWith("'")) {
                     arg1 = this.reformatHardCodedDate(arg1.trim(), arg2.trim());
                     if (colExpr1.size() == 1) {
                        colExpr1.setElementAt(arg1, 0);
                     }
                  } else if (arg2.startsWith("'") && !arg1.startsWith("'") && arg1.length() + 2 <= arg2.length()) {
                     arg1 = this.reformatHardCodedDate("'" + arg1.trim() + "'", arg2.trim());
                     if (colExpr1.size() == 1) {
                        colExpr1.setElementAt(arg1, 0);
                     }
                  }

                  String splitWhere = "";
                  if (arg1.lastIndexOf("-") > 1 && this.count(arg1, "-") > 1) {
                     splitWhere = "-";
                     ishyphen = true;
                     splitDate = true;
                  } else if (arg1.lastIndexOf("/") > 1 && this.count(arg1, "/") > 1) {
                     splitWhere = "/";
                     splitDate = true;
                  } else {
                     splitDate = false;
                  }

                  if (splitDate) {
                     dateParts = arg1.split(splitWhere);
                     monthPart = dateParts[1];
                     secondsPart = dateParts[2];
                  }

                  if (monthPart.trim().length() == 3) {
                     threeLetterMonthFormat = true;
                  } else if (monthPart.trim().length() > 3) {
                     monthExpanded = true;
                  }

                  if (secondsPart.trim().toUpperCase().indexOf("AM") != -1) {
                     isAM = true;
                  } else if (secondsPart.trim().toUpperCase().indexOf("PM") != -1) {
                     isPM = true;
                  }
               }

               if (arg2.startsWith("'")) {
                  if (threeLetterMonthFormat) {
                     if (arg2.trim().toUpperCase().indexOf("MON") != -1) {
                        arg2 = arg2.replaceAll("MON", "MMM");
                     } else if (arg2.trim().toUpperCase().indexOf("MM") != -1) {
                        arg2 = arg2.replaceAll("MM", "MMM");
                     }
                  }

                  if (monthExpanded) {
                     if (arg2.trim().toUpperCase().indexOf("MONTH") != -1) {
                        arg2 = arg2.replaceAll("MONTH", "MMMM");
                     } else if (arg2.trim().toUpperCase().indexOf("MON") != -1) {
                        arg2 = arg2.replaceAll("MON", "MMMM");
                     } else if (arg2.trim().toUpperCase().indexOf("MM") != -1) {
                        arg2 = arg2.replaceAll("MM", "MMMM");
                     }
                  }

                  if (arg2.indexOf("HH24") != -1) {
                     arg2 = arg2.replaceAll("HH24", "HH");
                  } else if (arg2.indexOf("HH12") != -1) {
                     arg2 = arg2.replaceAll("HH12", "HH");
                  }

                  if (isAM || isPM) {
                     arg2 = arg2.replaceAll("SS", "SSBT");
                     if (arg2.indexOf("AM") != -1) {
                        arg2 = arg2.replaceAll("AM", "");
                     } else if (arg2.indexOf("PM") != -1) {
                        arg2 = arg2.replaceAll("PM", "");
                     }
                  }

                  if (arg2.indexOf(" ") != -1) {
                     arg2 = arg2.replaceAll(" ", "B");
                  } else if (arg2.indexOf(".") != -1) {
                     arg2 = arg2.replaceAll("\\.", "B");
                  }

                  if (arg2.indexOf("TZR") != -1) {
                     arg2 = arg2.replaceAll("TZR", "");
                  }

                  if (arg2.indexOf("MONTH") != -1) {
                     arg2 = arg2.replaceAll("MONTH", "MMMM");
                  } else if (arg2.indexOf("MON") != -1) {
                     arg2 = arg2.replaceAll("MON", "MMM");
                  }

                  if (arg2.indexOf("RR") != -1) {
                     arg2 = arg2.replaceAll("RR", "YY");
                  }
               }

               DateClass timestampType;
               if (arg2.indexOf(":") == -1 && (!arg1.startsWith("'") || !arg1.endsWith("'") || arg1.indexOf(":") == -1) && arg2.toUpperCase().indexOf("HH") == -1) {
                  timestampType = new DateClass();
                  timestampType.setDatatypeName("DATE");
                  arg2 = " FORMAT " + arg2;
                  colExpr2.set(0, arg2);
                  colExpr2.insertElementAt(timestampType, 0);
                  if (!this.functionName.getColumnName().trim().equalsIgnoreCase("to_timestamp_tz")) {
                     this.functionName.setColumnName("CAST");
                  }

                  this.setAsDatatype("AS");
                  this.setFunctionArguments(arguments);
               } else {
                  timestampType = new DateClass();
                  timestampType.setDatatypeName("TIMESTAMP");
                  timestampType.setOpenBrace("(");
                  timestampType.setClosedBrace(")");
                  timestampType.setSize("0");
                  arg2 = " FORMAT " + arg2;
                  colExpr2.set(0, arg2);
                  colExpr2.insertElementAt(timestampType, 0);
                  if (!this.functionName.getColumnName().trim().equalsIgnoreCase("to_timestamp_tz")) {
                     this.functionName.setColumnName("CAST");
                  }

                  this.setAsDatatype("AS");
                  this.setFunctionArguments(arguments);
               }
            }
         } else {
            this.functionName.setColumnName("CAST");
            DateClass dc = new DateClass();
            this.setAsDatatype("AS");
            if (orgFnName.equalsIgnoreCase("to_date")) {
               if (SwisSQLOptions.convert_Oracle_TO_DATE_To_Timestamp) {
                  dc.setDatatypeName("TIMESTAMP");
               } else {
                  dc.setDatatypeName("DATE");
               }
            } else if (orgFnName.equalsIgnoreCase("to_timestamp")) {
               dc.setDatatypeName("TIMESTAMP");
            }

            arguments.add(dc);
            this.setFunctionArguments(arguments);
         }
      }

   }

   private int count(String str, String sub) {
      int cnt = 0;
      int i = 1;

      for(int len = str.length(); i < len; ++i) {
         if (str.charAt(i) == sub.charAt(0)) {
            ++cnt;
         }
      }

      return cnt;
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

   private boolean ramcoSpecificDateFormatHandling(Vector functionArguments) {
      if (functionArguments.get(0) instanceof SelectColumn) {
         Vector colExp = ((SelectColumn)functionArguments.get(0)).getColumnExpression();
         if (colExp != null && colExp.size() > 4 && colExp.get(1) instanceof String && ((String)colExp.get(1)).trim().equals("+") && colExp.get(2) instanceof String && ((String)colExp.get(2)).startsWith("'") && ((String)colExp.get(2)).endsWith("'") && colExp.get(3) instanceof String && ((String)colExp.get(3)).trim().equals("+") && colExp.get(4) instanceof String && ((String)colExp.get(4)).startsWith("@")) {
            return true;
         }
      }

      return false;
   }

   private String reformatHardCodedDate(String dateArg, String dateFormat) {
      StringBuffer newDateArg = new StringBuffer();
      if (dateArg.startsWith("'")) {
         dateArg = dateArg.substring(1, dateArg.length() - 1);
      }

      if (dateFormat.startsWith("'")) {
         dateFormat = dateFormat.substring(1, dateFormat.length() - 1);
      }

      dateFormat = dateFormat.replaceAll("HH24", "HH");
      if (dateFormat.indexOf("HH12") != -1) {
         dateFormat = dateFormat.replaceAll("HH12", "HH");
      }

      String firstPart = "";
      String secondPart = "";
      String thirdPart = "";
      boolean splitDate = false;
      String splitWhere = "";
      String spaceOrDot = "";
      if (dateFormat.lastIndexOf("-") > 1) {
         splitWhere = "-";
         splitDate = true;
      } else if (dateFormat.lastIndexOf("/") > 1) {
         splitWhere = "/";
         splitDate = true;
      } else {
         splitDate = false;
      }

      String[] dateArgParts;
      if (splitDate) {
         dateArgParts = dateFormat.split(splitWhere);

         for(int k = 0; k < dateArgParts.length; ++k) {
            if (k == 0) {
               firstPart = dateArgParts[0];
            }

            if (k == 1) {
               secondPart = dateArgParts[1];
            }

            if (k == 2) {
               thirdPart = dateArgParts[2];
            }
         }
      }

      if (dateArg.indexOf(splitWhere) == -1) {
         newDateArg.append(dateArg.substring(0, firstPart.length()) + splitWhere);
         newDateArg.append(dateArg.substring(firstPart.length(), firstPart.length() + secondPart.length()) + splitWhere);
         newDateArg.append(dateArg.substring(firstPart.length() + secondPart.length()));
      } else if (splitWhere != "") {
         dateArgParts = dateArg.split(splitWhere);
         String dateArgFirst = "";
         String dateArgSecond = "";
         String dateArgThird = "";

         int diff;
         for(diff = 0; diff < dateArgParts.length; ++diff) {
            if (diff == 0) {
               dateArgFirst = dateArgParts[0];
            }

            if (diff == 1) {
               dateArgSecond = dateArgParts[1];
            }

            if (diff == 2) {
               dateArgThird = dateArgParts[2];
            }
         }

         int thirdArgLength;
         if (dateArgFirst != "" && dateArgFirst.length() < firstPart.length() && firstPart.indexOf("Y") == -1 && Character.isDigit(dateArgFirst.charAt(0))) {
            diff = firstPart.length() - dateArgFirst.length();

            for(thirdArgLength = 0; thirdArgLength < diff; ++thirdArgLength) {
               dateArgFirst = 0 + dateArgFirst;
            }
         }

         if (dateArgSecond != "" && dateArgSecond.length() < secondPart.length() && secondPart.indexOf("Y") == -1 && Character.isDigit(dateArgSecond.charAt(0))) {
            diff = secondPart.length() - dateArgSecond.length();

            for(thirdArgLength = 0; thirdArgLength < diff; ++thirdArgLength) {
               dateArgSecond = 0 + dateArgSecond;
            }
         }

         if (dateArgThird != "" && dateArgThird.length() < thirdPart.length() && thirdPart.indexOf("Y") == -1 && Character.isDigit(dateArgThird.charAt(0))) {
            diff = thirdPart.length() - dateArgThird.length();
            thirdArgLength = dateArgThird.length();

            for(int k = 0; k < diff; ++k) {
               if (thirdPart.charAt(k + thirdArgLength) == 'D') {
                  dateArgThird = 0 + dateArgThird;
               } else if (thirdPart.charAt(k + thirdArgLength) != 'H' && thirdPart.charAt(k + thirdArgLength) != 'S') {
                  if (thirdPart.charAt(k + thirdArgLength) == 'M') {
                     if (thirdPart.charAt(k + thirdArgLength + 1) == 'I') {
                        dateArgThird = dateArgThird + "00";
                        ++k;
                     } else {
                        dateArgThird = 0 + dateArgThird;
                     }
                  } else {
                     dateArgThird = dateArgThird + thirdPart.charAt(k + thirdArgLength);
                  }
               } else {
                  dateArgThird = dateArgThird + 0;
               }
            }
         }

         newDateArg.append(dateArgFirst + splitWhere);
         newDateArg.append(dateArgSecond + splitWhere);
         newDateArg.append(dateArgThird);
      } else {
         newDateArg.append(dateArg);
      }

      return "'" + newDateArg.toString() + "'";
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
   }
}
