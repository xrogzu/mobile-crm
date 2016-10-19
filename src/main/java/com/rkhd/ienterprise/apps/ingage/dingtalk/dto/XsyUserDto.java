package com.rkhd.ienterprise.apps.ingage.dingtalk.dto;

import java.io.Serializable;

/**
 * 销售易用户
 */
public class XsyUserDto implements Serializable {

    private long id;

    private String email;//邮箱

    private String phone;//手机号

    private String name;//姓名

    private String icon;//头像

    private String status;//用户状态

    private long statusInt;//用户状态编码  0:激活，1：已激活，-10：已删除，-11：已离职

    private String joinAtStr;//入职时间

    private String birthday;//出生日期

    private  String  employeeCode;//员工编号

    private String positionName;//职位

    private long userManagerId;//主管ID

    private long gender;//性别 1:男 2：女

    private  long  departId;//部门ID

    private String departmentName;//部门名称

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStatusInt() {
        return statusInt;
    }

    public void setStatusInt(long statusInt) {
        this.statusInt = statusInt;
    }

    public String getJoinAtStr() {
        return joinAtStr;
    }

    public void setJoinAtStr(String joinAtStr) {
        this.joinAtStr = joinAtStr;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public long getUserManagerId() {
        return userManagerId;
    }

    public void setUserManagerId(long userManagerId) {
        this.userManagerId = userManagerId;
    }

    public long getGender() {
        return gender;
    }

    public void setGender(long gender) {
        this.gender = gender;
    }

    public long getDepartId() {
        return departId;
    }

    public void setDepartId(long departId) {
        this.departId = departId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
