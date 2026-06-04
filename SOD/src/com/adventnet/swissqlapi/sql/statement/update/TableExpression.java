package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.ArrayList;
import java.util.Vector;

public class TableExpression {
   private ArrayList tableClauseList = null;
   private SelectQueryStatement subQuery = null;
   private SampleClause sampleClause = null;
   private String remoteTable;
   private WithClause withClause = null;
   private TableCollectionExpression tblCollExp = null;
   private boolean tableNameforAliasName = false;
   private UserObjectContext context = null;
   private String starInTableExp = null;
   public static boolean isUpdateStatement = false;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setTableClauseList(ArrayList list) {
      this.tableClauseList = list;
   }

   public ArrayList getTableClauseList() {
      return this.tableClauseList;
   }

   public void setSubQuery(SelectQueryStatement s) {
      this.subQuery = s;
   }

   public void setSampleClause(SampleClause sampleclause) {
      this.sampleClause = sampleclause;
   }

   public void setWithClause(WithClause withclause) {
      this.withClause = withclause;
   }

   public void setRemoteTable(String s) {
      this.remoteTable = s;
   }

   public void setTableCollectionExpression(TableCollectionExpression tablecollectionexpression) {
      this.tblCollExp = tablecollectionexpression;
   }

   public void setTableNameforAliasNameInDB2Insert(boolean tableNameForAliasName) {
      this.tableNameforAliasName = tableNameForAliasName;
   }

   public void setStarInTableExp(String star) {
      this.starInTableExp = star;
   }

   public String getStarInTableExp() {
      return this.starInTableExp;
   }

   public SelectQueryStatement getSubQuery() {
      return this.subQuery;
   }

   public SampleClause getSampleClause() {
      return this.sampleClause;
   }

   public WithClause getWithClause() {
      return this.withClause;
   }

   public String getRemoteTable() {
      return this.remoteTable;
   }

   public TableCollectionExpression getTableCollectionExpression() {
      return this.tblCollExp;
   }

   public void toGeneric() {
      if (this.subQuery == null) {
         if (this.tableClauseList != null && this.tableClauseList.get(0) != null && this.tableClauseList.get(0) instanceof TableClause) {
            TableClause tableClause = (TableClause)this.tableClauseList.get(0);
            if (tableClause.getAlias() != null) {
               return;
            }
         }
      } else {
         FromClause fromClause = this.subQuery.getFromClause();
         Vector fromItemList = fromClause.getFromItemList();
         FromTable fromTable = (FromTable)fromItemList.elementAt(0);
         String tableName = (String)fromTable.getTableName();
         this.tableClauseList = new ArrayList();
         this.tableClauseList.add(tableName);
         this.subQuery = null;
      }

      this.sampleClause = null;
      this.remoteTable = null;
      this.withClause = null;
      this.tblCollExp = null;
   }

   public void toMySQL() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toMySQL();
      }

      if (this.tableClauseList != null) {
         int size = this.tableClauseList.size();

         for(int i = 0; i < size; ++i) {
            if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause && !isUpdateStatement) {
               ((TableClause)this.tableClauseList.get(i)).toMySQL();
               ((TableClause)this.tableClauseList.get(i)).setAlias("");
            }
         }
      }

      this.toGeneric();
   }

   public void toOracle() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toOracle();
      }

      if (this.tableClauseList != null) {
         int size = this.tableClauseList.size();

         for(int i = 0; i < size; ++i) {
            if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
               ((TableClause)this.tableClauseList.get(i)).toOracle();
            }
         }
      }

      this.toGeneric();
   }

   public void toMSSQLServer() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toMSSQLServer();
      }

      if (this.tableClauseList != null) {
         int size = this.tableClauseList.size();

         for(int i = 0; i < size; ++i) {
            if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
               ((TableClause)this.tableClauseList.get(i)).toMSSQLServer();
            }
         }
      }

      this.toGeneric();
   }

   public void toSybase() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toSybase();
      }

      if (this.tableClauseList != null) {
         int size = this.tableClauseList.size();

         for(int i = 0; i < size; ++i) {
            if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
               ((TableClause)this.tableClauseList.get(i)).toSybase();
               ((TableClause)this.tableClauseList.get(i)).setAlias("");
            }
         }
      }

      this.toGeneric();
   }

   public void toBigQuery() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toBigQuery();
      }

      if (this.tableClauseList != null) {
         int size = this.tableClauseList.size();

         for(int i = 0; i < size; ++i) {
            if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
               ((TableClause)this.tableClauseList.get(i)).toBigQuery();
               ((TableClause)this.tableClauseList.get(i)).setAlias("");
            }
         }
      }

      this.toGeneric();
   }

   public void toPostgreSQL() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toPostgreSQL();
      }

      if (this.tableClauseList != null) {
         int size = this.tableClauseList.size();

         for(int i = 0; i < size; ++i) {
            if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
               ((TableClause)this.tableClauseList.get(i)).toPostgreSQL();
               ((TableClause)this.tableClauseList.get(i)).setAlias("");
            }
         }
      }

      this.toGeneric();
   }

   public void toDB2() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toDB2();
      }

      if (this.tableClauseList != null) {
         int size = this.tableClauseList.size();

         for(int i = 0; i < size; ++i) {
            if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
               ((TableClause)this.tableClauseList.get(i)).toDB2();
               if (this.tableNameforAliasName) {
                  ((TableClause)this.tableClauseList.get(i)).setAlias("");
               }

               this.tableNameforAliasName = false;
            }
         }
      }

      this.toGeneric();
   }

   public void toInformix() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toInformix();
      }

      if (this.tableClauseList != null) {
         int size = this.tableClauseList.size();

         for(int i = 0; i < size; ++i) {
            if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
               ((TableClause)this.tableClauseList.get(i)).toInformix();
               ((TableClause)this.tableClauseList.get(i)).setAlias("");
            }
         }
      }

      this.toGeneric();
   }

   public void toSnowflake() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toSnowflake();
      }

      if (this.tableClauseList != null) {
         int size = this.tableClauseList.size();

         for(int i = 0; i < size; ++i) {
            if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
               ((TableClause)this.tableClauseList.get(i)).toSnowflake();
               ((TableClause)this.tableClauseList.get(i)).setAlias("");
            }
         }
      }

      this.toGeneric();
   }

   public void toANSISQL() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toANSI();
      }

      if (this.tableClauseList != null) {
         int size = this.tableClauseList.size();

         for(int i = 0; i < size; ++i) {
            if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
               ((TableClause)this.tableClauseList.get(i)).toANSISQL();
               ((TableClause)this.tableClauseList.get(i)).setAlias("");
            }
         }
      }

      this.toGeneric();
   }

   public void toTeradata() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toTeradata();
      }

      if (this.tableClauseList != null) {
         int size = this.tableClauseList.size();

         for(int i = 0; i < size; ++i) {
            if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
               ((TableClause)this.tableClauseList.get(i)).toTeradata();
               ((TableClause)this.tableClauseList.get(i)).setAlias("");
            }
         }
      }

      this.toGeneric();
   }

   public void toTimesTen() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toTimesTen();
      }

      if (this.tableClauseList != null) {
         int size = this.tableClauseList.size();

         for(int i = 0; i < size; ++i) {
            if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
               ((TableClause)this.tableClauseList.get(i)).toTimesTen();
            }
         }
      }

      this.toGeneric();
   }

   public void toNetezza() throws ConvertException {
      if (this.subQuery != null) {
         this.subQuery = this.subQuery.toNetezza();
      }

      if (this.tableClauseList != null) {
         int size = this.tableClauseList.size();

         for(int i = 0; i < size; ++i) {
            if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
               ((TableClause)this.tableClauseList.get(i)).toNetezza();
               ((TableClause)this.tableClauseList.get(i)).setAlias("");
            }
         }
      }

      if (this.subQuery == null) {
         if (this.tableClauseList != null && this.tableClauseList.get(0) != null && this.tableClauseList.get(0) instanceof TableClause) {
            TableClause tableClause = (TableClause)this.tableClauseList.get(0);
            if (tableClause.getAlias() != null) {
               return;
            }
         }
      } else {
         FromClause fromClause = this.subQuery.getFromClause();
         Vector fromItemList = fromClause.getFromItemList();
         FromTable fromTable = (FromTable)fromItemList.elementAt(0);
         this.tableClauseList = new ArrayList();
         if (fromTable.getTableName() instanceof String) {
            String tableName = (String)fromTable.getTableName();
            this.tableClauseList.add(tableName);
         } else if (fromTable.getTableName() instanceof SelectQueryStatement) {
            SwisSQLUtils.swissqlMessageList.add("Netezza does not support subqueries in the UPDATE clause of UPDATE statements.");
            this.tableClauseList.add(fromTable.getTableName());
         } else {
            this.tableClauseList.add(fromTable.getTableName());
         }

         this.subQuery = null;
      }

      this.sampleClause = null;
      this.remoteTable = null;
      this.withClause = null;
      this.tblCollExp = null;
   }

   public String toString() {
      StringBuffer stringbuffer = new StringBuffer();
      if (this.tableClauseList != null) {
         int size = this.tableClauseList.size();

         for(int i = 0; i < size; ++i) {
            if (this.tableClauseList.get(i) instanceof TableClause) {
               ((TableClause)this.tableClauseList.get(i)).setObjectContext(this.context);
            }

            stringbuffer.append(this.tableClauseList.get(i).toString() + " ");
         }
      }

      if (this.subQuery != null) {
         stringbuffer.append("(" + this.subQuery.toString() + ")");
      }

      if (this.sampleClause != null) {
         stringbuffer.append(this.sampleClause.toString());
      }

      if (this.remoteTable != null) {
         stringbuffer.append(this.remoteTable.toString());
      }

      if (this.withClause != null) {
         this.withClause.setObjectContext(this.context);
         stringbuffer.append(this.withClause.toString());
      }

      if (this.tblCollExp != null) {
         stringbuffer.append(this.tblCollExp.toString());
      }

      return stringbuffer.toString();
   }
}
