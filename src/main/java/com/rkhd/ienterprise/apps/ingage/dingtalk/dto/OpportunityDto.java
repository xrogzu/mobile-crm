package com.rkhd.ienterprise.apps.ingage.dingtalk.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dell on 2016/1/11.
 */
public class OpportunityDto implements Serializable {

    private  long id;

    private long ownerId;

    private String opportunityName;

    private String accountId;//客户名称,所属公司名称

    private double opportunityType;//1:新客户机会,2:老客户机会

    private double money;//销售金额

    private String saleStageId;//89744:初步接洽,89745:需求确定,89746:方案/报价,89747:谈判审核,89748:赢单,89749:输单

    private int winRate;//赢率

    private String closeDate;//结单日期 yyyy-mm-dd

    private String  sourceId;//机会来源:    108172:客户介绍,108173:其它,108170：研讨会，108171：搜索引擎，108169：广告

    private List  product;//

    private String comment;//憋住

    private long dimDepart;//所属部门

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOpportunityName() {
        return opportunityName;
    }

    public void setOpportunityName(String opportunityName) {
        this.opportunityName = opportunityName;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(double opportunityType) {
        this.opportunityType = opportunityType;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getSaleStageId() {
        return saleStageId;
    }

    public void setSaleStageId(String saleStageId) {
        this.saleStageId = saleStageId;
    }

    public int getWinRate() {
        return winRate;
    }

    public void setWinRate(int winRate) {
        this.winRate = winRate;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public List getProduct() {
        return product;
    }

    public void setProduct(List product) {
        this.product = product;
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
}
