package odoo;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class OdooClient {

    private static final Properties P = new Properties();
    private static Integer uid;
    private static XmlRpcClient models;

    // Fixed break time deducted from the attendance duration
    private static final int BREAK_MINUTES = 30;

    // Odoo stores datetimes in UTC using this format
    private static final DateTimeFormatter ODOO_DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");

    // Used when the Odoo user has no timezone configured
    private static final String DEFAULT_TZ = "Asia/Kolkata";

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

 // Task stages shown by default (compared case-insensitively); Done is added only on request
    private static final Set<String> ACTIVE_STAGES =
            new HashSet<>(Arrays.asList("new", "ra", "da", "qa", "devops", "cs"));
    private static final String DONE_STAGE = "done";

    // Tasks of one project filtered by stage name, ordered by the Kanban stage order
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> listTasks(int projectId, boolean includeDone) throws Exception {
        Map<String, Object> opts = new HashMap<>();
        opts.put("fields", Arrays.asList("id", "name", "stage_id"));
        opts.put("order", "stage_id asc, name asc");

        Object[] rows = search("project.task",
                Arrays.asList(Arrays.asList("project_id", "=", projectId)), opts);

        List<Map<String, Object>> out = new ArrayList<>();
        for (Object o : rows) {
            Map<String, Object> m = (Map<String, Object>) o;
            String stage = m2oName(m.get("stage_id"));
            String key = stage.trim().toLowerCase(Locale.ROOT);

            // Keep only the allowed stages, plus Done when the checkbox is ticked
            boolean show = ACTIVE_STAGES.contains(key) || (includeDone && DONE_STAGE.equals(key));
            if (!show) continue;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", m.get("id"));
            item.put("name", m.get("name"));
            item.put("stage", stage);
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
    
    // Sets date_time / date_time_end (UTC) on the chosen date.
    // Keeps the existing time of day when there is one, else uses the current time.
    private static void putDateTimes(Map<String, Object> vals, String date, double hours, Object oldStart) {
        ZoneId zone = userZone();
        LocalDate d = LocalDate.parse(date.trim());
        LocalTime t = LocalTime.now(zone).withNano(0);
        if (oldStart instanceof String && !((String) oldStart).trim().isEmpty()) {
            t = LocalDateTime.parse((String) oldStart, ODOO_DT)
                    .atZone(ZoneOffset.UTC).withZoneSameInstant(zone).toLocalTime();
        }
        ZonedDateTime start = LocalDateTime.of(d, t).atZone(zone);
        ZonedDateTime end = start.plusSeconds(Math.round(hours * 3600));
        vals.put("date_time", start.withZoneSameInstant(ZoneOffset.UTC).format(ODOO_DT));
        vals.put("date_time_end", end.withZoneSameInstant(ZoneOffset.UTC).format(ODOO_DT));
    }

    // Create one timesheet line and return its id
    public static Object createTimesheet(int projectId, int taskId, Integer activityId,
                                         String date, double hours, String description) throws Exception {
        init();
        Map<String, Object> vals = new HashMap<>();
        vals.put("name", description);
        vals.put("date", date);              // YYYY-MM-DD
        vals.put("unit_amount", hours);      // Decimal hours
        putDateTimes(vals, date, hours, null);
        vals.put("project_id", projectId);
        vals.put("task_id", taskId);
        if (activityId != null) {
            vals.put("activity_id", activityId);
        }
        return call("account.analytic.line", "create",
                Collections.singletonList(vals), new HashMap<String, Object>());
    }

    // ---------------------------------------------------------------
    // Day summary: existing timesheets + attendance for one date
    // ---------------------------------------------------------------

    // date format: YYYY-MM-DD
    public static Map<String, Object> dayInfo(String date) throws Exception {
        init();
        LocalDate d = LocalDate.parse(date);
        ZoneId zone = userZone();

        List<Map<String, Object>> sheets = timesheetsOf(date);
        double total = 0;
        for (Map<String, Object> s : sheets) {
            total += ((Number) s.get("hours")).doubleValue();
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("date", date);
        out.put("timesheets", sheets);
        out.put("totalHours", Math.round(total * 100.0) / 100.0);
        out.put("attendance", attendanceOf(d, zone));
        return out;
    }

 // My timesheets of one date (ids and date are included so the page can edit an entry)
    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> timesheetsOf(String date) throws Exception {
        Map<String, Object> opts = new HashMap<>();
        opts.put("fields", Arrays.asList("id", "date", "name", "unit_amount", "project_id", "task_id", "activity_id"));
        opts.put("order", "id asc");

        Object[] rows = search("account.analytic.line", Arrays.asList(
                Arrays.asList("date", "=", date),
                Arrays.asList("user_id", "=", uid)), opts);

        List<Map<String, Object>> out = new ArrayList<>();
        for (Object o : rows) {
            Map<String, Object> m = (Map<String, Object>) o;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", m.get("id"));
            item.put("date", String.valueOf(m.get("date")));   // YYYY-MM-DD
            item.put("project", m2oName(m.get("project_id")));
            item.put("projectId", m2oId(m.get("project_id")));
            item.put("task", m2oName(m.get("task_id")));
            item.put("taskId", m2oId(m.get("task_id")));
            item.put("activity", m2oName(m.get("activity_id")));
            item.put("activityId", m2oId(m.get("activity_id")));
            item.put("description", m.get("name") == null ? "" : String.valueOf(m.get("name")));
            Object h = m.get("unit_amount");
            item.put("hours", (h instanceof Number) ? h : Double.valueOf(0));
            out.add(item);
        }
        return out;
    }
    
 // Return only the id part of a many2one value ([id, name] or false)
    private static Object m2oId(Object v) {
        return (v instanceof Object[] && ((Object[]) v).length > 0) ? ((Object[]) v)[0] : null;
    }

 // Update date, task, activity, hours and description of one of MY timesheet lines (project is never changed)
    @SuppressWarnings("unchecked")
    public static void updateTimesheet(int id, String date, int taskId, Integer activityId,
                                       double hours, String description) throws Exception {
        init();

        // Only my own timesheet lines can be edited from this page
        Map<String, Object> opts = new HashMap<>();
        opts.put("fields", Arrays.asList("id", "date_time"));
        opts.put("limit", 1);
        Object[] own = search("account.analytic.line", Arrays.asList(
                Arrays.asList("id", "=", id),
                Arrays.asList("user_id", "=", uid)), opts);
        if (own.length == 0) {
            throw new RuntimeException("Timesheet not found or it does not belong to you");
        }

        Map<String, Object> vals = new HashMap<>();
        vals.put("name", description);
        vals.put("unit_amount", hours);
        vals.put("task_id", taskId);
        // false clears the activity when none is selected
        vals.put("activity_id", activityId == null ? (Object) Boolean.FALSE : (Object) activityId);

        // Validate the format (YYYY-MM-DD) before sending it to Odoo
        if (date != null && !date.trim().isEmpty()) {
            vals.put("date", LocalDate.parse(date.trim()).toString());
            Object oldStart = ((Map<String, Object>) own[0]).get("date_time");
            putDateTimes(vals, date, hours, oldStart);
        }

        call("account.analytic.line", "write",
                Arrays.asList(Collections.singletonList(id), vals), new HashMap<String, Object>());
    }

    // Timezone of the Odoo user (attendance is stored in UTC)
    @SuppressWarnings("unchecked")
    private static ZoneId userZone() {
        try {
            Map<String, Object> kw = new HashMap<>();
            kw.put("fields", Arrays.asList("tz"));
            Object[] r = (Object[]) call("res.users", "read",
                    Collections.singletonList(Collections.singletonList(uid)), kw);
            if (r.length > 0) {
                Object tz = ((Map<String, Object>) r[0]).get("tz");
                if (tz instanceof String && !((String) tz).trim().isEmpty()) {
                    return ZoneId.of(((String) tz).trim());
                }
            }
        } catch (Exception e) {
            // Fall through to the default timezone
        }
        return ZoneId.of(DEFAULT_TZ);
    }

    // Employee id linked to the logged in Odoo user
    @SuppressWarnings("unchecked")
    private static Integer employeeId() throws Exception {
        Map<String, Object> opts = new HashMap<>();
        opts.put("fields", Arrays.asList("id"));
        opts.put("limit", 1);
        Object[] r = search("hr.employee", Arrays.asList(Arrays.asList("user_id", "=", uid)), opts);
        if (r.length == 0) return null;
        return (Integer) ((Map<String, Object>) r[0]).get("id");
    }

    // Convert an Odoo UTC datetime string to Instant (false / null becomes null)
    private static Instant toInstant(Object v) {
        if (v instanceof String && ((String) v).length() >= 19) {
            return LocalDateTime.parse(((String) v).substring(0, 19), ODOO_DT).toInstant(ZoneOffset.UTC);
        }
        return null;
    }

    // Attendance of one local date: first in, last out, gross and net (after break) minutes
    @SuppressWarnings("unchecked")
    private static Map<String, Object> attendanceOf(LocalDate d, ZoneId zone) {
        Map<String, Object> a = new LinkedHashMap<>();
        a.put("breakMinutes", BREAK_MINUTES);
        try {
            Integer emp = employeeId();
            if (emp == null) {
                a.put("found", false);
                a.put("note", "No employee is linked to this Odoo user");
                return a;
            }

            // Local day boundaries converted to UTC for the Odoo query
            String startUtc = d.atStartOfDay(zone).withZoneSameInstant(ZoneOffset.UTC).format(ODOO_DT);
            String endUtc = d.plusDays(1).atStartOfDay(zone).withZoneSameInstant(ZoneOffset.UTC).format(ODOO_DT);

            Map<String, Object> opts = new HashMap<>();
            opts.put("fields", Arrays.asList("check_in", "check_out"));
            opts.put("order", "check_in asc");

            Object[] rows = search("hr.attendance", Arrays.asList(
                    Arrays.asList("employee_id", "=", emp),
                    Arrays.asList("check_in", ">=", startUtc),
                    Arrays.asList("check_in", "<", endUtc)), opts);

            if (rows.length == 0) {
                a.put("found", false);
                return a;
            }

            Instant now = Instant.now();
            boolean isToday = d.equals(LocalDate.now(zone));
            long grossSec = 0;
            boolean open = false;        // today and not checked out yet
            boolean incomplete = false;  // past date with a missing out time
            Instant firstIn = null;
            Instant lastOut = null;

            for (Object o : rows) {
                Map<String, Object> m = (Map<String, Object>) o;
                Instant in = toInstant(m.get("check_in"));
                Instant out = toInstant(m.get("check_out"));
                if (in == null) continue;
                if (firstIn == null || in.isBefore(firstIn)) firstIn = in;

                if (out == null) {
                    if (isToday) {
                        // Still working: use the current time as the out time
                        out = now;
                        open = true;
                    } else {
                        incomplete = true;
                        continue;
                    }
                } else if (lastOut == null || out.isAfter(lastOut)) {
                    lastOut = out;
                }
                grossSec += Math.max(0, Duration.between(in, out).getSeconds());
            }

            long grossMin = grossSec / 60;
            long netMin = Math.max(0, grossMin - BREAK_MINUTES);

            a.put("found", true);
            a.put("firstIn", firstIn == null ? "" : firstIn.atZone(zone).format(HHMM));
            a.put("lastOut", lastOut == null ? "" : lastOut.atZone(zone).format(HHMM));
            a.put("open", open);
            a.put("incomplete", incomplete);
            a.put("nowTime", now.atZone(zone).format(HHMM));
            a.put("grossMinutes", grossMin);
            a.put("netMinutes", netMin);
        } catch (Exception e) {
            // Attendance app missing or no access: do not break the timesheet list
            a.put("found", false);
            String msg = String.valueOf(e.getMessage());
            a.put("note", "Attendance not available: " + (msg.length() > 120 ? msg.substring(0, 120) + "..." : msg));
        }
        return a;
    }
}