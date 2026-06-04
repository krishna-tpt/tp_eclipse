package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class NumToDSInterval extends FunctionCalls {
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
         String period = " ";
         String sign = "";
         SelectColumn tempSC1;
         Vector columnExp1;
         String minute;
         if (this.functionArguments.get(1) instanceof SelectColumn) {
            tempSC1 = (SelectColumn)this.functionArguments.get(1);
            columnExp1 = tempSC1.getColumnExpression();
            if (columnExp1.get(0) instanceof String) {
               minute = (String)columnExp1.get(0);
               if (minute.trim().startsWith("'") && minute.trim().endsWith("'")) {
                  format = minute.replaceAll("'", "");
               }
            }
         }

         if (this.functionArguments.get(0) instanceof SelectColumn) {
            tempSC1 = (SelectColumn)this.functionArguments.get(0);
            columnExp1 = tempSC1.getColumnExpression();
            if (columnExp1.get(0) instanceof String) {
               period = (String)columnExp1.get(0);
               if (columnExp1.size() == 2 && columnExp1.get(1) instanceof String && (period.trim().equals("-") || period.trim().equals("+"))) {
                  sign = (String)columnExp1.get(0);
                  minute = (String)columnExp1.get(1);
                  period = minute;
               }

               if (period.indexOf("'") != -1) {
                  period = period.replaceAll("'", "");
               }

               try {
                  int tempPeriod = Integer.parseInt(period);
                  isNumber = true;
               } catch (NumberFormatException var17) {
                  isNumber = false;
               }
            }
         }

         if (isNumber && (format.trim().equalsIgnoreCase("day") || format.trim().equalsIgnoreCase("hour") || format.trim().equalsIgnoreCase("minute") || format.trim().equalsIgnoreCase("second"))) {
            String day = "0000";
            String hour = "00";
            minute = "00";
            String second = "00";
            int length;
            int delimitter;
            if (format.trim().equalsIgnoreCase("day")) {
               length = period.length();
               delimitter = day.length() - length;
               day = day.substring(0, delimitter) + period;
            } else if (format.trim().equalsIgnoreCase("hour")) {
               length = period.length();
               delimitter = hour.length() - length;
               hour = hour.substring(0, delimitter) + period;
            } else if (format.trim().equalsIgnoreCase("minute")) {
               length = period.length();
               delimitter = minute.length() - length;
               minute = minute.substring(0, delimitter) + period;
            } else if (format.trim().equalsIgnoreCase("second")) {
               length = period.length();
               delimitter = second.length() - length;
               second = second.substring(0, delimitter) + period;
            }

            String resultString = "'" + sign + day + " " + hour + ":" + minute + ":" + second + "'";
            this.functionName.setColumnName("CAST");
            this.setAsDatatype("AS");
            SelectColumn sc = new SelectColumn();
            Vector colExp = new Vector();
            colExp.add(resultString);
            sc.setColumnExpression(colExp);
            DateClass intervalParameters = new DateClass();
            intervalParameters.setDatatypeName("INTERVAL DAY(4) TO SECOND(0)");
            newArguments.add(sc);
            newArguments.add(intervalParameters);
            this.setFunctionArguments(newArguments);
         }
      }

   }
}
