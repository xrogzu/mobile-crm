package com.rkhd.ienterprise.apps.ingage.wx.xunfei.action;

import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

/**
 * 科大讯飞
 */
@Namespace("/wx/xunfei")
public class XunfeiAction  extends WxBaseAction {

    @Action(value = "sign", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String xunfei_sign() {

            String secret_key = SystemGlobals.getPreference("xun.fei.secret_key");
            String appid = getRequest().getParameter("appid");
            String timestamp = getRequest().getParameter("timestamp");
            String expires = getRequest().getParameter("expires");

            String contact_string = appid + '&' + timestamp + '&' + expires + '&' + secret_key;

            String  md5_str = DigestUtils.md5Hex(contact_string.getBytes());
            EntityReturnData ret  =  new EntityReturnData() ;
            ret.setSuccess(true);
            ret.setEntity(md5_str);
            getRequest().setAttribute("jsondata",ret);
           return SUCCESS;


    }



}
