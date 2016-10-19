package com.rkhd.ienterprise.apps.ingage.wx.admin.action;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SetSystemProp;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.isv.action.WxAuthAction;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.WXApi;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxRuntime;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Namespace("/wx/admin")
public class AdminAction extends WxBaseAction {
    private static final Logger LOG = LoggerFactory.getLogger(AdminAction.class);

    private WXApi wxApi =  WXApi.getWxApi();

    /**
     * 参考
     * http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E5%90%8E%E5%8F%B0%E5%8D%95%E7%82%B9%E7%99%BB%E5%BD%95
     * @return
     * @throws Exception
     */
    @Action(value = "index", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/admin/admin_inde.jsp")})
    public String admin_inde() throws Exception{

       // https://www.AAA.com?auth_code=xxx&expires_in=600
        String corpid = getRequest().getParameter("corpid");
        String auth_code = getRequest().getParameter("auth_code");
        String expires_in = getRequest().getParameter("expires_in");

//        String code = getRequest().getParameter("code");
        String  permanent_code =  getPermanent_code(corpid);
        String suitaccess_token = WxRuntime.getSuite_access_token();
        JSONObject corpToken = wxApi.get_corp_token(suitaccess_token, WXAppConstant.APP_ID,corpid,permanent_code);
        String access_token = corpToken.getString("access_token");
        JSONObject jsonObject = wxApi.getLoginInfo(access_token,auth_code);
        LOG.info("登录用户信息为："+jsonObject.toJSONString());


        return  SUCCESS;
    }

}
