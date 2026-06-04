package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class TimeToSec extends FunctionCalls {
   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String funName = this.functionName.getColumnName().trim();
      int i_count;
      StringBuffer arguments;
      if (!funName.equalsIgnoreCase("timestampdiff")) {
         if (funName.equalsIgnoreCase("time_to_sec")) {
            arguments = new StringBuffer();

            for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  if (i_count == 0) {
                     this.handleStringLiteralForTime(from_sqs, i_count, false, true);
                  }

                  arguments.append(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
               } else {
                  arguments.append(this.functionArguments.elementAt(i_count));
               }
            }

            this.functionName.setColumnName("CAST(((time(timestamp(" + arguments + "))-(time '00:00:00'))/interval '1' second) AS BIGINT)");
         } else if (funName.equalsIgnoreCase("timediff")) {
            arguments = new StringBuffer();

            for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               StringBuffer temp = new StringBuffer();
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  this.handleStringLiteralForTime(from_sqs, i_count, false, true);
                  temp.append(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
               } else {
                  temp.append(this.functionArguments.elementAt(i_count));
               }

               if (i_count == 0) {
                  arguments.append("time(from_unixtime(0) + (time(" + temp + ")");
               } else if (i_count == 1) {
                  arguments.append("-time(" + temp + ")))");
               }
            }

            this.functionName.setColumnName(arguments + "");
         }
      } else {
         Vector arguments = new Vector();

         for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               if (i_count != 0) {
                  this.handleStringLiteralForDateTime(from_sqs, i_count, true);
               }

               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         if (!arguments.get(1).toString().trim().equalsIgnoreCase("from_unixtime(0)") || !arguments.get(0).toString().trim().equalsIgnoreCase("SECOND")) {
            this.functionName.setColumnName("TIMESTAMPDIFF");
            this.setFunctionArguments(arguments);
            return;
         }

         this.functionName.setColumnName("unix_timestamp(" + arguments.get(2) + ")");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

      if (funName.equalsIgnoreCase("ADDDATE") || funName.equalsIgnoreCase("SUBDATE")) {
         String operation = "+";
         if (funName.equalsIgnoreCase("SUBDATE")) {
            operation = "-";
         }

         StringBuffer arguments = new StringBuffer();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (i_count == 1) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn && ((SelectColumn)this.functionArguments.elementAt(i_count)).toString().trim().replaceAll("\\(", "").toLowerCase().startsWith("interval")) {
                  arguments.append(") " + operation + " ");
               } else {
                  arguments.append(") " + operation + " interval '1' day * ");
               }
            } else if (i_count == 0) {
               arguments.append("timestamp(");
            }

            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               if (i_count == 0) {
                  this.handleStringLiteralForDateTime(from_sqs, 0, true);
               }

               arguments.append("(" + ((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs) + ")");
            } else {
               arguments.append(this.functionArguments.elementAt(i_count));
            }
         }

         this.functionName.setColumnName("(" + arguments + ")");
      }

      if (funName.equalsIgnoreCase("weekday")) {
         arguments = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            this.handleStringLiteralForDateTime(from_sqs, 0, true);
            arguments.append(((SelectColumn)this.functionArguments.elementAt(0)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.append(this.functionArguments.elementAt(0));
         }

         String argument = "mod(int(dayofweek(" + arguments + ")+5),7)";
         this.functionName.setColumnName(argument);
      }

      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String qry = "";
      boolean isTimeDiffFnArg = false;
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (this.functionName.getColumnName().equalsIgnoreCase("timestamp")) {
               if (i_count == 0) {
                  this.handleStringLiteralForDateTime(from_sqs, i_count, true);
                  ((SelectColumn)this.functionArguments.elementAt(i_count)).modifyCurrentTimeAsCurrentTimestamp(from_sqs);
               } else {
                  this.handleStringLiteralForTime(from_sqs, i_count, true, true);
               }
            } else {
               this.handleStringLiteralForTime(from_sqs, i_count, true, true);
            }

            if (i_count == 0 && (this.getFunctionNameAsAString().equalsIgnoreCase("time_to_sec") || this.getFunctionNameAsAString().equalsIgnoreCase("microsecond"))) {
               ((SelectColumn)this.functionArguments.elementAt(i_count)).castCurrentDateAsTimeStamp(from_sqs);
               if (this.getFunctionNameAsAString().equalsIgnoreCase("time_to_sec")) {
                  Vector colExp = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression();
                  if (colExp.size() == 1 && colExp.get(0) != null && colExp.get(0) instanceof FunctionCalls && ((FunctionCalls)colExp.get(0)).getFunctionNameAsAString() != null && ((FunctionCalls)colExp.get(0)).getFunctionNameAsAString().equalsIgnoreCase("TIMEDIFF")) {
                     FunctionCalls ts = (FunctionCalls)colExp.get(0);
                     ts.setCastTimeDiffToText(false);
                     isTimeDiffFnArg = true;
                  }
               }
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("microsecond")) {
         qry = " (cast(" + extract.applyDatePartOrExtractWrapper(from_sqs, "MICROSECONDS", "(" + arguments.get(0) + ")::time") + " as int) %1000000) ";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("timestamp")) {
         if (arguments.size() == 1) {
            if (isPostgreLiveDbs) {
               qry = " (" + arguments.get(0) + " :: timestamp) ";
            } else {
               qry = "CAST(" + arguments.get(0) + " AS TIMESTAMP)";
               if (from_sqs != null && from_sqs.getBooleanValues("use.udf.functions.for.date.time")) {
                  qry = "TO_TIMESTAMP(" + arguments.get(0) + ")";
               }
            }
         } else if (isPostgreLiveDbs) {
            qry = " (" + arguments.get(0) + " :: timestamp + " + arguments.get(1) + " ::time)";
         } else {
            qry = "( TO_TIMESTAMP(" + arguments.get(0) + ") + (INTERVAL '1' SECOND * CAST(" + extract.applyDatePartOrExtractWrapper(from_sqs, "EPOCH", "(" + arguments.get(1) + ")::time") + " AS INTEGER)) )";
            if (from_sqs != null && from_sqs.getBooleanValues("use.udf.functions.for.date.time")) {
               qry = "DATE_ADD(" + arguments.get(0) + ", TO_TIME_UDF(" + arguments.get(1).toString() + "))";
            }
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("timediff")) {
         if (from_sqs != null && isPostgreLiveDbs) {
            String secdiff;
            if (from_sqs.isAmazonRedShift()) {
               secdiff = "DATEDIFF(second,CAST( " + arguments.get(1).toString() + " AS DATETIME),CAST( " + arguments.get(0).toString() + " AS DATETIME))";
               qry = "CASE WHEN ( " + secdiff + " > 35999 OR ( " + secdiff + " < 0 AND " + secdiff + " < -35999)) THEN CAST( " + secdiff + " / 3600 AS VARCHAR) ELSE ('0'+CAST( " + secdiff + " / 3600 AS VARCHAR)) END +':'+ RIGHT('0' + CAST(( abs( " + secdiff + " ) / 60) % 60 AS VARCHAR),2) + ':' + RIGHT('0' + CAST( abs( " + secdiff + " ) % 60 AS VARCHAR),2)";
            } else if (from_sqs.isVerticaDb()) {
               secdiff = "DATEDIFF(second,CAST( " + arguments.get(1).toString() + " AS DATETIME),CAST( " + arguments.get(0).toString() + " AS DATETIME))";
               qry = "CASE WHEN ( " + secdiff + " > 35999 OR ( " + secdiff + " < 0 AND " + secdiff + " < -35999)) THEN CAST(CAST( " + secdiff + " // 3600 AS INT)AS VARCHAR) ELSE ('0' || CAST( " + secdiff + " // 3600 AS VARCHAR)) END || ':' || RIGHT('0' || CAST(( abs( " + secdiff + " ) // 60) % 60 AS VARCHAR),2) || ':' || RIGHT('0' || CAST( abs( " + secdiff + " ) % 60 AS VARCHAR),2)";
            } else {
               secdiff = ((SelectColumn)arguments.get(0)).getColumnExpression().size() > 0 && ((SelectColumn)arguments.get(0)).getColumnExpression().get(0) instanceof TableColumn ? "CAST(" + ((SelectColumn)arguments.get(0)).getColumnExpression().get(0).toString() + " AS TIMESTAMP)" : arguments.get(0).toString();
               String secondArg = ((SelectColumn)arguments.get(1)).getColumnExpression().size() > 0 && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof TableColumn ? "CAST(" + ((SelectColumn)arguments.get(1)).getColumnExpression().get(0).toString() + " AS TIMESTAMP)" : arguments.get(1).toString();
               qry = "CAST(INTERVAL '1 SECOND' * (CAST(EXTRACT(EPOCH FROM (" + secdiff + ")) AS INTEGER) - CAST(EXTRACT(EPOCH FROM (" + secondArg + ")) AS INTEGER)) AS TEXT)";
            }
         } else if (from_sqs != null && from_sqs.getBooleanValues("use.udf.functions.for.date.time")) {
            qry = "CAST(INTERVAL '1' SECOND * (CAST(DATE_PART('EPOCH' , TO_TIME_UDF(" + arguments.get(0) + ")) AS INTEGER) - CAST(" + extract.applyDatePartOrExtractWrapper(from_sqs, "EPOCH", "TO_TIME_UDF(" + arguments.get(1) + ")") + " AS INTEGER)) AS TEXT)";
         } else if (from_sqs != null && from_sqs.getBooleanValues("can.use.udf.for.time.function")) {
            if (this.castTimeDiffToText()) {
               qry = "CAST( (TO_INTERVAL_UDF(" + arguments.get(0) + ") - TO_INTERVAL_UDF(" + arguments.get(1) + ")) AS TEXT)";
            } else {
               qry = "(TO_INTERVAL_UDF(" + arguments.get(0) + ") - TO_INTERVAL_UDF(" + arguments.get(1) + ")) ";
            }
         } else {
            qry = "CAST(INTERVAL '1' SECOND * (CAST(" + extract.applyDatePartOrExtractWrapper(from_sqs, "EPOCH", "(" + arguments.get(0) + ")::time") + " AS INTEGER) - CAST(" + extract.applyDatePartOrExtractWrapper(from_sqs, "EPOCH", "(" + arguments.get(1) + ")::time") + " AS INTEGER)) AS TEXT)";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("time_to_sec")) {
         if (from_sqs != null && isPostgreLiveDbs) {
            if (from_sqs.isAmazonRedShift()) {
               qry = "DATEDIFF(SECOND, ((" + arguments.get(0).toString() + " :: date) || ' 0:00:00')::timestamp , " + arguments.get(0).toString() + ")";
            } else {
               qry = "EXTRACT(EPOCH FROM  to_char(" + arguments.get(0).toString() + ",'HH24:MI:SS')::time)::int";
            }
         } else {
            qry = extract.applyDatePartOrExtractWrapper(from_sqs, "EPOCH", "(" + arguments.get(0) + ")::time") + "::int";
            if (from_sqs != null && from_sqs.getBooleanValues("use.udf.functions.for.date.time")) {
               qry = "TIME_TO_SEC(" + arguments.get(0) + ")";
            } else if (from_sqs != null && from_sqs.getBooleanValues("can.use.udf.for.time.function")) {
               qry = extract.applyDatePartOrExtractWrapper(from_sqs, "EPOCH", "TO_INTERVAL_UDF(" + arguments.get(0) + ")") + "::int";
               if (isTimeDiffFnArg) {
                  qry = extract.applyDatePartOrExtractWrapper(from_sqs, "EPOCH", "(" + arguments.get(0) + ")") + "::int";
               }
            }
         }
      } else {
         qry = extract.applyDatePartOrExtractWrapper(from_sqs, "EPOCH", " interval " + arguments.get(0));
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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

      String funName = this.functionName.getColumnName().trim();
      String secdiff;
      if (funName.equalsIgnoreCase("time_to_sec")) {
         secdiff = "cast(DATEDIFF(SECOND, CONVERT(date, " + arguments.get(0).toString() + ")," + arguments.get(0).toString() + ") as BIGINT)";
         this.functionName.setColumnName(secdiff);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("timestamp")) {
         secdiff = "CONVERT(datetime, " + arguments.get(0).toString() + ",102)";
         this.functionName.setColumnName(secdiff);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("timediff")) {
         secdiff = "DATEDIFF(second,CAST( " + arguments.get(1).toString() + " AS DATETIME),CAST( " + arguments.get(0).toString() + " AS DATETIME))";
         String qry = "CASE WHEN ( " + secdiff + " > 35999 OR ( " + secdiff + " < 0 AND " + secdiff + " < -35999)) THEN CAST( " + secdiff + " / 3600 AS VARCHAR) ELSE ('0'+CAST( " + secdiff + " / 3600 AS VARCHAR)) END +':'+ RIGHT('0' + CAST(( abs( " + secdiff + " ) / 60) % 60 AS VARCHAR),2) + ':' + RIGHT('0' + CAST( abs( " + secdiff + " ) % 60 AS VARCHAR),2)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }

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

      if (from_sqs != null && from_sqs.isHyperSql()) {
         String dateString = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
         String qry;
         if (this.functionName.getColumnName().equalsIgnoreCase("timediff")) {
            String dateString1 = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(1).toString());
            qry = " CASE WHEN CAST(" + dateString + " AS TIME) >= CAST(" + dateString1 + " AS TIME) THEN LPAD(CAST((TIMESTAMP '1970-01-01 00:00:00' + (" + dateString + " - " + dateString1 + ")) AS TIME),8,'0') ELSE '-' || LPAD(CAST((TIMESTAMP '1970-01-01 00:00:00' + (" + dateString1 + " - " + dateString + ")) AS TIME),8,'0') END ";
         } else {
            qry = " DATEDIFF('SECOND', TRUNC(" + dateString + ",'DD') , CAST(" + dateString + " AS TIMESTAMP)) ";
         }

         this.functionName.setColumnName(qry);
         this.setAsDatatype((String)null);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.functionName.setColumnName("cast");
         this.setAsDatatype("as");
         Vector finalArguments = new Vector();
         SelectColumn sc_timeDiff = new SelectColumn();
         FunctionCalls fn_timeDiff = new FunctionCalls();
         TableColumn tc_timeDiff = new TableColumn();
         tc_timeDiff.setColumnName("timediff");
         fn_timeDiff.setFunctionName(tc_timeDiff);
         Vector vc_timeDiffIn = new Vector();
         Vector vc_timeDiffOut = new Vector();
         vc_timeDiffIn.addElement(arguments.get(0));
         vc_timeDiffIn.addElement(arguments.get(1));
         fn_timeDiff.setFunctionArguments(vc_timeDiffIn);
         vc_timeDiffOut.addElement(fn_timeDiff);
         sc_timeDiff.setColumnExpression(vc_timeDiffOut);
         finalArguments.addElement(sc_timeDiff);
         finalArguments.add("char");
         this.setFunctionArguments(finalArguments);
      }
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

      String qry;
      if (this.functionName.toString().equalsIgnoreCase("time_to_sec")) {
         qry = "DATEDIFF(SECOND, CAST(DATE(" + arguments.get(0).toString() + ") AS DATETIME) ,CAST(" + arguments.get(0).toString() + " AS DATETIME))";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("timediff")) {
         qry = "";
         qry = "DATEDIFF(second,CAST( " + arguments.get(1).toString() + " AS DATETIME),CAST( " + arguments.get(0).toString() + " AS DATETIME))";
         String qry = "CASE WHEN ( " + qry + " > 35999 OR ( " + qry + " < 0 AND " + qry + " < -35999)) THEN CAST( (" + qry + " / 3600)::INT AS VARCHAR) ELSE ('0'||CAST( (" + qry + " / 3600)::INT AS VARCHAR)) END ||':'|| RIGHT('0' || CAST((( abs( " + qry + " ) / 60) % 60)::INT AS VARCHAR),2) || ':' || RIGHT('0' || CAST( (abs( " + qry + " ) % 60)::INT AS VARCHAR),2)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("timestamp")) {
         qry = "CAST( " + arguments.get(0).toString() + " AS TIMESTAMP)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
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

      String dateString = StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString());
      arguments.set(0, dateString);
      String secdiff;
      if (this.functionName.toString().equalsIgnoreCase("time_to_sec")) {
         secdiff = "CAST(HOUR(" + dateString + ")*60*60 + MINUTE(" + dateString + ")*60 +SECOND(" + dateString + ") AS BIGINT)";
         this.functionName.setColumnName(secdiff);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("timediff")) {
         secdiff = "";
         secdiff = "SECONDS_BETWEEN(CAST( " + StringFunctions.handleLiteralStringDateForSapHana(arguments.get(1).toString()) + " AS TIMESTAMP),CAST( " + StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString()) + " AS TIMESTAMP))";
         String qry = "CASE WHEN ( " + secdiff + " > 35999 OR ( " + secdiff + " < 0 AND " + secdiff + " < -35999)) THEN CAST( CAST((" + secdiff + " / 3600) AS INT) AS VARCHAR) ELSE ('0'||CAST( CAST((abs(" + secdiff + ")/ 3600) AS INT) AS VARCHAR)) END ||':'|| RIGHT('0' || CAST( CAST(MOD(( abs( " + secdiff + " ) / 60) , 60) AS INT) AS VARCHAR),2) || ':' || RIGHT('0' || CAST( CAST(MOD(abs( " + secdiff + " ) , 60) AS INT) AS VARCHAR),2)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("timestamp")) {
         this.functionName.setColumnName("TO_TIMESTAMP(" + dateString + ")");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }
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

      String dateString = StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString());
      arguments.set(0, dateString);
      String secdiff;
      if (this.functionName.toString().equalsIgnoreCase("time_to_sec")) {
         secdiff = "(cast(strftime('%H'," + dateString + ") as integer)*60*60+cast(strftime('%M'," + dateString + ") as integer)*60+cast(strftime('%S'," + dateString + ") as integer))";
         this.functionName.setColumnName(secdiff);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("timediff")) {
         secdiff = "cast(round((julianday(" + arguments.get(0) + ") - julianday(" + arguments.get(1) + "))*24*60*60) as integer)";
         String qry = "CASE WHEN ( " + secdiff + " > 35999 OR ( " + secdiff + " < 0 AND " + secdiff + " < -35999)) THEN CAST( CAST((" + secdiff + " / 3600) AS INT) AS VARCHAR) ELSE ('0'|| CAST( CAST((abs(" + secdiff + ")/ 3600) AS INT) AS VARCHAR)) END ||':'|| (CASE WHEN (LENGTH('0' || CAST( CAST(MOD(( abs( " + secdiff + " ) / 60) , 60) AS INT) AS VARCHAR)) <= 2) THEN SUBSTR('0' || CAST( CAST(MOD(( abs( " + secdiff + " ) / 60) , 60) AS INT) AS VARCHAR),1) ELSE SUBSTR('0' || CAST( CAST(MOD(( abs( " + secdiff + " ) / 60) , 60) AS INT) AS VARCHAR),-2) END) || ':' || (CASE WHEN (LENGTH('0' || CAST( CAST(MOD(abs( " + secdiff + " ) , 60) AS INT) AS VARCHAR)) <= 2) THEN SUBSTR('0' || CAST( CAST(MOD(abs( " + secdiff + " ) , 60) AS INT) AS VARCHAR),1) ELSE SUBSTR('0' || CAST( CAST(MOD(abs( " + secdiff + " ) , 60) AS INT) AS VARCHAR),-2) END)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("timestamp")) {
         this.functionName.setColumnName("DATETIME(" + dateString + ")");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }
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

      String dateString = StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString());
      String qry;
      if (this.functionName.toString().equalsIgnoreCase("time_to_sec")) {
         qry = "DATE_DIFF('SECOND', DATE(" + dateString + ") , CAST(" + dateString + " AS TIMESTAMP) )";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("timediff")) {
         qry = "";
         qry = "DATE_DIFF('SECOND',CAST( " + StringFunctions.handleLiteralStringDateForAthena(arguments.get(1).toString()) + " AS TIMESTAMP),CAST( " + dateString + " AS TIMESTAMP))";
         String qry = "CASE WHEN ( " + qry + " > 35999 OR ( " + qry + " < 0 AND " + qry + " < -35999)) THEN CAST( CAST((" + qry + " / 3600) AS INT) AS VARCHAR) ELSE ('0'||CAST( CAST((" + qry + " / 3600) AS INT) AS VARCHAR)) END ||':'|| SUBSTR('0' || CAST( CAST((( abs( " + qry + " ) / 60) % 60) AS INT) AS VARCHAR),-2) || ':' || SUBSTR('0' || CAST( CAST((abs( " + qry + " ) % 60) AS INT) AS VARCHAR),-2)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("timestamp")) {
         qry = "CAST( " + dateString + " AS TIMESTAMP)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String datestring = isdenodo ? StringFunctions.handleLiteralStringDateForDenodo(arguments.get(0).toString()) : StringFunctions.handleLiteralStringDateForOracle(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("timestamp")) {
         qry = "CAST(" + datestring + " as timestamp)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("time_to_sec")) {
         if (isdenodo) {
            qry = "GETSECOND(cast(" + datestring + " as time))+(GETMINUTE(cast(" + datestring + " as time))*60)+(GETHOUR(cast(" + datestring + " as time))*60*60)";
         } else {
            qry = "to_char(" + datestring + ",'FMHH24')*60*60 + to_char(" + datestring + ",'FMMI')*60 +to_char(" + datestring + ",'FMSS')";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("timediff")) {
         String datestring2 = isdenodo ? StringFunctions.handleLiteralStringDateForDenodo(arguments.get(1).toString()) : StringFunctions.handleLiteralStringDateForOracle(arguments.get(1).toString());
         if (isdenodo) {
            String diffInSecQry = "ADDSECOND(cast('1970-01-01' as timestamp),(round(trunc((GETTIMEINMILLIS(" + datestring + ")-GETTIMEINMILLIS(" + datestring2 + "))/(1000.0)))))";
            qry = "(((GETDAY(" + diffInSecQry + ")-1) *24)+(GETHOUR(" + diffInSecQry + "))) || ':' || FORMATDATE('mm:ss',TO_TIME('mm:ss',(GETMINUTE(" + diffInSecQry + ")) || ':' || (GETSECOND(" + diffInSecQry + "))))";
         } else {
            qry = "((TO_CHAR(DATE '1970-01-01' + ( 1 / 24 / 60 / 60) *(round((cast (" + datestring + " as date) - cast(" + datestring2 + " as date))*86400)),'dd')-1)*24) +TO_CHAR(DATE '1970-01-01' + ( 1 / 24 / 60 / 60) *(round((cast (" + datestring + " as date) - cast(" + datestring2 + " as date))*86400)),'hh24') ||':'||TO_CHAR(DATE '1970-01-01' + ( 1 / 24 / 60 / 60) *(round((cast (" + datestring + " as date) - cast(" + datestring2 + " as date))*86400)),'mi:ss')";
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("timestamp")) {
         if (arguments.size() == 1) {
            qry = " CAST(" + arguments.get(0) + " AS timestamp) ";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("timediff")) {
         qry = "TIME(TIMESTAMP_SECONDS(ABS(TIME_DIFF(TIME(CAST( " + arguments.get(0) + " AS DATETIME)),TIME(CAST( " + arguments.get(1) + " AS DATETIME)), SECOND))))";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("time_to_sec")) {
         qry = "TIMESTAMP_DIFF(CAST(" + arguments.get(0).toString() + " AS TIMESTAMP), CAST(DATE( CAST(" + arguments.get(0).toString() + " AS TIMESTAMP )) AS TIMESTAMP) ,SECOND)";
      } else {
         qry = "EXTRACT(EPOCH FROM interval " + arguments.get(0) + ")";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("timestamp")) {
         if (arguments.size() == 1) {
            qry = " CAST(" + arguments.get(0) + " AS timestamp) ";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("timediff")) {
         String secdiff = "";
         secdiff = "TIMESTAMPDIFF(2,CHAR( CAST(" + arguments.get(0).toString() + "AS TIMESTAMP) - CAST(" + arguments.get(1).toString() + " AS TIMESTAMP )))";
         qry = "CASE WHEN ( " + secdiff + " > 35999 OR ( " + secdiff + " < 0 AND " + secdiff + " < -35999)) THEN CAST( (" + secdiff + " / 3600)::INT AS VARCHAR) ELSE ('0'||CAST( (" + secdiff + " / 3600)::INT AS VARCHAR)) END ||':'|| RIGHT('0' || CAST((( abs( " + secdiff + " ) / 60) % 60)::INT AS VARCHAR),2) || ':' || RIGHT('0' || CAST( (abs( " + secdiff + " ) % 60)::INT AS VARCHAR),2)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("time_to_sec")) {
         qry = "TIMESTAMPDIFF(2,CHAR(" + arguments.get(0).toString() + " - DATE( CAST(" + arguments.get(0).toString() + " AS TIMESTAMP ))))";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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

      String dateString = StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString());
      String qry;
      if (this.functionName.toString().equalsIgnoreCase("time_to_sec")) {
         qry = " (TO_CHAR(" + dateString + ",'%H')*60*60 + TO_CHAR(" + dateString + ",'%M')*60 +TO_CHAR(" + dateString + ",'%S'))::BIGINT ";
      } else if (this.functionName.toString().equalsIgnoreCase("timediff")) {
         String dateString1 = StringFunctions.handleLiteralStringDateForInformix(arguments.get(1).toString());
         String secdiff = "(((EXTEND(" + dateString + ", YEAR TO DAY) - EXTEND(" + dateString1 + ",YEAR TO DAY))::INTERVAL DAY(9) TO DAY)::CHAR(15)::DECIMAL(18,5) * 86400 + ((EXTEND(" + dateString + ", HOUR TO FRACTION(5)) - EXTEND(" + dateString1 + ", HOUR TO FRACTION(5)))::INTERVAL SECOND(6) TO FRACTION(5))::CHAR(15)::DECIMAL(18,5))";
         qry = " CASE WHEN ( " + secdiff + " > 35999 OR ( " + secdiff + " < 0 AND " + secdiff + " < -35999)) THEN (" + secdiff + " / 3600)::INT::LVARCHAR ELSE ((CASE WHEN " + secdiff + " < 0 THEN '-0' ELSE '0' END)||ABS((" + secdiff + " / 3600))::INT::LVARCHAR) END ||':'|| RIGHT('0' || MOD(( abs( " + secdiff + " ) / 60), 60)::INT::LVARCHAR,2) || ':' || RIGHT('0' || MOD(abs( " + secdiff + " ), 60)::INT::LVARCHAR,2) ";
      } else {
         if (!this.functionName.toString().equalsIgnoreCase("timestamp")) {
            this.setFunctionArguments(arguments);
            return;
         }

         qry = "EXTEND( " + dateString + " ,YEAR TO FRACTION) ";
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

      String funName = this.functionName.getColumnName().trim();
      String secdiff;
      if (funName.equalsIgnoreCase("time_to_sec")) {
         secdiff = "CLng(HOUR(" + arguments.get(0).toString() + ")*3600+MINUTE(" + arguments.get(0).toString() + ")*60+SECOND(" + arguments.get(0).toString() + "))";
         this.functionName.setColumnName(secdiff);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("timestamp")) {
         secdiff = "CDate(" + arguments.get(0).toString() + ")";
         this.functionName.setColumnName(secdiff);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("timediff")) {
         secdiff = "DATEDIFF('s',CDate( " + arguments.get(1).toString() + "),CDate( " + arguments.get(0).toString() + "))";
         String qry = "CStr(Iif(( " + secdiff + " > 35999 OR ( " + secdiff + " < 0 AND " + secdiff + " < -35999)) , fix( " + secdiff + " / 3600) , ('0'+fix( " + secdiff + " / 3600)))) +':'+ RIGHT('0' + CStr(fix( Abs( " + secdiff + " ) / 60) mod 60),2) + ':' + RIGHT('0' + CStr( Abs( " + secdiff + " ) mod 60),2)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }

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

      if (this.functionName.getColumnName().equalsIgnoreCase("timestamp")) {
         this.setFunctionArguments(arguments);
      } else {
         String dateString = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
         String qry;
         if (this.functionName.getColumnName().equalsIgnoreCase("timediff")) {
            String dateString1 = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(1).toString());
            qry = " CASE WHEN CAST(" + dateString + " AS TIME) >= CAST(" + dateString1 + " AS TIME) THEN LPAD(CAST((TIMESTAMP '1970-01-01 00:00:00' + (" + dateString + " - " + dateString1 + ")) AS TIME),8,'0') ELSE '-' || LPAD(CAST((TIMESTAMP '1970-01-01 00:00:00' + (" + dateString1 + " - " + dateString + ")) AS TIME),8,'0') END ";
         } else {
            qry = " DATEDIFF('s', TRUNC(" + dateString + ",'DD') , CAST(" + dateString + " AS TIMESTAMP)) ";
         }

         this.functionName.setColumnName(qry);
         this.setAsDatatype((String)null);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }
}
