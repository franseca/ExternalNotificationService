/*
 * Decompiled with CFR 0.137.
 * 
 * Could not load the following classes:
 *  com.tecnotree.dap.output.util.GsResponseFormatUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.tecnotree.output.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsResponseFormatUtil {
    private static final Logger logger = LoggerFactory.getLogger(GsResponseFormatUtil.class);

    public static String format(String str, Map<String, Object> values) {
        logger.info("Start of format() method of  ResponseFormatUtil.class");
        if (logger.isDebugEnabled()) {
            logger.debug("str is:" + str);
            logger.debug("values is:" + values);
        }
        StringBuilder builder = new StringBuilder(str);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            int start;
            String key = entry.getKey().trim();
            Object value = entry.getValue();
            if (value == null) {
                logger.error("value is null, hence ignoring for key : {}", (Object)key);
                continue;
            }
            String findKey = key + "-NON_STRING_FIELD_TO_REPLACE";
            String patternforNonString = "'{:" + findKey + "}'";
            String patternforString = "{:" + key + "}";
            int index = builder.indexOf(findKey);
            String pattern = null;
            pattern = index != -1 ? patternforNonString : patternforString;
            while ((start = builder.indexOf(pattern)) != -1) {
                if (value != null) {
                    builder.replace(start, start + pattern.length(), value.toString());
                }
                if ((index = builder.indexOf(findKey)) != -1) {
                    pattern = patternforNonString;
                    continue;
                }
                pattern = patternforString;
            }
        }
        logger.info("End of format() method of  ResponseFormatUtil.class");
        return builder.toString();
    }
}

