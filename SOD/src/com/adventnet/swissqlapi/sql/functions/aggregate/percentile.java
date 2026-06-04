package com.adventnet.swissqlapi.sql.functions.aggregate;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class percentile extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      SelectColumn percentileSC = new SelectColumn();
      Vector vc_percentileOut = new Vector();
      FunctionCalls fnCl_percentile = new FunctionCalls();
      TableColumn tbCl_percentile = new TableColumn();
      tbCl_percentile.setColumnName("PERCENTILE_CONT");
      fnCl_percentile.setFunctionName(tbCl_percentile);

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector vc;
      SelectColumn finalSC;
      if (arguments.elementAt(1) instanceof SelectColumn) {
         finalSC = (SelectColumn)arguments.elementAt(1);
         vc = finalSC.getColumnExpression();
         if (!(vc.elementAt(0) instanceof String)) {
            throw new ConvertException("Invalid Argument Value for Function PERCENTILE", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"PERCENTILE", "RANGE"});
         }

         String range_str = (String)vc.elementAt(0);
         range_str = range_str.replaceAll("'", "");
         this.validatePercentileRange(range_str, "PERCENTILE");
         SelectColumn sc_percentile = new SelectColumn();
         Vector vc_percentile = new Vector();
         int percentile = Integer.parseInt(range_str);
         String percentile_str = Double.toString((double)percentile / 100.0D);
         vc_percentile.addElement(percentile_str);
         sc_percentile.setColumnExpression(vc_percentile);
         arguments.set(1, sc_percentile);
      }

      fnCl_percentile.setFunctionArguments(arguments);
      vc_percentileOut.addElement(fnCl_percentile);
      percentileSC.setColumnExpression(vc_percentileOut);
      finalSC = new SelectColumn();
      vc = new Vector();
      Vector finalVCOut = new Vector();
      vc.addElement(percentileSC);
      vc.add("*");
      vc.add("1");
      finalSC.setColumnExpression(vc);
      finalVCOut.addElement(finalSC);
      this.functionName.setColumnName("");
      this.setFunctionArguments(finalVCOut);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      qry = "PERCENTILE_CONT(" + arguments.get(1).toString() + ") WITHIN GROUP (ORDER BY (" + arguments.get(0).toString() + "))";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      qry = "PERCENTILE_CONT(" + arguments.get(1).toString() + ") WITHIN GROUP (ORDER BY (" + arguments.get(0).toString() + "))";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "PERCENTILE_CONT(" + arguments.get(1).toString() + ") WITHIN GROUP (ORDER BY (" + arguments.get(0).toString() + ")) OVER()";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      qry = "PERCENTILE_CONT(" + arguments.get(1).toString() + ") WITHIN GROUP (ORDER BY (" + arguments.get(0).toString() + "))";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      qry = "PERCENTILE_CONT(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") OVER()";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      qry = "PERCENTILE_CONT(" + arguments.get(1).toString() + ") WITHIN GROUP (ORDER BY (" + arguments.get(0).toString() + "))";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      qry = "PERCENTILE_CONT(" + arguments.get(1).toString() + ") WITHIN GROUP (ORDER BY (" + arguments.get(0).toString() + "))";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "APPROX_PERCENTILE(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ")";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String qry = "";
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      qry = "PERCENTILE_CONT(" + arguments.get(1).toString() + ") WITHIN GROUP (ORDER BY (" + arguments.get(0).toString() + ")) OVER()";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }
}
