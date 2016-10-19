package com.rkhd.ienterprise.apps.ingage.wx.interceptors;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DingSessionUser;
import com.rkhd.ienterprise.apps.ingage.dingtalk.enums.Sessionkes;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.dtos.SessionUser;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class WxAuthInterceptor extends MethodFilterInterceptor {


    private static final long serialVersionUID = 7175284785643072977L;

    private static final Logger LOG = LoggerFactory.getLogger(WxAuthInterceptor.class);

    /* (non-Javadoc)
         * @see com.opensymphony.xwork2.interceptor.MethodFilterInterceptor#doIntercept(com.opensymphony.xwork2.ActionInvocation)
         */
    @Override
    protected String doIntercept(ActionInvocation invoker) throws Exception {

        ActionContext ctx = invoker.getInvocationContext();
        HttpServletRequest request = (HttpServletRequest)ctx.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse response = (HttpServletResponse)ctx.get(ServletActionContext.HTTP_RESPONSE);
        String uri = request.getServletPath();
        uri = uri.replace(".action","");
        if(uri.indexOf("/wx/")<0){
            //非钉钉的不拦截
            return invoker.invoke();
        }else {
            String permissionUri = SystemGlobals.getPreference("system.no.need.auth.url");
            LOG.info("uri={}",uri);

            boolean permission = false;
            if(permissionUri.indexOf(","+uri+",")>=0){
                permission = true;
            }
            if(!permission){
                SessionUser sessionUser = (SessionUser) request.getSession().getAttribute(WxBaseAction.wxSessionUserKey.toString());
                if(sessionUser == null){
                    LOG.info("uri="+uri);
                    LOG.error("no  authorized url:"+uri);
                    return "no_auth";
                }
            }
            return invoker.invoke();
        }
    }

}