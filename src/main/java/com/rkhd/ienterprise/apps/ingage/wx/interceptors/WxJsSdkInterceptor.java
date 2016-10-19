package com.rkhd.ienterprise.apps.ingage.wx.interceptors;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by dell on 2016/5/3.
 */
public class WxJsSdkInterceptor extends MethodFilterInterceptor {
    @Override
    protected String doIntercept(ActionInvocation invoker) throws Exception {
        ActionContext ctx = invoker.getInvocationContext();
        HttpServletRequest request = (HttpServletRequest)ctx.get(ServletActionContext.HTTP_REQUEST);
        String action_url =  request.getRequestURI();
        if(request.getQueryString()!=null) //判断请求参数是否为空
        {
            action_url +="?"+request.getQueryString();   // 参数
        }
        request.setAttribute("action_url",action_url);
        return invoker.invoke();
    }
}
