package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class addmonths extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATEADD");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 2) {
         this.functionArguments.addElement(this.functionArguments.get(0));
         this.functionArguments.setElementAt("M", 0);
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATEADD");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 2) {
         this.functionArguments.addElement(this.functionArguments.get(0));
         this.functionArguments.setElementAt("MM", 0);
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 2) {
         SelectColumn sc = new SelectColumn();
         Vector colExp = new Vector();
         colExp.add(this.functionArguments.get(0));
         String isSign = new String();
         Object obj = ((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0);
         if (obj instanceof TableColumn) {
            TableColumn tableColumn = (TableColumn)obj;
            isSign = tableColumn.toString();
         } else if (obj instanceof String) {
            isSign = (String)obj;
         }

         if ("-".equals(isSign)) {
            colExp.add("-");
            colExp.add(((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(1) + "  MONTHS");
         } else {
            colExp.add("+");
            colExp.add(obj + "  MONTHS");
         }

         sc.setColumnExpression(colExp);
         this.functionArguments.setElementAt(sc, 0);
         this.functionArguments.setSize(1);
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (!SwisSQLOptions.passFunctionsWithOutThrowingConvertException) {
         throw new ConvertException("ADD_MONTHS function is not supported in PostgreSQL");
      }
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE_ADD");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 2) {
         SelectColumn sc = new SelectColumn();
         Vector colExp = new Vector();
         colExp.add(" INTERVAL ");
         new String();
         String isSign;
         if (((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0) instanceof TableColumn) {
            TableColumn tableColumn = (TableColumn)((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0);
            isSign = tableColumn.toString();
         } else {
            isSign = (String)((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0);
         }

         if ("-".equals(isSign)) {
            colExp.add(((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(1) + "  MONTHS");
            this.functionName.setColumnName("DATE_SUB");
         } else {
            colExp.add(((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0) + "  MONTH");
         }

         sc.setColumnExpression(colExp);
         this.functionArguments.setElementAt(sc, 1);
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 2) {
         SelectColumn sc = new SelectColumn();
         Vector colExp = new Vector();
         colExp.add(this.functionArguments.get(0));
         new String();
         String isSign;
         if (((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0) instanceof TableColumn) {
            TableColumn tableColumn = (TableColumn)((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0);
            isSign = tableColumn.toString();
         } else {
            isSign = (String)((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0);
         }

         if ("-".equals(isSign)) {
            colExp.add("-");
            colExp.add(((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(1) + "  UNITS MONTH");
         } else {
            colExp.add("+");
            colExp.add(((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0) + "  UNITS MONTH");
         }

         sc.setColumnExpression(colExp);
         this.functionArguments.setElementAt(sc, 0);
         this.functionArguments.setSize(1);
      }

   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ADD_MONTHS");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

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
   }
}
