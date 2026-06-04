package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class right extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUBSTR");
      Vector arguments = new Vector();
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn arg2 = new SelectColumn();
      Vector colExpArg2 = new Vector();
      FunctionCalls length = new FunctionCalls();
      TableColumn outerFunction = new TableColumn();
      String lengthSyntax = isdenodo ? "LEN" : "LENGTH";
      outerFunction.setColumnName(lengthSyntax);
      length.setFunctionName(outerFunction);
      Vector lenArgument = new Vector();
      lenArgument.add(this.functionArguments.get(0));
      length.setFunctionArguments(lenArgument);
      colExpArg2.addElement(length);
      colExpArg2.addElement("-");
      colExpArg2.addElement(this.functionArguments.get(1));
      colExpArg2.addElement("+1");
      arg2.setColumnExpression(colExpArg2);
      arguments.add(1, arg2);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RIGHT");
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

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RIGHT");
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
      this.functionName.setColumnName("RIGHT");
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
      String functionNameStr = "RIGHT";
      Integer number = null;
      if (from_sqs != null && from_sqs.getBooleanValues("use.udf.functions.for.text")) {
         functionNameStr = "RIGHT_UDF";
      }

      this.functionName.setColumnName(functionNameStr);
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            if (i_count == 0) {
               sc.convertSelectColumnToTextDataType();
            }

            String arg = sc.toPostgreSQLSelect(to_sqs, from_sqs).toString();
            if (i_count == 1) {
               number = StringFunctions.getIntegerValue(arg);
               if (number != null) {
                  arg = number.toString();
               } else if (from_sqs != null && from_sqs.getBooleanValues("use.udf.functions.for.text") && from_sqs.getBooleanValues("use.udf.functions.for.numeric")) {
                  arg = "TOINTEGER_UDF(" + arg + ")";
               }
            }

            arguments.addElement(arg);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 2 && from_sqs != null && (isPostgreLiveDbs || !from_sqs.getBooleanValues("use.udf.functions.for.text"))) {
         String dataType = from_sqs.isVerticaDb() ? "VARCHAR" : "TEXT";
         String qry = "(case when " + arguments.get(1) + " < 0 then NULL else right(" + arguments.get(0) + "," + arguments.get(1) + ") end)";
         if (number != null) {
            if (number > 0) {
               qry = "RIGHT(" + arguments.get(0) + "," + number.toString() + ")";
            } else {
               qry = "CAST(NULL AS " + dataType + ")";
            }
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RIGHT");
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
      this.functionName.setColumnName("SUBSTR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn arg2 = new SelectColumn();
      Vector colExpArg2 = new Vector();
      FunctionCalls length = new FunctionCalls();
      TableColumn outerFunction = new TableColumn();
      outerFunction.setOwnerName(this.functionName.getOwnerName());
      outerFunction.setTableName(this.functionName.getTableName());
      outerFunction.setColumnName("LENGTH");
      length.setFunctionName(outerFunction);
      Vector lenArgument = new Vector();
      lenArgument.add(this.functionArguments.get(0));
      length.setFunctionArguments(lenArgument);
      colExpArg2.addElement(length);
      colExpArg2.addElement("-");
      colExpArg2.addElement(this.functionArguments.get(1));
      colExpArg2.addElement("+1");
      arg2.setColumnExpression(colExpArg2);
      this.functionArguments.insertElementAt(arg2, 1);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUBSTR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn arg2 = new SelectColumn();
      Vector colExpArg2 = new Vector();
      FunctionCalls length = new FunctionCalls();
      TableColumn outerFunction = new TableColumn();
      outerFunction.setOwnerName(this.functionName.getOwnerName());
      outerFunction.setTableName(this.functionName.getTableName());
      outerFunction.setColumnName("CHARACTER_LENGTH");
      length.setFunctionName(outerFunction);
      Vector lenArgument = new Vector();
      lenArgument.add(this.functionArguments.get(0));
      length.setFunctionArguments(lenArgument);
      colExpArg2.addElement(length);
      colExpArg2.addElement("-");
      colExpArg2.addElement(this.functionArguments.get(1));
      colExpArg2.addElement("+1");
      arg2.setColumnExpression(colExpArg2);
      this.functionArguments.insertElementAt(arg2, 1);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RIGHT");
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

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nThe built-in function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUBSTR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn arg2 = new SelectColumn();
      Vector colExpArg2 = new Vector();
      FunctionCalls length = new FunctionCalls();
      TableColumn outerFunction = new TableColumn();
      outerFunction.setOwnerName(this.functionName.getOwnerName());
      outerFunction.setTableName(this.functionName.getTableName());
      outerFunction.setColumnName("LENGTH");
      length.setFunctionName(outerFunction);
      Vector lenArgument = new Vector();
      lenArgument.add(this.functionArguments.get(0));
      length.setFunctionArguments(lenArgument);
      colExpArg2.addElement(length);
      colExpArg2.addElement("-");
      colExpArg2.addElement(this.functionArguments.get(1));
      colExpArg2.addElement("+1");
      arg2.setColumnExpression(colExpArg2);
      this.functionArguments.insertElementAt(arg2, 1);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RIGHT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            if (i_count == 0) {
               sc.convertSelectColumnToTextDataType();
            }

            arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUBSTR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn arg2 = new SelectColumn();
      Vector colExpArg2 = new Vector();
      FunctionCalls length = new FunctionCalls();
      TableColumn outerFunction = new TableColumn();
      outerFunction.setColumnName("LENGTH");
      length.setFunctionName(outerFunction);
      Vector lenArgument = new Vector();
      lenArgument.add(this.functionArguments.get(0));
      length.setFunctionArguments(lenArgument);
      colExpArg2.addElement(length);
      colExpArg2.addElement("-");
      colExpArg2.addElement(this.functionArguments.get(1));
      colExpArg2.addElement("+1");
      arg2.setColumnExpression(colExpArg2);
      arguments.add(1, arg2);
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUBSTR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      String qry = " IF((LENGTH(" + arguments.get(0).toString() + ") <= " + arguments.get(1).toString() + "), SUBSTR(" + arguments.get(0).toString() + ",1), SUBSTR(" + arguments.get(0).toString() + ",-" + arguments.get(1).toString() + "))";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RIGHT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
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

      String qry = "(CASE WHEN (LENGTH(" + arguments.get(0).toString() + ") <= " + arguments.get(1).toString() + ") THEN SUBSTR(" + arguments.get(0).toString() + ",1) ELSE SUBSTR(" + arguments.get(0).toString() + ",-" + arguments.get(1).toString() + ") END)";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RIGHT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RIGHT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }
}
