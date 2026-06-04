package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class MakeDate extends FunctionCalls {
   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String[] arguments = new String[this.functionArguments.size()];

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments[i_count] = "" + ((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs);
         } else {
            arguments[i_count] = "" + this.functionArguments.elementAt(i_count);
         }
      }

      String functionChange = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
         functionChange = "((str_to_date(concat(" + arguments[0] + ",'-01-01'),'%Y-%m-%d')+interval '1' day * (" + arguments[1] + ")) - interval '1' day)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("period_add")) {
         functionChange = "CAST(DATE_FORMAT(TIMESTAMPADD(MONTH," + arguments[1] + ", (CAST(concat(left(case when lpad(concat(''," + arguments[0] + "),6,'0') like '00%' then concat((case when cast(substr(lpad(concat(''," + arguments[0] + "),6,'0'),3,2) as integer) < 70 then '20' else '19' end),right(lpad(concat(''," + arguments[0] + "),6,'0'),4)) else lpad(concat(''," + arguments[0] + "),6,'0') end, 4), '-01-01') AS DATE) + interval '1' month * (integer(right(lpad(concat(''," + arguments[0] + "),6,'0'),2)) -1)) ),'%Y%m') AS INTEGER)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("period_diff")) {
         functionChange = "TIMESTAMPDIFF(MONTH,(CAST(concat(left(case when lpad(concat(''," + arguments[1] + "),6,'0') like '00%' then concat((case when cast(substr(lpad(concat(''," + arguments[1] + "),6,'0'),3,2) as integer) < 70 then '20' else '19' end),right(lpad(concat(''," + arguments[1] + "),6,'0'),4)) else lpad(concat(''," + arguments[1] + "),6,'0') end, 4), '-01-01') AS DATE) + interval '1' month * (integer(right(lpad(concat(''," + arguments[1] + "),6,'0'),2)) -1)), (CAST(concat(left(case when lpad(concat(''," + arguments[0] + "),6,'0') like '00%' then concat((case when cast(substr(lpad(concat(''," + arguments[0] + "),6,'0'),3,2) as integer) < 70 then '20' else '19' end),right(lpad(concat(''," + arguments[0] + "),6,'0'),4)) else lpad(concat(''," + arguments[0] + "),6,'0') end, 4), '-01-01') AS DATE) + interval '1' month * (integer(right(lpad(concat(''," + arguments[0] + "),6,'0'),2)) -1)) )";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("MAKETIME")) {
         functionChange = "CONCAT(" + arguments[0] + ",':',IF((" + arguments[1] + ") BETWEEN 0 AND 59 , (" + arguments[1] + "), NULL),':',IF((" + arguments[2] + ") BETWEEN 0 AND 59 , (" + arguments[2] + "), NULL))";
      }

      this.functionName.setColumnName(functionChange);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      String qry;
      if (isPostgreLiveDbs) {
         qry = from_sqs != null && from_sqs.isVerticaDb() ? "VARCHAR" : "TEXT";
         String qry = "";
         if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
            qry = "(CASE WHEN " + arguments.get(0).toString() + " > 0 AND " + arguments.get(0).toString() + " <= 9999 AND " + arguments.get(1).toString() + " > 0 THEN (TO_TIMESTAMP(CAST(" + arguments.get(0).toString() + " AS " + qry + ") || '-01-01'::" + qry + ", (LEFT('YYYY'::" + qry + ",LENGTH( CAST( " + arguments.get(0).toString() + "AS " + qry + "))) || '-MM-DD'::" + qry + ")) + (" + arguments.get(1).toString() + " -1) * Interval '1 day') ELSE NULL END)::DATE";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("PERIOD_DIFF")) {
            qry = "DATEDIFF(MONTH, DATE(CAST(concat(left(case when lpad(concat(''," + arguments.get(1).toString() + "),6,'0') like '00%' then concat((case when cast(substr(lpad(concat(''," + arguments.get(1).toString() + "),6,'0'),3,2) as integer) < 70 then '20' else '19' end),right(lpad(concat(''," + arguments.get(1).toString() + "),6,'0'),4)) else lpad(concat(''," + arguments.get(1).toString() + "),6,'0') end, 4), '-01-01') AS DATE) + interval '1' month * (CAST(right(lpad(concat(''," + arguments.get(1).toString() + "),6,'0'),2) AS INTEGER) -1)),DATE(CAST(concat(left(case when lpad(concat(''," + arguments.get(0).toString() + "),6,'0') like '00%' then concat((case when cast(substr(lpad(concat(''," + arguments.get(0).toString() + "),6,'0'),3,2) as integer) < 70 then '20' else '19' end),right(lpad(concat(''," + arguments.get(0).toString() + "),6,'0'),4)) else lpad(concat(''," + arguments.get(0).toString() + "),6,'0') end, 4), '-01-01') AS DATE) + interval '1' month * (CAST(right(lpad(concat(''," + arguments.get(0).toString() + "),6,'0'),2) AS INTEGER) -1)) )";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("PERIOD_ADD")) {
            qry = "CAST(TO_CHAR( (DATE(CAST(concat(left(case when lpad(concat(''," + arguments.get(0).toString() + "),6,'0') like '00%' then concat((case when cast(substr(lpad(concat(''," + arguments.get(0).toString() + "),6,'0'),3,2) as integer) < 70 then '20' else '19' end),right(lpad(concat(''," + arguments.get(0).toString() + "),6,'0'),4)) else lpad(concat(''," + arguments.get(0).toString() + "),6,'0') end, 4), '-01-01') AS DATE) + interval '1' month * (CAST(right(lpad(concat(''," + arguments.get(0).toString() + "),6,'0'),2) AS INTEGER) -1)) + (interval '1 month' * " + arguments.get(1).toString() + ")), 'YYYYMM') AS INTEGER)";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("MAKETIME")) {
            qry = "(" + arguments.get(0).toString() + " || ':' || (CASE WHEN (" + arguments.get(1).toString() + ") BETWEEN 0 AND 59 THEN (" + arguments.get(1).toString() + ") ELSE NULL END) || ':' || (CASE WHEN (" + arguments.get(2).toString() + ") BETWEEN 0 AND 59 THEN (" + arguments.get(2).toString() + ") ELSE NULL END))";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("MAKETIME")) {
         qry = " make_time(cast(" + arguments.get(0) + " as int) ,cast(" + arguments.get(1) + " as int), cast(" + arguments.get(2) + " as double precision))";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
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

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
         qry = "DATE_FROM_PARTS( " + arguments.get(0).toString() + ", 1, " + arguments.get(1).toString() + " )";
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
      if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
         String arg1 = arguments.get(0).toString().replace("'", "");
         String arg2 = arguments.get(1).toString().replace("'", "");
         qry = "CAST(CASE WHEN " + arg1 + " > 0 AND " + arg1 + " <= 9999 AND " + arg2 + " > 0 THEN ADD_DAYS(TO_TIMESTAMP('" + arg1 + "-01-01 00:00:00')," + arg2 + "-1) ELSE NULL END AS DATE)";
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
      if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
         String arg1 = arguments.get(0).toString().replace("'", "");
         String arg2 = arguments.get(1).toString().replace("'", "");
         qry = "(CASE WHEN " + arg1 + " > 0 AND " + arg1 + " <= 9999 AND " + arg2 + " > 0 THEN DATE('" + arg1 + "-01-01 00:00:00','+' || (" + arg2 + "-1) || ' days') ELSE NULL END)";
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

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
         qry = " CAST(DATEADD(d,CAST(" + arguments.get(1).toString() + " AS BIGINT)-1 ,CONCAT(CAST( " + arguments.get(0).toString() + " AS BIGINT),'-01-01'))AS DATE)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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
      if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
         if (!(arguments.elementAt(1) instanceof String) && arguments.elementAt(0) instanceof String) {
            throw new ConvertException("Invalid Argument Value for Function " + this.functionName.getColumnName() + "", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{this.functionName.getColumnName()});
         }

         String year = arguments.get(0).toString();
         year = year.replaceAll("'", "");
         String days = arguments.get(1).toString();
         days = days.replaceAll("'", "");
         if (isdenodo) {
            qry = "CASE WHEN " + days + "=0 THEN NULL ELSE ADDDAY(TO_LOCALDATE('yyyy-MM-dd','" + year + "-01-01')," + days + "-1) END";
         } else {
            qry = "CASE WHEN " + days + "=0 THEN null ELSE (to_date('" + year + "-01-01','YYYY-MM-DD')+ " + days + " - 1) END";
         }
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

      this.setFunctionArguments(arguments);
      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
         qry = "CAST(CASE WHEN " + arguments.get(0).toString().replace("'", "") + " > 0 AND " + arguments.get(0).toString().replace("'", "") + " <= 9999 AND " + arguments.get(1).toString().replace("'", "") + " > 0 THEN TIMESTAMP_ADD(PARSE_TIMESTAMP((CAST('%Y'AS STRING)|| CAST('-%m-%d'AS STRING)),CAST(" + arguments.get(0).toString() + " AS STRING) || CAST('-01-01'as STRING) ), Interval (" + arguments.get(1).toString().replace("'", "") + " -1) day) ELSE NULL END AS DATE)";
      } else if (this.functionName.getColumnName().equalsIgnoreCase("MAKETIME")) {
         qry = "TIME(" + arguments.get(0).toString() + " ," + arguments.get(1).toString() + " ," + arguments.get(2).toString() + ")";
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

      this.setFunctionArguments(arguments);
      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
         String arg1 = arguments.get(0).toString().replace("'", "");
         String arg2 = arguments.get(1).toString().replace("'", "");
         qry = "CAST(CASE WHEN " + arg1 + " > 0 AND " + arg1 + " <= 9999 AND " + arg2 + " > 0 THEN DATE_ADD('day'," + arg2 + "-1,TIMESTAMP'" + arg1 + "-01-01 00:00:00' ) ELSE NULL END AS DATE)";
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

      this.setFunctionArguments(arguments);
      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
         qry = "( DATE(CAST( " + arguments.get(0).toString() + " AS VARCHAR)||'-01-01') + cast(" + arguments.get(1).toString() + " as INT) Days )";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("MAKE_DATE")) {
         this.functionName.setColumnName("MAKEDATE");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (from_sqs.isHyperSql()) {
         String qry = " CAST(CONCAT(" + arguments.get(0).toString() + ",'-01-01') AS DATE) + (CAST(" + arguments.get(1).toString() + " AS BIGINT)-1) DAY";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

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

      this.setFunctionArguments(arguments);
      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
         String arg1 = arguments.get(0).toString().replace("'", "");
         String arg2 = arguments.get(1).toString().replace("'", "");
         qry = " ((CASE WHEN " + arg1 + " > 0 AND " + arg1 + " <= 9999 AND " + arg2 + " > 0 THEN ('" + arg1 + "-01-01'::DATE + (" + arg2 + " - 1) UNITS DAY)  ELSE NULL END)::DATE) ";
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

      this.setFunctionArguments(arguments);
      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
         String arg1 = arguments.get(0).toString().replace("'", "");
         String arg2 = arguments.get(1).toString().replace("'", "");
         qry = " (Iif(" + arg1 + " > 0 AND " + arg1 + " <= 9999 AND " + arg2 + " > 0 , DATESERIAL(" + arg1 + ",1," + arg2 + ") , NULL)) ";
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

      String qry = " CAST(CONCAT(" + arguments.get(0).toString() + ",'-01-01') AS DATE) + (CAST(" + arguments.get(1).toString() + " AS BIGINT)-1) DAY";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }
}
