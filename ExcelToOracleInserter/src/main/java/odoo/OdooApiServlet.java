package odoo;

import java.io.IOException;
import java.util.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/*")
public class OdooApiServlet extends HttpServlet {

    // GET /api/projects, /api/activities, /api/tasks?projectId=27&includeDone=true, /api/day?date=YYYY-MM-DD
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        try {
            switch (path) {
                case "/projects":
                    send(resp, 200, OdooClient.listProjects());
                    break;
                case "/activities":
                    send(resp, 200, OdooClient.listActivities());
                    break;
                case "/tasks": {
                    // includeDone=true also returns the tasks of the Done stage
                    boolean includeDone = "true".equals(req.getParameter("includeDone"));
                    send(resp, 200, OdooClient.listTasks(Integer.parseInt(req.getParameter("projectId")), includeDone));
                    break;
                }
                case "/day":
                    // Existing timesheets + attendance for the selected date
                    send(resp, 200, OdooClient.dayInfo(req.getParameter("date")));
                    break;
                default:
                    send(resp, 404, Collections.singletonMap("error", "Not found"));
            }
        } catch (Exception e) {
            sendError(resp, e);
        }
    }

    // POST /api/timesheet (create) and POST /api/timesheet/update (edit), form encoded
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Read form values as UTF-8 so special characters in the description stay intact
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        try {
            if ("/timesheet".equals(path)) {
                int projectId = Integer.parseInt(req.getParameter("projectId"));
                int taskId = Integer.parseInt(req.getParameter("taskId"));
                Integer activityId = parseOptionalInt(req.getParameter("activityId"));
                String date = req.getParameter("date");
                double hours = Double.parseDouble(req.getParameter("hours"));
                String description = req.getParameter("description");

                // Basic validation
                if (date == null || date.trim().isEmpty()
                        || description == null || description.trim().isEmpty() || hours <= 0) {
                    send(resp, 400, Collections.singletonMap("error", "Date, hours and description are required"));
                    return;
                }
                Object id = OdooClient.createTimesheet(projectId, taskId, activityId, date, hours, description.trim());
                send(resp, 200, Collections.singletonMap("id", id));

            } else if ("/timesheet/update".equals(path)) {
                int id = Integer.parseInt(req.getParameter("id"));
                int taskId = Integer.parseInt(req.getParameter("taskId"));
                Integer activityId = parseOptionalInt(req.getParameter("activityId"));
                double hours = Double.parseDouble(req.getParameter("hours"));
                String description = req.getParameter("description");

                // Basic validation
                if (description == null || description.trim().isEmpty() || hours <= 0) {
                    send(resp, 400, Collections.singletonMap("error", "Hours and description are required"));
                    return;
                }
                OdooClient.updateTimesheet(id, taskId, activityId, hours, description.trim());
                send(resp, 200, Collections.singletonMap("ok", true));

            } else {
                send(resp, 404, Collections.singletonMap("error", "Not found"));
            }
        } catch (Exception e) {
            sendError(resp, e);
        }
    }

    // Blank or missing value becomes null
    private static Integer parseOptionalInt(String s) {
        return (s == null || s.trim().isEmpty()) ? null : Integer.valueOf(s.trim());
    }

    private void sendError(HttpServletResponse resp, Exception e) throws IOException {
        String msg = String.valueOf(e.getMessage());
        // Odoo faults contain a long traceback, keep only the start
        if (msg.length() > 500) msg = msg.substring(0, 500) + "...";
        send(resp, 500, Collections.singletonMap("error", msg));
    }

    private void send(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json(body));
    }

    // Minimal JSON writer (Map, List, arrays, numbers, booleans, strings)
    private static String json(Object o) {
        if (o == null) return "null";
        if (o instanceof Number || o instanceof Boolean) return o.toString();
        if (o instanceof Map) {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append(json(String.valueOf(e.getKey()))).append(":").append(json(e.getValue()));
            }
            return sb.append("}").toString();
        }
        if (o instanceof Object[]) o = Arrays.asList((Object[]) o);
        if (o instanceof Iterable) {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object x : (Iterable<?>) o) {
                if (!first) sb.append(",");
                first = false;
                sb.append(json(x));
            }
            return sb.append("]").toString();
        }
        // String: escape special characters
        StringBuilder sb = new StringBuilder("\"");
        for (char c : o.toString().toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.append("\"").toString();
    }
}