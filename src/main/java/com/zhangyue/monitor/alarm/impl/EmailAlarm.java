package com.zhangyue.monitor.alarm.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zhangyue.monitor.alarm.Alarm;
import com.zhangyue.monitor.alarm.AlarmMessage;
import com.zhangyue.monitor.exception.AlarmException;
import com.zhangyue.monitor.exception.HandlerConstructException;
import com.zhangyue.monitor.exception.MailException;
import com.zhangyue.monitor.handler.MailHandler;
import com.zhangyue.monitor.handler.impl.DefaultMailHandler;
import com.zhangyue.monitor.util.ParamsManager;

/**
 * @Descriptions The class EmailAlarm.java's implementation：TODO described the
 *               implementation of class
 * @author scott 2013-8-18 下午3:26:14
 * @version 1.0
 */
public class EmailAlarm implements Alarm {

    private MailHandler mailHandler = null;
    private boolean isNeedDynamicChooseRecipients;
    private String[] administratorRecipientsArr;

    private static final Log LOG = LogFactory.getLog(EmailAlarm.class);

    /**
     * 多个邮件接收人用逗号分隔
     * 
     * @param administratorRecipients 管理员邮件接收者
     * @param isNeedDynamicChooseRecipients 是否需要动态选择接收者
     * @throws HandlerConstructException
     * @throws MailException
     */
    public EmailAlarm(String administratorRecipients,
                      boolean isNeedDynamicChooseRecipients)
        throws HandlerConstructException, MailException{
        if (administratorRecipients == null
            || "".equals(administratorRecipients)) {
            throw new MailException("There is no mail recipients addresses.");
        }
        this.isNeedDynamicChooseRecipients = isNeedDynamicChooseRecipients;
        constructHandler();
        if (isNeedDynamicChooseRecipients) {
            administratorRecipientsArr = administratorRecipients.split(",");
            mailHandler.initialize(null);
        } else {
            mailHandler.initialize(administratorRecipients);
        }
    }

    private void constructHandler() throws HandlerConstructException {
        String strAlarmHandler = ParamsManager.getString("email.alarm.handler");
        if (strAlarmHandler == null || "".equals(strAlarmHandler)) {
            mailHandler = new DefaultMailHandler();
        } else {
            Constructor<?> constructor = null;
            try {
                Class<?> clazz = Class.forName(strAlarmHandler);
                constructor = clazz.getDeclaredConstructor(new Class[] {});
                constructor.setAccessible(true);
            } catch (ClassNotFoundException e) {
                throw new HandlerConstructException(e);
            } catch (SecurityException e) {
                throw new HandlerConstructException(e);
            } catch (NoSuchMethodException e) {
                throw new HandlerConstructException(e);
            }
            try {
                mailHandler = (MailHandler) constructor.newInstance();
            } catch (IllegalArgumentException e) {
                throw new HandlerConstructException(e);
            } catch (InstantiationException e) {
                throw new HandlerConstructException(e);
            } catch (IllegalAccessException e) {
                throw new HandlerConstructException(e);
            } catch (InvocationTargetException e) {
                throw new HandlerConstructException(e);
            }
        }
    }

    public void doSend(AlarmMessage[] messages) throws AlarmException {
        if (messages == null || "".equals(messages)) {
            LOG.error("Can't send this message,because the messages is null.");
            return;
        }
        if (isNeedDynamicChooseRecipients) {
            String[] mailAddress;
            for (AlarmMessage message : messages) {
                mailAddress = message.getEmailToAddress();
                if (mailAddress == null) {
                    if(administratorRecipientsArr==null){
                        LOG.warn("There is no address to send mail.Content : "+message);
                        continue;
                    }
                    mailAddress = administratorRecipientsArr;
                }
                try {
                    mailHandler.sendMail(mailAddress, message.getTitle(),
                        message.getContent());
                } catch (MailException e) {
                    LOG.error("Fail to send mail,the mail content is : "
                              + message + ". Exception detail is : "
                              + e.getMessage());
                    continue;
                }
            }
        } else {
            for (AlarmMessage message : messages) {
                try {
                    mailHandler.sendMail(message.getTitle(),
                        message.getContent());
                } catch (MailException e) {
                    LOG.error("Fail to send mail,the mail content is : "
                              + message + ". Exception detail is : "
                              + e.getMessage());
                    continue;
                }
            }
        }
    }

}
