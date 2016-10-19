package com.rkhd.ienterprise.apps.ingage.dingtalk.enums;

/**
 * Created by dell on 2016/1/27.
 */
public enum ActivityRecordType {
    DO_DING(1,"钉一下"),TEL(2,"打电话"),SIGN_IN(3,"拜访签到"),
    RECORD(-11,"活动记录"),TASK(-10,"做任务"),SMS(-12,"发短信");

    private ActivityRecordType (int order,String name){
        this.name = name;
        this.order = order;
    }
    private String name;

    private int order;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        System.out.println(ActivityRecordType.DO_DING.name());
    }
}
