package com.rkhd.ienterprise.apps.ingage.dingtalk.dto;

import java.io.Serializable;

/**
 * 活动记录共享位置信息
 */
public class ActivityRecordPositionDto   implements Serializable {

    private static final long serialVersionUID = 4834631895930023656L;

    private  double longitude;//经度

    private double  latitude;//纬度

    private String location;//位置信息

    private String locationDetail;//位置信息详细

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationDetail() {
        return locationDetail;
    }

    public void setLocationDetail(String locationDetail) {
        this.locationDetail = locationDetail;
    }
}
