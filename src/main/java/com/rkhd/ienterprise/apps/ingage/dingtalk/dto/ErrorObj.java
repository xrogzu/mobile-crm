package com.rkhd.ienterprise.apps.ingage.dingtalk.dto;

import java.io.Serializable;

/**
 * Created by dell on 2016/2/23.
 */
public class ErrorObj implements Serializable {
    private static final long serialVersionUID = -3242731982979970191L;
    private String status;//异常编码
    private String key;//异常字段
    private String msg;//异常提示消息

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
