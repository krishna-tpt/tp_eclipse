package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import java.util.Vector;

public class ComputeByStatement {
   private String compute;
   private String byString;
   private Vector functionNameVector;
   private Vector tableNameVector;

   public void setCompute(String compute) {
      this.compute = compute;
   }

   public void setFunctionNameVector(Vector functionNameVector) {
      this.functionNameVector = functionNameVector;
   }

   public void setBy(String byString) {
      this.byString = byString;
   }

   public void setTableNameVector(Vector tableNameVector) {
      this.tableNameVector = tableNameVector;
   }

   public Vector getFunctionNameVector() {
      return this.functionNameVector;
   }

   public Vector getTableNameVector() {
      return this.tableNameVector;
   }

   public ComputeByStatement toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toANSISelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toANSISelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toTeradataSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toTeradataSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toDB2Select(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toDB2Select(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toInformixSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toInformixSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toMSSQLServerSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toMSSQLServerSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toMySQLSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toMySQLSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toOracleSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toOracleSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toBigQuerySelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toBigQuerySelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toAthenaSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toAthenaSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toSapHanaSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toSapHanaSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toSqliteSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toSqliteSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toExcelSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toExcelSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toMsAccessJdbcSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toMsAccessJdbcSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      return cbs;
   }

   public ComputeByStatement toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toNetezzaSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toNetezzaSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toSnowflakeSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toSnowflakeSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.compute != null) {
         sb.append(this.compute.toUpperCase());
      }

      int i_count;
      int i;
      if (this.functionNameVector != null) {
         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);

         for(i_count = 0; i_count < this.functionNameVector.size(); ++i_count) {
            if (i_count == 0) {
               sb.append(" " + ((FunctionCalls)this.functionNameVector.elementAt(i_count)).toString());
            } else {
               sb.append(",");
               sb.append("\n");

               for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
                  sb.append("\t");
               }

               sb.append(((FunctionCalls)this.functionNameVector.elementAt(i_count)).toString());
            }
         }

         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
      }

      if (this.byString != null) {
         sb.append(" " + this.byString.toUpperCase());
      }

      if (this.tableNameVector != null) {
         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);

         for(i_count = 0; i_count < this.tableNameVector.size(); ++i_count) {
            if (i_count == 0) {
               sb.append(" " + ((TableColumn)this.tableNameVector.elementAt(i_count)).toString());
            } else {
               sb.append(",");
               sb.append("\n");

               for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
                  sb.append("\t\t");
               }

               sb.append(((TableColumn)this.tableNameVector.elementAt(i_count)).toString());
            }
         }

         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
      }

      return sb.toString();
   }

   public ComputeByStatement toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement cbs = new ComputeByStatement();
      cbs.setCompute(this.compute);
      if (this.byString != null) {
         cbs.setBy(this.byString);
      }

      Vector tableNames;
      int i;
      if (this.functionNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.functionNameVector.get(i);
               tableNames.add(fc.toVectorWiseSelect(to_sqs, from_sqs));
            }
         }

         cbs.setFunctionNameVector(tableNames);
      }

      if (this.tableNameVector != null) {
         tableNames = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)this.tableNameVector.get(i);
               tableNames.add(tc.toVectorWiseSelect(to_sqs, from_sqs));
            }
         }

         cbs.setTableNameVector(tableNames);
      }

      return cbs;
   }

   public ComputeByStatement toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ComputeByStatement computeByStmtConv = new ComputeByStatement();
      if (this.compute != null) {
         computeByStmtConv.setCompute(this.compute);
      }

      if (this.byString != null) {
         computeByStmtConv.setBy(this.byString);
      }

      Vector tblNameVectorConv;
      int i;
      if (this.functionNameVector != null) {
         tblNameVectorConv = new Vector();

         for(i = 0; i < this.functionNameVector.size(); ++i) {
            if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
               tblNameVectorConv.add(((FunctionCalls)this.functionNameVector.get(i)).toReplaceTblCol(to_sqs, from_sqs));
            }
         }

         computeByStmtConv.setFunctionNameVector(tblNameVectorConv);
      }

      if (this.tableNameVector != null) {
         tblNameVectorConv = new Vector();

         for(i = 0; i < this.tableNameVector.size(); ++i) {
            if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
               tblNameVectorConv.add(((TableColumn)this.tableNameVector.get(i)).toReplaceTblCol(to_sqs, from_sqs));
            }
         }

         computeByStmtConv.setTableNameVector(this.tableNameVector);
      }

      return computeByStmtConv;
   }
}
