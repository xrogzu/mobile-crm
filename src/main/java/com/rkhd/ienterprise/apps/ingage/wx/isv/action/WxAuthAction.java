package com.rkhd.ienterprise.apps.ingage.wx.isv.action;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import com.rkhd.ienterprise.apps.ingage.enums.SynErrorMsg;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.helper.AuthCompositionHelper;
import com.rkhd.ienterprise.apps.ingage.wx.helper.EventHelper;
import com.rkhd.ienterprise.apps.ingage.wx.helper.WxAuthHelper;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.WXApi;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.utils.aes.AesException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.utils.aes.WXBizMsgCrypt;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxInfoType;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxRuntime;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxSdkUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;

@Namespace("/wx/auth")
public class WxAuthAction extends WxBaseAction {
    private  static Logger LOG = LoggerFactory.getLogger(WxAuthAction.class);
    private WXApi wxApi =  WXApi.getWxApi();

    @Autowired
    private AuthCompositionHelper authCompositionHelper;
    @Autowired
    private WxAuthHelper wxauthHelper;

    @Autowired
    private EventHelper eventHelper;

    /**
     * 引导授权页面
     * http://qydev.weixin.qq.com/wiki/index.php?title=%E6%88%90%E5%91%98%E7%99%BB%E5%BD%95%E6%8E%88%E6%9D%83
     *
     * 已经被webapp/wx/index.jsp替代
     * @return
     * @throws Exception
     */
    @Deprecated
    @Action(value = "index", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/auth/loginpage.jsp")})
    public String yinDaoShouQuan() throws Exception{
        return SUCCESS;
    }
    @Action(value = "doyinDao", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String doyinDao() {

        String serverName = getRequest().getServerName();

        String contextPath = getRequest().getContextPath();

        String     redirect_uri =  "https://" +serverName +contextPath+"/wx/auth/doauth.action";
        try {
            redirect_uri = URLEncoder.encode(redirect_uri, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String suit_id = WXAppConstant.APP_ID;
        String pre_auth_code = WxRuntime.getPre_auth_code();
        String state = "1";
        String aherf_provider = "https://qy.weixin.qq.com/cgi-bin/loginpage?suite_id="+suit_id+"&pre_auth_code="+pre_auth_code+"&redirect_uri="+redirect_uri+"&state="+state+"&usertype=all";
        EntityReturnData ret = new EntityReturnData();
        ret.setSuccess(true);
        ret.setEntity(aherf_provider);
        getRequest().setAttribute("jsondata",ret);
        return SUCCESS;
    }

    /**
     * 参考文档：
     * http://qydev.weixin.qq.com/wiki/index.php?title=%E5%9B%9E%E8%B0%83%E6%A8%A1%E5%BC%8F
     * @return
     * @throws Exception
     */
    @Action(value = "callback", results = {@Result(name = SUCCESS, location = "/WEB-INF/pages/common/infoPage.jsp")})
    public String callback()throws Exception{
        String method = getRequest().getMethod();
//        LOG.info("method="+method);
//        Enumeration<String> enumeration = getRequest().getParameterNames();
//        while(enumeration.hasMoreElements()){
//            String em = enumeration.nextElement();
//            LOG.info("{}={}",em,getRequest().getParameter(em));
//        }
        String nonce = getRequest().getParameter("nonce");
        String msg_signature = getRequest().getParameter("msg_signature");
        String timestamp = getRequest().getParameter("timestamp");
        String sVerifyEchoStr = getRequest().getParameter("echostr");
        if("GET".equals(method)){
            try {
                WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(WXAppConstant.TOKEN, WXAppConstant.EncodingAESKey, WXAppConstant.CorpID);
                String  sEchoStr = wxcpt.verifyUrl(msg_signature, timestamp,
                        nonce, sVerifyEchoStr);
                getRequest().setAttribute("returnStr",sEchoStr);
            } catch (AesException e) {
                LOG.info("WXAppConstant.TOKEN="+WXAppConstant.TOKEN);
                LOG.info("WXAppConstant.EncodingAESKey="+WXAppConstant.EncodingAESKey);
                LOG.info("WXAppConstant.CorpID="+WXAppConstant.CorpID);
                e.printStackTrace();
            }
        }else if("POST".equals(method)){
            InputStream inputStream = getRequest().getInputStream();
            String content = IOUtils.toString(inputStream, "UTF-8");
            String corpid  = WXAppConstant.APP_ID;
            Element root = WxSdkUtil.paseRootElement(content);
            NodeList toUserNameList = root.getElementsByTagName("ToUserName");
            if(toUserNameList == null  || toUserNameList.getLength() <=0){
                corpid  = WXAppConstant.APP_ID;
            }else{
                corpid = toUserNameList.item(0).getTextContent();
            }
            content = wxApi.decryptContent( corpid,"aes",msg_signature,timestamp,nonce,content);
//            LOG.info("callback content="+content);

//下面的同步代码尽量不要开启，避免产生多线程执行同步工作的现象。如果多线程执行，会出现部门同步问题，异常出现等不确定问题。
//             eventHelper.doEventWork(inputStream, nonce, msg_signature, timestamp);
            getRequest().setAttribute("returnStr","");
        }else{
            getRequest().setAttribute("returnStr","");
        }
        return SUCCESS;
    }

    /**
     * 授权后的redirectURI
     * 参考：
     * http://qydev.weixin.qq.com/wiki/index.php?title=%E6%88%90%E5%91%98%E7%99%BB%E5%BD%95%E6%8E%88%E6%9D%83
     * http://qydev.weixin.qq.com/wiki/index.php?title=第三方应用接口说明
     * @return
     * @throws Exception
     */
    @Action(value = "doauth", results = {
            @Result(name = SUCCESS, location = PAGES_ROOT+"/auth/auth_success.jsp"),
            @Result(name = "fail", location = PAGES_ROOT+"/auth/auth_fail.jsp")
    })
    public String doauth(){
        String auth_code = getRequest().getParameter("auth_code");

        JSONObject json =  wxauthHelper.doauth(auth_code, WxInfoType.CREATE_AUTH);
        boolean success = json.getBoolean("success");
        getRequest().setAttribute("authJson",json.getJSONObject("authJson"));
        getRequest().setAttribute("success",success);
        getRequest().setAttribute("error",json.get("error"));
        if(success){
            return SUCCESS;
        }else{
            return "fail";
        }


    }

}
