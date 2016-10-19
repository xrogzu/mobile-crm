package com.rkhd.ienterprise.apps.ingage.wx.sdk.base;


import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.utils.WXHttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;


public class WXHttpDispatch {
    private  static Logger LOG = LoggerFactory.getLogger(WXHttpDispatch.class);
    private final static String CharSet = "UTF-8";

    private final static String URL_FORMAT = "%s?%s";

    public static Object execute(WXRequest request) throws WXException {
        Object wxResponse = null;
        switch (request.getMethod()) {
            case GET:
                wxResponse = doGet(request);
                break;
            case POST:
                wxResponse = doPost(request);
                break;
            case UPLOAD:
                wxResponse = doUpload(request);
                break;
            default:
                throw new WXException("undefined request method.");
        }
        if (wxResponse == null) {
            throw new WXException("response is null");
        }
//        if (wxResponse instanceof WXJsonResponse) {
//            WXJsonResponseChecker.check((WXJsonResponse) wxResponse);
//        }
        return wxResponse;
    }

    private static Object doGet(WXRequest request) throws WXException {
        String url = String.format(URL_FORMAT,
                request.getUrl(),
                constructQuery(request.getParameters()));
        return request.createResponse(WXHttpUtil.doGet(url));
    }

    private static Object doPost(WXRequest request) throws WXException {
        String url = String.format(URL_FORMAT,
                request.getUrl(),
                constructQuery(request.getParameters()));
        return request.createResponse(WXHttpUtil.doPost(url, request.getBody()));
    }

    private static Object doUpload(WXRequest request) throws WXException {
        String url = String.format(URL_FORMAT,
                request.getUrl(),
                constructQuery(request.getParameters()));
        return request.createResponse(WXHttpUtil.doUpload(url, request.getFilePath()));
    }

    private static String constructQuery(Map<String, String> parameters) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                if(entry == null || entry.getValue() == null){
                    LOG.error(entry.getKey()+"`value is null");
                    continue;
                }
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue(), CharSet));
                sb.append("&");
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
