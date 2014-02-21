package com.zhangyue.monitor.handler.impl;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.zhangyue.monitor.exception.MailException;
import com.zhangyue.monitor.handler.MailHandler;
import com.zhangyue.monitor.util.InetAddressUtil;
import com.zhangyue.monitor.util.ParamsManager;

/**
 * 
 * @Descriptions The class DefaultMailHandler.java's implementation：TODO described the implementation of class
 * @author scott 2013-8-19 上午10:14:54
 * @version 1.0
 */
public class DefaultMailHandler implements MailHandler {

    private Message mailMessage = null;
    private String localhost = null;

    public void initialize(String toAddress) throws MailException {
        String fromAddress = ParamsManager.getString("email.from.address");
        Properties prop = new Properties();
        prop.put("mail.smtp.host", getSMTPHostName(fromAddress));
        prop.put("mail.smtp.auth", "true");

        MailAuthenticator authenticator =
                new MailAuthenticator(fromAddress,
                    ParamsManager.getString("email.from.password"));
        Session sendMailSession =
                Session.getDefaultInstance(prop, authenticator);
        mailMessage = new MimeMessage(sendMailSession);

        InternetAddress from;
        try {
            from =
                    new InternetAddress(
                        ParamsManager.getString("email.from.address"));
        } catch (AddressException e) {
            throw new MailException("Fail to get email.from.address,detail is "
                                    + e.getMessage());
        }
        // 创建邮件的接收者地址，并设置到邮件消息中
        if (toAddress != null) {
            InternetAddress[] toList;
            try {
                toList = InternetAddress.parse(toAddress);
            } catch (AddressException e) {
                throw new MailException(
                    "Fail to get email.to.address,detail is " + e.getMessage());
            }
            try {
                mailMessage.setRecipients(Message.RecipientType.TO, toList);
            } catch (MessagingException e) {
                throw new MailException(
                    "Fail to set mail recipients message,detail is "
                            + e.getMessage());
            }
        }
        localhost = InetAddressUtil.getLocalHost();
        // 设置邮件公共信息
        try {
            mailMessage.setFrom(from);
            mailMessage.setSentDate(new Date());
        } catch (MessagingException e) {
            throw new MailException(
                "Fail to set common mail message,detail is " + e.getMessage());
        }
    }

    private String getSMTPHostName(String username) {
        return "smtp." + username.split("@")[1];
    }

    public void sendMail(String subject, String content) throws MailException {
        try {
            mailMessage.setSubject(subject + "(" + localhost + ")");
            mailMessage.setContent(content, "text/html;charset=utf-8");
        } catch (MessagingException e) {
            throw new MailException(
                "Fail to set mail title or content,detail is " + e.getMessage());
        }
        try {
            Transport.send(mailMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new MailException("Fail to send mail,detail is "
                                    + e.getMessage());
        }
    }

    public void sendMail(String[] emailToAddress, String subject, String content)
        throws MailException {
        if(emailToAddress==null||emailToAddress.length==0){
            throw new MailException("There is no available mail receipients address.");
        }
        StringBuffer sb = new StringBuffer();
        int len = emailToAddress.length;
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(emailToAddress[i]);
        }
        InternetAddress[] toList;
        try {
            toList = InternetAddress.parse(sb.toString());
        } catch (AddressException e) {
            throw new MailException(
                "Fail to get email.to.address,detail is " + e.getMessage());
        }
        try {
            mailMessage.setRecipients(Message.RecipientType.TO, toList);
            mailMessage.setSubject(subject + "(" + localhost + ")");
            mailMessage.setContent(content, "text/html;charset=utf-8");
        } catch (MessagingException e) {
            throw new MailException(
                "Fail to set mail title or content,detail is " + e.getMessage());
        }
        try {
            Transport.send(mailMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new MailException("Fail to send mail,detail is "
                                    + e.getMessage());
        }
    }

    private class MailAuthenticator extends Authenticator {

        private String username;
        private String password;

        MailAuthenticator(String username, String password){
            super();
            this.username = username;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }
}
