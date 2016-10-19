package com.rkhd.ienterprise.apps.ingage.wx.sdk.base;



import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class WXRequest<T  > {

    protected WXHTTPMethod method = WXHTTPMethod.GET;

    protected Map<String, String> parameters;
    protected String path;

    protected String body;
    protected String baseUrl;

    protected String filePath;

    public WXRequest() {
        this(WXBaseUrl.COMMON);
        this.parameters = new HashMap<String, String>();
    }

    public WXRequest(String baseUrl) {
        this.baseUrl = baseUrl;
        this.parameters = new HashMap<String, String>();
    }

    public String getUrl() {
        return baseUrl + path;
    }

    public void addParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    public abstract T createResponse(String body) throws WXException;


    public WXHTTPMethod getMethod() {
        return method;
    }

    public void setMethod(WXHTTPMethod method) {
        this.method = method;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
