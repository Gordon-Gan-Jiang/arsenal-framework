// Copyright 2022 Kingsoft Office Software Inc. All rights reserved.

package com.arsenal.framework.model.thread;

import io.netty.util.concurrent.FastThreadLocalThread;

/**
 * @author Gordon.Gan
 */
public class FastThreadLocal<T> {
    private final ThreadLocal normalThreadLocal = new ThreadLocal<T>();
    private final io.netty.util.concurrent.FastThreadLocal nettyThreadLocal =
            new io.netty.util.concurrent.FastThreadLocal<T>();
    private final Integer pos = FastThreadLocalRepository.registerThreadLocal(this);

    public T get() {
        Thread thread = Thread.currentThread();
        if (thread instanceof ArsenalThread) {
            ArsenalThread wcdThread = (ArsenalThread) thread;
            Object[] values = wcdThread.getThreadLocalValues();
            if (pos < values.length) {
                return (T) values[pos.intValue()];
            } else {
                expandArray(wcdThread, pos);
                return null;
            }
        }

        if (thread instanceof FastThreadLocalThread) {
            return (T) nettyThreadLocal.get();
        }

        return (T) normalThreadLocal.get();
    }

    public T safeGet(FastThreadLocalFactory<T> factory) {
        if (get() != null) {
            return get();
        } else {
            set(factory.getObject());
            return factory.getObject();
        }
    }

    public void set(T value) {
        Thread thread = Thread.currentThread();
        if (thread instanceof ArsenalThread) {
            ArsenalThread wcdThread = (ArsenalThread) thread;
            Object[] values = wcdThread.getThreadLocalValues();
            if (pos >= values.length) {
                expandArray(wcdThread, pos);
            }
            wcdThread.getThreadLocalValues()[pos] = value;
        } else if (thread instanceof FastThreadLocalThread) {
            nettyThreadLocal.set(value);
        } else {
            normalThreadLocal.set(value);
        }
    }

    public T safeGet(Runnable f) {
        T value = get();
        if (value == null) {
            f.run();
            set(value);
        }
        return value;
    }

    private void expandArray(ArsenalThread thread, Integer pos) {
        int oldSize = thread.getThreadLocalValues().length;
        int newSize = oldSize;
        while (newSize <= pos) {
            newSize *= 2;
        }
        int[] newArray = new int[newSize];
        System.arraycopy(thread.getThreadLocalValues(), 0, newArray, 0, oldSize);
    }

}
