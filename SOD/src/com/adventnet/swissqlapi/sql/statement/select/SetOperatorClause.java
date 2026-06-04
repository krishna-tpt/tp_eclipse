package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class SetOperatorClause {
   private String SetClause;
   private SelectQueryStatement query_statement;
   private String OpenBrace;
   private String CloseBrace;
   private WhereExpression whereExpression = null;
   private boolean checkSetOperator;
   private UserObjectContext context = null;
   private CommentClass commentObj;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setSetClause(String s_sc) {
      this.SetClause = s_sc;
   }

   public void setSelectQueryStatement(SelectQueryStatement q_qs) {
      this.query_statement = q_qs;
   }

   public void setOpenBrace(String s_ob) {
      this.OpenBrace = s_ob;
   }

   public void setCloseBrace(String s_cb) {
      this.CloseBrace = s_cb;
   }

   public void setCheckSetOperator(boolean b_cso) {
      this.checkSetOperator = b_cso;
   }

   public void setWhereExpression(WhereExpression we) {
      this.whereExpression = we;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public boolean getCheckSetOperator() {
      return this.checkSetOperator;
   }

   public String getSetClause() {
      return this.SetClause;
   }

   public SelectQueryStatement getSelectQueryStatement() {
      return this.query_statement;
   }

   public String getOpenBrace() {
      return this.OpenBrace;
   }

   public String getCloseBrace() {
      return this.CloseBrace;
   }

   public SetOperatorClause toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      soc.setOpenBrace(this.OpenBrace);
      if (this.SetClause.equalsIgnoreCase("MINUS")) {
         soc.setSetClause("EXCEPT");
      } else {
         soc.setSetClause(this.SetClause);
      }

      soc.setSelectQueryStatement(this.query_statement.toANSI());
      soc.setCloseBrace(this.CloseBrace);
      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      soc.setOpenBrace(this.OpenBrace);
      if (this.SetClause.equalsIgnoreCase("MINUS")) {
         soc.setSetClause("EXCEPT");
      } else {
         soc.setSetClause(this.SetClause);
      }

      if (from_sqs != null && this.query_statement != null) {
         this.query_statement.setTopLevel(from_sqs.getTopLevel());
      }

      soc.setSelectQueryStatement(this.query_statement.toTeradata());
      soc.setCloseBrace(this.CloseBrace);
      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      soc.setOpenBrace(this.OpenBrace);
      if (this.SetClause.equalsIgnoreCase("MINUS")) {
         soc.setSetClause("EXCEPT");
      } else {
         soc.setSetClause(this.SetClause);
      }

      soc.setSelectQueryStatement(this.query_statement.toDB2());
      soc.setCloseBrace(this.CloseBrace);
      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      soc.setOpenBrace(this.OpenBrace);
      if (this.SetClause.equalsIgnoreCase("MINUS") | this.SetClause.equalsIgnoreCase("EXCEPT ALL")) {
         soc.setSetClause("EXCEPT");
      } else if (this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
         soc.setSetClause("INTERSECT");
      } else {
         soc.setSetClause(this.SetClause);
      }

      boolean fromSQSISNotNull = from_sqs != null;
      if (fromSQSISNotNull) {
         this.query_statement.addAllIndexPositionsForStringLiterals(from_sqs.getIndexPositionsForStringLiterals());
         this.query_statement.addAllIndexPositionsForNULLString(from_sqs.getIndexPositionsForNULLString());
      }

      this.query_statement.setSetOperatorQuery(true);
      if (fromSQSISNotNull) {
         this.query_statement.setAmazonRedShiftFlag(from_sqs.isAmazonRedShift());
         this.query_statement.setPgsqlFlag(from_sqs.isPgsqlLive());
         this.query_statement.setVerticaFlag(from_sqs.isVerticaDb());
         this.query_statement.setMSAzureFlag(from_sqs.isMSAzure());
         this.query_statement.setMSAzureWareHouseFlag(from_sqs.isMSAzureWareHouse());
         this.query_statement.setOracleLiveFlag(from_sqs.isOracleLive());
         this.query_statement.setDenodoFlag(from_sqs.isDenodo());
         this.query_statement.setQueryConversionPropHandler(from_sqs.getQueryConvPropHandler());
         if (from_sqs.isCTESupported()) {
            this.query_statement.setReportsMeta(from_sqs.getReportsMeta());
         }
      }

      SelectQueryStatement sqs = this.query_statement.toPostgreSQL();
      if (fromSQSISNotNull) {
         sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
         sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
         from_sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
         Set nullSet = from_sqs.getIndexPositionsForNULLString();
         nullSet.clear();
         from_sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
      }

      soc.setSelectQueryStatement(sqs);
      soc.setCloseBrace(this.CloseBrace);
      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      WhereExpression we = new WhereExpression();
      WhereItem wi = new WhereItem();
      if (from_sqs != null) {
         this.query_statement.setMySqlLiveFlag(from_sqs.isMySqlLive());
         this.query_statement.setMongoDbFlag(from_sqs.isMongoDb());
         this.query_statement.setSQLDialect(from_sqs.getSQLDialect());
         this.query_statement.setValidationHandler(from_sqs.getValidationHandler());
         this.query_statement.setQueryConvDataHandler(from_sqs.getQueryConvDataHandler());
         this.query_statement.setIsQtNewFlow(from_sqs.getIsQtNewFlow());
         this.query_statement.setQueryConversionPropHandler(from_sqs.getQueryConvPropHandler());
         this.query_statement.setInstanceDataTypeHandler(from_sqs.getinstanceDataTypeHandler());
      }

      SelectQueryStatement sqs = this.query_statement.toMySQL();
      sqs.setMySqlLiveFlag(this.query_statement.isMySqlLive());
      sqs.setMongoDbFlag(this.query_statement.isMongoDb());
      sqs.setSQLDialect(this.query_statement.getSQLDialect());
      sqs.setValidationHandler(this.query_statement.getValidationHandler());
      sqs.setQueryConversionPropHandler(this.query_statement.getQueryConvPropHandler());
      sqs.setQueryConvDataHandler(this.query_statement.getQueryConvDataHandler());
      soc.setSelectQueryStatement(sqs);
      soc.setOpenBrace(this.OpenBrace);
      soc.setCloseBrace(this.CloseBrace);
      if (!this.SetClause.equalsIgnoreCase("UNION") && !this.SetClause.equalsIgnoreCase("UNION ALL")) {
         if (this.SetClause.equalsIgnoreCase("INTERSECT") | this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
            wi.setOperator("EXISTS");
         } else {
            wi.setOperator("NOT EXISTS");
            Vector query_statementFromTableList = sqs.getFromClause().getFromItemList();
            int qListSize = query_statementFromTableList.size();

            for(int qi = 0; qi < qListSize; ++qi) {
               FromTable fromTableObj = (FromTable)query_statementFromTableList.get(qi);
               if (fromTableObj.getAliasName() == null || fromTableObj.getAliasName().equalsIgnoreCase(" ")) {
                  fromTableObj.setAliasName("ADV_INNER_ALIAS_" + qi);
               }
            }
         }

         if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().trim().equals("")) {
            sqs.getWhereExpression().addOperator("AND");
            sqs.getWhereExpression().addWhereExpression(this.createWhereExp(to_sqs, sqs));
         } else {
            sqs.setWhereExpression(this.createWhereExp(to_sqs, sqs));
         }

         wi.setRightWhereSubQuery(sqs);
         we.addWhereItem(wi);
         if (to_sqs.getWhereExpression() != null && !to_sqs.getWhereExpression().toString().trim().equals("")) {
            to_sqs.getWhereExpression().addOperator("AND");
            to_sqs.getWhereExpression().addWhereExpression(we);
         } else {
            to_sqs.setWhereExpression(we);
         }

         if (to_sqs.getSelectStatement() != null) {
            to_sqs.getSelectStatement().setSelectQualifier("DISTINCT");
         }

         soc = null;
      } else {
         soc.setSetClause(this.SetClause);
      }

      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      soc.setOpenBrace(this.OpenBrace);
      if (this.SetClause.equalsIgnoreCase("MINUS") | this.SetClause.equalsIgnoreCase("EXCEPT ALL")) {
         soc.setSetClause("EXCEPT");
      } else if (this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
         soc.setSetClause("INTERSECT");
      } else {
         soc.setSetClause(this.SetClause);
      }

      boolean fromSQSISNotNull = from_sqs != null;
      if (fromSQSISNotNull) {
         this.query_statement.addAllIndexPositionsForStringLiterals(from_sqs.getIndexPositionsForStringLiterals());
         this.query_statement.addAllIndexPositionsForNULLString(from_sqs.getIndexPositionsForNULLString());
      }

      this.query_statement.setSetOperatorQuery(true);
      if (fromSQSISNotNull) {
         this.query_statement.setAmazonRedShiftFlag(from_sqs.isAmazonRedShift());
         this.query_statement.setPgsqlFlag(from_sqs.isPgsqlLive());
         this.query_statement.setMSAzureFlag(from_sqs.isMSAzure());
         this.query_statement.setSnowflakeFlag(from_sqs.isSnowflake());
         this.query_statement.setMSAzureWareHouseFlag(from_sqs.isMSAzureWareHouse());
         this.query_statement.setOracleLiveFlag(from_sqs.isOracleLive());
         this.query_statement.setDenodoFlag(from_sqs.isDenodo());
         this.query_statement.setQueryConversionPropHandler(from_sqs.getQueryConvPropHandler());
      }

      SelectQueryStatement sqs = this.query_statement.toSnowflake();
      if (fromSQSISNotNull) {
         sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
         sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
         from_sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
         Set nullSet = from_sqs.getIndexPositionsForNULLString();
         nullSet.clear();
         from_sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
      }

      soc.setSelectQueryStatement(sqs);
      soc.setCloseBrace(this.CloseBrace);
      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      soc.setOpenBrace(this.OpenBrace);
      if (this.SetClause.equalsIgnoreCase("MINUS") | this.SetClause.equalsIgnoreCase("EXCEPT ALL")) {
         soc.setSetClause("EXCEPT");
      } else if (this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
         soc.setSetClause("INTERSECT");
      } else {
         soc.setSetClause(this.SetClause);
      }

      boolean fromSQSISNotNull = from_sqs != null;
      if (fromSQSISNotNull) {
         this.query_statement.addAllIndexPositionsForStringLiterals(from_sqs.getIndexPositionsForStringLiterals());
         this.query_statement.addAllIndexPositionsForNULLString(from_sqs.getIndexPositionsForNULLString());
      }

      this.query_statement.setSetOperatorQuery(true);
      if (fromSQSISNotNull) {
         this.query_statement.setAthenaFlag(from_sqs.isAthena());
         this.query_statement.setQueryConversionPropHandler(from_sqs.getQueryConvPropHandler());
      }

      SelectQueryStatement sqs = this.query_statement.toAthena();
      if (fromSQSISNotNull) {
         sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
         sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
         from_sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
         Set nullSet = from_sqs.getIndexPositionsForNULLString();
         nullSet.clear();
         from_sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
      }

      soc.setSelectQueryStatement(sqs);
      soc.setCloseBrace(this.CloseBrace);
      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      soc.setOpenBrace(this.OpenBrace);
      if (this.SetClause.equalsIgnoreCase("MINUS") | this.SetClause.equalsIgnoreCase("EXCEPT ALL")) {
         soc.setSetClause("EXCEPT");
      } else if (this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
         soc.setSetClause("INTERSECT");
      } else {
         soc.setSetClause(this.SetClause);
      }

      boolean fromSQSISNotNull = from_sqs != null;
      if (fromSQSISNotNull) {
         this.query_statement.addAllIndexPositionsForStringLiterals(from_sqs.getIndexPositionsForStringLiterals());
         this.query_statement.addAllIndexPositionsForNULLString(from_sqs.getIndexPositionsForNULLString());
      }

      this.query_statement.setSetOperatorQuery(true);
      if (fromSQSISNotNull) {
         this.query_statement.setSapHanaFlag(from_sqs.isSapHanaDb());
         this.query_statement.setQueryConversionPropHandler(from_sqs.getQueryConvPropHandler());
      }

      SelectQueryStatement sqs = this.query_statement.toSapHana();
      if (fromSQSISNotNull) {
         sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
         sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
         from_sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
         Set nullSet = from_sqs.getIndexPositionsForNULLString();
         nullSet.clear();
         from_sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
      }

      soc.setSelectQueryStatement(sqs);
      soc.setCloseBrace(this.CloseBrace);
      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      soc.setOpenBrace(this.OpenBrace);
      if (this.SetClause.equalsIgnoreCase("MINUS") | this.SetClause.equalsIgnoreCase("EXCEPT ALL")) {
         soc.setSetClause("EXCEPT");
      } else if (this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
         soc.setSetClause("INTERSECT");
      } else {
         soc.setSetClause(this.SetClause);
      }

      boolean fromSQSISNotNull = from_sqs != null;
      if (fromSQSISNotNull) {
         this.query_statement.addAllIndexPositionsForStringLiterals(from_sqs.getIndexPositionsForStringLiterals());
         this.query_statement.addAllIndexPositionsForNULLString(from_sqs.getIndexPositionsForNULLString());
      }

      this.query_statement.setSetOperatorQuery(true);
      if (fromSQSISNotNull) {
         this.query_statement.setSqliteFlag(from_sqs.isSqlite());
         this.query_statement.setQueryConversionPropHandler(from_sqs.getQueryConvPropHandler());
      }

      SelectQueryStatement sqs = this.query_statement.toSqlite();
      if (fromSQSISNotNull) {
         sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
         sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
         from_sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
         Set nullSet = from_sqs.getIndexPositionsForNULLString();
         nullSet.clear();
         from_sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
      }

      soc.setSelectQueryStatement(sqs);
      soc.setCloseBrace(this.CloseBrace);
      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      WhereExpression we = new WhereExpression();
      WhereItem wi = new WhereItem();
      SelectQueryStatement sqs = this.query_statement.toExcel();
      soc.setSelectQueryStatement(sqs);
      soc.setOpenBrace(this.OpenBrace);
      soc.setCloseBrace(this.CloseBrace);
      if (!this.SetClause.equalsIgnoreCase("UNION") && !this.SetClause.equalsIgnoreCase("UNION ALL")) {
         if (this.SetClause.equalsIgnoreCase("INTERSECT") | this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
            wi.setOperator("EXISTS");
         } else {
            wi.setOperator("NOT EXISTS");
            Vector query_statementFromTableList = sqs.getFromClause().getFromItemList();
            int qListSize = query_statementFromTableList.size();

            for(int qi = 0; qi < qListSize; ++qi) {
               FromTable fromTableObj = (FromTable)query_statementFromTableList.get(qi);
               if (fromTableObj.getAliasName() == null || fromTableObj.getAliasName().equalsIgnoreCase(" ")) {
                  fromTableObj.setAliasName("ADV_INNER_ALIAS_" + qi);
               }
            }
         }

         if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().trim().equals("")) {
            sqs.getWhereExpression().addOperator("AND");
            sqs.getWhereExpression().addWhereExpression(this.createWhereExp(to_sqs, sqs));
         } else {
            sqs.setWhereExpression(this.createWhereExp(to_sqs, sqs));
         }

         wi.setRightWhereSubQuery(sqs);
         we.addWhereItem(wi);
         if (to_sqs.getWhereExpression() != null && !to_sqs.getWhereExpression().toString().trim().equals("")) {
            to_sqs.getWhereExpression().addOperator("AND");
            to_sqs.getWhereExpression().addWhereExpression(we);
         } else {
            to_sqs.setWhereExpression(we);
         }

         if (to_sqs.getSelectStatement() != null) {
            to_sqs.getSelectStatement().setSelectQualifier("DISTINCT");
         }

         soc = null;
      } else {
         soc.setSetClause(this.SetClause);
      }

      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      WhereExpression we = new WhereExpression();
      WhereItem wi = new WhereItem();
      SelectQueryStatement sqs = this.query_statement.toMsAccessJdbc();
      soc.setSelectQueryStatement(sqs);
      soc.setOpenBrace(this.OpenBrace);
      soc.setCloseBrace(this.CloseBrace);
      if (!this.SetClause.equalsIgnoreCase("UNION") && !this.SetClause.equalsIgnoreCase("UNION ALL")) {
         if (this.SetClause.equalsIgnoreCase("INTERSECT") | this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
            wi.setOperator("EXISTS");
         } else {
            wi.setOperator("NOT EXISTS");
            Vector query_statementFromTableList = sqs.getFromClause().getFromItemList();
            int qListSize = query_statementFromTableList.size();

            for(int qi = 0; qi < qListSize; ++qi) {
               FromTable fromTableObj = (FromTable)query_statementFromTableList.get(qi);
               if (fromTableObj.getAliasName() == null || fromTableObj.getAliasName().equalsIgnoreCase(" ")) {
                  fromTableObj.setAliasName("ADV_INNER_ALIAS_" + qi);
               }
            }
         }

         if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().trim().equals("")) {
            sqs.getWhereExpression().addOperator("AND");
            sqs.getWhereExpression().addWhereExpression(this.createWhereExp(to_sqs, sqs));
         } else {
            sqs.setWhereExpression(this.createWhereExp(to_sqs, sqs));
         }

         wi.setRightWhereSubQuery(sqs);
         we.addWhereItem(wi);
         if (to_sqs.getWhereExpression() != null && !to_sqs.getWhereExpression().toString().trim().equals("")) {
            to_sqs.getWhereExpression().addOperator("AND");
            to_sqs.getWhereExpression().addWhereExpression(we);
         } else {
            to_sqs.setWhereExpression(we);
         }

         if (to_sqs.getSelectStatement() != null) {
            to_sqs.getSelectStatement().setSelectQualifier("DISTINCT");
         }

         soc = null;
      } else {
         soc.setSetClause(this.SetClause);
      }

      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      soc.setCommentClass(this.commentObj);
      soc.setOpenBrace(this.OpenBrace);
      if (this.SetClause.equalsIgnoreCase("EXCEPT") | this.SetClause.equalsIgnoreCase("EXCEPT ALL")) {
         soc.setSetClause("MINUS");
      } else if (this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
         soc.setSetClause("INTERSECT");
      } else {
         soc.setSetClause(this.SetClause);
      }

      this.query_statement.setObjectContext(this.context);
      if (from_sqs != null && this.query_statement != null) {
         this.query_statement.setCanUseOracleFetch(from_sqs.getCanUseOracleFetch());
      }

      soc.setSelectQueryStatement(this.query_statement.toOracle());
      soc.setCloseBrace(this.CloseBrace);
      soc.setObjectContext(this.context);
      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      WhereExpression we = new WhereExpression();
      WhereItem wi = new WhereItem();
      SelectQueryStatement sqs = this.query_statement.toMSSQLServer();
      soc.setSelectQueryStatement(sqs);
      soc.setOpenBrace(this.OpenBrace);
      soc.setCloseBrace(this.CloseBrace);
      if (!this.SetClause.equalsIgnoreCase("UNION") && !this.SetClause.equalsIgnoreCase("UNION ALL")) {
         if (this.SetClause.equalsIgnoreCase("INTERSECT") | this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
            wi.setOperator("EXISTS");
         } else {
            wi.setOperator("NOT EXISTS");
            Vector query_statementFromTableList = sqs.getFromClause().getFromItemList();
            int qListSize = query_statementFromTableList.size();

            for(int qi = 0; qi < qListSize; ++qi) {
               FromTable fromTableObj = (FromTable)query_statementFromTableList.get(qi);
               if (fromTableObj.getAliasName() == null || fromTableObj.getAliasName().equalsIgnoreCase(" ")) {
                  fromTableObj.setAliasName("ADV_INNER_ALIAS_" + qi);
               }
            }
         }

         if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().trim().equals("")) {
            sqs.getWhereExpression().addOperator("AND");
            sqs.getWhereExpression().addWhereExpression(this.createWhereExp(to_sqs, sqs));
         } else {
            sqs.setWhereExpression(this.createWhereExp(to_sqs, sqs));
         }

         wi.setRightWhereSubQuery(sqs);
         we.addWhereItem(wi);
         if (to_sqs.getWhereExpression() != null && !to_sqs.getWhereExpression().toString().trim().equals("")) {
            to_sqs.getWhereExpression().addOperator("AND");
            to_sqs.getWhereExpression().addWhereExpression(we);
         } else {
            to_sqs.setWhereExpression(we);
         }

         if (to_sqs.getSelectStatement() != null) {
            to_sqs.getSelectStatement().setSelectQualifier("DISTINCT");
         }

         soc = null;
      } else {
         soc.setSetClause(this.SetClause);
      }

      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      soc.setObjectContext(this.context);
      WhereExpression we = new WhereExpression();
      WhereItem wi = new WhereItem();
      SelectQueryStatement sqs = this.query_statement.toSybase();
      sqs.setObjectContext(this.context);
      we.setObjectContext(this.context);
      wi.setObjectContext(this.context);
      soc.setSelectQueryStatement(sqs);
      soc.setOpenBrace(this.OpenBrace);
      soc.setCloseBrace(this.CloseBrace);
      if (!this.SetClause.equalsIgnoreCase("UNION") && !this.SetClause.equalsIgnoreCase("UNION ALL")) {
         if (this.SetClause.equalsIgnoreCase("INTERSECT") | this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
            wi.setOperator("EXISTS");
         } else {
            wi.setOperator("NOT EXISTS");
            Vector query_statementFromTableList = sqs.getFromClause().getFromItemList();
            int qListSize = query_statementFromTableList.size();

            for(int qi = 0; qi < qListSize; ++qi) {
               FromTable fromTableObj = (FromTable)query_statementFromTableList.get(qi);
               if (fromTableObj.getAliasName() == null || fromTableObj.getAliasName().equalsIgnoreCase(" ")) {
                  fromTableObj.setAliasName("ADV_INNER_ALIAS_" + qi);
               }
            }
         }

         if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().trim().equals("")) {
            sqs.getWhereExpression().addOperator("AND");
            sqs.getWhereExpression().addWhereExpression(this.createWhereExp(to_sqs, sqs));
         } else {
            sqs.setWhereExpression(this.createWhereExp(to_sqs, sqs));
         }

         wi.setRightWhereSubQuery(sqs);
         we.addWhereItem(wi);
         if (to_sqs.getWhereExpression() != null && !to_sqs.getWhereExpression().toString().trim().equals("")) {
            to_sqs.getWhereExpression().addOperator("AND");
            to_sqs.getWhereExpression().addWhereExpression(we);
         } else {
            to_sqs.setWhereExpression(we);
         }

         if (to_sqs.getSelectStatement() != null) {
            to_sqs.getSelectStatement().setSelectQualifier("DISTINCT");
         }

         soc = null;
      } else {
         soc.setSetClause(this.SetClause);
      }

      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      SelectQueryStatement sqs = this.query_statement.toInformix();
      soc.setSelectQueryStatement(sqs);
      soc.setOpenBrace(this.OpenBrace);
      soc.setCloseBrace(this.CloseBrace);
      if (this.SetClause.equalsIgnoreCase("EXCEPT ALL")) {
         soc.setSetClause("EXCEPT");
      } else if (this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
         soc.setSetClause("INTERSECT");
      } else {
         soc.setSetClause(this.SetClause);
      }

      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nUnsupported SQL in TimesTen 5.1.21\n");
   }

   public SetOperatorClause toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      soc.setOpenBrace(this.OpenBrace);
      if (this.SetClause.equalsIgnoreCase("MINUS")) {
         soc.setSetClause("EXCEPT");
      } else {
         soc.setSetClause(this.SetClause);
      }

      soc.setSelectQueryStatement(this.query_statement.toNetezza());
      soc.setCloseBrace(this.CloseBrace);
      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      soc.setOpenBrace(this.OpenBrace);
      if (this.SetClause.equalsIgnoreCase("MINUS") | this.SetClause.equalsIgnoreCase("EXCEPT ALL")) {
         soc.setSetClause("EXCEPT");
      } else if (this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
         soc.setSetClause("INTERSECT");
      } else {
         soc.setSetClause(this.SetClause);
      }

      boolean fromSQSISNotNull = from_sqs != null;
      if (fromSQSISNotNull) {
         this.query_statement.addAllIndexPositionsForStringLiterals(from_sqs.getIndexPositionsForStringLiterals());
         this.query_statement.addAllIndexPositionsForNULLString(from_sqs.getIndexPositionsForNULLString());
      }

      this.query_statement.setSetOperatorQuery(true);
      if (fromSQSISNotNull) {
         this.query_statement.setBigQueryFlag(from_sqs.isBigQueryLive());
         this.query_statement.setQueryConversionPropHandler(from_sqs.getQueryConvPropHandler());
      }

      SelectQueryStatement sqs = this.query_statement.toBigQuery();
      if (fromSQSISNotNull) {
         sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
         sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
         from_sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
         Set nullSet = from_sqs.getIndexPositionsForNULLString();
         nullSet.clear();
         from_sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
      }

      soc.setSelectQueryStatement(sqs);
      soc.setCloseBrace(this.CloseBrace);
      this.checkSetOperator = true;
      return soc;
   }

   public WhereExpression createWhereExp(SelectQueryStatement to_sqs, SelectQueryStatement sqs) throws ConvertException {
      Vector v_sil = to_sqs.getSelectStatement().getSelectItemList();
      Vector v_scsil = sqs.getSelectStatement().getSelectItemList();
      Vector v_fil = new Vector();
      Vector v_scfil = new Vector();
      String t_name = null;
      String t_scname = null;
      if (to_sqs.getFromClause() != null) {
         v_fil = to_sqs.getFromClause().getFromItemList();
      }

      if (this.query_statement.getFromClause() != null) {
         v_scfil = sqs.getFromClause().getFromItemList();
      }

      if (v_sil.size() != v_scsil.size()) {
         throw new ConvertException("incorrect number of columns");
      } else {
         FromTable ft_i = (FromTable)v_fil.elementAt(0);
         FromTable ft_sci = (FromTable)v_scfil.elementAt(0);
         if (ft_i.getAliasName() != null) {
            t_name = ft_i.getAliasName();
         } else if (ft_i.getTableName() instanceof String) {
            t_name = (String)ft_i.getTableName();
         }

         if (ft_sci.getAliasName() != null) {
            t_scname = ft_sci.getAliasName();
         } else if (ft_sci.getTableName() instanceof String) {
            t_scname = (String)ft_sci.getTableName();
         }

         WhereExpression we = new WhereExpression();

         for(int i_count = 0; i_count < v_sil.size(); ++i_count) {
            WhereItem wi = new WhereItem();
            new Vector();
            SelectColumn sc;
            WhereColumn wc_new;
            Vector containsSelectColumn;
            if (v_sil.elementAt(i_count) instanceof SelectColumn) {
               sc = (SelectColumn)v_sil.get(i_count);
               if (sc.toString().indexOf(".") != -1) {
                  wc_new = new WhereColumn();
                  containsSelectColumn = sc.getColumnExpression();
                  wc_new.setColumnExpression(containsSelectColumn);
                  wi.setLeftWhereExp(wc_new);
               } else {
                  wc_new = this.createColumn((SelectColumn)v_sil.elementAt(i_count), t_name);
                  wi.setLeftWhereExp(wc_new);
               }
            }

            if (v_scsil.elementAt(i_count) instanceof SelectColumn) {
               sc = (SelectColumn)v_scsil.get(i_count);
               if (sc.toString().indexOf(".") != -1) {
                  wc_new = new WhereColumn();
                  containsSelectColumn = sc.getColumnExpression();
                  wc_new.setColumnExpression(containsSelectColumn);
                  wi.setRightWhereExp(wc_new);
               } else {
                  wc_new = this.createColumn((SelectColumn)v_scsil.elementAt(i_count), t_scname);
                  wi.setRightWhereExp(wc_new);
               }
            }

            wi.setOperator("=");
            if (i_count == v_sil.size() - 1) {
               we.addWhereItem(wi);
            } else {
               we.addWhereItem(wi);
               we.addOperator("AND");
            }
         }

         return we;
      }
   }

   public WhereColumn createColumn(SelectColumn sc, String tn) {
      Vector v_ce = sc.getColumnExpression();
      WhereColumn wc_new = new WhereColumn();
      Vector v_nce = new Vector();

      for(int i_count = 0; i_count < v_ce.size(); ++i_count) {
         if (v_ce.elementAt(i_count) instanceof String) {
            String st = tn + "." + (String)v_ce.elementAt(i_count);
            v_nce.addElement(st);
         } else if (v_ce.elementAt(i_count) instanceof TableColumn) {
            TableColumn tc = (TableColumn)v_ce.elementAt(i_count);
            tc.setTableName(tn);
            v_nce.addElement(tc);
         } else if (v_ce.elementAt(i_count) instanceof FunctionCalls) {
            FunctionCalls fc = (FunctionCalls)v_ce.elementAt(i_count);
            TableColumn tc = fc.getFunctionName();
            if (tc != null) {
               tc.setTableName(tn);
            }

            v_nce.addElement(fc);
         } else {
            v_nce.addElement(this.createColumn((SelectColumn)v_ce.elementAt(i_count), tn));
         }
      }

      wc_new.setColumnExpression(v_nce);
      return wc_new;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.whereExpression != null) {
         sb.append("  WHERE  " + this.whereExpression);
      }

      for(int i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
         sb.append("\t");
      }

      if (this.commentObj != null) {
         sb.append(this.commentObj.toString().trim() + " ");
      }

      sb.append(this.SetClause.toUpperCase());
      if (this.OpenBrace != null) {
         sb.append(" " + this.OpenBrace);
      }

      sb.append("\n ");
      this.query_statement.setObjectContext(this.context);
      sb.append(this.query_statement.toString());
      if (this.CloseBrace != null) {
         sb.append(this.CloseBrace);
      }

      return sb.toString();
   }

   public SetOperatorClause toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      WhereExpression we = new WhereExpression();
      WhereItem wi = new WhereItem();
      this.query_statement.setSetOperatorQuery(true);
      boolean isFromSQSNotNull = from_sqs != null;
      if (isFromSQSNotNull) {
         this.query_statement.addAllIndexPositionsForStringLiterals(from_sqs.getIndexPositionsForStringLiterals());
         this.query_statement.addAllIndexPositionsForNULLString(from_sqs.getIndexPositionsForNULLString());
         this.query_statement.setAmazonRedShiftFlag(from_sqs.isAmazonRedShift());
         this.query_statement.setPgsqlFlag(from_sqs.isPgsqlLive());
         this.query_statement.setMSAzureFlag(from_sqs.isMSAzure());
         this.query_statement.setMSAzureWareHouseFlag(from_sqs.isMSAzureWareHouse());
         this.query_statement.setOracleLiveFlag(from_sqs.isOracleLive());
         this.query_statement.setDenodoFlag(from_sqs.isDenodo());
         this.query_statement.setQueryConversionPropHandler(from_sqs.getQueryConvPropHandler());
      }

      SelectQueryStatement sqs = this.query_statement.toVectorWise();
      if (isFromSQSNotNull) {
         sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
         sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
         Set nullSet = from_sqs.getIndexPositionsForNULLString();
         nullSet.clear();
         from_sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
         from_sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
      }

      soc.setSelectQueryStatement(sqs);
      soc.setOpenBrace(this.OpenBrace);
      soc.setCloseBrace(this.CloseBrace);
      if (!this.SetClause.equalsIgnoreCase("UNION") && !this.SetClause.equalsIgnoreCase("UNION ALL")) {
         if (this.SetClause.equalsIgnoreCase("INTERSECT") | this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
            wi.setOperator("EXISTS");
         } else {
            wi.setOperator("NOT EXISTS");
            Vector query_statementFromTableList = sqs.getFromClause().getFromItemList();
            int qListSize = query_statementFromTableList.size();

            for(int qi = 0; qi < qListSize; ++qi) {
               FromTable fromTableObj = (FromTable)query_statementFromTableList.get(qi);
               if (fromTableObj.getAliasName() == null || fromTableObj.getAliasName().equalsIgnoreCase(" ")) {
                  fromTableObj.setAliasName("ADV_INNER_ALIAS_" + qi);
               }
            }
         }

         if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().trim().equals("")) {
            sqs.getWhereExpression().addOperator("AND");
            sqs.getWhereExpression().addWhereExpression(this.createWhereExp(to_sqs, sqs));
         } else {
            sqs.setWhereExpression(this.createWhereExp(to_sqs, sqs));
         }

         wi.setRightWhereSubQuery(sqs);
         we.addWhereItem(wi);
         if (to_sqs.getWhereExpression() != null && !to_sqs.getWhereExpression().toString().trim().equals("")) {
            to_sqs.getWhereExpression().addOperator("AND");
            to_sqs.getWhereExpression().addWhereExpression(we);
         } else {
            to_sqs.setWhereExpression(we);
         }

         if (to_sqs.getSelectStatement() != null) {
            to_sqs.getSelectStatement().setSelectQualifier("DISTINCT");
         }

         soc = null;
      } else {
         soc.setSetClause(this.SetClause);
      }

      this.checkSetOperator = true;
      return soc;
   }

   public SetOperatorClause toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SetOperatorClause setOpClConv = new SetOperatorClause();
      if (this.OpenBrace != null) {
         setOpClConv.setOpenBrace(this.OpenBrace);
      }

      if (this.CloseBrace != null) {
         setOpClConv.setCloseBrace(this.CloseBrace);
      }

      if (this.commentObj != null) {
         setOpClConv.setCommentClass(this.commentObj);
      }

      if (this.SetClause != null) {
         setOpClConv.setSetClause(this.SetClause);
      }

      if (this.query_statement != null) {
         this.query_statement.setPropAndHandlerFromSQS(from_sqs);
         this.query_statement.setSetOperatorQuery(true);
         if (this.getSelectQueryStatement().getCTEViewDetsMap() != null) {
            this.query_statement.setCTEViewDetsMap(from_sqs.getCTEViewDetsMap());
         }

         SelectQueryStatement setOpQueryConv = this.query_statement.toReplaceTblCol();
         HashMap<String, String> selColmap = new HashMap();
         selColmap.putAll(setOpQueryConv.getSelColNameMap());
         selColmap.putAll(to_sqs.getSelColNameMap());
         to_sqs.setSelColNameMap(selColmap);
         setOpClConv.setSelectQueryStatement(setOpQueryConv);
      }

      return setOpClConv;
   }
}
