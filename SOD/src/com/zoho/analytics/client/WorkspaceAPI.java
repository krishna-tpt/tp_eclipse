package com.zoho.analytics.client;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WorkspaceAPI {
   private final AnalyticsClient ac;
   private final long orgId;
   private final String workspaceEndPoint;
   private final Map<String, String> reqHeader = new HashMap();

   protected WorkspaceAPI(AnalyticsClient ac, long orgId, long workspaceId) {
      this.ac = ac;
      this.orgId = orgId;
      this.workspaceEndPoint = "/restapi/v2/workspaces/" + workspaceId;
      this.reqHeader.put("ZANALYTICS-ORGID", String.valueOf(orgId));
   }

   public void rename(String workspaceName, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      config = config == null ? new JSONObject() : config;
      config.put("workspaceName", workspaceName);
      this.ac.sendAPIRequest("PUT", this.workspaceEndPoint, config, this.reqHeader);
   }

   public void delete() throws ServerException, ParseException, IOException, JSONException {
      this.ac.sendAPIRequest("DELETE", this.workspaceEndPoint, (JSONObject)null, this.reqHeader);
   }

   public String getSecretKey(JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/secretkey";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, config, this.reqHeader);
      return response.getJSONObject("data").getString("workspaceKey");
   }

   public long copy(String newWorkspaceName, JSONObject config, Long destOrgId) throws ServerException, ParseException, IOException, JSONException {
      config = config == null ? new JSONObject() : config;
      config.put("newWorkspaceName", newWorkspaceName);
      Map<String, String> header = new HashMap();
      header.putAll(this.reqHeader);
      if (destOrgId != null) {
         header.put("ZANALYTICS-DEST-ORGID", String.valueOf(destOrgId));
      }

      JSONObject response = this.ac.sendAPIRequest("POST", this.workspaceEndPoint, config, header);
      String workspaceId = response.getJSONObject("data").getString("workspaceId");
      return Long.valueOf(workspaceId);
   }

   public void addFavorite() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/favorite";
      this.ac.sendAPIRequest("POST", endPoint, (JSONObject)null, this.reqHeader);
   }

   public void removeFavorite() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/favorite";
      this.ac.sendAPIRequest("DELETE", endPoint, (JSONObject)null, this.reqHeader);
   }

   public void addDefault() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/default";
      this.ac.sendAPIRequest("POST", endPoint, (JSONObject)null, this.reqHeader);
   }

   public void removeDefault() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/default";
      this.ac.sendAPIRequest("DELETE", endPoint, (JSONObject)null, this.reqHeader);
   }

   public void enableDomainAccess() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/wlaccess";
      this.ac.sendAPIRequest("POST", endPoint, (JSONObject)null, this.reqHeader);
   }

   public void disableDomainAccess() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/wlaccess";
      this.ac.sendAPIRequest("DELETE", endPoint, (JSONObject)null, this.reqHeader);
   }

   public JSONArray getViews() throws ServerException, ParseException, IOException, JSONException {
      return this.getViews((JSONObject)null);
   }

   public JSONArray getViews(JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/views";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, config, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("views");
   }

   public long createTable(JSONObject tableDesign) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/tables";
      JSONObject config = new JSONObject();
      config.put("tableDesign", tableDesign);
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      String viewId = response.getJSONObject("data").getString("viewId");
      return Long.valueOf(viewId);
   }

   public JSONArray copyViews(JSONArray viewIds, long destWorkspaceId, JSONObject config, Long destOrgId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/views/copy";
      config = config == null ? new JSONObject() : config;
      config.put("viewIds", viewIds);
      config.put("destWorkspaceId", destWorkspaceId);
      Map<String, String> header = new HashMap();
      header.putAll(this.reqHeader);
      if (destOrgId != null) {
         header.put("ZANALYTICS-DEST-ORGID", String.valueOf(destOrgId));
      }

      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, header);
      return response.getJSONObject("data").getJSONArray("views");
   }

   public JSONArray getFolders() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/folders";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("folders");
   }

   public long createFolder(String folderName, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/folders";
      config = config == null ? new JSONObject() : config;
      config.put("folderName", folderName);
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      String folderId = response.getJSONObject("data").getString("folderId");
      return Long.valueOf(folderId);
   }

   public void renameFolder(long folderId, String folderName, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/folders/" + folderId;
      config = config == null ? new JSONObject() : config;
      config.put("folderName", folderName);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void deleteFolder(long folderId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/folders/" + folderId;
      this.ac.sendAPIRequest("DELETE", endPoint, (JSONObject)null, this.reqHeader);
   }

   public JSONArray getGroups() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/groups";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("groups");
   }

   public JSONObject getGroupDetails(long groupId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/groups/" + groupId;
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONObject("groups");
   }

   public String createGroup(String groupName, JSONArray emailIds, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/groups";
      config = config == null ? new JSONObject() : config;
      config.put("groupName", groupName);
      config.put("emailIds", emailIds);
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      return response.getJSONObject("data").getString("groupId");
   }

   public void renameGroup(long groupId, String groupName, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/groups/" + groupId;
      config = config == null ? new JSONObject() : config;
      config.put("groupName", groupName);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void deleteGroup(long groupId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/groups/" + groupId;
      this.ac.sendAPIRequest("DELETE", endPoint, (JSONObject)null, this.reqHeader);
   }

   public void addGroupMembers(long groupId, JSONArray emailIds, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/groups/" + groupId + "/members";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
   }

   public void removeGroupMembers(long groupId, JSONArray emailIds, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/groups/" + groupId + "/members";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      this.ac.sendAPIRequest("DELETE", endPoint, config, this.reqHeader);
   }

   public JSONArray getAdmins() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/admins";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("workspaceAdmins");
   }

   public void addAdmins(JSONArray emailIds, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/admins";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
   }

   public void removeAdmins(JSONArray emailIds, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/admins";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      this.ac.sendAPIRequest("DELETE", endPoint, config, this.reqHeader);
   }

   public ShareInfo getShareInfo() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/share";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return new ShareInfo(response.getJSONObject("data"));
   }

   public void shareViews(JSONArray viewIds, JSONArray emailIds, JSONObject permissions, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/share";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      config.put("viewIds", viewIds);
      config.put("permissions", permissions);
      this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
   }

   public void removeShare(JSONArray viewIds, JSONArray emailIds, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/share";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      if (viewIds != null) {
         config.put("viewIds", viewIds);
      }

      this.ac.sendAPIRequest("DELETE", endPoint, config, this.reqHeader);
   }

   public JSONArray getSharedDetailsForViews(JSONArray viewIds) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/share/shareddetails";
      JSONObject config = new JSONObject();
      config.put("viewIds", viewIds);
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, config, this.reqHeader);
      JSONArray sharedDetails = response.getJSONObject("data").getJSONArray("sharedDetails");
      return sharedDetails;
   }

   public JSONArray getSlideshows() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/slides";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      JSONArray slideshows = response.getJSONObject("data").getJSONArray("slideshows");
      return slideshows;
   }

   public JSONObject getSlideshowDetails(long slideId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/slides/" + slideId;
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONObject("slideInfo");
   }

   public String getSlideshowURL(long slideId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/slides/" + slideId + "/publish";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, config, this.reqHeader);
      return response.getJSONObject("data").getString("slideUrl");
   }

   public long createSlideshow(String slideName, JSONArray viewIds, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/slides";
      config = config == null ? new JSONObject() : config;
      config.put("slideName", slideName);
      config.put("viewIds", viewIds);
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      String slideId = response.getJSONObject("data").getString("slideId");
      return Long.valueOf(slideId);
   }

   public void updateSlideshow(long slideId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/slides/" + slideId;
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void deleteSlideshow(long slideId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/slides/" + slideId;
      this.ac.sendAPIRequest("DELETE", endPoint, (JSONObject)null, this.reqHeader);
   }

   public long createVariable(String variableName, int variableDataType, int variableType, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/variables";
      config = config == null ? new JSONObject() : config;
      config.put("variableName", variableName);
      config.put("variableDataType", variableDataType);
      config.put("variableType", variableType);
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      String variableId = response.getJSONObject("data").getString("variableId");
      return Long.valueOf(variableId);
   }

   public void updateVariable(long variableId, String variableName, int variableDataType, int variableType, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/variables/" + variableId;
      config = config == null ? new JSONObject() : config;
      config.put("variableName", variableName);
      config.put("variableDataType", variableDataType);
      config.put("variableType", variableType);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void deleteVariable(long variableId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/variables/" + variableId;
      this.ac.sendAPIRequest("DELETE", endPoint, (JSONObject)null, this.reqHeader);
   }

   public JSONArray getVariables() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/variables";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("variables");
   }

   public JSONObject getVariableDetails(long variableId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/variables/" + variableId;
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data");
   }

   public void makeDefaultFolder(long folderId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/folders/" + folderId + "/default";
      this.ac.sendAPIRequest("PUT", endPoint, (JSONObject)null, this.reqHeader);
   }

   public JSONArray getDatasources() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/datasources";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("dataSources");
   }

   public void syncData(long datasourceId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/datasources/" + datasourceId + "/sync";
      this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
   }

   public void updateDatasourceConnection(long datasourceId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/datasources/" + datasourceId;
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public JSONArray getTrashViews() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/trash";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("views");
   }

   public void restoreTrashView(long viewId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/trash/" + viewId;
      this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
   }

   public void deleteTrashView(long viewId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/trash/" + viewId;
      this.ac.sendAPIRequest("DELETE", endPoint, config, this.reqHeader);
   }

   public long createQueryTable(String sqlQuery, String queryTableName, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/querytables";
      config = config == null ? new JSONObject() : config;
      config.put("sqlQuery", sqlQuery);
      config.put("queryTableName", queryTableName);
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      String viewId = response.getJSONObject("data").getString("viewId");
      return Long.valueOf(viewId);
   }

   public void updateQueryTable(long viewId, String sqlQuery, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/querytables/" + viewId;
      config = config == null ? new JSONObject() : config;
      config.put("sqlQuery", sqlQuery);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void changeFolderHierarchy(long folderId, int hierarchy, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/folders/" + folderId + "/move";
      config = config == null ? new JSONObject() : config;
      config.put("hierarchy", hierarchy);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void changeFolderPosition(long folderId, long referenceFolderId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/folders/" + folderId + "/reorder";
      config = config == null ? new JSONObject() : config;
      config.put("referenceFolderId", referenceFolderId);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void moveViewsToFolder(long folderId, JSONArray viewIds, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/views/movetofolder";
      config = config == null ? new JSONObject() : config;
      config.put("folderId", folderId);
      config.put("viewIds", viewIds);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public JSONArray getWorkspaceUsers() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/users";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("users");
   }

   public void addWorkspaceUsers(JSONArray emailIds, String role, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/users";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      config.put("role", role);
      this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
   }

   public void removeWorkspaceUsers(JSONArray emailIds, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/users";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      this.ac.sendAPIRequest("DELETE", endPoint, config, this.reqHeader);
   }

   public void changeWorkspaceUserStatus(JSONArray emailIds, String operation, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/users/status";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      config.put("operation", operation);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void changeWorkspaceUserRole(JSONArray emailIds, String role, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/users/role";
      config = config == null ? new JSONObject() : config;
      config.put("emailIds", emailIds);
      config.put("role", role);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void exportAsTemplate(JSONArray viewIds, String filePath, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/template/data";
      config = config == null ? new JSONObject() : config;
      config.put("viewIds", viewIds);
      File file = new File(filePath);
      this.ac.sendExportAPIRequest(endPoint, config, this.reqHeader, file);
   }

   public JSONArray getEmailSchedules() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/emailschedules";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("emailSchedules");
   }

   public long createEmailSchedule(String scheduleName, JSONArray viewIds, String format, JSONArray emailIds, JSONObject scheduleDetails, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/emailschedules";
      config = config == null ? new JSONObject() : config;
      config.put("scheduleName", scheduleName);
      config.put("viewIds", viewIds);
      config.put("exportType", format);
      config.put("emailIds", emailIds);
      config.put("scheduleDetails", scheduleDetails);
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      String scheduleId = response.getJSONObject("data").getString("scheduleId");
      return Long.valueOf(scheduleId);
   }

   public long updateEmailSchedule(long scheduleId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/emailschedules/" + scheduleId;
      config = config == null ? new JSONObject() : config;
      JSONObject response = this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
      String newScheduleId = response.getJSONObject("data").getString("scheduleId");
      return Long.valueOf(newScheduleId);
   }

   public void triggerEmailSchedule(long scheduleId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/emailschedules/" + scheduleId;
      this.ac.sendAPIRequest("POST", endPoint, (JSONObject)null, this.reqHeader);
   }

   public void changeEmailScheduleStatus(long scheduleId, String operation) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/emailschedules/" + scheduleId + "/status";
      JSONObject config = new JSONObject();
      config.put("operation", operation);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void deleteEmailSchedule(long scheduleId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/emailschedules/" + scheduleId;
      this.ac.sendAPIRequest("DELETE", endPoint, (JSONObject)null, this.reqHeader);
   }

   public JSONArray getAggregateFormulas(JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/aggregateformulas";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, config, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("aggregateFormulas");
   }

   public JSONObject getAggregateFormulaDependents(long formulaId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/aggregateformulas/" + formulaId + "/dependents";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data");
   }

   public String getAggregateFormulaValue(long formulaId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/aggregateformulas/" + formulaId + "/value";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getString("formulaValue");
   }

   public long createReport(JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/reports";
      config = config == null ? new JSONObject() : config;
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      String variableId = response.getJSONObject("data").getString("viewId");
      return Long.valueOf(variableId);
   }

   public void updateReport(long viewId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/reports/" + viewId;
      config = config == null ? new JSONObject() : config;
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public JSONObject getWorkspaceMetadata() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.workspaceEndPoint + "/metadata";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONObject("workspaces");
   }
}
