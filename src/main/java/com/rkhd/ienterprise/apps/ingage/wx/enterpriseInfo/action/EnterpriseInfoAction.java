package com.rkhd.ienterprise.apps.ingage.wx.enterpriseInfo.action;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.MyEnterpriseInfoService;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.SimpleDateFormat;
import java.util.*;

@Namespace("/wx/enterpriseinfo")
public class EnterpriseInfoAction  extends WxBaseAction {

    private  static Logger LOG = LoggerFactory.getLogger(EnterpriseInfoAction.class);

    private String enterpriseName;

    @Autowired
    @Qualifier("mwebMyEnterpriseInfoService")
    private MyEnterpriseInfoService myEnterpriseInfoService;

    @Action(value = "dolist", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String dolist()  {
        EntityReturnData ret = new EntityReturnData();
        try{
            TenantParam tenantParam = getSessionUser().getXsy_TenantParam();
            ret =  myEnterpriseInfoService.search(getAccessToken(),  tenantParam,getEnterpriseName(),0);
            if(!ret.isSuccess()){
                LOG.error("myEnterpriseInfoService.search error; token="+getAccessToken()+";result = "+ JSON.toJSONString(ret));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",ret);



        return  SUCCESS;
    }
    @Action(value = "get", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String get()  {
        EntityReturnData ret = new EntityReturnData();
        try{
            ret =  myEnterpriseInfoService.get(getEnterpriseName());
            if(!ret.isSuccess()){
                LOG.error("myEnterpriseInfoService.get error; enterpriseName="+getEnterpriseName()+";result = "+ JSON.toJSONString(ret));
            }else{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                JSONObject detailEnterprise = (JSONObject) ret.getEntity();
                if(detailEnterprise.get("busiStartDate")!=null && detailEnterprise.getLongValue("busiStartDate") != 0){
                    Date busiStartDate = new Date(detailEnterprise.getLongValue("busiStartDate"));
                    String busiStartDate_string = sdf.format(busiStartDate);
                    detailEnterprise.put("busiStartDate",busiStartDate_string);
                }

                if(detailEnterprise.get("establishDate")!=null && detailEnterprise.getLongValue("establishDate") != 0){
                    Date establishDate = new Date(detailEnterprise.getLongValue("establishDate"));
                    String establishDate_string = sdf.format(establishDate);
                    detailEnterprise.put("establishDate",establishDate_string);
                }

                if(detailEnterprise.get("checkDate")!=null && detailEnterprise.getLongValue("checkDate") != 0){
                    Date checkDate = new Date(detailEnterprise.getLongValue("checkDate"));
                    String checkDate_string = sdf.format(checkDate);
                    detailEnterprise.put("checkDate",checkDate_string);
                }
                if(detailEnterprise.get("changeRecords") != null ){
                    JSONArray changeRecords = detailEnterprise.getJSONArray("changeRecords");
                    if( changeRecords != null && changeRecords.size() >0){
                        int length = changeRecords.size();
                        for( int i = 0;i<length;i++){
                            JSONObject item = changeRecords.getJSONObject(i);
                            Long changeDate = item.getLong("changeDate");
                            if( changeDate != null && changeDate.longValue() >0){
                                Date checkDate = new Date(changeDate);
                                String changeDateString = sdf.format(checkDate);
                                item.put("changeDate",changeDateString);
                                changeRecords.set(i,item);
                            }
                        }
                    }
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }
}
