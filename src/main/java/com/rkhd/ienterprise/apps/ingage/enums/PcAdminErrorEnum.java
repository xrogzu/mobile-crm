package com.rkhd.ienterprise.apps.ingage.enums;

/**
 * Created by dell on 2016/6/1.
 */
public enum PcAdminErrorEnum {

    NO_PROVIDER_ACCESS_TOKEN("000001","没有微信provider_access_token"),
    NO_AUTH_CODE("000002","没有微信AUTH_CODE"),
    NO_GET_LOGIN_INFO("000003","没有获得微信登录用户信息"),

    NO_SYN_INFO("000004","没有同步信息需要重新授权"),
    SYN_FAILED("000005","信息同步失败需要重新授权"),
    SYN_ING("000006","信息同步中"),
    NO_PERMANENT_CODE("000007","没有获得企业永久授权码"),
    NO_USER_RELATION("000008","没有获得用户映射关系"),

    CONTACT_US("000009","请联系管理员");//系统错误
    private String errorCode;
    private String errorMsg;

    private PcAdminErrorEnum(String errorCode,String errorMsg){
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

    public static void main(String[] args){
        for(PcAdminErrorEnum item :PcAdminErrorEnum.values()){
            System.out.println(item.name()+" : "+item.getErrorCode()+":"+item.getErrorMsg());
        }
    }
}
