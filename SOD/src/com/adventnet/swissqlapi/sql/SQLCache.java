package com.adventnet.swissqlapi.sql;

import java.util.Enumeration;
import java.util.Hashtable;

public class SQLCache {
   private static Hashtable SQLTable = new Hashtable();
   private static int maxCacheSize = 1000;
   private static boolean cacheToBePersisted = false;

   public static void putConvertedSQL(String gvnSQL, String cnvtSQL) {
      if (SQLTable.size() >= maxCacheSize) {
         Enumeration e = SQLTable.keys();
         if (e.hasMoreElements()) {
            SQLTable.remove(e.nextElement());
         }
      }

      SQLTable.put(gvnSQL, cnvtSQL);
   }

   public static String getConvertedSQL(String gvnSQL) {
      try {
         return (String)SQLTable.get(gvnSQL);
      } catch (Exception var2) {
         return null;
      }
   }

   public static void setMaxCacheSize(int size) {
      maxCacheSize = size;
   }

   public static int getMaxCacheSize() {
      return maxCacheSize;
   }

   public static void persistSQLCache(boolean persist) {
      cacheToBePersisted = persist;
   }
}
