package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.StringTokenizer;
import java.util.Vector;

public class date extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector vector = new Vector();
      Boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            vector.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            vector.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dateString = vector.get(0).toString();
      dateString = isdenodo ? StringFunctions.handleLiteralStringDateForDenodo(dateString) : StringFunctions.handleLiteralStringDateForOracle(dateString);
      String query = isdenodo ? "TO_LOCALDATE('yyyy-MM-dd',FORMATDATE('yyyy-MM-dd'," + dateString + "))" : "TO_DATE(TO_CHAR(" + dateString + ", 'DD-MM-YYYY'),'DD-MM-YYYY')";
      this.functionName.setColumnName(query);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   private String getDB2DateFormat(String date) {
      StringTokenizer st;
      String token3;
      if (date.indexOf(".") != -1) {
         st = new StringTokenizer(date, ".");
         if (st.countTokens() == 3) {
            st.nextToken();
            st.nextToken();
            token3 = st.nextToken();
            if (token3.length() == 5) {
               return "'DD.MM.YYYY'";
            }
         }
      } else if (date.indexOf("/") != -1) {
         st = new StringTokenizer(date, "/");
         if (st.countTokens() == 3) {
            st.nextToken();
            st.nextToken();
            token3 = st.nextToken();
            if (token3.length() == 5) {
               return "'MM/DD/YYYY'";
            }
         }
      } else if (date.indexOf("-") != -1) {
         return this.getDB2DateFormatForHyphenatedDate(date);
      }

      return null;
   }

   private String getDB2DateFormatForHyphenatedDate(String date) {
      StringTokenizer st = new StringTokenizer(date, "-");
      if (st.countTokens() == 3) {
         String token1 = st.nextToken();
         st.nextToken();
         String token3 = st.nextToken();
         if (token3.length() == 5) {
            return "'MM-DD-YYYY'";
         }

         if (token1.length() == 5) {
            return "'YYYY-MM-DD'";
         }
      }

      return null;
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CONVERT");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      arguments.add(0, "DATE");
      this.setFunctionArguments(arguments);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CONVERT");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      arguments.add(0, "DATETIME");
      this.setFunctionArguments(arguments);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            this.handleStringLiteralForDateTime(from_sqs, 0, false);
            ((SelectColumn)this.functionArguments.elementAt(i)).modifyCurrentTimeAsCurrentTimestamp(from_sqs);
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (from_sqs != null && from_sqs.isHyperSql()) {
         if (arguments.size() == 1) {
            arguments.set(0, StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString()));
         }

         this.functionName.setColumnName("CAST");
         arguments.add("DATE");
         this.setAsDatatype("AS");
      }

      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.getDateArithmetic() != null) {
         String[] dateArith = this.getDateArithmetic().trim().split(" ");
         if (dateArith.length > 0 && (dateArith[0].equalsIgnoreCase("+") || dateArith[0].equalsIgnoreCase("-"))) {
            String newDateArith = " " + dateArith[0] + " " + dateArith[1] + " * " + " interval '1 " + dateArith[2] + "'";
            this.setDateArithmetic(newDateArith);
         }
      }

   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CAST");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            this.handleStringLiteralForDateTime(from_sqs, 0, false);
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      arguments.add("DATE");
      this.setAsDatatype("AS");
      this.setFunctionArguments(arguments);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (arguments.size() == 1) {
         arguments.set(0, StringFunctions.handleLiteralStringDateForAthena(arguments.get(0).toString()));
      }

      this.setFunctionArguments(arguments);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TO_DATE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 1) {
         arguments.set(0, StringFunctions.handleLiteralStringDateForSapHana(arguments.get(0).toString()));
      }

      this.setFunctionArguments(arguments);
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (arguments.size() == 1) {
         arguments.set(0, StringFunctions.handleLiteralStringDateForSqlite(arguments.get(0).toString()));
      }

      this.setFunctionArguments(arguments);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATE");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (arguments.size() == 1) {
         arguments.set(0, StringFunctions.handleLiteralStringDateForInformix(arguments.get(0).toString()));
      }

      this.setFunctionArguments(arguments);
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.functionName.setColumnName("DateValue");
      this.setFunctionArguments(arguments);
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (arguments.size() == 1) {
         arguments.set(0, StringFunctions.handleLiteralStringDateForHyperSql(arguments.get(0).toString()));
      }

      this.functionName.setColumnName("CAST");
      arguments.add("DATE");
      this.setAsDatatype("AS");
      this.setFunctionArguments(arguments);
   }
}
