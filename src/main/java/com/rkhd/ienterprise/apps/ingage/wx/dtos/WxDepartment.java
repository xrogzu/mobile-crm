package com.rkhd.ienterprise.apps.ingage.wx.dtos;


import com.alibaba.fastjson.JSONArray;

import java.io.Serializable;


public class WxDepartment implements Serializable {
    private static final long serialVersionUID = -3635632801092959026L;

    private  int id;
    private int order ;
    private String name;
    private int parentid;

    private JSONArray childes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public JSONArray getChildes() {
        return childes;
    }

    public void setChildes(JSONArray childes) {
        this.childes = childes;
    }
}
