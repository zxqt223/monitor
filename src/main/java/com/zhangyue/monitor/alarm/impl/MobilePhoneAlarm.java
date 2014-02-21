package com.zhangyue.monitor.alarm.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.zhangyue.monitor.alarm.Alarm;
import com.zhangyue.monitor.alarm.AlarmMessage;
import com.zhangyue.monitor.exception.AlarmException;
import com.zhangyue.monitor.exception.HandlerConstructException;
import com.zhangyue.monitor.handler.HTTPHandler;
import com.zhangyue.monitor.handler.HTTPHandlerFactory;
import com.zhangyue.monitor.util.InetAddressUtil;
import com.zhangyue.monitor.util.ParamsManager;

/**
 * @Descriptions The class MobilePhoneAlarm.java's implementation：TODO described
 *               the implementation of class
 * @author scott 2013-8-18 下午3:26:14
 * @version 1.0
 */
public class MobilePhoneAlarm implements Alarm {

    private HTTPHandler httpHandler;
    private NameValuePair numberNVP = null;
    private String localHost = null;
    private boolean isNeedDynamicChoosePhoneNumber;

    private static final Log LOG = LogFactory.getLog(MobilePhoneAlarm.class);

    /**
     * 多个号码用逗号分割
     * 
     * @param administorMobilePhoneNumbers
     * @param isNeedDynamicChoosePhoneNumber 是否需要动态选择短信接收者
     * @throws HandlerConstructException
     */
    public MobilePhoneAlarm(String administorMobilePhoneNumbers,
                            boolean isNeedDynamicChoosePhoneNumber)
        throws HandlerConstructException{
        if (administorMobilePhoneNumbers == null
            || "".equals(administorMobilePhoneNumbers)) {
            return;
        }
        this.isNeedDynamicChoosePhoneNumber = isNeedDynamicChoosePhoneNumber;
        if (!isNeedDynamicChoosePhoneNumber) {
            numberNVP =
                    new BasicNameValuePair("number",
                        administorMobilePhoneNumbers);
        }
        initialize();
    }

    private void initialize() throws HandlerConstructException {
        httpHandler = HTTPHandlerFactory.getInstance();
        httpHandler.initialize(
            ParamsManager.getString("mobilephone.message.service.url"),ParamsManager.getBoolean("mobilephone.message.service.http.get.request", true));
        localHost = InetAddressUtil.getLocalHost();
    }

    public void doSend(AlarmMessage[] messages) throws AlarmException {
        if (messages == null) {
            LOG.error("Can't send this message,because the messages is null.");
            return;
        }

        if (isNeedDynamicChoosePhoneNumber) {
            for (AlarmMessage message : messages) {
                NameValuePair tmpNVP;
                if (message.getMobilePhoneNumbers() == null
                    || message.getMobilePhoneNumbers().length == 0) {
                    tmpNVP = numberNVP;
                    LOG.info(" send mobile phone message to administrator :"
                             + numberNVP.getValue() + " the content is : "
                             + message);
                } else {
                    StringBuffer sb = new StringBuffer();
                    int len = message.getMobilePhoneNumbers().length;
                    for (int i = 0; i < len; i++) {
                        if (i > 0) {
                            sb.append(",");
                        }
                        sb.append(message.getMobilePhoneNumbers()[i]);
                    }
                    tmpNVP = new BasicNameValuePair("number", sb.toString());
                    LOG.info(" send mobile phone message to user :"
                             + sb.toString() + " the content is : " + message);
                }

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                NameValuePair messageNVP =
                        new BasicNameValuePair("message", message.toString());
                params.add(tmpNVP);
                params.add(messageNVP);
                try {
                    httpHandler.sendRequest(params);
                } catch (IOException e) {
                    LOG.error("Fail to send mobile phone message,the content is : "
                              + message
                              + ". Exception detail is : "
                              + e.getMessage());
                    continue;
                }
            }
        } else {
            for (AlarmMessage message : messages) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                NameValuePair messageNVP =
                        new BasicNameValuePair("message", "(" + localHost + ")"
                                                          + message.toString());
                params.add(numberNVP);
                params.add(messageNVP);
                try {
                    httpHandler.sendRequest(params);
                } catch (IOException e) {
                    LOG.error("Fail to send mobile phone message,the content is : "
                              + message
                              + ". Exception detail is : "
                              + e.getMessage());
                    continue;
                }
            }
        }

    }

}
