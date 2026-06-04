package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.GroupByStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class grouping_id extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector non_rollupColumns;
      int i_count;
      Vector rollupArgs;
      FunctionCalls fc;
      if (this.functionName.getColumnName().equalsIgnoreCase("GROUP_ID")) {
         GroupByStatement gbs = from_sqs.getGroupByStatement();
         if (gbs != null) {
            Vector arguments = new Vector();
            non_rollupColumns = new Vector();
            Vector groupByItemList = gbs.getGroupByItemList();

            SelectColumn sc;
            String s_fn;
            for(i_count = 0; i_count < groupByItemList.size(); ++i_count) {
               if (groupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)groupByItemList.elementAt(i_count);
                  s_fn = sc.toString().toUpperCase();
                  if (!s_fn.startsWith("ROLLUP") && !s_fn.startsWith("CUBE")) {
                     non_rollupColumns.add(s_fn);
                  }
               }
            }

            for(i_count = 0; i_count < groupByItemList.size(); ++i_count) {
               if (groupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)groupByItemList.elementAt(i_count);
                  Vector v_ce = sc.getColumnExpression();
                  if (v_ce.elementAt(0) instanceof FunctionCalls) {
                     FunctionCalls fc = (FunctionCalls)v_ce.elementAt(0);
                     String s_fn = fc.getFunctionName().getColumnName();
                     if (s_fn.equalsIgnoreCase("cube") || s_fn.equalsIgnoreCase("rollup")) {
                        Vector rollupArgs = fc.getFunctionArguments();
                        if (rollupArgs.size() != non_rollupColumns.size() + 1) {
                           break;
                        }

                        for(int k = 0; k < rollupArgs.size(); ++k) {
                           Object rollupArg = rollupArgs.get(k);
                           if (rollupArg instanceof SelectColumn && !non_rollupColumns.contains(rollupArg.toString().toUpperCase())) {
                              arguments.add(rollupArg);
                           }
                        }
                     }
                  }
               } else if (groupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                  fc = (FunctionCalls)groupByItemList.elementAt(i_count);
                  s_fn = fc.getFunctionName().getColumnName();
                  if (s_fn.equalsIgnoreCase("cube") || s_fn.equalsIgnoreCase("rollup")) {
                     rollupArgs = fc.getFunctionArguments();
                     if (rollupArgs.size() != non_rollupColumns.size() + 1) {
                        break;
                     }

                     for(int k = 0; k < rollupArgs.size(); ++k) {
                        Object rollupArg = rollupArgs.get(k);
                        if (rollupArg instanceof SelectColumn && !non_rollupColumns.contains(rollupArg.toString().toUpperCase())) {
                           arguments.add(rollupArg);
                        }
                     }
                  }
               }
            }

            this.functionName.setColumnName("GROUPING");
            this.setFunctionArguments(arguments);
         }
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         SelectColumn newFunc = new SelectColumn();
         non_rollupColumns = new Vector();
         int n = arguments.size();
         non_rollupColumns.add("(");

         for(i_count = 0; i_count < n; ++i_count) {
            fc = new FunctionCalls();
            TableColumn tcn = new TableColumn();
            tcn.setColumnName("GROUPING");
            fc.setFunctionName(tcn);
            rollupArgs = new Vector();
            rollupArgs.add(arguments.get(i_count));
            fc.setFunctionArguments(rollupArgs);
            non_rollupColumns.add(fc);
            non_rollupColumns.add("*");
            non_rollupColumns.add("" + Math.pow(2.0D, (double)(n - (i_count + 1))));
            if (i_count != n - 1) {
               non_rollupColumns.add("+");
            }
         }

         newFunc.setColumnExpression(non_rollupColumns);
         this.setFunctionName((TableColumn)null);
         Vector newArgs = new Vector();
         newArgs.add(newFunc);
         this.setFunctionArguments(newArgs);
      }

   }
}
