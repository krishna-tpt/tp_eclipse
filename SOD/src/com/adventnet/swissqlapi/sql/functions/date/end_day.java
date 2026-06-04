package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class end_day extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      Vector vector = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vector.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String unit = "";
      SelectColumn sc_dateAdd;
      TableColumn tbCl_dateAdd;
      if (vector.elementAt(0) instanceof SelectColumn) {
         sc_dateAdd = (SelectColumn)vector.elementAt(0);
         Vector vc_units = sc_dateAdd.getColumnExpression();
         if (vc_units.elementAt(0) instanceof TableColumn) {
            tbCl_dateAdd = (TableColumn)vc_units.elementAt(0);
            unit = tbCl_dateAdd.getColumnName();
            unit = unit.replaceAll("'", "");
         }
      }

      Vector vc_dateAddIn;
      Vector vc_dateAddOut;
      SelectColumn sc_intr;
      FunctionCalls fnCall_dateAdd;
      TableColumn tbCl_intr;
      Vector vc_dateAddIn;
      Vector vc_value;
      SelectColumn sc_intr;
      Vector vc_intr;
      TableColumn tbCl_intr;
      SelectColumn sc_value;
      Vector vc_value;
      FunctionCalls fnCl_dateAdd;
      if (unit.equalsIgnoreCase("year")) {
         this.functionName.setColumnName("DATE");
         sc_dateAdd = new SelectColumn();
         fnCl_dateAdd = new FunctionCalls();
         tbCl_dateAdd = new TableColumn();
         tbCl_dateAdd.setColumnName("LAST_DAY");
         fnCl_dateAdd.setFunctionName(tbCl_dateAdd);
         vc_dateAddIn = new Vector();
         vc_dateAddOut = new Vector();
         sc_intr = new SelectColumn();
         fnCall_dateAdd = new FunctionCalls();
         tbCl_intr = new TableColumn();
         tbCl_intr.setColumnName("DATE_ADD");
         fnCall_dateAdd.setFunctionName(tbCl_intr);
         vc_dateAddIn = new Vector();
         vc_value = new Vector();
         vc_dateAddIn.addElement(vector.get(1));
         sc_intr = new SelectColumn();
         vc_intr = new Vector();
         tbCl_intr = new TableColumn();
         tbCl_intr.setColumnName("interval");
         vc_intr.add(tbCl_intr);
         sc_value = new SelectColumn();
         vc_value = new Vector();
         vc_value.addElement("12");
         vc_value.addElement("-");
         vc_value.addElement(this.units(to_sqs, from_sqs, "month"));
         sc_value.setOpenBrace("(");
         sc_value.setCloseBrace(")");
         sc_value.setColumnExpression(vc_value);
         vc_intr.addElement(sc_value);
         vc_intr.add(" MONTH");
         sc_intr.setColumnExpression(vc_intr);
         vc_dateAddIn.addElement(sc_intr);
         fnCall_dateAdd.setFunctionArguments(vc_dateAddIn);
         vc_value.addElement(fnCall_dateAdd);
         sc_intr.setColumnExpression(vc_value);
         vc_dateAddIn.addElement(sc_intr);
         fnCl_dateAdd.setFunctionArguments(vc_dateAddIn);
         vc_dateAddOut.addElement(fnCl_dateAdd);
         sc_dateAdd.setColumnExpression(vc_dateAddOut);
         arguments.addElement(sc_dateAdd);
      } else if (unit.equalsIgnoreCase("quarter")) {
         this.functionName.setColumnName("DATE");
         sc_dateAdd = new SelectColumn();
         fnCl_dateAdd = new FunctionCalls();
         tbCl_dateAdd = new TableColumn();
         tbCl_dateAdd.setColumnName("LAST_DAY");
         fnCl_dateAdd.setFunctionName(tbCl_dateAdd);
         vc_dateAddIn = new Vector();
         vc_dateAddOut = new Vector();
         sc_intr = new SelectColumn();
         fnCall_dateAdd = new FunctionCalls();
         tbCl_intr = new TableColumn();
         tbCl_intr.setColumnName("DATE_ADD");
         fnCall_dateAdd.setFunctionName(tbCl_intr);
         vc_dateAddIn = new Vector();
         vc_value = new Vector();
         vc_dateAddIn.addElement(vector.get(1));
         sc_intr = new SelectColumn();
         vc_intr = new Vector();
         tbCl_intr = new TableColumn();
         tbCl_intr.setColumnName("interval");
         vc_intr.add(tbCl_intr);
         sc_value = new SelectColumn();
         vc_value = new Vector();
         vc_value.addElement(this.units(to_sqs, from_sqs, "quarter"));
         vc_value.addElement("*");
         vc_value.addElement("3");
         vc_value.addElement("-");
         vc_value.addElement(this.units(to_sqs, from_sqs, "month"));
         sc_value.setOpenBrace("(");
         sc_value.setCloseBrace(")");
         sc_value.setColumnExpression(vc_value);
         vc_intr.addElement(sc_value);
         vc_intr.add(" MONTH");
         sc_intr.setColumnExpression(vc_intr);
         vc_dateAddIn.addElement(sc_intr);
         fnCall_dateAdd.setFunctionArguments(vc_dateAddIn);
         vc_value.addElement(fnCall_dateAdd);
         sc_intr.setColumnExpression(vc_value);
         vc_dateAddIn.addElement(sc_intr);
         fnCl_dateAdd.setFunctionArguments(vc_dateAddIn);
         vc_dateAddOut.addElement(fnCl_dateAdd);
         sc_dateAdd.setColumnExpression(vc_dateAddOut);
         arguments.addElement(sc_dateAdd);
      } else if (unit.equalsIgnoreCase("month")) {
         this.functionName.setColumnName("DATE");
         sc_dateAdd = new SelectColumn();
         fnCl_dateAdd = new FunctionCalls();
         tbCl_dateAdd = new TableColumn();
         tbCl_dateAdd.setColumnName("LAST_DAY");
         fnCl_dateAdd.setFunctionName(tbCl_dateAdd);
         vc_dateAddIn = new Vector();
         vc_dateAddOut = new Vector();
         vc_dateAddIn.addElement(vector.get(1));
         fnCl_dateAdd.setFunctionArguments(vc_dateAddIn);
         vc_dateAddOut.addElement(fnCl_dateAdd);
         sc_dateAdd.setColumnExpression(vc_dateAddOut);
         arguments.addElement(sc_dateAdd);
      } else {
         Vector vc_intr;
         SelectColumn sc_value;
         if (!unit.equalsIgnoreCase("week") && !unit.equalsIgnoreCase("week_monday")) {
            if (!unit.equalsIgnoreCase("week_sunday")) {
               throw new ConvertException("Invalid Argument Value for Function END_DAY", "INVALID_ARGUMENT_VALUE", new Object[]{"END_DAY", "DATE_UNITS", "Provide any one of the following value week, week_sunday, week_monday, month, quarter or year"});
            }

            this.functionName.setColumnName("DATE");
            sc_dateAdd = new SelectColumn();
            fnCl_dateAdd = new FunctionCalls();
            tbCl_dateAdd = new TableColumn();
            tbCl_dateAdd.setColumnName("DATE_ADD");
            fnCl_dateAdd.setFunctionName(tbCl_dateAdd);
            vc_dateAddIn = new Vector();
            vc_dateAddOut = new Vector();
            vc_dateAddIn.addElement(vector.get(1));
            sc_intr = new SelectColumn();
            vc_intr = new Vector();
            tbCl_intr = new TableColumn();
            tbCl_intr.setColumnName("interval");
            vc_intr.add(tbCl_intr);
            sc_value = new SelectColumn();
            vc_value = new Vector();
            vc_value.addElement("7");
            vc_value.addElement("-");
            vc_value.addElement(this.units(to_sqs, from_sqs, "dayofweek"));
            sc_value.setOpenBrace("(");
            sc_value.setCloseBrace(")");
            sc_value.setColumnExpression(vc_value);
            vc_intr.addElement(sc_value);
            vc_intr.add(" DAY");
            sc_intr.setColumnExpression(vc_intr);
            vc_dateAddIn.addElement(sc_intr);
            fnCl_dateAdd.setFunctionArguments(vc_dateAddIn);
            vc_dateAddOut.addElement(fnCl_dateAdd);
            sc_dateAdd.setColumnExpression(vc_dateAddOut);
            arguments.addElement(sc_dateAdd);
         } else {
            this.functionName.setColumnName("DATE");
            sc_dateAdd = new SelectColumn();
            fnCl_dateAdd = new FunctionCalls();
            tbCl_dateAdd = new TableColumn();
            tbCl_dateAdd.setColumnName("DATE_ADD");
            fnCl_dateAdd.setFunctionName(tbCl_dateAdd);
            vc_dateAddIn = new Vector();
            vc_dateAddOut = new Vector();
            vc_dateAddIn.addElement(vector.get(1));
            sc_intr = new SelectColumn();
            vc_intr = new Vector();
            tbCl_intr = new TableColumn();
            tbCl_intr.setColumnName("interval");
            vc_intr.add(tbCl_intr);
            sc_value = new SelectColumn();
            vc_value = new Vector();
            vc_value.addElement("6");
            vc_value.addElement("-");
            vc_value.addElement(this.units(to_sqs, from_sqs, "weekday"));
            sc_value.setOpenBrace("(");
            sc_value.setCloseBrace(")");
            sc_value.setColumnExpression(vc_value);
            vc_intr.addElement(sc_value);
            vc_intr.add(" DAY");
            sc_intr.setColumnExpression(vc_intr);
            vc_dateAddIn.addElement(sc_intr);
            fnCl_dateAdd.setFunctionArguments(vc_dateAddIn);
            vc_dateAddOut.addElement(fnCl_dateAdd);
            sc_dateAdd.setColumnExpression(vc_dateAddOut);
            arguments.addElement(sc_dateAdd);
         }
      }

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
