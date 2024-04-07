package com.arsenal.framework.rpc.ahc;

import static io.netty.handler.codec.http.HttpHeaders.setHeader;

import com.arsenal.framework.common.utility.NettyUtils;
import com.arsenal.framework.model.ArsenalConstant;
import com.arsenal.framework.model.RuntimeInfo;
import com.arsenal.framework.model.StartupArgs;
import com.arsenal.framework.model.io.MultiNettyByteBufBuffer;
import com.arsenal.framework.model.json.JsonConverter;
import com.arsenal.framework.model.utility.JodaUtils;
import com.arsenal.framework.rpc.ahc.entity.MultiNettyByteBufBody;
import com.arsenal.framework.rpc.base.client.ServiceClientSetting;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.hc.core5.http.HttpHeaders;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.netty.NettyResponse;
import org.asynchttpclient.request.body.Body;
import org.asynchttpclient.request.body.generator.BodyGenerator;
import org.asynchttpclient.request.body.multipart.Part;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Gordon.Gan
 */
public class AhcUtils {

    private static final int INFINITE_READ_TIMEOUT = -1;
    private static final Field BODY_PARTS_FIELD = FieldUtils.getDeclaredField(NettyResponse.class, "bodyParts", true);

    public static final AsyncHttpClient ahcClient = Dsl.asyncHttpClient(
            Dsl.config()
                    .setEventLoopGroup(NettyUtils.sharedEventLoopGroup)
                    .setConnectTimeout(ServiceClientSetting.CONNECT_TIMEOUT)
                    .setReadTimeout(ServiceClientSetting.READ_TIMEOUT)
                    .setRequestTimeout(ServiceClientSetting.CONNECT_TIMEOUT + ServiceClientSetting.READ_TIMEOUT)
                    .setMaxConnections(1024)
                    .setMaxConnectionsPerHost(128)
                    .setAcquireFreeChannelTimeout(ServiceClientSetting.CONNECT_TIMEOUT)
                    .setPooledConnectionIdleTimeout(Long.valueOf(60 * JodaUtils.MILLISECONDS_PER_SECOND).intValue())
                    .setConnectionPoolCleanerPeriod(Long.valueOf(10 * JodaUtils.MILLISECONDS_PER_SECOND).intValue())
                    .setMaxRequestRetry(0)
                    .setCompressionEnforced(StartupArgs.enableAhcGzip)
                    .setFollowRedirect(true)
                    .setUserAgent("AHC/" + RuntimeInfo.fullServiceNameAndVersion())
    );

    public MultiNettyByteBufBody buildRequestBody(Object obj, String contentType, BoundRequestBuilder builder) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof Body) {
            builder.setBody(new BodyGenerator() {
                @Override
                public Body createBody() {
                    return (Body) obj;
                }
            });
            if (contentType != null) {
                builder.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
            }
            return null;
        } else if (obj instanceof BodyGenerator) {
            builder.setBody((BodyGenerator) obj);
            if (contentType != null) {
                builder.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
            }
            return null;
        } else if (obj instanceof Part) {
            builder.addBodyPart((Part) obj);
            return null;
        } else if (obj instanceof List && isParts((List) obj)) {
            builder.setBodyParts((List<Part>) obj);
            return null;
        } else if (obj instanceof String) {
            String str = (String) obj;
            MultiNettyByteBufBody body = new MultiNettyByteBufBody(str.utf8EncodeToMultiNettyByteBuf(true));
            builder.setBody(new BodyGenerator() {
                @Override
                public Body createBody() {
                    return body;
                }
            });
            setHeader(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : Alo7Constant.TEXT_PLAIN_UTF8);
            return body;
        } else {
            MultiNettyByteBufBuffer buffer = new MultiNettyByteBufBuffer(true);
            JsonConverter.serializeTo(obj, buffer.asOutputStream());
            MultiNettyByteBufBody body = new MultiNettyByteBufBody(buffer);
            builder.setBody(new BodyGenerator() {
                @Override
                public Body createBody() {
                    return body;
                }
            });
            builder.setHeader(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : ArsenalConstant.APPLICATION_JSON_UTF8);
            return body;
        }
    }

    private Boolean isParts(Object object) {
        Boolean result = false;
        if (object instanceof List) {
            List<Object> objects = (List<Object>) object;
            for (Object it : objects) {
                if (it instanceof Part) {
                    result = true;
                } else {
                    return false;
                }
            }

        }

        return result;
    }
}
