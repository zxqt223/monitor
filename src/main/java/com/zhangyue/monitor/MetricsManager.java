package com.zhangyue.monitor;

import java.util.ArrayList;
import java.util.List;

import com.zhangyue.monitor.Monitor;
import com.zhangyue.monitor.alarm.AlarmMessage;
import com.zhangyue.monitor.exception.NullException;
import com.zhangyue.monitor.metrics.MetricsContext;
import com.zhangyue.monitor.util.ParamsManager;

/**
 * @Descriptions The class MetricsManager.java's implementation：TODO described
 *               the implementation of class
 * @author scott 2013-8-19 上午10:11:06
 * @version 1.0
 */
public class MetricsManager {

    private List<MetricsContext> mcList = new ArrayList<MetricsContext>();
    private Monitor monitor = null;
    private MetricsUpdater metricsUpdater=null;
    private volatile boolean isRunning = true;
    
    private long updateInterval;

    public MetricsManager(){
        this.updateInterval = ParamsManager.getLong("metrics.update.interval", 300) * 1000;
    }

    public void addMetricsContext(MetricsContext metricsContext) {
        mcList.add(metricsContext);
    }

    public void removeMetricsContext(MetricsContext metricsContext) {
        mcList.remove(metricsContext);
    }

    public void removeMetricsContext(int index) {
        mcList.remove(index);
    }

    public void close() {
        isRunning=false;
        mcList.clear();
        if(null != metricsUpdater){  //在当前场景更新线程可以直接中断结束，不需要等待线程执行完自然结束
            metricsUpdater.interrupt();
        }
    }

    void initialize(Monitor monitor) throws NullException {
        if (mcList.isEmpty()) {
            throw new NullException("There is no available metrics context!");
        }
        this.monitor = monitor;
        metricsUpdater=new MetricsUpdater();
        metricsUpdater.setDaemon(true);
        metricsUpdater.start();
    }

    private class MetricsUpdater extends Thread {

        public void run() {
            List<AlarmMessage> messages = new ArrayList<AlarmMessage>();
            while (isRunning) {
                for (MetricsContext metricsContext : mcList) {
                    metricsContext.doUpdates();
                    if (metricsContext.isMetricsValueException()) {
                        messages.addAll(metricsContext.getAlarmMessage());
                    }
                }
                if (!messages.isEmpty()) {
                    AlarmMessage[] arr = messages.toArray(new AlarmMessage[] {});
                    monitor.doNotify(arr);
                    messages.clear();
                }
                try {
                    Thread.sleep(updateInterval);
                } catch (InterruptedException e) {
                }
            }
        }
    }

}
