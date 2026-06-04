package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.ArrayList;

public class PartitionListAttributes {
   private String partition;
   private String partitionName;
   private String partitionString;
   private ArrayList valuesList;
   private ArrayList physicalAttributesPartitionArrayList;

   public void setPartition(String partition) {
      this.partition = partition;
   }

   public void setPartitionName(String partitionName) {
      this.partitionName = partitionName;
   }

   public void setPartitionTableString(String partitionString) {
      this.partitionString = partitionString;
   }

   public void setValuesList(ArrayList valuesList) {
      this.valuesList = valuesList;
   }

   public void setPartitionPhysicalAttributes(ArrayList physicalAttributesPartitionArrayList) {
      this.physicalAttributesPartitionArrayList = physicalAttributesPartitionArrayList;
   }

   public String getPartition() {
      return this.partition;
   }

   public String getPartitionName() {
      return this.partitionName;
   }

   public String getPartitionTableString() {
      return this.partitionString;
   }

   public ArrayList getValuesList() {
      return this.valuesList;
   }

   public ArrayList getPartitionPhysicalAttributes() {
      return this.physicalAttributesPartitionArrayList;
   }

   public PartitionListAttributes toANSI() throws ConvertException {
      PartitionListAttributes part = this.copyObjectValues();
      part.setPartition((String)null);
      part.setPartitionName((String)null);
      part.setPartitionTableString((String)null);
      part.setValuesList((ArrayList)null);
      part.setPartitionPhysicalAttributes((ArrayList)null);
      return part;
   }

   public PartitionListAttributes toDB2() throws ConvertException {
      PartitionListAttributes part = this.copyObjectValues();
      part.setPartition((String)null);
      part.setPartitionName((String)null);
      part.setPartitionTableString((String)null);
      part.setValuesList((ArrayList)null);
      part.setPartitionPhysicalAttributes((ArrayList)null);
      return part;
   }

   public PartitionListAttributes toInformix() throws ConvertException {
      PartitionListAttributes part = this.copyObjectValues();
      part.setPartition((String)null);
      part.setPartitionName((String)null);
      part.setPartitionTableString((String)null);
      part.setValuesList((ArrayList)null);
      part.setPartitionPhysicalAttributes((ArrayList)null);
      return part;
   }

   public PartitionListAttributes toMSSQLServer() throws ConvertException {
      PartitionListAttributes part = this.copyObjectValues();
      part.setPartition((String)null);
      part.setPartitionName((String)null);
      part.setPartitionTableString((String)null);
      part.setValuesList((ArrayList)null);
      part.setPartitionPhysicalAttributes((ArrayList)null);
      return part;
   }

   public PartitionListAttributes toSybase() throws ConvertException {
      PartitionListAttributes part = this.copyObjectValues();
      part.setPartition((String)null);
      part.setPartitionName((String)null);
      part.setPartitionTableString((String)null);
      part.setValuesList((ArrayList)null);
      part.setPartitionPhysicalAttributes((ArrayList)null);
      return part;
   }

   public PartitionListAttributes toMySQL() throws ConvertException {
      PartitionListAttributes part = this.copyObjectValues();
      part.setPartition((String)null);
      part.setPartitionName((String)null);
      part.setPartitionTableString((String)null);
      part.setValuesList((ArrayList)null);
      part.setPartitionPhysicalAttributes((ArrayList)null);
      return part;
   }

   public PartitionListAttributes toOracle() throws ConvertException {
      PartitionListAttributes part = this.copyObjectValues();
      String var2;
      if (part.getPartition() != null) {
         var2 = part.getPartition();
      }

      if (part.getPartitionName() != null) {
         var2 = part.getPartitionName();
      }

      if (part.getPartitionTableString() != null) {
         var2 = part.getPartitionTableString();
      }

      ArrayList oraclePhysicalAttributesArrayList;
      int i;
      ArrayList tempPhysicalAttributesPartitionArrayList;
      if (part.getValuesList() != null) {
         tempPhysicalAttributesPartitionArrayList = new ArrayList();
         oraclePhysicalAttributesArrayList = part.getValuesList();

         for(i = 0; i < oraclePhysicalAttributesArrayList.size(); ++i) {
            SelectColumn selectColumn = (SelectColumn)oraclePhysicalAttributesArrayList.get(i);
            SelectColumn tempSelectColumn = selectColumn.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            tempPhysicalAttributesPartitionArrayList.add(tempSelectColumn);
         }

         part.setValuesList(tempPhysicalAttributesPartitionArrayList);
      }

      if (part.getPartitionPhysicalAttributes() != null) {
         tempPhysicalAttributesPartitionArrayList = part.getPartitionPhysicalAttributes();
         oraclePhysicalAttributesArrayList = new ArrayList();

         for(i = 0; i < tempPhysicalAttributesPartitionArrayList.size(); ++i) {
            PhysicalAttributesClause physicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributesPartitionArrayList.get(i);
            PhysicalAttributesClause oraclePhysicalAttributesClause = physicalAttributesClause.toOracle();
            oraclePhysicalAttributesArrayList.add(oraclePhysicalAttributesClause);
         }

         part.setPartitionPhysicalAttributes(oraclePhysicalAttributesArrayList);
      }

      return part;
   }

   public PartitionListAttributes toPostgreSQL() throws ConvertException {
      PartitionListAttributes part = this.copyObjectValues();
      part.setPartition((String)null);
      part.setPartitionName((String)null);
      part.setPartitionTableString((String)null);
      part.setValuesList((ArrayList)null);
      part.setPartitionPhysicalAttributes((ArrayList)null);
      return part;
   }

   public String removeIndent(String str) {
      str = str.replace('\n', ' ');
      str = str.replace('\t', ' ');
      return str;
   }

   public PartitionListAttributes copyObjectValues() {
      PartitionListAttributes dupPartitionListAttributes = new PartitionListAttributes();
      dupPartitionListAttributes.setPartition(this.partition);
      dupPartitionListAttributes.setPartitionName(this.partitionName);
      dupPartitionListAttributes.setPartitionTableString(this.partitionString);
      dupPartitionListAttributes.setValuesList(this.valuesList);
      dupPartitionListAttributes.setPartitionPhysicalAttributes(this.physicalAttributesPartitionArrayList);
      if (this.partitionString != null) {
         String var2 = dupPartitionListAttributes.getPartitionTableString();
      }

      if (this.physicalAttributesPartitionArrayList != null) {
         ArrayList var3 = dupPartitionListAttributes.getPartitionPhysicalAttributes();
      }

      return dupPartitionListAttributes;
   }

   public PartitionListAttributes toNetezza() throws ConvertException {
      PartitionListAttributes part = this.copyObjectValues();
      part.setPartition((String)null);
      part.setPartitionName((String)null);
      part.setPartitionTableString((String)null);
      part.setValuesList((ArrayList)null);
      part.setPartitionPhysicalAttributes((ArrayList)null);
      return part;
   }

   public PartitionListAttributes toTeradata() throws ConvertException {
      PartitionListAttributes part = this.copyObjectValues();
      part.setPartition((String)null);
      part.setPartitionName((String)null);
      part.setPartitionTableString((String)null);
      part.setValuesList((ArrayList)null);
      part.setPartitionPhysicalAttributes((ArrayList)null);
      return part;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.partition != null) {
         sb.append(this.partition.toUpperCase() + " ");
      }

      if (this.partitionName != null) {
         sb.append(this.partitionName + " ");
      }

      if (this.partitionString != null) {
         sb.append(this.partitionString.toUpperCase() + " ");
      }

      int i;
      if (this.valuesList != null) {
         i = this.valuesList.size();

         for(int i = 0; i < this.valuesList.size(); ++i) {
            SelectColumn selectColumn = (SelectColumn)this.valuesList.get(i);
            sb.append(selectColumn);
            if (i < i - 1) {
               sb.append(" ,");
            }
         }

         sb.append(")");
      }

      if (this.physicalAttributesPartitionArrayList != null) {
         sb.append(" ");

         for(i = 0; i < this.physicalAttributesPartitionArrayList.size(); ++i) {
            PhysicalAttributesClause physicalAttributesClause = (PhysicalAttributesClause)this.physicalAttributesPartitionArrayList.get(i);
            sb.append(physicalAttributesClause.toString() + " ");
         }
      }

      return sb.toString();
   }
}
