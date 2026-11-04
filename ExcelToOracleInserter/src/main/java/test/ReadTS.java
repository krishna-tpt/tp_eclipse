package test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class ReadTS {

    // Odoo connection details
	static final String BASE_URL = "https://ten10.in";
    static final String DB       = "intra3live";
    static final String USERNAME = "krishnakumar.rangasamy@tenthplanet.co";
    static final String API_KEY  = "a3ffd1a87b59845f84d73f62d34c3863fa64b5ed";

    static final String FROM_DATE   = "2026-11-01";  // Read timesheets from this date onwards
    static final int    DETAIL_ROWS = 10;             // How many entries to dump with all fields

    // Convert any Odoo value into readable text
    static String show(Object v) {
        if (v instanceof Object[]) {
            // many2one comes as [id, name], x2many as a list of ids
            return Arrays.deepToString((Object[]) v);
        }
        return String.valueOf(v);
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

        // Step 3: my timesheets from FROM_DATE (no "fields" option = read ALL fields)
        Map<String, Object> opts = new HashMap<>();
        opts.put("order", "date desc, id desc");
        opts.put("limit", 20);

        Object[] rows = (Object[]) models.execute("execute_kw", Arrays.asList(
                DB, uid, API_KEY,
                "account.analytic.line", "search_read",
                Arrays.asList(Arrays.asList(
                        Arrays.asList("date", ">=", FROM_DATE),
                        Arrays.asList("user_id", "=", uid))),
                opts));

        System.out.println("Timesheets found: " + rows.length);

        // Part A: summary table
        System.out.println("\n=== A) Summary ===");
        System.out.println("ID | Date | Hours | Project | Task | Description");
        for (Object r : rows) {
            @SuppressWarnings("unchecked")
            Map<String, Object> row = (Map<String, Object>) r;
            System.out.println(row.get("id") + " | " + row.get("date") + " | "
                    + row.get("unit_amount") + " | " + show(row.get("project_id")) + " | "
                    + show(row.get("task_id")) + " | " + row.get("name"));
        }

        // Part B: full dump of the first few entries (only non-empty fields)
        System.out.println("\n=== B) Full details (non-empty fields only) ===");
        int count = 0;
        for (Object r : rows) {
            if (count++ >= DETAIL_ROWS) break;
            @SuppressWarnings("unchecked")
            Map<String, Object> row = new TreeMap<>((Map<String, Object>) r);

            System.out.println("\n--- Timesheet id " + row.get("id") + " ---");
            for (Map.Entry<String, Object> e : row.entrySet()) {
                Object v = e.getValue();

                // Skip empty values and noisy technical fields
                if (v == null || Boolean.FALSE.equals(v)) continue;
                if (v instanceof Object[] && ((Object[]) v).length == 0) continue;
                String key = e.getKey();
                if (key.startsWith("message_") || key.startsWith("activity_")
                        || key.equals("write_date") || key.equals("create_date")
                        || key.equals("__last_update") || key.startsWith("website_")) continue;

                // Mark custom fields so they are easy to spot
                String mark = key.startsWith("x_") ? "  <== custom field" : "";
                System.out.println(key + " = " + show(v) + mark);
            }
        }
    }
}