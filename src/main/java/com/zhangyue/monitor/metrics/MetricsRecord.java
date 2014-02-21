package com.zhangyue.monitor.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Descriptions The class MetricsRecord.java's implementation：TODO described
 *               the implementation of class
 * @author scott 2013-8-19 上午10:15:34
 * @version 1.0
 */
public class MetricsRecord {

    public enum State {
        OK, ERROR
    }

    private Map<String, String> metrics = new HashMap<String, String>();
    private String metricName = null;
    private State metricState;
    private int countThreshold;
    private long count = 0;

    public MetricsRecord(String metricName, int countThreshold){
        this.metricName = metricName;
        this.metricState = State.OK;
        this.countThreshold = countThreshold;
    }

    public void addMetric(String key, String value) {
        metrics.put(key, value);
    }

    public void clearMetrics() {
        metrics.clear();
    }

    public State getMetricState() {
        return metricState;
    }

    public void setMetricState(State metricState) {
        this.metricState = metricState;
        if (this.metricState == State.ERROR) {
            this.count++;
        }
    }

    public String getMetricName() {
        return this.metricName;
    }

    /**
     * judge the metrics normal or not
     * 
     * @return
     */
    public boolean isMetricNormal() {
        if (this.metricState == State.OK) {
            if (this.count == 0) {
                return true;
            } else {
                this.count = 0;
                return false;
            }
        }
        if (this.metricState == State.ERROR) {
            // warning message exceed threshold,it does not send message again
            if (this.count > this.countThreshold) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        Set<Map.Entry<String, String>> set = metrics.entrySet();
        if (set.isEmpty()) {
            return "no metrics";
        }
        StringBuffer message = new StringBuffer();
        boolean isFirst = true;
        for (Map.Entry<String, String> me : set) {
            if (isFirst) {
                isFirst = false;
            } else {
                message.append(",");
            }
            message.append(me.getKey() + ":" + me.getValue());
        }
        return message.toString() + ",status:" + this.metricState;
    }
}
