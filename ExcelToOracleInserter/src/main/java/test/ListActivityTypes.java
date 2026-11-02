package test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class ListActivityTypes {

    // Odoo connection details
	static final String BASE_URL = "https://ten10.in";
    static final String DB       = "intra3live";
    static final String USERNAME = "krishnakumar.rangasamy@tenthplanet.co";
    static final String API_KEY  = "64e66714c5cd69cdbfee2c635f1cbd0b3de99112";

    static int uid;
    static XmlRpcClient models;

    // Small helper around execute_kw
    static Object call(String model, String method, Object[] args, Map<String, Object> kwargs) throws Exception {
        return models.execute("execute_kw", Arrays.asList(DB, uid, API_KEY, model, method, args, kwargs));
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
        uid = (Integer) uidObj;

        // Step 2: client for model calls
        XmlRpcClientConfigImpl objConfig = new XmlRpcClientConfigImpl();
        objConfig.setServerURL(new URL(BASE_URL + "/xmlrpc/2/object"));
        models = new XmlRpcClient();
        models.setConfig(objConfig);

        // Step 3: read the field definitions of the timesheet model
        Map<String, Object> kw = new HashMap<>();
        kw.put("attributes", Arrays.asList("string", "type", "relation", "selection", "required", "readonly"));

        @SuppressWarnings("unchecked")
        Map<String, Object> fields = (Map<String, Object>) call(
                "account.analytic.line", "fields_get", new Object[]{Collections.emptyList()}, kw);

        // Sort by technical name for a clean output
        Map<String, Object> sorted = new TreeMap<>(fields);

        System.out.println("=== Candidate fields (custom x_* or name contains activity/type) ===");

        for (Map.Entry<String, Object> e : sorted.entrySet()) {
            String name = e.getKey();
            @SuppressWarnings("unchecked")
            Map<String, Object> def = (Map<String, Object>) e.getValue();
            String label = String.valueOf(def.get("string"));
            String type  = String.valueOf(def.get("type"));

            // Keep only custom fields or fields that look like an activity type
            String lower = (name + " " + label).toLowerCase();
            boolean candidate = name.startsWith("x_")
                    || lower.contains("activity")
                    || lower.contains("type");
            if (!candidate) continue;

            System.out.println("\nField   : " + name);
            System.out.println("Label   : " + label);
            System.out.println("Type    : " + type);
            System.out.println("Required: " + def.get("required") + " | Readonly: " + def.get("readonly"));

            // Case 1: selection field -> print the options (key = label)
            if ("selection".equals(type) && def.get("selection") instanceof Object[]) {
                System.out.println("Values  :");
                for (Object o : (Object[]) def.get("selection")) {
                    Object[] pair = (Object[]) o;
                    System.out.println("   " + pair[0] + " = " + pair[1]);
                }
            }

            // Case 2: many2one field -> list the records of the related model
            if ("many2one".equals(type) && def.get("relation") != null) {
                String relation = String.valueOf(def.get("relation"));
                System.out.println("Relation: " + relation);

                Map<String, Object> opts = new HashMap<>();
                opts.put("fields", Arrays.asList("id", "display_name"));
                opts.put("limit", 100);

                try {
                    Object[] recs = (Object[]) call(relation, "search_read",
                            new Object[]{Collections.emptyList()}, opts);
                    System.out.println("Values  :");
                    for (Object r : recs) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> rec = (Map<String, Object>) r;
                        System.out.println("   " + rec.get("id") + " = " + rec.get("display_name"));
                    }
                } catch (Exception ex) {
                    // The user may not have read access to the related model
                    System.out.println("   (cannot read " + relation + ": " + ex.getMessage() + ")");
                }
            }
        }
    }
}