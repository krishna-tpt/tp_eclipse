package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class addToDate extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      String addStr = "";
      String funName = this.functionName.getColumnName();
      if (funName.equalsIgnoreCase("ADD_DATE")) {
         addStr = "DAY";
      } else if (!funName.equalsIgnoreCase("ADDYEAR") && !funName.equalsIgnoreCase("ADD_YEAR")) {
         if (!funName.equalsIgnoreCase("ADDMONTH") && !funName.equalsIgnoreCase("ADD_MONTH")) {
            if (!funName.equalsIgnoreCase("ADDWEEK") && !funName.equalsIgnoreCase("ADD_WEEK")) {
               if (!funName.equalsIgnoreCase("ADDQUARTER") && !funName.equalsIgnoreCase("ADD_QUARTER")) {
                  if (!funName.equalsIgnoreCase("ADDHOUR") && !funName.equalsIgnoreCase("ADD_HOUR")) {
                     if (!funName.equalsIgnoreCase("ADDMINUTE") && !funName.equalsIgnoreCase("ADD_MINUTE")) {
                        if (funName.equalsIgnoreCase("ADDSECOND") || funName.equalsIgnoreCase("ADD_SECOND")) {
                           addStr = "SECOND";
                        }
                     } else {
                        addStr = "MINUTE";
                     }
                  } else {
                     addStr = "HOUR";
                  }
               } else {
                  addStr = "QUARTER";
               }
            } else {
               addStr = "WEEK";
            }
         } else {
            addStr = "MONTH";
         }
      } else {
         addStr = "YEAR";
      }

      this.functionName.setColumnName("DATE_ADD");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (from_sqs != null && from_sqs.isHyperSql()) {
         if (funName.equalsIgnoreCase("SUBDATE")) {
            this.functionName.setColumnName("DATE_SUB");
         }

         Vector colExp = ((SelectColumn)arguments.get(1)).getColumnExpression();
         if (colExp.get(0) instanceof TableColumn && ((TableColumn)((TableColumn)colExp.get(0))).getColumnName().equalsIgnoreCase("interval") && colExp.get(1) instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)colExp.get(1);
            if (selCol.getOpenBrace().equalsIgnoreCase("(") && selCol.getColumnExpression().size() == 1) {
               colExp.set(1, selCol.getColumnExpression().get(0));
            }
         }

         this.setFunctionArguments(arguments);
      } else {
         if (this.functionArguments.size() == 2) {
            SelectColumn sc = new SelectColumn();
            Vector colExp = new Vector();
            colExp.add(" INTERVAL ");
            SelectColumn intrVal = (SelectColumn)arguments.get(1);
            intrVal.setOpenBrace("(");
            intrVal.setCloseBrace(")");
            colExp.addElement(intrVal);
            colExp.add(addStr);
            sc.setColumnExpression(colExp);
            arguments.setElementAt(sc, 1);
         }

         this.setFunctionArguments(arguments);
      }
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String addStr = "";
      String funName = this.functionName.getColumnName();
      if (funName.equalsIgnoreCase("ADDYEAR")) {
         addStr = "yy";
      } else if (funName.equalsIgnoreCase("ADDMONTH")) {
         addStr = "mm";
      } else if (funName.equalsIgnoreCase("ADDWEEK")) {
         addStr = "wk";
      } else if (funName.equalsIgnoreCase("ADDQUARTER")) {
         addStr = "qq";
      } else if (funName.equalsIgnoreCase("ADDHOUR")) {
         addStr = "hh";
      } else if (funName.equalsIgnoreCase("ADDMINUTE")) {
         addStr = "mi";
      } else if (funName.equalsIgnoreCase("ADDSECOND")) {
         addStr = "ss";
      }

      this.functionName.setColumnName("DATEADD");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String argOne = "CAST(" + arguments.get(0).toString() + " AS DATETIME)";
      arguments.set(0, addStr);
      arguments.addElement(argOne);
      this.setFunctionArguments(arguments);
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector vector = new Vector();
      Boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            vector.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String funName = this.functionName.getColumnName();
      String dateColumn = vector.get(0).toString();
      String interval = vector.get(1).toString();
      dateColumn = StringFunctions.handleLiteralStringDateForOracle(dateColumn);
      String query = null;
      String intervelVariable;
      String intervalType;
      if (!funName.equalsIgnoreCase("adddate") && !funName.equalsIgnoreCase("subdate") && !funName.equalsIgnoreCase("date_add") && !funName.equalsIgnoreCase("date_sub")) {
         if (funName.equalsIgnoreCase("addtime") || funName.equalsIgnoreCase("subtime")) {
            intervelVariable = null;
            if (funName.equalsIgnoreCase("addtime")) {
               intervelVariable = isdenodo ? "1" : " + ";
            } else if (funName.equalsIgnoreCase("subtime")) {
               intervelVariable = isdenodo ? "-1" : " - ";
            }

            if (isdenodo) {
               interval = StringFunctions.handleLiteralStringDateForDenodo(interval);
               intervalType = "((GETHOUR(" + interval + ")*60*60)+(GETMINUTE(" + interval + ")*60)+GETSECOND(" + interval + "))*" + intervelVariable + "";
               query = "ADDSECOND(" + dateColumn + "," + intervalType + ")";
            } else {
               query = " ( " + dateColumn + intervelVariable + " INTERVAL " + interval + " HOUR TO SECOND ) ";
            }
         }
      } else {
         intervelVariable = null;
         intervalType = null;
         if (((SelectColumn)vector.get(1)).getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)((TableColumn)((SelectColumn)vector.get(1)).getColumnExpression().get(0))).getColumnName().equalsIgnoreCase("interval")) {
            if (((SelectColumn)vector.get(1)).getColumnExpression().get(1) instanceof SelectColumn) {
               if (((SelectColumn)((SelectColumn)vector.get(1)).getColumnExpression().get(1)).getColumnExpression().size() == 1) {
                  intervelVariable = ((SelectColumn)((SelectColumn)vector.get(1)).getColumnExpression().get(1)).getColumnExpression().get(0).toString();
               } else {
                  intervelVariable = ((SelectColumn)vector.get(1)).getColumnExpression().get(1).toString();
               }
            } else {
               intervelVariable = ((SelectColumn)vector.get(1)).getColumnExpression().get(1).toString();
            }
         } else {
            intervelVariable = "(" + vector.get(1).toString() + ")";
         }

         if (vector.get(1) instanceof SelectColumn) {
            SelectColumn selectColumn = (SelectColumn)vector.get(1);
            Vector vc = selectColumn.getColumnExpression();
            if (vc.size() != 1 && vc.elementAt(0).toString().equalsIgnoreCase("interval")) {
               intervalType = vc.elementAt(2).toString();
            } else {
               intervalType = "day";
            }
         }

         if (intervelVariable == null && intervalType == null) {
            throw new ConvertException("Invalid Argument Value for Function " + funName + "", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{funName});
         }

         if (isdenodo) {
            dateColumn = StringFunctions.handleLiteralStringDateForDenodo(vector.get(0).toString());
            int operator = !funName.equalsIgnoreCase("subdate") && !funName.equalsIgnoreCase("date_sub") ? 1 : -1;
            intervelVariable = "CAST(" + intervelVariable + " AS INT4)";
            if (intervalType.equalsIgnoreCase("month")) {
               query = "ADDMONTH(" + dateColumn + ", " + intervelVariable + "*" + operator + ")";
            } else if (intervalType.equalsIgnoreCase("week")) {
               query = "ADDWEEK( " + dateColumn + ", " + intervelVariable + "*" + operator + ")";
            } else if (intervalType.equalsIgnoreCase("quarter")) {
               query = "ADDMONTH(" + dateColumn + ", " + intervelVariable + " * 3 *" + operator + ")";
            } else if (intervalType.equalsIgnoreCase("year")) {
               query = "ADDYEAR(" + dateColumn + ", " + intervelVariable + "*" + operator + ")";
            } else if (intervalType.equalsIgnoreCase("hour")) {
               query = "ADDHOUR(" + dateColumn + ", " + intervelVariable + "*" + operator + ")";
            } else if (intervalType.equalsIgnoreCase("minute")) {
               query = "ADDMINUTE(" + dateColumn + ", " + intervelVariable + "*" + operator + ")";
            } else if (intervalType.equalsIgnoreCase("second")) {
               query = "ADDSECOND(" + dateColumn + ", " + intervelVariable + "*" + operator + ")";
            } else if (intervalType.equalsIgnoreCase("microsecond")) {
               query = "ADDSECOND(" + dateColumn + ", " + intervelVariable + " * 0.000001 *" + operator + ")";
            } else {
               query = "ADDDAY(" + dateColumn + ", " + intervelVariable + "*" + operator + ")";
            }
         } else if (!funName.equalsIgnoreCase("adddate") && !funName.equalsIgnoreCase("date_add")) {
            if (funName.equalsIgnoreCase("subdate") || funName.equalsIgnoreCase("date_sub")) {
               if (intervalType.equalsIgnoreCase("month")) {
                  query = "CAST(ADD_MONTHS(" + dateColumn + ", (" + intervelVariable + "* -1)) AS TIMESTAMP)";
               } else if (intervalType.equalsIgnoreCase("week")) {
                  query = " (" + dateColumn + " -  ((interval '7' day) * " + intervelVariable + " ))";
               } else if (intervalType.equalsIgnoreCase("quarter")) {
                  query = "CAST(ADD_MONTHS(" + dateColumn + ", " + intervelVariable + " * -3 ) AS TIMESTAMP)";
               } else if (intervalType.equalsIgnoreCase("year")) {
                  query = "CAST(ADD_MONTHS(" + dateColumn + ", " + intervelVariable + " * -12 ) AS TIMESTAMP)";
               } else if (intervalType.equalsIgnoreCase("microsecond")) {
                  query = "CAST(" + dateColumn + "- ((interval '0.000001' second) * " + intervelVariable + ") AS TIMESTAMP)";
               } else {
                  query = " (" + dateColumn + " - ((interval '1' " + intervalType + " ) *  " + intervelVariable + "))";
               }
            }
         } else if (intervalType.equalsIgnoreCase("month")) {
            query = "CAST(ADD_MONTHS(" + dateColumn + ", " + intervelVariable + ") AS TIMESTAMP)";
         } else if (intervalType.equalsIgnoreCase("week")) {
            query = "(" + dateColumn + " +  ((interval '7' day) * " + intervelVariable + "))";
         } else if (intervalType.equalsIgnoreCase("quarter")) {
            query = "CAST(ADD_MONTHS(" + dateColumn + ", " + intervelVariable + " * 3) AS TIMESTAMP)";
         } else if (intervalType.equalsIgnoreCase("year")) {
            query = "CAST(ADD_MONTHS(" + dateColumn + ", " + intervelVariable + " * 12) AS TIMESTAMP)";
         } else if (intervalType.equalsIgnoreCase("microsecond")) {
            query = "CAST(" + dateColumn + "+ ((interval '0.000001' second) * " + intervelVariable + ") AS TIMESTAMP)";
         } else {
            query = " (" + dateColumn + " + ((interval '1' " + intervalType + " ) *  " + intervelVariable + "))";
         }
      }

      this.functionName.setColumnName(query);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }
}
