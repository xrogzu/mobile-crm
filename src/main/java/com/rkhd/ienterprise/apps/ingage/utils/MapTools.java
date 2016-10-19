package com.rkhd.ienterprise.apps.ingage.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class MapTools {

    public static List<Map<String, Object>> parseJSON2List(String jsonStr){

        JSONArray jsonArr = JSONArray.parseArray(jsonStr);
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        if(jsonArr != null ){
            for(int i=0;i<jsonArr.size();i++){
                JSONObject json2 = jsonArr.getJSONObject(i);
                list.add(parseJSON2Map(json2.toString()));
            }
        }

        return list;
    }


    public static Map<String, Object> parseJSON2Map(String jsonStr){
        Map<String, Object> map = new HashMap<String, Object>();
        //最外层解析
        JSONObject json = JSONObject.parseObject(jsonStr);
        for(Object k : json.keySet()){
            Object v = json.get(k);
            //如果内层还是数组的话，继续解析
            if(v instanceof JSONArray){
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

                JSONArray jsonArr =  (JSONArray)v;
                if(jsonArr != null ){
                    for(int i=0;i<jsonArr.size();i++){
                        JSONObject json2 = jsonArr.getJSONObject(i);
                        list.add(parseJSON2Map(json2.toString()));
                    }
                }
                map.put(k.toString(), list);
            } else {
                map.put(k.toString(), v);
            }
        }
        return map;
    }
    public static Map<String, Object> parseJSON2Map(JSONObject json){
        Map<String, Object> map = new HashMap<String, Object>();

        for(Object k : json.keySet()){
            Object v = json.get(k);
            //如果内层还是数组的话，继续解析
            if(v instanceof JSONArray){
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
                List<Object > baseList = new ArrayList<Object>();
                JSONArray jsonArr =  (JSONArray)v;
                String tag = "JSONObject";
                if(jsonArr != null ){

                    for(int i=0;i<jsonArr.size();i++){
                        Object item = jsonArr.get(i);
                        if(item instanceof  JSONObject){
                            JSONObject json2 = jsonArr.getJSONObject(i);
                            list.add(parseJSON2Map(json2.toString()));
                        }else{
                            tag = "base";
                            baseList.add(item);
                        }

                    }
                }
                if( tag.equals("JSONObject")){
                    map.put(k.toString(), list);
                }else{
                    map.put(k.toString(), baseList);
                }


            } else {
                map.put(k.toString(), v);
            }
        }
        return map;
    }


    public static List<Map<String, Object>> getListByUrl(String url){
        try {
            //通过HTTP获取JSON数据
            InputStream in = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
            return parseJSON2List(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Map<String, Object> getMapByUrl(String url){
        try {
            //通过HTTP获取JSON数据
            InputStream in = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
            return parseJSON2Map(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //test
    public static void main(String[] args) {
        String url = "http://...";
        List<Map<String,Object>> list = getListByUrl(url);
        System.out.println(list);
    }
}