package com.tecnotree.output.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsResponseFormatUtil {
   private static final Logger logger = LoggerFactory.getLogger(GsResponseFormatUtil.class);

   public static String format(String str, Map<String, Object> values) {
      logger.info("Satrt of format() method of  ResponseFormatUtil.class");
      if (logger.isDebugEnabled()) {
         logger.debug("str is:" + str);
         logger.debug("values is:" + values);
      }

      StringBuilder builder = new StringBuilder(str);
      Iterator var3 = values.entrySet().iterator();

      while(true) {
         while(var3.hasNext()) {
            Entry<String, Object> entry = (Entry)var3.next();
            String key = ((String)entry.getKey()).trim();
            Object value = entry.getValue();
            if (value == null) {
               logger.error("value is null, hence ignoring for key : {}", key);
            } else {
               String findKey = key + "-NON_STRING_FIELD_TO_REPLACE";
               String patternforNonString = "'{:" + findKey + "}'";
               String patternforString = "{:" + key + "}";
               int index = builder.indexOf(findKey);
               String pattern = null;
               if (index != -1) {
                  pattern = patternforNonString;
               } else {
                  pattern = patternforString;
               }

               int start;
               while((start = builder.indexOf(pattern)) != -1) {
                  if (value != null) {
                     builder.replace(start, start + pattern.length(), value.toString());
                  }

                  index = builder.indexOf(findKey);
                  if (index != -1) {
                     pattern = patternforNonString;
                  } else {
                     pattern = patternforString;
                  }
               }
            }
         }

         logger.info("End of format() method of  ResponseFormatUtil.class");
         return builder.toString();
      }
   }
}