package com.rkhd.ienterprise.apps.ingage.dingtalk.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

/**
 * Created by dell on 2016/3/1.
 */
@Namespace("/dingtalk/authorized")
public class AuthorizedAction extends BaseAction{

    @Action(value = "no", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/authorized_no.jsp")})
    public String noAuthorized() throws Exception{
        return  SUCCESS;
    }
}
