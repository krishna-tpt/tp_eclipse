package com.zoho.analytics.client;

public class ParseException extends Exception {
   private String responseContent;

   protected ParseException(String responseContent, String message, Throwable th) {
      super(message, th);
      this.responseContent = responseContent;
   }

   public String getResponseMessage() {
      return this.responseContent;
   }
}
