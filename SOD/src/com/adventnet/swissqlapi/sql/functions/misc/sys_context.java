package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class sys_context extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String fnName = this.functionName.getColumnName();
      this.functionName.setColumnName("SYS_CONTEXT");
      if (fnName.equalsIgnoreCase("db_id")) {
         if (this.functionArguments.size() == 0) {
            arguments.add("'USERENV'");
            arguments.add("'CURRENT_SCHEMAID'");
         } else {
            this.functionName.setColumnName("");
            arguments.add("SELECT user_id SCHEMA_ID FROM all_users WHERE username = " + this.functionArguments.get(0).toString().toUpperCase());
         }
      } else if (fnName.equalsIgnoreCase("db_name") && this.functionArguments.size() == 0) {
         arguments.add("'USERENV'");
         arguments.add("'CURRENT_SCHEMA'");
      } else if (fnName.equalsIgnoreCase("host_id") && this.functionArguments.size() == 0) {
         arguments.add("'USERENV'");
         arguments.add("'CLIENT_IDENTIFIER'");
      } else if (fnName.equalsIgnoreCase("host_name") && this.functionArguments.size() == 0) {
         arguments.add("'USERENV'");
         arguments.add("'HOST'");
      }

      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nThe function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }
}
