package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Vector;

public class FetchClause {
   public String FetchFirstClause;
   public String FetchCount;
   public String RowOnlyClause;
   private String fetchCountVariable;
   private String fetchOffsetCount;

   public void setFetchFirstClause(String s_ffc) {
      this.FetchFirstClause = s_ffc;
   }

   public void setFetchCount(String s_fc) {
      this.FetchCount = s_fc;
   }

   public void setFetchOffSetCount(String s_fc) {
      this.fetchOffsetCount = s_fc;
   }

   public void setFetchCountVariable(String variable) {
      this.fetchCountVariable = variable;
   }

   public void setRowOnlyClause(String s_roc) {
      this.RowOnlyClause = s_roc;
   }

   public String getFetchFirstClause() {
      return this.FetchFirstClause;
   }

   public String getFetchCount() {
      return this.FetchCount;
   }

   public String getFetchCountVariable() {
      return this.fetchCountVariable;
   }

   public String getRowOnlyClause() {
      return this.RowOnlyClause;
   }

   public String getFetchOffSetCount(String s_fc) {
      return this.fetchOffsetCount;
   }

   public FetchClause toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FetchClause fc = new FetchClause();
      fc.setFetchFirstClause(this.FetchFirstClause);
      fc.setFetchCount(this.FetchCount);
      fc.setRowOnlyClause(this.RowOnlyClause);
      return fc;
   }

   public FetchClause toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
         throw new ConvertException();
      } else {
         to_sqs.getSelectStatement().setSelectRowSpecifier("TOP");
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.FetchCount));
         return null;
      }
   }

   public FetchClause toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
         throw new ConvertException();
      } else {
         to_sqs.getSelectStatement().setSelectRowSpecifier("TOP");
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.FetchCount));
         return null;
      }
   }

   public FetchClause toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (to_sqs.getLimitClause() != null) {
         throw new ConvertException();
      } else {
         lc.setLimitClause("LIMIT");
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         lc.setLimitValue(this.FetchCount);
         to_sqs.setLimitClause(lc);
         return null;
      }
   }

   public FetchClause toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (to_sqs.getLimitClause() != null) {
         throw new ConvertException();
      } else {
         lc.setLimitClause("LIMIT");
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         lc.setLimitValue(this.FetchCount);
         to_sqs.setLimitClause(lc);
         return null;
      }
   }

   public FetchClause toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (to_sqs.getLimitClause() != null) {
         throw new ConvertException();
      } else {
         lc.setLimitClause("LIMIT");
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         lc.setLimitValue(this.FetchCount);
         to_sqs.setLimitClause(lc);
         return null;
      }
   }

   public FetchClause toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (to_sqs.getLimitClause() != null) {
         throw new ConvertException();
      } else {
         lc.setLimitClause("LIMIT");
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         lc.setLimitValue(this.FetchCount);
         to_sqs.setLimitClause(lc);
         return null;
      }
   }

   public FetchClause toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
         throw new ConvertException();
      } else {
         to_sqs.getSelectStatement().setSelectRowSpecifier("TOP");
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.FetchCount));
         return null;
      }
   }

   public FetchClause toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (to_sqs.getLimitClause() != null) {
         throw new ConvertException();
      } else {
         lc.setLimitClause("LIMIT");
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         lc.setLimitValue(this.FetchCount);
         to_sqs.setLimitClause(lc);
         return null;
      }
   }

   public FetchClause toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (to_sqs.getLimitClause() != null) {
         throw new ConvertException();
      } else {
         lc.setLimitClause("LIMIT");
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         lc.setLimitValue(this.FetchCount);
         to_sqs.setLimitClause(lc);
         return null;
      }
   }

   public FetchClause toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (to_sqs.getLimitClause() != null) {
         throw new ConvertException();
      } else {
         lc.setLimitClause("LIMIT");
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         lc.setLimitValue(this.FetchCount);
         to_sqs.setLimitClause(lc);
         return null;
      }
   }

   public FetchClause toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (to_sqs.getLimitClause() != null) {
         throw new ConvertException();
      } else {
         lc.setLimitClause("LIMIT");
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         lc.setLimitValue(this.FetchCount);
         to_sqs.setLimitClause(lc);
         return null;
      }
   }

   public FetchClause toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      new LimitClause();
      if (from_sqs.getLimitClause() == null && from_sqs.getSelectStatement().getSelectRowSpecifier() == null) {
         WhereExpression f_we = from_sqs.getWhereExpression();
         WhereItem wi = new WhereItem();
         Vector v_temp = new Vector();
         WhereColumn wc_temp = new WhereColumn();
         v_temp.addElement("ROWNUM");
         wc_temp.setColumnExpression(v_temp);
         wi.setLeftWhereExp(wc_temp);
         wi.setOperator("<");
         v_temp = new Vector();
         wc_temp = new WhereColumn();
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         v_temp.addElement(Integer.toString(Integer.parseInt(this.FetchCount) + 1));
         wc_temp.setColumnExpression(v_temp);
         wi.setRightWhereExp(wc_temp);
         if (f_we != null && f_we.getCheckWhere()) {
            to_sqs.getWhereExpression().addOperator("AND");
            to_sqs.getWhereExpression().addWhereItem(wi);
         } else if (f_we != null) {
            to_sqs.setWhereExpression(f_we.toOracleSelect(to_sqs, from_sqs));
            to_sqs.getWhereExpression().addOperator("AND");
            to_sqs.getWhereExpression().addWhereItem(wi);
         } else {
            WhereExpression we = new WhereExpression();
            we.addWhereItem(wi);
            if (to_sqs != null && to_sqs.getWhereExpression() != null) {
               to_sqs.getWhereExpression().addOperator("AND");
               to_sqs.getWhereExpression().addWhereExpression(we);
            } else {
               to_sqs.setWhereExpression(we);
            }
         }

         return null;
      } else {
         throw new ConvertException();
      }
   }

   public FetchClause toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
         throw new ConvertException();
      } else {
         to_sqs.getSelectStatement().setInformixRowSpecifier("FIRST");
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.FetchCount));
         return null;
      }
   }

   public FetchClause toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
         throw new ConvertException();
      } else {
         to_sqs.getSelectStatement().setSelectRowSpecifier("FIRST");
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.FetchCount));
         return null;
      }
   }

   public FetchClause toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      LimitClause lc = new LimitClause();
      if (to_sqs.getLimitClause() != null) {
         throw new ConvertException();
      } else {
         lc.setLimitClause("LIMIT");
         if (this.FetchCount == null) {
            this.FetchCount = "1";
         }

         lc.setLimitValue(this.FetchCount);
         to_sqs.setLimitClause(lc);
         return null;
      }
   }

   public FetchClause toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FetchClause fetchClConv = new FetchClause();
      if (this.FetchFirstClause != null) {
         fetchClConv.setFetchFirstClause(this.FetchFirstClause);
      }

      if (this.FetchCount != null) {
         fetchClConv.setFetchCount(this.FetchCount);
      }

      if (this.RowOnlyClause != null) {
         fetchClConv.setRowOnlyClause(this.RowOnlyClause);
      }

      if (this.fetchCountVariable != null) {
         fetchClConv.setFetchCountVariable(this.fetchCountVariable);
      }

      if (this.fetchOffsetCount != null) {
         fetchClConv.setFetchOffSetCount(this.fetchOffsetCount);
      }

      return fetchClConv;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.fetchOffsetCount == null) {
         sb.append(this.FetchFirstClause.toUpperCase());
         if (this.fetchCountVariable != null) {
            sb.append(" (" + this.fetchCountVariable + ")");
         } else if (this.FetchCount != null) {
            sb.append(" " + this.FetchCount.toUpperCase());
         }

         sb.append(" " + this.RowOnlyClause.toUpperCase());
      } else {
         sb.append("OFFSET");
         sb.append(" " + this.fetchOffsetCount);
         sb.append(" " + this.FetchFirstClause);
         sb.append(" " + this.FetchCount);
         sb.append(" " + this.RowOnlyClause);
      }

      return sb.toString();
   }
}
