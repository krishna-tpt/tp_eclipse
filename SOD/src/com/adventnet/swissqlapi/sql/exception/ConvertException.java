package com.adventnet.swissqlapi.sql.exception;

public class ConvertException extends Exception {
   String ErrorMessage;
   String ErrorCode;
   Object[] additionalInfo;

   public ConvertException() {
      super("Conversion Failure.. Invalid Query..");
      this.ErrorMessage = "Conversion Failure.. Invalid Query..";
   }

   public ConvertException(String s_errmeg) {
      super(s_errmeg);
      this.ErrorMessage = s_errmeg;
   }

   public ConvertException(String s_errmeg, String errorCode) {
      super(s_errmeg);
      this.ErrorMessage = s_errmeg;
      this.ErrorCode = errorCode;
      this.additionalInfo = null;
   }

   public ConvertException(String s_errmeg, String errorCode, Object[] additionalInfo) {
      super(s_errmeg);
      this.ErrorMessage = s_errmeg;
      this.ErrorCode = errorCode;
      this.additionalInfo = additionalInfo;
   }

   public String toString() {
      return this.ErrorMessage;
   }

   public Object[] getAdditionalInfo() {
      return this.additionalInfo;
   }

   public String getErrorCode() {
      return this.ErrorCode;
   }

   public void setErrorCode(String val) {
      this.ErrorCode = val;
   }
}
