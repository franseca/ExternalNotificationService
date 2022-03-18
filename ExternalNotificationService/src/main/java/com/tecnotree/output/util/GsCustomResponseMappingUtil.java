package com.tecnotree.output.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

public class GsCustomResponseMappingUtil {
   private static final Logger logger = LoggerFactory.getLogger(GsCustomResponseMappingUtil.class);

   public static JSONObject proccessException(Integer code, String message) {
      logger.info("Started GsCustomResponseMappingUtil.class : Proccess Exception message");
      Map<String, Object> errmap = new HashMap();
      if (null != code && code > 0) {
         errmap.put("code", code);
      } else {
         errmap.put("code", 500);
      }

      if (null != message && !message.isBlank()) {
         errmap.put("message", message);
      } else {
         errmap.put("message", "GENERIC SERVICE PROCESS FAILED");
      }

      logger.info("Ended GsCustomResponseMappingUtil.class");
      JSONObject errRes = new JSONObject();
      errRes.put("error", errmap);
      return errRes;
   }

   public static JSONObject successResponseFormat(Integer code, String message) {
      logger.info("Started GsCustomResponseMappingUtil.class : Proccess Success Message");
      String successFormat = "{\"response\":{\"body\":{\"code\":\"{:code}\",\"message\":\"{:message}\"}}}";
      Map<String, Object> values = new HashMap();
      if (null != code && code > 0) {
         values.put("code", code);
      } else {
         values.put("code", 200);
      }

      if (null != message && !message.isBlank()) {
         values.put("message", message);
      } else {
         values.put("message", "GENERIC SERVICE PROCESS SUCCESSFULLY COMPLETED");
      }

      logger.info("Ended GsCustomResponseMappingUtil.class");
      return JSONObject.fromObject(GsResponseFormatUtil.format(successFormat, values));
   }
}