package com.rkhd.ienterprise.apps.ingage.enums;

import com.rkhd.ienterprise.base.oauth.model.OauthToken;

/**
 * Created by dell on 2016/5/30.
 */
public enum IndexError {
    NO_SYN_INFO("000001","没有获得同步记录"),
    SYN_FAILED("000002","同步失败"),
    SYN_ING("000003","同步中"),
    NO_PERMANENT_CODE("000004","获得永久授权码失败"),
    AUTH_ERROR("000005","授权错误"),
    NO_RELTHIRDCORPXSYTENANT("000006","没有获得企业映射关系"),
    NO_RELTHIRDUSERXSYUSER("000007","没有获得用户映射关系"),
    NO_OAUTHTOKEN("000008","没有获得API TOKEN"),
    NO_GET_XSY_USER("000009","没有获得销售易用户"),
    WEI_XIN_API_ERROR("000010","微信API错误"),
    XSY_SERVICE_ERROR("000011","销售易服务错误"),
    NO_CODE("0000012","获得CODE失败"),
    CONTACT_US("000013","联系我们"),
    NO_LICENESE("000014","没有license"),
    LICENESE_EXPIRE("000015","license过期"),
    LICENESE_NO_GET_AUTHORIZATION("000016","没有获得授权license");




    //系统错误
    private String errorCode;
    private String errorMsg;

    private IndexError(String errorCode,String errorMsg){
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
