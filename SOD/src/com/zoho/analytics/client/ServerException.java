package com.zoho.analytics.client;

public class ServerException extends Exception {
   private final int httpStatusCode;
   private final int errorCode;
   private final String errorMessage;

   protected ServerException(int httpStatusCode, int errorCode, String message) {
      this.httpStatusCode = httpStatusCode;
      this.errorCode = errorCode;
      this.errorMessage = message;
   }

   public int getHttpStatusCode() {
      return this.httpStatusCode;
   }

   public int getErrorCode() {
      return this.errorCode;
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public String toString() {
      return "ServerException (HttpStatusCode: " + this.httpStatusCode + " Error Code: " + this.errorCode + ", Message: " + this.errorMessage + ")";
   }
}
