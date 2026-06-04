package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhenStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class todecisionbox extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement caseStatement = new CaseStatement();
      caseStatement.setCaseClause("CASE");
      WhereExpression we = new WhereExpression();
      Vector v_wi = new Vector();
      WhereItem wi = new WhereItem();
      WhereColumn wc = new WhereColumn();
      Vector v_wc = new Vector();
      FunctionCalls fc = new FunctionCalls();
      TableColumn tc = new TableColumn();
      tc.setColumnName("LCASE");
      fc.setFunctionName(tc);
      Vector fn_Args = new Vector();
      SelectColumn sc = new SelectColumn();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      sc.setColumnExpression(arguments);
      fn_Args.add(sc);
      fc.setFunctionArguments(fn_Args);
      v_wc.add(fc);
      wc.setColumnExpression(v_wc);
      wi.setLeftWhereExp(wc);
      v_wi.add(wi);
      we.setWhereItem(v_wi);
      caseStatement.setCaseCondition(we);
      Vector whenThens = new Vector();
      whenThens.add(this.generateWhenThen("'yes'", "1"));
      whenThens.add(this.generateWhenThen("'true'", "1"));
      whenThens.add(this.generateWhenThen("'1'", "1"));
      whenThens.add(this.generateWhenThen("'no'", "0"));
      whenThens.add(this.generateWhenThen("'false'", "0"));
      whenThens.add(this.generateWhenThen("'0'", "0"));
      whenThens.add(this.generateWhenThen("'off'", "0"));
      whenThens.add(this.generateWhenThen("'on'", "1"));
      caseStatement.setWhenStatementList(whenThens);
      caseStatement.setElseClause("ELSE");
      caseStatement.setElseStatement(this.getSelectColumn("null"));
      caseStatement.setEndClause("END");
      this.functionName.setColumnName("");
      Vector fnArgs = new Vector();
      SelectColumn tempsc = new SelectColumn();
      Vector tempscColExp = new Vector();
      tempscColExp.add(caseStatement);
      tempsc.setColumnExpression(tempscColExp);
      fnArgs.add(tempsc);
      this.setCustomDataType("BOOLEAN");
      this.setFunctionArguments(fnArgs);
   }

   public WhereExpression getWhereExpression(String s) {
      WhereExpression we = new WhereExpression();
      Vector v_wi = new Vector();
      WhereItem wi = new WhereItem();
      WhereColumn wc = new WhereColumn();
      Vector colExp = new Vector();
      colExp.add(s);
      wc.setColumnExpression(colExp);
      wi.setLeftWhereExp(wc);
      v_wi.add(wi);
      we.setWhereItem(v_wi);
      return we;
   }

   public SelectColumn getSelectColumn(String s) {
      SelectColumn sc = new SelectColumn();
      Vector colexp = new Vector();
      colexp.add(s);
      sc.setColumnExpression(colexp);
      return sc;
   }

   public WhenStatement generateWhenThen(String inp, String out) {
      WhenStatement ws = new WhenStatement();
      ws.setWhenClause("WHEN");
      ws.setWhenCondition(this.getWhereExpression(inp));
      ws.setThenClause("THEN");
      ws.setThenStatement(this.getSelectColumn(out));
      return ws;
   }
}
