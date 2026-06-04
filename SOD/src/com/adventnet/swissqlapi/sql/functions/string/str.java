package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class str extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUBSTR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      FunctionCalls round;
      FunctionCalls lpad;
      TableColumn innerFunction1;
      TableColumn innerFunction1;
      TableColumn innerFunction2;
      Vector tocharArg;
      Vector ceilArg;
      Vector tocharArg;
      Object obj;
      FunctionCalls tochar;
      if (this.functionArguments.size() == 2) {
         tochar = new FunctionCalls();
         round = new FunctionCalls();
         lpad = new FunctionCalls();
         innerFunction1 = new TableColumn();
         innerFunction1 = new TableColumn();
         innerFunction2 = new TableColumn();
         innerFunction1.setOwnerName(this.functionName.getOwnerName());
         innerFunction1.setTableName(this.functionName.getTableName());
         innerFunction1.setColumnName("ROUND");
         innerFunction1.setOwnerName(this.functionName.getOwnerName());
         innerFunction1.setTableName(this.functionName.getTableName());
         innerFunction1.setColumnName("TO_CHAR");
         innerFunction2.setOwnerName(this.functionName.getOwnerName());
         innerFunction2.setTableName(this.functionName.getTableName());
         innerFunction2.setColumnName("LPAD");
         tocharArg = new Vector();
         ceilArg = new Vector();
         tocharArg = new Vector();
         round.setFunctionName(innerFunction1);
         tochar.setFunctionName(innerFunction1);
         lpad.setFunctionName(innerFunction2);
         ceilArg.add(this.functionArguments.get(0));
         round.setFunctionArguments(ceilArg);
         tocharArg.add(round);
         tochar.setFunctionArguments(tocharArg);
         tocharArg.add(tochar);
         tocharArg.add(this.functionArguments.get(1));
         lpad.setFunctionArguments(tocharArg);
         this.functionArguments.setElementAt(lpad, 0);
         obj = this.functionArguments.get(1);
         this.functionArguments.setElementAt("1", 1);
         this.functionArguments.add(obj);
      }

      if (this.functionArguments.size() == 3) {
         tochar = new FunctionCalls();
         round = new FunctionCalls();
         lpad = new FunctionCalls();
         innerFunction1 = new TableColumn();
         innerFunction1 = new TableColumn();
         innerFunction2 = new TableColumn();
         innerFunction1.setOwnerName(this.functionName.getOwnerName());
         innerFunction1.setTableName(this.functionName.getTableName());
         innerFunction1.setColumnName("ROUND");
         innerFunction1.setOwnerName(this.functionName.getOwnerName());
         innerFunction1.setTableName(this.functionName.getTableName());
         innerFunction1.setColumnName("TO_CHAR");
         innerFunction2.setOwnerName(this.functionName.getOwnerName());
         innerFunction2.setTableName(this.functionName.getTableName());
         innerFunction2.setColumnName("LPAD");
         tocharArg = new Vector();
         ceilArg = new Vector();
         tocharArg = new Vector();
         round.setFunctionName(innerFunction1);
         tochar.setFunctionName(innerFunction1);
         lpad.setFunctionName(innerFunction2);
         ceilArg.add(this.functionArguments.get(0));
         ceilArg.add(this.functionArguments.remove(2));
         round.setFunctionArguments(ceilArg);
         tocharArg.add(round);
         tochar.setFunctionArguments(tocharArg);
         tocharArg.add(tochar);
         tocharArg.add(this.functionArguments.get(1));
         lpad.setFunctionArguments(tocharArg);
         this.functionArguments.setElementAt(lpad, 0);
         obj = this.functionArguments.get(1);
         this.functionArguments.setElementAt("1", 1);
         this.functionArguments.add(obj);
      }

      if (this.functionArguments.size() == 1) {
         tochar = new FunctionCalls();
         round = new FunctionCalls();
         lpad = new FunctionCalls();
         FunctionCalls len = new FunctionCalls();
         innerFunction1 = new TableColumn();
         innerFunction2 = new TableColumn();
         TableColumn innerFunction3 = new TableColumn();
         TableColumn lenFunction3 = new TableColumn();
         innerFunction1.setOwnerName(this.functionName.getOwnerName());
         innerFunction1.setTableName(this.functionName.getTableName());
         innerFunction1.setColumnName("ROUND");
         innerFunction2.setOwnerName(this.functionName.getOwnerName());
         innerFunction2.setTableName(this.functionName.getTableName());
         innerFunction2.setColumnName("TO_CHAR");
         innerFunction3.setOwnerName(this.functionName.getOwnerName());
         innerFunction3.setTableName(this.functionName.getTableName());
         innerFunction3.setColumnName("LPAD");
         lenFunction3.setOwnerName(this.functionName.getOwnerName());
         lenFunction3.setTableName(this.functionName.getTableName());
         lenFunction3.setColumnName("LENGTH");
         tocharArg = new Vector();
         Vector roundArg = new Vector();
         Vector lpadArg = new Vector();
         Vector lenArg = new Vector();
         round.setFunctionName(innerFunction1);
         tochar.setFunctionName(innerFunction2);
         lpad.setFunctionName(innerFunction3);
         len.setFunctionName(lenFunction3);
         roundArg.add(this.functionArguments.get(0));
         round.setFunctionArguments(roundArg);
         tocharArg.add(round);
         tochar.setFunctionArguments(tocharArg);
         lpadArg.add(tochar);
         lpadArg.add(new String("10"));
         lpad.setFunctionArguments(lpadArg);
         lenArg.add(this.functionArguments.get(0));
         len.setFunctionArguments(lenArg);
         this.functionArguments.setElementAt(lpad, 0);
         this.functionArguments.add(new String("1"));
         this.functionArguments.add(new String("10"));
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("STR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("STR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("STR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("STR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("STR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("STR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("STR");
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

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUBSTR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      FunctionCalls ceil;
      FunctionCalls lpad;
      TableColumn innerFunction1;
      TableColumn innerFunction2;
      Vector tocharArg;
      FunctionCalls tochar;
      if (this.functionArguments.size() == 2) {
         tochar = new FunctionCalls();
         ceil = new FunctionCalls();
         lpad = new FunctionCalls();
         TableColumn innerFunction1 = new TableColumn();
         innerFunction1 = new TableColumn();
         innerFunction2 = new TableColumn();
         innerFunction1.setOwnerName(this.functionName.getOwnerName());
         innerFunction1.setTableName(this.functionName.getTableName());
         innerFunction1.setColumnName("ROUND");
         innerFunction1.setOwnerName(this.functionName.getOwnerName());
         innerFunction1.setTableName(this.functionName.getTableName());
         innerFunction1.setColumnName("TO_CHAR");
         innerFunction2.setOwnerName(this.functionName.getOwnerName());
         innerFunction2.setTableName(this.functionName.getTableName());
         innerFunction2.setColumnName("LPAD");
         Vector tocharArg = new Vector();
         Vector ceilArg = new Vector();
         tocharArg = new Vector();
         ceil.setFunctionName(innerFunction1);
         tochar.setFunctionName(innerFunction1);
         lpad.setFunctionName(innerFunction2);
         ceilArg.add(this.functionArguments.get(0));
         ceil.setFunctionArguments(ceilArg);
         tocharArg.add(ceil);
         tochar.setFunctionArguments(tocharArg);
         tocharArg.add(tochar);
         tocharArg.add(this.functionArguments.get(1));
         lpad.setFunctionArguments(tocharArg);
         this.functionArguments.setElementAt(lpad, 0);
         Object obj = this.functionArguments.get(1);
         this.functionArguments.setElementAt("1", 1);
         this.functionArguments.add(obj);
      }

      if (this.functionArguments.size() == 1) {
         tochar = new FunctionCalls();
         ceil = new FunctionCalls();
         lpad = new FunctionCalls();
         FunctionCalls len = new FunctionCalls();
         innerFunction1 = new TableColumn();
         innerFunction2 = new TableColumn();
         TableColumn innerFunction3 = new TableColumn();
         TableColumn lenFunction3 = new TableColumn();
         innerFunction1.setOwnerName(this.functionName.getOwnerName());
         innerFunction1.setTableName(this.functionName.getTableName());
         innerFunction1.setColumnName("ROUND");
         innerFunction2.setOwnerName(this.functionName.getOwnerName());
         innerFunction2.setTableName(this.functionName.getTableName());
         innerFunction2.setColumnName("TO_CHAR");
         innerFunction3.setOwnerName(this.functionName.getOwnerName());
         innerFunction3.setTableName(this.functionName.getTableName());
         innerFunction3.setColumnName("LPAD");
         lenFunction3.setOwnerName(this.functionName.getOwnerName());
         lenFunction3.setTableName(this.functionName.getTableName());
         lenFunction3.setColumnName("LENGTH");
         tocharArg = new Vector();
         Vector ceilArg = new Vector();
         Vector lpadArg = new Vector();
         Vector lenArg = new Vector();
         ceil.setFunctionName(innerFunction1);
         tochar.setFunctionName(innerFunction2);
         lpad.setFunctionName(innerFunction3);
         len.setFunctionName(lenFunction3);
         ceilArg.add(this.functionArguments.get(0));
         ceil.setFunctionArguments(ceilArg);
         tocharArg.add(ceil);
         tochar.setFunctionArguments(tocharArg);
         lpadArg.add(tochar);
         lpadArg.add(new String("10"));
         lpad.setFunctionArguments(lpadArg);
         lenArg.add(this.functionArguments.get(0));
         len.setFunctionArguments(lenArg);
         this.functionArguments.setElementAt(lpad, 0);
         this.functionArguments.add(new String("1"));
         this.functionArguments.add(new String("10"));
      }

   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nThe built-in function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("STR");
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

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("STR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUBSTR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }
}
