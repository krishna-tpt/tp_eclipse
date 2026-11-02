package test;


import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class InsertTS {

    // Odoo connection details
	static final String BASE_URL = "https://ten10.in";
    static final String DB       = "intra3live";
    static final String USERNAME = "krishnakumar.rangasamy@tenthplanet.co";
    static final String API_KEY  = "64e66714c5cd69cdbfee2c635f1cbd0b3de99112";

    // Timesheet values
    static final int    PROJECT_ID  = 27;      // Michelin Camso Support
    static final int    TASK_ID     = 19017;   // FOP Integration
    static final int    ACTIVITY_ID = 29;      // Implementation (mail.activity.type)

    static final String DESCRIPTION = "FOP Integration development";
    static final String DATE        = "2026-11-02";  // YYYY-MM-DD
    static final double HOURS       = 2.5;           // Decimal hours (2.5 = 2h 30m)

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

        // Step 3: build the timesheet line values
        Map<String, Object> vals = new HashMap<>();
        vals.put("name", DESCRIPTION);
        vals.put("date", DATE);
        vals.put("unit_amount", HOURS);
        vals.put("project_id", PROJECT_ID);
        vals.put("task_id", TASK_ID);
        vals.put("activity_id", ACTIVITY_ID);   // many2one expects the record ID

        // Step 4: create the timesheet entry
        Object newId = models.execute("execute_kw", Arrays.asList(
                DB, uid, API_KEY,
                "account.analytic.line", "create",
                Arrays.asList(vals)));

        System.out.println("Timesheet created, id = " + newId);
    }
}