package com.rkhd.ienterprise.apps.ingage.wx.sdk;


import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXHttpDispatch;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.message.entitys.AbstractWxMessage;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.request.GetAuthInfoRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.user.request.DepartmentUserInfoListRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.user.request.DepartmentUserListRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.user.request.UserInfoRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.utils.aes.AesException;


public class WXApi {
    private WXServiceFactory factory;
    private static WXApi wxApi;

    public static   WXApi getWxApi() {
        if (wxApi == null) {
            synchronized(WXApi.class){
                if(wxApi == null){
                    wxApi = new WXApi(WXAppConstant.APP_ID, WXAppConstant.APP_SECRET, WXAppConstant.TOKEN, WXAppConstant.EncodingAESKey);

                }
            }

        }
        return wxApi;
    }
    /**
     * 微信api接口
     *
     * @param appId          app id
     * @param appSecret      app secret
     * @param appToken       app token
     * @param encodingAESKey encoding aes key
     * @param domainName     domain name
     */
    public WXApi(String appId, String appSecret, String appToken, String encodingAESKey, String domainName) {
        WXAppConstant.init(appId, appSecret, appToken, encodingAESKey, domainName);
        factory = new WXServiceFactory();
    }

    /**
     * 构造函数
     *
     * @param appId          app id
     * @param appSecret      app secret
     * @param appToken       app token
     * @param encodingAESKey encoding aes key
     */
    public WXApi(String appId, String appSecret, String appToken, String encodingAESKey) {
        WXAppConstant.init(appId, appSecret, appToken, encodingAESKey);
        factory = new WXServiceFactory();
    }
    /**
     * 通过检验signature对请求进行校验
     *
     * @param signature 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return 若返回true，请原样返回echostr参数内容，则接入生效，成为开发者成功，否则接入失败。
     */
    public boolean checkSignature(String signature, String timestamp, String nonce) {
        return factory.getWxSecurityService().checkSignature(signature, timestamp, nonce);
    }
    /**
     * 解密消息
     *
     * @param encryptType  加密类型，encrypt_type为aes时，表示aes加密（暂时只有raw和aes两种值)。
     * @param msgSignature 表示对消息体的签名
     * @param timestamp    时间戳
     * @param nonce        随机数
     * @param content      需要解密的消息
     * @return 解密后的消息
     * @throws AesException 异常信息
     */
    public String decryptContent(String corpid ,String encryptType, String msgSignature, String timestamp, String nonce, String content) throws AesException {
        return factory.getWxSecurityService().decryptContent( corpid ,encryptType, msgSignature, timestamp, nonce, content);
    }
    /**
     * 加密消息
     *
     * @param encryptType 加密类型，encrypt_type为aes时，表示aes加密（暂时只有raw和aes两种值)。
     * @param timestamp   时间戳
     * @param nonce       随机数
     * @param content     需要加密的消息
     * @return 加密后的消息
     * @throws AesException 异常信息
     */
    public String encryptContent(String encryptType, String timestamp, String nonce, String content) throws AesException {
        return factory.getWxSecurityService().encryptContent(encryptType, timestamp, nonce, content);
    }

    /**
     * 获取企业号登录用户信息
     * @param access_token
     * @param auth_code
     * @return
     * @throws WXException
     */
    public JSONObject getLoginInfo(String access_token  ,String auth_code) throws WXException {
        return factory.getGetLoginInfoService().getLoginInfo(access_token,auth_code);
    }

    /***
     * 获取应用套件令牌
    * @param suite_id
    * @param suite_secret
    * @param suite_ticket
    * @return
            * @throws WXException
    */
    public String get_suite_token(String suite_id,String suite_secret,String suite_ticket) throws WXException {
        return factory.getQyWxhApiService().get_suite_token( suite_id, suite_secret, suite_ticket);
    }

    /**
     * 获取应用套件令牌
     * @param suite_access_token
     * @param suite_id
     * @return
     * @throws WXException
     */
    public String get_pre_auth_code(String suite_access_token, String suite_id) throws WXException {
        return factory.getQyWxhApiService().get_pre_auth_code( suite_access_token, suite_id);
    }

    /**
     *
     * @param suite_access_token
     * @param suite_id
     * @param auth_code
     * @return
     * @throws WXException
     */
    public JSONObject  get_permanent_code(String suite_access_token  ,String suite_id  , String auth_code)throws WXException {
        return factory.getQyWxhApiService().get_permanent_code(suite_access_token,suite_id,auth_code);
    }

    /**
     *
     * @param suite_access_token
     * @param suite_id          应用套件id
     * @param auth_corpid       授权方corpid
     * @param permanent_code    永久授权码，通过get_permanent_code获取
     * @return
     * @throws WXException
     */
    public JSONObject  get_corp_token(String suite_access_token,String suite_id,String auth_corpid,String permanent_code) throws WXException {
        return factory.getQyWxhApiService().get_corp_token(suite_access_token,suite_id,auth_corpid,permanent_code);
    }

    public JSONObject getDepartmentList(String access_token  ,String departmentId  ) throws WXException {
        return factory.getDepartmentService().getDepartmentList(access_token,departmentId);
    }

    public JSONObject get_user(String access_token  ,String userid) throws WXException {

        return factory.getUserService().get_user(access_token  , userid);
    }

    public JSONObject getDepartment_user_simplelist(String access_token  ,String department_id ,int fetch_child,String status) throws WXException {
        return factory.getUserService().getDepartment_user_simplelist(access_token  , department_id ,fetch_child, status );
    }

    public JSONObject getDepartment_user_list(String access_token  ,String department_id ,int fetch_child,String status) throws WXException {
        return factory.getUserService().getDepartment_user_list(access_token  , department_id ,fetch_child, status );
    }

    public JSONObject getUserByCode(String access_token  ,String code) throws WXException {
        return factory.getUserService().getUserByCode(access_token,code);
    }

    /**
     * 获取企业号的授权信息
     * @param suite_access_token
     * @param suite_id
     * @param auth_corpid
     * @param permanent_code
     * @return
     * @throws WXException
     */
    public JSONObject  get_auth_info(String suite_access_token  ,String suite_id  , String auth_corpid,String permanent_code) throws WXException {

        return factory.getQyWxhApiService().get_auth_info(suite_access_token  , suite_id  ,  auth_corpid, permanent_code);
    }


    public String getJSTicket(String access_token) throws WXException {
        JSONObject  tickJSONObject = factory.getQyWxhApiService().getJSTicket(access_token); 
        String  jsTicket = null;
        if(tickJSONObject.containsKey("errcode") && tickJSONObject.getIntValue("errcode") == 0){
            jsTicket = tickJSONObject.getString("ticket");
        }
        return  jsTicket;

    }

    public String getProviderTokenRequest(String corpid  ,String provider_secret) throws WXException {

        JSONObject providerInfo = factory.getQyWxhApiService().getProviderToken(corpid,provider_secret);
        return providerInfo.getString("provider_access_token");
    }

    /**
     * 获取标签成员
     * 参考：http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%A0%87%E7%AD%BE
     * @param access_token
     * @param tagid
     * @return
     * @throws WXException
     */
    public JSONObject getTagMember(String access_token  ,String tagid) throws WXException {
        JSONObject tagMemberJsonObject = factory.getQyWxhApiService().getTagMember(access_token,tagid);
        return tagMemberJsonObject;
    }

    /**
     * 发送消息
     * 参考：http://qydev.weixin.qq.com/wiki/index.php?title=%E5%8F%91%E9%80%81%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E
     * @param access_token
     * @param subAbstractWxMessageJSONString
     * @return
     * @throws WXException
     */
    public JSONObject sendMsg(String access_token  , String subAbstractWxMessageJSONString) throws WXException {

        JSONObject msgResponseObject = factory.getWxMessageService().sendMsg(access_token,subAbstractWxMessageJSONString);
        return msgResponseObject;
    }

    /**
     * http://qydev.weixin.qq.com/wiki/index.php?title=%E4%BC%81%E4%B8%9A%E4%BC%9A%E8%AF%9D%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E
     * @param access_token
     * @param chatJSONString
     * @return
     * @throws WXException
     */
    public JSONObject chatSend(String access_token  , String chatJSONString) throws WXException {

        JSONObject msgResponseObject = factory.getWxChatService().chatSend(access_token,chatJSONString);
        return msgResponseObject;
    }

    public JSONObject syncGetPage(String access_token,int seq,int offset) throws WXException {
        return  factory.getQyWxhApiService().syncGetPage(access_token, seq, offset);
    }


}
