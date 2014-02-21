package com.zhangyue.monitor.metrics;

import java.util.List;

import com.zhangyue.monitor.alarm.AlarmMessage;

/**
 * @Descriptions The class MetricsContext.java's implementation：TODO described
 *               the implementation of class
 * @author scott 2013-8-19 上午10:15:20
 * @version 1.0
 */
public interface MetricsContext {

    public void doUpdates();

    public boolean isMetricsValueException();

    public List<AlarmMessage> getAlarmMessage();
}
