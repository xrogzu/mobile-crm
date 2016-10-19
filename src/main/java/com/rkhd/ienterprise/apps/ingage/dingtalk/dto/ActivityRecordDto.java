package com.rkhd.ienterprise.apps.ingage.dingtalk.dto;

import java.io.Serializable;

/**
 * Created by dell on 2016/1/25.
 */
public class ActivityRecordDto  implements Serializable {

    private static final long serialVersionUID = 870444068105908949L;

    public String content;//内容

    private long belongId;//来源业务对象1：客户，2：联系人，3：销售机会，6：市场活动，11：销售线索

    private long groupId;

    private long  activityTypeId;//活动记录类型

    private int source  = -1 ;//终端类型 0:PC 1:android 2:iphone

    private long objectId = 0;//来源对象数据的编号

    private ActivityRecordPositionDto  position;//

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getBelongId() {
        return belongId;
    }

    public void setBelongId(long belongId) {
        this.belongId = belongId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(long activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public ActivityRecordPositionDto getPosition() {
        return position;
    }

    public void setPosition(ActivityRecordPositionDto position) {
        this.position = position;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }
}
