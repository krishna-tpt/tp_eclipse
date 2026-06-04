package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.ArrayList;

public class LocalOrGlobalPartitionTable {
   private boolean createTableLocalOrGlobalPartition;
   private String globalPartitionSyntax;
   private ArrayList columnList;
   private ArrayList partitionArrayList;
   private String localPartitionSyntax;
   private String localPartitionName;
   private ArrayList localPhysicalAttributesArrayList;

   public void setCreateTableLocalOrGlobalPartition(boolean createTableLocalOrGlobalPartition) {
      this.createTableLocalOrGlobalPartition = createTableLocalOrGlobalPartition;
   }

   public void setGlobalPartitionSyntax(String globalPartitionSyntax) {
      this.globalPartitionSyntax = globalPartitionSyntax;
   }

   public void setColumnList(ArrayList columnList) {
      this.columnList = columnList;
   }

   public void setGlobalPartitionArrayList(ArrayList partitionArrayList) {
      this.partitionArrayList = partitionArrayList;
   }

   public void setLocalPartitionSyntax(String localPartitionSyntax) {
      this.localPartitionSyntax = localPartitionSyntax;
   }

   public void setLocalPartitionName(String localPartitionName) {
      this.localPartitionName = localPartitionName;
   }

   public void setLocalPartitionArrayList(ArrayList localPhysicalAttributesArrayList) {
      this.localPhysicalAttributesArrayList = localPhysicalAttributesArrayList;
   }

   public boolean getCreateTableLocalOrGlobalPartition() {
      return this.createTableLocalOrGlobalPartition;
   }

   public String getGlobalPartitionSyntax() {
      return this.globalPartitionSyntax;
   }

   public ArrayList getColumnList() {
      return this.columnList;
   }

   public ArrayList getGlobalPartitionArrayList() {
      return this.partitionArrayList;
   }

   public String getLocalPartitionSyntax() {
      return this.localPartitionSyntax;
   }

   public String getLocalPartitionName() {
      return this.localPartitionName;
   }

   public ArrayList getLocalPartitionArrayList() {
      return this.localPhysicalAttributesArrayList;
   }

   public LocalOrGlobalPartitionTable toANSI() throws ConvertException {
      LocalOrGlobalPartitionTable partition = this.copyObjectValues();
      partition.setGlobalPartitionSyntax((String)null);
      partition.setCreateTableLocalOrGlobalPartition(false);
      partition.setColumnList((ArrayList)null);
      partition.setLocalPartitionName((String)null);
      partition.setGlobalPartitionArrayList((ArrayList)null);
      partition.setLocalPartitionSyntax((String)null);
      partition.setLocalPartitionArrayList((ArrayList)null);
      return partition;
   }

   public LocalOrGlobalPartitionTable toDB2() throws ConvertException {
      LocalOrGlobalPartitionTable partition = this.copyObjectValues();
      partition.setGlobalPartitionSyntax((String)null);
      partition.setCreateTableLocalOrGlobalPartition(false);
      partition.setColumnList((ArrayList)null);
      partition.setLocalPartitionName((String)null);
      partition.setGlobalPartitionArrayList((ArrayList)null);
      partition.setLocalPartitionSyntax((String)null);
      partition.setLocalPartitionArrayList((ArrayList)null);
      return partition;
   }

   public LocalOrGlobalPartitionTable toInformix() throws ConvertException {
      LocalOrGlobalPartitionTable partition = this.copyObjectValues();
      partition.setGlobalPartitionSyntax((String)null);
      partition.setCreateTableLocalOrGlobalPartition(false);
      partition.setColumnList((ArrayList)null);
      partition.setLocalPartitionName((String)null);
      partition.setGlobalPartitionArrayList((ArrayList)null);
      partition.setLocalPartitionSyntax((String)null);
      partition.setLocalPartitionArrayList((ArrayList)null);
      return partition;
   }

   public LocalOrGlobalPartitionTable toMSSQLServer() throws ConvertException {
      LocalOrGlobalPartitionTable partition = this.copyObjectValues();
      partition.setGlobalPartitionSyntax((String)null);
      partition.setCreateTableLocalOrGlobalPartition(false);
      partition.setColumnList((ArrayList)null);
      partition.setLocalPartitionName((String)null);
      partition.setGlobalPartitionArrayList((ArrayList)null);
      partition.setLocalPartitionSyntax((String)null);
      partition.setLocalPartitionArrayList((ArrayList)null);
      return partition;
   }

   public LocalOrGlobalPartitionTable toSybase() throws ConvertException {
      LocalOrGlobalPartitionTable partition = this.copyObjectValues();
      partition.setGlobalPartitionSyntax((String)null);
      partition.setCreateTableLocalOrGlobalPartition(false);
      partition.setColumnList((ArrayList)null);
      partition.setLocalPartitionName((String)null);
      partition.setGlobalPartitionArrayList((ArrayList)null);
      partition.setLocalPartitionSyntax((String)null);
      partition.setLocalPartitionArrayList((ArrayList)null);
      return partition;
   }

   public LocalOrGlobalPartitionTable toMySQL() throws ConvertException {
      LocalOrGlobalPartitionTable partition = this.copyObjectValues();
      partition.setGlobalPartitionSyntax((String)null);
      partition.setCreateTableLocalOrGlobalPartition(false);
      partition.setColumnList((ArrayList)null);
      partition.setLocalPartitionName((String)null);
      partition.setGlobalPartitionArrayList((ArrayList)null);
      partition.setLocalPartitionSyntax((String)null);
      partition.setLocalPartitionArrayList((ArrayList)null);
      return partition;
   }

   public LocalOrGlobalPartitionTable toOracle() throws ConvertException {
      LocalOrGlobalPartitionTable partition = this.copyObjectValues();
      if (partition.getCreateTableLocalOrGlobalPartition()) {
         boolean var2 = partition.getCreateTableLocalOrGlobalPartition();
      }

      String tempLocalPartitionName;
      if (partition.getGlobalPartitionSyntax() != null) {
         tempLocalPartitionName = partition.getGlobalPartitionSyntax();
      }

      ArrayList oracleLocalPhysicalAttributesArrayList;
      int i;
      ArrayList tempLocalPhysicalAttributesArrayList;
      if (partition.getGlobalPartitionArrayList() != null) {
         tempLocalPhysicalAttributesArrayList = partition.getGlobalPartitionArrayList();
         oracleLocalPhysicalAttributesArrayList = new ArrayList();

         for(i = 0; i < tempLocalPhysicalAttributesArrayList.size(); ++i) {
            PartitionListAttributes partitionListAttributes = (PartitionListAttributes)tempLocalPhysicalAttributesArrayList.get(i);
            PartitionListAttributes oraclePartitionListAttributesClause = partitionListAttributes.toOracle();
            oracleLocalPhysicalAttributesArrayList.add(oraclePartitionListAttributesClause);
         }

         partition.setGlobalPartitionArrayList(oracleLocalPhysicalAttributesArrayList);
      }

      if (partition.getColumnList() != null) {
         tempLocalPhysicalAttributesArrayList = new ArrayList();
         oracleLocalPhysicalAttributesArrayList = partition.getColumnList();

         for(i = 0; i < oracleLocalPhysicalAttributesArrayList.size(); ++i) {
            SelectColumn selectColumn = (SelectColumn)oracleLocalPhysicalAttributesArrayList.get(i);
            SelectColumn tempSelectColumn = selectColumn.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            tempLocalPhysicalAttributesArrayList.add(tempSelectColumn);
         }

         partition.setColumnList(tempLocalPhysicalAttributesArrayList);
      }

      if (partition.getLocalPartitionName() != null) {
         tempLocalPartitionName = partition.getLocalPartitionName();
         partition.setLocalPartitionName(tempLocalPartitionName);
      }

      if (partition.getLocalPartitionSyntax() != null) {
         tempLocalPartitionName = partition.getLocalPartitionSyntax();
      }

      if (partition.getLocalPartitionArrayList() != null) {
         tempLocalPhysicalAttributesArrayList = partition.getLocalPartitionArrayList();
         oracleLocalPhysicalAttributesArrayList = new ArrayList();

         for(i = 0; i < tempLocalPhysicalAttributesArrayList.size(); ++i) {
            PhysicalAttributesClause physicalAttributesClause = (PhysicalAttributesClause)tempLocalPhysicalAttributesArrayList.get(i);
            PhysicalAttributesClause oraclePhysicalAttributesClause = physicalAttributesClause.toOracle();
            oracleLocalPhysicalAttributesArrayList.add(oraclePhysicalAttributesClause);
         }

         partition.setLocalPartitionArrayList(oracleLocalPhysicalAttributesArrayList);
      }

      return partition;
   }

   public LocalOrGlobalPartitionTable toPostgreSQL() throws ConvertException {
      LocalOrGlobalPartitionTable partition = this.copyObjectValues();
      partition.setGlobalPartitionSyntax((String)null);
      partition.setCreateTableLocalOrGlobalPartition(false);
      partition.setColumnList((ArrayList)null);
      partition.setLocalPartitionName((String)null);
      partition.setGlobalPartitionArrayList((ArrayList)null);
      partition.setLocalPartitionSyntax((String)null);
      partition.setLocalPartitionArrayList((ArrayList)null);
      return partition;
   }

   public LocalOrGlobalPartitionTable toNetezza() throws ConvertException {
      LocalOrGlobalPartitionTable partition = this.copyObjectValues();
      partition.setGlobalPartitionSyntax((String)null);
      partition.setCreateTableLocalOrGlobalPartition(false);
      partition.setColumnList((ArrayList)null);
      partition.setLocalPartitionName((String)null);
      partition.setGlobalPartitionArrayList((ArrayList)null);
      partition.setLocalPartitionSyntax((String)null);
      partition.setLocalPartitionArrayList((ArrayList)null);
      return partition;
   }

   public LocalOrGlobalPartitionTable toTeradata() throws ConvertException {
      LocalOrGlobalPartitionTable partition = this.copyObjectValues();
      partition.setGlobalPartitionSyntax((String)null);
      partition.setCreateTableLocalOrGlobalPartition(false);
      partition.setColumnList((ArrayList)null);
      partition.setLocalPartitionName((String)null);
      partition.setGlobalPartitionArrayList((ArrayList)null);
      partition.setLocalPartitionSyntax((String)null);
      partition.setLocalPartitionArrayList((ArrayList)null);
      return partition;
   }

   public String removeIndent(String str) {
      str = str.replace('\n', ' ');
      str = str.replace('\t', ' ');
      return str;
   }

   public LocalOrGlobalPartitionTable copyObjectValues() {
      LocalOrGlobalPartitionTable dupLocalOrGlobalPartitionTable = new LocalOrGlobalPartitionTable();
      dupLocalOrGlobalPartitionTable.setGlobalPartitionSyntax(this.globalPartitionSyntax);
      dupLocalOrGlobalPartitionTable.setCreateTableLocalOrGlobalPartition(this.createTableLocalOrGlobalPartition);
      dupLocalOrGlobalPartitionTable.setColumnList(this.columnList);
      dupLocalOrGlobalPartitionTable.setGlobalPartitionArrayList(this.partitionArrayList);
      dupLocalOrGlobalPartitionTable.setLocalPartitionSyntax(this.localPartitionSyntax);
      dupLocalOrGlobalPartitionTable.setLocalPartitionName(this.localPartitionName);
      dupLocalOrGlobalPartitionTable.setLocalPartitionArrayList(this.localPhysicalAttributesArrayList);
      String var2;
      if (this.globalPartitionSyntax != null) {
         var2 = dupLocalOrGlobalPartitionTable.getGlobalPartitionSyntax();
      }

      ArrayList var3;
      if (this.partitionArrayList != null) {
         var3 = dupLocalOrGlobalPartitionTable.getGlobalPartitionArrayList();
      }

      if (this.localPartitionSyntax != null) {
         var2 = dupLocalOrGlobalPartitionTable.getLocalPartitionSyntax();
      }

      if (this.localPhysicalAttributesArrayList != null) {
         var3 = dupLocalOrGlobalPartitionTable.getLocalPartitionArrayList();
      }

      return dupLocalOrGlobalPartitionTable;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.createTableLocalOrGlobalPartition) {
         sb.append("\t");
      }

      if (this.globalPartitionSyntax != null) {
         sb.append(this.globalPartitionSyntax.toUpperCase());
      }

      int i;
      int i;
      if (this.columnList != null) {
         sb.append("( ");
         i = this.columnList.size();

         for(i = 0; i < this.columnList.size(); ++i) {
            SelectColumn selectColumn = (SelectColumn)this.columnList.get(i);
            sb.append(selectColumn);
            if (i < i - 1) {
               sb.append(",");
            }
         }

         sb.append(")  \n");
      }

      if (this.partitionArrayList != null) {
         if (this.createTableLocalOrGlobalPartition) {
            sb.append("\t");
         }

         sb.append("(\n");
         i = this.partitionArrayList.size();

         for(i = 0; i < this.partitionArrayList.size(); ++i) {
            PartitionListAttributes partitionListAttributes = (PartitionListAttributes)this.partitionArrayList.get(i);
            if (this.createTableLocalOrGlobalPartition) {
               sb.append("\t");
            }

            sb.append("\t" + partitionListAttributes.toString() + " ");
            if (i < i - 1) {
               sb.append(",\n");
            }
         }

         if (this.createTableLocalOrGlobalPartition) {
            sb.append("\n\t ) \n");
         } else {
            sb.append("\n)\n");
         }
      }

      if (this.localPartitionSyntax != null) {
         if (this.createTableLocalOrGlobalPartition) {
            sb.append("\t");
         }

         sb.append(this.localPartitionSyntax.toUpperCase() + " ");
      }

      if (this.localPartitionName != null) {
         sb.append(this.localPartitionName + "\n\t");
      }

      if (this.localPhysicalAttributesArrayList != null) {
         for(i = 0; i < this.localPhysicalAttributesArrayList.size(); ++i) {
            PhysicalAttributesClause physicalAttributesClause = (PhysicalAttributesClause)this.localPhysicalAttributesArrayList.get(i);
            if (this.createTableLocalOrGlobalPartition) {
               sb.append("\t");
            }

            sb.append(physicalAttributesClause.toString() + " ");
         }

         sb.append("\n");
      }

      return sb.toString();
   }
}
