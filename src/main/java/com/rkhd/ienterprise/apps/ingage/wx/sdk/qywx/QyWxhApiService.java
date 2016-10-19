package com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx;


import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXHttpDispatch;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.isv.provider.GetProviderTokenRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.isv.tag.TagMemberRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.request.*;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.response.GetPreAuthCodeResponse;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.response.GetSuiteTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QyWxhApiService {
    private  static Logger LOG = LoggerFactory.getLogger(QyWxhApiService.class);
    /**
     *获取应用套件令牌
     * @param suite_id      应用套件id
     * @param suite_secret  应用套件secret
     * @param suite_ticket  微信后台推送的ticket
     * @return
     * @throws WXException
     */
    public String get_suite_token(String suite_id,String suite_secret,String suite_ticket) throws WXException {
        GetSuiteTokenRequest request = new GetSuiteTokenRequest( suite_id, suite_secret, suite_ticket);
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        String suite_access_token = null;
        if (response.containsKey("suite_access_token")){
            suite_access_token = response.getString("suite_access_token");
        }else {
            LOG.error("get_suite_token response is "+(response==null?"null":response.toJSONString()));
        }
        return suite_access_token;
    }

    /**
     * 获取预授权码
     * @param suite_access_token
     * @param suite_id
     * @return
     * @throws WXException
     */
    public String get_pre_auth_code(String suite_access_token,String suite_id) throws WXException {
        GetPreAuthCodeRequest request = new GetPreAuthCodeRequest( suite_access_token,suite_id );
        GetPreAuthCodeResponse response = (GetPreAuthCodeResponse) WXHttpDispatch.execute(request);
        return response.getPreAuthCode();
    }

    /**
     * 获取企业号的永久授权码
     * 参考网址：http://qydev.weixin.qq.com/wiki/index.php?title=第三方应用接口说明
     * @param suite_access_token
     * @param suite_id
     * @param auth_code
     * @return
     * @throws WXException
     */
    public JSONObject get_permanent_code(String suite_access_token  ,String suite_id  , String auth_code) throws WXException {
        GetPermanentCodeRequest request = new GetPermanentCodeRequest( suite_access_token,suite_id,auth_code );
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        return response;
    }

    /**
     * 获取企业号access_token
     * @param suite_access_token
     * @param suite_id
     * @param auth_corpid
     * @param permanent_code
     * @return
     * @throws WXException
     */
    public JSONObject  	get_corp_token(String suite_access_token,String suite_id,String auth_corpid,String permanent_code) throws WXException {
        GetCorpTokenRequest request = new GetCorpTokenRequest( suite_access_token,suite_id,auth_corpid,permanent_code );
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        return response;
    }

    /**
     * 获取企业授权信息
     * @param suite_access_token
     * @param suite_id
     * @param auth_corpid
     * @param permanent_code
     * @return
     * @throws WXException
     */
    public JSONObject  	get_auth_info(String suite_access_token  ,String suite_id  , String auth_corpid,String permanent_code) throws WXException {
        GetAuthInfoRequest request = new GetAuthInfoRequest( suite_access_token,suite_id,auth_corpid,permanent_code );
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        return response;
    }

    public JSONObject  getJSTicket(String access_token) throws WXException {
        JSTicketRequest request = new JSTicketRequest( access_token );
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        return response;
    }

    public JSONObject getProviderToken(String corpid  ,String provider_secret) throws WXException {
        GetProviderTokenRequest request = new GetProviderTokenRequest( corpid  , provider_secret);
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        return response;
    }

    public JSONObject getTagMember(String access_token  ,String tagid) throws WXException {
        TagMemberRequest request = new TagMemberRequest( access_token  , tagid);
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        return response;
    }

    public JSONObject syncGetPage(String access_token,int seq,int offset) throws WXException {
        SyncGetPageRequest request = new SyncGetPageRequest( access_token  , seq,offset);
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        return response;
    }

}
