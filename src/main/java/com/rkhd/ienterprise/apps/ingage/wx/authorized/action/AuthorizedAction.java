package com.rkhd.ienterprise.apps.ingage.wx.authorized.action;

import com.rkhd.ienterprise.apps.ingage.dingtalk.action.BaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

/**
 * Created by dell on 2016/3/1.
 */
@Namespace("/wx/authorized")
public class AuthorizedAction extends WxBaseAction {

    @Action(value = "no", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/authorized_no.jsp")})
    public String noAuthorized() throws Exception{
        return  SUCCESS;
    }
}
