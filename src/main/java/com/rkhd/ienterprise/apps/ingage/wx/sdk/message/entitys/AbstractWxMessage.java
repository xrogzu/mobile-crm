package com.rkhd.ienterprise.apps.ingage.wx.sdk.message.entitys;


public abstract class AbstractWxMessage {
    /**
     * 非必填
     * 成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送
     */
    private  String  touser;
    /**
     * 非必填
     * 部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
     */
    private String toparty;

    /**
     * 非必填
     * 标签ID列表，多个接收者用‘|’分隔。当touser为@all时忽略本参数
     */
    private String totag;

    /**
     * 必填
     * 消息类型
     */
    private String msgtype;
    /**
     * 必填
     * 企业应用的id，整型。可在应用的设置页面查看
     */
    private long agentid;

    /**
     * 否
     * 表示是否是保密消息，0表示否，1表示是，默认0
     */
    private int safe;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getToparty() {
        return toparty;
    }

    public void setToparty(String toparty) {
        this.toparty = toparty;
    }

    public String getTotag() {
        return totag;
    }

    public void setTotag(String totag) {
        this.totag = totag;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public long getAgentid() {
        return agentid;
    }

    public void setAgentid(long agentid) {
        this.agentid = agentid;
    }

    public int getSafe() {
        return safe;
    }

    public void setSafe(int safe) {
        this.safe = safe;
    }
}
