package com.zhangyue.monitor.metrics.impl;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zhangyue.monitor.alarm.AlarmMessage;
import com.zhangyue.monitor.metrics.MetricsContext;
import com.zhangyue.monitor.metrics.MetricsRecord;
import com.zhangyue.monitor.metrics.MetricsRecord.State;
import com.zhangyue.monitor.util.ParamsManager;

/**
 * @Descriptions The class JVMMetrics.java's implementation：TODO described the
 *               implementation of class
 * @author scott 2013-8-19 上午10:16:01
 * @version 1.0
 */
public class JVMMetrics implements MetricsContext {

    private static final float M = 1024 * 1024;
    private static final String MEMORY_MONITOR = "memory_monitor";
    private static final String FULL_GC_MONITOR = "full_gc_monitor";

    private Map<String, MetricsRecord> metrics;
    private List<AlarmMessage> messages;

    // garbage collection counters
    private long currentGCCount = 0;
    private long lastGCCount = 0;

    private double memoryUsedThreshold;
    private int warningCountThreshold;
    private Set<String> oldGenCollectorNames;
    private DecimalFormat df;

    private static final Log LOG = LogFactory.getLog(JVMMetrics.class);

    /** Creates a new instance of JvmMetrics */
    public JVMMetrics(){
        metrics = new HashMap<String, MetricsRecord>();
        messages = new ArrayList<AlarmMessage>();
        memoryUsedThreshold =
                ParamsManager.getDouble("memory.used.threshold", 0.90);
        warningCountThreshold =
                ParamsManager.getInt("warning.count.threshold", 3);
        df = new DecimalFormat("#.0");
        oldGenCollectorNames = new HashSet<String>();
        String[] arr =
                ParamsManager.getString("old.generation.collector.names").split(
                    ",");
        if (arr != null) {
            for (String o : arr) {
                oldGenCollectorNames.add(o);
            }
        }
    }

    /**
     * This will be called periodically (with the period being configuration
     * dependent).
     */
    public void doUpdates() {
        doMemoryUpdates();
        doGarbageCollectionUpdates();
    }

    public static long getHeapMemoryUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memHeap = memoryMXBean.getHeapMemoryUsage();
        return memHeap.getUsed();
    }

    public static long getTotalMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory();
    }

    private void doMemoryUpdates() {
        double maxMemory = getTotalMemory();
        long memHeapUsed = getHeapMemoryUsage();

        MetricsRecord metric;
        if (metrics.containsKey(MEMORY_MONITOR)) {
            metric = metrics.get(MEMORY_MONITOR);
            metric.clearMetrics();
        } else {
            metric = new MetricsRecord(MEMORY_MONITOR, warningCountThreshold);
            metrics.put(MEMORY_MONITOR, metric);
        }
        String strMaxMemory = (maxMemory / M) + "M";
        String strMemHeapUsed = (memHeapUsed / M) + "M";
        if (memHeapUsed / maxMemory >= memoryUsedThreshold) {
            metric.setMetricState(MetricsRecord.State.ERROR);
            LOG.warn("memory warning.maxMemory:" + strMaxMemory
                     + ",memHeapUsed:" + strMemHeapUsed);
        } else {
            metric.setMetricState(MetricsRecord.State.OK);
        }
        metric.addMetric("maxMemory", strMaxMemory);
        metric.addMetric("memHeapUsed", strMemHeapUsed);
    }

    /** collect garbage count */
    private long collectCount() {
        long totalGCTimeMillis = 0;
        currentGCCount = 0;
        List<GarbageCollectorMXBean> gcBeans =
                ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            if (oldGenCollectorNames.contains(gcBean.getName())) {
                currentGCCount += gcBean.getCollectionCount();
                totalGCTimeMillis += gcBean.getCollectionTime();
            }
        }
        return totalGCTimeMillis;
    }

    /**
     * update garbage collection 更新策略，采集数据5次，每次之间间隔1s,累计GC次数，如果至少有4次发生full
     * GC,则报警
     */
    private void doGarbageCollectionUpdates() {
        collectCount();
        lastGCCount = currentGCCount;
        int threshold = 5;
        int gcCount = 0;
        int totalGCTimeMillis = 0;
        /** excute collectCount() 3 times,and sleep 1s every times */
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        for (int i = 0; i < threshold; i++) {
            totalGCTimeMillis += collectCount();
            gcCount += currentGCCount - lastGCCount;
            lastGCCount = currentGCCount;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        MetricsRecord metric;
        if (metrics.containsKey(FULL_GC_MONITOR)) {
            metric = metrics.get(FULL_GC_MONITOR);
            metric.clearMetrics();
        } else {
            metric = new MetricsRecord(FULL_GC_MONITOR, warningCountThreshold);
            metrics.put(FULL_GC_MONITOR, metric);
        }
        /**
         * if jvm do full gc at least 1 times per second,then do full gc
         * frequently
         */
        String strAvgFGCCount = df.format(gcCount / 5f) + "";
        String strAvgFGCTime = df.format(totalGCTimeMillis / 5f);
        if (gcCount >= 4) {
            metric.setMetricState(State.ERROR);
            LOG.warn("Full gc warning.avgFGCCount:" + strAvgFGCCount
                     + ",avgFGCTime:" + strAvgFGCTime);
        } else {
            metric.setMetricState(State.OK);
        }

        metric.addMetric("avgFGCCount", strAvgFGCCount);
        metric.addMetric("avgFGCTime", strAvgFGCTime + " millis");
    }

    public boolean isMetricsValueException() {
        if (!messages.isEmpty()) {
            messages.clear();
        }
        boolean res = false;
        MetricsRecord memoryMR = metrics.get(MEMORY_MONITOR);
        if (memoryMR != null && !memoryMR.isMetricNormal()) {
            messages.add(new AlarmMessage(memoryMR.getMetricName(),
                memoryMR.toString()));
            res = true;
        }

        MetricsRecord fullGCMR = metrics.get(FULL_GC_MONITOR);
        if (fullGCMR != null && !fullGCMR.isMetricNormal()) {
            messages.add(new AlarmMessage(fullGCMR.getMetricName(),
                fullGCMR.toString()));
            res = true;
        }
        return res;
    }

    public List<AlarmMessage> getAlarmMessage() {
        return this.messages;
    }
}
