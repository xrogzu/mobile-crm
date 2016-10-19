package com.rkhd.ienterprise.apps.ingage.services;

import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DSuiteAccessToken;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.service.ServiceHelper;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SetSystemProp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by dell on 2015/12/25.
 */
@Component
public class DingService {

    private static final Logger LOG = LoggerFactory.getLogger(DingService.class);

    /**
     * 定时通信以后，保存到数据库中
     * @param suiteTicket
     */
    public void setSuiteTicket(String suiteTicket) throws OApiException {
        Env.suiteTicket = suiteTicket;
        //undo 存储到数据库
        SetSystemProp.writeProperties("suiteTicket", suiteTicket);
        //更新套件suite_access_token
        //updatePersistSuite_access_token();
    }

    /**
     * 获取suiteToken
     * @return
     */
    public String getSuiteTicket(){
        //undo 从数据库或缓存中查询suiteToken
        //return  SetSystemProp.getKeyValue("suiteTicket");
        return Env.suiteTicket == null? SetSystemProp.getKeyValue("suiteTicket"):Env.suiteTicket;
    }

    /**
     * 修改持久层的Suite_access_token
     */
    public void updatePersistSuite_access_token() throws OApiException {
        //先从钉钉服务器，获取套件访问Token（suite_access_token）
        String suiteTicket = getSuiteTicket();
        LOG.info("suiteTicket={}",suiteTicket);
        DSuiteAccessToken dSuiteAccessToken = ServiceHelper.getSuiteToken(Env.SUITE_KEY, Env.SUITE_SECRET, suiteTicket);
        //undo 存储到数据库

        //undo 修改缓存文件，暂时保存到配置文件中
        LOG.info("过期时间为：{}",dSuiteAccessToken.getExpires_in());
        // TODO: 2015/12/24 需要把数据保存到数据库中，此处暂时保存到文件中,
        Long currentMillis = System.currentTimeMillis();
        SetSystemProp.writeProperties("suite_access_token", dSuiteAccessToken.getSuite_access_token());
//        SetSystemProp.writeProperties("suiteToken_expires_in", dSuiteAccessToken.getExpires_in()+"");

        Env.suite_access_token = dSuiteAccessToken.getSuite_access_token();


        LOG.info("suite_access_token写入={}",dSuiteAccessToken.getSuite_access_token());
        LOG.info("suiteToken_expires_in={}",dSuiteAccessToken.getExpires_in());
        LOG.info("currentMillis={}",currentMillis);

    }

    public String  getSuiteAccessToken() throws OApiException {
        //先从缓存中拿
        String suite_access_token =   Env.suite_access_token;//SetSystemProp.getKeyValue("suite_access_token");
        if(suite_access_token == null){
            LOG.debug("Env.suite_access_token == null,从缓存中拿suite_access_token");
        }
        //undo 如果过期，则重新获取 调用updatePersistSuite_access_token()方法，否则直接返回结果
        return suite_access_token == null?SetSystemProp.getKeyValue("suite_access_token"):suite_access_token;
    }
}
