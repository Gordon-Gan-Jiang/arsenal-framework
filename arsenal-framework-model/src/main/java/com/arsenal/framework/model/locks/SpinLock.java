package com.arsenal.framework.model.locks;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Gordon.Gan
 */
public class SpinLock {
    private AtomicBoolean locked = new AtomicBoolean(false);

    public <T> T use(Callable<T> f) {
        lock();
        try {
            return f.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            unlock();
        }
    }

    public void lock() {
        int spins = 0;
        while (true) {
            if (locked.compareAndSet(false, true)) {
                return;
            }
            if (spins > 1000) {
                Thread.yield();
            } else {
                spins++;
            }
        }
    }

    public void unlock() {
        locked.set(false);
    }
}
