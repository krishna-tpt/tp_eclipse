package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class encrypt extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnName = this.functionName.getColumnName();
      Vector arguments = new Vector();
      int i_count;
      if (fnName.equalsIgnoreCase("md5")) {
         for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
               sc.convertSelectColumnToTextDataType();
               arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
      } else {
         for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            Object obj = this.functionArguments.elementAt(i_count);
            if (obj instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)obj).toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(obj);
            }
         }

         String qry = "";
         if (fnName.equalsIgnoreCase("ZA_AES128_DECRYPT")) {
            qry = "convert_from(decrypt((" + arguments.get(0).toString() + ")::bytea,(" + arguments.get(1).toString() + ")::bytea, 'aes'),'utf8')";
         } else if (fnName.equalsIgnoreCase("ZA_AES128_ENCRYPT")) {
            qry = "(encrypt(convert_to((" + arguments.get(0).toString() + ")::text,'utf8'), (" + arguments.get(1).toString() + ")::bytea, 'aes')::bytea)";
         }

         this.setOpenBracesForFunctionNameRequired(false);
         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
      }

   }
}
