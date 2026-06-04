package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.ArrayList;

public class CreateIndexClause {
   private String uniqueOrBitMapString;
   private String clusteredOrNonClustered;
   private String indexOrKey;
   private TableObject tableObject;
   private TableObject indexObject;
   private String on;
   private String openBraces;
   private ArrayList indexColumns;
   private String closedBrace;
   private String cluster;
   private String clusterName;
   private String with;
   private ArrayList padIndexArrayList;
   private String padIndexComma;
   private ArrayList physicalAttributes;
   private LocalOrGlobalPartitionTable localOrGlobalPartitionTable;
   private String onGoIdentifier;
   private String parallelOrNoParallel;
   private String parallelIdentifier;
   private String using;
   private String tree;
   private String removeIndent;
   private boolean isTenroxRequirement = false;
   private String objectName;
   private UserObjectContext context = null;
   private boolean isToOracle = false;
   private boolean isToSybase = false;
   private boolean isToTeradata = false;

   public void setObjectName(String name) {
      this.objectName = name;
   }

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public String getObjectName() {
      return this.objectName;
   }

   public void setUniqueOrBitMapString(String uniqueOrBitMapString) {
      this.uniqueOrBitMapString = uniqueOrBitMapString;
   }

   public void setClusteredOrNonClustered(String clusteredOrNonClustered) {
      this.clusteredOrNonClustered = clusteredOrNonClustered;
   }

   public void setIndexOrKey(String indexOrKey) {
      this.indexOrKey = indexOrKey;
   }

   public void setIndexName(TableObject indexObject) {
      this.indexObject = indexObject;
   }

   public void setOn(String on) {
      this.on = on;
   }

   public void setTableOrView(TableObject tableObject) {
      this.tableObject = tableObject;
   }

   public void setOpenBraces(String openBraces) {
      this.openBraces = openBraces;
   }

   public void setIndexColumns(ArrayList indexColumns) {
      this.indexColumns = indexColumns;
   }

   public void setClosedBraces(String closedBrace) {
      this.closedBrace = closedBrace;
   }

   public void setCluster(String cluster) {
      this.cluster = cluster;
   }

   public void setClusterName(String clusterName) {
      this.clusterName = clusterName;
   }

   public void setWith(String with) {
      this.with = with;
   }

   public void setToOracle(boolean isToOracle) {
      this.isToOracle = isToOracle;
   }

   public void setToSybase(boolean isToSybase) {
      this.isToSybase = isToSybase;
   }

   public void setToTeradata(boolean isToTeradata) {
      this.isToTeradata = isToTeradata;
   }

   public void setPadIndexArrayList(ArrayList padIndexArrayList) {
      this.padIndexArrayList = padIndexArrayList;
   }

   public void setPadIndexComma(String padIndexComma) {
      this.padIndexComma = padIndexComma;
   }

   public void setPhysicalAttributes(ArrayList physicalAttributes) {
      this.physicalAttributes = physicalAttributes;
   }

   public void setLocalOrGlobalPartitionTable(LocalOrGlobalPartitionTable localOrGlobalPartitionTable) {
      this.localOrGlobalPartitionTable = localOrGlobalPartitionTable;
   }

   public void setOnGoIdentifier(String onGoIdentifier) {
      this.onGoIdentifier = onGoIdentifier;
   }

   public void setParallelOrNoParallel(String parallelOrNoParallel) {
      this.parallelOrNoParallel = parallelOrNoParallel;
   }

   public void setParallelIdentifier(String parallelIdentifier) {
      this.parallelIdentifier = parallelIdentifier;
   }

   public void setUsing(String using) {
      this.using = using;
   }

   public void setTree(String tree) {
      this.tree = tree;
   }

   public String getUniqueOrBitMapString() {
      return this.uniqueOrBitMapString;
   }

   public String getClusteredOrNonClustered() {
      return this.clusteredOrNonClustered;
   }

   public String getIndexOrKey() {
      return this.indexOrKey;
   }

   public TableObject getIndexName() {
      return this.indexObject;
   }

   public String getOn() {
      return this.on;
   }

   public TableObject getTableOrView() {
      return this.tableObject;
   }

   public String getOpenBraces() {
      return this.openBraces;
   }

   public ArrayList getIndexColumns() {
      return this.indexColumns;
   }

   public String getClosedBraces() {
      return this.closedBrace;
   }

   public String getCluster() {
      return this.cluster;
   }

   public String getClusterName() {
      return this.clusterName;
   }

   public String getWith() {
      return this.with;
   }

   public ArrayList getPadIndexArrayList() {
      return this.padIndexArrayList;
   }

   public String getPadIndexComma() {
      return this.padIndexComma;
   }

   public LocalOrGlobalPartitionTable getLocalOrGlobalPartitionTable() {
      return this.localOrGlobalPartitionTable;
   }

   public ArrayList getPhysicalAttributes() {
      return this.physicalAttributes;
   }

   public String getOnGoIdentifier() {
      return this.onGoIdentifier;
   }

   public String getParallelOrNoParallel() {
      return this.parallelOrNoParallel;
   }

   public String getParallelIdentifier() {
      return this.parallelIdentifier;
   }

   public String getUsing() {
      return this.using;
   }

   public String getTree() {
      return this.tree;
   }

   public String removeIndent(String str) {
      str = str.replace('\n', ' ');
      str = str.replace('\t', ' ');
      return str;
   }

   public CreateIndexClause toANSI() throws ConvertException {
      CreateIndexClause cic = this.copyObjectValues();
      cic.setUniqueOrBitMapString((String)null);
      cic.setClusteredOrNonClustered((String)null);
      String tempIndexOrKey;
      if (cic.getIndexOrKey() != null) {
         tempIndexOrKey = cic.getIndexOrKey();
         if (tempIndexOrKey.equalsIgnoreCase("key")) {
            cic.setIndexOrKey("Index");
         }
      }

      String ownerName;
      String userName;
      String tableName;
      TableObject tempTableObject;
      if (cic.getIndexName() != null) {
         tempTableObject = cic.getIndexName();
         ownerName = tempTableObject.getOwner();
         userName = tempTableObject.getUser();
         tableName = tempTableObject.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempTableObject.setOwner(ownerName);
         tempTableObject.setUser(userName);
         tempTableObject.setTableName(tableName);
         tempTableObject.toANSISQL();
      }

      if (cic.getOn() != null) {
         tempIndexOrKey = cic.getOn();
      }

      if (cic.getTableOrView() != null) {
         tempTableObject = cic.getTableOrView();
         ownerName = tempTableObject.getOwner();
         userName = tempTableObject.getUser();
         tableName = tempTableObject.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempTableObject.setOwner(ownerName);
         tempTableObject.setUser(userName);
         tempTableObject.setTableName(tableName);
         tempTableObject.toANSISQL();
      }

      if (cic.getOpenBraces() != null) {
         tempIndexOrKey = cic.getOpenBraces();
      }

      if (cic.getIndexColumns() != null) {
         ArrayList ansiIndexColumnArrayList = new ArrayList();

         for(int i = 0; i < this.indexColumns.size(); ++i) {
            IndexColumn indexColumn = (IndexColumn)this.indexColumns.get(i);
            IndexColumn ansiIndexColumn = indexColumn.toANSI();
            ansiIndexColumnArrayList.add(ansiIndexColumn);
         }

         cic.setIndexColumns(ansiIndexColumnArrayList);
      }

      cic.setWith((String)null);
      cic.setPadIndexArrayList((ArrayList)null);
      cic.setPadIndexComma((String)null);
      if (cic.getClosedBraces() != null) {
         tempIndexOrKey = cic.getClosedBraces();
      }

      cic.setPhysicalAttributes((ArrayList)null);
      cic.setLocalOrGlobalPartitionTable((LocalOrGlobalPartitionTable)null);
      cic.setOnGoIdentifier((String)null);
      cic.setParallelOrNoParallel((String)null);
      cic.setParallelIdentifier((String)null);
      cic.setUsing((String)null);
      cic.setTree((String)null);
      return cic;
   }

   public CreateIndexClause toDB2() throws ConvertException {
      CreateIndexClause cic = this.copyObjectValues();
      String tempIndexOrKey;
      if (cic.getUniqueOrBitMapString() != null) {
         tempIndexOrKey = cic.getUniqueOrBitMapString();
         if (tempIndexOrKey.equalsIgnoreCase("CLUSTER") || tempIndexOrKey.equalsIgnoreCase("DISTINCT") || tempIndexOrKey.equalsIgnoreCase("BITMAP")) {
            cic.setUniqueOrBitMapString("UNIQUE");
         }
      }

      cic.setClusteredOrNonClustered((String)null);
      if (cic.getIndexOrKey() != null) {
         tempIndexOrKey = cic.getIndexOrKey();
         if (tempIndexOrKey.equalsIgnoreCase("key")) {
            cic.setIndexOrKey("Index");
         }
      }

      String table_name;
      String ownerName;
      String userName;
      TableObject orgTableObject;
      if (cic.getIndexName() != null) {
         orgTableObject = cic.getIndexName();
         table_name = orgTableObject.getOwner();
         ownerName = orgTableObject.getUser();
         userName = orgTableObject.getTableName();
         if (table_name != null && (table_name.startsWith("[") && table_name.endsWith("]") || table_name.startsWith("`") && table_name.endsWith("`"))) {
            table_name = table_name.substring(1, table_name.length() - 1);
            if (table_name.indexOf(32) != -1) {
               table_name = "\"" + table_name + "\"";
            }
         }

         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (SwisSQLAPI.truncateTableNameForDB2 && userName.length() > 18) {
            if (userName.indexOf("\"") != -1) {
               userName = userName.substring(0, 11) + "_ADV" + SwisSQLAPI.truncateIndexCount + "\"";
               ++SwisSQLAPI.truncateIndexCount;
            } else {
               userName = userName.substring(0, 12) + "_ADV" + SwisSQLAPI.truncateIndexCount;
               ++SwisSQLAPI.truncateIndexCount;
            }
         }

         orgTableObject.setOwner(table_name);
         orgTableObject.setUser(ownerName);
         orgTableObject.setTableName(userName);
         orgTableObject.toDB2();
      }

      if (cic.getOn() != null) {
         tempIndexOrKey = cic.getOn();
      }

      if (cic.getTableOrView() != null) {
         orgTableObject = cic.getTableOrView();
         table_name = orgTableObject.getTableName();
         ownerName = orgTableObject.getOwner();
         userName = orgTableObject.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (table_name != null && (table_name.startsWith("[") && table_name.endsWith("]") || table_name.startsWith("`") && table_name.endsWith("`"))) {
            table_name = table_name.substring(1, table_name.length() - 1);
            if (table_name.indexOf(32) != -1) {
               table_name = "\"" + table_name + "\"";
            }
         }

         orgTableObject.setOwner(ownerName);
         orgTableObject.setUser(userName);
         orgTableObject.setTableName(table_name);
         orgTableObject.toOracle();
      }

      if (cic.getOpenBraces() != null) {
         tempIndexOrKey = cic.getOpenBraces();
      }

      if (cic.getIndexColumns() != null) {
         ArrayList db2IndexColumnArrayList = new ArrayList();

         for(int i = 0; i < this.indexColumns.size(); ++i) {
            IndexColumn indexColumn = (IndexColumn)this.indexColumns.get(i);
            IndexColumn db2IndexColumn = indexColumn.toDB2();
            db2IndexColumnArrayList.add(db2IndexColumn);
         }

         cic.setIndexColumns(db2IndexColumnArrayList);
      }

      cic.setWith((String)null);
      cic.setPadIndexArrayList((ArrayList)null);
      cic.setPadIndexComma((String)null);
      if (cic.getClosedBraces() != null) {
         tempIndexOrKey = cic.getClosedBraces();
      }

      cic.setPhysicalAttributes((ArrayList)null);
      cic.setLocalOrGlobalPartitionTable((LocalOrGlobalPartitionTable)null);
      cic.setOnGoIdentifier((String)null);
      cic.setParallelOrNoParallel((String)null);
      cic.setParallelIdentifier((String)null);
      cic.setUsing((String)null);
      cic.setTree((String)null);
      return cic;
   }

   public CreateIndexClause toInformix() throws ConvertException {
      CreateIndexClause cic = this.copyObjectValues();
      String tempIndexOrKey;
      if (cic.getUniqueOrBitMapString() != null) {
         tempIndexOrKey = cic.getUniqueOrBitMapString();
         if (tempIndexOrKey.equalsIgnoreCase("BITMAP")) {
            cic.setUniqueOrBitMapString("UNIQUE");
         }
      }

      cic.setClusteredOrNonClustered((String)null);
      if (cic.getIndexOrKey() != null) {
         tempIndexOrKey = cic.getIndexOrKey();
         if (tempIndexOrKey.equalsIgnoreCase("key")) {
            cic.setIndexOrKey("Index");
         }
      }

      TableObject tempIndexObject;
      if (cic.getIndexName() != null) {
         tempIndexObject = cic.getIndexName();
         String ownerName = tempIndexObject.getOwner();
         String userName = tempIndexObject.getUser();
         String tableName = tempIndexObject.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempIndexObject.setOwner(ownerName);
         tempIndexObject.setUser(userName);
         tempIndexObject.setTableName(tableName);
         tempIndexObject.toInformix();
         cic.setIndexName(tempIndexObject);
      }

      if (cic.getOn() != null) {
         tempIndexOrKey = cic.getOn();
      }

      if (cic.getTableOrView() != null) {
         tempIndexObject = cic.getTableOrView();
         tempIndexObject.toInformix();
      }

      if (cic.getOpenBraces() != null) {
         tempIndexOrKey = cic.getOpenBraces();
      }

      if (cic.getIndexColumns() != null) {
         ArrayList informixIndexColumnArrayList = new ArrayList();

         for(int i = 0; i < this.indexColumns.size(); ++i) {
            IndexColumn indexColumn = (IndexColumn)this.indexColumns.get(i);
            IndexColumn informixIndexColumn = indexColumn.toInformix();
            informixIndexColumnArrayList.add(informixIndexColumn);
         }

         cic.setIndexColumns(informixIndexColumnArrayList);
      }

      cic.setWith((String)null);
      cic.setPadIndexArrayList((ArrayList)null);
      cic.setPadIndexComma((String)null);
      if (cic.getClosedBraces() != null) {
         tempIndexOrKey = cic.getClosedBraces();
      }

      cic.setPhysicalAttributes((ArrayList)null);
      cic.setLocalOrGlobalPartitionTable((LocalOrGlobalPartitionTable)null);
      cic.setOnGoIdentifier((String)null);
      cic.setParallelOrNoParallel((String)null);
      cic.setParallelIdentifier((String)null);
      cic.setUsing((String)null);
      cic.setTree((String)null);
      return cic;
   }

   public CreateIndexClause toMSSQLServer() throws ConvertException {
      CreateIndexClause cic = this.copyObjectValues();
      String tempTree;
      if (cic.getUniqueOrBitMapString() != null) {
         tempTree = cic.getUniqueOrBitMapString();
         if (tempTree.equalsIgnoreCase("CLUSTER") || tempTree.equalsIgnoreCase("DISTINCT") || tempTree.equalsIgnoreCase("BITMAP")) {
            cic.setUniqueOrBitMapString("UNIQUE");
         }
      }

      if (cic.getClusteredOrNonClustered() != null) {
         tempTree = cic.getClusteredOrNonClustered();
      }

      if (cic.getIndexOrKey() != null) {
         tempTree = cic.getIndexOrKey();
         if (tempTree.equalsIgnoreCase("key")) {
            cic.setIndexOrKey("Index");
         }
      }

      String ownerName;
      String userName;
      String tableName;
      TableObject tempTableObject;
      if (cic.getIndexName() != null) {
         tempTableObject = cic.getIndexName();
         ownerName = tempTableObject.getOwner();
         userName = tempTableObject.getUser();
         tableName = tempTableObject.getTableName();
         if (SwisSQLOptions.TRUNCATE_ORACLE_SCHEMA_INFORMATION) {
            userName = null;
            ownerName = null;
         }

         if (SwisSQLOptions.EnableDeltekSpecificConversions && userName != null && userName.trim().equalsIgnoreCase("DELTEK")) {
            userName = null;
         }

         if (ownerName != null && ownerName.startsWith("`") && ownerName.endsWith("`")) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && userName.startsWith("`") && userName.endsWith("`")) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && tableName.startsWith("`") && tableName.endsWith("`")) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempTableObject.setOwner(ownerName);
         tempTableObject.setUser(userName);
         tempTableObject.setTableName(tableName);
         tempTableObject.toMSSQLServer();
      }

      if (cic.getOn() != null) {
         tempTree = cic.getOn();
      }

      if (cic.getTableOrView() != null) {
         tempTableObject = cic.getTableOrView();
         ownerName = tempTableObject.getOwner();
         userName = tempTableObject.getUser();
         tableName = tempTableObject.getTableName();
         if (ownerName != null && ownerName.startsWith("`") && ownerName.endsWith("`")) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && userName.startsWith("`") && userName.endsWith("`")) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && tableName.startsWith("`") && tableName.endsWith("`")) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempTableObject.setOwner(ownerName);
         tempTableObject.setUser(userName);
         tempTableObject.setTableName(tableName);
         tempTableObject.toMSSQLServer();
      }

      if (cic.getOpenBraces() != null) {
         tempTree = cic.getOpenBraces();
      }

      ArrayList tempPhysicalAttributes;
      if (cic.getIndexColumns() != null) {
         tempPhysicalAttributes = new ArrayList();

         for(int i = 0; i < this.indexColumns.size(); ++i) {
            IndexColumn indexColumn = (IndexColumn)this.indexColumns.get(i);
            IndexColumn msSQLServerIndexColumn = indexColumn.toMSSQLServer();
            tempPhysicalAttributes.add(msSQLServerIndexColumn);
         }

         cic.setIndexColumns(tempPhysicalAttributes);
      }

      if (cic.getClosedBraces() != null) {
         tempTree = cic.getClosedBraces();
      }

      if (cic.getWith() != null) {
         tempTree = cic.getWith();
      }

      PhysicalAttributesClause msSQLServerPhysicalAttributesClause;
      ArrayList msSQLServerPhysicalAttributesArrayList;
      int i;
      if (cic.getPadIndexArrayList() != null) {
         tempPhysicalAttributes = new ArrayList();
         msSQLServerPhysicalAttributesArrayList = cic.getPadIndexArrayList();
         i = msSQLServerPhysicalAttributesArrayList.size();

         for(int i = 0; i < msSQLServerPhysicalAttributesArrayList.size(); ++i) {
            msSQLServerPhysicalAttributesClause = (PhysicalAttributesClause)msSQLServerPhysicalAttributesArrayList.get(i);
            PhysicalAttributesClause msSQLServerPhysicalAttributesClause = msSQLServerPhysicalAttributesClause.toMSSQLServer();
            tempPhysicalAttributes.add(msSQLServerPhysicalAttributesClause);
         }

         cic.setPadIndexArrayList(tempPhysicalAttributes);
      }

      if (cic.getPadIndexComma() != null) {
         tempTree = cic.getPadIndexComma();
      }

      if (cic.getOnGoIdentifier() != null) {
         tempTree = cic.getOnGoIdentifier();
      }

      if (cic.getPhysicalAttributes() != null) {
         tempPhysicalAttributes = cic.getPhysicalAttributes();
         msSQLServerPhysicalAttributesArrayList = new ArrayList();

         for(i = 0; i < tempPhysicalAttributes.size(); ++i) {
            PhysicalAttributesClause physicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributes.get(i);
            msSQLServerPhysicalAttributesClause = physicalAttributesClause.toMSSQLServer();
            if (!msSQLServerPhysicalAttributesClause.toString().equalsIgnoreCase("WITH ALLOW_DUP_ROW")) {
               msSQLServerPhysicalAttributesArrayList.add(msSQLServerPhysicalAttributesClause);
            }
         }

         cic.setPhysicalAttributes(msSQLServerPhysicalAttributesArrayList);
      }

      if (cic.getPhysicalAttributes() != null && cic.getPhysicalAttributes().size() != 0) {
         tempPhysicalAttributes = cic.getPhysicalAttributes();
         PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributes.get(0);
         PhysicalAttributesClause msSQLServerPhysicalAttributesClause = tempPhysicalAttributesClause.toMSSQLServer();
         if (msSQLServerPhysicalAttributesClause.getWith() == null && (msSQLServerPhysicalAttributesClause.getFillFactor() != null || msSQLServerPhysicalAttributesClause.getPadIndex() != null || msSQLServerPhysicalAttributesClause.getDiskAttr() != null && msSQLServerPhysicalAttributesClause.getDiskAttr().get("IGNORE_DUP_KEY") != null || msSQLServerPhysicalAttributesClause.getDropExisting() != null || msSQLServerPhysicalAttributesClause.getStatisticsNoreCompute() != null)) {
            cic.setWith("WITH");
         }

         if (msSQLServerPhysicalAttributesClause.getDiskAttr() != null && msSQLServerPhysicalAttributesClause.getDiskAttr().get("IGNORE_DUP_KEY") != null) {
            cic.setUniqueOrBitMapString("UNIQUE");
         }
      }

      cic.setLocalOrGlobalPartitionTable((LocalOrGlobalPartitionTable)null);
      cic.setParallelOrNoParallel((String)null);
      cic.setParallelIdentifier((String)null);
      cic.setUsing((String)null);
      if (cic.getTree() != null) {
         tempTree = cic.getTree();
         if (tempTree.equalsIgnoreCase("Btree")) {
            cic.setClusteredOrNonClustered("Clustered");
            cic.setTree((String)null);
         } else {
            cic.setTree((String)null);
         }
      }

      return cic;
   }

   public CreateIndexClause toSybase() throws ConvertException {
      CreateIndexClause cic = this.copyObjectValues();
      cic.setToSybase(true);
      String tempTree;
      if (cic.getUniqueOrBitMapString() != null) {
         tempTree = cic.getUniqueOrBitMapString();
         if (tempTree.equalsIgnoreCase("CLUSTER") || tempTree.equalsIgnoreCase("DISTINCT") || tempTree.equalsIgnoreCase("BITMAP")) {
            cic.setUniqueOrBitMapString("UNIQUE");
         }
      }

      if (cic.getClusteredOrNonClustered() != null) {
         tempTree = cic.getClusteredOrNonClustered();
      }

      if (cic.getIndexOrKey() != null) {
         tempTree = cic.getIndexOrKey();
         if (tempTree.equalsIgnoreCase("key")) {
            cic.setIndexOrKey("Index");
         }
      }

      TableObject tempIndexObject;
      if (cic.getIndexName() != null) {
         tempIndexObject = cic.getIndexName();
         String ownerName = tempIndexObject.getOwner();
         String userName = tempIndexObject.getUser();
         String tableName = tempIndexObject.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempIndexObject.setOwner(ownerName);
         tempIndexObject.setUser(userName);
         tempIndexObject.setTableName(tableName);
         tempIndexObject.toSybase();
         cic.setIndexName(tempIndexObject);
      }

      if (cic.getOn() != null) {
         tempTree = cic.getOn();
      }

      if (cic.getTableOrView() != null) {
         tempIndexObject = cic.getTableOrView();
         tempIndexObject.toSybase();
      }

      if (cic.getOpenBraces() != null) {
         tempTree = cic.getOpenBraces();
      }

      ArrayList tempPhysicalAttributes;
      if (cic.getIndexColumns() != null) {
         tempPhysicalAttributes = new ArrayList();

         for(int i = 0; i < this.indexColumns.size(); ++i) {
            IndexColumn indexColumn = (IndexColumn)this.indexColumns.get(i);
            IndexColumn sybaseIndexColumn = indexColumn.toSybase();
            tempPhysicalAttributes.add(sybaseIndexColumn);
         }

         cic.setIndexColumns(tempPhysicalAttributes);
      }

      if (cic.getClosedBraces() != null) {
         tempTree = cic.getClosedBraces();
      }

      if (cic.getWith() != null) {
         tempTree = cic.getWith();
      }

      PhysicalAttributesClause sybasePhysicalAttributesClause;
      ArrayList sybasePhysicalAttributesArrayList;
      int i;
      if (cic.getPadIndexArrayList() != null) {
         tempPhysicalAttributes = new ArrayList();
         sybasePhysicalAttributesArrayList = cic.getPadIndexArrayList();
         i = sybasePhysicalAttributesArrayList.size();

         for(int i = 0; i < sybasePhysicalAttributesArrayList.size(); ++i) {
            sybasePhysicalAttributesClause = (PhysicalAttributesClause)sybasePhysicalAttributesArrayList.get(i);
            PhysicalAttributesClause sybasePhysicalAttributesClause = sybasePhysicalAttributesClause.toSybase();
            tempPhysicalAttributes.add(sybasePhysicalAttributesClause);
         }

         cic.setPadIndexArrayList(tempPhysicalAttributes);
      }

      if (cic.getPadIndexComma() != null) {
         tempTree = cic.getPadIndexComma();
      }

      if (cic.getOnGoIdentifier() != null) {
         tempTree = cic.getOnGoIdentifier();
      }

      if (cic.getPhysicalAttributes() != null) {
         tempPhysicalAttributes = cic.getPhysicalAttributes();
         sybasePhysicalAttributesArrayList = new ArrayList();

         for(i = 0; i < tempPhysicalAttributes.size(); ++i) {
            PhysicalAttributesClause physicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributes.get(i);
            sybasePhysicalAttributesClause = physicalAttributesClause.toSybase();
            sybasePhysicalAttributesArrayList.add(sybasePhysicalAttributesClause);
         }

         cic.setPhysicalAttributes(sybasePhysicalAttributesArrayList);
      }

      if (cic.getPhysicalAttributes() != null && cic.getPhysicalAttributes().size() != 0) {
         tempPhysicalAttributes = cic.getPhysicalAttributes();
         PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)tempPhysicalAttributes.get(0);
         PhysicalAttributesClause sybasePhysicalAttributesClause = tempPhysicalAttributesClause.toSybase();
         if (sybasePhysicalAttributesClause.getWith() == null && (sybasePhysicalAttributesClause.getFillFactor() != null || sybasePhysicalAttributesClause.getPadIndex() != null || sybasePhysicalAttributesClause.getDiskAttr() != null && sybasePhysicalAttributesClause.getDiskAttr().get("IGNORE_DUP_KEY") != null || sybasePhysicalAttributesClause.getDropExisting() != null || sybasePhysicalAttributesClause.getStatisticsNoreCompute() != null)) {
            cic.setWith("WITH");
         }
      }

      cic.setLocalOrGlobalPartitionTable((LocalOrGlobalPartitionTable)null);
      cic.setParallelOrNoParallel((String)null);
      cic.setParallelIdentifier((String)null);
      cic.setUsing((String)null);
      if (cic.getTree() != null) {
         tempTree = cic.getTree();
         if (tempTree.equalsIgnoreCase("Btree")) {
            cic.setClusteredOrNonClustered("Clustered");
            cic.setTree((String)null);
         } else {
            cic.setTree((String)null);
         }
      }

      return cic;
   }

   public CreateIndexClause toMySQL() throws ConvertException {
      CreateIndexClause cic = this.copyObjectValues();
      String tempUniqueOrBitMapString;
      if (cic.getUniqueOrBitMapString() != null) {
         tempUniqueOrBitMapString = cic.getUniqueOrBitMapString();
         if (tempUniqueOrBitMapString.equalsIgnoreCase("CLUSTER") || tempUniqueOrBitMapString.equalsIgnoreCase("DISTINCT") || tempUniqueOrBitMapString.equalsIgnoreCase("BITMAP")) {
            cic.setUniqueOrBitMapString("UNIQUE");
         }
      }

      cic.setClusteredOrNonClustered((String)null);
      if (cic.getIndexOrKey() != null) {
         tempUniqueOrBitMapString = cic.getIndexOrKey();
      }

      String table_name;
      String ownerName;
      String userName;
      TableObject orgTableObject;
      if (cic.getIndexName() != null) {
         orgTableObject = cic.getIndexName();
         table_name = orgTableObject.getOwner();
         ownerName = orgTableObject.getUser();
         userName = orgTableObject.getTableName();
         if (table_name != null && (table_name.startsWith("[") && table_name.endsWith("]") || table_name.startsWith("`") && table_name.endsWith("`"))) {
            table_name = table_name.substring(1, table_name.length() - 1);
            if (table_name.indexOf(32) != -1) {
               table_name = "\"" + table_name + "\"";
            }
         }

         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         orgTableObject.setOwner(table_name);
         orgTableObject.setUser(ownerName);
         orgTableObject.setTableName(userName);
         String tableNameStr = orgTableObject.getTableName();
         orgTableObject.toMySQL();
         cic.setIndexName(orgTableObject);
      }

      if (cic.getOn() != null) {
         tempUniqueOrBitMapString = cic.getOn();
      }

      if (cic.getTableOrView() != null) {
         orgTableObject = cic.getTableOrView();
         table_name = orgTableObject.getTableName();
         ownerName = orgTableObject.getOwner();
         userName = orgTableObject.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (table_name != null && (table_name.startsWith("[") && table_name.endsWith("]") || table_name.startsWith("`") && table_name.endsWith("`"))) {
            table_name = table_name.substring(1, table_name.length() - 1);
            if (table_name.indexOf(32) != -1) {
               table_name = "\"" + table_name + "\"";
            }
         }

         orgTableObject.setOwner(ownerName);
         orgTableObject.setUser(userName);
         orgTableObject.setTableName(table_name);
         orgTableObject.toMySQL();
      }

      if (cic.getOpenBraces() != null) {
         tempUniqueOrBitMapString = cic.getOpenBraces();
      }

      if (cic.getIndexColumns() != null) {
         ArrayList mySQLIndexColumnArrayList = new ArrayList();

         for(int i = 0; i < this.indexColumns.size(); ++i) {
            IndexColumn indexColumn = (IndexColumn)this.indexColumns.get(i);
            IndexColumn mySQLIndexColumn = indexColumn.toMySQL();
            mySQLIndexColumnArrayList.add(mySQLIndexColumn);
         }

         cic.setIndexColumns(mySQLIndexColumnArrayList);
      }

      cic.setWith((String)null);
      cic.setPadIndexArrayList((ArrayList)null);
      cic.setPadIndexComma((String)null);
      if (cic.getClosedBraces() != null) {
         tempUniqueOrBitMapString = cic.getClosedBraces();
      }

      cic.setPhysicalAttributes((ArrayList)null);
      cic.setLocalOrGlobalPartitionTable((LocalOrGlobalPartitionTable)null);
      cic.setOnGoIdentifier((String)null);
      cic.setParallelOrNoParallel((String)null);
      cic.setParallelIdentifier((String)null);
      cic.setUsing((String)null);
      cic.setTree((String)null);
      return cic;
   }

   public CreateIndexClause toOracle() throws ConvertException {
      CreateIndexClause cic = this.copyObjectValues();
      String tempIndexOrKey;
      if (cic.getUniqueOrBitMapString() != null) {
         tempIndexOrKey = cic.getUniqueOrBitMapString();
         if (tempIndexOrKey.equalsIgnoreCase("CLUSTER") || tempIndexOrKey.equalsIgnoreCase("DISTINCT")) {
            cic.setUniqueOrBitMapString("UNIQUE");
         }
      }

      cic.setClusteredOrNonClustered((String)null);
      cic.setToOracle(true);
      if (cic.getIndexOrKey() != null) {
         tempIndexOrKey = cic.getIndexOrKey();
         if (tempIndexOrKey.equalsIgnoreCase("key")) {
            cic.setIndexOrKey("INDEX");
         } else {
            cic.setIndexOrKey(tempIndexOrKey);
         }
      }

      String table_name;
      String ownerName;
      String userName;
      TableObject orgTableObject;
      if (cic.getIndexName() != null) {
         orgTableObject = cic.getIndexName();
         table_name = orgTableObject.getOwner();
         ownerName = orgTableObject.getUser();
         userName = orgTableObject.getTableName();
         if (table_name != null && (table_name.startsWith("[") && table_name.endsWith("]") || table_name.startsWith("`") && table_name.endsWith("`"))) {
            table_name = table_name.substring(1, table_name.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || table_name.indexOf(32) != -1) {
               table_name = "\"" + table_name + "\"";
            }
         }

         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (userName.startsWith("#")) {
            userName = userName.substring(1);
         }

         orgTableObject.setOwner(table_name);
         orgTableObject.setUser(ownerName);
         orgTableObject.setTableName(userName);
         String tableNameStr = orgTableObject.getTableName();
         orgTableObject.toOracle();
         cic.setIndexName(orgTableObject);
      }

      if (cic.getOn() != null) {
         tempIndexOrKey = cic.getOn();
      }

      if (cic.getTableOrView() != null) {
         orgTableObject = cic.getTableOrView();
         table_name = orgTableObject.getTableName();
         ownerName = orgTableObject.getOwner();
         userName = orgTableObject.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (table_name != null && (table_name.startsWith("[") && table_name.endsWith("]") || table_name.startsWith("`") && table_name.endsWith("`"))) {
            table_name = table_name.substring(1, table_name.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || table_name.indexOf(32) != -1) {
               table_name = "\"" + table_name + "\"";
            }
         }

         orgTableObject.setOwner(ownerName);
         orgTableObject.setUser(userName);
         orgTableObject.setTableName(table_name);
         orgTableObject.toOracle();
      }

      if (cic.getOpenBraces() != null) {
         tempIndexOrKey = cic.getOpenBraces();
      }

      ArrayList oraclePadIndexArrayList;
      if (cic.getIndexColumns() != null) {
         oraclePadIndexArrayList = new ArrayList();

         for(int i = 0; i < this.indexColumns.size(); ++i) {
            IndexColumn indexColumn = (IndexColumn)this.indexColumns.get(i);
            IndexColumn oracleIndexColumn = indexColumn.toOracle();
            oraclePadIndexArrayList.add(oracleIndexColumn);
         }

         cic.setIndexColumns(oraclePadIndexArrayList);
      }

      if (cic.getClosedBraces() != null) {
         tempIndexOrKey = cic.getClosedBraces();
      }

      if (cic.getClusterName() != null) {
         tempIndexOrKey = cic.getClusterName();
      }

      ArrayList tempPadIndexArrayList;
      PhysicalAttributesClause oraclePhysicalAttributesClause;
      int i;
      PhysicalAttributesClause physicalAttributesClause;
      if (cic.getPhysicalAttributes() != null) {
         oraclePadIndexArrayList = cic.getPhysicalAttributes();
         tempPadIndexArrayList = new ArrayList();

         for(i = 0; i < oraclePadIndexArrayList.size(); ++i) {
            physicalAttributesClause = (PhysicalAttributesClause)oraclePadIndexArrayList.get(i);
            oraclePhysicalAttributesClause = physicalAttributesClause.toOracle();
            tempPadIndexArrayList.add(oraclePhysicalAttributesClause);
         }

         cic.setPhysicalAttributes(tempPadIndexArrayList);
      }

      if (cic.getPadIndexArrayList() != null) {
         oraclePadIndexArrayList = new ArrayList();
         tempPadIndexArrayList = cic.getPadIndexArrayList();

         for(i = 0; i < tempPadIndexArrayList.size(); ++i) {
            physicalAttributesClause = (PhysicalAttributesClause)tempPadIndexArrayList.get(i);
            oraclePhysicalAttributesClause = physicalAttributesClause.toOracle();
            oraclePadIndexArrayList.add(oraclePhysicalAttributesClause);
         }

         cic.setPadIndexArrayList(oraclePadIndexArrayList);
      }

      cic.setWith((String)null);
      cic.setPadIndexComma((String)null);
      if (cic.getParallelOrNoParallel() != null) {
         tempIndexOrKey = cic.getParallelOrNoParallel();
      }

      if (cic.getParallelIdentifier() != null) {
         tempIndexOrKey = cic.getParallelIdentifier();
      }

      if (cic.getLocalOrGlobalPartitionTable() != null) {
         LocalOrGlobalPartitionTable tempLocalOrGlobalPartitionTable = cic.getLocalOrGlobalPartitionTable();
         LocalOrGlobalPartitionTable oracleLocalOrGlobalPartitionTable = tempLocalOrGlobalPartitionTable.toOracle();
         cic.setLocalOrGlobalPartitionTable(oracleLocalOrGlobalPartitionTable);
      }

      cic.setOnGoIdentifier((String)null);
      cic.setUsing((String)null);
      cic.setTree((String)null);
      return cic;
   }

   public CreateIndexClause toBigQuery() throws ConvertException {
      CreateIndexClause cic = this.copyObjectValues();
      String tempIndexOrKey;
      if (cic.getUniqueOrBitMapString() != null) {
         tempIndexOrKey = cic.getUniqueOrBitMapString();
         if (tempIndexOrKey.equalsIgnoreCase("CLUSTER") || tempIndexOrKey.equalsIgnoreCase("DISTINCT") || tempIndexOrKey.equalsIgnoreCase("BITMAP")) {
            cic.setUniqueOrBitMapString("UNIQUE");
         }
      }

      if (cic.getClusteredOrNonClustered() != null) {
         tempIndexOrKey = cic.getClusteredOrNonClustered();
         if (tempIndexOrKey.equalsIgnoreCase("Clustered")) {
            cic.setTree("BTree");
         }

         cic.setUsing("Using");
         cic.setClusteredOrNonClustered((String)null);
      }

      if (cic.getIndexOrKey() != null) {
         tempIndexOrKey = cic.getIndexOrKey();
         if (tempIndexOrKey.equalsIgnoreCase("key")) {
            cic.setIndexOrKey("Index");
         }
      }

      String ownerName;
      String userName;
      String tableName;
      TableObject tempTableObject;
      if (cic.getIndexName() != null) {
         tempTableObject = cic.getIndexName();
         ownerName = tempTableObject.getOwner();
         userName = tempTableObject.getUser();
         tableName = tempTableObject.getTableName();
         if (ownerName != null && ownerName.startsWith("[") && ownerName.endsWith("]")) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && userName.startsWith("[") && userName.endsWith("]")) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && tableName.startsWith("[") && tableName.endsWith("]")) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempTableObject.setOwner(ownerName);
         tempTableObject.setUser(userName);
         tempTableObject.setTableName(tableName);
         tempTableObject.toBigQuery();
         cic.setIndexName(tempTableObject);
      }

      if (cic.getOn() != null) {
         tempIndexOrKey = cic.getOn();
      }

      if (cic.getTableOrView() != null) {
         tempTableObject = cic.getTableOrView();
         ownerName = tempTableObject.getOwner();
         userName = tempTableObject.getUser();
         tableName = tempTableObject.getTableName();
         if (ownerName != null && ownerName.startsWith("[") && ownerName.endsWith("]")) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && userName.startsWith("[") && userName.endsWith("]")) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && tableName.startsWith("[") && tableName.endsWith("]")) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempTableObject.setOwner(ownerName);
         tempTableObject.setUser(userName);
         tempTableObject.setTableName(tableName);
         tempTableObject.toBigQuery();
      }

      if (cic.getIndexColumns() != null) {
         ArrayList bigQueryIndexColumnArrayList = new ArrayList();

         for(int i = 0; i < this.indexColumns.size(); ++i) {
            IndexColumn indexColumn = (IndexColumn)this.indexColumns.get(i);
            IndexColumn bigQueryIndexColumn = indexColumn.toBigQuery();
            bigQueryIndexColumnArrayList.add(bigQueryIndexColumn);
         }

         cic.setIndexColumns(bigQueryIndexColumnArrayList);
      }

      if (cic.getUsing() != null) {
         tempIndexOrKey = cic.getUsing();
      }

      if (cic.getTree() != null) {
         tempIndexOrKey = cic.getTree();
      }

      if (cic.getOpenBraces() != null) {
         tempIndexOrKey = cic.getOpenBraces();
      }

      cic.setWith((String)null);
      cic.setPadIndexArrayList((ArrayList)null);
      cic.setPadIndexComma((String)null);
      if (cic.getClosedBraces() != null) {
         tempIndexOrKey = cic.getClosedBraces();
      }

      cic.setPhysicalAttributes((ArrayList)null);
      cic.setLocalOrGlobalPartitionTable((LocalOrGlobalPartitionTable)null);
      cic.setOnGoIdentifier((String)null);
      cic.setParallelOrNoParallel((String)null);
      cic.setParallelIdentifier((String)null);
      if (cic.getUsing() != null) {
         tempIndexOrKey = cic.getUsing();
      }

      if (cic.getTree() != null) {
         tempIndexOrKey = cic.getTree();
      }

      return cic;
   }

   public CreateIndexClause toPostgreSQL() throws ConvertException {
      CreateIndexClause cic = this.copyObjectValues();
      String tempIndexOrKey;
      if (cic.getUniqueOrBitMapString() != null) {
         tempIndexOrKey = cic.getUniqueOrBitMapString();
         if (tempIndexOrKey.equalsIgnoreCase("CLUSTER") || tempIndexOrKey.equalsIgnoreCase("DISTINCT") || tempIndexOrKey.equalsIgnoreCase("BITMAP")) {
            cic.setUniqueOrBitMapString("UNIQUE");
         }
      }

      if (cic.getClusteredOrNonClustered() != null) {
         tempIndexOrKey = cic.getClusteredOrNonClustered();
         if (tempIndexOrKey.equalsIgnoreCase("Clustered")) {
            cic.setTree("BTree");
         }

         cic.setUsing("Using");
         cic.setClusteredOrNonClustered((String)null);
      }

      if (cic.getIndexOrKey() != null) {
         tempIndexOrKey = cic.getIndexOrKey();
         if (tempIndexOrKey.equalsIgnoreCase("key")) {
            cic.setIndexOrKey("Index");
         }
      }

      String ownerName;
      String userName;
      String tableName;
      TableObject tempTableObject;
      if (cic.getIndexName() != null) {
         tempTableObject = cic.getIndexName();
         ownerName = tempTableObject.getOwner();
         userName = tempTableObject.getUser();
         tableName = tempTableObject.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempTableObject.setOwner(ownerName);
         tempTableObject.setUser(userName);
         tempTableObject.setTableName(tableName);
         tempTableObject.toPostgreSQL();
         cic.setIndexName(tempTableObject);
      }

      if (cic.getOn() != null) {
         tempIndexOrKey = cic.getOn();
      }

      if (cic.getTableOrView() != null) {
         tempTableObject = cic.getTableOrView();
         ownerName = tempTableObject.getOwner();
         userName = tempTableObject.getUser();
         tableName = tempTableObject.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempTableObject.setOwner(ownerName);
         tempTableObject.setUser(userName);
         tempTableObject.setTableName(tableName);
         tempTableObject.toPostgreSQL();
      }

      if (cic.getIndexColumns() != null) {
         ArrayList postgreSQLIndexColumnArrayList = new ArrayList();

         for(int i = 0; i < this.indexColumns.size(); ++i) {
            IndexColumn indexColumn = (IndexColumn)this.indexColumns.get(i);
            IndexColumn postgreSQLIndexColumn = indexColumn.toPostgreSQL();
            postgreSQLIndexColumnArrayList.add(postgreSQLIndexColumn);
         }

         cic.setIndexColumns(postgreSQLIndexColumnArrayList);
      }

      if (cic.getUsing() != null) {
         tempIndexOrKey = cic.getUsing();
      }

      if (cic.getTree() != null) {
         tempIndexOrKey = cic.getTree();
      }

      if (cic.getOpenBraces() != null) {
         tempIndexOrKey = cic.getOpenBraces();
      }

      cic.setWith((String)null);
      cic.setPadIndexArrayList((ArrayList)null);
      cic.setPadIndexComma((String)null);
      if (cic.getClosedBraces() != null) {
         tempIndexOrKey = cic.getClosedBraces();
      }

      cic.setPhysicalAttributes((ArrayList)null);
      cic.setLocalOrGlobalPartitionTable((LocalOrGlobalPartitionTable)null);
      cic.setOnGoIdentifier((String)null);
      cic.setParallelOrNoParallel((String)null);
      cic.setParallelIdentifier((String)null);
      if (cic.getUsing() != null) {
         tempIndexOrKey = cic.getUsing();
      }

      if (cic.getTree() != null) {
         tempIndexOrKey = cic.getTree();
      }

      return cic;
   }

   public CreateIndexClause toTimesTen() throws ConvertException {
      CreateIndexClause cic = this.copyObjectValues();
      if (cic.getUniqueOrBitMapString() != null) {
         if (cic.getUniqueOrBitMapString().equalsIgnoreCase("unique")) {
            cic.setUniqueOrBitMapString("UNIQUE");
         } else {
            cic.setUniqueOrBitMapString((String)null);
         }
      }

      if (cic.getClusteredOrNonClustered() != null) {
         cic.setClusteredOrNonClustered((String)null);
      }

      if (cic.getIndexOrKey() != null) {
         String tempIndexOrKey = cic.getIndexOrKey();
         if (tempIndexOrKey.equalsIgnoreCase("key")) {
            cic.setIndexOrKey("INDEX");
         }
      }

      TableObject tempIndexObject;
      if (cic.getIndexName() != null) {
         tempIndexObject = cic.getIndexName();
         String ownerName = tempIndexObject.getOwner();
         String tableName = tempIndexObject.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempIndexObject.setOwner(ownerName);
         tempIndexObject.setTableName(tableName);
         tempIndexObject.toTimesTen();
         cic.setIndexName(tempIndexObject);
      }

      if (cic.getTableOrView() != null) {
         tempIndexObject = cic.getTableOrView();
         tempIndexObject.toTimesTen();
      }

      if (cic.getIndexColumns() != null) {
         ArrayList indexColumnArrayList = new ArrayList();

         for(int i = 0; i < this.indexColumns.size(); ++i) {
            IndexColumn indexColumn = (IndexColumn)this.indexColumns.get(i);
            IndexColumn timesTenIndexColumn = indexColumn.toTimesTen();
            indexColumnArrayList.add(timesTenIndexColumn);
         }

         cic.setIndexColumns(indexColumnArrayList);
      }

      if (cic.getWith() != null) {
         cic.setWith((String)null);
      }

      if (cic.getPadIndexArrayList() != null) {
         cic.setPadIndexArrayList((ArrayList)null);
      }

      if (cic.getPhysicalAttributes() != null) {
         cic.setPhysicalAttributes((ArrayList)null);
      }

      cic.setLocalOrGlobalPartitionTable((LocalOrGlobalPartitionTable)null);
      cic.setParallelOrNoParallel((String)null);
      cic.setParallelIdentifier((String)null);
      cic.setUsing((String)null);
      cic.setOnGoIdentifier((String)null);
      if (cic.getTree() != null) {
         cic.setTree((String)null);
      }

      return cic;
   }

   public CreateIndexClause toNetezza() throws ConvertException {
      CreateIndexClause cic = this.copyObjectValues();
      cic.setUniqueOrBitMapString((String)null);
      cic.setClusteredOrNonClustered((String)null);
      String tempIndexOrKey;
      if (cic.getIndexOrKey() != null) {
         tempIndexOrKey = cic.getIndexOrKey();
         if (tempIndexOrKey.equalsIgnoreCase("key")) {
            cic.setIndexOrKey("Index");
         }
      }

      String ownerName;
      String userName;
      String tableName;
      TableObject tempTableObject;
      if (cic.getIndexName() != null) {
         tempTableObject = cic.getIndexName();
         ownerName = tempTableObject.getOwner();
         userName = tempTableObject.getUser();
         tableName = tempTableObject.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempTableObject.setOwner(ownerName);
         tempTableObject.setUser(userName);
         tempTableObject.setTableName(tableName);
         tempTableObject.toNetezza();
      }

      if (cic.getOn() != null) {
         tempIndexOrKey = cic.getOn();
      }

      if (cic.getTableOrView() != null) {
         tempTableObject = cic.getTableOrView();
         ownerName = tempTableObject.getOwner();
         userName = tempTableObject.getUser();
         tableName = tempTableObject.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempTableObject.setOwner(ownerName);
         tempTableObject.setUser(userName);
         tempTableObject.setTableName(tableName);
         tempTableObject.toNetezza();
      }

      if (cic.getOpenBraces() != null) {
         tempIndexOrKey = cic.getOpenBraces();
      }

      if (cic.getIndexColumns() != null) {
         ArrayList netezzaIndexColumnArrayList = new ArrayList();

         for(int i = 0; i < this.indexColumns.size(); ++i) {
            IndexColumn indexColumn = (IndexColumn)this.indexColumns.get(i);
            IndexColumn netezzaIndexColumn = indexColumn.toNetezza();
            netezzaIndexColumnArrayList.add(netezzaIndexColumn);
         }

         cic.setIndexColumns(netezzaIndexColumnArrayList);
      }

      cic.setWith((String)null);
      cic.setPadIndexArrayList((ArrayList)null);
      cic.setPadIndexComma((String)null);
      if (cic.getClosedBraces() != null) {
         tempIndexOrKey = cic.getClosedBraces();
      }

      cic.setPhysicalAttributes((ArrayList)null);
      cic.setLocalOrGlobalPartitionTable((LocalOrGlobalPartitionTable)null);
      cic.setOnGoIdentifier((String)null);
      cic.setParallelOrNoParallel((String)null);
      cic.setParallelIdentifier((String)null);
      cic.setUsing((String)null);
      cic.setTree((String)null);
      return cic;
   }

   public CreateIndexClause toSnowflake() throws ConvertException {
      CreateIndexClause cic = this.copyObjectValues();
      String tempIndexOrKey;
      if (cic.getUniqueOrBitMapString() != null) {
         tempIndexOrKey = cic.getUniqueOrBitMapString();
         if (tempIndexOrKey.equalsIgnoreCase("CLUSTER") || tempIndexOrKey.equalsIgnoreCase("DISTINCT") || tempIndexOrKey.equalsIgnoreCase("BITMAP")) {
            cic.setUniqueOrBitMapString("UNIQUE");
         }
      }

      if (cic.getClusteredOrNonClustered() != null) {
         tempIndexOrKey = cic.getClusteredOrNonClustered();
         if (tempIndexOrKey.equalsIgnoreCase("Clustered")) {
            cic.setTree("BTree");
         }

         cic.setUsing("Using");
         cic.setClusteredOrNonClustered((String)null);
      }

      if (cic.getIndexOrKey() != null) {
         tempIndexOrKey = cic.getIndexOrKey();
         if (tempIndexOrKey.equalsIgnoreCase("key")) {
            cic.setIndexOrKey("Index");
         }
      }

      String ownerName;
      String userName;
      String tableName;
      TableObject tempTableObject;
      if (cic.getIndexName() != null) {
         tempTableObject = cic.getIndexName();
         ownerName = tempTableObject.getOwner();
         userName = tempTableObject.getUser();
         tableName = tempTableObject.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempTableObject.setOwner(ownerName);
         tempTableObject.setUser(userName);
         tempTableObject.setTableName(tableName);
         tempTableObject.toSnowflake();
         cic.setIndexName(tempTableObject);
      }

      if (cic.getOn() != null) {
         tempIndexOrKey = cic.getOn();
      }

      if (cic.getTableOrView() != null) {
         tempTableObject = cic.getTableOrView();
         ownerName = tempTableObject.getOwner();
         userName = tempTableObject.getUser();
         tableName = tempTableObject.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempTableObject.setOwner(ownerName);
         tempTableObject.setUser(userName);
         tempTableObject.setTableName(tableName);
         tempTableObject.toSnowflake();
      }

      if (cic.getIndexColumns() != null) {
         ArrayList snowflakeIndexColumnArrayList = new ArrayList();

         for(int i = 0; i < this.indexColumns.size(); ++i) {
            IndexColumn indexColumn = (IndexColumn)this.indexColumns.get(i);
            IndexColumn snowflakeIndexColumn = indexColumn.toSnowflake();
            snowflakeIndexColumnArrayList.add(snowflakeIndexColumn);
         }

         cic.setIndexColumns(snowflakeIndexColumnArrayList);
      }

      if (cic.getUsing() != null) {
         tempIndexOrKey = cic.getUsing();
      }

      if (cic.getTree() != null) {
         tempIndexOrKey = cic.getTree();
      }

      if (cic.getOpenBraces() != null) {
         tempIndexOrKey = cic.getOpenBraces();
      }

      cic.setWith((String)null);
      cic.setPadIndexArrayList((ArrayList)null);
      cic.setPadIndexComma((String)null);
      if (cic.getClosedBraces() != null) {
         tempIndexOrKey = cic.getClosedBraces();
      }

      cic.setPhysicalAttributes((ArrayList)null);
      cic.setLocalOrGlobalPartitionTable((LocalOrGlobalPartitionTable)null);
      cic.setOnGoIdentifier((String)null);
      cic.setParallelOrNoParallel((String)null);
      cic.setParallelIdentifier((String)null);
      if (cic.getUsing() != null) {
         tempIndexOrKey = cic.getUsing();
      }

      if (cic.getTree() != null) {
         tempIndexOrKey = cic.getTree();
      }

      return cic;
   }

   public CreateIndexClause toTeradata() throws ConvertException {
      CreateIndexClause cic = this.copyObjectValues();
      cic.setToTeradata(true);
      cic.setUniqueOrBitMapString((String)null);
      cic.setClusteredOrNonClustered((String)null);
      String tempIndexOrKey;
      if (cic.getIndexOrKey() != null) {
         tempIndexOrKey = cic.getIndexOrKey();
         if (tempIndexOrKey.equalsIgnoreCase("key")) {
            cic.setIndexOrKey("Index");
         }
      }

      String ownerName;
      String userName;
      String tableName;
      TableObject tempTableObject;
      if (cic.getIndexName() != null) {
         tempTableObject = cic.getIndexName();
         ownerName = tempTableObject.getOwner();
         userName = tempTableObject.getUser();
         tableName = tempTableObject.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempTableObject.setOwner(ownerName);
         tempTableObject.setUser(userName);
         tempTableObject.setTableName(tableName);
         tempTableObject.toTeradata();
      }

      if (cic.getOn() != null) {
         tempIndexOrKey = cic.getOn();
      }

      if (cic.getTableOrView() != null) {
         tempTableObject = cic.getTableOrView();
         ownerName = tempTableObject.getOwner();
         userName = tempTableObject.getUser();
         tableName = tempTableObject.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tempTableObject.setOwner(ownerName);
         tempTableObject.setUser(userName);
         tempTableObject.setTableName(tableName);
         tempTableObject.toTeradata();
      }

      if (cic.getOpenBraces() != null) {
         tempIndexOrKey = cic.getOpenBraces();
      }

      if (cic.getIndexColumns() != null) {
         ArrayList TeradataIndexColumnArrayList = new ArrayList();

         for(int i = 0; i < this.indexColumns.size(); ++i) {
            IndexColumn indexColumn = (IndexColumn)this.indexColumns.get(i);
            IndexColumn TeradataIndexColumn = indexColumn.toTeradata();
            TeradataIndexColumnArrayList.add(TeradataIndexColumn);
         }

         cic.setIndexColumns(TeradataIndexColumnArrayList);
      }

      cic.setWith((String)null);
      cic.setPadIndexArrayList((ArrayList)null);
      cic.setPadIndexComma((String)null);
      if (cic.getClosedBraces() != null) {
         tempIndexOrKey = cic.getClosedBraces();
      }

      cic.setPhysicalAttributes((ArrayList)null);
      cic.setLocalOrGlobalPartitionTable((LocalOrGlobalPartitionTable)null);
      cic.setOnGoIdentifier((String)null);
      cic.setParallelOrNoParallel((String)null);
      cic.setParallelIdentifier((String)null);
      cic.setUsing((String)null);
      cic.setTree((String)null);
      return cic;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.uniqueOrBitMapString != null) {
         sb.append(this.uniqueOrBitMapString.toUpperCase() + " ");
      }

      if (this.clusteredOrNonClustered != null) {
         sb.append(this.clusteredOrNonClustered.toUpperCase() + " ");
      }

      if (this.indexOrKey != null) {
         sb.append(this.indexOrKey.toUpperCase() + " ");
      }

      if (this.indexObject != null) {
         this.indexObject.setObjectContext(this.context);
         if (this.context == null) {
            String tableNameStr = this.indexObject.getTableName();
            if (this.isToOracle) {
               if (tableNameStr.length() > 30) {
                  tableNameStr = tableNameStr.substring(0, 26) + SwisSQLAPI.truncateIndexCount;
                  if (tableNameStr.startsWith("\"") && !tableNameStr.endsWith("\"")) {
                     tableNameStr = tableNameStr + "\"";
                  }

                  this.indexObject.setTableName(tableNameStr);
                  SwisSQLUtils.setObjectNameForMapping(this.indexObject.getOrigTableName(), tableNameStr);
                  ++SwisSQLAPI.truncateIndexCount;
               }
            } else if (this.isToSybase && tableNameStr.length() > 30) {
               tableNameStr = tableNameStr.substring(0, 27) + SwisSQLAPI.truncateIndexCount;
               this.indexObject.setTableName(tableNameStr);
               ++SwisSQLAPI.truncateIndexCount;
            }
         }

         sb.append(this.indexObject + " ");
      }

      if (this.isToTeradata && this.openBraces != null) {
         sb.append(this.openBraces.toUpperCase() + " ");
      }

      int i;
      IndexColumn indexColumn;
      int i;
      if (this.isToTeradata && this.indexColumns != null) {
         i = this.indexColumns.size();

         for(i = 0; i < this.indexColumns.size(); ++i) {
            indexColumn = (IndexColumn)this.indexColumns.get(i);
            indexColumn.setObjectContext(this.context);
            sb.append(indexColumn.toString());
            if (i < i - 1) {
               sb.append(",");
            }
         }
      }

      if (this.isToTeradata && this.closedBrace != null) {
         sb.append(this.closedBrace + "\n");
      }

      if (this.on != null) {
         sb.append(this.on.toUpperCase());
      }

      if (this.tableObject != null) {
         this.tableObject.setObjectContext(this.context);
         sb.append("\n" + this.tableObject + "  ");
      }

      if (this.using != null) {
         sb.append(this.using.toUpperCase() + " ");
      }

      if (this.tree != null) {
         sb.append(this.tree.toUpperCase() + " ");
      }

      if (this.openBraces != null && !this.isToTeradata) {
         sb.append(this.openBraces.toUpperCase() + " ");
      }

      if (this.cluster != null) {
         sb.append("\n" + this.cluster.toUpperCase() + " ");
      }

      if (this.clusterName != null) {
         sb.append(this.clusterName + "\n");
      }

      if (this.indexColumns != null && !this.isToTeradata) {
         i = this.indexColumns.size();

         for(i = 0; i < this.indexColumns.size(); ++i) {
            indexColumn = (IndexColumn)this.indexColumns.get(i);
            indexColumn.setObjectContext(this.context);
            sb.append(indexColumn.toString());
            if (i < i - 1) {
               sb.append(",");
            }
         }
      }

      if (this.closedBrace != null && !this.isToTeradata) {
         sb.append(this.closedBrace + "\n");
      }

      if (this.with != null) {
         sb.append("\t" + this.with.toUpperCase() + "  ");
      }

      PhysicalAttributesClause tempPhysicalAttributesClause;
      if (this.padIndexArrayList != null) {
         for(i = 0; i < this.padIndexArrayList.size(); ++i) {
            i = this.padIndexArrayList.size();
            tempPhysicalAttributesClause = (PhysicalAttributesClause)this.padIndexArrayList.get(i);
            sb.append(tempPhysicalAttributesClause.toString());
            if (i < i - 1 && this.padIndexComma != null) {
               sb.append(this.padIndexComma + " ");
            }
         }

         sb.append("\n");
      }

      if (this.physicalAttributes != null) {
         sb.append("\t");
         boolean comma = false;

         for(i = 0; i < this.physicalAttributes.size(); ++i) {
            tempPhysicalAttributesClause = (PhysicalAttributesClause)this.physicalAttributes.get(i);
            if (tempPhysicalAttributesClause.getWith() != null) {
               comma = true;
            }

            if (comma && i != this.physicalAttributes.size() - 1) {
               sb.append(tempPhysicalAttributesClause.toString() + ", ");
            } else {
               sb.append(tempPhysicalAttributesClause.toString() + " ");
            }
         }

         sb.append("\n");
      }

      if (this.localOrGlobalPartitionTable != null) {
         sb.append(this.localOrGlobalPartitionTable.toString() + " ");
      }

      if (this.onGoIdentifier != null) {
         sb.append(this.onGoIdentifier.toUpperCase() + " ");
      }

      if (this.parallelOrNoParallel != null) {
         sb.append(this.parallelOrNoParallel.toUpperCase() + " ");
      }

      if (this.parallelIdentifier != null) {
         sb.append(this.parallelIdentifier + " ");
      }

      return sb.toString();
   }

   public CreateIndexClause copyObjectValues() {
      CreateIndexClause dupCreateIndexClause = new CreateIndexClause();
      dupCreateIndexClause.setUniqueOrBitMapString(this.uniqueOrBitMapString);
      dupCreateIndexClause.setClusteredOrNonClustered(this.clusteredOrNonClustered);
      dupCreateIndexClause.setIndexOrKey(this.indexOrKey);
      dupCreateIndexClause.setIndexName(this.indexObject);
      dupCreateIndexClause.setOn(this.on);
      dupCreateIndexClause.setTableOrView(this.tableObject);
      dupCreateIndexClause.setOpenBraces(this.openBraces);
      dupCreateIndexClause.setIndexColumns(this.indexColumns);
      dupCreateIndexClause.setClosedBraces(this.closedBrace);
      dupCreateIndexClause.setCluster(this.cluster);
      dupCreateIndexClause.setClusterName(this.clusterName);
      dupCreateIndexClause.setWith(this.with);
      dupCreateIndexClause.setPadIndexArrayList(this.padIndexArrayList);
      dupCreateIndexClause.setPadIndexComma(this.padIndexComma);
      dupCreateIndexClause.setPhysicalAttributes(this.physicalAttributes);
      dupCreateIndexClause.setOnGoIdentifier(this.onGoIdentifier);
      dupCreateIndexClause.setParallelOrNoParallel(this.parallelOrNoParallel);
      dupCreateIndexClause.setParallelIdentifier(this.parallelIdentifier);
      dupCreateIndexClause.setUsing(this.using);
      dupCreateIndexClause.setTree(this.tree);
      dupCreateIndexClause.setObjectContext(this.context);
      dupCreateIndexClause.setObjectName(this.objectName);
      dupCreateIndexClause.setLocalOrGlobalPartitionTable(this.localOrGlobalPartitionTable);
      dupCreateIndexClause.setToOracle(false);
      dupCreateIndexClause.setToSybase(false);
      if (this.indexOrKey != null) {
         String var2 = dupCreateIndexClause.getIndexOrKey();
      }

      if (this.indexColumns != null) {
         ArrayList var3 = dupCreateIndexClause.getIndexColumns();
      }

      return dupCreateIndexClause;
   }
}
