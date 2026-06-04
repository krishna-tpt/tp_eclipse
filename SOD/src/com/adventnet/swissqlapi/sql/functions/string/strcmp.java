package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class strcmp extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 2) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in PostgreSQL\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String dataType = from_sqs != null && from_sqs.isVerticaDb() ? "VARCHAR" : "TEXT";
         String qry = "( case when CAST(" + arguments.get(0) + " AS " + dataType + ") < CAST(" + arguments.get(1) + " AS " + dataType + ") THEN -1 WHEN CAST(" + arguments.get(0) + " AS " + dataType + ") > CAST(" + arguments.get(1) + " AS " + dataType + ") THEN 1 ELSE 0 END )  ";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 2) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in MSSQL\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "( case when " + arguments.get(0) + " < " + arguments.get(1) + " THEN -1 WHEN " + arguments.get(0) + " > " + arguments.get(1) + " THEN 1 ELSE 0 END )  ";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 2) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in Snowflake\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "( case when " + arguments.get(0) + " < " + arguments.get(1) + " THEN -1 WHEN " + arguments.get(0) + " > " + arguments.get(1) + " THEN 1 ELSE 0 END )  ";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 2) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in Vectorwise\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "( case when CAST(" + arguments.get(0) + " AS VARCHAR) < CAST(" + arguments.get(1) + " AS VARCHAR) THEN -1 WHEN CAST(" + arguments.get(0) + " AS VARCHAR) > CAST(" + arguments.get(1) + " AS VARCHAR) THEN 1 ELSE 0 END )  ";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 2) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in Oracle\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "( case when " + arguments.get(0) + " < " + arguments.get(1) + " THEN -1 WHEN " + arguments.get(0) + " > " + arguments.get(1) + " THEN 1 ELSE 0 END )  ";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 2) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in BigQuery\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "( case when CAST(" + arguments.get(0) + " AS STRING) < CAST(" + arguments.get(1) + " AS STRING) THEN -1 WHEN CAST(" + arguments.get(0) + " AS STRING) > CAST(" + arguments.get(1) + " AS STRING) THEN 1 ELSE 0 END )  ";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 2) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in DB2\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "( case when " + arguments.get(0) + " < " + arguments.get(1) + " THEN -1 WHEN " + arguments.get(0) + " > " + arguments.get(1) + " THEN 1 ELSE 0 END )  ";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("strcmp");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      String equal;
      if (from_sqs != null && from_sqs.isMongoDb()) {
         this.functionName.setColumnName("");
         arguments = new Vector();
         equal = "";
         String lessThan = "";

         for(int i_count = 0; i_count < this.functionArguments.size() - 1; ++i_count) {
            equal = this.functionArguments.elementAt(i_count).toString() + " = " + this.functionArguments.elementAt(i_count + 1);
            lessThan = this.functionArguments.elementAt(i_count).toString() + " < " + this.functionArguments.elementAt(i_count + 1);
         }

         String qry = "IF(" + equal + ",0,IF(" + lessThan + ",-1,1))";
         arguments.addElement(qry);
         this.setFunctionArguments(arguments);
      } else if (from_sqs != null && from_sqs.isHyperSql()) {
         equal = "( CASE WHEN CAST(" + arguments.get(0) + " AS LONGVARCHAR) < CAST(" + arguments.get(1) + " AS LONGVARCHAR) THEN -1 WHEN CAST(" + arguments.get(0) + " AS LONGVARCHAR) > CAST(" + arguments.get(1) + " AS LONGVARCHAR) THEN 1 ELSE 0 END )  ";
         this.functionName.setColumnName(equal);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 2) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in Athena\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "( case when CAST(" + arguments.get(0) + " AS VARCHAR) < CAST(" + arguments.get(1) + " AS VARCHAR) THEN -1 WHEN CAST(" + arguments.get(0) + " AS VARCHAR) > CAST(" + arguments.get(1) + " AS VARCHAR) THEN 1 ELSE 0 END )  ";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 2) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in SapHana\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "( case when CAST(" + arguments.get(0) + " AS VARCHAR) < CAST(" + arguments.get(1) + " AS VARCHAR) THEN -1 WHEN CAST(" + arguments.get(0) + " AS VARCHAR) > CAST(" + arguments.get(1) + " AS VARCHAR) THEN 1 ELSE 0 END )";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 2) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in Sqlite\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "( case when CAST(" + arguments.get(0) + " AS TEXT) < CAST(" + arguments.get(1) + " AS TEXT) THEN -1 WHEN CAST(" + arguments.get(0) + " AS TEXT) > CAST(" + arguments.get(1) + " AS TEXT) THEN 1 ELSE 0 END )";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 2) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in Informix\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "( CASE WHEN " + arguments.get(0) + " < " + arguments.get(1) + " THEN -1 WHEN " + arguments.get(0) + " > " + arguments.get(1) + " THEN 1 ELSE 0 END )  ";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 2) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported.\n Function Arguments Count Mismatch\n");
      } else {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String qry = "StrComp(" + arguments.get(0) + "," + arguments.get(1) + ",1)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "( CASE WHEN CAST(" + arguments.get(0) + " AS LONGVARCHAR) < CAST(" + arguments.get(1) + " AS LONGVARCHAR) THEN -1 WHEN CAST(" + arguments.get(0) + " AS LONGVARCHAR) > CAST(" + arguments.get(1) + " AS LONGVARCHAR) THEN 1 ELSE 0 END )  ";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }
}
