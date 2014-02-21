package com.zhangyue.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zhangyue.monitor.alarm.Alarm;
import com.zhangyue.monitor.alarm.AlarmMessage;
import com.zhangyue.monitor.exception.AlarmException;
import com.zhangyue.monitor.exception.NullException;
import com.zhangyue.monitor.util.ParamsManager;

/**
 * @Descriptions The class AlarmManager.java's implementation：TODO described the
 *               implementation of class
 * @author scott 2013-8-19 上午10:10:50
 * @version 1.0
 */
public class AlarmManager {

    private ExecutorService threadPool = null;
    private List<Alarm> alarms = new ArrayList<Alarm>();

    private static final Log LOG = LogFactory.getLog(AlarmManager.class);

    public void addAlarm(Alarm alarm) {
        synchronized (alarms) {
            alarms.add(alarm);
        }
    }

    public void removeAlarm(Alarm alarm) {
        synchronized (alarms) {
            alarms.remove(alarm);
        }
    }

    public void removeAlarm(int index) {
        synchronized (alarms) {
            alarms.remove(index);
        }
    }

    public void close(){
        this.alarms.clear();
        if(null != this.threadPool){
            this.threadPool.shutdown();
        }
    }
    
    void initialize() throws NullException {
        if (alarms.isEmpty()) {
            throw new NullException("There is no available alarm method!");
        }
        threadPool =
                Executors.newFixedThreadPool(ParamsManager.getInt(
                    "message.sender.thread.number", 10));
    }
    
    void sendAlarmMessage(AlarmMessage[] messages) {
        for (Alarm alarm : alarms) {
            threadPool.execute(new SenderThread(alarm, messages));
        }
    }

    private class SenderThread extends Thread {

        private AlarmMessage[] messages = null;
        private Alarm alarm = null;

        private SenderThread(Alarm alarm, AlarmMessage[] messages){
            this.alarm = alarm;
            this.messages = messages;
        }

        public void run() {
            try {
                alarm.doSend(messages);
            } catch (AlarmException e) {
                LOG.error("Fail to send alarm message.", e);
            }
        }
    }
}
