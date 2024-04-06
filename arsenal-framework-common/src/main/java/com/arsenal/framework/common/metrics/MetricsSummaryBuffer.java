package com.arsenal.framework.common.metrics;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Gordon.Gan
 */
@Setter
@Getter
public class MetricsSummaryBuffer {
    public MetricsSummaryBuffer(MetricsKey metricsKey, SummaryBuffer summaryBuffer) {
        this.metricsKey = metricsKey;
        this.summaryBuffer = summaryBuffer;
    }

    private MetricsKey metricsKey;
    private SummaryBuffer summaryBuffer;
}
