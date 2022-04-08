package com.tecnotree.output.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

public class EnCustomResponseMappingUtil {
   private static final Logger logger = LoggerFactory.getLogger(EnCustomResponseMappingUtil.class);

   public static JSONObject proccessException(Integer code, String message) {
      logger.debug("Started EnCustomResponseMappingUtil.class : Proccess Exception message");
      Map<String, Object> errmap = new HashMap<String, Object>();
      if (null != code && code > 0) {
         errmap.put("code", code);
      } else {
         errmap.put("code", 500);
      }

      if (null != message && !message.isBlank()) {
         errmap.put("message", message);
      } else {
         errmap.put("message", "External Notification service process failed");
      }

      logger.debug("Ended EnCustomResponseMappingUtil.class");
      JSONObject errRes = new JSONObject();
      errRes.put("error", errmap);
      return errRes;
   }

   public static JSONObject successResponseFormat(Integer code, String message) {
      logger.info("Started EnCustomResponseMappingUtil.class : Proccess Success Message");
      String successFormat = "{\"response\":{\"body\":{\"code\":\"{:code}\",\"message\":\"{:message}\"}}}";
      Map<String, Object> values = new HashMap<String, Object>();
      if (null != code && code > 0) {
         values.put("code", code);
      } else {
         values.put("code", 200);
      }

      if (null != message && !message.isBlank()) {
         values.put("message", message);
      } else {
         values.put("message", "EXTERNAL NOTIFICATION SERVICE PROCESS SUCCESSFULLY COMPLETED");
      }

      logger.info("Ended EnCustomResponseMappingUtil.class");
      return JSONObject.fromObject(EnResponseFormatUtil.format(successFormat, values));
   }
}