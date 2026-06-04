package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class trim extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TRIM");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String newArgumentQualifier = this.getArgumentQualifier();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.trailingString != null) {
         arguments.addElement(this.trailingString);
      }

      this.setArgumentQualifier((String)null);
      this.setTrailingString((String)null);
      this.setFromInTrim((String)null);
      this.setLengthString((String)null);
      if (newArgumentQualifier != null) {
         if (newArgumentQualifier.equalsIgnoreCase("LEADING")) {
            this.functionName.setColumnName("LTRIM");
         } else if (newArgumentQualifier.equalsIgnoreCase("TRAILING")) {
            this.functionName.setColumnName("RTRIM");
         } else if (newArgumentQualifier.equalsIgnoreCase("BOTH")) {
            this.functionName.setColumnName("TRIM");
         }
      } else {
         this.functionName.setColumnName("TRIM");
      }

      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String newArgumentQualifier = this.getArgumentQualifier();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (FunctionCalls.charToIntName) {
         this.functionName.setColumnName("TRIM");
         this.functionName.setTableName("DBO");
      } else if (newArgumentQualifier != null) {
         String qry;
         if (newArgumentQualifier.equalsIgnoreCase("LEADING")) {
            qry = "RTRIM(TRIM(" + this.trailingString + " FROM CONCAT(" + arguments.get(0).toString() + ", ' ')))";
            this.functionName.setColumnName(qry);
            this.setArgumentQualifier((String)null);
            this.setTrailingString((String)null);
            this.setFromInTrim((String)null);
            this.setLengthString((String)null);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         } else if (newArgumentQualifier.equalsIgnoreCase("TRAILING")) {
            qry = "LTRIM(TRIM(" + this.trailingString + " FROM CONCAT(' ', " + arguments.get(0).toString() + ")))";
            this.functionName.setColumnName(qry);
            this.setArgumentQualifier((String)null);
            this.setTrailingString((String)null);
            this.setFromInTrim((String)null);
            this.setLengthString((String)null);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         } else if (newArgumentQualifier.equalsIgnoreCase("BOTH")) {
            this.setArgumentQualifier((String)null);
         }
      } else {
         this.functionName.setColumnName("LTRIM");
         this.setArgumentQualifier((String)null);
         this.setTrailingString((String)null);
         this.setFromInTrim((String)null);
         this.setLengthString((String)null);
         FunctionCalls trimBothSides = new FunctionCalls();
         TableColumn trimRightFunction = new TableColumn();
         trimRightFunction.setColumnName("RTRIM");
         trimBothSides.setFunctionName(trimRightFunction);
         Vector trimArgument = new Vector();
         Vector ltrimArgument = new Vector();
         trimArgument.addElement(this.functionArguments.get(0));
         trimBothSides.setFunctionArguments(trimArgument);
         ltrimArgument.addElement(trimBothSides);
         this.setFunctionArguments(ltrimArgument);
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String newArgumentQualifier = this.getArgumentQualifier();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      TableColumn trimRightFunction;
      Vector trimArgument;
      Vector ltrimArgument;
      FunctionCalls trimBothSides;
      if (newArgumentQualifier != null) {
         Vector trimArgument;
         if (newArgumentQualifier.equalsIgnoreCase("LEADING")) {
            this.functionName.setColumnName("LTRIM");
            this.setArgumentQualifier((String)null);
            this.setTrailingString((String)null);
            this.setFromInTrim((String)null);
            this.setLengthString((String)null);
            trimArgument = new Vector();
            trimArgument.addElement(this.functionArguments.get(0));
            this.setFunctionArguments(trimArgument);
         } else if (newArgumentQualifier.equalsIgnoreCase("TRAILING")) {
            this.functionName.setColumnName("RTRIM");
            this.setArgumentQualifier((String)null);
            this.setTrailingString((String)null);
            this.setFromInTrim((String)null);
            this.setLengthString((String)null);
            trimArgument = new Vector();
            trimArgument.addElement(this.functionArguments.get(0));
            this.setFunctionArguments(trimArgument);
         } else if (newArgumentQualifier.equalsIgnoreCase("BOTH")) {
            this.functionName.setColumnName("LTRIM");
            this.setArgumentQualifier((String)null);
            this.setTrailingString((String)null);
            this.setFromInTrim((String)null);
            this.setLengthString((String)null);
            trimBothSides = new FunctionCalls();
            trimRightFunction = new TableColumn();
            trimRightFunction.setColumnName("RTRIM");
            trimBothSides.setFunctionName(trimRightFunction);
            trimArgument = new Vector();
            ltrimArgument = new Vector();
            trimArgument.addElement(this.functionArguments.get(0));
            trimBothSides.setFunctionArguments(trimArgument);
            ltrimArgument.addElement(trimBothSides);
            this.setFunctionArguments(ltrimArgument);
         }
      } else {
         this.functionName.setColumnName("LTRIM");
         this.setArgumentQualifier((String)null);
         this.setTrailingString((String)null);
         this.setFromInTrim((String)null);
         this.setLengthString((String)null);
         trimBothSides = new FunctionCalls();
         trimRightFunction = new TableColumn();
         trimRightFunction.setColumnName("RTRIM");
         trimBothSides.setFunctionName(trimRightFunction);
         trimArgument = new Vector();
         ltrimArgument = new Vector();
         trimArgument.addElement(this.functionArguments.get(0));
         trimBothSides.setFunctionArguments(trimArgument);
         ltrimArgument.addElement(trimBothSides);
         this.setFunctionArguments(ltrimArgument);
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String newArgumentQualifier = this.getArgumentQualifier();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      TableColumn trimRightFunction;
      Vector trimArgument;
      Vector ltrimArgument;
      FunctionCalls trimBothSides;
      if (newArgumentQualifier != null) {
         Vector trimArgument;
         if (newArgumentQualifier.equalsIgnoreCase("LEADING")) {
            this.functionName.setColumnName("LTRIM");
            this.setArgumentQualifier((String)null);
            this.setFromInTrim((String)null);
            this.setLengthString((String)null);
            trimArgument = new Vector();
            trimArgument.addElement(this.functionArguments.get(0));
            trimArgument.addElement(this.trailingString);
            this.setTrailingString((String)null);
            this.setFunctionArguments(trimArgument);
         } else if (newArgumentQualifier.equalsIgnoreCase("TRAILING")) {
            this.functionName.setColumnName("RTRIM");
            this.setArgumentQualifier((String)null);
            this.setFromInTrim((String)null);
            this.setLengthString((String)null);
            trimArgument = new Vector();
            trimArgument.addElement(this.functionArguments.get(0));
            trimArgument.addElement(this.trailingString);
            this.setTrailingString((String)null);
            this.setFunctionArguments(trimArgument);
         } else if (newArgumentQualifier.equalsIgnoreCase("BOTH")) {
            this.functionName.setColumnName("LTRIM");
            this.setArgumentQualifier((String)null);
            this.setFromInTrim((String)null);
            this.setLengthString((String)null);
            trimBothSides = new FunctionCalls();
            trimRightFunction = new TableColumn();
            trimRightFunction.setColumnName("RTRIM");
            trimBothSides.setFunctionName(trimRightFunction);
            trimArgument = new Vector();
            ltrimArgument = new Vector();
            trimArgument.addElement(this.functionArguments.get(0));
            trimArgument.addElement(this.trailingString);
            trimBothSides.setFunctionArguments(trimArgument);
            ltrimArgument.addElement(trimBothSides);
            ltrimArgument.addElement(this.trailingString);
            this.setFunctionArguments(ltrimArgument);
            this.setTrailingString((String)null);
         }
      } else {
         this.functionName.setColumnName("LTRIM");
         this.setArgumentQualifier((String)null);
         this.setTrailingString((String)null);
         this.setFromInTrim((String)null);
         this.setLengthString((String)null);
         trimBothSides = new FunctionCalls();
         trimRightFunction = new TableColumn();
         trimRightFunction.setColumnName("RTRIM");
         trimBothSides.setFunctionName(trimRightFunction);
         trimArgument = new Vector();
         ltrimArgument = new Vector();
         trimArgument.addElement(this.functionArguments.get(0));
         trimBothSides.setFunctionArguments(trimArgument);
         ltrimArgument.addElement(trimBothSides);
         this.setFunctionArguments(ltrimArgument);
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String newArgumentQualifier = this.getArgumentQualifier();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            sc.convertSelectColumnToTextDataType();
            arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String newArgumentQualifier = this.getArgumentQualifier();
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
      String newArgumentQualifier = this.getArgumentQualifier();
      this.functionName.setColumnName("TRIM");
      Vector arguments = new Vector();

      int i_count;
      for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      i_count = this.functionArguments.size();
      if (i_count != 2) {
         ;
      }
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TRIM");
      Vector arguments = new Vector();

      int i_count;
      for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      i_count = this.functionArguments.size();
      if (i_count > 1) {
         if (arguments.elementAt(1) instanceof SelectColumn) {
            this.setArgumentQualifier("BOTH " + ((SelectColumn)arguments.get(1)).toString());
            arguments.removeElementAt(1);
         } else {
            this.setArgumentQualifier("BOTH");
         }
      } else {
         this.setArgumentQualifier("BOTH");
      }

      this.setFromInTrim("FROM");
      this.setFunctionArguments(arguments);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TRIM");
      Vector arguments = new Vector();

      int i_count;
      for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      i_count = this.functionArguments.size();
      if (i_count > 1) {
         if (arguments.elementAt(1) instanceof SelectColumn) {
            this.setArgumentQualifier("BOTH " + ((SelectColumn)arguments.get(1)).toString());
            arguments.removeElementAt(1);
         } else {
            this.setArgumentQualifier("BOTH");
         }
      } else {
         this.setArgumentQualifier("BOTH");
      }

      this.setFromInTrim("FROM");
      this.setFunctionArguments(arguments);
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String newArgumentQualifier = this.getArgumentQualifier();
      this.functionName.setColumnName("TRIM");
      Vector arguments = new Vector();

      int i_count;
      for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      i_count = this.functionArguments.size();
      if (i_count != 2) {
         ;
      }
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String newArgumentQualifier = this.getArgumentQualifier();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            sc.convertSelectColumnToTextDataType();
            arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (newArgumentQualifier == null) {
         this.setArgumentQualifier("BOTH");
         this.setFromInTrim("FROM");
      }

      this.setFunctionArguments(arguments);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String newArgumentQualifier = this.getArgumentQualifier();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      Vector trimArgument;
      if (newArgumentQualifier != null) {
         if (newArgumentQualifier.equalsIgnoreCase("LEADING")) {
            this.functionName.setColumnName("LTRIM");
            trimArgument = new Vector();
            trimArgument.addElement(arguments.get(0));
            trimArgument.addElement(this.trailingString);
            this.setArgumentQualifier((String)null);
            this.setTrailingString((String)null);
            this.setFromInTrim((String)null);
            this.setLengthString((String)null);
            this.setFunctionArguments(trimArgument);
         } else if (newArgumentQualifier.equalsIgnoreCase("TRAILING")) {
            this.functionName.setColumnName("RTRIM");
            trimArgument = new Vector();
            trimArgument.addElement(arguments.get(0));
            trimArgument.addElement(this.trailingString);
            this.setArgumentQualifier((String)null);
            this.setTrailingString((String)null);
            this.setFromInTrim((String)null);
            this.setLengthString((String)null);
            this.setFunctionArguments(trimArgument);
         } else if (newArgumentQualifier.equalsIgnoreCase("BOTH")) {
            this.functionName.setColumnName("TRIM");
            trimArgument = new Vector();
            trimArgument.addElement(this.functionArguments.get(0));
            trimArgument.addElement(this.trailingString);
            this.setArgumentQualifier((String)null);
            this.setTrailingString((String)null);
            this.setFromInTrim((String)null);
            this.setLengthString((String)null);
            this.setFunctionArguments(trimArgument);
         }
      } else {
         this.functionName.setColumnName("TRIM");
         trimArgument = new Vector();
         trimArgument.addElement(this.functionArguments.get(0));
         this.setArgumentQualifier((String)null);
         this.setTrailingString((String)null);
         this.setFromInTrim((String)null);
         this.setLengthString((String)null);
         this.setFunctionArguments(trimArgument);
      }

   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String newArgumentQualifier = this.getArgumentQualifier();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.trailingString != null) {
         arguments.addElement(this.trailingString);
      }

      this.setArgumentQualifier((String)null);
      this.setTrailingString((String)null);
      this.setFromInTrim((String)null);
      this.setLengthString((String)null);
      if (newArgumentQualifier != null) {
         if (newArgumentQualifier.equalsIgnoreCase("LEADING")) {
            this.functionName.setColumnName("LTRIM");
         } else if (newArgumentQualifier.equalsIgnoreCase("TRAILING")) {
            this.functionName.setColumnName("RTRIM");
         } else if (newArgumentQualifier.equalsIgnoreCase("BOTH")) {
            this.functionName.setColumnName("TRIM");
         }
      } else {
         this.functionName.setColumnName("TRIM");
      }

      arguments.set(0, "CAST(" + arguments.get(0).toString() + " AS VARCHAR)");
      this.setFunctionArguments(arguments);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TRIM");
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
      String newArgumentQualifier = this.getArgumentQualifier();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (this.trailingString != null) {
         arguments.addElement(this.trailingString);
      }

      this.setArgumentQualifier((String)null);
      this.setTrailingString((String)null);
      this.setFromInTrim((String)null);
      this.setLengthString((String)null);
      if (newArgumentQualifier != null) {
         if (newArgumentQualifier.equalsIgnoreCase("LEADING")) {
            this.functionName.setColumnName("LTRIM");
         } else if (newArgumentQualifier.equalsIgnoreCase("TRAILING")) {
            this.functionName.setColumnName("RTRIM");
         } else if (newArgumentQualifier.equalsIgnoreCase("BOTH")) {
            this.functionName.setColumnName("TRIM");
         }
      } else {
         this.functionName.setColumnName("TRIM");
      }

      this.setFunctionArguments(arguments);
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      String newArgumentQualifier = this.getArgumentQualifier();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (this.trailingString != null) {
         arguments.addElement(this.trailingString);
      }

      this.setArgumentQualifier((String)null);
      this.setTrailingString((String)null);
      this.setFromInTrim((String)null);
      this.setLengthString((String)null);
      if (newArgumentQualifier != null) {
         if (newArgumentQualifier.equalsIgnoreCase("LEADING")) {
            this.functionName.setColumnName("LTRIM");
         } else if (newArgumentQualifier.equalsIgnoreCase("TRAILING")) {
            this.functionName.setColumnName("RTRIM");
         } else if (newArgumentQualifier.equalsIgnoreCase("BOTH")) {
            this.functionName.setColumnName("TRIM");
         }
      } else {
         this.functionName.setColumnName("TRIM");
      }

      this.setFunctionArguments(arguments);
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

      this.setFunctionArguments(arguments);
   }
}
