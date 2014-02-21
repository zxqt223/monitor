package com.zhangyue.monitor.handler;

import com.zhangyue.monitor.exception.MailException;
import com.zhangyue.monitor.handler.MailHandler;
import com.zhangyue.monitor.handler.impl.DefaultMailHandler;

import junit.framework.TestCase;

public class DefaultMailHandlerTest extends TestCase {

  private MailHandler mailHandler = null;

  public void setUp() {
    mailHandler = new DefaultMailHandler();
  }

  public void testSendMail() {
    try {
      mailHandler.initialize("zxqt223@163.com");
      mailHandler.sendMail("TEST_MAIL", "This is my test mail.");
    } catch (MailException e) {
      e.printStackTrace();
      fail();
    }
  }
}
