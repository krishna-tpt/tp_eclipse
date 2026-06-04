package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.Vector;

public class nvl extends FunctionCalls {
   private String targetDataType;
   private boolean inArithmeticExpr = false;

   public void setInArithmeticExpression(boolean inArithmeticExpr) {
      this.inArithmeticExpr = inArithmeticExpr;
   }

   public void setTargetDataType(String targetDataType) {
      this.targetDataType = targetDataType;
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      int noOfArguments;
      for(noOfArguments = 0; noOfArguments < this.functionArguments.size(); ++noOfArguments) {
         if (this.functionArguments.elementAt(noOfArguments) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(noOfArguments)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(noOfArguments));
         }
      }

      this.setFunctionArguments(arguments);
      if (isdenodo) {
         this.functionName.setColumnName("COALESCE");
      } else {
         this.functionName.setColumnName("NVL");
         noOfArguments = this.functionArguments.size();
         if (noOfArguments > 2) {
            FunctionCalls[] nvlFunctions = new FunctionCalls[noOfArguments - 1];
            Vector[] argList = new Vector[noOfArguments - 1];
            TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("NVL");
            int argc = noOfArguments;

            for(int i = 0; i < argc - 1; ++i) {
               argList[i] = new Vector();
               nvlFunctions[i] = new FunctionCalls();
               if (i == 0) {
                  argList[i].add(this.functionArguments.remove(noOfArguments - 2));
                  --noOfArguments;
                  argList[i].add(this.functionArguments.remove(noOfArguments - 1));
                  --noOfArguments;
               } else {
                  argList[i].add(this.functionArguments.remove(noOfArguments - 1));
                  --noOfArguments;
                  argList[i].add(nvlFunctions[i - 1]);
               }

               nvlFunctions[i].setFunctionArguments(argList[i]);
               nvlFunctions[i].setFunctionName(innerFunction);
            }

            this.setFunctionArguments(argList[argc - 2]);
         }
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("NVL")) {
         this.functionName.setColumnName("ISNULL");
      } else {
         this.functionName.setColumnName("COALESCE");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("NVL")) {
         this.functionName.setColumnName("ISNULL");
      } else {
         this.functionName.setColumnName("COALESCE");
      }

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
      this.functionName.setColumnName("COALESCE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            ((SelectColumn)this.functionArguments.elementAt(i_count)).setTargetDataType(this.targetDataType);
            ((SelectColumn)this.functionArguments.elementAt(i_count)).setInArithmeticExpression(this.inArithmeticExpr);
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean needsCasting = this.needsCastingForStringLiterals() || to_sqs != null && to_sqs.getBooleanValues("set.can.cast.all.to.text.columns");
      boolean coalesce = true;
      if (!needsCasting && this.functionArguments.size() <= 10) {
         FunctionCalls fc = this.convertToIFFunctionCall(to_sqs, from_sqs);
         if (fc != null) {
            fc.toPostgreSQL(to_sqs, from_sqs);
            this.functionName.setColumnName("IF");
            this.setFunctionArguments(fc.getFunctionArguments());
            fc = null;
            coalesce = false;
         } else {
            coalesce = true;
         }
      }

      if (coalesce) {
         this.functionName.setColumnName("COALESCE");
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
               sc.convertSelectColumnToTextDataType(needsCasting);
               arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
         if (to_sqs != null) {
            to_sqs.addCurrentIndexToCoalesceFunctionList();
         }
      }

   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COALESCE");
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

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COALESCE");
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

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COALESCE");
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
      this.functionName.setColumnName("NVL");
      Vector arguments = new Vector();

      int noOfArguments;
      for(noOfArguments = 0; noOfArguments < this.functionArguments.size(); ++noOfArguments) {
         if (this.functionArguments.elementAt(noOfArguments) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(noOfArguments)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(noOfArguments));
         }
      }

      this.setFunctionArguments(arguments);
      noOfArguments = this.functionArguments.size();
      if (noOfArguments > 2) {
         FunctionCalls[] nvlFunctions = new FunctionCalls[noOfArguments - 1];
         Vector[] argList = new Vector[noOfArguments - 1];
         TableColumn innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("NVL");
         int argc = noOfArguments;

         for(int i = 0; i < argc - 1; ++i) {
            argList[i] = new Vector();
            nvlFunctions[i] = new FunctionCalls();
            if (i == 0) {
               argList[i].add(this.functionArguments.remove(noOfArguments - 2));
               --noOfArguments;
               argList[i].add(this.functionArguments.remove(noOfArguments - 1));
               --noOfArguments;
            } else {
               argList[i].add(this.functionArguments.remove(noOfArguments - 1));
               --noOfArguments;
               argList[i].add(nvlFunctions[i - 1]);
            }

            nvlFunctions[i].setFunctionArguments(argList[i]);
            nvlFunctions[i].setFunctionName(innerFunction);
         }

         this.setFunctionArguments(argList[argc - 2]);
      }

   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("NVL");
      Vector arguments = new Vector();

      int noOfArguments;
      for(noOfArguments = 0; noOfArguments < this.functionArguments.size(); ++noOfArguments) {
         if (this.functionArguments.elementAt(noOfArguments) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(noOfArguments)).toTimesTenSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(noOfArguments));
         }
      }

      this.setFunctionArguments(arguments);
      noOfArguments = this.functionArguments.size();
      if (noOfArguments > 2) {
         FunctionCalls[] nvlFunctions = new FunctionCalls[noOfArguments - 1];
         Vector[] argList = new Vector[noOfArguments - 1];
         TableColumn innerFunction = new TableColumn();
         innerFunction.setOwnerName(this.functionName.getOwnerName());
         innerFunction.setTableName(this.functionName.getTableName());
         innerFunction.setColumnName("NVL");
         int argc = noOfArguments;

         for(int i = 0; i < argc - 1; ++i) {
            argList[i] = new Vector();
            nvlFunctions[i] = new FunctionCalls();
            if (i == 0) {
               argList[i].add(this.functionArguments.remove(noOfArguments - 2));
               --noOfArguments;
               argList[i].add(this.functionArguments.remove(noOfArguments - 1));
               --noOfArguments;
            } else {
               argList[i].add(this.functionArguments.remove(noOfArguments - 1));
               --noOfArguments;
               argList[i].add(nvlFunctions[i - 1]);
            }

            nvlFunctions[i].setFunctionArguments(argList[i]);
            nvlFunctions[i].setFunctionName(innerFunction);
         }

         this.setFunctionArguments(argList[argc - 2]);
      }

   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("nvl")) {
         this.functionName.setColumnName("NVL");
      } else if (this.functionName.getColumnName().equalsIgnoreCase("coalesce")) {
         this.functionName.setColumnName("COALESCE");
      }

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
      this.functionName.setColumnName("COALESCE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      boolean isDateArg = false;

      for(int j = 0; j < this.functionArguments.size(); ++j) {
         FunctionCalls dateFunc;
         if (this.functionArguments.elementAt(j) instanceof SelectColumn) {
            if (((SelectColumn)this.functionArguments.elementAt(j)).getColumnExpression().get(0) instanceof FunctionCalls) {
               dateFunc = (FunctionCalls)((SelectColumn)this.functionArguments.elementAt(j)).getColumnExpression().get(0);
               if (dateFunc.getFunctionName() != null && SwisSQLUtils.getFunctionReturnType(dateFunc.getFunctionName().getColumnName(), dateFunc.getFunctionArguments()).equalsIgnoreCase("date")) {
                  isDateArg = true;
               }
            } else if (((SelectColumn)this.functionArguments.elementAt(j)).getColumnExpression().get(0) instanceof TableColumn) {
               TableColumn dateFunc = (TableColumn)((SelectColumn)this.functionArguments.elementAt(j)).getColumnExpression().get(0);
               if (SwisSQLUtils.getFunctionReturnType(dateFunc.getColumnName(), (Vector)null).equalsIgnoreCase("date")) {
                  isDateArg = true;
               }
            }
         } else if (this.functionArguments.elementAt(j) instanceof FunctionCalls) {
            dateFunc = (FunctionCalls)this.functionArguments.elementAt(j);
            if (dateFunc.getFunctionName() != null && SwisSQLUtils.getFunctionReturnType(dateFunc.getFunctionName().getColumnName(), dateFunc.getFunctionArguments()).equalsIgnoreCase("date")) {
               isDateArg = true;
            }
         } else if (this.functionArguments.elementAt(j) instanceof String) {
         }
      }

      if (isDateArg) {
         Vector newArguments = new Vector();

         for(int k = 0; k < arguments.size(); ++k) {
            FunctionCalls castTimestamp = new FunctionCalls();
            TableColumn castTcn = new TableColumn();
            castTcn.setColumnName("CAST");
            castTimestamp.setFunctionName(castTcn);
            castTimestamp.setAsDatatype("AS");
            DateClass castDatatype = new DateClass();
            castDatatype.setDatatypeName("TIMESTAMP");
            castDatatype.setOpenBrace("(");
            castDatatype.setSize("0");
            castDatatype.setClosedBrace(")");
            Vector castTimestampArgs = new Vector();
            castTimestampArgs.add(arguments.get(k));
            castTimestampArgs.add(castDatatype);
            castTimestamp.setFunctionArguments(castTimestampArgs);
            newArguments.add(castTimestamp);
         }

         this.setFunctionArguments(newArguments);
      } else {
         this.setFunctionArguments(arguments);
      }

   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COALESCE");
      boolean needsCasting = this.needsCastingForStringLiterals();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            sc.convertSelectColumnToTextDataType(needsCasting);
            arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COALESCE");
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
      this.functionName.setColumnName("COALESCE");
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
      this.functionName.setColumnName("COALESCE");
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
      this.functionName.setColumnName("COALESCE");
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
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionArguments.size() == 2) {
         String qry = "Iif(ISNULL(" + arguments.get(0) + ")," + arguments.get(1) + "," + arguments.get(0) + ")";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COALESCE");
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

   public FunctionCalls getFunctionCall() {
      FunctionCalls fc = new FunctionCalls();
      TableColumn tc = new TableColumn();
      tc.setColumnName("IF");
      fc.setFunctionName(tc);
      return fc;
   }

   public FunctionCalls convertToIFFunctionCall(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      FunctionCalls fcFinal = null;
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());

      try {
         if (from_sqs != null && from_sqs.canUseIFFunctionForPGCaseWhenExp() && !isPostgreLiveDbs) {
            Vector fcList = new Vector();
            fcFinal = this.getFunctionCall();
            FunctionCalls fc = fcFinal;
            FunctionCalls reference = null;

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (!(this.functionArguments.elementAt(i) instanceof SelectColumn)) {
                  fcFinal = null;
                  break;
               }

               SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i);
               if (i != 0) {
                  fc = reference;
               }

               SelectColumn sc1 = new SelectColumn();
               Vector colExp = new Vector();
               WhereItem wi = new WhereItem();
               WhereColumn wc = new WhereColumn();
               wc.setColumnExpression(sc.getColumnExpression());
               wi.setLeftWhereExp(wc);
               wi.setRightWhereExp((WhereColumn)null);
               wi.setOperator("IS NOT NULL");
               colExp.add(wi);
               sc1.setColumnExpression(colExp);
               SelectColumn sc3 = new SelectColumn();
               Vector colExp2 = new Vector();
               if (i + 1 < this.functionArguments.size()) {
                  reference = this.getFunctionCall();
                  colExp2.add(reference);
               } else {
                  colExp2.add("NULL");
               }

               sc3.setColumnExpression(colExp2);
               Vector fnArgs = new Vector();
               fnArgs.add(sc1);
               fnArgs.add(sc);
               fnArgs.add(sc3);
               fc.setFunctionArguments(fnArgs);
               fcList.add(fc);
            }
         }
      } catch (Exception var18) {
         fcFinal = null;
      }

      return fcFinal;
   }
}
