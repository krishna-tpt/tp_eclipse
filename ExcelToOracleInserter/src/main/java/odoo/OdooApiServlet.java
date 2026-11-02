package odoo;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/*")
public class OdooApiServlet extends HttpServlet {

    // GET /api/projects, /api/activities, /api/tasks?projectId=27
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
                case "/tasks":
                    send(resp, 200, OdooClient.listTasks(Integer.parseInt(req.getParameter("projectId"))));
                    break;
                default:
                    send(resp, 404, Map.of("error", "Not found"));
            }
        } catch (Exception e) {
            sendError(resp, e);
        }
    }

 // POST /api/timesheet (form encoded)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!"/timesheet".equals(req.getPathInfo())) {
            send(resp, 404, Collections.singletonMap("error", "Not found"));
            return;
        }
        try {
            int projectId = Integer.parseInt(req.getParameter("projectId"));
            int taskId = Integer.parseInt(req.getParameter("taskId"));
            String act = req.getParameter("activityId");
            // Java 8: isBlank() replaced with trim().isEmpty()
            Integer activityId = (act == null || act.trim().isEmpty()) ? null : Integer.valueOf(act);
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
        } catch (Exception e) {
            sendError(resp, e);
        }
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