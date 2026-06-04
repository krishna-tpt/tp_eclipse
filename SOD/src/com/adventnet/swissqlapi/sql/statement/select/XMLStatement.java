package com.adventnet.swissqlapi.sql.statement.select;

public class XMLStatement {
   private String forString;
   private String xmlString;
   private String xmlType;
   private String xmlAtomicValue;
   private String xmlData;
   private String elements;

   public void setFor(String forString) {
      this.forString = forString;
   }

   public void setXML(String xmlString) {
      this.xmlString = xmlString;
   }

   public void setXMLType(String xmlType) {
      this.xmlType = xmlType;
   }

   public void setXMLAtomicValue(String xmlAtomicValue) {
      this.xmlAtomicValue = xmlAtomicValue;
   }

   public void setXMLData(String xmlData) {
      this.xmlData = xmlData;
   }

   public void setElements(String elements) {
      this.elements = elements;
   }

   public String getFor() {
      return this.forString;
   }

   public String getXML() {
      return this.xmlString;
   }

   public String getXMLType() {
      return this.xmlType;
   }

   public String getXMLAtomicValue() {
      return this.xmlAtomicValue;
   }

   public String getElements() {
      return this.elements;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.forString != null) {
         sb.append(this.forString + " ");
      }

      if (this.xmlString != null) {
         sb.append(this.xmlString + " ");
      }

      if (this.xmlType != null) {
         sb.append(this.xmlType + " ");
      }

      if (this.xmlAtomicValue != null) {
         sb.append("(" + this.xmlAtomicValue + ") ");
      }

      if (this.xmlData != null) {
         sb.append(", " + this.xmlData);
      }

      if (this.elements != null) {
         sb.append(", " + this.elements);
      }

      return sb.toString();
   }
}
