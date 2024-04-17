package com.arsenal.framework.rpc.apache;

import com.arsenal.framework.rpc.ArsenalJerseyUriBuilder;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;


import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.CompletableFuture;

public class HttpRequest {
    private final ArsenalJerseyUriBuilder builder;
    private final CloseableHttpClient client;
    private final CloseableHttpAsyncClient asyncClient;
    private final int connectTimeout;
    private final int readTimeout;
    private Map<String, String> headerMap;
    private AsyncEntityProducer asyncEntityProducer;

    public HttpRequest(String host, int connectTimeout, int readTimeout,
                       CloseableHttpClient client, CloseableHttpAsyncClient asyncClient) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.client = client;
        this.asyncClient = asyncClient;
        this.builder = Alo7JerseyUriBuilder.fromUri(host);
    }

    public HttpRequest(String host) {
        this(host, 2000, 5000, httpClient, asyncHttpClient);
    }

    public HttpRequest uri(String value) {
        builder.uri(value);
        return this;
    }

    public HttpRequest path(String value) {
        builder.path(value);
        return this;
    }

    public HttpRequest queryParam(String key, Object value) {
        builder.queryParam(key, value != null ? value.toString() : null);
        return this;
    }

    public HttpRequest header(String key, String value) {
        if (headerMap == null) {
            headerMap = new java.util.HashMap<>();
        }
        headerMap.put(key, value);
        return this;
    }

    public CloseableHttpResponse performGet() throws IOException {
        return get().execute(client);
    }

    public CloseableHttpResponse performPost(Object obj, ContentType contentType) throws IOException {
        return post(obj, contentType, false).execute(client);
    }

    public CloseableHttpResponse performJsonPost(Object obj) throws IOException {
        return performPost(obj, Alo7Constant.APPLICATION_JSON_UTF8);
    }

    public CloseableHttpResponse performPut(Object obj, ContentType contentType) throws IOException {
        return put(obj, contentType, false).execute(client);
    }

    public CloseableHttpResponse performJsonPut(Object obj) throws IOException {
        return performPut(obj, Alo7Constant.APPLICATION_JSON_UTF8);
    }

    public CloseableHttpResponse performPatch(Object obj, ContentType contentType) throws IOException {
        return patch(obj, contentType, false).execute(client);
    }

    public CloseableHttpResponse performJsonPatch(Object obj) throws IOException {
        return performPatch(obj, Alo7Constant.APPLICATION_JSON_UTF8);
    }

    public CloseableHttpResponse performDelete() throws IOException {
        return delete().execute(client);
    }

    public CloseableHttpResponse performOptions() throws IOException {
        return options().execute(client);
    }

    public CloseableHttpResponse performHead() throws IOException {
        return head().execute(client);
    }

    public CloseableHttpResponse performTrace() throws IOException {
        return trace().execute(client);
    }

    public CompletableFuture<Message<HttpResponse, HttpEntity>> performGetSuspend() {
        return get().asyncExecuteSuspend(asyncClient);
    }

    public CompletableFuture<Message<HttpResponse, HttpEntity>> performPostSuspend(Object obj, ContentType contentType) {
        return post(obj, contentType, true).asyncExecuteSuspend(asyncEntityProducer, asyncClient);
    }

    public CompletableFuture<Message<HttpResponse, HttpEntity>> performPutSuspend(Object obj, ContentType contentType) {
        return put(obj, contentType, true).asyncExecuteSuspend(asyncEntityProducer, asyncClient);
    }

    public CompletableFuture<Message<HttpResponse, HttpEntity>> performPatchSuspend(Object obj, ContentType contentType) {
        return patch(obj, contentType, true).asyncExecuteSuspend(asyncEntityProducer, asyncClient);
    }

    public CompletableFuture<Message<HttpResponse, HttpEntity>> performDeleteSuspend() {
        return delete().asyncExecuteSuspend(asyncClient);
    }

    public CompletableFuture<Message<HttpResponse, HttpEntity>> performOptionsSuspend() {
        return options().asyncExecuteSuspend(asyncClient);
    }

    public CompletableFuture<Message<HttpResponse, HttpEntity>> performHeadSuspend() {
        return head().asyncExecuteSuspend(asyncClient);
    }

    public CompletableFuture<Message<HttpResponse, HttpEntity>> performTraceSuspend() {
        return trace().asyncExecuteSuspend(asyncClient);
    }

    private HttpUriRequestBase get() {
        HttpGet httpGet = new HttpGet(build());
        prepare(httpGet);
        return httpGet;
    }

    private HttpUriRequestBase post(Object obj, ContentType contentType, boolean async) {
        HttpPost httpPost = new HttpPost(build());
        prepareRequestBody(httpPost, obj, contentType, async);
        prepare(httpPost);
        return httpPost;
    }

    private HttpUriRequestBase put(Object obj, ContentType contentType, boolean async) {
        HttpPut httpPut = new HttpPut(build());
        prepareRequestBody(httpPut, obj, contentType, async);
        prepare(httpPut);
        return httpPut;
    }

    private HttpUriRequestBase patch(Object obj, ContentType contentType, boolean async) {
        HttpPatch httpPatch = new HttpPatch(build());
        prepareRequestBody(httpPatch, obj, contentType, async);
        prepare(httpPatch);
        return httpPatch;
    }

    private HttpUriRequestBase delete() {
        HttpDelete httpDelete = new HttpDelete(build());
        prepare(httpDelete);
        return httpDelete;
    }

    private HttpUriRequestBase options() {
        HttpOptions httpOptions = new HttpOptions(build());
        prepare(httpOptions);
        return httpOptions;
    }

    private HttpUriRequestBase head() {
        HttpHead httpHead = new HttpHead(build());
        prepare(httpHead);
        return httpHead;
    }

    private HttpUriRequestBase trace() {
        HttpTrace httpTrace = new HttpTrace(build());
        prepare(httpTrace);
        return httpTrace;
    }

    private void prepare(HttpUriRequestBase request) {
        RequestConfig config = configCache.getIfAbsent(combineInts(connectTimeout, readTimeout),
                () -> RequestConfig.custom()
                        .setConnectTimeout(connectTimeout)
                        .setSocketTimeout(readTimeout)
                        .build());
        request.setConfig(config);

        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }

        if (!headerMap.containsKey(Alo7Constant.X_REQUEST_ID_HEADER) &&
                !headerMap.containsKey(Alo7Constant.X_ALO7_REQUEST_ID_HEADER)) {
            String requestId = RpcRequestInfoUtils.getRequestId();
            request.setHeader(Alo7Constant.X_REQUEST_ID_HEADER, requestId);
            request.setHeader(Alo7Constant.X_ALO7_REQUEST_ID_HEADER, requestId);
        }
    }

    private void prepareRequestBody(HttpEntityEnclosingRequestBase request, Object obj,
                                    ContentType contentType, boolean async) {
        if (async) {
            asyncEntityProducer = toAsyncEntityProducer(obj, contentType);
        } else {
            request.setEntity(toHttpEntity(obj, contentType, false));
        }
    }

    private String build() {
        return builder.build();
    }

    private static final AtomicImmutableMap<Long, RequestConfig> configCache = new AtomicImmutableMap<>();
    private static final CloseableHttpClient httpClient = ...; // Define your default HTTP client here
    private static final CloseableHttpAsyncClient asyncHttpClient = ...; // Define your default asynchronous HTTP client here
}
