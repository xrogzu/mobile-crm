package com.rkhd.ienterprise.apps.ingage.dingtalk.helper.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.action.IsvReceiveAction;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import org.apache.commons.httpclient.auth.CredentialsProvider;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public class HttpHelper {
    private  static Logger LOG = LoggerFactory.getLogger(HttpHelper.class);


    private static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 3000;
    private static final String CHARSET = "utf-8";

    private static final HttpHelper INSTANCE = new HttpHelper();
    private static SSLConnectionSocketFactory sslConnectionSocketFactory;
    private static RequestConfig config;

    public static HttpHelper getInstance() {
        return INSTANCE;
    }

    private HttpHelper() {
        initTrustHosts();
        initConfig();
    }

    private void initConfig() {
        config = RequestConfig.custom().setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT).setSocketTimeout(DEFAULT_SOCKET_TIMEOUT).build();
    }

    private void initTrustHosts() {
        try {


//            RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();

            //SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();

            //SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,new String[] { "TLSv1" },null,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,new String[] { "TLSv1" },null,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public CloseableHttpClient createClient() {
        return HttpClients.custom().setDefaultRequestConfig(config).setSSLSocketFactory(sslConnectionSocketFactory).build();
    }



	public static JSONObject httpGet(String url) throws OApiException {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
//        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = HttpHelper.getInstance().createClient();
        RequestConfig requestConfig = RequestConfig.custom().
        		setSocketTimeout(2000).setConnectTimeout(2000).build();
        httpGet.setConfig(requestConfig);


        try {
            response = httpClient.execute(httpGet, new BasicHttpContext());

            if (response.getStatusLine().getStatusCode() != 200) {

                LOG.info("request url failed, http code=" + response.getStatusLine().getStatusCode()
                                   + ", url=" + url);
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String resultStr = EntityUtils.toString(entity, "utf-8");

                JSONObject result = JSON.parseObject(resultStr);
                if (result.getInteger("errcode") == 0) {
//                	result.remove("errcode");
//                	result.remove("errmsg");
                    return result;
                } else {
                    LOG.info("request url=" + url + ",return value=");
                    LOG.info(resultStr);
                    int errCode = result.getInteger("errcode");
                    String errMsg = result.getString("errmsg");
                    throw new OApiException(errCode, errMsg);
                }
            }
        } catch (IOException e) {
            LOG.info("request url=" + url + ", exception, msg=" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (response != null) try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
	
	
	public static JSONObject httpPost(String url, Object data) throws OApiException {
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
       // CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = HttpHelper.getInstance().createClient();
        RequestConfig requestConfig = RequestConfig.custom().
        		setSocketTimeout(5000).setConnectTimeout(5000).build();
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type", "application/json");

        try {
        	StringEntity requestEntity = new StringEntity(JSON.toJSONString(data), "utf-8");
            httpPost.setEntity(requestEntity);
            
            response = httpClient.execute(httpPost, new BasicHttpContext());

            if (response.getStatusLine().getStatusCode() != 200) {

                LOG.info("request url failed, http code=" + response.getStatusLine().getStatusCode()
                                   + ", url=" + url);
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String resultStr = EntityUtils.toString(entity, "utf-8");

                JSONObject result = JSON.parseObject(resultStr);
                if (result.getInteger("errcode") == 0) {
                	result.remove("errcode");
                	result.remove("errmsg");
                    return result;
                } else {
                    LOG.info("request url=" + url + ",return value="+resultStr);
                    int errCode = result.getInteger("errcode");
                    String errMsg = result.getString("errmsg");
                    throw new OApiException(errCode, errMsg);
                }
            }
        } catch (IOException e) {
            LOG.error("request url=" + url + ", exception, msg=" + e.getMessage() +" ;data="+JSON.toJSONString(data));
            e.printStackTrace();
        } finally {
            if (response != null) try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
	
	
	public static JSONObject uploadMedia(String url, File file) throws OApiException {
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
       // CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = HttpHelper.getInstance().createClient();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
        httpPost.setConfig(requestConfig);

        HttpEntity requestEntity = MultipartEntityBuilder.create().addPart("media",
        		new FileBody(file, ContentType.APPLICATION_OCTET_STREAM, file.getName())).build();
        httpPost.setEntity(requestEntity);

        try {
            response = httpClient.execute(httpPost, new BasicHttpContext());

            if (response.getStatusLine().getStatusCode() != 200) {

                LOG.info("request url failed, http code=" + response.getStatusLine().getStatusCode()
                                   + ", url=" + url);
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String resultStr = EntityUtils.toString(entity, "utf-8");

                JSONObject result = JSON.parseObject(resultStr);
                if (result.getInteger("errcode") == 0) {
                    // 成功
                	result.remove("errcode");
                	result.remove("errmsg");
                    return result;
                } else {
                    LOG.info("request url=" + url + ",return value=");
                    LOG.info(resultStr);
                    int errCode = result.getInteger("errcode");
                    String errMsg = result.getString("errmsg");
                    throw new OApiException(errCode, errMsg);
                }
            }
        } catch (IOException e) {
            LOG.info("request url=" + url + ", exception, msg=" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (response != null) try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
	
	
	public static JSONObject downloadMedia(String url, String fileDir) throws OApiException {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
       //CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = HttpHelper.getInstance().createClient();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
        httpGet.setConfig(requestConfig);

        try {
            HttpContext localContext = new BasicHttpContext();

            response = httpClient.execute(httpGet, localContext);

            RedirectLocations locations = (RedirectLocations) localContext.getAttribute(HttpClientContext.REDIRECT_LOCATIONS);
            if (locations != null) {
                URI downloadUrl = locations.getAll().get(0);
                String filename = downloadUrl.toURL().getFile();
                LOG.info("downloadUrl=" + downloadUrl);
                File downloadFile = new File(fileDir + File.separator + filename);
                FileUtils.writeByteArrayToFile(downloadFile, EntityUtils.toByteArray(response.getEntity()));
                JSONObject obj = new JSONObject();
                obj.put("downloadFilePath", downloadFile.getAbsolutePath());
                obj.put("httpcode", response.getStatusLine().getStatusCode());
                
               
                
                return obj;
            } else {
                if (response.getStatusLine().getStatusCode() != 200) {

                    LOG.info("request url failed, http code=" + response.getStatusLine().getStatusCode()
                                       + ", url=" + url);
                    return null;
                }
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String resultStr = EntityUtils.toString(entity, "utf-8");

                    JSONObject result = JSON.parseObject(resultStr);
                    if (result.getInteger("errcode") == 0) {
                        // 成功
                    	result.remove("errcode");
                    	result.remove("errmsg");
                        return result;
                    } else {
                        LOG.info("request url=" + url + ",return value=");
                        LOG.info(resultStr);
                        int errCode = result.getInteger("errcode");
                        String errMsg = result.getString("errmsg");
                        throw new OApiException(errCode, errMsg);
                    }
                }
            }
        } catch (IOException e) {
            LOG.info("request url=" + url + ", exception, msg=" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (response != null) try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
