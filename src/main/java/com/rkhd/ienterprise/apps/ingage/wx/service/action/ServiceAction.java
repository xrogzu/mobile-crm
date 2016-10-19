package com.rkhd.ienterprise.apps.ingage.wx.service.action;

import com.rkhd.ienterprise.apps.ingage.dingtalk.action.BaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;


@Namespace("/wx/serveice")
public class ServiceAction extends WxBaseAction {

    /**
     * 服务首页
     * @return
     * @throws Exception
     */
    @Action(value = "index", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/serveice/serveice_index.jsp")})
    public String info() throws Exception{
        return  SUCCESS;
    }

    /**
     * 小易秘籍
     * @return
     * @throws Exception
     */
    @Action(value = "miji", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/serveice/serveice_miji.jsp")})
    public String miji() throws Exception{
        return  SUCCESS;
    }

    /**
     * 意见反馈
     * @return
     * @throws Exception
     */
    @Action(value = "yjfk", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/serveice/serveice_yjfk.jsp")})
    public String yjfk() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "about", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/serveice/serveice_about.jsp")})
    public String about() throws Exception{
        return  SUCCESS;
    }
}
