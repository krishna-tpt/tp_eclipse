package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class indexof extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int argLength = this.functionArguments.size();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            sc.convertSelectColumnToTextDataType();
            arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      this.functionName.setColumnName("STRPOS");
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

      if (this.functionName.getColumnName().equalsIgnoreCase("INDEXOF") || this.functionName.getColumnName().equalsIgnoreCase("INDEX_OF_STRING") || this.functionName.getColumnName().equalsIgnoreCase("STRPOS") || this.functionName.getColumnName().equalsIgnoreCase("SUBSTRING_POSITION")) {
         Object temp = arguments.get(0);
         arguments.set(0, arguments.get(1));
         arguments.set(1, temp);
      }

      this.functionName.setColumnName("LOCATE");
      this.setFunctionArguments(arguments);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer[] argu = new StringBuffer[this.functionArguments.size()];
      String qry = "";
      String firstArg = "";
      String secArg = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         argu[i_count] = new StringBuffer();
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            sc.convertSelectColumnToTextDataType();
            argu[i_count].append(sc.toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            argu[i_count].append(this.functionArguments.elementAt(i_count));
         }
      }

      firstArg = argu[0].toString();
      secArg = argu[1].toString();
      if (this.functionName.getColumnName().equalsIgnoreCase("INDEXOF")) {
         firstArg = argu[1].toString();
         secArg = argu[0].toString();
      }

      qry = " position(" + firstArg + "," + secArg + ")";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            arguments.addElement(sc.toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      this.functionName.setColumnName("STRPOS");
   }
}
