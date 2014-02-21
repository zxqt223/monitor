package com.zhangyue.monitor.handler;

import com.zhangyue.monitor.exception.MailException;

/**
 * @Descriptions The class MailHandler.java's implementation：The inferface of
 *               mail handler.
 * @author scott 2013-8-19 上午10:14:25
 * @version 1.0
 */
public interface MailHandler {

    public void initialize(String toAddress) throws MailException;

    public void sendMail(String subject, String content) throws MailException;

    public void sendMail(String[] emailToAddress, String subject, String content)
        throws MailException;
}
