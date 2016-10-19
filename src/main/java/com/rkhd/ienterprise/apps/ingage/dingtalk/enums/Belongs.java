package com.rkhd.ienterprise.apps.ingage.dingtalk.enums;

/**
 * Created by dell on 2016/1/25.
 */
public enum Belongs {

    ACCOUNT(1,"客户","Account","Account"),

    CONTACT(2,"联系人","Contact","Contact"),

    OPPORTURNITY(3,"商机","Opporturnity","Opporturnity");

    private int belongId;

    private String belongName;

    private String name;

    private String  alias;

    private  Belongs(int belongId,String belongName,String name,String alias){
        this.belongId = belongId;
        this.belongName = belongName;
        this.name = name;
        this.alias = alias;
    }

    public int getBelongId() {
        return belongId;
    }

    public void setBelongId(int belongId) {
        this.belongId = belongId;
    }

    public String getBelongName() {
        return belongName;
    }

    public void setBelongName(String belongName) {
        this.belongName = belongName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
