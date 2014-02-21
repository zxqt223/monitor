package com.zhangyue.monitor.handler.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.zhangyue.monitor.exception.HandlerConstructException;
import com.zhangyue.monitor.exception.MailException;
import com.zhangyue.monitor.handler.HTTPHandler;
import com.zhangyue.monitor.handler.HTTPHandlerFactory;
import com.zhangyue.monitor.handler.MailHandler;
import com.zhangyue.monitor.util.InetAddressUtil;
import com.zhangyue.monitor.util.ParamsManager;

/**
 * @Descriptions The class RRMailHandler.java's implementation：TODO described
 *               the implementation of class
 * @author scott 2013-8-19 上午10:15:04
 * @version 1.0
 */
public class HTTPMailHandler implements MailHandler {

    private NameValuePair toAddressNVP = null;
    private HTTPHandler httpHandler = null;
    private String localhost = null;

    public void initialize(String toAddress) throws MailException {
        if (toAddress != null) {
            this.toAddressNVP = new BasicNameValuePair("to", toAddress);
        }
        try {
            httpHandler = HTTPHandlerFactory.getInstance();
        } catch (HandlerConstructException e) {
            throw new MailException(e);
        }
        httpHandler.initialize(ParamsManager.getString("email.service.url"),
            ParamsManager.getBoolean("email.service.http.get.request", true));
        localhost = InetAddressUtil.getLocalHost();
    }

    public void sendMail(String subject, String content) throws MailException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair titleNVP =
                new BasicNameValuePair("subject", subject + "(" + localhost
                                                  + ")");
        NameValuePair contentNVP = new BasicNameValuePair("message", content);
        params.add(titleNVP);
        params.add(toAddressNVP);
        params.add(contentNVP);
        try {
            httpHandler.sendRequest(params);
        } catch (IOException e) {
            throw new MailException(e);
        }
    }

    public void sendMail(String[] emailToAddress, String subject, String content)
        throws MailException {
        StringBuffer sb = new StringBuffer();
        int len = emailToAddress.length;
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(emailToAddress[i]);
        }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair titleNVP =
                new BasicNameValuePair("subject", subject + "(" + localhost
                                                  + ")");
        NameValuePair contentNVP = new BasicNameValuePair("message", content);
        params.add(titleNVP);
        params.add(new BasicNameValuePair("to", sb.toString()));
        params.add(contentNVP);
        try {
            httpHandler.sendRequest(params);
        } catch (IOException e) {
            throw new MailException(e);
        }
    }
}
