package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class timestampDiff extends FunctionCalls {
   private static Map<String, String> CONVERTIONMAP = new HashMap();
   private static Map<String, String> EQUIVDB2INTERVALMAP;

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean isString = false;
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      boolean canUseUDFFunction = from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.date.time");
      boolean var10000;
      if (from_sqs != null && !from_sqs.isAmazonRedShift() && !from_sqs.isPgsqlLive()) {
         var10000 = true;
      } else {
         var10000 = false;
      }

      Vector arguments = new Vector();
      boolean isRedshift = from_sqs != null && from_sqs.isAmazonRedShift();
      boolean isVertica = from_sqs != null && from_sqs.isVerticaDb();
      String divType = isVertica ? "//" : "/";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            if (!isRedshift && selColumn.getColumnExpression() != null && !selColumn.getColumnExpression().isEmpty()) {
               if (selColumn.getColumnExpression().get(0) != null && selColumn.getColumnExpression().get(0) instanceof String) {
                  String stringValue = selColumn.getColumnExpression().get(0).toString();
                  stringValue = "CAST(" + this.handleStringLiteralForDateTime(stringValue, from_sqs) + " AS TIMESTAMP)";
                  selColumn.getColumnExpression().set(0, stringValue);
               } else {
                  selColumn.modifyCurrentTimeAsCurrentTimestamp(from_sqs);
               }
            }

            arguments.addElement(selColumn.toPostgreSQLSelect(to_sqs, from_sqs));
         }
      }

      if (arguments.size() == 2) {
         arguments.addElement("CURRENT_DATE");
      }

      boolean isSecond = arguments.get(0).toString().equalsIgnoreCase("SECOND");
      boolean isMicroSecond = arguments.get(0).toString().equalsIgnoreCase("MICROSECOND");
      boolean isToTimestamp = arguments.get(1).toString().trim().equalsIgnoreCase("to_timestamp(0)");
      String qry;
      if (isRedshift) {
         if (isToTimestamp && (isSecond || isMicroSecond)) {
            qry = "EXTRACT(EPOCH FROM  (" + arguments.get(2) + " - " + arguments.get(1) + ") )";
            if (!isSecond) {
               qry = "cast((" + qry + " * " + (String)CONVERTIONMAP.get(arguments.get(0).toString().trim().toLowerCase()) + ") as bigint)";
            } else {
               qry = "cast(floor(" + qry + ") as bigint)";
            }

            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else {
            this.functionName.setColumnName("DATEDIFF");
            arguments.set(1, "CAST(" + arguments.get(1).toString() + " AS TIMESTAMP)");
            arguments.set(2, "CAST(" + arguments.get(2).toString() + " AS TIMESTAMP)");
            this.setFunctionArguments(arguments);
         }
      } else {
         qry = "";
         if (!isSecond && !isMicroSecond) {
            if (arguments.get(0).toString().equalsIgnoreCase("YEAR")) {
               qry = isVertica ? "case when cast(AGE_IN_YEARS(" + arguments.get(1) + ",CAST((" + arguments.get(2) + ") AS TIMESTAMP)) as bigint) < 0 then abs(cast(AGE_IN_YEARS(" + arguments.get(1) + ",CAST((" + arguments.get(2) + ") AS TIMESTAMP)) as bigint)+1) else -1*(cast(AGE_IN_YEARS(" + arguments.get(1) + ",CAST((" + arguments.get(2) + ") AS TIMESTAMP)) as bigint)) end" : "cast(" + extract.applyDatePartOrExtractWrapper(from_sqs, "YEAR", " AGE(" + arguments.get(2) + "," + arguments.get(1) + ")") + "as bigint)";
               if (canUseUDFFunction) {
                  qry = "TIMESTAMPDIFF_YEAR(" + arguments.get(1) + "," + arguments.get(2) + ")";
               }
            } else {
               String arg1;
               String arg1;
               if (arguments.get(0).toString().equalsIgnoreCase("MONTH")) {
                  if (isVertica) {
                     arg1 = arguments.get(1).toString();
                     arg1 = arguments.get(2).toString();
                     if (!arguments.elementAt(1).toString().toLowerCase().contains("interval  '1'  day * round((( cast(extract(day from ") && !arguments.elementAt(2).toString().toLowerCase().contains("interval  '1'  day * round((( cast(extract(day from ")) {
                        qry = "(case when age_in_months(" + arg1 + "," + arg1 + ") < 0 then age_in_months(" + arg1 + "," + arg1 + ")+1 else age_in_months(" + arg1 + "," + arg1 + ") end)";
                     } else {
                        qry = "TIMESTAMPDIFF('month'," + arg1 + "," + arg1 + ")";
                     }
                  } else {
                     qry = "cast(((" + extract.applyDatePartOrExtractWrapper(from_sqs, "YEAR", "AGE(" + arguments.get(2) + "," + arguments.get(1) + ")") + " * 12) + " + extract.applyDatePartOrExtractWrapper(from_sqs, "MONTH", "AGE(" + arguments.get(2) + "," + arguments.get(1) + ")") + ") as bigint)";
                  }

                  if (canUseUDFFunction) {
                     qry = "TIMESTAMPDIFF_MONTH(" + arguments.get(1) + "," + arguments.get(2) + ")";
                  }
               } else if (arguments.get(0).toString().equalsIgnoreCase("QUARTER")) {
                  if (isVertica) {
                     arg1 = arguments.get(1).toString();
                     arg1 = arguments.get(2).toString();
                     if (!this.functionArguments.elementAt(1).toString().toLowerCase().contains("date_sub") && !this.functionArguments.elementAt(1).toString().toLowerCase().contains("dayofmonth") && !this.functionArguments.elementAt(2).toString().toLowerCase().contains("date_sub") && !this.functionArguments.elementAt(2).toString().toLowerCase().contains("dayofmonth")) {
                        qry = "((case when age_in_months(" + arg1 + "," + arg1 + ") < 0 then age_in_months(" + arg1 + "," + arg1 + ")+1 else age_in_months(" + arg1 + "," + arg1 + ") end) // 3)";
                     } else {
                        qry = "TIMESTAMPDIFF('quarter'," + arg1 + "," + arg1 + ")";
                     }
                  } else {
                     qry = "cast(((" + extract.applyDatePartOrExtractWrapper(from_sqs, "YEAR", "AGE(" + arguments.get(2) + "," + arguments.get(1) + ")") + " * 4) + (" + extract.applyDatePartOrExtractWrapper(from_sqs, "MONTH", "AGE(" + arguments.get(2) + "," + arguments.get(1) + ")") + "::integer / 3)) as bigint)";
                  }

                  if (canUseUDFFunction) {
                     qry = "TIMESTAMPDIFF_QUARTER(" + arguments.get(1) + "," + arguments.get(2) + ")";
                  }
               } else if (!arguments.get(0).toString().equalsIgnoreCase("DAY")) {
                  qry = "cast((" + extract.applyDatePartOrExtractWrapper(from_sqs, "EPOCH", "" + arguments.get(2) + "-" + arguments.get(1) + "") + "::bigint " + divType + " " + (String)CONVERTIONMAP.get(arguments.get(0).toString().trim().toLowerCase()) + "::bigint) as bigint)";
                  if (canUseUDFFunction) {
                     qry = "TIMESTAMPDIFF(" + arguments.get(1) + "," + arguments.get(2) + "," + (String)CONVERTIONMAP.get(arguments.get(0).toString().trim().toLowerCase()) + ",1 )";
                  }
               } else {
                  SelectColumn sc1 = (SelectColumn)arguments.get(1);
                  arg1 = arguments.get(1).toString();
                  String arg2 = arguments.get(2).toString();
                  if (sc1.getColumnExpression() != null && sc1.getColumnExpression().size() == 1 && sc1.getColumnExpression().get(0) instanceof FunctionCalls) {
                     FunctionCalls fnCl = (FunctionCalls)sc1.getColumnExpression().get(0);
                     String fnName = fnCl.getFunctionName().getColumnName();
                     if (fnName.equalsIgnoreCase("date") || fnCl.getFunctionArguments() != null && fnCl.getFunctionArguments().size() == 1 && fnCl.getFunctionArguments().get(0) instanceof String && ((String)fnCl.getFunctionArguments().get(0)).equalsIgnoreCase("current_date")) {
                        arg1 = arg1 + "::timestamp";
                     }
                  }

                  qry = "cast((" + extract.applyDatePartOrExtractWrapper(from_sqs, "EPOCH", "" + arg2 + " - " + arg1 + "") + "::bigint " + divType + " 86400::bigint) as bigint)";
                  if (canUseUDFFunction) {
                     qry = "TIMESTAMPDIFF(" + arguments.get(1) + "," + arguments.get(2) + ",86400,1 )";
                  }
               }
            }
         } else {
            qry = extract.applyDatePartOrExtractWrapper(from_sqs, "EPOCH", arguments.get(2) + " - " + arguments.get(1) + "");
            if (!isSecond) {
               qry = "cast((" + qry + " * " + (String)CONVERTIONMAP.get(arguments.get(0).toString().trim().toLowerCase()) + ") as bigint)";
            } else {
               qry = "cast(floor(" + qry + ") as bigint)";
            }

            if (canUseUDFFunction && !isToTimestamp) {
               qry = "TIMESTAMPDIFF(" + arguments.get(1) + "," + arguments.get(2) + "," + "1," + (String)CONVERTIONMAP.get(arguments.get(0).toString().trim().toLowerCase()) + ")";
            }
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      if (this.functionName.getColumnName().equalsIgnoreCase("age_years")) {
         arguments.addElement("YEAR");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("age_months")) {
         arguments.addElement("MONTH");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("days_between")) {
         arguments.addElement("DAY");
      }

      this.functionName.setColumnName("TIMESTAMPDIFF");

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() < 3) {
         arguments.add("now()");
      }

      if (from_sqs != null && from_sqs.isHyperSql()) {
         String arg1 = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(1).toString());
         String arg2 = arguments.size() < 3 ? "CURRENT_TIMESTAMP" : StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(2).toString());
         String qry = "";
         String type = arguments.get(0).toString();
         if (type.equalsIgnoreCase("month")) {
            qry = " TRUNC(MONTHS_BETWEEN(" + arg2 + "," + arg1 + ")) ";
         } else if (type.equalsIgnoreCase("year")) {
            qry = " TRUNC(MONTHS_BETWEEN(" + arg2 + "," + arg1 + ")/12) ";
         } else if (type.equalsIgnoreCase("day")) {
            qry = " (TIMESTAMPDIFF(MICROSECOND," + arg1 + "," + arg2 + ")/(1000000*60*60*24)) ";
         } else if (type.equalsIgnoreCase("hour")) {
            qry = " (TIMESTAMPDIFF(MICROSECOND," + arg1 + "," + arg2 + ")/(1000000*60*60)) ";
         } else if (type.equalsIgnoreCase("minute")) {
            qry = " (TIMESTAMPDIFF(MICROSECOND," + arg1 + "," + arg2 + ")/(1000000*60)) ";
         } else if (type.equalsIgnoreCase("second")) {
            qry = " (TIMESTAMPDIFF(MICROSECOND," + arg1 + "," + arg2 + ")/(1000000)) ";
         } else if (type.equalsIgnoreCase("microsecond")) {
            qry = " TIMESTAMPDIFF(MICROSECOND," + arg1 + "," + arg2 + ") ";
         } else if (type.equalsIgnoreCase("week")) {
            qry = " TRUNC((TIMESTAMPDIFF(MICROSECOND," + arg1 + "," + arg2 + ")/(1000000*60*60*24))/7) ";
         } else if (type.equalsIgnoreCase("quarter")) {
            qry = " TRUNC(MONTHS_BETWEEN(" + arg2 + "," + arg1 + ")/3) ";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      } else {
         this.setFunctionArguments(arguments);
      }
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count != 0) {
               this.handleStringLiteralForDateTime(from_sqs, i_count, true);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.functionName.setColumnName("TIMESTAMPDIFF");
      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }

         if (i_count >= 1) {
            String argStr = "CAST( " + arguments.get(i_count).toString() + " AS DATETIME)";
            arguments.set(i_count, argStr);
         }
      }

      String qry = "CAST(DATEDIFF( " + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ", " + arguments.get(2).toString() + " ) AS BIGINT)";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }

         if (i_count >= 1) {
            String argStr = "CAST( " + StringFunctions.handleLiteralStringDateForAthena(arguments.get(i_count).toString()) + " AS TIMESTAMP)";
            arguments.set(i_count, argStr);
         }
      }

      String qry;
      if (arguments.get(0).toString().equalsIgnoreCase("microsecond")) {
         qry = "CAST(DATE_DIFF( 'millisecond', " + arguments.get(1).toString() + ", " + arguments.get(2).toString() + " ) AS BIGINT) * 1000";
      } else {
         qry = "CAST(DATE_DIFF( '" + arguments.get(0).toString() + "', " + arguments.get(1).toString() + ", " + arguments.get(2).toString() + " ) AS BIGINT)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector vector = new Vector();
      Boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (isdenodo && i_count == 0 && (this.functionName.getColumnName().equalsIgnoreCase("TIMESTAMPDIFF") || this.functionName.getColumnName().equalsIgnoreCase("DATETIMEDIFF"))) {
            this.functionArguments.setElementAt(this.functionArguments.elementAt(i_count).toString(), i_count);
            vector.addElement(this.functionArguments.elementAt(i_count));
         } else if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            vector.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (vector.size() == 3) {
         String dateString1 = vector.get(1).toString();
         String dateString2 = vector.get(2).toString();
         dateString1 = isdenodo ? StringFunctions.handleLiteralStringDateForDenodo(dateString1) : StringFunctions.handleLiteralStringDateForOracle(dateString1);
         dateString2 = isdenodo ? StringFunctions.handleLiteralStringDateForDenodo(dateString2) : StringFunctions.handleLiteralStringDateForOracle(dateString2);
         String query = "";
         String months_between = isdenodo ? "GETMONTHSBETWEEN" : "MONTHS_BETWEEN";
         if (isdenodo && (vector.get(0).toString().equalsIgnoreCase("YEAR") || vector.get(0).toString().equalsIgnoreCase("MONTH") || vector.get(0).toString().equalsIgnoreCase("Quarter"))) {
            dateString2 = dateString1;
            dateString1 = StringFunctions.handleLiteralStringDateForDenodo(vector.get(2).toString());
         }

         if (vector.get(0).toString().equalsIgnoreCase("YEAR")) {
            query = "TRUNC(" + months_between + "( " + dateString2 + ", " + dateString1 + ") / 12)";
         } else if (vector.get(0).toString().equalsIgnoreCase("MONTH")) {
            query = "TRUNC(" + months_between + "( " + dateString2 + ", " + dateString1 + "))";
         } else if (vector.get(0).toString().equalsIgnoreCase("DAY")) {
            if (isdenodo) {
               query = "TRUNC((GETTIMEINMILLIS(" + dateString2 + ")-GETTIMEINMILLIS(" + dateString1 + "))/(1000.0*24*3600))";
            } else {
               query = "TRUNC( CAST (" + dateString2 + " as DATE) -  CAST ( " + dateString1 + " as DATE))";
            }
         } else if (vector.get(0).toString().equalsIgnoreCase("HOUR")) {
            if (isdenodo) {
               query = "round((GETTIMEINMILLIS(" + dateString2 + ")-GETTIMEINMILLIS(" + dateString1 + "))/(1000.0*60.0*60.0))";
            } else {
               query = "round(( CAST( " + dateString2 + " AS DATE ) - CAST( " + dateString1 + " AS DATE ) ) * 24)";
            }
         } else if (vector.get(0).toString().equalsIgnoreCase("MINUTE")) {
            if (isdenodo) {
               query = "round((GETTIMEINMILLIS(" + dateString2 + ")-GETTIMEINMILLIS(" + dateString1 + "))/(1000.0*60))";
            } else {
               query = "round(( CAST( " + dateString2 + " AS DATE ) - CAST( " + dateString1 + " AS DATE ) ) * 1440)";
            }
         } else if (vector.get(0).toString().equalsIgnoreCase("SECOND")) {
            if (isdenodo) {
               query = "round((GETTIMEINMILLIS(" + dateString2 + ")-GETTIMEINMILLIS(" + dateString1 + "))/(1000.0))";
            } else {
               query = "round(( CAST( " + dateString2 + " AS DATE ) - CAST( " + dateString1 + " AS DATE ) ) * 86400)";
            }
         } else if (vector.get(0).toString().equalsIgnoreCase("MICROSECOND")) {
            if (isdenodo) {
               query = "((GETTIMEINMILLIS(" + dateString2 + ")-GETTIMEINMILLIS(" + dateString1 + "))*1000)";
            } else {
               query = "round(( CAST( " + dateString2 + " AS DATE ) - CAST( " + dateString1 + " AS DATE ) ) * 86400000000)";
            }
         } else if (vector.get(0).toString().equalsIgnoreCase("Quarter")) {
            query = "TRUNC( " + months_between + "( " + dateString2 + ", " + dateString1 + ") / 3)";
         } else if (vector.get(0).toString().equalsIgnoreCase("WEEK")) {
            if (isdenodo) {
               query = "TRUNC((GETTIMEINMILLIS(" + dateString2 + ")-GETTIMEINMILLIS(" + dateString1 + "))/(1000.0*24*3600*7))";
            } else {
               query = "TRUNC(TRUNC(CAST( " + dateString2 + " AS DATE )  - CAST( " + dateString1 + " AS DATE )) / 7)";
            }
         }

         this.functionName.setColumnName(query);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            if (selColumn.getColumnExpression() != null && !selColumn.getColumnExpression().isEmpty() && selColumn.getColumnExpression().get(0) != null && selColumn.getColumnExpression().get(0) instanceof String) {
               String stringValue = selColumn.getColumnExpression().get(0).toString();
               stringValue = "CAST(" + stringValue + " AS TIMESTAMP)";
               selColumn.getColumnExpression().set(0, stringValue);
            }

            arguments.addElement(selColumn.toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 2) {
         arguments.addElement("CURRENT_DATE");
      }

      Vector newArg = new Vector();
      newArg.addElement("CAST( " + arguments.get(2) + " AS DATETIME)");
      newArg.addElement("CAST( " + arguments.get(1) + " AS DATETIME)");
      newArg.addElement(arguments.get(0));
      this.functionName.setColumnName("DATETIME_DIFF");
      this.setFunctionArguments(newArg);
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
         newArguments.addElement(arguments.get(1));
         newArguments.addElement(arguments.get(0));
         this.setFunctionArguments(newArguments);
      } else {
         this.setFunctionArguments(arguments);
      }
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      String type;
      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }

         if (i_count >= 1) {
            type = this.functionArguments.get(0).toString().equalsIgnoreCase("microsecond") ? StringFunctions.handleLiteralStringDateForSapHana(arguments.get(i_count).toString()) : "CAST( " + StringFunctions.handleLiteralStringDateForSapHana(arguments.get(i_count).toString()) + " AS TIMESTAMP)";
            arguments.set(i_count, type);
         }
      }

      String qry = "";
      type = arguments.get(0).toString();
      if (type.equalsIgnoreCase("month")) {
         qry = "MONTHS_BETWEEN(" + arguments.get(1) + "," + arguments.get(2) + ")";
      } else if (type.equalsIgnoreCase("year")) {
         qry = "YEARS_BETWEEN(" + arguments.get(1) + "," + arguments.get(2) + ")";
      } else if (type.equalsIgnoreCase("day")) {
         qry = "DAYS_BETWEEN(" + arguments.get(1) + "," + arguments.get(2) + ")";
      } else if (type.equalsIgnoreCase("hour")) {
         qry = "CAST((SECONDS_BETWEEN(" + arguments.get(1) + "," + arguments.get(2) + ")/3600) AS BIGINT)";
      } else if (type.equalsIgnoreCase("minute")) {
         qry = "CAST((SECONDS_BETWEEN(" + arguments.get(1) + "," + arguments.get(2) + ")/60) AS BIGINT)";
      } else if (type.equalsIgnoreCase("second")) {
         qry = "SECONDS_BETWEEN(" + arguments.get(1) + "," + arguments.get(2) + ")";
      } else if (type.equalsIgnoreCase("microsecond")) {
         qry = "CAST((NANO100_BETWEEN(" + arguments.get(1) + "," + arguments.get(2) + ")/10) AS BIGINT)";
      } else if (type.equalsIgnoreCase("week")) {
         qry = "CAST((DAYS_BETWEEN(" + arguments.get(1) + "," + arguments.get(2) + ")/7) AS BIGINT)";
      } else if (type.equalsIgnoreCase("quarter")) {
         qry = "CAST((MONTHS_BETWEEN(" + arguments.get(1) + "," + arguments.get(2) + ")/3) AS BIGINT)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      String type;
      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }

         if (i_count >= 1) {
            type = StringFunctions.handleLiteralStringDateForSqlite(arguments.get(i_count).toString());
            arguments.set(i_count, type);
         }
      }

      String qry = "";
      type = arguments.get(0).toString();
      if (type.equalsIgnoreCase("month")) {
         qry = "cast(((case when strftime('%d'," + arguments.get(1) + ")>strftime('%d',datetime(" + arguments.get(2) + ")) then case when strftime('%Y'," + arguments.get(1) + ")<strftime('%Y',datetime(" + arguments.get(2) + ")) or (strftime('%Y'," + arguments.get(1) + ") == strftime('%Y',datetime(" + arguments.get(2) + ")) and strftime('%m'," + arguments.get(1) + ") < strftime('%m',datetime(" + arguments.get(2) + "))) then case when strftime('%d',datetime(" + arguments.get(2) + "))<strftime('%d'," + arguments.get(1) + ") then (abs(strftime('%d',datetime(" + arguments.get(2) + "))+strftime('%d', date(DATETIME(datetime(" + arguments.get(2) + "), '-' || 1 || ' months') ,'start of month','+1 month','-1 day'))-strftime('%d'," + arguments.get(1) + "))/100.0)+((strftime('%Y',datetime(" + arguments.get(2) + "))*12)+strftime('%m',datetime(" + arguments.get(2) + "))-(strftime('%Y'," + arguments.get(1) + ")*12)-strftime('%m'," + arguments.get(1) + ")-1) else (abs(strftime('%d',datetime(" + arguments.get(2) + "))-strftime('%d'," + arguments.get(1) + "))/100.0)+((strftime('%Y',datetime(" + arguments.get(2) + "))*12)+strftime('%m',datetime(" + arguments.get(2) + "))-(strftime('%Y'," + arguments.get(1) + ")*12)-strftime('%m'," + arguments.get(1) + ")) end else (abs(strftime('%d',datetime(" + arguments.get(2) + "))-strftime('%d'," + arguments.get(1) + "))/100.0)+((strftime('%Y',datetime(" + arguments.get(2) + "))*12)+strftime('%m',datetime(" + arguments.get(2) + "))-(strftime('%Y'," + arguments.get(1) + ")*12)-strftime('%m'," + arguments.get(1) + ")) end else (abs(strftime('%d',datetime(" + arguments.get(2) + "))-strftime('%d'," + arguments.get(1) + "))/100.0)+((strftime('%Y',datetime(" + arguments.get(2) + "))*12)+strftime('%m',datetime(" + arguments.get(2) + "))-(strftime('%Y'," + arguments.get(1) + ")*12)-strftime('%m'," + arguments.get(1) + ")) end)) - (case when (julianday(time(" + arguments.get(1) + "))-julianday(time(" + arguments.get(2) + "))) > 0 then substr(cast((julianday(time(" + arguments.get(2) + "))-2451545) as text),1,4) else 0 end) as integer)";
      } else if (type.equalsIgnoreCase("year")) {
         qry = "-cast(((julianday(" + arguments.get(1) + ") - julianday(" + arguments.get(2) + "))/365) as integer)";
      } else if (type.equalsIgnoreCase("day")) {
         qry = "-cast((julianday(" + arguments.get(1) + ") - julianday(" + arguments.get(2) + ")) as integer)";
      } else if (type.equalsIgnoreCase("hour")) {
         qry = "-cast(round((julianday(" + arguments.get(1) + ") - julianday(" + arguments.get(2) + "))*24) as integer)";
      } else if (type.equalsIgnoreCase("minute")) {
         qry = "-cast(((julianday(" + arguments.get(1) + ") - julianday(" + arguments.get(2) + "))*24*60) as integer)";
      } else if (type.equalsIgnoreCase("second")) {
         qry = "-cast(round((julianday(" + arguments.get(1) + ") - julianday(" + arguments.get(2) + "))*24*60*60) as integer)";
      } else if (type.equalsIgnoreCase("microsecond")) {
         qry = "-cast(((round((julianday(" + arguments.get(1) + ") - julianday(" + arguments.get(2) + "))*24*60*60))*1000000) as integer)";
      } else if (type.equalsIgnoreCase("week")) {
         qry = "-cast(((julianday(" + arguments.get(1) + ") - julianday(" + arguments.get(2) + "))/7) as integer)";
      } else if (type.equalsIgnoreCase("quarter")) {
         qry = "-cast((((julianday(" + arguments.get(1) + ") - julianday(" + arguments.get(2) + "))/365)*4) as integer)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      boolean isDateDiff = this.functionName.getColumnName().equalsIgnoreCase("DATEDIFF");
      String arg1;
      String arg2;
      String type;
      if (isDateDiff) {
         type = "day";
         arg1 = arguments.get(1).toString();
         arg2 = arguments.get(0).toString();
      } else {
         type = arguments.get(0).toString();
         arg1 = arguments.get(1).toString();
         arg2 = arguments.get(2).toString();
      }

      String microSecondsDiff = "((DAYS(TIMESTAMP(" + arg2 + ")) - DAYS(TIMESTAMP(" + arg1 + ") )) * BIGINT (86400000000)\n" + "+ (MIDNIGHT_SECONDS(TIMESTAMP(" + arg2 + ")) - MIDNIGHT_SECONDS(TIMESTAMP(" + arg1 + "))) * BIGINT(1000000)\n" + "+ (MICROSECOND(TIMESTAMP(" + arg2 + ")) - MICROSECOND(TIMESTAMP(" + arg1 + "))))";
      String qry;
      if (type.equalsIgnoreCase("week")) {
         qry = "(" + microSecondsDiff + "/604800000000)";
      } else if (type.equalsIgnoreCase("day")) {
         qry = "(" + microSecondsDiff + "/86400000000)";
      } else if (type.equalsIgnoreCase("hour")) {
         qry = "(" + microSecondsDiff + "/3600000000)";
      } else if (type.equalsIgnoreCase("minute")) {
         qry = "(" + microSecondsDiff + "/60000000)";
      } else if (type.equalsIgnoreCase("second")) {
         qry = "(" + microSecondsDiff + "/1000000)";
      } else if (type.equalsIgnoreCase("microsecond")) {
         qry = microSecondsDiff;
      } else {
         qry = "Cast(TIMESTAMPDIFF(" + (String)EQUIVDB2INTERVALMAP.get(type.toLowerCase()) + " ,CHAR( TIMESTAMP(" + arg2 + ") - TIMESTAMP(" + arg1 + "))) AS BIGINT)";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
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

      String qry = "";
      String type = arguments.get(0).toString();
      String dateString1 = StringFunctions.handleLiteralStringDateForInformix(arguments.get(1).toString());
      String dateString2 = StringFunctions.handleLiteralStringDateForInformix(arguments.get(2).toString());
      String secondsDiff = "(((EXTEND(" + dateString2 + ", YEAR TO DAY) - EXTEND(" + dateString1 + ",YEAR TO DAY))::INTERVAL DAY(9) TO DAY)::CHAR(15)::DECIMAL(18,5) * 86400 + ((EXTEND(" + dateString2 + ", HOUR TO FRACTION(5)) - EXTEND(" + dateString1 + ", HOUR TO FRACTION(5)))::INTERVAL SECOND(6) TO FRACTION(5))::CHAR(15)::DECIMAL(18,5))";
      if (type.equalsIgnoreCase("month")) {
         qry = "(MONTHS_BETWEEN(" + dateString2 + "," + dateString1 + ")::BIGINT)";
      } else if (type.equalsIgnoreCase("year")) {
         qry = "((MONTHS_BETWEEN(" + dateString2 + "," + dateString1 + ")/12)::BIGINT)";
      } else if (type.equalsIgnoreCase("day")) {
         qry = "((" + secondsDiff + "/86400)::BIGINT)";
      } else if (type.equalsIgnoreCase("hour")) {
         qry = "((" + secondsDiff + "/3600)::BIGINT)";
      } else if (type.equalsIgnoreCase("minute")) {
         qry = "((" + secondsDiff + "/60)::BIGINT)";
      } else if (type.equalsIgnoreCase("second")) {
         qry = "((" + secondsDiff + ")::BIGINT)";
      } else if (type.equalsIgnoreCase("microsecond")) {
         qry = "((" + secondsDiff + " * 1000000)::BIGINT)";
      } else if (type.equalsIgnoreCase("week")) {
         qry = "((" + secondsDiff + "/604800)::BIGINT)";
      } else if (type.equalsIgnoreCase("quarter")) {
         qry = "((MONTHS_BETWEEN(" + dateString2 + "," + dateString1 + ")/3)::BIGINT)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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

      String type = arguments.get(0).toString();
      String interval = "";
      String qry = "";
      if (type.equalsIgnoreCase("YEAR")) {
         interval = "'yyyy'";
      } else if (type.equalsIgnoreCase("QUARTER")) {
         interval = "'q'";
      } else if (type.equalsIgnoreCase("MONTH")) {
         interval = "'m'";
      } else if (type.equalsIgnoreCase("DAY")) {
         interval = "'d'";
      } else if (type.equalsIgnoreCase("WEEK")) {
         interval = "'ww'";
      } else if (type.equalsIgnoreCase("HOUR")) {
         interval = "'h'";
      } else if (type.equalsIgnoreCase("MINUTE")) {
         interval = "'n'";
      } else if (type.equalsIgnoreCase("SECOND")) {
         interval = "'s'";
      } else if (type.equalsIgnoreCase("MICROSECOND")) {
         interval = "'s'";
      }

      if (this.functionName.toString().equalsIgnoreCase("datediff")) {
         qry = "CCur(DATEDIFF('d', " + arguments.get(1).toString() + ", " + arguments.get(0).toString() + " ))";
      } else {
         qry = qry + "CCur(DATEDIFF( " + interval + ", " + arguments.get(1).toString() + ", " + arguments.get(2).toString() + " ))";
         if (type.equalsIgnoreCase("MICROSECOND")) {
            qry = "CCur((10^3)*" + qry + ")";
         }
      }

      this.functionName.setColumnName("(" + qry + ")");
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

      String arg1 = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(1).toString());
      String arg2 = arguments.size() < 3 ? "CURRENT_TIMESTAMP" : StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(2).toString());
      String qry = "";
      String type = arguments.get(0).toString();
      if (type.equalsIgnoreCase("month")) {
         qry = " TRUNC(MONTHS_BETWEEN(" + arg2 + "," + arg1 + ")) ";
      } else if (type.equalsIgnoreCase("year")) {
         qry = " TRUNC(MONTHS_BETWEEN(" + arg2 + "," + arg1 + ")/12) ";
      } else if (type.equalsIgnoreCase("day")) {
         qry = " (TIMESTAMPDIFF(MICROSECOND," + arg1 + "," + arg2 + ")/(1000000*60*60*24)) ";
      } else if (type.equalsIgnoreCase("hour")) {
         qry = " (TIMESTAMPDIFF(MICROSECOND," + arg1 + "," + arg2 + ")/(1000000*60*60)) ";
      } else if (type.equalsIgnoreCase("minute")) {
         qry = " (TIMESTAMPDIFF(MICROSECOND," + arg1 + "," + arg2 + ")/(1000000*60)) ";
      } else if (type.equalsIgnoreCase("second")) {
         qry = " (TIMESTAMPDIFF(MICROSECOND," + arg1 + "," + arg2 + ")/(1000000)) ";
      } else if (type.equalsIgnoreCase("microsecond")) {
         qry = " TIMESTAMPDIFF(MICROSECOND," + arg1 + "," + arg2 + ") ";
      } else if (type.equalsIgnoreCase("week")) {
         qry = " TRUNC((TIMESTAMPDIFF(MICROSECOND," + arg1 + "," + arg2 + ")/(1000000*60*60*24))/7) ";
      } else if (type.equalsIgnoreCase("quarter")) {
         qry = " TRUNC(MONTHS_BETWEEN(" + arg2 + "," + arg1 + ")/3) ";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("datediff")) {
         qry = "(TIMESTAMPDIFF(DAY,CAST(" + arguments.get(1).toString() + " AS TIMESTAMP),CAST( " + arguments.get(0).toString() + " AS TIMESTAMP)))";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   static {
      CONVERTIONMAP.put("microsecond", "1000000");
      CONVERTIONMAP.put("second", "1");
      CONVERTIONMAP.put("minute", "60");
      CONVERTIONMAP.put("hour", "3600");
      CONVERTIONMAP.put("day", "86400");
      CONVERTIONMAP.put("week", "604800");
      CONVERTIONMAP.put("month", "2629760");
      CONVERTIONMAP.put("quarter", "7889280");
      CONVERTIONMAP.put("year", "31557120");
      EQUIVDB2INTERVALMAP = new HashMap();
      EQUIVDB2INTERVALMAP.put("microsecond", "1");
      EQUIVDB2INTERVALMAP.put("second", "2");
      EQUIVDB2INTERVALMAP.put("minute", "4");
      EQUIVDB2INTERVALMAP.put("hour", "8");
      EQUIVDB2INTERVALMAP.put("day", "16");
      EQUIVDB2INTERVALMAP.put("week", "32");
      EQUIVDB2INTERVALMAP.put("month", "64");
      EQUIVDB2INTERVALMAP.put("quarter", "128");
      EQUIVDB2INTERVALMAP.put("year", "256");
   }
}
