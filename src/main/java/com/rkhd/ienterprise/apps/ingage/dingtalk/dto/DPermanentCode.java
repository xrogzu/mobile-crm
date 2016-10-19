package com.rkhd.ienterprise.apps.ingage.dingtalk.dto;

import java.io.Serializable;

/**
 * 企业永久授权码信息.
 */
public class DPermanentCode implements Serializable {
    /**
     * 授权方企业名称
     */
    private String permanent_code;
    /**
     * 授权方企业id
     */
    private String  corpid;
    /**
     * corp_name
     */
    private String  corp_name;
}
