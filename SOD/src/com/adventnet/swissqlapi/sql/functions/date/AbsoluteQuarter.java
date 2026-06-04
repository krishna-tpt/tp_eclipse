package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class AbsoluteQuarter extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      int argLength = this.functionArguments.size();

      for(int i_count = 0; i_count < argLength; ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0) instanceof String) {
               String dateString = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0).toString();
               dateString = "CAST(" + dateString + " AS TIMESTAMP)";
               arguments.addElement(dateString);
            } else {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            }
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Object date = arguments.get(0);
      SelectColumn absQSC = new SelectColumn();
      Vector absQSCColExp = new Vector();
      absQSCColExp.addElement("(");
      absQSCColExp.addElement("(");
      absQSCColExp.addElement("'Q'");
      absQSCColExp.addElement("||");
      SelectColumn dateSC = new SelectColumn();
      Vector dateScVector = new Vector();
      dateScVector.addElement(date);
      dateSC.setColumnExpression(dateScVector);
      SelectColumn castFSC = new SelectColumn();
      Vector castFSCColExp = new Vector();
      FunctionCalls castFC = new FunctionCalls();
      castFC.getFunctionName().setColumnName("cast");
      Vector castFCArgs = new Vector();
      SelectColumn extractFSC = new SelectColumn();
      Vector extractFSCColExp = new Vector();
      FunctionCalls extractFC = new FunctionCalls();
      extractFC.getFunctionName().setColumnName("EXTRACT");
      extractFC.setFromInTrim("FROM");
      extractFC.setTrailingString("QUARTER");
      Vector extractFCArgs = new Vector();
      extractFCArgs.addElement(dateSC);
      extractFC.setFunctionArguments(extractFCArgs);
      extractFSCColExp.addElement(extractFC);
      extractFSC.setColumnExpression(extractFSCColExp);
      castFC.setAsDatatype("as");
      castFCArgs.addElement(extractFSC);
      castFCArgs.addElement("int");
      castFC.setFunctionArguments(castFCArgs);
      castFSCColExp.addElement(castFC);
      castFSC.setColumnExpression(castFSCColExp);
      absQSCColExp.addElement(castFSC);
      absQSCColExp.addElement(")");
      absQSCColExp.addElement("||");
      absQSCColExp.addElement("','");
      absQSCColExp.addElement(")");
      absQSCColExp.addElement("||");
      SelectColumn cast2FSC = new SelectColumn();
      Vector cast2FSCColExp = new Vector();
      FunctionCalls cast2FC = new FunctionCalls();
      cast2FC.getFunctionName().setColumnName("cast");
      Vector cast2FCArgs = new Vector();
      SelectColumn extract2FSC = new SelectColumn();
      Vector extract2FSCColExp = new Vector();
      FunctionCalls extract2FC = new FunctionCalls();
      extract2FC.getFunctionName().setColumnName("extract");
      extract2FC.setFromInTrim("from");
      extract2FC.setTrailingString("year");
      Vector extract2FCArgs = new Vector();
      extract2FCArgs.addElement(dateSC);
      extract2FC.setFunctionArguments(extract2FCArgs);
      extract2FSCColExp.addElement(extract2FC);
      extract2FSC.setColumnExpression(extract2FSCColExp);
      cast2FC.setAsDatatype("as");
      cast2FCArgs.addElement(extract2FSC);
      cast2FCArgs.addElement("int");
      cast2FC.setFunctionArguments(cast2FCArgs);
      cast2FSCColExp.addElement(cast2FC);
      cast2FSC.setColumnExpression(cast2FSCColExp);
      absQSCColExp.addElement(cast2FSC);
      absQSC.setColumnExpression(absQSCColExp);
      this.functionName.setColumnName("");
      Vector fnArgs = new Vector();
      fnArgs.addElement(absQSC);
      this.setFunctionArguments(fnArgs);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int argLength = this.functionArguments.size();
      Vector vector1 = new Vector();
      Vector vector2 = new Vector();

      for(int i_count = 0; i_count < argLength; ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector1.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            vector2.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vector1.addElement(this.functionArguments.elementAt(i_count));
            vector2.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      SelectColumn concatFSC = new SelectColumn();
      Vector concatFSCColExp = new Vector();
      FunctionCalls concatFC = new FunctionCalls();
      concatFC.getFunctionName().setColumnName("concat");
      Vector concatFCArgs = new Vector();
      concatFCArgs.addElement("'Q'");
      if (argLength > 1) {
         String fiscal_start_month = "";
         if (vector1.elementAt(1) instanceof SelectColumn) {
            SelectColumn fiscalSC = (SelectColumn)vector1.elementAt(1);
            Vector fiscalVC = fiscalSC.getColumnExpression();
            if (!(fiscalVC.elementAt(0) instanceof String)) {
               throw new ConvertException("Invalid Argument Value for Function ABSQUARTER", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"ABSQUARTER", "FISCAL_START_MONTH"});
            }

            fiscal_start_month = (String)fiscalVC.elementAt(0);
            if (fiscal_start_month.equalsIgnoreCase("null")) {
               fiscal_start_month = "1";
            }

            fiscal_start_month = fiscal_start_month.replaceAll("'", "");
            this.validateFiscalStartMonth(fiscal_start_month, this.functionName.getColumnName().toUpperCase());
            int addmonth = Integer.parseInt(fiscal_start_month);
            addmonth = 12 - addmonth + 1;
            String addFiscalMonth = Integer.toString(addmonth);
            SelectColumn zr_QuarterFSC = new SelectColumn();
            Vector zr_QuarterFSCColExp = new Vector();
            FunctionCalls zr_QuarterFC = new FunctionCalls();
            zr_QuarterFC.getFunctionName().setColumnName("ZR_fQuarterDt");
            Vector zr_QuarterFCArgs = new Vector();
            zr_QuarterFCArgs.addElement(vector1.get(0));
            zr_QuarterFCArgs.addElement(addFiscalMonth);
            zr_QuarterFC.setFunctionArguments(zr_QuarterFCArgs);
            zr_QuarterFSCColExp.addElement(zr_QuarterFC);
            zr_QuarterFSC.setColumnExpression(zr_QuarterFSCColExp);
            SelectColumn zr_YearFSC = new SelectColumn();
            Vector zr_YearFSCColExp = new Vector();
            FunctionCalls zr_YearFC = new FunctionCalls();
            zr_YearFC.getFunctionName().setColumnName("ZR_fyearDt");
            Vector zr_YearFCArgs = new Vector();
            zr_YearFCArgs.addElement(vector2.get(0));
            zr_YearFCArgs.addElement(addFiscalMonth);
            zr_YearFC.setFunctionArguments(zr_YearFCArgs);
            zr_YearFSCColExp.addElement(zr_YearFC);
            zr_YearFSC.setColumnExpression(zr_YearFSCColExp);
            concatFCArgs.addElement(zr_QuarterFSC);
            concatFCArgs.addElement("', FY '");
            concatFCArgs.addElement(zr_YearFSC);
         }
      } else {
         SelectColumn yearFSC = new SelectColumn();
         Vector yearFSCColExp = new Vector();
         FunctionCalls yearFC = new FunctionCalls();
         yearFC.getFunctionName().setColumnName("year");
         Vector yearFCArgs = new Vector();
         yearFCArgs.addElement(vector1.get(0));
         yearFC.setFunctionArguments(yearFCArgs);
         yearFSCColExp.addElement(yearFC);
         yearFSC.setColumnExpression(yearFSCColExp);
         SelectColumn quarterFSC = new SelectColumn();
         Vector quarterFSCColExp = new Vector();
         FunctionCalls quarterFC = new FunctionCalls();
         quarterFC.getFunctionName().setColumnName("quarter");
         Vector quarterFCArgs = new Vector();
         quarterFCArgs.addElement(vector2.get(0));
         quarterFC.setFunctionArguments(quarterFCArgs);
         quarterFSCColExp.addElement(quarterFC);
         quarterFSC.setColumnExpression(quarterFSCColExp);
         concatFCArgs.addElement(quarterFSC);
         concatFCArgs.addElement("', '");
         concatFCArgs.addElement(yearFSC);
      }

      concatFC.setFunctionArguments(concatFCArgs);
      concatFSCColExp.addElement(concatFC);
      concatFSC.setColumnExpression(concatFSCColExp);
      this.functionName.setColumnName("");
      Vector fnArgs = new Vector();
      fnArgs.addElement(concatFSC);
      this.setFunctionArguments(fnArgs);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int argLength = this.functionArguments.size();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < argLength; ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String date = arguments.get(0).toString();
      arguments = new Vector();
      arguments.addElement("'Q'");
      arguments.addElement("quarter(" + date + ")");
      arguments.addElement("', '");
      arguments.addElement("YEAR(" + date + ")");
      this.functionName.setColumnName("CONCAT");
      this.setFunctionArguments(arguments);
   }
}
