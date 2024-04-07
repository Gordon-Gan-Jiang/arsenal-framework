// Copyright 2022 Kingsoft Office Software Inc. All rights reserved.

package com.arsenal.framework.model.thread;

/**
 * @author Gordon.Gan
 */
public class ArsenalThread extends Thread {
    private Object[] threadLocalValues = new Object[32];

    private ArsenalThread() {
        super();
    }

    private ArsenalThread(Runnable target) {
        super(target);
    }

    ArsenalThread(Runnable target, String name) {
        super(target, name);
    }

    public Object[] getThreadLocalValues() {
        return threadLocalValues;
    }

    public void setThreadLocalValues(Object[] threadLocalValues) {
        this.threadLocalValues = threadLocalValues;
    }
}
