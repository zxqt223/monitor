package com.zhangyue.monitor.handler.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.zhangyue.monitor.handler.HTTPHandler;

public class DefaultHTTPHandler implements HTTPHandler {

    private HttpClient httpClient = null;
    private String requestURL = null;
    private boolean isGetRequest;

    public void initialize(String requestURL, boolean isGetRequest) {
        PoolingClientConnectionManager conMan = new PoolingClientConnectionManager( SchemeRegistryFactory.createDefault() );
        conMan.setMaxTotal(200);
        conMan.setDefaultMaxPerRoute(200);

        httpClient = new DefaultHttpClient(conMan);

        //The following parameter configurations are not
        //neccessary for this example, but they show how
        //to further tweak the HttpClient
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 20000);
        HttpConnectionParams.setSoTimeout(params, 15000);
        this.isGetRequest=isGetRequest;
        this.requestURL=requestURL;
    }

    public void sendRequest(List<NameValuePair> params) throws IOException{
        if (requestURL == null) {
            throw new IOException("There is no available http get request url!");
        }
        if(this.isGetRequest){
            sendGetRequest(params);
        }else{
            sendPostRequest(params);
        }
    }
    public synchronized void sendGetRequest(List<NameValuePair> params)
        throws IOException {
        HttpGet httpGet = new HttpGet(requestURL);
        String strParams =
                EntityUtils.toString(new UrlEncodedFormEntity(params));
        try {
            httpGet.setURI(new URI(httpGet.getURI().toString() + "?"
                                   + strParams));
        } catch (URISyntaxException e) {
            throw new IOException(
                "Fail to construct URI object.URISyntaxException is : "
                        + e.getMessage());
        }
        HttpResponse response = httpClient.execute(httpGet);
        int status = response.getStatusLine().getStatusCode();
        if (status != HttpStatus.SC_OK) {
            EntityUtils.consume(response.getEntity());
            throw new IOException("Fail to send get request!");
        }
    }

    public synchronized void sendPostRequest(List<NameValuePair> params)
        throws IOException {
        HttpPost httpPost = new HttpPost(requestURL);
        httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse response = httpClient.execute(httpPost);
        int status = response.getStatusLine().getStatusCode();
        httpPost.releaseConnection();
        if (status != HttpStatus.SC_OK) {
            EntityUtils.consume(response.getEntity());
            throw new IOException("Fail to send post request!");
        }
    }
}
