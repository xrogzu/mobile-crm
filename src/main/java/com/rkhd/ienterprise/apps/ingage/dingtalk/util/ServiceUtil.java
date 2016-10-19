package com.rkhd.ienterprise.apps.ingage.dingtalk.util;

import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.services.DingService;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by hougx on 2016/1/15.
 * 此类主要是为了方便静态工具类想操作DB，
 * 操作DB的service方法在此类中再封装一下。
 */
@Component
public class ServiceUtil {
    private static final ServiceUtil INSTANCE = new ServiceUtil();

    @Autowired
    private RelThirdTokenService relThirdTokenService;

    @Autowired
    private DingService dDingService;

    public static ServiceUtil getInstance() {
        return INSTANCE;
    }

    public RelThirdToken getRelThirdTokenByCorpId(String corpId) throws Exception{
       return relThirdTokenService.getRelThirdTokenByThirdSourceAndThirdCorpid(Platform.dingding,corpId, Env.SUITE_KEY);
    }

    public String  getSuiteAccessToken() throws Exception{
        return dDingService.getSuiteAccessToken();

    }

    public String getSuiteTicket() throws  Exception{
        return dDingService.getSuiteTicket();
    }
}
