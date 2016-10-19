package com.rkhd.ienterprise.apps.ingage.dingtalk.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.enums.ActivityRecordType;
import com.rkhd.ienterprise.apps.ingage.services.ActivityRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by dell on 2016/2/14.
 */
public class ActivityRecordTypeTransUtil {
    private  static Logger LOG = LoggerFactory.getLogger(ActivityRecordTypeTransUtil.class);

    public static Long trans(String authorization, HttpServletRequest request, long activityTypeId){
        ActivityRecordType[] types = ActivityRecordType.values();
        ActivityRecordType type = null;
        for(int i=0;i<types.length;i++){
            if(types[i].getOrder() == activityTypeId){
                type =  types[i];
                break;
            }
        }
        if(type == null){
            LOG.info("参数错误，活动记录类型的参数不在提供范围内，传过来的参数为：{}",activityTypeId);
        }
        Long  result = (Long) request.getSession().getAttribute(type.name());
        if(result == null ){
            ActivityRecordService service = new ActivityRecordService();
            EntityReturnData entityReturnData =  service.getDesc(authorization);
            if(entityReturnData.isSuccess()){
                JSONObject descJSONObject = (JSONObject) entityReturnData.getEntity();
                JSONArray busiparamsArray = descJSONObject.getJSONArray("busiparams");
                JSONObject paramsJSONObject = null;
                boolean isBreak = false;
                for(int i=0;i<busiparamsArray.size();i++){
                    paramsJSONObject = busiparamsArray.getJSONObject(i);
                    LOG.info("{}", JSON.toJSONString(paramsJSONObject));
                    if(paramsJSONObject.getString("fieldname").equals("type")){
                        JSONArray params = paramsJSONObject.getJSONArray("params");
                        for(int j=0;j<params.size();j++){
                            if(params.getJSONObject(j).getString("type").equals(type.name())){
                                result = params.getJSONObject(j).getLong("id");
                                request.getSession().setAttribute(type.name(),result);
                                isBreak = true;
                                break;
                            }
                        }
                        if(isBreak){
                            break;
                        }

                    }
                }
            }else {
                LOG.error("查询活动记录类型失败，authorization={}，返回记录为：{}",authorization,JSONObject.toJSONString(entityReturnData));
            }
        }
        return  result;
    }

    public static void main(String[] args) {
        //activityTypeId
        System.out.println(ActivityRecordType.DO_DING.name());
        ActivityRecordType[] types = ActivityRecordType.values();
        for(int i=0;i<types.length;i++){
            System.out.println(types[i].getOrder());
        }
    }
}
