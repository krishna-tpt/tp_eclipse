package com.adventnet.swissqlapi.sql.functions.aggregate;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class aggregateIf extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector vector = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            vector.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector arguments = new Vector();
      SelectColumn sc_if;
      Vector vc_ifIn;
      FunctionCalls fn_distinct;
      TableColumn tb_distinct;
      if (!fnStr.equalsIgnoreCase("avgif") && !fnStr.equalsIgnoreCase("avg_if")) {
         if (!fnStr.equalsIgnoreCase("sumif") && !fnStr.equalsIgnoreCase("sum_if")) {
            TableColumn tb_if;
            Vector vc_ifOut;
            FunctionCalls fn_if;
            if (!fnStr.equalsIgnoreCase("countif") && !fnStr.equalsIgnoreCase("count_if")) {
               if (!fnStr.equalsIgnoreCase("distinctcount") && !fnStr.equalsIgnoreCase("count_distinct")) {
                  if (fnStr.equalsIgnoreCase("count_wb")) {
                     this.functionName.setColumnName("SUM");
                     sc_if = new SelectColumn();
                     vc_ifIn = new Vector();
                     vc_ifOut = new Vector();
                     fn_if = new FunctionCalls();
                     tb_if = new TableColumn();
                     tb_if.setColumnName("IF");
                     fn_if.setFunctionName(tb_if);
                     SelectColumn sc_whIt = new SelectColumn();
                     Vector vc_whIt = new Vector();
                     WhereItem whItem = new WhereItem();
                     WhereColumn leftExp = new WhereColumn();
                     Vector vc_leftExp = new Vector();
                     new WhereColumn();
                     new Vector();
                     vc_leftExp.addElement(vector.get(0));
                     leftExp.setColumnExpression(vc_leftExp);
                     whItem.setLeftWhereExp(leftExp);
                     whItem.setOperator("is null");
                     vc_whIt.addElement(whItem);
                     sc_whIt.setColumnExpression(vc_whIt);
                     vc_ifIn.addElement(sc_whIt);
                     vc_ifIn.addElement("1");
                     vc_ifIn.addElement("1");
                     fn_if.setFunctionArguments(vc_ifIn);
                     vc_ifOut.addElement(fn_if);
                     sc_if.setColumnExpression(vc_ifOut);
                     arguments.addElement(sc_if);
                  }
               } else {
                  this.functionName.setColumnName("COUNT");
                  sc_if = new SelectColumn();
                  vc_ifIn = new Vector();
                  fn_distinct = new FunctionCalls();
                  tb_distinct = new TableColumn();
                  tb_distinct.setColumnName("DISTINCT");
                  fn_distinct.setFunctionName(tb_distinct);
                  fn_distinct.setFunctionArguments(vector);
                  vc_ifIn.addElement(fn_distinct);
                  sc_if.setColumnExpression(vc_ifIn);
                  arguments.addElement(sc_if);
               }
            } else {
               this.functionName.setColumnName("COUNT");
               Vector vc_count = new Vector();
               vc_count.addElement(vector.get(0));
               vc_count.addElement("1");
               vc_count.addElement("null");
               SelectColumn sc_if = new SelectColumn();
               vc_ifOut = new Vector();
               fn_if = new FunctionCalls();
               tb_if = new TableColumn();
               tb_if.setColumnName("IF");
               fn_if.setFunctionName(tb_if);
               fn_if.setFunctionArguments(vc_count);
               vc_ifOut.addElement(fn_if);
               sc_if.setColumnExpression(vc_ifOut);
               arguments.addElement(sc_if);
            }
         } else {
            this.functionName.setColumnName("SUM");
            if (this.functionArguments.size() < 3) {
               vector.addElement("null");
            }

            sc_if = new SelectColumn();
            vc_ifIn = new Vector();
            fn_distinct = new FunctionCalls();
            tb_distinct = new TableColumn();
            tb_distinct.setColumnName("IF");
            fn_distinct.setFunctionName(tb_distinct);
            fn_distinct.setFunctionArguments(vector);
            vc_ifIn.addElement(fn_distinct);
            sc_if.setColumnExpression(vc_ifIn);
            arguments.addElement(sc_if);
         }
      } else {
         this.functionName.setColumnName("AVG");
         if (this.functionArguments.size() < 3) {
            vector.addElement("null");
         }

         sc_if = new SelectColumn();
         vc_ifIn = new Vector();
         fn_distinct = new FunctionCalls();
         tb_distinct = new TableColumn();
         tb_distinct.setColumnName("IF");
         fn_distinct.setFunctionName(tb_distinct);
         fn_distinct.setFunctionArguments(vector);
         vc_ifIn.addElement(fn_distinct);
         sc_if.setColumnExpression(vc_ifIn);
         arguments.addElement(sc_if);
      }

      this.setFunctionArguments(arguments);
   }
}
