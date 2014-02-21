package com.zhangyue.monitor.handler;

import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;

/**
 * Description: Http handler interface<br>
 * Copyright: Copyright (c) 2012 <br>
 * Company: www.renren.com
 * 
 * @author xianquan.zhang{xianquan.zhang@renren.inc.com} 2013-1-1
 * @version 1.0
 */
/**
 * @Descriptions The class HTTPHandler.java's implementation：Http handler
 *               interface
 * @author scott 2013-8-19 上午10:13:41
 * @version 1.0
 */
public interface HTTPHandler {

    public void initialize(String requestURL, boolean isGetRequest);

    public void sendRequest(List<NameValuePair> params) throws IOException;
}
