package com.arsenal.framework.rpc;

import jakarta.ws.rs.core.UriBuilder;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Gordon.Gan
 */
public class ArsenalJerseyUriBuilder {
    private URI uri;
    private UriBuilder builder;

    public ArsenalJerseyUriBuilder(URI uri) {
        this.uri = uri;
        this.builder = JerseyUriBuilder.fromUri(uri);
    }

    public ArsenalJerseyUriBuilder uri(String p) {
        builder.uri(p);
        return this;
    }

    public ArsenalJerseyUriBuilder path(String p) {
        builder.path(p);
        return this;
    }

    public ArsenalJerseyUriBuilder queryParam(String q, String v) {
        if (v != null) {
            builder.queryParam(q, JerseyUtils.encodeAsQuery(v));
        }

        return this;
    }

    public URI build(Map<String, String> values) {
        return builder.buildFromMap(values.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, it -> JerseyUtils.encodeAsPath(it.getValue()))));
    }

    public URI build() {
        return builder.build();
    }

    public static ArsenalJerseyUriBuilder fromUri(String uri) {
        return new ArsenalJerseyUriBuilder(URI.create(uri));
    }

    public static ArsenalJerseyUriBuilder fromUri(URI uri) {
        return new ArsenalJerseyUriBuilder(uri);
    }

}
