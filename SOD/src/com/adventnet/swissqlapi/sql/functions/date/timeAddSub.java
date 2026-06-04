package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.util.Vector;

public class timeAddSub extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isStringColumn = false;
      boolean isFirstArgTime = false;
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      boolean canUseUDFFunction = from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.date.time");

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            if (((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0) instanceof String) {
               this.handleStringLiteralForTime(from_sqs, i_count, false, true);
               String elementAt = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0).toString();
               if (!elementAt.contains("/") && !elementAt.contains("-")) {
                  if (elementAt.contains(":")) {
                     elementAt = "CAST(" + elementAt + " AS TIME)";
                     isFirstArgTime = true;
                  }
               } else {
                  elementAt = "CAST(" + elementAt + " AS TIMESTAMP)";
               }

               ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().set(0, elementAt);
               if (i_count == 0) {
                  isStringColumn = true;
               }
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         }
      }

      String qry = "";
      new StringBuffer();
      String timeString = " CAST( " + arguments.get(1).toString() + " AS TIME)";
      if (!arguments.get(1).toString().contains(":")) {
         timeString = " interval '1' second * " + arguments.get(1).toString();
      }

      String dateTimeString = arguments.get(0).toString();
      if (!isStringColumn) {
         dateTimeString = "CAST(" + arguments.get(0) + " AS TIMESTAMP)";
      }

      boolean isStringInstance;
      String timeInterval;
      String[] timeSplit;
      ArrayList result;
      int i;
      if (this.functionName.getColumnName().equalsIgnoreCase("addtime")) {
         isStringInstance = arguments.get(1) != null && ((SelectColumn)arguments.get(1)).getColumnExpression().size() == 1 && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0) != null && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof String;
         if (from_sqs != null && from_sqs.isVerticaDb() && isStringInstance) {
            timeInterval = timeString.replace("CAST", "").replace("AS", "").replace("TIME", "").replace(" ", "").replace("((", "").replace("))", "");
            timeSplit = timeInterval.split(":");
            result = new ArrayList();
            if (timeSplit.length <= 1) {
               result.add("INTERVAL '" + timeSplit[0].replace("'", "").replace("`", "") + " SECOND'");
            } else if (timeSplit.length == 2) {
               result.add("INTERVAL '" + timeSplit[0].replace("'", "").replace("`", "") + " HOUR'");
               result.add("INTERVAL '" + timeSplit[1].replace("'", "").replace("`", "") + " MINUTE'");
            } else {
               result.add("INTERVAL '" + timeSplit[0].replace("'", "").replace("`", "") + " HOUR'");
               result.add("INTERVAL '" + timeSplit[1].replace("'", "").replace("`", "") + " MINUTE'");
               result.add("INTERVAL '" + timeSplit[2].replace("'", "").replace("`", "") + " SECOND'");
            }

            qry = arguments.get(0).toString();

            for(i = 0; i < result.size(); ++i) {
               qry = qry + "+" + (String)result.get(i);
            }
         } else {
            qry = isFirstArgTime ? " ( " + dateTimeString + " +  " + timeString + ")::TIMESTAMP " : " ( " + dateTimeString + " +  " + timeString + ")";
         }

         if (canUseUDFFunction) {
            qry = "DATE_ADD(" + arguments.get(0).toString() + ", TO_TIME_UDF(" + arguments.get(1).toString() + "))";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("subtime")) {
         isStringInstance = arguments.get(1) != null && ((SelectColumn)arguments.get(1)).getColumnExpression().size() == 1 && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0) != null && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof String;
         if (from_sqs != null && from_sqs.isVerticaDb() && isStringInstance) {
            timeInterval = timeString.replace("CAST", "").replace("AS", "").replace("TIME", "").replace(" ", "").replace("((", "").replace("))", "");
            timeSplit = timeInterval.split(":");
            result = new ArrayList();
            if (timeSplit.length <= 1) {
               result.add("INTERVAL '" + timeSplit[0].replace("'", "").replace("`", "") + " SECOND'");
            } else if (timeSplit.length == 2) {
               result.add("INTERVAL '" + timeSplit[0].replace("'", "").replace("`", "") + " HOUR'");
               result.add("INTERVAL '" + timeSplit[1].replace("'", "").replace("`", "") + " MINUTE'");
            } else {
               result.add("INTERVAL '" + timeSplit[0].replace("'", "").replace("`", "") + " HOUR'");
               result.add("INTERVAL '" + timeSplit[1].replace("'", "").replace("`", "") + " MINUTE'");
               result.add("INTERVAL '" + timeSplit[2].replace("'", "").replace("`", "") + " SECOND'");
            }

            qry = arguments.get(0).toString();

            for(i = 0; i < result.size(); ++i) {
               qry = qry + "-" + (String)result.get(i);
            }
         } else {
            qry = isFirstArgTime ? "( " + dateTimeString + " -  " + timeString + " )::TIMESTAMP " : "( " + dateTimeString + " -  " + timeString + " )";
         }

         if (canUseUDFFunction) {
            qry = "DATE_SUB(" + arguments.get(0).toString() + ", TO_TIME_UDF(" + arguments.get(1).toString() + "))";
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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

      String qry = "";
      String datetime = "Cast(" + arguments.get(0).toString() + " as DATETIME)";
      String time = "Cast(" + arguments.get(1).toString() + " as TIME)";
      if (this.functionName.getColumnName().equalsIgnoreCase("ADDTIME")) {
         qry = "DATEADD(SECOND, EXTRACT(SECOND FROM ( " + time + " )),DATEADD(MINUTE,EXTRACT(MINUTE FROM ( " + time + " )),  DATEADD(HOUR, EXTRACT(HOUR FROM ( " + time + " )), " + datetime + " ) ))";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("SUBTIME")) {
         qry = "DATEADD(SECOND, -(EXTRACT(SECOND FROM ( " + time + " ))),DATEADD(MINUTE,-(EXTRACT(MINUTE FROM ( " + time + " ))),  DATEADD(HOUR, -(EXTRACT(HOUR FROM ( " + time + " ))), " + datetime + " ) ))";
      }

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
      }

      String qry = "";
      String datetime = "Cast(" + arguments.get(0).toString() + " as TIMESTAMP)";
      String time = "Cast(" + arguments.get(1).toString() + " as TIME)";
      if (this.functionName.getColumnName().equalsIgnoreCase("ADDTIME")) {
         qry = datetime + " + (SECOND( " + time + " ) * interval '1' second) + (MINUTE( " + time + " ) * interval '1' minute) + (HOUR( " + time + " ) * interval '1' hour)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("SUBTIME")) {
         qry = datetime + " - (SECOND( " + time + " ) * interval '1' second) - (MINUTE( " + time + " ) * interval '1' minute) - (HOUR( " + time + " ) * interval '1' hour)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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

      String qry = "";
      String datetime = "TO_TIMESTAMP(" + arguments.get(0).toString() + ")";
      String time = "TO_TIME(" + arguments.get(1).toString() + ")";
      if (this.functionName.getColumnName().equalsIgnoreCase("ADDTIME")) {
         qry = "ADD_SECONDS(" + datetime + ",HOUR(" + time + ")*3600+MINUTE(" + time + ")*60+SECOND(" + time + "))";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("SUBTIME")) {
         qry = "ADD_SECONDS(" + datetime + ",-(HOUR(" + time + ")*3600+MINUTE(" + time + ")*60+SECOND(" + time + ")))";
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

      String operator = "+";
      if (this.functionName.getColumnName().equalsIgnoreCase("SUBTIME")) {
         operator = "-";
      }

      String timeString = "'" + operator + "' || cast(strftime('%H'," + arguments.get(1).toString() + ") as integer) || ' hours','" + operator + "' || cast(strftime('%M'," + arguments.get(1).toString() + ") as integer) || ' minutes','" + operator + "' || cast(strftime('%S'," + arguments.get(1).toString() + ") as integer) || ' seconds'";
      String qry = "strftime('%Y-%m-%d %H:%M:%f'," + StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString()) + "," + timeString + ")";
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

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("ADDTIME")) {
         qry = "Cast(" + arguments.get(0).toString() + " as DATETIME) + Cast(" + arguments.get(1).toString() + " as DATETIME)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("SUBTIME")) {
         qry = "Cast(" + arguments.get(0).toString() + " as DATETIME) - Cast(" + arguments.get(1).toString() + " as DATETIME)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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
      String datetime = "Cast(" + arguments.get(0).toString() + " as DATETIME)";
      String time = "Cast(" + arguments.get(1).toString() + " as TIME)";
      if (this.functionName.getColumnName().equalsIgnoreCase("ADDTIME")) {
         qry = "DATETIME_ADD(DATETIME_ADD(DATETIME_ADD(" + datetime + ", INTERVAL EXTRACT(HOUR FROM ( " + time + " )) HOUR),INTERVAL EXTRACT(MINUTE FROM ( " + time + " )) MINUTE),INTERVAL EXTRACT(SECOND FROM ( " + time + " )) SECOND)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("SUBTIME")) {
         qry = "DATETIME_SUB(DATETIME_SUB(DATETIME_SUB(" + datetime + ", INTERVAL EXTRACT(HOUR FROM ( " + time + " )) HOUR),INTERVAL EXTRACT(MINUTE FROM ( " + time + " )) MINUTE),INTERVAL EXTRACT(SECOND FROM ( " + time + " )) SECOND)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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

      String qry = "";
      String datetime = "Cast(" + arguments.get(0).toString() + " as TIMESTAMP)";
      String time = "Cast(" + arguments.get(1).toString() + " as TIME)";
      if (this.functionName.getColumnName().equalsIgnoreCase("ADDTIME")) {
         qry = "( " + datetime + " + " + "HOUR( " + time + " ) HOURS" + " + MINUTE( " + time + " ) MINUTES" + " + SECOND( " + time + " ) SECONDS)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("SUBTIME")) {
         qry = "( " + datetime + " - " + "HOUR( " + time + " ) HOURS" + " - MINUTE( " + time + " ) MINUTES" + " - SECOND( " + time + " ) SECONDS)";
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

      String qry = "";
      String dateString = StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("ADDTIME")) {
         qry = dateString + "+ TO_DSINTERVAL('0 ' || " + arguments.get(1).toString() + ") ";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("SUBTIME")) {
         qry = dateString + " - TO_DSINTERVAL('0 ' || " + arguments.get(1).toString() + ") ";
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

      String qry = "";
      String datetime = "CDate(" + arguments.get(0).toString() + ")";
      String time = "TimeValue(" + arguments.get(1).toString() + ")";
      if (this.functionName.getColumnName().equalsIgnoreCase("ADDTIME")) {
         qry = "DATEADD('s', SECOND( " + time + " ),DATEADD('n',MINUTE( " + time + " ),  DATEADD('h', HOUR( " + time + " ), " + datetime + " ) ))";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("SUBTIME")) {
         qry = "DATEADD('s', -(SECOND( " + time + " )),DATEADD('n',-(MINUTE( " + time + " )),  DATEADD('h', -(HOUR( " + time + " )), " + datetime + " ) ))";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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
      String datetime = "CAST(" + dateString + " AS TIMESTAMP)";
      String time = "CAST(" + arguments.get(1).toString() + " AS TIME)";
      if (funName.equalsIgnoreCase("addtime")) {
         qry = "( " + datetime + " + " + "HOUR( " + time + " ) HOUR" + " + MINUTE( " + time + " ) MINUTE" + " + SECOND( " + time + " ) SECOND)";
      } else if (funName.equalsIgnoreCase("subtime")) {
         qry = "( " + datetime + " - " + "HOUR( " + time + " ) HOUR" + " - MINUTE( " + time + " ) MINUTE" + " - SECOND( " + time + " ) SECOND)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }
}
