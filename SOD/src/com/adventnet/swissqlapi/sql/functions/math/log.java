package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class log extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      Boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      if (this.functionName.getColumnName().equalsIgnoreCase("log10")) {
         this.functionName.setColumnName("LOG");
         arguments.add("10");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("log2")) {
         this.functionName.setColumnName("LOG");
         arguments.add("2");
      } else {
         this.functionName.setColumnName("LN");
      }

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }

         if (i == 1) {
            this.functionName.setColumnName("LOG");
         }
      }

      if (isdenodo && arguments.size() == 2) {
         Vector swaparguments = new Vector();
         swaparguments.addElement(arguments.get(1));
         swaparguments.addElement(arguments.get(0));
         this.setFunctionArguments(swaparguments);
      } else {
         this.setFunctionArguments(arguments);
      }
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (from_sqs != null && from_sqs.isMSAzure()) {
         String argument;
         if (this.functionArguments.size() == 2) {
            argument = "CASE WHEN " + arguments.get(1).toString() + " <= 0 THEN NULL ELSE " + arguments.get(1).toString() + " END";
            arguments.set(1, argument);
         } else {
            argument = "CASE WHEN " + arguments.get(0).toString() + " <= 0 THEN NULL ELSE " + arguments.get(0).toString() + " END";
            arguments.set(0, argument);
         }

         if (this.functionName.toString().equalsIgnoreCase("LOG")) {
            Vector swaparguments = new Vector();
            this.functionName.setColumnName("LOG");
            if (this.functionArguments.size() < 2) {
               swaparguments.addElement(arguments.get(0));
            } else {
               swaparguments.addElement(arguments.get(1));
               swaparguments.addElement(arguments.get(0));
            }

            this.setFunctionArguments(swaparguments);
         } else if (this.functionName.toString().equalsIgnoreCase("LOG2")) {
            this.functionName.setColumnName("LOG");
            arguments.addElement(2);
            this.setFunctionArguments(arguments);
         } else if (this.functionName.toString().equalsIgnoreCase("LOG10")) {
            this.functionName.setColumnName("LOG");
            arguments.addElement(10);
            this.setFunctionArguments(arguments);
         }
      } else {
         this.functionName.setColumnName("LOG");
         this.setFunctionArguments(arguments);
         if (arguments.size() == 2) {
            this.functionName.setColumnName("LOG10");
            if (arguments.get(0) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)arguments.get(0);
               if (sc.getColumnExpression() != null && !sc.getColumnExpression().isEmpty()) {
                  Vector exp = sc.getColumnExpression();
                  if (exp.get(0) instanceof String) {
                     String longValue = exp.get(0).toString();
                     if (longValue.equals("10")) {
                        arguments.remove(0);
                     }
                  }
               }
            }
         }
      }

   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }

         String argumentValue = "CASE WHEN " + arguments.get(i).toString() + " <= 0 THEN NULL ELSE " + arguments.get(i).toString() + " END";
         arguments.set(i, argumentValue);
      }

      if (this.functionName.toString().equalsIgnoreCase("LOG")) {
         this.setFunctionArguments(arguments);
      } else {
         Vector swaparguments;
         if (this.functionName.toString().equalsIgnoreCase("LOG2")) {
            swaparguments = new Vector();
            this.functionName.setColumnName("LOG");
            swaparguments.addElement(2);
            swaparguments.addElement(arguments.get(0));
            this.setFunctionArguments(swaparguments);
         } else if (this.functionName.toString().equalsIgnoreCase("LOG10")) {
            swaparguments = new Vector();
            this.functionName.setColumnName("LOG");
            swaparguments.addElement(10);
            swaparguments.addElement(arguments.get(0));
            this.setFunctionArguments(swaparguments);
         }
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LOG");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      if (arguments.size() == 2) {
         this.functionName.setColumnName("LOG10");
         if (arguments.get(0) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)arguments.get(0);
            if (sc.getColumnExpression() != null && !sc.getColumnExpression().isEmpty()) {
               Vector exp = sc.getColumnExpression();
               if (exp.get(0) instanceof String) {
                  String longValue = exp.get(0).toString();
                  if (longValue.equals("10")) {
                     arguments.remove(0);
                  }
               }
            }
         }
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String oldfuncName = this.functionName.getColumnName();
      this.functionName.setColumnName("LOG10");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (oldfuncName.equalsIgnoreCase("log2")) {
         this.functionName.setColumnName("(LOG10(" + arguments.get(0).toString() + ")/LOG10(2))");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         this.setFunctionArguments(arguments);
         if (arguments.size() == 2) {
            this.functionName.setColumnName("(LOG10(" + arguments.get(1).toString() + ")/LOG10(" + arguments.get(0).toString() + "))");
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         }

      }
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      String argument;
      if (this.functionArguments.size() == 2) {
         argument = "";
         if (isPostgreLiveDbs) {
            if (from_sqs != null && from_sqs.isAmazonRedShift()) {
               argument = "CASE WHEN " + arguments.get(1).toString() + " <= 0 THEN NULL ELSE " + arguments.get(1).toString() + "::real END";
            } else {
               argument = "CASE WHEN " + arguments.get(1).toString() + " <= 0 THEN NULL ELSE " + arguments.get(1).toString() + "::numeric END";
            }
         } else {
            argument = "CASE WHEN " + arguments.get(1).toString() + " <= 0 THEN NULL ELSE " + arguments.get(1).toString() + " END";
         }

         arguments.set(1, argument);
      } else {
         argument = "";
         if (isPostgreLiveDbs) {
            if (from_sqs != null && from_sqs.isAmazonRedShift()) {
               argument = "CASE WHEN " + arguments.get(0).toString() + " <= 0 THEN NULL ELSE " + arguments.get(0).toString() + "::real END";
            } else {
               argument = "CASE WHEN " + arguments.get(0).toString() + " <= 0 THEN NULL ELSE " + arguments.get(0).toString() + "::numeric END";
            }
         } else {
            argument = "CASE WHEN " + arguments.get(0).toString() + " <= 0 THEN NULL ELSE " + arguments.get(0).toString() + " END";
         }

         arguments.set(0, argument);
      }

      if (this.functionName.toString().equalsIgnoreCase("LOG")) {
         if (from_sqs != null && from_sqs.isAmazonRedShift()) {
            this.functionName.setColumnName("LOG(" + arguments.get(1).toString() + ")/LOG(" + arguments.get(0).toString() + "::real)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else {
            this.functionName.setColumnName("LOG");
            this.setFunctionArguments(arguments);
         }
      } else {
         Object temp;
         if (this.functionName.toString().equalsIgnoreCase("LOG10")) {
            if (from_sqs != null && from_sqs.isAmazonRedShift()) {
               this.functionName.setColumnName("LOG(" + arguments.get(0).toString() + ")");
               this.setOpenBracesForFunctionNameRequired(false);
               this.functionArguments = new Vector();
            } else {
               this.functionName.setColumnName("LOG");
               temp = arguments.get(0);
               this.functionArguments.set(0, 10);
               this.functionArguments.add(1, temp);
               this.setFunctionArguments(this.functionArguments);
            }
         } else if (this.functionName.toString().equalsIgnoreCase("LOG2")) {
            if (from_sqs != null && from_sqs.isAmazonRedShift()) {
               this.functionName.setColumnName("LOG(" + arguments.get(0).toString() + ")/LOG(2::real)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.functionArguments = new Vector();
            } else {
               this.functionName.setColumnName("LOG");
               temp = arguments.get(0);
               this.functionArguments.set(0, 2);
               this.functionArguments.add(1, temp);
               this.setFunctionArguments(this.functionArguments);
            }
         }
      }

   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnName = this.functionName.getColumnName().trim();
      if (fnName.equalsIgnoreCase("LOG10")) {
         this.functionName.setColumnName("LOG10");
      } else {
         this.functionName.setColumnName("LOG");
      }

      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      if (from_sqs != null && from_sqs.isHyperSql()) {
         String qry;
         if (this.functionArguments.size() == 2) {
            qry = "(LOG(" + arguments.get(1).toString() + ")/LOG(" + arguments.get(0).toString() + "))";
         } else if (fnName.equalsIgnoreCase("LOG2")) {
            qry = "(LOG(" + arguments.get(0).toString() + ")/LOG(2))";
         } else if (fnName.equalsIgnoreCase("LOG10")) {
            qry = "LOG10(" + arguments.get(0).toString() + ")";
         } else {
            qry = "LOG(" + arguments.get(0).toString() + ")";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LOG");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      if (arguments.size() == 2) {
         this.functionName.setColumnName("LOG10");
         if (arguments.get(0) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)arguments.get(0);
            if (sc.getColumnExpression() != null && !sc.getColumnExpression().isEmpty()) {
               Vector exp = sc.getColumnExpression();
               if (exp.get(0) instanceof String) {
                  String longValue = exp.get(0).toString();
                  if (longValue.equals("10")) {
                     arguments.remove(0);
                  }
               }
            }
         }
      }

   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LOG");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      if (arguments.size() == 2) {
         this.functionName.setColumnName("LOG10");
         if (arguments.get(0) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)arguments.get(0);
            if (sc.getColumnExpression() != null && !sc.getColumnExpression().isEmpty()) {
               Vector exp = sc.getColumnExpression();
               if (exp.get(0) instanceof String) {
                  String longValue = exp.get(0).toString();
                  if (longValue.equals("10")) {
                     arguments.remove(0);
                  }
               }
            }
         }
      }

   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }

         String argumentValue = "(CASE WHEN " + arguments.get(i).toString() + " <= 0 THEN NULL ELSE " + arguments.get(i).toString() + " END)";
         arguments.set(i, argumentValue);
      }

      String qry;
      if (this.functionArguments.size() == 2) {
         qry = "(LN(" + arguments.get(1).toString() + ")/LN(" + arguments.get(0).toString() + "))";
      } else if (this.functionName.toString().equalsIgnoreCase("LOG2")) {
         qry = "(LN(" + arguments.get(0).toString() + ")/LN(2))";
      } else if (this.functionName.toString().equalsIgnoreCase("LOG10")) {
         qry = "LOG10(" + arguments.get(0).toString() + ")";
      } else {
         qry = "LN(" + arguments.get(0).toString() + ")";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            Vector colExpr = sc.getColumnExpression();
            if (colExpr.size() == 1 && colExpr.get(0) instanceof String) {
               this.functionName.setColumnName("");
               this.setOpenBracesForFunctionNameRequired(false);
               arguments.add(Math.log(Double.parseDouble(colExpr.get(0).toString())) + "");
            } else {
               if (this.functionName.getColumnName().equalsIgnoreCase("LOG")) {
                  throw new ConvertException("\nThe function LOG is not supported in TimesTen 5.1.21\n");
               }

               if (this.functionName.getColumnName().equalsIgnoreCase("LOG10")) {
                  throw new ConvertException("\nThe function LOG10 is not supported in TimesTen 5.1.21\n");
               }
            }
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LOG");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      if (arguments.size() == 2) {
         this.functionName.setColumnName("LOG");
         if (arguments.get(0) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)arguments.get(0);
            if (sc.getColumnExpression() != null && !sc.getColumnExpression().isEmpty()) {
               Vector exp = sc.getColumnExpression();
               if (exp.get(0) instanceof String) {
                  String longValue = exp.get(0).toString();
                  if (longValue.equals("10")) {
                     arguments.remove(0);
                  }
               }
            }
         }
      }

   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName().trim();
      String qry = "";
      Vector arguments = new Vector();
      String argumentValue = "";

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }

         argumentValue = "IF(" + arguments.get(i).toString() + "<=0, NULL, " + arguments.get(i).toString() + ")";
         arguments.set(i, argumentValue);
      }

      if (arguments.size() == 2) {
         qry = "(log(" + arguments.get(1).toString() + ") / log(" + arguments.get(0).toString() + "))";
      } else if (fnStr.equalsIgnoreCase("LOG10")) {
         qry = "(log(" + arguments.get(0).toString() + ") / log(10))";
      } else if (fnStr.equalsIgnoreCase("LOG2")) {
         qry = "(log(" + arguments.get(0).toString() + ") / log(2))";
      } else if (fnStr.equalsIgnoreCase("log")) {
         qry = "log(" + arguments.get(0).toString() + ")";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (this.functionArguments.size() == 2) {
         Vector temp = new Vector();
         temp.addElement(arguments.get(0));
         String argument = "CASE WHEN " + arguments.get(1).toString() + " <= 0 THEN NULL ELSE " + arguments.get(1).toString() + " END";
         arguments.set(0, argument);
         arguments.set(1, temp.get(0));
      } else {
         String argument = "CASE WHEN " + arguments.get(0).toString() + " <= 0 THEN NULL ELSE " + arguments.get(0).toString() + " END";
         arguments.set(0, argument);
      }

      if (this.functionName.toString().equalsIgnoreCase("LOG10")) {
         this.functionName.setColumnName("LOG10(" + arguments.get(0).toString() + ")");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("LOG2")) {
         this.functionName.setColumnName("LOG(" + arguments.get(0).toString() + ")/LOG(2)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }

   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (this.functionArguments.size() == 2) {
         Vector temp = new Vector();
         temp.addElement(arguments.get(0));
         String argument = "CASE WHEN " + arguments.get(1).toString() + " <= 0 THEN NULL ELSE " + arguments.get(1).toString() + " END";
         arguments.set(0, argument);
         arguments.set(1, temp.get(0));
      } else {
         String argument = "CASE WHEN " + arguments.get(0).toString() + " <= 0 THEN NULL ELSE " + arguments.get(0).toString() + " END";
         arguments.set(0, argument);
      }

      if (this.functionName.toString().equalsIgnoreCase("LOG10")) {
         this.functionName.setColumnName("LOG10(" + arguments.get(0).toString() + ")");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("LOG2")) {
         this.functionName.setColumnName("LOG2(" + arguments.get(0).toString() + ")");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }

   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }

         String argumentValue = "CASE WHEN " + arguments.get(i).toString() + " <= 0 THEN NULL ELSE " + arguments.get(i).toString() + " END";
         arguments.set(i, argumentValue);
      }

      if (this.functionName.toString().equalsIgnoreCase("LOG")) {
         this.setFunctionArguments(arguments);
      } else {
         Vector swaparguments;
         if (this.functionName.toString().equalsIgnoreCase("LOG2")) {
            swaparguments = new Vector();
            this.functionName.setColumnName("LOG");
            swaparguments.addElement(2);
            swaparguments.addElement(arguments.get(0));
            this.setFunctionArguments(swaparguments);
         } else if (this.functionName.toString().equalsIgnoreCase("LOG10")) {
            swaparguments = new Vector();
            this.functionName.setColumnName("LOG");
            swaparguments.addElement(10);
            swaparguments.addElement(arguments.get(0));
            this.setFunctionArguments(swaparguments);
         }
      }

   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }

         String argumentValue = "CASE WHEN " + arguments.get(i).toString() + " <= 0 THEN NULL ELSE " + arguments.get(i).toString() + " END";
         arguments.set(i, argumentValue);
      }

      if (this.functionName.toString().equalsIgnoreCase("LOG10")) {
         this.functionName.setColumnName("LOG10(" + arguments.get(0).toString() + ")");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionName.toString().equalsIgnoreCase("LOG2")) {
         this.functionName.setColumnName("LOG2(" + arguments.get(0).toString() + ")");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }

   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName().trim();
      String qry = "";
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (arguments.size() == 2) {
         qry = "(log(" + arguments.get(1).toString() + ") / log(" + arguments.get(0).toString() + "))";
      } else if (fnStr.equalsIgnoreCase("LOG10")) {
         qry = "(log(" + arguments.get(0).toString() + ") / log(10))";
      } else if (fnStr.equalsIgnoreCase("LOG2")) {
         qry = "(log(" + arguments.get(0).toString() + ") / log(2))";
      } else if (fnStr.equalsIgnoreCase("log")) {
         qry = "log(" + arguments.get(0).toString() + ")";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnName = this.functionName.getColumnName().trim();
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      String qry;
      if (this.functionArguments.size() == 2) {
         qry = "(LOG(" + arguments.get(1).toString() + ")/LOG(" + arguments.get(0).toString() + "))";
      } else if (fnName.equalsIgnoreCase("LOG2")) {
         qry = "(LOG(" + arguments.get(0).toString() + ")/LOG(2))";
      } else if (fnName.equalsIgnoreCase("LOG10")) {
         qry = "LOG10(" + arguments.get(0).toString() + ")";
      } else {
         qry = "LOG(" + arguments.get(0).toString() + ")";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }
}
