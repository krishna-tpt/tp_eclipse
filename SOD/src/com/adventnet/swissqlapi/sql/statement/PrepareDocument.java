package com.adventnet.swissqlapi.sql.statement;

import java.util.ArrayList;

public class PrepareDocument {
   private static ArrayList idocs = new ArrayList();
   private static ArrayList xmls = new ArrayList();
   private static ArrayList xpaths = new ArrayList();

   public static void prepareDocument(String idoc, String xml, String xpath) {
      idocs.add(0, idoc.toLowerCase());
      xmls.add(0, xml);
      xpaths.add(0, xpath);
   }

   public static void removeDocument(String idoc, String xml, String xpath) {
   }

   public static String getXML(String idoc) {
      idoc = idoc.toLowerCase();
      if (idocs.size() == 0) {
         return "";
      } else if (idocs.indexOf(idoc) != -1) {
         return (String)xmls.get(idocs.indexOf(idoc));
      } else {
         return idocs.indexOf(idoc.substring(1)) != -1 ? (String)xmls.get(idocs.indexOf(idoc.substring(1))) : "";
      }
   }

   public static void resetPrepareDocument() {
      idocs = new ArrayList();
      xmls = new ArrayList();
      xpaths = new ArrayList();
   }
}
