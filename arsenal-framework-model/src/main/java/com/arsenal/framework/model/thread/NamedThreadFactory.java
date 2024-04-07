// Copyright 2022 Kingsoft Office Software Inc. All rights reserved.

package com.arsenal.framework.model.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Gordon.Gan
 */
public class NamedThreadFactory implements ThreadFactory {
    private String name;
    private Integer max = -1;
    private Boolean daemon = true;

    public NamedThreadFactory(String name) {
        this.name = name;
    }

    public NamedThreadFactory(String name, Integer max, Boolean daemon) {
        this.name = name;
        this.max = max;
        this.daemon = daemon;
    }

    private AtomicInteger threadIndex = new AtomicInteger(1);

    @Override
    public Thread newThread(Runnable target) {
        ArsenalThread wcdThread = null;
        if (max.equals(1)) {
            wcdThread = new ArsenalThread(target, name);
        } else if (max > 10 && max < 99) {
            wcdThread = new ArsenalThread(target, String.format("%s-%02d", name, threadIndex.getAndIncrement()));
        } else {
            wcdThread = new ArsenalThread(target, name + "-" + threadIndex.getAndIncrement());
        }
        wcdThread.setDaemon(daemon);
        return wcdThread;
    }
}
