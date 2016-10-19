package com.rkhd.ienterprise.apps.ingage.wx.interceptors;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsStatics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by dell on 2016/3/4.
 */
public class UrlInterceptor  extends MethodFilterInterceptor {

    private static final long serialVersionUID = 7407675393063848474L;

    private static final Logger LOG = LoggerFactory.getLogger(UrlInterceptor.class);

    private static boolean loadOnce = false;

    private static String hostUrl = null;

    protected String doIntercept(ActionInvocation invocation) throws Exception {
        ActionContext actionContext = invocation.getInvocationContext();
        HttpServletRequest request= (HttpServletRequest) actionContext.get(StrutsStatics.HTTP_REQUEST);

        String requestUrl = request.getRequestURL().toString();

        requestUrl = requestUrl.replace("http://","https://");

        request.setAttribute("requestUrl",requestUrl);


        if(!loadOnce){
            //初始化数据时，线程处于锁定状态
            synchronized (UrlInterceptor.class){
                if(!loadOnce){
                    String serverName = request.getServerName();

                    hostUrl = "https://"+serverName;

                    String profile =SystemGlobals.getPreference(serverName);
                    if(StringUtils.isBlank(profile)){
                        LOG.error("serverName is '"+serverName+"',but we not find it`s profile");
                    }else {
                        LOG.info("serverName is '"+serverName+"', we  find it`s profile is "+profile);
                        Env.SUITE_KEY =  SystemGlobals.getPreference(profile+".SUITE_KEY");
                        Env.SUITE_SECRET =  SystemGlobals.getPreference(profile+".SUITE_SECRET");
                        Env.TOKEN =  SystemGlobals.getPreference(profile+".TOKEN");
                        Env.ENCODING_AES_KEY =  SystemGlobals.getPreference(profile+".ENCODING_AES_KEY");

                        WXAppConstant.APP_ID =  SystemGlobals.getPreference(profile+".wx.APP_ID");
                        WXAppConstant.APP_SECRET =  SystemGlobals.getPreference(profile+".wx.APP_SECRET");
                        WXAppConstant.TOKEN =  SystemGlobals.getPreference(profile+".wx.TOKEN");
                        WXAppConstant.EncodingAESKey = SystemGlobals.getPreference(profile+".wx.EncodingAESKey");
                        WXAppConstant.CorpID = SystemGlobals.getPreference(profile+".wx.corpid");
                        WXAppConstant.providersecret = SystemGlobals.getPreference(profile+".wx.providersecret");

                        if(
                                StringUtils.isEmpty(WXAppConstant.APP_ID)  ||
                                        StringUtils.isEmpty(WXAppConstant.APP_SECRET)  ||
                                        StringUtils.isEmpty(WXAppConstant.TOKEN)  ||
                                        StringUtils.isEmpty(WXAppConstant.EncodingAESKey)
                                ){
                            LOG.info("WXAppConstant.APP_ID="+WXAppConstant.APP_ID);
                            LOG.info("WXAppConstant.APP_SECRET="+WXAppConstant.APP_SECRET);
                            LOG.info("WXAppConstant.TOKEN="+WXAppConstant.TOKEN);
                            LOG.info("WXAppConstant.EncodingAESKey="+WXAppConstant.EncodingAESKey);
                            throw new RuntimeException("参数为空");

                        }else{
                            LOG.info("WXAppConstant.APP_ID="+WXAppConstant.APP_ID);
                            LOG.info("WXAppConstant.APP_SECRET="+WXAppConstant.APP_SECRET);
                            LOG.info("WXAppConstant.TOKEN="+WXAppConstant.TOKEN);
                            LOG.info("WXAppConstant.EncodingAESKey="+WXAppConstant.EncodingAESKey);
                        }
                    }
                    //配置OpenAPI url
                    String apiProfile =SystemGlobals.getPreference(serverName+".api");
                    if(StringUtils.isBlank(apiProfile)){
                        LOG.error("serverName is '"+serverName+"',but we not find it`s apiProfile");
                    }else{
                        String crmapi_host_url_key  = "crmapi.host.url";
                        String crmapi_host = SystemGlobals.getPreference(apiProfile+"."+crmapi_host_url_key);
                        if(StringUtils.isBlank(crmapi_host)){
                            LOG.error("serverName is '"+serverName+"',but we not find it`s crmapi_host");
                        }else{
                            SystemGlobals.setPreference(crmapi_host_url_key,crmapi_host);
                            LOG.info("serverName is '"+serverName+"',and we  find it`s xsy`open api url is "+crmapi_host);
                        }
                    }

                    loadOnce = true;
                }

            }

        }
        String result= invocation.invoke();
        return result;
    }
    public static String getHostUrl()
    {
        return hostUrl;
    }
}
