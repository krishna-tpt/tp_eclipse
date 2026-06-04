package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class CommitStatement implements SwisSQLStatement {
   private String commit;
   private String transaction;
   private String transactionNameVariable;
   private String work;
   private String rollback;

   public void setCommit(String commit) {
      this.commit = commit;
   }

   public void setTransaction(String transaction) {
      this.transaction = transaction;
   }

   public void setTransactionNameVariable(String transactionNameVariable) {
      this.transactionNameVariable = transactionNameVariable;
   }

   public void setWork(String work) {
      this.work = work;
   }

   public void setRollback(String rollback) {
      this.rollback = rollback;
   }

   public String getCommit() {
      return this.commit;
   }

   public String getTransaction() {
      return this.transaction;
   }

   public String getTransactionNameVariable() {
      return this.transactionNameVariable;
   }

   public String getWork() {
      return this.work;
   }

   public String getRollback() {
      return this.rollback;
   }

   public CommentClass getCommentClass() {
      return null;
   }

   public UserObjectContext getObjectContext() {
      return null;
   }

   public String removeIndent(String str) {
      return str;
   }

   public void setCommentClass(CommentClass commentClass) {
   }

   public void setObjectContext(UserObjectContext userObjectContext) {
   }

   public String toANSIString() throws ConvertException {
      return this.toANSICommit().toString();
   }

   public String toTeradataString() throws ConvertException {
      return this.toTeradataCommit().toString();
   }

   public String toDB2String() throws ConvertException {
      return this.toDB2Commit().toString();
   }

   public String toInformixString() throws ConvertException {
      return this.toInformixCommit().toString();
   }

   public String toMSSQLServerString() throws ConvertException {
      return this.toSQLServerCommit().toString();
   }

   public String toMySQLString() throws ConvertException {
      return this.toMysqlCommit().toString();
   }

   public String toOracleString() throws ConvertException {
      return this.toOracleCommit().toString();
   }

   public String toPostgreSQLString() throws ConvertException {
      return this.toPostgresCommit().toString();
   }

   public String toBigQueryString() throws ConvertException {
      return this.toBigQueryCommit().toString();
   }

   public String toSybaseString() throws ConvertException {
      return this.toSybaseCommit().toString();
   }

   public String toTimesTenString() throws ConvertException {
      return this.toTimesTenCommit().toString();
   }

   public String toNetezzaString() throws ConvertException {
      return this.toNetezzaCommit().toString();
   }

   public String toSnowflakeString() throws ConvertException {
      return this.toSnowflakeCommit().toString();
   }

   public String toAthenaString() throws ConvertException {
      return null;
   }

   public String toSapHanaString() throws ConvertException {
      return null;
   }

   public String toSqliteString() throws ConvertException {
      return null;
   }

   public String toExcelString() throws ConvertException {
      return null;
   }

   public String toMsAccessJdbcString() throws ConvertException {
      return null;
   }

   public CommitStatement toOracleCommit() throws ConvertException {
      CommitStatement commitStmt = new CommitStatement();
      if (this.commit != null) {
         commitStmt.setCommit(this.commit);
      }

      commitStmt.setTransaction((String)null);
      commitStmt.setTransactionNameVariable((String)null);
      if (this.work != null) {
         commitStmt.setWork(this.work);
      }

      return commitStmt;
   }

   public CommitStatement toSQLServerCommit() throws ConvertException {
      CommitStatement commitStmt = new CommitStatement();
      if (this.commit != null) {
         commitStmt.setCommit(this.commit);
      }

      commitStmt.setTransaction(this.transaction);
      commitStmt.setTransactionNameVariable(this.transactionNameVariable);
      if (this.work != null) {
         commitStmt.setWork(this.work);
      }

      return commitStmt;
   }

   public CommitStatement toSybaseCommit() throws ConvertException {
      CommitStatement commitStmt = new CommitStatement();
      if (this.commit != null) {
         commitStmt.setCommit(this.commit);
      }

      commitStmt.setTransaction(this.transaction);
      commitStmt.setTransactionNameVariable(this.transactionNameVariable);
      if (this.work != null) {
         commitStmt.setWork(this.work);
      }

      return commitStmt;
   }

   public CommitStatement toDB2Commit() throws ConvertException {
      CommitStatement commitStmt = new CommitStatement();
      if (this.commit != null) {
         commitStmt.setCommit(this.commit);
      }

      commitStmt.setTransaction((String)null);
      commitStmt.setTransactionNameVariable((String)null);
      if (this.work != null) {
         commitStmt.setWork(this.work);
      }

      return commitStmt;
   }

   public CommitStatement toBigQueryCommit() throws ConvertException {
      CommitStatement commitStmt = new CommitStatement();
      if (this.commit != null) {
         commitStmt.setCommit(this.commit);
      }

      commitStmt.setTransaction((String)null);
      commitStmt.setTransactionNameVariable((String)null);
      if (this.work != null) {
         commitStmt.setWork(this.work);
      }

      return commitStmt;
   }

   public CommitStatement toPostgresCommit() throws ConvertException {
      CommitStatement commitStmt = new CommitStatement();
      if (this.commit != null) {
         commitStmt.setCommit(this.commit);
      }

      commitStmt.setTransaction((String)null);
      commitStmt.setTransactionNameVariable((String)null);
      if (this.work != null) {
         commitStmt.setWork(this.work);
      }

      return commitStmt;
   }

   public CommitStatement toInformixCommit() throws ConvertException {
      CommitStatement commitStmt = new CommitStatement();
      if (this.commit != null) {
         commitStmt.setCommit(this.commit);
      }

      commitStmt.setTransaction((String)null);
      commitStmt.setTransactionNameVariable((String)null);
      if (this.work != null) {
         commitStmt.setWork(this.work);
      }

      return commitStmt;
   }

   public CommitStatement toANSICommit() throws ConvertException {
      CommitStatement commitStmt = new CommitStatement();
      if (this.commit != null) {
         commitStmt.setCommit(this.commit);
      }

      commitStmt.setTransaction((String)null);
      commitStmt.setTransactionNameVariable((String)null);
      if (this.work != null) {
         commitStmt.setWork(this.work);
      }

      return commitStmt;
   }

   public CommitStatement toTeradataCommit() throws ConvertException {
      CommitStatement commitStmt = new CommitStatement();
      if (this.commit != null) {
         commitStmt.setCommit(this.commit);
      }

      commitStmt.setTransaction((String)null);
      commitStmt.setTransactionNameVariable((String)null);
      if (this.work != null) {
         commitStmt.setWork(this.work);
      }

      return commitStmt;
   }

   public CommitStatement toMysqlCommit() throws ConvertException {
      CommitStatement commitStmt = new CommitStatement();
      if (this.commit != null) {
         commitStmt.setCommit(this.commit);
      }

      commitStmt.setTransaction((String)null);
      commitStmt.setTransactionNameVariable((String)null);
      if (this.work != null) {
         commitStmt.setWork(this.work);
      }

      return commitStmt;
   }

   public CommitStatement toSnowflakeCommit() throws ConvertException {
      CommitStatement commitStmt = new CommitStatement();
      if (this.commit != null) {
         commitStmt.setCommit(this.commit);
      }

      commitStmt.setTransaction((String)null);
      commitStmt.setTransactionNameVariable((String)null);
      if (this.work != null) {
         commitStmt.setWork(this.work);
      }

      return commitStmt;
   }

   public CommitStatement toTimesTenCommit() throws ConvertException {
      CommitStatement commitStmt = new CommitStatement();
      if (this.commit != null) {
         commitStmt.setCommit(this.commit);
      }

      commitStmt.setTransaction(this.transaction);
      commitStmt.setTransactionNameVariable(this.transactionNameVariable);
      if (this.work != null) {
         commitStmt.setWork(this.work);
      }

      return commitStmt;
   }

   public CommitStatement toNetezzaCommit() throws ConvertException {
      CommitStatement commitStmt = new CommitStatement();
      if (this.commit != null) {
         commitStmt.setCommit(this.commit);
      }

      if (this.rollback != null) {
         commitStmt.setRollback(this.rollback);
      }

      commitStmt.setTransaction((String)null);
      commitStmt.setTransactionNameVariable((String)null);
      if (this.work != null) {
         commitStmt.setWork(this.work);
      }

      return commitStmt;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      String indentString = "\n";
      if (this.commit != null) {
         sb.append(indentString + this.commit.toUpperCase());
      }

      if (this.rollback != null) {
         sb.append(indentString + this.rollback.toUpperCase());
      }

      if (this.transaction != null) {
         sb.append(" " + this.transaction.toUpperCase());
      }

      if (this.transactionNameVariable != null) {
         sb.append(" " + this.transactionNameVariable);
      }

      if (this.work != null) {
         sb.append(" " + this.work.toUpperCase());
      }

      return sb.toString();
   }

   public String toVectorWiseString() throws ConvertException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public SwisSQLStatement toVectorWise() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toInformix() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toNetezza() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toTimesTen() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toOracle() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSqlite() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSapHana() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toAthena() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSnowflake() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toMySQL() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toPostgreSQL() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toBigQuery() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toDB2() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSybase() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toMSSQLServer() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toTeradata() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toANSI() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toMsAccessJdbc() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toExcel() throws ConvertException {
      return null;
   }
}
