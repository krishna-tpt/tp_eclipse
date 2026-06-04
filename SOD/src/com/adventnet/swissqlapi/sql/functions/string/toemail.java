package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhenStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class toemail extends FunctionCalls {
   public void toemail() {
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      CaseStatement caseStatement = new CaseStatement();
      caseStatement.setCaseClause("CASE");
      Vector arguments = new Vector();
      Vector forThen = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
            forThen.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
            forThen.addElement(this.functionArguments.elementAt(i));
         }
      }

      caseStatement.setWhenStatementList(this.generateWhenThen(arguments, forThen));
      caseStatement.setElseClause("ELSE");
      caseStatement.setElseStatement(this.generateEmptySelectColumn());
      caseStatement.setEndClause("END");
      this.setCustomDataType("EMAIL");
      this.functionName.setColumnName("");
      Vector fnArgs = new Vector();
      SelectColumn tempsc = new SelectColumn();
      Vector tempscColExp = new Vector();
      tempscColExp.add(caseStatement);
      tempsc.setColumnExpression(tempscColExp);
      fnArgs.add(tempsc);
      this.setCustomDataType("EMAIL");
      this.setFunctionArguments(fnArgs);
   }

   private Vector generateWhenThen(Vector args, Vector forThen) {
      Vector whenStatemetList = new Vector();
      WhenStatement ws = new WhenStatement();
      ws.setWhenClause("WHEN");
      ws.setWhenCondition(this.generateWhereCondition(args));
      ws.setThenClause("THEN");
      ws.setThenStatement(this.generateDuplicateSelectColumn(forThen));
      whenStatemetList.add(ws);
      return whenStatemetList;
   }

   private SelectColumn generateEmptySelectColumn() {
      SelectColumn sc = new SelectColumn();
      Vector colExp = new Vector();
      colExp.add("''");
      sc.setColumnExpression(colExp);
      return sc;
   }

   private SelectColumn generateDuplicateSelectColumn(Vector args) {
      SelectColumn scFinal = new SelectColumn();
      Vector colExp = new Vector();
      colExp.addAll(args);
      scFinal.setColumnExpression(colExp);
      return scFinal;
   }

   private WhereExpression generateWhereCondition(Vector args) {
      WhereExpression we = new WhereExpression();
      we.addWhereItem(this.generateWhereItem(args));
      return we;
   }

   private WhereItem generateWhereItem(Vector args) {
      WhereItem wi = new WhereItem();
      WhereColumn lwc = new WhereColumn();
      lwc.setColumnExpression(args);
      wi.setLeftWhereExp(lwc);
      wi.setOperator("RLIKE");
      WhereColumn rwc = new WhereColumn();
      Vector colExp = new Vector();
      colExp.add("'^.+@.+\\..+$'");
      rwc.setColumnExpression(colExp);
      wi.setRightWhereExp(rwc);
      return wi;
   }
}
