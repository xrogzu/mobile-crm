package com.rkhd.ienterprise.apps.ingage.wx.utils;

/**
 * Created by dell on 2016/8/24.
 */
public enum WxInfoType {
    SUITE_TICKET("推送suite_ticket协议"),CHANGE_AUTH("变更授权"),CANCEL_AUTH("取消授权"),CREATE_AUTH("授权成功"),CONTACT_SYNC("联系人同步"),PC_SYNC("PC端发起的同步请求");
    private String desc;
    private WxInfoType(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static void main(String[] args) {
        System.out.println(WxInfoType.SUITE_TICKET.name());
    }
}
