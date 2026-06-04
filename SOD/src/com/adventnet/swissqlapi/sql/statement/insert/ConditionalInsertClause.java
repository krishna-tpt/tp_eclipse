package com.adventnet.swissqlapi.sql.statement.insert;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import java.util.Vector;

public class ConditionalInsertClause {
   WhereExpression whenExp = null;
   InsertQueryStatement iQuery = null;

   public void setWhenExpression(WhereExpression we) {
      this.whenExp = we;
   }

   public void setInsertStmt(InsertQueryStatement multiInsertStmt) {
      this.iQuery = multiInsertStmt;
   }

   public InsertQueryStatement toNetezza(InsertQueryStatement insertQueryStmt) throws ConvertException {
      InsertQueryStatement insertQuery = new InsertQueryStatement();
      insertQuery.setInsertClause(this.iQuery.getInsertClause());
      SelectQueryStatement insertSubQuery = new SelectQueryStatement();
      SelectStatement insertSubQuerySelectStmt = new SelectStatement();
      insertSubQuerySelectStmt.setSelectClause("SELECT");
      Vector selectItems = new Vector();
      if (this.iQuery != null && this.iQuery.getValuesClause() != null) {
         for(int j = 0; j < this.iQuery.getValuesClause().getValuesList().size(); ++j) {
            Object obj = this.iQuery.getValuesClause().getValuesList().get(j);
            if (!obj.toString().equalsIgnoreCase("(") && !obj.toString().equalsIgnoreCase(")")) {
               selectItems.add(obj);
            }
         }
      }

      if (selectItems.size() <= 0) {
         selectItems.add("*");
      }

      insertSubQuerySelectStmt.setSelectItemList(selectItems);
      FromClause fromClause = new FromClause();
      fromClause.setFromClause("FROM");
      FromTable fromTable = new FromTable();
      fromTable.setTableName(insertQueryStmt.getSubQuery());
      Vector fromItems = new Vector();
      fromItems.add(fromTable);
      fromClause.setFromItemList(fromItems);
      insertSubQuery.setFromClause(fromClause);
      insertSubQuery.setSelectStatement(insertSubQuerySelectStmt);
      insertSubQuery.setWhereExpression(this.whenExp);
      insertQuery.setSubQuery(insertSubQuery);
      insertQuery.setValuesClause((ValuesClause)null);
      return insertQuery;
   }
}
