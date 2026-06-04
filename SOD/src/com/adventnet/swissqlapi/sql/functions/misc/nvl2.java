package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class nvl2 extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("NVL2");
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
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 3) {
         StringBuffer argument = new StringBuffer();
         argument.append("CASE WHEN ");
         argument.append(this.functionArguments.get(0).toString() + " ");
         argument.append(" IS NULL THEN ");
         argument.append(this.functionArguments.get(2).toString() + " ");
         argument.append(" ELSE ");
         argument.append(this.functionArguments.get(1).toString() + " ");
         argument.append("END");
         Vector arg = new Vector();
         arg.addElement(argument.toString());
         this.functionArguments = arg;
      }

   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("NVL2");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 3) {
         StringBuffer argument = new StringBuffer();
         argument.append("CASE WHEN ");
         argument.append(this.functionArguments.get(0).toString() + " ");
         argument.append(" IS NULL THEN ");
         argument.append(this.functionArguments.get(2).toString() + " ");
         argument.append(" ELSE ");
         argument.append(this.functionArguments.get(1).toString() + " ");
         argument.append("END");
         Vector arg = new Vector();
         arg.addElement(argument.toString());
         this.functionArguments = arg;
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
      if (this.functionArguments.size() == 3) {
         StringBuffer argument = new StringBuffer();
         argument.append("CASE WHEN ");
         argument.append(this.functionArguments.get(0).toString() + " ");
         argument.append(" IS NULL THEN ");
         argument.append(this.functionArguments.get(2).toString() + " ");
         argument.append(" ELSE ");
         argument.append(this.functionArguments.get(1).toString() + " ");
         argument.append("END");
         Vector arg = new Vector();
         arg.addElement(argument.toString());
         this.functionArguments = arg;
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 3) {
         StringBuffer argument = new StringBuffer();
         argument.append("CASE WHEN ");
         argument.append(this.functionArguments.get(0).toString() + " ");
         argument.append(" IS NULL THEN ");
         argument.append(this.functionArguments.get(2).toString() + " ");
         argument.append(" ELSE ");
         argument.append(this.functionArguments.get(1).toString() + " ");
         argument.append("END");
         Vector arg = new Vector();
         arg.addElement(argument.toString());
         this.functionArguments = arg;
      }

   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 3) {
         StringBuffer argument = new StringBuffer();
         argument.append("CASE WHEN ");
         argument.append(this.functionArguments.get(0).toString() + " ");
         argument.append(" IS NULL THEN ");
         argument.append(this.functionArguments.get(2).toString() + " ");
         argument.append(" ELSE ");
         argument.append(this.functionArguments.get(1).toString() + " ");
         argument.append("END");
         Vector arg = new Vector();
         arg.addElement(argument.toString());
         this.functionArguments = arg;
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 3) {
         StringBuffer argument = new StringBuffer();
         argument.append("CASE WHEN ");
         argument.append(this.functionArguments.get(0).toString() + " ");
         argument.append(" IS NULL THEN ");
         argument.append(this.functionArguments.get(2).toString() + " ");
         argument.append(" ELSE ");
         argument.append(this.functionArguments.get(1).toString() + " ");
         argument.append("END");
         Vector arg = new Vector();
         arg.addElement(argument.toString());
         this.functionArguments = arg;
      }

   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 3) {
         StringBuffer argument = new StringBuffer();
         argument.append("CASE WHEN ");
         argument.append(this.functionArguments.get(0).toString() + " ");
         argument.append(" IS NULL THEN ");
         argument.append(this.functionArguments.get(2).toString() + " ");
         argument.append(" ELSE ");
         argument.append(this.functionArguments.get(1).toString() + " ");
         argument.append("END");
         Vector arg = new Vector();
         arg.addElement(argument.toString());
         this.functionArguments = arg;
      }

   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 3) {
         StringBuffer argument = new StringBuffer();
         argument.append("DECODE (");
         argument.append(this.functionArguments.get(0).toString() + " ");
         argument.append(", NULL, ");
         argument.append(this.functionArguments.get(2).toString() + " ");
         argument.append(", ");
         argument.append(this.functionArguments.get(1).toString() + " ");
         argument.append(")");
         Vector arg = new Vector();
         arg.addElement(argument.toString());
         this.functionArguments = arg;
      }

   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("NVL2");
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

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 3) {
         StringBuffer argument = new StringBuffer();
         argument.append("CASE WHEN ");
         argument.append(this.functionArguments.get(0).toString() + " ");
         argument.append(" IS NULL THEN ");
         argument.append(this.functionArguments.get(2).toString() + " ");
         argument.append(" ELSE ");
         argument.append(this.functionArguments.get(1).toString() + " ");
         argument.append("END");
         Vector arg = new Vector();
         arg.addElement(argument.toString());
         this.functionArguments = arg;
      }

   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 3) {
         StringBuffer argument = new StringBuffer();
         argument.append("CASE WHEN ");
         argument.append(this.functionArguments.get(0).toString() + " ");
         argument.append(" IS NULL THEN ");
         argument.append(this.functionArguments.get(2).toString() + " ");
         argument.append(" ELSE ");
         argument.append(this.functionArguments.get(1).toString() + " ");
         argument.append("END");
         Vector arg = new Vector();
         arg.addElement(argument.toString());
         this.functionArguments = arg;
      }

   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 3) {
         StringBuffer argument = new StringBuffer();
         argument.append("CASE WHEN ");
         argument.append(this.functionArguments.get(0).toString() + " ");
         argument.append(" IS NULL THEN ");
         argument.append(this.functionArguments.get(2).toString() + " ");
         argument.append(" ELSE ");
         argument.append(this.functionArguments.get(1).toString() + " ");
         argument.append("END");
         Vector arg = new Vector();
         arg.addElement(argument.toString());
         this.functionArguments = arg;
      }

   }
}
