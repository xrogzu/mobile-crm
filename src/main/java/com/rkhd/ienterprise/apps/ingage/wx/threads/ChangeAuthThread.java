package com.rkhd.ienterprise.apps.ingage.wx.threads;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.wx.helper.ChangeAuthCompositionHelper;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxInfoType;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.SynResult;
import com.rkhd.ienterprise.thirdparty.service.SynResultService;
import com.rkhd.platform.exception.PaasException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChangeAuthThread extends Thread {

    private  static Logger LOG = LoggerFactory.getLogger(ChangeAuthThread.class);
    public String thirdCorpid;
    private String suiteId;
    private WxInfoType wxInfoType = null;

    private ChangeAuthCompositionHelper changeAuthCompositionHelper;

    private SynResultService synResultService;

    public ChangeAuthThread(  ChangeAuthCompositionHelper changeAuthCompositionHelper,String thirdCorpid,String suiteId, SynResultService synResultService,WxInfoType wxInfoType){
        this.suiteId =suiteId;
        this.thirdCorpid= thirdCorpid;
        this.changeAuthCompositionHelper = changeAuthCompositionHelper;
        this.synResultService = synResultService;
        this.wxInfoType = wxInfoType;

    }

    //同步通讯录
    public void run() {
        try {
            SynResult synResult =  synResultService.getByThirdCorpid(thirdCorpid, WXAppConstant.APP_ID, Platform.WEIXIN);
            if(synResult == null  ){
                LOG.info("thirdCorpid["+thirdCorpid+"]`s   synResult  not found");
            }else if( "false".equals( synResult.getSynResult())){
                LOG.info("thirdCorpid["+thirdCorpid+"]`s   synResult  is error;  "+ JSON.toJSONString(synResult));
            }else{
                JSONObject jsonResult =  changeAuthCompositionHelper.doSyncAuth(thirdCorpid,suiteId,wxInfoType);
                LOG.info("chang auth jsonResult = "+jsonResult.toJSONString());
            }



        } catch (WXException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (PaasException e) {
            e.printStackTrace();
        }
    }
}
