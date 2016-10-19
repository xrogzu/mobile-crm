package com.rkhd.ienterprise.apps.ingage.dingtalk.enums;


import com.alibaba.fastjson.JSONObject;

/**
 * Created by dell on 2016/2/17.
 */
public enum OpportunityCloseDateTypes {
    NONE(0,"不限"),THIS_WEEK(1,"本周"),NEXT_WEEK(2,"下周")
    ,THIS_MONTH(3,"本月"),NEXT_MONTH(4,"下月")
    ,THIS_QUARTER(5,"本季度"),NEXT_QUARTER(6,"下季度")
    ,THIS_YEAR(7,"本年"),NEXT_YEAR(8,"下年");

    private long  value;

    private String desc;
    private OpportunityCloseDateTypes(long  value,String desc){
        this.value = value;
        this.desc = desc;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static void main(String[] args) {
        OpportunityCloseDateTypes[] ss =  OpportunityCloseDateTypes.values();
        for(OpportunityCloseDateTypes s: ss){
            System.out.println(s.getValue()+":"+s.getDesc());
            System.out.println(JSONObject.toJSONString(s));
        }
    }
}
