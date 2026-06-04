package com.adventnet.swissqlapi.sql.statement;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CommentClass implements Serializable {
   private ArrayList specialTokenList = new ArrayList();
   private int SQLDialect = 0;
   private String comment = null;

   public void setSQLDialect(int SQLDialect) {
      this.SQLDialect = SQLDialect;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public void setSpecialToken(ArrayList specialTokenList) {
      for(int i = 0; i < specialTokenList.size(); ++i) {
         if (!this.specialTokenList.contains(specialTokenList.get(i))) {
            this.specialTokenList.add(specialTokenList.get(i));
         }
      }

   }

   public int getSQLDialect() {
      return this.SQLDialect;
   }

   public ArrayList getSpecialToken() {
      return this.specialTokenList;
   }

   public String toString() {
      if (SwisSQLAPI.getProperSelExp()) {
         return new String();
      } else if (this.comment != null) {
         return this.comment;
      } else {
         StringBuffer tempBuffer = new StringBuffer();

         for(int i = 0; i < this.specialTokenList.size(); ++i) {
            String specialToken = (String)this.specialTokenList.get(i);
            if (specialToken.trim().startsWith("--")) {
               if (specialToken.indexOf("*/") != -1) {
                  specialToken = StringFunctions.replaceAll("*//*", "*/", specialToken);
               }

               specialToken = StringFunctions.replaceFirst("/*", "--", specialToken);
               specialToken = specialToken + "*/";
               tempBuffer.append(specialToken);
            } else if (specialToken.trim().toUpperCase().indexOf("%SSTD%") != -1 && this.SQLDialect == 12) {
               String teradataCom = "%SSTD%";
               tempBuffer.append(specialToken.replaceAll("/\\*", "").replaceAll(teradataCom, "").replaceAll("\\*/", ""));
               if (!specialToken.trim().endsWith(";")) {
                  tempBuffer.append(";");
               }

               tempBuffer.append("\n");
            } else if (specialToken.trim().startsWith("/*")) {
               StringTokenizer newLineTokenizer = new StringTokenizer(specialToken, "\n");
               tempBuffer.append(newLineTokenizer.nextToken());

               while(newLineTokenizer.hasMoreTokens()) {
                  String tokenBeforeLine = newLineTokenizer.nextToken();
                  tempBuffer.append("\n");
                  tempBuffer.append(tokenBeforeLine);
               }
            } else {
               System.err.println("SpecialToken inside Comment Clause toStirng " + specialToken);
            }

            tempBuffer.append("\n");
         }

         return tempBuffer.toString();
      }
   }

   public String toTSQLString() {
      StringBuffer tempBuffer = new StringBuffer();

      for(int i = 0; i < this.specialTokenList.size(); ++i) {
         String specialToken = (String)this.specialTokenList.get(i);
         if (specialToken.trim().startsWith("/*")) {
            StringTokenizer newLineTokenizer = new StringTokenizer(specialToken, "\n");
            tempBuffer.append(newLineTokenizer.nextToken());

            while(newLineTokenizer.hasMoreTokens()) {
               String tokenBeforeLine = newLineTokenizer.nextToken();
               tempBuffer.append("\n");
               tempBuffer.append(tokenBeforeLine);
            }
         }

         tempBuffer.append("\n");
      }

      return tempBuffer.toString();
   }
}
