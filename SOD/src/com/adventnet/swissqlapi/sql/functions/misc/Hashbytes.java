package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class Hashbytes extends FunctionCalls {
   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("HASHBYTES");
      Vector arguments = new Vector();
      Vector argumentsConcat = new Vector();
      SelectColumn concatenatedCol = new SelectColumn();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (i_count != 0 && i_count != this.functionArguments.size() - 1) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               argumentsConcat.addElement("convert(nvarchar(4000)," + ((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs) + ") + ");
            } else {
               argumentsConcat.addElement("convert(nvarchar(4000)," + this.functionArguments.elementAt(i_count) + ") + ");
            }
         } else if (i_count != 0 && i_count == this.functionArguments.size() - 1) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               argumentsConcat.addElement("convert(nvarchar(4000)," + ((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs) + ")");
            } else {
               argumentsConcat.addElement("convert(nvarchar(4000)," + this.functionArguments.elementAt(i_count) + ")");
            }
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      concatenatedCol.setColumnExpression(argumentsConcat);
      arguments.addElement(concatenatedCol);
      this.setFunctionArguments(arguments);
   }
}
