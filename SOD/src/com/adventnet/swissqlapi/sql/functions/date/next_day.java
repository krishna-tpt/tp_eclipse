package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class next_day extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Object obj = this.functionArguments.elementAt(0);
      String firstArg = null;
      String secondArg = null;
      if (obj instanceof SelectColumn) {
         firstArg = ((SelectColumn)obj).toSybaseSelect(to_sqs, from_sqs).toString();
      } else {
         firstArg = obj.toString();
      }

      secondArg = this.functionArguments.elementAt(1).toString();
      String target = this.convertToCaseWhen(firstArg, secondArg);
      this.functionName.setColumnName("");
      Vector arguments = new Vector();
      arguments.add(target);
      this.setFunctionArguments(arguments);
   }

   private String convertToCaseWhen(String firstArg, String secondArg) {
      int i = 0;
      String target = "";
      if (secondArg.equalsIgnoreCase("'MONDAY'")) {
         i = 1;
      } else if (secondArg.equalsIgnoreCase("'TUESDAY'")) {
         i = 2;
      } else if (secondArg.equalsIgnoreCase("'WEDNESDAY'")) {
         i = 3;
      } else if (secondArg.equalsIgnoreCase("'THURSDAY'")) {
         i = 4;
      } else if (secondArg.equalsIgnoreCase("'FRIDAY'")) {
         i = 5;
      } else if (secondArg.equalsIgnoreCase("'SATURDAY'")) {
         i = 6;
      } else if (secondArg.equalsIgnoreCase("'SUNDAY'")) {
         i = 7;
      }

      if (i != 0 && !SwisSQLOptions.convertNextDayWithAllCase) {
         target = "\nCASE\nWHEN datepart(cdw," + firstArg + ") >= " + i + " THEN dateadd(day," + (i + 7) + "-datepart(cdw," + firstArg + ")," + firstArg + ")" + "\nWHEN datepart(cdw," + firstArg + ") < " + i + " THEN dateadd(day," + i + "-datepart(cdw," + firstArg + ") ," + firstArg + ")\nEND";
      } else {
         target = target + "\nCASE";

         for(int j = 1; j < 8; ++j) {
            if (j == 1) {
               target = target + "\nWHEN UPPER(" + secondArg + ") = 'MONDAY' THEN";
            } else if (j == 2) {
               target = target + "\nWHEN UPPER(" + secondArg + ") = 'TUESDAY' THEN";
            } else if (j == 3) {
               target = target + "\nWHEN UPPER(" + secondArg + ") = 'WEDNESDAY' THEN";
            } else if (j == 4) {
               target = target + "\nWHEN UPPER(" + secondArg + ") = 'THURSDAY' THEN";
            } else if (j == 5) {
               target = target + "\nWHEN UPPER(" + secondArg + ") = 'FRIDAY' THEN";
            } else if (j == 6) {
               target = target + "\nWHEN UPPER(" + secondArg + ") = 'SATURDAY' THEN";
            } else if (j == 7) {
               target = target + "\nWHEN UPPER(" + secondArg + ") = 'SUNDAY' THEN";
            }

            target = target + "\n\tCASE\n\tWHEN datepart(cdw," + firstArg + ") >= " + j + " THEN dateadd(day," + (j + 7) + "-datepart(cdw," + firstArg + ")," + firstArg + ")\n\t" + "WHEN datepart(cdw," + firstArg + ") < " + j + " THEN dateadd(day," + j + "-datepart(cdw," + firstArg + ") ," + firstArg + ")\n\tEND";
         }

         target = target + "\nEND";
      }

      return target;
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Object obj = this.functionArguments.elementAt(0);
      String firstArg = null;
      String secondArg = null;
      if (obj instanceof SelectColumn) {
         firstArg = ((SelectColumn)obj).toNetezzaSelect(to_sqs, from_sqs).toString();
      } else {
         firstArg = obj.toString();
      }

      secondArg = this.functionArguments.elementAt(1).toString();
      String target = this.convertToCaseWhenForNetezza(firstArg, secondArg);
      this.functionName.setColumnName("");
      Vector arguments = new Vector();
      arguments.add(target);
      this.setFunctionArguments(arguments);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("NEXT_DAY");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   private String convertToCaseWhenForNetezza(String firstArg, String secondArg) {
      int i = 0;
      String target = "";
      if (secondArg.equalsIgnoreCase("'MONDAY'")) {
         i = 1;
      } else if (secondArg.equalsIgnoreCase("'TUESDAY'")) {
         i = 2;
      } else if (secondArg.equalsIgnoreCase("'WEDNESDAY'")) {
         i = 3;
      } else if (secondArg.equalsIgnoreCase("'THURSDAY'")) {
         i = 4;
      } else if (secondArg.equalsIgnoreCase("'FRIDAY'")) {
         i = 5;
      } else if (secondArg.equalsIgnoreCase("'SATURDAY'")) {
         i = 6;
      } else if (secondArg.equalsIgnoreCase("'SUNDAY'")) {
         i = 7;
      }

      if (i != 0 && !SwisSQLOptions.convertNextDayWithAllCase) {
         target = "\nCASE\nWHEN date_part('dow', date " + firstArg + ") >= " + i + " THEN dateadd(day," + (i + 7) + "-date_part('dow', date " + firstArg + "), date " + firstArg + ")" + "\nWHEN date_part('dow', date " + firstArg + ") < " + i + " THEN dateadd(day," + i + "-date_part('dow', date " + firstArg + ") , date " + firstArg + ")\nEND";
      } else {
         target = target + "\nCASE";

         for(int j = 1; j < 8; ++j) {
            if (j == 1) {
               target = target + "\nWHEN UPPER(" + secondArg + ") = 'MONDAY' THEN";
            } else if (j == 2) {
               target = target + "\nWHEN UPPER(" + secondArg + ") = 'TUESDAY' THEN";
            } else if (j == 3) {
               target = target + "\nWHEN UPPER(" + secondArg + ") = 'WEDNESDAY' THEN";
            } else if (j == 4) {
               target = target + "\nWHEN UPPER(" + secondArg + ") = 'THURSDAY' THEN";
            } else if (j == 5) {
               target = target + "\nWHEN UPPER(" + secondArg + ") = 'FRIDAY' THEN";
            } else if (j == 6) {
               target = target + "\nWHEN UPPER(" + secondArg + ") = 'SATURDAY' THEN";
            } else if (j == 7) {
               target = target + "\nWHEN UPPER(" + secondArg + ") = 'SUNDAY' THEN";
            }

            target = target + "\n\tCASE\n\tWHEN date_part('dow', date " + firstArg + " ) >= " + j + " THEN  date " + firstArg + " + abs( " + (j + 7) + " - date_part('dow', date " + firstArg + " )) * interval '1 day' " + "\n\t" + " WHEN date_part('dow', date " + firstArg + " ) < " + j + " THEN  date " + firstArg + "+ abs( " + j + " - date_part('dow', date " + firstArg + " )) * interval '1 day' " + "\n\tEND";
         }

         target = target + "\nEND";
      }

      return target;
   }
}
