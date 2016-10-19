package com.rkhd.ienterprise.apps.ingage.dingtalk.enums;

/**
 * Created by dell on 2016/3/1.
 */
public enum TimeEnum {
//    NONE(0,"不限"),
    THIS_WEEK(1,"本周"),
    LAST_WEEK(2,"上周")
    ,LAST_MONTH(3,"上月")
    ,THIS_MONTH(4,"本月")
    ,NEXT_MONTH(5,"下月")
    ,LAST_QUARTER(6,"上季")
    ,THIS_QUARTER(7,"本季")
    ,NEXT_QUARTER(8,"下季")
    ,LAST_YEAR(9,"上年")
    ,THIS_YEAR(10,"本年")
    ,NEXT_YEAR(11,"下年")
    ,TODAY(12,"本日")
    ,YESTERDAY(13,"昨日");
    private long  value;

    private String desc;
    private TimeEnum(long  value,String desc){
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
}
