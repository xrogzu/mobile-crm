package com.rkhd.ienterprise.apps.ingage.wx.sdk.chat.entitys;

import java.io.Serializable;

/**
 * Created by dell on 2016/7/1.
 */
public class BaseSingleChat implements Serializable {


    private static final long serialVersionUID = -1209882174970883707L;

    private ChatReceiver receiver;

    private String sender;

    public ChatReceiver getReceiver() {
        return receiver;
    }

    public void setReceiver(ChatReceiver receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
