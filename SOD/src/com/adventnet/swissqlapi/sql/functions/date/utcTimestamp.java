package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class utcTimestamp extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = " (current_timestamp at time zone 'utc') ";
      if (this.functionName.getColumnName().equals("utc_time")) {
         String fsp = this.functionArguments.isEmpty() ? "0" : this.functionArguments.get(0).toString();
         qry = " (current_time(" + fsp + ") at time zone 'utc') ";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (from_sqs != null && from_sqs.isHyperSql() && this.functionName.getColumnName().equalsIgnoreCase("utc_timestamp")) {
         this.functionName.setColumnName("TO_TIMESTAMP(CURRENT_TIMESTAMP AT TIME ZONE 'UTC')");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      }

   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "TO_TIMESTAMP(DATE_GMT('now'))";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("utc_timestamp")) {
         this.functionName.setColumnName("TO_TIMESTAMP_NTZ(convert_timezone('UTC', current_timestamp(0)))");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("utc_timestamp")) {
         this.functionName.setColumnName("GETUTCDATE");
         this.setFunctionArguments(new Vector());
      }

   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("utc_timestamp")) {
         this.functionName.setColumnName("CURRENT_TIMESTAMP");
         this.setFunctionArguments(new Vector());
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("utc_timestamp")) {
         this.functionName.setColumnName("(CURRENT TIMESTAMP - CURRENT TIMEZONE)");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      }

   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("utc_timestamp")) {
         if (from_sqs != null && from_sqs.isTrino()) {
            this.functionName.setColumnName("cast(at_timezone(current_timestamp ,'UTC') as timestamp)");
         } else {
            this.functionName.setColumnName("CAST((current_timestamp at time zone 'utc') AS TIMESTAMP)");
         }

         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      }

   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("utc_timestamp")) {
         this.functionName.setColumnName("CURRENT_UTCTIMESTAMP");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      }

   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("utc_timestamp")) {
         this.functionName.setColumnName("datetime('now')");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      }

   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("utc_timestamp")) {
         this.functionName.setColumnName("DBINFO('utc_to_datetime',DBINFO('utc_current'))");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      }

   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("utc_timestamp")) {
         this.functionName.setColumnName("now");
         this.setFunctionArguments(new Vector());
      }

   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("utc_timestamp")) {
         this.functionName.setColumnName("TO_TIMESTAMP(CURRENT_TIMESTAMP AT TIME ZONE 'UTC')");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      }

   }
}
