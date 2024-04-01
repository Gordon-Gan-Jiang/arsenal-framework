package com.arsenal.framework.model.utility;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;

/**
 * @author Gordon.Gan
 */
@Slf4j
public class ObjectHelper {

    public static Class<?> classNameToClass(String className) {
        try {
            return ClassUtils.getClass(className);
        } catch (ClassNotFoundException e) {
            log.debug("get class failed.", e);
            return null;
        }
    }
}
