package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class NumToYMInterval extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 2) {
         Vector newArguments = new Vector();
         String format = "";
         boolean isNumber = false;
         String period = "";
         String sign = "";
         SelectColumn tempSC1;
         Vector columnExp1;
         String resultString;
         if (this.functionArguments.get(1) instanceof SelectColumn) {
            tempSC1 = (SelectColumn)this.functionArguments.get(1);
            columnExp1 = tempSC1.getColumnExpression();
            if (columnExp1.get(0) instanceof String) {
               resultString = (String)columnExp1.get(0);
               if (resultString.trim().startsWith("'") && resultString.trim().endsWith("'")) {
                  format = resultString.replaceAll("'", "");
               }
            }
         }

         int tempPeriod;
         if (this.functionArguments.get(0) instanceof SelectColumn) {
            tempSC1 = (SelectColumn)this.functionArguments.get(0);
            columnExp1 = tempSC1.getColumnExpression();
            if (columnExp1.get(0) instanceof String) {
               period = (String)columnExp1.get(0);
               if (columnExp1.size() == 2 && columnExp1.get(1) instanceof String && (period.trim().equals("-") || period.trim().equals("+"))) {
                  sign = (String)columnExp1.get(0);
                  resultString = (String)columnExp1.get(1);
                  period = resultString;
               }

               if (period.indexOf("'") != -1) {
                  period = period.replaceAll("'", "");
               }

               try {
                  tempPeriod = Integer.parseInt(period);
                  isNumber = true;
               } catch (NumberFormatException var15) {
                  isNumber = false;
               }
            }
         }

         if (isNumber && (format.trim().equalsIgnoreCase("month") || format.trim().equalsIgnoreCase("year"))) {
            String year = "00";
            String month = "00";
            int delimitter;
            if (format.trim().equalsIgnoreCase("year")) {
               tempPeriod = period.length();
               delimitter = year.length() - tempPeriod;
               year = year.substring(0, delimitter) + period;
            } else if (format.trim().equalsIgnoreCase("month")) {
               tempPeriod = period.length();
               delimitter = month.length() - tempPeriod;
               month = month.substring(0, delimitter) + period;
            }

            resultString = "'" + sign + year + "-" + month + "'";
            this.functionName.setColumnName("CAST");
            this.setAsDatatype("AS");
            SelectColumn sc = new SelectColumn();
            Vector colExp = new Vector();
            colExp.add(resultString);
            sc.setColumnExpression(colExp);
            DateClass intervalParameters = new DateClass();
            intervalParameters.setDatatypeName("INTERVAL YEAR TO MONTH");
            newArguments.add(sc);
            newArguments.add(intervalParameters);
            this.setFunctionArguments(newArguments);
         }
      }

   }
}
