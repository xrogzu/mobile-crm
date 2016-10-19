package com.rkhd.ienterprise.apps.ingage.wx.threads;


import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.WXApi;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxRuntime;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import com.rkhd.ienterprise.util.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class UpdateWxAccessTokenThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateWxAccessTokenThread.class);

    private WXApi wxApi =  WXApi.getWxApi();

    private RelThirdTokenService relThirdTokenService;

    private CommonService commonService;

    public UpdateWxAccessTokenThread(CommonService commonService, RelThirdTokenService relThirdTokenService){

        this.commonService= commonService;
        this.relThirdTokenService = relThirdTokenService;

    }

    public void run(){
       try {
            Pagination<RelThirdToken> pager =  relThirdTokenService.getPaginationByThirdParams(Platform.WEIXIN,WXAppConstant.APP_ID,0, Integer.MAX_VALUE);
            List<RelThirdToken> pagerData =  pager.getCurrentPageDatas();
            if(pagerData != null){
                JSONObject corpToken = null;
                for(RelThirdToken item :pagerData){
                    long currentTime = System.currentTimeMillis();
                    long createAt =  item.getCreateAt().getTime();
                    if((currentTime-createAt) < (1000*60*90)){
                        LOG.info("corpId[{}] not update",item.getThirdCorpid());
                        continue;
                    }
                     corpToken = wxApi.get_corp_token(WxRuntime.getSuite_access_token(), WXAppConstant.APP_ID,item.getThirdCorpid(),item.getPermanentCode());
                    if(corpToken.containsKey("errcode")){
                        LOG.info("corpId[{}] not update",item.getThirdCorpid());
                        continue;
                    }else{
                        String access_token =  corpToken.getString("access_token");
                        LOG.info("corpId[{}]  update",item.getThirdCorpid());
                       // commonService.addOrUpdateRelThirdToken(item.getThirdCorpid(), access_token, "", item.getPermanentCode(),Platform.WEIXIN);
                        item.setCreateAt(new Date());
                        item.setTokenValue(access_token);
                        relThirdTokenService.update(item);
                    }

                }
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (WXException e) {
            e.printStackTrace();
        }
    }
}
