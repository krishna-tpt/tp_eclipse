package test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class Util {

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
        System.out.println("Authenticated, uid = " + uid);

        // Step 2: client for model calls
        XmlRpcClientConfigImpl objConfig = new XmlRpcClientConfigImpl();
        objConfig.setServerURL(new URL(BASE_URL + "/xmlrpc/2/object"));
        XmlRpcClient models = new XmlRpcClient();
        models.setConfig(objConfig);

        // Step 3: find the project id by name
        Map<String, Object> projectOpts = new HashMap<>();
        projectOpts.put("fields", Arrays.asList("id", "name"));
        projectOpts.put("limit", 5);

        Object[] projects = (Object[]) models.execute("execute_kw", Arrays.asList(
                DB, uid, API_KEY,
                "project.project", "search_read",
                Arrays.asList(Arrays.asList(Arrays.asList("name", "ilike", "My Project"))),
                projectOpts));

        if (projects.length == 0) {
            throw new RuntimeException("Project not found");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> project = (Map<String, Object>) projects[0];
        int projectId = (Integer) project.get("id");
        System.out.println("Project: " + project.get("name") + " (id " + projectId + ")");

        // Step 4: find a task inside that project (optional)
        Map<String, Object> taskOpts = new HashMap<>();
        taskOpts.put("fields", Arrays.asList("id", "name"));
        taskOpts.put("limit", 10);

        Object[] tasks = (Object[]) models.execute("execute_kw", Arrays.asList(
                DB, uid, API_KEY,
                "project.task", "search_read",
                Arrays.asList(Arrays.asList(Arrays.asList("project_id", "=", projectId))),
                taskOpts));

        // Step 5: build the timesheet line values
        Map<String, Object> vals = new HashMap<>();
        vals.put("name", "Worked on DBeaver export setup"); // Description
        vals.put("date", "2026-11-02");                     // YYYY-MM-DD
        vals.put("unit_amount", 2.5);                       // Hours (2.5 = 2h 30m)
        vals.put("project_id", projectId);

        if (tasks.length > 0) {
            @SuppressWarnings("unchecked")
            Map<String, Object> task = (Map<String, Object>) tasks[0];
            vals.put("task_id", task.get("id"));
        }

        // Step 6: create the timesheet entry
        Object newId = models.execute("execute_kw", Arrays.asList(
                DB, uid, API_KEY,
                "account.analytic.line", "create",
                Arrays.asList(vals)));

        System.out.println("Timesheet created, id = " + newId);
    }
}