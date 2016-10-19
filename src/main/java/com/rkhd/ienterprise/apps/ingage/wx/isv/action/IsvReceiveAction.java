package com.rkhd.ienterprise.apps.ingage.wx.isv.action;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SetSystemProp;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.helper.AuthCompositionHelper;
import com.rkhd.ienterprise.apps.ingage.wx.helper.ChangeAuthCompositionHelper;
import com.rkhd.ienterprise.apps.ingage.wx.helper.EventHelper;
import com.rkhd.ienterprise.apps.ingage.wx.helper.WxAuthHelper;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.WXApi;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.utils.aes.AesException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.utils.aes.WXBizMsgCrypt;
import com.rkhd.ienterprise.apps.ingage.wx.threads.ChangeAuthThread;
import com.rkhd.ienterprise.apps.ingage.wx.threads.UpdateWxAccessTokenThread;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxRuntime;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxSdkUtil;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import com.rkhd.ienterprise.thirdparty.service.SynResultService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

/**
 * /wx/isv/t.action
 */
@Namespace("/wx/isv")
public class IsvReceiveAction extends WxBaseAction {
    private  static Logger LOG = LoggerFactory.getLogger(IsvReceiveAction.class);

    private WXApi wxApi =  WXApi.getWxApi();
    @Autowired
    private ChangeAuthCompositionHelper changeAuthCompositionHelper;


    @Autowired
    private AuthCompositionHelper authCompositionHelper;
    @Autowired
    private RelThirdTokenService relThirdTokenService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private SynResultService synResultService;
    @Autowired
    private WxAuthHelper wxauthHelper;
    @Autowired
    private EventHelper eventHelper;

    /**
     *
     * 应用回调函数
     * 参考：https://github.com/liuhouer/wxqyh/blob/master/src/com/bruce/wechat/servlet/CoreServlet.java
     * @return
     * @throws Exception
     */
    @Action(value = "t", results = {@Result(name = SUCCESS, location = "/WEB-INF/pages/common/infoPage.jsp")})
    public String isvEvent() throws Exception{

        String method = getRequest().getMethod();
        LOG.info("###############method:"+method);
        if("post".equalsIgnoreCase(method)){
            return doPost();
        }else{
            return doGet();
        }

    }

    protected String doGet() throws ServletException, IOException, OApiException,ServiceException {

        LOG.info("doGet" + ":let's rock!");
        String nonce = getRequest().getParameter("nonce");
        String msg_signature = getRequest().getParameter("msg_signature");
        String timestamp = getRequest().getParameter("timestamp");
        String sVerifyEchoStr = getRequest().getParameter("echostr");
        try {
            LOG.info("WXAppConstant.TOKEN="+WXAppConstant.TOKEN);
            LOG.info("WXAppConstant.EncodingAESKey="+WXAppConstant.EncodingAESKey);
            LOG.info("WXAppConstant.CorpID="+WXAppConstant.CorpID);
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(WXAppConstant.TOKEN, WXAppConstant.EncodingAESKey, WXAppConstant.CorpID);
            String  sEchoStr = wxcpt.verifyUrl(msg_signature, timestamp,
                    nonce, sVerifyEchoStr);
            getRequest().setAttribute("returnStr",sEchoStr);
        } catch (AesException e) {
            e.printStackTrace();
        }
        return SUCCESS;

    }

    protected String doPost() throws ServletException, IOException, OApiException,ServiceException {
        // TODO Auto-generated method stub
        LOG.info("doPost" + ":let's rock!");

        String nonce = getRequest().getParameter("nonce");
        String msg_signature = getRequest().getParameter("msg_signature");
        String timestamp = getRequest().getParameter("timestamp");
        return executeAction(nonce,msg_signature,timestamp);
    }

    /**
     * 事件说明文档：http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AC%AC%E4%B8%89%E6%96%B9%E5%9B%9E%E8%B0%83%E5%8D%8F%E8%AE%AE
     * @param nonce
     * @param msg_signature
     * @param timestamp
     * @return
     * @throws IOException
     * @throws OApiException
     * @throws ServiceException
     */
    public String executeAction( String nonce,String msg_signature,String timestamp) throws IOException, OApiException,ServiceException {
        InputStream inputStream = getRequest().getInputStream();
        JSONObject result = eventHelper.doEventWork(inputStream,nonce, msg_signature, timestamp);
        boolean success = result.getBoolean("success");
        String msg =  result.getString("msg");
        if(success){
            getRequest().setAttribute("returnStr","success");
        }else{
            if(StringUtils.isNotBlank(msg)){
                getRequest().setAttribute("returnStr",msg);
            }else{
                getRequest().setAttribute("returnStr","系统异常,请重试");
            }
        }

        return SUCCESS;
    }



}
