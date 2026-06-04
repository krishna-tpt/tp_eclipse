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

public class initcap extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("INITCAP");
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

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn arg = new SelectColumn();
      SelectColumn arg2 = new SelectColumn();
      Vector colExpArg = new Vector();
      Vector colExpArg2 = new Vector();
      FunctionCalls length = new FunctionCalls();
      FunctionCalls substringForLcase = new FunctionCalls();
      FunctionCalls substringForUcase = new FunctionCalls();
      FunctionCalls lowerCase = new FunctionCalls();
      FunctionCalls upperCase = new FunctionCalls();
      TableColumn lengthFunction = new TableColumn();
      TableColumn substrFunctionForLcase = new TableColumn();
      new TableColumn();
      TableColumn lcaseFunction = new TableColumn();
      TableColumn ucaseFunction = new TableColumn();
      lengthFunction.setColumnName("DATALENGTH");
      length.setFunctionName(lengthFunction);
      substrFunctionForLcase.setColumnName("SUBSTRING");
      substringForLcase.setFunctionName(substrFunctionForLcase);
      substringForUcase.setFunctionName(substrFunctionForLcase);
      lcaseFunction.setColumnName("LOWER");
      ucaseFunction.setColumnName("UPPER");
      lowerCase.setFunctionName(lcaseFunction);
      upperCase.setFunctionName(ucaseFunction);
      Vector lengthArgument = new Vector();
      Vector substrArgumentForLcase = new Vector();
      Vector substrArgumentForUcase = new Vector();
      Vector lcaseArgument = new Vector();
      Vector ucaseArgument = new Vector();
      Vector dummyArgument = new Vector();
      lengthArgument.addElement(this.functionArguments.get(0));
      length.setFunctionArguments(lengthArgument);
      colExpArg.addElement(length);
      colExpArg.addElement("-1");
      arg.setColumnExpression(colExpArg);
      substrArgumentForLcase.addElement(this.functionArguments.get(0));
      substrArgumentForLcase.addElement("2");
      substrArgumentForLcase.addElement(arg);
      substringForLcase.setFunctionArguments(substrArgumentForLcase);
      lcaseArgument.addElement(substringForLcase);
      lowerCase.setFunctionArguments(lcaseArgument);
      substrArgumentForUcase.addElement(this.functionArguments.get(0));
      substrArgumentForUcase.addElement("1");
      substrArgumentForUcase.addElement("1");
      substringForUcase.setFunctionArguments(substrArgumentForUcase);
      ucaseArgument.addElement(substringForUcase);
      upperCase.setFunctionArguments(ucaseArgument);
      colExpArg2.addElement(upperCase);
      colExpArg2.addElement("+");
      colExpArg2.addElement(lowerCase);
      arg2.setColumnExpression(colExpArg2);
      dummyArgument.addElement(arg2);
      this.setFunctionArguments(dummyArgument);
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
      SelectColumn arg = new SelectColumn();
      SelectColumn arg2 = new SelectColumn();
      Vector colExpArg = new Vector();
      Vector colExpArg2 = new Vector();
      FunctionCalls length = new FunctionCalls();
      FunctionCalls substringForLcase = new FunctionCalls();
      FunctionCalls substringForUcase = new FunctionCalls();
      FunctionCalls lowerCase = new FunctionCalls();
      FunctionCalls upperCase = new FunctionCalls();
      TableColumn lengthFunction = new TableColumn();
      TableColumn substrFunctionForLcase = new TableColumn();
      new TableColumn();
      TableColumn lcaseFunction = new TableColumn();
      TableColumn ucaseFunction = new TableColumn();
      lengthFunction.setColumnName("DATALENGTH");
      length.setFunctionName(lengthFunction);
      substrFunctionForLcase.setColumnName("SUBSTRING");
      substringForLcase.setFunctionName(substrFunctionForLcase);
      substringForUcase.setFunctionName(substrFunctionForLcase);
      lcaseFunction.setColumnName("LOWER");
      ucaseFunction.setColumnName("UPPER");
      lowerCase.setFunctionName(lcaseFunction);
      upperCase.setFunctionName(ucaseFunction);
      Vector lengthArgument = new Vector();
      Vector substrArgumentForLcase = new Vector();
      Vector substrArgumentForUcase = new Vector();
      Vector lcaseArgument = new Vector();
      Vector ucaseArgument = new Vector();
      Vector dummyArgument = new Vector();
      lengthArgument.addElement(this.functionArguments.get(0));
      length.setFunctionArguments(lengthArgument);
      colExpArg.addElement(length);
      colExpArg.addElement("-1");
      arg.setColumnExpression(colExpArg);
      substrArgumentForLcase.addElement(this.functionArguments.get(0));
      substrArgumentForLcase.addElement("2");
      substrArgumentForLcase.addElement(arg);
      substringForLcase.setFunctionArguments(substrArgumentForLcase);
      lcaseArgument.addElement(substringForLcase);
      lowerCase.setFunctionArguments(lcaseArgument);
      substrArgumentForUcase.addElement(this.functionArguments.get(0));
      substrArgumentForUcase.addElement("1");
      substrArgumentForUcase.addElement("1");
      substringForUcase.setFunctionArguments(substrArgumentForUcase);
      ucaseArgument.addElement(substringForUcase);
      upperCase.setFunctionArguments(ucaseArgument);
      colExpArg2.addElement(upperCase);
      colExpArg2.addElement("+");
      colExpArg2.addElement(lowerCase);
      arg2.setColumnExpression(colExpArg2);
      dummyArgument.addElement(arg2);
      this.setFunctionArguments(dummyArgument);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CONCAT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn arg = new SelectColumn();
      new SelectColumn();
      Vector colExpArg = new Vector();
      Vector colExpArg2 = new Vector();
      FunctionCalls length = new FunctionCalls();
      FunctionCalls substringForLcase = new FunctionCalls();
      FunctionCalls substringForUcase = new FunctionCalls();
      FunctionCalls lowerCase = new FunctionCalls();
      FunctionCalls upperCase = new FunctionCalls();
      TableColumn lengthFunction = new TableColumn();
      TableColumn substrFunctionForLcase = new TableColumn();
      new TableColumn();
      TableColumn lcaseFunction = new TableColumn();
      TableColumn ucaseFunction = new TableColumn();
      lengthFunction.setColumnName("LENGTH");
      length.setFunctionName(lengthFunction);
      substrFunctionForLcase.setColumnName("SUBSTR");
      substringForLcase.setFunctionName(substrFunctionForLcase);
      substringForUcase.setFunctionName(substrFunctionForLcase);
      lcaseFunction.setColumnName("LCASE");
      ucaseFunction.setColumnName("UCASE");
      lowerCase.setFunctionName(lcaseFunction);
      upperCase.setFunctionName(ucaseFunction);
      Vector lengthArgument = new Vector();
      Vector substrArgumentForLcase = new Vector();
      Vector substrArgumentForUcase = new Vector();
      Vector lcaseArgument = new Vector();
      Vector ucaseArgument = new Vector();
      lengthArgument.addElement(this.functionArguments.get(0));
      length.setFunctionArguments(lengthArgument);
      colExpArg.addElement(length);
      colExpArg.addElement("-1");
      arg.setColumnExpression(colExpArg);
      substrArgumentForLcase.addElement(this.functionArguments.get(0));
      substrArgumentForLcase.addElement("2");
      substrArgumentForLcase.addElement(arg);
      substringForLcase.setFunctionArguments(substrArgumentForLcase);
      lcaseArgument.addElement(substringForLcase);
      lowerCase.setFunctionArguments(lcaseArgument);
      substrArgumentForUcase.addElement(this.functionArguments.get(0));
      substrArgumentForUcase.addElement("1");
      substrArgumentForUcase.addElement("1");
      substringForUcase.setFunctionArguments(substrArgumentForUcase);
      ucaseArgument.addElement(substringForUcase);
      upperCase.setFunctionArguments(ucaseArgument);
      colExpArg2.addElement(upperCase);
      colExpArg2.addElement(lowerCase);
      this.setFunctionArguments(colExpArg2);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("INITCAP");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("IF");
      Vector arguments1 = new Vector();
      Vector arguments2 = new Vector();
      Vector arguments3 = new Vector();
      Vector arguments4 = new Vector();
      Vector arguments5 = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments1.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            arguments2.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            arguments3.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            arguments4.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            arguments5.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments1.addElement(this.functionArguments.elementAt(i_count));
            arguments2.addElement(this.functionArguments.elementAt(i_count));
            arguments3.addElement(this.functionArguments.elementAt(i_count));
            arguments4.addElement(this.functionArguments.elementAt(i_count));
            arguments5.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector ifArguments = new Vector();
      SelectColumn sc_ifCond = new SelectColumn();
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
      vcFnLen.addElement(arguments5.get(0));
      fnLen.setFunctionArguments(vcFnLen);
      vcLeftFinalCol.addElement(fnLen);
      leftFinalCol.setColumnExpression(vcLeftFinalCol);
      WhereColumn rightFinalCol = new WhereColumn();
      Vector vcRightFinalCol = new Vector();
      vcRightFinalCol.addElement(1);
      rightFinalCol.setColumnExpression(vcRightFinalCol);
      whItemFinal.setLeftWhereExp(leftFinalCol);
      whItemFinal.setOperator("<=");
      whItemFinal.setRightWhereExp(rightFinalCol);
      vcWhItemFinal.addElement(whItemFinal);
      whExpFinal.setWhereItem(vcWhItemFinal);
      vcFinalCond.addElement(whExpFinal);
      sc_ifCond.setColumnExpression(vcFinalCond);
      SelectColumn sc_trueSt = new SelectColumn();
      FunctionCalls fc_upper = new FunctionCalls();
      TableColumn tc_upper = new TableColumn();
      tc_upper.setColumnName("UPPER");
      fc_upper.setFunctionName(tc_upper);
      Vector vc_upperIn = new Vector();
      Vector vc_upperOut = new Vector();
      vc_upperIn.addElement(arguments4.get(0));
      fc_upper.setFunctionArguments(vc_upperIn);
      vc_upperOut.addElement(fc_upper);
      sc_trueSt.setColumnExpression(vc_upperOut);
      SelectColumn sc_falseSt = new SelectColumn();
      FunctionCalls fc_concat = new FunctionCalls();
      TableColumn tc_concat = new TableColumn();
      tc_concat.setColumnName("CONCAT");
      fc_concat.setFunctionName(tc_concat);
      Vector concatArgumentsIn = new Vector();
      Vector concatArgumentsOut = new Vector();
      SelectColumn sc_upperCase = new SelectColumn();
      FunctionCalls fn_upperCase = new FunctionCalls();
      TableColumn tb_upperCase = new TableColumn();
      tb_upperCase.setColumnName("UPPER");
      fn_upperCase.setFunctionName(tb_upperCase);
      Vector vc_upperCaseIn = new Vector();
      Vector vc_upperCaseOut = new Vector();
      SelectColumn sc_subStrFirstLetter = new SelectColumn();
      FunctionCalls fn_subStrFirstLetter = new FunctionCalls();
      TableColumn tb_subStrFirstLetter = new TableColumn();
      tb_subStrFirstLetter.setColumnName("SUBSTRING");
      fn_subStrFirstLetter.setFunctionName(tb_subStrFirstLetter);
      Vector vc_subStrFirstLetterIn = new Vector();
      Vector vc_subStrFirstLetterOut = new Vector();
      vc_subStrFirstLetterIn.addElement(arguments1.get(0));
      vc_subStrFirstLetterIn.addElement("1");
      vc_subStrFirstLetterIn.addElement("1");
      fn_subStrFirstLetter.setFunctionArguments(vc_subStrFirstLetterIn);
      vc_subStrFirstLetterOut.addElement(fn_subStrFirstLetter);
      sc_subStrFirstLetter.setColumnExpression(vc_subStrFirstLetterOut);
      vc_upperCaseIn.addElement(sc_subStrFirstLetter);
      fn_upperCase.setFunctionArguments(vc_upperCaseIn);
      vc_upperCaseOut.addElement(fn_upperCase);
      sc_upperCase.setColumnExpression(vc_upperCaseOut);
      SelectColumn sc_lowerCase = new SelectColumn();
      FunctionCalls fn_lowerCase = new FunctionCalls();
      TableColumn tb_lowerCase = new TableColumn();
      tb_lowerCase.setColumnName("LOWER");
      fn_lowerCase.setFunctionName(tb_lowerCase);
      Vector vc_lowerCaseIn = new Vector();
      Vector vc_lowerCaseOut = new Vector();
      SelectColumn sc_subStrRemainingLetters = new SelectColumn();
      FunctionCalls fn_subStrRemainingLetters = new FunctionCalls();
      TableColumn tb_subStrRemainingLetters = new TableColumn();
      tb_subStrRemainingLetters.setColumnName("SUBSTRING");
      fn_subStrRemainingLetters.setFunctionName(tb_subStrRemainingLetters);
      Vector vc_subStrRemainingLettersIn = new Vector();
      Vector vc_subStrRemainingLettersOut = new Vector();
      vc_subStrRemainingLettersIn.addElement(arguments2.get(0));
      vc_subStrRemainingLettersIn.addElement("2");
      SelectColumn sc_endIndexForRemainingLetters = new SelectColumn();
      Vector vc_endIndexForRemainingLetters = new Vector();
      SelectColumn sc_lenArgForLowerCase = new SelectColumn();
      FunctionCalls fn_lenArgForLowerCase = new FunctionCalls();
      TableColumn tb_lenArgForLowerCase = new TableColumn();
      tb_lenArgForLowerCase.setColumnName("LENGTH");
      fn_lenArgForLowerCase.setFunctionName(tb_lenArgForLowerCase);
      Vector vc_lenArgForLowerCaseIn = new Vector();
      Vector vc_lenArgForLowerCaseOut = new Vector();
      vc_lenArgForLowerCaseIn.addElement(arguments3.get(0));
      fn_lenArgForLowerCase.setFunctionArguments(vc_lenArgForLowerCaseIn);
      vc_lenArgForLowerCaseOut.addElement(fn_lenArgForLowerCase);
      sc_lenArgForLowerCase.setColumnExpression(vc_lenArgForLowerCaseOut);
      vc_endIndexForRemainingLetters.addElement(sc_lenArgForLowerCase);
      vc_endIndexForRemainingLetters.addElement("-");
      vc_endIndexForRemainingLetters.addElement("1");
      sc_endIndexForRemainingLetters.setOpenBrace("(");
      sc_endIndexForRemainingLetters.setCloseBrace(")");
      sc_endIndexForRemainingLetters.setColumnExpression(vc_endIndexForRemainingLetters);
      vc_subStrRemainingLettersIn.addElement(sc_endIndexForRemainingLetters);
      fn_subStrRemainingLetters.setFunctionArguments(vc_subStrRemainingLettersIn);
      vc_subStrRemainingLettersOut.addElement(fn_subStrRemainingLetters);
      sc_subStrRemainingLetters.setColumnExpression(vc_subStrRemainingLettersOut);
      vc_lowerCaseIn.addElement(sc_subStrRemainingLetters);
      fn_lowerCase.setFunctionArguments(vc_lowerCaseIn);
      vc_lowerCaseOut.addElement(fn_lowerCase);
      sc_lowerCase.setColumnExpression(vc_lowerCaseOut);
      concatArgumentsIn.addElement(sc_upperCase);
      concatArgumentsIn.addElement(sc_lowerCase);
      fc_concat.setFunctionArguments(concatArgumentsIn);
      concatArgumentsOut.addElement(fc_concat);
      sc_falseSt.setColumnExpression(concatArgumentsOut);
      ifArguments.addElement(sc_ifCond);
      ifArguments.addElement(sc_trueSt);
      ifArguments.addElement(sc_falseSt);
      this.setFunctionArguments(ifArguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      FunctionCalls length = new FunctionCalls();
      TableColumn lengthFunction = new TableColumn();
      lengthFunction.setColumnName("CHARACTER_LENGTH");
      length.setFunctionName(lengthFunction);
      Vector lengthArgument = new Vector();
      lengthArgument.addElement(this.functionArguments.get(0));
      length.setFunctionArguments(lengthArgument);
      SelectColumn arg = new SelectColumn();
      Vector colExpArg = new Vector();
      colExpArg.addElement(length);
      colExpArg.addElement("-1");
      arg.setColumnExpression(colExpArg);
      FunctionCalls substringForLcase = new FunctionCalls();
      TableColumn substrFunctionForLcase = new TableColumn();
      substrFunctionForLcase.setColumnName("SUBSTRING");
      substringForLcase.setFunctionName(substrFunctionForLcase);
      Vector substrArgumentForLcase = new Vector();
      substrArgumentForLcase.addElement(this.functionArguments.get(0));
      substrArgumentForLcase.addElement(" FROM ");
      substrArgumentForLcase.addElement("2");
      substrArgumentForLcase.addElement(" FOR ");
      substrArgumentForLcase.addElement(arg);
      substringForLcase.setFunctionArguments(substrArgumentForLcase);
      substringForLcase.setStripComma(true);
      FunctionCalls substringForUcase = new FunctionCalls();
      substringForUcase.setFunctionName(substrFunctionForLcase);
      Vector substrArgumentForUcase = new Vector();
      substrArgumentForUcase.addElement(this.functionArguments.get(0));
      substrArgumentForUcase.addElement(" FROM ");
      substrArgumentForUcase.addElement("1");
      substrArgumentForUcase.addElement(" FOR ");
      substrArgumentForUcase.addElement("1");
      substringForUcase.setFunctionArguments(substrArgumentForUcase);
      substringForUcase.setStripComma(true);
      FunctionCalls lowerCase = new FunctionCalls();
      TableColumn lcaseFunction = new TableColumn();
      lcaseFunction.setColumnName("LOWER");
      lowerCase.setFunctionName(lcaseFunction);
      Vector lcaseArgument = new Vector();
      lcaseArgument.addElement(substringForLcase);
      lowerCase.setFunctionArguments(lcaseArgument);
      FunctionCalls upperCase = new FunctionCalls();
      TableColumn ucaseFunction = new TableColumn();
      ucaseFunction.setColumnName("UPPER");
      upperCase.setFunctionName(ucaseFunction);
      Vector ucaseArgument = new Vector();
      ucaseArgument.addElement(substringForUcase);
      upperCase.setFunctionArguments(ucaseArgument);
      SelectColumn arg2 = new SelectColumn();
      Vector colExpArg2 = new Vector();
      colExpArg2.addElement(upperCase);
      colExpArg2.addElement("||");
      colExpArg2.addElement(lowerCase);
      arg2.setColumnExpression(colExpArg2);
      Vector dummyArgument = new Vector();
      dummyArgument.addElement(arg2);
      this.setFunctionArguments(dummyArgument);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      SelectColumn arg = new SelectColumn();
      SelectColumn arg2 = new SelectColumn();
      Vector colExpArg = new Vector();
      Vector colExpArg2 = new Vector();
      FunctionCalls length = new FunctionCalls();
      FunctionCalls substringForLcase = new FunctionCalls();
      FunctionCalls substringForUcase = new FunctionCalls();
      FunctionCalls lowerCase = new FunctionCalls();
      FunctionCalls upperCase = new FunctionCalls();
      TableColumn lengthFunction = new TableColumn();
      TableColumn substrFunctionForLcase = new TableColumn();
      new TableColumn();
      TableColumn lcaseFunction = new TableColumn();
      TableColumn ucaseFunction = new TableColumn();
      lengthFunction.setColumnName("CHARACTER_LENGTH");
      length.setFunctionName(lengthFunction);
      substrFunctionForLcase.setColumnName("SUBSTR");
      substringForLcase.setFunctionName(substrFunctionForLcase);
      substringForUcase.setFunctionName(substrFunctionForLcase);
      lcaseFunction.setColumnName("LOWER");
      ucaseFunction.setColumnName("UPPER");
      lowerCase.setFunctionName(lcaseFunction);
      upperCase.setFunctionName(ucaseFunction);
      Vector lengthArgument = new Vector();
      Vector substrArgumentForLcase = new Vector();
      Vector substrArgumentForUcase = new Vector();
      Vector lcaseArgument = new Vector();
      Vector ucaseArgument = new Vector();
      Vector dummyArgument = new Vector();
      lengthArgument.addElement(this.functionArguments.get(0));
      length.setFunctionArguments(lengthArgument);
      colExpArg.addElement(length);
      colExpArg.addElement("-1");
      arg.setColumnExpression(colExpArg);
      substrArgumentForLcase.addElement(this.functionArguments.get(0));
      substrArgumentForLcase.addElement("2");
      substrArgumentForLcase.addElement(arg);
      substringForLcase.setFunctionArguments(substrArgumentForLcase);
      lcaseArgument.addElement(substringForLcase);
      lowerCase.setFunctionArguments(lcaseArgument);
      substrArgumentForUcase.addElement(this.functionArguments.get(0));
      substrArgumentForUcase.addElement("1");
      substrArgumentForUcase.addElement("1");
      substringForUcase.setFunctionArguments(substrArgumentForUcase);
      ucaseArgument.addElement(substringForUcase);
      upperCase.setFunctionArguments(ucaseArgument);
      colExpArg2.addElement(upperCase);
      colExpArg2.addElement("||");
      colExpArg2.addElement(lowerCase);
      arg2.setColumnExpression(colExpArg2);
      dummyArgument.addElement(arg2);
      this.setFunctionArguments(dummyArgument);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("INITCAP");
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
      this.functionName.setColumnName("INITCAP");
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

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("Initcap");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }
}
