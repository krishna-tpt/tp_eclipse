package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.ArrayList;

public class QueryPartitionClause {
   ArrayList selectColumnList = new ArrayList();
   String partitionBy = null;
   String openBrace = null;
   String closeBrace = null;

   public void setSelectColumnList(ArrayList selColList) {
      this.selectColumnList = selColList;
   }

   public void setPartitionBy(String partitionBy) {
      this.partitionBy = partitionBy;
   }

   public void setOpenBrace(String openBrace) {
      this.openBrace = openBrace;
   }

   public void setCloseBrace(String closeBrace) {
      this.closeBrace = closeBrace;
   }

   public ArrayList getSelectColumnList() {
      return this.selectColumnList;
   }

   public String getPartitionBy() {
      return this.partitionBy;
   }

   public String getOpenBrace() {
      return this.openBrace;
   }

   public String getCloseBrace() {
      return this.closeBrace;
   }

   public QueryPartitionClause toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toANSISelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      queryPartCl.setOpenBrace(this.openBrace);
      queryPartCl.setCloseBrace(this.closeBrace);
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toTeradataSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toDB2Select(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toInformixSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toMySQLSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toNetezzaSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toOracleSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toBigQuerySelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toAthenaSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toSapHanaSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toSqliteSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toExcelSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toSybaseSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toTimesTenSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.partitionBy != null) {
         sb.append(this.partitionBy + " ");
      }

      if (this.openBrace != null) {
         sb.append(this.openBrace);
      }

      if (this.selectColumnList.size() > 0) {
         int size = this.selectColumnList.size();

         for(int i = 0; i < size; ++i) {
            sb.append(this.selectColumnList.get(i).toString() + " ");
         }
      }

      if (this.closeBrace != null) {
         sb.append(this.closeBrace + " ");
      }

      return sb.toString();
   }

   public QueryPartitionClause toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause queryPartCl = new QueryPartitionClause();
      queryPartCl.setPartitionBy(this.getPartitionBy());
      ArrayList newSelectColList = new ArrayList();
      int selColListSize = this.selectColumnList.size();

      for(int i = 0; i < selColListSize; ++i) {
         Object obj = this.selectColumnList.get(i);
         if (obj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)obj;
            newSelectColList.add(selCol.toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            newSelectColList.add(obj);
         }
      }

      queryPartCl.setSelectColumnList(newSelectColList);
      return queryPartCl;
   }

   public QueryPartitionClause toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      QueryPartitionClause qryPartionClConv = new QueryPartitionClause();
      if (this.partitionBy != null) {
         qryPartionClConv.setPartitionBy(this.partitionBy);
      }

      if (this.openBrace != null) {
         qryPartionClConv.setOpenBrace(this.openBrace);
      }

      if (this.closeBrace != null) {
         qryPartionClConv.setCloseBrace(this.closeBrace);
      }

      if (this.selectColumnList != null) {
         ArrayList newSelectColList = new ArrayList();
         int i = 0;

         for(int selColListSize = this.selectColumnList.size(); i < selColListSize; ++i) {
            if (this.selectColumnList.get(i) instanceof SelectColumn) {
               newSelectColList.add(((SelectColumn)this.selectColumnList.get(i)).toReplaceTblCol(to_sqs, from_sqs));
            } else {
               newSelectColList.add(this.selectColumnList.get(i));
            }
         }

         qryPartionClConv.setSelectColumnList(newSelectColList);
      }

      return qryPartionClConv;
   }
}
