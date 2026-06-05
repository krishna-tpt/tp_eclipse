package com.expensemanager.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Stub servlet that loads on startup (order 1).
 * Actual initial load is done by AppStartupListener.
 * This servlet handles /excel/* sub-paths if needed.
 */
@WebServlet(loadOnStartup = 1, urlPatterns = "/excel/*")
public class ExcelDownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // /excel/download → redirect to sync
        resp.sendRedirect(req.getContextPath() + "/sync");
    }
}
