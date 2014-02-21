package com.zhangyue.monitor.handler;

import com.zhangyue.monitor.exception.MailException;
import com.zhangyue.monitor.handler.MailHandler;
import com.zhangyue.monitor.handler.impl.HTTPMailHandler;

import junit.framework.TestCase;

public class HTTPMailHandlerTest extends TestCase{
  private MailHandler mailHandler = null;

  public void setUp() {
    mailHandler = new HTTPMailHandler();
  }

  public void testSendMail() {
    try {
      mailHandler.initialize("zxqt223@163.com");
      mailHandler.sendMail("TEST_MAIL", "This is my test mail111.");
    } catch (MailException e) {
      e.printStackTrace();
    }
  }
}
