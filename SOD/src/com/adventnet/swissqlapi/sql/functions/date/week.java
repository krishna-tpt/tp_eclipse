package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class week extends FunctionCalls {
   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int i;
      if (!this.functionName.getColumnName().equalsIgnoreCase("week") && !this.functionName.getColumnName().equalsIgnoreCase("yearweek") && !this.functionName.getColumnName().equalsIgnoreCase("weekofyear")) {
         StringBuffer[] argument;
         if (!this.functionName.getColumnName().equalsIgnoreCase("addtime") && !this.functionName.getColumnName().equalsIgnoreCase("subtime")) {
            if (this.functionName.getColumnName().equalsIgnoreCase("convert_tz") || this.functionName.getColumnName().equalsIgnoreCase("convert_timezone") || this.functionName.getColumnName().equalsIgnoreCase("converttimezone")) {
               argument = new StringBuffer[3];

               for(i = 0; i < this.functionArguments.size(); ++i) {
                  argument[i] = new StringBuffer();
                  if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                     if (i == 0) {
                        this.handleStringLiteralForDateTime(from_sqs, i, true);
                     }

                     argument[i].append(((SelectColumn)this.functionArguments.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs));
                  } else {
                     argument[i].append(this.functionArguments.elementAt(i));
                  }
               }

               i = 0;

               try {
                  if (argument.length == 3 && argument[1] != null && argument[2] != null) {
                     String[] currentTZ = argument[1].toString().replaceAll("'", "").split(":");
                     String[] toTZ = argument[2].toString().replaceAll("'", "").split(":");
                     int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
                     int currentTZMin = currentTZ.length == 2 ? Integer.parseInt(currentTZ[1]) : 0;
                     int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
                     int toTZMin = toTZ.length == 2 ? Integer.parseInt(toTZ[1]) : 0;
                     int toTZOverallMins = toTZHour * 60 + toTZMin;
                     int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
                     if (currentTZ[0].contains("-")) {
                        currentTZHour *= -1;
                     }

                     if (toTZ[0].contains("-")) {
                        toTZHour *= -1;
                     }

                     if (currentTZHour < 0) {
                        if (toTZHour < 0) {
                           i = toTZOverallMins * -1 - currentTZOverallMins * -1;
                        } else {
                           i = toTZOverallMins - currentTZOverallMins * -1;
                        }
                     } else if (toTZHour < 0) {
                        i = toTZOverallMins * -1 - currentTZOverallMins;
                     } else {
                        i = toTZOverallMins - currentTZOverallMins;
                     }
                  }
               } catch (Exception var13) {
               }

               this.functionName.setColumnName("TIMESTAMPADD(MINUTE," + i + "," + argument[0] + ")");
               this.setOpenBracesForFunctionNameRequired(false);
               this.functionArguments = new Vector();
            }
         } else {
            argument = new StringBuffer[2];

            for(i = 0; i < this.functionArguments.size(); ++i) {
               argument[i] = new StringBuffer();
               if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                  this.handleStringLiteralForTime(from_sqs, i, i == 0, false);
                  argument[i].append(((SelectColumn)this.functionArguments.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs));
               } else {
                  argument[i].append(this.functionArguments.elementAt(i));
               }
            }

            String timevalue = "interval '1' second * (TIMESTAMPDIFF(SECOND,timestamp(time('00:00:00')),timestamp(time(" + argument[1] + "))))";
            if (this.functionName.getColumnName().equalsIgnoreCase("addtime")) {
               this.functionName.setColumnName(argument[0] + "+" + timevalue);
            } else {
               this.functionName.setColumnName(argument[0] + "-" + timevalue);
            }

            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }
      } else {
         Vector arguments = new Vector();

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               if (i == 0) {
                  this.handleStringLiteralForDateTime(from_sqs, i, true);
               }

               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         if (arguments.size() < 2 && this.functionName.getColumnName().equalsIgnoreCase("week")) {
            Integer arg2 = new Integer(0);
            arguments.addElement(arg2);
         }

         this.setFunctionArguments(arguments);
         if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
            this.functionName.setColumnName("yearweek");
         } else if (this.functionName.getColumnName().equalsIgnoreCase("weekofyear")) {
            this.functionName.setColumnName("week_iso");
         } else {
            this.functionName.setColumnName("week");
         }
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      boolean canUseUDFFunction = from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.date.time");
      boolean var10000;
      if (from_sqs != null && !from_sqs.isAmazonRedShift() && !from_sqs.isPgsqlLive()) {
         var10000 = true;
      } else {
         var10000 = false;
      }

      boolean isVertica = from_sqs != null && from_sqs.isVerticaDb();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            if (i_count == 0) {
               if (selColumn.getColumnExpression().size() == 1 && selColumn.getColumnExpression().get(0) instanceof String) {
                  String dateString = selColumn.getColumnExpression().get(0).toString();
                  dateString = "CAST(" + this.handleStringLiteralForDateTime(dateString, from_sqs) + " AS DATE)";
                  selColumn.getColumnExpression().set(0, dateString);
               } else {
                  selColumn.modifyCurrentTimeAsCurrentTimestamp(from_sqs);
               }
            }

            arguments.addElement(selColumn.toPostgreSQLSelect(to_sqs, from_sqs));
         }
      }

      String weekQry = "CAST((CASE      WHEN (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  " + arguments.get(0) + " ) as int)  || '-01-01')::date) as int) +6, 7) + 4), 7) -3) * - 1  >=  cast(date_part('doy' ," + arguments.get(0) + ") as int) THEN CEIL(((MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  " + arguments.get(0) + " ) as int)  -1 || '-01-01')::date) as int) +6, 7) + 4), 7) -3) +  cast(date_part('doy' ,(cast(extract (year from  " + arguments.get(0) + " ) as int)  -1 || '-12-31')::date) as int) +  cast(date_part('doy' ," + arguments.get(0) + ") as int)) / 7.0)      ELSE       CASE        WHEN ( cast(date_part('doy' ," + arguments.get(0) + ") as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  " + arguments.get(0) + " ) as int)  || '-01-01')::date) as int) +6, 7) + 4), 7) -3))  > 364 THEN         CASE          WHEN (( cast(date_part('doy' ,(cast(extract (year from  " + arguments.get(0) + " ) as int)  || '-12-31')::date) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  " + arguments.get(0) + " ) as int)  || '-01-01')::date) as int) +6, 7) + 4), 7) -3)) -364)  > 3 THEN 53          ELSE 1         END        ELSE CEIL(( cast(date_part('doy' ," + arguments.get(0) + ") as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  " + arguments.get(0) + " ) as int)  || '-01-01')::date) as int) +6, 7) + 4), 7) -3)) / 7.0)       END     END) as int)";
      String dataType;
      if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = " cast(" + extract.applyDatePartOrExtractWrapper(from_sqs, "WEEK", qry + arguments.get(0) + "") + " as int) ";
            if (canUseUDFFunction) {
               qry = "WEEK(" + arguments.get(0).toString() + ")";
            }
         } else {
            qry = "ZR_WeekDtNwkStrtDay(DATE(" + arguments.get(0) + "),4)";
            if (isPostgreLiveDbs) {
               dataType = "/";
               if (arguments.size() >= 2) {
                  qry = "CAST((CASE      WHEN (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3) * - 1  >=  cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) THEN CEIL(((MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3) +  cast(date_part('doy' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || '-12-31')::DATE) as int) +  cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int)) " + dataType + " ((case when 7=0 then null else 7 end)*1.0))      ELSE       CASE        WHEN ( cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3))  > 364 THEN         CASE          WHEN (( cast(date_part('doy' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-12-31')::DATE) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3)) -364)  > 3 THEN 53          ELSE 1         END        ELSE CEIL(( cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3)) " + dataType + " ((case when 7=0 then null else 7 end)*1.0))       END     END) AS INTEGER)";
               } else {
                  qry = isVertica ? "CASE WHEN DAYOFWEEK((YEAR(" + arguments.get(0).toString() + "::date) || '-01-01')::date) = 1 THEN WEEK(" + arguments.get(0).toString() + ") ELSE WEEK(" + arguments.get(0).toString() + ")-1 END" : "CAST((CASE      WHEN (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3) * - 1  >=  cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) THEN CEIL(((MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3) +  cast(date_part('doy' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || '-12-31')::DATE) as int) +  cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int)) " + dataType + " ((case when 7=0 then null else 7 end)*1.0)) - 53      ELSE       CASE        WHEN ( cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3))  > 364 THEN         CASE          WHEN (( cast(date_part('doy' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-12-31')::DATE) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3)) -364)  > 3 THEN 53          ELSE 1         END        ELSE CEIL(( cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3)) " + dataType + " ((case when 7=0 then null else 7 end)*1.0))       END     END) AS INTEGER)";
               }
            }
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("weekofyear")) {
         qry = " cast(" + extract.applyDatePartOrExtractWrapper(from_sqs, "WEEK", qry + arguments.get(0) + "") + " as int) ";
         if (canUseUDFFunction) {
            qry = "WEEKOFYEAR(" + arguments.get(0).toString() + ")";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = "cast(to_char(DATE(" + arguments.get(0) + "),'IYYYIW') as integer)";
         } else {
            qry = "ZR_WeekYearDtNwkStrtDay(DATE(" + arguments.get(0) + "),4)";
            if (isPostgreLiveDbs) {
               dataType = from_sqs != null && from_sqs.isVerticaDb() ? "VARCHAR" : "TEXT";
               qry = "cast(  CASE    WHEN (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3) * - 1  >=  cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) THEN (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || SUBSTRING((100 + CEIL(((MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3) +  cast(date_part('doy' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || '-12-31')::DATE) as int) +  cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int)) / ((case when 7=0 then null else 7 end)*1.0)))::" + dataType + ",2))    ELSE     CASE      WHEN ( cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3))  > 364 THEN       CASE        WHEN (( cast(date_part('doy' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-12-31')::DATE) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3)) -364)  > 3 THEN (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '53')        ELSE (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  + 1 || '01')       END      ELSE (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || SUBSTRING((100 + CEIL(( cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3)) / ((case when 7=0 then null else 7 end)*1.0)))::" + dataType + ",2))  END END as INTEGER)";
            }
         }
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

      String dateArg = arguments.get(0).toString();
      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
         if (arguments.size() < 2) {
            qry = "CASE WHEN " + dateArg + " IS NOT NULL THEN CAST(CASE  WHEN (((((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4)) % ( 7 )) -3) * - 1  >= DATEPART(dy, " + dateArg + ") THEN CEILING((((( ((datepart(dw,concat(YEAR(" + dateArg + ") -1, '-01-01'))+6)%7 + 4) ) % ( 7 )) -3) + DATEPART(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')) + DATEPART(dy, " + dateArg + ")) / CONVERT(FLOAT, 7)) - 52     ELSE       CASE        WHEN ((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3))  > 364) THEN         CASE          WHEN (((DATEPART(dy, concat(YEAR(" + dateArg + "), '-12-31')) + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3)) -364)  > 3) THEN 53          ELSE 1         END        ELSE CASE WHEN CEILING((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3)) / 7) = 0 THEN 1 ELSE CEILING((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3)) / 7) END END END AS INT) ELSE NULL END";
         } else if (arguments.get(1).toString().equals("6")) {
            qry = "CASE WHEN " + dateArg + " IS NOT NULL THEN CAST(CASE  WHEN (((((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4)) % ( 7 )) -3) * - 1  >= DATEPART(dy, " + dateArg + ") THEN CEILING((((( ((datepart(dw,concat(YEAR(" + dateArg + ") -1, '-01-01'))+6)%7 + 4) ) % ( 7 )) -3) + DATEPART(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')) + DATEPART(dy, " + dateArg + ")) / CONVERT(FLOAT, 7))     ELSE       CASE        WHEN ((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3))  > 364) THEN         CASE          WHEN (((DATEPART(dy, concat(YEAR(" + dateArg + "), '-12-31')) + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3)) -364)  > 3) THEN 53          ELSE 1         END        ELSE CASE WHEN CEILING((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3)) / 7) = 0 THEN 1 ELSE CEILING((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3)) / 7) END END END AS INT) ELSE NULL END";
         } else {
            qry = "DATEPART(wk," + dateArg + ")";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("weekofyear")) {
         qry = "DATEPART(wk," + dateArg + ")";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = "CASE WHEN " + arguments.get(0).toString() + " IS NOT NULL THEN concat(datepart(yyyy," + arguments.get(0).toString() + "),datepart(wk," + arguments.get(0).toString() + ")) ELSE NULL END";
         } else {
            qry = "CASE WHEN " + dateArg + " IS NOT NULL THEN CAST(CASE  WHEN (((((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4)) % ( 7 )) -3) * - 1  >= DATEPART(dy, " + dateArg + ") THEN CONCAT(YEAR(" + dateArg + ") -1, SUBSTRING(Cast((100 + CEILING((((( ((datepart(dw,concat(YEAR(" + dateArg + ") -1, '-01-01'))+6)%7 + 4) ) % ( 7 )) -3) + DATEPART(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')) + DATEPART(dy, " + dateArg + ")) / CONVERT(FLOAT, 7)))AS VARCHAR), 2, LEN((100 + CEILING((((( ((datepart(dw,concat(YEAR(" + dateArg + ") -1, '-01-01'))+6)%7 + 4) ) % ( 7 )) -3) + DATEPART(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')) + DATEPART(dy, " + dateArg + ")) / CONVERT(FLOAT, 7)))) -1))      ELSE       CASE        WHEN ((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3))  > 364) THEN CASE  WHEN (((DATEPART(dy, concat(YEAR(" + dateArg + "), '-12-31')) + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3)) -364)  > 3) THEN CONCAT(YEAR(" + dateArg + "), '53')  ELSE CONCAT(YEAR(" + dateArg + ") + 1, '01') END    ELSE CONCAT(YEAR(" + dateArg + "), SUBSTRING(CAST((100 + CEILING((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3)) / CONVERT(FLOAT, 7)))AS VARCHAR), 2, LEN((100 + CEILING((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3)) / CONVERT(FLOAT, 7)))) -1))       END     END as BIGINT) ELSE NULL END";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("convert_tz")) {
         int finalMins = 0;

         try {
            if (arguments.size() == 3 && arguments.get(1) != null && arguments.get(2) != null && arguments.get(1).toString().contains(":") && arguments.get(2).toString().contains(":")) {
               String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
               String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
               int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
               int currentTZMin = Integer.parseInt(currentTZ[1]);
               int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
               int toTZMin = Integer.parseInt(toTZ[1]);
               int toTZOverallMins = toTZHour * 60 + toTZMin;
               int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
               if (currentTZ[0].contains("-")) {
                  currentTZHour *= -1;
               }

               if (toTZ[0].contains("-")) {
                  toTZHour *= -1;
               }

               if (currentTZHour < 0) {
                  if (toTZHour < 0) {
                     finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                  } else {
                     finalMins = toTZOverallMins - currentTZOverallMins * -1;
                  }
               } else if (toTZHour < 0) {
                  finalMins = toTZOverallMins * -1 - currentTZOverallMins;
               } else {
                  finalMins = toTZOverallMins - currentTZOverallMins;
               }
            }
         } catch (Exception var15) {
         }

         qry = "DATEADD(mi," + finalMins + ",Cast(" + arguments.get(0).toString() + " AS DATETIME))";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      int finalMins;
      for(finalMins = 0; finalMins < this.functionArguments.size(); ++finalMins) {
         if (this.functionArguments.elementAt(finalMins) instanceof SelectColumn) {
            if (finalMins == 0 && ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().get(0) instanceof String) {
               String dateString = ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().get(0).toString();
               dateString = "CAST(" + this.handleStringLiteralForDateTime(dateString, from_sqs) + " AS DATE)";
               ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().set(0, dateString);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(finalMins)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(finalMins));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = " cast(extract(week from " + qry + arguments.get(0) + ") as int) ";
         } else if (arguments.size() >= 2) {
            qry = " cast(extract(week from " + arguments.get(0) + ") as int) ";
         } else {
            qry = " CASE WHEN (cast(extract(week from " + arguments.get(0) + ") as int)) = 0 THEN CASE WHEN  (cast(extract(DAYOFWEEK from " + arguments.get(0) + ")+1 as int)) < 5 THEN 1 ELSE 53 END ELSE (cast(extract(week from " + arguments.get(0) + ") as int)) END";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("weekofyear")) {
         qry = " cast(extract(week from  " + qry + arguments.get(0) + ") as int) ";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = "concat(DATE_PART(yyyy," + arguments.get(0).toString() + "),DATE_PART(wk," + arguments.get(0).toString() + "))";
         } else {
            String dateArg = arguments.get(0).toString();
            qry = "CAST(CASE  WHEN (((((DATE_PART(dw,concat(YEAR(" + dateArg + "), '-01-01')::DATE)+6)%7 + 4)) % ( 7 )) -3) * - 1  >= DATE_PART(dy, " + dateArg + ") THEN CONCAT(YEAR(" + dateArg + ") -1, SUBSTRING(Cast((100 + CEIL((((( ((DATE_PART(dw,concat(YEAR(" + dateArg + ") -1, '-01-01')::DATE)+6)%7 + 4) ) % ( 7 )) -3) + DATE_PART(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')::DATE) + DATE_PART(dy, " + dateArg + ")) / 7))AS VARCHAR), 2, LEN((100 + CEIL((((( ((DATE_PART(dw,concat(YEAR(" + dateArg + ") -1, '-01-01')::DATE)+6)%7 + 4) ) % ( 7 )) -3) + DATE_PART(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')::DATE) + DATE_PART(dy, " + dateArg + ")) / 7))) -1))      ELSE       CASE        WHEN ((DATE_PART(dy, " + dateArg + ") + ((( ((DATE_PART(dw,concat(YEAR(" + dateArg + "), '-01-01')::DATE)+6)%7 + 4) ) % ( 7 )) -3))  > 364) THEN CASE  WHEN (((DATE_PART(dy, concat(YEAR(" + dateArg + "), '-12-31')::DATE) + ((( ((DATE_PART(dw,concat(YEAR(" + dateArg + "), '-01-01')::DATE)+6)%7 + 4) ) % ( 7 )) -3)) -364)  > 3) THEN CONCAT(YEAR(" + dateArg + "), '53')  ELSE CONCAT(YEAR(" + dateArg + ") + 1, '01') END    ELSE CONCAT(YEAR(" + dateArg + "), SUBSTRING(CAST((100 + CEIL((DATE_PART(dy, " + dateArg + ") + ((( ((DATE_PART(dw,concat(YEAR(" + dateArg + "), '-01-01')::DATE)+6)%7 + 4) ) % ( 7 )) -3)) / 7))AS VARCHAR), 2, LEN((100 + CEIL((DATE_PART(dy, " + dateArg + ") + ((( ((DATE_PART(dw,concat(YEAR(" + dateArg + "), '-01-01')::DATE)+6)%7 + 4) ) % ( 7 )) -3)) / 7))) -1))       END     END as BIGINT)";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("convert_tz")) {
         finalMins = 0;

         try {
            if (arguments.size() == 3 && arguments.get(1) != null && arguments.get(2) != null && arguments.get(1).toString().contains(":") && arguments.get(2).toString().contains(":")) {
               String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
               String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
               int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
               int currentTZMin = Integer.parseInt(currentTZ[1]);
               int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
               int toTZMin = Integer.parseInt(toTZ[1]);
               int toTZOverallMins = toTZHour * 60 + toTZMin;
               int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
               if (currentTZ[0].contains("-")) {
                  currentTZHour *= -1;
               }

               if (toTZ[0].contains("-")) {
                  toTZHour *= -1;
               }

               if (currentTZHour < 0) {
                  if (toTZHour < 0) {
                     finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                  } else {
                     finalMins = toTZOverallMins - currentTZOverallMins * -1;
                  }
               } else if (toTZHour < 0) {
                  finalMins = toTZOverallMins * -1 - currentTZOverallMins;
               } else {
                  finalMins = toTZOverallMins - currentTZOverallMins;
               }
            }
         } catch (Exception var14) {
         }

         qry = "DATEADD(MINUTES," + finalMins + ",Cast(" + arguments.get(0).toString() + " AS TIMESTAMP))";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName().toUpperCase();
      Vector vector;
      int i;
      String weekMode;
      String weekStartDay;
      if (!fnStr.equalsIgnoreCase("weekofyear") && !fnStr.equalsIgnoreCase("week_of_year")) {
         if (from_sqs != null && from_sqs.isHyperSql()) {
            vector = new Vector();

            for(i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                  vector.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
               } else {
                  vector.addElement(this.functionArguments.elementAt(i));
               }
            }

            weekStartDay = "";
            weekMode = StringFunctions.handleLiteralStringDateForHyperSql(vector.get(0).toString());
            if (fnStr.equalsIgnoreCase("yearweek")) {
               if (vector.size() >= 2 && !vector.get(1).toString().equals("6")) {
                  weekStartDay = " CAST(TO_CHAR(CAST(" + vector.get(0) + " AS DATE),'IYYYIW') AS INT) ";
               } else {
                  weekStartDay = " CAST(CASE WHEN (MOD(( MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ") || '-01-01' AS DATE)) +5,7) +4), 7) -3) * - 1 >= DAYOFYEAR(" + weekMode + ") THEN ((YEAR(" + weekMode + ") - 1 ) || SUBSTR(TO_CHAR((100+CEIL(((MOD(( MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ")-1 || '-01-01' AS DATE)) +5,7) + 4),7) -3) + DAYOFYEAR(CAST(YEAR(" + weekMode + ")-1 || '-12-31' AS DATE)) + DAYOFYEAR(" + weekMode + ")) / 7.0))),2)) ELSE CASE WHEN ( DAYOFYEAR(" + weekMode + ") + (MOD(( MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ") || '-01-01' AS DATE)) +5,7) +4), 7) -3)) > 364 THEN CASE WHEN ((DAYOFYEAR(CAST(YEAR(" + weekMode + ") || '-12-31' AS DATE))+(MOD(( MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ") || '-01-01' AS DATE)) +5,7) +4), 7) -3)) -364) > 3 THEN (YEAR(" + weekMode + ") || '53') ELSE ((YEAR(" + weekMode + ")+1) || '01') END ELSE (YEAR(" + weekMode + ") || SUBSTR(TO_CHAR((100 + CEIL(CAST((DAYOFYEAR(" + weekMode + ") + (MOD(( MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ") || '-01-01' AS DATE)) +5,7) +4), 7) -3)) AS DECIMAL)/ 7.0))),2)) END END AS INT) ";
               }
            } else if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
               if (vector.size() < 2) {
                  weekStartDay = " CAST(FLOOR((CASE WHEN (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ") || '-01-01' AS DATE)) +5,7) +4),7) -3) * -1 >= DAYOFYEAR(" + weekMode + ") THEN CEIL(((MOD((MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ") || '-01-01' AS DATE)) +5,7) +4),7) -3) + DAYOFYEAR(CAST(YEAR(" + weekMode + ")-1 || '-12-31' AS DATE)) + DAYOFYEAR(" + weekMode + ")) / 7.0) - 53 ELSE CASE WHEN ( DAYOFYEAR(" + weekMode + ") + (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ") || '-01-01' AS DATE)) +5,7) +4),7) -3)) > 364 THEN CASE WHEN ((DAYOFYEAR(CAST(YEAR(" + weekMode + ") || '-12-31' AS DATE)) + (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ") || '-01-01' AS DATE)) +5,7) +4),7) -3)) -364) >3 THEN 53 ELSE 1 END ELSE CEIL(CAST(( DAYOFYEAR(" + weekMode + ") + (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ") || '-01-01' AS DATE)) +5,7) +4),7) -3)) AS DECIMAL) / 7.0) END END)) AS INT) ";
               } else if (vector.get(1).toString().equals("6")) {
                  weekStartDay = " CAST(FLOOR((CASE WHEN (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ") || '-01-01' AS DATE)) +5,7) +4),7) -3) * -1 >= DAYOFYEAR(" + weekMode + ") THEN CEIL(((MOD((MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ") || '-01-01' AS DATE)) +5,7) +4),7) -3) + DAYOFYEAR(CAST(YEAR(" + weekMode + ")-1 || '-12-31' AS DATE)) + DAYOFYEAR(" + weekMode + ")) / 7.0) ELSE CASE WHEN ( DAYOFYEAR(" + weekMode + ") + (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ") || '-01-01' AS DATE)) +5,7) +4),7) -3)) > 364 THEN CASE WHEN ((DAYOFYEAR(CAST(YEAR(" + weekMode + ") || '-12-31' AS DATE)) + (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ") || '-01-01' AS DATE)) +5,7) +4),7) -3)) -364) >3 THEN 53 ELSE 1 END ELSE CEIL(CAST(( DAYOFYEAR(" + weekMode + ") + (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + weekMode + ") || '-01-01' AS DATE)) +5,7) +4),7) -3)) AS DECIMAL) / 7.0) END END)) AS INT) ";
               } else {
                  weekStartDay = " CAST(TO_CHAR(CAST(" + vector.get(0) + " AS DATE),'IW') AS INT) ";
               }
            }

            this.functionName.setColumnName(weekStartDay);
            this.setOpenBracesForFunctionNameRequired(false);
            this.setFunctionArguments(new Vector());
         }
      } else {
         vector = new Vector();

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               vector.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
            } else {
               vector.addElement(this.functionArguments.elementAt(i));
            }
         }

         weekStartDay = "1";
         weekMode = "1";
         String fiscalStartMonth = "1";
         int fiscalStartMonth_int = false;
         int weekMode_int = false;
         int weekStartDay_int = false;
         boolean isJanFiscalStMonth = false;
         boolean isISOWeekMode = false;
         SelectColumn sc;
         Vector vc;
         if (vector.size() == 4) {
            if (vector.elementAt(3) instanceof SelectColumn) {
               sc = (SelectColumn)vector.elementAt(3);
               vc = sc.getColumnExpression();
               if (!(vc.elementAt(0) instanceof String)) {
                  throw new ConvertException("Invalid Argument Value for Function WEEKOFYEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"WEEKOFYEAR", "FISCAL_START_MONTH"});
               }

               fiscalStartMonth = (String)vc.elementAt(0);
               if (fiscalStartMonth.equalsIgnoreCase("null")) {
                  fiscalStartMonth = "1";
               }

               fiscalStartMonth = fiscalStartMonth.replaceAll("'", "");
               this.validateFiscalStartMonth(fiscalStartMonth, fnStr);
            }

            if (fiscalStartMonth.equals("1")) {
               isJanFiscalStMonth = true;
            } else {
               int fiscalStartMonth_int = Integer.parseInt(fiscalStartMonth);
               if (vector.elementAt(2) instanceof SelectColumn) {
                  sc = (SelectColumn)vector.elementAt(2);
                  vc = sc.getColumnExpression();
                  if (!(vc.elementAt(0) instanceof String)) {
                     throw new ConvertException("Invalid Argument Value for Function WEEKOFYEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"WEEKOFYEAR", "WEEK_MODE"});
                  }

                  weekMode = (String)vc.elementAt(0);
                  if (weekMode.equalsIgnoreCase("null")) {
                     weekMode = "1";
                  }

                  weekMode = weekMode.replaceAll("'", "");
                  this.validateWeekMode(weekMode, fnStr);
               }

               if (weekMode.equals("2")) {
                  this.WeekMode2FiscalWeeKQuery(to_sqs, from_sqs, fiscalStartMonth);
               } else {
                  if (vector.elementAt(1) instanceof SelectColumn) {
                     sc = (SelectColumn)vector.elementAt(1);
                     vc = sc.getColumnExpression();
                     if (!(vc.elementAt(0) instanceof String)) {
                        throw new ConvertException("Invalid Argument Value for Function WEEKOFYEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"WEEKOFYEAR", "WEEK_START_DAY"});
                     }

                     weekStartDay = (String)vc.elementAt(0);
                     if (weekStartDay.equalsIgnoreCase("null")) {
                        weekStartDay = "1";
                     }

                     weekStartDay = weekStartDay.replaceAll("'", "");
                     this.validateWeek_Start_Day(weekStartDay, fnStr);
                  }

                  this.WeekMode1Args4(vector, weekStartDay, fiscalStartMonth);
               }
            }
         }

         if (vector.size() == 3 || isJanFiscalStMonth) {
            if (vector.elementAt(2) instanceof SelectColumn) {
               sc = (SelectColumn)vector.elementAt(2);
               vc = sc.getColumnExpression();
               if (!(vc.elementAt(0) instanceof String)) {
                  throw new ConvertException("Invalid Argument Value for Function WEEKOFYEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"WEEKOFYEAR", "WEEK_MODE"});
               }

               weekMode = (String)vc.elementAt(0);
               if (weekMode.equalsIgnoreCase("null")) {
                  weekMode = "1";
               }

               weekMode = weekMode.replaceAll("'", "");
               this.validateWeekMode(weekMode, fnStr);
            }

            if (weekMode.equals("2")) {
               this.WeekMode2WeeKQuery(to_sqs, from_sqs);
            } else {
               isISOWeekMode = true;
            }
         }

         if (vector.size() == 2 || isISOWeekMode) {
            if (vector.elementAt(1) instanceof SelectColumn) {
               sc = (SelectColumn)vector.elementAt(1);
               vc = sc.getColumnExpression();
               if (!(vc.elementAt(0) instanceof String)) {
                  throw new ConvertException("Invalid Argument Value for Function WEEKOFYEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"WEEKOFYEAR", "WEEK_START_DAY"});
               }

               weekStartDay = (String)vc.elementAt(0);
               if (weekStartDay.equalsIgnoreCase("null")) {
                  weekStartDay = "1";
               }

               weekStartDay = weekStartDay.replaceAll("'", "");
               this.validateWeek_Start_Day(weekStartDay, fnStr);
            }

            this.WeekMode1Args2(vector, weekStartDay);
         }

         if (vector.size() == 1) {
            this.WeekMode1Args2(vector, "1");
         }
      }

   }

   public void WeekMode1Args2(Vector vector, String weekStDay) {
      int weekStDay_int = Integer.parseInt(weekStDay);
      Vector vc_week = new Vector();
      vc_week.addElement(vector.get(0));
      if (weekStDay_int == 1) {
         this.functionName.setColumnName("WEEK");
         vc_week.addElement("6");
      } else if (weekStDay_int == 2) {
         this.functionName.setColumnName("WEEK");
         vc_week.addElement("3");
      } else if (weekStDay_int > 2 && weekStDay_int < 8) {
         this.functionName.setColumnName("ZR_WEEKDTNWKSTRTDAY");
         weekStDay_int = getWeekStartDayValue(1, weekStDay_int);
         weekStDay = Integer.toString(weekStDay_int);
         vc_week.addElement(weekStDay);
      }

      this.setFunctionArguments(vc_week);
   }

   public void WeekMode1Args4(Vector vector, String weekStDay, String fiscalStartMonth) {
      Vector vc_fiscalWeek = new Vector();
      vc_fiscalWeek.addElement(vector.get(0));
      vc_fiscalWeek.addElement(fiscalStartMonth);
      int startMonth = Integer.parseInt(fiscalStartMonth);
      String startDate = startMonth < 10 ? "'-0" + startMonth + "-01'" : "'-" + startMonth + "-01'";
      vc_fiscalWeek.addElement(startDate);
      int fiscalStartMonth_int = Integer.parseInt(fiscalStartMonth);
      if (weekStDay.equalsIgnoreCase("0")) {
         this.functionName.setColumnName("ZR_FWEEKDT");
      } else {
         this.functionName.setColumnName("ZR_FWEEKDTNWKSTRTDAY");
         int weekStDay_int = Integer.parseInt(weekStDay);
         weekStDay_int = getWeekStartDayValue(fiscalStartMonth_int, weekStDay_int);
         weekStDay = Integer.toString(weekStDay_int);
         vc_fiscalWeek.addElement(weekStDay);
      }

      this.setFunctionArguments(vc_fiscalWeek);
   }

   public void WeekMode2WeeKQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector vector1 = new Vector();
      Vector vector2 = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector1.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            vector2.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vector1.addElement(this.functionArguments.elementAt(i_count));
            vector2.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String weekStartDay = null;
      if (vector1.elementAt(1) instanceof SelectColumn) {
         SelectColumn sc = (SelectColumn)vector1.elementAt(1);
         Vector vc = sc.getColumnExpression();
         if (!(vc.elementAt(0) instanceof String)) {
            throw new ConvertException("Invalid Argument Value for Function WEEKOFYEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"WEEKOFYEAR", "WEEK_START_DAY"});
         }

         weekStartDay = (String)vc.elementAt(0);
      }

      if (weekStartDay.equalsIgnoreCase("null")) {
         weekStartDay = "1";
      }

      weekStartDay = weekStartDay.replaceAll("'", "");
      this.validateWeek_Start_Day(weekStartDay, "WEEKOFYEAR");
      this.functionName.setColumnName("");
      Vector fnArguments = new Vector();
      SelectColumn sc_ceil = new SelectColumn();
      FunctionCalls fnCl_ceil = new FunctionCalls();
      TableColumn tbCl_ceil = new TableColumn();
      tbCl_ceil.setColumnName("CEIL");
      fnCl_ceil.setFunctionName(tbCl_ceil);
      Vector vc_ceilIn = new Vector();
      Vector vc_ceilOut = new Vector();
      SelectColumn sc_ceilArgsDividend = new SelectColumn();
      Vector vc_ceilArgsDividend = new Vector();
      SelectColumn sc_ceilArgsMultiplicand = new SelectColumn();
      Vector vc_ceilArgsMultiplicand = new Vector();
      SelectColumn sc_partsofMultiplicand = new SelectColumn();
      Vector vc_partsofMultiplicand = new Vector();
      SelectColumn sc_ceilArgsAddend = new SelectColumn();
      FunctionCalls fn_ceilArgsAddend = new FunctionCalls();
      TableColumn tb_ceilArgsAddend = new TableColumn();
      tb_ceilArgsAddend.setColumnName("DAYOFYEAR");
      fn_ceilArgsAddend.setFunctionName(tb_ceilArgsAddend);
      Vector vc_ceilArgsAddendIn = new Vector();
      vc_ceilArgsAddendIn.addElement(vector1.get(0));
      Vector vc_ceilArgsAddendOut = new Vector();
      fn_ceilArgsAddend.setFunctionArguments(vc_ceilArgsAddendIn);
      vc_ceilArgsAddendOut.addElement(fn_ceilArgsAddend);
      sc_ceilArgsAddend.setColumnExpression(vc_ceilArgsAddendOut);
      SelectColumn sc_ceilArgsAdded = new SelectColumn();
      Vector vc_ceilArgsAdded = new Vector();
      SelectColumn sc_ceilArgsAddedDividend = new SelectColumn();
      Vector vc_ceilArgsAddedDividend = new Vector();
      SelectColumn sc_dayofweek = new SelectColumn();
      FunctionCalls fn_dayofweek = new FunctionCalls();
      TableColumn tb_dayofweek = new TableColumn();
      tb_dayofweek.setColumnName("DAYOFWEEK");
      fn_dayofweek.setFunctionName(tb_dayofweek);
      Vector vc_dayofweekIn = new Vector();
      Vector vc_dayofweekOut = new Vector();
      SelectColumn sc_strtodate = new SelectColumn();
      FunctionCalls fn_strtodate = new FunctionCalls();
      TableColumn tb_strtodate = new TableColumn();
      tb_strtodate.setColumnName("STR_TO_DATE");
      fn_strtodate.setFunctionName(tb_strtodate);
      Vector vc_strtodateIn = new Vector();
      Vector vc_strtodateOut = new Vector();
      SelectColumn sc_concat = new SelectColumn();
      FunctionCalls fn_concat = new FunctionCalls();
      TableColumn tb_concat = new TableColumn();
      tb_concat.setColumnName("CONCAT");
      fn_concat.setFunctionName(tb_concat);
      Vector vc_concatIn = new Vector();
      Vector vc_concatOut = new Vector();
      SelectColumn sc_yearInCeilArgs = new SelectColumn();
      FunctionCalls fn_yearInCeilArgs = new FunctionCalls();
      TableColumn tb_yearInCeilArgs = new TableColumn();
      tb_yearInCeilArgs.setColumnName("YEAR");
      fn_yearInCeilArgs.setFunctionName(tb_yearInCeilArgs);
      Vector vc_yearInCeilArgsIn = new Vector();
      Vector vc_yearInCeilArgsOut = new Vector();
      vc_yearInCeilArgsIn.addElement(vector2.get(0));
      fn_yearInCeilArgs.setFunctionArguments(vc_yearInCeilArgsIn);
      vc_yearInCeilArgsOut.addElement(fn_yearInCeilArgs);
      sc_yearInCeilArgs.setColumnExpression(vc_yearInCeilArgsOut);
      vc_concatIn.addElement(sc_yearInCeilArgs);
      vc_concatIn.addElement("'-01-01'");
      fn_concat.setFunctionArguments(vc_concatIn);
      vc_concatOut.addElement(fn_concat);
      sc_concat.setColumnExpression(vc_concatOut);
      vc_strtodateIn.addElement(sc_concat);
      vc_strtodateIn.addElement("'%Y-%m-%d'");
      fn_strtodate.setFunctionArguments(vc_strtodateIn);
      vc_strtodateOut.addElement(fn_strtodate);
      sc_strtodate.setColumnExpression(vc_strtodateOut);
      vc_dayofweekIn.addElement(sc_strtodate);
      fn_dayofweek.setFunctionArguments(vc_dayofweekIn);
      vc_dayofweekOut.addElement(fn_dayofweek);
      sc_dayofweek.setColumnExpression(vc_dayofweekOut);
      vc_ceilArgsAddedDividend.addElement(sc_dayofweek);
      vc_ceilArgsAddedDividend.addElement("+");
      vc_ceilArgsAddedDividend.addElement("7");
      vc_ceilArgsAddedDividend.addElement("-");
      vc_ceilArgsAddedDividend.addElement(weekStartDay);
      sc_ceilArgsAddedDividend.setColumnExpression(vc_ceilArgsAddedDividend);
      vc_ceilArgsAdded.addElement("(");
      vc_ceilArgsAdded.addElement(sc_ceilArgsAddedDividend);
      vc_ceilArgsAdded.addElement(")");
      vc_ceilArgsAdded.addElement("%");
      vc_ceilArgsAdded.addElement("7");
      sc_ceilArgsAdded.setColumnExpression(vc_ceilArgsAdded);
      vc_partsofMultiplicand.addElement(sc_ceilArgsAddend);
      vc_partsofMultiplicand.addElement("+");
      vc_partsofMultiplicand.addElement(sc_ceilArgsAdded);
      sc_partsofMultiplicand.setColumnExpression(vc_partsofMultiplicand);
      vc_ceilArgsMultiplicand.addElement("(");
      vc_ceilArgsMultiplicand.addElement(sc_partsofMultiplicand);
      vc_ceilArgsMultiplicand.addElement(")");
      vc_ceilArgsMultiplicand.addElement("*");
      vc_ceilArgsMultiplicand.addElement("1.0");
      sc_ceilArgsMultiplicand.setColumnExpression(vc_ceilArgsMultiplicand);
      vc_ceilArgsDividend.addElement("(");
      vc_ceilArgsDividend.addElement(sc_ceilArgsMultiplicand);
      vc_ceilArgsDividend.addElement(")");
      vc_ceilArgsDividend.addElement("/");
      vc_ceilArgsDividend.addElement("7");
      sc_ceilArgsDividend.setColumnExpression(vc_ceilArgsDividend);
      vc_ceilIn.addElement(sc_ceilArgsDividend);
      fnCl_ceil.setFunctionArguments(vc_ceilIn);
      vc_ceilOut.addElement(fnCl_ceil);
      sc_ceil.setColumnExpression(vc_ceilOut);
      fnArguments.addElement(sc_ceil);
      this.setFunctionArguments(fnArguments);
   }

   public void WeekMode2FiscalWeeKQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, String fiscalStartMonth) throws ConvertException {
      Vector vector = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vector.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String weekStartDay = "";
      if (vector.elementAt(1) instanceof SelectColumn) {
         SelectColumn sc = (SelectColumn)vector.elementAt(1);
         Vector vc = sc.getColumnExpression();
         if (!(vc.elementAt(0) instanceof String)) {
            throw new ConvertException("Invalid Argument Value for Function WEEKOFYEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"WEEKOFYEAR", "WEEK_START_DAY"});
         }

         weekStartDay = (String)vc.elementAt(0);
      }

      if (weekStartDay.equalsIgnoreCase("null")) {
         weekStartDay = "1";
      }

      weekStartDay = weekStartDay.replaceAll("'", "");
      this.validateWeek_Start_Day(weekStartDay, "WEEKOFYEAR");
      this.functionName.setColumnName("");
      Vector fnArguments = new Vector();
      SelectColumn sc_ceil = new SelectColumn();
      FunctionCalls fnCl_ceil = new FunctionCalls();
      TableColumn tbCl_ceil = new TableColumn();
      tbCl_ceil.setColumnName("CEIL");
      fnCl_ceil.setFunctionName(tbCl_ceil);
      Vector vc_ceilIn = new Vector();
      Vector vc_ceilOut = new Vector();
      SelectColumn sc_ceilArgs = new SelectColumn();
      Vector vc_ceilArgs = new Vector();
      SelectColumn sc_ceilDividend = new SelectColumn();
      Vector vc_ceilDividend = new Vector();
      SelectColumn sc_ceilMultiplicand = new SelectColumn();
      Vector vc_ceilMultiplicand = new Vector();
      SelectColumn sc_datediffAddend = new SelectColumn();
      FunctionCalls fn_datediffAddend = new FunctionCalls();
      TableColumn tb_datediffAddend = new TableColumn();
      tb_datediffAddend.setColumnName("DATEDIFF");
      fn_datediffAddend.setFunctionName(tb_datediffAddend);
      Vector vc_datediffAddendIn = new Vector();
      Vector vc_datediffAddendOut = new Vector();
      vc_datediffAddendIn.addElement(vector.get(0));
      vc_datediffAddendIn.addElement(this.WeekMode2FiscalYear(to_sqs, from_sqs, fiscalStartMonth));
      fn_datediffAddend.setFunctionArguments(vc_datediffAddendIn);
      vc_datediffAddendOut.addElement(fn_datediffAddend);
      sc_datediffAddend.setColumnExpression(vc_datediffAddendOut);
      SelectColumn sc_Added = new SelectColumn();
      Vector vc_Added = new Vector();
      SelectColumn sc_AddedDividend = new SelectColumn();
      Vector vc_AddedDividend = new Vector();
      SelectColumn sc_dayofWeek = new SelectColumn();
      FunctionCalls fn_dayofWeek = new FunctionCalls();
      TableColumn tb_dayofWeek = new TableColumn();
      tb_dayofWeek.setColumnName("DAYOFWEEK");
      fn_dayofWeek.setFunctionName(tb_dayofWeek);
      Vector vc_dayofWeekIn = new Vector();
      vc_dayofWeekIn.addElement(this.WeekMode2FiscalYear(to_sqs, from_sqs, fiscalStartMonth));
      Vector vc_dayofWeekOut = new Vector();
      fn_dayofWeek.setFunctionArguments(vc_dayofWeekIn);
      vc_dayofWeekOut.addElement(fn_dayofWeek);
      sc_dayofWeek.setColumnExpression(vc_dayofWeekOut);
      vc_AddedDividend.addElement(sc_dayofWeek);
      vc_AddedDividend.addElement("+");
      vc_AddedDividend.addElement("7");
      vc_AddedDividend.addElement("-");
      vc_AddedDividend.addElement(weekStartDay);
      sc_AddedDividend.setColumnExpression(vc_AddedDividend);
      vc_Added.addElement("(");
      vc_Added.addElement(sc_AddedDividend);
      vc_Added.addElement(")");
      vc_Added.addElement("%");
      vc_Added.addElement("7");
      sc_Added.setColumnExpression(vc_Added);
      vc_ceilMultiplicand.addElement("(");
      vc_ceilMultiplicand.addElement(sc_datediffAddend);
      vc_ceilMultiplicand.addElement("+");
      vc_ceilMultiplicand.addElement("1");
      vc_ceilMultiplicand.addElement("+");
      vc_ceilMultiplicand.addElement(sc_Added);
      vc_ceilMultiplicand.addElement(")");
      sc_ceilMultiplicand.setColumnExpression(vc_ceilMultiplicand);
      vc_ceilDividend.addElement("(");
      vc_ceilDividend.addElement(sc_ceilMultiplicand);
      vc_ceilDividend.addElement("*");
      vc_ceilDividend.addElement("1.0");
      vc_ceilDividend.addElement(")");
      sc_ceilDividend.setColumnExpression(vc_ceilDividend);
      vc_ceilArgs.addElement(sc_ceilDividend);
      vc_ceilArgs.addElement("/");
      vc_ceilArgs.addElement("7");
      sc_ceilArgs.setColumnExpression(vc_ceilArgs);
      vc_ceilIn.addElement(sc_ceilArgs);
      fnCl_ceil.setFunctionArguments(vc_ceilIn);
      vc_ceilOut.addElement(fnCl_ceil);
      sc_ceil.setColumnExpression(vc_ceilOut);
      fnArguments.addElement(sc_ceil);
      this.setFunctionArguments(fnArguments);
   }

   public SelectColumn WeekMode2FiscalYear(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, String fiscalStartMonth) throws ConvertException {
      Vector vector1 = new Vector();
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

      SelectColumn sc_dateDiffStr_To_Date = new SelectColumn();
      FunctionCalls fn_dateDiffStr_To_Date = new FunctionCalls();
      TableColumn tb_dateDiffStr_To_Date = new TableColumn();
      tb_dateDiffStr_To_Date.setColumnName("STR_TO_DATE");
      fn_dateDiffStr_To_Date.setFunctionName(tb_dateDiffStr_To_Date);
      Vector vc_dateDiffStr_To_DateIn = new Vector();
      Vector vc_dateDiffStr_To_DateOut = new Vector();
      SelectColumn sc_dateDiffConcat = new SelectColumn();
      FunctionCalls fn_dateDiffConcat = new FunctionCalls();
      TableColumn tb_dateDiffConcat = new TableColumn();
      tb_dateDiffConcat.setColumnName("CONCAT");
      fn_dateDiffConcat.setFunctionName(tb_dateDiffConcat);
      Vector vc_dateDiffConcatIn = new Vector();
      Vector vc_dateDiffConcatOut = new Vector();
      SelectColumn sc_ifYearWeekFloor = new SelectColumn();
      FunctionCalls fn_ifYearWeekFloor = new FunctionCalls();
      TableColumn tb_ifYearWeekFloor = new TableColumn();
      tb_ifYearWeekFloor.setColumnName("IF");
      fn_ifYearWeekFloor.setFunctionName(tb_ifYearWeekFloor);
      Vector vc_ifYearWeekFloorIn = new Vector();
      Vector vc_ifYearWeekFloorOut = new Vector();
      SelectColumn sc_ifYWFCon = new SelectColumn();
      Vector vc_ifYWFCon = new Vector();
      WhereItem whIt_YWCon = new WhereItem();
      WhereColumn whCol_YWConLeftExp = new WhereColumn();
      Vector vc_YWConLeftExp = new Vector();
      SelectColumn sc_yearWeekMonth = new SelectColumn();
      FunctionCalls fn_yearWeekMonth = new FunctionCalls();
      TableColumn tb_yearWeekMonth = new TableColumn();
      tb_yearWeekMonth.setColumnName("MONTH");
      fn_yearWeekMonth.setFunctionName(tb_yearWeekMonth);
      Vector vc_yearWeekMonthIn = new Vector();
      Vector vc_yearWeekMonthOut = new Vector();
      if (vector1.elementAt(0) instanceof SelectColumn) {
         SelectColumn sc = (SelectColumn)vector1.elementAt(0);
         vc_yearWeekMonthIn.addElement(sc);
      } else {
         vc_yearWeekMonthIn.addElement(vector1.elementAt(0));
      }

      fn_yearWeekMonth.setFunctionArguments(vc_yearWeekMonthIn);
      vc_yearWeekMonthOut.addElement(fn_yearWeekMonth);
      sc_yearWeekMonth.setColumnExpression(vc_yearWeekMonthOut);
      vc_YWConLeftExp.addElement(sc_yearWeekMonth);
      whCol_YWConLeftExp.setColumnExpression(vc_YWConLeftExp);
      whIt_YWCon.setLeftWhereExp(whCol_YWConLeftExp);
      whIt_YWCon.setOperator("<");
      WhereColumn whCol_YWConRightExp = new WhereColumn();
      Vector vc_YWConRightExp = new Vector();
      vc_YWConRightExp.addElement(fiscalStartMonth);
      whCol_YWConRightExp.setColumnExpression(vc_YWConRightExp);
      whIt_YWCon.setRightWhereExp(whCol_YWConRightExp);
      vc_ifYWFCon.addElement(whIt_YWCon);
      sc_ifYWFCon.setColumnExpression(vc_ifYWFCon);
      SelectColumn sc_YWTrueStmt = new SelectColumn();
      Vector vc_YWTrueStmt = new Vector();
      SelectColumn sc_YWYear = new SelectColumn();
      FunctionCalls fn_YWYear = new FunctionCalls();
      TableColumn tb_YWYear = new TableColumn();
      tb_YWYear.setColumnName("YEAR");
      fn_YWYear.setFunctionName(tb_YWYear);
      Vector vc_YWYearIn = new Vector();
      Vector vc_YWYearOut = new Vector();
      SelectColumn sc_YWFalseStmt;
      if (vector3.elementAt(0) instanceof SelectColumn) {
         sc_YWFalseStmt = (SelectColumn)vector3.elementAt(0);
         vc_YWYearIn.addElement(sc_YWFalseStmt);
      } else {
         vc_YWYearIn.addElement(vector3.elementAt(0));
      }

      fn_YWYear.setFunctionArguments(vc_YWYearIn);
      vc_YWYearOut.addElement(fn_YWYear);
      sc_YWYear.setColumnExpression(vc_YWYearOut);
      vc_YWTrueStmt.addElement("(");
      vc_YWTrueStmt.addElement(sc_YWYear);
      vc_YWTrueStmt.addElement("-");
      vc_YWTrueStmt.addElement("1");
      vc_YWTrueStmt.addElement(")");
      sc_YWTrueStmt.setColumnExpression(vc_YWTrueStmt);
      sc_YWFalseStmt = new SelectColumn();
      FunctionCalls fn_YWFalseStmt = new FunctionCalls();
      TableColumn tb_YWFalseStmt = new TableColumn();
      tb_YWFalseStmt.setColumnName("YEAR");
      fn_YWFalseStmt.setFunctionName(tb_YWFalseStmt);
      Vector vc_YWFalseStmtIn = new Vector();
      Vector vc_YWFalseStmtOut = new Vector();
      if (vector2.elementAt(0) instanceof SelectColumn) {
         SelectColumn sc = (SelectColumn)vector2.elementAt(0);
         vc_YWFalseStmtIn.addElement(sc);
      } else {
         vc_YWFalseStmtIn.addElement(vector2.elementAt(0));
      }

      fn_YWFalseStmt.setFunctionArguments(vc_YWFalseStmtIn);
      vc_YWFalseStmtOut.addElement(fn_YWFalseStmt);
      sc_YWFalseStmt.setColumnExpression(vc_YWFalseStmtOut);
      vc_ifYearWeekFloorIn.addElement(sc_ifYWFCon);
      vc_ifYearWeekFloorIn.addElement(sc_YWTrueStmt);
      vc_ifYearWeekFloorIn.addElement(sc_YWFalseStmt);
      fn_ifYearWeekFloor.setFunctionArguments(vc_ifYearWeekFloorIn);
      vc_ifYearWeekFloorOut.addElement(fn_ifYearWeekFloor);
      sc_ifYearWeekFloor.setColumnExpression(vc_ifYearWeekFloorOut);
      vc_dateDiffConcatIn.addElement(sc_ifYearWeekFloor);
      vc_dateDiffConcatIn.addElement("'-'");
      vc_dateDiffConcatIn.addElement(fiscalStartMonth);
      vc_dateDiffConcatIn.addElement("'-01'");
      fn_dateDiffConcat.setFunctionArguments(vc_dateDiffConcatIn);
      vc_dateDiffConcatOut.addElement(fn_dateDiffConcat);
      sc_dateDiffConcat.setColumnExpression(vc_dateDiffConcatOut);
      vc_dateDiffStr_To_DateIn.addElement(sc_dateDiffConcat);
      vc_dateDiffStr_To_DateIn.addElement("'%Y-%m-%d'");
      fn_dateDiffStr_To_Date.setFunctionArguments(vc_dateDiffStr_To_DateIn);
      vc_dateDiffStr_To_DateOut.addElement(fn_dateDiffStr_To_Date);
      sc_dateDiffStr_To_Date.setColumnExpression(vc_dateDiffStr_To_DateOut);
      return sc_dateDiffStr_To_Date;
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      Boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("convert_tz")) {
         int finalMins = 0;

         try {
            if (arguments.size() == 3 && arguments.get(1) != null && arguments.get(2) != null && arguments.get(1).toString().contains(":") && arguments.get(2).toString().contains(":")) {
               String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
               String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
               int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
               int currentTZMin = Integer.parseInt(currentTZ[1]);
               int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
               int toTZMin = Integer.parseInt(toTZ[1]);
               int toTZOverallMins = toTZHour * 60 + toTZMin;
               int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
               if (currentTZ[0].contains("-")) {
                  currentTZHour *= -1;
               }

               if (toTZ[0].contains("-")) {
                  toTZHour *= -1;
               }

               if (currentTZHour < 0) {
                  if (toTZHour < 0) {
                     finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                  } else {
                     finalMins = toTZOverallMins - currentTZOverallMins * -1;
                  }
               } else if (toTZHour < 0) {
                  finalMins = toTZOverallMins * -1 - currentTZOverallMins;
               } else {
                  finalMins = toTZOverallMins - currentTZOverallMins;
               }
            }
         } catch (Exception var15) {
         }

         qry = "CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) + ((interval '1' minute) * " + finalMins + ")";
      } else {
         String datestring;
         if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
            datestring = isdenodo ? StringFunctions.handleLiteralStringDateForDenodo(arguments.get(0).toString()) : StringFunctions.handleLiteralStringDateForOracle(arguments.get(0).toString());
            if (arguments.size() < 2) {
               if (isdenodo) {
                  qry = "CAST((floor(( CAST(" + datestring + " AS DATE) - NEXTWEEKDAY(ADDDAY( TRUNC( CAST(" + datestring + " AS DATE), 'YY' ),-1),0) ) / 7.0) + 1) AS INT4)";
               } else {
                  qry = "TO_NUMBER(floor(( CAST(" + datestring + " AS DATE) - NEXT_DAY( TRUNC( CAST(" + datestring + " AS DATE), 'YY' ) - INTERVAL '1' DAY, 'SUNDAY' ) ) / 7) + 1)";
               }
            } else if (arguments.get(1).toString().equals("6")) {
               if (isdenodo) {
                  qry = "cast(CASE WHEN (MOD(( mod(cast ((1+(trunc(cast((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int4)  || '-01-01') as date))-trunc(cast((cast(extract (year from CAST(" + datestring + " AS DATE)) as int4)  || '-01-01') as date),'IW'))) as int4) +6, 7) + 4), 7) -3) * - 1 >= cast(GETDAYOFYEAR(" + datestring + ") as int4) THEN SUBSTR((cast((100+CEIL(((MOD(( mod(cast ((1+(trunc(cast(((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int4)-1)  || '-01-01') as date))-trunc(cast(((cast(extract (year from CAST(" + datestring + " AS DATE)) as int4)-1)  || '-01-01') as date),'IW'))) as int4) +6, 7) + 4), 7) -3)+ cast(GETDAYOFYEAR(cast(((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int4)-1)  || '-12-31') as date)) as int4)+ cast(GETDAYOFYEAR(" + datestring + ") as int4))/ ((case when 7=0 then null else 7 end)*1.0))) as char(100))),2) ELSE CASE WHEN (cast(GETDAYOFYEAR(" + datestring + ") as int4) + (MOD(( mod(cast ((1+(trunc(cast((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int4)  || '-01-01') as date))-trunc(cast((cast(extract (year from CAST(" + datestring + " AS DATE)) as int4)  || '-01-01') as date),'IW'))) as int4) +6, 7) + 4), 7) -3)) > 364 THEN CASE WHEN ((cast(GETDAYOFYEAR(cast((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int4)  || '-12-31') as date)) as int4)+(MOD(( mod(cast ((1+(trunc(cast((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int4)  || '-01-01') as date))-trunc(cast((cast(extract (year from CAST(" + datestring + " AS DATE)) as int4)  || '-01-01') as date),'IW'))) as int4) +6, 7) + 4), 7) -3)) -364) >3 THEN ('53') ELSE ('01') END ELSE SUBSTR((cast((100+ CEIL((cast(GETDAYOFYEAR(" + datestring + ") as int4)+(MOD(( mod(cast ((1+(trunc(cast((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int4)  || '-01-01') as date))-trunc(cast((cast(extract (year from CAST(" + datestring + " AS DATE)) as int4)  || '-01-01') as date),'IW'))) as int4) +6, 7) + 4), 7) -3))/ ((case when 7=0 then null else 7 end)*1.0))) as char(100))),2) END END AS int4)";
               } else {
                  qry = "cast(CASE    WHEN (MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + 4), 7) -3) * - 1 >= cast(to_char(" + datestring + ",'FMDDD') as int) THEN SUBSTR((cast((100+CEIL(((MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)-1  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)-1  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + 4), 7) -3)+ cast(to_char(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)-1  || '-12-31'),'YYYY-MM-DD'),'FMDDD') as int)+ cast(to_char(" + datestring + ",'FMDDD') as int))/ ((case when 7=0 then null else 7 end)*1.0))) as char(100))),2) ELSE CASE WHEN (cast(to_char(" + datestring + ",'FMDDD') as int) + (MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + 4), 7) -3)) >364 THEN CASE WHEN ((cast(to_char(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-12-31'),'YYYY-MM-DD'),'FMDDD') as int)+(MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + 4), 7) -3)) -364) >3 THEN ('53') ELSE ('01') END ELSE SUBSTR((cast((100+CEIL((cast(to_char(" + datestring + ",'FMDDD') as int)+(MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + 4), 7) -3))/ ((case when 7=0 then null else 7 end)*1.0))) as char(100))),2) END END AS int)";
               }
            } else if (isdenodo) {
               qry = "GETWEEK(trunc(CAST(" + datestring + " AS DATE),'IW'))";
            } else {
               qry = "CAST(TO_CHAR(CAST(" + datestring + " AS DATE),'IW') AS int)";
            }
         } else if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
            datestring = isdenodo ? StringFunctions.handleLiteralStringDateForDenodo(arguments.get(0).toString()) : StringFunctions.handleLiteralStringDateForOracle(arguments.get(0).toString());
            if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
               if (isdenodo) {
                  qry = "CAST((GETYEAR(" + datestring + ") || (CASE WHEN GETWEEK(TRUNC(" + datestring + ",'IW')) < 10 THEN 0 || GETWEEK(TRUNC(" + datestring + ",'IW')) ELSE CAST(GETWEEK(TRUNC(" + datestring + ",'IW')) AS TEXT) END)) AS INT4)";
               } else {
                  qry = "CAST(TO_CHAR(" + datestring + ",'IYYYIW') AS int)";
               }
            } else if (isdenodo) {
               qry = "CAST(CASE WHEN (MOD(( MOD(CAST ((1+(TRUNC(CAST((CAST(EXTRACT (YEAR FROM " + datestring + ") AS INT4) || '-01-01') AS DATE))-TRUNC(CAST((CAST(EXTRACT (YEAR FROM " + datestring + ") AS INT4) || '-01-01') AS DATE),'IW'))) AS INT4) +6, 7) + 4), 7) -3) * -1 >= CAST(GETDAYOFYEAR(" + datestring + ") AS INT4) THEN ((CAST(EXTRACT(YEAR FROM " + datestring + ") AS INT4))-1) || SUBSTR((CAST((100+CEIL(((MOD(( MOD(CAST(( 1+(TRUNC(CAST(((CAST(EXTRACT (YEAR FROM " + datestring + ") AS INT4)-1) || '-01-01') AS DATE))-TRUNC(CAST(((CAST(EXTRACT (YEAR FROM " + datestring + ") AS INT4)-1) || '-01-01') AS DATE),'IW'))) AS INT4) +6, 7) + 4), 7) -3)+CAST(GETDAYOFYEAR(CAST(((CAST(EXTRACT (YEAR FROM " + datestring + ") AS INT4)-1) || '-12-31') AS DATE)) AS INT4)+CAST(GETDAYOFYEAR(" + datestring + ") AS INT4)) / ((CASE WHEN 7=0 THEN NULL ELSE 7 END)*1.0))) AS CHAR(100))),2) ELSE CASE WHEN (CAST(GETDAYOFYEAR(" + datestring + ") AS INT4) + (MOD((MOD(CAST((1+(TRUNC(CAST((CAST(EXTRACT (YEAR FROM " + datestring + ") AS INT4) || '-01-01') AS DATE))-TRUNC(CAST((CAST(EXTRACT(YEAR FROM " + datestring + ") AS INT4) || '-01-01') AS DATE),'IW'))) AS INT4) +6, 7) +4), 7) -3)) > 364 THEN CASE WHEN ((CAST(GETDAYOFYEAR(CAST((CAST(EXTRACT(YEAR FROM " + datestring + ") AS INT4) || '-12-31') AS DATE)) AS INT4)+(MOD(( MOD(CAST((1+(TRUNC(CAST((CAST(EXTRACT (YEAR FROM " + datestring + ") AS INT4) || '-01-01') AS DATE))-TRUNC(CAST((CAST(EXTRACT (YEAR FROM " + datestring + ") AS INT4) || '-01-01') AS DATE),'IW'))) AS INT4) +6, 7) +4), 7) -3)) -364) > 3 THEN (CAST(EXTRACT (YEAR FROM " + datestring + ") AS INT4) || '53') ELSE ((CAST(EXTRACT (YEAR FROM " + datestring + ") AS INT4) +1) || '01') END ELSE CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || SUBSTR((CAST((100+CEIL((CAST(GETDAYOFYEAR(" + datestring + ") AS INT4)+(MOD(( MOD(CAST ((1+(TRUNC(CAST((CAST(EXTRACT( YEAR FROM " + datestring + ") AS INT4) || '-01-01') AS DATE)) - TRUNC(CAST((CAST(EXTRACT (YEAR FROM " + datestring + ") AS INT4) || '-01-01') AS DATE),'IW'))) AS INT4) +6, 7) +4), 7) -3)) / ((CASE WHEN 7=0 THEN NULL ELSE 7 END) * 1.0))) AS CHAR(100))),2) END END AS INT4)";
            } else {
               qry = "cast(CASE    WHEN (MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + 4), 7) -3) * - 1 >= cast(to_char(" + datestring + ",'FMDDD') as int) THEN (cast(extract(year from cast(" + datestring + " as date))as int))-1 ||SUBSTR((cast((100+CEIL(((MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)-1  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)-1  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + 4), 7) -3)+ cast(to_char(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)-1  || '-12-31'),'YYYY-MM-DD'),'FMDDD') as int)+ cast(to_char(" + datestring + ",'FMDDD') as int))/ ((case when 7=0 then null else 7 end)*1.0))) as char(100))),2) ELSE CASE WHEN (cast(to_char(" + datestring + ",'FMDDD') as int) + (MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + 4), 7) -3)) >364 THEN CASE WHEN ((cast(to_char(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-12-31'),'YYYY-MM-DD'),'FMDDD') as int)+(MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + 4), 7) -3)) -364) >3 THEN (cast(extract (year from  CAST(" + datestring + " AS DATE)) as int) ||'53') ELSE (cast(extract (year from  CAST(" + datestring + " AS DATE)) as int) + 1 ||'01') END ELSE cast(extract (year from  CAST(" + datestring + " AS DATE)) as int) ||SUBSTR((cast((100+CEIL((cast(to_char(" + datestring + ",'FMDDD') as int)+(MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + 4), 7) -3))/ ((case when 7=0 then null else 7 end)*1.0))) as char(100))),2) END END AS int)";
            }
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0 && ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().get(0) instanceof String) {
               String dateString = ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().get(0).toString();
               dateString = "CAST(" + this.handleStringLiteralForDateTime(dateString, from_sqs) + " AS DATE)";
               ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().set(0, dateString);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = " cast(extract(week from " + qry + arguments.get(0) + ") as int) ";
         } else if (arguments.size() >= 2) {
            qry = " cast(extract(week from " + arguments.get(0) + ") as int64) ";
         } else {
            qry = " CASE WHEN (cast(extract(week from " + arguments.get(0) + ") as int64)) = 0 THEN CASE WHEN  (cast(extract(DAYOFWEEK from " + arguments.get(0) + ") as int64)) < 5 THEN 1 ELSE 53 END ELSE (cast(extract(week from " + arguments.get(0) + ") as int64)) END";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("weekofyear")) {
         qry = " cast(extract(week from  " + qry + arguments.get(0) + ") as int64) ";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = "cast(FORMAT_TIMESTAMP('%G%V',CAST(" + arguments.get(0) + " AS TIMESTAMP)) as int64)";
         } else {
            qry = "cast(  CASE    WHEN (MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + 4), 7) -3) * - 1  >=  cast(FORMAT_TIMESTAMP('%j' ,CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as INT64) THEN ((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  -1) || SUBSTR(CAST((100 + CEIL(((MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST(((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  -1) || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + 4), 7) -3) +  cast(FORMAT_TIMESTAMP('%j' ,CAST(((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  -1) || '-12-31') AS TIMESTAMP)) as INT64) +  cast(FORMAT_TIMESTAMP('%j' ,CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as INT64)) / ((case when 7=0 then null else 7 end)*1.0))) AS STRING),2))    ELSE     CASE      WHEN ( cast(FORMAT_TIMESTAMP('%j' ,CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as INT64) + (MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + 4), 7) -3))  > 364 THEN       CASE        WHEN (( cast(FORMAT_TIMESTAMP('%j' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-12-31') AS TIMESTAMP)) as INT64) + (MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + 4), 7) -3)) -364)  > 3 THEN (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '53')        ELSE ((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  + 1) || '01')       END      ELSE (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || SUBSTR(CAST((100 + CEIL(( cast(FORMAT_TIMESTAMP('%j' ,CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as INT64) + (MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + 4), 7) -3)) / ((case when 7=0 then null else 7 end)*1.0))) AS STRING),2))     END   END as INT64)";
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
         if (arguments.size() < 2) {
            qry = "cast(floor(cast(date_diff('day',date_add('day', 7 - extract(day_of_week from date_trunc('year'," + dateString + ")), date_trunc('year'," + dateString + "))," + dateString + ") as decimal) /7.0) +1 as INT)";
         } else if (arguments.get(1).toString().equals("6")) {
            qry = "CAST((CASE      WHEN (MOD(( mod(DOW(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE)) +6, 7) + 4), 7) -3) * - 1  >=  DOY(" + dateString + ") THEN CEIL(((MOD(( mod(DOW(CAST(concat(CAST(YEAR(" + dateString + ")-1 AS VARCHAR),'-01-01') AS DATE)) +6, 7) + 4), 7) -3) +  DOY(CAST(concat(CAST(YEAR(" + dateString + ")-1 AS VARCHAR),'-12-31') AS DATE)) +  DOY(" + dateString + ")) / 7.0)      ELSE       CASE        WHEN ( DOY(" + dateString + ") + (MOD(( mod(DOW(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE)) +6, 7) + 4), 7) -3))  > 364 THEN         CASE          WHEN (( DOY(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-12-31') AS DATE)) + (MOD(( mod(DOW(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE)) +6, 7) + 4), 7) -3)) -364)  > 3 THEN 53          ELSE 1         END        ELSE CEIL(( DOY(" + dateString + ") + (MOD(( mod(DOW(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE)) +6, 7) + 4), 7) -3)) / 7.0)       END     END) AS INTEGER)";
         } else {
            qry = " cast(WEEK(" + dateString + ") as INT) ";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("weekofyear")) {
         qry = " cast(WEEK(" + dateString + ") as INT)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = " CAST(CAST(YEAR_OF_WEEK(" + dateString + ") AS VARCHAR) || LPAD(CAST(WEEK(" + dateString + ") AS VARCHAR),2,'0') AS INT) ";
         } else {
            qry = "cast(  CASE    WHEN (MOD(( mod(DOW(CAST(CONCAT(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE)) +6, 7) + 4), 7) -3) * - 1  >=  DOY(" + dateString + ") THEN CONCAT(CAST(YEAR(" + dateString + ") -1 as VARCHAR) , SUBSTR(CAST(CAST((100 + CEIL(((MOD(( mod(DOW(CAST(CONCAT(CAST(YEAR(" + dateString + ")-1 AS VARCHAR),'-01-01') AS DATE)) +6, 7) + 4), 7) -3) +  DOY(CAST(CONCAT(CAST(YEAR(" + dateString + ")-1 AS VARCHAR),'-12-31') AS DATE)) +  DOY(" + dateString + ")) / 7.0)) AS BIGINT) AS VARCHAR),2))    ELSE     CASE      WHEN ( DOY(" + dateString + ") + (MOD(( mod(DOW(CAST(CONCAT(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE)) +6, 7) + 4), 7) -3))  > 364 THEN       CASE        WHEN (( DOY(CAST(CONCAT(CAST(YEAR(" + dateString + ") AS VARCHAR),'-12-31') AS DATE)) + (MOD(( mod(DOW(CAST(CONCAT(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE)) +6, 7) + 4), 7) -3)) -364)  > 3 THEN CONCAT(CAST(YEAR(" + dateString + ") AS VARCHAR)  , '53')        ELSE CONCAT(CAST(YEAR(" + dateString + ")  + 1 AS VARCHAR) , '01')       END      ELSE CONCAT(CAST(YEAR(" + dateString + ")  as VARCHAR) , SUBSTR(CAST(CAST((100 + CEIL(( DOY(" + dateString + ") + (MOD(( mod(DOW(CAST(CONCAT(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE)) +6, 7) + 4), 7) -3)) / 7.0)) AS BIGINT) AS VARCHAR),2))     END   END as INTEGER)";
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = " CAST(CAST(YEAR(" + dateString + ") AS VARCHAR) || LPAD(CAST(WEEK(" + dateString + ") AS VARCHAR),2,'0') AS INT) ";
         } else if (arguments.size() >= 2) {
            qry = "FLOOR(CASE WHEN (MOD(( mod(cast(MOD(WEEKDAY(CAST(YEAR(" + dateString + ") AS VARCHAR) || '-01-01')+1,7) as int) +6, 7) +4), 7) -3) * - 1 >= cast(DAYOFYEAR(" + dateString + ") as int) THEN (cast(YEAR(" + dateString + ") - 1 as varchar) || SUBSTRING(cast((100+CEIL(((MOD(( mod(cast(mod(WEEKDAY(YEAR(" + dateString + ")-1 || '-01-01')+1,7) as int) +6, 7) + 4),7) -3) + cast(DAYOFYEAR(cast(YEAR(" + dateString + ")-1 as varchar) || '-12-31') as int) + cast (DAYOFYEAR(" + dateString + ") as int)) / ((case when 7=0 then null else 7 end)*1.0))) as varchar),2)) ELSE CASE WHEN ( cast(DAYOFYEAR(" + dateString + ") as int) + (MOD(( mod(cast(MOD(WEEKDAY(cast(YEAR(" + dateString + ") as varchar) || '-01-01')+1,7) as int) +6, 7) +4), 7) -3)) > 364 THEN CASE WHEN ((cast(DAYOFYEAR(cast(YEAR(" + dateString + ") as varchar) || '-12-31') as int)+(MOD(( mod(cast(mod(WEEKDAY(cast(YEAR(" + dateString + ") as varchar) || '-01-01')+1,7) as int) +6, 7) +4), 7) -3)) -364) > 3 THEN (cast(YEAR(" + dateString + ") as varchar) || '53') ELSE (cast(YEAR(" + dateString + ")+1 as varchar) || '01') END ELSE (cast(YEAR(" + dateString + ") as varchar) || SUBSTRING(cast((100 + CEIL((cast(DAYOFYEAR(" + dateString + ") as int) + (MOD(( mod(cast(MOD(WEEKDAY(cast(YEAR(" + dateString + ") as varchar) || '-01-01')+1,7) as int) +6, 7) +4), 7) -3)) / ((case when 7=0 then null else 7 end)*1.0))) as varchar),2)) END END)";
         } else {
            qry = "case when cast(floor(cast((DAYS_BETWEEN(ADD_DAYS(YEAR(" + dateString + ") || '-01-01',7 - MOD(WEEKDAY(YEAR(" + dateString + ") || '-01-01')+1,7))," + dateString + ")) as decimal) / 7.0) +1 as int) = 0 then case when((MOD(YEAR(" + dateString + ")-1,4) = 0 and MOD(YEAR(" + dateString + ")-1,100) !=0) or (MOD(YEAR(" + dateString + ")-1,400)=0)) then YEAR(" + dateString + ")-1 || '53' else YEAR(" + dateString + ")-1 || '52' end when cast(floor(cast((DAYS_BETWEEN(ADD_DAYS(YEAR(" + dateString + ") || '-01-01',7 - MOD(WEEKDAY(YEAR(" + dateString + ") || '-01-01')+1,7))," + dateString + ")) as decimal) / 7.0) +1 as int) < 10 then (YEAR(" + dateString + ") || '0' || cast(floor(cast((DAYS_BETWEEN(ADD_DAYS(YEAR(" + dateString + ") || '-01-01',7 - MOD(WEEKDAY(YEAR(" + dateString + ") || '-01-01')+1,7))," + dateString + ")) as decimal) / 7.0) +1 as int)) else YEAR(" + dateString + ") || cast(floor(cast((DAYS_BETWEEN(ADD_DAYS(YEAR(" + dateString + ") || '-01-01',7 - MOD(WEEKDAY(YEAR(" + dateString + ") || '-01-01')+1,7))," + dateString + ")) as decimal) / 7.0) +1 as int) end";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = "CAST(WEEK(" + dateString + ") AS INT)";
         } else if (arguments.size() >= 2) {
            qry = "FLOOR((CASE WHEN (MOD((mod(mod(cast(WEEKDAY(cast(YEAR(" + dateString + ") as int) || '-01-01') as int)+1,7) +6,7) +4),7) -3) * -1 >= cast(DAYOFYEAR(" + dateString + ") as int) THEN CEIL(((MOD((mod(mod(cast(WEEKDAY(cast(YEAR(" + dateString + ") as int) || '-01-01') as int)+1,7) +6,7) +4),7) -3) + cast(DAYOFYEAR(YEAR(" + dateString + ") - 1 || '-12-31') as int) + cast(DAYOFYEAR(" + dateString + ") as int)) / ((case when 7=0 then null else 7 end)*1.0)) ELSE CASE WHEN ( cast(DAYOFYEAR(" + dateString + ") as int) + (MOD((mod(mod(cast(WEEKDAY(cast(YEAR(" + dateString + ") as int) || '-01-01') as int)+1,7) +6,7) +4),7) -3)) > 364 THEN CASE WHEN ((cast(DAYOFYEAR(YEAR(" + dateString + ") || '-12-31') as int) + (MOD((mod(mod(cast(WEEKDAY(cast(YEAR(" + dateString + ") as int) || '-01-01') as int)+1,7) +6,7) +4),7) -3)) -364) >3 THEN 53 ELSE 1 END ELSE CEIL(( cast(DAYOFYEAR(" + dateString + ") as int) + (MOD((mod(mod(cast(WEEKDAY(cast(YEAR(" + dateString + ") as int) || '-01-01') as int)+1,7) +6,7) +4),7) -3)) / ((case when 7=0 then null else 7 end)*1.0)) END END))";
         } else {
            qry = "cast(floor(cast((DAYS_BETWEEN(ADD_DAYS(YEAR(" + dateString + ") || '-01-01',7 - MOD(WEEKDAY(YEAR(" + dateString + ") || '-01-01')+1,7))," + dateString + ")) as decimal) / 7.0) +1 as int)";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("weekofyear")) {
         qry = "CAST(WEEK(" + dateString + ") AS INT)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = "(cast((strftime('%Y'," + dateString + ") || (replicate('0',2-length(strftime('%W'," + dateString + "))) || strftime('%W'," + dateString + "))) as integer))";
         } else if (arguments.size() >= 2) {
            qry = "(cast((floor(case when (mod((mod(cast(mod(((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7)+1,7) as integer) +6,7) +4), 7) -3) * -1 >= cast(strftime('%j'," + dateString + ") as integer) then ((strftime('%Y'," + dateString + ")-1) || substr((100+ceil(((mod((mod(cast(mod(((strftime('%w',((strftime('%Y'," + dateString + ")-1) || '-01-01'))+6)%7)+1,7) as integer)+6,7)+4),7)-3) + cast(strftime('%j',((strftime('%Y'," + dateString + ")-1) || '-12-31')) as integer) + cast(strftime('%j'," + dateString + ") as integer)) / ((case when 7=0 then null else 7 end)*1.0))),2)) else case when (cast(strftime('%j'," + dateString + ") as integer)+(mod((mod(cast(mod(((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7)+1,7) as integer)+6,7)+4),7)-3)) > 364 then case when ((cast(strftime('%j',(strftime('%Y'," + dateString + ") || '-12-31')) as integer)+(mod((mod(cast(mod(((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7)+1,7) as integer)+6,7)+4),7)-3))-364) > 3 then (strftime('%Y'," + dateString + ") || '53') else ((strftime('%Y'," + dateString + ")+1) || '01') end else (strftime('%Y'," + dateString + ") || substr((100+ceil((cast(strftime('%j'," + dateString + ") as integer) + (mod((mod(cast(mod(((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7)+1,7) as integer) +6, 7)+4),7)-3)) / ((case when 7=0 then null else 7 end)*1.0))),2)) end end)) as integer))";
         } else {
            qry = "(cast(strftime('%Y'," + dateString + ") || (case when (floor(cast((cast(-(julianday(datetime(strftime('%Y'," + dateString + ") || '-01-01','+' || (7-mod(((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01')+6)%7)+1),7)) || ' days'))-julianday(" + dateString + ")) as integer)) as numeric) / 7.0)+1) < 10 then '0' || (floor(cast((cast(-(julianday(datetime(strftime('%Y'," + dateString + ") || '-01-01','+' || (7-mod(((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01')+6)%7)+1),7)) || ' days'))-julianday(" + dateString + ")) as integer)) as numeric) / 7.0)+1) else (floor(cast((cast(-(julianday(datetime(strftime('%Y'," + dateString + ") || '-01-01','+' || (7-mod(((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01')+6)%7)+1),7)) || ' days'))-julianday(" + dateString + ")) as integer)) as numeric) / 7.0)+1) end) as integer))";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = "(cast(strftime('%W'," + dateString + ") as integer))";
         } else if (arguments.size() >= 2) {
            qry = "(cast((floor((case when (mod((mod(mod(cast(((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7) as integer)+1,7)+6,7)+4),7)-3) * -1 >= cast(strftime('%j'," + dateString + ") as integer) then ceil(((mod((mod(mod(cast(((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7) as integer)+1,7)+6,7)+4),7)-3) + cast(strftime('%j',((strftime('%Y'," + dateString + ")-1) || '-12-31')) as integer)+cast(strftime('%j'," + dateString + ") as integer)) / ((case when 7=0 then null else 7 end)*1.0)) else case when (cast(strftime('%j'," + dateString + ") as integer)+(mod((mod(mod(cast(((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7) as integer)+1,7)+6,7)+4),7)-3)) > 364 then case when ((cast(strftime('%j',(strftime('%Y'," + dateString + ") || '-12-31')) as integer) + (mod((mod(mod(cast(((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7) as integer)+1,7)+6,7)+4),7)-3))-364) >3 then 53 else 1 end else ceil((strftime('%j'," + dateString + ")+(mod((mod(mod(cast(((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7) as integer)+1,7)+6,7)+4),7)-3)) / ((case when 7=0 then null else 7 end)*1.0)) end end))) as integer))";
         } else {
            qry = "(cast(floor(cast((cast(-(julianday(datetime(strftime('%Y'," + dateString + ") || '-01-01','+' || (7-mod(((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01')+6)%7)+1),7)) || ' days'))-julianday(" + dateString + ")) as integer)) as numeric) / 7.0)+1 as integer))";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("weekofyear")) {
         qry = "(cast(strftime('%W'," + dateString + ") as integer))";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0 && ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().get(0) instanceof String) {
               String dateString = ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().get(0).toString();
               dateString = "CAST(" + this.handleStringLiteralForDateTime(dateString, from_sqs) + " AS DATE)";
               ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression().set(0, dateString);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = "Week_ISO( " + arguments.get(0) + " )";
         } else {
            qry = " (CASE WHEN (Week( " + arguments.get(0).toString() + " )=1) THEN (CASE WHEN (DAYOFWEEK(CONCAT(YEAR(" + arguments.get(0).toString() + "),'-01-01'))>1) THEN  53 ELSE 1 END) ELSE (Week( " + arguments.get(0).toString() + " )-1) END) ";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("weekofyear")) {
         qry = "week( " + arguments.get(0) + " )";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
         String dateArg = arguments.get(0).toString();
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = "CONCAT(YEAR(" + dateArg + "),LPAD(Week_ISO(" + dateArg + "),2,0))";
         } else {
            qry = "CONCAT((CASE WHEN ((Week( " + dateArg + " )=1) AND (DAYOFWEEK(CONCAT(YEAR(" + dateArg + "),'-01-01'))>1)) THEN YEAR(" + dateArg + ")-1 ELSE YEAR(" + dateArg + ") END) ,LPAD((CASE WHEN (Week( " + dateArg + " )=1) THEN (CASE WHEN (DAYOFWEEK(CONCAT(YEAR(" + dateArg + "),'-01-01'))>1) THEN  53 ELSE 1 END) ELSE (Week( " + dateArg + " )-1) END),2,0))";
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = " (YEAR(DATE((DATE(" + dateString + ") - (CASE WHEN WEEKDAY(" + dateString + ")=0 THEN 6 ELSE (WEEKDAY(" + dateString + ")-1) END)) + 3 UNITS DAY)) || LPAD(((CAST(DATE((DATE(" + dateString + ") - (CASE WHEN WEEKDAY(" + dateString + ")=0 THEN 6 ELSE (WEEKDAY(" + dateString + ")-1) END)) + 3 UNITS DAY)- MDY(1,1,YEAR(((DATE(" + dateString + ") - (CASE WHEN WEEKDAY(" + dateString + ")=0 THEN 6 ELSE (WEEKDAY(" + dateString + ")-1) END)) + 3 UNITS DAY))) +1 AS INT) + 6) / 7)::INT,2,'0'))::INT ";
         } else {
            qry = " (FLOOR(CASE WHEN (MOD(( mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) +6, 7) +4), 7) -3) * - 1 >= CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT) THEN ((YEAR(" + dateString + ") - 1 ) || SUBSTR(CAST((100+CEIL(((MOD(( mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + ")-1)) +6, 7) + 4),7) -3) + CAST(DATE(MDY(12,31,YEAR(" + dateString + ")-1))-MDY(1,1,YEAR(" + dateString + ")-1)+1 AS INT) + CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT)) / 7.0)) AS LVARCHAR),2)) ELSE CASE WHEN ( CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT) + (MOD(( mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) +6, 7) +4), 7) -3)) > 364 THEN CASE WHEN ((CAST(DATE(MDY(12,31,YEAR(" + dateString + ")))-MDY(1,1,YEAR(" + dateString + "))+1 AS INT)+(MOD(( mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) +6, 7) +4), 7) -3)) -364) > 3 THEN (YEAR(" + dateString + ") || '53') ELSE ((YEAR(" + dateString + ")+1) || '01') END ELSE (YEAR(" + dateString + ") || SUBSTR(CAST((100 + CEIL((CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT) + (MOD(( mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) +6, 7) +4), 7) -3)) / 7.0)) AS LVARCHAR),2)) END END))::INT ";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
         if (arguments.size() < 2) {
            qry = " (FLOOR((CASE WHEN (MOD((mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) +6,7) +4),7) -3) * -1 >= CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT) THEN CEIL(((MOD((mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) +6,7) +4),7) -3) + CAST(MDY(12,31,YEAR(" + dateString + ") - 1) - MDY(1, 1, YEAR(" + dateString + ")-1) + 1 AS INT) + CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT)) / 7.0) - 53 ELSE CASE WHEN ( CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT) + (MOD((mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) +6,7) +4),7) -3)) > 364 THEN CASE WHEN ((CAST(MDY(12,31,YEAR(" + dateString + ")) - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT) + (MOD((mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) +6,7) +4),7) -3)) -364) >3 THEN 53 ELSE 1 END ELSE CEIL(( CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT) + (MOD((mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) +6,7) +4),7) -3)) / 7.0) END END)))::INT ";
         } else if (arguments.get(1).toString().equals("6")) {
            qry = " (FLOOR((CASE WHEN (MOD((mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) +6,7) +4),7) -3) * -1 >= CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT) THEN CEIL(((MOD((mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) +6,7) +4),7) -3) + CAST(MDY(12,31,YEAR(" + dateString + ") - 1) - MDY(1, 1, YEAR(" + dateString + ")-1) + 1 AS INT) + CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT)) / 7.0) ELSE CASE WHEN ( CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT) + (MOD((mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) +6,7) +4),7) -3)) > 364 THEN CASE WHEN ((CAST(MDY(12,31,YEAR(" + dateString + ")) - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT) + (MOD((mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) +6,7) +4),7) -3)) -364) >3 THEN 53 ELSE 1 END ELSE CEIL(( CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT) + (MOD((mod(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) +6,7) +4),7) -3)) / 7.0) END END)))::INT ";
         } else {
            qry = " ((CAST(DATE((DATE(" + dateString + ") - (CASE WHEN WEEKDAY(" + dateString + ")=0 THEN 6 ELSE (WEEKDAY(" + dateString + ")-1) END)) + 3 UNITS DAY)- MDY(1,1,YEAR(((DATE(" + dateString + ") - (CASE WHEN WEEKDAY(" + dateString + ")=0 THEN 6 ELSE (WEEKDAY(" + dateString + ")-1) END)) + 3 UNITS DAY))) +1 AS INT) + 6) / 7)::INT ";
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = arguments.get(0).toString();
      if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = "CLng(Iif((Month(" + dateString + ") = 12 AND Day(" + dateString + ") >= 29) OR (Month(" + dateString + ") = 1 AND Day(" + dateString + ") <= 2),DatePart('yyyy', DateAdd('yyyy', 1, " + dateString + ")),DatePart('yyyy', " + dateString + ")) &  Iif((DatePart('ww'," + dateString + ", 2, 2) > 52) And (DatePart('ww'," + dateString + "+7, 2, 2) = 2), '01', Iif((DatePart('ww'," + dateString + ", 2, 2)) < 10 , 0 & (DatePart('ww'," + dateString + ", 2, 2)), (DatePart('ww'," + dateString + ", 2, 2)))))";
         } else {
            qry = "CLng(Iif(((((((WEEKDAY(YEAR(" + dateString + ") & '-01-01') + 6) MOD 7)+4) MOD 7)-3) * -1 >= DATEPART('y'," + dateString + ")), ((YEAR(" + dateString + ") - 1 ) & (Mid(CStr((100+(fix(((((((WEEKDAY((YEAR(" + dateString + ")-1) & '-01-01')+6) MOD 7) +4) MOD 7)-3) + DATEPART('y',(YEAR(" + dateString + ")-1) & '-12-31') + DATEPART('y'," + dateString + ")) / 7.0)+1))),2,LEN((100+(fix(((((((WEEKDAY((YEAR(" + dateString + ")-1) & '-01-01')+6) MOD 7) +4) MOD 7)-3) + DATEPART('y',(YEAR(" + dateString + ")-1) & '-12-31') + DATEPART('y'," + dateString + ")) / 7.0)+1)))-1))),Iif((DATEPART('y'," + dateString + ") + (((((WEEKDAY(YEAR(" + dateString + ") & '-01-01') + 6) MOD 7)+4) MOD 7)-3)) > 364, Iif( ((DATEPART('y',YEAR(" + dateString + ") & '-12-31') + (((((WEEKDAY(YEAR(" + dateString + ") & '-01-01') + 6) MOD 7)+4) MOD 7)-3)) - 364) > 3,YEAR(" + dateString + ") & '53',((YEAR(" + dateString + ")+1) & '01')), (YEAR(" + dateString + ") & Mid(CStr((100+(fix((DATEPART('y'," + dateString + ")+(((((WEEKDAY((YEAR(" + dateString + ")) & '-01-01')+6) MOD 7) +4) MOD 7)-3)) / 7.0)+1))),2, LEN(100+(fix((DATEPART('y'," + dateString + ")+(((((WEEKDAY((YEAR(" + dateString + ")) & '-01-01')+6) MOD 7) +4) MOD 7)-3)) / 7.0)+1))-1)))))";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
         if (arguments.size() < 2) {
            qry = "CLng(Iif(((((((WEEKDAY(YEAR(" + dateString + ") & '-01-01') + 6) MOD 7)+4) MOD 7)-3) * -1 >= DATEPART('y'," + dateString + ")), (fix(((((((WEEKDAY((YEAR(" + dateString + ")-1) & '-01-01')+6) MOD 7) +4) MOD 7)-3) + DATEPART('y',(YEAR(" + dateString + ")-1) & '-12-31') + DATEPART('y'," + dateString + ")) / 7.0) - 52),Iif((DATEPART('y'," + dateString + ") + (((((WEEKDAY(YEAR(" + dateString + ") & '-01-01') + 6) MOD 7)+4) MOD 7)-3)) > 364, Iif( ((DATEPART('y',YEAR(" + dateString + ") & '-12-31') + (((((WEEKDAY(YEAR(" + dateString + ") & '-01-01') + 6) MOD 7)+4) MOD 7)-3)) - 364) > 3,53,1), Iif((fix((DATEPART('y'," + dateString + ")+(((((WEEKDAY((YEAR(" + dateString + ")) & '-01-01')+6) MOD 7) +4) MOD 7)-3)) / 7) = 0),1,fix((DATEPART('y'," + dateString + ")+(((((WEEKDAY((YEAR(" + dateString + ")) & '-01-01')+6) MOD 7) +4) MOD 7)-3)) / 7)))))";
         } else if (arguments.get(1).toString().equals("6")) {
            qry = "CLng(Iif(((((((WEEKDAY(YEAR(" + dateString + ") & '-01-01') + 6) MOD 7)+4) MOD 7)-3) * -1 >= DATEPART('y'," + dateString + ")), (fix(((((((WEEKDAY((YEAR(" + dateString + ")) & '-01-01')+6) MOD 7) +4) MOD 7)-3) + DATEPART('y',(YEAR(" + dateString + ")-1) & '-12-31') + DATEPART('y'," + dateString + ")) / 7.0)),Iif((DATEPART('y'," + dateString + ") + (((((WEEKDAY(YEAR(" + dateString + ") & '-01-01') + 6) MOD 7)+4) MOD 7)-3)) > 364, Iif( ((DATEPART('y',YEAR(" + dateString + ") & '-12-31') + (((((WEEKDAY(YEAR(" + dateString + ") & '-01-01') + 6) MOD 7)+4) MOD 7)-3)) - 364) > 3,53,1),Iif((fix((DATEPART('y'," + dateString + ")+(((((WEEKDAY((YEAR(" + dateString + ")) & '-01-01')+6) MOD 7) +4) MOD 7)-3)) / 7.0) = 0), 1, fix((DATEPART('y'," + dateString + ")+(((((WEEKDAY((YEAR(" + dateString + ")) & '-01-01')+6) MOD 7) +4) MOD 7)-3)) / 7.0)))))";
         } else {
            qry = "Iif((DatePart('ww'," + dateString + ", 2, 2) > 52) And (DatePart('ww'," + dateString + "+7, 2, 2) = 2), 1, DatePart('ww'," + dateString + ", 2, 2))";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("convert_tz")) {
         int finalMins = 0;

         try {
            if (arguments.size() == 3 && arguments.get(1) != null && arguments.get(2) != null && arguments.get(1).toString().contains(":") && arguments.get(2).toString().contains(":")) {
               String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
               String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
               int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
               int currentTZMin = Integer.parseInt(currentTZ[1]);
               int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
               int toTZMin = Integer.parseInt(toTZ[1]);
               int toTZOverallMins = toTZHour * 60 + toTZMin;
               int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
               if (currentTZ[0].contains("-")) {
                  currentTZHour *= -1;
               }

               if (toTZ[0].contains("-")) {
                  toTZHour *= -1;
               }

               if (currentTZHour < 0) {
                  if (toTZHour < 0) {
                     finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                  } else {
                     finalMins = toTZOverallMins - currentTZOverallMins * -1;
                  }
               } else if (toTZHour < 0) {
                  finalMins = toTZOverallMins * -1 - currentTZOverallMins;
               } else {
                  finalMins = toTZOverallMins - currentTZOverallMins;
               }
            }
         } catch (Exception var15) {
         }

         qry = "DATEADD('n'," + finalMins + ",CDate(" + arguments.get(0).toString() + "))";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      String qry = "";
      String dateString = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
         if (arguments.size() >= 2 && !arguments.get(1).toString().equals("6")) {
            qry = " CAST(TO_CHAR(CAST(" + arguments.get(0) + " AS DATE),'IYYYIW') AS INT) ";
         } else {
            qry = " CAST(CASE WHEN (MOD(( MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7) +4), 7) -3) * - 1 >= DAYOFYEAR(" + dateString + ") THEN ((YEAR(" + dateString + ") - 1 ) || SUBSTR(TO_CHAR((100+CEIL(((MOD(( MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ")-1 || '-01-01' AS DATE)) +5,7) + 4),7) -3) + DAYOFYEAR(CAST(YEAR(" + dateString + ")-1 || '-12-31' AS DATE)) + DAYOFYEAR(" + dateString + ")) / 7.0))),2)) ELSE CASE WHEN ( DAYOFYEAR(" + dateString + ") + (MOD(( MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7) +4), 7) -3)) > 364 THEN CASE WHEN ((DAYOFYEAR(CAST(YEAR(" + dateString + ") || '-12-31' AS DATE))+(MOD(( MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7) +4), 7) -3)) -364) > 3 THEN (YEAR(" + dateString + ") || '53') ELSE ((YEAR(" + dateString + ")+1) || '01') END ELSE (YEAR(" + dateString + ") || SUBSTR(TO_CHAR((100 + CEIL(CAST((DAYOFYEAR(" + dateString + ") + (MOD(( MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7) +4), 7) -3)) AS DECIMAL)/ 7.0))),2)) END END AS INT) ";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
         if (arguments.size() < 2) {
            qry = " CAST(FLOOR((CASE WHEN (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7) +4),7) -3) * -1 >= DAYOFYEAR(" + dateString + ") THEN CEIL(((MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7) +4),7) -3) + DAYOFYEAR(CAST(YEAR(" + dateString + ")-1 || '-12-31' AS DATE)) + DAYOFYEAR(" + dateString + ")) / 7.0) - 53 ELSE CASE WHEN ( DAYOFYEAR(" + dateString + ") + (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7) +4),7) -3)) > 364 THEN CASE WHEN ((DAYOFYEAR(CAST(YEAR(" + dateString + ") || '-12-31' AS DATE)) + (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7) +4),7) -3)) -364) >3 THEN 53 ELSE 1 END ELSE CEIL(CAST(( DAYOFYEAR(" + dateString + ") + (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7) +4),7) -3)) AS DECIMAL) / 7.0) END END)) AS INT) ";
         } else if (arguments.get(1).toString().equals("6")) {
            qry = " CAST(FLOOR((CASE WHEN (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7) +4),7) -3) * -1 >= DAYOFYEAR(" + dateString + ") THEN CEIL(((MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7) +4),7) -3) + DAYOFYEAR(CAST(YEAR(" + dateString + ")-1 || '-12-31' AS DATE)) + DAYOFYEAR(" + dateString + ")) / 7.0) ELSE CASE WHEN ( DAYOFYEAR(" + dateString + ") + (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7) +4),7) -3)) > 364 THEN CASE WHEN ((DAYOFYEAR(CAST(YEAR(" + dateString + ") || '-12-31' AS DATE)) + (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7) +4),7) -3)) -364) >3 THEN 53 ELSE 1 END ELSE CEIL(CAST(( DAYOFYEAR(" + dateString + ") + (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7) +4),7) -3)) AS DECIMAL) / 7.0) END END)) AS INT) ";
         } else {
            qry = " CAST(TO_CHAR(CAST(" + arguments.get(0) + " AS DATE),'IW') AS INT) ";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("convert_tz")) {
         int finalMins = 0;
         boolean firstArg = arguments.get(1) != null && ((SelectColumn)arguments.get(1)).getColumnExpression().size() == 1 && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0) != null && ((SelectColumn)arguments.get(1)).getColumnExpression().get(0) instanceof String;
         boolean secondArg = arguments.get(2) != null && ((SelectColumn)arguments.get(2)).getColumnExpression().size() == 1 && ((SelectColumn)arguments.get(2)).getColumnExpression().get(0) != null && ((SelectColumn)arguments.get(2)).getColumnExpression().get(0) instanceof String;
         boolean isStrigInstance = arguments.size() == 3 && firstArg && secondArg;

         try {
            if (isStrigInstance && arguments.get(1).toString().contains(":") && arguments.get(2).toString().contains(":")) {
               String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
               String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
               int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
               int currentTZMin = Integer.parseInt(currentTZ[1]);
               int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
               int toTZMin = Integer.parseInt(toTZ[1]);
               int toTZOverallMins = toTZHour * 60 + toTZMin;
               int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
               if (currentTZ[0].contains("-")) {
                  currentTZHour *= -1;
               }

               if (toTZ[0].contains("-")) {
                  toTZHour *= -1;
               }

               if (currentTZHour < 0) {
                  if (toTZHour < 0) {
                     finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                  } else {
                     finalMins = toTZOverallMins - currentTZOverallMins * -1;
                  }
               } else if (toTZHour < 0) {
                  finalMins = toTZOverallMins * -1 - currentTZOverallMins;
               } else {
                  finalMins = toTZOverallMins - currentTZOverallMins;
               }
            }
         } catch (Exception var18) {
            throw new ConvertException("\nThe function argument is not supported");
         }

         qry = "DATE_ADD(" + arguments.get(0).toString() + ",INTERVAL " + finalMins + " MINUTE)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }
}
