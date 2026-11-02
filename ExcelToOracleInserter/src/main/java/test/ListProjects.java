package test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class ListProjects {

    // Odoo connection details
	static final String BASE_URL = "https://ten10.in";
    static final String DB       = "intra3live";
    static final String USERNAME = "krishnakumar.rangasamy@tenthplanet.co";
    static final String API_KEY  = "64e66714c5cd69cdbfee2c635f1cbd0b3de99112";

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

        // Step 3: list all projects visible to this user (empty domain = no extra filter)
        Map<String, Object> opts = new HashMap<>();
        opts.put("fields", Arrays.asList("id", "name", "allow_timesheets"));
        opts.put("order", "name asc");

        Object[] projects = (Object[]) models.execute("execute_kw", Arrays.asList(
                DB, uid, API_KEY,
                "project.project", "search_read",
                Arrays.asList(Collections.emptyList()),   // empty domain
                opts));

        System.out.println("Projects accessible: " + projects.length);
        System.out.println("ID\tTimesheet\tName");

        for (Object p : projects) {
            @SuppressWarnings("unchecked")
            Map<String, Object> project = (Map<String, Object>) p;
            System.out.println(project.get("id") + "\t"
                    + project.get("allow_timesheets") + "\t\t"
                    + project.get("name"));
        }
    }
}