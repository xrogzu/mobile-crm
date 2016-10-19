package com.rkhd.ienterprise.apps.ingage.wx.sdk.token.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXJsonResponse;
import lombok.Getter;
import lombok.Getter;

/**
 * Created by lemon_bar on 15/7/7.
 */
@Getter
public class WXTokenResponse extends WXJsonResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private long expires;

    public WXTokenResponse() {
        super();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }
}
