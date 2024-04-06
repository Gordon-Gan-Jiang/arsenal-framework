package com.arsenal.framework.common.exception;

import com.arsenal.framework.common.metrics.MetricsUtils;
import com.arsenal.framework.model.exception.ArsenalError;
import com.arsenal.framework.model.exception.ArsenalErrorBean;
import com.arsenal.framework.model.exception.ArsenalException;
import com.arsenal.framework.model.exception.ArsenalRuntimeException;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.Map;

public class ArsenalExceptionBuilder {
    private int statusCode = ArsenalError.SERVER_INTERNAL_ERROR.getStatusCode();
    private String message;
    private String errorCode;
    private final Map<String, Object> meta = new HashMap<>();

    public ArsenalExceptionBuilder() {
    }

    public ArsenalExceptionBuilder(ArsenalError error) {
        this.message = error.getDetail();
        this.errorCode = error.getErrorCode();
        this.statusCode = error.getStatusCode();
    }

    public ArsenalExceptionBuilder requestId(String requestId) {
        if (requestId == null) {
            return this;
        }
        meta.put(ArsenalErrorBean.REQUEST_ID_FIELD, requestId);
        return this;
    }

    public ArsenalExceptionBuilder message(String message) {
        this.message = message;
        return this;
    }

    public ArsenalExceptionBuilder errorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public ArsenalExceptionBuilder statusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ArsenalExceptionBuilder source(String source) {
        if (source == null) {
            return this;
        }
        meta.put(ArsenalErrorBean.SOURCE_FIELD, source);
        return this;
    }

    public ArsenalExceptionBuilder meta(Map<String, Object> meta) {
        if (meta == null || meta.isEmpty()) {
            return this;
        }
        this.meta.putAll(meta);
        return this;
    }

    public ArsenalExceptionBuilder meta(String key, Object value) {
        meta.put(key, value);
        return this;
    }

    public ArsenalException toArsenalException() {
        recordMetrics();
        return new ArsenalException(message)
                .errorCode(errorCode)
                .statusCode(statusCode)
                .meta(meta);
    }

    public ArsenalException toArsenalException(String message) {
        recordMetrics();
        return new ArsenalException(message)
                .errorCode(errorCode)
                .statusCode(statusCode)
                .meta(meta);
    }

    public ArsenalException toArsenalException(Throwable t) {
        recordMetrics();
        return new ArsenalException(ExceptionUtils.getRootCauseMessage(t), t)
                .errorCode(errorCode)
                .statusCode(statusCode)
                .meta(meta);
    }

    public ArsenalException toArsenalException(String message, Throwable t) {
        recordMetrics();
        return new ArsenalException(message, t)
                .errorCode(errorCode)
                .statusCode(statusCode)
                .meta(meta);
    }

    public ArsenalException formatArsenalException(String message, Object... args) {
        recordMetrics();
        return new ArsenalException(String.format(message, args))
                .errorCode(errorCode)
                .statusCode(statusCode)
                .meta(meta);
    }

    public ArsenalException formatArsenalException(String message, Throwable t, Object... args) {
        recordMetrics();
        return new ArsenalException(String.format(message, args), t)
                .errorCode(errorCode)
                .statusCode(statusCode)
                .meta(meta);
    }

    public ArsenalRuntimeException toArsenalRuntimeException() {
        return toArsenalException().toRuntimeException();
    }

    public ArsenalRuntimeException toArsenalRuntimeException(String message) {
        return toArsenalException(message).toRuntimeException();
    }

    public ArsenalRuntimeException toArsenalRuntimeException(Throwable t) {
        return toArsenalException(t).toRuntimeException();
    }

    public ArsenalRuntimeException toArsenalRuntimeException(String message, Throwable t) {
        return toArsenalException(message, t).toRuntimeException();
    }

    public ArsenalRuntimeException formatArsenalRuntimeException(String message, Object... args) {
        return formatArsenalException(message, args).toRuntimeException();
    }

    public ArsenalRuntimeException formatArsenalRuntimeException(String message, Throwable t, Object... args) {
        return formatArsenalException(message, t, args).toRuntimeException();
    }

    private void recordMetrics() {
        // Record exception source.  
        Object source = meta.get(ArsenalErrorBean.SOURCE_FIELD);
        if (source == null) {
            return;
        }
        MetricsUtils.recordCounter(MetricsUtils.EXCEPTION_SOURCE_CATEGORY, source.toString(), Integer.toString(statusCode));
    }
}
