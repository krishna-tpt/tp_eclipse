package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class substring extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String originalFunctionName = this.functionName.getColumnName();
      this.functionName.setColumnName("SUBSTR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (!from_sqs.isOracleLive() && originalFunctionName.equalsIgnoreCase("SUBSTRING")) {
         if (this.functionArguments.size() > 2) {
            SelectColumn expression1 = (SelectColumn)this.functionArguments.get(1);
            SelectColumn expression2 = (SelectColumn)this.functionArguments.get(2);
            if (expression1.getColumnExpression().get(0) instanceof String) {
               String startAt = new String();

               for(int i = 0; i < expression1.getColumnExpression().size(); ++i) {
                  startAt = startAt + expression1.getColumnExpression().get(i).toString();
               }

               String noOfChar = new String();

               int newNoOfChar;
               for(newNoOfChar = 0; newNoOfChar < expression2.getColumnExpression().size(); ++newNoOfChar) {
                  noOfChar = noOfChar + expression2.getColumnExpression().get(newNoOfChar).toString();
               }

               try {
                  if (Integer.parseInt(startAt) <= 0) {
                     newNoOfChar = Integer.parseInt(noOfChar) + Integer.parseInt(startAt) - 1;
                     this.functionArguments.remove(1);
                     this.functionArguments.add(1, "1");
                     String numOfCharStr = "" + newNoOfChar;
                     this.functionArguments.remove(2);
                     this.functionArguments.add(2, numOfCharStr);
                  }
               } catch (NumberFormatException var11) {
               }
            }
         } else {
            if (this.getFromInTrim() != null && this.getFromInTrim().equalsIgnoreCase("FROM")) {
               this.setFromInTrim(",");
            }

            if (this.getForLength() != null && this.getForLength().equalsIgnoreCase("FOR")) {
               this.setForLength(",");
            }
         }
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String originalFunctionName = this.functionName.getColumnName();
      this.functionName.setColumnName("SUBSTRING");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      TableColumn innerFunction;
      Vector argList;
      FunctionCalls len;
      if (originalFunctionName.equalsIgnoreCase("SUBSTR")) {
         if (this.functionArguments.size() == 2) {
            len = new FunctionCalls();
            innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("LEN");
            argList = new Vector();
            argList.add(this.functionArguments.get(0));
            len.setFunctionArguments(argList);
            len.setFunctionName(innerFunction);
            this.functionArguments.add(len);
         } else if (this.functionArguments.size() > 1 && this.functionArguments.get(1) instanceof SelectColumn && ((SelectColumn)((SelectColumn)this.functionArguments.get(1))).getColumnExpression().size() > 1) {
            SelectColumn expression = (SelectColumn)this.functionArguments.get(1);
            if (expression.getColumnExpression().get(0) instanceof String) {
               String sign = (String)expression.getColumnExpression().get(0);
               if ("-".equalsIgnoreCase(sign)) {
                  expression.setOpenBrace("(");
                  expression.setCloseBrace(")");
                  SelectColumn exp = new SelectColumn();
                  exp.setOpenBrace("(");
                  exp.setCloseBrace(")");
                  new Vector();
                  FunctionCalls len = new FunctionCalls();
                  TableColumn innerFunction = new TableColumn();
                  innerFunction.setOwnerName(this.functionName.getOwnerName());
                  innerFunction.setTableName(this.functionName.getTableName());
                  innerFunction.setColumnName("LEN");
                  Vector argList = new Vector();
                  argList.add(this.functionArguments.get(0));
                  len.setFunctionArguments(argList);
                  len.setFunctionName(innerFunction);
                  expression.addColumnExpressionElement("+");
                  expression.addColumnExpressionElement(len);
                  expression.addColumnExpressionElement("+");
                  expression.addColumnExpressionElement("1");
               }
            }
         }
      }

      if (originalFunctionName.equalsIgnoreCase("SUBSTRING")) {
         if (this.functionArguments.size() == 1) {
            if (this.getFromInTrim() != null && this.getFromInTrim().equalsIgnoreCase("FROM")) {
               this.setFromInTrim(",");
            }

            if (this.getForLength() != null) {
               if (this.getForLength().equalsIgnoreCase("FOR")) {
                  this.setForLength(",");
               }
            } else if (this.trailingString != null) {
               len = new FunctionCalls();
               innerFunction = new TableColumn();
               innerFunction.setOwnerName(this.functionName.getOwnerName());
               innerFunction.setTableName(this.functionName.getTableName());
               innerFunction.setColumnName("LEN");
               argList = new Vector();
               argList.add(this.trailingString);
               len.setFunctionArguments(argList);
               len.setFunctionName(innerFunction);
               this.functionArguments.add(len);
            }
         }

         arguments.set(0, "(CAST( " + arguments.get(0).toString() + " AS VARCHAR(MAX)))");
         String strpos;
         if (this.functionArguments.size() == 2) {
            arguments.addElement("(CASE WHEN DATALENGTH(" + arguments.get(0).toString() + ") = 0 THEN 0 ELSE (DATALENGTH(" + arguments.get(0).toString() + ")/DATALENGTH(LEFT((" + arguments.get(0).toString() + "),1))) END)");
            strpos = "(CASE WHEN " + arguments.get(1).toString() + " < 0 THEN (" + arguments.get(2).toString() + " + (" + arguments.get(1).toString() + " + 1)) ELSE " + arguments.get(1).toString() + " END)";
            arguments.set(1, strpos);
            this.setFunctionArguments(arguments);
         } else if (this.functionArguments.size() == 3) {
            strpos = "(CASE WHEN " + arguments.get(1).toString() + " < 0 THEN ((CASE WHEN DATALENGTH(" + arguments.get(0).toString() + ") = 0 THEN 0 ELSE (DATALENGTH(" + arguments.get(0).toString() + ")/DATALENGTH(LEFT((" + arguments.get(0).toString() + "),1))) END) + (" + arguments.get(1).toString() + " + 1)) ELSE " + arguments.get(1).toString() + " END)";
            arguments.set(1, strpos);
            this.setFunctionArguments(arguments);
         }
      }

   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String originalFunctionName = this.functionName.getColumnName();
      this.functionName.setColumnName("SUBSTRING");
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

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUBSTR");
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

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String originalFunctionName = this.functionName.getColumnName();
      this.functionName.setColumnName("SUBSTRING");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      TableColumn innerFunction;
      Vector argList;
      FunctionCalls len;
      if (originalFunctionName.equalsIgnoreCase("SUBSTR")) {
         if (this.functionArguments.size() == 2) {
            len = new FunctionCalls();
            innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("LEN");
            argList = new Vector();
            argList.add(this.functionArguments.get(0));
            len.setFunctionArguments(argList);
            len.setFunctionName(innerFunction);
            this.functionArguments.add(len);
         } else if (this.functionArguments.size() > 1 && this.functionArguments.get(1) instanceof SelectColumn && ((SelectColumn)((SelectColumn)this.functionArguments.get(1))).getColumnExpression().size() > 1) {
            SelectColumn expression = (SelectColumn)this.functionArguments.get(1);
            if (expression.getColumnExpression().get(0) instanceof String) {
               String sign = (String)expression.getColumnExpression().get(0);
               if ("-".equalsIgnoreCase(sign)) {
                  expression.setOpenBrace("(");
                  expression.setCloseBrace(")");
                  SelectColumn exp = new SelectColumn();
                  exp.setOpenBrace("(");
                  exp.setCloseBrace(")");
                  new Vector();
                  FunctionCalls len = new FunctionCalls();
                  TableColumn innerFunction = new TableColumn();
                  innerFunction.setOwnerName(this.functionName.getOwnerName());
                  innerFunction.setTableName(this.functionName.getTableName());
                  innerFunction.setColumnName("LEN");
                  Vector argList = new Vector();
                  argList.add(this.functionArguments.get(0));
                  len.setFunctionArguments(argList);
                  len.setFunctionName(innerFunction);
                  expression.addColumnExpressionElement("+");
                  expression.addColumnExpressionElement(len);
                  expression.addColumnExpressionElement("+");
                  expression.addColumnExpressionElement("1");
               }
            }
         }
      }

      if (originalFunctionName.equalsIgnoreCase("SUBSTRING") && this.functionArguments.size() == 1) {
         if (this.getFromInTrim() != null && this.getFromInTrim().equalsIgnoreCase("FROM")) {
            this.setFromInTrim(",");
         }

         if (this.getForLength() != null) {
            if (this.getForLength().equalsIgnoreCase("FOR")) {
               this.setForLength(",");
            }
         } else if (this.trailingString != null) {
            len = new FunctionCalls();
            innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("LEN");
            argList = new Vector();
            argList.add(this.trailingString);
            len.setFunctionArguments(argList);
            len.setFunctionName(innerFunction);
            this.functionArguments.add(len);
         }
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String originalFunctionName = this.functionName.getColumnName();
      this.functionName.setColumnName("SUBSTR");
      int direction = false;
      Vector arguments = new Vector();

      SelectColumn expression2;
      String startAt;
      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.size() == 3 && this.functionArguments.elementAt(0) != null && this.functionArguments.elementAt(0) instanceof SelectColumn) {
            expression2 = ((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs);
            if (expression2 != null) {
               startAt = expression2.toString();
               if (startAt.equalsIgnoreCase("CURRENT TIMESTAMP") && this.functionArguments.elementAt(1) != null && this.functionArguments.elementAt(1).toString().equalsIgnoreCase("1") && this.functionArguments.elementAt(2) != null && this.functionArguments.elementAt(2).toString().equalsIgnoreCase("10")) {
                  this.functionName.setColumnName("CHAR");
                  arguments.addElement("CURRENT DATE");
                  break;
               }
            }
         }

         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            expression2 = ((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs);
            if (expression2.toString().equalsIgnoreCase("CURRENT TIMESTAMP")) {
               FunctionCalls fn = new FunctionCalls();
               TableColumn tc = new TableColumn();
               tc.setColumnName("CHAR");
               Vector fnArgs = new Vector();
               fnArgs.addElement(expression2);
               fn.setFunctionArguments(fnArgs);
               arguments.addElement(fn);
            } else {
               arguments.addElement(expression2);
            }
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn expression1;
      if (originalFunctionName.equalsIgnoreCase("SUBSTR")) {
         if (this.functionArguments.size() > 1 && ((SelectColumn)((SelectColumn)this.functionArguments.get(1))).getColumnExpression().size() > 1) {
            expression1 = (SelectColumn)this.functionArguments.get(1);
            if (expression1.getColumnExpression().get(0) instanceof String) {
               String sign = (String)expression1.getColumnExpression().get(0);
               if ("-".equalsIgnoreCase(sign)) {
                  expression1.setOpenBrace("(");
                  expression1.setCloseBrace(")");
                  SelectColumn exp = new SelectColumn();
                  exp.setOpenBrace("(");
                  exp.setCloseBrace(")");
                  new Vector();
                  FunctionCalls len = new FunctionCalls();
                  TableColumn innerFunction = new TableColumn();
                  innerFunction.setOwnerName(this.functionName.getOwnerName());
                  innerFunction.setTableName(this.functionName.getTableName());
                  innerFunction.setColumnName("LENGTH");
                  Vector argList = new Vector();
                  argList.add(this.functionArguments.get(0));
                  len.setFunctionArguments(argList);
                  len.setFunctionName(innerFunction);
                  expression1.addColumnExpressionElement("+");
                  expression1.addColumnExpressionElement(len);
                  expression1.addColumnExpressionElement("+");
                  expression1.addColumnExpressionElement("1");
               }
            }
         }
      } else if (originalFunctionName.equalsIgnoreCase("SUBSTRING")) {
         if (this.functionArguments.size() > 2) {
            expression1 = (SelectColumn)this.functionArguments.get(1);
            expression2 = (SelectColumn)this.functionArguments.get(2);
            if (expression1.getColumnExpression().get(0) instanceof String) {
               startAt = new String();

               for(int i = 0; i < expression1.getColumnExpression().size(); ++i) {
                  startAt = startAt + (String)expression1.getColumnExpression().get(i);
               }

               String noOfChar = new String();

               int newNoOfChar;
               for(newNoOfChar = 0; newNoOfChar < expression2.getColumnExpression().size(); ++newNoOfChar) {
                  noOfChar = noOfChar + expression2.getColumnExpression().get(newNoOfChar).toString();
               }

               try {
                  if (Integer.parseInt(startAt) <= 0) {
                     newNoOfChar = Integer.parseInt(noOfChar) + Integer.parseInt(startAt) - 1;
                     this.functionArguments.remove(1);
                     this.functionArguments.add(1, "1");
                     String numOfCharStr = "" + newNoOfChar;
                     this.functionArguments.remove(2);
                     this.functionArguments.add(2, numOfCharStr);
                  }
               } catch (NumberFormatException var13) {
               }
            }
         } else {
            if (this.getFromInTrim() != null && this.getFromInTrim().equalsIgnoreCase("FROM")) {
               this.setFromInTrim(",");
            }

            if (this.getForLength() != null && this.getForLength().equalsIgnoreCase("FOR")) {
               this.setForLength(",");
            }
         }
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String originalFunctionName = this.functionName.getColumnName();
      int direction = false;
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());

      Integer length;
      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            Object arg = null;
            if (i_count == 0) {
               sc.convertSelectColumnToTextDataType();
               arg = sc.toPostgreSQLSelect(to_sqs, from_sqs);
            } else {
               arg = sc.toPostgreSQLSelect(to_sqs, from_sqs);
               length = StringFunctions.getIntegerValue(arg.toString());
               if (length != null) {
                  arg = length.toString();
               } else if (from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.text") && from_sqs.getBooleanValues("use.udf.functions.for.numeric")) {
                  arg = "TOINTEGER_UDF(" + arg.toString() + ")";
               }
            }

            arguments.addElement(arg);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.text")) {
         this.functionName.setColumnName("SUBSTRING_UDF");
      } else {
         String qry = "";
         String dataType = from_sqs.isVerticaDb() ? "VARCHAR" : "TEXT";
         if (this.functionArguments.size() == 2) {
            qry = "( CASE WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN CAST(NULL AS " + dataType + ") WHEN " + arguments.get(1).toString() + " <=0 THEN SUBSTRING(" + arguments.get(0) + ",LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE SUBSTRING(" + arguments.get(0) + "," + arguments.get(1) + ") END )";

            try {
               int index = Integer.parseInt(arguments.get(1).toString());
               if (index > 0) {
                  qry = "SUBSTRING(" + arguments.get(0) + "," + index + ")";
               } else if (index == 0) {
                  qry = "CAST(NULL AS " + dataType + ")";
               }
            } catch (Exception var14) {
            }

            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.functionArguments.size() == 3) {
            qry = "( CASE WHEN " + arguments.get(2) + " <=0 THEN NULL WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN CAST(NULL AS " + dataType + ") ELSE SUBSTRING(" + arguments.get(0) + ", CASE WHEN " + arguments.get(1) + " <=0 THEN (LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE " + arguments.get(1) + " END,  " + arguments.get(2) + ") END )";

            try {
               Integer index = null;
               length = null;

               try {
                  index = Integer.parseInt(arguments.get(1).toString());
               } catch (Exception var13) {
               }

               try {
                  length = Integer.parseInt(arguments.get(2).toString());
               } catch (Exception var12) {
               }

               if (index != null && length != null) {
                  if (index > 0 && length > 0) {
                     qry = "SUBSTRING(" + arguments.get(0) + "," + index + "," + length + ")";
                  } else if (index != 0 && length != 0) {
                     if (length > 0) {
                        qry = "SUBSTRING(" + arguments.get(0) + ", (CASE WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN NULL WHEN " + arguments.get(1) + " <=0 THEN (LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE " + arguments.get(1) + " END),  " + arguments.get(2) + ")";
                     }
                  } else {
                     qry = "CAST(NULL AS " + dataType + ")";
                  }
               } else if (index != null && index == 0) {
                  qry = "CAST(NULL AS " + dataType + ")";
               } else if (length != null) {
                  if (length == 0) {
                     qry = "CAST(NULL AS " + dataType + ")";
                  } else if (length > 0) {
                     qry = "SUBSTRING(" + arguments.get(0) + ", (CASE WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN NULL WHEN " + arguments.get(1) + " <=0 THEN (LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE " + arguments.get(1) + " END),  " + arguments.get(2) + ")";
                  }
               }
            } catch (Exception var15) {
            }

            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }
      }

   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String originalFunctionName = this.functionName.getColumnName();
      this.functionName.setColumnName("SUBSTRING");
      int direction = false;
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (originalFunctionName.equalsIgnoreCase("SUBSTR") && ((SelectColumn)((SelectColumn)arguments.get(1))).getColumnExpression().size() > 1) {
         SelectColumn expression = (SelectColumn)arguments.get(1);
         if (expression.getColumnExpression().get(0) instanceof String) {
            String sign = (String)expression.getColumnExpression().get(0);
            if ("-".equalsIgnoreCase(sign)) {
               expression.setOpenBrace("(");
               expression.setCloseBrace(")");
               SelectColumn exp = new SelectColumn();
               exp.setOpenBrace("(");
               exp.setCloseBrace(")");
               new Vector();
               FunctionCalls len = new FunctionCalls();
               TableColumn innerFunction = new TableColumn();
               innerFunction.setOwnerName(this.functionName.getOwnerName());
               innerFunction.setTableName(this.functionName.getTableName());
               innerFunction.setColumnName("LENGTH");
               Vector argList = new Vector();
               argList.add(this.functionArguments.get(0));
               len.setFunctionArguments(argList);
               len.setFunctionName(innerFunction);
               expression.addColumnExpressionElement("+");
               expression.addColumnExpressionElement(len);
               expression.addColumnExpressionElement("+");
               expression.addColumnExpressionElement("1");
            }
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUBSTR");
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

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnName = this.functionName.getColumnName();
      if (fnName.equalsIgnoreCase("substr") || fnName.equalsIgnoreCase("substrb")) {
         this.functionName.setColumnName("SUBSTRING");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (SwisSQLOptions.useANSIFormatForSubString) {
         if (arguments.size() > 2 && arguments.get(2) != null) {
            arguments.insertElementAt("FOR", 2);
         }

         arguments.insertElementAt(" FROM ", 1);
         this.setStripComma(true);
      }

      this.setFunctionArguments(arguments);
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUBSTRING");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            Vector colExpr = sc.getColumnExpression();
            if (colExpr.size() == 1 && colExpr.get(0) instanceof String && i_count == 0 && ((SelectColumn)this.functionArguments.elementAt(1)).getColumnExpression().elementAt(0) instanceof String && ((SelectColumn)this.functionArguments.elementAt(2)).getColumnExpression().elementAt(0) instanceof String) {
               this.functionName.setColumnName("");
               this.setOpenBracesForFunctionNameRequired(false);
               String str = colExpr.get(0).toString().substring(1, colExpr.get(0).toString().length() - 1);
               int start = Integer.parseInt(this.functionArguments.elementAt(1).toString()) - 1;
               int length = Integer.parseInt(this.functionArguments.elementAt(2).toString());
               int end = start + length;
               if (end > str.length()) {
                  end = str.length();
               }

               arguments.add("'" + str.substring(start, end) + "'");
            } else if (!this.functionName.getColumnName().equalsIgnoreCase("")) {
               throw new ConvertException("\nThe function SUBSTRING is not supported in TimesTen 5.1.21\n");
            }
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String originalFunctionName = this.functionName.getColumnName();
      this.functionName.setColumnName("SUBSTR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn expression1;
      if (originalFunctionName.equalsIgnoreCase("SUBSTRING")) {
         if (this.functionArguments.size() > 2) {
            expression1 = (SelectColumn)this.functionArguments.get(1);
            SelectColumn expression2 = (SelectColumn)this.functionArguments.get(2);
            if (expression1.getColumnExpression().get(0) instanceof String) {
               String startAt = new String();

               for(int i = 0; i < expression1.getColumnExpression().size(); ++i) {
                  startAt = startAt + expression1.getColumnExpression().get(i).toString();
               }

               String noOfChar = new String();

               int newNoOfChar;
               for(newNoOfChar = 0; newNoOfChar < expression2.getColumnExpression().size(); ++newNoOfChar) {
                  noOfChar = noOfChar + expression2.getColumnExpression().get(newNoOfChar).toString();
               }

               try {
                  if (Integer.parseInt(startAt) <= 0) {
                     newNoOfChar = Integer.parseInt(noOfChar) + Integer.parseInt(startAt) - 1;
                     this.functionArguments.remove(1);
                     this.functionArguments.add(1, "1");
                     String numOfCharStr = "" + newNoOfChar;
                     this.functionArguments.remove(2);
                     this.functionArguments.add(2, numOfCharStr);
                  }
               } catch (NumberFormatException var11) {
               }
            }
         } else {
            if (this.getFromInTrim() != null && this.getFromInTrim().equalsIgnoreCase("FROM")) {
               this.setFromInTrim(",");
            }

            if (this.getForLength() != null && this.getForLength().equalsIgnoreCase("FOR")) {
               this.setForLength(",");
            }
         }
      } else if (originalFunctionName.equalsIgnoreCase("SUBSTR")) {
         try {
            if (this.functionArguments.size() > 2) {
               expression1 = (SelectColumn)this.functionArguments.get(1);
               if (expression1.getColumnExpression().get(0) instanceof String) {
                  String startAt = new String();

                  int startAtNum;
                  for(startAtNum = 0; startAtNum < expression1.getColumnExpression().size(); ++startAtNum) {
                     startAt = startAt + expression1.getColumnExpression().get(startAtNum).toString();
                  }

                  startAtNum = Integer.parseInt(startAt);
                  if (startAtNum <= 0) {
                     this.functionArguments.remove(1);
                     this.functionArguments.add(1, "1");
                  }
               }
            }
         } catch (NumberFormatException var12) {
         }
      }

   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnName = this.functionName.getColumnName();
      if (fnName.equalsIgnoreCase("substr") || fnName.equalsIgnoreCase("substrb")) {
         this.functionName.setColumnName("SUBSTR");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() > 1 && arguments.get(1) != null) {
         Object arg2 = arguments.get(1);
         if (arg2 instanceof SelectColumn) {
            SelectColumn arg2Col = (SelectColumn)arg2;
            if (arg2Col.getColumnExpression().size() == 1 && arg2Col.getColumnExpression().get(0).toString().equalsIgnoreCase("0")) {
               arg2Col.getColumnExpression().setElementAt("1", 0);
            }
         } else if (arguments.get(1).toString().equalsIgnoreCase("0")) {
            arguments.setElementAt("1", 1);
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String originalFunctionName = this.functionName.getColumnName();
      this.functionName.setColumnName("SUBSTRING");
      int direction = false;
      String argStr = "";
      Vector arguments = new Vector();

      int index;
      for(index = 0; index < this.functionArguments.size(); ++index) {
         if (this.functionArguments.elementAt(index) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(index);
            if (index == 0) {
               sc.convertSelectColumnToTextDataType();
            }

            arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(index));
         }
      }

      if (arguments.size() == 2) {
         argStr = "SUBSTRING(" + arguments.get(0) + ", CASE WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN NULL WHEN " + arguments.get(1) + " <=0 THEN (LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE " + arguments.get(1) + " END)";

         try {
            index = Integer.parseInt(arguments.get(1).toString());
            if (index > 0) {
               argStr = "SUBSTRING(" + arguments.get(0) + "," + index + ")";
            } else if (index == 0) {
               argStr = "CAST(NULL AS VARCHAR)";
            }
         } catch (Exception var12) {
         }
      } else if (arguments.size() == 3) {
         argStr = "( CASE WHEN " + arguments.get(2) + " <=0 THEN NULL ELSE SUBSTRING(" + arguments.get(0) + ", (CASE WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN NULL WHEN " + arguments.get(1) + " <=0 THEN (LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE " + arguments.get(1) + " END),  " + arguments.get(2) + ") END )";

         try {
            Integer index = null;
            Integer length = null;

            try {
               index = Integer.parseInt(arguments.get(1).toString());
            } catch (Exception var11) {
            }

            try {
               length = Integer.parseInt(arguments.get(2).toString());
            } catch (Exception var10) {
            }

            if (index != null && length != null) {
               if (index > 0 && length > 0) {
                  argStr = "SUBSTRING(" + arguments.get(0) + "," + index + "," + length + ")";
               } else if (index != 0 && length != 0) {
                  if (length > 0) {
                     argStr = "SUBSTRING(" + arguments.get(0) + ", (CASE WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN NULL WHEN " + arguments.get(1) + " <=0 THEN (LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE " + arguments.get(1) + " END),  " + arguments.get(2) + ")";
                  }
               } else {
                  argStr = "CAST(NULL AS VARCHAR)";
               }
            } else if (index != null && index == 0) {
               argStr = "CAST(NULL AS VARCHAR)";
            } else if (length != null) {
               if (length == 0) {
                  argStr = "CAST(NULL AS VARCHAR)";
               } else if (length > 0) {
                  argStr = "SUBSTRING(" + arguments.get(0) + ", (CASE WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN NULL WHEN " + arguments.get(1) + " <=0 THEN (LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE " + arguments.get(1) + " END),  " + arguments.get(2) + ")";
               }
            }
         } catch (Exception var13) {
         }
      }

      this.functionName.setColumnName(argStr);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
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

      if (this.functionName.getColumnName().equalsIgnoreCase("mid")) {
         if (arguments.size() == 2) {
            this.functionName.setColumnName("SUBSTR(CAST(" + arguments.get(0).toString() + " AS STRING)," + arguments.get(1).toString() + ")");
         } else {
            this.functionName.setColumnName("SUBSTR(CAST(" + arguments.get(0).toString() + " AS STRING)," + arguments.get(1).toString() + "," + arguments.get(2).toString() + ")");
         }
      } else {
         this.functionName.setColumnName("SUBSTR");
      }

      this.setFunctionArguments(arguments);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUBSTRING");
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
      this.functionName.setColumnName("SUBSTR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MID");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionArguments.size() > 1 && this.functionArguments.get(1) instanceof SelectColumn && ((SelectColumn)((SelectColumn)this.functionArguments.get(1))).getColumnExpression().size() > 1) {
         if (this.functionArguments.size() == 2) {
            FunctionCalls len = new FunctionCalls();
            TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("LEN");
            Vector argList = new Vector();
            argList.add(this.functionArguments.get(0));
            len.setFunctionArguments(argList);
            len.setFunctionName(innerFunction);
            this.functionArguments.add(len);
         }

         SelectColumn expression = (SelectColumn)this.functionArguments.get(1);
         String strpos;
         if (expression.getColumnExpression().get(0) instanceof String) {
            strpos = (String)expression.getColumnExpression().get(0);
            if ("-".equalsIgnoreCase(strpos)) {
               expression.setOpenBrace("(");
               expression.setCloseBrace(")");
               SelectColumn exp = new SelectColumn();
               exp.setOpenBrace("(");
               exp.setCloseBrace(")");
               new Vector();
               FunctionCalls len = new FunctionCalls();
               TableColumn innerFunction = new TableColumn();
               innerFunction.setOwnerName(this.functionName.getOwnerName());
               innerFunction.setTableName(this.functionName.getTableName());
               innerFunction.setColumnName("LEN");
               Vector argList = new Vector();
               argList.add(this.functionArguments.get(0));
               len.setFunctionArguments(argList);
               len.setFunctionName(innerFunction);
               expression.addColumnExpressionElement("+");
               expression.addColumnExpressionElement(len);
               expression.addColumnExpressionElement("+");
               expression.addColumnExpressionElement("1");
            }
         }

         arguments.set(0, "(CStr( " + arguments.get(0).toString() + "))");
         if (this.functionArguments.size() == 2) {
            arguments.addElement("(Iif(LEN(" + arguments.get(0).toString() + ") = 0, 0,(LEN(" + arguments.get(0).toString() + ")/LEN(LEFT((" + arguments.get(0).toString() + "),1)))))");
            strpos = "(Iif(" + arguments.get(1).toString() + " < 0 , (" + arguments.get(2).toString() + " + (" + arguments.get(1).toString() + " + 1)) , " + arguments.get(1).toString() + " ))";
            arguments.set(1, strpos);
         } else if (this.functionArguments.size() == 3) {
            strpos = "(Iif( " + arguments.get(1).toString() + " < 0 , ((Iif( LEN(" + arguments.get(0).toString() + ") = 0 , 0 , (LEN(" + arguments.get(0).toString() + ")/LEN(LEFT((" + arguments.get(0).toString() + "),1))) )) + (" + arguments.get(1).toString() + " + 1)) , " + arguments.get(1).toString() + " ))";
            arguments.set(1, strpos);
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUBSTR");
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
