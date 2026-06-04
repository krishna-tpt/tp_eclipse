package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class getdate extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arg = new Vector();
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      if (this.functionName.getColumnName() != null) {
         if (this.functionName.getColumnName().equalsIgnoreCase("GETUTCDATE")) {
            if (isdenodo) {
               this.functionName.setColumnName("CURRENT_DATE");
               this.setFunctionArguments(new Vector());
            } else {
               this.functionName.setColumnName("SYS_EXTRACT_UTC");
               SelectColumn sc = new SelectColumn();
               Vector funcArg = new Vector();
               funcArg.add("SYSTIMESTAMP");
               sc.setColumnExpression(funcArg);
               arg.add(sc);
               this.setFunctionArguments(arg);
            }
         } else {
            String qry;
            if (!this.functionName.getColumnName().equalsIgnoreCase("now") && !this.functionName.getColumnName().equalsIgnoreCase("systimestamp") && !this.functionName.getColumnName().equalsIgnoreCase("utc_timestamp")) {
               if (this.functionName.getColumnName().equalsIgnoreCase("SYSDATE") || this.functionName.getColumnName().equalsIgnoreCase("TO_DATE(SYSDATE)")) {
                  if (isdenodo) {
                     qry = "TO_LOCALDATE('yyyy-MM-dd',CURRENT_DATE)";
                     this.functionName.setColumnName(qry);
                  }

                  this.setOpenBracesForFunctionNameRequired(false);
               }
            } else {
               qry = isdenodo ? "CURRENT_TIMESTAMP" : "CAST(systimestamp as Timestamp)";
               this.functionName.setColumnName(qry);
               this.setOpenBracesForFunctionNameRequired(false);
            }
         }
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("now") || this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("sysdate")) {
         this.functionName.setColumnName("GETDATE");
         this.setFunctionArguments(new Vector());
      }

      if (this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("curdate") || this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("currentdate") || this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("current_date") || this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("cur_date")) {
         this.functionName.setColumnName("CONVERT(DATE,CURRENT_TIMESTAMP)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("now") || this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("sysdate")) {
         this.functionName.setColumnName("GETDATE");
         this.setFunctionArguments(new Vector());
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arg = new Vector();
      if (this.functionName.getColumnName().equalsIgnoreCase("now")) {
         arg.add("NOW");
      } else {
         arg.add("CURRENT DATE");
      }

      this.setFunctionArguments(arg);
      this.functionName.setColumnName("");
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("now")) {
         if (from_sqs != null && from_sqs.isAmazonRedShift()) {
            this.functionName.setColumnName("getdate");
         }

      } else if (!this.functionName.getColumnName().equalsIgnoreCase("sysdate")) {
         this.functionName.setColumnName("");
         Vector arg = new Vector();
         arg.add("CURRENT_DATE");
         this.setFunctionArguments(arg);
      } else {
         if (from_sqs != null && from_sqs.isAmazonRedShift()) {
            this.setOpenBracesForFunctionNameRequired(false);
         } else {
            this.functionName.setColumnName("NOW");
         }

      }
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("now")) {
         this.functionName.setColumnName("CAST(CURRENT_TIMESTAMP AS TIMESTAMP)");
      } else {
         this.functionName.setColumnName("CURRENT_DATE");
      }

      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("now")) {
         this.functionName.setColumnName("CURRENT_TIMESTAMP");
      } else {
         this.functionName.setColumnName("CURRENT_DATE");
      }

      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (!this.functionName.getColumnName().equalsIgnoreCase("now") && !this.functionName.getColumnName().equalsIgnoreCase("current_timestamp")) {
         this.functionName.setColumnName("DATE");
      } else {
         this.functionName.setColumnName("DATETIME('now')");
         this.setOpenBracesForFunctionNameRequired(false);
      }

      this.setFunctionArguments(new Vector());
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (from_sqs != null && from_sqs.isHyperSql()) {
         if (!this.functionName.getColumnName().equalsIgnoreCase("now") && !this.functionName.getColumnName().equalsIgnoreCase("current_timestamp")) {
            if (this.functionName.getColumnName().equalsIgnoreCase("current_time")) {
               this.functionName.setColumnName(" TO_CHAR(CURRENT_TIME,'HH24:MI:SS') ");
            }
         } else {
            this.functionName.setColumnName(" TIMESTAMP(CURRENT_TIMESTAMP) ");
         }

         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         this.functionName.setColumnName("CURRENT_TIMESTAMP");
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arg = new Vector();
      arg.add("CURRENT_TIMESTAMP");
      this.setFunctionArguments(arg);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (!this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("curdate") && !this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("currentdate") && !this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("current_date") && !this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("cur_date")) {
         this.functionName.setColumnName("CURRENT");
      } else {
         this.functionName.setColumnName("DATE(CURRENT)");
      }

      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arg = new Vector();
      arg.add("CURRENT_TIMESTAMP");
      this.setFunctionArguments(arg);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arg = new Vector();
      arg.add("CURRENT_TIMESTAMP");
      this.setFunctionArguments(arg);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arg = new Vector();
      if (this.functionName.getColumnName().equalsIgnoreCase("now")) {
         this.functionName.setColumnName("CURRENT_TIMESTAMP");
      } else {
         this.functionName.setColumnName("CURRENT_DATE");
      }

      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(arg);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CURRENT_DATETIME");
      this.setFunctionArguments(new Vector());
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arg = new Vector();
      arg.add("CURRENT_TIMESTAMP");
      this.setFunctionArguments(arg);
   }

   public void toExcel(SelectQueryStatement to_Sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (!this.functionName.getColumnName().equalsIgnoreCase("now") && !this.functionName.getColumnName().equalsIgnoreCase("current_timestamp")) {
         if (!this.functionName.getColumnName().equalsIgnoreCase("current_time") && !this.functionName.getColumnName().equalsIgnoreCase("time")) {
            this.functionName.setColumnName("Format(date(),'dd,MMM yyyy')");
         } else {
            this.functionName.setColumnName("Format(time(),'hh:nn:ss')");
         }
      } else {
         this.functionName.setColumnName("Format(now(),'dd,MMM yyyy hh:nn:ss')");
      }

      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (!this.functionName.getColumnName().equalsIgnoreCase("now") && !this.functionName.getColumnName().equalsIgnoreCase("current_timestamp")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("current_time")) {
            this.functionName.setColumnName(" TO_CHAR(CURRENT_TIME,'HH24:MI:SS') ");
         }
      } else {
         this.functionName.setColumnName(" TIMESTAMP(CURRENT_TIMESTAMP) ");
      }

      this.setOpenBracesForFunctionNameRequired(false);
   }
}
