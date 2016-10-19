package com.rkhd.ienterprise.apps.ingage.dingtalk.dto;

import java.io.Serializable;

/**
 * Created by dell on 2016/1/7.
 */
public class Department implements Serializable {

    private long id;
    private long parentDepartId;//上级部门
    private long departType;//1:市场，2：销售，3：服务；4：其他
    private String departName;
    private String departCode;//部门编码

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentDepartId() {
        return parentDepartId;
    }

    public void setParentDepartId(long parentDepartId) {
        this.parentDepartId = parentDepartId;
    }

    public long getDepartType() {
        return departType;
    }

    public void setDepartType(long departType) {
        this.departType = departType;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public String getDepartCode() {
        return departCode;
    }

    public void setDepartCode(String departCode) {
        this.departCode = departCode;
    }
}
