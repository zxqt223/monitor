package com.zhangyue.monitor.alarm;

import com.zhangyue.monitor.exception.AlarmException;

/**
 * @Descriptions The class Alarm.java's implementation：TODO described the
 *               implementation of class
 * @author scott 2013-8-19 上午10:11:33
 * @version 1.0
 */
public interface Alarm {

    public void doSend(AlarmMessage[] messages) throws AlarmException;

}
