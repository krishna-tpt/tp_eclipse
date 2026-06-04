package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class datepart extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      Boolean isDenodoDb = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (isDenodoDb) {
         this.toDenodoDatePartFunConversion();
      } else {
         Vector colExpr;
         Vector colExpr1;
         Vector newArgument;
         Vector fnArgs;
         SelectColumn newSC;
         Vector functionArguments;
         if (arguments.size() == 2 && arguments.get(0) instanceof SelectColumn && ((SelectColumn)arguments.get(0)).getColumnExpression() != null && ((SelectColumn)arguments.get(0)).getColumnExpression().size() > 0 && ((SelectColumn)arguments.get(0)).getColumnExpression().get(0) instanceof TableColumn) {
            TableColumn tc = (TableColumn)((SelectColumn)arguments.get(0)).getColumnExpression().get(0);
            if (tc.getTableName() == null && tc.getColumnName() != null) {
               colExpr = new Vector();
               boolean ms = false;
               FunctionCalls subFunction;
               TableColumn subTC;
               SelectColumn sc;
               if (!tc.getColumnName().equalsIgnoreCase("MONTH") && !tc.getColumnName().equalsIgnoreCase("MM") && !tc.getColumnName().equalsIgnoreCase("M")) {
                  if (!tc.getColumnName().equalsIgnoreCase("YEAR") && !tc.getColumnName().equalsIgnoreCase("YYYY") && !tc.getColumnName().equalsIgnoreCase("YY")) {
                     if (!tc.getColumnName().equalsIgnoreCase("QUARTER") && !tc.getColumnName().equalsIgnoreCase("QQ") && !tc.getColumnName().equalsIgnoreCase("Q")) {
                        if (!tc.getColumnName().equalsIgnoreCase("DAY") && !tc.getColumnName().equalsIgnoreCase("DD") && !tc.getColumnName().equalsIgnoreCase("D")) {
                           if (!tc.getColumnName().equalsIgnoreCase("DAYOFYEAR") && !tc.getColumnName().equalsIgnoreCase("DY") && !tc.getColumnName().equalsIgnoreCase("Y")) {
                              if (!tc.getColumnName().equalsIgnoreCase("WEEK") && !tc.getColumnName().equalsIgnoreCase("WK") && !tc.getColumnName().equalsIgnoreCase("WW")) {
                                 if (!tc.getColumnName().equalsIgnoreCase("DW") && !tc.getColumnName().equalsIgnoreCase("WEEKDAY")) {
                                    if (!tc.getColumnName().equalsIgnoreCase("HOUR") && !tc.getColumnName().equalsIgnoreCase("HH")) {
                                       if (!tc.getColumnName().equalsIgnoreCase("MI") && !tc.getColumnName().equalsIgnoreCase("N") && !tc.getColumnName().equalsIgnoreCase("MINUTE")) {
                                          if (!tc.getColumnName().equalsIgnoreCase("SS") && !tc.getColumnName().equalsIgnoreCase("S") && !tc.getColumnName().equalsIgnoreCase("SECOND") && !tc.getColumnName().equalsIgnoreCase("SECONDS")) {
                                             if (tc.getColumnName().equalsIgnoreCase("MS") || tc.getColumnName().equalsIgnoreCase("MILLISECOND")) {
                                                this.functionName.setColumnName("TO_NUMBER");
                                                subFunction = new FunctionCalls();
                                                subTC = new TableColumn();
                                                subTC.setColumnName("TO_CHAR");
                                                subFunction.setFunctionName(subTC);
                                                colExpr1 = new Vector();
                                                sc = new SelectColumn();
                                                ms = true;
                                                colExpr.add("'FF'");
                                                subFunction.setFunctionArguments(colExpr);
                                                colExpr1.add(subFunction);
                                                sc.setColumnExpression(colExpr1);
                                                newArgument = new Vector();
                                                newArgument.add(sc);
                                                this.setFunctionArguments(newArgument);
                                                if (arguments.get(1).toString().trim().equalsIgnoreCase("(SYSDATE)")) {
                                                   arguments.set(1, "SYSTIMESTAMP");
                                                }
                                             }
                                          } else {
                                             this.functionName.setColumnName("TO_NUMBER");
                                             subFunction = new FunctionCalls();
                                             subTC = new TableColumn();
                                             subTC.setColumnName("TO_CHAR");
                                             subFunction.setFunctionName(subTC);
                                             colExpr1 = new Vector();
                                             sc = new SelectColumn();
                                             colExpr.add("'SS'");
                                             subFunction.setFunctionArguments(colExpr);
                                             colExpr1.add(subFunction);
                                             sc.setColumnExpression(colExpr1);
                                             newArgument = new Vector();
                                             newArgument.add(sc);
                                             this.setFunctionArguments(newArgument);
                                          }
                                       } else {
                                          this.functionName.setColumnName("TO_NUMBER");
                                          subFunction = new FunctionCalls();
                                          subTC = new TableColumn();
                                          subTC.setColumnName("TO_CHAR");
                                          subFunction.setFunctionName(subTC);
                                          colExpr1 = new Vector();
                                          sc = new SelectColumn();
                                          colExpr.add("'MI'");
                                          subFunction.setFunctionArguments(colExpr);
                                          colExpr1.add(subFunction);
                                          sc.setColumnExpression(colExpr1);
                                          newArgument = new Vector();
                                          newArgument.add(sc);
                                          this.setFunctionArguments(newArgument);
                                       }
                                    } else {
                                       this.functionName.setColumnName("TO_NUMBER");
                                       subFunction = new FunctionCalls();
                                       subTC = new TableColumn();
                                       subTC.setColumnName("TO_CHAR");
                                       subFunction.setFunctionName(subTC);
                                       colExpr1 = new Vector();
                                       sc = new SelectColumn();
                                       colExpr.add("'HH24'");
                                       subFunction.setFunctionArguments(colExpr);
                                       colExpr1.add(subFunction);
                                       sc.setColumnExpression(colExpr1);
                                       newArgument = new Vector();
                                       newArgument.add(sc);
                                       this.setFunctionArguments(newArgument);
                                    }
                                 } else {
                                    this.functionName.setColumnName("TO_NUMBER");
                                    subFunction = new FunctionCalls();
                                    subTC = new TableColumn();
                                    subTC.setColumnName("TO_CHAR");
                                    subFunction.setFunctionName(subTC);
                                    colExpr1 = new Vector();
                                    sc = new SelectColumn();
                                    colExpr.add("'D'");
                                    subFunction.setFunctionArguments(colExpr);
                                    colExpr1.add(subFunction);
                                    sc.setColumnExpression(colExpr1);
                                    newArgument = new Vector();
                                    newArgument.add(sc);
                                    this.setFunctionArguments(newArgument);
                                 }
                              } else {
                                 this.functionName.setColumnName("TO_NUMBER");
                                 subFunction = new FunctionCalls();
                                 subTC = new TableColumn();
                                 subTC.setColumnName("TO_CHAR");
                                 subFunction.setFunctionName(subTC);
                                 colExpr1 = new Vector();
                                 sc = new SelectColumn();
                                 colExpr.add("'WW'");
                                 subFunction.setFunctionArguments(colExpr);
                                 colExpr1.add(subFunction);
                                 sc.setColumnExpression(colExpr1);
                                 newArgument = new Vector();
                                 newArgument.add(sc);
                                 this.setFunctionArguments(newArgument);
                              }
                           } else {
                              this.functionName.setColumnName("TO_NUMBER");
                              subFunction = new FunctionCalls();
                              subTC = new TableColumn();
                              subTC.setColumnName("TO_CHAR");
                              subFunction.setFunctionName(subTC);
                              colExpr1 = new Vector();
                              sc = new SelectColumn();
                              colExpr.add("'DDD'");
                              subFunction.setFunctionArguments(colExpr);
                              colExpr1.add(subFunction);
                              sc.setColumnExpression(colExpr1);
                              newArgument = new Vector();
                              newArgument.add(sc);
                              this.setFunctionArguments(newArgument);
                           }
                        } else {
                           this.functionName.setColumnName("TO_NUMBER");
                           subFunction = new FunctionCalls();
                           subTC = new TableColumn();
                           subTC.setColumnName("TO_CHAR");
                           subFunction.setFunctionName(subTC);
                           colExpr1 = new Vector();
                           sc = new SelectColumn();
                           colExpr.add("'DD'");
                           subFunction.setFunctionArguments(colExpr);
                           colExpr1.add(subFunction);
                           sc.setColumnExpression(colExpr1);
                           newArgument = new Vector();
                           newArgument.add(sc);
                           this.setFunctionArguments(newArgument);
                        }
                     } else {
                        this.functionName.setColumnName("TO_NUMBER");
                        subFunction = new FunctionCalls();
                        subTC = new TableColumn();
                        subTC.setColumnName("TO_CHAR");
                        subFunction.setFunctionName(subTC);
                        colExpr1 = new Vector();
                        sc = new SelectColumn();
                        colExpr.add("'Q'");
                        subFunction.setFunctionArguments(colExpr);
                        colExpr1.add(subFunction);
                        sc.setColumnExpression(colExpr1);
                        newArgument = new Vector();
                        newArgument.add(sc);
                        this.setFunctionArguments(newArgument);
                     }
                  } else {
                     this.functionName.setColumnName("TO_NUMBER");
                     subFunction = new FunctionCalls();
                     subTC = new TableColumn();
                     subTC.setColumnName("TO_CHAR");
                     subFunction.setFunctionName(subTC);
                     colExpr1 = new Vector();
                     sc = new SelectColumn();
                     colExpr.add("'YYYY'");
                     subFunction.setFunctionArguments(colExpr);
                     colExpr1.add(subFunction);
                     sc.setColumnExpression(colExpr1);
                     newArgument = new Vector();
                     newArgument.add(sc);
                     this.setFunctionArguments(newArgument);
                  }
               } else {
                  this.functionName.setColumnName("TO_NUMBER");
                  subFunction = new FunctionCalls();
                  subTC = new TableColumn();
                  subTC.setColumnName("TO_CHAR");
                  subFunction.setFunctionName(subTC);
                  colExpr1 = new Vector();
                  sc = new SelectColumn();
                  colExpr.add("'MM'");
                  subFunction.setFunctionArguments(colExpr);
                  colExpr1.add(subFunction);
                  sc.setColumnExpression(colExpr1);
                  newArgument = new Vector();
                  newArgument.add(sc);
                  this.setFunctionArguments(newArgument);
               }

               if (arguments.get(1).toString().trim().startsWith("'")) {
                  Object obj = arguments.get(1);
                  String format = SwisSQLUtils.getDateFormat(obj.toString().trim(), 1);
                  newSC = new SelectColumn();
                  functionArguments = new Vector();
                  FunctionCalls fc = new FunctionCalls();
                  TableColumn fctc = new TableColumn();
                  if (ms) {
                     fctc.setColumnName("TO_TIMESTAMP");
                  } else {
                     fctc.setColumnName("TO_DATE");
                  }

                  fnArgs = new Vector();
                  fnArgs.add(obj);
                  fc.setFunctionArguments(fnArgs);
                  fc.setFunctionName(fctc);
                  functionArguments.add(fc);
                  newSC.setColumnExpression(functionArguments);
                  colExpr.add(0, newSC);
                  if (format != null) {
                     if (format.startsWith("'1900")) {
                        fnArgs.setElementAt(format, 0);
                        fnArgs.add("'YYYY-MM-DD HH24:MI:SS'");
                     } else {
                        fnArgs.add(format);
                     }
                  }
               } else {
                  colExpr.add(0, arguments.get(1));
               }
            }
         }

         String fname = this.functionName.getColumnName();
         SelectColumn sc;
         Vector subFunctionArgs1;
         if (fname.equalsIgnoreCase("monthname")) {
            this.functionName.setColumnName("TO_CHAR");
            colExpr = new Vector();
            sc = new SelectColumn();
            colExpr.add(arguments.get(0));
            sc.setColumnExpression(colExpr);
            SelectColumn sc1 = new SelectColumn();
            subFunctionArgs1 = new Vector();
            subFunctionArgs1.add("'FMMonth'");
            sc1.setColumnExpression(subFunctionArgs1);
            colExpr1 = new Vector();
            colExpr1.add(sc);
            colExpr1.add(sc1);
            this.setFunctionArguments(colExpr1);
         } else {
            FunctionCalls subFunction;
            TableColumn subTC;
            Vector scColumnExpression;
            if (fname.equalsIgnoreCase("julian_day")) {
               this.functionName.setColumnName("TO_NUMBER");
               subFunction = new FunctionCalls();
               subTC = new TableColumn();
               subTC.setColumnName("TO_CHAR");
               subFunction.setFunctionName(subTC);
               scColumnExpression = new Vector();
               subFunctionArgs1 = new Vector();
               newSC = new SelectColumn();
               scColumnExpression.add(arguments.get(0));
               scColumnExpression.add("'J'");
               subFunction.setFunctionArguments(scColumnExpression);
               subFunctionArgs1.add(subFunction);
               newSC.setColumnExpression(subFunctionArgs1);
               functionArguments = new Vector();
               functionArguments.add(newSC);
               this.setFunctionArguments(functionArguments);
            } else if (fname.equalsIgnoreCase("week_iso")) {
               this.functionName.setColumnName("TO_NUMBER");
               subFunction = new FunctionCalls();
               subTC = new TableColumn();
               subTC.setColumnName("TO_CHAR");
               subFunction.setFunctionName(subTC);
               scColumnExpression = new Vector();
               subFunctionArgs1 = new Vector();
               newSC = new SelectColumn();
               scColumnExpression.add(arguments.get(0));
               scColumnExpression.add("'IW'");
               subFunction.setFunctionArguments(scColumnExpression);
               subFunctionArgs1.add(subFunction);
               newSC.setColumnExpression(subFunctionArgs1);
               functionArguments = new Vector();
               functionArguments.add(newSC);
               this.setFunctionArguments(functionArguments);
            } else {
               String dateStringColumn;
               if (fname.equalsIgnoreCase("dayofweek")) {
                  this.functionName.setColumnName("TO_NUMBER");
                  subFunction = new FunctionCalls();
                  subTC = new TableColumn();
                  subTC.setColumnName("TO_CHAR");
                  subFunction.setFunctionName(subTC);
                  scColumnExpression = new Vector();
                  subFunctionArgs1 = new Vector();
                  newSC = new SelectColumn();
                  dateStringColumn = arguments.get(0).toString();
                  dateStringColumn = StringFunctions.handleLiteralStringDateForOracle(dateStringColumn);
                  scColumnExpression.add(dateStringColumn);
                  scColumnExpression.add("'D'");
                  subFunction.setFunctionArguments(scColumnExpression);
                  subFunctionArgs1.add(subFunction);
                  newSC.setColumnExpression(subFunctionArgs1);
                  newArgument = new Vector();
                  newArgument.add(newSC);
                  this.setFunctionArguments(newArgument);
               } else if (fname.equalsIgnoreCase("days")) {
                  this.functionName.setColumnName("");
                  this.setOpenBracesForFunctionNameRequired(true);
                  colExpr = new Vector();
                  FunctionCalls subFunction1 = new FunctionCalls();
                  TableColumn subTC1 = new TableColumn();
                  subTC1.setColumnName("TO_DATE");
                  subFunction1.setFunctionName(subTC1);
                  subFunctionArgs1 = new Vector();
                  subFunctionArgs1.add(arguments.get(0));
                  subFunction1.setFunctionArguments(subFunctionArgs1);
                  colExpr.add(subFunction1);
                  colExpr.add("-");
                  FunctionCalls subFunction2 = new FunctionCalls();
                  TableColumn subTC2 = new TableColumn();
                  subTC2.setColumnName("TO_DATE");
                  subFunction2.setFunctionName(subTC2);
                  newArgument = new Vector();
                  newArgument.add("'01010001'");
                  newArgument.add("'MMDDYYYY'");
                  subFunction2.setFunctionArguments(newArgument);
                  colExpr.add(subFunction2);
                  colExpr.add("-");
                  colExpr.add("1");
                  SelectColumn sc1 = new SelectColumn();
                  sc1.setColumnExpression(colExpr);
                  fnArgs = new Vector();
                  fnArgs.add(sc1);
                  this.setFunctionArguments(fnArgs);
               } else {
                  SelectColumn sc1;
                  if (fname.equalsIgnoreCase("DAYOFYEAR")) {
                     this.functionName.setColumnName("TO_NUMBER");
                     subFunction = new FunctionCalls();
                     subTC = new TableColumn();
                     subTC.setColumnName("TO_CHAR");
                     subFunction.setFunctionName(subTC);
                     scColumnExpression = new Vector();
                     sc1 = new SelectColumn();
                     colExpr1 = new Vector();
                     dateStringColumn = arguments.get(0).toString();
                     dateStringColumn = StringFunctions.handleLiteralStringDateForOracle(dateStringColumn);
                     colExpr1.add(dateStringColumn);
                     colExpr1.add("'DDD'");
                     subFunction.setFunctionArguments(colExpr1);
                     scColumnExpression.add(subFunction);
                     sc1.setColumnExpression(scColumnExpression);
                     newArgument = new Vector();
                     newArgument.add(sc1);
                     this.setFunctionArguments(newArgument);
                  } else if (fname.equalsIgnoreCase("DAYOFMONTH")) {
                     this.functionName.setColumnName("TO_NUMBER");
                     subFunction = new FunctionCalls();
                     subTC = new TableColumn();
                     subTC.setColumnName("TO_CHAR");
                     subFunction.setFunctionName(subTC);
                     scColumnExpression = new Vector();
                     sc1 = new SelectColumn();
                     colExpr1 = new Vector();
                     dateStringColumn = arguments.get(0).toString();
                     dateStringColumn = StringFunctions.handleLiteralStringDateForOracle(dateStringColumn);
                     colExpr1.add(dateStringColumn);
                     colExpr1.add("'DD'");
                     subFunction.setFunctionArguments(colExpr1);
                     scColumnExpression.add(subFunction);
                     sc1.setColumnExpression(scColumnExpression);
                     newArgument = new Vector();
                     newArgument.add(sc1);
                     this.setFunctionArguments(newArgument);
                  } else {
                     String dateString;
                     String query;
                     if (fname.equalsIgnoreCase("from_unixtime")) {
                        dateString = arguments.get(0).toString();
                        query = "cast((to_date('1970-01-01 ','YYYY-MM-DD ') + numtodsinterval(" + dateString + ",'SECOND')) as TIMESTAMP)";
                        this.functionName.setColumnName(query);
                        this.setOpenBracesForFunctionNameRequired(false);
                        this.functionArguments = new Vector();
                     } else if (fname.equalsIgnoreCase("HOUR")) {
                        this.functionName.setColumnName("TO_NUMBER");
                        subFunction = new FunctionCalls();
                        subTC = new TableColumn();
                        subTC.setColumnName("TO_CHAR");
                        subFunction.setFunctionName(subTC);
                        scColumnExpression = new Vector();
                        sc1 = new SelectColumn();
                        colExpr1 = new Vector();
                        dateStringColumn = arguments.get(0).toString();
                        dateStringColumn = StringFunctions.handleLiteralStringDateForOracle(dateStringColumn);
                        colExpr1.add(dateStringColumn);
                        colExpr1.add("'HH24'");
                        subFunction.setFunctionArguments(colExpr1);
                        scColumnExpression.add(subFunction);
                        sc1.setColumnExpression(scColumnExpression);
                        newArgument = new Vector();
                        newArgument.add(sc1);
                        this.setFunctionArguments(newArgument);
                     } else if (fname.equalsIgnoreCase("minute")) {
                        this.functionName.setColumnName("TO_NUMBER");
                        subFunction = new FunctionCalls();
                        subTC = new TableColumn();
                        subTC.setColumnName("TO_CHAR");
                        subFunction.setFunctionName(subTC);
                        scColumnExpression = new Vector();
                        sc1 = new SelectColumn();
                        colExpr1 = new Vector();
                        dateStringColumn = arguments.get(0).toString();
                        dateStringColumn = StringFunctions.handleLiteralStringDateForOracle(dateStringColumn);
                        colExpr1.add(dateStringColumn);
                        colExpr1.add("'MI'");
                        subFunction.setFunctionArguments(colExpr1);
                        scColumnExpression.add(subFunction);
                        sc1.setColumnExpression(scColumnExpression);
                        newArgument = new Vector();
                        newArgument.add(sc1);
                        this.setFunctionArguments(newArgument);
                     } else if (fname.equalsIgnoreCase("microsecond")) {
                        dateString = arguments.get(0).toString();
                        if (((SelectColumn)arguments.get(0)).getColumnExpression().get(0) instanceof String) {
                           dateString = "TO_TIMESTAMP(" + dateString + ",'YYYY-MM-DD HH24:MI:SS.FF')";
                        }

                        query = "TO_NUMBER(TO_CHAR( CAST(" + dateString + " AS TIMESTAMP),'FF6'))";
                        this.functionName.setColumnName(query);
                        this.setOpenBracesForFunctionNameRequired(false);
                        this.functionArguments = new Vector();
                     } else if (fname.equalsIgnoreCase("dayname")) {
                        this.functionName.setColumnName("TO_CHAR");
                        colExpr = new Vector();
                        sc = new SelectColumn();
                        String dateStringColumn = arguments.get(0).toString();
                        dateStringColumn = StringFunctions.handleLiteralStringDateForOracle(dateStringColumn);
                        colExpr.add(dateStringColumn);
                        sc.setColumnExpression(colExpr);
                        sc1 = new SelectColumn();
                        colExpr1 = new Vector();
                        colExpr1.add("'FMDay'");
                        sc1.setColumnExpression(colExpr1);
                        functionArguments = new Vector();
                        functionArguments.add(sc);
                        functionArguments.add(sc1);
                        this.setFunctionArguments(functionArguments);
                     }
                  }
               }
            }
         }

      }
   }

   private void toDenodoDatePartFunConversion() throws ConvertException {
      String fname = this.functionName.getColumnName();
      SelectColumn sc;
      Vector colExpr1;
      SelectColumn sc1;
      Vector colExpr;
      Vector functionArgs;
      if (fname.equalsIgnoreCase("monthname")) {
         this.functionName.setColumnName("FORMATDATE");
         sc = new SelectColumn();
         colExpr1 = new Vector();
         colExpr1.add("'MMMM'");
         sc.setColumnExpression(colExpr1);
         sc1 = new SelectColumn();
         colExpr = new Vector();
         colExpr.add("CAST(" + StringFunctions.handleLiteralStringDateForDenodo(this.functionArguments.get(0).toString()) + " AS DATE)");
         sc1.setColumnExpression(colExpr);
         functionArgs = new Vector();
         functionArgs.add(sc);
         functionArgs.add(sc1);
         this.setFunctionArguments(functionArgs);
      } else if (fname.equalsIgnoreCase("dayofweek")) {
         this.functionName.setColumnName("GETDAYOFWEEK");
         this.functionArguments.set(0, StringFunctions.handleLiteralStringDateForDenodo(this.functionArguments.get(0).toString()));
      } else if (fname.equalsIgnoreCase("DAYOFYEAR")) {
         this.functionName.setColumnName("GETDAYOFYEAR");
         this.functionArguments.set(0, StringFunctions.handleLiteralStringDateForDenodo(this.functionArguments.get(0).toString()));
      } else if (fname.equalsIgnoreCase("DAYOFMONTH")) {
         this.functionName.setColumnName("GETDAY");
         this.functionArguments.set(0, StringFunctions.handleLiteralStringDateForDenodo(this.functionArguments.get(0).toString()));
      } else if (fname.equalsIgnoreCase("HOUR")) {
         this.functionName.setColumnName("GETHOUR");
         this.functionArguments.set(0, StringFunctions.handleLiteralStringDateForDenodo(this.functionArguments.get(0).toString()));
      } else if (fname.equalsIgnoreCase("minute")) {
         this.functionName.setColumnName("GETMINUTE");
         this.functionArguments.set(0, StringFunctions.handleLiteralStringDateForDenodo(this.functionArguments.get(0).toString()));
      } else if (fname.equalsIgnoreCase("microsecond")) {
         String query = "CAST(FORMATDATE('SSSSSS'," + StringFunctions.handleLiteralStringDateForDenodo(this.functionArguments.get(0).toString()) + ") AS INT8)";
         this.functionName.setColumnName(query);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (fname.equalsIgnoreCase("week_iso")) {
         this.functionName.setColumnName("GETWEEK");
         sc = new SelectColumn();
         FunctionCalls subFunction = new FunctionCalls();
         TableColumn subTC = new TableColumn();
         subTC.setColumnName("TRUNC");
         subFunction.setFunctionName(subTC);
         colExpr = new Vector();
         functionArgs = new Vector();
         functionArgs.add(StringFunctions.handleLiteralStringDateForDenodo(this.functionArguments.get(0).toString()));
         functionArgs.add("'IW'");
         subFunction.setFunctionArguments(functionArgs);
         colExpr.add(subFunction);
         sc.setColumnExpression(colExpr);
         Vector newArgument = new Vector();
         newArgument.add(sc);
         this.setFunctionArguments(newArgument);
      } else if (fname.equalsIgnoreCase("dayname")) {
         this.functionName.setColumnName("FORMATDATE");
         sc = new SelectColumn();
         colExpr1 = new Vector();
         colExpr1.add("'EEEE'");
         sc.setColumnExpression(colExpr1);
         sc1 = new SelectColumn();
         colExpr = new Vector();
         colExpr.add("CAST(" + StringFunctions.handleLiteralStringDateForDenodo(this.functionArguments.get(0).toString()) + " AS DATE)");
         sc1.setColumnExpression(colExpr);
         functionArgs = new Vector();
         functionArgs.add(sc);
         functionArgs.add(sc1);
         this.setFunctionArguments(functionArgs);
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String oldFunname = this.functionName.getColumnName();
      if (this.functionName.getColumnName().equalsIgnoreCase("hour")) {
         if (from_sqs != null && from_sqs.isMSAzure()) {
            arguments.addElement("hh");
         } else {
            this.functionArguments.add(0, "hh");
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("monthname")) {
         this.functionName.setColumnName("DATENAME");
         arguments.addElement("MONTH");
      } else {
         if (from_sqs.isMSAzure()) {
            if (this.functionName.getColumnName().equalsIgnoreCase("dayofweek")) {
               arguments.addElement("dw");
            } else if (this.functionName.getColumnName().equalsIgnoreCase("dayofyear")) {
               arguments.addElement("dy");
            } else if (this.functionName.getColumnName().equalsIgnoreCase("microsecond")) {
               arguments.addElement("mcs");
            } else if (this.functionName.getColumnName().equalsIgnoreCase("second")) {
               arguments.addElement("s");
            }
         }

         this.functionName.setColumnName("DATEPART");
      }

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (oldFunname.equalsIgnoreCase("hour") && this.functionArguments.size() == 1) {
         arguments.setElementAt("CAST( " + arguments.get(1).toString() + " AS DATETIME)", 1);
      }

      String arg1 = this.functionArguments.get(0).toString();
      if (!arg1.equalsIgnoreCase("CALDAYOFWEEK") && !arg1.equalsIgnoreCase("CDW")) {
         if (!arg1.equalsIgnoreCase("CALWEEKOFYEAR") && !arg1.equalsIgnoreCase("CWK")) {
            if (arg1.equalsIgnoreCase("CALYEAROFWEEK") || arg1.equalsIgnoreCase("CYR")) {
               arguments.setElementAt("YY", 0);
            }
         } else {
            arguments.setElementAt("WK", 0);
         }
      } else {
         arguments.setElementAt("DW", 0);
      }

      this.setFunctionArguments(arguments);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATEPART");
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
      String oldname = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (!oldname.equalsIgnoreCase("Dayofweek") && !oldname.equalsIgnoreCase("Hour")) {
         this.functionName.setColumnName("DATEPART");
         if (arguments.size() == 2) {
            String arg1 = arguments.get(0).toString();
            arguments.remove(0);
            if (!arg1.equalsIgnoreCase("yy") && !arg1.equalsIgnoreCase("year")) {
               if (!arg1.equalsIgnoreCase("quarter") && !arg1.equalsIgnoreCase("qq")) {
                  if (!arg1.equalsIgnoreCase("dd") && !arg1.equalsIgnoreCase("day")) {
                     if (!arg1.equalsIgnoreCase("month") && !arg1.equalsIgnoreCase("mm")) {
                        if (!arg1.equalsIgnoreCase("week") && !arg1.equalsIgnoreCase("wk")) {
                           if (!arg1.equalsIgnoreCase("dayofyear") && !arg1.equalsIgnoreCase("dy")) {
                              if (!arg1.equalsIgnoreCase("weekday") && !arg1.equalsIgnoreCase("dw")) {
                                 if (!arg1.equalsIgnoreCase("hour") && !arg1.equalsIgnoreCase("hh")) {
                                    if (!arg1.equalsIgnoreCase("minute") && !arg1.equalsIgnoreCase("mi")) {
                                       if (arg1.equalsIgnoreCase("second") || arg1.equalsIgnoreCase("ss")) {
                                          this.functionName.setColumnName("SECOND");
                                       }
                                    } else {
                                       this.functionName.setColumnName("MINUTE");
                                    }
                                 } else {
                                    this.functionName.setColumnName("HOUR");
                                 }
                              } else {
                                 this.functionName.setColumnName("DAYOFWEEK");
                              }
                           } else {
                              this.functionName.setColumnName("DAYOFYEAR");
                           }
                        } else {
                           this.functionName.setColumnName("WEEK");
                        }
                     } else {
                        this.functionName.setColumnName("MONTH");
                     }
                  } else {
                     this.functionName.setColumnName("DAY");
                  }
               } else {
                  this.functionName.setColumnName("QUARTER");
               }
            } else {
               this.functionName.setColumnName("YEAR");
            }
         } else if (oldname.equalsIgnoreCase("Monthname")) {
            this.functionName.setColumnName("VARCHAR_FORMAT( " + arguments.get(0).toString() + " , 'Month')");
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
            return;
         }

         this.setFunctionArguments(arguments);
      } else {
         this.setFunctionArguments(arguments);
      }
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      boolean canUseUDFFunction = from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.date.time");

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            ((SelectColumn)this.functionArguments.elementAt(i_count)).modifyCurrentTimeAsCurrentTimestamp(from_sqs);
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            if (((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression() != null && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0) instanceof String) {
               if (this.functionName.getColumnName().equalsIgnoreCase("HOUR")) {
                  arguments.add(i_count, " CAST( " + this.handleStringLiteralForTime(arguments.get(i_count).toString(), from_sqs, true) + " AS TIME)");
               } else {
                  arguments.add(i_count, " CAST( " + this.handleStringLiteralForDateTime(arguments.get(i_count).toString(), from_sqs) + " AS TIMESTAMP)");
               }
            }
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("HOUR")) {
         qry = "cast(" + extract.applyDatePartOrExtractWrapper(from_sqs, "HOUR", arguments.get(0) + "") + " as int)";
         if (canUseUDFFunction) {
            qry = "HOUR(" + arguments.get(0).toString() + ")";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("DAYOFWEEK")) {
         qry = " cast( " + extract.applyDatePartOrExtractWrapper(from_sqs, "DOW", arguments.get(0) + "") + " + 1 as int)";
         if (canUseUDFFunction) {
            qry = "DAYOFWEEK(" + arguments.get(0).toString() + ")";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("MONTHNAME")) {
         qry = " to_char(DATE(" + arguments.get(0) + "), 'FMMonth') ";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean changeArgumentsForDatePart = false;
      String fnName = this.functionName.getColumnName().trim();
      if (fnName.equalsIgnoreCase("hour")) {
         this.functionName.setColumnName("hour");
      } else if (!fnName.equalsIgnoreCase("monthname") && !fnName.equalsIgnoreCase("month_name")) {
         if (!fnName.equalsIgnoreCase("dayofweek") && !fnName.equalsIgnoreCase("day_of_week")) {
            if (fnName.equalsIgnoreCase("DATEPART")) {
               changeArgumentsForDatePart = true;
            }

            this.functionName.setColumnName("DATEPART");
         } else {
            this.functionName.setColumnName("DAYOFWEEK");
         }
      } else {
         this.functionName.setColumnName("MONTHNAME");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else if (changeArgumentsForDatePart) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            Vector colExp = sc.getColumnExpression();
            if (colExp.get(0) instanceof TableColumn && this.functionArguments.size() == 2) {
               String datePart = ((TableColumn)colExp.get(0)).getColumnName().trim();
               if (!datePart.equalsIgnoreCase("weekday") && !datePart.equalsIgnoreCase("dw")) {
                  if (datePart.equalsIgnoreCase("month")) {
                     this.functionName.setColumnName("MONTH");
                  } else if (datePart.equalsIgnoreCase("day")) {
                     this.functionName.setColumnName("DAY");
                  } else if (datePart.equalsIgnoreCase("year")) {
                     this.functionName.setColumnName("YEAR");
                  } else if (datePart.equalsIgnoreCase("dayofyear")) {
                     this.functionName.setColumnName("DAYOFYEAR");
                  } else if (datePart.equalsIgnoreCase("hour")) {
                     this.functionName.setColumnName("HOUR");
                  } else if (datePart.equalsIgnoreCase("minute")) {
                     this.functionName.setColumnName("MINUTE");
                  }
               } else {
                  this.functionName.setColumnName("DAYOFWEEK");
               }

               changeArgumentsForDatePart = false;
            }
         } else {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         }
      }

      if (from_sqs != null && from_sqs.isHyperSql()) {
         arguments.set(0, StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString()));
      }

      this.setFunctionArguments(arguments);
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean changeArgumentsForDatePart = this.functionName.getColumnName().trim().equalsIgnoreCase("DATEPART");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else if (changeArgumentsForDatePart) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            Vector colExp = sc.getColumnExpression();
            if (colExp.get(0) instanceof TableColumn && this.functionArguments.size() == 2) {
               String datePart = ((TableColumn)colExp.get(0)).getColumnName().trim();
               if (!datePart.equalsIgnoreCase("weekday") && !datePart.equalsIgnoreCase("dw")) {
                  if (datePart.equalsIgnoreCase("month")) {
                     this.functionName.setColumnName("MONTH");
                  } else if (datePart.equalsIgnoreCase("day")) {
                     this.functionName.setColumnName("DAY");
                  } else if (datePart.equalsIgnoreCase("year")) {
                     this.functionName.setColumnName("YEAR");
                  } else if (datePart.equalsIgnoreCase("dayofyear")) {
                     this.functionName.setColumnName("DAY_OF_YEAR");
                  } else if (datePart.equalsIgnoreCase("hour")) {
                     this.functionName.setColumnName("HOUR");
                  } else if (datePart.equalsIgnoreCase("minute")) {
                     this.functionName.setColumnName("MINUTE");
                  }
               } else {
                  this.functionName.setColumnName("DAY_OF_WEEK");
               }

               changeArgumentsForDatePart = false;
            }
         } else {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         }
      }

      String fnName = this.functionName.getColumnName().trim();
      String dateString = StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString());
      if (!fnName.equalsIgnoreCase("monthname") && !fnName.equalsIgnoreCase("month_name")) {
         if (fnName.equalsIgnoreCase("hour")) {
            this.functionName.setColumnName("HOUR");
         } else if (fnName.equalsIgnoreCase("dayofweek") || fnName.equalsIgnoreCase("day_of_week")) {
            dateString = dateString + " + interval '1' day";
            this.functionName.setColumnName("DAY_OF_WEEK");
         }
      } else {
         arguments.addElement("'%M'");
         this.functionName.setColumnName("DATE_FORMAT");
      }

      arguments.setElementAt(dateString, 0);
      this.setFunctionArguments(arguments);
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

      String fnName = this.functionName.getColumnName().trim();
      String dateString = StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString());
      String qry = "";
      if (fnName.equalsIgnoreCase("monthname")) {
         qry = "LEFT(MONTHNAME(" + dateString + "),1) || LCASE(SUBSTR(MONTHNAME(" + dateString + "),2,LENGTH(" + dateString + ")))";
      } else if (fnName.equalsIgnoreCase("dayofweek")) {
         qry = "(MOD((WEEKDAY(" + dateString + ")+1),7)+1)";
      } else if (fnName.equalsIgnoreCase("hour")) {
         qry = "HOUR(" + dateString + ")";
      }

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

      String fnName = this.functionName.getColumnName().trim();
      String dateString = StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString());
      String qry = "";
      if (fnName.equalsIgnoreCase("monthname")) {
         qry = "(case cast(strftime('%m'," + dateString + ") as integer) when 1 then 'January' when 2 then 'February' when 3 then 'March' when 4 then 'April' when 5 then 'May' when 6 then 'June' when 7 then 'July' when 8 then 'August' when 9 then 'September' when 10 then 'October' when 11 then 'November' else 'December' end)";
      } else if (fnName.equalsIgnoreCase("dayofweek")) {
         qry = "(cast((strftime('%w'," + dateString + ")%7) as integer)+1)";
      } else if (fnName.equalsIgnoreCase("hour")) {
         qry = "cast(strftime('%H'," + dateString + ") as integer)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATEPART");
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

      String fnName = this.functionName.getColumnName().trim();
      String dateString = StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString());
      String qry = "";
      if (fnName.equalsIgnoreCase("monthname")) {
         qry = " TO_CHAR(" + dateString + ",'%B') ";
      } else if (fnName.equalsIgnoreCase("dayofweek")) {
         qry = " (WEEKDAY(" + dateString + ")+1) ";
      } else if (fnName.equalsIgnoreCase("hour")) {
         qry = " CAST(TO_CHAR(" + dateString + ",'%H') AS INT) ";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TO_CHAR");
      Vector arguments = new Vector();
      if (this.functionArguments.size() == 2) {
         if (this.functionArguments.get(1) instanceof SelectColumn) {
            arguments.add(((SelectColumn)this.functionArguments.get(1)).toTimesTenSelect(to_sqs, from_sqs));
         } else {
            arguments.add(this.functionArguments.get(1));
         }

         SelectColumn sc = (SelectColumn)this.functionArguments.get(0);
         String datePart = sc.getColumnExpression().get(0).toString();
         if (!datePart.equalsIgnoreCase("year") && !datePart.equalsIgnoreCase("yy")) {
            if (!datePart.equalsIgnoreCase("quarter") && !datePart.equalsIgnoreCase("qq")) {
               if (!datePart.equalsIgnoreCase("month") && !datePart.equalsIgnoreCase("mm")) {
                  if (!datePart.equalsIgnoreCase("day") && !datePart.equalsIgnoreCase("dd")) {
                     if (!datePart.equalsIgnoreCase("hour") && !datePart.equalsIgnoreCase("hh")) {
                        if (!datePart.equalsIgnoreCase("minute") && !datePart.equalsIgnoreCase("mi")) {
                           if (datePart.equalsIgnoreCase("second") || datePart.equalsIgnoreCase("ss")) {
                              arguments.add("'SS'");
                           }
                        } else {
                           arguments.add("'MI'");
                        }
                     } else {
                        arguments.add("'HH24'");
                     }
                  } else {
                     arguments.add("'DD'");
                  }
               } else {
                  arguments.add("'MM'");
               }
            } else {
               arguments.add("'Q'");
            }
         } else {
            arguments.add("'YYYY'");
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATEPART");
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
      this.functionName.setColumnName("DATEPART");
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
      Vector arguments = new Vector();
      String funName = this.functionName.getColumnName().trim();
      if (funName.equalsIgnoreCase("hour")) {
         this.functionName.setColumnName("HOUR");
      } else if (!funName.equalsIgnoreCase("monthname")) {
         if (funName.equalsIgnoreCase("QUATER")) {
            this.functionName.setColumnName("QUATER");
         } else if (funName.equalsIgnoreCase("dayofweek")) {
            this.functionName.setColumnName("DAYOFWEEK");
         } else if (funName.equalsIgnoreCase("dow")) {
            this.functionName.setColumnName("DAYOFWEEK");
         } else if (funName.equalsIgnoreCase("second")) {
            this.functionName.setColumnName("SECOND");
         } else if (funName.equalsIgnoreCase("MINUTE")) {
            this.functionName.setColumnName("MINUTE");
         } else if (funName.equalsIgnoreCase("microsecond")) {
            this.functionName.setColumnName("MICROSECOND");
         } else if (funName.equalsIgnoreCase("dayofyear")) {
            this.functionName.setColumnName("dayofyear");
         } else if (funName.equalsIgnoreCase("day")) {
            this.functionName.setColumnName("DAY");
         } else if (funName.equalsIgnoreCase("month")) {
            this.functionName.setColumnName("MONTH");
         } else {
            if (!funName.equalsIgnoreCase("year")) {
               throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
            }

            this.functionName.setColumnName("YEAR");
         }
      }

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            if (i_count == 0) {
               if (!funName.equalsIgnoreCase("HOUR") && !funName.equalsIgnoreCase("MINUTE") && !funName.equalsIgnoreCase("SECOND") && !funName.equalsIgnoreCase("MICROSECOND")) {
                  this.handleStringLiteralForDateTime(from_sqs, i_count, true);
               } else {
                  this.handleStringLiteralForTime(from_sqs, i_count, true, true);
               }
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         }
      }

      if (funName.trim().equalsIgnoreCase("monthname")) {
         arguments.addElement("'%M'");
         this.functionName.setColumnName("DATE_FORMAT");
      }

      this.setFunctionArguments(arguments);
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
      if (this.functionName.getColumnName().equalsIgnoreCase("HOUR")) {
         qry = "EXTRACT (HOUR FROM  (CAST(" + arguments.get(0) + " AS TIMESTAMP)))";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("DAYOFWEEK")) {
         qry = "(CAST( FORMAT_TIMESTAMP( '%w', CAST( " + arguments.get(0) + " AS TIMESTAMP)) AS INT64) + 1)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("MONTHNAME")) {
         qry = "FORMAT_TIMESTAMP('%B',CAST(" + arguments.get(0).toString() + " AS TIMESTAMP))";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String funName = this.functionName.getColumnName().trim();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (funName.equalsIgnoreCase("DAYOFWEEK")) {
         qry = "(DAYOFWEEK(" + arguments.get(0).toString() + " ) +1)";
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionName.setColumnName(qry);
      } else if (funName.equalsIgnoreCase("MONTHNAME")) {
         this.functionName.setColumnName("TO_CHAR(CAST(" + arguments.get(0).toString() + " AS DATETIME),'MMMM')");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("HOUR")) {
         qry = "HOUR(CAST(" + arguments.get(0).toString() + "AS DATETIME))";
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionName.setColumnName(qry);
      } else {
         this.setFunctionArguments(arguments);
      }
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

      String fnName = this.functionName.getColumnName().trim();
      String qry = "";
      if (fnName.equalsIgnoreCase("monthname")) {
         qry = "MONTHNAME(MONTH(" + arguments.get(0).toString() + "))";
      } else if (fnName.equalsIgnoreCase("dayofweek")) {
         qry = "WEEKDAY(" + arguments.get(0).toString() + ")";
      } else if (fnName.equalsIgnoreCase("hour")) {
         qry = "HOUR(" + arguments.get(0).toString() + ")";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String fnName = this.functionName.getColumnName().trim();
      String qry = "";
      String dateString = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
      if (fnName.equalsIgnoreCase("monthname")) {
         qry = "MONTHNAME(MONTH(" + dateString + "))";
      } else if (!fnName.equalsIgnoreCase("dayofweek") && !fnName.equalsIgnoreCase("weekday")) {
         if (fnName.equalsIgnoreCase("hour")) {
            qry = "HOUR(" + dateString + ")";
         }
      } else {
         qry = "DAYOFWEEK(" + dateString + ")";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }
}
