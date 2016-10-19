package com.rkhd.ienterprise.apps.ingage.wx.sdk.security;


import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXHttpDispatch;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.security.request.WXServerIplistRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.security.response.WXServerIpListResponse;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.token.WXTokenController;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.utils.WXSignatureUtil;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.utils.aes.AesException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.utils.aes.WXBizMsgCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class WXSecurityService {
    private static final Logger LOG = LoggerFactory.getLogger(WXSecurityService.class);
    public boolean checkSignature(String signature, String timestamp, String nonce) {
        return WXSignatureUtil.checkSignature(signature, timestamp, nonce, WXAppConstant.TOKEN);
    }

    public String decryptContent(String corpid , String encryptType, String msgSignature, String timestamp, String nonce, String content) throws AesException {
        WXBizMsgCrypt msgCrypt = null;
        if ("aes".equals(encryptType)) {
            try {
                msgCrypt = new WXBizMsgCrypt(WXAppConstant.TOKEN, WXAppConstant.EncodingAESKey,corpid);
                content = msgCrypt.decryptMsg(msgSignature, timestamp, nonce, content);

            } catch (AesException e) {
                LOG.error("WXAppConstant.TOKEN="+WXAppConstant.TOKEN);
                LOG.error("WXAppConstant.EncodingAESKey="+WXAppConstant.EncodingAESKey);
                LOG.error("corpid="+corpid);
                LOG.error("msgSignature="+msgSignature);
                LOG.error("timestamp="+timestamp);
                LOG.error("nonce="+nonce);
                LOG.info("content="+content);
                e.printStackTrace();
                throw e;
            }
        }
        return content;
    }

    public String encryptContent(String encryptType, String timestamp, String nonce, String content) throws AesException {
        WXBizMsgCrypt msgCrypt = null;
        //加密
        if ("aes".equals(encryptType)) {
            try {
                msgCrypt = new WXBizMsgCrypt(WXAppConstant.TOKEN, WXAppConstant.EncodingAESKey, WXAppConstant.APP_ID);
                content = msgCrypt.encryptMsg(content, timestamp, nonce);
            } catch (AesException e) {
                e.printStackTrace();
                throw e;
            }
        }

        return content;
    }

    public List<String> getCallbackIpList() throws WXException {
        WXServerIplistRequest request = new WXServerIplistRequest(WXTokenController.getToken());
        WXServerIpListResponse response = (WXServerIpListResponse) WXHttpDispatch.execute(request);
        return response.getIpList();
    }
}
