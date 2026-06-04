package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class quarter extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName().toUpperCase();
      String fiscalStartMonth = "";
      Vector arguments = new Vector();
      Vector vector = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vector.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      SelectColumn sc_Quarter;
      if (vector.size() == 2 && vector.elementAt(1) instanceof SelectColumn) {
         sc_Quarter = (SelectColumn)vector.elementAt(1);
         Vector vc_FiscalStartMonth = sc_Quarter.getColumnExpression();
         if (!(vc_FiscalStartMonth.elementAt(0) instanceof String)) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{fnStr, "FISCAL_START_MONTH"});
         }

         fiscalStartMonth = (String)vc_FiscalStartMonth.elementAt(0);
         if (fiscalStartMonth.equalsIgnoreCase("null")) {
            fiscalStartMonth = "1";
         }

         fiscalStartMonth = fiscalStartMonth.replaceAll("'", "");
         this.validateFiscalStartMonth(fiscalStartMonth, this.functionName.getColumnName().toUpperCase());
      }

      if (!fnStr.equalsIgnoreCase("QUARTERNAME") && !fnStr.equalsIgnoreCase("QUARTER_NAME")) {
         if (fnStr.equalsIgnoreCase("QUARTERNUM") || fnStr.equalsIgnoreCase("QUARTER") || fnStr.equalsIgnoreCase("QUARTER_NUM")) {
            arguments.addElement(vector.get(0));
            if (this.functionArguments.size() == 1) {
               this.functionName.setColumnName("QUARTER");
            } else if (this.functionArguments.size() == 2) {
               if (fiscalStartMonth.equalsIgnoreCase("1")) {
                  this.functionName.setColumnName("QUARTER");
               } else {
                  this.functionName.setColumnName("ZR_FQUARTERDT");
                  arguments.addElement(this.fiscalQuarterNumber(vector));
               }
            }
         }
      } else {
         this.functionName.setColumnName("CONCAT");
         arguments.addElement("'Q'");
         sc_Quarter = new SelectColumn();
         FunctionCalls fn_Quarter = new FunctionCalls();
         TableColumn tb_Quarter = new TableColumn();
         Vector vc_QuarterIn = new Vector();
         vc_QuarterIn.addElement(vector.get(0));
         if (this.functionArguments.size() == 1) {
            tb_Quarter.setColumnName("QUARTER");
         } else if (this.functionArguments.size() == 2) {
            if (fiscalStartMonth.equalsIgnoreCase("1")) {
               tb_Quarter.setColumnName("QUARTER");
            } else {
               tb_Quarter.setColumnName("ZR_FQUARTERDT");
               vc_QuarterIn.addElement(this.fiscalQuarterNumber(vector));
            }
         }

         fn_Quarter.setFunctionName(tb_Quarter);
         Vector vc_QuarterOut = new Vector();
         fn_Quarter.setFunctionArguments(vc_QuarterIn);
         vc_QuarterOut.addElement(fn_Quarter);
         sc_Quarter.setColumnExpression(vc_QuarterOut);
         arguments.addElement(sc_Quarter);
      }

      this.setFunctionArguments(arguments);
   }

   public SelectColumn fiscalQuarterNumber(Vector vector) {
      SelectColumn sc_addMonth = new SelectColumn();
      Vector vc_addMonth = new Vector();
      vc_addMonth.addElement("12");
      vc_addMonth.addElement("-");
      vc_addMonth.addElement(vector.get(1));
      vc_addMonth.addElement("+");
      vc_addMonth.addElement("1");
      sc_addMonth.setOpenBrace("(");
      sc_addMonth.setCloseBrace(")");
      sc_addMonth.setColumnExpression(vc_addMonth);
      return sc_addMonth;
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector vector = new Vector();
      Boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            vector.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = vector.get(0).toString();
      String query;
      if (isdenodo) {
         query = "GETQUARTER(" + StringFunctions.handleLiteralStringDateForDenodo(dateString) + ")";
      } else {
         dateString = StringFunctions.handleLiteralStringDateForOracle(dateString);
         query = " TO_NUMBER(TO_CHAR(" + dateString + ", 'Q'))";
      }

      this.functionName.setColumnName(query);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector vector = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            vector.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String query = " EXTRACT(QUARTER FROM CAST(" + vector.get(0).toString() + " AS TIMESTAMP) )";
      this.functionName.setColumnName(query);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }
}
