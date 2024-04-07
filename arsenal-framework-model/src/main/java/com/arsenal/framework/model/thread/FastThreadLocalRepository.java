
package com.arsenal.framework.model.thread;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gordon.Gan
 */
public class FastThreadLocalRepository {
    private static final List<FastThreadLocal<?>> threadLocals = new ArrayList<FastThreadLocal<?>>();

    public static Integer registerThreadLocal(FastThreadLocal<?> threadLocal) {
        synchronized (threadLocals) {
            Integer pos = threadLocals.size();
            threadLocals.add(threadLocal);
            return pos;
        }
    }
}
