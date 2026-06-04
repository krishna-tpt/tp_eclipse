package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class repeat extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (isdenodo) {
         this.functionName.setColumnName("REPEAT");
      } else {
         Object obj = this.functionArguments.get(0);
         String arg1 = "NULL";
         if (obj instanceof SelectColumn) {
            SelectColumn arg2 = (SelectColumn)this.functionArguments.get(1);
            if (arg2.getColumnExpression().get(0) instanceof String) {
               try {
                  String countStr = arg2.getColumnExpression().get(0).toString();
                  countStr = countStr.replaceAll("'", "");
                  int repeatCount = Integer.parseInt(countStr);
                  if (repeatCount > 0) {
                     arg1 = obj.toString();

                     for(int i = 0; i < repeatCount - 1; ++i) {
                        arg1 = arg1 + "||" + obj.toString();
                     }
                  }
               } catch (Exception var11) {
               }
            }
         }

         this.functionArguments.setElementAt(arg1, 0);
         this.functionArguments.setSize(1);
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("REPLICATE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("REPEAT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("REPLICATE");
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
      this.functionName.setColumnName("REPEAT");
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
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            if (i_count == 0) {
               sc.convertSelectColumnToTextDataType();
            }

            arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String secArgument;
      if (this.functionName.toString().equalsIgnoreCase("group_concat")) {
         secArgument = " array_to_string(ARRAY(SELECT unnest(array_agg(" + arguments.get(0) + ")) ORDER BY 1),',')  ";
         this.functionName.setColumnName(secArgument);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.functionName.setColumnName("REPEAT");
         if (arguments.size() == 2) {
            secArgument = arguments.get(1).toString();
            Integer number = StringFunctions.getIntegerValue(secArgument);
            if (number != null) {
               secArgument = number.toString();
            } else if (from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.numeric")) {
               secArgument = "TOINTEGER_UDF(" + secArgument + ")";
            } else {
               secArgument = "ROUND(" + secArgument + ")::integer";
            }

            arguments.set(1, secArgument);
         }

         this.setFunctionArguments(arguments);
      }

   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("REPEAT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("REPEAT");
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

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("REPEAT");
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

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RPAD");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "RPAD('',(CHAR_LENGTH(" + arguments.get(0).toString() + ") * CAST(" + arguments.get(1).toString() + " AS INTEGER))," + arguments.get(0).toString() + ")";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nThe built-in function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("REPEAT");
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

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments;
      int i_count;
      SelectColumn sc;
      String separateString;
      if (this.functionName.getColumnName().trim().equalsIgnoreCase("REPEAT")) {
         arguments = new Vector();
         this.functionName.setColumnName("RPAD");

         for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               sc = (SelectColumn)this.functionArguments.elementAt(i_count);
               if (i_count == 0) {
                  sc.convertSelectColumnToTextDataType();
               }

               arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String lengthArg = "LENGTH(" + arguments.get(0).toString() + ")";
         separateString = lengthArg + " + (" + lengthArg + " * CAST(" + arguments.get(1).toString() + " AS BIGINT))";
         arguments.setElementAt(separateString, 1);
         arguments.add(arguments.get(0));
         this.setFunctionArguments(arguments);
      } else if (this.functionName.getColumnName().trim().equalsIgnoreCase("group_concat")) {
         arguments = new Vector();
         StringBuffer str = new StringBuffer();
         this.functionName.setColumnName("listagg");
         str.append("concat(");
         separateString = "','";
         int size = this.separatorString != null ? this.functionArguments.size() - 1 : this.functionArguments.size();

         int i_count;
         for(i_count = 0; i_count < size; ++i_count) {
            if (i_count > 0) {
               str.append(",");
            }

            str.append("cast(");
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
               selColumn.convertWhereExpAloneInsideFunctionTo_IF_Function(size);
               str.append(selColumn.toVectorWiseSelect(to_sqs, from_sqs));
            } else {
               str.append(this.functionArguments.elementAt(i_count));
            }

            str.append(" as varchar)");
         }

         str.append(")");
         if (this.separatorString != null && i_count + 1 == this.functionArguments.size() && this.functionArguments.get(i_count) instanceof String) {
            separateString = this.functionArguments.elementAt(i_count).toString();
         }

         arguments.addElement(str);
         arguments.addElement(separateString);
         this.separatorString = null;
         this.setFunctionArguments(arguments);
         String orderByWithinGroup = "";
         if (this.obs != null) {
            if (this.argumentQualifier == null) {
               OrderByStatement vwObs = this.obs.toVectorWiseSelect(to_sqs, from_sqs);
               orderByWithinGroup = "WITHIN GROUP (" + vwObs.toString() + ")";
            } else {
               orderByWithinGroup = "WITHIN GROUP ( ORDER BY " + str + " ASC NULLS LAST)";
            }

            this.obs = null;
         }

         String argQualifier = this.argumentQualifier != null ? this.argumentQualifier : "";
         this.functionName.setColumnName("LISTAGG(" + argQualifier + " " + str + "," + separateString + ") " + orderByWithinGroup);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
         this.setArgumentQualifier((String)null);
      } else if (this.functionName.getColumnName().trim().equalsIgnoreCase("substring_index")) {
         arguments = new Vector();
         this.functionName.setColumnName("substring_index");

         for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               sc = (SelectColumn)this.functionArguments.elementAt(i_count);
               if (i_count < this.functionArguments.size() - 1) {
                  sc.convertSelectColumnToTextDataType();
               }

               arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
      }

   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("REPEAT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
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

      this.setFunctionArguments(arguments);
      String qry = "ARRAY_JOIN(REPEAT(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "),'')";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      String qry = "LPAD('',LENGTH(" + arguments.get(0).toString() + ")*" + arguments.get(1).toString() + "," + arguments.get(0).toString() + ")";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      String qry = "REPLACE(HEX(ZEROBLOB(" + arguments.get(1).toString() + ")), '00', " + arguments.get(0).toString() + ")";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "Replace(String(" + arguments.get(1).toString() + ",' '),' '," + arguments.get(0).toString() + ")";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("REPEAT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }
}
