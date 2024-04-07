package com.arsenal.framework.common.utility;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.internal.PlatformDependent;
import org.apache.hc.core5.concurrent.DefaultThreadFactory;

/**
 * @author Gordon.Gan
 */
public class NettyUtils {
    public static final int ioThreadCount = Runtime.getRuntime().availableProcessors();
    public static final EventLoopGroup sharedEventLoopGroup = createEventLoopGroup();

    public static void shutdown() {
        sharedEventLoopGroup.shutdownGracefully();
    }

    private static EventLoopGroup createEventLoopGroup() {
        DefaultThreadFactory threadFactory = new DefaultThreadFactory("netty");
        if (PlatformDependent.isOsx()) {
            if (PlatformDependent.normalizedArch().equals("aarch_64")) {
                return new NioEventLoopGroup(ioThreadCount, threadFactory);
            } else {
                return new KQueueEventLoopGroup(ioThreadCount, threadFactory);
            }
        } else if (PlatformDependent.isWindows()) {
            return new NioEventLoopGroup(ioThreadCount, threadFactory);
        } else {
            return new EpollEventLoopGroup(ioThreadCount, threadFactory);
        }
    }
}