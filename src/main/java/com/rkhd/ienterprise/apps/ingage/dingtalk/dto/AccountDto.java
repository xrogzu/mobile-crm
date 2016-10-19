package com.rkhd.ienterprise.apps.ingage.dingtalk.dto;

import java.io.Serializable;

/**
 * Created by dell on 2015/12/22.
 */
public class AccountDto implements Serializable {

    private static final long serialVersionUID = 4519657072726921011L;


    private long id;

    private String region = "";//区

    private String  phone = "";//电话

    private String state = "";//省份

    private long parentAccountId;//上级客户

    private String city = "";//城市

    private int level;//客户级别. 1: A(重点客户),2: B(普通客户),3: C(非优先客户)

    private String zipCode = "";//邮编

    private String fax = "";//传真

    private long longitude;//经度

   // private String url;//公司网址

    private String accountName = "";//客户名称

    private String address = "";//地址

    private long latitude;//纬度

    private String   comment = "";//备注

    private String ownerId = "";//客户所有人

    private long industryId;//行业id

    //private String  weibo;//微博

    private long dimDepart ;//多维度 使用创建者所属部门的

    private long  dbcSelect1 ;//公司状态

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getDimDepart() {
        return dimDepart;
    }

    public void setDimDepart(long dimDepart) {
        this.dimDepart = dimDepart;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getParentAccountId() {
        return parentAccountId;
    }

    public void setParentAccountId(long parentAccountId) {
        this.parentAccountId = parentAccountId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getIndustryId() {
        return industryId;
    }

    public void setIndustryId(long industryId) {
        this.industryId = industryId;
    }


    public long getDbcSelect1() {
        return dbcSelect1;
    }

    public void setDbcSelect1(long dbcSelect1) {
        this.dbcSelect1 = dbcSelect1;
    }
}
