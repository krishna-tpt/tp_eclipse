package com.adventnet.swissqlapi.sql.functions.json;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class extract_json extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector(this.functionArguments.size());

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (i_count == 0) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         } else if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc_checkForError = (SelectColumn)this.functionArguments.get(i_count);
            Vector vc_checkForError = sc_checkForError.getColumnExpression();
            if (!(vc_checkForError.get(0) instanceof String)) {
               throw new ConvertException("Invalid Argument Value for Function extract_json", "INVALID_ARGUMENT_VALUE", new Object[]{"EXTRACT_JSON", "PATH", "Provide only string values enclosed in single quotes"});
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector funcArgs_if = new Vector(2);
      funcArgs_if.addElement(arguments.get(0));
      String concatedString = "'$";

      for(int i = 1; i < arguments.size(); ++i) {
         String argument = arguments.get(i).toString();
         if (argument.length() <= 2) {
            throw new ConvertException("Invalid Argument Value for Function extract_json", "INVALID_ARGUMENT_VALUE", new Object[]{"EXTRACT_JSON", "PATH", "Provide only string values enclosed in single quotes"});
         }

         if (argument.startsWith("'[") && argument.endsWith("]'")) {
            concatedString = concatedString + argument.substring(1, argument.length() - 1);
         } else if (argument.startsWith("'\"") && argument.endsWith("\"'")) {
            concatedString = concatedString + "." + argument.substring(1, argument.length() - 1);
         } else {
            if (!argument.startsWith("'") || !argument.endsWith("'")) {
               throw new ConvertException("Invalid Argument Value for Function extract_json", "INVALID_ARGUMENT_VALUE", new Object[]{"EXTRACT_JSON", "PATH", "Provide only string values enclosed in single quotes"});
            }

            concatedString = concatedString + ".\"" + argument.substring(1, argument.length() - 1) + "\"";
         }
      }

      concatedString = concatedString + "'";
      this.functionName.setColumnName("JSON_VALUE");
      funcArgs_if.addElement(concatedString);
      this.setFunctionArguments(funcArgs_if);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector(2);

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector v_arguments = new Vector();
      v_arguments.addElement(arguments.get(0));
      String concatedString = String.valueOf(arguments.get(1));
      concatedString = concatedString.substring(2, concatedString.length() - 1);
      char[] charArray = concatedString.toCharArray();

      for(int i = 0; i < charArray.length; ++i) {
         char c = charArray[i];
         if (c == '"') {
            int endIndex = concatedString.indexOf(34, i + 1);
            if (endIndex != -1) {
               String currentToken = concatedString.substring(i + 1, endIndex);
               v_arguments.add("'" + currentToken + "'");
               i = endIndex;
            }
         } else if (c != '[' && c != ']' && c != '.' && c != '"' && c != '$') {
            v_arguments.add("'" + c + "'");
         }
      }

      this.functionName.setColumnName("json_extract_path_text_udf");
      this.setFunctionArguments(v_arguments);
   }
}
