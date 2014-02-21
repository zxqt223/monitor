package com.zhangyue.monitor;

import com.zhangyue.monitor.AlarmManager;
import com.zhangyue.monitor.MetricsManager;
import com.zhangyue.monitor.Monitor;
import com.zhangyue.monitor.alarm.impl.EmailAlarm;
import com.zhangyue.monitor.alarm.impl.MobilePhoneAlarm;
import com.zhangyue.monitor.exception.HandlerConstructException;
import com.zhangyue.monitor.exception.MailException;
import com.zhangyue.monitor.metrics.impl.DiskMetrics;
import com.zhangyue.monitor.metrics.impl.JVMMetrics;
import com.zhangyue.monitor.util.ParamsManager;

public class AppDemo {

    public static void main(String[] args) {
        ParamsManager.setString("metrics.update.interval", "2");
        HeapGenerator t = new HeapGenerator();
        t.start();
        Monitor monitor = Monitor.getMonitor();
        MetricsManager metricsManager = new MetricsManager();
        metricsManager.addMetricsContext(new JVMMetrics());
        metricsManager.addMetricsContext(new DiskMetrics(new String[] { "/" }));

        AlarmManager alarmManager = new AlarmManager();
//        try {
//            alarmManager.addAlarm(new MobilePhoneAlarm("13810089769", false));
//        } catch (HandlerConstructException e1) {
//            e1.printStackTrace();
//        }
        try {
            alarmManager.addAlarm(new EmailAlarm(
                "zhangxianquan@zhangyue.com", false));
        } catch (HandlerConstructException e1) {
            e1.printStackTrace();
        } catch (MailException e1) {
            e1.printStackTrace();
        }
        try {
            monitor.initialize(metricsManager, alarmManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(60 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class HeapGenerator extends Thread {

        public void run() {
            int[] arr = new int[5 * 1024 * 1024];
            int count = 10;
            while (count > 0) {
                arr = new int[10 * 1024 * 1024];
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count--;
            }
            System.out.println("array len : " + arr.length);
        }
    }
}
