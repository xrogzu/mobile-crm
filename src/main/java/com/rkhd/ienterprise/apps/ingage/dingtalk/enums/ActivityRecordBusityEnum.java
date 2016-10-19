package com.rkhd.ienterprise.apps.ingage.dingtalk.enums;

/**
 * Created by dell on 2016/1/27.
 */
public enum ActivityRecordBusityEnum {

    WITH_POSITION(1,"带有位置信息"),
    OTHER(2,"其他");

    private int type;

    private String typeText;

    private ActivityRecordBusityEnum(int type,String typeText){
        this.type = type;
        this.typeText = typeText;
    }

    public String getTypeText() {
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
