package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class square extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("POWER");
      Vector arguments = new Vector();
      Object obj = this.functionArguments.elementAt(0);
      if (obj instanceof SelectColumn) {
         arguments.addElement(((SelectColumn)obj).toOracleSelect(to_sqs, from_sqs));
      } else {
         arguments.addElement(obj);
      }

      arguments.addElement(new String("2"));
      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SQUARE");
      Vector arguments = new Vector();
      Object obj = this.functionArguments.elementAt(0);
      if (obj instanceof SelectColumn) {
         arguments.addElement(((SelectColumn)obj).toMSSQLServerSelect(to_sqs, from_sqs));
      } else {
         arguments.addElement(obj);
      }

      this.setFunctionArguments(arguments);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SQUARE");
      Vector arguments = new Vector();
      Object obj = this.functionArguments.elementAt(0);
      if (obj instanceof SelectColumn) {
         arguments.addElement(((SelectColumn)obj).toSybaseSelect(to_sqs, from_sqs));
      } else {
         arguments.addElement(obj);
      }

      this.setFunctionArguments(arguments);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("POWER");
      Vector arguments = new Vector();
      Object obj = this.functionArguments.elementAt(0);
      if (obj instanceof SelectColumn) {
         arguments.addElement(((SelectColumn)obj).toDB2Select(to_sqs, from_sqs));
      } else {
         arguments.addElement(obj);
      }

      arguments.addElement(new String("2"));
      this.setFunctionArguments(arguments);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("POW");
      Vector arguments = new Vector();
      Object obj = this.functionArguments.elementAt(0);
      if (obj instanceof SelectColumn) {
         arguments.addElement(((SelectColumn)obj).toPostgreSQLSelect(to_sqs, from_sqs));
      } else {
         arguments.addElement(obj);
      }

      arguments.addElement(new String("2"));
      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("POW");
      Vector arguments = new Vector();
      Object obj = this.functionArguments.elementAt(0);
      if (obj instanceof SelectColumn) {
         arguments.addElement(((SelectColumn)obj).toMySQLSelect(to_sqs, from_sqs));
      } else {
         arguments.addElement(obj);
      }

      arguments.addElement(new String("2"));
      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("POW");
      Vector arguments = new Vector();
      Object obj = this.functionArguments.elementAt(0);
      if (obj instanceof SelectColumn) {
         arguments.addElement(((SelectColumn)obj).toANSISelect(to_sqs, from_sqs));
      } else {
         arguments.addElement(obj);
      }

      arguments.addElement(new String("2"));
      this.setFunctionArguments(arguments);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("POW");
      Vector arguments = new Vector();
      Object obj = this.functionArguments.elementAt(0);
      if (obj instanceof SelectColumn) {
         arguments.addElement(((SelectColumn)obj).toTeradataSelect(to_sqs, from_sqs));
      } else {
         arguments.addElement(obj);
      }

      arguments.addElement(new String("2"));
      this.setFunctionArguments(arguments);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("POW");
      Vector arguments = new Vector();
      Object obj = this.functionArguments.elementAt(0);
      if (obj instanceof SelectColumn) {
         arguments.addElement(((SelectColumn)obj).toInformixSelect(to_sqs, from_sqs));
      } else {
         arguments.addElement(obj);
      }

      arguments.addElement(new String("2"));
      this.setFunctionArguments(arguments);
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SQUARE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            this.functionName.setColumnName("");
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            sc.toTimesTenSelect(to_sqs, from_sqs);
            Vector colExpr = sc.getColumnExpression();
            if (colExpr.size() != 1 || !(colExpr.get(0) instanceof SelectColumn)) {
               colExpr.add(0, "(");
               colExpr.add(")");
            }

            int n = colExpr.size();
            colExpr.add("*");

            for(int i = 0; i < n; ++i) {
               colExpr.add(colExpr.get(i));
            }

            arguments.addElement(sc);
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("POW");
      Vector arguments = new Vector();
      Object obj = this.functionArguments.elementAt(0);
      if (obj instanceof SelectColumn) {
         arguments.addElement(((SelectColumn)obj).toNetezzaSelect(to_sqs, from_sqs));
      } else {
         arguments.addElement(obj);
      }

      arguments.addElement(new String("2"));
      this.setFunctionArguments(arguments);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("POWER");
      Vector arguments = new Vector();
      Object obj = this.functionArguments.elementAt(0);
      if (obj instanceof SelectColumn) {
         arguments.addElement(((SelectColumn)obj).toVectorWiseSelect(to_sqs, from_sqs));
      } else {
         arguments.addElement(obj);
      }

      arguments.addElement(new String("2"));
      this.setFunctionArguments(arguments);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("POW");
      Vector arguments = new Vector();
      Object obj = this.functionArguments.elementAt(0);
      if (obj instanceof SelectColumn) {
         arguments.addElement(((SelectColumn)obj).toBigQuerySelect(to_sqs, from_sqs));
      } else {
         arguments.addElement(obj);
      }

      arguments.addElement(new String("2"));
      this.setFunctionArguments(arguments);
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector newArguments = new Vector();
      SelectColumn newArgument = new SelectColumn();
      Vector colExp = new Vector();
      colExp.addElement("(");
      colExp.addElement(arguments.get(0));
      colExp.addElement(") ^ (");
      colExp.addElement("2");
      colExp.addElement(")");
      newArgument.setColumnExpression(colExp);
      newArguments.add(newArgument);
      this.setFunctionArguments(newArguments);
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("POWER");
      Vector arguments = new Vector();
      Object obj = this.functionArguments.elementAt(0);
      if (obj instanceof SelectColumn) {
         arguments.addElement(((SelectColumn)obj).toMsAccessJdbcSelect(to_sqs, from_sqs));
      } else {
         arguments.addElement(obj);
      }

      arguments.addElement(new String("2"));
      this.setFunctionArguments(arguments);
   }
}
