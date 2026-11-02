package test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class FindActivityField2 {

    // Odoo connection details
	static final String BASE_URL = "https://ten10.in";
    static final String DB       = "intra3live";
    static final String USERNAME = "krishnakumar.rangasamy@tenthplanet.co";
    static final String API_KEY  = "64e66714c5cd69cdbfee2c635f1cbd0b3de99112";

    static final int    SAMPLE_TS_ID   = 40844;           // Existing timesheet to inspect
    static final int    ACTIVITY_ID    = 29;              // Known activity type id
    static final String ACTIVITY_VALUE = "Implementation"; // Known activity type name

    static int uid;
    static XmlRpcClient models;

    // Small helper around execute_kw
    static Object call(String model, String method, Object[] args, Map<String, Object> kw) throws Exception {
        return models.execute("execute_kw", Arrays.asList(DB, uid, API_KEY, model, method, args, kw));
    }

    public static void main(String[] args) throws Exception {

        // Step 1: authenticate
        XmlRpcClientConfigImpl commonConfig = new XmlRpcClientConfigImpl();
        commonConfig.setServerURL(new URL(BASE_URL + "/xmlrpc/2/common"));
        XmlRpcClient commonClient = new XmlRpcClient();
        commonClient.setConfig(commonConfig);
        uid = (Integer) commonClient.execute("authenticate",
                Arrays.asList(DB, USERNAME, API_KEY, Collections.emptyMap()));

        XmlRpcClientConfigImpl objConfig = new XmlRpcClientConfigImpl();
        objConfig.setServerURL(new URL(BASE_URL + "/xmlrpc/2/object"));
        models = new XmlRpcClient();
        models.setConfig(objConfig);

        // Step 2: all field definitions of the timesheet model (NO name filtering)
        Map<String, Object> kw = new HashMap<>();
        kw.put("attributes", Arrays.asList("string", "type", "relation", "store"));
        @SuppressWarnings("unchecked")
        Map<String, Object> fields = new TreeMap<>((Map<String, Object>) call(
                "account.analytic.line", "fields_get", new Object[]{Collections.emptyList()}, kw));

        // Step 3: read the sample timesheet once (all fields) to show raw values
        Map<String, Object> readKw = new HashMap<>();
        Object[] recs = (Object[]) call("account.analytic.line", "read",
                new Object[]{Arrays.asList(SAMPLE_TS_ID)}, readKw);
        @SuppressWarnings("unchecked")
        Map<String, Object> sample = (Map<String, Object>) recs[0];

        // Part A: fields that look like activity / type / category
        System.out.println("=== A) Name/label contains activity, type or categ ===");
        for (Map.Entry<String, Object> e : fields.entrySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> def = (Map<String, Object>) e.getValue();
            String text = (e.getKey() + " " + def.get("string")).toLowerCase();
            if (text.contains("activity") || text.contains("type") || text.contains("categ")) {
                Object v = sample.get(e.getKey());
                String val = (v instanceof Object[]) ? Arrays.deepToString((Object[]) v) : String.valueOf(v);
                System.out.println(e.getKey() + " | label=" + def.get("string")
                        + " | type=" + def.get("type") + " | relation=" + def.get("relation")
                        + " | stored=" + def.get("store") + " | value(ts " + SAMPLE_TS_ID + ")=" + val);
            }
        }

        // Part B: which many2one field points to a record 29 named "Implementation"
        System.out.println("\n=== B) many2one fields where id " + ACTIVITY_ID + " = " + ACTIVITY_VALUE + " ===");
        for (Map.Entry<String, Object> e : fields.entrySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> def = (Map<String, Object>) e.getValue();
            if (!"many2one".equals(String.valueOf(def.get("type"))) || def.get("relation") == null) continue;
            String relation = String.valueOf(def.get("relation"));
            try {
                Map<String, Object> rk = new HashMap<>();
                rk.put("fields", Arrays.asList("display_name"));
                Object[] r = (Object[]) call(relation, "read",
                        new Object[]{Arrays.asList(ACTIVITY_ID)}, rk);
                @SuppressWarnings("unchecked")
                Map<String, Object> rec = (Map<String, Object>) r[0];
                if (String.valueOf(rec.get("display_name")).toLowerCase()
                        .contains(ACTIVITY_VALUE.toLowerCase())) {
                    System.out.println("MATCH -> field=" + e.getKey() + " | label=" + def.get("string")
                            + " | relation=" + relation + " | value=" + rec.get("display_name"));
                }
            } catch (Exception ex) {
                // Skip models that cannot be read or where the id does not exist
            }
        }
    }
}
