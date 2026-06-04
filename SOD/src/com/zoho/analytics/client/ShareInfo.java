package com.zoho.analytics.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

public class ShareInfo {
   private HashMap<String, ArrayList<PermissionInfo>> sharedUserPermInfoMap = new HashMap();
   private HashMap<String, ArrayList<PermissionInfo>> groupPermInfoMap = new HashMap();
   private ArrayList<PermissionInfo> privateLinkPermInfo = new ArrayList();
   private ArrayList<PermissionInfo> publicPermInfo = new ArrayList();

   protected ShareInfo(JSONObject shareInfo) throws ParseException {
      try {
         JSONArray userShareInfo = shareInfo.getJSONArray("userShareInfo");
         if (userShareInfo.length() > 0) {
            this.sharedUserPermInfoMap = getUserPermissionsInfoMap(userShareInfo);
         }

         JSONArray groupShareInfo = shareInfo.getJSONArray("groupShareInfo");
         if (groupShareInfo.length() > 0) {
            this.groupPermInfoMap = getGroupPermissionsInfoMap(groupShareInfo);
         }

         JSONObject publicShareInfo = shareInfo.getJSONObject("publicShareInfo");
         if (publicShareInfo.has("views")) {
            JSONArray publicShareInfoViewsArr = publicShareInfo.getJSONArray("views");
            this.publicPermInfo = getPermissionInfoList(publicShareInfoViewsArr);
         }

         JSONObject privateLinkShareInfo = shareInfo.getJSONObject("privateLinkShareInfo");
         if (privateLinkShareInfo.has("views")) {
            JSONArray privateLinkShareInfoViewsArr = privateLinkShareInfo.getJSONArray("views");
            this.privateLinkPermInfo = getPermissionInfoList(privateLinkShareInfoViewsArr);
         }

      } catch (Exception var7) {
         throw new ParseException(shareInfo.toString(), "Exception while parsing the share info.", var7);
      }
   }

   public Set<String> getSharedUsers() {
      return this.sharedUserPermInfoMap.keySet();
   }

   public HashMap<String, ArrayList<PermissionInfo>> getSharedUserPermissions() {
      return this.sharedUserPermInfoMap;
   }

   public Set<String> getGroupNames() {
      return this.groupPermInfoMap.keySet();
   }

   public HashMap<String, ArrayList<PermissionInfo>> getGroupPermissions() {
      return this.groupPermInfoMap;
   }

   public ArrayList<PermissionInfo> getPrivateLinkPermissions() {
      return this.privateLinkPermInfo;
   }

   public ArrayList<PermissionInfo> getPublicPermissions() {
      return this.publicPermInfo;
   }

   private static HashMap<String, ArrayList<PermissionInfo>> getUserPermissionsInfoMap(JSONArray shareInfoArr) throws Exception {
      HashMap<String, ArrayList<PermissionInfo>> permInfo = new HashMap();
      int arrLength = shareInfoArr.length();
      if (arrLength > 0) {
         for(int i = 0; i < arrLength; ++i) {
            JSONObject permInfoObj = shareInfoArr.getJSONObject(i);
            String emailId = permInfoObj.getString("emailId");
            JSONArray views = permInfoObj.getJSONArray("views");
            ArrayList<PermissionInfo> permInfoList = getPermissionInfoList(views);
            permInfo.put(emailId, permInfoList);
         }
      }

      return permInfo;
   }

   private static HashMap<String, ArrayList<PermissionInfo>> getGroupPermissionsInfoMap(JSONArray shareInfoArr) throws Exception {
      HashMap<String, ArrayList<PermissionInfo>> permInfo = new HashMap();
      int arrLength = shareInfoArr.length();
      if (arrLength > 0) {
         for(int i = 0; i < arrLength; ++i) {
            JSONObject permInfoObj = shareInfoArr.getJSONObject(i);
            String groupName = permInfoObj.getString("groupName");
            JSONArray views = permInfoObj.getJSONArray("views");
            ArrayList<PermissionInfo> permInfoList = getPermissionInfoList(views);
            permInfo.put(groupName, permInfoList);
         }
      }

      return permInfo;
   }

   private static ArrayList<PermissionInfo> getPermissionInfoList(JSONArray viewsArr) throws Exception {
      ArrayList<PermissionInfo> permissionInfoList = new ArrayList();
      int viewsLength = viewsArr.length();

      for(int i = 0; i < viewsLength; ++i) {
         JSONObject viewPerm = viewsArr.getJSONObject(i);
         long viewId = viewPerm.getLong("viewId");
         String viewName = viewPerm.getString("viewName");
         String sharedBy = viewPerm.getString("sharedBy");
         PermissionInfo permInfo = new PermissionInfo(viewName, viewId, sharedBy);
         JSONObject permissions = viewPerm.getJSONObject("permissions");
         Iterator ie = permissions.keys();

         while(ie.hasNext()) {
            String permName = (String)ie.next();
            permInfo.setPermission(permName, permissions.getBoolean(permName));
         }

         if (viewPerm.has("filterCriteria")) {
            JSONObject criteriaObj = viewPerm.getJSONObject("filterCriteria");
            permInfo.setFilterCriteria(criteriaObj.getString("criteria"));
         }

         permissionInfoList.add(permInfo);
      }

      return permissionInfoList;
   }
}
