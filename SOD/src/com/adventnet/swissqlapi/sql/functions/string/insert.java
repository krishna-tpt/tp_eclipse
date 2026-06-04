package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class insert extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 4) {
         throw new ConvertException("\nGiven datatype " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in PostgreSQL\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LENGTH(" + arguments.get(0).toString() + ") THEN CAST(" + arguments.get(0).toString() + " AS TEXT) ELSE overlay(CAST(" + arguments.get(0) + " AS TEXT) placing CAST(" + arguments.get(3) + " AS TEXT) from CAST(" + arguments.get(1) + " AS TEXT) for CAST(" + arguments.get(2) + " AS TEXT)) END";
         if (from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb())) {
            qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LENGTH(" + arguments.get(0).toString() + ") THEN " + arguments.get(0).toString() + " ELSE (substring(" + arguments.get(0).toString() + ",1," + arguments.get(1).toString() + "-1) || " + arguments.get(3).toString() + " || substring(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "+" + arguments.get(2).toString() + ")) END";
         } else if (from_sqs != null && from_sqs.getBooleanValues("use.udf.functions.for.text")) {
            qry = "overlay(CAST(" + arguments.get(0) + " AS TEXT) placing CAST(" + arguments.get(3) + " AS TEXT) from CAST(tointeger_udf(" + arguments.get(1) + ") AS BIGINT) for CAST(tointeger_udf(" + arguments.get(2) + ") AS BIGINT))";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LEN(" + arguments.get(0).toString() + ") THEN " + arguments.get(0).toString() + " ELSE (substring(" + arguments.get(0).toString() + ",1," + arguments.get(1).toString() + "-1) + " + arguments.get(3).toString() + " + substring(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "+" + arguments.get(2).toString() + ",LEN(" + arguments.get(0).toString() + ")))END";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
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

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LENGTH(CAST(" + arguments.get(0).toString() + " AS VARCHAR)) THEN CAST(" + arguments.get(0).toString() + " AS VARCHAR) ELSE (CONCAT(substring(CAST(" + arguments.get(0).toString() + " AS VARCHAR),1,((" + arguments.get(1).toString() + ")-1)) , CAST(" + arguments.get(3).toString() + " AS VARCHAR) , substring(CAST(" + arguments.get(0).toString() + " AS VARCHAR)," + arguments.get(1).toString() + "+" + arguments.get(2).toString() + ",LENGTH(CAST(" + arguments.get(0).toString() + " AS VARCHAR))))) END";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String lengthSyntax = isdenodo ? "LEN" : "LENGTH";
      String qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > " + lengthSyntax + "(" + arguments.get(0).toString() + ") THEN " + arguments.get(0).toString() + " ELSE (substr(" + arguments.get(0).toString() + ",1," + arguments.get(1).toString() + "-1) || " + arguments.get(3).toString() + " || substr(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "+" + arguments.get(2).toString() + "," + lengthSyntax + "(" + arguments.get(0).toString() + "))) END";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 4) {
         throw new ConvertException("\nGiven datatype " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in BigQuery\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LENGTH(" + arguments.get(0).toString() + ") THEN " + arguments.get(0).toString() + " ELSE (substr(" + arguments.get(0).toString() + ",1," + arguments.get(1).toString() + "-1) || " + arguments.get(3).toString() + " || substr(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "+" + arguments.get(2).toString() + ")) END";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 4) {
         throw new ConvertException("\nGiven datatype " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in Athena\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LENGTH(" + arguments.get(0).toString() + ") THEN " + arguments.get(0).toString() + " ELSE (substr(" + arguments.get(0).toString() + ",1," + arguments.get(1).toString() + "-1) || " + arguments.get(3).toString() + " || substr(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "+" + arguments.get(2).toString() + ")) END";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("insert");
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

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 4) {
         throw new ConvertException("\nGiven datatype " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in SapHana\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LENGTH(" + arguments.get(0).toString() + ") THEN " + arguments.get(0).toString() + " ELSE (SUBSTRING(" + arguments.get(0).toString() + ",1," + arguments.get(1).toString() + "-1) || " + arguments.get(3).toString() + " || SUBSTRING(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "+" + arguments.get(2).toString() + ")) END";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 4) {
         throw new ConvertException("\nGiven datatype " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in Sqlite\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LENGTH(" + arguments.get(0).toString() + ") THEN " + arguments.get(0).toString() + " ELSE (SUBSTR(" + arguments.get(0).toString() + ",1," + arguments.get(1).toString() + "-1) || " + arguments.get(3).toString() + " || SUBSTR(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "+" + arguments.get(2).toString() + ")) END";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 4) {
         throw new ConvertException("\nGiven datatype " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in Informix\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LENGTH(" + arguments.get(0).toString() + ") THEN " + arguments.get(0).toString() + " ELSE (SUBSTR(" + arguments.get(0).toString() + ",1," + arguments.get(1).toString() + "-1) || " + arguments.get(3).toString() + " || SUBSTR(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "+" + arguments.get(2).toString() + ")) END";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 4) {
         throw new ConvertException("\nGiven datatype " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported.\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "Iif( " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LEN(" + arguments.get(0).toString() + ") , " + arguments.get(0).toString() + " , (Mid(" + arguments.get(0).toString() + ",1," + arguments.get(1).toString() + "-1) & " + arguments.get(3).toString() + " & Mid(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "+" + arguments.get(2).toString() + ")) )";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("insert");
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
