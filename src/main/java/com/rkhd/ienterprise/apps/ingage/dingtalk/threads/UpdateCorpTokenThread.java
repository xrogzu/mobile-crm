package com.rkhd.ienterprise.apps.ingage.dingtalk.threads;

import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.auth.AuthHelper;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.service.ServiceHelper;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.services.DingService;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by hougx on 2016/1/15.
 */
public class UpdateCorpTokenThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateCorpTokenThread.class);

    private RelThirdTokenService relThirdTokenService;

    private CommonService commonService;

    private DingService dingService;

    public UpdateCorpTokenThread(RelThirdTokenService relThirdTokenService, CommonService commonService, DingService dingService) {
        this.relThirdTokenService = relThirdTokenService;

        this.commonService = commonService;
        this.dingService = dingService;
    }

    /**
     * 更新企业的token和jsticket
     */
    public void run(){
        try {
            List<Long> ids = relThirdTokenService.getRelThirdTokenIdList();
            LOG.info("同步tocket,查询到的Ids ={}", JSON.toJSONString(ids));
            for(int i=0;i<ids.size();i++){
                String corpId = "";
                try {
                    RelThirdToken relThirdToken = relThirdTokenService.get(ids.get(i));

                    if(! Env.SUITE_KEY.equals(relThirdToken.getSuiteKey())){
                        continue;
                    }
                    corpId = relThirdToken.getThirdCorpid();

                    System.out.println("corpId="+corpId);
                    long currentTime = System.currentTimeMillis();
                    System.out.println("currentTime:"+currentTime);
                    long createAt =  relThirdToken.getCreateAt().getTime();
                    System.out.println("createAt:"+createAt);
                    System.out.println("currentTime-createAt:"+(currentTime-createAt));
                   // System.out.println("(currentTime-createAt) < (1000*60*90)"+(currentTime-createAt) < (1000*60*90));
                    if((currentTime-createAt) < (1000*60*90)){
                        LOG.info("corpId[{}] not update",corpId);
                        continue;
                    }else {
                        LOG.info("corpId[{}]  update",corpId);
                    }

                    String access_token = ServiceHelper.getCorpToken(relThirdToken.getThirdCorpid(), relThirdToken.getPermanentCode(), dingService.getSuiteAccessToken());
                    String jsticket = AuthHelper.getJsapiTicket(access_token);
                    commonService.addOrUpdateRelThirdToken(relThirdToken.getThirdCorpid(), access_token, jsticket, relThirdToken.getPermanentCode(), Platform.dingding);
                }catch(Exception e){
                    LOG.info("updateCorptoken failed, corpId="+corpId+" ,Exception={}",e.toString());
                }
            }
            LOG.info("updateCorptoken execute end");
        }catch (Exception e){
            e.printStackTrace();
            LOG.info("更新accessToken失败,Exception={}",e.toString());
        }
    }
}
