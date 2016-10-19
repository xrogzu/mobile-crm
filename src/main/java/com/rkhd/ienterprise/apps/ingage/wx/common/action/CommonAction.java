package com.rkhd.ienterprise.apps.ingage.wx.common.action;

import com.rkhd.ienterprise.apps.ingage.dingtalk.action.BaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

/**
 * Created by dell on 2016/1/16.
 */
@Namespace("/wx/common")
public class CommonAction extends WxBaseAction {

    /**
     * 公用的下拉选页面
     * @return
     * @throws Exception
     */
    @Action(value = "listpage", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/common/list_page.jsp")})
    public String select() throws Exception{
        return  SUCCESS;
    }

    @Action(value = "isdel", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/common/isdel.jsp")})
    public String isdel() throws Exception{
        return  SUCCESS;
    }
}
