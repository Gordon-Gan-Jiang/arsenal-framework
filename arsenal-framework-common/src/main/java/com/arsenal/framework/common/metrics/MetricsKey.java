package com.arsenal.framework.common.metrics;

import com.arsenal.framework.model.utility.StringCache;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Gordon.Gan
 */
@Setter
@Getter
public class MetricsKey {
    private String category;
    private String name;
    private String subName;
    private String metrics;

    public MetricsKey(String category, String name, String subName, String metrics) {
        this.category = category;
        this.name = name;
        this.subName = subName;
        this.metrics = metrics;
    }

    public MetricsKey(String category, String name, String subName) {
        this.category = category;
        this.name = name;
        this.subName = subName;
    }

    public MetricsKey withMetrics(String metrics) {
        return new MetricsKey(category, name, subName, metrics);
    }

    public String fullname() {
        return subName.isEmpty() ? name : StringCache.queryConcat(name, ".", subName);
    }
}
