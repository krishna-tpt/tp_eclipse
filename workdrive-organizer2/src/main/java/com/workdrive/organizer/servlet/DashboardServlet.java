package com.workdrive.organizer.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class DashboardServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DashboardServlet.class);

    @Override
    public void init() throws ServletException {
//        log.info("=== DashboardServlet initialized ===");
//        log.info("Application started at context: {}", getServletContext().getContextPath());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

//        log.info(">>> DashboardServlet.doGet() called");
//        log.debug("Request URL : {}", req.getRequestURL());
//        log.debug("Remote IP   : {}", req.getRemoteAddr());

        resp.setContentType("text/html; charset=UTF-8");

        try (InputStream in = getServletContext().getResourceAsStream("/index.html")) {
            if (in != null) {
//                log.debug("index.html found — serving file");
                resp.getOutputStream().write(in.readAllBytes());
            } else {
                log.warn("index.html NOT found in webapp root!");
                resp.getWriter().write("<h2>index.html not found</h2>");
            }
        }

//        log.info("<<< DashboardServlet.doGet() completed");
    }
}