package com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXJsonResponse;
import lombok.Getter;

@Getter
public class GetPreAuthCodeResponse extends WXJsonResponse {

    @JsonProperty("pre_auth_code")
    private String preAuthCode;

    @JsonProperty("expires_in")
    private long expiresIn;
}
