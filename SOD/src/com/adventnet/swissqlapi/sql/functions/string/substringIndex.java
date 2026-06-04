package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class substringIndex extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("substring_index");
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            if (i_count != 2) {
               sc.convertSelectColumnToTextDataType();
            }

            arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 3) {
         String thirdArg = arguments.get(2).toString() + "::integer";
         Integer number = StringFunctions.getIntegerValue(arguments.get(2).toString());
         String delimiter = arguments.get(1).toString();
         if (from_sqs != null && isPostgreLiveDbs) {
            String qry = "";
            if (from_sqs.isVerticaDb()) {
               qry = "(CASE WHEN Instr(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ", 1,ABS(" + arguments.get(2).toString() + "))>0 THEN (CASE WHEN " + arguments.get(2).toString() + ">0 THEN SUBSTR(" + arguments.get(0).toString() + ", 1, Instr(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ", 1,ABS(" + arguments.get(2).toString() + ")) -1) ELSE SUBSTR(" + arguments.get(0).toString() + ",Instr(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + ", -1,ABS(" + arguments.get(2).toString() + ")) +1) END) ELSE " + arguments.get(0).toString() + " END)";
            } else if (from_sqs.isPgsqlLive()) {
               qry = "CASE WHEN " + arguments.get(2).toString() + " > 0 THEN array_to_string((string_to_array(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "))[0:" + arguments.get(2).toString() + "]," + arguments.get(1).toString() + ") ELSE array_to_string((string_to_array(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "))[array_length(string_to_array(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "),1)+" + arguments.get(2).toString() + "+1:array_length(string_to_array(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "),1)]," + arguments.get(1).toString() + ") END";
            } else {
               if (number == null) {
                  throw new ConvertException("Delimiter_count should be integer value. Column is not supported.");
               }

               if (number > 10) {
                  throw new ConvertException("Delimiter_count should be less than 10 to prevent performance issue.");
               }

               String firstarg = arguments.get(0).toString();
               boolean negative = false;
               if (number < 0) {
                  number = number * -1;
                  arguments.set(0, " REVERSE( " + arguments.get(0).toString() + " )");
                  delimiter = "REVERSE(" + delimiter + ")";
                  negative = true;
               }

               for(int postion = 1; postion <= number; ++postion) {
                  if (postion > 1) {
                     qry = qry.concat(" || " + delimiter + " || ");
                  }

                  qry = qry.concat(" SPLIT_PART(" + arguments.get(0).toString() + "," + delimiter + "," + postion + ") ");
               }

               if (negative) {
                  qry = "REVERSE( " + qry + " )";
               }

               qry = "CASE WHEN (SPLIT_PART(" + arguments.get(0).toString() + "," + delimiter + "," + number + ")) = '' AND GET_ARRAY_LENGTH(SPLIT_TO_ARRAY(" + arguments.get(0).toString() + "," + delimiter + "))<=" + number + " THEN " + firstarg + " ELSE " + qry + " END";
            }

            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else {
            if (number != null) {
               thirdArg = number.toString();
            } else if (from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.numeric")) {
               thirdArg = "TOINTEGER_UDF(" + arguments.get(2).toString() + ")";
            }

            arguments.set(2, thirdArg);
            this.setFunctionArguments(arguments);
         }
      }

   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (from_sqs != null && from_sqs.isHyperSql()) {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         if (arguments.size() == 3) {
            String thirdArg = "0";
            String totString = arguments.get(0).toString();
            String arg1 = arguments.get(1).toString();
            Integer delim_count = StringFunctions.getIntegerValue(arguments.get(2).toString());
            if (delim_count == null) {
               throw new ConvertException("Delimiter_count should be integer value. Column is not supported.");
            }

            if (delim_count < 0) {
               totString = "reverse(" + totString + " )";
               arg1 = "REVERSE(" + arg1 + ")";
               delim_count = delim_count * -1;
            }

            for(int i = 0; i < delim_count; ++i) {
               String newstr = "LOCATE(" + arg1 + "," + totString + ", " + thirdArg + " + 1)";
               thirdArg = newstr;
            }

            String qry = "CASE WHEN (LENGTH(" + totString + ")-LENGTH(REPLACE(" + totString + "," + arg1 + ",'')))/LENGTH(" + arg1 + ") < " + delim_count + " THEN " + arguments.get(0).toString() + " ELSE CASE WHEN " + arguments.get(2).toString() + " > 0 THEN SUBSTRING(" + totString + ", 0 ," + thirdArg + ") ELSE REVERSE(SUBSTRING(" + totString + ", 0 , " + thirdArg + ")) END END";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }
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

      if (this.functionArguments.size() == 3) {
         String thirdArg = "0";
         String totString = arguments.get(0).toString();
         String arg2 = arguments.get(1).toString();
         Integer delim_count = StringFunctions.getIntegerValue(arguments.get(2).toString());
         if (delim_count == null) {
            throw new ConvertException("Delimiter_count should be integer value. Column is not supported.");
         }

         if (delim_count < 0) {
            totString = "Reverse (" + totString + " )";
            arg2 = "REVERSE(" + arg2 + ")";
            delim_count = delim_count * -1;
         }

         String arg2nbsp;
         for(int i = 0; i < delim_count; ++i) {
            arg2nbsp = "CHARINDEX(" + arg2 + "," + totString + ", " + thirdArg + " + 1)";
            thirdArg = arg2nbsp;
         }

         String totStringnbsp = "REPLACE(" + totString + ",' ',CHAR(160))";
         arg2nbsp = "REPLACE(" + arg2 + ",' ',CHAR(160))";
         String qry = "CASE WHEN (" + totString + " IS NULL) OR (" + arg2 + " IS NULL) THEN NULL WHEN (" + arg2nbsp + "='') THEN '' WHEN ((LEN(" + totStringnbsp + ")-LEN(REPLACE(" + totStringnbsp + "," + arg2nbsp + ",'')))/LEN(" + arg2nbsp + ") < " + delim_count + ") THEN " + arguments.get(0).toString() + " ELSE CASE WHEN " + arguments.get(2).toString() + " > 0 THEN SUBSTRING(" + totString + ", 0 ," + thirdArg + ") ELSE REVERSE(SUBSTRING(" + totString + ", 0 , " + thirdArg + ")) END END";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String delimiter = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      Integer number = StringFunctions.getIntegerValue(arguments.get(2).toString());
      if (number == null) {
         throw new ConvertException("Delimiter_count should be integer value. Column is not supported.");
      } else {
         if (this.functionArguments.size() == 3) {
            if (number > 10) {
               throw new ConvertException("Delimiter_count should be less than 10 to prevent performance issue.");
            }

            String firstarg = arguments.get(0).toString();
            delimiter = arguments.get(1).toString();
            arguments.set(0, "CAST( " + firstarg + " AS STRING) ");
            arguments.set(1, "CAST( " + arguments.get(1).toString() + " AS STRING) ");
            boolean negative = false;
            if (number < 0) {
               number = number * -1;
               arguments.set(0, " REVERSE( " + arguments.get(0).toString() + " )");
               arguments.set(1, " REVERSE( " + arguments.get(1).toString() + " )");
               negative = true;
            }

            for(int postion = 0; postion < number; ++postion) {
               if (postion > 0) {
                  qry = qry.concat(" || " + delimiter + " || ");
               }

               qry = qry.concat(" SPLIT(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + ")[OFFSET(" + postion + ")] ");
            }

            if (negative) {
               qry = "REVERSE( " + qry + " )";
            }

            qry = "CASE WHEN ARRAY_LENGTH(SPLIT(" + arguments.get(0).toString() + "," + delimiter + " )) >= " + number + " THEN " + qry + " ELSE " + firstarg + " END";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }

      }
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

      if (this.functionArguments.size() == 3) {
         String arg1 = arguments.get(0).toString();
         String arg2 = arguments.get(1).toString();
         String arg3 = arguments.get(2).toString();
         String qry = "ARRAY_TO_STRING(ARRAY_SLICE(SPLIT(" + arg1 + "," + arg2 + "),LEAST(0," + arg3 + "),ABS(LEAST(" + arg3 + ",(" + arg3 + "/ABS(" + arg3 + "))*ARRAY_SIZE(SPLIT(" + arg1 + "," + arg2 + ")))))," + arg2 + ")";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      Boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 3) {
         String qry = "";
         String arg1 = arguments.get(0).toString();
         String arg2 = arguments.get(1).toString();
         String arg3 = arguments.get(2).toString();
         if (!isdenodo) {
            qry = "(CASE WHEN Instr(" + arg1 + ", " + arg2 + ", 1,ABS(" + arg3 + "))>0 THEN (CASE WHEN " + arg3 + ">0 THEN SUBSTR(" + arg1 + ", 1, Instr(" + arg1 + ", " + arg2 + ", 1,ABS(" + arg3 + ")) -1) ELSE SUBSTR(" + arg1 + ",Instr(" + arg1 + "," + arg2 + ", -1,ABS(" + arg3 + ")) +1) END) ELSE " + arg1 + " END)";
         } else {
            Integer delim_count = StringFunctions.getIntegerValue(arg3);
            if (delim_count == null) {
               throw new ConvertException("Delimiter_count should be integer value. Column is not supported.");
            }

            if (delim_count < 0) {
               throw new ConvertException("Delimiter_count should be a positive value. Negative value is not supported.");
            }

            qry = arg1;

            for(int i = 0; i < delim_count; ++i) {
               qry = "SUBSTR(" + qry + ",POSITION(" + arg2 + " IN " + qry + ")+1)";
            }

            qry = "SUBSTR(REPLACE(" + arg1 + "," + qry + ",''),1,LEN(REPLACE(" + arg1 + "," + qry + ",''))-1)";
         }

         this.functionName.setColumnName(qry);
         this.functionArguments = new Vector();
         this.setOpenBracesForFunctionNameRequired(false);
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 3) {
         String arg1 = arguments.get(0).toString();
         String arg2 = arguments.get(1).toString();
         String arg3 = arguments.get(2).toString();
         String qry = "(CASE WHEN ((LENGTH(" + arg1 + ") - LENGTH(REPLACE(" + arg1 + ", " + arg2 + ", '')))/LENGTH(" + arg2 + ")) < ABS(" + arg3 + ") THEN " + arg1 + " ELSE (CASE WHEN " + arg3 + ">0 THEN SUBSTR(" + arg1 + ", 1, INSTR(" + arg1 + ", " + arg2 + ", 1,ABS(" + arg3 + ")) -1) ELSE SUBSTR(" + arg1 + ",INSTR(" + arg1 + "," + arg2 + ", -1,ABS(" + arg3 + ")) +LENGTH(" + arg2 + ")) END) END)";
         this.functionName.setColumnName(qry);
         this.functionArguments = new Vector();
         this.setOpenBracesForFunctionNameRequired(false);
      }

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

      if (this.functionArguments.size() == 3) {
         String arg1 = arguments.get(0).toString();
         String arg2 = arguments.get(1).toString();
         String arg3 = arguments.get(2).toString();
         String qry = "IF(CARDINALITY(SPLIT(" + arg1 + "," + arg2 + "))<ABS(" + arg3 + ")," + arg1 + ",ARRAY_JOIN(SLICE(SPLIT(" + arg1 + "," + arg2 + "),LEAST(1," + arg3 + "),ABS(LEAST(" + arg3 + ",(" + arg3 + "/ABS(" + arg3 + "))*CARDINALITY(SPLIT(" + arg1 + "," + arg2 + ")))))," + arg2 + "))";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }
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

      if (arguments.size() == 3) {
         String arg1 = arguments.get(0).toString();
         String arg2 = arguments.get(1).toString();
         String arg3 = arguments.get(2).toString();
         String qry = "(CASE WHEN LOCATE(" + arg1 + ", " + arg2 + ", 1,ABS(" + arg3 + "))>0 THEN (CASE WHEN " + arg3 + ">0 THEN SUBSTRING(" + arg1 + ", 1, LOCATE(" + arg1 + ", " + arg2 + ", 1,ABS(" + arg3 + ")) -1) ELSE SUBSTRING(" + arg1 + ",LOCATE(" + arg1 + "," + arg2 + ", -1,ABS(" + arg3 + ")) +1) END) ELSE " + arg1 + " END)";
         this.functionName.setColumnName(qry);
         this.functionArguments = new Vector();
         this.setOpenBracesForFunctionNameRequired(false);
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

      if (arguments.size() == 3) {
         String thirdArg = "0";
         String totString = arguments.get(0).toString();
         String arg1 = arguments.get(1).toString();
         Integer delim_count = StringFunctions.getIntegerValue(arguments.get(2).toString());
         if (delim_count == null) {
            throw new ConvertException("Delimiter_count should be integer value. Column is not supported.");
         }

         if (delim_count < 0) {
            totString = "reverse(" + totString + " )";
            arg1 = "REVERSE(" + arg1 + ")";
            delim_count = delim_count * -1;
         }

         for(int i = 0; i < delim_count; ++i) {
            String newstr = "CHARINDEX(" + arg1 + "," + totString + ", " + thirdArg + " + 1)";
            thirdArg = newstr;
         }

         String qry = "CASE WHEN (LENGTH(" + totString + ")-LENGTH(REPLACE(" + totString + "," + arg1 + ",'')))/LENGTH(" + arg1 + ") < " + delim_count + " THEN " + arguments.get(0).toString() + " ELSE CASE WHEN " + arguments.get(2).toString() + " > 0 THEN SUBSTR(" + totString + ", 0 ," + thirdArg + ") ELSE REVERSE(SUBSTR(" + totString + ", 0 , " + thirdArg + ")) END END";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

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

      if (arguments.size() == 3) {
         String arg1 = arguments.get(0).toString();
         String arg2 = arguments.get(1).toString();
         String arg3 = arguments.get(2).toString();
         String qry = "(CASE WHEN INSTR(" + arg1 + ", " + arg2 + ", 1,ABS(" + arg3 + "))>0 THEN (CASE WHEN " + arg3 + ">0 THEN SUBSTR(" + arg1 + ", 1, INSTR(" + arg1 + ", " + arg2 + ", 1,ABS(" + arg3 + ")) -1) ELSE SUBSTR(" + arg1 + ",INSTR(" + arg1 + "," + arg2 + ", -1,ABS(" + arg3 + ")) +1) END) ELSE " + arg1 + " END)";
         this.functionName.setColumnName(qry);
         this.functionArguments = new Vector();
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         this.setFunctionArguments(arguments);
      }
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

      if (this.functionArguments.size() == 3) {
         String thirdArg = "0";
         String totString = arguments.get(0).toString();
         String arg2 = arguments.get(1).toString();
         Integer delim_count = StringFunctions.getIntegerValue(arguments.get(2).toString());
         if (delim_count == null) {
            throw new ConvertException("Delimiter_count should be integer value. Column is not supported.");
         }

         if (delim_count < 0) {
            totString = "StrReverse(" + totString + ")";
            arg2 = "StrReverse(" + arg2 + ")";
            delim_count = delim_count * -1;
         }

         String arg2nbsp;
         for(int i = 0; i < delim_count; ++i) {
            arg2nbsp = "InStr(" + thirdArg + " + 1," + totString + ", " + arg2 + ")";
            thirdArg = arg2nbsp;
         }

         thirdArg = "(Iif(" + thirdArg + " = 0 ,0,(" + thirdArg + ")-1))";
         String totStringnbsp = "REPLACE(" + totString + ",' ',Chr(160))";
         arg2nbsp = "REPLACE(" + arg2 + ",' ',Chr(160))";
         String qry = "Iif((" + totString + " IS NULL) OR (" + arg2 + " IS NULL), NULL, Iif( " + arg2nbsp + "='','',Iif((LEN(" + totStringnbsp + ")-LEN(REPLACE(" + totStringnbsp + "," + arg2nbsp + ",'')))/LEN(" + arg2nbsp + ") < " + delim_count + ", " + arguments.get(0).toString() + " ,Iif(" + arguments.get(2).toString() + " > 0 , Mid(" + totString + ", 1 ," + thirdArg + ") ,StrReverse(Mid(" + totString + ", 1 , " + thirdArg + "))))))";
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

      if (arguments.size() == 3) {
         String thirdArg = "0";
         String totString = arguments.get(0).toString();
         String arg1 = arguments.get(1).toString();
         Integer delim_count = StringFunctions.getIntegerValue(arguments.get(2).toString());
         if (delim_count == null) {
            throw new ConvertException("Delimiter_count should be integer value. Column is not supported.");
         }

         if (delim_count < 0) {
            totString = "reverse(" + totString + " )";
            arg1 = "REVERSE(" + arg1 + ")";
            delim_count = delim_count * -1;
         }

         for(int i = 0; i < delim_count; ++i) {
            String newstr = "LOCATE(" + arg1 + "," + totString + ", " + thirdArg + " + 1)";
            thirdArg = newstr;
         }

         String qry = "CASE WHEN (LENGTH(" + totString + ")-LENGTH(REPLACE(" + totString + "," + arg1 + ",'')))/LENGTH(" + arg1 + ") < " + delim_count + " THEN " + arguments.get(0).toString() + " ELSE CASE WHEN " + arguments.get(2).toString() + " > 0 THEN SUBSTRING(" + totString + ", 0 ," + thirdArg + ") ELSE REVERSE(SUBSTRING(" + totString + ", 0 , " + thirdArg + ")) END END";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }
}
