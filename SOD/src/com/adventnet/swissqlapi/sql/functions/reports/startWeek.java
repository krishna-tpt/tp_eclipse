package com.adventnet.swissqlapi.sql.functions.reports;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class startWeek extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer[] argu = new StringBuffer[3];

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         argu[i_count] = new StringBuffer();
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            argu[i_count].append(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            argu[i_count].append(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
         qry = "cast(  CASE    WHEN (MOD((mod(int(dayofweek(CONCAT(YEAR(" + argu[0] + "), '-01-01'))+5),7) + " + argu[1] + "), 7) -3) * - 1  >= DAYOFYEAR(" + argu[0] + ") THEN CONCAT(YEAR(" + argu[0] + ") -1, SUBSTRING((100 + CEILING(((MOD((mod(int(dayofweek(CONCAT(YEAR(" + argu[0] + ") -1, '-01-01'))+5),7) + " + argu[1] + "), 7) -3) + DAYOFYEAR(CONCAT(YEAR(" + argu[0] + ") -1, '-12-31')) + DAYOFYEAR(" + argu[0] + ")) / (if(7=0,null,7)*1.0))), 2))    ELSE     CASE      WHEN (DAYOFYEAR(" + argu[0] + ") + (MOD((mod(int(dayofweek(CONCAT(YEAR(" + argu[0] + "), '-01-01'))+5),7) + " + argu[1] + "), 7) -3))  > 364 THEN       CASE        WHEN ((DAYOFYEAR(CONCAT(YEAR(" + argu[0] + "), '-12-31')) + (MOD((mod(int(dayofweek(CONCAT(YEAR(" + argu[0] + "), '-01-01'))+5),7) + " + argu[1] + "), 7) -3)) -364)  > 3 THEN CONCAT(YEAR(" + argu[0] + "), '53')        ELSE CONCAT(YEAR(" + argu[0] + ") + 1, '01')       END      ELSE CONCAT(YEAR(" + argu[0] + "), SUBSTRING((100 + CEILING((DAYOFYEAR(" + argu[0] + ") + (MOD((mod(int(dayofweek(CONCAT(YEAR(" + argu[0] + "), '-01-01'))+5),7) + " + argu[1] + "), 7) -3)) / (if(7=0,null,7)*1.0))), 2)) END   END as INTEGER)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
         qry = "CAST(if((((mod(dayofweek(date(concat(year(date(" + argu[0] + ")),'-01-01')))+5+" + argu[1] + ",7)-3)*(-1)) >= doy(date(" + argu[0] + "))),(ceil(((mod(dayofweek(date(concat(year(date(" + argu[0] + ")),'-01-01')))+5+" + argu[1] + ",7)-3)+doy(date(concat((year(date(" + argu[0] + "))-1),'-12-31')))+doy(date(" + argu[0] + ")))/7.0)),(if(((doy(date(" + argu[0] + "))+(mod(dayofweek(date(concat(year(date(" + argu[0] + ")),'-01-01')))+5+" + argu[1] + ",7)-3))>364),(if(((doy(date(concat((year(date(" + argu[0] + "))-1),'-12-31')))+(mod(dayofweek(date(concat(year(date(" + argu[0] + ")),'-01-01')))+5+" + argu[1] + ",7)-3))-364) > 3,53,1)),(ceil((doy(date(" + argu[0] + "))+(mod(dayofweek(date(concat(year(date(" + argu[0] + ")),'-01-01')))+5+" + argu[1] + ",7)-3))/7.0))))) AS INTEGER)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearIntrvalNwkStrtDay")) {
         qry = "CAST(if((((mod(dayofweek(date(concat(year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000))),'-01-01')))+5+" + argu[1] + ",7)-3)*(-1)) >= doy(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000)))),concat(year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000)))-1,substring((100+ceil(((mod((dayofweek(date(concat(year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000)))-1,'-01-01')))+5+" + argu[1] + "),7)-3)+doy(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000))))/7.0)),2)),if((doy(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000)))+(mod((dayofweek(date(concat(year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000)))-1,'-01-01')))+5+" + argu[1] + "),7)-3))>364,if(((doy(date(concat((year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000)))-1),'-12-31')))+(mod((dayofweek(date(concat(year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000)))-1,'-01-01')))+" + argu[1] + "),7)-3))-364)>3,concat(year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000))),'53') , concat(year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000))+1),'01')),concat(year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000)))-1,substring((100+ceil(((mod((dayofweek(date(concat(year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000)))-1,'-01-01')))+5+" + argu[1] + "),7)-3)+doy(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000))))/7.0)),2)))) AS INTEGER)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekIntrvalNwkStrtDay")) {
         qry = "CAST(if((((mod(dayofweek(date(concat(year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000))),'-01-01')))+5+" + argu[1] + ",7)-3)*(-1)) >= doy(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000)))),(ceil(((mod(dayofweek(date(concat(year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000))),'-01-01')))+5+" + argu[1] + ",7)-3)+doy(date(concat((year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000)))-1),'-12-31')))+doy(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000))))/7.0)),(if(((doy(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000)))+(mod(dayofweek(date(concat(year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000))),'-01-01')))+5+" + argu[1] + ",7)-3))>364),(if(((doy(date(concat((year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000)))-1),'-12-31')))+(mod(dayofweek(date(concat(year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000))),'-01-01')))+5+" + argu[1] + ",7)-3))-364) > 3,53,1)),(ceil((doy(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000)))+(mod(dayofweek(date(concat(year(date(from_unixtime(0)+interval '1' second * (" + argu[0] + "/1000))),'-01-01')))+5+" + argu[1] + ",7)-3))/7.0))))) AS INTEGER)";
      } else {
         qry = "";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0 && !isPostgreLiveDbs) {
               this.handleStringLiteralForDate(from_sqs, i_count, true);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (isPostgreLiveDbs) {
         String dataType = from_sqs != null && from_sqs.isVerticaDb() ? "VARCHAR" : "TEXT";
         String qry = "";
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
            qry = "cast(  CASE    WHEN (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) * - 1  >=  cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) THEN (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || SUBSTRING((100 + CEIL(((MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) +  cast(date_part('doy' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || '-12-31')::DATE) as int) +  cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int)) / ((case when 7=0 then null else 7 end)*1.0)))::" + dataType + ",2))    ELSE     CASE      WHEN ( cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3))  > 364 THEN       CASE        WHEN (( cast(date_part('doy' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-12-31')::DATE) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) -364)  > 3 THEN (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '53')        ELSE (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  + 1 || '01')       END      ELSE (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || SUBSTRING((100 + CEIL(( cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) / ((case when 7=0 then null else 7 end)*1.0)))::" + dataType + ",2))     END   END as INTEGER)";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
            qry = "CAST((CASE      WHEN (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) * - 1  >=  cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) THEN CEIL(((MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) +  cast(date_part('doy' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || '-12-31')::DATE) as int) +  cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int)) / ((case when 7=0 then null else 7 end)*1.0))      ELSE       CASE        WHEN ( cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3))  > 364 THEN         CASE          WHEN (( cast(date_part('doy' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-12-31')::DATE) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) -364)  > 3 THEN 53          ELSE 1         END        ELSE CEIL(( cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) / ((case when 7=0 then null else 7 end)*1.0))       END     END) AS INTEGER)";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearIntrvalNwkStrtDay")) {
            qry = "cast(  CASE    WHEN (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) * - 1  >=  cast(date_part('doy' ,(TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int) THEN (cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  -1 || SUBSTRING((100 + CEIL(((MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  -1 || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) +  cast(date_part('doy' ,(cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  -1 || '-12-31')::DATE) as int) +  cast(date_part('doy' ,(TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int)) / ((case when 7=0 then null else 7 end)*1.0))),2))    ELSE     CASE      WHEN ( cast(date_part('doy' ,(TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3))  > 364 THEN       CASE        WHEN (( cast(date_part('doy' ,(cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  || '-12-31')::DATE) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) -364)  > 3 THEN (cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  || '53')        ELSE (cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  + 1 || '01')       END      ELSE (cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  || SUBSTRING((100 + CEIL(( cast(date_part('doy' ,(TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) / ((case when 7=0 then null else 7 end)*1.0))),2))     END   END as INTEGER)";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekIntrvalNwkStrtDay")) {
            qry = "CAST((CASE      WHEN (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) * - 1  >=  cast(date_part('doy' ,(TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int) THEN CEIL(((MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  -1 || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) +  cast(date_part('doy' ,(cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  -1 || '-12-31')::DATE) as int) +  cast(date_part('doy' ,(TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int)) / ((case when 7=0 then null else 7 end)*1.0))      ELSE       CASE        WHEN ( cast(date_part('doy' ,(TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3))  > 364 THEN         CASE          WHEN (( cast(date_part('doy' ,(cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  || '-12-31')::DATE) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) -364)  > 3 THEN 53          ELSE 1         END        ELSE CEIL(( cast(date_part('doy' ,(TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  (TO_TIMESTAMP(0) + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int)  || '-01-01')::DATE) as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) / ((case when 7=0 then null else 7 end)*1.0))       END     END) AS INTEGER) ";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

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
      String dateArg = arguments.get(0).toString();
      String wkStartDay = arguments.get(1).toString();
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
         qry = "CASE WHEN " + dateArg + " IS NOT NULL THEN CAST(CASE  WHEN (((((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + " + wkStartDay + ")) % ( 7 )) -3) * - 1  >= DATEPART(dy, " + dateArg + ") THEN CONCAT(YEAR(" + dateArg + ") -1, SUBSTRING(Cast((100 + CEILING((((( ((datepart(dw,concat(YEAR(" + dateArg + ") -1, '-01-01'))+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3) + DATEPART(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')) + DATEPART(dy, " + dateArg + ")) / CONVERT(FLOAT, 7)))AS VARCHAR), 2, LEN((100 + CEILING((((( ((datepart(dw,concat(YEAR(" + dateArg + ") -1, '-01-01'))+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3) + DATEPART(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')) + DATEPART(dy, " + dateArg + ")) / CONVERT(FLOAT, 7)))) -1))      ELSE       CASE        WHEN ((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3))  > 364) THEN CASE  WHEN (((DATEPART(dy, concat(YEAR(" + dateArg + "), '-12-31')) + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3)) -364)  > 3) THEN CONCAT(YEAR(" + dateArg + "), '53')  ELSE CONCAT(YEAR(" + dateArg + ") + 1, '01') END    ELSE CONCAT(YEAR(" + dateArg + "), SUBSTRING(CAST((100 + CEILING((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3)) / CONVERT(FLOAT, 7)))AS VARCHAR), 2, LEN((100 + CEILING((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3)) / CONVERT(FLOAT, 7)))) -1))       END     END as BIGINT) ELSE NULL END";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
         qry = "CASE WHEN " + dateArg + " IS NOT NULL THEN CASE  WHEN (((((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + " + wkStartDay + ")) % ( 7 )) -3) * - 1  >= DATEPART(dy, " + dateArg + ") THEN CEILING((((( ((datepart(dw,concat(YEAR(" + dateArg + ") -1, '-01-01'))+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3) + DATEPART(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')) + DATEPART(dy, " + dateArg + ")) / CONVERT(FLOAT, 7))      ELSE       CASE        WHEN ((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3))  > 364) THEN         CASE          WHEN (((DATEPART(dy, concat(YEAR(" + dateArg + "), '-12-31')) + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3)) -364)  > 3) THEN 53          ELSE 1         END        ELSE CEILING((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3)) / CONVERT(FLOAT, 7)) END END ELSE NULL END";
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
      String dateArg = arguments.get(0).toString();
      String wkStartDay = arguments.get(1).toString();
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
         qry = "CAST(CASE  WHEN (((((date_part(dw,concat(YEAR(" + dateArg + "), '-01-01')::DATE)+6)%7 + " + wkStartDay + ")) % ( 7 )) -3) * - 1  >= date_part(dy, " + dateArg + ") THEN CONCAT(YEAR(" + dateArg + ") -1, SUBSTRING(Cast((100 + CEIL((((( ((date_part(dw,concat(YEAR(" + dateArg + ") -1, '-01-01')::DATE)+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3) + date_part(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')::DATE) + date_part(dy, " + dateArg + ")) / 7))AS VARCHAR), 2, LEN((100 + CEIL((((( ((date_part(dw,concat(YEAR(" + dateArg + ") -1, '-01-01')::DATE)+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3) + date_part(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')::DATE) + date_part(dy, " + dateArg + ")) / 7))) -1))      ELSE       CASE        WHEN ((date_part(dy, " + dateArg + ") + ((( ((date_part(dw,concat(YEAR(" + dateArg + "), '-01-01')::DATE)+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3))  > 364) THEN CASE  WHEN (((date_part(dy, concat(YEAR(" + dateArg + "), '-12-31')::DATE) + ((( ((date_part(dw,concat(YEAR(" + dateArg + "), '-01-01')::DATE)+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3)) -364)  > 3) THEN CONCAT(YEAR(" + dateArg + "), '53')  ELSE CONCAT(YEAR(" + dateArg + ") + 1, '01') END    ELSE CONCAT(YEAR(" + dateArg + "), SUBSTRING(CAST((100 + CEIL((date_part(dy, " + dateArg + ") + ((( ((date_part(dw,concat(YEAR(" + dateArg + "), '-01-01')::DATE)+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3)) / 7))AS VARCHAR), 2, LEN((100 + CEIL((date_part(dy, " + dateArg + ") + ((( ((date_part(dw,concat(YEAR(" + dateArg + "), '-01-01')::DATE)+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3)) / 7))) -1))       END     END as BIGINT)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
         qry = "CASE  WHEN (((((date_part(dw,concat(YEAR(" + dateArg + "), '-01-01')::DATE)+6)%7 + " + wkStartDay + ")) % ( 7 )) -3) * - 1  >= date_part(dy, " + dateArg + ") THEN CEIL((((( ((date_part(dw,concat(YEAR(" + dateArg + ") -1, '-01-01')::DATE)+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3) + date_part(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')::DATE) + date_part(dy, " + dateArg + ")) / 7)      ELSE       CASE        WHEN ((date_part(dy, " + dateArg + ") + ((( ((date_part(dw,concat(YEAR(" + dateArg + "), '-01-01')::DATE)+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3))  > 364) THEN         CASE          WHEN (((date_part(dy, concat(YEAR(" + dateArg + "), '-12-31')::DATE) + ((( ((date_part(dw,concat(YEAR(" + dateArg + "), '-01-01')::DATE)+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3)) -364)  > 3) THEN 53          ELSE 1         END        ELSE CEIL((date_part(dy, " + dateArg + ") + ((( ((date_part(dw,concat(YEAR(" + dateArg + "), '-01-01')::DATE)+6)%7 + " + wkStartDay + ") ) % ( 7 )) -3)) / 7) END END";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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

      this.setFunctionArguments(arguments);
      String qry;
      if (from_sqs != null && from_sqs.isHyperSql()) {
         qry = "";
         String dateString = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
            qry = " CAST(CASE WHEN ( (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3)*-1 >= DAYOFYEAR(" + dateString + ") ) THEN   ((YEAR(" + dateString + ")-1) || SUBSTRING(TO_CHAR(CAST((100+CEIL( CAST(((MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ")-1 || '-01-01' AS DATE)) +5,7)  +" + arguments.get(1).toString() + "),7)-3)+DAYOFYEAR(CAST(YEAR(" + dateString + ")-1 || '-12-31' AS DATE))+DAYOFYEAR(" + dateString + "))  AS DECIMAL) /7.0)) AS BIGINT)),2))ELSE CASE WHEN   ( (DAYOFYEAR(" + dateString + ")+(MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3))>364 ) THEN CASE WHEN   ( ((DAYOFYEAR(CAST(YEAR(" + dateString + ") || '-12-31' AS DATE))+(MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)  +" + arguments.get(1).toString() + "),7)-3))-364)>3 ) THEN (YEAR(" + dateString + ") || '53') ELSE (YEAR(" + dateString + ")+1 || '01') END ELSE   (YEAR(" + dateString + ") || SUBSTRING(TO_CHAR(CAST((100+CEIL(CAST((DAYOFYEAR(" + dateString + ")+  (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3)) AS DECIMAL) /7.0)) AS BIGINT)),2)) END END AS BIGINT) ";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
            qry = " CASE WHEN ( (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3)*-1 >= DAYOFYEAR(" + dateString + ") ) THEN  CAST(CEIL(CAST(((MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ")-1 || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3)+ DAYOFYEAR(CAST(YEAR(" + dateString + ")-1 || '-12-31' AS DATE))+DAYOFYEAR(" + dateString + ")) AS DECIMAL) /7.0) AS BIGINT) ELSE CASE WHEN  ( (DAYOFYEAR(" + dateString + ")+(MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3))>364 )  THEN CASE WHEN ( ((DAYOFYEAR(CAST(YEAR(" + dateString + ") || '-12-31' AS DATE))+ (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3))-364)>3 ) THEN 53 ELSE 1 END  ELSE CAST(CEIL(CAST((DAYOFYEAR(" + dateString + ")+(MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3)) AS DECIMAL)/7.0) AS BIGINT) END END ";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (from_sqs != null && from_sqs.isMySqlLive()) {
         qry = "";
         if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
            qry = "CASE WHEN ( (MOD((WEEKDAY(concat(YEAR(" + arguments.get(0).toString() + "),'-01-01'))+" + arguments.get(1).toString() + "),7)-3)*-1 >= DAYOFYEAR(" + arguments.get(0).toString() + ") ) THEN CEIL( ((MOD((WEEKDAY(concat(YEAR(" + arguments.get(0).toString() + ")-1,'-01-01'))+" + arguments.get(1).toString() + "),7)-3)+DAYOFYEAR(concat(YEAR(" + arguments.get(0).toString() + ")-1,'-12-31'))+DAYOFYEAR(" + arguments.get(0).toString() + ")) /7) ELSE CASE WHEN ( (DAYOFYEAR(" + arguments.get(0).toString() + ")+(MOD((WEEKDAY(concat(YEAR(" + arguments.get(0).toString() + "),'-01-01'))+" + arguments.get(1).toString() + "),7)-3))>364 ) THEN CASE WHEN ( ((DAYOFYEAR(concat(YEAR(" + arguments.get(0).toString() + "),'-12-31'))+(MOD((WEEKDAY(concat(YEAR(" + arguments.get(0).toString() + "),'-01-01'))+" + arguments.get(1).toString() + "),7)-3))-364)>3 ) THEN 53 ELSE 1 END ELSE CEIL((DAYOFYEAR(" + arguments.get(0).toString() + ")+(MOD((WEEKDAY(concat(YEAR(" + arguments.get(0).toString() + "),'-01-01'))+" + arguments.get(1).toString() + "),7)-3))/7) END END";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekIntrvalNwkStrtDay")) {
            qry = "CASE WHEN ( (MOD((WEEKDAY(concat(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)),'-01-01'))+" + arguments.get(1).toString() + "),7)-3)*-1 >= DAYOFYEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)) ) THEN CEIL( ((MOD((WEEKDAY(concat(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))-1,'-01-01'))+" + arguments.get(1).toString() + "),7)-3)+DAYOFYEAR(concat(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))-1,'-12-31'))+DAYOFYEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))) /7) ELSE CASE WHEN ( (DAYOFYEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))+(MOD((WEEKDAY(concat(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)),'-01-01'))+" + arguments.get(1).toString() + "),7)-3))>364 ) THEN CASE WHEN ( ((DAYOFYEAR(concat(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)),'-12-31'))+(MOD((WEEKDAY(concat(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)),'-01-01'))+" + arguments.get(1).toString() + "),7)-3))-364)>3 ) THEN 53 ELSE 1 END ELSE CEIL((DAYOFYEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))+(MOD((WEEKDAY(concat(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)),'-01-01'))+" + arguments.get(1).toString() + "),7)-3))/7) END END";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearIntrvalNwkStrtDay")) {
            qry = "CAST(CASE WHEN ( (MOD((WEEKDAY(concat(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)),'-01-01'))+" + arguments.get(1).toString() + "),7)-3)*-1 >= DAYOFYEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)) ) THEN CONCAT(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))-1,SUBSTRING((100+CEIL( ((MOD((WEEKDAY(concat(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))-1,'-01-01'))+" + arguments.get(1).toString() + "),7)-3)+DAYOFYEAR(concat(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))-1,'-12-31'))+DAYOFYEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))) /7)),2))ELSE CASE WHEN ( (DAYOFYEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))+(MOD((WEEKDAY(concat(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)),'-01-01'))+" + arguments.get(1).toString() + "),7)-3))>364 ) THEN CASE WHEN ( ((DAYOFYEAR(concat(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)),'-12-31'))+(MOD((WEEKDAY(concat(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)),'-01-01'))+" + arguments.get(1).toString() + "),7)-3))-364)>3 ) THEN CONCAT(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)),'53') ELSE CONCAT(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))+1,'01') END ELSE CONCAT(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)),SUBSTRING((100+CEIL((DAYOFYEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second))+(MOD((WEEKDAY(concat(YEAR((FROM_UNIXTIME(0)+INTERVAL(" + arguments.get(0).toString() + " DIV 1000) second)),'-01-01'))+" + arguments.get(1).toString() + "),7)-3))/7)),2)) END END as SIGNED)";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
            qry = "CAST(CASE WHEN ( (MOD((WEEKDAY(concat(YEAR(" + arguments.get(0).toString() + "),'-01-01'))+" + arguments.get(1).toString() + "),7)-3)*-1 >= DAYOFYEAR(" + arguments.get(0).toString() + ") ) THEN CONCAT(YEAR(" + arguments.get(0).toString() + ")-1,SUBSTRING((100+CEIL( ((MOD((WEEKDAY(concat(YEAR(" + arguments.get(0).toString() + ")-1,'-01-01'))+" + arguments.get(1).toString() + "),7)-3)+DAYOFYEAR(concat(YEAR(" + arguments.get(0).toString() + ")-1,'-12-31'))+DAYOFYEAR(" + arguments.get(0).toString() + ")) /7)),2))ELSE CASE WHEN ( (DAYOFYEAR(" + arguments.get(0).toString() + ")+(MOD((WEEKDAY(concat(YEAR(" + arguments.get(0).toString() + "),'-01-01'))+" + arguments.get(1).toString() + "),7)-3))>364 ) THEN CASE WHEN ( ((DAYOFYEAR(concat(YEAR(" + arguments.get(0).toString() + "),'-12-31'))+(MOD((WEEKDAY(concat(YEAR(" + arguments.get(0).toString() + "),'-01-01'))+" + arguments.get(1).toString() + "),7)-3))-364)>3 ) THEN CONCAT(YEAR(" + arguments.get(0).toString() + "),'53') ELSE CONCAT(YEAR(" + arguments.get(0).toString() + ")+1,'01') END ELSE CONCAT(YEAR(" + arguments.get(0).toString() + "),SUBSTRING((100+CEIL((DAYOFYEAR(" + arguments.get(0).toString() + ")+(MOD((WEEKDAY(concat(YEAR(" + arguments.get(0).toString() + "),'-01-01'))+" + arguments.get(1).toString() + "),7)-3))/7)),2)) END END as SIGNED)";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

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

      this.setFunctionArguments(arguments);
      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
         qry = "cast(  CASE    WHEN (MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) * - 1  >=  cast(FORMAT_TIMESTAMP('%j' ,CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as INT64) THEN ((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  -1) || SUBSTR(CAST((100 + CEIL(((MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST(((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  -1) || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) +  cast(FORMAT_TIMESTAMP('%j' ,CAST(((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  -1) || '-12-31') AS TIMESTAMP)) as INT64) +  cast(FORMAT_TIMESTAMP('%j' ,CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as INT64)) / ((case when 7=0 then null else 7 end)*1.0))) AS STRING),2))    ELSE     CASE      WHEN ( cast(FORMAT_TIMESTAMP('%j' ,CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as INT64) + (MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + " + arguments.get(1).toString() + "), 7) -3))  > 364 THEN       CASE        WHEN (( cast(FORMAT_TIMESTAMP('%j' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-12-31') AS TIMESTAMP)) as INT64) + (MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) -364)  > 3 THEN (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '53')        ELSE ((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  + 1) || '01')       END      ELSE (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || SUBSTR(CAST((100 + CEIL(( cast(FORMAT_TIMESTAMP('%j' ,CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as INT64) + (MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) / ((case when 7=0 then null else 7 end)*1.0))) AS STRING),2))     END   END as INT64)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
         qry = "CAST((CASE      WHEN (MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) * - 1  >=  cast(FORMAT_TIMESTAMP('%j' ,CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as INT64) THEN CEIL(((MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST(((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  -1) || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) +  cast(FORMAT_TIMESTAMP('%j' ,CAST(((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  -1) || '-12-31') AS TIMESTAMP)) as INT64) +  cast(FORMAT_TIMESTAMP('%j' ,CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as INT64)) / ((case when 7=0 then null else 7 end)*1.0))      ELSE       CASE        WHEN ( cast(FORMAT_TIMESTAMP('%j' ,CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as INT64) + (MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + " + arguments.get(1).toString() + "), 7) -3))  > 364 THEN         CASE          WHEN (( cast(FORMAT_TIMESTAMP('%j' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-12-31') AS TIMESTAMP)) as INT64) + (MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) -364)  > 3 THEN 53          ELSE 1         END        ELSE CEIL(( cast(FORMAT_TIMESTAMP('%j' ,CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)) as INT64) + (MOD(( mod(cast(FORMAT_TIMESTAMP('%w' ,CAST((cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS TIMESTAMP) ) as INT64)  || '-01-01') AS TIMESTAMP)) as INT64) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) / ((case when 7=0 then null else 7 end)*1.0))       END     END) AS INT64)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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

      this.setFunctionArguments(arguments);
      String qry = "";
      String datestring = isdenodo ? arguments.get(0).toString() : StringFunctions.handleLiteralStringDateForOracle(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
         if (isdenodo) {
            qry = "CAST(CASE WHEN (MOD((MOD(CAST((1+(TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01') AS DATE))-TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01') AS DATE),'IW'))) AS INT4) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) * -1 >= CAST(GETDAYOFYEAR(" + datestring + ") AS INT4) THEN (CAST(EXTRACT(YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4)-1) || SUBSTR((CAST((100+CEIL(((MOD((MOD(CAST((1+(TRUNC(CAST(((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4)-1 ) || '-01-01') AS DATE))-TRUNC(CAST(((CAST(EXTRACT( YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4)-1) || '-01-01') AS DATE),'IW'))) AS INT4) +6, 7) +" + arguments.get(1).toString() + "),7) -3)+ CAST(GETDAYOFYEAR(CAST(((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4)-1) || '-12-31') AS DATE)) AS INT4)+CAST(GETDAYOFYEAR(" + datestring + ") AS INT4)) / ((CASE WHEN 7=0 THEN NULL ELSE 7 END)*1.0))) AS CHAR(100))),2) ELSE CASE WHEN (CAST(GETDAYOFYEAR(" + datestring + ") AS INT4)+ (MOD(( MOD(CAST ((1+(TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01') AS DATE))-TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01')  AS DATE),'IW'))) AS INT4) +6, 7) + " + arguments.get(1).toString() + "),7) -3)) >364 THEN CASE WHEN ((CAST(GETDAYOFYEAR(CAST((CAST(EXTRACT( YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-12-31') AS DATE)) AS INT4)+(MOD(( MOD(CAST((1+(TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01') AS DATE))-TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01') AS DATE),'IW'))) AS INT4) +6, 7)+" + arguments.get(1).toString() + "), 7) -3)) -364) >3 THEN (CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '53') ELSE ((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4)+1) || '01') END ELSE CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || SUBSTR((CAST((100+CEIL((CAST(GETDAYOFYEAR(" + datestring + ") AS INT4)+(MOD(( MOD(CAST((1+(TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01') AS DATE))-TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01') AS DATE),'IW'))) AS INT4) +6, 7)+" + arguments.get(1).toString() + "), 7) -3)) / (7.0))) AS CHAR(100))),2) END END AS INT4)";
         } else {
            qry = "cast(CASE    WHEN (MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) * - 1 >= cast(to_char(" + datestring + ",'FMDDD') as int) THEN (cast(extract(year from cast(" + datestring + " as date))as int))-1 ||SUBSTR((cast((100+CEIL(((MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)-1  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)-1  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)+ cast(to_char(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)-1  || '-12-31'),'YYYY-MM-DD'),'FMDDD') as int)+ cast(to_char(" + datestring + ",'FMDDD') as int))/ ((case when 7=0 then null else 7 end)*1.0))) as char(100))),2) ELSE CASE WHEN (cast(to_char(" + datestring + ",'FMDDD') as int) + (MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) >364 THEN CASE WHEN ((cast(to_char(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-12-31'),'YYYY-MM-DD'),'FMDDD') as int)+(MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) -364) >3 THEN (cast(extract (year from  CAST(" + datestring + " AS DATE)) as int) ||'53') ELSE (cast(extract (year from  CAST(" + datestring + " AS DATE)) as int) + 1 ||'01') END ELSE cast(extract (year from  CAST(" + datestring + " AS DATE)) as int) ||SUBSTR((cast((100+CEIL((cast(to_char(" + datestring + ",'FMDDD') as int)+(MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3))/ (7.0))) as char(100))),2) END END AS int)";
         }
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
         if (isdenodo) {
            qry = "CAST(CASE WHEN (MOD((MOD(CAST((1+(TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01') AS DATE))-TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01') AS DATE),'IW'))) AS INT4) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) * -1 >= CAST(GETDAYOFYEAR(" + datestring + ") AS INT4) THEN CEIL(((MOD((MOD(CAST((1+(TRUNC(CAST(((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4)-1 ) || '-01-01') AS DATE))-TRUNC(CAST(((CAST(EXTRACT( YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4)-1) || '-01-01') AS DATE),'IW'))) AS INT4) +6, 7) +" + arguments.get(1).toString() + "),7) -3)+ CAST(GETDAYOFYEAR(CAST(((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4)-1) || '-12-31') AS DATE)) AS INT4)+CAST(GETDAYOFYEAR(" + datestring + ") AS INT4)) / ((CASE WHEN 7=0 THEN NULL ELSE 7 END)*1.0)) ELSE CASE WHEN (CAST(GETDAYOFYEAR(" + datestring + ") AS INT4)+ (MOD(( MOD(CAST ((1+(TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01') AS DATE))-TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01')  AS DATE),'IW'))) AS INT4) +6, 7) + " + arguments.get(1).toString() + "),7) -3)) >364 THEN CASE WHEN ((CAST(GETDAYOFYEAR(CAST((CAST(EXTRACT( YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-12-31') AS DATE)) AS INT4)+(MOD(( MOD(CAST((1+(TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01') AS DATE))-TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01') AS DATE),'IW'))) AS INT4) +6, 7)+" + arguments.get(1).toString() + "), 7) -3)) -364) >3 THEN 53 ELSE 1 END ELSE CEIL((CAST(GETDAYOFYEAR(" + datestring + ") AS INT4)+(MOD(( MOD(CAST((1+(TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01') AS DATE))-TRUNC(CAST((CAST(EXTRACT (YEAR FROM CAST(" + datestring + " AS DATE)) AS INT4) || '-01-01') AS DATE),'IW'))) AS INT4) +6, 7)+" + arguments.get(1).toString() + "), 7) -3)) / (7.0)) END END AS INT4)";
         } else {
            qry = "cast(CASE    WHEN (MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3) * - 1 >= cast(to_char(" + datestring + ",'FMDDD') as int) THEN CEIL(((MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)-1  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)-1  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)+ cast(to_char(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)-1  || '-12-31'),'YYYY-MM-DD'),'FMDDD') as int)+ cast(to_char(" + datestring + ",'FMDDD') as int))/ ((case when 7=0 then null else 7 end)*1.0)) ELSE CASE WHEN (cast(to_char(" + datestring + ",'FMDDD') as int) +  (MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) >364  THEN CASE WHEN ((cast(to_char(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-12-31'),'YYYY-MM-DD'),'FMDDD') as int)+(MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3)) -364) >3 THEN 53 ELSE 1 END ELSE CEIL((cast(to_char(" + datestring + ",'FMDDD') as int)+(MOD(( mod(cast ((1+trunc(to_date((cast(extract (year from  CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'))-trunc(to_date((cast(extract (year from CAST(" + datestring + " AS DATE)) as int)  || '-01-01'),'YYYY-MM-DD'),'IW'))as int) +6, 7) + " + arguments.get(1).toString() + "), 7) -3))/ (7.0)) END END AS int)";
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer[] argu = new StringBuffer[3];

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         argu[i_count] = new StringBuffer();
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            argu[i_count].append(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            argu[i_count].append(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
         qry = "CAST(CASE WHEN ( (MOD((DAYOFWEEK(concat(YEAR(" + argu[0] + "),'-01-01'))+" + argu[1] + "),7)-3)*-1 >= DAYOFYEAR(" + argu[0] + ") ) THEN CONCAT(YEAR(" + argu[0] + ")-1,SUBSTRING((100+CEIL( ((MOD((DAYOFWEEK(concat(YEAR(" + argu[0] + ")-1,'-01-01'))+" + argu[1] + "),7)-3)+DAYOFYEAR(concat(YEAR(" + argu[0] + ")-1,'-12-31'))+DAYOFYEAR(" + argu[0] + ")) /7)),2))ELSE CASE WHEN ( (DAYOFYEAR(" + argu[0] + ")+(MOD((DAYOFWEEK(concat(YEAR(" + argu[0] + "),'-01-01'))+" + argu[1] + "),7)-3))>364 ) THEN CASE WHEN ( ((DAYOFYEAR(concat(YEAR(" + argu[0] + "),'-12-31'))+(MOD((DAYOFWEEK(concat(YEAR(" + argu[0] + "),'-01-01'))+" + argu[1] + "),7)-3))-364)>3 ) THEN CONCAT(YEAR(" + argu[0] + "),'53') ELSE CONCAT(YEAR(" + argu[0] + ")+1,'01') END ELSE CONCAT(YEAR(" + argu[0] + "),SUBSTRING((100+CEIL((DAYOFYEAR(" + argu[0] + ")+(MOD((DAYOFWEEK(concat(YEAR(" + argu[0] + "),'-01-01'))+" + argu[1] + "),7)-3))/7)),2)) END END as INTEGER)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
         qry = "CASE WHEN ( (MOD((DAYOFWEEK(concat(YEAR(" + argu[0] + "),'-01-01'))+" + argu[1] + "),7)-3)*-1 >= DAYOFYEAR(" + argu[0] + ") ) THEN CEIL( ((MOD((DAYOFWEEK(concat(YEAR(" + argu[0] + ")-1,'-01-01'))+" + argu[1] + "),7)-3)+DAYOFYEAR(concat(YEAR(" + argu[0] + ")-1,'-12-31'))+DAYOFYEAR(" + argu[0] + ")) /7) ELSE CASE WHEN ( (DAYOFYEAR(" + argu[0] + ")+(MOD((DAYOFWEEK(concat(YEAR(" + argu[0] + "),'-01-01'))+" + argu[1] + "),7)-3))>364 ) THEN CASE WHEN ( ((DAYOFYEAR(concat(YEAR(" + argu[0] + "),'-12-31'))+(MOD((DAYOFWEEK(concat(YEAR(" + argu[0] + "),'-01-01'))+" + argu[1] + "),7)-3))-364)>3 ) THEN 53 ELSE 1 END ELSE CEIL((DAYOFYEAR(" + argu[0] + ")+(MOD((DAYOFWEEK(concat(YEAR(" + argu[0] + "),'-01-01'))+" + argu[1] + "),7)-3))/7) END END";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer[] argu = new StringBuffer[3];

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         argu[i_count] = new StringBuffer();
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            argu[i_count].append(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            argu[i_count].append(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String dateString = StringFunctions.handleLiteralStringDateForAthena(argu[0].toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
         qry = "CAST(CASE WHEN ( (MOD(((DOW(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE)) + 6)%7+" + argu[1] + "),7)-3)*-1 >= DOY(" + dateString + ") ) THEN CONCAT(CAST(YEAR(" + dateString + ")-1 AS VARCHAR),SUBSTR(CAST(CAST((100+CEIL( ((MOD(((DOW(CAST(concat(CAST(YEAR(" + dateString + ")-1 AS VARCHAR),'-01-01') AS DATE)) + 6)%7+" + argu[1] + "),7)-3)+DOY(CAST(concat(CAST(YEAR(" + dateString + ")-1 AS VARCHAR),'-12-31') AS DATE))+DOY(" + dateString + ")) /7.0)) AS BIGINT) AS VARCHAR),2))ELSE CASE WHEN ( (DOY(" + dateString + ")+(MOD(((DOW(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE) ) + 6)%7+" + argu[1] + "),7)-3))>364 ) THEN CASE WHEN ( ((DOY(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-12-31') AS DATE))+(MOD(((DOW(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE)) + 6)%7+" + argu[1] + "),7)-3))-364)>3 ) THEN CONCAT(CAST(YEAR(" + dateString + ") AS VARCHAR),'53') ELSE CONCAT(CAST(YEAR(" + dateString + ")+1 AS VARCHAR),'01') END ELSE CONCAT(CAST(YEAR(" + dateString + ") AS VARCHAR),SUBSTR(CAST(CAST((100+CEIL((DOY(" + dateString + ")+(MOD(((DOW(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE)) + 6)%7+" + argu[1] + "),7)-3))/7.0)) AS BIGINT) AS VARCHAR),2)) END END as BIGINT)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
         qry = "CASE WHEN ( (MOD(((DOW(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE)) + 6)%7+" + argu[1] + "),7)-3)*-1 >= DOY(" + dateString + ") ) THEN CAST(CEIL( ((MOD(((DOW(CAST(concat(CAST(YEAR(" + dateString + ")-1 AS VARCHAR),'-01-01') AS DATE)) + 6)%7+" + argu[1] + "),7)-3)+DOY(CAST(concat(CAST(YEAR(" + dateString + ")-1 AS VARCHAR),'-12-31') AS DATE))+DOY(" + dateString + ")) /7.0) AS BIGINT) ELSE CASE WHEN ( (DOY(" + dateString + ")+(MOD(((DOW(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE) ) + 6)%7+" + argu[1] + "),7)-3))>364 ) THEN CASE WHEN ( ((DOY(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-12-31') AS DATE))+(MOD(((DOW(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE)) + 6)%7+" + argu[1] + "),7)-3))-364)>3 ) THEN 53 ELSE 1 END ELSE CAST(CEIL((DOY(" + dateString + ")+(MOD(((DOW(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE))+6)%7+" + argu[1] + "),7)-3))/7.0) AS BIGINT) END END";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer[] arg = new StringBuffer[3];

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         arg[i_count] = new StringBuffer();
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arg[i_count].append(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arg[i_count].append(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String dateString = StringFunctions.handleLiteralStringDateForSapHana(arg[0].toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
         qry = "CAST(CASE WHEN ( (MOD((MOD((mod(WEEKDAY(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE))+1,7) + 6),7)+" + arg[1] + "),7)-3)*-1 >= DAYOFYEAR(" + dateString + ") ) THEN CONCAT(CAST(YEAR(" + dateString + ")-1 AS VARCHAR),SUBSTRING(CAST(CAST((100+CEIL( ((MOD((MOD((mod(WEEKDAY(CAST(concat(CAST(YEAR(" + dateString + ")-1 AS VARCHAR),'-01-01') AS DATE))+1,7) + 6),7)+" + arg[1] + "),7)-3)+DAYOFYEAR(CAST(concat(CAST(YEAR(" + dateString + ")-1 AS VARCHAR),'-12-31') AS DATE))+DAYOFYEAR(" + dateString + ")) /7.0)) AS BIGINT) AS VARCHAR),2))ELSE CASE WHEN ( (DAYOFYEAR(" + dateString + ")+(MOD((MOD((mod(WEEKDAY(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE) )+1,7) + 6),7)+" + arg[1] + "),7)-3))>364 ) THEN CASE WHEN ( ((DAYOFYEAR(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-12-31') AS DATE))+(MOD((MOD((mod(WEEKDAY(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE))+1,7) + 6),7)+" + arg[1] + "),7)-3))-364)>3 ) THEN CONCAT(CAST(YEAR(" + dateString + ") AS VARCHAR),'53') ELSE CONCAT(CAST(YEAR(" + dateString + ")+1 AS VARCHAR),'01') END ELSE CONCAT(CAST(YEAR(" + dateString + ") AS VARCHAR),SUBSTRING(CAST(CAST((100+CEIL((DAYOFYEAR(" + dateString + ")+(MOD((MOD(mod(WEEKDAY(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE))+1,7) + 6,7)+" + arg[1] + "),7)-3))/7.0)) AS BIGINT) AS VARCHAR),2)) END END as BIGINT)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
         qry = "CASE WHEN ( (MOD((MOD((mod(WEEKDAY(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE))+1,7) + 6),7)+" + arg[1] + "),7)-3)*-1 >= DAYOFYEAR(" + dateString + ") ) THEN CAST(CEIL( ((MOD((MOD((mod(WEEKDAY(CAST(concat(CAST(YEAR(" + dateString + ")-1 AS VARCHAR),'-01-01') AS DATE))+1,7) + 6),7)+" + arg[1] + "),7)-3)+DAYOFYEAR(CAST(concat(CAST(YEAR(" + dateString + ")-1 AS VARCHAR),'-12-31') AS DATE))+DAYOFYEAR(" + dateString + ")) /7.0) AS BIGINT) ELSE CASE WHEN ( (DAYOFYEAR(" + dateString + ")+(MOD((MOD((mod(WEEKDAY(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE) )+1,7) + 6),7)+" + arg[1] + "),7)-3))>364 ) THEN CASE WHEN ( ((DAYOFYEAR(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-12-31') AS DATE))+(MOD((MOD((mod(WEEKDAY(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE))+1,7) + 6),7)+" + arg[1] + "),7)-3))-364)>3 ) THEN 53 ELSE 1 END ELSE CAST(CEIL((DAYOFYEAR(" + dateString + ")+(MOD((MOD((mod(WEEKDAY(CAST(concat(CAST(YEAR(" + dateString + ") AS VARCHAR),'-01-01') AS DATE))+1,7)+6),7)+" + arg[1] + "),7)-3))/7.0) AS BIGINT) END END";
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

      String qry = "";
      String dateString = StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
         qry = "cast(case when ((((((((((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7)+1)%7) +6)%7)+" + arguments.get(1).toString() + ")%7)-3)*-1 >= cast(strftime('%j'," + dateString + ") as integer)) then ((strftime('%Y'," + dateString + ")-1) || substr(cast((100+ceil(((((((((((strftime('%w',((strftime('%Y'," + dateString + ")-1) || '-01-01'))+6)%7)+1)%7)+6)%7)+" + arguments.get(1).toString() + ")%7)-3)+strftime('%j',((strftime('%Y'," + dateString + ")-1) || '-12-31'))+strftime('%j'," + dateString + ")) / 7.0)) as integer),2)) else case when ((strftime('%j'," + dateString + ")+(((((((((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7)+1)%7)+6)%7)+" + arguments.get(1).toString() + ")%7)-3))>364) then case when (((strftime('%j',(strftime('%Y'," + dateString + ") || '-12-31'))+(((((((((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7)+1)%7)+6)%7)+" + arguments.get(1).toString() + ")%7)-3))-364)>3) then (strftime('%Y'," + dateString + ") || '53') else ((strftime('%Y'," + dateString + ")+1) || '01') end else (strftime('%Y'," + dateString + ") || substr(cast((100+ceil((strftime('%j'," + dateString + ")+((((((((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7)+1%7)+6)%7)+" + arguments.get(1).toString() + ")%7)-3))/7.0)) as integer),2)) end end as integer)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
         qry = "case when (((((((((((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7)+1)%7)+6)%7)+" + arguments.get(1).toString() + ")%7)-3)*-1) >= cast(strftime('%j'," + dateString + ") as integer)) then cast(ceil(((((((((((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7)+1)%7)+6)%7)+" + arguments.get(1).toString() + ")%7)-3)+strftime('%j',((strftime('%Y'," + dateString + ")-1) || '-12-31'))+strftime('%j'," + dateString + "))/7.0) as integer) else (case when (strftime('%j'," + dateString + ")+((((((((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01')+6)%7)+1)%7)+6)%7)+" + arguments.get(0).toString() + ")%7)-3))>364 then (case when (((strftime('%j',(strftime('%Y'," + dateString + ") || '-12-31'))+(((((((((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7)+1)%7)+6)%7)+" + arguments.get(1).toString() + ")%7)-3))-364)>3) then 53 else 1 end) else cast(ceil((strftime('%j'," + dateString + ")+(((((((((strftime('%w',(strftime('%Y'," + dateString + ") || '-01-01'))+6)%7)+1)%7)+6)%7)+" + arguments.get(1).toString() + ")%7)-3))/7.0) as integer) end) end";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer[] arg = new StringBuffer[3];

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         arg[i_count] = new StringBuffer();
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arg[i_count].append(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arg[i_count].append(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String dateString = StringFunctions.handleLiteralStringDateForInformix(arg[0].toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
         qry = " CAST(CASE WHEN ( (MOD((MOD((WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) + 6),7)+" + arg[1] + "),7)-3)*-1 >= CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT) ) THEN   ((YEAR(" + dateString + ")-1) || SUBSTR(CAST((100+CEIL( ((MOD((MOD((WEEKDAY(MDY(1,1,YEAR(" + dateString + ")-1)) + 6),7)  +" + arg[1] + "),7)-3)+CAST(DATE(MDY(12,31,YEAR(" + dateString + ")-1))-MDY(1,1,YEAR(" + dateString + ")-1)+1 AS INT)+CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT)) /7.0)) AS BIGINT),2))ELSE CASE WHEN   ( (CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT)+(MOD((MOD((WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) + 6),7)+" + arg[1] + "),7)-3))>364 ) THEN CASE WHEN   ( ((CAST(DATE(MDY(12,31,YEAR(" + dateString + ")))-MDY(1,1,YEAR(" + dateString + "))+1 AS INT)+(MOD((MOD((WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) + 6),7)  +" + arg[1] + "),7)-3))-364)>3 ) THEN (YEAR(" + dateString + ") || '53') ELSE (YEAR(" + dateString + ")+1 || '01') END ELSE   (YEAR(" + dateString + ") || SUBSTR(CAST((100+CEIL((CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT)+  (MOD((MOD(WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) + 6,7)+" + arg[1] + "),7)-3))/7.0)) AS BIGINT),2)) END END AS BIGINT) ";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
         qry = " CASE WHEN ( (MOD((MOD((WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) + 6),7)+" + arg[1] + "),7)-3)*-1 >= CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT) ) THEN  CAST(CEIL( ((MOD((MOD((WEEKDAY(MDY(1,1,YEAR(" + dateString + ")-1)) + 6),7)+" + arg[1] + "),7)-3)+ CAST(DATE(MDY(12,31,YEAR(" + dateString + ")-1))-MDY(1,1,YEAR(" + dateString + ")-1)+1 AS INT)+CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT)) /7.0) AS BIGINT) ELSE CASE WHEN  ( (CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT)+(MOD((MOD((WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) + 6),7)+" + arg[1] + "),7)-3))>364 )  THEN CASE WHEN ( ((CAST(DATE(MDY(12,31,YEAR(" + dateString + ")))-MDY(1,1,YEAR(" + dateString + "))+1 AS INT)+ (MOD((MOD((WEEKDAY(MDY(1,1,YEAR(" + dateString + "))) + 6),7)+" + arg[1] + "),7)-3))-364)>3 ) THEN 53 ELSE 1 END  ELSE CAST(CEIL((CAST(DATE(" + dateString + ") - MDY(1, 1, YEAR(" + dateString + ")) + 1 AS INT)+(MOD((MOD((WEEKDAY(MDY(1,1,YEAR(" + dateString + ")))+6),7)+" + arg[1] + "),7)-3))/7.0) AS BIGINT) END END ";
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
      String dateArg = arguments.get(0).toString();
      String wkStartDay = arguments.get(1).toString();
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
         qry = "Iif( " + dateArg + " IS NOT NULL , CLng(Iif( (((((datepart('w',(YEAR(" + dateArg + ") & '-01-01'))+6) MOD 7 + " + wkStartDay + ")) MOD ( 7 )) -3) * - 1  >= DATEPART('y', " + dateArg + ") , ((YEAR(" + dateArg + ") -1)  & Mid(CStr((100 + fix((((( ((datepart('w',((YEAR(" + dateArg + ") -1) & '-01-01'))+6) MOD 7 + " + wkStartDay + ") ) MOD ( 7 )) -3) + DATEPART('y', ((YEAR(" + dateArg + ") -1) & '-12-31')) + DATEPART('y', " + dateArg + ")) / 7.0)+1)), 2, LEN((100 + fix((((( ((datepart('w',((YEAR(" + dateArg + ") -1) & '-01-01'))+6) MOD 7 + " + wkStartDay + ") ) MOD ( 7 )) -3) + DATEPART('y', ((YEAR(" + dateArg + ") -1) & '-12-31')) + DATEPART('y', " + dateArg + ")) / 7.0)+1)) -1))   , Iif( ((DATEPART('y', " + dateArg + ") + ((( ((datepart('w',(YEAR(" + dateArg + ") & '-01-01'))+6) MOD 7 + " + wkStartDay + ") ) MOD ( 7 )) -3))  > 364) , Iif( (((DATEPART('y',(YEAR(" + dateArg + ") & '-12-31')) + ((( ((datepart('w',(YEAR(" + dateArg + ") & '-01-01'))+6) MOD 7 + " + wkStartDay + ") ) MOD ( 7 )) -3)) -364)  > 3) , (YEAR(" + dateArg + ") & '53') , ((YEAR(" + dateArg + ") + 1) & '01') ) , (YEAR(" + dateArg + ") & Mid(CStr((100 + fix((DATEPART('y', " + dateArg + ") + ((( ((datepart('w',(YEAR(" + dateArg + ") & '-01-01'))+6) MOD 7 + " + wkStartDay + ") ) MOD ( 7 )) -3)) / 7.0)+1)), 2, LEN((100 + fix((DATEPART('y', " + dateArg + ") + ((( ((datepart('w',(YEAR(" + dateArg + ")  & '-01-01'))+6) MOD 7 + " + wkStartDay + ") ) MOD ( 7 )) -3)) / 7.0)+1)) -1))  ) )) , NULL)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
         qry = "Iif( " + dateArg + " IS NOT NULL , CLng(Iif( (((((datepart('w',(YEAR(" + dateArg + ") & '-01-01'))+6) MOD 7 + " + wkStartDay + ")) MOD ( 7 )) -3) * - 1  >= DATEPART('y', " + dateArg + ") ,  fix((((( ((datepart('w',((YEAR(" + dateArg + ") -1) & '-01-01'))+6) MOD 7 + " + wkStartDay + ") ) MOD ( 7 )) -3) + DATEPART('y', ((YEAR(" + dateArg + ") -1) & '-12-31')) + DATEPART('y', " + dateArg + ")) / 7.0)+1 , Iif( ((DATEPART('y', " + dateArg + ") + ((( ((datepart('w',(YEAR(" + dateArg + ") & '-01-01'))+6) MOD 7 + " + wkStartDay + ") ) MOD ( 7 )) -3))  > 364) , Iif( (((DATEPART('y',(YEAR(" + dateArg + ") & '-12-31')) + ((( ((datepart('w',(YEAR(" + dateArg + ") & '-01-01'))+6) MOD 7 + " + wkStartDay + ") ) MOD ( 7 )) -3)) -364)  > 3) , 53 , 1 ) , fix((DATEPART('y', " + dateArg + ") + ((( ((datepart('w',(YEAR(" + dateArg + ")  & '-01-01'))+6) MOD 7 + " + wkStartDay + ") ) MOD ( 7 )) -3)) / 7.0)+1 ) )) , NULL)";
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

      String qry = "";
      String dateString = StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString());
      if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
         qry = " CAST(CASE WHEN ( (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3)*-1 >= DAYOFYEAR(" + dateString + ") ) THEN   ((YEAR(" + dateString + ")-1) || SUBSTRING(TO_CHAR(CAST((100+CEIL( CAST(((MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ")-1 || '-01-01' AS DATE)) +5,7)  +" + arguments.get(1).toString() + "),7)-3)+DAYOFYEAR(CAST(YEAR(" + dateString + ")-1 || '-12-31' AS DATE))+DAYOFYEAR(" + dateString + "))  AS DECIMAL) /7.0)) AS BIGINT)),2))ELSE CASE WHEN   ( (DAYOFYEAR(" + dateString + ")+(MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3))>364 ) THEN CASE WHEN   ( ((DAYOFYEAR(CAST(YEAR(" + dateString + ") || '-12-31' AS DATE))+(MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)  +" + arguments.get(1).toString() + "),7)-3))-364)>3 ) THEN (YEAR(" + dateString + ") || '53') ELSE (YEAR(" + dateString + ")+1 || '01') END ELSE   (YEAR(" + dateString + ") || SUBSTRING(TO_CHAR(CAST((100+CEIL(CAST((DAYOFYEAR(" + dateString + ")+  (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3)) AS DECIMAL) /7.0)) AS BIGINT)),2)) END END AS BIGINT) ";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
         qry = " CASE WHEN ( (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3)*-1 >= DAYOFYEAR(" + dateString + ") ) THEN  CAST(CEIL(CAST(((MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ")-1 || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3)+ DAYOFYEAR(CAST(YEAR(" + dateString + ")-1 || '-12-31' AS DATE))+DAYOFYEAR(" + dateString + ")) AS DECIMAL) /7.0) AS BIGINT) ELSE CASE WHEN  ( (DAYOFYEAR(" + dateString + ")+(MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3))>364 )  THEN CASE WHEN ( ((DAYOFYEAR(CAST(YEAR(" + dateString + ") || '-12-31' AS DATE))+ (MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3))-364)>3 ) THEN 53 ELSE 1 END  ELSE CAST(CEIL(CAST((DAYOFYEAR(" + dateString + ")+(MOD((MOD(DAYOFWEEK(CAST(YEAR(" + dateString + ") || '-01-01' AS DATE)) +5,7)+" + arguments.get(1).toString() + "),7)-3)) AS DECIMAL)/7.0) AS BIGINT) END END ";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }
}
