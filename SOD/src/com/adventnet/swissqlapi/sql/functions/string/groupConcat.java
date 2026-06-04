package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.OrderItem;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class groupConcat extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      int size = this.separatorString != null ? this.functionArguments.size() - 1 : this.functionArguments.size();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            selColumn.convertWhereExpAloneInsideFunctionTo_IF_Function(size);
            arguments.addElement(selColumn.toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String newqry = "";
      StringBuffer str = new StringBuffer();
      if (this.functionName.getColumnName().equalsIgnoreCase("group_concat")) {
         String s;
         if (arguments.size() == 1) {
            s = "CAST(" + arguments.get(0).toString() + " AS TEXT)";
            if (this.argumentQualifier != null && this.argumentQualifier.equalsIgnoreCase("distinct")) {
               s = "DISTINCT " + s;
               this.argumentQualifier = null;
            }

            qry = " array_to_string(ARRAY(SELECT unnest(array_agg(" + s + ")) ORDER BY 1),',')  ";
            newqry = "string_agg(" + s + ",',')";
         }

         if (arguments.size() > 1) {
            s = null;
            int l = false;
            String arg = "CAST(" + arguments.get(0).toString() + " AS TEXT)";
            boolean isDistinct = false;
            if (this.argumentQualifier != null && this.argumentQualifier.equalsIgnoreCase("distinct")) {
               arg = "DISTINCT " + arg;
               isDistinct = true;
               this.argumentQualifier = null;
            }

            String orderByArg = this.getOrderByClause(isDistinct, arg, to_sqs, from_sqs);
            if (this.separatorString != null) {
               String sepString = arguments.get(1).toString().contains("\\") ? "E" + arguments.get(1).toString() : arguments.get(1).toString();
               qry = " array_to_string(ARRAY(SELECT unnest(array_agg(" + arg + ")) ORDER BY 1)," + arguments.get(1).toString() + ")  ";
               newqry = "string_agg(" + arg + ", " + sepString + " " + orderByArg + ")";
               this.separatorString = null;
            } else {
               for(int i = 1; i < this.functionArguments.size(); ++i) {
                  str.append("CAST(" + arguments.get(i) + " AS TEXT)");
                  str.append(" || ");
               }

               if (str.toString().endsWith(" || ")) {
                  int l = str.toString().length() - " || ".length();
                  s = str.toString().substring(0, l);
               }

               qry = " array_to_string(ARRAY(SELECT unnest(array_agg(" + arg + " || " + s + ")) ORDER BY 1),',')  ";
               newqry = "string_agg(" + arg + " || " + s + ", ',' " + orderByArg + ")";
            }
         }

         if (from_sqs == null || !from_sqs.isAmazonRedShift() && !from_sqs.isPgsqlLive() && !from_sqs.isVerticaDb()) {
            this.functionName.setColumnName(newqry);
         } else {
            this.functionName.setColumnName(qry);
         }

         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public String getOrderByClause(boolean isDistinct, String argument, SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      String orderBy = "";

      try {
         if (this.obs != null && this.obs.getOrderItemList() != null && this.obs.getOrderItemList().get(0) instanceof OrderItem) {
            String order = ((OrderItem)this.obs.getOrderItemList().get(0)).getOrder();
            if (order == null || order.equalsIgnoreCase("null")) {
               order = "";
            }

            if (isDistinct) {
               orderBy = "ORDER BY " + argument + " " + order;
            } else {
               orderBy = this.obs.toPostgreSQLSelect(to_sqs, from_sqs).toString();
            }
         }
      } catch (Exception var10) {
      } finally {
         this.obs = null;
      }

      return orderBy;
   }
}
