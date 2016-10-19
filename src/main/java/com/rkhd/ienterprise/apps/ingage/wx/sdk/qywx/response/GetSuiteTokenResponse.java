package com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXJsonResponse;
import lombok.Getter;

@Getter
public class GetSuiteTokenResponse extends WXJsonResponse {

    @JsonProperty("suite_access_token")
    private String suiteAccessToken;

    @JsonProperty("expires_in")
    private long expiresIn;
}
