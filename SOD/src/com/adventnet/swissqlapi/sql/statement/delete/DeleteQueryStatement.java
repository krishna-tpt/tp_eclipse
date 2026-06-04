package com.adventnet.swissqlapi.sql.statement.delete;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.parser.ParseException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.RownumClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.update.HintClause;
import com.adventnet.swissqlapi.sql.statement.update.OptionalHintClause;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;
import com.adventnet.swissqlapi.sql.statement.update.OracleSpecificClass;
import com.adventnet.swissqlapi.sql.statement.update.ReturningClause;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.update.WhereCurrentClause;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.util.Vector;

public class DeleteQueryStatement implements SwisSQLStatement {
   private UserObjectContext objectContext = null;
   private DeleteClause deleteClause = null;
   private FromClause fromClause = null;
   private WhereExpression whereExpression = null;
   private TableExpression tableExp = null;
   private DeleteLimitClause deleteLimitClause = null;
   private ReturningClause returningClause;
   private HintClause hintClause;
   private OptionalHintClause optionalHintClause;
   private WhereCurrentClause whereCurrentClause;
   private OracleSpecificClass OracleSpecificInstance;
   private CommentClass commentObject;
   private OrderByStatement orderByStatement;
   private String isolationLevel = null;
   private String withString = null;
   private ArrayList lockTableList = new ArrayList();
   private ArrayList usingTableList;

   public void setCommentClass(CommentClass commentObject) {
      this.commentObject = commentObject;
   }

   public CommentClass getCommentClass() {
      return this.commentObject;
   }

   public void setDeleteClause(DeleteClause dc) {
      this.deleteClause = dc;
   }

   public void setFromClause(FromClause fromClause) {
      this.fromClause = fromClause;
   }

   public FromClause getFromClause() {
      return this.fromClause;
   }

   public void setWhereClause(WhereExpression we) {
      this.whereExpression = we;
      we.setFromDeleteQueryStatement(true);
   }

   public void setWhereCurrentClause(WhereCurrentClause whereCurrentClause) {
      this.whereCurrentClause = whereCurrentClause;
   }

   public void setOrderByStatement(OrderByStatement obs) {
      this.orderByStatement = obs;
   }

   public void setUsingTableList(ArrayList usingTableList) {
      this.usingTableList = usingTableList;
   }

   public ArrayList getUsingTableList() {
      return this.usingTableList;
   }

   public OrderByStatement getOrderByStatement() {
      return this.orderByStatement;
   }

   public WhereExpression getWhereExpression() {
      return this.whereExpression;
   }

   public void setTableExpression(TableExpression tableexpression) {
      this.tableExp = tableexpression;
   }

   public TableExpression getTableExpression() {
      return this.tableExp;
   }

   public void setDeleteLimitClause(DeleteLimitClause l) {
      this.deleteLimitClause = l;
   }

   public DeleteLimitClause getLimitClause() {
      return this.deleteLimitClause;
   }

   public void setHintClause(HintClause hc) {
      this.hintClause = hc;
   }

   public HintClause getHintClause() {
      return this.hintClause;
   }

   public void setOracleSpecificInstance(OracleSpecificClass oraclespecificclass) {
      this.OracleSpecificInstance = oraclespecificclass;
   }

   public void setOptionalHintClause(OptionalHintClause ohc) {
      this.optionalHintClause = ohc;
   }

   public OptionalHintClause getOptionalHintClause() {
      return this.optionalHintClause;
   }

   public void setReturningClause(ReturningClause rc) {
      this.returningClause = rc;
   }

   public void setWithString(String w) {
      this.withString = w;
   }

   public String getWithString() {
      return this.withString;
   }

   public void setIsolationLevel(String il) {
      this.isolationLevel = il;
   }

   public String getIsolationLevel() {
      return this.isolationLevel;
   }

   public void addLockTableList(String lockTableStt) {
      this.lockTableList.add(lockTableStt);
   }

   public ReturningClause getReturningClause() {
      return this.returningClause;
   }

   public WhereCurrentClause getWhereCurrentClause() {
      return this.whereCurrentClause;
   }

   public DeleteClause getDeleteClause() {
      return this.deleteClause;
   }

   public String toString() {
      StringBuffer stringbuffer = new StringBuffer();
      if (this.commentObject != null) {
         stringbuffer.append(this.commentObject.toString() + "\n");
      }

      for(int k = 0; k < this.lockTableList.size(); ++k) {
         stringbuffer.append(this.lockTableList.get(k).toString() + ";\n");
      }

      if (this.singleQueryIntoMultipleQueriesForPLSQL() != null) {
         stringbuffer.append(this.singleQueryIntoMultipleQueriesForPLSQL());
         SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
      }

      stringbuffer.append(this.deleteClause.toString() + " ");
      if (this.tableExp != null) {
         if (this.objectContext != null) {
            this.tableExp.setObjectContext(this.objectContext);
         }

         stringbuffer.append(this.tableExp.toString() + " \n");
      }

      if (this.hintClause != null && this.hintClause.toString() != null && !this.hintClause.toString().trim().equals("")) {
         stringbuffer.append(this.hintClause.toString() + " \n");
      }

      if (this.fromClause != null) {
         if (this.objectContext != null) {
            this.fromClause.setObjectContext(this.objectContext);
         }

         stringbuffer.append(this.fromClause.toString() + " \n");
      }

      if (this.whereCurrentClause != null) {
         stringbuffer.append(this.whereCurrentClause.toString() + " \n");
      }

      if (this.whereExpression != null) {
         if (!this.whereExpression.toString().trim().equals("")) {
            stringbuffer.append("WHERE ");
            this.whereExpression.setObjectContext(this.objectContext);
            if (this.whereExpression.toString().indexOf("AND") == 0) {
               stringbuffer.append(StringFunctions.replaceFirst(" ", "AND", this.whereExpression.toString()));
            } else {
               stringbuffer.append(" " + this.whereExpression.toString());
            }
         }

         stringbuffer.append(" \n");
      }

      if (this.orderByStatement != null) {
         stringbuffer.append(this.orderByStatement.toString() + "\n");
      }

      if (this.deleteLimitClause != null) {
         stringbuffer.append(this.deleteLimitClause.toString() + " \n");
      }

      if (this.optionalHintClause != null) {
         stringbuffer.append(this.optionalHintClause.toString() + " \n");
      }

      if (this.returningClause != null) {
         stringbuffer.append(this.returningClause.toString() + "\n");
      }

      if (this.withString != null) {
         stringbuffer.append(this.withString + " ");
      }

      if (this.isolationLevel != null) {
         stringbuffer.append(this.isolationLevel);
      }

      return stringbuffer.toString();
   }

   public String removeIndent(String s_ri) {
      s_ri = s_ri.replace('\n', ' ');
      s_ri = s_ri.replace('\t', ' ');
      return s_ri;
   }

   public String toInformixString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.deleteClause.toInformix();
      this.tableExp.toInformix();
      if (this.hintClause != null) {
         this.hintClause.toSQLServer();
      }

      this.convertAliasNameToTableName();
      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.returningClause = null;
      this.deleteLimitClause = null;
      this.orderByStatement = null;
      return this.toString();
   }

   public String toOracleString() throws ConvertException {
      new StringBuffer();
      this.deleteClause.toOracle();
      this.tableExp.toOracle();
      if (this.hintClause != null) {
         this.hintClause.toOracle();
      }

      if (this.whereExpression != null) {
         this.setDQSForWhereColumn(this.whereExpression);
         this.whereExpression = this.whereExpression.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      if (this.fromClause != null) {
         try {
            this.fromClause.convertToSubQuery((DeleteQueryStatement)this, 1, this.fromClause);
         } catch (ParseException var8) {
            System.err.println("Exception in SQLQuery : " + this.toString());
            var8.printStackTrace();
            throw new ConvertException("conversion failure ");
         }
      }

      this.optionalHintClause = null;
      if (this.deleteLimitClause != null) {
         this.deleteLimitClause.toOracleRowNum(this);
      }

      this.deleteLimitClause = null;
      this.orderByStatement = null;
      if (this.withString != null && this.isolationLevel != null) {
         String lockStatement = "LOCK TABLE ";
         ArrayList tableList = this.getTableExpression().getTableClauseList();

         for(int i = 0; i < tableList.size(); ++i) {
            Object o = tableList.get(i);
            if (o instanceof TableClause) {
               TableClause tc = (TableClause)o;
               String s = tc.getTableObject().getTableName();
               lockStatement = lockStatement + s + " IN ";
               if (this.isolationLevel.trim().equalsIgnoreCase("RR") || this.isolationLevel.trim().equalsIgnoreCase("RS")) {
                  lockStatement = lockStatement + "EXCLUSIVE MODE";
                  this.addLockTableList(lockStatement);
               }
            }
         }
      }

      this.withString = null;
      this.isolationLevel = null;
      return this.toString();
   }

   public String toMSSQLServerString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.deleteClause.toSQLServer();
      this.tableExp.toMSSQLServer();
      if (this.hintClause != null) {
         this.hintClause.toSQLServer();
      }

      this.convertAliasNameToTableName();
      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.returningClause = null;
      this.deleteLimitClause = null;
      this.orderByStatement = null;
      ArrayList tableList = this.tableExp.getTableClauseList();
      if (tableList != null) {
         for(int i = 0; i < tableList.size(); ++i) {
            if (tableList.get(i) instanceof TableClause) {
               ((TableClause)tableList.get(i)).setAlias("");
            }
         }
      }

      return this.toString();
   }

   public String toSybaseString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.deleteClause.toSybase();
      this.tableExp.toSybase();
      if (this.hintClause != null) {
         this.hintClause.toSQLServer();
      }

      if (this.whereExpression != null) {
         this.whereExpression.setObjectContext(this.objectContext);
         this.whereExpression = this.whereExpression.toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.returningClause = null;
      this.deleteLimitClause = null;
      this.orderByStatement = null;
      return this.toString();
   }

   public String toDB2String() throws ConvertException {
      this.deleteClause.toDB2();
      this.tableExp.toDB2();
      if (this.hintClause != null) {
         this.hintClause = null;
      }

      if (this.whereExpression != null) {
         ArrayList tableNamesList = new ArrayList();
         if (this.tableExp != null) {
            ArrayList tableClauseList = this.tableExp.getTableClauseList();

            for(int i = 0; i < tableClauseList.size(); ++i) {
               if (tableClauseList.get(i) != null && tableClauseList.get(i) instanceof TableClause) {
                  TableObject tableObject = ((TableClause)tableClauseList.get(i)).getTableObject();
                  tableNamesList.add(tableObject.getTableName());
               }
            }

            this.whereExpression.setFromTableList(tableNamesList);
         }

         WhereExpression we = new WhereExpression();
         WhereItem wi = new WhereItem();
         WhereColumn wc1 = new WhereColumn();
         WhereColumn wc2 = new WhereColumn();
         Vector whereColVector1 = new Vector();
         Vector whereColVector2 = new Vector();
         Vector whereItemVector = new Vector();
         RownumClause rc = this.whereExpression.getRownumClause();
         if (rc != null) {
            whereColVector1.add("ROWNUM");
            wc1.setColumnExpression(whereColVector1);
            wi.setLeftWhereExp(wc1);
            wi.setOperator(rc.getOperator());
            whereColVector2.add(rc.getRownumValue());
            wc2.setColumnExpression(whereColVector2);
            wi.setRightWhereExp(wc2);
            whereItemVector.add(wi);
            we.setWhereItem(whereItemVector);
         }

         this.whereExpression = this.whereExpression.toDB2Select((SelectQueryStatement)null, (SelectQueryStatement)null);
         if (rc != null) {
            SelectQueryStatement sqs = new SelectQueryStatement();
            SelectStatement ss = new SelectStatement();
            Vector selectListVector = new Vector();
            SelectColumn sc1 = new SelectColumn();
            Vector selectColumnVector1 = new Vector();
            SelectColumn sc2 = new SelectColumn();
            Vector selectColumnVector2 = new Vector();
            FromTable ft = new FromTable();
            FromClause fc = new FromClause();
            Vector fromClauseVector = new Vector();
            TableExpression tabExpr = new TableExpression();
            selectColumnVector1.add("ROW_NUMBER() OVER()");
            sc1.setColumnExpression(selectColumnVector1);
            sc1.setIsAS("AS");
            sc1.setAliasName("ROWNUM");
            sc1.setEndsWith(",");
            selectListVector.add(sc1);
            if (tableNamesList.size() > 0) {
               selectColumnVector2.add(tableNamesList.get(0) + ".*");
               sc2.setColumnExpression(selectColumnVector2);
               selectListVector.add(sc2);
               ft.setTableName(tableNamesList.get(0));
               fromClauseVector.add(ft);
               fc.setFromClause("FROM");
               fc.setFromItemList(fromClauseVector);
               sqs.setFromClause(fc);
            }

            ss.setSelectItemList(selectListVector);
            ss.setSelectClause("SELECT");
            sqs.setSelectStatement(ss);
            sqs.setWhereExpression(this.whereExpression);
            tabExpr.setSubQuery(sqs);
            this.setTableExpression(tabExpr);
            this.setWhereClause(we);
         }
      }

      if (this.fromClause != null) {
         try {
            this.fromClause.convertToSubQuery((DeleteQueryStatement)this, 3, this.fromClause);
         } catch (ParseException var21) {
            throw new ConvertException("conversion failure");
         }
      }

      this.optionalHintClause = null;
      this.returningClause = null;
      this.deleteLimitClause = null;
      this.orderByStatement = null;
      return this.toString();
   }

   private boolean isNumber(String str) {
      try {
         int i = Integer.parseInt(str);
         return true;
      } catch (NumberFormatException var3) {
         System.out.println("Limit Dimension is not an Integer  : " + var3.getMessage());
         return false;
      }
   }

   public String toMySQLString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      DeleteClause dd = this.deleteClause;
      this.deleteClause.toMySQL();
      if (this.tableExp != null && this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().get(0) instanceof TableClause) {
         TableClause tc = (TableClause)this.tableExp.getTableClauseList().get(0);
         TableObject tb = tc.getTableObject();
         if (tb.getTableName().equalsIgnoreCase("TOP") && tc.getAlias() != null && this.isNumber(tc.getAlias())) {
            DeleteLimitClause dLimitClause = new DeleteLimitClause();
            dLimitClause.setLimit("LIMIT");
            dLimitClause.setDimension(tc.getAlias());
            this.setDeleteLimitClause(dLimitClause);
            this.tableExp.getTableClauseList().set(0, " ");
         }
      }

      if (this.tableExp != null) {
         if (this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().size() == 1) {
            this.convertAliasNameToTableName();
         }

         this.tableExp.toMySQL();
         ArrayList tables = this.tableExp.getTableClauseList();
         if (tables != null && tables.size() > 1 && this.deleteClause.getOptionalSpecifier() != null && this.deleteClause.getOptionalSpecifier().getFrom() == null) {
            this.deleteClause.setOptionalSpecifier((OptionalSpecifier)null);
         }
      }

      if (this.fromClause != null) {
         if (this.deleteClause.getOptionalSpecifier() != null && this.deleteClause.getOptionalSpecifier().getFrom() != null) {
            this.deleteClause.getOptionalSpecifier().setFrom((String)null);
         }

         this.fromClause.toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.convertRowNumClauseToDeleteLimitClause();
      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.optionalHintClause = null;
      this.returningClause = null;
      return this.toString();
   }

   public String toPostgreSQLString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.deleteClause.toPostgreSQL();
      this.convertAliasNameToTableName();
      this.tableExp.toPostgreSQL();
      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.optionalHintClause = null;
      this.returningClause = null;
      this.deleteLimitClause = null;
      this.orderByStatement = null;
      return this.toString();
   }

   public String toBigQueryString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.deleteClause.toBigQuery();
      this.convertAliasNameToTableName();
      this.tableExp.toBigQuery();
      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.optionalHintClause = null;
      this.returningClause = null;
      this.deleteLimitClause = null;
      this.orderByStatement = null;
      return this.toString();
   }

   private void setDQSForWhereColumn(WhereExpression we) {
      Vector wis = we.getWhereItems();
      if (wis != null) {
         for(int i = 0; i < wis.size(); ++i) {
            Object obj = wis.get(i);
            if (obj instanceof WhereItem) {
               WhereItem wi = (WhereItem)obj;
               WhereColumn lwc = wi.getLeftWhereExp();
               if (lwc != null) {
                  lwc.setFromDQS(this);
               }

               WhereColumn rwc = wi.getRightWhereExp();
               if (rwc != null) {
                  rwc.setFromDQS(this);
               }
            }
         }
      }

   }

   private String singleQueryIntoMultipleQueriesForPLSQL() {
      return SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
   }

   private void convertRowNumClauseToDeleteLimitClause() throws ConvertException {
      RownumClause rownumClause = null;
      if (this.whereExpression != null) {
         rownumClause = this.whereExpression.getRownumClause();
      }

      if (rownumClause != null) {
         DeleteLimitClause dlimitClause = new DeleteLimitClause();
         String rownumValue = "0";
         if (rownumClause.getRownumValue() instanceof SelectQueryStatement) {
            throw new ConvertException("Conversion failure.. Subquery can't be converted");
         }

         if (rownumClause.getRownumValue() instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)rownumClause.getRownumValue();
            Vector colExp = sc.getColumnExpression();

            for(int i = 0; i < colExp.size(); ++i) {
               if (colExp.elementAt(i) instanceof FunctionCalls) {
                  throw new ConvertException("Conversion failure.. Function calls can't be converted");
               }

               if (colExp.elementAt(i) instanceof TableColumn) {
                  throw new ConvertException("Conversion failure.. Identifier can't be converted");
               }

               if (!(colExp.elementAt(i) instanceof String) || colExp.size() != 1) {
                  throw new ConvertException("Conversion failure.. Expression can't be converted");
               }

               rownumValue = (String)colExp.elementAt(i);
            }
         }

         dlimitClause.setLimit("LIMIT");
         if (rownumClause.getOperator().equals("<=")) {
            dlimitClause.setDimension(rownumValue);
         } else {
            dlimitClause.setDimension(Integer.parseInt(rownumValue) - 1 + "");
         }

         if (this.getLimitClause() != null) {
            throw new ConvertException();
         }

         this.setDeleteLimitClause(dlimitClause);
         this.whereExpression.setRownumClause((RownumClause)null);
      }

   }

   public String toANSIString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.deleteClause.toANSISQL();
      this.tableExp.toANSISQL();
      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      if (this.fromClause != null) {
         try {
            this.fromClause.convertToSubQuery((DeleteQueryStatement)this, 8, this.fromClause);
         } catch (ParseException var2) {
            throw new ConvertException("conversion failure");
         }
      }

      this.optionalHintClause = null;
      this.returningClause = null;
      this.deleteLimitClause = null;
      this.orderByStatement = null;
      return this.toString();
   }

   public String toTeradataString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      if (this.commentObject != null) {
         this.commentObject.setSQLDialect(12);
      }

      this.deleteClause.toTeradata();
      this.tableExp.toTeradata();
      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      if (this.fromClause != null) {
         try {
            this.fromClause.convertToSubQuery((DeleteQueryStatement)this, 12, this.fromClause);
         } catch (ParseException var2) {
            throw new ConvertException("conversion failure");
         }
      }

      this.optionalHintClause = null;
      this.returningClause = null;
      this.deleteLimitClause = null;
      this.orderByStatement = null;
      return this.toString();
   }

   public String toTimesTenString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      new StringBuffer();
      this.deleteClause.toTimesTen();
      this.tableExp.toTimesTen();
      if (this.whereExpression != null) {
         this.setDQSForWhereColumn(this.whereExpression);
         this.whereExpression = this.whereExpression.toTimesTenSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      if (this.fromClause != null) {
         WhereItem wi = new WhereItem();
         wi.setOperator("EXISTS");
         SelectQueryStatement sqs = new SelectQueryStatement();
         SelectStatement ss = new SelectStatement();
         ss.setSelectClause("SELECT");
         Vector sItems = new Vector();
         SelectColumn sc = new SelectColumn();
         Vector colExpr = new Vector();
         colExpr.add("1");
         sc.setColumnExpression(colExpr);
         sItems.add(sc);
         ss.setSelectItemList(sItems);
         sqs.setSelectStatement(ss);
         sqs.setFromClause(this.getClonedFromClause(this.fromClause).toTimesTenSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         sqs.setWhereExpression(this.getClonedWhereExpression(this.whereExpression).toTimesTenSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         wi.setRightWhereSubQuery(sqs);
         Vector wItems = new Vector();
         wItems.add(wi);
         this.whereExpression = new WhereExpression();
         this.whereExpression.setWhereItem(wItems);
         this.fromClause = null;
      }

      this.optionalHintClause = null;
      this.deleteLimitClause = null;
      this.orderByStatement = null;
      this.returningClause = null;
      return this.toString();
   }

   public String toNetezzaString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.deleteClause.toNetezza();
      SelectQueryStatement tempSubQuery = this.tableExp.getSubQuery();
      if (tempSubQuery != null && tempSubQuery.getWhereExpression() != null) {
         if (this.whereExpression != null) {
            this.whereExpression.addWhereExpression(tempSubQuery.getWhereExpression());
            this.whereExpression.addOperator("AND");
         } else {
            this.whereExpression = tempSubQuery.getWhereExpression();
         }
      }

      this.tableExp.toNetezza();
      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      if (this.fromClause != null) {
         try {
            this.fromClause.convertToSubQuery((DeleteQueryStatement)this, 11, this.fromClause);
         } catch (ParseException var3) {
            throw new ConvertException("conversion failure");
         }
      }

      this.optionalHintClause = null;
      this.returningClause = null;
      this.deleteLimitClause = null;
      this.orderByStatement = null;
      return this.toString();
   }

   public String toSnowflakeString() throws ConvertException {
      this.withString = null;
      this.isolationLevel = null;
      this.deleteClause.toSnowflake();
      this.convertAliasNameToTableName();
      this.tableExp.toSnowflake();
      if (this.whereExpression != null) {
         this.whereExpression = this.whereExpression.toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      }

      this.optionalHintClause = null;
      this.returningClause = null;
      this.deleteLimitClause = null;
      this.orderByStatement = null;
      return this.toString();
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

   private WhereExpression getClonedWhereExpression(WhereExpression whereExpression) {
      WhereExpression clonedWhereExpression = new WhereExpression();
      Vector whereItemList = new Vector();
      new Vector();
      clonedWhereExpression.setOperator((Vector)whereExpression.getOperator().clone());
      Vector whereItems = whereExpression.getWhereItems();

      for(int i = 0; i < whereItems.size(); ++i) {
         if (whereItems.elementAt(i) instanceof WhereItem) {
            WhereItem whereItem = (WhereItem)((WhereItem)whereItems.elementAt(i)).clone();
            whereItemList.addElement(whereItem);
         } else if (whereItems.elementAt(i) instanceof WhereExpression) {
            whereItemList.addElement(this.getClonedWhereExpression((WhereExpression)whereItems.elementAt(i)));
         }
      }

      clonedWhereExpression.setWhereItem(whereItemList);
      return clonedWhereExpression;
   }

   public FromClause getClonedFromClause(FromClause fc) {
      FromClause clonedFC = new FromClause();
      clonedFC.setFromClause("FROM");
      Vector clonedFromItems = new Vector();
      Vector fromItems = fc.getFromItemList();

      for(int i = 0; i < fromItems.size(); ++i) {
         FromTable ft = (FromTable)((FromTable)fromItems.get(i)).clone();
         clonedFromItems.add(ft);
      }

      clonedFC.setFromItemList(clonedFromItems);
      return clonedFC;
   }

   public void convertAliasNameToTableName() throws ConvertException {
      ArrayList al_tcl = this.tableExp.getTableClauseList();
      if (al_tcl.get(0) instanceof TableClause) {
         TableClause tc = (TableClause)al_tcl.get(0);
         String s_an = tc.getAlias();
         String s_tn = tc.getTableObject().toString();
         if (this.whereExpression != null) {
            this.changeWhereColumn(this.whereExpression, s_an, s_tn);
         }
      }

   }

   public void changeWhereColumn(WhereExpression we, String s_an, String s_tn) {
      Vector v_wi = we.getWhereItems();

      for(int i_count = 0; i_count < v_wi.size(); ++i_count) {
         if (!(v_wi.elementAt(i_count) instanceof WhereItem)) {
            if (v_wi.elementAt(i_count) instanceof WhereExpression) {
               this.changeWhereColumn((WhereExpression)v_wi.elementAt(i_count), s_an, s_tn);
            }
         } else {
            WhereItem wi = (WhereItem)v_wi.elementAt(i_count);
            WhereColumn lwc = wi.getLeftWhereExp();
            WhereColumn rwc = wi.getRightWhereExp();
            SelectQueryStatement sqs = wi.getRightWhereSubQuery();
            Vector v_lce;
            int i_icount;
            TableColumn tc;
            String s_tablename;
            if (lwc != null) {
               v_lce = lwc.getColumnExpression();
               if (v_lce != null) {
                  for(i_icount = 0; i_icount < v_lce.size(); ++i_icount) {
                     if (v_lce.elementAt(i_icount) instanceof TableColumn) {
                        tc = (TableColumn)v_lce.elementAt(i_icount);
                        s_tablename = tc.getTableName();
                        if (s_tablename != null && s_tablename.equalsIgnoreCase(s_an)) {
                           tc.setTableName(s_tn);
                        }
                     } else if (v_lce.elementAt(i_icount) instanceof FunctionCalls) {
                        this.changeFunctionCallArgs((FunctionCalls)v_lce.elementAt(i_icount), s_an, s_tn);
                     }
                  }
               }
            }

            if (rwc != null) {
               v_lce = rwc.getColumnExpression();
               if (v_lce != null) {
                  for(i_icount = 0; i_icount < v_lce.size(); ++i_icount) {
                     if (v_lce.elementAt(i_icount) instanceof TableColumn) {
                        tc = (TableColumn)v_lce.elementAt(i_icount);
                        s_tablename = tc.getTableName();
                        if (s_tablename != null && s_tablename.equalsIgnoreCase(s_an)) {
                           tc.setTableName(s_tn);
                        }
                     } else if (v_lce.elementAt(i_icount) instanceof FunctionCalls) {
                        this.changeFunctionCallArgs((FunctionCalls)v_lce.elementAt(i_icount), s_an, s_tn);
                     }
                  }
               }
            }

            if (sqs != null) {
               FromClause fc = sqs.getFromClause();
               if (fc != null) {
                  Vector fromItems = fc.getFromItemList();
                  if (fromItems != null) {
                     int j;
                     for(j = 0; j < fromItems.size(); ++j) {
                        Object obj = fromItems.get(j);
                        if (obj instanceof FromTable) {
                           FromTable ft = (FromTable)obj;
                           String alias = ft.getAliasName();
                           Object fromTable = ft.getTableName();
                           if (alias != null && alias.equalsIgnoreCase(s_an) || fromTable instanceof String && ((String)fromTable).equalsIgnoreCase(s_an)) {
                              break;
                           }
                        }
                     }

                     if (j != fromItems.size()) {
                        continue;
                     }
                  }
               }

               WhereExpression subWE = sqs.getWhereExpression();
               if (subWE != null) {
                  this.changeWhereColumn(subWE, s_an, s_tn);
               }
            }
         }
      }

   }

   private void changeFunctionCallArgs(FunctionCalls fnObj, String s_an, String s_tn) {
      Vector fnArgs = fnObj.getFunctionArguments();
      if (fnArgs != null) {
         for(int k = 0; k < fnArgs.size(); ++k) {
            if (fnArgs.get(k) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)fnArgs.get(k);
               Vector colExpr = sc.getColumnExpression();
               if (colExpr != null) {
                  for(int n = 0; n < colExpr.size(); ++n) {
                     Object tcObj = colExpr.get(n);
                     if (tcObj instanceof TableColumn) {
                        TableColumn tc = (TableColumn)tcObj;
                        String tablename = tc.getTableName();
                        if (tablename != null && tablename.equalsIgnoreCase(s_an)) {
                           tc.setTableName(s_tn);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public UserObjectContext getObjectContext() {
      return this.objectContext;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.objectContext = obj;
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
