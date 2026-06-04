package com.adventnet.swissqlapi.sql.functions.analytic;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class Tabular extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            if (i_count != 0) {
               if (!fnStr.equalsIgnoreCase("include_groupby") && !fnStr.equalsIgnoreCase("exclude_groupby") && !fnStr.equalsIgnoreCase("fixed_groupby")) {
                  if (fnStr.equalsIgnoreCase("map_groupby")) {
                     this.validateTabularArgs(sc, fnStr);
                  } else if (fnStr.equalsIgnoreCase("ignore_filters")) {
                     this.validateFilterValueIndexArgs(sc, fnStr, i_count == this.functionArguments.size() - 1);
                  }
               } else {
                  this.validateGranularityArgs(sc, fnStr);
               }
            }

            arguments.addElement(sc.toMySQLSelect(to_sqs, from_sqs));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void validateGranularityArgs(SelectColumn sc, String fnStr) throws ConvertException {
      Vector colExp = sc.getColumnExpression();
      ConvertException convExp = new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_GRANULARITY_ARGS_VALUE", new Object[]{fnStr, "COLUMN", "Provide a column or simple date function"});
      if (colExp.size() > 1) {
         throw convExp;
      } else {
         if (colExp.get(0) instanceof FunctionCalls) {
            FunctionCalls fnCl = (FunctionCalls)colExp.get(0);
            if (!FunctionCalls.getSimpleDateFnList().contains(fnCl.getFunctionName().getColumnName().toLowerCase())) {
               throw convExp;
            }

            Vector fnArgs = fnCl.getFunctionArguments();
            if (fnArgs.size() > 1) {
               throw convExp;
            }

            Vector argExpr = ((SelectColumn)fnArgs.get(0)).getColumnExpression();
            if (argExpr.size() > 1 || !(argExpr.get(0) instanceof TableColumn)) {
               throw convExp;
            }
         } else if (!(colExp.get(0) instanceof TableColumn)) {
            throw convExp;
         }

      }
   }

   public void validateTabularArgs(SelectColumn sc, String fnStr) throws ConvertException {
      Vector colExp = sc.getColumnExpression();
      ConvertException convExp = new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_TABULAR_ARGS_VALUE", new Object[]{fnStr, "COLUMN", "Provide a simple column only"});
      if (colExp.size() > 1 || !(colExp.get(0) instanceof TableColumn)) {
         throw convExp;
      }
   }

   public void validateFilterValueIndexArgs(SelectColumn sc, String fnStr, boolean isLastIndex) throws ConvertException {
      Vector colExp = sc.getColumnExpression();
      ConvertException convExp1 = new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_TABULAR_ARGS_VALUE", new Object[]{fnStr, "COLUMN", "Provide a simple column only"});
      ConvertException convExp2 = new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_TABULAR_FILTER_VALUE", new Object[]{fnStr, "COLUMN", "Provide a valid filter options"});
      if (colExp.size() > 1) {
         throw convExp1;
      } else {
         if (isLastIndex && colExp.get(0) instanceof String) {
            try {
               int value = Integer.parseInt((String)colExp.get(0));
               if (value < 0 || value > 2) {
                  throw convExp2;
               }
            } catch (Exception var8) {
               throw convExp2;
            }
         } else if (!(colExp.get(0) instanceof TableColumn)) {
            throw convExp1;
         }

      }
   }
}
