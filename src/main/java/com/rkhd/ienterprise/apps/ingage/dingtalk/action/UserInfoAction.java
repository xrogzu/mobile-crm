package com.rkhd.ienterprise.apps.ingage.dingtalk.action;


import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DingSessionUser;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.XsyUserDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.enums.Sessionkes;
import com.rkhd.ienterprise.apps.ingage.services.DingService;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SessionUtils;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Namespace("/wx/user")
public class UserInfoAction extends WxBaseAction {

    private static final Logger LOG = LoggerFactory.getLogger(UserInfoAction.class);

    @Autowired
    private RelThirdTokenService relThirdTokenService;
    @Autowired
    private DingService dDingService;

    private String corpid;

    @Action(value = "list", results = {@Result(name = "success", location = PAGES_ROOT+"/dingtalk/user_list.jsp")})
    public String index() throws Exception{
        return  SUCCESS;
    }

    @Action(value = "getuser",results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getuser() throws Exception{
        EntityReturnData ret = new EntityReturnData();
        XsyUserDto xsyUser = null;
        try{
            if(SessionUtils.needReLogin(getRequest())){
               // LOG.info("no login");
                ret.setMsg("用户未登陆");
            }else {
                DingSessionUser dingSessionUser = (DingSessionUser) getRequest().getSession().getAttribute(Sessionkes.SESSIONUSER.toString());
                xsyUser = dingSessionUser.getXsy_user();
                ret.setEntity(xsyUser);
            }
            ret.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }

    public HttpServletRequest getRequest() {
        return ServletActionContext.getRequest();
    }

    protected HttpServletResponse getResponse() {
        return ServletActionContext.getResponse();
    }

    protected ServletContext getServletContext() {
        return ServletActionContext.getServletContext();
    }

    public String getCorpid() {
        return corpid;
    }

    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }
}
