package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class currentTimestamp extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = " (CURRENT_TIMESTAMP)";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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

      String qry = " (CURRENT_TIMESTAMP)";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("current_timestamp")) {
         this.functionName.setColumnName("CURRENT_TIMESTAMP");
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("current_timestamp")) {
         this.functionName.setColumnName("CURRENT_TIMESTAMP");
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("current_timestamp")) {
         this.functionName.setColumnName("CURRENT_TIMESTAMP");
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("current_timestamp")) {
         this.functionName.setColumnName("CAST(CURRENT_TIMESTAMP AS TIMESTAMP)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("current_timestamp")) {
         this.functionName.setColumnName("CURRENT TIMESTAMP");
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("current_timestamp")) {
         this.functionName.setColumnName(" CURRENT ");
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }
}
