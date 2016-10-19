package com.rkhd.ienterprise.apps.ingage.dingtalk.dto;

import java.io.Serializable;

/**
 * 统计活动记录时使用
 */
public class StatisticActivityRecord   implements Serializable {
    private static final long serialVersionUID = 8775992256306548362L;

    /**
     * 类型ID
     */
    private long  id;

    /**
     * 类型名称，比如邮件、打电话
     */
    private String typeName;

    /**
     * 次数
     */
    private long times;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }
}
