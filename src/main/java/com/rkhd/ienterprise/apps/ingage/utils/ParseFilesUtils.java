package com.rkhd.ienterprise.apps.ingage.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.enums.ObjectType;
import com.rkhd.ienterprise.apps.ingage.enums.SceneEnum;
import com.sleepycat.bind.tuple.SortedPackedLongBinding;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class ParseFilesUtils {

    public static JSONObject parseDefaultLayout(JSONObject defaultLayout ,String scene,ObjectType objectType ){
        JSONArray newComponents = new JSONArray();
        defaultLayout.remove("error_code");
        defaultLayout.remove("message");

        JSONArray components = defaultLayout.getJSONArray("components");

        for(int i=0;i<components.size();i++){
            JSONObject item = components.getJSONObject(i);

            if(scene.equals(SceneEnum.ADD.toString())){
                if(item.getBoolean("createable")){
                    if(objectType == ObjectType.ACCOUNT){
                        String filter = ",lockStatus,parentAccountId,createdBy,updatedBy,applicantId,";
                        if(filter.indexOf(","+item.getString("propertyName")+",")>=0){
                            continue;
                        }
                    }else if(objectType == ObjectType.CONTACT){
                        String filter = ",lockStatus,recentActivityRecordTime,createdAt,createdBy,updatedAt,updatedBy,";
                        if(filter.indexOf(","+item.getString("propertyName")+",")>=0){
                            continue;
                        }
                    }else if(objectType == ObjectType.OPPORTUNITY){
                        String filter = ",lockStatus,recentActivityRecordTime,createdAt,createdBy,updatedAt,updatedBy,applicantId,";
                        if(filter.indexOf(","+item.getString("propertyName")+",")>=0){
                            continue;
                        }
                        String propertyName = item.getString("propertyName");
                        if("ownerId".equals(propertyName)){
                            item.put("label","商机所有人");
                        }
                    }
                    newComponents.add(item);
                }

            }else if(scene.equals(SceneEnum.UPDATE.toString())){
                if(item.getBoolean("updateable")  ){
                    if(objectType == ObjectType.ACCOUNT){
                        String filter = ",lockStatus,parentAccountId,createdBy,updatedBy,applicantId,";
                        if(filter.indexOf(","+item.getString("propertyName")+",")>=0){
                            continue;
                        }
                    }else if(objectType == ObjectType.CONTACT){
                        String filter = ",lockStatus,accountId,recentActivityRecordTime,createdAt,createdBy,updatedAt,updatedBy,";
                        if(filter.indexOf(","+item.getString("propertyName")+",")>=0){
                            continue;
                        }
                    }else if(objectType == ObjectType.OPPORTUNITY){
                        String filter = ",lockStatus,recentActivityRecordTime,createdAt,createdBy,updatedAt,updatedBy,applicantId,";
                        if(filter.indexOf(","+item.getString("propertyName")+",")>=0){
                            continue;
                        }
                        String propertyName = item.getString("propertyName");
                        if("ownerId".equals(propertyName)){
                            item.put("label","商机所有人");
                        }
                    }
                    newComponents.add(item);
                }
            }else if(scene.equals(SceneEnum.DETAIL.toString())){
                if(objectType == ObjectType.ACCOUNT){
                    String propertyName = item.getString("propertyName");
                    String filter = ",lockStatus,applicantId,";
                    if(filter.indexOf(","+propertyName+",")>=0){
                        continue;
                    }
                }else  if(objectType == ObjectType.CONTACT){
                    String propertyName = item.getString("propertyName");
                    String filter = ",recentActivityRecordTime,lockStatus,";
                    if(filter.indexOf(","+propertyName+",")>=0){
                        continue;
                    }
                     if("ownerId".equals(propertyName)){
                        item.put("label","所有人");
                    }
                }else   if(objectType == ObjectType.OPPORTUNITY){
                    String propertyName = item.getString("propertyName");
                    String filter = ",applicantId,lockStatus,";
                    if(filter.indexOf(","+propertyName+",")>=0){
                        continue;
                    }
                    if("ownerId".equals(propertyName)){
                        item.put("label","所有人");
                    }
                }
                newComponents.add(item);
            }

            //JsonArraySort(newComponents,"itemOrder");
            defaultLayout.put("components",newComponents);
        }
        return  defaultLayout;
    }

    public static void JsonArraySort(JSONArray jsonArray,String sortKey){
        for(int j=1,jl=jsonArray.size();j < jl;j++){
            JSONObject  temp = jsonArray.getJSONObject(j); //第二个对象
             int       orderValue = temp.getIntValue(sortKey);
             int       i = j-1;
            while(i >=0 && jsonArray.getJSONObject(i).getIntValue(sortKey)>orderValue){ //用第二个和第一个比较
                jsonArray.set(i+1,jsonArray.getJSONObject(i));
                i = i-1;
            }
            jsonArray.set(i+1,temp);

        }
       // return jsonArray;

    }

    /**
     * 美化数据，由于api 版本问题，我们这里使用时需要做兼容方案
     * 1：联系人的创建日期，出生日期，最后修改日期需要格式化为年月日形式
     */
    public static void dataBeatutiful(JSONObject dbData, ObjectType objectType){
        if(objectType == ObjectType.ACCOUNT){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//小写的mm表示的是分钟
            SimpleDateFormat last_sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
            if(dbData.containsKey("recentActivityRecordTime")){
                if(StringUtils.isNotBlank(dbData.getString("recentActivityRecordTime"))){
                    Date recentActivityRecordTime = new Date(new Long(dbData.getString("recentActivityRecordTime")));
                    String format2min = sdf.format(recentActivityRecordTime);
                    dbData.put("recentActivityRecordTime",format2min);
                }
            }

            String dateString = null;
            if(dbData.containsKey("createdAt")){
                dateString = dateFormat(dbData.getString("createdAt"),sdf,last_sdf);
                dbData.put("createdAt",dateString);
            }
            if(dbData.containsKey("updatedAt")){
                dateString = dateFormat(dbData.getString("updatedAt"),sdf,last_sdf);
                dbData.put("updatedAt",dateString);
            }
        }else   if(objectType == ObjectType.CONTACT){
            String dateString = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//小写的mm表示的是分钟
            SimpleDateFormat last_sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
            if(dbData.containsKey("birthday")){
                dateString = dateFormat(dbData.getString("birthday"),sdf,last_sdf);
                dbData.put("birthday",dateString);
            }
            if(dbData.containsKey("createdAt")){
                dateString = dateFormat(dbData.getString("createdAt"),sdf,last_sdf);
                dbData.put("createdAt",dateString);
            }
            if(dbData.containsKey("updatedAt")){
                dateString = dateFormat(dbData.getString("updatedAt"),sdf,last_sdf);
                dbData.put("updatedAt",dateString);
            }
        }else if(objectType == ObjectType.OPPORTUNITY){
            String dateString = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//小写的mm表示的是分钟
            SimpleDateFormat last_sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
            if(dbData.containsKey("createdAt")){
                dateString = dateFormat(dbData.getString("createdAt"),sdf,last_sdf);
                dbData.put("createdAt",dateString);
            }
            if(dbData.containsKey("updatedAt")){
                dateString = dateFormat(dbData.getString("updatedAt"),sdf,last_sdf);
                dbData.put("updatedAt",dateString);
            }
            if(dbData.containsKey("closeDate")){
                dateString = dateFormat(dbData.getString("closeDate"),sdf,last_sdf);
                dbData.put("closeDate",dateString);
            }
            if(dbData.containsKey("recentActivityRecordTime")){
                if(StringUtils.isNotBlank(dbData.getString("recentActivityRecordTime"))){
                    Date recentActivityRecordTime = new Date(new Long(dbData.getString("recentActivityRecordTime")));
                    String format2min = sdf.format(recentActivityRecordTime);
                    dbData.put("recentActivityRecordTime",format2min);
                }
            }
        }else   if(objectType == ObjectType.SCHEDULE){
            String dateString = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//小写的mm表示的是分钟
            SimpleDateFormat last_sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
            if(dbData.containsKey("startDate") && dbData.get("startDate") != null){
                dateString =  sdf.format(new Date(dbData.getLong("startDate")));
                dbData.put("startDate",dateString);
            }
            if(dbData.containsKey("endDate") && dbData.get("endDate") != null){
                dateString =  sdf.format(new Date(dbData.getLong("endDate")));
                dbData.put("endDate",dateString);
            }

            if(dbData.containsKey("recurStopValue") && dbData.get("recurStopValue") != null && dbData.getLong("recurStopValue").longValue() != 0){
                dateString =  sdf.format(new Date(dbData.getLong("recurStopValue")));
                dbData.put("recurStopValue",dateString);
            }


            if(dbData.containsKey("createdAt")){
                dateString =  sdf.format(new Date(dbData.getLong("createdAt")));
                dbData.put("createdAt",dateString);
            }
            if(dbData.containsKey("updatedAt")){
                dateString =  sdf.format(new Date(dbData.getLong("updatedAt")));
                dbData.put("updatedAt",dateString);
            }
        }

    }
    private static String dateFormat(String  dateString, SimpleDateFormat sdf,SimpleDateFormat last_sdf){
        if(StringUtils.isNotBlank(dateString)){
            try {
                dateString = last_sdf.format(sdf.parse(dateString));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return dateString;
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//小写的mm表示的是分钟
       String  dateString =  sdf.format(new Date(1470096000000L));
        System.out.println(dateString);
    }
}
