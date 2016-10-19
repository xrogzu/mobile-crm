package com.rkhd.ienterprise.apps.ingage.wx.sdk.security.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXJsonResponse;
import lombok.Data;

import java.util.List;

/**
 * {
 * "ip_list":["127.0.0.1","127.0.0.1"]
 * }
 */
@Data
public class WXServerIpListResponse extends WXJsonResponse {
    @JsonProperty("ip_list")
    private List<String> ipList;

    public List<String> getIpList() {
        return ipList;
    }

    public void setIpList(List<String> ipList) {
        this.ipList = ipList;
    }
}
