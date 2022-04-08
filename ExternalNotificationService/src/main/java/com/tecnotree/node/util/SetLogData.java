package com.tecnotree.node.util;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import com.tecnotree.node.action.db.exception.DataProcessingException;
import com.tecnotree.node.logger.EnLog;
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
      //logData.put("logType", 0);
      logData.put("userName", "system");
      logData.put("threadID", Thread.currentThread().getName());
      logData.put("serviceName", serviceName);
      logData.put("transactionID", transactionId);
      logData.put("operationName", "REST");
      JSONObject json = null;

      try {
         json = JSONObject.fromObject(reqPayload);
      } catch (Exception e) {
         throw new DataProcessingException("Invalid Json Payload", e);
      }

      logData.put("request", json);
      EnLog.setRootKey(transactionId, logData);
      return transactionId;
   }

   public void sendLogs(String instanceId, Integer code, Object response, Long startTime) {
      Integer statusCode = null;
      if (code == 200) {
         try {
            statusCode = (Integer)JsonPath.read(response, "$.error.code", new Predicate[0]);
         } catch (Exception e) {
            statusCode = code;
         }
      } else {
         statusCode = code;
      }

      EnLog.setLog(instanceId, "response", response);
      EnLog.setLog(instanceId, "statusCode", statusCode);
      EnLog.setLog(instanceId, "timeTaken", System.currentTimeMillis() - startTime);
      EnLog.sendLogs(instanceId);
   }
}