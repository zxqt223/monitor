package com.zhangyue.monitor.metrics;

import java.util.List;

import com.zhangyue.monitor.alarm.AlarmMessage;
import com.zhangyue.monitor.metrics.MetricsContext;
import com.zhangyue.monitor.metrics.impl.JVMMetrics;
import com.zhangyue.monitor.util.ParamsManager;

import junit.framework.TestCase;

public class JVMMetricsTest extends TestCase {
  private MetricsContext metricsContext = null;

  public void setUp() {
    ParamsManager.setString("memory.used.threshold", "0.9");
    metricsContext = new JVMMetrics();
  }

  public void testAlarmMessage() {
    metricsContext.doUpdates();
    int[] t = new int[1024 * 1024 * 10];
    while (!metricsContext.isMetricsValueException()) {
      t = new int[1024 * 1024 * 10];
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      metricsContext.doUpdates();
    }
    while (true) {
      List<AlarmMessage> list = metricsContext.getAlarmMessage();
      assertEquals(!list.isEmpty(), true);
      System.out.println(list.get(0).toString());
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
      }
      metricsContext.doUpdates();
      System.out.println("isMetricsValueException:"+metricsContext.isMetricsValueException());
    }
  }
}
