package com.zoho.analytics.client;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class BulkAPI {
   private final AnalyticsClient ac;
   private final String dataEndPoint;
   private final String bulkDataEndPoint;
   Map<String, String> reqHeader = new HashMap();

   protected BulkAPI(AnalyticsClient ac, long orgId, long workspaceId) {
      this.ac = ac;
      this.dataEndPoint = "/restapi/v2/workspaces/" + workspaceId;
      this.bulkDataEndPoint = "/restapi/v2/bulk/workspaces/" + workspaceId;
      this.reqHeader.put("ZANALYTICS-ORGID", String.valueOf(orgId));
   }

   public JSONObject importDataInNewTable(String tableName, String fileType, boolean autoIdentify, String filePath, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.dataEndPoint + "/data";
      config = config == null ? new JSONObject() : config;
      config.put("tableName", tableName);
      config.put("fileType", fileType);
      config.put("autoIdentify", autoIdentify);
      File file = new File(filePath);
      JSONObject response = this.ac.sendImportAPIRequest(endPoint, config, this.reqHeader, file);
      return response.getJSONObject("data");
   }

   public JSONObject importData(long viewId, String importType, String fileType, boolean autoIdentify, String filePath, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.dataEndPoint + "/views/" + viewId + "/data";
      config = config == null ? new JSONObject() : config;
      config.put("fileType", fileType);
      config.put("autoIdentify", autoIdentify);
      config.put("importType", importType);
      File file = new File(filePath);
      JSONObject response = this.ac.sendImportAPIRequest(endPoint, config, this.reqHeader, file);
      return response.getJSONObject("data");
   }

   public JSONObject importRawDataInNewTable(String tableName, String fileType, boolean autoIdentify, String data, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.dataEndPoint + "/data";
      config = config == null ? new JSONObject() : config;
      config.put("tableName", tableName);
      config.put("fileType", fileType);
      config.put("autoIdentify", autoIdentify);
      JSONObject response = this.ac.sendImportAPIRequest(endPoint, config, this.reqHeader, data);
      return response.getJSONObject("data");
   }

   public JSONObject importRawData(long viewId, String importType, String fileType, boolean autoIdentify, String data, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.dataEndPoint + "/views/" + viewId + "/data";
      config = config == null ? new JSONObject() : config;
      config.put("fileType", fileType);
      config.put("autoIdentify", autoIdentify);
      config.put("importType", importType);
      JSONObject response = this.ac.sendImportAPIRequest(endPoint, config, this.reqHeader, data);
      return response.getJSONObject("data");
   }

   public long importBulkDataInNewTable(String tableName, String fileType, boolean autoIdentify, String filePath, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.bulkDataEndPoint + "/data";
      config = config == null ? new JSONObject() : config;
      config.put("tableName", tableName);
      config.put("fileType", fileType);
      config.put("autoIdentify", autoIdentify);
      File file = new File(filePath);
      JSONObject response = this.ac.sendImportAPIRequest(endPoint, config, this.reqHeader, file);
      String jobId = response.getJSONObject("data").getString("jobId");
      return Long.valueOf(jobId);
   }

   public long importBulkData(long viewId, String importType, String fileType, boolean autoIdentify, String filePath, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.bulkDataEndPoint + "/views/" + viewId + "/data";
      config = config == null ? new JSONObject() : config;
      config.put("fileType", fileType);
      config.put("autoIdentify", autoIdentify);
      config.put("importType", importType);
      File file = new File(filePath);
      JSONObject response = this.ac.sendImportAPIRequest(endPoint, config, this.reqHeader, file);
      String jobId = response.getJSONObject("data").getString("jobId");
      return Long.valueOf(jobId);
   }

   public JSONObject getImportJobDetails(long jobId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.bulkDataEndPoint + "/importjobs/" + jobId;
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data");
   }

   public void exportData(long viewId, String responseFormat, String filePath, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.dataEndPoint + "/views/" + viewId + "/data";
      config = config == null ? new JSONObject() : config;
      config.put("responseFormat", responseFormat);
      File file = new File(filePath);
      this.ac.sendExportAPIRequest(endPoint, config, this.reqHeader, file);
   }

   public void exportData(long viewId, String responseFormat, OutputStream stream, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.dataEndPoint + "/views/" + viewId + "/data";
      config = config == null ? new JSONObject() : config;
      config.put("responseFormat", responseFormat);
      this.ac.sendExportAPIRequest(endPoint, config, this.reqHeader, stream);
   }

   public void exportDataUsingSQL(String sqlQuery, String responseFormat, OutputStream stream, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.dataEndPoint + "/data";
      config = config == null ? new JSONObject() : config;
      config.put("sqlQuery", sqlQuery);
      config.put("responseFormat", responseFormat);
      this.ac.sendExportAPIRequest(endPoint, config, this.reqHeader, stream);
   }

   public void exportDataUsingSQL(String sqlQuery, String responseFormat, String filePath, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.dataEndPoint + "/data";
      config = config == null ? new JSONObject() : config;
      config.put("sqlQuery", sqlQuery);
      config.put("responseFormat", responseFormat);
      File file = new File(filePath);
      this.ac.sendExportAPIRequest(endPoint, config, this.reqHeader, file);
   }

   public long initiateBulkExport(long viewId, String responseFormat, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.bulkDataEndPoint + "/views/" + viewId + "/data";
      config = config == null ? new JSONObject() : config;
      config.put("responseFormat", responseFormat);
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, config, this.reqHeader);
      String jobId = response.getJSONObject("data").getString("jobId");
      return Long.valueOf(jobId);
   }

   public long initiateBulkExportUsingSQL(String sqlQuery, String responseFormat, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.bulkDataEndPoint + "/data";
      config = config == null ? new JSONObject() : config;
      config.put("sqlQuery", sqlQuery);
      config.put("responseFormat", responseFormat);
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, config, this.reqHeader);
      String jobId = response.getJSONObject("data").getString("jobId");
      return Long.valueOf(jobId);
   }

   public JSONObject getExportJobDetails(long jobId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.bulkDataEndPoint + "/exportjobs/" + jobId;
      JSONObject response = this.ac.sendAPIRequest("GET", endPoint, (JSONObject)null, this.reqHeader);
      return response.getJSONObject("data");
   }

   public void exportBulkData(long jobId, String filePath) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.bulkDataEndPoint + "/exportjobs/" + jobId + "/data";
      File file = new File(filePath);
      this.ac.sendExportAPIRequest(endPoint, (JSONObject)null, this.reqHeader, file);
   }

   public void exportBulkData(long jobId, OutputStream stream) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = this.bulkDataEndPoint + "/exportjobs/" + jobId + "/data";
      this.ac.sendExportAPIRequest(endPoint, (JSONObject)null, this.reqHeader, stream);
   }

   public long importDataInNewTableAsBatches(String tableName, boolean autoIdentify, String filePath, int batchSize, JSONObject config, JSONObject toolConfig) throws ServerException, ParseException, IOException, JSONException, Exception {
      String endPoint = this.bulkDataEndPoint + "/data/batch";
      config = config == null ? new JSONObject() : config;
      config.put("tableName", tableName);
      config.put("autoIdentify", autoIdentify);
      config.put("batchKey", "start");
      File file = new File(filePath);
      JSONObject response = this.ac.importDataInBatches(endPoint, config, this.reqHeader, file, batchSize, toolConfig);
      return Long.valueOf(response.getString("jobId"));
   }

   public long importDataAsBatches(long viewId, String importType, boolean autoIdentify, String filePath, int batchSize, JSONObject config, JSONObject toolConfig) throws ServerException, ParseException, IOException, JSONException, Exception {
      String endPoint = this.bulkDataEndPoint + "/views/" + viewId + "/data/batch";
      config = config == null ? new JSONObject() : config;
      config.put("importType", importType);
      config.put("autoIdentify", autoIdentify);
      config.put("batchKey", "start");
      File file = new File(filePath);
      JSONObject response = this.ac.importDataInBatches(endPoint, config, this.reqHeader, file, batchSize, toolConfig);
      return Long.valueOf(response.getString("jobId"));
   }

   public JSONObject handleCustomBatchImport(long viewId, String filePath, JSONObject config) throws ServerException, ParseException, IOException, JSONException, Exception {
      String endPoint = this.bulkDataEndPoint + "/views/" + viewId + "/data/batch";
      config = config == null ? new JSONObject() : config;
      File file = new File(filePath);
      JSONObject response = this.ac.sendImportAPIRequest(endPoint, config, this.reqHeader, file);
      return response.getJSONObject("data");
   }
}
