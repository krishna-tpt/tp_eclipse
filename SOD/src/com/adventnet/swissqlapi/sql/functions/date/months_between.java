package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class months_between extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTHS_BETWEEN");
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
      this.functionName.setColumnName("DATEDIFF");
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
         Vector newArguments = new Vector();
         newArguments.add("month");
         newArguments.add(arguments.get(1));
         newArguments.add(arguments.get(0));
         this.setFunctionArguments(newArguments);
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTHS_BETWEEN");
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
      this.functionName.setColumnName("MONTHS_BETWEEN");
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
      this.functionName.setColumnName("MONTHS_BETWEEN");
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
      this.functionName.setColumnName("");
      Vector arguments = new Vector();
      SelectColumn sc_monthsbwt = new SelectColumn();
      Vector vc_monthsbwt = new Vector();
      Vector vector1 = new Vector();
      Vector vector2 = new Vector();
      Vector vector3 = new Vector();
      Vector vector4 = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector1.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            vector2.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            vector3.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            vector4.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vector1.addElement(this.functionArguments.elementAt(i_count));
            vector2.addElement(this.functionArguments.elementAt(i_count));
            vector3.addElement(this.functionArguments.elementAt(i_count));
            vector4.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionArguments.size() < 2) {
         vector1.add(1, "now()");
         vector2.add(1, "now()");
         vector3.add(1, "now()");
         vector4.add(1, "now()");
      }

      SelectColumn sc_timestampdiff = new SelectColumn();
      FunctionCalls fn_timestampdiff = new FunctionCalls();
      TableColumn tb_timestampdiff = new TableColumn();
      tb_timestampdiff.setColumnName("TIMESTAMPDIFF");
      fn_timestampdiff.setFunctionName(tb_timestampdiff);
      Vector vc_timestampdiffIn = new Vector();
      Vector vc_timestampdiffOut = new Vector();
      vc_timestampdiffIn.add("MONTH");
      vc_timestampdiffIn.addElement(vector1.get(0));
      vc_timestampdiffIn.addElement(vector1.get(1));
      fn_timestampdiff.setFunctionArguments(vc_timestampdiffIn);
      vc_timestampdiffOut.addElement(fn_timestampdiff);
      sc_timestampdiff.setColumnExpression(vc_timestampdiffOut);
      vc_monthsbwt.addElement(sc_timestampdiff);
      String absoluteValue = "";
      SelectColumn sc_if;
      if (this.functionArguments.size() > 2) {
         if (vector1.elementAt(2) instanceof SelectColumn) {
            sc_if = (SelectColumn)vector1.elementAt(2);
            Vector vc = sc_if.getColumnExpression();
            if (!(vc.elementAt(0) instanceof String)) {
               throw new ConvertException("Invalid Argument Value for Function MONTHS_BETWEEN", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{"MONTHS_BETWEEN", "ISWHOLE_VALUE"});
            }

            absoluteValue = (String)vc.elementAt(0);
            absoluteValue = absoluteValue.replaceAll("'", "");
            absoluteValue = absoluteValue.trim();
            this.validateIsWholeValue(absoluteValue, "MONTHS_BETWEEN");
         }
      } else {
         absoluteValue = "0";
      }

      if (absoluteValue.equalsIgnoreCase("0")) {
         vc_monthsbwt.add("+");
         sc_if = new SelectColumn();
         FunctionCalls fn_if = new FunctionCalls();
         TableColumn tb_if = new TableColumn();
         tb_if.setColumnName("IF");
         fn_if.setFunctionName(tb_if);
         Vector vc_ifIn = new Vector();
         Vector vc_ifOut = new Vector();
         SelectColumn sc_wi = new SelectColumn();
         Vector vc_wi = new Vector();
         WhereItem wi_if = new WhereItem();
         WhereColumn if_left = new WhereColumn();
         Vector vc_if_left = new Vector();
         WhereColumn if_right = new WhereColumn();
         Vector vc_if_right = new Vector();
         vc_if_left.addElement(this.dayofmonth(vector2, 1));
         if_left.setColumnExpression(vc_if_left);
         wi_if.setLeftWhereExp(if_left);
         wi_if.setOperator(">=");
         vc_if_right.addElement(this.dayofmonth(vector2, 0));
         if_right.setColumnExpression(vc_if_right);
         wi_if.setRightWhereExp(if_right);
         vc_wi.addElement(wi_if);
         sc_wi.setColumnExpression(vc_wi);
         SelectColumn sc_tStmt = new SelectColumn();
         Vector vc_tStmt = new Vector();
         SelectColumn sc_tQuo = new SelectColumn();
         Vector vc_tQuo = new Vector();
         vc_tQuo.addElement(this.dayofmonth(vector3, 1));
         vc_tQuo.add("-");
         vc_tQuo.addElement(this.dayofmonth(vector3, 0));
         sc_tQuo.setOpenBrace("(");
         sc_tQuo.setColumnExpression(vc_tQuo);
         sc_tQuo.setCloseBrace(")");
         vc_tStmt.addElement(sc_tQuo);
         vc_tStmt.add("/");
         vc_tStmt.add("31");
         sc_tStmt.setColumnExpression(vc_tStmt);
         SelectColumn sc_fStmt = new SelectColumn();
         Vector vc_fStmt = new Vector();
         SelectColumn sc_fQuo = new SelectColumn();
         Vector vc_fQuo = new Vector();
         vc_fQuo.add("31");
         vc_fQuo.add("-");
         vc_fQuo.addElement(this.dayofmonth(vector4, 0));
         vc_fQuo.add("+");
         vc_fQuo.addElement(this.dayofmonth(vector4, 1));
         sc_fQuo.setOpenBrace("(");
         sc_fQuo.setColumnExpression(vc_fQuo);
         sc_fQuo.setCloseBrace(")");
         vc_fStmt.addElement(sc_fQuo);
         vc_fStmt.add("/");
         vc_fStmt.add("31");
         sc_fStmt.setColumnExpression(vc_fStmt);
         vc_ifIn.addElement(sc_wi);
         vc_ifIn.addElement(sc_tStmt);
         vc_ifIn.addElement(sc_fStmt);
         fn_if.setFunctionArguments(vc_ifIn);
         vc_ifOut.addElement(fn_if);
         sc_if.setColumnExpression(vc_ifOut);
         vc_monthsbwt.addElement(sc_if);
      }

      sc_monthsbwt.setColumnExpression(vc_monthsbwt);
      arguments.addElement(sc_monthsbwt);
      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTHS_BETWEEN");
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

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTHS_BETWEEN");
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

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTHS_BETWEEN");
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
      this.functionName.setColumnName("MONTHS_BETWEEN");
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

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MONTHS_BETWEEN");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public SelectColumn dayofmonth(Vector vector, int sded) {
      SelectColumn sc_dayofmonth = new SelectColumn();
      FunctionCalls fn_dayofmonth = new FunctionCalls();
      TableColumn tb_dayofmonth = new TableColumn();
      tb_dayofmonth.setColumnName("DAYOFMONTH");
      fn_dayofmonth.setFunctionName(tb_dayofmonth);
      Vector vc_dayofmonthIn = new Vector();
      Vector vc_dayofmonthOut = new Vector();
      vc_dayofmonthIn.addElement(vector.get(sded));
      fn_dayofmonth.setFunctionArguments(vc_dayofmonthIn);
      vc_dayofmonthOut.addElement(fn_dayofmonth);
      sc_dayofmonth.setColumnExpression(vc_dayofmonthOut);
      return sc_dayofmonth;
   }
}
