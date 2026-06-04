package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class rpad extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RPAD");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (from_sqs != null && from_sqs.isDenodo()) {
         String qry = "CASE WHEN (" + arguments.get(1) + " = 0 OR " + arguments.get(1) + " IS NULL or " + arguments.get(2) + " IS NULL) then NULL WHEN LEN(" + arguments.get(0) + ") > " + arguments.get(1) + " THEN SUBSTR(" + arguments.get(0) + ",1," + arguments.get(1) + ") ELSE (" + arguments.get(0) + " || SUBSTR(REPEAT(" + arguments.get(2) + "," + arguments.get(1) + "-LEN(" + arguments.get(0) + ")),1," + arguments.get(1) + "-LEN(" + arguments.get(0) + "))) END";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
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

      this.setFunctionArguments(arguments);
      if (from_sqs != null && from_sqs.isMSAzure()) {
         String qry = "SUBSTRING(CAST(" + arguments.get(0).toString() + " AS VARCHAR(MAX)) + REPLICATE(" + arguments.get(2).toString() + "," + arguments.get(1).toString() + "),1," + arguments.get(1).toString() + ")";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         Vector ramcoArguments;
         if (FunctionCalls.charToIntName) {
            ramcoArguments = this.ramcoRPADProcessing(arguments);
            if (ramcoArguments.size() < 3) {
               this.functionName.setColumnName("CONVERT");
               ramcoArguments.insertElementAt("VARCHAR", 0);
               this.setFunctionArguments(ramcoArguments);
            } else {
               Vector newArg = this.sqlServerConversion(this.functionName, this.functionArguments);
               this.setFunctionArguments(newArg);
            }
         } else {
            ramcoArguments = this.sqlServerConversion(this.functionName, this.functionArguments);
            this.setFunctionArguments(ramcoArguments);
         }

      }
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      Vector newArg = this.sqlServerConversion(this.functionName, this.functionArguments);
      this.setFunctionArguments(newArg);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RPAD");
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
      if (this.functionArguments.size() != 3) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in PostgreSQL\n Function Arguments Count Mismatch\n");
      } else {
         this.functionName.setColumnName("RPAD");
         Vector arguments = new Vector();
         boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
               if (i_count != 1) {
                  sc.convertSelectColumnToTextDataType();
               }

               arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         if (arguments.size() == 3) {
            String secArgument = arguments.get(1).toString();
            Integer number = StringFunctions.getIntegerValue(secArgument);
            if (number != null) {
               secArgument = number.toString();
            } else if (from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.numeric")) {
               secArgument = "TOINTEGER_UDF(" + secArgument + ")";
            } else {
               secArgument = "(" + secArgument + ")::integer";
            }

            arguments.set(1, secArgument);
         }

         this.setFunctionArguments(arguments);
      }
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RPAD");
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
      this.functionName.setColumnName("RPAD");
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
      this.functionName.setColumnName("RPAD");
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

      this.setFunctionArguments(arguments);
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RPAD");
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

   private Vector ramcoRPADProcessing(Vector functionArguments) {
      Vector ramcoArguments = new Vector();
      if (functionArguments != null) {
         for(int i = 0; i < functionArguments.size(); ++i) {
            if (functionArguments.get(i) instanceof TableColumn) {
               ramcoArguments.add(functionArguments.get(i));
            } else if (functionArguments.get(i) instanceof String) {
               if (((String)functionArguments.get(i)).trim().startsWith("@")) {
                  ramcoArguments.add(functionArguments.get(i));
               } else if (((String)functionArguments.get(i)).trim().startsWith("'")) {
                  ramcoArguments.add(functionArguments.get(i));
               }
            } else {
               Vector FunctionArgs;
               Vector tempVector;
               int j;
               if (functionArguments.get(i) instanceof SelectColumn) {
                  FunctionArgs = ((SelectColumn)functionArguments.get(i)).getColumnExpression();
                  tempVector = this.ramcoRPADProcessing(FunctionArgs);
                  if (tempVector != null) {
                     for(j = 0; j < tempVector.size(); ++j) {
                        ramcoArguments.add(tempVector.get(j));
                     }
                  }
               } else if (functionArguments.get(i) instanceof FunctionCalls) {
                  FunctionArgs = ((FunctionCalls)functionArguments.get(i)).getFunctionArguments();
                  tempVector = this.ramcoRPADProcessing(FunctionArgs);
                  if (tempVector != null) {
                     for(j = 0; j < tempVector.size(); ++j) {
                        ramcoArguments.add(tempVector.get(j));
                     }
                  }
               }
            }
         }
      }

      return ramcoArguments;
   }

   private Vector sqlServerConversion(TableColumn functionName, Vector functionArguments) {
      Vector newArg = new Vector();
      int noOfArguments = functionArguments.size();
      Object param1 = functionArguments.get(0);
      Object param2 = functionArguments.get(1);
      Object param3;
      if (noOfArguments > 2) {
         param3 = functionArguments.get(2);
      } else {
         param3 = new String("' '");
      }

      SelectColumn sc = new SelectColumn();
      FunctionCalls replicate = new FunctionCalls();
      FunctionCalls length1 = new FunctionCalls();
      FunctionCalls length2 = new FunctionCalls();
      FunctionCalls substring = new FunctionCalls();
      TableColumn replicateFunction = new TableColumn();
      TableColumn lengthFunction1 = new TableColumn();
      TableColumn lengthFunction2 = new TableColumn();
      TableColumn substringFunction = new TableColumn();
      replicateFunction.setOwnerName(functionName.getOwnerName());
      replicateFunction.setTableName(functionName.getTableName());
      replicateFunction.setColumnName("REPLICATE");
      Vector replicateArg = new Vector();
      replicateArg.add(param3);
      SelectColumn lengthArgSC = new SelectColumn();
      lengthFunction1.setOwnerName(functionName.getOwnerName());
      lengthFunction2.setOwnerName(functionName.getOwnerName());
      lengthFunction1.setTableName(functionName.getTableName());
      lengthFunction2.setTableName(functionName.getOwnerName());
      lengthFunction1.setColumnName("LEN");
      lengthFunction2.setColumnName("LEN");
      Vector lengthArg1 = new Vector();
      lengthArg1.add(param1);
      length1.setFunctionName(lengthFunction1);
      length1.setFunctionArguments(lengthArg1);
      Vector dummyArg = new Vector();
      dummyArg.add("(");
      dummyArg.add(param2);
      dummyArg.add(" - ");
      dummyArg.add(length1);
      lengthArgSC.setColumnExpression(dummyArg);
      replicateArg.add(lengthArgSC);
      replicate.setFunctionName(replicateFunction);
      replicate.setFunctionArguments(replicateArg);
      Vector lengthArg2 = new Vector();
      lengthArg2.add(param3);
      length2.setFunctionName(lengthFunction2);
      length2.setFunctionArguments(lengthArg2);
      substringFunction.setOwnerName(functionName.getOwnerName());
      substringFunction.setTableName(functionName.getTableName());
      substringFunction.setColumnName("SUBSTRING");
      Vector substringArg = new Vector();
      substringArg.add(param3);
      substringArg.add(" 1 ");
      Vector dummyArg1 = new Vector();
      dummyArg1.add(" ( ");
      dummyArg1.add(param2);
      dummyArg1.add(" - ");
      dummyArg1.add(length1);
      dummyArg1.add(" ) % ");
      dummyArg1.add(length2);
      dummyArg1.add(") ");
      SelectColumn lengthArgSC1 = new SelectColumn();
      lengthArgSC1.setColumnExpression(dummyArg1);
      substringArg.add(lengthArgSC1);
      substring.setFunctionName(substringFunction);
      substring.setFunctionArguments(substringArg);
      Vector colExp = new Vector();
      colExp.add(" ( ");
      colExp.add(param1);
      colExp.add(" + ");
      colExp.add(replicate);
      colExp.add(" / ");
      colExp.add(length2);
      colExp.add(" ) ");
      colExp.add(" + ");
      colExp.add(substring);
      sc.setColumnExpression(colExp);
      newArg.add(sc);
      return newArg;
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RPAD");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            if (i_count != 1) {
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
      this.functionName.setColumnName("RPAD");
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
      this.functionName.setColumnName("RPAD");
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
      this.functionName.setColumnName("RPAD");
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

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      String qry = "CASE WHEN LENGTH(" + arguments.get(0) + ") > " + arguments.get(1) + " THEN SUBSTR(" + arguments.get(0) + ",1," + arguments.get(1) + ") ELSE (" + arguments.get(0) + " || REPLACE(HEX(ZEROBLOB(" + arguments.get(1) + "-LENGTH(" + arguments.get(0) + "))),'00'," + arguments.get(2) + ")) END";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
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

      String qry = "Mid(CStr(" + arguments.get(0).toString() + ") + Replace(Space(" + arguments.get(1).toString() + "),' '," + arguments.get(2).toString() + "),1," + arguments.get(1).toString() + ")";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("RPAD");
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
