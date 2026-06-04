package com.adventnet.swissqlapi.sql.functions.aggregate;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WindowingClause;
import java.util.Vector;

public class variance extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("VAR")) {
         this.functionName.setColumnName("VARIANCE");
      } else if (!this.functionName.getColumnName().equalsIgnoreCase("VARP") && !this.functionName.getColumnName().equalsIgnoreCase("VARIANCE")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
            this.functionName.setColumnName("CORR");
         } else if (this.functionName.getColumnName().equalsIgnoreCase("COVARIANCE") || this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
            this.functionName.setColumnName("COVAR_POP");
         }
      } else {
         this.functionName.setColumnName("VAR_POP");
      }

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
      if (isdenodo) {
         String arg2;
         String arg1;
         if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
            arg1 = arguments.get(0).toString();
            arg2 = arguments.get(1).toString();
            this.functionName.setColumnName("((sum(" + arg1 + " * " + arg2 + ") - (sum(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " ELSE NULL END ) * 1.0 * sum(CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " ELSE NULL END) / count(CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END))) / sqrt(CAST((sum(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " * " + arg1 + " ELSE NULL END ) - power(sum(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " ELSE NULL END ), 2) / count(CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END)) * (sum(CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " * " + arg2 + " ELSE NULL END ) - power(sum(CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " ELSE NULL END ), 2) / count(CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END )) AS INT8)))");
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         } else if (this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
            arg1 = arguments.get(0).toString();
            arg2 = arguments.get(1).toString();
            this.functionName.setColumnName("( SUM( " + arg1 + " * " + arg2 + " ) - SUM(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " ELSE NULL END ) * 1.0 * SUM( CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " ELSE NULL END ) / COUNT( CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END ) ) / COUNT( CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END )");
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         }
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (!this.functionName.getColumnName().equalsIgnoreCase("VARP") && !this.functionName.getColumnName().equalsIgnoreCase("VAR_POP") && !this.functionName.getColumnName().equalsIgnoreCase("VARIANCE")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
            this.functionName.setColumnName("CORR");
         } else if (!this.functionName.getColumnName().equalsIgnoreCase("COVARIANCE") && !this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
            this.functionName.setColumnName("VAR");
         } else {
            this.functionName.setColumnName("COVAR_POP");
         }
      } else {
         this.functionName.setColumnName("VARP");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String arg2;
      String arg1;
      if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
         arg1 = arguments.get(0).toString();
         arg2 = arguments.get(1).toString();
         this.functionName.setColumnName("((sum(" + arg1 + " * " + arg2 + ") - (sum(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " ELSE NULL END ) * sum(CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " ELSE NULL END) / count(CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END))) / sqrt((sum(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " * " + arg1 + " ELSE NULL END ) - power(sum(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " ELSE NULL END ), 2.0) / count(CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END)) * (sum(CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " * " + arg2 + " ELSE NULL END ) - power(sum(CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " ELSE NULL END ), 2.0) / count(CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END ))))");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else if (this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
         arg1 = arguments.get(0).toString();
         arg2 = arguments.get(1).toString();
         this.functionName.setColumnName("( SUM( " + arg1 + " * " + arg2 + " ) - SUM(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " ELSE NULL END ) * SUM( CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " ELSE NULL END ) / COUNT( CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END ) ) / COUNT( CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END )");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         this.setFunctionArguments(arguments);
      }
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("VAR_SAMP")) {
         this.functionName.setColumnName("VAR_SAMP");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
         this.functionName.setColumnName("CORR");
      } else if (!this.functionName.getColumnName().equalsIgnoreCase("COVARIANCE") && !this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
         this.functionName.setColumnName("VAR_POP");
      } else {
         this.functionName.setColumnName("COVAR_POP");
      }

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
      this.functionName.setColumnName("VARIANCE");
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
      if (this.functionName.getColumnName().equalsIgnoreCase("VAR")) {
         this.functionName.setColumnName("VAR");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
         this.functionName.setColumnName("CORRELATION");
      } else if (!this.functionName.getColumnName().equalsIgnoreCase("COVARIANCE") && !this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
         this.functionName.setColumnName("VARIANCE");
      } else {
         this.functionName.setColumnName("COVARIANCE");
      }

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
      String fnName = this.functionName.getColumnName();
      if (fnName.equalsIgnoreCase("VAR_SAMP")) {
         this.functionName.setColumnName("VAR_SAMP");
      } else if (fnName.equalsIgnoreCase("CORR")) {
         this.functionName.setColumnName("CORR");
      } else if (!fnName.equalsIgnoreCase("COVARIANCE") && !fnName.equalsIgnoreCase("COVAR_POP")) {
         this.functionName.setColumnName("VAR_POP");
      } else {
         this.functionName.setColumnName("COVAR_POP");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            selColumn.convertWhereExpAloneInsideFunctionTo_IF_Function(this.functionArguments.size());
            arguments.addElement(selColumn.toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (from_sqs != null && from_sqs.isAmazonRedShift()) {
         String arg1;
         String arg2;
         if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
            arg1 = arguments.get(0).toString();
            arg2 = arguments.get(1).toString();
            this.functionName.setColumnName("((sum(" + arg1 + " * " + arg2 + ") - (sum(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " ELSE NULL END ) * sum(CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " ELSE NULL END) / nullif(count(CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END),0) )) / nullif(sqrt(case when ((sum(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " * " + arg1 + " ELSE NULL END ) - pow(sum(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " ELSE NULL END ), 2.0) / nullif(count(CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END),0) ) * (sum(CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " * " + arg2 + " ELSE NULL END ) - pow(sum(CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " ELSE NULL END ), 2.0) / nullif(count(CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END ),0) )) < 0 then null else ((sum(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " * " + arg1 + " ELSE NULL END ) - pow(sum(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " ELSE NULL END ), 2.0) / nullif(count(CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END),0) ) * (sum(CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " * " + arg2 + " ELSE NULL END ) - pow(sum(CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " ELSE NULL END ), 2.0) / nullif(count(CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END ),0) )) end),0) )");
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
            return;
         }

         if (this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
            arg1 = arguments.get(0).toString();
            arg2 = arguments.get(1).toString();
            this.functionName.setColumnName("( SUM( " + arg1 + " * " + arg2 + " ) - SUM(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " ELSE NULL END ) * SUM( CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " ELSE NULL END ) / COUNT( CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END ) ) / COUNT( CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END )");
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
            return;
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnName = this.functionName.getColumnName();
      if (!fnName.equalsIgnoreCase("VAR_SAMP") && !fnName.equalsIgnoreCase("VARIANCE_SAMP") && !fnName.equalsIgnoreCase("VARIANCE_SAMPLE")) {
         if (fnName.equalsIgnoreCase("CORR")) {
            this.functionName.setColumnName("CORR");
         } else if (!fnName.equalsIgnoreCase("COVARIANCE") && !fnName.equalsIgnoreCase("COVAR_POP")) {
            if (from_sqs != null && from_sqs.isHyperSql()) {
               this.functionName.setColumnName("VAR_POP");
            } else {
               this.functionName.setColumnName("VARIANCE");
            }
         } else {
            this.functionName.setColumnName("COVARIANCE");
         }
      } else {
         this.functionName.setColumnName("VAR_SAMP");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (from_sqs.isMySqlLive()) {
         String arg1;
         String qry;
         if (from_sqs.isMongoDb()) {
            if (this.functionName.getColumnName().equalsIgnoreCase("VARIANCE")) {
               qry = arguments.get(0).toString();
               arg1 = from_sqs.getFromClause().getFromItemList().get(0).toString();
               this.functionName.setColumnName("SUM((" + qry + " - (SELECT AVG(" + qry + ") FROM " + arg1 + "))*(" + qry + " - (SELECT AVG(" + qry + ") FROM " + arg1 + "))) / COUNT(" + qry + ")");
               this.setFunctionArguments(new Vector());
               this.setOpenBracesForFunctionNameRequired(false);
               return;
            }

            if (this.functionName.getColumnName().equalsIgnoreCase("VAR_SAMP")) {
               qry = arguments.get(0).toString();
               arg1 = from_sqs.getFromClause().getFromItemList().get(0).toString();
               this.functionName.setColumnName("SUM((" + qry + " - (SELECT AVG(" + qry + ") FROM " + arg1 + "))*(" + qry + " - (SELECT AVG(" + qry + ") FROM " + arg1 + "))) / (COUNT(" + qry + ") - 1)");
               this.setFunctionArguments(new Vector());
               this.setOpenBracesForFunctionNameRequired(false);
               return;
            }
         }

         if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
            qry = arguments.get(0).toString();
            arg1 = arguments.get(1).toString();
            this.functionName.setColumnName("((sum(" + qry + " * " + arg1 + ") - (sum(IF(" + arg1 + " is Not NULL, " + qry + ",NULL)) * sum(IF(" + qry + " is Not NULL ," + arg1 + ", NULL)) / count(IF( " + qry + " is Not NULL AND " + arg1 + " is Not NULL,  1 , NULL )))) / sqrt((sum(IF(" + arg1 + " is Not NULL," + qry + " * " + qry + ",NULL)) - power(sum(IF(" + arg1 + " is Not NULL, " + qry + ",NULL)), 2.0) / count(IF( " + qry + " is Not NULL AND " + arg1 + " is Not NULL,  1 , NULL ))) * (sum(IF(" + qry + " is Not NULL ," + arg1 + " * " + arg1 + ",NULL)) - power(sum(IF(" + qry + " is Not NULL ," + arg1 + ", NULL)), 2.0) / count(IF( " + qry + " is Not NULL AND " + arg1 + " is Not NULL,  1 , NULL )))))");
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
            return;
         }

         if (this.functionName.getColumnName().equalsIgnoreCase("COVARIANCE")) {
            arg1 = arguments.get(0).toString();
            String arg2 = arguments.get(1).toString();
            if (from_sqs.isHyperSql()) {
               qry = "( SUM( " + arg1 + " * " + arg2 + " ) - SUM(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " ELSE NULL END) * SUM( CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " ELSE NULL END) / COUNT( CASE WHEN  " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END) ) / COUNT( CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END)";
            } else {
               qry = "( SUM( " + arg1 + " * " + arg2 + " ) - SUM(IF(" + arg2 + " is Not NULL, " + arg1 + ",NULL) ) * SUM( IF(" + arg1 + " is Not NULL ," + arg2 + ", NULL) ) / COUNT( IF( " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL,  1 , NULL ) ) ) / COUNT( IF( " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL,  1 , NULL ) )";
            }

            this.functionName.setColumnName(qry);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
            return;
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("VARIANCE");
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
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String arg1;
      if (this.functionName.getColumnName().equalsIgnoreCase("VAR_SAMP")) {
         arg1 = from_sqs.getWhereExpression() != null ? " WHERE " + from_sqs.getWhereExpression() : "";
         qry = "(SUM((" + arguments.get(0) + " - (SELECT AVG(" + arguments.get(0) + ") " + from_sqs.getFromClause() + arg1 + "))*(" + arguments.get(0) + " - (SELECT AVG(" + arguments.get(0) + ") " + from_sqs.getFromClause() + arg1 + "))) / (COUNT(" + arguments.get(0) + ")-1))";
      } else {
         String arg2;
         if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
            arg1 = arguments.get(0).toString();
            arg2 = arguments.get(1).toString();
            qry = "((SUM(" + arg1 + "*" + arg2 + ") - ((SUM(CASE WHEN " + arg2 + " IS NOT NULL THEN " + arg1 + " ELSE NULL END) * SUM(CASE WHEN " + arg1 + " IS NOT NULL THEN " + arg2 + " ELSE NULL END)) / COUNT(CASE WHEN " + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL THEN 1 ELSE NULL END))) / SQRT((SUM(CASE WHEN " + arg2 + " IS NOT NULL THEN " + arg1 + "*" + arg1 + " ELSE NULL END) - POWER(SUM(CASE WHEN " + arg2 + " IS NOT NULL THEN " + arg1 + " ELSE NULL END),2.0) / COUNT(CASE WHEN " + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL THEN 1 ELSE NULL END))*(SUM(CASE WHEN " + arg1 + " IS NOT NULL THEN " + arg2 + "*" + arg2 + " ELSE NULL END) - POWER(SUM(CASE WHEN " + arg1 + " IS NOT NULL THEN " + arg2 + " ELSE NULL END),2.0) / COUNT(CASE WHEN " + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL THEN 1 ELSE NULL END))))";
         } else if (!this.functionName.getColumnName().equalsIgnoreCase("COVARIANCE") && !this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
            qry = "VARIANCE(" + arguments.get(0).toString() + ")";
         } else {
            arg1 = arguments.get(0).toString();
            arg2 = arguments.get(1).toString();
            qry = "(SUM(" + arg1 + " * " + arg2 + ") - SUM(CASE WHEN " + arg2 + " IS NOT NULL THEN " + arg1 + " ELSE NULL END) * SUM( CASE WHEN " + arg1 + " IS NOT NULL THEN " + arg2 + " ELSE NULL END) / COUNT(CASE WHEN " + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL THEN 1 ELSE NULL END)) / COUNT(CASE WHEN " + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL THEN 1 ELSE NULL END)";
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String firstArg = null;
      String secondArg = null;
      SelectColumn firstArgSelCol = null;
      SelectColumn secondArgSelCol = null;
      if (this.functionName.getColumnName().equalsIgnoreCase("VAR")) {
         this.functionName.setColumnName("VARIANCE");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("VARP")) {
         this.functionName.setColumnName("VAR_POP");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selCol = ((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs);
            arguments.addElement(selCol);
            if (i_count == 0) {
               firstArgSelCol = selCol;
               firstArg = selCol.toString();
            } else if (i_count == 1) {
               secondArgSelCol = selCol;
               secondArg = selCol.toString();
            }
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
            if (i_count == 0) {
               firstArg = this.functionArguments.elementAt(i_count).toString();
            } else if (i_count == 1) {
               secondArg = this.functionArguments.elementAt(i_count).toString();
            }
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionName.getColumnName().equalsIgnoreCase("CORR") || this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
         String target = "";
         if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
            target = "((sum(" + firstArg + "*" + secondArg + ")/count(" + firstArg + ")) - (( sum(" + firstArg + ")*sum(" + secondArg + "))/(count(" + firstArg + ")**2)))/(sqrt(var_pop(" + firstArg + ")*var_pop(" + secondArg + ")))  ";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
            target = "(sum(" + firstArg + "*" + secondArg + ")/count(" + firstArg + ")) - (( sum(" + firstArg + ")*sum(" + secondArg + "))/(count(" + firstArg + ")**2))";
         }

         Vector targetArg = new Vector();
         targetArg.add(target);
         this.setFunctionArguments(targetArg);
         this.setOver((String)null);
         this.setOrderBy((OrderByStatement)null);
         this.setPartitionBy((String)null);
         this.setWindowingClause((WindowingClause)null);
         this.functionName.setColumnName("");
         TableColumn firstArgTableCol = (TableColumn)this.getTableColumnFromSelectColumn(firstArgSelCol);
         TableColumn secondArgTableCol = (TableColumn)this.getTableColumnFromSelectColumn(secondArgSelCol);
         WhereExpression whereExp = this.createWhereExpression(firstArgTableCol, secondArgTableCol);
         WhereExpression existingWhereExpr = from_sqs.getWhereExpression();
         if (existingWhereExpr != null && firstArgTableCol != null && secondArgTableCol != null) {
            boolean setWhereExp = true;
            Vector functionColumnVector = existingWhereExpr.getFunctionColumnVector();
            Vector newTableColVector = new Vector();
            int functionColumnVectorSize = functionColumnVector.size();
            int i = 0;

            while(true) {
               if (i >= functionColumnVectorSize) {
                  if (setWhereExp) {
                     existingWhereExpr.addOperator("AND");
                     existingWhereExpr.addWhereExpression(whereExp);
                     newTableColVector.add(firstArgTableCol);
                     newTableColVector.add(secondArgTableCol);
                  }

                  if (newTableColVector.size() > 0) {
                     functionColumnVector.add(newTableColVector.get(0));
                     functionColumnVector.add(newTableColVector.get(1));
                     newTableColVector.clear();
                  }
                  break;
               }

               TableColumn funcTableCol = (TableColumn)functionColumnVector.get(i);
               if (funcTableCol.toString().equalsIgnoreCase(firstArgTableCol.toString()) || funcTableCol.toString().equalsIgnoreCase(secondArgTableCol.toString())) {
                  setWhereExp = false;
               }

               ++i;
            }
         } else {
            from_sqs.setWhereExpression(whereExp);
         }

         this.functionName.setColumnName("");
      }

   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("VAR_POP");
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

   private WhereExpression createWhereExpression(TableColumn firstCol, TableColumn secondCol) {
      WhereExpression whereExp = new WhereExpression();
      WhereItem firstArgItem = new WhereItem();
      WhereColumn firstArgColumn = new WhereColumn();
      Vector firstArgColExp = new Vector();
      firstArgColExp.add(firstCol);
      firstArgColumn.setColumnExpression(firstArgColExp);
      firstArgItem.setLeftWhereExp(firstArgColumn);
      firstArgItem.setOperator("IS NOT NULL");
      firstArgItem.setRightWhereExp((WhereColumn)null);
      WhereItem secondArgItem = new WhereItem();
      WhereColumn secondArgColumn = new WhereColumn();
      Vector secondArgColExp = new Vector();
      secondArgColExp.add(secondCol);
      secondArgColumn.setColumnExpression(secondArgColExp);
      secondArgItem.setLeftWhereExp(secondArgColumn);
      secondArgItem.setOperator("IS NOT NULL");
      secondArgItem.setRightWhereExp((WhereColumn)null);
      whereExp.addWhereItem(firstArgItem);
      whereExp.addOperator("AND");
      whereExp.addWhereItem(secondArgItem);
      return whereExp;
   }

   private Object getTableColumnFromSelectColumn(SelectColumn selCol) {
      Vector columnExpression = selCol.getColumnExpression();
      int colExpSize = columnExpression.size();

      for(int i = 0; i < colExpSize; ++i) {
         if (columnExpression.elementAt(i) instanceof TableColumn) {
            return columnExpression.elementAt(i);
         }

         if (columnExpression.elementAt(i) instanceof FunctionCalls) {
            FunctionCalls funcCall = (FunctionCalls)columnExpression.elementAt(i);
            Vector funcArgs = funcCall.getFunctionArguments();
            int funcArgsSize = funcArgs.size();

            for(int i_count = 0; i_count < funcArgsSize; ++i_count) {
               if (funcArgs.elementAt(i_count) instanceof SelectColumn) {
                  return this.getTableColumnFromSelectColumn((SelectColumn)funcArgs.elementAt(i_count));
               }
            }
         } else if (columnExpression.elementAt(i) instanceof SelectColumn) {
            return this.getTableColumnFromSelectColumn((SelectColumn)columnExpression.elementAt(i));
         }
      }

      return null;
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnName = this.functionName.getColumnName();
      if (fnName.equalsIgnoreCase("VAR_SAMP")) {
         this.functionName.setColumnName("VAR_SAMP");
      } else if (fnName.equalsIgnoreCase("CORR")) {
         this.functionName.setColumnName("CORR");
      } else if (!fnName.equalsIgnoreCase("COVARIANCE") && !fnName.equalsIgnoreCase("COVAR_POP")) {
         this.functionName.setColumnName("VAR_POP");
      } else {
         this.functionName.setColumnName("COVAR_POP");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            selColumn.convertWhereExpAloneInsideFunctionTo_IF_Function(this.functionArguments.size());
            arguments.addElement(selColumn.toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("VAR_SAMP")) {
         this.functionName.setColumnName("VAR_SAMP");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
         this.functionName.setColumnName("CORR");
      } else if (!this.functionName.getColumnName().equalsIgnoreCase("COVARIANCE") && !this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
         this.functionName.setColumnName("VAR_POP");
      } else {
         this.functionName.setColumnName("COVAR_POP");
      }

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
      if (this.functionName.getColumnName().equalsIgnoreCase("VAR_SAMP")) {
         this.functionName.setColumnName("VAR_SAMP");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
         this.functionName.setColumnName("CORR");
      } else if (!this.functionName.getColumnName().equalsIgnoreCase("COVARIANCE") && !this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
         this.functionName.setColumnName("VAR_POP");
      } else {
         this.functionName.setColumnName("COVAR_POP");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("VAR_SAMP")) {
         this.functionName.setColumnName("VAR_SAMP");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
         this.functionName.setColumnName("CORR");
      } else if (!this.functionName.getColumnName().equalsIgnoreCase("COVARIANCE") && !this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("VARIANCE")) {
            this.functionName.setColumnName("VAR");
         } else {
            this.functionName.setColumnName("VAR_POP");
         }
      } else {
         this.functionName.setColumnName("COVARIANCE");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("COVARIANCE")) {
         String arg1 = arguments.get(0).toString();
         String arg2 = arguments.get(1).toString();
         this.functionName.setColumnName("( SUM( " + arg1 + " * " + arg2 + " ) - SUM(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " ELSE NULL END) * SUM(CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " ELSE NULL END) / COUNT(CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END) ) / COUNT( CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN 1 ELSE NULL END)");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         this.setFunctionArguments(arguments);
      }
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

      String qry = "";
      if (this.functionName.getColumnName().equalsIgnoreCase("VAR_SAMP")) {
         qry = "variance(" + arguments.get(0) + ")";
      } else {
         String arg1;
         String arg2;
         if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
            arg1 = arguments.get(0).toString();
            arg2 = arguments.get(1).toString();
            qry = "((SUM(" + arg1 + "*" + arg2 + ") - ((SUM(CASE WHEN " + arg2 + " IS NOT NULL THEN " + arg1 + " ELSE NULL END) * SUM(CASE WHEN " + arg1 + " IS NOT NULL THEN " + arg2 + " ELSE NULL END)) / COUNT(CASE WHEN " + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL THEN 1 ELSE NULL END))) / SQRT((SUM(CASE WHEN " + arg2 + " IS NOT NULL THEN " + arg1 + "*" + arg1 + " ELSE NULL END) - POWER(SUM(CASE WHEN " + arg2 + " IS NOT NULL THEN " + arg1 + " ELSE NULL END),2.0) / COUNT(CASE WHEN " + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL THEN 1 ELSE NULL END))*(SUM(CASE WHEN " + arg1 + " IS NOT NULL THEN " + arg2 + "*" + arg2 + " ELSE NULL END) - POWER(SUM(CASE WHEN " + arg1 + " IS NOT NULL THEN " + arg2 + " ELSE NULL END),2.0) / COUNT(CASE WHEN " + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL THEN 1 ELSE NULL END))))";
         } else if (!this.functionName.getColumnName().equalsIgnoreCase("COVARIANCE") && !this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
            arg1 = from_sqs.getWhereExpression() != null ? " WHERE " + from_sqs.getWhereExpression() : "";
            qry = "SUM((" + arguments.get(0) + " - (SELECT AVG(" + arguments.get(0) + ") " + from_sqs.getFromClause() + arg1 + "))*(" + arguments.get(0) + " - (SELECT AVG(" + arguments.get(0) + ") " + from_sqs.getFromClause() + arg1 + "))) / COUNT(" + arguments.get(0) + ")";
         } else {
            arg1 = arguments.get(0).toString();
            arg2 = arguments.get(1).toString();
            qry = "(SUM(" + arg1 + " * " + arg2 + ") - SUM(CASE WHEN " + arg2 + " IS NOT NULL THEN " + arg1 + " ELSE NULL END) * SUM( CASE WHEN " + arg1 + " IS NOT NULL THEN " + arg2 + " ELSE NULL END) / COUNT(CASE WHEN " + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL THEN 1 ELSE NULL END)) / COUNT(CASE WHEN " + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL THEN 1 ELSE NULL END)";
         }
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      if (this.functionName.getColumnName().equalsIgnoreCase("VAR_SAMP")) {
         this.functionName.setColumnName("Var");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("VARIANCE")) {
         this.functionName.setColumnName("Var");
      } else {
         this.functionName.setColumnName("VarP");
      }

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String arg2;
      String arg1;
      if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
         arg1 = arguments.get(0).toString();
         arg2 = arguments.get(1).toString();
         this.functionName.setColumnName("((SUM(" + arg1 + "*" + arg2 + ") - ((SUM(Iif(" + arg2 + " IS NOT NULL ," + arg1 + ",NULL)) * SUM(Iif(" + arg1 + " IS NOT NULL," + arg2 + ",NULL))) / COUNT(Iif(" + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL,1 ,NULL)))) / SQR((SUM(Iif(" + arg2 + " IS NOT NULL," + arg1 + "*" + arg1 + ", NULL)) - (SUM(Iif(" + arg2 + " IS NOT NULL," + arg1 + " ,NULL)) ^ (2.0)) / COUNT(Iif(" + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL,1 ,NULL)))*(SUM(Iif(" + arg1 + " IS NOT NULL ," + arg2 + "*" + arg2 + " ,NULL)) - (SUM(Iif( " + arg1 + " IS NOT NULL ," + arg2 + ",NULL)) ^ (2.0)) / COUNT(Iif(" + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL,1,NULL)))))");
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      } else if (!this.functionName.getColumnName().equalsIgnoreCase("COVARIANCE") && !this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
         this.setFunctionArguments(arguments);
      } else {
         arg1 = arguments.get(0).toString();
         arg2 = arguments.get(1).toString();
         this.functionName.setColumnName("(SUM(" + arg1 + " * " + arg2 + ") - SUM(Iif(" + arg2 + " IS NOT NULL ," + arg1 + " ,NULL)) * SUM( Iif(" + arg1 + " IS NOT NULL ," + arg2 + ",NULL)) / COUNT(Iif(" + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL ,1,NULL))) / COUNT(Iif(" + arg1 + " IS NOT NULL AND " + arg2 + " IS NOT NULL,1,NULL))");
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnName = this.functionName.getColumnName();
      if (!fnName.equalsIgnoreCase("VAR_SAMP") && !fnName.equalsIgnoreCase("VARIANCE_SAMP") && !fnName.equalsIgnoreCase("VARIANCE_SAMPLE")) {
         if (fnName.equalsIgnoreCase("CORR")) {
            this.functionName.setColumnName("CORR");
         } else if (!fnName.equalsIgnoreCase("COVARIANCE") && !fnName.equalsIgnoreCase("COVAR_POP")) {
            this.functionName.setColumnName("VAR_POP");
         } else {
            this.functionName.setColumnName("COVARIANCE");
         }
      } else {
         this.functionName.setColumnName("VAR_SAMP");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String arg1;
      String qry;
      if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
         qry = arguments.get(0).toString();
         arg1 = arguments.get(1).toString();
         this.functionName.setColumnName("((sum(" + qry + " * " + arg1 + ") - (sum((CASE WHEN " + arg1 + " is Not NULL THEN " + qry + " ELSE NULL END)) * sum((CASE WHEN " + qry + " is Not NULL THEN " + arg1 + " ELSE NULL END)) / count((CASE WHEN " + qry + " is Not NULL AND " + arg1 + " is Not NULL THEN  1 ELSE NULL END)))) / sqrt((sum((CASE WHEN " + arg1 + " is Not NULL THEN " + qry + " * " + qry + " ELSE NULL END)) - power(sum((CASE WHEN " + arg1 + " is Not NULL THEN " + qry + " ELSE NULL END)), 2.0) / count((CASE WHEN " + qry + " is Not NULL AND " + arg1 + " is Not NULL THEN  1 ELSE NULL END))) * (sum((CASE WHEN " + qry + " is Not NULL THEN " + arg1 + " * " + arg1 + " ELSE NULL END)) - power(sum((CASE WHEN " + qry + " is Not NULL THEN " + arg1 + " ELSE NULL END)), 2.0) / count((CASE WHEN " + qry + " is Not NULL AND " + arg1 + " is Not NULL THEN 1 ELSE NULL END)))))");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else if (this.functionName.getColumnName().equalsIgnoreCase("COVARIANCE")) {
         arg1 = arguments.get(0).toString();
         String arg2 = arguments.get(1).toString();
         qry = "( SUM( " + arg1 + " * " + arg2 + " ) - SUM(CASE WHEN " + arg2 + " is Not NULL THEN " + arg1 + " ELSE NULL END) * SUM( CASE WHEN " + arg1 + " is Not NULL THEN " + arg2 + " ELSE NULL END) / COUNT( CASE WHEN  " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END) ) / COUNT( CASE WHEN " + arg1 + " is Not NULL AND " + arg2 + " is Not NULL THEN  1 ELSE NULL END)";
         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         this.setFunctionArguments(arguments);
      }
   }
}
