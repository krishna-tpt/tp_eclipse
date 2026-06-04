package com.adventnet.swissqlapi.sql.functions.aggregate;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WindowingClause;
import java.util.Vector;

public class regression extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
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
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
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

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String firstArg = null;
      String secondArg = null;
      SelectColumn firstArgSelCol = null;
      SelectColumn secondArgSelCol = null;
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selCol = ((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs);
            arguments.addElement(selCol);
            if (i_count == 0) {
               firstArgSelCol = selCol;
               firstArg = selCol.toString();
            } else if (i_count == 1) {
               secondArgSelCol = selCol;
               secondArg = selCol.toString();
            }
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
            if (i_count == 0) {
               firstArg = this.functionArguments.elementAt(i_count).toString();
            } else if (i_count == 1) {
               secondArg = this.functionArguments.elementAt(i_count).toString();
            }
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionName.getColumnName().equalsIgnoreCase("REGR_INTERCEPT") || this.functionName.getColumnName().equalsIgnoreCase("REGR_R2") || this.functionName.getColumnName().equalsIgnoreCase("REGR_SLOPE") || this.functionName.getColumnName().equalsIgnoreCase("REGR_AVGX") || this.functionName.getColumnName().equalsIgnoreCase("REGR_AVGY")) {
         String target = "";
         if (this.functionName.getColumnName().equalsIgnoreCase("REGR_INTERCEPT")) {
            target = "cast (AVG(" + firstArg + ") - (((COUNT(" + secondArg + ") * SUM(" + secondArg + " * " + firstArg + "))-(SUM(" + secondArg + ") * SUM(" + firstArg + ")))/((COUNT(" + secondArg + ") * SUM(" + secondArg + "^2))-(SUM(" + secondArg + ")^2)) * AVG(" + secondArg + ")) as real)";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("REGR_R2")) {
            target = "cast(((((COUNT(" + firstArg + ") * SUM(" + firstArg + " * " + secondArg + "))-((SUM(" + firstArg + ") * SUM(" + secondArg + "))))^2)/ ((COUNT(" + firstArg + ") * SUM(" + firstArg + "^2)-(SUM(" + firstArg + ")) * SUM(" + firstArg + ")) * (count(" + secondArg + ") * sum(" + secondArg + "^2)-(sum(" + secondArg + ") * sum(" + secondArg + "))))) as float)";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("REGR_SLOPE")) {
            target = "cast (((COUNT(" + firstArg + ") * SUM(" + firstArg + " * " + secondArg + "))-(SUM(" + firstArg + ") * SUM(" + secondArg + ")))/((COUNT(" + secondArg + ") * SUM(" + secondArg + "^2))-(SUM(" + secondArg + ")^2)) as real)";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("REGR_AVGX")) {
            target = "cast ((SUM(" + secondArg + ")/COUNT(" + secondArg + ")) as real)";
         } else if (this.functionName.getColumnName().equalsIgnoreCase("REGR_AVGY")) {
            target = "cast ((SUM(" + firstArg + ")/COUNT(" + firstArg + ")) as real)";
         }

         Vector targetArg = new Vector();
         targetArg.add(target);
         this.setFunctionArguments(targetArg);
         this.setOver((String)null);
         this.setOrderBy((OrderByStatement)null);
         this.setPartitionBy((String)null);
         this.setWindowingClause((WindowingClause)null);
         TableColumn firstArgTableCol = (TableColumn)this.getTableColumnFromSelectColumn(firstArgSelCol);
         TableColumn secondArgTableCol = (TableColumn)this.getTableColumnFromSelectColumn(secondArgSelCol);
         WhereExpression whereExp = this.createWhereExpression(firstArgTableCol, secondArgTableCol);
         WhereExpression existingWhereExpr = from_sqs.getWhereExpression();
         if (existingWhereExpr != null && firstArgTableCol != null && secondArgTableCol != null) {
            boolean setWhereExp = true;
            Vector functionColumnVector = existingWhereExpr.getFunctionColumnVector();
            Vector newTableColVector = new Vector();
            int functionColumnVectorSize = functionColumnVector.size();
            int i = 0;

            while(true) {
               if (i >= functionColumnVectorSize) {
                  if (setWhereExp) {
                     existingWhereExpr.addOperator("AND");
                     existingWhereExpr.addWhereExpression(whereExp);
                     newTableColVector.add(firstArgTableCol);
                     newTableColVector.add(secondArgTableCol);
                  }

                  if (newTableColVector.size() > 0) {
                     functionColumnVector.add(newTableColVector.get(0));
                     functionColumnVector.add(newTableColVector.get(1));
                     newTableColVector.clear();
                  }
                  break;
               }

               TableColumn funcTableCol = (TableColumn)functionColumnVector.get(i);
               if (funcTableCol.toString().equalsIgnoreCase(firstArgTableCol.toString()) || funcTableCol.toString().equalsIgnoreCase(secondArgTableCol.toString())) {
                  setWhereExp = false;
               }

               ++i;
            }
         } else {
            from_sqs.setWhereExpression(whereExp);
         }

         this.functionName.setColumnName("");
      }

   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   private WhereExpression createWhereExpression(TableColumn firstCol, TableColumn secondCol) {
      WhereExpression whereExp = new WhereExpression();
      WhereItem firstArgItem = new WhereItem();
      WhereColumn firstArgColumn = new WhereColumn();
      Vector firstArgColExp = new Vector();
      firstArgColExp.add(firstCol);
      firstArgColumn.setColumnExpression(firstArgColExp);
      firstArgItem.setLeftWhereExp(firstArgColumn);
      firstArgItem.setOperator("IS NOT NULL");
      firstArgItem.setRightWhereExp((WhereColumn)null);
      WhereItem secondArgItem = new WhereItem();
      WhereColumn secondArgColumn = new WhereColumn();
      Vector secondArgColExp = new Vector();
      secondArgColExp.add(secondCol);
      secondArgColumn.setColumnExpression(secondArgColExp);
      secondArgItem.setLeftWhereExp(secondArgColumn);
      secondArgItem.setOperator("IS NOT NULL");
      secondArgItem.setRightWhereExp((WhereColumn)null);
      whereExp.addWhereItem(firstArgItem);
      whereExp.addOperator("AND");
      whereExp.addWhereItem(secondArgItem);
      return whereExp;
   }

   private Object getTableColumnFromSelectColumn(SelectColumn selCol) {
      Vector columnExpression = selCol.getColumnExpression();
      int colExpSize = columnExpression.size();

      for(int i = 0; i < colExpSize; ++i) {
         if (columnExpression.elementAt(i) instanceof TableColumn) {
            return columnExpression.elementAt(i);
         }

         if (columnExpression.elementAt(i) instanceof FunctionCalls) {
            FunctionCalls funcCall = (FunctionCalls)columnExpression.elementAt(i);
            Vector funcArgs = funcCall.getFunctionArguments();
            int funcArgsSize = funcArgs.size();

            for(int i_count = 0; i_count < funcArgsSize; ++i_count) {
               if (funcArgs.elementAt(i_count) instanceof SelectColumn) {
                  return this.getTableColumnFromSelectColumn((SelectColumn)funcArgs.elementAt(i_count));
               }
            }
         } else if (columnExpression.elementAt(i) instanceof SelectColumn) {
            return this.getTableColumnFromSelectColumn((SelectColumn)columnExpression.elementAt(i));
         }
      }

      return null;
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }
}
