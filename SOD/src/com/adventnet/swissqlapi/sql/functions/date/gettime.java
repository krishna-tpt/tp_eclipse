package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class gettime extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (!this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("now")) {
         this.functionName.setColumnName("");
         Vector arg = new Vector();
         if (from_sqs != null && from_sqs.isAmazonRedShift()) {
            this.functionName.setColumnName("to_char(getdate(), 'HH24:MI:SS')");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else {
            String fsp = this.functionArguments.isEmpty() ? "0" : this.functionArguments.get(0).toString();
            if (fsp.equals("0")) {
               arg.add("CURRENT_TIME");
               this.setFunctionArguments(arg);
            } else {
               String qry = "CURRENT_TIME(" + fsp + ")";
               this.functionName.setColumnName(qry);
               this.setOpenBracesForFunctionNameRequired(false);
               this.functionArguments = new Vector();
            }
         }

      }
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arg = new Vector();
      arg.add("CURRENT_TIME");
      this.setFunctionArguments(arg);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arg = new Vector();
      arg.add("CURRENT TIME");
      this.setFunctionArguments(arg);
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

      if (this.functionName.getColumnName().equalsIgnoreCase("TO_CHAR(SYSDATE,'HH:MI:SS')")) {
         if (isdenodo) {
            this.functionName.setColumnName("CAST(CURRENT_TIMESTAMP AS TIME WITHOUT TIME ZONE)");
         }

         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      } else {
         this.setFunctionArguments(arguments);
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CONVERT(TIME,CURRENT_TIMESTAMP)");
      Vector arg = new Vector();
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(arg);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arg = new Vector();
      arg.add("CURRENT_TIME");
      this.setFunctionArguments(arg);
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE_FORMAT(CAST(CURRENT_TIME AS TIMESTAMP),'%H:%i:%s')");
      Vector arg = new Vector();
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(arg);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arg = new Vector();
      arg.add("CURRENT_TIME");
      this.setFunctionArguments(arg);
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TIME");
      this.setFunctionArguments(new Vector());
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName(" (CURRENT HOUR TO SECOND)::LVARCHAR ");
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }
}
