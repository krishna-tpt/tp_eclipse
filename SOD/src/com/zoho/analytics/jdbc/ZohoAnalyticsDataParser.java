package com.zoho.analytics.jdbc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import zr.org.apache.commons.csv.CSVParser;

public class ZohoAnalyticsDataParser {
   public String[] colNameArr = null;
   public int[] sqlTypeArr = null;
   public String[] tblNameArr = null;
   public String[] subTypeArr = null;
   public int[] subTypeIdArr = null;

   ZohoAnalyticsDataParser() {
   }

   void parseCSV(ArrayList<String[]> rowData, ByteArrayOutputStream bos) throws SQLException {
      ByteArrayInputStream is = new ByteArrayInputStream(bos.toByteArray());

      try {
         BufferedReader br = new BufferedReader(new InputStreamReader(is));
         Throwable var5 = null;

         try {
            CSVParser parser = new CSVParser(br);
            this.colNameArr = parser.getLine();
            if (this.colNameArr[0].startsWith("\ufeff")) {
               this.colNameArr[0] = this.colNameArr[0].substring(1);
            }

            String[] sqlTypeStringArr = parser.getLine();
            this.sqlTypeArr = new int[sqlTypeStringArr.length];

            for(int i = 0; i < sqlTypeStringArr.length; ++i) {
               this.sqlTypeArr[i] = Integer.parseInt(sqlTypeStringArr[i]);
            }

            this.tblNameArr = parser.getLine();
            this.subTypeArr = parser.getLine();
            String[] sqlTypeIdStringArr = parser.getLine();
            this.subTypeIdArr = new int[sqlTypeIdStringArr.length];

            for(int i = 0; i < sqlTypeIdStringArr.length; ++i) {
               this.subTypeIdArr[i] = Integer.parseInt(sqlTypeIdStringArr[i]);
            }

            while(true) {
               String[] data = parser.getLine();
               if (data == null) {
                  return;
               }

               rowData.add(data);
            }
         } catch (Throwable var18) {
            var5 = var18;
            throw var18;
         } finally {
            if (br != null) {
               if (var5 != null) {
                  try {
                     br.close();
                  } catch (Throwable var17) {
                     var5.addSuppressed(var17);
                  }
               } else {
                  br.close();
               }
            }

         }
      } catch (IOException var20) {
         throw new SQLException(var20.getMessage());
      }
   }
}
