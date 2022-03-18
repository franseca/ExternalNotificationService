package com.tecnotree.node.action.db.exception;

public class DataProcessingException extends Exception {
   private static final long serialVersionUID = 1L;

   public DataProcessingException() {
   }

   public DataProcessingException(String message) {
      super(message);
   }

   public DataProcessingException(Throwable cause) {
      super(cause);
   }

   public DataProcessingException(String message, Throwable cause) {
      super(message, cause);
   }

   public DataProcessingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }
}