package com.adventnet.swissqlapi.sql.functions.aggregate;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WindowingClause;
import java.util.ArrayList;
import java.util.Vector;

public class sum extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUM");
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
      this.functionName.setColumnName("SUM");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         SelectColumn sc = new SelectColumn();
         FunctionCalls fc = new FunctionCalls();
         TableColumn functionName = new TableColumn();
         functionName.setColumnName("CONVERT");
         fc.setFunctionName(functionName);
         Vector subFunctionArgs = new Vector();
         subFunctionArgs.add("FLOAT");
         subFunctionArgs.add(this.functionArguments.get(0));
         fc.setFunctionArguments(subFunctionArgs);
         Vector colExp = new Vector();
         colExp.add(fc);
         sc.setColumnExpression(colExp);
         Vector newFunctionArg = new Vector();
         newFunctionArg.add(sc);
         this.setFunctionArguments(newFunctionArg);
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUM");
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
      this.functionName.setColumnName("SUM");
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
      this.functionName.setColumnName("SUM");
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
      this.functionName.setColumnName("SUM");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (from_sqs != null && !from_sqs.getBooleanValues("allow.logical.exp.in.agg.function")) {
         this.validateAggFunArgsType(arguments, "SUM");
      }

      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUM");
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
      this.functionName.setColumnName("SUM");
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
      this.functionName.setColumnName("SUM");
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
      this.functionName.setColumnName("SUM");
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
      this.functionName.setColumnName("SUM");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.getOrderBy() != null && this.getWindowingClause() != null && this.getWindowingClause().toString().equals("")) {
         WindowingClause windowClause = new WindowingClause();
         windowClause.setRowsOrRange("ROWS");
         windowClause.setUnbounded("UNBOUNDED");
         windowClause.setPreceding("PRECEDING");
         this.setWindowingClause(windowClause);
      }

      String argQualifier = this.getArgumentQualifier();
      if (argQualifier != null && argQualifier.equalsIgnoreCase("distinct") && this.getOver() != null && this.getOver().equalsIgnoreCase("over") && this.getPartitionByClause() != null) {
         String pString = this.getPartitionByClause().toString();
         String colString = this.getFunctionArguments().get(0).toString();
         new FromTable();
         FromTable ft;
         if (from_sqs.getSumDerivedTables().containsKey(this.getPartitionByClause().toString())) {
            ft = (FromTable)from_sqs.getSumDerivedTables().get(pString);
            SelectQueryStatement dummysqs = (SelectQueryStatement)ft.getTableName();
            Vector oldSelectItems = new Vector(dummysqs.getSelectStatement().getSelectItemList());
            SelectColumn dummysc = (SelectColumn)oldSelectItems.lastElement();
            dummysc.setEndsWith(",");
            oldSelectItems.setElementAt(dummysc, oldSelectItems.size() - 1);
            Vector v = new Vector();
            FunctionCalls fc = new FunctionCalls();
            fc.setFunctionArguments(this.getFunctionArguments());
            fc.setFunctionName(this.getFunctionName());
            fc.setArgumentQualifier(this.getArgumentQualifier());
            fc.setOrderBy(this.getOrderBy());
            v.add(fc);
            SelectColumn fnCallsc = new SelectColumn();
            fnCallsc.setColumnExpression(v);
            fnCallsc.setAliasName("sum_" + (oldSelectItems.size() + 1));
            oldSelectItems.add(fnCallsc);
            dummysqs.getSelectStatement().setSelectItemList(oldSelectItems);
            ft.setTableName(dummysqs);
            from_sqs.addSumDerivedTables(pString, ft);
            from_sqs.addSumSelectColumnAlias(pString + colString, "sum_" + oldSelectItems.size());
         } else {
            SelectColumn sc = new SelectColumn();
            new SelectColumn();
            SelectColumn dummysc = (SelectColumn)this.getFunctionArguments().get(0);
            sc.setColumnExpression(dummysc.getColumnExpression());
            String aliasName = "SUM_ALIAS";
            String indexForAlias = "" + (from_sqs.getSumDerivedTables().size() + 1);
            ft = this.createTeradataDerivedTable(to_sqs, this, sc, aliasName + indexForAlias);
            ft.setAliasName(aliasName + indexForAlias);
            from_sqs.setSumFunctionWithPartitionAvailable(true);
            from_sqs.addSumDerivedTables(pString, ft);
            from_sqs.addSumSelectColumnAlias(pString + colString, "sum_" + this.getPartitionByClause().getSelectColumnList().size());
         }
      }

   }

   public FromTable createTeradataDerivedTable(SelectQueryStatement to_sqs, FunctionCalls fnCall, SelectColumn functionArgument, String alias) throws ConvertException {
      String fnName = fnCall.getFunctionName().getColumnName();
      SelectQueryStatement derivedTable = new SelectQueryStatement();
      SelectStatement selStmt = new SelectStatement();
      selStmt.setSelectClause("SELECT");
      Vector newSelColList = new Vector();
      Vector newWhereItemList = new Vector();
      WhereItem wi = new WhereItem();
      WhereColumn lwc = new WhereColumn();
      Vector lwcColExp = new Vector();
      WhereColumn rwc = new WhereColumn();
      Vector rwcColExp = new Vector();
      rwcColExp.add("1");
      lwc.setColumnExpression(lwcColExp);
      rwc.setColumnExpression(rwcColExp);
      wi.setLeftWhereExp(lwc);
      wi.setRightWhereExp(rwc);
      wi.setOperator("=");
      Vector v;
      if (fnCall.getPartitionByClause() != null) {
         ArrayList selColsList = fnCall.getPartitionByClause().getSelectColumnList();

         SelectColumn newPartSelCol;
         for(int k = 0; k < selColsList.size(); ++k) {
            if (selColsList.get(k) instanceof SelectColumn) {
               SelectColumn partSelCol = (SelectColumn)selColsList.get(k);
               newPartSelCol = new SelectColumn();
               newPartSelCol.setColumnExpression(partSelCol.getColumnExpression());
               newPartSelCol.setAliasName("sum_" + k);
               newPartSelCol.setEndsWith(",");
               newWhereItemList.add(this.generateWhereItems(partSelCol, alias, "sum_" + k));
               newSelColList.add(newPartSelCol);
            }
         }

         v = new Vector();
         FunctionCalls fc = new FunctionCalls();
         fc.setFunctionArguments(fnCall.getFunctionArguments());
         fc.setFunctionName(fnCall.getFunctionName());
         fc.setArgumentQualifier(fnCall.getArgumentQualifier());
         fc.setOrderBy(fnCall.getOrderBy());
         v.add(fc);
         newPartSelCol = new SelectColumn();
         newPartSelCol.setColumnExpression(v);
         newPartSelCol.setAliasName("sum_" + selColsList.size());
         newSelColList.add(newPartSelCol);
      } else {
         newWhereItemList.add(wi);
      }

      ((SelectColumn)newSelColList.lastElement()).setEndsWith((String)null);
      selStmt.setSelectItemList(newSelColList);
      derivedTable.setSelectStatement(selStmt);
      WhereExpression weForDerivedTable = new WhereExpression();
      v = new Vector();
      WhereItem newWI = new WhereItem();
      WhereColumn newWC = new WhereColumn();
      if (functionArgument.getIgnoreNulls() != null) {
         newWI.setOperator("IS NOT NULL");
         newWC.setColumnExpression(functionArgument.getColumnExpression());
         newWI.setLeftWhereExp(newWC);
         v.add(newWI);
         weForDerivedTable.setWhereItem(v);
         derivedTable.setWhereExpression(weForDerivedTable);
      }

      FromTable derivedTableFromItem = new FromTable();
      derivedTableFromItem.setTableName(derivedTable);
      derivedTableFromItem.setAliasName(alias);
      derivedTableFromItem.setJoinClause("LEFT OUTER JOIN ");
      derivedTableFromItem.setOnOrUsingJoin("ON");
      Vector joinCondition = new Vector();
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

   private WhereItem generateWhereItems(SelectColumn selCol, String derivedTableAlias, String derivedTableColumn) throws ConvertException {
      WhereItem wi = new WhereItem();
      WhereColumn lwc = new WhereColumn();
      Vector lwcColExp = new Vector();
      WhereColumn rwc = new WhereColumn();
      Vector rwcColExp = new Vector();
      if (selCol != null) {
         new TableColumn();
         Vector vlwc = new Vector(this.changeTableNameInWhereItems(selCol.getColumnExpression(), "orgnl"));
         SelectColumn sc = new SelectColumn();
         sc.setColumnExpression(vlwc);
         lwcColExp.add(sc);
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

   private Vector changeTableNameInWhereItems(Vector vc, String alias) {
      Vector vc1 = new Vector();
      int i = 0;

      for(int size = vc.size(); i < size; ++i) {
         if (vc.get(i) instanceof SelectColumn) {
            new SelectColumn();
            SelectColumn scOld = (SelectColumn)vc.get(i);
            Vector scv = new Vector();
            scv.addAll(scOld.getColumnExpression());
            SelectColumn scNew = new SelectColumn();
            scNew.setColumnExpression(this.changeTableNameInWhereItems(scv, alias));
            vc1.add(scNew);
         }

         if (vc.get(i) instanceof TableColumn) {
            TableColumn tc = (TableColumn)vc.get(i);
            TableColumn tcNew = new TableColumn();
            tcNew.setTableName(alias);
            tcNew.setColumnName(tc.getColumnName());
            vc1.add(tcNew);
         }

         if (vc.get(i) instanceof FunctionCalls) {
            Vector vfc = new Vector(((FunctionCalls)vc.get(i)).getFunctionArguments());
            vfc = new Vector(this.changeTableNameInWhereItems(vfc, alias));
            FunctionCalls fc = new FunctionCalls();
            fc.setFunctionArguments(vfc);
            vc1.add(fc);
         }
      }

      return vc1;
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUM");
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

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUM");
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
      this.functionName.setColumnName("SUM");
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

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUM");
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
      this.functionName.setColumnName("SUM");
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
      this.functionName.setColumnName("SUM");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("SUM");
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
