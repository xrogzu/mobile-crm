package com.rkhd.ienterprise.apps.ingage.wx.sdk.message.entitys;




import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class TextWxMessage extends  AbstractWxMessage  implements Serializable {
    private static final long serialVersionUID = -7608650562549933244L;

    private String msgtype = "text";

    /**
     * 必填
     * 消息内容，最长不超过2048个字节，注意：主页型应用推送的文本消息在微信端最多只显示20个字（包含中英文）
     */
    private JSONObject text = null;

    public JSONObject getText() {
        return text;
    }

    public void setText(JSONObject text) {
        this.text = text;
    }

    @Override
    public String getMsgtype() {
        return msgtype;
    }
}
