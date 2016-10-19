package com.rkhd.ienterprise.apps.ingage.dingtalk.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import com.rkhd.ienterprise.permission.interceptor.IdentityInterceptor;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.TokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * Created by dell on 2016/3/29.
 */
public class CsrfInterceptor  implements Interceptor {
    private static final Logger LOG = LoggerFactory.getLogger(CsrfInterceptor.class);


    public void destroy() {

    }


    public void init() {

    }


    public String intercept(ActionInvocation invocation) throws Exception {

        HttpServletRequest  request = ServletActionContext.getRequest();
//        HttpServletResponse response = ServletActionContext.getResponse();
        String referer = request.getHeader("referer");// (String) request.getAttribute("requestUrl");
        try{
            String csrf_allow_host = SystemGlobals.getPreference("csrf.allow.host");
            String[] csrf_allow_hostArray = csrf_allow_host.split(",");
            boolean pass = false;
            if(referer != null ){
                for(int i=0;i<csrf_allow_hostArray.length;i++){
                    if(referer.startsWith(csrf_allow_hostArray[i])){
                        pass = true;
                        break;
                    }
                }
            }else {
                pass = true;
            }

            if(!pass){
                LOG.error("csrf interceptored,remoteAddr is:"+request.getRemoteAddr()+";referer="+referer);
                return "no_auth";
            }
     //执行调用
            String result =  invocation.invoke();
            return result;
        }catch (Exception e){
            LOG.error("referer={}",referer);
            e.printStackTrace();
            throw e;
        }


    }
}
