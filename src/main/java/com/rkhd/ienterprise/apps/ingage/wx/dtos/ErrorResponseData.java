package com.rkhd.ienterprise.apps.ingage.wx.dtos;

import java.io.Serializable;

/**
 * 发生错误时的返回值
 */
public class ErrorResponseData implements Serializable {
    private static final long serialVersionUID = -198971400421570904L;

    private String errorCode;

    private String errorMsg;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
