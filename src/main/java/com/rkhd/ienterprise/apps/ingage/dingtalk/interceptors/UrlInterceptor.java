package com.rkhd.ienterprise.apps.ingage.dingtalk.interceptors;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
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

    protected String doIntercept(ActionInvocation invocation) throws Exception {
        ActionContext actionContext = invocation.getInvocationContext();
        HttpServletRequest request= (HttpServletRequest) actionContext.get(StrutsStatics.HTTP_REQUEST);

        String requestUrl = request.getRequestURL().toString();

        requestUrl = requestUrl.replace("http://","https://");
        request.setAttribute("requestUrl",requestUrl);

        String result= invocation.invoke();
        return result;
    }
}
