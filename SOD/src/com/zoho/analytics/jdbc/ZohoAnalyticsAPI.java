package com.zoho.analytics.jdbc;

import com.zoho.analytics.client.AnalyticsClient;
import com.zoho.analytics.client.BulkAPI;
import com.zoho.analytics.client.OrgAPI;
import com.zoho.analytics.client.ParseException;
import com.zoho.analytics.client.ServerException;
import com.zoho.analytics.client.ViewAPI;
import com.zoho.analytics.client.WorkspaceAPI;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ZohoAnalyticsAPI {
	AnalyticsClient ac;

	ZohoAnalyticsAPI(Properties props) throws Exception {
		try {
			String hostName = (String) props.get("HOST_NAME");
			String clientId = (String) props.get("CLIENT_ID");
			String clientSecret = (String) props.get("CLIENT_SECRET");
			String refreshToken = (String) props.get("REFRESH_TOKEN");
			this.ac = new AnalyticsClient(clientId, clientSecret, refreshToken);
			this.ac.setAccountsServerURL(ZohoAnalyticsJDBCUtil.getAccountsURL(hostName, props));
			this.ac.setAnalyticsServerURL(ZohoAnalyticsJDBCUtil.getAnalyticsURL(hostName, props));
			this.ac.setHeaders("X-Analytics-JDBC-Version", "v2.7.0");
			String proxyServer = (String) props.get("PROXYSERVER");
			if (proxyServer != null) {
				this.ac.setProxy(proxyServer, Integer.parseInt((String) props.get("PROXYPORT")),
						(String) props.get("PROXYUSERNAME"), (String) props.get("PROXYPASSWORD"));
			}

		} catch (Exception var7) {
			throw var7;
		}
	}

	JSONArray fetchWorkspaceList(String orgId) throws Exception {
		OrgAPI org = this.ac.getOrgInstance(Long.parseLong(orgId));
		return org.getOrgWorkspaces();
	}

	void fetchAnalyticsData(long organizationId, long workspaceId, String sql, ByteArrayOutputStream bos)
			throws Exception {
		BulkAPI bulk = this.ac.getBulkInstance(organizationId, workspaceId);
		JSONObject config = new JSONObject();
		config.put("includeMetadata", true);
		bulk.exportDataUsingSQL(sql, "csv", bos, config);
	}

	JSONObject fetchWorkspaceMetaData(String orgId, String workspaceId) throws SQLException {
		try {
			WorkspaceAPI ws = this.ac.getWorkspaceInstance(Long.valueOf(orgId), Long.valueOf(workspaceId));
			return ws.getWorkspaceMetadata();
		} catch (ServerException var4) {
			throw (SQLException) (new SQLException(var4.getErrorMessage(), (String) null, var4.getErrorCode()))
					.initCause(var4);
		} catch (Exception var5) {
			throw (SQLException) (new SQLException(var5.getMessage())).initCause(var5);
		}
	}

	JSONObject fetchViewMetaData(long viewId) throws SQLException {
		try {
			JSONObject config = new JSONObject();
			config.put("withInvolvedMetaInfo", true);
			return this.ac.getViewDetails(viewId, config);
		} catch (ServerException var4) {
			throw (SQLException) (new SQLException(var4.getErrorMessage(), (String) null, var4.getErrorCode()))
					.initCause(var4);
		} catch (Exception var5) {
			throw (SQLException) (new SQLException(var5.getMessage())).initCause(var5);
		}
	}

	void addRows(long organisationId, long workspaceId, long viewId, JSONObject columnValues)
			throws ServerException, ParseException, JSONException, IOException {
		ViewAPI view = this.ac.getViewInstance(organisationId, workspaceId, viewId);
		view.addRow(columnValues, (JSONObject) null);
	}

	int deleteRows(long organisationId, long workspaceId, long viewId, String criteria, JSONObject config)
			throws ServerException, ParseException, IOException, JSONException {
		ViewAPI view = this.ac.getViewInstance(organisationId, workspaceId, viewId);
		return view.deleteRow(criteria, config);
	}

	int updateRows(long organisationId, long workspaceId, long viewId, JSONObject columnData, String criteriaValues,
			JSONObject config) throws ServerException, ParseException, IOException, JSONException {
		ViewAPI view = this.ac.getViewInstance(organisationId, workspaceId, viewId);
		JSONObject result = view.updateRow(columnData, criteriaValues, config);
		return result.getInt("updatedRows");
	}

	long createTable(long organisationId, long workspaceId, JSONObject tableDetails)
			throws ServerException, ParseException, IOException {
		WorkspaceAPI workspace = this.ac.getWorkspaceInstance(organisationId, workspaceId);
		return workspace.createTable(tableDetails);
	}

	long createQueryTable(long organisationId, long workspaceId, String sqlQuery, String queryTableName)
			throws ServerException, ParseException, IOException {
		WorkspaceAPI workspace = this.ac.getWorkspaceInstance(organisationId, workspaceId);
		return workspace.createQueryTable(sqlQuery, queryTableName, (JSONObject) null);
	}

	void renameColumn(long organisationId, long workspaceId, long viewId, long columnId, String columnName)
			throws Exception {
		ViewAPI view = this.ac.getViewInstance(organisationId, workspaceId, viewId);
		view.renameColumn(columnId, columnName);
	}

	void addLookup(long organisationId, long workspaceId, long viewId, long columnId, long refViewId, long refColumnId)
			throws Exception {
		ViewAPI view = this.ac.getViewInstance(organisationId, workspaceId, viewId);
		view.addLookup(columnId, refViewId, refColumnId, (JSONObject) null);
	}

	void deleteView(long organisationId, long workspaceId, long viewId) throws Exception {
		ViewAPI view = this.ac.getViewInstance(organisationId, workspaceId, viewId);
		view.delete((JSONObject) null);
	}

	void addColumn(long organisationId, long workspaceId, long viewId, String columnName, String dataType,
			JSONObject config) throws ServerException, ParseException, IOException {
		ViewAPI view = this.ac.getViewInstance(organisationId, workspaceId, viewId);
		view.addColumn(columnName, dataType, config);
	}

	void renameView(long orgId, long workspaceId, long viewId, String newTableName)
			throws ServerException, ParseException, IOException {
		ViewAPI view = this.ac.getViewInstance(orgId, workspaceId, viewId);
		view.rename(newTableName, (JSONObject) null);
	}

	void deleteColumn(long orgId, long workspaceId, long viewId, long columnId)
			throws ServerException, ParseException, IOException {
		ViewAPI view = this.ac.getViewInstance(orgId, workspaceId, viewId);
		view.deleteColumn(columnId, (JSONObject) null);
	}
}
