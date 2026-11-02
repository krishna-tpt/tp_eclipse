package odoo;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class OdooClient {

    private static final Properties P = new Properties();
    private static Integer uid;
    private static XmlRpcClient models;

    // Load connection details from odoo.properties on the classpath
    static {
        try (InputStream in = OdooClient.class.getResourceAsStream("/odoo.properties")) {
            P.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load odoo.properties", e);
        }
    }

    // Authenticate once and reuse the uid and model client
    private static synchronized void init() throws Exception {
        if (uid != null) return;

        XmlRpcClientConfigImpl cc = new XmlRpcClientConfigImpl();
        cc.setServerURL(new URL(P.getProperty("odoo.url") + "/xmlrpc/2/common"));
        XmlRpcClient common = new XmlRpcClient();
        common.setConfig(cc);

        Object r = common.execute("authenticate", Arrays.asList(
                P.getProperty("odoo.db"), P.getProperty("odoo.user"),
                P.getProperty("odoo.apikey"), Collections.emptyMap()));
        if (!(r instanceof Integer)) {
            throw new RuntimeException("Odoo authentication failed: check DB, login and API key");
        }

        XmlRpcClientConfigImpl oc = new XmlRpcClientConfigImpl();
        oc.setServerURL(new URL(P.getProperty("odoo.url") + "/xmlrpc/2/object"));
        models = new XmlRpcClient();
        models.setConfig(oc);
        uid = (Integer) r;
    }

    // Generic execute_kw wrapper
    private static Object call(String model, String method, List<?> args, Map<String, Object> kw) throws Exception {
        init();
        return models.execute("execute_kw", Arrays.asList(
                P.getProperty("odoo.db"), uid, P.getProperty("odoo.apikey"),
                model, method, args, kw));
    }

    // search_read helper: domain + options
    private static Object[] search(String model, List<?> domain, Map<String, Object> opts) throws Exception {
        return (Object[]) call(model, "search_read", Collections.singletonList(domain), opts);
    }

    // Return only the name part of a many2one value ([id, name] or false)
    @SuppressWarnings("unchecked")
    private static String m2oName(Object v) {
        return (v instanceof Object[] && ((Object[]) v).length > 1) ? String.valueOf(((Object[]) v)[1]) : "";
    }

    // Projects where timesheets are allowed (Odoo applies the user's access rules)
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> listProjects() throws Exception {
        Map<String, Object> opts = new HashMap<>();
        opts.put("fields", Arrays.asList("id", "name"));
        opts.put("order", "name asc");

        Object[] rows = search("project.project",
                Arrays.asList(Arrays.asList("allow_timesheets", "=", true)), opts);

        List<Map<String, Object>> out = new ArrayList<>();
        for (Object o : rows) {
            Map<String, Object> m = (Map<String, Object>) o;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", m.get("id"));
            item.put("name", m.get("name"));
            out.add(item);
        }
        return out;
    }

    // Open tasks of one project (not Done / Canceled), with the stage name
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> listTasks(int projectId) throws Exception {
        Map<String, Object> opts = new HashMap<>();
        opts.put("fields", Arrays.asList("id", "name", "stage_id"));
        opts.put("order", "name asc");

        Object[] rows;
        try {
            rows = search("project.task", Arrays.asList(
                    Arrays.asList("project_id", "=", projectId),
                    Arrays.asList("state", "not in", Arrays.asList("1_done", "1_canceled"))), opts);
        } catch (Exception e) {
            // Older Odoo versions have no "state" field: fall back to all tasks
            rows = search("project.task",
                    Arrays.asList(Arrays.asList("project_id", "=", projectId)), opts);
        }

        List<Map<String, Object>> out = new ArrayList<>();
        for (Object o : rows) {
            Map<String, Object> m = (Map<String, Object>) o;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", m.get("id"));
            item.put("name", m.get("name"));
            item.put("stage", m2oName(m.get("stage_id")));
            out.add(item);
        }
        return out;
    }

    // Activity types for the "Activity" dropdown
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> listActivities() throws Exception {
        Map<String, Object> opts = new HashMap<>();
        opts.put("fields", Arrays.asList("id", "name"));
        opts.put("order", "name asc");

        Object[] rows = search("mail.activity.type", Collections.emptyList(), opts);

        List<Map<String, Object>> out = new ArrayList<>();
        for (Object o : rows) {
            Map<String, Object> m = (Map<String, Object>) o;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", m.get("id"));
            item.put("name", m.get("name"));
            out.add(item);
        }
        return out;
    }

    // Create one timesheet line and return its id
    public static Object createTimesheet(int projectId, int taskId, Integer activityId,
                                         String date, double hours, String description) throws Exception {
        Map<String, Object> vals = new HashMap<>();
        vals.put("name", description);
        vals.put("date", date);              // YYYY-MM-DD
        vals.put("unit_amount", hours);      // Decimal hours
        vals.put("project_id", projectId);
        vals.put("task_id", taskId);
        if (activityId != null) {
            vals.put("activity_id", activityId);
        }
        return call("account.analytic.line", "create",
                Collections.singletonList(vals), new HashMap<String, Object>());
    }
}