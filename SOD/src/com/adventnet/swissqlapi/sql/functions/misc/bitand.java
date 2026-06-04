package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class bitand extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("BITAND");
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
      if (arguments.size() == 2) {
         SelectColumn bitAndSelectColumn = new SelectColumn();
         Vector bitAndExpression = new Vector();
         bitAndExpression.add(arguments.get(0));
         bitAndExpression.add("&");
         bitAndExpression.add(arguments.get(1));
         bitAndSelectColumn.setColumnExpression(bitAndExpression);
         Vector newFunctionArgument = new Vector();
         newFunctionArgument.add(bitAndSelectColumn);
         this.setFunctionArguments(newFunctionArgument);
      }

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
      if (arguments.size() == 2) {
         SelectColumn bitAndSelectColumn = new SelectColumn();
         Vector bitAndExpression = new Vector();
         bitAndExpression.add(arguments.get(0));
         bitAndExpression.add("&");
         bitAndExpression.add(arguments.get(1));
         bitAndSelectColumn.setColumnExpression(bitAndExpression);
         Vector newFunctionArgument = new Vector();
         newFunctionArgument.add(bitAndSelectColumn);
         this.setFunctionArguments(newFunctionArgument);
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("BITAND");
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
      this.functionName.setColumnName("");
      Vector arguments = new Vector();
      SelectColumn sc_BitAnd = new SelectColumn();
      Vector vc_BitAnd = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vc_BitAnd.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            vc_BitAnd.addElement(this.functionArguments.elementAt(i_count));
         }

         if (i_count != this.functionArguments.size() - 1) {
            vc_BitAnd.add("&");
         }
      }

      sc_BitAnd.setColumnExpression(vc_BitAnd);
      arguments.add(sc_BitAnd);
      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();
      SelectColumn sc_BitAnd = new SelectColumn();
      Vector vc_BitAnd = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vc_BitAnd.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vc_BitAnd.addElement(this.functionArguments.elementAt(i_count));
         }

         if (i_count != this.functionArguments.size() - 1) {
            vc_BitAnd.add("&");
         }
      }

      sc_BitAnd.setColumnExpression(vc_BitAnd);
      arguments.add(sc_BitAnd);
      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("BITAND");
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
      this.functionName.setColumnName("BITAND");
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
      this.functionName.setColumnName("BITAND");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTimesTenSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (arguments.size() == 2) {
         SelectColumn bitAndSelectColumn = new SelectColumn();
         Vector bitAndExpression = new Vector();
         bitAndExpression.add(arguments.get(0));
         bitAndExpression.add("&");
         bitAndExpression.add(arguments.get(1));
         bitAndSelectColumn.setColumnExpression(bitAndExpression);
         Vector newFunctionArgument = new Vector();
         newFunctionArgument.add(bitAndSelectColumn);
         this.setFunctionArguments(newFunctionArgument);
      }

   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (arguments.size() == 2) {
         SelectColumn bitAndSelectColumn = new SelectColumn();
         Vector bitAndExpression = new Vector();
         bitAndExpression.add(arguments.get(0));
         bitAndExpression.add("&");
         bitAndExpression.add(arguments.get(1));
         bitAndSelectColumn.setColumnExpression(bitAndExpression);
         Vector newFunctionArgument = new Vector();
         newFunctionArgument.add(bitAndSelectColumn);
         this.setFunctionArguments(newFunctionArgument);
      }

   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " is not supported in VectorWise\n");
   }
}
