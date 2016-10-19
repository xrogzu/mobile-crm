package com.rkhd.ienterprise.apps.ingage.dingtalk.util;


import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.XsyApiUserService;
import com.rkhd.ienterprise.util.HttpClientUtil;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dell on 2015/12/17.
 */
public class MwebHttpClientUtil   {
    private static final Logger LOG = LoggerFactory.getLogger(MwebHttpClientUtil.class);
    private static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 3000;
    private static final String CHARSET = "utf-8";
    private static final MwebHttpClientUtil INSTANCE = new MwebHttpClientUtil();
    private static SSLConnectionSocketFactory sslConnectionSocketFactory;
    private static RequestConfig config;

    public static MwebHttpClientUtil getInstance() {
        return INSTANCE;
    }

    private MwebHttpClientUtil() {
        this.initTrustHosts();
        this.initConfig();
    }

    private void initConfig() {
        config = RequestConfig.custom().setConnectTimeout(3000).setSocketTimeout(10000).build();
    }

    private void initTrustHosts() {
        try {
            SSLContext e = (new SSLContextBuilder()).loadTrustMaterial((KeyStore)null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
           // sslConnectionSocketFactory = new SSLConnectionSocketFactory(e);
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(e,new String[] { "TLSv1" },null,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (NoSuchAlgorithmException var2) {
            var2.printStackTrace();
        } catch (KeyManagementException var3) {
            var3.printStackTrace();
        } catch (KeyStoreException var4) {
            var4.printStackTrace();
        }

    }

    public CloseableHttpClient createClient() {
        return HttpClients.custom().setDefaultRequestConfig(config).setSSLSocketFactory(sslConnectionSocketFactory).build();
    }

    public static String basicAuthorizationGet(String authorization,String url, Map<String, String> params) throws IOException {
        String result = null;
        try{
            StringBuilder sb = new StringBuilder(url);

            if(params != null && !params.isEmpty()) {
                ArrayList httpGet = new ArrayList(params.size());
                Iterator response = params.entrySet().iterator();

                while(response.hasNext()) {
                    Map.Entry statusCode = (Map.Entry)response.next();
                    String entity = (String)statusCode.getValue();
                    if(entity != null) {
                        httpGet.add(new BasicNameValuePair((String)statusCode.getKey(), entity));
                    }
                }

                sb.append("?").append(EntityUtils.toString(new UrlEncodedFormEntity(httpGet, "utf-8")));
            }

            HttpGet httpGet1 = new HttpGet(sb.toString());
           // httpGet1.addHeader("Authorization",authorization);
    //        httpGet1.addHeader("BasicAccessToken",authorization);
            httpGet1.addHeader("Authorization","Basic " + authorization);

            CloseableHttpResponse response1 = getInstance().createClient().execute(httpGet1);
            int statusCode1 = response1.getStatusLine().getStatusCode();
            if(statusCode1 == 200) {
                HttpEntity entity1 = response1.getEntity();
                if(entity1 != null) {
                    result = EntityUtils.toString(entity1, "utf-8");
                    EntityUtils.consume(entity1);
                }
            } else {
                httpGet1.abort();
            }

            response1.close();
        }catch (SocketTimeoutException ex){
            LOG.error("url  is  "+url+"; api authorization is "+authorization+";  param is "+JSON.toString(params)+"; error is "+ex.getMessage());
            JSONObject error = new JSONObject();
            error.put("error_code","10000001");
            error.put("message","XSYAPI SocketTimeout");
            result = error.toJSONString();


        }catch (org.apache.http.conn.ConnectTimeoutException ex){
            LOG.error("url  is  "+url+"; api authorization is "+authorization+";  param is "+JSON.toString(params)+"; error is "+ex.getMessage());
            JSONObject error = new JSONObject();
            error.put("error_code","10000002");
            error.put("message","XSYAPI ConnectTimeout");
            result = error.toJSONString();
        }
        showLogInfoString( url, authorization,  params , result );
        return result;
    }
    public static String basicAuthorizationPostString(String authorization,String url, Map<String, String> params) throws IOException {
        String result = null;
        try{
            UrlEncodedFormEntity urlEncodedFormEntity = null;
            if(params != null && !params.isEmpty()) {
                ArrayList httpPost = new ArrayList(params.size());
                Iterator response = params.entrySet().iterator();

                while(response.hasNext()) {
                    Map.Entry statusCode = (Map.Entry)response.next();
                    String entity = (String)statusCode.getValue();
                    if(entity != null) {
                        httpPost.add(new BasicNameValuePair((String)statusCode.getKey(), entity));
                    }
                }

                urlEncodedFormEntity = new UrlEncodedFormEntity(httpPost, "utf-8");
            }

             HttpPost httpPost1 = new HttpPost(url);
//        httpPost1.addHeader("BasicAccessToken",authorization);
            httpPost1.addHeader("Authorization","Basic " + authorization);
//        httpPost1.addHeader("Content-type","x-www-form-urlencoded");
            if(urlEncodedFormEntity != null) {
                httpPost1.setEntity(urlEncodedFormEntity);
            }

            CloseableHttpResponse response1 = getInstance().createClient().execute(httpPost1);
            int statusCode1 = response1.getStatusLine().getStatusCode();
            if(statusCode1 == 200) {
                HttpEntity entity1 = response1.getEntity();
                if(entity1 != null) {
                    result = EntityUtils.toString(entity1, "utf-8");
                    EntityUtils.consume(entity1);
                }
            } else {
                httpPost1.abort();
            }

            response1.close();
        }catch (SocketTimeoutException ex){
            LOG.error("url  is  "+url+"; api authorization is "+authorization+";  param is "+JSON.toString(params)+"; error is "+ex.getMessage());
            JSONObject error = new JSONObject();
            error.put("error_code","10000001");
            error.put("message","XSYAPI SocketTimeout");
            result = error.toJSONString();


        }catch (org.apache.http.conn.ConnectTimeoutException ex){
            LOG.error("url  is  "+url+"; api authorization is "+authorization+";  param is "+JSON.toString(params)+"; error is "+ex.getMessage());
            JSONObject error = new JSONObject();
            error.put("error_code","10000002");
            error.put("message","XSYAPI ConnectTimeout");
            result = error.toJSONString();
        }
        showLogInfoString( url, authorization,  params , result );
        return result;
    }
    public static String basicAuthorizationPostJSON(String authorization,String url, Map<String, Object> params) throws IOException {
        String result = null;
        try{
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            //httpPost.addHeader("Access-Token", publicKey);
            httpPost.addHeader("Authorization","Basic " + authorization);

            StringEntity se = new StringEntity(JSON.toString(params),"utf-8");

            se.setContentType(MediaType.APPLICATION_JSON);
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON));
            //se.getContentLength();
            httpPost.setEntity(se);

            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;
            try {
                //client = HttpClientUtil.getInstance().createClient();
                client = getInstance().createClient();
                response = client.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        result = EntityUtils.toString(entity, CHARSET);
                        EntityUtils.consume(entity);
                    }
                } else {
                    httpPost.abort();
                }
                httpPost.abort();
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if(client != null){
                    client.close();
                }
                if(response != null){
                    response.close();
                }
            }
        }catch (SocketTimeoutException ex){
            LOG.error("url  is  "+url+"; api authorization is "+authorization+";  param is "+JSON.toString(params)+"; error is "+ex.getMessage());
            JSONObject error = new JSONObject();
            error.put("error_code","10000001");
            error.put("message","XSYAPI SocketTimeout");
            result = error.toJSONString();
        }catch (org.apache.http.conn.ConnectTimeoutException ex){
            LOG.error("url  is  "+url+"; api authorization is "+authorization+";  param is "+JSON.toString(params)+"; error is "+ex.getMessage());
            JSONObject error = new JSONObject();
            error.put("error_code","10000002");
            error.put("message","XSYAPI ConnectTimeout");
            result = error.toJSONString();
        }
        showLogInfo( url, authorization,  params , result );
        return result;
    }

    private static void showLogInfo(String url,String authorization,  Map<String, Object> params ,String result ){
        if(StringUtils.isBlank(result)){
            LOG.error("url  is  "+url+"; api authorization is "+authorization+";  param is "+JSON.toString(params)+";result is null");
        }else {
            JSONObject json =  com.alibaba.fastjson.JSON.parseObject(result);
            if(json.containsKey("error_code") &&  0 != json.getIntValue("error_code") ){
                LOG.error("url  is  "+url+"; api authorization is "+authorization+";  param is "+JSON.toString(params)+";result is "+json.toJSONString());
            }else {
                LOG.info("url  is  "+url+"; api authorization is "+authorization+";  param is "+JSON.toString(params)+";result is "+result);
            }
        }
    }
    private static void showLogInfoString(String url,String authorization,  Map<String, String > params ,String result ){
        if(StringUtils.isBlank(result)){
            LOG.error("url  is  "+url+"; api authorization is "+authorization+";  param is "+JSON.toString(params)+";result is null");
        }else {
            JSONObject json =  com.alibaba.fastjson.JSON.parseObject(result);
            if(json.containsKey("error_code") &&  0 != json.getIntValue("error_code") ){
                LOG.error("url  is  "+url+"; api authorization is "+authorization+";  param is "+JSON.toString(params)+";result is "+json.toJSONString());
            }else {
                LOG.info("url  is  "+url+"; api authorization is "+authorization+";  param is "+JSON.toString(params)+";result is "+result);
            }
        }
    }
}
