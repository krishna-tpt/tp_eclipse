package com.zoho.analytics.jdbc;

import java.util.HashMap;
import java.util.Map;

public class ZohoAnalyticsConstants implements ZohoAnalyticsData {
   private Map<String, String> properties = new HashMap();

   ZohoAnalyticsConstants(String key, String value) {
      this.properties.put(key, value);
   }

   ZohoAnalyticsConstants(HashMap<String, String> props) {
      this.properties.putAll(props);
   }

   public String get(String propertyName) {
      return (String)this.properties.getOrDefault(propertyName, (Object)null);
   }
}
