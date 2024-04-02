package com.arsenal.framework.rpc;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gordon.Gan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.TYPE_PARAMETER, ElementType.CONSTRUCTOR})
@Qualifier
public @interface RpcClient {

    /**
     * The name of a RPC client bean must be prefixed with this, otherwise it will be registered as a request handler by
     * [org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping].
     */
    String BEAN_NAME_PREFIX = "scopedTarget.";

}
