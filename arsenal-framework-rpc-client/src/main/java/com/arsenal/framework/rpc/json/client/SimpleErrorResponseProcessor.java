package com.arsenal.framework.rpc.json.client;

import com.arsenal.framework.common.exception.ArsenalExceptionBuilder;
import com.arsenal.framework.model.utility.StringHelper;

/**
 * Simple implementation of [ErrorResponseProcessor] which converts error response to [Alo7Exception], in which the
 * error json has unknown schema.
 *
 * @author Gordon.Gan
 */
public class SimpleErrorResponseProcessor {

    public void processExceptionResponse(Integer status, String errorJson, String serviceName) {
        throw new ArsenalExceptionBuilder()
                .message("RPC failed: " + StringHelper.removeLineEnding(errorJson))
                .statusCode(status)
                .source(serviceName)
                .toArsenalRuntimeException();
    }
}
