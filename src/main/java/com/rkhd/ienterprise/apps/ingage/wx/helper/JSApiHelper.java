package com.rkhd.ienterprise.apps.ingage.wx.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.dtos.SessionUser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dell on 2016/4/25.
 */
public class JSApiHelper {
    private static final Logger LOG = LoggerFactory.getLogger(JSApiHelper.class);
//    wx.config({
//        debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
//                appId: '', // 必填，企业号的唯一标识，此处填写企业号corpid
//                timestamp: , // 必填，生成签名的时间戳
//        nonceStr: '', // 必填，生成签名的随机串
//                signature: '',// 必填，签名，见附录1
//                jsApiList: [] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
//    });

    /**
     * url
     * jsapi_ticket
     * nonceStr
     * timestamp
     * signature
     *
     * @param request
     * @param url
     * @param nonce_str
     * @param timestamp
     * @return
     */
    public static String getConfig(HttpServletRequest request, String url,String nonce_str,String timestamp){
        SessionUser sessionUser = (SessionUser) request.getSession().getAttribute(WxBaseAction.wxSessionUserKey);
        String jsapi_ticket = null;
        if(sessionUser != null){
            jsapi_ticket =  sessionUser.getJsticket();
        }else {
            return new JSONObject().toJSONString();
        }
        if(StringUtils.isBlank(nonce_str)){
            nonce_str = create_nonce_str();
        }
        if(StringUtils.isBlank(timestamp)){
            timestamp = create_timestamp();
        }
        Map<String, String> ret = sign(jsapi_ticket, url,nonce_str,timestamp);
        ret.put("corpId",sessionUser.getThirdcorpid());
        String config = JSON.toJSONString(ret);
//        LOG.info(config);
        return config;
    }

    public static Map<String, String> sign(String jsapi_ticket, String url, String nonce_str,String timestamp) {
        Map<String, String> ret = new HashMap<String, String>();

        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket +
                "&noncestr=" + nonce_str +
                "&timestamp=" + timestamp +
                "&url=" + url;
//        LOG.info(string1);

        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);

        return ret;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
