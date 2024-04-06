package com.arsenal.framework.common.metrics;

/**
 * @author Gordon.Gan
 */
public class SummaryBuffer {

    public SummaryBuffer() {
    }

    private volatile long count = 0L;
    private volatile long sum = 0L;

    public long getCount() {
        return count;
    }

    private void setCount(long count) {
        this.count = count;
    }

    public long getSum() {
        return sum;
    }

    private void setSum(long sum) {
        this.sum = sum;
    }

    public void increment() {
        increment(0);
    }

    public void increment(int latency) {
        setCount(getCount() + 1);
        setSum(getSum() + latency);
    }
}
