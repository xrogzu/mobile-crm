package com.rkhd.ienterprise.apps.ingage.wx.sdk.exception;

import lombok.Getter;


public class WXException extends Exception {
    @Getter
    private long code;
    @Getter
    private String message;

    public WXException(long code, String message) {
        super(String.format("%d: %s", code, message));
    }

    public WXException(String message) {
        this(-1, message);
    }
}