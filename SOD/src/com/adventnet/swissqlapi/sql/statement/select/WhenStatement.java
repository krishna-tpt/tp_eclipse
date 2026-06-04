package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;

public class WhenStatement {
   private String WhenClause = null;
   private WhereExpression WhenCondition = null;
   private String ThenClause = null;
   private SelectColumn ThenStatement = null;
   private UserObjectContext context = null;
   private CommentClass commentObj;

   public void setWhenClause(String s_when_clause) {
      this.WhenClause = s_when_clause;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   public void setWhenCondition(WhereExpression we_when_condition) {
      this.WhenCondition = we_when_condition;
   }

   public void setThenClause(String s_then_clause) {
      this.ThenClause = s_then_clause;
   }

   public void setThenStatement(SelectColumn sc_then_statement) {
      this.ThenStatement = sc_then_statement;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public String getWhenClause() {
      return this.WhenClause;
   }

   public WhereExpression getWhenCondition() {
      return this.WhenCondition;
   }

   public String getThenClause() {
      return this.ThenClause;
   }

   public SelectColumn getThenStatement() {
      return this.ThenStatement;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public WhenStatement toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toOracleSelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toOracleSelect(to_sqs, from_sqs));
      return when_statement;
   }

   public WhenStatement toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toInformixSelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toInformixSelect(to_sqs, from_sqs));
      return when_statement;
   }

   public WhenStatement toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toDB2Select(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toDB2Select(to_sqs, from_sqs));
      return when_statement;
   }

   public WhenStatement toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toMSSQLServerSelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toMSSQLServerSelect(to_sqs, from_sqs));
      return when_statement;
   }

   public WhenStatement toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toSybaseSelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toSybaseSelect(to_sqs, from_sqs));
      return when_statement;
   }

   public WhenStatement toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toBigQuerySelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toBigQuerySelect(to_sqs, from_sqs));
      when_statement.setObjectContext(this.context);
      return when_statement;
   }

   public WhenStatement toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toAthenaSelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toAthenaSelect(to_sqs, from_sqs));
      when_statement.setObjectContext(this.context);
      return when_statement;
   }

   public WhenStatement toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toSapHanaSelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toSapHanaSelect(to_sqs, from_sqs));
      when_statement.setObjectContext(this.context);
      return when_statement;
   }

   public WhenStatement toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toSqliteSelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toSqliteSelect(to_sqs, from_sqs));
      when_statement.setObjectContext(this.context);
      return when_statement;
   }

   public WhenStatement toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toExcelSelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toExcelSelect(to_sqs, from_sqs));
      when_statement.setObjectContext(this.context);
      return when_statement;
   }

   public WhenStatement toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toMsAccessJdbcSelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toMsAccessJdbcSelect(to_sqs, from_sqs));
      when_statement.setObjectContext(this.context);
      return when_statement;
   }

   public WhenStatement toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toPostgreSQLSelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toPostgreSQLSelect(to_sqs, from_sqs));
      when_statement.setObjectContext(this.context);
      return when_statement;
   }

   public WhenStatement toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toMySQLSelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toMySQLSelect(to_sqs, from_sqs));
      return when_statement;
   }

   public WhenStatement toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toSnowflakeSelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toSnowflakeSelect(to_sqs, from_sqs));
      when_statement.setObjectContext(this.context);
      return when_statement;
   }

   public WhenStatement toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toANSISelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toANSISelect(to_sqs, from_sqs));
      return when_statement;
   }

   public WhenStatement toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      this.WhenCondition.setCaseExpressionBool(true);
      WhereExpression whenExp = this.WhenCondition.toTeradataSelect(to_sqs, from_sqs);
      if (this.WhenCondition.getRownumClause() != null) {
         whenExp.setRownumClause(this.WhenCondition.getRownumClause());
      }

      when_statement.setWhenCondition(whenExp);
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toTeradataSelect(to_sqs, from_sqs));
      return when_statement;
   }

   public WhenStatement toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toNetezzaSelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toNetezzaSelect(to_sqs, from_sqs));
      return when_statement;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.commentObj != null) {
         sb.append(this.commentObj.toString().trim() + " ");
      }

      sb.append(this.WhenClause);
      this.WhenCondition.setObjectContext(this.context);
      sb.append(" " + this.WhenCondition.toString());
      sb.append(" " + this.ThenClause);
      this.ThenStatement.setObjectContext(this.context);
      sb.append(" " + this.ThenStatement.toString());
      return sb.toString();
   }

   public WhenStatement toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement when_statement = new WhenStatement();
      when_statement.setWhenClause(this.WhenClause);
      when_statement.setWhenCondition(this.WhenCondition.toVectorWiseSelect(to_sqs, from_sqs));
      when_statement.setThenClause(this.ThenClause);
      when_statement.setThenStatement(this.ThenStatement.toVectorWiseSelect(to_sqs, from_sqs));
      return when_statement;
   }

   public WhenStatement toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhenStatement whenStmtConv = new WhenStatement();
      if (this.WhenClause != null) {
         whenStmtConv.setWhenClause(this.WhenClause);
      }

      if (this.WhenCondition != null) {
         whenStmtConv.setWhenCondition(this.WhenCondition.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.ThenClause != null) {
         whenStmtConv.setThenClause(this.ThenClause);
      }

      if (this.ThenStatement != null) {
         whenStmtConv.setThenStatement(this.ThenStatement.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.context != null) {
         whenStmtConv.setObjectContext(this.context);
      }

      if (this.commentObj != null) {
         whenStmtConv.setCommentClass(this.commentObj);
      }

      return whenStmtConv;
   }
}
