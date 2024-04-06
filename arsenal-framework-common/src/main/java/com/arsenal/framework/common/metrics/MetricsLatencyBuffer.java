package com.arsenal.framework.common.metrics;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Gordon.Gan
 */
@Setter
@Getter
public class MetricsLatencyBuffer {
    private MetricsKey metricsKey;
    private volatile LatencyBuffer latencyBuffer;

    public MetricsLatencyBuffer(MetricsKey metricsKey, LatencyBuffer latencyBuffer) {
        this.metricsKey = metricsKey;
        this.latencyBuffer = latencyBuffer;
    }
}
