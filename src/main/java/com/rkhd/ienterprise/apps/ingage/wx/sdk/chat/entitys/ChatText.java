package com.rkhd.ienterprise.apps.ingage.wx.sdk.chat.entitys;

import java.io.Serializable;


public class ChatText  implements Serializable {
    private static final long serialVersionUID = -6180148044565950324L;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
