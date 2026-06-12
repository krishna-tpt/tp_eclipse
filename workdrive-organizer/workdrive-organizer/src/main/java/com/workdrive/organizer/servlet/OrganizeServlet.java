package com.workdrive.organizer.servlet;

import com.workdrive.organizer.service.OrganizerService;
import com.workdrive.organizer.util.AppConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * POST /organize
 * Triggers the file organizer and returns a JSON summary of what was moved.
 */
public class OrganizeServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(OrganizeServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json; charset=UTF-8");
        resp.setHeader("Cache-Control", "no-cache");

//        log.trace("ENTER OrganizeServlet.doPost()");
//        log.info(">>> POST /organize triggered by {}", req.getRemoteAddr());
        
        try {
//        	 log.trace("Getting OrganizerService from AppConfig");
            OrganizerService svc = AppConfig.getOrganizerService(getServletContext());
            
//            log.trace("Calling OrganizerService.organize()");
            OrganizerService.OrganizeResult result = svc.organize();

            log.info("Organize done — Moved={}, Skipped={}, Failed={}",
                    result.getMovedCount(), result.getSkippedCount(), result.getFailedCount());


            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(result.toJson());
//            log.trace("EXIT OrganizeServlet.doPost() - success");

            log.debug(result.toJson().toString());

        } catch (IllegalStateException e) {
            log.error("Config error: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Configuration error: " + escapeJson(e.getMessage()) + "\"}");
        } catch (Exception e) {
            log.error("Organize failed", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            
//            log.error("OrganizeServlet error", e);
//            log.trace("EXIT OrganizeServlet.doPost() - error");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"error\":\"Use POST to trigger organize\"}");
    }

    private String escapeJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
