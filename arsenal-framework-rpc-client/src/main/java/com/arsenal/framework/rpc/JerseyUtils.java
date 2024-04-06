package com.arsenal.framework.rpc;

import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.uri.UriComponent.Type;

/**
 * @author Gordon.Gan
 */
public class JerseyUtils {

    public static String encodeAsQueryParam(String url) {
        return UriComponent.encode(url, Type.QUERY_PARAM);
    }

    public static String encodeAsQuery(String url) {
        return UriComponent.encode(url, Type.QUERY);
    }

    public static String encodeAsPath(String url) {
        return UriComponent.encode(url, Type.PATH);
    }
}
