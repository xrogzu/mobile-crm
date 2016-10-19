package com.rkhd.ienterprise.apps.ingage.wx.exceptions;


public class BusinessException extends  RuntimeException {

    public static final String WX_ATUH_USERID_EMPTY = "000001";


    private static final long serialVersionUID = -2705437429071304576L;

    private String errorCode;

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
