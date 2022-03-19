package com.tecnotree.node.logger;

import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;
import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecnotree.exception.ExceptionCode;
import com.tecnotree.output.util.GsCustomResponseMappingUtil;

import net.sf.json.JSONObject;

public class GsLog {
   private static final Logger _log = LoggerFactory.getLogger(GsLog.class);
   private static ConcurrentHashMap<String, Document> logMap = new ConcurrentHashMap();

   public static void setRootKey(String rootKey, Document logData) {
      if (logData == null) {
         logData = new Document();
      }

      logMap.put(rootKey, logData);
   }

   public static void setLog(String rootKey, String key, Object value) {
      Document data = (Document)logMap.get(rootKey);
      data.put(key, value);
      logMap.put(rootKey, data);
   }

   public static void sendLogs(String rootKey) {
      (new Thread(new Runnable() {
         public void run() {
            String errMsg;
            try {
               Document doc = (Document)GsLog.logMap.get(rootKey);
               errMsg = doc.getInteger("statusCode") == 200 ? "SUCCESS" : "FAILED";
               doc.put("status", errMsg);
               System.out.println(JSONWriter.valueToString(doc));
               GsLog.logMap.remove(rootKey);
            } catch (Exception e) {
               errMsg = e.getLocalizedMessage();
               GsLog._log.error("Error Occured While Writing The logs -> " + errMsg);
               String msg = null == errMsg ? "LOGS PROCESSING FAILED DUE TO NULL VALUE" : errMsg;
               JSONObject errorRes = GsCustomResponseMappingUtil.proccessException(ExceptionCode.EXCEPTION_CODE, msg);
               JSONObject response = new JSONObject();
               response.put("errorResponse", errorRes);
               response.put("transactionID", rootKey);
               response.put("message", "Error Occured While Printing The Logs But Request Processed Successfully");
               System.out.println(JSONWriter.valueToString(response));
               GsLog.logMap.remove(rootKey);
            }

         }
      })).start();
   }

   public static void clearLogs(String rootKey) {
      logMap.remove(rootKey);
   }

   public static Document getLogs(String key) {
      return (Document)logMap.get(key);
   }
}