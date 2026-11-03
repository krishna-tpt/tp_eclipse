package test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class ListTasks {

	// Odoo connection details
	static final String BASE_URL = "https://ten10.in";
    static final String DB       = "intra3live";
    static final String USERNAME = "krishnakumar.rangasamy@tenthplanet.co";
    static final String API_KEY  = "a3ffd1a87b59845f84d73f62d34c3863fa64b5ed";
	static final int PROJECT_ID = 27; // Michelin Camso Support

	// Convert a many2one value ([id, name] or false) into a readable name
	static String m2oName(Object value) {
		if (value instanceof Object[]) {
			Object[] arr = (Object[]) value;
			return String.valueOf(arr[1]);
		}
		return "-";
	}

	// Convert the technical state code into a readable label
	static String stateLabel(Object code) {
		if (code == null)
			return "-";
		switch (String.valueOf(code)) {
		case "01_in_progress":
			return "In Progress";
		case "02_changes_requested":
			return "Changes Requested";
		case "03_approved":
			return "Approved";
		case "04_waiting_normal":
			return "Waiting";
		case "1_done":
			return "Done";
		case "1_canceled":
			return "Canceled";
		default:
			return String.valueOf(code);
		}
	}

	public static void main(String[] args) throws Exception {

		// Step 1: authenticate and get the user id (uid)
		XmlRpcClientConfigImpl commonConfig = new XmlRpcClientConfigImpl();
		commonConfig.setServerURL(new URL(BASE_URL + "/xmlrpc/2/common"));
		XmlRpcClient commonClient = new XmlRpcClient();
		commonClient.setConfig(commonConfig);

		Object uidObj = commonClient.execute("authenticate",
				Arrays.asList(DB, USERNAME, API_KEY, Collections.emptyMap()));
		if (!(uidObj instanceof Integer)) {
			throw new RuntimeException("Authentication failed: check DB, login and API key");
		}
		int uid = (Integer) uidObj;

		// Step 2: client for model calls
		XmlRpcClientConfigImpl objConfig = new XmlRpcClientConfigImpl();
		objConfig.setServerURL(new URL(BASE_URL + "/xmlrpc/2/object"));
		XmlRpcClient models = new XmlRpcClient();
		models.setConfig(objConfig);

		// Step 3: read tasks of the project, ordered by stage first, then by state
		Map<String, Object> opts = new HashMap<>();
		opts.put("fields", Arrays.asList("id", "name", "stage_id", "state"));
		opts.put("order", "stage_id asc, state asc, id asc");

		Object[] tasks = (Object[]) models.execute("execute_kw", Arrays.asList(DB, uid, API_KEY, "project.task",
				"search_read", Arrays.asList(Arrays.asList(Arrays.asList("project_id", "=", PROJECT_ID))), opts));

		System.out.println("Tasks found: " + tasks.length);
		System.out.println("ID\tStage\t\tState\t\tName");

		for (Object t : tasks) {
			@SuppressWarnings("unchecked")
			Map<String, Object> task = (Map<String, Object>) t;
			System.out.println(task.get("id") + "\t" + m2oName(task.get("stage_id")) + "\t\t"
					+ stateLabel(task.get("state")) + "\t\t" + task.get("name"));
		}
	}
}