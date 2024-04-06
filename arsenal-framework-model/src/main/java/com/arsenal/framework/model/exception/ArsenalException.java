
package com.arsenal.framework.model.exception;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Map;

/**
 * Arsenal exception with error code.
 *
 * @author Gordon.Gan
 */
public class ArsenalException extends Exception {
    @Getter
    @Setter
    private int statusCode = ArsenalError.SERVER_INTERNAL_ERROR.getStatusCode();
    @Getter
    @Setter
    private String errorCode;
    @Getter
    @Setter
    private Map<String, Object> meta;

    public ArsenalException() {
    }

    public ArsenalException(String message) {
        super(message);
    }

    public ArsenalException(Throwable cause) {
        super(ExceptionUtils.getRootCauseMessage(cause), cause);
    }

    public ArsenalException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArsenalException requestId(String requestId) {
        if (requestId != null) {
            getOrCreateMeta().put(ArsenalErrorBean.REQUEST_ID_FIELD, requestId);
        }
        return this;
    }

    public ArsenalException errorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public ArsenalException statusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ArsenalException meta(Map<String, Object> meta) {
        if (meta != null) {
            getOrCreateMeta().putAll(meta);
        }
        return this;
    }

    public ArsenalException meta(String key, Object value) {
        if (key != null && value != null) {
            getOrCreateMeta().put(key, value);
        }
        return this;
    }

    public ArsenalRuntimeException toRuntimeException() {
        return new ArsenalRuntimeException(this);
    }

    @Override
    public String toString() {
        return getClass().getName() + ": "
                + "("
                + statusCode
                + ", "
                + errorCode
                + ") "
                + getLocalizedMessage();
    }

    private Map<String, Object> getOrCreateMeta() {
        if (meta == null) {
            meta = Maps.newHashMap();
        }
        return meta;
    }
}
