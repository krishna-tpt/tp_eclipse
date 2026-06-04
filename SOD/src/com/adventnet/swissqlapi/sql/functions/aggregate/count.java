package com.adventnet.swissqlapi.sql.functions.aggregate;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.GroupByStatement;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.QueryPartitionClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.ArrayList;
import java.util.Vector;

public class count extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
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
      this.functionName.setColumnName("COUNT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            selColumn.convertWhereExpAloneInsideFunctionTo_IF_Function(this.functionArguments.size());
            arguments.addElement(selColumn.toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (from_sqs != null && !from_sqs.getBooleanValues("allow.logical.exp.in.agg.function")) {
         this.validateAggFunArgsType(arguments, "COUNT");
      }

      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
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

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTimesTenSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
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

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      String argQualifier = this.getArgumentQualifier();
      if (argQualifier != null && argQualifier.equalsIgnoreCase("distinct") && this.getOver() != null && this.getOver().equalsIgnoreCase("over")) {
         from_sqs.setOlapFunctionPresent(true);
         String alias = from_sqs.getFromClause().getLastElement().getAliasName();
         String idx = "" + from_sqs.getOlapDerivedTables().size();
         if (this.getPartitionByClause() != null) {
            String countString = "count" + this.getPartitionByClause().toString() + this.obs;
            if (from_sqs.getOlapDerivedTables().containsKey(countString)) {
               FromTable var10000 = (FromTable)from_sqs.getOlapDerivedTables().get(countString);
               idx = "" + (from_sqs.getOlapDerivedTables().size() - 1);
            } else {
               from_sqs.addOlapDerivedTables(countString, this.createTeradataDerivedTable(to_sqs, this, (SelectColumn)null, alias + idx));
            }
         } else {
            from_sqs.addOlapDerivedTables("countpartition" + idx + this.obs, this.createTeradataDerivedTable(to_sqs, this, (SelectColumn)null, alias + idx));
         }

         TableColumn newTabCol = new TableColumn();
         newTabCol.setTableName(alias + idx);
         newTabCol.setColumnName("cnt_0");
         this.setFunctionName(newTabCol);
         this.getFunctionArguments().clear();
         this.setArgumentQualifier("");
         this.setOpenBracesForFunctionNameRequired(false);
         this.setPartitionByClause((QueryPartitionClause)null);
         this.setOver((String)null);
         this.setOrderBy((OrderByStatement)null);
      }

   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public FromTable createTeradataDerivedTable(SelectQueryStatement to_sqs, FunctionCalls fnCall, SelectColumn functionArgument, String alias) throws ConvertException {
      String fnName = fnCall.getFunctionName().getColumnName();
      SelectQueryStatement derivedTable = new SelectQueryStatement();
      SelectStatement selStmt = new SelectStatement();
      selStmt.setSelectClause("SELECT");
      Vector newSelColList = new Vector();
      Vector newWhereItemList = new Vector();
      SelectColumn rownumCol = this.getNewSelectColumnForDerivedTable(fnCall, 0);
      newSelColList.add(rownumCol);
      WhereItem wi = new WhereItem();
      WhereColumn lwc = new WhereColumn();
      Vector lwcColExp = new Vector();
      if (fnCall.getPartitionByClause() != null) {
         TableColumn tc1 = new TableColumn();
         tc1.setColumnName(rownumCol.getAliasName());
         lwcColExp.add(tc1);
      } else {
         lwcColExp.add("1");
      }

      WhereColumn rwc = new WhereColumn();
      Vector rwcColExp = new Vector();
      rwcColExp.add("1");
      lwc.setColumnExpression(lwcColExp);
      rwc.setColumnExpression(rwcColExp);
      wi.setLeftWhereExp(lwc);
      wi.setRightWhereExp(rwc);
      wi.setOperator("=");
      if (functionArgument != null) {
         newSelColList.add(functionArgument);
      }

      SelectColumn gbsSC;
      if (fnCall.getPartitionByClause() != null) {
         ArrayList selColsList = fnCall.getPartitionByClause().getSelectColumnList();

         for(int k = 0; k < selColsList.size(); ++k) {
            if (selColsList.get(k) instanceof SelectColumn) {
               SelectColumn partSelCol = (SelectColumn)selColsList.get(k);
               gbsSC = new SelectColumn();
               gbsSC.setColumnExpression(partSelCol.getColumnExpression());
               gbsSC.setAliasName("partition_" + k);
               gbsSC.setEndsWith(",");
               newWhereItemList.add(this.generateWhereItems(partSelCol, alias, "partition_" + k));
               newSelColList.add(gbsSC);
            }
         }
      } else {
         newWhereItemList.add(wi);
      }

      ((SelectColumn)newSelColList.lastElement()).setEndsWith((String)null);
      selStmt.setSelectItemList(newSelColList);
      derivedTable.setSelectStatement(selStmt);
      Vector joinCondition;
      if (fnCall.getPartitionByClause() != null) {
         GroupByStatement gbs = new GroupByStatement();
         gbs.setGroupClause("GROUP BY");
         joinCondition = new Vector();

         for(int k = 1; k < newSelColList.size(); ++k) {
            gbsSC = new SelectColumn();
            Vector gbsSCExp = new Vector();
            gbsSCExp.add("" + (k + 1));
            gbsSC.setColumnExpression(gbsSCExp);
            joinCondition.add(gbsSC);
         }

         gbs.setGroupByItemList(joinCondition);
         derivedTable.setGroupByStatement(gbs);
      }

      FromTable derivedTableFromItem = new FromTable();
      derivedTableFromItem.setTableName(derivedTable);
      derivedTableFromItem.setAliasName(alias);
      derivedTableFromItem.setJoinClause("LEFT OUTER JOIN ");
      derivedTableFromItem.setOnOrUsingJoin("ON");
      joinCondition = new Vector();
      WhereExpression we = new WhereExpression();
      we.setWhereItem(newWhereItemList);
      Vector operators = new Vector();

      for(int s = 0; s < newWhereItemList.size() - 1; ++s) {
         operators.add("AND");
      }

      we.setOperator(operators);
      joinCondition.add(we);
      derivedTableFromItem.setJoinExpression(joinCondition);
      return derivedTableFromItem;
   }

   private SelectColumn getNewSelectColumnForDerivedTable(FunctionCalls fnCall, int argIndex) throws ConvertException {
      SelectColumn rownumSelCol = new SelectColumn();
      String origFnName = fnCall.getFunctionName().getColumnName();
      FunctionCalls countFunc = new FunctionCalls();
      countFunc.setFunctionName(fnCall.getFunctionName());
      Vector newFuncArgs = new Vector();
      newFuncArgs.addAll(fnCall.getFunctionArguments());
      countFunc.setFunctionArguments(newFuncArgs);
      countFunc.setArgumentQualifier(fnCall.getArgumentQualifier());
      Vector rownumSelColExp = new Vector();
      rownumSelColExp.add(countFunc);
      rownumSelCol.setColumnExpression(rownumSelColExp);
      rownumSelCol.setAliasName("cnt_" + argIndex);
      rownumSelCol.setEndsWith(",");
      return rownumSelCol;
   }

   private WhereItem generateWhereItems(SelectColumn selCol, String derivedTableAlias, String derivedTableColumn) throws ConvertException {
      WhereItem wi = new WhereItem();
      WhereColumn lwc = new WhereColumn();
      Vector lwcColExp = new Vector();
      lwcColExp.add(selCol);
      WhereColumn rwc = new WhereColumn();
      Vector rwcColExp = new Vector();
      if (selCol != null) {
         TableColumn rsc = new TableColumn();
         rsc.setTableName(derivedTableAlias);
         rsc.setColumnName(derivedTableColumn);
         rwcColExp.add(rsc);
      }

      lwc.setColumnExpression(lwcColExp);
      rwc.setColumnExpression(rwcColExp);
      wi.setLeftWhereExp(lwc);
      wi.setRightWhereExp(rwc);
      wi.setOperator("=");
      return wi;
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selColumn = (SelectColumn)this.functionArguments.elementAt(i_count);
            selColumn.convertWhereExpAloneInsideFunctionTo_IF_Function(this.functionArguments.size());
            arguments.addElement(selColumn.toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
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

      this.setFunctionArguments(arguments);
      if (this.argumentQualifier != null && this.argumentQualifier.equalsIgnoreCase("DISTINCT")) {
         FromClause fc = new FromClause();
         fc.setFromClause("FROM");
         Vector v = new Vector();
         v.add((FromTable)from_sqs.getFromClause().getFromItemList().get(0));
         fc.setFromItemList(v);
         String qry = "(SELECT COUNT(*) FROM (SELECT DISTINCT " + arguments.get(0).toString() + " " + fc.toExcelSelect(to_sqs, from_sqs) + "))";
         this.argumentQualifier = null;
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
      }

   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COUNT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }
}
