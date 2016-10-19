package com.rkhd.ienterprise.apps.ingage.dingtalk.util;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.action.AccountAction;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DingSessionUser;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.XsyUserDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.enums.Sessionkes;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.dtos.SessionUser;
import com.rkhd.ienterprise.base.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by dell on 2016/1/26.
 */
public class SessionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SessionUtils.class);

    public static String  getSessionUser(HttpServletRequest request){
        if(needReLogin(request)){
            return null;
        }else {
            DingSessionUser dingSessionUser = (DingSessionUser) request.getSession().getAttribute(Sessionkes.SESSIONUSER.toString());
            XsyUserDto xsy_user =  dingSessionUser.getXsy_user();
            JSONObject json = (JSONObject) JSON.toJSON(xsy_user);
            json.put("normal" ,dingSessionUser.isNormal());
            json.put("manager" ,dingSessionUser.isManager());
            json.put("systemManager" ,dingSessionUser.isSystemManager());

            LOG.info("dingSessionUser={}",json.toJSONString());
            return json.toJSONString();
        }
    }
    public static String  getWxSessionUser(HttpServletRequest request){
        SessionUser sessionUser = (SessionUser) request.getSession().getAttribute(WxBaseAction.wxSessionUserKey);
        if(sessionUser != null) {
            XsyUserDto xsy_user =  sessionUser.getXsy_user();
            JSONObject json = (JSONObject) JSON.toJSON(xsy_user);
            return json.toJSONString();
        }else {
            return null;
        }


    }

    /**
     * 1：更换企业登录，重新登录
     * 2：session中用户信息为空，需要重新登录
     * @param request
     * @return
     */
    public static  boolean needReLogin(HttpServletRequest request){
        DingSessionUser dingSessionUser = (DingSessionUser) request.getSession().getAttribute(Sessionkes.SESSIONUSER.toString());
        if(dingSessionUser == null){
            return true;
        }else {
            String session_thirdcorpid = dingSessionUser.getThirdcorpid();
            String corpId = request.getParameter("corpid");
            if(session_thirdcorpid.equals(corpId) == false){//更换企业登录
                return true;
            }else {
                return false;
            }
        }
    }
}
