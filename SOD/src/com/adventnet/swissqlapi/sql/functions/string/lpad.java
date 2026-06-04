package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class lpad extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LPAD");
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
         String qry = "CASE WHEN (" + arguments.get(1) + " = 0 OR " + arguments.get(1) + " IS NULL or " + arguments.get(2) + " IS NULL) then NULL WHEN LEN(" + arguments.get(0) + ") > " + arguments.get(1) + " THEN SUBSTR(" + arguments.get(0) + ",1," + arguments.get(1) + ") ELSE (SUBSTR(REPEAT(" + arguments.get(2) + "," + arguments.get(1) + "-LEN(" + arguments.get(0) + ")),1," + arguments.get(1) + "-LEN(" + arguments.get(0) + ")) || " + arguments.get(0) + ") END";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   private String oracleTOSqlServer(String ip_str, String noofchars, String fillchar) {
      String retStr = null;
      if (SwisSQLAPI.variableDatatypeMapping != null) {
         if (SwisSQLAPI.variableDatatypeMapping.containsKey(ip_str.substring(1, ip_str.length()))) {
            if (SwisSQLAPI.variableDatatypeMapping.get(ip_str.substring(1)) instanceof String) {
               String dataType = ((String)((String)SwisSQLAPI.variableDatatypeMapping.get(ip_str.substring(1, ip_str.length())))).toLowerCase();
               if (dataType != null && dataType.startsWith("number")) {
                  retStr = "CASE WHEN LEN(" + ip_str + ") >= " + noofchars + " THEN SUBSTRING(CONVERT(VARCHAR(4000), " + ip_str + "),1," + noofchars + ") ";
                  retStr = retStr + "ELSE SUBSTRING(REPLICATE(" + fillchar + "," + noofchars + "),1,ABS(" + noofchars + "-LEN(" + ip_str + "))) + CONVERT(VARCHAR(4000)," + ip_str + ") END";
               } else {
                  retStr = "CASE WHEN LEN(" + ip_str + ") >= " + noofchars + " THEN SUBSTRING(" + ip_str + ",1," + noofchars + ") ";
                  retStr = retStr + "ELSE SUBSTRING(REPLICATE(" + fillchar + "," + noofchars + "),1,ABS(" + noofchars + "-LEN(" + ip_str + "))) + " + ip_str + " END";
               }
            } else {
               retStr = "CASE WHEN LEN(" + ip_str + ") >= " + noofchars + " THEN SUBSTRING(" + ip_str + ",1," + noofchars + ") ";
               retStr = retStr + "ELSE SUBSTRING(REPLICATE(" + fillchar + "," + noofchars + "),1,ABS(" + noofchars + "-LEN(" + ip_str + "))) + " + ip_str + " END";
            }
         } else {
            retStr = "CASE WHEN LEN(" + ip_str + ") >= " + noofchars + " THEN SUBSTRING(" + ip_str + ",1," + noofchars + ") ";
            retStr = retStr + "ELSE SUBSTRING(REPLICATE(" + fillchar + "," + noofchars + "),1,ABS(" + noofchars + "-LEN(" + ip_str + "))) + " + ip_str + " END";
         }
      } else {
         retStr = "CASE WHEN LEN(" + ip_str + ") >= " + noofchars + " THEN SUBSTRING(" + ip_str + ",1," + noofchars + ") ";
         retStr = retStr + "ELSE SUBSTRING(REPLICATE(" + fillchar + "," + noofchars + "),1,ABS(" + noofchars + "-LEN(" + ip_str + "))) + " + ip_str + " END";
      }

      return retStr;
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      int i_count;
      for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      i_count = arguments.size();
      Object param1 = arguments.get(0);
      Object param2 = arguments.get(1);
      Object param3;
      if (i_count > 2) {
         param3 = arguments.get(2);
      } else {
         param3 = new String("' '");
      }

      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector(0));
      this.functionName.setColumnName(this.oracleTOSqlServer(param1.toString(), param2.toString(), param3.toString()));
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      int i_count;
      for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      i_count = this.functionArguments.size();
      Object param1 = this.functionArguments.get(0);
      Object param2 = this.functionArguments.get(1);
      Object param3;
      if (i_count > 2) {
         param3 = this.functionArguments.get(2);
      } else {
         param3 = new String("' '");
      }

      SelectColumn sc = new SelectColumn();
      FunctionCalls replicate = new FunctionCalls();
      FunctionCalls length = new FunctionCalls();
      FunctionCalls convert = new FunctionCalls();
      TableColumn replicateFunction = new TableColumn();
      TableColumn lengthFunction = new TableColumn();
      TableColumn convertFunction = new TableColumn();
      replicateFunction.setOwnerName(this.functionName.getOwnerName());
      replicateFunction.setTableName(this.functionName.getTableName());
      replicateFunction.setColumnName("REPLICATE");
      Vector replicateArg = new Vector();
      replicateArg.add(param3);
      SelectColumn lengthArgSC = new SelectColumn();
      lengthFunction.setOwnerName(this.functionName.getOwnerName());
      lengthFunction.setTableName(this.functionName.getTableName());
      lengthFunction.setColumnName("LEN");
      Vector lengthArg = new Vector();
      lengthArg.add(param1);
      length.setFunctionName(lengthFunction);
      length.setFunctionArguments(lengthArg);
      Vector dummyArg = new Vector();
      dummyArg.add(param2);
      dummyArg.add(" - ");
      dummyArg.add(length);
      lengthArgSC.setColumnExpression(dummyArg);
      replicateArg.add(lengthArgSC);
      replicate.setFunctionName(replicateFunction);
      replicate.setFunctionArguments(replicateArg);
      convertFunction.setOwnerName(this.functionName.getOwnerName());
      convertFunction.setTableName(this.functionName.getTableName());
      convertFunction.setColumnName("CONVERT");
      Vector convertArg = new Vector();
      convertArg.add("VARCHAR");
      convertArg.add(param1);
      convert.setFunctionName(convertFunction);
      convert.setFunctionArguments(convertArg);
      Vector colExp = new Vector();
      colExp.add(replicate);
      colExp.add(" + ");
      colExp.add(convert);
      sc.setColumnExpression(colExp);
      Vector newArg = new Vector();
      newArg.add(sc);
      this.setFunctionArguments(newArg);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LPAD");
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
         this.functionName.setColumnName("LPAD");
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
      this.functionName.setColumnName("LPAD");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (from_sqs != null && from_sqs.isHyperSql()) {
         String qry = "CASE WHEN " + arguments.get(1) + " < LENGTH(" + arguments.get(0) + ") THEN SUBSTR(" + arguments.get(0) + ",1, " + arguments.get(1) + ") ELSE LPAD(" + arguments.get(0) + ", " + arguments.get(1) + ", " + arguments.get(2) + ") END";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LPAD");
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
      this.functionName.setColumnName("LPAD");
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
      throw new ConvertException("\nThe function LPAD is not supported in TimesTen 5.1.21\n");
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LPAD");
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

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LPAD");
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

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LPAD");
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
      this.functionName.setColumnName("LPAD");
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
      this.functionName.setColumnName("LPAD");
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
      this.functionName.setColumnName("LPAD");
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

      String qry = "CASE WHEN LENGTH(" + arguments.get(0) + ") > " + arguments.get(1) + " THEN SUBSTR(" + arguments.get(0) + ",1," + arguments.get(1) + ") ELSE (REPLACE(HEX(ZEROBLOB(" + arguments.get(1) + "-LENGTH(" + arguments.get(0) + "))),'00'," + arguments.get(2) + ") || " + arguments.get(0) + ") END";
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

      String qry = "Mid(Replace(Space(" + arguments.get(1).toString() + "),' '," + arguments.get(2).toString() + ") + CStr(" + arguments.get(0).toString() + ") ,1," + arguments.get(1).toString() + ")";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LPAD");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "CASE WHEN " + arguments.get(1) + " < LENGTH(" + arguments.get(0) + ") THEN SUBSTR(" + arguments.get(0) + ",1, " + arguments.get(1) + ") ELSE LPAD(" + arguments.get(0) + ", " + arguments.get(1) + ", " + arguments.get(2) + ") END";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
   }
}
