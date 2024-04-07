package com.arsenal.framework.rpc.base.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gordon.Gan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface ServiceClientSetting {
    static final int CONNECT_TIMEOUT = 2000;

    static final int READ_TIMEOUT = 5000;

    static final int MAX_ATTEMPT_TIMES = 3;

    /**
     * Retry times.
     */
    int retryTimes() default MAX_ATTEMPT_TIMES;


    /**
     * Connect timeout in milliseconds.
     */
    int connectTimeout() default CONNECT_TIMEOUT;

    /**
     * Read timeout in milliseconds.
     */
    int readTimeout() default READ_TIMEOUT;


    /**
     * Acceptable error code. When receives these error code, we do not retry. ONLY used for 5XX errors.
     */
    String[] acceptableErrorCode();
}
