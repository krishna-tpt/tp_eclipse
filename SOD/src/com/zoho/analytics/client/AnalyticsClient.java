package com.zoho.analytics.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.net.Proxy.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AnalyticsClient {
   private final String client_version = "2.6.0";
   private String analyticsServerURL = "https://analyticsapi.zoho.com";
   private String accountsServerURL = "https://accounts.zoho.com";
   private final String clientId;
   private final String clientSecret;
   private final String refreshToken;
   private String accessToken;
   private int connectionTimeout = 5000;
   private int readTimeout = 0;
   private boolean proxy = false;
   private String proxyHost;
   private int proxyPort;
   private String proxyUsername;
   private String proxyPassword;
   Map<String, String> headers = new HashMap();
   static final Logger LOGGER = Logger.getLogger(AnalyticsClient.class.getName());

   public AnalyticsClient(String clientId, String clientSecret, String refreshToken) {
      this.clientId = clientId;
      this.clientSecret = clientSecret;
      this.refreshToken = refreshToken;
   }

   public void setAccessToken(String accessToken) {
      this.accessToken = accessToken;
   }

   public OrgAPI getOrgInstance(long orgId) {
      return new OrgAPI(this, orgId);
   }

   public WorkspaceAPI getWorkspaceInstance(long orgId, long workspaceId) {
      return new WorkspaceAPI(this, orgId, workspaceId);
   }

   public ViewAPI getViewInstance(long orgId, long workspaceId, long viewId) {
      return new ViewAPI(this, orgId, workspaceId, viewId);
   }

   public BulkAPI getBulkInstance(long orgId, long workspaceId) {
      return new BulkAPI(this, orgId, workspaceId);
   }

   public JSONArray getOrgs() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/orgs";
      JSONObject response = this.sendAPIRequest("GET", endPoint, (JSONObject)null, (Map)null);
      return response.getJSONObject("data").getJSONArray("orgs");
   }

   public JSONObject getWorkspaces() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/workspaces";
      JSONObject response = this.sendAPIRequest("GET", endPoint, (JSONObject)null, (Map)null);
      return response.getJSONObject("data");
   }

   public JSONArray getOwnedWorkspaces() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/workspaces/owned";
      JSONObject response = this.sendAPIRequest("GET", endPoint, (JSONObject)null, (Map)null);
      return response.getJSONObject("data").getJSONArray("workspaces");
   }

   public JSONArray getSharedWorkspaces() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/workspaces/shared";
      JSONObject response = this.sendAPIRequest("GET", endPoint, (JSONObject)null, (Map)null);
      return response.getJSONObject("data").getJSONArray("workspaces");
   }

   public JSONArray getRecentViews() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/recentviews";
      JSONObject response = this.sendAPIRequest("GET", endPoint, (JSONObject)null, (Map)null);
      return response.getJSONObject("data").getJSONArray("views");
   }

   public JSONObject getDashboards() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/dashboards";
      JSONObject response = this.sendAPIRequest("GET", endPoint, (JSONObject)null, (Map)null);
      return response.getJSONObject("data");
   }

   public JSONArray getOwnedDashboards() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/dashboards/owned";
      JSONObject response = this.sendAPIRequest("GET", endPoint, (JSONObject)null, (Map)null);
      return response.getJSONObject("data").getJSONArray("views");
   }

   public JSONArray getSharedDashboards() throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/dashboards/shared";
      JSONObject response = this.sendAPIRequest("GET", endPoint, (JSONObject)null, (Map)null);
      return response.getJSONObject("data").getJSONArray("views");
   }

   public JSONObject getWorkspaceDetails(long workspaceId) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/workspaces/" + workspaceId;
      JSONObject response = this.sendAPIRequest("GET", endPoint, (JSONObject)null, (Map)null);
      return response.getJSONObject("data").getJSONObject("workspaces");
   }

   public JSONObject getViewDetails(long viewId, JSONObject config) throws ServerException, ParseException, IOException, JSONException {
      String endPoint = "/restapi/v2/views/" + viewId;
      JSONObject response = this.sendAPIRequest("GET", endPoint, config, (Map)null);
      return response.getJSONObject("data").getJSONObject("views");
   }

   public void setAnalyticsServerURL(String serverURL) {
      this.analyticsServerURL = serverURL;
   }

   public void setAccountsServerURL(String serverURL) {
      this.accountsServerURL = serverURL;
   }

   public int getConnectionTimeout() {
      return this.connectionTimeout;
   }

   public void setConnectionTimeout(int timeout) {
      this.connectionTimeout = timeout;
   }

   public int getReadTimeout() {
      return this.readTimeout;
   }

   public void setReadTimeout(int timeout) {
      this.readTimeout = timeout;
   }

   public void setProxy(String proxyHost, int proxyPort, String proxyUsername, String proxyPassword) {
      this.proxy = true;
      this.proxyHost = proxyHost;
      this.proxyPort = proxyPort;
      this.proxyUsername = proxyUsername;
      this.proxyPassword = proxyPassword;
   }

   protected JSONObject sendAPIRequest(String reqMethod, String endPoint, JSONObject config, Map<String, String> reqHeaders) throws ServerException, ParseException, IOException, JSONException {
      if (this.accessToken == null) {
         this.regenerateOAuthToken();
      }

      String reqUrl = this.analyticsServerURL + endPoint;
      String configParam = null;
      String httpStatusCode = reqMethod.toUpperCase();
      byte var9 = -1;
      switch(httpStatusCode.hashCode()) {
      case 70454:
         if (httpStatusCode.equals("GET")) {
            var9 = 0;
         }
         break;
      case 79599:
         if (httpStatusCode.equals("PUT")) {
            var9 = 2;
         }
         break;
      case 2461856:
         if (httpStatusCode.equals("POST")) {
            var9 = 1;
         }
         break;
      case 2012838315:
         if (httpStatusCode.equals("DELETE")) {
            var9 = 3;
         }
      }

      Map responseMap;
      switch(var9) {
      case 0:
         String encodedParam = config == null ? "" : "CONFIG=" + URLEncoder.encode(config.toString(), "UTF-8");
         if (!encodedParam.isEmpty()) {
            reqUrl = reqUrl + "?" + encodedParam;
         }

         responseMap = this.submitRequest(reqMethod, reqUrl, (Map)reqHeaders, this.accessToken, (String)null);
         break;
      case 1:
      case 2:
      case 3:
         if (reqHeaders == null) {
            reqHeaders = new HashMap();
         }

         ((Map)reqHeaders).put("Content-Type", "application/x-www-form-urlencoded");
         configParam = config == null ? null : "CONFIG=" + config.toString();
         responseMap = this.submitRequest(reqMethod, reqUrl, (Map)reqHeaders, this.accessToken, configParam);
         break;
      default:
         throw new IllegalArgumentException("Unsupported HTTP method: " + reqMethod);
      }

      httpStatusCode = (String)responseMap.get("httpStatusCode");
      String response = (String)responseMap.get("response");
      JSONObject responseJObj = response.isEmpty() ? new JSONObject() : this.getJSONResponse(response);
      if (!httpStatusCode.startsWith("2") && this.isOAuthExpired(responseJObj)) {
         this.regenerateOAuthToken();
         responseMap = this.submitRequest(reqMethod, reqUrl, (Map)reqHeaders, this.accessToken, configParam);
         httpStatusCode = (String)responseMap.get("httpStatusCode");
         response = (String)responseMap.get("response");
         responseJObj = response.isEmpty() ? new JSONObject() : this.getJSONResponse(response);
      }

      if (!httpStatusCode.startsWith("2")) {
         JSONObject dataObj = responseJObj.getJSONObject("data");
         throw new ServerException(Integer.parseInt(httpStatusCode), dataObj.getInt("errorCode"), dataObj.getString("errorMessage"));
      } else {
         return responseJObj;
      }
   }

   private JSONObject getJSONResponse(String response) throws ParseException {
      try {
         JSONObject responseJObj = new JSONObject(response);
         return responseJObj;
      } catch (Exception var4) {
         throw new ParseException(response, "Exception while parsing the response.", var4);
      }
   }

   private Map<String, String> submitRequest(String reqMethod, String reqUrl, Map<String, String> reqHeaders, String oAuthToken, String reqBody) throws IOException {
      HttpURLConnection urlCon = this.getConnection(reqUrl, reqMethod, reqHeaders, oAuthToken);
      if (reqBody != null && !reqBody.isEmpty()) {
         urlCon.setDoOutput(true);
         OutputStream os = urlCon.getOutputStream();
         Throwable var8 = null;

         try {
            byte[] input = reqBody.getBytes("UTF-8");
            os.write(input, 0, input.length);
         } catch (Throwable var17) {
            var8 = var17;
            throw var17;
         } finally {
            if (os != null) {
               if (var8 != null) {
                  try {
                     os.close();
                  } catch (Throwable var16) {
                     var8.addSuppressed(var16);
                  }
               } else {
                  os.close();
               }
            }

         }
      }

      Map<String, String> respMap = new HashMap();
      respMap.put("httpStatusCode", String.valueOf(urlCon.getResponseCode()));
      respMap.put("response", this.getResponse(urlCon));
      return respMap;
   }

   protected JSONObject sendExportAPIRequest(String endPoint, JSONObject config, Map<String, String> reqHeaders, Object dataHandler) throws ServerException, ParseException, IOException, JSONException {
      if (this.accessToken == null) {
         this.regenerateOAuthToken();
      }

      String params = config == null ? "" : "CONFIG=" + URLEncoder.encode(config.toString(), "UTF-8");
      String reqUrl = this.analyticsServerURL + endPoint + "?" + params;
      Map<String, String> responseMap = this.submitExportRequest(reqUrl, reqHeaders, this.accessToken, dataHandler);
      String httpStatusCode = (String)responseMap.get("httpStatusCode");
      String response = (String)responseMap.get("response");
      JSONObject responseJObj = new JSONObject();
      if (!response.isEmpty()) {
         responseJObj = this.getJSONResponse(response);
      }

      if (!httpStatusCode.startsWith("2") && this.isOAuthExpired(responseJObj)) {
         this.regenerateOAuthToken();
         responseMap = this.submitExportRequest(reqUrl, reqHeaders, this.accessToken, dataHandler);
         httpStatusCode = (String)responseMap.get("httpStatusCode");
         response = (String)responseMap.get("response");
         if (!response.isEmpty()) {
            responseJObj = this.getJSONResponse(response);
         }
      }

      if (!httpStatusCode.startsWith("2")) {
         JSONObject dataObj = responseJObj.getJSONObject("data");
         throw new ServerException(Integer.parseInt(httpStatusCode), dataObj.getInt("errorCode"), dataObj.getString("errorMessage"));
      } else {
         return responseJObj;
      }
   }

   private Map<String, String> submitExportRequest(String reqUrl, Map<String, String> reqHeaders, String oAuthToken, Object dataHandler) throws IOException {
      String reqMethod = "GET";
      HttpURLConnection urlCon = this.getConnection(reqUrl, reqMethod, reqHeaders, oAuthToken);
      String response = "";
      Map<String, String> respMap = new HashMap();
      String responseCode = urlCon.getResponseCode() + "";
      StringBuilder sb = new StringBuilder();
      if (urlCon.getErrorStream() != null) {
         BufferedReader br = null;

         try {
            br = new BufferedReader(new InputStreamReader(urlCon.getErrorStream()));

            int c;
            while((c = br.read()) >= 0) {
               sb.append((char)c);
            }
         } finally {
            if (br != null) {
               br.close();
            }

         }
      } else if (urlCon.getResponseCode() == 200) {
         InputStream is = null;
         Object os = null;

         try {
            is = urlCon.getInputStream();
            if (dataHandler instanceof File) {
               os = new BufferedOutputStream(new FileOutputStream((File)dataHandler));
            } else {
               os = (OutputStream)dataHandler;
            }

            byte[] arr = new byte[1024];
            boolean var14 = false;

            int length;
            while((length = is.read(arr)) > -1) {
               ((OutputStream)os).write(arr, 0, length);
            }
         } finally {
            if (is != null) {
               try {
                  is.close();
               } catch (Exception var28) {
                  throw var28;
               }
            }

            if (os != null) {
               try {
                  ((OutputStream)os).close();
               } catch (Exception var27) {
                  throw var27;
               }
            }

         }
      }

      response = sb.toString();
      respMap.put("httpStatusCode", responseCode);
      respMap.put("response", response);
      return respMap;
   }

   protected JSONObject sendImportAPIRequest(String endPoint, JSONObject config, Map<String, String> reqHeaders, Object dataHandler) throws ServerException, ParseException, IOException, JSONException {
      if (this.accessToken == null) {
         this.regenerateOAuthToken();
      }

      String params = config == null ? "" : "CONFIG=" + URLEncoder.encode(config.toString(), "UTF-8");
      String reqUrl = this.analyticsServerURL + endPoint + "?" + params;
      Map<String, String> responseMap = this.submitImportRequest(reqUrl, reqHeaders, this.accessToken, dataHandler);
      String httpStatusCode = (String)responseMap.get("httpStatusCode");
      String response = (String)responseMap.get("response");
      JSONObject responseJObj = new JSONObject();
      if (!response.isEmpty()) {
         responseJObj = this.getJSONResponse(response);
      }

      if (!httpStatusCode.startsWith("2") && this.isOAuthExpired(responseJObj)) {
         this.regenerateOAuthToken();
         responseMap = this.submitImportRequest(reqUrl, reqHeaders, this.accessToken, dataHandler);
         httpStatusCode = (String)responseMap.get("httpStatusCode");
         response = (String)responseMap.get("response");
         if (!response.isEmpty()) {
            responseJObj = this.getJSONResponse(response);
         }
      }

      if (!httpStatusCode.startsWith("2")) {
         JSONObject dataObj = responseJObj.getJSONObject("data");
         throw new ServerException(Integer.parseInt(httpStatusCode), dataObj.getInt("errorCode"), dataObj.getString("errorMessage"));
      } else {
         return responseJObj;
      }
   }

   protected JSONObject sendBatchImportAPIRequest(String endPoint, Map<String, String> reqHeaders, Object dataHandler) throws ServerException, ParseException, IOException, JSONException {
      if (this.accessToken == null) {
         this.regenerateOAuthToken();
      }

      String reqUrl = this.analyticsServerURL + endPoint;
      Map<String, String> responseMap = this.submitImportRequest(reqUrl, reqHeaders, this.accessToken, dataHandler);
      String httpStatusCode = (String)responseMap.get("httpStatusCode");
      String response = (String)responseMap.get("response");
      JSONObject responseJObj = new JSONObject();
      if (!response.isEmpty()) {
         responseJObj = this.getJSONResponse(response);
      }

      if (!httpStatusCode.startsWith("2") && this.isOAuthExpired(responseJObj)) {
         this.regenerateOAuthToken();
         responseMap = this.submitImportRequest(reqUrl, reqHeaders, this.accessToken, dataHandler);
         httpStatusCode = (String)responseMap.get("httpStatusCode");
         response = (String)responseMap.get("response");
         if (!response.isEmpty()) {
            responseJObj = this.getJSONResponse(response);
         }
      }

      if (!httpStatusCode.startsWith("2")) {
         JSONObject dataObj = responseJObj.getJSONObject("data");
         throw new ServerException(Integer.parseInt(httpStatusCode), dataObj.getInt("errorCode"), dataObj.getString("errorMessage"));
      } else {
         return responseJObj;
      }
   }

   private Map<String, String> submitImportRequest(String reqUrl, Map<String, String> reqHeaders, String accessToken, Object dataHandler) throws IOException, ServerException, ParseException {
      HttpURLConnection urlCon = this.getConnection(reqUrl, "POST", reqHeaders, accessToken);
      urlCon.setRequestMethod("POST");
      urlCon.setDoOutput(true);
      String boundary = UUID.randomUUID().toString();
      urlCon.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
      OutputStream outputStream = urlCon.getOutputStream();
      PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
      Throwable var9 = null;

      try {
         if (dataHandler instanceof File) {
            File file = (File)dataHandler;
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"FILE\"; filename=\"" + file.getName() + "\"").append("\r\n");
            writer.append("Content-Type: application/octet-stream").append("\r\n\r\n");
            writer.flush();
            FileInputStream inputStream = new FileInputStream(file);
            Throwable var12 = null;

            try {
               byte[] buffer = new byte[4096];

               int bytesRead;
               while((bytesRead = inputStream.read(buffer)) != -1) {
                  outputStream.write(buffer, 0, bytesRead);
               }

               outputStream.flush();
               inputStream.close();
               writer.append("\r\n");
               writer.flush();
            } catch (Throwable var36) {
               var12 = var36;
               throw var36;
            } finally {
               if (inputStream != null) {
                  if (var12 != null) {
                     try {
                        inputStream.close();
                     } catch (Throwable var35) {
                        var12.addSuppressed(var35);
                     }
                  } else {
                     inputStream.close();
                  }
               }

            }
         } else if (dataHandler instanceof String) {
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"DATA\"").append("\r\n\r\n");
            writer.append((String)dataHandler).append("\r\n");
            writer.flush();
         }

         writer.append("--" + boundary + "--").append("\r\n");
      } catch (Throwable var38) {
         var9 = var38;
         throw var38;
      } finally {
         if (writer != null) {
            if (var9 != null) {
               try {
                  writer.close();
               } catch (Throwable var34) {
                  var9.addSuppressed(var34);
               }
            } else {
               writer.close();
            }
         }

      }

      Map<String, String> respMap = new HashMap();
      respMap.put("httpStatusCode", String.valueOf(urlCon.getResponseCode()));
      respMap.put("response", this.getResponse(urlCon));
      return respMap;
   }

   private String getResponse(HttpURLConnection urlCon) throws IOException {
      BufferedReader br = null;
      String response = "";

      try {
         if (urlCon.getErrorStream() != null) {
            br = new BufferedReader(new InputStreamReader(urlCon.getErrorStream()));
         } else {
            br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
         }

         StringBuilder sb = new StringBuilder();

         int c;
         while((c = br.read()) >= 0) {
            sb.append((char)c);
         }

         response = sb.toString();
         return response;
      } finally {
         if (br != null) {
            br.close();
         }

      }
   }

   private HttpURLConnection getConnection(String reqUrl, String reqMethod, Map<String, String> reqHeaders, String oAuthToken) throws IOException {
      URL url = new URL(reqUrl);
      HttpURLConnection urlCon;
      if (this.proxy) {
         Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(this.proxyHost, this.proxyPort));
         urlCon = (HttpURLConnection)url.openConnection(proxy);
      } else {
         urlCon = (HttpURLConnection)url.openConnection();
      }

      urlCon.setRequestMethod(reqMethod);
      urlCon.setConnectTimeout(this.connectionTimeout);
      urlCon.setReadTimeout(this.readTimeout);
      urlCon.setUseCaches(false);
      urlCon.setInstanceFollowRedirects(false);
      Iterator var8;
      String key;
      Set headerKeys;
      if (this.headers != null) {
         headerKeys = this.headers.keySet();
         var8 = headerKeys.iterator();

         while(var8.hasNext()) {
            key = (String)var8.next();
            urlCon.setRequestProperty(key, (String)this.headers.get(key));
         }
      }

      if (reqHeaders != null) {
         headerKeys = reqHeaders.keySet();
         var8 = headerKeys.iterator();

         while(var8.hasNext()) {
            key = (String)var8.next();
            urlCon.setRequestProperty(key, (String)reqHeaders.get(key));
         }
      }

      StringBuilder var10002 = (new StringBuilder()).append("Analytics Java Client v");
      this.getClass();
      urlCon.setRequestProperty("User-Agent", var10002.append("2.6.0").toString());
      if (oAuthToken != null) {
         urlCon.setRequestProperty("Authorization", "Zoho-oauthtoken " + oAuthToken);
      }

      if (reqMethod.equals("POST") || reqMethod.equals("PUT")) {
         urlCon.setDoOutput(true);
      }

      if (this.proxy) {
         Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(AnalyticsClient.this.proxyUsername, AnalyticsClient.this.proxyPassword.toCharArray());
            }
         });
      }

      return urlCon;
   }

   public void setHeaders(String key, String value) throws Exception {
      if (key != null && value != null) {
         if (!key.matches("^[A-Za-z0-9-]+$")) {
            throw new IllegalArgumentException("Invalid header key.");
         } else if (!value.contains("\n") && !value.contains("\r")) {
            this.headers.put(key, value);
         } else {
            throw new IllegalArgumentException("Header value cannot contain newline characters.");
         }
      } else {
         throw new IllegalArgumentException("Key or value cannot be null.");
      }
   }

   private boolean isOAuthExpired(JSONObject response) {
      try {
         int errorCode = response.getJSONObject("data").getInt("errorCode");
         return errorCode == 8535;
      } catch (Exception var3) {
         return false;
      }
   }

   private void regenerateOAuthToken() throws ServerException, ParseException, IOException, JSONException {
      if (this.clientId == null) {
         if (this.accessToken == null) {
            this.accessToken = "1000.00000000000000000000000000000000.00000000000000000000000000000000";
         }

      } else {
         String reqUrl = this.accountsServerURL + "/oauth/v2/token";
         String params = "client_id=" + this.clientId + "&client_secret=" + this.clientSecret + "&refresh_token=" + this.refreshToken + "&grant_type=refresh_token";
         Map<String, String> reqHeaders = new HashMap();
         reqHeaders.put("Content-Type", "application/x-www-form-urlencoded");
         Map<String, String> responseObj = this.submitRequest("POST", reqUrl, reqHeaders, (String)null, params);
         String statusCode = (String)responseObj.get("httpStatusCode");
         JSONObject respJObj = this.getJSONResponse((String)responseObj.get("response"));
         if (statusCode.startsWith("2") && respJObj.has("access_token")) {
            this.accessToken = respJObj.getString("access_token");
         } else {
            throw new ServerException(Integer.parseInt(statusCode), 0, respJObj.getString("error"));
         }
      }
   }

   public JSONObject importDataInBatches(String endPoint, JSONObject config, Map<String, String> reqHeaders, File csvFile, int batchSize, JSONObject toolConfig) throws IOException, ServerException, ParseException, Exception {
      LOGGER.log(Level.FINEST, "Starting Import Data In Batches {0}", csvFile.getAbsolutePath());
      LOGGER.log(Level.FINEST, "File size : {0}", csvFile.length());
      BufferedReader reader = null;
      FileInputStream fis = null;
      StringBuilder buf = null;
      String batchKey = "start";
      String jobId = "";
      boolean isLastBatch = false;
      int bufSize = 10000;
      int lineCount = 100;
      String columnHeaders = null;
      boolean isFirstRowHeader = toolConfig.optBoolean("isFirstRowHeader", true);
      if (!isFirstRowHeader) {
         JSONArray colHeaders = toolConfig.optJSONArray("columnHeaders");
         if (colHeaders == null || colHeaders.length() <= 0) {
            throw new Exception("columnHeaders cannot be empty");
         }

         columnHeaders = "";

         for(int i = 0; i < colHeaders.length(); ++i) {
            columnHeaders = columnHeaders + colHeaders.getString(i).trim() + ",";
         }

         columnHeaders = columnHeaders.substring(0, columnHeaders.lastIndexOf(",") - 1);
      }

      try {
         String curLine = null;
         AnalyticsClient.BOMHandler bomHandler = new AnalyticsClient.BOMHandler();
         int length = bomHandler.handleBOMAndSetEncoding(new HashMap(), csvFile);
         fis = new FileInputStream(csvFile);
         if (length != 0) {
            byte[] bom = new byte[4];
            fis.read(bom, 0, length);
         }

         reader = new BufferedReader(new InputStreamReader(fis, bomHandler.fileEncoding));
         if (columnHeaders == null) {
            columnHeaders = reader.readLine();
         }

         while(true) {
            int currentBatchLineCount = 0;
            buf = new StringBuilder(bufSize);
            this.writeLine(buf, columnHeaders);

            do {
               if (curLine != null) {
                  this.writeLine(buf, curLine);
               } else {
                  curLine = reader.readLine();
                  if (curLine == null) {
                     isLastBatch = true;
                     break;
                  }

                  this.writeLine(buf, curLine);
               }

               curLine = null;
               ++currentBatchLineCount;
            } while(currentBatchLineCount < lineCount);

            curLine = reader.readLine();
            if (curLine == null) {
               isLastBatch = true;
            }

            if (currentBatchLineCount > 0 || columnHeaders != null) {
               config.put("batchKey", batchKey);
               config.put("isLastBatch", isLastBatch);
               bufSize = buf.length();
               LOGGER.log(Level.FINEST, "Lines read : {0}", currentBatchLineCount);
               LOGGER.log(Level.FINEST, "Size : {0}", bufSize);
               File tempZippedFile = getZippedFile(csvFile.getName(), buf.toString());

               try {
                  JSONObject response = this.sendImportAPIRequest(endPoint, config, reqHeaders, tempZippedFile);
                  batchKey = response.getJSONObject("data").getString("batchKey");
                  jobId = response.getJSONObject("data").getString("jobId");
                  lineCount = batchSize;
                  LOGGER.log(Level.FINEST, "Finished current batch");
               } catch (Exception var42) {
                  throw var42;
               } finally {
                  if (tempZippedFile.exists()) {
                     tempZippedFile.delete();
                  }

               }
            }

            if (curLine == null) {
               break;
            }

            try {
               Thread.sleep(2000L);
            } catch (Exception var43) {
               throw new RuntimeException(var43);
            }
         }
      } finally {
         if (reader != null) {
            try {
               reader.close();
            } catch (Exception var41) {
               LOGGER.log(Level.SEVERE, (String)null, var41);
            }
         }

         if (fis != null) {
            try {
               fis.close();
            } catch (Exception var40) {
               LOGGER.log(Level.SEVERE, (String)null, var40);
            }
         }

      }

      LOGGER.log(Level.FINEST, "Finished importing {0}", csvFile.getAbsolutePath());
      JSONObject response = new JSONObject();
      response.put("batchKey", batchKey);
      response.put("jobId", jobId);
      return response;
   }

   protected void writeLine(Appendable writer, String curLine) throws IOException {
      writer.append(curLine);
      writer.append('\n');
   }

   static File getZippedFile(String csvFileName, String csvContent) throws IOException {
      byte[] buf = csvContent.getBytes("UTF-8");
      File tempFile = File.createTempFile(csvFileName, ".zip");
      tempFile.deleteOnExit();
      ZipOutputStream zos = null;

      File var5;
      try {
         zos = new ZipOutputStream(new FileOutputStream(tempFile));
         zos.setLevel(9);
         zos.putNextEntry(new ZipEntry(csvFileName));
         zos.write(buf);
         var5 = tempFile;
      } finally {
         close(zos);
      }

      return var5;
   }

   public static void close(Closeable... args) {
      Closeable[] var1 = args;
      int var2 = args.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Closeable c = var1[var3];
         if (c != null) {
            try {
               c.close();
            } catch (Exception var6) {
               LOGGER.log(Level.SEVERE, (String)null, var6);
            }
         }
      }

   }

   class BOMHandler {
      String fileEncoding = "UTF-8";
      int length = 0;

      public BOMHandler() {
      }

      protected int handleBOMAndSetEncoding(Map importConfig, File file) throws Exception {
         String fileEncode = (String)importConfig.get("ZOHO_FILE_ENCODING");
         if (fileEncode != null) {
            this.fileEncoding = fileEncode;
         }

         String removeBOM = (String)importConfig.get("ZOHO_REMOVE_BOM");
         if (removeBOM != null && "true".equalsIgnoreCase(removeBOM)) {
            this.setFileEncodingAndRemoveBOM(file);
         }

         return this.length;
      }

      protected void setFileEncodingAndRemoveBOM(File file) throws Exception {
         InputStream is = null;
         byte[] bomByte = new byte[4];

         try {
            is = new BufferedInputStream(new FileInputStream(file));
            is.read(bomByte, 0, 4);
         } catch (Exception var8) {
            throw var8;
         } finally {
            if (is != null) {
               is.close();
            }

         }

         if (bomByte[0] == -2 && bomByte[1] == -1) {
            this.fileEncoding = "UTF-16BE";
            this.length = 2;
         } else if (bomByte[0] == -1 && bomByte[1] == -2) {
            this.fileEncoding = "UTF-16LE";
            this.length = 2;
         } else if (bomByte[0] == 0 && bomByte[1] == 0 && bomByte[2] == -2 && bomByte[3] == -1) {
            this.fileEncoding = "UTF-32BE";
            this.length = 4;
         } else if (bomByte[0] == -1 && bomByte[1] == -2 && bomByte[2] == 0 && bomByte[3] == 0) {
            this.fileEncoding = "UTF-32LE";
            this.length = 4;
         } else if (bomByte[0] == -17 && bomByte[1] == -69 && bomByte[2] == -65) {
            this.fileEncoding = "UTF-8";
            this.length = 3;
         }

      }
   }
}
