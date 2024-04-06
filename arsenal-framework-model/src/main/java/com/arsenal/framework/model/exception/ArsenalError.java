
package com.arsenal.framework.model.exception;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.hc.core5.http.HttpStatus;

import java.util.List;

/**
 * Errors for Arsenal.
 *
 * @author Gordon.Gan
 */
public final class ArsenalError {
    public static final ArsenalError NO_ERROR = new ArsenalError("", HttpStatus.SC_OK, "");

    /**
     * The error happens in server side.
     */
    public static final ArsenalError SERVER_INTERNAL_ERROR = new ArsenalError("error.common.server_error",
            HttpStatus.SC_INTERNAL_SERVER_ERROR, "Server internal error.");

    /**
     * The service is stopped.
     */
    public static final ArsenalError SERVICE_STOPPED = new ArsenalError("error.common.service_stopped",
            HttpStatus.SC_SERVICE_UNAVAILABLE, "Service is stopped, no longer supported.");

    /**
     * The server is busy.
     */
    public static final ArsenalError SERVER_BUSY = new ArsenalError("error.common.server_busy",
            HttpStatus.SC_SERVICE_UNAVAILABLE,
            "Server is busy, please try later.");

    /**
     * The service is disabled.
     */
    public static final ArsenalError SERVICE_DISABLED = new ArsenalError("error.common.service_disabled",
            HttpStatus.SC_SERVICE_UNAVAILABLE,
            "Service is temporarily disabled, please try later.");

    /**
     * The server is timeout.
     */
    public static final ArsenalError SERVER_TIMEOUT = new ArsenalError("error.common.server_timeout",
            HttpStatus.SC_GATEWAY_TIMEOUT,
            "Server is timeout, please try later.");

    /**
     * Bad request.
     */
    public static final ArsenalError BAD_REQUEST = new ArsenalError("error.common.bad_request", HttpStatus.SC_BAD_REQUEST,
            "Bad request.");

    /**
     * The request is forbidden.
     */
    public static final ArsenalError FORBIDDEN = new ArsenalError("error.common.forbidden", HttpStatus.SC_FORBIDDEN,
            "Operation is forbidden.");

    /**
     * The request is not authorized.
     */
    public static final ArsenalError NOT_AUTHORIZED = new ArsenalError("error.common.unauthorized",
            HttpStatus.SC_UNAUTHORIZED,
            "Operation is not authorized.");

    /**
     * The request resource is not found.
     */
    public static final ArsenalError NOT_FOUND = new ArsenalError("error.common.not_found", HttpStatus.SC_NOT_FOUND,
            "Request resource is not found.");

    /**
     * The request method is not allowed.
     */
    public static final ArsenalError METHOD_NOT_ALLOWED = new ArsenalError("error.common.method_not_allowed",
            HttpStatus.SC_METHOD_NOT_ALLOWED,
            "Request method is not allowed.");

    /**
     * The request is conflict with others.
     */
    public static final ArsenalError CONFLICT = new ArsenalError("error.common.conflict", HttpStatus.SC_CONFLICT,
            "The request could not be completed due to a conflict with the current state of the target resource.");

    /**
     * The database error.
     */
    public static final ArsenalError DATABASE_ERROR = new ArsenalError("error.common.database_error",
            HttpStatus.SC_INTERNAL_SERVER_ERROR, "Database error.");

    /**
     * The request has invalid argument.
     */
    public static final ArsenalError INVALID_ARGUMENT = new ArsenalError("error.common.invalid_argument",
            HttpStatus.SC_NOT_ACCEPTABLE, "Invalid argument.");

    public static final List<ArsenalError> VALUES = Lists.newArrayList(
            NO_ERROR,
            SERVER_INTERNAL_ERROR,
            SERVICE_STOPPED,
            SERVER_BUSY,
            BAD_REQUEST,
            FORBIDDEN,
            NOT_AUTHORIZED,
            NOT_FOUND,
            CONFLICT,
            DATABASE_ERROR,
            INVALID_ARGUMENT
    );

    @Getter
    private final String errorCode;
    @Getter
    private final int statusCode;
    @Getter
    private final String detail;

    public ArsenalError(String errorCode, int statusCode, String detail) {
        this.errorCode = errorCode;
        this.statusCode = statusCode;
        this.detail = detail;
    }

    @Deprecated
    public ArsenalError errorCode(String errorCode) {
        return new ArsenalError(errorCode, statusCode, detail);
    }

    @Deprecated
    public ArsenalError statusCode(int statusCode) {
        return new ArsenalError(errorCode, statusCode, detail);
    }

    @Deprecated
    public ArsenalError detail(String detail) {
        return new ArsenalError(errorCode, statusCode, detail);
    }

    public ArsenalException toAlo7Exception() {
        return new ArsenalException(detail)
                .errorCode(errorCode)
                .statusCode(statusCode);
    }

    public ArsenalException toAlo7Exception(String message) {
        return new ArsenalException(message)
                .errorCode(errorCode)
                .statusCode(statusCode);
    }

    public ArsenalException toAlo7Exception(Throwable t) {
        return new ArsenalException(ExceptionUtils.getRootCauseMessage(t), t)
                .errorCode(errorCode)
                .statusCode(statusCode);
    }

    public ArsenalException toAlo7Exception(String message, Throwable t) {
        return new ArsenalException(message, t)
                .errorCode(errorCode)
                .statusCode(statusCode);
    }

    public ArsenalException formatAlo7Exception(String message, Object... args) {
        return new ArsenalException(String.format(message, args))
                .errorCode(errorCode)
                .statusCode(statusCode);
    }

    public ArsenalException formatAlo7Exception(String message, Throwable t, Object... args) {
        return new ArsenalException(String.format(message, args), t)
                .errorCode(errorCode)
                .statusCode(statusCode);
    }

    public ArsenalRuntimeException toAlo7RuntimeException() {
        return toAlo7Exception().toRuntimeException();
    }

    public ArsenalRuntimeException toAlo7RuntimeException(String message) {
        return toAlo7Exception(message).toRuntimeException();
    }

    public ArsenalRuntimeException toAlo7RuntimeException(Throwable t) {
        return toAlo7Exception(t).toRuntimeException();
    }

    public ArsenalRuntimeException toAlo7RuntimeException(String message, Throwable t) {
        return toAlo7Exception(message, t).toRuntimeException();
    }

    public ArsenalRuntimeException formatAlo7RuntimeException(String message, Object... args) {
        return formatAlo7Exception(message, args).toRuntimeException();
    }

    public ArsenalRuntimeException formatAlo7RuntimeException(String message, Throwable t, Object... args) {
        return formatAlo7Exception(message, t, args).toRuntimeException();
    }
}
