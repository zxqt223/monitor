package com.zhangyue.monitor.util;

import com.zhangyue.monitor.util.ParamsManager;

import junit.framework.TestCase;

public class ParamsManagerTest extends TestCase{

  public void testGetString(){
    String mobilePhoneMessageServiceURL=ParamsManager.getString("mobilephone.message.service.url");
    assertTrue(mobilePhoneMessageServiceURL!=null);
  }
}
