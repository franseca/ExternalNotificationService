package com.tecnotree.node.util;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import com.tecnotree.node.action.db.exception.DataProcessingException;
import com.tecnotree.node.logger.GsLog;
import java.time.LocalDateTime;
import java.util.UUID;
import net.sf.json.JSONObject;
import org.bson.Document;

public class SetLogData {
   
	public String setLogInfo(String serviceName, String logLevel, String reqPayload) throws DataProcessingException {
      String transactionId = UUID.randomUUID().toString();
      Document logData = new Document();
      logData.put("timestamp", LocalDateTime.now());
      logData.put("level", logLevel);
      logData.put("logType", 0);
      logData.put("userName", "system");
      logData.put("threadID", Thread.currentThread().getId());
      logData.put("serviceName", serviceName);
      logData.put("transactionID", transactionId);
      JSONObject json = null;

      try {
         json = JSONObject.fromObject(reqPayload);
      } catch (Exception e) {
         throw new DataProcessingException("Invalid Json Payload", e);
      }

      logData.put("request", json);
      GsLog.setRootKey(transactionId, logData);
      return transactionId;
   }

   public void sendLogs(String instanceId, Integer code, Object response, Long startTime) {
      Integer statusCode = null;
      if (code == 200) {
         try {
            statusCode = (Integer)JsonPath.read(response, "$.error.code", new Predicate[0]);
         } catch (Exception var7) {
            statusCode = code;
         }
      } else {
         statusCode = code;
      }

      GsLog.setLog(instanceId, "response", response);
      GsLog.setLog(instanceId, "statusCode", statusCode);
      GsLog.setLog(instanceId, "timeTaken", System.currentTimeMillis() - startTime);
      GsLog.sendLogs(instanceId);
   }
}