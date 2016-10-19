package com.rkhd.ienterprise.apps.ingage.dingtalk.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

/**
 * Created by dell on 2016/1/16.
 */
@Namespace("/dingtalk/common")
public class CommonAction  extends BaseAction{

    /**
     * 公用的下拉选页面
     * @return
     * @throws Exception
     */
    @Action(value = "listpage", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/common/list_page.jsp")})
    public String select() throws Exception{
        return  SUCCESS;
    }

    @Action(value = "isdel", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/common/isdel.jsp")})
    public String isdel() throws Exception{
        return  SUCCESS;
    }
}
