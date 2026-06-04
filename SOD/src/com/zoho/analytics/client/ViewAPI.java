package com.zoho.analytics.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewAPI {
   private final AnalyticsClient ac;
   private final String viewEndPoint;
   private final Map<String, String> reqHeader = new HashMap();

   protected ViewAPI(AnalyticsClient ac, long orgId, long workspaceId, long viewId) {
      this.ac = ac;
      this.viewEndPoint = "/restapi/v2/workspaces/" + workspaceId + "/views/" + viewId;
      this.reqHeader.put("ZANALYTICS-ORGID", String.valueOf(orgId));
   }

   public JSONObject addRow(JSONObject columnValues, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/rows";
      config = config == null ? new JSONObject() : config;
      config.put("columns", columnValues);
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      return response.getJSONObject("data");
   }

   public JSONObject updateRow(JSONObject columnValues, String criteria, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/rows";
      config = config == null ? new JSONObject() : config;
      config.put("columns", columnValues);
      if (criteria != null) {
         config.put("criteria", criteria);
      }

      JSONObject response = this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
      return response.getJSONObject("data");
   }

   public int deleteRow(String criteria, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/rows";
      config = config == null ? new JSONObject() : config;
      if (criteria != null) {
         config.put("criteria", criteria);
      }

      JSONObject response = this.ac.sendAPIRequest("DELETE", endPoint, config, this.reqHeader);
      return response.getJSONObject("data").getInt("deletedRows");
   }

   public void rename(String viewName, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      config = config == null ? new JSONObject() : config;
      config.put("viewName", viewName);
      this.ac.sendAPIRequest("PUT", this.viewEndPoint, config, this.reqHeader);
   }

   public void delete(JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      this.ac.sendAPIRequest("DELETE", this.viewEndPoint, config, this.reqHeader);
   }

   public long saveAs(String newViewName, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/saveas";
      config = config == null ? new JSONObject() : config;
      config.put("viewName", newViewName);
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      String viewId = response.getJSONObject("data").getString("viewId");
      return Long.valueOf(viewId);
   }

   public void createSimilarViews(long refViewId, long folderId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/similarviews";
      config = config == null ? new JSONObject() : config;
      config.put("referenceViewId", refViewId);
      config.put("folderId", folderId);
      this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
   }

   public void autoAnalyse(JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/autoanalyse";
      this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
   }

   public void addFavorite() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/favorite";
      this.ac.sendAPIRequest("POST", endPoint, (JSONObject)null, this.reqHeader);
   }

   public void removeFavorite() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/favorite";
      this.ac.sendAPIRequest("DELETE", endPoint, (JSONObject)null, this.reqHeader);
   }

   public String getViewURL(JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/publish";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, config, this.reqHeader);
      return response.getJSONObject("data").getString("viewUrl");
   }

   public String getEmbedURL(JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/publish/embed";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, config, this.reqHeader);
      return response.getJSONObject("data").getString("embedUrl");
   }

   public String getPrivateURL(JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/publish/privatelink";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, config, this.reqHeader);
      return response.getJSONObject("data").getString("privateUrl");
   }

   public String createPrivateURL(JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/publish/privatelink";
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      return response.getJSONObject("data").getString("privateUrl");
   }

   public void removePrivateAccess() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/publish/privatelink";
      this.ac.sendAPIRequest("DELETE", endPoint, (JSONObject)null, this.reqHeader);
   }

   public String makeViewPublic(JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/publish/public";
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      return response.getJSONObject("data").getString("publicUrl");
   }

   public void removePublicAccess() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/publish/public";
      this.ac.sendAPIRequest("DELETE", endPoint, (JSONObject)null, this.reqHeader);
   }

   public JSONObject getPublishConfigurations() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/publish/config";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data");
   }

   public void updatePublishConfigurations(JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/publish/config";
      config = config == null ? new JSONObject() : config;
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public JSONObject getMyPermissions() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/share/userpermissions";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONObject("permissions");
   }

   public long addColumn(String columnName, String dataType, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/columns";
      config = config == null ? new JSONObject() : config;
      config.put("columnName", columnName);
      config.put("dataType", dataType);
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      String columnId = response.getJSONObject("data").getString("columnId");
      return Long.valueOf(columnId);
   }

   public void renameColumn(long columnId, String columnName) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/columns/" + columnId;
      JSONObject config = new JSONObject();
      config.put("columnName", columnName);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void deleteColumn(long columnId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/columns/" + columnId;
      this.ac.sendAPIRequest("DELETE", endPoint, config, this.reqHeader);
   }

   public void addLookup(long columnId, long refViewId, long refColumnId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/columns/" + columnId + "/lookup";
      config = config == null ? new JSONObject() : config;
      config.put("referenceViewId", refViewId);
      config.put("referenceColumnId", refColumnId);
      this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
   }

   public void removeLookup(long columnId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/columns/" + columnId + "/lookup";
      this.ac.sendAPIRequest("DELETE", endPoint, config, this.reqHeader);
   }

   public void hideColumns(JSONArray columnIds) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/columns/hide";
      JSONObject config = new JSONObject();
      config.put("columnIds", columnIds);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void showColumns(JSONArray columnIds) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/columns/show";
      JSONObject config = new JSONObject();
      config.put("columnIds", columnIds);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void autoAnalyseColumn(long columnId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/columns/" + columnId + "/autoanalyse";
      this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
   }

   public void copyFormulas(JSONArray formulaNames, long destWorkspaceId, JSONObject config, Long destOrgId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/formulas/copy";
      config = config == null ? new JSONObject() : config;
      config.put("formulaColumnNames", formulaNames);
      config.put("destWorkspaceId", destWorkspaceId);
      Map<String, String> header = new HashMap();
      header.putAll(this.reqHeader);
      if (destOrgId != null) {
         header.put("ZANALYTICS-DEST-ORGID", String.valueOf(destOrgId));
      }

      this.ac.sendAPIRequest("POST", endPoint, config, header);
   }

   public void refetchData(JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/sync";
      this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
   }

   public JSONObject getLastImportDetails() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/importdetails";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data");
   }

   public JSONArray getFormulaColumns() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/formulacolumns";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("formulaColumns");
   }

   public long addFormulaColumn(String formulaName, String expression, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/formulacolumns";
      config = config == null ? new JSONObject() : config;
      config.put("formulaName", formulaName);
      config.put("expression", expression);
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      String formulaId = response.getJSONObject("data").getString("formulaId");
      return Long.valueOf(formulaId);
   }

   public void editFormulaColumn(long formulaId, String expression, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/formulacolumns/" + formulaId;
      config = config == null ? new JSONObject() : config;
      config.put("expression", expression);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void deleteFormulaColumn(long formulaId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/formulacolumns/" + formulaId;
      config = config == null ? new JSONObject() : config;
      this.ac.sendAPIRequest("DELETE", endPoint, config, this.reqHeader);
   }

   public JSONArray getAggregateFormulas() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/aggregateformulas";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("aggregateFormulas");
   }

   public long addAggregateFormula(String formulaName, String expression, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/aggregateformulas";
      config = config == null ? new JSONObject() : config;
      config.put("formulaName", formulaName);
      config.put("expression", expression);
      JSONObject response = this.ac.sendAPIRequest("POST", endPoint, config, this.reqHeader);
      String formulaId = response.getJSONObject("data").getString("formulaId");
      return Long.valueOf(formulaId);
   }

   public void editAggregateFormula(long formulaId, String expression, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/aggregateformulas/" + formulaId;
      config = config == null ? new JSONObject() : config;
      config.put("expression", expression);
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }

   public void deleteAggregateFormula(long formulaId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/aggregateformulas/" + formulaId;
      config = config == null ? new JSONObject() : config;
      this.ac.sendAPIRequest("DELETE", endPoint, config, this.reqHeader);
   }

   public JSONArray getViewDependents() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/dependents";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data").getJSONArray("views");
   }

   public JSONObject getColumnDependents(long columnId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/columns/" + columnId + "/dependents";
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data");
   }

   public void UpdateSharedDetails(JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.viewEndPoint + "/share";
      config = config == null ? new JSONObject() : config;
      this.ac.sendAPIRequest("PUT", endPoint, config, this.reqHeader);
   }
}
