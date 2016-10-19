package com.rkhd.ienterprise.apps.ingage.utils;

/**
 * Created by dell on 2016/8/12.
 */

public enum ErrorCode {
    OK("0000000","成功"),
    DUPLICATE("0000001","重复"),
    EMOJICHAR("0000002","包含表情字符"),
    NOAUTHORIZED("0000003","未授权登录"),
    NOOPERATIONPERMISSIONS("0000004","没有操作权限"),
    XSY_API_ERROR("0000005","请求api出错"),
    NO_VIEW_AUTHORITY("0000006","没有查看权限"),
    OTHER_ERROR("9999999","其他错误");
    private String errorCode;

    private String errorMsg;
    private ErrorCode(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
