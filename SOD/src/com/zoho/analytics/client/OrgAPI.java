package com.zoho.analytics.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrgAPI {
   private final AnalyticsClient ac;
   private final Map<String, String> reqHeader = new HashMap();

   protected OrgAPI(AnalyticsClient ac, long orgId) {
      this.ac = ac;
      this.reqHeader.put("ZANALYTICS-ORGID", String.valueOf(orgId));
   }

   public long createWorkspace(String workspaceName, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/workspaces/";
      config = config == null ? new JSONObject() : config;
      config.put("workspaceName", workspaceName);
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      String workspaceId = response.getJSONObject("data").getString("workspaceId");
      return Long.valueOf(workspaceId);
   }

   public JSONArray getAdmins() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/orgadmins";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("orgAdmins");
   }

   public JSONArray getUsers() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/users";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("users");
   }

   public void addUsers(JSONArray emailIds, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/users";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
   }

   public void removeUsers(JSONArray emailIds, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/users";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      this.ac.sendAPIRequest("DELETE", endPoint, config, this.reqHeader);
   }

   public void activateUsers(JSONArray emailIds, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/users/active";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void deactivateUsers(JSONArray emailIds, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/users/inactive";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void changeUserRole(JSONArray emailIds, String role, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/users/role";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      config.put("role", role);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public JSONObject getMetaDetails(String workspaceName, String viewName) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/metadetails";
      JSONObject config = new JSONObject();
      config.put("workspaceName", workspaceName);
      if (viewName != null) {
         config.put("viewName", viewName);
      }

      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, config, this.reqHeader);
      return response.getJSONObject("data");
   }

   public JSONObject getSubscriptionDetails() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/subscription";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONObject("subscription");
   }

   public JSONArray getResourceDetails() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/resources";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("resourceDetails");
   }

   public JSONArray getOrgWorkspaces() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/orgs/workspaces";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("workspaces");
   }
}
