package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class WeekOfMonth extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      int argLength = this.functionArguments.size();

      for(int i_count = 0; i_count < argLength; ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0) instanceof String) {
               String dateString = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression().get(0).toString();
               dateString = "CAST(" + this.handleStringLiteralForDateTime(dateString, from_sqs) + " AS TIMESTAMP)";
               arguments.addElement(dateString);
            } else {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            }
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String date = arguments.get(0).toString();
      arguments = new Vector();
      SelectColumn weekOfMonthSC = new SelectColumn();
      FunctionCalls zrWeekFC = new FunctionCalls();
      zrWeekFC.getFunctionName().setColumnName("ZR_WeekDtNwkStrtDay");
      Vector zrWeekFCArgs = new Vector();
      SelectColumn dateFSC = new SelectColumn();
      Vector dateFSCColExp = new Vector();
      FunctionCalls dateFC = new FunctionCalls();
      dateFC.getFunctionName().setColumnName("date");
      Vector dateFCArgs = new Vector();
      SelectColumn dateSC = new SelectColumn();
      Vector dateSCVector = new Vector();
      dateSCVector.addElement(date);
      dateSC.setColumnExpression(dateSCVector);
      dateFCArgs.addElement(dateSC);
      dateFC.setFunctionArguments(dateFCArgs);
      dateFSCColExp.addElement(dateFC);
      dateFSC.setColumnExpression(dateFSCColExp);
      zrWeekFCArgs.addElement(dateFSC);
      zrWeekFCArgs.addElement("4");
      zrWeekFC.setFunctionArguments(zrWeekFCArgs);
      arguments.addElement(zrWeekFC);
      FunctionCalls zrWeekFC2 = new FunctionCalls();
      zrWeekFC2.getFunctionName().setColumnName("ZR_WeekDtNwkStrtDay");
      Vector zrWeekFC2Args = new Vector();
      SelectColumn dateFSC2 = new SelectColumn();
      Vector dateFSC2ColExp = new Vector();
      FunctionCalls dateFC2 = new FunctionCalls();
      dateFC2.getFunctionName().setColumnName("DATE");
      Vector dateFC2Args = new Vector();
      SelectColumn castFSC = new SelectColumn();
      Vector castFSCColExp = new Vector();
      FunctionCalls castFC = new FunctionCalls();
      castFC.getFunctionName().setColumnName("cast");
      Vector castFCArgs = new Vector();
      SelectColumn extractFSC = new SelectColumn();
      Vector extractFSCColExp = new Vector();
      FunctionCalls extractFC = new FunctionCalls();
      extractFC.getFunctionName().setColumnName("extract");
      Vector extractFCArgs = new Vector();
      extractFC.setTrailingString("DAY");
      extractFC.setFromInTrim("from");
      extractFCArgs.addElement(dateSC);
      extractFC.setFunctionArguments(extractFCArgs);
      extractFSCColExp.addElement(extractFC);
      extractFSC.setColumnExpression(extractFSCColExp);
      castFCArgs.addElement(extractFSC);
      castFC.setAsDatatype("as");
      castFCArgs.addElement("int");
      castFC.setFunctionArguments(castFCArgs);
      castFSCColExp.addElement(dateSC);
      castFSCColExp.addElement("-");
      castFSCColExp.addElement(castFC);
      castFSCColExp.addElement("-");
      castFSCColExp.addElement("1");
      castFSC.setColumnExpression(castFSCColExp);
      dateFC2Args.addElement(castFSC);
      dateFC2.setFunctionArguments(dateFC2Args);
      dateFSC2ColExp.addElement(dateFC2);
      dateFSC2.setColumnExpression(dateFSC2ColExp);
      zrWeekFC2Args.addElement(dateFSC2);
      zrWeekFC2Args.addElement("4");
      zrWeekFC2.setFunctionArguments(zrWeekFC2Args);
      arguments.addElement("-");
      arguments.addElement(zrWeekFC2);
      arguments.addElement("+");
      arguments.addElement("1");
      weekOfMonthSC.setColumnExpression(arguments);
      this.functionName.setColumnName("");
      Vector fnArgs = new Vector();
      fnArgs.addElement(weekOfMonthSC);
      this.setFunctionArguments(fnArgs);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int argLength = this.functionArguments.size();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < argLength; ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Object date = arguments.get(0);
      Vector date1 = new Vector(1);
      Vector date2 = new Vector(1);
      Vector date3 = new Vector(1);
      if (date instanceof SelectColumn) {
         date1.add(((SelectColumn)this.functionArguments.elementAt(0)).toMySQLSelect(to_sqs, from_sqs));
         date2.add(((SelectColumn)this.functionArguments.elementAt(0)).toMySQLSelect(to_sqs, from_sqs));
         date3.add(((SelectColumn)this.functionArguments.elementAt(0)).toMySQLSelect(to_sqs, from_sqs));
      } else {
         date1.add(date);
         date2.add(date);
         date3.add(date);
      }

      SelectColumn include1 = new SelectColumn();
      Vector include1V = new Vector();
      SelectColumn div7 = new SelectColumn();
      div7.setOpenBrace("(");
      Vector div7V = new Vector();
      SelectColumn daystoCalculate = new SelectColumn();
      daystoCalculate.setOpenBrace("(");
      Vector daystoCalculateV = new Vector();
      FunctionCalls dayofmonth = new FunctionCalls();
      TableColumn DOM = new TableColumn();
      DOM.setColumnName("DAYOFMONTH");
      dayofmonth.setFunctionName(DOM);
      Vector DOMpara = new Vector();
      DOMpara.add(date1.get(0));
      dayofmonth.setFunctionArguments(DOMpara);
      daystoCalculateV.add(dayofmonth);
      daystoCalculateV.add("-");
      daystoCalculateV.add("2");
      daystoCalculateV.add("+");
      SelectColumn dayofweek = new SelectColumn();
      Vector DOW = new Vector();
      FunctionCalls dayofweekF = new FunctionCalls();
      TableColumn dayw = new TableColumn();
      dayw.setColumnName("DAYOFWEEK");
      dayofweekF.setFunctionName(dayw);
      Vector daywArg = new Vector();
      FunctionCalls datesub = new FunctionCalls();
      TableColumn ds = new TableColumn();
      ds.setColumnName("DATE_SUB");
      datesub.setFunctionName(ds);
      Vector dsArg = new Vector();
      dsArg.add(date2.get(0));
      SelectColumn Inntervalday = new SelectColumn();
      Vector IntervldayV = new Vector();
      TableColumn interv = new TableColumn();
      interv.setColumnName("INTERVAL");
      IntervldayV.add(interv);
      SelectColumn dayofmonth2 = new SelectColumn();
      dayofmonth2.setOpenBrace("(");
      Vector DOM2 = new Vector();
      FunctionCalls finalday = new FunctionCalls();
      TableColumn FDOM = new TableColumn();
      FDOM.setColumnName("DAYOFMONTH");
      finalday.setFunctionName(FDOM);
      Vector finaldayArg = new Vector();
      finaldayArg.add(date3.get(0));
      finalday.setFunctionArguments(finaldayArg);
      DOM2.add(finalday);
      DOM2.add("-");
      DOM2.add("1");
      dayofmonth2.setCloseBrace(")");
      dayofmonth2.setColumnExpression(DOM2);
      IntervldayV.add(dayofmonth2);
      IntervldayV.add("DAY");
      Inntervalday.setColumnExpression(IntervldayV);
      dsArg.add(Inntervalday);
      datesub.setFunctionArguments(dsArg);
      daywArg.add(datesub);
      dayofweekF.setFunctionArguments(daywArg);
      DOW.add(dayofweekF);
      dayofweek.setColumnExpression(DOW);
      daystoCalculateV.add(dayofweek);
      daystoCalculate.setColumnExpression(daystoCalculateV);
      daystoCalculate.setCloseBrace(")");
      div7V.add(daystoCalculate);
      div7V.add("DIV");
      div7V.add("7");
      div7.setColumnExpression(div7V);
      div7.setCloseBrace(")");
      include1V.add(div7);
      include1V.add("+");
      include1V.add("1");
      include1.setColumnExpression(include1V);
      Vector finalArgumnets = new Vector();
      finalArgumnets.add(include1);
      this.functionName.setColumnName("");
      this.setFunctionArguments(finalArgumnets);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int argLength = this.functionArguments.size();
      StringBuffer[] argu = new StringBuffer[this.functionArguments.size()];

      for(int i_count = 0; i_count < argLength; ++i_count) {
         argu[i_count] = new StringBuffer();
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 0) {
               this.handleStringLiteralForDateTime(from_sqs, i_count, true);
            }

            argu[i_count].append(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            argu[i_count].append(this.functionArguments.elementAt(i_count));
         }
      }

      String date = "";
      String qry = "";
      date = argu[0].toString();
      qry = "week(" + date + ", 0) -week(timestamp((" + date + "))- (DAY(" + date + ")-1), 0) +1";
      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }
}
