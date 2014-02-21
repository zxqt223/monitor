package com.zhangyue.monitor.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.zhangyue.monitor.handler.HTTPHandler;
import com.zhangyue.monitor.handler.impl.DefaultHTTPHandler;

import junit.framework.TestCase;

public class DefaultHTTPHandlerTest extends TestCase{
  private HTTPHandler httpHandler=null;
  public void setUp(){
    httpHandler=new DefaultHTTPHandler();
	httpHandler.initialize("http://sms.notify.d.xiaonei.com:2000/receiver",true);
  }
  public void testGetRequest(){
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    NameValuePair nv = new BasicNameValuePair ("number","13810089769");
    NameValuePair nv1 = new BasicNameValuePair ("message", "this is a my test message22222!");
    params.add(nv);
    params.add(nv1);
    try {
      httpHandler.sendRequest(params);
	} catch (IOException e) {
	  e.printStackTrace();
	  fail();
	} 
  }
}
