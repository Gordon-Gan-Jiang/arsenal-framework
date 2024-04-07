package com.arsenal.framework.common.exception;

import com.arsenal.framework.model.exception.ArsenalError;

/**
 * @author Gordon.Gan
 */
public class ArsenalExceptionUtils {

    public static ArsenalExceptionBuilder builder(ArsenalError arsenalError) {
        return new ArsenalExceptionBuilder(arsenalError);
    }
}
