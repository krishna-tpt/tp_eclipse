package com.adventnet.swissqlapi.sql.statement;

import java.util.ArrayList;

public class OpenXML {
   private String idoc = null;
   private String row_pattern = null;
   private String flag = null;
   private PrepareDocument prepare = null;
   private String varAS = "ADV";
   private OpenXMLSchema schema = null;

   public void setFlag(String flag) {
      this.flag = flag;
   }

   public String getFlag() {
      return this.flag;
   }

   public void setIdoc(String idoc) {
      this.idoc = idoc;
   }

   public String getIdoc() {
      return this.idoc;
   }

   public void setRowPattern(String row_pattern) {
      this.row_pattern = row_pattern;
   }

   public String getRowPattern() {
      return this.row_pattern;
   }

   public void setSchema(OpenXMLSchema schema) {
      this.schema = schema;
   }

   public OpenXMLSchema getSchema() {
      return this.schema;
   }

   public String toString() {
      return this.toOracleString();
   }

   public String toOracleString() {
      StringBuffer sb = new StringBuffer();
      StringBuffer sb1 = new StringBuffer();
      String s = "";
      if (this.schema.getNoMetaData()) {
         sb.append("\n /* SwisSQL Info : Kindly fetch the MetaData for the exact migration of the below OPENXML()*/ \n");
         if (this.schema.getTableName() != null) {
            s = "WITH " + this.schema.getTableName();
         }

         sb.append("OPENXML(" + this.idoc + ", " + this.row_pattern + ", " + this.flag + ")" + s);
         return sb.toString();
      } else {
         sb.append("SELECT ");
         sb1.append("EXTRACTVALUE");
         sb1.append("(");
         sb1.append("value(" + this.varAS + "),");
         sb.append(this.repeatForMultipleCloumns(sb1.toString()));
         sb.append(" FROM TABLE");
         sb.append("(XMLSEQUENCE(XMLTYPE.EXTRACT(XMLTYPE(");
         PrepareDocument var10001 = this.prepare;
         sb.append(PrepareDocument.getXML(this.idoc));
         sb.append(")");
         sb.append(",");
         sb.append("'");
         sb.append(this.row_pattern);
         sb.append("'");
         sb.append("))) " + this.varAS);
         return sb.toString();
      }
   }

   public ArrayList removeSquareBrackets(ArrayList cols) {
      if (cols == null) {
         return null;
      } else {
         ArrayList toRet = new ArrayList();

         for(int i = 0; i < cols.size(); ++i) {
            String s = (String)cols.get(i);
            if (s != null && s.startsWith("[")) {
               s = s.substring(1);
            }

            if (s != null && s.endsWith("]")) {
               s = s.substring(0, s.length() - 1);
            }

            toRet.add(s);
         }

         return toRet;
      }
   }

   public String repeatForMultipleCloumns(String orgStr) {
      this.schema.setColumnNames(this.removeSquareBrackets(this.schema.getColumnNames()));
      ArrayList columnNames = this.schema.getColumnNames();
      StringBuffer retStr = new StringBuffer();
      retStr.append(orgStr);
      String newRowPattern = this.row_pattern;
      if (newRowPattern.indexOf("/") == 0) {
         newRowPattern = newRowPattern.substring(1);
      }

      if (newRowPattern.indexOf("/") > 0) {
         newRowPattern = newRowPattern.substring(newRowPattern.indexOf("/") + 1);
      }

      if (columnNames == null) {
         retStr.append("'" + newRowPattern + "'");
         retStr.append(")");
         return retStr.toString();
      } else {
         if (this.row_pattern.startsWith("\"") || this.row_pattern.startsWith("'")) {
            this.row_pattern = this.row_pattern.substring(1);
         }

         if (this.row_pattern.endsWith("\"") || this.row_pattern.endsWith("'")) {
            this.row_pattern = this.row_pattern.substring(0, this.row_pattern.length() - 1);
         }

         if (newRowPattern.startsWith("\"") || newRowPattern.startsWith("'")) {
            newRowPattern = newRowPattern.substring(1);
         }

         if (newRowPattern.endsWith("\"") || newRowPattern.endsWith("'")) {
            newRowPattern = newRowPattern.substring(0, newRowPattern.length() - 1);
         }

         if (this.flag != null && !this.flag.trim().equals("1")) {
            if (this.flag.trim().equals("2")) {
               retStr.append("'" + newRowPattern + "/" + columnNames.get(0) + "'");
            }
         } else {
            this.flag = "1";
            retStr.append("'" + newRowPattern + "/@" + columnNames.get(0) + "'");
         }

         retStr.append(")");
         retStr.append(" AS ");
         retStr.append(" " + columnNames.get(0).toString() + " ");

         for(int i = 1; i < columnNames.size(); ++i) {
            retStr.append(",");
            retStr.append(orgStr);
            if (this.flag.trim().equals("2")) {
               retStr.append("'" + newRowPattern + "/" + columnNames.get(i) + "'");
            } else {
               retStr.append("'" + newRowPattern + "/@" + columnNames.get(i) + "'");
            }

            retStr.append(")");
            retStr.append(" AS ");
            retStr.append(" " + columnNames.get(i).toString() + " ");
         }

         return retStr.toString();
      }
   }

   public void setPrepareDocument(PrepareDocument prepare) {
      this.prepare = prepare;
   }
}
