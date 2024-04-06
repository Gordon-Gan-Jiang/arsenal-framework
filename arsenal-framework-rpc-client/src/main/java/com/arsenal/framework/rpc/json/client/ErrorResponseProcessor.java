package com.arsenal.framework.rpc.json.client;

/**
 * @author Gordon.Gan
 */
public interface ErrorResponseProcessor {

    /**
     * Process error response of json RPC. The status is not 2XX.
     */
    public void processExceptionResponse(Integer status, String errorJson, String serviceName) throws Exception;
}
