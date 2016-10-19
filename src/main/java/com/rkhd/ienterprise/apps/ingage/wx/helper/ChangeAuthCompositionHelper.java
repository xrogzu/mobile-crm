package com.rkhd.ienterprise.apps.ingage.wx.helper;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SetSystemProp;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.WXApi;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.chat.entitys.ChatReceiver;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.chat.entitys.ChatText;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.chat.entitys.TextSingleChat;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.message.entitys.AbstractWxMessage;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.message.entitys.TextWxMessage;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxInfoType;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxRuntime;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdCorpXsyTenant;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;
import com.rkhd.ienterprise.thirdparty.service.RelThirdCorpXsyTenantService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import com.rkhd.platform.auth.model.Role;
import com.rkhd.platform.auth.model.UserRole;
import com.rkhd.platform.auth.service.RoleService;
import com.rkhd.platform.auth.service.UserRoleService;
import com.rkhd.platform.exception.PaasException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChangeAuthCompositionHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ChangeAuthCompositionHelper.class);

    private WXApi wxApi =  WXApi.getWxApi();

    @Autowired
    private RelThirdTokenService relThirdTokenService;

    @Autowired
    private CommonSynHelper commonSynHelper;

    @Autowired
    private RelThirdCorpXsyTenantService relThirdCorpXsyTenantService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;


    public JSONObject doSyncAuth(String thirdCorpid,String suiteId , WxInfoType wxInfoType) throws WXException, ServiceException, PaasException {
        JSONObject retJson = new JSONObject();
        boolean success = false;
        String  errmsg = "OK";
        RelThirdToken relThirdToken = relThirdTokenService.getRelThirdTokenByThirdSourceAndThirdCorpid(Platform.WEIXIN,thirdCorpid, WXAppConstant.APP_ID);
        if(relThirdToken != null){
            String permanent_code = relThirdToken.getPermanentCode();
            String suitaccess_token = WxRuntime.getSuite_access_token();
            if(StringUtils.isBlank(suitaccess_token)){
                //SetSystemProp.writeProperties(WxRuntime.suite_access_tokenKey, suite_access_token);
                suitaccess_token = SetSystemProp.getKeyValue(WxRuntime.suite_access_tokenKey);
            }
            JSONObject jSONObject = wxApi.get_auth_info(suitaccess_token,suiteId,thirdCorpid,permanent_code);


            if( jSONObject.containsKey("errcode") ){
                LOG.error("from weixin="+jSONObject.toJSONString());
                errmsg  = jSONObject.getString("errmsg");
            }else{
                JSONObject auth_info =  jSONObject.getJSONObject("auth_info");
                JSONObject agent_0 = auth_info.getJSONArray("agent").getJSONObject(0);
                int agentid = agent_0.getInteger("agentid");


                JSONObject privilege = agent_0.getJSONObject("privilege");

                JSONArray extra_users = privilege.getJSONArray("extra_user");
                JSONArray allow_users = privilege.getJSONArray("allow_user");
                JSONArray  extra_tags = privilege.getJSONArray("extra_tag");
                JSONArray allow_tags = privilege.getJSONArray("allow_tag");

                RelThirdCorpXsyTenant relThirdCorpXsyTenant = relThirdCorpXsyTenantService.getByThirdPlatformIdAndSource(thirdCorpid, Platform.WEIXIN, WXAppConstant.APP_ID);
                if(relThirdCorpXsyTenant != null){


                    Long tenantId = relThirdCorpXsyTenant.getXsyTenantid();
                    TenantParam tenantParam = new TenantParam(tenantId);
                    String corp_access_token = relThirdToken.getTokenValue();
//                    TextWxMessage msg = new TextWxMessage();
//                    msg.setAgentid(agentid);
//                    msg.setSafe(0);
//                    msg.setTouser("@all");
//                    JSONObject text = new JSONObject();
//                    text.put("content","发消息测试发消息测试发消息测试发消息测试发消息测试发消息测试发消息测试发消息测试发消息测试发消息测试");
//                    msg.setText(text);
//                    JSONObject msgResponseObject = wxApi.sendMsg(corp_access_token, JSON.toJSONString(msg));
//                    LOG.info(msgResponseObject.toJSONString());

//                    TextSingleChat chat = new TextSingleChat();
//                    ChatReceiver receiver = new ChatReceiver();
//                    receiver.setId("xubaoyong_test");
//                    receiver.setType("single");
//                    chat.setReceiver(receiver);
//                    chat.setSender("liuzhiqiang");
//
//                    ChatText chatText = new ChatText();
//                    chatText.setContent("发消息测试发消息测试发消息测试发消息测试发消息测试发消息测试发消息测试发消息测试发消息测试发消息测试");
//                    chat.setText(chatText);
//                    String chatString = JSON.toJSONString(chat);
//                     LOG.info(chatString);
//                 JSONObject chatResult =  wxApi.chatSend(corp_access_token,chatString);
//
//                    LOG.info(chatResult.toJSONString());
//                    receiver.setId("houguangxin");
//                    chat.setReceiver(receiver);
//
//                    chatString = JSON.toJSONString(chat);
//                    LOG.info(chatString);
//                       chatResult =  wxApi.chatSend(corp_access_token,JSON.toJSONString(chat));
//                    LOG.info(chatResult.toJSONString());

                    Role adminRole =  roleService.getDefaultAdmin(tenantParam);
                    List<UserRole> userRoles = userRoleService.getListByRoleId(adminRole.getId(),tenantParam);
                    Map<String,Boolean> adminUserMap = new HashMap<String,Boolean>();
                    Long xsy_ManagerUserId = 0L;
                    if(userRoles != null ){
                        for(int i=0 ;i<userRoles.size();i++){
                            adminUserMap.put("u_"+userRoles.get(i).getUserId(),true);
                            if(i == userRoles.size()-1){
                                //保留最后一个系统管理员
                                xsy_ManagerUserId = userRoles.get(i).getUserId();
                            }
                        }
                    }
                    //开始同步其他非管理员数据
                    commonSynHelper.doSync(xsy_ManagerUserId,tenantParam,thirdCorpid,corp_access_token,extra_users,allow_users,extra_tags,allow_tags,  wxInfoType);

                    success = true;
                }else {
                    errmsg = "no get relThirdCorpXsyTenant";
                }
            }
        }else{
            errmsg = "no get relThirdToken";
        }

        retJson.put("success",success);
        retJson.put("errmsg",errmsg);
        return retJson;
    }
}
