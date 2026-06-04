package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PhysicalAttributesClause {
   private String pctfree;
   private String pctfreeValue;
   private String pctused;
   private String pctUsedValue;
   private String initrans;
   private String maxtrans;
   private String storageClause;
   private String online;
   private String loggingOrNoLogging;
   private String compute;
   private String tableSpaceOrDefault;
   private String tableSpaceName;
   private String compressOrNoCompress;
   private String noSortOrReverse;
   private String padIndex;
   private String fillfactor;
   private String fillfactorValue;
   private String dropExisting;
   private String statisticsNoreCompute;
   private String sortInTempDb;
   private int fillFactorValueOfPctused;
   private String noCache;
   private String with;
   private HashMap diskAttr;
   private String overflow;

   public void setPctFree(String pctfree) {
      this.pctfree = pctfree;
   }

   public void setPctFreeValue(String pctfreeValue) {
      this.pctfreeValue = pctfreeValue;
   }

   public void setPctUsed(String pctused) {
      this.pctused = pctused;
   }

   public void setPctUsedValue(String pctUsedValue) {
      this.pctUsedValue = pctUsedValue;
   }

   public void setIniTrans(String initrans) {
      this.initrans = initrans;
   }

   public void setMaxTrans(String maxtrans) {
      this.maxtrans = maxtrans;
   }

   public void setStorageClause(String storageClause) {
      this.storageClause = storageClause;
   }

   public void setLoggingOrNoLogging(String loggingOrNoLogging) {
      this.loggingOrNoLogging = loggingOrNoLogging;
   }

   public void setOnline(String online) {
      this.online = online;
   }

   public void setCompute(String compute) {
      this.compute = compute;
   }

   public void setTableSpaceOrDefault(String tableSpaceOrDefault) {
      this.tableSpaceOrDefault = tableSpaceOrDefault;
   }

   public void setTableSpaceName(String tableSpaceName) {
      this.tableSpaceName = tableSpaceName;
   }

   public void setCompressOrNoCompress(String compressOrNoCompress) {
      this.compressOrNoCompress = compressOrNoCompress;
   }

   public void setNoSortOrReverse(String noSortOrReverse) {
      this.noSortOrReverse = noSortOrReverse;
   }

   public void setPadIndex(String padIndex) {
      this.padIndex = padIndex;
   }

   public void setFillFactor(String fillfactor) {
      this.fillfactor = fillfactor;
   }

   public void setFillFactorValue(String fillfactorValue) {
      this.fillfactorValue = fillfactorValue;
   }

   public void setDropExisting(String dropExisting) {
      this.dropExisting = dropExisting;
   }

   public void setStatisticsNoreCompute(String statisticsNoreCompute) {
      this.statisticsNoreCompute = statisticsNoreCompute;
   }

   public void setSortInTempDb(String sortInTempDb) {
      this.sortInTempDb = sortInTempDb;
   }

   public void setNoCache(String noCache) {
      this.noCache = noCache;
   }

   public void setWith(String with) {
      this.with = with;
   }

   public void setDiskAttr(HashMap diskAttr) {
      this.diskAttr = diskAttr;
   }

   public void setOverflow(String overflow) {
      this.overflow = overflow;
   }

   public String getPctFree() {
      return this.pctfree;
   }

   public String getPctFreeValue() {
      return this.pctfreeValue;
   }

   public String getPctUsed() {
      return this.pctused;
   }

   public String getPctUsedValue() {
      return this.pctUsedValue;
   }

   public String getIniTrans() {
      return this.initrans;
   }

   public String getMaxTrans() {
      return this.maxtrans;
   }

   public String getStorageClause() {
      return this.storageClause;
   }

   public String getLoggingOrNoLogging() {
      return this.loggingOrNoLogging;
   }

   public String getOnline() {
      return this.online;
   }

   public String getCompute() {
      return this.compute;
   }

   public String getTableSpaceOrDefault() {
      return this.tableSpaceOrDefault;
   }

   public String getTableSpaceName() {
      return this.tableSpaceName;
   }

   public String getCompressOrNoCompress() {
      return this.compressOrNoCompress;
   }

   public String getNoSortOrReverse() {
      return this.noSortOrReverse;
   }

   public String getPadIndex() {
      return this.padIndex;
   }

   public String getFillFactor() {
      return this.fillfactor;
   }

   public String getFillFactorValue() {
      return this.fillfactorValue;
   }

   public String getDropExisting() {
      return this.dropExisting;
   }

   public String getStatisticsNoreCompute() {
      return this.statisticsNoreCompute;
   }

   public String getSortInTempDb() {
      return this.sortInTempDb;
   }

   public String getNoCache() {
      return this.noCache;
   }

   public String getWith() {
      return this.with;
   }

   public HashMap getDiskAttr() {
      return this.diskAttr;
   }

   public String getOverflow() {
      return this.overflow;
   }

   public PhysicalAttributesClause toANSI() throws ConvertException {
      PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
      physicalAttributes.setPctFree((String)null);
      physicalAttributes.setPctFreeValue((String)null);
      physicalAttributes.setPctUsed((String)null);
      physicalAttributes.setPctUsedValue((String)null);
      physicalAttributes.setIniTrans((String)null);
      physicalAttributes.setMaxTrans((String)null);
      physicalAttributes.setStorageClause((String)null);
      physicalAttributes.setNoCache((String)null);
      physicalAttributes.setOnline((String)null);
      physicalAttributes.setLoggingOrNoLogging((String)null);
      physicalAttributes.setCompute((String)null);
      physicalAttributes.setTableSpaceOrDefault((String)null);
      physicalAttributes.setTableSpaceName((String)null);
      physicalAttributes.setCompressOrNoCompress((String)null);
      physicalAttributes.setNoSortOrReverse((String)null);
      physicalAttributes.setPadIndex((String)null);
      physicalAttributes.setFillFactor((String)null);
      physicalAttributes.setFillFactorValue((String)null);
      physicalAttributes.setDropExisting((String)null);
      physicalAttributes.setStatisticsNoreCompute((String)null);
      physicalAttributes.setSortInTempDb((String)null);
      physicalAttributes.setWith((String)null);
      physicalAttributes.setDiskAttr((HashMap)null);
      return physicalAttributes;
   }

   public PhysicalAttributesClause toTeradata() throws ConvertException {
      PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
      physicalAttributes.setPctFree((String)null);
      physicalAttributes.setPctFreeValue((String)null);
      physicalAttributes.setPctUsed((String)null);
      physicalAttributes.setPctUsedValue((String)null);
      physicalAttributes.setIniTrans((String)null);
      physicalAttributes.setMaxTrans((String)null);
      physicalAttributes.setStorageClause((String)null);
      physicalAttributes.setNoCache((String)null);
      physicalAttributes.setOnline((String)null);
      physicalAttributes.setLoggingOrNoLogging((String)null);
      physicalAttributes.setCompute((String)null);
      physicalAttributes.setTableSpaceOrDefault((String)null);
      physicalAttributes.setTableSpaceName((String)null);
      physicalAttributes.setCompressOrNoCompress((String)null);
      physicalAttributes.setNoSortOrReverse((String)null);
      physicalAttributes.setPadIndex((String)null);
      physicalAttributes.setFillFactor((String)null);
      physicalAttributes.setFillFactorValue((String)null);
      physicalAttributes.setDropExisting((String)null);
      physicalAttributes.setStatisticsNoreCompute((String)null);
      physicalAttributes.setSortInTempDb((String)null);
      physicalAttributes.setWith((String)null);
      physicalAttributes.setDiskAttr((HashMap)null);
      return physicalAttributes;
   }

   public PhysicalAttributesClause toDB2() throws ConvertException {
      PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
      physicalAttributes.setPctFree((String)null);
      physicalAttributes.setPctFreeValue((String)null);
      physicalAttributes.setPctUsed((String)null);
      physicalAttributes.setPctUsedValue((String)null);
      physicalAttributes.setIniTrans((String)null);
      physicalAttributes.setMaxTrans((String)null);
      physicalAttributes.setStorageClause((String)null);
      physicalAttributes.setNoCache((String)null);
      physicalAttributes.setOnline((String)null);
      physicalAttributes.setLoggingOrNoLogging((String)null);
      physicalAttributes.setCompute((String)null);
      physicalAttributes.setTableSpaceOrDefault((String)null);
      physicalAttributes.setTableSpaceName((String)null);
      physicalAttributes.setCompressOrNoCompress((String)null);
      physicalAttributes.setNoSortOrReverse((String)null);
      physicalAttributes.setPadIndex((String)null);
      physicalAttributes.setFillFactor((String)null);
      physicalAttributes.setFillFactorValue((String)null);
      physicalAttributes.setDropExisting((String)null);
      physicalAttributes.setWith((String)null);
      physicalAttributes.setDiskAttr((HashMap)null);
      physicalAttributes.setStatisticsNoreCompute((String)null);
      physicalAttributes.setSortInTempDb((String)null);
      return physicalAttributes;
   }

   public PhysicalAttributesClause toInformix() throws ConvertException {
      PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
      physicalAttributes.setPctFree((String)null);
      physicalAttributes.setPctFreeValue((String)null);
      physicalAttributes.setPctUsed((String)null);
      physicalAttributes.setPctUsedValue((String)null);
      physicalAttributes.setIniTrans((String)null);
      physicalAttributes.setMaxTrans((String)null);
      physicalAttributes.setStorageClause((String)null);
      physicalAttributes.setNoCache((String)null);
      physicalAttributes.setOnline((String)null);
      physicalAttributes.setLoggingOrNoLogging((String)null);
      physicalAttributes.setCompute((String)null);
      physicalAttributes.setTableSpaceOrDefault((String)null);
      physicalAttributes.setTableSpaceName((String)null);
      physicalAttributes.setCompressOrNoCompress((String)null);
      physicalAttributes.setNoSortOrReverse((String)null);
      physicalAttributes.setPadIndex((String)null);
      physicalAttributes.setFillFactor((String)null);
      physicalAttributes.setFillFactorValue((String)null);
      physicalAttributes.setDropExisting((String)null);
      physicalAttributes.setWith((String)null);
      physicalAttributes.setDiskAttr((HashMap)null);
      physicalAttributes.setStatisticsNoreCompute((String)null);
      physicalAttributes.setSortInTempDb((String)null);
      return physicalAttributes;
   }

   public PhysicalAttributesClause toMSSQLServer() throws ConvertException {
      PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
      String tempPctUsedValue;
      if (physicalAttributes.getPctFree() != null) {
         tempPctUsedValue = physicalAttributes.getPctFree();
         physicalAttributes.setFillFactor("FILLFACTOR = ");
         physicalAttributes.setPctFree((String)null);
      }

      if (physicalAttributes.getPctFreeValue() != null) {
         tempPctUsedValue = physicalAttributes.getPctFreeValue();
         physicalAttributes.setFillFactorValue(tempPctUsedValue);
         physicalAttributes.setPctFreeValue((String)null);
      }

      if (physicalAttributes.getPctUsed() != null) {
         tempPctUsedValue = physicalAttributes.getPctFreeValue();
         physicalAttributes.setFillFactor("FILLFACTOR = ");
         physicalAttributes.setPctUsed((String)null);
      }

      if (physicalAttributes.getPctUsedValue() != null) {
         tempPctUsedValue = physicalAttributes.getPctUsedValue();
         int fillIntValue = Integer.parseInt(tempPctUsedValue);
         this.fillFactorValueOfPctused = 100 - fillIntValue;
         tempPctUsedValue = "" + this.fillFactorValueOfPctused;
         physicalAttributes.setFillFactorValue(tempPctUsedValue);
         physicalAttributes.setPctUsedValue((String)null);
      }

      physicalAttributes.setIniTrans((String)null);
      physicalAttributes.setMaxTrans((String)null);
      physicalAttributes.setStorageClause((String)null);
      physicalAttributes.setNoCache((String)null);
      physicalAttributes.setOnline((String)null);
      physicalAttributes.setLoggingOrNoLogging((String)null);
      physicalAttributes.setCompute((String)null);
      physicalAttributes.setTableSpaceOrDefault((String)null);
      physicalAttributes.setTableSpaceName((String)null);
      physicalAttributes.setCompressOrNoCompress((String)null);
      physicalAttributes.setNoSortOrReverse((String)null);
      if (physicalAttributes.getPadIndex() != null) {
         tempPctUsedValue = physicalAttributes.getPadIndex();
      }

      if (physicalAttributes.getFillFactor() != null) {
         tempPctUsedValue = physicalAttributes.getFillFactor();
      }

      if (physicalAttributes.getFillFactorValue() != null) {
         tempPctUsedValue = physicalAttributes.getFillFactorValue();
      }

      if (physicalAttributes.getDropExisting() != null) {
         tempPctUsedValue = physicalAttributes.getDropExisting();
      }

      if (physicalAttributes.getStatisticsNoreCompute() != null) {
         tempPctUsedValue = physicalAttributes.getStatisticsNoreCompute();
      }

      if (physicalAttributes.getSortInTempDb() != null) {
         tempPctUsedValue = physicalAttributes.getSortInTempDb();
      }

      if (physicalAttributes.getDiskAttr() != null) {
         HashMap diskAttr = physicalAttributes.getDiskAttr();
         String ignore_row = "IGNORE_DUP_ROW";
         if (diskAttr.containsKey(ignore_row)) {
            diskAttr.remove(ignore_row);
            diskAttr.put("IGNORE_DUP_KEY", "");
         } else if (diskAttr.containsKey(ignore_row.toLowerCase())) {
            diskAttr.remove(ignore_row.toLowerCase());
            diskAttr.put("IGNORE_DUP_KEY", "");
         }
      }

      return physicalAttributes;
   }

   public PhysicalAttributesClause toSybase() throws ConvertException {
      PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
      String tempPctUsedValue;
      if (physicalAttributes.getPctFree() != null) {
         tempPctUsedValue = physicalAttributes.getPctFree();
         physicalAttributes.setFillFactor("FILLFACTOR = ");
         physicalAttributes.setPctFree((String)null);
      }

      if (physicalAttributes.getPctFreeValue() != null) {
         tempPctUsedValue = physicalAttributes.getPctFreeValue();
         physicalAttributes.setFillFactorValue(tempPctUsedValue);
         physicalAttributes.setPctFreeValue((String)null);
      }

      if (physicalAttributes.getPctUsed() != null) {
         tempPctUsedValue = physicalAttributes.getPctFreeValue();
         physicalAttributes.setFillFactor("FILLFACTOR = ");
         physicalAttributes.setPctUsed((String)null);
      }

      if (physicalAttributes.getPctUsedValue() != null) {
         tempPctUsedValue = physicalAttributes.getPctUsedValue();
         int fillIntValue = Integer.parseInt(tempPctUsedValue);
         this.fillFactorValueOfPctused = 100 - fillIntValue;
         tempPctUsedValue = "" + this.fillFactorValueOfPctused;
         physicalAttributes.setFillFactorValue(tempPctUsedValue);
         physicalAttributes.setPctUsedValue((String)null);
      }

      physicalAttributes.setIniTrans((String)null);
      physicalAttributes.setMaxTrans((String)null);
      physicalAttributes.setStorageClause((String)null);
      physicalAttributes.setNoCache((String)null);
      physicalAttributes.setOnline((String)null);
      physicalAttributes.setLoggingOrNoLogging((String)null);
      physicalAttributes.setCompute((String)null);
      physicalAttributes.setTableSpaceOrDefault((String)null);
      physicalAttributes.setTableSpaceName((String)null);
      physicalAttributes.setCompressOrNoCompress((String)null);
      physicalAttributes.setNoSortOrReverse((String)null);
      if (physicalAttributes.getPadIndex() != null) {
         tempPctUsedValue = physicalAttributes.getPadIndex();
      }

      if (physicalAttributes.getFillFactor() != null) {
         tempPctUsedValue = physicalAttributes.getFillFactor();
      }

      if (physicalAttributes.getFillFactorValue() != null) {
         tempPctUsedValue = physicalAttributes.getFillFactorValue();
      }

      if (physicalAttributes.getDropExisting() != null) {
         tempPctUsedValue = physicalAttributes.getDropExisting();
      }

      if (physicalAttributes.getStatisticsNoreCompute() != null) {
         tempPctUsedValue = physicalAttributes.getStatisticsNoreCompute();
      }

      if (physicalAttributes.getSortInTempDb() != null) {
         tempPctUsedValue = physicalAttributes.getSortInTempDb();
      }

      return physicalAttributes;
   }

   public PhysicalAttributesClause toMySQL() throws ConvertException {
      PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
      physicalAttributes.setPctFree((String)null);
      physicalAttributes.setPctFreeValue((String)null);
      physicalAttributes.setPctUsed((String)null);
      physicalAttributes.setPctUsedValue((String)null);
      physicalAttributes.setIniTrans((String)null);
      physicalAttributes.setMaxTrans((String)null);
      physicalAttributes.setStorageClause((String)null);
      physicalAttributes.setNoCache((String)null);
      physicalAttributes.setOnline((String)null);
      physicalAttributes.setLoggingOrNoLogging((String)null);
      physicalAttributes.setCompute((String)null);
      physicalAttributes.setTableSpaceOrDefault((String)null);
      physicalAttributes.setTableSpaceName((String)null);
      physicalAttributes.setCompressOrNoCompress((String)null);
      physicalAttributes.setNoSortOrReverse((String)null);
      physicalAttributes.setPadIndex((String)null);
      physicalAttributes.setFillFactor((String)null);
      physicalAttributes.setFillFactorValue((String)null);
      physicalAttributes.setDropExisting((String)null);
      physicalAttributes.setWith((String)null);
      physicalAttributes.setDiskAttr((HashMap)null);
      physicalAttributes.setStatisticsNoreCompute((String)null);
      physicalAttributes.setSortInTempDb((String)null);
      return physicalAttributes;
   }

   public PhysicalAttributesClause toOracle() throws ConvertException {
      PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
      String var2;
      if (physicalAttributes.getPctFree() != null) {
         var2 = physicalAttributes.getPctFree();
      }

      if (physicalAttributes.getPctFreeValue() != null) {
         var2 = physicalAttributes.getPctFreeValue();
      }

      if (physicalAttributes.getPctUsed() != null) {
         var2 = physicalAttributes.getPctUsed();
      }

      if (physicalAttributes.getIniTrans() != null) {
         var2 = physicalAttributes.getIniTrans();
      }

      if (physicalAttributes.getMaxTrans() != null) {
         var2 = physicalAttributes.getMaxTrans();
      }

      if (physicalAttributes.getStorageClause() != null) {
         var2 = physicalAttributes.getStorageClause();
      }

      if (physicalAttributes.getOnline() != null) {
         var2 = physicalAttributes.getOnline();
      }

      if (physicalAttributes.getLoggingOrNoLogging() != null) {
         var2 = physicalAttributes.getLoggingOrNoLogging();
      }

      if (physicalAttributes.getCompute() != null) {
         var2 = physicalAttributes.getCompute();
      }

      if (physicalAttributes.getTableSpaceOrDefault() != null) {
         var2 = physicalAttributes.getTableSpaceOrDefault();
      }

      if (physicalAttributes.getTableSpaceName() != null) {
         var2 = physicalAttributes.getTableSpaceName();
      }

      if (physicalAttributes.getCompressOrNoCompress() != null) {
         var2 = physicalAttributes.getCompressOrNoCompress();
      }

      if (physicalAttributes.getNoSortOrReverse() != null) {
         var2 = physicalAttributes.getNoSortOrReverse();
      }

      if (physicalAttributes.getFillFactor() != null) {
         physicalAttributes.setFillFactor("PCTFREE ");
      }

      if (physicalAttributes.getFillFactorValue() != null) {
         physicalAttributes.setFillFactorValue(this.fillfactorValue);
      }

      if (physicalAttributes.getTableSpaceName() != null) {
         physicalAttributes.setTableSpaceOrDefault("TABLESPACE");
      }

      physicalAttributes.setPadIndex((String)null);
      physicalAttributes.setDropExisting((String)null);
      physicalAttributes.setWith((String)null);
      physicalAttributes.setDiskAttr((HashMap)null);
      physicalAttributes.setStatisticsNoreCompute((String)null);
      physicalAttributes.setSortInTempDb((String)null);
      return physicalAttributes;
   }

   public PhysicalAttributesClause toBigQuery() throws ConvertException {
      PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
      physicalAttributes.setPctFree((String)null);
      physicalAttributes.setPctFreeValue((String)null);
      physicalAttributes.setPctUsed((String)null);
      physicalAttributes.setPctUsedValue((String)null);
      physicalAttributes.setIniTrans((String)null);
      physicalAttributes.setMaxTrans((String)null);
      physicalAttributes.setStorageClause((String)null);
      physicalAttributes.setNoCache((String)null);
      physicalAttributes.setOnline((String)null);
      physicalAttributes.setLoggingOrNoLogging((String)null);
      physicalAttributes.setCompute((String)null);
      physicalAttributes.setTableSpaceOrDefault((String)null);
      physicalAttributes.setTableSpaceName((String)null);
      physicalAttributes.setCompressOrNoCompress((String)null);
      physicalAttributes.setNoSortOrReverse((String)null);
      physicalAttributes.setPadIndex((String)null);
      physicalAttributes.setFillFactor((String)null);
      physicalAttributes.setFillFactorValue((String)null);
      physicalAttributes.setDropExisting((String)null);
      physicalAttributes.setWith((String)null);
      physicalAttributes.setDiskAttr((HashMap)null);
      physicalAttributes.setStatisticsNoreCompute((String)null);
      physicalAttributes.setSortInTempDb((String)null);
      return physicalAttributes;
   }

   public PhysicalAttributesClause toPostgreSQL() throws ConvertException {
      PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
      physicalAttributes.setPctFree((String)null);
      physicalAttributes.setPctFreeValue((String)null);
      physicalAttributes.setPctUsed((String)null);
      physicalAttributes.setPctUsedValue((String)null);
      physicalAttributes.setIniTrans((String)null);
      physicalAttributes.setMaxTrans((String)null);
      physicalAttributes.setStorageClause((String)null);
      physicalAttributes.setNoCache((String)null);
      physicalAttributes.setOnline((String)null);
      physicalAttributes.setLoggingOrNoLogging((String)null);
      physicalAttributes.setCompute((String)null);
      physicalAttributes.setTableSpaceOrDefault((String)null);
      physicalAttributes.setTableSpaceName((String)null);
      physicalAttributes.setCompressOrNoCompress((String)null);
      physicalAttributes.setNoSortOrReverse((String)null);
      physicalAttributes.setPadIndex((String)null);
      physicalAttributes.setFillFactor((String)null);
      physicalAttributes.setFillFactorValue((String)null);
      physicalAttributes.setDropExisting((String)null);
      physicalAttributes.setWith((String)null);
      physicalAttributes.setDiskAttr((HashMap)null);
      physicalAttributes.setStatisticsNoreCompute((String)null);
      physicalAttributes.setSortInTempDb((String)null);
      return physicalAttributes;
   }

   public PhysicalAttributesClause toTimesTen() throws ConvertException {
      PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
      physicalAttributes.setPctFree((String)null);
      physicalAttributes.setPctFreeValue((String)null);
      physicalAttributes.setPctUsed((String)null);
      physicalAttributes.setPctUsedValue((String)null);
      physicalAttributes.setIniTrans((String)null);
      physicalAttributes.setMaxTrans((String)null);
      physicalAttributes.setStorageClause((String)null);
      physicalAttributes.setNoCache((String)null);
      physicalAttributes.setOnline((String)null);
      physicalAttributes.setLoggingOrNoLogging((String)null);
      physicalAttributes.setCompute((String)null);
      physicalAttributes.setTableSpaceOrDefault((String)null);
      physicalAttributes.setTableSpaceName((String)null);
      physicalAttributes.setCompressOrNoCompress((String)null);
      physicalAttributes.setNoSortOrReverse((String)null);
      physicalAttributes.setPadIndex((String)null);
      physicalAttributes.setFillFactor((String)null);
      physicalAttributes.setFillFactorValue((String)null);
      physicalAttributes.setDropExisting((String)null);
      physicalAttributes.setStatisticsNoreCompute((String)null);
      physicalAttributes.setSortInTempDb((String)null);
      physicalAttributes.setWith((String)null);
      physicalAttributes.setDiskAttr((HashMap)null);
      return physicalAttributes;
   }

   public PhysicalAttributesClause toNetezza() throws ConvertException {
      PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
      physicalAttributes.setPctFree((String)null);
      physicalAttributes.setPctFreeValue((String)null);
      physicalAttributes.setPctUsed((String)null);
      physicalAttributes.setPctUsedValue((String)null);
      physicalAttributes.setIniTrans((String)null);
      physicalAttributes.setMaxTrans((String)null);
      physicalAttributes.setStorageClause((String)null);
      physicalAttributes.setNoCache((String)null);
      physicalAttributes.setOnline((String)null);
      physicalAttributes.setLoggingOrNoLogging((String)null);
      physicalAttributes.setCompute((String)null);
      physicalAttributes.setTableSpaceOrDefault((String)null);
      physicalAttributes.setTableSpaceName((String)null);
      physicalAttributes.setCompressOrNoCompress((String)null);
      physicalAttributes.setNoSortOrReverse((String)null);
      physicalAttributes.setPadIndex((String)null);
      physicalAttributes.setFillFactor((String)null);
      physicalAttributes.setFillFactorValue((String)null);
      physicalAttributes.setDropExisting((String)null);
      physicalAttributes.setStatisticsNoreCompute((String)null);
      physicalAttributes.setSortInTempDb((String)null);
      physicalAttributes.setWith((String)null);
      physicalAttributes.setDiskAttr((HashMap)null);
      return physicalAttributes;
   }

   public PhysicalAttributesClause toSnowflake() throws ConvertException {
      PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
      physicalAttributes.setPctFree((String)null);
      physicalAttributes.setPctFreeValue((String)null);
      physicalAttributes.setPctUsed((String)null);
      physicalAttributes.setPctUsedValue((String)null);
      physicalAttributes.setIniTrans((String)null);
      physicalAttributes.setMaxTrans((String)null);
      physicalAttributes.setStorageClause((String)null);
      physicalAttributes.setNoCache((String)null);
      physicalAttributes.setOnline((String)null);
      physicalAttributes.setLoggingOrNoLogging((String)null);
      physicalAttributes.setCompute((String)null);
      physicalAttributes.setTableSpaceOrDefault((String)null);
      physicalAttributes.setTableSpaceName((String)null);
      physicalAttributes.setCompressOrNoCompress((String)null);
      physicalAttributes.setNoSortOrReverse((String)null);
      physicalAttributes.setPadIndex((String)null);
      physicalAttributes.setFillFactor((String)null);
      physicalAttributes.setFillFactorValue((String)null);
      physicalAttributes.setDropExisting((String)null);
      physicalAttributes.setWith((String)null);
      physicalAttributes.setDiskAttr((HashMap)null);
      physicalAttributes.setStatisticsNoreCompute((String)null);
      physicalAttributes.setSortInTempDb((String)null);
      return physicalAttributes;
   }

   public String removeIndent(String str) {
      str = str.replace('\n', ' ');
      str = str.replace('\t', ' ');
      return str;
   }

   public PhysicalAttributesClause copyObjectValues() {
      PhysicalAttributesClause dupPhysicalAttributesClause = new PhysicalAttributesClause();
      dupPhysicalAttributesClause.setPctFree(this.pctfree);
      dupPhysicalAttributesClause.setPctFreeValue(this.pctfreeValue);
      dupPhysicalAttributesClause.setPctUsed(this.pctused);
      dupPhysicalAttributesClause.setPctUsedValue(this.pctUsedValue);
      dupPhysicalAttributesClause.setIniTrans(this.initrans);
      dupPhysicalAttributesClause.setMaxTrans(this.maxtrans);
      dupPhysicalAttributesClause.setStorageClause(this.storageClause);
      dupPhysicalAttributesClause.setNoCache(this.noCache);
      dupPhysicalAttributesClause.setLoggingOrNoLogging(this.loggingOrNoLogging);
      dupPhysicalAttributesClause.setOnline(this.online);
      dupPhysicalAttributesClause.setCompute(this.compute);
      dupPhysicalAttributesClause.setTableSpaceOrDefault(this.tableSpaceOrDefault);
      dupPhysicalAttributesClause.setTableSpaceName(this.tableSpaceName);
      dupPhysicalAttributesClause.setCompressOrNoCompress(this.compressOrNoCompress);
      dupPhysicalAttributesClause.setNoSortOrReverse(this.noSortOrReverse);
      dupPhysicalAttributesClause.setPadIndex(this.padIndex);
      dupPhysicalAttributesClause.setFillFactor(this.fillfactor);
      dupPhysicalAttributesClause.setFillFactorValue(this.fillfactorValue);
      dupPhysicalAttributesClause.setStatisticsNoreCompute(this.statisticsNoreCompute);
      dupPhysicalAttributesClause.setSortInTempDb(this.sortInTempDb);
      dupPhysicalAttributesClause.setDropExisting(this.dropExisting);
      dupPhysicalAttributesClause.setWith(this.with);
      dupPhysicalAttributesClause.setDiskAttr(this.diskAttr);
      return dupPhysicalAttributesClause;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.pctfree != null) {
         sb.append(this.pctfree.toUpperCase() + " ");
      }

      if (this.pctfreeValue != null) {
         sb.append(this.pctfreeValue.toUpperCase() + " ");
      }

      if (this.pctused != null) {
         sb.append(this.pctused.toUpperCase() + " ");
      }

      if (this.pctUsedValue != null) {
         sb.append(this.pctUsedValue + " ");
      }

      if (this.initrans != null) {
         sb.append(this.initrans.toUpperCase() + " ");
      }

      if (this.maxtrans != null) {
         sb.append(this.maxtrans.toUpperCase() + " ");
      }

      if (this.storageClause != null) {
         sb.append(this.storageClause.toUpperCase() + " ");
      }

      if (this.noCache != null) {
         sb.append(this.noCache.toUpperCase() + " ");
      }

      if (this.online != null) {
         sb.append(this.online.toUpperCase() + " ");
      }

      if (this.loggingOrNoLogging != null) {
         sb.append(this.loggingOrNoLogging.toUpperCase() + " ");
      }

      if (this.compute != null) {
         sb.append(this.compute.toUpperCase() + " ");
      }

      if (this.tableSpaceOrDefault != null) {
         sb.append(this.tableSpaceOrDefault.toUpperCase() + " ");
      }

      if (this.tableSpaceName != null) {
         sb.append(this.tableSpaceName + " ");
      }

      if (this.noSortOrReverse != null) {
         sb.append(this.noSortOrReverse.toUpperCase() + " ");
      }

      if (this.compressOrNoCompress != null) {
         sb.append(this.compressOrNoCompress.toUpperCase() + " ");
      }

      if (this.padIndex != null) {
         sb.append(this.padIndex.toUpperCase() + " ");
      }

      if (this.fillfactor != null) {
         sb.append(this.fillfactor.toUpperCase() + "  ");
      }

      if (this.fillfactorValue != null) {
         sb.append(this.fillfactorValue.toUpperCase() + " ");
      }

      if (this.dropExisting != null) {
         sb.append(this.dropExisting.toUpperCase() + " ");
      }

      if (this.statisticsNoreCompute != null) {
         sb.append(this.statisticsNoreCompute.toUpperCase() + " ");
      }

      if (this.sortInTempDb != null) {
         sb.append(this.sortInTempDb.toUpperCase() + " ");
      }

      if (this.with != null) {
         sb.append(this.with.toUpperCase() + " ");
      }

      if (this.diskAttr != null && this.diskAttr.size() > 0) {
         Set keys = this.diskAttr.keySet();
         Iterator it = keys.iterator();

         for(boolean start = true; it.hasNext(); start = false) {
            if (!start) {
               sb.append(", ");
            }

            Object obj = it.next();
            if (((String)this.diskAttr.get(obj)).equals("")) {
               sb.append(obj.toString().toUpperCase());
            } else {
               sb.append(obj.toString().toUpperCase() + " = " + (String)this.diskAttr.get(obj));
            }
         }
      }

      return sb.toString();
   }
}
