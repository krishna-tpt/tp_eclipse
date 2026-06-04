package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class IndexColumn {
   private SelectColumn selectColumn;
   private String ascOrDesc;
   private UserObjectContext context = null;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setIndexColumnName(SelectColumn selectColumn) {
      this.selectColumn = selectColumn;
   }

   public void setAscOrDesc(String ascOrDesc) {
      this.ascOrDesc = ascOrDesc;
   }

   public SelectColumn getIndexColumnName() {
      return this.selectColumn;
   }

   public String getAscOrDesc() {
      return this.ascOrDesc;
   }

   public IndexColumn toANSI() throws ConvertException {
      IndexColumn tempIndexColumn = this.copyObjectvalues();
      if (tempIndexColumn.getIndexColumnName() != null) {
         SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
         SelectColumn ansiSelectColumn = tempSelectColumn.toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null);
         tempIndexColumn.setIndexColumnName(ansiSelectColumn);
      }

      tempIndexColumn.setAscOrDesc((String)null);
      return tempIndexColumn;
   }

   public IndexColumn toDB2() throws ConvertException {
      IndexColumn tempIndexColumn = this.copyObjectvalues();
      if (tempIndexColumn.getIndexColumnName() != null) {
         SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
         SelectColumn db2SelectColumn = tempSelectColumn.toDB2Select((SelectQueryStatement)null, (SelectQueryStatement)null);
         tempIndexColumn.setIndexColumnName(db2SelectColumn);
      }

      tempIndexColumn.setAscOrDesc((String)null);
      return tempIndexColumn;
   }

   public IndexColumn toInformix() throws ConvertException {
      IndexColumn tempIndexColumn = this.copyObjectvalues();
      if (tempIndexColumn.getIndexColumnName() != null) {
         SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
         SelectColumn informixSelectColumn = tempSelectColumn.toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
         tempIndexColumn.setIndexColumnName(informixSelectColumn);
      }

      tempIndexColumn.setAscOrDesc((String)null);
      return tempIndexColumn;
   }

   public IndexColumn toMSSQLServer() throws ConvertException {
      IndexColumn tempIndexColumn = this.copyObjectvalues();
      if (tempIndexColumn.getIndexColumnName() != null) {
         if (tempIndexColumn.toString().trim().equalsIgnoreCase("date")) {
            tempIndexColumn.setIndexColumnName(tempIndexColumn.getIndexColumnName());
         } else {
            SelectColumn tempSelectColumn;
            if (tempIndexColumn.toString().trim().equalsIgnoreCase("user")) {
               tempSelectColumn = new SelectColumn();
               Vector tempSelectColumnVector = new Vector();
               tempSelectColumnVector.add("[user]");
               tempSelectColumn.setColumnExpression(tempSelectColumnVector);
               tempIndexColumn.setIndexColumnName(tempSelectColumn);
            } else {
               tempSelectColumn = tempIndexColumn.getIndexColumnName();
               SelectColumn msSQLServerSelectColumn = tempSelectColumn.toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
               tempIndexColumn.setIndexColumnName(msSQLServerSelectColumn);
            }
         }
      }

      if (tempIndexColumn.getAscOrDesc() != null) {
         String tempAscOrDesc = tempIndexColumn.getAscOrDesc();
         tempIndexColumn.setAscOrDesc(tempAscOrDesc);
      }

      return tempIndexColumn;
   }

   public IndexColumn toSybase() throws ConvertException {
      IndexColumn tempIndexColumn = this.copyObjectvalues();
      if (tempIndexColumn.getIndexColumnName() != null) {
         SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
         SelectColumn sybaseSelectColumn = tempSelectColumn.toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
         tempIndexColumn.setIndexColumnName(sybaseSelectColumn);
      }

      if (tempIndexColumn.getAscOrDesc() != null) {
         String tempAscOrDesc = tempIndexColumn.getAscOrDesc();
         tempIndexColumn.setAscOrDesc(tempAscOrDesc);
      }

      return tempIndexColumn;
   }

   public IndexColumn toMySQL() throws ConvertException {
      IndexColumn tempIndexColumn = this.copyObjectvalues();
      if (tempIndexColumn.getIndexColumnName() != null) {
         SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
         Vector colExp = tempSelectColumn.getColumnExpression();
         boolean ignoreConversion = false;
         if (colExp != null) {
            int colExpSize = colExp.size();

            for(int i = 0; i < colExpSize; ++i) {
               Object obj = colExp.get(i);
               if (obj instanceof TableColumn) {
                  TableColumn tabCol = (TableColumn)obj;
                  String colName = tabCol.getColumnName();
                  if (colName != null && colName.trim().equalsIgnoreCase("DATE")) {
                     ignoreConversion = true;
                     colName = "`" + colName + "`";
                     tabCol.setColumnName(colName);
                  }
               }
            }
         }

         if (!ignoreConversion) {
            SelectColumn mySQLSelectColumn = tempSelectColumn.toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            tempIndexColumn.setIndexColumnName(mySQLSelectColumn);
         }
      }

      tempIndexColumn.setAscOrDesc((String)null);
      return tempIndexColumn;
   }

   public IndexColumn toOracle() throws ConvertException {
      IndexColumn tempIndexColumn = this.copyObjectvalues();
      if (tempIndexColumn.getIndexColumnName() != null) {
         SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
         SelectColumn oracleSelectColumn = tempSelectColumn.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
         Vector colExpr = oracleSelectColumn.getColumnExpression();

         for(int i = 0; i < colExpr.size(); ++i) {
            Object obj = colExpr.get(i);
            if (obj instanceof TableColumn) {
               TableColumn tc = (TableColumn)obj;
               String tableName = tc.getTableName();
               String colName = tc.getColumnName();
               if (tableName == null && colName.startsWith("\"") && colName.endsWith("\"")) {
                  colName = colName.substring(1, colName.length() - 1);
                  if (colName.length() > 30) {
                     colName = "\"" + colName.substring(0, 30) + "\"";
                     tc.setColumnName(colName);
                  }
               }
            }
         }

         tempIndexColumn.setIndexColumnName(oracleSelectColumn);
      }

      if (tempIndexColumn.getAscOrDesc() != null) {
         String tempAscOrDesc = tempIndexColumn.getAscOrDesc();
         tempIndexColumn.setAscOrDesc(tempAscOrDesc);
      }

      return tempIndexColumn;
   }

   public IndexColumn toBigQuery() throws ConvertException {
      IndexColumn tempIndexColumn = this.copyObjectvalues();
      if (tempIndexColumn.getIndexColumnName() != null) {
         SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
         SelectColumn bigQueryColumn = tempSelectColumn.toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null);
         tempIndexColumn.setIndexColumnName(bigQueryColumn);
      }

      tempIndexColumn.setAscOrDesc((String)null);
      return tempIndexColumn;
   }

   public IndexColumn toPostgreSQL() throws ConvertException {
      IndexColumn tempIndexColumn = this.copyObjectvalues();
      if (tempIndexColumn.getIndexColumnName() != null) {
         SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
         SelectColumn postgreSelectColumn = tempSelectColumn.toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
         tempIndexColumn.setIndexColumnName(postgreSelectColumn);
      }

      tempIndexColumn.setAscOrDesc((String)null);
      return tempIndexColumn;
   }

   public IndexColumn toTimesTen() throws ConvertException {
      IndexColumn tempIndexColumn = this.copyObjectvalues();
      if (tempIndexColumn.getIndexColumnName() instanceof SelectColumn) {
         Vector colexp = tempIndexColumn.getIndexColumnName().getColumnExpression();
         if (colexp.size() > 1) {
            throw new ConvertException("\n\nIndex creation on 'Column Expressions' is not supported in TimesTen 5.1.21\n");
         }

         if (colexp.get(0) instanceof FunctionCalls) {
            throw new ConvertException("\n\nIndex creation on 'Function Calls' is not supported in TimesTen 5.1.21\n");
         }
      }

      if (tempIndexColumn.getIndexColumnName() != null) {
         SelectColumn var3 = tempIndexColumn.getIndexColumnName();
      }

      if (tempIndexColumn.getAscOrDesc() != null) {
         String tempAscOrDesc = tempIndexColumn.getAscOrDesc();
         tempIndexColumn.setAscOrDesc(tempAscOrDesc);
      }

      return tempIndexColumn;
   }

   public IndexColumn toNetezza() throws ConvertException {
      IndexColumn tempIndexColumn = this.copyObjectvalues();
      if (tempIndexColumn.getIndexColumnName() != null) {
         SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
         SelectColumn netezzaSelectColumn = tempSelectColumn.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
         tempIndexColumn.setIndexColumnName(netezzaSelectColumn);
      }

      tempIndexColumn.setAscOrDesc((String)null);
      return tempIndexColumn;
   }

   public IndexColumn toSnowflake() throws ConvertException {
      IndexColumn tempIndexColumn = this.copyObjectvalues();
      if (tempIndexColumn.getIndexColumnName() != null) {
         SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
         SelectColumn snowflakeSelectColumn = tempSelectColumn.toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
         tempIndexColumn.setIndexColumnName(snowflakeSelectColumn);
      }

      tempIndexColumn.setAscOrDesc((String)null);
      return tempIndexColumn;
   }

   public IndexColumn toTeradata() throws ConvertException {
      IndexColumn tempIndexColumn = this.copyObjectvalues();
      if (tempIndexColumn.getIndexColumnName() != null) {
         SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
         SelectColumn TeradataSelectColumn = tempSelectColumn.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
         tempIndexColumn.setIndexColumnName(TeradataSelectColumn);
      }

      tempIndexColumn.setAscOrDesc((String)null);
      return tempIndexColumn;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.selectColumn != null) {
         this.selectColumn.setObjectContext(this.context);
         sb.append(this.selectColumn + " ");
      }

      if (this.ascOrDesc != null) {
         sb.append(this.ascOrDesc.toUpperCase() + " ");
      }

      return sb.toString();
   }

   public IndexColumn copyObjectvalues() {
      IndexColumn dupIndexColumn = new IndexColumn();
      dupIndexColumn.setIndexColumnName(this.selectColumn);
      dupIndexColumn.setAscOrDesc(this.ascOrDesc);
      dupIndexColumn.setObjectContext(this.context);
      return dupIndexColumn;
   }
}
