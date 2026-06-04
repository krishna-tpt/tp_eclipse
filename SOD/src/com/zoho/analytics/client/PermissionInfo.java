package com.zoho.analytics.client;

import java.util.HashMap;

public class PermissionInfo {
   private String viewName;
   private long viewId;
   private String sharedBy;
   private String filterCriteria = null;
   private HashMap<String, Boolean> permsMap = new HashMap();

   protected PermissionInfo(String viewName, long viewId, String sharedBy) {
      this.viewName = viewName;
      this.viewId = viewId;
      this.sharedBy = sharedBy;
   }

   void setPermission(String permName, boolean permValue) {
      this.permsMap.put(permName, permValue);
   }

   void setFilterCriteria(String filterCriteria) {
      this.filterCriteria = filterCriteria;
   }

   public String getViewName() {
      return this.viewName;
   }

   public long getViewId() {
      return this.viewId;
   }

   public String getSharedBy() {
      return this.sharedBy;
   }

   public String getFilterCriteria() {
      return this.filterCriteria;
   }

   public Boolean hasReadPermission() {
      return (Boolean)this.permsMap.get("read");
   }

   public Boolean hasExportPermission() {
      return (Boolean)this.permsMap.get("export");
   }

   public Boolean hasVUDPermission() {
      return (Boolean)this.permsMap.get("vud");
   }

   public Boolean hasAddRowPermission() {
      return (Boolean)this.permsMap.get("addRow");
   }

   public Boolean hasUpdateRowPermission() {
      return (Boolean)this.permsMap.get("updateRow");
   }

   public Boolean hasDeleteRowPermission() {
      return (Boolean)this.permsMap.get("deleteRow");
   }

   public Boolean hasDeleteAllRowsPermission() {
      return (Boolean)this.permsMap.get("deleteAllRows");
   }

   public Boolean hasAppendImportPermission() {
      return (Boolean)this.permsMap.get("importAppend");
   }

   public Boolean hasUpdateImportPermission() {
      return (Boolean)this.permsMap.get("importAddOrUpdate");
   }

   public Boolean hasTruncateImportPermission() {
      return (Boolean)this.permsMap.get("importDeleteAllAdd");
   }

   public Boolean hasDeleteUpdateAddImportPermission() {
      return (Boolean)this.permsMap.get("importDeleteUpdateAdd");
   }

   public Boolean hasDrillDownPermission() {
      return (Boolean)this.permsMap.get("drillDown");
   }

   public Boolean hasSharePermission() {
      return (Boolean)this.permsMap.get("share");
   }

   public Boolean hasDiscussionPermission() {
      return (Boolean)this.permsMap.get("discussion");
   }
}
