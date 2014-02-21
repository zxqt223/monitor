package com.zhangyue.monitor.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.zhangyue.monitor.handler.HTTPHandler;
import com.zhangyue.monitor.handler.impl.DefaultHTTPHandler;

import junit.framework.TestCase;

public class DefaultHTTPHandlerTest1 extends TestCase{
  private HTTPHandler httpHandler=null;
  public void setUp(){
    httpHandler=new DefaultHTTPHandler();
	httpHandler.initialize("http://192.168.1.100:50002/uploadJobFlow",true);
  }
  public void testGetRequest(){
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    NameValuePair nv = new BasicNameValuePair ("jobFlowID", "jf0001");
    NameValuePair nv1 = new BasicNameValuePair ("fileName", "myjobflow.jar");
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
