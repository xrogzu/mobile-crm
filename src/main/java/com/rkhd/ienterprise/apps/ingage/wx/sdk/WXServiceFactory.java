package com.rkhd.ienterprise.apps.ingage.wx.sdk;

import com.rkhd.ienterprise.apps.ingage.wx.sdk.chat.WxChatService;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.department.DepartmentService;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.isv.logininfo.GetLoginInfoService;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.message.WxMessageService;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.QyWxhApiService;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.security.WXSecurityService;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.user.UserService;

public class WXServiceFactory {
    private  static GetLoginInfoService getLoginInfoService;

    public  GetLoginInfoService getGetLoginInfoService() {
        if(getLoginInfoService == null){
            synchronized (WXServiceFactory.class){
                if(getLoginInfoService == null){
                    getLoginInfoService = new GetLoginInfoService();
                }
            }
        }

        return getLoginInfoService;
    }

    private static  WXSecurityService wxSecurityService = null;

    public  WXSecurityService getWxSecurityService() {
        if (wxSecurityService == null) {
            synchronized (WXServiceFactory.class){
                if(wxSecurityService == null){
                    wxSecurityService = new WXSecurityService();
                }
            }

        }
        return wxSecurityService;
    }

    private static  QyWxhApiService qyWxhApiService = null;

    public   QyWxhApiService getQyWxhApiService() {
        if (qyWxhApiService == null) {
            synchronized (WXServiceFactory.class){
                if (qyWxhApiService == null) {
                    qyWxhApiService = new QyWxhApiService();
                }
            }
        }

        return qyWxhApiService;
    }

    private static DepartmentService departmentService  = null;

    public static DepartmentService getDepartmentService() {
        if (departmentService == null) {
            synchronized (WXServiceFactory.class){
                if (departmentService == null) {
                    departmentService = new DepartmentService();
                }
            }
        }


        return departmentService;
    }

    private static UserService userService;

    public static UserService getUserService() {

        if (userService == null) {
            synchronized (WXServiceFactory.class){
                if (userService == null) {
                    userService = new UserService();
                }
            }
        }
        return userService;
    }

    private static WxMessageService wxMessageService;
    public static WxMessageService getWxMessageService() {

        if (wxMessageService == null) {
            synchronized (WXServiceFactory.class){
                if (wxMessageService == null) {
                    wxMessageService = new WxMessageService();
                }
            }
        }
        return wxMessageService;
    }

    private static WxChatService wxChatService;
    public static WxChatService getWxChatService() {
        if (wxChatService == null) {
            synchronized (WXServiceFactory.class){
                if (wxChatService == null) {
                    wxChatService = new WxChatService();
                }
            }
        }
        return wxChatService;
    }
}
