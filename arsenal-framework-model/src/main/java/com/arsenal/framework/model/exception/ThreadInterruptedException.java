
package com.arsenal.framework.model.exception;

/**
 * @author Gordon.Gan
 */
public class ThreadInterruptedException extends RuntimeException {
    private static final long serialVersionUID = 9088225711164368803L;

    public ThreadInterruptedException() {
    }

    public ThreadInterruptedException(String message) {
        super(message);
    }

    public ThreadInterruptedException(Throwable cause) {
        super(cause);
    }

    public ThreadInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
