package com.rkhd.ienterprise.apps.ingage.dingtalk.dto;

import java.io.Serializable;

/**
 * Created by dell on 2016/2/17.
 */
public class DateDto implements Serializable {
    private static final long serialVersionUID = 5795662353840546334L;

    private long downDate;

    private long upDate;

    public long getDownDate() {
        return downDate;
    }

    public void setDownDate(long downDate) {
        this.downDate = downDate;
    }

    public long getUpDate() {
        return upDate;
    }

    public void setUpDate(long upDate) {
        this.upDate = upDate;
    }
}
