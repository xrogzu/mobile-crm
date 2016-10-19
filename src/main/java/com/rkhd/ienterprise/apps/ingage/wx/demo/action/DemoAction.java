package com.rkhd.ienterprise.apps.ingage.wx.demo.action;

import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Namespace("/wx/demo")
public class DemoAction  extends WxBaseAction {

    private static final Logger LOG = LoggerFactory.getLogger(DemoAction.class);

    @Action(value = "demo", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/demo/demo.jsp")})
    public String demo() throws Exception{
        return  SUCCESS;
    }
}
