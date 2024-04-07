package com.arsenal.framework.model;

/**
 * @author Gordon.Gan
 */
public class StartupArgs {
    public static volatile int apiThreadCount = 16;
    public static volatile int slowApiThreadCount = 8;
    public static volatile int maxSlowRequestCount = 400;
    public static volatile int maxDbConnectionCount = 24;
    public static volatile int maxAsyncDbConnectionCount = 16;
    public static volatile int threadLocalBufferCountPerThread = 16;
    public static volatile int threadLocalBufferSize = 4096;
    public static volatile int gzipMinResponseSize = 4096;
    public static volatile boolean useAhc = true;
    public static volatile boolean supportHttp2 = false;
    public static volatile boolean enableApacheHttp2 = false;
    public static volatile boolean enableAhcGzip = true;
    public static volatile boolean enableAccessLog = true;
    public static volatile boolean inlineAccessLog = false;
    public static volatile String accessLogFormat = "\"%{time,yyyy-MM-dd HH:mm:ss.SSSZ} %a -- \"%r\" %s %b %D -- \"%{i,x-forwarded-for}\" \"%{i,Referer}\" \"%{i,User-Agent}\" -- \"%{o,Content-Encoding}\" \"%{o,X-Arsenal-Request-Id}\"";
    //public static volatile Alo7UniformAppender.Setting uniformAppenderSetting = Alo7UniformAppender.Setting.ENABLED;
    @Deprecated
   // public static volatile boolean useChunkedEncoding = false;
    public static volatile boolean disableFormData = false;
    public static volatile long gracefulShutdownTimeout = 60000L;

}
