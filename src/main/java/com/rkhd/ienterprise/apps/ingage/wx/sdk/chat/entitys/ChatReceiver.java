package com.rkhd.ienterprise.apps.ingage.wx.sdk.chat.entitys;

import java.io.Serializable;

public class ChatReceiver implements Serializable {
    private static final long serialVersionUID = 2739395302039303726L;

    private String type;

    private String id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
