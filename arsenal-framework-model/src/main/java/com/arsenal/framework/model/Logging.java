package com.arsenal.framework.model;

import com.arsenal.framework.model.annotations.ArsenalLogTopic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Gordon.Gan
 */
public class Logging {
    public static Logger log;

    public Logging() {
        Class<?> clazz = getClass();
        log = getLogger(clazz);
    }

    private static Logger getLogger(Class<?> clazz) {
        ArsenalLogTopic logTopic = clazz.getAnnotation(ArsenalLogTopic.class);
        if (logTopic != null && !logTopic.value().isEmpty()) {
            return LogManager.getLogger(logTopic.value());
        } else {
            return LogManager.getLogger(clazz);
        }
    }
}
