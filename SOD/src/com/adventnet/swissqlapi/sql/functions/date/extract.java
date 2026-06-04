package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.util.Vector;

public class extract extends FunctionCalls {
   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      if (from_sqs.isMSAzure()) {
         if (this.functionName.toString().equalsIgnoreCase("minute")) {
            arguments.addElement("mi");
         }

         if (this.functionName.toString().equalsIgnoreCase("quarter")) {
            arguments.addElement("q");
         }

         if (this.functionName.toString().equalsIgnoreCase("dayofyear")) {
            arguments.addElement("dy");
         }

         if (this.functionName.toString().equalsIgnoreCase("dayofmonth")) {
            arguments.addElement("d");
         }

         if (this.functionName.toString().equalsIgnoreCase("dayofquarter")) {
            arguments.addElement("q");
         }

         if (this.functionName.toString().equalsIgnoreCase("MICROSECOND")) {
            arguments.addElement("mcs");
         }
      }

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selCol = ((SelectColumn)this.functionArguments.get(i_count)).toMSSQLServerSelect(to_sqs, from_sqs);
            arguments.addElement(selCol);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (from_sqs.isMSAzure()) {
         String arg;
         if (this.functionName.toString().equalsIgnoreCase("minute")) {
            arg = "CAST( " + arguments.get(1) + " AS DATETIME)";
            arguments.set(1, arg);
         }

         if (this.functionName.toString().equalsIgnoreCase("quarter")) {
            arg = "CAST(DATEPART( " + arguments.get(0).toString() + " , " + arguments.get(1).toString() + ") AS INT)";
            this.functionName.setColumnName(arg);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
            return;
         }

         if (this.functionName.toString().equalsIgnoreCase("dayname")) {
            arg = "FORMAT(CAST(" + arguments.get(0).toString() + " AS DATE),'dddd')";
            this.functionName.setColumnName(arg);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
            return;
         }

         if (this.functionName.getColumnName().equalsIgnoreCase("WEEKDAY")) {
            arg = "((datepart(dw," + arguments.get(0).toString() + ")+5)%7)";
            this.functionName.setColumnName(arg);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
            return;
         }

         if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null) {
            if (this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND")) {
               this.functionName.setColumnName("CAST(format(CAST(" + arguments.get(0).toString() + " AS DATETIME),'ssffffff') AS BIGINT)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.trailingString = null;
               this.fromStringInFunction = null;
               this.functionArguments = new Vector();
               return;
            }

            if (this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND")) {
               this.functionName.setColumnName("CAST(format(CAST(" + arguments.get(0).toString() + " AS DATETIME),'mmssffffff') AS BIGINT)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.trailingString = null;
               this.fromStringInFunction = null;
               this.functionArguments = new Vector();
               return;
            }

            if (this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND")) {
               this.functionName.setColumnName("CAST(format(CAST(" + arguments.get(0).toString() + " AS DATETIME),'HHmmssffffff') AS BIGINT)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.trailingString = null;
               this.fromStringInFunction = null;
               this.functionArguments = new Vector();
               return;
            }

            if (this.trailingString.equalsIgnoreCase("MINUTE_SECOND")) {
               this.functionName.setColumnName("CAST(format(CAST(" + arguments.get(0).toString() + " AS DATETIME),'mmss') AS INT)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.trailingString = null;
               this.fromStringInFunction = null;
               this.functionArguments = new Vector();
               return;
            }

            if (this.trailingString.equalsIgnoreCase("HOUR_SECOND")) {
               this.functionName.setColumnName("CAST(format(CAST(" + arguments.get(0).toString() + " AS DATETIME),'HHmmss') AS BIGINT)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.trailingString = null;
               this.fromStringInFunction = null;
               this.functionArguments = new Vector();
               return;
            }

            if (this.trailingString.equalsIgnoreCase("HOUR_MINUTE")) {
               this.functionName.setColumnName("CAST(format(CAST(" + arguments.get(0).toString() + " AS DATETIME),'HHmm') AS INT)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.trailingString = null;
               this.fromStringInFunction = null;
               this.functionArguments = new Vector();
               return;
            }

            if (this.trailingString.equalsIgnoreCase("DAY_MICROSECOND")) {
               this.functionName.setColumnName("CAST(format(CAST(" + arguments.get(0).toString() + " AS DATETIME),'ddHHmmssffffff') AS BIGINT)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.trailingString = null;
               this.fromStringInFunction = null;
               this.functionArguments = new Vector();
               return;
            }

            if (this.trailingString.equalsIgnoreCase("DAY_SECOND")) {
               this.functionName.setColumnName("CAST(format(CAST(" + arguments.get(0).toString() + " AS DATETIME),'ddHHmmss') AS BIGINT)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.trailingString = null;
               this.fromStringInFunction = null;
               this.functionArguments = new Vector();
               return;
            }

            if (this.trailingString.equalsIgnoreCase("DAY_MINUTE")) {
               this.functionName.setColumnName("CAST(format(CAST(" + arguments.get(0).toString() + " AS DATETIME),'ddHHmm') AS INT)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.trailingString = null;
               this.fromStringInFunction = null;
               this.functionArguments = new Vector();
               return;
            }

            if (this.trailingString.equalsIgnoreCase("DAY_HOUR")) {
               this.functionName.setColumnName("CAST(format(CAST(" + arguments.get(0).toString() + " AS DATETIME),'ddHH') AS INT)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.trailingString = null;
               this.fromStringInFunction = null;
               this.functionArguments = new Vector();
               return;
            }

            if (this.trailingString.equalsIgnoreCase("YEAR_MONTH")) {
               this.functionName.setColumnName("CAST(format(CAST(" + arguments.get(0).toString() + " AS DATETIME),'yyyyMM') AS INT)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.trailingString = null;
               this.fromStringInFunction = null;
               this.functionArguments = new Vector();
               return;
            }

            Vector newarg = new Vector();
            if (this.trailingString.equalsIgnoreCase("YEAR") || this.trailingString.equalsIgnoreCase("MONTH") || this.trailingString.equalsIgnoreCase("DAY") || this.trailingString.equalsIgnoreCase("HOUR") || this.trailingString.equalsIgnoreCase("MINUTE") || this.trailingString.equalsIgnoreCase("SECOND") || this.trailingString.equalsIgnoreCase("MICROSECOND")) {
               newarg.addElement(this.trailingString);
               newarg.addElement(arguments.get(0).toString());
               this.trailingString = null;
               this.fromStringInFunction = null;
               this.setFunctionArguments(newarg);
               this.getFunctionName().setColumnName("DATEPART");
               return;
            }
         }
      }

      this.setFunctionArguments(arguments);
      this.getFunctionName().setColumnName("DATEPART");
      if (!from_sqs.isMSAzure()) {
         this.setFromInTrim(",");
      }

   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selCol = ((SelectColumn)this.functionArguments.get(0)).toNetezzaSelect(to_sqs, from_sqs);
            if (selCol.getAliasName() != null && selCol.getAliasName().startsWith("\"")) {
               selCol.setAliasName(selCol.getAliasName().replaceAll("\"", "'"));
            }

            arguments.addElement(selCol);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String qry = "";
      String cons_val = "";
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      boolean canUseUDFFunction = from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.date.time");

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            selColumn.modifyCurrentTimeAsCurrentTimestamp(from_sqs);
            if (i_count == 0 && ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().get(0) instanceof String) {
               String dateString = ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().get(0).toString();
               if (!this.functionName.getColumnName().equalsIgnoreCase("MINUTE") && !this.functionName.getColumnName().equalsIgnoreCase("SECOND") && !this.functionName.getColumnName().equalsIgnoreCase("MICROSECOND")) {
                  if (canUseUDFFunction && this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && (this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND") || this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND") || this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND") || this.trailingString.equalsIgnoreCase("MINUTE_SECOND") || this.trailingString.equalsIgnoreCase("HOUR_SECOND") || this.trailingString.equalsIgnoreCase("HOUR_MINUTE"))) {
                     dateString = "CAST(" + this.handleStringLiteralForTime(dateString, from_sqs, true) + " AS TIMESTAMP)";
                  } else {
                     dateString = "CAST(" + this.handleStringLiteralForDateTime(dateString, from_sqs) + " AS TIMESTAMP)";
                  }
               } else {
                  dateString = "CAST(" + this.handleStringLiteralForTime(dateString, from_sqs, true) + " AS TIME)";
               }

               ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().set(0, dateString);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.get(0)).toPostgreSQLSelect(to_sqs, from_sqs));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("dayname")) {
         qry = "  to_char(" + cons_val + arguments.get(0) + ",'FMDay') ";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("DAYOFMONTH")) {
         if (from_sqs != null && from_sqs.isAmazonRedShift() && arguments.get(0).toString().equalsIgnoreCase("now()")) {
            qry = " cast(EXTRACT(DAY FROM " + cons_val + " CURRENT_DATE ) as int)";
         } else {
            qry = " cast(" + applyDatePartOrExtractWrapper(from_sqs, "DAY", arguments.get(0) + "") + " as int)";
         }

         if (canUseUDFFunction) {
            qry = "DAYOFMONTH(" + arguments.get(0).toString() + ")";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("DAYOFYEAR")) {
         qry = " cast(date_part('doy' ," + cons_val + arguments.get(0) + ") as int)";
         if (canUseUDFFunction) {
            qry = "DAYOFYEAR(" + arguments.get(0).toString() + ")";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("WEEKDAY")) {
         qry = " mod(cast(date_part('dow' ," + cons_val + arguments.get(0) + ") as int) +6, 7)";
         if (canUseUDFFunction) {
            qry = "WEEKDAY(" + arguments.get(0).toString() + ")";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("MINUTE")) {
         qry = " cast(" + applyDatePartOrExtractWrapper(from_sqs, "MINUTE", cons_val + arguments.get(0) + "") + " as int)";
         if (canUseUDFFunction) {
            qry = "MINUTE(" + arguments.get(0).toString() + ")";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("SECOND")) {
         qry = " cast(" + applyDatePartOrExtractWrapper(from_sqs, "SECOND", cons_val + arguments.get(0) + "") + " as int)";
         if (canUseUDFFunction) {
            qry = "SECOND(" + arguments.get(0).toString() + ")";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("MICROSECOND")) {
         qry = " (cast(" + applyDatePartOrExtractWrapper(from_sqs, "MICROSECOND", cons_val + arguments.get(0) + "") + " as int)%1000000)";
         if (canUseUDFFunction) {
            qry = "MICROSECOND(" + arguments.get(0).toString() + ")";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("QUARTER")) {
         qry = " cast(" + applyDatePartOrExtractWrapper(from_sqs, "QUARTER", cons_val + arguments.get(0) + "") + " as int)";
         if (canUseUDFFunction) {
            qry = "QUARTER(" + arguments.get(0).toString() + ")";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("to_days")) {
         this.functionName.setColumnName("(719528+div(" + applyDatePartOrExtractWrapper(from_sqs, "EPOCH", "( " + arguments.get(0).toString() + ")::date") + "::bigint,86400))");
         if (canUseUDFFunction) {
            qry = "TO_DAYS(" + arguments.get(0).toString() + ")";
         }

         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND")) {
         this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'SSUS')::bigint");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND")) {
         this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'MISSUS')::bigint");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND")) {
         this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'HH24MISSUS')::bigint");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("MINUTE_SECOND")) {
         this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'MISS')::int");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_SECOND")) {
         this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'HH24MISS')::int");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_MINUTE")) {
         this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'HH24MI')::int");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_MICROSECOND")) {
         this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'DDHH24MISSUS')::bigint");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_SECOND")) {
         this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'DDHH24MISS')::int");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_MINUTE")) {
         this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'DDHH24MI')::int");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_HOUR")) {
         this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'DDHH24')::int");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("YEAR_MONTH")) {
         this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'YYYYMM')::int");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("MICROSECOND")) {
         this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'US')::int");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      } else if (!canUseUDFFunction && this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null) {
         if (this.trailingString.equalsIgnoreCase("DAY") || this.trailingString.equalsIgnoreCase("HOUR") || this.trailingString.equalsIgnoreCase("MINUTE") || this.trailingString.equalsIgnoreCase("SECOND") || this.trailingString.equalsIgnoreCase("MICROSECOND") || this.trailingString.equalsIgnoreCase("MONTH") || this.trailingString.equalsIgnoreCase("YEAR") || this.trailingString.equalsIgnoreCase("WEEK") || this.trailingString.equalsIgnoreCase("QUARTER")) {
            qry = applyDatePartOrExtractWrapper(from_sqs, this.trailingString, cons_val + arguments.get(0) + "");
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.fromStringInFunction = null;
            this.trailingString = null;
            this.functionArguments = new Vector();
         }
      } else {
         if (canUseUDFFunction) {
            if (this.trailingString != null && this.trailingString.equalsIgnoreCase("YEAR")) {
               this.functionName.setColumnName("YEAR");
               this.trailingString = null;
               this.fromStringInFunction = null;
            } else if (this.trailingString != null && this.trailingString.equalsIgnoreCase("MONTH")) {
               this.functionName.setColumnName("MONTH");
               this.trailingString = null;
               this.fromStringInFunction = null;
            } else if (this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY")) {
               this.functionName.setColumnName("DAY");
               this.trailingString = null;
               this.fromStringInFunction = null;
            } else if (this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR")) {
               this.functionName.setColumnName("HOUR");
               this.trailingString = null;
               this.fromStringInFunction = null;
            } else if (this.trailingString != null && this.trailingString.equalsIgnoreCase("MINUTE")) {
               this.functionName.setColumnName("MINUTE");
               this.trailingString = null;
               this.fromStringInFunction = null;
            } else if (this.trailingString != null && this.trailingString.equalsIgnoreCase("SECOND")) {
               this.functionName.setColumnName("SECOND");
               this.trailingString = null;
               this.fromStringInFunction = null;
            } else if (this.trailingString != null && this.trailingString.equalsIgnoreCase("MICROSECOND")) {
               this.functionName.setColumnName("MICROSECOND");
               this.trailingString = null;
               this.fromStringInFunction = null;
            }
         }

         String arg = arguments.get(0).toString();
         arguments.set(0, cons_val + arg);
         this.setFunctionArguments(arguments);
      }

   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selCol = ((SelectColumn)this.functionArguments.get(0)).toTeradataSelect(to_sqs, from_sqs);
            arguments.addElement(selCol);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selCol = ((SelectColumn)this.functionArguments.get(0)).toSnowflakeSelect(to_sqs, from_sqs);
            arguments.addElement(selCol);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      String dateString = " CAST(" + arguments.get(0).toString() + " AS DATETIME) ";
      String arg;
      if (this.functionName.toString().equalsIgnoreCase("dayname")) {
         arg = "DECODE(DAYNAME( " + arguments.get(0).toString() + " ), 'Mon','Monday', 'Tue','Tuesday', 'Wed','Wednesday','Thu','Thursday','Fri','Friday','Sat','Saturday','Sun','Sunday')";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("weekday")) {
         arg = "((DAYOFWEEKISO(" + dateString + ")+6)%7)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("quarter")) {
         arg = "QUARTER(" + dateString + ")::int";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if ((!this.functionName.getColumnName().equalsIgnoreCase("extract") || this.trailingString == null || !this.trailingString.equalsIgnoreCase("MICROSECOND")) && !this.functionName.getColumnName().equalsIgnoreCase("MICROSECOND")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND")) {
            this.functionName.setColumnName("to_char(" + dateString + ",'SSFF5')::bigint");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND")) {
            this.functionName.setColumnName("to_char(" + dateString + ",'MISSFF5')::bigint");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND")) {
            this.functionName.setColumnName("to_char(" + dateString + ",'HH24MISSFF5')::bigint");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("MINUTE_SECOND")) {
            this.functionName.setColumnName("to_char(" + dateString + ",'MISS')::int");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_SECOND")) {
            this.functionName.setColumnName("to_char(" + dateString + ",'HH24MISS')::int");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_MINUTE")) {
            this.functionName.setColumnName("to_char(" + dateString + ",'HH24MI')::int");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_MICROSECOND")) {
            this.functionName.setColumnName("to_char(" + dateString + ",'DDHH24MISSFF5')::bigint");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_SECOND")) {
            this.functionName.setColumnName("to_char(" + dateString + ",'DDHH24MISS')::int");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_MINUTE")) {
            this.functionName.setColumnName("to_char(" + dateString + ",'DDHH24MI')::int");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_HOUR")) {
            this.functionName.setColumnName("to_char(" + dateString + ",'DDHH24')::int");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("YEAR_MONTH")) {
            this.functionName.setColumnName("to_char(" + dateString + ",'YYYYMM')::int");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else {
            arguments.set(0, dateString);
            this.setFunctionArguments(arguments);
         }
      } else {
         this.functionName.setColumnName("to_char(" + dateString + ",'FF6')::bigint");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      }

   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selCol = ((SelectColumn)this.functionArguments.get(0)).toSapHanaSelect(to_sqs, from_sqs);
            arguments.addElement(selCol);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String funName = this.functionName.getColumnName();
      String dateString = funName.equalsIgnoreCase("microsecond") || funName.equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.toUpperCase().contains("MICROSECOND") ? "CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)" : StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString());
      String arg;
      if (funName.equalsIgnoreCase("quarter")) {
         arg = "SUBSTRING(CAST(QUARTER(" + dateString + ") AS VARCHAR),7,1)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("dayname")) {
         arg = "LEFT(DAYNAME(" + dateString + "),1) || LCASE(SUBSTR(DAYNAME(" + dateString + "),2,LENGTH(" + dateString + ")))";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("dayofmonth")) {
         arg = "DAYOFMONTH(" + dateString + ")";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("dayofyear")) {
         arg = "DAYOFYEAR(" + dateString + ")";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("weekday")) {
         arg = "WEEKDAY(" + dateString + ")";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("hour")) {
         arg = "HOUR(" + dateString + ")";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("minute")) {
         arg = "MINUTE(" + dateString + ")";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("second")) {
         arg = "SECOND(" + dateString + ")";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("microsecond")) {
         arg = "cast(to_char(" + dateString + ",'FF6') as bigint)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("extract") && this.trailingString != null) {
         if (this.trailingString.equalsIgnoreCase("HOUR_SECOND")) {
            this.functionName.setColumnName("cast(to_char(" + dateString + ",'HH24MISS') as int)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("MINUTE_SECOND")) {
            this.functionName.setColumnName("cast(to_char(" + dateString + ",'MISS') as int)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("DAY_SECOND")) {
            this.functionName.setColumnName("cast(to_char(" + dateString + ",'DDHH24MISS') as int)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND")) {
            this.functionName.setColumnName("cast(to_char(" + dateString + ",'SSFF6') as bigint)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND")) {
            this.functionName.setColumnName("cast(to_char(" + dateString + ",'MISSFF6') as bigint)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND")) {
            this.functionName.setColumnName("cast(to_char(" + dateString + ",'HH24MISSFF6') as bigint)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("DAY_MICROSECOND")) {
            this.functionName.setColumnName("cast(to_char(" + dateString + ",'DDHH24MISSFF6') as bigint)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("HOUR_MINUTE")) {
            this.functionName.setColumnName("cast(to_char(" + dateString + ",'HH24MI') as bigint)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("DAY_MINUTE")) {
            this.functionName.setColumnName("cast(to_char(" + dateString + ",'DDHH24MI') as bigint)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("DAY_HOUR")) {
            this.functionName.setColumnName("cast(to_char(" + dateString + ",'DDHH24') as bigint)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("YEAR_MONTH")) {
            this.functionName.setColumnName("cast(to_char(" + dateString + ",'YYYYMM') as bigint)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("MICROSECOND")) {
            this.functionName.setColumnName("cast(to_char(" + dateString + ",'FF6') as bigint)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else {
            arguments.set(0, dateString);
            this.setFunctionArguments(arguments);
         }
      }

   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selCol = ((SelectColumn)this.functionArguments.get(0)).toSqliteSelect(to_sqs, from_sqs);
            arguments.addElement(selCol);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String funName = this.functionName.getColumnName();
      String dateString = funName.equalsIgnoreCase("microsecond") || funName.equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("MICROSECOND") ? arguments.get(0).toString() : StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString());
      String arg;
      if (funName.equalsIgnoreCase("quarter")) {
         arg = "(((strftime('%m'," + dateString + ")-1)/3)+1)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("dayofyear")) {
         arg = "(cast(strftime('%j'," + dateString + ") as integer))";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("dayname")) {
         arg = "(case cast(strftime('%w'," + dateString + ") as integer) when 0 then 'Sunday' when 1 then 'Monday' when 2 then 'Tuesday' when 3 then 'Wednesday' when 4 then 'Thursday' when 5 then 'Friday' when 6 then 'Saturday' end)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("dayofmonth")) {
         arg = "(cast(strftime('%d'," + dateString + ") as integer))";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("dayofyear")) {
         arg = "cast(strftime('%j'," + dateString + ") as integer)+1";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("weekday")) {
         arg = "(cast((strftime('%w'," + dateString + ")+6)%7 as integer))";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("hour")) {
         arg = "cast(strftime('%H'," + dateString + ") as integer)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("minute")) {
         arg = "cast(strftime('%M'," + dateString + ") as integer)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("second")) {
         arg = "cast(strftime('%S'," + dateString + ") as integer)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("microsecond")) {
         arg = "CAST((CASE WHEN ((SUBSTR(" + dateString + ",21) = '') || (CAST(SUBSTR(" + dateString + ",21) AS INTEGER) = 0)) THEN 0 ELSE (CASE WHEN LENGTH(SUBSTR(" + dateString + ",21))<6 THEN CAST(SUBSTR(" + dateString + ",21)*POW(10,6-LENGTH(SUBSTR(" + dateString + ",21))) AS INTEGER) ELSE SUBSTR(" + dateString + ",21) END) END) AS INTEGER)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (funName.equalsIgnoreCase("extract") && this.trailingString != null) {
         if (this.trailingString.equalsIgnoreCase("HOUR_SECOND")) {
            this.functionName.setColumnName("cast(strftime('%H%M%S'," + dateString + ") as integer)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("MINUTE_SECOND")) {
            this.functionName.setColumnName("cast(strftime('%M%S'," + dateString + ") as integer)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("DAY_SECOND")) {
            this.functionName.setColumnName("cast(strftime('%d%H%M%S'," + dateString + ") as integer)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND")) {
            this.functionName.setColumnName("CAST(cast(strftime('%S'," + dateString + ") as integer) || (CASE WHEN ((SUBSTR(" + dateString + ",21) = '') || (CAST(SUBSTR(" + dateString + ",21) AS INTEGER) = 0)) THEN '000000' ELSE (CASE WHEN LENGTH(SUBSTR(" + dateString + ",21))<6 THEN CAST(SUBSTR(" + dateString + ",21)*POW(10,6-LENGTH(SUBSTR(" + dateString + ",21))) AS INTEGER) ELSE SUBSTR(" + dateString + ",21) END) END) AS INTEGER)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND")) {
            this.functionName.setColumnName("CAST(cast(strftime('%M%S'," + dateString + ") as integer) || (CASE WHEN ((SUBSTR(" + dateString + ",21) = '') || (CAST(SUBSTR(" + dateString + ",21) AS INTEGER) = 0)) THEN '000000' ELSE (CASE WHEN LENGTH(SUBSTR(" + dateString + ",21))<6 THEN CAST(SUBSTR(" + dateString + ",21)*POW(10,6-LENGTH(SUBSTR(" + dateString + ",21))) AS INTEGER) ELSE SUBSTR(" + dateString + ",21) END) END) AS INTEGER)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND")) {
            this.functionName.setColumnName("CAST(cast(strftime('%H%M%S'," + dateString + ") as integer) || (CASE WHEN ((SUBSTR(" + dateString + ",21) = '') || (CAST(SUBSTR(" + dateString + ",21) AS INTEGER) = 0)) THEN '000000' ELSE (CASE WHEN LENGTH(SUBSTR(" + dateString + ",21))<6 THEN CAST(SUBSTR(" + dateString + ",21)*POW(10,6-LENGTH(SUBSTR(" + dateString + ",21))) AS INTEGER) ELSE SUBSTR(" + dateString + ",21) END) END) AS INTEGER)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("DAY_MICROSECOND")) {
            this.functionName.setColumnName("CAST(cast(strftime('%d%H%M%S'," + dateString + ") as integer) || (CASE WHEN ((SUBSTR(" + dateString + ",21) = '') || (CAST(SUBSTR(" + dateString + ",21) AS INTEGER) = 0)) THEN '000000' ELSE (CASE WHEN LENGTH(SUBSTR(" + dateString + ",21))<6 THEN CAST(SUBSTR(" + dateString + ",21)*POW(10,6-LENGTH(SUBSTR(" + dateString + ",21))) AS INTEGER) ELSE SUBSTR(" + dateString + ",21) END) END) AS INTEGER)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("HOUR_MINUTE")) {
            this.functionName.setColumnName("cast(strftime('%H%M'," + dateString + ") as integer)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("DAY_MINUTE")) {
            this.functionName.setColumnName("cast(strftime('%d%H%M'," + dateString + ") as integer)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("DAY_HOUR")) {
            this.functionName.setColumnName("cast(strftime('%d%H'," + dateString + ") as integer)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("YEAR_MONTH")) {
            this.functionName.setColumnName("cast(strftime('%Y%m'," + dateString + ") as integer)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("DAY")) {
            this.functionName.setColumnName("(cast(strftime('%d'," + dateString + ") as integer))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("MONTH")) {
            this.functionName.setColumnName("(cast(strftime('%m'," + dateString + ") as integer))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("YEAR")) {
            this.functionName.setColumnName("(cast(strftime('%Y'," + dateString + ") as integer))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("HOUR")) {
            this.functionName.setColumnName("(cast(strftime('%H'," + dateString + ") as integer))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("MINUTE")) {
            this.functionName.setColumnName("(cast(strftime('%M'," + dateString + ") as integer))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("SECOND")) {
            this.functionName.setColumnName("(cast(strftime('%S'," + dateString + ") as integer))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("MICROSECOND")) {
            this.functionName.setColumnName("CAST((CASE WHEN ((SUBSTR(" + dateString + ",21) = '') || (CAST(SUBSTR(" + dateString + ",21) AS INTEGER) = 0)) THEN 0 ELSE (CASE WHEN LENGTH(SUBSTR(" + dateString + ",21))<6 THEN CAST(SUBSTR(" + dateString + ",21)*POW(10,6-LENGTH(SUBSTR(" + dateString + ",21))) AS INTEGER) ELSE SUBSTR(" + dateString + ",21) END) END) AS INTEGER)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else {
            arguments.set(0, dateString);
            this.setFunctionArguments(arguments);
         }
      }

   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selCol = ((SelectColumn)this.functionArguments.get(0)).toAthenaSelect(to_sqs, from_sqs);
            arguments.addElement(selCol);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = !this.functionName.toString().equalsIgnoreCase("microsecond") ? StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString()) : "CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)";
      String arg;
      if (this.functionName.toString().equalsIgnoreCase("dayname")) {
         arg = "DATE_FORMAT( " + dateString + ",'%W' )";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("weekday")) {
         arg = "((DAY_OF_WEEK(" + dateString + ")+6)%7)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("minute")) {
         arg = "CAST(MINUTE(" + dateString + ") as INT)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("second")) {
         arg = "CAST(SECOND(" + dateString + ") as INT)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("quarter")) {
         arg = "CAST(QUARTER(" + dateString + ") as INT)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("dayofyear")) {
         arg = "CAST(DAY_OF_YEAR(" + dateString + ") as INT)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("dayofmonth")) {
         arg = "CAST(DAY_OF_MONTH(" + dateString + ") as INT)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if ((!this.functionName.getColumnName().equalsIgnoreCase("extract") || this.trailingString == null || !this.trailingString.equalsIgnoreCase("MICROSECOND")) && !this.functionName.getColumnName().equalsIgnoreCase("MICROSECOND")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND")) {
            this.functionName.setColumnName("CAST(DATE_FORMAT(" + dateString + ",'%s%f') as BIGINT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND")) {
            this.functionName.setColumnName("CAST(DATE_FORMAT(" + dateString + ",'%i%s%f') as BIGINT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND")) {
            this.functionName.setColumnName("CAST(DATE_FORMAT(" + dateString + ",'%H%i%s%f') as BIGINT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("MINUTE_SECOND")) {
            this.functionName.setColumnName("CAST(DATE_FORMAT(" + dateString + ",'%i%s') as INT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_SECOND")) {
            this.functionName.setColumnName("CAST(DATE_FORMAT(" + dateString + ",'%H%i%s') as INT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_MINUTE")) {
            this.functionName.setColumnName("CAST(DATE_FORMAT(" + dateString + ",'%H%i') as INT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_MICROSECOND")) {
            this.functionName.setColumnName("CAST(DATE_FORMAT(" + dateString + ",'%d%H%i%s%f') as BIGINT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_SECOND")) {
            this.functionName.setColumnName("CAST(DATE_FORMAT(" + dateString + ",'%d%H%i%s') as INT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_MINUTE")) {
            this.functionName.setColumnName("CAST(DATE_FORMAT(" + dateString + ",'%d%H%i') as INT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_HOUR")) {
            this.functionName.setColumnName("CAST(DATE_FORMAT(" + dateString + ",'%d%H') as INT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("YEAR_MONTH")) {
            this.functionName.setColumnName("CAST(DATE_FORMAT(" + dateString + ",'%Y%m') as INT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else {
            arguments.set(0, dateString);
            this.setFunctionArguments(arguments);
         }
      } else {
         this.functionName.setColumnName("CAST(DATE_FORMAT(" + dateString + ",'%f') as BIGINT)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      }

   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector vector = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            vector.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = vector.get(0).toString();
      String arg;
      if (this.functionName.toString().equalsIgnoreCase("dayname")) {
         arg = "WEEKDAYNAME(WEEKDAY( " + dateString + "))";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("weekday")) {
         arg = "((WEEKDAY(" + dateString + ")+5) MOD 7)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("minute")) {
         arg = "MINUTE(" + dateString + ")";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("second")) {
         arg = "SECOND(" + dateString + ")";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("quarter")) {
         arg = "DATEPART('q'," + dateString + ")";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("dayofyear")) {
         arg = "DATEPART('y'," + dateString + ")";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("dayofmonth")) {
         arg = "DAY(" + dateString + ")";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if ((!this.functionName.getColumnName().equalsIgnoreCase("extract") || this.trailingString == null || !this.trailingString.equalsIgnoreCase("MICROSECOND")) && !this.functionName.getColumnName().equalsIgnoreCase("MICROSECOND")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND")) {
            this.functionName.setColumnName("CLng(SECOND(" + dateString + ") & Iif(InStrRev(CStr(" + dateString + "),'.') > 0, CLng(Mid(CStr(" + dateString + "),InStrRev(CStr(" + dateString + "),'.')+1)*(10^(6-Len (Mid(CStr(" + dateString + "),InStrRev(CStr(" + dateString + "),'.')+1))))) , '000000'))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND")) {
            this.functionName.setColumnName("CCur(MINUTE(" + dateString + ") & Iif(SECOND(" + dateString + ") < 10 , 0 & SECOND(" + dateString + "), SECOND(" + dateString + ")) & Iif(InStrRev(CStr(" + dateString + "),'.') > 0, CLng(Mid(CStr(" + dateString + "),InStrRev(CStr(" + dateString + "),'.')+1)*(10^(6-Len (Mid(CStr(" + dateString + "),InStrRev(CStr(" + dateString + "),'.')+1))))) , '000000'))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND")) {
            this.functionName.setColumnName("CCur(HOUR(" + dateString + ") & Iif(MINUTE(" + dateString + ") < 10 , 0 & MINUTE(" + dateString + "), MINUTE(" + dateString + ")) & Iif(SECOND(" + dateString + ") < 10 , 0 & SECOND(" + dateString + "), SECOND(" + dateString + ")) & Iif(InStrRev(CStr(" + dateString + "),'.') > 0, CLng(Mid(CStr(" + dateString + "),InStrRev(CStr(" + dateString + "),'.')+1)*(10^(6-Len (Mid(CStr(" + dateString + "),InStrRev(CStr(" + dateString + "),'.')+1))))) , '000000'))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("MINUTE_SECOND")) {
            this.functionName.setColumnName("CLng(MINUTE(" + dateString + ") & Iif(SECOND(" + dateString + ") < 10 , 0 & SECOND(" + dateString + "), SECOND(" + dateString + ")))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_SECOND")) {
            this.functionName.setColumnName("CLng(HOUR(" + dateString + ") & Iif(MINUTE(" + dateString + ") < 10 , 0 & MINUTE(" + dateString + "), MINUTE(" + dateString + ")) & Iif(SECOND(" + dateString + ") < 10 , 0 & SECOND(" + dateString + "), SECOND(" + dateString + ")))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_MINUTE")) {
            this.functionName.setColumnName("CLng(HOUR(" + dateString + ") & Iif(MINUTE(" + dateString + ") < 10 , 0 & MINUTE(" + dateString + "), MINUTE(" + dateString + ")))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_MICROSECOND")) {
            this.functionName.setColumnName("CCur(DAY(" + dateString + ") & Iif(HOUR(" + dateString + ") < 10, 0 & HOUR(" + dateString + "), HOUR(" + dateString + ")) & Iif(MINUTE(" + dateString + ") < 10 , 0 & MINUTE(" + dateString + "), MINUTE(" + dateString + ")) & Iif(SECOND(" + dateString + ") < 10 , 0 & SECOND(" + dateString + "), SECOND(" + dateString + ")) & Iif(InStrRev(CStr(" + dateString + "),'.') > 0, CLng(Mid(CStr(" + dateString + "),InStrRev(CStr(" + dateString + "),'.')+1)*(10^(6-Len (Mid(CStr(" + dateString + "),InStrRev(CStr(" + dateString + "),'.')+1))))) , '000000'))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_SECOND")) {
            this.functionName.setColumnName("CLng(DAY(" + dateString + ") & Iif(HOUR(" + dateString + ") < 10, 0 & HOUR(" + dateString + "), HOUR(" + dateString + ")) & Iif(MINUTE(" + dateString + ") < 10 , 0 & MINUTE(" + dateString + "), MINUTE(" + dateString + ")) & Iif(SECOND(" + dateString + ") < 10 , 0 & SECOND(" + dateString + "), SECOND(" + dateString + ")))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_MINUTE")) {
            this.functionName.setColumnName("CLng(DAY(" + dateString + ") & Iif(HOUR(" + dateString + ") < 10, 0 & HOUR(" + dateString + "), HOUR(" + dateString + ")) & Iif(MINUTE(" + dateString + ") < 10 , 0 & MINUTE(" + dateString + "), MINUTE(" + dateString + ")))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_HOUR")) {
            this.functionName.setColumnName("CLng(DAY(" + dateString + ") & Iif(HOUR(" + dateString + ") < 10, 0 & HOUR(" + dateString + "), HOUR(" + dateString + ")))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("YEAR_MONTH")) {
            this.functionName.setColumnName("CLng(YEAR(" + dateString + ") & Iif(MONTH(" + dateString + ") < 10 , 0 & MONTH(" + dateString + "), MONTH(" + dateString + ")))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY")) {
            this.functionName.setColumnName("CLng(DAY(" + dateString + "))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("MONTH")) {
            this.functionName.setColumnName("CLng(MONTH(" + dateString + "))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("YEAR")) {
            this.functionName.setColumnName("CLng(YEAR(" + dateString + "))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR")) {
            this.functionName.setColumnName("CLng(HOUR(" + dateString + "))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("MINUTE")) {
            this.functionName.setColumnName("CLng(MINUTE(" + dateString + "))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("SECOND")) {
            this.functionName.setColumnName("CLng(SECOND(" + dateString + "))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
         } else {
            vector.set(0, dateString);
            this.setFunctionArguments(vector);
         }
      } else {
         this.functionName.setColumnName("Iif(InStrRev(CStr(" + dateString + "),'.') > 0, CLng(Mid(CStr(" + dateString + "),InStrRev(CStr(" + dateString + "),'.')+1)*(10^(6-Len (Mid(CStr(" + dateString + "),InStrRev(CStr(" + dateString + "),'.')+1))))) , 0)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionArguments = new Vector();
      }

   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String funName = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String dateString = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
      if (funName.equalsIgnoreCase("weekday")) {
         qry = " MOD(DAYOFWEEK(" + dateString + ") +5 , 7) ";
      } else if (funName.equalsIgnoreCase("dayname")) {
         qry = " DAYNAME(" + dateString + ") ";
      } else if (funName.equalsIgnoreCase("second")) {
         qry = " CAST(TO_CHAR(" + dateString + ",'SS') AS INT) ";
      } else if (funName.equalsIgnoreCase("minute")) {
         qry = "MINUTE(" + dateString + ")";
      } else if (funName.equalsIgnoreCase("quarter")) {
         qry = "QUARTER(" + dateString + ")";
      } else if (funName.equalsIgnoreCase("dayofmonth")) {
         qry = "DAYOFMONTH(" + dateString + ")";
      } else if (funName.equalsIgnoreCase("dayofyear")) {
         qry = "DAYOFYEAR(" + dateString + ")";
      } else if (funName.equalsIgnoreCase("microsecond")) {
         qry = " CAST(RPAD(EXTRACT(MICROSECOND FROM " + dateString + "), 6, '0') AS BIGINT) ";
      } else {
         if (!funName.equalsIgnoreCase("extract") || this.trailingString == null) {
            arguments.set(0, dateString);
            this.setFunctionArguments(arguments);
            return;
         }

         if (this.trailingString.equalsIgnoreCase("HOUR_SECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'HH24MISS') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("MINUTE_SECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'MISS') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("DAY_SECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'DDHH24MISS') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'SS') || RPAD(EXTRACT(MICROSECOND FROM " + dateString + "), 6, '0') AS BIGINT) ";
         } else if (this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'MISS') || RPAD(EXTRACT(MICROSECOND FROM " + dateString + "), 6, '0') AS BIGINT) ";
         } else if (this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'HH24MISS') || RPAD(EXTRACT(MICROSECOND FROM " + dateString + "), 6, '0') AS BIGINT) ";
         } else if (this.trailingString.equalsIgnoreCase("DAY_MICROSECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'DDHH24MISS') || RPAD(EXTRACT(MICROSECOND FROM " + dateString + "), 6, '0') AS BIGINT) ";
         } else if (this.trailingString.equalsIgnoreCase("HOUR_MINUTE")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'HH24MI') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("DAY_MINUTE")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'DDHH24MI') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("DAY_HOUR")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'DDHH24') AS INT) ";
         } else {
            if (!this.trailingString.equalsIgnoreCase("YEAR_MONTH")) {
               arguments.set(0, dateString);
               this.setFunctionArguments(arguments);
               return;
            }

            qry = " CAST(TO_CHAR(" + dateString + ",'YYYYMM') AS INT) ";
         }

         this.trailingString = null;
         this.fromStringInFunction = null;
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            if (i_count == 0) {
               if (!this.functionName.getColumnName().equalsIgnoreCase("MINUTE") && !this.functionName.getColumnName().equalsIgnoreCase("SECOND") && !this.functionName.getColumnName().equalsIgnoreCase("MICROSECOND") && (!this.functionName.getColumnName().equalsIgnoreCase("extract") || this.trailingString == null || !this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND") && !this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND") && !this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND") && !this.trailingString.equalsIgnoreCase("MINUTE_SECOND") && !this.trailingString.equalsIgnoreCase("HOUR_SECOND") && !this.trailingString.equalsIgnoreCase("HOUR_MINUTE"))) {
                  this.handleStringLiteralForDateTime(from_sqs, i_count, true);
               } else {
                  this.handleStringLiteralForTime(from_sqs, i_count, true, true);
               }
            }

            SelectColumn selCol = ((SelectColumn)this.functionArguments.get(0)).toVectorWiseSelect(to_sqs, from_sqs);
            arguments.addElement(selCol);
         }
      }

      this.setFunctionArguments(arguments);
      if (this.trailingString != null) {
         if (this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND")) {
            this.functionName.setColumnName("bigint(date_format(" + arguments.get(0).toString() + ",'%s%f'))");
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND")) {
            this.functionName.setColumnName("bigint(date_format(" + arguments.get(0).toString() + ",'%i%s%f'))");
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("MINUTE_SECOND")) {
            this.functionName.setColumnName("int(date_format(" + arguments.get(0).toString() + ",'%i%s'))");
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND")) {
            this.functionName.setColumnName("bigint(date_format(" + arguments.get(0).toString() + ",'%H%i%s%f'))");
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("HOUR_SECOND")) {
            this.functionName.setColumnName("int(date_format(" + arguments.get(0).toString() + ",'%H%i%s'))");
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("HOUR_MINUTE")) {
            this.functionName.setColumnName("int(date_format(" + arguments.get(0).toString() + ",'%H%i'))");
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("DAY_MICROSECOND")) {
            this.functionName.setColumnName("bigint(date_format(" + arguments.get(0).toString() + ",'%e%H%i%s%f'))");
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("DAY_SECOND")) {
            this.functionName.setColumnName("int(date_format(" + arguments.get(0).toString() + ",'%e%H%i%s'))");
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("DAY_MINUTE")) {
            this.functionName.setColumnName("int(date_format(" + arguments.get(0).toString() + ",'%e%H%i'))");
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("DAY_HOUR")) {
            this.functionName.setColumnName("int(date_format(" + arguments.get(0).toString() + ",'%e%H'))");
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.trailingString.equalsIgnoreCase("YEAR_MONTH")) {
            this.functionName.setColumnName("int(to_char(" + arguments.get(0).toString() + ",'YYYYMM'))");
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }
      }

   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selCol = ((SelectColumn)this.functionArguments.get(i_count)).toBigQuerySelect(to_sqs, from_sqs);
            arguments.addElement(selCol);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = "CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)";
      if (this.functionName.getColumnName().equalsIgnoreCase("dayname")) {
         qry = "FORMAT_TIMESTAMP( '%A', " + dateString + ")";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("DAYOFMONTH")) {
         if (arguments.get(0).toString().equalsIgnoreCase("now()")) {
            qry = " cast(EXTRACT(DAY FROM CURRENT_DATE ) as int64)";
         } else {
            qry = " cast(EXTRACT(DAY FROM " + dateString + ") as int64)";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("DAYOFYEAR")) {
         qry = "cast(FORMAT_TIMESTAMP( '%j', " + dateString + ")AS INT64)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("WEEKDAY")) {
         qry = "(CAST( FORMAT_TIMESTAMP( '%u', " + dateString + ") AS INT64) - 1)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("MINUTE")) {
         qry = " cast(EXTRACT(MINUTE FROM  " + dateString + ") as int64)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("SECOND")) {
         qry = " cast(EXTRACT(SECOND FROM  " + dateString + ") as int64)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("MICROSECOND")) {
         qry = " MOD(cast(EXTRACT(MICROSECOND FROM  " + dateString + ") as int64),1000000)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("QUARTER")) {
         qry = " cast(EXTRACT(QUARTER FROM  " + dateString + ") as int64)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("day_hour")) {
         qry = "cast(FORMAT_TIMESTAMP('%e%H'," + dateString + ") as int64)";
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("day_minute")) {
         qry = "cast(FORMAT_TIMESTAMP('%e%H%M'," + dateString + ") as int64)";
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("day_second")) {
         qry = "cast(FORMAT_TIMESTAMP('%e%H%M%S'," + dateString + ") as int64)";
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("day_microsecond")) {
         qry = "cast(concat(FORMAT_TIMESTAMP('%e%H%M%S'," + dateString + "),case when cast(extract(microsecond from " + dateString + ") as int64) = 0 then '000000' else cast(Extract(microsecond from " + dateString + ") as string) end) as int64)";
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("hour_minute")) {
         qry = "cast(FORMAT_TIMESTAMP('%H%M'," + dateString + ") as int64)";
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("hour_second")) {
         qry = "cast(FORMAT_TIMESTAMP('%H%M%S'," + dateString + ") as int64)";
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("hour_microsecond")) {
         qry = "cast(concat(FORMAT_TIMESTAMP('%H%M%S'," + dateString + "),case when cast(extract(microsecond from " + dateString + ") as int64) = 0 then '000000' else cast(Extract(microsecond from " + dateString + ") as string) end) as int64)";
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("minute_second")) {
         qry = "cast(FORMAT_TIMESTAMP('%M%S'," + dateString + ") as int64)";
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("minute_microsecond")) {
         qry = "cast(concat(FORMAT_TIMESTAMP('%M%S'," + dateString + "),case when cast(extract(microsecond from " + dateString + ") as int64) = 0 then '000000' else cast(Extract(microsecond from " + dateString + ") as string) end) as int64)";
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("second_microsecond")) {
         qry = "cast(concat(extract(second from " + dateString + "),case when cast(extract(microsecond from " + dateString + ") as int64) = 0 then '000000' else cast(Extract(microsecond from " + dateString + ") as string) end) as int64)";
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("year_month")) {
         qry = "cast(FORMAT_TIMESTAMP('%Y%m'," + dateString + ") as int64)";
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         arguments.set(0, dateString);
         this.setFunctionArguments(arguments);
      }

   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      String format;
      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (!isdenodo && i_count == 0 && ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().get(0) instanceof String) {
               format = ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().get(0).toString();
               format = "TO_TIMESTAMP(" + format + ",'FMYYYY-FMMM-FMDD FMHH24:FMMI:FMSS.FMFF')";
               ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().set(0, format);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.get(0)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      String funName = this.functionName.getColumnName();
      if (funName.equalsIgnoreCase("extract") && this.trailingString != null) {
         format = "";
         if (this.trailingString.equalsIgnoreCase("HOUR") || this.trailingString.equalsIgnoreCase("MINUTE") || this.trailingString.equalsIgnoreCase("SECOND") || this.trailingString.equalsIgnoreCase("DAY") || this.trailingString.equalsIgnoreCase("MONTH") || this.trailingString.equalsIgnoreCase("YEAR")) {
            return;
         }

         if (this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND")) {
            format = isdenodo ? "'ssSSSSSS'" : "'FMSSFMFF6'";
         } else if (this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND")) {
            format = isdenodo ? "'mmssSSSSSS'" : "'FMMIFMSSFF6'";
         } else if (this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND")) {
            format = isdenodo ? "'HHmmssSSSSSS'" : "'FMHH24FMMISSFF6'";
         } else if (this.trailingString.equalsIgnoreCase("MINUTE_SECOND")) {
            format = isdenodo ? "'mmss'" : "'FMMIFMSS'";
         } else if (this.trailingString.equalsIgnoreCase("HOUR_SECOND")) {
            format = isdenodo ? "'HHmmss'" : "'FMHH24FMMISS'";
         } else if (this.trailingString.equalsIgnoreCase("HOUR_MINUTE")) {
            format = isdenodo ? "'HHmm'" : "'FMHH24FMMI'";
         } else if (this.trailingString.equalsIgnoreCase("DAY_MICROSECOND")) {
            format = isdenodo ? "'ddHHmmssSSSSSS'" : "'FMDDFMHH24MISSFF6'";
         } else if (this.trailingString.equalsIgnoreCase("DAY_SECOND")) {
            format = isdenodo ? "'ddHHmmss'" : "'FMDDFMHH24MISS'";
         } else if (this.trailingString.equalsIgnoreCase("DAY_MINUTE")) {
            format = isdenodo ? "'ddHHmm'" : "'FMDDFMHH24MI'";
         } else if (this.trailingString.equalsIgnoreCase("DAY_HOUR")) {
            format = isdenodo ? "'ddHH'" : "'FMDDFMHH24'";
         } else if (this.trailingString.equalsIgnoreCase("YEAR_MONTH")) {
            format = isdenodo ? "'yyyyMM'" : "'FMYYYYFMMM'";
         } else if (this.trailingString.equalsIgnoreCase("MICROSECOND")) {
            format = isdenodo ? "'SSSSSS'" : "'FF6'";
         }

         qry = isdenodo ? "CAST(FORMATDATE(" + format + "," + StringFunctions.handleLiteralStringDateForDenodo(arguments.get(0).toString()) + ") AS INT8)" : "cast(to_char(" + arguments.get(0).toString() + "," + format + ") as int)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.trailingString = null;
         this.fromStringInFunction = null;
         this.setFunctionArguments(new Vector());
      } else if (this.functionName.getColumnName().equalsIgnoreCase("WEEKDAY")) {
         format = isdenodo ? StringFunctions.handleLiteralStringDateForDenodo(arguments.get(0).toString()) : StringFunctions.handleLiteralStringDateForOracle(arguments.get(0).toString());
         String intSyntax = isdenodo ? "INT4" : "INT";
         qry = "CAST((TRUNC(" + format + ")-TRUNC(" + format + ",'IW')) AS " + intSyntax + ")";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selCol = ((SelectColumn)this.functionArguments.get(0)).toDB2Select(to_sqs, from_sqs);
            arguments.addElement(selCol);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      String arg;
      if (this.functionName.toString().equalsIgnoreCase("dayname")) {
         arg = "DAYNAME( " + arguments.get(0).toString() + " )";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("weekday")) {
         arg = "((DAYOFWEEK_ISO(" + arguments.get(0).toString() + ")+6)%7)";
         this.functionName.setColumnName(arg);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selCol = ((SelectColumn)this.functionArguments.get(0)).toInformixSelect(to_sqs, from_sqs);
            arguments.addElement(selCol);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String funName = this.functionName.getColumnName();
      String dateString = StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString());
      String qry;
      if (funName.equalsIgnoreCase("quarter")) {
         qry = " QUARTER(" + dateString + ") ";
      } else if (funName.equalsIgnoreCase("dayname")) {
         qry = " TO_CHAR(" + dateString + ",'%A') ";
      } else if (funName.equalsIgnoreCase("dayofmonth")) {
         qry = " DAY(" + dateString + ") ";
      } else if (funName.equalsIgnoreCase("dayofyear")) {
         qry = " CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT) ";
      } else if (funName.equalsIgnoreCase("weekday")) {
         qry = " WEEKDAY(" + dateString + " - 1 UNITS DAY) ";
      } else if (funName.equalsIgnoreCase("hour")) {
         qry = " CAST(TO_CHAR(" + dateString + ",'%H') AS INT) ";
      } else if (funName.equalsIgnoreCase("minute")) {
         qry = " CAST(TO_CHAR(" + dateString + ",'%M') AS INT) ";
      } else if (funName.equalsIgnoreCase("second")) {
         qry = " CAST(TO_CHAR(" + dateString + ",'%S') AS INT) ";
      } else if (funName.equalsIgnoreCase("microsecond")) {
         qry = " CAST(TO_CHAR(" + dateString + ",'%F50') AS INT) ";
      } else {
         if (!funName.equalsIgnoreCase("extract") || this.trailingString == null) {
            arguments.set(0, dateString);
            this.setFunctionArguments(arguments);
            return;
         }

         if (this.trailingString.equalsIgnoreCase("DAY")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%d') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("MONTH")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%m') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("YEAR")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%Y') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("HOUR")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%H') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("MINUTE")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%M') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("SECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%S') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("MICROSECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%F50') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("HOUR_SECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%H%M%S') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("MINUTE_SECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%M%S') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("DAY_SECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%d%H%M%S') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%S%F50') AS BIGINT) ";
         } else if (this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%M%S%F50') AS BIGINT)";
         } else if (this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%H%M%S%F50') AS BIGINT) ";
         } else if (this.trailingString.equalsIgnoreCase("DAY_MICROSECOND")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%d%H%M%S%F50') AS BIGINT) ";
         } else if (this.trailingString.equalsIgnoreCase("HOUR_MINUTE")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%H%M') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("DAY_MINUTE")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%d%H%M') AS INT) ";
         } else if (this.trailingString.equalsIgnoreCase("DAY_HOUR")) {
            qry = " CAST(TO_CHAR(" + dateString + ",'%d%H') AS INT) ";
         } else {
            if (!this.trailingString.equalsIgnoreCase("YEAR_MONTH")) {
               arguments.set(0, dateString);
               this.setFunctionArguments(arguments);
               return;
            }

            qry = " CAST(TO_CHAR(" + dateString + ",'%Y%m') AS INT) ";
         }

         this.trailingString = null;
         this.fromStringInFunction = null;
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String funName = this.functionName.getColumnName();
      if (funName.equalsIgnoreCase("add_time")) {
         this.functionName.setColumnName("ADDTIME");
      } else if (funName.equalsIgnoreCase("day_name")) {
         this.functionName.setColumnName("DAYNAME");
      } else if (funName.equalsIgnoreCase("day_of_month")) {
         this.functionName.setColumnName("DAYOFMONTH");
      } else if (funName.equalsIgnoreCase("day_of_year")) {
         this.functionName.setColumnName("DAYOFYEAR");
      } else if (funName.equalsIgnoreCase("micro_second")) {
         this.functionName.setColumnName("MICROSECOND");
      } else if (funName.equalsIgnoreCase("sub_date")) {
         this.functionName.setColumnName("SUBDATE");
      } else if (funName.equalsIgnoreCase("sub_time")) {
         this.functionName.setColumnName("SUBTIME");
      } else if (funName.equalsIgnoreCase("week_day")) {
         this.functionName.setColumnName("WEEKDAY");
      } else if (funName.equalsIgnoreCase("year_week")) {
         this.functionName.setColumnName("YEARWEEK");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String time;
      String qry;
      if (from_sqs.isMongoDb() && (funName.equalsIgnoreCase("addtime") || funName.equalsIgnoreCase("subtime"))) {
         arguments = new Vector();
         this.functionName.setColumnName("");
         qry = funName.equalsIgnoreCase("addtime") ? "+" : "-";
         String[] timeSplit = this.functionArguments.get(1).toString().split(":");
         ArrayList<String> result = new ArrayList();
         if (timeSplit.length <= 1) {
            result.add("INTERVAL " + this.functionArguments.get(1).toString().replace("'", "").replace("`", "") + " SECOND");
         } else if (timeSplit.length == 2) {
            result.add("INTERVAL " + timeSplit[0].replace("'", "").replace("`", "") + " HOUR");
            result.add("INTERVAL " + timeSplit[1].replace("'", "").replace("`", "") + " MINUTE");
         } else {
            result.add("INTERVAL " + timeSplit[0].replace("'", "").replace("`", "") + " HOUR");
            result.add("INTERVAL " + timeSplit[1].replace("'", "").replace("`", "") + " MINUTE");
            result.add("INTERVAL " + timeSplit[2].replace("'", "").replace("`", "") + " SECOND");
         }

         time = this.functionArguments.get(0).toString().replace("`", "");

         for(int i = 0; i < result.size(); ++i) {
            time = time + qry + (String)result.get(i);
         }

         arguments.addElement(time);
      }

      if (!from_sqs.isHyperSql()) {
         this.setFunctionArguments(arguments);
      } else {
         qry = "";
         String dateString = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
         if (!funName.equalsIgnoreCase("subtime") && !funName.equalsIgnoreCase("addtime")) {
            if (funName.equalsIgnoreCase("weekday")) {
               qry = " MOD(DAYOFWEEK(" + dateString + ") +5 , 7) ";
            } else if (funName.equalsIgnoreCase("second")) {
               qry = " CAST(TO_CHAR(" + dateString + ",'SS') AS INT) ";
            } else if (funName.equalsIgnoreCase("microsecond")) {
               qry = " CAST(RPAD(EXTRACT(MICROSECOND FROM " + dateString + "), 6, '0') AS BIGINT) ";
            } else {
               if (!funName.equalsIgnoreCase("extract") || this.trailingString == null) {
                  arguments.set(0, dateString);
                  this.setFunctionArguments(arguments);
                  return;
               }

               if (this.trailingString.equalsIgnoreCase("HOUR_SECOND")) {
                  qry = " CAST(TO_CHAR(" + dateString + ",'HH24MISS') AS INT) ";
               } else if (this.trailingString.equalsIgnoreCase("MINUTE_SECOND")) {
                  qry = " CAST(TO_CHAR(" + dateString + ",'MISS') AS INT) ";
               } else if (this.trailingString.equalsIgnoreCase("DAY_SECOND")) {
                  qry = " CAST(TO_CHAR(" + dateString + ",'DDHH24MISS') AS INT) ";
               } else if (this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND")) {
                  qry = " CAST(TO_CHAR(" + dateString + ",'SS') || RPAD(EXTRACT(MICROSECOND FROM " + dateString + "), 6, '0') AS BIGINT) ";
               } else if (this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND")) {
                  qry = " CAST(TO_CHAR(" + dateString + ",'MISS') || RPAD(EXTRACT(MICROSECOND FROM " + dateString + "), 6, '0') AS BIGINT) ";
               } else if (this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND")) {
                  qry = " CAST(TO_CHAR(" + dateString + ",'HH24MISS') || RPAD(EXTRACT(MICROSECOND FROM " + dateString + "), 6, '0') AS BIGINT) ";
               } else if (this.trailingString.equalsIgnoreCase("DAY_MICROSECOND")) {
                  qry = " CAST(TO_CHAR(" + dateString + ",'DDHH24MISS') || RPAD(EXTRACT(MICROSECOND FROM " + dateString + "), 6, '0') AS BIGINT) ";
               } else if (this.trailingString.equalsIgnoreCase("HOUR_MINUTE")) {
                  qry = " CAST(TO_CHAR(" + dateString + ",'HH24MI') AS INT) ";
               } else if (this.trailingString.equalsIgnoreCase("DAY_MINUTE")) {
                  qry = " CAST(TO_CHAR(" + dateString + ",'DDHH24MI') AS INT) ";
               } else if (this.trailingString.equalsIgnoreCase("DAY_HOUR")) {
                  qry = " CAST(TO_CHAR(" + dateString + ",'DDHH24') AS INT) ";
               } else {
                  if (!this.trailingString.equalsIgnoreCase("YEAR_MONTH")) {
                     arguments.set(0, dateString);
                     this.setFunctionArguments(arguments);
                     return;
                  }

                  qry = " CAST(TO_CHAR(" + dateString + ",'YYYYMM') AS INT) ";
               }

               this.trailingString = null;
               this.fromStringInFunction = null;
            }
         } else {
            String datetime = "CAST(" + dateString + " AS TIMESTAMP)";
            time = "CAST(" + arguments.get(1).toString() + " AS TIME)";
            if (funName.equalsIgnoreCase("addtime")) {
               qry = "( " + datetime + " + " + "HOUR( " + time + " ) HOUR" + " + MINUTE( " + time + " ) MINUTE" + " + SECOND( " + time + " ) SECOND)";
            } else if (funName.equalsIgnoreCase("subtime")) {
               qry = "( " + datetime + " - " + "HOUR( " + time + " ) HOUR" + " - MINUTE( " + time + " ) MINUTE" + " - SECOND( " + time + " ) SECOND)";
            }
         }

         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      }
   }

   public static String applyDatePartOrExtractWrapper(SelectQueryStatement fromSqs, String unit, String argument1) {
      String qry = "";
      if (fromSqs != null && fromSqs.getBooleanValues("can.use.date.part.instead.of.extract")) {
         qry = "DATE_PART('" + unit + "' ,(" + argument1 + "))";
      } else {
         qry = "EXTRACT(" + unit + " FROM (" + argument1 + "))";
      }

      return qry;
   }
}
