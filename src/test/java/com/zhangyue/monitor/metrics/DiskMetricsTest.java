package com.zhangyue.monitor.metrics;

import java.util.List;

import com.zhangyue.monitor.alarm.AlarmMessage;
import com.zhangyue.monitor.metrics.MetricsContext;
import com.zhangyue.monitor.metrics.impl.DiskMetrics;
import com.zhangyue.monitor.util.ParamsManager;

import junit.framework.TestCase;

public class DiskMetricsTest extends TestCase {

  private MetricsContext metricsContext = null;

  public void setUp() {
    ParamsManager.setString("disk.used.threshold", "0.1");
    metricsContext = new DiskMetrics(new String[] { "/" });
    metricsContext.doUpdates();
  }

  public void testDoUpdates() {
    if (metricsContext.isMetricsValueException()) {
      List<AlarmMessage> list = metricsContext.getAlarmMessage();
      assertEquals(!list.isEmpty(), true);
    }
  }

}
