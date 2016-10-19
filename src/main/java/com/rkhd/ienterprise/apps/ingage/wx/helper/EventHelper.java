package com.rkhd.ienterprise.apps.ingage.wx.helper;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SetSystemProp;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.WXApi;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.utils.aes.AesException;
import com.rkhd.ienterprise.apps.ingage.wx.threads.AuthThread;
import com.rkhd.ienterprise.apps.ingage.wx.threads.ChangeAuthThread;
import com.rkhd.ienterprise.apps.ingage.wx.threads.UpdateWxAccessTokenThread;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxInfoType;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxRuntime;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxSdkUtil;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import com.rkhd.ienterprise.thirdparty.service.SynResultService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

@Component
public class EventHelper {
    private WXApi wxApi =  WXApi.getWxApi();
    private  static Logger LOG = LoggerFactory.getLogger(EventHelper.class);
    @Autowired
    private CommonService commonService;

    @Autowired
    private WxAuthHelper wxauthHelper;

    @Autowired
    private RelThirdTokenService relThirdTokenService;

    @Autowired
    private ChangeAuthCompositionHelper changeAuthCompositionHelper;
    @Autowired
    private SynResultService synResultService;



    public JSONObject doEventWork( InputStream inputStream,String nonce,String msg_signature,String timestamp){
        JSONObject result = new JSONObject();
        boolean success = true;
        String msg = null;

        try {

            // commons.io.jar 方法
            String content = IOUtils.toString(inputStream, "UTF-8");
//           LOG.info("content="+content);
            String corpid  = WXAppConstant.APP_ID;
            Element root = WxSdkUtil.paseRootElement(content);
            NodeList toUserNameList = root.getElementsByTagName("ToUserName");
            if(toUserNameList == null  || toUserNameList.getLength() <=0){
                  corpid  = WXAppConstant.APP_ID;
            }else{
                corpid = toUserNameList.item(0).getTextContent();
            }
            content = wxApi.decryptContent( corpid,"aes",msg_signature,timestamp,nonce,content);
          LOG.info("content="+content);
            root = WxSdkUtil.paseRootElement(content);
            NodeList infoTypeList = root.getElementsByTagName("InfoType");
            if(infoTypeList != null && infoTypeList.getLength()> 0 ){
                String infoType = infoTypeList.item(0).getTextContent();
                LOG.info("infoType="+infoType);
                if("suite_ticket".equals(infoType)){
//
                    NodeList suiteTicketList = root.getElementsByTagName("SuiteTicket");
                    String suiteTicket = suiteTicketList.item(0).getTextContent();
                    WxRuntime.setSuiteTicket( suiteTicket);

                    SetSystemProp.writeProperties(WxRuntime.suiteTicketKey, suiteTicket);
                    String suite_id = WXAppConstant.APP_ID;
                    String suite_secret = WXAppConstant.APP_SECRET;
                    String suite_ticket = suiteTicket;
                    try {
                        //获取应用套件令牌
                        String suite_access_token  = wxApi.get_suite_token(suite_id,suite_secret,suite_ticket);
                        if(StringUtils.isBlank(suite_access_token)){
                            LOG.error("not get suite_access_token,result is "+suite_access_token);
                        }else{
                            WxRuntime.setSuite_access_token(suite_access_token);
                            SetSystemProp.writeProperties(WxRuntime.suite_access_tokenKey, suite_access_token);
                            //获取预授权码
                            String pre_auth_code =  wxApi.get_pre_auth_code(suite_access_token,suite_id);
                            WxRuntime.setPre_auth_code( pre_auth_code);
                            SetSystemProp.writeProperties(WxRuntime.pre_auth_codeKey, pre_auth_code);

                            //更新token
                            new UpdateWxAccessTokenThread(commonService,relThirdTokenService).start();
                        }


                    } catch (WXException e) {
                        e.printStackTrace();
                    }
                }else if ("create_auth".equals(infoType)){
                    NodeList authCodelist = root.getElementsByTagName("AuthCode");
                    String authCode = authCodelist.item(0).getTextContent();

                    new AuthThread(wxauthHelper,authCode,WxInfoType.CREATE_AUTH).start();

                   //   result =  wxauthHelper.doauth(authCode,WxInfoType.CREATE_AUTH);
//                      success = result.getBoolean("success");
//                      msg = result.getString("error");
                    success = true;

                } else if ("change_auth".equals(infoType)){

                    NodeList authCorpIdlist = root.getElementsByTagName("AuthCorpId");
                    String authCorpId = authCorpIdlist.item(0).getTextContent();

                    NodeList SuiteIdlist = root.getElementsByTagName("SuiteId");
                    String suiteId = SuiteIdlist.item(0).getTextContent();


                   synFilter(authCorpId,suiteId,WxInfoType.CHANGE_AUTH);

                }else if ("cancel_auth".equals(infoType)){
                    LOG.info("cancel_auth="+infoType);
                }else if ("contact_sync".equals(infoType)){

                    NodeList authCorpIdlist = root.getElementsByTagName("AuthCorpId");
                    String authCorpId = authCorpIdlist.item(0).getTextContent();

                    NodeList SuiteIdlist = root.getElementsByTagName("SuiteId");
                    String suiteId = SuiteIdlist.item(0).getTextContent();

                    synFilter(authCorpId,suiteId,WxInfoType.CONTACT_SYNC);

                }
            }else {
//                LOG.info("decryptContent="+content);
//                <Event><![CDATA[subscribe]]></Event>
                        NodeList eventList = root.getElementsByTagName("Event");
                    if(eventList != null && eventList.getLength() > 0){
                        String event = eventList.item(0).getTextContent();
//                        LOG.info("event="+event);
                    }
            }

        } catch (AesException e) {
            e.printStackTrace();
            success = false;
        }  catch (SAXException e) {
            e.printStackTrace();
            success = false;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            success = false;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        result.put("success",success);
        result.put("msg",msg);
        return result;
    }

    public void synFilter(String authCorpId , String suiteId,WxInfoType wxInfoType){
        new ChangeAuthThread(changeAuthCompositionHelper,authCorpId,suiteId,synResultService,wxInfoType).start();

    }


}
