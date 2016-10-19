package com.rkhd.ienterprise.apps.ingage.wx.sdk.chat.entitys;


public class TextSingleChat  extends  BaseSingleChat{
    private static final long serialVersionUID = 7407170527126512400L;

    private ChatText text;

    private String msgtype = "text";

    public ChatText getText() {
        return text;
    }

    public void setText(ChatText text) {
        this.text = text;
    }

    public String getMsgtype() {
        return msgtype;
    }
}
