package com.zhangyue.monitor;

import com.zhangyue.monitor.alarm.AlarmMessage;

/**
 * @Descriptions The class Monitor.java's implementation：TODO described the
 *               implementation of class
 * @author scott 2013-8-19 上午10:11:21
 * @version 1.0
 */
public class Monitor {

    private static Monitor monitor;
    private static boolean isInit = false;
    private MetricsManager metricsManager = null;
    private AlarmManager alarmManager = null;

    public static Monitor getMonitor() {
        if (monitor == null) {
            synchronized (Monitor.class) {
                if (monitor == null) {
                    monitor = new Monitor();
                }
            }
        }
        return monitor;
    }

    public synchronized void initialize(MetricsManager metricsManager,
        AlarmManager alarmManager) throws Exception {
        if (isInit) {
            return;
        }
        isInit = true;
        this.metricsManager = metricsManager;
        this.alarmManager = alarmManager;
        this.metricsManager.initialize(monitor);
        this.alarmManager.initialize();
    }

    protected void doNotify(AlarmMessage[] alarmMessages) {
        alarmManager.sendAlarmMessage(alarmMessages);
    }
}
