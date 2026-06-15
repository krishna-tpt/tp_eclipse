package com.expensemanager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

	private static final Logger log = LoggerFactory.getLogger(AppContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		String url = sce.getServletContext().getInitParameter("DB_URL");
		String user = sce.getServletContext().getInitParameter("DB_USER");
		String pass = sce.getServletContext().getInitParameter("DB_PASSWORD");
		log.info("DB Start to Connect...");
		try {
			Class.forName("org.postgresql.Driver");
			DBConnection.init(url, user, pass);
			log.info("Expense Manager started — DB connected");

//			AppLogCapture.install();

		} catch (Exception e) {
			log.error("DB init failed: {}", e.getMessage(), e);
		} /*
			 * finally { com.expensemanager.servlet.AppLogCapture.install(); }
			 */
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		log.info("DB Shutdown....");
		DBConnection.shutdown();
	}
}
