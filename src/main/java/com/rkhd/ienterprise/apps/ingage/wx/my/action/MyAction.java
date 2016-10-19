package com.rkhd.ienterprise.apps.ingage.wx.my.action;

import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

@Namespace("/wx/my")
public class MyAction extends WxBaseAction {

    @Action(value = "index", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/my/my_index.jsp")})
    public String list() throws Exception{
        return  SUCCESS;
    }
}
