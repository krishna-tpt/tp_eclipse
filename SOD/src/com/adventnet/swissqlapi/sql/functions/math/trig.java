package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhenStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class trig extends FunctionCalls {
   CaseStatement caseStatement = null;

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.getColumnName().toUpperCase();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("ASIN") || this.functionName.getColumnName().equalsIgnoreCase("ACOS")) {
         String argumentsNew = "CASE WHEN " + arguments.get(0).toString() + " BETWEEN -1 AND 1 THEN " + arguments.get(0).toString() + " ELSE NULL END";
         arguments = new Vector();
         arguments.add(argumentsNew);
      }

      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.getColumnName().toUpperCase();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (from_sqs != null && from_sqs.isMSAzure() && (this.functionName.getColumnName().equalsIgnoreCase("ASIN") || this.functionName.getColumnName().equalsIgnoreCase("ACOS"))) {
         String argumentsNew = "CASE WHEN " + arguments.get(0).toString() + " BETWEEN -1 AND 1 THEN " + arguments.get(0).toString() + " ELSE NULL END";
         arguments = new Vector();
         arguments.add(argumentsNew);
      }

      this.setFunctionArguments(arguments);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.getColumnName().toUpperCase();
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
      this.functionName.getColumnName().toUpperCase();
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
      this.functionName.getColumnName().toUpperCase();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("ASIN") || this.functionName.getColumnName().equalsIgnoreCase("ACOS")) {
         String argumentsNew = "CASE WHEN " + arguments.get(0).toString() + " BETWEEN -1 AND 1 THEN " + arguments.get(0).toString() + " ELSE NULL END";
         arguments = new Vector();
         arguments.add(argumentsNew);
      }

      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.getColumnName().toUpperCase();
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

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.getColumnName().toUpperCase();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("ASIN") || this.functionName.getColumnName().equalsIgnoreCase("ACOS")) {
         String argumentsNew = "CASE WHEN " + arguments.get(0).toString() + " BETWEEN -1 AND 1 THEN " + arguments.get(0).toString() + " ELSE NULL END";
         arguments = new Vector();
         arguments.add(argumentsNew);
      }

      this.setFunctionArguments(arguments);
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.getColumnName().toUpperCase();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("ASIN") || this.functionName.getColumnName().equalsIgnoreCase("ACOS")) {
         String argumentsNew = "CASE WHEN " + arguments.get(0).toString() + " BETWEEN -1 AND 1 THEN " + arguments.get(0).toString() + " ELSE NULL END";
         arguments = new Vector();
         arguments.add(argumentsNew);
      }

      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.getColumnName().toUpperCase();
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
      this.functionName.getColumnName().toUpperCase();
      Vector arguments = new Vector();
      if (this.functionName.getColumnName().equalsIgnoreCase("SIGN")) {
         this.caseStatement = new CaseStatement();
         this.caseStatement.setCaseClause("CASE");
         Vector whenStmtList = new Vector();

         for(int i = 0; i < 2; ++i) {
            WhenStatement when_statement = new WhenStatement();
            when_statement.setWhenClause("WHEN");
            when_statement.setThenClause("THEN");
            WhereItem wi = new WhereItem();
            WhereColumn wc = new WhereColumn();
            Vector wcColExp = new Vector();
            wcColExp.add(((SelectColumn)this.functionArguments.elementAt(0)).toTeradataSelect(to_sqs, from_sqs));
            wc.setColumnExpression(wcColExp);
            wi.setLeftWhereExp(wc);
            if (i == 0) {
               wi.setOperator("<");
            } else {
               wi.setOperator(">");
            }

            WhereColumn rwc = new WhereColumn();
            Vector rwcColExp = new Vector();
            rwcColExp.add("0");
            rwc.setColumnExpression(rwcColExp);
            wi.setRightWhereExp(rwc);
            WhereExpression we = new WhereExpression();
            we.addWhereItem(wi);
            when_statement.setWhenCondition(we);
            SelectColumn thenSelectColumn = new SelectColumn();
            Vector thenSelectColumnExpr = new Vector();
            if (i == 0) {
               thenSelectColumnExpr.add("-1");
            } else {
               thenSelectColumnExpr.add("1");
            }

            thenSelectColumn.setColumnExpression(thenSelectColumnExpr);
            when_statement.setThenStatement(thenSelectColumn);
            whenStmtList.add(when_statement);
         }

         this.caseStatement.setWhenStatementList(whenStmtList);
         this.caseStatement.setElseClause("ELSE");
         SelectColumn elseSelectColumn = new SelectColumn();
         Vector elseSelectColumnExpr = new Vector();
         elseSelectColumnExpr.add("0");
         elseSelectColumn.setColumnExpression(elseSelectColumnExpr);
         this.caseStatement.setElseStatement(elseSelectColumn);
         this.caseStatement.setEndClause("END");
         this.functionName = null;
         this.argumentQualifier = null;
         this.functionArguments = null;
      } else {
         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
      }

   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.getColumnName().toUpperCase();
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
      Vector arguments = new Vector();
      if (this.functionName.getColumnName().equalsIgnoreCase("SIGN")) {
         Vector colExpr = ((SelectColumn)this.functionArguments.get(0)).getColumnExpression();
         if (colExpr.size() == 1 && colExpr.get(0) instanceof String) {
            this.functionName.setColumnName("");
            this.setOpenBracesForFunctionNameRequired(false);
            String str = colExpr.get(0).toString();
            int value = Integer.parseInt(str);
            if (value > 0) {
               arguments.add("1");
            } else {
               arguments.add("0");
            }

            this.setFunctionArguments(arguments);
         } else {
            if (colExpr.size() != 2) {
               throw new ConvertException("\nThe function SIGN is not supported in TimesTen 5.1.21\n");
            }

            this.functionName.setColumnName("");
            this.setOpenBracesForFunctionNameRequired(false);
            arguments.add("-1");
            this.setFunctionArguments(arguments);
         }
      } else {
         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
               throw new ConvertException("\nThe built-in function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
            }

            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            Vector colExpr = sc.getColumnExpression();
            if (colExpr.size() != 1 || !(colExpr.get(0) instanceof String)) {
               throw new ConvertException("\nThe built-in function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
            }

            if (!this.functionName.getColumnName().equalsIgnoreCase("ATAN") && !this.functionName.getColumnName().equalsIgnoreCase("ATN2")) {
               if (this.functionName.getColumnName().equalsIgnoreCase("ASIN")) {
                  arguments.add(Math.asin(Double.parseDouble(colExpr.get(0).toString())) + "");
               } else if (this.functionName.getColumnName().equalsIgnoreCase("ACOS")) {
                  arguments.add(Math.acos(Double.parseDouble(colExpr.get(0).toString())) + "");
               } else if (this.functionName.getColumnName().equalsIgnoreCase("SIN")) {
                  arguments.add(Math.sin(Double.parseDouble(colExpr.get(0).toString())) + "");
               } else if (this.functionName.getColumnName().equalsIgnoreCase("COS")) {
                  arguments.add(Math.cos(Double.parseDouble(colExpr.get(0).toString())) + "");
               } else if (this.functionName.getColumnName().equalsIgnoreCase("TAN")) {
                  arguments.add(Math.tan(Double.parseDouble(colExpr.get(0).toString())) + "");
               }
            } else {
               arguments.add(Math.atan(Double.parseDouble(colExpr.get(0).toString())) + "");
            }

            this.functionName.setColumnName("");
            this.setFunctionArguments(arguments);
            this.setOpenBracesForFunctionNameRequired(false);
         }
      }

   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.getColumnName().toUpperCase();
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

   public String toString() {
      return this.caseStatement != null ? this.caseStatement.toString() : super.toString();
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("sin")) {
         this.functionName.setColumnName("SIN");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("cos")) {
         this.functionName.setColumnName("COS");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("tan")) {
         this.functionName.setColumnName("TAN");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("asin")) {
         this.functionName.setColumnName("ASIN");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("acos")) {
         this.functionName.setColumnName("ACOS");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("atan")) {
         this.functionName.setColumnName("ATAN");
      } else {
         if (!this.functionName.getColumnName().equalsIgnoreCase("sign")) {
            throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
         }

         this.functionName.setColumnName("SIGN");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("ASIN") || this.functionName.getColumnName().equalsIgnoreCase("ACOS")) {
         String argumentsNew = "CASE WHEN " + arguments.get(0).toString() + " BETWEEN -1 AND 1 THEN " + arguments.get(0).toString() + " ELSE NULL END";
         arguments = new Vector();
         arguments.add(argumentsNew);
      }

      this.setFunctionArguments(arguments);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("ASIN") || this.functionName.getColumnName().equalsIgnoreCase("ACOS")) {
         String argumentsNew = "CASE WHEN " + arguments.get(0).toString() + " BETWEEN -1 AND 1 THEN " + arguments.get(0).toString() + " ELSE NULL END";
         arguments = new Vector();
         arguments.add(argumentsNew);
      }

      this.setFunctionArguments(arguments);
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

      if (this.functionName.getColumnName().equalsIgnoreCase("ASIN") || this.functionName.getColumnName().equalsIgnoreCase("ACOS")) {
         String argumentsNew = "CASE WHEN " + arguments.get(0).toString() + " BETWEEN -1 AND 1 THEN " + arguments.get(0).toString() + " ELSE NULL END";
         arguments = new Vector();
         arguments.add(argumentsNew);
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

      if (this.functionName.getColumnName().equalsIgnoreCase("ASIN") || this.functionName.getColumnName().equalsIgnoreCase("ACOS")) {
         String argumentsNew = "CASE WHEN " + arguments.get(0).toString() + " BETWEEN -1 AND 1 THEN " + arguments.get(0).toString() + " ELSE NULL END";
         arguments = new Vector();
         arguments.add(argumentsNew);
      }

      this.setFunctionArguments(arguments);
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      if (this.functionName.getColumnName().equalsIgnoreCase("ATAN")) {
         this.functionName.setColumnName("Atn");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("SIGN")) {
         this.functionName.setColumnName("Sgn");
      }

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry;
      if (this.functionName.getColumnName().equalsIgnoreCase("ASIN")) {
         qry = "Iif(" + arguments.get(0) + " < -1 Or " + arguments.get(0) + " > 1,null,Atn(" + arguments.get(0) + " / Sqr(-" + arguments.get(0) + " * " + arguments.get(0) + " + 1)))";
         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else if (this.functionName.getColumnName().equalsIgnoreCase("ACOS")) {
         qry = "Iif(" + arguments.get(0) + " < -1 Or " + arguments.get(0) + " > 1,null,Atn(-" + arguments.get(0) + " / Sqr(-" + arguments.get(0) + " * " + arguments.get(0) + " + 1)) + 2 * Atn(1))";
         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         this.setFunctionArguments(arguments);
      }

   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.getColumnName().toUpperCase();
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
