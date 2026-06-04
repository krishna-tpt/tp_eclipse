package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class substring_between extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnName;
      if (from_sqs != null && from_sqs.isMySqlLive() && !from_sqs.isHyperSql()) {
         fnName = "if(locate(" + this.functionArguments.get(1).toString() + "," + this.functionArguments.get(0).toString() + ") >=1 and locate(" + this.functionArguments.get(2).toString() + "," + this.functionArguments.get(0).toString() + ",(locate(" + this.functionArguments.get(1).toString() + "," + this.functionArguments.get(0).toString() + ")+ char_length(" + this.functionArguments.get(1).toString() + "))) >=1 , substring_index (substring(" + this.functionArguments.get(0).toString() + ",(locate(" + this.functionArguments.get(1).toString() + "," + this.functionArguments.get(0).toString() + ")+char_length(" + this.functionArguments.get(1).toString() + ")))," + this.functionArguments.get(2).toString() + ",1 ), null )";
         this.functionName.setColumnName(fnName);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         fnName = this.functionName.getColumnName();
         Vector arguments = new Vector();
         Vector arguments1 = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
               arguments1.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
               arguments1.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String index;
         if (from_sqs != null && from_sqs.isHyperSql()) {
            index = "SUBSTRING(" + arguments.get(0).toString() + ",(INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + ")))";
            FunctionCalls fn = this.getSubstringIndexFncall(index, arguments.get(2).toString(), 1);
            fn.toMySQL(to_sqs, from_sqs);
            String qry = "(CASE WHEN " + arguments.get(0).toString() + " = '' OR " + arguments.get(1).toString() + " = '' OR " + arguments.get(2).toString() + " ='' THEN NULL ELSE (CASE WHEN INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ")  >= 1 AND INSTR(SUBSTRING(" + arguments.get(0).toString() + ", (INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + "))), " + arguments.get(2).toString() + ")  >= 1 THEN " + fn + " ELSE null END) END)";
            this.functionName.setColumnName(qry);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
         } else {
            SelectColumn sc_index;
            if (fnName.equalsIgnoreCase("SUBSTRING_BETWEEN")) {
               this.functionName.setColumnName("ZR_TEXTBETWEEN");
               Vector finalArgs = new Vector();
               if (this.functionArguments.size() > 3) {
                  sc_index = new SelectColumn();
                  FunctionCalls fnCall_locate_subStr = new FunctionCalls();
                  TableColumn tbCl_locate_subStr = new TableColumn();
                  tbCl_locate_subStr.setColumnName("SUBSTRING");
                  fnCall_locate_subStr.setFunctionName(tbCl_locate_subStr);
                  Vector vc_locate_subStrIn = new Vector();
                  Vector vc_locate_subStrOut = new Vector();
                  vc_locate_subStrIn.addElement(arguments.get(0));
                  vc_locate_subStrIn.addElement(arguments.get(3));
                  fnCall_locate_subStr.setFunctionArguments(vc_locate_subStrIn);
                  vc_locate_subStrOut.addElement(fnCall_locate_subStr);
                  sc_index.setColumnExpression(vc_locate_subStrOut);
                  finalArgs.addElement(sc_index);
                  finalArgs.addElement(arguments.get(1));
                  finalArgs.addElement(arguments.get(2));
                  this.setFunctionArguments(finalArgs);
               } else {
                  this.setFunctionArguments(arguments);
               }
            } else {
               Vector finalArgs;
               if (!fnName.equalsIgnoreCase("SUBSTRING_BEFORE")) {
                  if (fnName.equalsIgnoreCase("SUBSTRING_AFTER")) {
                     index = "1";
                     if (this.functionArguments.size() == 3) {
                        sc_index = (SelectColumn)arguments.elementAt(2);
                        Vector vc_index = sc_index.getColumnExpression();
                        if (vc_index.size() != 1 || !(vc_index.get(0) instanceof String)) {
                           throw new ConvertException("Invalid Argument Value for Function SUBSTRING_AFTER", "INVALID_ARGUMENT_VALUE", new Object[]{"SUBSTRING_AFTER", "INDEX", "Provide only numeric values"});
                        }

                        index = (String)vc_index.get(0);
                        this.validateSubStringIndex(index, fnName.toUpperCase());
                     }

                     if (this.functionArguments.get(1).toString().equalsIgnoreCase("''")) {
                        this.functionName.setColumnName("");
                        finalArgs = new Vector();
                        finalArgs.add(arguments1.get(0));
                        this.setFunctionArguments(finalArgs);
                     } else {
                        this.functionName.setColumnName("IF");
                        finalArgs = new Vector(3);
                        SelectColumn scFinalCond = new SelectColumn();
                        Vector vcFinalCond = new Vector();
                        WhereExpression whExpFinal = new WhereExpression();
                        WhereItem whItemFinal = new WhereItem();
                        Vector vcWhItemFinal = new Vector();
                        WhereColumn leftFinalCol = new WhereColumn();
                        Vector vcLeftFinalCol = new Vector();
                        FunctionCalls fnLen = new FunctionCalls();
                        TableColumn tcLen = new TableColumn();
                        tcLen.setColumnName("LENGTH");
                        fnLen.setFunctionName(tcLen);
                        Vector vcFnLen = new Vector();
                        vcFnLen.addElement(arguments1.get(1));
                        fnLen.setFunctionArguments(vcFnLen);
                        vcLeftFinalCol.addElement(fnLen);
                        leftFinalCol.setColumnExpression(vcLeftFinalCol);
                        WhereColumn rightFinalCol = new WhereColumn();
                        Vector vcRightFinalCol = new Vector();
                        vcRightFinalCol.addElement(0);
                        rightFinalCol.setColumnExpression(vcRightFinalCol);
                        whItemFinal.setLeftWhereExp(leftFinalCol);
                        whItemFinal.setOperator("=");
                        whItemFinal.setRightWhereExp(rightFinalCol);
                        vcWhItemFinal.addElement(whItemFinal);
                        whExpFinal.setWhereItem(vcWhItemFinal);
                        vcFinalCond.addElement(whExpFinal);
                        scFinalCond.setColumnExpression(vcFinalCond);
                        SelectColumn scFinalTrueSt = new SelectColumn();
                        Vector vcFinalTrueSt = new Vector();
                        vcFinalTrueSt.addElement(arguments1.get(0));
                        scFinalTrueSt.setColumnExpression(vcFinalTrueSt);
                        SelectColumn scFinalFalseSt = new SelectColumn();
                        Vector vcFinalFalseSt = new Vector();
                        FunctionCalls fnClIf = new FunctionCalls();
                        TableColumn tbClIf = new TableColumn();
                        tbClIf.setColumnName("IF");
                        fnClIf.setFunctionName(tbClIf);
                        Vector finalArgs = new Vector(3);
                        SelectColumn scCond = new SelectColumn();
                        Vector vcScCond = new Vector(1);
                        WhereExpression whExp = new WhereExpression();
                        WhereItem whItem = new WhereItem();
                        WhereColumn leftWhCol = new WhereColumn();
                        Vector vcLeftWhCol = new Vector(1);
                        FunctionCalls fnClSubStringCount = new FunctionCalls();
                        TableColumn tblClSubStringCount = new TableColumn();
                        tblClSubStringCount.setColumnName("SUBSTRING_COUNT");
                        fnClSubStringCount.setFunctionName(tblClSubStringCount);
                        Vector fnArgsSubStringCount = new Vector(2);
                        fnArgsSubStringCount.addElement(this.functionArguments.get(0));
                        fnArgsSubStringCount.addElement(this.functionArguments.get(1));
                        fnClSubStringCount.setFunctionArguments(fnArgsSubStringCount);
                        vcLeftWhCol.addElement(fnClSubStringCount.toMySQLSelect(to_sqs, from_sqs));
                        leftWhCol.setColumnExpression(vcLeftWhCol);
                        WhereColumn rightWhCol = new WhereColumn();
                        Vector vcRightWhCol = new Vector(1);
                        vcRightWhCol.addElement(index);
                        rightWhCol.setColumnExpression(vcRightWhCol);
                        whItem.setLeftWhereExp(leftWhCol);
                        whItem.setOperator(">=");
                        whItem.setRightWhereExp(rightWhCol);
                        whExp.addWhereItem(whItem);
                        vcScCond.addElement(whExp);
                        scCond.setColumnExpression(vcScCond);
                        SelectColumn scTrueSt = new SelectColumn();
                        Vector vcScTrueSt = new Vector(1);
                        FunctionCalls fnClTrueSt = new FunctionCalls();
                        TableColumn tblClTrueSt = new TableColumn();
                        tblClTrueSt.setColumnName("SUBSTRING_INDEX");
                        fnClTrueSt.setFunctionName(tblClTrueSt);
                        Vector fnArgsTrueSt = new Vector(3);
                        fnArgsTrueSt.addElement(arguments.get(0));
                        fnArgsTrueSt.addElement(arguments.get(1));
                        SelectColumn scIndexValue = new SelectColumn();
                        Vector vcScIndexValue = new Vector(1);
                        vcScIndexValue.addElement("-");
                        SelectColumn scActualIndex = new SelectColumn();
                        Vector vcScActualIndex = new Vector(5);
                        vcScActualIndex.addElement(fnClSubStringCount.toMySQLSelect(to_sqs, from_sqs));
                        vcScActualIndex.addElement("-");
                        vcScActualIndex.addElement(index);
                        vcScActualIndex.addElement("+ 1");
                        scActualIndex.setColumnExpression(vcScActualIndex);
                        vcScIndexValue.addElement(scActualIndex);
                        scIndexValue.setColumnExpression(vcScIndexValue);
                        scActualIndex.setOpenBrace("(");
                        scActualIndex.setCloseBrace(")");
                        fnArgsTrueSt.addElement(scIndexValue);
                        fnClTrueSt.setFunctionArguments(fnArgsTrueSt);
                        vcScTrueSt.addElement(fnClTrueSt);
                        scTrueSt.setColumnExpression(vcScTrueSt);
                        SelectColumn scFalseSt = new SelectColumn();
                        Vector vcScFalseSt = new Vector(1);
                        vcScFalseSt.addElement("NULL");
                        scFalseSt.setColumnExpression(vcScFalseSt);
                        finalArgs.addElement(scCond);
                        finalArgs.addElement(scTrueSt);
                        finalArgs.addElement(scFalseSt);
                        fnClIf.setFunctionArguments(finalArgs);
                        vcFinalFalseSt.addElement(fnClIf);
                        scFinalFalseSt.setColumnExpression(vcFinalFalseSt);
                        finalArgs.addElement(scFinalCond);
                        finalArgs.addElement(scFinalTrueSt);
                        finalArgs.addElement(scFinalFalseSt);
                        this.setFunctionArguments(finalArgs);
                     }
                  }
               } else {
                  this.functionName.setColumnName("SUBSTRING_INDEX");
                  if (this.functionArguments.size() == 3) {
                     SelectColumn sc_index = (SelectColumn)arguments.elementAt(2);
                     finalArgs = sc_index.getColumnExpression();
                     if (finalArgs.size() != 1 || !(finalArgs.get(0) instanceof String)) {
                        throw new ConvertException("Invalid Argument Value for Function SUBSTRING_BEFORE", "INVALID_ARGUMENT_VALUE", new Object[]{"SUBSTRING_BEFORE", "INDEX", "Provide only numeric values"});
                     }

                     this.validateSubStringIndex((String)finalArgs.get(0), fnName.toUpperCase());
                  } else {
                     arguments.addElement("1");
                  }

                  this.setFunctionArguments(arguments);
               }
            }

         }
      }
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (from_sqs != null && isPostgreLiveDbs) {
         String qry = "";
         String arg1 = "SUBSTRING(" + arguments.get(0).toString() + ",(STRPOS(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + CHAR_LENGTH(" + arguments.get(1).toString() + ")))";
         FunctionCalls fn = this.getSubstringIndexFncall(arg1, arguments.get(2).toString(), 1);
         fn.toPostgreSQL(to_sqs, from_sqs);
         qry = "(CASE WHEN STRPOS(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ")  >= 1 AND STRPOS(SUBSTRING(" + arguments.get(0).toString() + ", (STRPOS(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + CHAR_LENGTH(" + arguments.get(1).toString() + "))), " + arguments.get(2).toString() + ") >= 1 THEN " + fn + " ELSE null END)";
         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         this.setFunctionArguments(arguments);
      }

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

      String qry = "";
      qry = "if(position(CAST(" + arguments.get(1).toString() + " as VARCHAR),CAST(" + arguments.get(0).toString() + " as VARCHAR))  >= 1 AND if(position(CAST(" + arguments.get(2).toString() + " as VARCHAR),substring(CAST(" + arguments.get(0).toString() + " as VARCHAR),( position(CAST(" + arguments.get(1).toString() + " as VARCHAR),CAST(" + arguments.get(0).toString() + " as VARCHAR)) + LENGTH(CAST(" + arguments.get(1).toString() + " as VARCHAR)))))>0,(( position(CAST(" + arguments.get(1).toString() + " as VARCHAR),CAST(" + arguments.get(0).toString() + " as VARCHAR)) + LENGTH(CAST(" + arguments.get(1).toString() + " as VARCHAR)))+position(CAST(" + arguments.get(2).toString() + " as VARCHAR),substring(CAST(" + arguments.get(0).toString() + " as VARCHAR),( position(CAST(" + arguments.get(1).toString() + " as VARCHAR),CAST(" + arguments.get(0).toString() + " as VARCHAR)) + LENGTH(CAST(" + arguments.get(1).toString() + " as VARCHAR)))))-1),0)  >= 1, substring_index(SUBSTRING(CAST(" + arguments.get(0).toString() + " as VARCHAR), (position(CAST(" + arguments.get(1).toString() + " as VARCHAR),CAST(" + arguments.get(0).toString() + " as VARCHAR)) + LENGTH(CAST(" + arguments.get(1).toString() + " as VARCHAR)))), CAST(" + arguments.get(2).toString() + " as VARCHAR), 1), null)";
      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
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

      String arg1 = "SUBSTRING(" + arguments.get(0).toString() + ",(CHARINDEX(" + arguments.get(1).toString() + ", " + arguments.get(0).toString() + ") + (CASE WHEN DATALENGTH(" + arguments.get(1).toString() + ") = 0 THEN 0 ELSE (DATALENGTH(" + arguments.get(1).toString() + ")/DATALENGTH(LEFT((" + arguments.get(1).toString() + "),1))) END)),(CASE WHEN DATALENGTH(" + arguments.get(0).toString() + ") = 0 THEN 0 ELSE (DATALENGTH(" + arguments.get(0).toString() + ")/DATALENGTH(LEFT((" + arguments.get(0).toString() + "),1))) END))";
      FunctionCalls fn = this.getSubstringIndexFncall(arg1, arguments.get(2).toString(), 1);
      fn.toMSSQLServer(to_sqs, from_sqs);
      String qry = "(CASE WHEN CHARINDEX(" + arguments.get(1).toString() + ", " + arguments.get(0).toString() + ")  >= 1 AND CHARINDEX(" + arguments.get(2).toString() + "," + arguments.get(0).toString() + ",(CHARINDEX(" + arguments.get(1).toString() + "," + arguments.get(0).toString() + ") + (CASE WHEN DATALENGTH(" + arguments.get(1).toString() + ") = 0 THEN 0 ELSE (DATALENGTH(" + arguments.get(1).toString() + ")/DATALENGTH(LEFT((" + arguments.get(1).toString() + "),1))) END))) >= 1 THEN " + fn + " ELSE null END)";
      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      String qry = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String arg1 = isdenodo ? "SUBSTR(" + arguments.get(0).toString() + ",(INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ")+1 + LEN(" + arguments.get(1).toString() + ")))" : "SUBSTR(" + arguments.get(0).toString() + ",(INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + ")))";
      FunctionCalls fn = this.getSubstringIndexFncall(arg1, arguments.get(2).toString(), 1);
      fn.toOracle(to_sqs, from_sqs);
      if (isdenodo) {
         qry = "(CASE WHEN (INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ")+1)  >= 1 AND (INSTR(SUBSTR(" + arguments.get(0).toString() + ", (INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ")+1 + LEN(" + arguments.get(1).toString() + "))), " + arguments.get(2).toString() + ")+1) >= 1 THEN " + fn + " ELSE null END)";
      } else {
         qry = "(CASE WHEN INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ")  >= 1 AND INSTR(" + arguments.get(0).toString() + "," + arguments.get(2).toString() + ",(INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + "))) >= 1 THEN " + fn + " ELSE null END)";
      }

      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
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

      String arg1 = "SUBSTR(" + arguments.get(0).toString() + ",(INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + ")))";
      FunctionCalls fn = this.getSubstringIndexFncall(arg1, arguments.get(2).toString(), 1);
      fn.toBigQuery(to_sqs, from_sqs);
      String qry = "(CASE WHEN STRPOS(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ")  >= 1 AND STRPOS(SUBSTRING(" + arguments.get(0).toString() + ", (STRPOS(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + "))), " + arguments.get(2).toString() + ")   >= 1 THEN " + fn + " ELSE null END)";
      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
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

      String arg1 = "SUBSTRING(" + arguments.get(0).toString() + ",(CHARINDEX(" + arguments.get(1).toString() + ", " + arguments.get(0).toString() + ") + LEN(" + arguments.get(1).toString() + ")))";
      FunctionCalls fn = this.getSubstringIndexFncall(arg1, arguments.get(2).toString(), 1);
      fn.toSnowflake(to_sqs, from_sqs);
      String qry = "(CASE WHEN CHARINDEX(" + arguments.get(1).toString() + ", " + arguments.get(0).toString() + ")  >= 1 AND CHARINDEX(" + arguments.get(2).toString() + "," + arguments.get(0).toString() + ",(CHARINDEX(" + arguments.get(1).toString() + "," + arguments.get(0).toString() + ") + LEN(" + arguments.get(1).toString() + ")))  >= 1 THEN " + fn + " ELSE null END)";
      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
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

      String arg1 = "SUBSTR(" + arguments.get(0).toString() + ",(LOCATE(" + arguments.get(1).toString() + ", " + arguments.get(0).toString() + ") + LENGTH(" + arguments.get(1).toString() + ")))";
      FunctionCalls fn = this.getSubstringIndexFncall(arg1, arguments.get(2).toString(), 1);
      fn.toDB2(to_sqs, from_sqs);
      String qry = "(CASE WHEN LOCATE(" + arguments.get(1).toString() + ", " + arguments.get(0).toString() + ")  >= 1 AND LOCATE(" + arguments.get(2).toString() + "," + arguments.get(0).toString() + ",(LOCATE(" + arguments.get(1).toString() + ", " + arguments.get(0).toString() + ") + LENGTH(" + arguments.get(1).toString() + ")))  >= 1 THEN " + fn + " ELSE null END)";
      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
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

      String arg1 = "SUBSTR(" + arguments.get(0).toString() + ",(STRPOS(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + ")))";
      FunctionCalls fn = this.getSubstringIndexFncall(arg1, arguments.get(2).toString(), 1);
      fn.toAthena(to_sqs, from_sqs);
      String qry = "(CASE WHEN STRPOS(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ")  >= 1 AND STRPOS(SUBSTR(" + arguments.get(0).toString() + ", (STRPOS(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + "))), " + arguments.get(2).toString() + ")  >= 1 THEN " + fn + " ELSE null END)";
      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
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

      String arg1 = "SUBSTRING(" + arguments.get(0).toString() + ",(LOCATE(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + ")))";
      FunctionCalls fn = this.getSubstringIndexFncall(arg1, arguments.get(2).toString(), 1);
      fn.toSapHana(to_sqs, from_sqs);
      String qry = "(CASE WHEN LOCATE(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ")  >= 1 AND LOCATE(SUBSTRING(" + arguments.get(0).toString() + ", (LOCATE(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + "))), " + arguments.get(2).toString() + ")  >= 1 THEN " + fn + " ELSE null END)";
      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
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

      String arg1 = "SUBSTRING(" + arguments.get(0).toString() + ",(INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + ")))";
      FunctionCalls fn = this.getSubstringIndexFncall(arg1, arguments.get(2).toString(), 1);
      fn.toSqlite(to_sqs, from_sqs);
      String qry = "(CASE WHEN INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ")  >= 1 AND INSTR(SUBSTRING(" + arguments.get(0).toString() + ", (INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + "))), " + arguments.get(2).toString() + ")  >= 1 THEN " + fn + " ELSE null END)";
      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
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

      String arg1 = "MID(" + arguments.get(0).toString() + ",(INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + (Iif(LEN(" + arguments.get(1).toString() + ") = 0 , 0 , (LEN(" + arguments.get(1).toString() + ")/LEN(LEFT((" + arguments.get(1).toString() + "),1))) ))),(Iif( LEN(" + arguments.get(0).toString() + ") = 0 , 0 , (LEN(" + arguments.get(0).toString() + ")/LEN(LEFT((" + arguments.get(0).toString() + "),1))) )))";
      FunctionCalls fn = this.getSubstringIndexFncall(arg1, arguments.get(2).toString(), 1);
      fn.toExcel(to_sqs, from_sqs);
      String qry = "(Iif( INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ")  >= 1 AND INSTR(" + arguments.get(0).toString() + "," + arguments.get(2).toString() + ",(INSTR(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + ") + (Iif( LEN(" + arguments.get(1).toString() + ") = 0 , 0 , (LEN(" + arguments.get(1).toString() + ")/LEN(LEFT((" + arguments.get(1).toString() + "),1))) )))) >= 1 , " + fn + " , null ))";
      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      Vector arguments1 = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            arguments1.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
            arguments1.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String arg1 = "SUBSTRING(" + arguments.get(0).toString() + ",(INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + ")))";
      FunctionCalls fn = this.getSubstringIndexFncall(arg1, arguments.get(2).toString(), 1);
      fn.toMsAccess(to_sqs, from_sqs);
      String qry = "(CASE WHEN " + arguments.get(0).toString() + " = '' OR " + arguments.get(1).toString() + " = '' OR " + arguments.get(2).toString() + " ='' THEN NULL ELSE (CASE WHEN INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ")  >= 1 AND INSTR(SUBSTRING(" + arguments.get(0).toString() + ", (INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + "))), " + arguments.get(2).toString() + ")  >= 1 THEN " + fn + " ELSE null END) END)";
      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
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

      String arg1 = "SUBSTR(" + arguments.get(0).toString() + ",(INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + ")))";
      FunctionCalls fn = this.getSubstringIndexFncall(arg1, arguments.get(2).toString(), 1);
      fn.toInformix(to_sqs, from_sqs);
      String qry = "(CASE WHEN INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ")  >= 1 AND INSTR(" + arguments.get(0).toString() + "," + arguments.get(2).toString() + ",(INSTR(" + arguments.get(0).toString() + ", " + arguments.get(1).toString() + ") + LENGTH(" + arguments.get(1).toString() + "))) >= 1 THEN " + fn + " ELSE null END)";
      this.functionName.setColumnName(qry);
      this.setFunctionArguments(new Vector());
      this.setOpenBracesForFunctionNameRequired(false);
   }

   public FunctionCalls getSubstringIndexFncall(String arg1, String arg2, int arg3) {
      FunctionCalls fn = new substringIndex();
      Vector args = new Vector();
      args.add(arg1);
      args.add(arg2);
      args.add(arg3);
      fn.setFunctionArguments(args);
      return fn;
   }
}
