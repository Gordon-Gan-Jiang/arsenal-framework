
package com.arsenal.framework.model.exception;

/**
 * Arsenal exception, to wrap any unexpected exception as runtime exception.
 *
 * @author Gordon.Gan
 */
public class ArsenalRuntimeException extends RuntimeException {
    public ArsenalRuntimeException(String message) {
        super(message);
    }

    public ArsenalRuntimeException(Throwable cause) {
        super(cause);
    }

    public ArsenalRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
