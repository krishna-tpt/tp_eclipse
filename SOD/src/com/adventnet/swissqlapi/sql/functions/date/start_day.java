package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class start_day extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE");
      Vector arguments = new Vector();
      SelectColumn sc_dateSub = new SelectColumn();
      FunctionCalls fnCl_dateSub = new FunctionCalls();
      TableColumn tbCl_dateSub = new TableColumn();
      tbCl_dateSub.setColumnName("DATE_SUB");
      fnCl_dateSub.setFunctionName(tbCl_dateSub);
      Vector vc_dateSubIn = new Vector();
      Vector vc_dateSubOut = new Vector();
      Vector vector = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vector.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String unit = "";
      SelectColumn sc_interval = new SelectColumn();
      Vector vc_interval = new Vector();
      TableColumn tbCl_interval = new TableColumn();
      tbCl_interval.setColumnName("interval");
      vc_interval.addElement(tbCl_interval);
      SelectColumn sc_value;
      Vector vc_value;
      TableColumn tb_dateSubQtr;
      if (vector.elementAt(0) instanceof SelectColumn) {
         sc_value = (SelectColumn)vector.elementAt(0);
         vc_value = sc_value.getColumnExpression();
         if (vc_value.elementAt(0) instanceof TableColumn) {
            tb_dateSubQtr = (TableColumn)vc_value.elementAt(0);
            unit = tb_dateSubQtr.getColumnName();
            unit = unit.replaceAll("'", "");
         }
      }

      if (!unit.equalsIgnoreCase("WEEK") && !unit.equalsIgnoreCase("WEEK_MONDAY")) {
         if (unit.equalsIgnoreCase("WEEK_SUNDAY")) {
            vc_dateSubIn.addElement(vector.get(1));
            sc_value = new SelectColumn();
            vc_value = new Vector();
            vc_value.addElement(this.units(to_sqs, from_sqs, "dayofweek"));
            vc_value.add("-");
            vc_value.add("1");
            sc_value.setOpenBrace("(");
            sc_value.setCloseBrace(")");
            sc_value.setColumnExpression(vc_value);
            vc_interval.addElement(sc_value);
            vc_interval.add(" DAY");
            sc_interval.setColumnExpression(vc_interval);
         } else if (unit.equalsIgnoreCase("MONTH")) {
            vc_dateSubIn.addElement(vector.get(1));
            sc_value = new SelectColumn();
            vc_value = new Vector();
            vc_value.addElement(this.units(to_sqs, from_sqs, "dayofmonth"));
            vc_value.add("-");
            vc_value.add("1");
            sc_value.setOpenBrace("(");
            sc_value.setCloseBrace(")");
            sc_value.setColumnExpression(vc_value);
            vc_interval.addElement(sc_value);
            vc_interval.add(" DAY");
            sc_interval.setColumnExpression(vc_interval);
         } else if (unit.equalsIgnoreCase("QUARTER")) {
            sc_value = new SelectColumn();
            FunctionCalls fnCall_dateSubQtr = new FunctionCalls();
            tb_dateSubQtr = new TableColumn();
            tb_dateSubQtr.setColumnName("DATE_SUB");
            fnCall_dateSubQtr.setFunctionName(tb_dateSubQtr);
            Vector vc_dateSubQtrIn = new Vector();
            Vector vc_dateSubQtrOut = new Vector();
            vc_dateSubQtrIn.addElement(vector.get(1));
            SelectColumn sc_intr = new SelectColumn();
            Vector vc_intr = new Vector();
            TableColumn tbCl_intr = new TableColumn();
            tbCl_intr.setColumnName("interval");
            vc_intr.add(tbCl_intr);
            SelectColumn sc_dayValue = new SelectColumn();
            Vector vc_dayValue = new Vector();
            vc_dayValue.addElement(this.units(to_sqs, from_sqs, "dayofmonth"));
            vc_dayValue.add("-");
            vc_dayValue.add("1");
            sc_dayValue.setOpenBrace("(");
            sc_dayValue.setCloseBrace(")");
            sc_dayValue.setColumnExpression(vc_dayValue);
            vc_intr.addElement(sc_dayValue);
            vc_intr.add(" DAY");
            sc_intr.setColumnExpression(vc_intr);
            vc_dateSubQtrIn.addElement(sc_intr);
            fnCall_dateSubQtr.setFunctionArguments(vc_dateSubQtrIn);
            vc_dateSubQtrOut.addElement(fnCall_dateSubQtr);
            sc_value.setColumnExpression(vc_dateSubQtrOut);
            vc_dateSubIn.addElement(sc_value);
            SelectColumn sc_monthValue = new SelectColumn();
            Vector vc_monthValue = new Vector();
            vc_monthValue.addElement("(");
            vc_monthValue.addElement(this.units(to_sqs, from_sqs, "month"));
            vc_monthValue.add("-");
            vc_monthValue.add("1");
            vc_monthValue.addElement(")");
            vc_monthValue.addElement("%");
            vc_monthValue.addElement("3");
            sc_monthValue.setOpenBrace("(");
            sc_monthValue.setCloseBrace(")");
            sc_monthValue.setColumnExpression(vc_monthValue);
            vc_interval.addElement(sc_monthValue);
            vc_interval.add(" MONTH");
            sc_interval.setColumnExpression(vc_interval);
         } else {
            if (!unit.equalsIgnoreCase("YEAR")) {
               throw new ConvertException("Invalid Argument Value for Function START_DAY", "INVALID_ARGUMENT_VALUE", new Object[]{"START_DAY", "DATE_UNITS", "Provide any one of the following value week, week_sunday, week_monday, month, quarter or year"});
            }

            vc_dateSubIn.addElement(vector.get(1));
            sc_value = new SelectColumn();
            vc_value = new Vector();
            vc_value.addElement(this.units(to_sqs, from_sqs, "dayofyear"));
            vc_value.add("-");
            vc_value.add("1");
            sc_value.setOpenBrace("(");
            sc_value.setCloseBrace(")");
            sc_value.setColumnExpression(vc_value);
            vc_interval.addElement(sc_value);
            vc_interval.add(" DAY");
            sc_interval.setColumnExpression(vc_interval);
         }
      } else {
         vc_dateSubIn.addElement(vector.get(1));
         vc_interval.addElement(this.units(to_sqs, from_sqs, "weekday"));
         vc_interval.add(" DAY");
         sc_interval.setColumnExpression(vc_interval);
      }

      vc_dateSubIn.addElement(sc_interval);
      fnCl_dateSub.setFunctionArguments(vc_dateSubIn);
      vc_dateSubOut.addElement(fnCl_dateSub);
      sc_dateSub.setColumnExpression(vc_dateSubOut);
      arguments.addElement(sc_dateSub);
      this.setFunctionArguments(arguments);
   }

   public SelectColumn units(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, String unit) throws ConvertException {
      SelectColumn sc_unit = new SelectColumn();
      FunctionCalls fnCall_unit = new FunctionCalls();
      TableColumn tb_unit = new TableColumn();
      tb_unit.setColumnName(unit);
      fnCall_unit.setFunctionName(tb_unit);
      Vector vc_unitIn = new Vector();
      Vector vc_unitOut = new Vector();
      if (this.functionArguments.elementAt(1) instanceof SelectColumn) {
         vc_unitIn.addElement(((SelectColumn)this.functionArguments.elementAt(1)).toMySQLSelect(to_sqs, from_sqs));
      } else {
         vc_unitIn.addElement(this.functionArguments.elementAt(1));
      }

      fnCall_unit.setFunctionArguments(vc_unitIn);
      vc_unitOut.addElement(fnCall_unit);
      sc_unit.setOpenBrace("(");
      sc_unit.setCloseBrace(")");
      sc_unit.setColumnExpression(vc_unitOut);
      return sc_unit;
   }
}
