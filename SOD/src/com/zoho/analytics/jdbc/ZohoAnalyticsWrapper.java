package com.zoho.analytics.jdbc;

import java.sql.SQLException;
import java.sql.Wrapper;

public class ZohoAnalyticsWrapper implements Wrapper {
   public <T> T unwrap(Class<T> iface) throws SQLException {
      throw new NotImplementedException();
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      return false;
   }
}
