package com.zoho.analytics.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZohoAnalyticsDriver implements Driver {
   Matcher validateURLAndGetMatcher(String url) throws SQLException {
      if (url == null) {
         throw new SQLException("Empty JDBC URL");
      } else {
         String regex = "^jdbc:zohoanalytics://([\\w\\.]+)/([0-9]+)/(.+)$";
         Pattern pattern = Pattern.compile(regex);
         Matcher matcher = pattern.matcher(url);
         if (!matcher.matches()) {
            throw new SQLException("Invalid JDBC URL format");
         } else {
            return matcher;
         }
      }
   }

   public boolean acceptsURL(String url) throws SQLException {
      if (url == null) {
         return false;
      } else {
         try {
            this.validateURLAndGetMatcher(url);
            return true;
         } catch (Exception var3) {
            return false;
         }
      }
   }

   public Connection connect(String url, Properties properties) throws SQLException {
      Matcher matcher = this.validateURLAndGetMatcher(url);
      properties.put("HOST_NAME", matcher.group(1));
      properties.put("ORG_ID", matcher.group(2));
      properties.put("WORKSPACE_NAME", matcher.group(3));
      return new ZohoAnalyticsConnection(properties);
   }

   public int getMajorVersion() {
      return 1;
   }

   public int getMinorVersion() {
      return 1;
   }

   public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
      if (info == null) {
         info = new Properties();
      }

      if (!this.acceptsURL(url)) {
         return null;
      } else {
         try {
            this.validateURLAndGetMatcher(url);
         } catch (Exception var11) {
            throw new SQLException(var11.getMessage());
         }

         DriverPropertyInfo clientIdProp = new DriverPropertyInfo("CLIENT_ID", info.getProperty("CLIENT_ID"));
         clientIdProp.description = "OAuth client id";
         clientIdProp.required = true;
         DriverPropertyInfo clientSecretProp = new DriverPropertyInfo("CLIENT_SECRET", info.getProperty("CLIENT_SECRET"));
         clientSecretProp.description = "OAuth client secret";
         clientSecretProp.required = true;
         DriverPropertyInfo refTokenProp = new DriverPropertyInfo("REFRESH_TOKEN", info.getProperty("REFRESH_TOKEN"));
         refTokenProp.description = "OAuth refresh token";
         refTokenProp.required = true;
         DriverPropertyInfo proxyHostProp = new DriverPropertyInfo("PROXYSERVER", info.getProperty("PROXYSERVER"));
         proxyHostProp.description = "Proxy server's hostname";
         proxyHostProp.required = false;
         DriverPropertyInfo proxyPortProp = new DriverPropertyInfo("PROXYPORT", info.getProperty("PROXYPORT"));
         proxyPortProp.description = "Proxy server's port number";
         proxyPortProp.required = false;
         DriverPropertyInfo proxyUserProp = new DriverPropertyInfo("PROXYUSERNAME", info.getProperty("PROXYUSERNAME"));
         proxyUserProp.description = "Proxy server username";
         proxyUserProp.required = false;
         DriverPropertyInfo proxyPwdProp = new DriverPropertyInfo("PROXYPASSWORD", info.getProperty("PROXYPASSWORD"));
         proxyPwdProp.description = "Proxy server password";
         proxyPwdProp.required = false;
         DriverPropertyInfo[] arr = new DriverPropertyInfo[]{clientIdProp, clientSecretProp, refTokenProp, proxyHostProp, proxyPortProp, proxyUserProp, proxyPwdProp};
         return arr;
      }
   }

   public boolean jdbcCompliant() {
      return false;
   }

   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   static {
      ZohoAnalyticsDriver driver = new ZohoAnalyticsDriver();

      try {
         DriverManager.registerDriver(driver);
      } catch (Exception var2) {
         throw new RuntimeException("Can't register driver!");
      }
   }
}
