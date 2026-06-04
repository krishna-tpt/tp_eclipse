package com.zoho.analytics.jdbc;

import java.sql.SQLException;

public class NotImplementedException extends SQLException {
   public NotImplementedException() {
      super("Not Implemented");
   }
}
